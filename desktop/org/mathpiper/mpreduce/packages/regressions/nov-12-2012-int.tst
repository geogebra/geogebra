%
% A couple of test cases that returned either a wrong result, or never came back
%

int(e^x/sqrt(x),x);

int(e^(1/x^3)/x^7,x);

int((f^(b*x))^p *sec(c + d*x)^3,x);

int((f^(b*x))^p /cos(c + d*x)^3,x);

int(e^(e^(3*x)),x);

int(e^(e^x^3)*(x^2-x+2),x);

int(e^(e^x^3)*(x^2-x+2)/(x^3+2*x^2-3*x+4),x);

z:=e^x;

int(e^z*(z^2-z+2)/(z^3+2*z^2-3*z+4),x);

int(e^(8*z^6)*(z^2-z+2)/(z^3+2*z^2-3*z+4),x);

z^2+1;
ws^6;
u:=sqrt(2)*z/(z^2+1);
e^(u^6)/u^2;    


% The following two integrals must give identical results.

I1 := int(cosh(x)^2,x);
I2 := int(1/sech(x)^2,x);

I1-I2;

% as must these two

I3 := int(sinh(x)^2,x);
I4 := int(1/csch(x)^2,x);

I3-I4;

% integrals that caused a very long run

int(e^(1/x^3)*sqrt(x-2),x);

on combinelogs;

int(e^(1/x^3)*sqrt(x-2),x);

off combinelogs;


int(e^(1/x^3)*sqrt(x)*sqrt(x-2),x);

on combinelogs;
int(e^(1/x^3)*sqrt(x)*sqrt(x-2),x);
off combinelogs;

int(e^(1/x)*sqrt(x-2)/sqrt(x),x);

% These can be expressed by ei(x^2)

int(e^(x^2)/x,x);

int(e^(x^2/4)/x,x);

% Test that free variables in int rules do not match against
%  expressions containing the integration variable

operator b;

int(2^(2*x^2),x);

int(x^(2*x^2),x);

int(2^(sqrt(x)*x^2),x);

% All the following should be returned un evaluated

int(log(x)/(b(x)-x),x);

int(e^(x^2/b(x))/x,x);

int(e^(x/b(x))/x,x);

int(sin(b(x)*x)/x,x);

int(sin(x/b(x))/x,x);

int(cos(b(x)*x)/x,x);

int(cos(x/b(x))/x,x);

int(1/log(b(x)*x),x);

int(1/log(x/b(x)),x);

int(1/log(x+b(x)),x);

int(1/log(sqrt(x)*x+b(x)),x);

int(1/log(x/b(x)+sqrt(x)),x);

end;

% Integrals from bug reports

p2:=1/(sqrt(d-x)*sqrt(c-x)*sqrt(b-x)*sqrt(a-x)*(a*b-a*x-b*x+x**2));
r1:=int(p2,x);
verif:=num (df(r1,x)-p2);

on algint;
r1:=int(p2,x);
verif:=num (df(r1,x)-p2);

off algint;

% The next one needs precise off to return the correct result
off precise;
on factor;
!2prim:=log(sqrt((a-x)*(b-x)*(x-1)*x));
kk:=E**(-!2PRIM);
write "kk=",kk;
v11:=const/(x*(a*b*x - a*b - a*x**2 + a*x - b*x**2 + b*x + x**3 -
x**2));
write "v11=",v11;
ll:=v11*kk;
write "ll=",ll;
sauvetasoeur:=int(ll,x);
verif:=df(sauvetasoeur,x)-ll;

end;
