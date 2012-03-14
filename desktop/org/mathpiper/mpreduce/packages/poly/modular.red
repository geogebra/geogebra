module modular; % *** Tables for modular integers ***.

% Author: Anthony C. Hearn and Herbert Melenk.

% Copyright (c) 1995 The RAND Corporation. All rights reserved.

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


global '(domainlist!*);

fluid '(!*balanced_mod !*modular !*precise current!-modulus alglist!*
        dmode!*);

switch modular,balanced_mod;

domainlist!* := union('(!:mod!:),domainlist!*);

put('modular,'tag,'!:mod!:);
put('!:mod!:,'dname,'modular);
flag('(!:mod!:),'field);
flag('(!:mod!:),'convert);
put('!:mod!:,'i2d,'!*i2mod);
put('!:mod!:,'!:ft!:,'modcnv);
put('!:mod!:,'!:rn!:,'modcnv);
put('!:mod!:,'minusp,'modminusp!:);
put('!:mod!:,'plus,'modplus!:);
put('!:mod!:,'times,'modtimes!:);
put('!:mod!:,'difference,'moddifference!:);
put('!:mod!:,'quotient,'modquotient!:);
put('!:mod!:,'divide,'moddivide!:);
put('!:mod!:,'gcd,'modgcd!:);
put('!:mod!:,'zerop,'modzerop!:);
put('!:mod!:,'onep,'modonep!:);
put('!:mod!:,'factorfn,'factormod!:);
put('!:mod!:,'sqfrfactorfn,'factormod!:);
put('!:mod!:,'expt,'exptmod!:);
put('!:mod!:,'prepfn,'modprep!:);
put('!:mod!:,'prifn,'(lambda(x) (prin2!* (prepf x))));

put('!:mod!:,'unitsfn,'!:mod!:unitconv);

symbolic procedure !*modular2f u;
   % Convert u to a modular number.  Treat 0 as special case, but not 1.
   % Also allow for !*balanced_mod.
   if u=0 then nil
%   else if u=1 then 1
    else if !*balanced_mod
     then if u+u>current!-modulus
            then '!:mod!: . (u - current!-modulus)
           else if u+u <= - current!-modulus
            then !*modular2f(u + current!-modulus)
           else '!:mod!: . u
    else '!:mod!: . u;

symbolic procedure exptmod!:(u,n);
% This procedure will check for cdr u > n-1 if n prime.
% This used to treat 1 as a special case.
   !*modular2f general!-modular!-expt(cdr u,n);

symbolic procedure !:mod!:unitconv(u,v);
  if v=1 then u else
   (if x then multd(x,numr u) ./ multd(x,denr u)
     else mod!-error {'quotient,1,cdr v})
    where x = !*modular2f !:mod!:units(current!-modulus,y,0,1)
    where y = if cdr v>0 or null !*balanced_mod then cdr v
               else current!-modulus+cdr v;

symbolic procedure !:mod!:units(a,b,x,y);
   % Same procedure as general!-reciprocal!-by!-degree in genmod
   %  without error call.
   if b=0 then 0
    else if b=1 then if y < 0 then y+current!-modulus else y
    else begin scalar w;
           w := a/b;
           return !:mod!:units(b,a-b*w,y,x-y*w)
         end;

symbolic procedure !*i2mod u;
   % Converts integer U to modular form.
%  if (u := general!-modular!-number u)=0 then nil else '!:mod!: . u;
   !*modular2f general!-modular!-number u;

symbolic procedure modcnv u;
   rerror(poly,13,list("Conversion between modular integers and",
                       get(car u,'dname),"not defined"));

symbolic procedure modminusp!: u;
   if !*balanced_mod then 2*cdr u > current!-modulus else nil;

symbolic procedure modplus!:(u,v);
   !*modular2f general!-modular!-plus(cdr u,cdr v);

symbolic procedure modtimes!:(u,v);
   !*modular2f general!-modular!-times(cdr u,cdr v);

symbolic procedure moddifference!:(u,v);
   !*modular2f general!-modular!-difference(cdr u,cdr v);

symbolic procedure moddivide!:(u,v); !*i2mod 0 . u;

symbolic procedure modgcd!:(u,v); !*i2mod 1;

symbolic procedure modquotient!:(u,v);
   !*modular2f general!-modular!-times(cdr u,
                                   general!-modular!-reciprocal cdr v);

symbolic procedure modzerop!: u; cdr u=0;

symbolic procedure modonep!: u; cdr u=1;

symbolic procedure factormod!: u;
   begin scalar alglist!*,dmode!*;
      % 1 is needed since factorize expects first factor to be a number.
      return pfactor(!*q2f resimp(u ./ 1),current!-modulus)
   end;

symbolic procedure modprep!: u;
   cdr u;

initdmode 'modular;


% Modular routines are defined in the GENMOD module with the exception
% of the following:

symbolic procedure setmod u;
   % Returns value of CURRENT!-MODULUS on entry unless an error
   % occurs.  It crudely distinguishes between prime moduli, for which
   % division is possible, and others, for which it possibly is not.
   % The code should really distinguish prime powers and composites as
   % well.
   begin scalar dmode!*;
      if not atom u then u := car u;   % Since setmod is a psopfn.
      u := reval u;  % dmode* is NIL, so this won't be reduced wrt
                     % current modulus.
      if fixp u and u>0
        then <<if primep u then flag('(!:mod!:),'field)
                else remflag('(!:mod!:),'field);
               return set!-general!-modulus u>>
      else if u=0 or null u then return current!-modulus
       else typerr(u,"modulus")
   end;

put('setmod, 'psopfn, 'setmod);

% A more general definition of general-modular-number.

%symbolic procedure general!-modular!-number m;
   % Returns normalized M.
%   (lambda n; %if n<0 then n+current!-modulus else n)
%   if atom m then remainder(m,current!-modulus)
%    else begin scalar x;
%       x := dcombine(m,current!-modulus,'divide);
%        return cdr x
%     end;

% Support for "mod" as an infix operator.

infix mod;

precedence mod,over;

put('mod,'psopfn,'evalmod);

symbolic procedure evalmod u;
  begin scalar dm,cp,m,mm,w,!*rounded,!*modular;
    if !*complex then
      <<cp:=t; setdmode('complex,nil); !*complex:=nil>>;
    if (dm:=get(dmode!*,'dname)) then setdmode(dm,nil);
    % We need to evaluate the first term before setting any modulus.
    % e.g., a := -8/7; (num a) mod 7;
    w:=aeval!* car u;
    m:=ieval cadr u;
    setdmode('modular,t); !*modular:=t;
    mm:=apply1('setmod,{m});
    w := aeval!* w;
    apply1('setmod,{mm});
    if dm neq 'modular then
     <<setdmode('modular,nil); if dm then setdmode(dm,t)>>;
    if cp then <<setdmode('complex,t); !*complex :=t>>;
    return w;
  end;

% Support for function evaluation in the modular domain.
% At present only rational exponentiation, including surds.

put('!:mod!:,'domainvalchk,'mod!-domainvalchk);

symbolic procedure mod!-domainvalchk(fn,u);
   begin scalar w;
    w:=if fn='expt then mod!-expt!-fract(car u,cadr u)
       else nil;
    return if w='failed then nil else w ./1;
   end;

symbolic procedure mod!-expt!-fract(u,x);
 % Modular u**x where x is a rational number n/m. Compute a solution of
 % q^n=u^m. If *precise on, expressions with non-unique are not
 % simplified. Non existing surds are mapped to an error message.
  begin scalar n,m,w;
    if denr u =1 then u:=numr u else go to done;
    if eqcar(u,'!:mod!:) then t
      else if fixp u then u:= '!:mod!: . u else go to done;
    if u='(!:mod!: . 1) then return 1;
    n:=numr x; m:=denr x;
    if not fixp n or not fixp m then go to done;
    if m=1 then return exptmod!:(u,n);
    load!-package 'modsr;
    w := msolve {{'equal,{'expt,'x,m},{'expt,cdr u,n}}};
    if w eq 'failed then return w else w := cdr w;
    if null w then mod!-error({'expt,u,{'quotient,n,m}});
    if null cdr w or null !*precise then return caddr cadr car w;
      % value is not unique - prevent the default integer
      % handling that would compute an incorrect value.
      % e.g. sqrt(4) mod 9 is not 2 but {2,7}.
    return !*k2f car fkern {'expt,cdr u,{'quotient,n,m}};
 done:
    return if null w or cdr w then 'failed else caddr car w;
 end;

symbolic procedure mod!-error u;
   typerr(u, {"expression mod", current!-modulus});

endmodule;

end;
