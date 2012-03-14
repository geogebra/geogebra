% Title:  Examples of Laplace Transforms.

% Author: L. Kazasov.

% Date: 24 October 1988.

order p;

% Elementary functions with argument k*x, where x is object var.

laplace(1,x,p);
laplace(c,x,p);
laplace(sin(k*x),x,p); laplace(sin(x/a),x,p);
laplace(sin(17*x),x,p);
laplace(sinh x,x,p);
laplace(cosh(k*x),x,p);
laplace(x,x,p); laplace(x**3,x,p);
off mcd; laplace(e**(c*x) + a**x, x, s);
laplace(e**x - e**(a*x) + x**2, x, p);
laplace(one(k*t) + sin(a*t) - cos(b*t) - e**t, t, p);
laplace(sqrt(x),x,p); laplace(x**(1/2),x,p); on mcd;
laplace(x**(-1/2),x,p); laplace(x**(5/2),x,p);
laplace(-1/4*x**2*c*sqrt(x), x, p);

% Elementary functions with argument k*x - tau,
%   where k>0, tau>=0, x is object var.

laplace(cos(x-a),x,p);
laplace(one(k*x-tau),x,p);
laplace(sinh(k*x-tau),x,p); laplace(sinh(k*x),x,p);
laplace((a*x-b)**c,x,p);
% But ...
off mcd; laplace((a*x-b)**2,x,p); on mcd;
laplace(sin(2*x-3),x,p);
on lmon; laplace(sin(2*x-3),x,p); off lmon;
off mcd; laplace(cosh(t-a) - sin(3*t-5), t, p); on mcd;

% More complicated examples - multiplication of functions.
% We use here on lmon - a new switch that forces all
% trigonometrical functions which depend on object var
% to be represented as exponents.

laplace(x*e**(a*x)*cos(k*x), x, p);
laplace(x**(1/2)*e**(a*x), x, p);
laplace(-1/4*e**(a*x)*(x-k)**(-1/2), x, p);
laplace(x**(5/2)*e**(a*x), x, p);
laplace((a*x-b)**c*e**(k*x)*const/2, x, p);
off mcd; laplace(x*e**(a*x)*sin(7*x)/c*3, x, p); on mcd;
laplace(x*e**(a*x)*sin(k*x-tau), x, p);
% The next is unknown if lmon is off.
laplace(sin(k*x)*cosh(k*x), x, p);
laplace(x**(1/2)*sin(k*x), x, p);
on lmon;  % But now is OK.
laplace(x**(1/2)*sin(a*x)*cos(a*b), x, p);
laplace(sin(x)*cosh(x), x, p);
laplace(sin(k*x)*cosh(k*x), x, p);
% Off exp leads to very messy output in this case.
% off exp; laplace(sin(k*x-t)*cosh(k*x-t), x, p); on exp;
laplace(sin(k*x-t)*cosh(k*x-t), x, p);
laplace(cos(x)**2,x,p);laplace(c*cos(k*x)**2,x,p);
laplace(c*cos(2/3*x)**2, x, p);
laplace(5*sinh(x)*e**(a*x)*x**3, x, p);
off exp; laplace(sin(2*x-3)*cosh(7*x-5), x, p); on exp;
laplace(sin(a*x-b)*cosh(c*x-d), x, p);
% To solve this problem we must tell the program which one-function
% is rightmost shifted.  However, in REDUCE 3.4, this rule is still
% not sufficient.
for all x let one(x-b/a)*one(x-d/c) = one(x-b/a);
laplace(sin(a*x-b)*cosh(c*x-d), x, p);
for all x clear one(x-b/a)*one(x-d/c) ;
off lmon;

% Floating point arithmetic.
% laplace(3.5/c*sin(2.3*x-4.11)*e**(1.5*x), x, p);
on rounded;
laplace(3.5/c*sin(2.3*x-4.11)*e**(1.5*x), x, p);
laplace(x**2.156,x,p);
laplace(x**(-0.5),x,p);
off rounded; laplace(x**(-0.5),x,p); on rounded;
laplace(x*e**(2.35*x)*cos(7.42*x), x, p);
laplace(x*e**(2.35*x)*cos(7.42*x-74.2), x, p);
% Higher precision works, but uses more memory.
% precision 20; laplace(x**2.156,x,p);
% laplace(x*e**(2.35*x)*cos(7.42*x-74.2), x, p);
off rounded;

