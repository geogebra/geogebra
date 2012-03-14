module spde;  % Determine Lie symmetries of partial differential eqns.

% Author: Fritz Schwarz.

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


 %*******************************************************************$
 %                                                                   $
 %       This is the  REDUCE package SPDE for determining            $
 %       Lie symmetries of partial differential equations            $
 %                Version of November 1986                           $
 %                                                                   $
 %                                                                   $
 %               Fritz Schwarz                                       $
 %               GMD Institut F1                                     $
 %               Postfach 1240                                       $
 %               5205 St. Augustin                                   $
 %               West Germany                                        $
 %                                                                   $
 %               Tel. 02241-142782                                   $
 %               EARN Id. DBNGMD21.GF1002                            $
 %*******************************************************************$

create!-package('(spde),'(contrib spde));

 algebraic operator x,u,xi,eta,c,xi!*,eta!*$
 algebraic operator deq,dx,du,gl,gen,sder,rule$
 fluid '(depl!*);
 global'(pclass mm nn num!-cgen num!-dgen)$
 share pclass,mm,nn$
 lisp(pclass:=mm:=nn:=num!-cgen:=num!-dgen:=0)$

 lisp(operator simpsys,result,prsys,prsys!*)$
 fluid '(!*list kord!*)$
 fluid'(uhf dfsub csub czero rdep !*rational)$
 fluid'(list!-m list!-deq list!-pq)$

%symbolic procedure prload$
%  begin
%  if not getd 'solve1 then load solve1,solvetab,quartic;
%  if not getd 'depend1 then load depend;
%  if not getd 'ratfunpri then load ratprin;
%  end$

