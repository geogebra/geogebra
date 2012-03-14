% Tests of the ZEILBERG package.

% Authors: Gregor Stoelting, Wolfram Koepf (koepf@zib-berlin.de)

load_package sum;
% on time;

% 1) Successful summations by the Gosper algorithm
% R. W. Gosper, Jr.:
% Decision procedure for indefinite hypergeometric summation,
% Proc. Nat. Acad. Sci. USA 75 (1978), 40-42.

gosper(k,k);
gosper(k^2,k);
gosper(k^3,k);
gosper(k^4,k);
gosper(k^5,k);
% gosper(k^20,k);
gosper((6*k+3)/(4*k^4+8*k^3+8*k^2+4*k+3),k);
% gosper(2^k*(k^3-3*k^2-3*k-1)/(k^3*(k+1)^3),k);
gosper(x*k,k);
gosper(k*x^k,k);
gosper(k*factorial(k),k);
gosper(1/(k^2-1),k);
gosper((1+2*k)/((1+k^2)*(k^2+2*k+2)),k);
gosper((k^2+4*k+1)*factorial(k),k);
gosper((4*k-3)*factorial(2*k-2)/factorial(k-1),k);
gosper(gamma(k+n+2)*n/((k+n+1)*gamma(k+2)*gamma(n+1)),k);
gosper((k+n)*factorial(k+n),k);
gosper((3*(1+2*k))/((1+k^2)*(2+2*k+k^2)),k);
% gosper((-25+15*k+18*k^2-2*k^3-k^4)/
% (-23+479*k+613*k^2+137*k^3+53*k^4+5*k^5+k^6),k);
% gosper(3^k*(2*k^4+4*k^3-7*k^2-k-4)/(k*(k+1)*(k^2+1)*((k+1)^2+1)),k);
gosper(3^k*(4*k^2-2*k-3)/((2*k+3)*(2*k+1)*(k+1)*k),k);
% gosper(2^k*(2*k^3+3*k^2-20*k-15)/
% ((2*k+3)*(2*k+1)*(k+5)*(k+4)*(k+1)*k),k);
% gosper(-2^k*((k+11)^2*(k+1)^2-2*(k+10)^2*k^2)/
% ((k+11)^2*(k+10)^2*(k+1)^2*k^2),k);
% gosper(-2^k*((k+6)^2*(k+1)^2-2*(k+5)^2*k^2)/
% ((k+6)^2*(k+5)^2*(k+1)^2*k^2),k);
% gosper(2^k*(k^4-14*k^2-24*k-9)/(k^2*(k+1)^2*(k+2)^2*(k+3)^2),k);
% gosper(((k^2-k-n^2-n-2)*gamma(k+n+2)*gamma(n+1))/
% (2*(-1)^k*2^k*(k+n+1)*gamma(-(k-n-1))*gamma(k+2)),k);
% gosper(1/(k+1)*binomial(2*k,k)/(n-k+1)*binomial(2*n-2*k,n-k),k);
gosper(3^k*(4*k^2+2*a*k-4*k-2-a)/((2*k+2+a)*(2*k+a)*(k+1)*k),k);
gosper(2^k*(k^2-2*k-1)/(k^2*(k+1)^2),k);
gosper((3*k^2+3*k+1)/(k^3*(k+1)^3),k);
gosper((6*k+3)/(4*k^4+8*k^3+8*k^2+4*k+3),k);
gosper(-(k^2+3*k+3)/(k^4+2*k^3-3*k^2-4*k+2),k);
gosper(k^2*4^k/((k+1)*(k+2)),k);
gosper((2*k-1)^3,k);
gosper(3*k^2+3*k+1,k);
gosper((k^2+4*k+2)*2^k,k);
% gosper(2^k*(k^3-3*k^2-3*k-1)/(k^3*(k+1)^3),k);
gosper(k*n^k,k);
% gosper(3^k*(2*k^3+k^2+3*k+6)/((k^2+2)*(k^2+2*k+3)),k);
% gosper(4*(1-k)*(k^2-2*k-1)/(k^2*(k+1)^2*(k-2)^2*(k-3)^2),k);
% gosper(2^k*(k^4-14*k^2-24*k-9)/(k^2*(k+1)^2*(k+2)^2*(k+3)^2),k);
gosper((1+k)/(1-k)+2/k,k);
gosper(1/(k*(k+1)),k);
gosper(1/(k*(k+2)),k);
gosper(1/(k*(k+10)),k);
% gosper(1/(k*(k+30)),k);
gosper(1/(k*(k+1)*(k+2)),k);
gosper(1/(k*(k+1)*(k+2)*(k+3)*(k+4)*(k+5)*(k+6)*(k+7)*(k+8)*(k+9)*
	  (k+10)),k);
