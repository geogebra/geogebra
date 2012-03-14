%
%  Testing file for REDUCE Special Functions Package
%
%             Chris Cannam, ZIB Berlin
%             October 1992 -> Feb 1993
%        (only some of the time, of course)
%
%  Corrections and comments to neun@sc.zib-berlin.de
%


on savesfs;	% just in case it's off for some reason

off bfspace;	% to provide more similarity between runs
		% with old & new bigfloats

let {sinh (~x) => (exp(x) - exp (-x))/2 };
		% this will improve some results


% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     1. Bernoulli numbers

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


off rounded;

procedure do!*one!*bern(x);
   write ("Bernoulli ", x, " is ", bernoulli x);

do!*one!*bern(1);
do!*one!*bern(2);
do!*one!*bern(3);
do!*one!*bern(13);
do!*one!*bern(14);
do!*one!*bern(300);
do!*one!*bern(-2);
do!*one!*bern(0);

for n := 2 step 2 until 100 do do!*one!*bern n;

on rounded;
precision 100;

do!*one!*bern(1);
do!*one!*bern(2);
do!*one!*bern(3);
do!*one!*bern(13);
do!*one!*bern(14);
do!*one!*bern(300);
do!*one!*bern(-2);
do!*one!*bern(0);
do!*one!*bern(38);
do!*one!*bern(400);


% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     2. Gamma function

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


on rounded;
off complex;
precision 40;

algebraic procedure wg(x);
   write ("gamma (", x, ")  ==>  ", gamma x);

algebraic procedure wp(x);
   write ("-- precision ", x, ",  from ", precision(x));

wg (1/2);
wg (3/2);

write ("sqrt(pi)/2  ==>  ", sqrt(pi)/2);

wp(10);

for x := 0 step 5 until 100 do
   << wg (1 + x/1000);
      wg (-1 - x/13);
      wp (8+floor(x/4)) >>;

wg(1/1000000003);

off rounded;

gamma(17/2);
gamma(-17/2);
gamma(4);
gamma(0);
gamma(-4);
gamma(-17/3);

p := gamma(x**2) * gamma(x-y**gamma(y)) - (1/(gamma(4*(x-y))));
y := 1/4;
p;
x := 3;
p;
y := -3/8;
p;

on rounded, complex;
precision 50;

p;

off rounded, complex;
clear y;

p;



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     3. Beta function.  Not very interesting

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


algebraic procedure do!*one!*beta(x,y);
   write ("Beta (", x, ",", y, ") = ", beta(x,y));

do!*one!*beta(0,1);
do!*one!*beta(2,-3);
do!*one!*beta(3,2);
do!*one!*beta(a+b,(c+d)**(b-a));
do!*one!*beta(-3,4);
do!*one!*beta(-3,2);
do!*one!*beta(-3,-7.5);
do!*one!*beta((pi * 10), exp(5));

on rounded;
precision 30;

do!*one!*beta(0,1);
do!*one!*beta(2,-3);
do!*one!*beta(3,2);
do!*one!*beta(a+b,(c+d)**(b-a));
do!*one!*beta(-3,4);
do!*one!*beta(-3,2);
do!*one!*beta(-3,-7.5);
do!*one!*beta((pi * 10), exp(5));



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     4. Pochhammer notation

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


off rounded;

pochhammer(4,5);
pochhammer(-4,5);
pochhammer(4,-5);
pochhammer(-4,-5);
pochhammer(17/2,12);
pochhammer(-17/2,12);
pochhammer(1/3,14)*pochhammer(2/3,15);

q := pochhammer(1/5,11)*pochhammer(2/5,11)*pochhammer(3/5,11)*
      pochhammer(1-1/5,11)*pochhammer(1,11)*pochhammer(6/5,11)*
       pochhammer(70/50,11)*pochhammer(8/5,11)*pochhammer(9/5,11);

on complex;

pochhammer(a+b*i,c)*pochhammer(a-b*i,c);

a := 2;
b := 3;
c := 5;

pochhammer(a+b*i,c)*pochhammer(a-b*i,c);

off complex;
on rounded;

pochhammer(1/5,11)*pochhammer(2/5,11)*pochhammer(3/5,11)*
 pochhammer(1-1/5,11)*pochhammer(1,11)*pochhammer(6/5,11)*
  pochhammer(70/50,11)*pochhammer(8/5,11)*pochhammer(9/5,11);

q;

pochhammer(pi,floor (pi**8));
pochhammer(-pi,floor (pi**7));
pochhammer(1.5,floor (pi**8));



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     5. Digamma function

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


procedure do!*one!*psi(x);
   << precision (precision(0) + 4)$
      write("Psi of ", x, " is ", psi(x) ) >> ;

clear x, y;

z := x * ((x+y)**2 + (x**y));

off rounded;

do!*one!*psi(3);
do!*one!*psi(pi);
do!*one!*psi(1.005);
do!*one!*psi(1.995);
do!*one!*psi(74);
do!*one!*psi(-1/2);
do!*one!*psi(-3);
do!*one!*psi(z);

