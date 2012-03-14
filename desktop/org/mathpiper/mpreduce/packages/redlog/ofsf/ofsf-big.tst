% ----------------------------------------------------------------------
% $Id: ofsf.tst 469 2009-11-28 13:58:18Z arthurcnorman $
% ----------------------------------------------------------------------
% Copyright (c) 2006-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

load_package redlog;
rlset ofsf$
off rlverbose;

%%% Testing rlqe (xopt-qe)
mtp3 :=
ex(x111,ex(x112,ex(x113,ex(x121,ex(x122,ex(x123,ex(x131,ex(x132,ex(x133,
ex(x211,ex(x212,ex(x213,ex(x221,ex(x222,ex(x223,ex(x231,ex(x232,ex(x233,
ex(x311,ex(x312,ex(x313,ex(x321,ex(x322,ex(x323,ex(x331,ex(x332,ex(x333,
x111+x211+x311=a11 and x112+x212+x312=a12 and x113+x213+x313=a13 and
x121+x221+x321=a21 and x122+x222+x322=a22 and x123+x223+x323=a23 and
x131+x231+x331=a31 and x132+x232+x332=a32 and x133+x233+x333=a33 and
x111+x121+x131=b11 and x112+x122+x132=b12 and x113+x123+x133=b13 and
x211+x221+x231=b21 and x212+x222+x232=b22 and x213+x223+x233=b23 and
x311+x321+x331=b31 and x312+x322+x332=b32 and x313+x323+x333=b33 and
x111+x112+x113=c11 and x121+x122+x123=c12 and x131+x132+x133=c13 and
x211+x212+x213=c21 and x221+x222+x223=c22 and x231+x232+x233=c23 and
x311+x312+x313=c31 and x321+x322+x323=c32 and x331+x332+x333=c33 and
0 leq x111 and 0 leq x112 and 0 leq x113 and
0 leq x121 and 0 leq x122 and 0 leq x123 and
0 leq x131 and 0 leq x132 and 0 leq x133 and
0 leq x211 and 0 leq x212 and 0 leq x213 and
0 leq x221 and 0 leq x222 and 0 leq x223 and
0 leq x231 and 0 leq x232 and 0 leq x233 and
0 leq x311 and 0 leq x312 and 0 leq x313 and
0 leq x321 and 0 leq x322 and 0 leq x323 and
0 leq x331 and 0 leq x332 and 0 leq x333)))))))))))))))))))))))))))$
rlqe mtp3$
rlatnum ws;

%%% Test rlqe (linear)

% Rectangle Problem. rp3 with b normalized to 1.
rp3 := ex(x1,ex(x2,ex(x3,ex(y1,ex(y2,ex(y3,ex(d2,ex(d3,all(u,all(v,a -
d1 - x1 >= 0 and a - d2 - x2 >= 0 and a - d3 - x3 >= 0 and a > 0 and d1
- d2 < 0 and d1 + y1 - 1 <= 0 and d1 > 0 and d2 - d3 < 0 and d2 + y2 - 1
 <= 0 and d3 + y3 - 1 <= 0 and x1 >= 0 and x2 >= 0 and x3 >= 0 and y1 >=
 0 and y2 >= 0 and y3 >= 0 and not(d2 - x1 + x2 > 0 and d2 - y1 + y2 > 0
 and x1 - x2 >= 0 and y1 - y2 >= 0 or d1 - d2 + x1 - x2 < 0 and d1 + x1
- x2 >= 0 and d2 - y1 + y2 > 0 and y1 - y2 >= 0 or d1 - d2 + y1 - y2 < 0
 and d1 + y1 - y2 >= 0 and d2 - x1 + x2 > 0 and x1 - x2 >= 0 or d1 - d2
+ x1 - x2 < 0 and d1 - d2 + y1 - y2 < 0 and d1 + x1 - x2 >= 0 and d1 +
y1 - y2 >= 0 or d3 - x1 + x3 > 0 and d3 - y1 + y3 > 0 and x1 - x3 >= 0
and y1 - y3 >= 0 or d1 - d3 + x1 - x3 < 0 and d1 + x1 - x3 >= 0 and d3 -
 y1 + y3 > 0 and y1 - y3 >= 0 or d1 - d3 + y1 - y3 < 0 and d1 + y1 - y3
>= 0 and d3 - x1 + x3 > 0 and x1 - x3 >= 0 or d1 - d3 + x1 - x3 < 0 and
d1 - d3 + y1 - y3 < 0 and d1 + x1 - x3 >= 0 and d1 + y1 - y3 >= 0 or d1
+ x1 - x2 > 0 and d1 + y1 - y2 > 0 and x1 - x2 <= 0 and y1 - y2 <= 0 or
d1 - d2 + x1 - x2 > 0 and d1 + y1 - y2 > 0 and d2 - x1 + x2 >= 0 and y1
- y2 <= 0 or d1 - d2 + y1 - y2 > 0 and d1 + x1 - x2 > 0 and d2 - y1 + y2
 >= 0 and x1 - x2 <= 0 or d1 - d2 + x1 - x2 > 0 and d1 - d2 + y1 - y2 >
0 and d2 - x1 + x2 >= 0 and d2 - y1 + y2 >= 0 or d3 - x2 + x3 > 0 and d3
 - y2 + y3 > 0 and x2 - x3 >= 0 and y2 - y3 >= 0 or d2 - d3 + x2 - x3 <
0 and d2 + x2 - x3 >= 0 and d3 - y2 + y3 > 0 and y2 - y3 >= 0 or d2 - d3
 + y2 - y3 < 0 and d2 + y2 - y3 >= 0 and d3 - x2 + x3 > 0 and x2 - x3 >=
 0 or d2 - d3 + x2 - x3 < 0 and d2 - d3 + y2 - y3 < 0 and d2 + x2 - x3
>= 0 and d2 + y2 - y3 >= 0 or d1 + x1 - x3 > 0 and d1 + y1 - y3 > 0 and
x1 - x3 <= 0 and y1 - y3 <= 0 or d1 - d3 + x1 - x3 > 0 and d1 + y1 - y3
> 0 and d3 - x1 + x3 >= 0 and y1 - y3 <= 0 or d1 - d3 + y1 - y3 > 0 and
d1 + x1 - x3 > 0 and d3 - y1 + y3 >= 0 and x1 - x3 <= 0 or d1 - d3 + x1
- x3 > 0 and d1 - d3 + y1 - y3 > 0 and d3 - x1 + x3 >= 0 and d3 - y1 +
y3 >= 0 or d2 + x2 - x3 > 0 and d2 + y2 - y3 > 0 and x2 - x3 <= 0 and y2
 - y3 <= 0 or d2 - d3 + x2 - x3 > 0 and d2 + y2 - y3 > 0 and d3 - x2 +
x3 >= 0 and y2 - y3 <= 0 or d2 - d3 + y2 - y3 > 0 and d2 + x2 - x3 > 0
and d3 - y2 + y3 >= 0 and x2 - x3 <= 0 or d2 - d3 + x2 - x3 > 0 and d2 -
 d3 + y2 - y3 > 0 and d3 - x2 + x3 >= 0 and d3 - y2 + y3 >= 0) and (a -
u > 0 and u >= 0 and v - 1 < 0 and v >= 0 impl d1 - u + x1 > 0 and d1 -
v + y1 > 0 and u - x1 >= 0 and v - y1 >= 0 or d2 - u + x2 > 0 and d2 - v
+ y2 > 0 and u - x2 >= 0 and v - y2 >= 0 or d3 - u + x3 > 0 and d3 - v
+ y3 > 0 and u - x3 >= 0 and v - y3 >= 0)))))))))))$
rp3sol := rlqe rp3;

%%% Testing rlqe (DFS)

% Prolog III. Communications of the ACM. 33(7), 70-90, July 1990
% Description of the problem on p.79: Consider the infinite sequence
% of real numbers defined by x_{i+2}:=|x_{i+1}|-x_i where $x_1$ and
% $x_2$ are arbitrary numbers. Our aim is to show that this sequence
% is always periodic and that the period is 9, in other words that the
% sequences $x_1, x_2, \dots$ and $x_{1+9}, x_{2+9}, \dots$ are always
% identical.
p9 := rlall((for i:=1:9 mkand
   mkid(x,i+1)>=0 and mkid(x,i+2)=mkid(x,i+1)-mkid(x,i) or
   mkid(x,i+1)<0 and mkid(x,i+2)=-mkid(x,i+1)-mkid(x,i))
      impl x1=x10 and x2=x11)$
rlqe p9;

%%% Testing rlcad

% Wilkinson's polynomial. Tests how fast root isolation is.
wilk := ex(x,8388608*x**20 + 1761607681*x**19 + 172931153920*x**18 +
10543221964800*x**17 + 447347234439168*x**16 +
14028108264898560*x**15 + 336985244869591040*x**14 +
6342720331186176000*x**13 + 94877480085669019648*x**12 +
1137370949952460554240 *x**11 + 10968398649699241820160*x**10 +
85079777790228273561600*x**9 + 528740774622641958944768 *x**8 +
2611655889692786813829120*x**7 + 10122095419974470210682880*x**6 +
30198816984091441338777600 *x**5 + 67426052557934862488567808*x**4 +
107969196810523545855590400*x**3 + 115794329499468438700032000 *x**2 +
73425049924762651852800000*x + 20408661249006627717120000 = 0
and -20<=x<=-10)$
rlcad wilk;

% Convexity of unit square
as5v := all({x1,y1,x2,y2},(0<x1<1 and 0<y1<1 and 0<x2<1 and 0<y2<1) impl
all(l, (0<l and l<1) impl (0<x1+l*(-x1+x2)<1 and 0<y1+l*(-y1+y2)<1)))$
rlcad as5v;

% Consistency
con := ex({z,x,y},x**2+y**2+z**2<1 and x**2+(y+z-2)**2<1)$
rlcad con;

% Condition on quartic polynomial being positive semidefinite.
quartic := all(x,x**4+p*x**2+q*x+r>=0)$
quarticneg := not quartic$
rlcad quartic;
on rlcadfulldimonly;
rlcad quarticneg;
off rlcadfulldimonly;

% Solotareff's problem (cubic) [Ho92] (in [CJ98], p. 211)
solo2 := ex({x,y},1<=4*a<=7 and -3<=4*b<=3 and -1<=x<=0 and 0<=y<=1 and
   3*x**2-2*x-a=0 and x**3-x**2-a*x=2*b-a+2 and 3*y**2-2*y-a=0 and
      y**3-y**2-a*y=a-2)$
rlcad solo2;

% An arithmetic component in an expert system
% Cut without undercutting
aci :=
  ex({x,y},0<x and y<0 and x*r-x*tt+tt=x*q-x*s+s and x*b-x*d+d=y*a-y*c+c)$
aciqf := rlcad aci;

% QE special issue - Geometric Intersections
% Geometric Intersections by Scott McCallum
% THE Computer Journal 36(5), 432--438, 1993.
% The examples are given on page 436 and 437.
% Open unit ball with center at the origin in $R^3$.
b1 := x**2+y**2+z**2<1$
% Open unit ball with center at the point $(1,1,1)$ in $R^3$.
b2 := (x-1)**2+(y-1)**2+(z-1)**2<1$
% Open unit ball with center at the point $(3/2,2,0)$ in $R^3$.
b4 := (x-3/2)**2+(y-2)**2+z**2<1$
mc4 := rlex(b1 and b2 and b4)$
rlcad mc4;

%%% Testing rlgcad

% x-axis ellipse problem
ell := all({x,y},b**2*(x-c)**2+a**2*y**2-a**2*b**2=0 impl x**2+y**2-1 <=0);
ellgqf := rlgcad ell;

% x-axis ellipse problem variant (refined formula) [Ho92]
cc := x**2+y**2-1$
ee := b**2*(x-c)**2+a**2*y**2-a**2*b**2$
ellv := all({x,y},0<a<=1 and 0<b<=1 and 0<=c<=1-a and
   ((c-a<x<c+a and ee=0) impl cc <= 0))$
ellvgqf := rlgcad ellv;

% Steiner--Lehmus Theorem
h1 := u2>=0 and x1>=0$
h2 := r^2=1+x1^2=u1^2+(u2-x1)^2$
h3 := x2<=0 and r^2=(x2-x1)^2$
h4 := u1*x2+u2*x3-x2*x3=0$
h5 := x4<=1 and (x4-1)^2=(u1-1)^2+u2^2$
h6 := (x4-x5)^2+x6^2=(u1-x5)^2+(u2-x6)^2 and u1*x6-u2*x5-u2+x6=0$
h7 := (-1-u1)^2+u2^2<2^2$
h := h1 and h2 and h3 and h4 and h5 and h6 and h7$
g := (u1-x3)^2+u2^2<(x5-1)^2+x6^2$
sl9 := all({x6,x5,x4,x3,x2,x1,r},h impl g)$
sol := rlgqe sl9$

%%% Testing rlgsn

% Result of the rectangle problem from testing rlqe above.
rlgsn rp3sol;

% Some benchmarks.
% There is a list of formulas containing 3232 lines starting here.
% Forward-search for $ to get to the end of this list.
testseries := {q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 =
 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0
and 9*td - 20050 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*
z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
 250*p1 + 2*q - 1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td +
750*z - 4647 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 +
 2*q - 1457 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td +
50000*z - 669797 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*
p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q
- 50000*td - 10000000*z - 402109359 = 0) or q >= 0 and q - 40 <= 0 and n
 >= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2
*q - 1377 = 0 and 20*n - 2*td - 300*z - 8331 = 0 and (400*q + 9*td -
20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) or q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and 2*
p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 100*n - 10*td - 2500
*z - 36519 = 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td
- 20050 > 0 and 9*td - 20050 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*n
 - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0
and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377
<= 0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or
80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td -
10000000*z - 402109359 = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >=
 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 20*n - 50*q - 2*td - 300*z
- 8331 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >= 0 and n - td
= 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >= 0 and 80*i2
 + 250*p1 + 2*q - 1457 = 0 and 100*n - 250*q - 10*td - 2500*z - 36519 =
0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0
and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9
*td - 20050 >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or
 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td
 + 12000*z + 25898 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q +
175*td + 100000*z - 838344 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80
*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 +
 268750*q - 21875*td - 10000000*z - 464765609 = 0) or q >= 0 and q - 40
<= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 -
 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 160*n - 7*td - 2400*z -