symbolic procedure prload; nil;

 %*******************************************************************$
 %              Auxiliary RLISP procedures                           $
 %*******************************************************************$

 remflag('(ordp ordpa),'lose);   % We must use these definitions.

 symbolic procedure ordp(u,v)$
 %Modified ordering function which orders kernels with CAR parts;
 %DF, ETA, XI and C ahead of anything else;
 if null u then null v else if null v then t else
 if eq(u,'df) or eq(u,'eta) and not eq(v,'df)
 or eq(u,'xi) and not(eq(v,'df) or eq(v,'eta))
 or eq(u,'c) and not(eq(v,'df) or eq(v,'eta) or eq(v,'xi)) then t else
 if eq(u,'eta) and eq(v,'df)
 or eq(u,'xi) and (eq(v,'df) or eq(v,'eta))
 or eq(u,'c) and (eq(v,'df) or eq(v,'eta) or eq(v,'xi))
 or eq(v,'df) or eq(v,'eta) or eq(v,'xi) or eq(v,'c) then nil else
 if atom u then if atom v then
 if numberp u then numberp v and not(u<v) else
 if numberp v then t else orderp(u,v) else nil else
 if atom v then t else
 if car u=car v then ordp(cdr u,cdr v) else ordp(car u,car v)$

 symbolic procedure ordpa(u,v); ordp(u,v);

 symbolic procedure makeset u$
 if not u then nil else
 if member(car u,cdr u) then makeset cdr u else
 car u . makeset cdr u$

 symbolic procedure lastmem u$
 if cdr u then lastmem cdr u else car u$

 symbolic procedure xmember(u,v)$ reverse member(u,reverse v)$

 symbolic procedure sacar(a,u)$
 if atom u then nil else
 if eq(a,car u) and cdr u then list u else
 append(sacar(a,car u),sacar(a,cdr u))$

 symbolic procedure scar(a,u)$
 if atom u then nil else if a=car u then u else
 scar(a,car u) or scar(a,cdr u)$

 symbolic procedure inter(u,v);
 if not u then nil else
 if member(car u,v) then
 (car u) . inter(cdr u,v) else inter(cdr u,v)$

 symbolic procedure compl(u,v)$
 if not u then nil else if member(car u,v) then
 compl(cdr u,v) else car u . compl(cdr u,v)$

 symbolic procedure vlist u$
 %U is list of items, returns U with all integers omitted;
 if not u then nil else
 if numberp car u then vlist cdr u else (car u) . vlist cdr u$

 symbolic procedure delnil u$
 %U is list, returns U with all occurences of nil deleted;
 if not u then nil else
 if car u then (car u) . delnil cdr u else delnil cdr u$

 symbolic procedure prlist u$
 %U is list of items, returns list of all pairs in U;
 if not u then nil else
 if pairp car u then (car u) . prlist cdr u else prlist cdr u$

 symbolic procedure appends(u,v,w)$ append(u,append(v,w))$

 symbolic procedure propa(fn,u)$
 %FN is predicate of a single argument, U a list;
 %Returns T if predicate is true for all elements of U;
   begin scalar ind;
   ind:=t;
   while ind and u do <<ind:=apply1(fn,car u); u:=cdr u>>;
   return ind;
   end$

 symbolic procedure sortx(fn,u)$
   begin scalar v,w;
   while u do<<v:=maxmem(fn,u); u:=delete(v,u); w:=v . w>>;
   return w;
   end$

 symbolic procedure maxmem(fn,u)$
 %FN is function of a single argument, U a list;
 %Returns element of U for which FN is maximal;
   begin scalar v;
   v:=car u;
   foreach x in cdr u do
   if greaterp(apply1(fn,x),apply1(fn,v)) then v:=x;
   return v;
   end$

 symbolic procedure maxl u$
 %U is list of integers, returns largest element of U;
 if not u then -10000 else max(car u,maxl cdr u)$

 symbolic procedure suml u$
 %U is list of integers, returns sum of all elements;
 if not u then 0 else plus2(car u,suml cdr u)$

 symbolic procedure spde!-subsetp(u,v)$
 %U and V are list representing sets;
 %Returns T if set U is subset of V;
 if not u then t else
 member(car u,v) and spde!-subsetp(cdr u,v)$

 symbolic procedure product!-set2(u,v)$
 %U and V are lists representing sets, returns list representing;
 %product set of sets represented by U and V;
   begin scalar w;
   foreach x in u do foreach y in v do w:=list(x,y) . w;
   return w;
   end$

 symbolic procedure leqgrt(l,i,j)$
 i leq j and eqn(l,i) or i geq add1 j$

 symbolic procedure fidep u$
 assoc(u,depl!*) and cdr assoc(u,depl!*)$

 symbolic procedure mkdep u$
 foreach x in cdr u do depend1(car u,x,t)$

 symbolic procedure rmdep u$
 <<rmsubs(); foreach x in cdr u do depend1(car u,x,nil)>>$

 symbolic procedure blanks l;
   begin scalar u;
   u := '(!");
   for k:=1:l do u:='! . u;
   return compress('!" . u)
   end$

 symbolic procedure terpri2$ <<terpri(); terpri()>>$

 %*******************************************************************$
 %    Auxiliary procedures for manipulating standard forms           $
 %*******************************************************************$

 symbolic procedure lcf u$ not domainp u and lc u$

 symbolic procedure minus!-f u$
 %U is s.f., returns T if lnc U is negative;
 minusf numr simp reval u$

 lisp operator minus!-f$

 symbolic procedure lengthn u$
 if not u then 0 else
 if numberp car u then plus(sub1 car u,lengthn cdr u) else
 plus(1,lengthn cdr u)$

 symbolic procedure lengthf u$
 %U is prefix s.f., returns printlength for U;
 if not u then 0 else
 if atom u then flatsizec u else
 if eqcar(u,'plus)
 then plus(times(3,sub1 length cdr u),lengthf cdr u) else
 if eqcar(u,'times) or eqcar(u,'minus)
 then plus(sub1 length cdr u,lengthf cdr u) else
 if eqcar(u,'quotient) then
 if !*rational then add1 add1 max(flatsizec cadr u,flatsizec caddr u)
 else add1 plus(flatsizec cadr u,flatsizec caddr u) else
 if eqcar(u,'expt) then add1 flatsizec cadr u else
 if eqcar(u,'dx) or eqcar(u,'du) then plus(flatsizec cadr u,4) else
 if eqcar(u,'xi) or eqcar(u,'eta) or eqcar(u,'c)
 or eqcar(u,'x) or eqcar(u,'u) then times(2,length u) else
 if eqcar(u,'df) then plus(4,lengthf cadr u,lengthf cddr u) else
 plus(lengthf car u,lengthf cdr u)$

 lisp operator lengthf$

 symbolic procedure diford u$ lengthn cddr u$

 symbolic procedure adiff(u,v)$
 %U is kernel with CAR part DF, V is kernel;
 %Returns U integrated with respect to V;
 if not member(v,u) then u else
 if length u=3 and member(v,u) then cadr u else
 if not cdr member(v,u)
 or not numberp cadr member(v,u) then delete(v,u) else
 if cadr member(v,u)=2 then
 append(xmember(v,u),cddr member(v,u)) else
 append(xmember(v,u),(sub1 cadr member(v,u)) . cddr member(v,u))$

 symbolic procedure sub!-int!-df u$
 %U is kernel with CAR part INT, returns integrated kernel if CADR;
 %part of U is DF and integration variable occurs as argument of DF;
 if eqcar(cadr u,'df) and member(lastmem u,cadr u) then
 adiff(cadr u,lastmem u) else u$

 symbolic procedure subintf u$
 %U is s.f., performs all integrations which may be done;
 %by cancellation of corresponding differentiation;
   begin
   foreach x in makeset sacar('int,u) do
   u:=subst(sub!-int!-df x,x,u);
   return numr simp prepf u;
   end$

 symbolic procedure monop u$
 %Returns T if u is monomial;
 domainp u or not red u and monop lc u$

 symbolic procedure solvef(u,v)$ car solve0(prepf u,v)$

 symbolic procedure comfacn u$  lnc ckrn u$

 symbolic procedure remfacn u$ quotf(u,lnc ckrn u)$

 %*******************************************************************$
 %     Procedures for manipulating l.d.f.'s, U is always l.d.f.      $
 %                      in this section                              $
 %*******************************************************************$

 symbolic procedure ldf!-mvar u$
 %Returns function argument of mvar U;
 (if eqcar(x,'df) then cadr x else x) where x=mvar u;

 symbolic procedure ldf!-fvar u$
 %Returns all function arguments occuring in U;
 makeset foreach x in u collect ldt!-tvar x$

 symbolic procedure ldf!-fvar!-part(u,v)$
 %V is function xi(i), eta(alpha) or c(k), returns l.d.f. of those;
 %terms in U with ldt-tvar x equal to V, overall factors not removed;
   begin scalar w;
   foreach x in u do if eq(ldt!-tvar x,v) then w:=x . w;
   return reverse w;
   end$

 symbolic procedure ldf!-dep!-var u$
 %Returns all variables x(i) or u(alpha) which occur as;
 %arguments of XI, ETA or C;
   begin scalar v;
   foreach x in u do if assoc(ldt!-tvar x,depl!*) then
   v:=append(cdr assoc(ldt!-tvar x,depl!*),v);
   return makeset v;
   end$

 symbolic procedure ldf!-pow!-var u$
 %Returns all variables x(i) or u(alpha) which occur as powers;
   begin scalar v,z;
   foreach x in u do v:=append(v,kernels tc x);
   foreach y in prlist makeset v do
   if eqcar(y,'x) or eqcar(y,'u) then z:=y . z;
   return makeset z;
   end$

 symbolic procedure ldf!-deg(u,v)$
 %V is kernel x(i) or u(alpha), returns degree of U in V;
 maxl foreach x in u collect degreef(tc x,v)$

 symbolic procedure ldf!-spf!-var u$
 %Returns all variables x(i) or u(alpha) which occur as;
 %arguments of any other kernel than xi, eta or c;
   begin scalar v,z;
   foreach x in u do v:=append(v,kernels tc x);
   foreach y in prlist makeset v do
   if not eqcar(y,'x) and not eqcar(y,'u) then
   z:=appends(sacar('x,cdr y),sacar('u,cdr y),z);
   return makeset z;
   end$

 symbolic procedure ldf!-all!-var u$
 %Returns all variables x(i) or u(alpha) which occur in U;
 makeset appends(ldf!-dep!-var u,ldf!-pow!-var u,ldf!-spf!-var u)$

 symbolic procedure ldf!-sep!-var u$
 %Returns all variables w.r.t. which U may be separated;
 compl(compl(ldf!-pow!-var u,ldf!-dep!-var u),ldf!-spf!-var u)$

 symbolic procedure ldf!-int!-var u$
 %Returns all variables w.r.t. which U may be integrated;
 if eqcar(mvar u,'df) then
   begin scalar v;
   v:=ldf!-all!-var u;
   while v and u do
   <<v:=compl(v,compl(ldt!-dep car u,ldt!-dfvar car u)); u:=cdr u>>;
   return v;
   end$

 symbolic procedure ldf!-int u$
 %U is l.d.f, returns U with all possible integrations performed;
 %or unchanged if integration is not possible;
   begin scalar v,w,z,test; integer nfun;
   a:
   test:=nil;
   w:=ldf!-int!-var u;
   nfun:=find!-nfun();
   foreach x in w do
   if not smember('int,z:=caadr algebraic int(lisp prepf u,x))
   or not smember('int,z:=subintf z) then
   <<v:=!*a2k list('c,nfun:=nfun+1); test:=t;
     mkdep(v . delete(x,ldf!-all!-var u));
     u:=addf(z,!*k2f v)>>;
   if test then go to a;
   return u;
   end$

 symbolic procedure ldf!-df!-diff u$
 %Returns list of all df-kernels which may be obtained;
 %from U by differentiation or nil;
   begin scalar dfvar,dfsub,v,w,z0,z; integer n0,nmax;
   v:=compl(ldf!-dep!-var u,ldf!-spf!-var u);
   if not v then return;
   w:=foreach x in v collect list(x,add1 ldf!-deg(u,x));
   nmax:=maxl foreach x in w collect cadr x;
   while (n0:=n0+1) leq nmax and not(z0:=nil) do
   <<foreach x in w do if cadr x geq n0 then z0:=(car x) . z0;
     z:=z0 . z>>;
   z:=reverse z;
   dfvar:=foreach x in car z collect list x;
   foreach x in cdr z do dfvar:=
   append(dfvar,foreach y in dfvar collect car product!-set2(x,y));
   foreach x in dfvar do
     begin scalar p,q;
     p:=x; q:=u;
     while p and q and red q do
     <<q:=ldf!-simp numr difff(q,car p); p:=cdr p>>;
     if pairp q and not red q and eqcar(mvar q,'df) then
     dfsub:=(mvar q) . dfsub;
     end;
   return makeset dfsub;
   end$

 symbolic procedure ldf!-sub!-var u$
 %Returns function w.r.t. which U may be resolved;
   begin scalar v,w,z;
   w:=ldf!-all!-var u;
   foreach x in u do if not v and not eqcar(z:=tvar x,'df)
   and monop tc x and spde!-subsetp(w,ldt!-dep x)
   and not smember(z,delete(x,u)) then v:=z;
   return v;
   end$

 symbolic procedure ldf!-simp u$
 %Returns l.d.f. form of U;
 if not u then nil else
 if not red u then numr simp prepf !*k2f mvar u else
   begin scalar v;
   v:=numr simp prepf u;
   if not domainp v then v := quotf(v,cdr comfac v);
   return absf v
   end$

 symbolic procedure ldf!-sep u$
 %Returns list of l.d.f. into which U has been separated;
   begin scalar v; integer k;
   if not(v:=ldf!-sep!-var u) then return list u;
   foreach x in v do u:=subst(list('ux,1,k:=k+1),x,u);
   return foreach x in coeff!-all(u,'ux) collect
   ldf!-simp numr simp prepf x;
   end$

 symbolic procedure ldf!-subf0 u$
 %Returns U with CZERO substituted;
 ldf!-simp delnil foreach x in u collect ldt!-subt0 x$

 %*******************************************************************$
 %     Procedures for manipulating l.d.t.'s, U is always l.d.t.      $
 %                      in this section                              $
 %*******************************************************************$

 symbolic procedure ldt!-tvar u$
 %U is l.d.t., returns function argument of tvar U;
 (if eqcar(x,'df) then cadr x else x) where x=tvar u$

 symbolic procedure ldt!-dfvar u$
 %U is l.d.t., returns variables w.r.t. which tvar u is derived or nil;
 (if eqcar(x,'df) then vlist cddr x else nil) where x=tvar u$

 symbolic procedure ldt!-dep u$
 %U is l.d.t., returns list of variables x or y which occur as;
 %arguments LDT-tvar u;
   (if x then cdr x else nil) where x=assoc(ldt!-tvar u,depl!*)$

 symbolic procedure ldt!-subt0 u$
 %U is l.d.t., returns U if LDT-tvar u is not on czero;
 if not member(ldt!-tvar u,czero) then u else nil$

 %*******************************************************************$
 %      Procedures for constructing the determining system           $
 %*******************************************************************$

 symbolic procedure cresys u$
   begin scalar r,v,w,lgl,lsub,depl!*!*,list!-sder;
   remprop('df,'kvalue); remprop('df,'klist);
   remprop('c,'kvalue); remprop('c,'klist);
   prload();
   rmsubs();
   depl!*:=nil;
   if car u then
   list!-deq:=foreach x in u collect assoc(x,get(car x,'kvalue)) else
   list!-deq:=get('deq,'kvalue);
   if eqn(length list!-deq,1) then
     begin scalar p;
     p:=maxmem(function length,makeset sacar('u,list!-deq));
     p:=mk!*sq !*k2q p;
     list!-sder:=list list(list('sder, cadaar list!-deq),p);
     end else if car u then
   list!-sder:=foreach x in list!-deq collect
   assoc(list('sder,cadar x),get('sder,'kvalue)) else
   list!-sder:=get('sder,'kvalue);
   if not list!-deq then rerror(spde,1,
   "Differential equations not defined");
   if not list!-sder then rerror(spde,2,
   "Substitutions for derivatives not defined");
   mm:=find!-m list!-deq; nn:=find!-n list!-deq;
   list!-m:=
   makeset foreach x in sacar('u,list!-deq) collect cadr x;
   for k:=1:nn do<<w:=!*a2k list('xi,k) . w; v:=!*a2k list('x,k) . v>>;
   for k:=1:mm do if member(k,list!-m) then
   <<w:=!*a2k list('eta,k) . w; v:=!*a2k list('u,k) . v>>;
   for k:=1:nn do r:=(!*a2k list('dx,k)) . r;
   for k:=1:mm do r:=(!*a2k list('du,k)) . r;
   for k:=1:mm do depl!*!*:=(!*a2k list('eta,k) . v) . depl!*!*;
   for k:=1:nn do depl!*!*:=(!*a2k list('xi,k) . v) . depl!*!*;
   depl!*:=depl!*!*;
   kord!*:=reverse r;
   foreach x in list!-sder do
   lsub:=((mvar caadr cadr x) . prepsq caar solvef(caadr cadr assoc
   (list('deq,cadar x),list!-deq),mvar caadr cadr x)) . lsub;
   foreach x in list!-deq do
     begin scalar s,z,lx,lu;
     z:=caadr cadr x;
     lx:=makeset sacar('x,z);
     lu:=makeset sacar('u,z);
     foreach y in lx do s:=addf(s,
     multf(!*k2f !*a2k list('xi,cadr y),numr simp prepsq difff(z,y)));
     foreach y in lu do if length y=2 then
     s:=addf(s,multf
     (!*k2f !*a2k list('eta,cadr y),numr simp prepsq difff(z,y))) else
     s:=addf(s, multf(numr zeta!* cdr y,numr simp prepsq difff(z,y)));
     s:=numr subf(s,lsub);
     s:=numr subf(s,lsub);
     lgl:=append(coeff!-all(s,'u),lgl);
     end;
   uhf:=list(makeset lgl,foreach x in reverse w collect !*k2q x);
   end$

 lisp rlistat'(cresys)$

 symbolic procedure totder(u,i)$
   begin scalar z,v,w;
   v:=car difff(u,!*a2k list('x,i));
   z:=makeset sacar('u,u);
   for k:=1:mm do if member(k,list!-m) then
   z:=(!*a2k list('u,k)) . z;
   foreach x in makeset z do w:=addf(w,
   multf(!*k2f !*a2k append(x,list i),car difff(u,x)));
   return numr simp prepf addf(v,w);
   end$

 symbolic procedure zeta!* u$
 if not get('deq,'kvalue) and (eqn(mm,0) or eqn(nn,0)) then
 rerror(spde,3,"Number of variables not defined") else
 if length u geq 3 then
   begin scalar v,w;
   prload();
   if eqn(nn,0) then nn:=find!-n list!-deq;
   v:=totder(numr zeta!* reverse cdr reverse u,car reverse u);
   for s:=1:nn do w:=addf(w,
   multf(!*k2f !*a2k('u . append(reverse cdr reverse u,list s)),
   totder(!*k2f !*a2k list('xi,s),car reverse u)));
   return simp prepsq(addf(v,negf w) ./ 1);
   end else
   begin scalar v,w;
   prload();
   if eqn(nn,0) then
   <<nn :=find!-n list!-deq; mm:=find!-m list!-deq>> else
     begin scalar p,z;
     for k:=1:mm do z:=cons(k,z);
     for k:=1:nn do p:=(!*a2k list('x,k)) . p;
     for k:=1:mm do p:=(!*a2k list('u,k)) . p;
     for k:=1:nn do mkdep((!*a2k list('xi,k)) . p);
     for k:=1:mm do mkdep((!*a2k list('eta,k)) . p);
     list!-m:=z;
     end;
   v:=totder(!*k2f !*a2k list('eta,car u),cadr u);
   for s:=1:nn do w:=addf(w,
   multf(!*k2f !*a2k list('u,car u,s),
   totder(!*k2f !*a2k list('xi,s),car reverse u)));
   return simp prepsq(addf(v,negf w) ./ 1);
   end$

 symbolic procedure simpu u$
 !*p2q mksp(('u . (car u . reverse ordn cdr u)),1)$

 put('u,'simpfn,'simpu)$
 put('zeta,'simpfn,'zeta!*)$

 symbolic procedure coeff!-all(u,v)$
   begin scalar z;
   list!-pq:=nil;
   splitrec(u,v,1,nil);
   foreach x in list!-pq do
   z:=(ldf!-simp numr simp prepf cdr x) . z;
   return makeset z;
   end$

 symbolic procedure splitrec(u,v,p,q)$
 if domainp u then
   begin scalar y;
   p:=multf(u,p);
   if y:=assoc(q,list!-pq) then
   rplacd(y,addf(cdr y,p)) else list!-pq:=(q . p) . list!-pq;
   end else
   begin
   if eqcar(mvar u,v) and length mvar u greaterp 2
   then splitrec(lc u,v,p,(lpow u) . q)
   else splitrec(lc u,v,!*t2f(lpow(u) .* p),q);
   if red u then splitrec(red u,v,p,q);
   end$

 symbolic procedure find!-m u$
 maxl makeset foreach x in sacar('u,u) collect cadr x$

 symbolic procedure find!-n u$
   begin scalar vx,vu,wx,wu;
   vx:=makeset sacar('x,u);
   vu:=makeset sacar('u,u);
   foreach x in vx do wx:=(cadr x) . wx;
   foreach x in vu do if length x geq 3 then
   wu:=append(cddr x,wu);
   return max(maxl wx,maxl wu);
   end$

 %*******************************************************************$
 %           Procedures for solving the determining system           $
 %*******************************************************************$

 symbolic procedure rule0$
 %Searches for equations of the form C(I)=0 and stores them on CZERO;
 if uhf then foreach x in car uhf do
 if not red x and not eqcar(mvar x,'df)
 then czero:=(mvar x) . czero$

 symbolic procedure rule1$
 %Searches for equations of the form DF(function,variable)=0;
 %and stores it on the list RDEP;
 if uhf and car uhf then
   begin scalar dfsub;
   foreach x in car uhf do
   if not red x and eqcar(mvar x,'df) and eqn(diford mvar x,1)
   then rdep:=(mvar x) . rdep;
   if rdep then return t;
   end$

 symbolic procedure rule1!-diff$
 %Searches for equations of the form DF(function,variable)=0;
 %which may be obtained by a single differentiation and stores it on;
 %the list RDEP;
 if uhf and car uhf then
   begin scalar u,v,z;
   foreach x in car uhf do if(z:=ldf!-df!-diff x) then
   u:=append(z,u);
   foreach x in u do if eqn(diford x,1) then v:=x . v;
   rdep:=makeset v;
   if rdep then return t;
   end$

 symbolic procedure rulec l$
 %Searches for equations of length L which may be solved for a;
 %function and stores the corresponding rules on CSUB;
 if uhf and car uhf then
   begin scalar v;
   foreach u in car uhf do if leqgrt(length u,l,4)
   and (v:=ldf!-sub!-var u) and not smember(v,csub)
   and not inter(foreach x in csub collect car x,ldf!-fvar u)
   then csub:=(v . prepsq caar solvef(u,v)) . csub;
   if csub then return t;
   end$

 symbolic procedure ruledf l$
 %Searches for equations of the form DF(function,derivative list)=0;
 %the derivative beeing of order L and stores the resulting;
 %substitution polynomial on CSUB;
 if uhf and car uhf then
   begin scalar dfsub;
   foreach x in car uhf do
   if not red x and eqcar(mvar x,'df) and eqn(diford mvar x,l)
   and not smember(ldf!-mvar x,dfsub) then dfsub:=(mvar x) . dfsub;
   csub:=foreach x in dfsub collect(cadr x) . crepol x;
   if csub then return t;
   end$

 symbolic procedure ruledf!-diff l$
 %Searches for all equations of the form;
 %DF(function,derivative list)=0 which may be obtained by;
 %differentiation, picks out those of order L and stores;
 %the corresponding substitution polynomial on CSUB;
 if uhf and car uhf then
   begin scalar v,dfsub;
   foreach u in car uhf do v:=append(v,ldf!-df!-diff u);
   if not(v:=makeset v) then return;
   foreach x in v do if eqn(diford x,l) then dfsub:=x . dfsub;
   if not dfsub then return;
   csub:=((cadar dfsub) . crepol car dfsub) . csub;
   if csub then return t;
   end$

 symbolic procedure rule!-int l$
 %Searches for an equation of length L which may be solved for a;
 %function after beeing integrated and stores the corresponding;
 %rule on CSUB;
 if uhf and car uhf then
   begin scalar v,w;
   foreach u in car uhf do if not csub and leqgrt(length u,l,4)
   and (v:=ldf!-sub!-var(w:=ldf!-int u))
   then csub:=list(v . prepsq caar solvef(w,v));
   if csub then return t;
   end$

 symbolic procedure simpsys0$
 %Removes variable which are stored on list CZERO;
   begin scalar u,v;
   if pclass=2 then<<write"Entering SIMPSYS0"; terpri2()>>;
   u:=delnil foreach x in car uhf collect ldf!-subf0 x;
   v:=foreach x in cadr uhf collect ldf!-subf0 numr x ./ denr x;
   uhf:=list(makeset u,v);
   if pclass=1 then
     begin
     terpri2();
     if eqn(length czero,1) then
     write"Substitution" else write"Substitutions";
     terpri();
     foreach x in czero do
     algebraic write (lisp aeval x),":=0";
     terpri();
     end;
   if pclass=2 then<<write"CZERO:="; prettyprint czero; terpri()>>;
   czero:=nil;
   if pclass=2 then<<write"Leaving SIMPSYS0"; terpri2()>>;
   end$

 symbolic procedure simpsys!-rdep$
 %Removes dependencies which are stored on list RDEP;
   begin scalar u,v;
   if pclass=2 then<<write"Entering SIMPSYS!-RDEP"; terpri2()>>;
   foreach x in rdep do rmdep cdr x;
   u:=makeset delnil foreach x in car uhf collect ldf!-simp x;
   v:=foreach x in cadr uhf collect simp prepsq x;
   uhf:=list(u,v);
   if pclass=1 then
     begin
     terpri();
     write"Dependencies removed"; terpri2();
     foreach x in rdep do
     <<maprin cadr x; prin2!*" independent of ";
       maprin caddr x; terpri!* t;>>;
     terpri();
     end;
   if pclass=2 then<<write"RDEP:='"; prettyprint rdep; terpri()>>;
   if pclass=2 then<<write"Leaving SIMPSYS!-RDEP"; terpri2()>>;
   end$

 symbolic procedure simpsys!-sep$
 %Performs all possible separations;
 if uhf and car uhf then
   begin scalar u,v,test;
   if pclass=2 then<<write"Entering SIMPSYS!-SEP"; terpri2()>>;
   foreach x in car uhf do
   if eqn(length(v:=ldf!-sep x),1) then u:=x . u else
     begin
     u:=append(v,u);
     if pclass=1 or pclass=2 then
       begin scalar z; integer l;
       terpri();
       l:=length car uhf-length member(x,car uhf)+1;
       write"Equation ",l," separated into the terms";
       terpri();
       if pclass=1 then for k:=1:length v do
         begin
         z:=prepf nth(v,k);
         !*list := lengthf z geq 50;
         algebraic write"Term   ",k,"  ",z;
         end;
       if pclass=2 then foreach y in v do prettyprint y;
       end;
     test:=t;
     end;
   !*list := nil;
   if test then uhf:=list(reverse makeset u,cadr uhf);
   if pclass=2 then<<write"Leaving SIMPSYS!-SEP"; terpri2()>>;
   end$

 symbolic procedure simpsys!-sub$
 %Performs all substitutions which are stored on CSUB;
 if uhf and car uhf then
   begin scalar u,v;
   if pclass=2 then<<write"Entering SIMPSYS!-SUB"; terpri2()>>;
   if pclass=1 then prrule csub;
   if pclass=2 then<<write"CSUB:='"; prettyprint csub; terpri()>>;
   u:=makeset delnil foreach x in car uhf collect
   ldf!-simp numr subf(x,csub);
   v:=foreach x in cadr uhf collect subsq(x,csub);
   uhf:=list(u,v);
   csub:=nil;
   if pclass=2 then<<write"Leaving SIMPSYS!-SUB"; terpri2()>>;
   end$

 symbolic procedure simpsys$
 if not uhf then
 rerror(spde,4,"The determining system is not defined") else
 if not car uhf then
 rerror(spde,5,"The determining system completely solved") else
   begin scalar u,v; integer nfun;
   prload();
   u:=makeset delnil foreach x in car uhf collect ldf!-simp x;
   v:=foreach x in cadr uhf collect simp prepsq x;
   uhf:=list(u,v);
   mark0:
   if pclass=1 then<<prsys!*"Entering main loop">> else
   if pclass=2 then prtlist"Entering main loop";
   czero:=csub:=rdep:=nil;
   simpsys!-sep();
   rule0();
   if czero then<<simpsys0(); go to mark0>>;
   if rule1() or rule1!-diff() then<<simpsys!-rdep(); go to mark0>>;
   if ruledf 2 or rulec 2 or rule!-int 2 or ruledf!-diff 2
   or ruledf 3 or rulec 3 or rule!-int 3 or ruledf!-diff 3
   or ruledf 4 or rulec 4 or rule!-int 4 or ruledf!-diff 4
   or ruledf 5 or rulec 5 or rule!-int 5 or ruledf!-diff 5
   then <<simpsys!-sub(); go to mark0>>;
   if car uhf then
   <<write"Determining system is not completely solved";
     terpri2(); prsys!*"The remaining equations are";
     if not zerop(nfun:=find!-nfun()) then
     write"Number of functions is ",nfun>>;
   end$

 symbolic procedure crepol u$
   begin scalar l1,f; integer pow,nfun;
   nfun:=find!-nfun();
   l1:=cdr assoc(car(u:=cdr u),depl!*);
   while (u:=cdr u) do
     begin scalar v;
     v:=car u;
     if  length u=1 or not numberp cadr u then pow:=1 else
     <<pow:=cadr u; u:=delete(pow,u);>>;
     for k:=1:pow do
       begin scalar w;
       w:=!*a2k list('c,nfun:=nfun+1);
       mkdep(w . delete(v,l1));
       if k=1 then f:=w  . f;
       if k=2 then f:=list('times,w,v) . f;
       if k geq 3 then
       f:=list('times,w,list('expt,v,k-1)) . f;
       end;
     end;
   return append('(plus),f);
   end$

 %*************************************************************$
 %     Procedures  for  analysing the result                   $
 %*************************************************************$

 symbolic procedure cpar u$
   begin scalar v;
   v:=makeset appends(sacar('xi,u),sacar('eta,u),sacar('c,u));
   foreach x in v do if not assoc(x,depl!*) then v:=delete(x,v);
   return v;
   end$

 symbolic procedure makeset!-c!-x u$
 if not u then nil else
 if member!-c!-x(car u,cdr u) then makeset!-c!-x cdr u else
 car u . makeset!-c!-x cdr u$

 symbolic procedure member!-c!-x(u,v)$
 if not v then nil else
 if equal!-c!-x(u,car v) then v else member!-c!-x(u,cdr v)$

 symbolic procedure equal!-c!-x(u,v)$
   begin scalar p,q;
   p:=scar('c,u) or scar('xi,u) or scar('eta,u);
   q:=scar('c,v) or scar('xi,v) or scar('eta,v);
   return equal(subst('cxx,p,u),subst('cxx,q,v));
   end$

 symbolic procedure numgen$ length get('gen,'kvalue)$

 symbolic operator numgen$

 symbolic procedure gengen$
   begin scalar u,z,cgen,dgen; integer ngen;
   remprop('gen,'kvalue); remprop('gen,'klist);
   foreach x in cadr uhf do u:=append(ldf!-fvar numr x,u);
   foreach x in makeset u do
     begin scalar v,w;
     w:=nil ./ 1;
     if assoc(x,depl!*) then
     v:=foreach y in cadr uhf collect
     simp prepsq(ldf!-fvar!-part(numr y,x) ./denr y) else
     v:=foreach y in cadr uhf collect
     simp prepsq((lcf ldf!-fvar!-part(numr y,x)) ./denr y);
     for k:=1:nn do if numr nth(v,k) then
     w:=addsq(multsq(nth(v,k),!*k2q !*a2k list('dx,k)),w);
     for k:=1:mm do if numr nth(v,nn+k) then
     w:=addsq(multsq(nth(v,nn+k),!*k2q !*a2k list('du,k)),w);
     if assoc(x,depl!*) then
     cgen:=(absf remfacn numr simp prepf numr  w) . cgen else
     dgen:=(absf remfacn numr simp prepf numr w) . dgen;
     end;
   dgen:=makeset dgen; cgen:=makeset!-c!-x cgen;
   num!-dgen:=length dgen; num!-cgen:=length cgen;
   for k:=1:nn do if member(z:=!*k2f !*a2k list('dx,k),dgen) then
   <<setk(list('gen,ngen:=add1 ngen),prepf z); dgen:=delete(z,dgen)>>;
   for k:=1:mm do if member(z:=!*k2f !*a2k list('du,k),dgen) then
   <<setk(list('gen,ngen:=add1 ngen),prepf z); dgen:=delete(z,dgen)>>;
   dgen:=sortx(function length,dgen);
   foreach x in dgen do setk(list('gen,ngen:=add1 ngen),prepf x);
   cgen:=sortx(function length,cgen);
   foreach x in cgen do setk(list('gen,ngen:=add1 ngen),prepf x);
   end$

 symbolic operator gengen$

 algebraic procedure comm(a,b)$
   begin scalar z;
   if (lisp length list!-deq)=0 then
   <<write"Differential equations not defined"; return nil>>;
   z:= (for k:=1:nn sum df(a,dx k)*df(b,x k)-df(b,dx k)*df(a,x k))
   +(for k:=1:mm sum df(a,du k)*df(b,u k)-df(b,du k)*df(a,u k))$
   return z;
   end$

 algebraic procedure result$
   begin integer l;
   if (l:=lisp length list!-deq)=1 then
   write"The differential equation" else
   write"The differential equations";
   for j:=1:l do
     begin scalar z; integer i,k;
     lisp(z:=car cadadr nth(list!-deq,j));
     i:=lisp cadar nth(list!-deq,j);
     k:=lisp lengthf prepf z;
     symbolic(!*list := k>40);
     write"DEQ(",i,"):=",lisp prepf z;
     end;
   !*list := nil;
   if (lisp length car uhf) neq 0  then
   prsys!*"The determining system is not completely solved" else
   <<lisp gengen(); prgen(); comm!-tab()>>;
   end$

 %*************************************************************$
 %     Procedures  for  displaying the output                  $
 %*************************************************************$

 symbolic procedure prsys!* u$
 if uhf and car uhf then
 <<terpri(); write u; terpri(); prsys(); terpri()>>$

 symbolic procedure prsys$
   begin scalar v;
   terpri();
   remprop('gl,'kvalue); remprop('gl,'klist);
   for k:=1:length car uhf do
     begin scalar z; integer l;
     z:=prepf nth(car uhf,k);
     l:=lengthf prepf nth(car uhf,k);
     !*list := l>50;
     algebraic write"GL(",k,"):=",z;
     setk(list('gl,k),z);
     end;
   terpri2();
   write"The remaining dependencies";
   terpri2();
   v:=makeset
   appends(sacar('xi,car uhf),sacar('eta,car uhf),sacar('c,car uhf));
   foreach x in v do write!-dep x;
   !*list := nil;
   end$

 symbolic procedure prrule u$
   begin
   terpri2();
   if eqn(length u,1) then
   write"Substitution" else write"Substitutions";
   terpri2();
   foreach x in u do
   <<maprin car x; prin2!*" = "; maprin cdr x; terpri!* t;>>;
   terpri();
   foreach x in u do foreach y in sacar('c,cdr x) do write!-dep y;
   end$

  symbolic procedure prtlist u$
  <<write u; terpri2(); write"DEPL!*:='"; prettyprint depl!*;
    write"UHF:='"; prettyprint uhf>>$

 symbolic procedure write!-df!-sub$
 if get('df,'kvalue) then
   begin scalar w;
   w:=get('df,'kvalue);
   remprop('df,'kvalue);
   terpri();
   if length w=1 then write"Constraint" else write"Constraints";
   terpri2();
   foreach x in w do
     begin scalar u,v;
     u:=car x;
     v:=cadadr x;
     algebraic write lisp u,":=",lisp prepsq v;
     terpri2();
     end;
   put('df,'kvalue,w);
   end$

 algebraic procedure prgen$
   begin scalar lcpar;
   for k:=1:nn do <<order dx k; factor dx k>>;
   for k:=1:mm do factor du k$
   lisp(lcpar:=cpar get('gen,'kvalue));
   write"The symmetry generators are";
   for k:=1:numgen() do
   if (lisp lengthf reval list('gen,k)) leq 60 then
   <<symbolic(!*list := nil); write"GEN(",k,"):=",gen k>> else
     begin scalar z; integer r,s,nt; operator gen!*;
     nt:=lisp length(z:=numr simp reval list('gen,k));
     r:=lisp maxl foreach x in z collect abs comfacn list x;
     if r=1 then r:=0 else r:=lisp flatsizec r;
     for l:=1:nt do gen!* l:=lisp prepf list nth(z,l);
     for l:=1:nt do
       begin
       symbolic(!*list := lengthf prepf tc nth(z,l) geq 56);
       s:=lisp abs comfacn list nth(z,l);
       if r=0 then s:=0 else
       if s=1 then s:=-1 else s:=lisp flatsizec s;
       if l=1 then write"GEN(",k,"):=",lisp blanks(r-s+1),gen!* 1 else
       if minus!-f gen!* l then
       write lisp blanks(r-s+6),gen!* l else
       write lisp blanks(r-s+6)," + ",gen!* l;
       end;
     clear gen!*;
     symbolic(!*list := nil);
     end;
   if (lisp length lcpar) neq 0 then
   <<write"The remaining dependencies"; lisp terpri()>>;
   for k:=1:(lisp length lcpar) do
   <<lisp write!-dep nth(lcpar,k);>>;
   if (lisp length lcpar) neq 0 then lisp terpri();
   lisp write!-df!-sub();
   end$

 algebraic procedure comm!-tab$
 if (lisp num!-dgen) geq 2 then
   begin integer nd; scalar v;
   nd:=lisp num!-dgen;
   write"The non-vanishing commutators of the finite subgroup";
   for i:=1:nd-1 do for j:=(i+1):nd do
   if(v:=comm(gen i,gen j)) neq 0 then
   if (lisp lengthf reval v) leq 60 then
   <<symbolic(!*list := nil); write"COMM(",i,",",j,"):= ",v>> else
     begin integer r,s,nt; scalar z; operator gen!*;
     nt:=lisp length(z:=numr simp reval v);
     r:=lisp maxl foreach x in z collect abs comfacn list x;
     if r=1 then r:=0 else r:=lisp flatsizec r;
     for i:=1:nt do gen!* i:=lisp prepf list nth(z,i);
     for l:=1:nt do
       begin
       symbolic(!*list := lengthf reval list('gen!*,l) geq 63);
       s:=lisp abs comfacn list nth(z,l);
       if r=0 then s:=0 else
       if s=1 then s:=-1 else s:=lisp flatsizec s;
       if l=1 then
       write"COMM(",i,",",j,"):=",lisp blanks(r-s+1),gen!* 1 else
       if minus!-f gen!* l then
       write lisp blanks(r-s+9),gen!* l else
       write lisp blanks(r-s+9)," + ",gen!* l;
       end;
     clear gen!*;
     end;
   symbolic(!*list := nil);
   end$

 symbolic procedure write!-dep u$
 if assoc(reval u,depl!*) then
   begin scalar v;
   v:=cdr assoc(u,depl!*);
   write car u,"(",cadr u,") depends on ";
   write caar v,"(",cadar v,")";
   foreach x in cdr v do write",",car x,"(",cadr x,")";
   terpri2();
   end$

 symbolic operator write!-dep$

 symbolic procedure find!-nfun$
 if not get('c,'klist) then 0 else
 maxl makeset foreach x in get('c,'klist) collect cadar x$

endmodule;

end;
