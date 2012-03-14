% Tests of limits package.

limit(sin(x)/x,x,0); % 1
limit(sin(x)^2/x,x,0); % 0
limit(sin(x)/x,x,1); % sin(1)
limit(1/x,x,0); % infinity
limit(-1/x,x,0); % - infinity
limit((sin(x)-x)/x^3,x,0); % -1/6
limit(x*sin(1/x),x,infinity); % 1
limit(sin x/x^2,x,0); % infinity
limit(x^2*sin(1/x),x,infinity); % infinity

% Simple examples from Schaum's Theory & Problems of Advanced Calculus

limit(x^2-6x+4,x,2); % -4
limit((x+3)*(2x-1)/(x^2+3x-2),x,-1); % 3/2
limit((sqrt(4+h)-2)/h,h,0); % 1/4
limit((sqrt(x)-2)/(4-x),x,4); % -1/4
limit((x^2-4)/(x-2),x,2); % 4
limit(1/(2x-5),x,-1); % -1/7
limit(sqrt(x)/(x+1),x,1); % 1/2
limit((2x+5)/(3x-2),x,infinity); % 2/3
limit((1/(x+3)-2/(3x+5))/(x-1),x,1); % 1/32
limit(sin(3x)/x,x,0); % 3
limit((1-cos(x))/x^2,x,0); % 1/2
limit((6x-sin(2x))/(2x+3*sin(4x)),x,0); % 2/7
limit((1-2*cos(x)+cos(2x))/x^2,x,0); % -1
limit((3*sin(pi*x) - sin(3*pi*x))/x^3,x,0); % 4*pi^3
limit((cos(a*x)-cos(b*x))/x^2,x,0); % (-a^2 + b^2)/2
limit((e^x-1)/x,x,0); % 1
limit((a^x-b^x)/x,x,0); % log(a) - log(b)

% Examples taken from Hyslop's Real Variable

limit(sinh(2x)^2/log(1+x^2),x,0); % 4
limit(x^2*(e^(1/x)-1)*(log(x+2)-log(x)),x,infinity); % 2
limit(x^alpha*log(x+1)^2/log(x),x,infinity);
  %% if repart alpha < 0 then 0 else infinity.
  %% fails because answer depends in essential way on parameter.

limit((2*cosh(x)-2-x^2)/log(1+x^2)^2,x,0); % 1/12
limit((x*sinh(x)-2+2*cosh(x))/(x^4+2*x^2),x,0); % 1
limit((2*sinh(x)-tanh(x))/(e^x-1),x,0); % 1
limit(x*tanh(x)/(sqrt(1-x^2)-1),x,0); % -2
limit((2*log(1+x)+x^2-2*x)/x^3,x,0); % 2/3
limit((e^(5*x)-2*x)^(1/x),x,0); % e^3
limit(log(log(x))/log(x)^2,x,infinity); % 0

% These are adapted from Lession 4 from Stoutmyer

limit((e^x-1)/x, x, 0); % 1
limit(((1-x)/log(x))**2, x, 1); % 1
limit(x/(e**x-1), x, 0); % 1

%% One sided limits
limit!+(sin(x)/sqrt(x),x,0); % 0
limit!-(sin(x)/sqrt(x),x,0); % 0


limit(x/log x,x,0); % 0
limit(log(1 + x)/log x,x,infinity); % 1
limit(log x/sqrt x,x,infinity); % 0
limit!+(sqrt x/sin x,x,0); % infinity
limit(log x,x,0); % - infinity
limit(x*log x,x,0); % 0
limit(log x/log(2x),x,0); % 1
limit(log x*log(1+x)*(1+x),x,0); % 0
limit(log x/x,x,infinity); % 0
limit(log x/sqrt x,x,infinity); % 0
limit(log x,x,infinity); % infinity
limit(log(x+1)/sin x,x,0); % 1
limit(log(1+1/x)*sin x,x,0); % 0
limit(-log(1+x)*(x+2)/sin x,x,0); % -2
limit(-log x*(3+x)/log(2x),x,0); % -3
limit(log(x+1)^2/sqrt x,x,infinity); % 0
limit(log(x + 1) - log x,x,infinity); % 0
limit(-(log x)^2/log log x,x,infinity); % - infinity
limit(log(x-1)/sin x,x,0); % infinity

limit!-(sqrt x/sin x,x,0); % infinity
limit(log x-log(2x),x,0); % - log(2)
limit(sqrt x-sqrt(x+1),x,infinity); % 0

limit(sin sin x/x,x,0); % 1
limit!-(sin x/cos x,x,pi/2); % infinity % this works!
limit!+(sin x/cos x,x,pi/2); % - infinity % so does this!
limit(sin x/cosh x,x,infinity); % 0
limit(sin x/x,x,infinity); % 0
limit(x*sin(1/x),x,0); % 0
limit(exp x/((exp x + exp(-x))/2),x,infinity); % 2
% limit(exp x/cosh x,x,infinity); % fails in this form, but if cosh is
  %defined using let, then it works.
limit((sin(x^2)/(x*sinh x)),x,0); % 1
limit(log x*sin(x^2)/(x*sinh x),x,0); % - infinity
limit(sin(x^2)/(x*sinh x*log x),x,0); % 0
limit(log x/log(x^2),x,0); % 1/2
limit(log(x^2)-log(x^2+8x),x,0); % - infinity
limit(log(x^2)-log(x^2+8x),x,infinity); % 0
limit(sqrt(x+5)-sqrt x,x,infinity); % 0
limit(2^(log x),x,0); % 0

