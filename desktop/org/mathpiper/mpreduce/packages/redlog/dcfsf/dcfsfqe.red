% ----------------------------------------------------------------------
% $Id: dcfsfqe.red 1608 2012-04-26 12:01:48Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2004-2009 A. Dolzmann, 2004-2010 T. Sturm
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
   fluid '(dcfsf_qe_rcsid!* dcfsf_qe_copyright!*);
   dcfsf_qe_rcsid!* :=
      "$Id: dcfsfqe.red 1608 2012-04-26 12:01:48Z thomas-sturm $";
   dcfsf_qe_copyright!* := "(c) 2004-2009 A. Dolzmann, 2004-2010 T. Sturm"
>>;

module dcfsfqe;
% Diferentially closed field standard form quantifier elimination.

procedure dcfsf_orddegf(f,v);
   % Diferentially closed field standard form order and degree. [f] is
   % a standard form; [v] is a variable. Returns a pair of numbers.
   % The [car] is the order and the [cdr] is the degree wrt. [v].
   dcfsf_orddegf1(f,v,(-1) . (-1));

procedure dcfsf_orddegf1(f,v,od);
   % Diferentially closed field standard form order and degree
   % subroutine. [f] is a standard form; [v] is a variable; [od] is a
   % pair of numbers. Returns a pair of numbers. The [car] is the
   % order and the [cdr] is the degree wrt. [v].
   begin scalar mv,r; integer lord;
      if domainp f then
 	 return od;
      mv := mvar f;
      lord := if mv eq v then
	 0
      else if pairp mv and cadr mv eq v then
	 caddr mv
      else
	 -2;
      if lord > car od then
	 od := lord . ldeg f
      else if lord = car od then
	 od := lord . max(cdr od,ldeg f);
      r := f;
      while not domainp r and mvar r eq mv do
	 r := red r;
      return dcfsf_orddegf1(lc f,v,dcfsf_orddegf1(r,v,od))
   end;

procedure dcfsf_ordf(f,v);
   % Diferentially closed field standard form order. [f] is a standard
   % form with kernel order [{...,(d v 2),(d v 1),v}]; [v] is a
   % variable. Returns a number, the order of [f] wrt. [v].
   if domainp f then
      -1
   else if mvar f eq v then
      0
   else if pairp mvar f and cadr mvar f eq v then
      caddr mvar f
   else
      -1;

procedure dcfsf_degf(f,v);
   % Diferentially closed field standard form order. [f] is a standard
   % form with kernel order [{...,(d v 2),(d v 1),v}]; [v] is a
   % variable. Returns a number, the degree of [f] wrt. [v].
   if domainp f then
      0
   else if mvar f eq v or pairp mvar f and cadr mvar f eq v then
      ldeg f
   else
      0;

procedure dcfsf_df(f,x);
   % Diferentially closed field standard form derivative. [f] is a
   % standard form; [x] is a possibly composite kernel. Returns a
   % standard form. Computes the formal partial derivative of [f] wrt.
   % [x].
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := dcfsf_df1(reorder f,x);
      setkorder oldorder;
      return reorder w
   end;

procedure dcfsf_df1(f,x);
   % Diferentially closed field standard form derivative subroutine. [f]
   % is a standard form; [x] is a possibly composite kernel that is
   % largest wrt. the current kernel order. Returns a standard form.
   % Computes the formal partial derivative of [f] wrt. [x].
   if domainp f or mvar f neq x then
      nil
   else if eqn(ldeg f,1) then
      lc f
   else
      x .** (ldeg f - 1) .* multf(ldeg f,lc f) .+ dcfsf_df1(red f,x);

procedure dcfsf_derivationf(f,n,theo);
   % Diferentially closed field standard form n-th derivation. [f] is a
   % standard form; [theo] is a theory. Returns a standard form.
   % Computes the n-th derivative of [f].
   begin scalar r;
      r := f;
      for i := 1 : n do
	 r := dcfsf_derivation1f(r,theo);
      return r
   end;

procedure dcfsf_derivation1f(f,theo);
   % Diferentially closed field standard form derivation. [f] is a
   % standard form; [theo] is a theory. Returns a standard form.
   % Computes the derivative of [f].
   begin scalar res;
      for each v in kernels f do
	 res := addf(res,multf(dcfsf_df(f,v),dcfsf_derivationk(v,theo)));
      return res
   end;

procedure dcfsf_derivationk(k,theo);
   % Diferentially closed field kernel derivation. [k] is a kernel;
   % [theo] is a theory. Returns a standard form. Computes the
   % derivative of [k], which is possibly specified in [theo].
   begin scalar oldorder,kpf,kp,a,cnt;
      kp := dcfsf_derivationk1 k;
      kpf := kp .** 1 .* 1 .+ nil;
      oldorder := setkorder {kp};
      cnt := t;
      while cnt and theo do <<
	 a := pop theo;
	 if dcfsf_op a eq 'equal then <<
	    a := reorder dcfsf_arg2l a;
	    if mvar a eq kp and lc a = 1 then <<
	       cnt := nil;
	       kpf := negf red a
	    >>
	 >>
      >>;
      setkorder oldorder;
      return reorder kpf
   end;

