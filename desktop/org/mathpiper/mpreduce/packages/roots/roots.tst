% Tests of the root finding package.

% Author: Stanley L. Kameny (stan_kameny@rand.org)

comment  Addition for roots mod 1.95.  The function multroot has been
  added to the roots package in mod 1.95.  This provides the capability
  to solve a nest of n polynomials in n variables, provided that each
  polynomial either is univariate or introduces a new variable to the
  set.  The solutions can be either real solutions only, or complex
  solutions.  All solutions to the new examples, problems 117) and
  subsequent, are correct to all digits shown.  As in the prior examples,
  root order and values should agree exactly with that given here.

comment  This test file works only with Reduce version 3.5 and later
  and contains examples all of which are solved by roots mod 1.94.
  Answers are rounded to the value given by rootacc (default = 6)
  unless higher accuracy is needed to separate roots.  Format may differ 
  from that given here, but root order and values should agree exactly.
  (Although the function ROOTS may obtain the roots in a different order,
  they are sorted into a standard order in mod 1.94 and later.)
         In the following, problems 20) and 82) are time consuming and
  have been commented out to speed up the test.
         The hard examples 111) through 115) almost double the test time
  but are necessary to test some logical paths.
         A new "hardest" example has been added as example 116).  It is
  commented out, since it is time consuming, but it is solved by roots
  mod 1.94.  The time needed to run the three commented-out examples is
  almost exactly equal to the time for the rest of the test.  Users of
  fast computers can uncomment the lines marked with %**%.  The three
  examples by themselves are contained in the test file rootsxtr.tst.

         When answers are produced which require precision increase for
  printing out or input of roots, roots functions cause precision
  increase to occur.  If the precision is already higher than the 
  default value, a message is printed out warning of the the precision 
  normally needed for input of those values.$

realroots x;   % To load roots package.

write "This is Roots Package test ", symbolic roots!-mod$


% Simple root finding.

% 1) multiple real and imaginary roots plus two real roots.
zz:= (x-3)**2*(100x**2+113)**2*(1000000x-10000111)*(x-1); roots zz;
%{x=1.06301*i,x=1.06301*i,x=-1.06301*i,x=-1.06301*i,
%x=3.0,x=3.0,x=1,x=10.0001} (rootacc caused rounding to 6 places)

% Accuracy is increased whenever necessary to separate distinct roots.

% 2) accuracy increase to 7 required for two roots.
zz:=(x**2+1)*(x-2)*(1000000x-2000001); roots zz;
%{x=i,x= -i,x=2.0,x=2.000001}

% 3) accuracy increase to 8 required.
zz:= (x-3)*(10000000x-30000001); roots zz;
%{x=3.0,x=3.0000001}

% 4) accuracy increase required here to separate repeated root from
% simple root.
zz := (x-3)*(1000000x-3000001)*(x-3)*(1000000x-3241234); roots zz;
%{x=3.0,x=3.0,x=3.000001,x=3.24123}

% other simple examples

% 5) five real roots with widely different spacing.
zz:= (x-1)*(10x-11)*(x-1000)*(x-1001)*(x-100000); roots zz;
%{x=1,x=1.1,x=1000.0,x=1001.0,x=1.0E+5}

% 6) a cluster of 5 roots in complex plane in vicinity of x=1.
zz:= (x-1)*(10000x**2-20000x+10001)*(10000x**2-20000x+9999); roots zz;
%{x=0.99,x=1,x=1 + 0.01*i,x=1 - 0.01*i,x=1.01}

% 7) four closely spaced real roots.
zz := (x-1)*(100x-101)*(100x-102)*(100x-103); roots zz;
%{x=1,x=1.01,x=1.02,x=1.03}

% 8) five closely spaced roots, 3 real + 1 complex pair.
zz := (x-1)*(100x-101)*(100x-102)*(100x**2-200x+101); roots zz;
%{x=1,x=1 + 0.1*i,x=1 - 0.1*i,x=1.01,x=1.02}

% 9) symmetric cluster of 5 roots, 3 real + 1 complex pair.
zz := (x-2)*(10000x**2-40000x+40001)*(10000x**2-40000x+39999); roots zz;
%{x=1.99,x=2.0,x=2.0 + 0.01*i,x=2.0 - 0.01*i,x=2.01}

% 10) closely spaced real and complex pair.
ss:= (x-2)*(100000000x**2-400000000x+400000001); roots ss;
%{x=2.0,x=2.0 + 0.0001*i,x=2.0 - 0.0001*i}

% 11) Zero roots and multiple roots cause no problem.
% Multiple roots are shown when the switch multiroot is on
%(normally on.)
zz:= x*(x-1)**2*(x-4)**3*(x**2+1); roots zz;
%{x=0,x=4.0,x=4.0,x=4.0,x=1,x=1,x=i,x= - i}

% 12) nearestroot will find a single root "near" a value, real or
% complex.
nearestroot(zz,2i);
%{x=i}

% More difficult examples.

% Three examples in which root scaling is needed in the complex
% iteration process.

% 13) nine roots, 3 real and 3 complex pairs.
zz:= x**9-45x-2; roots zz;
%{x= - 1.60371,x=-1.13237 + 1.13805*i,x=-1.13237 - 1.13805*i,
% x= - 0.0444444,x=0.00555357 + 1.60944*i,x=0.00555357 - 1.60944*i,
% x=1.14348 + 1.13804*i,x=1.14348 - 1.13804*i,x=1.61483}


comment  In the next two examples, there are complex roots with
  extremely  small real parts (new capability in Mod 1.91.);

% 14) nine roots, 1 real and 4 complex pairs.
zz:= x**9-9999x**2-0.01; roots zz;
%{x=-3.3584 + 1.61732*i,x=-3.3584 - 1.61732*i,
% x=-0.829456 + 3.63408*i,x=-0.829456 - 3.63408*i,
% x=5.0025E-29 + 0.00100005*i,x=5.0025E-29 - 0.00100005*i,
% x=2.32408 + 2.91431*i,x=2.32408 - 2.91431*i,x=3.72754}

comment  Rootacc 7 produces 7 place accuracy.  Answers will print in
  bigfloat format if floating point print >6 digits is not implemented.;

% 15) nine roots, 1 real and 4 complex pairs.
rootacc 7; zz:= x**9-500x**2-0.001; roots zz;
%{x=-2.189157 + 1.054242*i,x=-2.189157 - 1.054242*i,
% x=-0.5406772 + 2.368861*i,x=-0.5406772 - 2.368861*i,
% x=1.6E-26 + 0.001414214*i,x=1.6E-26 - 0.001414214*i,
% x=1.514944 + 1.899679*i,x=1.514944 - 1.899679*i,x=2.429781}

% the famous Wilkinson "ill-conditioned" polynomial and its family.

% 16) W(6) four real roots plus one complex pair.
zz:= 10000*(for j:=1:6 product(x+j))+27x**5; roots zz;
%{x= - 6.143833,x=-4.452438 + 0.02123455*i,x=-4.452438 - 0.02123455*i,
% x= - 2.950367,x= - 2.003647,x= - 0.9999775}

% 17) W(8) 4 real roots plus 2 complex pairs.
zz:= 1000*(for j:=1:8 product(x+j))+2x**7; roots zz;
%{x= - 8.437546,x=-6.494828 + 1.015417*i,x=-6.494828 - 1.015417*i,
% x=-4.295858 + 0.2815097*i,x=-4.295858 - 0.2815097*i,
% x= - 2.982725,x= - 2.000356,x= - 0.9999996}

% 18) W(10) 6 real roots plus 2 complex pairs.
zz:=1000*(for j:= 1:10 product (x+j))+x**9; roots zz;
%{x= - 10.80988,x=-8.70405 + 1.691061*i,x=-8.70405 - 1.691061*i,
% x=-6.046279 + 1.134321*i,x=-6.046279 - 1.134321*i,x= - 4.616444,
% x= - 4.075943,x= - 2.998063,x= - 2.000013,x= - 1}

% 19) W(12) 6 real roots plus 3 complex pairs.
zz:= 10000*(for j:=1:12 product(x+j))+4x**11; roots zz;
%{x= - 13.1895,x=-11.02192 + 2.23956*i,x=-11.02192 - 2.23956*i,
% x=-7.953917 + 1.948001*i,x=-7.953917 - 1.948001*i,
% x=-5.985629 + 0.8094247*i,x=-5.985629 - 0.8094247*i,
% x= - 4.880956,x= - 4.007117,x= - 2.999902,x= - 2.0,x= - 1}

