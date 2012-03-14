% Test cases for definite integration.

int(x/(x+2),x,2,6);

int(sin x,x,0,pi/2);

int(log(x),x,1,5);

int((1+x**2/p**2)**(1/2),x,0,p);

int(x**9+y+y**x+x,x,0,2);


% Collected by Kerry Gaskell, ZIB, 1993/94.

int(x^2*log(1+x),x,0,infinity);

int(x*e^(-1/2x),x,0,infinity);

int(x/4*e^(-1/2x),x,0,infinity);

int(sqrt(2)*x^(1/2)*e^(-1/2x),x,0,infinity);

int(x^(3/2)*e^(-x),x,0,infinity);

int(sqrt(pi)*x^(3/2)*e^(-x),x,0,infinity);

int(x*log(1+1/x),x,0,infinity);

int(si(1/x),x,0,infinity);

int(cos(1/x),x,0,infinity);

int(sin(x^2),x,0,infinity);

int(sin(x^(3/2)),x,0,infinity);

int(besselj(2,x),x,0,infinity);

int(besselj(2,y^(5/4)),y,0,infinity);

int(x^(-1)*besselj(2,sqrt(x)),x,0,infinity);

int(bessely(2,x),x,0,infinity);

int(x*besseli(2,x),x,0,infinity);

int(besselk(0,x),x,0,infinity);

int(x^2*besselk(2,x),x,0,infinity);

int(sinh(x),x,0,infinity);

int(cosh(2*x),x,0,infinity);

int(-3*ei(-x),x,0,infinity);

int(x*shi(x),x,0,infinity);

int(x*fresnel_c(x),x,0,infinity);

int(x^3*e^(-2*x),x,0,infinity);

int(x^(-1)*sin(x/3),x,0,infinity);

int(x^(-1/2)*sin(x),x,0,infinity);

int(2*x^(-1/2)*cos(x),x,0,infinity);

int(sin x + cos x,x,0,infinity);

int(ei(-x) + sin(x^2),x,0,infinity);

int(x^(-1)*(sin (-2*x) + sin(x^2)),x,0,infinity);

int(x^(-1)*(cos(x/2) - cos(x/3)),x,0,infinity);

int(x^(-1)*(cos x - cos(2*x)),x,0,infinity);

int(x^(-1)*(cos(x) - cos(x)),x,0,infinity);

int(2,x,0,infinity);

int(cos(x)*si(x),x,0,infinity);

int(2*cos(x)*e^(-x),x,0,infinity);

int(x/2*cos(x)*e^(-x),x,0,infinity);

int(x^2/4*cos(x)*e^(-2*x),x,0,infinity);

int(1/(2*x)*sin(x)*e^(-3*x),x,0,infinity);

int(3/x^2*sin(x)*e^(-x),x,0,infinity);

int(cos(sqrt(x))*e^(-x),x,0,infinity);

int(e^(-x)*besselj(2,x),x,0,infinity);

int(cos(x^2)*e^(-x),x,0,infinity);

int(erf(x)*e^(-x),x,0,infinity);

int(besseli(2,x)*e^(-x),x,0,infinity);

int(e^(-x^2)*cos(x),x,0,infinity);

int(x^(-1)*sin(x)*cos(x),x,0,infinity);

int(x^(-1)*sin(x)*cos(2*x),x,0,infinity);

int(x^(-1)*sin(x)*cos(x/2),x,0,infinity);

int(e^x,x,0,infinity);

int(e^(-x^2 - x),x,0,infinity);

int(e^(-(x+x^2+1)),x,0,infinity);

int(e^(-(x+1/x)^2),x,0,infinity);

int(e^(-(x+2))*sin(x),x,0,infinity);

int(-3*x*e^(-1/2x),x,0,infinity);

int(x*e^(-1/2*x^2),x,0,infinity);

int(x^2*besselj(2,x),x,0,infinity);

int(x*besselk(1,x),x,0,infinity);

int(-3*ei(-x),x,0,infinity);

int(x^3*e^(-2*x^2),x,0,infinity);

int(sqrt(2)/2*x^(-3/2)*sin x,x,0,infinity);

