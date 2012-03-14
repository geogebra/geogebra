module hurwp;

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


% Version REDUCE 3.6     05/1991

global '(ofl!* mlist!*)$
fluid '(!*exp !*gcd)$

flag('(tcon),'matflg)$
put('tcon,'msimpfn,'tcon)$
put('tcon,'rtypefn,'getrtypecar)$

procedure tcon u$
% Calculates complex conjugate and transpose matrix
begin
  scalar v,b$
  v:=matsm list('tp,u)$
  for each a in v do
    <<b:=a$
      while b do
        <<rplaca(b,quotsq(subf(numr car b, '((i minus i))),
                          !*f2q denr car b))$
          b:=cdr b >> >>$
  return v
end$

algebraic$
korder lam$

symbolic$

global '(positive!* userpos!* userneg!* !*pfactor)$
!*pfactor:=nil$

procedure positivep u$
% U is prefix form. Procedure tests if U>0, eventually writes this
% condition and puts U into POSITIVE!*. If U<=0 then returns NIL,
% if U>0 then T, in other cases 'COND.
% If it does not know if U>0 and program is running in interactive
% mode it asks user if U>0 and return value is based on user reply.
if numberp u then
    if u>0 then t
      else nil
  else if eqcar(u,'!*sq) and fixp caadr u and fixp cdadr u then
           if caadr u*cdadr u>0 then t
             else nil
  else
    begin
      scalar x,exp$
      exp:=!*exp$
      if !*pfactor and
             member('factor,mlist!*) then
           <<!*exp:=nil$
             u:=aeval list('ppfactor,u) >>$
      u:=prepsq!* simp u$
      !*exp:=exp$
      x:=if terminalp() and null ofl!* then
             begin
               scalar y,z$
               prin2!* "Is it true, that  "$
               maprin u$
               prin2!* "  >  0    ?"$
             a:prin2!* "  Reply (Y/N/?)"$
               terpri!* t$
               y:=read()$
               if y eq 'y then <<z:=t$ userpos!*:=u . userpos!* >>
                 else if y eq 'n
                  then <<z:=nil$ userneg!*:=u . userneg!*>>
                 else if y eq '? then z:='cond
                 else go to a$
               return z
             end
           else 'cond$
      if x eq 'cond then
          if null positive!* then positive!*:= list (1 . u)
            else positive!* := ((caar positive!* + 1) . u) . positive!*$
      return x
    end$

global'(hconds!*)$

algebraic$
array cof(20),fcof(20)$
share hconds!*$

procedure ppfactor x$
begin
  scalar d,n,de$
  d:= old_factorize(num x)$
  n:=for each a in d product a$
  if den x=1 then return n$
  d:= old_factorize(den x)$
  de:=for each a in d product a$
  return (n/de)
end$

procedure hurwitzp u$
% U is a polynomial in LAM. Procedure tests if it is Hurwitz polynomial
% i.e. for all its rools LAMI holds RE(LAMI)<0.
% Returned values: YES  - definitely yes
%                  NO   - definitely no
%                  COND - if conditions holds (all members of POSITIVE!*
%                         are >0)
if im u=0 then rehurwp u
  else cohurwp u$

symbolic$

procedure coef1(u,v,a)$
begin
  scalar lco,l$
  lco:=aeval list('coeff,u,v)$
  lco:=cdr lco$
  l:=length lco - 1$
  for i:=0:l do
    <<setel(list(a,i),car lco)$
      lco:=cdr lco >>$
  return l
end$

procedure rehurwp u$
begin
  scalar deg,hurp,gcd$
  gcd:=!*gcd$
  !*gcd:=t$
  deg:=coef1(car u,'lam,'cof)$
  if deg=0 then return typerr(u,"Polynomial in LAM")$
  positive!* := userpos!* := userneg!* := nil$
  if deg <= 2 then
      <<for i:=0:deg do setel(list('cof,i),
                              aeval list('quotient,
                                         getel list('cof,i),
                                         getel list('cof,deg)))$
        if deg=1 then hurp:=positivep getel list('cof,0)
          else if deg=2 then hurp:=
                   if positivep getel list('cof,0) and
                      positivep getel list('cof,1) then
                        if positive!* then 'cond
                          else t
                      else if positive!* then 'cond    % added 08/08/91
                      else nil$
        printcond(nil) >>
    else hurp:=rehurwp1 deg$
  !*gcd:=gcd$
  return rethurp hurp
end$

procedure rethurp hurp$
<<hconds!*:= 'list . if positive!* then
                         for each a in positive!* collect cdr a
                       else nil$
  !*k2q(if null hurp then 'no
          else if null positive!* then 'yes
          else 'cond) >>$

put('rehurwp,'simpfn,'rehurwp)$

procedure cohurwp u$
begin
  scalar deg$
  u:=reval list('sub,'(equal lam (times i lam)),car u)$
  deg:=coef1(u,'lam,'cof)$
  if deg=0 then return typerr(u,"Polynomial in LAM")$
  positive!* := userpos!* := userneg!* :=nil$
  if aeval list('im,getel list('cof,deg))=0 then
        for j:= 0:deg do setel(list('cof,j),
                              aeval list('times,'i,getel list('cof,j)))$
  return rethurp cohurwp1 (deg)
end$

put('cohurwp,'simpfn,'cohurwp)$

procedure rehurwp1 deg$
begin
  scalar i,bai,bdi,x,lich,sud,bsud,matr,hmat,csud,clich,dsud,dlich$
a:i:=deg$
  csud:=clich:=nil$
  bsud:=t$
b:x:=positivep getel list('cof,i)$
  if null x then go to c
    else if x eq t then bai:=t
    else if x eq 'cond then
      if i=deg or i=0 then <<csud:=caar positive!* . csud$
                             clich:=caar positive!* . clich >>
        else if bsud then csud:=caar positive!* . csud
        else clich:=caar positive!* . clich$
  i:=i-1$
  bsud:=not bsud$
  if i>=0 then go to b$
  go to d$
  % Change of sign AI = - AI
c:if bai or bdi then go to n
    else bai:=t$
  for i:=0:deg do setel(list('cof,i),
                        aeval list('minus,getel list('cof,i)))$
  go to a$
  % Checking DI > 0 - Hurwitz determinants
  % Splitting to odd and even coeffs. AI, A0 is coeff. by LAM**DEG
d:bsud:=t$
  for i:=deg step -1 until 0 do
    <<x:=simp getel list('cof,i)$
      if bsud then sud:=x . sud
        else lich:=x . lich$
      bsud:=not bsud >>$
  sud:=reverse sud$
  lich:=reverse lich$
  % Filling of SUD and LICH on the length DEG by zeroes from right
  sud:=filzero(sud,deg)$
  lich:=filzero(lich,deg)$
  dsud:=dlich:=nil$
  matr:=nil$
  i:=1$
  bsud:=nil$
d1:matr:=nconc(matr,list lich)$
  lich:=(nil . 1) . lich$
d2:hmat:=cutmat(matr,i)$
  x:=mk!*sq detq hmat$
  x:=positivep x$  % Necessary to add storing of odd and even DIs
  if null x then
      if bsud then go to n
        else go to c
    else if x eq t and not bsud then bdi:=t
    else if x eq 'cond then
      if bsud then dsud:=caar positive!* . dsud
        else dlich:=caar positive!* . dlich$
  i:=i+1$
  bsud:=not bsud$
  if i>deg then go to k$
  if not bsud then go to d1$
  matr:=nconc(matr,list sud)$
  sud:=(nil . 1) . sud$
  go to d2$
n:return nil$
k:if null positive!* or ((null csud or null clich) and
                         (null dsud or null dlich))
      then return <<printuser()$ t>>$
  prin2t "If we denote:"$
  printcond(t)$
  printdef('c1,clich:=reverse clich)$
  printdef('c2,csud:=reverse csud)$
  printdef('d1,dlich:=reverse dlich)$
  printdef('d2,dsud:=reverse dsud)$
  prin2t "Necessary and sufficient conditions are:"$
  prin2t if null csud or null clich then         "  (D1)  OR  (D2)"
           else if null dsud or null dlich then  "  (C1)  OR  (C2)"
           else "  (  (C1)  OR  (C2)  )  AND  (  (D1)  OR  (D2)  )"$
  printuser()$
  return 'cond
end$

procedure printcond(x)$
<<if not x then
      prin2t "Necessary and sufficient conditions are:"$
  positive!*:=reverse positive!*$
  for each a in positive!* do
    <<if x then <<prin2!* " ("$
                  prin2!* car a$
                  prin2!* ")  " >>$
      maprin cdr a$
      prin2!* "  >  0"$
      terpri!* t >>$
  if not x then printuser() >>$

procedure printuser()$
if userpos!* or userneg!* then
    <<prin2t"You have explicitly stated:"$
      for each a in userpos!* do <<maprin a$
                                   prin2!* "  >  0"$
                                   terpri!* t >>$
      for each a in userneg!* do <<maprin a$
                                   prin2!* "  <=  0"$
                                   terpri!* t >> >>$

procedure printdef(x,y)$
if y then
    <<prin2!* " ("$
      prin2!* x$
      prin2!* ")   ("$
      prin2!* car y$
      prin2!* ")"$
      if cdr y then for each a in cdr y do
          <<prin2!* " AND ("$
            prin2!* a$
            prin2!* ")" >>$
      terpri!* t >>$

procedure filzero(x,n)$
% Adds zeros (in S.Q. form) to the list X from right on the length N
begin
  scalar y,i$
  y:=x$
  i:=1$
  if null x then return typerr(x,"Empty list")$
  while cdr y do
    <<y:=cdr y$
      i:=i+1>>$
  while i<n do
    <<rplacd(y,list(nil . 1))$
      y:=cdr y$
      i:=i+1 >>$
  return x
end$

procedure cutmat(x,n)$
% From each member of list X, i.e. row of a matrix, remains
% the first N elements
for each a in x collect cutrow(a,n)$

procedure cutrow(y,n)$
begin
  scalar i,z,zz$
  i:=1$
  z:=list car y$
  zz:=z$
  y:=cdr y$
  while i<n do
    <<rplacd(zz,list car y)$
      y:=cdr y$
      zz:=cdr zz$
      i:=i+1 >>$
  return z
end$

procedure cohurwp1 (deg)$
begin
  scalar k,x,y,ak,bk,akk,bkk,matr,hmat$
  % Splitting on RE and IM part
  for j:=0:deg do
    <<x:=getel list('cof,j)$
      y:=simp list('re,x)$
      x:=resimp simp list('quotient,list('difference,x,mk!*sq y),'i)$
      ak:=x . ak$
      bk:=y . bk >>$
  % Construction of coeffs. AI, BI
  positive!*:=userpos!*:=userneg!*:=nil$
  akk:=filzero(ak,2*deg)$
  bkk:=filzero(bk,2*deg)$
  k:=2$
d1:matr:=nconc(matr,list akk)$
  matr:=nconc(matr,list bkk)$
  akk:=(nil . 1) . akk$
  bkk:=(nil . 1) . bkk$
  hmat:=cutmat(matr,k)$
  x:=mk!*sq detq hmat$
  x:=positivep x$
  if null x then go to n$
  if k=2*deg then go to ko$
  k:=k+2$
  go to d1$
n:return nil$
ko:printcond(nil)$
  return t
end$

endmodule;

end;
