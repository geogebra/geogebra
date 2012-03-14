module odenonn$  % Special form nonlinear ODEs of order > 1

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


% F.J.Wright@maths.qmw.ac.uk, Time-stamp: <14 August 2001>

% Trivial order reduction.
% Special cases of Lie symmetry, namely
%    autonomous, equidimensional and scale invariant equations.
% Simplification of arbitrary constants.

% TO DO:
%   avoid computing orders in both reduce and shift


algebraic procedure ODESolve!-nonlinearn(ode, y, x);
   %% `symbolic' mode here is ESSENTIAL, otherwise the code generated
   %% is as if this were a macro, except then it does not get eval'ed!
   symbolic ODENon!-Reduce!-Order(ode, y, x)$

%% The following defines are used to allow easy changes to the calling
%% sequence.

define ODENon!-Reduce!-Order!-Next = ODESolve!-Shift$
%% Shifting currently NEEDED for Zimmer (8) (only)!

define ODESolve!-Shift!-Next = ODESolve!-nonlinearn!*1$

switch odesolve_equidim_y$              % TEMPORARY?
symbolic(!*odesolve_equidim_y := t)$    % TEMPORARY?

algebraic procedure ODESolve!-nonlinearn!*1(ode, y, x);
   %% The order here seems to be important in practice:
   symbolic or(
      ODESolve!-Autonomous(ode, y, x),
      ODESolve!-ScaleInv(ode, y, x),    % includes equidim in x
      !*odesolve_equidim_y and
      ODESolve!-Equidim!-y(ode, y, x) )$

algebraic procedure ODENon!-Reduce!-Order(ode, y, x);
   %% If ode does not explicitly involve y and low order derivatives
   %% then simplify by reducing the effective order (unless there is
   %% only one) and then try to solve the reduced ode directly to give
   %% a first integral.  Applies only to odes of order > 1.
   begin scalar deriv_orders, min_order, max_order;
      traceode1 "Trying trivial order reduction ...";
      deriv_orders := get_deriv_orders(ode, y);
      %% Check for purely algebraic factor from some simplification,
      %% such as autonomous reduction:
      if deriv_orders = {} or deriv_orders = {0} then
         return {ode = 0};              % purely algebraic!
      %% Avoid reduction to a purely algebraic equation:
      if (min_order := min deriv_orders) = 0 or
         length deriv_orders = 1 then return
            ODENon!-Reduce!-Order!-Next(ode, y, x);
      max_order := max deriv_orders;
      ode := sub(df = odesolve!-df, ode);
      for ord := min_order : max_order do
         ode := if ord = 1 then (ode where odesolve!-df(y,x) => y)
         else (ode where odesolve!-df(y,x,ord) =>
            odesolve!-df(y,x,ord-min_order));
      ode := sub(odesolve!-df = df, ode);
      traceode "Performing trivial order reduction to give ",
         "the order ", max_order - min_order, " nonlinear ODE: ",
         ode = 0;
      ode := symbolic(
         (if max_order - min_order = 1 then % first order
            ODESolve!-nonlinear1(ode, y, x)
         else
            ODENon!-Reduce!-Order!-Next(ode, y, x))
               where !*odesolve_explicit = t);
      if not ode then <<
         traceode "Cannot solve order-reduced ODE!";
         return                         % abandon solution
      >>;
      %% ode := sub(y = df(y,x,min_order), ode);
      traceode "Solution of order-reduced ODE is ", ode;
      traceode "Restoring order, ", y => df(y,x,min_order),
         ", to give: ", sub(y = df(y,x,min_order), ode),
         " and re-solving ...";
      ode := for each soln in ode join
         %% Each `soln' here is an EQUATION for y that may be
         %% implicit.
         if lhs soln = y then           % explicit
            { y = ODESolve!-multi!-int!*(rhs soln, x, min_order) }
         else <<
            soln := solve(soln, y);
            for each s in soln collect
               if lhs s = y then        % explicit
                  y = ODESolve!-multi!-int!*(rhs s, x, min_order)
               else                     % implicit
                  %% leave unsolved for now
                  sub(y = df(y,x,min_order), s)
         >>;
      return ODESolve!-Simp!-ArbConsts(ode, y, x)
   end$

