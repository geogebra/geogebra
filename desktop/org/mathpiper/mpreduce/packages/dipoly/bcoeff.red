module bcoeff;% Computation of base coefficients.

% Definitions of base coefficient operations for distributive
% polynomial package.  Fields and rings are supported as coefficient
% domains. Side relations (computing modulo an ideal) are supported
% if the list bczerodivl is non-zero.
%
% In this module, a standard quotient coefficient is assumed, unless
% !*grmod!* is true, in which case it is a small modular number.

% Authors: R. Gebauer, A. C. Hearn, H. Kredel

% H. Melenk: added routines for faster computation with
% quotients representing integers.

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


symbolic procedure bcint2op(a1,a2,op);
 if null dmode!* and
    1=denr a1 and numberp (a1:=numr a1) and
    1=denr a2 and numberp (a2:=numr a2) and
    (a1:=if op = 'times then a1*a2 else
           if op = 'plus  then a1+a2 else apply2(op,a1,a2)) then
    ((if a1=0 then nil else a1) ./ 1);

fluid'(!*nat);

% The following two could be smacros.  However, they would then need to
% be included in dipoly, thus destroying the modularity of the base
% coefficient code.

symbolic procedure bcminus!? u;
%    Boolean function. Returns true if u is a negative base coeff
    null !*grmod!* and minusf numr u;

symbolic procedure bczero!? u;
%    Returns a boolean expression, true if the base coefficient u is
%    zero
    if !*grmod!* then eqn(u,0) else null numr u;

symbolic procedure bcfd a;
%    Base coefficient from domain. a is a domain element. bcfd(a)
%    returns the base coefficient a.
     if null !*grmod!* then mkbc(a,1)
      else if fixp a then bcfi a
      else if not(car a eq '!:mod!:)
       then rederr list("Invalid modular coefficient",a)
      else bcfi cdr a;

symbolic procedure bcfi a;
%    Base coefficient from integer. a is an integer. bcfi(a)
%    returns the base coefficient a.
   (if u<0 then if !*balanced_mod and u+u > - current!-modulus then u
            else u #+ current!-modulus
     else if !*balanced_mod and u+u > current!-modulus
      then u #- current!-modulus
     else u) where u=remainder(a,current!-modulus);

symbolic procedure bcdomain!? u;
%  True if base coefficient u is a domain element.
   !*grmod!* or (denr u =1 and domainp numr u);

symbolic procedure bclcmd(u,v);
% Base coefficient least common multiple of denominators.
% u and v are two base coefficients. bclcmd(u,v) calculates the
% least common multiple of the denominator of u and the
% denominator of v and returns a base coefficient of the form
% 1/lcm(denom u,denom v).
  if bczero!? u then mkbc(1,denr v)
   else if bczero!? v then mkbc(1,denr u)
   else mkbc(1,multf(quotfx(denr u,gcdf(denr u,denr v)),denr v));

symbolic procedure bclcmdprod(u,v);
% Base coefficient least common multiple denominator product.
% u is a basecoefficient of the form 1/integer. v is a base
% coefficient. bclcmdprod(u,v) calculates (denom u/denom v)*nom v/1
% and returns a base coefficient.
  mkbc(multf(quotfx(denr u,denr v),numr v),1);

symbolic procedure bcone!? u;
%    Base coefficient one. u is a base coefficient.
%    bcone!?(u) returns a boolean expression, true if the
%    base coefficient u is equal 1.
   if !*grmod!* then eqn(u,1) else denr u = 1 and numr u = 1;

symbolic procedure bcinv u;
%    Base coefficient inverse. u is a base coefficient.
%    bcinv(u) calculates 1/u and returns a base coefficient.
   if !*grmod!*
     then if !*balanced_mod
            then (if v+v>current!-modulus then v #- current!-modulus
                   else v) where v= modular!-reciprocal u
           else reciprocal!-by!-gcd(current!-modulus,u,0,1)
    else invsq u;

symbolic procedure bcneg u;
%    Base coefficient negative. u is a base coefficient.
%    bcneg(u) returns the negative of the base coefficient
%    u, a base coefficient.
   if !*grmod!* then if eqn(u,0) then u else current!-modulus #- u
    else negsq u;

symbolic procedure bcprod (u,v);
%    Base coefficient product. u and v are base coefficients.
%    bcprod(u,v) calculates u*v and returns a base coefficient.
   if !*grmod!* then bcfi(u*v)
    else bcint2op(u,v,'times) or bccheckz multsq(u,v);

symbolic procedure mkbc (u,v);
<<numberp u or f2dip11 u;numberp v or f2dip11 v;
%   Convert u and v into u/v in lowest terms
  if !*grmod!* then bcfi(u * modular!-reciprocal v)
   else if v = 1 then (if u = 1 then ' ( 1 . 1 ) else u ./ v )
   else if minusf v then mkbc(negf u,negf v)
   else quotfx(u,m) ./ quotfx(v,m) where m=gcdf(u,v)>>;

if null getd 'quotientx then copyd('quotientx,'quotient);

symbolic procedure bcquot(u,v);
%    Base coefficient quotient. u and v are base coefficients.
%    bcquot(u,v) calculates u/v and returns a base coefficient.
  if !*grmod!*
    then bcfi(u*modular!-reciprocal v)
   else if !*vdpinteger then
     (bcint2op(u,v,'quotientx) or !*f2q quotfx(numr u,numr v))
   else quotsq(u,v);

symbolic procedure bcsum(u,v);
%    Base coefficient sum. u and v are base coefficients.
%    bcsum(u,v) calculates u+v and returns a base coefficient.
   if !*grmod!* then bcfi(u+v)
    else bcint2op(u,v,'plus2) or bccheckz addsq(u,v);

symbolic procedure bccheckz u;
% Reduce a sum/difference result by members of bczerodivl!*.
   if null numr u then u else if !*bcsubs2 then subs2 u else
 <<while l and n do <<n:=cdr qremf(n,car l);l:=cdr l>>;n./d>>
     where l=bczerodivl!*,n=numr u,d=denr u;

symbolic procedure bcdif(u,v);
%    Base coefficient difference. u and v are base coefficients.
%    bcdif(u,v) calculates u-v and returns a base coefficient.
   if !*grmod!* then bcfi(u - v)
    else bcint2op(u,v,'difference) or bcsum(u,bcneg v);

symbolic procedure bcpow(u,n);
%    Returns the base coefficient u raised to the nth power, where
%    n is an integer
   if !*grmod!* then modular!-expt(u,n) else exptsq(u,n);

symbolic procedure a2bc u;
%   Converts the algebraic (kernel) u into a base coefficient.
   if !*grmod!*
     then if not domainp u then rederr list ( " Invalid coefficient " , u )
           else bcfd u
    else simp!* u;

symbolic procedure bc2a u;
%    Returns the prefix equivalent of the base coefficient u
   if !*grmod!* then u else prepsq u;

fluid'(!*groebigpos !*groebigneg !*groescale);
!*groescale:=20;!*groebigpos:= 10** !*groescale;!*groebigneg:=- 10** !*groescale;

symbolic procedure bcprin u;
%    Prints a base coefficient in infix form
   if !*grmod!* then prin2 u else
   begin scalar nat;
      nat:=!*nat;
      !*nat:=nil;
      if cdr u = 1 and numberp car u and
           (car u>!*groebigpos or car u<!*groebigneg)
          then bcprin2big car u
      else if cdr u neq 1 or not numberp car u then
         <<prin2!* " [ ";sqprint u;prin2!* " ] " >> else sqprint u;
      !*nat:=nat end;

symbolic procedure bcprin2big u;
    <<if u<0 then<< prin2 "-";u:= -u>>;bcprin2big1(u,0)>>;

symbolic procedure bcprin2big1 (u,n);
    if u>!*groebigpos then
             bcprin2big1 (u/!*groebigpos,n#+!*groescale)
      else <<prin2 u;prin2 "e";prin2 n>>;

endmodule;;end;