% 20) W(20) 10 real roots plus 5 complex pairs. (The original problem)
% This example is commented out, since it takes significant time without
% being particularly difficult or checking out new paths:
%**%  zz:= x**19+10**7*for j:=1:20 product (x+j); roots zz;
%{x= - 20.78881,x=-19.45964 + 1.874357*i,x=-19.45964 - 1.874357*i,
% x=-16.72504 + 2.731577*i,x=-16.72504 - 2.731577*i,
% x=-14.01105 + 2.449466*i,x=-14.01105 - 2.449466*i,
% x=-11.82101 + 1.598621*i,x=-11.82101 - 1.598621*i,
% x=-10.12155 + 0.6012977*i,x=-10.12155 - 0.6012977*i,
% x= - 8.928803,x= - 8.006075,x= - 6.999746,x= - 6.000006,
% x= - 5.0,x= - 4.0,x= - 3.0,x= - 2.0,x= - 1}

rootacc 6;
% 21)  Finding one of a cluster of 8 roots.
zz:= (10**16*(x-1)**8-1); nearestroot(zz,2);
%{x=1.01}

% 22)  Six real roots spaced 0.01 apart.
c := 100; zz:= (x-1)*for i:=1:5 product (c*x-(c+i)); roots zz;
%{x=1,x=1.01,x=1.02,x=1.03,x=1.04,x=1.05}

% 23)  Six real roots spaced 0.001 apart.
c := 1000; zz:= (x-1)*for i:=1:5 product (c*x-(c+i)); roots zz;
%{x=1,x=1.001,x=1.002,x=1.003,x=1.004,x=1.005}

% 24)  Five real roots spaced 0.0001 apart.
c := 10000; zz:= (x-1)*for i:=1:4 product (c*x-(c+i)); roots zz;
%{x=1,x=1.0001,x=1.0002,x=1.0003,x=1.0004}

% 25)  A cluster of 9 roots, 5 real, 2 complex pairs; spacing 0.1.
zz:= (x-1)*(10**8*(x-1)**8-1); roots zz;
%{x=0.9,x=0.929289 + 0.0707107*i,x=0.929289 - 0.0707107*i,
% x=1,x=1 + 0.1*i,x=1 - 0.1*i,
% x=1.07071 + 0.0707107*i,x=1.07071 - 0.0707107*i,x=1.1}

% 26)  Same, but with spacing 0.01.
zz:= (x-1)*(10**16*(x-1)**8-1); roots zz;
%{x=0.99,x=0.992929 + 0.00707107*i,x=0.992929 - 0.00707107*i,
% x=1,x=1 + 0.01*i,x=1 - 0.01*i,
% x=1.00707 + 0.00707107*i,x=1.00707 - 0.00707107*i,x=1.01}

% 27)  Spacing reduced to 0.001.
zz:= (x-1)*(10**24*(x-1)**8-1); roots zz;
%{x=0.999,x=0.999293 + 0.000707107*i,x=0.999293 - 0.000707107*i,
% x=1,x=1 + 0.001*i,x=1 - 0.001*i,
% x=1.00071 + 0.000707107*i,x=1.00071 - 0.000707107*i,x=1.001}

% 28)  Eight roots divided into two clusters.
zz:= (10**8*(x-1)**4-1)*(10**8*(x+1)**4-1); roots zz;
%{x= - 0.99,x=0.99, x=-1 - 0.01*i,x=1 + 0.01*i,
% x=-1 + 0.01*i,x=1 - 0.01*i,x= - 1.01,x=1.01}

% 29)  A cluster of 8 roots in a different configuration.
zz:= (10**8*(x-1)**4-1)*(10**8*(100x-102)**4-1); roots zz;
%{x=0.99,x=1 + 0.01*i,x=1 - 0.01*i,x=1.01,
% x=1.0199,x=1.02 + 0.0001*i,x=1.02 - 0.0001*i,x=1.0201}

% 30)  A cluster of 8 complex roots.
zz:= ((10x-1)**4+1)*((10x+1)**4+1); roots zz;
%{x=-0.0292893 - 0.0707107*i,x=0.0292893 + 0.0707107*i,
% x=-0.0292893 + 0.0707107*i,x=0.0292893 - 0.0707107*i,
% x=-0.170711 - 0.0707107*i,x=0.170711 + 0.0707107*i,
% x=-0.170711 + 0.0707107*i,x=0.170711 - 0.0707107*i}

comment  In these examples, accuracy increase is required to separate a
  repeated root from a simple root.;

% 31)  Using allroots;
zz:= (x-4)*(x-3)**2*(1000000x-3000001); roots zz;
%{x=3.0,x=3.0,x=3.000001,x=4.0}

% 32)  Using realroots;
realroots zz;
%{x=3.0,x=3.0,x=3.000001,x=4.0}

comment  Tests of new capabilities in mod 1.87 for handling complex
  polynomials and polynomials with very small imaginary parts or very
  small real roots. A few real examples are shown, just to demonstrate
  that these still work.;

% 33) A trivial complex case (but degrees 1 and 2 are special cases);
zz:= x-i; roots zz;
%{x=i}

% 34) Real case.
zz:= y-7; roots zz;
%{y=7.0}

% 35) Roots with small imaginary parts (new capability);
zz := 10**16*(x**2-2x+1)+1; roots zz;
%{x=1 + 0.00000001*i,x=1 - 0.00000001*i}

% 36) One real, one complex root.
zz:=(x-9)*(x-5i-7); roots zz;
%{x=9.0,x=7.0 + 5.0*i}

% 37) Three real roots.
zz:= (x-1)*(x-2)*(x-3); roots zz;
%{x=1,x=2.0,x=3.0}

% 38) 2 real + 1 imaginary root.
zz:=(x**2-8)*(x-5i); roots zz;
%{x= - 2.82843,x=2.82843,x=5.0*i}

% 39) 2 complex roots.
zz:= (x-1-2i)*(x+2+3i); roots zz;
%{x=-2.0 - 3.0*i,x=1 + 2.0*i}

% 40) 2 irrational complex roots.
zz:= x**2+(3+2i)*x+7i; roots zz;
%{x=-3.14936 + 0.21259*i,x=0.149358 - 2.21259*i}

% 41) 2 complex roots of very different magnitudes with small imaginary
% parts.
zz:= x**2+(1000000000+12i)*x-1000000000; roots zz;
%{x=-1.0E+9 - 12.0*i,x=1 - 0.000000012*i}

% 42) Multiple real and complex roots cause no difficulty, provided
% that input is given in integer or rational form, (or if in decimal
% fraction format, with switch rounded off  or adjprec on and
% coefficients input explicitly,) so that polynomial is stored exactly.
zz :=(x**2-2i*x+5)**3*(x-2i)*(x-11/10)**2; roots zz;
%{x=-1.44949*i, x=-1.44949*i, x=-1.44949*i,
% x=3.44949*i, x=3.44949*i, x=3.44949*i, x=1.1, x=1.1, x=2.0*i}

% 42a) would have failed in roots Mod 1.93 and previously (bug)
realroots zz;
%{x=1.1,x=1.1}

% 43) 2 real, 2 complex roots.
zz:= (x**2-4)*(x**2+3i*x+5i); roots zz;
%{x= - 2.0,x=2.0,x=-1.2714 + 0.466333*i,x=1.2714 - 3.46633*i}

% 44) 4 complex roots.
zz:= x**4+(0.000001i)*x-16; roots zz;
%{x=-2.0 - 0.0000000625*i,x=-2.0*i,x=2.0*i,x=2.0 - 0.0000000625*i}

% 45) 2 real, 2 complex roots.
zz:= (x**2-4)*(x**2+2i*x+8); roots zz;
%{x= - 2.0,x=2.0,x=-4.0*i,x=2.0*i}

% 46) Using realroots to find only real roots.
realroots zz;
%{x= - 2.0,x=2.0}

% 47) Same example, applying nearestroot to find a single root.
zz:= (x**2-4)*(x**2+2i*x+8); nearestroot(zz,1);
%{x=2.0}

% 48) Same example, but focusing on imaginary point.
nearestroot(zz,i);
%{x=2.0*i}

% 49) The seed parameter can be complex also.
nearestroot(zz,1+i);
%{x=2.0*i}

% 50) One more nearestroot example.  Nearest root to real point may be
% complex.
zz:= (x**2-4)*(x**2-i); roots zz;
%{x= - 2.0,x=2.0,x=-0.707107 - 0.707107*i,x=0.707107 + 0.707107*i}

nearestroot (zz,1);
%{X=0.707107 + 0.707107*i}

% 51) 1 real root plus 5 complex roots.
zz:=(x**3-3i*x**2-5x+9)*(x**3-8); roots zz;
%{x=-1 + 1.73205*i,x=-1 - 1.73205*i,x=2.0,
% x=-2.41613 + 1.19385*i,x=0.981383 - 0.646597*i,x=1.43475 + 2.45274*i}

nearestroot(zz,1);
%{x=0.981383 - 0.646597*i}

% 52) roots can be computed to any accuracy desired, eg.  (note that the
% imaginary part of the second root is truncated because of its size,
% and that the imaginary part of a complex root is never polished away,
% even if it is smaller than the accuracy would require.)
zz := x**3+10**(-20)*i*x**2+8; rootacc 12; roots zz; rootacc 6;
%{x=-2.0 - 3.33333333333E-21*i,x=1 - 1.73205080757*i,
% x=1 + 1.73205080757*i}

