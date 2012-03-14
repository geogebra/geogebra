module facform;  % Factored form representation for standard form polys.

% Author: Anthony C. Hearn.

% Modifications by: Francis J. Wright.

% Copyright (c) 1990 The RAND Corporation. All rights reserved.

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


% INTEGER FACTORS?

% SHOULDN'T SYMMETRIC TESTS ETC BE RUN RECURSIVELY?

fluid '(!*exp !*ezgcd !*factor !*force!-prime !*gcd !*ifactor !*nopowers
        !*kernreverse !*limitedfactors !*sqfree !*trfac current!-modulus
        dmode!* m!-image!-variable ncmp!*);

switch limitedfactors,nopowers;

% switch sqfree;

put('sqfree,'simpfg,'((t (rmsubs) (setq !*exp nil))
                      (nil (rmsubs) (setq !*exp t))));

comment In this module, we consider the manipulation of factored forms.
    These have the structure

       <monomial> . <form-power-list>

        where the monomial is a standard form (with numerator and
        denominator satisfying the KERNLP test) and a form-power is a
        dotted pair whose car is a standard form and cdr an integer>0.
        We have thus represented the form as a product of a monomial
        quotient and powers of non-monomial factors;

symbolic procedure fac!-merge(u,v);
   % Returns the merge of the factored forms U and V.
   multf(car u,car v) . append(cdr u,cdr v);