gosper(pochhammer(k-n,n),k);
gosper((a+k-1)*pochhammer(a,k),k);
gosper((a-k-1)/pochhammer(a-k,k),k);
gosper(binomial(k,n),k); 
gosper(k*binomial(k,n),k);
% gosper(k^10*binomial(k,n),k);
gosper(1/binomial(k,n),k); 
gosper(k/binomial(k,n),k);
% gosper(k^10/binomial(k,n),k);
gosper(binomial(k-n,k),k);
gosper((-1)^k*binomial(n,k),k);
gosper((-1)^k/binomial(n,k),k);
gosper((-1)^(k+1)*(4*k+1)*factorial(2*k)/
(factorial(k)*4^k*(2*k-1)*factorial(k+1)),k);
% term:=3^k*(3*k^2+2*a*k-4*k-2-a)/((2*k+2+a)*(2*k+a)*(k+1)*k)$
% term:=sub(k=k+3,term)-term$
% gosper(term,k);
% clear(term);

% 2) Examples for the Wilf-Zeilberger method:
% H. S. Wilf and D. Zeilberger:
% Rational functions certify combinatorial identities. 
% J. Amer. Math. Soc. 3, 1990, 147-158.

% Binomial theorem
summand:=binomial(n,k)/2^n$
gosper(sub(n=n+1,summand)-summand,k);

% Vandermonde
summand:=binomial(n,k)^2/binomial(2*n,n)$
gosper(sub(n=n+1,summand)-summand,k);

% Gauss
% summand:=factorial(n+k)*factorial(b+k)*factorial(c-n-1)*factorial(c-b-1)
% /(factorial(n-1)*factorial(c-n-b-1)*factorial(k+1)*factorial(c+k)*
% factorial(b-1))$
% gosper(sub(n=n+1,summand)-summand,k);

% Kummer
% summand:=(-1)^(n+k)*factorial(2*n+c-1)*factorial(n)*factorial(n+c-1)/(
% factorial(2*n+c-1-k)*factorial(2*n-k)*factorial(c+k-1)*factorial(k))$
% gosper(sub(n=n+1,summand)-summand,k);

% Saalschuetz
% summand:=factorial(a+k-1)*factorial(b+k-1)*factorial(n)*
% factorial(n+c-a-b-k-1)*factorial(n+c-1)/(factorial(k)*factorial(n-k)*
% factorial(k+c-1)*factorial(n+c-a-1)*factorial(n+c-b-1))$
% gosper(sub(n=n+1,summand)-summand,k);

% Dixon
% summand:=(-1)^k*binomial(n+b,n+k)*binomial(n+c,c+k)*binomial(b+c,b+k)*
% factorial(n)*factorial(b)*factorial(c)/factorial(n+b+c)$
% gosper(sub(n=n+1,summand)-summand,k);

% 3) Results from Gosper's original work
% R. W. Gosper, Jr.:
% Decision procedure for indefinite hypergeometric summation,
% Proc. Nat. Acad. Sci. USA 75 (1978), 40-42.

% ff(k)=product(a+b*j+c*j^2,j,1,k);
% gg(k)=product(e+b*j+c*j^2,j,1,k);

operator ff,gg;  

let {ff(~k+~m) => ff(k+m-1)*(c*(k+m)^2+b*(k+m)+a)
		     when (fixp(m) and m>0),
ff(~k+~m) => ff(k+m+1)/(c*(k+m+1)^2+b*(k+m+1)+a)
		when (fixp(m) and m<0)};

let {gg(~k+~m) => gg(k+m-1)*(c*(k+m)^2+b*(k+m)+e)
		     when (fixp(m) and m>0),
gg(~k+~m) => gg(k+m+1)/(c*(k+m+1)^2+b*(k+m+1)+e)
		when (fixp(m) and m<0)};

gosper(ff(k-1)/gg(k),k);
% gosper(ff(k-1)/gg(k+1),k);
% gosper(ff(k-1)/gg(k+2),k);

