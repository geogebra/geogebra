module diff; % Differentiation package.

% Author: Anthony C. Hearn.

% Modifications by: Francis J. Wright.

% Copyright (c) 2000 Anthony C. Hearn.  All rights reserved.

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


fluid '(!*depend frlis!* powlis!* subfg!* wtl!* depl!*);

fluid '(!*allowdfint !*dfint !*expanddf !*intflag!*);

global '(mcond!*);

% Contains a reference to RPLACD (a table update), commented out.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Turning on the switch `allowdfint' allows "differentiation under the
% integral sign", i.e. df(int(y, x), v) -> int(df(y, v), x), if this
% results in a simplification.  If the switch `dfint' is also turned
% on then this happens regardless of whether the result simplifies.
% Both switches are off by default.

switch allowdfint, dfint;

deflist('((dfint ((t (rmsubs))))
   (allowdfint ((t (progn (put 'int 'dfform 'dfform_int) (rmsubs)))
                (nil (remprop 'int 'dfform))))), 'simpfg);
   % There is no code to reverse the df-int commutation,
   % so no reason to call rmsubs when the switch is turned off.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Consider df(u,x,y,z).

% If none of x,y,z are equal to u then the order of differentiation is
% commuted into a canonical form, unless the switch `nocommutedf' is
% turned on, in which case the order of differentiation is not
% commuted at all.  The switch `nocommutedf' is off by default.

% If one (or more) of x,y,z is equal to u then the order of
% differentiation is NOT commuted and the derivative is NOT simplified
% to zero, unless the switch `commutedf' is turned on.  It is off by
% default.  (CRACK needs to turn it on!)

% The new default behaviour should match the behaviour of REDUCE 3.6.
% Turning on the switch `commutedf' should reproduce the default
% behaviour of REDUCE 3.7.

% If `commutedf' is off and the switch `simpnoncomdf' is on then
% simplify df(u,x,u) -> df(u,x,2)/df(u,x), df(u,x,n,u) ->
% df(u,x,n+1)/df(u,x), as suggested by Alain Moussiaux, PROVIDED u
% depends only on the one variable x.  This simplification removes the
% non-commutative aspect of the derivative.

switch commutedf, nocommutedf, simpnoncomdf;
% Turning either `commutedf' or `nocommutedf' on turns the other off.
% Turning commutation on or noncommutation off, or turning
% simplification of noncommutative derivatives on, causes
% resimplification.
deflist('((commutedf ((t (off1 'nocommutedf) (rmsubs))))
   (nocommutedf ((t (off1 'commutedf)) (nil (rmsubs))))
      (simpnoncomdf ((t (rmsubs))))), 'simpfg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% If the switch `expanddf' is turned on then REDUCE uses the chain
% rule to expand symbolic derivatives of indirectly dependent
% variables provided the result is unambiguous, i.e. provided there is
% no direct dependence.  It is off by default.  Thus, for example,
% given
% depend f, u, v; depend {u, v}, x;
% then, if `expanddf' is on,
% df(f,x) -> df(f,u)*df(u,x) + df(f,v)*df(v,x)
% whereas after
% depend f, x;
% df(f,x) does not expand at all (since the result would be ambiguous
% and the algorithm would loop).

% For similar handling in the case of explicit dependence,
% e.g. df(f(u(x),v(x)),x), please use the standard package `DFPART' by
% Herbert Melenk.

switch expanddf;
deflist('((expanddf ((t (rmsubs))))), 'simpfg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure simpdf u;
   % U is a list of forms, the first an expression and the remainder
   % kernels and numbers.
   % Value is derivative of first form wrt rest of list.
   begin scalar v,x,y,z;
      if null subfg!* then return mksq('df . u,1);
      v := cdr u;
      u := simp!* car u;
  a:  if null v or null numr u then return u;
      x := if null y or y=0 then simp!* car v else y;
      if denr x neq 1 or atom numr x
        then typerr(prepsq x,"kernel or integer")
       else (if domainp z
               then if get(car z,'domain!-diff!-fn)
                       then begin scalar dmode!*,alglist!*;
                              x := prepf z;
                              if null prekernp x
                                then typerr(x,"kernel")
                            end
                     else typerr(prepf z,"kernel")
              else if null red z and lc z = 1 and ldeg z = 1
                      then x := mvar z
              else typerr(prepf z,"kernel")) where z = numr x;
      v := cdr v;
      if null v then <<u := diffsq(u,x); go to a>>;
      y := simp!* car v;
      % At this point, y must be a kernel or equivalent to an integer.
      % Any other value is an error.
      if null numr y then <<v := cdr v; y := nil; go to a>>
       else if not(z := d2int y) then <<u := diffsq(u,x); go to a>>;
      v := cdr v;
      for i:=1:z do u := diffsq(u,x);
      y := nil;
      go to a
   end;

symbolic procedure d2int u;
   if denr u neq 1 then nil
    else if numberp(u := numr u) then u
    else if not domainp u or not(car u eq '!:rd!:) then nil
    else (if abs(float x - u)<!!fleps1 then x else nil)
          where x=fix u where u=rd2fl u;

put('df,'simpfn,'simpdf);

symbolic procedure prekernp u;
   if atom u then idp u
    else idp car u
         and null((car u memq '(plus minus times quotient recip))
                   or ((car u eq 'expt) and fixp caddr u));

symbolic procedure diffsq(u,v);
   % U is a standard quotient, V a kernel.
   % Value is the standard quotient derivative of U wrt V.
   % Algorithm: df(x/y,z)= (x'-(x/y)*y')/y.
   multsq(addsq(difff(numr u,v),negsq multsq(u,difff(denr u,v))),
          1 ./ denr u);

symbolic procedure difff(u,v);
   %U is a standard form, V a kernel.
   %Value is the standard quotient derivative of U wrt V.
   % Allow for differentiable domains.
   if atom u then nil ./ 1
    else if atom car u
     then (if diff!-fn then apply2(diff!-fn,u,v) else nil ./ 1)
        where (diff!-fn =  get(car u,'domain!-diff!-fn))
    else addsq(addsq(multpq(lpow u,difff(lc u,v)),
                        multsq(diffp(lpow u,v),lc u ./ 1)),
               difff(red u,v));

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
           %
           % Try chain rule for nested derivatives:
           % (df (df v x y z ...) a) where v depends on a
           %
           if !*expanddf and depends(cadr u,v)
              and (not (x := atsoc(cadr u,powlis!*)) or not depends(cadddr x,v))
             then <<
                if not smember(v, cadr u)
                 then <<
                  % first check for declared dependency of kernel cadr u on v
                  x := assoc(cadr u, depl!*);
                  % then if cadr u is not a simple symbol,
                  %  check whether anything in cdr cadr u has an explicit
                  %  dependency on v by collecting all kernels in cdr cadr u
                  y := (not atom cadr u and cdr cadr u and get!-all!-kernels cdr cadr u);
                  % but take care to exclude the kernel v when checking dependencies
		  if x and y and ldepends(delete(v,y),v) then <<
               	  % possible inconsistent dependencies, do not apply chain rule
%                   msgpri("Possible inconsistent dependencies in",u,
%                         nil,nil,nil);
                    nil >>
                   else if x and not(v memq (x:=cdr x))
                    % declared indirect dependency, 
                    then << w := df!-chain!-rule(u, v, x); go to e>>
                   else if y and not smember(v,y)
                    % possible indirect dependency of kernel arglist on v
                    then << w := df!-chain!-rule(u, v, y); go to e>>
                  >>
              >>;
           if (x := find_sub_df(w:= cadr u . merge!-ind!-vars(u,v),
                                           get('df,'kvalue)))
                          then <<w := simp car x;
                                 for each el in cdr x do
                                    for i := 1:cdr el do
                                        w := diffsq(w,car el);
                                 go to e>>
                       else w := 'df . w
        >> else w := {'df,u,v};
   j:   if (x := opmtch w) then w := simp x
         % At this point nested df's may have been collapsed, so
         % we have to consider all dependencies on all variables
         % and be very careful about returning zero.
         else if not depends(u,v)
                 and (not (x:= atsoc(u:=cadr w,powlis!*))
                       or not dependsl(cadddr x,cddr w))
                 and null !*depend then return nil ./ 1
%         else if !*expanddf and not atom u 
         % do not try to apply the chain rule to cases that are handled earlier
         % (i.e. for nested/multiple derivatives, or differentiation of integrals)
         % or that may come from inconsistent dependencies, e.g. after
         %  depend u(v),a;
         % do not replace df(u(v),v) by df(u,v),a)*df(a,v) 
         else if !*expanddf and not atom u and null cdddr w
                 and not(car u memq '(df int)) and not smember(v,u)
                 and (not (x:= atsoc(u,powlis!*)) or not depends(cadddr x,v))
          then <<
            % first check for declared dependency of kernel u on v
            x := assoc(u, depl!*);
            % then check whether anything in cdr u has an explicit
            % dependence on v by collecting all kernels in cdr u
            y := (cdr u and get!-all!-kernels cdr u);
            % but take care to exclude the kernel v when checking dependencies
            if x and y and ldepends(delete(v,y),v) then <<
               % possible inconsistent dependencies, do not apply chain rule
               msgpri("Possible inconsistent dependencies in",u,
                      nil,nil,nil);
               w := mksq(w,1) >>
             else if x then
                % declared dependency
                if (v memq (x:=cdr x))
                  then w := mksq(w,1)
                 else w := df!-chain!-rule(u, v, x)
             else if y then
              % possible dependency of kernel arglist on v
              w := if smember(v,y) then mksq(w,1) else df!-chain!-rule(u, v, y)
             else w := mksq(w,1)
           >>
         else w := mksq(w,1);
      go to e
   end;

symbolic procedure get!-all!-kernels(plis);
   % plis is a list of expressions in prefix form
   % result is a list of all kernels in the simplified expressions
   if atom plis then nil
    else union((union(kernels numr sq,kernels denr sq)
                  where sq := simp car plis),
               get!-all!-kernels(cdr plis));

symbolic procedure df!-chain!-rule(u, v, klis);
   % compute the derivative of u w.r.t. v with intermediate
   %  variables from klis, via the chain rule
   % returns a s.q.
   begin scalar w;
     w := nil ./ 1;
     for each krnl in klis do
       w := addsq(w, multsq(simp{'df,u,krnl},simp{'df,krnl,v}));
     return w;
   end;

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
      if not(n=1) then
         result := multsq( (((u .** (n-1)) .* n) .+ nil) ./ 1, result);
      return result
   end;

symbolic procedure not_df_p y;
   % True if the SQ form y is not a df kernel.
   not(denr y eq 1 and
       not domainp (y := numr y) and eqcar(mvar y, 'df));

% Compute a dfn-property name corresponding to the argument number
% of an operator expression. Here we assume that most functions
% will have not more than 3 arguments.

symbolic procedure dfn_prop(w);
  (if n=1 then 'dfn else if n=2 then 'dfn2 else if n=3 then 'dfn3
    else mkid('dfn,n))
     where n=length cdr w;

% The following three functions, and the hooks to this code above, were
% suggested by Gerhard Post and Marcel Roelofs.

symbolic procedure find_sub_df(df_args,df_values);
   df_values and
      (is_sub_df(df_args,car df_values) or
       find_sub_df(df_args,cdr df_values));

symbolic procedure is_sub_df(df_args,df_value);
   begin scalar df_set,kernel,n,entry;
     if car(df_args) neq cadar(df_value) then return nil;  % check fns.
     df_args := dot_df_args cdr df_args;
     df_set  := cddar df_value;
     while df_set and df_args do              % Check differentiations.
       <<kernel := car df_set;
         if cdr df_set and fixp(n := cadr df_set)
           then df_set := cdr df_set else n := 1;
         if (entry := atsoc(kernel,df_args))
                 and (n := cdr entry-n) geq 0
           then rplacd(entry,n) else df_args:=nil;
         df_set := cdr df_set>>;
     return if df_args then (cadr(df_value) . df_args);
   end;

symbolic procedure dot_df_args l;
   begin scalar kernel,n,df_args;
     while l do
       <<kernel := car l;
         if cdr l and fixp(n := cadr l) then l := cdr l else n := 1;
         df_args := (kernel . n) . df_args;
         l := cdr l>>;
     return df_args;
   end;

symbolic procedure merge!-ind!-vars(u,v);
   % Consider (df u v) where u = (df a b c d ...)
   % It is non-commuting if a = v or if a in (b c d ...)
   % i.e. if a in (v b c d ...)
   if !*nocommutedf or
      (not !*commutedf and (cadr u memq (v . cddr u)))
   then derad!*(v,cddr u) else derad(v,cddr u);

symbolic procedure derad!*(u,v);        % Non-commuting derad
   %% Return the canonical list of differentiation variables
   %% equivalent to v,u, where v is a LIST of previus differentiation
   %% variables, when df(df(f(v,u), v), u) is simplified to
   %% df(f(v,u), v, u).  Essentially just cons u onto v.
   reverse
      if u eq car(v:=reverse v) then   % x,y, y
         2 . v
      else if numberp car v and u eq cadr v then % x,y,n, y
         (car v + 1) . cdr v
      else u . v;                       % x,y, z

symbolic procedure derad(u,v);
   if null v then list u
    else if numberp car v then car v . derad(u,cdr v)
    else if u=car v then if cdr v and numberp cadr v
                           then u . (cadr v + 1) . cddr v
                          else u . 2 . cdr v
    else if ordp(u,car v) then u . v
    else car v . derad(u,cdr v);

symbolic procedure letdf(u,v,w,x,b);
   begin scalar y,z,dfn;
        if atom cadr x then go to b
         else if not idp caadr x then typerr(caadr x,"operator")
         else if not get(caadr x,'simpfn)
          then <<redmsg(caadr x,"operator"); mkop caadr x>>;
        rmsubs();
        dfn := dfn_prop cadr x;
        if not(mcond!* eq 't)
                or not frlp cdadr x
                or null cddr x
                or cdddr x
                or not frlp cddr x
                or not idlistp cdadr x
                or repeats cdadr x
                or not(caddr x member cdadr x)
         then go to b;
        z := lpos(caddr x,cdadr x);
        if not get(caadr x,dfn)
            then put(caadr x,
                     dfn,
                     nlist(nil,length cdadr x));
        w := get(caadr x,dfn);
        if length w neq length cdadr x
          then rerror(poly,17,
                      list("Incompatible DF rule argument length for",
                            caadr x));
   a:   if null w or z=0 then return errpri1 u
         else if z neq 1
          then <<y := car w . y; w := cdr w; z := z-1; go to a>>
         else if null b then y := append(reverse y,nil . cdr w)
         else y := append(reverse y,(cdadr x . v) . cdr w);
        return put(caadr x,dfn,y);
   b:   %check for dependency;
        if caddr x memq frlis!* then return nil
         else if idp cadr x and not(cadr x memq frlis!*)
           then depend1(cadr x,caddr x,t)
         else if not atom cadr x and idp caadr x and frlp cdadr x
          then depend1(caadr x,caddr x,t);
        return nil
   end;

symbolic procedure frlp u;
   null u or (car u memq frlis!* and frlp cdr u);

symbolic procedure lpos(u,v);
   if u eq car v then 1 else lpos(u,cdr v)+1;


endmodule;


end;
