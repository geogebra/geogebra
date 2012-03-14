on errcont;
bounds (x,x=(1 .. 2));
bounds (2*x,x=(1 .. 2));
bounds (x**3,x=(1 .. 2));
bounds (x*y,x=(1 .. 2),y=(-1 .. 0));
bounds (x**3+y,x=(1 .. 2),y=(-1 .. 0));
bounds (x**3/y,{x=(1 .. 2),y=(-1 .. -0.5)});
bounds (x**3/y,x=(1 .. 2),y=(-1 .. -0.5));
   % unbounded expression (pole at y=0)
bounds (x**3/y,x=(1 .. 2),y=(-1 .. 0.5));

on rounded;
bounds(e**x,x=(1 .. 2));
bounds((1/2)**x,x=(1 .. 2));
off rounded;

bounds(abs x,x=(1 .. 2));
bounds(abs x,x=(-3 .. 2));
bounds(abs x,x=(-3 .. -2));

bounds(sin x,x=(1 .. 2));
 
on rounded;

bounds(sin x,x=(1 .. 2));
bounds(sin x,x=(1 .. 10));
bounds(sin x,x=(1001 .. 1002));

bounds(log x,x=(1 .. 10));

bounds(tan x,x=(1 .. 1.1));

bounds(cot x,x=(1 .. 1.1));
bounds(asin x,x=(-0.6 .. 0.6));
bounds(acos x,x=(-0.6 .. 0.6));

bounds(sqrt(x),x=(1 .. 1.1));
bounds(x**(7/3),x=(1 .. 1.1));
bounds(x**y,x=(1 .. 1.1),y=(2 .. 4));
 
off rounded;


% MINIMA  (steepest descent)

% Rosenbrock function (minimum extremely hard to find).
fktn := 100*(x1^2-x2)^2 + (1-x1)^2;
num_min(fktn, x1=-1.2, x2=1, accuracy=6);

% infinitely many local minima
num_min(sin(x)+x/5, x=1);

% bivariate polynomial
num_min(x^4 + 3 x^2 * y + 5 y^2 + x + y, x=0.1, y=0.2);


% ROOTS (non polynomial: damped Newton)

 num_solve (cos x -x, x=0,accuracy=6);

   % automatically randomized starting point
num_solve (cos x -x,x, accuracy=6);  
 
   % syntactical errors: forms do not evaluate to purely 
   % numerical values
num_solve (cos x -x, x=a);
num_solve (cos x -a, x=0);

num_solve (sin x = 0, x=3);

  % blows up: no real solution exists
num_solve(sin x = 2, x=1);

  % solution in complex plane(only fond with complex starting point):
on complex;
num_solve(sin x = 2, x=1+i);
off complex;

  % blows up for derivative 0 in starting point 
num_solve(x^2-1, x=0);

  % succeeds because of perturbed starting point
num_solve(x^2-1, x=0.1);

  % bivariate equation system
num_solve({sin x=cos y, x + y = 1},{x=1,y=2});
on rounded,evallhseqp;
sub(ws,{sin x=cos y, x + y = 1});
off rounded,evallhseqp;
 
  % temporal member of the Barry Simon test sequence
sys :={sin (x) + y^2 + log(z) = 7,
       3*x + 2^y - z^3 = -90,
       x^2 + y^2 + z^(1/2) = 16};
sol:=num_solve(sys,{x=1,y=1,z=1});
on rounded;
for each s in sys collect sub(sol,lhs s-rhs s);  
off rounded;
clear sys,sol;
 
  % 2 examples taken from Nowak/Weimann (Tech.Rep TR91-10, ZIB Berlin)
 
  % #1: exp/sin combination

on rounded;
sys := {e**(x1**2 + x2**2)-3, x1 + x2 - sin(3(x1 + x2))};
num_solve(sys,x1=0.81, x2=0.82);
sub(ws,sys);

  % 2nd example (semiconductor simulation), here computed with 
  % intermediate steps printed

alpha := 38.683;
ni := 1.22e10;
v := 100;
d := 1e17;
sys := { e**(alpha*(x3-x1)) - e**(alpha*(x1-x2)) - d/ni,
         x2,
         x3,
         e**(alpha*(x6-x4)) - e**(alpha*(x4-x5)) + d/ni,
         x5 - v,
         x6 - v};
on trnumeric;
num_solve(sys,x1=1,x2=2,x3=3,x4=4,x5=5,x6=6,iterations=100);
off trnumeric;
clear alpha,ni,v,d,sys;
off rounded;