% ff(k)=product(a+b*j+c*j^2+d*j^3,j,1,k);
% gg(k)=product(e+b*j+c*j^2+d*j^3,j,1,k);

let {
ff(~k+~m) => ff(k+m-1)*(d*(k+m)^3+c*(k+m)^2+b*(k+m)+a)
		when (fixp(m) and m>0),
ff(~k+~m) => ff(k+m+1)/(d*(k+m+1)^3+c*(k+m+1)^2+b*(k+m+1)+a) 
when (fixp(m) and m<0)};

let {
gg(~k+~m) => gg(k+m-1)*(d*(k+m)^3+c*(k+m)^2+b*(k+m)+e)
		when (fixp(m) and m>0),
gg(~k+~m) => gg(k+m+1)/(d*(k+m+1)^3+c*(k+m+1)^2+b*(k+m+1)+e) 
when (fixp(m) and m<0)};

gosper(ff(k-1)/gg(k),k);
gosper(ff(k-1)/gg(k+1),k);
% Decision: no closed form solution exists 

% ff(k)=product(a+b*j+c*j^2+d*j^3+e*j^4,j,1,k);
% gg(k)=product(f+b*j+c*j^2+d*j^3+e*j^4,j,1,k);

let {
ff(~k+~m) => ff(k+m-1)*(e*(k+m)^4+d*(k+m)^3+c*(k+m)^2+b*(k+m)+a) 
when (fixp(m) and m>0),
ff(~k+~m) => ff(k+m+1)/(e*(k+m+1)^4+d*(k+m+1)^3+c*(k+m+1)^2+b*(k+m+1)+a)
when (fixp(m) and m<0)};

let {
gg(~k+~m) => gg(k+m-1)*(e*(k+m)^4+d*(k+m)^3+c*(k+m)^2+b*(k+m)+f) 
when (fixp(m) and m>0),
gg(~k+~m) =>
   gg(k+m+1)/(e*(k+m+1)^4+d*(k+m+1)^3+c*(k+m+1)^2+b*(k+m+1)+f)
      when (fixp(m) and m<0)};

gosper(ff(k-1)/gg(k),k);

% ff=product(j^3+b*j^2+c*j+(2*c-4*b+8),j,1,k);
% gg=product(j^3+b*j^2+c*j,j,1,k)

let {
ff(~k+~m) => ff(k+m-1)*((k+m)^3+c*(k+m)^2+b*(k+m)+(2*c-4*b+8)) 
when (fixp(m) and m>0),
ff(~k+~m) => ff(k+m+1)/((k+m+1)^2+c*(k+m+1)^2+b*(k+m+1)+(2*c-4*b+8)) 
when (fixp(m) and m<0)};

let {
gg(~k+~m) => gg(k+m-1)*((k+m)^3+c*(k+m)^2+b*(k+m)+1)
		when (fixp(m) and m>0),
gg(~k+~m) => gg(k+m+1)/((k+m+1)^2+c*(k+m+1)^2+b*(k+m+1)+1) 
when (fixp(m) and m<0)};

gosper(ff(k-1)/gg(k),k);

clear(ff,gg);

% 4) Examples for which gosper decides that no hypergeometric term
% antidifference exists

gosper(factorial(k),k);
gosper(factorial(2*k)/(factorial(k)*factorial(k+1)),k);
% gosper(1/(factorial(k)*(k^4+k^2+1)),k);
gosper(binomial(A,k),k);
gosper(1/k,k);
gosper((1+k)/(1-k),k);
% gosper(3^k*(3*k^2+2*a*k-4*k-2-a)/((2*k+2+a)*(2*k+a)*(k+1)*k),k);
gosper(factorial(k+n)*factorial(n)/
((-1)^k*factorial(n-k)*factorial(k)*2^k),k);
gosper(1/(k*(k+1/2)),k);
gosper(pochhammer(a,k),k);
gosper(binomial(n,k),k);

% 5) Finding recurrence equations for definite sums
% D. Zeilberger,
% A fast algorithm for proving terminating hypergeometric identities,
% Discrete Math. 80 (1990), 207-211.

