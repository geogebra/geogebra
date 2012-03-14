module odepatch$  % Patches to standard REDUCE facilities

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


% F.J.Wright@Maths.QMW.ac.uk, Time-stamp: <18 September 2000>

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Integrator patches
% ==================

% Avoid trying to integrate an integral to speed up some odesolve
% calls.

% NB: subst copies, even if no substitution is made.  It is therefore
% very likely to destroy uniqueness of kernels!

%% load_package int$
%% apply1('load!-package, 'int)$           % not at compile time!
packages_to_load int$                   % not at compile time!

global '(ODESolveOldSimpInt)$
(if not(s eq 'NoIntInt_SimpInt) then ODESolveOldSimpInt := s) where
   s = get('int, 'simpfn)$              % to allow reloading
put('int, 'simpfn, 'NoIntInt_SimpInt)$

fluid '(!*NoInt !*Odesolve_NoInt)$

symbolic procedure NoIntInt_SimpInt u;
   %% Patch to avoid trying to re-integrate symbolic integrals in the
   %% integrand, because it can take forever and achieve nothing!
   if !*NoInt then
   begin scalar v, varstack!*;
      % Based on int/driver/simpint1.
      % Varstack* rebound, since FORMLNR use can create recursive
      % evaluations.  (E.g., with int(cos(x)/x**2,x)).
      u := 'int . u;
      return if (v := formlnr u) neq u then <<
         v := simp subst('int!*, 'int, v);
         remakesf numr v ./ remakesf denr v
      >> else !*kk2q u
   end
   else
   if atom u or null cdr u or cddr u and (null cdddr u or cddddr u)
     then rerror(int,1,"Improper number of arguments to INT")
    else if cddr u then simpdint u      % header from simpint
   else begin scalar car_u, result;
      %% put('int, 'simpfn, 'SimpNoInt);
      car_u := mk!*sq simp!* car u;
      %% car_u := subeval{{'equal, 'Int, 'NoInt}, car_u};
      car_u := subst('NoInt, 'Int, car_u);
      u := car_u . !*a2k cadr u . nil;
      %% Prevent SimpInt from resetting itself!
      put('int, 'simpfn, ODESolveOldSimpInt);    % assumed & RESET by simpint
      result := errorset!*({ODESolveOldSimpInt, mkquote u}, t);
      put('int, 'simpfn, 'NoIntInt_SimpInt); % reset INT interface
      if errorp result then error1();
      return NoInt2Int car result;
      %% Does this cause non-unique kernels?
   end$

algebraic operator NoInt$               % Inert integration operator

%% symbolic procedure SimpNoInt u;
%%    !*kk2q('NoInt . u)$                % remain symbolic

symbolic operator Odesolve!-Int$
symbolic procedure Odesolve!-Int(y, x);
   %% Used in SolveLinear1 on ode1 to control integration.
   if !*Odesolve_NoInt then formlnr {'NoInt, y, x}
   else mk!*sq NoIntInt_SimpInt{y, x}$  % aeval{'int, y, x}$

%% put('Odesolve!-Int, 'simpfn, 'Simp!-Odesolve!-Int)$
%% symbolic procedure Simp!-Odesolve!-Int u;
%%    %% Used in SolveLinear1 on ode1 to control integration.
%%    if !*Odesolve_NoInt then !*kk2q('NoInt . u)  % must eval u!!!
%%    else NoIntInt_SimpInt u$         % aeval{'int, y, x}$

symbolic procedure NoInt2Int u;
   %% Convert all NoInt's back to Int's, without algebraic evaluation.
   subst('Int, 'NoInt, u)$

switch NoIntInt$  !*NoIntInt := t$
put('NoIntInt, 'simpfg,
   '((nil (put 'int 'simpfn 'SimpInt) (rmsubs))
     (t (put 'int 'simpfn 'NoIntInt_SimpInt))))$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Differentiator patches
% ======================

% Differentiate integrals correctly!

% NB: `ON' is flagged ignore and so not compiled, so...
on1 'allowdfint$

% To replace the versions in `reduce/packages/poly/diff.red' once
% tested.

% deflist('((dfint ((t (progn (on1 'allowdfint) (rmsubs)))))), 'simpfg);

symbolic procedure diffp(u,v);
   % U is a standard power, V a kernel.
   % Value is the standard quotient derivative of U wrt V.
   begin scalar n,w,x,y,z; integer m;
        n := cdr u;     % integer power.
        u := car u;     % main variable.
        % Take care with noncommuting expressions.
        if n>1 and noncomp u
          then return addsq(multsq(simpdf {u,v},simpexpt {u,n - 1}),
                            multpq(u .** 1,diffp(u . (n - 1),v)))
         else if u eq v and (w := 1 ./ 1) then go to e
         else if atom u then go to f
         %else if (x := assoc(u,dsubl!*)) and (x := atsoc(v,cdr x))
%               and (w := cdr x) then go to e   % deriv known.
             % DSUBL!* not used for now.
         else if (not atom car u and (w:= difff(u,v)))
                  or (car u eq '!*sq and (w:= diffsq(cadr u,v)))
          then go to c  % extended kernel found.
         else if x := get(car u,'dfform) then return apply3(x,u,v,n)
         else if x:= get(car u,dfn_prop u) then nil
         else if car u eq 'plus and (w := diffsq(simp u,v))
          then go to c
         else go to h;  % unknown derivative.
        y := x;
        z := cdr u;
    a:  w := diffsq(simp car z,v) . w;
        if caar w and null car y then go to h;  % unknown deriv.
        y := cdr y;
        z := cdr z;
        if z and y then go to a
         else if z or y then go to h;  % arguments do not match.
        y := reverse w;
        z := cdr u;
        w := nil ./ 1;
        % computation of kernel derivative.
        repeat <<
          if caar y
            then w := addsq(multsq(car y,simp subla(pair(caar x,z),
                                                    cdar x)),
                            w);
          x := cdr x;
          y := cdr y >>
         until null y;
    c:  % save calculated deriv in case it is used again.
        % if x := atsoc(u,dsubl!*) then go to d
        %  else x := u . nil;
        % dsubl!* := x . dsubl!*;
  % d:   rplacd(x,xadd(v . w,cdr x,t));
    e:  % allowance for power.
        % first check to see if kernel has weight.
        if (x := atsoc(u,wtl!*))
          then w := multpq('k!* .** (-cdr x),w);
        m := n-1;
        % Evaluation is far more efficient if results are rationalized.
        return rationalizesq if n=1 then w
                else if flagp(dmode!*,'convert)
                     and null(n := int!-equiv!-chk
                                           apply1(get(dmode!*,'i2d),n))
                 then nil ./ 1
                else multsq(!*t2q((u .** m) .* n),w);
    f:  % Check for possible unused substitution rule.
        if not depends(u,v)
           and (not (x:= atsoc(u,powlis!*))
                 or not depends(cadddr x,v))
           and null !*depend
          then return nil ./ 1;
        % Derivative of a dependent identifier; maybe apply chain
        % rule.  Suppose u(v) = u(a(v),b(v),...), i.e. given
        % depend {u}, a, b, {a, b}, v;
        % then (essentially) depl!* = ((b v) (a v) (u b a))
        if !*expanddf
           and (not (x := atsoc(u,powlis!*)) or not depends(cadddr x,v))
           and (x := atsoc(u, depl!*)) and not(v memq (x:=cdr x)) then <<
           w := df!-chain!-rule(u, v, x);
           go to e
        >>;
        w := list('df,u,v);
        w := if x := opmtch w then simp x else mksq(w,1);
        go to e;
    h:  % Final check for possible kernel deriv.
        if car u eq 'df then <<         % multiple derivative
           if cadr u eq v then <<
              % (df (df v x y z ...) v) ==> 0 if commutedf
              if !*commutedf and null !*depend then return nil ./ 1
              else if !*simpnoncomdf and (w:=atsoc(v, depl!*))
                 and null cddr w % and (cadr w eq (x:=caddr u))
              then
                 % (df (df v x) v) ==> (df v x 2)/(df v x) etc.
                 % if single independent variable
                 <<
                    x := caddr u;
                    % w := simp {'quotient, {'df,u,x}, {'df,v,x}};
                    w := quotsq(simp{'df,u,x},simp{'df,v,x});
                    go to e
                 >>
              >>
           else if eqcar(cadr u, 'int) then
              % (df (df (int F x) A) v) ==> (df (df (int F x) v) A) ?
              % Commute the derivatives to differentiate the integral?
              if caddr cadr u eq v then
                 % Evaluating (df u v) where u = (df (int F v) A)
                 % Just return (df F A) - derivative absorbed
                 << w := 'df . cadr cadr u . cddr u;  go to j >>
              else if !*allowdfint and
                 % Evaluating (df u v) where u = (df (int F x) A)
                 % (If dfint is also on then this will not arise!)
                 % Commute only if the result simplifies:
                 not_df_p(w := diffsq(simp!* cadr cadr u, v))
              then <<
                 % Generally must re-evaluate the integral (carefully!)
                 w := 'df . reval{'int, mk!*sq w, caddr cadr u} . cddr u;
                 go to j >>;  % derivative absorbed
           if (x := find_sub_df(w:= cadr u . merge!-ind!-vars(u,v),
                                           get('df,'kvalue)))
                          then <<w := simp car x;
                                 for each el in cdr x do
                                    for i := 1:cdr el do
                                        w := diffsq(w,car el);
                                 go to e>>
                       else w := 'df . w
        >> else if !*expanddf and not atom cadr u then <<
           % Derivative of an algebraic operator u(a(v),...) via the
           % chain rule: df(u(v),v) = u_1(a(v),b(v),...)*df(a,v) + ...
           x := intern compress nconc(explode car u, '(!! !! !_));
           y := cdr u;  w := nil ./ 1;  m := 0;
           for each a in y do
           begin scalar b;
              m:=m#+1;
              if numr(b:=simp{'df,a,v}) then <<
                 z := mkid(x, m);
                 put(z, 'simpfn, 'simpiden);
                 w := addsq(w, multsq(simp(z . y), b))
              >>
           end;
           go to e
        >> else w := {'df,u,v};
   j:   if (x := opmtch w) then w := simp x
         % At this point nested df's may have been collapsed, so
         % we have to consider all dependencies on all variables
         % and be very careful about returning zero.
         else if not depends(u,v)
                 and (not (x:= atsoc(u:=cadr w,powlis!*))
                       or not dependsl(cadddr x,cddr w))
                 and null !*depend then return nil ./ 1
         else w := mksq(w,1);
      go to e
   end$

symbolic procedure dfform_int(u, v, n);
   % Simplify a SINGLE derivative of an integral.
   % u = '(int y x) [as main variable of SQ form]
   % v = kernel
   % n = integer power
   % Return SQ form of df(u**n, v) = n*u**(n-1)*df(u, v)
   % This routine is called by diffp via the hook
   % "if x := get(car u,'dfform) then return apply3(x,u,v,n)".
   % It does not necessarily need to use this hook, but it needs to be
   % called as an alternative to diffp so that the linearity of
   % differentiation has already been applied.
   begin scalar result, x, y, dx!/dv;
      y := simp!* cadr u;  % SQ form integrand
      x := caddr u;  % kernel
      result :=
      if v eq x then y
         % Special case -- just differentiate the integral:
         % df(int(y,x), x) -> y  replacing the let rule in INT.RED
      else if not !*intflag!* and       % not in the integrator
         % If used in the integrator it can cause infinite loops,
         % e.g. in df(int(int(f,x),y),x) and df(int(int(f,x),y),y)
         !*allowdfint and               % must be on for dfint to work
         <<
            % Compute PARTIAL df(y, v), where y must depend on x, so
            % if x depends on v, temporarily replace x:
            result := if numr(dx!/dv:=diffp(x.**1,v)) then
               %% (Subst OK because all kernels.)
               subst(x, xx, diffsq(subst(xx, x, y), v)) where
                  xx = gensym()
            else diffsq(y, v);
            !*dfint or not_df_p result
         >>
      then
         % Differentiate under the integral sign:
         % df(int(y,x), v) -> df(x,v)*y + int(df(y,v), x)
         addsq(
            multsq(dx!/dv, y),
            simp{'int, mk!*sq result, x})  % MUST re-simplify it!!!
            % (Perhaps I should use prepsq -
            % kernels are normally true prefix?)
      else !*kk2q{'df, u, v};  % remain unchanged
      if n neq 1 then
         result := multsq( (((u .** (n-1)) .* n) .+ nil) ./ 1, result);
      return result
   end$


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Solve patches
% =============

% Needed for definition of `mkrootsoftag' function.
packages_to_load solve$                 % not at compile time!

endmodule$
end$

%                ***** NOT INSTALLED AT PRESENT *****

%% An algebraic solver appropriate to ODESolve, that never returns
%% implicit solutions and returns nil when it fails.  Also, it solves
%% quadratics in terms of plus_or_minus.

%% *** NB: This messes up `root_multiplicities'. ***

%% (Later also use the root_of_unity operator where appropriate.
%% Could do this by a wrapper around `solvehipow' in solve/solv1.)

% This switch controls `solve' globally once this file has been
% loaded:
switch plus_or_minus$                   % off by default

%% The integrator does not handle integrands containing the
%% `plus_or_minus' operator at all well.  This may require some
%% re-writing of the integrator (!).  Temporarily, just turn off the
%% use of the `plus_or_minus' operator when necessary.

% This switch controls some `solve' calls locally within `ODESolve':
switch odesolve_plus_or_minus$          % TEMPORARY
% off by default -- it's to odangerous at present!
% !*odesolve_plus_or_minus := t$          % TEMPORARY

symbolic operator AlgSolve$
symbolic procedure AlgSolve(u, v);
   %% Return either a list of EXPLICIT solutions of a single scalar
   %% expression `u' for variable `v' or nil.
   begin scalar soln, tail, !*plus_or_minus;
      !*plus_or_minus := t;
      tail := cdr(soln := solveeval1{u, v});
      while tail do
         if car tail eq v then tail := cdr tail
         else tail := soln := nil;
      return soln
   end$

algebraic procedure SolvePM(u, v);
   %% Solve a single scalar expression `u' for variable `v', using the
   %% `plus_or_minus' operator in the solution of quadratics.
   %% *** NB: This messes up `root_multiplicities'. ***
   symbolic(solveeval1{u, v}
      where !*plus_or_minus := !*odesolve_plus_or_minus)$

%% This is a modified version of a routine in solve/quartic

symbolic procedure solvequadratic(a2,a1,a0);
   % A2, a1 and a0 are standard quotients.
   % Solves a2*x**2+a1*x+a0=0 for x.
   % Returns a list of standard quotient solutions.
   % Modified to use root_val to compute numeric roots.  SLK.
   if !*rounded and numcoef a0 and numcoef a1 and numcoef a2
      then for each z in cdr root_val list mkpolyexp2(a2,a1,a0)
         collect simp!* z else
   begin scalar d;
      d := sqrtq subtrsq(quotsqf(exptsq(a1,2),4),multsq(a2,a0));
      a1 := quotsqf(negsq a1,2);
      return
         if !*plus_or_minus then        % FJW
            list(subs2!* quotsq(addsq(a1,
               multsq(!*kk2q newplus_or_minus(),d)),a2))
                  %% Must uniquefy here until newplus_or_minus does it
                  %% for itself!
         else
            list(subs2!* quotsq(addsq(a1,d),a2),
               subs2!* quotsq(subtrsq(a1,d),a2))
   end$

endmodule$
end$
