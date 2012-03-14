module forstat;   % Definition of REDUCE FOR loops.

% Author: Anthony C. Hearn.

% Copyright (c) 1993 The RAND Corporation.  All rights reserved.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


fluid '(!*blockp !*fastfor !*modular);

global '(cursym!* foractions!*);

Comment the syntax of the FOR statement is as follows:

                 {step i3 until}
        {i := i1 {             } i2 }
        {        {      :      }    }
   for  {                           } <action> <expr>
        {        { in }             }
        { each i {    }  <list>     }
                 { on }

In all cases, the <expr> is evaluated algebraically within the scope of
the current value of i.  If <action> is DO, then nothing else happens.
In other cases, <action> is a binary operator that causes a result to be
built up and returned by FOR.  In each case, the loop is initialized to
a default value.  The test for the end condition is made before any
action is taken.

The effect of the definition here is to replace all for loops by
semantically equivalent blocks.  As a result, none of the mapping
functions are needed in REDUCE.

To declare a set of actions, one says;

foractions!* := '(do collect conc product sum);

remflag(foractions!*,'delim);    % For bootstrapping purposes.

% To associate a binary function with an action, one says:

deflist('((product times) (sum plus)),'bin);

% And to give these an initial value in a loop:

deflist('((product 1) (sum 0)),'initval);

% NB:  We need to reset for and let delims if an error occurs.  It's
% probably best to do this in the begin1 loop.

% flag('(for),'nochange);

symbolic procedure forstat;
   begin scalar !*blockp;
      return if scan() eq 'all then forallstat()
              else if cursym!* eq 'each then foreachstat()
              else forloop()
   end;

put('for,'stat,'forstat);

symbolic procedure forloop;
   begin scalar action,bool,incr,var,x;
      if flagp('step,'delim) then bool := t else flag('(step),'delim);
      x := errorset!*('(xread1 'for),t);
      if null bool then remflag('(step),'delim) else bool := nil;
      if errorp x then error1() else x := car x;
      if not eqcar(x,'setq) or not idp(var := cadr x)
        then symerr('for,t);
      x := caddr x;
      if cursym!* eq 'step
        then <<if flagp('until,'delim) then bool := t
                else flag('(until),'delim);
               incr := xread t;
               if null bool then remflag('(until),'delim)
                else bool := nil;
               if not(cursym!* eq 'until) then symerr('for,t)>>
       else if cursym!* eq '!*colon!* then incr := 1
       else symerr('for,t);
      if flagp(car foractions!*,'delim) then bool := t % nested loop
       else flag(foractions!*,'delim);
      incr := list(x,incr,xread t);
      if null bool then remflag(foractions!*,'delim);
      if not((action := cursym!*) memq foractions!*)
        then symerr('for,t);
      return list('for,var,incr,action,xread t)
   end;

symbolic procedure formfor(u,vars,mode);
   begin scalar action,algp,body,endval,incr,initval,var,x;
         scalar !*!*a2sfn;
        % ALGP is used to determine if the loop calculation must be
        % done algebraically or not.
      !*!*a2sfn := 'aeval!*;
      var := cadr u;
      incr := caddr u;
      incr := list(formc(car incr,vars,mode),
                   formc(cadr incr,vars,mode),
                   formc(caddr incr,vars,mode));
      if not atsoc(var,vars)
        then if intexprnp(car incr,vars) and intexprnp(cadr incr,vars)
               then vars := (var . 'integer) . vars
              else vars := (var . mode) . vars;
      action := cadddr u;
      body := formc(car cddddr u,vars,mode);
      initval := car incr;
      endval := caddr incr;
      incr := cadr incr;
      algp := algmodep initval or algmodep incr or algmodep endval;
      if algp then <<endval := unreval endval; incr := unreval incr>>;
      x := if algp then list('list,''difference,endval,var)
            else list(if !*fastfor then 'idifference else 'difference,
                      endval,var);
      if incr neq 1
        then x := if algp then list('list,''times,incr,x)
                   else list('times,incr,x);
      % We could consider simplifying X here (via reval).
      x := if algp then list('aminusp!:,x)
            else list(if !*fastfor then 'iminusp else 'minusp,x);
      return forformat(action,body,initval,x,
                       if algp
                          then list('aeval!*,list('list,''plus,incr))
                        else list(if !*fastfor then 'iplus2 else 'plus2,
                                  incr),
                       var,vars,mode)
   end;

put('for,'formfn,'formfor);

symbolic procedure algmodep u;
   not atom u and car u memq '(aeval aeval!*);

symbolic procedure aminusp!: u;
   % This is only used in loop tests. We must make sure we are not in a
   % modular domain (where the difference will always be positive!).
   begin scalar oldmode,v;
      if !*modular then oldmode := setdmode('modular,nil);
      v := errorset2 list('aminusp!:1,mkquote u);
      if oldmode then setdmode('modular,t);
      if errorp v then typerr(u,"arithmetic expression")
       else return car v
   end;

symbolic procedure aminusp!:1 u;
   begin scalar x;
      u := aeval!* u;
      x := u;
      if fixp x then return minusp x
       else if not eqcar(x,'!*sq)
        then msgpri(nil,reval u,"invalid in FOR statement",nil,t);
      x := cadr x;
      if fixp car x and fixp cdr x then return minusp car x
       else if not(cdr x = 1)
             or not (atom(x := car x) or atom car x)
         % Should be DOMAINP, but SMACROs not yet defined.
        then msgpri(nil,reval u,"invalid in FOR statement",nil,t)
       else return apply1('!:minusp,x)
   end;

symbolic procedure foreachstat;
   begin scalar w,x,y,z;
        if not idp(x := scan()) or not((y := scan()) memq '(in on))
          then symerr("FOR EACH",t)
         else if flagp(car foractions!*,'delim) then w := t
         else flag(foractions!*,'delim);
        z := xread t;
        if null w then remflag(foractions!*,'delim);
        w := cursym!*;
        if not(w memq foractions!*) then symerr("FOR EACH",t);
        return list('foreach,x,y,z,w,xread t)
   end;

put('foreach,'stat,'foreachstat);

symbolic procedure formforeach(u,vars,mode);
   begin scalar action,body,lst,mod1,var;
        var := cadr u; u := cddr u;
        mod1 := car u; u := cdr u;
        lst := formc(car u,vars,mode); u := cdr u;
        if not(mode eq 'symbolic) then lst := list('getrlist,lst);
        action := car u; u := cdr u;
        body := formc(car u,(var . mode) . vars,mode); % was FORMC
        if mod1 eq 'in
          then body := list(list('lambda,list var,body),list('car,var))
         else if not(mode eq 'symbolic) then typerr(mod1,'action);
        return forformat(action,body,lst,
                         list('null,var),list 'cdr,var,vars,mode)
   end;

put('foreach,'formfn,'formforeach);

symbolic procedure forformat(action,body,initval,
                             testexp,updform,var,vars,mode);
   begin scalar result;
       % Next test is to correct structure generated by formfor.
% The expansion here can introduce new local variables named
% forall!-result and forall!-endprt. Until recently the first of
% these had been created as a gensym to avoid any prospect of name
% clashing. However that results in code parsing onto different stuff
% each time it is read, and that causes some confusion in the CSL world
% where I want to match source code to compiled code. The messy use
% of a "sort of obscure" name should in practise be OK. Especially since
% I observe that "endptr" had been polluting the name-space for some years.
      if algmodep updform and length cadr updform > 2
         then <<result:='forall!-result; % gensym();
                updform:= list list('lambda,
                                    list result,
                                    list('aeval!*,
                                         caadr updform .
                                              cadadr updform .
                                              result .
                                              cddadr updform))>>;
      result := 'forall!-result; % gensym();
      return
         sublis(list('body2 .
                if mode eq 'symbolic or intexprnp(body,vars)
                  then list(get(action,'bin),body,result)
                 else list('aeval!*,list('list,mkquote get(action,'bin),
                            unreval body,result)),
               'body3 .
                   if mode eq 'symbolic then body
                      else list('getrlist,body),
               'body . body,
               'initval . initval,
               'nillist .
                   if mode eq 'symbolic then nil else '(makelist nil),
               'result . result,
               'initresult . get(action,'initval),
               'resultlist . if mode eq 'symbolic then result
                              else list('cons,''list,result),
               'testexp . testexp,
               'updfn . car updform,
               'updval . cdr updform,
               'var . var),
          if action eq 'do
            then '(prog (var)
                  (setq var initval)
              lab (cond (testexp (return nil)))
                  body
                  (setq var (updfn var . updval))
                  (go lab))
           else if action eq 'collect
            then '(prog (var result forall!-endptr)
                  (setq var initval)
                  (cond (testexp (return nillist)))
                  (setq result (setq forall!-endptr (cons body nil)))
                looplabel
                  (setq var (updfn var . updval))
                  (cond (testexp (return resultlist)))
                  (rplacd forall!-endptr (cons body nil))
                  (setq forall!-endptr (cdr forall!-endptr))
                  (go looplabel))
           else if action eq 'conc
            then '(prog (var result forall!-endptr)
                  (setq var initval)
               startover
                  (cond (testexp (return nillist)))
                  (setq result body)
                  (setq forall!-endptr (lastpair resultlist))
                  (setq var (updfn var . updval))
                  (cond ((atom forall!-endptr) (go startover)))
                looplabel
                  (cond (testexp (return result)))
                  (rplacd forall!-endptr body3)
                  (setq forall!-endptr (lastpair forall!-endptr))
                  (setq var (updfn var . updval))
                  (go looplabel))
           else '(prog (var result)
                 (setq var initval)
                 (setq result initresult)
              lab1
                 (cond (testexp (return result)))
                 (setq result body2)
                 (setq var (updfn var . updval))
                 (go lab1)))
   end;

symbolic procedure lastpair u;
   % Return the last pair of the list u.
   if atom u or atom cdr u then u else lastpair cdr u;

symbolic procedure unreval u;
   % Remove spurious aeval or reval in inner expression.
   if atom u or null(car u memq '(aeval reval)) then u else cadr u;

remprop('conc,'newnam);

put('join,'newnam,'conc);   % alternative for CONC

endmodule;

end;
