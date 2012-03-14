module mksp; % Functions for making standard powers.

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


% This module has a non-trivial use of ACONC.

fluid '(!*nosubs !*sub2 asymplis!* powlis!* subfg!* wtl!*);

% fluid '(varstack!*);

global '(!*resubs);

% exports mksfpf,mksp,mksq,to;

% imports !*p2f,aconc,eqcar,exptf,exptsq,leq,mkprod!*,module,multsq,
%       ordad,over,simpcar,union;

symbolic procedure getpower(u,n);
   %U is a list (<kernel> . <properties>), N a positive integer.
   %Value is the standard power of U**N;
   <<if eqcar(car u,'expt) and n>1 then !*sub2 := t; car u . n>>;
%   begin scalar v;
%       v := cadr u;
%       if null v then return caar rplaca(cdr u,list (car u . n));
%    a: if n=cdar v then return car v
%        else if n<cdar v
%           then return car rplacw(v,(caar v . n) . (car v . cdr v))
%        else if null cdr v
%           then return cadr rplacd(v,list (caar v . n));
%       v := cdr v;
%       go to a
%   end;

symbolic procedure mksp(u,p);
   %U is a (non-unique) kernel and P a non-zero integer
   %Value is the standard power for U**P;
   getpower(fkern u,p);

symbolic procedure u to p;
   %U is a (unique) kernel and P a non-zero integer;
   %Value is the standard power of U**P;
   u . p;
%   getpower(fkern u,p);

symbolic procedure mksp!*(u,n);
   % Returns a standard form for U**N. If U is a kernel product,
   % direct exponentiation is used.  Otherwise, U is first made
   % positive and then converted into a kernel.
   begin scalar b;
      if null u or kernlp u then return exptf(u,n)
       else if minusf u then <<b := t; u := negf u>>;
      u := !*p2f mksp(u,n);
      return if b and not evenp n then negf u else u
   end;

symbolic procedure mksfpf(u,n);
   % Raises form U to power N with EXP off. Returns a form.
   % If we assume that MKPROD returns a kernlp form, check for red x
   % is redundant.
   (if n=1 then x
     else if domainp x then !:expt(x,n)
     else if n>=0 and onep lc x and null red x
      then (((if subfg!* and z and cdr z<=m then nil
               else !*p2f mksp(y,m))
            where z=assoc(y,asymplis!*)) where m=ldeg x*n,y=mvar x)
     else exptf2(x,n))
    where x=mkprod u;

symbolic procedure mksq(u,n);
    % U is a kernel, N a non-zero integer.
    % Value is a standard quotient of U**N, after making any
    % possible substitutions for U.
   begin scalar x,y,z;
%  (begin scalar x,y,z;
        if null subfg!* then go to a1
         else if (y := assoc(u,wtl!*))
                and null car(y := mksq('k!*,n*cdr y)) then return y
         else if not atom u then go to b
         else if null !*nosubs and (z:= get(u,'avalue)) then go to c;
        if idp u then flag(list u,'used!*);
        %tell system U used as algebraic var (unless it's a string);
    a:  if !*nosubs or n=1 then go to a1
         else if (z:= assoc(u,asymplis!*)) and cdr z<=n
          then return nil ./ 1
         else if ((z:= assoc(u,powlis!*))
                or not atom u and car u memq '(expt sqrt)
                and (z := assoc(cadr u,powlis!*)))
             and not(n*cadr z<0)
           % Implements explicit sign matching.
          then !*sub2 := t;
    a1: if null x then x := fkern u;
        x := !*p2f getpower(x,n) ./ 1;
        return if y then multsq(y,x) else x;
    b:  if null !*nosubs and atom car u
           and ((z := get(car u,'mksqsubfn)) and (z := apply1(z,u))
                or (z:= assoc(u,get(car u,'kvalue))))
          then go to c
         else if not('used!* memq cddr (x := fkern u))
          then aconc(x,'used!*);
        go to a;
    c:  z := cdr z;
%       varstack!* := u . varstack!*;   % I don't think this is needed.
        %optimization is possible as shown if all expression
        %dependency is known;
        %if cdr z then return exptsq(cdr z,n); % Value already computed.
        if null !*resubs then !*nosubs := t;
        x := simpcar z;
        !*nosubs := nil;
        %rplacd(z,x);           % Save simplified value.
        %subl!* := z . subl!*;
        return exptsq(x,n)
   end;
%  end) where varstack!* := varstack!*; % I don't think this is needed.

endmodule;

end;
