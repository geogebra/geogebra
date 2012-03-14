module iimet;

% Author: R. Liska
% Version REDUCE 3.6  05/1991$

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


global '(cursym!* !*val dimension!*)$
fluid '(!*exp alglist!*)$

symbolic procedure array u$
begin
  scalar msg,erfg$
  msg:=!*msg$
  !*msg:=nil$
  erfg:=erfg!*$
  erfg!*:=nil$
  arrayfn('symbolic,
          for each a in u collect(car a . sub1lis cdr a) )$
  erfg!*:=erfg$
  !*msg:=msg
end$

symbolic procedure sub1lis u$
   if null u then nil else ((car u - 1) . sub1lis cdr u)$

sfprod!*:=1$

global'(date!*!*)$

date!*!*:= "IIMET Ver 1.1.2"$

put('version,'stat,'rlis)$

put('diff,'simpfn,'simpiden)$

global '(coords!* icoords!* dvars!* grids!* given!* same!*
         difml!* iobjs!* !*twogrid !*eqfu !*fulleq !*centergrid)$

switch twogrid,eqfu,fulleq,centergrid$

!*twogrid:=t$      %  Given functions can be on both grids.
!*eqfu:=nil$       %  During pattern matching the given and
                   %  looked for functions are different.
!*fulleq:=t$       %  Optimalization is performed on both sides of PDE.
!*centergrid:=t$   %  Centers of grid cells are in points I
                   %  (otherwise in I+1/2).

icoords!*:='(i j k l m n i1 j1 k1 l1 m1 n1)$
%  Indices which are given implicit.

procedure coordfn$
% Stat procedure of the COORDINATES statement, which defines indexes
% of coordinates.
begin
  scalar cor,icor$
  flag('(into),'delim)$
  cor:=remcomma xread nil$
  remflag('(into),'delim)$
  if cursym!* eq 'into then
      icor:=remcomma xread nil
    else if cursym!* eq '!*semicol!* then
      icor:=icoords!*
    else return symerr('coordfn,t)$
  return list('putcor,
              mkquote cor,
              mkquote icor)
end$

put('coordinates,'stat,'coordfn)$
flag('(putcor),'nochange)$

procedure putcor(u,v)$
begin
  scalar j$
  j:=1$
  coords!*:=u$
  while u do
    <<if not idp car u then        msgpri
          (" Coordinate ",car u," must be identifier",nil,'hold)$
      if not(idp car v or fixp car v) then        msgpri
          (" Index ",car v," must be identifier or integer",nil,'hold)$
      put(car u,'index,list car v)$
      put(car v,'coord,list car u)$
      put(car u,'ngrid,j)$
      j:=j+1$
      put(car u,'simpfn,'simpiden)$
      u:=cdr u$
      v:=cdr v >>
end$

procedure tcar u$
if pairp u then car u
  else u$

procedure grid u$
%  Procedure definning the statement GRID.
eval list(get(car u,'grid),
          mkquote cdr u)$

put('grid,'stat,'rlis)$
put('uniform,'grid,'gridunif)$

procedure gridunif u$
flag(u,'uniform)$

procedure dependence u$
%  Procedure definning the statemnt DEPENDENCE.
begin
  scalar x,y,z,gg,l,te,yy,y1,yl$
  if null coords!* then rederr
       " Coordinates have not been defined yet"$
  gg:=explode '!*grid$
  l:=list(length coords!* + 1)$
a:x:=car u$
  y:=car x$
  if idp y then if not(y memq dvars!*) then dvars!*:=y . dvars!*
                  else nil
    else return msgpri(" Variable ",y," must be identifier",nil,
                       'hold)$
  z:=cdr x$
  x:=car z$
  if not numberp x then go to b$
  if x=1 then apply('vectors,list y)
    else if x=2 then apply('dyads,list y)
    else if x=0 then t
    else return errpri2(car u,'hold)$
  z:=cdr z$
b:yl:=nil$
  yy:=explode y$
  te:=aeval y$
  if eqcar(te,'tensor) then te:=caddr te
    else te:=nil$
  if te=1 then
      for i:=1:dimension!* do
        <<y1:=intern compress append(yy,explode i)$
          yl:=y1 . yl$
          setk1(list(y,i),y1,t)$
          x:=intern compress append(explode y1,gg)$
              %  The name of an array filled with grids of Y(I)
          put(y1,'grid,x)$
          eval list('array,mkquote list(x . l)) >>
    else if te=2 then
      for i:=1:dimension!* do
        for j:=1:dimension!* do
          <<y1:=intern compress append(yy,append(explode i,
                                                 explode j))$
            yl:=y1 .  yl$
            setk1(list(y,i,j),y1,t)$
            x:=intern compress append(explode y1,gg)$
              %  The name of an array filled with grids of Y(I)
            put(y1,'grid,x)$
            eval list('array,mkquote list(x . l)) >>
    else
      <<yl:=y . yl$
        x:=intern compress append(yy,gg)$
        put(y,'grid,x)$
        eval list('array,mkquote list(x . l)) >>$
  for each a in yl do put(a,'simpfn, 'simpiden)$
  put(y,'names,reverse yl)$
  if te member '(1 2) then
      <<put(y,'kkvalue,get(y,'kvalue))$
        remprop(y,'kvalue) >>$
  for each v in z do
    if v memq coords!* then
        for each w in yl do depend1(w,v,t)
      else msgpri(" Identifier ",v," is not coordinate",nil,'hold)$
  u:=cdr u$
  if u then go to a$
  return nil
end$

put('dependence,'stat,'rlis)$

procedure given u$
begin
  scalar x,xnam$
a:x:=car u$
  xnam:=get(x,'names)$
  if not idp x then msgpri
       (" Variable ",x," must be identifier",nil,'hold)
    else if xnam then given!* := union(xnam,given!*)
    else msgpri
       (" Identifier ",x," is not variable",nil,'hold)$
  u:=cdr u$
  if u then go to a$
  return nil
end$

put('given,'stat,'rlis)$

procedure cleargiven$
<<remflag(given!*,'noeq)$
  given!*:=nil >>$

put('cleargiven,'stat,'endstat)$
flag('(cleargiven),'eval)$

newtok'(( !. !. ) isgr)$
algebraic infix ..$
grids!* := '(one half)$

procedure trlis$
% Stat procedure of the statement ISGRID.
begin
  scalar x$
  put('!*,'newnam,'tims)$
  x:=rlis()$
  remprop('!*,'newnam)$
  return x
end$

procedure formtr(u,vars,mode)$
list('isgrid,mkquote cdr u)$

procedure isgrid u$
% Procedure definning the statement ISGRID.
begin
  scalar x,y,z,z1,te,gd,lz,lz1$
a:x:=car u$
  y:=car x$
  x:=cdr x$
  if not(y memq dvars!*) then return msgpri
      (" Identifier ",y," is not variable",nil,'hold)$
  if null x then go to er$
  te:=aeval y$
  te:=if eqcar(te,'tensor) then caddr te
        else nil$
  if (te=1 and null atom x and fide_indexp car x and gridp cdr x) or
     (te=2 and null atom x and fide_indexp car x and null atom cdr x
      and fide_indexp cadr x and gridp cddr x) or
     ((te=0 or null te) and null atom x and gridp x) then t
    else go to er$
  if te=1 then
      <<z:=car x$
        x:=cdr x$
        lz:=nil$
        if numberp z then lz:=z . lz
          else for i:=1:dimension!* do lz:=i . lz$
        for each a in lz do for each b in x do
            setel(list(get(cadr assoc(list(y,a),
                                      get(y,'kkvalue)),
                           'grid),
                       car b),
                  cadr b . nil)  >>
    else if te=2 then
      <<z:=car x$
        z1:=cadr x$
        x:=cddr x$
        lz:=lz1:=nil$
        if numberp z then lz:=z . lz
          else for i:=1:dimension!* do lz:=i . lz$
        if numberp z1 then lz1:=z1 . lz1
          else for i:=1:dimension!* do lz1:=i . lz1$
        for each a in lz do for each b in lz1 do
          for each c in x do
            setel(list(get(cadr assoc(list(y,a,b),
                                      get(y,'kkvalue)),
                           'grid),
                       car c),
                  cadr c . nil) >>
    else for each c in x do
        setel(list(get(y,'grid),car c),cadr c . nil)$
  u:=cdr u$
  if u then go to a$
  return nil$
er:errpri2(car u,'hold)
end$

put('isgrid,'stat,'trlis)$
put('isgrid,'formfn,'formtr)$

procedure fide_indexp u$
u eq 'tims or (numberp u and 0<u and u<dimension!*)$

procedure gridp u$
null atom u and
  begin
    scalar x$
  a:x:=car u$
    if null atom x and car x eq 'isgr and null atom cdr x
       and cadr x memq coords!* and null atom cddr x and
       caddr x memq grids!* then rplaca(u,cdr x)
      else return nil$
    x:=car u$
    rplaca(x,get(car x,'ngrid))$
    u:=cdr u$
    if u then go to a$
    return t
  end$

procedure grideq u$
begin
  scalar x,y,z$
  x:=u$
a:y:=car x$
  if not(car y memq dvars!*) then return msgpri(
      "Identifier ",car y," is not variable",nil,'hold)$
  z:=cdr y$
b:if not atom car z and caar z eq 'isgr and cadar z memq coords!* and
      caddar z memq grids!* then put(car y,cadar z,caddar z)
    else return errpri2(u,'hold)$
  z:=cdr z$
  if z then go to b$
  x:=cdr x$
  if x then go to a$
  return nil
end$

put('grideq,'stat,'rlis)$

fluid '(coord unvars sunvars interpolp novars ncor nvar intp icor gvar
        hi hip1 hip2 him1 him2 lhs rhs lsun lun xxgrid resar)$

%  GVAR   -  grid on which is actual equation integrated
%  NVAR   -  number of actual equation in IIM21
%  NCOR   -  number of actual coordinate
%  UNVARS -  list of looked for functions
%  NOVARS -  list of functions controlled by the SAME statement
%  SUNVARS - list of looked for functions, which are not controlled by
%            SAME, and of given functions, which are not controlled by
%            SAME and which can be only on one grid (if OFF TWOGRID)
%            in case ON TWOGRID given functions are not in SUNVARS
%            distinguishing of given functions defined in ISGRID is done
%            in procedures TWOGRIDP and GETGRID
%  LSUN    - length of  SUNVARS-1
%  INTERPOLP-flag for MATCHLINEAR (for OFF EXP), has value T for calling
%            from INTERPOL and NIL for calling from SIMPINTT - for
%            NGETVAR in SIMPINTT
%  RESAR  -  the name of an array which will be filled by the result

procedure zero u$
nil . 1$

procedure ngetvar(u,v)$
if atom u then get(u,v)
  else if atom car u then get(car u,v)
  else nil$

procedure ngrid u$
if u eq 'one then 'none
  else if u eq 'half then 'nhalf
  else nil$

procedure gnot u$
% Procedure inverts denotation of half-integer and integer grid
if u='one then 'half
  else if u='half then 'one
  else nil$

procedure same u$
begin
  scalar x,y,z,bo$
  if null same!* then <<same!*:=list u$
                        return nil >>$
  x:=same!*$
a:y:=car x$
  z:=u$
  bo:=nil$
  while z and not bo do
    <<if car z memq y then bo:=t$
      z:=cdr z >>$
  if bo then go to b$
  x:=cdr x$
  if x then go to a$
  same!*:= u . same!*$
  return nil$
b:rplaca(x,union(y,u))$
  return nil
end$

put('same,'stat,'rlis)$

procedure clearsame$
same!*:=nil$

put('clearsame,'stat,'endstat)$
flag('(clearsame),'eval)$

procedure mksame$
begin
  scalar x,y,z,yy,bo$
  x:=expndsame()$
a:y:=car x$
  yy:=y$
  while yy and not(car yy memq unvars) do yy:=cdr yy$
  if null yy then
       <<msgpri
      (" Same declaration ",nil,list(y,
       " doesn't contain unknown variable"),nil,'hold)$
         go to b >>$
  if y neq yy then
      <<z:=car y$         % The looked for function on the first place
        rplaca(y,car yy)$
        rplaca(yy,z) >>$
  z:=car y$
  yy:=cdr y$
  put(z,'sames,yy)$
  novars:=union(novars,yy)$
  for each a in cdr y do
  % Testing if A has appeared in the statement DEPENDENCE
    if not get(a,'grid) then msgpri
        (" Identifier ",a," is not variable",nil,'hold)
      else put(a,'same,z)$
  for i:=1:length coords!* do
    <<yy:=y$
      bo:=nil$
      while yy and not bo do     % Test on ISGRID
        <<bo:=getel list(get(car yy,'grid),i)$
          yy:=cdr yy >>$
      if bo then filgrid(y,bo,i) >>$
b:x:=cdr x$
  if x then go to a$
  sunvars:=setdiff(unvars,novars)$
  return unvars
end$

procedure filgrid(y,bo,i)$
% Filling up after finding ISGRID according to SAME
begin
  scalar yy,bg$
  yy:=y$
  while yy do
    <<bg:=getel list(get(car yy,'grid),i)$
      if null bg then setel(list(get(car yy,'grid),i),bo)
        else if bg eq bo then t
        else msgpri
          (" Same declaration ",nil,list(y,
           " doesn't correspond to isgrid declarations"),nil,'hold)$
       yy:=cdr yy>>
end$

procedure expndsame$
% Extending SAME!* by new identifiers for vectors and tensors
begin
  scalar x,y,sam$
  x:=same!*$
a:y:=for each a in car x join copy1 get(a,'names)$
  sam:=y . sam$
  x:=cdr x$
  if x then go to a$
  return sam
end$

procedure copy1 u$
if null u then nil
  else if atom u then u
  else car u . copy1 cdr u$

procedure nrsame$
% Changing the numbering of variables according to SAME
for each a in sunvars do
  begin
    scalar x,nx$
    x:=get(a,'sames)$
    if x then
        <<nx:=get(a,'nrvar)$
          for each b in x do put(b,'nrvar,nx) >>$
    return nil
  end$

procedure iim u$
% Procedure defines the statement IIM
begin
  scalar xx,xxx,be,beb1,val,twogr$
  iim1 u$
  iobjs!*:=append(unvars,append(coords!*,given!*))$
  val:=!*val$
  !*val:=nil$
  novars:=sunvars:=nil$
  if same!* then mksame() else sunvars:=unvars$
  twogr:=!*twogrid$
  xxx:=setdiff(given!*,novars)$
  if !*twogrid then
      if null xxx then !*twogrid:=nil
        else flag(xxx,'twogrid)
    else sunvars:=union(sunvars,xxx)$
  flag(given!*,'noeq)$
  xxx:=0$                %  Numbering of variables and equation
  for each a in sunvars do
    <<put(a,'nrvar,xxx)$
      xxx:=xxx+1 >>$
  if same!* then nrsame()$
  xxx:=0$
  for each a in unvars do
    <<put(a,'nreq,xxx)$
      xxx:=xxx+1 >>$
  lun:=length unvars-1$
  lsun:=length sunvars-1$
  eval list('array,mkquote list('!*f2 . add1lis list(lun,lsun,1)))$
  xxx:=coords!*$
d:coord:=car xxx$
  icor:=tcar get(coord,'index)$
  difml!*:=nil$
  for i:=0:10 do difml!*:=append(difml!*,
       for each a in getel list('difm!*,i) collect
           if (xx:=atsoc(coord,cdr a)) then car a . cdr xx
             else if (xx:=atsoc('all,cdr a)) then car a . cdr xx
             else nil )$
  difml!*:=for each a on difml!* join if null car a then nil
                                        else list car a$
  if !*twogrid then difml!*:=
      for each a in difml!* collect
        if (xx:=caadr a) and (!*eqfu or memq(caar xx,'(f g))) then
            (car a . extdif(cdr a,nil))
          else a$
  be:=iim2 ()$
  iim21 be$
  if car be then beb1:=iim22 be
    else beb1:=list(car be,cadr be,car be)$
  if not fixp intp then msgpri(" INTP after heuristic search ",
            nil,list("is not a number, INTP=",intp),nil,nil)$
  if not(intp=0) then iim3 beb1$
  iim4 ()$
  xxx:=cdr xxx$
  if xxx then go to d$
  iim5 ()$
  for each a in '(rtype avalue dimension) do remprop('!*f2,a)$
  !*val:=val$ !*twogrid:=twogr$
  return nil
end$

procedure extdif(x,lg)$
% Performs corrections of diff. schemes for given functions on
% two grids - everytime chooses the scheme with minimal penalty.
% LG - list of all terms from (U V W F G), which has been in X
%      already changed and choosen.
begin
  scalar olds,news,y,gy,xx,lgrid,gg,g1$
  lgrid:=get('difm!*,'grids)$
  gy:=caar x$
  gg:=gy$
  for each a in lg do gg:=delete(atsoc(a,gg),gg)$
  if gg then gg:=caar gg
    else return x$
  x:=for each a in x collect a$
a:xx:=x$
  y:=car x$
  gy:=car y$
  g1:=atsoc(gg,gy)$
  gy:=(gg . gnot cdr g1) . delete(g1,gy)$
  gy:=acmemb(gy,lgrid)$
  while cdr xx and not eqcar(cadr xx,gy) do xx:=cdr xx$
  if cdr xx then
      <<olds:=y . (cadr xx . olds)$
        y:=if cadr y > cadadr xx
              or (cadr y=cadadr xx
                     and sublength caddr y > sublength car cddadr xx)
             then cadr xx
             else y$
        rplacd(xx,cddr xx) >>
    else olds:=y . olds$
  gy:=car y$
  g1:=atsoc(gg,gy)$
  gy:=delete(g1,gy)$
  if null gy then t
    else if (xx:=acmemb(gy,lgrid)) then gy:=xx
    else nconc(lgrid, list gy)$
  y:=gy . cdr y$
  news:=y . news$
  x:=cdr x$
  if x then go to a$
  if(xx:=caar news) and (!*eqfu or memq(caar xx,'(f g))) then
      <<olds:=extdif(olds,gg . lg)$
        news:=extdif(news,lg) >>$
  return nconc(olds,news)
end$

procedure sublength u$
if atom u then 0
  else length u + sublengthca u$

procedure sublengthca u$
if null u then 0
  else sublength car u + sublengthca cdr u$

procedure iim1 u$
% Checks the syntax of the IIM statement, calculates scalar PDEs,
% vector and tensor equations are expanded to scalar components.
begin
  scalar x,xx,e,te,exp$
  terpri()$
  prin2t"*****************************"$
  prin2 "*****      Program      *****          "$
  prin2t date!*!*$
  prin2t"*****************************"$
  exp:=!*exp$
  !*exp:=t$
  rhs:=lhs:=unvars:=nil$
  if null coords!* then return lprie " Coordinates defined not yet"$
  if null dvars!* then return lprie " Variables defined not yet"$
  for each v in dvars!* do
    if eqcar((te:=aeval v),'tensor) and caddr te member '(1 2) then
        <<put(v,'kvalue,get(v,'kkvalue))$
          rmsubs()>>$
  if atom u or not idp car u then return errpri2(u,'hold)$
  resar:=car u$
  u:=cdr u$
a:if atom u or atom cdr u then return errpri2(u,'hold)$
  x:=car u$
  if not idp x then return msgpri
      (" Parameter ",x," must be identifier",nil,'hold)
    else if not(x memq dvars!*) then return msgpri
      (" Identifier ",x," is not variable",nil,'hold)
    else if x memq unvars then return msgpri
      (" Variable ",x," has second appearance",nil,'hold)
    else if x memq given!* then return msgpri
      (" Variable ",x," cannot be declared given",nil,'hold)
    else unvars:=x . unvars$
  e:=cadr u$
  if not eqexpr e then return msgpri
      (" Parameter ",e," must be equation",nil,'hold)
    else e:=aeval list('times,
                       list('difference,cadr e,caddr e),
                       sfprod!*)$
  if atom e then return msgpri
      (" Equation ",e," must be P.D.E.",nil,'hold)$
  te:=aeval x$
  te:=if eqcar(te,'tensor) then caddr te
        else nil$
  if(te=1 and car e eq 'tensor and caddr e=1) or
    (te=2 and car e eq 'tensor and caddr e=2) or
    (null te and car e eq 'tensor and caddr e=0) then
      e:=cadddr e   %  Necessary to carrect after change in EXPRESS
    else if null te and car e eq '!*sq then e:=cadr e
    else return msgpri
      (" Tensor order of",x," does not correspond to order of ",e,
       'hold)$
  lhs:=e . lhs$
  u:=cddr u$
  if u then go to a$
  for each v in dvars!* do
    if eqcar((te:=aeval v),'tensor) and caddr te member '(1 2) then
        remprop(v,'kvalue)$
b:x:=car unvars$
  e:=car lhs$  %  Transformation of vectors and tensor into components
  te:=aeval x$
  te:=if eqcar(te,'tensor) then caddr te
        else nil$
  if te=1 then rhs:=append(e,rhs)
    else if te=2 then for each a in reverse e do
                        rhs:=append(a,rhs)
    else rhs:=e . rhs$
  xx:=append(get(x,'names),xx)$  %  Add the checking if given equation
  unvars:=cdr unvars$            %  solves given variable (tensor var.)
  lhs:=cdr lhs$
  if unvars then go to b$
  unvars:=xx$
  lhs:=rhs$
  put('diff,'simpfn,'zero)$      %  Splitting left and right hand side
  alglist!*:=nil . nil$          %  All derivatives go to left h.s.
  rhs:=for each a in rhs collect resimp a$
  put('diff,'simpfn,'simpiden)$
  alglist!*:=nil . nil$
  x:=lhs$
  xx:=rhs$
  terpri()$
  prin2t "      Partial Differential Equations"$
  prin2t "      =============================="$
  terpri()$
c:rplaca(xx,negsq car xx)$
  rplaca(x,prepsq!* addsq(car x,car xx))$
  rplaca(xx,prepsq!* car xx)$
  maprin car x$
  prin2!* "    =    "$
  maprin car xx$
  terpri!* t$
  rplaca(x,prepsq simp car x)$
  rplaca(xx,prepsq simp car xx)$
  x:=cdr x$
  xx:=cdr xx$
  if x then go to c$
  terpri()$
  x:=length lhs-1$
  if x=0 then eval list('array, mkquote list(resar . add1lis list(1)))
    else eval list('array,mkquote list(resar . add1lis list(x,1)))$
  !*exp:=exp$
  return nil
end$

procedure iim2$
% Defines the steps of the grid, splits variables to free and predefined
% grid in actual coordinate.
begin
  scalar b,e,xx,dihalf,dione,dihalfc$
  e:=append(explode 'h,explode coord)$
  e:=intern compress e$
  if flagp(coord,'uniform) then hi:=hip1:=him1:=him2:=e
    else <<put(e,'simpfn,'simpiden)$
           xx:=tcar get(coord,'index)$
           him1:=list(e,list('difference,xx,1))$
           him2:=list(e,list('difference,xx,2))$
           hi:=list(e,xx)$
           hip2:=list(e,list('plus,xx,2))$
           hip1:=list(e,list('plus,xx,1)) >>$
  dihalf:=list(
        'di . list('quotient,
                   list('plus,him1,hi),
                   2),
        'dim1 . him1,
        'dip1 . hi,
        'dim2 . list('quotient,
                     list('plus,him2,him1),
                     2),
        'dip2 . list('quotient,
                     list('plus,hi,hip1),
                     2))$
  dihalfc:=list(
        'di . list('quotient,
                   list('plus,hip1,hi),
                   2),
        'dim1 . hi,
        'dip1 . hip1,
        'dim2 . list('quotient,
                     list('plus,hi,him1),
                     2),
        'dip2 . list('quotient,
                     list('plus,hip2,hip1),
                     2))$
  dione:=list(
        'di . hi,
        'dim1 . list('quotient,
                     list('plus,him1,hi),
                     2),
        'dip1 . list('quotient,
                     list('plus,hi,hip1),
                     2),
        'dim2 . him1,
        'dip2 . hip1)$
  put('steps,'one,
      ('i . icor) . (if !*centergrid then dione else dihalf))$
  put('steps,'half,
      ('i . list('plus,
                 icor,
                 '(quotient 1 2))) . (if !*centergrid then dihalfc
                                        else dione))$
  ncor:=get(coord,'ngrid)$     % Number of the COODR coordinate
  e:=nil$
  for each a in sunvars do     % Splitting of variables with predefined
                               % grid.
    if (xx:=getel list(get(a,'grid),ncor))
            and car xx then e:=a . e$
  b:=setdiff(sunvars,e)$
  return list(b,e)
end$

procedure filfree(var,vgrid,freelst,pgr,peq)$
begin
  scalar x,nx,grn,nv,ng,ngrn,g1,g2,saml,bsam,asam,egrid$
  x:=ngetvar (var,'nrvar)$
c:put(var,pgr,vgrid)$
  egrid:=vgrid$
  if flagp(var,'noeq) then go to d$
  nx:=ngetvar (var,'nreq)$
% calulating in which point will be the euation for VAR integrated
  if egrid:=get(var,coord) then go to a
    else egrid:=vgrid$
  put('f2val,'free,'f2vzero)$
  if (g1:=f2eval(nx,x,0)) > (g2:=f2eval(nx,x,1)) then
      egrid:=gnot vgrid$
  if not(g1=g2) then go to a$
  put('f2val,'free,'f2vmin)$
  if(g1:=f2eval(nx,x,0)) > (g2:=f2eval(nx,x,1)) then
      egrid:=gnot vgrid$
  if not(g1=g2) then go to a$
  put('f2val,'free,'f2vmax)$
  if (g1:=f2eval(nx,x,0)) > (g2:=f2eval(nx,x,1)) then
      egrid:=gnot vgrid$
a:put(var,peq,egrid)$
% Penalties for free variables in the equation for VAR
  grn:=gnot egrid$
  ng:=ngrid egrid$
  ngrn:=ngrid grn$
  for each a in freelst do
    <<nv:=ngetvar (a,'nrvar)$
      asam:=a . get(a,'sames)$
      for each aa in asam do put(aa,pgr,egrid)$
      put(a,ng,cfplus2(get(a,ng),getel list('!*f2,nx,nv,0)))$
      for each aa in asam do put(aa,pgr,grn)$
      put(a,ngrn,cfplus2(get(a,ngrn),getel list('!*f2,nx,nv,1)))$
      for each aa in asam do remprop(aa,pgr) >>$
  if bsam then go to d$
  saml:=get(var,'sames)$
  bsam:=t$
d:if null saml then go to b$
  var:=car saml$
  saml:=cdr saml$
  go to c$
b:return egrid
end$

procedure f2eval(i,j,k)$
eval getel list('!*f2,i,j,k)$

procedure f2plus(i,j,k,l)$
% Procedure fills F2(I,J,K) with the number F2(I,J,K)+L$
begin
  scalar ma,x,y$
  if pairp l then
      if length car l=2 and cadr l=caddr l then l:=cadr l
        else if length l=3 and cadr l=caddr l and cadr l=cadddr l and
                cadr l=car cddddr l then l:=cadr l$
  ma:=list('!*f2,i,j,k)$
  x:=getel ma$
  if numberp l then
      if numberp x then setel(ma,x+l)
        else rplaca(x,car x+l)
    else
      if numberp x then setel(ma,list(x,l))
        else if (y:=assoc(car l,cdr x)) then
                 <<rplaca(cdr y,cadr y+cadr l)$
                   rplaca(cddr y,caddr y+caddr l)$
                   if cddar l then
                       <<rplaca(cdddr y,cadddr y + cadddr l)$
                         rplaca(cddddr y,car cddddr y+car cddddr l)>>>>
               else rplacd(x,(l . cdr x))
end$

procedure f2var u$
% Forms the elements of array !*F2 into the form
%               (FPLUS <CISLO> {(F2VAL U V N1 N2)})
if numberp u then u
  else ('fplus .
            car u . for each a in cdr u collect
                      list('f2val,car a,cdr a) )$

macro procedure f2val x$
% Evaluates the expression (F2VAL ...)
begin
  scalar us,ns,u,v,w,n1,n2,n3,n4,gu,gv,gw$
  x:=cdr x;
  us:=car x$
  ns:=cadr x$
  u:=car us$
  v:=cadr us$
  n1:=car ns$
  n2:= cadr ns$
  gu:=get(u,xxgrid)$
  gv:=get(v,xxgrid)$
  if cddr us then
      <<w:=caddr us$
        gw:=get(w,xxgrid)$
        n3:=caddr ns$
        n4:=cadddr ns>>$
  return mkquote
    if w then
        if gu and gv and gw then
            if gu eq gv and gu eq gw then n1
              else if gu eq gv then n2
              else if gu eq gw then n3
              else if gv eq gw then n4
              else apply(get('f2val,'free),list(us,ns))
          else if gu and gv then
            if gu eq gv then aplf2val(u,w,n1,n2)
              else aplf2val(u,w,n3,n4)
          else if gu and gw then
            if gu eq gw then aplf2val(u,v,n1,n3)
              else aplf2val(u,v,n2,n4)
          else if gv and gw then
            if gv eq gw then aplf2val(u,v,n1,n4)
              else aplf2val(u,v,n2,n3)
          else apply(get('f2val,'free),list(us,ns))
      else if gu and gv then
        if gu eq gv then n1
          else n2
      else apply(get('f2val,'free),list(us,ns))
end$

procedure aplf2val(u,v,n1,n2)$
apply(get('f2val,'free),list(list(u,v),list(n1,n2)))$

macro procedure fplus u$
% Evaluates the expression (FPLUS ...)
begin
  scalar x,y,z$
  u:=cdr u;
  y:=car u$
a:u:=cdr u$
  z:=eval car u$
  if numberp z then y:=y+z
    else x:=z . x$
  if cdr u then go to a$
  return mkquote
    if x then ('fplus . y . x)
      else y
end$

procedure cfplus2(u,v)$
% Adds the expressions of type (FPLUS ...).
% Destroys U, does not destroy V.
begin
  scalar f2v$
  f2v:=get('f2val,'free)$
  put('f2val,'free,'f2vunchange)$
  if not fixp u then u:=eval u$
  if not fixp v then v:=eval v$
  put('f2val,'free,f2v)$
  return
    if fixp u then
        if fixp v then (u + v)
          else ('fplus . (cadr v+u) . cddr v)
      else if numberp v then <<rplaca(cdr u,cadr u+v)$ u>>
      else <<rplaca(cdr u,cadr u+cadr v)$
             nconc(u,cddr v) >>
end$

procedure f2vunchange(us,ns)$
list('f2val,us,ns)$

procedure f2vzero(us,ns)$
0$

procedure f2vplus(us,ns)$
eval('plus . ns)$

procedure f2vmax(us,ns)$
eval('max . ns)$

procedure f2vmin(us,ns)$
eval('min . ns)$

put('f2val,'fselect,'f2vplus)$
put('f2val,'fgrid,'f2vmin)$

procedure iim21 u$
% Fills the array !*F2 according to the system of PDE and penalties
% given.
% Fills the properties NONE,NHALF (FONE,FHALF) of free variables.
% According to predefined variables filles the properties XGRID and EQ
% of predefined variables.
begin
  scalar b,e,lh,lhe,xx,rh,bdef$
  b:=car u$            % Free vars
  e:=cadr u$           % Predefined vars
  for i:=0:lun do
    for j:=0:lsun do   % Filling the array F2
      <<setel(list('!*f2,i,j,0),0)$
        setel(list('!*f2,i,j,1),0) >>$
  nvar:=0$             % Number of actual variable
  lh:=lhs$
  rh:=rhs$
  interpolp:=nil$
  put('intt,'simpfn,'simpiden)$
a:lhe:=car lh$         % Actual equation
  lhe:=formlnr list('intt,lhe,coord)$
  rplaca(lh,lhe)$
  bdef:=t$
  for each var in sunvars do
    if get(var,coord) then t
      else bdef:=nil$
  if null b and bdef then go to c$
      % If there are no free variables it is not necessary to fill
      % the array F2 - no optimalization is necessary -> To use this
      % statement, we have to test if we know in which point (over
      % which interval) will all equation be integrated (discretized).
  put('intt,'simpfn,'simpintt)$
  alglist!*:=nil . nil$
  simp lhe$
  put('intt,'simpfn,'simpiden)$
  if !*fulleq then     % Optimalizatioon is performed for both sides of
      <<lhe:=car rh$   % equations, otherwise only for left hand side.
        lhe:=formlnr list('intt,lhe,coord)$
        put('intt,'simpfn,'simpintt)$
        simp lhe$
        alglist!*:=nil . nil$
        put('intt,'simpfn,'simpiden)$
        rh:=cdr rh >>$
c:nvar:=nvar+1$
  lh:=cdr lh$
  if lh then go to a$
  for i:=0:lun do
    for j:=0:lsun do
      for k:=0:1 do
        setel(list('!*f2,i,j,k),f2var getel list('!*f2,i,j,k))$
  xxgrid:='xgrid$
  for each a in b do
    <<put(a,'none,0)$
      put(a,'nhalf,0) >>$
  for each a in e do    % Predefined variables
    filfree(a,car getel list(get(a,'grid),ncor),b,'xgrid,'eq)$
  % Predefined penalties
  intp:=0$
  for each a in e do
    if a memq unvars then
        <<xx:=ngetvar(a,'nreq)$
          for each c in e do
            if get(c,'xgrid) eq get(a,'eq) then intpfplus(xx,c,0)
              else intpfplus(xx,c,1) >>$
  for each a in b do
    <<put(a,'fone,get(a,'none))$
      put(a,'fhalf,get(a,'nhalf)) >>$
  return nil
end$

procedure iim22 u$
begin
  scalar b,e,bb,b1,b2,x,xx,x1,nv,g1,g2$
  b:=car u$
  e:=cadr u$
  bb:=b$             % Heuristic determination of grids for
                     % variables from B
f:x:=car bb$         % Chose the next variable X
  put('f2val,'free,get('f2val,'fselect))$
  xx:=abs(eval get(x,'none)-eval get(x,'nhalf))$
  b2:=cdr bb$
  while b2 do
    if xx<(x1:=abs(eval get(car b2,'none)-eval get(car b2,'nhalf)))
        then <<x:=car b2$
               xx:=x1$
               b2:=cdr b2 >>
      else b2:=cdr b2$
  b1:=x . b1$       %  List of variables subsequently choosen from B
  bb:=delete(x,bb)$ %  List of variables remaining in B
  put('f2val,'free,get('f2val,'fgrid))$
  put(x,'xgrid,'one)$
  g1:=eval get(x,'none)$
  put(x,'xgrid,'half)$
  g2:=eval get(x,'nhalf)$
  if g1>g2 then xx:='half
    else xx:='one$
  filfree(x,xx,bb,'xgrid,'eq)$
  intpgplus(x,xx)$
  for each ax in (x . get(x,'sames)) do
    if ax memq unvars then
    <<x1:=ngetvar(ax,'nreq)$
      for each a in append(b1,e) do
        if get(a,'xgrid)=get(ax,'eq) then intpfplus(x1,a,0)
          else intpfplus(x1,a,1) >>$
  if bb then go to f$
  return list(b,e,b1)
end$

procedure intpfplus(nx1,a,n)$
intp:=cfplus2(intp,getel list('!*f2,nx1,ngetvar(a,'nrvar),n))$

procedure intpgplus(a,ga)$
intp:=cfplus2(intp,get(a,ngrid ga))$

procedure iim3 u$
begin
  scalar b,e,b1,bb$
  prin2t" Backtracking needed in grid optimalization"$
  b:=car u$         % Free vars
  e:=cadr u$        % Predefined vars
  b1:=caddr u$
  for each a in b do        % Full search - bactracking
    <<put(a,'none,get(a,'fone))$
      put(a,'nhalf,get(a,'fhalf)) >>$
  xxgrid:='bxgrid$
  nbxgrid(e,'bxgrid,'beq,'xgrid,'eq)$
  put('f2val,'free,'f2vunchange)$
  varyback(b1,nil)$
  for each a in union(unvars,given!*) do
    <<remprop(a,'bxgrid)$
      remprop(a,'beq) >>$
  return nil
end$

procedure nbxgrid(u,ng,ne,og,oe)$
for each a in u do
  for each b in (a . get(a,'sames)) do
    <<put(b,ng,get(b,og))$
      put(b,ne,get(b,oe)) >>$

procedure varyback(bb,b1)$
% Performs full search of BB. B1 is B-BB. N is the number of
% interpolations performed up to now.
if null bb then
    begin
      scalar none,nhalf,n,eqg,i,j$
      n:=0$
      for each a in unvars do
        <<none:=nhalf:=0$
          i:=get(a,'nreq)$
          for each b in sunvars do
            <<j:=get(b,'nrvar)$
              none:=none + if get(b,'bxgrid) eq 'one then f2eval(i,j,0)
                             else f2eval(i,j,1)$
              nhalf:=nhalf + if get(b,'bxgrid) eq 'half
                               then f2eval(i,j,0)
                              else f2eval(i,j,1) >>$
          put(a,'beq,if (eqg:=get(a,coord)) then eqg
                       else if none<=nhalf then 'one
                       else 'half)$
          n:=n + if eqg then
                     if eqg eq 'one then none
                       else if eqg eq 'half then nhalf
                       else <<msgpri("VARYBACK ",eqg,nil,nil,'hold)$ 0>>
                   else if none<=nhalf then none
                   else nhalf >>$
      if n<intp then
          <<nbxgrid(b1,'xgrid,'eq,'bxgrid,'beq)$
            for each a in unvars do put(a,'eq,get(a,'beq))$
            intp:=n >>
    end
  else if intp=0 then t
  else <<varb(bb,b1,'one)$
         varb(bb,b1,'half) >>$

procedure varb(bb,b1,xx)$
% Subprocedure of VARYBACK procedure
% In BB are temporary free variables
% In B1 are temporary predefined variables (over BXGRID property)
begin
  scalar x$
  x:=car bb$
  for each a in (x . get(x,'sames)) do
    put(a,'bxgrid,xx)$
  return varyback(cdr bb,x . b1)
end$

procedure iim4$
begin
  scalar lh,rh,x,lhe,var$
  intp:=intp/6$
  prin2 intp$
  prin2 " interpolations are needed in "$
  prin2 coord$
  prin2t " coordinate"$
  for each a in unvars do
    <<prin2"  Equation for "$
      prin2 a$
      prin2" variable is integrated in "$
      prin2 get(a,'eq)$
      prin2t" grid point" >>$
  interpolp:=t$
  put('intt,'simpfn,'simpinterpol)$
  lh:=lhs$
  rh:=rhs$
  x:=unvars$
j:var:=car x$
  gvar:=get(var,'eq)$
  lhe:=car lh$
  alglist!*:=nil . nil$
  lhe:=prepsq simp lhe$
  rplaca(lh,lhe)$
  lhe:=car rh$
  lhe:=formlnr list('intt,lhe,coord)$
  lhe:=prepsq simp lhe$
  rplaca(rh,lhe)$
  x:=cdr x$
  lh:=cdr lh$
  rh:=cdr rh$
  if x then go to j$
  put('intt,'simpfn,'simpiden)$
  return lhs
end$

procedure iim5$
begin
  scalar lh,rh,val,nreq,ar$
  val:=!*val$
  !*val:=nil$
  for each a in union(union(unvars,sunvars),given!*) do
    <<remprop(a,'xgrid)$
      remprop(a,'eq)$
      remprop(a,'nreq)$
      remprop(a,'nrvar)$
      remprop(a,'sames)$
      remprop(a,'none)$
      remprop(a,'nhalf)$
      remprop(a,'fone)$
      remprop(a,'fhalf) >>$
  remflag(given!*,'twogrid)$
  remflag(given!*,'noeq)$
  terpri()$
  prin2t "         Equations after Discretization Using IIM :"$
  prin2t "         =========================================="$
  terpri()$
  lh:=lhs$
  rh:=rhs$
  nreq:=0$
k:rplaca(lh,prepsq!* simp!* car lh)$
  maprin car lh$
  prin2!* "   =   "$
  rplaca(rh,prepsq!* simp!* car rh)$
  maprin car rh$
  terpri!* t$
  terpri()$
  ar:=if null cdr lhs then list(resar,0) else list(resar,nreq,0)$
  setel(ar,car lh)$
  ar:=if null cdr lhs then list(resar,1) else list(resar,nreq,1)$
  setel(ar,car rh)$
  lh:=cdr lh$
  rh:=cdr rh$
  nreq:=nreq+1$
  if lh then go to k$
  !*val:=val$
  return nil
end$

put('iim,'stat,'rlis)$

array difm!*(10)$

procedure iscomposedof(x,objs,ops)$
if null x then nil
  else if atom x then if idp x then memq(x,objs)
                        else if fixp x then t
                        else nil
  else if idp car x and car x memq ops and cdr x then
            iscompos(cdr x,objs,ops)
  else nil$

procedure iscompos(x,objs,ops)$
if null x then t
  else if idp car x then car x memq objs and iscompos(cdr x,objs,
                                                       ops)
  else if numberp car x then iscompos(cdr x,objs,ops)
  else if atom car x then nil
  else if idp caar x then caar x memq ops and cdar x and
                iscompos(cdar x,objs,ops) and iscompos(cdr x,objs,ops)
  else nil$

global'(difconst!* diffuncs!*)$
difconst!* := '(i n di dim1 dip1 dim2 dip2)$
diffuncs!*:=nil$

procedure difconst u$
difconst!* := append(u,difconst!*)$

put('difconst,'stat,'rlis)$

procedure diffunc u$
<<diffuncs!*:=append(u,diffuncs!*)$
  for each a in u do put(a,'matcheq,'matchdfunc) >>$

put('diffunc,'stat,'rlis)$

procedure matchdfunc(u,v)$
begin
  scalar x,y$
  return
    if null u and null v then list t
      else if null u or null v then nil
      else if (x:=matcheq(car u,car v)) and (y:=matchdfunc(cdr u,cdr v))
        then union(x,y)
      else nil
end$

procedure difmatch u$
begin
  scalar l,gds,gdsf,pl,x,dx,y,z,coor$
  coor:=car u$
  if not atom coor then go to er$
  u:=cdr u$
  x:=car u$
  if not iscomposedof(x,'(u f x n v w g),
       append(diffuncs!*,'(diff times expt quotient recip)))then
      go to er$
  x:=prepsq simp x$
  l:=if atom x then 0 else length x$
  x:=x . nil$
  if null(y:=getel list('difm!*,l)) then setel(list('difm!*,l),list x)
    else if (z:=assoc(car x,y)) then x:=z
    else nconc(y,list x)$
  y:=cdr u$
a:gds:=nil$
  gdsf:=nil$
  if not eqexpr car y then go to b$
a1:if not(cadar y memq '(u v w f g) and caddar y memq grids!*) then
                                                        go to er$
  if cadar y memq '(f g) then gdsf:=(cadar y . caddar y) . gdsf
    else gds:=(cadar y . caddar y) . gds$
  y:=cdr y$
  if null y then go to er$
  if eqexpr car y then go to a1$
b:if not fixp car y then go to er$
  pl:=car y$
  y:=cdr y$
  if null y then go to er$
  if not iscomposedof(car y,difconst!*,append(diffuncs!*,'(u x f v w
     g plus minus difference times quotient recip expt)))then go to er$
  dx:=car y$
  y:=cdr y$
  gds:=nconc(gdsf,gds)$
  defdfmatch(x,gds,pl,list dx,coor)$
  if y then go to a$
  return nil$
er:errpri2(y,'hold)
end$

procedure defdfmatch(x,gds,pl,dx,coor)$
begin
  scalar y,z,yy$
  y:=get('difm!*,'grids)$
  if null y then put('difm!*,'grids,list gds)
    else if null gds then t
    else if (z:=acmemb(gds,y)) then gds:=z
    else nconc(y,list gds)$
  y:=cdr x$
  if y then
      if (yy:=atsoc(coor,y)) then
          if (z:=assoc(gds,cdr yy)) then
              <<rplacd(z,pl . dx)$
                msgpri(" Difference scheme for ",car x," redefined ",
                    nil,nil) >>
            else nconc(cdr yy,list(gds . (pl . dx)))
        else nconc(y,list(coor . list(gds . (pl . dx))))
    else rplacd(x,list(coor . list(gds . (pl . dx))))$
  return y
end$

deflist('((difmatch rlis) (cleardifmatch endstat)),'stat)$

procedure cleardifmatch$
for i:=0:10 do difm!*(i):=nil$

flag('(cleardifmatch),'eval)$

procedure acmemb(u,v)$
if null v then nil
  else if aceq(u,car v) then car v
  else acmemb(u,cdr v)$

procedure aceq(u,v)$
if null u then null v
  else if null v then nil
  else if car u member v then aceq(cdr u,delete(car u,v))
  else nil$

procedure matcheq(u,v)$
if null u or null v then nil
  else if numberp u then if u=v then list t else nil
  else if atom u then
    begin
      scalar x$
      x:=eval list(get(u,'matcheq),mkquote u,mkquote
                       (if atom v then list v else v))$
      return
        if x then x
          else if null !*exp and pairp v and car v memq
                  '(plus difference) then matchlinear(u,v)
          else nil
    end
  else if atom v then nil
  else if atom car u and car u eq car v then
      eval list(get(car u,'matcheq),mkquote cdr u,mkquote cdr v)
  else if null !*exp and car v memq'(plus difference)
       and car u eq 'diff then
      matchlinear(u,v)
  else nil$

algebraic operator matchplus$

fluid'(uu vv)$

procedure matchlinear(u,v)$
% Construction for OFF EXP and second and next coordinates
begin
  scalar x,uu,vv,alg$
  if not atom u then return
      if car u eq 'diff then matchlindf(u,v)
        else if car u eq 'times then matchlintimes(u,v)
        else nil$
  uu:=u$
  vv:='first$
  x:=formlnr list('matchplus,v,coord)$
  put('matchplus,'simpfn,'matchp)$
  alg:=alglist!*$
  alglist!*:=nil . nil$
  simp x$
  alglist!*:=alg$
  put('matchplus,'simpfn,'simpiden)$
  return
    if vv then list(u .  (if interpolp then v else vv))
      else nil
end$

procedure matchp y$
begin
  scalar x$
  if null vv then return(nil . 1)$
  x:=matcheq(uu,car y)$
  if null x then return
      begin
        vv:=nil$
        return(nil . 1)
      end$
  if vv eq 'first then return
      begin
        vv:=cdar x$
        return (nil . 1)
      end$
  if mainvareq(vv,cdar x) then return (nil . 1)$
  vv:=nil$
  return(nil . 1)
end$

unfluid '(uu vv)$

procedure mainvareq(x,y)$
if atom x then eq(x,y)
  else if car x memq iobjs!* then eq(car x,car y)
  else if car x memq '(diff expt) then
    (car y eq car x and mainvareq(cadr x,cadr y) and cddr x=cddr y)
  else nil$

procedure tlist x$
if atom x then list x
  else x$

procedure matchlindf(u,v)$
begin
  scalar x,y,b$
  x:=for each a in cdr v collect fsamedf a$
  y:=cdar x$
  if null y then return nil$
  x:=for each a in x collect if y=cdr a then car a
                               else b:=t$
  if b then return nil$
  x:=(car v . x) . y$
  return matchdf(cdr u,x)
end$

procedure fsamedf u$
begin
  scalar x$
  return
    if atom u then nil . nil
      else if car u eq 'minus then
        <<x:=fsamedf cadr u$
          list('minus,car x) . cdr x >>
      else if car u eq 'diff then cadr u . cddr u
      else if car u eq 'times then
        begin
          scalar y,z$
          x:=cdr u$
        a:if null x or y=t then go to b$
          if numberp car x then z:=car x . z
            else if eqcar(car x,'diff) then
              <<if y then y:=t
                  else y:=cddar x$
                z:=cadar x . z >>
            else if depends(car x,coord) then y:=t
            else z:=car x . z$
          x:=cdr x$
          go to a$
        b:return if y=t then nil . nil
                   else ('times . z) . y
        end
    else nil . nil
end$

procedure matchlintimes(u,v)$
begin
  scalar x,y,z$
  y:=cadr v$
  if eqcar(y,'times) then y:=cdr y
    else if eqcar(y,'minus) and eqcar(cadr y,'times)
     then y:=  (-1) . cdadr y
    else return nil$
  x:=for each a in cdr v collect
       if eqcar(a,'times) then <<y:=intersect(y,cdr a)$
                                 a>>
         else if eqcar(a,'minus) and eqcar(cadr a,'times) then
           <<y:=intersect(y,cdadr a)$
             'times . ((-1) . cdadr a) >>
         else y:=nil$
  if null y then return nil$
  x:=for each a in x collect <<z:=setdiff(cdr a,y)$
                               if null cdr z then car z
                                 else 'times . z >>$
  x:=car v . x$
  return matchtimes(cdr u,x . y)
end$

procedure intersect(u,v)$
if null u or null v then nil
  else if member(car u,v) then car u . intersect(cdr u,v)
  else intersect(cdr u,v)$

procedure matchu(u,v)$
if car v memq unvars or (!*eqfu and car v memq given!*) then list(u . v)
  else if car v eq 'diff and not(coord memq cddr v)
                and matcheq(u,tlist cadr v)
           then list(u . (car v . (tlist cadr v . cddr v)))
  else if car v eq 'times then
% Product can be inside brackets or in DIFF
    begin
      scalar x,b1,vv$
      x:=for each a in cdr v collect a$   % To allow RPLACA
      vv:=car v . x$
      b1:=0$
      while x and b1<2 do
        <<if depends(car x,coord) then
              <<b1:=b1+1$
                if atom car x then rplaca(x,list car x) >>$
          x:=cdr x >>$
      return if b1=0 or b1>1 then nil
               else (u . vv)
    end
  else nil$

put('u,'matcheq,'matchu)$
put('v,'matcheq,'matchu)$
put('w,'matcheq,'matchu)$

procedure matchf(u,v)$
if car v memq given!* then list(u . v)
  else if car v eq 'diff and not(coord memq cddr v)
                and matchf(u,tlist cadr v)
           then list(u . (car v . (tlist cadr v . cddr v)))
  else nil$

put('f,'matcheq,'matchf)$
put('g,'matcheq,'matchf)$

procedure matchx(u,v)$
if car v eq coord then list t
  else nil$

put('x,'matcheq,'matchx)$

procedure matchtimes(u,v)$
begin
  scalar bool,bo,x,y,y1,asl$
  x:=u$
a:y:=t . v$
d:bool:=nil$
  while not bool and cdr y do
    <<y:=cdr y$
      bool:=matcheq(car x,car y)>>$
  if null bool then go to b$
  bo:=bool$
c: if not atom bo and not atom car bo then y1:=atsoc(caar bo,asl)
    else y1 := nil$
  if y1 and not(y1=car bo) then go to d$
  bo:=cdr bo$
  if bo then go to c$
  v:=delete(car y,v)$
  x:=cdr x$
  asl:=union(bool,asl)$
  if x then go to a$
  if v then return nil$
  return asl$
b:return if null cdr v and cdr x then
             if y:=matcheq('times . x,car v) then union(asl,y)
               else nil
           else nil
end$

put('times,'matcheq,'matchtimes)$

procedure matchexpt(u,v)$
if fixp cadr u then
    if cadr u=cadr v then matcheq(car u,car v)
      else nil
  else if cadr u='n then
      begin
        scalar x$
        x:=matcheq(car u,car v)$
        return if x then (('n . cadr v) . x)
                 else nil
      end
  else nil$

put('expt,'matcheq,'matchexpt)$

procedure matchquot(u,v)$
begin
  scalar man,mad$
  return if(man:=matcheq(car u,car v))
              and (mad:=matcheq(cadr u,cadr v)) then
             union(man,mad)
           else nil
end$

put('quotient,'matcheq,'matchquot)$

procedure matchrecip(u,v)$
matcheq(car u,car v)$

put('recip,'matcheq,'matchrecip)$

procedure matchdf(u,v)$
begin
  scalar x,asl,y$
  asl:=matcheq(car u,car v)$
  if null asl then return nil$
  y:=x:=append(cdr v,nil)$
  while x and car x neq coord do x:=cdr x$
  if null x then return nil
    else if null cddr u then
             if null cdr x or idp cadr x then go to df1
                 else return nil
      else if cdr x and caddr u=cadr x then t
      else return nil$
  rplacd(x,cddr x)$
df1:y:=delete(coord,y)$
  if null y or null interpolp then return asl
%  !!! Be aware !!! in mixed derivations of product
    else return list(car u . ('diff . (cdar asl . y)))
end$

put('diff,'matcheq,'matchdf)$

procedure finddifm u$
begin
  scalar x,v,asl,eqfu,b,bfntwo,bftwo1$
  eqfu:=!*eqfu$
  if eqfu then !*eqfu:=nil$
a:x:=t . difml!*$
  bftwo1:=bfntwo$
  bfntwo:=nil$
  if !*eqfu then b:=t$
  while cdr x and not asl do
    <<x:=cdr x$
      asl:=matcheq(caar x,u)$
% Test for given variables of type F, which have to be on one grid,
% if choosed substitution from DIFMATCH contains definition of F
% on grids.
      if asl then for each a in asl do
          if not atom a and car a memq'(f g) and (cadr a memq novars or
             (null !*twogrid and cadr a memq sunvars)) and
             null atsoc(car a,caadar x) then bfntwo:=t$
      if bfntwo then asl:=nil >>$
  !*eqfu:=eqfu$
  if null asl then
      if null b and eqfu then go to a
    else go to nm$
  return list(('x . coord) . delete(t,asl),cdar x)$
nm:if eqcar(u,'times) and null !*exp then
       <<!*exp:=t$
         alglist!*:=nil . nil$
         v:=prepsq simp u$
         v:=formlnr list('intt,v,coord)$
         !*exp:=nil$
         if null atom v and car v memq'(plus difference) then
             return ('special . simp v) >>$
  msgpri(" Matching of ",u," term not find ",nil,'hold)$
  if bfntwo or bftwo1 then
       lprie(" Variable of type F not defined on grids in DIFMATCH")$
  return nil
end$

procedure tdifpair x$
% From CDR ATSOC(.,ASL) makes an atom - free variable
<<if eqcar(x,'diff) then
      if eqcar(cadr x,'times) then
          <<x:=cdadr x$
            while x and not depends(car x,coord) do x:=cdr x$
            x:=tdifpair car x>>   % patch
        else x:=cadr x$
  if pairp x then x:=car x$
  x >>$

procedure simpintt u$
begin
  scalar asl,agdsl,l,x,nv,y,x1,y1,nv1,n1,n2,nn1,nn2,
         x2,y2,nv2,n3,n4,n5,n6,lgrids,gds$
  u:=prepsq simp car u$
  if u=1
        then go to r$
  asl:=finddifm u$
  if null asl or eqcar(asl,'special) then go to r$
  agdsl:=cadr asl$              % List from DIFML!*
  asl:=car asl$                 % ASOC. list of assignments
  gds:=caar agdsl$
  l:=length gds$
  if l=0 then go to r$
a:y:=caar gds$
  x:=atsoc(y,asl)$
  if null x then go to er1$
  x:=tdifpair cdr x$
  if !*twogrid and flagp(x,'twogrid) then
      if l=1 then go to r
        else <<gds:=cdr gds$
               l:=l-1$
               go to a>>$
  nv:=ngetvar(x,'nrvar)$
  if l=1 then go to l1
    else go to l2$
l1:x:=assoc(list(y . 'one),agdsl)$
  if null x then go to er2$
  f2plus(nvar,nv,0,6*cadr x)$
  x:=assoc(list(y . 'half),agdsl)$
  if null x then go to er2$
  f2plus(nvar,nv,1,6*cadr x)$
  go to r$
l2:y1:=caadr gds$
  x1:=atsoc(y1,asl)$
  if null x1 then go to er1$
  x1:=tdifpair cdr x1$
  if !*twogrid and flagp(x1,'twogrid) then
      if l=2 then go to l1
        else <<gds:=cdr gds$
               l:=l-1$
               go to l2 >>$
  nv1:=ngetvar(x1,'nrvar)$
  lgrids:=get('difm!*,'grids)$
  if l=3 then go to l3
    else if l>3  then go to er$
l20:n1:=atsoc(acmemb(list(y . 'one,y1 . 'one),lgrids),agdsl)$
  n2:=atsoc(acmemb(list(y . 'one,y1 . 'half),lgrids),agdsl)$
  nn1:=atsoc(acmemb(list(y . 'half,y1 . 'half),lgrids),agdsl)$
  nn2:=atsoc(acmemb(list(y . 'half,y1 . 'one),lgrids),agdsl)$
  if n1 and n2 and nn1 and nn2 then t
    else go to er2$
  n1:=cadr n1$
  n2:=cadr n2$
  nn1:=cadr nn1$
  nn2:=cadr nn2$
l21:add2sint(nv,nv1,x,x1,n1,n2,nn1,nn2)$
  go to r$
l3:y2:=caaddr gds$
  x2:=atsoc(y2,asl)$
  if null x2 then go to er1$
  x2:=tdifpair cdr x2$
  if !*twogrid and flagp(x2,'twogrid) then go to l20$
  nv2:=ngetvar(x2,'nrvar)$
  n1:=atsoc(acmemb(list(y . 'one,y1 . 'one,y2 . 'one),lgrids),agdsl)$
  n2:=atsoc(acmemb(list(y . 'half,y1 . 'half,y2 . 'half),lgrids),agdsl)$
  nn1:=atsoc(acmemb(list(y . 'one,y1 . 'one,y2 . 'half),lgrids),agdsl)$
  nn2:=atsoc(acmemb(list(y . 'half,y1 . 'half,y2 . 'one),lgrids),agdsl)$
  n3:=atsoc(acmemb(list(y . 'one,y1 . 'half,y2 . 'one),lgrids),agdsl)$
  n4:=atsoc(acmemb(list(y . 'half,y1 . 'one,y2 . 'half),lgrids),agdsl)$
  n5:=atsoc(acmemb(list(y . 'one,y1 . 'half,y2 . 'half),lgrids),agdsl)$
  n6:=atsoc(acmemb(list(y . 'half,y1 . 'one,y2 . 'one),lgrids),agdsl)$
  if n1 and n2 and nn1 and nn2 and n3 and n4 and n5 and n6 then t
    else go to er2$
  n1:=cadr n1$
  n2:= cadr n2$
  nn1:=cadr nn1$
  nn2:=cadr nn2$
  n3:=cadr n3$
  n4:=cadr n4$
  n5:=cadr n5$
  n6:=cadr n6$
  if n1=nn1 and n2=nn2 and n3=n5 and n4=n6 then
      <<n2:=n3$
        nn1:=nn2$
        nn2:=n4$
        go to l21 >>
    else if n1=n3 and n2=n4 and nn1=n5 and nn2=n6 then
      <<n2:=nn1$
        nn1:=n4$
        x1:=x2$
        nv1:=nv2$
        go to l21 >>
    else if n1=n6 and n2=n5 and nn1=n4 and nn2=n3 then
      <<n2:=nn2$
        nn1:=n5:
        nn2:=n4$
        x:=x2$
        nv:=nv2$
        go to l21 >>$
  add3sint(nv,nv1,nv2,x,x1,x2,n1,n2,n3,n4,n5,n6,nn1,nn2)$
r:return (nil . 1)$
er:msgpri(nil,l," Free vars not yet implemented ",nil,'hold)$
  go to r$
er1:msgpri(" Failed matching of variables in  ",
        u,list(asl,agdsl),nil,'hold)$
  go to r$
er2:msgpri(" All grids not given for term  ",u,list(asl,agdsl),
                   nil,'hold)$
  go to r
end$

procedure add2sint(nv,nv1,x,x1,n1,n2,nn1,nn2)$
begin
% Enhansment for symmetries, when only one variable influence
  if n1=n2 and nn1=nn2 then
      <<f2plus(nvar,nv,0,6*n1)$
        f2plus(nvar,nv,1,6*nn1)$
        return >>
    else if n1=nn2 and n2=nn1 then
      <<f2plus(nvar,nv1,0,6*n1)$
        f2plus(nvar,nv1,1,6*n2)$
        return >>$
  n1:=3*n1$
  n2:=3*n2$
  nn1:=3*nn1$
  nn2:=3*nn2$
  x:=list(x,x1)$
  f2plus(nvar,nv,0,list(x,n1,n2))$
  f2plus(nvar,nv,1,list(x,nn1,nn2))$
  x:=reverse x$
  f2plus(nvar,nv1,0,list(x,n1,nn2))$
  f2plus(nvar,nv1,1,list(x,nn1,n2))$
  return
end$

procedure add3sint(nv,nv1,nv2,x,x1,x2,n1,n2,n3,n4,n5,n6,nn1,nn2)$
begin
  n1:=2*n1$
  n2:=2*n2$
  nn1:=2*nn1$
  nn2:=2*nn2$
  n3:=2*n3$
  n4:=2*n4$
  n5:=2*n5$
  n6:=2*n6$
  x:=list(x,x1,x2)$
  f2plus(nvar,nv,0,list(x,n1,nn1,n3,n5))$
  f2plus(nvar,nv,1,list(x,n2,nn2,n4,n6))$
  f2plus(nvar,nv1,0,list(x,n1,nn1,n4,n6))$
  f2plus(nvar,nv1,1,list(x,n2,nn2,n3,n5))$
  f2plus(nvar,nv2,0,list(x,n1,nn2,n3,n6))$
  f2plus(nvar,nv2,1,list(x,n2,nn1,n4,n5))$
  return
end$

procedure simpinterpol u$
begin
  scalar asl,agdsl,gds,x,y,xx,a$
  u:=prepsq simp car u$
  if eqcar(u,'diff) and not(coord memq cddr u) then
%  !!!! Be aware  !!!! could not work for mixed derivatives
      return <<asl:=!*exp$
               !*exp:=t$
               alglist!*:=nil . nil$              % added 13/06/91
               u:= simp formlnr('diff . (prepsq simp formlnr
                       list('intt,cadr u,coord) . cddr u))$
               !*exp:=asl$
               u>>$
  asl:=finddifm u$
  if null asl then return (nil . 1)
    else if eqcar(asl,'special) then return cdr asl$
  agdsl:=cadr asl$   %  Actual list from DIFML!*, contains definition
                     %  of grid, penalty and diff. scheme
  asl:=car asl$      %  Assoc. list of assignments of variables X,U,V,W
                     %  to actual variables
  if not(gvar memq grids!*) then go to erg$
  asl:=append(asl,get('steps,gvar))$   % Adding DIM1, DIP1 ... to assoc.
                                       % list
  if null caar agdsl then return simp sublap(asl,caddar agdsl)$  % For
  a:=caar agdsl$                       % DIFMATCH without def. grids
b:if null a then go to c$
  y:=caar a$
  x:=atsoc(y,asl)$
  if null x then go to er1$            % GDS is assoc. list of actual
  xx:=cdr x$                           % assignments of grids to
  x:=getgrid xx$                       % variables U, V
  if gvar eq 'half then x:=gnot x$
  if !*twogrid and twogridp xx then t
    else gds:=(y . x) . gds$
  a:=cdr a$
  go to b$
c:if null gds then go to a$      % For given functions which can be on
  y:=get('difm!*,'grids)$        % both grids
  x:=acmemb(gds,y)$              % Unique GDS
  if null x then go to er1$
  gds:=x$
a:x:=assoc(gds,agdsl)$
  if null x then go to erg$
  x:=caddr x$                    % Actual difference scheme
  return simp sublap(asl,x)$
er1:msgpri(" Failed matching of ",u,list(asl,agdsl,gds),nil,
                  'hold)$
  return (nil . 1)$
erg:msgpri(" Bad grids ",u,list(asl,agdsl,gds),nil,'hold)$
  return (nil . 1)
end$

procedure twogridp u$
% Checks if prefix form U can be on both grids
begin
  scalar x$
  return
    if atom u then
        if flagp(u,'twogrid) then
            if !*twogrid and u memq given!* and
               getel list(get(u,'grid),ncor) then nil
              else t
          else nil
      else if flagp(car u,'twogrid) then
        if !*twogrid and car u memq given!* and
           getel list(get(car u,'grid),ncor) then nil
          else t
      else if car u memq '(diff plus difference) then twogridp cadr u
      else if car u eq 'times then twogridpti cdr u
      else nil
end$

procedure twogridpti u$
begin
  scalar x$
a:x:=twogridp car u$
  if x then return x$
  u:=cdr u$
  if u then go to a$
  return nil
end$


procedure getgrid u$
begin
  scalar x$
  return
    if atom u then
        if x:=get(u,'xgrid) then x
          else if !*twogrid and u memq given!* and
                  (x:=getel list(get(u,'grid),ncor)) then car x
          else nil
      else if (x:=get(car u,'xgrid)) then x
      else if !*twogrid and car u memq given!* and
               (x:=getel list(get(car u,'grid),ncor)) then car x
      else if car u eq 'diff then
               if atom cadr u then getgrid cadr u
                %else if caadr u eq 'times then           % probably can
                %         if (x:=getgrid cadadr u) then x % be deleted
                %           else getgrid caddr cadr u     % !!!!!"!!!
                 else getgrid cadr u
      else if car u memq '(plus difference) then getgrid cadr u
      else if car u eq 'times then getgti cdr u
      else nil
end$

procedure getgti u$
begin
  scalar x$
a:x:=getgrid car u$
  if x then return x$
  u:=cdr u$
  if u then go to a$
  return nil
end$

procedure sublap(u,v)$
% U is assoc. list, V is pattern diff. scheme
% Performs substitution of assod. list into the diff. scheme
begin
  scalar x$
  return
    if null u or null v then v
      else if atom v then
        if numberp v then v
          else if x:=atsoc(v,u) then cdr x
          else v
      else if flagp(car v,'app) then sublap1(u,v)
      else (sublap(u,car v) . sublap(u,cdr v))
end$

flag('(u f v w x g),'app)$

procedure sublap1(u,v)$
begin
  scalar x,y$
  x:=atsoc(car v,u)$
  if null x then return msgpri(" Substitution for ",v," not find",
                               nil,'hold)$
  x:=cdr x$
  y:=for each a in cdr v collect irev sublap(u,a)$
  return
    if eqcar(x,'diff) then ('diff . (subappend(cadr x,y) . cddr x))
      else subappend(x,y)
end$

procedure subappend(x,y)$
if atom x then if x memq iobjs!* and depends(x,coord) then (x . y)
                 else x
  else if car x memq iobjs!* and depends(car x,coord) then append(x,y)
         else (car x . for each a in cdr x collect subappend(a,y) )$

procedure irev u$
begin
  u:=simp u$
  return
    if cdaaar u=1 and cdaar u=cdr u and fixp cdar u then
        if cdr u=1 then
            if cdar u<0 then list('difference,
                                   caaaar u,
                                   -cdar u)
               else list('plus,
                         caaaar u,
                         cdar u)
           else if cdar u<0 then list('difference,
                                      caaaar u,
                                      list('quotient,
                                           -cdar u,
                                           cdr u))
                  else list('plus,
                            caaaar u,
                            list('quotient,
                                 cdar u,
                                 cdr u))
      else prepsq u
end$

unfluid '(coord unvars sunvars interpolp novars ncor nvar intp icor gvar
        hi hip1 hip2 him1 him2 lhs rhs lsun lun xxgrid resar)$


procedure gentempst$
list('gentemp,xread t)$

put('gentemp,'stat,'gentempst)$
put('gentemp,'formfn,'formgentran)$

put('outtemp,'stat,'endstat)$
flag('(outtemp),'eval)$

endmodule;

end;
