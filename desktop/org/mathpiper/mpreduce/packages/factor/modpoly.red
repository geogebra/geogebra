module modpoly;   % Routines for performing arithmetic on multivariate
                  % polynomials with coefficients that are modular
                  % numbers as defined by modular!-plus etc.

% Authors: A. C. Norman and P. M. A. Moore, 1979.

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


fluid '(current!-modulus
        exact!-quotient!-flag
        m!-image!-variable
        modulus!/2
        reduction!-count);

% Note that the datastructure used is the same as that used in
% REDUCE except that it is assumed that domain elements are atomic.

symbolic smacro procedure comes!-before(p1,p2);
   % Similar to the REDUCE function ORDPP, but does not cater for non-
   % commutative terms and assumes that exponents are small integers.
    (car p1=car p2 and igreaterp(cdr p1,cdr p2)) or
       (not(car p1=car p2) and ordop(car p1,car p2));

symbolic procedure plus!-mod!-p(a,b);
   % form the sum of the two polynomials a and b working over the
   % ground domain defined by the routines % modular!-plus,
   % modular!-times etc. the inputs to this % routine are assumed to
   % have coefficients already in the required domain.
   if null a then b
   else if null b then a
   else if domainp a then
      if domainp b then !*n2f modular!-plus(a,b)
      else (lt b) .+ plus!-mod!-p(a,red b)
   else if domainp b then (lt a) .+ plus!-mod!-p(red a,b)
   else if lpow a = lpow b then
      adjoin!-term(lpow a,
         plus!-mod!-p(lc a,lc b),plus!-mod!-p(red a,red b))
   else if comes!-before(lpow a,lpow b) then
         (lt a) .+ plus!-mod!-p(red a,b)
   else (lt b) .+ plus!-mod!-p(a,red b);

symbolic procedure times!-mod!-p(a,b);
   if (null a) or (null b) then nil
   else if domainp a then multiply!-by!-constant!-mod!-p(b,a)
   else if domainp b then multiply!-by!-constant!-mod!-p(a,b)
   else if mvar a=mvar b then plus!-mod!-p(
     plus!-mod!-p(times!-term!-mod!-p(lt a,b),
                  times!-term!-mod!-p(lt b,red a)),
     times!-mod!-p(red a,red b))
   else if ordop(mvar a,mvar b) then
     adjoin!-term(lpow a,times!-mod!-p(lc a,b),times!-mod!-p(red a,b))
   else adjoin!-term(lpow b,
        times!-mod!-p(a,lc b),times!-mod!-p(a,red b));

symbolic procedure times!-term!-mod!-p(term,b);
   % Multiply the given polynomial by the given term.
    if null b then nil
    else if domainp b then
        adjoin!-term(tpow term,
            multiply!-by!-constant!-mod!-p(tc term,b),nil)
    else if tvar term=mvar b then
         adjoin!-term(mksp(tvar term,iplus2(tdeg term,ldeg b)),
                      times!-mod!-p(tc term,lc b),
                      times!-term!-mod!-p(term,red b))
    else if ordop(tvar term,mvar b) then
      adjoin!-term(tpow term,times!-mod!-p(tc term,b),nil)
    else adjoin!-term(lpow b,
      times!-term!-mod!-p(term,lc b),
      times!-term!-mod!-p(term,red b));

symbolic procedure difference!-mod!-p(a,b);
   plus!-mod!-p(a,minus!-mod!-p b);

symbolic procedure minus!-mod!-p a;
   if null a then nil
   else if domainp a then modular!-minus a
   else (lpow a .* minus!-mod!-p lc a) .+ minus!-mod!-p red a;


symbolic procedure reduce!-mod!-p a;
   % Converts a multivariate poly from normal into modular polynomial.
    if null a then nil
    else if domainp a then !*n2f modular!-number a
    else adjoin!-term(lpow a,reduce!-mod!-p lc a,reduce!-mod!-p red a);

symbolic procedure monic!-mod!-p a;
   % This procedure can only cope with polys that have a numeric
   % leading coeff.
   if a=nil then nil
   else if domainp a then 1
   else if lc a = 1 then a
   else if not domainp lc a then
       errorf "LC NOT NUMERIC IN MONIC-MOD-P"
   else multiply!-by!-constant!-mod!-p(a,
     modular!-reciprocal lc a);

symbolic procedure quotfail!-mod!-p(a,b);
   % Form quotient A/B, but complain if the division is not exact.
  begin scalar c;
    exact!-quotient!-flag:=t;
    c:=quotient!-mod!-p(a,b);
    if exact!-quotient!-flag then return c
    else errorf "QUOTIENT NOT EXACT (MOD P)"
  end;

symbolic procedure quotient!-mod!-p(a,b);
   % Truncated quotient of a by b.
    if null b then errorf "B=0 IN QUOTIENT-MOD-P"
    else if domainp b then begin
        scalar r;
        r := safe!-modular!-reciprocal b;
        if null b then return exact!-quotient!-flag:=nil
        else return multiply!-by!-constant!-mod!-p(a, r)
    end
    else if a=nil then nil
    else if domainp a then exact!-quotient!-flag:=nil
    else if mvar a=mvar b then xquotient!-mod!-p(a,b,mvar b)
    else if ordop(mvar a,mvar b) then
       adjoin!-term(lpow a,
          quotient!-mod!-p(lc a,b),
          quotient!-mod!-p(red a,b))
    else exact!-quotient!-flag:=nil;