procedure dcfsf_derivationk1(k);
   % Diferentially closed field kernel derivation subroutine. [k] is a
   % kernel. Returns a kernel. Computes the derivative of [k].
   if atom k then
      !*a2k {'d,k,1}
   else
      !*a2k {'d,cadr k,caddr k + 1};


switch kacem;    % DO NOT SWITCH ON
switch dcfsfold; % DO NOT SWITCH ON

procedure dcfsf_qe(f,theo);
   % Quantifier elimination. [f] is a formula, [theo] is a theory.
   % Returns a quantifier-free formula. The result is equivalent to [f]
   % wrt. [theo].
   if !*kacem then
      dcfsf_qe!-kacem(f,theo)
   else
      dcfsf_qe0(f,theo);

procedure dcfsf_qe!-kacem(f,theo);
   % DO NOT USE.
   begin scalar w;
      f := rl_prepfof f;
      f := cl_pnf f;
      w := dqe_start1 f;
      if w eq t then
	 w := 'true
      else if null w then
	 w := 'false;
      w := rl_simp w;
      return w
   end;

procedure dcfsf_qe0(f,theo);
   % Quantifier elimination entry point. [f] is a formula, [theo] is a
   % theory. Returns a quantifier-free formula. The result is equivalent
   % to [f] wrt. [theo].
   begin scalar w,bl;
      f := cl_simpl(cl_pnf cl_nnf f,theo,-1);
      w := cl_splt f;
      bl := car w;
      f := cadr w;
      for each blk in bl do
	 f := cl_simpl(dcfsf_qeblk(f,blk,theo),theo,-1);
      return f
   end;

procedure dcfsf_qeblk(f,blk,theo);
   % Quantifier elimination for one block. [f] is a quantifier-free
   % formula; [blk] is a QBLK (see clmisc); [theo] is a theory. Returns
   % a quantifier-free formula equivalent to $[blk] [f]$ wrt. [theo].
   if car blk eq 'all then
      rl_nnfnot dcfsf_qeblk1(rl_nnfnot f,blk,theo)
   else
      dcfsf_qeblk1(f,blk,theo);

procedure dcfsf_qeblk1(f,blk,theo);
   % Quantifier elimination for one block subroutine. [f] is a
   % quantifier-free formula; [blk] is a QBLK (see clmisc); [theo] is a
   % theory. Returns a quantifier-free formula equivalent to $[blk] [f]$
   % wrt. [theo].
   <<
      if !*rlverbose then
	 ioto_tprin2t {"Eliminating ",blk};
      for each v in cdr blk do
	 f := if !*dcfsfold then
      	    dcfsf_qevarold(f,v,theo)
	 else
	    dcfsf_qevar(f,v,theo);
      f
   >>;

procedure dcfsf_qevar(phi,v,theo);
   % Quantifier elimination for one variable. [f] is a quantifier-free
   % formula; [v] is a variable considered existentially quantified;
   % [theo] is a theory. Returns a quantifier-free formula. The result
   % is equivalent to $\exists [v] [f]$ wrt. [theo].
   begin scalar kl,oo,pll,!*rlsifacne;
      if !*rlverbose then
	 ioto_tprin2t {"Eliminating ",v};
      phi := cl_dnf cl_simpl(phi,theo,-1);
      if rl_tvalp phi then
	 return phi;
      kl := dcfsf_mkkl(v,dcfsf_maxorder(cl_terml phi,v));
      oo := setkorder kl;
      phi := cl_apply2ats(phi,function(dcfsf_reorderat));
      pll := dcfsf_dnf2pll(phi,v,theo);
      if !*rlverbose then
	 ioto_tprin2t "Computing ENF";
      pll := dcfsf_enf1(pll,v,theo);
      if !*rlverbose then
	 ioto_tprin2t "Eliminating base cases";
      phi := dcfsf_elim(pll,v,theo);
      if !*rlverbose then
	 ioto_tprin2t "Final simplification";
      setkorder oo;
      phi := cl_apply2ats(phi,function(dcfsf_reorderat));
      phi := rl_simpl(phi,theo,-1);
      return phi
   end;

procedure dcfsf_elim(pll,v,theo);
   begin integer enf1len;
      if !*rlverbose then
	 enf1len := pll_length pll + 1;
      return rl_smkn('or,for each pl in pll_2l pll collect <<
	 enf1len := enf1len - 1;
   	 dcfsf_elim1(pl,v,theo,enf1len)>>)
   end;

procedure dcfsf_elim1(pl,v,theo,left);
   begin scalar veql,vnel,oeql,onel,argl,f,i,g,rmd,of,og,df,dg,r;
      {veql,vnel,oeql,onel} := pl_2l pl;
      % The following three conditions cannot occur provided that the
      % code is correct:
      if veql and cdr veql then
	 rederr {"dcfsf_elim1: more than one equation in ",v};
      if vnel and cdr vnel then
	 rederr {"dcfsf_elim1: more than one inequality in ",v};
      if null vnel then
	 rederr {"dcfsf_elim1: no inequality in ",v};
      argl := dcfsf_oatl(oeql,onel);
      if not veql then <<
	 % no equation, only (one) inequality
	 if !*rlverbose then ioto_prin2 {"[",left,":C1] "};
	 return rl_smkn('and,dcfsf_bc1(car vnel,v,theo) . argl)
      >>;
      if domainp car vnel and not null car vnel then <<
	 % no inequality, only (one) equation
	 if !*rlverbose then ioto_prin2 {"[",left,":C2] "};
	 %%	 return rl_smkn('and,dcfsf_bc2(car veql,v,theo) . argl)
      	 % The following condition cannot occur provided that the code is
      	 % correct:
	 i := lc car veql;
	 if not (domainp i and not null i or member(sfto_sqfpartf i,onel)) then
	    rederr {"dcfsf_elim1: no initial inequality for equation"};
	 return rl_smkn('and,argl)
      >>;
      % We now know that there is one equation and one inequality.
      f := car veql;
      i := lc f;
      g . rmd := qremf(car vnel,i);
      % The following condition cannot occur provided that the code is
      % correct:
      if rmd then
	 rederr {"dcfsf_elim1: lhs of inequality is not divisible by I_f"};
      of . df := dcfsf_orddegf(f,v);
      og . dg := dcfsf_orddegf(g,v);
      if og < of then <<
	 if !*rlverbose then ioto_prin2 {"[",left,":C3] "};
	 return rl_smkn('and,dcfsf_bc3(i,g,v,theo) . argl)
      >>;
      if og = of and dg < df then <<
	 if !*rlverbose then ioto_prin2 {"[",left,":C4] "};
	 r := car dcfsf_reduce({exptf(g,df)},f,v);
	 return rl_smkn('and,dcfsf_bc3(i,r,v,theo) . argl)
      >>;
      rederr {"dcfsf_elim1: ord(g) = ord(f) and deg(g) >= deg(f)"}
   end;

procedure dcfsf_oatl(oeql,onel);
   begin scalar atl;
      for each one in onel do
 	 atl := dcfsf_0mk2('neq,one) . atl;
      for each oeq in oeql do
 	 atl := dcfsf_0mk2('equal,oeq) . atl;
      return atl
   end;

procedure dcfsf_bc1(g,v,theo);
   % Base Case 1
   rl_smkn('or,for each gt in dcfsf_cl(g,v) collect dcfsf_0mk2('neq,gt));

procedure dcfsf_bc2(f,v,theo);
   % Base Case 2
   begin scalar ftl,f1;
      ftl . f1 := dcfsf_cl1(f,v);
      return rl_smkn('or,dcfsf_0mk2('equal,f1) .
	 for each gt in ftl collect dcfsf_0mk2('neq,gt))
   end;

procedure dcfsf_bc3(i,g,v,theo);
   % Base Case 3
   begin scalar iff,w1,w2;
      w1 := for each gt in dcfsf_cl(g,v) collect
	 dcfsf_0mk2('neq,gt);
      w2 := for each ct in dcfsf_cl(i,v) collect
	 dcfsf_0mk2('neq,ct);
      return rl_mkn('and,{rl_smkn('or,w1),rl_smkn('or,w2)})
   end;

procedure dcfsf_qevarold(f,v,theo);
   % Quantifier elimination for one variable. [f] is a quantifier-free
   % formula; [v] is a variable considered existentially quantified;
   % [theo] is a theory. Returns a quantifier-free formula. The result
   % is equivalent to $\exists [v] [f]$ wrt. [theo].
   begin scalar rl;
      if !*rlverbose then
	 ioto_tprin2t {"Eliminating ",v};
      f := cl_dnf f;
      rl := if rl_op f eq 'or then
      	 for each ff in rl_argn f collect
	    dcfsf_qevar1(ff,v,theo)
      else
	 {dcfsf_qevar1(f,v,theo)};
      return rl_smkn('or,rl)
   end;

procedure dcfsf_qevar1(f,v,theo);
   % Quantifier elimination for one variable subroutine. [f] is a
   % conjunction of atomic formulas or an atomic formula or a truth
   % value; [v] is a variable considered existentially quantified;
   % [theo] is a theory. Returns a quantifier-free formula. The result
   % is equivalent to $\exists [v] [f]$ wrt. [theo].
   begin scalar r,w;
      if rl_tvalp f then
	 return f;
      w := dcfsf_nf(f,v);
      r := dcfsf_qevar2(car w,cadr w,v,theo);
      r := rl_mkn('and,{rl_smkn('and,caddr w),r});
      return r
   end;

procedure dcfsf_nf(f,v);
   % Normal form. [f] is a conjunction of atomic formulas or an atomic
   % formula; [v] is a variable. Returns a triplet $([e],[n],[s|)$,
   % where [e] is a list of standard forms, [n] is a standard form, and
   % [s] is a list of atomic formulas. [e] is the list of all left hand
   % sides of equations containing [v] in [f], [n] is the product of all
   % left hand side of inequalities containing [v] in [f], and [s] is
   % the list of all atomic formulas not containing [v] in [f].
      if rl_op f eq 'and then
	 dcfsf_nf1(rl_argn f,v)
      else
	 dcfsf_nf1({f},v);

procedure dcfsf_nf1(f,v);
   % Normal form subroutine. [f] is a list of atomic formulas; [v] is a
   % variable. Returns a triplet $([e],[n],[s|)$, where [e] is a list of
   % standard forms, [n] is a standard form, and [s] is a list of atomic
   % formulas. [e] is the list of all left hand sides of equations
   % containing [v] in [f], [n] is the product of all left hand side of
   % inequalities containing [v] in [f], and [s] is the list of all
   % atomic formulas not containing [v] in [f].
   begin scalar e,n,s;
      n := numr simp 1;
      for each at in f do
	 if not(v memq dcfsf_varlat at) then
	    s := at . s
	 else if dcfsf_op at eq 'equal then
	    e := dcfsf_arg2l(at) . e
	 else
	    n := multf(dcfsf_arg2l at,n);
      return {e,n,s}
   end;


procedure dcfsf_qevar2(fl,g,v,theo);
   % Quantifier elimination for one variable subroutine. [f] is a list
   % of standard forms, [g] is a standard form, [v] is a variable,
   % [theo] is a theory. Returns a quantifier-free formula. The result
   % is equivalent to $\exists [v] (\bigwedge_{f \in [fl]} f=0 \land g
   % \neq 0)$ wrt. [v]. Old comment: "Special case on page 5."
   begin scalar oo,kl,r;
      kl := dcfsf_mkkl(v,dcfsf_maxorder(g . fl,v));
      oo := setkorder kl;
      fl := for each f in fl collect reorder f;
      g := reorder g;
      r := dcfsf_qesc5(fl,g,v,theo,t);
      setkorder oo;
      return cl_apply2ats(r,'dcfsf_reorderat)
   end;

procedure dcfsf_reorderat(a);
   % Reorder atomic formula. [a] is an atomic formula. Returns an atomic
   % formula reorders the left hand side of a wrt. the current kernel
   % order.
   if rl_tvalp a then
      a
   else
      dcfsf_0mk2(dcfsf_op a,reorder dcfsf_arg2l a);

procedure dcfsf_maxorder(fl,v);
   % Maximal order. [fl] is a list of standard forms; [v] is variable.
   % Returns a number. The result is the maximum of the orders wrt. [v]
   % of the differential polynomials in [fl].
   begin scalar w; integer m;
      for each f in fl do <<
	 w := dcfsf_orddegf(f,v);
	 if car w > m then
	    m := car w
      >>;
      return m
   end;

procedure dcfsf_mkkl(v,m);
   % Make kernel list. [v] is a variable; [m] is a non-negative integer.
   % Returns a list of (composite) kernels. The result is
   % $([v],[v]',[v]'',...,[v]^{(m)})$.
   reversip(v . for i := 1 : m collect !*a2k {'d,v,i});

procedure dcfsf_qesc5(fl,g,v,theo,elim);
   if elim then
      dcfsf_qesc5!-elim(fl,g,v,theo)
   else
      dcfsf_qesc5!-noelim(fl,g,v,theo);

procedure dcfsf_qesc5!-elim(fl,g,v,theo);
   % Special case on page 5.
   <<
      fl := sort(fl,'dcfsf_qeordp!-desc);
      if !*rlverbose then
	 ioto_prin2 {"[",length fl,dcfsf_orddegf(lastcar fl,v),"] "};
      if null fl then
	 % CASC Base Case 2
	 dcfsf_qesc1(g,v,theo)
      else if null cdr fl then
	 % m=1: CASC Recursion Subcases 2.1 and 2.2
	 dcfsf_qebasis(car fl,g,v,theo)
      else
	 % m>1: CASC Recursion Subcase 2.3
      	 dcfsf_qesc5r(fl,g,v,theo,t)
   >>;

procedure dcfsf_qesc5!-noelim(fl,g,v,theo);
   % Special case on page 5.
   <<
      fl := sort(fl,'dcfsf_qeordp!-desc);
      if !*rlverbose then
	 ioto_prin2 {"[",length fl,dcfsf_orddegf(lastcar fl,v),"] "};
      if null fl then
	 dcfsf_0mk2('neq,g)
      else if null cdr fl then
	 rl_mkn('and,{dcfsf_0mk2('equal,car fl),dcfsf_0mk2('neq,g)})
      else
      	 dcfsf_qesc5r(fl,g,v,theo,nil)
   >>;

procedure dcfsf_qesc50(fl,g,v,theo,elim);
   % Essentially CASC Recursion Case 1
   begin scalar nfl,r,f,pl;
      if null g then
	 return 'false;
      if domainp g then
	 g := 1;
      while fl do <<
	 f := pop fl;
	 if domainp f then <<
	    if f then <<
	       r := 'false;
	       fl := nil
	    >>
	 >> else if not(v memq dcfsf_varlat1 kernels f) then
	    pl := dcfsf_0mk2('equal,f) . pl
	 else
	    nfl := f . nfl;
      >>;
      if r eq 'false then
	 return 'false;
      r := dcfsf_qesc5(nfl,g,v,theo,elim);
      r := rl_mkn('and,{rl_smkn('and,pl),r});
      return r
   end;

procedure dcfsf_qeordp!-desc(f1,f2);
   % Order predicate. [f1] and [f2] are SFs. Returns Bool. The result is
   % non-[nil] iff [f1] > [f2] wrt. to lexicographically considering
   % (order, degree). That is, the order is the principle criterion.
   begin scalar p1,p2,v;
      v := dcfsf_mvar f1;
      p1 := dcfsf_orddegf(f1,v);
      p2 := dcfsf_orddegf(f2,v);
      return car p1 > car p2 or car p1 = car p2 and cdr p1 > cdr p2
   end;

procedure dcfsf_qebasis(f1,g,v,theo);
   if null g then
      % CASC Base Case 1
      'false
   else if domainp g then
      % CASC Base Case 3
      dcfsf_qesc2(f1,v,theo)
   else if dcfsf_ordf(g,v) leq dcfsf_ordf(f1,v) then
      % CASC Recursion Subcase 2.1
      dcfsf_qebasis1(f1,g,v,theo)
   else
      % CASC Recursion Subcase 2.2
      dcfsf_qebasis2(f1,g,v,theo);

switch dzopt;

procedure dcfsf_qebasis1(f1,g,v,theo);
   % CASC Recursion Subcase 2.1
   begin scalar phi1p,phi2p;
      if !*dzopt and null cdr qremf(g,lc f1) then <<
	 phi1p := 'false;
	 phi2p := dcfsf_qesc(f1,lc f1,g,v,theo);
      >> else <<
      	 phi1p := dcfsf_qesc50({red f1,lc f1},g,v,theo,t);
	 phi1p := cl_simpl(phi1p,theo,-1);
      	 if phi1p eq 'true then
	    return 'true;
      	 phi2p := dcfsf_qesc(f1,lc f1,g,v,theo);
      >>;
      return cl_simpl(rl_mkn('or,{phi1p,phi2p}),theo,-1);
   end;

procedure dcfsf_qebasis2(f1,g,v,theo);
   % CASC Recursion Subcase 2.2
   begin scalar psi,sp,s1,sf1,if1,qr,r,dp,phi1p,phi3p,r;
      if1 := lc f1;
      sp := dcfsf_ordf(g,v);
      s1 := dcfsf_ordf(f1,v);
      sf1 := dcfsf_separant f1;
      dp := dcfsf_degf(g,v);
      qr := qremf(multf(exptf(sf1,dp),g),dcfsf_dn(f1,sp-s1,v,theo));
      r := cdr qr;
      if !*dzopt and null cdr qremf(g,lc f1) then <<
	 if1 := 1;
	 phi1p := 'false;
      >> else
      	 phi1p := dcfsf_qesc50({red f1,lc f1},g,v,theo,t);
      phi1p := cl_simpl(phi1p,theo,-1);
      if phi1p eq 'true then
	 return 'true;
      if dcfsf_degf(f1,v) > 1 then <<
      	 psi := dcfsf_qebasis(f1,multf(multf(sf1,if1),r),v,theo);
      	 phi3p := dcfsf_qesc50({f1,sf1},g,v,theo,t);
	 r := rl_mkn('or,{phi1p,psi,phi3p});
      >> else <<
      	 psi := dcfsf_qebasis(f1,multf(if1,r),v,theo);
	 r := rl_mkn('or,{phi1p,psi})
      >>;
      return r
   end;

procedure dcfsf_mvar(f);
   % Main variable. [f] is an SF. Returns an identifier. The result is
   % the leading variable, in contrast to leading kernel, of [f] or
   % [nil] if [f] is a domain element.
   if domainp f then
      nil
   else if eqcar(mvar f,'d) then
      cadr mvar f
   else
      mvar f;

procedure dcfsf_separant(f);
   % Separant. [f] is an SF. Returns an SF. The result is the separant
   % of [f].
   dcfsf_df(f,mvar f);

procedure dcfsf_qesc5r(fl,g,v,theo,elim);
   % CASC Recursion Case 2, phi1
   begin scalar phi1p,phi2p,fm,ffl;
      ffl := reverse fl;
      fm := car ffl;
      if !*dzopt and null cdr qremf(g,lc fm) then
	 phi1p := 'false
      else
      	 phi1p := dcfsf_qesc50(red fm . lc fm . cdr ffl,g,v,theo,elim);
      phi2p := dcfsf_qesc5r2(fl,g,v,theo,elim);
      ioto_tprin2t "fl:";
      mathprint('list . for each f in fl collect prepf f);
      ioto_tprin2t "phi1':";
      mathprint rl_prepfof cl_simpl(phi1p,nil,-1);
      ioto_tprin2t "phi2':";
      mathprint rl_prepfof cl_simpl(phi2p,nil,-1);
      ioto_tprin2t "----------------------------------------";
      return rl_mkn('or,{phi1p,phi2p})
   end;

procedure dcfsf_qesc5r2(fl,g,v,theo,elim);
   % CASC Recursion Case 2, phi2
   begin scalar ffl,fm,fm1;
      ffl := reverse fl;
      fm := pop ffl;
      fm1 := pop ffl;
      if dcfsf_ordf(fm,v) = dcfsf_ordf(fm1,v) then
	 return dcfsf_qesc5r2u1(fm,fm1,ffl,g,v,theo,elim);
      return dcfsf_qesc5r2u2(fm,fm1,ffl,g,v,theo,elim)
   end;

procedure dcfsf_qesc5r2u1(fm,fm1,ffl,g,v,theo,elim);
   begin scalar dm1,ifm,qr,r,psip;
      dm1 := dcfsf_degf(fm1,v);
      ifm := lc fm;
      qr := qremf(multf(exptf(ifm,dm1),fm1),fm);
      r := cdr qr;
      if !*dzopt and null cdr qremf(g,lc fm) then
	 psip := dcfsf_qesc50(fm . r . ffl,g,v,theo,elim)
      else
      	 psip := dcfsf_qesc50(fm . r . ffl,multf(ifm,g),v,theo,elim);
      return psip
   end;

procedure dcfsf_qesc5r2u2(fm,fm1,ffl,g,v,theo,elim);
   begin scalar sfm,dm1,qr,r,sm,sm1,psip,ifm;
      sfm := dcfsf_separant fm;
      dm1 := dcfsf_degf(fm1,v);
      sm := dcfsf_ordf(fm,v);
      sm1 := dcfsf_ordf(fm1,v);
      ifm := lc fm;
      qr := qremf(multf(exptf(sfm,dm1),fm1),
	 dcfsf_dn(fm,sm1-sm,v,theo));
      r := cdr qr;
      if !*dzopt and null cdr qremf(g,lc fm) then
      	 psip := dcfsf_qesc50(fm . r . ffl,g,v,theo,elim)
      else
%      	 psip := dcfsf_qesc50(fm . r . ffl,multf(ifm,g),v,theo,elim);
      	 psip := dcfsf_qesc50(fm . r . ffl,multf(sfm,g),v,theo,elim);
      return psip
   end;

procedure dcfsf_dn(f,n,v,theo);
   % Derivate n times. [f] is an SF, [n] is a number, [v] is an
   % identifier, [theo] is a theory. Returns an SF. Dynamically extends
   % the current kernel order in such a way that all existing SFs remain
   % valid.
   begin scalar r,s,m;
      m := if kord!* and pairp car kord!* and car car kord!* eq 'd then
	 caddr car kord!* else 0;
      s := car dcfsf_orddegf(f,v);
      m := max(m,s+n);
      setkorder dcfsf_mkkl(v,m);
      r := reorder f;
      r := dcfsf_derivationf(r,n,theo);
      % I think the following reorder is not really necessary:
      return reorder r
   end;

procedure dcfsf_qesc1(g,v,theo);
   % CASC Base Case 2
   rl_smkn('or,for each gt in dcfsf_cl(g,v) collect dcfsf_0mk2('neq,gt));

procedure dcfsf_cl(f,v);
   % Coefficient list. [f] is an SF; [v] is an identifier. Returns a
   % list of SFs. The result is the list of coefficients of [f] as a
   % differential polynomial in [v].
   if domainp f or not(v memq dcfsf_varlat1 kernels f) then
      {f}
   else
      nconc(dcfsf_cl(lc f,v),dcfsf_cl(red f,v));

procedure dcfsf_cl1(f,v);
   % Coefficient list variant. [f] is an SF; [v] is an identifier.
   % Returns a pair $a . d$, where $a$ is a list of SFs and $d$ is an
   % SF. In the result $d$ is absolute summand of [f] as a differential
   % polynomial in [v], and $a$ is the list of all other coefficients.
   dcfsf_cl2(f,v,T);

procedure dcfsf_cl2(f,v,flg);
   begin scalar w;
      if domainp f or not(v memq dcfsf_varlat1 kernels f) then
      	 return if flg then
	    nil . f
      	 else
      	    {f} . nil
      else <<
	 w := dcfsf_cl2(red f,v,T);
      	 return nconc(car dcfsf_cl2(lc f,v,nil),car w) . cdr w
      >>
   end;

procedure dcfsf_qesc(f1,if1,g,v,theo);
   begin
      if null g or null if1 then
      	 return 'false;
      if domainp if1 then
      	 if1 := 1;
      if g = 1 and not(v memq dcfsf_varlat1 kernels if1) then
      	 return rl_mkn('and,{dcfsf_0mk2('neq,if1),dcfsf_qesc2(f1,v,theo)});
      if dcfsf_ordf(g,v) < dcfsf_ordf(f1,v) then
      	 return dcfsf_qesc3(f1,g,if1,v,theo);
      return dcfsf_qesc4(f1,g,if1,v,theo)
   end;

procedure dcfsf_qesc2(f,v,theo);
   % CASC Base Case 3
   begin scalar ftl,f1;
      ftl . f1 := dcfsf_cl1(f,v);
      return rl_smkn('or,dcfsf_0mk2('equal,f1) .
	 for each gt in ftl collect dcfsf_0mk2('neq,gt))
   end;

procedure dcfsf_qesc3(f,g,iff,v,theo);
   begin scalar iff,w1,w2;
      w1 := for each gt in dcfsf_cl(g,v) collect
	 dcfsf_0mk2('neq,gt);
      w2 := for each ct in dcfsf_cl(lc f,v) collect
	 dcfsf_0mk2('neq,ct);
      return rl_mkn('and,{rl_smkn('or,w1),rl_smkn('or,w2)})
   end;

procedure dcfsf_qesc4(f,g,iff,v,theo);
   begin scalar qr,dd,dp,w1,w2,r,s;
      dd := dcfsf_degf(f,v);
      dp := dcfsf_degf(g,v);
      s := dcfsf_ordf(f,v);
      qr := qremf(multf(exptf(lc f,dd*dp),exptf(g,dd)),f);
      r := cdr qr;
      w1 := for each ct in dcfsf_cl(lc f,v) collect
	 dcfsf_0mk2('neq,ct);
      w2 := for each rt in dcfsf_cl(r,v) collect
	 dcfsf_0mk2('neq,rt);
      return rl_mkn('and,{rl_smkn('or,w1),rl_smkn('or,w2)})
   end;

procedure dcfsf_1equation(f,v,theo);
   begin scalar fl,gl,g,oo,kl,r,!*rlsiexpla;
      f := cl_simpl(f,theo,-1);
      if rl_op f neq 'and then
	 rederr {"dcfsf_1equation:",f,"is not a conjunction"};
      g := 1;
      for each at in rl_argn f do
	 if rl_op at eq 'equal then
	    fl := dcfsf_arg2l at . fl
	 else if rl_op at eq 'neq then
	    g := multf(g,dcfsf_arg2l at)
	 else
	    rederr {"dcfsf_1equation:",at,"is not an atomic formula"};
      kl := dcfsf_mkkl(v,dcfsf_maxorder(g . fl,v));
      oo := setkorder kl;
      fl := for each f in fl collect reorder f;
      g := reorder g;
      r := dcfsf_qesc5(fl,g,v,nil,nil);
      setkorder oo;
      r := cl_apply2ats(r,'dcfsf_reorderat);
      r := cl_simpl(cl_dnf r,theo,-1);
      return r
   end;

%DS
% <pll> ::= {<pl>, ...};  considered as a disjunction
% <pl> ::= {<veql>, <vnel>, <oeql>, <onel>};  considered as a conjunction
% <veql> ::= {<sf>, ...};  left hand sides of equations containing v
% <vnel> ::= {<sf>};  product of left hand sides of inequalities containing v
% <oeql> ::= {<sf>, ...};  left hand sides of equations not containing v
% <onel> ::= {<sf>, ...};  left hand sides of inequalities not containing v

put('!*rl_pl,'prifn,'pl_prifn);

put('!*rl_pll,'prifn,'pll_prifn);

procedure pll_new();
   {'!*rl_pll};

procedure pll_emptyp(pll);
   not cdr pll;

procedure pll_length(pll);
   length pll_2l pll;

macro procedure pll_pop(l);
   % A limited pop in the sense of ANSI Common Lisp. Admits only a
   % single identifier as its argument. A more sophisticated version
   % would evaluate the properties setqfn or assignop.
   begin scalar ll;
      if null cdr l or cddr l then
         rederr {"pop called with",length cdr l, "arguments instead of 1"};
      ll := cadr l;
      if not idp ll then
         typerr(ll,"identifier");
      return {'prog,{'a},
         {'setq,'a,{'cadr,ll}},
         {'rplacd,ll,{'cddr,ll}},
         {'return,'a}}
   end;

procedure pll_cons(pl,pll);
   rplacd(pll,lto_insert(pl,cdr pll));

procedure pll_fl(l);
   begin scalar pll;
      pll := pll_new();
      for each pl in l do
	 pll := pll_cons(pl,pll);
      return pll
   end;

procedure pll_prifn(pll);
   <<
      ioto_prin2 "<pll>";
      for each pl in pl_2l pll do
	 pl_prifn pl;
      ioto_prin2 "<pll>";
   >>;

procedure pll_2l(pll);
   cdr pll;

procedure pl_factorize(pl);
   % [pl] is a PL. Returns a PLL containing PLs to be inserted.
   begin scalar veql,vnel,oeql,onel,fveql,apll;
      pl := pl_sqfpart pl;
      pl := pl_simpl pl;
      if pl_falsep pl then
	 return pll_new();
      if not !*rlenffac then
      	 return pll_fl {pl};
      {veql,vnel,oeql,onel} := pl_2l pl;
      fveql := for each veq in veql collect
	 for each pr in cdr fctrf veq collect
	    car pr;
      apll := pll_new();
      for each pveql in lto_cartprod fveql do
	 apll := pll_cons(pl_new(list2set pveql,vnel,oeql,onel),apll);
      return apll
   end;

procedure pll_ins(apll,pll);
   <<
      for each pl in pll_2l apll do
	 pll := pll_cons(pl,pll);
      pll
   >>;

procedure pl_sqfpart(pl);
   begin scalar l,w;
      l := for each ll in pl_2l pl collect
	 for each f in ll collect
	    if domainp f then
	       f
	    else <<
	       w := sfto_sqfpartf f;
	       if minusf w then w := negf w;
	       w
	    >>;
      return pl_fl l
   end;

procedure pl_new(veql,vnel,oeql,onel);
   pl_fl {veql,vnel,oeql,onel};

procedure pl_2l(pl);
   cdr pl;

procedure pl_fl(l);
   '!*rl_pl . l;

procedure pl_prifn(pl);
   <<
      pop pl;
      ioto_prin2 "<pl>";
      pl_mapril pop pl;
      for i := 1:3 do <<
      	 ioto_prin2 ",";
      	 pl_mapril pop pl
      >>;
      ioto_prin2 "</pl>"
   >>;

procedure pl_mapril(l);
   <<
      ioto_prin2 "{";
      if l then <<
	 maprin prepf pop l;
      	 for each f in l do <<
	    ioto_prin2 ",";
	    maprin prepf f
      	 >>
      >>;
      ioto_prin2 "}"
   >>;

switch rlenfsimpl;
switch rlenf1twice;

procedure dcfsf_enf(phi,v,theo);
   % Elimination normal form. [phi] is a formula; [v] is an identifier;
   % [theo] is a theory. Returns a formula in DNF. The result is
   % equivalent to [phi].
   begin scalar kl,oo,pll,!*rlsiexpla,!*rlsifac,pll2,!*nat;
      if !*rlverbose then
	 ioto_tprin2t "Computing ENF";
      phi := cl_dnf cl_simpl(phi,theo,-1);
      if rl_tvalp phi or cl_atfp phi then
	 return phi;
      kl := dcfsf_mkkl(v,dcfsf_maxorder(cl_terml phi,v));
      oo := setkorder kl;
      phi := cl_apply2ats(phi,function(dcfsf_reorderat));
      pll := dcfsf_dnf2pll(phi,v,theo);
      if !*rlenf1twice then <<
      	 pll := dcfsf_enf1(pll,v,theo);
      	 pll2 := 'rl_pll!* . reverse cdr pll;
      	 pll2 := dcfsf_enf1(pll2,v,theo);
	 if pll2 neq pll then <<
	    lprim {"enf1 not idempotent here"};
	    terpri!* t;
	    mathprint pll;
	    mathprint pll2;
	    pll := pll2
	 >>
      >> else
      	 pll := dcfsf_enf1(pll,v,theo);
      phi := pll_2dnf pll;
      setkorder oo;
      phi := cl_apply2ats(phi,function(dcfsf_reorderat));
      if !*rlenfsimpl then
      	 phi := rl_simpl(phi,theo,-1);
      if !*rlverbose then
	 ioto_tprin2t "";
      return phi
   end;

procedure dcfsf_dnf2pll(phi,v,theo);
   % Disjunctive normal form to <pll>. [phi] is a formula in DNF; [v] is
   % an identifier; [theo] is a theory. Returns a <pll>.
   begin scalar pll,apll;
      phi := cl_mkstrict(phi,'or);
      pll := pll_new();
      for each conj in rl_argn phi do <<
	 apll := pl_factorize dcfsf_atl2pl(rl_argn conj,v);
	    pll := pll_ins(apll,pll)
      >>;
      return pll
   end;

procedure dcfsf_atl2pl(atl,v);
   % Atomic formula list to <pll>. [atl] is a list of atomic formulas;
   % [v] is an identifier. Returns a <pll>.
   begin scalar op,lhs,veql,vne,oeql,onel;
      vne := 1;
      for each at in atl do <<
	 op := rl_op at;
	 lhs := rl_arg2l at;
	 if op eq 'equal then
	    if dcfsf_mvar lhs eq v then
	       veql := lhs . veql
	    else
	       oeql := lhs . oeql
	 else if rl_op at eq 'neq then
	    if dcfsf_mvar lhs eq v then
	       vne := sfto_sqfpartf multf(lhs,vne)
	    else
	       onel := lhs . onel
	 else
	    rederr {"dcfsf_atl2pll: unexpected operator ",op}
      >>;
      return pl_new(veql,{vne},oeql,onel)
   end;

procedure pll_2dnf(pll);
   % <pll> to DNF. [pll] is a <pll>. Returns a formula in DNF.
   rl_smkn('or,reversip for each pl in pll_2l pll collect
      rl_smkn('and,pl_2atl pl));

procedure pl_2atl(pl);
   % <pl> to list of atomic formulas. [pl] is a <pl>. Returns a possibly
   % degenerate conjunction of atomic formulas.
   begin scalar veql,vnel,oeql,onel;
      pop pl;
      veql := pop pl;
      veql := for each f in veql collect dcfsf_0mk2('equal,f);
      vnel := pop pl;
      vnel := if !*rlenffacne then
	 for each f in cdr fctrf car vnel collect dcfsf_0mk2('neq,car f)
      else
	 {dcfsf_0mk2('neq,car vnel)};
      oeql := pop pl;
      oeql := for each f in oeql collect dcfsf_0mk2('equal,f);
      onel := pop pl;
      onel := for each f in onel collect dcfsf_0mk2('neq,f);
      return lto_nconcn {veql,vnel,oeql,onel}
   end;

procedure pl_simpl(pl);
   begin scalar veql,vnel,oeql,onel,a,brk,nveql,nvnel,noeql,nonel,w;
      if not !*rlplsimpl then
	 return pl;
      {veql,vnel,oeql,onel} := pl_2l pl;
      nveql := veql;
      nvnel := vnel;
      brk := nil; while oeql and not brk do <<
	 a := pop oeql;
	 if domainp a then
	    (if not null a then brk := t)
	 else
	    noeql := lto_insert(a,noeql)
      >>;
      if brk then
 	 return pl_false();
      noeql := reversip noeql;
      brk := nil; while onel and not brk do <<
	 a := pop onel;
	 if domainp a then
	    (if null a then brk := t)
	 else
	    nonel := lto_insert(a,nonel)
      >>;
      if brk then
 	 return pl_false();
      nonel := reversip nonel;
      pl := pl_new(nveql,nvnel,noeql,nonel);
      if cl_simpl(rl_smkn('and,pl_2atl pl),nil,-1) eq 'false then
	 return pl_false();
      return pl
   end;

procedure pl_false();
   '(!*rl_pl nil (1) (1) nil);

procedure pl_falsep(pl);
   pl = pl_false();

procedure dcfsf_enf1(pll,v,theo);
   % Elimination normal form subroutine. [pll] is a <pll>; [v] is an
   % identifier; [theo] is a theory. Returns a <pll>. When interpreted
   % as a formula, the result is equivalent to [pll].
   begin scalar pl,veql,vnel,oeql,onel,npll,veq1,veql1_,i,r,s,o,d,apll,w;
      npll := pll_new();
      while not pll_emptyp pll do <<
	 if !*rlverbose then ioto_prin2 {"[",pll_length pll};
	 pl := pll_pop pll;
	 {veql,vnel,oeql,onel} := pl_2l pl;
	 veql := sort(veql,function dcfsf_qeordp);
	 if not veql then <<  % base case
	    if !*rlverbose then ioto_prin2 "] ";
	    npll := pll_ins({pl},npll)
	 >> else <<  % at least on equation in [v]
	    veq1 := car veql;
	    veql1_ := cdr veql;
	    if !*rlverbose then <<
	       o . d := dcfsf_orddegf(veq1,v);
	       ioto_prin2 {":(",o,",",d,")] "}
	    >>;
	    i := lc veq1;
	    r := red veq1;
	    s := dcfsf_separant veq1;
	    % Case 1: initial = 0
	    apll := pl_factorize dcfsf_enf1c1(i,r,veql1_,vnel,oeql,onel,v);
	    pll := pll_ins(apll,pll);
	    % Case 2: inital <> 0 and separant = 0
	    if ldeg veq1 neq 1 then <<
	       % otherwise there is nothing to do because initial = separant
	       apll := pl_factorize dcfsf_enf1c2(i,r,s,veql,vnel,oeql,onel,v);
	       pll := pll_ins(apll,pll)
	    >>;
    	    % Case 3: initial <> 0 and separant <> 0 and further equations
	    if veql1_ then <<
	       apll := pl_factorize
 		  dcfsf_enf1c3(i,s,veq1,veql1_,vnel,oeql,onel,v);
	       pll := pll_ins(apll,pll)
	    >> else << % "base case"
	       apll := pl_factorize
		     dcfsf_enf1c3b(i,s,veq1,veql,vnel,oeql,onel,v);
	       if (w := pll_2l apll) and cdr w then
		  pll := pll_ins(apll,pll)
	       else
	       	  npll := pll_ins(apll,npll)
	    >>
	 >>
      >>;
      return npll
   end;

procedure dcfsf_qeordp(f1,f2);
   % Order predicate. [f1] and [f2] are SFs. Returns Bool. The result is
   % non-[nil] iff [f1] < [f2] wrt. to lexicographically considering
   % (order, degree). That is, the order is the principle criterion.
   begin scalar p1,p2,v;
      v := dcfsf_mvar f1;
      p1 := dcfsf_orddegf(f1,v);
      p2 := dcfsf_orddegf(f2,v);
      return car p1 < car p2 or car p1 = car p2 and cdr p1 < cdr p2
   end;

procedure dcfsf_enf1c1(i,r,veql1_,vnel,oeql,onel,v);
   % Elimination normal form subroutine, Case 1: initial = 0. [i], [r]
   % are SFs; [veql1_], [vnel], [oeql], [onel] are <pl>s; [v] is an
   % identifier. Returns a <pl>.
   begin scalar veql1,oeql1;
      veql1 . oeql1 := dcfsf_inserteq(veql1_,oeql,r,v);
      veql1 . oeql1 := dcfsf_inserteq(veql1,oeql1,i,v);
      return pl_new(veql1,vnel,oeql1,onel)
   end;

procedure dcfsf_enf1c2(i,r,s,veql,vnel,oeql,onel,v);
   % Elimination normal form subroutine, Case 2: inital <> 0 and
   % separant = 0. [i], [r], [s] are SFs; [veql], [vnel], [oeql], [onel]
   % are <pl>; [v] is an identifier. Returns a <pl>.
   begin scalar veql1,vnel1,oeql1,onel1;
      vnel1 . onel1 := dcfsf_insertne(vnel,onel,i,v);
      veql1 . oeql1 := dcfsf_reduceeq(veql,oeql,s,v);
      veql1 := lto_insert(s,veql1);
      vnel1 . onel1 := dcfsf_reducene(vnel1,onel1,s,v);
      return pl_new(veql1,vnel1,oeql1,onel1)
   end;

procedure dcfsf_enf1c3(i,s,veq1,veql1_,vnel,oeql,onel,v);
   % Elimination normal form subroutine, Case 3: inital <> 0 and
   % separant <> 0. [i], [s] are SFs; [veql], [vnel], [oeql], [onel] are
   % <pl>; [v] is an identifier. Returns a <pl>.
   begin scalar veql1,vnel1,oeql1,onel1;
      vnel1 . onel1 := dcfsf_insertne(vnel,onel,i,v);
      vnel1 . onel1 := dcfsf_insertne(vnel1,onel1,s,v);
      vnel1 . onel1 := dcfsf_reducene(vnel1,onel1,veq1,v);
      veql1 . oeql1 := dcfsf_reduceeq(veql1_,oeql,veq1,v);
      veql1 := lto_insert(veq1,veql1);
      return pl_new(veql1,vnel1,oeql1,onel1)
   end;

procedure dcfsf_enf1c3b(i,s,veq1,veql,vnel,oeql,onel,v);
   % Elimination normal form subroutine, Case 3 base: inital <> 0 and
   % separant <> 0 and no more equations in [v]. [i], [s] are SFs;
   % [veql], [vnel], [oeql], [onel] are <pl>; [v] is an identifier.
   % Returns a <pl>.
   begin scalar vnel1,onel1;
      vnel1 . onel1 := dcfsf_insertne(vnel,onel,i,v);
      vnel1 . onel1 := dcfsf_insertne(vnel1,onel1,s,v);
      vnel1 . onel1 := dcfsf_reducene(vnel1,onel1,veq1,v);
      return pl_new(veql,vnel1,oeql,onel1)
   end;

procedure dcfsf_reduceeq(fl,ofl,g,v);
   begin scalar w,vfl;
      w := dcfsf_reduce(fl,g,v);
      for each f in w do
	 vfl . ofl := dcfsf_inserteq(vfl,ofl,f,v);
      return vfl . ofl
   end;

procedure dcfsf_reducene(fl,ofl,g,v);
   begin scalar w,vfl;
      w := dcfsf_reduce(fl,g,v);
      vfl := {1};
      for each f in w do
	 vfl . ofl := dcfsf_insertne(vfl,ofl,f,v);
      return vfl . ofl
   end;

procedure dcfsf_reduce_old(fl,g,v);
   begin scalar of,df,og,dg,g1,w,vfl,ofl;
      og . dg := dcfsf_orddegf(g,v);
      w := for each f in fl collect <<
      	 of . df := dcfsf_orddegf(f,v);
      	 if of < og or (of = og and df < dg) then
	    f
	 else if eqn(of,og) then  % we know df >= dg
	    cdr qremf(multf(f,exptf(lc g,df-dg+1)),g)
	 else <<  % we know of > og
	    g1 := dcfsf_derivationf(g,of-og,nil);
	    % After differntiating at least once, the degree of g1 is 1.
	    cdr qremf(multf(f,exptf(lc g1,df)),g1)
	 >>
      >>;
      return w
   end;

procedure dcfsf_reduce(fl,g,v);
   begin scalar of,df,og,dg,g1,w,vfl,ofl;
      og . dg := dcfsf_orddegf(g,v);
      w := for each f in fl collect <<
      	 of . df := dcfsf_orddegf(f,v);
	 while of > og or (of = og and df >= dg) do <<
 	    if eqn(of,og) then  % we know df >= dg
	       f := cdr qremf(multf(f,exptf(lc g,df-dg+1)),g)
	    else <<  % we know of > og
	       g1 := dcfsf_derivationf(g,of-og,nil);
	       % After differentiating at least once, the degree of g1 is 1.
	       f := cdr qremf(multf(f,exptf(lc g1,df)),g1)
	    >>;
      	    of . df := dcfsf_orddegf(f,v)
	 >>;
	 f
      >>;
      return w
   end;

procedure dcfsf_inserteq(vl,ol,f,v);
   if dcfsf_mvar f eq v then
      lto_insert(f,vl) . ol
   else
      vl . lto_insert(f,ol);

procedure dcfsf_insertne(vl,ol,f,v);
   if dcfsf_mvar f eq v then
      {sfto_sqfpartf multf(f,car vl)} . ol
   else
      vl . lto_insert(f,ol);

endmodule;  % [dcfsfqe]

end;  % of file
