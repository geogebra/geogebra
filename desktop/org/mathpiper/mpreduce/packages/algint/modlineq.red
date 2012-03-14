module modlineq;

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


fluid '(!*tra !*trmin current!-modulus sqrts!-mod!-prime);

global '(list!-of!-medium!-primes sqrts!-mod!-8);

exports check!-lineq;

list!-of!-medium!-primes:='(101 103 107 109);

sqrts!-mod!-8:=mkvect 7;

putv(sqrts!-mod!-8,0,t);

putv(sqrts!-mod!-8,1,t);

putv(sqrts!-mod!-8,4,t);

symbolic procedure modp!-nth!-root(m,n,p);
begin
  scalar j,p2;
  p2:=p/2;
  for i:=-p2 step 1 until p2 do
    if modular!-expt(i,n) iequal m
      then << j:=i; i:=p2 >>;
  return j
  end;


symbolic procedure modp!-sqrt(n,p);
begin
  scalar p2,s,tt;
  p2:=p/2;
  if n < 0
    then n:=n+p;
  for i:=0:p2 do begin
    tt:=n+p*i;
    if null getv(sqrts!-mod!-8,iremainder(tt,8))
      then return;
      % mod 8 test for perfect squares.
    if (iadd1 iremainder(tt,5)) > 2
      then return;
      % squares are -1,0,1 mod 5.
    s:=int!-sqrt tt;
    if fixp s then <<
      p2:=0;
      return >>
    end;
  if (not fixp s) or null s
    then return nil
    else return s
  end;

symbolic procedure check!-lineq(m,rightside);
begin
  scalar vlist,n1,n2,u,primelist,m1,v,modp!-subs,atoms;
  n1:=upbv m;
  for i:=0:n1 do <<
    u:=getv(m,i);
    if u
      then for j:=0:(n2:=upbv u) do
        vlist:=varsinsq(getv(u,j),vlist) >>;
  u:=vlist;
  while u do <<
    v:=car u;
    u:=cdr u;
    if atom v
      then atoms:=v.atoms
      else if (car v eq 'sqrt) or (car v eq 'expt)
    then for each w in varsinsf(!*q2f simp argof v,nil) do
             if not (w member vlist)
               then <<
                 u:=w.u;
                 vlist:=w.vlist >>
        else nil
      else interr "Unexpected item" >>;
  if sqrts!-mod!-prime and
     subsetp(vlist,for each u in cdr sqrts!-mod!-prime
                     collect car u)
    then go to end!-of!-loop;
  vlist:=setdiff(vlist,atoms);
  u:=nil;
  for each v in vlist do
    if car v neq 'sqrt
      then u:=v.u;
  vlist:=nconc(u,sortsqrts(setdiff(vlist,u),nil));
    % NIL is the variable to measure nesting on:
    % therefore all nesting is being caught.
  primelist:=list!-of!-medium!-primes;
  set!-modulus car primelist;
  atoms:=for each u in atoms collect
       u . modular!-number random car primelist;
  goto try!-prime;
next!-prime:
  primelist:=cdr primelist;
  if null primelist and !*tra
    then printc "Ran out of primes in check!-lineq";
  if null primelist
    then return t;
  set!-modulus car primelist;
try!-prime:
  modp!-subs:=atoms;
  v:=vlist;
loop:
  if null v
    then go to end!-of!-loop;
  u:=modp!-subst(simp argof car v,modp!-subs);
  if caar v eq 'sqrt
    then u:=modp!-sqrt(u,car primelist)
    else if caar v eq 'expt
      then u:=modp!-nth!-root(modular!-expt(u,cadr caddr car v),
        caddr caddr car v,car primelist)
      else interr "Unexpected item";
  if null u
    then go to next!-prime;
  modp!-subs:=(car v . u) . modp!-subs;
  v:=cdr v;
  go to loop;
