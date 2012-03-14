% Tests of REDUCE Arithmetic.

% Authors: Anthony C. Hearn and Stanley L. Kameny.

% Copyright (c) 1987, 1988, 1989, 1991, Stanley L. Kameny.
% Copyright (c) 1998, Anthony C. Hearn.
% All Rights Reserved.

% This test file is a combination of three tests files from versions
% 3.6 or earlier: math, rounded and complex.


% Simple tests of rounded arithmetic.

% Tests in the exact mode.

x := 1/2;

y := x + 0.7;

% Tests in approximate mode.

on rounded;

y;   % As expected not converted to approximate form.

z := y+1.2;

z/3;

% Let's raise this to a high power.

ws^24;

% Now a high exponent value.

% 10.2^821;

% Elementary function evaluation.

cos(pi);

symbolic ws;

z := sin(pi);

symbolic ws;

% Handling very small quantities.

% With normal defaults, underflows are converted to 0.

exp(-100000.1**2);

% However, if you really want that small number, roundbf can be used.

on roundbf;

exp(-100000.1**2);

off roundbf;

% Now let us evaluate pi.

pi;

% Let us try a higher precision.

prec0 := precision 50;

pi;

% Now find the cosine of pi/6.

cos(ws/6);

% This should be the sqrt(3)/2.

ws**2;


% Here are some well known examples which show the power of this system.

precision 10;

% This should give the usual default again.

let xx=e**(pi*sqrt(163));

let yy=1-2*cos((6*log(2)+log(10005))/sqrt(163));

% First notice that xx looks like an integer.

xx;

% And that yy looks like zero.

yy;

% But of course it's an illusion.

precision 50;

xx;

yy;

% Now let's look at an unusual way of finding an old friend.

procedure agm;
   begin scalar a,b,u,x,y,p,pn;
      a := 1; b := 1/sqrt 2; u:= 1/4; x := 1$ pn := 4;
      repeat <<p := pn;
	       y := a; a := (a+b)/2; b := sqrt(y*b); % Arith-geom mean.
	       u := u-x*(a-y)**2; x := 2*x; pn := a**2/u;
	       write "pn=",pn>>
	 until pn>=p;
      return p
   end;

agm();

% The limit is obviously.

pi;

off rounded;

clear x;

precision prec0;


% Tests of Complex arithmetic.

on complex;

(31+i)/74;

ws/(b+1);  % This now comes out right!

w:=(x+3*i)**2;

on gcd;

(x**3-7*x**2+x-7)/(x**2+(3+i)*x+3*i);

off gcd;

sqrt(x**4+14*i*x**3-51*x**2-14*i*x+1);

% All rounded tests are done twice:  first, they are done at the default
% precision, in which all rounded operations use standard floating point
% logic.  Then precision is increased, causing all rounded operations to
% use extended precision bigfloat arithmetic.  This is necessary to
% exercise and test the bigfloat-based arithmetic functions.

prec0 := precision 0;  % To determine the nominal default precision.

% Tests using default precision.

on rounded;

(3.25 + 8.5i) + (6.75 - 8.5i);

(3.25 + 8.5i) - (6.0 - 9.5i);

(1.0 + 10.0*i)*(-6.5 + 2.5*i);

(1.2 - 3.4*i)*(-5.6 + 7.8*i);

(19.8 + 28.4*i)/(-5.6 + 7.8*i);

e;

pi;

17*i**2;

(-7.0 + 24.0*i)**(1/2);

sqrt(-7.0 + 24.0*i);

sqrt(-10.12 - 8.16*i);

sin(0.0 + 0.0*i);

sin(1.0 + 0.0*i);

sin(1.0 + 1.0*i);

cos(0.0 + 0.0*i);

cos(1.0 - 0.0*i);

cos(1.0 + 1.0*i);

tan(0.0 + 0.0*i);

tan(1.0 + 0.0*i);

tan(1.0 + 1.0*i);

asin(1.0 + 1.0*i);

acos(1.0 + 1.0*i);

atan(1.0 + 1.0*i);

log(1.0 + 1.0*i);

asin 2;

sin ws;

acos 2;