sumrecursion(binomial(n,k),k,n);
sumrecursion(k*binomial(n,k),k,n);
% sumrecursion(
% (-1)^k*binomial(2*n,k)*binomial(2*k,k)*binomial(4*n-2*k,2*n-k),k,n);
sumrecursion(binomial(n,k)^2,k,n);
sumrecursion(binomial(n,k)^2/binomial(2*n,n),k,n);
% sumrecursion((-1)^k*binomial(n,k)^2,k,n);
% Gauss
sumrecursion(
factorial(n+k)*factorial(b+k)*factorial(c-n-1)*factorial(c-b-1),k,n);
sumrecursion(
pochhammer(a,k)*pochhammer(b,k)/(factorial(k)*pochhammer(c,k)),k,a);
% Kummer
sumrecursion((-1)^(n+k)*factorial(2*n+c-1)*factorial(n)*factorial(n+c-1)
/(factorial(2*n+c-1-k)*factorial(2*n-k)*factorial(c+k-1)*
   factorial(k)),k,n);
sumrecursion((-1)^k/(
factorial(2*n+c-1-k)*factorial(2*n-k)*factorial(c+k-1)*
   factorial(k)),k,n);
% Saalschuetz
% sumrecursion(factorial(a+k-1)*factorial(b+k-1)*factorial(n)*
% factorial(n+c-a-b-k-1)*factorial(n+c-1)/
% (factorial(k)*factorial(n-k)*factorial(k+c-1)*
% factorial(n+c-a-1)*factorial(n+c-b-1)),k,n);
sumrecursion(factorial(a+k-1)*factorial(b+k-1)*factorial(n+c-a-b-k-1)/(
factorial(k)*factorial(n-k)*factorial(k+c-1)),k,n);
% Dixon
% sumrecursion((-1)^k*binomial(n+b,n+k)*binomial(n+c,c+k)*
%    binomial(b+c,b+k)*
% factorial(n)*factorial(b)*factorial(c)/factorial(n+b+c),k,n);
sumrecursion((-1)^k*binomial(n+b,n+k)*binomial(n+c,c+k)*
   binomial(b+c,b+k),k,n);
