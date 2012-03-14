% -*- REDUCE -*-
% The Postel/Zimmermann (11/4/96) ODE test examples.
% Equation names from Postel/Zimmermann.
% This version uses REDUCE-style variable notation wherever possible.

on trode;
on div, intstr;  off allfac;            % to look prettier

% 1  Single equations without initial conditions
% ==============================================

% 1.1 Linear equations
% ====================

depend y, x;

% (1) Linear Bernoulli 1
odesolve((x^4-x^3)*df(y,x) + 2*x^4*y = x^3/3 + C, y, x);

% (2) Linear Bernoulli 2
odesolve(-1/2*df(y,x) + y = sin x, y, x);

% (3) Linear change of variables (FJW: shifted Euler equation)
odesolve(df(y,x,2)*(a*x+b)^2 + 4df(y,x)*(a*x+b)*a + 2y*a^2 = 0, y, x);

% (4) Adjoint
odesolve((x^2-x)*df(y,x,2) + (2x^2+4x-3)*df(y,x) + 8x*y = 1, y, x);

% (5) Polynomial solutions
% (FJW: currently very slow, and fails anyway!)
% odesolve((x^2-x)*df(y,x,2) + (1-2x^2)*df(y,x) + (4x-2)*y = 0, y, x);

% (6) Dependent variable missing
odesolve(df(y,x,2) + 2x*df(y,x) = 2x, y, x);

% (7) Liouvillian solutions
% (FJW: INTEGRATION IMPOSSIBLY SLOW WITHOUT EITHER ALGINT OR NOINT OPTION)
begin scalar !*allfac;  !*allfac := t;  return
   odesolve((x^3/2-x^2)*df(y,x,2) + (2x^2-3x+1)*df(y,x) + (x-1)*y = 0,
      y, x, algint);
end;
% NB: DO NOT RE-EVALUATE RESULT WITHOUT TURNING ON ALGINT OR NOINT SWITCH

% (8) Reduction of order
% (FJW: Attempting to make explicit currently too slow.)
odesolve(df(y,x,2) - 2x*df(y,x) + 2y = 3, y, x);

% (9) Integrating factors
% (FJW: Currently very slow, and fails anyway!)
% odesolve(sqrt(x)*df(y,x,2) + 2x*df(y,x) + 3y = 0, y, x);

% (10) Radical solution (FJW: omitted for now)

% (11) Undetermined coefficients
odesolve(df(y,x,2) - 2/x^2*y = 7x^4 + 3*x^3, y, x);

% (12) Variation of parameters
odesolve(df(y,x,2) + y = csc(x), y, x);

% (13) Linear constant coefficients
<< factor exp(x);  write
odesolve(df(y,x,7) - 14df(y,x,6) + 80df(y,x,5) - 242df(y,x,4)
   + 419df(y,x,3) - 416df(y,x,2) + 220df(y,x) - 48y = 0, y, x);
remfac exp(x) >>;

% (14) Euler
odesolve(df(y,x,4) - 4/x^2*df(y,x,2) + 8/x^3*df(y,x) - 8/x^4*y = 0, y, x);

% (15) Exact n-th order
odesolve((1+x+x^2)*df(y,x,3) + (3+6x)*df(y,x,2) + 6df(y,x) = 6x, y, x);


% 1.2 Nonlinear equations
% =======================

% (16) Integrating factors 1
odesolve(df(y,x) = y/(y*log y + x), y, x);

% (17) Integrating factors 2
odesolve(2y*df(y,x)^2 - 2x*df(y,x) - y = 0, y, x);
% This parametric solution is correct, cf. Zwillinger (1989) p.168 (41.10)
% (except that first edition is missing the constant C)!

% (18) Bernoulli 1
odesolve(df(y,x) + y = y^3*sin x, y, x, explicit);
expand_plus_or_minus ws;

% (19) Bernoulli 2
depend {P, Q}, x;
begin scalar soln, !*exp, !*allfac;  % for a neat solution
   on allfac;
   soln := odesolve(df(y,x) + P*y = Q*y^n, y, x);
   off allfac;  return soln
end;
odesolve(df(y,x) + P*y = Q*y^(2/3), y, x);

% (20) Clairaut 1
odesolve((x^2-1)*df(y,x)^2 - 2x*y*df(y,x) + y^2 - 1 = 0, y, x, explicit);

% (21) Clairaut 2
operator f, g;
odesolve(f(x*df(y,x)-y) = g(df(y,x)), y, x);

% (22) Equations of the form  y' = f(x,y)
odesolve(df(y,x) = (3x^2-y^2-7)/(exp(y)+2x*y+1), y, x);

% (23) Homogeneous
odesolve(df(y,x) = (2x^3*y-y^4)/(x^4-2x*y^3), y, x);

% (24) Factoring the equation
odesolve(df(y,x)*(df(y,x)+y) = x*(x+y), y, x);

% (25) Interchange variables
% (NB: Soln in Zwillinger (1989) wrong, as is last eqn in Table 68!)
odesolve(df(y,x) = x/(x^2*y^2+y^5), y, x);

% (26) Lagrange 1
odesolve(y = 2x*df(y,x) - a*df(y,x)^3, y, x);
odesolve(y = 2x*df(y,x) - a*df(y,x)^3, y, x, implicit);
% root_of quartic is VERY slow if explicit option used!

% (27) Lagrange 2
odesolve(y = 2x*df(y,x) - df(y,x)^2, y, x);
odesolve(y = 2x*df(y,x) - df(y,x)^2, y, x, implicit);