symbolic procedure xquotient!-mod!-p(a,b,v);
   % Truncated quotient a/b given that b is nontrivial.
    if a=nil then nil
    else if (domainp a) or (not(mvar a=v)) or
      ilessp(ldeg a,ldeg b) then exact!-quotient!-flag:=nil
    else if ldeg a = ldeg b then begin scalar w;
      w:=quotient!-mod!-p(lc a,lc b);
      if w = nil or
         difference!-mod!-p(a,times!-mod!-p(w,b)) then
        exact!-quotient!-flag:=nil;
      return w
      end
    else begin scalar term;
      term:=mksp(mvar a,idifference(ldeg a,ldeg b)) .*
        quotient!-mod!-p(lc a,lc b);
   % That is the leading term of the quotient.  Now subtract term*b from
   % a.
      a:=plus!-mod!-p(red a,
                      times!-term!-mod!-p(negate!-term term,red b));
   % or a:=a-b*term given leading terms must cancel.
      return term .+ xquotient!-mod!-p(a,b,v)
    end;

symbolic procedure negate!-term term;
   % Negate a term.
    tpow term .* minus!-mod!-p tc term;


symbolic procedure remainder!-mod!-p(a,b);
   % Remainder when a is divided by b.
    if null b then errorf "B=0 IN REMAINDER-MOD-P"
    else if domainp b then nil
    else if domainp a then a
    else xremainder!-mod!-p(a,b,mvar b);


symbolic procedure xremainder!-mod!-p(a,b,v);
   % Remainder when the modular polynomial a is divided by b, given that
   % b is non degenerate.
   if (domainp a) or (not(mvar a=v)) or ilessp(ldeg a,ldeg b) then a
   else begin
    scalar q,w;
    q:=quotient!-mod!-p(minus!-mod!-p lc a,lc b);
   % compute -lc of quotient.
    w:=idifference(ldeg a,ldeg b); %ldeg of quotient;
    if w=0 then a:=plus!-mod!-p(red a,
      multiply!-by!-constant!-mod!-p(red b,q))
    else
      a:=plus!-mod!-p(red a,times!-term!-mod!-p(
            mksp(mvar b,w) .* q,red b));
   % The above lines of code use red a and red b because by construc-
   % tion the leading terms of the required % answers will cancel out.
     return xremainder!-mod!-p(a,b,v)
   end;

symbolic procedure multiply!-by!-constant!-mod!-p(a,n);
   % Multiply the polynomial a by the constant n.
   if null a then nil
   else if n=1 then a
   else if domainp a then !*n2f modular!-times(a,n)
   else adjoin!-term(lpow a,multiply!-by!-constant!-mod!-p(lc a,n),
     multiply!-by!-constant!-mod!-p(red a,n));

symbolic procedure gcd!-mod!-p(a,b);
   % Return the monic gcd of the two modular univariate polynomials a
   % and b.  Set REDUCTION-COUNT to the number of steps taken in the
   % process.
 << reduction!-count := 0;
    if null a then monic!-mod!-p b
    else if null b then monic!-mod!-p a
    else if domainp a then 1
    else if domainp b then 1
    else if igreaterp(ldeg a,ldeg b) then
      ordered!-gcd!-mod!-p(a,b)
    else ordered!-gcd!-mod!-p(b,a) >>;

symbolic procedure ordered!-gcd!-mod!-p(a,b);
   % As above, but deg a > deg b.
  begin scalar steps;
    steps := 0;
top:
    a := reduce!-degree!-mod!-p(a,b);
    if null a then return monic!-mod!-p b;
    steps := steps + 1;
    if domainp a then <<
        reduction!-count := reduction!-count+steps;
        return 1 >>
    else if ldeg a<ldeg b then begin
      scalar w;
      reduction!-count := reduction!-count + steps;
      steps := 0;
      w := a; a := b; b := w
      end;
    go to top
  end;

symbolic procedure reduce!-degree!-mod!-p(a,b);
   % Compute A-Q*B where Q is a single term chosen so that the result
   % has lower degree than A did.
  begin
    scalar q,w;
    q:=modular!-quotient(modular!-minus lc a,lc b);
   % compute -lc of quotient;
    w:=idifference(ldeg a,ldeg b); %ldeg of quotient;
   % The next lines of code use red a and red b because by construction
   % the leading terms of the required answers will cancel out.
    if w=0 then return plus!-mod!-p(red a,
      multiply!-by!-constant!-mod!-p(red b,q))
    else return plus!-mod!-p(red a,times!-term!-mod!-p(
            mksp(mvar b,w) .* q,red b))
   end;

symbolic procedure derivative!-mod!-p a;
   % Derivative of a wrt its main variable.
   if domainp a then nil
   else if ldeg a=1 then lc a
   else derivative!-mod!-p!-1(a,mvar a);

