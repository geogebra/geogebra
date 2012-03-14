module gcd; % Greatest common divisor routines.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


fluid '(!*exp !*anygcd !*ezgcd !*gcd !*heugcd !*mcd asymplis!* dmode!*
        !*combineexpt);

switch anygcd,ezgcd,heugcd;
!*anygcd := t;

% Note: The handling of non-commuting quantities in the following is
% dubious. The problem is that to do things properly, a left- and
% right-hand gcd and quotient would be necessary.  For now, the code
% returns 1 if the quotient tests fail in gcdf1 for non-commuting
% arguments.

symbolic procedure comfac p;
  % P is a non-atomic standard form.
  % CAR of result is lowest common power of leading kernel in
  % every term in P (or NIL). CDR is gcd of all coefficients of
  % powers of leading kernel.
  % If field elements are involved, lnc is normalized to 1.
  % We need GCDF here since the same function is used by EZGCD.
   begin scalar x,y;
        if flagp(dmode!*,'field) and ((x := lnc p) neq 1)
          then p := multd(!:recip x,p);
        if null red p then return lt p;
        x := lc p;
        y := mvar p;
    a:  p := red p;
        if degr(p,y)=0
          then return nil
            . if domainp p or not(noncomp y and noncomp mvar p)
                then gcdf(x,p)
               else 1
         else if null red p then return lpow p . gcdf(x,lc p)
         else x := gcdf(lc p,x);
        go to a
   end;

symbolic procedure degr(u,var);
   if domainp u or not(mvar u eq var) then 0 else ldeg u;

put('gcd,'polyfn,'gcdf!*);

put('gcd,'number!-of!-args,2);

symbolic procedure gcdf!*(u,v);
   begin scalar !*gcd; !*gcd := t; return gcdf(u,v) end;

symbolic procedure gcdf(u,v);
   % U and V are standard forms.
   % Value is the gcd of U and V, complete only if *GCD is true.
   begin scalar !*exp,!*rounded,mcdsaved;
      % The next line was to prevent numerators moving to denominators
      % as in weight x=1,y=2$ wtlevel 4$ wtest:=(z^4-z^3*y-z^3*x+z^2*y^2
      % +2*z^2*x*y+z^2*x^2-3*z*x^2*y-z*x^3+x^4)/z^5; wtest where z=>a;
      % However, the results are formally correct without it, and it
      % causes other problems.
      % if wtl!* then return 1;
      mcdsaved := !*mcd;
      !*exp := t;
      u := if domainp u or domainp v or not !*ezgcd
      %       or dmode!* memq '(!:rn!: !:rd!:)  % Should be generalized.
              or dmode!* % I don't know what to do in this case.
              or free!-powerp u or free!-powerp v
             then <<if !*combineexpt then !*mcd := t;
                    gcdf1(u,v)>>
            else ezgcdf(u,v);
      !*mcd := mcdsaved;
      return if minusf u then negf u else u
   end;

symbolic procedure free!-powerp u;
   not domainp u
      and (not fixp ldeg u or free!-powerp lc u or free!-powerp red u);

symbolic procedure gcdf1(u,v);
   begin scalar w;
      if null u then return v
       else if null v then return u
       else if u=1 or v=1 then return 1
       else if domainp u then return gcdfd(u,v)
       else if domainp v then return gcdfd(v,u)
% "off anygcd" can prevent anything that could even possibly be expensive.
       else if not !*anygcd or
               not num!-exponents u or
               not num!-exponents v then return 1
       else if quotf1(u,v) then return v
       else if quotf1(v,u) then return u;
      w := gcdf2(u,v);
      if !*gcd and not(dmode!* memq '(!:rd!: !:cr!:))
          and (null quotf1(u,w) or null quotf1(v,w))
        then if noncomfp u or noncomfp v then return 1
              else errach list("gcdf failed",prepf u,prepf v);
              % This probably implies that integer overflow occurred.
      return w
   end;

symbolic procedure gcdf2(u,v);
   % U and V are both non-trivial forms. Value is their GCD.
   % We need to rebind asymplis!* to avoid setting higher powers to 0.
   begin scalar asymplis!*,w,z;
      if not !*anygcd or
         not num!-exponents u or
         not num!-exponents v then return 1;
      if !*gcd and length(w := kernord(u,v))>1
        then <<w := list setkorder w;   % List used to make sure non-NIL
               u := reorder u;
               v := reorder v>>
       else w := nil;
      % Things can go wrong with noncom oprs.  However, empirically we
      % only need to make sure that both u and v do not have a leading
      % noncom opr.
      if mvar u eq mvar v
        then begin scalar x,y;
                x := comfac u;
                y := comfac v;
                z := gcdf1(cdr x,cdr y);
                u := quotf1(u,comfac!-to!-poly x);
                v := quotf1(v,comfac!-to!-poly y);
                if !*gcd then z := multf(gcdk(u,v),z)
                 else if v and quotf1(u,v) then z := multf(v,z)
                 else if u and quotf1(v,u) then z := multf(u,z);
                if car x and car y
                  then if pdeg car x>pdeg car y
                         then z := multpf(car y,z)
                        else z := multpf(car x,z)
             end
       else if noncomp mvar u and noncomp mvar v
        then z := gcdfnc(u,v,mvar v)
       else if ordop(mvar u,mvar v) then z := gcdf1(cdr comfac u,v)
       else z := gcdf1(cdr comfac v,u);
      if w then <<setkorder car w; z := reorder z>>;
      return z
   end;

symbolic procedure gcdfnc(x,p,y);
   if domainp x or not noncomp mvar x then gcdf1(x,p)
    else if null red x then gcdfnc(lc x,p,y)
    else gcdf1(gcdfnc(lc x,p,y),gcdfnc(red x,p,y));

symbolic procedure num!-exponents u;
  % check that all exponents are integers (this may not be true in
  % rules).
   domainp u or
   fixp ldeg u and num!-exponents lc u and num!-exponents red u;

symbolic procedure gcdfd(u,v);
   % U is a domain element, V a form.
   % Value is gcd of U and V.
%  if not atom u and flagp(car u,'field) then 1 else gcdfd1(u,v);
   if flagp(dmode!*,'field) then 1 else gcdfd1(u,v);

symbolic procedure gcdfd1(u,v);
   if null v then u
    else if domainp v then gcddd(u,v)
    else gcdfd1(gcdfd1(u,lc v),red v);

symbolic procedure gcddd(u,v);
   %U and V are domain elements.  If they are invertable, value is 1
   %otherwise the gcd of U and V as a domain element;
   if u=1 or v=1 then 1
%   else if atom u and atom v then gcdn(u,v)
    else if atom u then if atom v then gcdn(u,v)
                         else if fieldp v then 1
                         else dcombine(u,v,'gcd)
    else if atom v
     then if flagp(car u,'field) then 1 else dcombine(u,v,'gcd)
    else if flagp(car u,'field) or flagp(car v,'field) then 1
    else dcombine(u,v,'gcd);

symbolic procedure gcdk(u,v);
   % U and V are primitive polynomials in the main variable VAR.
   % Result is gcd of U and V.
   begin scalar lclst,var,w,x;
        if u=v then return u
         else if domainp u or degr(v,(var := mvar u))=0 then return 1
         else if ldeg u<ldeg v then <<w := u; u := v; v := w>>;
        if quotf1(u,v) then return v
         else if !*heugcd and (x := heu!-gcd(u,v)) then return x
%        else if flagp(dmode!*,'field) then return 1
              % otherwise problems arise.
         else if ldeg v=1
           or getd 'modular!-multicheck and modular!-multicheck(u,v,var)
           or not !*mcd
          then return 1;
    a:  w := remk(u,v);
        if null w then return v
         else if degr(w,var)=0 then return 1;
        lclst := addlc(v,lclst);
        if x := quotf1(w,lc w) then w := x
         else for each y in lclst do
              % prevent endless loop in !:gi!: or field modes.
            if atom y and not flagp(dmode!*,'field)
              or not
               (domainp y and (flagp(dmode!*,'field)
                  or ((x := get(car y,'units))
                       and y member (for each z in x collect car z))))
            then while (x := quotf1(w,y)) do w := x;
        u := v; v := prim!-part w;
        if degr(v,var)=0 then return 1 else go to a
   end;

symbolic procedure addlc(u,v);
   if u=1 then v
    else (lambda x;
      if x=1 or x=-1 or not atom x and flagp(car x,'field) then v
       else x . v)
     lc u;

symbolic procedure delallasc(u,v);
   if null v then nil
    else if u eq caar v then delallasc(u,cdr v)
    else car v . delallasc(u,cdr v);

symbolic procedure kernord(u,v);
   <<u := kernord!-split(u,v);
     append(kernord!-sort car u,kernord!-sort cdr u)>>;

symbolic procedure kernord!-split(u,v);
   % splits U and V into a set of powers of those kernels occurring in
   % one form and not the other, and those occurring in both;
   begin scalar x,y;
      u := powers u;
      v := powers v;
      for each j in u do
          if assoc(car j,v) then y := j . y else x := j . x;
      for each j in v do
          if assoc(car j,u) then y := j . y else x := j . x;
      return reversip x . reversip y
   end;

symbolic procedure kernord!-sort u;
   % returns list of kernels ordered so that kernel with lowest maximum
   % power in U (a list of powers) is first, and so on;
   begin scalar x,y;
      while u do
       <<x := maxdeg(cdr u,car u);
         u := delallasc(car x,u);
         y := car x . y>>;
      return y
   end;

symbolic procedure maxdeg(u,v);
   if null u then v
    else if cdar u>cdr v then maxdeg(cdr u,car u)
    else maxdeg(cdr u,v);

symbolic procedure powers form;
   % returns a list of the maximum powers of each kernel in FORM.
   % order tends to be opposite to original order.
   powers0(form,nil);

symbolic procedure powers0(form,powlst);
   if null form or domainp form then powlst
    else begin scalar x;
        if (x := atsoc(mvar form,powlst))
%         then ldeg form>cdr x and rplacd(x,ldeg form)
          then (if ldeg form>cdr x
                  then powlst := repasc(mvar form,ldeg form,powlst))
         else powlst := (mvar form . ldeg form) . powlst;
        return powers0(red form,powers0(lc form,powlst))
     end;

put('lcm,'polyfn,'lcm!*);

put('lcm,'number!-of!-args,2);

symbolic procedure lcm!*(u,v);
   begin scalar !*gcd; !*gcd := t; return lcm(u,v) end;

symbolic procedure lcm(u,v);
   %U and V are standard forms. Value is lcm of U and V;
   if null u or null v then nil
    else if u=1 then v     % ONEP
    else if v=1 then u     % ONEP
    else multf(u,quotf(v,gcdf(u,v)));

symbolic procedure remk(u,v);
   %modified pseudo-remainder algorithm
   %U and V are polynomials, value is modified prem of U and V;
   begin scalar f1,var,x; integer k,n;
        f1 := lc v;
        var := mvar v;
        n := ldeg v;
        while (k := degr(u,var)-n)>=0 do
         <<x := negf multf(lc u,red v);
           if k>0 then x := multpf(var .** k,x);
           u := addf(multf(f1,red u),x)>>;
        return u
   end;

symbolic procedure prim!-part u;
   %returns the primitive part of the polynomial U wrt leading var;
   quotf1(u,comfac!-to!-poly comfac u);

symbolic procedure comfac!-to!-poly u;
   if null car u then cdr u else list u;

endmodule;

end;
