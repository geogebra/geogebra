% Test of algebraic number package.

defpoly sqrt2**2-2;

1/(sqrt2+1);

(x**2+2*sqrt2*x+2)/(x+sqrt2);

on gcd;

(x**3+(sqrt2-2)*x**2-(2*sqrt2+3)*x-3*sqrt2)/(x**2-2);

off gcd;

sqrt(x**2-2*sqrt2*x*y+2*y**2);

off arnum;  %to start a new algebraic extension.


defpoly cbrt5**3-5;

on rationalize;

1/(x-cbrt5);

off rationalize;

off arnum;  %to start a new algebraic extension.


%The following examples are taken from P.S. Wang Math. Comp. 30,
%    134,(1976),p.324.

on factor;

defpoly i**2+1=0;

w0 := x**2+1;

w1 := x**4-1;

w2 := x**4+(i+2)*x**3+(2*i+5)*x**2+(2*i+6)*x+6;

w3 := (2*i+3)*x**4+(3*i-2)*x**3-2*(i+1)*x**2+i*x-1;

off arnum;


defpoly a**2-5;

w4 := x**2+x-1;

off arnum;


defpoly a**2+a+2;

w5 := x**4+3*x**2+4;

off arnum;


defpoly a**3+2=0;

w6:=64*x**6-4;

off arnum;


defpoly a**4+a**3+a**2+a+1=0;

w7:=16*x**4+8*x**3+4*x**2+2*x+1;

off arnum, factor;


defpoly sqrt5**2-5,cbrt3**3-3;

cbrt3**3;

sqrt5**2;

cbrt3;

sqrt5;

sqrt(x**2+2*(sqrt5-cbrt3)*x+5-2*sqrt5*cbrt3+cbrt3**2);

on rationalize;

1/(x+sqrt5-cbrt3);

off arnum, rationalize;


split_field(x**3+2);

for each j in ws product (x-j);

split_field(x**3+4*x**2+x-1);

for each j in ws product (x-j);

split_field(x**3-3*x+7);

for each j in ws product (x-j);

split_field(x**3+4*x**2+x-1);

for each j in ws product (x-j);

split_field(x**3-x**2-x-1);

for each j in ws product (x-j);

% A longer example.

off arnum;

defpoly a**6+3*a**5+6*a**4+a**3-3*a**2+12*a+16;

factorize(x**3-3);

end;
