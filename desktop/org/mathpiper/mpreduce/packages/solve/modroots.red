module modroots; % Roots of a univariate polynomial mod m,
                 % m not necessarily prime.

% Author: Herbert Melenk, ZIB Berlin.

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


% Algebraic interface: m_roots(polynomial, modulus);

symbolic procedure modroots0(f,m);
 % f: univariate standard form with modular coeffients,
 % m: positive integer modulus.
 % Algorithm: compute roots modulo the biggest factor of m
 % and lift these for the remaining factors. During lifing
 % the number of factors may change in both directions.
 begin scalar ml;
   ml := sort(for each q in zfactor m join
                for i:=1:cdr q collect car q,'lessp);
   return sort(modroots1(f,ml),'lessp);
 end;

symbolic procedure modroots1(f,ml);
   if null cdr ml then modroots2(f,car ml,nil) else
  begin scalar f1,p,q,pq,r,s,x,y;
    p:=car ml; ml:=cdr ml;
    r := modroots1(f,ml);
    if null r then return nil;
    x:=mvar f; y:=gensym();
    q:=for each m in ml product m;
    pq:=p*q;
     % lift roots to p*q:
     %   if f(r)=0 mod q, solve f(n*q+r)=0 mod p.
    for each w in r do
    <<f1:=numr subf(f,{x . {'plus,{'times,y,q},w}});
      for each y in modroots2(reduce!-mod!-p!*(f1,p),p,t)
        do <<y:= modp(y*q+w,pq);
             if null reduce!-mod!-p!*(numr subf(f,{x . y}),pq)
               and not member(y,s) then s:=y.s>>;
    >>;
    return s;
  end;

symbolic procedure modroots2(f,p,rec);
  if domainp f and f then nil
  else if null f then
    if p=2 and rec then '(-1 0 1)
      else for i:=0:(p-1) collect i
  else if p=2 then modroots4(f,t,rec)
  else modroots3(f,p);

symbolic procedure x!*!*p!-w(x,p,w);
  % Make a form x^p - w mod p.
    general!-difference!-mod!-p(x .** p .*1 .+ nil,w);

symbolic procedure modroots3(f,current!-modulus);
 % Roots of a polynomial f mod p, p prime.
 % Algorithm:
 % H. Cohen: Computational Algebraic Number theory, 1.6.1
 begin scalar a,d,p,r,x;  integer n;

     % From now on, we compute with untagged modular coefficients
     % using the routines in "factor/modpoly".
   p := current!-modulus;
   f := general!-reduce!-mod!-p f;
   x := mvar f;

     % gcd(f, x^p - x)
   a := general!-gcd!-mod!-p(f , x!*!*p!-w(x,p,!*k2f x));
   d := ldeg a;
   n := lowestdeg(a,x,0);
   if n>0 then
   <<r:='(0); a:=general!-quotient!-mod!-p(a,x!*!*p!-w(x,n,nil))>>;
   return append(r,modroots31(a,x,p));
 end;

symbolic procedure modroots31(a,x,p);
  begin scalar a0,a1,a2,b,d,e,s,w;

  s2:
   if domainp a then return nil;
   if ldeg a = 1 then
      return {general!-modular!-quotient(
                 if red a then general!-modular!-minus red a else 0,
                 lc a)};
   if ldeg a = 2 then
   <<
     a2:=lc a; a:=red a;
     if not domainp a then
     <<a1:= lc a; a:=red a>> else a1:=0;
     a0:=if null a then 0 else a;
     d:=general!-modular!-difference(
           general!-modular!-times(a1,a1),
           general!-modular!-times(4,general!-modular!-times(a0,a2)));
     s:=legendre!-symbol(d,p);
     if s=-1 then return nil;
     e:= modsqrt(d,p);
     a2:=general!-modular!-reciprocal general!-modular!-plus(a2,a2);
     a1:=general!-modular!-minus a1;
     return
      {general!-modular!-times(general!-modular!-plus(a1,e),a2),
       general!-modular!-times(general!-modular!-difference(a1,e),a2)};
   >>;

   s3:
    e:=random(p);
      % compute gcd[x ^((p-1)/2) - 1, A(x - e)]
    w:=x!*!*p!-w(x,(p-1)/2,1);
    a1:=general!-reduce!-mod!-p numr subf(a,{x.{'difference,x,e}});
    b:=general!-gcd!-mod!-p(w,a1);
    if domainp b or ldeg b = ldeg a then go to s3;

   s4:
     % Compute both root groups and transform roots back to x - e;
    return
    for each w in union(modroots31(general!-quotient!-mod!-p(a1,b),x,p),
                        modroots31(b,x,p))
       collect general!-modular!-difference(w,e)
 end;

symbolic procedure modroots4(f,w,rec);
  % roots of f mod 2: count terms.
  if domainp f then
  <<
   if f then w:=not w;
   append(
    if null f then '(0),
    if w then (if rec then '(-1 1) else '(1))
         )
  >>
  else modroots4(red f,not w,rec);

put('m_roots,'psopfn,
      function(lambda(u);
               'list . modroots0(numr simp car u,reval cadr u)));

endmodule;

end;
