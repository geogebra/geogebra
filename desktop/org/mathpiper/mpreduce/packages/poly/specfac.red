module specfac;   % Splitting of low degree polynomials.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND. All rights reserved.

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


fluid '(!*keepsqrts !*sub2 !*surds knowndiscrimsign kord!* zlist);

% switch surds;

exports cubicf,quadraticf,quarticf;

symbolic procedure coeffs pol;
% Extract coefficients of polynomial wrt its main variable and leading
% degree. Result is a list of coefficients.
    begin integer degree,deg1; scalar cofs,mv;
      mv := mvar pol;
      degree := ldeg pol;
      while not domainp pol and mvar pol eq mv do
       <<deg1 := ldeg pol;
         for i:= 1:(degree-deg1-1) do cofs := nil . cofs;
         cofs := lc pol . cofs;
         pol := red pol;
         degree := deg1>>;
      for i:=1:degree-1 do cofs := nil . cofs;
      return reversip(pol . cofs)
   end;

symbolic procedure shift!-pol pol;
% Shifts main variable, mv, of square free nth degree polynomial pol so
% that coefficient of mv**(n-1) is zero.
% Does not assume pol is univariate.
   begin scalar lc1,ld,mv,pol1,redp,shift,x;
      mv := mvar pol;
      ld := ldeg pol;
      redp := red pol;
      if domainp redp or not(mvar redp eq mv) or ldeg redp<(ld-1)
        then return list(pol,1,nil ./ 1);
      lc1 := lc pol;
      x := lc redp;
      shift := quotsq(!*f2q x,!*f2q multd(ld,lc1));
      pol1 := subf1(pol,list(mv . mk!*sq addsq(!*k2q mv,negsq shift)));
      return list(numr pol1,denr pol1,shift)
   end;

symbolic procedure quadraticf!*(pol,var);
   if domainp pol then errach "invalid quadratic to factr"
    else if mvar pol = var then quadraticf pol
    else begin scalar kord,w;
        kord := kord!*;
        kord!* := list var;
        w := coeffs !*q2f resimp(pol ./ 1);
        kord!* := kord;
        w := quadraticf1(car w,cadr w,caddr w);
        if w eq 'failed then return list(1,pol);
        var := !*k2f var;
        return list(if car w neq 1 then mkrn(1,car w) else 1,
                    addf(multf(var,cadr w),caddr w),
                         addf(multf(var,cadddr w),car cddddr w))
     end;

symbolic procedure quadraticf pol;
   % Finds factors of square free quadratic polynomial pol (if they
   % exist).  Does not assume pol is univariate.
   (if x eq 'failed then list(1,pol)
    else if not domainp car x then list(1,pol)
            % Answer would be rational.
    else list(if car x neq 1 then mkrn(1,car x) else 1,
              y .* cadr x .+ caddr x,y .* cadddr x .+ car cddddr x)
       where y = (mvar pol .** 1))
    where x = quadraticf1(car w,cadr w,caddr w) where w = coeffs pol;

symbolic procedure quadraticf1(a,b,c);
   begin scalar a1,denom,discrim,w;
      if null b and minusf c and not minusf a
       then <<a := rootxf(a,2);
              c := rootxf(negf c,2);
              return if a eq 'failed or c eq 'failed then 'failed
                      else list(1,a,c,a,negf c)>>;
      discrim := powsubsf addf(exptf(b,2),multd(-4,multf(a,c)));
      % A null discriminator can arise from a polynomial such as
      % 16x^2+(32i-8)*x-8i-15;
      if null discrim then nil
       else <<if knowndiscrimsign
                then <<if knowndiscrimsign eq 'negative
                         then return 'failed>>
      %        else if not clogflag and minusf discrim
      %         then return 'failed;
               else if minusf discrim then return 'failed;
             discrim:=rootxf(discrim,2);
             if discrim='failed then return discrim>>;
      denom := multd(4,a);
      a := a1 := multd(2,a);
      w := addf(b,discrim);
      c := addf(b,negf discrim);
      b := w;
      if (w := gcdf(a,b)) neq 1
        then <<a1 := quotf(a,w); b := quotf(b,w);
               denom := quotf(denom,w)>>;
      if (w := gcdf(a,denom)) neq 1 and (w := gcdf(c,denom))
        then <<a := quotf(a,w);
               c := quotf(c,w);
               denom := quotf(denom,w)>>;
      return list(denom,a1,b,a,c)
    end;