cos ws;

atan(1+i);

tan ws;

log(2+i);

exp ws;

e**(i*pi);

e**i;

z := sqrt i;

z**2;

off rounded;

%-----------------end of normal floating point tests--------------------

precision(prec0+6); % Arbitrary precision increase -> bigfloat functions

%----------------------start of bigfloat tests--------------------------

on rounded;

(3.25 + 8.5i) + (6.75 - 8.5i);

(3.25 + 8.5i) - (6.0 - 9.5i);

(1.0 + 10.0*i)*(-6.5 + 2.5*i);

(1.2 - 3.4*i)*(-5.6 + 7.8*i);

(19.8 + 28.4*i)/(-5.6 + 7.8*i);

e;

pi;

17*i**2;

(-7.0 + 24.0*i)**(1/2);

sqrt(-7.0 + 24.0*i);

sqrt(-10.12 - 8.16*i);

sin(0.0 + 0.0*i);

sin(1.0 + 0.0*i);

sin(1.0 + 1.0*i);

cos(0.0 + 0.0*i);

cos(1.0 - 0.0*i);

cos(1.0 + 1.0*i);

tan(0.0 + 0.0*i);

tan(1.0 + 0.0*i);

tan(1.0 + 1.0*i);

asin(1.0 + 1.0*i);

acos(1.0 + 1.0*i);

atan(1.0 + 1.0*i);

log(1.0 + 1.0*i);

asin 2;

sin ws;

acos 2;

cos ws;

atan(1+i);

tan ws;

log(2+i);

exp ws;

e**(i*pi);

e**i;

z := sqrt i;

z**2;

off rounded;

% ---------------------------------------------------------------------

% The following examples are independent of precision.

precision prec0; % Restores default precision.

s:= 1.1+2.3i;

s/4;  % This would have had a common factor of 4.

x:= a+1.1+2.3i;

y:= b+1.2+1.3i;

z:= x/y;

z/4;  % This would have had a common polynomial factor b^2 + ...

z*7/4;

s/(c^2+c+1);  % This would have had a common factor of c^2+c+1,

clear x;

zz:= x^2+(1.1+2.3i)*x+1.2+1.3i;

ss:=1.23456789x^2+1.3579i*x+5.6789;

z:= x+1.1+2.3i;

on rationalize;

z;               % Same as previous answer.

off rationalize;

1.23456789x^2+2.3456i*x+7.89;

on factor;

x**2+1;

x**4-1;

x**4+(i+2)*x**3+(2*i+5)*x**2+(2*i+6)*x+6;

(2*i+3)*x**4+(3*i-2)*x**3-2*(i+1)*x**2+i*x-1;

% Multivariate examples.

x**2+y**2;

off factor;

factorize(x**2+1);

off complex;


% Tests of some elementary functions.

comment Integer functions that work in all domain modes, independent of
switch NUMVAL, so long as their arguments evaluate to real numbers.

Functions of one argument:
FIX, SGN, ROUND, CEILING, FLOOR

(The following functions are available only in symbolic mode, so they
 are not tested here: ISQRT, ICBRT, ILOG2, IROOTN);

fix a;  % Will be evaluated only if a evaluates to a real number.

a := 27/4;

fix a;

fix 12.345;

sign (-15/2);

round 12.5;

ceiling 12.5;

floor 12.5;

% isqrt 12.5;

% icbrt 12.5;

% ilog2 130.7;

% irootn(72,4);

% irootn(72,3/2); % This will not evaluate.


comment   Functions which require arguments which evaluate to integers.

Function of one argument:  FACTORIAL

Function of two arguments:  PERM, CHOOSE;$

factorial 10;

perm(5,10);  % Permutations of 5 out of 10.

choose(5,10);  % Choose 5 out of 10.


comment

These functions are evaluated in dmodes ROUNDED and COMPLEX-ROUNDED
(ON ROUNDED,COMPLEX) so long as their arguments and values evaluate
to real numbers and NUMVAL (normally ON) is ON.

Variable treated as function of no arguments:  E, PI.

