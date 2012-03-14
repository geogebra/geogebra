module jpatches;   % Routines for manipulating sf's with power folding.

% Author: James H. Davenport.

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


fluid '(!*noncomp sqrtflag);

exports !*addsq,!*multsq,!*invsq,!*multf,!*exptsq,!*exptf; %squashsqrtsq

% !*MULTF(A,B) multiplies the polynomials (standard forms) U and V
% in much the same way as MULTF(U,V) would, EXCEPT...
%     (1) !*MULTF inhibits the action of OFF EXP and of non-commutative
%         multiplications
%     (2) Within !*MULTF powers of square roots, and powers of
%         exponential kernels are reduced as if substitution rules
%         such as FOR ALL X LET SQRT(X)**2=X were being applied;

% Note that !*MULTF comes between MULTF and !*Q2F SUBS2F MULTF in its
% behaviour, and that it is the responsibility of the user to call it
% in sensible places where its services are needed.

% Similarly for the other functions defined here.


%symbolic procedure !*addsq(u,v);
   %U and V are standard quotients.
%  %Value is canonical sum of U and V;
%   if null numr u then v
%    else if null numr v then u
%    else if denr u=1 and denr v=1 then addf(numr u,numr v) ./ 1
%    else begin scalar nu,du,nv,dv,x;
%        x := gcdf(du:=denr u,dv:=denr v);
%        du:=quotf(du,x); dv:=quotf(dv,x);
%        nu:=numr u; nv:=numr v;
%        u:=addf(!*multf(nu,dv),!*multf(nv,du));
%        if u=nil then return nil ./ 1;
%        v:=!*multf(du,denr v);
%        return !*ff2sq(u,v)
%    end;

%symbolic procedure !*multsq(a,b);
%  begin
%    scalar n,d;
%    n:=!*multf(numr a,numr b);
%    d:=!*multf(denr a,denr b);
%    return !*ff2sq(n,d)
%  end;

%symbolic procedure !*ff2sq(a,b);
%  begin
%    scalar gg;
%    if null a then return nil ./ 1;
%    gg:=gcdf(a,b);
%    if not (gg=1) then <<
%        a:=quotf(a,gg);
%        b:=quotf(b,gg) >>;
%    if minusf b then <<
%        a:=negf a;
%        b:=negf b >>;
%    return a ./ b
%  end;

symbolic procedure !*addsq(u,v);
   %U and V are standard quotients.
   %Value is canonical sum of U and V;
   if null numr u then v
    else if null numr v then u
    else if denr u=1 and denr v=1 then addf(numr u,numr v) ./ 1
    else begin scalar du,dv,x,y,z;
        x := gcdf(du:=denr u,dv:=denr v);
        du:=quotf(du,x); dv:=quotf(dv,x);
        y:=addf(!*multf(dv,numr u),!*multf(du,numr v));
        if null y then return nil ./ 1;
        z:=!*multf(denr u,dv);
        if minusf z then <<y := negf y; z := negf z>>;
        % In this case (as opposed to ADDSQ), Y and Z may have
        % developed common factors from SQRT expansion, so a
        % gcd of Y and Z is needed.
        x := gcdf(y,z);
        return if x=1 then y ./ z else quotf(y,x) ./ quotf(z,x)
    end;

symbolic procedure !*multsq(u,v);
   %U and V are standard quotients. Result is the canonical product of
   %U and V with surd powers suitably reduced.
   if null numr u or null numr v then nil ./ 1
    else if denr u=1 and denr v=1 then !*multf(numr u,numr v) ./ 1
    else begin scalar w,x,y;
     x := gcdf(numr u,denr v);
     y := gcdf(numr v,denr u);
     w := !*multf(quotf(numr u,x),quotf(numr v,y));
     x := !*multf(quotf(denr u,y),quotf(denr v,x));
     if minusf x then <<w := negf w; x := negf x>>;
     y := gcdf(w,x);  % another factor may have been generated.
     return if y=1 then w ./ x else quotf(w,y) ./ quotf(x,y)
    end;

