module odetop$  % Top level ODESolve routines, exact ODEs, general
                % nonlinear ODE simplifications and utilities

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

% TO DO:
%    allow for non-trivial denominator in exact ODEs

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Code to support hooks for extending the functionality of ODESolve
% without needing to edit the main source code.  (Based on the hooks
% supported by Emacs.)  [Note that in CSL, each hook must be declared
% global (or fluid), even for interactive testing, otherwise boundp
% does not work as expected!]

% To do: Run hooks within an errorset for extra security?

symbolic procedure ODESolve!-Run!-Hook(hook, args);
   %% HOOK is the *name* of a hook; ARGS is a *list* of arguments.
   %% If HOOK is a function or is bound to a function then apply it to
   %% ARGS; if HOOK is bound to a list of functions then apply them in
   %% turn to ARGS until one of them returns non-nil and return that
   %% value.  Otherwise, return nil.
   if getd hook then apply(hook, args)
   else if boundp hook then <<
      hook := eval hook;
      if atom hook then
         getd hook and apply(hook, args)
      else
      begin scalar result;
         while hook and null(
            getd car hook and
            (result:=apply(car hook, args))) do
               hook := cdr hook;
         if hook then return result
      end
   >>$

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Code to break ODE simplifier loops
% ==================================

fluid '(odesolve!-interchange!-list!* !*odesolve!-norecurse)$

global '(ODESolve!-Standard!-x ODESolve!-Standard!-y)$

ODESolve!-Standard!-x := gensym()$
ODESolve!-Standard!-y := gensym()$

symbolic procedure ODESolve!-Standardize(ode, y, x);
   %% Return the numerator of ode in true prefix form and with
   %% standardized variable names.  (What about sign, etc.?)
   subst(ODESolve!-Standard!-y, y,
      subst(ODESolve!-Standard!-x, x, prepf numr simp!* ode))$

symbolic procedure ODESimp!-Interrupt(ode, y, x);
   begin scalar std_ode;
      ode := num !*eqn2a ode;           % Returns ode as expression.
      if member(std_ode:=ODESolve!-Standardize(ode, y, x),
         odesolve!-interchange!-list!*) then <<
            traceode "ODE simplifier loop interrupted! ";
            return t
         >>;
      odesolve!-interchange!-list!* := std_ode .
         odesolve!-interchange!-list!*
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Top-level classification of an ODE, primarily as linear or
% nonlinear.

global '(ODESolve_Before_Hook ODESolve_After_Hook)$
global '(ODESolve_Before_Non_Hook ODESolve_After_Non_Hook)$

