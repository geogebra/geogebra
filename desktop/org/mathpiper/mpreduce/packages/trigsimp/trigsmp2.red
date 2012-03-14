module trigsmp2$  % TrigSimp executable code

% small revision by Winfried Neun 3. Nov. 2008
% need to take care of the dependencies (depl*) in case a df is in the form,
% e.g. trigsimp(cos((f1 - f2)/4)**4*df(f1,x,y),sin);

% Revised by Francis J. Wright <f.j.wright@maths.qmul.ac.uk>
% Revision Time-stamp: <FJW, 07 November 2008>

% (FJW) To do:
%   check with non-integer number domains

% These variables control rules in trigsmp1:
share hyp_preference, trig_preference$

fluid '(!*complex dmode!*)$


%%%%%%%%%%%%
% TrigSimp %
%%%%%%%%%%%%

fluid '(depl!*);

symbolic procedure trigsimp!*(u);
   (trigsimp(reval car u, revlis cdr u) where depl!* = depl!*);

put('trigsimp, 'psopfn, 'trigsimp!*)$

%% FJW: trigsimp is defined to autoload

symbolic procedure trigsimp(f, options);
   %% Map trigsimp1 over possible structures:
   if atom f then f                     % nothing to simplify!
   else if car f eq 'equal then         % equation
      'equal . for each ff in cdr f collect trigsimp(ff, options)
   else if car f eq 'list then          % list
      'list . for each ff in cdr f collect trigsimp(ff, options)
   else if car f eq 'mat then           % matrix
      'mat . for each ff in cdr f collect
         for each fff in ff collect trigsimp(fff, options)
   else trigsimp1(f, options);          % scalar


symbolic procedure trigsimp1(f, options);
   %% The main TrigSimp driver.
   begin scalar dname, trigpreference, hyppreference,
         tanpreference, tanhpreference,
         direction, mode, keepalltrig, onlytan, opt_args;

      onlytan := not or(smember('sin,f), smember('cos,f),
         smember('sinh,f), smember('cosh,f),
         smember('csc,f), smember('sec,f),
         smember('csch,f), smember('sech,f));

      %% Return quickly if simplification not appropriate:
      if onlytan and not or(smember('tan,f), smember('cot,f),
         smember('tanh,f), smember('coth,f),
         smember('exp,f), smember('e,f)) then
         return f;

      if (dname := get(dmode!*, 'dname)) then <<
         %% Force integer domain mode:
         off dname;
         f := prepsq simp!* f
      >>;

      %% Process optional arguments:
      for each u in options do
         if u memq '(sin cos) then
            if trigpreference then
               (u eq trigpreference) or
                  RedErr "Incompatible options: use either sin or cos."
            else trigpreference := u
         else if u memq '(sinh cosh) then
            if hyppreference then
               (u eq hyppreference) or
                  RedErr "Incompatible options: use either sinh or cosh."
            else hyppreference := u
         else if u eq 'tan then tanpreference := t
         else if u eq 'tanh then tanhpreference := t
         else if u memq '(expand combine compact) then
            if direction then
               (u eq direction) or
                  RedErr "Incompatible options: use either expand or combine or compact."
            else direction := u
         else if u memq '(hyp trig expon) then
            if mode then
               (u eq mode) or
                  RedErr "Incompatible options: use either hyp or trig or expon."
            else mode := u
         else if u eq 'keepalltrig then keepalltrig := t
         else if eqcar(u, 'quotient) and not(u member opt_args) then
            %% optional trig arg of the form `x/2'
            opt_args := u . opt_args
         else
            RedErr {"Option", u, "invalid.", " Allowed options are",
               "sin or cos, tan, cosh or sinh, tanh,",
               "expand or combine or compact,",
               "hyp or trig or expon, keepalltrig."};

      %% Set defaults and globals:
      if trigpreference then
         (if tanpreference then         % reverse trig preference
            trigpreference := if trigpreference eq 'sin then 'cos else 'sin)
      else
         trigpreference := 'sin;
      trig_preference := trigpreference;

      if hyppreference then
         (if tanhpreference then        % reverse hyp preference
            hyppreference := if hyppreference eq 'sinh then 'cosh else 'sinh)
      else
         hyppreference := 'sinh;
      hyp_preference := hyppreference;

      direction or (direction := 'expand);

      %% Application:
      %% algebraic let trig_normalize!*;
      if trigpreference eq 'sin
      then algebraic let trig_normalize2sin!*
      else algebraic let trig_normalize2cos!*;
      if hyppreference eq 'sinh
      then algebraic let trig_normalize2sinh!*
      else algebraic let trig_normalize2cosh!*;

      %% f := algebraic f;

      if not keepalltrig or direction memq '(combine compact)
      then f := algebraic(f where trig_standardize!*);

      if mode then f :=
         if mode eq 'trig then
            behandle algebraic(f where hyp2trig!*)
         else if mode eq 'hyp then
            << f := behandle(f);  algebraic(f where trig2hyp!*) >>
         else if mode eq 'expon then
            algebraic(f where trig2exp!*);

      if direction eq 'expand then
         algebraic(begin scalar u;
            %% Handling of dependent variables
            let trig_expand_addition!*;
            %% f := f;
            symbolic(u := subs_symbolic_multiples(reval f, opt_args));
            symbolic(f := car u);       % substituted term
            let trig_expand_multiplication!*;
            f := sub(symbolic cadr u, f); % unsubstitution equations
            clearrules trig_expand_addition!*,
               trig_expand_multiplication!*
         end)
      else if direction eq 'combine then <<
         f := algebraic(f where trig_combine!*);
         if onlytan and keepalltrig then
            f := algebraic(f where subtan!*)
      >>;
      %% algebraic clearrules(trig_normalize!*);
      algebraic clearrules trig_normalize2sin!*, trig_normalize2cos!*,
         trig_normalize2sinh!*, trig_normalize2cosh!*;

      if direction eq 'compact then algebraic <<
         load_package compact;
         %% f := f where trig_expand!*;
         f := (f where trig_expand_addition!*,
            trig_expand_multiplication!*);
         f := compact(f, {sin(x)**2+cos(x)**2=1})
      >>;

      if tanpreference then
         f := if trigpreference eq 'sin then
            algebraic(f where sin ~x => cos x * tan x)
         else
            algebraic(f where cos ~x => sin x / tan x);
      if tanhpreference then
         f := if hyppreference eq 'sinh then
            algebraic(f where sinh ~x => cosh x * tanh x)
         else
            algebraic(f where cosh ~x => sinh x / tanh x);

      if dname then <<
         %% Resimplify using global domain mode:
         on dname;
         f := prepsq simp!* f
      >>;

      return f
   end;


symbolic procedure more_variables(a, b);
   length find_indets(a, nil) > length find_indets(b, nil);

symbolic procedure find_indets(term, vars);
   % Watch out!!!  Expect to see the exponential function as "e" here
   if numberp term then vars            % FJW number (integer)
   else if atom term then               % FJW variable
      (if not memq(term, vars) then term . vars)
   else if cdr term then <<             % FJW examine function arguments only
      term := cdr term;
      vars := find_indets(car term, vars);
      if cdr term then find_indets(cdr term, vars) else vars
   >> else                              % FJW nullary function
      find_indets(car term, vars);


% auxiliary variables

algebraic operator auxiliary_symbolic_var!*$

symbolic procedure subs_symbolic_multiples(term, opt_args);
   %% This procedure replaces trig arguments in `term' that differ
   %% only by a (rational) numerical factor by their lowest common
   %% denominator, e.g. x/3 and x/4 would be replaced by x' = x/12, so
   %% that x/3 -> 4x' and x/4 -> 3x'.
   %% Assumes `term' is a prefix expression.
   %% `opt_args' is an initial list of user-specified trig arguments.
   %% Returns a Lisp list:
   %%   {substituted term, unsubstitution equation list}.
   if term = 0 then '(0 (list)) else
   begin scalar arg_list, unsubs, j;
      opt_args := union(opt_args, nil); % make into set
      arg_list := get_trig_arguments(term, opt_args);
      arg_list := for each arg in arg_list collect simp!* arg;
      j := 0;
      while arg_list do
      begin scalar x, x_nu, x_lcm;
         j := j + 1;
         x := car arg_list;
         x_lcm := denr(x_nu := numberget x); % integer

         %% Find args that differ only by a numerical factor, and find
         %% the lcm of their denominators.  Delete args that have been
         %% processed.
         begin scalar tail;  tail := arg_list;
            while cdr tail do
            begin scalar y, q, y_den;
               y := cadr tail;  q := quotsq(x, y);
               if atom numr q and atom denr q then <<
                  y_den := integer_content denr y;
                  %% Integer arithmetic, division guaranteed:
                  x_lcm := (x_lcm * y_den) / gcdn(x_lcm,y_den);
                  %% Delete the argument:
                  cdr tail :=  cddr tail
               >> else
                  tail := cdr tail
            end
         end;

         arg_list := cdr arg_list;

         if x_lcm neq 1 then <<
            x := !*q2a quotsq(x, x_nu); % primitive part
            depl!* := append(depl!*,
                sublis(list (reval x .
                        list('auxiliary_symbolic_var!*,j)),depl!*));
% in case of a df(x,...) in the term. This would be nullified. WN
            term := algebraic
               sub(x = auxiliary_symbolic_var!*(j)*x_lcm, term);
            unsubs := algebraic(auxiliary_symbolic_var!*(j) = x/x_lcm)
               . unsubs;
         >>
      end;
      return {term, 'list . unsubs}
   end;

symbolic procedure behandle ex;
   begin scalar n, d;
      %% FJW: Force (exp x)^n + (exp(-x))^n to simplify:
      ex := algebraic(ex where pow2quot!*);
      %% (Appears to have been unnecessary before REDUCE 3.7.)
      ex := simp!* ex;
      n := mk!*sq (numr ex ./ 1);
      d := mk!*sq (denr ex ./ 1);
      return algebraic((n where exp2trig1!*)/(d where exp2trig2!*))
   end;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% General support routines %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure get_trig_arguments(term, args);
   %% Return a SET of all the arguments of the trig functions in the
   %% expression.  (Note that trig functions are unary!)  The
   %% arguments may themselves be general expressions -- they need not
   %% be kernels!
   if atom term then args else
   begin scalar f, r;
      f := car term;                    % function or operator
      % Winfried Neun, 1 May 2008: you might in very special cases
      % enter with equations which contain *SQs. These equations are
      % not perfectly reval'ed to prefix form. This is special for
      % equations and intentional, I think.  So...
      if (f = '!*sq) then << term := reval term; f := car term >>;
      r := cdr term;                    % arguments or operands
      if f memq '(sin cos sinh cosh) then return
         if not member(r := car r, args) then r . args else args;
      for each j in r do args := get_trig_arguments(j, args);
      return args
   end;

put('numberget, 'simpfn, 'numberget!-simpfn)$
symbolic procedure numberget!-simpfn p;
   %% Return the rational numeric content of a rational expression.
   %% Algebraic-mode interface.
   %% Cannot assume a numeric denominator!
   numberget simp!* car p;

symbolic procedure numberget p;
   %% Return the rational numeric content of a rational expression.
   %% Input and output in standard quotient form.
   %% Assume integer domain mode.
   %% Cannot assume a numeric denominator!
   begin scalar n, d, g;
      n := integer_content numr p;
      d := integer_content denr p;
      g := gcdn(n,d);
      return (n/g) ./ (d/g)
   end;

% FJW: The following numeric content code is modelled on that in
% poly/heugcd by James Davenport & Julian Padget.

symbolic procedure integer_content p;
   %% Extract INTEGER content of (multivariate) polynomial p in
   %% standard form, assuming default (integer) domain mode!
   if atom p then
      p or 0
   else if atom red p then
      if red p then gcdn(integer_content lc p, red p)
      else integer_content lc p
   else integer_content1(red red p,
      gcdn(integer_content lc p, integer_content lc red p));

symbolic procedure integer_content1(p,a);
   if a=1 then 1
   else if atom p then
      if p then gcdn(p,a) else a
   else integer_content1(red p,
      gcdn(remainder(integer_content lc p,a), a));


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% TrigGCD and TrigFactorize %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic operator degree$
symbolic procedure degree(p,x);
   %% cf. deg in poly/polyop
   if numr(p := simp!* p) then
      numrdeg(numr p, x) - numrdeg(denr p, x)
   else 'inf;

symbolic procedure balanced(p,x);
   %% cf. deg in poly/polyop
   <<
      p := simp!* p;
      numrdeg(numr p, x) = 2*numrdeg(denr p, x)
   >>;

symbolic procedure coordinated(p, x);
   %% FJW: Returns t if p contains only even or only odd degree terms
   %% wrt x; returns nil if p contains both even and odd degree terms.
   begin scalar kord!*, evendeg, coord;
      kord!* := {x := !*a2k x};
      p := reorder numr simp!* p;
      if domainp p or not(mvar p eq x) then return t; % degree = 0
      evendeg := remainder(ldeg p, 2) = 0; % leading degree is even
      p := red p;
      coord := t;
      while p and coord do
         if domainp p or not(mvar p eq x) then <<
            coord := evendeg eq t;         % degree = 0
            p := nil
         >> else <<
            coord := evendeg eq (remainder(ldeg p, 2) = 0);
            p := red p
         >>;
      return coord
   end;

flag ('(balanced coordinated), 'boolean)$

algebraic procedure trig2ord(p,x,y);
   if not balanced(p,x) or not balanced(p,y) then
      RedErr "trig2ord error: polynomial not balanced."
   else if not coordinated(p,x) or not coordinated(p,y) then
      RedErr "trig2ord error: polynomial not coordinated."
   else sub(x=sqrt(x), y=sqrt(y), x**degree(p,x)*y**degree(p,y)*p);

algebraic procedure ord2trig(p,x,y);
   x**(-degree(p,x))*y**(-degree(p,y))*sub(x=x**2, y=y**2, p);

algebraic procedure subpoly2trig(p,x);
   begin scalar r, d;
      d := degree(den(p),x);
      r := sub(x=cos(x)+i*sin(x), p*x**d);
      return r*(cos(x)-i*sin(x))**d
   end;

algebraic procedure subpoly2hyp(p,x);
   begin scalar r, d;
      d := degree(den(p),x);
      r := sub(x=cosh(x)+sinh(x), p*x**d);
      return r*(cosh(x)-sinh(x))**d
   end;

algebraic procedure varget(p);
   %% FJW: This procedure returns the variable `x' from an argument
   %% `p' of the form `n*x', where `n' must be numeric and `x' must be
   %% a kernel.
   begin scalar var;
      if not(var := mainvar num p) then RedErr
         "TrigGCD/Factorize error: no variable specified.";
      if not numberp(p/var) then RedErr
         "TrigGCD/Factorize error: last arg must be [number*]variable.";
      return var
   end;

symbolic procedure trigargcheck(p, var, nu);
   %% Check validity of trig arguments.  Note that nu may be rational!
   begin scalar df_arg_var;
      for each arg in get_trig_arguments(p, nil) do algebraic
         if (df_arg_var := df(arg,var)) and
            not fixp(df_arg_var/nu) then
            RedErr "TrigGCD/Factorize error: basis not possible."
   end;

symbolic operator sub2poly$
symbolic procedure sub2poly(p, var, nu, x, y);
   <<
      trigargcheck(p, var, nu);
      p := trigsimp1(p, nil);
      p := algebraic sub(
         sin var = sin(var/nu),
         cos var = cos(var/nu),
         sinh var = sinh(var/nu),
         cosh var = cosh(var/nu), p);
      p := trigsimp1(p, nil);
      algebraic sub(
         sin var = (x-1/x)/(2i),
         cos var = (x+1/x)/2,
         sinh var = (y-1/y)/2,
         cosh var = (y+1/y)/2, p)
   >>;

algebraic procedure triggcd(p, q, x);
   begin scalar not_complex, var, nu, f;
      symbolic if (not_complex := not !*complex) then on complex;
      var := varget x;  nu := numberget x;
      %% xx_x, yy_y should be gensyms?
      p := sub2poly(p, var, nu, xx_x, yy_y);
      q := sub2poly(q, var, nu, xx_x, yy_y);
      if not and(balanced(p,xx_x), balanced(q,xx_x),
         coordinated(p,xx_x), coordinated(q,xx_x),
         balanced(p,yy_y), balanced(q,yy_y),
         coordinated(p,yy_y), coordinated(q,yy_y))
      then f := 1
      else
      begin scalar h, !*nopowers, !*ifactor;
         symbolic(!*nopowers := t);
         p := trig2ord(p, xx_x, yy_y);
         q := trig2ord(q, xx_x, yy_y);
         h := gcd(num p, num q);
         h := ord2trig(h, xx_x, yy_y) / lcm(den p, den q);
         h := subpoly2trig(h, xx_x);
         h := subpoly2hyp(h, yy_y);
         h := sub(xx_x=var*nu, yy_y=var*nu, h);
         h := symbolic trigsimp1(h, nil);
         %% What follows is an expensive way to extract the primitive
         %% part!  Try using `integer_content' defined above or
         %% `comfac' in alg/gcd?
         h := factorize(num h);
         if numberp first h then h := rest h;
         f := for each r in h product r
      end;
      symbolic if not_complex then off complex;
      return f
   end;

algebraic procedure trigfactorize(p, x);
   begin scalar not_complex, var, nu, q, factors;
      symbolic if (not_complex := not !*complex) then on complex;
      var := varget x;  nu := numberget x;
      %% xx_x, yy_y should be gensyms?
      q := sub2poly(p, var, nu, xx_x, yy_y);
      if not(balanced(q,xx_x) and coordinated(q,xx_x) and
         balanced(q,yy_y) and coordinated(q,yy_y))
      then factors := if symbolic !*nopowers then {p} else {{p,1}}
      else
      begin scalar pow, content;
         %% Handle desired factorized form:
         if symbolic(not !*nopowers) then pow := 1;
         q := trig2ord(q, xx_x, yy_y);
         content := 1/den q;
         factors := {};
         for each fac in factorize num q do <<
            if pow then << pow := second fac;  fac := first fac >>;
            fac := ord2trig(fac, xx_x, yy_y);
            fac := subpoly2trig(fac, xx_x);
            fac := subpoly2hyp(fac, yy_y);
            fac := sub(xx_x=var*nu, yy_y=var*nu, fac);
            fac := symbolic trigsimp1(fac, nil);
            if fac freeof var then
               content := content*(if pow > 1 then fac^pow else fac)
            else
            begin scalar !*nopowers;
               for each u in factorize(fac) do
                  if u freeof var then <<
                     u := first u ^ second u;
                     content := content*(if pow > 1 then u^pow else u);
                     fac := fac/u
                  >>;
               factors := (if pow then {fac,pow} else fac) . factors
            end
         >>;
         if content neq 1 then
            factors := (if symbolic !*nopowers then content else {content,1})
               . factors
      end;
      symbolic if not_complex then off complex;
      return factors
   end;

endmodule;

end;
