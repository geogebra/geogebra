module odeintfc$  % Enhanced ODE solver interface

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


% F.J.Wright@Maths.QMW.ac.uk, Time-stamp: <30 October 2000>

% Use: odesolve(ode, y, x, conds, options)
%   or   dsolve(ode, y, x, conds, options)  (cf. Maple)

% The first argument must evaluate to an ODE or a list of ODEs.

% Each dependent variable y may be either an identifier or an
% operator, such as y(x), which is replaced by a new identifier
% internally.  If y(x) is specified and x is not specified then x is
% extracted from y(x) (cf. Maple).  Equations containing operators of
% the form y(x) with different arguments x are trapped, currently as
% an error, until differential-delay equation solving is implemented.

% If a dependent variable (y) does not depend on the independent
% variable (x) then y is automatically declared to depend on x, and a
% warning message to this effect is output.  Derivatives are not
% evaluated until this dependence has been enforced.  BUT NOTE THAT
% THIS DOES NOT WORK IF THE FIRST ARGUMENT IS AN ASSIGNMENT!  This is
% because the assignment is performed BEFORE the ode solver takes
% control.  This is something of an inconsistency in the current
% REDUCE algebraic processing model.

% All arguments after the first are optional but the order must be
% preserved.  If the first argument is a list of ODEs then y is
% expected to be a list of dependent variables.  If x is specified
% then y must also be specified (first).  An empty list can be used as
% a place-holder argument.  If x and/or y are missing then they are
% parsed out of the ODE.

% Thus, possible argument combinations, each of which may optionally
% be followed by conds, are: ode | ode, y | ode, y, x

% Currently, conditions can be specified only for a single ODE.

% If specified, conds must take the form of an unordered list of
% (unordered lists of) equations with either y, x, or a derivative of
% y on the left.  A single list of conditions need not be contained
% within an outer list.  Combinations of conditions are allowed.
% Conditions within one (inner) list all relate to the same x value.
% For example:

% Boundary conditions:
% {{y=y0, x=x0}, {y=y1, x=x1}, ...}

% Initial conditions:
% {x=x0, y=y0, df(y,x)=dy0, ...}

% Combined conditions:
% {{y=y0, x=x0}, {df(y,x)=dy1, x=x1}, {df(y,x)=dy2, y=y2, x=x2}, ...}

% Boundary conditions on the values of y at various values of x may
% also be specified by replacing the variables by equations with
% single values or matching lists of values on the right, of the form:

% y = y0, x = x0  |  y = {y0, y1, ...}, x = {x0, x2, ...}

% The final argument may be one of the identifiers

% implicit, explicit, laplace, numeric, series

% specifying an option.  The options "implicit" and "explicit" set the
% switches odesolve_implicit and odesolve_explicit locally.  The other
% options specify solution techniques -- they are not yet implemented.


% TO DO:
%   Improved condition code to handle eigenvalue-type BVPs.
%   Solve systems of odes, calling crack where appropriate

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% User interface
% ==============