% (28) Riccati 1
odesolve(df(y,x) = exp(x)*y^2 - y + exp(-x), y, x);

% (29) Riccati 2
factor x;
odesolve(df(y,x) = y^2 - x*y + 1, y, x);
remfac x;

% (30) Separable
odesolve(df(y,x) = (9x^8+1)/(y^2+1), y, x);

% (31) Solvable for x
odesolve(y = 2x*df(y,x) + y*df(y,x)^2, y, x);
odesolve(y = 2x*df(y,x) + y*df(y,x)^2, y, x, implicit);

% (32) Solvable for y
begin scalar !*allfac;  !*allfac := t;  return
   odesolve(x = y*df(y,x) - x*df(y,x)^2, y, x)
end;

% (33) Autonomous 1
odesolve(df(y,x,2)-df(y,x) = 2y*df(y,x), y, x, explicit);

% (34) Autonomous 2  (FJW: Slow without either algint or noint option.)
odesolve(df(y,x,2)/y - df(y,x)^2/y^2 - 1 + 1/y^3 = 0, y, x, algint);

% (35) Differentiation method
odesolve(2y*df(y,x,2) - df(y,x)^2 = 1/3(df(y,x) - x*df(y,x,2))^2, y, x, explicit);

% (36) Equidimensional in x
odesolve(x*df(y,x,2) = 2y*df(y,x), y, x, explicit);

% (37) Equidimensional in y
odesolve((1-x)*(y*df(y,x,2)-df(y,x)^2) + x^2*y^2 = 0, y, x);

% (38) Exact second order
odesolve(x*y*df(y,x,2) + x*df(y,x)^2 + y*df(y,x) = 0, y, x, explicit);

% (39) Factoring differential operator
odesolve(df(y,x,2)^2 - 2df(y,x)*df(y,x,2) + 2y*df(y,x) - y^2 = 0, y, x);

% (40) Scale invariant (fails with algint option)
odesolve(x^2*df(y,x,2) + 3x*df(y,x) = 1/(y^3*x^4), y, x);

% Revised scale-invariant example (hangs with algint option):
ode := x^2*df(y,x,2) + 3x*df(y,x) + 2*y = 1/(y^3*x^4);
% Choose full (explicit and expanded) solution:
odesolve(ode, y, x, full);              % or "explicit, expand"
% Check it -- each solution should simplify to 0:
foreach soln in ws collect
   trigsimp sub(soln, num(lhs ode - rhs ode));

% (41) Autonomous, 3rd order
odesolve((df(y,x)^2+1)*df(y,x,3) - 3df(y,x)*df(y,x,2)^2 = 0, y, x);

% (42) Autonomous, 4th order
odesolve(3*df(y,x,2)*df(y,x,4) - 5df(y,x,3)^2 = 0, y, x);


% 1.3 Special equations
% =====================

% (43) Delay
operator y;
odesolve(df(y(x),x) + a*y(x-1) = 0, y(x), x);

% (44) Functions with several parameters
odesolve(df(y(x,a),x) = a*y(x,a), y(x,a), x);


% 2  Single equations with initial conditions
% ===========================================

% (45) Exact 4th order
odesolve(df(y,x,4) = sin x, y, x,
   {x=0, y=0, df(y,x)=0, df(y,x,2)=0, df(y,x,3)=0});

% (46) Linear polynomial coefficients -- Bessel J0
odesolve(x*df(y,x,2) + df(y,x) + 2x*y = 0, y, x,
   {x=0, y=1, df(y,x)=0});

% (47) Second-degree separable
soln :=
   odesolve(x*df(y,x)^2 - y^2 + 1 = 0, y=1, x=0, explicit);
% Alternatively ...
soln where e^~x => cosh x + sinh x;
% but this works ONLY with `on div, intstr; off allfac;'
% A better alternative is ...
trigsimp(soln, hyp, combine);
expand_plus_or_minus ws;

% (48) Autonomous
odesolve(df(y,x,2) + y*df(y,x)^3 = 0, y, x, {x=0, y=0, df(y,x)=2});
%% Only one explicit solution satisfies the conditions:
begin scalar !*trode, !*fullroots;  !*fullroots := t;  return
   odesolve(df(y,x,2) + y*df(y,x)^3 = 0, y, x,
      {x=0, y=0, df(y,x)=2}, explicit);
end;


% 3  Systems of equations
% =======================

% (49) Integrable combinations
depend {x, y, z}, t;
odesolve({df(x,t) = -3y*z, df(y,t) = 3x*z, df(z,t) = -x*y}, {x,y,z}, t);

% (50) Matrix Riccati
depend {a, b}, t;
odesolve({df(x,t) = a*(y^2-x^2) + 2b*x*y + 2c*x,
   df(y,t) = b*(y^2-x^2) - 2a*x*y + 2c*y}, {x,y}, t);

% (51) Triangular
odesolve({df(x,t) = x*(1 + cos(t)/(2+sin(t))), df(y,t) = x - y},
   {x,y}, t);

% (52) Vector
odesolve({df(x,t) = 9x + 2y, df(y,t) = x + 8y}, {x,y}, t);

% (53) Higher order
odesolve({df(x,t) - x + 2y = 0, df(x,t,2) - 2df(y,t) = 2t - cos(2t)},
   {x,y}, t);

% (54) Inhomogeneous system
equ := {df(x,t) = -1/(t*(t^2+1))*x + 1/(t^2*(t^2+1))*y + 1/t,
        df(y,t) = -t^2/(t^2+1)*x + (2t^2+1)/(t*(t^2+1))*y + 1};
odesolve(equ, {x,y}, t);

end;
