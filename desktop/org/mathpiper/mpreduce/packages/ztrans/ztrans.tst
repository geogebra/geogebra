% test file for ztrans package
% 
operator f;
operator g;
operator h;

% Examples for Z transformation
ztrans(1,n,z);
ztrans(a,n,z);
ztrans((-1)^n,n,z);
ztrans(n,n,z);
ztrans(n^2,n,z);
ztrans(n^k,n,z);
% should be output=input
ztrans((-1)^n*n^2,n,z);
ztrans(binomial(n,m),n,z);
ztrans((-1)^n*binomial(n,m),n,z);
ztrans(binomial(n+k,m),n,z);
ztrans(a^n,n,z);
ztrans(a^(n-1),n,z);
ztrans(a^(n+k),n,z);
ztrans((-1)^n*a^n,n,z);
ztrans(1-a^n,n,z);
ztrans(n*a^n,n,z);
ztrans(n^3*a^n,n,z);
ztrans(binomial(n,m)*a^n,n,z);
ztrans(1/(n+1),n,z);
ztrans(1/(n+2),n,z);
ztrans((-1)^(n)/(n+1),n,z);
ztrans((-1)^(n)/(n+2),n,z);
ztrans(a^(n-1)/(n+1),n,z);
ztrans(a^(n+k)/(n+1),n,z);
ztrans(a^n/factorial(n),n,z);
ztrans((n+1)*a^n/factorial(n),n,z);
ztrans(1/factorial(n-1),n,z);
% ERROR message o.k.
ztrans((-1)^n/factorial(2*n+1),n,z);
ztrans((-1)^n/factorial(2*n),n,z);
ztrans(1/factorial(2*n+1),n,z);
ztrans(1/factorial(2*n-1),n,z);
ztrans(1/factorial(2*n+3),n,z);
ztrans(1/factorial(2*n),n,z);
ztrans(1/factorial(2*n+2),n,z);
ztrans(a^n/factorial(2*n+1),n,z);
ztrans(a^n/factorial(2*n),n,z);
ztrans(e^(a*n),n,z);
ztrans(e^(a*(n+k)),n,z);
ztrans(sinh(a*n),n,z);
ztrans(cosh(a*n),n,z);
ztrans(sinh(a*n+p),n,z);
ztrans(cosh(a*n+p),n,z);
ztrans(a^n*sinh(a*n),n,z);
ztrans(a^n*cosh(a*n),n,z);
ztrans(n*sinh(a*n),n,z);
ztrans(n*cosh(a*n),n,z);
ztrans(n^2*a^n*sinh(b*n),n,z);
ztrans(sin(b*n),n,z);
ztrans(cos(b*n),n,z);
ztrans(sin(b*n+p),n,z);
ztrans(cos(b*n+p),n,z);
ztrans(e^(a*n)*sin(b*n),n,z);
ztrans(e^(a*n)*cos(b*n),n,z);
ztrans((-1)^n*e^(a*n)*sin(b*n),n,z);
ztrans((-1)^n*e^(a*n)*cos(b*n),n,z);
ztrans(n*sin(b*n),n,z);
ztrans(n*cos(b*n),n,z);
ztrans(n^2*a^n*sin(b*n),n,z);
ztrans(cos(b*(n+1))/(n+1),n,z);
ztrans(sin(b*(n+1))/(n+1),n,z);
ztrans(cos(b*(n+2))/(n+2),n,z);
ztrans((-1)^(n)*cos(b*(n+1))/(n+1),n,z);
ztrans((-1)^(n)*sin(b*(n+1))/(n+1),n,z);
ztrans(cos(b*n)/factorial(n),n,z);
ztrans(sin(b*n)/factorial(n),n,z);
ztrans(a*f(n)+b*g(n)+c*h(n),n,z);
ztrans(sum(f(k)*g(n-k),k,0,n),n,z);
ztrans(sum(f(k),k,0,n),n,z);
ztrans(sum(f(k),k,-2,n),n,z);
ztrans(sum(f(k),k,3,n),n,z);
ztrans(sum(f(k),k,0,n+2),n,z);
ztrans(sum(f(k),k,0,n-3),n,z);
ztrans(sum(f(k),k,-2,n+3),n,z);
ztrans(sum(1/factorial(k),k,0,n),n,z);
ztrans(sum(1/factorial(k+2),k,0,n),n,z);
ztrans(n^2*sum(1/factorial(k),k,0,n),n,z);
ztrans(sum(n^2/factorial(k),k,0,n),n,z);
ztrans(sum(1/k,k,0,n),n,z);
% ERROR o.k.
ztrans(sum(1/(k+1),k,0,n),n,z);
ztrans(sum(1/(k+3),k,0,n),n,z);
ztrans(f(n+k),n,z);
% output=input
ztrans(f(n+2),n,z);
ztrans(f(n-k),n,z);
% output=input
ztrans(f(n-3),n,z);
% output=input
ztrans(a^n*f(n),n,z);
ztrans(n*f(n),n,z);
ztrans(1/a^n,n,z);
ztrans(1/a^(n+1),n,z);
ztrans(1/a^(n-1),n,z);
ztrans(2*n+n^2-3/4*n^3,n,x);
ztrans(n^2*cos(n*x),n,z);
ztrans((1+n)^2*f(n),n,z);
ztrans(n^2*f(n),n,z);
ztrans(n/factorial(n),n,z);
ztrans(n^2/factorial(n),n,z);
ztrans(a^n/factorial(n),n,z);
ztrans(1/(a^n*factorial(n)),n,z);
ztrans(sum(f(k)*g(n-k),k,0,n),n,z);
ztrans(sum(f(k),k,0,n-1),n,z);
ztrans(sum(f(k),k,0,n),n,z);
ztrans(sum(1/factorial(k),k,0,n),n,z);
ztrans(sum(k/factorial(k),k,0,n),n,z);
ztrans(sum(a^k*k^2/factorial(k),k,0,n),n,z);
ztrans(a^n*f(n),n,z);
ztrans(binomial(n,k),n,z);
ztrans(1/(n+1),n,z);
ztrans(n/factorial(2*n+1),n,z);
ztrans(a^n*sin(n*x+y),n,z);
ztrans(n^3*sin(n*x+y),n,z);
ztrans((n+1)/factorial(n),n,z);
ztrans(factorial(n)/(factorial(k)*factorial(n-k)),n,z);