sumrecursion((-1)^(k-n)*binomial(2*n,k)^3,k,n);
sumrecursion(
(-1)^(k-n)*binomial(2*n,k)^3/(binomial(3*n,n)*binomial(2*n,n)),k,n);
% Clausen
% summand:=factorial(a+k-1)*factorial(b+k-1)/
% (factorial(k)*factorial(-1/2+a+b+k))*factorial(a+n-k-1)*
%     factorial(b+n-k-1)/(factorial(n-k)*factorial(-1/2+a+b+n-k))$
% sumrecursion(summand,k,n);
% Dougall
% summand:=
% pochhammer(d,k)*pochhammer(1+d/2,k)*pochhammer(d+b-a,k)*
%   pochhammer(d+c-a,k)*
% pochhammer(1+a-b-c,k)*pochhammer(n+a,k)*pochhammer(-n,k)/
%    (factorial(k)*
% pochhammer(d/2,k)*pochhammer(1+a-b,k)*pochhammer(1+a-c,k)*
% pochhammer(b+c+d-a,k)*pochhammer(1+d-a-n,k)*pochhammer(1+d+n,k))$
% sumrecursion(summand,k,n);
% Apery
sumrecursion(binomial(n,k)^2*binomial(n+k,k)^2,k,n);
% sumrecursion(4*(-1)^k*binomial(m-1,k)*binomial(2*m-1,2*k)/
% binomial(4*m-1,4*k)*(4*m^2+16*k^2-16*k*m+16*k-6*m+3)/
% ((4*m-4*k-3)*(4*m-4*k-1)),k,m);
sumrecursion((-1)^k*binomial(n,k)*binomial(k,n),k,n);
sumrecursion((-1)^k*binomial(n,k)*binomial(2*k,n),k,n);
sumrecursion((-1)^k*binomial(n,k)*binomial(k,j)^2,k,n);
sumrecursion(binomial(n,k)*binomial(a,k),k,n);
sumrecursion((3*k-2*n)*binomial(n,k)^2*binomial(2*k,k),k,n);
sumrecursion(binomial(n-k,k),k,n);
sumrecursion(binomial(n,k)*binomial(n+k,k),k,n);
% sumrecursion(binomial(n+k,m+2*k)*binomial(2*k,k)*(-1)^k/(k+1),k,n);
sumrecursion((-1)^k*binomial(n-k,k)*binomial(n-2*k,m-k),k,n);
% sumrecursion((-1)^k*binomial(n-k,k)*binomial(n-2*k,m-k),k,m);
sumrecursion(binomial(n+k,2*k)*2^(n-k),k,n);
sumrecursion(binomial(n,k)*binomial(2*k,k)*(-1/4)^k,k,n);
% sumrecursion(binomial(n,i)*binomial(2*n,n-i),i,n);
sumrecursion((-1)^k*binomial(n,k)*binomial(2*k,k)*4^(n-k),k,n);
sumrecursion((-1)^k*binomial(n,k)/binomial(k+a,k),k,n);
% sumrecursion((-1)^k*binomial(n,k)/binomial(k+a,k),k,a);
sumrecursion((-1)^(n-k)*binomial(2*n,k)^2,k,n);
sumrecursion(factorial(a+k)*factorial(b+k)*factorial(n+c-a-b-k-1)/(
factorial(k+1)*factorial(n-k)*factorial(k+c)),k,a);
% sumrecursion(factorial(a+k)*factorial(b+k)*factorial(n+c-a-b-k-1)/(
% factorial(k+1)*factorial(n-k)*factorial(k+c)),k,b);
% sumrecursion(factorial(a+k)*factorial(b+k)*factorial(n+c-a-b-k-1)/(
% factorial(k+1)*factorial(n-k)*factorial(k+c)),k,c);
sumrecursion(binomial(2*n+1,2*p+2*k+1)*binomial(p+k,k),k,n);
% sumrecursion(binomial(2*n+1,2*p+2*k+1)*binomial(p+k,k),k,p);
sumrecursion(binomial(r,m)*binomial(s,t-m),m,r);
% sumrecursion(binomial(r,m)*binomial(s,t-m),m,s);
% sumrecursion(binomial(r,m)*binomial(s,t-m),m,t);
sumrecursion(binomial(2*n+1,2*k)*binomial(m+k,2*n),k,n);
% sumrecursion(binomial(2*n+1,2*k)*binomial(m+k,2*n),k,m);
sumrecursion(binomial(n,k)*binomial(k,j)*x^j,k,n);
% sumrecursion(binomial(n,k)*binomial(k,j)*x^j,k,j);
% sumrecursion(binomial(n,k)*binomial(k,j)*x^k,k,n);
sumrecursion(x*binomial(n+k,2*k)*((x^2-1)/4)^(n-k),k,n);
sumrecursion(binomial(n+k-1,2*k-1)*(x-1)^(2*k)*x^(n-k)/k,k,n);
sumrecursion(
1/(k+1)*binomial(2*k,k)/(n-k+1)*binomial(2*n-2*k,n-k),k,n);
sumrecursion(binomial(m,r)*binomial(n-r,n-r-q)*(t-1)^r,r,m);
% sumrecursion(binomial(m,r)*binomial(n-r,n-r-q)*(t-1)^r,r,n);
% sumrecursion(binomial(m,r)*binomial(n-r,n-r-q)*(t-1)^r,r,q);
% sumrecursion(binomial(m,r)*binomial(n-r,n-r-q)*(t-1)^r,r,r);
sumrecursion(pochhammer(-n/2,k)*pochhammer(-n/2+1/2,k)/
(factorial(k)*pochhammer(b+1/2,k)),k,n);
% Watson
% sumrecursion(pochhammer(a,k)*pochhammer(b,k)*pochhammer(c,k)/(
% factorial(k)*pochhammer(1/2*(a+b+1),k)*pochhammer(2*c,k)),k,c);
% sumrecursion(pochhammer(-m,j)*pochhammer(m+2*k+2,j)*pochhammer(k+1/2,j)/
% (factorial(j)*pochhammer(k+3/2,j)*pochhammer(2*k+1,j)),j,k);
sumrecursion((-1)^k*binomial(n,k)^3,k,n);
% sumrecursion(pochhammer(-n,k)*pochhammer(n+2*a,k)*pochhammer(a,k)/(
% factorial(k)*pochhammer(2*a/2,k)*pochhammer((2*a+1)/2,k))*(2/4)^k,k,n);
% sumrecursion(pochhammer(-n,k)*pochhammer(n+4*a,k)*pochhammer(a,k)/(
% factorial(k)*pochhammer(4*a/2,k)*pochhammer((4*a+1)/2,k))*(4/4)^k,k,n);
% sumrecursion(binomial(n+k+1,n-k)*pochhammer(-n+k,j)*pochhammer(k+1/2,j)*
% pochhammer(n+k+2,j)/(factorial(j)*pochhammer(k+3/2,j)*
%    pochhammer(2*k+1,j)),j,n);
% sumrecursion(pochhammer(-m,j)*pochhammer(m+2*k+2,j)*
%    pochhammer(k+1/2,j)/(
% factorial(j)*pochhammer(k+3/2,j)*pochhammer(2*k+1,j)),j,m);
% sumrecursion(binomial(n+k+1,n-k)*pochhammer(-n+k,j)*
%   pochhammer(k+1/2,j)*
% pochhammer(n+k+2,j)/(factorial(j)*pochhammer(k+3/2,j)*
%   pochhammer(2*k+1,j)),
% j,k);
% sumrecursion(pochhammer(a+b+c-n,j+l)*pochhammer(a+b-n/2,j+l)/
% (factorial(j)*factorial(l)*pochhammer(a-n/2+1,j)*
%   pochhammer(b-n/2+1,l)),j,a);
% sumrecursion(pochhammer(a+b+c-n,j+l)*pochhammer(a+b-n/2,j+l)/
% (factorial(j)*factorial(l)*pochhammer(a-n/2+1,j)*
%   pochhammer(b-n/2+1,l)),j,b);
sumrecursion(pochhammer(a+b+c-n,j+l)*pochhammer(a+b-n/2,j+l)/
(factorial(j)*factorial(l)*pochhammer(a-n/2+1,j)*
   pochhammer(b-n/2+1,l)),j,c);