Functions of one argument:
EXP, LOG, LN, LOG10, NORM, ARG, SQRT,
RAD2DEG, RAD2DMS, DEG2RAD, DEG2DMS, DMS2DEG, DMS2RAD,
SIN, ASIN, COS, ACOS, TAN, ATAN, COT, ACOT, SEC, ASEC, CSC, ACSC,
SINH, ASINH, COSH, ACOSH, TANH, ATANH, COTH, ACOTH, SECH, ASECH,
CSCH, ACSCH.

Functions of two arguments:
EXPT, LOGB, HYPOT, ATAN2.

Function evaluation is carried out to the precision specified in the
latest PRECISION statement.

(The following functions are available only in symbolic mode, so they
 are not tested here:
  SIND, ASIND, COSD, ACOSD, TAND, ATAND, COTD, ACOTD, SECD, ASECD,
  CSCD, ACSCD, ATAN2D, CBRT);

on rounded; precision 6;

a := exp 3;

log a;

ln a;

log10 1000;

norm (-12.345);  % For real x, this is equivalent to ABS x.

arg (-12.345);  % For real x, this -> if x<0 then pi else 0.0.

sqrt 3;

ws**2;

deg2rad 30;

rad2deg ws;

a := deg2dms 12.345; % a will be a list.

dms2deg ws;

dms2rad a;

rad2deg ws;

asin 0.5;

sin ws;

acos 0.5;

cos ws;

atan 0.5;

tan ws;

acot 0.5;

cot ws;

asec 3;

sec ws;

acsc 3;

csc ws;

asinh 0.5;

sinh ws;

acosh 2;

cosh ws;

atanh 0.5;

tanh ws;

acoth 2;

coth ws;

sech 1;

asech ws;

csch 1;

acsch ws;

expt(2,1.234);

logb(ws,2);

hypot(3,4);

a := -3*pi/4; % Any  -pi<a<=pi should work.

atan2(sin a,cos a);

ws - a;  % Should be 0.

precision 20;  % Functions will be computed to 20 places.

sin 1.5;

asin ws;

precision 50;  % Functions computed to 50 places.

sin 1.5;

asin ws;

precision 6;

comment   If argument or value are complex, functions are not computed
when dmode is ROUNDED; $

sin(1+i);  % Complex argument.

asin 2;  % Value would be complex.

on complex; % Now complex arguments and complex results will be handled.

comment   Complex functions of one argument:
EXP, LOG, NORM, ARG, SQRT,
SIN, ASIN, COS, ACOS, TAN, ATAN, COT, ACOT, SEC, ASEC, CSC, ACSC,
SINH, ASINH, COSH. ACOSH, TANH, ATANH, COTH, ACOTH, SECH, ASECH,
CSCH, ACSCH.
(The following functions are available only in symbolic mode, so they
 are not tested here:
  SIND, ASIND, COSD, ACOSD, TAND, ATAND, COTD, ACOTD, SECD, ASECD,
  CSCD, ACSCD.)

Complex function of two variables:  EXPT, LOGB, ATAN2;

e**(pi*i); % Should be -1 (except for computational error.)

log(1+i);

exp ws;

norm(5*exp(2i));

arg(5*exp(2i));

sqrt(1+i);

ws**2;

asin 2;

sin ws;

acos 2;

cos ws;

atan(1+i);

tan ws;

acot(1+i);

cot ws;

asec 0.1;

sec ws;

acsc 0.1;

csc ws;

sinh(1+i);

asinh ws;

cosh(1+i);

acosh ws;

atanh 2;

tanh ws;

acoth 0.3;

coth ws;

asech(1-i);

sech ws;

acsch(1-i);

csch ws;

expt(1+i,1-i);

logb(ws,1+i);

a := 1+i; % Any a such that - pi < repart a <= pi should work.

atan2(sin a,cos a);

ws - a; % Should be 0.

clear a;


% Further math package tests.