86698 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td -
450 >= 0 and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and
400*q + 9*td - 20050 >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1457 = 0 and 800*n - 35*td - 20000*z - 392402 = 0 and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q -
40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050
<= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (2*
p1 - 7 < 0 and 20*n - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*
i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (20*n - 2*
td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0 or 20*n - 2*td
 - 300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0 and 4420*n - 442
*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z - 8923 > 0 and 20*n -
 2*td - 300*z - 10699 < 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >= 0 and n - td = 0 and
i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and 20*n - 50*q -
 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 +
 162*q + 5*td + 750*z - 4647 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (20*n - 50*q - 2*td - 300*z -
 5963 > 0 and 20*n - 50*q - 2*td - 300*z - 8331 <= 0 or 20*n - 50*q - 2*
td - 300*z - 8331 > 0 and 20*n - 50*q - 2*td - 300*z - 8923 <= 0 and
4420*n - 11050*q - 442*td + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td
- 300*z - 8923 > 0 and 20*n - 50*q - 2*td - 300*z - 10699 < 0 and
31937500*n - 79843750*q - 3193750*td - 571562500*z - 13629165033 = 0) or
 q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*
n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td -
20050 >= 0 and (2*p1 - 7 < 0 and 160*n - 7*td - 2400*z - 39338 = 0 or 2*
p1 - 7 >= 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z +
25898 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
 0 and z = 0) and (160*n - 7*td - 2400*z - 67754 > 0 and 160*n - 7*td -
2400*z - 86698 <= 0 or 160*n - 7*td - 2400*z - 86698 > 0 and 160*n - 7*
td - 2400*z - 91434 <= 0 and 35360*n - 1547*td + 653600*z - 29792578 = 0
 or 160*n - 7*td - 2400*z - 91434 > 0 and 160*n - 7*td - 2400*z - 105642
 < 0 and 255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) or
 q >= 0 and q - 40 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750
*z - 4647 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z + 353
>= 0 and 112480*i2 - 3575*n + 351500*p1 + 2812*q + 380*td + 57000*z -
353172 >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and
 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 400*q + 9
*td - 20050 > 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*
z + 25898 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2
*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z - 8331
>= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 715*n - 76*td - 11400*z -
316578 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1377 = 0 and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*
td - 300*z - 8331 = 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td -
2400*z - 86698 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z
 - 8923 >= 0 and 20*n - 2*td - 300*z - 10923 <= 0 and 715*n - 76*td -
11400*z - 339074 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80
*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z - 1012 = 0 and (400*q + 9*td -
20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8923 = 0 or 400*q + 9*td -
20050 > 0 and 160*n - 7*td - 2400*z - 91434 = 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40
 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <=
0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (2*p1
- 7 < 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2
 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0) and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
100*n - 10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0
and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z -
6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500
*z - 36519 > 0 and 100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n -
18731250*td - 4082187500*z - 74105780531 = 0) or q >= 0 and q - 40 <= 0
and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050
<= 0 and (2*p1 - 7 < 0 and 20*n - 50*q - 2*td - 500*z + 1153 = 0 or 2*p1
 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z
- 669797 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (100*n - 250*q - 10*td - 2500*z - 17299 > 0 and
 20*n - 50*q - 2*td - 500*z - 6535 <= 0 and 4420*n - 11050*q - 442*td +
81700*z - 3170191 = 0 or 20*n - 50*q - 2*td - 500*z - 6535 > 0 and 100*n
 - 250*q - 10*td - 2500*z - 36519 <= 0 or 100*n - 250*q - 10*td - 2500*z
 - 36519 > 0 and 100*n - 250*q - 10*td - 2500*z - 48051 < 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and
180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td
- 20050 >= 0 and (2*p1 - 7 < 0 and 160*n - 7*td - 4000*z - 10826 = 0 or
2*p1 - 7 >= 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q + 175*td +
100000*z - 838344 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (800*n - 35*td - 20000*z - 238642 > 0
and 160*n - 7*td - 4000*z - 72330 <= 0 and 35360*n - 1547*td + 653600*z
- 29792578 = 0 or 160*n - 7*td - 4000*z - 72330 > 0 and 800*n - 35*td -
20000*z - 392402 <= 0 or 800*n - 35*td - 20000*z - 392402 > 0 and 800*n
- 35*td - 20000*z - 484658 < 0 and 1498500000*n - 65559375*td - 32657500000
*z - 780627025498 = 0) or q >= 0 and q - 40 <= 0 and 76880*i2 - 2000*n +
 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and 76880*i2 - 2000
*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and 1460720*i2
- 35750*n + 4564750*p1 + 36518*q + 3800*td + 950000*z - 12726143 >= 0
and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 76880*i2 -
2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0 or 400*q + 9
*td - 20050 > 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q + 175*td +
100000*z - 838344 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577
= 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400
*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015
= 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2
+ 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q -
 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 500*z - 6535
>= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 715*n - 76*td - 19000*z -
248330 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1377 = 0 and 50*z - 449 = 0 and (400*q + 9*td - 20050 <= 0
and 20*n - 50*q - 2*td - 500*z - 6535 = 0 or 400*q + 9*td - 20050 > 0
and 160*n - 7*td - 4000*z - 72330 = 0) and (td - 400 >= 0 and td - 700 <
 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*
td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and
 4420*n - 442*td + 81700*z - 3170191 >= 0 and 4420*n - 442*td + 81700*z
- 3612191 <= 0 and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and
n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 4420*n - 11050*
q - 442*td + 81700*z - 3170191 = 0 or 400*q + 9*td - 20050 > 0 and 35360
*n - 1547*td + 653600*z - 29792578 = 0) and (2*p1 - 7 < 0 and 50*z - 891
 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0
and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 > 0 and
108437500*z - 865858649 = 0) or q >= 0 and q - 40 <= 0 and 100*n - 10*td
 - 2500*z - 36519 >= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 3575*
n - 380*td - 95000*z - 1387722 <= 0 and n - td = 0 and i2 = 0 and 2*p1 -
 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (400*q + 9*td - 20050 <=
 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q + 9*td -
20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q
 - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td -
20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0)
and (2*p1 - 7 < 0 and 62500*n - 6250*td - 1250000*z - 40571873 = 0 or 2*
p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*
td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) and (62500*n - 6250*td - 1250000*z
 - 32509373 < 0 and 62500*n - 6250*td - 1250000*z - 27134373 >= 0 and
31937500*n - 3193750*td - 571562500*z - 13629165033 = 0 or 62500*n -
6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*td - 1250000*z -
25790623 >= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531
 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*
td - 1250000*z - 21759373 > 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1
- 7 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 40571873 = 0 or 2
*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000
*td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (62500*n - 156250*q - 6250*td
 - 1250000*z - 32509373 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z
 - 27134373 >= 0 and 31937500*n - 79843750*q - 3193750*td - 571562500*z
- 13629165033 = 0 or 62500*n - 156250*q - 6250*td - 1250000*z - 27134373
 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
 or 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n
- 156250*q - 6250*td - 1250000*z - 21759373 > 0) or q >= 0 and q - 40 <=
 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950
>= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and (2*p1 -
 7 < 0 and 500000*n - 21875*td - 10000000*z - 387231234 = 0 or 2*p1 - 7
>= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 21875*td -
10000000*z - 464765609 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (500000*n - 21875*td - 10000000*z -
322731234 < 0 and 500000*n - 21875*td - 10000000*z - 279731234 >= 0 and
255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0 or 500000*n
- 21875*td - 10000000*z - 279731234 < 0 and 500000*n - 21875*td -
10000000*z - 268981234 >= 0 and 1498500000*n - 65559375*td - 32657500000
*z - 780627025498 = 0 or 500000*n - 21875*td - 10000000*z - 268981234 <
0 and 500000*n - 21875*td - 10000000*z - 236731234 > 0) or q >= 0 and q
- 40 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td
 - 10000000*z - 402109359 >= 0 and 10750000*i2 + 500000*n + 33593750*p1
+ 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and 204250000*i2 +
8937500*n + 638281250*p1 + 5106250*q - 950000*td - 190000000*z - 7640077821
 <= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 = 0 or 400*q + 9*td - 20050 > 0 and 10750000*i2 + 500000*n +
 33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) and (2*
p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) or q
>= 0 and q - 40 <= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 >= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 1141765625
*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and n - td = 0 and
 i2 = 0 and (400*q + 9*td - 20050 <= 0 and 31937500*n - 79843750*q -
3193750*td - 571562500*z - 13629165033 = 0 or 400*q + 9*td - 20050 > 0
and 255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) and (2*
p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2
+ 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (156250*z +
 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z + 549999 > 0 and
78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or 78125*z -
523438 > 0 and 156250*z - 5837501 < 0) or q >= 0 and q - 40 <= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0 and 6696421875
*n - 711787500*td - 155123125000*z - 2816019660178 <= 0 and n - td = 0
and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 187312500*n - 468281250*q
- 18731250*td - 4082187500*z - 74105780531 = 0 or 400*q + 9*td - 20050 >
 0 and 1498500000*n - 65559375*td - 32657500000*z - 780627025498 = 0)
and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z
 - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or
78125*z - 741562 < 0 and 156250*z + 4136251 > 0),
td = 0 and i2 = 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and 20*n - 2
*td - 300*z - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and q - 40
<= 0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331
= 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td - 2400*z - 86698 = 0)
or td = 0 and i2 = 0 and 715*n - 76*td - 11400*z - 339074 <= 0 and 20*n
- 2*td - 300*z - 10923 <= 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q -
40 <= 0 and q >= 0 and 125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and 20*n -
50*q - 2*td - 300*z - 8923 = 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7
*td - 2400*z - 91434 = 0) or td = 0 and i2 = 0 and 112480*i2 - 3575*n +
351500*p1 + 2812*q + 380*td + 57000*z - 353172 >= 0 and 1480*i2 - 50*n +
 4625*p1 + 37*q + 5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n + 4625*p1 +
 37*q + 5*td + 750*z - 4647 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 +
 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*i2 +
 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 17680*
i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q -
1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2 + 79843750*
p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td -
20050 <= 0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 =
0 or 400*q + 9*td - 20050 > 0 and 23680*i2 - 800*n + 74000*p1 + 592*q +
35*td + 12000*z + 25898 = 0) or td = 0 and i2 = 0 and 715*n - 76*td -
19000*z - 248330 <= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 20*n - 2*
td - 500*z - 6535 >= 0 and q - 40 <= 0 and q >= 0 and 50*z - 449 = 0 and
 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*
td - 20050 <= 0 and 20*n - 50*q - 2*td - 500*z - 6535 = 0 or 400*q + 9*
td - 20050 > 0 and 160*n - 7*td - 4000*z - 72330 = 0) or td = 0 and i2 =
 0 and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and 4420*n - 442
*td + 81700*z - 3612191 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >=
0 and q - 40 <= 0 and q >= 0 and (250*z - 3129 < 0 and 50*z - 449 >= 0
or 50*z - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z -
1361 > 0 and 108437500*z - 865858649 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
50*z - 891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*
z - 483917 = 0) and (400*q + 9*td - 20050 <= 0 and 4420*n - 11050*q -
442*td + 81700*z - 3170191 = 0 or 400*q + 9*td - 20050 > 0 and 35360*n -
 1547*td + 653600*z - 29792578 = 0) or td = 0 and i2 = 0 and 3575*n -
380*td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0
and 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and 2
*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q + 9*
td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) or td = 0 and
i2 = 0 and 1460720*i2 - 35750*n + 4564750*p1 + 36518*q + 3800*td +
950000*z - 12726143 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 469797 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*
q + 200*td + 50000*z - 669797 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2
 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2
*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2
*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 +
93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 <
 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*
td - 20050 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td +
50000*z - 669797 = 0 or 400*q + 9*td - 20050 > 0 and 153760*i2 - 4000*n
+ 480500*p1 + 3844*q + 175*td + 100000*z - 838344 = 0) or td = 0 and i2
= 0 and 1141765625*n - 121362500*td - 21719375000*z - 517908271254 <= 0
and 31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and
31937500*n - 3193750*td - 571562500*z - 13629165033 >= 0 and q - 40 <= 0
 and q >= 0 and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or
156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z -
865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (
400*q + 9*td - 20050 <= 0 and 31937500*n - 79843750*q - 3193750*td -
571562500*z - 13629165033 = 0 or 400*q + 9*td - 20050 > 0 and 255500000*
n - 11178125*td - 4572500000*z - 141050664014 = 0) or td = 0 and i2 = 0
and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178 <= 0
and 187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and q - 40
<= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0
 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z -
 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0) and (td
 - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >=
0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and
(2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and 29970000*
i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (400*q + 9*
td - 20050 <= 0 and 187312500*n - 468281250*q - 18731250*td - 4082187500
*z - 74105780531 = 0 or 400*q + 9*td - 20050 > 0 and 1498500000*n -
65559375*td - 32657500000*z - 780627025498 = 0) or td = 0 and i2 = 0 and
 204250000*i2 + 8937500*n + 638281250*p1 + 5106250*q - 950000*td -
190000000*z - 7640077821 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 +
 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and 10750000*i2 +
500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 >=
 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and
80*i2 + 250*p1 + 2*q - 1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*
q - 2500000*z - 448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*
i2 + 250*p1 + 2*q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q +
 2500000*z - 569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2
+ 250*p1 + 2*q - 1697 < 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
 - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td - 20050 <= 0 and 10750000
*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z -
402109359 = 0 or 400*q + 9*td - 20050 > 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) or td =
0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q -
40 <= 0 and q >= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0 and 2*p1 -
7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or td = 0 and 400*q + 9
*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q
>= 0 and (20*n - 50*q - 2*td - 300*z - 5963 > 0 and 20*n - 50*q - 2*td -
 300*z - 8331 <= 0 or 20*n - 50*q - 2*td - 300*z - 8331 > 0 and 20*n -
50*q - 2*td - 300*z - 8923 <= 0 and 4420*n - 11050*q - 442*td + 81700*z
- 3170191 = 0 or 20*n - 50*q - 2*td - 300*z - 8923 > 0 and 20*n - 50*q -
 2*td - 300*z - 10699 < 0 and 31937500*n - 79843750*q - 3193750*td -
571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3
*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*
z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 20*n - 50*q - 2*
td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 +
162*q + 5*td + 750*z - 4647 = 0) or td = 0 and 400*q + 9*td - 20050 <= 0
 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 100*n -
250*q - 10*td - 2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1
+ 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) or td = 0 and 400*q + 9*td - 20050 <= 0 and i2 = 0
and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and (100*n - 250*q - 10*
td - 2500*z - 17299 > 0 and 20*n - 50*q - 2*td - 500*z - 6535 <= 0 and
4420*n - 11050*q - 442*td + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td
- 500*z - 6535 > 0 and 100*n - 250*q - 10*td - 2500*z - 36519 <= 0 or
100*n - 250*q - 10*td - 2500*z - 36519 > 0 and 100*n - 250*q - 10*td -
2500*z - 48051 < 0 and 187312500*n - 468281250*q - 18731250*td - 4082187500
*z - 74105780531 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400
*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015
= 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 20*n - 50*q - 2*td - 500*
z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q
 + 200*td + 50000*z - 669797 = 0) or td = 0 and 400*q + 9*td - 20050 <=
0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and (62500*
n - 156250*q - 6250*td - 1250000*z - 32509373 < 0 and 62500*n - 156250*q
 - 6250*td - 1250000*z - 27134373 >= 0 and 31937500*n - 79843750*q -
3193750*td - 571562500*z - 13629165033 = 0 or 62500*n - 156250*q - 6250*
td - 1250000*z - 27134373 < 0 and 62500*n - 156250*q - 6250*td - 1250000
*z - 25790623 >= 0 and 187312500*n - 468281250*q - 18731250*td - 4082187500
*z - 74105780531 = 0 or 62500*n - 156250*q - 6250*td - 1250000*z -
25790623 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 21759373 > 0
) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) and (2*p1 - 7 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z -
40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 -
 981250*q - 50000*td - 10000000*z - 402109359 = 0) or td = 0 and 400*q +
 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q
 >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0
 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0
 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797
= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 <
 0 and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td -
10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) or td = 0 and 400*q + 9*td - 20050 >= 0
and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td -
 20050 <= 0 and q - 40 <= 0 and q >= 0 and 160*n - 7*td - 2400*z - 86698
 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or td = 0
and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*td - 380950 >=
0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and
 (160*n - 7*td - 2400*z - 67754 > 0 and 160*n - 7*td - 2400*z - 86698 <=
 0 or 160*n - 7*td - 2400*z - 86698 > 0 and 160*n - 7*td - 2400*z -
91434 <= 0 and 35360*n - 1547*td + 653600*z - 29792578 = 0 or 160*n - 7*
td - 2400*z - 91434 > 0 and 160*n - 7*td - 2400*z - 105642 < 0 and
255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 160*n - 7*td - 2400*z - 39338 = 0 or 2*p1 - 7 >= 0 and
23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z + 25898 = 0) or td
 = 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*td -
380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and
q >= 0 and 800*n - 35*td - 20000*z - 392402 = 0 and 2*p1 - 7 >= 0 and 80
*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) or td = 0 and 400*q + 9*td - 20050 >= 0
 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td
- 20050 <= 0 and q - 40 <= 0 and q >= 0 and (800*n - 35*td - 20000*z -
238642 > 0 and 160*n - 7*td - 4000*z - 72330 <= 0 and 35360*n - 1547*td
+ 653600*z - 29792578 = 0 or 160*n - 7*td - 4000*z - 72330 > 0 and 800*n
 - 35*td - 20000*z - 392402 <= 0 or 800*n - 35*td - 20000*z - 392402 > 0
 and 800*n - 35*td - 20000*z - 484658 < 0 and 1498500000*n - 65559375*td
 - 32657500000*z - 780627025498 = 0) and (td - 400 >= 0 and td - 700 < 0
 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
- 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 160*n - 7*
td - 4000*z - 10826 = 0 or 2*p1 - 7 >= 0 and 153760*i2 - 4000*n + 480500
*p1 + 3844*q + 175*td + 100000*z - 838344 = 0) or td = 0 and 400*q + 9*
td - 20050 >= 0 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450
 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and (500000*n -
21875*td - 10000000*z - 322731234 < 0 and 500000*n - 21875*td - 10000000
*z - 279731234 >= 0 and 255500000*n - 11178125*td - 4572500000*z - 141050664014
 = 0 or 500000*n - 21875*td - 10000000*z - 279731234 < 0 and 500000*n -
21875*td - 10000000*z - 268981234 >= 0 and 1498500000*n - 65559375*td - 32657500000
*z - 780627025498 = 0 or 500000*n - 21875*td - 10000000*z - 268981234 <
0 and 500000*n - 21875*td - 10000000*z - 236731234 > 0) and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7
 < 0 and 500000*n - 21875*td - 10000000*z - 387231234 = 0 or 2*p1 - 7 >=
 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 21875*td -
10000000*z - 464765609 = 0) or td = 0 and 400*q + 9*td - 20050 >= 0 and
i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td -
20050 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 >
 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 23680*i2 - 800*n + 74000*p1
+ 592*q + 35*td + 12000*z + 25898 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0
 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 153760*i2 - 4000*n + 480500*p1
 + 3844*q + 175*td + 100000*z - 838344 = 0 or 80*i2 + 250*p1 + 2*q -
1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n
+ 33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
or td = 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 20*n - 2*
td - 300*z - 8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377
= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and
 z = 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050
 > 0 and 9*td - 20050 = 0) or td = 0 and i2 = 0 and n >= 0 and q - 40 <=
 0 and q >= 0 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*
z - 8331 <= 0 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z
- 8923 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td -
 300*z - 8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n -
3193750*td - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
20*n - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n +
4625*p1 + 37*q + 5*td + 750*z - 4647 = 0) and (400*q + 9*td - 20050 <= 0
 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or td = 0
and i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 100*n - 10*td -
2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0
 and 9*td - 20050 = 0) or td = 0 and i2 = 0 and n >= 0 and q - 40 <= 0
and q >= 0 and (100*n - 10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500
*z - 6535 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*
td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n -
 10*td - 2500*z - 36519 > 0 and 100*n - 10*td - 2500*z - 48051 < 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and
76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0)
and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and
 9*td - 20050 = 0) or td = 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q
 >= 0 and (62500*n - 6250*td - 1250000*z - 32509373 < 0 and 62500*n -
6250*td - 1250000*z - 27134373 >= 0 and 31937500*n - 3193750*td -
571562500*z - 13629165033 = 0 or 62500*n - 6250*td - 1250000*z -
27134373 < 0 and 62500*n - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0 or 62500*n -
6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td - 1250000*z -
21759373 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 62500*n - 6250*td - 1250000*z
 - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*
p1 + 268750*q - 50000*td - 10000000*z - 402109359 = 0) and (400*q + 9*td
 - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0
) or td = 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (80*i2
+ 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 1480*
i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0 or 80*i2 + 250*p1 +
 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 76880*i2 - 2000
*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0 or 80*i2 + 250*
p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2
+ 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359
= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and
 z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7
 >= 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050
> 0 and 9*td - 20050 = 0) or 6*n - 5*td < 0 and i2 = 0 and 715*n - 76*td
 - 11400*z - 316578 <= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 20*n -
 2*td - 300*z - 8331 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0
and 20*n - 50*q - 2*td - 300*z - 8331 = 0 or 400*q + 9*td - 20050 > 0
and 160*n - 7*td - 2400*z - 86698 = 0) or 6*n - 5*td < 0 and i2 = 0 and
715*n - 76*td - 11400*z - 339074 <= 0 and 20*n - 2*td - 300*z - 10923 <=
 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q - 40 <= 0 and q >= 0 and
125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8923
= 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td - 2400*z - 91434 = 0)
or 6*n - 5*td < 0 and i2 = 0 and 112480*i2 - 3575*n + 351500*p1 + 2812*q
 + 380*td + 57000*z - 353172 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q +
5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750
*z - 4647 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q -
1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q -
1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 +
442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q -
2500000*z - 448579359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
 - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td - 20050 <= 0 and 1480*i2
- 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 400*q + 9*td -
20050 > 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z +
25898 = 0) or 6*n - 5*td < 0 and i2 = 0 and 715*n - 76*td - 19000*z -
248330 <= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 20*n - 2*td - 500*z
- 6535 >= 0 and q - 40 <= 0 and q >= 0 and 50*z - 449 = 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700
 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2
*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <=
 0 and 20*n - 50*q - 2*td - 500*z - 6535 = 0 or 400*q + 9*td - 20050 > 0
 and 160*n - 7*td - 4000*z - 72330 = 0) or 6*n - 5*td < 0 and i2 = 0 and
 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and 4420*n - 442*td +
81700*z - 3612191 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >= 0 and
q - 40 <= 0 and q >= 0 and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z
 - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 >
0 and 108437500*z - 865858649 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 50*z - 891
= 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917
= 0) and (400*q + 9*td - 20050 <= 0 and 4420*n - 11050*q - 442*td +
81700*z - 3170191 = 0 or 400*q + 9*td - 20050 > 0 and 35360*n - 1547*td
+ 653600*z - 29792578 = 0) or 6*n - 5*td < 0 and i2 = 0 and 3575*n - 380
*td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and
 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q + 9*
td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) or 6*n - 5*td <
 0 and i2 = 0 and 1460720*i2 - 35750*n + 4564750*p1 + 36518*q + 3800*td
+ 950000*z - 12726143 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 469797 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*
q + 200*td + 50000*z - 669797 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2
 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2
*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2
*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 +
93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 <
 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*
td - 20050 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td +
50000*z - 669797 = 0 or 400*q + 9*td - 20050 > 0 and 153760*i2 - 4000*n
+ 480500*p1 + 3844*q + 175*td + 100000*z - 838344 = 0) or 6*n - 5*td < 0
 and i2 = 0 and 1141765625*n - 121362500*td - 21719375000*z - 517908271254
 <= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and
31937500*n - 3193750*td - 571562500*z - 13629165033 >= 0 and q - 40 <= 0
 and q >= 0 and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or
156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z -
865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (
400*q + 9*td - 20050 <= 0 and 31937500*n - 79843750*q - 3193750*td -
571562500*z - 13629165033 = 0 or 400*q + 9*td - 20050 > 0 and 255500000*
n - 11178125*td - 4572500000*z - 141050664014 = 0) or 6*n - 5*td < 0 and
 i2 = 0 and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0
and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and q -
40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z - 3356249
>= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125
*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0) and
 (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
400*q + 9*td - 20050 <= 0 and 187312500*n - 468281250*q - 18731250*td - 4082187500
*z - 74105780531 = 0 or 400*q + 9*td - 20050 > 0 and 1498500000*n -
65559375*td - 32657500000*z - 780627025498 = 0) or 6*n - 5*td < 0 and i2
 = 0 and 204250000*i2 + 8937500*n + 638281250*p1 + 5106250*q - 950000*td
 - 190000000*z - 7640077821 <= 0 and 10750000*i2 + 500000*n + 33593750*
