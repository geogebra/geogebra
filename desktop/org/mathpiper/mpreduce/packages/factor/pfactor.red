module pfactor;  % Factorization of polynomials modulo p.

% Author: A. C. Norman, 1978.

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


fluid '(!*balanced_mod
        !*gcd
        current!-modulus
        m!-image!-variable
        modular!-info
        modulus!/2
        user!-prime);

global '(largest!-small!-modulus);

symbolic procedure pfactor(q,p);
   % Q is a standard form. Factorize and return the factors mod p.
   begin scalar user!-prime,current!-modulus,modulus!/2,r,x;
%   set!-time();
    if not numberp p then typerr(p,"number")
     else if not primep p then typerr(p,"prime")
     else if p>largest!-small!-modulus
      then rederr {p,"too large a modulus for factorization"};
    user!-prime:=p;
    set!-modulus p;
    if domainp q or null reduce!-mod!-p lc q then
       prin2t "*** Degenerate case in modular factorization";
    if not (length variables!-in!-form q=1) then
   %%    rerror(factor,1,"Multivariate input to modular factorization");
       return fctrfkronm q;
    r:=reduce!-mod!-p q;
%   lncoeff := lc r;
    x := lnc r;
    r :=monic!-mod!-p r;
%   print!-time "About to call FACTOR-FORM-MOD-P";
    r:=errorset!*(list('factor!-form!-mod!-p,mkquote r),t);
%   print!-time "FACTOR-FORM-MOD-P returned";
    if not errorp r
      then return x . for each j in car r
                          collect mod!-adjust car j . cdr j;
    prin2t "****** FACTORIZATION FAILED******";
    return list(1,prepf q)   % 1 needed by factorize.
  end;

symbolic procedure mod!-adjust u;
   % Make sure any modular numbers in u are in the right range.
   if null !*balanced_mod then u else mod!-adjust1 u;

symbolic procedure mod!-adjust1 u;
   if domainp u
     then if fixp u then !*modular2f u
           else if eqcar(u,'!:mod!:) then !*modular2f cdr u
           else typerr(u,"modular number")
    else lpow u .* mod!-adjust1 lc u .+ mod!-adjust1 red u;


symbolic procedure factor!-form!-mod!-p p;
% input:
% p is a reduce standard form that is to be factorized
% mod prime;
% result:
% ((p1 . x1) (p2 . x2) .. (pn . xn))
% where p<i> are standard forms and x<i> are integers,
% and p= product<i> p<i>**x<i>;
    sort!-factors factorize!-by!-square!-free!-mod!-p p;

symbolic procedure factorize!-by!-square!-free!-mod!-p p;
    if p=1 then nil
    else if domainp p then (p . 1) . nil
    else
     begin
      scalar dp,v;
      v:=(mksp(mvar p,1).* 1) .+ nil;
      dp:=0;
      while evaluate!-mod!-p(p,mvar v,0)=0 do <<
        p:=quotfail!-mod!-p(p,v);
        dp:=dp+1 >>;
      if dp>0 then return ((v . dp) .
        factorize!-by!-square!-free!-mod!-p p);
      dp:=derivative!-mod!-p p;
      if dp=nil then <<
%here p is a something to the power current!-modulus;
        p:=divide!-exponents!-by!-p(p,current!-modulus);
        p:=factorize!-by!-square!-free!-mod!-p p;
        return multiply!-multiplicities(p,current!-modulus) >>;
      dp:=gcd!-mod!-p(p,dp);
      if dp=1 then return factorize!-pp!-mod!-p p;
%now p is not square-free;
      p:=quotfail!-mod!-p(p,dp);
%factorize p and dp separately;
      p:=factorize!-pp!-mod!-p p;
      dp:=factorize!-by!-square!-free!-mod!-p dp;
% i feel that this scheme is slightly clumsy, but
% square-free decomposition mod p is not as straightforward
% as square free decomposition over the integers, and pfactor
% is probably not going to be slowed down too badly by
% this;
      return mergefactors(p,dp)
   end;




%**********************************************************************;
% code to factorize primitive square-free polynomials mod p;



symbolic procedure divide!-exponents!-by!-p(p,n);
    if domainp p then p
    else (mksp(mvar p,exactquotient(ldeg p,n)) .* lc p) .+
       divide!-exponents!-by!-p(red p,n);

symbolic procedure exactquotient(a,b);
  begin
    scalar w;
    w:=divide(a,b);
    if cdr w=0 then return car w;
    error(50,list("Inexact division",list(a,b,w)))
  end;


symbolic procedure multiply!-multiplicities(l,n);
    if null l then nil
    else (caar l . (n*cdar l)) .
        multiply!-multiplicities(cdr l,n);


symbolic procedure mergefactors(a,b);
% a and b are lists of factors (with multiplicities),
% merge them so that no factor occurs more than once in
% the result;
    if null a then b
    else mergefactors(cdr a,addfactor(car a,b));

symbolic procedure addfactor(a,b);
%add factor a into list b;
    if null b then list a
    else if car a=caar b then
      (car a . (cdr a + cdar b)) . cdr b
    else car b . addfactor(a,cdr b);

symbolic procedure factorize!-pp!-mod!-p p;
%input a primitive square-free polynomial p,
% output a list of irreducible factors of p;
  begin
    scalar vars;
    if p=1 then return nil
    else if domainp p then return (p . 1) . nil;
% now I am certain that p is not degenerate;
%   print!-time "primitive square-free case detected";
    vars:=variables!-in!-form p;
    if length vars=1 then return unifac!-mod!-p p;
    errorf "SHAMBLED IN PFACTOR - MULTIVARIATE CASE RESURFACED"
  end;

symbolic procedure unifac!-mod!-p p;
%input p a primitive square-free univariate polynomial
%output a list of the factors of p over z mod p;
  begin
    scalar modular!-info,m!-image!-variable;
    if domainp p then return nil
    else if ldeg p=1 then return (p . 1) . nil;
    modular!-info:=mkvect 1;
    m!-image!-variable:=mvar p;
    get!-factor!-count!-mod!-p(1,p,user!-prime,nil);
%   print!-time "Factor counts obtained";
    get!-factors!-mod!-p(1,user!-prime);
%   print!-time "Actual factors extracted";
    return for each z in getv(modular!-info,1) collect (z . 1)
  end;

endmodule;

end;
