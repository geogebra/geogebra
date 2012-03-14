module charpol;

% Author: R. Liska.

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


% Version REDUCE 3.6     05/1991.

fluid '(!*exp !*gcd !*prfourmat)$

switch prfourmat$
!*prfourmat:=t$

procedure coefc1 uu$
begin
  scalar lco,l,u,v,a$
  u:=car uu$
  v:=cadr uu$
  a:=caddr uu$
  lco:=aeval list('coeff,u,v)$
  lco:=cdr lco$
  l:=length lco - 1$
  for i:=0:l do
    <<setel(list(a,i),car lco)$
      lco:=cdr lco >>$
  return (l . 1)
end$

deflist('((coefc1 coefc1)),'simpfn)$

global '(cursym!* coords!* icoords!* unvars!*)$

icoords!*:='(i j k l m n i1 j1 k1 l1 m1 n1)$

flag('(tcon unit charmat ampmat denotemat),'matflg)$

put('unit,'rtypefn,'getrtypecar)$
put('charmat,'rtypefn,'getrtypecar)$
put('ampmat,'rtypefn,'getrtypecar)$
put('denotemat,'rtypefn,'getrtypecar)$

procedure unit u$
generateident length matsm u$

procedure charmat u$
matsm list('difference,list('times,'lam,list('unit,u)),u)$

procedure charpol u$
begin
  scalar x,complexx;
  complexx:=!*complex;
  algebraic on complex;
  x:=simp list('det,list('charmat,carx(u,'charpol)))$
  if null complexx then algebraic off complex;
  return x
end;

put('charpol,'simpfn,'charpol)$

algebraic$
korder lam$

procedure re(x)$
sub(i=0,x)$

procedure im(x)$
(x-re(x))/i$

procedure con(x)$
sub(i=-i,x)$

procedure complexpol x$
begin
  scalar y$
  y:=troot1 x$
  return if im y=0 then y
           else y*con y
end$

procedure troot1 x$
begin
  scalar y$
  y:=x$
  while not(sub(lam=0,y)=y) and sub(lam=1,y)=0 do y:=y/(lam-1)$
  x:=sub(lam=1,y)$
  if not(numberp y or (numberp num y and numberp den y)) then
      write " If  ",re x,"  =  0  and  ",im x,
            "  =  0  , a root of the polynomial is equal to 1."$
  return y
end$

procedure hurw(x)$
% X is a polynomial in LAM, all its roots are |LAMI|<1 <=> for all roots
% of the polynomial HURW(X) holds RE(LAMI)<0.
(lam-1)**deg(num x,lam)*sub(lam=(lam+1)/(lam-1),x)$

symbolic$

procedure unfunc u$
<<unvars!*:=u$
  for each a in u do put(a,'simpfn,'simpiden) >>$

put('unfunc,'stat,'rlis)$

global '(denotation!* denotid!*)$
denotation!*:=nil$
denotid!*:='a$

procedure denotid u$
<<denotid!*:=car u$nil>>$

put('denotid,'stat,'rlis)$

procedure cleardenot$
denotation!*:=nil$

put('cleardenot,'stat,'endstat)$
flag('(cleardenot),'eval)$

algebraic$
array cofpol!*(20)$

procedure denotepol u$
begin
  scalar nco,dco$
  dco:=den u$
  u:=num u$
  nco:=coefc1 (u,lam,cofpol!*)$
  for j:=0:nco do cofpol!*(j):=cofpol!*(j)/dco$
  denotear nco$
  u:=for j:=0:nco sum lam**j*cofpol!*(j)$
  return u
end$

symbolic$

put('denotear,'simpfn,'denotear)$

procedure denotear u$
begin
  scalar nco,x$
  nco:=car u$
  for i:=0:nco do
    <<x:=list('cofpol!*,i)$
      setel(x,mk!*sq denote(getel x,0,i)) >>$
  return (nil .1)
end$

procedure denotemat u$
begin
  scalar i,j,x$
  i:=0$
  x:=for each a in  matsm u collect
       <<i:=i+1$
         j:=0$
         for each b in a collect
           <<j:=j+1$
             denote(mk!*sq b,i,j) >> >>$
  return x
end$

procedure denote(u,i,j)$
% U is prefix form, I,J are integers
begin
  scalar reu,imu,ireu,iimu,eij,fgcd$
  if atom u then return simp u$
  fgcd:=!*gcd$
  !*gcd:=t$
  reu:=simp!* list('re,u)$
  imu:=simp!* list('im,u)$
  !*gcd:=fgcd$
  eij:=append(explode i,explode j)$
  ireu:=intern compress append(append(explode denotid!* ,'(r)),eij)$
  iimu:=intern compress append(append(explode denotid!* ,'(i)),eij)$
  if car reu then insdenot(ireu,reu)$
  if car imu then insdenot(iimu,imu)$
  return simp list('plus,
                   if car reu then ireu
                     else 0,
                   list('times,
                        'i,
                        if car imu then iimu
                          else 0))
end$

procedure insdenot(iden,u)$
denotation!*:=(u . iden) . denotation!*$

procedure prdenot$
for each a in reverse denotation!* do
  assgnpri(list('!*sq,car a,t),list cdr a,'only)$

put('prdenot,'stat,'endstat)$
flag('(prdenot),'eval)$

procedure ampmat u$
begin
  scalar x,i,h1,h0,un,rh1,rh0,ru,ph1,ph0,!*exp,!*gcd,complexx$
  complexx:=!*complex;
  !*exp:=t$
  fouriersubs()$
  u:=car matsm u$
  x:=for each a in coords!* collect if a='t then 0
                                      else list('times,
                                                tcar get(a,'index),
                                                get(a,'wave),
                                                get(a,'step))$
  x:=list('expp,'plus . x)$
  x:=simp x$
  u:=for each a in u collect resimp quotsq(a,x)$
  gonsubs()$
  algebraic on complex;
  u:=for each a in u collect resimp a$
  remfourier()$
a:if null u then go to d$
  ru:=caar u$
  un:=unvars!*$
  i:=1$
b:if un then go to c$
  rh1:=reverse rh1$
  rh0:=reverse rh0$
  h1:=rh1 . h1$
  h0:=rh0 . h0$
  rh0:=rh1:=nil$
  u:=cdr u$
  go to a$
c:rh1:=coefck(ru,list('u1!*,i)) . rh1$
  rh0:=negsq coefck(ru,list('u0!*,i)) . rh0$
  un:=cdr un$
  i:=i+1$
  go to b$
d:h1:=reverse h1$
  h0:=reverse h0$
  if !*prfourmat then
      <<ph1:=for each a in h1 collect
               for each b in a collect prepsq!* b$
        setmatpri('h1,nil . ph1)$
        ph1:=nil$
        ph0:=for each a in h0 collect
               for each b in a collect prepsq!* b$
        setmatpri('h0,nil . ph0)$
        ph0:=nil >>$
  !*gcd:=t;
  x:=if length h1=1 then list list quotsq(caar h0,caar h1)
       else lnrsolve(h1,h0)$
  if null complexx then algebraic off complex;
  return x
end$

procedure coefck(x,y)$
% X is standard form, Y is prefix form, returns coefficient of Y
% appearing in X, i.e. X contains COEFCK(X,Y)*Y
begin
  scalar ky,xs$
  ky:=!*a2k y$
  xs:=car subf(x,list(ky . 0))$
  xs:=addf(x,negf xs)$
  if null xs then return(nil . 1)$
  xs:=quotf1(xs,!*k2f ky)$
  return if null xs then <<msgpri
    ("COEFCK failed on ",list('sq!*,x . 1,nil)," with ",y,'hold)$
                           xread nil$
                           !*f2q xs>>
           else !*f2q xs
end$

procedure simpfour u$
begin
  scalar nrunv,x,ex,arg,mv,cor,incr,lcor$
  nrunv:=get(car u,'nrunvar)$
a:u:=cdr u$
  if null u then go to r$
  arg:=simp car u$
  mv:=mvar car arg$
  if not atom mv or not numberp cdr arg then return msgpri
      ("Bad index ",car u,nil,nil,'hold)$
  cor:=tcar get(mv,'coord)$
  if not(cor member coords!*) then return msgpri
      ("Term ",car u," contains non-coordinate ",mv,'hold)$
  if cor member lcor then return msgpri
      ("Term ",car u," means second appearance of coordinate ",cor,
       'hold)$
  if not(cor='t) and cdr arg>get(cor,'maxden)
    then put(cor,'maxden,cdr arg)$
  lcor:=cor . lcor$
  incr:=addsq(arg,negsq !*k2q mv)$
  if not flagp(cor,'uniform) then return lprie
      ("Non-uniform grids not yet supported")$
  if cor='t then go to ti$
  ex:=list('times,car u,get(cor,'step),get(cor,'wave)) . ex$
  go to a$
ti:if null car incr then x:=list('u0!*,nrunv)
    else if incr= 1 . 1 then x:=list('u1!*,nrunv)
    else return lprie "Scheme is not twostep in time"$
  go to a$
r:for each a in setdiff(coords!*,lcor) do
    if a='t then x:=list('u0!*,nrunv)
      else ex:=list('times,tcar get(a,'index),get(a,'step),get(a,'wave))
                  . ex$
  return simp list('times,x,list('expp,'plus . ex))
end$

procedure fouriersubs$
begin
  scalar x,i$
  for each a in '(expp u1!* u0!*) do put(a,'simpfn,'simpiden)$
  x:=unvars!*$
  i:=1$
a:if null x then go to b$
  put(car x,'nrunvar,i)$
  i:=i+1$
  x:=cdr x$
  go to a$
b:flag(unvars!*,'full)$
  for each a in unvars!* do put(a,'simpfn,'simpfour)$
  for each a in coords!* do
    if not(a='t) then
    <<x:=intern compress append(explode 'h,explode a)$
      put(a,'step,x)$
      if not flagp(a,'uniform) then put(x,'simpfn,'simpiden)$
      x:=intern compress append(explode 'k,explode a)$
      put(a,'wave,x)$
      x:=intern compress append(explode 'a,explode a)$
      put(a,'angle,x)$
      put(a,'maxden,1) >>$
  algebraic for all z,y,v let
    expp((z+y)/v)=expp(z/v)*expp(y/v),
    expp(z+y)=expp z*expp y
end$

procedure gonsubs$
begin
  scalar xx$
  algebraic for all z,y,v clear expp((z+y)/v),expp(z+y)$
  for each a in coords!* do
    if not(a='t) then
      <<xx:=list('quotient,
                 list('times,
                      get(a,'maxden),
                      get(a,'angle)),
                 get(a,'step))$
        setk(get(a,'wave),aeval xx)$
        xx:=list('times,
                 get(a,'wave),
                 get(a,'step))$
        mathprint list('setq,
                       get(a,'angle),
                       if get(a,'maxden)=1 then xx
                         else list('quotient,
                                   xx,
                                   get(a,'maxden))) >>$
  algebraic for all x let expp x=cos x+i*sin x$
  algebraic for all x,n such that numberp n and n>1 let
    sin(n*x)=sin x*cos((n-1)*x)+cos x*sin((n-1)*x),
    cos(n*x)=cos x*cos((n-1)*x)-sin x*sin((n-1)*x)$
  for each a in unvars!* do
    <<put(a,'simpfn,'simpiden)$
      remprop(a,'nrunvar) >>
end$

procedure remfourier$
<<algebraic for all x clear expp x$
  algebraic for all x,n such that numberp n and n>1 clear
    sin(n*x),cos(n*x)$
  for each a in coords!* do
    if not(a='t) then
    <<remprop(a,'step)$
      remprop(remprop(a,'wave),'avalue)$
      remprop(a,'maxden) >> >>$

operator numberp$

endmodule;

end;
