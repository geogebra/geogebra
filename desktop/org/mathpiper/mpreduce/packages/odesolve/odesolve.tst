% Tests and demonstrations for the ODESolve 1+ package --
% an updated version of the original odesolve test file.

% Original Author: M. A. H. MacCallum
% Maintainer: F.J.Wright@Maths.QMW.ac.uk

ODESolve_version;
on trode, combinelogs;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% First-order differential equations
% (using automatic variable and dependence declaration).

% First-order quadrature case:
odesolve(df(y,x) - x^2 - e^x);

% First-order linear equation, with initial condition y = 1 at x = 0:
odesolve(df(y,x) + y * tan x - sec x, y, x, {x=0, y=1});
odesolve(cos x * df(y,x) + y * sin x - 1, y, x, {x=0, y=1});

% A simple separable case:
odesolve(df(y,x) - y^2, y, x, explicit);

% A separable case, in different variables, with the initial condition
% z = 2 at w = 1/2:
odesolve((1-z^2)*w*df(z,w)+(1+w^2)*z, z, w, {w=1/2, z=2});

% Now a homogeneous one:
odesolve(df(y,x) - (x-y)/(x+y), y, x);

% Reducible to homogeneous:
% (Note this is the previous example with origin shifted.)
odesolve(df(y,x) - (x-y-3)/(x+y-1), y, x);

% and the special case of reducible to homogeneous:
odesolve(df(y,x) - (2x+3y+1)/(4x+6y+1), y, x);

% A Bernoulli equation:
odesolve(x*(1-x^2)*df(y,x) + (2x^2 -1)*y - x^3*y^3, y, x);

% and finally, in this set, an exact case:
odesolve((2x^3 - 6x*y + 6x*y^2) + (-3x^2 + 6x^2*y - y^3)*df(y,x), y, x);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Now for higher-order linear equations with constant coefficients

% First, examples without driving terms
% A simple one to start:
odesolve(6df(y,x,2) + df(y,x) - 2y, y, x);

% An example with repeated and complex roots:
odesolve(ode := df(y,x,4) + 2df(y,x,2) + y, y, x);

% A simple right-hand-side using the above example:
odesolve(ode = exp(x), y, x);

ode := df(y,x,2) + 4df(y,x) + 4y - x*exp(x);
% At x=1 let y=0 and df(y,x)=1:
odesolve(ode, y, x, {x=1, y=0, df(y,x)=1});

% For simultaneous equations you can use the machine, e.g. as follows:

depend z,x;
ode1 := df(y,x,2) + 5y - 4z + 36cos(7x);
ode2 := y + df(z,x,2) - 99cos(7x);
ode := df(ode1,x,2) + 4ode2;
y := rhs first odesolve(ode, y, x);
z := rhs first solve(ode1,z);
clear ode1, ode2, ode, y, z;
nodepend z,x;

% A "homogeneous" n-th order (Euler) equation:
odesolve(x*df(y,x,2) + df(y, x) + y/x + (log x)^3, y, x);

% The solution here remains symbolic (because neither REDUCE nor Maple
% can evaluate the resulting integral):
odesolve(6df(y,x,2) + df(y,x) - 2y + tan x, y, x);

end;