algebraic procedure ODESolve!-multi!-int!*(y, x, m);
   %% Integate y wrt x m times and add arbitrary constants:
   ODESolve!-multi!-int(y, x, m) +
      %% << >> below is NECESSARY to force immediate evaluation!
      for i := 0 : m-1 sum <<newarbconst()>>*x^i$


% Internal wrapper function for ODESolve!-Shift:
algebraic operator odesolve!-sub!*$

algebraic procedure ODESolve!-Shift(ode, y, x);
   %% A first attempt at canonicalizing an ODE by shifting the
   %% independent variable.
   symbolic if not !*odesolve_fast then % heuristic solution
   algebraic begin scalar deriv_orders, a, c, d;
      traceode1 "Looking for an independent variable shift ...";
      deriv_orders := get_deriv_orders(ode, y);
      deriv_orders := sort(deriv_orders, >);
      %% Try to find a non-trivial "coefficient" polynomial
      %% constituent c that is linear in x.
      while deriv_orders neq {} and
         (c := lcof(ode, df(y,x,first deriv_orders))) freeof x do
            deriv_orders := rest deriv_orders;
      if deriv_orders = {} then         % not shiftable
         return ODESolve!-Shift!-Next(ode, y, x);
      if (d := deg(c, x)) neq 1 then <<
         c := decompose c;
         while (c := rest c) neq {} and deg(rhs first c, x) neq 1 do;
         %% << null loop body >>
         if c neq {} then c := rhs first c;
         if deg(c, x) neq 1 then        % not shiftable
            return ODESolve!-Shift!-Next(ode, y, x)
      >>;
      %% c = ax + b is a linear component polynomial of the ode
      %% coefficients.
      if not(c freeof y) or not((c := coeff(c,x)) freeof x) or
         first c = 0 then               % not shiftable
         return ODESolve!-Shift!-Next(ode, y, x);
      c := first c / (a := second c);
      %% ode is a function of ax + b (= a(x + c)), so ...
      ode := sub(x = x - c, ode) / a^d;
      %% This will leave sub(..., df(...)) symbolic, so ...
      ode := num sub(sub = odesolve!-sub!*, ode);
      ode := (ode where odesolve!-sub!*(~a! ,~b! ) => b! );
      traceode "This ODE can be simplified by the ",
         "independent variable shift ", x => x - c,
         " to give: ", ode = 0;
      ode := ODESolve!-Shift!-Next(ode, y, x);
      if ode then return
         for each soln in ode collect
            if symbolic rlistp soln then   % parametric solution
               for each s in soln collect
                  if symbolic eqcar(s, 'equal) and lhs s = x
                  then x = rhs s - c else s
            else sub(x = x + c, soln)
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Autonomous, equidimensional and scale-invariant ODEs
% ====================================================

algebraic procedure ODESolve!-Autonomous(ode, y, x);
   %% If ODE is autonomous, i.e. x does not appear explicitly, then
   %% reduce the order by using y as independent variable and then try
   %% to solve the reduced ODE directly.  Applies only to ODEs of
   %% order > 1.  Do not apply to a linear ODE, because it will become
   %% nonlinear!
   begin scalar ode1, u, soln;
      traceode1 "Testing whether ODE is autonomous ...";
      ode1 := (ode where df(y,x) => 1, df(y,x,~n) => 1);
      if smember(x, ode1) then return;  % not autonomous
      u := gensym();
      symbolic depend1(u,x,t);  symbolic depend1(u,y,t);
      ode := (ode where df(y,x) => u, df(y,x,~n) => df(u,x,n-1));
      ode := (ode where df(u,x,~n) => u*df(df(u,x,n-1),y) when n > 1,
         %% above condition n > 1 is NECESSARY!
         df(u,x) => u*df(u,y));
      symbolic depend1(u,x,nil);
      traceode
         "This ODE is autonomous -- transforming dependent variable ",
         "to derivative to give this ODE of order 1 lower: ", ode = 0;
      ode := symbolic(ODESolve!*1(ode, u, y)
         where !*odesolve_explicit = t);
      if not ode then <<
         symbolic depend1(u,y,nil);
         traceode "Cannot solve transformed autonomous ODE!";
         return
      >>;
      ode := sub(u = df(y,x), ode);
      symbolic depend1(u,y,nil);
      traceode "Restoring order to give these first-order ODEs ...";
      soln := {};
   a: if ode neq {} then
         if (u := ODESolve!-FirstOrder(first ode, y, x)) then <<
            soln := append(soln, u);
            ode := rest ode;
            go to a
         >> else <<
            traceode "Cannot solve one of the first-order ODEs ",
               "arising from solution of transformed autonomous ODE!";
            return
         >>;
      return ODESolve!-Simp!-ArbConsts(soln, y, x)
   end$

algebraic procedure ODESolve!-ScaleInv(ode, y, x);
   %% If ODE is scale invariant, i.e. invariant under x -> a x, y ->
   %% a^p y, then transform it to an equidimensional-in-x ODE and try
   %% to solve it.  If p = 0 then it is already equidimensional-in-x
   %% as a special case.  Returns a solution or nil if this method
   %% does not lead to a solution.  PROBABLY NOT USEFUL FOR LINEAR
   %% ODES.
   begin scalar u, p, ode1, pow, !*allfac;
      traceode1 "Testing whether ODE is scale invariant or ",
         "equidimensional in the independent variable ", x, " ...";
      u := gensym();  p := gensym();
      ode1 := (ode where df(y,x,~n) => mkid(u,n)*x^(p-n),
         df(y,x) => mkid(u,1)*x^(p-1));
      %% mkid's to avoid spurious cancellations.
      ode1 := num sub(y = u*x^p, ode1);
      %% Try to choose p to make ode1 proportional to some single
      %% power of x.  Assume ode1 is a sum of terms.
      begin scalar part1, n_parts;
         part1 := part(ode1, 1);
         n_parts := arglength ode1;     % must be at least 2 terms
         for i := 2 : n_parts do <<
            parti := part(ode1, i)/part1;
            pow := df(parti, x)*x/parti;
            if pow then << pow := solve(pow, p);  n_parts := 0 >>
         >>;
         if n_parts then
            %% Scale invariant for ANY p =>
            %% equidimensional in both x and y
            return pow := {p=0};
         ode1 := (ode1 - part1)/part1;
         while pow neq {} and           % check scale invariance
            (symbolic eqcar(caddr cadr pow, 'root_of) or
               not(sub(first pow, ode1) freeof x)) do
                  pow := rest pow
      end;
      if pow = {} then return;          % not scale invariant
      if not(p := rhs first pow) then
         %% Scale invariant for p=0 =>
         %% equidimensional in x ...
         return ODESolve!-ScaleInv!-Equidim!-x(ode, y, x);
      %% ode is scale invariant (with p neq 0)
      symbolic depend1(u, x, t);
      ode := sub(y = x^p*u, ode);
      traceode "This ODE is scale invariant -- applying ", y => x^p*u,
         " to transform to the simpler ODE: ", ode = 0;
      ode := ODESolve!-ScaleInv!-Equidim!-x(ode, u, x);
      symbolic depend1(u, x, nil);
      if ode then return sub(u = y/x^p, ode);
      traceode "Cannot solve transformed scale invariant ODE!"
   end$

algebraic procedure ODESolve!-ScaleInv!-Equidim!-x(ode, y, x);
   %% ODE is equidimensional in x, i.e. invariant under x -> ax, so
   %% transform it to an autonomous ODE and try to solve it.  (This
   %% includes "reduced" Euler equations as a special case.  Could
   %% ignore terms independent of y in testing equidimensionality; if
   %% there are such terms then the simplified ODE will not be
   %% autonomous.  This generalization includes ALL Euler equations.)
   %% Returns a solution or nil if this method does not lead to a
   %% solution.
   begin scalar tt, exp_tt;
      tt := gensym();
      %% ode is equidimensional in x; x -> exp(tt):
      exp_tt := exp(tt);
      symbolic depend1(y,tt,t);
      ode := (ode where df(y,x) => df(y,tt)/exp_tt,
         df(y,x,~n) => df(df(y,x,n-1),tt)/exp_tt when
            numberp n and n > 0);  % n > 0 condition is necessary!
      ode := num sub(x = exp_tt, ode);
      traceode
         "This ODE is equidimensional in the independent variable ",
         x, " -- applying ", x => exp_tt,
         " to transform to the simpler ODE: ",
         ode = 0;
      symbolic depend1(y,x,nil);   % Necessary to avoid dependence loops
      %% ode should be autonomous PROVIDED no term independent of y
      ode := symbolic ODESolve!-Autonomous(ode, y, tt);
      symbolic depend1(y,x,t);   %%% ???
      symbolic depend1(y,tt,nil);
      if ode then return sub(tt = log x, ode);
      traceode "Cannot solve transformed equidimensional ODE!"
   end$

algebraic procedure ODESolve!-Equidim!-y(ode, y, x);
   %% If ODE is equidimensional in y, i.e. invariant under y -> ay,
   %% then simplify the ODE and try to solve the result.  Returns a
   %% solution or nil if this method does not lead to a solution.  Do
   %% not apply to a linear ODE, which is trivially equidimensional in
   %% y, because it will become nonlinear!
   begin scalar ode1, u, exp_u;
      traceode1 "Testing whether ODE is equidimensional in ",
         "the dependent variable ", y, " ...";
      u := gensym();  % to avoid spurious cancellations
      ode1 := (ode where df(y,x,~n) => y*mkid(u,n),
         df(y,x) => y*u);
      %% ode1 must be proportional to some single positive integer
      %% power of y:
      if reduct(ode1, y) or depends(lcof(ode1, y), y) then return;
      %% ode is equidimensional in y; y -> exp(u):
      exp_u := exp(u);
      symbolic depend1(u,x,t);
      ode := lcof(num sub(y = exp_u, ode), exp_u);
      %% (Lcof above to remove irrelevant factor of a power of y.)
      traceode
         "This ODE is equidimensional in the dependent variable ",
         y, " -- applying ", y => exp_u,
         " to transform to the simpler ODE: ",
         ode = 0;
      %% ode here could be ANY kind of ode.  It should be less
      %% nonlinear, but I don't think there is any guarantee that it
      %% will be linear -- is there?  Hence we must call the full
      %% general ode solver again:
      ode := ODESolve!*1(ode, u, x);
      symbolic depend1(u,x,nil);
      if not ode then <<
         traceode "Cannot solve transformed equidimensional ODE!";
         return
      >>;
      return for each soln in ode collect
         if lhs soln = u then y = exp rhs soln % retain explicit soln
         else sub(u = log y, ode)
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Simplification of Arbitrary Constants
% =====================================

algebraic procedure ODESolve!-Simp!-ArbConsts(solns, y, x);
   %% To be applied to non-parametric solutions of ODEs containing
   %% arbconsts introduced earlier.
   for each soln in solns collect
      if symbolic rlistp soln then soln else
         (ODESolve!-Simp!-ArbConsts1(lhs soln, y, x) =
            ODESolve!-Simp!-ArbConsts1(rhs soln, y, x))$

algebraic procedure ODESolve!-Simp!-ArbConsts1(soln, y, x);
   %% Simplify arbconst expressions within soln.  Messy arbconst
   %% expressions can be introduced by the integrator from simple
   %% arbconsts, and there would appear to be no way to avoid this
   %% other than to remove them after the event.
   begin scalar !*precise, ss, acexprns;
      if not(ss := ODESolve!-Structr(soln, x, y, 'arbconst))
      then return soln;
      acexprns := rest ss;  ss := first ss;
      traceode "Simplifying the arbconst expressions in ", soln,
         " by the rewrites ...";
      for each s in acexprns do <<
         %% s has the form ansj = "expression in arbconst(n)"
         %% MAY NEED TO CHECK ONLY 1 ARBCONST?
         %% n!* must be a global algebraic variable!
         rhs s where arbconst(~n) => (n!* := n);
         traceode rhs s, " => ", arbconst(n!*); % to evaluate `rhs'
         %% Remove other occurrences of arbconst(n!*):
         ss := sub(solve(s, arbconst(n!*)), ss);
         %% Finally rename ansj as arbconst(n):
         ss := sub(lhs s = arbconst(n!*), ss)
      >>;
      return ss
   end$

symbolic operator ODESolve!-Structr$
%% symbolic procedure ODESolve!-Structr(u, x, y, arbop);
%%    %% Return an rlist consisting of an expression involving variables
%%    %% ansj representing sub-structures followed by equations of the
%%    %% form ansj = sub-structure, where the sub-structures depend
%%    %% non-trivially on the arbitrary opertor arbop, essentially in the
%%    %% format returned by structr, or nil if this decomposition is not
%%    %% possible.
%%    begin scalar !*savestructr, !*precise, ss, arbexprns;
%%       !*savestructr := t;
%%       ss := cdr structr u;              % rlistat; ss = (exprn eqns)
%%       if null cdr ss then return;
%%       %% Ignore "structures" of the form ansj = arbop(i)
%%       ss := car ss . for each s in cdr ss join
%%          if eqcar(caddr(s:=reval s), arbop)
%%          then << arbexprns := s . arbexprns; nil >>
%%          else {s};
%%       %% by substituting them back into the structure list:
%%       if arbexprns then
%%          ss := cdr subeval nconc(arbexprns, {makelist ss});
%%
%%       %% Get simplifiable arbop expressions:
%%       arbexprns := nil;
%%       ss := car ss . for each s in cdr ss join
%%          begin scalar rhs_s;  rhs_s := caddr s;
%%             return if smember(arbop, rhs_s) and
%%                not(depends(rhs_s, x) or depends(rhs_s, y))
%%             then << arbexprns := s . arbexprns; nil >>
%%             else {s}
%%          end;
%%       if null arbexprns then return;
%%       %% Rebuild the rest of the stucture as ss:
%%       ss := if cdr ss then
%%          subeval nconc(cdr ss, {car ss})
%%       else car ss;
%%       return makelist(ss . arbexprns)
%%    end$

symbolic procedure ODESolve!-Structr(u, x, y, arbop);
   %% Return an rlist representing U that consists of an expression
   %% involving variables `ansj' representing sub-structures followed
   %% by equations of the form `ansj = sub-structure', where the
   %% sub-structures depend non-trivially on the arbitrary opertor
   %% ARBOP and do not depend on X or Y, essentially in the format
   %% returned by structr, or nil if this decomposition is not
   %% possible.
   begin scalar !*savestructr, !*precise, ss, arbexprns;
      !*savestructr := t;
      ss := cdr structr u;              % rlistat; ss = (exprn eqns)
      if null cdr ss then return;

      %% Ignore trivial structure of the form ansj = arbop(i) by
      %% substituting it back into the structure list:
      ss := car ss . for each s in cdr ss join
         if eqcar(caddr(s:=reval s), arbop)
         then << arbexprns := s . arbexprns; nil >>
         else {s};
      if null cdr ss then return;
      if arbexprns then
         ss := cdr subeval nconc(arbexprns, {makelist ss});

      %% Ignore structure that does not depend on arbop by
      %% substituting it back into the structure list:
      arbexprns := nil;
      ss := car ss . for each s in cdr ss join
         if not smember(arbop, s)
         then << arbexprns := s . arbexprns; nil >>
         else {s};
      if null cdr ss then return;
      if arbexprns then
         ss := cdr subeval nconc(arbexprns, {makelist ss});

      %% Ignore all structure that depends on x or y by repeatedly
      %% substituting it back into the structure list:
      arbexprns := t;
      while arbexprns and cdr ss do <<
         arbexprns := nil;
         ss := car ss . for each s in cdr ss join
            if depends(s, x) or depends(s, y)
            then << arbexprns := s . arbexprns; nil >>
            else {s};
         if arbexprns then
            ss := cdr subeval nconc(arbexprns, {makelist ss})
      >>;
      if null cdr ss then return;

      return makelist ss
   end$

endmodule$

end$
