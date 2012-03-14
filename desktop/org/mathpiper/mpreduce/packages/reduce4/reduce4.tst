Comment This is a standard test file for REDUCE that has been used for
many years.  It only tests a limited number of facilities in the
current system.  In particular, it does not test floating point
arithmetic, or any of the more advanced packages that have been made
available since REDUCE 3.0 was released.  It has been used for a long
time to benchmark the performance of REDUCE.  A description of this
benchmarking with statistics for REDUCE 3.2 was reported in Jed B.
Marti and Anthony C. Hearn, "REDUCE as a Lisp Benchmark", SIGSAM Bull.
19 (1985) 8-16.  That paper also gives information on the the parts of
the system exercised by the test file.  Updated statistics may be found
in the "timings" file in the REDUCE Network Library;

showtime;

on reduce4;  % For the time being.

comment some examples of the FOR statement;

comment summing the squares of the even positive integers
	through 50;

for i:=2 step 2 until 50 sum i**2;

comment to set  w  to the factorial of 10;

w := for i:=1:10 product i;

comment alternatively, we could set the elements a(i) of the
	array  a  to the factorial of i by the statements;

array a(10);

a(0):=1$

for i:=1:10 do a(i):=i*a(i-1);

comment the above version of the FOR statement does not return
	an algebraic value, but we can now use these array
	elements as factorials in expressions, e. g.;

1+a(5);

comment we could have printed the values of each a(i)
	as they were computed by writing the FOR statement as;

for i:=1:10 do write "a(",i,") := ",a(i):= i*a(i-1);

comment another way to use factorials would be to introduce an
operator FAC by an integer procedure as follows;

procedure fac(n:int)
   begin local m:int;
	m:=1;
    l1:	if n=0 then return m;
	m:=m*n;
	n:=n-1;
	go to l1
   end;

comment we can now use  fac  as an operator in expressions, e. g.;

z**2+fac(4)-2*fac 2*y;

comment note in the above example that the parentheses around
the arguments of FAC may be omitted since it is a unary operator;

comment the following examples illustrate the solution of some
	complete problems;