p1 + 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and 10750000*i2 +
 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359
>= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and
 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750
*q - 2500000*z - 448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80
*i2 + 250*p1 + 2*q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q
+ 2500000*z - 569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2
 + 250*p1 + 2*q - 1697 < 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td - 20050 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 = 0 or 400*q + 9*td - 20050 > 0 and 10750000*i2 + 500000*n +
 33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) or 6*n
- 5*td < 0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0
 and q - 40 <= 0 and q >= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0
and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 6*n - 5*td <
 0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q -
 40 <= 0 and q >= 0 and (20*n - 50*q - 2*td - 300*z - 5963 > 0 and 20*n
- 50*q - 2*td - 300*z - 8331 <= 0 or 20*n - 50*q - 2*td - 300*z - 8331 >
 0 and 20*n - 50*q - 2*td - 300*z - 8923 <= 0 and 4420*n - 11050*q - 442
*td + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td - 300*z - 8923 > 0 and
 20*n - 50*q - 2*td - 300*z - 10699 < 0 and 31937500*n - 79843750*q -
3193750*td - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
20*n - 50*q - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*
n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0) or 6*n - 5*td < 0 and 400
*q + 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0
and q >= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700
 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2
*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 6*n - 5*td < 0 and 400*q
+ 9*td - 20050 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and
q >= 0 and (100*n - 250*q - 10*td - 2500*z - 17299 > 0 and 20*n - 50*q -
 2*td - 500*z - 6535 <= 0 and 4420*n - 11050*q - 442*td + 81700*z -
3170191 = 0 or 20*n - 50*q - 2*td - 500*z - 6535 > 0 and 100*n - 250*q -
 10*td - 2500*z - 36519 <= 0 or 100*n - 250*q - 10*td - 2500*z - 36519 >
 0 and 100*n - 250*q - 10*td - 2500*z - 48051 < 0 and 187312500*n -
468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 20*n - 50*q - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0
and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 =
 0) or 6*n - 5*td < 0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and 9*n -
 380*q >= 0 and q - 40 <= 0 and q >= 0 and (62500*n - 156250*q - 6250*td
 - 1250000*z - 32509373 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z
 - 27134373 >= 0 and 31937500*n - 79843750*q - 3193750*td - 571562500*z
- 13629165033 = 0 or 62500*n - 156250*q - 6250*td - 1250000*z - 27134373
 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
 or 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n
- 156250*q - 6250*td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0
and 62500*n - 156250*q - 6250*td - 1250000*z - 40571873 = 0 or 2*p1 - 7
>= 0 and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td -
10000000*z - 402109359 = 0) or 6*n - 5*td < 0 and 400*q + 9*td - 20050
<= 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and (80*
i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 80*i2 +
250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 76880*
i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) or 6*n - 5*td < 0 and 400*q + 9*td - 20050 >= 0 and
i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td -
20050 <= 0 and q - 40 <= 0 and q >= 0 and 160*n - 7*td - 2400*z - 86698
= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400
>= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 6*n - 5
*td < 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*td -
380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and
q >= 0 and (160*n - 7*td - 2400*z - 67754 > 0 and 160*n - 7*td - 2400*z
- 86698 <= 0 or 160*n - 7*td - 2400*z - 86698 > 0 and 160*n - 7*td -
2400*z - 91434 <= 0 and 35360*n - 1547*td + 653600*z - 29792578 = 0 or
160*n - 7*td - 2400*z - 91434 > 0 and 160*n - 7*td - 2400*z - 105642 < 0
 and 255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 160*n - 7*td - 2400*z - 39338 = 0 or 2*p1 - 7 >= 0
 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z + 25898 = 0)
or 6*n - 5*td < 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n +
171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40
<= 0 and q >= 0 and 800*n - 35*td - 20000*z - 392402 = 0 and 2*p1 - 7 >=
 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 <
 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*
td - 300*z + 1015 = 0 or td = 0 and z = 0) or 6*n - 5*td < 0 and 400*q +
 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td -
450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and (800*n -
35*td - 20000*z - 238642 > 0 and 160*n - 7*td - 4000*z - 72330 <= 0 and
35360*n - 1547*td + 653600*z - 29792578 = 0 or 160*n - 7*td - 4000*z -
72330 > 0 and 800*n - 35*td - 20000*z - 392402 <= 0 or 800*n - 35*td -
20000*z - 392402 > 0 and 800*n - 35*td - 20000*z - 484658 < 0 and 1498500000
*n - 65559375*td - 32657500000*z - 780627025498 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 <
 0 and 160*n - 7*td - 4000*z - 10826 = 0 or 2*p1 - 7 >= 0 and 153760*i2
- 4000*n + 480500*p1 + 3844*q + 175*td + 100000*z - 838344 = 0) or 6*n -
 5*td < 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*td -
380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and
q >= 0 and (500000*n - 21875*td - 10000000*z - 322731234 < 0 and 500000*
n - 21875*td - 10000000*z - 279731234 >= 0 and 255500000*n - 11178125*td
 - 4572500000*z - 141050664014 = 0 or 500000*n - 21875*td - 10000000*z -
 279731234 < 0 and 500000*n - 21875*td - 10000000*z - 268981234 >= 0 and
 1498500000*n - 65559375*td - 32657500000*z - 780627025498 = 0 or 500000
*n - 21875*td - 10000000*z - 268981234 < 0 and 500000*n - 21875*td -
10000000*z - 236731234 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 500000*n - 21875*td
 - 10000000*z - 387231234 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*
n + 33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) or 6
*n - 5*td < 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and 180*n + 171*
td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0
 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*
q - 1377 <= 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z
+ 25898 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q
- 1457 <= 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q + 175*td +
100000*z - 838344 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250
*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q
 - 21875*td - 10000000*z - 464765609 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 6*n - 5*td < 0 and
i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 20*n - 2*td - 300*z -
8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td -
 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td
 - 20050 = 0) or 6*n - 5*td < 0 and i2 = 0 and n >= 0 and q - 40 <= 0
and q >= 0 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z -
 8331 <= 0 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z -
8923 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td -
300*z - 8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n -
3193750*td - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
20*n - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n +
4625*p1 + 37*q + 5*td + 750*z - 4647 = 0) and (400*q + 9*td - 20050 <= 0
 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or 6*n - 5*
td < 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 100*n - 10*
td - 2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
 and z = 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td -
20050 > 0 and 9*td - 20050 = 0) or 6*n - 5*td < 0 and i2 = 0 and n >= 0
and q - 40 <= 0 and q >= 0 and (100*n - 10*td - 2500*z - 17299 > 0 and
20*n - 2*td - 500*z - 6535 <= 0 and 4420*n - 442*td + 81700*z - 3170191
= 0 or 20*n - 2*td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519
 <= 0 or 100*n - 10*td - 2500*z - 36519 > 0 and 100*n - 10*td - 2500*z -
 48051 < 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 =
0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
 - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
 = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7
>= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z -
669797 = 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td -
20050 > 0 and 9*td - 20050 = 0) or 6*n - 5*td < 0 and i2 = 0 and n >= 0
and q - 40 <= 0 and q >= 0 and (62500*n - 6250*td - 1250000*z - 32509373
 < 0 and 62500*n - 6250*td - 1250000*z - 27134373 >= 0 and 31937500*n -
3193750*td - 571562500*z - 13629165033 = 0 or 62500*n - 6250*td -
1250000*z - 27134373 < 0 and 62500*n - 6250*td - 1250000*z - 25790623 >=
 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0 or
62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td -
1250000*z - 21759373 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 62500*n - 6250*td -
1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 = 0) and (400
*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td -
20050 = 0) or 6*n - 5*td < 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q
 >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0
or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0
and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 =
 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 <
0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td -
10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td - 20050 <= 0 and q = 0
or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0),
2*p1 - 7 >= 0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0
and q >= 0 and q - 40 <= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 2*p1 - 7 >= 0 and 400*q + 9*td - 20050
>= 0 and i2 = 0 and n - td = 0 and td - 450 >= 0 and 9*td - 20050 <= 0
and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 2*p1 - 7 >= 0 and i2 = 0 and n - td = 0
 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and q - 40
 = 0 or 400*q + 9*td - 20050 > 0 and td - 450 = 0) or 2*p1 - 7 < 0 and
400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and q >= 0 and q -
40 <= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) or 2*p1 - 7 < 0 and 400*q + 9*td - 20050 >= 0 and i2 = 0 and
n - td = 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q
 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) or 2*p1 - 7 < 0 and i2 = 0 and n - td = 0 and q - 40 <= 0 and
 q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td
 - 20050 > 0 and td - 450 = 0),
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (
400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td
 - 20050 = 0) or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*
q - 1697 < 0 and i2 = 0 and n - td = 0 and 9*n - 380*q >= 0 and q - 40
<= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0 and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 80*i2 + 250*p1 + 2*q
 - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and i2 = 0 and n - td =
0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <=
 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 = 0 and n - td = 0
and n >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 <
0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
 - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0
and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 = 0 and n - td = 0 and 9
*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0
 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
 0) or 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 = 0 and
n - td = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td -
 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and
 (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0
and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (
400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td
 - 20050 = 0) or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*
q - 1457 <= 0 and i2 = 0 and n - td = 0 and 9*n - 380*q >= 0 and q - 40
<= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0 and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 80*i2 + 250*p1 + 2*q
 - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and i2 = 0 and n - td =
 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050
<= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and i2 = 0 and n - td = 0
and n >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 <
0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
 - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0
and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and i2 = 0 and n - td = 0 and 9
*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0
 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
 0) or 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and i2 = 0 and
n - td = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td -
 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and
 (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0
and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (
400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td
 - 20050 = 0) or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*
q - 1377 <= 0 and i2 = 0 and n - td = 0 and 9*n - 380*q >= 0 and q - 40
<= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0 and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 80*i2 + 250*p1 + 2*q
 - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and i2 = 0 and n - td =
 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050
<= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2
*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0),
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (2*p1 - 7 < 0 and 80*i2
 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td - 20050 <=
 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or 80*i2
+ 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and i2 = 0
 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td -
20050 <= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1
- 7 >= 0) or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
1697 < 0 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0
and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td -
20050 >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1
- 7 >= 0) or 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 =
0 and n >= 0 and q - 40 <= 0 and q >= 0 and (400*q + 9*td - 20050 <= 0
and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) or 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 = 0 and 9*n - 380*q >= 0
 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0 or 2*p1 - 7 >=
 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and i2 = 0 and 180*n + 171*td -
380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and
q >= 0 and 400*q + 9*td - 20050 >= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0
and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and i2 = 0 and n >= 0 and q - 40 <=
 0 and q >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*
p1 - 7 >= 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td -
20050 > 0 and 9*td - 20050 = 0) or 80*i2 + 250*p1 + 2*q - 1377 > 0 and
80*i2 + 250*p1 + 2*q - 1457 <= 0 and i2 = 0 and 9*n - 380*q >= 0 and q -
 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 80*i2 + 250*p1 + 2*q
 - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and i2 = 0 and 180*n +
171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40
<= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and (2*p1 - 7 < 0 and 80*
i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 2*p1 - 7 >= 0 and 80*i2
 + 250*p1 + 2*q - 1377 = 0 and i2 = 0 and n >= 0 and q - 40 <= 0 and q
>= 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 >
0 and 9*td - 20050 = 0) or 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377
 = 0 and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*
q + 9*td - 20050 <= 0 or 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 =
 0 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*
td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0
 or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0
 and i2 = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (400*q + 9*td -
20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0)
or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0
and i2 = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9
*td - 20050 <= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or
 2*p1 - 7 >= 0) or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 +
2*q - 1377 <= 0 and i2 = 0 and 180*n + 171*td - 380950 >= 0 and td - 450
 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td
- 20050 >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*
p1 - 7 >= 0),
q >= 0 and q - 40 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*
z + 353 > 0 and 9*n - 15200 >= 0 and n - td = 0 and i2 = 0 and (400*q +
9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0 and td -
450 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 -
7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q
 - 1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q
 - 1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) or q
>= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z - 10331 < 0 and 9*n - 15200
 >= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2
*q - 1377 = 0 and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9
*td - 20050 > 0 and td - 450 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 20*
n - 2*td - 300*z - 10923 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2
= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z -
1012 = 0 and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td -
 20050 > 0 and td - 450 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and n >= 0
and 9*n - 15200 <= 0 and 112480*i2 - 3575*n + 351500*p1 + 2812*q + 380*
td + 57000*z - 353172 > 0 and n - td = 0 and i2 = 0 and (400*q + 9*td -
20050 <= 0 and 9*n - 380*q = 0 or 400*q + 9*td - 20050 > 0 and 180*n +
171*td - 380950 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and 9*n - 15200 <= 0
 and 715*n - 76*td - 11400*z - 316578 < 0 and n - td = 0 and i2 = 0 and
2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (400*q + 9*td -
20050 <= 0 and 9*n - 380*q = 0 or 400*q + 9*td - 20050 > 0 and 180*n +
171*td - 380950 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*
z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and 9*n -
15200 <= 0 and 715*n - 76*td - 11400*z - 339074 < 0 and n - td = 0 and
i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z -
 1012 = 0 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0 or 400*q +
9*td - 20050 > 0 and 180*n + 171*td - 380950 = 0) and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q -
40 <= 0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 > 0
and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050
<= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >=
 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and
 z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q -
1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) or q
>= 0 and q - 40 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331 < 0 and 9*n -
 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and
2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q -
40 <= 0 and 20*n - 50*q - 2*td - 300*z - 8923 < 0 and 9*n - 380*q >= 0
and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >=
0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z - 1012 = 0 and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0
and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 23680*i2 -
800*n + 74000*p1 + 592*q + 35*td + 12000*z + 25898 > 0 and 180*n + 171*
td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0
 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377
<= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457
<= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 +
250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 25550000
*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) or q >= 0 and
q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 160*n - 7*td -
2400*z - 86698 < 0 and 180*n + 171*td - 380950 >= 0 and n - td = 0 and
i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0
and td - 450 >= 0 and 160*n - 7*td - 2400*z - 91434 < 0 and 180*n + 171*
td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0
 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z - 1012
= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and
 z = 0) or q >= 0 and q - 40 <= 0 and 76880*i2 - 2000*n + 240250*p1 +
1922*q + 200*td + 50000*z - 469797 > 0 and 9*n - 15200 >= 0 and n - td =
 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9
*td - 20050 > 0 and td - 450 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 +
 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 >
 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q
+ 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 +
250*p1 + 2*q - 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q +
2500000*z - 569558609 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td -
500*z - 8535 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2 = 0 and 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 50*z - 449 = 0 and (
400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0 and
 td - 450 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 4420*n - 442*td +
81700*z - 3612191 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2 = 0 and
 (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0
and td - 450 = 0) and (2*p1 - 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >= 0
and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0) and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (250*z -
3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0 or
125*z - 1012 < 0 and 250*z - 1361 > 0 and 108437500*z - 865858649 = 0)
or q >= 0 and q - 40 <= 0 and 100*n - 10*td - 2500*z - 46519 < 0 and 9*n
 - 15200 >= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
250*p1 + 2*q - 1457 = 0 and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or
 400*q + 9*td - 20050 > 0 and td - 450 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <=
 0 and n >= 0 and 9*n - 15200 <= 0 and 1460720*i2 - 35750*n + 4564750*p1
 + 36518*q + 3800*td + 950000*z - 12726143 > 0 and n - td = 0 and i2 = 0
 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0 or 400*q + 9*td -
20050 > 0 and 180*n + 171*td - 380950 = 0) and (2*p1 - 7 < 0 and 80*i2 +
 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2
*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*
p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and
 80*i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and
 80*i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*
q + 2500000*z - 569558609 = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and
9*n - 15200 <= 0 and 715*n - 76*td - 19000*z - 248330 < 0 and n - td = 0
 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 50
*z - 449 = 0 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0 or 400*q
 + 9*td - 20050 > 0 and 180*n + 171*td - 380950 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q
 - 40 <= 0 and n >= 0 and 9*n - 15200 <= 0 and 158015*n - 16796*td +
3104600*z - 120467258 < 0 and n - td = 0 and i2 = 0 and (400*q + 9*td -
20050 <= 0 and 9*n - 380*q = 0 or 400*q + 9*td - 20050 > 0 and 180*n +
171*td - 380950 = 0) and (2*p1 - 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >=
 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0) and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (250*z
 - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0
or 125*z - 1012 < 0 and 250*z - 1361 > 0 and 108437500*z - 865858649 = 0
) or q >= 0 and q - 40 <= 0 and n >= 0 and 9*n - 15200 <= 0 and 3575*n -
 380*td - 95000*z - 1387722 < 0 and n - td = 0 and i2 = 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (400*q + 9*td - 20050 <= 0
and 9*n - 380*q = 0 or 400*q + 9*td - 20050 > 0 and 180*n + 171*td -
380950 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320
= 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td
= 0 and z = 0) or q >= 0 and q - 40 <= 0 and 76880*i2 - 2000*n + 240250*
p1 + 6922*q + 200*td + 50000*z - 669797 > 0 and 9*n - 380*q >= 0 and n -
 td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and
80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2 + 250*p1
+ 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 +
55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 >
 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 >
 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 + 93656250*p1 +
749250*q + 2500000*z - 569558609 = 0) or q >= 0 and q - 40 <= 0 and 20*n
 - 50*q - 2*td - 500*z - 6535 < 0 and 9*n - 380*q >= 0 and n - td = 0
and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >= 0 and 80*i2 +
250*p1 + 2*q - 1377 = 0 and 50*z - 449 = 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <=
 0 and 4420*n - 11050*q - 442*td + 81700*z - 3170191 < 0 and 9*n - 380*q
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1
- 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 +
442*q + 20000*z - 483917 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (250*z - 3129 < 0 and 50*z - 449 >=
 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z
 - 1361 > 0 and 108437500*z - 865858649 = 0) or q >= 0 and q - 40 <= 0
and 100*n - 250*q - 10*td - 2500*z - 36519 < 0 and 9*n - 380*q >= 0 and
n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*
td - 20050 <= 0 and td - 450 >= 0 and 153760*i2 - 4000*n + 480500*p1 +
3844*q + 175*td + 100000*z - 838344 > 0 and 180*n + 171*td - 380950 >= 0
 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and (2*p1 - 7 <
 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2 +
250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*
i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q -
1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2*q -
1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 + 93656250*
p1 + 749250*q + 2500000*z - 569558609 = 0) or q >= 0 and q - 40 <= 0 and
 9*td - 20050 <= 0 and td - 450 >= 0 and 160*n - 7*td - 4000*z - 72330 <
 0 and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q
+ 9*td - 20050 >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 =
0 and 50*z - 449 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*
z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0
and td - 450 >= 0 and 35360*n - 1547*td + 653600*z - 29792578 < 0 and
180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td
- 20050 >= 0 and (2*p1 - 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >= 0 and
17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (250*z -
3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0 or
125*z - 1012 < 0 and 250*z - 1361 > 0 and 108437500*z - 865858649 = 0)
or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and
800*n - 35*td - 20000*z - 392402 < 0 and 180*n + 171*td - 380950 >= 0
and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 - 7 >=
0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 <
0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
 - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 452109359 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2 = 0 and (400*
q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0 and td
- 450 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1
- 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2
*q - 1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*
q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*
q - 1697 < 0) or q >= 0 and q - 40 <= 0 and 31937500*n - 3193750*td -
571562500*z - 16822915033 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2
 = 0 and (400*q + 9*td - 20050 <= 0 and q - 40 = 0 or 400*q + 9*td -
20050 > 0 and td - 450 = 0) and (2*p1 - 7 < 0 and 156250*z + 16518749 =
0 or 2*p1 - 7 >= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z
- 448579359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (156250*z + 6937499 > 0 and 156250*z + 549999
<= 0 or 156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z
- 865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) or q
 >= 0 and q - 40 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531
 < 0 and 9*n - 15200 >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td -
20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0 and td - 450 = 0)
and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z
 - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or
78125*z - 741562 < 0 and 156250*z + 4136251 > 0) or q >= 0 and q - 40 <=
 0 and n >= 0 and 9*n - 15200 <= 0 and 204250000*i2 + 8937500*n +