% Additional examples
limit((sin tan x-tan sin x)/(asin atan x-atan asin x),x,0); % 1

% This one has the value infinity, but fails with de L'Hospital's rule:
limit((e+1)^(x^2)/e^x,x,infinity); % infinity  % fails

comment
The following examples were not in the previous set$

% Simon test examples:
limit(log(x-a)/((a-b)*(a-c)) + log(2(x-b))/((b-c)*(b-a))
      + log(x-c)/((c-a)*(c-b)),x,infinity); % log(1/2)/((a-b)*(b-c))

limit(1/(e^x-e^(x-1/x^2)),x,infinity); % infinity  % fails

% new capabilities: branch points at the origin, needed for definite
% integration.

limit(x+sqrt x,x,0); % 0
limit!+(sqrt x/(x+1),x,0); % 0
limit!+(x^(1/3)/(x+1),x,0); % 0
limit(log(x)^2/x^(1/3),x,0); % infinity
limit(log x/x^(1/3),x,0); % - infinity

h := (X^(1/3) + 3*X**(1/4))/(7*(SQRT(X + 9) - 3)**(1/4));
limit(h,x,0); % 3/7*6^(1/4)

% Examples from Paul S. Wang's thesis:

limit(x^log(1/x),x,infinity); % 0
limit(cos x - 1/(e^x^2 - 1),x,0); % - infinity
limit((1+a*x)^(1/x),x,infinity); % 1
limit(x^2*sqrt(4*x^4+5)-2*x^4,x,infinity); % 5/4
limit!+(1/x-1/sin x,x,0); % 0
limit(e^(x*sqrt(x^2+1))-e^(x^2),x,infinity); % 0 fails
limit((e^x+x*log x)/(log(x^4+x+1)+e^sqrt(x^3+1)),x,infinity); %0 % fails
limit!-(1/(x^3-6*x+11*x-6),x,2); % 1/12
limit((x*sqrt(x+5))/(sqrt(4*x^3+1)+x),x,infinity); % 1/2
limit!-(tan x/log cos x,x,pi/2); % - infinity

z0 := z*(z-2*pi*i)*(z-pi*i/2)/(sinh z - i);
limit(df(z0,z),z,pi*i/2); % infinity
z1 := z0*(z-pi*i/2);
limit(df(z1,z),z,pi*i/2); % -2*pi

% and the analogous problem:
z2 := z*(z-2*pi)*(z-pi/2)/(sin z - 1);
limit(df(z2,z),z,pi/2); % infinity
z3 := z2*(z-pi/2);
limit(df(z3,z),z,pi/2); % 2*pi

% A test by Wolfram Koepf.
f:=x^2/(3*(-27*x^2 - 2*x^3 + 3^(3/2)*(27*x^4 + 4*x^5)^(1/2))^(1/3));
L0:=limit(f,x,0); % L0 := 0
f1:=((f-L0)/x^(1/3))$
L1:=limit(f1,x,0); % L1 := 0
f2:=((f1-L1)/x^(1/3))$
L2:=limit(f2,x,0); % L2 := -1/2^(1/3)
f3:=((f2-L2)/x^(1/3))$
L3:=limit(f3,x,0); % L3 := 0
f4:=((f3-L3)/x^(1/3))$
L4:=limit(f4,x,0); % L4 := 0
f5:=((f4-L4)/x^(1/3))$
L5:=limit(f5,x,0); % L5 = -2^(2/3)/81
f6:=((f5-L5)/x^(1/3))$
L6:=limit(f6,x,0); % L6 := 0
f7:=((f6-L6)/x^(1/3))$
L7:=limit(f7,x,0); % L7 := 0
f8:=((f7-L7)/x^(1/3))$
L8:=limit(f8,x,0); % L8 := 7/(6561*2^(1/3))

limit(log(1+x)^2/x^(1/3),x,infinity);  % 0
limit(e^(log(1+x)^2/x^(1/3)),x,infinity); % 1

ss := (sqrt(x^(2/5) +1) - x^(1/3)-1)/x^(1/3);
limit(ss,x,0); % -1
limit(exp(ss),x,0); % 1/e
limit(log x,x,-1); % log(-1)
limit(log(ss),x,0); % log(-1)

ss := ((x^(1/2) - 1)^(1/3) + (x^(1/5) + 1)^2)/x^(1/5);
limit(ss,x,0); % 2

h := (X^(1/5) + 3*X**(1/4))^2/(7*(SQRT(X + 9) - 3 - x/6))**(1/5);
limit(h,x,0); % -6^(3/5)/7^(1/5)

end;

comment  The following examples all fail with the present limit package.
To make them all work it will be best to define a separate limit 
evaluator, either to be used separately, or to be used when the present 
evaluator has failed.$

limit((e+1)^(x^2)/e^x,x,infinity); % infinity
limit(e^x-e^(x-1/x^2),x,infinity); % infinity
limit(1/(e^x-e^(x-1/x^2)),x,infinity); % infinity
limit(e^(x*sqrt(x^2+1))-e^(x^2),x,infinity); % infinity
limit((e^x+x*log x)/(log(x^4+x+1)+e^sqrt(x^3+1)),x,infinity); % 0
limit!-(log(x)^2/x^(1/3),x,0); % infinity
limit (log(log(1+x)^2/x^(1/3)),x,infinity); % - infinity

end;