symbolic procedure rootxf(u,n);
   % Return either polynomial nth root of u or "failed".
   begin scalar x,y,z,w;
      if domainp u
        then return if minusf u then 'failed
                     else if atom u and (y := irootn(u,n))**n=u then y
                     else if not atom u and (x := get(car u,'rootfn))
                      then apply2(x,u,n)
                     else if !*surds and not(u member zlist)
                      then nrootn!*(u,n)
                     else 'failed;
      x := comfac u;
      u := quotf(u,comfac!-to!-poly x);
      z := 1;
      if car x then if cdr(y := divide(cdar x,n)) = 0
                      then z := multpf(caar x .** car y,z)
                     else if !*surds
                      then <<z := multf(mkrootf(caar x,n,cdr y),z);
                             if car y neq 0
                               then z := multpf(caar x .** car y,z)>>
       else return 'failed;
      x := cdr x;
      if domainp x
        then if minusf x then return 'failed
              else if fixp x and (y := irootn(x,n))**n=x
               then z := multd(y,z)
              else if !*surds and fixp x
               then z := multf(nrootn!*(x,n),z)
              else if not atom x and (w := get(car x,'rootfn))
               then apply2(w,x,n)
              else return 'failed
       else if (y := rootxf(x,n)) eq 'failed then return y
       else z := multf(y,z);
      if u=1 then return z;
      x := sqfrf u;
   c: if null x then return z
       else if cdr(y := divide(cdar x,n)) = 0
        then <<z := multf(exptf(caar x,car y),z); x := cdr x>>
       else if !*surds
        then <<z := multf(mkrootf(prepf caar x,n,cdr y),
                          multf(exptf(caar x,car y),z));
          % It's possible to get things like sqrt(-3-9) in z. An "eval"
          % before the prepf fixes this, but it's not worth it.
               x := cdr x>>
       else return 'failed;
      go to c
   end;

symbolic procedure mkrootf(u,m,n);
   if m neq 2 or null !*keepsqrts
     then !*p2f mksp(list('expt,u,list('quotient,1,m)),n)
    else if n neq 1 then errach 'mkrootf
    else !*q2f simpsqrt list u;

symbolic procedure nrootn!*(u,n);
   % Returns a standard form representation of the nth root of u.
   begin scalar x;
      if null u then return nil;
      u := nrootn(u,n);
      x := cdr u;         % surd part.
      u := car u;         % rational part.
      if x=1 then return x;
      x := mkrootf(prepf x,n,1);
      return powsubsf multf(u,x)
   end;

symbolic procedure cubicf pol;
   % Split the cubic pol if a change of origin puts it in the form
   % (x-a)**3-b=0.
   begin scalar a,a0,a1,b,neg,p;
      p := shift!-pol pol;
      a := coeffs car p;
      if cadr a then return list(1,pol)
      % Cadr a non nil probably means there are some surds in the
      % coefficients that don't reduce to 0.
       else if caddr a then return list(1,pol);
      % Factorization not possible by this method.
      a0 := cadddr a;
      a := car a;
      if minusf a0 then <<neg := t; a0 := negf a0>>;
      if (a := rootxf(a,3)) eq 'failed
         or (a0 := rootxf(a0,3)) eq 'failed
        then return list(1,pol);
      if neg then a0 := negf a0;
      a := !*f2q a;
      a0 := !*f2q a0;
      p := addsq(!*k2q mvar pol,caddr p);
      % Now numr (a*(mv+shift)+a0) is a factor of pol.
      a1 := numr addsq(multsq(a,p),a0);
      % quotf(pol,a) is quadratic factor. However, the surd division may
      % not work properly, so we calculate factor directly.
      b := multsq(a0,a0);
      b := addsq(b,multsq(negsq multsq(a,a0),p));
      b := numr addsq(b,multsq(multsq(a,a),exptsq(p,2)));
      return aconc!*(quadraticf b,a1)
   end;

symbolic procedure powsubsf u;
   % We believe that the result of this operation must be a polynomial.
   % If subs2q returns a rational, it must be because there are
   % unsimplified surds.  Hopefully rationalizesq can fix those.
   begin scalar !*sub2;
      u := subs2q !*f2q u;
      if denr u neq 1
        then <<u := rationalizesq u;
               if denr u neq 1 then errach list('powsubsf,u)>>;
      return numr u
   end;

symbolic procedure quarticf pol;
  % Splits quartics that can be written in the form
  % (x-a)**4+b*(x-a)**2+c.
  % Note that any call of rootxf can lead to a result "failed."
   begin scalar !*sub2,a,a2,a0,b,dsc,p,p1,p2,q,shift,var;
      var := mvar pol;
      p := shift!-pol pol;
      a := coeffs car p;
      shift := caddr p;
      if cadr a    % pol not correctly shifted, possibly due to sqrt.
      % e.g., 729para^4*be^4 - 81para^3*sqrt(27*be^2*para^2 - 8cte1^3)*
      % sqrt(3)*be^3 - 216para^2*be^2*cte1^3 + 12para*sqrt(27be^2*para^2
      %  - 8*cte1^3)*sqrt(3) *be*cte1^3 + 8*cte1^6.
         or cadddr a then return list(1,pol);
       % Factorization not possible by this method.
      a2 := cddr a;
      a0 := caddr a2;
      a2 := car a2;
      a := car a;
      q := quadraticf1(a,a2,a0);
      if not(q eq 'failed)
        then <<a2 := car q; q := cdr q;
               a := exptsq(addsq(!*k2q mvar pol,shift),2);
               b := numr subs2q quotsq(addsq(multsq(!*f2q car q,a),
                                             !*f2q cadr q),
                                       !*f2q cadr p);
               a := numr subs2q quotsq(addsq(multsq(!*f2q caddr q,a),
                                             !*f2q cadddr q),
                                       !*f2q cadr p);
               a := quadraticf!*(a,var);
               b := quadraticf!*(b,var);
               return multf(a2,multf(car a,car b))
                         . nconc!*(cdr a,cdr b)>>
       else if null !*surds or denr shift neq 1
        then return list(1,pol);
       % Factorization not possible by this method.
      shift := numr shift;
      if knowndiscrimsign eq 'negative then go to complex;
      dsc := powsubsf addf(exptf(a2,2),multd(-4,multf(a,a0)));
      p2 := minusf a0;
      if not p2 and minusf dsc then go to complex;
      p1 := not a2 or minusf a2;
      if not p1 then if p2 then p1 := t else p2 := t;
      p1 := if p1 then 'positive else 'negative;
      p2 := if p2 then 'negative else 'positive;
      a := rootxf(a,2);
      if a eq 'failed then return list(1,pol);
      dsc := rootxf(dsc,2);
      if dsc eq 'failed then return list(1,pol);
      p := invsq !*f2q addf(a,a);
      q := multsq(!*f2q addf(a2,negf dsc),p);
      p := multsq(!*f2q addf(a2,dsc),p);
      b := multf(a,exptf(addf(!*k2f mvar pol,shift),2));
      a := powsubsf addf(b,q);
      b := powsubsf addf(b,p);
      knowndiscrimsign := p1;
      a := quadraticf!*(a,var);
      knowndiscrimsign := p2;
      b := quadraticf!*(b,var);
      knowndiscrimsign := nil;
      return multf(car a,car b) . nconc!*(cdr a,cdr b);
      % Complex case.
   complex:
      a := rootxf(a,2);
      if a eq 'failed then return list(1,pol);
      a0 := rootxf(a0,2);
      if a0 eq 'failed then return list(1,pol);
      a2 := powsubsf addf(multf(2,multf(a,a0)),negf a2);
      a2 := rootxf(a2,2);
      if a2 eq 'failed then return list(1,pol);
      % Now a*(x+shift)**2 (+/-) b*(x+shift) + c is a factor.
      p := addf(!*k2f mvar pol,shift);
      q := addf(multf(a,exptf(p,2)),a0);
      p := multf(a2,p);
      a := powsubsf addf(q,p);
      b := powsubsf addf(q,negf p);
      knowndiscrimsign := 'negative;
      a := quadraticf!*(a,var);
      b := quadraticf!*(b,var);
      knowndiscrimsign := nil;
      return multf(car a,car b) . nconc!*(cdr a,cdr b)
   end;

endmodule;

end;
