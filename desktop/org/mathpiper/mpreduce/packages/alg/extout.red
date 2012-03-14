module extout; % Extended output package for expressions.

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


fluid '(!*allfac !*div !*mcd !*noequiv !*pri !*rat factors!* kord!*
  !*combinelogs wtl!*);

global '(dnl!* ordl!* upl!*);

switch allfac,div,pri,rat;

!*allfac := t;          % factoring option for this package
!*pri := t;             % to activate this package

% dnl!* := nil;         % output control flag: puts powers in denom
% factors!* := nil;     % list of output factors
% ordl!* := nil;        % list of kernels introduced by ORDER statement
% upl!* := nil;         % output control flag: puts denom powers in
                        % numerator
% !*div := nil;         % division option in this package
% !*rat := nil;         % flag indicating rational mode for output.

symbolic procedure factor u;
   factor1(u,t,'factors!*);

symbolic procedure factor1(u,v,w);
   begin scalar x,y,z,r;
        y := lispeval w;
        for each j in u do
          if (x := getrtype j) and (z := get(x,'factor1fn))
              then apply2(z,u,v)
            else <<while eqcar(j:=reval j,'list) and cdr j do
                     <<r:=append(r,cddr j); j:=cadr j>>;
                   x := !*a2kwoweight j;
                   if v then y := aconc!*(delete(x,y),x)
                    else if not(x member y)
                     then msgpri(nil,j,"not found",nil,nil)
                    else y := delete(x,y)>>;
        set(w,y);
        if r then return factor1(r,v,w)
   end;

symbolic procedure remfac u;
   factor1(u,nil,'factors!*);

rlistat '(factor remfac);

symbolic procedure order u;
   <<rmsubs();   % Since order of terms in an operator argument can
                 % affect simplification.
     if u and null car u and null cdr u then (ordl!* := nil)
      else for each x in kernel!-list u do
        <<if x member ordl!* then ordl!* := delete(x,ordl!*);
          ordl!* := aconc!*(ordl!*,x)>>>>;

rlistat '(order);

symbolic procedure up u;
   factor1(u,t,'upl!*);

symbolic procedure down u;
   factor1(u,t,'dnl!*);

% rlistat '(up down);  % Omitted since not documented.

symbolic procedure formop u;
   if domainp u then u
    else raddf(multop(lpow u,formop lc u),formop red u);

symbolic procedure multop(u,v);
   if null kord!* then multpf(u,v)
    else if car u eq 'k!* then v
    else rmultpf(u,v);

symbolic smacro procedure lcx u;
   % Returns leading coefficient of a form with zero reductum, or an
   % error otherwise.
   cdr carx(u,'lcx);

symbolic procedure quotof(p,q);
   % P is a standard form, Q a standard form which is either a domain
   % element or has zero reductum.
   % Returns the quotient of P and Q for output purposes.
   if null p then nil
    else if p=q then 1
    else if q=1 then p
    else if domainp q then quotofd(p,q)
    else if domainp p
      % Make sure free variable degrees are accommodated.
     then (mksp(mvar q,
                if numberp x then -x else {'minus,x})
                    .* quotof(p,lcx q) .+ nil) where x = ldeg q
    else (lambda (x,y);
          if car x eq car y
              then (lambda (n,w,z);
                 if n=0 then raddf(w,z)
                  else ((car y .** n) .* w) .+ z)
              (cdr x-cdr y,quotof(lc p,lcx q),quotof(red p,q))
           else if ordop(car x,car y)
              then (x .* quotof(lc p,q)) .+ quotof(red p,q)
           else mksp(car y,- cdr y) .* quotof(p,lcx q) .+ nil)
       (lpow p,lpow q);

symbolic procedure quotofd(p,q);
   % P is a form, Q a domain element. Value is quotient of P and Q
   % for output purposes.
   if null p then nil
    else if domainp p then quotodd(p,q)
    else (lpow p .* quotofd(lc p,q)) .+ quotofd(red p,q);

symbolic procedure quotodd(p,q);
   % P and Q are domain elements. Value is domain element for P/Q.
   if atom p and atom q then int!-equiv!-chk mkrn(p,q)
    else lowest!-terms(p,q);

symbolic procedure lowest!-terms(u,v);
   % Reduces compatible domain elements U and V to a ratio in lowest
   % terms.  Value as a rational may contain domain arguments rather
   % just integers.  Modified to use dcombine for field division.
   if u=v then 1
    else if flagp(dmode!*,'field) or not atom u and flagp(car u,'field)
       or not atom v and flagp(car v,'field)
%    then multdm(u,!:recip v)
     then dcombine!*(u,v,'quotient)
     else begin scalar x;
      if atom(x := dcombine!*(u,v,'gcd)) and x neq 1 then
         <<u := dcombine!*(u,x,'quotient);
           v := dcombine!*(v,x,'quotient)>>;
      return if v=1 then u else '!:rn!: . (u . v)
   end;

symbolic procedure dcombine!*(u,v,w);
   if atom u and atom v then apply2(w,u,v) else dcombine(u,v,w);

symbolic procedure ckrn u;
   % Factors out the leading numerical coefficient from field domains.
   if flagp(dmode!*,'field) and not(dmode!* memq '(!:rd!: !:cr!:))
     then begin scalar x;
       x := lnc u;
       x := multf(x,ckrn1 quotfd(u,x));
       if null x then x := 1;
          % NULL could be caused by floating point underflow.
       return x
      end
     else ckrn1 u;

symbolic procedure ckrn1 u;
   begin scalar x;
        if domainp u then return u;
    a:  x := gck2(ckrn1 cdar u,x);
        if null cdr u
          then return if noncomp mvar u then x else list(caar u . x)
         else if domainp cdr u or not(caaar u eq caaadr u)
          then return gck2(ckrn1 cdr u,x);
        u := cdr u;
        go to a
   end;

symbolic procedure gck2(u,v);
   % U and V are domain elements or forms with a zero reductum.
   % Value is the gcd of U and V.
   if null v then u
    else if u=v then u
    else if domainp u
     then if domainp v then
        if flagp(dmode!*,'field)
          or pairp u and flagp(car u,'field)
          or pairp v and flagp(car v,'field) then 1
           else if dmode!* eq '!:gi!: then intgcdd(u,v) else gcddd(u,v)
        else gck2(u,cdarx v)
    else if domainp v then gck2(cdarx u,v)
    else (lambda (x,y);
        if car x eq car y
          then list((if cdr x>cdr y then y else x) .
                    gck2(cdarx u,cdarx v))
         else if ordop(car x,car y) then gck2(cdarx u,v)
         else gck2(u,cdarx v))
    (caar u,caar v);

symbolic procedure cdarx u;
   cdr carx(u,'cdar);

symbolic procedure negf!* u; negf u where !*noequiv = t;

symbolic procedure prepsq!* u;
   begin scalar x,y,!*combinelogs;
        if null numr u then return 0;
        % The following leads to some ugly output.
%        else if minusf numr u
%         then return list('minus,prepsq!*(negf!* numr u ./ denr u));
        x := setkorder ordl!*;
        setkorder
                  append(sort(for each j in factors!*
                     join if not idp j then nil
                           else if y := get(j,'prepsq!*fn)
                            then apply2(y,u,j)
                           else for each k in get(j,'klist)
                                     collect car k,'ordop),
                   append(sort(factors!*,'ordop),ordl!*));
        if kord!* neq x or wtl!*
          then u := formop numr u . formop denr u;
%       u := if !*rat or (not flagp(dmode!*,'field) and !*div)
        u := if !*rat or !*div
                      or upl!* or dnl!*
               then replus prepsq!*1(numr u,denr u,nil)
              else sqform(u,function prepsq!*2);
        setkorder x;
        return u
end;

symbolic procedure prepsq!*0(u,v);
   % U is a standard quotient, but not necessarily in lowest terms.
   % V a list of factored powers.
   % Value is equivalent list of prefix expressions (an implicit sum).
   begin scalar x;
      return if null numr u then nil
              else if (x := gcdf(numr u,denr u)) neq 1
        then prepsq!*1(quotf(numr u,x),quotf(denr u,x),v)
       else prepsq!*1(numr u,denr u,v)
   end;

symbolic procedure prepsq!*1(u,v,w);
   % U and V are the numerator and denominator expression resp,
   % in lowest terms.
   % W is a list of powers to be factored from U.
   begin scalar x,y,z;
        % Look for "factors" in the numerator.
        if not domainp u and (mvar u member factors!* or (not
                atom mvar u and car mvar u member factors!*))
          then return nconc!*(
               if v=1 then prepsq!*0(lc u ./ v,lpow u . w)
                else (begin scalar n,v1,z1;
                % See if the same "factor" appears in denominator.
                n := ldeg u;
                v1 := v;
                z1 := !*k2f mvar u;
                while (z := quotfm(v1,z1)) do <<v1 := z; n := n-1>>;
                return
                  prepsq!*0(lc u ./ v1,
                            if n>0 then (mvar u .** n) . w
                             else if n<0
                              then mksp(list('expt,mvar u,n),1) . w
                             else w)
                   end),
                        prepsq!*0(red u ./ v,w));
        % Now see if there are any remaining "factors" in denominator.
        % (KORD!* contains all potential kernel factors.)
        if not domainp v
         then for each j in kord!* do
           begin integer n; scalar z1;
                n := 0;
                z1 := !*k2f j;
                while z := quotfm(v,z1) do <<n := n-1; v := z>>;
                if n<0 then w := mksp(list('expt,j,n),1) . w
           end;
        % Now all "factors" have been removed.
        if kernlp u then <<u := mkkl(w,u); w := nil>>;
        if dnl!*
          then <<x := if null !*allfac then 1 else ckrn u;
                 z := ckrn!*(x,dnl!*);
                 x := quotof(x,z);
                 u := quotof(u,z);
                 v := quotof(v,z)>>;
        if upl!*
          then <<y := ckrn v;
                 z := ckrn!*(y,upl!*);
                 y := quotof(y,z);
                 u := quotof(u,z);
                 v := quotof(v,z)>>
         else if !*div then y := ckrn v
         else y := 1;
        u := canonsq (u . quotof(v,y));
%       if !*gcd then u := cancel u;
        u := quotof(numr u,y) ./ denr u;
        if !*allfac
          then <<x := ckrn numr u; y := ckrn denr u;
                 if (x neq 1 or y neq 1)
                    and (x neq numr u or y neq denr u)
                  then <<v := quotof(denr u,y);
                         u := quotof(numr u,x);
                         w := prepf mkkl(w,x);
                         x := prepf y;
                         u := addfactors(w,u);
                         v := addfactors(x,v);
                         return if v=1 then rmplus u
                                 else list if eqcar(u,'minus)
                                             then list('minus,
                                               list('quotient,cadr u,v))
                                           else list('quotient,u,v)>>>>;
        return if w then list retimes aconc!*(exchk w,prepsq u)
         else rmplus prepsq u
   end;

symbolic procedure addfactors(u,v);
   % U is a (possible) product of factors, v a standard form.
   % Result is a folded prefix expression.
   if u = 1 then prepf v
    else if v = 1 then u
    else if eqcar(u,'times) then 'times . aconc!*(cdr u,prepf v)
    else retimes list(u,prepf v);

symbolic procedure rmplus u; if eqcar(u,'plus) then cdr u else list u;

symbolic procedure prepsq!*2 u; replus prepsq!*1(u,1,nil);

symbolic procedure ckrn!*(u,v);
   if null u then errach 'ckrn!*
    else if domainp u then 1
    else if caaar u member v
       then list (caar u . ckrn!*(cdr carx(u,'ckrn),v))
    else ckrn!*(cdr carx(u,'ckrn),v);

symbolic procedure mkkl(u,v);
   if null u then v else mkkl(cdr u,list (car u . v));

symbolic procedure quotfm(u,v);
   begin scalar !*mcd; !*mcd := t; return quotf(u,v) end;

endmodule;

end;
