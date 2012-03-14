module heugcd;

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


%Authors: James Davenport & Julian Padget

% For full details of the algorithms see first Char et al. from the
% Proceedings of EUROSAM 84 (Springer LNCS #174), then Davenport and
% Padget in the Proceedings of EUROCAL 85 (Springer LNCS #204) and
% Davenport and Padget in the proceedings of Calcul Formel (Homage a
% Noel Gastinel) published by Masson-Wiley (France).

%exports heu!-gcd, heu!-gcd!-list;

%imports to be determined

%internal-functions
%       univariatep, univariatep1, htc, kontent, kontent1,
%       horner!-eval!-rat, horner!-eval!-rat!-and!-gcdl,
%       horner!-eval!-rat!-and!-gcdl1, heu!-quotfl, heu!-quotfl1,
%       heu!-quotf, xceiling, next!-even!-value, next!-odd!-value,
%       heu!-gcdl, analyse!-polynomials, negshiftz, gen!-poly,
%       gen!-poly!-forward, gen!-poly!-backward, gcdlist2, gcdf2

fluid '(!*heugcd reduction!-count);

global '(ee);


% ****************** Various polynomial utilities **********************

symbolic smacro procedure univariatep p; univariatep1(p,mvar p);

symbolic procedure univariatep1(p,v);
% checks that p is univariate in v;
   if atom p then t
   else if mvar p neq v then nil
   else if atom lc p then univariatep1(red p,v)
   else nil;

symbolic procedure htc p;
   if atom p then p
   else if null red p then lc p
   else htc red p;

symbolic procedure kontent p;
% extract integer content of polynomial p
   if domainp p then
           if numberp p then p
      else if null p then 1
      else rederr "HEUGCD(kontent): unsupported domain element"
   else if domainp red p then
           if numberp red p then gcdn(lc p,red p)
      else if null red p then lc p
      else rederr "HEUGCD(kontent): unsupported domain element"
  else kontent1(red red p,gcdn(lc p,lc red p));

symbolic procedure kontent1(p,a);
   if a=1 then 1
   else if domainp p then
      if numberp p then gcdn(p,a)
      else if null p then a
      else rederr "HEUGCD(kontent1): unsupported domain element"
   else kontent1(red p,gcdn(remainder(lc p,a),a));

symbolic procedure horner!-eval!-rat(p,v);
% evaluate the (sparse univariate) polynomial p at a rational v using
% Horner's scheme.  Denominators are cleared by in fact calculating the
% following:
%
%    for i:=min:max sum (a[i] * n**(i-min) * d**(max-min-i))
%
% note that if the polynomial does not end in a non-zero constant
% the routine it return the evaluation of p/(trailing exponent)
%        s accumulates d**(max-min-i)
%        ans accumulates the sum
%        m is degree difference between current and previous term
% See specific routines below for further detail
   if (numr v)=1 then horner!-eval!-integer(p,denr v,1,0)
   else if (denr v)=1 then horner!-eval!-reciprocal(p,numr v,0,0)
   else horner!-eval!-rational(p,numr v,denr v,0,1,0);

symbolic procedure horner!-eval!-rational(p,n,d,m,s,ans);
% general case of an arbitrary rational
   if domainp p then
      if p then ans*n**m+s*p else ans
   else (lambda mp;
           horner!-eval!-rational(red p,n,d,mp,s*d**mp,ans*n**m+s*lc p))
         (ldeg p)-(if domainp red p then 0 else ldeg red p);

symbolic procedure horner!-eval!-integer(p,d,s,ans);
% simple sub case of an integer (n/1)
   if domainp p then
      if p then ans+s*p else ans
   else horner!-eval!-integer(red p,d,
           s*d**((ldeg p)-(if domainp red p then 0 else ldeg red p)),
           ans+s*lc p);

symbolic procedure horner!-eval!-reciprocal(p,n,m,ans);
% simpler sub case of a straight reciprocal of an integer (1/n)
   if domainp p then
      if p then ans*n**m+p else ans
   else horner!-eval!-reciprocal(red p,n,
           (ldeg p)-(if domainp red p then 0 else ldeg red p),
           ans*n**m+lc p);

symbolic procedure horner!-eval!-rat!-and!-gcdl(l,v);
% l is a list of polynomials to be evaluated at the point v
% and then take the GCD of these evaluations.  We use an auxiliary
% routine with an accumulator variable to make the computation
% tail-recursive
   if null cdr l then horner!-eval!-rat(car l,v)
   else if null cddr l then
      gcdn(horner!-eval!-rat(car l,v),horner!-eval!-rat(cadr l,v))
   else horner!-eval!-rat!-and!-gcdl1(cddr l,v,
          gcdn(horner!-eval!-rat(car l,v),horner!-eval!-rat(cadr l,v)));

symbolic procedure horner!-eval!-rat!-and!-gcdl1(l,v,a);
   if a=1 then 1
   else if null l then a
   else horner!-eval!-rat!-and!-gcdl1(cdr l,v,
           gcdn(horner!-eval!-rat(car l,v),a));

%*********** Polynomial division utilities and extensions *************

symbolic procedure heu!-quotfl(l,d);
% test division of each of a list of SF's (l) by the SF d
   if null cdr l then heu!-quotf(car l,d)
   else heu!-quotfl1(cdr l,d,heu!-quotf(car l,d));

symbolic procedure heu!-quotfl1(l,d,flag);
   if null flag then nil
   else if null cdr l then heu!-quotf(car l,d)
   else heu!-quotfl1(cdr l,d,heu!-quotf(car l,d));

symbolic procedure heu!-quotf(p,q);
   if domainp q then
      if domainp p then
         if null p then nil
         else if null q then
            rederr "HEUGCD(heu-quotf): division by zero"
         else (lambda temp; if cdr temp=0 then car temp else nil)
               divide(p,q)
      else quotf(p,q)
   else if domainp p then nil
   else if ldeg p<ldeg q then nil
   else if (cdr divide(lc p,lc q)) neq 0 then nil
   else if p=q then 1
   else (lambda qv;
           if qv=0 then quotf(p,q)
           else if remainder(horner!-eval!-rat(p,'(2 . 1)),qv)=0 then
              quotf(p,q)
           else nil)
         horner!-eval!-rat(q,'(2 . 1));

%****************** Z-adic polynomial GCD routines ********************

symbolic smacro procedure xceiling(n,d); (n+d-1)/d;

symbolic smacro procedure force!-even x;
   if evenp x then x else x+1;

symbolic smacro procedure force!-odd x;
   if evenp x then x+1 else x;

symbolic smacro procedure next!-even!-value x;
   if (denr x)=1 then force!-even fix(numr x * ee) ./ 1
    else 1 ./ force!-even fix(denr x * ee);

symbolic smacro procedure next!-odd!-value x;
   if (denr x)=1 then force!-odd fix(numr x * ee) ./ 1
    else 1 ./ force!-odd fix(denr x * ee);

symbolic smacro procedure first!-value(inp,inq,lcp,lcq,tcp,tcq);
   % Initial evaluation is based on Cauchy's inequality.
   if lcp<tcp then
      if lcq<tcq then
         if (inp*tcq)<(inq*tcp) then 1 . (2+2*xceiling(inp,tcp))
         else 1 . (2+2*xceiling(inq,tcq))
      else if (inp*lcq)<(inq*tcp) then 1 . (2+2*xceiling(inp,tcp))
         else (2+2*xceiling(inq,lcq)) . 1
   else if lcq<tcq then
         if (inp*tcq)<(inq*lcp) then (2+2*xceiling(inp,lcp)) . 1
         else 1 . (2+2*xceiling(inq,tcq))
      else if (inp*lcq)<(inq*lcp) then (2+2*xceiling(inp,lcp)) . 1
         else (2+2*xceiling(inq,lcq)) . 1;

symbolic smacro procedure
   second!-value(inp,inq,lcp,lcq,lgcd,tcp,tcq,tgcd);
   % The second evaluation point is from a modified Mignotte bound.
   (lambda (inp,inq,lcp,lcq,tcp,tcq);
      if lcp<tcp then
         if lcq<tcq then
            if (inp*tcq)<(inq*tcp) then
               1 . force!-even (2+xceiling(inp,tcp))
            else
               1 . force!-even (2+xceiling(inq,tcq))
         else if (inp*lcq)<(inq*tcp) then
                 1 . force!-even (2+xceiling(inp,tcp))
            else force!-even (2+xceiling(inq,lcq)) . 1
      else if lcq<tcq then
            if (inp*tcq)<(inq*lcp) then
               force!-even (2+xceiling(inp,lcp)) . 1
            else 1 . force!-even (2+xceiling(inq,tcq))
         else if (inp*lcq)<(inq*lcp) then
            force!-even (2+xceiling(inp,lcp)) . 1
         else force!-even (2+xceiling(inq,lcq)) . 1)
    (inp,inq,max(2,lcp/lgcd),max(2,lcq/lgcd),
             max(2,tcp/tgcd),max(2,tcq/tgcd));

symbolic procedure heu!-gcd!-list l;
   if null cdr l then car l
   else if null cddr l then heu!-gcd(car l,cadr l)
   else heu!-gcdl sort(l,function (lambda (p1,p2); ldeg p1<ldeg p2));

symbolic procedure heu!-gcdl l;
% Heuristic univariate polynomial GCD after Davenport & Padgets'
% extensions of Geddes' algorithm (EUROSAM 84) for a list of polynomials
begin scalar k,value,dval,d,xsx,inp,inq,lcp,lcq,lgcd,tcp,tcq,tgcd,tmp;
      % check if first one is linear (input is sorted by leading degree)
   if (ldeg car l=1) then return
      (lambda pcarl; if heu!-quotfl(cdr l,pcarl) then pcarl else 1)
      quotf(car l,kontent car l);
      % general case - compute GCD of all of them at Cauchy's bound
   tmp:=analyse!-polynomial car l;
   if tmp then <<
      inp:=car tmp; lcp:=lc car l; xsx:=cadr tmp; tcp:=caddr tmp;
      tmp:=analyse!-polynomial cadr l;
      if tmp then <<
         inq:=car tmp; lcq:=lc cadr l;
         xsx:=min(xsx,cadr tmp); tcq:=caddr tmp>>
      else return nil>>
   else return nil;
   value:=first!-value(inp,inq,lcp,lcq,tcp,tcq);
      % first check for trivial GCD
   d:=gen!-poly(horner!-eval!-rat!-and!-gcdl(l,value),
                value,mvar car l,xsx);
   if heu!-quotfl(l,d) then return d;
      % since that failed we pick a much higher evaluation point
      % courtesy of a modified Mignotte inequality and just work on the
      % first two
   lgcd:=gcdn(lcp,lcq);
   for each x in cddr l do lgcd:=gcdn(lc x,lgcd);
   tgcd:=gcdn(tcp,tcq);
   for each x in cddr l do tgcd:=gcdn(htc x,tgcd);
   value:=second!-value(inp,inq,lcp,lcq,lgcd,tcp,tcq,tgcd);
loop:
   d:=gen!-poly(horner!-eval!-rat!-and!-gcdl(l,value),
                value,mvar car l,xsx);
   if heu!-quotfl(l,d) then return d;
   value:=next!-odd!-value value;
   k:=k+1;
   d:=gen!-poly(horner!-eval!-rat!-and!-gcdl(l,value),
                value,mvar car l,xsx);
   if heu!-quotfl(l,d) then return d;
   value:=next!-even!-value value;
   k:=k+1;
   if k < 10 then goto loop;
   print "(HEUGCD):heu-gcd-list fails";
   return nil
end;

symbolic procedure heu!-gcd(p,q);
% Heuristic univariate polynomial GCD after Davenport & Padgets'
% extensions of Geddes' algorithm (EUROSAM 84)
% the method of choosing the evaluation point is quite complex (but not
% as general as it ought to be).  It is
%
%    min(infinity!-norm p/lc p, infinity!-norm p/htc p,
%        infinity!-norm q/lc q, infinity!-norm q/htc q)
%
begin scalar k,value,d,dval,xsx,inp,inq,lcp,lcq,lgcd,tcp,tcq,tgcd,tmp;
      % check if one of p and q is linear
   if (ldeg q=1) or (ldeg p=1) then return
      if univariatep p and univariatep q then
         (lambda (pp,pq);
           if (ldeg pq)=1 then
              (lambda h; if null h then 1 else pq) heu!-quotf(pp,pq)
           else
              (lambda h; if null h then 1 else pp) heu!-quotf(pq,pp))
          (quotf(p, kontent p), quotf(q, kontent q))
      else nil;
      % general case
   if (ldeg p)>(ldeg q) then return heu!-gcd(q,p);
   tmp:=analyse!-polynomial p;
   if tmp then <<
      inp:=car tmp; lcp:=lc p; xsx:=cadr tmp; tcp:=caddr tmp;
      tmp:=analyse!-polynomial q;
      if tmp then <<
         inq:=car tmp; lcq:=lc q; xsx:=min(xsx,cadr tmp); tcq:=caddr tmp>>
      else return nil>>
   else return nil;
   value:=first!-value(inp,inq,lcp,lcq,tcp,tcq);
      % first check for trivial GCD
   dval:=gcdn(horner!-eval!-rat(p,value),horner!-eval!-rat(q,value));
   d:=gen!-poly(dval,value,mvar p,xsx);
   if heu!-quotf(p,d) and heu!-quotf(q,d) then return d;
      % if that failed we pick a much higher evaluation point
   lgcd:=gcdn(lcp,lcq);
   tgcd:=gcdn(lcp,lcq);
   value:=second!-value(inp,inq,lcp,lcq,lgcd,tcp,tcq,tgcd);
   k:=0;
loop:
   dval:=gcdn(horner!-eval!-rat(p,value),horner!-eval!-rat(q,value));
   d:=gen!-poly(dval,value,mvar p,xsx);
   if heu!-quotf(p,d) and heu!-quotf(q,d) then return d;
   value:=next!-odd!-value value;
   k:=k+1;
   dval:=gcdn(horner!-eval!-rat(p,value),horner!-eval!-rat(q,value));
   d:=gen!-poly(dval,value,mvar p,xsx);
   if heu!-quotf(p,d) and heu!-quotf(q,d) then return d;
   value:=next!-even!-value value;
   k:=k+1;
   if k < 10 then goto loop;
   print "(HEUGCD):heu-gcd fails";
   return nil
end;

symbolic procedure analyse!-polynomial p;
% Determine the infinity norm of p and take note of the trailing
% coefficient, simultaneously check that p is univariate and take note
% of any trailing powers of the main variable. The result is a triple
% of (infinity-norm,excess powers,trailing coefficient)
   analyse!-polynomial1(p,1,lc p,0,mvar p);

symbolic procedure analyse!-polynomial1 (p,inp,tcp,xsxp,mvarp);
   if domainp p then
      if p then list(max(inp,abs p),0,abs p)
      else list(inp,xsxp,abs tcp)
   else if ((mvar p) neq mvarp) then nil
   else if domainp lc p then
      analyse!-polynomial1(red p,max(inp,abs lc p),lc p,ldeg p,mvarp)
   else nil;

%********** Reconstruction from the Z-adic representation *************

% given a number in [0,modulus), return the equivalent
% member of [-modulus/2,modulus/2)
% LAMBDA to ensure only one evaluation of arguments;
symbolic smacro procedure negshiftz(n,modulus);
   (lambda (nn,mmodulus);
      if nn>quotient(mmodulus,2) then nn-mmodulus else nn)
    (n,modulus);

symbolic procedure gen!-poly(dval,value,var,xsx);
   if (numr value)=1 then gen!-poly!-backward(dval,denr value,var,xsx)
   else if (denr value)=1 then gen!-poly!-forward(dval,numr value,var,xsx)
   else rederr "HEUGCD(gen-poly):point must be integral or reciprocal";

symbolic procedure gen!-poly!-forward(dval,value,var,xsx);
% generate a new polynomial in var from the value-adic representation
% provided by dval
begin scalar i,d,val,val1,kont;
   kont:=0;
   val:=dval;
   i:=xsx;
   if zerop i then <<
      % an x**0 term is represented specially;
      val1:=negshiftz(remainder(val,value),value);
      if not zerop val1 then kont:=d:=val1;
      val:=quotient(val-val1,value);
      i:=1
   >>;
   while not zerop val do <<
      val1:=negshiftz(remainder(val,value),value);
      if not zerop val1 then <<
         kont:=gcdn(val1,kont);
         d:=var .** i .* val1 .+ d
      >>;
      val:=quotient(val-val1,value);
      i:=1+i
   >>;
   return quotf(d,kont)
end;

symbolic procedure gen!-poly!-backward(dval,value,var,xsx);
% generate a new polynomial in var from the 1/value-adic representation
% provided by dval
begin scalar i,d,ans,val,val1,kont;
   kont:=0;
   val:=dval;
   % because we are at the 1/value representation
   % we need the implicit REVERSE that the two-loop strategy here
   % provides;
   while not zerop val do <<
      val1:=negshiftz(remainder(val,value),value);
      d:=val1 . d;
      val:=quotient(val-val1,value)
   >>;
   i:=xsx;
   if (zerop i and not zerop car d) then <<
      % Handle x**0 term specially;
      kont:=ans:=car d;
      d:=cdr d;
      i:=1
   >>;
   while d do <<
      if not zerop car d then <<
         kont:=gcdn(car d,kont);
         ans:= var .** i .* car d .+ ans
      >>;
      d:=cdr d;
      i:=i+1
   >>;
   return quotf(ans,kont)
end;

endmodule;

end;