%*********************************************************************
%**
%**  The math package will compute the floating point values of   **
%**  the usual elementary functions, namely:                      **
%**     sin     asin     sind    asind     sinh    asinh          **
%**     cos     acos     cosd    acosd     cosh    acosh          **
%**     tan     atan     tand    atand     tanh    atanh          **
%**     cot     acot     cotd    acotd     coth    acoth          **
%**     sec     asec     secd    asecd     sech    asech          **
%**     csc     acsc     cscd    acscd     csch    acsch          **
%**             atan2            atan2d                           **
%**     exp     ln       sqrt                                     **
%**     expt    log      cbrt                                     **
%**     logb    hypot                                             **
%**     log10   floor                                             **
%**             ceiling                                           **
%**             round                                             **
%**                                                               **
%**  All functions are computed to the accuracy of the floating-  **
%**  point precision of the system set up at the time.            **
%**                                                               **
%*********************************************************************
%**  File #1===Trig Function Tests===
%**  Trig functions are tested in both degrees and radians modes.
%*********************************************************************

symbolic;

math!!label;

symbolic procedure terpr(i,j); if remainder(i,j)=0 then terpri()$

on rounded;   % We need !!plumin, etc.

% #1: sind**2+cosd**2 test: ideal answers 1.0 for all args.

  for i:=0:45 do <<write "  ",i,"->",sind float i**2+cosd float i**2;
                       terpr(i,4)>>;

% #2: Quadrant test of sind, cosd: proper answers + +,+ -,- -,- +.

begin scalar a;
    a:= sind 45.0;
    for i:= 0.0:3.0 do
       <<write " ",sind(i*90+45)/a," ", cosd (i*90+45)/a;terpri()>>
  end$

% #3: Scaling test: all values should be 1 exactly.

begin scalar a; a:= cosd 60.0;
%  for i:= -10.0:10.0 do write fix(cosd(60+i*360)/a)," "
   for i:= -10.0:10.0 do write round(cosd(60+i*360)/a)," "
 end$

% #4: Test of radians -> degrees evaluation: ideal values 1.0.

array a(6)$

begin
   for i:=1:6 do  a(i):=sind(15.0*i);
   for i:=1:6 do <<write sin(!!pii2*i/6.0)/a(i),"  "; terpr(i,3)>>
 end$

% #5: Test of tand*cotd: ideal values 1.0.

begin
   for i:=5 step 5 until 85 do
      <<write tand float i*cotd float i,"  "; terpr(i,25)>>;
   terpri()
 end$

% #6: Test of secd**2-tand**2: ideal values 1.0.

begin
   for i:=5 step 5 until 85 do
      <<write secd float i**2-tand float i**2,"  "; terpr(i,25)>>
 end$

% #7: Test of cscd**2-cotd**2: ideal values 1.0.

begin
   for i:=5 step 5 until 85 do
      <<write cscd float i**2-cotd float i**2,"  "; terpr(i,25)>>
 end$

% #8: Test of asind+acosd: ideal values 1.0.

begin write "sind and cosd"; terpri();
   for i:=-10:10 do
      <<write (asind(0.1*i)+acosd(0.1*i))/90,"  "; terpr(i,5)>>;
   write "sin and cos";terpri();
   for i:=-10:10 do
      <<write (acos(0.1*i)+asin(0.1*i))/!!pii2,"  "; terpr(i,5)>>
 end$

% #9: Test of atand+acotd: ideal values 1.0.

begin scalar x; write "tand, atand and acotd"; terpri();
   for i:=-80 step 10 until 80 do
   <<x:=tand float i; write (atand x+acotd x)/90,"  "; terpr(i,50)>>;
     terpri();
   write "tan, atan and acot";terpri();
   for i:=-80 step 10 until 80 do
      <<x:= tan (!!pii2*i/90.0); write (atan x+acot x)/!!pii2,"  ";
     terpr(i,50)>>
 end$

% #10: Test of atand tand: ideal values i for i:=-9:89.

begin
   for i:=-9:89 do
      <<write " ",i,"->",if i=0 then 1.0 else atand tand float i;
        terpr(i,4)>>
 end$

% #11: Test of acot cotd: ideal values 10*i for i:=1:17.

begin
   for i:=10 step 10 until 170 do
   <<write " ",i,"->",acotd cotd i; terpr(i,40)>>; terpri();terpri() end$

