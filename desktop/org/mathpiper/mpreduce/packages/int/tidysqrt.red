module tidysqrt;  % General tidying up of square roots.

% Authors: Mary Ann Moore and Arthur C. Norman.
% Modifications by J.H. Davenport.

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


exports sqrt2top,tidysqrt;

%symbolic procedure tidysqrtdf a;
%    if null a then nil
%    else begin    scalar tt,r;
%        tt:=tidysqrt lc a;
%        r:=tidysqrtdf red a;
%        if null numr tt then return r;
%        return ((lpow a) .* tt) .+ r
%    end;
%
symbolic procedure tidysqrt q;
    begin    scalar n1,dd;
        n1:=tidysqrtf numr q;
        if null n1 then return nil ./ 1; %answer is zero.
        dd:=tidysqrtf denr q;
        return multsq(n1,invsq dd)
    end;

symbolic procedure tidysqrtf p;
%Input - standard form.
%Output - standard quotient.
%Simplifies sqrt(a)**n with n>1.
    if domainp p then p ./ 1
    else begin    scalar v,w;
        v:=lpow p;
        if car v='i then v:=mksp('(sqrt -1),cdr v); %I->sqrt(-1);
        if eqcar(car v,'sqrt) and not onep cdr v
          then begin    scalar x;
             %here we have a reduction to apply.
            x:=divide(cdr v,2); %halve exponent.
            w:=exptsq(simp cadar v,car x); %rational part of answer.
            if cdr x neq 0
              then w := multsq(w,((mksp(car v,1) .* 1) .+ nil) ./ 1);
            %the next line allows for the horrors of nested sqrts.
            w:=tidysqrt w
            end
        else w:=((v .* 1) .+ nil) ./ 1;
        v:=multsq(w,tidysqrtf lc p);
        return addsq(v,tidysqrtf red p)
    end;


symbolic procedure multoutdenr q;
  % Move sqrts in a sq to the numerator.
    begin  scalar n,d,root,conj;
        n:=numr q;
        d:=denr q;
        while (root:=findsquareroot d) do <<
          conj:=conjugatewrt(d,root);
          n:=!*multf(n,conj);
          d:=!*multf(d,conj) >>;
        while (root:=findnthroot d) do <<
          conj:=conjugateexpt(d,root,kord!*);
          n:=!*multf(n,conj);
          d:=!*multf(d,conj) >>;
        return (n . d);
        end;

symbolic procedure conjugateexpt(d,root,kord!*);
  begin scalar ord,ans,repl,xi;
  ord:=caddr caddr root; % the denominator of the exponent;
  ans:=1;
  kord!*:= (xi:=gensym()) . kord!*;
  % XI is an ORD'th root of unity;
  for i:=1:ord-1 do <<
    ans:=!*multf(ans,numr subf(d,
                   list(root . list('times,root,list('explt,xi,i)))));
    while (mvar ans eq xi) and ldeg ans > ord do
      ans:=addf(red ans,(xi) to (ldeg ans - ord) .* lc ans .+ nil);
    if (mvar ans eq xi) and ldeg ans = ord then
      ans:=addf(red ans,lc ans) >>;
  if (mvar ans eq xi) and ldeg ans = ord-1 then <<
    repl:=-1;
    for i:=1:ord-2 do
      repl:=(xi) to i .* -1 .+ repl;
    ans:=addf(red ans,!*multf(lc ans,repl)) >>;
  if not domainp ans and mvar ans eq xi
    then interr "Conjugation failure";
  return ans;
  end;

symbolic procedure sqrt2top q;
begin
  scalar n,d;
  n:=multoutdenr q;
  d:=denr n;
  n:=numr n;
  if d eq denr q
    then return q;%no change.
  if d iequal 1
    then return (n ./ 1);
  q:=gcdcoeffsofsqrts n;
  if q iequal 1
    then if minusf d
      then return (negf n ./ negf d)
      else return (n ./ d);
  q:=gcdf(q,d);
  n:=quotf(n,q);
  d:=quotf(d,q);
  if minusf d
    then return (negf n ./ negf d)
    else return (n ./ d)
    end;

%symbolic procedure denrsqrt2top q;
%begin
%  scalar n,d;
%  n:=multoutdenr q;
%  d:=denr n;
%  n:=numr n;
%  if d eq denr q
%    then return d; % no changes;
%  if d iequal 1
%    then return 1;
%  q:=gcdcoeffsofsqrts n;
%  if q iequal 1
%    then return d;
%  q:=gcdf(q,d);
%  if q iequal 1
%    then return d
%    else return quotf(d,q)
%  end;

symbolic procedure findsquareroot p;
  % Locate a sqrt symbol in poly p.
    if domainp p then nil
    else begin scalar w;
        w:=mvar p; %check main var first.
        if atom w
          then return nil; %we have passed all sqrts.
        if eqcar(w,'sqrt) then return w;
        w:=findsquareroot lc p;
        if null w then w:=findsquareroot red p;
        return w
    end;

symbolic procedure findnthroot p;
   nil;   % Until corrected.

% symbolic procedure x!-findnthroot p;
%     % Locate an n-th root symbol in poly p.
%     if domainp p then nil
%     else begin scalar w;
%         w:=mvar p; %check main var first.
%         if atom w
%           then return nil; %we have passed all sqrts.
%         if eqcar(w,'expt) and eqcar(caddr w,'quotient) then return w;
%         w:=findnthroot lc p;
%         if null w then w:=findnthroot red p;
%         return w
%     end;

symbolic procedure conjugatewrt(p,var);
  % Var -> -var in form p.
    if domainp p then p
    else if mvar p=var then begin
        scalar x,c,r;
        x:=tdeg lt p; %degree
        c:=lc p; %coefficient
        r:=red p; %reductum
        x:=remainder(x,2); %now just 0 or 1.
        if x=1 then c:=negf c; %-coefficient.
        return (lpow p .* c) .+ conjugatewrt(r,var) end
    else if ordop(var,mvar p) then p
    else (lpow p .* conjugatewrt(lc p,var)) .+
        conjugatewrt(red p,var);

symbolic procedure gcdcoeffsofsqrts u;
if atom u
  then if numberp u and minusp u
    then -u
    else u
  else if eqcar(mvar u,'sqrt)
    then begin
      scalar v;
      v:=gcdcoeffsofsqrts lc u;
      if v iequal 1
        then return v
        else return gcdf(v,gcdcoeffsofsqrts red u)
      end
    else begin
      scalar root;
      root:=findsquareroot u;
      if null root
        then return u;
      u:=makemainvar(u,root);
      root:=gcdcoeffsofsqrts lc u;
      if root iequal 1
        then return 1
        else return gcdf(root,gcdcoeffsofsqrts red u)
      end;

endmodule;

end;
