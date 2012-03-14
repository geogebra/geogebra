module odenon1$  % Special form nonlinear ODEs of order 1

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


% F.J.Wright@maths.qmw.ac.uk, Time-stamp: <11 August 2001>

% Original author: Malcolm MacCallum

% Basic layout is to test first for well-known special forms, namely:

%    separable
%    quasi-separable (separable after linear transformation)
%    (algebraically) homogeneous
%    quasi-homogeneous (homogeneous after linear transformation)
%    Bernoulli
%    Riccati
%    solvable for x or y, including Clairaut and Lagrange
%    exact  (FJW: general exact ode now handled elsewhere)

% and at a later stage of development to add more general methods,
% such as Prelle Singer

% Note that all cases of first order ODEs can be considered equivalent
% to searches for integrating factors.  (In particular Lie methods do
% not help here.)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% algebraic procedure odesolve(ode, y, x);
%%    %% Temporary definition for test purposes.
%%    begin scalar !*precise, !*odesolve!-solvable!-xy, solution;
%%       ode := num !*eqn2a ode;           % returns ode as expression
%%       if (solution := ODESolve!-NonLinear1(ode, y, x)) then
%%          return solution
%%       else
%%          write "***** ODESolve cannot solve this ODE!"
%%    end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global '(ODESolve_Before_Non1Grad_Hook ODESolve_After_Non1Grad_Hook)$

