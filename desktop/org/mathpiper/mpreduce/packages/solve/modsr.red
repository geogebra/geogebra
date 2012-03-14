module modsr; % Modular Solve and Roots.

% Author: Herbert Melenk, ZIB Berlin, Jan 95.

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


create!-package('(modsr modsqrt modroots modsolve),'(solve));

fluid '(current!-modulus);

% Some routines from solve and factor(modpoly) are needed.

load!-package 'solve;
load!-package 'factor;

% Now a few things that MIGHT have been in the factorizer but were not
% It is quite possible that as a matter of style these few functions
% should be put into factor.red, even though they are not used there,
% since that way they live near their friends and are more generally
% useful???

symbolic procedure general!-evaluate!-mod!-p(a,v,n);
   % Evaluate polynomial A at the point V=N.
    if domainp a then a
    else if n=0 then general!-evaluate!-mod!-p(a,v,nil)
    else if v=nil then errorf "Variable=NIL in GENERAL-EVALUATE-MOD-P"
    else if mvar a=v
     then general!-horner!-rule!-mod!-p(lc a,ldeg a,red a,n,v)
    else adjoin!-term(lpow a,
      general!-evaluate!-mod!-p(lc a,v,n),
      general!-evaluate!-mod!-p(red a,v,n));

symbolic procedure general!-horner!-rule!-mod!-p(v,degg,a,n,var);
   % V is the running total, and it must be multiplied by n**deg and
   % added to the value of a at n.
    if domainp a or not(mvar a=var)
      then if null n or zerop n then a
            else <<v:=general!-times!-mod!-p(v,
                         general!-expt!-mod!-p(n,degg));
                   general!-plus!-mod!-p(a,v)>>
    else begin scalar newdeg;
      newdeg:=ldeg a;
      return general!-horner!-rule!-mod!-p(
          if null n or zerop n then lc a
          else general!-plus!-mod!-p(lc a,
             general!-times!-mod!-p(v,
                general!-expt!-mod!-p(n,idifference(degg,newdeg)))),
       newdeg,red a,n,var)
    end;

symbolic procedure general!-expt!-mod!-p(a,n);
   % a**n.
    if n=0 then 1
    else if n=1 then a
    else begin scalar w,x;
     w:=divide(n,2);
     x:=general!-expt!-mod!-p(a,car w);
     x:=general!-times!-mod!-p(x,x);
     if not (cdr w = 0) then x:=general!-times!-mod!-p(x,a);
     return x
    end;

symbolic procedure general!-monic!-mod!-p a;
   % This procedure can only cope with polys that have a numeric
   % leading coeff.
   if a=nil then nil
   else if domainp a then 1
   else if lc a = 1 then a
   else if not domainp lc a then
       errorf "LC NOT NUMERIC IN GENERAL-MONIC-MOD-P"
   else general!-multiply!-by!-constant!-mod!-p(a,
     general!-modular!-reciprocal lc a);

symbolic procedure general!-quotient!-mod!-p(a,b);
   % Truncated quotient of a by b.
    if null b then errorf "B=0 IN GENERAL-QUOTIENT-MOD-P"
    else if domainp b then general!-multiply!-by!-constant!-mod!-p(a,
                             general!-modular!-reciprocal b)
    else if a=nil then nil
    else if domainp a then exact!-quotient!-flag:=nil
    else if mvar a=mvar b then general!-xquotient!-mod!-p(a,b,mvar b)
    else if ordop(mvar a,mvar b) then
       adjoin!-term(lpow a,
          general!-quotient!-mod!-p(lc a,b),
          general!-quotient!-mod!-p(red a,b))
    else exact!-quotient!-flag:=nil;


symbolic procedure general!-xquotient!-mod!-p(a,b,v);
   % Truncated quotient a/b given that b is nontrivial.
    if a=nil then nil
    else if (domainp a) or (not(mvar a=v)) or
      ilessp(ldeg a,ldeg b) then exact!-quotient!-flag:=nil
    else if ldeg a = ldeg b then begin scalar w;
      w:=general!-quotient!-mod!-p(lc a,lc b);
      if general!-difference!-mod!-p(a,general!-times!-mod!-p(w,b)) then
        exact!-quotient!-flag:=nil;
      return w
      end
    else begin scalar term;
      term:=mksp(mvar a,idifference(ldeg a,ldeg b)) .*
        general!-quotient!-mod!-p(lc a,lc b);
   % That is the leading term of the quotient.  Now subtract term*b from
   % a.
      a:=general!-plus!-mod!-p(red a,
                general!-times!-term!-mod!-p(general!-negate!-term term,
                                             red b));
   % or a:=a-b*term given leading terms must cancel.
      return term .+ general!-xquotient!-mod!-p(a,b,v)
    end;

