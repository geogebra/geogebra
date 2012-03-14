module rsolve;  % Rational and integer zeros of a univariate polynomial
                % using fast modular methods.

% Author: F.J.Wright@Maths.QMW.ac.uk
% Version 1.051, 16 Jan 1995

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


% ***** NOT QUITE COMPATIBLE WITH VERSION 1, 6 Jul 1994 *****

% More robust, compatible with solve, minor bug fixes.
% Multiplicity also computed.
% More flexible interface.

% Note the essential temporary patch at the end of this file,
% necessary with versions of REDUCE up to and including 3.5.

% DOCUMENTATION
% =============

% Defines operators r_solve and i_solve that take a single univariate
% polynomial (or polynomial equation) as argument, and optionally the
% variable as second argument, and return respectively the sets of
% rational and integer zeros.  Any denominator is completely ignored!
% See the test/demo file rsolve.tst for examples.

% Default output format is the same as used by solve (including
% respecting the multiplicities switch), but optional arguments allow
% other output formats (see below).  Solutions of degenerate equations
% are expressed by r_solve and i_solve using the operators ARBRAT
% (which is new) and ARBINT respectively.

% Computing only the integer zeros is slightly more efficient than
% extracting them from the rational zeros.  This algorithm appears to
% be faster than solve by a factor that depends on the example, but
% typically up to about 2, and gives more convenient output if only
% integer or rational zeros are required.

% The algorithm used is that described by R. Loos (1983):
% Computing rational zeros of integral polynomials by $p$-adic
% expansion.  {\it SIAM J. Computing}. {\bf 12}, 286--293.

% The switch TRSOLVE turns on tracing of the algorithm.


% Possible extensions:
%   improve root bound used;
%   map option;
%   coupled multivariate polynomials;
%   Gaussian integer and rational zeros.


% Note that modular methods do not appear to be helpful for computing
% integer square roots.  The function isqrt in arith.red, which
% effectively returns isqrt m = floor sqrt m computed using an integer
% Newton iteration, is significantly faster than any modular method
% that I have tried to develop!

create!-package('(rsolve),'(solve));

% Algebraic interface
% ===================

switch multiplicities;

global '(!!arbint multiplicities!*);
share multiplicities!*;