symbolic procedure factorize u;
   % Factorize the polynomial u, returning the factors found.
   (begin scalar x,y;
      x := simp!* u;
      y := denr x;
      if not domainp y then typerr(u,"polynomial");
      u := numr x;
      if u = 1 then return
         {'list, if !*nopowers then 1 else {'list,1,1}} % FJW
       else if fixp u then !*ifactor := t;   % Factor an integer.
      if !*force!-prime and not primep !*force!-prime
        then typerr(!*force!-prime,"prime");
      u := if dmode!* and not(dmode!* memq '(!:rd!: !:cr!:))
         then if get(dmode!*,'factorfn)
                 then begin scalar !*factor;
                        !*factor := t;
                         return fctrf u
                      end
           else rerror(poly,14,
                       list("Factorization not supported over domain",
                       get(dmode!*,'dname)))
       else fctrf u;
      return facform2list(u,y)
   end) where !*ifactor = !*ifactor;

symbolic procedure facform2list(x,y);
   % x is a factored form.
   % y is a possible numerical (domain) denominator.
   begin scalar factor!-count,z;
      if null car x and null cdr x then return list 'list
      % car x is now expected to be a number.
       else if null !*nopowers then z := facform2list2 x
        else <<
         z:= (0 . car x) . nil;
         x := reversip!* cdr x;  % This puts factors in better order.
         factor!-count:=0;
         for each fff in x do
         for i:=1:cdr fff do
            z := ((factor!-count:=factor!-count+1) .
                    mk!*sq(car fff ./ 1)) . z;
         z := multiple!-result(z,nil);
         if atom z then typerr(z,"factor form")  % old style input.
          else if numberp cadr z and cadr z<0 and cddr z
           then z := car z .
                           (- cadr z) . mk!*sq negsq simp caddr z
                           . cdddr z;
         % make numerical coefficient positive.
         z := cdr z;
         if car z = 1 then z := cdr z
          else if not fixp car z then z := prepd car z . cdr z
          else if !*ifactor
              then z := append(pairlist2list reversip zfactor car z,
                            cdr z)>>;
      if y neq 1 then z := list('recip,prepd y) . z;
      return 'list . z
  end;

symbolic procedure facform2list2 u;
   begin scalar bool,x;
      if !:minusp(x := car u) then <<bool := t; x := !:minus x>>;
      u := cdr u;
      if x neq 1
        then if !*ifactor and fixp x
               then u := append(reversip zfactor x,u)
              else u := (x . 1) . u;
      % Adjust for negative sign.
      x := nil;
      for each j in u do
         if bool and not evenp cdr j
           then <<bool := nil; x := (negf car j . cdr j) . x>>
          else x := j . x;
      % Convert terms to list form.
      u := nil;
      for each j in x do
         if fixp car j then u := {'list,car j,cdr j} . u
          else u := {'list,mk!*sq(car j ./ 1),cdr j} . u;
      return if bool then '(list -1 1) . u else u
   end;

symbolic procedure old_factorize u; factorize u where !*nopowers=t;

symbolic procedure factorize!-mod!-p(u, p);
  begin
     scalar r, s1, s2;
     s1 := setmod p;
     s2 := !*modular;
     if not s2 then setdmode('modular, t);
     r := factorize u;
     if not s2 then setdmode('modular, nil);
     setmod s1;
     return r;
  end;

flag('(factorize old_factorize factorize!-mod!-p),'opfn);

symbolic procedure pairlist2list u;
   for each x in u conc nlist(car x,cdr x);

symbolic procedure fctrf u;
   % U is a standard form. Value is a factored form.
   % The function FACTORF is an assumed entry point to a more complete
   % factorization module.  It returns a form power list.
   (begin scalar !*ezgcd,!*gcd,denom,x,y;
      if domainp u then return list u
       else if ncmp!* and not noncomfp u then ncmp!* := nil;
      !*gcd := t;
      if null !*limitedfactors and null dmode!* then !*ezgcd := t;
      if null !*mcd
        then rerror(poly,15,"Factorization invalid with MCD off")
       else if null !*exp
        then <<!*exp := t; u := !*q2f resimp !*f2q u>>;
      % Convert rationals to integers for factorization.
      if dmode!* eq '!:rn!:
        then <<dmode!* := nil; alglist!* := nil . nil;
               x := simp prepf u;
               if atom denr x then <<denom := denr x; u := numr x>>
                else denom := 1>>;
      % Check for homogeneous polynomials.  This can't be done with
      % current code though if non-commuting objects occur.
      if null ncmp!*
        then <<x := sf2ss u;
               if homogp x
                 then <<if !*trfac
                        then prin2t
                    "This polynomial is homogeneous - variables scaled";
                        y := caaar x . listsum caaadr x;
                        x := fctrf1 ss2sf(car(x)
                                . (reverse subs0 cadr x . 1));
                        x := rconst(y,x);
                        return car x . sort!-factors cdr x>>>>;
      u := fctrf1 u;
      if denom
        then <<alglist!* := nil . nil;
               dmode!* := '!:rn!:; car u := quotf!*(car u,denom)>>;
      return  car u . sort!-factors cdr u
   end) where !*exp = !*exp, ncmp!* = ncmp!*;

symbolic procedure fctrf1 u;
   begin scalar x,y,z;
      if domainp u then return list u;  % Do this again just in case.
      if flagp(dmode!*,'field) and ((z := lnc u) neq 1)
         then u := multd(!:recip z,u)
       else if dmode!* and (y := get(dmode!*,'unitsfn))
         then <<x := apply2(y,1 . u,lnc u);
                u := cdr x;
                z := !:recip car x>>;
      x := comfac u;
      u := quotf(u,comfac!-to!-poly x);
      y := fctrf1 cdr x;   % factor the content.
      if car x then y := car y . (!*k2f caar x . cdar x) . cdr y;
      if z and (z neq 1) then y := multd(z,car y) . cdr y;
      if domainp u then return multf(u,car y) . cdr y
       else if minusf u
        then <<u := negf u; y := negf car y . cdr y>>;
      return fac!-merge(factor!-prim!-f u,y)
   end;

symbolic procedure factorize!-form!-recursion u;
   fctrf1 u;

symbolic procedure factor!-prim!-f u;
   % U is a non-trivial form which is primitive in all its variables
   % and has a positive leading numerical coefficient. Result is a
   % form power list.
   begin scalar v,w,x,y;
      if ncmp!* then return list(1,u . 1);
      if dmode!* and (x := get(dmode!*,'sqfrfactorfn))
        then if !*factor then v := apply1(x,u) else v := list(1,u . 1)
       else if flagp(dmode!*,'field) and ((w := lnc u) neq 1)
        then v := w . sqfrf multd(!:recip w,u)
       else if (w := get(dmode!*,'units)) and (w := assoc(y := lnc u,w))
        then v := y . sqfrf multd(cdr w,u)
       else v := 1 . sqfrf u;
      if x and (x eq get(dmode!*,'factorfn))
        then return v;   % No point in re-factorizing.
      w := list car v;
      for each x in cdr v
         do w := fac!-merge(factor!-prim!-sqfree!-f x,w);
      return w
   end;

symbolic procedure factor!-prim!-sqfree!-f u;
   % U is of the form <square free poly> . <power>. Result is a factored
   % form.
   % Modified to work properly in rounded (real or complex),
   % rational and complex modes. SLK.
   begin scalar x,y,!*msg,r;
      r := !*rounded;
      % It's probable that lc numr u and denr u will always be 1 if
      % u is univariate.
      if r and univariatep numr u and lc numr u=1 and denr u=1
        then return unifactor u
       else if r or !*complex or !*rational then
         <<if r then on rational;
           u := numr resimp !*f2q car u . cdr u>>;
      if null !*limitedfactors
        then <<if null dmode!* then y := 'factorf
                else <<x := get(dmode!*,'sqfrfactorfn);
                       y := get(dmode!*,'factorfn);
                       if x and not(x eq y) then y := 'factorf>>;
               if y
                 then <<y := apply1(y,car u);
                        u := (exptf(car y,cdr u) . for each j in cdr y
                                           collect(car j . cdr u));
                        go to ret>>>>;
      u := factor!-prim!-sqfree!-f!-1(car u,cdr u);
 ret: if r then
      <<on rounded;
        u := car u . for each j in cdr u collect
           (numr resimp !*f2q car j . cdr j)>>;
      return u
   end;

symbolic procedure unifactor u;
   if not eqcar(u := root_val list mk!*sq u,'list)
     then errach {"unifactor1",u}
    else 1 . for each j in cdr u collect
                 if not eqcar(j,'equal) then errach{"unifactor2",u}
                  else addsq(!*k2q cadr j,negsq simp caddr j);

symbolic procedure distribute!.multiplicity(factorlist,n);
   % Factorlist is a simple list of factors of a square free primitive
   % multivariate poly and n is their multiplicity in a square free
   % decomposition of another polynomial. result is a list of form:
   %  ((f1 . n),(f2 . n),...) where fi are the factors.
   for each w in factorlist collect (w . n);

symbolic procedure factorf u;
   % NOTE: This is not an entry point to be used by novice programmers.
   % Please used FCTRF instead.
   % There is an inefficiency in this procedure relating to ordering.
   % There is a final reordering done at every recursive level in order
   % to make sure final result has the right order.  However, this only
   % need be done once at the top level, probably in fctrf.  Since some
   % programmers still use this function however, it's safer for it to
   % return results in the correct order.
  (begin scalar m!-image!-variable,new!-korder,old!-korder,sign,v,w;
      if domainp u then return list u;
      new!-korder:=kernord(u,nil);
      if !*kernreverse then new!-korder:=reverse new!-korder;
      old!-korder:=setkorder new!-korder;
      u := reorder u; % Make var of lowest degree the main one.
      if minusf u then <<sign := not sign; u := negf u>>;
      v := comfac u;   % Since new order may reveal more factors.
      u := quotf1(u,cdr v);
      if domainp u then errach list("Improper factors in factorf");
      % The example on rounded; solve(df(e^x/(e^(2*x)+1)^1.5,x),{x});
      % shows car v can be non-zero.
      w := car v;
      v := fctrf1 cdr v;
      if w then v := car v . (!*k2f car w . cdr w) . cdr v;
      m!-image!-variable := mvar u;
      u :=
        distribute!.multiplicity(factorize!-primitive!-polynomial u,1);
      setkorder old!-korder;
      if sign then u := (negf caar u . cdar u) . cdr u;
      u := fac!-merge(v,1 . u);
      return car u . for each w in cdr u collect (reorder car w . cdr w)
   end) where current!-modulus = current!-modulus;

symbolic procedure factor!-prim!-sqfree!-f!-1(u,n);
   (exptf(car x,n) . for each j in cdr x collect (j . n))
      where x = prsqfrfacf u;

symbolic procedure sqfrf u;
   % U is a non-trivial form which is primitive in all its variables
   % and has an overall numerical coefficient which should be a unit.
   % SQFRF performs square free factorization on U and returns a
   % form power list.
   % Modified to work properly in rounded (real or complex) modes. SLK.
   begin integer n; scalar !*gcd,units,v,w,x,y,z,!*msg,r;
      !*gcd := t;
      if (r := !*rounded) then
         <<on rational; u := numr resimp !*f2q u>>;
      n := 1;
      x := mvar u;
      % With ezgcd off, some sqrts can take a long, long time.
      v := gcdf(u,diff(u,x)) where !*ezgcd = t;
      u := quotf(u,v);
      % If domain is a field, or has non-trivial units, v can have a
      % spurious numerical factor.
      if flagp(dmode!*,'field) and ((y := lnc u) neq 1)
        then <<u := multd(!:recip y,u); v := multd(y,v)>>
      % The following check for units can result in the loss of such
      % a unit.
%      else if (units := get(dmode!*,'units))
%         and (w := assoc(y:= lnc u,units))
%       then <<u := multd(cdr w,u); v := multd(y,v)>>;
      ;
      while degr(v,x)>0 do
       <<w := gcdf(v,u);
         if u neq w then z := (quotf(u,w) . n) . z;
              % Don't add units to z.
         v := quotf(v,w);
         u := w;
         n := n + 1>>;
         if r then
            <<on rounded;
              u := numr resimp !*f2q u;
              z := for each j in z
                       collect numr resimp !*f2q car j . cdr j>>;
      if v neq 1 and assoc(v,units) then v := 1;
      if v neq 1 then if n=1 then u := multf(v,u)
       else if (w := rassoc(1,z)) then rplaca(w,multf(v,car w))
       else if null z and ((w := rootxf(v,n)) neq 'failed)
        then u := multf(w,u)
       else if not domainp v then z := aconc(z,v . 1)
       else errach {"sqfrf failure",u,n,z};
      return (u . n) . z
   end;

symbolic procedure square_free u;
   'list . for each v in sqfrf !*q2f simp!* u
              collect {'list,mk!*sq(car v . 1),cdr v};

flag('(square_free),'opfn);

symbolic procedure diff(u,v);
   % A polynomial differentation routine which does not check
   % indeterminate dependencies.
   if domainp u then nil
    else addf(addf(multpf(lpow u,diff(lc u,v)),
        multf(lc u,diffp1(lpow u,v))),
          diff(red u,v));

symbolic procedure diffp1(u,v);
   if not( car u eq v) then nil
    else if cdr u=1 then 1
    else multd(cdr u,!*p2f(car u .** (cdr u-1)));

endmodule;

end;
