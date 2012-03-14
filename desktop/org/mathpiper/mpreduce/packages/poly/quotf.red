module quotfx;

% Author:  Herbert Melenk.

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


Comment in many calls to QUOTF, the result is not checked for NIL
because the caller is sure there will be no remainder, e.g. if the
divisor is a gcd.  This occurs not only at several places in the REDUCE
kernel, but especially in Groebner, which simplifies polynomials with
parameters by dividing out their contents.

In all those cases, QUOTF computes too much: if you divide

   P= p_n x^n + p_n-1 x^(n-1) + ...
   Q= q_m x^m + q_m-1 x^(m-1) + ...
        (the coefficients may be polynomials in other variables)

the result comes only from the first k=(n-m+1) coefficients of P.  The
remaining terms only have influence on the potential remainder.  So it
is not necessary to execute the subtractions completely if we don't
need the remainder (or test for its absence).

You can stop after the the power x^(n-k) in P and x^(m-k) in Q, and in
the loop n down to m you can stop in Q again at each step depending on
the actual k.

The method is a polynomial extension of Jebelean's method for dividing
bignums where you know in advance they have no remainder.  The
resulting code is a modification of the standard QUOTF code in polrep;

symbolic procedure quotfx(u,v);
  if null !*exp or null !*mcd then quotf(u,v) else quotfx1(u,v);

symbolic procedure quotfx1(p,q);
   % P and Q are standard forms where Q divides P without remainder.
   % Value is the quotient of P and Q.
   if null p then quotfxerr(p,q)
    else if p=q then 1
    else if q=1 then p
    else if domainp q then quotfdx(p,q)
    else if domainp p then quotfxerr(p,q)
    else if mvar p eq mvar q
     then begin scalar f,dp,dq,u,v,w,x,y,z; integer n;
        w := mvar q;
        dq:=ldeg q;
    a:  if (dp:=ldeg p) <dq then return quotfxerr(p,q);
        u := lt!* p;
        v := lt!* q;
        w := mvar q;
        x := quotfx1(tc u,tc v);
        n := idifference(dp,dq);
        if n=0 then return rnconc(z,x);
        y := w .** n;
        if null f then p := cutf(p,w,isub1 idifference(dp,n));
        f:=t;
        q:=cutf(q,w,isub1 idifference(dq,n));
        p := addf(p,multf(if n=0 then q else multpf(y,q),negf x));
        if p and (domainp p or not(mvar p eq w)) then
             return quotfxerr(p,q);
        z := aconc!*(z,y .* x);
        if null p then return z;
        go to a
    end
    else if ordop(mvar p,mvar q) then quotkx(p,q)
    else quotfxerr(p,q);

symbolic procedure quotkx(p,q);
   (if w then if null red p then list(lpow p .* w)
               else (if y then lpow p .* w .+ y else nil)
                     where y=quotfx1(red p,q)
     else nil)
    where w=quotfx1(lc p,q);

symbolic procedure quotfdx(p,q);
   if p=q then 1
   else if flagp(dmode!*,'field) then divd(p,q)
   else if domainp p then quotdd(p,q)
   else quotkx(p,q);

symbolic procedure quotfxerr(u,v);
   rederr("exact division failed");

symbolic procedure cutf(u,x,n);
   % U is a standard form. Delete the terms with a degree in x below n.
   if ilessp(n,1) then u else cutf1(u,x,n);

symbolic procedure cutf1(u,x,n);
   if domainp u or mvar u neq x or ilessp(ldeg u,n) then nil else
    lt u .+ cutf1(red u,x,n);

endmodule;

end;