% Integral from 0 to x, where x is object var.
% Syntax is intl(<expr>,<var>,0,<obj.var>).

laplace(c1/c2*intl(2*y**2,y,0,x), x,p);
off mcd; laplace(intl(e**(2*y)*y**2+sqrt(y),y,0,x),x,p); on mcd;
laplace(-2/3*intl(1/2*y*e**(a*y)*sin(k*y),y,0,x), x, p);

% Use of delta function and derivatives.

laplace(-1/2*delta(x), x, p); laplace(delta(x-tau), x, p);
laplace(c*cos(k*x)*delta(x),x,p);
laplace(e**(a*x)*delta(x), x, p);
laplace(c*x**2*delta(x), x, p);
laplace(-1/4*x**2*delta(x-pi), x, p);
laplace(cos(2*x-3)*delta(x-pi),x,p);
laplace(e**(-b*x)*delta(x-tau), x, p);
on lmon;
laplace(cos(2*x)*delta(x),x,p);
laplace(c*x**2*delta(x), x, p);
laplace(c*x**2*delta(x-pi), x, p);
laplace(cos(a*x-b)*delta(x-pi),x,p);
laplace(e**(-b*x)*delta(x-tau), x, p);
off lmon;

laplace(2/3*df(delta x,x),x,p);
off exp; laplace(e**(a*x)*df(delta x,x,5), x, p); on exp;
laplace(df(delta(x-a),x), x, p);
laplace(e**(k*x)*df(delta(x),x), x, p);
laplace(e**(k*x)*c*df(delta(x-tau),x,2), x, p);
on lmon;laplace(e**(k*x)*sin(a*x)*df(delta(x-t),x,2),x,p);off lmon;

% But if tau is positive, Laplace transform is not defined.

laplace(e**(a*x)*delta(x+tau), x, p);
laplace(2*c*df(delta(x+tau),x), x, p);
laplace(e**(k*x)*df(delta(x+tau),x,3), x, p);

% Adding new let rules for Laplace operator. Note the syntax.

for all x let laplace(log(x),x) = -log(gam*il!&)/il!&;
laplace(-log(x)*a/4, x, p); laplace(-log(x),x,p);
laplace(a*log(x)*e**(k*x), x, p);
for all x clear laplace(log(x),x);

operator f; for all x let
    laplace(df(f(x),x),x) = il!&*laplace(f(x),x) - sub(x=0,f(x));
for all x,n such that numberp n and fixp n let
    laplace(df(f(x),x,n),x) = il!&**n*laplace(f(x),x) -
      for i:=n-1 step -1 until 0 sum
        sub(x=0, df(f(x),x,n-1-i)) * il!&**i ;
for all x let laplace(f(x),x) = f(il!&);

laplace(1/2*a*df(-2/3*f(x)*c,x), x,p);
laplace(1/2*a*df(-2/3*f(x)*c,x,4), x,p);
laplace(1/2*a*e**(k*x)*df(-2/3*f(x)*c,x,2), x,p);
clear f;

% Or if the boundary conditions are known and assume that
% f(i,0)=sub(x=0,df(f(x),x,i)) the above may be overwritten as:
operator f; for all x let
    laplace(df(f(x),x),x) = il!&*laplace(f(x),x) - f(0,0);
for all x,n such that numberp n and fixp n let
    laplace(df(f(x),x,n),x) = il!&**n*laplace(f(x),x) -
      for i:=n-1 step -1 until 0 sum il!&**i * f(n-1-i,0);
for all x let laplace(f(x),x) = f(il!&);
let f(0,0)=0, f(1,0)=1, f(2,0)=2, f(3,0)=3;
laplace(1/2*a*df(-2/3*f(x)*c,x), x,p);
laplace(1/2*a*df(-2/3*f(x)*c,x,4), x,p);
clear f(0,0), f(1,0), f(2,0), f(3,0); clear f;

% Very complicated examples.

on lmon;
laplace(sin(a*x-b)**2, x, p);
off mcd; laplace(x**3*(sin x)**4*e**(5*k*x)*c/2, x,p);
a:=(sin x)**4*e**(5*k*x)*c/2; laplace(x**3*a,x,p); clear a; on mcd;
% And so on, but is very time consuming.
% laplace(e**(k*x)*x**2*sin(a*x-b)**2, x, p);
% for all x let one(a*x-b)*one(c*x-d) = one(c*x-d);
% laplace(x*e**(-2*x)*cos(a*x-b)*sinh(c*x-d), x, p);
% for all x clear one(a*x-b)*one(c*x-d) ;
% laplace(x*e**(c*x)*sin(k*x)**3*cosh(x)**2*cos(a*x), x, p);
off lmon;

