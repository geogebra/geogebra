module solve1; % Fundamental SOLVE procedures.

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


fluid '(!*allbranch !*arbvars !*exp !*ezgcd !*fullroots !*limitedfactors
        !*multiplicities !*notseparate !*numval !*numval!* !*precise
        !*rounded !*solvealgp !*solvesingular !*varopt !!gcd !:prec!:
        asymplis!* alglist!* dmode!* kord!* vars!*
        !*!*norootvarrenamep!*!*);

% NB: !*!*norootvarrenamep!*!* is internal to this module, and should
%     *never* be changed by a user.

global '(!!arbint multiplicities!* assumptions requirements);

switch allbranch,arbvars,fullroots,multiplicities,solvesingular;
       % nonlnr.

% !*varopt := t;   % Set now in alg/intro.

put('fullroots,'simpfg,'((t (rmsubs))));

flag('(!*allbranch multiplicities!* assumptions requirements),
     'share);

% Those switches that are on are now set in entry.red.

% !*allbranch     := t; % Returns all branches of solutions if T.
% !*arbvars       := t; % Presents solutions to singular systems
                      % in terms of original variables if NIL
% !*multiplicities      Lists all roots with multiplicities if on.
% !*fullroots   := t; % Computes full roots of cubics and quartics.
% !*solvesingular := t; % Default value.
%  !!gcd                SOLVECOEFF returns GCD of powers of its arg in
%                       this.  With the decompose code, this should
%                       only occur with expressions of form x^n + c.

Comment most of these procedures return a list of "solve solutions".  A
   solve solution is a list with three fields: the list of solutions,
   the corresponding variables (or NIL if the equations could not be
   solved --- in which case there is only one solution in the first
   field) and the multiplicity;

symbolic procedure solve0(elst,xlst);
   % This is the driving function for the solve package.
   % Elst is any prefix expression, including a list prefixed by LIST.
   % Xlst is a kernel or list of kernels.  Solves eqns in elst for
   % vars in xlst, returning either a list of solutions, a single
   % solution, or NIL if the solutions are inconsistent.
   begin scalar !*exp,!*notseparate,w;  integer neqn;
   % NOTSEPARATE used be set on.  However, this led to wrong results in
   % solve(({ex*z+y*b1-x*b2,-z*b1+ex*y+x*b3,z*b2-y*b3+ex*x}
   %        where ex=sqrt(-b1**2-b2**2-b3**2)),{x,y,z});
   !*exp := t; % !*notseparate := t;
   % Form a list of equations as expressions.
   elst := for each j in solveargchk elst collect simp!* !*eqn2a j;
   neqn := length elst;   % There must be at least one.
   % Determine variables.
   if null xlst
     then <<vars!* := solvevars elst;
            terpri();
            if null vars!* then nil
             else if cdr vars!*
              then <<prin2!* "Unknowns: "; maprin('list . vars!*)>>
             else <<prin2!* "Unknown: "; maprin car vars!*>>;
            terpri!* nil>>
    else <<xlst := solveargchk xlst;
           vars!* := for each j in xlst collect !*a2k j>>;
   if length vars!* = 0
     then rerror(solve,3,"SOLVE called with no variables");
   if neqn = 1 and length vars!* = 1
      then if null numr car elst
              then return if !*solvesingular
                          then {{{!*f2q makearbcomplex()},vars!*,1}}
                          else nil
              else if solutionp(w := solvesq(car elst,car vars!*,1))
                      or null !*solvealgp
                      or univariatep numr car elst
                      then return w;
   % More than one equation or variable, or single eqn has no solution.
    elst := for each j in elst collect numr j;
    w := solvesys(elst,vars!*);
    if null w then return nil;
    if car w memq {'t,'inconsistent,'singular} then return cdr w
     else if car w eq 'failed or null car w
      then return for each j in elst collect list(list(j ./ 1),nil,1)
     else errach list("Improper solve solution tag",car w)
   end;

symbolic procedure basic!-kern u;
   <<for each k in u do w:=union(basic!-kern1 k,w); w>> where w=nil;

symbolic procedure basic!-kern1 u;
   % Expand a composite kernel.
   begin scalar w;
      if atom u then return {u} else
       if algebraic!-function car u and
        (w := allbkern for each q in cdr u collect simp q)
        then return w
       else return {u}
   end;

symbolic procedure algebraic!-function q;
 % Returns T if q is a name of an operator with algebraic evaluation
 % properties.
   flagp(q,'realvalued) or
      flagp(q,'alwaysrealvalued) or
      get(q,'!:rd!:) or
      get(q,'!:cr!:) or
      get(q,'opmtch);

symbolic procedure allbkern elst;
 % extract all elementary kernels from list of quotients.
  if null elst then nil else
   union(basic!-kern kernels numr car elst, allbkern cdr elst);

symbolic procedure solvevars elst;
 <<for each j in allbkern elst do
      if not constant_exprp j then s := ordad(j,s);
   s>> where s=nil;

symbolic procedure solutionp u;
   null u or cadar u and not root_of_soln_p caar u and solutionp cdr u;

symbolic procedure root_of_soln_p u;
   null cdr u and kernp (u := car u) and eqcar(mvar numr u,'root_of);

symbolic procedure solveargchk u;
   if getrtype (u := reval u) eq 'list then cdr reval u
    else if atom u or not(car u eq 'lst) then list u
    else cdr u;


% ***** Procedures for collecting side information about the solution.

symbolic procedure solve!-clean!-info(fl,flg);
   % Check for constants and multiples in side relations fl.
   % If flg is t then relations are factorised and constants removed.
   % Otherwise the relations are autoreduced and the presence of a
   % constant truncates the whole list.
  begin scalar r,w,p;
     for each form in cdr fl do if not p then
        if constant_exprp(form := reval form) then
          (if not flg then p := r := {1})
        else
          if flg then
            for each w in cdr fctrf numr simp form do
             <<w := absf car w;
               if not member(w,r) then r := w . r>>
          else
             <<w:= absf numr simp{'nprimitive,form};
               if not domainp w then w := caar sqfrf w;
               for each z in r do if w then
                 if null cdr qremf(z,w) then r:=delete(z,r)
                  else if null cdr qremf(w,z) then w := nil;
               if w then r := w . r>>;
     return 'list . for each q in r collect prepf q
  end;


% ***** Procedures for solving a single eqn *****

symbolic procedure solvesq (ex,var,mul);
   % We assume that if a solution is a "root_of", then the denominator
   % will not reduce to zero when this root is substituted in.  Without
   % this test, the subf calculation can be very, very long.
   begin scalar r,x;
      r:= for each w in solvesq1(ex,var,mul) join
        if null cadr w
           or eqcar(x := prepsq caar w,'root_of)
           or numr subfx(denr ex,{caadr w . x}) then {w};
      if r and not domainp denr ex then
           assumptions:=append(assumptions,{prepf denr ex});
      return r
  end;

symbolic procedure subfx(u,v);
   (if errorp x then nil ./ 1 else car x)
      where x = errorset2 {'subf,mkquote u,mkquote v};

symbolic procedure solvesq1 (ex,var,mul);
   % Attempts to find solutions for standard quotient ex with respect to
   % top level occurrences of var and kernels containing variable var.
   % Solutions containing more than one such kernel are returned
   % unsolved, and solve1 is applied to the other solutions.  Integer
   % mul is the multiplicity passed from any previous factorizations.
   % Returns a list of triplets consisting of solutions, variables and
   % multiplicity.
     begin scalar e1,oldkorder,x1,y,z;  integer mu;
      ex := numr ex;
      if null(x1 := topkern(ex,var)) then return nil;
      oldkorder := setkorder list var;
      % The following section should be extended for other cases.
      e1 := reorder ex;
      setkorder oldkorder;
      if !*modular then
      <<load!_package 'modsr; return msolvesys({e1},x1,nil)>>;
      if mvar e1 = var and null cdr x1 and ldeg e1 =1
        then return {{{quotsq(negf reorder red e1 ./1,
                              reorder lc e1 ./ 1)},
                     {var},mul}};
     % don't call fctrf here in rounded mode, so polynomial won't get
     % rounded (since factoring isn't going to succeed)
      ex := if !*rounded then {1,ex . 1} else fctrf ex;
      % Now process monomial.
      if domainp car ex then ex := cdr ex
       else ex := (car ex . 1) . cdr ex;
      for each j in ex do
        <<e1 := car j;
          x1 := topkern(e1,var);
          mu := mul*cdr j;
          % Test for decomposition of e1.  Only do if rounded is off.
          if null !*rounded and length x1=1 and length kernels e1<5
             and length(y := decomposef1(e1,nil))>1
             and (y := solvedecomp(reverse y,car x1,mu))
            then z := solnsmerge(y,z)
           else if (degr(y := reorder e1,var) where kord!*={var}) = 1
              and not smember(var,delete(var,x1))
                  % var may not be unique here.
            then <<y := {{quotsq(!*f2q negf reorder red y,
                                 !*f2q reorder lc y)},
                         {var},mu};
                   z := y . z>>
           else if x1
            then z := solnsmerge(
             if null cdr x1 then solve1(e1,car x1,var,mu)
              else if (y := principle!-of!-powers!-soln(e1,x1,var,mu))
                          neq 'unsolved
               then y
              else if not smemq('sol,x1 := solve!-apply!-rules(e1,var))
               then solvesq(x1,var,mu)
              else mkrootsof(e1 ./ 1,var,mu),
                 z)>>;
      return z
   end;

symbolic procedure solvedecomp(u,var,mu);
   % Solve for decomposed expression.  At the moment, only one
   % level of decomposition is considered.
   begin scalar failed,x,y;
      if length(x := solve0(car u,cadadr u))=1 then return nil;
      u := cdr u;
      while u do
       <<y := x;
         x := nil;
         for each j in y do
            if caddr j neq 1 or null cadr j
              then <<lprim list("Tell Hearn solvedecomp",y,u);
                     failed := t;
                     nil>>
             else x := solnsmerge(
                        solve0(list('difference,prepsq caar j,caddar u),
                               if cdr u then cadadr u else var),x);
         if failed then u := nil else u := cdr u>>;
      return if failed then nil else adjustmul(x,mu)
   end;

symbolic procedure adjustmul(u,n);
   % Multiply the multiplicities of the solutions in u by n.
   if n=1 then u
    else for each x in u collect list(car x,cadr x,n*caddr x);

symbolic procedure solve1(e1,x1,var,mu);
   Comment e1 is a standard form, non-trivial in the kernel x1, which
      is itself a function of var, mu is an integer.  Uses roots of
      unity, known solutions, inverses, together with quadratic, cubic
      and quartic formulas, treating other cases as unsolvable.
      Returns a list of solve solutions;
   begin scalar !*numval!*;
      !*numval!* := !*numval;    % Keep value for use in solve11.
      return solve11(e1,x1,var,mu)
   end;

symbolic procedure solve11(e1,x1,var,mu);
   begin scalar !*numval,b,coefs,hipow,w;  integer n;
      % The next test should check for true degree in var.
      if null !*fullroots and null !*rounded and numrdeg(e1,var)>2
        then return mkrootsof(e1 ./ 1,var,mu);
      !*numval := t;   % Assume that actual numerical values wanted.
      coefs:= errorset!*(list('solvecoeff,mkquote e1,mkquote x1),nil);
      if atom coefs or atom x1 and x1 neq var
        then return mkrootsof(e1 ./ 1,var,mu);
          % solvecoeff problem - no soln.
      coefs := car coefs;
      n:= !!gcd;   % numerical gcd of powers.
      hipow := car(w:=car reverse coefs);
      if not domainp numr cdr w then
        assumptions:=append(assumptions,{prepf numr cdr w});
      if not domainp denr cdr w then
        assumptions:=append(assumptions,{prepf denr cdr w});
      if hipow = 1
        then return begin scalar lincoeff,y,z;
           if null cdr coefs then b := 0
            else b := prepsq quotsq(negsq cdar coefs,cdadr coefs);
           if n neq 1 then b := list('expt,b,list('quotient,1,n));
           % We may need to merge more solutions in the following if
           % there are repeated roots.
           for k := 0:n-1 do   % equation in power of var.
            <<lincoeff := list('times,b,
                          mkexp list('quotient,list('times,k,2,'pi),n));
              lincoeff := simp!* lincoeff where alglist!* = nil . nil;
              if x1=var
                then y := solnmerge(list lincoeff,list var,mu,y)
               else if not idp(z := car x1)
                then typerr(z,"solve operator")
               else if z := get(z,'solvefn)
                then y := solnsmerge(apply1(z,
                                           list(cdr x1,var,mu,lincoeff))
                                 ,y)
               else if (z := get(car x1,'inverse))   % known inverse
                then y := solnsmerge(solvesq(subtrsq(simp!* cadr x1,
                                 simp!* list(z,mk!*sq lincoeff)),
                                 var,mu),y)
               else y := list(list subtrsq(simp!* x1,lincoeff),nil,mu)
                            . y>>;
         return y
        end
       else if hipow=2
        then return <<x1 := exptsq(simp!* x1,n);
                      % allows for power variable.
                      w := nil;
                      for each j in solvequadratic(getcoeff(coefs,2),
                                    getcoeff(coefs,1),getcoeff(coefs,0))
                         do w :=
                            solnsmerge(solvesq(subtrsq(x1,j),var,mu),w);
                      w>>
       else return solvehipow(e1,x1,var,mu,coefs,hipow)
 end;

symbolic procedure solnsmerge(u,v);
   if null u then v
    else solnsmerge(cdr u,solnmerge(caar u,cadar u,caddar u,v));

symbolic procedure getcoeff(u,n);
   % Get the nth coefficient in the list u as a standard quotient.
   if null u then nil ./ 1
    else if n=caar u then cdar u
    else if n<caar u then nil ./ 1
    else getcoeff(cdr u,n);

symbolic procedure putcoeff(u,n,v);
   % Replace the nth coefficient in the list u by v.
   if null u then list(n . v)
    else if n=caar u then (n . v) . cdr u
    else if n<caar u then (n . v) . u
    else car u . putcoeff(cdr u,n,v);

symbolic procedure solvehipow(e1,x1,var,mu,coefs,hipow);
   % Solve a system with degree greater than 2.  Since we cannot write
   % down the solution directly, we look for various forms that we
   % know how to solve.
   begin scalar b,c,d,f,rcoeffs,z;
      f:=(hipow+1)/2;
      d:=exptsq(simp!* x1,!!gcd);
      rcoeffs := reverse coefs;
      return if solve1test1(coefs,rcoeffs,f)  % Coefficients symmetric.
        then if f+f=hipow+1   % odd
               then <<c:=addsq(d, 1 ./ 1);
                      solnsmerge(solvesq(c,var,mu),
                             solvesq(quotsq(e1 ./ 1, c),var,mu))>>
              else <<coefs := putcoeff(coefs,0,2 ./ 1);
                     coefs := putcoeff(coefs,1,simp!* '!!x);
                     c:=addsq(multsq(getcoeff(coefs,f+1),
                                     getcoeff(coefs,1)),
                              getcoeff(coefs,f));
                     for j:=2:f do <<
                         coefs := putcoeff(coefs,j,
                            subtrsq(multsq(getcoeff(coefs,1),
                                           getcoeff(coefs,j-1)),
                                    getcoeff(coefs,j-2)));
                         c:=addsq(c,multsq(getcoeff(coefs,j),
                                           getcoeff(coefs,f+j)))>>;
                     for each j in solvesq(c,'!!x,mu) do z :=
                      solnsmerge(
                      solvesq(addsq(1 ./ 1,multsq(d,subtrsq(d,caar j))),
                                var,caddr j),z);
                     z>>
       else if solve1test2(coefs,rcoeffs,f)
          % coefficients antisymmetric
        then <<c:=addsq(d,(-1 ./1));
               b := solvesq(c,var,mu);
               e1 := quotsq(e1 ./ 1, c);
               if f+f = hipow
                then <<c := addsq(d,(1 ./ 1));
                       b := solnsmerge(solvesq(c,var,mu),b);
                       e1 := quotsq(e1,c)>>;
               solnsmerge(solvesq(e1,var,mu),b)>>
          % equation has no symmetry
          % now look for real roots before cubics or quartics.  We must
          % reverse the answer from solveroots so that roots come out
          % in same order from SOLVE.
%      else if !*numval!* and dmode!* memq '(!:rd!: !:cr!:)
      % this forces solveroots independent of numval.
       else if !*rounded and univariatep e1
        then reversip solveroots(e1,var,mu)
       else if null !*fullroots then mkrootsof(e1 ./ 1,var,mu)
       else if hipow=3
        then <<for each j in solvecubic(getcoeff(coefs,3),
                                        getcoeff(coefs,2),
                                        getcoeff(coefs,1),
                                        getcoeff(coefs,0))
                     do z := solnsmerge(solvesq(subtrsq(d,j),var,mu),z);
                z>>
       else if hipow=4
        then <<for each j in solvequartic(getcoeff(coefs,4),
                                          getcoeff(coefs,3),
                                          getcoeff(coefs,2),
                                          getcoeff(coefs,1),
                                          getcoeff(coefs,0))
                     do z := solnsmerge(solvesq(subtrsq(d,j),var,mu),z);
                z>>
       else mkrootsof(e1 ./ 1,var,mu)
          % We can't solve quintic and higher.
   end;

symbolic procedure solnmerge(u,varlist,mu,y);
   % Merge solutions in case of multiplicities. It may be that this is
   % only needed for the trivial solution x=0.
   if null y then list list(u,varlist,mu)
    else if u = caar y and varlist = cadar y
           then list(caar y,cadar y,mu+caddar y) . cdr y
    else car y . solnmerge(u,varlist,mu,cdr y);

symbolic procedure nilchk u; if null u then !*f2q u else u;

symbolic procedure solve1test1(coefs,rcoeffs,f);
   % True if equation is symmetric in its coefficients. f is midpoint.
   begin integer j,p;
      if null coefs or caar coefs neq 0 or null !*fullroots
        then return nil;
      p := caar coefs + caar rcoeffs;
   a: if j>f then return t
       else if (caar coefs + caar rcoeffs) neq p
               or cdar coefs neq cdar rcoeffs then return nil;
      coefs := cdr coefs;
      rcoeffs := cdr rcoeffs;
      j := j+1;
      go to a
   end;

symbolic procedure solve1test2(coefs,rcoeffs,f);
   % True if equation is antisymmetric in its coefficients. f is
   %  midpoint.
   begin integer j,p;
      if null coefs or caar coefs neq 0 or null !*fullroots
        then return nil;
      p := caar coefs + caar rcoeffs;
   a: if j>f then return t
       else if (caar coefs + caar rcoeffs) neq p
          or numr addsq(cdar coefs,cdar rcoeffs) then return nil;
      coefs := cdr coefs;
      rcoeffs := cdr rcoeffs;
      j := j+1;
      go to a
   end;

symbolic procedure solveabs u;
   begin scalar mu,var,lincoeff;
      var := cadr u;
      mu := caddr u;
      lincoeff := cadddr u;
      u := simp!* caar u;
      return solnsmerge(solvesq(addsq(u,lincoeff),var,mu),
                    solvesq(subtrsq(u,lincoeff),var,mu))
   end;

put('abs,'solvefn,'solveabs);

symbolic procedure solveexpt u;
   begin scalar c,mu,var,lincoeff;
      var := cadr u;
      mu := caddr u;
      lincoeff := cadddr u;
%       the following line made solve(x^(1/2)=0) etc. wrong
%      if null numr lincoeff then return nil;
      u := car u;
      return if freeof(car u,var)    % c**(...) = b.
    then if null numr lincoeff then nil else
        <<if !*allbranch
                 then <<!!arbint:=!!arbint+1;
            c:=list('times,2,'i,'pi,
                list('arbint,!!arbint))>>
                else c:=0;
                solvesq(subtrsq(simp!* cadr u,
                     quotsq(addsq(solveexpt!-logterm lincoeff,
                                  simp!* c),
                        simp!* list('log,car u))),var,mu)>>
       else if freeof(cadr u,var) and null numr lincoeff  %(...)**b=0.
        then if check!-condition {'equal,{'sign,cadr u},1}
                 then solvesq(simp!* car u,var,mu)
                else solveexpt!-rootsof(u,lincoeff,var,mu)
       else if freeof(cadr u,var)   %  (...)**(m/n) = b;
        then if ratnump cadr u
           then solve!-fractional!-power(u,lincoeff,var,mu)
          else <<   %  (...)**c = b.
                 if !*allbranch
                   then <<!!arbint:=!!arbint+1;
                          c := mkexp list('quotient,
                                          list('times,2,'pi,
                                               list('arbint,!!arbint)),
                                               cadr u)>>
%                         c := mkexp list('times,
%                                         list('arbreal,!!arbint))>>
                  else c:=1;
                 solvesq(subtrsq(simp!* car u,
                multsq(simp!* list('expt,
                           mk!*sq lincoeff,
                           mk!*sq invsq
                              simp!* cadr u),
                       simp!* c)),var,mu)>>
        %  (...)**(...) = b : transcendental.
%   else list list(list subtrsq(simp!*('expt . u),lincoeff),nil,mu)
    else solveexpt!-rootsof(u,lincoeff,var,mu)
   end;

symbolic procedure solveexpt!-rootsof(u,lincoeff,var,mu);
    mkrootsof(subtrsq(simp!*('expt . u),lincoeff),var,mu);

put('expt,'solvefn,'solveexpt);

symbolic procedure solveexpt!-logterm lincoeff;
  % compute log(lincoeff), ignoring multiplicity and converting
  % log(-a) to log(a) + i pi.
   simp!* list('log,mk!*sq lincoeff);
%  if not !*allbranch or not minusf numr lincoeff
%     then simp!* list('log,mk!*sq lincoeff)
%  else
%   addsq(simp!*'(times i pi),
%         simp!* {'log,mk!*sq(negf numr lincoeff ./ denr lincoeff)});

symbolic procedure solvelog u;
   solvesq(subtrsq(simp!* caar u,simp!* list('expt,'e,mk!*sq cadddr u)),
          cadr u,caddr u);

put('log,'solvefn,'solvelog);

symbolic procedure solveinvpat(u,op);
   begin scalar c,f;
      f:=get(op,'solveinvpat);
      if smemq('arbint,f) then f:=subst(
         if !*allbranch then list('arbint,!!arbint:=!!arbint+1) else 0,
          'arbint,f);
      if not !*allbranch then f:={car f};
      return
       for each c in reverse f join
         solvesq(simp!*
                    subst(caar u,'(~v),subst(mk!*sq cadddr u,'(~r),c)),
                 cadr u,caddr u)
   end;


put('cos,'solveinvpat,
{ quote (- ~v + acos(~r) + 2*arbint*pi),
  quote (- ~v - acos(~r) + 2*arbint*pi) });

put('cos,'solvefn, '(lambda(u) (solveinvpat u 'cos)));

put('sin,'solveinvpat,
{ quote (- ~v + asin(~r) + 2*arbint*pi),
  quote (- ~v - asin(~r) + 2*arbint*pi + pi) });

put('sin,'solvefn, '(lambda(u) (solveinvpat u 'sin)));

put('sec,'solveinvpat,
{ quote (- ~v + asec(~r) + 2*arbint*pi),
  quote (- ~v - asec(~r) + 2*arbint*pi) });

put('sec,'solvefn, '(lambda(u) (solveinvpat u 'sec)));

put('csc,'solveinvpat,
{ quote (- ~v + acsc(~r) + 2*arbint*pi),
  quote (- ~v - acsc(~r) + 2*arbint*pi + pi) });

put('csc,'solvefn, '(lambda(u) (solveinvpat u 'csc)));

put('tan,'solveinvpat, { quote (- ~v + atan(~r) + arbint*pi)});

put('tan,'solvefn, '(lambda(u) (solveinvpat u 'tan)));

put('cot,'solveinvpat, { quote (- ~v + acot(~r) + arbint*pi)});

put('cot,'solvefn, '(lambda(u) (solveinvpat u 'cot)));

put('cosh,'solveinvpat,
{ quote (- ~v + acosh(~r) + 2*arbint*i*pi),
  quote (- ~v - acosh(~r) + 2*arbint*i*pi) });

put('cosh,'solvefn, '(lambda(u) (solveinvpat u 'cosh)));

put('sinh,'solveinvpat,
{ quote (- ~v + asinh(~r) + 2*arbint*i*pi),
  quote (- ~v - asinh(~r) + 2*arbint*i*pi + i*pi) });

put('sinh,'solvefn, '(lambda(u) (solveinvpat u 'sinh)));

put('sech,'solveinvpat,
{ quote (- ~v + asech(~r) + 2*arbint*i*pi),
  quote (- ~v - asech(~r) + 2*arbint*i*pi) });

put('sech,'solvefn, '(lambda(u) (solveinvpat u 'sech)));

put('csch,'solveinvpat,
{ quote (- ~v + acsch(~r) + 2*arbint*i*pi),
  quote (- ~v - acsch(~r) + 2*arbint*i*pi + i*pi) });

put('csch,'solvefn, '(lambda(u) (solveinvpat u 'csch)));

put('tanh,'solveinvpat, { quote (- ~v + atanh(~r) + arbint*i*pi)});

put('tanh,'solvefn, '(lambda(u) (solveinvpat u 'tanh)));

put('coth,'solveinvpat, { quote (- ~v + acoth(~r) + arbint*i*pi)});

put('coth,'solvefn, '(lambda(u) (solveinvpat u 'coth)));

symbolic procedure mkexp u;
   reval(aeval!*({'plus,{'cos,x},{'times,'i,{'sin,x}}}
            where x = reval u)
          where !*rounded = nil,dmode!* = nil);

symbolic procedure solvecoeff(ex,var);
   % Ex is a standard form and var a kernel.  Returns a list of
   % dotted pairs of exponents and coefficients (as standard quotients)
   % of var in ex, lowest power first, with exponents divided by their
   % gcd. This gcd is stored in !!GCD.
   begin scalar clist,oldkord;
      oldkord := updkorder var;
      clist := reorder ex;
      setkorder oldkord;
      clist := coeflis clist;
      !!gcd := caar clist;
      for each x in cdr clist do !!gcd := gcdn(car x,!!gcd);
      for each x in clist
         do <<rplaca(x,car x/!!gcd); rplacd(x,cdr x ./ 1)>>;
      return clist
   end;

symbolic procedure solveroots(ex,var,mu);
   % Ex is a square and content free univariate standard form, var the
   % relevant variable and mu the root multiplicity.  Finds insoluble,
   % complex roots of EX, returning a list of solve solutions.
   begin scalar x,y;
      x := reval list('root_val,mk!*sq(ex ./ 1));
      if not(car x eq 'list)
        then errach list("incorrect root format",ex);
      for each z in cdr x do
         y := solnsmerge(
                 solvesq(if not(car z eq 'equal)
                           then errach list("incorrect root format",ex)
                          else simpplus {cadr z,{'minus,caddr z}},
                         var,mu),
                         y);
      return y
   end;


% ***** Procedures for solving a system of eqns *****

Comment. The routines for solving systems of equations return a "tagged
solution list", where

        tagged solution list ::= tag . list of solve solution
        tag ::= t | nil | 'inconsistent | 'singular | 'failed
        solve solution ::= {solution rhs,solution lhs,multiplicity} |
                           {solution rhs,nil,multiplicity}
        solution rhs ::= list of sq
        solution lhs ::= list of kernel
        multiplicity ::= posint

If the tag is anything but t, the list of solve solutions is empty. See
solvenonlnrsys for more about the tags;

symbolic procedure solvesys(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvesys: tagged solution list
   % The expressions in exlis are reordered wrt the kernels in varlis,
   % and solved. For some switch settings, the internal
   % solve procedure may produce an error, leaving the kernel order
   % disturbed, so an errorset is used here.
   begin scalar oldkord,oldvars;
        % The standard methods for linear and polynomial system
        % don't work for non-prime modulus.
      if !*modular then
        <<load!-package 'modsr; return msolvesys(exlis,varlis,t)>>;
      oldvars := vars!*; % Protect this from change in sub-problems.
      oldkord := setkorder varlis;
      exlis := for each j in exlis collect reorder j;
      exlis := errorset!*({'solvemixedsys,mkquote exlis,mkquote varlis},
     t);
      setkorder oldkord;
      vars!* := oldvars;
      if errorp exlis then error1();
      return car exlis;
   end;

symbolic procedure solvemixedsys(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvemixedsys: tagged solution list
   % Solve a mixed linear/nonlinear system, solving the linear
   % part and substituting into the nonlinear until a core nonlinear
   % system remains. Assumes solvenonlnrsys and solvelnrsys both handle
   % all trivial cases properly.
   if null cadr(exlis := siftnonlnr(exlis,varlis)) then % linear
      solvelnrsys(car exlis,varlis)
   else if null car exlis then                          % nonlinear
      solvenonlnrsys(cadr exlis,varlis)
   else                                                 % mixed
      begin scalar x,y,z;
      x := solvelnrsys(car exlis,varlis) where !*arbvars = nil;
      if car x = 'inconsistent then return x
      else x := cadr x;
      z := pair(cadr x,foreach ex in car x collect mk!*sq ex);
      % David Hartley wanted the "resimp" in the next command, but
      % couldn't remember why ...
      exlis := foreach ex in cadr exlis join
                  if ex := numr subs2 resimp subf(ex,z) then {ex};
      varlis := setdiff(varlis,cadr x); % remaining free variables
      y := solvemixedsys(exlis,varlis);
      if car y memq {'inconsistent,'singular,'failed,nil} then return y
      else return t . foreach s in cdr y collect
            <<z := foreach pr in pair(cadr s,car s) join
                      if not smemq('root_of,cdr pr) then
                         {car pr . mk!*sq cdr pr};
              {append(car s,foreach ex in car x collect subsq(ex,z)),
               append(cadr s,cadr x),caddr s}>>;
      end;

symbolic procedure siftnonlnr(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> siftnonlnr: {list of sf, list of sf}
   % separate exlis into {linear,nonlinear}
   begin scalar lin,nonlin;
   foreach ex in exlis do
      if ex then
               if nonlnr(ex,varlis) then nonlin := ex . nonlin
               else lin := ex . lin;
   return {reversip lin,reversip nonlin};
   end;

symbolic procedure nonlnrsys(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> nonlnrsys: bool
   if null exlis then nil
   else nonlnr(car exlis,varlis) or nonlnrsys(cdr exlis,varlis);


symbolic procedure nonlnr(ex,varlis);
   % ex: sf, varlis: list of kernel -> nonlnr: bool
   if domainp ex then nil
   else if mvar ex member varlis then
        ldeg ex>1 or not freeofl(lc ex,varlis) or
        nonlnr(red ex,varlis)
   else not freeofl(mvar ex,varlis) or
        nonlnr(lc ex,varlis) or
        nonlnr(red ex,varlis);


% ***** Support for one_of and root_of *****.

symbolic procedure mkrootsoftag();
   begin scalar name; integer n;
 loop: n:=n #+1;
    name := intern compress append('(t a g _),explode n);
    if flagp(name,'used!*) then go to loop;
    return reval name;
  end;

symbolic procedure mkrootsof(e1,var,mu);
   begin scalar x,name;
      x := if idp var then var else 'q_;
      name := !*!*norootvarrenamep!*!* or mkrootsoftag();
      if not !*!*norootvarrenamep!*!*
        then while smember(x,e1) do
             x := intern compress append(explode x,explode '!_);
      e1 := prepsq!* e1;
      if x neq var then e1 := subst(x,var,e1);
      return list list(list !*kk2q list('root_of,e1,x,name),list var,mu)
   end;

put('root_of,'psopfn,'root_of_eval);

symbolic procedure root_of_eval u;
   begin scalar !*!*norootvarrenamep!*!*,x,n;
      if null cdr u then rederr "Too few arguments to root_eval";
      n := if cddr u then caddr u else mkrootsoftag();
      !*!*norootvarrenamep!*!* := n;
      x := solveeval1{car u,cadr u};
      if eqcar(x,'list) then x := cdr x else typerr(x,"list");
      x := foreach j in x collect if eqcar(j,'equal) then caddr j
                                   else typerr(j,"equation");
      if null x then rederr "solve confusion in root_of_eval"
       else if null cdr x then return car x
       else return{'one_of, 'list . x,n}
   end;

put('root_of,'subfunc,'subrootof);

symbolic procedure subrootof(l,expn);
   % Sets up a formal SUB expression when necessary.
   begin scalar x,y;
      for each j in cddr expn do
         if (x := assoc(j,l)) then <<y := x . y; l := delete(x,l)>>;
      expn := sublis(l,car expn)
                 . for each j in cdr expn collect subsublis(l,j);
        %to ensure only opr and individual args are transformed;
      if null y then return expn;
      expn := aconc!*(for each j in reversip!* y
                     collect list('equal,car j,aeval cdr j),expn);
      return if l then subeval expn
              else mk!*sq !*p2q mksp('sub . expn,1)
   end;

symbolic procedure polypeval u;
   % True if car u is a "pure" polynomial in cadr u (i.e., no other
   % kernels contain cadr u).
   begin scalar bool,v;
      v := cadr u;
      u := simpcar u;
      if cdr u neq 1 then return nil else u := kernels car u;
      while u and null bool do
        <<if v neq car u and smember(v,car u) then bool := t;
          u := cdr u>>;
      return null bool
   end;

put('polyp,'psopfn,'polypeval);

(algebraic <<

depend(!~p,!~x); % Needed for the simplification of the rule pattern.

let root_of(~p,~x,~tg)^~n =>
       sub(x=root_of(p,x,tg),
           -reduct(p,x)/coeffn(p,x,deg(p,x))) ^ (n-deg(p,x)+1)
               when polyp(p,x) and fixp n and deg(p,x)>=1 and n>=deg(p,x);

nodepend(!~p,!~x);

>>) where dmode!*=nil,!*modular=nil,!*rounded=nil,!*complex=nil;

symbolic procedure expand_cases u;
   begin scalar bool,sl,tags;
      sl:=list nil; tags:=list nil;
      u := reval u;
      if not eqcar(u,'list) then typerr(u,"equation list")
        else u := cdr u;
      if eqcar(car u,'list)
        then <<u := for each j in u collect
                        if eqcar(j,'list) then cdr j
                         else typerr(j,"equation list");
               bool := t>>
       else u := for each j in u collect {j};
      u := for each j in u join expand_case1(j,sl,tags);
      return 'list .
         for each j in u collect if null bool then car j else 'list . j
   end;

symbolic procedure expand_case1(u,sl,tags);
   if null u then nil
    else expand_merge(expand_case2(car u,sl,tags),
                      expand_case1(cdr u,sl,tags));

symbolic procedure expand_merge(u,v);
   if null v then for each j in u collect {j}
    else for each j in u join for each k in v collect j . k;

symbolic procedure expand_case2(u,sl,tags);
   begin scalar tag,v,var;
      var := cadr u; v := caddr u;
      if eqcar(v,'one_of)
        then <<tag := caddr v;
               if tag member tags then typerr(tag,"unique choice tag")
                 else if null assoc(tag,sl)
                  then cdr sl := (tag . cdadr v) . cdr sl;
               return if eqcar(cadr v,'list)
                      then for each j in cdadr v collect {'equal,var,j}
                      else typerr(cadr v,"list")>>
      % The next section doesn't do anything currently since root_of
      % is wrapped in a !*sq at this point.
       else if eqcar(v,'root_of)
        then <<tag := cadddr v;
               cdr tags := tag . cdr tags;
               if assoc(tag,sl) then typerr(tag,"unique choice tag")>>;
      return {u}
   end;

% Rules for solving inverse trigonometrical functions.

fluid '(solve_invtrig_soln!*);

share solve_invtrig_soln!*;

symbolic procedure check_solve_inv_trig(fn,equ,var);
   begin scalar x,s;
     x := evalletsub2({'(solve_trig_rules),{'simp!*,mkquote {fn,equ}}},
                      nil);
     if errorp x or not ((x := car x) freeof '(asin acos atan))
       then return nil;
     for each sol in cdr solveeval1 {mk!*sq subtrsq(x,simp!* {fn,0}),
                                    var} do
       if is_solution(sol,equ) then s := caddr sol . s;
     if null s then <<solve_invtrig_soln!* := 1;
                      return t>> % no solution found
      else if null cdr s then s := car s     % one solution
      else s := 'one_of . s;
     solve_invtrig_soln!* := {'difference,var,s};
     return t
   end;

flag('(check_solve_inv_trig),'boolean);

symbolic procedure is_solution(sol,equ);
   begin scalar var,s,rhs,result;
     var := cadr sol;
     rhs := caddr sol;
     equ := numr simp!* equ;
     if eqcar(rhs,'one_of)
       then result := check!-solns(for each s in cdr rhs collect
                                     {{simp!* s},{var},1},
                                   equ,var)
      else if eqcar(rhs,'root_of) then result := t
      else result := check!-solns({{{simp!* rhs},{var},1}},equ,var);
     return if not (result eq 'unsolved) then result else nil
   end;

symbolic procedure check!-condition u;
   null !*precise or eval formbool(u,nil,'algebraic);

endmodule;

end;