638281250*p1 + 5106250*q - 950000*td - 190000000*z - 7640077821 < 0 and
n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0
 or 400*q + 9*td - 20050 > 0 and 180*n + 171*td - 380950 = 0) and (2*p1
- 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) or q
>= 0 and q - 40 <= 0 and n >= 0 and 9*n - 15200 <= 0 and 1141765625*n -
121362500*td - 21719375000*z - 517908271254 < 0 and n - td = 0 and i2 =
0 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0 or 400*q + 9*td -
20050 > 0 and 180*n + 171*td - 380950 = 0) and (2*p1 - 7 < 0 and 156250*
z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2 + 79843750*p1 + 638750
*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0 and td - 700 < 0 and
3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300
*z + 1015 = 0 or td = 0 and z = 0) and (156250*z + 6937499 > 0 and
156250*z + 549999 <= 0 or 156250*z + 549999 > 0 and 78125*z - 523438 <=
0 and 108437500*z - 865858649 = 0 or 78125*z - 523438 > 0 and 156250*z -
 5837501 < 0) or q >= 0 and q - 40 <= 0 and n >= 0 and 9*n - 15200 <= 0
and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178 < 0 and
 n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q =
0 or 400*q + 9*td - 20050 > 0 and 180*n + 171*td - 380950 = 0) and (2*p1
 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and 29970000*i2 +
93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (156250*z -
 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z - 865858649 =
0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or 78125*z -
741562 < 0 and 156250*z + 4136251 > 0) or q >= 0 and q - 40 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 < 0 and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q
 + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*q -
2500000*z - 448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
 250*p1 + 2*q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q +
2500000*z - 569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 +
 250*p1 + 2*q - 1697 < 0) or q >= 0 and q - 40 <= 0 and 31937500*n -
79843750*q - 3193750*td - 571562500*z - 13629165033 < 0 and 9*n - 380*q
>= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 -
 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2 +
79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (156250*z +
 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z + 549999 > 0 and
78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or 78125*z -
523438 > 0 and 156250*z - 5837501 < 0) or q >= 0 and q - 40 <= 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 < 0
 and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050
 <= 0 and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z
 - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or
78125*z - 741562 < 0 and 156250*z + 4136251 > 0) or q >= 0 and q - 40 <=
 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 < 0 and 180*n
 + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td -
20050 >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1
- 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2
*q - 1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*
q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*
q - 1697 < 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td -
450 >= 0 and 255500000*n - 11178125*td - 4572500000*z - 141050664014 < 0
 and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q +
9*td - 20050 >= 0 and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1
- 7 >= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (156250*z + 6937499 > 0 and 156250*z + 549999
<= 0 or 156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z
- 865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) or q
 >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 1498500000
*n - 65559375*td - 32657500000*z - 780627025498 < 0 and 180*n + 171*td -
 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and
 (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and 29970000
*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (td - 400
>= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z -
865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or
78125*z - 741562 < 0 and 156250*z + 4136251 > 0),
62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td -
1250000*z - 21759373 > 0 and q >= 0 and q - 40 <= 0 and n >= 0 and n -
td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and (400*q + 9*td - 20050 <= 0 and q
 = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (td - 400 >=
0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 62500*n -
156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 156250*q -
6250*td - 1250000*z - 21759373 > 0 and q >= 0 and q - 40 <= 0 and 9*n -
380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2
*p1 - 7 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320
 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td
 = 0 and z = 0) or 500000*n - 21875*td - 10000000*z - 268981234 < 0 and
500000*n - 21875*td - 10000000*z - 236731234 > 0 and q >= 0 and q - 40
<= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 -
 7 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) or q >= 0 and q - 40 <= 0 and 100*n - 10*td - 2500*z - 36519
>= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 3575*n - 380*td - 95000
*z - 1387722 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and (400*q
 + 9*td - 20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or
400*q + 9*td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or q >= 0 and q - 40 <= 0 and 100*n - 10*td - 2500*z - 36519 >= 0 and
100*n - 10*td - 2500*z - 46519 <= 0 and 3575*n - 380*td - 95000*z -
1387722 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and (400*q + 9*
td - 20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q
+ 9*td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 20*
n - 2*td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 and
q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7
 >= 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 >
 0 and 9*td - 20050 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 20*n - 50*q - 2*td - 500*z - 6535 > 0
and 100*n - 250*q - 10*td - 2500*z - 36519 <= 0 and q >= 0 and q - 40 <=
 0 and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td -
20050 <= 0 and 2*p1 - 7 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) or 160*n - 7*td - 4000*z - 72330 > 0
and 800*n - 35*td - 20000*z - 392402 <= 0 and q >= 0 and q - 40 <= 0 and
 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950 >= 0
and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 - 7 >=
0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and
20*n - 2*td - 300*z - 10331 <= 0 and 715*n - 76*td - 11400*z - 316578 <=
 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and (400*q + 9*td - 20050
 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0 or 400*q + 9*td - 20050
> 0 and 160*n - 7*td - 2400*z - 86698 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <=
 0 and 20*n - 2*td - 300*z - 8331 >= 0 and 20*n - 2*td - 300*z - 10331
<= 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and n - td = 0 and i2 = 0
 and 2*p1 - 7 >= 0 and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*td
 - 300*z - 8331 = 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td - 2400*
z - 86698 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) or 20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td -
 300*z - 8331 <= 0 and q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0
and i2 = 0 and 2*p1 - 7 >= 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or
 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 20*n - 50*q - 2*
td - 300*z - 5963 > 0 and 20*n - 50*q - 2*td - 300*z - 8331 <= 0 and q
>= 0 and q - 40 <= 0 and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and
400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >= 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 160*n - 7*td - 2400*
z - 67754 > 0 and 160*n - 7*td - 2400*z - 86698 <= 0 and q >= 0 and q -
40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td -
380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and
2*p1 - 7 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0),
40*i2 + q - 251 <= 0 and i2 = 0 and n - td = 0 and 715*n - 76*td - 11400
*z - 316578 <= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 20*n - 2*td -
300*z - 8331 >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td -
 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0 or 400*q + 9*td -
20050 > 0 and 160*n - 7*td - 2400*z - 86698 = 0) or 2960*i2 - 100*n + 74
*q + 10*td + 1500*z + 23081 <= 0 and i2 = 0 and n - td = 0 and n >= 0
and q - 40 <= 0 and q >= 0 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n
- 2*td - 300*z - 8331 <= 0 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n -
2*td - 300*z - 8923 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or
20*n - 2*td - 300*z - 8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and
31937500*n - 3193750*td - 571562500*z - 13629165033 = 0) and (td - 400
>= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q
 + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td -
20050 = 0) or 40*i2 + q - 251 <= 0 and i2 = 0 and n - td = 0 and 715*n -
 76*td - 11400*z - 316578 <= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and
20*n - 2*td - 300*z - 8331 >= 0 and q - 40 <= 0 and q >= 0 and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q
 + 9*td - 20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0 or 400*q
+ 9*td - 20050 > 0 and 160*n - 7*td - 2400*z - 86698 = 0) or 2960*i2 -
100*n + 324*q + 10*td + 1500*z + 23081 <= 0 and i2 = 0 and n - td = 0
and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050
 <= 0 and (20*n - 50*q - 2*td - 300*z - 5963 > 0 and 20*n - 50*q - 2*td
- 300*z - 8331 <= 0 or 20*n - 50*q - 2*td - 300*z - 8331 > 0 and 20*n -
50*q - 2*td - 300*z - 8923 <= 0 and 4420*n - 11050*q - 442*td + 81700*z
- 3170191 = 0 or 20*n - 50*q - 2*td - 300*z - 8923 > 0 and 20*n - 50*q -
 2*td - 300*z - 10699 < 0 and 31937500*n - 79843750*q - 3193750*td -
571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3
*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*
z + 1015 = 0 or td = 0 and z = 0) or 23680*i2 - 800*n + 592*q + 35*td +
12000*z + 284898 <= 0 and i2 = 0 and n - td = 0 and 180*n + 171*td -
380950 >= 0 and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and
q >= 0 and 400*q + 9*td - 20050 >= 0 and (160*n - 7*td - 2400*z - 67754
> 0 and 160*n - 7*td - 2400*z - 86698 <= 0 or 160*n - 7*td - 2400*z -
86698 > 0 and 160*n - 7*td - 2400*z - 91434 <= 0 and 35360*n - 1547*td +
 653600*z - 29792578 = 0 or 160*n - 7*td - 2400*z - 91434 > 0 and 160*n
- 7*td - 2400*z - 105642 < 0 and 255500000*n - 11178125*td - 4572500000*
z - 141050664014 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400
*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015
= 0 or td = 0 and z = 0) or 40*i2 + q - 291 <= 0 and i2 = 0 and n - td =
 0 and 3575*n - 380*td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500
*z - 46519 <= 0 and 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0
and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and 100*n - 250*q - 10*td -
 2500*z - 36519 = 0 or 400*q + 9*td - 20050 > 0 and 800*n - 35*td -
20000*z - 392402 = 0) or 38440*i2 - 1000*n + 961*q + 100*td + 25000*z +
85539 <= 0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >=
 0 and (100*n - 10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z -
6535 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td -
500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*
td - 2500*z - 36519 > 0 and 100*n - 10*td - 2500*z - 48051 < 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td
 - 20050 = 0) or 40*i2 + q - 251 <= 0 and i2 = 0 and n - td = 0 and 715*
n - 76*td - 19000*z - 248330 <= 0 and 20*n - 2*td - 500*z - 8535 <= 0
and 20*n - 2*td - 500*z - 6535 >= 0 and q - 40 <= 0 and q >= 0 and 50*z
- 449 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*td - 500
*z - 6535 = 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td - 4000*z -
72330 = 0) or 8840*i2 + 221*q + 10000*z - 145271 <= 0 and i2 = 0 and n -
 td = 0 and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and 4420*n
- 442*td + 81700*z - 3612191 <= 0 and 4420*n - 442*td + 81700*z -
3170191 >= 0 and q - 40 <= 0 and q >= 0 and (250*z - 3129 < 0 and 50*z -
 449 >= 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0
and 250*z - 1361 > 0 and 108437500*z - 865858649 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*
td - 20050 <= 0 and 4420*n - 11050*q - 442*td + 81700*z - 3170191 = 0 or
 400*q + 9*td - 20050 > 0 and 35360*n - 1547*td + 653600*z - 29792578 =
0) or 40*i2 + q - 291 <= 0 and i2 = 0 and n - td = 0 and 3575*n - 380*td
 - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and
100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and (td -
 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
400*q + 9*td - 20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0
 or 400*q + 9*td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0)
or 38440*i2 - 1000*n + 3461*q + 100*td + 25000*z + 85539 <= 0 and i2 = 0
 and n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*
q + 9*td - 20050 <= 0 and (100*n - 250*q - 10*td - 2500*z - 17299 > 0
and 20*n - 50*q - 2*td - 500*z - 6535 <= 0 and 4420*n - 11050*q - 442*td
 + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td - 500*z - 6535 > 0 and
100*n - 250*q - 10*td - 2500*z - 36519 <= 0 or 100*n - 250*q - 10*td -
2500*z - 36519 > 0 and 100*n - 250*q - 10*td - 2500*z - 48051 < 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) or 153760*i2 - 4000*n + 3844*q + 175*td + 100000*z + 843406 <= 0
and i2 = 0 and n - td = 0 and 180*n + 171*td - 380950 >= 0 and td - 450
>= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td -
 20050 >= 0 and (800*n - 35*td - 20000*z - 238642 > 0 and 160*n - 7*td -
 4000*z - 72330 <= 0 and 35360*n - 1547*td + 653600*z - 29792578 = 0 or
160*n - 7*td - 4000*z - 72330 > 0 and 800*n - 35*td - 20000*z - 392402
<= 0 or 800*n - 35*td - 20000*z - 392402 > 0 and 800*n - 35*td - 20000*z
 - 484658 < 0 and 1498500000*n - 65559375*td - 32657500000*z - 780627025498
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) or 102125000*i2 + 4468750*n + 2553125*q - 475000*td -
95000000*z - 2703046723 <= 0 and i2 = 0 and n - td = 0 and 9*n - 15200
<= 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (2234375*n - 237500*td -
47500000*z - 1235356174 < 0 and 2234375*n - 237500*td - 47500000*z -
1031106174 >= 0 and 1141765625*n - 121362500*td - 21719375000*z - 517908271254
 = 0 or 2234375*n - 237500*td - 47500000*z - 1031106174 < 0 and 2234375*
n - 237500*td - 47500000*z - 980043674 >= 0 and 6696421875*n - 711787500
*td - 155123125000*z - 2816019660178 = 0 or 2234375*n - 237500*td -
47500000*z - 980043674 < 0 and 2234375*n - 237500*td - 47500000*z -
826856174 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and 9*n - 380*q = 0
or 400*q + 9*td - 20050 > 0 and 180*n + 171*td - 380950 = 0) or 5375000*
i2 + 250000*n + 134375*q - 25000*td - 5000000*z - 167265617 <= 0 and i2
= 0 and n - td = 0 and 9*n - 15200 >= 0 and q - 40 <= 0 and q >= 0 and (
62500*n - 6250*td - 1250000*z - 38759373 < 0 and 62500*n - 6250*td -
1250000*z - 33384373 >= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033
 = 0 or 62500*n - 6250*td - 1250000*z - 33384373 < 0 and 62500*n - 6250*
td - 1250000*z - 32040623 >= 0 and 187312500*n - 18731250*td - 4082187500
*z - 92837030531 = 0 or 62500*n - 6250*td - 1250000*z - 32040623 < 0 and
 62500*n - 6250*td - 1250000*z - 28009373 > 0) and (td - 400 >= 0 and td
 - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and q - 40 = 0 or 400*q + 9*td - 20050 > 0 and td - 450 = 0)
or 40*i2 + q - 251 <= 0 and i2 = 0 and n - td = 0 and 2234375*n - 237500
*td - 47500000*z - 1031106174 <= 0 and 62500*n - 6250*td - 1250000*z -
33384373 <= 0 and 62500*n - 6250*td - 1250000*z - 27134373 >= 0 and q -
40 <= 0 and q >= 0 and 156250*z + 549999 = 0 and (td - 400 >= 0 and td -
 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 27134373 = 0
or 400*q + 9*td - 20050 > 0 and 500000*n - 21875*td - 10000000*z -
279731234 = 0) or 12775000*i2 + 319375*q - 1250000*z - 84563117 <= 0 and
 i2 = 0 and n - td = 0 and 1141765625*n - 121362500*td - 21719375000*z -
 517908271254 <= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033
 <= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 >= 0 and q
 - 40 <= 0 and q >= 0 and (156250*z + 6937499 > 0 and 156250*z + 549999
<= 0 or 156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z
- 865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and
(td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (400*q + 9*td - 20050 <= 0 and 31937500*n - 79843750*q - 3193750*td
- 571562500*z - 13629165033 = 0 or 400*q + 9*td - 20050 > 0 and
255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) or 40*i2 +
q - 291 <= 0 and i2 = 0 and n - td = 0 and 2234375*n - 237500*td -
47500000*z - 980043674 <= 0 and 62500*n - 6250*td - 1250000*z - 32040623
 <= 0 and 62500*n - 6250*td - 1250000*z - 25790623 >= 0 and q - 40 <= 0
and q >= 0 and 78125*z - 741562 = 0 and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0
and 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 = 0 or 400*q + 9
*td - 20050 > 0 and 500000*n - 21875*td - 10000000*z - 268981234 = 0) or
 14985000*i2 + 374625*q + 1250000*z - 120880867 <= 0 and i2 = 0 and n -
td = 0 and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178
<= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0 and
 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and q - 40
<= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0
 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z -
 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0) and (td
 - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >=
0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and
(400*q + 9*td - 20050 <= 0 and 187312500*n - 468281250*q - 18731250*td -
 4082187500*z - 74105780531 = 0 or 400*q + 9*td - 20050 > 0 and 1498500000
*n - 65559375*td - 32657500000*z - 780627025498 = 0) or 5375000*i2 +
250000*n - 490625*q - 25000*td - 5000000*z - 142265617 <= 0 and i2 = 0
and n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 400*q
 + 9*td - 20050 <= 0 and (62500*n - 156250*q - 6250*td - 1250000*z -
32509373 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 27134373 >=
0 and 31937500*n - 79843750*q - 3193750*td - 571562500*z - 13629165033 =
 0 or 62500*n - 156250*q - 6250*td - 1250000*z - 27134373 < 0 and 62500*
n - 156250*q - 6250*td - 1250000*z - 25790623 >= 0 and 187312500*n -
468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0 or 62500*n -
156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 156250*q -
6250*td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or 10750000*i2 + 500000*n +
268750*q - 21875*td - 10000000*z - 347187484 <= 0 and i2 = 0 and n - td
= 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0 and 9*td - 20050
<= 0 and q - 40 <= 0 and q >= 0 and 400*q + 9*td - 20050 >= 0 and (
500000*n - 21875*td - 10000000*z - 322731234 < 0 and 500000*n - 21875*td
 - 10000000*z - 279731234 >= 0 and 255500000*n - 11178125*td - 4572500000
*z - 141050664014 = 0 or 500000*n - 21875*td - 10000000*z - 279731234 <
0 and 500000*n - 21875*td - 10000000*z - 268981234 >= 0 and 1498500000*n
 - 65559375*td - 32657500000*z - 780627025498 = 0 or 500000*n - 21875*td
 - 10000000*z - 268981234 < 0 and 500000*n - 21875*td - 10000000*z -
236731234 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0),
62500*n - 156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n -
156250*q - 6250*td - 1250000*z - 21759373 > 0 and 400*q + 9*td - 20050
<= 0 and i2 = 0 and n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and
q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) or 500000*n - 21875*td - 10000000*z - 268981234 < 0 and
500000*n - 21875*td - 10000000*z - 236731234 > 0 and 400*q + 9*td -
20050 >= 0 and i2 = 0 and n - td = 0 and 180*n + 171*td - 380950 >= 0
and td - 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td -
1250000*z - 21759373 > 0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40
 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q +
 9*td - 20050 > 0 and 9*td - 20050 = 0) or i2 = 0 and n - td = 0 and
3575*n - 380*td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z -
46519 <= 0 and 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and q
 >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (400*q + 9*td - 20050 <= 0 and 100*n - 250*q - 10*td -
2500*z - 36519 = 0 or 400*q + 9*td - 20050 > 0 and 800*n - 35*td - 20000
*z - 392402 = 0) or i2 = 0 and n - td = 0 and 3575*n - 380*td - 95000*z
- 1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 100*n - 10*td
 - 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
 20050 <= 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q + 9*