% Error messages.

laplace(sin(-x),x,p);
on lmon; laplace(sin(-a*x), x, p); off lmon;
laplace(e**(k*x**2), x, p);
laplace(sin(-a*x+b)*cos(c*x+d), x, p);
laplace(x**(-5/2),x,p);
% With int arg, can't be shifted.
laplace(intl(y*e**(a*y)*sin(k*y-tau),y,0,x), x, p);
laplace(cosh(x**2), x, p);
laplace(3*x/(x**2-5*x+6),x,p);
laplace(1/sin(x),x,p);   % But ...
laplace(x/sin(-3*a**2),x,p);
% Severe errors.
% laplace(sin x,x,cos y);
% laplace(sin x,x,y+1);
% laplace(sin(x+1),x+1,p);


Comment  Examples of Inverse Laplace transformations;

symbolic(ordl!* := nil);   % To nullify previous order declarations.

order t;

% Elementary ratio of polynomials.

invlap(1/p, p, t);
invlap(1/p**3, p, t);
invlap(1/(p-a), p, t); invlap(1/(2*p-a),p,t); invlap(1/(p/2-a),p,t);
invlap(e**(-k*p)/(p-a), p, t); invlap(b**(-k*p)/(p-a), p, t);
invlap(1/(p-a)**3, p, t);
invlap(1/(c*p-a)**3, p, t); invlap(1/(p/c-a)**3, p, t);
invlap((c*p-a)**(-1)/(c*p-a)**2, p, t);
invlap(c/((p/c-a)**2*(p-a*c)), p, t);
invlap(1/(p*(p-a)), p, t);
invlap(c/((p-a)*(p-b)), p, t);
invlap(p/((p-a)*(p-b)), p, t);
off mcd; invlap((p+d)/(p*(p-a)), p, t);
invlap((p+d)/((p-a)*(p-b)), p, t);
invlap(1/(e**(k*p)*p*(p+1)), p, t); on mcd;
off exp; invlap(c/(p*(p+a)**2), p, t); on exp;
invlap(1, p, t); invlap(c1*p/c2, p, t);
invlap(p/(p-a), p, t); invlap(c*p**2, p, t);
invlap(p**2*e**(-a*p)*c, p, t);
off mcd;invlap(e**(-a*p)*(1/p**2-p/(p-1))+c/p, p, t);on mcd;
invlap(a*p**2-2*p+1, p, x);

% P to non-integer power in denominator - i.e. gamma-function case.

invlap(1/sqrt(p), p, t); invlap(1/sqrt(p-a), p, t);
invlap(c/(p*sqrt(p)), p, t); invlap(c*sqrt(p)/p**2, p, t);
invlap((p-a)**(-3/2), p, t);
invlap(sqrt(p-a)*c/(p-a)**2, p, t);
invlap(1/((p-a)*b*sqrt(p-a)), p, t);
invlap((p/(c1-3)-a)**(-3/2), p, t);
invlap(1/((p/(c1-3)-a)*b*sqrt(p/(c1-3)-a)), p, t);
invlap((p*2-a)**(-3/2), p, t);
invlap(sqrt(2*p-a)*c/(p*2-a)**2, p, t);
invlap(c/p**(7/2), p, t); invlap(p**(-7/3), p, t);
invlap(gamma(b)/p**b,p,t); invlap(c*gamma(b)*(p-a)**(-b),p,t);
invlap(e**(-k*p)/sqrt(p-a), p, t);

% Images that give elementary object functions.
% Use of new switches lmon, lhyp.

invlap(k/(p**2+k**2), p, t);
% This is made more readable by :
on ltrig; invlap(k/(p**2+k**2), p, t);
invlap(p/(p**2+1), p, t);
invlap((p**2-a**2)/(p**2+a**2)**2, p, t);
invlap(p/(p**2+a**2)**2, p, t);
invlap((p-a)/((p-a)**2+b**2), p, t); off ltrig;
on lhyp; invlap(s/(s**2-k**2), s, t);
invlap(e**(-tau/k*p)*p/(p**2-k**2), p, t); off lhyp;
% But it is not always possible to convert expt. functions, e.g.:
on lhyp; invlap(k/((p-a)**2-k**2), p, t); off lhyp;
on ltrig; invlap(e**(-tau/k*p)*k/(p**2+k**2), p, t); off ltrig;
% In such situations use the default switches:
invlap(k/((p-a)**2-k**2), p, t); % i.e. e**(a*t)*cosh(k*t).
invlap(e**(-tau/k*p)*k/(p**2+k**2), p, t); % i.e. sin(k*t-tau).