comment the f and g series (ref  Sconzo, P., Leschack, A. R. and
	 Tobey, R. G., Astronomical Journal, Vol 70 (May 1965);

deps:= -sigma*(mu+2*epsilon)$
dmu:= -3*mu*sigma$
dsig:= epsilon-2*sigma**2$
f1:= 1$
g1:= 0$
 
for i:= 1:8 do 
 <<f2:= -mu*g1 + deps*df(f1,epsilon) + dmu*df(f1,mu) + dsig*df(f1,sigma);
   write "f(",i,") := ",f2;
   g2:= f1 + deps*df(g1,epsilon) + dmu*df(g1,mu) + dsig*df(g1,sigma);
   write "g(",i,") := ",g2;
   f1:=f2;
   g1:=g2>>;

comment a problem in Fourier analysis;

factor cos,sin;

on list;

(a1*cos(omega*t) + a3*cos(3*omega*t) + b1*sin(omega*t) +
	b3*sin(3*omega*t))**3 where
	{cos(~x)*cos(~y) => (cos(x+y)+cos(x-y))/2,
	       cos(~x)*sin(~y) => (sin(x+y)-sin(x-y))/2,
	       sin(~x)*sin(~y) => (cos(x-y)-cos(x+y))/2,
	       cos(~x)**2 => (1+cos(2*x))/2,
	       sin(~x)**2 => (1-cos(2*x))/2};

remfac cos,sin;

off list;

comment end of Fourier analysis example;

comment the following program, written in  collaboration  with  David
Barton  and  John  Fitch,  solves a problem in general relativity. it
will compute the Einstein tensor from any given metric;

on nero;

comment here we introduce the covariant and contravariant metrics;

operator p1,q1,x;

array gg(3,3),h(3,3);

gg(0,0):=e**(q1(x(1)))$
gg(1,1):=-e**(p1(x(1)))$
gg(2,2):=-x(1)**2$
gg(3,3):=-x(1)**2*sin(x(2))**2$

for i:=0:3 do h(i,i):=1/gg(i,i);

comment generate Christoffel symbols and store in arrays cs1 and cs2;

array cs1(3,3,3),cs2(3,3,3);

for i:=0:3 do for j:=i:3 do
   <<for k:=0:3 do
	cs1(j,i,k) := cs1(i,j,k):=(df(gg(i,k),x(j))+df(gg(j,k),x(i)) -
				      df(gg(i,j),x(k)))/2;
	for k:=0:3 do cs2(j,i,k):= cs2(i,j,k) := for p := 0:3 sum
				      h(k,p)*cs1(i,j,p)>>;

comment now compute the Riemann tensor and store in r(i,j,k,l);

array r(3,3,3,3);

for i:=0:3 do for j:=i+1:3 do for k:=i:3 do
   for l:=k+1:if k=i then j else 3 do
      <<r(j,i,l,k) := r(i,j,k,l) := for q := 0:3 sum
		    gg(i,q)*(df(cs2(k,j,q),x(l))-df(cs2(j,l,q),x(k)) +
		  for p:=0:3 sum (cs2(p,l,q)*cs2(k,j,p) -
			 cs2(p,k,q)*cs2(l,j,p)));
	r(i,j,l,k) := -r(i,j,k,l);
	r(j,i,k,l) := -r(i,j,k,l);
	if i neq k or j>l then
	       <<r(k,l,i,j) := r(l,k,j,i) := r(i,j,k,l);
		 r(l,k,i,j) := -r(i,j,k,l);
		 r(k,l,j,i) := -r(i,j,k,l)>>>>;

comment now compute and print the Ricci tensor;

array ricci(3,3);

for i:=0:3 do for j:=0:3 do  
    write ricci(j,i) := ricci(i,j) := for p := 0:3 sum for q := 0:3 sum
					h(p,q)*r(q,i,p,j);

comment now compute and print the Ricci scalar;

rs := for i:= 0:3 sum for j:= 0:3 sum h(i,j)*ricci(i,j);

comment finally compute and print the Einstein tensor;

array einstein(3,3);

for i:=0:3 do for j:=0:3 do
	 write einstein(i,j):=ricci(i,j)-rs*gg(i,j)/2;

comment end of Einstein tensor program;

clear gg,h,cs1,cs2,r,ricci,einstein;

comment an example using the matrix facility;

matrix xx,yy,zz;

let xx= mat((a11,a12),(a21,a22)),
   yy= mat((y1),(y2));

2*det xx - 3*w;

zz:= xx**(-1)*yy;

1/xx**2;

comment end of matrix examples;

comment a physics example;

on div; comment this gives us output in same form as Bjorken and Drell;

mass ki= 0, kf= 0, p1= m, pf= m;

vector eei,ef;

mshell ki,kf,p1,pf;

let p1.eei= 0, p1.ef= 0, p1.pf= m**2+ki.kf, p1.ki= m*k,p1.kf=
    m*kp, pf.eei= -kf.eei, pf.ef= ki.ef, pf.ki= m*kp, pf.kf=
    m*k, ki.eei= 0, ki.kf= m*(k-kp), kf.ef= 0, eei.eei= -1, ef.ef=
    -1; 

operator gp;

for all p let gp(p)= g(l,p)+m;

comment this is just to save us a lot of writing;

gp(pf)*(g(l,ef,eei,ki)/(2*ki.p1) + g(l,eei,ef,kf)/(2*kf.p1))
  * gp(p1)*(g(l,ki,eei,ef)/(2*ki.p1) + g(l,kf,ef,eei)/(2*kf.p1))$

write "The Compton cross-section is ",ws;

comment end of first physics example; 

off div;

comment another physics example;

index ix,iy,iz;

mass p1=mm,p2=mm,p3= mm,p4= mm,k1=0;

mshell p1,p2,p3,p4,k1;

vector qi,q2;

factor mm,p1.p3;

order mm;

operator ga,gb;

for all p let ga(p)=g(la,p)+mm, gb(p)= g(lb,p)+mm; 

ga(-p2)*g(la,ix)*ga(-p4)*g(la,iy)* (gb(p3)*g(lb,ix)*gb(qi)*
    g(lb,iz)*gb(p1)*g(lb,iy)*gb(q2)*g(lb,iz)   +   gb(p3)*
    g(lb,iz)*gb(q2)*g(lb,ix)*gb(p1)*g(lb,iz)*gb(qi)*g(lb,iy))$

let qi=p1-k1, q2=p3+k1;

comment it is usually faster to make such substitutions after all the
	trace algebra is done;

write "CXN =",ws;

comment end of second physics example; 

showtime;

end;
