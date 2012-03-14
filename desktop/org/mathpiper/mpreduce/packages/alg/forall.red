module forall; % FOR ALL and LET-related commands.

% Author: Anthony C. Hearn.
% Modifications by:  Herbert Melenk.

% Copyright (c) 1993 RAND.  All rights reserved.

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


fluid '(!*resimp !*sub2 alglist!* arbl!* asymplis!* frasc!* wtl!*);

fluid '(!*!*noremove!*!* frlis!* newrule!* oldrules!* props!* subfg!*);

fluid '(!*reduce4 !*sqrtrulep powlis!* powlis1!*);

global '(!*match cursym!* erfg!* letl!* mcond!*);

letl!* := '(let match clear saveas such);   % Special delimiters.

% Contains two RPLAC references commented out.

remprop('forall,'stat);

remprop('forall,'formfn);

symbolic procedure forallstat;
   begin scalar arbl,conds;
        if cursym!* memq letl!* then symerr('forall,t);
        flag(letl!*,'delim);
        arbl := remcomma xread nil;
        if cursym!* eq 'such then
          <<if not(scan() eq 'that) then symerr('let,t);
            conds := xread nil>>;
        remflag(letl!*,'delim);
        if not(cursym!* memq letl!*) then symerr('let,t)
         else return list('forall,arbl,conds,xread1 t)
   end;

symbolic procedure forall u;
   begin scalar v,x,y;
      v := for each j in car u collect 
             if null j then rsverr j else j;
      x := for each j in v collect newvar j;
      y := pair(v,x);
      mcond!* := if cadr u eq 't then t else subla(y,cadr u);
%     mcond!* := formbool(subla(y,eval cadr u),nil,'algebraic);
      frasc!* := y;
      frlis!* := union(x,frlis!*);
      return lispeval caddr u
   end;

symbolic procedure arbstat;
   <<lpriw("*****","ARB no longer supported");
     symerr('if,t)>>;

put('arb,'stat,'arbstat);

symbolic procedure newvar u;
   if not idp u then typerr(u,"free variable")
%   else if flagp(u,'reserved)
%    then typerr(list("Reserved variable",u),"free variable")
    else intern compress append(explode '!=,explode u);

symbolic procedure formforall(u,vars,mode);
   begin scalar arbl!*,x,y;
      u := cdr u;
%     vars := append(car u,vars);   % Semantics are different.
      if null cadr u then x := t else x := formbool(cadr u,vars,mode);
%     if null cadr u then x := t else x := form1(cadr u,vars,mode);
      y := form1(caddr u,vars,mode);
      % Allow for a LET or MATCH call during a similar evaluation.
      % This might occur in autoloading.
      if eqcar(y,'let) then y := 'let00 . cdr y
       else if eqcar(y,'match) then y := 'match00 . cdr y;
      return list('forall,list('list,mkquote union(arbl!*,car u),
                  mkquote x,mkquote y))
   end;

symbolic procedure def u;
   % Defines a list of operators.
   <<lprim "Please do not use the DEF operator; it is no longer supported";
     for each x in u do
        if not eqexpr x or not idlistp cadr x then errpri2(x,t)
         else <<mkop caadr x;
                forall list(cdadr x,t,list('let,mkarg(list x,nil)))>>>>;

put('def,'stat,'rlis);

deflist('((forall formforall)),'formfn);

deflist('((forall forallstat)),'stat);

flag ('(clear let match),'quote);

symbolic procedure formlet1(u,vars,mode);
   requote ('list . for each x in u collect
      if eqexpr x
        then list('list,mkquote car x,form1(cadr x,vars,mode),
                                !*s2arg(form1(caddr x,vars,mode),vars))
       else form1(x,vars,mode));

symbolic procedure requote u;
   if atom u or not(car u eq 'list) then u
    else (if x then mkquote x else u) where x=requote1 cdr u;

symbolic procedure requote1 u;
   begin scalar x,y;
   a: if null u then return reversip x
       else if numberp car u or car u memq '(nil t)
        then x := car u . x
       else if atom car u then return nil
       else if caar u eq 'quote then x := cadar u . x
       else if caar u eq 'list and (y := requote1 cdar u)
        then x := y . x
       else return nil;
      u := cdr u;
      go to a
   end;

symbolic procedure !*s2arg(u,vars);
   %makes all NOCHANGE operators into their listed form;
   if atom u or eq(car u,'quote) then u
    else if not idp car u or not flagp(car u,'nochange)
     then for each j in u collect !*s2arg(j,vars)
    else mkarg(u,vars);

put('let,'formfn,'formlet);

put('clear,'formfn,'formclear);

put('match,'formfn,'formmatch);

symbolic procedure formclear(u,vars,mode);
   list('clear,formclear1(cdr u,vars,mode));

symbolic procedure formclear1(u,vars,mode);
   'list . for each x in u collect
      if flagp(x,'share) then mkquote x else form1(x,vars,mode);

symbolic procedure formlet(u,vars,mode);
   list('let,formlet1(cdr u,vars,mode));

symbolic procedure formmatch(u,vars,mode);
   list('match,formlet1(cdr u,vars,mode));

symbolic procedure let u; let0 u;    % to distinguish between operator
                                     % and function.

symbolic procedure let0 u;
   let00 u where frasc!* = nil;

symbolic procedure let00 u;
   begin
      u := errorset!*(list('let1,mkquote u),t);
      frasc!* := mcond!* := nil;
      if errorp u then error1() else return car u
   end;

symbolic procedure let1 u;
   begin scalar x,y;
      u := reverse u;  % So that rules are added in order given.
      while u do
         <<if idp u then typerr(u,"rule list")
            else if eqcar(y := listeval0(x := car u),'list)
             then rule!-list(reverse cdr y,t)
            else if idp x then revalruletst x
            else if car x eq 'replaceby
             then if frasc!*
                    then rerror(alg,100,
                                "=> invalid in FOR ALL statement")
                   else rule!-list(list x,t)
            else if car x eq 'equal
                    then if smemq('!~,x)
                           then if frasc!* then typerr(x,"rule")
                                 else rule!-list(list x,t)
                          else let2(cadr x,caddr x,nil,t)
            else revalruletst x;
           u := cdr u>>
   end;

symbolic procedure revalruletst u;
   (if u neq v then let1 list v else typerr(u,"rule list"))
   where v = reval u;

symbolic procedure let2(u,v,w,b);
   begin scalar flgg,x,y,z;
        % FLGG is set true if free variables are found.
        if (y := getrtype u) and (z := get(y,'typeletfn))
           and flagp(z,'direct)
          then return lispapply(z,list(u,v,y,b,getrtype v))
         else if (y := getrtype v) and (z := get(y,'typeletfn))
                 and flagp(z,'direct)
          then return lispapply(z,list(u,v,nil,b,y));
        x := subla(frasc!*,u);
        if x neq u
          then if atom x then return errpri1 u
                 else <<flgg := t; u := x>>;
        x := subla(frasc!*,v);
        if x neq v
          then <<v := x;
                 if eqcar(v,'!*sq!*) then v := prepsq!* cadr v>>;
                 % to ensure no kernels are replaced by uneq copies
                 % during pattern matching process.
        % Check for unmatched free variables.
        x := smemql(frlis!*,mcond!*);
        y := smemql(frlis!*,u);
        if (z := setdiff(x,y))
           or (z := setdiff(setdiff(smemql(frlis!*,v),x),
                    setdiff(y,x)))
          then <<lprie ("Unmatched free variable(s)" . z);
                 erfg!* := 'hold;
                 return nil>>
         else if atom u then nil
         else if car u eq 'getel then u := lispeval cadr u
         else if flagp(car u,'immediate) then u := reval u;
      return let3(u,v,w,b,flgg)
   end;

symbolic procedure let3(u,v,w,b,flgg);
   % U is left-hand-side of a rule, v the right-hand-side.
   % W is true if a match, NIL otherwise.
   % B is true if the rule is being added, NIL if being removed.
   % Flgg is true if there are free variables in the rule.
   begin scalar x,y1,y2,z;
        x := u;
        if null x then <<u := 0; return errpri1 u>>
         else if numberp x then return errpri1 u;
        % Allow redefinition of id's, regardless of type.
       % The next line allows type of LHS to be redefined.
       y2 := getrtype v;
       if b and idp x then <<remprop(x,'rtype); remprop(x,'avalue)>>;
%        else if idp x and flagp(x,'reserved)
%         then rederr list(x,"is a reserved identifier");
        if (y1 := getrtype x)
          then return if z := get(y1,'typeletfn)
                        then lispapply(z,list(x,v,y1,b,getrtype v))
                       else typelet(x,v,y1,b,getrtype v)
         else if y2 and not(y2 eq 'yetunknowntype)
          then return if z := get(y2,'typeletfn)
                        then lispapply(z,list(x,v,nil,b,y2))
                       else typelet(x,v,nil,b,y2)
         else letscalar(u,v,w,x,b,flgg)
   end;

symbolic procedure letscalar(u,v,w,x,b,flgg);
   begin
      if not atom x
               then if not idp car x then return errpri2(u,'hold)
                     else if car x eq 'df
                      then if null letdf(u,v,w,x,b) then nil
                            else return nil
                     else if getrtype car x
                      then return let2(reval x,v,w,b)
                     else if not get(car x,'simpfn)
                      then <<redmsg(car x,"operator");
                             mkop car x;
                             return let3(u,v,w,b,flgg)>>
                     else nil
         else if null b and null w
          then <<remprop(x,'avalue);
                 remprop(x,'rtype);    % just in case
                 remflag(list x,'antisymmetric);
                 remprop(x,'infix);
               % remprop(x,'klist);
               % commented out: the relevant objects may still exist.
                 remprop(x,'kvalue);
                 remflag(list x,'linear);
                 remflag(list x,'noncom);
                 remprop(x,'op);
                 remprop(x,'opmtch);
                 remprop(x,'simpfn);
                 remflag(list x,'symmetric);
                 wtl!* := delasc(x,wtl!*);
                 if flagp(x,'opfn)
                   then <<remflag(list x,'opfn); remd x>>;
                 rmsubs(); % since all kernel lists are gone.
                 return nil>>;
        if eqcar(x,'expt) and caddr x memq frlis!*
          then letexprn(u,v,w,!*k2q x,b,flgg)
           % Special case of a non-integer exponent match.
         else if eqcar(x,'sqrt)
          then <<!*sqrtrulep := t;
                 let2({'expt,cadr x,'(quotient 1 2)},v,w,b)>>;
           % Since SQRTs can be converted into EXPTs.
        x := simp0 x where !*precise = t;  % We don't want to break
                                           % up exponents.
        return if not domainp numr x then letexprn(u,v,w,x,b,flgg)
                else errpri1 u
   end;

symbolic procedure letexprn(u,v,w,x,b,flgg);
   % Replacement of scalar expressions.
   begin scalar y,z;
        if denr x neq 1
          then return let2(let!-prepf numr x,
                           list('times,let!-prepf denr x,v),w,b)
         else if red(x := numr x)
          then return let2(let!-prepf !*t2f lt x,
                           list('difference,v,let!-prepf red x),w,b)
         else if null (y := kernlp x)
          then <<y := term!-split x;
                 return let2(let!-prepf car y,
                            list('difference,v,let!-prepf cdr y),w,b)>>
         else if y neq 1
          then return let2(let!-prepf quotf!*(x,y),
                           list('quotient,v,let!-prepf y),w,b);
        x := klistt x;
        y := list(w . (if mcond!* then mcond!* else t),v,nil);
        if cdr x
          then return <<rmsubs(); !*match:= xadd!*(x . y,!*match,b)>>
         else if null w and cdar x=1    % ONEP
          then <<x := caar x;
                 if null flgg and (null mcond!* or mcond!* eq 't
                        or not smember(x,mcond!*))
                   then <<if atom x
                            then if flagp(x,'used!*) then rmsubs()
                                  else nil
                           else if 'used!* memq cddr fkern x
                              or car x eq 'df
                            then rmsubs();
                          setk1(x,v,b)>>
                  else if atom x then return errpri1 u
                  else <<rmsubs(); % if get(car x,'klist) then rmsubs();
                                   % the "get" is always true currently.
                         put(car x,
                             'opmtch,
                           xadd!*(cdr x . y,get(car x,'opmtch),b))>>>>
         else <<rmsubs();
                if v=0 and null w and not flgg
                  then <<asymplis!* := xadd(car x,asymplis!*,b);
                         powlis!*
                      := xadd(caar x . cdar x . y,powlis!*,'replace)>>
                 else if w or not(cdar y eq t) or frasc!*
                  then powlis1!* := xadd(car x . y,powlis1!*,b)
                 else if null b and (z := assoc(caar x,asymplis!*))
                    and z=car x
                  then asymplis!* := delasc(caar x,asymplis!*)
              else <<powlis!* := xadd(caar x . cdar x . y,powlis!*,b);
                   if b then asymplis!* := delasc(caar x,asymplis!*)>>>>
   end;

rlistat '(clear let match);


% Further support for rule lists and local rule applications.

symbolic procedure clearrules u;
   rule!-list(u,nil) where !*sqrtrulep=nil;

% symbolic procedure letrules u; rule!-list(u,t);

rlistat '(clearrules);   % letrules.

symbolic procedure rule!-list(u,type);
   % Type is true if the rule is being added, NIL if being removed.
   begin scalar v,x,y,z;
   a: frasc!* := nil;   % Since free variables must be declared in each
                        % rule.
      if null u or u = {{}} then return (mcond!* := nil);
      mcond!* := t;
      v := car u;
      if idp v
        then if (x := get(v,'avalue)) and car x eq 'list
               then <<u := append(reverse cdadr x,cdr u); go to a>>
              else typerr(v,"rule list")
       else if car v eq 'list
          then <<u := append(cdr v,cdr u); go to a>>
       else if car v eq 'equal
        then lprim "Please use => instead of = in rules"
       else if not(car v eq 'replaceby) then typerr(v,"rule");
      y := remove!-free!-vars cadr v;
      if eqcar(caddr v,'when)
        then <<mcond!* := formbool(remove!-free!-vars!* caddr caddr v,
                                   nil,'algebraic);
               z := remove!-free!-vars!* cadr caddr v>>
       else z := remove!-free!-vars!* caddr v;
      rule!*(y,z,frasc!*,mcond!*,type);
      u := cdr u;
      go to a
   end;

symbolic procedure rule!*(u,v,frasc,mcond,type);
   % Type is T if a rule is being added, OLD if an old rule is being
   % reinstalled, or NIL if a rule is being removed.
   begin scalar x;
      frasc!* := frasc;
      mcond!* := mcond eq t or subla(frasc,mcond);
      if type and type neq 'old
        then <<newrule!* := list(u,v,frasc,mcond);
%              prin2t list("newrule:",newrule!*);
               if idp u
                 then <<if x := get(u,'rtype)
                          then <<props!*:= (u . ('rtype . x)) . props!*;
                                 remprop(u,'rtype)>>;
                        if x := get(u,'avalue)
                          then <<updoldrules(x,nil);
                                 remprop(u,'avalue)>>>>;
               % Asymptotic case.
               if v=0 and eqcar(u,'expt) and idp cadr u
                  and numberp caddr u
                  and (x := assoc(cadr u,asymplis!*))
                then updoldrules(x,nil)>>;
      return rule(u,v,frasc,if type eq 'old then t else type)
   end;

symbolic procedure rule(u,v,frasc,type);
   begin scalar flg,frlis,x,y,z;
        % FLGG is set true if free variables are found.
        %
        x := subla(frasc,u);
        if x neq u
          then if atom x then return errpri1 u
                 else <<flg := t; u := x>>;
        x := subla(frasc,v);
        if x neq v
          then <<v := x;
                 if eqcar(v,'!*sq!*) then v := prepsq!* cadr v>>;
                 % to ensure no kernels are replaced by uneq copies
                 % during pattern matching process.
        % Check for unmatched free variables.
        frlis := for each j in frasc collect cdr j;
        x := smemql(frlis,mcond!*);
        y := smemql(frlis,u);
        if (z := setdiff(x,y))
           or (z := setdiff(setdiff(smemql(frlis,v),x),
                    setdiff(y,x)))
          then <<lprie ("Unmatched free variable(s)" . z);
                 erfg!* := 'hold;
                 return nil>>
         else if eqcar(u,'getel) then u := lispeval cadr u;
      return let3(u,v,nil,type,flg)
   end;

mkop '!~;               % Declare as algebraic operator.

put('!~,'prifn,'tildepri);

symbolic procedure tildepri u; <<prin2!* "~"; prin2!* cadr u>>;

newtok '((!= !>) replaceby);

infix =>;

precedence =>,to;

symbolic procedure equalreplaceby u;
   'replaceby . u;

put('replaceby,'psopfn,'equalreplaceby);

flag('(replaceby),'equalopr);           % Make LHS, RHS etc work.

flag('(replaceby),'spaced);             % Make it print with spaces.

symbolic procedure formreplaceby(u,vars,mode);
   list('list,mkquote car u,form1(cadr u,vars,mode),
                                !*s2arg(form1(caddr u,vars,mode),vars));

put('replaceby,'formfn,'formreplaceby);

infix when;

precedence when,=>;

symbolic procedure formwhen(u,vars,mode);
   list('list,algid('when,vars),form1(cadr u,vars,mode),
%  We exclude formbool in following so that rules print prettily.
%                   mkarg(formbool(caddr u,vars,mode),vars));
                    mkarg(caddr u,vars));

put('when,'formfn,'formwhen);

flag('(whereexp),'listargp);   % letsub.

% put('letsub,'simpfn,'simpletsub);

put('whereexp,'psopfn,'evalwhereexp);

% symbolic procedure simpletsub u; simp evalletsub1(u,t);

symbolic procedure evalwhereexp u;
   % We assume that the arguments of this function are well-formed, as
   % they would be if produced from a "where" parse.
   % It looks like there is a spurious simplification, but it's needed
   % in x:= (e^(12i*pi/5) - e^(8i*pi/5) + 4e^(6i*pi/5) - e^(4i*pi/5)
   %     - 2e^(2i*pi/5) - 1)/(16e^(6i*pi/5)); y:= {e^(~a*i*pi/~(~ b))
   %     => e^((a - b)/b*i*pi) when numberp a and numberp b and a>b};
   %     x where y;
   evalletsub({cdar u,{'aeval,mkquote{'aeval,carx(cdr u,'where)}}},nil);

flag('(aeval),'opfn);   % To make the previous procedure work.

% symbolic procedure evalletsub1(u,v);
%  begin scalar x;
%     x := car u;
%     u := carx(cdr u,'simpletsub);
%     if eqcar(x,'list) then x := cdr x else errach 'simpletsub;
%     return evalletsub2({x,{'aeval,mkquote u}},v)
%  end;

symbolic procedure evalletsub(u,v);
   if errorp(u := evalletsub2(u,v))
     then rerror(alg,24,"Invalid simplification")
    else car u;

symbolic procedure evalletsub2(u,v);
  % car u   is an untagged list of rules or ruleset names,
  % cadr u  is an expression to be evaluated by errorset* with the
  %          rules activated locally,
  % v should be nil unless the rules contain equations.
  % Returns the expression value corresponding to the
  % errorset protocol.
   begin scalar newrule!*,oldrules!*,props!*,w;
      w := set_rules(car u,v);
      % We need resimp on since u may contain (*SQ ... T).
      u := errorset!*(cadr u,nil); % where !*resimp = t;
      % Restore previous environment, if changed.
      restore_rules w;
      return u
   end;

symbolic procedure set_rules(u,v);
   begin scalar !*resimp,x,y,z;
      for each j in u do
      % The "v" check in next line causes "a where a=>4" to fail.
         if eqcar(j,'replaceby) then y := j . y
          else if null v and eqcar(j,'equal)
           then <<lprim "Please use => instead of = in rules";
                  y := ('replaceby . cdr j) . y>>
          else if (x := validrule j)
             or idp j and (x := validrule reval j)
           then (x := reverse car x) and <<rule!-list(x,t); z := x . z>>
          else typerr(j,"rule list");
      rule!-list(y,t);
      return y . z
   end;

symbolic procedure restore_rules u;
   <<for each j in u do rule!-list(j,nil);
     for each j in oldrules!*
         do if atom cdar j
              then if idp cdar j
                     then if cdar j eq 'scalar
                            then let3(caar j,cadr j,nil,t,nil)
                           else typelet(caar j,cadr j,nil,t,cdar j)
                    else nil
             else rule!*(car j,cadr j,caddr j,cadddr j,'old);
     restore_props()>>
   where !*resimp := nil;

symbolic procedure restore_props;
   % At present, the only thing props!* can contain is an RTYPE
   % property.  However, it is in this form to handle any other cases
   % that arise.
   for each j in props!* do
      if pairp cdr j then put(car j,cadr j,cddr j)
       else flag({car j},cdr j);

symbolic procedure resimpcar u; resimp car u;

symbolic procedure validrule u;
   (if null x then nil else list x) where x=validrule1 u;

symbolic procedure validrule1 u;
   if atom u then nil
    else if car u eq 'list
     then if null cdr u then {{}}
           else for each j in cdr u collect validrule1 j
    else if car u eq 'replaceby then u
    else if car u eq 'equal then 'replaceby . cdr u
    else nil;

symbolic procedure remove!-free!-vars!* u;
   remove!-free!-vars u where !*!*noremove!*!* := t;

symbolic procedure remove!-free!-vars u;
   begin scalar x,w;
      return if atom u then u
          else if car u eq '!~
           then if !*!*noremove!*!*
                  then if (x := atsoc(cadr u,frasc!*))
                         or eqcar(cadr u,'!~)
                            and (x := atsoc(cadadr u,frasc!*))
                        then cdr x else u
                 else if atom cdr u then typerr(u,"free variable")
                 % Allow for the substitution of a free variable.
                 else if numberp(w := cadr u) then u
                 else if idp w or eqcar(w,'!~) and (w:=cadr w)
                  then <<frlis!* := union(list get!-free!-form cadr u,
                                          frlis!*);
                         w>>
                 else if idp caadr u   % Free operator.
                  then <<frlis!* := union(list get!-free!-form caadr u,
                                          frlis!*);
                         caadr u . remove!-free!-vars!-l cdadr u>>
                 else typerr(u,"free variable")
          else remove!-free!-vars!-l u
   end;

%symbolic procedure remove!-free!-vars!-l u;
%   if atom u then u
%    else if car u eq '!*sq then remove!-free!-vars!-l prepsq!* cadr u
%    else (if x=u then u else x)
%         where x=remove!-free!-vars car u . remove!-free!-vars!-l cdr u;

% The version that follows is supposed to have exactly the same external
% behaviour as the above simpler one apart from it using a LOT less stack.
% ACN December 2011.

symbolic procedure remove!-free!-vars!-l u;
  begin
    scalar r, w, changed;
  top:
    if atom u then <<
       while r do <<
          if not changed then <<
             if caar w = car r then u := car w
             else <<
               u := car r . u;
               changed := t >> >>
          else u := car r . u;
          w := cdr w;
          r := cdr r >>;
       return u >>
    else if car u eq '!*sq then <<
       u := prepsq!* cadr u;
       changed := t;
       go to top >>;
    w := u . w;
    r := remove!-free!-vars car u . r;
    u := cdr u;
    go to top
  end;


symbolic procedure get!-free!-form u;
   begin scalar x,opt;
      if x := atsoc(u,frasc!*) then return cdr x;
      if eqcar(u,'!~) then <<u:= cadr u; x := '(!! !~ !! !~); opt := t>>
       else x := '(!! !~);
      x := intern compress append(x,explode u);
      frasc!* := (u . x) . frasc!*;
      if opt then flag({x},'optional);
      return x
   end;


symbolic procedure term!-split u;
   % U is a standard form which is not a kernel list (i.e., kernlp
   % is false). Result is the dotted pair of the leading part of the
    % expression for which kernlp is true, and the remainder;
   begin scalar x;
      while null red u do <<x := lpow u . x; u := lc u>>;
      return tpowadd(x,!*t2f lt u) . tpowadd(x,red u)
   end;

symbolic procedure tpowadd(u,v);
   <<for each j in u do v := !*t2f(j .* v); v>>;

symbolic procedure frvarsof(u,l);
  % Extract the free variables in u in their left-to-right order.
   if memq(u,frlis!*) then if memq(u,l) then l else append(l,{u})
    else if atom u then l
    else frvarsof(cdr u,frvarsof(car u,l));

symbolic procedure simp0 u;
   begin scalar !*factor,x,y,z;
        if eqcar(u,'!*sq) then return simp0 prepsq!* cadr u;
        y := setkorder frvarsof(u,nil);
        x := subfg!* . !*sub2;
        alglist!* := nil . nil;   % Since assignments will change.
        subfg!* := nil;
        if atom u
           or idp car u
              and (flagp(car u,'simp0fn) or get(car u,'rtype))
          then z := simp u
         else z := simpiden u;
        rplaca(alglist!*, delete_from_alglist(u, car alglist!*));
        % Since we don't want to keep this value.
        subfg!* := car x;
        !*sub2 := cdr x;
        setkorder y;
        return z
   end;

flag('(cons difference eps expt minus plus quotient times),'simp0fn);

symbolic procedure let!-prepf u;
   subla(for each x in frasc!* collect (cdr x . car x),prepf u);

symbolic procedure match u;
   match00 u where frasc!* = nil;

symbolic procedure match00 u;
   <<for each x in u do let2(cadr x,caddr x,t,t);
     frasc!* := mcond!* := nil>>;

symbolic procedure clear u;
   begin
      rmsubs();
      u := errorset!*(list('clear1,mkquote u),t);
      mcond!* := frasc!* := nil;
      if errorp u then error1() else return car u
   end;

symbolic procedure clear1 u;
   begin scalar x,y;
      while u do
         <<if flagp(x := car u,'share)
             then if not flagp(x,'reserved) then set(x,x) else rsverr x
            % if argument is an explicit list, clear each element.
            else if eqcar(x,'list)
                   then u := nil . append(cdr x,cdr u)
            % The following two cases allow for rules or the lhs of
            % rules as arguments to CLEAR.
            else if eqcar(x,'replaceby) then rule!-list(list x,nil)
            else if smemq('!~,x)
             then if eqcar(x,'equal) then rule!-list(list x,nil)
                   else rule!-list(list list('replaceby,x,nil),nil)
            % Hook for a generalized "clear" facility.
            else if (y := get(if atom x then x else car x,'clearfn))
                 then apply1(y,x)
            else <<let2(x,nil,nil,nil); let2(x,nil,t,nil)>>;
           u := cdr u>>
   end;

symbolic procedure typelet(u,v,ltype,b,rtype);
   % General function for setting up rules for typed expressions.
   % LTYPE is the type of the left hand side U, RTYPE, that of RHS V.
   % B is a flag that is true if this is an update, nil for a removal.
   begin scalar ls;
        if null rtype then rtype := 'scalar;
        if ltype eq rtype then go to a
         else if null b then go to c
         else if ltype
          then if ltype eq 'list and rtype eq 'scalar
                 then <<ls := t; go to l>>
                else typerr(list(ltype,u),rtype)
         else if not atom u
          then if arrayp car u then go to a else typerr(u,rtype);
        redmsg(u,rtype);
    l:  put(u,'rtype,rtype);
        ltype := rtype;
    a:  if b and (not atom u or flagp(u,'used!*)) then rmsubs();
    c:  if not atom u
          then if arrayp car u
                 then setelv(u,if b then v else nil)
                else put(car u,'opmtch,xadd!*(cdr u .
                    list(nil . (if mcond!* then mcond!* else t),v,nil),
                        get(car u,'opmtch),b))
         else if null b
          then <<remprop(u,'avalue);
                 remprop(u,'rtype);
                 if ltype eq 'array then remprop(u,'dimension)>>
         else if ls
          then <<remprop(u,'rtype); put!-avalue(u,rtype,v)>>
         else <<if (b := get(u,'avalue))
                  then if not(rtype eq car b)
                          and (not(car b memq(ls := '(scalar list)))
                               or not(rtype memq ls))
                         then typerr(list(car b,u),rtype);
                put!-avalue(u,rtype,v)>>
   end;

symbolic procedure setk(u,v);
   if not atom u
     then (if x then setk0(car u . apply1(x,cdr u),v)
            else if get(car u,'rtype) eq 'matrix then setk0(u,v)
            else setk0(car u . revlis cdr u,v))
           where x=get(car u,'evalargfn)
    else setk0(u,v);

symbolic procedure setk0(u,v);
   % Clear frasc!* to allow for autoloading within LET constructs.
   begin scalar x,frasc!*;
      % We need to reset alglist!* for structures on the left or right
      % hand side.
      if (x := getrtype v) and get(x,'setelemfn)
        then <<alglist!* := nil . nil; let2(u,v,nil,t)>>
       else if not atom u
         and idp car u
      % Excalc currently needs getrtype to check for free indices.
      % Getrtype *must* be called as first argument in OR below.
         and ((x := getrtype u or get(car u,'rtype))
                and (x := get(x,'setelemfn))
               or (x := get(car u,'setkfn)))
        % We must update alglist!* when an element is defined.
        then <<alglist!* := nil . nil; apply2(x,u,v)>>
        % alglist!* is updated here in simp0.
       else let2(u,v,nil,t);
      return v
   end;

symbolic procedure setk1(u,v,b);
   begin scalar x,y,z,!*uncached;
      !*uncached := t;
      if atom u
        then <<if null b
                 then <<if not get(u,'avalue)
                          then msgpri(nil,u,"not found",nil,nil)
                         else remprop(u,'avalue);
                        return nil>>
                else if (x:= get(u,'avalue)) then put!-avalue(u,car x,v)
                else put!-avalue(u,'scalar,v);
               return v>>
       else if not atom car u
        then rerror(alg,25,"Invalid syntax: improper assignment");
      u := car u . revlis cdr u;
      if null b
        then <<z:=assoc(u,wtl!*);
               if not(y := get(car u,'kvalue))
                  or not (x := assoc(u,y))
                 then <<if null z and null !*sqrtrulep then
                            msgpri(nil,u,"not found",nil,nil)>>
                else put(car u,'kvalue,delete(x,y));
                if z then wtl!*:=delasc(u,wtl!*);
               return nil>>
       else if not (y := get(car u,'kvalue))
        then put!-kvalue(car u,nil,u,v)
       else <<if x := assoc(u,y)
                then <<updoldrules(u,v); y := delasc(car x,y)>>;
              put!-kvalue(car u,y,u,v)>>;
      return v
     end;

% symbolic procedure put!-avalue(u,v,w);
%    if smember(u,w) then recursiveerror u
%    else put(u,'avalue,{v,w});

symbolic procedure put!-avalue(u,v,w);
   % This definition allows for an assignment such as a := a 4.
   if v eq 'scalar
     then if eqcar(w,'!*sq) and sq_member(u,cadr w)
            then recursiveerror u
           else if !*reduce4 then putobject(u,w,'generic)
           else put(u,'avalue,{v,w})
    else if smember(u,w) then recursiveerror u
    else put(u,'avalue,{v,w});

symbolic procedure sq_member(u,v);
   sf_member(u,numr v) or sf_member(u,denr v);

symbolic procedure sf_member(u,v);
   null domainp v and
     (mvar_member(u,mvar v) or sf_member(u,lc v) or sf_member(u,red v));

symbolic procedure mvar_member(u,v);
   % This and arglist member have to cater for the funny forms we
   % find in packages like TAYLOR.
   u = v or (null atom v and arglist_member(u,cdr v));

symbolic procedure arglist_member(u,v);
   null atom v and (mvar_member(u,car v) or arglist_member(u,cdr v));

% symbolic procedure put!-kvalue(u,v,w,x);
%    if smember(w,x) then recursiveerror w
%     else put(u,'kvalue,aconc(v,{w,x}));

symbolic procedure put!-kvalue(u,v,w,x);
   % This definition is needed to allow p(2) := sqrt(1-p^2).
   if (if eqcar(x,'!*sq) then sq_member(w,cadr x) else smember(w,x))
     then recursiveerror w
    else put(u,'kvalue,aconc(v,{w,x}));

symbolic procedure klistt u;
   if atom u then nil else caar u . klistt cdr carx(u,'list);

symbolic procedure kernlp u;
   % Returns leading domain coefficient if U is a monomial product
   % of kernels, NIL otherwise.
   if domainp u then u else if null red u then kernlp lc u else nil;

symbolic procedure xadd(u,v,b);
   % Adds replacement U to table V, with new rule at head.
   % Note that format of u and v depends on whether a free variable
   % occurs in the expression or asymplis* is being updated!!.
   begin scalar x;
        x := assoc(car u,v);
        if null x
          then if b and not(b eq 'replace) then v := u . v else nil
         else if b
          then <<v := delete(x,v);
                 if not atom cdr x and length x=5
                   then x := cdr x;  % No free variable.
                 if not atom cdr x   % atom is asymplis update.
                   then updoldrules(caddr x,cdadr x);
                 if not(b eq 'replace) then v := u . v>>
%        else if cadr x=cadr u then v := delete(x,v);
         else if atom cdr x and cdr x=cdr u
              or not atom cdr x and cadr x=cadr u
          then v := delete(x,v);
        return v
   end;

symbolic procedure updoldrules(v,w);
   (if null u then nil
     else oldrules!* := append(
                 (if not atom v and numberp cdr v   % asymptotic case.
                    then list list(list('expt,car v,cdr v),0,nil,t)
                   else if atom car u
                    then list list(car u . car v,cadr v,nil,t)
                   else (if car u neq y
                           then list list(car u,y,x,rsubla(x,w))
                          else nil) where y=rsubla(x,v)),
                 oldrules!*)
           where x=caddr u)
    where u=newrule!*;

symbolic procedure xadd!*(u,v,b);
   % Adds replacement U to table V, with new rule at head.
   % Also checks boolean part for equality.
   % Note, in an earlier version, we removed all rules in the CLEAR mode
   % regardless of whether they came from a LET or a MATCH, or had
   % boolean constraints.  However, this made the fps tests not work.
   begin scalar x,y;
      x := v;
%     while x and not(car u=caar x and (cadr u=cadar x or null b))
      while x and not(car u=caar x and cadr u=cadar x)
         do x := cdr x;
      if x then <<v := delete(car x,v); x := car x;
                  % If this section is entered, then car x and car
                  % newrule!* should be the same. If not, a rule of the
                  % form a+b => c might have occurred, in which case we
                  % need to adjust the form of the replaced value.
                 if b and newrule!*
                   then if car x neq (y := car newrule!*)
                           and powlisp car x
                      then updoldrules(prepsq simp {'plus,y,
                              {'difference,caddr x,'times .
                       for each j in car x collect {'expt,car j,cdr j}}},
                                       cdadr x)
                  else updoldrules(caddr x,cdadr x)>>;
      if b then v := u . v;
      return v
   end;

symbolic procedure powlisp u;
   null u or not atom car u and numberp cdar u and powlisp cdr u;

symbolic procedure rsubla(u,v);
   begin scalar x;
        if null u or null v then return v
         else if atom v
                 then return if x:= rassoc(v,u) then car x else v
         else return(rsubla(u,car v) . rsubla(u,cdr v))
   end;

endmodule;

end;
