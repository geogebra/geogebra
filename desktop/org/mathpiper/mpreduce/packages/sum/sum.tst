% Tests of the SUM package.

% Author: Fujio Kako (kako@kako.math.sci.hiroshima-u.ac.jp)

% 1) Summations.

sum(n,n);

for i:=2:10 do write sum(n**i,n);

sum((n+1)**3,n);

sum(x**n,n);

sum(n**2*x**n,n);

sum(1/n,n);

sum(1/n/(n+2),n);

sum(log (n/(n+1)),n);

% 2) Expressions including trigonometric functions.

sum(sin(n*x),n);

sum(n*sin(n*x),n,1,k);

sum(cos((2*r-1)*pi/n),r);

sum(cos((2*r-1)*pi/n),r,1,n);

sum(cos((2*r-1)*pi/(2*n+1)),r);

sum(cos((2*r-1)*pi/(2*n+1)),r,1,n);

sum(sin((2*r-1)*x),r,1,n);

sum(cos((2*r-1)*x),r,1,n);

sum(sin(n*x)**2,n);

sum(cos(n*x)**2,n);

sum(sin(n*x)*sin((n+1)*x),n);

sum(sec(n*x)*sec((n+1)*x),n);

sum(1/2**n*tan(x/2**n),n);

sum(sin(r*x)*sin((r+1)*x),r,1,n);

sum(sec(r*x)*sec((r+1)*x),r,1,n);

sum(1/2**r*tan(x/2**r),r,1,n);

sum(k*sin(k*x),k,1,n - 1);

sum(k*cos(k*x),k,1,n - 1);

sum(sin((2k - 1)*x),k,1,n);

sum(sin(x + k*y),k,0,n);

sum(cos(x + k*y),k,0,n);

sum((-1)**(k - 1)*sin((2k - 1)*x),k,1,n + 1);

sum((-1)**(k - 1)*cos((2k - 1)*x),k,1,n + 1);

sum(r**k*sin(k*x),k,1,n - 1);

sum(r**k*cos(k*x),k,0,n - 1);

sum(sin(k*x)*sin((k + 1)*x),k,1,n);

sum(sin(k*x)*sin((k + 2)*x),k,1,n);

sum(sin(k*x)*sin((2k - 1)*x),k,1,n);


% The next examples cannot be summed in closed form.

sum(1/(cos(x/2**k)*2**k)**2,k,1,n);

sum((2**k*sin(x/2**k)**2)**2,k,1,n);

sum(tan(x/2**k)/2**k,k,0,n);

sum(cos(k**2*2*pi/n),k,0,n - 1);

sum(sin(k*pi/n),k,1,n - 1);

% 3) Expressions including the factorial function.

for all n,m such that fixp m let
	factorial(n+m)=if m > 0 then factorial(n+m-1)*(n+m)
		   else factorial(n+m+1)/(n+m+1);

sum(n*factorial(n),n);

sum(n/factorial(n+1),n);

sum((n**2+n-1)/factorial(n+2),n);

sum(n*2**n/factorial(n+2),n);

sum(n*x**n/factorial(n+2),n);

for all n,m such that fixp m and m > 3 let
	factorial((n+m)/2)= factorial((n+m)/2-1)*((n+m)/2),
	factorial((n-m)/2)= factorial((n-m)/2+1)/((n-m)/2+1);

sum(factorial(n-1/2)/factorial(n+1),n);

for all n,m such that fixp m and m > 3 clear factorial((n+m)/2);

for all n,m such that fixp m and m > 3 clear factorial((n-m)/2);

% 4) Expressions including combination.

operator comb;          % Combination function.

for all n ,m let comb(n,m)=factorial(n)/factorial(n-m)/factorial(m);

sum((-1)**k*comb(n,k),k,1,m);

sum(comb(n + p,q)/comb(n + r,q + 2),n,1,m);

sum((-1)**(k + 1)*comb(n,k)/(k + 1),k,1,n);

for all n ,m clear comb(n,m);

for all n,m such that fixp m clear factorial(n+m);

% 3) Examples taken from
%         "Decision procedure for indefinite hypergeometric summation"
%          Proc. Natl. Acad. Sci. USA  vol. 75, no. 1 pp.40-42 (1978)
%          R. William Gosper, Jr.
%

%            n
%           ____     2
%       f =  ||  (b*k +c*k+d)
%           k=1
%
%            n
%           ____     2
%       g =  ||  (b*k +c*k+e)
%           k=1
%
operator f,gg;  % gg used to avoid possible conflict with high energy
		% physics operator.

for all n,m such that fixp m let
        f(n+m)=if m > 0 then f(n+m-1)*(b*(n+m)**2+c*(n+m)+d)
                   else f(n+m+1)/(b*(n+m+1)**2+c*(n+m+1)+d);

for all n,m such that fixp m let
	gg(n+m)=if m > 0 then gg(n+m-1)*(b*(n+m)**2+c*(n+m)+e)
		   else gg(n+m+1)/(b*(n+m+1)**2+c*(n+m+1)+e);

sum(f(n-1)/gg(n),n);

sum(f(n-1)/gg(n+1),n);

for all n,m such that fixp m clear f(n+m);

for all n,m such that fixp m clear gg(n+m);

clear f,gg;

% 4) Products.

prod(n/(n+2),n);

prod(x**n,n);

prod(e**(sin(n*x)),n);

end;
