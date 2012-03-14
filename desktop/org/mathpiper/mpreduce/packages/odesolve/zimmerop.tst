% -*- REDUCE -*-
% The Postel/Zimmermann (11/4/96) ODE test examples.
% Equation names from Postel/Zimmermann.
% This version uses Maple-style functional notation wherever possible.

% on trode;
on div, intstr;  off allfac;            % to look prettier

% 1  Single equations without initial conditions
% ==============================================

% 1.1 Linear equations
% ====================

operator y;

% (1) Linear Bernoulli 1
odesolve((x^4-x^3)*df(y(x),x) + 2*x^4*y(x) = x^3/3 + C, y(x), x);

% (2) Linear Bernoulli 2
odesolve(-1/2*df(y(x),x) + y(x) = sin x, y(x), x);

% (3) Linear change of variables (FJW: shifted Euler equation)
odesolve(df(y(x),x,2)*(a*x+b)^2 + 4df(y(x),x)*(a*x+b)*a + 2y(x)*a^2 = 0,
   y(x), x);

% (4) Adjoint
odesolve((x^2-x)*df(y(x),x,2) + (2x^2+4x-3)*df(y(x),x) + 8x*y(x) = 1,
   y(x), x);

% (5) Polynomial solutions
% (FJW: Currently very slow, and fails anyway!)
% odesolve((x^2-x)*df(y(x),x,2) + (1-2x^2)*df(y(x),x) + (4x-2)*y(x) = 0,
%    y(x), x);

% (6) Dependent variable missing
odesolve(df(y(x),x,2) + 2x*df(y(x),x) = 2x, y(x), x);

% (7) Liouvillian solutions
% (FJW: INTEGRATION IMPOSSIBLY SLOW WITHOUT EITHER ALGINT OR NOINT OPTION)
begin scalar !*allfac;  !*allfac := t;  return
   odesolve((x^3/2-x^2)*df(y(x),x,2) + (2x^2-3x+1)*df(y(x),x) + (x-1)*y(x) = 0,
      y(x), x, noint);
end;
% WARNING: DO NOT RE-EVALUATE RESULT WITHOUT TURNING ON THE NOINT SWITCH

% (8) Reduction of order
% (FJW: Attempting to make explicit currently too slow.)
odesolve(df(y(x),x,2) - 2x*df(y(x),x) + 2y(x) = 3, y(x), x);

% (9) Integrating factors
% (FJW: Currently very slow, and fails anyway!)
% odesolve(sqrt(x)*df(y(x),x,2) + 2x*df(y(x),x) + 3y(x) = 0, y(x), x);

% (10) Radical solution (FJW: omitted for now)

% (11) Undetermined coefficients
odesolve(df(y(x),x,2) - 2/x^2*y(x) = 7x^4 + 3*x^3, y(x), x);

% (12) Variation of parameters
odesolve(df(y(x),x,2) + y(x) = csc(x), y(x), x);

% (13) Linear constant coefficients
<< factor exp(x);  write
odesolve(df(y(x),x,7) - 14df(y(x),x,6) + 80df(y(x),x,5) - 242df(y(x),x,4)
   + 419df(y(x),x,3) - 416df(y(x),x,2) + 220df(y(x),x) - 48y(x) = 0, y(x), x);
remfac exp(x) >>;

% (14) Euler
odesolve(df(y(x),x,4) - 4/x^2*df(y(x),x,2) + 8/x^3*df(y(x),x) - 8/x^4*y(x) = 0,
   y(x), x);

% (15) Exact n-th order
odesolve((1+x+x^2)*df(y(x),x,3) + (3+6x)*df(y(x),x,2) + 6df(y(x),x) = 6x,
   y(x), x);


% 1.2 Nonlinear equations
% =======================

% (16) Integrating factors 1
odesolve(df(y(x),x) = y(x)/(y(x)*log y(x) + x), y(x), x);

% (17) Integrating factors 2
odesolve(2y(x)*df(y(x),x)^2 - 2x*df(y(x),x) - y(x) = 0, y(x), x);
% This parametric solution is correct, cf. Zwillinger (1989) p.168 (41.10)
% (except that first edition is missing the constant C)!

% (18) Bernoulli 1
odesolve(df(y(x),x) + y(x) = y(x)^3*sin x, y(x), x, explicit);
expand_plus_or_minus ws;