% 53) Precision of 12 required to find small imaginary root,
% but standard accuracy can be used.
zz := x**2+123456789i*x+1; roots zz;
%{x=-1.23457E+8*i,x=0.0000000081*i}

% 54) Small real root is found with root 10*18 times larger(new).
zz := (x+1)*(x**2+123456789*x+1); roots zz;
%{x= - 1.23457E+8,x= - 1,x= - 0.0000000081}

% 55) 2 complex, 3 real irrational roots.
ss := (45*x**2+(-10i+12)*x-10i)*(x**3-5x**2+1); roots ss;
%{x= - 0.429174,x=0.469832,x=4.95934,
% x=-0.448056 - 0.19486*i,x=0.18139 + 0.417083*i}

% 56) Complex polynomial with floating coefficients.
zz := x**2+1.2i*x+2.3i+6.7; roots zz;
%{x=-0.427317 + 2.09121*i,x=0.427317 - 3.29121*i}

% 56a) multiple roots will be found if coefficients read in exactly.
% Exact read-in will occur unless dmode is rounded or complex-rounded.
zz := x**3 + (1.09 - 2.4*i)*x**2 + (-1.44 - 2.616*i)*x + -1.5696;
roots zz;
%{x=1.2*i,x=1.2*i,x= - 1.09}

% 57) Realroots, isolater and rlrootno accept 1, 2 or 3 arguments: (new)
zz:= for j:=-1:3 product (x-j); rlrootno zz;
% 5

realroots zz;
%{x=0,x= -1,x=1,x=2.0,x=3.0}

rlrootno(zz,positive); %positive selects positive, excluding 0.
% 3

rlrootno(zz,negative); %negative selects negative, excluding 0.
% 1

realroots(zz,positive);
%{x=1,x=2.0,x=3.0}

rlrootno(zz,-1.5,2); %the format with 3 arguments selects a range.
% 4

realroots(zz,-1.5,2); %the range is inclusive, except that:
%{x=0,x= - 1,x=1,x=2.0}

% A specific limit b may be excluded by using   exclude b.  Also, the
% limits infinity and -infinity can be specified.

realroots(zz,exclude 0,infinity);
% equivalent to realroots(zz,positive).
%{x=1,x=2.0,x=3.0}

rlrootno(zz,-infinity,exclude 0); % equivalent to rlrootno(zz,negative).
% 1

rlrootno(zz,-infinity,0);
% 2

rlrootno(zz,infinity,-infinity);
%equivalent to rlrootno zz; (order of limits does not matter.)
% 5

realroots(zz,1,infinity); % finds all real roots >= 1.
%{x=1,x=2.0,x=3.0}

realroots(zz,1,positive); % finds all real roots > 1.
%{x=2.0,x=3.0}

% 57a) Bug corrected in mod 1.94.  (handling of rational limits)
zz := (x-1/3)*(x-1/5)*(x-1/7)*(x-1/11);
realroots(zz,1/11,exclude(1/3));
%{x=0.0909091,x=0.142857,x=0.2}

realroots(zz,exclude(1/11),1/3);
%{x=0.142857,x=0.2,x=0.333333}

% New capabilities added in mod 1.88.

% 58) 3 complex roots, with two separated by very small real difference.
zz :=(x+i)*(x+10**8i)*(x+10**8i+1); roots zz;
%{x=-1 - 1.0E+8*i,x=-1.0E+8*i,x= - i}

% 59) Real polynomial with two complex roots separated by very small
% imaginary part.
zz:= (10**14x+123456789000000+i)*(10**14x+123456789000000-i); roots zz;
%{x=-1.23457 + 1.0E-14*i,x=-1.23457 - 1.0E-14*i}

% 60) Real polynomial with two roots extremely close together.
zz:= (x+2)*(10**10x+12345678901)*(10**10x+12345678900); roots zz;
%{x= - 2.0,x= - 1.2345678901,x= - 1.23456789}

% 61) Real polynomial with multiple root extremely close to simple root.
zz:= (x-12345678/10000000)*(x-12345679/10000000)**2; roots zz;
%{x=1.2345679,x=1.2345679,x=1.2345678}

% 62) Similar problem using realroots.
zz:=(x-2**30/10**8)**2*(x-(2**30+1)/10**8); realroots zz;
%{x=10.73741824,x=10.73741824,x=10.73741825}

% 63) Three complex roots with small real separation between two.
zz:= (x-i)*(x-1-10**8i)*(x-2-10**8i); roots zz;
%{x=i,x=1 + 1.0E+8*i,x=2.0 + 1.0E+8*i}

% 64) Use of nearestroot to isolate one of the close roots.
nearestroot(zz,10**8i+99/100);
%{x=1 + 1.0E+8*i}

% 65) Slightly more complicated example with close complex roots.
zz:= (x-i)*(10**8x-1234-10**12i)*(10**8x-1233-10**12i); roots zz;
%{x=i,x=0.00001233 + 10000.0*i,x=0.00001234 + 10000.0*i}

% 66) Four closely spaced real roots with varying spacings.
zz:= (x-1+1/10**7)*(x-1+1/10**8)*(x-1)*(x-1-1/10**7); roots zz;
%{x=0.9999999,x=0.99999999,x=1,x=1.0000001}

% 67) Complex pair plus two close real roots.
zz:= (x**2+1)*(x-12345678/10000000)*(x-12345679/10000000); roots zz;
%{x=i,x= - i,x=1.2345678,x=1.2345679}

% 68) Same problem using realroots to find only real roots.
realroots zz;
%{x=1.2345678,x=1.2345679}

% The switch ratroot causes output to be given in rational form.
% 69) Two complex roots with output in rational form.
on ratroot,complex; zz:=x**2-(5i+1)*x+1; sss:= roots zz; 

%           346859 - 1863580*i     482657 + 2593180*i
%sss := {x=--------------------,x=--------------------}
%                10000000                500000

% With roots in rational form, mkpoly can be used to reconstruct a
% polynomial.
zz1 := mkpoly sss;

%                      2
%zz1 := 5000000000000*x  - (4999999500000 + 25000010000000*i)*x
%
%        + 5000012308763 - 2110440*i

% Finding the roots of the new polynomial zz1.
rr:= roots zz1; 

%          346859 - 1863580*i     482657 + 2593180*i
%rr := {x=--------------------,x=--------------------}
%               10000000                500000

% The roots are stable to the extent that rr=ss, although zz1 and
% zz may differ.

zz1 - zz;

%               2
%4999999999999*x  - (4999999499999 + 25000009999995*i)*x
%
% + 5000012308762 - 2110440*i

% 70) Same type of problem in which roots are found exactly.
zz:=(x-10**8+i)*(x-10**8-i)*(x-10**8+3i/2)*(x-i); rr := roots zz;

%          4                    3                      2
%zz := (2*x  - (600000000 - i)*x  + 60000000000000005*x
%
%        - (2000000000000000800000000 + 29999999999999999*i)*x
%
%        + (30000000000000003 + 2000000000000000200000000*i))/2

%rr := {x=100000000 + i,x=100000000 - i,x=i,
%
%          200000000 - 3*i
%       x=-----------------}
%                 2

% Reconstructing a polynomial from the roots.
ss := mkpoly rr;

%         4                    3                      2
%ss := 2*x  - (600000000 - i)*x  + 60000000000000005*x
%
%       - (2000000000000000800000000 + 29999999999999999*i)*x
%
%       + (30000000000000003 + 2000000000000000200000000*i)

% In this case, the same polynomial is obtained.
ss - num zz;
% 0

% 71) Finding one of the complex roots using nearestroot.
nearestroot(zz,10**8-2i); 

%    200000000 - 3*I
%{x=-----------------}
%           2

% Finding the other complex root using nearestroot.
nearestroot(zz,10**8+2i);
%{x=100000000 + I}

% 72) A realroots problem which requires accuracy increase to avoid
% confusion of two roots.
zz:=(x+1)*(10000000x-19999999)*(1000000x-2000001)*(x-2);
realroots zz; 

%          19999999         2000001
% {x=-1,x=----------,x=2,x=---------}
%          10000000         1000000

% 73) Without the accuracy increase, this example would produce the
% obviously incorrect answer 2.
realroots(zz,3/2,exclude 2); 

%     19999999
% {x=----------}
%     10000000

% Rlrootno also gives the correct answer in this case.
rlrootno(zz,3/2,exclude 2);
% 1

% 74) Roots works equally well in this problem.
rr := roots zz;
%                 19999999        2000001
%rr := {x= - 1,x=----------,x=2,x=---------}
%                 10000000        1000000

% 75) The function getroot is convenient for obtaining the value of a
% root.
rr1 := getroot(1,rr);

%         19999999
% rr1 := ----------
%         10000000

% 76) For example, the value can be used as an argument to nearestroot.
nearestroot(zz,rr1); 

%     19999999
% {x=----------}
%     10000000


