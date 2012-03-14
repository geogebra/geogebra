module hf;

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


COMMENT

               ###################################
               ##                               ##
               ##    WEIGHTED HILBERT SERIES    ##
               ##                               ##
               ###################################

This module supports (weighted) Hilbert series computations and
related topics. It contains

        - Two algorithms computing Hilbert series of ideals and
                modules.

Lit.:

[BS]    Bayer, Stillman : J. Symb. Comp. 14 (1992), 31 - 50.

[BCRT]  Bigatti, Conti, Robbiano, Traverso . LNCS 673 (1993), 76 - 88.

The version of the algorithm is chosen through the 'hf!=hf entry on
the property list of 'cali.

END COMMENT;

% Choosing the version of the algorithm and first initialization :

put('cali,'hf!=hf,'hf!=whilb1);

symbolic operator hftestversion;
symbolic procedure hftestversion n;
  if member(n,{1,2}) then
        put('cali,'hf!=hf,mkid('hf!=whilb,n));

% --- first variant : [BS]

symbolic procedure hf!=whilb1(m,w);
% Compute the weighted Hilbert series of the moideal m by the rule
% H(m + (M)) = H((M)) - t^ec(m) * H((M):m)
   if null m then dp_fi 1
   else begin scalar m1,m2;
    for each x in m do
        if mo_linear x then m1:=x . m1 else m2:=x . m2;
    if null m2 then return hf!=whilbmon(m1,w)
    else if null cdr m2 then return hf!=whilbmon(car m2 . m1,w)
    else if hf!=powers m2 then return hf!=whilbmon(append(m1,m2),w)
    else return dp_prod(hf!=whilbmon(m1,w),
            dp_diff(hf!=whilb1(cdr m2,w),
                    dp_times_mo(mo_wconvert(car m2,w),
                        hf!=whilb1(moid_quot(cdr m2,car m2),w))));
    end;

symbolic procedure hf!=whilbmon(m,w);
% Returns the product of the converted dpolys 1 - mo for the
% monomials mo in m.
   if null m then dp_fi 1
   else begin scalar p;
    m:=for each x in m collect
        dp_sum(dp_fi 1,list dp_term(bc_fi(-1),mo_wconvert(x,w)));
    p:=car m;
    for each x in cdr m do p:=dp_prod(p,x);
    return p;
    end;

symbolic procedure hf!=powers m;
% m contains only powers of variables.
  if null m then t
  else (length mo_support car m<2) and hf!=powers cdr m;

Comment

Second variant : by induction on the number of variables using the
exactness of the sequence

        0 --> S/(I:(x))[-deg x] --> S/I --> S/(I+(x)) --> 0

[BCRT] do even better, choosing x not as variable, but as splitting
monomial. I hope to return to that later on.

end Comment;

symbolic procedure hf!=whilb2(m,w);
  if null m then dp_fi 1
   else begin scalar m1,m2,x,p;
    for each x in m do
        if mo_linear x then m1:=x . m1 else m2:=x . m2;
    if null m2 then return hf!=whilbmon(m1,w)
    else if null cdr m2 then return hf!=whilbmon(car m2 . m1,w)
    else if hf!=powers m2 then return hf!=whilbmon(append(m1,m2),w)
    else begin scalar x;
        x:=mo_from_a car mo_support car m2;
        p:=dp_prod(hf!=whilbmon(m1,w),
            dp_sum(hf!=whilb2(moid_red(x . m2),w),
            dp_times_mo(mo_wconvert(x,w),
                hf!=whilb2(moid_quot(m2,x),w))))
        end;
    return p;
    end;

% -------- Weighted Hilbert series from a free resolution --------

symbolic procedure hf_whilb3(u,w);
% Weighted Hilbert series numerator from the resolution u.
  begin scalar sgn,p; sgn:=t;
  for each x in u do
    << if sgn then p:=dp_sum(p,hf!=whilb3(x,w))
       else p:=dp_diff(p,hf!=whilb3(x,w));
       sgn:=not sgn;
    >>;
  return p;
  end;

symbolic procedure hf!=whilb3(u,w);
% Convert column degrees of the dpmat u to a generating polynomial.
  (if length c = dpmat_cols u then
   begin scalar p;
      for each x in c do
        p:=dp_sum(p,{dp_term(bc_fi 1,mo_wconvert(cdr x,w))});
      return p
   end else dp_fi max(1,dpmat_cols u))
  where c:=dpmat_coldegs u;

% ------- The common interface ----------------

symbolic procedure hf_whilb(m,wt);
% Returns the weighted Hilbert series numerator of the dpmat m as
% a dpoly using the internal Hilbert series computation
% get('cali,'hf!=hf) for moideals. m must be a Groebner basis.
  (begin scalar fn,w,lt,p,p1; integer i;
  if null(fn:=get('cali,'hf!=hf)) then
        rederr"No version for the Hilbert function algorithm chosen";
  if dpmat_cols m = 0 then
        return apply2(fn,moid_from_bas dpmat_list m,wt);
    lt:=moid_from_dpmat m;
    for i:=1:dpmat_cols m do
      << p1:=atsoc(i,lt);
         if null p1 then rederr"WHILB with wrong leading term list"
         else p1:=apply2(fn,cdr p1,wt);
         w:=atsoc(i,cali!=degrees);
         if w then p1:=dp_times_mo(mo_wconvert(cdr w,wt),p1);
         p:=dp_sum(p,p1);
       >>;
    return p;
    end) where cali!=degrees:=dpmat_coldegs m;

symbolic procedure hf!=whilb2hs(h,w);
% Converts the Hilbert series numerator h into a rational expression
% with denom = prod ( 1-w(x) | x in ringvars ) and cancels common
% factors. Uses gcdf and returns a s.q.
  begin scalar a,g,den,num;
  num:=numr simp dp_2a h;       % This is the numerator as a s.f.
  den:=1;
  for each x in ring_names cali!=basering do
  << a:=numr simp dp_2a hf!=whilbmon({mo_from_a x},w);
     g:=gcdf!*(num,a);
     num:=quotf(num,g); den:=multf(den,quotf(a,g));
  >>;
  return num ./ den;
  end;

symbolic procedure weightedhilbertseries!*(m,w);
% m must be a Gbasis.
  hf!=whilb2hs(hf_whilb(m,w),w);

symbolic procedure hf_whs_from_resolution(u,w);
% u must be a resolution.
  hf!=whilb2hs(hf_whilb3(u,w),w);

symbolic procedure hilbertseries!* m;
% m must be a Gbasis.
  weightedhilbertseries!*(m,{ring_ecart cali!=basering});

% --------- Multiplicity and dimension ---------------------

symbolic procedure hf_mult n;
% Get the sum of the coefficients of the s.f. (car n). For homogeneous
% ideals and "good" weight vectors this is the multiplicity.
   prepf absf hf!=sum_up car n;

symbolic procedure hf!=sum_up f;
   if numberp f then f else hf!=sum_up car subf(f,list (mvar f . 1));

symbolic procedure hf_dim f;
% Returns the dimension as the pole order at 1 of the HF f.
  if domainp denr f then 0
  else begin scalar g,x,d; integer n;
    f:=denr f; x:=mvar f; n:=0; d:=(((x.1).-1).1);
    while null cdr (g:=qremf(f,d)) do
        << n:=n+1; f:=car g >>;
    return n;
    end;

symbolic procedure degree!* m; hf_mult hilbertseries!* m;

% ------- Algebraic Mode Interface for weighted Hilbert series.

symbolic operator weightedhilbertseries;
symbolic procedure weightedhilbertseries(m,w);
% m must be a gbasis, w a list of weight lists.
  if !*mode='algebraic then
  begin scalar w1,l;
  w1:=for each x in cdr reval w collect cdr x;
  l:=length ring_names cali!=basering;
  for each x in w1 do
        if (not numberlistp x) or (length x neq l)
                then typerr(w,"weight list");
  m:=dpmat_from_a reval m;
  l:=mk!*sq weightedhilbertseries!*(m,w1);
  return l;
  end else weightedhilbertseries!*(m,w);

endmodule; % hf

end;
