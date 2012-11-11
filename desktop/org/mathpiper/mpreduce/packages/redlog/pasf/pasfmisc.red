% ----------------------------------------------------------------------
% $Id: pasfmisc.red 1796 2012-10-30 07:06:15Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2002-2009 A. Dolzmann, A. Seidl, T. Sturm, 2010 T. Sturm
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
   fluid '(pasf_misc_rcsid!* pasf_misc_copyright!*);
   pasf_misc_rcsid!* :=
      "$Id: pasfmisc.red 1796 2012-10-30 07:06:15Z thomas-sturm $";
   pasf_misc_copyright!* :=
      "(c) 1995-2009 by A. Dolzmann, A. Seidl, T. Sturm, 2010 T. Sturm"
>>;

module pasfmisc;
% This module provides a collection of algorithms shared by all other modules
% in Presburger arithmetic standard form (PASF) context.

procedure pasf_atf2iv(atf);
   % Presburger arithmetic standard form atomic formula to interval. [atf] is
   % an atomic formula. Returns an interval as a dotted pair of lower bound
   % and upper bound or a congruence class (see also iv_newcong).
   begin scalar dc,nom,den,floor,ceil,eucd;
      dc := repr_atfnew(atf,car cl_fvarl atf,nil);
      nom := repr_a dc;
      den := repr_n dc;
      floor := pasf_floor(nom,den);
      ceil := pasf_ceil(nom,den);
      if repr_op dc eq 'equal then
	 % Check if the equality solution is an integer
	 if eqn(den,1) then
	    % Floor and ceil are the same
	    return iv_new(floor,floor)
	 else
	    return {};
      if repr_op dc eq 'leq then
	 return iv_new('minf,floor);
      if repr_op dc eq 'lessp then
	 return iv_new('minf,addf(ceil,negf 1));
      if repr_op dc eq 'geq then
	 return iv_new(ceil,'pinf);
      if repr_op dc eq 'greaterp then
	 return iv_new(addf(floor,1),'pinf);
      if repr_op dc eq 'neq then
	 return iv_merge(iv_new('minf,addf(ceil,negf 1)),
	    iv_new(addf(floor,1),'pinf));
      if pasf_congp atf then
	 % Check if the equality solution is an integer
	 if gcdf(den,pasf_m atf) eq 1 then <<
	    eucd := sfto_exteucd(den,pasf_m atf);
	    return iv_newcong(repr_op dc,multf(cadr eucd,nom))
	 >> else
	    return {};
     rederr{"pasf_atf2iv: illegal operator ",pasf_op atf}
   end;

procedure pasf_qff2ivl(f);
   % Presburger arithmetic standard form positive quantifier free formula to
   % interval. [f] is a quantifier free formula in one variable. Returns the
   % satisfability set of [f] as a list of ascending intervals.
   if pasf_uprap f then
      rederr{"pasf_qff2ivl : uniform Presburger arithmetic formula in input"}
   else
      pasf_qff2ivl1 pasf_dnf f;

procedure pasf_qff2ivl1(f);
   % Presburger arithmetic standard form positive quantifier free formula to
   % interval subprocedure. [f] is a quantifier free formula in one variable.
   % Returns the satisfability set of [f] as a list of ascending intervals IV.
   begin scalar fs,cng;
      if rl_tvalp f then
	 % 'true or 'false
	 if f eq 'true then
	    rederr{"pasf_qff2ivl1 : true as a bound is invalid"}
	 else
	    return nil;
      if rl_op f eq 'and then <<
      	 % Using the fact the formula is in DNF. Than an "and" is the end node
      	 % of the formula's evaluation tree
      	 for each pf in rl_argn f do
	    % Only atomic formulas
	    if pasf_congp pf then
	       cng := (car pasf_atf2iv pf) . cng
	    else
	       fs := (pasf_atf2iv pf) . fs;
         % print cng;
	 return iv_cutcongs(iv_cutn fs,cng)
      >> else
      	 if rl_op f eq 'or then
      	    return iv_mergen for each pf in rl_argn f collect
	       pasf_qff2ivl1 pf;
      % This case can only occur if one of the DNF's conjunctions contains
      % only one atomic formula (e.g. x = 0 or x = 10). This leads to a
      % correct expansion only if this atomic formula is an equality (truth
      % values are already concerned) and represent so a finite set. In other
      % cases the original input formula represents an infinite set and is
      % illegal for expansion. Note: Atomic formulas are assumed to be
      % simplified.
      if pasf_opn f eq 'equal then return pasf_atf2iv f;
      % Something is wrong (mainly an error in DNF computation or a formula
      % with infinite satisfiability set in input)
      rederr{"pasf_qff2ivl1 : abused procedure call with",f}
   end;

procedure pasf_ivl2qff(ivl,var);
   % Presburger arithmetic standard form interval list to quantifier free
   % formula. [ivl] is an interval list; [var] is a free variable. Returns a
   % quantifier free formula with [var] as single free valiable with [ivl] as
   % satisfiability interval.
   if not iv_empty ivl then
      rl_smkn('or,for each iv in ivl collect
      	 pasf_mkrng(numr simp var,numr simp car iv,numr simp cdr iv))
   else
      'false;

procedure pasf_bsatp(f,var);
   % Presburger arithmetic standard form bound satisfiability. [f] is
   % a bound. Returns [t] if the bound is satifiable and nil if it is
   % too expensive to compute or if the bound is equivalent to 'false.
   begin scalar flg,r,argn,argna,a;
      if cl_fvarl f neq {var} then return nil;
      f := pasf_dnf f;
      if f eq 'false then return nil;
      if f eq 'true then rederr {"pasf_bsatp: infinite bound"};
      % Looking for one argument in the DNF without a congruence
      argn := if rl_op f eq 'or then rl_argn f else {f};
      while argn and null r do <<
	 argna := if rl_op car argn eq 'and then
	    rl_argn car argn else {car argn};
	 flg := nil;
 	 while argna and not flg do <<
	    flg := pasf_congp car argna;
	    argna := cdr argna
	 >>;
	 % Found a constituent without congruences
	 if null flg and not iv_empty pasf_qff2ivl car argn then
	    r := t;
	 argn := cdr argn
      >>;
      return r
   end;

procedure pasf_b2atl(b,k);
   % Presburger arithmetic standard form bound to list of atoms. [b] is a
   % formula in at most one variable with finite satisfability set in DNF; [k]
   % is the variable of [b]. Returns a list of atomic formulas
   % $(\varphi_i)_{i}$, so that the equivalence $$\bigvee_i \varphi_i
   % \longleftrightarrow b$$ holds and nil if it is too expensive to derive
   % atomic formulas from the bound.
   if rl_tvalp b then
      (if b eq 'false then {} else rederr "pasf_b2atl: infinite bound")
   else if cl_atfp b then
      {b}
   else if rl_op b eq 'and then
      % Using the fact the application of b2atl is performed after a DNF
      rl_argn b;

procedure pasf_simplb(f,var);
   % Presburger arithmetic standard form simplify formulas' bound. [f] is a
   % bound of some bounded formula; [var] is the bounded variable. Returns an
   % [f]-equivalent simplified formula (flat simplified DNF of [f]).
   begin scalar sb,nsb,flg,argn,argna;
      f := pasf_dnf cl_simpl(f,nil,-1);
      if rl_tvalp f then return f;
      % If the bound is parametric or contains univariate formulas only normal
      % simplification is done
      if length cl_fvarl f > 1 or pasf_univnlfp(f,var) then
	 return f;
      % Looking for one argument in the DNF without a congruence
      argn := if rl_op f eq 'or then rl_argn f else {f};
      % Note: Congruences in the bound are critical to heap space and time
      for each arg in argn do <<
	 flg := nil;
	 argna := if rl_op arg eq 'and then rl_argn arg else {arg};
	 for each a in argna do if pasf_congp a then flg := t;
	 if null flg then
	    sb := arg . sb
	 else
	    nsb := arg . nsb
      >>;
      sb := pasf_ivl2qff(pasf_qff2ivl rl_smkn('or,sb),var);
      return cl_simpl(rl_smkn('or,sb . nsb),nil,-1)
   end;

procedure pasf_b2terml(b,var);
   % Presburger arithmetic standard form bound to termlist. [b] is a bound of
   % some bounded formula; [var] is the bounded variable. Returns the
   % satisfiability set as a list of satisfying terms (for example
   % $\{1,2,3,10\}$).
   begin scalar ivl;
      % Term list for uniform bounds not possible
%%       if length cl_fvarl b > 1 then
%% 	 rederr{"pasf_b2terml called with a parametric bound"};
      % Note: imprudent use of this code is extremely space- and time-critical
      ivl := pasf_qff2ivl b;
      return for each iv in ivl join
	 if (numberp car iv) and (numberp cdr iv) then
	    for i := car iv : cdr iv collect
	       i
     	 else
	    rederr{"pasf_b2terml : trying to expand infinite bound"}
   end;

procedure pasf_rmax(rng1,rng2);
   % Presburger arithmetic standard range maximum. [rng1] and [rng2] are pairs
   % of integers representing intervals. Returns an pair of integers
   % representing inteval that contains both [rng1] and [rng2].
   (pasf_min(car rng1,car rng2) . pasf_max(cdr rng1,cdr rng2));

procedure pasf_brng(b,var);
   % Presburger arithmetic standard form bound range. [b] is a bound; [var] is
   % the bound variable. Returns a pair of minimal and maximal bound values.
   begin scalar tmp,bmax;
      % Range approximation for uniform bounds is not possible
      if length cl_fvarl b > 1 then
	 rederr{"pasf_brng called with parametric bound"};
      tmp := cl_simpl(pasf_dnf b,nil,-1);
      if tmp eq 'false then
	 rederr{"Not satisfiable bound in pasf_brng"};
      if tmp eq 'true then
	 rederr{"Tautological bound in pasf_brng"};
      bmax := ('pinf . 'minf);
      % Note: The initial value for bmax is always rewritten by the first
      % application of pasf_rmax since each result of pasf_brng1 describes a
      % finite set.
      if rl_op tmp eq 'or then
	 for each sf in rl_argn tmp do
	    bmax := pasf_rmax(pasf_brng1(sf,var),bmax)
      else
	 bmax := pasf_brng1(tmp,var);
      return bmax;
   end;

procedure pasf_brng1(b,var);
   % Presburger arithmetic standard form bound range. [b] is a bound that is a
   % conjunction; [var] is the bound variable. Returns a pair of minimal and
   % maximal bound values.
   begin scalar tmp;
      % We can simply remove all congruences, since they only strengthten the
      % solution set.
      tmp := pasf_qff2ivl rl_smkn('and,
	 for each atf in cl_atl b collect
	    if null (pasf_op atf memq '(cong ncong)) then
	       atf);
      if null tmp then
	 rederr{"pasf_brng1 : Something is wrong, empty bound solution set"};
      return (caar tmp . cdar reverse tmp);
   end;

procedure pasf_ordatp(a1,a2);
   % Presburger arithmetic standard form atomic formula predicate. [a1] is an
   % atomic formula; [a2] is an atomic formula. Returns t iff [a1] is less
   % than [a2].
   begin scalar lhs1,lhs2;
      lhs1 := pasf_arg2l a1;
      lhs2 := pasf_arg2l a2;
      if lhs1 neq lhs2 then return ordp(lhs1,lhs2);
      return pasf_ordrelp(pasf_opn a1,pasf_opn a2)
   end;

procedure pasf_ordrelp(r1,r2);
   % Presburger arithmetic standard form relation order predicate. [r1] is a
   % relation; [r2] is a relation. Returns t iff $[r1] < [r2]$.
   not not (r2 memq (r1 memq '(equal neq
      leq lessp geq greaterp cong ncong)));

procedure pasf_dec(u);
   % Presburger arithmetic standard form decompose a standard form. [u] is a
   % SF. Returns a pair $(p . a)$, where $p$ and $a$ are SF's. $p$ is the
   % parametric part of [u] and $a$ is the absolut part of [u].
   begin scalar absv;
      absv := u;
      while not domainp absv do
	 absv := red absv;
      return (addf(u,negf absv) . absv)
   end;

procedure pasf_deci(u);
   % Presburger arithmetic standard form decompose a standard form with
   % integer constant part. [u] is a SF. Returns a pair $(p . a)$, where $p$
   % is a SF and $a$ is an integer; $p$ is the parametric part of [u] and $a$
   % is the absolut part of [u].
   begin scalar r;
      r := pasf_dec u;
      return (car r . if null cdr r then 0 else cdr r)
   end;

procedure pasf_varlat(atf);
   % Presburger arithmetic standard form atomic formula list of variables.
   % [atf] is an atomic formula. Returns the variables contained in $atf$ as a
   % list.
   append(kernels pasf_arg2l atf,
      if pasf_congp atf then kernels pasf_m atf else nil);

procedure pasf_varsubstat(atf,new,old);
   % Presburger arithmetic standard form substitute variable for variable in
   % atomic formula. [atf] is an atomic formula; [new] is a variable; [old] is
   % a variable. Returns an atomic formula equivalent to [atf] where [old] is
   % substituted with [new].
   if rl_tvalp atf then
      % If so no substitution is done.
      atf
   else
      pasf_0mk2(if pasf_congp atf then
	 % Substituting in modulus also
      	 (pasf_opn atf . numr subf(pasf_m atf,{old . new}))
      else
	 pasf_op atf,
	 numr subf(pasf_arg2l atf,{old . new}));

procedure pasf_negateat(atf);
   % Presburger arithmetic standard form negate atomic formula. [atf] is an
   % atomic formula. Returns an atomic formula equivalent to $\lnot([atf])$.
   if rl_tvalp atf then
      (if atf eq 'false then 'true else 'false)
   else if (pasf_opn atf) memq '(cong ncong) then
      pasf_mk2(pasf_mkop(pasf_lnegrel pasf_opn atf,pasf_m atf),
	 pasf_arg2l atf, pasf_arg2r atf)
   else
      pasf_mk2(pasf_lnegrel pasf_opn atf,pasf_arg2l atf,pasf_arg2r atf);

procedure pasf_lnegrel(r);
   % Presburger arithmetic standard form logically negate relation. [r] is a
   % relation. Returns a relation $\rho$ such that for terms $t_1$, $t_2$ we
   % have $\rho(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$.
   if r eq 'equal then 'neq
   else if r eq 'neq then 'equal
   else if r eq 'leq then 'greaterp
   else if r eq 'lessp then 'geq
   else if r eq 'geq then 'lessp
   else if r eq 'greaterp then 'leq
   else if r eq 'cong then 'ncong
   else if r eq 'ncong then 'cong
   else rederr {"pasf_lnegrel: unknown operator",r};

procedure pasf_anegateat(atf);
   % Presburger arithmetic standard form negate atomic formula
   % algebraically. [atf] is an atomic formula. Returns an atomic formula
   % equivalent to $-([atf])$.
   if (pasf_opn atf) memq '(cong ncong) then
      pasf_mk2(pasf_mkop(pasf_anegrel pasf_opn atf,pasf_m atf),
	 negf pasf_arg2l atf,negf pasf_arg2r atf)
   else
      pasf_mk2(pasf_anegrel pasf_opn atf,
	 negf pasf_arg2l atf,negf pasf_arg2r atf);

procedure pasf_anegrel(r);
   % Presburger arithmetic standard form algebraically negate relation. [r] is
   % a relation. Returns a relation $\rho$ such that $\rho(-t,0)$ is
   % equivalent to $[r](t,0)$ for a term $t$.
   cdr atsoc(r,'((equal . equal) (neq . neq) (leq . geq) (geq . leq)
      (lessp . greaterp) (greaterp . lessp) (cong . cong) (ncong . ncong)))
	 or rederr {"pasf_anegrel: unknown operator ",r};

procedure pasf_subat(al,f);
   % Presburger arithmetic standard form substitute into an atomic
   % formula. [al] is a substitution list; [f] is the formula. Returns an
   % atomic formula after substitution.
   begin scalar nlhs,nlhs1;
      for each a in al do
	 if null eqn(denr simp cdr a,1) then
	    rederr "pasf_subat: only presburger terms can be substituted";
      if pasf_congp f then <<
      	 nlhs := subf(pasf_arg2l f,al);
	 nlhs1 := subf(pasf_m f,al);
      	 if not domainp denr nlhs or not domainp denr nlhs1 then
	    rederr "pasf_subat: parametric denominator after substitution";
	 return pasf_0mk2((pasf_opn f . numr nlhs1), numr nlhs)
      >>;
      nlhs := subf(pasf_arg2l f,al);
      if not domainp denr nlhs then
	 rederr "pasf_subat: parametric denominator after substitution";
      return pasf_0mk2(pasf_op f,numr nlhs)
   end;

procedure pasf_mkstrict(r);
   % Presburger arithmetic standard form make strict. [r] is an ordering
   % relation. Returns the strict part of [r].
   if r eq 'leq then 'lessp else if r eq 'geq then 'greaterp else r;

procedure pasf_subalchk(al);
   % Presburger arithmetic standard form check for parametric
   % denominators. [al] is a list. Returns nil or raises an error.
   for each x in al do
      if not domainp denr simp cdr x then
	 rederr "pasf_subalchk: parametric denominator in substituted term";

procedure pasf_eqnrhskernels(x);
   % Presburger arithmetic standard form equation right handside kernels. [x]
   % is an expression. Returns a list of kernels.
   nconc(kernels numr w,kernels denr w) where w=simp cdr x;

procedure pasf_floor(nom,den);
   % Presburer arithmetic standard form floor of two domain valued standard
   % forms. [nom] is the nominator SF; [den] is the denominator SF. Returns
   % $\lfloor \frac{[nom]}{[den]} \rfloor$.
   if domainp  nom and domainp den then
      if null nom then
	 nil
      else
      	 numr simp
	    if remainder(nom,den) = 0 then
      	       nom / den
	    % The value is not negative
	    else if nom*den > 0 then
	       nom / den
	    else
	       nom / den - 1
   else
      rederr{"pasf_floor: not a domain valued sf in input",nom,den};

procedure pasf_ceil(nom,den);
   % Presburer arithmetic standard form ceil of two domain valued standard
   % forms. [nom] is the nominator SF; [den] is the denominator SF. Returns
   % $\lceil \frac{[nom]}{[den]} \rceil$.
   if domainp nom and domainp den then
      if null nom then
	 nil
      else
	 numr simp
	    if remainder(nom,den) = 0 then
	       nom / den
	    % The value is not negative
   	    else if nom*den > 0 then
	       nom / den + 1
	    else
	       nom / den
   else
      rederr{"pasf_ceil: not a domain valued sf in input",nom,den};

procedure pasf_const(ex);
   % Presburger arithmetic standard form constant part of an expresion
   % computation. [expr] is an expression. Returns the constant part of [ex].
   if domainp ex then
      ex
   else
      pasf_const red ex;

procedure pasf_fctrat(atf);
   % Presburger arithmetic standard form factorize atomic formula. [atf] is an
   % atomic formula $l \mathrel{\varrho} 0$. Returns a list $(...,(f_i
   % . d_i),...)$, where $f$ is an irreducible SF and $d$ is a
   % positive integer. We have $l=c \prod_i g_i^{d_i}$ for an integer
   % $c$.
   if pasf_congp atf then
      nconc(cdr fctrf pasf_arg2l atf,cdr fctrf pasf_m atf)
   else
      cdr fctrf pasf_arg2l atf;

procedure pasf_termmlat(atf);
   % Presburger arithmetic standard form term multiplicity list of an atomic
   % formula. [atf] is an atomic formula. Returns the multiplicity list of all
   % non-zero terms in [atf].
   if pasf_arg2l atf then {(pasf_arg2l atf . 1)};

procedure pasf_max(a,b);
   % Presburger arithmetic standard form maximum of two constant expressions
   % in $\mathbb{Z} \cup \{ \infty, -\infty \}$. [a] is a constant expression;
   % [b] is a constant epxression. Returns the maximum of [a] and [b].
   if pasf_leqp(a,b) then b else a;

procedure pasf_min(a,b);
   % Presburger arithmetic standard form minimum of two constant expressions
   % in $\mathbb{Z} \cup \{ \infty, -\infty \}$. [a] is a constant expression;
   % [b] is a constant epxression. Returns the minimum of [a] and [b].
   if pasf_leqp(a,b) then a else b;

procedure pasf_leqp(c1,c2);
   % Presburger arithmetic standard form less or equal predicate on extended
   % integer expressions in $\mathbb{Z} \cup \{ \infty, -\infty \}$. [c1] is a
   % constant expression; [c2] is a constant expression. Returns t iff [c1]
   % is less or equal than [c2].
   begin
      if null c1 then c1 := 0;
      if null c2 then c2 := 0;
      return if (c1 eq 'minf) or (c2 eq 'pinf) or
      	 (c1 neq 'pinf and c2 neq 'minf and c1 <= c2) then
	    t
   end;

procedure pasf_leq(c1,c2);
   % Presburger arithmetic standard form less or equal predicate on extended
   % integer expressions in $\mathbb{Z} \cup \{ \infty, -\infty \}$. [c1] is a
   % constant expression; [c2] is a constant expression. Returns t iff [c1]
   % is less or equal than [c2].
   begin
      if null c1 then c1 := 0;
      if null c2 then c2 := 0;
      return if (c1 eq 'minf) or (c2 eq 'pinf) or
      	 (c1 neq 'pinf and c2 neq 'pinf and c2 neq 'minf and c1 < c2) then
	    t
   end;

procedure pasf_expand(f);
   % Presburger arithmetic standard form expand a formula with non
   % parametric bounded quantifiers. [f] is a formula with bounded
   % quantifiers. Returns an equivalent formula without bounded
   % quantifiers. If the bounds of [f] are all non-parametric, then it
   % is possible to expand smartly. Note: [rl_pnf] renames variables
   % so that the bounded variables, and free variables are distinct.
   begin scalar fdec,flag,tmp;
      if !*rlverbose then ioto_tprin2 {"++++ Entering pasf_expand"};
      % TS: Blindly use pasf_exprng for now
      if !*rlverbose then ioto_prin2t " (exprng)";
      return cl_simpl(pasf_exprng f, nil, -1);
      %
      fdec := fdec_new(pasf_pnf f,nil);
      for each b in fdec_bvl fdec do <<
	 tmp := cl_fvarl car b;
	 if length tmp > 1 or (length tmp = 1 and cdr b neq car tmp) then
	    flag := t
      >>;
      return if flag then <<
      	 if !*rlverbose then ioto_prin2t " (regular)";
      	 cl_simpl(pasf_exprng1 f,nil,-1)
      >> else <<
      	 if !*rlverbose then ioto_prin2t " (smart)";
	 cl_simpl(pasf_exprng2 f,nil,-1)
      >>
   end;

procedure pasf_exprng1(f);
   % Presburger arithmetic standard form expand bounded quantifier. [f] is a
   % formula. Returns an equivalent formula, where each bounded quantifier is
   % expanded.
   begin scalar op;
      if rl_tvalp f then
 	 return f;
      op := rl_op f;
      if rl_boolp op then
	 return rl_smkn(op,for each arg in rl_argn f collect pasf_exprng1 arg);
      if rl_quap op then
      	 return rl_mkq(op,rl_var f,pasf_exprng1 rl_mat f);
      if op eq 'ball then
	 return pasf_exprng1!-gand(
	    op,rl_var f,rl_b f,rl_mat f,'and,'true,'false);
      if op eq 'bex then
	 return pasf_exprng1!-gand(
	    op,rl_var f,rl_b f,rl_mat f,'or,'false,'true);
      return f
   end;

procedure pasf_exprng1!-gand(op,v,b,m,gand,gtrue,gfalse);
   begin scalar w,matj,terml,j,c,resl;
      w := cl_fvarl b;
      if not eqcar(w,v) or cdr w then
	 rederr {"Expanding a parametric bounded formula is impossible"};
      terml := pasf_b2terml(b,v);
      if !*rlverbose then ioto_prin2 {"[",op,",",v,",",length terml};
      c := t; while c and terml do <<
	 j := car terml;
	 terml := cdr terml;
	 % if !*rlverbose then ioto_prin2 {"(",j,")"};
	 matj := cl_simpl(pasf_exprng1 pasf_subfof(v,j,m),nil,-1);
	 if matj eq gfalse then <<
	    if !*rlverbose then ioto_prin2 {"!"};
	    secondvalue!* := (v . j) . secondvalue!*;
	    c := nil
	 >> else if matj neq gtrue then
	    resl :=  matj . resl
      >>;
      if !*rlverbose then ioto_prin2 {"]"};
      return if c then cl_simpl(rl_smkn(gand,resl),nil,-1) else gfalse
   end;

procedure pasf_exprng2(f);
   % Presburger arithmetic standard form expand bounded quantifier smart. [f]
   % is a formula. Returns an equivalent formula, where each bounded
   % quantifier is expanded.
   begin scalar terml,evaltype,matr,tmp,res;
      if rl_tvalp f then return f;
      if rl_boolp rl_op f then
	 return rl_smkn(rl_op f,for each sf in rl_argn f collect
	    cl_simpl(pasf_exprng2 sf,nil,-1));
      if rl_bquap rl_op f then <<
      	 % Long or or long and check
      	 if rl_op f eq 'bex then
	    evaltype := 'or
      	 else if rl_op f eq 'ball then
	    evaltype := 'and
      	    else
	       % Unknown operator
	       rederr{"pasf_expand : unknown or illegal quantifier",rl_op f};
	 tmp := cl_fvarl rl_b f;
	 if cdr tmp or not eqcar(tmp, rl_var f) then
	    rederr {"Expanding a parametric bounded formula is impossible"};
	 terml := pasf_b2terml(rl_b f,rl_var f);
	 matr := pasf_exprng2 rl_mat f;
	 if !*rlverbose then
	    ioto_tprin2t {"---- (",rl_op f," ",rl_var f,")"};
	 res := {};
	 for each j in terml collect <<
	    if !*rlverbose then ioto_prin2 {"[",j,"]"};
	    res := cl_simpl(pasf_subfof(rl_var f,j,matr),nil,-1) . res
	 >>;
	 ioto_prin2t {""};
	 return rl_smkn(evaltype,res)
      >>;
      if rl_quap rl_op f then
      	 rl_mkq(rl_op f, rl_var f, pasf_exprng2 rl_mat f);
      return f
   end;

procedure pasf_exprng(f);
   % Expand range. [f] is a weakly quantifier-free formula. Returns a
   % quantifier-free formula.
   begin scalar op, w, !*rlsism;
      op := rl_op f;
      if op eq 'and then
	 return pasf_exprng!-gand('and, rl_argn f, 'true, 'false);
      if op eq 'or then
	 return pasf_exprng!-gand('or, rl_argn f, 'false, 'true);
      if op eq 'ball then
	 return pasf_exprng!-gball(
	    rl_var f, rl_b f, rl_mat f, 'and, 'true, 'false);
      if op eq 'bex then
	 return pasf_exprng!-gball(
	    rl_var f, rl_b f, rl_mat f, 'or, 'false, 'true);
      if rl_boolp op then <<
	 w := for each subf in rl_argn f collect pasf_exprng subf;
	 return cl_simpl(rl_smkn(op, w), nil, -1)
      >>;
      % [f] is atomic or a truth value.
      return f
   end;

procedure pasf_exprng!-gand(gand, argl, gtrue, gfalse);
   begin scalar c, a, w, nargl;
      c := t; while c and argl do <<
	 a := pop argl;
	 w := pasf_exprng a;
	 if w eq gfalse then
	    c := nil
	 else if w neq gtrue then
	    nargl := w . nargl
      >>;
      if not c then
	 return gfalse;
      return rl_smkn(gand, nargl)
   end;

% Experimental option to expand from the inside to the outside instead of a
% natural recursion. This is a more natural recursion but it does not work in
% general when there are nested bounded quantifiers.
switch rlexprngnatural;
off1 'rlexprngnatural;

procedure pasf_exprng!-gball(v, b, m, gand, gtrue, gfalse);
   begin scalar c, u, w, argl, ivl, iv;
      w := cl_fvarl b;
      if not eqcar(w, v) or cdr w then
	 rederr {"pasf_exprng: bad bound ",b," with free variables ", w};
      if !*rlexprngnatural then
      	 m := pasf_exprng m;
      ivl := pasf_qff2ivl b;
      c := t; while c and ivl do <<
	 iv := pop ivl;
	 u := car iv;
	 while c and u leq cdr iv do <<
	    w := pasf_sisub(m, v, u);
	    if not !*rlexprngnatural then
 	       w := pasf_exprng w;
	    if w eq gfalse then
	       c := nil
	    else <<
	       if w neq gtrue then
	       	  argl := w . argl;
	       u := u + 1
	    >>
	 >>
      >>;
      if not c then
	 return gfalse;
      return rl_smkn(gand, argl)
   end;

procedure pasf_sisub(f, v, n);
   % Simplifying substitution. [f] is a formula, [v] is a variable, [n] is an
   % integer.
   begin scalar op;
      op := rl_op f;
      if rl_quap op then
	 return rl_mkq(op, rl_var f, pasf_sisub(rl_mat f, v, n));
      if rl_bquap op then
	 return rl_mkbq(
	    op, rl_var f, pasf_sisub(rl_b f, v, n), pasf_sisub(rl_mat f, v, n));
      if op eq 'and then
	 return pasf_sisub!-gand('and, rl_argn f, v, n, 'true, 'false);
      if op eq 'or then
	 return pasf_sisub!-gand('or, rl_argn f, v, n, 'false, 'true);
      if rl_boolp op then
	 return rl_smkn(op, for each sf in rl_argn f collect
	    pasf_sisub(sf, v, n));
      if rl_tvalp op then
	 return f;
      % [f] is atomic.
      return pasf_simplat1(
	 pasf_0mk2(pasf_op f, numr subf(pasf_arg2l f, {v . n})), op)
   end;

procedure pasf_sisub!-gand(gand, argl, v, n, gtrue, gfalse);
   begin scalar c, w, a, nargl;
      c := t; while c and argl do <<
	 a := pop argl;
	 w := pasf_sisub(a, v, n);
	 if w eq gfalse then
	    c := nil
	 else if w neq gtrue then
	    nargl := w . nargl
      >>;
      if not c then
	 return gfalse;
      return rl_smkn(gand, nargl)
   end;

switch hack;

procedure pasf_expanda(answ,phi);
   % Presburger arithmetic standard form expand an answer. [answ] is
   % an answer structure. Returns an answer with expanded first
   % components. The argument [phi] is not yet used. This is planned
   % to be the original quantified formula so that its matrix can be
   % possibly used for finding suitable values.
   begin scalar guard,w,badl,goodl,gdis,sample;
      for each a in answ do <<
	 secondvalue!* := nil;
      	 guard := pasf_expand car a;
	 w := secondvalue!*;
	 sample := pasf_findsample(cadr a,caddr a,w);
	 if car sample then
	    badl := lto_insert({guard,nconc(cdr sample,'!! . car sample)},badl)
	 else
	    goodl := lto_insert({guard,cdr sample},goodl)
      >>;
      gdis := cl_simpl(rl_smkn('or,for each gp in goodl collect car gp),nil,-1);
      if !*rlqeasri then
      	 badl := for each gp in badl join
	    if pasf_srip(car gp,gdis) then <<
	       if !*rlverbose then ioto_prin2 "(SRI) ";
	       nil
	    >> else
	       {gp};
      return nconc(reversip goodl,reversip badl)
   end;

procedure pasf_srip(prem,concl);
   % Presburger arithmetic standard form simplifier-recognized
   % implication.
   cl_simpl(rl_mk2('impl,prem,concl),nil,-1) eq 'true;

procedure pasf_findsample(rangel,points,hitl);
   begin scalar w,answ,nrangel;
      answ := for each point in points collect
	 {car point,cadr point,prepsq subsq(simp caddr point,hitl)};
      nrangel := for each range in rangel join <<
	 w := cl_simpl(cl_subfof(hitl,range),nil,-1);
	 % FRAGE: Kann false rauskommen? Was dann?
	 if not rl_tvalp w then {rl_prepfof w}
      >>;
      return nrangel . answ
   end;

procedure pasf_zsimpl(f);
   begin scalar w,z,fl,fb,best,gleq,glessp,gone;
      w := cl_fvarl f;
      if cdr w then rederr {"pasf_zsimpl: more than one variable: ",w};
      z := car w;
      f := cl_dnf f;
      fl := if rl_op f eq 'or then rl_argn f else {f};
      fb := pasf_zsimpl!-firstbound fl;
      if fb eq 'lessp or fb eq 'leq then <<
	 gleq := 'leq;
	 glessp := 'lessp;
	 gone := 1
      >> else if fb eq 'greaterp or fb eq 'geq then <<
	 gleq := 'geq;
	 glessp := 'greaterp;
	 gone := -1
      >> else
	 rederr "pasf_zsimpl: cannot determine direction";
      for each arg in fl do
	 best := pasf_improve(z,best,arg,gleq,glessp,gone);
      return pasf_0mk2(gleq,z .** 1 .* 1 .+ -best)
   end;

procedure pasf_zsimpl!-firstbound(fl);
   begin scalar f,op,fb,atl;
      while not fb and fl do <<
	 f := car fl;
	 fl := cdr fl;
	 atl := if rl_op f eq 'and then rl_argn f else {f};
	 while not fb and atl do <<
	    op := rl_op car atl;
	    atl := cdr atl;
	    if op memq '(lessp leq greaterp geq) then
	       fb := op
	 >>
      >>;
      return fb
   end;

procedure pasf_improve(z,best,arg,gleq,glessp,gone);
   begin scalar op,argl,type,cand,congl,cong;
      argl := if rl_op arg eq 'and then rl_argn arg else {arg};
      for each at in argl do <<
	 if pasf_congp at then
	    congl := at . congl
	 else <<
	    op := rl_op at;
	    if op eq gleq then <<
	       if type then rederr {"pasf_improve: too many bounds in",arg};
	       cand := pasf_improve!-getval(z,rl_arg2l at)
	    >> else if op eq glessp then <<
	       if type then rederr {"pasf_improve: too many bounds in",arg};
	       cand := pasf_improve!-getval(z,rl_arg2l at) - gone
	    >> else if op eq 'equal then <<
	       if type then rederr {"pasf_improve: too many bounds in",arg};
	       cand := pasf_improve!-getval(z,rl_arg2l at)
	    >> else rederr {"pasf_improve: unexpected operator",op};
	    type := op
	 >>
      >>;
      if best and eval {gleq,cand,best} then return best;
      cong := rl_smkn('and,congl);
      if type eq 'equal then
	 return if pasf_improve!-congp(z,cand,cong) then cand else best;
      while (null best or eval {glessp,best,cand}) and
	 not pasf_improve!-congp(z,cand,cong)
      do
	 cand := cand - gone;
      return if (null best or eval {glessp,best,cand}) then cand else best
   end;

procedure pasf_improve!-getval(z,u);
   <<
      if mvar u neq z or ldeg u neq 1 or lc u neq 1 then
      	 rederr {"pasf_improve: unexpected term ",u};
      - red u
   >>;

procedure pasf_improve!-congp(z,cand,cong);
   cl_simpl(cl_subfof({z . cand},cong),nil,-1) eq 'true;

procedure pasf_pdp(term);
   % Presburger arithmetic standard form definitness test. [term] is a
   % standard form. Returns one of the 'pdef, 'ndef or 'indef. Note: 'indef
   % has as semantic, that the test for positive and negative definitness has
   % failed. Not that the term is indefinite.
   begin scalar c,r;
      if domainp term then
	 return (if null term then 'indef
      	 else if term < 0 then 'ndef
      	 else if term > 0 then 'pdef
      	 else 'indef);
      if evenp ldeg term then <<
	 c := pasf_pdp lc term;
	 r := pasf_pdp red term;
	 if null r and (c eq 'psdef or c eq 'pdef) then return 'psdef;
	 if null r and (c eq 'nsdef or c eq 'ndef) then return 'nsdef;
	 if r eq 'pdef and (c eq 'psdef or c eq 'pdef) then return 'pdef;
	 if r eq 'ndef and (c eq 'nsdef or c eq 'ndef) then return 'ndef
      >>;
      return 'indef
   end;

procedure pasf_subfof(var,ex,f);
   % Presburger arithmetic standard form substitute into a strong quantifier
   % free formula. [var] is the variable to substitute; [ex] is the expression
   % to substitute; [f] is a formula. Returns the formula where every
   % occurence of [var] is substituted by [ex].
   cl_apply2ats1(f,'pasf_subfof1,{var,ex});

procedure pasf_subfof1(atf,var,ex);
   % Presburger arithmetic standard form substitute into a formula
   % subroutinue. [atf] is an atomic formula; [var] is the variable to
   % substitute; [ex] is the expression to substitute. Returns an atomic
   % formula where every occurence of [var] is substituted by [ex].
   pasf_mk2(if pasf_congp atf then
      (pasf_opn atf . numr subf(pasf_m atf,{(var . ex)}))
   else pasf_opn atf,
      numr subf(pasf_arg2l atf,{(var . ex)}),
      numr subf(pasf_arg2r atf,{(var . ex)}));
   % LASARUK: Evidence for an error!

procedure pasf_newvar(f);
   % Presburger arithmetic standard form new variable generation. [f] is a
   % formula. Returns a new variable which is not present in [f].
   intern gensym();

procedure pasf_newvar1(f);
   % Presburger arithmetic standard form new variable generation. [f] is a
   % formula. Returns a new variable which is not present in [f].
   begin scalar varl,varv,expld,l;
      varl := cl_varl f;
      varv := 0;
      % Checking only the whole varlist
      for each var in append(car varl,cdr varl) do <<
	 expld := explode var;
	 % Looking for k variables
	 if car expld eq 'k then <<
	    l := implode cdr expld;
	    if l >= varv then
	       varv := l+1
	 >>
      >>;
      return implode('k . explode(varv))
   end;

procedure pasf_cauchybnd(p,x);
   % Presburgr arithmetic standard form polynomial sign change bounds. [p] is
   % an expression; [x] is a variable. Returns an expression $b$, such that
   % $\abs{z} \leq b$ for each interval boundary $z$ of [p] in [x].
   begin scalar cl,res;
      cl := pasf_coeflst(p,x);
      for each p in cdr cl do
	 res := addf(res,exptf(car p,2));
      return addf(res,1)
   end;

procedure pasf_cauchybndcl(cl);
   % Presburgr arithmetic standard form polynomial sign change bounds from
   % coefficient list. [cl] is a coefficient list. Returns an expression $b$,
   % such that $\abs{z} \leq b$ for each characteristic point $z$ of the
   % solution set of the polynomial with coefficients [cl]. We assume [cl]
   % here to be sorted such that the highest degree is the first entry.
   begin scalar res;
      for each p in cdr cl do
	 res := addf(res,exptf(car p,2));
      return addf(res,1);
   end;

procedure pasf_coeflst(p,x);
   % Presburgr arithmetic standard form coefficient list. [p] is a polynomial
   % expression; [x] is a variable. Returns a list of pairs of coefficients;
   % the car is a standard form; the cdr is a positive number. The cars are
   % the coefficients of [p] as a polynomial in [x] and the cdrs are the
   % corresponding degrees. We guarantee the list is sorted by degrees
   % starting with the highest one.
   begin scalar oldkord,nexpr,res;
      oldkord := setkorder({x});
      nexpr := reorder p;
      while not domainp nexpr and mvar nexpr eq x do <<
	 res := (lc nexpr . ldeg nexpr) . res;
	 nexpr := red nexpr
      >>;
      setkorder oldkord;
      return reversip ((negf nexpr . 0) . res)
   end;

% ---- Structure definitions and accessor methods ----------------------------

% REPR is a datastructure that represents the decomposition of an atomic
% formula into representant term, bounded term, coefficient list w.r.t. the
% quantified variable and the atomic formula's position in the input
% formula. After the list of [repr] is computed the structure of the
% corresponding formula is not allowed to be changed.

procedure repr_new(pos,op,cl,tn);
   % Presburger arithmetic standard form REPR constructor. [pos] ist the
   % position of the atomic formula in the input; [op] is the operator; [r] is
   % the representant term; [cl] is the list of pairs of coefficients and
   % their power; [tn] is a linear combination of bounded variables. Returns a
   % new REPR structure.
   {pos,op,cl,tn,if null cl then
      rederr{"repr_new : invalid coefficient list"}
   else
      cdar cl};

procedure repr_eq(repr1,repr2);
   % Presburger arithmetic standard form REPR comparator. [repr1] is a REPR
   % structure; [repr2] is a REPR structure. Returns t only if positions of
   % [repr1] and [repr2] are different.
   cdr repr1 eq cdr repr2;

procedure repr_pos(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the position.
   car repr;

procedure repr_setpos(repr,pos);
   % Presburger arithmetic standard form REPR modifier. [repr] is a REPR
   % structure; [pos] is a position. Returns a new REPR structure with new
   % position.
   pos . cdr repr;

procedure repr_op(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the operator.
   cadr repr;

procedure repr_ldeg(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the leading degree of the corresponding formula.
   car cddddr repr;

procedure repr_n(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the leading coefficient.
   if null caddr repr then
      rederr{"repr_n : invalid REPR structure"}
   else if car cddddr repr = 0 then
      nil
   %else if car cddddr repr >= 2 then
   %   % First element of the second element of the coefficient list
   %   car cadr caddr reverse repr
   %   %rederr{"repr_n : nonlinear formula where a linear was expected"}
   else
      caar caddr repr;

procedure repr_r(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the representant term.
   caar reverse caddr repr;

procedure repr_cl(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the coefficients list.
   caddr repr;

procedure repr_t(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the linear combination of bounded variables.
   cadddr repr;

procedure repr_a(repr);
   % Presburger arithmetic standard form REPR accessor. [repr] is a REPR
   % structure. Returns the summ of r and t.
   addf(caar reverse caddr repr,cadddr repr);

procedure repr_atfnew(atf,x,pos);
   % Presburger arithmetic standard form REPR basic atomic formula
   % decompose. [atf] is an atomic formula; [x] is a variable; [pos] is the
   % position of the atomic formula. Returns the according REPR structure.
   begin scalar op,cl;
      op := pasf_op atf;
      cl := pasf_coeflst(pasf_arg2l atf,x);
      if minusf caar cl then <<
	 % Note : multiplication of the modulus by -1 does not change the
	 % semantics
	 op := if pasf_congp atf then
	    (pasf_anegrel car op . cdr op)
	 else
	    pasf_anegrel op;
	 cl := for each c in cl collect
	    (multf(car c,-1) . cdr c);
      >>;
      % This decomposition assumes no bounded variables
      return repr_new(pos,op,cl,nil)
   end;

procedure repr_atfbnew(atf,x,pos,bvl);
   % Presburger arithmetic standard form atomic formula bounded
   % decomposition. [atf] is an atomic formula; [x] is the eliminated
   % variable; [pos] is the position inside the formula; [bvl] is the list of
   % bound/bounded variable pairs. Returns a REPR structure.
   begin scalar rp,r,tm,tmp;
      if rl_tvalp atf then return repr_new(pos,atf,nil,nil);
      % Decomposing the atomic formula
      rp := repr_atfnew(atf,x,pos);
      r := repr_a rp;
      % Building the linear combination of all bound variables and the
      % representant right hand side term
      for each v in bvl do <<
	 tmp := pasf_coeflst(r,cdr v);
	 % Adding the next bounded variable to the linear combination Note:
	 % assuming bounded variables occur linearly in the formula
 	 if length tmp > 1 then
	    tm := addf(tm,multf(numr simp cdr v,caar tmp));
      	 % Substituting 0 for bounded variables to get the representants
	 r := numr subf(r,{(cdr v . nil)})
      >>;
      return repr_new(pos,repr_op rp,
	 reversip ((r . 0) . cdr reverse repr_cl rp),tm)
   end;

% FDEC represents a decomposition of a formula in PNF into the bound list,
% matrix and the position of the matrix in the formula.

procedure fdec_new(f,x);
   % Presburger arithmetic standard form FDEC constructor. [f] is a weak
   % quantifier free formula in PNF; [x] is an exception variable, that should
   % not apper in the bounds of [f], actually the quantified variable. Returns
   % an FDEC structure.
   begin scalar bvl,pos,btl;
      % Note: Using the fact the input formula is in PNF
      while rl_bquap rl_op f do <<
	 % Test of exception in bounds
	 if x memq rl_fvarl rl_b f then
	    rederr{"Quantified variable",x,
	       "is not allowed inside formula's bound"};
	 bvl := (rl_b f . rl_var f) . bvl;
	 pos := append(pos,{0});
	 btl := rl_op f . btl;
	 f := rl_mat f
      >>;
      return {f,pos,bvl,btl}
   end;

procedure fdec_mat(fdec);
   % Presburger arithmetic standard form bound accessor. [fdec] is a FDEC
   % structure of a weak quantifier free formula in PNF. Returns the matrix.
   car fdec;

procedure fdec_pos(fdec);
   % Presburger arithmetic standard form bound accessor. [fdec] is a FDEC
   % structure of a weak quantifier free formula in PNF. Returns the position
   % of the matrix.
   cadr fdec;

procedure fdec_bvl(fdec);
   % Presburger arithmetic standard form bound accessor. [fdec] is a FDEC
   % structure of a weak quantifier free formula in PNF. Returns the list of
   % pairs (bound . bounded variable).
   caddr fdec;

procedure fdec_bopl(fdec);
   % Presburger arithmetic standard form bound accessor. [fdec] is a FDEC
   % structure of a weak quantifier free formula in PNF. Returns the list of
   % quantifier types according to the bounded quantifiers.
   cadddr fdec;

% ELIMPT represents an elimination set part corresponding to a
% representant. The application of an ELIMPT results in a row of bounded
% quantifiers.

procedure elimpt_new(pos,guard,nom,den,bvl,unif);
   % Presburger arithmetic standard form ELIMPT constructor. [pos] is the
   % position of the representant in the formula; [guard] is the guard for the
   % testpoint; [nom] represents the nominator of the testpoint [nom]/[den];
   % [den] represents the denominator of the testpoint [nom]/[den]; [bvl] is a
   % list of bounds that will be attached by the application of the ELIMPT;
   % [unif] is a flag that is t iff the test point represents an element of
   % the cauchy bounds. Returns a new ELIMPT structure.
   {pos,guard,nom,den,bvl,unif};

procedure elimpt_pos(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the position.
   car elimpt;

procedure elimpt_cpos(elimpt1,elimpt2);
   % Presburger arithmetic standard form ELIMPT common position. [elimpt1] and
   % [elimpt2] are ELIMPT structures. Returns the common position of two
   % eliminatin points.
   begin scalar pos,p1,p2;
      p1 := car elimpt1;
      p2 := car elimpt2;
      while (p1 and p2 and car p1 eq car p2) do <<
	 pos := car p1 . pos;
	 p1 := cdr p1;
	 p2 := cdr p2
      >>;
      return reverse pos
   end;

procedure elimpt_guard(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the guard.
   cadr elimpt;

procedure elimpt_nom(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the nominator.
   caddr elimpt;

procedure elimpt_den(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the denominator.
   cadddr elimpt;

procedure elimpt_bvl(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the list of bounds.
   car cddddr elimpt;

procedure elimpt_unif(elimpt);
   % Presburger arithmetic standard form ELIMPT accessor. [elimpt] is an
   % ELIMPT structure. Returns the unif flag of bounds.
   cadr cddddr elimpt;

% ANSW represents a quantifier elimination answer. An answer contains a
% formula and a list of substitution values possibly bounded by some ranges
% given by bounds.

procedure answ_new(f,bl,tl);
   % Presburger arithmetic standard form ANSW constructor. [f] is a formula;
   % [bl] is a list of bounds; [tl] is a list of terms. Returns a new ANSW
   % structure.
   {f,bl,tl};

procedure answ_f(answ);
   % Presburger arithmetic standard form ANSW accessor. [answ] is an ANSW
   % structure. Returns the formula.
   car answ;

procedure answ_bl(answ);
   % Presburger arithmetic standard form ANSW accessor. [answ] is an ANSW
   % structure. Returns the list of bounds.
   cadr answ;

procedure answ_tl(answ);
   % Presburger arithmetic standard form ANSW accessor. [answ] is an ANSW
   % structure. Returns the list of terms.
   caddr answ;

procedure answ_backsubst(answ1,answ2);
   % Presburger arithmetic standard form ANSW backsubstitution. [answ1] is a
   % new answer; [answ2] is an old answ. Returns a list of terms, where
   % equations in [answ1] are substituted into [answ2] and the formula of the
   % old answer is replaced by the new one.
   begin scalar res,sub,var;
      if null answ2 and answ1 then
	 return answ1
      else if null answ1 then rederr{"incorrect ANSW structure"};
      sub := {(prepf pasf_arg2l caaddr answ1 .
	 prepsq pasf_arg2r caaddr answ1)};
      res := for each eqn in caddr answ2 collect
	 pasf_mk2('equal,pasf_arg2l eqn,subsq(pasf_arg2r eqn,sub));
      return {car answ1,append(cadr answ1,cadr answ2),
	 (caaddr answ1) . res}
   end;

% IV structure defines a simple representation of finite interval joints and
% provides some operations on that structure such as merge and cut. A
% procedure to map quantifier free formulas in one variable to IV's is also
% privided in this module above.

procedure iv_new(lb,rb);
   % Presburger arithmetic standard form interval datastructure
   % constructor. [lb] is the lower bound; [rb] is the upper bound. Returns a
   % new interval $[[lb],[rb]]$ (including the bounds).
   {((if lb then lb else 0) . (if rb then rb else 0))};

procedure iv_newcong(op,class);
   % Presburger arithmetic standard form interval datastructure congruence
   % constructor. [op] is the congruence operator; [class] is a representant
   % of the congruence class. Returns the (possibly non canonical)
   % datastructure representation for $[class] + modulo \mathbb{Z}$.
   {(op . if class then class else 0)};

procedure iv_congp(ivl);
   % Presburger arithmetic standard form interval datastructure new interval
   % congruence predicate. [ivl] is an interval list. Returns t iff [ivl]
   % contains a congruence.
   if ivl then pairp caar ivl or iv_congp cdr ivl;

procedure iv_empty(ivl);
   % Presburger arithmetic standard form interval datastructure empty
   % attribute. [ivl] is a an interval list. Returns t if the list is empty.
   not ivl;

procedure iv_congsplitl(ivl);
   % Presburger arithmetic standard form interval datastructure congruence
   % split of an interval list. [ivl] is an interval list.  Returns a pair
   % $(iv_1 . iv_2)$ where $iv_1$ is a list of intervals without congruences
   % and $iv_2$ are all the congruences.
   begin scalar split,rest;
      if ivl then return (nil . nil);
      % Splitting the first list
      split := iv_congsplit car ivl;
      rest := iv_congsplitl cdr ivl;
      return ((car split . car rest) . (cdr split . cdr rest))
   end;

procedure iv_congsplit(iv);
   % Presburger arithmetic standard form interval datastructure congruence
   % split. [iv] is an interval. Returns a pair $(iv_1 . iv_2)$ where $iv_1$
   % all intervals without congruences and $iv_2$ are all the congruences.
   if iv then
      if iv_congp({car iv}) then
	 (car iv_congsplit cdr iv . (car iv . cdr iv_congsplit cdr iv))
      else
	 ((car iv . car iv_congsplit cdr iv) . cdr iv_congsplit cdr iv)
   else
      (nil . nil);

procedure iv_cutn(ivl);
   % Presburger arithmetic standard form interval datastructure multiple
   % interval cut. [ivl] is a list of intervals. Returns interval $\bigcap_{iv
   % \in [ivl]} iv$.
   if cdr ivl then
      iv_cut(car ivl,iv_cutn cdr ivl)
   else
      car ivl;

procedure iv_cut(iv1,iv2);
   % Presburger arithmetic standard form interval datastructure cut. [iv1] is
   % a congruence-free interval; [iv2] is a congruence-free interval. Returns
   % interval $[iv1] \cap [iv2]$.
   begin scalar curr,lower,res;
      % If one of the intervals is empty returning nil
      if iv_empty iv1 or iv_empty iv2 then
	 return nil;
      % Until all lists are empty
      while not(iv_empty iv1 and iv_empty iv2) do <<
	 % Choosing the interval with the smallest lower bound. If one of
	 % those is empty then we take the lower bound from the lover one
	 if iv_empty iv2 or
	    (not iv_empty iv1 and pasf_leqp(caar iv1,caar iv2)) then <<
	       lower := car iv1;
	       iv1 := cdr iv1
	    >> else <<
	       lower := car iv2;
	       iv2 := cdr iv2
	    >>;
	 % Initialization of a new result interval
	 if null curr then
	    curr := lower
	 else
	    if pasf_leq(cdr curr,car lower) then
	       % The limit of the next smallest interval is bigger than the
	       % end of the current
	       curr := lower
	    else
	       if pasf_leqp(cdr curr,cdr lower) then <<
		  res := (car lower . cdr curr) . res;
		  curr := lower
	       >> else
		  res := lower . res
      >>;
      return reverse res
   end;

procedure iv_cutcongs(ivl,congs);
   % Presburger arithmetic standard form interval datastructure congruence
   % processing. [ivl] is a congruence free interval list; [congs] is a list
   % of congruences. Returns an interval list that represents $[congs] \cup
   % [ivl]$.
   begin scalar curr,res;
      if not congs then return ivl;
      while not iv_empty ivl do <<
	 for i := caar ivl : cdar ivl do <<
	    iv_cutcongs1(i,congs);
	    if iv_cutcongs1(i,congs) then
	       if curr then
		  curr := (car curr . i)
	       else
		  curr := (i . i)
	    else
	       if curr then <<
		  res := curr . res;
		  curr := nil
	       >>
	 >>;
	 % Joining the last interval limit
	 if null cdr ivl and curr then
	    res := (car curr . cdar ivl) . res;
	 ivl := cdr ivl
      >>;
      return reverse res
   end;

procedure iv_cutcongs1(val,congs);
   % Presburger arithmetic standard form interval datastructure congruence
   % processing. [val] is a value; [congs] is a list of congruences. Returns
   % t iff [val] satisfies all congruences.
   if congs then
      iv_cutcongs2(val,car congs) and iv_cutcongs1(val,cdr congs)
   else
      t;

procedure iv_cutcongs2(val,cong);
   % Presburger arithmetic standard form interval datastructure congruence
   % processing. [val] is a value; [cong] is a congruence. Returns t iff
   % [val] satisfies [cong].
   if caar cong eq 'cong then
      remainder(cdr cong - val,cdar cong) = 0
   else
      not (remainder(cdr cong - val,cdar cong) = 0);

procedure iv_mergen(ivl);
   % Presburger arithmetic standard form interval datastructure multiple
   % intervals merge. [ivl] is an interval list. Returns interval $\bigcup_{iv
   % \in [ivl]} iv$.
   if cdr ivl then
      iv_merge(car ivl,iv_mergen cdr ivl)
   else
      car ivl;

procedure iv_merge(iv1,iv2);
   % Presburger arithmetic standard form interval datastructure merge. [iv1]
   % is an interval; [iv2] is an interval. Returns interval $[iv1] \cup
   % [iv2]$.
   begin scalar curr,lower,res;
      % Test for congruences in the intervals
      if iv_congp iv1 or iv_congp iv2 then
	 rederr{"iv_merge : merging a congruence not possible }"};
      % Test for empty input lists
      if iv_empty iv1 and iv_empty iv2 then
	 return nil;
      % Until all lists are empty
      while not(iv_empty iv1 and iv_empty iv2) do <<
	 % Choosing the interval with the smallest lower bound.  If one of
	 % those is empty then we take the lower bound from the lover one
      	 if iv_empty iv2 or
	    (not iv_empty iv1 and pasf_leqp(caar iv1,caar iv2)) then <<
	    lower := car iv1;
	    iv1 := cdr iv1
	 >> else <<
	    lower := car iv2;
	    iv2 := cdr iv2
	 >>;
	 % Initialization of a new result interval
	 if not curr then
	    curr := lower
	 else
	    if pasf_leq(cdr curr,car lower) then <<
	       % The limit of the next smallest interval is bigger than the
	       % end of the current
	       res := curr . res;
	       curr := lower
	    >> else
	       if pasf_leqp(cdr curr,cdr lower) then
		  % A new limit must be set for the current interval
		  curr := (car curr . cdr lower)
      >>;
      return reverse (curr . res)
   end;

procedure pasf_rxffn(op);
   if op eq 'max then
      'cl_rxffn!-max
   else if op eq 'min then
      'cl_rxffn!-max
   else if op eq 'abs then
      'cl_rxffn!-abs
   else if op eq 'sign then
      'cl_rxffn!-sign
   else if op eq 'sqrt then
      'cl_rxffn!-sqrt
   else
      nil;

procedure pasf_stex(f);
   cl_apply2ats1(f,function pasf_stexat,{nil . nil});

procedure pasf_stexat(at,rndalpair);
   begin scalar al,lhs,w;
      al := car rndalpair;
      lhs := pasf_arg2l at;
      w := pasf_stexf(lhs,al);
      car rndalpair := cdr w;
      return pasf_0mk2(pasf_op at,car w)
   end;

procedure pasf_stexf(u,al);
   begin scalar w,c,r;
      if domainp u then
      	 return u . al;
      w := pasf_stexf(lc u,al);
      al := cdr w;
      c := car w;
      w := pasf_stexf(red u,al);
      al := cdr w;
      r := car w;
      w := pasf_stexk(mvar u,al);
      return addf(multf(c,exptf(car w,ldeg u)),r) . cdr w
   end;

procedure pasf_stexk(k,al);
   begin scalar w;
      if idp k then
	 return !*k2f k . al;
      % We now know that k is an rnd() kernel.
      w := atsoc(caddr k,al);
      if w then
	 return cdr w . al;
      if not domainp cadr k then
	 rederr {"pasf_stexk:",cadr k,"is not a number"};
      w := random(cadr k + 1);
      return w . ((caddr k . w) . al)
   end;

endmodule; % pasfmisc

end; % of file