% #12: Test of asind sind: ideal values 10*i for i:=-9:9.

begin
   for i:=-90 step 10 until 90 do
      <<write " ",i,"->",asind sind float i; terpr(i,40)>>
 end$

% #13: Test of acosd cosd: ideal values 10*i for i:=1:18.

begin
   for i:=10 step 10 until 180 do
      <<write " ",i,"->",acosd cosd float i; terpr(i,40)>>
 end$

% #14: Test of acscd cscd: ideal values 10*i for i:=-9:9, except
%       error for i=0.

begin
   for i:=-90 step 10 until 90 do
      <<write " ",i,"->",if i=0 then "error" else acscd cscd float i;
        terpr(i,40)>>
 end$

% #15: Test of asecd secd: ideal values 10*i for i :=0:18. except
%       error for i=9.

begin
   for i:=0 step 10 until 180 do
      <<write" ",i,"->",if i=90 then "error" else asecd secd float i;
        terpr(i,40)>>
 end$

%*********************************************************************
%**  ===Exp,Log,Sqrt,Cbrt, and Expt  Function tests===
%*********************************************************************

% #16: Test of properties of exp function: ideal results 1.0.

array b(5)$

begin scalar x; x:=0;
   write "multiplicative property";terpri();
   for i:=0:5 do b(i):=1+i/6.0; for i:=0:5 do for j:=i:5 do
      <<write "  ",exp (b(i)+b(j))/exp(b(i))/exp(b(j));
        terpr(x:=x+1,5)>>
 end$

% #17: Various properties of exp: ideal results 1.0.

begin write "inverse property"$ terpri()$
   for i:=1:5 do write "  ",exp(b(i))*exp(-b(i));terpri();
   write "squares"; terpri();
      for i:=-10:10 do
         <<write "  ",sqrt(exp(0.2*i))/exp(0.1*i); terpr(i,5)>>;
   write "cubes"; terpri();
      for i:=-10:10 do
         <<write "  ",cbrt(exp(0.3*i))/exp(0.1*i); terpr(i,5)>>
 end$

% #18: Test of log exp: ideal results 1.0.

begin for i:=-5:5 do
   <<write if i=0 then "0/0" else (log exp float i)/i,"  "; terpr(i,5)>>
 end$

% #19: Test of log10 expt(10.0,i): ideal results 1.0.

begin scalar i; write "small values i:=-5:5"; terpri();
   for j:=-5:5 do
      <<write if j neq 0 then log10 float expt(10.0,j)/j
          else "zero","  ";
        terpr(j,5)>>;
   write "large i=2**j where j:=0:6"; terpri();
   for j:=0:5 do
      <<write (log10 float expt(10.0,2**j))/2**j,"  "; terpr(j,5)>>;
        terpri();
        write "noninteger values of i=j/10.0 where j:=1:20";terpri();
        for j:=1:20 do
            <<i:=j/10.0; write (log10 float expt(10,i))/i,"  ";
              terpr(j,5)>>
 end$

% #20: Test of properties of expt(x,i)*(expt(x,-i). ideal result 1.0.

begin integer j;
   for x:=2:6 do for i:=2:6 do
      <<write expt(float x,i)*expt(float x,-i),"  "; terpr(j:=j+1,5)>>
 end$

% #21: Test of expt(-x,i)/expt(x,i) for fractional i.

begin integer j,k; write "odd numerator. ideal result -1.0"; terpri();
   for i:=1:10 do
      <<k:=(2*i-1.0)/(8*i+1); write rexpt(-8,k)/rexpt(8,k),"  ";
        terpr(j:=j+1,5)>>;
   write "even numerator. ideal result 1.0"; terpri();
   for i:=1:10 do
      <<k:=(2.0*i)/(8*i+1); write rexpt(-8,k)/rexpt(8,k),"  ";
        terpr(j:=j+1,5)>>
 end$

% #22: Test of properties of ln or log or logb:
%      inverse argument: ideal result -1.0.

begin integer x;
   for i:=2:5 do for j:= 2:10 do
      <<x:=x+1; write logb(float i,float j)/logb(float i,1.0/j),"  ";
        terpr(x,5)>>
 end$

