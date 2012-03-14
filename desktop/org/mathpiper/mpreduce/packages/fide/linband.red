module linband;

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


% Version REDUCE 3.6     05/1991

% GENTRAN package has to be loaded prior to this module

global'(fortcurrind!* genstmtnum!* genstmtincr!*)$
fluid'(!*period)$            % declaration for 3.4

fluid'(!*imsl !*nag !*essl)$

switch imsl,nag,essl$
!*imsl:=nil$
!*nag:=nil$
!*essl:=nil$

procedure ison x$
if eval x then 1
  else 0$

operator ison$

if null getd 'gentempst then
procedure gentempst$
list('gentemp,xread t)$

global'(temp!*)$
temp!*:=nil$

procedure gentemp u$
<<temp!* := ((!*period . fortcurrind!*) . u) . temp!*$ nil>>$

put('gentemp,'stat,'gentempst)$
put('gentemp,'formfn,'formgentran)$
load!-package 'gentran;

procedure outtemp$
begin
  scalar period,fortind$
  period:=!*period$
  fortind:=fortcurrind!*$
  for each a in reverse temp!* do
    <<!*period:=caar a$
      fortcurrind!*:=cdar a$
      eval list('gentran,mkquote cdr a,nil)>>$
  temp!* := nil$
  !*period:=period$
  fortcurrind!*:=fortind$
  return nil
end$

put('outtemp,'stat,'endstat)$
flag('(outtemp),'eval)$

algebraic$

procedure genlinbandsol(nlc,nuc,system)$
% Generates FORTRAN program for solving of linear algebraic system
% of equations with band matrix with NLC lower codiagonals and NUC
% upper codiagonals.
begin
  scalar pvars,svars,vareq,fveq$
  %  PVARS  - list of variables before actual variable
  %  SVARS  - list of variables after actual variable
  %  VAREQ  - actual v-equation (list {variable equation})
  symbolic
    <<put('list,'evfno,get('list,'evfn))$
      put('list,'evfn,'listnoeval)$
      put('equal,'psopfno,get('equal,'psopfn))$
      put('equal,'psopfn,'equalaeval)>>$
  system:=expanddo(nlc,nuc,system)$
  vareq:=first system$
  pvars:={}$
  svars:=findsvars(nuc,vareq,system)$
  off period$
  gentran n:=1$
  gentemp n:=1$
  on period$
  ncol!*:=nlc+nuc+1$
  for i:=1:nlc do
    <<genvareq(pvars,svars,vareq,nlc,nlc-i+1,pfix!*)$
      pvars:=append(pvars,first vareq . {})$
      system:=nextvareqsys(vareq,system)$
      vareq:=first system$
      system:=rest system$
      gennp1()$
      svars:=findsvars(nuc,vareq,system) >>$
  while length svars=nuc do
    <<genvareq(pvars,svars,vareq,nlc,0,0)$
      pvars:=append(rest pvars,first vareq . {})$
      fveq:=first system$
      system:=nextvareqsys(vareq,system)$
      vareq:=first system$
      system:=rest system$
  % Get in and get out of loop
      if (ffst system=do) and (first vareq=first frrfst system and
          rest vareq=rest frrfst system) then
          pvars:=findpvars(nlc,first system)
        else if first fveq=do and not(ffst system=do) then
          pvars:=lastvars(nlc,fveq)$
      gennp1()$
      svars:=findsvars(nuc,vareq,system) >>$
  for i:=1:nuc do
    <<genvareq(pvars,svars,vareq,nlc,i,sfix!*)$
      pvars:=append(rest pvars,first vareq . {})$
      system:=nextvareqsys(vareq,system)$
      vareq:=first system$
      system:=rest system$
      if not(svars={}) then
          <<svars:=rest svars$
            gennp1() >> >>$
  off period$
  if ison !*imsl = 1 then pvars:=gencall!-imsl(nlc,nuc)
    else if ison !*nag = 1 then pvars:=gencall!-nag(nlc,nuc)
    else if ison !*essl= 1 then pvars:=gencall!-essl(nlc,nuc)
    else pvars:=gencall!-linpack(nlc,nuc)$
  on period$
  outtemp$
  symbolic <<put('list,'evfn,remprop('list,'evfno))$
             put('equal,'psopfn,remprop('equal,'psopfno))>>
end$

procedure gencall!-imsl (nlc,nuc)$
gentran
  <<literal tab!*,"call leqt1b(acof,n,",eval nlc,",",eval nuc,
              ",iacof,arhs,1,iarhs,0,xl,ier)",cr!*$
    literal "c  iacof is actual 1-st dimension of the acof array",cr!*$
    literal "c  iarhs is actual 1-st dimension of the arhs array",cr!*$
    literal "c  xl is working array with size n*(nlc+1)",cr!*$
    literal
       "c  where n is number of equations nlc number of lower",cr!*$
    literal "c  codiagonals",cr!*$
    literal
       "c  if ier=129( .ne.0) matrix acof is algorithmically singular",
        cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!*>>$

procedure gencall!-linpack(nlc,nuc)$
if ncol!*=3 and nlc=1 then gencall!-linpack!-trid(nlc,nuc)
  else gentran
  <<literal tab!*,"call dgbfa(acof,iacof,n,",eval nlc,",",eval nuc,
            ",ipvt,ier)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal "c  acof is array of dimension (iacof,p), p >= n",cr!*$
    literal "c     iacof >= ",eval(nlc+ncol!*),cr!*$
    literal "c  ipvt is array of dimension at least (n)",cr!*$
    literal "c  if (ier.ne.0) matrix acof is algorithmically singular",
              cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!*$
    literal tab!*,"call dgbsl(acof,iacof,n,",eval nlc,",",eval nuc,
            ",ipvt,arhs,0)",cr!*>>$

procedure gencall!-linpack!-trid(nlc,nuc)$
gentran
  <<literal tab!*,"call dgtsl(n,alcd,ad,aucd,arhs,ier)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal
      "c  alcd,ad,aucd,arhs are arrays of dimension at least (n)",cr!*$
    literal "c  if (ier.ne.0) matrix acof is algorithmically singular",
              cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!* >>$

procedure gencall!-essl(nlc,nuc)$
if ncol!*=3 and nlc=1 then gencall!-essl!-trid(nlc,nuc)
  else gentran
  <<literal tab!*,"call dgbf(acof,iacof,n,",eval nlc,",",eval nuc,
            ",ipvt)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal "c  acof and arhs are double precision type",cr!*$
    literal "c  for single precision change dgbf to sgbf and ",
            "dgbs to sgbs",cr!*$
    literal "c  acof is array of dimension (iacof,p), p >= n",cr!*$
    literal "c     iacof >= ",eval(nlc+ncol!*+15),cr!*$
    literal "c  arhs is array of dimension at least (n)",cr!*$
    literal "c  ipvt is integer array of dimension at least (n)",cr!*$
    literal tab!*,"call dgbs(acof,iacof,n,",eval nlc,",",eval nuc,
            ",ipvt,arhs)",cr!*>>$

procedure gencall!-essl!-trid(nlc,nuc)$
gentran
  <<literal tab!*,"call dgtf(n,alcd,ad,aucd,af,ipvt)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal
    "c  alcd,ad,aucd,af,arhs are arrays of dimension at least (n)",cr!*$
    literal "c  these arrays are double precision type",cr!*$
    literal "c  for single precision change dgtf to sgtf and ",
            "dgts to sgts",cr!*$
    literal
    "c  ipvt is integer array of dimension at least (n+3)/8",cr!*$
    literal tab!*,"call dgts(n,alcd,ad,aucd,af,ipvt,arhs)",cr!* >>$

procedure gencall!-nag(nlc,nuc)$
if ncol!*=3 and nlc=1 then gencall!-nag!-trid(nlc,nuc)
  else gentran
  <<ier:=0$
    literal tab!*,"call f01lbf(n,",eval nlc,",",eval nuc,
         ",acof,iacof,al,ial,in,iv,ier)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal "c  acof is array of dimension (iacof,p), p >= n",cr!*$
    literal "c      iacof >= min(n,",eval ncol!*,")",cr!*$
    literal "c  al is array of dimension (ial,p), p >= n",cr!*$
    literal "c      ial >= max(1,",eval nlc,")",cr!*$
    literal "c  in is integer array of dimension at least (n)",cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!*$
    literal tab!*,"call f04ldf(n,",eval nlc,",",eval nuc,
            ",1,acof,iacof,al,ial,in,arhs,iarhs,ier)",cr!*$
    literal "c  arhs is array of dimension (iarhs), iarhs >= n",cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!* >>$

procedure gencall!-nag!-trid(nlc,nuc)$
gentran
  <<ier:=0$
    literal tab!*,
          "call f01lef(n,ad,0.,aucd,alcd,1.e-10,au2cd,in,ier)",cr!*$
    literal "c  n is number of equations",cr!*$
    literal
"c  alcd,ad,aucd,au2cd,arhs are arrays of dimension at least (n)",cr!*$
    literal "c  in is integer array of dimension at least (n)",cr!*$
    literal tab!*,"if(ier.ne.0 .or. in(n).ne.0) call errout",cr!*$
    literal tab!*,
         "call f04lef(1,n,ad,aucd,alcd,au2cd,in,arhs,0.,ier)",cr!*$
    literal tab!*,"if(ier.ne.0) call errout",cr!* >>$

procedure gennp1$
<<off period$
  gentran n:=n+1$
  gentemp n:=n+1$
  on period >>$

% Definition of operator SUBE

symbolic$

symbolic procedure simpsube u$
begin
  scalar x$
a:if null cdr u then go to d
    else if null eqexpr car u then errpri2(car u,t)$
  x:=list('equal,reval cadar u,caddar u) . x$
  u:=cdr u$
  go to a$
d:x:=reverse(car u . x)$
  x:=subeval x$
  return x
end$

symbolic put('sube,'psopfn,'simpsube)$

algebraic$

% Procedures FFRRST etc.

procedure ffst u$
first first u$
procedure frst u$
first rest u$
procedure rfst u$
rest first u$
procedure rrst u$
rest rest u$

procedure fffst u$
first ffst u$
procedure ffrst u$
first frst u$
procedure frfst u$
first rfst u$
procedure frrst u$
first rrst u$
procedure rffst u$
rest ffst u$
procedure rfrst u$
rest frst u$
procedure rrfst u$
rest rfst u$
procedure rrrst u$
rest rrst u$

procedure ffffst u$
ffst ffst u$
procedure fffrst u$
ffst frst u$
procedure ffrfst u$
ffst rfst u$
procedure ffrrst u$
ffst rrst u$
procedure frffst u$
frst ffst u$
procedure frfrst u$
frst frst u$
procedure frrfst u$
frst rfst u$
procedure frrrst u$
frst rrst u$
procedure rfffst u$
rfst ffst u$
procedure rffrst u$
rfst frst u$
procedure rfrfst u$
rfst rfst u$
procedure rfrrst u$
rfst rrst u$
procedure rrffst u$
rrst ffst u$
procedure rrfrst u$
rrst frst u$
procedure rrrfst u$
rrst rfst u$
procedure rrrrst u$
rrst rrst u$

procedure findsvars(nuc,vareq,system)$
% Looks for NUC next variables in SYSTEM
% VAREQ is actual v-equation
if ffst system=do then findsvarsdo(nuc,vareq,first system)
  else findsvars1(nuc,rest system)$

procedure findsvars1(nuc,system)$
% Substitutes values for loop variable
if nuc=0 or system={} then {}
  else if ffst system=do then fsvars1do(nuc,first system)
  else ffst system . findsvars1(nuc-1,rest system)$

procedure fsvars1do(nuc,cykl)$
% Substitutes into the loop CYKL
begin
  scalar id,from,step,syst,x,y$
  cykl:=rest cykl$
  syst:=first cykl$
  id:=first syst$
  from:=frst syst$
  step:=frrrst syst$
  syst:=rest cykl$
  x:={}$
a:y:=sube(id=from,ffst syst)$
  x:=y . x$
  nuc:=nuc-1$
  if nuc=0 then go to r$
  syst:=rest syst$
  if not(syst={}) then go to a$
  syst:=rest cykl$
  from:=from+step$
  go to a$
r:x:=reverse x$
  return x
end$

procedure findsvarsdo(nuc,vareq,cykl)$
% Does not substitute for loop variable, only increases it
% by STEP if it is necessary
begin
  scalar id,add1,step,syst,x,y$
  cykl:=rest cykl$
  syst:=first cykl$
  id:=first syst$
  step:=frrrst syst$
  syst:=rest cykl$
  while not(first vareq=ffst syst and rest vareq=rfst syst)
     do syst:=rest syst$
  syst:=rest syst$
  add1:=0$
  x:={}$
a:if syst={} then go to b$
  y:=sube(id=id+add1,ffst syst)$
  x:=y . x$
  nuc:=nuc-1$
  if nuc=0 then go to r$
  syst:=rest syst$
  go to a$
b:syst:=rest cykl$
  add1:=add1+step$
  go to a$
r:x:=reverse x$
  return x
end$

procedure expanddo(nlc,nuc,system)$
% Every loop in SYSTEM is expanded so that more than or equal to
% NLC first elements and more than or equal NUC last elements are
% excluded from the loop, and changes the parameters of loop so
% that its meaning remains the same
begin
  scalar x$
  x:={}$
a:if system={} then go to r$
  if ffst system=do then x:=append(expddo(nlc,nuc,first system),x)
    else x:=first system . x$
  system:=rest system$
  go to a$
r:x:=reverse x$
  return x
end$

procedure expddo(nlc,nuc,cykl)$
% Performs the expansion of the loop CYKL - returns reverse list
begin
  scalar id,from,to1,step,syst,lsyst,ns,x,y,bn$
  cykl:=rest cykl$
  syst:=first cykl$
  id:=first syst$
  from:=frst syst$
  to1:=frrst syst$
  step:=frrrst syst$
  syst:=rest cykl$
  lsyst:=length syst$
  ns:=quotient1(nlc,lsyst)$
  if nlc>ns*lsyst then ns:=ns+1$
  bn:=0$
  x:={}$
a:y:=sube(id=from,ffst syst) . sube(id=from,frfst syst) . {}$
  x:=y . x$
  syst:=rest syst$
  if not(syst={}) then go to a$
  ns:=ns-1$
  from:=from+step$
  if ns=0 then go to b$
  syst:=rest cykl$
  go to a$
b:if bn=1 then go to r$
  syst:=rest cykl$
  ns:=quotient1(nuc,lsyst)$
  if nuc>ns*lsyst then ns:=ns+1$
  to1:=to1-ns*step$
  y:=do . (id . from . to1 . step . {}) . syst$
  x:=y . x$
  from:=to1+step$
  bn:=1$
  go to a$
r:return x
end$

symbolic procedure quotient1(u,v)$
quotient(u,v)$

symbolic operator quotient1$
operator acof,arhs$

procedure genvareq(pvars,svars,vareq,nlc,nzero,mode)$
if ison !*imsl = 1 then
       genvareq!-imsl(pvars,svars,vareq,nlc,nzero,mode)
  else if ison !*nag = 1 then
       genvareq!-nag(pvars,svars,vareq,nlc,nzero,mode)
  else genvareq!-linpack(pvars,svars,vareq,nlc,nzero,mode)$

procedure genvareq!-imsl(pvars,svars,vareq,nlc,nzero,mode)$
% Generates N-th row of coeff. matrix ACOF and right hand side ARHS
% according to the v-equation VAREQ.
% NZERO is number of zeroes before or after (according to MODE).
% Matrix ACOF is transformed to IMSL band matrix storage.
begin
  integer j$
  scalar var,rhside,lhside,x,y$
  if not(length pvars + length svars+1+nzero=ncol!*) then return
      write" Unconsistent PVARS:",pvars," SVARS:",svars," NZERO:",nzero$
  var:=first vareq$
  vareq:=frst vareq$
  rhside:=rhs vareq$
  lhside:=lhs vareq$
  j:=1$
  x:=0$
  if mode=pfix!* then while j<=nzero do
      <<gentran acof(n,eval j):=0$
        j:=j+1 >>$
  for each a in pvars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(n,eval j):=:y$
      j:=j+1>>$
  y:=lincof(lhside,var)$
  x:=x+var*y$
  gentran acof(n,eval j):=:y$
  j:=j+1$
  for each a in svars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(n,eval j):=:y$
      j:=j+1>>$
  if mode=sfix!* then while j<=ncol!* do
      <<gentran acof(n,eval j):=0$
        j:=j+1 >>$
  gentran arhs(n):=:rhside$
  gentemp eval(var):=arhs(n)$
  if not(x-lhside=0) then write " For equation ",vareq," given only ",
      "variables ",pvars,svars,var$
  return
end$

procedure genvareq!-linpack(pvars,svars,vareq,nlc,nzero,mode)$
% Generates N-th row of coeff. matrix ACOF and right hand side ARHS
% according to the v-equation VAREQ.
% NZERO is number of zeroes before or after (according to MODE).
% Matrix ACOF is transformed to LINPACK band matrix storage.
% NCOL!* is the band width.
begin
  integer j,jj,nn$
  scalar var,rhside,lhside,x,y$
  if not(length pvars + length svars+1+nzero=ncol!*) then return
      write" Unconsistent PVARS:",pvars," SVARS:",svars," NZERO:",nzero$
  if nlc=1 and ncol!*=3 then return
         genvareq!-linpack!-trid(pvars,svars,vareq,nlc,nzero,mode)$
  var:=first vareq$
  vareq:=frst vareq$
  rhside:=rhs vareq$
  lhside:=lhs vareq$
  j:=n-nlc$
  jj:=1$
  nn:=ncol!*+nlc$
  x:=0$
  if mode=pfix!* then while jj<=nzero do
      <<nn:=nn-1$
        jj:=jj+1$
        j:=j+1 >>$
  for each a in pvars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(nn,j)::=:y$
      nn:=nn-1$
      j:=j+1>>$
  y:=lincof(lhside,var)$
  x:=x+var*y$
  gentran acof(nn,j)::=:y$
  nn:=nn-1$
  j:=j+1$
  for each a in svars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(nn,j)::=:y$
      nn:=nn-1$
      j:=j+1>>$
  gentran arhs(n):=:rhside$
  gentemp eval(var):=arhs(n)$
  if not(x-lhside=0) then write " For equation ",vareq," given only ",
      "variables ",pvars,svars,var$
  return
end$

procedure genvareq!-linpack!-trid(pvars,svars,vareq,nlc,nzero,mode)$
begin
  scalar var,rhside,lhside,x,y$
  var:=first vareq$
  vareq:=frst vareq$
  rhside:=rhs vareq$
  lhside:=lhs vareq$
  x:=0$
  for each a in pvars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran alcd(n):=:y >>$
  y:=lincof(lhside,var)$
  x:=x+var*y$
  gentran ad(n):=:y$
  for each a in svars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran aucd(n):=:y >>$
  gentran arhs(n):=:rhside$
  gentemp eval(var):=arhs(n)$
  if not(x-lhside=0) then write " For equation ",vareq," given only ",
      "variables ",pvars,svars,var$
  return
end$

procedure genvareq!-nag(pvars,svars,vareq,nlc,nzero,mode)$
% Generates N-th row of coeff. matrix ACOF and right hand side ARHS
% according to the v-equation VAREQ.
% NZERO is number of zeroes before or after (according to MODE).
% Matrix ACOF is transformed to NAG band matrix storage.
% NCOL!* is the band width.
begin
  integer j$
  scalar var,rhside,lhside,x,y$
  if not(length pvars + length svars+1+nzero=ncol!*) then return
      write" Unconsistent PVARS:",pvars," SVARS:",svars," NZERO:",nzero$
  if nlc=1 and ncol!*=3 then return
         genvareq!-nag!-trid(pvars,svars,vareq,nlc,nzero,mode)$
  var:=first vareq$
  vareq:=frst vareq$
  rhside:=rhs vareq$
  lhside:=lhs vareq$
  j:=1$
  x:=0$
  for each a in pvars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(eval j,n):=:y$
      j:=j+1>>$
  y:=lincof(lhside,var)$
  x:=x+var*y$
  gentran acof(eval j,n):=:y$
  j:=j+1$
  for each a in svars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran acof(eval j,n):=:y$
      j:=j+1>>$
  gentran arhs(n):=:rhside$
  gentemp eval(var):=arhs(n)$
  if not(x-lhside=0) then write " For equation ",vareq," given only ",
      "variables ",pvars,svars,var$
  return
end$

procedure genvareq!-nag!-trid(pvars,svars,vareq,nlc,nzero,mode)$
begin
  scalar var,rhside,lhside,x,y$
  var:=first vareq$
  vareq:=frst vareq$
  rhside:=rhs vareq$
  lhside:=lhs vareq$
  x:=0$
  for each a in pvars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran alcd(n):=:y >>$
  y:=lincof(lhside,var)$
  x:=x+var*y$
  gentran ad(n):=:y$
  for each a in svars do
    <<y:=lincof(lhside,a)$
      x:=x+a*y$
      gentran aucd(n+1):=:y >>$
  gentran arhs(n):=:rhside$
  gentemp eval(var):=arhs(n)$
  if not(x-lhside=0) then write " For equation ",vareq," given only ",
      "variables ",pvars,svars,var$
  return
end$

procedure lincof(expre,ker)$
% Expression EXPRE is linear in kernel KER.
% Returns coeff. of KER in EXPRE.
(expre-sube(ker=0,expre))/ker$

stackdolabel!*:={}$

procedure nextvareqsys(vareq,system)$
% Looks for the next v-equation. Returns the new v-equation . SYSTEM.
% During get into the loop generates the beginning of the loop,
% during get out of the loop generates end of the loop.
if rest system={} then {} . {}
  else if ffst system=do then nextvesdo(vareq,system)
  else if ffrst system=do then nextvesdofst(rest system)
  else frst system . rest system$

procedure nextvesdofst(system)$
% Get into the loop
begin
  scalar id,from,to1,step$
  id:=frfst system$
  from:=frst id$
  to1:=frrst id$
  step:=frrrst id$
  id:=first id$
  genstmtnum!*:=genstmtnum!*+genstmtincr!*$
  gentran literal tab!*,"do ",eval(genstmtnum!*)," ",eval(id),"=",
          eval(from),",",eval(to1),",",eval(step),cr!*$
  stackdolabel!*:=genstmtnum!* . stackdolabel!*$
  genstmtnum!*:=genstmtnum!*+genstmtincr!*$
  gentemp <<literal tab!*,"do ",eval(genstmtnum!*)," ",
            eval(id),"=",eval(from),
          ",",eval(to1),",",eval(step),cr!*>>$
  fortcurrind!*:=fortcurrind!* + 4$
  stackdolabel!*:=genstmtnum!* . stackdolabel!*$
  id:=frrfst system . system$
  return id
end$

procedure nextvesdo(vareq,system)$
% SYSTEM begins with a loop - test on the end of loop.
% Suppose that after the loop cannot be another loop, which
% follows from EXPANDDO.
begin
  scalar vareqs$
  vareqs:=rrfst system$
  while not(first vareq=ffst vareqs and rest vareq=rfst vareqs) and
        not(rest vareqs={}) do vareqs:=rest vareqs$
  vareqs:=rest vareqs$
  if vareqs={} then
    % end of loop
      <<fortcurrind!* := fortcurrind!* - 4$
        gentemp<<literal eval first stackdolabel!*,tab!*,"continue",
                 cr!*>>$
        stackdolabel!*:=rest stackdolabel!*$
        gentran literal eval first stackdolabel!*,tab!*,"continue",cr!*$
        stackdolabel!*:=rest stackdolabel!*$
        vareqs:=frst system . rest system >>
    else vareqs:=first vareqs . system$
  return vareqs
end$

procedure findpvars(nlc,cykl)$
% Looks for NLC previous variables during geting into the loop
begin
  scalar id,step$
  id:=frst cykl$
  step:=frrrst id$
  id:=first id$
  cykl:=reverse rrst cykl$
  id:=reverse fsvars1do(nlc,
                       do . (id . (id-step) . 0 . (-step) . {}) . cykl)$
  return id
end$

procedure lastvars(nlc,cykl)$
% Looks for the NLC last variables of the loop CYKL
begin
  scalar id,step,to1$
  id:=frst cykl$
  to1:=frrst id$
  step:=frrrst id$
  id:=first id$
  cykl:=reverse rrst cykl$
  id:=reverse fsvars1do(nlc,do . (id . to1 . 0 . (-step) . {}) . cykl)$
  return id
end$

symbolic$
flag('(ffst frst rfst rrst fffst ffrst frfst frrst rffst rfrst rrfst
     rrrst ffffst fffrst ffrfst ffrrst frffst frfrst frrfst frrrst
     rfffst rffrst rfrfst rfrrst rrffst rrfrst rrrfst rrrrst
     findsvars findsvars1 fsvars1do findsvarsdo expanddo expddo
     genvareq nextvareqsys nextvesdofst nextvesdo findpvars lastvars),
     'noval)$

procedure equalaeval u$
'equal . aevlis u$

procedure aevlis u$
for each a in u collect aeval a$

procedure listnoeval(u,v)$
if atom u then listnoeval(cadr get(u,'avalue),v)
  else u$

endmodule;

end;