comment  New capabilities added to Mod 1.90 for avoiding floating point
  exceptions and exceeding iteration limits.;

% 77) This and the next example would previously have aborted because
%of exceeding iteration limits:
off ratroot; zz := x**16 - 900x**15 -2; roots zz;
%{x= - 0.665423,x=-0.607902 + 0.270641*i,x=-0.607902 - 0.270641*i,
% x=-0.44528 + 0.494497*i, x=-0.44528 - 0.494497*i,
% x=-0.205664 + 0.632867*i,x=-0.205664 - 0.632867*i,
% x=0.069527 + 0.661817*i,x=0.069527 - 0.661817*i,
% x=0.332711 + 0.57633*i,x=0.332711 - 0.57633*i,
% x=0.538375 + 0.391176*i,x=0.538375 - 0.391176*i,
% x=0.650944 + 0.138369*i,x=0.650944 - 0.138369*i,x=900.0}

% 78) a still harder example.
zz := x**30 - 900x**29 - 2; roots zz;
%{x= - 0.810021,x=-0.791085 + 0.174125*i,x=-0.791085 - 0.174125*i,
% x=-0.735162 + 0.340111*i,x=-0.735162 - 0.340111*i,
% x=-0.644866 + 0.490195*i,x=-0.644866 - 0.490195*i,
% x=-0.524417 + 0.617362*i,x=-0.524417 - 0.617362*i,
% x=-0.379447 + 0.715665*i,x=-0.379447 - 0.715665*i,
% x=-0.216732 + 0.780507*i,x=-0.216732 - 0.780507*i,
% x=-0.04388 + 0.808856*i,x=-0.04388 - 0.808856*i,
% x=0.131027 + 0.799383*i,x=0.131027 - 0.799383*i,
% x=0.299811 + 0.752532*i,x=0.299811 - 0.752532*i,
% x=0.454578 + 0.67049*i,x=0.454578 - 0.67049*i,
% x=0.588091 + 0.557094*i,x=0.588091 - 0.557094*i,
% x=0.694106 + 0.417645*i,x=0.694106 - 0.417645*i,
% x=0.767663 + 0.258664*i,x=0.767663 - 0.258664*i,
% x=0.805322 + 0.0875868*i,x=0.805322 - 0.0875868*i,x=900.0}

% 79) this deceptively simple example previously caused floating point
% overflows on some systems:
aa := x**6 - 4*x**3 + 2; realroots aa;
%{x=0.836719,x=1.50579}

% 80) a harder problem, which would have failed on almost all systems:
rr := x**16 - 90000x**15 - x**2 -2; realroots rr;
%{x= - 0.493299,x=90000.0}

% 81) this example would have failed because of floating point
% exceptions on almost all computer systems.
rr := x**30 - 9*10**10*x**29 - 2;  realroots rr;
%{x= - 0.429188,x=9.0E+10}

% 82) a test of allroot on this example.
% This example is commented out because it takes significant time
% without breaking new ground.
%**% roots  rr;
%{x= - 0.429188,
% x=-0.419154 + 0.092263*i,x=-0.419154 - 0.092263*i,
% x=-0.389521 + 0.180211*i,x=-0.389521 - 0.180211*i,
% x=-0.341674 + 0.259734*i,x=-0.341674 - 0.259734*i,
% x=-0.277851 + 0.327111*i,x=-0.277851 - 0.327111*i,
% x=-0.201035 + 0.379193*i,x=-0.201035 - 0.379193*i,
% x=-0.11482 + 0.413544*i,x=-0.11482 - 0.413544*i,
% x=-0.0232358 + 0.428559*i,x=-0.0232358 - 0.428559*i,
% x=0.0694349 + 0.423534*i,x=0.0694349 - 0.423534*i,
% x=0.158859 + 0.398706*i,x=0.158859 - 0.398706*i,
% x=0.240855 + 0.355234*i,x=0.240855 - 0.355234*i,
% x=0.311589 + 0.295153*i,x=0.311589 - 0.295153*i,
% x=0.367753 + 0.22127*i,x=0.367753 - 0.22127*i,
% x=0.406722 + 0.13704*i,x=0.406722 - 0.13704*i,
% x=0.426672 + 0.0464034*i,x=0.426672 - 0.0464034*i,x=9.0E+10}

% 83) test of starting point for iteration: no convergence if good
% real starting point is not found.
zz := x**30 -9*10**12x**29 -2; firstroot zz;
%{x= - 0.36617}

% 84) a case in which there are no real roots and good imaginary
% starting point must be used or roots cannot be found.
zz:= 9x**16 - x**5 +1; roots zz;
%{x=-0.866594 + 0.193562*i,x=-0.866594 - 0.193562*i,
% x=-0.697397 + 0.473355*i,x=-0.697397 - 0.473355*i,
% x=-0.510014 + 0.716449*i,x=-0.510014 - 0.716449*i,
% x=-0.161318 + 0.87905*i,x=-0.161318 - 0.87905*i,
% x=0.182294 + 0.828368*i,x=0.182294 - 0.828368*i,
% x=0.459373 + 0.737443*i,x=0.459373 - 0.737443*i,
% x=0.748039 + 0.494348*i,x=0.748039 - 0.494348*i,
% x=0.845617 + 0.142879*i,x=0.845617 - 0.142879*i}

% 85) five complex roots.
zz := x**5 - x**3 + i; roots zz;
%{x=-1.16695 - 0.217853*i,x=-0.664702 + 0.636663*i,x=-0.83762*i,
% x=0.664702 + 0.636663*i,x=1.16695 - 0.217853*i}

% Additional capabilities in Mod 1.91.

% 86) handling of polynomial with huge or infinitesimal coefficients.
precision reset;
on rounded; precision reset;
% so that the system will start this example in floating point.  Rounded
% is on so that the polynomial won't fill the page!
zz:= 1.0e-500x**3+x**2+x;
roots zz; off rounded; % rounded not normally needed for roots.
%{x=0,x= - 1.0E+500,x= - 1}
off roundbf;

comment  Switch roundbf will have been turned on in the last example in
  most computer systems.  This will inhibit the use of hardware floating
  point unless roundbf is turned off.

  Polynomials which make use of powergcd substitution and cascaded
  solutions.

  Uncomplicated cases.;

switch powergcd; % introduced here to verify that same answers are
% obtained with and without employing powergcd strategy.  Roots are
% found faster for applicable cases when !*powergcd=t (default state.)

% 87) powergcd done at the top level.
zz := x**12-5x**9+1; roots zz;
%{x=-0.783212 + 0.276071*i,x=0.152522 - 0.816316*i,
% x=0.63069 + 0.540246*i,x=-0.783212 - 0.276071*i,
% x=0.152522 + 0.816316*i,x=0.63069 - 0.540246*i,
% x=-0.424222 + 0.734774*i,x=-0.424222 - 0.734774*i,x=0.848444,
% x=-0.85453 + 1.48009*i,x=-0.85453 - 1.48009*i,x=1.70906}

off powergcd; roots zz; on powergcd;
%{x=-0.85453 + 1.48009*i,x=-0.85453 - 1.48009*i,
% x=-0.783212 + 0.276071*i,x=-0.783212 - 0.276071*i,
% x=-0.424222 + 0.734774*i,x=-0.424222 - 0.734774*i,
% x=0.152522 + 0.816316*i,x=0.152522 - 0.816316*i,
% x=0.63069 + 0.540246*i,x=0.63069 - 0.540246*i,x=0.848444,x=1.70906}

% 88) powergcd done after square free factoring.
zz := (x-1)**2*zz; roots zz;
%{x=1,x=1,
% x=-0.783212 + 0.276071*i,x=0.152522 - 0.816316*i,
% x=0.63069 + 0.540246*i,x=-0.783212 - 0.276071*i,
% x=0.152522 + 0.816316*i,x=0.63069 - 0.540246*i,
% x=-0.424222 + 0.734774*i,x=-0.424222 - 0.734774*i,x=0.848444,
% x=-0.85453 + 1.48009*i,x=-0.85453 - 1.48009*i,x=1.70906}

off powergcd; roots zz; on powergcd;
%{x=1,x=1,
% x=-0.85453 + 1.48009*i,x=-0.85453 - 1.48009*i,
% x=-0.783212 + 0.276071*i,x=-0.783212 - 0.276071*i,
% x=-0.424222 + 0.734774*i,x=-0.424222 - 0.734774*i,
% x=0.152522 + 0.816316*i,x=0.152522 - 0.816316*i,
% x=0.63069 + 0.540246*i,x=0.63069 - 0.540246*i,
% x=0.848444,x=1.70906}

% 89) powergcd done after separation into real and complex polynomial.
zz := x**5-i*x**4+x**3-i*x**2+x-i; roots zz;
%{x=-0.5 - 0.866025*i,x=0.5 + 0.866025*i,
% x=-0.5 + 0.866025*i,x=0.5 - 0.866025*i,x=i}