% sumrecursion(
% (-1)^(a+b+c)*gamma(a+b+c-d/2)*gamma(d/2-c)*gamma(a+c-d/2)*
%    gamma(b+c-d/2)/
% (gamma(a)*gamma(b)*gamma(d/2)*gamma(a+b+2*c-d)*(m^2)^(a+b+c-d))*
% pochhammer(a+b+c-d,k)*pochhammer(a+c-d/2,k)/
% (pochhammer(a+b+2*c-d,k)*factorial(k)),k,a);
% sumrecursion(
% (-1)^(a+b+c)*gamma(a+b+c-d/2)*gamma(d/2-c)*gamma(a+c-d/2)*
%  gamma(b+c-d/2)/
% (gamma(a)*gamma(b)*gamma(d/2)*gamma(a+b+2*c-d)*(m^2)^(a+b+c-d))*
% pochhammer(a+b+c-d,k)*pochhammer(a+c-d/2,k)/
% (pochhammer(a+b+2*c-d,k)*factorial(k)),k,b);
% sumrecursion(
% (-1)^(a+b+c)*gamma(a+b+c-d/2)*gamma(d/2-c)*gamma(a+c-d/2)*
%  gamma(b+c-d/2)/
% (gamma(a)*gamma(b)*gamma(d/2)*gamma(a+b+2*c-d)*(m^2)^(a+b+c-d))*
% pochhammer(a+b+c-d,k)*pochhammer(a+c-d/2,k)/
% (pochhammer(a+b+2*c-d,k)*factorial(k)),k,c);
% sumrecursion(pochhammer(-n,k)*pochhammer(n+3*a,k)*pochhammer(a,k)/(
% factorial(k)*pochhammer(3*a/2,k)*
%    pochhammer((3*a+1)/2,k))*(3/4)^k,k,n);
% summand:=k*(-1)^j*pochhammer(2*k+j+1,j)*pochhammer(2*k+2*j+2,n-k-j)/(
% factorial(k+j)*factorial(j)*factorial(n-k-j))*exp(-(j+k)*t)$
% summand:=k*(-1)^j*pochhammer(2*k+j+1,j)*pochhammer(2*k+2*j+2,n-k-j)/(
% (k+j)*factorial(j)*factorial(n-k-j))*exp(-(j+k)*t)$
% sumrecursion(summand,j,n);
clear(summand);

% 6) Finding recurrence equations for hypergeometric functions
% Koornwinder, T. H.:
% On Zeilberger's algorithm and
% its q-analogue: a rigorous description.
% J. of Comput. and Appl. Math. 48, 1993, 91-111.

