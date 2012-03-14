module glexconv;% Newbase - algorithm :
% Faugere,Gianni,Lazard,Mora .

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


flag('(gvarslast),'share);

switch groebfac,trgroeb;

% Variables for counting and numbering .
fluid '(pcount!*);

fluid '(glexmat!*);% Matrix for the indirect lex ordering .

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    Interface functions .

% Parameters;
%      glexconvert(basis,[vars],[maxdeg=n],[newvars={x,y,..}]) .

symbolic procedure glexconverteval u;
begin scalar !*groebfac,!*groebrm,!*factor,!*gsugar,
  v,bas,vars,maxdeg,newvars,!*exp;!*exp:=t;
 u:=for each p in u collect reval p;
 bas:=car u;u:=cdr u;
 while u do
 << v:=car u;u:=cdr u;
    if eqcar(v,'list)and null vars then vars:=v
     else if eqcar(v,'equal)then
      if(v:=cdr v)and eqcar(v,'maxdeg)then maxdeg:=cadr v
       else if eqcar(v,'newvars)then newvars:=cadr v
        else << prin2(car v);
                rerror(groebnr2,4,"glexconvert, keyword unknown")>>
       else rerror(groebnr2,5,
                       "Glexconvert, too many positional parameters")>>;
 return glexbase1(bas,vars,maxdeg,newvars)end;

put( 'glexconvert,'psopfn,'glexconverteval);

symbolic procedure glexbase1(u,v,maxdeg,nv);
begin scalar vars,w,nd,oldorder,!*gcd,!*ezgcd,!*gsugar;
 integer pcount!*;!*gcd:=t;
 w:=for each j in groerevlist u
  collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebnr2,6,"Empty list in Groebner");
 vars:=groebnervars(w,v);
 !*vdpinteger:=!*vdpmodular:=nil;
 if not flagp(dmode!*,'field)then !*vdpinteger:=t
  else if !*modular then !*vdpmodular:=t;
 if null vars then vdperr 'groebner;
 oldorder:=vdpinit vars;
                  % Cancel common denominators .
 w:=for each j in w collect reorder numr simp j;
                  % Optimize varable sequence if desired .
 w:=for each j in w collect f2vdp j;
 for each p in w do nd:=nd or not vdpcoeffcientsfromdomain!? p;
 if nd then
 << !*vdpmodular:= nil;!*vdpinteger:=t;glexdomain!*:=2 >>
  else  glexdomain!*:=1;
 if glexdomain!*=1 and not !*vdpmodular then !*ezgcd:=t;
 if null maxdeg then maxdeg:=200;
 if nv then nv:=groerevlist nv;
 if null nv then nv:=vars else
  for each x in nv do if not member(x,vars) then
  << rerror(groebnr2,7,{ "new variable ",x,
                           " is not a basis variable" })>>;
 u:=for each v in nv collect a2vdp v;
 gbtest w;
 w:=glexbase2(w,u,maxdeg);
 w:='list.for each j in w collect prepf j;
 setkorder oldorder;
 gvarslast:='list.vars;return w end;

fluid '(glexeqsys!* glexvars!* glexcount!* glexsub!*);

symbolic procedure glexbase2(oldbase,vars,maxdeg);
% In contrast to documented algorithm monbase ist a list of
% triplets(mon . cof . vect)
% such that cof * mon== vect modulo oldbase
%(cof is needed because of the integer algoritm).
begin scalar lexbase,staircase,monbase;
 scalar monom,listofnexts,vect,q,glexeqsys!*,glexvars!*,glexsub!*;
 integer n;
 if not groezerodim!?(oldbase,length vars)then
  prin2t "####### warning: ideal is not zerodimensional ######";
             % Prepare matrix for the indirect lex ordering .
  glexmat!*:=for each u in vars collect vdpevlmon u;
  monbase:=staircase:=lexbase:=nil;
  monom:=a2vdp 1;listofnexts:=nil;
  while not(monom=nil)do
  << if not glexmultipletest(monom,staircase)then
     << vect:=glexnormalform(monom,oldbase);
        q:=glexlinrel(monom,vect,monbase);
        if q then
        << lexbase:=q . lexbase;maxdeg:=nil;
           staircase:=monom . staircase >>
           else
           << monbase:=glexaddtomonbase(monom,vect,monbase);
             n:=n #+1;
             if maxdeg and n#> maxdeg then
              rerror(groebnr2,8,"No univar. polynomial within degree bound");
             listofnexts:=glexinsernexts(monom,listofnexts,vars)>> >>;
     if null listofnexts then monom:=nil
      else << monom:=car listofnexts;listofnexts:=cdr listofnexts >>
 >>;return lexbase end;

symbolic procedure glexinsernexts(monom,l,vars);
begin scalar x;
 for each v in vars do
 << x:=vdpprod(monom,v);
   if not vdpmember(x,l)then
   << vdpputprop(x,'factor,monom);
      vdpputprop(x,'monfac,v);
      l:=glexinsernexts1(x,l)>> >>;return l end;

symbolic procedure glexmultipletest(monom,staircase);
 if null staircase then nil
  else if vevmtest!?(vdpevlmon monom,vdpevlmon car staircase)
        then t
    else glexmultipletest(monom,cdr staircase);

symbolic procedure glexinsernexts1(m,l);
 if null l then list m
  else if glexcomp(vdpevlmon m,vdpevlmon car l)then m . l
        else car l . glexinsernexts1(m,cdr l);

symbolic procedure glexcomp(ev1,ev2);
% True if ev1 is greater than ev2;
% we use an indirect ordering here(mapping via newbase variables) .
 glexcomp0(glexcompmap(ev1,glexmat!*), glexcompmap(ev2,glexmat!*));

symbolic procedure glexcomp0(ev1,ev2);
 if null ev1 then nil
  else if null ev2 then glexcomp0(ev1,'(0))
        else if(car ev1 #- car ev2)=0
              then glexcomp0( cdr ev1,cdr ev2)
               else if car ev1 #< car ev2 then t else nil;

symbolic procedure glexcompmap(ev,ma);
 if null ma then nil
  else glexcompmap1(ev,car ma). glexcompmap(ev,cdr ma);

symbolic procedure glexcompmap1(ev1,ev2);
% The dot product of two vectors .
 if null ev1 or null ev2 then 0
  else(car ev1 #* car ev2)#+ glexcompmap1(cdr ev1,cdr ev2);

symbolic procedure glexaddtomonbase(monom,vect,monbase);
% Primary effect:(monom . vect) . monbase;
% Secondary effect: builds the equation system .
begin scalar x;
 if null glexeqsys!* then
 << glexeqsys!*:=a2vdp 0;glexcount!*:=-1 >>;
 x:=mkid('gunivar,glexcount!*:=glexcount!*+1);
 glexeqsys!*:=vdpsum(glexeqsys!*,vdpprod(a2vdp x,cdr vect));
 glexsub!*:=(x .(monom . vect)) . glexsub!*;
 glexvars!*:=x . glexvars!*;
 return(monom . vect). monbase end;

symbolic procedure glexlinrelold(monom,vect,monbase);
 if monbase then
  begin scalar sys,sub,auxvars,r,v,x;
   integer n;
   v:=cdr vect;
   for each b in reverse monbase do
   << x:=mkid('gunivar,n);n:=n + 1;
    v:=vdpsum(v,vdpprod(a2vdp x,cddr b));
    sub:=( x . b). sub;
    auxvars:=x . auxvars >>;
   while not vdpzero!? v do
   << sys:=vdp2f vdpfmon(vdplbc v,nil). sys;v:=vdpred v >>;
   x:=sys;sys:=groelinsolve(sys,auxvars);
   if null sys then return nil;
          % Construct the lex polynomial .
   if !*trgroeb then prin2t "======= constructing new basis polynomial";
   r:=vdp2f vdpprod(monom,car vect)./ 1;
   for each s in sub do
    r:= addsq(r,multsq(vdp2f vdpprod(cadr s,caddr s)./ 1,
                            cdr assoc(car s,sys)));
   r:=vdp2f vdpsimpcont f2vdp numr r;return r end;

symbolic procedure glexlinrel(monom,vect,monbase);
 if monbase then
  begin scalar sys,r,v,x;
   v:=vdpsum(cdr vect,glexeqsys!*);
   while not vdpzero!? v do
   << sys:=vdp2f vdpfmon(vdplbc v,nil). sys;v:=vdpred v >>;
   x:=sys;sys:=groelinsolve(sys,glexvars!*);
   if null sys then return nil;
   r:=vdp2f vdpprod(monom,car vect)./ 1; % Construct the lex polynomial.
   for each s in glexsub!* do
    r:= addsq(r,multsq(vdp2f vdpprod(cadr s,caddr s)./ 1,
                            cdr assoc(car s,sys)));
   r:=vdp2f vdpsimpcont f2vdp numr r;
   return r end;

symbolic procedure glexnormalform(m,g);
% Reduce 'm' wrt basis 'g';
% the reduction product is preserved in m for later usage .
 begin scalar cof,vect,r,f,fac1;
  if !*trgroeb then prin2t "======= reducing ";
  fac1:=vdpgetprop(m,'factor);
  if fac1 then vect:=vdpgetprop(fac1,'vector);
  if vect then
  <<f:=vdpprod(cdr vect,vdpgetprop(m,'monfac));cof:=car vect>>
  else
  <<f:=m;cof:= a2vdp 1 >>;
  r:=glexnormalform1(f,g,cof);
  vdpputprop(m,'vector,r);
  if !*trgroeb then
  <<vdpprint vdpprod(car r,m);prin2t "=====> ";vdpprint cdr r>>;return r end;


symbolic procedure glexnormalform1(f,g,cof);
begin scalar f1,c,vev,divisor,done,fold,a,b;
 fold:=f;f1:=vdpzero();a:= a2vdp 1;
 while not vdpzero!? f do
 begin vev:=vdpevlmon f;c:=vdplbc f;
  divisor:=groebsearchinlist(vev,g); if divisor then done:=t;
  if divisor then
   if !*vdpinteger then
   <<f:=groebreduceonestepint(f,a,c,vev,divisor); b:=secondvalue!*;
    cof:=vdpprod(b,cof); if not vdpzero!? f1 then f1:=vdpprod(b,f1)>>
     else f:=groebreduceonesteprat(f,nil,c,vev,divisor)
    else
     <<f1:=vdpappendmon(f1,vdplbc f,vdpevlmon f);f:=vdpred f>>end;
  if not done then return cof.fold;
  f:=groebsimpcont2(f1,cof);cof:=secondvalue!*; return cof.f end;


symbolic procedure groelinsolve(equations,xvars);
(begin scalar r,q,test,oldmod,oldmodulus;
  if !*trgroeb then prin2t "======= testing linear dependency ";
  r:=t;
  if not !*modular and glexdomain!*=1 then
  <<oldmod:=dmode!*;
   if oldmod then setdmode(get(oldmod,'dname), nil);
   oldmodulus:=current!-modulus;
   setmod list 16381;%=2**14-3
   setdmode('modular,t);
   r:=groelinsolve1(for each u in equations collect numr simp prepf u,xvars);
   setdmode('modular,nil);
   setmod{oldmodulus};
   if oldmod then setdmode(get(oldmod,'dname),t);
   >> where !*ezgcd=nil;
  if null r then return nil;
  r:=groelinsolve1(equations,xvars);
  if null r then return nil;
         % Divide out the common content .
  for each s in r do if not(denr cdr s=1)then test:=t;
  if test then return r;
  q:=numr cdr car r;
% for each s in cdr r do
%  if q neq 1 then
%   q:=gcdf!*(q,numr cdr s);
% if q=1 then return r;
% r:=for each s in r collect
%  car s .(quotf(numr cdr s,q)./ 1);
  return r end)where !*ezgcd=!*ezgcd;% Stack old value.

symbolic procedure groelinsolve1(equations,xvars);
% Gaussian elimination in integer mode;
% free of unexact divisions(see Davenport et al,CA,pp 86 - 87
% special cases: trivial equations are ruled out early.
% INPUT:
% equations:     List of standard forms.
% xvars:
% OUTPUT:
% list of pairs(var.solu) where solu is a standard quotient.
% Internal data structure: standard forms as polynomials invars.
begin scalar oldorder,x,p,solutions,val,later,break,gc,field;
 oldorder:=setkorder xvars;
 field:=dmode!* and flagp(dmode!*,'field);
 equations:=for each eqa in equations collect reorder eqa;
 for each eqa in equations do
  if eqa and domainp eqa then break:= t;
 if break then goto empty;
 equations:=sort(equations,function grloelinord);
again: break:=nil;
 for each eqa in equations do if not break then
     % First step: eliminate equations of type 23=0 and 17 * u=0
     %                                    and 17 * u + 22=0.
 <<if null eqa then equations:=delete(eqa,equations)
    else if domainp eqa then break:=t  % Inconsistent system .
     else if not member(mvar eqa,xvars)then break:=t
      else if domainp red eqa or not member(mvar red eqa,xvars)then
      <<equations:=delete(eqa,equations);x:=mvar eqa;
       val:=if lc eqa=1 then negf red eqa ./ 1
        else multsq(negf red eqa ./ 1,1 ./lc eqa);
       solutions:=(x.val).solutions;
       equations:=for each q in equations collect
        groelinsub(q,list(x . val));
       later:= for each q in later collect groelinsub(q,list(x.val));
         break:=0>> >>;
 if break=0 then goto again else if break then goto empty;
     % Perform an elimination loop.
 if null equations then goto ready;
 equations:=sort(equations,function grloelinord);
 p:=car equations;x:=mvar p;
 equations:=for each eqa in cdr equations collect
  if mvar eqa=x then
  <<if field then
     eqa:=addf(eqa,negf multf(quotf(lc eqa,lc p),p)) else
     <<gc:=gcdf(lc p,lc eqa);
       eqa:=addf(multf(quotf(lc p,gc),eqa),
                      negf multf(quotf(lc eqa,gc),p)) >>;
   if not domainp eqa then eqa:=numr multsq(eqa ./ 1,1 ./ lc eqa);
   %%%%%%eqa:=groelinscont(eqa,xvars);
   eqa>>
   else eqa;
  later:=p.later;goto again;
ready:   % Do backsubstitutions .
  while later do
  <<p:=car later;later:=cdr later;
    p:=groelinsub(p,solutions);
    if domainp p or not member(mvar p,xvars)or
     (not domainp red p and member(mvar red p,xvars)) then
     <<break:=t;later:=nil>>;
     x:=mvar p;
     val:=if lc p=1 then negf red p ./ 1
      else quotsq(negf red p ./ 1,lc p ./ 1);
     solutions:=(x.val).solutions>>;
   if break then goto empty else goto finis;
empty: solutions:=nil;
finis: setkorder oldorder;
  solutions:=for each s in solutions collect
   car s.(reorder numr cdr s ./ reorder denr cdr s);
  return solutions end;

symbolic procedure grloelinord(u,v);
% Apply ordop to the mainvars of 'u' and 'v'.
 ordop(mvar u,mvar v);

%symbolic procedure groelinscont(f,vars);
%% Reduce content from standard form f.
% if domainp f then f else
%  begin scalar c;
%   c:=groelinscont1(lc f,red f,vars);
%   if c=1 then return f;
%   prin2 "*************content: ";print c;
%   return quotf(f,c)end;

%symbolic procedure groelinscont1(q,f,vars);
%% Calculate the contents of standard form 'f'.
% if null f or q=1 then q
%  else if domainp f or not member(mvar f,vars)then gcdf!*(q,f)
%   else groelinscont1(gcdf!*(q,lc f),red f,vars);

symbolic procedure groelinsub(s,a);
% 's' is a standard form linear in the top level variables,
% a is an assiciation list(variable.sq). ...
% The value is the standard form,where all substitutions
% from a are done in 's'(common denominator ignored).
 numr groelinsub1(s,a);

symbolic procedure groelinsub1(s,a);
 if domainp s then s  ./ 1
  else(if x then addsq(multsq(cdr x,lc s ./ 1), y)
          else addsq(lt s.+nil ./ 1,y))
    where x=assoc(mvar s,a), y=groelinsub1(red s,a);

endmodule;;end;