off powergcd; roots zz; on powergcd;
%{x=-0.5 + 0.866025*i,x=-0.5 - 0.866025*i,
% x=0.5 + 0.866025*i,x=0.5 - 0.866025*i,x=i}

% Cases where root separation requires accuracy and/or precision
% increase.  In some examples we get excess accuracy, but it is hard
% avoid this and still get all roots separated.

% 90) accuracy increase required to separate close roots;
let x=y**2;
zz:= (x-3)*(100000000x-300000001); roots zz;
%{y= - 1.732050808,y=1.732050808,y= - 1.73205081,y=1.73205081}

off powergcd; roots zz;  on powergcd;
%{y= - 1.73205081,y= - 1.732050808,y=1.732050808,y=1.73205081}

% 91) roots to be separated are on different square free factors.
zz:= (x-3)**2*(10000000x-30000001); roots zz;
%{y= - 1.73205081,y= - 1.73205081,y=1.73205081,y=1.73205081,
% y= - 1.73205084,y=1.73205084}

off powergcd; roots zz;  on powergcd;
%{y= - 1.73205081,y= - 1.73205081,y=1.73205081,y=1.73205081,
% y= - 1.73205084,y=1.73205084}

% 91a) A new capability for nearestroot:
nearestroot(zz,1.800000000001); % should find the root to 13 places.
%{y=1.732050836436}

% 92) roots must be separated in the complex polynomial factor only.
zz :=(y+1)*(x+10**8i)*(x+10**8i+1); roots zz;
%{y= - 1,
% y=-7071.067777 + 7071.067847*i,y=7071.067777 - 7071.067847*i,
% y=-7071.067812 + 7071.067812*i,y=7071.067812 - 7071.067812*i}

% 93)
zz := (x-2)**2*(1000000x-2000001)*(y-1); roots zz;
%{y= - 1.4142136,y= - 1.4142136,y=1.4142136,y=1.4142136,
% y= - 1.4142139,y=1,y=1.4142139}

% 94)
zz := (x-2)*(10000000x-20000001); roots zz;
%{y= - 1.41421356 ,y=1.41421356 ,y= - 1.4142136,y=1.4142136}

% 95)
zz := (x-3)*(10000000x-30000001); roots zz;
%{y= - 1.73205081 ,y=1.73205081 ,y= - 1.73205084 ,y=1.73205084}

% 96)
zz := (x-9)**2*(1000000x-9000001); roots zz;
%{y= - 3.0,y= - 3.0,y=3.0,y=3.0,y= - 3.00000017,y=3.00000017}

% 97)
zz := (x-3)**2*(1000000x-3000001); roots zz;
%{y= - 1.7320508,y= - 1.7320508,y=1.7320508,y=1.7320508,
% y= - 1.7320511,y=1.7320511}

% 98) the accuracy of the root sqrt 5 depends upon another close root.
% Although one of the factors is given in decimal notation, it is not
% necessary to turn rounded on.
rootacc 10; % using rootacc to specify the minumum desired accuracy.
zz := (y^2-5)*(y-2.2360679775);
% in this case, adding one place to the root near sqrt 5 causes a
% required increase of 4 places in accuracy of the root at sqrt 5.
roots zz;
%{y= - 2.236067977,y=2.2360679774998,y=2.2360679775}

realroots zz; % should get the same answer from realroots.
%{y= - 2.2360679775,y=2.2360679774998,y=2.2360679775}


% 99) The same thing also happens when the root near sqrt 5 is on a
% different square-free factor.
zz := (y^2-5)^2*(y-2.2360679775);
roots zz;
%{y= - 2.236067977,y= - 2.236067977,y=2.2360679774998,
% y=2.2360679774998,y=2.2360679775}

realroots zz; % realroots handles this case also.
%{y= - 2.236067977,y= - 2.236067977,y=2.2360679774998,y=2.2360679774998,
% y=2.2360679775}

% 100)
rootacc 6;
zz := (y-i)*(x-2)*(1000000x-2000001); roots zz;
%{y= - 1.4142136,y=1.4142136,y= - 1.4142139,y=1.4142139,y=i}

% 101) this example requires accuracy 15.
zz:= (y-2)*(100000000000000y-200000000000001);
roots zz;
%{y=2.0,y=2.00000000000001}

% 102) still higher precision needed.
zz:= (y-2)*(10000000000000000000y-20000000000000000001); roots zz;
%{y=2.0,y=2.0000000000000000001}

% 103) increase in precision required for substituted polynomial.
zz:= (x-2)*(10000000000x-20000000001); roots zz;
%{y= - 1.41421356237,y=1.41421356237,y= - 1.41421356241,y=1.41421356241}

% 104) still higher precision required for substituted polynomial.
zz:= (x-2)*(100000000000000x-200000000000001); roots zz;
%{y= - 1.414213562373095,y=1.414213562373095,
% y= - 1.414213562373099,y=1.414213562373099}

% 105) accuracy must be increased to separate root of complex factor
% from root of real factor.
zz:=(9y-10)*(y-2)*(9y-10-9i/100000000); roots zz;
%{y=1.111111111,y=2.0,y=1.111111111 + 0.00000001*i}

% 106) realroots does the same accuracy increase for real root based
% upon the presence of a close complex root in the same polynomial.
% The reason for this might not be obvious unless roots is called.
realroots zz;
%{y=1.111111111,y=2.0}

% 107) realroots now uses powergcd logic whenever it is applicable.
zz := (x-1)*(x-2)*(x-3);  realroots zz;
%{y= - 1,y=1,y= - 1.41421,y=1.41421,y= - 1.73205,y=1.73205}

realroots(zz,exclude 1,2);
%{y=1.41421,y=1.73205}

% 108) root of degree 1 polynomial factor must be evaluated at
% precision 18 and accuracy 10 in order to separate it from a root of
% another real factor.
clear x; zz:=(9x-10)**2*(9x-10-9/100000000)*(x-2); roots zz;
%{x=1.111111111,x=1.111111111,x=1.111111121,x=2.0}

nearestroot(zz,1);
%{x=1.111111111}

nearestroot(zz,1.5);
%{x=1.111111121}

nearestroot(zz,1.65);
%{x=2.0}

% 108a) new cability in mod 1.94.
realroots zz;
%{x=1.111111111,x=1.111111111,x=1.111111121,x=2.0}

% 109) in this example, precision >=40 is used and two roots need to be
% found to accuracy 16 and two to accuracy 14.
zz := (9x-10)*(7x-8)*(9x-10-9/10**12)*(7x-8-7/10**14); roots zz;
%{x=1.1111111111111,x=1.1111111111121,
% x=1.142857142857143,x=1.142857142857153}

% 110) very small real or imaginary parts of roots require high
% precision or exact computations, or they will be lost or incorrectly
% found.
zz := 1000000*r**18 + 250000000000*r**4 - 1000000*r**2 + 1; roots zz;
%{r=2.42978*i,r=-2.42978*i,
% r=-1.05424 - 2.18916*i,r=1.05424 + 2.18916*i,
% r=-1.05424 + 2.18916*i,r=1.05424 - 2.18916*i,
% r=-0.00141421 - 1.6E-26*i,r=0.00141421 + 1.6E-26*i,
% r=-0.00141421 + 1.6E-26*i,r=0.00141421 - 1.6E-26*i,
% r=-1.89968 - 1.51494*i,r=1.89968 + 1.51494*i,
% r=-1.89968 + 1.51494*i,r=1.89968 - 1.51494*i,
% r=-2.36886 - 0.540677*i,r=2.36886 + 0.540677*i,
% r=-2.36886 + 0.540677*i,r=2.36886 - 0.540677*i}


comment  These five examples are very difficult root finding problems
  for automatic root finding (not employing problem-specific
  procedures.) They require extremely high precision and high accuracy
  to separate almost multiple roots (multiplicity broken by a small high
  order perturbation.)  The examples are roughly in ascending order of
  difficulty.;

% 111) Two simple complex roots with extremely small real separation.
c := 10^-6;
zz:=(x-3c^2)^2+i*c*x^7; roots zz;
%{x=-15.0732 + 4.89759*i,x=-9.31577 - 12.8221*i,x=-1.2E-12 + 15.8489*i,
% x=2.99999999999999999999999999999997E-12
%    + 3.3068111527572904325663335008527E-44*i,
% x=3.00000000000000000000000000000003E-12
%    - 3.30681115275729043256633350085321E-44*i,
% x=9.31577 - 12.8221*i,x=15.0732 + 4.89759*i}

% 112) Four simple complex roots in two close sets.
c := 10^-4;
zz:=(x^2-3c^2)^2+i*c^2*x^9; roots zz;
%{x=-37.8622 + 12.3022*i,x=-23.4002 - 32.2075*i,
% x=-0.00017320508075689 - 2.41778234660324E-18*i,
% x=-0.000173205080756885 + 2.4177823466027E-18*i,
% x=39.8107*i,
% x=0.000173205080756885 + 2.4177823466027E-18*i,
% x=0.00017320508075689 - 2.41778234660324E-18*i,
% x=23.4002 - 32.2075*i,x=37.8622 + 12.3022*i}