symbolic procedure derivative!-mod!-p!-1(a,v);
    if domainp a then nil
    else if not(mvar a=v) then nil
    else if ldeg a=1 then lc a
   else adjoin!-term(mksp(v,isub1 ldeg a),
                 multiply!-by!-constant!-mod!-p(lc a,
                                                modular!-number ldeg a),
                 derivative!-mod!-p!-1(red a,v));

symbolic procedure square!-free!-mod!-p a;
   % predicate that tests if a is square-free as a modular univariate
   % polynomial.
    domainp a or domainp gcd!-mod!-p(a,derivative!-mod!-p a);

symbolic procedure evaluate!-mod!-p(a,v,n);
   % Evaluate polynomial A at the point V=N.
    if domainp a then a
    else if n=0 then evaluate!-mod!-p(a,v,nil)
    else if v=nil then errorf "Variable=NIL in EVALUATE-MOD-P"
    else if mvar a=v then horner!-rule!-mod!-p(lc a,ldeg a,red a,n,v)
    else adjoin!-term(lpow a,
      evaluate!-mod!-p(lc a,v,n),
      evaluate!-mod!-p(red a,v,n));

symbolic procedure horner!-rule!-mod!-p(v,degg,a,n,var);
   % V is the running total, and it must be multiplied by n**deg and
   % added to the value of a at n.
    if domainp a or not(mvar a=var)
      then if null n or zerop n then a
            else <<v:=times!-mod!-p(v,expt!-mod!-p(n,degg));
                   plus!-mod!-p(a,v)>>
    else begin scalar newdeg;
      newdeg:=ldeg a;
      return horner!-rule!-mod!-p(if null n or zerop n then lc a
                                   else plus!-mod!-p(lc a,
         times!-mod!-p(v,expt!-mod!-p(n,idifference(degg,newdeg)))),
       newdeg,red a,n,var)
    end;

symbolic procedure expt!-mod!-p(a,n);
   % a**n.
    if n=0 then 1
    else if n=1 then a
    else begin scalar w,x;
     w:=divide(n,2);
     x:=expt!-mod!-p(a,car w);
     x:=times!-mod!-p(x,x);
     if not (cdr w = 0) then x:=times!-mod!-p(x,a);
     return x
    end;

symbolic procedure make!-bivariate!-mod!-p(u,imset,v);
   % Substitute into U for all variables in IMSET which should result in
   % a bivariate poly. One variable is M-IMAGE-VARIABLE and V is the
   % other U is modular multivariate with these two variables at top 2
   % levels - V at 2nd level.
  if domainp u then u
  else if mvar u = m!-image!-variable then
    adjoin!-term(lpow u,make!-univariate!-mod!-p(lc u,imset,v),
      make!-bivariate!-mod!-p(red u,imset,v))
  else make!-univariate!-mod!-p(u,imset,v);

symbolic procedure make!-univariate!-mod!-p(u,imset,v);
   % Substitute into U for all variables in IMSET giving a univariate
   % poly in V. U is modular multivariate with V at top level.
  if domainp u then u
  else if mvar u = v then
    adjoin!-term(lpow u,!*n2f evaluate!-in!-order!-mod!-p(lc u,imset),
      make!-univariate!-mod!-p(red u,imset,v))
  else !*n2f evaluate!-in!-order!-mod!-p(u,imset);

symbolic procedure evaluate!-in!-order!-mod!-p(u,imset);
   % Makes an image of u wrt imageset, imset, using Horner's rule.
   % Result should be purely numeric (and modular).
  if domainp u then !*d2n u
   else if mvar u=caar imset then
    horner!-rule!-in!-order!-mod!-p(
      evaluate!-in!-order!-mod!-p(lc u,cdr imset),ldeg u,red u,imset)
   else evaluate!-in!-order!-mod!-p(u,cdr imset);

symbolic procedure horner!-rule!-in!-order!-mod!-p(c,degg,a,vset);
   % C is running total and a is what is left.
  if domainp a then modular!-plus(!*d2n a,
    modular!-times(c,modular!-expt(cdar vset,degg)))
  else if not(mvar a=caar vset) then
    modular!-plus(
      evaluate!-in!-order!-mod!-p(a,cdr vset),
      modular!-times(c,modular!-expt(cdar vset,degg)))
  else begin scalar newdeg;
    newdeg:=ldeg a;
    return horner!-rule!-in!-order!-mod!-p(
      modular!-plus(
        evaluate!-in!-order!-mod!-p(lc a,cdr vset),
        modular!-times(c,
          modular!-expt(cdar vset,(idifference(degg,newdeg))))),
      newdeg,red a,vset)
  end;

symbolic procedure make!-modular!-symmetric a;
   % Input is a multivariate MODULAR poly A with nos in range 0->(p-1).
   % This folds it onto the symmetric range (-p/2)->(p/2).
    if null a then nil
    else if domainp a then
      if a>modulus!/2 then !*n2f(a - current!-modulus)
      else a
    else adjoin!-term(lpow a,make!-modular!-symmetric lc a,
      make!-modular!-symmetric red a);

endmodule;

end;
