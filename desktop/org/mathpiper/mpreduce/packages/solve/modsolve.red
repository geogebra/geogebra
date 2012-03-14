module modsolve; % Solve modular.

% Author: Herbert Melenk <melenk@zib.de>

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


% Algebraic interface: m_solve(eqn/eqnlist [,variables]).

% Some routines from solve and factor(modpoly) are needed.

fluid'(!*trnonlnr current!-modulus);

% The limit '10000000' for the current modulus has been calculated
% using a 500 MHz 686 machine which needs 115 seconds for a square root
% computation for that limit.  For faster machines the limit may be set
% a bit higher, for slower machines it should be set lower.

load!-package 'solve;
load!-package 'factor;

put('m_solve,'psopfn,'msolve);

symbolic procedure msolve(u);
  begin scalar s,s1,v,v1,w;
   s:=reval car u;
   s:=if eqcar(s,'list) then cdr s else {s};
   if cdr u then
   <<v:= reval cadr u;
     v:=if eqcar(v,'list) then cdr v else {v}>>;
   % test, collect variables.
   s1:=for each q in s collect
   <<if eqcar(q,'equal) then q:='difference.cdr q;
     w:=numr simp q./1; v1:=union(v1,solvevars{w});
     numr w>>;
   if null v then v:=v1;
   return msolve!-result
      if null cdr s1 then msolve!-poly(car s1,v)
                     else msolve!-psys(s1,v)
   end;

symbolic procedure msolve!-result u;
if u='failed then u else
 'list.for each v in u collect
  'list.for each w in v collect{'equal,car w,cdr w};

symbolic procedure msolvesys(s1,v,tg);
 % Interface for the Solve package.
  begin scalar w,fail;
   if null cdr s1 then
     <<w:=msolve!-poly(car s1,v); go to done>>;
     % Reject parametric modular equation system.
   for each p in s1 do
     for each x in kernels p do
       if not member(x,v) then fail:=t;
   if fail then
    <<if !*trnonlnr then lprim "Cannot solve parametric modular system";
       go to failed>>;
   w:=msolve!-psys(s1,v);
 done:
   if w='failed then go to failed;
   w:=for each q in w collect
     {for each r in q collect simp cdr r,
      for each r in q collect car r,1};
   return if tg then t.w else w;
 failed:
   return if null cdr s1 and null cdr v and null tg
            then mkrootsof(car s1 ./1,car v,1)
           else if tg then '(failed)
           else 'failed
 end;

symbolic procedure msolve!-poly1(f,x);
  % polynomial f(x);
 begin scalar w,l;
  if domainp f then nil
   else if ldeg f=1 then
   <<w:=safe!-modrecip  lc f;
     erfg!*:=nil;
     if null w then go to enum;
     w:=moduntag multf(w,negf red f);
     if w and (w<0 or w>current!-modulus)
             then w:=general!-modular!-number w;
     w:={w};
     go to done>>;
 enum:
    l:=lowestdeg(f,x,0);
    if l>0 then f:=quotf(f,numr simp{'expt,x,l});
    f:=general!-reduce!-mod!-p moduntag f;
    w:=for i:=1:current!-modulus -1 join
      if null general!-evaluate!-mod!-p(f,x,i) then {i};
    if l>0 then w:=append(w,{nil});
 done:
    return for each q in w collect{x.prepf q}
 end;

symbolic procedure msolve!-poly(f,l);
  % Solve one polynomial wrt several variables.
  begin scalar x,vl,limit;
   limit:=10000000; %%%%%%%%%%%%%%%%%%%%%%%%%%%% limit
   if current!-modulus>limit then
      <<if !*trnonlnr then lprim {"Current modulus larger than",limit};
        return 'failed>>;
   vl:=kernels f;
   for each x in l do
    <<if not member(x,vl) then l:=delete(x,l);
      vl:=delete(x,vl)>>;
   if null l then return nil;
   return if vl then msolve!-polya(f,l) else msolve!-polyn(f,l)
 end;

symbolic procedure msolve!-polyn(f,l);
(if null cdr l then msolve!-poly1(f,car l) else
 for i:=0:current!-modulus -1 join
  for each s in msolve!-polyn(numr subf(f,{x.i}),cdr l)
   collect (x.i).s) where x=car l;

symbolic procedure msolve!-polya(f,l);
  % 'f' is a polynomial with variables in 'l' and at least one more
  % formal parameter. 'f' can be solved only if 'f' is linear in one of
  % the variables with an invertible coefficient.  Otherwise we must
  % return a root-of expression.
  begin scalar x,c,w;
    for each y in l do if null x then
      if 1=ldeg ((w:=reorder f) where kord!*={y}) then x:=y;
    if null x then go to none;
    c:=lc w; w:=red w;
    if not domainp c then go to none;
    c:=safe!-modrecip c;
    if null c then go to none;
    return{{x.prepf multf(negf w,c)}};
 none: return{{car l.mk!*sq caaar mkrootsof(f./1,car l,1)}}
 end;

symbolic procedure msolve!-psys(s,v);
  % Solve system 's' for variables 'v'. 's' has no additional free
  % parameters.
  begin scalar b,o,z,w;
    if current!-modulus * length s>1000
        and primep current!-modulus then
    <<% Domain is a field and big problem - compute a Groebner base
      % first.
       load!-package 'groebner;load!-package 'groebnr2;
       o:=apply1('torder,{'list.v,'lex});
       b:=groebnereval{'list.for each p in s collect prepf p};
       z:=gzerodimeval{b};
       % The reverse basis for increasing variable number.
       s:=reversip for each p in cdr b collect numr simp p;
       apply1('torder,cdr o)>>
     else
    <<% Rearrange system for increasing variable number.
       w:=for each p in s collect
         length(for each x in v join if smemq(x,p)then{x}).p;
       w:=for each p in sort(w,'lesspcar) collect cdr p>>;
    return msolve!-psys1(s,v)end;

symbolic procedure msolve!-psys1(s,v);
  % Solve system by successive substitution.
  begin scalar w,w1,f,f1;
    w:={nil};
    for each f in s do
    <<w1:=nil;
      for each s in w do
      <<f1:=general!-reduce!-mod!-p moduntag numr subf(f,s);
        if null f1 then w1:=s.w1
         else if domainp f1 then nil
          else for each ns in msolve!-poly(f1,v)do
           w1:=append(s,ns).w1>>;
      w:=w1>>;
    return w
  end;

endmodule;

end;