% 113) Same example, but with higher minimum root accuracy specified.
rootacc 20;
roots zz;
%{x=-37.862241873586290526 + 12.302188128448775345*i,
% x=-23.400152368145827118 - 32.207546656274351069*i,
% x=-0.00017320508075689014714 - 2.417782346603239319E-18*i,
% x=-0.00017320508075688531157 + 2.417782346602699319E-18*i,
% x=39.810717055651151449*i,
% x=0.00017320508075688531157 + 2.417782346602699319E-18*i,
% x=0.00017320508075689014714 - 2.417782346603239319E-18*i,
% x=23.400152368145827118 - 32.207546656274351069*i,
% x=37.862241873586290526 + 12.302188128448775345*i}

precision reset;
% This resets precision and rootacc to nominal.

% 114) Two extremely close real roots plus a complex pair with extremely
% small imaginary part.
c := 10^6;
zz:=(c^2*x^2-3)^2+c^2*x^9; roots zz;
%{x= - 251.189,x=-77.6216 + 238.895*i,x=-77.6216 - 238.895*i,
% x= - 0.000001732050807568877293531,
% x= - 0.000001732050807568877293524,
% x=0.00000173205 + 3.41926E-27*i,x=0.00000173205 - 3.41926E-27*i,
% x=203.216 + 147.645*i,x=203.216 - 147.645*i}

% 114a) this example is a critical test for realroots as well.
realroots zz;
%{x= - 251.189,x= - 0.000001732050807568877293531,
% x= - 0.000001732050807568877293524}

% 115) Four simple complex roots in two extremely close sets.
c := 10^6;
zz:=(c^2*x^2-3)^2+i*c^2*x^9; roots zz;
%{x=-238.895 + 77.6216*i,x=-147.645 - 203.216*i,
% x=-0.00000173205080756887729353 - 2.417782346602969319022E-27*i,
% x=-0.000001732050807568877293525 + 2.417782346602969318968E-27*i,
% x=251.189*i,
% x=0.000001732050807568877293525 + 2.417782346602969318968E-27*i,
% x=0.00000173205080756887729353 - 2.417782346602969319022E-27*i,
% x=147.645 - 203.216*i,x=238.895 + 77.6216*i}


% 116) A new "hardest example" type.  This polynomial has two sets of
% extremely close real roots and two sets of extremely close conjugate
% complex roots, both large and small, with the maximum accuracy and
% precision required for the largest roots.  Three restarts are
% required, at progressively higher precision, to find all roots.
 % (to run this example, uncomment the following two lines.)
%**% zz1:= (10^12x^2-sqrt 2)^2+x^7$ zz2:= (10^12x^2+sqrt 2)^2+x^7$
%**% zzzz := zz1*zz2$ roots zzzz;
%{x= - 1.00000000000000000000000000009E+8,
% x= - 9.99999999999999999999999999906E+7,
% x= - 0.0000011892071150027210667183,
% x= - 0.0000011892071150027210667167,
% x=-5.4525386633262882960501E-28 + 0.000001189207115002721066718*i,
% x=-5.4525386633262882960501E-28 - 0.000001189207115002721066718*i,
% x=5.4525386633262882960201E-28 + 0.000001189207115002721066717*i,
% x=5.4525386633262882960201E-28 - 0.000001189207115002721066717*i,
% x=0.00000118921 + 7.71105E-28*i,
% x=0.00000118921 - 7.71105E-28*i,
% x=4.99999999999999999999999999953E+7
%    + 8.66025403784438646763723170835E+7*i,
% x=4.99999999999999999999999999953E+7
%    - 8.66025403784438646763723170835E+7*i,
% x=5.00000000000000000000000000047E+7
%    + 8.66025403784438646763723170671E+7*i,
% x=5.00000000000000000000000000047E+7
%    - 8.66025403784438646763723170671E+7*i}

% Realroots strategy on this example is different, but determining the
% necessary precision and accuracy is tricky.
%**% realroots zzzz;
%{x= - 1.00000000000000000000000000009E+8,
% x= - 9.9999999999999999999999999991E+7,
% x= - 0.0000011892071150027210667183,
% x= - 0.0000011892071150027210667167}

% 117) multroot examples.  Multroot can be called directly, or it can be
% called by giving roots or realroots a list of polynomials as argument.
% Here, multroot is called directly.  Realroots is used unless the switch
% compxroots is on.  In this example, p1 must be computed at accuracy 33
% in order to yield an accuracy of 20 for p2.
res :=          % Structure is {eq1(p1,p2),eq2(p1)}
{ - 65193331905902035840886401184447471772856493442267717*P1**13
- 1664429561324832520726401259146912155464247056480012434*P1**12
- 6261475374084274810766056740641579522309310708502887990*P1**11
+ 58050875148721867394302891225676265051604299348469583622*P1**10
- 25149162547648105419319267662238682603649922079217227285*P1**9
- 440495842372965561251919788209759089436362766115660350108*P1**8
+ 1031835865631194068430476093579502290454870220388968336688*P1**7
- 176560168441303582471783015188457142709772508915411137856*P1**6
- 3394297397883799767380936436924078166849454318674637153232*P1**5
+ 8572159983028240622274769676964404195355003175115163884096*P1**4
- 11689989317682872105592244166702248132836279639925035950656*P1**3
+ 9646776768609439752430866001814626337809195004192011294976*P1**2
- 4455646388442119339178004445898515058096390082146233345536*P1
+ 4709370575236909034773453200518274143851133066819671040*P2
+ 886058257542744466307567014351806947093655767531394713600,
53271*P1**14 + 1393662*P1**13 + 6077030*P1**12 - 41382626*P1**11
+ 6240255*P1**10 + 313751524*P1**9 - 698694844*P1**8
+ 134987928*P1**7 + 2322386256*P1**6 - 6102636608*P1**5
+ 8722164608*P1**4 - 7907887488*P1**3 + 4508378368*P1**2
- 1477342720*P1 + 213248000}$
multroot(20,res);
%{{p1= - 16.330244199212269912,p2= - 12.905402440394357204},{p1
%    = - 13.071850241794867852,p2= - 20.369934278813005573}}

% 118)  structure is {p1(x1,x3,x4),p2(x2,x4),p3(x2,x3,x4),p4(x4)}
h := {36439926476029643745*x1 + 36439926476029643745*x3
- 966689910765785535050240000*x4**17
+ 2589213991952971388822784000*x4**16
- 1455736281904024746728256000*x4**15
- 1114734065976529083327407360*x4**14
+ 720240539282202478990426752*x4**13
+ 419779761544955697624679296*x4**12
- 168749980172837712266699840*x4**11
+ 290913179471491189688854560*x4**10 - 432958804125555395247740688*x4**9 
+ 10386593827154614897599504*x4**8 + 155547361883654478618679440*x4**7
- 31113996003728470659075480*x4**6 - 41175755320900503555096780*x4**5
+ 33003268068791208924709740*x4**4 - 6778828915691466390091200*x4**3
- 1496167017611703417373950*x4**2 + 149688116448660711183825*x4
+ 138148004064999041884935,
- 36439926476029643745*x2 - 784034192593211415232000000*x4**17
+ 2099814921874128508369920000*x4**16
- 1180307545285783973854272000*x4**15
- 904159020650675303719168000*x4**14
+ 583907514538684395627559680*x4**13
+ 340458856280381353403249664*x4**12
- 136785894094420325707236352*x4**11
+ 235962131906791901454310848*x4**10
- 351090033711917923140908256*x4**9 + 8379974606095284871931520*x4**8
+ 126131069262992237456374584*x4**7 -25220359028157888406315896*x4**6
- 33393008746801847984243640*x4**5 + 26761347051713933045852160*x4**4
- 5495296446381334401240210*x4**3 - 1213098761225775782417310*x4**2
+ 121243165959568584810870*x4 + 112046752277725240396125,
145759705904118574980*x3**2 - 3866759643063142140200960000*x3*x4**17
+ 10356855967811885555291136000*x3*x4**16
- 5822945127616098986913024000*x3*x4**15
- 4458936263906116333309629440*x3*x4**14
+ 2880962157128809915961707008*x3*x4**13
+ 1679119046179822790498717184*x3*x4**12
- 674999920691350849066799360*x3*x4**11
+ 1163652717885964758755418240*x3*x4**10
- 1731835216502221580990962752*x3*x4**9
+ 41546375308618459590398016*x3*x4**8
+ 622189447534617914474717760*x3*x4**7
- 124455984014913882636301920*x3*x4**6
- 164703021283602014220387120*x3*x4**5
+ 132013072275164835698838960*x3*x4**4
- 27115315662765865560364800*x3*x4**3
- 5984668070446813669495800*x3*x4**2 + 598752465794642844735300*x3*x4
+ 552592016259996167539740*x3 + 3550270013715070172487680000*x4**17
- 9573649159583488469933568000*x4**16
+ 5464438450196473162575360000*x4**15
+ 4096921924516221821604523520*x4**14
- 2717026023466705910519606784*x4**13
- 1554544907157405816469959168*x4**12
+ 636859360057972319632500992*x4**11
- 1065163663567422851531986944*x4**10
+ 1612243029585251439302638656*x4**9
- 48252032958282805311135168*x4**8 - 579133322758350220074700320*x4**7
+ 117976179842506552019678280*x4**6 + 152287445048713077301910400*x4**5 
- 123053170142513516618082960*x4**4 + 25533441675517563881962200*x4**3
+ 5583415080801636858130200*x4**2 - 574247940288215661001800*x4
- 518304795930023609925945,
- 5120000*x4**18 + 18432000*x4**17 - 20352000*x4**16 + 1208320*x4**15
+ 9255936*x4**14 - 1296384*x4**13 - 2943488*x4**12 + 2365440*x4**11
- 3712896*x4**10 + 2169600*x4**9 + 772560*x4**8 - 924480*x4**7
- 66000*x4**6 + 375840*x4**5 - 197100*x4**4 + 25200*x4**3 + 8100*x4**2
- 675}$
multroot(20,h);
%{{x1= - 0.12444800707566022364,x2=0.40264591905223704246,x3
%  =0.70281784593572688134,x4=0.92049796029182926078},
% {x1= - 0.12444800707566022364,x2=0.92049796029182926078,x3
%  =0.70281784593572688134,x4=0.40264591905223704246},
% {x1=0.22075230018295426413,x2=0.48100256896929398759,x3
%  =0.74057635603986743051,x4=0.93049526909398804249},
% {x1=0.22075230018295426413,x2=0.93049526909398804249,x3
%  =0.74057635603986743051,x4=0.48100256896929398759},
% {x1=0.70281784593572688134,x2=0.40264591905223704246,x3
%  = - 0.12444800707566022364,x4=0.92049796029182926078},
% {x1=0.70281784593572688134,x2=0.92049796029182926078,x3
%  = - 0.12444800707566022364,x4=0.40264591905223704246},
% {x1=0.74057635603986743051,x2=0.48100256896929398759,x3
%  =0.22075230018295426413,x4=0.93049526909398804249},
% {x1=0.74057635603986743051,x2=0.93049526909398804249,x3
%  =0.22075230018295426413,x4=0.48100256896929398759}}

