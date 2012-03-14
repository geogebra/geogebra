module polrep; % Arithmetic operations on standard forms and quotients.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid '(!*asymp!* !*exp !*factor !*gcd !*lcm !*mcd !*rationalize frlis!*
        !*roundall !*rounded !*sqfree !*sub2 asymplis!* dmode!* subfg!*
        ncmp!* powlis!* wtl!* !*!*processed !*ncmp);

global '(!*group rd!-tolerance!* cr!-tolerance!*);

put('roundall,'simpfg,'((t (rmsubs))));

switch roundall;

!*roundall := t;   % Default is on.

symbolic smacro procedure subtrsq(u,v); addsq(u,negsq v);

symbolic procedure addsq(u,v);
   % U and V are standard quotients.
   % Value is canonical sum of U and V.
   if null numr u then v
    else if null numr v then u
    else if denr u=1 and denr v=1 then addf(numr u,numr v) ./ 1
    else begin scalar x,y,z;
        if null !*exp then <<u := numr u ./ mkprod denr u;
                             v := numr v ./ mkprod denr v>>;
        if !*lcm then x := gcdf!*(denr u,denr v)
         else x := gcdf(denr u,denr v);
        z := canonsq(quotf(denr u,x) ./ quotf(denr v,x));
        y := addf(multf(denr z,numr u),multf(numr z,numr v));
        if null y then return nil ./ 1;
        z := multf(denr u,denr z);
        return if x=1 or (x := gcdf(y,x))=1 then y ./ z
                else canonsq(quotf(y,x) ./ quotf(z,x))
    end;

symbolic procedure multsq(u,v);
   % U and V are standard quotients.
   % Value is canonical product of U and V.
   if null numr u or null numr v then nil ./ 1
    else if denr u=1 and denr v=1 then multf(numr u,numr v) ./ 1
    else begin scalar x,y,z;
        x := gcdf(numr u,denr v);
        y := gcdf(numr v,denr u);
        z := multf(quotf(numr u,x),quotf(numr v,y));
        x := multf(quotf(denr u,y),quotf(denr v,x));
        return canonsq(z ./ x)
    end;

symbolic procedure negsq u; negf numr u ./ denr u;

smacro procedure multpq(u,v);
   multsq(!*p2q u,v);

symbolic procedure cancel u;
   %returns canonical form of non-canonical standard form U;
   if !*mcd or denr u=1 then multsq(numr u ./ 1,1 ./ denr u)
    else multsq(numr u ./ 1,simpexpt list(mk!*sq(denr u ./ 1),-1));


% ***** FUNCTIONS FOR ADDING AND MULTIPLYING STANDARD FORMS *****

symbolic smacro procedure peq(u,v);
   %tests for equality of powers U and V;
   u = v;

symbolic procedure addf(u,v);
   % U and V are standard forms. Value is standard form for U+V.
   if null u then v
    else if null v then u
    else if domainp u then addd(u,v)
    else if domainp v then addd(v,u)
    else if peq(lpow u,lpow v)
       then (if null x then y else lpow u .* x .+ y)
             where x=addf(lc u,lc v),y=addf(red u,red v)
    else if ordpp(lpow u,lpow v) then lt u .+ addf(red u,v)
    else lt v .+ addf(u,red v);

symbolic procedure addd(u,v);
   % U is a domain element, V a standard form.
   % Value is a standard form for U+V.
   if null v then u
    else if domainp v then adddm(u,v)
    else lt v .+ addd(u,red v);

symbolic procedure adddm(u,v);
   % U and V are both non-zero domain elements.
   % Value is standard form for U+V.
   % The int-equiv-chk is needed to convert say (:MOD: . 0) to NIL.
   % A simpler function might therefore be possible and more efficient.
   if atom u and atom v
     then (if null dmode!* or not flagp(dmode!*,'convert) then !*n2f x
            else int!-equiv!-chk apply1(get(dmode!*,'i2d),x))
          where x=plus2(u,v)
    else dcombine(u,v,'plus);

symbolic procedure domainp u; atom u or atom car u;

symbolic procedure noncomp u;
   !*ncmp and noncomp1 u;

symbolic procedure noncomp1 u;
  if null pairp u then nil
   else if pairp car u then noncomfp u
   else if car u eq 'taylor!* then nil
   else flagp(car u,'noncom) or noncomlistp cdr u;

symbolic procedure noncomlistp u;
   pairp u and (noncomp1 car u or noncomlistp cdr u);

symbolic procedure multf(u,v);
   % U and V are standard forms.
   % Value is standard form for U*V.
   begin scalar x,y;
    a:  if null u or null v then return nil
         else if u=1 then return v     % ONEP
         else if v=1 then return u     % ONEP
         else if domainp u then return multd(u,v)
         else if domainp v then return multd(v,u)
         else if not(!*exp or ncmp!* or wtl!* or x)
          then <<u := mkprod u; v := mkprod v; x := t; go to a>>;
        x := mvar u;
        y := mvar v;
        if noncomfp v and (noncomp x or null !*!*processed)
          then return multfnc(u,v)
         else if x eq y
          then <<% Allow for free variables in rules.
                 if not fixp ldeg u or not fixp ldeg v
                   then x := x .** reval list('plus,ldeg u,ldeg v)
                  else x := mkspm(x,ldeg u+ldeg v);
                 % The order in the next line is IMPORTANT. See analysis
                 % by J.H. Davenport et al. for details.
                 y := addf(multf(red u,v),multf(!*t2f lt u,red v));
                 return if null x or null(u := multf(lc u,lc v))
                    then <<!*asymp!* := t; y>>
                   else if x=1 then addf(u,y)
                   else if null !*mcd then addf(!*t2f(x .* u),y)
                   else x .* u .+ y>>
         else if ordop(x,y)
          then <<x := multf(lc u,v);
                 y := multf(red u,v);
                 return if null x then y else lpow u .* x .+ y>>;
        x := multf(u,lc v);
        y := multf(u,red v);
        return if null x then y else lpow v .* x .+ y
   end;

symbolic procedure noncomfp u;
   % It's possible that ncmp!* would work here.
   !*ncmp and noncomfp1 u;

symbolic procedure noncomfp1 u;
   not domainp u
      and (noncomp mvar u or noncomfp1 lc u or noncomfp1 red u);

symbolic procedure multfnc(u,v);
   % Returns canonical product of U and V, with both main vars non-
   % commutative.
   begin scalar x,y;
      x := multf(lc u,!*t2f lt v);
      if null x then nil
       else if not domainp x and mvar x eq mvar u
        then x := addf(if null (y := mkspm(mvar u,ldeg u+ldeg x))
                         then nil
                        else if y = 1 then lc x
                        else !*t2f(y .* lc x),
                       multf(!*p2f lpow u,red x))
       else if noncomp mvar u then x := !*t2f(lpow u .* x)
       else x := multf(!*p2f lpow u,x) where !*!*processed=t;
      return addf(x,addf(multf(red u,v),multf(!*t2f lt u,red v)))
   end;

symbolic procedure multd(u,v);
   % U is a domain element, V a standard form.
   % Value is standard form for U*V.
   if null v then nil
    else if v=1 then u      % Common enough to be tested.
    else if domainp v then multdm(u,v)
    else lpow v .* multd(u,lc v) .+ multd(u,red v);

symbolic procedure multdm(u,v);
   % U and V are both non-zero domain elements.
   % Value is standard form for U*V;
   if atom u and atom v
     then (lambda x; if null dmode!*
                        or not flagp(dmode!*,'convert) then x
                      else % int!-equiv!-chk
                              apply1(get(dmode!*,'i2d),x))
           times2(u,v)
    else dcombine(u,v,'times);

smacro procedure multpf(u,v); multf(!*p2f u,v);

symbolic procedure negf u;
   if null u then nil
    else if domainp u
           then !:minus if atom u and flagp(dmode!*,'convert)
                          then apply1(get(dmode!*,'i2d),u)
                         else u
    else lpow u .* negf lc u .+ negf red u;

symbolic procedure degreef(pol,var);
   % Find degree of kernel in standard form pol.
   % Note: uniqueness of kernel assumed.
   if domainp pol then 0
    else if mvar pol eq var then ldeg pol
    else max(degreef(lc pol,var),degreef(red pol,var));

put('!*sq,'lengthfn,'!*sqlength);

symbolic procedure !*sqlength u;
   (if denr car u=1 then x else x+termsf denr car u)
    where x = termsf numr car u;

symbolic procedure terms u;
   <<lprim "Please use LENGTH instead"; termsf numr simp!* u>>;

flag('(terms),'opfn);

flag('(terms),'noval);

symbolic procedure termsf u;
   % U is a standard form.
   % Value is number of terms in U (excluding kernel structure).
   begin integer n;
      while not domainp u do <<n := n + termsf lc u; u := red u>>;
      return if null u then n else n+1
   end;

symbolic procedure tmsf u;
   % U is a standard form.
   % Value is number of terms in U (including kernel structure).
   begin integer n; scalar x;
    % Integer declaration initializes N to 0.
      while not domainp u do
       <<n := n+(if sfp(x := mvar u) then tmsf x else 1)+tmsf!* lc u;
         if ldeg u neq 1 then if ldeg u=2 then n := n+1 else n := n+2;
         u := red u>>;   % Previously, if U was non-zero, we used to add
                         % one more here.
      return if null u then n else n+1
   end;

symbolic procedure tmsf!* u;
   if numberp u and abs fix u=1 then 0 else tmsf u; % Was tmsf u+1.

symbolic procedure tms u; tmsf numr simp!* u;

flag('(tms),'opfn);

flag('(tms),'noval);


% ***** FUNCTIONS FOR WORKING WITH STRUCTURED FORMS *****

fluid '(!*really_off_exp);

symbolic procedure offexpchk u;
   % Return structured form for standard quotient u.
   % The freevar check is required to correctly evaluate rules like
   % for all n let f(a^n-b^n)=c when exp is off and gcd on.
   if !*really_off_exp or
        (frlis!* and freevarinexptchk numr u or freevarinexptchk denr u)
     then u
    else canprod(mkprod numr u,mkprod denr u);

symbolic procedure freevarinexptchk u;
   not domainp u and(not numberp ldeg u or freevarinexptchk lc u
                                   or freevarinexptchk red u);

symbolic procedure mkprod u;
   begin scalar w,x,y,z,!*exp,!*sub2;
      if null u or kernlp u then return u;
      % First make sure there are no further simplifications.
      !*sub2 := t;
      x := subs2(u ./ 1);
      if denr x neq 1 then return u  % We can't do much more here.
       else if numr x neq u
        then <<u := numr x; if null u or kernlp u then return u>>;
      !*exp := t;
      w := ckrn u;
      u := quotf(u,w);
      x := expnd u;
      if null x or kernlp x then return multf(w,x);
      % After this point, X is not KERNLP.
      % The check below for *MCD was suggested by James Davenport.
      % Without it, on gcd; off mcd,exp; (x**2+2x+1)/x+1; loops
      % forever.
      if !*mcd and (!*sqfree or !*factor or !*gcd)
        then y := fctrf x
       else <<y := ckrn x; x := quotf(x,y); y := list(y,x . 1)>>;
      if cdadr y>1 or cddr y
        then <<z := car y;
               for each j in cdr y do
                  z := multf(mksp!*(car j,cdr j),z)>>
       else if not !*group and tmsf u>tmsf caadr y
        then z := multf(mksp!*(caadr y,cdadr y),car y)
       else z := mksp!*(u,1);
      return multf(w,z)
   end;

symbolic procedure expnd u;
  if !*really_off_exp then u else
   begin scalar !*sub2,v;
      u := expnd1 u;
      return if !*sub2 and denr(v := subs2f u) = 1 then numr v
              else u
   end;

symbolic procedure expnd1 u;
   if domainp u then u
    else addf(if not sfp mvar u or ldeg u<0
                then multpf(lpow u,expnd1 lc u)
        else multf(exptf(expnd1 mvar u,ldeg u),expnd1 lc u),
                        expnd1 red u);

symbolic procedure canprod(p,q);
   % P and Q are kernel product standard forms, value is P/Q in
   % which a  top level standard form kernel by itself has been
   % unwound.
   begin scalar v,w,x,y,z;
      if domainp q or red q or (not domainp p and red p)
        then return cancel(p ./ q);
      % Look for possible cancellations.
      while not domainp p or not domainp q do
      if sfpf p then <<z := cprod1(mvar p,ldeg p,v,w);
                       v := car z; w := cdr z; p := lc p>>
       else if sfpf q then <<z := cprod1(mvar q,ldeg q,w,v);
                             w := car z; v := cdr z; q := lc q>>
       else if domainp p then <<y := lpow q . y; q := lc q>>
       else if domainp q then <<x := lpow p . x; p := lc p>>
       else <<x := lpow p . x; y := lpow q . y;
              p := lc p; q := lc q>>;
      v := reprod(v,reprod(x,p));
      w := reprod(w,reprod(y,q));
      if minusf w then <<v := negf v; w := negf w>>;
      w := cancel(v ./ w);
      % Final check for unnecessary structure.
      v := numr w;
      if not domainp v and null red v and lc v=1
         and ldeg v=1 and sfp(x := mvar v)
        then v := x;
      w := denr w;
      if not domainp w and null red w and lc w=1
         and ldeg w=1 and sfp(x := mvar w)
        then w := x;
      return canonsq(v ./ w)
   end;

symbolic procedure sfpf u;
   not domainp u and sfp mvar u;

symbolic procedure sfp u;
   % True if mvar U is a standard form.
   not atom u and not atom car u;

symbolic procedure reprod(u,v);
   % U is a list of powers, V a standard form.
   % Value is product of terms in U with V.
   <<while u do <<v := multpf(car u,v); u := cdr u>>; v>>;

symbolic procedure cprod1(p,m,v,w);
   % U is a standard form, which occurs in a kernel raised to power M.
   % V is a list of powers multiplying P**M, W a list dividing it.
   % Value is a dotted pair of lists of powers after all possible
   % kernels have been cancelled.
   begin scalar z;
      z := cprod2(p,m,w,nil);
      w := cadr z;
      v := append(cddr z,v);
      z := cprod2(car z,m,v,t);
      v := cadr z;
      w := append(cddr z,w);
      if car z neq 1 then v := mksp(car z,m) . v;
      return v . w
   end;

symbolic procedure cprod2(p,m,u,b);
   %P and M are as in CPROD1. U is a list of powers. B is true if P**M
   %multiplies U, false if it divides.
   %Value has three parts: the first is the part of P which does not
   %have any common factors with U, the second a list of powers (plus
   %U) which multiply U, and the third a list of powers which divide U;
   %it is implicit here that the kernel standard forms are positive;
   begin scalar n,v,w,y,z;
      while u and p neq 1 do
        <<if (z := gcdf(p,caar u)) neq 1
            then
           <<p := quotf(p,z);
             y := quotf(caar u,z);
             if y neq 1 then v := mksp(y,cdar u) . v;
             if b then v := mksp(z,m+cdar u) . v
              else if (n := m-cdar u)>0 then w := mksp(z,n) . w
              else if n<0 then v := mksp(z,-n) . v>>
            else v := car u . v;
           u := cdr u>>;
      return (p . nconc!*(u,v) . w)
   end;

symbolic procedure mkspm(u,p);
   %U is a unique kernel, P an integer;
   %value is 1 if P=0, NIL if U**P is 0, else standard power of U**P;
   % should we add a check for NOT(U EQ K!*) in first line?
   if p=0 then 1
    else begin scalar x;
        if subfg!* and (x:= atsoc(u,asymplis!*)) and cdr x<=p
          then return nil;
        sub2chk u;
        return u .** p
   end;

symbolic procedure sub2chk u;
   %determines if kernel U is such that a power substitution is
   %necessary;
   if subfg!*
      and(atsoc(u,powlis!*) or not atom u and car u memq '(expt sqrt))
     then !*sub2 := t;


% ***** FUNCTIONS FOR DIVIDING STANDARD FORMS *****

symbolic procedure quotsq(u,v);
   multsq(u,invsq v);

symbolic procedure quotf!*(u,v);
   % We do the rationalizesq step to allow for surd divisors.
   if null u then nil
    else (if x then x
           else (if denr y = 1 then numr y
                  else errach list("DIVISION FAILED",u,v))
                 where y=rationalizesq(u ./ v))
          where x=quotf(u,v);

symbolic procedure quotf(u,v);
%  begin scalar xexp;
%       xexp := !*exp;
%       !*exp := t;
%       u := quotf1(u,v);
%       !*exp := xexp;
%       return u
%  end;
   quotf1(u,v) where !*exp = t;

symbolic procedure quotf1(p,q);
   % P and Q are standard forms.
   % Value is the quotient of P and Q if it exists or NIL.
   if null p then nil
    else if p=q then 1
    else if q=1 then p
    else if domainp q then quotfd(p,q)
    else if domainp p then nil
    else if mvar p eq mvar q
     then begin scalar u,v,w,x,xx,y,z,z1; integer n;
    a:if idp(u := rank p) or idp(v := rank q) or u<v then return nil;
        % The above IDP test is because of the possibility of a free
        % variable in the degree position from LET statements.
        u := lt!* p;
        v := lt!* q;
        w := mvar q;
        x := quotf1(tc u,tc v);
        if null x then return nil;
        n := tdeg u-tdeg v;
        if n neq 0 then y := w .** n;
%       p := addf2zro(p,multf(if n=0 then q
%       p := addf(p,multf(negf x,if n=0 then q else multpf(y,q)));
        xx := multf(negf x,red q);
        % The following expression for p explicitly calculates the
        % needed remainder.  It has to be this way for non-commuting
        % expressions.
        p := addf(red p,if n=0 then xx else multpf(y,xx));
        % Leading terms of P and Q do not cancel if MCD is off. However,
        % there may be a problem with off exp.
        if p and (domainp p or not(mvar p eq w)) then return nil
         else if n=0 then go to b;
        z := aconc!*(z,y .* x);
        %provided we have a non-zero power of X, terms
        %come out in right order;
        if null p then return if z1 then rnconc(z,z1) else z;
        go to a;
    b:  if null p then return rnconc(z,x)
         else if !*mcd then return nil
         else z1 := x;
        go to a
   end
    else if ordop(mvar p,mvar q) then quotk(p,q)
    else nil;

% symbolic procedure addf2zro(p,q);
%    <<q := addf(p,q);
%      if not dmode!* memq '(!:rd!: !:cr!:)
%         or null q or null (p := quotf(q,p)) then q
%      else if domainp p then
%         if eqcar(p,'!:rd!:)
%            and lessp!:(abs!: bfloat round!* p,rd!-tolerance!*)
%         or eqcar(p,'!:cr!:)
%        and (lessp!:(plus!:(times!:(x,x),times!:(y,y)),cr!-tolerance!*)
%               where x=bfloat round!* tagrl p,y=bfloat round!* tagim p)
%        then nil else q>>;

symbolic procedure rnconc(u,v);
   if null u then v
    else if !*ncmp and noncomfp1 u and noncomfp1 v then addf(u,v)
    else begin scalar w;
   % This is like nconc, but doesn't assume its second argument is a
   % list.
            w := u;
            while cdr w do <<w := cdr w>>;
            rplacd(w,v);
            return u
         end;

symbolic procedure quotfd(p,q);
   % P is a standard form, Q a domain element.
   % Value is P/Q if exact division is possible, or NIL otherwise.
   if p=q then 1
   else if flagp(dmode!*,'field) then divd(p,q)
   else if domainp p then quotdd(p,q)
   else quotk(p,q);

symbolic procedure divd(v,u);
   % U is a domain element, V a standard form.
   % Value is standard form for V/U.
   if null u
     then if null v then rerror(poly,1,"0/0 formed")
           else rerror(poly,2,"Zero divisor")
    else if null v then nil
    else if domainp v then divdm(v,u)
    else lpow v .* divd(lc v,u) .+ divd(red v,u);

symbolic procedure divdm(v,u);
   % U and V are both non-zero domain elements.
   % Value is standard form for V/U.
   if atom v and atom u
     then if remainder(v,u)=0 then v/u
           else !:rn2rd mkrn(v,u)
    else y % (if null dmode!* then y else int!-equiv!-chk y)
          where y=dcombine(v,u,'quotient);

symbolic procedure quotdd(u,v);
   % U and V are domain elements.  Value is U/V if division is exact,
   % NIL otherwise.
   begin scalar w;
      if atom u then
        if atom v then
          <<w := divide(u,v); return if cdr w = 0 then car w else nil>>
          else if (w := get(car v,'i2d)) then u := apply1(w,u)
      else if atom v and (w := get(car u,'i2d)) then v := apply1(w,v);
      return dcombine(u,v,'quotient)
   end;

symbolic procedure quotk(p,q);
   (lambda w;
      if w then if null red p then list (lpow p .* w)
                 else (lambda y;if y then lpow p .* w .+ y else nil)
                          quotf1(red p,q)
         else nil)
      quotf1(lc p,q);

symbolic procedure rank p;
   %P is a standard form
   %Value is the rank of P;
   if !*mcd then ldeg p
    else begin integer m,n; scalar y;
        n := ldeg p;
        y := mvar p;
    a:  m := ldeg p;
        if null red p then return n-m;
        p := red p;
        if degr(p,y)=0 then return if m<0 then if n<0 then -m
                else n-m else n;
        go to a
    end;

symbolic procedure lt!* p;
   %Returns true leading term of polynomial P;
   if !*mcd or ldeg p>0 then car p
    else begin scalar x,y;
        x := lt p;
        y := mvar p;
    a:  p := red p;
        if null p then return x
         else if degr(p,y)=0 then return (y . 0) .* p;
        go to a
   end;

symbolic procedure remf(u,v);
   %returns the remainder of U divided by V;
   if null v then rerror(poly,201,"Zero divisor") else cdr qremf(u,v);

put('remainder,'polyfn,'remf);

symbolic procedure qremf(u,v);
   % Returns the quotient and remainder of U divided by V.
   % Exp cannot be off, otherwise a loop can occur. e.g.,
   % qremf('(((x . 1) . -1) . 1),'(((x . 2)  . -3) . 4)).
   begin integer n; scalar !*exp,x,y,z;
        !*exp := t;
        if domainp v then return qremd(u,v);
        z := list nil;   % Final value.
    a:  if domainp u then return praddf(z,nil . u)
         else if mvar u eq mvar v
          then if (n := ldeg u-ldeg v)<0 then return praddf(z,nil . u)
                else <<x := qremf(lc u,lc v);
                y := multpf(lpow u,cdr x);
                z := praddf(z,(if n=0 then car x
                                else multpf(mvar u .** n,car x))
                                . y);
                u := if null car x then red u
                        else addf(addf(u,multf(if n=0 then v
                                        else multpf(mvar u .** n,v),
                                        negf car x)), negf y);
                go to a>>
         else if not ordop(mvar u,mvar v)
          then return praddf(z,nil . u);
        x := qremf(lc u,v);
        z := praddf(z,multpf(lpow u,car x) . multpf(lpow u,cdr x));
        u := red u;
        go to a
   end;

symbolic procedure praddf(u,v);
   % U and V are dotted pairs of standard forms.
   addf(car u,car v) . addf(cdr u,cdr v);

symbolic procedure qremd(u,v);
   % Returns a dotted pair of quotient and remainder of form U
   % divided by domain element V.
   if null u then u . u
    else if v=1 then list u
    else if flagp(dmode!*,'field) then list multd(!:recip v,u)
    else if domainp u then !:divide(u,v)
    else begin scalar x;
        x := qremf(lc u,v);
        return praddf(multpf(lpow u,car x) . multpf(lpow u,cdr x),
                        qremd(red u,v))
   end;

symbolic procedure lqremf(u,v);
   %returns a list of coeffs of powers of V in U, constant term first;
   begin scalar x,y;
      y := list u;
      while car(x := qremf(car y,v)) do y := car x . cdr x . cdr y;
      return reversip!* y
   end;

symbolic procedure minusf u;
   %U is a non-zero standard form.
   %Value is T if U has a negative leading numerical coeff,
   %NIL otherwise;
   if null u then nil
    else if domainp u
           then if atom u then u<0 else apply1(get(car u,'minusp),u)
    else minusf lc u;

symbolic procedure absf!* u;
   % Returns representation for absolute value of standard form U.
   (if domainp u then x else !*p2f mksp(list('abs,prepf x),1))
    where x = absf u;

symbolic procedure absf u;
   if minusf u then negf u else u;

symbolic procedure canonsq u;
   % U is a standard quotient.  Value is a standard quotient in which
   % the leading power of the denominator has a positive numerical
   % coefficient and the denominator is normalized where possible.
   if denr u=1 then u
    else if null numr u then nil ./ 1
    else begin scalar x,y;
       % This example shows the following gcd check is needed:
       % a:=1+x/2; let x**2=0; a*a;
       % Should only be needed when an asymptotic reduction occurs.
       if asymplis!* and ((x := gcdf(numr u,denr u)) neq 1)
         then u := quotf(numr u,x) ./ quotf(denr u,x);
       % Now adjust for a positive leading numerical coeff in denr.
        x := lnc denr u;
        if x=1 then return u
         else if atom x then if minusp x
                               then <<u := negf numr u ./ negf denr u;
                                      x := -x>>
                              else nil
         else if apply1(get(car x,'minusp),x)
                               then <<u := negf numr u ./ negf denr u;
                                      x:= apply2(get(car x,'difference),
                                              apply1(get(car x,'i2d),0),
                                                     x)>>;
        % Now check for a global field mode, a leading domain coeff
        % with field properties or "unit" properties so we can adjust
        % numr and denr.  The tests are done in the following order
        % since the other order will give wrong results with some
        % polynomials with decimal coefficients in dmode :gi:.
      return if not numberp x and (y := get(dmode!*,'unitsfn))
            then apply2(y,u,x)
         else if flagp(dmode!*,'field)
            or not atom x and flagp(car x,'field) then fieldconv(x,u)
         else u
   end;

symbolic procedure fieldconv(x,u);
   % U is a standard quotient and x the leading numerical coefficient
   % of the denominator.  Returns inverse(x)*numr u/denr u.
   % X is a domain, but d may not be; dmode!* or x is field.
   begin scalar n,d,y; n := numr u; d := denr u;
      if null dmode!* then
         <<if not eqcar(x,'!:rn!:)
             then if (y := get(car x,'!:rn!:)) and atom y
                    then x := apply1(y,x)
                   else if get(car x,'quotient)
                    then <<x := dcombine(1,x,'quotient);
                           return multd(x,numr u) ./ multd(x,denr u)>>
                   else errach list("field conversion",x);
           x := (car x) . (cddr x) . cadr x;
           return simpgd if domainp d then multd(x,n) ./ 1
                          else multd(x,n) ./ multd(x,d)>>;
      return if domainp d then divd(n,d) ./ 1
              else divd(n,x) ./ divd(d,x)
   end;

symbolic procedure simpgd u;
   if null flagp(dmode!*,'field) then u
   else begin scalar x;
           if (x := gcdf(numr u,denr u)) neq 1
             then u := quotf(numr u,x) ./ quotf(denr u,x);
           return u
        end;

symbolic procedure lnc u;
   % U is a standard form.  Value is the leading numerical coefficient.
   if null u then 0
    else if domainp u then u
    else lnc lc u;

symbolic procedure invsq u;
   begin
      if null numr u then rerror(poly,3,"Zero divisor");
      u := revpr u;
      if !*rationalize then u := gcdchk u;
      % Since result may not be in lowest terms.
      return canonsq u
   end;

symbolic procedure gcdchk u;
   % Makes sure standard quotient u is in lowest terms.
   (if x neq 1 then quotf(numr u,x) ./ quotf(denr u,x) else u)
   where x = gcdf(numr u,denr u);

endmodule;

end;