% More complicated examples.

off exp,mcd; invlap((p+d)/(p**2*(p-a)), p, t);
invlap(e**(-tau/k*p)*c/(p*(p-a)**2), p, t);
invlap(1/((p-a)*(p-b)*(p-c)), p, t);
invlap((p**2+g*p+d)/(p*(p-a)**2), p, t); on exp,mcd;
invlap(k*c**(-b*p)/((p-a)**2+k**2), p, t);
on ltrig; invlap(c/(p**2*(p**2+a**2)), p, t);
invlap(1/(p**2-p+1), p, t); invlap(1/(p**2-p+1)**2, p, t);
invlap(2*a**2/(p*(p**2+4*a**2)), p, t);
% This is (sin(a*t))**2 and you can get this by using the let rules :
for all x let sin(2*x)=2*sin x*cos x, cos(2*x)=(cos x)**2-(sin x)**2,
(cos x)**2 =1-(sin x)**2;
invlap(2*a**2/(p*(p**2+4*a**2)), p, t);
for all x clear sin(2*x),cos(2*x),cos(x)**2;  off ltrig;
on lhyp;invlap((p**2-2*a**2)/(p*(p**2-4*a**2)),p,t);
off lhyp; % Analogously, the above is (cosh(a*t))**2.

% Floating arithmetic.

invlap(2.55/((0.5*p-2.0)*(p-3.3333)), p, t);
on rounded;
invlap(2.55/((0.5*p-2.0)*(p-3.3333)), p, t);
invlap(1.5/sqrt(p-0.5), p, t);
invlap(2.75*p**2-0.5*p+e**(-0.9*p)/p, p, t);
invlap(1/(2.0*p-3.0)**3, p, t); invlap(1/(2.0*p-3.0)**(3/2), p, t);
invlap(1/(p**2-5.0*p+6), p, t);
off rounded;

% Adding new let rules for the invlap operator. note the syntax:

for all x let invlap(log(gam*x)/x,x) = -log(lp!&);
invlap(-1/2*log(gam*p)/p, p, t);
invlap(-e**(-a*p)*log(gam*p)/(c*p), p, t);
for all x clear invlap(1/x*log(gam*x),x);

% Very complicated examples and use of factorizer.

off exp,mcd; invlap(c**(-k*p)*(p**2+g*p+d)/(p**2*(p-a)**3), p, t);
on exp,mcd;
invlap(1/(2*p**3-5*p**2+4*p-1), p, t);
on ltrig,lhyp; invlap(1/(p**4-a**4), p, t);
invlap(1/((b-3)*p**4-a**4*(2+b-5)), p, t); off ltrig,lhyp;
% The next three examples are the same:
invlap(c/(p**3/8-9*p**2/4+27/2*p-27)**2,p,t);invlap(c/(p/2-3)**6,p,t);
off exp; a:=(p/2-3)**6; on exp; invlap(c/a, p, t); clear a;
% The following two examples are the same :
invlap(c/(p**4+2*p**2+1)**2, p, t); invlap(c/((p-i)**4*(p+i)**4),p,t);
% The following three examples are the same :
invlap(e**(-k*p)/(2*p-3)**6, p, t);
invlap(e**(-k*p)/(4*p**2-12*p+9)**3, p, t);
invlap(e**(-k*p)/(8*p**3-36*p**2+54*p-27)**2, p, t);

% Error messages.

invlap(e**(a*p)/p, p, t);
invlap(c*p*sqrt(p), p, t);
invlap(sin(p), p, t);
invlap(1/(a*p**3+b*p**2+c*p+d),p,t);
invlap(1/(p**2-p*sin(p)+a**2),p,t);
on rounded; invlap(1/(p**3-1), p, t); off rounded;
% Severe errors:
%invlap(1/(p**2+1), p+1, sin(t) );
%invlap(p/(p+1)**2, sin(p), t);

end;