td - 20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) or 20*n - 50*q
- 2*td - 500*z - 6535 > 0 and 100*n - 250*q - 10*td - 2500*z - 36519 <=
0 and 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 9*n - 380*
q >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or 160*n - 7*td - 4000*z - 72330
> 0 and 800*n - 35*td - 20000*z - 392402 <= 0 and 400*q + 9*td - 20050
>= 0 and i2 = 0 and n - td = 0 and 180*n + 171*td - 380950 >= 0 and td -
 450 >= 0 and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 20*n -
2*td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 and i2 =
 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q +
9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050
 = 0) or i2 = 0 and n - td = 0 and 715*n - 76*td - 11400*z - 316578 <= 0
 and 20*n - 2*td - 300*z - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >=
0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td - 20050 <= 0 and 20*n
 - 50*q - 2*td - 300*z - 8331 = 0 or 400*q + 9*td - 20050 > 0 and 160*n
- 7*td - 2400*z - 86698 = 0) or i2 = 0 and n - td = 0 and 715*n - 76*td
- 11400*z - 316578 <= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 20*n -
2*td - 300*z - 8331 >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*
td - 20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8331 = 0 or 400*q + 9*
td - 20050 > 0 and 160*n - 7*td - 2400*z - 86698 = 0) or 20*n - 50*q - 2
*td - 300*z - 5963 > 0 and 20*n - 50*q - 2*td - 300*z - 8331 <= 0 and
400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 9*n - 380*q >= 0
 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) or 160*n - 7*td - 2400*z - 67754 > 0 and
160*n - 7*td - 2400*z - 86698 <= 0 and 400*q + 9*td - 20050 >= 0 and i2
= 0 and n - td = 0 and 180*n + 171*td - 380950 >= 0 and td - 450 >= 0
and 9*td - 20050 <= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 20*n - 2*td - 300
*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0 and i2 = 0 and n - td
= 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (400*q + 9*td -
20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0),
20*n - 50*q - 2*td - 300*z - 8331 < 0 and n - td = 0 and 715*n - 76*td -
 11400*z - 316578 <= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 20*n - 2
*td - 300*z - 8331 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1 - 7 >= 0 and
 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) or 20*n - 50*q - 2*td - 300*z -
8923 < 0 and n - td = 0 and 715*n - 76*td - 11400*z - 339074 <= 0 and 20
*n - 2*td - 300*z - 10923 <= 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q
 - 40 <= 0 and q >= 0 and 125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td
 + 750*z - 4647 > 0 and n - td = 0 and 112480*i2 - 3575*n + 351500*p1 +
2812*q + 380*td + 57000*z - 353172 >= 0 and 1480*i2 - 50*n + 4625*p1 +
37*q + 5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*
td + 750*z - 4647 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 +
2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 +
2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250
*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0
and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2 + 79843750*p1 +
638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0 and td - 700 <
0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
 - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 +
250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 20*n - 50*q - 2*td - 500*z -
 6535 < 0 and n - td = 0 and 715*n - 76*td - 19000*z - 248330 <= 0 and
20*n - 2*td - 500*z - 8535 <= 0 and 20*n - 2*td - 500*z - 6535 >= 0 and
q - 40 <= 0 and q >= 0 and 50*z - 449 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 4420*n - 11050*q - 442*td + 81700*z -
3170191 < 0 and n - td = 0 and 158015*n - 16796*td + 3104600*z -
120467258 <= 0 and 4420*n - 442*td + 81700*z - 3612191 <= 0 and 4420*n -
 442*td + 81700*z - 3170191 >= 0 and q - 40 <= 0 and q >= 0 and (250*z -
 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0 and 125*z - 1012 >= 0 or
 125*z - 1012 < 0 and 250*z - 1361 > 0 and 108437500*z - 865858649 = 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (2*p1 - 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 +
55250*p1 + 442*q + 20000*z - 483917 = 0) or 100*n - 250*q - 10*td - 2500
*z - 36519 < 0 and n - td = 0 and 3575*n - 380*td - 95000*z - 1387722 <=
 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 100*n - 10*td - 2500*z -
36519 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) or 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*
td + 50000*z - 669797 > 0 and n - td = 0 and 1460720*i2 - 35750*n +
4564750*p1 + 36518*q + 3800*td + 950000*z - 12726143 >= 0 and 76880*i2 -
 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and 76880*
i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and q
- 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q -
 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) or 31937500*n - 79843750*q - 3193750*td - 571562500*
z - 13629165033 < 0 and n - td = 0 and 1141765625*n - 121362500*td - 21719375000
*z - 517908271254 <= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033
 <= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 >= 0 and q
 - 40 <= 0 and q >= 0 and (156250*z + 6937499 > 0 and 156250*z + 549999
<= 0 or 156250*z + 549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z
- 865858649 = 0 or 78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and
(td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) or
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 < 0
 and n - td = 0 and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0
and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and q -
40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z - 3356249
>= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125
*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0) and
 (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) or
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 < 0 and n - td = 0 and 204250000*i2 + 8937500*n + 638281250*
p1 + 5106250*q - 950000*td - 190000000*z - 7640077821 <= 0 and 10750000*
i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z -
452109359 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q -
50000*td - 10000000*z - 402109359 >= 0 and q - 40 <= 0 and q >= 0 and (
80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
or n - td = 0 and n >= 0 and q - 40 <= 0 and q > 0 and 20*n - 2*td - 300
*z - 8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and
(td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or n - td = 0 and n >= 0 and q - 40 <= 0 and q > 0 and (20*n - 2*td -
300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0 or 20*n - 2*td -
300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0 and 4420*n - 442*td
 + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z - 8923 > 0 and 20*n - 2*
td - 300*z - 10699 < 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 300*z - 2411 = 0 or 2*p1
- 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0)
or n - td = 0 and n >= 0 and q - 40 <= 0 and q > 0 and 100*n - 10*td -
2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
 0) or n - td = 0 and n >= 0 and q - 40 <= 0 and q > 0 and (100*n - 10*
td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0 and 4420*n -
 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z - 6535 > 0 and
100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500*z - 36519 >
0 and 100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n - 18731250*td -
 4082187500*z - 74105780531 = 0) and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 20*n - 2*td
- 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 +
1922*q + 200*td + 50000*z - 669797 = 0) or n - td = 0 and n >= 0 and q -
 40 <= 0 and q > 0 and (62500*n - 6250*td - 1250000*z - 32509373 < 0 and
 62500*n - 6250*td - 1250000*z - 27134373 >= 0 and 31937500*n - 3193750*
td - 571562500*z - 13629165033 = 0 or 62500*n - 6250*td - 1250000*z -
27134373 < 0 and 62500*n - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0 or 62500*n -
6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td - 1250000*z -
21759373 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 62500*n - 6250*td - 1250000*z
 - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*
p1 + 268750*q - 50000*td - 10000000*z - 402109359 = 0) or n - td = 0 and
 n >= 0 and q - 40 <= 0 and q > 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0
and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q
 + 5*td + 750*z - 4647 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2
+ 250*p1 + 2*q - 1457 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 669797 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 +
268750*q - 50000*td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0
and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or n - td = 0 and 9
*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and 20*n - 50*q - 2*td - 300*
z - 8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and (20*n
- 50*q - 2*td - 300*z - 5963 > 0 and 20*n - 50*q - 2*td - 300*z - 8331
<= 0 or 20*n - 50*q - 2*td - 300*z - 8331 > 0 and 20*n - 50*q - 2*td -
300*z - 8923 <= 0 and 4420*n - 11050*q - 442*td + 81700*z - 3170191 = 0
or 20*n - 50*q - 2*td - 300*z - 8923 > 0 and 20*n - 50*q - 2*td - 300*z
- 10699 < 0 and 31937500*n - 79843750*q - 3193750*td - 571562500*z - 13629165033
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (2*p1 - 7 < 0 and 20*n - 50*q - 2*td - 300*z - 2411 = 0
or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z -
4647 = 0) or n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0
and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*
i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) or n - td = 0 and 9*n - 380*q >= 0 and
q - 40 <= 0 and q >= 0 and (100*n - 250*q - 10*td - 2500*z - 17299 > 0
and 20*n - 50*q - 2*td - 500*z - 6535 <= 0 and 4420*n - 11050*q - 442*td
 + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td - 500*z - 6535 > 0 and
100*n - 250*q - 10*td - 2500*z - 36519 <= 0 or 100*n - 250*q - 10*td -
2500*z - 36519 > 0 and 100*n - 250*q - 10*td - 2500*z - 48051 < 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) and (2*p1 - 7 < 0 and 20*n - 50*q - 2*td - 500*z + 1153 = 0 or 2*p1
 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z
- 669797 = 0) or n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q
>= 0 and (62500*n - 156250*q - 6250*td - 1250000*z - 32509373 < 0 and
62500*n - 156250*q - 6250*td - 1250000*z - 27134373 >= 0 and 31937500*n
- 79843750*q - 3193750*td - 571562500*z - 13629165033 = 0 or 62500*n -
156250*q - 6250*td - 1250000*z - 27134373 < 0 and 62500*n - 156250*q -
6250*td - 1250000*z - 25790623 >= 0 and 187312500*n - 468281250*q -
18731250*td - 4082187500*z - 74105780531 = 0 or 62500*n - 156250*q -
6250*td - 1250000*z - 25790623 < 0 and 62500*n - 156250*q - 6250*td -
1250000*z - 21759373 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 62500*n - 156250*q -
 6250*td - 1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 +
500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z - 402109359 =
0) or n - td = 0 and 9*n - 380*q >= 0 and q - 40 <= 0 and q >= 0 and (80
*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 80*i2 +
250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 76880*
i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0),
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
q >= 0 and q - 40 <= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td -
20050 > 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 -
 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
 and z = 0) or q >= 0 and q - 40 <= 0 and n - td = 0 and i2 = 0 and 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 400*q + 9*td - 20050 >
 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
 - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
 = 0) or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457
 <= 0 and q >= 0 and q - 40 <= 0 and n - td = 0 and i2 = 0 and 400*q + 9
*td - 20050 > 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or
2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and n - td = 0 and i2 = 0
 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 400*q + 9*td
- 20050 > 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320
= 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td
= 0 and z = 0) or 80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2
*q - 1377 <= 0 and q >= 0 and q - 40 <= 0 and n - td = 0 and i2 = 0 and
400*q + 9*td - 20050 > 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q -
577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and q >= 0 and q - 40 <= 0 and n - td = 0
and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and 80*i2 +
250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700
 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2
*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0
and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1457 = 0 and 400*q + 9*td - 20050 <= 0 and (td - 400 >= 0 and td - 700 <
 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*
td - 300*z + 1015 = 0 or td = 0 and z = 0) or 80*i2 + 250*p1 + 2*q -
1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and q >= 0 and q - 40 <= 0
 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 <
 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and
 q - 40 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250
*p1 + 2*q - 1377 = 0 and 400*q + 9*td - 20050 <= 0 and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 80*i2 + 250*
p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and q >= 0 and
q - 40 <= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and
(2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0),
400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0
 and n >= 0 and q - 40 <= 0 and q >= 0 and 20*n - 2*td - 300*z - 8331 =
0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td
- 990 < 0 and 4*td - 600*z + 3245 = 0) or 400*q + 9*td - 20050 > 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0
and q >= 0 and 100*n - 10*td - 2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and
80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and
3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0 and 4*td - 600
*z + 3245 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385
= 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (80*i2 +
250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 1480*i2
 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0 or 80*i2 + 250*p1 + 2
*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 76880*i2 - 2000*n
 + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0 or 80*i2 + 250*p1
+ 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 +
500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 =
0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 6130 = 0 or td
 - 700 >= 0 and td - 990 < 0 and 4*td - 600*z + 3245 = 0) and (2*p1 - 7
< 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td
 - 20050 > 0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q
>= 0 and 20*n - 2*td - 300*z - 8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and
 n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 100*n - 10*td -
2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0 and n >= 0 and
 q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
 250*p1 + 2*q - 1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td +
750*z - 4647 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 +
 2*q - 1457 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td +
50000*z - 669797 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*
p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q
- 50000*td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700
 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2
*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2
+ 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0
and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and n >= 0 and q - 40 <=
 0 and q >= 0 and 20*n - 2*td - 300*z - 8331 = 0 and 2*p1 - 7 >= 0 and
80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and
3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0 and 4*td - 600
*z + 3245 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385
 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 100*n - 10
*td - 2500*z - 36519 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 6130 = 0
 or td - 700 >= 0 and td - 990 < 0 and 4*td - 600*z + 3245 = 0) or 400*q
 + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and
 n >= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0
and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q
 + 5*td + 750*z - 4647 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2
+ 250*p1 + 2*q - 1457 <= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 669797 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 +
268750*q - 50000*td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990
< 0 and 4*td - 600*z + 3245 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 +
2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0
and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and 20*n - 2*td -
300*z - 8331 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and n >= 0 and
 q - 40 <= 0 and q >= 0 and 100*n - 10*td - 2500*z - 36519 = 0 and 2*p1
- 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td -
 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050
 <= 0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0
and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <=
0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0 or 80*i2
 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td -
10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and 200*i2 -
10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >=
 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40
<= 0 and q >= 0 and 120*n + 3*td + 200*z - 80636 = 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0
or td - 990 < 0 and td - 700 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2
 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470
 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q -
40 <= 0 and q >= 0 and 600*n + 15*td - 5000*z - 372364 = 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700
 < 0 or td - 990 < 0 and td - 700 = 0) or 400*q + 9*td - 20050 > 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z -
116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0
and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*
i2 + 250*p1 + 2*q - 1377 <= 0 and 17760*i2 - 600*n + 55500*p1 + 444*q -
15*td - 1000*z + 97486 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2
+ 250*p1 + 2*q - 1457 <= 0 and 230640*i2 - 6000*n + 720750*p1 + 5766*q -
 150*td + 50000*z - 476891 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80
*i2 + 250*p1 + 2*q - 1697 < 0 and 32250000*i2 + 1500000*n + 100781250*p1
 + 806250*q + 37500*td - 5000000*z - 1589453077 = 0) and (td - 400 >= 0
and td - 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1 - 7 < 0 and
 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td -
20050 > 0 and i2 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470
>= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q -
40 <= 0 and q >= 0 and 120*n + 3*td + 200*z - 80636 = 0 and 2*p1 - 7 >=
0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 <
0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td
 - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and
 i2 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td
+ 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q
>= 0 and 600*n + 15*td - 5000*z - 372364 = 0 and 2*p1 - 7 >= 0 and 80*i2
 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and
 n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td + 400*z -
3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q >= 0 and (
80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
 17760*i2 - 600*n + 55500*p1 + 444*q - 15*td - 1000*z + 97486 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
230640*i2 - 6000*n + 720750*p1 + 5766*q - 150*td + 50000*z - 476891 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 32250000*i2 + 1500000*n + 100781250*p1 + 806250*q + 37500*td -
5000000*z - 1589453077 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 -
10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >=
 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40
<= 0 and q >= 0 and 120*n + 3*td + 200*z - 80636 = 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0
or td - 990 < 0 and td - 700 = 0) or 400*q + 9*td - 20050 <= 0 and 200*
i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z -
116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0
and q - 40 <= 0 and q >= 0 and 600*n + 15*td - 5000*z - 372364 = 0 and 2
*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and
td - 700 < 0 or td - 990 < 0 and td - 700 = 0) or 400*q + 9*td - 20050
<= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td +
7600*z - 116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z -
6130 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 >
0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17760*i2 - 600*n + 55500*p1 +
 444*q - 15*td - 1000*z + 97486 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0
and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 230640*i2 - 6000*n + 720750*p1
+ 5766*q - 150*td + 50000*z - 476891 = 0 or 80*i2 + 250*p1 + 2*q - 1457
> 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 32250000*i2 + 1500000*n +
100781250*p1 + 806250*q + 37500*td - 5000000*z - 1589453077 = 0) and (td
 - 400 >= 0 and td - 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1
 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q +
 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 27*n + 57*td + 7600*z -
 116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0
and q - 40 <= 0 and q >= 0 and 120*n + 3*td + 200*z - 80636 = 0 and 2*p1
 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050
 <= 0 and i2 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0
and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <=
0 and q >= 0 and 600*n + 15*td - 5000*z - 372364 = 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 <= 0 and
i2 = 0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td +
 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q >=
 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377
<= 0 and 17760*i2 - 600*n + 55500*p1 + 444*q - 15*td - 1000*z + 97486 =
0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <=
0 and 230640*i2 - 6000*n + 720750*p1 + 5766*q - 150*td + 50000*z -
476891 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 32250000*i2 + 1500000*n + 100781250*p1 + 806250*q + 37500*
td - 5000000*z - 1589453077 = 0) and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*
p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and td
- 700 >= 0 and td - 990 < 0 and 200*i2 - 10*q - td + 385 = 0 and n - td
= 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z - 355 <=
0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and 90*n - 19*
td + 150*z - 45602 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377
 = 0 or 400*q + 9*td - 20050 > 0 and td - 700 >= 0 and td - 990 < 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 81*n - 152*td + 22800*z
- 123310 >= 0 and 4*td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0
and q - 40 <= 0 and q >= 0 and 450*n - 95*td - 3750*z - 204898 = 0 and 2
*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 or 400*q + 9*td - 20050
 > 0 and td - 700 >= 0 and td - 990 < 0 and 200*i2 - 10*q - td + 385 = 0
 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600
