module sfbes;  % Procedures and Rules for the Bessel functions.

% Author: Chris Cannam, October 1992.
%
% April 13, 2006 added an improvement proposed by Alain Moussiaux
%
% Firstly, procedures to compute values of the Bessel functions by
% direct bigfloat manipulation; also procedures for large arguments,
% using an asymptotic formula.

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


% These are specific to the Schoepf/Beckingham binary bigfloats, though
% easily adapted, and they should only be used with n and z both
% numeric, real and non-negative.

% Then follow procedures written in algebraic mode and used for certain
% special cases such as complex arguments.  Anybody who wishes to create
% symbolic mode complex-rounded versions is welcome to do so, with my
% blessing.

% No functions are provided to compute bessel K, though for special
% cases the ruleset handles it.


imports complex!*on!*switch, complex!*off!*switch,
   complex!*restore!*switch, sq2bf!*, sf!*eval;


% This module exports no functions.  I want to keep it available only
% through the algebraic operators, largely because the functions are
% quite a complicated lot.  If you want to use it from symbolic mode,
% use a wrapper and use the algebraic operators- it's slower, but at
% least that way you'll get the answers.

global '(logten);

algebraic operator besselJ, besselY, besselI, besselK, hankel1, hankel2;

%%%%%%%%%%%%%%%%%%%%%%%%%besselj%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% an improvement proposed by Alain Moussiaux

algebraic <<
operator !10j;
let {
!10j(1/2,~x) => sqrt(2/(pi*x))*sin(x),
!10j(-1/2,~x) => sqrt(2/(pi*x))*cos(x),
!10j(3/2,~x) => sqrt(2/(pi*x))*(sin(x)/x-cos(x)),
!10j(-3/2,~x) => sqrt(2/(pi*x))*(-cos(x)/x-sin(x)) };
>>;

algebraic procedure cvpr130108(nu,x);
begin
scalar numf,n;
%tout est scalar
%calcul de bessel nu=1/2,3/2,5/2 etc...
%et des bessel nu=-1/2,-3/2,-5/2 etc
%On renvois 100 si pas dans le cas
%bessel(5/2,x)

if nu=1/2 then
<<
%write "Nu demi entier posif =",nu;
return !10j(1/2,x)>>;

if nu=-1/2 then
<<
%write "Nu demi entier negatif =",nu;
return !10j(-1/2,x)>>;

if nu=3/2 then
<<
%write "Nu demi entier posif =",nu;
return !10j(3/2,x)>>;

if nu=-3/2 then
<<
%write "Nu demi entier negatif =",nu;
return !10j(-3/2,x)>>;

if fixp (nu*2) and (2*nu)>0 then
<<
%write "Nu demi entier posif =",nu;
numf:=num nu;

for n:=5 step 2 until numf do
<<
nu:=n/2;
!10j(nu,x):=(1/x)*(2*(nu-1)*!10j(nu-1,x)-x*!10j(nu-2,x));
>>;
%+++++++++++++
return !10j(nu,x);
>> else
if fixp(-2*nu) and 2*nu < 0 then <<
%write "Nu demi entier negatif =",nu;
numf:=-num nu;
%+++++++++++++++++++++++++
for n:=5 step 2 until numf do <<
nu:=-n/2;
!10j(nu,x):=2*(nu+1)*!10j(nu+1,x)/x-!10j(nu+2,x);
>>;
return !10j(nu,x);
>>;
end;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

algebraic <<let besselj(~n,~x) =>  cvpr130108(n,x) when (numberp n and den n = 2 and
                            fixp(num n) and  abs(num n) < 6)>>;

symbolic operator do!*j, do!*y, do!*i;