algebraic procedure ODESolve!-NonLinear1(ode, y, x);
   %% Top-level solver for non-linear first-order ODEs.
   begin scalar odecoeffs, gradient, solution, p, ode_p;
      traceode1 "Entering top-level non-linear first-order solver ...";
      %% traceode "This is a nonlinear ODE of order 1.";
      p := gensym();
      ode_p := num sub(df(y,x) = p, ode);
      %% If ode contains exponentials then the above sub can produce a
      %% denominator when there is none in ode!
      odecoeffs := coeff(ode_p, p);
      if length odecoeffs = 2 and not smember(p, odecoeffs)
      then <<                           % first DEGREE ODE
         gradient := -first odecoeffs / second odecoeffs;
         symbolic if (solution := or(
            ODESolve!-Run!-Hook(
               'ODESolve_Before_Non1Grad_Hook, {gradient, y, x}),
            ODESolve!-Separable(gradient, y, x),
            ODESolve!-QuasiSeparable(gradient, y, x),
            ODESolve!-Homogeneous(gradient, y, x),
            ODESolve!-QuasiHomog(gradient, y, x),
            ODESolve!-Bernoulli(gradient, y, x),
            ODESolve!-Riccati(gradient, y, x),
            ODESolve!-Run!-Hook(
               'ODESolve_After_Non1Grad_Hook, {gradient, y, x})))
         then return solution
      >>;
      %% If ode degree neq 1 or above solvers fail then ...

      %% A first-order ode is "solvable-for-y" if it can be put into
      %% the form y = f(x,p) where p = y'.  This form includes
      %% Clairaut and Lagrange equations as special cases.  The
      %% Lagrange form is y = xF(y') + G(y').  If F(p) = p then this
      %% is a Clairaut equation.  It is "solvable-for-x" if it can be
      %% put into the form x = f(y,p).
      if (solution := ODESolve!-Clairaut(ode, ode_p, p, y, x)) then
         return solution;
      %% Avoid infinite loops:
      symbolic if !*odesolve!-solvable!-xy then return;
      symbolic(!*odesolve!-solvable!-xy := t);
      %% "Solvable for y" includes Lagrange as a special case:
      symbolic return
         ODESolve!-Solvable!-y(ode_p, p, y, x) or
         ODESolve!-Solvable!-x(ode_p, p, y, x)
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Support routines

algebraic procedure ODENon!-Linear1(ode, y, x);
   %% Solve the linear ODE:  dy/dx = gradient(x,y) = P(x)*y + Q(x)
   %% Split into 2 procedures by FJW.
   begin scalar gradient;
      gradient := coeff(num ode, df(y,x));   % {low, high}
      gradient := -first gradient/second gradient;
      traceode!* "This is a first-order linear ODE solved by ";
      return if smember(y, gradient) then
      begin scalar P, Q;
         traceode "the integrating factor method.";
         P := lcof(num gradient,y)/den gradient;
         Q := gradient - P*y;
         return { y = ODENon!-Linear1PQ(P, Q, x) }
      end
      else <<
         traceode "quadrature.";
         % FJW: Optionally turn off final integration:
         { y = ODESolve!-Int(gradient, x) + newarbconst() }
      >>
   end$

algebraic procedure ODENon!-Linear1PQ(P, Q, x);
   %% Solve the linear ODE:  dy/dx = P(x)*y + Q(x)
   %% Called directly by ODESolve!-Bernoulli
   begin scalar intfactor, !*combinelogs;
      %% intfactor simplifies better if logs in the integral are
      %% combined:
      symbolic(!*combinelogs := t);
      intfactor := exp(int(-P, x));
      %% Optionally turn off final integration:
      return (newarbconst() + ODESolve!-Int(intfactor*Q,x))/intfactor
   end$

%% algebraic procedure unfactorize factorlist;
%%    %% Multiply out a factor list of the form
%%    %% { {factor, multiplicity}, ... }:
%%    for each fac in factorlist product first fac^second fac$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Separable ODEs

algebraic procedure ODESolve!-Separable(gradient, y, x);
   %% The ODE has the form dy/dx = gradient = F(x,y).  If F(x,y) =
   %% f(x)g(y) then the ODE is separable, in which case return the
   %% solution; otherwise return nil.
   begin scalar f, g, !*redefmsg;
      traceode1 "Testing for a separable ODE ...";

      %% Handle implicit dependence on x (ignoring that via y):
      symbolic (<< depend1(y, x, nil);
         %% Hack sub to handle implicit dependences:
         copyd('ODESolve!-old!-subsublis, 'subsublis);
         copyd('subsublis, 'ODESolve!-subsublis);
         g := errorset!*(
            {'ODESolve!-Separable1, mkquote gradient, mkquote x}, nil);
         copyd('subsublis, 'ODESolve!-old!-subsublis);
         if errorp g then RedErr {"(in ODESolve!-Separable1)", emsg!*};
         g := car g;
         if depends(g, x) then g := nil;
      >> where depl!* = depl!*);
      if not g then return;

      %% Then F(alpha,y) = f(alpha)g(y), and F(x,y)/F(alpha,y) =
      %% f(x)/f(alpha) is free of y:
      if depends(f := gradient/g, y) then return;
      traceode "It is separable.";
      %% Any separation of F(x,y) will suffice!
      %% gradient := int(1/g, y) - int(f, x) + newarbconst();
      %% Try to optimize structure of solution to avoid a likely
      %% logarithm of a function of y:
      gradient := int(1/g, y);
      gradient := if part(gradient,0) = log then
         part(gradient,1) - newarbconst()*exp(int(f, x))
      else gradient - int(f, x) + newarbconst();
      return { num gradient = 0 }
   end$

algebraic procedure ODESolve!-Separable1(gradient, x);
   %% Find a small constant alpha such that F(alpha,y) exists and
   %% F(alpha,y) neq 0, and return F(alpha,y), where F = gradient:
   begin scalar numer, denom, alpha, d, n;
      numer := num gradient;
      denom := den gradient;
      alpha := 0;
      while (d:=sub(x=alpha,denom)) = 0 or
         (n:=sub(x=alpha,numer)) = 0 do
            alpha := alpha + 1;
      return n/d
   end$

symbolic procedure ODESolve!-subsublis(u,v);
   % NOTE: This definition assumes that with the exception of *SQ and
   % domain elements, expressions do not contain dotted pairs.

   % This is the standard `subsublis' plus support for one level of
   % indirect dependence (cf. freeof), in which case sub converts the
   % dependent variable into an independent variable with a different
   % but related name.

   % u is an alist of substitutions, v is an expression to be
   % substituted:
   begin scalar x;
      return if x := assoc(v,u) then cdr x
              % allow for case of sub(sqrt 2=s2,atan sqrt 2).
              else if eqcar(v,'sqrt)
                 and (x := assoc(list('expt,cadr v,'(quotient 1 2)),u))
               then cdr x
              else if atom v then
                 if (x := assoc(v,depl!*)) and % FJW
                    %% Run through variables on which v depends:
                    << while (x := cdr x) and not assoc(car x,u) do; x >> % FJW
                 then mkid(v, '!!) else % FJW
                 v
              else if not idp car v
               then for each j in v collect ODESolve!-subsublis(u,j)
              else if x := get(car v,'subfunc) then apply2(x,u,v)
              else if get(car v,'dname) then v
              else if car v eq '!*sq then ODESolve!-subsublis(u,prepsq cadr v)
              else for each j in v collect ODESolve!-subsublis(u,j)
   end$

algebraic procedure ODESolve!-QuasiSeparable(gradient, y, x);
   %% The ODE has the form dy/dx = gradient = F(x,y).  If F(x,y) =
   %% f(y+kx) then the ODE is quasi-separable, in which case return
   %% the solution; otherwise return nil.
   begin scalar k;
      traceode1 "Testing for a quasi-separable ODE ...";
      %% F(x,y) = f(y+kx) iff df(F,x)/df(F,y) = k = constant (using
      %% PARTIAL derivatives):
      k := (df(gradient,x)/df(gradient,y) where df(y,x) => 0);
      if depends(k, x) then return;     % sufficient since y = y(x)
      traceode "It is separable after letting ", y+k*x => y;
      %% Setting u = y+kx gives du/dx = dy/dx+k = f(u)+k:
      gradient := sub(x=0, gradient) + k;
      %% => int(1/(f(u)+k),u) = x + arbconst.
      gradient := sub(y=y+k*x, int(1/gradient, y)) - x + newarbconst();
      return { num gradient = 0 }
   end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Algebraically homogeneous ODEs

algebraic procedure ODESolve!-Homogeneous(gradient, y, x);
   %% The ODE has the form dy/dx = gradient = F(x,y).  If F(x,y) =
   %% f(y/x) then the ODE is algebraically homogeneous.
   %% Setting y = vx => v + x dv/dx = F(x,vx) = f(v)
   begin scalar v;  v := gensym();
      traceode1 "Testing for a homogeneous ODE ...";
      gradient := sub(y=v*x, gradient);
      if depends(gradient, x) then return;
      traceode
         "It is of algebraically homogeneous type ",
         "solved by a change of variables of the form `y = vx'.";
      %% Integrate to give  int(1/(f(v)-v),v) = int(1/x, x) + arbconst
      %% Hence  exp int(1/(f(v)-v),v) = arbconst * x
      gradient := exp(int(1/(gradient-v), v));
      gradient := (gradient where (sqrt ~a)*(sqrt ~b) => sqrt(a*b));
      gradient := sub(v=y/x, x/gradient) + newarbconst();
      return {num gradient = 0}
   end$

%% A quasi-homogeneous first-order nonlinear ODE has the form

%% dy/dx = F(..., ((a1*x + b1*y + c1)/(a2*x + b2*y + c2))^p, ...)

%% where F is an arbitrary composition of functions and p is an
%% arbitrary power.  F may have other arguments that do not depend on
%% x or y or that depend in the same way (e.g. F may be a sum or
%% product).  The argument of F that depends on x or y will be a
%% quotient of expanded polynomials if p is a positive integer.
%% Otherwise, it will be a power of a quotient (expt (quotient ... )),
%% in which case the `expt' can be treated as part of the composition
%% F, or a quotient of powers (quotient (expt ... ) (expt ... )) which
%% must be treated separately.

algebraic procedure ODESolve!-QuasiHomog(gradient, y, x);
   %% The ODE has the form dy/dx = gradient = F(x,y).  If F(x,y) =
   %% f((a1*x + b1*y + c1)/(a2*x + b2*y + c2)) where the function f
   %% may be arbitrary then the ODE is reducible to algebraically
   %% homogeneous.  Find the first linear-rational sub-expression, and
   %% use it to try to make the ODE homogeneous:
   begin scalar tmp, n, d, soln;
      traceode1 "Testing for a quasi-homogeneous ODE ...";
      %% First, find an "argument" that is a rational function, with
      %% numerator and denominator that both depend on x.
      if not(tmp := symbolic ODESolve!-QuasiHomog1(reval gradient, x))
      then return;
      n := num tmp;  d := den tmp;
      %% Now check that numerator and denominator have the same degree
      %% in x (and y):
      if (tmp := deg(n,x)) neq deg(d,x) then return;
      %% If that degree > 1 then extract the squarefree factor:
      if tmp = 1 then                % => deg(d,x) = 1
         %% ( if deg(n,y) neq 1 or deg(d,y) neq 1 then return )
      else <<
         %% Use partial squarefree factorization to find p'th root of
         %% numerator and denominator:
         %%   If f = poly = (a x + b y + c)^p
         %%   then f' = a p(a x + b y + c)^(p-1)
         %%   => gcd(f, f') = (a x + b y + c)^(p-1)
         %%   => f / gcd(f, f') = a x + b y + c.
         n := n / gcd(n, df(n, x));     % must be linear in x and y
         %% if deg(n,x) neq 1 or deg(n,y) neq 1 then return;
         d := d / gcd(d, df(d, x));     % must be linear in x and y
         %% if deg(d,x) neq 1 or deg(d,y) neq 1 then return
      >>;
      %% Check that numerator and denominator are really LINEAR
      %% POLYNOMIALS in x and y (where y depends on x):
      if length(tmp := coeff(n, y)) neq 2 or depends(tmp, y) then return;
      if depends(second tmp, x) or      % coeff of y^1
         length(tmp := coeff(first tmp, x)) neq 2 or % coeff of y^0
         depends(tmp, x) then return;
      if length(tmp := coeff(d, y)) neq 2 or depends(tmp, y) then return;
      if depends(second tmp, x) or      % coeff of y^1
         length(tmp := coeff(first tmp, x)) neq 2 or % coeff of y^0
         depends(tmp, x) then return;
      %% The degenerate case a1*b2=a2*b1 is now treated as quasi-separable.
      traceode "It is quasi-homogeneous if ",
         "the result of shifting the origin is homogeneous ...";
      soln := first solve({n,d},{x,y});
      n := rhs first soln;  d := rhs second soln;
      gradient := sub(x=x+n, y=y+d, gradient);
      %% ODE was quasi-homogeneous iff the new ODE is homogeneous:
      if (soln := ODESolve!-Homogeneous(gradient,y,x)) then
         return sub(x=x-n, y=y-d, soln);
      traceode "... which it is not!"
   end$

%%% The calls to `depends' below are inefficient!

symbolic procedure ODESolve!-QuasiHomog1(u, x);
   %% Assumes "algebraic" form!  Get the first argument of any
   %% composition of functions that is a quotient of polynomials or
   %% symbolic powers (expt forms) that both depend on `x'.
   if atom u then nil                   % not QH, else operator ...
   else if car u eq 'quotient and       % quotient, in which
      depends(cadr u, x) and            % numerator depends on x, and
      depends(caddr u, x) then          % denominator depends on x.
         if eqcar(cadr u, 'expt) then   % symbolic powers
            ( if eqcar(caddr u, 'expt) and (caddr cadr u eq caddr caddr u)
            then {'quotient, cadr cadr u, cadr caddr u} )
         else
            ( if eqcar(cadr u, 'plus) and eqcar(caddr u, 'plus)
            then u )                    % polynomials (?)
   else  % Process first x-dependent argument of operator u:
   begin
   a: if (u := cdr u) then
         if depends(car u, x) then return ODESolve!-QuasiHomog1(car u, x)
         else go to a
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Bernoulli ODEs

symbolic operator ODESolve!-Bernoulli$

symbolic procedure ODESolve!-Bernoulli(rhs, y, x);
   %% The ODE has the form df(y,x) = rhs.  If rhs has the Bernoulli
   %% form P(x)*y + Q(x)*y^n then extract P(x), Q(x), n and return the
   %% solution else return nil.
   ( begin scalar num_rhs, den_rhs, C1, C2, d, d1, d2, d3, P, Q, n;
      traceode1 "Testing for a Bernoulli ODE ...";
      %% Degrees will be constructed in true prefix form.
      %% Need sum of two terms, both with main var (essentially) y.
      depend1(x, y, nil) where !*msg = nil;
      << updkorder y;  rhs := simp rhs >> where kord!* = kord!*;
      num_rhs := numr rhs;  den_rhs := denr rhs;
      if domainp num_rhs or not(mvar num_rhs eq y) then return;

      %% Num may have the form y^d * (y^d1 C1(x) + y^d2 C2(x)) ...
      if null red num_rhs then <<
         d := ldeg num_rhs;  num_rhs := lc num_rhs;
         if domainp num_rhs then return
      >>;

      %% Now num must have the form y^d1 C1(x) + y^d2 C2(x),
      %% where d1 > d2 and d2 = 0 is allowed (if d <> 0 or d3 <> 0).
      if (C1 := get!!y!^n!*C(num_rhs, y)) then
         << d1 := car C1;  C1 := cdr C1 >>
      else return;
      num_rhs := red num_rhs;
      %% Allow d2 = 0 => num_rhs freeof y
      if not smember(y, num_rhs) then
         << d2 := 0;  C2 := num_rhs >>
      else if red num_rhs then return
      else if (C2 := get!!y!^n!*C(num_rhs, y)) then
         << d2 := car C2;  C2 := cdr C2 >>
      else return;

      %% Den must have the form C3(x) or y^d3 C3(x).
      %% In the latter case, combine the powers of y.
      if smember(y, den_rhs) then
         if null red den_rhs and
            (den_rhs := get!!y!^n!*C(den_rhs, y)) then <<
            d3 := car den_rhs;  den_rhs := cdr den_rhs;
            d1 := {'difference, d1, d3};
            d2 := {'difference, d2, d3}
         >> else return;

      %% Simplify the degrees of y and find which is 1:
      if d then << d1 := {'plus, d1, d};  d2 := {'plus, d2, d} >>;
      d1 := aeval d1;  d2 := aeval d2;
      if d1 = 1 then << P := C1; Q := C2; n := d2 >>
      else if d2 = 1 then << P := C2; Q := C1; n := d1 >>
      else return;
      %% A final check that P, Q, n are valid:
      if Bernoulli!-depend!-check(P, y) or
         Bernoulli!-depend!-check(Q, y) or
            Bernoulli!-depend!-check(den_rhs, y) or
               Bernoulli!-depend!-check(n, x) then return;
      %% ( Last test implies Bernoulli!-depend!-check(n, y). )
      %% For testing:
      %% return {'list, mk!*sq(P ./ den_rhs), mk!*sq(Q ./ den_rhs), n};
      P := mk!*sq(P ./ den_rhs);  Q := mk!*sq(Q ./ den_rhs);
      return ODESolve!-Bernoulli1(P, Q, y, x, n)
   end ) where depl!* = depl!*$

symbolic procedure Bernoulli!-depend!-check(f, xy);
   %% f is a standard form, xy is an identifier (kernel).
   if numr difff(f, xy) then <<         % neq 0 (nil)
      traceode "It is not of Bernoulli type because ...";
      MsgPri(nil, !*f2a f, "depends (possibly implicitly) on",
         get(xy, 'odesolve!-depvar) or xy, nil);
      %% (y might be gensym -- 'odesolve!-depvar set in odeintfc)
      t
   >>$

symbolic procedure get!!y!^n!*C(u, y);
   %% Assume that u is a standard form representation of y^n * C(x)
   %% with y the leading kernel.  Return (n . C) or nil
   begin scalar n, C;
      if mvar u eq y then <<            % ?? y^n * C(x), n nonnegint
         n := ldeg u;  C := lc u;
         return if not domainp C and smember(y, mvar C) then
            ( if (C := get!!y!^n!*C1(C, y)) then
               % y^n * y^n1 * C(x), n nonnegint
               {'plus, n, car C} . cdr C )
         else n . C
      >> else                           % (y^n1)^n * C(x), n nonnegint
         return get!!y!^n!*C1(u, y)
   end$

symbolic procedure get!!y!^n!*C1(u, y);
   % u = (y^n1)^n * C(x), n nonnegint
   begin scalar n, C;
      n := mvar u;
      if not(eqcar(n, 'expt) and cadr n eq y) then return;
      n := {'times, caddr n, ldeg u};
      C := lc u;
      return n . C
   end$

algebraic procedure ODESolve!-Bernoulli1(P, Q, y, x, n);
   begin scalar !*odesolve_noint;  % Force integration?
      traceode "It is of Bernoulli type.";
      n := 1 - n;
      return if symbolic !*odesolve_explicit then
         { y = ODENon!-Linear1PQ(n*P, n*Q, x)^(1/n)*
               newroot_of_unity(n) }    % explicit form
      else { y^n = ODENon!-Linear1PQ(n*P, n*Q, x) } % implicit form
   end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Riccati ODEs

algebraic procedure ODESolve!-Riccati(rhs, y, x);
   %% The ODE has the form df(y,x) = rhs.  If rhs has the Riccati form
   %% a(x)y^2 + b(x)y + c(x) then transform to a reduced linear
   %% second-order ODE and attempt to solve it.
   symbolic if not !*odesolve_fast then % heuristic solution
   algebraic begin scalar a, b, c, soln, !*ratarg;
      traceode1 "Testing for a Riccati ODE ...";
      %% rhs may have a denominator that depends on y, so ...
      symbolic(!*ratarg := t);
      c := coeff(rhs, y);
      if length c neq 3 or depends(c, y) then return;
      a := third c;  b := second c;  c := first c;
      c := a*c;  b := -(df(a,x)/a + b);
      traceode "It is of Riccati type ",
         "and transforms into the linear second-order ODE: ",
         num(df(y,x,2) + b*df(y,x) + c*y) = 0;
      soln := {c, b, 1};                % low .. high
      soln := ODESolve!-linear!-basis(soln, 0, y, x, 2,
         if c then 0 else if b then 1 else 2); % min_order
      if not soln then <<
         traceode "But ODESolve cannot solve it!";
         return
      >>;
      %% The solution of the linear second-order ode must have the
      %% form y = arbconst(1)*y1 + arbconst(2)*y2, in which only the
      %% ratio of the arbconst's is relevant here, so ...
      soln := first soln;
      soln := newarbconst()*first soln + second soln;
      return {y = sub(y = soln, -df(y,x)/(a*y))}
      %% BEWARE: above minus sign missing in Zwillinger, first edn.
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% ODEs that are "solvable for y or x", including Clairaut and Lagrange
% as special cases.

algebraic procedure ODESolve!-Clairaut(ode, ode_p, p, y, x);
   %% Assuming that ode is first order, determine whether it is of
   %% Clairaut type f(xy'-y) = g(y'), and if so return its general
   %% solution together with a singular solution if one exists.
   begin scalar sing_soln;
      traceode1 "Testing for a Clairaut ODE ...";
      sing_soln := sub(y = x*p, ode_p);
      if depends(sing_soln, x) or depends(sing_soln, y) then
         return;                        % Not Clairaut
      traceode "It is of Clairaut type.";
      %% Look for a singular solution:
      sing_soln := solve(df(ode, x), df(y,x));
      sing_soln := for each soln in sing_soln join
         %% NB: Cannot use algebraic code to check soln because it may
         %% contain derivatives that evaluate to 0.
         if symbolic eqcar(caddr soln, 'root_of)
         then {} else {num sub(soln, ode) = 0};
      return (sub(p = newarbconst(), ode_p) = 0) . sing_soln
   end$

symbolic operator ODESolve!-Solvable!-y$
symbolic procedure ODESolve!-Solvable!-y(ode_p, p, y, x);
   %% ode_p has the form f(x,y,p) where p = y'.
   ( algebraic begin scalar c, lagrange, ode_y, ode_x;
      traceode1 "Testing for a ""solvable for y"" or Lagrange ODE ...";
      %% Is the ODE "solvable for y"?
      c := coeff(ode_p, y);
      if length c neq 2 or depends(c, y) then return;
      ode_y := -first c / second c;
      %% ode_y has the form f(x,p) where p = y' and ode is y = ode_y.
      %% If f(x,p) = xF(p) + G(p) then ode is a Lagrange equation.
      if not depends(den ode_y, x) and
         length(c:=coeff(num ode_y, x)) = 2 and
         not depends(c, x) then
         lagrange := 1;                 % Lagrange form
      symbolic depend1(p, x, t);
      ode_x := num(p - df(ode_y, x));
      if lagrange then <<
         %% ODE is a Lagrange equation, hence ode_x is a LINEAR ODE
         %% for x(p) that can be solved EXPLICITLY (using an
         %% integrating factor) for a single value of x.
         symbolic depend1(x, p, t);
         ode_x := num(ode_x where df(p,x) => 1/df(x,p));
         symbolic depend1(p, x, nil);
         traceode "It is of Lagrange type and reduces to this ",
            "subsidiary ODE for x(y'): ",
            ode_x = 0;
         ode_x := ODENon!-Linear1(ode_x, x, p)
      >> else if symbolic !*odesolve_fast then return
         traceode "Sub-solver terminated: fast mode, no heuristics!"
      else <<
         %% ode_x is an arbitrary first-order ODE for p(x), so ...
         traceode "It is ""solvable for y"" and reduces ",
            "to this subsidiary ODE for y'(x):";
         ode_x := ODESolve!-FirstOrder(ode_x, p, x);
         if not ode_x then <<
            traceode "But ODESolve cannot solve it!";
            return
         >>
      >>;
      traceode "The subsidiary solution is ", ode_x,
         " and the main ODE can be solved parametrically ",
         "in terms of the derivative.";
      return if symbolic(!*odesolve_implicit or !*odesolve_explicit) then
         %% Try to eliminate p between ode_y and ode_x else fail.
         %% Assume that the interface code will try to actually solve
         %% this for y:
         Odesolve!-Elim!-Param(ode_y, y, ode_x, p, y)
      else
         %% Return a parametric solution {y(p), x(p), p}:
         if lagrange then            % soln explicit for x
            for each soln in ode_x collect ODESolve!-Simp!-ArbParam
               sub(p=newarbparam(), {y = sub(soln, ode_y), soln, p})
         else
            for each soln in ode_x join <<
               %% Make solution as explicit as possible:
               soln := solve(soln, x);
               for each s in soln collect ODESolve!-Simp!-ArbParam
                  sub(p=newarbparam(),
                     if symbolic eqcar(caddr s, 'root_of) then
                        {y=ode_y, sub(part(rhs s, 2)=x, part(rhs s, 1)), p}
                     else {y=sub(s, ode_y), s, p})
            >>
   end ) where depl!* = depl!*$


symbolic operator ODESolve!-Solvable!-x$
symbolic procedure ODESolve!-Solvable!-x(ode_p, p, y, x);
   %% ode_p has the form f(x,y,p) where p = y'.
   not !*odesolve_fast and              % heuristic solution
   ( algebraic begin scalar c, ode_x, ode_y;
      traceode1 "Testing for a ""solvable for x"" ODE ...";
      %% Is the ODE "solvable for x"?
      c := coeff(ode_p, x);
      if length c neq 2 or depends(c, x) then return;
      ode_x := -first c / second c;
      %% ode_x has the form f(y,p) where p = y' and ode is x = ode_x.
      symbolic depend1(p, y, t);
      ode_y := num(1/p - df(ode_x, y));
      %% ode_y is an arbitrary first-order ODE for p(y), so ...
      traceode "It is ""solvable for x"" and reduces ",
         "to this subsidiary ODE for y'(y):";
      ode_y := ODESolve!-FirstOrder(ode_y, p, y);
      if not ode_y then <<
         traceode "But ODESolve cannot solve it! ";
         return
      >>;
      traceode "The subsidiary solution is ", ode_y,
         " and the main ODE can be solved parametrically ",
         "in terms of the derivative.";
      return if symbolic(!*odesolve_implicit or !*odesolve_explicit) then
         %% Try to eliminate p between ode_x and ode_y else fail.
         %% Assume that the interface code will try to actually solve
         %% this for y:
         Odesolve!-Elim!-Param(ode_x, x, ode_y, p, y)
      else
         for each soln in ode_y join <<
            %% Return a parametric solution {y(p), x(p), p}:
            %% Make solution as explicit as possible:
            soln := solve(soln, y);
            for each s in soln collect ODESolve!-Simp!-ArbParam
               sub(p=newarbparam(),
                  if symbolic eqcar(caddr s, 'root_of) then
                     {sub(part(rhs s, 2)=y, part(rhs s, 1)), x=ode_x, p}
                  else {s, x=sub(s, ode_x), p})
         >>
   end ) where depl!* = depl!*$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The arbitrary parameters in solutions:

algebraic operator arbparam$
algebraic (!!arbparam := 0)$
algebraic procedure newarbparam();
   arbparam(!!arbparam:=!!arbparam+1)$

algebraic procedure Odesolve!-Elim!-Param(ode_y, y, ode_x, p, depvar);
   %% ode_y is an expression for y and ode_x is an rlist of odesolve
   %% solutions for x, both in terms of a parameter p.  Return a list
   %% of equations corresponding to the equations in ode_x but with p
   %% eliminated with ode_y, or nil if this is not possible.
   %% depvar is the true dependent variable.
   begin scalar soln, result;
      if polynomialp(ode_y := num(y - ode_y), p) then <<
         result := {};
         while ode_x neq {} and
            polynomialp(soln := num !*eqn2a first ode_x, p)
            do <<
               result := resultant(ode_y, soln, p) . result;
               ode_x := rest ode_x
            >>;
         if ode_x = {} then
            return Odesolve!-Tidy!-Implicit(result, y)
      >>;
      ode_y := solve(ode_y, p);
      %% solve here may return a one_of construct (zimmer (4) & (19)).
      %% I don't see why, but ...
      %% (expand_cases not defined until solve loaded.)
      ode_y := expand_cases ode_y;
      if not smember(root_of, ode_y) then <<
         result := for each soln in ode_y join
            for each s in ode_x join
               if rhs s = 0 then  % s is f(x,y) = 0
                  if (s:=sub(soln, num lhs s)) neq 0 then {num s}
                  else {}
               else {num(sub(soln, rhs s) - x)};  % s is x = f(x,y)
         return Odesolve!-Tidy!-Implicit(result, depvar)
      >>;
      traceode "But cannot eliminate parameter ",
         "to make solution explicit."
   end$

algebraic procedure Odesolve!-Tidy!-Implicit(solns, depvar);
   %% Remove repeated and irrelevant factors from implicit solutions.
   for each soln in solns join
      for each fac in factorize soln join
         if smember(depvar, fac:=first fac) then {fac = 0} else {}$

switch odesolve_simp_arbparam$          % DEFAULT OFF.  TEMPORARY?

symbolic operator ODESolve!-Simp!-ArbParam$
symbolic procedure ODESolve!-Simp!-ArbParam u;
   %% Simplify arbparam expressions within parametric solution u
   %% (cf. ODESolve!-Simp!-ArbConsts)
   begin scalar !*precise, x, y, ss_x, ss_y, arbexprns_x, arbexprns_y,
      arbexprns, param;
      if not(rlistp u and length u = 4) then
         TypErr(u, "parametric ODE solution");
      if not !*odesolve_simp_arbparam then return u; % TEMPORARY?
      x := lhs cadr u;  y := lhs caddr u;
      if not(ss_x := ODESolve!-Structr(caddr cadr u, x, y, 'arbparam))
      then return u;
      if not(ss_y := ODESolve!-Structr(caddr caddr u, x, y, 'arbparam))
      then return u;
      ss_x := cdr ss_x;  ss_y := cdr ss_y;
      arbexprns_x := for each s in cdr ss_x collect caddr s;
      arbexprns_y := for each s in cdr ss_y collect caddr s;
      if null(arbexprns := intersection(arbexprns_x, arbexprns_y))
      then return u;
      arbexprns_x := cdr ss_x;  ss_x := car ss_x;
      arbexprns_y := cdr ss_y;  ss_y := car ss_y;
      arbexprns_x := for each s in arbexprns_x join
         if member(caddr s, arbexprns) then {s}
         else << ss_x := subeval{s, ss_x}; nil >>;
      arbexprns_y := for each s in arbexprns_y join
         if member(caddr s, arbexprns) then {s}
         else << ss_y := subeval{s, ss_y}; nil >>;
      traceode "Simplifying the arbparam expressions in ", u,
         " by the rewrites ...";
      param := cadddr u;
      for each s in arbexprns_x do <<
         %% s has the form ansj = "expression in arbparam(n)"
         traceode rhs s => param;
         %% Remove other occurrences of arbparam(n):
         ss_x := algebraic sub(solve(s, param), ss_x);
         %% Finally rename ansj as arbparam(n):
         ss_x := subeval{{'equal, cadr s, param}, ss_x}
      >>;
      for each s in arbexprns_y do <<
         ss_y := algebraic sub(solve(s, param), ss_y);
         ss_y := subeval{{'equal, cadr s, param}, ss_y}
      >>;
      %% Try a further heuristic simplification:
      if smember(param, den ss_x) and smember(param, den ss_y) then
      begin scalar ss_x1, ss_y1;
         ss_x1 := algebraic sub(param = 1/param, ss_x);
         if smember(param, den ss_x1) then return;
         ss_y1 := algebraic sub(param = 1/param, ss_y);
         if smember(param, den ss_y1) then return;
         traceode "Simplifying further by the rewrite ",
            param => 1/param;
         ss_x := ss_x1;  ss_y := ss_y1
      end;
      return makelist {{'equal, x, ss_x}, {'equal, y, ss_y}, param}
   end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic operator Polynomialp$

%% symbolic procedure polynomialp(pol, var);
%%    %% Returns true if numerator of pol is polynomial in var.
%%    %% Assumes on exp, mcd.
%%    begin scalar kord!*;  kord!* := {var};
%%       pol := numr simp!* pol;
%%       while not domainp pol and mvar pol eq var and
%%          (domainp lc pol or not smember(var, mvar lc pol)) do
%%             pol := red pol;
%%       return not smember(var, pol)
%%    end$

symbolic procedure Polynomialp(pol, var);
   %% Returns true if numerator of pol is polynomial in var.
   %% Assumes on exp, mcd.
   Polynomial!-Form!-p(numr simp!* pol, !*a2k var)$

symbolic procedure Polynomial!-Form!-p(sf, y);
   %% A standard form `sf' is polynomial if each of its terms is
   %% polynomial:
   domainp sf or
      (Polynomial!-Term!-p(lt sf, y) and Polynomial!-Form!-p(red sf, y))$

symbolic procedure Polynomial!-Term!-p(st, y);
   %% A standard term `st' is polynomial if either (a) its
   %% leading power is polynomial and its coefficient is free of y, or (b)
   %% its leading power is free of y and its coefficient is polynomial:
   if tvar st eq y then not smember(y, tc st)
   else if not smember(y, tvar st) then Polynomial!-Form!-p(tc st, y)$

endmodule;

end;