put('odesolve, 'psopfn, 'odesolve!-eval)$
put('dsolve, 'psopfn, 'odesolve!-eval)$ % alternative (cf. Maple)
listargp odesolve, dsolve$              % May have single list arg.

symbolic procedure odesolve!-eval args;
   %% Establish a suitable global environment:
   (if !*div or !*intstr or !*factor or not !*exp or not !*mcd then
      NoInt2Int reval result else NoInt2Int result) where result =
      begin scalar !*evallhseqp, !*multiplicities, !*div, !*intstr,
            !*exp, !*mcd, !*factor, !*ifactor, !*precise,
            !*nopowers, !*algint, !*echo;
         %% Turn echo off to stop Win32 PSL REDUCE (only)
         %% outputting its trigsimp lap code at the end of
         %% odesolve.tst.  (Don't ask!)
         !*evallhseqp := !*exp := !*mcd := t;
         return odesolve!-eval1 args
      end$

symbolic procedure odesolve(ode, y, x);
   %% Direct symbolic-mode interface equivalent to MAHM's original.
   %% Calls odesolve!-eval to ensure correct environment.
   odesolve!-eval{ode, y, x}$

global '(ODESolve!-tracing!-synonyms)$
ODESolve!-tracing!-synonyms := '(trode trace tracing)$

symbolic procedure odesolve!-eval1 args;
   %% args = (ode &optional y x conds)
   %% Parse variables from ode if necessary (like solve),
   %% automatically declare y to depend on x if necessary and
   %% optionally impose conditions on the general solution.
   %% Support for systems of odes partly implemented so far.
   ( begin scalar ode, system, y, x, yconds, xconds, conds, soln;
      if null args then RedErr
         "ODESolve requires at least one argument -- the ODE";

      begin scalar df_simpfn, !*uncached;  !*uncached := t;
         % Turn off simplification of df (in case y does not yet
         % depend on x) before evaluating args (which may be lists):
         df_simpfn := get('df, 'simpfn);
         put('df, 'simpfn, 'simpiden);
         args := errorset!*({'revlis, mkquote args}, t);
         put('df, 'simpfn, df_simpfn);
         if errorp args then error1();
         args := car args
      end;

      ode := car args;  args := cdr args;
      system := rlistp ode;
      %% Find dependent and independent variables:
      %% (rlistp is a smacro defined in rlisp.red)
      if args then <<  y := car args;
         if rlistp y then
            if null cdr y then          % empty list - ignore
               y := 'empty
            else if rlistp cadr y or eqnp cadr y then
               y := nil                 % condition rlist
            else if system then
               y := makelist            % rlist of dependent variables
                  for each yy in cdr y collect !*a2k yy
            else MsgPri("ODESolve: invalid second argument",
               y, nil, nil, t)
         else if system then TypErr(y, "dependent var list")
         else if eqnp y then
            if cadr y memq 'output . ODESolve!-tracing!-synonyms then % option
               y := nil
            else << yconds := caddr y;  y := !*a2k cadr y >>
         else if not smember(y:=!*a2k y, ode) then
            y := nil                    % option
      >>;
      if y then args := cdr args;
      if args and y then <<  x := car args;
         if rlistp x then
            if null cdr x then          % empty list - ignore
               x := 'empty
            else x := nil               % condition list
         else if eqnp x then
            if cadr x memq 'output . ODESolve!-tracing!-synonyms then % option
               x := nil
            else << xconds := caddr x;  x := !*a2k cadr x >>
         else if not smember(x:=!*a2k x, ode) then
            x := nil                    % option
      >>;
      if x then args := cdr args;
      if y eq 'empty then y := nil;
      if x eq 'empty then x := nil;
      %% If x not given and y an operator (list) then extract x:
      if null x and y then
         if rlistp y then
            begin scalar yy;  yy := cdr y;
               while yy and atom car yy do yy := cdr yy;
               if yy and cdar yy then x := cadar yy;
               if not idp x then x := nil
            end
         else if pairp y and cdr y then x := cadr y;
      %% Finally, attempt to parse variables from ode if necessary:
      if null y or null x then
         %%%%% NOTE: ODE ALREADY AEVAL'ED ABOVE
         %%%%% so some of the following is now redundant !!!!!
      begin scalar df_simpfn, k_list, df_list;
         % Turn off simplification of df (in case y does not yet
         % depend on x) before evaluating ode, which may be a list:
         df_simpfn := get('df, 'simpfn);
         put('df, 'simpfn, 'simpiden);
         k_list := errorset!*({'get_k_list, mkquote ode}, t);
         put('df, 'simpfn, df_simpfn);
         if errorp k_list then error1() else k_list := car k_list;
         df_list := get_op_knl('df, car k_list);
         for each knl in cdr k_list do
            df_list := union(df_list, get_op_knl('df, knl));
         %% df_list is set of derivatives in ode(s).
         if null df_list then RedErr
            "No derivatives found -- use solve instead.";
         %% df_list = ((df y x ...) ... (df z x ...) ... )
         if null y then <<
            y := cadar df_list . nil;
            for each el in cdr df_list do
               if not member(cadr el, y) then y := cadr el . y;
            %% y is a list at this point.
            if system then
               if length ode < length y then
                  RedErr "ODESolve: under-determined system of ODEs."
               else y := makelist y     % algebraic list of vars
            else
               if cdr y then
                  MsgPri("ODESolve -- too many dependent variables:",
                     makelist y, nil, nil, t)
               else y := car y;         % single var
            MsgPri("Dependent var(s) assumed to be", y, nil, nil, nil)
         >>;
         if null x then <<
            x := caddar df_list;
            MsgPri("Independent var assumed to be", x, nil, nil, nil)
         >>;
      end;

      %% Process the ode (re-simplifying derivatives):
      EnsureDependency(y, x);
      ode := aeval ode;
      %% !*eqn2a is defined in alg.red
      if system then
         if length ode > 2 then
            %% RedErr "Solving a system of ODEs is not yet supported."
            %% Skip conditions TEMPORARILY!
            return ODESolve!-Depend(
               makelist for each o in cdr ode collect !*eqn2a o, y, x, nil)
         else
            << ode := !*eqn2a cadr ode;  y := cadr y >>
      else ode := !*eqn2a ode;

      %% Process conditions (re-simplifying derivatives):
      if args then
         if rlistp(conds := aeval car args) then <<
            args := cdr args;
            conds := if not rlistp cadr conds then conds . nil
            else cdr conds
         >> else conds := nil;
      %% Now conds should be a lisp list of rlists (of equations).
      if yconds then
         yconds := if rlistp yconds then cdr yconds else yconds . nil;
      if xconds then
         xconds := if rlistp xconds then cdr xconds else xconds . nil;
      %% Concatenate separate x & y conds onto conds list:
      while yconds and xconds do <<
         conds := {'list, {'equal, x, car xconds},
            {'equal, y, car yconds}} . conds;
         yconds := cdr yconds;  xconds := cdr xconds
      >>;
      if yconds or xconds then
         RedErr "Different condition list lengths";
      if conds then
         %% Move this into odesolve!-with!-conds?
         conds := makelist odesolve!-sort!-conds(conds, y, x);

      %% Process remaining control option arguments:
      while args do
      begin scalar arg;  arg := car args;  args := cdr args;
         if eqnp arg then               % equation argument
            if cadr arg eq 'output then
               args := caddr arg . args
            else if cadr arg memq ODESolve!-tracing!-synonyms then
               !*trode := caddr arg
            else MsgPri("Invalid ODESolve option", arg,
               "ignored.", nil, nil)
                                        % keyword argument
         else if arg memq
            '(implicit explicit expand noint verbose
               basis noswap norecurse fast check) then
            set(mkid('!*odesolve_, arg), t)
         else if arg eq 'algint then on1 'algint
         else if arg eq 'full or !*odesolve_full then
            !*odesolve_expand := !*odesolve_explicit := t
         else if arg memq ODESolve!-tracing!-synonyms then !*trode := t
         else if arg memq '(laplace numeric series) then
            RedErr{"ODESolve option", arg, "not yet implemented."}
               %% Pass remaining args to routine called
         else RedErr{"Invalid ODESolve option", arg}
      end;

      if !*odesolve_verbose then algebraic <<
         write "ODE: ", num ode=0;
         write "Dependent variable: ", y, ";  independent variable: ", x;
         write "Conditions: ", symbolic(conds or "none");
      >>;

      %% Rationalize conflicting options:
      %% Conditions override basis
      if conds then !*odesolve_basis := nil;
%%       %% Basis overrides explicit
%%       if !*odesolve_basis then !*odesolve_explicit := nil;

      %% Finally, solve the ode!
      if not getd 'ODESolve!*0 then     % for testing
         return {'ODESolve, ode, y, x, conds};
%%       soln := if conds then
%%          odesolve!-with!-conds(ode, y, x, conds)
%%       else odesolve!-depend(ode, y, x);
      if null(soln := ODESolve!-Depend(ode, y, x, conds)) then
         return algebraic {num ode=0};
      %% Done as follows because it may be easier to solve after
      %% imposing conditions than before, and it would be necessary to
      %% remove root_of's before imposing conditions anyway.
      if !*odesolve_explicit and not ODESolve!-basisp soln then
         soln := ODESolve!-Make!-Explicit(soln, y, conds);
      if !*odesolve_expand then
         soln := expand_roots_of_unity soln;
      if !*odesolve_check then
         ODE!-Soln!-Check(if !*odesolve_noint then NoInt2Int soln else soln,
            ode, y, x, conds) where !*noint = t;
      return soln

   end ) where !*odesolve_implicit = !*odesolve_implicit,
               !*odesolve_explicit = !*odesolve_explicit,
               !*odesolve_expand = !*odesolve_expand,
               !*trode = !*trode,
               !*odesolve_noint = !*odesolve_noint,
               !*odesolve_verbose = !*odesolve_verbose,
               !*odesolve_basis = !*odesolve_basis,
               !*odesolve_noswap = !*odesolve_noswap,
               !*odesolve_norecurse = !*odesolve_norecurse,
               !*odesolve_fast = !*odesolve_fast,
               !*odesolve_check = !*odesolve_check$

symbolic procedure Odesolve!-Make!-Explicit(solns, y, conds);
   <<
      %% SHOULD PROBABLY CHECK THAT Y IS NOT INSIDE AN UNEVALUATED
      %% INTEGRAL BEFORE TRYING TO SOLVE FOR IT -- IT SEEMS TO UPSET
      %% SOLVE!
      solns := for each soln in cdr solns join
         if cadr soln eq y then {soln} else <<
            %% soln is an implicit solution of ode for y
            %% for each s in cdr reval aeval {'solve, soln, y} join
            %%    %% Make this test optional?
            %%    if eqcar(caddr s, 'root_of) or eval('and .
            %%       mapcar(cdr expand_roots_of_unity subeval{s, ode},
            %%          'zerop))
            %%    then {s}
            %%    else if !*trode then algebraic write "Solution ", s,
            %%    " discarded -- does not satisfy ODE";
            traceode
               "Solution before trying to solve for dependent variable is ",
               soln;
            cdr reval aeval {'solve, soln, y}
         >>;
      %% It is reasonable to return root_of's here.
      %% Solving can produce duplicates, so ...
      %% solns := union(solns, nil); % union still necessary?

      %% Check that each explicit solution still satisfies any
      %% conditions:
      if conds then
         for each cond in cdr conds do  % each cond is an rlist
         begin scalar xcond;
            xcond := cadr cond;
            cond := makelist for each c in cddr cond collect !*eqn2a c;
            solns := for each s in solns join
               if eqcar(caddr s, 'root_of) or
                  union(cdr %% trig_simplify
                     subeval{xcond, subeval{s, cond}}, nil) = {0}
               then {s}
               else algebraic traceode "Solution ", s,
               " discarded -- does not satisfy conditions";
         end;
      makelist solns
   >>$

% Should now be able to use the standard package `trigsimp' instead!
algebraic procedure trig_simplify u;
   u where tan_half_angle_rules$

algebraic(tan_half_angle_rules := {
   sin(~u) => 2tan(u/2)/(1+tan(u/2)^2),
   cos(~u) => (1-tan(u/2)^2)/(1+tan(u/2)^2) })$
%% Cannot include tan rule -- recursive!


symbolic procedure get_k_list ode;
   %% Return set of all top-level kernels in ode or rlist of odes.
   %% (Do not cache to ensure derivatives are [eventually] evaluated
   %% properly!)
   begin scalar k_list, !*uncached;  !*uncached := t;
      %% Do not make an assignment twice:
      if eqcar(ode, 'setk) then ode := caddr ode;
      if rlistp(ode := reval ode) then <<
         k_list := get_k_list1 cadr ode;
         for each el in cddr ode do
            k_list := union(k_list, get_k_list1 el)
      >>
      else k_list := get_k_list1 ode;
      return k_list
   end$

symbolic procedure get_k_list1 ode;
   union(kernels numr o, kernels denr o)
      where o = simp !*eqn2a ode$

symbolic procedure get_op_knl(op, knl);
   %% Return set of all operator kernels within knl with op as car.
   if pairp knl then
      if car knl eq op then knl . nil
      else
         ( if op_in_car then union(op_in_car, op_in_cdr) else op_in_cdr )
            where op_in_car = get_op_knl(op, car knl),
               op_in_cdr = get_op_knl(op, cdr knl)$

symbolic procedure EnsureDependency(y, x);
   for each yy in (if rlistp y then cdr y else y . nil) do
      if not depends(yy, x) then <<
         MsgPri("depend", yy, ",", x, nil);
         depend1(yy, x, t)
      >>$


symbolic procedure odesolve!-sort!-conds(conds, y, x);
   %% conds is a lisp list of rlists of condition equations.
   %% Return a canonical condition list.
   %% Collect conditions at the same value of x, check them for
   %% consistency and sort them by increasing order of derivative.
   begin scalar cond_alist;
      for each cond in conds do
      begin scalar x_cond, y_conds, x_alist;
         if not rlistp cond then TypErr(cond, "ode condition");

         %% Extract the x condition:
         y_conds := for each c in cdr cond join
            if not CondEq(c, y, x) then
               TypErr(c, "ode condition equation")
            else if cadr c eq x then << x_cond := c; nil >>
            else c . nil;
         if null x_cond then
            MsgPri(nil, x, "omitted from ode condition", cond, t);
         if null y_conds then
            MsgPri(nil, y, "omitted from ode condition", cond, t);

         %% Build the new condition alist, with the x condition as key:
         if (x_alist := assoc(x_cond, cond_alist)) then
            nconc(x_alist, y_conds)
         else cond_alist := (x_cond . y_conds) . cond_alist
      end;
      %% Now cond_alist is a list of lists of equations, each
      %% beginning with a unique x condition.

      %% Sort the lists and return a list of rlists:
      return for each cond in cond_alist collect makelist
         if null cddr cond then cond else car cond .
         begin scalar sorted, next_sorted, this, next, result;
            sorted := sort(cdr cond, 'lessp!-deriv!-ord);
            %% sorted is a list of equations.
            while sorted and (next_sorted := cdr sorted) do <<
               if cadr(this := car sorted) eq
                  cadr(next := car next_sorted) then
                  %% Two conds have same lhs, so ...
                  ( if caddr this neq caddr next then
                     MsgPri("Inconsistent conditions:",
                        {'list, this, next}, "at", car cond, t) )
                           % otherwise ignore second copy
               else result := this . result;
               sorted := next_sorted
            >>;
            return reversip(next . result)
         end
   end$

symbolic procedure CondEq(c, y, x);
   %% Return true if c is a valid condition equation for y(x).
   %% cf. eqexpr in alg.red
   eqexpr c and ( (c := cadr c) eq x or c eq y or
      (eqcar(c, 'df) and cadr c eq y and caddr c eq x
         %% Is the following test overkill?
         and (null cdddr c or fixp cadddr c)) )$

symbolic procedure lessp!-deriv!-ord(a, b);
   %% (y=y0) < (df(y,x)=y1) and df(y,x,m)=ym < df(y,x,n)=yn iff m < n
   %% But y might be a kernel rather than an identifier!
   if atom(a := cadr a) then            % a = (y=?)
      not atom cadr b                   % b = (df(y,x,...)=?)
   else if atom(b := cadr b) then       % b = (y=?)
      not atom cadr a                   % a = (df(y,x,...)=?)
   else if not(car a eq 'df) then       % a = (y(x)=?)
      car b eq 'df                      % b = (df(y(x),x,...)=?)
   else                                 % a = (df(y,x,...)=?), any y
      car b eq 'df and                  % b = (df(y,x,...)=?)
         if null(a := cdddr a) then     % a = (df(y,x)=?)
            cdddr b                     % b = (df(y,x,n)=?), 1 < n
         else                           % a = (df(y,x,m)=?), m > 1
            (b := cdddr b) and
               car a < car b$           % b = (df(y,x,n)=?), m < n


%%% THE FOLLOWING PROCEDURE SHOULD PROBABLY INCLUDE THE CODE TO MAKE
%%% SOLUTIONS EXPLICIT BEFORE RESTORING OPERATOR FORMS FOR Y.

symbolic procedure ODESolve!-Depend(ode, y, x, conds);
   %% Check variables and dependences before really calling odesolve.
   %% If y is an operator kernel then check whether ode is a
   %% differential-delay equation, and if not solve ode with y
   %% replaced by an identifier.
   ( begin scalar xeqt, ylist, sublist;
      y := if rlistp ode then cdr y else y . nil;
      %% Using `t' as a variable causes trouble when checking
      %% dependence of *SQ forms, which may contain `t' as their last
      %% element, so...
      if x eq t then <<
         xeqt := t;  x := gensym();
         for each yy in y do if idp yy then depend1(yy, x, t);
         %% Cannot simply use `sub' on independent variables in
         %% derivatives, so...
         ode := subst(x, t, reval ode); % reval subst?
         if conds then conds := subst(x, t, reval conds); % reval subst?
         sublist := (t.x) . sublist
      >>;
      for each yy in y do
         if idp yy and not(yy eq t) then <<
            %% Locally and quietly remove any spurious inverse
            %% implicit dependence of x on y:
            ylist := yy . ylist;
            depend1(x, yy, nil) where !*msg = nil;
         >> else                        % replace variable
         begin scalar yyy;
            yyy := gensym();  depend1(yyy, x, t);
            ylist := yyy . ylist;
            put(yyy, 'odesolve!-depvar, yy); % for later access
            sublist := (yy.yyy) . sublist;
            if xeqt then yy := subeval{{'equal,t,x}, yy};
            odesolve!-delay!-check(ode, yy);
            ode := subeval{{'equal,yy,yyy}, ode};
            if conds then conds := subeval{{'equal,yy,yyy}, conds}
         end;
      ylist := reverse ylist;
      ode := if rlistp ode then
         ODESolve!-System(cdr ode, ylist, x)
      else if conds then
         odesolve!-with!-conds(ode, car ylist, x, conds)
      else ODESolve!*0(ode, car ylist, x);
      if null ode then return;
      if sublist then
      begin scalar !*NoInt;
         %% Substitute into derivatives and integrals
         %% (and turn off integration for speed).
         ode := reval ode;              % necessary?
         for each s in sublist do
            ode := subst(car s, cdr s, ode);
         %% ode := reval ode;              % necessary?
      end;
      return ode
   end ) where depl!* = depl!*$

symbolic procedure ODESolve!-System(ode, y, x);
   %% TEMPORARY
   {'ODESolve!-System, makelist ode, makelist y, x}$
algebraic operator ODESolve!-System$

symbolic procedure odesolve!-delay!-check(ode, y);
   %% Check that ode is not a differential-delay equation in y,
   %% i.e. check that every occurrence of the operator y = y(x) has
   %% the same argument (without any shifts).  This could be used as a
   %% hook to call an appropriate solver -- if there were one!
   begin scalar odelist;
      odelist := if rlistp ode then cdr ode else ode . nil;
      for each ode in odelist do
         ( for each knl in kernels numr simp ode do
            for each yy in get_op_knl(y_op, knl) do
               if not(yy eq y) then
                  MsgPri("Arguments of", y_op, "differ --",
                     "solving delay equations is not implemented.", t) )
                        where y_op = car y
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Impose initial/boundary conditions
% ==================================

% A first attempt to impose initial/boundary conditions on the
% solution of a single ode returned by odesolve.

% Solving with conditions provides access to the general solution as
% the value of the global algebraic variable ode_solution.

% If the solution is explicit then the following code could be
% simplified and should be slightly more efficient, but is it worth
% testing for an explicit solution and adding the special case code?

algebraic procedure odesolve!-with!-conds(ode, y, x, conds);
   % conds must be a list of ordered lists of the form
   %    {x=x0, y=y0, df(y,x)=y1, df(y,x,2)=y2, ...}.
   % All conditions applied at the same value of x must be collected
   % into the same list.
   % More generality is allowed only by odesolve!-eval.
   % This code could perhaps be more efficient by building a list of
   % all required derivatives of the ode solution once and for all?
   begin scalar first!!arbconst, arbconsts;
      first!!arbconst := !!arbconst + 1;

      %% Find the general solution of the ode and assign it to the
      %% global algebraic variable ode_solution:
      %1.03% ode_solution := odesolve!-depend(ode, y, x);
      ode_solution := symbolic ODESolve!*0(ode, y, x);
      if not ode_solution then return;
      traceode "General solution is ", ode_solution;
      traceode "Applying conditions ", conds;

      arbconsts :=
         for i := first!!arbconst : !!arbconst collect arbconst i;

      return for each soln in ode_solution join
         odesolve!-with!-conds1(soln, y, x, conds, arbconsts)
   end$

algebraic procedure odesolve!-with!-conds1(soln, y, x, conds, arbconsts);
   begin scalar arbconsteqns;
      %% Impose the conditions (care is needed if the solution is
      %% implicit):
      arbconsteqns := for each cond in conds join
         begin scalar xcond, ycond, dfconds, arbconsteqns;
            xcond := first cond;  cond := rest cond;
            ycond := first cond;
            if lhs ycond = y then cond := rest cond else ycond := 0;
            %% Now cond contains only conditions on derivatives.
            arbconsteqns :=
               if ycond then         % Impose the condition on y:
                  {sub(xcond := {xcond, ycond}, soln)}
               else {};
            dfconds := {};
            %% Impose the conditions on df(y, x, n).  If the solution
            %% is implicit, then in general all lower derivatives will
            %% be introduced, so ...
            while cond neq {} do
            begin scalar dfcond, result;
               %% dfcond : next highest derivative
               %% result : of substituting for all derivatives
               %% dfconds : all derivatives so far including this one
               dfcond := first cond;  cond := rest cond;
               dfconds := dfcond . dfconds;
               %% All conditions on derivatives are handled before
               %% conditions on x and y to protect against
               %% substituting for x or y in df(y,x,...):
               result := sub(dfconds, map(y => lhs dfcond, soln));
               if not(result freeof df) then % see comment below
                  RedErr "Cannot apply conditions";
               arbconsteqns := sub(xcond, result) . arbconsteqns
            end;
            return arbconsteqns
         end;

      %% Solve for the arbitrary constants:
      arbconsts := solve(arbconsteqns, arbconsts);
      %% ***** SHOULD CHECK THAT THE SOLUTION HAS SUCCEEDED! *****
      %% and substitute each distinct arbconst solution set into the
      %% general ode solution:
      return for each cond in arbconsts collect
         if rhs soln = 0 then           % implicit solution
            num sub(cond, lhs soln) = 0
         else
            sub(cond, soln)
   end$

%% The above df error can happen only if the solution is implicit and
%% a derivative is missing from the sequence, which is unlikely.
%% Should try to recover by computing the value of the missing
%% derivative from the conditions on lower order derivatives, and
%% letting solve eliminate them.  Try this later IF it ever proves
%% necessary.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Check the solution set
% ======================

% This code checks that the solution satisfies the ODE and that a
% general solution is general.

% A `solution' is either a basis solution:
%   {{b1(x), b2(x), ..., bn(x)}, PI(x)}; PI may be omitted if zero
% or a list of component solutions.

algebraic procedure ODE!-Soln!-Check(soln, ode, y, x, conds);
   %% SOLN is a LIST of solutions; ODE is a differential EXPRESSION; Y
   %% is the dependent variable; X is the independent variable; CONDS
   %% is true if conditions were specified.
   begin scalar n, !*allowdfint, !*expanddf;
      symbolic(!*allowdfint := !*expanddf := t);
      ode := num !*eqn2a ode;           % returns ode as expression
      %% Should compute order `on demand' in ODE!-Comp!-Soln!-Fails.
      n := ODE!-Order(ode, y);
      if symbolic ODESolve!-basisp soln then <<
         %% Basis solution (of linear ODE).
         %% Only arises as general solution.
         %% Remove contribution from PI if there is one:
         if arglength soln = 2 and second soln then
            ode := num sub(y = y + second soln, ode);
         if length(soln := first soln) neq n then
            write "ODESolve warning - ",
               "wrong number of functions in basis!";
         %% Test each basis function in turn:
         for each s in soln do
            if (s:=sub(y = s, ode)) and trigsimp s then
               write "ODESolve warning - ",
                  "basis function may not satisfy ODE: ", s
      >> else <<
         %% List of component solutions.
         %% Check generality:
         if not conds and ODESolve!-arbconsts soln < n then
            write "ODESolve warning - ",
               "too few arbitrary constants in general solution!";
         for each s in soln do
            if ODE!-Comp!-Soln!-Fails(s, ode, y, x, n) then
               write "ODESolve warning - ",
                  "component solution may not satisfy ODE: ", s;
      >>
   end$

% Each component solution may be
% explicit: y = f(x)
% implicit: f(x,y) = g(x,y); rhs MAY be 0
% unsolved: ode = 0, but CAN THIS CASE ARISE?
% parametric: {y = g(p), x = f(p), p}

algebraic procedure ODE!-Comp!-Soln!-Fails(soln, ode, y, x, n);
   %% SOLN is a SINGLE component solution; ODE is a differential
   %% EXPRESSION; Y is the dependent variable; X is the independent
   %% variable; N is the order of ODE.
   if symbolic eqnp soln then           % explicit, implicit or unsolved
      if lhs soln = y and rhs soln freeof y then
                                        % explicit: y = f(x)
         (if (ode := sub(soln, ode)) then trigsimp ode)
      else if rhs soln = 0 and lhs soln = ode then
         1                              % unsolved: ode = 0
      else                              % implicit: f(x,y) = 0
      begin scalar derivs, deriv;
         %% Construct in `derivs' a list of successive derivatives of
         %% the implicit solution f(x,y) up to the order of the ODE in
         %% decreasing order; each expression is linear in the highest
         %% derivative.
         derivs := {soln := num !*eqn2a soln};
         for i := 1 : n do
            derivs := (soln:=num df(soln,x)) . derivs;
         %% Substitute for each derivative in ODE in turn in
         %% decreasing order until the result is zero; if not the
         %% solution fails.
         while n > 0 and <<
            deriv := solve(first derivs, df(y,x,n)); % linear
            if deriv = {} then 0 else
               ode := num sub(first deriv, ode) >> do <<
               n := n - 1;
               derivs := rest derivs
               >>;
         if deriv = {} then <<
            write "ODESolve warning - cannot compute ", df(y,x,n);
            return 1
         >>;
         derivs := first derivs;
         ode := (ode where derivs => 0); % for tracing
         return ode                     % 0 for good solution
      end
   else if symbolic(rlistp soln and eqnp cadr soln) then
                                        % parametric: {y = g(p), x = f(p), p}
   begin scalar xx, yy, p, dp!/dx, deriv, derivs;
      yy := rhs first soln;             % Should not depend on ordering!
      xx := rhs second soln;            % Should not depend on ordering!
      p := third soln;                  % parameter
      %% Construct in `derivs' a list of successive derivatives of the
      %% parametric solution (yy,xx) up to the order of the ODE in
      %% decreasing order.
      dp!/dx := 1/df(xx,p);
      derivs := {deriv:=yy};
      for i := 1 : n do
         derivs := (deriv:=dp!/dx*df(deriv,p)) . derivs;
      %% Substitute for each derivative in ODE in turn in
      %% decreasing order until the result is zero; if not the
      %% solution fails.
      while n > 0 and (ode := num sub(df(y,x,n)=first derivs, ode)) do <<
         n := n - 1;
         derivs := rest derivs
      >>;
      return sub(y=yy, x=xx, ode)
   end
   else write "ODESolve warning - invalid solution type: ", soln$

%% Code to find the actual number of arbitrary constants in a solution:

fluid '(ODESolve!-arbconst!-args)$

symbolic operator ODESolve!-arbconsts$
symbolic procedure ODESolve!-arbconsts u;
   %% Return the number of distinct arbconsts in any sexpr u.
   begin scalar ODESolve!-arbconst!-args;
      ODESolve!-arbconsts1 u;
      return length ODESolve!-arbconst!-args
   end$

symbolic procedure ODESolve!-arbconsts1 u;
   %% Collect all the indices of arbconsts in u into a set in the
   %% fluid variable ODESolve!-arbconst!-args.
   if not atom u then
      if car u eq 'arbconst then
         (if not member(cadr u, ODESolve!-arbconst!-args) then
            ODESolve!-arbconst!-args := cadr u . ODESolve!-arbconst!-args)
      else
         << ODESolve!-arbconsts1 car u;  ODESolve!-arbconsts1 cdr u >>$

endmodule$

end$
