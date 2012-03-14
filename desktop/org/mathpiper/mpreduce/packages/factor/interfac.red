module interfac;

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


% Modifications by: Anthony C. Hearn.

fluid '(m!-image!-variable
        poly!-vector
        polyzero
        unknowns!-list
        varlist);


%**********************************************************************;
%
% Routines that are specific to REDUCE.
%  These are either routines that are not needed in the HASH system
%  (which is the other algebra system that this factorizer
%  can be plugged into) or routines that are specifically
%  redefined in the HASH system.


%---------------------------------------------------------------------;
% The following would normally live in section:  ALPHAS
%---------------------------------------------------------------------;

symbolic procedure assoc!-alpha(poly,alist);  assoc(poly,alist);


%---------------------------------------------------------------------;
% The following would normally live in section:  COEFFTS
%---------------------------------------------------------------------;

symbolic procedure termvector2sf v;
  begin scalar r,w;
    for i:=car getv(v,0) step -1 until 1 do <<
      w:=getv(v,i);
            % degree . coefft;
      r:=if car w=0 then cdr w else
        (mksp(m!-image!-variable,car w) .* cdr w) .+ r
    >>;
    return r
  end;

symbolic procedure force!-lc(a,n);
% force polynomial a to have leading coefficient as specified;
    (lpow a .* n) .+ red a;

symbolic procedure merge!-terms(u,v);
  merge!-terms1(1,u,v,car getv(v,0));

