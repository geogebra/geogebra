module odelin$  % Simple linear ODE solver

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


% F.J.Wright@Maths.QMW.ac.uk, Time-stamp: <14 September 2000>

% Based in part on code by Malcolm MacCallum.

% TO DO:

%   Polynomial solutions for polynomial coeffs.

%   Check the distinction between finding a solution technique for an
%   ODE that then fails internally to solve it (e.g. failing to solve
%   an auxiliary equation) and failing to find a solution technique.
%   (Should the former be handled by `odefailure'?)


%% Techniques implemented
%% ======================
%%   First order (integrating factor)
%%   Constant coefficients
%%   Euler and shifted Euler
%%   Exact
%%   Trivial order reduction (dep var and low-order derivs missing)

%%   Second-order special function ODEs (module odespcfn)

%% Notes: Overall factors are handled in most cases by making the ODE
%% "monic".


%% Internal representation
%% =======================

%% A linear ode is represented by its list of coefficient functions
%% (odecoeffs), driver term (driver), and dependent (y) and
%% independent (x) variables.  The maximum (ode_order) and minimum
%% (min_order) derivative orders are included in the representation
%% for efficiency/convenience.  Its solution is represented as a basis
%% for the solution space of the reduced ODE together with a
%% particular integral of the full ODE.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% algebraic procedure odesolve(ode, y, x);
%%    %% Temporary definition for test purposes.
%%    begin scalar !*precise, solution;
%%       ode := num !*eqn2a ode;           % returns ode as expression
%%       if (solution := ODESolve!-linear(ode, y, x)) then
%%          return solution
%%       else
%%          write "***** ODESolve cannot solve this ODE!"
%%    end$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

global '(ODESolve_Before_Lin_Hook ODESolve_After_Lin_Hook)$

algebraic procedure ODESolve!-linear(ode, y, x);
   %% MAIN LINEAR SOLVER
   %% Assumes ODE is an algebraically irreducible "polynomial" expression.
   begin scalar reduced_ode, auxvar, auxeqn, odecoeffs,
         first_arb, solution, driver;
      %% The following decomposition is needed FOR ALL linear ODEs.
      %% The DRIVER is the part of the ODE independent of y, such that
      %% the ODE can be expressed as REDUCED_ODE = DRIVER.
%%       driver := if part(ode, 0) = plus then
%%          -select(~u freeof y, ode) else 0;
      driver := -sub(y=0, ode);
      reduced_ode := ode + driver;
      auxvar := symbolic gensym();
      %% df(y, x, n) => m^n, where m = auxvar
      auxeqn := sub(y=e^(auxvar*x), reduced_ode)/e^(auxvar*x);
      odecoeffs := coeff(auxeqn, auxvar); % low .. high
      traceode "This is a linear ODE of order ", high_pow, ".";
      first_arb := !!arbconst + 1;
      symbolic if not(solution := ODESolve!-linear!-basis
         (odecoeffs, driver, y, x, high_pow, low_pow) or
         (not !*odesolve_fast and
         %% Add a switch to control access to this thread?
         %% It is currently necessary for Zimmer (8).
         << traceode
            "But ODESolve cannot solve it using linear techniques, so ...";
            %% NB: This will probably produce a NONLINEAR ODE!
            %% But, in desperation, try it anyway ...
            (ODESolve!-Interchange(ode, y, x) where !*odesolve_basis = nil)
         >>)) then return;
      %% Return solution as BASIS or LINEAR COMBINATION, assuming a
      %% SINGLE solution since the ODE is linear:
      return if symbolic !*odesolve_basis then % return basis
         if (part(solution, 1, 0) = equal) then
            if lhs first solution = y and % solution is explicit
         (auxeqn := ODESolve!-LinComb2Basis
            (rhs first solution, first_arb, !!arbconst)) then auxeqn
            else << write
               "***** Cannot convert nonlinear combination solution to basis!";
            solution >>
         else solution
      else                              % return linear combination
         if part(solution, 1, 0) = list then
            {y = ODESolve!-Basis2LinComb solution}
         else solution
   end$

algebraic procedure ODESolve!-linear!-basis
   (odecoeffs, driver, y, x, ode_order, min_order);
   %% Always returns the solution in basis format.
   %% Called by ODESolve!-Riccati in odenon1.
   symbolic if ode_order = 1 then
      ODESolve!-linear1(odecoeffs, driver, x)
   else or(
      ODESolve!-Run!-Hook(
         'ODESolve_Before_Lin_Hook,
         {odecoeffs,driver,y,x,ode_order,min_order}),
      ODESolve!-linearn
         (odecoeffs, driver, y, x, ode_order, min_order, nil),
      ODESolve!-Run!-Hook(
         'ODESolve_After_Lin_Hook,
         {odecoeffs,driver,y,x,ode_order,min_order}))$

algebraic procedure ODESolve!-linear!-basis!-recursive
   (odecoeffs, driver, y, x, ode_order, min_order);
   %% Always returns the solution in basis format.
   %% Internal linear solver called recursively.
   symbolic if ode_order = 1 then
      ODESolve!-linear1(odecoeffs, driver, x)
   else
      ODESolve!-linearn
         (odecoeffs, driver, y, x, ode_order, min_order, t)$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Solve a linear first-order ODE by using an integrating factor.
% Based on procedure linear1 from module ode1ord by Malcolm MacCallum.

algebraic procedure ODESolve!-linear1(odecoeffs, driver, x);
   %% Solve the linear ODE reduced_ode = driver, where
   %% reduced_ode = A(x)*(dy/dx + P(x)*y), driver = A(x)*Q(x).
   %% Uses Odesolve!-Int to optionally turn off final integration.
   begin scalar A, P, Q;
      A := second odecoeffs;
      P := first odecoeffs/A;
      Q := driver/A;
      return if P then                  % dy/dx + P(x)*y = Q(x)
         begin scalar intfactor, !*combinelogs;
            traceode "It is solved by the integrating factor method.";
            %% intfactor simplifies better if logs are combined:
            symbolic(!*combinelogs := t);
            P := (P where tan(~x) => sin(x)/cos(x));
            intfactor := exp(int(P, x));
            return if Q then
               { {1/intfactor}, Odesolve!-Int(intfactor*Q,x)/intfactor }
            else { {1/intfactor} }
         end
      else <<                           % dy/dx = Q(x)
         traceode "It is solved by quadrature.";
         if Q then {{1}, Odesolve!-Int(Q, x)} else {{1}}
      >>
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Try to solve a linear ODE of order > 1.
% Based on procedure linearn from module linearn by Malcolm MacCallum.

% If the first integral of an exact ODE has constant coefficients, is
% of Euler type or has trivally reducible order then so does the
% original ODE.  Also, trivial order reduction preserves constant
% coefficients or Euler type, and the reduced ODE is not further
% reducible.  Hence, when the linear ODE solver is called recursively
% to solve a first integral of an exact ODE or a trivially order-
% reduced ODE, the argument `recursive' is used to avoid checking
% again whether it has constant coefficients, is of Euler type or has
% trivially reducible order.

algebraic procedure ODESolve!-linearn
   (odecoeffs, driver, y, x, ode_order, min_order, recursive);
   %% Solve the linear ODE: reduced_ode = driver.
   begin scalar lcoeff, odecoeffs1, driver1, solution;
      %% Make the ODE "monic" as assumed by some solvers:
      %% (Note that this makes algebraic factorization largely
      %% irrelevant!)
      if (lcoeff := part(odecoeffs, ode_order+1)) = 1 then <<
         odecoeffs1 := odecoeffs;
         driver1 := driver
      >> else <<
         odecoeffs1 := for each c in odecoeffs collect c/lcoeff; % low .. high
         %% Could discard last element of odecoeffs1 because it must be 1!
         driver1 := driver/lcoeff
      >>;
      if recursive then goto a;

      %% Test for constant coefficients:
      if odecoeffs1 freeof x then
         return ODESolve!-LCC(odecoeffs1, driver1, x, ode_order);

      traceode "It has non-constant coefficients.";

      %% Test for Euler form:
      if (solution :=
         ODESolve!-Euler(odecoeffs1, driver1, x, ode_order))
      then return solution;

      %% Test for trivial order reduction.  The result cannot have
      %% constant coeffs or Euler form, but it could be first order or
      %% exact or ...
      if min_order neq 0 and ode_order neq min_order and
         %% else would reduce to purely algebraic equation
         (solution := ODELin!-Reduce!-Order
            (odecoeffs, driver, y, x, ode_order, min_order))
      then return solution;

   a: %% Non-trivial solution techniques for recursive calls ...

      %% Test for exact form - try monic then original form:
      if (solution :=
         ODELin!-Exact(odecoeffs1, driver1, y, x, ode_order))
      then return solution;
      if lcoeff neq 1 and (solution :=
         ODELin!-Exact(odecoeffs, driver, y, x, ode_order))
      then return solution;

      %% Add other methods here ...
      %% Null return implies failure.

      %% FINALLY, test for a second-order special-function equation:
      if ode_order = 2 and
         (solution := ODESolve!-Specfn(odecoeffs1, driver1, x))
      then return solution
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Convert between basis and linear combination formats

% Solution basis output format:  { {B1, B2, ...}, PI }
% where {Bi} is a basis for the reduced ODE and PI is a particular
% intergral for the full ODE, which may be absent.
% This corresponds to the linear-combination output format
% y = ( for each B in {B1, B2, ...} sum newarbconst()*B ) + PI.

% This agrees with Maple, e.g. Maple V Release 5 gives
% > dsolve(diff(y(x),x) + y(x) = x, output=basis);
%                          [[exp(-x)], -1 + x]
% > dsolve(diff(y(x),x) + y(x) = x);
%                       y(x) = x - 1 + exp(-x) _C1

algebraic procedure ODESolve!-Basis2LinComb solution;
   %% Convert basis { {B1, B2, ...}, PI } to linear combination:
   begin scalar lincomb;
      lincomb := for each B in first solution sum <<newarbconst()>>*B;
      %% << >> above is NECESSARY to force immediate evaluation!
      if length solution > 1 then
         lincomb := lincomb + second solution;
      return lincomb
   end$

algebraic procedure ODESolve!-LinComb2Basis
      (lincomb, first_arb, last_arb);
   %% Convert linear combination to basis { {B1, B2, ...}, PI }:
   ODESolve!-LinComb2Basis1({}, lincomb, first_arb, last_arb)$

algebraic procedure ODESolve!-LinComb2Basis1
      (basis, lincomb, first_arb, last_arb);
   %% `basis' is a LIST of independent reduced_ode solutions.
   %% Algorithm is to recursively move components from lincomb to
   %% basis.
   begin scalar coeffs, C;  C := arbconst last_arb;
      coeffs := coeff(lincomb, C);
      if high_pow > 1 or smember(C, coeffs) then
         return                         % cannot convert
      else if high_pow = 1 then <<
         basis := second coeffs . basis;
         lincomb := first coeffs
      >>;
      %% else independent of this arbconst
      return if first_arb >= last_arb then
         { basis, lincomb }
      else
         ODESolve!-LinComb2Basis1
            (basis, lincomb, first_arb, last_arb-1)
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Solve a linear, constant-coefficient ODE.
% Based on code by Malcolm MacCallum.

% There are (at least) 4 ways to get the particular integral (P.I.)
% for a given driving term on the right of the equation:

% 1. The method of undetermined coefficients: this is similar to the
% integrator in that for a given driving term one has to find the
% functional form of the P.I. and then solve for the numerical
% coefficients in it.  Making it really general is as big a task as
% rewriting the integrator.

% 2. The method of variation of parameters: this expands the P.I. as a
% sum of functions of `x' times the linearly independent solutions in
% the complementary function (C.F.).

% 3. Factorise the linear operator (done anyway for the C.F.) and then
% apply for each root `m' the operation
%   ans := exp(m*x) * int(ans * exp(-m*x))
% This is a form of the "D-operator method".  N.B. Some `m' are
% complex.

% 4. Use Laplace transforms (and some kind of table lookup for the
% inverse transforms).

% The current implementation first tries to use the "D-operator
% method", but as soon as any integral fails to evaluate it switches
% to "variation of parameters".

algebraic procedure ODESolve!-LCC(odecoeffs, driver, x, ode_order);
   % Returns a solution basis or nil (if it fails).
   begin scalar auxvar, auxeqn, i, auxroots, solutions, PI;
      traceode "It has constant coefficients.";
      %% TEMPORARY HACK -- REBUILD AUXEQN:
      auxvar := symbolic gensym();  i := -1;
      auxeqn := for each c in odecoeffs sum c*auxvar^(i:=i+1);
      % First we solve for the auxiliary roots:
      auxroots := solve(auxeqn, auxvar);
      % and check the solution carefully:
      if ode_order neq
         (for each multi in multiplicities!* sum multi) then return
            traceode "But insufficient roots of auxiliary equation!";
      solutions := auxroots;
   a: if lhs first solutions neq auxvar then return
         traceode "But auxiliary equation could not be solved!";
      if (solutions := rest solutions) neq {} then goto a;
      % Now we find the complementary solution:
      solutions := ODESolve!-LCC!-CompSoln(auxroots, x);
      % Next the particular integral:
      if driver = 0 then return { solutions };
      if not (PI := ODESolve!-LCC!-PI(auxroots, driver, x)) then
         %% (Cannot use `or' as an algebraic operator!)
         PI := ODESolve!-PI(solutions, driver, x);
      return { solutions, PI }
   end$

algebraic procedure ODESolve!-LCC!-CompSoln(auxroots, x);
   %% Construct the complimentary solution (functions) from the roots
   %% of the auxiliary equation for a linear ODE with constant
   %% coefficients.  Pairs of complex conjugate roots are converted to
   %% real trigonometric form up to the minimum of their
   %% multiplicities (regardless of complex switch and parameters).
   %% `auxroots' is a list of equations with a temporary variable on
   %% the left and an auxilliary root on the right.  The root
   %% multiplicities are stored as a list in the global variable
   %% `multiplicities!*'.  `x' is the independent variable.
   begin scalar multilist, crootlist, ans, multi, imroot, exppart;
      %% crootlist will be a list of lists of the form
      %% {unpaired_complex_root, multiplicity}.
      multilist := multiplicities!*;
      crootlist := {};  ans := {};
      for each root in auxroots do <<
         root := rhs root;
         multi := first multilist;  multilist := rest multilist;
         % Test for complex roots:
         imroot := impart!* root;
         if imroot = 0 then <<
            exppart := exp(root*x);
            for j := 1 : multi do ans := (x**(j-1)*exppart) . ans
         >> else
         begin scalar conjroot, conjmulti;
            %% Cannot assume anything about the order of the roots in
            %% auxroots, so build a list of the complex roots found to
            %% avoid using complex conjugate pairs twice.
            conjroot := conj!* root;  conjmulti := 0;
            %% Essentially do assoc followed by delete if found:
            crootlist := for each root in crootlist join
               if first root = conjroot then <<
                  conjmulti := second root; {}
               >> else {root};
            if conjmulti then           % conjugate pair found:
            begin scalar minmulti;
               exppart := exp(repart!* root*x);
               minmulti := min(multi, conjmulti);
               imroot := abs imroot;    % to avoid spurious minus sign
               imroot := (imroot where abs ~x => x); % to avoid spurious abs!
               for j := 1 : minmulti do
                  ans := (x**(j-1)*cos(imroot*x)*exppart) .
                     (x**(j-1)*sin(imroot*x)*exppart) . ans;
               if multi neq conjmulti then <<
                  %% Skip this unlikely case if possible
                  minmulti := minmulti + 1;
                  exppart := exp(root*x);
                  for j := minmulti : multi do
                     ans := (x**(j-1)*exppart) . ans;
                  exppart := exp(conjroot*x);
                  for j := minmulti : conjmulti do
                     ans := (x**(j-1)*exppart) . ans
               >>
            end
            else crootlist := {root, multi} . crootlist
         end
      >>;
      %% Finally include unpaired complex roots:
      for each root in crootlist do <<
         exppart := exp(first root*x);
         multi := second root;
         for j := 1 : multi do ans := (x**(j-1)*exppart) . ans
      >>;
      return ans
   end$

% The following procedures process complex-valued expressions with
% regard only to their EXPLICIT complexity, i.e. assuming that all
% symbolic quantities are pure real.  They need to work with the
% complex switch both on and off, which is slightly tricky!

algebraic(vars!-are!-real := {repart ~x => x, impart ~x => 0})$

algebraic procedure repart!* u;
   << u := repart u;  u where vars!-are!-real >>$

algebraic procedure impart!* u;
   << u := impart u;  u where vars!-are!-real >>$

algebraic procedure conj!* u;
   << u := conj u;  u where vars!-are!-real >>$

algebraic procedure ODESolve!-LCC!-PI(auxroots, driver, x);
   % Try to construct a particular integral using the `D-operator
   % method'.  Factorise the linear operator (done anyway for the
   % C.F.) and then apply for each root m the operation
   % ans := exp(m*x) * int(ans * exp(-m*x));
   % N.B. Some m may be complex.
   % See e.g. Stephenson, section 21.8, p 410.
   % Returns nil if any integral cannot be evaluated.
   begin scalar exp_mx, multiplicities, multi;
      traceode
         "Constructing particular integral using `D-operator method'.";
      multiplicities := multiplicities!*;
      while driver and auxroots neq {} do <<
         exp_mx := exp((rhs first auxroots)*x);
         driver := driver/exp_mx;
         multi := first multiplicities;
         while driver and multi >= 1 do <<
            driver := int(driver, x);
            if driver freeof int then multi := multi - 1 else driver := 0
         >>;
         driver := exp_mx*driver;
         auxroots := rest auxroots;
         multiplicities := rest multiplicities
      >>;
      if driver = 0 then
         traceode "But cannot evaluate the integrals, so ..."
      else return driver
   end$

algebraic procedure ODESolve!-PI(solutions, R, x);
   % Given a "monic" forced linear nth-order ODE

   %   y^(n) + a_(n-1)(x)y^(n-1) + ... + a_1(x)y = R(x)

   % and a set of n linearly independent solutions {yi(x)} to the
   % unforced ODE, construct a particular solution of the forced ODE
   % in the form of a (single) integral representation by the method
   % of variation of parameters.
   begin scalar n;
      traceode
         "Constructing particular integral using `variation of parameters'.";
      return
         if (n := length solutions) = 2 then
         begin scalar y1, y2, W;
            y1 := first solutions;  y2 := second solutions;
            %% The Wronskian, kept separate to facilitate tracing:
            W := trigsimp(y1*df(y2, x) - y2*df(y1, x));
            traceode "The Wronskian is ", W;
            R := R/W;
            return -ode!-int(y2*R, x)*y1 + ode!-int(y1*R, x)*y2
         end
         else
         begin scalar Wmat, ys, W, i;
            %% Construct the (square) Wronskian matrix of the solutions:
            Wmat := {ys := solutions};
            for i := 2 : n do
               Wmat := (ys := for each y in ys collect df(y,x)) . Wmat;
            load_package matrix;              % to define mat
            Wmat := list2mat reverse Wmat;
            %% The Wronskian (determinant), kept separate for tracing:
            W := trigsimp det Wmat;
            traceode "The Wronskian is ", W;
            R := R/W;  i := 0;
            return
               for each y in solutions sum
                  ode!-int(cofactor(Wmat, n, i:=i+1)*R, x) * y
         end
   end$

% This facility should be in the standard matrix package!
symbolic operator list2mat$
symbolic procedure list2mat M;
   % Input:  (list (list A B ...) (list C D ...) ...)
   % Output: (mat  (A B ...) (C D ...) ...)
   'mat . for each row in cdr M collect cdr row$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Special cases of non-constant coefficients:

algebraic procedure ODESolve!-Euler(odecoeffs, driver, x, ode_order);
   %% Solve a (MONIC) ODE having (essentially) the form
   %% reduced_ode = x^n df(y,x,n) + ... + a_{n-1} x df(y,x) + a_n y = driver
   %% odecoeffs = {a_n, a_{n-1} x, ..., a_0 x^n} / (a_0 x^n)
   %%           = {a_n/(a_0 x^n), a_{n-1}/(a_0 x^{n-1}), ..., a_1/(a_0 x), 1}
   begin scalar tmp, shift, i, c, solution;
      odecoeffs := reverse odecoeffs;   % high .. low

      %% Check for possible Euler or "shifted Euler" form.
      %% Find second non-zero ode coeff:
      tmp := rest odecoeffs;  i := 1;
      while first tmp = 0 do << tmp := rest tmp; i := i+1 >>;
      tmp := first tmp;                 % second non-zero ode coeff
      tmp := den tmp;                   % ax^i or a(x+b)^i
      tmp := reverse coeff(tmp, x);     % high .. low
      if high_pow neq i then return;    % not Euler
      if second tmp then <<             % "shifted Euler"
         shift := second tmp/(i*first tmp); % b
         driver := sub(x=x-shift, driver) % x -> x-b
      >>;

      tmp := {first odecoeffs};  i := 0;
      odecoeffs := rest odecoeffs;
   a: if odecoeffs neq {} then <<
         c := first odecoeffs * (x+shift)^(i:=i+1);
         if not(c freeof x) then return; % not Euler
         tmp := c . tmp;
         odecoeffs := rest odecoeffs;
         go to a
      >>;
      odecoeffs := tmp;

      traceode "It is of the homogeneous (Euler) type ",
         if shift then "(with shifted coefficients) " else "",
         "and is reducible to a simpler ODE ...";
      i := -2;
      tmp := for each c in odecoeffs sum <<
         i := i + 1;
         c * for j := 0 : i product (x-j)
      >>;
      odecoeffs := coeff(tmp, x);       % TEMPORARY HACK!
      driver := sub(x=e^x, driver*x^ode_order);
      solution := ODESolve!-LCC(odecoeffs, driver, x, ode_order);
      solution := sub(x=log x, solution);
      if shift then solution := sub(x=x+shift, solution);
      return solution
   end$

algebraic procedure ODELin!-Exact(P_list, driver, y, x, n);
   %% Computes a (linear) first integral if ODE is an exact linear
   %% n'th order ODE P_n(x) df(y,x,n) + ... + P_0(x) y = R(x).
   begin scalar P_0, C, Q_list, Q, const, soln, PI;
      P_0 := first P_list;
      P_list := reverse rest P_list;         % P_n, ..., P_1
      %% ODE is exact if C = df(P_n,x,n) - df(P_{n-1},x,{n-1}) + ...
      %% + (-1)^{n-1} df(P_1,x) + (-1)^n P_0 = 0.
      for each P in P_list do C := P - df(C,x);
      C := P_0 - df(C,x);               % C = 0 if exact
      if C then return;
      Q_list := {};
      for each P in P_list do
         Q_list := (Q := P - df(Q,x)) . Q_list; % Q_0, ..., Q_{n-1}
      driver := int(driver, x) + (const := symbolic gensym());
      %% The first integral is the LINEAR (n-1)'th order ODE
      %% Q_{n-1}(x) df(y,x,n) + ... + Q_0(x) y = int(R(x),x).
      traceode "It is exact, and the following linear ODE of order ",
         n-1, " is a first integral:";
      if symbolic !*trode then <<
         C := y;
         soln := first Q_list*y +
            ( for each Q in rest Q_list sum Q*(C := df(C,x)) );
         write soln = driver
      >>;
      %% Recurse on the order:
      C := Q_list;
      %% First-integral ODE must have min order 0, since input ODE was
      %% already order-reduced.
      soln := ODESolve!-linear!-basis!-recursive
         (Q_list, driver, y, x, n-1, 0);
      PI := second soln;                % MUST exist since driver neq 0
      PI := coeff(PI, const);           % { real PI, extra basis fn }
      return if high_pow = 1 then
         if first PI then { second PI . first soln, first PI }
         else { second PI . first soln }
      else <<
         %% This error should now be redundant!
         write "*** Internal error in ODELin!-Exact:",
            " cannot separate basis functions! ";
         write "(Probably caused by `noint' option.)";
         soln
      >>
   end$

algebraic procedure ODELin!-Reduce!-Order
   (odecoeffs, driver, y, x, ode_order, min_order);
   %% If ODE does not explicitly involve y (and perhaps low order
   %% derivatives) then simplify by reducing the effective order
   %% (unless there is only one) and try to solve the reduced ODE to
   %% give a first integral.  Applies only to ODEs of order > 1.
   begin scalar solution, PI;
      ode_order := ode_order - min_order;
      for ord := 1 : min_order do odecoeffs := rest odecoeffs;
      traceode "Performing trivial order reduction to give the order ",
         ode_order, " linear ODE with coefficients (low -- high): ",
         odecoeffs;
      solution := ODESolve!-linear!-basis!-recursive
         (odecoeffs, driver, y, x, ode_order, 0);
      if not solution then <<
         traceode "But ODESolve cannot solve the reduced ODE! ";
         return
      >>;
      traceode "Solution of order-reduced ODE is ", solution;
      traceode "Restoring order, ", y => df(y,x,min_order),
         ", to give: ", df(y,x,min_order) = solution,
         " and re-solving ...";
      %% = lin comb of fns of x, so just integrate min_order times:
      if length solution > 1 then       % PI = particular integral
         PI := second solution;
      solution := append(
         for each c in first solution collect
            ODESolve!-multi!-int(c, x, min_order),
         %% and add min_order extra basis functions:
         for i := min_order-1 step -1 until 0 collect x^i );
      return if PI then
         { solution, ODESolve!-multi!-int(PI, x, min_order) }
      else
         { solution }
   end$

endmodule$

end$