% INTEGRALS
 
num_int( x**2,x=(1 .. 2),accuracy=3);

  % 1st case: using formal integral
needle := 1/(10**-4 + x**2);
num_int(needle,x=(-1 .. 1),accuracy=3);           % 312.16

  % no formal integral, but easy Chebyshev fit
num_int(sin x/x,x=(1 .. 10));

  % using a Chebyshev fit of order 60
num_int(exp(-x**2),x=(-10 .. 10),accuracy=3);     % 1.772

   % cases with singularities

num_int(1/sqrt x ,x=(0 .. 1),accuracy=2);          % 1.999

num_int(1/sqrt abs x ,x=(-1 .. 1),iterations=50);     % 3.999

   % simple multidimensional integrals
num_int(x+y,x=(0 .. 1),y=(2 .. 3));

num_int(sin(x+y),x=(0 .. 1),y=(0 .. 1));

% some integrals with infinite bounds
 
on rounded; % for the error function

num_int(e^(-x) ,x=(0 .. infinity));  % 1.000

2/sqrt(pi)* num_int(e^(-x^2) ,x=(0 .. infinity)); % 1.00

2/sqrt(pi)* num_int(e^(-x^2), x=(-infinity .. infinity)); % 2.00

num_int(sin(x) * e^(-x), x=(0 .. infinity)); % 0.500
 
off rounded;
 
% APPROXIMATION
 
  %approximate sin x by a cubic polynomial 
num_fit(sin x,{1,x,x**2,x**3},x=for i:=0:20 collect 0.1*i);
 
  % approximate x**2 by a harmonic series in the interval [0,1]
num_fit(x**2,1 . for i:=1:5 join {sin(i*x)},
               x=for i:=0:10 collect i/10);
 
  % approximate a set of points by a polynomial
pts:=for i:=1 step 0.1 until 3 collect i$
vals:=for each p in pts collect (p+2)**3$
num_fit(vals,{1,x,x**2,x**3},x=pts);
  % compute the approximation error
on rounded;
first ws - (x+2)**3;
off rounded;


 
% ODE SOLUTION (Runge-Kutta)
 
depend(y,x);

   % approximate y=y(x) with df(y,x)=2y in interval [0 : 5]
num_odesolve(df(y,x)=y,y=2,x=(0 .. 5),iterations=20);
 
   % same with negative direction
num_odesolve(df(y,x)=y,y=2,x=(0 .. -5),iterations=20);

   % giving a nice picture when plotted
num_odesolve(df(y,x)=1- x*y**2 ,y=0,x=(0 .. 4),iterations=20);

   % system of ordinary differential equations
depend(y,x);
depend(z,x);
num_odesolve(
    {df(z,x) = y, df(y,x)=  y+x},
    {z=2, y=4},
     x=(0 .. 5),iterations=20);


%----------------- Chebyshev fit -------------------------

on rounded;

func := x**2 * (x**2 - 2) * sin x;
ord := 15;

cx:=chebyshev_fit(func,x=(0 .. 2),ord)$
cp:=first cx;
cc:=second cx;

for u:=0 step 0.2 until 2 do write
    "x:",u," true value:",sub(x=u,func),
           " Chebyshev eval:", chebyshev_eval(cc,x=(0 .. 2),x=u),
           " Chebyshev polynomial:",sub(x=u,cp);

% integral

   % integrate coefficients
ci := chebyshev_int(cc,x=(0 .. 2));

   % compare with true values (normalized absolute term)
ci0:=chebyshev_eval(ci,x=(0 .. 2),x=0)$
ifunc := int(func,x)$ if0 := sub(x=0,ifunc);

for u:=0 step 0.2 until 2 do write
       {u,sub(x=u,ifunc) - if0,
          chebyshev_eval(ci,x=(0 .. 2),x=u) - ci0};

% derivative

   % differentiate coefficients
cd := chebyshev_df(cc,x=(0 .. 2))$
   % compute coefficients of derivative
cds := second chebyshev_fit(df(func,x),x=(0 .. 2),ord)$
   % compare coefficients
for i:=1:ord do write {part(cd,i),part(cds,i)};

clear func,ord,cc,cx,cd,cds,ci,ci0;

% One from ISSAC '97 -- should be  ~ 1.10*10^36300

f := x^(x^x); num_int(f,x= (1 .. 6),iterations=40);

off rounded;

end;