symbolic procedure merge!-terms1(i,u,v,n);
  if i#>n then u
  else begin scalar a,b;
    a:=getv(v,i);
    if domainp u or not(mvar u=m!-image!-variable) then
      if not(car a=0) then errorf list("MERGING COEFFTS FAILED",u,a)
      else if cdr a then return cdr a
      else return u;
    b:=lt u;
    if tdeg b=car a then return
      (if cdr a then tpow b .* cdr a else b) .+
        merge!-terms1(i #+ 1,red u,v,n)
    else if tdeg b #> car a then return b .+ merge!-terms1(i,red u,v,n)
    else errorf list("MERGING COEFFTS FAILED ",u,a)
  end;

symbolic procedure list!-terms!-in!-factor u;
% ...;
  if domainp u then list (0 . nil)
  else (ldeg u . nil) . list!-terms!-in!-factor red u;

symbolic procedure try!-other!-coeffts(r,unknowns!-list,uv);
  begin scalar ldeg!-r,lc!-r,w;
    while not domainp r and (r:=red r) and not(w='complete) do <<
      if not depends!-on!-var(r,m!-image!-variable) then
        << ldeg!-r:=0; lc!-r:=r >>
      else << ldeg!-r:=ldeg r; lc!-r:=lc r >>;
      w:=solve!-next!-coefft(ldeg!-r,lc!-r,unknowns!-list,uv) >>
  end;


%---------------------------------------------------------------------;
% The following would normally live in section:  FACMISC
%---------------------------------------------------------------------;

symbolic procedure derivative!-wrt!-main!-variable(p,var);
% partial derivative of the polynomial p with respect to
% its main variable, var;
    if domainp p or (mvar p neq var) then nil
    else
     begin
      scalar degree;
      degree:=ldeg p;
      if degree=1 then return lc p; %degree one term is special;
      return (mksp(mvar p,degree-1) .* multf(degree,lc p)) .+
        derivative!-wrt!-main!-variable(red p,var)
     end;

symbolic procedure fac!-univariatep u;
% tests to see if u is univariate;
  domainp u or not multivariatep(u,mvar u);

symbolic procedure variables!.in!.form(a,sofar);
    if domainp a then sofar
    else <<
      if not memq(mvar a,sofar) then
        sofar:=mvar a . sofar;
      variables!.in!.form(red a,
        variables!.in!.form(lc a,sofar)) >>;


symbolic procedure degree!-in!-variable(p,v);
% returns the degree of the polynomial p in the
% variable v;
    if domainp p then 0
    else if lc p=0
     then errorf "Polynomial with a zero coefficient found"
    else if v=mvar p then ldeg p
    else max(degree!-in!-variable(lc p,v),
      degree!-in!-variable(red p,v));

symbolic procedure get!-height poly;
% find height (max coefft) of given poly;
  if null poly then 0
  else if numberp poly then abs poly
  else max(get!-height lc poly,get!-height red poly);


symbolic procedure poly!-minusp a;
    if a=nil then nil
    else if domainp a then minusp a
    else poly!-minusp lc a;

symbolic procedure poly!-abs a;
    if poly!-minusp a then negf a
    else a;

symbolic procedure fac!-printfactors l;
% procedure to print the result of factorize!-form;
% ie. l is of the form: (c . f)
%  where c is the numeric content (may be 1)
%  and f is of the form: ( (f1 . e1) (f2 . e2) ... (fn . en) )
%    where the fi's are s.f.s and ei's are numbers;
<< terpri();
  if not (car l = 1) then printsf car l;
  for each item in cdr l do
    printsf !*p2f mksp(prepf car item,cdr item) >>;


%---------------------------------------------------------------------;
% The following would normally live in section:  FACPRIM
%---------------------------------------------------------------------;

symbolic procedure invert!.poly(u,var);
% u is a non-trivial primitive square free multivariate polynomial.
% assuming var is the top-level variable in u, this effectively
% reverses the position of the coeffts: ie
%   a(n)*var**n + a(n-1)*var**(n-1) + ... + a(0)
% becomes:
%   a(0)*var**n + a(1)*var**(n-1) + ... + a(n) .               ;
  begin scalar w,invert!-sign;
    w:=invert!.poly1(red u,ldeg u,lc u,var);
    if poly!-minusp lc w then <<
      w:=negf w;
      invert!-sign:=-1 >>
    else invert!-sign:=1;
    return invert!-sign . w
  end;

symbolic procedure invert!.poly1(u,d,v,var);
% d is the degree of the poly we wish to invert.
% assume d > ldeg u always, and that v is never nil;
  if (domainp u) or not (mvar u=var) then
    (var to d) .* u .+ v
  else invert!.poly1(red u,d,(var to (d-ldeg u)) .* (lc u) .+ v,var);


symbolic procedure trailing!.coefft(u,var);
% u is multivariate poly with var as the top-level variable. we find
% the trailing coefft - ie the constant wrt var in u;
  if domainp u then u
  else if mvar u=var then trailing!.coefft(red u,var)
  else u;


%---------------------------------------------------------------------;
% The following would normally live in section:  IMAGESET
%---------------------------------------------------------------------;

symbolic procedure make!-image!-lc!-list(u,imset);
  reversip make!-image!-lc!-list1(u,imset,
    for each x in imset collect car x);

symbolic procedure make!-image!-lc!-list1(u,imset,varlist);
% If IMSET=((x1 . a1, x2 . a2, ... , xn . an)) (ordered) where xj is
% the variable and aj its value, then this fn creates n images of U wrt
% sets S(i) where S(i)= ((x1 . a1), ... , (xi . ai)). The result is an
% ordered list of pairs: (u(i) . X(i+1)) where u(i)= U wrt S(i) and
% X(i) = (xi, ... , xn) and X(n+1) = NIL.  VARLIST = X(1).
% (Note. the variables tagged to u(i) should be all those
% appearing in u(i) unless it is degenerate). The returned list is
% ordered with u(1) first and ending with the number u(n);
  if null imset then nil
  else if domainp u then list(!*d2n u . cdr varlist)
  else if mvar u=caar imset then
    begin scalar w;
      w:=horner!-rule!-for!-one!-var(
        u,caar imset,cdar imset,polyzero,ldeg u) . cdr varlist;
      return
        if polyzerop car w then list (0 . cdr w)
        else (w . make!-image!-lc!-list1(car w,cdr imset,cdr varlist))
    end
  else make!-image!-lc!-list1(u,cdr imset,cdr varlist);

symbolic procedure horner!-rule!-for!-one!-var(u,x,val,c,degg);
  if domainp u or not(mvar u=x)
    then if zerop val then u else addf(u,multf(c,!*n2f(val**degg)))
  else begin scalar newdeg;
    newdeg:=ldeg u;
    return horner!-rule!-for!-one!-var(red u,x,val,
       if zerop val then lc u
        else addf(lc u,
                  multf(c,!*n2f(val**(idifference(degg,newdeg))))),
                            newdeg)
  end;

symbolic procedure make!-image(u,imset);
% finds image of u wrt image set, imset, (=association list);
  if domainp u then u
  else if mvar u=m!-image!-variable then
    adjoin!-term(lpow u,!*n2f evaluate!-in!-order(lc u,imset),
                        make!-image(red u,imset))
  else !*n2f evaluate!-in!-order(u,imset);

symbolic procedure evaluate!-in!-order(u,imset);
% makes an image of u wrt imageset, imset, using horner's rule. result
% should be purely numeric;
  if domainp u then !*d2n u
  else if mvar u=caar imset then
    horner!-rule(evaluate!-in!-order(lc u,cdr imset),
      ldeg u,red u,imset)
  else evaluate!-in!-order(u,cdr imset);

symbolic procedure horner!-rule(c,degg,a,vset);
% c is running total and a is what is left;
  if domainp a
    then if zerop cdar vset then !*d2n a
          else (!*d2n a)+c*((cdar vset)**degg)
  else if not(mvar a=caar vset)
   then if zerop cdar vset then evaluate!-in!-order(a,cdr vset)
         else evaluate!-in!-order(a,cdr vset)+c*((cdar vset)**degg)
  else begin scalar newdeg;
    newdeg:=ldeg a;
    return horner!-rule(if zerop cdar vset
                          then evaluate!-in!-order(lc a,cdr vset)
                         else evaluate!-in!-order(lc a,cdr vset)
      +c*((cdar vset)**(idifference(degg,newdeg))),newdeg,red a,vset)
  end;


%---------------------------------------------------------------------;
% The following would normally live in section:  MHENSFNS
%---------------------------------------------------------------------;

symbolic procedure max!-degree(u,n);
% finds maximum degree of any single variable in U (n is max so far);
  if domainp u then n
  else if igreaterp(n,ldeg u) then
    max!-degree(red u,max!-degree(lc u,n))
  else max!-degree(red u,max!-degree(lc u,ldeg u));

symbolic procedure diff!-over!-k!-mod!-p(u,k,v);
% derivative of u wrt v divided by k (=number);
  if domainp u then nil
  else if mvar u = v then
    if ldeg u = 1 then quotient!-mod!-p(lc u,modular!-number k)
    else adjoin!-term(mksp(v,isub1 ldeg u),
      quotient!-mod!-p(
        times!-mod!-p(modular!-number ldeg u,lc u),
        modular!-number k),
      diff!-over!-k!-mod!-p(red u,k,v))
  else adjoin!-term(lpow u,
    diff!-over!-k!-mod!-p(lc u,k,v),
    diff!-over!-k!-mod!-p(red u,k,v));

symbolic procedure diff!-k!-times!-mod!-p(u,k,v);
% differentiates u k times wrt v and divides by (k!) ie. for each term
% a*v**n we get [n k]*a*v**(n-k) if n>=k and nil if n<k where
% [n k] is the binomial coefficient;
  if domainp u then nil
  else if mvar u = v then
    if ldeg u < k then nil
    else if ldeg u = k then lc u
    else adjoin!-term(mksp(v,ldeg u - k),
      times!-mod!-p(binomial!-coefft!-mod!-p(ldeg u,k),lc u),
      diff!-k!-times!-mod!-p(red u,k,v))
  else adjoin!-term(lpow u,
    diff!-k!-times!-mod!-p(lc u,k,v),
    diff!-k!-times!-mod!-p(red u,k,v));

symbolic procedure spreadvar(u,v,slist);
% find all the powers of V in U and merge their degrees into SLIST.
% We ignore the constant term wrt V;
  if domainp u then slist
  else <<
    if mvar u=v and not member(ldeg u,slist) then slist:=ldeg u . slist;
    spreadvar(red u,v,spreadvar(lc u,v,slist)) >>;


%---------------------------------------------------------------------;
% The following would normally live in section:  UNIHENS
%---------------------------------------------------------------------;

symbolic procedure root!-squares(u,sofar);
  if null u then pmam!-sqrt sofar
  else if domainp u then pmam!-sqrt(sofar+(u*u))
  else root!-squares(red u,sofar+(lc u * lc u));

%---------------------------------------------------------------------;
% The following would normally live in section:  VECPOLY
%---------------------------------------------------------------------;

symbolic procedure poly!-to!-vector p;
% spread the given univariate polynomial out into POLY-VECTOR;
    if domainp p then putv(poly!-vector,0,!*d2n p)
    else <<
      putv(poly!-vector,ldeg p,lc p);
      poly!-to!-vector red p >>;

symbolic procedure vector!-to!-poly(p,d,v);
% Convert the vector P into a polynomial of degree D in variable V;
  begin
    scalar r;
    if d#<0 then return nil;
    r:=!*n2f getv(p,0);
    for i:=1:d do
      if getv(p,i) neq 0 then r:=((v to i) .* getv(p,i)) .+ r;
    return r
  end;



endmodule;


end;
