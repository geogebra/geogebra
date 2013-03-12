% ----------------------------------------------------------------------
% $Id: sfto.red 1841 2012-11-19 12:27:53Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 A. Dolzmann, T. Sturm, 2010 T. Sturm
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

lisp <<
   fluid '(sfto_rcsid!* sfto_copyright!*);
   sfto_rcsid!* := "$Id: sfto.red 1841 2012-11-19 12:27:53Z thomas-sturm $";
   sfto_copyright!* := "(c) 1995-2009 A. Dolzmann, T. Sturm, 2010 T. Sturm"
>>;

module sfto;
% Standard form tools.

fluid '(!*ezgcd !*gcd !*rldavgcd !*rational);

switch sfto_yun,sfto_tobey,sfto_musser;
!*sfto_yun := T;

put('sqfpart,'polyfn,'sfto_sqfpartf);
put('tsqsum,'psopfn,'sfto_tsqsum!$);
put('sqfdec,'psopfn,'sfto_sqfdec!$);
put('pdec,'psopfn,'sfto_pdec!$);
put('sfto_yun,'simpfg,'((T (setq !*sfto_tobey nil) (setq !*sfto_musser nil))));
put('sfto_tobey,'simpfg,'((T (setq !*sfto_yun nil) (setq !*sfto_musser nil))));
put('sfto_musser,'simpfg,'((T (setq !*sfto_tobey nil) (setq !*sfto_yun nil))));

operator exteuc;
operator degree;
operator coefficients;

struct AmList checked by am_listp;
struct AmPoly checked by am_polyp;

declare addf: (SF,SF) -> SF;
declare multf: (SF,SF) -> SF;
declare negf: (NoOrdSF) -> NoOrdSF;

declare sfto_dcontentf: (SF) -> Domain;

procedure sfto_dcontentf(u);
   % Domain content standard form. Returns the (non-negative) content of [u]
   % considered as a multivariate polynomial over the current domain.
   sfto_dcontentf1(u,nil);


declare sfto_dcontentf1: (SF,Domain) -> Domain;

procedure sfto_dcontentf1(u,g);
   % Domain content standard form subroutine. Returns the gcd of the content of
   % [u] considered as a multivariate polynomial and [g].
   if g = 1 then
      g
   else if domainp u then
      sfto_gcdf(absf u,g)
   else
      sfto_dcontentf1(red u,sfto_dcontentf1(lc u,g));


declare sfto_dprpartf: (SF) -> SF;

procedure sfto_dprpartf(u);
   % Domain primitive part standard form. Returns the primitive part of [u] as a
   % multivariate polynomial over the current domain. The sign of the head
   % coefficient in this primitive part is positive.
   sfto_dprpartf1(u,sfto_dcontentf u);


declare sfto_dprpartksf: (SF) -> SF;

procedure sfto_dprpartksf(u);
   % Domain primitive part standard form keep sign. Returns the primitive part
   % of [u] as a multivariate polynomial over the current domain. The sign of
   % the head coefficient in this primitive part is that of [u].
   quotf(u,sfto_dcontentf u);


declare sfto_dprpartf1: (SF,SF) -> SF;

procedure sfto_dprpartf1(u,c);
   % Domain primitive part standard form subroutine. Returns the primitive part
   % of [u] considered as a multivariate polynomial over the current domain.
   (if minusf w then negf w else w) where w = quotf(u,c);


declare sfto_sqfpartf: (SF!*) -> SF!*;

procedure sfto_sqfpartf(u);
   % Square-free part. Returns the square-free part of [u] as a multivariate
   % polynomial. The (domain) content is dropped.
   begin scalar c,pp;
      if domainp u then return 1;
      c := sfto_ucontentf u;
      pp := quotf(u,c);
      return multf(sfto_sqfpartf(c),quotf(pp,sfto_gcdf!*(pp,diff(pp,mvar u))))
   end;


declare sfto_ucontentf: (SF) -> SF;

procedure sfto_ucontentf(u);
   % Univariate content standard form. Returns the content of [u] considered as
   % a univariate polynomial in its [mvar] over the polynomial ring in all other
   % contained variables.
   if domainp u then u else sfto_ucontentf1(u,mvar u);


declare sfto_ucontentf1: (SF,Kernel) -> SF;

procedure sfto_ucontentf1(u,v);
   % Univariate content standard form subroutine. We assume [mvar u = v].
   % Returns the content of [u] as an univariate polynomial in [v] over the
   % polynomial ring in all other contained variables.
   if domainp u or mvar u neq v then u else
      sfto_gcdf!*(lc u,sfto_ucontentf1(red u,v));


declare sfto_uprpartf: (SF) -> SF;

procedure sfto_uprpartf(u);
   % Univariate primitive part. Returns the primitive part of [u] as a
   % univariate polynomial in its [mvar] over the polynomial ring in all other
   % contained variables.
   quotf(u,sfto_ucontentf u);


declare sfto_tsqsumf: (SF) -> Id;

procedure sfto_tsqsumf(u);
   % Trivial square sum standard form. Returns one of [nil], ['stsq], or ['tsq].
   % ['stsq] means that in the sparse distributive representation of [u] all
   % exponents are even and all coefficients are positive. ['tsq] means that all
   % exponents are even and all coefficients are positive except for that there
   % is no absolute summand.
   if domainp u then
      (if null u then 'tsq else if not minusf u then 'stsq)
   else
      evenp ldeg u and sfto_tsqsumf lc u and sfto_tsqsumf red u;


declare sfto_tsqsum!$: (List) -> Id;

procedure sfto_tsqsum!$(argl);
   sfto_tsqsumf(numr simp car argl);


declare sfto_sqfdecf: (SF) -> Alist;

procedure sfto_sqfdecf(u);
   % Multivariate square-free decomposition standard form. Returns a (dense)
   % list $((q_1 . 1),(q_2 . 2),...,(q_n . n))$ such that $\prod q_i^i = u$ with
   % the $q_i$ square-free and pairwise relatively prime. The (integer) content
   % of u is dropped. Decomposition is performed by merging univariate
   % decompositions. The univariate decomposition method is selected by turning
   % on one of the switches [sfto_yun] (default), [sfto_tobey], or
   % [sfto_musser].
   begin scalar c,pp;
      if domainp u then return {1 . 1};
      c := sfto_ucontentf u;
      pp := quotf(u,c);
      return sfto_sqdmerge(sfto_sqfdecf(c),sfto_usqfdecf(pp))
   end;


declare sfto_sqfdec!$: (List) -> AmList;

procedure sfto_sqfdec!$(argl);
   % Square free decomposition. [argl] is an argument list. Returns an AM list
   % of AM lists of the form $(p_i,m_i)$, where the $p_i$ are polynomials
   % represented as a Lisp prefix form and the $m_i$ are integers.
   begin scalar w;
      return 'list . for each x in sfto_sqfdecf numr simp car argl join
      	 if (w := prepf car x) neq 1 then {{'list,w,cdr x}}
   end;


declare sfto_usqfdecf: (SF) -> Alist;

procedure sfto_usqfdecf(u);
   if !*sfto_yun then
      sfto_yun!-usqfdecf u
   else if !*sfto_musser then
      sfto_musser!-usqfdecf u
   else if !*sfto_tobey then
      sfto_tobey!-usqfdecf u
   else
      rederr {"sfto_usqfdecf: select a decomposition method"};


declare sfto_yun!-usqfdecf: (SF) -> Alist;

procedure sfto_yun!-usqfdecf(p);
   % Univariate square-free decomposition after Yun. [p] is a an SF that is
   % viewed a univariate Polynomial in its [mvar] over the polynomial ring in
   % all other variables; in this sense, [p] must be primitive. Returns the
   % square-free decomposition of [p] as a (dense) list $((q_1 . 1),(q_2 .
   % 2),...,(q_n . n))$ such that $\prod q_i^i = u$ with the $q_i$ square-free
   % and pairwise relatively prime.
   begin scalar !*gcd,x,g,c,d,w,l; integer n;
      !*gcd := T;
      x := mvar p;
      g := sfto_gcdf(p,w := diff(p,x));
      c := quotf(p,g);
      d := addf(quotf(w,g),negf(diff(c,x)));
      repeat <<
	 p := sfto_gcdf(c,d);
	 l := (p . (n := n+1)) . l;
	 c := quotf(c,p);
	 d := addf(quotf(d,p),negf(diff(c,x)))
      >> until domainp c;
      return reversip l
   end;


declare sfto_musser!-usqfdecf: (SF) -> Alist;

procedure sfto_musser!-usqfdecf(u);
   % Univariate square-free decomposition after Musser. [p] is a an SF that is
   % viewed a univariate Polynomial in its [mvar] over the polynomial ring in
   % all other variables; in this sense, [p] must be primitive. Returns the
   % square-free decomposition of [p] as a (dense) list $((q_1 . 1),(q_2 .
   % 2),...,(q_n . n))$ such that $\prod q_i^i = u$ with the $q_i$ square-free
   % and pairwise relatively prime.
   begin scalar !*gcd,v,u1,sqfp,sqfp1,l; integer n;
      !*gcd := T;
      v := mvar u;
      u1 := sfto_gcdf(u,diff(u,v));
      sqfp := quotf(u,u1);
      while degr(u1,v)>0 do <<
	 sqfp1 := sfto_gcdf(sqfp,u1);
	 l := (quotf(sqfp,sqfp1) . (n := n+1)) . l;
	 u1 := quotf(u1,sqfp1);
	 sqfp := sqfp1
      >>;
      l := (sqfp . (n := n+1)) . l;
      return reversip l
   end;


declare sfto_tobey!-usqfdecf: (SF) -> Alist;

procedure sfto_tobey!-usqfdecf(u);
   % Univariate square-free decomposition after Tobey and Horowitz. [p] is a an
   % SF that is viewed a univariate Polynomial in its [mvar] over the polynomial
   % ring in all other variables; in this sense, [p] must be primitive. Returns
   % the square-free decomposition of [p] as a (dense) list $((q_1 . 1),(q_2 .
   % 2),...,(q_n . n))$ such that $\prod q_i^i = u$ with the $q_i$ square-free
   % and pairwise relatively prime.
   begin scalar !*gcd,v,h,q1,q2,l; integer n;
      !*gcd := T;
      v := mvar u;
      h := sfto_gcdf(u,diff(u,v));
      q2 := quotf(u,h);
      while degr(u,v)>0 do <<
	 u := h;
	 q1 := q2;
	 h := sfto_gcdf(u,diff(u,v));
	 q2 := quotf(u,h);
	 l := (quotf(q1,q2) . (n := n+1)) . l
      >>;
      return reversip l
   end;


declare sfto_sqdmerge: (Alist,Alist) -> Alist;

procedure sfto_sqdmerge(l1,l2);
   % Square-free decomposition merge.
   begin scalar l;
      l := l1;
      while l1 and l2 do <<
	 caar l1 := multf(caar l1,caar l2);
      	 l1 := cdr l1;
	 l2 := cdr l2
      >>;
      if l2 then l := nconc(l,l2);
      return l
   end;


declare sfto_pdecf: (SF) -> DottedPair;

procedure sfto_pdecf(u);
   % Multivariate parity decomposition. Returns $a . d$ such that $a$ is the
   % product of all square-free factors with an odd multiplicity in [u] and $d$
   % is that of the even multiplicity square-free factors. The (integer) content
   % of u is dropped. Decomposition is performed by merging univariate
   % decompositions. The univariate decomposition method is selected by turning
   % on one of the switches [sfto_yun] (default), [sfto_musser].
   begin scalar c,dpdc,dpdpp;
      if domainp u then return 1 . 1;
      c := sfto_ucontentf u;
      dpdc := sfto_pdecf c;
      dpdpp := sfto_updecf quotf(u,c);
      return multf(car dpdc,car dpdpp) . multf(cdr dpdc,cdr dpdpp)
   end;


declare sfto_updecf: (SF) -> DottedPair;

procedure sfto_updecf(u);
   if !*sfto_yun then
      sfto_yun!-updecf u
   else if !*sfto_musser then
      sfto_musser!-updecf u
   else
      rederr {"sfto_updecf: select a decomposition method"};


declare sfto_yun!-updecf: (SF) -> DottedPair;

procedure sfto_yun!-updecf(p);
   begin scalar !*gcd,x,g,c,d,w,l,od;
      !*gcd := T;
      l := 1 . 1;
      x := mvar p;
      g := sfto_gcdf(p,w := diff(p,x));
      c := quotf(p,g);
      d := addf(quotf(w,g),negf(diff(c,x)));
      repeat <<
	 od := not od;
	 p := sfto_gcdf(c,d);
	 if od then car l := multf(car l,p) else cdr l := multf(cdr l,p);
	 c := quotf(c,p);
	 d := addf(quotf(d,p),negf(diff(c,x)))
      >> until domainp c;
      return l
   end;


declare sfto_musser!-updecf: (SF) -> DottedPair;

procedure sfto_musser!-updecf(u);
   begin scalar !*gcd,od,v,u1,sqfp,sqfp1,l;
      !*gcd := T;
      od := T;
      l := 1 . 1;
      v := mvar u;
      u1 := sfto_gcdf(u,diff(u,v));
      sqfp := quotf(u,u1);
      while degr(u1,v)>0 do <<
	 sqfp1 := sfto_gcdf(sqfp,u1);
	 if od then
 	    car l := multf(car l,quotf(sqfp,sqfp1))
	 else
	    cdr l := multf(cdr l,quotf(sqfp,sqfp1));
	 u1 := quotf(u1,sqfp1);
	 sqfp := sqfp1;
	 od := not od
      >>;
      if od then
	 car l := multf(car l,sqfp)
      else
	 cdr l := multf(cdr l,sqfp);
      return l
   end;


declare sfto_pdec!$: (List) -> AmList;

procedure sfto_pdec!$(argl);
   {'list,prepf car w,prepf cdr w}
      where w=sfto_pdecf numr simp car argl;


declare sfto_decdegf: (SF,Kernel,Integer) -> SF;

procedure sfto_decdegf(u,k,n);
   % Decrement degree standard form. Replace each occurence of $[k]^d$ by
   % $k^(d/n)$.
   begin scalar sal,hit,newkk;
      if !*rlbrkcxk then <<
	 for each kk in kernels u do
	    if pairp kk then <<
	       hit . newkk := sfto_decdegcxk(kk,k,n,nil);
	       if hit then
	       	  sal := (kk . newkk) . sal
	    >>;
	 u := numr subf(u,sal)
      >>;
      u := reorder sfto_decdegf1(sfto_reorder(u,k),k,n);
      return u
   end;


declare sfto_decdegcxk: (Kernel,Kernel,Integer,Boolean) -> DottedPair;

procedure sfto_decdegcxk(kk,k,n,hit);
   % Decrement degree complex kernel. Returns $h . l$, where $h$ is boolean and
   % $l$ is lisp prefix. $l$ is obtained by replacing in [kk] each occurrence of
   % $[k]^d$ by $k^(d/n)$; $h$ is [t] iff $h$ is not equal to [kk].
   begin scalar w,argl;
      if not pairp kk then
	 return hit . kk;
      if car kk eq 'expt and cadr kk eq k then
	 return t . {'expt,k,caddr kk / n};
      for each arg in cdr kk do <<
	 hit . w := sfto_decdegcxk(arg,k,n,hit);
	 argl := w . argl
      >>;
      return hit . (car kk . reversip argl)
   end;


declare sfto_decdegf1: (Any,Kernel,Integer) -> Any;

procedure sfto_decdegf1(u,k,n);
   % Decrement degree standard form. [u] is an SF with main variable [k]; [k] is
   % a variable; [n] is an integer. Returns an SF. Replace each occurence of
   % $[k]^d$ by $k^(d/n)$.
   if degr(u,k)=0 then
      u
   else
      mvar u .** (ldeg u / n) .* lc u .+ sfto_decdegf1(red u,k,n);


declare sfto_reorder: (SF,Kernel) -> Any;

procedure sfto_reorder(u,v);
   % Reorder. Returns [u] reorderd wrt. [{v}] without modifiying the current
   % kernel order.
   begin scalar w;
      w := setkorder {v};
      u := reorder u;
      setkorder w;
      return u
   end;


declare sfto_groebnerf: (List) -> List;

procedure sfto_groebnerf(l);
   % Groebner calculation standard form. [l] is a list of SF's. Returns a list
   % of SF's. The returned list is the reduced Groebner basis of [l] wrt. the
   % current term order.
   begin scalar w;
      if null l then return nil;
      w := groebnereval {'list . for each sf in l collect prepf sf};
      return for each x in cdr w collect
	 numr simp x
   end;


declare sfto_preducef: (SF,List) -> SF;

procedure sfto_preducef(f,gl);
   % Polynomial reduction standard form. [gl] is a list of SF's. Returns the
   % numerator of [f] reduced modulo [gl].
   if null gl then
      f
   else if (null cdr gl) and (domainp car gl) then
      nil
   else
      numr simp preduceeval {prepf f,'list . for each sf in gl collect prepf sf};

declare sfto_greducef: (SF,List) -> SF;

procedure sfto_greducef(f,gl);
   % Polynomial reduction standard form. [gl] is a list of SF's. Returns [f]
   % reduced modulo a Groebner basis of [gl].
   if null gl then
      f
   else if (null cdr gl) and (domainp car gl) then
      nil
   else
      numr simp greduceeval {prepf f,'list . for each sf in gl collect prepf sf};

declare sfto_gcdf!*: (SF,SF) -> SF;

procedure sfto_gcdf!*(f,g);
   % Greatest common divisor of standard forms. Returns the GCD of [f] and [g].
   % Compute the GCD of [f] and [g] via [gcdf!*] or [ezgcdf] according to
   % Davenport's criterion: If, in one polynomial, the number of variables of a
   % degree greater than 2 is greater than 1, then use [ezgcd].
   sfto_gcdf(f,g) where !*gcd=T;


declare sfto_gcdf: (SF,SF) -> SF;

procedure sfto_gcdf(f,g);
   % Greatest common divisor of standard forms. Returns the GCD of [f] and [g]
   % computed via [gcdf!*] or [ezgcdf] according to Davenport's criterion: If,
   % in one polynomial, the number of variables of a degree greater than 2 is
   % greater than 1, then use [ezgcd]. For computing the real gcd of [f] ang [g]
   % this procedures require, that [!*gcd] is set to [T].
   if null !*rldavgcd then
      gcdf(f,g)
   else if sfto_davp(f,nil) or sfto_davp(g,nil) then
      gcdf(f,g) where !*ezgcd=nil
   else
      ezgcdf(f,g);


declare sfto_davp: (SF,Any) -> Boolean;

procedure sfto_davp(f,badv);
   % Davenport predicate. [v] is a kernel or [nil]. Returns [t] if [gcdf] if
   % heuristically considered more efficient than [ezgcdf].
   if domainp f then
      T
   else if ldeg f > 2 then
      if badv and mvar f neq badv then
	 nil
      else
	 sfto_davp(lc f,mvar f) and sfto_davp(red f,mvar f)
   else
      sfto_davp(lc f,badv) and sfto_davp(red f,badv);


declare sfto_sqrtf: (SF) -> ExtraBoolean;

procedure sfto_sqrtf(f);
   % Square root standard form. Returns [nil] or an SF $g$, such that $g^2=[f]$.
   begin scalar a,c,w,sd,result;
      c := sfto_dcontentf(f);
      result := isqrt c;
      if result**2 neq c then
	 return nil;
      sd := sfto_sqfdecf(f);
      w := sd;
      while sd do <<
	 a := car sd;
      	 sd := cdr sd;
	 if not(evenp cdr a) and car a neq 1 then <<
 	    sd := nil;
	    a := 'break
	 >> else
	    result := multf(result,exptf(car a,cdr a / 2 ))
      >>;
      if a neq 'break and exptf(result,2) = f then
	 return result
   end;


declare sfto_monfp: (SF) -> Boolean;

procedure sfto_monfp(sf);
   % Monomial predicate. Check if [sf] is of the form $a x_1 \dots x_n$ for a
   % domain element $a$ and kernels $x_i$.
   domainp sf or (null red sf and sfto_monfp lc sf);


declare sfto_sqfpartz: (Integer) -> Integer;

procedure sfto_sqfpartz(z);
   % Square free part of an integer. [z] is an integer with prime decomposition
   % $p_1^{e_1}\cdots p_n^{e_n}$. Returns $\prod \{p_i\}$.
   sfto_zdgen(z,0);


declare sfto_zdeqn: (Integer,Integer) -> Integer;

procedure sfto_zdeqn(z,n);
   % Z decomposition equal n. [z] is an integer with prime decomposition
   % $p_1^{e_1}\cdots p_m^{e_m}$; [n] is a positive integer. Returns $\prod
   % \{p_i:e_i=n\}$.
   for each x in zfactor z product
      if cdr x = n then car x else 1;


declare sfto_zdgtn: (Integer,Integer) -> Integer;

procedure sfto_zdgtn(z,n);
   % Z decomposition greater than n. [z] is an integer with prime decomposition
   % $p_1^{e_1}\cdots p_n^{e_n}$; [n] is a positive integer. Returns $\prod
   % \{p_i:e_i>n\}$.
   for each x in zfactor z product
      if cdr x > n then car x else 1;


declare sfto_zdgen: (Integer,Integer) -> Integer;

procedure sfto_zdgen(z,n);
   % Z decomposition greater than or equal to n. [z] is an integer with prime
   % decomposition $p_1^{e_1}\cdots p_n^{e_n}$; [n] is a positive integer.
   % Returns $\prod \{p_i:e_i\geq n\}$.
   for each x in zfactor z product
      if cdr x >= n then car x else 1;


declare sfto_exteucf: (SF,SF) -> List3;

procedure sfto_exteucf(a,b);
   % Extended Euclidean Algorithm for polynomials. [a], [b] are univariate
   % polynomials both in the same variable. Returns a list $(g,s,t)$ of SQ such
   % that $\gcd([a],[b])=g=s[a]+t[b]$. If the GCD $g$ is a domain element, then
   % it is normalized to $1$.
   begin scalar w,s,tt,u,v,s1,t1,!*rational;
      on1 'rational;
      s := numr simp 1;
      tt := numr simp 0;
      u := numr simp 0;
      v := numr simp 1;
      while not null b do <<
	 w := qremf(a,b);
	 a := b;
	 b := cdr w;
	 s1 := s;
	 t1 := tt;
	 s := u;
	 tt := v;
	 u := addf(s1,negf multf(car w,u));
	 v := addf(t1,negf multf(car w,v))
      >>;
      if domainp a then <<
	 s := quotf(s,a);
	 tt := quotf(tt,a);
	 a := 1
      >>;
      off1 'rational;
      return {resimp !*f2q a,resimp !*f2q s,resimp !*f2q tt}
   end;


declare exteuc: (AmPoly,AmPoly) -> AmList;

procedure exteuc(a,b);
   begin scalar af,bf,ka,kb;
      af := numr simp a;
      bf := numr simp b;
      if domainp af and domainp bf then
	 return 'list . sfto_exteucd(a,b);
      ka := kernels af;
      if (ka and cdr ka) or (kb and cdr kb) then
	 rederr "non-univariate input polynomial";
      return 'list . for each x in sfto_exteucf(af,bf) collect prepsq x
   end;


declare sfto_exteucd: (Integer,Integer) -> List3;

procedure sfto_exteucd(a,b);
   % Extended Euclidean Algorithm for domain elements (integers). Returns a list
   % $(g,s,t)$ of numbers such that $g>=0$ and $\gcd([a],[b])=g=s[a]+t[b]$.
   begin integer s,tt,u,v,s1,t1,q,r;
      s := 1;
      v :=  1;
      while not eqn(b,0) do <<
	 q := a / b;
	 r := remainder(a,b);
	 a := b;
	 b := r;
	 s1 := s;
	 t1 := tt;
	 s := u;
	 tt := v;
	 u := s1 - q * u;
	 v := t1 - q * v
      >>;
      if a < 0 then <<
	 s := -s;
	 tt := -tt;
	 a := -a
      >>;
      return {a,s,tt}
   end;


declare sfto_linp: (SF,List) -> Boolean;

procedure sfto_linp(f,vl);
   % Linear predicate. [vl] is a list of variables. Returns [T] iff [f] is
   % linear in [vl].
   sfto_linp1(f,vl,nil);


declare sfto_linp1: (SF,List,List) -> Boolean;

procedure sfto_linp1(f,vl,oc);
   domainp f or
      (not(mvar f memq vl) and not(mvar f memq oc) and
	 sfto_linp1(lc f,vl,oc) and sfto_linp1(red f,vl,oc)) or
      (mvar f memq vl and not(mvar f memq oc) and ldeg f = 1 and
	 sfto_linp1(lc f,vl,mvar f . oc) and sfto_linp1(red f,vl,oc));


declare sfto_linwpp: (SF,List) -> Boolean;

procedure sfto_linwpp(f,vl);
   % Linear and weakly parametric predicate. [vl] is a list of variables.
   % Returns [t] iff [f] is linear and weakly parametric in [vl].
   domainp f or
      (not(mvar f memq vl) and null intersection(kernels lc f,vl) and
	 sfto_linwpp(red f,vl)) or
      (mvar f memq vl and ldeg f = 1 and domainp lc f and
	 sfto_linwpp(red f,vl));


declare sfto_varf: (SF) -> ExtraBoolean;

procedure sfto_varf(f);
   % Variable form. If [f] is a variable then return the corresponding kernel
   % else return [nil]. In contrast to sfto_idvarf, this function rerturn also
   % non-atomic kernels.
   if not domainp f and null red f and eqn(lc f,1) and eqn(ldeg f,1) then
      mvar f;


declare sfto_idvarf: (SF) -> ExtraBoolean;

procedure sfto_idvarf(f);
   % Identifier variable form. If [f] is an atomic variable then return the
   % corresponding identifier else return [nil].
   if not domainp f and
      null red f and eqn(lc f,1) and eqn(ldeg f,1) and idp mvar f
   then
      mvar f;


declare sfto_lmultf: (List) -> SF;

procedure sfto_lmultf(fl);
   % Ordered field standard form list multf. [fl] is a list of SF. Returns the
   % product of the SFs in [fl].
   if null fl then 1 else multf(car fl,sfto_lmultf cdr fl);


declare degree: (AmPoly) -> Integer;

procedure degree(u);
   sfto_tdegf numr simp u;


declare sfto_tdegf: (SF) -> Integer;

procedure sfto_tdegf(f);
   % Ordered field standard form total degree standard form. Returns the total
   % degree of [f].
   begin scalar w,x; integer td;
      if domainp f then
      	 return 0;
      x := mvar f;
      while not domainp f and mvar f eq x do <<
	 w := sfto_tdegf lc f + ldeg f;
	 if w > td then
	    td := w;
	 f := red f
      >>;
      w := sfto_tdegf f;
      if  w > td then
	 return w;
      return td
   end;


declare coefficients: (AmPoly,AmList) -> AmList;

procedure coefficients(f,vl);
   'list . for each c in sfto_allcoeffs(numr simp f,cdr vl) collect
      prepf c;


declare sfto_allcoeffs: (SF,List) -> List;

procedure sfto_allcoeffs(f,vl);
   sfto_allcoeffs1({f},vl);


declare sfto_allcoeffs1: (List,List) -> List;

procedure sfto_allcoeffs1(l,vl);
   if null vl then
      l
   else
      sfto_allcoeffs1(for each f in l join
	 sfto_coefs(sfto_reorder(f,car vl),car vl),cdr vl);


declare sfto_coefs: (SF,Kernel) -> List;

procedure sfto_coefs(f,v);
   if not domainp f and mvar f eq v then coeffs f else {f};


declare sfto_kernelp: (Any) -> Boolean;

procedure sfto_kernelp(u);
   begin scalar w;
      if idp u then
 	 return t;
      if not pairp u then
 	 return nil;
      if get(car u,'fkernfn) then
	 return t;
      w := if atom car u then get(car u,'klist) else exlist!*;
      return atsoc(u,w)
   end;

procedure sfto_varp(f);
   % [f] is an SF. Returns extended Boolean. Returns [k] if $[f] = 1*[k]^1+nil$,
   % return [nil] else.
   if not domainp f and null red f and eqn(lc f,1) and eqn(ldeg f,1) then
      mvar f;

procedure sfto_varIsNumP(f);
   % [f] is an SF. Returns extended Boolean. Returns [k] if $[f] = d1*[k]^1+d2$
   % for domain elements [d1], [d2], return [nil] else.
   if not domainp f and domainp lc f and domainp red f and eqn(ldeg f,1) then
      mvar f;

endmodule;  % [sfto]

end;  % of file
