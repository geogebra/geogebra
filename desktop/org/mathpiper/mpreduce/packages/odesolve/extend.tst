% Test and demonstration of the ODESolve extension interface

% F.J.Wright@Maths.QMW.ac.uk, Time-stamp: <17 July 2000>

% Load odesolve before inputting this file if not using test interface:
% load_package odesolve;

% Hook into the general ODE solver:

algebraic procedure ODESolve_Hook_Demo (ode, y, x);
   %% For any ODE, if the dependent variable is z then this hook
   %% procedure returns a solution corresponding to ODESolve failing
   %% to find any solution; otherwise it returns nil (nothing) and so
   %% is ignored.
   if y=z then {ode=0};

% Set the hook:
symbolic(ODESolve_Before_Hook := '(ODESolve_Hook_Demo));

% Hook into the nonlinear ODE solver:

algebraic procedure ODESolve_Non_Hook_Demo (ode, y, x, n);
   %% If the ODE is nontrivially nonlinear and the order is 3 then
   %% this hook procedure returns a solution corresponding to ODESolve
   %% failing to find any solution; otherwise it returns nil (nothing)
   %% and so is ignored.
   if n=3 then {ode=0};

% Set the hook:
symbolic(ODESolve_Before_Non_Hook := '(ODESolve_Non_Hook_Demo));

% Hook into the general linear ODE solver:

algebraic procedure ODESolve_Lin_Hook_Demo
   (odecoeffs, driver, y, x, n, m);
   %% If the ODE is linear and the order is 3 then this hook procedure
   %% returns a solution corresponding to ODESolve failing to find any
   %% solution; otherwise it returns nil (nothing) and so is ignored.
   %% (NB: Algebraic-mode lists are indexed from 1 in REDUCE!)
   if n=3 then
      {(for i := m : n sum part(odecoeffs,i+1)*df(y,x,i)) = driver};

% Set the hook:
symbolic(ODESolve_Before_Lin_Hook := '(ODESolve_Lin_Hook_Demo));

% Test all the hooks:

% The general ODE solver:
odesolve(df(y,x));                      % hook ignored
odesolve(df(z,x));                      % hook operates

% The nonlinear ODE solver:
odesolve(y*df(y,x,2)+1);                % hook ignored
odesolve(y*df(y,x,3)+1);                % hook operates

% The general linear ODE solver:
odesolve(df(y,x,2)+1);                  % hook ignored
odesolve(df(y,x,3)+1);                  % hook operates

% Clear the hooks:
symbolic(ODESolve_Before_Hook := nil);
symbolic(ODESolve_Before_Non_Hook := nil);
symbolic(ODESolve_Before_Lin_Hook := nil);

end;