algebraic procedure ODESolve!*0(ode, y, x);
   %% Top-level general ODE solver.  If no derivatives call solve?
   %% ***** DO NOT CALL RECURSIVELY *****
   symbolic begin scalar !*precise, solution, !*odesolve!-norecurse,
         odesolve!-interchange!-list!*, !*odesolve!-solvable!-xy;
      %% (odesolve!-interchange!-list!* and !*odesolve!-solvable!-xy
      %% are used to prevent infinite loops.)
      ode := num !*eqn2a ode;           % returns ode as expression
      if (solution := or(
         ODESolve!-Run!-Hook('ODESolve_Before_Hook, {ode,y,x}),
         ODESolve!*1(ode, y, x),
         %% Call ODESolve!-Diff once only, not in recursive loop?
         %% SHOULD apply only to nonlinear ODEs?
         not !*odesolve_fast and ODESolve!-Diff(ode, y, x),
         ODESolve!-Run!-Hook('ODESolve_After_Hook, {ode,y,x})))
      then return solution;
      traceode "ODESolve cannot solve this ODE!"
   end$

algebraic procedure ODESolve!*1(ode, y, x);
   %% Top-level discrimination between linear and nonlinear ODEs.
   %% May be called recursively.
   %% (NB: A product of linear factors is NONLINEAR!)
   symbolic if !*odesolve!-norecurse then
      traceode "ODESolve terminated: no recursion mode!"
   else if ODESimp!-Interrupt(ode, y, x) then nil else
   <<
      !*odesolve!-norecurse := !*odesolve_norecurse;
      traceode1 "Entering top-level general recursive solver ...";
      if ODE!-Linearp(ode, y) then      % linear
         ODESolve!-linear(ode, y, x)
      else  % nonlinear -- turn off basis solution
      algebraic begin scalar !*odesolve_basis, ode_factors, solns;
         %% Split into algebraic factors (which may lose exactness).
         %% For each algebraic factor, check its linearity and call
         %% appropriate (linear or nonlinear) main solver.
         %% Merge solution sets.
         traceode1 "Trying to factorize nonlinear ODE algebraically ...";
         ode_factors := factorize ode;
         %% { {factor, multiplicity}, ... }
         if length ode_factors = 1 and second first ode_factors = 1 then
            %% Guaranteed algebraically-irreducible nonlinear ODE ...
            return ODESolve!-nonlinear(ode, y, x);
         traceode "This is a nonlinear ODE that factorizes algebraically ",
            "and each distinct factor ODE will be solved separately ...";
         solns := {};
         while ode_factors neq {} do
         begin scalar fac;
            %% Discard repeated factors:
            if smember(y, fac := first first ode_factors) then
               %% Guaranteed algebraically-irreducible -- may be
               %% either algebraic or linear or nonlinear ODE ...
               if (fac := ODESolve!*2!*(fac, y, x)) then <<
                  solns := append(solns, fac);
                  ode_factors := rest ode_factors
               >>  else solns := ode_factors := {}
            else <<
               if depends(fac, x) or depends(fac, y) then
                  symbolic MsgPri("ODE factor", fac, "ignored", nil, nil);
               ode_factors := rest ode_factors
            >>;
         end;
         %% Finally check whether the UNFACTORIZED ode was exact:
         return
            if solns = {} then Odesolve!-Exact!*(ode, y, x)
            else solns
      end
   >>$

algebraic procedure ODESolve!-FirstOrder(ode, y, x);
   %% Solve an ARBITRARY first-order ODE.
   %% (Called from various other modules.)
   symbolic <<
      ode := num !*eqn2a ode;
      traceode ode = 0;
%%       if ODE!-Linearp(ode, y)        % nil <> 0 !!!
%%       then ODENon!-Linear1(ode, y, x)
%%       else ODESolve!-NonLinear1(ode, y, x)
      %% A nonlinear first-order ODE may need the full solver ...
      %% but could later arrange to pass the order rather than
      %% recompute it.
      ODESolve!*1(ode, y, x)
   >>$

algebraic procedure ODESolve!*2!*(ode, y, x);
   %% Internal discrimination between algebraic or differential factor.
   if smember(df, ode) then             % ODE
      ODESolve!*2(ode, y, x)
   else if ode = y then                 % Common special algebraic case,
      {y = 0}                           % e.g. solving autonomous ODEs.
   else solve(ode, y)$                  % General algebraic case.

algebraic procedure ODESolve!*2(ode, y, x);
   %% Internal discrimination between linear and nonlinear ODEs.  Like
   %% ODESolve!*1 but does not attempt any algebraic factorization.
   symbolic <<
      traceode1 "Entering top-level recursive solver ",
         "without algebraic factorization ...";
      traceode ode=0;
      if ODE!-Linearp(ode, y) then      % linear
         ODESolve!-linear(ode, y, x)
      else                              % nonlinear
         ODESolve!-nonlinear(ode, y, x)
   >>$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The entry point to the non-trivially nonlinear ODE solver
% =========================================================

algebraic procedure ODESolve!-nonlinear(ode, y, x);
   %% Attempt to solve an algebraically-irreducible nonlinear ODE.
   symbolic %% if ODESimp!-Interrupt(ode, y, x) then nil else
      begin scalar ode_order;
         ode_order := ODE!-Order(ode, y);
         traceode "This is a nonlinear ODE of order ", ode_order, ".";
         return or(
            ODESolve!-Run!-Hook(
               'ODESolve_Before_Non_Hook, {ode,y,x,ode_order}),
            (if ode_order = 1 then
               ODESolve!-nonlinear1(ode, y, x)
            else
               %% ODESolve!-Diff(ode, y, x) or % TEMPORARY
               ODESolve!-nonlinearn(ode, y, x)),
            ODESolve!-Exact(ode, y, x, ode_order),
            not !*odesolve_fast and ODESolve!-Alg!-Solve(ode, y, x),
            not !*odesolve_fast and ODESolve!-Interchange(ode, y, x),
            ODESolve!-Run!-Hook(
               'ODESolve_After_Non_Hook, {ode,y,x,ode_order}))
      end$

symbolic procedure ODESolve!-Interchange(ode, y, x);
   %% Interchange x <--> y and try to solve.
   %% PROBABLY NOT DESIRABLE FOR LINEAR ODES!
   if !*odesolve_noswap then
      traceode "ODESolve terminated: no variable swap mode!"
   else
   ( begin scalar !*precise;            % Can cause trouble here
      traceode
         "Interchanging dependent and independent variables ...";
      %% Should fully canonicalize ode before comparison!!!
      %% Temporarily, just use reval to at least ensure the same format.
      %% Cannot use aeval form because simplified flag gets reset.
%%       odesolve!-interchange!-list!* :=
%%          %% reval ode . odesolve!-interchange!-list!*;
%%          ODESolve!-Standardize(ode, y, x) . odesolve!-interchange!-list!*;
      depend1(x, y, t);
      algebraic begin scalar rules;
         rules := {odesolve!-df(y,x,~n) => 1/odesolve!-df(x,y)*
            odesolve!-df(odesolve!-df(y,x,n-1),y) when n > 1,
            odesolve!-df(y,x) => 1/odesolve!-df(x,y),
            odesolve!-df(y,x,1) => 1/odesolve!-df(x,y)};
         ode := sub(df = odesolve!-df, ode);
         ode := (ode where rules);
         ode := num sub(odesolve!-df = df, ode)
      end;
      depend1(y, x, nil);   % Necessary to avoid dependence loops
      %% Now ode is an ode for x as a function of y
      traceode ode;
%%       %% if member(reval ode, odesolve!-interchange!-list!*) then
%%       if member(ODESolve!-Standardize(ode, x, y),
%%          odesolve!-interchange!-list!*) then
%%          %% Give up -- we have already interchanged variables in this
%%          %% ode once!
%%          << !*odesolve_failed := t;  return algebraic {ode=0} >>;
      ode := ODESolve!*1(ode, x, y);    % Try again ..
      if ode then return
         makelist for each soln in cdr ode join
            if smember(y, soln) then {soln} else {}
   end ) where depl!* = depl!*$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Exact equations
% ===============

% Solve an ODE if it is an exact first or second order ODE.  Exactness
% might be lost by factorizing an ode, so all exact ode routines are
% gathered together here under one master routine that can be called
% independently of any ODE simplification.

% Replace by one general routine for any order nonlinear ODE?

% The first-order code is based on code by Malcolm MacCallum.

algebraic procedure ODESolve!-Exact!*(ode, y, x);
   %% Solve an exact first or second order nonlinear ODE of unknown
   %% order.
   ODESolve!-Exact(ode, y, x, ODE!-Order(ode, y))$

algebraic procedure ODESolve!-Exact(ode, y, x, ode_order);
   %% Solve an exact first or second order nonlinear ODE of known
   %% order.
   begin scalar c, den_ode, result;
      traceode1 "Checking for an exact ode ...";
      c := coeff(num ode, df(y,x,ode_order));
      den_ode := den ode;
      if not depends(den_ode, x) then den_ode := 0;
      %% ... meaning den ode has no effect on exactness.
      %% if length c neq 2 or depends(c, df(y,x,n)) then return;
      %% NB: depends recurses indefinitely if x depends on y, i.e. after
      %% interchange at present.  But smember nearly suffices anyway!
      if length c neq 2 or smember(df(y,x,ode_order), c) then return;
      return if ode_order = 1 then
         symbolic ODESolve!-Exact!-1(c, den_ode, y, x)
      else if ode_order = 2 then
         symbolic ODESolve!-Exact!-2(c, den_ode, y, x)
   end$

symbolic procedure ODESolve!-Exact!-1(c, den_ode, y, x);
   %% Solves the ode if it is an exact (nonlinear) first order ode of
   %% the form = N dy/dx + M.
   ( algebraic begin scalar M, N;
      M := first c;  N := second c;
      symbolic depend1(y, x, nil);      % all derivatives partial
      if df(M,y) - df(N,x) and
         (not den_ode or df(M:=M/den_ode,y) - df(N:=N/den_ode,x))
      then return;
      %% traceode "This is an exact first-order ODE.";
      traceode "It is exact and is solved by quadrature.";
      return {exact1_pde(M, N, y, x) = 0}
   end ) where depl!* = depl!*$

algebraic procedure exact1_pde(M, N, y, x);
   %% Return phi(x,y) such that df(phi,x) = M(x,y), df(phi,y) =
   %% N(x,y), required to integrate first and second order exact odes.
   begin scalar int_M;  int_M := int(M, x);
      %% phi = int_M + f(y)
      %% => df(phi,y) = df(int_M,y) + df(f,y) = N
      %% => f = int(N - df(int_M,y), y)
      return num(int_M + int(N - df(int_M,y), y) + newarbconst())
   end$

symbolic procedure ODESolve!-Exact!-2(c, den_ode, y, x);
   %% Computes a first integral of ODE if it is an exact (nonlinear)
   %% second order ODE of the form f(x,y,y') y'' + g(x,y,y') = 0.
   %% *** EXTEND THIS GENERAL CODE TO HIGHER ORDER ??? ***
   ( algebraic begin scalar p, f, g, h, first_int, h_x, h_y;
      p := gensym();
      f := sub(df(y,x) = p, second c);
      g := sub(df(y,x) = p, first c);
      symbolic depend1(y, x, nil);      % all derivatives partial
      if ODESolve!-Exact!-2!-test(f, g, p, y, x)
         and (not den_ode or
            ODESolve!-Exact!-2!-test(f:=f/den_ode, g:=g/den_ode, p, y, x))
      then return;
      %% ODE is exact
      %% traceode "This is an exact second-order ODE for which ",
      %%   "a first integral can be constructed:";
      traceode "It is exact and a first integral can be constructed ...";
      h := gensym();
      symbolic depend1(h, x, t);  symbolic depend1(h, y, t);
      first_int := int(f, p) + h;
      c := df(first_int,x) + df(first_int,y)*p - g; % = 0
      %% Should be linear in p by construction -- equate coeffs:
      c := coeff(num c, p);
      if length c neq 2 or depends(c, p) then return
         traceode "but ODESolve cannot determine the arbitrary function!";
      %% MUST be linear in h_x and h_y by construction, so ...
      h_x := coeff(first c, df(h,x));  h_x := -first h_x / second h_x;
      h_y := coeff(second c, df(h,y));  h_y := -first h_y / second h_y;
      h_x := exact1_pde(h_x, h_y, y, x);
      symbolic depend1(y, x, t);
      first_int := sub(h = h_x, p = df(y,x), first_int);
      %% traceode first_int = 0;
      first_int := ODESolve!-FirstOrder(first_int, y, x);
      return
         if first_int then ODESolve!-Simp!-ArbConsts(first_int, y, x)
         else traceode "But ODESolve cannot solve it!"
   end ) where depl!* = depl!*$

algebraic procedure ODESolve!-Exact!-2!-test(f, g, p, y, x);
   if ( (df(f,x,2) + 2p*df(f,x,y) + p^2*df(f,y,2)) -
      (df(g,x,p) + p*df(g,y,p) - df(g,y)) or
         (df(f,x,p) + p*df(f,y,p) + 2df(f,y)) - df(g,p,2) ) then 1$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

switch odesolve_diff$                   % TEMPORARY?
symbolic(!*odesolve_diff := t)$         % TEMPORARY?

fluid '(!*arbvars)$

algebraic procedure ODESolve!-Diff(ode, y, x);
   %% If the derivative of ode factorizes then try to solve each
   %% factor and return the solutions, otherwise return nil.
   %% This is the inverse of detecting an exact ode!
   if symbolic !*odesolve_diff then     % TEMPORARY?
   begin scalar ode_factors, solns;
      load_package solve;  % to allow overriding !*arbvars := t
      traceode1
         "Trying to factorize derivative of ODE algebraically ...";
      ode_factors := factorize num df(ode, x);
      %% { {factor, multiplicity}, ... }
      if length ode_factors = 1 and second first ode_factors = 1 then return;
      traceode "The derivative of the ODE factorizes algebraically ",
         "and each distinct factor ODE will be solved separately ...";
      solns := {};
      while ode_factors neq {} do
      begin scalar fac, deriv_orders, first!!arbconst, arbconsts,
         !*arbvars;
         fac := first first ode_factors;  ode_factors := rest ode_factors;
         deriv_orders := get_deriv_orders(fac, y);
         %% Check for purely algebraic factor:
         if deriv_orders = {} then return;  % no y -- ignore
         if deriv_orders = {0} then return
            for each s in solve(fac, y) do
               if sub(s, ode) = 0 then solns := (s = 0) . solns;
         first!!arbconst := !!arbconst + 1;
         fac := ODESolve!*2(fac, y, x); % to avoid nasty loops
         if not fac then return solns := ode_factors := {};
         arbconsts :=
            for i := first!!arbconst : !!arbconst collect arbconst i;
         %% ***** THIS WILL WORK ONLY FOR EXPLICIT SOLUTIONS *****
         for each soln in fac do
            for each s in solve(sub(soln, ode), arbconsts) do
               solns := sub(s, soln) . solns
      end;
      if solns neq {} then return solns;
      traceode "... but cannot solve all factor ODEs.";
   end$

algebraic procedure ODESolve!-Alg!-Solve(ode, y, x);
   %% Try to solve algebraically for a single derivative and then
   %% solve each solution ode directly.
   begin scalar deriv, L, R, d, root_odes, solns;
      scalar !*fullroots, !*trigform, !*precise;
      %% symbolic(!*fullroots := t);       % Can be VERY slow!
      traceode1
         "Trying to solve algebraically for a single derivative ...";
      deriv := delete(0, get_deriv_orders(ode, y));
      if length deriv neq 1 then return; % not a single deriv
      %% Now ode is an expression in df(y,x,ord) involving no other
      %% derivatives.  Try to solve it algebraically for the
      %% derivative.
      deriv := df(y, x, first deriv);
      if not( smember(deriv, L:=lcof(ode,deriv)) or
              smember(deriv, R:=reduct(ode,deriv)) ) then
         if (d:=deg(ode,deriv)) = 1 then
            return                      % linear in single deriv
         else
            root_odes :=                % single integer power
               { num(deriv - (-R/L)^(1/d)*newroot_of_unity(d)) }
               %% Expand roots of unity later.
      else <<
         root_odes := solve(ode, deriv);
         if not(length root_odes > 1 or
            first root_multiplicities > 1) then return;
         %% Eventually, replace above 3 lines with this:
         %% root_odes := SolvePM(ode, deriv); % `use `plus_or_minus'
         root_odes := for each ode in root_odes collect
            num if symbolic eqcar(caddr ode, 'root_of) then
               sub(part(rhs ode, 2)=deriv, part(rhs ode, 1))
            else lhs ode - rhs ode
      >>;
      traceode "It can be (partially) solved algebraically ",
         "for the single-order derivative ",
         "and each `root ODE' will be solved separately ...";
      solns := {};
      while root_odes neq {} do
      begin scalar soln;
         if (soln := ODESolve!*2(first root_odes, y, x)) then <<
            solns := append(solns, soln);
            root_odes := rest root_odes
         >> else solns := root_odes := {}
      end;
      if solns neq {} then return solns
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Utility procedures
% ==================

% Linearity and order tests, which are best kept separate!

%% NB: smember in ODE!-Linearp should probably be depends!

symbolic operator ODE!-Linearp$
symbolic procedure ODE!-Linearp(ode, y);
   %% ODE is assumed to be an expression (not an equation).
   %% Returns t if ODE is linear in y, nil otherwise.
   %% Assumes on exp, mcd.
   ODE!-Lin!-Form!-p(numr simp!* ode, !*a2k y)$

symbolic procedure ODE!-Lin!-Form!-p(sf, y);
   %% A standard (polynomial) form `sf' is linear if each of its terms
   %% is linear:
   domainp sf or
      (ODE!-Lin!-Term!-p(lt sf, y) and ODE!-Lin!-Form!-p(red sf, y))$

symbolic procedure ODE!-Lin!-Term!-p(st, y);
   %% A standard (polynomial) term `st' is linear if either (a) its
   %% leading power is linear and its coefficient is independent of y,
   %% or (b) its leading power is independent of y and its coefficient
   %% is linear:
   begin scalar knl;  knl := tvar st;
      return if knl eq y or (eqcar(knl, 'df) and cadr knl eq y) then
         %% Kernel knl is either y or a derivative of y (df y ...)
         tdeg st eq 1 and not depends(tc st, y)
      else if not depends(knl, y) then ODE!-Lin!-Form!-p(tc st, y)
   end$


symbolic operator ODE!-Order$
symbolic procedure ODE!-Order(u, y);
   %% u is initially an ODE, assumed to be an expression (not an
   %% equation).  Returns its order wrt. y.
   if atom u then 0
   else if car u eq 'df and cadr u eq y then
      %% u = (df y x n) or (df y x)
      (if cdddr u then cadddr u else 1)
   else
      max(ODE!-Order(car u, y), ODE!-Order(cdr u, y))$


symbolic operator get_deriv_orders$
symbolic procedure get_deriv_orders(ode, y);
%    %% Return range of orders of derivatives df(y,x,n) in ode as the
%    %% algebraic list {min_ord, min_d_ord, max_ord} where min_ord
%    %% includes 0, and min_d_ord excludes 0.
   %% Return the SET of all orders of derivatives df(y,x,n) in ode as
   %% an unsorted algebraic list.  Empty if ode freeof y.
   begin scalar result;
      ode := kernels numr simp!* ode;
      if null ode then return makelist nil;
      result := get_deriv_ords_knl(car ode, y);
      for each knl in cdr ode do
         result := union(get_deriv_ords_knl(knl, y), result);
%       return {'list, min_ord,
%          if zerop min_ord then min!* delete(0, result) else min_ord,
%             max!* result} where min_ord = min!* result
      return makelist result
   end$

symbolic procedure get_deriv_ords_knl(knl, y);
   %% Return a list of all orders of derivatives df(y,x,n) in kernel
   %% knl, treating y as df(y,x,0).
   if atom knl then (if knl eq y then (0 . nil))
   else if car knl eq 'df then
      (if cadr knl eq y then
         (if cdddr knl then cadddr knl else 1) . nil)
   else
      ( if in_car then union(in_car, in_cdr) else in_cdr )
         where in_car = get_deriv_ords_knl(car knl, y),
            in_cdr = get_deriv_ords_knl(cdr knl, y)$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Support for an n'th root of unity operator
% ==========================================

algebraic operator root_of_unity, plus_or_minus$

fluid '(!*intflag!*)$                   % true when in the integrator

% Simplify powers of these operators, but only when not in the
% integrator, which seems to be upset by this:
algebraic let (plus_or_minus(~tag))^2 => 1 when symbolic not !*intflag!*,
   (root_of_unity(~n, ~tag))^n => 1 when symbolic not !*intflag!*$
% Should really be more general, e.g.
%    (root_of_unity(~n, ~tag))^nn => 1 when fixp(nn/n)

algebraic procedure newroot_of_unity(n);
   if n = 0 then RedErr "zeroth roots of unity undefined"
   else if numberp n and (n:=abs num n) = 1 then 1
   else if n = 2 then
      plus_or_minus(newroot_of_unity_tag())
   else
      root_of_unity(n, newroot_of_unity_tag())$

algebraic procedure newplus_or_minus;
   %% Like this for immediate evaluation, especially in symbolic mode:
   symbolic {'plus_or_minus, newroot_of_unity_tag()}$

%% fluid '(!!root_of_unity)$  !!root_of_unity := 0$

algebraic procedure newroot_of_unity_tag;
   %% symbolic mkid('tag_, !!root_of_unity := add1 !!root_of_unity)$
   symbolic mkrootsoftag()$             % defined in module solve/solve1

define expand_plus_or_minus = expand_roots_of_unity$
define expand_root_of_unity = expand_roots_of_unity$

symbolic operator expand_roots_of_unity$
flag('(expand_roots_of_unity), 'noval)$

symbolic procedure expand_roots_of_unity u;
   begin scalar !*NoInt;  !*NoInt := t;  u := aeval u;
      return makelist union(            % discard repeats
         for each uu in (if rlistp u then cdr u else {u}) join
            cdr expand_roots_of_unity1 makelist {uu}, nil)
   end$

symbolic procedure expand_roots_of_unity1 u; % u is an rlist
   ( if r then expand_roots_of_unity1 makelist append(
      (if car r eq 'plus_or_minus then
         cdr subeval{{'equal, r, -1}, u}
      else
      begin scalar n, n!-1;
         if not fixp(n := numr simp!* cadr r) then
            TypErr(n, "root of unity");
         n!-1 := sub1 n;
         return for m := 1 : n!-1 join
            cdr algebraic sub(r = exp(i*2*pi*m/n), u)
      end), cdr subeval{{'equal, r, 1}, u} )
   else u ) where r = find_root_of_unity cdr u$

symbolic procedure find_root_of_unity u; % u is a list
   if atom u then nil
   else if car u eq 'plus_or_minus then u
   else if car u eq 'root_of_unity and evalnumberp cadr u then u
   else find_root_of_unity car u or find_root_of_unity cdr u$

endmodule$

end$
