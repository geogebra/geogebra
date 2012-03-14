module newtok;  % Functions for introducing infix tokens to the system.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


fluid '(!*msg !*redeflg!*);

global '(preclis!* fixedpreclis!*);

% Several operators in REDUCE are used in an infix form (e.g., +,- ).
% The internal alphanumeric names associated with these operators are
% introduced by the function NEWTOK defined below.  This association,
% and the precedence of each infix operator, is initialized in this
% section.  We also associate printing characters with each internal
% alphanumeric name as well.

fixedpreclis!* := '(where !*comma!* setq);

preclis!*:= '(or and member memq equal neq eq geq greaterp leq   % not
              lessp freeof plus difference times quotient expt cons);

deflist ('(
%  (not not)
   (plus plus)
   (difference minus)
   (minus minus)
   (times times)
   (quotient recip)
   (recip recip)
 ), 'unary);

flag ('(and or !*comma!* plus times),'nary);

flag ('(cons setq plus times),'right);

deflist ('((minus plus) (recip times)),'alt);

symbolic procedure mkprec;
   begin scalar x,y,z;
        x := append(fixedpreclis!*,preclis!*);
        y := 1;
    a:  if null x then return nil;
        put(car x,'infix,y);
        put(car x,'op,list list(y,y));   % for RPRINT.
        if z := get(car x,'unary) then put(z,'infix,y);
        if and(z,null flagp(z,'nary)) then put(z,'op,list(nil,y));
        x := cdr x;
        y := add1 y;
        go to a
   end;

mkprec();

symbolic procedure newtok u;
   begin scalar !*redeflg!*,x,y;
      if atom u or atom car u or null idp caar u
        then typerr(u,"NEWTOK argument");
      % set up SWITCH* property.
      put(caar u,'switch!*,
          cdr newtok1(car u,cadr u,get(caar u,'switch!*)));
      % set up PRTCH property.
      y := intern compress consescc car u;
      if !*redeflg!* then lprim list(y,"redefined");
      put(cadr u,'prtch,y);
      if x := get(cadr u,'unary) then put(x,'prtch,y)
   end;

symbolic procedure newtok1(charlist,name,propy);
      if null propy then lstchr(charlist,name)
       else if null cdr charlist
        then begin
                if cdr propy and !*msg then !*redeflg!* := t;
                return list(car charlist,car propy,name)
             end
       else car charlist . newtok2(cdr charlist,name,car propy)
                         . cdr propy;

symbolic procedure newtok2(charlist,name,assoclist);
   if null assoclist then list lstchr(charlist,name)
    else if car charlist eq caar assoclist
     then newtok1(charlist,name,cdar assoclist) . cdr assoclist
    else car assoclist . newtok2(charlist,name,cdr assoclist);

symbolic procedure consescc u;
   if null u then nil else '!! . car u . consescc cdr u;

symbolic procedure lstchr(u,v);
   if null cdr u then list(car u,nil,v)
    else list(car u,list lstchr(cdr u,v));

newtok '((!$) !*semicol!*);
newtok '((!;) !*semicol!*);
newtok '((!+) plus);
newtok '((!-) difference);
newtok '((!*) times);
newtok '((!^) expt);
newtok '((!* !*) expt);
newtok '((!/) quotient);
newtok '((!=) equal);
newtok '((!,) !*comma!*);
newtok '((!() !*lpar!*);
newtok '((!)) !*rpar!*);
newtok '((!:) !*colon!*);
newtok '((!: !=) setq);
newtok '((!.) cons);
newtok '((!<) lessp);
newtok '((!< !=) leq);
newtok '((![) !*lsqbkt!*);
newtok '((!< !<) !*lsqbkt!*);
newtok '((!>) greaterp);
newtok '((!> !=) geq);
newtok '((!]) !*rsqbkt!*);
newtok '((!> !>) !*rsqbkt!*);

put('expt,'prtch,'!*!*);   % To ensure that FORTRAN output is correct.

flag('(difference minus plus setq),'spaced);

flag('(newtok),'eval);

endmodule;

end;