symbolic procedure general!-negate!-term term;
   % Negate a term.
    tpow term .* general!-minus!-mod!-p tc term;


symbolic procedure general!-remainder!-mod!-p(a,b);
   % Remainder when a is divided by b.
    if null b then errorf "B=0 IN GENERAL-REMAINDER-MOD-P"
    else if domainp b then nil
    else if domainp a then a
    else general!-xremainder!-mod!-p(a,b,mvar b);


symbolic procedure general!-xremainder!-mod!-p(a,b,v);
   % Remainder when the modular polynomial a is divided by b, given that
   % b is non degenerate.
   if (domainp a) or (not(mvar a=v)) or ilessp(ldeg a,ldeg b) then a
   else begin
    scalar q,w;
    q:=general!-quotient!-mod!-p(general!-minus!-mod!-p lc a,lc b);
   % compute -lc of quotient.
    w:=idifference(ldeg a,ldeg b); %ldeg of quotient;
    if w=0 then a:=general!-plus!-mod!-p(red a,
      general!-multiply!-by!-constant!-mod!-p(red b,q))
    else
      a:=general!-plus!-mod!-p(red a,general!-times!-term!-mod!-p(
            mksp(mvar b,w) .* q,red b));
   % The above lines of code use red a and red b because by construc-
   % tion the leading terms of the required % answers will cancel out.
     return general!-xremainder!-mod!-p(a,b,v)
   end;

symbolic procedure general!-multiply!-by!-constant!-mod!-p(a,n);
   % Multiply the polynomial a by the constant n.
   if null a then nil
   else if n=1 then a
   else if domainp a then !*n2f general!-modular!-times(a,n)
   else adjoin!-term(lpow a,
                     general!-multiply!-by!-constant!-mod!-p(lc a,n),
     general!-multiply!-by!-constant!-mod!-p(red a,n));

symbolic procedure general!-gcd!-mod!-p(a,b);
   % Return the monic gcd of the two modular univariate polynomials a
   % and b.  Set REDUCTION-COUNT to the number of steps taken in the
   % process.
 << reduction!-count := 0;
    if null a then monic!-mod!-p b
    else if null b then monic!-mod!-p a
    else if domainp a then 1
    else if domainp b then 1
    else if igreaterp(ldeg a,ldeg b) then
      general!-ordered!-gcd!-mod!-p(a,b)
    else general!-ordered!-gcd!-mod!-p(b,a) >>;

symbolic procedure general!-ordered!-gcd!-mod!-p(a,b);
   % As above, but deg a > deg b.
  begin scalar steps;
    steps := 0;
top:
    a := general!-reduce!-degree!-mod!-p(a,b);
    if null a then return general!-monic!-mod!-p b;
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

symbolic procedure general!-reduce!-degree!-mod!-p(a,b);
   % Compute A-Q*B where Q is a single term chosen so that the result
   % has lower degree than A did.
  begin
    scalar q,w;
    q:=general!-modular!-quotient(general!-modular!-minus lc a,lc b);
   % compute -lc of quotient;
    w:=idifference(ldeg a,ldeg b); %ldeg of quotient;
   % The next lines of code use red a and red b because by construction
   % the leading terms of the required answers will cancel out.
    if w=0 then return general!-plus!-mod!-p(red a,
      general!-multiply!-by!-constant!-mod!-p(red b,q))
    else return general!-plus!-mod!-p(red a,
                   general!-times!-term!-mod!-p(mksp(mvar b,w) .* q,
                                                red b))
   end;


%%%%%%%

symbolic procedure modp(a,p);
   <<a:=remainder(a,p); if a<0 then a+p else a>>;

symbolic procedure lowestdeg(f,x,n);
   if null f then n else
   if domainp f or mvar f neq x then 0 else
   lowestdeg(red f,x,ldeg f);

symbolic procedure reduce!-mod!-p!*(f,p);
  (general!-reduce!-mod!-p f) where current!-modulus = p;

symbolic procedure moduntag f;
  if eqcar(f,'!:mod!:) then cdr f else
  if atom f then f else
  moduntag car f . moduntag cdr f;

symbolic procedure safe!-modrecip u;
   % Return 1/u or nil.
   begin scalar q,!*msg,!*protfg;
      !*msg := nil; !*protfg := t;
      if eqcar(u,'!:mod!:) then u := cdr u;
      q := errorset({'general!-modular!-reciprocal, u},nil,nil);
      erfg!* := nil;
      return if errorp q then nil else car q
   end;

endmodule;

end;
