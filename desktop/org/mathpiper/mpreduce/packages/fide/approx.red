module approx;

% Author: R. Liska$

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


% Version REDUCE 3.6     05/1991$

fluid '(!*prapprox)$

switch prapprox$
!*prapprox:=nil$

global '(cursym!* coords!* icoords!* functions!* hipow!* lowpow!*)$
%  Implicitely given indices
icoords!*:='(i j k l m n i1 j1 k1 l1 m1 n1)$

algebraic$

procedure fact(n)$
if n=0 then 1
  else n*fact(n-1)$

procedure taylor(fce,var,step,ord)$
if step=0 or ord=0 then fce
  else fce+for j:=1:ord sum step**j/fact(j)*df(fce,var,j)$

symbolic$

procedure maxorder u$
begin
  scalar movar,var$
a:movar:=car u$
  if not eqexpr movar then return errpri2(movar,'hold)$
  movar:=cdr movar$
  var:=car movar$
  movar:=reval cadr movar$
  if not atom var or not(var memq coords!*) then return msgpri(
        " Parameter ",var," must be coordinate",nil,'hold)
    else if not fixp movar then return msgpri(
        " Parameter ", movar," must be integer",nil,'hold)
    else put(var,'maxorder,movar)$
  u:=cdr u$
  if u then go to a$
  return nil
end$

put('maxorder,'stat,'rlis)$

procedure center u$
begin
  scalar movar,var$
a:movar:=car u$
  if not eqexpr movar then return errpri2(movar,'hold)$
  movar:=cdr movar$
  var:=car movar$
  movar:=reval cadr movar$
  if not atom var or not(var memq coords!*) then return msgpri(
        " Parameter ",var," must be coordinate",nil,'hold)
    else if not(fixp movar or (eqcar(movar,'quotient) and
           (fixp cadr movar or
            (eqcar(cadr movar,'minus) and fixp cadadr movar))
                        and fixp caddr movar)) then return msgpri(
        " Parameter ", movar," must be integer or rational number",nil,
        'hold)
    else put(var,'center,movar)$
  u:=cdr u$
  if u then go to a$
  return nil
end$

put('center,'stat,'rlis)$

procedure functions u$
<<functions!* := u$
  for each a in u do put(a,'simpfn,'simpiden) >>$

put('functions,'stat,'rlis)$

procedure simptaylor u$
begin
  scalar ind,var,movar,step,fce,ifce$
  fce:=car u$
  if null cdr u then return simp fce$
  ifce:=cadr u$
  if cddr u then fce:= fce . cddr u$
  ind:=mvar numr simp ifce$
  var:=tcar get(ind,'coord)$
  step:=reval list('difference,
                   ifce,
                   list('plus,
                        if (movar:=get(var,'center)) then movar
                          else 0,
                        ind))$
  step:=list('times,
             step,
             get(var,'gridstep))$
  movar:=if (movar:=get(var,'maxorder)) then movar
           else 3$
  return simp list('taylor,
                   fce,
                   var,
                   step,
                   movar)
end$

algebraic$

procedure approx difsch$
begin
  scalar ldifsch,rdifsch,nrcoor,coors,rest,ldifeq,rdifeq,alglist!*$
  symbolic
    <<for each a in functions!* do
          <<put(a,'simpfn,'simptaylor)$
            eval list('depend,mkquote (a . coords!*)) >>$
      flag(functions!*,'full)$
      for each a in coords!* do put(a,'gridstep, intern compress append
                           (explode 'h,explode a))$
      nrcoor:=length coords!* - 1$
      eval list('array,
                mkquote list('steps . add1lis list(nrcoor)))$
      coors:=coords!*$
      for j:=0:nrcoor do
        <<setel(list('steps,j),aeval get(car coors,'gridstep))$
          coors:=cdr coors >>  >>$
  ldifsch:=lhs difsch$
  rdifsch:=rhs difsch$
  ldifeq:=ldifsch$
  rdifeq:=rdifsch$
  ldifeq:=substeps(ldifeq)$
  rdifeq:=substeps(rdifeq)$
  rest:=ldifsch-ldifeq-rdifsch+rdifeq$
  for j:=0:nrcoor do
    steps(j):=steps(j)**minorder(rest,steps(j))$
  write " Difference scheme approximates differential equation ",
     ldifeq=rdifeq$
  write " with orders of approximation:"$
  on div$
  for j:=0:nrcoor do write steps(j)$
  off div$
  symbolic if !*prapprox
     then algebraic write " Rest of approximation : ",rest$
  symbolic
    <<for each a in functions!* do
        <<put(a,'simpfn,'simpiden)$
          eval list('nodepend,mkquote (a . coords!*)) >>$
      remflag(functions!*,'full)>>$
  clear steps
end$

procedure substeps u$
begin
  scalar step,nu,du$
  nu:=num u$
  du:=den u$
  symbolic for each a in coords!* do
    <<step:=get(a,'gridstep)$
      flag(list step,'used!*)$
      put(step,'avalue,'(scalar 0)) >>$
  symbolic rmsubs()$
  nu:=nu$
  du:=du$
  symbolic for each a in coords!* do
    <<step:=get(a,'gridstep)$
      remflag(list step,'used!*)$
      remprop(step,'avalue) >>$
  symbolic rmsubs()$
  if du=0 then <<write
    " Reformulate difference scheme, grid steps remain in denominators"$
                 u:=0 >>
    else u:=nu/du$
  return u
end$

procedure minorder(pol,var)$
begin
  scalar lcofs,mord$
  coeff(den pol,var)$
  mord:=-hipow!*$
  lcofs := rest coeff(num pol,var)$
  if not(mord=0) then return (mord+lowpow!*)$
  mord:=1$
a:if lcofs={} then return 0
    else if first lcofs=0 then lcofs:=rest lcofs
    else return mord$
  mord:=mord+1$
  go to a
end$

endmodule;

end;
