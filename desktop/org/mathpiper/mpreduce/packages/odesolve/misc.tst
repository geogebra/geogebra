% Miscellaneous ODESolve 1+ tests

% Check for a problem in 1.03, spotted by David Hartley
% <DHartley@physics.adelaide.edu.au>, caused by the reval in
% `get_k_list' with caching enabled.  The following should all give
% the same result:

odesolve(df(u,x,x)=df(u,x));
odesolve(df(u,x,2)=df(u,x));
odesolve(df(u,x,x)=df(u,x), u, x);


% Linear first-order ODE:
odesolve(df(y,t) = -w*y*tan(w*t - d));
% The solution, by inspection, is y = A cos(w t - d)


% Variation of parameters:
depend y, x;

ode := df(y,x,2) + y - csc(x)$
odesolve(ode, y, x);
sub(ws, ode);
trigsimp ws;

ode := 2*df(y,x,2) + y - csc(x)$
odesolve(ode, y, x);
sub(ws, ode);
trigsimp ws;


% Bernoulli:
ode := df(y,x)*y*x^2 - y^2*x - x^3 + 1;
odesolve(ode, y, x, explicit);
sub(ws, ode);


% Implicit dependence:

% (NB: Wierd constants need to be mopped up by the arbconst
% simplification code!)

% These should all behave equivalently:
operator f, g;
depend {y, ff}, x, {gg}, y;

odesolve(df(y,x) = f(x), y, x);
odesolve(df(y,x) = ff, y, x);
odesolve(df(y,x) = g(y), y, x);
odesolve(df(y,x) = gg, y, x);
odesolve(df(y,x) = f(x)*g(y), y, x);
odesolve(df(y,x) = ff*gg, y, x);
odesolve(df(y,x) = 1/f(x)*g(y), y, x);
odesolve(df(y,x) = 1/ff*gg, y, x);
odesolve(df(y,x) = f(x)/g(y), y, x);
odesolve(df(y,x) = ff/gg, y, x);

% These should all fail (they are too implicit):
depend {ff}, y, {gg}, x;

odesolve(df(y,x) = ff, y, x);
odesolve(df(y,x) = gg, y, x);
odesolve(df(y,x) = ff*gg, y, x);
odesolve(df(y,x) = 1/ff*gg, y, x);
odesolve(df(y,x) = ff/gg, y, x);


% NONlinear ODEs:
odesolve(df(y,x) + y**(5/3)*arbconst(-1)=0);

% Do not re-evaluate the solution without turning the algint switch on!
odesolve(df(y,x,2) + c/(y^2 + k^2)^(3/2) = 0, y, x, algint);

% Good test of ODESolve!-Alg!-Solve.  Takes forever with fullroots on,
% but with fullroots off ODESolve solves it.  (Slightly tidier with
% algint, but not necessary.  However, the explicit option misses the
% non-trivial solution that can fairly easily be found by hand!)
odesolve(df(y,x,3) = 6*df(y,x)*df(y,x,2)/y - 6*df(y,x)^3/(y^2), y, x, algint);

% Hangs with algint option!
% off odesolve_plus_or_minus;
odesolve(a*tan(asin((df(y,x) - y)/(2*y))/2)^2 + a -
   2*sqrt(3)*tan(asin((df(y,x) - y)/(2*y))/2)*y + 4*sqrt(3)*y +
   tan(asin((df(y,x) - y)/(2*y))/2)^2*y -
   4*tan(asin((df(y,x) - y)/(2*y))/2)*y + 7*y, y, x);
% on odesolve_plus_or_minus;

% From: K Sudhakar <ks@maths.qmw.ac.uk>
odesolve(2*df(f,x,3)*df(f,x)*f^2*x^2 - 3*df(f,x,2)^2*x^2*f^2 +
   df(f,x)^4*x^2 - df(f,x)^2*f^2, f, x);

% Related intermediate problem:
odesolve(2*df(y,x)*x*y + x^2 - 2*x*y - y^2, y, x, explicit);


% Anharmonic oscillator problem (which apparently Maple V R5.1 solves
% in terms of a root of an expression involving unevaluated integrals
% but Maple 6 cannot!).

% General solution:
odesolve(M*L*df(phi(tt),tt,2) = -M*g*sin(phi(tt)));

% Use of `t' as independent variable:
odesolve(M*L*df(phi(t),t,2) = -M*g*sin(phi(t)));

% Conditional (eigenvalue) solution:
%% odesolve(M*L*df(phi(t),t,2) = -M*g*sin(phi(t)),
%%    {t=0, phi(t)=0, df(phi(t),t)=Pi});
%%
%% Conditional solutions need more work!  This fails with
%% ***** 0 invalid as kernel

% Try setting
%% L:=1;  g:=10;  ws;

end;
