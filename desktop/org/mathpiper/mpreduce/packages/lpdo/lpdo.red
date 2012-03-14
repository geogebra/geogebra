% ----------------------------------------------------------------------
% $Id: lpdo.red 658 2010-06-03 08:21:16Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008-2010 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

module lpdo;
% Approximate factorization of linear partial differential operators.
% Still under development.

fluid '(!*utf8);

if !*utf8 then on1 'utf8exp;

load!-package 'redlog;
load!-package 'ofsf;

rl_set '(reals);

switch lpdodf,lpdocoeffnorm;

on1 'lpdodf;
on1 'lpdocoeffnorm;

infix lpdotimes;
flag('(lpdotimes),'spaced);
flag('(lpdotimes),'nary);
precedence lpdotimes,times;
newtok '((!* !* !*) lpdotimes) where !*msg=nil;

put('!*lpdo,'rtypefn,'quotelpdo);
put('!*lpdo,'prifn,'lpdo_pri!*lpdo);
put('!*lpdo,'fancy!-prifn,'lpdo_pri!*lpdo);
put('!*lpdo,'fancy!-setprifn,'lpdo_setpri!*lpdo);
put('!*lpdo,'lpdo_simpfn,'lpdo_simp!*lpdo);

put('plus,'rtypefn,'getrtypeor);
put('plus,'lpdo_simpfn,'lpdo_simpplus);

put('minus,'rtypefn,'getrtypeor);
put('minus,'lpdo_simpfn,'lpdo_simpminus);

put('difference,'rtypefn,'getrtypeor);
put('difference,'lpdo_simpfn,'lpdo_simpdifference);

put('expt,'rtypefn,'getrtypeor);
put('expt,'lpdo_simpfn,'lpdo_simpexpt);

put('lpdotimes,'rtypefn,'quotelpdo);
put('lpdotimes,'lpdo_simpfn,'lpdo_simplpdotimes);

put('times,'rtypefn,'getrtypeor);
%put('times,'lpdo_simpfn,'lpdo_simptimes);
put('times,'lpdo_simpfn,'lpdo_simplpdotimes);

put('abs,'lpdo_simpfn,'lpdo_simpabs);

put('quotient,'lpdo_simpfn,'lpdo_simpquotient);

put('partial,'rtypefn,'quotelpdo);
if rl_texmacsp() then <<
   put('partial,'fancy!-functionsymbol,"\partial ");
   put('partial,'fancy!-prifn,'lpdo_fancy!-pripartial)
>>;
put('partial,'lpdo_simpfn,'lpdo_simppartial);

put('diff,'simpfn,'lpdo_simpdiff1);
put('diff,'lpdo_simpfn,'lpdo_simpdiff);

put('lpdo,'evfn,'lpdo_reval);
put('lpdo,'subfn,'lpdo_subst0);
put('lpdo,'tag,'!*lpdo);

fluid '(lpdopol!* !*rlverbose shapesym!*);

shapesym!* := '!#;

if null lpdopol!* then lpdopol!* := (-1) . nil;

put('lpdoset,'psopfn,'lpdo_set);

put('lpdoweyl,'psopfn,'lpdo_weyl);

put('lpdorat,'psopfn,'lpdo_rat);

procedure lpdo_set(l);
   begin scalar w;
      if null l then
	 return 'list . lpdopol!*;
      rmsubs();
      w := lpdopol!*;
      if null cdr l and eqcar(car l,'list) then
	 l := cdar l
      else if eqcar(l,-1) then
	 l := (-1) . nil
      else
      	 l := cdr reval car l;
      lpdopol!* := car l . sort(cdr l,'ordp);
      return 'list . w
   end;

procedure lpdo_weyl(l);
   'list . lpdo_polxpnd1(shapesym!*,cdr l,car l,'times,'reval) . cdr l;

procedure lpdo_rat(l);
   begin scalar nsym,dsym,num,den;
      nsym := intern compress nconc(explode shapesym!*,{'!_,'n});
      dsym := intern compress nconc(explode shapesym!*,{'!_,'d});
      num := lpdo_polxpnd1(nsym,cddr l,car l,'times,'reval);
      den := lpdo_polxpnd1(dsym,cddr l,cadr l,'times,'reval);
      return 'list . {'quotient,num,den} . cddr l
   end;


procedure lpdo_templp();
   car lpdopol!* neq -1 and not null cdr lpdopol!*;

procedure quotelpdo(x);
   'lpdo;

procedure lpdo_pri!*lpdo(u);
   maprin lpdo_prep cadr u;

procedure lpdo_setpri!*lpdo(x,u);
   <<
      fancy!-maprint(x,0);
      fancy!-prin2!*(":=",4);
      lpdo_pri!*lpdo u
   >>;

procedure lpdo_fancy!-pripartial(u);
   if null !*nat then
      'failed
   else <<
      fancy!-prefix!-operator car u;
      fancy!-maprint!-atom(cadr u,0)
   >>;

procedure lpdo_reval(u,v);
   if v then lpdo_prep lpdo_simp u else lpdo_mk!*lpdo lpdo_simp u;

procedure lpdo_simp(u);
   begin scalar w;
      if atom u then
 	 return lpdo_simpatom u;
      argnochk u;
      if flagp(car u,'opfn) then
	 return lpdo_simp apply(car u,for each x in cdr u collect reval x);
      if (w := get(car u,'lpdo_simpfn)) then
 	 return if flagp(w,'full) then apply(w,{u}) else apply(w,{cdr u});
      if (w := get(car u,'psopfn)) then
 	 return lpdo_simp apply1(w,cdr u);
      if (w := get(car u,'prepfn2)) then
 	 return lpdo_simp apply(w,{u});
      if cdr u then typerr (u,"lpdo (no arguments permitted)");
      if get(u,'simpfn) neq 'lpdo_xpnd then <<
      	 put(car u,'simpfn,'lpdo_xpnd);
      	 flag({car u},'full);
	 rmsubs()
      >>;
      return {lpdo_xpnd u . nil}
   end;

procedure lpdo_xpnd(a);
   begin scalar vl;
      if not lpdo_templp() then return mksq(a,1);
      vl := cdr lpdopol!*;
      if null vl then return mksq(a,1);
      a := car a;
      return lpdo_txpnd a
   end;

procedure lpdo_txpnd(a);
   simp lpdo_txpnd1(a,car lpdopol!*);

procedure lpdo_txpnd1(a,templ);
   begin scalar w;
      if idp templ and eqcar(w := explode2 templ,shapesym!*) then
	 return intern compress nconc(explode2 a,cdr w);
      if atom templ then
	 return templ;
      return car templ . for each arg in cdr templ collect lpdo_txpnd1(a,arg)
   end;

procedure lpdo_polxpnd1(a,vl1,deg,tm,sm);
   begin scalar w,v,tupl,atup,vl;
      tupl := lpdo_tupl(length vl1,deg);
      w := for each tup in tupl collect <<
	 vl := vl1;
	 atup := if idp a then
 	    mkid(lpdo_mkid(a,'!_ . tup),'!_)
 	 else
 	    {lpdo_mkid(car a,'!_ . tup)};
	 lpdo_smkn(tm,atup . for each d in tup join <<
	       v := car vl;
	       vl := cdr vl;
	       if eqn(d,1) then {v} else if d > 1 then {{'expt,v,d}}
	    >>)
      >>;
      return apply(sm,{lpdo_smkn('plus,w)})
   end;

procedure lpdo_tupl(k,n);
   if eqn(k,1) then
      for i := 0:n collect {i}
   else
      for i := 0:n join
 	 for each r in lpdo_tupl(k-1,n-i) collect i . r;

procedure lpdo_mkid(id,l);
   if null l then id else lpdo_mkid(mkid(id,car l),cdr l);

procedure lpdo_simpatom(u);
   begin scalar w;
      if null u then typerr("nil","lpdo");
      if stringp u then typerr({"string",u},"lpdo");
      if (w := lpdo_gettype u) then <<
	 if w eq 'lpdo or w eq 'scalar then
	    return lpdo_simp cadr get(u,'avalue);
	 typerr({w,u},"lpdo")
      >>;
      if numberp u or idp u then return {simp u . nil};
      typerr(u,"lpdo")
   end;

procedure lpdo_gettype(v);
   % Get type. Return type information if present. Handle scalars
   % properly.
   (if w then car w else get(v,'rtype)) where w = get(v,'avalue);

procedure lpdo_resimp(dp);
   lpdo_simp lpdo_prep dp;

procedure lpdo_prep(dp);
   if null dp then
      0
   else if null cdr dp then
      lpdo_preplpdotimes car dp
   else
      'plus . for each x in dp collect lpdo_preplpdotimes x;

procedure lpdo_preplpdotimes(x);
   begin scalar w,neg;
      if null cdr x then
      	 return prepsq car x;
      w := prepsq car x;
      if eqcar(w,'minus) then <<
 	 neg := t;
	 w := cadr w
      >>;
      w := if eqn(w,1) then
	 lpdo_smkn('times,cdr x)
%	 lpdo_smkn('lpdotimes,cdr x)
      else
       	 'times . w . cdr x;
%   	 'lpdotimes . w . cdr x;
      if neg then
	 w := {'minus,w};
      return w
   end;

procedure lpdo_mk!*lpdo(dp);
   '!*lpdo . dp . if !*resubs then !*sqvar!* else {nil};

procedure lpdo_simp!*lpdo(u);
   if cadr u then  % [!*sqvar!*=T]
      car u
   else
      lpdo_resimp car u;

procedure lpdo_simppartial(x);
   <<
      lpdo_partialchk x;
      {!*f2q 1 . {'partial . x}}
   >>;

procedure lpdo_partialchk(x);
   if not idp car x then typerr(car x,"variable");

procedure lpdo_simplpdotimes(u);
   lpdo_multn for each x in u collect lpdo_simp x;

procedure lpdo_multn(l);
   lpdo_compact for each x in lpdo_cartprod l join lpdo_commute x;

procedure lpdo_mult(dp1,dp2);
   lpdo_multn {dp1,dp2};

procedure lpdo_cartprod(suml);
   begin scalar a,w;
      if null suml then
 	 return nil;
      if null cdr suml then
      	 return for each m in car suml collect {m};
      a := car suml;
      w := lpdo_cartprod cdr suml;
      return for each m in a join
      	 for each y in w collect
 	    m . y
   end;

procedure lpdo_commute(u);
   % u is a mulptiplicative list of mons. Returns an additive list of
   % mons.
   if u then lpdo_commute2(car u,lpdo_commute cdr u);

procedure lpdo_commute2(a,d);
   % a is a mon, d is an additive list of mons. Returns an additive
   % list of mons.
   if null d then {a} else for each x in d join lpdo_commute21(a,x);

procedure lpdo_commute21(a,d);
   % a, d are mons considered multiplicatively. Returns an additive
   % list of mons.
   begin scalar p1,d1,p2,d2,v,w,pt,p2diff,ak; integer n;
      p1 := car a;
      d1 := cdr a;
      p2 := car d;
      d2 := cdr d;
      if (domainp numr p2 and domainp denr p2) or null d1 then
	 return {multsq(p1,p2) . append(d1,d2)};
      pt := car d1;
      d1 := cdr d1;
      v := lpdo_partialv pt;
      n := lpdo_partialdeg pt;
      w := for k := n step -1 until 0 join <<
	 p2diff := multsq(simp choose(k,n),lpdo_df(prepsq p2,v,n-k));
	 ak := lpdo_ak(pt,k);
      	 lpdo_commute21(p1 . d1,p2diff . if ak then ak . d2 else d2)
      >>;
      return w
   end;

procedure lpdo_ak(a,k);
   if eqn(k,0) then
      nil
   else if eqn(k,1) then
      lpdo_partialbas a
   else
      {'expt,lpdo_partialbas a,k};

procedure lpdo_compact(dp);
   begin scalar w,al;
      for each m in dp do
      	 al := lpdo_alinsert(al,lpdo_compactpt cdr m,car m);
      al := sort(al,function(lambda(x1,x2); ordp(car x1,car x2)));
      w := for each pr in al join if not null numr cdr pr then
	 {cdr pr . car pr};
      return w
   end;

procedure lpdo_alinsert(al,key,p);
   begin scalar w,sm;
      w := assoc(key,al);
      if null w then
	 return (key . p) . al;
      sm := addsq(cdr w,p);
      if null numr sm  then
	 return delete(w,al);
      cdr w := sm;
      return al
   end;

procedure lpdo_compactpt(l);
   begin scalar al,v; integer n;
      for each pt in l do <<
	 v := lpdo_partialv pt;
	 n := lpdo_partialdeg pt;
	 al := lpdo_alinsertpt(al,v,n)
      >>;
      al := sort(al,function(lambda(x1,x2); ordp(car x1,car x2)));
      return for each pr in al collect
	 lpdo_exptpt({'partial,car pr},cdr pr)
   end;

procedure lpdo_alinsertpt(al,v,n);
   begin scalar w;
      w := assoc(v,al);
      if null w then
	 return (v . n) . al;
      cdr w := cdr w + n;
      return al
   end;

procedure lpdo_simpdifference(u);
   lpdo_simp {'plus,car u,{'minus,cadr u}};

procedure lpdo_simpplus(u);
   lpdo_addn for each x in u collect lpdo_simp x;

procedure lpdo_addn(l);
   lpdo_compact for each dp in l join copy dp;

procedure lpdo_add(dp1,dp2);
   lpdo_addn {dp1,dp2};

procedure lpdo_simpminus(u);
   lpdo_minus lpdo_simp car u;

procedure lpdo_minus(dp);
   for each mon in dp collect negsq car mon . cdr mon;

procedure lpdo_simpquotient(u);
   lpdo_quot(lpdo_simp car u,lpdo_simp cadr u);

procedure lpdo_quot(n,d);
   <<
      if cdr d or cdar d then
	 typerr('quotient . {n,d},"lpdo (partial in denominator)");
      for each x in n collect
	 quotsq(car x,caar d) . cdr x
   >>;

procedure lpdo_simptimes(u);
   begin scalar w;
      w := simp('times . u);
      return {w . nil}
   end;

procedure lpdo_simpabs(u);
   begin scalar w;
      w := simp('abs . u);
      return {w . nil}
   end;

procedure lpdo_simpexpt(u);
   begin integer d;
      d := reval cadr u;
      if not numberp d then
	 typerr(u,"lpdo exponent");
      if eqn(d,0) then
	 return lpdo_simp 1;
      if eqcar(car u,'partial) then <<
	 lpdo_partialchk car u;
	 return {!*f2q 1 . {{'expt,car u,d}}}
      >>;
      return lpdo_simp('lpdotimes . for i:=1:d collect car u)
   end;

procedure lpdo_simpdiff(l);
   {lpdo_simpdiff1 l . nil};

procedure lpdo_simpdiff1(l);
   begin scalar w,c;
      w := simp car l;
      if !*lpdodf and lpdo_idlp kernels numr w and lpdo_idlp kernels denr w
      then
	 return simp('df . l);
      if cddr l and eqn(caddr l,0) then
      	 return w;
      c := sfto_dcontentf numr w;
      if c neq 1 then
      	 return multsq(!*f2q c,
	    mksq('diff . prepsq quotsq(w,!*f2q c) . cdr l,1));
      c := sfto_dcontentf denr w;
      if c neq 1 then
      	 return quotsq(mksq('diff . prepsq multsq(w,!*f2q c) . cdr l,1),
	    !*f2q c);
      return mksq('diff . revlis l,1);
   end;

procedure lpdo_idlp(l);
   null l or idp car l and lpdo_idlp cdr l;

procedure lpdo_smkn(op,argl);
   if null argl then
      if op eq 'plus then 0 else 1
   else if null cdr argl then
      car argl
   else op . argl;

procedure lpdo_partialv(u);
   if eqcar(u,'partial) then
      cadr u
   else if eqcar(u,'minus) or eqcar(u,'expt) then
      lpdo_partialv cadr u;

procedure lpdo_partialdeg(u);
   if eqcar(u,'partial) then
      1
   else if eqcar(u,'minus) then
      lpdo_partialdeg cadr u
   else if eqcar(u,'expt) and eqcar(cadr u,'partial) then
      caddr u
   else
      0;

procedure lpdo_partialbas(u);
   if eqcar(u,'partial) then
      u
   else if eqcar(u,'minus) or eqcar(u,'expt) then
      lpdo_partialbas cadr u
   else
      rederr {"lpdo_partialbas: unexpected term",u};

procedure lpdo_df(u,x,n);
   if !*lpdodf and lpdo_templp() then
      simp {'df,u,x,n}
   else
      lpdo_simpdiff1 {u,x,n};

procedure lpdo_d2c(u);
   lpdo_sub(u,'lpdotimes,'times);

procedure lpdo_c2d(u);
   lpdo_sub(u,'times,'lpdotimes);

procedure lpdo_sub(u,tim1,tim2);
   if atom u then
      u
   else
      (if car u eq tim1 then tim2 else car u) .
	 for each x in cdr u collect lpdo_sub(x,tim1,tim2);

procedure lpdo_exptpt(pt,n);
   begin scalar d,b;
      b := lpdo_partialbas pt;
      d := lpdo_partialdeg pt * n;
      if eqn(d,0) then rederr {"lpdo_exptpt: degree zero"};
      if eqn(d,1) then return b;
      return {'expt,b,d}
   end;

put('lpdoord,'psopfn,'lpdo_ord!$);

procedure lpdo_ord!$(l);
   <<
      lpdo_argnochk('lpdosym,1,1,l);
      lpdo_ord lpdo_simp car l
   >>;

procedure lpdo_ord(dp);
   begin integer ord,monord;
      for each mon in dp do <<
	 monord := lpdo_monord mon;
	 if monord > ord then ord := monord
      >>;
      return ord
   end;

procedure lpdo_monord(mon);
   for each pt in cdr mon sum lpdo_partialdeg pt;

put('lpdosym,'psopfn,'lpdo_sym!$);

procedure lpdo_sym!$(l);
   begin scalar w;
      lpdo_argnochk('lpdosym,1,3,l);
      w := lpdo_simp car l;
      return mk!*sq lpdo_sym(w,if cdr l then reval cadr l else lpdo_ord w,
      	 if cdr l and cddr l then caddr l else 'y)
   end;

switch lpdotrsym;

procedure lpdo_sym(d,m,y);
   begin scalar res;
      res := !*f2q nil;
      if m < 0 then
	 return res;
      for each mon in d do
      	 if eqn(lpdo_monord mon,m) then
	    res := addsq(res,multsq(car mon,lpdo_ptl2sym(cdr mon,y)));
      if !*lpdotrsym then <<
	 mathprint lpdo_prep d;
	 ioto_prin2t {"sym_",m,"="};
	 maprin prepsq res;
	 ioto_flush()
      >>;
      return res
   end;

procedure lpdo_ptl2sym(ptl,y);
   begin scalar v,d,w;
      w := 1;
      for each pt in ptl do <<
	 v := lpdo_partialv pt;
	 d := lpdo_partialdeg pt;
	 w := multf(w,exptf(!*k2f lpdo_mkid(y,{'!_,v,'!_}),d))
      >>;
      return !*f2q w
   end;

procedure lpdo_subst0(al,d);
   lpdo_mk!*lpdo lpdo_subst(al,lpdo_simp d);

procedure lpdo_subst(al,d);
   begin scalar w;
      return for each dmon in d join <<
	 w := subsq(car dmon,al);
       	 if numr w then {w . cdr dmon}
      >>
   end;

put('lpdos,'psopfn,'lpdo_s!$);

procedure lpdo_s!$(l);
   <<
      lpdo_argnochk('lpdo_s,3,4,l);
      mk!*sq lpdo_s(lpdo_simp car l,lpdo_simp cadr l,reval caddr l,
	 if cdddr l then cadddr l else 'y)
   >>;

procedure lpdo_s(p,q,m,y);
   begin scalar res;
      res := !*f2q nil;
      for each mon in q do
      	 if eqn(lpdo_monord mon,m) then
	    res := addsq(res,multsq(lpdo_s1(p,car mon),
	       lpdo_ptl2sym(cdr mon,y)));
      return res
   end;

procedure lpdo_s1(p,qj);
   begin scalar res,v,d,w;
      res := !*f2q nil;
      for each mon in p do <<
	 if cdr mon then <<
	    v := lpdo_partialv car cdr mon;
	    d := lpdo_partialdeg car cdr mon;
	    w := multsq(car mon,lpdo_df(prepsq qj,v,d))
	 >> else
	    w := multsq(car mon,qj);
	 res := addsq(res,w)
      >>;
      return res
   end;

put('lpdofac,'psopfn,'lpdo_fac!$);

procedure lpdo_fac!$(l);
   % Generate factorization condition PSOPFN entry point.
   begin scalar w,d,p,q,y,prulel,qrulel;
      lpdo_argnochk('lpdofac,1,4,l);
      d := lpdo_simp car l;
      l := cdr l;
      if l then << p := lpdo_simp car l; l := cdr l >> else p := '(p);
      if l then << q := lpdo_simp car l; l := cdr l >> else q := '(q);
      if l then << y := car l; l := cdr l >> else y := 'y;
      w := lpdo_fac(d,p,q,y);
      return rl_mk!*fof w
   end;

procedure lpdo_fac(d,p,q,y);
   % Genrate factorization condition. [d] is a differential
   % polynomial; [prulel], [qrulel] are alists mapping differential
   % terms to coefficient templates; [p], [q], [y] are identifiers.
   % Returns a an OFSF formula. [p], [q] are used as basenames within
   % the coefficient factors, and [y] as the basename for the symbol
   % variables. They are all invisible from outside and usually set to
   % ['p], ['q], ['y], resp., by the PSOPFN entry point. The result is
   % a first-order formula specifying necessary and sufficient
   % conditions for the reducibility of [d] into [p], [q], where [p]
   % has order one.
   begin scalar w;
      if not lpdo_templp() then
 	 rederr "lpdo_fac: use lpdoset to fix delta ring";
      if lpdo_genfunp p then
	 p := lpdo_p2pp(p,d);
      if lpdo_genfunp q then
	 q := lpdo_q2qq(q,d);
      w := for each lhs in lpdo_faclhsl(d,p,q,y) join
	 if  domainp denr lhs then
 	    {ofsf_0mk2('equal,numr lhs)}
	 else
	    {ofsf_0mk2('equal,numr lhs),ofsf_0mk2('neq,denr lhs)};
      return lpdo_facquantify(rl_smkn('and,w),d,p,q,y)
   end;

procedure lpdo_p2pp(p,d);
   lpdo_polxpnd1(p,lpdo_ptl d,1,'lpdotimes,'lpdo_simp);

procedure lpdo_q2qq(q,d);
   lpdo_polxpnd1(q,lpdo_ptl d,lpdo_ord d - 1,'lpdotimes,'lpdo_simp);

procedure lpdo_faclhsl(d,pp,qq,y);
   begin scalar lhs,rhs;
      return for i := lpdo_ord d step -1 until 0 collect <<
	 lhs := subtrsq(lpdo_sym(d,i,y),lpdo_s(pp,qq,i,y));
	 rhs := multsq(lpdo_sym(pp,1,y),lpdo_sym(qq,i-1,y));
	 lhs := subtrsq(lhs,rhs);
	 lhs
      >>
   end;

procedure lpdo_genfunp(p);
   pairp p and null cdr p and atom car p;

procedure lpdo_facquantify(phi,d,pp,qq,y);
   begin scalar dcl,pcl,qcl,pl,ql,xl,yl,w;
      dcl := lpdo_coeffs d;
      pcl := lpdo_coeffs pp;
      qcl := lpdo_coeffs qq;
      for each v in rl_fvarl phi do
	 if v memq cdr lpdopol!* then
	    xl := v . xl
	 else if (w := lpdo_mykernp v) and eqcar(w,y) then
	    yl := v . yl
	 else if v memq pcl and not (v memq dcl) then
	    pl := v . pl
	 else if v memq qcl and not (v memq dcl) then
	    ql := v . ql;
      for each v in reversip nconc(sort(xl,'ordp),sort(yl,'ordp)) do
	 phi := rl_mkq('all,v,phi);
      for each v in reversip nconc(sort(pl,'ordp),sort(ql,'ordp)) do
	 phi := rl_mkq('ex,v,phi);
      return phi
   end;

procedure lpdo_coeffs(dp);
   begin scalar kl;
      for each dmon in dp do
	 for each k in union(kernels numr car dmon,kernels denr car dmon) do
	    if not eqcar(k,'partial) then
	       kl := lto_insertq(k,kl);
      return kl
   end;

procedure lpdo_mykernp(id);
   begin scalar v,expl;
      if not idp id then
      	 return nil;
      expl := reversip explode id;
      if car expl neq '!_ then
	 return nil;
      expl := reversip cdr expl;
      v := car expl;
      expl := cdr expl;
      if car expl neq '!_ then
	 return nil;
      expl := cdr expl;
      return intern v . intern compress('!! . expl)
   end;

put('lpdofacx,'psopfn,'lpdo_facx!$);

procedure lpdo_facx!$(l);
   begin scalar w,d,psi,p,q,y,eps;
      lpdo_argnochk('lpdofacx,1,6,l);
      d := lpdo_simp pop l;
      psi := rl_simp if l then pop l else 'true;
      p := if l then lpdo_simp pop l else '(p);
      q := if l then lpdo_simp pop l else '(q);
      eps := if l then pop l else 'epsilon;
      y := if l then pop l else 'y;
      w := lpdo_facx(d,psi,p,q,eps,y);
      return rl_mk!*fof w
   end;

procedure lpdo_facx(d,psi,p,q,eps,y);
   % [d] is an LPDO, [psi] is formula, [p] and [q] are generic LPDO
   % templates, [eps] is an identifier, [y] is an identifier.
   begin scalar w;
      if not lpdo_templp() then
 	 rederr "lpdo_fac: use lpdoset to fix delta ring";
      if lpdo_genfunp p then
	 p := lpdo_p2pp(p,d);
      if lpdo_genfunp q then
	 q := lpdo_p2pp(q,d);
      w := if !*lpdocoeffnorm then
	 for each lhs in lpdo_faclhsl(d,p,q,y) collect <<
	    if not domainp denr lhs then
	       lprim {"dropping denominator in equation:",prepf denr lhs};
	    lpdo_absleq(numr lhs,eps,y)
      	 >> else
	    for each lhs in lpdo_faclhsl(d,p,q,y) collect <<
	       if not domainp denr lhs then
	    	  lprim {"dropping denominator in equation:",prepf denr lhs};
	       lpdo_absleq_lasaruk(numr lhs,eps,y)
	    >>;
      w := rl_mk2('impl,psi,rl_smkn('and,w));
      return lpdo_facquantify(w,d,p,q,y)
   end;

procedure lpdo_absleq(lhs,eps,y);
   rl_smkn('and,for each f in lpdo_allcoeffs(lhs,lpdo_ylist(lhs,y)) collect
      rl_mkn('and,{
	 ofsf_0mk2('leq,addf(negf f,negf numr simp eps)),
	 ofsf_0mk2('leq,addf(f,negf numr simp eps))}));

procedure lpdo_absleq_lasaruk(lhs,eps,y);
   begin scalar s;
      for each f in lpdo_allcoeffs(lhs,lpdo_ylist(lhs,y)) do
	 s := addf(s,f);
      return rl_mkn('and,{
	 ofsf_0mk2('leq,addf(negf s,negf numr simp eps)),
	 ofsf_0mk2('leq,addf(s,negf numr simp eps))})
   end;

procedure lpdo_allcoeffs(f,vl);
   lpdo_allcoeffs1({f},vl);

procedure lpdo_allcoeffs1(l,vl);
   if null vl then
      l
   else
      lpdo_allcoeffs1(for each f in l join
	 lpdo_coefs(sfto_reorder(f,car vl),car vl),cdr vl);

procedure lpdo_ylist(lhs,y);
   begin scalar w;
      return for each v in kernels lhs join <<
	 w := lpdo_mykernp v;
      	 if w and eqcar(w,y) then {v}
      >>
   end;

procedure lpdo_coefs(f,v);
   if not domainp f and mvar f eq v then coeffs f else {f};

procedure lpdo_absleq_old(lhs,eps,y);
   rl_mkn('and,{
      ofsf_0mk2('leq,addf(negf lhs,negf numr simp eps)),
      ofsf_0mk2('leq,addf(lhs,negf numr simp eps))});

put('lpdoptl,'psopfn,'lpdo_ptl!$);

procedure lpdo_ptl!$(l);
   % List of partials PSOPFN entry point.
   <<
      lpdo_argnochk('lpdoptl,1,1,l);
      'list . lpdo_ptl lpdo_simp car l
   >>;

procedure lpdo_ptl(d);
   % List of partials. [d] is a differential polynomial. Returns a
   % list of kernels.
   begin scalar res;
      for each mon in d do
	 for each pt in cdr mon do
	    res := lto_insert(lpdo_partialbas pt,res);
      return sort(res,'ordp)
   end;

put('lpdogp,'psopfn,'lpdo_gp!$);

procedure lpdo_gp!$(l);
   % Generic polynomial PSOPFN entry point.
   <<
      lpdo_argnochk('lpdogp,3,3,l);
      mk!*sq lpdo_polxpnd1(car l,cdr reval cadr l,reval caddr l,'times,'simp)
   >>;

put('lpdogdp,'psopfn,'lpdo_gdp!$);

procedure lpdo_gdp!$(l);
   % Generic differential polynomial PSOPFN entry point.
   <<
      lpdo_argnochk('lpdogdp,3,3,l);
      lpdo_mk!*lpdo lpdo_polxpnd1(car l,
	 for each v in cdr reval cadr l collect
 	    if idp v then {'partial,v} else v,
	 reval caddr l,'lpdotimes,'lpdo_simp)
   >>;

put('lpdosym2dp,'psopfn,'lpdo_sym2dp!$);

procedure lpdo_sym2dp!$(l);
   <<
      lpdo_argnochk('lpdosym2dp,1,2,l);
      lpdo_mk!*lpdo lpdo_sym2dp(simp car l,if cdr l then cadr l else 'y)
   >>;

procedure lpdo_sym2dp(q,v);
   lpdo_quot(lpdo_sym2dp1(numr q,v),lpdo_simp prepf denr q);

procedure lpdo_sym2dp1(f,v);
   if domainp f then
      lpdo_simp prepf f
   else
      lpdo_add(lpdo_mult1(lpdo_sym2dp1(lc f,v),
	 lpdo_simp {'expt,lpdo_sym2dpv(mvar f,v),ldeg f}),
	 lpdo_sym2dp1(red f,v));

procedure lpdo_sym2dpv(kn,v);
   begin scalar ekn;
      ekn := explode kn;
      if not eqcar(ekn,v) then return kn;
      ekn := cdr ekn;
      if not eqcar(ekn,'!_) then return kn;
      ekn := reversip cdr ekn;
      if not eqcar(ekn,'!_) then return kn;
      ekn := reversip cdr ekn;
      return {'partial,intern compress ekn}
   end;

procedure lpdo_mult1(p,q);
   % Multiply differential expressions p and q in a commutative way
   % since they origin from Sym()'s, the representation of which is
   % disturbed by the kernel order for commutative SF's.
   lpdo_compact(for each pmon in p join
      for each qmon in q collect
	 multsq(car pmon,car qmon) . append(cdr pmon,cdr qmon));

put('lpdohrect,'psopfn,'lpdo_hrect!$);

procedure lpdo_hrect!$(l);
   <<
      lpdo_argnochk('lpdohrect,1,1,l);
      rl_mk!*fof lpdo_hrect(car l)
   >>;

procedure lpdo_hrect(m);
   begin scalar mv;
      if not lpdo_templp() then
      	 rederr "lpdo_hrect: use lpdoset to fix delta ring";
      return rl_mkn('and,for each v in cdr lpdopol!* join <<
      	 mv := if numberp m then m else !*k2f lpdo_mkid(m,{'!_,v,'!_});
	 {ofsf_0mk2('leq,addf(negf mv,negf !*k2f v)),
	    ofsf_0mk2('leq,addf(!*k2f v,negf mv))}
      >>)
   end;

put('lpdohcirc,'psopfn,'lpdo_hcirc!$);

procedure lpdo_hcirc!$(l);
   <<
      lpdo_argnochk('lpdocirc,1,1,l);
      rl_mk!*fof lpdo_hcirc(car l)
   >>;

procedure lpdo_hcirc(r);
   begin scalar lhs;
      if not lpdo_templp() then
      	 rederr "lpdo_hcirc: use lpdoset to fix delta ring";
      r := if numberp r then r**2 else exptf(!*k2f r,2);
      for each v in cdr lpdopol!* do
	 lhs := addf(lhs,exptf(!*k2f v,2));
      return ofsf_0mk2('leq,addf(lhs,negf r))
   end;

put('lpdofactorize,'psopfn,'lpdo_factorize!$);

procedure lpdo_factorize!$(l);
   % Factorize PSOPFN entry point.
   <<
      lpdo_argnochk('lpdofactorize,1,3,l);
      reval {'lpdo_factorize,
	 car l,
	 if cdr l then
 	    cadr l
 	 else
 	    {'lpdoglfac,car l},
	 if cdr l and cddr l then
 	    caddr l
 	 else
 	    {'lpdogofac,car l}}
   >>;

algebraic procedure lpdo_factorize(f,p,q);
   % Factorize. [f] is a differential polynomial; [p], [q] are
   % identifiers. Returns a list $(A,L)$, where $A$ is one of the
   % identifiers [true], [false], and $L$ is a list of differential
   % polynomials. [p] and [q] are generic coefficients, where [p] is
   % linear. In the result [true] indicates reducibility; in the
   % positive case $L$ contains two factors, the first of which is
   % linear.
   begin scalar ff,so,p0,q0,gamma,failedp,w; % ,!*rlverbose;
      on rlqeaprecise;
      ff := lpdofac(f,p,q);
      so := rlqea ff;
      so := for each bra in so join <<
	 gamma := rlsimpl first bra;
	 if gamma = false then
	    {}
	 else if gamma = true then <<
      	    p0 := sub(second bra,p);
      	    q0 := sub(second bra,q);
	    {lpdo_fixsign(p0,q0)}
	 >> else <<
	    failedp := t;
	    {}
	 >>
      >>;
      if so = {} and failedp then return failed;
      return so
   end;

operator lpdo_fixsign;

procedure lpdo_fixsign(p0,q0);
   <<
      p0 := lpdo_simp p0;
      q0 := lpdo_simp q0;
      p0 . q0 := lpdo_fixsign0(p0,q0);
      {'list,lpdo_mk!*lpdo p0,lpdo_mk!*lpdo q0}
   >>;

procedure lpdo_fixsign0(p0,q0);
   if p0 and minusf numr caar p0 then
      lpdo_minus p0 . lpdo_minus q0
   else
      p0 . q0;

algebraic procedure lpdoglfac(f);
   lpdogdp(p(),lpdoptl f,1);

algebraic procedure lpdogofac(f);
   lpdogdp(q(),lpdoptl f,lpdoord f - 1);

put('lpdofactorizex,'psopfn,'lpdo_factorizex!$);

procedure lpdo_factorizex!$(l);
   begin scalar af,f,psi,p,q,eps;
      lpdo_argnochk('lpdofactorizex,1,5,l);
      af := pop l;
      f := lpdo_simp af;
      psi := if l then rl_simp pop l else 'true;
      p := if l then lpdo_simp pop l else '(p); %reval {'lpdoglfac,af};
      q := if l then lpdo_simp pop l else '(q); %reval {'lpdogofac,af};
      eps := if l then pop l else 'epsilon;
      return 'list . for each bra in lpdo_factorizex(f,psi,p,q,eps) collect
	 {'list,
	    rl_prepfof car bra,
	    {'list,lpdo_prep caadr bra,lpdo_prep cadadr bra}}
   end;

procedure lpdo_factorizex(f,psi,p,q,eps);
   begin scalar ff,so,p0,q0,res,w,gamma,al,ww;%,!*rlverbose;
      on1 'rlqeaprecise;
      if not lpdo_templp() then
 	 rederr "lpdo_fac: use lpdoset to fix delta ring";
      if lpdo_genfunp p then
	 p := lpdo_p2pp(p,f);
      if lpdo_genfunp q then
	 q := lpdo_p2pp(q,f);
      ff := lpdo_facx(f,psi,p,q,eps,'y);
      so := rl_qea(ff,nil);
      for each bra in so do <<
	 al := for each eqn in cadr bra collect cadr eqn . caddr eqn;
      	 p0 := lpdo_subst(al,p);
      	 q0 := lpdo_subst(al,q);
	 gamma := car bra;
	 if gamma neq 'false then <<
	    ww := lpdo_fixsign0(p0,q0);
	    w := {gamma,{car ww,cdr ww}};
	    res := lto_insert(w,res)
	 >>
      >>;
      return reversip res
   end;

procedure lpdo_argnochk(name,mi,ma,argl);
   % Check number of arguments. [name] is an identifier; [mi], [ma]
   % are numbers; [argl] is a list. Returns [nil] or exists with an
   % error.
   begin integer len;
      len := length argl;
      if len < mi or len > ma then
	 rederr {name,"called with",len,"aruments instead of",mi,"-",ma}
   end;

endmodule;

end;