on rounded;
precision 100;

do!*one!*psi(3);
do!*one!*psi(pi);
do!*one!*psi(1.005);
do!*one!*psi(1.995);
do!*one!*psi(74);
do!*one!*psi(-1/2);
do!*one!*psi(-3);
do!*one!*psi(z);

precision 15;

x := 8/3;
y := 7/1000;
do!*one!*psi(z);

off rounded;

clear x, y;

df(psi(z), x);
df(df(psi(z), y),x);
int(psi(z), z);

on rounded;

for k := 1 step 0.1 until 2 do do!*one!*psi(k);

off rounded;

% PSI_SIMP.TST  F.J.Wright, 2 July 1993

on evallhseqp;
factor psi;  on rat, intstr, div;  % for neater output
% Do not try using "off mcd"!

psi(x+m) - psi(x+m-1) = 1/(x+m-1);
psi(x+2) - psi(x+1) + 2*psi(x) = 1/(x+1) + 2*psi(x);
psi(x+2) + 3*psi(x) = 4*psi(x) + 1/x + 1/(x+1);

psi(x + 1) = psi(x) + 1/x;
psi(x + 3/2) = psi(x + 1/2) + 1/(x + 1/2);
psi(x - 1/2) = psi(x + 1/2) - 1/(x - 1/2);
psi((x + 3a)/a);  psi(x/y + 3);

off rat, intstr, div;  on rational;

psi(x+m) - psi(x+m-1) = 1/(x+m-1);
psi(x+2) - psi(x+1) + 2*psi(x) = 1/(x+1) + 2*psi(x);
psi(x+2) + 3*psi(x) = 4*psi(x) + 1/x + 1/(x+1);

psi(x + 1) = psi(x) + 1/x;
psi(x + 3/2) = psi(x + 1/2) + 1/(x + 1/2);
psi(x - 1/2) = psi(x + 1/2) - 1/(x - 1/2);
psi((x + 3a)/a);  psi(x/y + 3);

off rational;

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     6. Polygamma functions

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


procedure do!*one!*pg(n,x);
   write ("Polygamma (", n, ") of ", x, " is ", polygamma(n,x));

off rounded;

do!*one!*pg(1,1/2);
do!*one!*pg(1,1);
do!*one!*pg(1,3/2);
do!*one!*pg(1,1.005);
do!*one!*pg(1,1.995);
do!*one!*pg(1,1e-10);
do!*one!*pg(2,1.45);
do!*one!*pg(3,1.99);
do!*one!*pg(4,-8.2);
do!*one!*pg(5,0);
do!*one!*pg(6,-5);
do!*one!*pg(7,200);

on rounded;
precision 100;

do!*one!*pg(1,1/2);
do!*one!*pg(1,1);
do!*one!*pg(1,3/2);
do!*one!*pg(1,1.005);
do!*one!*pg(1,1.995);
do!*one!*pg(1,1e-10);
do!*one!*pg(2,1.45);
do!*one!*pg(3,1.99);
do!*one!*pg(4,-8.2);
do!*one!*pg(5,0);
do!*one!*pg(6,-5);
do!*one!*pg(7,200);

off rounded;
clear x;


% Polygamma differentiation has already
% been tried a bit in the psi section

df(int(int(int(polygamma(3,x),x),x),x),x);

clear w, y, z;



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     7. Zeta function

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


procedure do!*one!*zeta(n);
   write ("Zeta of ", n, " is ", zeta n);

off rounded;
clear x, y, z;

z := x * ((x+y)**5 + (x**y));

do!*one!*zeta(0);

for k := 4 step 2 until 35 do 
   do!*one!*zeta(k);

do!*one!*zeta(-17/3);
do!*one!*zeta(190);
do!*one!*zeta(300);
do!*one!*zeta(0);
do!*one!*zeta(-44);

on rounded;
clear x, y;

for k := 3 step 3 until 36 do <<
   precision (31+k*3);
   do!*one!*zeta(k) >>;

precision 20;

do!*one!*zeta(-17/3);
do!*one!*zeta(z);

y := 3;
x := pi;

do!*one!*zeta(z);
do!*one!*zeta(190);
do!*one!*zeta(300);
do!*one!*zeta(0);
do!*one!*zeta(-44);

off rounded;



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     8. Kummer functions

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


off rounded;

t!*kummer!*a := { 1, 2.4, -1397/10  }$
t!*kummer!*b := { 0, 1, pi, -pi, 26 }$

for each a in t!*kummer!*a do
   for each b in t!*kummer!*a do
      for each z in t!*kummer!*a do
      	 << write "KummerM(", a, ",", b, ",", z, ") = ",
      	       kummerm(a,b,z);
      	    write "KummerU(", a, ",", b, ",", z, ") = ",
      	       kummeru(a,b,z) >>;

on rounded;
precision 30;
t!*k!*c := 7;