% Examples for inverse Z transformation
invztrans(z/(z-1),z,n);
invztrans(z/(z+1),z,n);
invztrans(z/(z-1)^2,z,n);
invztrans(z*(z+1)/(z-1)^3,z,n);
invztrans(z/(z-1)^m,z,n);
% invztrans(z/(z-1)^(m+1),z,n);
% not yet supported
invztrans(z/(z-1)^4,z,n);
invztrans((-1)^m*z/(z+1)^m,z,n);
% not yet supported
invztrans(z/(z+1)^4,z,n);
% invztrans(z^(k+1)/(z-1)^(m+1),z,n);
% not yet supported
invztrans(z^4/(z-1)^m,z,n);
% invztrans(z^4/(z-1)^(m+1),z,n);
% not yet supported
% invztrans(z^4/(z-1)^m,z,n);
% not yet supported
% invztrans(z^(k+1)/(z-1)^5,z,n);
% not yet supported
invztrans(z^3/(z-a)^4,z,n);
invztrans(z/(z-a),z,n);
invztrans(z/(z+a),z,n);
invztrans(z*(1-a)/((z-1)*(z-a)),z,n);
invztrans(z*a/(z-a)^2,z,n);
invztrans(z*3/(z-3)^2,z,n);
% invztrans(a^m*z/(z-a)^(m+1),z,n);
% not yet supported
% invztrans(a^m*z/(z-a)^m,z,n);
% not yet supported
% invztrans(4^m*z/(z-4)^(m+1),z,n);
% not yet supported
invztrans(a^3*z/(z-a)^5,z,n);
invztrans(z*log(z/(z-1)),z,n);
invztrans(z*log(1+1/z),z,n);
invztrans(z*log(z/(z-a)),z,n);
invztrans(e^(a/z),z,n);
invztrans(e^(1/(a*z)),z,n);
invztrans((1+a/z)*e^(a/z),z,n);
invztrans(e^(a/z)*(a+z)/z,z,n);
invztrans(sqrt(z)*sin(1/sqrt(z)),z,n);
invztrans(cos(1/sqrt(z)),z,n);
invztrans(sqrt(z)*sinh(1/sqrt(z)),z,n);
invztrans(cosh(1/sqrt(z)),z,n);
invztrans(sqrt(z/a)*sinh(sqrt(a/z)),z,n);
invztrans(cosh(sqrt(a/z)),z,n);
invztrans(z/(z-e^a),z,n);
invztrans(z*sinh(a)/(z^2-2*z*cosh(a)+1),z,n);
invztrans(z*(z-cosh(a))/(z^2-2*z*cosh(a)+1),z,n);
invztrans(z*(z*sinh(p)+sinh(a-p))/(z^2-2*z*cosh(a)+1),z,n);
% trigsimp(ws);
% trigsimp(ws,combine);
invztrans(z*(z*cosh(p)-cosh(a-p))/(z^2-2*z*cosh(a)+1),z,n);
% trigsimp(ws);
% trigsimp(ws,combine);
invztrans(a*z*sinh(a)/(z^2-2*a*z*cosh(a)+a^2),z,n);
invztrans(z*(z-a*cosh(a))/(z^2-2*a*z*cosh(a)+a^2),z,n);
invztrans(z*(z^2-1)*sinh(a)/(z^2-2*z*cosh(a)+1)^2,z,n);
% trigsimp(ws);
invztrans(z*((z^2+1)*cosh(a)-2*z)/(z^2-2*z*cosh(a)+1)^2,z,n);
invztrans(z*sin(b)/(z^2-2*z*cos(b)+1),z,n);
invztrans(z*(z-cos(b))/(z^2-2*z*cos(b)+1),z,n);
invztrans(z*(z*sin(p)+sin(b-p))/(z^2-2*z*cos(b)+1),z,n);
% trigsimp(ws);
% trigsimp(ws,combine);
invztrans(z*(z*cos(p)-cos(b-p))/(z^2-2*z*cos(b)+1),z,n);
% trigsimp(ws);
% trigsimp(ws,combine);
invztrans(z*e^(a)*sin(b)/(z^2-2*z*e^a*cos(b)+e^(2*a)),z,n);
invztrans(z*(z-e^a*cos(b))/(z^2-2*z*e^a*cos(b)+e^(2*a)),z,n);
invztrans(-z*e^a*sin(b)/(z^2+2*z*e^a*cos(b)+e^(2*a)),z,n);
invztrans(z*(z+e^a*cos(b))/(z^2+2*z*e^a*cos(b)+e^(2*a)),z,n);
invztrans(z*(z^2-1)*sin(b)/(z^2-2*z*cos(b)+1)^2,z,n);
% trigsimp(ws,expon);
% trigsimp(ws,trig);
invztrans(z*((z^2+1)*cos(b)-2*z)/(z^2-2*z*cos(b)+1)^2,z,n);
% trigsimp(ws,expon);
% trigsimp(ws,trig);
invztrans(z*log(z/sqrt(z^2-2*z*cos(b)+1)),z,n);
invztrans(z*atan(sin(b)/(z-cos(b))),z,n);
invztrans(z*log(sqrt(z^2+2*z*cos(b)+1)/z),z,n);
invztrans(z*atan(sin(b)/(z+cos(b))),z,n);
invztrans(cos(sin(b)/z)*e^(cos(b)/z),z,n);
invztrans(sin(sin(b)/z)*e^(cos(b)/z),z,n);
invztrans((f+a*z+b*z^2)/(c+d*z+e*z^2),z,n);