end!-of!-loop:
  if null primelist
    then <<
      setmod(car sqrts!-mod!-prime);
      modp!-subs:=cdr sqrts!-mod!-prime >>
    else sqrts!-mod!-prime:=(car primelist).modp!-subs;
  m1:=mkvect n1;
  for i:=0:n1 do begin
    u:=getv(m,i);
    if null u
      then return;
    putv(m1,i,v:=mkvect n2);
    for j:=0:n2 do
      putv(v,j,modp!-subst(getv(u,j),modp!-subs))
    end;
  v:=mkvect n1;
  for i:=0:n1 do
    putv(v,i,modp!-subst(getv(rightside,i),modp!-subs));
  u:=mod!-jhdsolve(m1,v);
  if (u eq 'failed) and (!*tra or !*trmin)
    then <<
      princ "Proved insoluble mod ";
      printc car sqrts!-mod!-prime >>;
  return u
  end;

symbolic procedure varsinsq(sq,vl);
  varsinsf(numr sq,varsinsf(denr sq,vl));

symbolic procedure modp!-subst(sq,slist);
modular!-quotient(modp!-subf(numr sq,slist),
          modp!-subf(denr sq,slist));


symbolic procedure modp!-subf(sf,slist);
if atom sf
  then if null sf
    then 0
    else modular!-number sf
  else begin
    scalar u;
    u:=assoc(mvar sf,slist);
    if null u
      then interr "Unexpected variable";
    return modular!-plus(modular!-times(modular!-expt(cdr u,ldeg sf),
                    modp!-subf(lc sf,slist)),
             modp!-subf(red sf,slist))
    end;


symbolic procedure mod!-jhdsolve(m,rightside);
% Returns answer to m.answer=rightside.
% Matrix m not necessarily square.
begin
  scalar ii,n1,n2,ans,u,row,swapflg,swaps;
  % The SWAPFLG is true if we have changed the order of the
  % columns and need later to invert this via SWAPS.
  n1:=upbv m;
  for i:=0:n1 do
    if (u:=getv(m,i))
      then (n2:=upbv u);
  swaps:=mkvect n2;
  for i:=0:n2 do
    putv(swaps,i,n2-i);
    % We have the SWAPS vector, which should be a vector of indices,
    % arranged like this because VECSORT sorts in decreasing order.
  for i:=0:isub1 n1 do begin
    scalar k,v,pivot;
  tryagain:
    row:=getv(m,i);
    if null row
      then go to interchange;
    % look for a pivot in row.
    k:=-1;
    for j:=0:n2 do
      if (pivot:=getv(row,j)) neq 0
        then <<
          k:=j;
          j:=n2 >>;
    if k neq -1
      then goto newrow;
    if getv(rightside,i) neq 0
      then <<
        m:='failed;
    i:=sub1 n1; %Force end of loop.
        go to finished >>;
interchange:
    % now interchange i and last element.
    swap(m,i,n1);
    swap(rightside,i,n1);
    n1:=isub1 n1;
    if i iequal n1
      then goto finished
      else goto tryagain;
  newrow:
    if i neq k
      then <<
        swapflg:=t;
        swap(swaps,i,k);
      % record what we have done.
        for l:=0:n1 do
          swap(getv(m,l),i,k) >>;
    % place pivot on diagonal.
    pivot:=modular!-minus modular!-reciprocal pivot;
    for j:=iadd1 i:n1 do begin
      u:=getv(m,j);
      if null u
        then return;
      v:=modular!-times(getv(u,i),pivot);
      if v neq 0 then <<
        putv(rightside,j,
        modular!-plus(getv(rightside,j),
        modular!-times(v,getv(rightside,i))));
        for l:=0:n2 do
          putv(u,l,
         modular!-plus(getv(u,l),
         modular!-times(v,getv(row,l)))) >>
      end;
  finished:
    end;
  if m eq 'failed
    then go to failed;
    % Equations were inconsistent.
  while null (row:=getv(m,n1)) do
    n1:=isub1 n1;
  u:=nil;
  for i:=0:n2 do
    if getv(row,i) neq 0 then u:='t;
  if null u
    then if getv(rightside,n1) neq 0
      then go to failed
      else n1:=isub1 n1;
      % Deals with a last equation which is all zero.
  if n1 > n2
    then go to failed;
    % Too many equations to satisfy.
  ans:=mkvect n2;
  for i:=0:n2 do
    putv(ans,i,0);
  % now to do the back-substitution.
  % Note that the system is not necessarily square.
  ii:=n2;
  for i:=n1 step -1 until 0 do begin
    row:=getv(m,i);
    while getv(row,ii) = 0 do ii:=isub1 ii;
    if null row
      then return;
    u:=getv(rightside,i);
    for j:=iadd1 ii:n2 do
      u:=modular!-plus(u,
     modular!-times(getv(row,j),modular!-minus getv(ans,j)));
    putv(ans,ii,modular!-times(u,modular!-reciprocal getv(row,ii)));
    ii:=isub1 ii;
    end;
  if swapflg
    then vecsort(swaps,list ans);
  return ans;
failed:
  if !*tra
    then printc "Unable to force correct zeroes";
  return 'failed
  end;

endmodule;

end;
