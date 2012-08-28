module reval; % Functions for algebraic evaluation of prefix forms.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid '(!*combineexpt !*exp !*intstr !*listargs !*mcd !*resimp alglist!*
        dmode!* subfg!* varstack!*);

switch listargs;

global '(!*resubs !*sqvar!* !*val);

symbolic procedure reval u;
   reval1(u,t);

symbolic procedure aeval u;
   reval1(u,nil);

symbolic procedure aeval!* u;
   % This version clears alglist!* to avoid invalid computation in
   % loops.
   begin % scalar alglist!*; rebinding is not enough.
      alglist!* := nil . nil;
      return reval1(u,nil)
   end;

remprop('revalp, 'simpfg); % While building Reduce rmsubs may not yet exist.

switch revalp;

on revalp;

put('revalp, 'simpfg, '((t (rmsubs))));

symbolic procedure reval1(u,v);
   if null !*revalp then u
   else
   (begin scalar x,y;
      if null u then return nil   % This may give trouble.
       else if stringp u then return u
       else if fixp u
        then return if flagp(dmode!*,'convert) then reval2(u,v) else u
       else if atom u
        then if null subfg!* then return u
              else if idp u and (x := get(u,'avalue))
               then if u memq varstack!* then recursiveerror u
                     else <<varstack!* := u . varstack!*;
                            return if y := get(car x,'evfn)
                                     then apply2(y,u,v)
                                    else reval1(cadr x,v)>>
              else nil
       else if not idp car u % or car u eq '!*comma!*
        then errpri2(u,t)
       else if car u eq '!*sq
        then return if caddr u and null !*resimp
                      then if null v then u else prepsqxx cadr u
                     else reval2(u,v)
       else if flagp(car u,'remember) then return rmmbreval(u,v)
       else if flagp(car u,'opfn) then return reval1(opfneval u,v)
       else if x := get(car u,'psopfn)
         then <<u := apply1(x,cdr u);
                if x := get(x,'cleanupfn) then u := apply2(x,u,v);
                return u>>
          % Note that we assume that the results of such functions are
          % always returned in evaluated form.
       else if arrayp car u then return reval1(getelv u,v);
       return if x := getrtype u then
                  if y := get(x,'evfn) then apply2(y,u,v)
                    else rerror(alg,101,
                                list("Missing evaluation for type",x))
               else if not atom u
                     and not atom cdr u
                     and (y := getrtype cadr u)
                     and null(y eq 'list and cddr u) % Don't pass opr to
                                   % list if there is more than one arg.
                     and (x := get(y,'aggregatefn))
                     and (not(x eq 'matrixmap) or flagp(car u,'matmapfn))
                     and not flagp(car u,'boolean)
                     and not !*listargs and not flagp(car u,'listargp)
                then apply2(x,u,v)
               else reval2(u,v)
   end) where varstack!* := varstack!*;

% I put "hold" here because its purpose is to default much of what reval
% does! The idea is that hold(expression) will encapsulate the un-evaluated
% form of the given expression. Note that that will mean that variables
% referenced in it will not have their values used, and that predefined
% functions and operators etc do not get simplified. It is rather like the
% Lisp "quote" notation. 

symbolic procedure simphold u;
   if length u neq 1 then rederr "hold needs exactly one argument"
   else mksp(if eqcar(car u, '!*hold) then car u
             else list('!*hold, car u), 1) .* 1 .+ nil ./ 1;

% put('hold, 'simpfn, 'simphold); This clashes with the pm package...
% So users who need this should use !*hold as their keyword for now.
put('!*hold, 'simpfn, 'simphold);

flagop listargp;

symbolic procedure rmmbreval(u,v);
 % The leading operator of u is flagged 'remember.
  begin scalar fn,x,w,u1,u2;
   fn := car u;
   u1:={fn}; u2:={fn};
   for each y in cdr u do
   <<w:=reval1(y,nil); u2:=aconc(u2,w);
     if eqcar(w,'!*sq) then w:=!*q2a(cadr w);
     u1:=aconc(u1,w)>>;
   if (x:=assoc(u1,w:=get(fn,'kvalue))) then<<x:=cadr x; go to a>>;
     % Evaluate "algebraic procedure" and "algebraic operator" directly.
   if flagp(fn,'opfn) then x:= reval1(opfneval u2,v)
   else if get(fn,'simpfn) then x:=!*q2a1(simp!* u2,v)
   else % All others are passed to reval.
   << remflag({fn},'remember);
      x:=reval1(u2,v);
      flag({fn},'remember);
   >>;
   if not smember(u1,x) and not smember(u2,x)
        then put!-kvalue(fn,get(fn,'kvalue),(car u)
                . foreach uuu in cdr u collect reval uuu,x);
 a: return x;
  end;

symbolic procedure remember u;
  % Remember declaration for operator and procedure names.
 for each fn in u do
  <<if not flagp(fn,'opfn) and null get(fn,'simpfn)
        then <<redmsg(fn,"operator"); mkop fn>>;
    if flagp(fn,'noval) or flagp(fn,'listargp)
        then typerr(fn,"remember operator");
    flag({fn},'remember);
  >>;

put('remember,'stat,'rlis);

symbolic procedure recursiveerror u;
   msgpri(nil,u,"improperly defined in terms of itself",nil,t);

put('quote,'psopfn,'car);    % Since we don't want this evaluated.

symbolic procedure opfneval u;
   if flagp(car u ,'remember) then
    begin scalar interm,resul,x;
             interm := for each j in
                  (if flagp(car u,'noval) then cdr u else revlis cdr u)
                  collect if fixp j then j else mkquote j;
              if (x:=assoc(car u . interm ,get(car u,'kvalue)))
                         then return cadr x;
              resul := lispeval(car u . interm);
              put!-kvalue(car u,get(car u,'kvalue), car u . interm, resul);
              return resul;
     end
       else
   lispeval(car u . for each j in
                  (if flagp(car u,'noval) then cdr u else revlis cdr u)
                  collect mkquote j);

flag('(reval),'opfn);   % to make it a symbolic operator.

symbolic procedure reval2(u,v);
   % This test is designed to simplify expressions such as e*e^(2/(2-x))
   % and e^(x+3)*e^(3/(4-3*x))/e^(5*x-3). However, the normform test
   % shows it doesn't work well with non-integer domains.
   if v or null !*combineexpt or dmode!* then !*q2a1(simp!* u,v)
    else !*q2a1((simp!* u where !*mcd = nil),v);

symbolic procedure getrtype u;
   % Returns overall algebraic type of u (or NIL is expression is a
   % scalar). Analysis is incomplete for efficiency reasons.
   % Type conflicts will later be resolved when expression is evaluated.
   begin scalar x,y;
    return
    if null u then nil   % Suggested by P.K.H. Gragert to avoid the
                         % loop caused if NIL has a share flag.
     else if atom u
      then if not idp u then not numberp u and getrtype1 u
            else if flagp(u,'share) % then getrtype lispeval u
             then if (x := eval u) eq u then nil else getrtype x
            else if (x := get(u,'avalue)) and
                       not(car x memq '(scalar generic))
                    or (x := get(u,'rtype)) and (x := list x)
                    then if y := get(car x,'rtypefn) then apply1(y,nil)
                          else car x
                  else nil
     else if not idp car u then nil
     else if (x := get(car u,'avalue)) and (x := get(car x,'rtypefn))
      then apply1(x,cdr u)
     % Special case handling for the SUB operator.
     else if car u eq 'sub then 'yetunknowntype
     else getrtype2 u
   end;

symbolic procedure getrtype1 u;
   % Placeholder for packages that use vectors.
   nil;

symbolic procedure getrtype2 u;
   % Placeholder for packages that key expression type to the operator.
   begin scalar x;
     % Next line is maybe only needed by EXCALC.
      return if (x := get(car u,'rtype)) and (x := get(x,'rtypefn))
               then apply1(x,cdr u)
              else if x := get(car u,'rtypefn) then apply1(x,cdr u)
              else if flagp(car u,'matmapfn) and cdr u
                 and getrtype cadr u eq 'matrix
               then 'matrix
              else nil
   end;

remprop('rtypecar,'stat);

symbolic procedure rtypecar u;
   for each j in u do put(j,'rtypefn,'getrtypecar);

deflist('((rtypecar rlis)),'stat);

rtypecar difference,expt,minus,plus,recip;

deflist('
  ((quotient getrtypeor)
   (times getrtypeor)
   (!*sq (lambda (x) nil))
 ),'rtypefn);

symbolic procedure getrtypecar u; getrtype car u;

symbolic procedure getrtypeor u;
   u and (getrtype car u or getrtypeor cdr u);

symbolic procedure !*eqn2a u;
   % If u is an equation a=b, it is converted to an equivalent equation
   % a-b=0, or if a=0, b=0.  Otherwise u is returned converted to true
   % prefix form.
   if not eqexpr u then prepsqyy u
    else if null cdr u or null cddr u or cdddr u
     then typerr(u,"equation")
    else (if rh=0 then lh else if lh=0 then rh else{'difference,lh,rh})
          where lh=prepsqyy cadr u,rh=prepsqyy caddr u;

symbolic procedure prepsqyy u;
   if eqcar(u,'!*sq) then prepsqxx cadr u else u;

symbolic procedure getelv u;
   % Returns the value of the array element U.
%  getel(car u . for each x in cdr u collect ieval x);
   getel(car u . for each x in cdr u collect reval_without_mod x);

symbolic procedure setelv(u,v);
%  setel(car u . for each x in cdr u collect ieval x,v);
   setel(car u . for each x in cdr u collect reval_without_mod x,v);

symbolic procedure reval_without_mod u;
   % Evaluate u without a modulus.
   if dmode!* eq '!:mod!: then (reval u where dmode!* = nil)
    else reval u;

symbolic procedure revlis u; for each j in u collect reval j;

symbolic procedure revop1 u;
   if !*val then car u . revlis cdr u else u;

symbolic procedure mk!*sq u;
   % Modified by Francis J. Wright to return a list correctly.
%  if null numr u then 0
%   else if atom numr u and denr u=1 then numr u
%   else '!*sq . expchk u . if !*resubs then !*sqvar!* else list nil;
  (if null numr u then 0
    else if atom numr u and denr u=1 then numr u
    else if kernp u and eqcar(mvar numr u,'list) then mvar numr u
    else '!*sq . u . if !*resubs then !*sqvar!* else list nil)
   where u=expchk u;

symbolic macro procedure !*sq u;
   % Provide an interface to symbolic mode.
   prepsq cadr u;

symbolic procedure expchk u; if !*exp then u else offexpchk u;

symbolic procedure lengthreval u;
   begin scalar v,w,x;
      if length u neq 1
        then rerror(alg,11,
                    "LENGTH called with wrong number of arguments");
      u := car u;
      if idp u and arrayp u then return 'list . get(u,'dimension);
      v := aeval u;
      if (w := getrtype v) and (x := get(w,'lengthfn))
        then return apply1(x,v)
       else if atom v then return 1
       else if not idp car v or not(x := get(car v,'lengthfn))
        then if w
          then lprie list("LENGTH not defined for argument of type",w)
         else typerr(u,"LENGTH argument")
       else return apply1(x,cdr v)
   end;

put('length,'psopfn,'lengthreval);


% Code for evaluation of expressions whose type can only be
% infered after partial evaluation.

symbolic procedure yetunknowntypeeval(u,v);
   % Assumes that only psopfn's can produce yet unknown types.
   reval1(eval!-yetunknowntypeexpr(u,v),v);

symbolic procedure eval!-yetunknowntypeexpr(u,v);
   if atom u
      then ((if w then eval!-yetunknowntypeexpr(cadr w,v)
              else u)
            where w = get(u,'avalue))
    else if car u eq '!*sq or get(car u,'dname) or car u eq '!:dn!:
     then u
    else ((if x and (getrtype u eq 'yetunknowntype)
              then apply1(x,cdr u)
            else car u . for each j in cdr u collect
                             eval!-yetunknowntypeexpr(j,v))
          where x = get(car u,'psopfn));

put('yetunknowntype,'evfn,'yetunknowntypeeval);

endmodule;

end;
