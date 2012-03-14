% Tests of the rataprx package.

% Authors: Lisa Temme, Wolfram Koepf (koepf@zib.de)

% periodic decimal representations
rational2periodic(1/3);
periodic2rational(ws);
rational2periodic(-1/3);
periodic2rational(ws);
rational2periodic(1.2/3);
periodic2rational(ws);
rational2periodic(1/3.4);
periodic2rational(ws);
rational2periodic(1.2/3.4);
periodic2rational(ws);
rational2periodic(352673/3124);
periodic2rational(ws);
rational2periodic(53765/5216);
periodic2rational(ws);

% continued fractions
% of numbers
cfrac pi;
cfrac(pi,3);
cfrac(pi,20);
oldprec:=precision 20;
cfrac pi;
cfrac(pi^2);
cfrac(pi*e*sqrt(2));
precision oldprec;

% of rational functions
cfrac((x+2/3)^2/(6*x-5),x);
cfrac((x+2/3)^2/(6*x-5),x,0);
cfrac((x+2/3)^2/(6*x-5),x,1);
cfrac((x+2/3)^2/(6*x-5),x,10);
cfrac((x*8-7/2)^4/(x^5-2/3),x);
cfrac((x*8-7/2)^4/(x^5-2/3),x,2);

% of analytic functions
cfrac(e^x,x,10);
% default order is 4
cfrac(e^x,x);
cfrac(x^2/(x-1)*e^x,x);
cfrac(x^2/(x-1)*e^x,x,2);
cfrac(atan(x),x,10);
cfrac(asin(x),x,5);

% not implemented
cfrac(log(x),x,4);
cfrac(asech(x),x,5);
cfrac(sin sqrt x,x,4);
% wrong input
cfrac(1,x);
cfrac(x,x,x);
cfrac(x,x,x,5);

% Pade representations
pade(sin(x),x,0,3,3);
pade(tanh(x),x,0,5,5);
pade(atan(x),x,0,5,5);
pade(1/(x*sin(x)),x,0,3,2);
pade(sin(x)/x^2,x,0,10,1);
pade(sin(x)/x^2,x,0,10,2);
pade(sin(x)/x^2,x,0,10,3);
pade(exp(x),x,0,10,10);
pade(sin(x),x,0,20,20);
% no Pade Approximation exists 
pade(exp(1/x),x,0,5,5);
% wrong order
pade(sin(x)/x^2,x,0,10,0);
% not implemented
pade(factorial(x),x,1,3,3);
% extended Pade representations
pade(asech(x),x,0,3,3);
taylor(ws-asech(x),x,0,10);
pade(sin(sqrt(x)),x,0,3,3);
taylor(ws-sin(sqrt(x)),x,0,10);

end;