% (19) Bernoulli 2
operator P, Q;
begin scalar soln, !*exp, !*allfac;  % for a neat solution
   on allfac;
   soln := odesolve(df(y(x),x) + P(x)*y(x) = Q(x)*y(x)^n, y(x), x);
   off allfac;  return soln
end;
odesolve(df(y(x),x) + P(x)*y(x) = Q(x)*y(x)^(2/3), y(x), x);

% (20) Clairaut 1
odesolve((x^2-1)*df(y(x),x)^2 - 2x*y(x)*df(y(x),x) + y(x)^2 - 1 = 0,
   y(x), x, explicit);

% (21) Clairaut 2
operator f, g;
odesolve(f(x*df(y(x),x)-y(x)) = g(df(y(x),x)), y(x), x);

% (22) Equations of the form  y' = f(x,y)
odesolve(df(y(x),x) = (3x^2-y(x)^2-7)/(exp(y(x))+2x*y(x)+1), y(x), x);

% (23) Homogeneous
odesolve(df(y(x),x) = (2x^3*y(x)-y(x)^4)/(x^4-2x*y(x)^3), y(x), x);

% (24) Factoring the equation
odesolve(df(y(x),x)*(df(y(x),x)+y(x)) = x*(x+y(x)), y(x), x);

% (25) Interchange variables
% (NB: Soln in Zwillinger (1989) wrong, as is last eqn in Table 68!)
odesolve(df(y(x),x) = x/(x^2*y(x)^2+y(x)^5), y(x), x);

% (26) Lagrange 1
odesolve(y(x) = 2x*df(y(x),x) - a*df(y(x),x)^3, y(x), x);
odesolve(y(x) = 2x*df(y(x),x) - a*df(y(x),x)^3, y(x), x, implicit);
% root_of quartic is VERY slow if explicit option used!

% (27) Lagrange 2
odesolve(y(x) = 2x*df(y(x),x) - df(y(x),x)^2, y(x), x);
odesolve(y(x) = 2x*df(y(x),x) - df(y(x),x)^2, y(x), x, implicit);

% (28) Riccati 1
odesolve(df(y(x),x) = exp(x)*y(x)^2 - y(x) + exp(-x), y(x), x);

% (29) Riccati 2
<< factor x;  write
odesolve(df(y(x),x) = y(x)^2 - x*y(x) + 1, y(x), x);
remfac x >>;

% (30) Separable
odesolve(df(y(x),x) = (9x^8+1)/(y(x)^2+1), y(x), x);

% (31) Solvable for x
odesolve(y(x) = 2x*df(y(x),x) + y(x)*df(y(x),x)^2, y(x), x);
odesolve(y(x) = 2x*df(y(x),x) + y(x)*df(y(x),x)^2, y(x), x, implicit);

% (32) Solvable for y
begin scalar !*allfac;  !*allfac := t;  return
   odesolve(x = y(x)*df(y(x),x) - x*df(y(x),x)^2, y(x), x)
end;

% (33) Autonomous 1
odesolve(df(y(x),x,2)-df(y(x),x) = 2y(x)*df(y(x),x), y(x), x, explicit);

% (34) Autonomous 2  (FJW: Slow without either algint or noint option.)
odesolve(df(y(x),x,2)/y(x) - df(y(x),x)^2/y(x)^2 - 1 + 1/y(x)^3 = 0,
   y(x), x, noint);

% (35) Differentiation method
odesolve(2y(x)*df(y(x),x,2) - df(y(x),x)^2 =
   1/3(df(y(x),x) - x*df(y(x),x,2))^2, y(x), x, explicit);

% (36) Equidimensional in x
odesolve(x*df(y(x),x,2) = 2y(x)*df(y(x),x), y(x), x, explicit);

% (37) Equidimensional in y
odesolve((1-x)*(y(x)*df(y(x),x,2)-df(y(x),x)^2) + x^2*y(x)^2 = 0, y(x), x);

% (38) Exact second order
odesolve(x*y(x)*df(y(x),x,2) + x*df(y(x),x)^2 + y(x)*df(y(x),x) = 0,
   y(x), x, explicit);

% (39) Factoring differential operator
odesolve(df(y(x),x,2)^2 - 2df(y(x),x)*df(y(x),x,2) + 2y(x)*df(y(x),x) -
   y(x)^2 = 0, y(x), x);

% (40) Scale invariant (fails with algint option)
odesolve(x^2*df(y(x),x,2) + 3x*df(y(x),x) = 1/(y(x)^3*x^4), y(x), x);