% System precision will have been set to 20 in the two previous
% examples.  In the following examples, the roots will be given to
% accuracy 12, because rootacc 12; was input.  If rootacc had not been
% input, the roots would be given at system precision, which could
% different answers on different systems if precision had been reset,
% or else it would have been 20 because of example 118).

rootacc 12;

% 119)
ss := {x^2-2,y^2-x^2,z^2-x-y}; % structure is {p1(x),p2(x,y),p3(x,y,z)}
realroots ss;
%{{x= - 1.41421356237,y=1.41421356237,z=0},
% {x= - 1.41421356237,y=1.41421356237,z=0},
% {x=1.41421356237,y= - 1.41421356237,z=0},
% {x=1.41421356237,y= - 1.41421356237,z=0},
% {x=1.41421356237,y=1.41421356237,z= - 1.68179283051},
% {x=1.41421356237,y=1.41421356237,z=1.68179283051}}

roots ss;
%{{x= - 1.41421356237,y= - 1.41421356237,z=1.68179283051*i},
% {x= - 1.41421356237,y= - 1.41421356237,z= - 1.68179283051*i},
% {x= - 1.41421356237,y=1.41421356237,z=0},
% {x= - 1.41421356237,y=1.41421356237,z=0},
% {x=1.41421356237,y= - 1.41421356237,z=0},
% {x=1.41421356237,y= - 1.41421356237,z=0},
% {x=1.41421356237,y=1.41421356237,z= - 1.68179283051},
% {x=1.41421356237,y=1.41421356237,z=1.68179283051}}

% 120)
realroots {x^5-45x+2,y^2-x+1};
%{{x=2.57878769906,y= - 1.25649818904},{x=2.57878769906,y
%    =1.25649818904}}

realroots {x^5-45x+2,y^2-x-1};
%{{x=0.0444444482981,y= - 1.02198064967},
% {x=0.0444444482981,y=1.02198064967},
% {x=2.57878769906,y= - 1.89176840524},
% {x=2.57878769906,y=1.89176840524}}

% 121)
realroots {x^2-2,y^2+x^2};
% {}

roots {x^2+2,y^2-x^2};
%{{x=1.41421356237*i,y=1.41421356237*i},
% {x=1.41421356237*i,y= - 1.41421356237*i},
% {x= - 1.41421356237*i,y=1.41421356237*i},
% {x= - 1.41421356237*i,y= - 1.41421356237*i}}

% 122)
roots {x^2-y^2,x^2+y^2+3};
%multroot fails because no univariate polynomial was given.
%multroot(12,{x**2 - y**2,x**2 + y**2 + 3})$

% 122a)
roots{x^2+y^2,x^2-y^2-z,z^2-z-1};
%*** multroot failure: at least one polynomial has no single base.
%multroot(12,{x**2 + y**2,(x**2 - y**2) - z,(z**2 - z) - 1})$

% 123)
roots {x^2-2,y^2+3,x+z-2,y-z+2};
%{}

% 124)
zz := {x^5-5x+3,x^2+y^2,x^3+z^3};
realroots zz;
%{}

realroots {x^5-5x+3,x^2-y^2,x^3+z^3};
%{{x= - 1.61803398875,y= - 1.61803398875,z=1.61803398875},
% {x= - 1.61803398875,y=1.61803398875,z=1.61803398875},
% {x=0.61803398875,y= - 0.61803398875,z= - 0.61803398875},
% {x=0.61803398875,y=0.61803398875,z= - 0.61803398875},
% {x=1.27568220365,y= - 1.27568220365,z= - 1.27568220365},
% {x=1.27568220365,y=1.27568220365,z= - 1.27568220365}}

% These show previous capability
%------------------------------------------------------------------
% These are new capability

% 125)
roots{x**2 - x - y,x*y - 2*y,y**2 - 2*y};
%{{x=0,y=0},{x=1,y=0},{x=2.0,y=2.0}}

% 126)
roots({x^2-9,y^3-27,x*y+9});
%{{x= - 3.0,y=3.0}}

% 127)
multroot(12,{y^2-z,y*z,z*(z-1)});
%{{y=0,z=0},{y=0,z=0}}

% 127a)
multroot(12,{y^2-z,y*z,z*(z-1),x^2-x-y});
%{{x=0,y=0,z=0},
% {x=0,y=0,z=0},
% {x=1,y=0,z=0},
% {x=1,y=0,z=0}}

% 128)
roots{y*z,z*(z-1)};
%{{z=0},{y=0,z=1}}

% 129)
zzl := {z*(z-1)*(z-2),(z-2)*z*y^2+(z-1)*z*y+z+1};
roots zzl;
%{{y= - 1.5,z=2.0},
% {y= - 1.41421356237,z=1},
% {y=1.41421356237,z=1}}

% 129a)
zzla := {z*(z-1)*(z-2),(z-2)*z*y^2+(z-1)*z*y+z+1,x^2-x-y};
roots zzla;
%{{x= - 0.790044015673,y=1.41421356237,z=1},
% {x=0.5 + 1.11803398875*i,y= - 1.5,z=2.0},
% {x=0.5 - 1.11803398875*i,y= - 1.5,z=2.0},
% {x=0.5 + 1.07898728555*i,y= - 1.41421356237,z=1},
% {x=0.5 - 1.07898728555*i,y= - 1.41421356237,z=1},
% {x=1.79004401567,y=1.41421356237,z=1}}

% 130)
zzl0 := {z*(z-1)*(z-2),(z-2)*z*y^2+(z-1)*z*y+z};
roots zzl0;
%{{y=-1,z=1},{y=-1,z=2.0},{z=0},{y=1,z=1}}

% 131)
zzl3a := {z*(z-1)*(z-2),(z-2)*z*y^2+(z-1)*z*y+z,x^2+y*x*z+z};
roots zzl3a;
%{{x=0.866025403784*i - 0.5,y=1,z=1},
% {x= - 0.866025403784*i - 0.5,y=1,z=1},
% {x=0,z=0},
% {x=0,z=0},
% {x=0.866025403784*i + 0.5,y=-1,z=1},
% {x= - 0.866025403784*i + 0.5,y=-1,z=1},
% {x=i + 1,y=-1,z=2.0},
% {x= - i + 1,y=-1,z=2.0}}$

% 132)
zzl3c := {z*(z-1)*(z-2),(z-2)*z*y^2+(z-1)*z*y+z,x^2+y*x+z};
roots zzl3c;
%*** for some root value, a variable dependends on an arbitrary variable
%multroot(12,{z**3 - 3*z**2 + 2*z,y**2*z**2 - 2*y**2*z + y*z**2 - y*z + z,
% x**2 + x*y + z})$

