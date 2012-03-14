module loops88;  % Rlisp88 looping forms other than the FOR statement.

% Author: Anthony C. Hearn.

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


fluid '(!*blockp loopdelimslist!*);

global '(cursym!* repeatkeywords!* whilekeywords!*);


% ***** REPEAT STATEMENT *****

repeatkeywords!* := '(finally initially returns until with);

symbolic procedure repeatstat88;
  begin scalar body,!*blockp,x,y,z;
      loopdelimslist!* := repeatkeywords!* . loopdelimslist!*;
      flag(repeatkeywords!*,'delim);
      body := erroreval '(xread t);
      if not (cursym!* memq repeatkeywords!*) then symerr('repeat,t);
  a:  x := cursym!*;
      y := erroreval if x eq 'with then '(xread 'lambda)
                      else '(xread t);
      z := (x . y) . z;
      if cursym!* memq repeatkeywords!* then go to a;
      remflag(car loopdelimslist!*,'delim);
      loopdelimslist!* := cdr loopdelimslist!*;
      if loopdelimslist!* then flag(car loopdelimslist!*,'delim);
      return 'repeat . body . reversip z
   end;

symbolic macro procedure repeat88 u;
   begin scalar body,lab,xwith;
        body := cadr u; u := cddr u;
        xwith := atsoc('with,u);
        return sublis(pair('(!$locals !$do !$rets !$inits !$fins !$bool
                             !$label),
                           list(if xwith then cdr xwith else nil,
                                body,
                                x!-car x!-cdr atsoc('returns,u),
                                mkfn(x!-cdr atsoc('initially,u),'progn),
                                mkfn(x!-cdr atsoc('finally,u),'progn),
                                x!-car x!-cdr atsoc('until,u),
                                gensym())),
                      '(prog !$locals
                             !$inits
                        !$label !$do
                             (cond (!$bool !$fins (return !$rets)))
                             (go !$label)))
   end;

symbolic procedure remcomma!* u; if null u then nil else remcomma cdr u;

symbolic procedure x!-car u; if atom u then u else car u;

symbolic procedure x!-cdr u; if null u then nil else list cdr u;

% flag('(repeat),'nochange);

symbolic procedure formrepeat88(u,vars,mode);
   begin scalar y,z;
      for each x in cddr u do
         if car x eq 'with
           then <<y := remcomma cdr x;
                  vars := nconc(for each j in y collect j . 'scalar,
                                vars);
                  z := (car x . y) . z>>
%         else if car x eq 'until
%          then z := (car x . formbool(cdr x,vars,mode)) . z
          else z := (car x . formc(cdr x,vars,mode)) . z;
      return 'repeat . formc(cadr u,vars,mode) . reversip z
   end;


% ***** WHILE STATEMENT *****

whilekeywords!* := '(collect do finally initially returns with);

symbolic procedure whilstat88;
   begin scalar !*blockp,bool1,x,y,z;
      loopdelimslist!* := whilekeywords!* . loopdelimslist!*;
      flag(whilekeywords!*,'delim);
      bool1 := erroreval '(xread t);
      if not (cursym!* memq whilekeywords!*) then symerr('while,t);
  a:  x := cursym!*;
      y := erroreval if x eq 'with then '(xread 'lambda)
                      else '(xread t);
      z := (x . y) . z;
      if cursym!* memq whilekeywords!* then go to a;
      remflag(car loopdelimslist!*,'delim);
      loopdelimslist!* := cdr loopdelimslist!*;
      if loopdelimslist!* then flag(car loopdelimslist!*,'delim);
      return 'while . bool1 . reversip z
   end;

symbolic macro procedure while88 u;
   begin scalar body,bool,lab,rets,vars;
      bool := cadr u; u := cddr u;
      rets := x!-car x!-cdr atsoc('returns,u);
      vars := x!-car x!-cdr atsoc('with,u);
      if body := atsoc('collect,u)
        then <<vars := gensym() . vars;
               body := list('setq,
                            car vars,
                            list('cons,cdr body,car vars));
               if rets then rederr "While loop value conflict";
               rets := list('reversip,car vars)>>
       else if body := atsoc('do,u) then body := cdr body
       else rederr "Missing body in WHILE statement";
      return sublis(pair('(!$locals !$do !$rets !$inits !$fins !$bool
                           !$label),
                         list(vars,
                              body,
                              rets,
                              mkfn(x!-cdr atsoc('initially,u),'progn),
                              mkfn(x!-cdr atsoc('finally,u),'progn),
                              bool,
                              gensym())),
                    '(prog !$locals
                           !$inits
                      !$label
                           (cond ((not !$bool) !$fins (return !$rets)))
                           !$do
                           (go !$label)))
   end;

% flag('(while),'nochange);

symbolic procedure formwhile88(u,vars,mode);
   begin scalar y,z;
      for each x in cddr u do
         if car x eq 'with
           then <<y := remcomma cdr x;
                  vars := nconc(for each j in y collect j . 'scalar,
                                vars);
                  z := (car x . y) . z>>
          else z := (car x . formc(cdr x,vars,mode)) . z;
      return 'while . formc(cadr u,vars,mode) . reversip z
   end;

endmodule;

end;