% Revised scale-invariant example (hangs with algint option):
ode := x^2*df(y(x),x,2) + 3x*df(y(x),x) + 2*y(x) = 1/(y(x)^3*x^4);
% Choose full (explicit and expanded) solution:
odesolve(ode, y(x), x, full);           % or "explicit, expand"
% Check it -- each solution should simplify to 0:
foreach soln in ws collect
   trigsimp sub(soln, num(lhs ode - rhs ode));

% (41) Autonomous, 3rd order
odesolve((df(y(x),x)^2+1)*df(y(x),x,3) - 3df(y(x),x)*df(y(x),x,2)^2 = 0,
   y(x), x);
% odesolve((df(y(x),x)^2+1)*df(y(x),x,3) - 3df(y(x),x)*df(y(x),x,2)^2 = 0,
%    y(x), x, implicit);
% Implicit form is currently too messy!

% (42) Autonomous, 4th order
odesolve(3*df(y(x),x,2)*df(y(x),x,4) - 5df(y(x),x,3)^2 = 0, y(x), x);


% 1.3 Special equations
% =====================

% (43) Delay
odesolve(df(y(x),x) + a*y(x-1) = 0, y(x), x);

% (44) Functions with several parameters
odesolve(df(y(x,a),x) = a*y(x,a), y(x,a), x);


% 2  Single equations with initial conditions
% ===========================================

% (45) Exact 4th order
odesolve(df(y(x),x,4) = sin x, y(x), x,
   {x=0, y(x)=0, df(y(x),x)=0, df(y(x),x,2)=0, df(y(x),x,3)=0});

% (46) Linear polynomial coefficients -- Bessel J0
odesolve(x*df(y(x),x,2) + df(y(x),x) + 2x*y(x) = 0, y(x), x,
   {x=0, y(x)=1, df(y(x),x)=0});

% (47) Second-degree separable
soln :=
   odesolve(x*df(y(x),x)^2 - y(x)^2 + 1 = 0, y(x)=1, x=0, explicit);
% Alternatively ...
soln where e^~x => cosh x + sinh x;
% but this works ONLY with `on div, intstr; off allfac;'
% A better alternative is ...
trigsimp(soln, hyp, combine);
expand_plus_or_minus ws;

% (48) Autonomous
odesolve(df(y(x),x,2) + y(x)*df(y(x),x)^3 = 0, y(x), x,
   {x=0, y(x)=0, df(y(x),x)=2});
%% Only one explicit solution satisfies the conditions:
begin scalar !*trode, !*fullroots;  !*fullroots := t;  return
   odesolve(df(y(x),x,2) + y(x)*df(y(x),x)^3 = 0, y(x), x,
      {x=0, y(x)=0, df(y(x),x)=2}, explicit);
end;


% 3  Systems of equations
% =======================

% (49) Integrable combinations
operator x, z;
odesolve({df(x(t),t) = -3y(t)*z(t), df(y(t),t) = 3x(t)*z(t),
   df(z(t),t) = -x(t)*y(t)}, {x(t),y(t),z(t)}, t);

% (50) Matrix Riccati
operator a, b;
odesolve({df(x(t),t) = a(t)*(y(t)^2-x(t)^2) + 2b(t)*x(t)*y(t) + 2c*x(t),
   df(y(t),t) = b(t)*(y(t)^2-x(t)^2) - 2a(t)*x(t)*y(t) + 2c*y(t)},
   {x(t),y(t)}, t);

% (51) Triangular
odesolve({df(x(t),t) = x(t)*(1 + cos(t)/(2+sin(t))),
   df(y(t),t) = x(t) - y(t)}, {x(t),y(t)}, t);

% (52) Vector
odesolve({df(x(t),t) = 9x(t) + 2y(t), df(y(t),t) = x(t) + 8y(t)},
   {x(t),y(t)}, t);

% (53) Higher order
odesolve({df(x(t),t) - x(t) + 2y(t) = 0,
   df(x(t),t,2) - 2df(y(t),t) = 2t - cos(2t)}, {x(t),y(t)}, t);

% (54) Inhomogeneous system
equ := {df(x(t),t) = -1/(t*(t^2+1))*x(t) + 1/(t^2*(t^2+1))*y(t) + 1/t,
        df(y(t),t) = -t^2/(t^2+1)*x(t) + (2t^2+1)/(t*(t^2+1))*y(t) + 1};
odesolve(equ, {x(t),y(t)}, t);

end;