algebraic (bessel!*rules := {

besselJ(~n,0)  => 1 when n=0,    % We need this form to be sure rules
                                 % are in right order.
besselJ(~n,0)  => 0
   when numberp n and n neq 0,

besselY(~n,0)  =>  infinity,

besselJ(1/2,~z)  =>  sqrt(2/(pi*z)) * sin(z),

besselJ(-1/2,~z)  =>  sqrt(2/(pi*z)) * cos(z),

besselY(-1/2,~z)  =>  sqrt(2/(pi*z)) * sin(z),

besselY(1/2,~z)  =>  - sqrt(2/(pi*z)) * cos(z),

besselK(~n,~z)  =>  sqrt(Pi/(2*z))*e^(-z)
   when (n = 1/2 or n=-1/2),

besselI(1/2,~z)  =>  1/sqrt(Pi*2*z)*(e^z - e^(-z)),

besselI(-1/2,~z)  =>  1/sqrt(pi*2*z)*(e^z + e^(-z)),

% J and Y for negative values and indices.

besselJ(~n,~z)  =>  ((-1)**n) * besselJ(-n,z)
   when numberp n and impart n=0 and n=floor n and n < 0,

besselJ(~n,~z)  =>  ((-1)**n) * besselJ(n,-z)
   when numberp n and impart n=0 and n=floor n
        and numberp z and repart z < 0,

besselY(~n,~z)  =>  ((-1)**n) * besselY(-n,z)
   when numberp n and impart n=0 and n=floor n and n < 0,

besselY(~n,~z)  =>  ((besselJ(n,z)*cos(n*pi))-(besselJ(-n,z)))/sin(n*pi)
   when not symbolic !*rounded
      and numberp n
               and (impart n neq 0 or not (repart n = floor repart n)),

% Hankel functions.

hankel1(~n,~z)  =>  sqrt(2/(pi*z)) * (exp(i*z)/i)
   when symbolic !*complex and n = 1/2,

hankel2(~n,~z)  =>  sqrt(2/(pi*z)) * (exp(-i*z)/(-i))
   when symbolic !*complex and n = 1/2,

hankel1(~n,~z)  =>  besselJ(n,z) + i * besselY(n,z)
   when symbolic !*complex and not symbolic !*rounded,

hankel2(~n,~z)  =>  besselJ(n,z) - i * besselY(n,z)
   when symbolic !*complex and not symbolic !*rounded,


% Modified Bessel functions I and K.

besselI(~n,0)  =>  (if n = 0 then 1 else 0) when numberp n,

besselI(~n,~z)  =>  besselI(-n,z)
   when numberp n and impart n=0 and n=floor n and n < 0,

besselK(~n,~z)  =>  besselK(-n,z)
   when numberp n and impart n=0 and n=floor n and n < 0,

besselK(~n,0)  =>  infinity,

besselK(~n,~z)  =>  (pi/2)*((besselI(-n,z) - besselI(n,z))/(sin(n*pi)))
   when numberp n and impart n = 0 and not (n = floor n),


% Derivatives.

% df(besselJ(~n,~z),z)  =>  -besselJ(1,z) when numberp n and n = 0,
% df(besselY(~n,~z),z)  =>  -besselY(1,z) when numberp n and n = 0,
% df(besselI(~n,~z),z)  =>  besselI(1,z) when numberp n and n = 0,
% df(besselK(~n,~z),z)  =>  -besselK(1,z) when numberp n and n = 0,

% AS (9.1.26 and 27)
df(besselJ(~n,~z),z)  =>  besselJ(n-1,z) - (n/z) * besselJ(n,z),
df(besselY(~n,~z),z)  =>  besselY(n-1,z) - (n/z) * besselY(n,z),
df(BesselK(~n,~z),z)  =>  - BesselK(n-1,z) - (n/z) * BesselK(n,z),
df(hankel1(~n,~z),z)  =>  hankel1(n-1,z) - (n/z) * hankel1(n,z),
df(hankel2(~n,~z),z)  =>  hankel2(n-1,z) - (n/z) * hankel2(n,z),
df(besselI(~n,~z),z)  => (besselI(n-1,z) + besselI(n+1,z)) / 2,


% Sending to be computed

besselJ(~n,~z)  =>  do!*j(n,z)
   when numberp n and numberp z and symbolic !*rounded,

besselY(~n,~z)  =>  do!*y(n,z)
   when numberp n and numberp z and symbolic !*rounded,

besselI(~n,~z)  =>  do!*i(n,z)
   when numberp n and numberp z and symbolic !*rounded

})$

algebraic (let bessel!*rules);