% 133)
xyz := {x^2-x-2,y^2+y,x^3+y^3+z+5};
roots xyz;
%{{x=-1,y=-1,z= - 3.0},
% {x=-1,y=0,z= - 4.0},
% {x=2.0,y=-1,z= - 12.0},
% {x=2.0,y=0,z= - 13.0}}

% 134) here, we had to eliminate a spurious imaginary part of z.
axyz := {a-1,a+x^2-x-2,a+y^2+y,a+x^3+y^3+z+5};
roots axyz;
%{{a=1,x= - 0.61803398875,y= - 0.5 + 0.866025403784*i,z= - 6.7639320225},
% {a=1,x= - 0.61803398875,y= - 0.5 - 0.866025403784*i,z= - 6.7639320225},
% {a=1,x=1.61803398875,y= - 0.5 + 0.866025403784*i,z= - 11.2360679775},
% {a=1,x=1.61803398875,y= - 0.5 - 0.866025403784*i,z= - 11.2360679775}}

% 134a) here, we had to eliminate a spurious real part of x.
roots{y^4+y^3+y^2+y+1,x^2+3*y^5+2};
%{{x=2.2360679775*i,y= - 0.809016994375 + 0.587785252292*i},
% {x=-2.2360679775*i,y= - 0.809016994375 + 0.587785252292*i},
% {x=-2.2360679775*i,y= - 0.809016994375 - 0.587785252292*i},
% {x=2.2360679775*i,y= - 0.809016994375 - 0.587785252292*i},
% {x=-2.2360679775*i,y=0.309016994375 + 0.951056516295*i},
% {x=2.2360679775*i,y=0.309016994375 + 0.951056516295*i},
% {x=2.2360679775*i,y=0.309016994375 - 0.951056516295*i},
% {x=-2.2360679775*i,y=0.309016994375 - 0.951056516295*i}}

% 135)
axyz2 := {a-1,a-1+x^2-x-2,a-1+y^2+y,x^3+y^3+z+5};
roots axyz2;
%{{a=1,x=-1,y=-1,z= - 3.0},
% {a=1,x=-1,y=0,z= - 4.0},
% {a=1,x=2.0,y=-1,z= - 12.0},
% {a=1,x=2.0,y=0,z= - 13.0}}

zyxa2 := reverse axyz2;
roots zyxa2;
% (same as above)

% 137)
rsxuv := {u^2+u*r+s*x*v,s+r^2,x-r-2,r+v,v^2-v-6};
roots rsxuv;
%{{r= - 3.0,s= - 9.0,u=1.5 + 4.97493718553*i,v=3.0,x=-1},
% {r= - 3.0,s= - 9.0,u=1.5 - 4.97493718553*i,v=3.0,x=-1},
% {r=2.0,s= - 4.0,u= - 1 + 5.56776436283*i,v= - 2.0,x=4.0},
% {r=2.0,s= - 4.0,u= - 1 - 5.56776436283*i,v= - 2.0,x=4.0}}

% 138)
rsxuv2 := {u^2+u*r+s*x,s+r,x-r-2,r+v,v^2-v-6};
roots rsxuv2;
%{{r= - 3.0,s=3.0,u= - 0.791287847478,v=3.0,x=-1},
% {r= - 3.0,s=3.0,u=3.79128784748,v=3.0,x=-1},
% {r=2.0,s= - 2.0,u= - 4.0,v= - 2.0,x=4.0},
% {r=2.0,s= - 2.0,u=2.0,v= - 2.0,x=4.0}}

% 139) combining both types of capabilities.
axyz3 := {a-1,a-1+x^2-x-2,a-1+y^2+y,x^3+y^3+z+5,y^2-x^2};
roots axyz3;
%{{a=1,x=-1,y=-1,z= - 3.0}}

% 140) spurious real and imag. parts had to be eliminated from z and y.
ayz := {a^2+a+1,z^2+a^3+3,y^3-z^2};
roots ayz;
%{{a= - 0.5 + 0.866025403784*i,y= - 1.58740105197,z=2.0*i},
% {a= - 0.5 + 0.866025403784*i,y= - 1.58740105197,z=-2.0*i},
% {a= - 0.5 - 0.866025403784*i,y= - 1.58740105197,z=-2.0*i},
% {a= - 0.5 - 0.866025403784*i,y= - 1.58740105197,z=2.0*i},
% {a= - 0.5 + 0.866025403784*i,y=0.793700525984 + 1.374729637*i,z=2.0*i},
% {a= - 0.5 + 0.866025403784*i,y=0.793700525984 - 1.374729637*i,z=2.0*i},
% {a= - 0.5 + 0.866025403784*i,y=0.793700525984 + 1.374729637*i,z=-2.0*i},
% {a= - 0.5 + 0.866025403784*i,y=0.793700525984 - 1.374729637*i,z=-2.0*i},
% {a= - 0.5 - 0.866025403784*i,y=0.793700525984 - 1.374729637*i,z=-2.0*i},
% {a= - 0.5 - 0.866025403784*i,y=0.793700525984 + 1.374729637*i,z=-2.0*i},
% {a= - 0.5 - 0.866025403784*i,y=0.793700525984 - 1.374729637*i,z=2.0*i},
% {a= - 0.5 - 0.866025403784*i,y=0.793700525984 + 1.374729637*i,z=2.0*i}}

% 141) some small real or imaginary parts are not spurious; they are kept.
zz:= {x**9-9999x**2-0.01,y^2+y-x};
roots zz;
%{{x= - 3.35839794887 + 1.61731917877*i,y= - 0.944735689647 -
%  1.81829254591*i},
% {x= - 3.35839794887 - 1.61731917877*i,y= - 0.944735689647 +
%  1.81829254591*i},
% {x= - 3.35839794887 + 1.61731917877*i,y= - 0.0552643103532 +
%  1.81829254591*i},
% {x= - 3.35839794887 - 1.61731917877*i,y= - 0.0552643103532 -
%  1.81829254591*i},
% {x= - 0.829455794538 + 3.6340832074*i,y= - 1.74509731832 -
%  1.45935709359*i},
% {x= - 0.829455794538 - 3.6340832074*i,y= - 1.74509731832 +
%  1.45935709359*i},
% {x= - 0.829455794538 + 3.6340832074*i,y=0.745097318317 +
%  1.45935709359*i},
% {x= - 0.829455794538 - 3.6340832074*i,y=0.745097318317 -
%  1.45935709359*i},
% {x=5.00250075018E-29 + 0.00100005000375*i,y= - 1.0000010001 -
%  0.00100004800346*i},
% {x=5.00250075018E-29 - 0.00100005000375*i,y= - 1.0000010001 +
%  0.00100004800346*i},
% {x=5.00250075018E-29 + 0.00100005000375*i,y=0.00000100009500904 +
%  0.00100004800346*i},
% {x=5.00250075018E-29 - 0.00100005000375*i,y=0.00000100009500904 -
%  0.00100004800346*i},
% {x=2.3240834909 + 2.91430845907*i,y= - 2.29755558063 -
%  0.810630973104*i},
% {x=2.3240834909 - 2.91430845907*i,y= - 2.29755558063 +
%  0.810630973104*i},
% {x=2.3240834909 + 2.91430845907*i,y=1.29755558063 + 0.810630973104*i},
% {x=2.3240834909 - 2.91430845907*i,y=1.29755558063 - 0.810630973104*i},
% {x=3.72754050502,y= - 2.49437722235},
% {x=3.72754050502,y=1.49437722235}}$

% 142) if quotient, only numerator is used as polynomial, so this works.
vv := {x+1+1/x,y^2-x^3};
roots vv;
%{{x= - 0.5 + 0.866025403784*i,y=-1},{x= - 0.5 - 0.866025403784*i,y=-1},
% {x= - 0.5 + 0.866025403784*i,y=1},{x= - 0.5 - 0.866025403784*i,y=1}}

% 143) and this also works.
ii := {x^2-2x+3/r,r^3-5};
roots ii;
%{{r= - 0.854987973338 + 1.48088260968*i,x= - 0.464963274745 -
%  0.518567329174*i},
% {r= - 0.854987973338 - 1.48088260968*i,x= - 0.464963274745 +
%  0.518567329174*i},
% {r= - 0.854987973338 + 1.48088260968*i,x=2.46496327474 +
%  0.518567329174*i},
% {r= - 0.854987973338 - 1.48088260968*i,x=2.46496327474 -
%  0.518567329174*i},
% {r=1.70997594668,x=1 + 0.868568156754*i},
% {r=1.70997594668,x=1 - 0.868568156754*i}}

% 144)
bb := {y+x+3,x^2+r+s-3,x^3+r+s-7,r^2-r,s^2+3s+2};
roots bb;
%{{r=0,s=-1,x=2.0,y= - 5.0},{r=1,s= - 2.0,x=2.0,y= - 5.0}}

end;