% #23: Test of log(a*b) = log a+log b: ideal result 1.0.

begin integer x;
   for i:=1:5 do for j:=i:5 do
      <<write log (i*j*0.01)/(log (i*0.1)+log(j*0.1)),"  ";
        terpr(x:=x+1,5)>>
 end$

% #24: Test of sqrt x*sqrt x/x for x:=5i*(5i/3)**i where i:=1:20
%      (test values strictly arbitrary): ideal results 1.0.

begin scalar x,s;
   for i:=1:20 do
      <<x:= 5.0*i;s:=sqrt(x:=x*(expt(x/3,i))); write s*s/x,"  ";
        terpr(i,5)>>
 end$

% #25: Test of cbrt x**3/x for x:=5i*(5i/3)**i where i:=-9:10
%      (test values strictly arbitrary):ideal results 1.0.

begin scalar x,s;
   for i:=-9:10 do
      <<x:= 5.0*i; if i neq 0 then s:= cbrt(x:=x*(expt(x/3,i)));
        write if i=0 then 1 else s*s*s/x,"  "; terpr(i,5)>>
 end$

%*********************************************************************
%**  ===Hyperbolic Function Tests===
%*********************************************************************

% #26: Test of sinh x+ cosh x= exp x: ideal results 1.0.

begin scalar x;
   for i:=1:10 do
      <<x:=ln float i$ write (sinh x+cosh x)/exp x,"  "$ terpr(i,5)>>
 end$

% #27: Test of cosh x-sinh x= exp(-x): ideal results 1.0.

begin scalar x;
   for i:=1:10 do
      <<x:=ln float i$ write(cosh x-sinh x)*exp x,"  "$ terpr(i,5)>>
 end$

% #28: Test of (cosh x)**2-(sinh x)**2: ideal results 1.0.

begin scalar x$
   for i:=1:10 do
      <<x:=ln float i$write(cosh x)**2-(sinh x)**2,"  "; terpr(i,5)>>
 end$

% #29: Test of tanh*cosh/sinh: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1);
        write if i=10 then 1 else tanh x*cosh x/sinh x,"  ";
        terpr(i,5)>>
 end$

% #30: Test of tanh*coth: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1); write if i=10 then 1 else tanh x*coth x,"  ";
        terpr(i,5)>>
 end$

% #31: Test of sech*cosh: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1); write sech x*cosh x,"  "; terpr(i,5)>>
 end$

% #32: Test of csch*sinh: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1);  write if i=10 then 1 else csch x*sinh x,"  ";
        terpr(i,5)>>
 end$

% #33: Test of asinh sinh: ideal results 1.0.

begin scalar x; for i:=1:20 do
   <<x:=ln(i*0.1); write if i=10 then 1 else asinh sinh x/x,"  ";
     terpr(i,5)>>
 end$

% #34: Test of acosh cosh: ideal results 1.0.  However, acosh x
%      loses accuracy as x -> 1 since d/dx cosh x -> 0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(1+i*0.05); write acosh cosh x/x,"  "; terpr(i,5)>>
 end$

% #35: Test of cosh acosh:ideal results 1.0.

begin scalar x;
   for i:=1:50 do
      <<x:=1+i/25.0; write (cosh acosh x)/x,"  "; terpr(i,5)>>
 end$

% #36: Test of atanh tanh: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1); write if i=10 then 1 else (atanh tanh x)/x,"  ";
        terpr(i,5)>>
 end$

% #37: Test of acoth coth: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1); write if i=10 then 1 else (acoth coth x)/x,"  ";
        terpr(i,5)>>
 end$

% #38: Test of asech sech: ideal results 1.0.  However, asech x
%      loses accuracy as x -> 1 since d/dx sech x -> 0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(1+i*0.05); write (asech sech x)/x,"  "; terpr(i,5)>>
 end$

% #39: Test of acsch csch: ideal results 1.0.

begin scalar x;
   for i:=1:20 do
      <<x:=ln(i*0.1); write if i=10 then 1 else (acsch csch x)/x,"  ";
        terpr(i,5)>>
 end$

algebraic;

end;
