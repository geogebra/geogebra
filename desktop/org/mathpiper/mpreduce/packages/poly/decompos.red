module decompos; % Decomposition of polynomials f(x) = g(h(x)).

% Author: Herbert Melenk <melenk@sc.zib-berlin.de>.

% Algorithms: 1. univariate case:
%            V.S. Alagar, M.Tanh: Fast Polynomial Decomposition
%            Algorithms, EUROCAL 1985, pp 150-153 (Springer).
%
%             2. multivariate lifting:
%            J. von zur Gathen: Functional Decomposition of Polynomials:
%            the Tame Case, J. Symbolic Computation (1990) 9, 281-299.

% Copyright (c) 1990 ZIB.
%
%     1-July-93  Replaced gensym calls by local name generator.
%                Otherwise decompose may produce different results
%                for identical input.
%    29-Apr.-93: completed normalization of multivariate results:
%                shifting sign and content (field: leading coefficient)
%                and absolute term to the 1st form.

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


global '(decomposegensym!*);

put('decompose,'psopfn,'decomposesf);

symbolic procedure decomposesf f;
  'list . reverse decomposef2(simp reval car f,t)
        where !*factor=nil,!*exp=t;

symbolic procedure decomposef1(f,msg);
    decomposef2(f ./ 1 ,msg);

symbolic procedure decomposef2(f,msg);
   begin scalar hvars,r,rr,x,y,u,vars,newvars,d;
      decomposegensym!*:=1000;
      vars := decomposesfvars(numr f,nil);
      newvars := for each x in vars collect decomposegensym();
      d := denr f;
      if not domainp d
        then rerror(poly,18,typerr(prepsq f,"polynomial"));
      f := numr subf(numr f,pair(vars,newvars));
      if length vars = 1 then r := decomposesfuni0 f
       else r := decomposesfmulti(f,newvars);
      hvars := '(u v w a b c d e);
      for each x in vars do hvars := delete (x,hvars);
      while r do
       <<if cdr r
           then <<y := x; x := nil;
                  while null x do
                     if hvars then <<x := car hvars; hvars := cdr hvars;
                                     if not(x=reval x) then x := nil>>
                      else x:=decomposegensym();
                  u := prepsq subsq(car r,list(mvar numr car r . x));
                  if d neq 1 then<<u:=list('QUOTIENT,u,prepf d);d:=1>>;
                  rr := (if y then list('EQUAL,y,u) else u) . rr>>
          else <<u := prepsq car r;
                 y := x;
                 rr := (if y then list('EQUAL,y,u) else u) . rr>>;
         r := cdr r>>;
      rr := subla(pair(newvars,vars),car rr) . cdr rr;
      return rr
   end;

symbolic procedure decomposesfvars(f,v);
    % Select the kernels from a standard form.
      if domainp f then v else
        decomposesfvars(red f,
            decomposesfvars(lc f,
                if not member(mvar f,v)
                 then append(v,list mvar f) else v));

symbolic procedure decomposesfuni0 f;
    for each p in decomposesfuni f collect (p ./ 1);

symbolic procedure decomposesfuni f;
    % Univariate variant.
   begin scalar x,y,res,ddfl,h,testf;
      integer n;
     n := ldeg f;
     if primep n then return list f;
     x := mvar f; y := decomposegensym();
     ddfl := decomposefctrf decomposedf(f,x);
    if length ddfl > 1 then
     for each d in ddfl do
      if null res and 0=remainder(n , (ldeg d + 1)) then
      <<h := numr decomposeint(d,x);
          if null testf then
               testf := addf(f,negf numr subf(f,list(x . y)));
          if quotf (testf,
                    addf(h,negf numr subf(h,list(x . y)))) then
          res := list(decomposebacksubstuni(f,h,x),h);
          if res and ldeg car res<2 then res:=nil;
      >>;
      if null res then return list f else
          return for each u in res join decomposesfuni u
   end;

symbolic procedure decomposefctrf f;
    % Generate all factors of f by combining the prime factors.
   begin scalar u,w,q;
       q := fctrf f; u:= cdr q;
       if length u = 1 and cdar u=1 then return list f;
          % eliminate the two trivial factors.
       w := delete(quotf(f,car q),decomposefctrf1 u);
       w := delete(1,w);
       return w;
   end;

symbolic procedure decomposefctrf1 v;
    % Collect all possible crossproducts from v.
    if null v then '(1) else
    begin scalar r,c,q;
     c:=car v;
     r:=decomposefctrf1 cdr v;
     q:=for i:=1:cdr c collect exptf(car c,i);
     return
      append(r,
       for each u in q join
         for each p in r collect
             multf(u,p)  );
    end;

symbolic procedure decomposebacksubstuni(f,h,x);
  begin scalar c,g,n,p,pars,ansatz,eqs;
     p := 1; n := ldeg f/ldeg h;
     for i:=0:n do
     <<c := mkid('coeff,i);
       pars := c . pars;
       ansatz := addf(multf(numr simp c,p) , ansatz);
       p := multf(p,h);
     >>;
     pars := reverse pars;
     ansatz := addf(f , negf ansatz);
     eqs := decomposecoeff(ansatz,list x);
     eqs := solveeval list('list . for each u in eqs collect prepf u,
                       'list . pars);
     eqs := cdr cadr eqs; % select the only solution.
     for i:= 0:n do
       g := addf(g,numr simp list('times,list('expt,x,i),
                                  caddr nth(eqs,i+1)));
     return g
   end;

symbolic procedure decomposedf(f,x);
   % Differentiate a polynomial wrt top-level variable x.
   % Returns a standard form.
    if domainp f or not(mvar f = x) then nil else
    if ldeg f = 1 then lc f else
    mvar f .** (ldeg f - 1) .* multf(lc f,ldeg f)
           .+ decomposedf(red f,x);

symbolic procedure decomposeint(f,x);
   % Integrate a polynomial (standard form) wrt the (main-)variable x.
   % Returns a standard quotient.
    if null f then nil ./ 1 else
    if domainp f then (x .** 1 .* f .+ nil) ./ 1 else
    addsq(multsq((x .** (ldeg f + 1) .* 1 .+ nil)./ 1 ,
                 multsq(lc f./1,1 ./ldeg f+1))
          ,  decomposeint(red f,x));

symbolic procedure decomposecoeff(f,vars);
   % Select the coefficients of f wrt vars.
     begin scalar o;
       o := setkorder vars;
       f := reorder f;
       setkorder o;
       return decomposecoeff1(f,vars)
     end;

symbolic procedure decomposecoeff1(f,vars);
     if domainp f then nil else
     if not member(mvar f,vars) then list f else
     nconc(decomposecoeff1(lc f,vars),decomposecoeff1(red f,vars));

symbolic procedure decomposetdg f;
    % calculate total degree
    if domainp f then 0 else
    max(ldeg f + decomposetdg lc f, decomposetdg red f);

symbolic procedure  decomposedegr(f,vl);
   if domainp f then vl else
   <<if ldeg f > cdr v then cdr v := ldeg f;
     decomposedegr(lc f,vl);
     decomposedegr(red f,vl);
     vl>> where v = assoc(mvar f,vl);

symbolic procedure compose (u,v);
    % Calculate f(x)=u(v(x)) for standard forms u,v.
    if domainp u then u else
         numr subf(u,list(mvar u . prepf v));

% Multivariate polynomial decomposition.
%
% Technique:
%    select a field as domain (rational),
%      map f to a strongly monic polynomial by variable transform,
%        map f to a univariate image,
%            decompose the univariate polynomial,
%        lift decomposition to multivariate,
%      convert back to original variables,
%    transform back to original domain (if possible).

symbolic procedure decomposesfmulti(f,vars);
   % Multivariant case: map to field (rationals).
    begin scalar dm,ft,r,rr,a,q,c,p1,p2;
      if null dmode!* or not flagp(dmode!*,'field) then
     <<setdmode('rational,t) where !*msg=nil; dm := t;
       ft := !*q2f resimp !*f2q f>> else ft := f;
     r := decomposesfmulti1(ft,vars);
     if dm then setdmode('rational,nil) where !*msg=nil;
     if null cdr r then return list(f./1);
 %   if null dm then return
 %      for each p in r collect (p ./ 1);
       % Convert back to integer polynomials.
     rr := for each p in reverse r collect simp prepf p;
     r := nil;
     while rr and cdr rr do
     <<p1 := car rr; p2 := cadr rr;
          % Propagate absolute term and content from p1 to p2.
       q := denr p1; a := numr p1;
       while not domainp a do a := red a;
       p1 := addf(numr p1,negf a);
       c := decomposenormfac p1;
       p1 := multsq(p1 ./ 1, 1 ./ c);
       p2 := subsq(p2,list(mvar numr p2 .
              list('quotient,
                   list('plus,list('times,decomposegensym(),prepf c),
                              prepf a),
                     prepf q)));
       r := p1 . r; rr := p2 . cddr rr>>;
     return car rr . r;
   end;

symbolic procedure decomposesfmulti1(f,vars);
  % Multivariate case: map to strongly monic polynomial.
    begin scalar lvars,ft,rt,x1,a0,kord,u,sigma;
     integer n,m;
       % get the variable with highest degree as main variable.
     u :=  decomposedegr(f,for each x in vars collect (x. 0));
     n := -1;
     for each x in u do
       if n<cdr x then <<n:=cdr x; x1 := car x>>;
     if n<2 then return list f;
     vars := x1 . delete(x1,vars);
     kord := setkorder vars;
     f := reorder f;
       % Convert f to a strongly monic polynomial.
     n := decomposetdg f;
     x1 := car vars;
     lvars := for each x in cdr vars collect (x . decomposegensym());
    again:
     if m>10 then << rt := list f; goto ret>>;
       % construct transformation sigma
     sigma := for each x in lvars collect x . random 1000;
     ft := numr subf(f,for each x in sigma collect
           (caar x . list('plus,cdar x,list('times,x1,cdr x))));
     if not domainp lc ft then <<m:=m+1; goto again>>;
     a0 := lc ft; ft := quotf(ft,a0);
     rt := decomposesfmnorm(ft,n,sublis(lvars,vars));
     if cdr rt then
      % Transform result back.
     <<rt := reverse rt;
       rt := numr subf(car rt,for each x in sigma collect
           (cdar x . list('difference,caar x,list('times,cdr x,x1))))
           . multf(a0,cadr rt) . cddr rt;
     >> else rt := list f;
   ret:
     setkorder kord;
     rt := for each p in rt collect reorder p;
        % try further decomposition of central polynomial.
     return if cdr rt and decomposetdg car rt>1 then
         append(reverse cdr rt,decomposesfmulti1(car rt,vars))
           else reverse rt;
   end;

symbolic procedure decomposelmon f;
   % Extract the variables of the leading monomial.
     if domainp f then nil else
     mvar f . decomposelmon lc f;

symbolic procedure decomposenormfac p1;
  if null dmode!* or not flagp(dmode!*,'field) then
     multf(numr mkabsfd decomposecont p1,decomposesign p1)
    else <<while not domainp p1 do p1:=lc p1; p1>>;

symbolic procedure decomposecont f;
   % Calculate the content of f if the domain is a ring.
      if domainp f then f else
      gcdf(decomposecont lc f, decomposecont red f);

symbolic procedure decomposesign f;
   % Compute a unit factor c such that the leading coefficient of
   % f/c is a positive integer.
   if domainp f then numr quotsq(f ./ 1,mkabsfd f)
        else decomposesign lc f;

symbolic procedure decomposesfmnorm(f,n,vars);
   % Multivariate case: map strongly monic polynomial to univariate
   % and lift result.
   begin scalar x,x1,f0,g,u,abort,h,k,tt,q,v;
      integer r,s;
     x1 := car vars;
    % Step 1.
     f0 := numr subf(f,for each y in cdr vars collect (y . 0));
     u := decomposesfuni f0;
        % For multivariate we accept degree=1 polynomials as nontrivial
        % but inhibit recursion.
     if null cdr u then <<u:=append(u,list !*k2f x1)>>;
     x := decomposegensym();
     g := numr subf(car u,list (x1 . x));
     r := ldeg g;
     h := cadr u; u := cddr u;
     while u do
     <<v := car u; u:= cdr u; h := numr subf(h,list(x1 . x));
       h := compose(h,v); >>;
      % Step 2.
     s := divide(n,r);
     if not(cdr s=0) then goto fail else s := car s;
     k := h;
     tt := compose(decomposedf(g,x),h);
      % Step 3: Hensel lifting in degree steps.
     for i:=1:s do
       if not abort then
        % Step 4: loop step.
       <<u := decomposehomog(addf(f,negf compose(g,k)),x1,i);
         q := quotf(u,tt);
         if u and null q then abort:=t else<<h:=q; k:=addf(k,h)>>
       >>;
      if abort then goto fail;
      % Step 5: test result and loop for lower part.
      h := k;
      if f = compose(g,h) then return list(g,h);
  fail:  % Exit: no decomposition found.
     return list f;
   end;

symbolic procedure decomposehomog(f,x,d);
   % F is a polynomial (standard form) in x and some other
   % variables. Select that part of f, where the coefficients
   % of x are monomials in total degree d.
   % Result is the sum (standard form) of these monomials.
   begin scalar u,v;
      u := decomposehomog1(f,x,d);
      for each m in u do v := addf(v,m);
      return v;
  end;

symbolic procedure decomposehomog1(f,x,d);
   % Select the monomials.
   if d<0 or null f then nil else
   if domainp f then (if d=0 then list f else nil)
   else begin scalar u1,u2;
     u1:= decomposehomog1(lc f,x,if mvar f = x then d
                                  else d-ldeg f);
     u2:= decomposehomog1(red f,x,d);
     return
        nconc(
         for each v in u1 collect
              multf(mvar f .** ldeg f .*1 .+ nil , v),
           u2);
  end;

symbolic procedure decomposegensym();
   compress(append('(!! !D !! !c !! !.),
            explode2(decomposegensym!*:=decomposegensym!*+1)));

endmodule;

end;