% Gauss
hyperrecursion({a,b},{c},1,a);
% Dougall
% hyperrecursion({d,1+d/2,d+b-a,d+c-a,1+a-b-c,n+a,-n},
%               {d/2,1+a-b,1+a-c,b+c+d-a,1+d-a-n,1+d+n},1,n);
% Baxter
% hyperrecursion({-n,-n-1,-n-2},{2,3},-1,n);
% Krawtchouk polynomials
% krawtchoukterm :=
%    (-1)^n*p^n*binomial(NN,n)*hyperterm({-n,-x},{-NN},1/p,k)$
% sumrecursion(krawtchoukterm,k,n);
% sumrecursion(krawtchoukterm,k,x);
% sumrecursion(krawtchoukterm,k,NN);
% clear(krawtchoukterm);
% hyperrecursion({-n,b,c+4},{b+1,c},1,n);
hyperrecursion({-n,b,c+1,d+1},{b+1,c,d},1,n);

% 7) Extended versions of Gosper's and Zeilberger's algorithms
% Koepf, W.:
% Algorithms for the indefinite and definite summation.
% Konrad-Zuse-Zentrum Berlin (ZIB), Preprint SC 94-33, 1994.

% extended Gosper algorithm
extended_gosper(k*factorial(k/7),k,7);
extended_gosper(k*factorial(k/2),k,2);
extended_gosper(k*factorial(k/2),k);
extended_gosper(binomial(k/2,n),k);
extended_gosper(binomial(n,k/2)-binomial(n,k/2-1),k);

% extended Zeilberger algorithm
% extended_sumrecursion(binomial(n,k)*binomial(k/2,n),k,n,1,2);
sumrecursion(binomial(n,k)*binomial(k/2,n),k,n);
extended_sumrecursion(binomial(n/2,k),k,n,2,1);
sumrecursion(binomial(n/2,k),k,n);
% sumrecursion(hyperterm({a,b,a+1/2-b,1+2*a/3,-n},
% {2*a+1-2*b,2*b,2/3*a,1+a+n/2},4,k)/
% (factorial(n)*2^(-n)/factorial(n/2))/
% hyperterm({a+1,1},{a-b+1,b+1/2},1,n/2),k,n);

% Watson
% sumrecursion(pochhammer(a,k)*pochhammer(b,k)*pochhammer(c,k)/(
% factorial(k)*pochhammer(1/2*(a+b+1),k)*pochhammer(2*c,k))/
% (GAMMA(1/2)*GAMMA(1/2+c)*GAMMA(1/2+a/2+b/2)*GAMMA(1/2-a/2-b/2+c))*
% GAMMA(1/2+a/2)*GAMMA(1/2+b/2)*GAMMA(1/2-a/2+c)*GAMMA(1/2-b/2+c),k,a);
% hyperrecursion({a,b,c},{1/2*(a+b+1),2*c},1,a);
% hyperrecursion({a,b,c},{1/2*(a+b+1),2*c},1,b);

% 8) Closed form representations of hypergeometric sums

% Vandermonde
hypersum({-n,b},{c},1,n);
% Saalschuetz
hypersum({a,b,-n},{c,1+a+b-c-n},1,n);
% Kummer
hypersum({a,-n},{1+a+n},-1,n);
% Dixon
hypersum({a,b,-n},{1+a-b,1+a+n},1,n);
% Dougall
% hypersum({a,1+a/2,b,c,d,1+2*a-b-c-d+n,-n},
% {a/2,1+a-b,1+a-c,1+a-d,1+a-(1+2*a-b-c-d+n),1+a+n},1,n);
% Clausen
% hypersum({a,b,1/2-a-b-n,-n},{1/2+a+b,1-a-n,1-b-n},1,n);

hypersum({a,1+a/2,c,d,-n},{a/2,1+a-c,1+a-d,1+a+n},1,n);
hypersum({a,1+a/2,d,-n},{a/2,1+a-d,1+a+n},-1,n);

% m-fold case:
hypersum({-n,-n,-n},{1,1},1,n);
% hypersum({-n,n+3*a,a},{3*a/2,(3*a+1)/2},3/4,n);

% 9) Hypergeometric representations

sumtohyper(binomial(n,k)^3,k);
sumtohyper(binomial(n,k)/2^n-sub(n=n-1,binomial(n,k)/2^n),k);
sumtohyper(binomial(k+j-1,k-j)*2*(-1)^(j+1)*factorial(2*j-1)/         
factorial(j-1)/factorial(j+1)*x^j,j);
% term:=1/(n-1+k)*(1/2-1/2*x)^k/n*binomial(k-n-1,-n-1)*k*
%         binomial(n-1+k,n-1);
% sumtohyper(sub(n=n+1,term)-term,k);

end;