int(x^(-1)*(sin(-2*x) + sin(x^2)),x,0,infinity);

int(x^(-1)*(cos(3*x) - cos(x/2)),x,0,infinity);

int(x^(-1)*(sin x - sin(2*x)),x,0,infinity);

int(1/x*sin(x)*e^(-3*x),x,0,infinity);

int(sin(x)*e^(-x),x,0,infinity);

int(x^(-1)*sin(x)*cos(x),x,0,infinity);

int(e^(1-x)*e^(2-x^2),x,0,infinity);

int(e^(-1/2x),x,0,y);

int(si(x),x,0,y);

int(besselj(2,x^(1/4)),x,0,y);

int(x*besseli(2,x),x,0,y);

int(x^(3/2)*e^(-x),x,0,y);

int(sinh(x),x,0,y);

int(cosh(2*x),x,0,y);

int(x*shi(x),x,0,y);

int(x^2*e^(-x^2),x,0,y);

int(x^(-1)/2*sin(x),x,0,y);

int(sin x + cos x,x,0,y);

int(sin x + sin(-2*x),x,0,y);

int(sin(n*x),x,0,y);

int(heaviside(x-1),x,0,y);


% Tests of transformations defined in defint package.

laplace_transform(1,x);

laplace_transform(x,x); 

laplace_transform(x^a/factorial(a),x);

laplace_transform(x,e^(-a*x),x);

laplace_transform(x^k,e^(-a*x),x);	

laplace_transform(cosh(a*x),x);	

laplace_transform(1/(2*a^3),sinh(a*x)-sin(a*x),x);

laplace_transform(1/(a^2),1-cos(a*x),x);	

laplace_transform(1/(b^2-a^2),cos(a*x)-cos(b*x),x);

laplace_transform(besselj(0,2*sqrt(k*x)),x);	

laplace_transform(Heaviside(x-1),x);

laplace_transform(1/x,sin(k*x),x);

laplace_transform(1/(k*sqrt(pi)),e^(-x^2/(4*k^2)),x);	

laplace_transform(1/k,e^(-k^2/(4*x)),x);

laplace_transform(2/(sqrt(pi*x)),besselk(0,2*sqrt(2*k*x)),x);	

hankel_transform(x,x);	

Y_transform(x,x);	

K_transform(x,x);	

struveh_transform(x,x);

fourier_sin(e^(-x),x);	

fourier_sin(sqrt(x),e^(-1/2*x),x);	

fourier_sin(1/x,e^(-a*x),x);	

fourier_sin(x^k,x);	

fourier_sin(1/(b-a),(e^(-a*x)-e^(-b*x)),x);

fourier_sin(besselj(0,a*x),x);

fourier_sin(1/sqrt(pi*x),cos(2*sqrt(k*x)),x);

fourier_sin(1/(k*sqrt(pi)),e^(-x^2/(4*k^2)),x);

fourier_cos(e^(-1/2x),x);	

fourier_cos(x,e^(-x),x);	

fourier_cos(x,e^(-1/2*x^2),x);	

fourier_cos(2*x^2,e^(-1/2x),x);	

fourier_cos(x,e^(-a*x),x);	

fourier_cos(x^n,e^(-a*x),x);	

fourier_cos(1/x,sin(k*x),x);

fourier_cos(1/sqrt(pi*x),cos(2*sqrt(k*x)),x);	

fourier_cos(1/(k*sqrt(pi)),e^(-x^2/(4*k^2)),x);

fourier_cos(1/(pi*x),sin(2*k*sqrt(x)),x);	

fourier_cos(1/(sqrt(pi*x)),e^(-2*k*sqrt(x)),x);

laplace_transform(x^n/factorial(n)*e^(-a*x),x);

laplace_transform(1/(2*a^2)*(cosh(a*x)-cos(a*x)),x);

laplace_transform(k*a^k/x*besselj(k,a*x),x);	

fourier_sin(1/x*e^(-3*x),x);	

fourier_sin(1/(pi*x)*sin(2*k*sqrt(x)),x);

fourier_cos(x^n*e^(-a*x),x);	

fourier_cos(1/(k*sqrt(pi))*e^(-x^2/(4*k^2)),x);

end;

