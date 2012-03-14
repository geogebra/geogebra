module expres$

% Author: R. Liska

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


% Version REDUCE 3.6    05/1991

global '(!*outp)$                  % declarations for 3.4
fluid '(!*wrchri orig!* posn!*)$

switch wrchri$

global '(olddimension!* dimension!* coordindx!* cyclic1!* cyclic2!*
         sfprod!* nscal!*)$

flag('(share),'eval)$    % So that SHARE recognized by FASL.

share olddimension!*,dimension!*,coordindx!*,cyclic1!*,cyclic2!*,
         sfprod!*,nscal!*$

!*precise := nil;   % Needed in this module.

nscal!*:=0$

put('tensor,'tag,'tens)$
put('tensor,'fn,'tensfn)$
put('tensor,'evfn,'expres)$
put('tens,'prifn,'tenspri)$
flag('(tensor),'sprifn)$
put('tens,'setprifn,'settenspri)$
put('tensor,'typeletfn,'tenslet)$

symbolic procedure ptensor x$
   'tensor$

symbolic procedure poptensor u$
   if null u then 'tensor else nil$

deflist('((tensor ptensor)
          (tensop poptensor)
          (df getrtypecar)
          (diff getrtypecar)
           (!& ptensor)
          (!# ptensor)
          (!? ptensor)
          (grad ptensor)
          (div ptensor)
          (lapl ptensor)
          (curl ptensor)
          (vect ptensor)
          (dyad ptensor)
          (dirdf ptensor)),'rtypefn)$
put('cons,'rtypefn,'getrtypecons)$
put('rcons,'evfn,'evrcons)$
remprop('cons,'psopfn)$

symbolic procedure getrtypecons u$
if getrtypecar u eq 'tensor then 'tensor
  else 'rcons$

symbolic procedure evrcons(u,v)$
rcons cdr u$

symbolic procedure tensor u$
for each a in u do
  <<put(a,'rtype,'tensop)$
    put(a,'avalue,list('tensor,mktensor(0 , (0 . 1)))) >>$

deflist('((tensor rlis)),'stat)$

symbolic procedure tenslet(u,v,typu,b,typv)$
if not atom u then lprie list(" Non atom tensor variable ",u)
  else if b then
     <<if not eqcar(v,'tensor) then v:= mktensor(0,
             if eqcar(v,'!*sq) then cadr v else simp!* v)$
       rmsubs()$
       put(u,'rtype,'tensop)$
       put(u,'avalue,list('tensor,v)) >>
  else
    <<remprop(u,'rtype)$
      remprop(u,'avalue) >>$

%======================================================================
%  Data structure for tensor quantities
%  ====================================
%  (tensor nr rnk val car !*sqvar!*)
%  nr   -  integer, should be equal to actual nscal!*, otherwise
%          the quantity has been defined in previous coor. system
%          number of coordinate system
%  rnk  -  integer, 0,1,2
%          rank of the tensor
%          0 - scalar
%          1 - vertor
%          2 - dyad (matrix)
%  val  -  value
%          s.q.                   for rnk = 0
%          list of s.q.s          for rnk = 1
%          list of lists of s.q.s for rnk = 2
%  !*sqvar!* used in resimplification routine
%======================================================================
%  Smacro definitions for access of data structure subparts
%======================================================================

smacro procedure tensrnk u$
% determines rank from cddr of datastructure
car u$

smacro procedure tensval u$
% determines value from cddr of datastructure
cadr u$

symbolic procedure mktensor(rnk,u)$
'tensor . nscal!* . rnk . u . if !*resubs then !*sqvar!* else list nil$

symbolic procedure settenspri(u,v)$
if not atom u then lprie list(" Non-atom tensor variable ",u)
  else <<prin2!* u$
         prin2!* get('setq,'prtch)$
         tenspri v >>$

symbolic procedure tenspri u$
begin
  scalar rnk$
  u:=cddr u$
  rnk:=car u$
  u:=cadr u$
  if rnk=0 then
      <<pmaprin u$
        terpri!* t >>
    else if rnk=1 then
      <<tenspri1 u$
        orig!*:=0$
        terpri!* t >>
    else if rnk=2 then
      <<prin2!* " ( "$
        tenspri1 car u$
        for each i in cdr u do
          <<prin2!* " , "$
            terpri!* t$
            tenspri1 i >>$
        prin2!* " ) "$
        orig!*:=0$
        terpri!* t >>
    else lprie list(" Can't print tensor ",u," with rank ",rnk)
end$

symbolic procedure tenspri1 u$
<<prin2!* " ( "$
  orig!*:=posn!*$
  pmaprin car u$
  for each i in cdr u do
    <<prin2!* " , "$
      terpri!* t$
      pmaprin i >>$
  orig!*:=orig!* - 3$
  prin2!* " ) " >>$

symbolic procedure pmaprin u$
maprin(!*outp:=prepsq!*  u)$

symbolic procedure updatedimen()$
if olddimension!* = dimension!* then t
  else
    <<if dimension!* =2 then <<coordindx!* :='(2 1)$
                               cyclic1!* :='(1 2)$
                               cyclic2!* :='(2 1) >>
        else if dimension!* =3 then
          <<coordindx!* :='(3 2 1)$
            cyclic1!* :='(2 3 1)$
            cyclic2!* :='(3 1 2) >>
        else lprie list(" Can't handle dimension = ",dimension!*)$
      olddimension!* := dimension!* >>$

symbolic procedure expres(expn,v)$
express expn$

symbolic procedure resimptens u$
mktensor(caddr u,if caddr u=0 then resimp cadddr u
                   else if caddr u=1 then for each a in cadddr u collect
                                                      resimp a
                   else if caddr u=2 then
                     for each a in cadddr u collect
                       for each b in a collect resimp b
                   else lprie list("Can't handle tensor ",u,
                                   " with rank ",caddr u))$

symbolic procedure express expn$
begin
  scalar lst,matrx,rnk,opexpress$
  if not atom expn then go to op$
  if get(expn,'rtype) eq 'tensop and (rnk:=get(expn,'avalue)) and
        car rnk memq '(tensor tensop) and (rnk:=cadr rnk) then return
      if cadr rnk=nscal!* then
          if car cddddr rnk then rnk
            else resimptens rnk
        else lprie list(" You must rebind tensor ",expn,
            " in the new coordinate system")$
  return mktensor(0,simp!* expn)$
op:if car expn = 'vect then return mktensor
       (1,testdim1 for each a in cdr expn collect simp!* a)
     else if car expn = 'dyad then return mktensor
       (2,testdim2 for each a in cdr expn collect
                     for each b in a collect simp!* b)
     else if car expn eq 'tensor then return
       if cadr expn=nscal!* then
           if car cddddr expn then expn
             else resimptens expn
         else lprie list(" You must rebind tensor ",expn,
             " in the new coordinate system")$
  lst:=for each a in cdr expn collect cddr express a$
  if (opexpress:=get(car expn,'express)) then
      <<rnk:=eval(opexpress . list mkquote lst)$
        return mktensor(car rnk,cadr rnk)>>$
  if get(car expn,'simpfn) then return mktensor(0,simp(
      car expn . for each a in lst collect
                       if car a=0 then list('!*sq,cdr a,nil)
                         else typerr(expn," well formed scalar ") ))$
  lst:=for each a in lst collect
               if car a=0 then prepsq cdr a
                 else typerr(expn," well formed tensor")$
  return mktensor(0,!*k2q(car expn.lst))
end$

procedure testdim1 u$
if length u=dimension!* then u
  else <<lprie "Bad number of vector components"$u>>$

procedure testdim2 u$
begin
  scalar x$
  if length u = dimension!* then t
    else go to er$
  x:=u$
a:if length car u = dimension!* then t
    else go to er$
  x:=cdr x$
  if x then go to a$
  return u$
er:lprie "Bad number of dyad components"$
  return u
end$

%======================================================================
%  Procedures in EXPRESS properties of operators are returning
%  (rnk val), their argument is list of (rnk val)

symbolic procedure vectors arglist$
for each i in arglist do
  <<put(i,'rtype,'tensop)$
    put(i,'simpfn,'simpiden)$
    put(i,'avalue,list('tensop,
                  mktensor(1,reverse
                             for each a in coordindx!* collect
                               simp list(i,a) )) )>>$

deflist('((vectors rlis)),'stat)$

symbolic procedure dyads arglist$
for each i in arglist do
  <<put(i,'rtype,'tensop)$
    put(i,'simpfn,'simpiden)$
    put(i,'avalue,list('tensop,
                        mktensor(2,reverse
                                   for each a in coordindx!* collect
                                     reverse
                                     for each b in coordindx!* collect
                                       simp list(i,a,b)))) >>$

deflist('((dyads rlis)),'stat)$

symbolic procedure plusexpress u$
begin
  scalar z$
  z:=car u$
a:u:=cdr u$
  if null u then return z$
  z:=plus2ex(z,car u)$
  go to a
end$

put('plus,'express,'plusexpress)$

symbolic procedure plus2ex(x,y)$
begin
  scalar mtx,mty,slx,sly,rnk,ans,ans1$
  rnk:=tensrnk x$
  if not(rnk=tensrnk y) then lprie "Tensor mishmash"$
  if rnk=0 then return list(rnk,addsq(cadr x,cadr y))
    else if rnk=1 then
             <<slx:=tensval x$
               sly:=tensval y$
               while slx do
                 <<ans:=addsq(car slx,car sly) . ans$
                   slx:=cdr slx$
                   sly:=cdr sly>>$
               ans:= list(1,reverse ans) >>
    else if rnk=2 then
             <<mtx:=tensval x$
               mty:=tensval y$
               while mtx do
                 <<slx:=car mtx$
                   sly:=car mty$
                   ans1:=nil$
                   while slx do
                     <<ans1:=addsq(car slx,car sly) . ans1$
                       slx:=cdr slx$
                       sly:=cdr sly>>$
                   ans:=reverse ans1 . ans$
                   mtx:=cdr mtx$
                   mty:=cdr mty>>$
               ans:=list(2,reverse ans) >>$
  return ans
end$

symbolic procedure timesexpress u$
begin
  scalar z$
  z:=car u$
a:u:=cdr u$
  if null u then return z$
  z:=times2ex(z,car u)$
  go to a
end$

put('times,'express,'timesexpress)$

symbolic procedure times2ex(x,y)$
begin
  scalar rnkx,rnky$
  rnkx:=tensrnk x$
  rnky:=tensrnk y$
  return
    if rnkx=0 then list(rnky,times0ex(tensval x,tensval y,rnky))
      else if rnky=0 then list(rnkx,times0ex(tensval y,tensval x,rnkx))
      else lprie " Tensor mishmash "
end$

symbolic procedure times0ex(x,y,rnk)$
if rnk=0 then multsq(x,y)
  else if rnk=1 then
    for each a in y collect multsq(x,a)
  else if rnk=2 then
    for each a in y collect
      for each b in a collect multsq(x,b)
  else lprie " Tensor mishmash "$

symbolic procedure minusexpress expn$
timesexpress list(list(0,cons(-1,1)),car expn)$

put('minus,'express,'minusexpress)$

symbolic procedure differenceexpress expn$
plusexpress list(car expn,minusexpress list cadr expn)$

put('difference,'express,'differenceexpress)$

symbolic procedure quotientexpress expn$
if tensrnk cadr expn = 0 then
    times2ex(list(0,simp!* list('recip,prepsq tensval cadr expn)),
             car expn)
  else lprie " Tensor mishmash "$

put('quotient,'express,'quotientexpress)$

symbolic procedure exptexpress expn$
if tensrnk car expn=0 and tensrnk cadr expn = 0 then
    list(0,simp!* list('expt,
                       prepsq tensval car expn,
                       prepsq tensval cadr expn))
  else lprie " Tensor mishmash "$

put('expt,'express,'exptexpress)$

symbolic procedure recipexpress expn$
if tensrnk car expn = 0 then
    list(0,simp!* list('recip,
                       prepsq tensval car expn))
  else lprie " Tensor mishmash "$

put('recip,'express,'recipexpress)$

symbolic procedure inprodexpress expn$
begin
  scalar arg1,arg2,rnk1,rnk2$
  arg1:=tensval car expn$
  arg2:=tensval cadr expn$
  rnk1:=tensrnk car expn$
  rnk2:=tensrnk cadr expn$
  return
    if rnk1=1 then inprod1ex(arg1,arg2,rnk2)
      else if rnk1=2 then inprod2ex(arg1,arg2,rnk2)
      else lprie " Tensor mishmash "
end$

put('cons,'express,'inprodexpress)$

symbolic procedure inprod1ex(x,y,rnk)$
begin
  scalar lstx,lsty,mty,z,zz$
  lstx:=x$
  lsty:=y$
  if rnk=1 then
      <<z:=nil . 1$
        while lstx do
          <<z:=addsq(z,multsq(car lstx,car lsty))$
            lstx:=cdr lstx$
            lsty:=cdr lsty>>$
        z:=list(0,z)>>
    else if rnk=2 then
      <<z:=nil$
        lsty:=mty:=copy2(y,t)$
        while car mty do
          <<lstx:=x$
            lsty:=mty$
            zz:=nil . 1$
            while lstx do
              <<zz:=addsq(zz,multsq(car lstx,caar lsty))$
                rplaca(lsty,cdar lsty)$
                lsty:=cdr lsty$
                lstx:=cdr lstx>>$
            z:=nconc(z,list zz) >>$
        z:=list(1,z)>>
    else lprie " Tensor mishmash "$
  return z
end$

symbolic procedure inprod2ex(x,y,rnk)$
begin
  scalar mtx,z$
  mtx:=x$
  if rnk=1 then
      while mtx do
        <<z:=nconc(z,cdr inprod1ex(car mtx,y,1))$
          mtx:=cdr mtx>>
    else if rnk=2 then
      while mtx do
        <<z:=nconc(z,cdr inprod1ex(car mtx,copy2(y,t),2))$
          mtx:=cdr mtx>>
    else lprie " Tensor mishmash "$
  return list(rnk,z)
end$

symbolic procedure outexpress expn$
begin
  scalar x,y,z$
  x:=tensval car expn$
  y:=tensval cadr expn$
  if tensrnk car expn=1 and tensrnk cadr expn=1 and null cddr expn then
      for each i in x do z:=(for each a in y collect multsq(a,i) ) . z
    else lprie list(" Outer product of ",expn)$
  return list(2,reverse z)
end$

put('!&,'express,'outexpress)$
flag('(!&),'tensfn)$

symbolic procedure copy2(x,p)$
if null x then nil else
if p then copy2(car x,nil) . copy2(cdr x,t)
  else car x . copy2(cdr x,nil)$

symbolic procedure listar(arg,j)$
if j=1 then car arg
  else if j=2 then cadr arg
  else if j=3 then caddr arg
  else lprie list(" LISTAR ",arg,j)$

symbolic procedure listarsq(arg,j)$
prepsq listar(arg,j)$

symbolic procedure dinprod expn$
begin
  scalar x,y,z,xx,yy$
  x:=tensval car expn$
  y:=copy2(tensval cadr expn,t)$
  z:=nil . 1$
  if not(tensrnk car expn=2 and tensrnk cadr expn=2 and null cddr expn)
      then lprie list(" D-scalar product of ",expn)$
a:if null x and null y then go to d
    else if null x or null y then go to er$
  xx:=car x$
  yy:=car y$
b:if null xx and null yy then go to c
    else if null xx or null yy then go to er$
  z:=addsq(z,multsq(car xx,car yy))$
  xx:=cdr xx$
  yy:=cdr yy$
  go to b$
c:x:=cdr x$
  y:=cdr y$
  go to a$
d:return list(0,z)$
er:lprie list(" EXPRESS error ",expn," D-S dyads of dif. size")
end$

put('!#,'express,'dinprod)$
put('hash,'express,'dinprod)$
put('hash,'rtypefn,'ptensor)$

symbolic procedure antisymsum(u,v)$
if dimension!* = 2 then difmul(car u,cadr u,cadr v,car v)
  else if dimension!* = 3 then list
      (difmul(cadr u,caddr u,caddr v,cadr v),
       difmul(caddr u,car u,car v,caddr v),
       difmul(car u,cadr u,cadr v,car v))
  else lprie list(" ANTISYMSUM ",u,v)$

symbolic procedure difmul(a,b,c,d)$
% A*C-B*D$
addsq(multsq(a,c),negsq multsq(b,d))$

symbolic procedure vectprod expn$
begin
  scalar x,y,rnx,rny$
  x:=tensval car expn$
  y:=tensval cadr expn$
  rnx:=tensrnk car expn$
  rny:=tensrnk cadr expn$
  if rnx=1 and rny=1 then return
      list(dimension!* - 2,antisymsum(x,y))
    else if rnx=2 and rny=1 then return
      list(dimension!* - 1,for each a in x collect antisymsum(a,y) )
    else if rnx=1 and rny=2 then return
      list(dimension!* - 1,
           if dimension!*=3 then
               tp1 copy2(for each a in tp1(copy2(y,t)) collect
                         antisymsum(x,a),t)
             else for each a in tp1(copy2(y,t)) collect
                         antisymsum(x,a) )
    else lprie list(" VECTPROD of ",expn)
end$

put('!?,'express,'vectprod)$

algebraic operator diff$

symbolic procedure gradexpress expn$
begin
  scalar arg,vt,ans,row,z$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  if vt=0 then
      for each i in coordindx!* do
        ans:=simp!* list('quotient,
                       list('diff,
                            list('!*sq,arg,nil),
                            getel list('coordinats,i)),
                       getel list('sf,i)) . ans
    else if vt=1 then
      for each i in coordindx!* do
        <<row:=nil$
          for each j in coordindx!* do
            <<z:=list('diff,
                      listarsq(arg,j),
                      getel list('coordinats,i))$
              z:=list list('quotient,
                           z,
                           getel list('sf,i))$
              for each k in coordindx!* do
                z:=list('times,
                        getel list('christoffel,i,j,k),
                        listarsq(arg,k)) . z$
              row:=simp!*('plus.z) . row>>$
          ans:=row . ans>>
    else lprie list(" GRAD of ",expn)$
  return list(vt+1,ans)
end$

put('grad,'express,'gradexpress)$

symbolic procedure divexpress expn$
begin
  scalar arg,vt,ans,z$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  if vt=1 then
      <<for each i in coordindx!* do
          z:=list('diff,
                  list('times,
                       sfprod!*,
                       listarsq(arg,i),
                       list('recip,
                            getel list('sf,i))),
                  getel list('coordinats,i)) . z$
          z:='plus . z$
          z:=list('quotient,
                  z,
                  sfprod!*)$
          ans:=simp!* z>>
    else if vt=2 then
      for each i in coordindx!* do
        <<z:=nil$
          for j:=1:dimension!* do
            <<z:=list('quotient,
                      list('diff,
                           list('times,
                                listarsq(listar(arg,j),i),
                                sfprod!*,
                                list('recip,
                                     getel list('sf,j))),
                           getel list('coordinats,j)),
                      sfprod!*) . z$
              for l:=1:dimension!* do
                z:=list('times,
                        listarsq(listar(arg,j),l),
                        getel list('christoffel,j,i,l)) . z>>$
          ans:=simp!*('plus.z) . ans>>
    else lprie list(" DIV of ",expn)$
  return list(vt-1,ans)
end$

put('div,'express,'divexpress)$

symbolic procedure laplexpress expn$
begin
  scalar arg,vt,ans$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  if vt=0 then
      <<for i:=1:dimension!* do
          ans:=list('diff,
                    list('times,
                         sfprod!*,
                         list('expt,
                              getel list('sf,i),
                              -2),
                         list('diff,
                              list('!*sq,arg,nil),
                              getel list('coordinats,i))),
                    getel list('coordinats,i)).ans$
        ans:=list(0,simp!* list('quotient,
                              'plus . ans,
                              sfprod!*))>>
    else if vt=1 then
        ans:=divexpress list gradexpress expn
    else lprie list(" LAPLACIAN of ",expn)$
  return ans
end$

put('lapl,'express,'laplexpress)$

symbolic procedure curlexpress expn$
begin
  scalar arg,vt,ans,ic1,ic2$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  if vt=1 then
      for each i in (if dimension!* = 3 then coordindx!*
                       else '(1) ) do
        <<ic1:=listar(cyclic1!*,i)$
          ic2:=listar(cyclic2!*,i)$
          ans:=simp!* list('times,
                         getel list('sf,i),
                         list('recip,sfprod!*),
                         list('difference,
                              list('diff,
                                   list('times,
                                        getel list('sf,ic2),
                                        listarsq(arg,ic2)),
                                   getel list('coordinats,ic1)),
                              list('diff,
                                   list('times,
                                        getel list('sf,ic1),
                                        listarsq(arg,ic1)),
                                   getel list('coordinats,ic2))))
                                                            . ans>>
    else lprie list(" CURL of ",expn)$
  return (if dimension!* = 3 then list(1,ans)
            else list(0,car ans))
end$

put('curl,'express,'curlexpress)$
flag('(cons grad div lapl curl tens vect dyad dirdf !& !# !?)
     ,'tensfn)$

symbolic procedure exscalval u$
begin
  scalar fce,args$
  fce:=car u$
  args:=for each a in cdr u collect cddr express a$
  fce:=eval(get(fce,'express) . list mkquote args)$
  if car fce=0 then return cadr fce
    else typerr(u," is not scalar ")$
  return (nil . 1)
end$

algebraic$

infix #,?,&$
precedence .,**$
precedence #,.$
precedence ?,#$
precedence &,?$

symbolic flag('(cons !# !? div lapl curl dirdf),'full)$
symbolic for each a in '(cons !# !? div lapl curl dirdf)
   do put(a,'simpfn,'exscalval)$

symbolic procedure scalefactors transf$
begin
  scalar var$
  dimension!*:=car transf$
  transf:=cdr transf$
  if dimension!*=2 then
      <<var:=cddr transf$
        transf:=list( car transf,cadr transf)>>
    else if dimension!*:=3 then
      <<var:=cdddr transf$
        transf:=list(car transf,cadr transf,caddr transf)>>
    else lprie list(" Can't handle dimension = ",dimension!*)$
  if dimension!*=length var then t
    else lprie list(" Transformation ",transf,var)$
  for i:=1:dimension!* do
    setel(list('coordinats,i),listar(var,i))$
  for row:=1:dimension!* do
    for col:=1:dimension!* do
      setel(list('jacobian,row,col),
            aeval list('df,
                       listar(transf,col),
                       getel list('coordinats ,row)))$
  updatedimen()$
  rscale()
end$

deflist('((scalefactors rlis)),'stat)$

flag('(remd),'eval);
remd 'jacobian; remprop('jacobian,'opfn);   % For bootstrapping.

array jacobian(3,3),coordinats (3),sf(3),christoffel(3,3,3)$

procedure rscale()$
begin
  sfprod!*:=1$
  nscal!*:=nscal!* + 1$
  for row:=1:dimension!* do
    <<for col:=1:(row-1) do
        <<sf(row):=gcov(row,col)$
          if sf(row)=0 then nil
            else write " WARNING: Coordinate system is nonorthogonal"
                       ," unless following simplifies to zero: ",
                       sf(row) >>$
      sf(row):=sqrt gcov(row,row)$
      sfprod!*:=sfprod!* *sf(row)>>$
  on nero$
  for i:=1:dimension!* do for j:=1:dimension!* do
    for k:=1:dimension!* do begin christoffel(i,j,k):=
      ((if i=j then df(sf(j),coordinats (k)) else 0)
      -(if i=k then df(sf(k),coordinats (j)) else 0))
      /(sf(j)*sf(k))$
      if wrchri(a)=0 then write christoffel(i,j,k):=
          christoffel(i,j,k)   end$
  off nero
end$

procedure gcov(j,k)$
for l:=1:dimension!* sum jacobian(j,l)*jacobian(k,l)$

symbolic$

symbolic procedure simpwrchri u$
if !*wrchri then nil . 1
  else 1 . 1$

put('wrchri,'simpfn,'simpwrchri)$

symbolic procedure rmat$
'dyad . cdr matstat()$

symbolic procedure formdyad(u,v,m)$
'list . mkquote 'dyad . cddr formmat(u,v,m)$

put('dyad,'stat,'rmat)$
put('dyad,'formfn,'formdyad)$

symbolic procedure dirdfexpress expn$
begin
  scalar arg,vt,direc,ans,z,dj,di,argj,sfj,sfi,cooj$
  arg:=cadr expn$
  vt:=tensrnk arg$
  direc:=car expn$
  if not (tensrnk direc=1) then return
      lprie list (" Direction in DIRDF is not a vector ",expn)$
  if vt=0 then return inprodexpress list (direc,
                         gradexpress list arg)$
  arg:=tensval arg$
  direc:=tensval direc$
  if not(vt=1) then return
      lprie list (" Argument of DIRDF is dyadic ",expn)$
  for each i in coordindx!* do
    <<z:=nil$
      di:=listarsq(direc,i)$
      sfi:=getel list('sf,i)$
      for j:=1:dimension!* do
        <<dj:=listarsq(direc,j)$
          argj:=listarsq(arg,j)$
          sfj:=getel list('sf,j)$
          cooj:=getel list('coordinats,j)$
          z:=list('times,
                  dj,
                  list('recip,sfj),
                  list('diff,
                       listarsq(arg,i),
                       cooj)) . z$
          z:=list('times,
                  di,
                  argj,
                  list('recip,sfi),
                  list('recip,sfj),
                  list('df,sfi,cooj)) . z$
          z:=list('minus,
                  list('times,
                       dj,
                       argj,
                       list('recip,sfi),
                       list('recip,sfj),
                       list('df,
                            sfj,
                            getel list('coordinats,i)))) . z>>$
      z:='plus . z$
      z:=simp!* z$
      ans:=z . ans >>$
  return list(1,ans)
end$

put('dirdf,'express,'dirdfexpress)$

symbolic procedure dfexpress expn$
begin
  scalar arg,vt,rest$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  rest:=cdr expn$
  rest:=for each a in rest collect
          if tensrnk a=0 then
              if atom tensval a then tensval a
                else if cdr tensval a=1 and numberp car tensval a
                       then car tensval a
                else !*q2k tensval a
            else lprie list(" Bad arg of DF ",expn)$
  if vt=0 then return list(0,simpdf(list('!*sq,arg,t) . rest))
    else if vt=1 then return list(1,for each a in arg collect
                                       simpdf(list('!*sq,a,t) . rest) )
    else if vt=2 then return list(2,for each a in arg collect
                                      for each b in a collect
                                        simpdf(list('!*sq,b,t) . rest) )
    else lprie list(" Bad tensor in DF ",expn)
end$

put('df,'express,'dfexpress)$

symbolic procedure diffexpress expn$
begin
  scalar arg,vt,rest$
  arg:=tensval car expn$
  vt:=tensrnk car expn$
  rest:=cdr expn$
  rest:=for each a in rest collect
             if tensrnk a=0 then
                 if atom tensval a then tensval a
                   else if cdr tensval a=1 and numberp car tensval a
                    then car tensval a
                   else !*q2k tensval a
               else lprie list(" Bad arg of DIFF ",expn)$
  if vt=0 then return list(0,simp('diff . (prepsq arg . rest)))
    else if vt=1 then return list(1,for each a in arg collect
                                    simp('diff . (prepsq a . rest)) )
    else if vt=2 then return list(2,for each a in arg collect
                                      for each b in a collect
                                        simp('diff . (prepsq b . rest)))
    else lprie list(" Bad tensor in DIFF ",expn)
end$

put('diff,'express,'diffexpress)$

remprop('diff,'number!-of!-args);  % Until we understand what's up.

algebraic$

scalefactors 3,x,y,z,x,y,z$

endmodule;

end;
