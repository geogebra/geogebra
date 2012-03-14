module randpoly;  % A random (generalized) polynomial generator

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


% F.J.Wright@Maths.QMW.ac.uk, 14 July 1994

% Based on a port of the Maple randpoly function.
% See RANDPOLY.TEX for documentation, and RANDPOLY.TST for examples.

create!-package('(randpoly),'(contrib misc));

symbolic smacro procedure apply_c c;
   % Apply a coefficient generator function c that returns
   % a prefix form and convert it to standard quotient form.
   simp!* apply(c, nil);

symbolic procedure apply_e e;
   % Apply an exponent generator function e
   % and check that it has returned an integer.
   if fixp(e := apply(e, nil)) then e else
      RedErr "randpoly expons function must return an integer";

put('randpoly, 'simpfn, 'randpoly);

flag('(randpoly), 'listargp);  % allow single list argument

symbolic procedure randpoly u;
   % Use: randpoly(vars, options)
   % vars: expression or list of expressions -- usually indeterminates
   % options: optional equations (of the form `option = value')
   % or keywords specifying properties:
   %
   %    Option   Use                                   Default Value
   %    ------   ---                                   -------------
   %    coeffs = procedure to generate a coefficient   rand(-99 .. 99)
   %    expons = procedure to generate an exponent     rand(6)
   %               (must return an integer)
   %    degree, deg or maxdeg = maximum total degree   5
   %    ord or mindeg = minimum total degree           0 (or 1)
   %      (defaults to 1 if any "variable" is an equation)
   %    terms  = number of terms generated if sparse   6
   %    dense    the polynomial is to be dense         sparse
   %    sparse   the polynomial is to be sparse        sparse

   begin scalar v, univar, c, e, trms, d, o, s, p, sublist;

      % Default values for options:
      %% c := rand {-99 .. 99};  % rand is a psopfn
      c := function(lambda(); random(199) - 99);
      d := 5;  o := 0;  trms := 6;  s := 'sparse;

      % Evaluate arguments and process "variables":
      begin scalar wtl!*;
         % Ignore weights when evaluating args, including in revlis.
         v := car(u := revlis u);
         v := if eqcar(v, 'list) then cdr v else
            << univar := t; v . nil >>;
         v := for each vv in v collect
            begin scalar tmpvar;
               if eqexpr vv then << vv := !*eqn2a vv;  o := 1 >>
               else if kernp simp!* vv then return vv;
               tmpvar := gensym();
               sublist := {'equal, tmpvar, vv} . sublist;
               return tmpvar
            end;
         if univar then v := car v
      end;

      % Process options:
      for each x in cdr u do
         if x eq 'dense or x eq 'sparse then s := x
         else if not (eqexpr x and
            (if cadr x eq 'coeffs and functionp caddr x then
               c := caddr x
            else if cadr x eq 'expons and functionp caddr x then
               e := caddr x
            else if cadr x memq '(degree deg maxdeg) and
               natnump caddr x then d := caddr x
            else if cadr x memq '(ord mindeg) and
               natnump caddr x then o := caddr x
            else if cadr x eq 'terms and natnump caddr x then
               trms := caddr x))
         then typerr(x, "optional randpoly argument");

      % Generate the random polynomial:
      p := nil ./ 1;
      if o <= d then
      if s eq 'sparse then
         if null e then
            for each x in rand!-mons!-sparse(v,trms,d,o,univar) do
               p := addsq(p, multsq(apply_c c, x ./ 1))
         else
            if univar then
               for i := 1 : trms do
                  p := addsq(p, multsq(apply_c c,
                                       !*kp2q(v, apply_e e)))
            else
               for i := 1 : trms do
                  (if numr cc then p := addsq(p, <<
                     for each vv in v do
                        cc := multsq(cc, !*kp2q(vv, apply_e e));
                     cc >> ))
                  where cc = apply_c c
      else   % s eq 'dense
         if univar then <<  p := apply_c c;
            if o > 0 then p := multsq(p, mksq(v,o));
            for i := o + 1 : d do
               p := addsq(p, multsq(apply_c c, mksq(v,i))) >>
         else
            for each x in rand!-mons!-dense(v,d,o) do
               p := addsq(p, multsq(apply_c c, x ./ 1));
      return
         % Make any necessary substitutions for temporary variables:
         if sublist then
            simp!* subeval append(sublist, {mk!*sq p})
         else p
   end;

symbolic procedure functionp f;
   % Returns t if f can be applied as a function.
   getd f or eqcar(f,'lambda);

symbolic procedure natnump n;
   % Returns t if n is a natural number.
   fixp n and n >= 0;

symbolic smacro procedure kp2f(k, p);
   % k : unique kernel, p : natural number > 0
   % Returns k^p as a standard form, taking account of
   % both asymptotic let rules and weightings.
   numr mksq(k, p);

symbolic procedure !*kp2f(k, p);
   % k : unique kernel, p : natural number
   % Returns k^p as a standard form, taking account of
   % both asymptotic let rules and weightings.
   if p > 0 then kp2f(k, p) else 1;

symbolic procedure !*kp2q(k, p);
   % k : unique kernel, p : any integer
   % Returns k^p as a standard quotient, taking account of
   % both asymptotic let rules and weightings.
   if p > 0 then mksq(k, p)
   else if zerop p then 1 ./ 1
   else
      % Is this the right behaviour?
      % cf. part of procedure invsq in POLY.RED
      if null numr(k := mksq(k, -p)) then RedErr "Zero divisor"
      else revpr k;


symbolic procedure rand!-mons!-dense(v, d, o);
   % v : list of variables,
   % d : max total degree, o : min total degree.
   % Recursively returns a dense list of multivariate monomials
   % with total degree in [o, d] as STANDARD FORMS.
   begin scalar v_1;  v_1 := car v;  v := cdr v;
      return
         if null v then  % single variable
            (if o > 0 then kp2f(v_1,o) else 1)
            . for i := o + 1 : d collect kp2f(v_1,i)
         else append( rand!-mons!-dense(v, d, o),
            for i := 1 : d join
               (for each x in rand!-mons!-dense(v, d - i, max(0, o - i))
                  collect multf(v_1!^i, x))
                     where v_1!^i = kp2f(v_1,i) )
   end;


symbolic procedure rand!-mons!-sparse(v, trms, d, o, univar);
   % v : (list of) variable(s), trms: number of terms,
   % d : max total degree, o : min total degree.
   % Returns a sparse list of at most trms monomials
   % with total degree in [o, d] as STANDARD FORMS.
   begin scalar n, v_1, maxtrms, otrms, s;
      if univar then maxtrms := d + 1 - o else <<
         n := length v;  v_1 := car v;
         otrms := if zerop o then 0 else binomial(n + o - 1, n);
            % max # terms to degree o-1:
         maxtrms := binomial(n + d, n) - otrms
            % max # terms in poly := max # terms to degree d - otrms
      >>;
      % Choose a random subset of the maxtrms terms by "index":
      s := rand!-comb(maxtrms, min(maxtrms,trms));
      return
      if univar then
         for each ss in s collect !*kp2f(v, ss + o)
      else
         for each ss in s collect
         begin scalar p;  p := 1;
            % Convert term "index" to exponent vector:
            ss := nil . inttovec(ss + otrms, n);
            for each vv in v do
               p := multf(!*kp2f(vv, car(ss := cdr ss)), p);
            return p
         end
   end;


% Support procedures for randpoly
% ===============================

global '(!_BinomialK !_BinomialB !_BinomialN);

% binomial in the specfn package is implemented as an algebraic
% operator, and I suspect is not very efficient.  It will not clash
% with the following implementation, which is about 50% faster on
% my PC for binomial(200, 100) (with its caching disabled);

symbolic procedure binomial(n, k);
   % Returns the binomial coefficient ASSUMING n, k integer >= 0.
   begin scalar n1, b;
      % Global !_BinomialK, !_BinomialB, !_BinomialN
      if k = 0 then return 1;
      if n < 2*k then return binomial(n,n-k);
      n1 := n+1;
      if !_BinomialN = n then <<        % Partial result exits ...
         b := !_BinomialB;
         if !_BinomialK <= k then
            for i := !_BinomialK+1 : k do
               b := quotient((n1-i)*b,i)
         else
            for i := !_BinomialK step -1 until k+1 do
               b := quotient(i*b,n1-i) >>
      else <<                           % First binomial computation
         b := 1;
         for i := 1 : k do
            b := quotient((n1-i)*b,i);
         !_BinomialN := n >>;
      !_BinomialK := k;
      return !_BinomialB := b
   end;


symbolic procedure rand!-comb(n, m);
   % Returns a list containing a random combination of m of the
   % first n NATURAL NUMBERS, ASSUMING integer n >= m >= 0.
   % (The values returned are 1 less than those
   % returned by the Maple randcomb function.)
   if m = n then for i := 0 : m - 1 collect i
   else begin scalar s;
      if n - m < m then
      begin scalar r;
         r := rand!-comb(n, n - m);
         for rr := 0 : n - 1 do
            if not(rr member r) then s := rr . s
      end
      else
         for i := 0 : m - 1 do
         begin scalar rr;
            while (rr := random n) member s do; % nothing
            s := rr . s
         end;
      return s
   end;


symbolic procedure inttovec(m, n);
   % Returns the m'th (in lexicographic order) list of n
   % non-negative integers, ASSUMING integer m >= 0, n > 0.
   inttovec1(n, inttovec!-solve(n,m));

symbolic procedure inttovec1(n, dm);
   % n > 0 : integer;  dm : dotted pair, d . m', where
   % d = norm of vector in N^n and m' = index of its tail in N^{n-1}.
   % Returns list representing vector in N^n, constructed recursively.
   % First vector element v_1 satisfies v_1 = d - norm of tail vector.
   if n = 1 then car dm . nil else
      ( (car dm - car dm1) . inttovec1(n - 1, dm1) )
         where dm1 = inttovec!-solve(n - 1, cdr dm); % dotted pair

symbolic procedure inttovec!-solve(n, m);
   % n > 0, m >= 0 : integer
   % Main subalgorithm to compute the vector in N^n with index m.
   % Returns as a dotted pair d . m' the norm (total degree) d in N^n
   % and the index m' of the tail sub-vector in N^{n-1}.
   % d is computed to satisfy ^{d-1+n}C_n <= m < ^{d+n}C_n,
   % where ^{d+n}C_n = number of n-vectors of norm d.
   if m = 0 or n = 1 then m . 0 else
   begin scalar d, c, cc;
      d := 0;  cc := 1;  % cc = ^{d+n}C_n
      repeat <<
         c := cc;  d := d + 1;          % c  = ^{d+n}C_n
         cc := quotient((n + d)*c, d);  % cc = ^{d+1+n}C_n
      >> until cc > m;
      return d . (m - c)
   end;


% Support for anonymous procedures (`proc's),
% ==========================================
% especially random number generators.
% ===================================

% Based partly on Maple's proc and rand function, and intended mainly
% for use with the randpoly operator.

% Interval code based on numeric.red and gnuplot.red by Herbert Melenk.
% Create .. infix operator, avoiding warning if already defined.
% (It is pre-defined via plot/plothook.sl at least in PSL-REDUCE.)

newtok '( (!. !.) !*interval!*);

precedence .., or;

algebraic operator ..;

put('!*interval!*, 'PRTCH, '! !.!.! );

put('rand, 'psopfn, 'rand);

symbolic procedure rand u;
   % Returns a random number generator, and compiles it if COMP is on.
   % Optional second argument generates a named procedure.
   if null u or (cdr u and cddr u) then
      RedErr "rand takes 1 or 2 arguments"
   else begin scalar fname, fn;
      if cdr u and not idp(fname := reval cadr u) then
         typerr(fname, "procedure name");
      fn := if fixp(u := reval car u) and u > 0 then {'random, u}
      else if eqcar(u,'!*interval!*) then
      begin scalar a, b;
        if not(fixp(a := cadr u) and fixp(b := caddr u) and a<b) then
        RedErr
        "rand range argument a .. b must have integer a,b with a < b";
        return if zerop a then {'random, b + 1}
           else {'plus2, a, {'random, b - a + 1}}
      end
      else typerr(u, "integer or integer range");
      fn := {'lambda, nil, fn};
      %% putd compiles the function if !*comp is non-nil.
      return if fname then
         << flag({fname}, 'opfn); putd(fname, 'expr, fn) >>
      else if !*comp then putd(gensym(), 'expr, fn)
      else fn
   end;


% Redefine the algebraic-mode random operator.
remflag('(random), 'opfn);
put('random, 'psopfn, 'evalrandom);

symbolic procedure evalrandom u;
   % More flexible interface to the random function.
   if null u or cdr u then RedErr "random takes a single argument"
   else if eqcar(u := reval car u,'!*interval!*) then
   begin scalar a, b;
     if not(fixp(a := cadr u) and fixp(b := caddr u) and a < b) then
     RedErr
     "random range argument a .. b must have integer a,b with a < b";
     return if zerop a then random(b + 1) else a + random(b - a + 1)
   end
   else if fixp u and u > 0 then random u
      % N.B. random also makes this check, but does not accept a range
   else typerr(u, "integer or integer range");


% Proc turns its argument expression into a lambda expression
% and compiles it if COMP is ON.  Provides a general version of rand.
% Some such mechanism is necessary to prevent expressions containing
% random number generators being evaluated too early.

put('proc, 'psopfn, 'proc);

symbolic procedure proc u;
   % Returns an anonymous procedure definition,
   % compiled if COMP is ON.
   if null u then RedErr "proc requires at least a body argument"
   else <<
      % aeval!* necessary instead of aeval here to avoid caching
      % and hence loss of possible randomness within loops:
      u := {'lambda, nil, {'aeval!*, mkquote car u}};
      if !*comp then putd(gensym(), 'expr, u) else u
   >>;


% User access procedures to evaluate and display procs etc.
% ========================================================
% Not necessary -- provided only for test purposes and curious users!

put('evalproc, 'psopfn, 'evalproc);

symbolic procedure evalproc r;
   % r : proc, arg_1, ..., arg_n; args optional
   % Evaluates a proc applied to the subsequent arguments.
   apply(getproc car r, revlis cdr r);

put('showproc, 'psopfn, 'showproc);

symbolic procedure showproc r;
   % Displays a proc.
   (if codep rr then
      RedErr "Argument is a compiled proc -- cannot display"
   else << terpri(); rprint subst('plus, 'plus2, rr); >>)
      where rr = getproc car r;

symbolic procedure getproc r;
   % Support procedure: get a proc body.
   (  if idp r then
         getfnbody r or
         ( (r := get(r, 'avalue)) and car r eq 'scalar
            and eqcar(r := cadr r, 'lambda) and r ) or
         getfnbody r
      else if pairp r then
         if car r eq 'lambda then r  % share variable
         else if eqcar(r := ( (if x then apply(x, {cdr r}))
            where x = get(car r, 'psopfn) ), 'lambda) then r )
   or RedErr "Argument is not a proc";

symbolic procedure getfnbody r;
   (r := getd r) and cdr r;

endmodule;

end;
