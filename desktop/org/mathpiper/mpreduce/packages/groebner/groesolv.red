module groesolv;% Tools for solving systems of polynomials(and poly-
% nomial equations)based on Groebner basis techniques.

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


%   Authors:     H. Melenk(ZI Berlin,Germany)
%                H. M Moeller(when this module was written Fernuniversitaet Hagen,
%                                now Universitaet Dortmund,Germany)
%                W. Neun(ZI Berlin,Germany)
%
%    Aug 1992:   accepting external solutions for univariate pols.
%    March 1994: access to roots / multroot.
%    Feb 1995: assumptions,requirements added.
%    Oct 2001: the REDUCE switch 'varopt' is copied to the switch
%              'groebopt' (which is saved during then processing of
%              'groesolve').
%    March 2005: the test for parameters is switched off, if
%              !*notestparameters is t.

% Operators:
%
%      GROESOLVE      does the whole job of solving a nonlinear
%                     set of expressions and/or equations.
%
%      GROEPOSTPROC   expects that its first parameter is a
%                     lexical groebner base already.

groesolvelevel!*:=0;

symbolic procedure groesolveeval u;
begin scalar !*ezgcd,gblist,oldtorder,res,!*groebopt,!*precise,
 y,fail,problems,denominators!*,variables!*,gmodule,at,!*notestparameters;
 !*notestparameters:=t;
 if null dmode!* then !*ezgcd:=t;
  % Copy the REDUCE switch 'varopt' to the switch 'groebopt'.
 !*groebopt:=!*varopt;gvarslast:='(list);
 oldtorder:=apply1('torder,'(lex));
 groesoldmode!*:=get(dmode!*,'dname);
 !*groebcomplex:=!*complex;
 groesetdmode(groesoldmode!*,nil);
 problems:={u};
 while problems and not fail do
 <<u:=car problems;problems:=cdr problems;
  gblist:=cdr groebnerfeval u;
  at:=union(at,cdr glterms);
  !*groebopt:=nil;% 29.8.88
  groesetdmode(groesoldmode!*,t);
  if null variables!* then variables!*:=gvarslast;
  if not(gblist='((list 1)))then
   for each gb in gblist do
   <<if !*trgroesolv then
    <<writepri("starting from basis ",'first);
     writepri(mkquote gb,'last)>>;
     for each r in res do if gb
                % Do not compare with the mother problem .
             and not subsetp(car r,car u)
       then if groesolvidsubset!?(gb,car r,variables!*)then
              res:=delete(r,res)else
            if groesolvidsubset!?(car r,gb,variables!*)then
             <<gb:=nil;if !*trgroesolv then writepri("redundant",'only)>>;
     if gb then
     <<y:=groesolvearb(groesolve0(gb,variables!*), variables!*);
      if y neq'failed then res:=(gb.y).res else fail:=t;
      if !*trgroesolv then
      <<writepri("partial result: ",'first);
         writepri(mkquote('list.cdar res),'last)>>;
      for each d in denominators!* do
       problems:={append(gb,{d}), variables!*}.problems;
      denominators!*:=nil>>>>>>;
 apply1('torder,{oldtorder});
 problems:=nil;if fail then res:=nil;
 if null res then requirements:=append(requirements,at)
  else assumptions:=append(assumptions,at);
 for each r in res do problems:=union(cdr r,problems);
 return'list.groesolve!-redun2 problems end;

symbolic procedure groesolve!-redun2 sol;
% Sol is a list of solutions;remove redundancies,now not
% by ideal theory but by simple substitution.
begin scalar b;
 for each s in sol do
  if s memq sol then
  <<b:=nil;
   for each r in sol do
    if not(r eq s)then if not b and groesolve!-redun2a(r,s)then b:=r;
   if b then sol:=delete(s,sol)>>;
 return sol end;

symbolic procedure groesolve!-redun2a(r,s);
% Redundancy test: if sub(s,r)=> trivial then t because s
% is a special case of r.
if smemq('root_of,s)then nil else
 begin scalar q,!*evallhseqp,!*protfg;
  !*evallhseqp:=t;!*protfg:=t;
  q:=errorset({'subeval,mkquote{s,r}},nil,nil);
  if errorp q then <<erfg!*:=nil;return nil>>;
  q:=cdar q;
  while q and 0=reval{'difference,cadar q,caddar q}do q:=cdr q;
  return null q end;

symbolic procedure groesolvidsubset!?(b1,b2,vars);
% Test if ideal b1 is a subset of ideal b2.
%(b2 is a specialization of b1 wrt zeros).
null b1 or(car b1='list or 0=preduceeval{car b1,b2,vars})and
 groesolvidsubset!?(cdr b1,b2,vars);

symbolic procedure groesolvearb(r,vars);
% Cover unmentioned variables.
 if atom r or not !*arbvars then r else
 for each s in r collect
  <<for each x in cdr vars do
      if not smember(x,s) then
           s:=append(s,{{'equal,x,prepf makearbcomplex()}});
    s>>;

%------------------- Driver for the postprocessor ----------------

symbolic procedure groesolve0(a,vars);
begin scalar r,ids,newvars,newa;
 if(r:=groepostnumsolve(a,vars))then return r;
 if(r:=groepostfastsolve(a,vars)) then return r;
 if(r:=groeposthillebrand(a,vars)) then return r;
 r:=groepostsolveeval{a,vars};
 if r neq'failed then return cdr r;
 ids:=cdr gindependent_seteval{a,vars};
 if null ids then goto nullerr;
 ids:=car ids;
 newvars:='list.for each x in cdr vars join
                 if not(x memq ids)then{x};
 newa:=groebnereval{a,newvars};
 denominators!*:=cdr glterms;
 if newa='(list 1)then rerror(groebner,24,"recomputation for dim=0 failed");
 r:=groepostfastsolve(newa,newvars);
 if r then return r;
 r:=groepostsolveeval{a,vars};
 if r neq'failed then return cdr r;
nullerr:
  rerror(groebner,23,
        "Moeller ideal decomposition failed with 0 dim ideal.")end;

symbolic procedure groepostnumsolve(gb,vars);
if not errorp errorset('(load!-package'roots2), nil,nil)
 and getd'multroot0
  and get(dmode!*,'dname)member'( rounded complex!-rounded)
  and length gb=length vars and groepostnumsolve1(gb,vars)
  then(cdr reval multroot0(precision 0,gb))where !*compxroots=t;

symbolic procedure groepostnumsolve1(gb,vars);
if null gb then t else
 groepostnumsolve1(cdr gb,cdr vars)and
 <<for each x in kernels numr simp car gb do q:=q and x member vars;
  q>>where q=t;

symbolic procedure groepostfastsolve(gb,vars);
% Try to find a fast solution.
begin scalar u,p1,p2,fail,kl,res;
 if !*trgroesolv then prin2t "fast solve attempt";
 groesoldmode!*:=get(dmode!*,'dname);
 !*groebnumval:=member(groesoldmode!*,'(rounded complex!-rounded));
 groesetdmode(groesoldmode!*,'nil);
 u:=kl:=for each p in cdr gb collect
 <<p:=numr simp p;intersection(vars,kernels p).p>>;
 if u='((nil)) then goto trivial;
 while u and cdr u do
 <<p1:=car u;p2:=cadr u;u:= cdr u;
  car p1:=setdiff(car p1,car p2);
  fail:=fail or null car p1>>;
 if fail then goto exit;
 res:=for each r in groepostfastsolve1(reverse kl,nil,0)
        collect'list.reverse r;
 goto exit;
trivial:
 res:={'list.for each x in cdr vars collect{'equal,x,mvar makearbcomplex()}};
exit:
 groesetdmode(groesoldmode!*,t);
 return res end;

fluid'(f);

symbolic procedure groepostfastsolve1(fl,sub,n);
 if null fl then'(nil)else
  begin scalar u,f,v,sub1;
   n:=n #+ 1;
   f:=car fl;v:=car f;f:=numr subf(cdr f,sub);
   if null f then return groepostfastsolve1(cdr fl,sub,n);
  % v:=car sort(v,function(lambda(x,y);degr(f,x)>degr(f,y)));
   v:=car v;
(f:=reorder f)where kord!*={v};
   if not domainp lc f then groepostcollectden reorder lc f;
   u:=groesolvepolyv(prepf f,v);
   return for each s in u join
   <<sub1:=if smemq('root_of,s)then sub else(v.caddar s).sub;
    for each q in groepostfastsolve1(cdr fl,sub1,n)collect
     car s.q>> end;

unfluid'(f);

symbolic procedure groepostcollectden d;
% d is a non trivial denominator(standard form);
% collect its factors.
 for each p in cdr fctrf d do
  if not member(p:=prepf car p,denominators!*)then
   denominators!*:=p.denominators!*;

put('groesolve,'psopfn,'groesolveeval);

symbolic procedure groepostsolveeval u;
begin scalar a,b,vars,oldorder,groesoldb!*;
 scalar !*groebprereduce,!*groebopt,!*groesolgarbage;
 groesoldmode!*:=get(dmode!*,'dname);
 groesetdmode(groesoldmode!*,'nil);
 !*groebnumval:=member(groesoldmode!*,'(rounded complex!-rounded));
 if vdpsortmode!*='lex then t else
  rerror(groebner,8,"groepostproc, illegal torder;(only lex allowed)");
 a:=groerevlist reval car u;
 vars:=cdr u and groerevlist cadr u or groebnervars(a,nil);
 oldorder:=setkorder vars;
 b:= groesolve1(a,a,vars);
 a:=nil;
 if b eq'failed then a:=b else
 <<for each sol in b do % delete multiplicities
     if not member(sol,a)then a:=sol.a;
 a:='list.for each sol in a collect'list.sol>>;
 setkorder oldorder;
 groesetdmode(groesoldmode!*,t);
 return a end;

put('groepostproc,'psopfn,'groepostsolveeval);

% Data structure:
%
%  All polynomials are held in prefix form(expressions).
%  Transformation to standard quotients / standard forms is done locally
%  only;distributive form is not used here.
%
%  A zero is a set of equations,if possible with a variable on the
%  lhs each
%       e.g.{y=17,z=b+8};
%        internally:(( equal y 17)(equal z(plus b 8)))
%  A zeroset is a list of zeros
%       elgl{{y=17,z=b+8},{e=17,z=b-8}}
%  Internally the sets(lists)are kept untagged as lists;the
%  tag'list is only added to the results and to those lists which
%  are parameters to algebraic operators not in this package.

symbolic procedure groesolve1(a,fulla,vars);
%  a lex Groebner basis or tail of lex Groebner basis.
%  fulla the complete lex Groebner basis to a.
%  vars  the list of variables.
if null a or a='(1)then nil else
<<begin scalar f1,a1,res,q,gi,ng1,ng2,ngq,qg,mv,test;
 res:=assoc(a,groesoldb!*);if res then return cdr res;
 groesolvelevel!*:=groesolvelevel!* #+ 1;
 if member(a,!*groesolrecurs)then return'failed;
 !*groesolrecurs:=a.!*groesolrecurs;
 if length a=1 then<<res:=groesolvepoly car a;goto ready>>;
         % step 1
 f1:=car a;
 a1:=cdr a;
 test:=nil;
 mv:=intersection(vars,ltermvariables f1);% test Buchcrit 4
 for each p in a1 do if intersection(mv,ltermvariables p)then test:=t;
 if not test then
 <<ngq:=groesolve1(a1,a1,vars);
  if ngq eq'failed then <<res:='failed;goto ready>>;
  res:=zerosetintersection(ngq,f1,vars);
  go to ready>>;
% q:=cdr groebidq('list.a1,f1,'list.vars);% a1:f1
  q:=groesolvidq(a1,f1,vars);
  if q='(1)then     % f1 already member of a1;skip it
  <<res:= groesolve1(a1,fulla,vars);go to ready>>;
  ngq:=groesolve1(q,q,vars);
  if ngq eq'failed then <<res:='failed;goto ready>>;
  ng1:=zerosetintersection(ngq,f1,vars);
         % step 4
  if groesolvidqtest(a1,f1,vars)then
  <<while q do
   <<gi:=car q;q:=cdr q;
    gi:=preduceeval{gi,'list.a,'list.vars};
    if gi=0 then q:=nil else
    a:=cdr groebidq('list.a,gi,'list.vars)>>;
   ng2:=groesolve1(a,a,vars);
   if ng2 eq'failed then <<res:='failed;go to ready>>
>>else
  <<ng2:=();
   if length q=1 then
   <<gi:=preduceeval{car q,'list.fulla,'list.vars};
    if gi neq 0 then
    <<qg:=cdr groebidq('list.fulla,gi,'list.vars);% a1:gi
     ng2:=groesolve1(qg,qg,vars);
     if ng2 eq'failed then <<res:='failed;go to ready>> >>>>
   else
   <<ng2:=groesolve2(a1,q,vars);
    if ng2 eq'failed then<<res:='failed;goto ready>> >>>>;
 res:= zerosetunion(ng1,ng2);
ready:
 groesolvelevel!*:=groesolvelevel!* #- 1;
 groesoldb!*:=(a.res).groesoldb!*;
 return res end
>> where !*groesolrecurs=!*groesolrecurs;% recursive fluid!

symbolic procedure groesolvidqtest(a1,f1,vars);
 not(deg(f1,car vars)eq deg(car a1,car vars));

symbolic procedure groesolvidq(a1,f1,vars);
begin scalar temp,x;
 x:=car vars;
 if not groesolvidqtest(a1,f1,vars)
  then return cdr groebidq('list.a1,f1,'list.vars);
 temp:=
  for each p in a1 collect
   reval car reverse coeffeval{p,x};
  return cdr groebnereval{'list.temp,'list.vars}end;

symbolic procedure groesolve2(a,q,vars);
% Calculation of the zeroset a1:(g1,g2,,,,gs),
% the gi given as members of q.
 groesolvetree(a,q,q,vars);

symbolic procedure groesolvetree(a,t1,phi,vars);
begin scalar q,ngtemp,ngall,t2,h,g,a2,phi2;
 if null phi then return nil else
  if null t1 then
  <<q:=cdr groebidq('list.a,'times.phi,'list.vars);
   return if car q=1 then nil
           else groesolve1(q,q,vars)>>;
   for each g in t1 do
   <<h:=preduceeval{g,'list.a,'list.vars};
    phi:=delete(g,phi);
    if not(h=0)then <<t2:=h.t2;phi:=h.phi>>>>;
 if null phi then return nil;% 29.8.88
 t1:=t2;
 q:=cdr groebidq('list.a,'times.phi,'list.vars);
 if not(car q=1)then
 <<ngall:=groesolve1(q,q,vars);
  if ngall eq'failed then return'failed>>;
 if !*groesolgarbage then return groesolverestruct(q,phi,vars,ngall);
 while t1 do
 <<g:=car t1;t1:=cdr t1;phi2:=delete(g,phi);
  if phi2 then
  <<a2:=cdr groebnereval{'list.g.a,'list.vars};
   if not(car a2=1)then
   <<ngtemp:=groesolvetree(a2,t1,phi2,vars);
    ngall:=zerosetunion(ngtemp,ngall)>> >>>>;
 return ngall end;

symbolic procedure groesolverestruct(a,phi,vars,ngall);
% There was a problem with an embedded solution in phi such that
% a : phi=a;
% we try a heuristic by making one variable a formal parameter.
begin scalar newa,newvars,mv,oldorder,solu;
 mv:=ltermvariables('times.phi);
 mv:=car mv;
 newvars:=delete(mv,vars);
 oldorder:=setkorder newvars;
 newa:=cdr groebnereval{'list.a,'list.newvars};
 !*groesolgarbage:=nil;
  solu:=groesolve1(newa,newa,newvars);
 setkorder oldorder;
 return if !*groesolgarbage then ngall else solu end;

symbolic procedure ltermvariables u;
% Extract variables of leading term in u.
begin scalar v;
 u:=numr simp u;
 while not domainp u do<<v:=mvar u.v;u:=lc u>>;
 return reversip v end;

symbolic procedure zerosetintersection(ng,poly,vars);
% ng is a zeroset, poly is a polynomial.
% The routine maps the zeros in'ng'by the polynomial:
%   each zero is substituted into the polynomial,
%   that gives a univariate
%   solved by SOLVE or numerical techniques.
% The result is the solution'ng',including the solutions of the
% polynomial.
begin scalar res,ns,testpoly,ppoly,sol,s,var,dom;
 res:=();
 poly:=simp poly;
 var:=if not domainp numr poly then groesolmvar(numr poly,vars)
  else'constant;
loop:
 if ng=()then go to finish;
 ns:=car ng;ng:=cdr ng;
 testpoly:=poly;
 dom:=groesoldmode!* or'rational;
 groesetdmode(dom,t);
 testpoly:=simp prepsq testpoly;
 for each u in ns do
  if idp lhs u and not smemq('root_of,rhs u)then
  <<s:=rhs u;
   testpoly:=subsq(testpoly,{lhs u.s})>>;
 groesetdmode(dom,nil);
 ppoly:=prepf numr testpoly;
 sol:=groesolvepolyv(ppoly,var);
 res:=append(res,for each r in sol collect append(r,ns));
 go to loop;
finish:
 return res end;

symbolic procedure groesolmvar(poly,vars);
% Select main variable wrt vars sequence.
<<while vars and not smember(car vars,poly)do vars:=cdr vars;
 if null vars then rerror('groebner,27,"illegal poly");car vars>>;


% Solving a single polynomial with respect to its main variable .

symbolic procedure groesolvepoly p;groesolvepolyv(p,mainvar p);

symbolic procedure groesolvepolyv(p,var);
% Find the zeros for one polynomial p in the variable'var'.
% Current dmode is'nil'.
( begin scalar res,u,!*convert,y,z;
  if(u:=assoc(var,depl!*))then depl!*:=delete(u,depl!*);
  if !*trgroesolv then
  <<writepri("   solving univariate with respect to ",'first);
   writepri(mkquote var,'last);writepri(mkquote p,'only)>>;
 for each s in groebroots!* do
  if 0=reval{'difference,p,car s}then res:=cdr s;
 if res then return res;
 groesetdmode(groesoldmode!*,t);
 u:=numr simp p;
 res:=if !*groebnumval and univariatepolynomial!? u
  then groeroots(p,var)
  else(solveeval{p,var})
    where kord!*=nil,alglist!*=nil.nil;
 res:=cdr res;
       % Collect nontrivial denominator factors.
       % Reorder for different local order during solveeval.
 for each x in res do
 <<y:=prepf(z:=reorder denr simp caddr x);
  if dependsl(y,variables!*)then groepostcollectden z>>;
 res:=for each x in res collect {x};
 groesetdmode(groesoldmode!*,nil);
 return res end) where depl!*=depl!*;

symbolic procedure univariatepolynomial!? fm;
 domainp fm or univariatepolynomial!?1(fm,mvar fm);

symbolic procedure univariatepolynomial!?1(fm,v);
 domainp fm or
 domainp lc fm and v=mvar fm and univariatepolynomial!?1(red fm,v);

symbolic procedure predecessor(r,l);
% Looks for the  predecessor of'r'in'l'.
 if not pairp l or not pairp cdr l or r=car l
  then rerror(groebner,9,"no predecessor available")else
  if r=cadr l then car l else predecessor(r,cdr l);

symbolic procedure zerosetunion(ng1,ng2);<<ng1:=zerosetunion1(ng1,ng2);ng1>>;

symbolic procedure zerosetunion1(ng1,ng2);
% Unify zeroset structures.
 if ng1=()then ng2
  else if zerosetmember(car ng1,ng2)then zerosetunion1(cdr ng1,ng2)
  else car ng1.zerosetunion1(cdr ng1,ng2);

symbolic procedure zerosetmember(ns,ng);
 if ng=()then nil else
 if zeroequal(ns,car ng)then ng else zerosetmember(ns,cdr ng);

symbolic procedure zeroequal(ns1,ns2);
 if zerosubset(ns1,ns2)then zerosubset(ns2,ns1)else nil;

symbolic procedure zerosubset(ns1,ns2);
 if null ns1 then t else
 if member(car ns1,ns2)then zerosubset(cdr ns1,ns2)else nil;

symbolic procedure groesetdmode(dmode,dir);
% Interface for switching an arbitrary domain on/off.
% Preserve complex mode. Turn on EZGCD whenever possible.
 if null dmode then nil else
 begin scalar !*msg,x,y;
  if null dir then
  <<if !*complex then y:=setdmode('complex,nil);
   !*complex:=nil;
   if dmode!*='!:rd!: then !*rounded:=nil;
   if dmode!* then y:=setdmode(get(dmode!*,'dname), nil);
   if !*groebcomplex then<<setdmode('complex,t);!*complex:=t>>>>
  else
  <<if memq(dmode,'(complex complex!-rounded complex!-rational))
      then<<!*complex:=t;y:=setdmode('complex,t);
    if(x:=atsoc(dmode,'((complex!-rounded.rounded)
                                 (complex!-rational.rational))))
     then y:=setdmode(cdr x,t)>>
  else y:=setdmode(dmode,t);
  if memq(dmode,'(rounded complex!-rounded)) then !*rounded:=t>>;
 !*ezgcd:=null dmode!*;return y end;

symbolic procedure preduceeval pars;
% Polynomial reduction driver.'u'is an expression and v a list of
% expressions. Preduce calculates the polynomial u reduced wrt the list
% of expressions'v'.
% Parameters:
%     1      Expression to be reduced,
%     2      polynomials or equations;base for reduction.
%     3      Optional: list of variables.
begin scalar n,vars,x,u,v,w,oldorder,!*factor,!*exp,!*gsugar,!*vdpinteger;
 integer pcount!*;!*exp:=t;
 if !*groebprot then groebprotfile:={'list};
 n:=length pars;
 x:=reval car pars;
 u:=reval cadr pars;
 v:=if n #> 2 then reval caddr pars else nil;
 w:=for each j in groerevlist u collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebnr2,3,"empty list in preduce.");
 vars:=groebnervars(w,v);
 if not vars then vdperr'preduce;
 oldorder:=vdpinit vars;
 w:=for each j in w collect a2vdp j;
 x:=a2vdp x;
 if !*groebprot then
 <<w:=for each j in w collect vdpenumerate j;
  groebprotsetq('candidate,vdp2a x);
  for each j in w do groebprotsetq(mkid('poly,vdpnumber j),vdp2a j)>>;
 w:=groebnormalform(x,w,'sort);
 w:=vdp2a w;
 setkorder oldorder;
 return if w then w else 0 end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% The following code is the interface to Stan's rootfinder.
%

symbolic procedure groeroots(p,x);
 begin scalar r;
  x:=nil;r:=reval{'roots,p};
         % Re-evaluate rhs in order to get prefix form.
  r:=for each e in cdr r collect{'equal,cadr e,reval caddr e};
  return'list.r end;

endmodule;;end;