put('arbrat,'simpfn,'simpiden);         % Arbitrary rational operator

fluid '(!*i_solve);

put('i_solve, 'psopfn, 'i_solve!-eval);
%put('isolve, 'psopfn, 'i_solve!-eval);  % Later, maybe

symbolic procedure i_solve!-eval u;
   % Compute only integer zeros of a univariate polynomial.
   r_solve!-eval u where !*i_solve = t;

put('r_solve, 'psopfn, 'r_solve!-eval);

symbolic procedure r_solve!-eval u;
   % Algebraic interface.  Sets integer domain mode internally.
   %
   % Use: {R/I}_Solve(eqn, var, options)
   %
   % eqn: univariate equation with rational coefficients (required).
   % var: solution variable (optional).
   % options: optional keywords acting as local switches:-
   %   separate: (default) assign multiplicity list to the
   %     global variable root_multiplicities;
   %   expand or multiplicities: (default if multiplicities switch
   %     is on) expand the solution list to include multiple zeros
   %     multiple times;
   %   together: return each solution as a list whose second
   %     element is the multiplicity;
   %   nomul: do not compute multiplicities (thereby saving some time);
   %   noeqs: do not return univariate zeros as equations.

   if null u then RedErr "r/i_solve called with no equations" else
   begin scalar var, mul, noeqs;
      % Process options, after setting defaults:
      mul := if !*multiplicities then 'expand else 'separate;
      begin scalar maybe_var;  maybe_var := t;
         for each x in cdr u do <<
            if x eq 'separate then mul := 'separate
            else if x memq '(expand multiplicities) then
               mul := 'expand
            else if x eq 'together then mul := t
            else if x eq 'nomul then mul := nil
            else if x eq 'noeqs then noeqs := t
            else if maybe_var then var := x
            else typerr(x, "optional r/i_solve argument");
            maybe_var := nil >>
      end;
      return
      (begin scalar result;
         % Domain modes have nasty global properties, so ...
         if old_dmode then setdmode(old_dmode, nil) where !*msg = nil;
         result := errorset!*({'r_solve!-eval1,
            mkquote car u, mkquote var, mkquote mul, mkquote noeqs}, t);
         if old_dmode then setdmode(old_dmode, t) where !*msg = nil;
         % The errorset outputs any error messages, so exit quietly:
         if errorp result then error1() else return car result
      end) where old_dmode = dmode!*;
    end;


% Tracing facility:
switch trsolve;

%% rlistat '(tr_write);
deflist('((tr_write rlis)), 'stat);  % flagged eval!

symbolic procedure tr_write u;
   if !*trsolve then
      << for each el in u do prin2 el; terpri() >>;

symbolic procedure r_solve!-eval1(f, var, mul, noeqs);
   % Algebraic interface to r_solve, ASSUMING integer domain mode.
   begin scalar x, u;
      f := numr simp!* !*eqn2a reval f; % Should check denr?
      tr_write "Simplified poly to solve: ", f;
      if not atom f then
         % Get mainvar and check f really is a polynomial:
         if eqcar(x := mvar f, 'list) then goto error;
      if var then                       % variable specified
         if not((var := !*a2k var) eq x) then
            if x then
               if not smember(var, f) then return makelist nil
               else goto error          % variable mis-match
            else if null f then <<      % degenerate equation
               multiplicities!* := makelist nil;
               return makelist { makeqn!-maybe(var,
                  {if !*i_solve then 'arbint else 'arbrat,
                     !!arbint:=!!arbint+1}, noeqs) } >>;
      if atom f then return makelist nil;
      u := f;
      while not atom u do
         u := if mvar u eq x and fixp lc u then red u else 'error;
      if not(u eq 'error) then
         return r_solve!-output(f, r_solve f, mul, noeqs);
   error:
      %% TypErr(prepf f, "univariate poly over Z in specified var")
      TypErr(prepf f, "univariate polynomial over Z")
   end;


symbolic procedure r_solve!-output(f, zeros, mul, noeqs);
   % Assumes f is a univariate integer polynomial in standard form
   % and zeros is a list of integers or rational SQ forms.
   begin scalar x;  x := mvar f;
      return makelist
      if null mul then <<
         multiplicities!* := makelist nil;
         for each s in zeros collect makeqn!-maybe(x, !*n2a s, noeqs) >>
      else
      % Compute multiplicities and return composite list.  This simple
      % successive-differentiation algorithm is the most efficient for
      % zeros of multiplicity less than about 5, because it requires
      % essentially m polynomial evaluations per zero of multiplicty
      % m, whereas the next best algorithm based on evaluating
      % (x-a)f'/f at x=a requires one exact division, one gcd, 2
      % polynomial evaluations and one numerical division per zero.
      begin scalar solns, m;  m := 0;
         % Construct a list of the form ( (soln . mult) ... ):
         while zeros do <<
            f := diff(f,x);  m := m + 1;
            zeros := for each z in zeros join if
                  (if !*i_solve then zerop eval_uni_poly(f,z) % integer.
                  else null numr eval_uni_poly_Q(f, z) ) % rational
               then {z} else << solns := (z . m) . solns; nil >>
            >>;
         solns := for each s in solns collect
            makeqn!-maybe(x, !*n2a car s, noeqs) . cdr s;
         multiplicities!* := nil;
         if mul eq 'separate then << % mul = separate (default)
            solns := for each s in solns collect
               <<multiplicities!* := cdr s . multiplicities!*; car s>>;
            multiplicities!* := reverse multiplicities!* >>
         else if mul eq 'expand then % cf. on multiplicities
            solns := for each s in solns join
               for i := 1 : cdr s collect car s
         else                           % mul = together
            solns := for each s in solns collect
               makelist {car s, cdr s};
         multiplicities!* := makelist multiplicities!*;
         return solns
      end
   end;

symbolic procedure makeqn!-maybe(x, s, noeqs);
   if noeqs then s else {'equal, x, s};

symbolic procedure !*n2a n;
   % Convert an integer or rational SQ to algebraic (prefix) form:
   if fixp n then n else !*q2a n;


% Modular arithmetic support, based on module alg/genmod
% ======================================================
% Avoids a few tests that are unnecessary for this application
% (I hope!).  Must be here because some definitions are smacros!

symbolic procedure mod!# n;
   % Reduce integer n modulo current!-modulus,
   % i.e. to range 0 <= n < current!-modulus:
   (if n_m < 0 then n_m + current!-modulus else n_m)
      where n_m = remainder(n, current!-modulus);

symbolic smacro procedure mod!+(a,b);
   % Sum of two positive modular numbers:
   general!-modular!-plus(a,b);

symbolic smacro procedure mod!*(a,b);
   % Product of two positive modular numbers:
   remainder(a*b, current!-modulus);

symbolic procedure mod!/(a,b);
   % Quotient of two positive modular numbers:
   mod!*(a, general!-reciprocal!-by!-gcd(current!-modulus,b,0,1));

symbolic smacro procedure mod!^(a,n);
   % Natural number power of a modular number:
   general!-modular!-expt(a,n);


% The main R_SOLVE procedure
% ==========================

fluid '(!*ezgcd !*gcd current!-modulus modulus!/2);

symbolic procedure r_solve f;
   % Returns the distinct RATIONAL zeros of a univariate integer
   % polynomial f by using quadratic Hensel lifting,
   % where f(x) = SUM_{i=0}^n a_i x^i = 0.
   % Assumes f is a standard form.
   % If !*i_solve then computes only INTEGER zeros.
   begin scalar x, f1, fm, f1m, L;
      scalar a_n, a_0, p, b, N, p!^N, !2!^r, !2!^r1, p2r, p2r1;
      scalar !*ezgcd, !*gcd, current!-modulus, modulus!/2;
      x := mvar f;  !*gcd := t;

      % Take squarefree factor (which removes numerical content)
      % and compute its derivative:
      f := (quotf!*(f, gcdf(f, diff(f,x))) where !*ezgcd = t);
      f1 := diff(f,x);
      tr_write "Squarefree poly: ", f;

      % Find abs leading coeff:
      a_n := abs lc f;
      % Find abs trailing coeff (not just constant term):
      a_0 := f;  while not atom a_0 and red a_0 do a_0 := red a_0;
      if not atom a_0 then a_0 := lc a_0;  a_0 := abs a_0;
      tr_write "Abs leading coeff: ", a_n,
            ",  abs trailing coeff: ", a_0;

      % Choose p as the smallest prime such that f
      % retains its degree and remains squarefree:
      % (Let the algebraic processor do the work here!)
      begin scalar old_mod, dmode!*;
         dmode!* := '!:mod!:;
         old_mod := setmod(p := 2);
         while zerop remainder(a_n, p) or
               gcdf(fm := resimpf f, resimpf f1) neq 1 do
            setmod(p := nextprime p);
         %% f1m := resimpf f1;
         setmod old_mod
      end;
      % Simplify back to integer domain mode:
      fm := resimpf fm;  %% f1m := resimpf f1m;
      tr_write "Prime modulus: ", p;
      tr_write "Poly mod ", p, ": ", fm;
      %% tr_write "Deriv mod ", p, ": ", f1m;

      % Find zero set of f mod p by simple exhaustive search:
      current!-modulus := p;
      for xx := 0 : p-1 do
         if zerop mod_eval_uni_poly(fm, xx) then L := xx . L;
      tr_write "Zero set mod ", p, ": ", L;
      if null L then return nil;

      % Determine modular prime power bound:
      b := 2 * if !*i_solve then a_0 else a_n*a_0;
      N := 1;  p!^N := p;
      while p!^N <= b do << N := N + 1;  p!^N := p * p!^N >>;
      tr_write "Modular prime power bound: ", N;

      % Lift to zeros of f mod p^N by quadratic Hensel lifting:
      !2!^r := 1;  p2r := p;            % r := 0
      while !2!^r < N do <<
         %% !2!^r1 := 2*!2!^r;  p2r1 := p2r^2;
         p2r1 := if (!2!^r1 := 2*!2!^r) < N then p2r^2
            else << !2!^r1 := N;  p!^N >>;
         tr_write "****************************************";
         tr_write "Lift modulo prime power ", p, "^", !2!^r1,
            " = ", p2r1;
         L := for each alpha_r in L collect
         begin integer fm, b_r, f1m, y;
            tr_write "Current zero mod ",p,"^",!2!^r,": ",alpha_r;
            current!-modulus := p2r1;
            fm := mod_eval_uni_poly(f, alpha_r);
            tr_write "fm: ", fm;
            if zerop fm then return alpha_r;
            b_r := fm/p2r;  % exact (non-modular) division
            current!-modulus := p2r;
            f1m := mod_eval_uni_poly(f1, alpha_r);
            tr_write "b_r: ", b_r, ",  f1m: ", f1m;
            % y := - b_r / f1m;
            % Must do this division mod p2r:
            y := mod!/(p2r - b_r, f1m);
            tr_write "y = -b_r/f1m mod ", p2r, ": ", y;
            return remainder(alpha_r + p2r*y, p2r1)
         end;
         tr_write "Zero set mod ", p, "^", !2!^r1, ": ", L;
         !2!^r := !2!^r1;  p2r := p2r1
      >>;

      return if !*i_solve then
         % Check for genuine (non-modular) integer roots:
         for each alpha in L join
            (if zerop eval_uni_poly(f, a) then {a})
               where a = balance_mod(alpha, p2r)
      else
         %% Reconstruct, using alpha = s/t mod p^N and t | a_n, so
         %% s/t = (alpha * a_n bal_mod p^N)/a_n canonicalized
         %% (because a_n = a_n bal_mod p^N by choice of N)
         %% and check genuine rational roots, because e.g.
         %% x^2 + 2 has zeros {1,2} mod 3, but no rational zeros.
         %% balance_mod is an odd function, i.e. commutes with sign
         %% change, so we can use a_n := abs lc f as computed above:
         for each alpha in L join <<
            if zerop alpha then alpha := nil ./ 1
            else <<
               alpha := balance_mod(remainder(alpha*a_n, p2r), p2r);
               alpha := ((alpha/g)./(a_n/g) where g = gcdn(alpha, a_n))
            >>;
            if null numr eval_uni_poly_Q(f, alpha) then {alpha} >>
   end;


% Support procedures
% ==================

symbolic procedure resimpf f;
   % Resimplify a standard form.
   numr subf1(f, nil) where varstack!*=nil;

symbolic procedure balance_mod(n, m);
   % Shift n to balanced range modulo m
   % [-floor((m-1)/2), ceil((m-1)/2)]
   % (cf. modprep!: in POLY.RED)
   if n + n > m then n - m else n;

symbolic procedure eval_uni_poly(f, a);
   % Evaluate univariate integer polynomial
   % f(x) at x = a using Horner's rule.
   % f : standard form; a : integer
   % Returns an integer.
   if atom f then f else
   begin scalar r, d;
      r := lc f;  d := ldeg f;  f := red f;
      while not atom f do <<
         r := r*a^(d - ldeg f) + lc f;
         d := ldeg f;  f := red f
      >>;
      r := r*a^d;  if f then r := r + f;
      return r
   end;

symbolic procedure eval_uni_poly_Q(f, a);
   % Evaluate univariate integer polynomial
   % f(x) at x = a using Horner's rule.
   % f : standard form; a : rational number as standard quotient
   % Returns a rational number as a standard quotient.
   if atom f then f ./ 1 else
   begin scalar r, d;
      r := lc f ./ 1;  d := ldeg f;  f := red f;
      while not atom f do <<
         r := addsq(multsq(r, exptsq(a,(d - ldeg f))), lc f ./ 1);
         d := ldeg f;  f := red f
      >>;
      return addsq(multsq(r, exptsq(a,d)), f ./ 1);
   end;

symbolic procedure mod_eval_uni_poly(f, a);
   % Evaluate modulo current!-modulus the univariate
   % integer polynomial f(x) at x = a using Horner's rule.
   % f : standard form; a : cardinal, 0 <= a < current!-modulus.
   if atom f then mod!# f else
   begin scalar r, d;
      r := mod!# lc f;  d := ldeg f;  f := red f;
      while not atom f do <<
         r := mod!+(mod!*(r, mod!^(a, d - ldeg f)), mod!# lc f);
         d := ldeg f;  f := red f
      >>;
      r := mod!*(r, mod!^(a,d));  if f then r := mod!+(r, mod!# f);
      return r
   end;

endmodule;

end;