%  To test each and every possible combination of
%  three arguments from t!*kummer!*b would take too
%  long, but we want the possibility of trying most
%  special cases.  Compromise: test every seventh
%  possibility.

for each a in t!*kummer!*b do
   for each b in t!*kummer!*b do
      for each z in t!*kummer!*b do
      	 << if t!*k!*c = 7
      	    then << write "KummerM(", a, ",", b, ",", z, ") = ",
      	       	       kummerm(a,b,z);
      	       	    write "KummerU(", a, ",", b, ",", z, ") = ",
      	       	       kummeru(a,b,z);
      	       	    t!*k!*c := 0 >>;
      	    t!*k!*c := t!*k!*c + 1 >>;

off rounded;
clear x, y, z, t!*k!*c;

df(df(kummerM(a,b,z),z),z);
df(kummerU(a,b,z),z);

z := ((x^2 + y)^5) + (x^(x+y));

df(df(kummerM(a,b,z),y),x);
df(kummerU(a,b,z),x);



% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

%     9. Bessel functions

% =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


%  Lengthy test of the Bessel functions.  This isn't even
%  remotely exhaustive of the special cases -- though a
%  real person with lots of time could no doubt come up
%  with a better lot of tests than this automated rubbish.
%  Again, compromise by only actually doing one in five or
%  nine.  If you want a really thorough test, you can
%  easily change this to get it; but it'll take hours to
%  run.

clear p, q;

hankel1(p,q);
r := df(ws,q);

on complex;

r;
p:=3/8;
r;
q := pi;
r;

on rounded;

r;

off complex, rounded;

df(df(besselj(pp,qq)+rr * hankel1(pp*2,qq) *
      bessely(pp-qq,qq),qq),qq);

% Possible values for real args
t!*bes!*vr := { 1, pi, -pi, 26 }$

% Possible values for real and imaginary parts of complex args
t!*bes!*vc := { 0, 3, -41/2 }$

array s!*bes(4)$

s!*bes(1) := "BesselJ"$
s!*bes(2) := "BesselY"$
s!*bes(3) := "BesselI"$
s!*bes(4) := "BesselK"$

pre := 16;
precision pre;
preord := 10**pre;
t!*b!*c := 3;

algebraic procedure do!*one!*bessel(s,n,z);
   (if s = 1 then besselj(n,z)
      else if s = 2 then bessely(n,z)
      else if s = 3 then besseli(n,z)
      else besselk(n,z));

algebraic procedure pr!*bessel(s,n,z,k);
   << if t!*b!*c = k
      then
      	 << on rounded;
      	    bes1 := do!*one!*bessel(s,n,z);
      	    precision(pre+5);
      	    bes2 := do!*one!*bessel(s,n,z);
      	    if bes1 neq 0
      	       then disc := floor abs(100*(bes2-bes1)*preord/bes1)
      	       else disc := 0;
      	    precision pre;
      	    write s!*bes(s), "(", n, ",", z, ") = ", bes1;
      	    if not numberp disc then
      	       << precom := !*complex;
      	       	  on complex;
      	       	  disc := disc;
      	       	  if not precom then off complex >>;
      	    if disc neq 0
      	       then write "   (discrepancy ", disc, "% of one s.f.)";
      	    if numberp disc and disc > 200 then
      	       << write "***** WARNING  Significant Inaccuracy.";
      	       	  write "      Lower  precision result:";
      	       	  write "      ", bes1;
      	       	  write "      Higher precision result:";
      	       	  precision(pre+5); write "      ", bes2; precision pre >>;
      	    off rounded;
      	    t!*b!*c := 0 >>;
      t!*b!*c := t!*b!*c + 1 >>;

%  About to begin Bessel test.  We have a list of possible
%  values, and we test every Bessel, with every value on the
%  list as both order and argument.  Every Bessel is computed
%  twice, to different precisions (differing by 3), and any
%  discrepancy is reported.  The value reported is the diff-
%  erence between the two computed values, expressed as a
%  percentage of the unit of the least significant displayed
%  digit.  A discrepancy between 100% and 200% means that the
%  last digit of the displayed value was found to differ at
%  higher precision; values greater than 200% are cause for
%  concern.  An ideal discrepancy would be between about 1%
%  and 20%; if the value is found to be zero, no discrepancy
%  is reported.

off msg;

for s := 1:4 do
   << write(" ... Testing ", s!*bes(s), " for real domains ... ");
      for each n in t!*bes!*vr do
         for each z in t!*bes!*vr do
      	    pr!*bessel(s, n, z, 5) >>;

on complex;

for s := 1:3 do
   << write (" ... Testing  ", s!*bes(s), " for complex domains ... ");
      for each nr in t!*bes!*vc do
      	 for each ni in t!*bes!*vc do
      	    for each zr in t!*bes!*vc do
      	       for each zi in t!*bes!*vc do
      	       	  pr!*bessel(s, nr+ni*i, zr+zi*i, 9) >>;

off complex;

on msg;

write (" ...");
write ("Bessel test complete.");

end;