*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0
and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <=
0 and 26640*i2 - 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0
or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0
and 691920*i2 - 18000*n + 2162250*p1 + 17298*q + 3800*td + 150000*z -
4405673 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q
- 1697 < 0 and 96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q -
950000*td - 15000000*z - 4024609231 = 0) and (2*p1 - 7 < 0 and 80*i2 +
250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and
 i2 = 0 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*
td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q
 >= 0 and 90*n - 19*td + 150*z - 45602 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and
 n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z -
 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and
450*n - 95*td - 3750*z - 204898 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1
 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td =
 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z - 355 <= 0
 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and (80*i2 +
250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 26640*
i2 - 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0 or 80*i2 +
250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 691920*
i2 - 18000*n + 2162250*p1 + 17298*q + 3800*td + 150000*z - 4405673 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q - 950000*td -
15000000*z - 4024609231 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 +
2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and td -
700 >= 0 and td - 990 < 0 and 200*i2 - 10*q - td + 385 = 0 and n - td =
0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z - 355 <= 0
and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and 90*n - 19*td
 + 150*z - 45602 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 =
 0 or 400*q + 9*td - 20050 <= 0 and td - 700 >= 0 and td - 990 < 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 81*n - 152*td + 22800*z
- 123310 >= 0 and 4*td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0
and q - 40 <= 0 and q >= 0 and 450*n - 95*td - 3750*z - 204898 = 0 and 2
*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 or 400*q + 9*td - 20050
 <= 0 and td - 700 >= 0 and td - 990 < 0 and 200*i2 - 10*q - td + 385 =
0 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td -
600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0
 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <=
 0 and 26640*i2 - 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0
or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0
and 691920*i2 - 18000*n + 2162250*p1 + 17298*q + 3800*td + 150000*z -
4405673 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q
- 1697 < 0 and 96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q -
950000*td - 15000000*z - 4024609231 = 0) and (2*p1 - 7 < 0 and 80*i2 +
250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0
and i2 = 0 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and
4*td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and
 q >= 0 and 90*n - 19*td + 150*z - 45602 = 0 and 2*p1 - 7 >= 0 and 80*i2
 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0
and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*
z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and
 450*n - 95*td - 3750*z - 204898 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n -
td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z - 355
<= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and (80*i2
+ 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 26640
*i2 - 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0 or 80*i2 +
250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 691920*
i2 - 18000*n + 2162250*p1 + 17298*q + 3800*td + 150000*z - 4405673 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q - 950000*td -
15000000*z - 4024609231 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 +
2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and 200*i2 -
 10*q - td + 385 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >=
0 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <=
0 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0
and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z -
8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n - 3193750*td
 - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0 and 4*td -
 600*z + 3245 = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 300*z - 2411 = 0
or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z -
4647 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0
and n - td = 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and 20*n - 2*td
 - 300*z - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and q - 40 <=
0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (
td - 400 >= 0 and td - 700 < 0 and 120*n + 3*td + 200*z - 80636 = 0 or
td - 700 >= 0 and td - 990 < 0 and 90*n - 19*td + 150*z - 45602 = 0) or
400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0
 and 715*n - 76*td - 11400*z - 339074 <= 0 and 20*n - 2*td - 300*z -
10923 <= 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q - 40 <= 0 and q >=
0 and 125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457
 = 0 and (td - 400 >= 0 and td - 700 < 0 and 120*n + 3*td + 200*z -
84188 = 0 or td - 700 >= 0 and td - 990 < 0 and 90*n - 19*td + 150*z -
48266 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0
and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td + 400*z
- 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q >= 0 and
(120*n + 3*td + 200*z - 66428 > 0 and 120*n + 3*td + 200*z - 80636 <= 0
or 120*n + 3*td + 200*z - 80636 > 0 and 120*n + 3*td + 200*z - 84188 <=
0 and 26520*n + 663*td + 932200*z - 25794796 = 0 or 120*n + 3*td + 200*z
 - 84188 > 0 and 120*n + 3*td + 200*z - 94844 < 0 and 191625000*n +
4790625*td - 235625000*z - 130719208948 = 0) and (td - 400 >= 0 and td -
 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1 - 7 < 0 and 120*n +
 3*td + 200*z - 45116 = 0 or 2*p1 - 7 >= 0 and 17760*i2 - 600*n + 55500*
p1 + 444*q - 15*td - 1000*z + 97486 = 0) or 400*q + 9*td - 20050 > 0 and
 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 81*n - 152*td + 22800*z
 - 123310 >= 0 and 4*td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0
and q - 40 <= 0 and q >= 0 and td - 700 >= 0 and td - 990 < 0 and (90*n
- 19*td + 150*z - 34946 > 0 and 90*n - 19*td + 150*z - 45602 <= 0 or 90*
n - 19*td + 150*z - 45602 > 0 and 90*n - 19*td + 150*z - 48266 <= 0 and
19890*n - 4199*td + 699150*z - 16058722 = 0 or 90*n - 19*td + 150*z -
48266 > 0 and 90*n - 19*td + 150*z - 56258 < 0 and 143718750*n -
30340625*td - 176718750*z - 74285891086 = 0) and (2*p1 - 7 < 0 and 90*n
- 19*td + 150*z - 18962 = 0 or 2*p1 - 7 >= 0 and 26640*i2 - 900*n +
83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0) or 400*q + 9*td - 20050 >
 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 112480*i2 - 3575*
n + 351500*p1 + 2812*q + 380*td + 57000*z - 353172 >= 0 and 1480*i2 - 50
*n + 4625*p1 + 37*q + 5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n + 4625*
p1 + 37*q + 5*td + 750*z - 4647 <= 0 and q - 40 <= 0 and q >= 0 and (80*
i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2
*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2 +
79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0
and td - 700 < 0 and 17760*i2 - 600*n + 55500*p1 + 444*q - 15*td - 1000*
z + 97486 = 0 or td - 700 >= 0 and td - 990 < 0 and 26640*i2 - 900*n +
83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0) and (2*p1 - 7 < 0 and 80*
i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 >
0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (
20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0 or 20
*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0 and
4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z - 8923 >
0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n - 3193750*td -
571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3
*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*
z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 20*n - 2*td -
300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q +
5*td + 750*z - 4647 = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n -
td = 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and 20*n - 2*td - 300*z
 - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and q - 40 <= 0 and q
>= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q +
 9*td - 20050 > 0 and i2 = 0 and n - td = 0 and 715*n - 76*td - 11400*z
- 339074 <= 0 and 20*n - 2*td - 300*z - 10923 <= 0 and 20*n - 2*td - 300
*z - 8923 >= 0 and q - 40 <= 0 and q >= 0 and 125*z - 1012 = 0 and 2*p1
- 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td -
 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050
 > 0 and i2 = 0 and n - td = 0 and 112480*i2 - 3575*n + 351500*p1 + 2812
*q + 380*td + 57000*z - 353172 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q
+ 5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td +
750*z - 4647 <= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q -
 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q -
 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 +
 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q -
2500000*z - 448579359 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
+ 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
 - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 -
10*q - td + 385 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0
 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0
 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0
and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z -
8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n - 3193750*td
 - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0 and 4*td -
 600*z + 3245 = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 300*z - 2411 = 0
or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z -
4647 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0
and n - td = 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and 20*n - 2*td
 - 300*z - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and q - 40 <=
0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (
td - 400 >= 0 and td - 700 < 0 and 120*n + 3*td + 200*z - 80636 = 0 or
td - 700 >= 0 and td - 990 < 0 and 90*n - 19*td + 150*z - 45602 = 0) or
400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td =
0 and 715*n - 76*td - 11400*z - 339074 <= 0 and 20*n - 2*td - 300*z -
10923 <= 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q - 40 <= 0 and q >=
0 and 125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457
 = 0 and (td - 400 >= 0 and td - 700 < 0 and 120*n + 3*td + 200*z -
84188 = 0 or td - 700 >= 0 and td - 990 < 0 and 90*n - 19*td + 150*z -
48266 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0
 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td + 400*z
 - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q >= 0 and
 (120*n + 3*td + 200*z - 66428 > 0 and 120*n + 3*td + 200*z - 80636 <= 0
 or 120*n + 3*td + 200*z - 80636 > 0 and 120*n + 3*td + 200*z - 84188 <=
 0 and 26520*n + 663*td + 932200*z - 25794796 = 0 or 120*n + 3*td + 200*
z - 84188 > 0 and 120*n + 3*td + 200*z - 94844 < 0 and 191625000*n +
4790625*td - 235625000*z - 130719208948 = 0) and (td - 400 >= 0 and td -
 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1 - 7 < 0 and 120*n +
 3*td + 200*z - 45116 = 0 or 2*p1 - 7 >= 0 and 17760*i2 - 600*n + 55500*
p1 + 444*q - 15*td - 1000*z + 97486 = 0) or 400*q + 9*td - 20050 <= 0
and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 81*n - 152*td +
22800*z - 123310 >= 0 and 4*td - 600*z - 355 <= 0 and 4*td - 600*z +
3245 >= 0 and q - 40 <= 0 and q >= 0 and td - 700 >= 0 and td - 990 < 0
and (90*n - 19*td + 150*z - 34946 > 0 and 90*n - 19*td + 150*z - 45602
<= 0 or 90*n - 19*td + 150*z - 45602 > 0 and 90*n - 19*td + 150*z -
48266 <= 0 and 19890*n - 4199*td + 699150*z - 16058722 = 0 or 90*n - 19*
td + 150*z - 48266 > 0 and 90*n - 19*td + 150*z - 56258 < 0 and
143718750*n - 30340625*td - 176718750*z - 74285891086 = 0) and (2*p1 - 7
 < 0 and 90*n - 19*td + 150*z - 18962 = 0 or 2*p1 - 7 >= 0 and 26640*i2
- 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0) or 400*q + 9*td
 - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 112480
*i2 - 3575*n + 351500*p1 + 2812*q + 380*td + 57000*z - 353172 >= 0 and
1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z + 353 >= 0 and 1480*i2 -
50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 <= 0 and q - 40 <= 0 and q
>= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q -
1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 17760*i2 - 600*n + 55500*p1 + 444*q -
 15*td - 1000*z + 97486 = 0 or td - 700 >= 0 and td - 990 < 0 and 26640*
i2 - 900*n + 83250*p1 + 666*q + 190*td - 1500*z - 2521 = 0) and (2*p1 -
7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*
td - 20050 <= 0 and i2 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and
 q >= 0 and (20*n - 2*td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z -
8331 <= 0 or 20*n - 2*td - 300*z - 8331 > 0 and 20*n - 2*td - 300*z -
8923 <= 0 and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td -
300*z - 8923 > 0 and 20*n - 2*td - 300*z - 10699 < 0 and 31937500*n -
3193750*td - 571562500*z - 13629165033 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
20*n - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n +
4625*p1 + 37*q + 5*td + 750*z - 4647 = 0) or 400*q + 9*td - 20050 <= 0
and i2 = 0 and n - td = 0 and 715*n - 76*td - 11400*z - 316578 <= 0 and
20*n - 2*td - 300*z - 10331 <= 0 and 20*n - 2*td - 300*z - 8331 >= 0 and
 q - 40 <= 0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0
 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
 and z = 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and
715*n - 76*td - 11400*z - 339074 <= 0 and 20*n - 2*td - 300*z - 10923 <=
 0 and 20*n - 2*td - 300*z - 8923 >= 0 and q - 40 <= 0 and q >= 0 and
125*z - 1012 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 112480*i2
- 3575*n + 351500*p1 + 2812*q + 380*td + 57000*z - 353172 >= 0 and 1480*
i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z + 353 >= 0 and 1480*i2 - 50*n
+ 4625*p1 + 37*q + 5*td + 750*z - 4647 <= 0 and q - 40 <= 0 and q >= 0
and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <=
0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <=
0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or 80*i2 + 250*
p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 25550000*i2
+ 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 <
 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td
- 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and n >= 0
and q - 40 <= 0 and q >= 0 and (100*n - 10*td - 2500*z - 17299 > 0 and
20*n - 2*td - 500*z - 6535 <= 0 and 4420*n - 442*td + 81700*z - 3170191
= 0 or 20*n - 2*td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z - 36519
 <= 0 or 100*n - 10*td - 2500*z - 36519 > 0 and 100*n - 10*td - 2500*z -
 48051 < 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 =
0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 6130 = 0 or td
 - 700 >= 0 and td - 990 < 0 and 4*td - 600*z + 3245 = 0) and (2*p1 - 7
< 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 -
2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0) or 400*q +
9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 715
*n - 76*td - 19000*z - 248330 <= 0 and 20*n - 2*td - 500*z - 8535 <= 0
and 20*n - 2*td - 500*z - 6535 >= 0 and q - 40 <= 0 and q >= 0 and 50*z
- 449 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td
- 400 >= 0 and td - 700 < 0 and 120*n + 3*td - 1000*z - 69860 = 0 or td
- 700 >= 0 and td - 990 < 0 and 90*n - 19*td - 750*z - 37520 = 0) or 400
*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0
and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and 4420*n - 442*td
 + 81700*z - 3612191 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >= 0
and q - 40 <= 0 and q >= 0 and (250*z - 3129 < 0 and 50*z - 449 >= 0 or
50*z - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z -
1361 > 0 and 108437500*z - 865858649 = 0) and (td - 400 >= 0 and td -
700 < 0 and 26520*n + 663*td + 932200*z - 25794796 = 0 or td - 700 >= 0
and td - 990 < 0 and 19890*n - 4199*td + 699150*z - 16058722 = 0) and (2
*p1 - 7 < 0 and 50*z - 891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1
+ 442*q + 20000*z - 483917 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 -
 10*q - td + 385 = 0 and n - td = 0 and 3575*n - 380*td - 95000*z -
1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 100*n - 10*td -
 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1 - 7 >= 0 and 80
*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td - 700 < 0 and
600*n + 15*td - 5000*z - 372364 = 0 or td - 700 >= 0 and td - 990 < 0
and 450*n - 95*td - 3750*z - 204898 = 0) or 400*q + 9*td - 20050 > 0 and
 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z -
 116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0
and q - 40 <= 0 and q >= 0 and (600*n + 15*td - 5000*z - 257044 > 0 and
120*n + 3*td - 1000*z - 69860 <= 0 and 26520*n + 663*td + 932200*z -
25794796 = 0 or 120*n + 3*td - 1000*z - 69860 > 0 and 600*n + 15*td -
5000*z - 372364 <= 0 or 600*n + 15*td - 5000*z - 372364 > 0 and 600*n +
15*td - 5000*z - 441556 < 0 and 374625000*n + 9365625*td - 1920625000*z
- 243897029812 = 0) and (td - 400 >= 0 and td - 700 < 0 or td - 990 < 0
and td - 700 = 0) and (2*p1 - 7 < 0 and 120*n + 3*td - 1000*z - 23732 =
0 or 2*p1 - 7 >= 0 and 230640*i2 - 6000*n + 720750*p1 + 5766*q - 150*td
+ 50000*z - 476891 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q -
td + 385 = 0 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0
and 4*td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0
 and q >= 0 and td - 700 >= 0 and td - 990 < 0 and (450*n - 95*td - 3750
*z - 118408 > 0 and 90*n - 19*td - 750*z - 37520 <= 0 and 19890*n - 4199
*td + 699150*z - 16058722 = 0 or 90*n - 19*td - 750*z - 37520 > 0 and
450*n - 95*td - 3750*z - 204898 <= 0 or 450*n - 95*td - 3750*z - 204898
> 0 and 450*n - 95*td - 3750*z - 256792 < 0 and 93656250*n - 19771875*td
 - 480156250*z - 45494960578 = 0) and (2*p1 - 7 < 0 and 90*n - 19*td -
750*z - 2924 = 0 or 2*p1 - 7 >= 0 and 691920*i2 - 18000*n + 2162250*p1 +
 17298*q + 3800*td + 150000*z - 4405673 = 0) or 400*q + 9*td - 20050 > 0
 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 1460720*i2 - 35750*
n + 4564750*p1 + 36518*q + 3800*td + 950000*z - 12726143 >= 0 and 76880*
i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and
76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0
and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*
i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z
- 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q
 - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q
 - 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0) and (td - 400 >= 0 and td - 700 < 0 and 230640*i2 - 6000*
n + 720750*p1 + 5766*q - 150*td + 50000*z - 476891 = 0 or td - 700 >= 0
and td - 990 < 0 and 691920*i2 - 18000*n + 2162250*p1 + 17298*q + 3800*
td + 150000*z - 4405673 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
- 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n
 - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (100*n - 10*td -
2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0 and 4420*n - 442*
td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z - 6535 > 0 and 100*n -
 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500*z - 36519 > 0 and
100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n - 18731250*td - 4082187500
*z - 74105780531 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400
*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015
= 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 500*z +
1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 669797 = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0
and n - td = 0 and 715*n - 76*td - 19000*z - 248330 <= 0 and 20*n - 2*td
 - 500*z - 8535 <= 0 and 20*n - 2*td - 500*z - 6535 >= 0 and q - 40 <= 0
 and q >= 0 and 50*z - 449 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*
q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320
 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td
 = 0 and z = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0
and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and 4420*n - 442*td
 + 81700*z - 3612191 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >= 0
and q - 40 <= 0 and q >= 0 and (250*z - 3129 < 0 and 50*z - 449 >= 0 or
50*z - 449 < 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z -
1361 > 0 and 108437500*z - 865858649 = 0) and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
50*z - 891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*
z - 483917 = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0
and 3575*n - 380*td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z
- 46519 <= 0 and 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and
 q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400
*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0 and 1460720*i2 - 35750*n
 + 4564750*p1 + 36518*q + 3800*td + 950000*z - 12726143 >= 0 and 76880*
i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and
76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0
and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*
i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z
- 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q
 - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q
 - 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td
+ 385 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (100*
n - 10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0 and
4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z - 6535 >
0 and 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500*z -
36519 > 0 and 100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n -
18731250*td - 4082187500*z - 74105780531 = 0) and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0
and 4*td - 600*z + 3245 = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 500*z +
 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q +
200*td + 50000*z - 669797 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 -
 10*q - td + 385 = 0 and n - td = 0 and 715*n - 76*td - 19000*z - 248330
 <= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 20*n - 2*td - 500*z - 6535
 >= 0 and q - 40 <= 0 and q >= 0 and 50*z - 449 = 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0
and 120*n + 3*td - 1000*z - 69860 = 0 or td - 700 >= 0 and td - 990 < 0
and 90*n - 19*td - 750*z - 37520 = 0) or 400*q + 9*td - 20050 <= 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 158015*n - 16796*td +
3104600*z - 120467258 <= 0 and 4420*n - 442*td + 81700*z - 3612191 <= 0
and 4420*n - 442*td + 81700*z - 3170191 >= 0 and q - 40 <= 0 and q >= 0
and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0 and 125*z -
1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 > 0 and 108437500*z -
865858649 = 0) and (td - 400 >= 0 and td - 700 < 0 and 26520*n + 663*td
+ 932200*z - 25794796 = 0 or td - 700 >= 0 and td - 990 < 0 and 19890*n
- 4199*td + 699150*z - 16058722 = 0) and (2*p1 - 7 < 0 and 50*z - 891 =
0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 =
0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n -
 td = 0 and 3575*n - 380*td - 95000*z - 1387722 <= 0 and 100*n - 10*td -
 2500*z - 46519 <= 0 and 100*n - 10*td - 2500*z - 36519 >= 0 and q - 40
<= 0 and q >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0
and (td - 400 >= 0 and td - 700 < 0 and 600*n + 15*td - 5000*z - 372364
= 0 or td - 700 >= 0 and td - 990 < 0 and 450*n - 95*td - 3750*z -
204898 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 =
0 and n - td = 0 and 27*n + 57*td + 7600*z - 116470 >= 0 and 3*td + 400*
z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0 and q - 40 <= 0 and q >= 0
and (600*n + 15*td - 5000*z - 257044 > 0 and 120*n + 3*td - 1000*z -
69860 <= 0 and 26520*n + 663*td + 932200*z - 25794796 = 0 or 120*n + 3*
td - 1000*z - 69860 > 0 and 600*n + 15*td - 5000*z - 372364 <= 0 or 600*
n + 15*td - 5000*z - 372364 > 0 and 600*n + 15*td - 5000*z - 441556 < 0
and 374625000*n + 9365625*td - 1920625000*z - 243897029812 = 0) and (td
- 400 >= 0 and td - 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1
- 7 < 0 and 120*n + 3*td - 1000*z - 23732 = 0 or 2*p1 - 7 >= 0 and
230640*i2 - 6000*n + 720750*p1 + 5766*q - 150*td + 50000*z - 476891 = 0)
 or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n -
td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z - 355
<= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and td -
700 >= 0 and td - 990 < 0 and (450*n - 95*td - 3750*z - 118408 > 0 and
90*n - 19*td - 750*z - 37520 <= 0 and 19890*n - 4199*td + 699150*z -
16058722 = 0 or 90*n - 19*td - 750*z - 37520 > 0 and 450*n - 95*td -
3750*z - 204898 <= 0 or 450*n - 95*td - 3750*z - 204898 > 0 and 450*n -
95*td - 3750*z - 256792 < 0 and 93656250*n - 19771875*td - 480156250*z -
 45494960578 = 0) and (2*p1 - 7 < 0 and 90*n - 19*td - 750*z - 2924 = 0
or 2*p1 - 7 >= 0 and 691920*i2 - 18000*n + 2162250*p1 + 17298*q + 3800*
td + 150000*z - 4405673 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 -
10*q - td + 385 = 0 and n - td = 0 and 1460720*i2 - 35750*n + 4564750*p1
 + 36518*q + 3800*td + 950000*z - 12726143 >= 0 and 76880*i2 - 2000*n +
240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and 76880*i2 - 2000*
n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and q - 40 <= 0
and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q
 - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or
80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 230640*i2 - 6000*n + 720750*p1 + 5766
*q - 150*td + 50000*z - 476891 = 0 or td - 700 >= 0 and td - 990 < 0 and
 691920*i2 - 18000*n + 2162250*p1 + 17298*q + 3800*td + 150000*z -
4405673 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*
p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and
n >= 0 and q - 40 <= 0 and q >= 0 and (100*n - 10*td - 2500*z - 17299 >
0 and 20*n - 2*td - 500*z - 6535 <= 0 and 4420*n - 442*td + 81700*z -
3170191 = 0 or 20*n - 2*td - 500*z - 6535 > 0 and 100*n - 10*td - 2500*z
 - 36519 <= 0 or 100*n - 10*td - 2500*z - 36519 > 0 and 100*n - 10*td -
2500*z - 48051 < 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (2*p1 - 7 < 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1
- 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z -
 669797 = 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and
715*n - 76*td - 19000*z - 248330 <= 0 and 20*n - 2*td - 500*z - 8535 <=
0 and 20*n - 2*td - 500*z - 6535 >= 0 and q - 40 <= 0 and q >= 0 and 50*
z - 449 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 158015*n -
16796*td + 3104600*z - 120467258 <= 0 and 4420*n - 442*td + 81700*z -
3612191 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >= 0 and q - 40 <=
0 and q >= 0 and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0
 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 > 0 and
108437500*z - 865858649 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and 50*z - 891 = 0 or
 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0)
or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 3575*n - 380*
td - 95000*z - 1387722 <= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and
100*n - 10*td - 2500*z - 36519 >= 0 and q - 40 <= 0 and q >= 0 and 2*p1
- 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td -
 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or 400*q + 9*td - 20050
 <= 0 and i2 = 0 and n - td = 0 and 1460720*i2 - 35750*n + 4564750*p1 +
36518*q + 3800*td + 950000*z - 12726143 >= 0 and 76880*i2 - 2000*n +
240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and 76880*i2 - 2000*
n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and q - 40 <= 0
and q >= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q
 - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917 = 0 or
80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 or
80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td
= 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (62500*n - 6250*td -
1250000*z - 32509373 < 0 and 62500*n - 6250*td - 1250000*z - 27134373 >=
 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 = 0 or 62500*
n - 6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*td - 1250000*z
 - 25790623 >= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531
 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*
td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3
*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0 and 4*td - 600*
z + 3245 = 0) and (2*p1 - 7 < 0 and 62500*n - 6250*td - 1250000*z -
40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 +
 268750*q - 50000*td - 10000000*z - 402109359 = 0) or 400*q + 9*td -
20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 1141765625
*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and 31937500*n -
3193750*td - 571562500*z - 16822915033 <= 0 and 31937500*n - 3193750*td
- 571562500*z - 13629165033 >= 0 and q - 40 <= 0 and q >= 0 and (156250*
z + 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z + 549999 > 0 and
78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or 78125*z -
523438 > 0 and 156250*z - 5837501 < 0) and (td - 400 >= 0 and td - 700 <
 0 and 191625000*n + 4790625*td - 235625000*z - 130719208948 = 0 or td -
 700 >= 0 and td - 990 < 0 and 143718750*n - 30340625*td - 176718750*z -
 74285891086 = 0) and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1
- 7 >= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385
= 0 and n - td = 0 and 6696421875*n - 711787500*td - 155123125000*z - 2816019660178
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0
and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and q -
40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z - 3356249
>= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125
*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0) and
 (td - 400 >= 0 and td - 700 < 0 and 374625000*n + 9365625*td - 1920625000
*z - 243897029812 = 0 or td - 700 >= 0 and td - 990 < 0 and 93656250*n -
 19771875*td - 480156250*z - 45494960578 = 0) and (2*p1 - 7 < 0 and
156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and 29970000*i2 + 93656250*p1 +
 749250*q + 2500000*z - 569558609 = 0) or 400*q + 9*td - 20050 > 0 and
200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57*td + 7600*z -
116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z - 6130 <= 0
and q - 40 <= 0 and q >= 0 and (375000*n + 9375*td - 1250000*z -
290837488 < 0 and 375000*n + 9375*td - 1250000*z - 258587488 >= 0 and
191625000*n + 4790625*td - 235625000*z - 130719208948 = 0 or 375000*n +
9375*td - 1250000*z - 258587488 < 0 and 375000*n + 9375*td - 1250000*z -
 250524988 >= 0 and 374625000*n + 9365625*td - 1920625000*z - 243897029812
 = 0 or 375000*n + 9375*td - 1250000*z - 250524988 < 0 and 375000*n +
9375*td - 1250000*z - 226337488 > 0) and (td - 400 >= 0 and td - 700 < 0
 or td - 990 < 0 and td - 700 = 0) and (2*p1 - 7 < 0 and 375000*n + 9375
*td - 1250000*z - 339212488 = 0 or 2*p1 - 7 >= 0 and 32250000*i2 +
1500000*n + 100781250*p1 + 806250*q + 37500*td - 5000000*z - 1589453077
= 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n
- td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*td - 600*z -
355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q >= 0 and td
- 700 >= 0 and td - 990 < 0 and (281250*n - 59375*td - 937500*z -
171643741 < 0 and 281250*n - 59375*td - 937500*z - 147456241 >= 0 and
143718750*n - 30340625*td - 176718750*z - 74285891086 = 0 or 281250*n -
59375*td - 937500*z - 147456241 < 0 and 281250*n - 59375*td - 937500*z -
 141409366 >= 0 and 93656250*n - 19771875*td - 480156250*z - 45494960578
 = 0 or 281250*n - 59375*td - 937500*z - 141409366 < 0 and 281250*n -
59375*td - 937500*z - 123268741 > 0) and (2*p1 - 7 < 0 and 281250*n -
59375*td - 937500*z - 207924991 = 0 or 2*p1 - 7 >= 0 and 96750000*i2 +
4500000*n + 302343750*p1 + 2418750*q - 950000*td - 15000000*z - 4024609231
 = 0) or 400*q + 9*td - 20050 > 0 and 200*i2 - 10*q - td + 385 = 0 and n
 - td = 0 and 204250000*i2 + 8937500*n + 638281250*p1 + 5106250*q -
950000*td - 190000000*z - 7640077821 <= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 402109359 >= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q -
1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 25550000*i2 + 79843750
*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*i2 + 250*p1 + 2*q -
1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 29970000*i2 + 93656250
*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*i2 + 250*p1 + 2*q -
1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) and (td - 400 >= 0 and td
- 700 < 0 and 32250000*i2 + 1500000*n + 100781250*p1 + 806250*q + 37500*
td - 5000000*z - 1589453077 = 0 or td - 700 >= 0 and td - 990 < 0 and
96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q - 950000*td -
15000000*z - 4024609231 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
- 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n
 - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (62500*n - 6250*td -
 1250000*z - 32509373 < 0 and 62500*n - 6250*td - 1250000*z - 27134373
>= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 = 0 or
62500*n - 6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*td -
1250000*z - 25790623 >= 0 and 187312500*n - 18731250*td - 4082187500*z -
 74105780531 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and
62500*n - 6250*td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
62500*n - 6250*td - 1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 402109359 = 0) or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0
and 1141765625*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and
31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 31937500*n
- 3193750*td - 571562500*z - 13629165033 >= 0 and q - 40 <= 0 and q >= 0
 and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z +
549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or
78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0
and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2 + 79843750*
p1 + 638750*q - 2500000*z - 448579359 = 0) or 400*q + 9*td - 20050 > 0
and i2 = 0 and n - td = 0 and 6696421875*n - 711787500*td - 155123125000
*z - 2816019660178 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0
and q - 40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z -
3356249 >= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0
and 78125*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251
 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >=
0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0)
or 400*q + 9*td - 20050 > 0 and i2 = 0 and n - td = 0 and 204250000*i2 +
 8937500*n + 638281250*p1 + 5106250*q - 950000*td - 190000000*z - 7640077821
 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td -
10000000*z - 452109359 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 +
268750*q - 50000*td - 10000000*z - 402109359 >= 0 and q - 40 <= 0 and q
>= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*
q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*
q - 1697 < 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td
+ 385 = 0 and n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (
62500*n - 6250*td - 1250000*z - 32509373 < 0 and 62500*n - 6250*td -
1250000*z - 27134373 >= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 = 0 or 62500*n - 6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*
td - 1250000*z - 25790623 >= 0 and 187312500*n - 18731250*td - 4082187500
*z - 74105780531 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and
 62500*n - 6250*td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and td
 - 700 < 0 and 3*td + 400*z - 6130 = 0 or td - 700 >= 0 and td - 990 < 0
 and 4*td - 600*z + 3245 = 0) and (2*p1 - 7 < 0 and 62500*n - 6250*td -
1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 = 0) or 400*q
 + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and
 1141765625*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and
31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 31937500*n
- 3193750*td - 571562500*z - 13629165033 >= 0 and q - 40 <= 0 and q >= 0
 and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z +
549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or
78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and (td - 400 >= 0 and
td - 700 < 0 and 191625000*n + 4790625*td - 235625000*z - 130719208948 =
 0 or td - 700 >= 0 and td - 990 < 0 and 143718750*n - 30340625*td -
176718750*z - 74285891086 = 0) and (2*p1 - 7 < 0 and 156250*z + 16518749
 = 0 or 2*p1 - 7 >= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000
*z - 448579359 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td
+ 385 = 0 and n - td = 0 and 6696421875*n - 711787500*td - 155123125000*
z - 2816019660178 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0
and q - 40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z -
3356249 >= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0
and 78125*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251
 > 0) and (td - 400 >= 0 and td - 700 < 0 and 374625000*n + 9365625*td -
 1920625000*z - 243897029812 = 0 or td - 700 >= 0 and td - 990 < 0 and
93656250*n - 19771875*td - 480156250*z - 45494960578 = 0) and (2*p1 - 7
< 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and 29970000*i2 +
93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) or 400*q + 9*td -
20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and n - td = 0 and 27*n + 57
*td + 7600*z - 116470 >= 0 and 3*td + 400*z - 3730 >= 0 and 3*td + 400*z
 - 6130 <= 0 and q - 40 <= 0 and q >= 0 and (375000*n + 9375*td -
1250000*z - 290837488 < 0 and 375000*n + 9375*td - 1250000*z - 258587488
 >= 0 and 191625000*n + 4790625*td - 235625000*z - 130719208948 = 0 or
375000*n + 9375*td - 1250000*z - 258587488 < 0 and 375000*n + 9375*td -
1250000*z - 250524988 >= 0 and 374625000*n + 9365625*td - 1920625000*z -
 243897029812 = 0 or 375000*n + 9375*td - 1250000*z - 250524988 < 0 and
375000*n + 9375*td - 1250000*z - 226337488 > 0) and (td - 400 >= 0 and
td - 700 < 0 or td - 990 < 0 and td - 700 = 0) and (2*p1 - 7 < 0 and
375000*n + 9375*td - 1250000*z - 339212488 = 0 or 2*p1 - 7 >= 0 and
32250000*i2 + 1500000*n + 100781250*p1 + 806250*q + 37500*td - 5000000*z
 - 1589453077 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td +
 385 = 0 and n - td = 0 and 81*n - 152*td + 22800*z - 123310 >= 0 and 4*
td - 600*z - 355 <= 0 and 4*td - 600*z + 3245 >= 0 and q - 40 <= 0 and q
 >= 0 and td - 700 >= 0 and td - 990 < 0 and (281250*n - 59375*td -
937500*z - 171643741 < 0 and 281250*n - 59375*td - 937500*z - 147456241
>= 0 and 143718750*n - 30340625*td - 176718750*z - 74285891086 = 0 or
281250*n - 59375*td - 937500*z - 147456241 < 0 and 281250*n - 59375*td -
 937500*z - 141409366 >= 0 and 93656250*n - 19771875*td - 480156250*z - 45494960578
 = 0 or 281250*n - 59375*td - 937500*z - 141409366 < 0 and 281250*n -
59375*td - 937500*z - 123268741 > 0) and (2*p1 - 7 < 0 and 281250*n -
59375*td - 937500*z - 207924991 = 0 or 2*p1 - 7 >= 0 and 96750000*i2 +
4500000*n + 302343750*p1 + 2418750*q - 950000*td - 15000000*z - 4024609231
 = 0) or 400*q + 9*td - 20050 <= 0 and 200*i2 - 10*q - td + 385 = 0 and
n - td = 0 and 204250000*i2 + 8937500*n + 638281250*p1 + 5106250*q -
950000*td - 190000000*z - 7640077821 <= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 402109359 >= 0 and q - 40 <= 0 and q >= 0 and (80*i2 + 250*p1 + 2*q -
1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 25550000*i2 + 79843750
*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*i2 + 250*p1 + 2*q -
1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 29970000*i2 + 93656250
*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*i2 + 250*p1 + 2*q -
1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) and (td - 400 >= 0 and td
- 700 < 0 and 32250000*i2 + 1500000*n + 100781250*p1 + 806250*q + 37500*
td - 5000000*z - 1589453077 = 0 or td - 700 >= 0 and td - 990 < 0 and
96750000*i2 + 4500000*n + 302343750*p1 + 2418750*q - 950000*td -
15000000*z - 4024609231 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q
- 577 = 0 or 2*p1 - 7 >= 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and
n - td = 0 and n >= 0 and q - 40 <= 0 and q >= 0 and (62500*n - 6250*td
- 1250000*z - 32509373 < 0 and 62500*n - 6250*td - 1250000*z - 27134373
>= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 = 0 or
62500*n - 6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*td -
1250000*z - 25790623 >= 0 and 187312500*n - 18731250*td - 4082187500*z -
 74105780531 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and
62500*n - 6250*td - 1250000*z - 21759373 > 0) and (td - 400 >= 0 and td
- 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0 and
62500*n - 6250*td - 1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 402109359 = 0) or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0
and 1141765625*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and
31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 31937500*n
- 3193750*td - 571562500*z - 13629165033 >= 0 and q - 40 <= 0 and q >= 0
 and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z +
549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or
78125*z - 523438 > 0 and 156250*z - 5837501 < 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (2*p1 - 7 < 0
and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2 + 79843750*
p1 + 638750*q - 2500000*z - 448579359 = 0) or 400*q + 9*td - 20050 <= 0
and i2 = 0 and n - td = 0 and 6696421875*n - 711787500*td - 155123125000
*z - 2816019660178 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 92837030531
 <= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0
and q - 40 <= 0 and q >= 0 and (156250*z - 10848749 < 0 and 156250*z -
3356249 >= 0 and 108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0
and 78125*z - 741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251
 > 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >=
0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0)
or 400*q + 9*td - 20050 <= 0 and i2 = 0 and n - td = 0 and 204250000*i2
+ 8937500*n + 638281250*p1 + 5106250*q - 950000*td - 190000000*z - 7640077821
 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td -
10000000*z - 452109359 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 +
268750*q - 50000*td - 10000000*z - 402109359 >= 0 and q - 40 <= 0 and q
>= 0 and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q -
1377 <= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*
q - 1457 <= 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*
q - 1697 < 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 =
0 or 2*p1 - 7 >= 0),
q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (2*p1 -
7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400
 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2
 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 1480
*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0 or 80*i2 + 250*p1
+ 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 76880*i2 -
2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0 or 80*i2 +
250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000
*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z -
402109359 = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and
i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 20*n -
2*td - 300*z - 8331 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and n
 - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 =
 0 and 100*n - 10*td - 2500*z - 36519 = 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <=
 0 and n >= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0 and 20*n - 2*
td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 + 37
*q + 5*td + 750*z - 4647 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (20*n - 2*td - 300*z - 5963 > 0 and
 20*n - 2*td - 300*z - 8331 <= 0 or 20*n - 2*td - 300*z - 8331 > 0 and
20*n - 2*td - 300*z - 8923 <= 0 and 4420*n - 442*td + 81700*z - 3170191
= 0 or 20*n - 2*td - 300*z - 8923 > 0 and 20*n - 2*td - 300*z - 10699 <
0 and 31937500*n - 3193750*td - 571562500*z - 13629165033 = 0) or q >= 0
 and q - 40 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z -
4647 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z + 353 >= 0
and 112480*i2 - 3575*n + 351500*p1 + 2812*q + 380*td + 57000*z - 353172
>= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*
q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*
td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z
 + 1015 = 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0
and 80*i2 + 250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0
and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 + 442*q +
20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*
p1 + 2*q - 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z
 - 448579359 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z -
8331 >= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 715*n - 76*td - 11400
*z - 316578 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 +
 250*p1 + 2*q - 1377 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td
- 300*z - 8923 >= 0 and 20*n - 2*td - 300*z - 10923 <= 0 and 715*n - 76*
td - 11400*z - 339074 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0
and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z - 1012 = 0 and (td - 400
>= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and
td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0
and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0
and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2 - 2000*
n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0) and (td - 400 >=
 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td
- 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (100*n -
10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0 and 4420*
n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z - 6535 > 0 and
 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500*z - 36519 >
 0 and 100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n - 18731250*td
- 4082187500*z - 74105780531 = 0) or q >= 0 and q - 40 <= 0 and 76880*i2
 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and
76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0
and 1460720*i2 - 35750*n + 4564750*p1 + 36518*q + 3800*td + 950000*z -
12726143 >= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0 and 80*i2 +
250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700
 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2
*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q -
1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 +
442*q + 20000*z - 483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*
i2 + 250*p1 + 2*q - 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*
i2 + 250*p1 + 2*q - 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q +
2500000*z - 569558609 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td -
500*z - 6535 >= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 715*n - 76*td
- 19000*z - 248330 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and
80*i2 + 250*p1 + 2*q - 1377 = 0 and 50*z - 449 = 0 and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q
 - 40 <= 0 and 4420*n - 442*td + 81700*z - 3170191 >= 0 and 4420*n - 442
*td + 81700*z - 3612191 <= 0 and 158015*n - 16796*td + 3104600*z -
120467258 <= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0 and 50*z -
891 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320
= 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td
= 0 and z = 0) and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 <
 0 and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 > 0 and
108437500*z - 865858649 = 0) or q >= 0 and q - 40 <= 0 and 100*n - 10*td
 - 2500*z - 36519 >= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 3575*
n - 380*td - 95000*z - 1387722 <= 0 and n - td = 0 and i2 = 0 and 2*p1 -
 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (td - 400 >= 0 and td -
700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0
and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <=
 0 and n >= 0 and n - td = 0 and i2 = 0 and (2*p1 - 7 < 0 and 62500*n -
6250*td - 1250000*z - 40571873 = 0 or 2*p1 - 7 >= 0 and 10750000*i2 +
500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 =
0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
 - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
 = 0) and (62500*n - 6250*td - 1250000*z - 32509373 < 0 and 62500*n -
6250*td - 1250000*z - 27134373 >= 0 and 31937500*n - 3193750*td -
571562500*z - 13629165033 = 0 or 62500*n - 6250*td - 1250000*z -
27134373 < 0 and 62500*n - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 = 0 or 62500*n -
6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*td - 1250000*z -
21759373 > 0) or q >= 0 and q - 40 <= 0 and 10750000*i2 + 500000*n +
33593750*p1 + 268750*q - 50000*td - 10000000*z - 402109359 >= 0 and
10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td - 10000000*z
- 452109359 <= 0 and 204250000*i2 + 8937500*n + 638281250*p1 + 5106250*q
 - 950000*td - 190000000*z - 7640077821 <= 0 and n - td = 0 and i2 = 0
and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377
<= 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 =
0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <=
0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0)
or q >= 0 and q - 40 <= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 >= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 1141765625
*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and n - td = 0 and
 i2 = 0 and (2*p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0
and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (156250*z + 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z +
549999 > 0 and 78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or
78125*z - 523438 > 0 and 156250*z - 5837501 < 0) or q >= 0 and q - 40 <=
 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0 and 6696421875
*n - 711787500*td - 155123125000*z - 2816019660178 <= 0 and n - td = 0
and i2 = 0 and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >=
0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0)
and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td -
700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z =
0) and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and
108437500*z - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z -
741562 >= 0 or 78125*z - 741562 < 0 and 156250*z + 4136251 > 0),
q >= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q +
 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td -
20050 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1
- 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2
*q - 1377 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647
 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457
<= 0 and 76880*i2 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z -
669797 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td
 - 10000000*z - 402109359 = 0) or q >= 0 and q - 40 <= 0 and n >= 0 and
n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377
= 0 and 20*n - 2*td - 300*z - 8331 = 0 and (400*q + 9*td - 20050 <= 0
and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (td -
400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q
>= 0 and q - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and 100*n - 10*td - 2500*z -
36519 = 0 and (400*q + 9*td - 20050 <= 0 and q = 0 or 400*q + 9*td -
20050 > 0 and 9*td - 20050 = 0) and (td - 400 >= 0 and td - 700 < 0 and
3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300
*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*n -
380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (
2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <=
0 and 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0
or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0
and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td -
10000000*z - 402109359 = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >=
 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7
>= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 20*n - 50*q - 2*td - 300*z
- 8331 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
 0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >= 0 and n - td
= 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and 2*p1 - 7 >= 0 and 80*i2
 + 250*p1 + 2*q - 1457 = 0 and 100*n - 250*q - 10*td - 2500*z - 36519 =
0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td
- 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z
= 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0
and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9
*td - 20050 >= 0 and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or
 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td
 + 12000*z + 25898 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q +
175*td + 100000*z - 838344 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80
*i2 + 250*p1 + 2*q - 1697 < 0 and 10750000*i2 + 500000*n + 33593750*p1 +
 268750*q - 21875*td - 10000000*z - 464765609 = 0) or q >= 0 and q - 40
<= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and 2*p1 -
 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1377 = 0 and 160*n - 7*td - 2400*z -
86698 = 0 and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
0 and z = 0) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td -
450 >= 0 and 180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and
400*q + 9*td - 20050 >= 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*p1 + 2*q -
1457 = 0 and 800*n - 35*td - 20000*z - 392402 = 0 and (td - 400 >= 0 and
 td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990
< 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q -
40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050
<= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (2*
p1 - 7 < 0 and 20*n - 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*
i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z - 4647 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (20*n - 2*
td - 300*z - 5963 > 0 and 20*n - 2*td - 300*z - 8331 <= 0 or 20*n - 2*td
 - 300*z - 8331 > 0 and 20*n - 2*td - 300*z - 8923 <= 0 and 4420*n - 442
*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 300*z - 8923 > 0 and 20*n -
 2*td - 300*z - 10699 < 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 = 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q >= 0 and n - td = 0 and
i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1 - 7 < 0 and 20*n - 50*q -
 2*td - 300*z - 2411 = 0 or 2*p1 - 7 >= 0 and 1480*i2 - 50*n + 4625*p1 +
 162*q + 5*td + 750*z - 4647 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (20*n - 50*q - 2*td - 300*z -
 5963 > 0 and 20*n - 50*q - 2*td - 300*z - 8331 <= 0 or 20*n - 50*q - 2*
td - 300*z - 8331 > 0 and 20*n - 50*q - 2*td - 300*z - 8923 <= 0 and
4420*n - 11050*q - 442*td + 81700*z - 3170191 = 0 or 20*n - 50*q - 2*td
- 300*z - 8923 > 0 and 20*n - 50*q - 2*td - 300*z - 10699 < 0 and
31937500*n - 79843750*q - 3193750*td - 571562500*z - 13629165033 = 0) or
 q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*
n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td -
20050 >= 0 and (2*p1 - 7 < 0 and 160*n - 7*td - 2400*z - 39338 = 0 or 2*
p1 - 7 >= 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*z +
25898 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 =
 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td =
 0 and z = 0) and (160*n - 7*td - 2400*z - 67754 > 0 and 160*n - 7*td -
2400*z - 86698 <= 0 or 160*n - 7*td - 2400*z - 86698 > 0 and 160*n - 7*
td - 2400*z - 91434 <= 0 and 35360*n - 1547*td + 653600*z - 29792578 = 0
 or 160*n - 7*td - 2400*z - 91434 > 0 and 160*n - 7*td - 2400*z - 105642
 < 0 and 255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) or
 q >= 0 and q - 40 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750
*z - 4647 <= 0 and 1480*i2 - 50*n + 4625*p1 + 37*q + 5*td + 750*z + 353
>= 0 and 112480*i2 - 3575*n + 351500*p1 + 2812*q + 380*td + 57000*z -
353172 >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and
 1480*i2 - 50*n + 4625*p1 + 162*q + 5*td + 750*z - 4647 = 0 or 400*q + 9
*td - 20050 > 0 and 23680*i2 - 800*n + 74000*p1 + 592*q + 35*td + 12000*
z + 25898 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2
*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 +
250*p1 + 2*q - 1377 <= 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 +
250*p1 + 2*q - 1457 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z -
448579359 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z - 8331
>= 0 and 20*n - 2*td - 300*z - 10331 <= 0 and 715*n - 76*td - 11400*z -
316578 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1377 = 0 and (400*q + 9*td - 20050 <= 0 and 20*n - 50*q - 2*
td - 300*z - 8331 = 0 or 400*q + 9*td - 20050 > 0 and 160*n - 7*td -
2400*z - 86698 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z
 - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 =
0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 300*z
 - 8923 >= 0 and 20*n - 2*td - 300*z - 10923 <= 0 and 715*n - 76*td -
11400*z - 339074 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80
*i2 + 250*p1 + 2*q - 1457 = 0 and 125*z - 1012 = 0 and (400*q + 9*td -
20050 <= 0 and 20*n - 50*q - 2*td - 300*z - 8923 = 0 or 400*q + 9*td -
20050 > 0 and 160*n - 7*td - 2400*z - 91434 = 0) and (td - 400 >= 0 and
td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 <
 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40
 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <=
0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0) and (2*p1
- 7 < 0 and 20*n - 2*td - 500*z + 1153 = 0 or 2*p1 - 7 >= 0 and 76880*i2
 - 2000*n + 240250*p1 + 1922*q + 200*td + 50000*z - 669797 = 0) and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
100*n - 10*td - 2500*z - 17299 > 0 and 20*n - 2*td - 500*z - 6535 <= 0
and 4420*n - 442*td + 81700*z - 3170191 = 0 or 20*n - 2*td - 500*z -
6535 > 0 and 100*n - 10*td - 2500*z - 36519 <= 0 or 100*n - 10*td - 2500
*z - 36519 > 0 and 100*n - 10*td - 2500*z - 48051 < 0 and 187312500*n -
18731250*td - 4082187500*z - 74105780531 = 0) or q >= 0 and q - 40 <= 0
and 9*n - 380*q >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050
<= 0 and (2*p1 - 7 < 0 and 20*n - 50*q - 2*td - 500*z + 1153 = 0 or 2*p1
 - 7 >= 0 and 76880*i2 - 2000*n + 240250*p1 + 6922*q + 200*td + 50000*z
- 669797 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z -
5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0
or td = 0 and z = 0) and (100*n - 250*q - 10*td - 2500*z - 17299 > 0 and
 20*n - 50*q - 2*td - 500*z - 6535 <= 0 and 4420*n - 11050*q - 442*td +
81700*z - 3170191 = 0 or 20*n - 50*q - 2*td - 500*z - 6535 > 0 and 100*n
 - 250*q - 10*td - 2500*z - 36519 <= 0 or 100*n - 250*q - 10*td - 2500*z
 - 36519 > 0 and 100*n - 250*q - 10*td - 2500*z - 48051 < 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
) or q >= 0 and q - 40 <= 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and
180*n + 171*td - 380950 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td
- 20050 >= 0 and (2*p1 - 7 < 0 and 160*n - 7*td - 4000*z - 10826 = 0 or
2*p1 - 7 >= 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q + 175*td +
100000*z - 838344 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td +
400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
1015 = 0 or td = 0 and z = 0) and (800*n - 35*td - 20000*z - 238642 > 0
and 160*n - 7*td - 4000*z - 72330 <= 0 and 35360*n - 1547*td + 653600*z
- 29792578 = 0 or 160*n - 7*td - 4000*z - 72330 > 0 and 800*n - 35*td -
20000*z - 392402 <= 0 or 800*n - 35*td - 20000*z - 392402 > 0 and 800*n
- 35*td - 20000*z - 484658 < 0 and 1498500000*n - 65559375*td - 32657500000
*z - 780627025498 = 0) or q >= 0 and q - 40 <= 0 and 76880*i2 - 2000*n +
 240250*p1 + 1922*q + 200*td + 50000*z - 669797 <= 0 and 76880*i2 - 2000
*n + 240250*p1 + 1922*q + 200*td + 50000*z - 469797 >= 0 and 1460720*i2
- 35750*n + 4564750*p1 + 36518*q + 3800*td + 950000*z - 12726143 >= 0
and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 76880*i2 -
2000*n + 240250*p1 + 6922*q + 200*td + 50000*z - 669797 = 0 or 400*q + 9
*td - 20050 > 0 and 153760*i2 - 4000*n + 480500*p1 + 3844*q + 175*td +
100000*z - 838344 = 0) and (2*p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577
= 0 or 2*p1 - 7 >= 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400
*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015
= 0 or td = 0 and z = 0) and (80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2
+ 250*p1 + 2*q - 1377 <= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z -
483917 = 0 or 80*i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q -
 1457 <= 0 or 80*i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q -
 1697 < 0 and 29970000*i2 + 93656250*p1 + 749250*q + 2500000*z -
569558609 = 0) or q >= 0 and q - 40 <= 0 and 20*n - 2*td - 500*z - 6535
>= 0 and 20*n - 2*td - 500*z - 8535 <= 0 and 715*n - 76*td - 19000*z -
248330 <= 0 and n - td = 0 and i2 = 0 and 2*p1 - 7 >= 0 and 80*i2 + 250*
p1 + 2*q - 1377 = 0 and 50*z - 449 = 0 and (400*q + 9*td - 20050 <= 0
and 20*n - 50*q - 2*td - 500*z - 6535 = 0 or 400*q + 9*td - 20050 > 0
and 160*n - 7*td - 4000*z - 72330 = 0) and (td - 400 >= 0 and td - 700 <
 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*
td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q - 40 <= 0 and
 4420*n - 442*td + 81700*z - 3170191 >= 0 and 4420*n - 442*td + 81700*z
- 3612191 <= 0 and 158015*n - 16796*td + 3104600*z - 120467258 <= 0 and
n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 4420*n - 11050*
q - 442*td + 81700*z - 3170191 = 0 or 400*q + 9*td - 20050 > 0 and 35360
*n - 1547*td + 653600*z - 29792578 = 0) and (2*p1 - 7 < 0 and 50*z - 891
 = 0 or 2*p1 - 7 >= 0 and 17680*i2 + 55250*p1 + 442*q + 20000*z - 483917
 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or
 td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0
and z = 0) and (250*z - 3129 < 0 and 50*z - 449 >= 0 or 50*z - 449 < 0
and 125*z - 1012 >= 0 or 125*z - 1012 < 0 and 250*z - 1361 > 0 and
108437500*z - 865858649 = 0) or q >= 0 and q - 40 <= 0 and 100*n - 10*td
 - 2500*z - 36519 >= 0 and 100*n - 10*td - 2500*z - 46519 <= 0 and 3575*
n - 380*td - 95000*z - 1387722 <= 0 and n - td = 0 and i2 = 0 and 2*p1 -
 7 >= 0 and 80*i2 + 250*p1 + 2*q - 1457 = 0 and (400*q + 9*td - 20050 <=
 0 and 100*n - 250*q - 10*td - 2500*z - 36519 = 0 or 400*q + 9*td -
20050 > 0 and 800*n - 35*td - 20000*z - 392402 = 0) and (td - 400 >= 0
and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) or q >= 0 and q
 - 40 <= 0 and n >= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td -
20050 <= 0 and q = 0 or 400*q + 9*td - 20050 > 0 and 9*td - 20050 = 0)
and (2*p1 - 7 < 0 and 62500*n - 6250*td - 1250000*z - 40571873 = 0 or 2*
p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*
td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0 and
 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
300*z + 1015 = 0 or td = 0 and z = 0) and (62500*n - 6250*td - 1250000*z
 - 32509373 < 0 and 62500*n - 6250*td - 1250000*z - 27134373 >= 0 and
31937500*n - 3193750*td - 571562500*z - 13629165033 = 0 or 62500*n -
6250*td - 1250000*z - 27134373 < 0 and 62500*n - 6250*td - 1250000*z -
25790623 >= 0 and 187312500*n - 18731250*td - 4082187500*z - 74105780531
 = 0 or 62500*n - 6250*td - 1250000*z - 25790623 < 0 and 62500*n - 6250*
td - 1250000*z - 21759373 > 0) or q >= 0 and q - 40 <= 0 and 9*n - 380*q
 >= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 <= 0 and (2*p1
- 7 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 40571873 = 0 or 2
*p1 - 7 >= 0 and 10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000
*td - 10000000*z - 402109359 = 0) and (td - 400 >= 0 and td - 700 < 0
and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td -
 300*z + 1015 = 0 or td = 0 and z = 0) and (62500*n - 156250*q - 6250*td
 - 1250000*z - 32509373 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z
 - 27134373 >= 0 and 31937500*n - 79843750*q - 3193750*td - 571562500*z
- 13629165033 = 0 or 62500*n - 156250*q - 6250*td - 1250000*z - 27134373
 < 0 and 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 >= 0 and
187312500*n - 468281250*q - 18731250*td - 4082187500*z - 74105780531 = 0
 or 62500*n - 156250*q - 6250*td - 1250000*z - 25790623 < 0 and 62500*n
- 156250*q - 6250*td - 1250000*z - 21759373 > 0) or q >= 0 and q - 40 <=
 0 and 9*td - 20050 <= 0 and td - 450 >= 0 and 180*n + 171*td - 380950
>= 0 and n - td = 0 and i2 = 0 and 400*q + 9*td - 20050 >= 0 and (2*p1 -
 7 < 0 and 500000*n - 21875*td - 10000000*z - 387231234 = 0 or 2*p1 - 7
>= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 21875*td -
10000000*z - 464765609 = 0) and (td - 400 >= 0 and td - 700 < 0 and 3*td
 + 400*z - 5320 = 0 or td - 700 >= 0 and td - 990 < 0 and 2*td - 300*z +
 1015 = 0 or td = 0 and z = 0) and (500000*n - 21875*td - 10000000*z -
322731234 < 0 and 500000*n - 21875*td - 10000000*z - 279731234 >= 0 and
255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0 or 500000*n
- 21875*td - 10000000*z - 279731234 < 0 and 500000*n - 21875*td -
10000000*z - 268981234 >= 0 and 1498500000*n - 65559375*td - 32657500000
*z - 780627025498 = 0 or 500000*n - 21875*td - 10000000*z - 268981234 <
0 and 500000*n - 21875*td - 10000000*z - 236731234 > 0) or q >= 0 and q
- 40 <= 0 and 10750000*i2 + 500000*n + 33593750*p1 + 268750*q - 50000*td
 - 10000000*z - 402109359 >= 0 and 10750000*i2 + 500000*n + 33593750*p1
+ 268750*q - 50000*td - 10000000*z - 452109359 <= 0 and 204250000*i2 +
8937500*n + 638281250*p1 + 5106250*q - 950000*td - 190000000*z - 7640077821
 <= 0 and n - td = 0 and i2 = 0 and (400*q + 9*td - 20050 <= 0 and
10750000*i2 + 500000*n + 33593750*p1 - 981250*q - 50000*td - 10000000*z
- 402109359 = 0 or 400*q + 9*td - 20050 > 0 and 10750000*i2 + 500000*n +
 33593750*p1 + 268750*q - 21875*td - 10000000*z - 464765609 = 0) and (2*
p1 - 7 < 0 and 80*i2 + 250*p1 + 2*q - 577 = 0 or 2*p1 - 7 >= 0) and (td
- 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0
 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (
80*i2 + 250*p1 + 2*q - 1057 > 0 and 80*i2 + 250*p1 + 2*q - 1377 <= 0 and
 25550000*i2 + 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0 or 80*
i2 + 250*p1 + 2*q - 1377 > 0 and 80*i2 + 250*p1 + 2*q - 1457 <= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0 or 80*
i2 + 250*p1 + 2*q - 1457 > 0 and 80*i2 + 250*p1 + 2*q - 1697 < 0) or q
>= 0 and q - 40 <= 0 and 31937500*n - 3193750*td - 571562500*z - 13629165033
 >= 0 and 31937500*n - 3193750*td - 571562500*z - 16822915033 <= 0 and 1141765625
*n - 121362500*td - 21719375000*z - 517908271254 <= 0 and n - td = 0 and
 i2 = 0 and (400*q + 9*td - 20050 <= 0 and 31937500*n - 79843750*q -
3193750*td - 571562500*z - 13629165033 = 0 or 400*q + 9*td - 20050 > 0
and 255500000*n - 11178125*td - 4572500000*z - 141050664014 = 0) and (2*
p1 - 7 < 0 and 156250*z + 16518749 = 0 or 2*p1 - 7 >= 0 and 25550000*i2
+ 79843750*p1 + 638750*q - 2500000*z - 448579359 = 0) and (td - 400 >= 0
 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700 >= 0 and td -
990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0) and (156250*z +
 6937499 > 0 and 156250*z + 549999 <= 0 or 156250*z + 549999 > 0 and
78125*z - 523438 <= 0 and 108437500*z - 865858649 = 0 or 78125*z -
523438 > 0 and 156250*z - 5837501 < 0) or q >= 0 and q - 40 <= 0 and
187312500*n - 18731250*td - 4082187500*z - 74105780531 >= 0 and
187312500*n - 18731250*td - 4082187500*z - 92837030531 <= 0 and 6696421875
*n - 711787500*td - 155123125000*z - 2816019660178 <= 0 and n - td = 0
and i2 = 0 and (400*q + 9*td - 20050 <= 0 and 187312500*n - 468281250*q
- 18731250*td - 4082187500*z - 74105780531 = 0 or 400*q + 9*td - 20050 >
 0 and 1498500000*n - 65559375*td - 32657500000*z - 780627025498 = 0)
and (2*p1 - 7 < 0 and 156250*z - 22087499 = 0 or 2*p1 - 7 >= 0 and
29970000*i2 + 93656250*p1 + 749250*q + 2500000*z - 569558609 = 0) and (
td - 400 >= 0 and td - 700 < 0 and 3*td + 400*z - 5320 = 0 or td - 700
>= 0 and td - 990 < 0 and 2*td - 300*z + 1015 = 0 or td = 0 and z = 0)
and (156250*z - 10848749 < 0 and 156250*z - 3356249 >= 0 and 108437500*z
 - 865858649 = 0 or 156250*z - 3356249 < 0 and 78125*z - 741562 >= 0 or
78125*z - 741562 < 0 and 156250*z + 4136251 > 0)}$
for each ex in testseries collect {rlatnum ex,rlatnum rlgsn ex};

%%% Testing rlitab

% Result of the rectangle problem from testing rlqe above.
rlitab rp3sol;

% The benchmarks from the rlgsn test once more
for each ex in testseries collect {rlatnum ex,rlatnum rlitab ex};

end;