symbolic procedure !*invsq a;
   % Note that several examples (e.g., int(1/(x**8+1),x)) give a more
   % compact result when SQRTFLAG is true if SQRT2TOP is not called.
   if sqrtflag then sqrt2top invsq a else invsq a;

symbolic procedure !*multf(u,v);
  % U and V are standard forms
  % Value is SF for U*V;
  begin scalar x,y;
      if null u or null v then return nil
      else if u=1 then return squashsqrt v
        else if v=1 then return squashsqrt u
        else if domainp u then return multd(u,squashsqrt v)
        else if domainp v then return multd(v,squashsqrt u)
        else if !*noncomp then return multf(u,v);
      x:=mvar u;
      y:=mvar v;
      if x eq y then go to c else if ordop(x,y) then go to b;
      x:=!*multf(u,lc v);
      y:=!*multf(u,red v);
      return if null x then y
              else if not domainp lc v
                and mvar u eq mvar lc v
                and not atom mvar u
                and car mvar u memq '(expt sqrt)
               then addf(!*multf(x,!*p2f lpow v),y)
              else makeupsf(lpow v,x,y);
  b:  x:=!*multf(lc u,v);
      y:=!*multf(red u,v);
      return if null x then y
              else if not domainp lc u
                and mvar lc u eq mvar v
                and not atom mvar v
                and car mvar v memq '(expt sqrt)
               then addf(!*multf(!*p2f lpow u,x),y)
              else makeupsf(lpow u,x,y);
  c:  y:=addf(!*multf(list lt u,red v),!*multf(red u,v));
      if eqcar(x,'sqrt)
        then return addf(squashsqrt y,!*multfsqrt(x,
                        !*multf(lc u,lc v),ldeg u + ldeg v))
        else if eqcar(x,'expt) and prefix!-rational!-numberp caddr x
          then return addf(squashsqrt y,!*multfexpt(x,
                        !*multf(lc u,lc v),ldeg u + ldeg v));
      x:=mkspm(x,ldeg u + ldeg v);
      return if null x or null (u:=!*multf(lc u,lc v))
               then y
               else addf(multpf(x,u),y)
    end;

symbolic procedure makeupsf(u,x,y);
% Makes u .* x .+ y  except when u is not a valid lpow (because of
% sqrts).
   if atom car u or cdr u = 1 then addf(multpf(u,x),y)
     else if caar u eq 'sqrt then addf(!*multfsqrt(car u,x,cdr u),y)
       else if <<begin scalar v;
                    v:=car u;
                    if car v neq 'expt then return nil;
                    v:=caddr v;
                    if atom v then return nil;
                    return (car v eq 'quotient
                            and numberp cadr v
                            and numberp caddr v)
                 end >>
         then addf(!*multfexpt(car u,x,cdr u),y)
         else addf(multpf(u,x),y);


symbolic procedure !*multfsqrt(x,u,w);
   % This code (Due to Norman a& Davenport) squashes SQRT(...)**2.
   begin scalar v;
     w:=divide(w,2);
     v:=!*q2f simp cadr x;
     u:=!*multf(u,exptf(v,car w));
     if cdr w neq 0 then u:=!*multf(u,!*p2f mksp(x,1));
     return u
   end;


symbolic procedure !*multfexpt(x,u,w);
  begin scalar expon,v;
    expon:=caddr x;
    x:=cadr x;
    w:=w * cadr expon;
    expon:=caddr expon;
    v:=gcdn(w,expon);
    w:=w/v;
    v:=expon/v;
    if not (w > 0) then rerror(int,8,"Invalid exponent")
     else if v = 1
      then return !*multf(u,exptf(if numberp x then x
                                    else if atom x then !*k2f x
                                    else !*q2f if car x eq '!*sq
                                                 then argof x
                                                else simp x,
                          w));
    expon:=0;
    while not (w < v) do <<expon:=expon + 1; w:=w-v>>;
    if expon>0 then u:=!*multf(u,exptf(!*q2f simp x,expon));
    if w = 0 then return u;
    x:=list('expt,x,list('quotient,1,v));
    return multf(squashsqrt u,!*p2f mksp(x,w))   % Cannot be *MULTF.
  end;

symbolic procedure prefix!-rational!-numberp u;
  % Tests for m/n in prefix representation.
    eqcar(u,'quotient) and numberp cadr u and numberp caddr u;

% symbolic procedure squashsqrtsq sq;
%    !*multsq(squashsqrt numr sq ./ 1,1 ./ squashsqrt denr sq);

symbolic procedure squashsqrt sf;
if (not sqrtflag) or atom sf or atom mvar sf
  then sf
  else if car mvar sf eq 'sqrt and ldeg sf > 1
    then addf(squashsqrt red sf,!*multfsqrt(mvar sf,lc sf,ldeg sf))
    else if car mvar sf eq 'expt
       and prefix!-rational!-numberp caddr mvar sf
       and ldeg sf >= caddr caddr mvar sf
      then addf(squashsqrt red sf,!*multfexpt(mvar sf,lc sf,ldeg sf))
      else (if null x then squashsqrt red sf
            else lpow sf .* x .+ squashsqrt red sf)
           where x = squashsqrt lc sf;

%remd 'simpx1;

% The following definition requires frlis!* declared global.

%symbolic procedure simpx1(u,m,n);
%   %u,m and n are prefix expressions;
%   %value is the standard quotient expression for u**(m/n);
%   begin scalar flg,z;
%        if null frlis!* or null intersection(frlis!*,flatten (m . n))
%          then go to a;
%        exptp!* := t;
%        return !*k2q list('expt,u,if n=1 then m
%                                   else list('quotient,m,n));
%    a:  if numberp m and fixp m then go to e
%         else if atom m then go to b
%         else if car m eq 'minus then go to mns
%         else if car m eq 'plus then go to pls
%         else if car m eq 'times and numberp cadr m and fixp cadr m
%                and numberp n
%          then go to tms;
%    b:  z := 1;
%    c:  if atom u and not numberp u then flag(list u,'used!*);
%        u := list('expt,u,if n=1 then m else list('quotient,m,n));
%        if not(u member exptl!*) then exptl!* := u . exptl!*;
%    d:  return mksq(u,if flg then -z else z); %u is already in lowest
%%       %terms;
%    e:  if numberp n and fixp n then go to int;
%        z := m;
%        m := 1;
%        go to c;
%    mns: m := cadr m;
%        if !*mcd then return invsq simpx1(u,m,n);
%        flg := not flg;
%        go to a;
%    pls: z := 1 ./ 1;
%    pl1: m := cdr m;
%        if null m then return z;
%        z := multsq(simpexpt list(u,
%                        list('quotient,if flg then list('minus,car m)
%                                        else car m,n)),
%                    z);
%        go to pl1;
%    tms: z := gcdn(n,cadr m);
%        n := n/z;
%        z := cadr m/z;
%        m := retimes cddr m;
%        go to c;
%    int:z := divide(m,n);
%        if cdr z<0 then z:= (car z - 1) . (cdr z+n);
%        if 0 = cdr z
%          then return simpexpt list(u,car z);
%        if n = 2
%          then return multsq(simpexpt list(u,car z),
%                             simpsqrti u);
%        return multsq(simpexpt list(u,car z),
%                        mksq(list('expt,u,list('quotient,1,n)),cdr z))
%   end;

symbolic procedure !*exptsq(a,n);
% Raises A to the power N using !*MULTSQ.
    if n=0 then 1 ./ 1
    else if n=1 then a
    else if n<0 then !*exptsq(invsq a,-n)
    else begin
      scalar q,r;
      q:=divide(n,2);
      r:=cdr q; q:=car q;
      q:=!*exptsq(!*multsq(a,a),q);
      if r=0 then return q
      else return !*multsq(a,q)
    end;


symbolic procedure !*exptf(a,n);
% Raises A to the power N using !*MULTF.
    if n=0 then 1
    else if n=1 then a
    else begin
      scalar q,r;
      q:=divide(n,2);
      r:=cdr q; q:=car q;
      q:=!*exptf(!*multf(a,a),q);
      if r=0 then return q
      else return !*multf(a,q)
    end;

endmodule;

end;