% Example 1 in Bronstein/Semendjajew, p. 651

f(0):=0;
f(1):=0;
f(2):=9;
f(3):=-2;
f(4):=23;
equation:=ztrans(f(n+5)-2*f(n+3)+2*f(n+2)-3*f(n+1)+2*f(n),n,z);
ztransresult:=solve(equation,ztrans(f(n),n,z));
result:=invztrans(part(first(ztransresult),2),z,n);

% Example 2 in Bronstein/Semendjajew, p. 651

clear(f);
operator f;
f(0):=0;
f(1):=1;
equation:=ztrans(f(n+2)-4*f(n+1)+3*f(n)-1,n,z);
ztransresult:=solve(equation,ztrans(f(n),n,z));
result:=invztrans(part(first(ztransresult),2),z,n);

% Other example:

clear(f);
operator f;
f(0):=1;
f(1):=1;
operator tmp;
equation:=ztrans((n+1)*f(n+1)-f(n),n,z);
equation:=sub(ztrans(f(n),n,z)=tmp(z),equation);
load_package odesolve;
oderesult:=odesolve(equation,tmp(z),z);
preresult:=invztrans(part(first(oderesult),2),z,n);
solveresult:=
solve({sub(n=0,preresult)=f(0),sub(n=1,preresult)=f(1)},arbconst(1));
result:=preresult where solveresult;

end;
