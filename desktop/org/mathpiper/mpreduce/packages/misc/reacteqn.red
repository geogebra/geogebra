module reacteqn;  % REDUCE support for reaction equations.

% Author: H. Melenk
% January 1991
% Copyright (c) Konrad-Zuse-Zentrum Berlin, all rights reserved.

create!-package('(reacteqn),'(contrib misc));

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


% Introduce operators for chemical equations.

algebraic operator rightarrow;
newtok '((!- !>) rightarrow);
infix rightarrow;
precedence rightarrow,equal;

algebraic operator doublearrow;
newtok '((!< !>) doublearrow);
infix doublearrow;
precedence doublearrow,equal;

algebraic operator rate;


global '(species); share species;
global '(rates); share rates;

put('reac2ode,'psopfn,'r2oeval);

symbolic procedure r2oeval u;
  begin scalar r,k,x,rhs,lhs,ratel,odel,oldorder,lhsl,rhsl;
       integer rc;
    if eqcar(species,'list) then
      odel:=for each x in cdr species collect reval x . 0;
    u := reval car u;
    if not eqcar(u,'list) then typerr(u,"list of reactions");
    u := cdr u;
 loop:
    if null u then goto finis;
    r := reval car u; u := cdr u;
    if not pairp r or not memq(car r,'(rightarrow doublearrow))
       then goto synerror;
    lhs := r2speclist cadr r;
    rhs := r2speclist caddr r;
      % include new species
    for each x in append(lhs,rhs) do
               odel:=r2oaddspecies(cdr x,odel);
      % generate contribution from forward reaction.
    k := if u and (x:=reval car u) and
         not(pairp x and memq(car x,'(rightarrow doublearrow)))
          then <<u:=cdr u; x>> else list('rate,rc:=rc+1);
    ratel := k . ratel;
    r2oreaction(lhs,rhs,k,odel);
     % eventually generate backward reaction
    if car r='doublearrow then
    <<k := if u and (x:=reval car u) and
         not(pairp x and memq(car x,'(rightarrow doublearrow)))
          then <<u:=cdr u; x>> else list('rate,rc:=rc+1);
      ratel := k . ratel;
      r2oreaction(rhs,lhs,k,odel);
    >>;
    lhsl := lhs.lhsl; rhsl := rhs.rhsl;
    goto loop;
  finis:
   ratel := reversip ratel;
   rates := 'list. ratel;
   for each x in ratel do
     if numberp x or pairp x and get(car x,'dname) then
        ratel := delete(x,ratel);
   species := 'list. for each x in odel collect car x;
   r2omat(cdr species,reversip lhsl,reversip rhsl);
   for each r in ratel do if not idp r then
        ratel:=delete(r,ratel);
   if ratel then eval list('order,mkquote ratel);
   oldorder := setkorder append(ratel,cdr species);
   odel := 'list .
     for each x in odel collect
       list('equal,list('df,car x,'t),reval cdr x);
   setkorder oldorder;
   return odel;
  synerror:
    typerr(r,"reaction");
  end;

symbolic procedure  r2omat(sp,lhsl,rhsl);
  % construct input and output matrices in REDUCE syntax.
  begin scalar m; integer nreac,nspec,j;
    nspec := length sp; nreac:= length lhsl;
    apply ('matrix,list list list('inputmat,nreac,nspec));
    apply ('matrix,list list list('outputmat,nreac,nspec));
    for i:=1:nreac do
    << for each x in nth(lhsl,i) do
       <<j:=r2findindex(cdr x,sp);
         setmatelem(list ('inputmat,i,j),car x);
       >>;
       for each x in nth(rhsl,i) do
       <<j:=r2findindex(cdr x,sp);
         setmatelem(list ('outputmat,i,j),car x);
       >>;
    >>;
  end;

symbolic procedure r2findindex(a,l); r2findindex1(a,l,1);

symbolic procedure r2findindex1(a,l,n);
   if null l then rederr "index not found" else
   if a=car l then n else r2findindex1(a,cdr l,n+1);


symbolic procedure r2speclist u;
  % convert lhs/rhs to a list of pairs (multiplicity . spec).
  <<u:=if eqcar(u,'plus) then cdr u else list u;
    for each x in u collect r2speclist1 x>>;

symbolic procedure r2speclist1 x;
  if eqcar(x,'times) then r2speclist2(cadr x,caddr x,cdddr x)
   else 1 . x;

symbolic procedure r2speclist2(x1,x2,rst);
  if not null rst or not fixp x1 and not fixp x2 then
     typerr(append(list('times,x1,x2),rst),"species") else
     if fixp x1 then x1.x2 else x2.x1;

symbolic procedure r2oaddspecies(s,odel);
  % generate a new (empty) equation for a new species.
   if assoc(s,odel) then odel else
    <<prin2 "new species: ";prin2t s;
      append(odel,list(s.0))>>;

symbolic procedure r2oreaction(lhs,rhs,k,odel);
  % add the contribution of one reaction to the ode's.
   begin scalar coeff,e;
     coeff := k;
     for each x in lhs do
      coeff:=aeval list('times,coeff,list('expt,cdr x,car x));
     for each x in lhs do
     <<e := assoc(cdr x,odel);
       rplacd(e,reval list('difference,cdr e,list('times,coeff,car x)))
     >>;
     for each x in rhs do
     <<e := assoc(cdr x,odel);
       rplacd(e,reval list('plus,cdr e,list('times,coeff,car x)))
     >>;
     return odel;
   end;

endmodule;

end;