algebraic procedure do!*j(n,z);
   (if impart n = 0 and impart z = 0 and repart z > 0
    then algebraic sf!*eval('j!*calc!*s,{n,z})
    else algebraic sf!*eval('j!*calc,   {n,z}));

algebraic procedure do!*y(n,z);
   (if impart n = 0 and impart z = 0 and n = floor n
    then if repart z < 0
         then algebraic sf!*eval('y!*calc!*sc, {n,z   })
               else algebraic sf!*eval('y!*calc!*s,  {n,z,{}})
    else if impart n neq 0 or n neq floor n
               then y!*reexpress(n,z)
               else algebraic sf!*eval('y!*calc,     {n,z   }));

% What should be the value of BesselY(0,3i)?


algebraic procedure do!*i(n,z);
   (if impart n = 0 and impart z = 0 and repart z > 0
    then algebraic sf!*eval('i!*calc!*s, {n,z})
    else algebraic sf!*eval('i!*calc,    {n,z}));


algebraic procedure j!*calc!*s(n,z);
   begin scalar n0, z0, fkgamnk, result, alglist!*;
      integer prepre, precom;
      precom := complex!*off!*switch();
      prepre := precision 0;
      if z > (2*prepre) and z > 2*n and
               (result := algebraic sf!*eval('asymp!*j!*calc,{n,z})) neq {}
      then
               << precision prepre;
                  complex!*restore!*switch(precom);
                  return result >>;
      if prepre < !!nfpd
      then precision (!!nfpd+3+floor(abs n/10))
      else precision (prepre+6+floor(abs n/10));
      n0 := n; z0 := z;
      fkgamnk := gamma(n+1);
      result :=
               algebraic sf!*eval('j!*calc!*s!*sub,{n0,z0,fkgamnk,prepre});
      precision prepre;
      complex!*restore!*switch(precom);
      return result;
   end;


symbolic procedure j!*calc!*s!*sub(n,z,fkgamnk,prepre);
   begin scalar result, admissable, this,
               modify, fkgamnk, zfsq, zfsqp, knk, azfsq, k;
      n := sq2bf!* n; z := sq2bf!* z;
      fkgamnk := sq2bf!* fkgamnk;
      modify := exp!:(timbf(log!:(divbf(z,bftwo!*),
               c!:prec!:()+2),n), c!:prec!:());
                     % modify := ((z/2)**n);
      zfsq := minus!:(divbf(timbf(z,z),i2bf!: 4));
                     % zfsq := (-(z**2)/4);
      azfsq := abs!: zfsq;
      result := divbf(bfone!*, fkgamnk);
      k := bfone!*; zfsqp := zfsq;
      fkgamnk := timbf(fkgamnk, plubf(n,bfone!*));

      if lessp!:(abs!: result, bfone!*) then
               admissable := abs!: divbf (bfone!*,
                  timbf (exp!:(timbf(fl2bf logten, i2bf!:(prepre +
                                  length explode fkgamnk)), 8), modify))
      else
               admissable := abs!: divbf (bfone!*,
                  timbf (exp!:(timbf(fl2bf logten, i2bf!:(prepre +
                                  length explode (1 + conv!:bf2i abs!: result))), 8),
                     modify));

      this := plubf(admissable, bfone!*);

      while greaterp!:(abs!: this, admissable) do
               << this := divbf(zfsqp, fkgamnk);
                  result := plubf (result, this);
                  k := plubf(k,bfone!*);
                  knk := timbf (k, plubf(n, k));
                  if greaterp!: (azfsq, knk) then
                     precision (precision(0) +
                     length explode(1 + conv!:bf2i divbf (azfsq, knk)));
                  zfsqp := timbf(zfsqp,zfsq);
                  fkgamnk := timbf(fkgamnk,knk) >>;
      result := timbf(result,modify);

      return mk!*sq !*f2q mkround result;
   end;

flag('(j!*calc!*s!*sub), 'opfn);


algebraic procedure asymp!*j!*calc(n,z);
   begin scalar result, admissable, alglist!*,
               modify, chi, mu, p, q, n0, z0;
      integer prepre;
      prepre := precision 0;
      if prepre < !!nfpd
      then precision (!!nfpd + 5)
      else precision (prepre+8);
      modify := sqrt(2/(pi*z));
      admissable := 1 / (10 ** (prepre + 5));
      chi := z - (n/2 + 1/4)*pi;
      mu := 4*(n**2);
      n0 := n; z0 := z;
      p := algebraic symbolic asymp!*p(n0,z0,mu,admissable);
      if p neq {} then
               << q := algebraic symbolic asymp!*q(n0,z0,mu,admissable);
                  if q neq {} then
               result := modify*(first p * cos chi - first q * sin chi)
                  else result := {} >>
      else result := {};
      precision prepre;
      return result;
   end;


algebraic procedure asymp!*y!*calc(n,z);
   begin scalar result, admissable, alglist!*,
               modify, chi, mu, p, q, n0, z0;
      integer prepre;
      prepre := precision 0;
      if prepre < !!nfpd
      then precision (!!nfpd + 5)
      else precision (prepre+8);
      modify := sqrt(2/(pi*z));
      admissable := 1 / (10 ** (prepre + 5));
      chi := z - (n/2 + 1/4)*pi;
      mu := 4*(n**2);
      n0 := n; z0 := z;
      p := algebraic symbolic asymp!*p(n0,z0,mu,admissable);
      if p neq {} then
               << q := algebraic symbolic asymp!*q(n0,z0,mu,admissable);
                  if q neq {} then
               result := modify*(first p * sin chi + first q * cos chi)
                  else result := {} >>
      else result := {};
      precision prepre;
      return result;
   end;


symbolic procedure asymp!*p(n,z,mu,admissable);
   begin scalar result, this, prev, zsq, zsqp, aj2t;
      integer k, f;
      n := sq2bf!* n; z := sq2bf!* z; mu := sq2bf!* mu;
      admissable := sq2bf!* admissable;
      k := 2; f := 1 + conv!:bf2i
               difbf(divbf(n,bftwo!*),divbf(bfone!*,i2bf!: 4));
      this := plubf(admissable, bfone!*);
      result := bfone!*;
      aj2t := asymp!*j!*2term(2, mu);
      zsq := timbf(i2bf!: 4, timbf(z, z));
      zsqp := zsq;
      while greaterp!:(abs!: this, admissable) do
               << prev := abs!: this;
                  this := timbf(i2bf!: ((-1)**(k/2)), divbf(aj2t, zsqp));
                  if greaterp!: (abs!: this, prev) and (k > f)
                  then result := this := bfz!*
                  else
                     << result := plubf(result, this);
                               zsqp := timbf(zsqp, zsq);
                               k := k + 2;
                               aj2t := timbf(aj2t, asymp!*j!*2term!*modifier(k, mu))
                     >> >>;
      if result = bfz!* then return '(list)
      else return list('list, mk!*sq !*f2q mkround result);
   end;


symbolic procedure asymp!*q(n,z,mu,admissable);
   begin scalar result, this, prev, zsq, zsqp, aj2t;
      integer k, f;
      n := sq2bf!* n; z := sq2bf!* z; mu := sq2bf!* mu;
      admissable := sq2bf!* admissable;
      k := 1; f := 1 + conv!:bf2i
               difbf(divbf(n,bftwo!*),divbf(i2bf!: 3, i2bf!: 4));
      this := plubf(admissable, bfone!*);
      result := bfz!*;
      aj2t := asymp!*j!*2term(1, mu);
      zsq := timbf(i2bf!: 4, timbf(z, z));
      zsqp := timbf(bftwo!*, z);
      while greaterp!:(abs!: this, admissable) do
               << prev := abs!: this;
                  this := timbf(i2bf!: ((-1)**((k-1)/2)), divbf(aj2t, zsqp));
                  if greaterp!: (abs!: this, prev) and (k > f)
                  then result := this := bfz!*
                  else
                     << result := plubf(result, this);
                               zsqp := timbf(zsqp, zsq);
                               k := k + 2;
                               aj2t := timbf(aj2t, asymp!*j!*2term!*modifier(k, mu))
                     >> >>;
      if result = bfz!* then return '(list)
      else return list('list, mk!*sq !*f2q mkround result);
   end;


symbolic procedure asymp!*j!*2term(k, mu);
   begin scalar result;
      result := bfone!*;
      for j := 1 step 2 until (2*k - 1) do
               result := timbf(result, difbf(mu, i2bf!: (j**2)));
      result := divbf (result, i2bf!: (factorial k * (2**(2*k))));
      return result;
   end;


symbolic procedure asymp!*j!*2term!*modifier(k, mu);
    (timbf (difbf(mu, i2bf!: ((2*k-3)**2)),
               divbf (difbf(mu, i2bf!: ((2*k-1)**2)),
                  i2bf!: ((k-1) * k * 16))));



algebraic procedure y!*calc!*s(n,z,st);
   begin scalar n0, z0, st0, ps, fkgamnk, result, alglist!*;
      integer prepre, precom;
      precom := complex!*off!*switch();
      prepre := precision 0;
      if z > (2*prepre) and z > 2*n and
               (result := asymp!*y!*calc(n,z)) neq {}
      then
               << precision prepre;
                  complex!*restore!*switch(precom);
                  return result >>;
      if prepre < !!nfpd then precision (!!nfpd+5)
      else precision (prepre + 8);
      n0 := n; z0 := z; st0 := st;
      ps := psi 1 + psi(1+n);
      fkgamnk := gamma(n+1);
      result :=
        algebraic symbolic y!*calc!*s!*sub(n0,z0,ps,fkgamnk,prepre,st0);
      precision prepre;
      complex!*restore!*switch(precom);
      return result;
   end;


% The last arg to the next procedure is an algebraic list of the
% modifier, start value and (factorial n) for the series.  If this is
% (LIST) (i.e. the nil algebraic list {}), the values will be computed
% in this procedure; otherwise the values in st0 will be used.  This
% feature is used for decomposition of the computation of y at negative
% real z.  It is of course designed to make the code as hard to follow
% as possible.  Why else?

% n must be a non-negative integer for this next procedure to work.

symbolic procedure y!*calc!*s!*sub(n,z,ps,fkgamnk,prepre, st0);
   begin scalar start, result, this, ps, fc, modify,
         zfsq, zfsqp, nps, azfsq, bj, z0, n0, tpi, admissable;
      integer k, fk, fnk, difd, fcp;
      z0 := z;
      z := sq2bf!* z; ps := sq2bf!* ps;
      n := sq2bf!* n; n0 := conv!:bf2i n;
      tpi := pi!*();

      if st0 = '(LIST) then
         << modify := divbf(exp!:
                  (timbf(n, log!:(divbf(z, bftwo!*), c!:prec!:()+2)),
                     c!:prec!:()), tpi);
            bj := retag cdr !*a2f
               sf!*eval('j!*calc!*s!*sub,
                  list('list,n0,z0,fkgamnk,prepre));

            if n0 < 1 then
               << start := timbf(timbf(divbf(bftwo!*,tpi),
                        log!:(divbf(z,bftwo!*),c!:prec!:()+1)), bj);
                  fc := factorial n0 >>

            else if (n0 < 100) then
               << start := bfz!*;
                  zfsq := divbf(timbf(z,z), i2bf!: 4);
                  for k := 0:(n0-1) do
                     start := plubf(start, divbf
                        (exptbf(zfsq, k, i2bf!: factorial (n0-k-1)),
                           i2bf!: factorial k));
                  start := minus!: timbf(start, divbf(exp!:
                     (timbf(minus!: n, log!:(divbf(z, bftwo!*),
                        c!:prec!:()+2)), c!:prec!:()), tpi));
                  start := plubf (start,
                        timbf(timbf(divbf(bftwo!*,tpi),bj),
                     log!:(divbf(z,bftwo!*), c!:prec!:()+2)));
                  fc := factorial n0 >>

            else
               << zfsq := divbf(timbf(z,z), i2bf!: 4); zfsqp := bfone!*;
                  fk := 1; fnk := factorial (n0-1); fc := fnk * n0;
                  start := bfz!*;
                  for k := 0:(n0-2) do
                     << start := plubf(start,
                           timbf(i2bf!: fnk, divbf(zfsqp, i2bf!: fk)));
                        fk := fk * (k+1);
                        fnk := fnk / (n0-k-1);
                        zfsqp := timbf(zfsqp, zfsq) >>;
                  start := plubf(start,
                     timbf(i2bf!: fnk, divbf(zfsqp, i2bf!: fk)));
                  start := minus!: plubf(timbf(start,
                        divbf(bfone!*,timbf(modify,timbf(tpi,tpi)))),
                     timbf(timbf(divbf(bftwo!*,tpi), bj),
                        log!:(divbf(z,bftwo!*),c!:prec!:()+2))) >> >>
      else
         << start := sq2bf!* cadr st0;
            modify := sq2bf!* caddr st0;
            fc := cadddr st0 >>;

      zfsq := minus!: divbf(timbf(z,z),i2bf!: 4);
      azfsq := abs!: zfsq;
      result := divbf(ps, i2bf!: fc);
      k := 1; zfsqp := zfsq; fc := fc * (n0+1);
      ps := plubf(ps,plubf(bfone!*,divbf(bfone!*,plubf(n,bfone!*))));


% Note: we are assuming numberp start.  Be sure to catch other cases
% elsewhere.  (Notably for z < 0).  This goes for bessel J as well.

      if lessp!: (abs!: plubf(result, start), bfone!*) then
         admissable := abs!: divbf(divbf(bfone!*,
               exp!:(timbf(fl2bf logten, plubf(i2bf!:(prepre+2),
                  divbf(log!:(divbf(bfone!*,
                        plubf(abs!: result, abs!: start)), 5),
                     fl2bf logten))), 5)), modify)
      else admissable := abs!: divbf(divbf(bfone!*,
               exp!:(timbf(fl2bf logten, plubf(i2bf!:(prepre+2),
                  divbf(log!:(plubf(abs!: result, abs!: start), 5),
                     fl2bf logten))), 5)), modify);

      this := plubf(admissable, bfone!*);

      while greaterp!: (abs!: this, admissable) do
         << this := timbf(ps, divbf(zfsqp, i2bf!: fc));
            result := plubf(result, this);
            k := k + 1; zfsqp := timbf(zfsqp, zfsq);
            nps := plubf(ps,
               plubf(divbf(bfone!*,i2bf!: k),
                  divbf(bfone!*,i2bf!:(k+n0))));
            fcp := k * (n0+k);
            if greaterp!:(timbf(nps,azfsq),timbf(ps,i2bf!: fcp)) then
               << difd := 1 + conv!:bf2i
                     divbf(timbf(nps,azfsq),timbf(ps,i2bf!: fcp));
                  precision (precision(0) + length explode difd) >>;
            fc := fc * fcp;
            ps := nps >>;

      result := difbf(start, timbf(result, modify));
      return mk!*sq !*f2q mkround result;
   end;

algebraic procedure i!*calc!*s(n,z);
   begin scalar n0, z0, ps, fkgamnk, result, alglist!*;
      integer prepre, precom;
      precom := complex!*off!*switch();
      prepre := precision 0;
      if prepre < !!nfpd
      then precision (!!nfpd+3+floor(abs n/10))
      else precision (prepre+8+floor(abs n/10));
      n0 := n; z0 := z;
      fkgamnk := gamma(n+1);
      result :=
               algebraic symbolic i!*calc!*s!*sub(n0,z0,fkgamnk,prepre);
      precision prepre;
      complex!*restore!*switch(precom);
      return result;
   end;


symbolic procedure i!*calc!*s!*sub(n,z,fkgamnk,prepre);
   begin scalar result, admissable, this,
               modify, fkgamnk, zfsq, zfsqp, knk, azfsq, k;

      n := sq2bf!* n; z := sq2bf!* z;
      fkgamnk := sq2bf!* fkgamnk;

      modify := exp!:(timbf(log!:(divbf(z,bftwo!*),
               c!:prec!:()+2),n), c!:prec!:());
                     % modify := ((z/2)**n);
      zfsq := divbf(timbf(z,z),i2bf!:(4));
                     % zfsq := (-(z**2)/4);
      azfsq := abs!: zfsq;
      result := divbf(bfone!*, fkgamnk);
      k := bfone!*; zfsqp := zfsq;
      fkgamnk := timbf(fkgamnk, plubf(n,bfone!*));

      if lessp!:(abs!: result, bfone!*) then
               admissable := abs!: divbf (bfone!*,
                  timbf (exp!:(timbf(fl2bf logten, i2bf!:(prepre +
                                  length explode fkgamnk)), 8), modify))
      else
               admissable := abs!: divbf (bfone!*,
                  timbf (exp!:(timbf(fl2bf logten, i2bf!:(prepre +
                                  length explode (1 + conv!:bf2i abs!: result))),
                               8),
                     modify));

      this := plubf(admissable, bfone!*);

      while greaterp!:(abs!: this, admissable) do
               << this := divbf(zfsqp, fkgamnk);
                  result := plubf (result, this);
                  k := plubf(k,bfone!*);
                  knk := timbf (k, plubf(n, k));
                  if greaterp!: (azfsq, knk) then
                     precision (precision(0) +
                               length explode (1 + conv!:bf2i divbf (azfsq, knk)));
                  zfsqp := timbf(zfsqp, zfsq);
                  fkgamnk := timbf(fkgamnk, knk) >>;
      result := timbf(result, modify);

      return mk!*sq !*f2q mkround result;
   end;


%
% algebraic procedure j!*calc(n,z);
%
%  Given integer n and arbitrary (I hope) z, compute and return
%  the value of the Bessel J-function, order n, at z.  Current
%  version mostly coded for speed rather than clarity.
%
%  Does work for non-integral n.
%

algebraic procedure j!*calc(n,z);
   begin scalar result, admissable, this, alglist!*,
        modify, fkgamnk, zfsq, zfsqp, azfsq, knk; % bind alglist!* to
      integer prepre, k, difd;       % stop global alglist being cleared
      prepre := precision 0;

% Don't need to check if asymptotic expansion is valid;
% if we're using this routine, it's not appropriate anyway.
%      if z > (2*prepre) and z > 2*n and
%              (result := asymp!*j!*calc(n,z)) neq {}
%      then return result;

      precision (prepre + 4);
      modify := ((z/2) ** n);
      zfsq := (-(z**2)/4); azfsq := abs zfsq;
      fkgamnk := gamma(n+1);
      result := (1 / (fkgamnk));
      k := 1; zfsqp := zfsq; fkgamnk := fkgamnk * (n+1);
      if numberp modify and impart modify = 0 then
               if (abs result) < 1 then
                  << difd := ceiling (1/abs result);
                     admissable := abs ((1 / (10 ** (prepre +
                               (symbolic length explode difd)))) / modify) >>
               else
                  << difd := ceiling abs result;
                     admissable := abs ((1 / (10 ** (prepre -
                               (symbolic length explode difd)))) / modify) >>
      else
               if (abs result) < 1 then
                  << difd := ceiling (1/abs result);
                     admissable := abs (1 / (10 ** (prepre + 10 +
                               (symbolic length explode difd)))) >>
               else
                  << difd := ceiling abs result;
                     admissable := abs (1 / (10 ** (prepre + 10 -
                               (symbolic length explode difd)))) >>;
      this := admissable + 1;

      while (abs this > admissable) do
               << this := (zfsqp / (fkgamnk));
                  result := result + this;
                  k := k + 1;                    % Maintain k as term counter,
                  knk := k * (n+k);
                  if azfsq > knk then
               <<difd := ceiling (azfsq / knk);
                 precision(precision(0)+(lisp length explode difd))>>;
            zfsqp := zfsqp * zfsq;      % zfsqp as ((-(z**2)/4)**k), and
                  fkgamnk := fkgamnk * knk >>;
                                        % fkgamnk as k! * gamma(n+k+1).
      result := result * modify;
      precision prepre;

      return result;
   end;



%
% Procedure to compute (modified) start value for
% Bessel Y computations. Also used to get imaginary
% part for certain values
%

algebraic procedure y!*modifier!*calc(n,z);
   begin scalar modify, start, zfsq, zfsqp, fc;
      integer fk, fnk, prepre;
      prepre := precision 0;
%      if prepre < !!nfpd then precision (!!nfpd + 2)
%      else precision (prepre + 2);
      modify := ((z/2)**n) / pi;

      %  Simple expression for start value when n<1.
      if (n < 1) then
               << start := ((2/pi) * log(z/2) * besselJ(n,z));
                  fc := factorial n >>

      %  If n smallish, just sum using factorials. (REDUCE
      %  seems to do smallish factorials quite quickly. In
      %  fact it does largish factorials quite quickly as well,
      %  but not quite as quickly as we can build them by
      %  per-term multiplication.)
      else if (n < 100) then
               << start := - (((z/2) ** (-n)) / pi) *
                     (for k := 0:(n-1) sum
                               ((factorial (n-k-1) * (((z**2)/4) ** k)) /
                                 (factorial k))) + ((2/pi)*log(z/2)*besselJ(n,z));
                  fc := factorial n >>

      %  If n largish, avoid computing factorials, and try
      %  to do the minimum possible real work.
      else
               << zfsq := (z**2)/4; zfsqp := 1;
                  fk := 1; fnk := factorial (n-1); fc := fnk * n;
                  start := 0;
                  for k := 0:(n-2) do
                     << start := start + (fnk * zfsqp / fk);
                               fk := fk * (k+1);
                               fnk := floor(fnk/(n-k-1));
                               zfsqp := zfsqp * zfsq >>;
                  start := start + (fnk * zfsqp / fk);
                  start := - ((1/(modify*(pi**2)))*start)+
                     ((2/pi)*log(z/2)*besselJ(n,z)) >>;

      precision prepre;
      return {start, modify, fc};
   end;


%
% algebraic procedure y!*calc(n,z);
%
%  Given integer n and arbitrary (I hope) z, compute and return
%  the value of the Bessel Y-function, order n, at z.  Current
%  version mostly coded for speed rather than clarity.
%
%  Owing to its dependence upon factorials, doesn't work for
%  non-integral n.  (But in any case it'd be very slow, particularly
%  for large non-integral n.)
%

algebraic procedure y!*calc(n,z);
   begin scalar start, result, this, ps, fc, smf,
               modify, zfsq, zfsqp, alglist!*, nps, azfsq;
      integer prepre, k, fk, fnk, difd, fcp;
      prepre := precision(0);

      precision (prepre + 8);
      smf := y!*modifier!*calc (n,z);
      start := first smf;
      modify := second smf;
      fc := third smf;

      %  Now we have the starting value: prepare the loop for
      %  the remaining terms. k will be our loop counter. p1
      %  will hold psi(k+1), and p2 psi(k+n+1); zfsqp is
      %  maintained at ((-(z**2)/4)**k); fc is k! * (n+k)!.
      %  The sum is of (p1 + p2) * zfsqp / fc, and we
      %  precompute the first term in order to get an idea
      %  of the general magnitude (it's a decreasing series).
      ps := psi 1 + psi(1+n);
      zfsq := (-(z**2)/4); azfsq := abs zfsq;
      result := ps / fc;
      k := 1; zfsqp := zfsq; fc := fc * (n+1);
      ps := ps + 1 + (1/(n+1));

      %  Having the first term and start, we check whether
      %  they're small or large and modify the maximum
      %  acceptable error accordingly.
      if numberp start then if (abs (result + start)) < 1 then
            admissable := abs ((1 / (10 **
               (prepre+2 + log10(1/(abs result + abs start)))))/modify)
               else admissable := abs ((1 / (10 ** (prepre + 2))) *
                      (log10(abs result + abs start)) / modify)
      else admissable := abs (1 / (10 ** (prepre + 10)));
      this := admissable + 1;

      %  Now sum the series.
      while ((abs this) > admissable) do
               << this := ps * (zfsqp / fc);
                  result := result + this;
                  k := k + 1; zfsqp := zfsqp * zfsq;
                  nps := ps + (1/k) + (1/(k+n));
                  fcp := k * (n+k);
                  if (nps*azfsq) > (ps*fcp) then
               <<difd := ceiling ((nps*azfsq)/(ps*fcp));
                 precision(precision(0) + (lisp length explode difd))>>;
                  fc := fc * fcp;                % fc ends up as k! * (n+k)!
                  ps := nps >>;

      %  Amalgamate the start value and modification, and
      %  return the answer.
      result := start - (result * modify);
      precision prepre;
      return result;
   end;




%
% algebraic procedure i!*calc(n,z);
%
%  Given integer n and arbitrary (I hope) z, compute and return
%  the value of the (modified) Bessel I-function, order n, at z.
%  Current version mostly coded for speed rather than clarity.
%
%  Does work for non-integral n.
%

algebraic procedure i!*calc(n,z);
   begin scalar result, admissable, this, prev, nprev, alglist!*,
         modify, fkgamnk, zfsq, zfsqp, knk;  % bind alglist!* to prevent
      integer prepre, k, difd;            % global alglist being cleared
      modify := ((z/2) ** n);
      prepre := precision 0;
      precision (prepre + 4);
      zfsq := (z**2)/4; azfsq := abs zfsq;
      fkgamnk := gamma(n+1);
      result := (1 / (fkgamnk));
      k := 1; zfsqp := zfsq; fkgamnk := fkgamnk * (n+1);
      if numberp modify then
               if (abs result) < 1 then
                  << difd := ceiling (1/abs result);
                     admissable := abs ((1 / (10 ** (prepre +
                               (symbolic length explode difd)))) / modify) >>
               else
                  << difd := ceiling abs result;
                     admissable := abs ((1 / (10 ** (prepre -
                               (symbolic length explode difd)))) / modify) >>
      else
               if (abs result) < 1 then
                  << difd := ceiling (1/abs result);
                     admissable := abs (1 / (10 ** (prepre + 10 +
                               (symbolic length explode difd)))) >>
               else
                  << difd := ceiling abs result;
                     admissable := abs (1 / (10 ** (prepre + 10 -
                               (symbolic length explode difd)))) >>;
      this := admissable + 1; nprev := abs this;
      while (abs this > admissable) do
               << this := (zfsqp / (fkgamnk));
                  result := result + this;
                  k := k + 1;                    % Maintain k as term counter,
                  knk := k * (n+k);
                  if azfsq > knk then
               <<difd := ceiling (azfsq / knk);
                 precision(precision(0) + (lisp length explode difd))>>;
            zfsqp := zfsqp * zfsq;      % zfsqp as ((-(z**2)/4)**k), and
                  fkgamnk := fkgamnk * knk >>;
                                        % fkgamnk as k! * gamma(n+k+1).
      result := result * modify;
      precision prepre;
      return result;
   end;



algebraic procedure k!*calc!*2(n,z);
   begin scalar result, precom;
      integer prepre;
      prepre := precision 0;
      precision (prepre + 8);
      precom := complex!*on!*switch();
      result := (pi/2)*i*exp((pi/2)*n*i)*hankel1(n,z*exp((pi/2)*i));
      complex!*restore!*switch(precom);
      precision prepre;
      return result;
   end;



%
% Function which simply rewrites bessely (with nonintegral
% order) in terms of besselj.  Turns off rounded mode to
% do so, because if rounded is on, cos(n*pi) =/= 0 for
% n*2 = floor (n*2), which can lead to some spectacular
% inaccuracies.
%

algebraic procedure y!*reexpress(n,z);
   begin scalar result, premsg;
      premsg := lisp !*msg;
      off msg;
      off rounded;
      result := ((besselJ(n,z)*cos(n*pi))-(besselJ(-n,z)))/sin(n*pi);
      on rounded;
      if premsg then on msg;
      return result;
   end;



%
% Function to make an evil blend of the symbolic and
% algebraic mode bessel-y functions where the order
% is real and the arg is real and negative.  Here the
% result will be complex (probably), but most of the
% computations involved will be with real numbers so
% the symbolic mode version will do them better.
%
% Therefore this routine, which gets the modifier
% and initial terms (the only complex bits) from the
% algebraic procedure and then gets the rest from the
% symbolic one.
%

algebraic procedure y!*calc!*sc(n,z);
   begin scalar st, ic, rc, md, fc, result, precom, prepre;
      prepre := precision 0; z := -z;
      if prepre < !!nfpd then precision (!!nfpd + 2)
      else precision (prepre + 4);
      st := y!*modifier!*calc(n,z);
      rc := - first st;
      precom := complex!*on!*switch();
      ic := impart(log(-pi/2));
      complex!*restore!*switch(precom);
      ic := ic*(2/pi)*besselj(n,-z);
      md := - second st; fc := third st;
      precision prepre;
      precom := complex!*off!*switch();
      result := y!*calc!*s(n,z,{rc,md,fc});
      complex!*restore!*switch(precom);
      if symbolic !*complex
      then result := result + i * ic
      else result := (if ic < 0 then 1 else -1) *
                  sqrt(-(ic**2)) + result;
      return result;
   end;

endmodule;

end;

