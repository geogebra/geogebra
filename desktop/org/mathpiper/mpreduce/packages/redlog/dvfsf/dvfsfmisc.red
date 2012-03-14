% ----------------------------------------------------------------------
% $Id: dvfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(dvfsf_misc_rcsid!* dvfsf_misc_copyright!*);
   dvfsf_misc_rcsid!* :=
      "$Id: dvfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   dvfsf_misc_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module dvfsfmisc;
% Discretely valued field standard form miscellaneous. Submodule of [dvfsf].

procedure dvfsf_ordatp(a1,a2);
   % Discretely valued field standard form ordering of atomic formulas
   % predicate. [a1] and [a2] are atomic formulas. Returns [T] iff
   % [a1] is less than or equal to [a2].
   begin scalar w1,w2;
      w1 := dvfsf_arg2l a1;
      w2 := dvfsf_arg2l a2;
      if w1 neq w2 then return ordp(w1,w2);
      w1 := dvfsf_arg2r a1;
      w2 := dvfsf_arg2r a2;
      if w1 neq w2 then return ordp(w1,w2);
      return dvfsf_ordrelp(dvfsf_op a1,dvfsf_op a2)
   end;

procedure dvfsf_ordrelp(r1,r2);
   % Discretely valued field standard form ordering of atomic formulas
   % predicate. [a1] and [a2] are relations. Returns [T] iff [r1] is
   % less than or equal to [r2].
   not not (r2 memq (r1 memq '(equal neq div sdiv assoc)));

procedure dvfsf_varlat(atf);
   % Discretely valued field standard form atomic formula list of
   % variables. [atf] is an atomic formula. Returns the variables
   % contained in [atf] as a list. The constant ['p] of our language
   % is not considered as an variable.
   delqip('p,union(kernels dvfsf_arg2l atf,kernels dvfsf_arg2r atf));

procedure dvfsf_varsubstat(atf,new,old);
   % Discretely valued field standard form substitute variable for
   % variable in atomic formula. [atf] is an atomic formula; [new] and
   % [old] are variables. Returns an atomic formula equivalent to
   % [atf] where [old] is substituted with [new].
   dvfsf_mk2(dvfsf_op atf,numr subf(dvfsf_arg2l atf,{old . new}),
      numr subf(dvfsf_arg2r atf,{old . new}));

procedure dvfsf_negateat(atf);
   % Discretely valued field standard form negate atomic formula.
   % [atf] is an atomic formula. Returns a quantifier-free positive
   % formula that is equivalent to $\lnot [atf]$.
   begin scalar op;
      op := dvfsf_op atf;
      if op eq 'equal then
	 return dvfsf_mkn('neq,dvfsf_argn atf);
      if op eq 'neq then
 	 return dvfsf_mkn('equal,dvfsf_argn atf);
      if op eq 'div then
 	 return dvfsf_mk2('sdiv,dvfsf_arg2r atf,dvfsf_arg2l atf);
      if op eq 'sdiv then
 	 return dvfsf_mk2('div,dvfsf_arg2r atf,dvfsf_arg2l atf);
      if op eq 'assoc then
 	 return dvfsf_mk2('nassoc,dvfsf_arg2l atf,dvfsf_arg2r atf);
      if op eq 'nassoc then
 	 return dvfsf_mk2('assoc,dvfsf_arg2l atf,dvfsf_arg2r atf);
   end;

procedure dvfsf_fctrat(at);
   % Discretely valued field standard form factorize atomic formula.
   % [at] is an atomic formula $l \mathrel{\varrho} r$. Returns a list
   % $(...,(f_i . d_i),...)$, where $f$ is an irreducible SF and $d$
   % is a positive integer. We have $l r=c \prod_i g_i^{d_i}$ for an
   % integer $c$.
   begin scalar w1,w2;
      w1 := cdr fctrf dvfsf_arg2l at;
      w2 := cdr fctrf dvfsf_arg2r at;
      return lto_almerge({w1,w2},'plus2)      
   end;

procedure dvfsf_v(z);
   % Discretely valued field standard form valuation function. [z] is
   % a non-zero integer. The fluid [dvfsf_p!*] must be fixed to a
   % positive prime integer $p$. Returns the $p$-adic value of [z].
   (if null cdr qrm then 1 + dvfsf_v car qrm else 0)
      where qrm=qremf(z,dvfsf_p!*);

procedure dvfsf_dnf(f);
   % Discretely valued field standard form conjunctive normal form.
   % [f] is a formula. Returns a DNF of [f].
   if !*rlbnfsac then
      (cl_dnf f) where !*rlsiso=T
   else
      cl_dnf f;

procedure dvfsf_cnf(f);
   % Discretely valued field standard form conjunctive normal form.
   % [f] is a formula. Returns a CNF of [f].
   if !*rlbnfsac then
      (cl_cnf f) where !*rlsiso=T
   else
      cl_cnf f;

procedure dvfsf_subsumption(l1,l2,gor);
   % Discretely valued field standard form subsume. [l1] and [l2] are
   % lists of atomic formulas. Returns one of [keep1], [keep2], [nil].
   if gor eq 'or then (
      if dvfsf_subsumep!-and(l1,l2) then
 	 'keep2
      else if dvfsf_subsumep!-and(l2,l1) then
	 'keep1
   ) else  % [gor eq 'and]
      if dvfsf_subsumep!-or(l1,l2) then
	 'keep1
      else if dvfsf_subsumep!-or(l2,l1) then
	 'keep2
      else
	 nil;

procedure dvfsf_subsumep!-and(l1,l2);
   % Discretely valued field standard form subsume [and] case. [l1]
   % and [l2] are lists of atomic formulas.
   begin scalar a;
      while l2 do <<
	 a := car l2;
	 l2 := cdr l2;
	 if cl_simpl(a,l1,-1) neq 'true then a := l2 := nil
      >>;
      return a
   end;

procedure dvfsf_subsumep!-or(l1,l2);
   % Discretely valued field standard form subsume [or] case. [l1] and
   % [l2] are lists of atomic formulas.
   begin scalar a;
      while l1 do <<
	 a := car l1;
	 l1 := cdr l1;
	 if cl_simpl(rl_smkn('or,l2),{a},-1) neq 'true then a := l1 := nil
      >>;
      return a
   end;

procedure dvfsf_a2cdl(atml);
   % Discretely valued field standard form atomic formula multiplicity
   % list to case distinction list. [atml] is a list of atomic
   % formulas with multiplicity. Returns a list, each containing a
   % list of complete case distinctions.
   begin scalar atf,termll,flag;
      while atml do <<
         atf := caar atml;
      	 atml := cdr atml;
      	 termll := dvfsf_argn atf . termll;
	 if not(dvfsf_op atf memq '(equal neq)) then flag := T
      >>;
      return if flag then
	 for each x in termll collect
	    {dvfsf_mk2('sdiv,car x,cadr x),
	       dvfsf_mk2('assoc,car x,cadr x),
	       dvfsf_mk2('sdiv,cadr x,car x)}
      else
	 for each x in termll collect
	    {dvfsf_0mk2('equal,x),dvfsf_0mk2('neq,x)}
   end;

procedure dvfsf_subat(al,f);
   % Discretely valued field standard form substitute in atomic
   % formula. [al] is ALIST for [subf()]; [f] is an atomic formula.
   % Returns an atomic formula.
   begin scalar nlhs,nrhs,w;
      nlhs := subf(dvfsf_arg2l f,al);
      nrhs := subf(dvfsf_arg2r f,al);
      if (not domainp denr nlhs) or  (not domainp denr nrhs) then
	 rederr "parametric denominator after substitution";
      w := lcm(denr nlhs,denr nrhs);
      return dvfsf_mk2(dvfsf_op f,
	 numr multsq(nlhs,!*f2q w),numr multsq(nrhs,!*f2q w))
   end;

procedure dvfsf_subalchk(al);
   % Discretely valued field standard form substitution alist check.
   % [al] is an ALIST for [subf()]. Return value undefined. Raises an
   % error if an illegal substituion is contained in [al].
   for each x in al do
      if not domainp denr simp cdr x then
	 rederr "parametric denominator in substituted term";

procedure dvfsf_eqnrhskernels(x);
   % Discretely valued field standard form equation right hand side
   % kernels. [x] is an equation. Returns a list of all kernels
   % contained in the right hand side of [x].
   nconc(kernels numr w,kernels denr w) where w=simp cdr x;

procedure dvfsf_structat(at,al);
   % Discretely valued field standard form structure of an atomic
   % formula. [at] is an atomic formula $([op],l,r)$; [al] is an
   % ALIST. Returns an atomic formula. [al] is of the form $(...,(f_i
   % . v_i),...)$ where $f_i$ is an SF and $v_i$ is a variable. Both
   % the left hand side of [at] and the right hand side of [at] occurs
   % as keys in [al]. Returns the atomic formula $[op](v_i,v_j)$,
   % provided $l=f_i$ and $r=f_j$.
   begin scalar lhs,rhs;
      lhs := dvfsf_arg2l at;
      rhs := dvfsf_arg2r at;
      if not(domainp lhs) then
	 lhs := numr simp cdr assoc(lhs,al);
      if not(domainp rhs) then
	 rhs := numr simp cdr assoc(rhs,al);
      return dvfsf_mk2(dvfsf_op at,lhs,rhs)
   end;

procedure dvfsf_ifstructat(at,al);
   % Discretely valued field standard form irreducible factor
   % structure of atomic formula. [at] is an atomic formula $(f\rho
   % g)$, [al] is an A-LIST of the form $(...,( f_i . v_i ),...)$.
   % Returns an atomic formula of the form $(z u_1 \cdots u_m \rho z'
   % w_1 \cdots w_m)$. Each $u_i$ and each $w_i$ occur as a value in
   % [al] with the keys $f'_i$ and $g'_i$, respectively, and we have
   % $f=z f'_1 \cdots f'_m$ and $g'=z' w_1 \cdots w_m$ for integers
   % $z$ and $z'$.
   begin scalar lhs,rhs,rl,rr;
      lhs := fctrf dvfsf_arg2l at;
      rhs := fctrf dvfsf_arg2r at;
      rl := car lhs;
      for each x in cdr lhs do
	 rl := multf(rl,expf(numr simp cdr assoc(car x,al),cdr x));
      rr := car rhs;
      for each x in cdr rhs do
	 rr := multf(rr,expf(numr simp cdr assoc(car x,al),cdr x));
      return dvfsf_mk2(dvfsf_op at,rl,rr)
   end;

procedure dvfsf_termmlat(at);
   % Discretely valued field standard form term multiplicity list of
   % atomic formula. [at] is an atomic formula. Returns the
   % multiplicity list off all non-zero terms in [at].
   begin scalar r,w;
      w := dvfsf_arg2l at;
      if w then
	 r := (w . 1) . r;
      w := dvfsf_arg2r at;
      if w then
	 r := (w . 1) . r;
      return r
   end;

procedure dvfsf_explats(f);
   % Discretely valued fields standard form explode z atomic formulas.
   % [f] is a formula. Returns a formula equivalent to [at]. Explodes
   % atomic formula contained in [f] in dependency of [!*rlsiexpl],
   % [!*rlsiexpla] and the operator of the respective boolean level.
   % Only atomic formulas of the form $z \mathrel{\varrho} 1$, where
   % $z$ is an integer and $\varrho$ is one of ['assoc], ['nassoc]
   % will be exploded.
   cl_simpl(cl_apply2ats2(f,'dvfsf_explodezat,nil,nil),nil,-1);

procedure dvfsf_explodezat(at,sop);
   % Discretely valued fields standard form explode z atomic formulas
   % for atomic formulas. [at] is an atomic formula; [sop] is a
   % boolean operator. Returns a formula equivalent to [at]. Explodes
   % [at] in dependency of [!*rlsiexpl], [!*rlsiexpla], and [sop].
   % Only atomic formulas of the form $z \mathrel{\varrho} 1$, where
   % $z$ is an integer and $\varrho$ is one of ['assoc], ['nassoc]
   % will be exploded.
   begin scalar op,lhs,rhs;
      lhs := dvfsf_arg2l at;
      rhs := dvfsf_arg2r at;
      if not(domainp lhs and rhs = 1) then
	 return at;
      op := dvfsf_op at;
      if not (op eq 'assoc or op eq 'nassoc) then
	 return at;
      if !*rlsiexpla then
      	 return dvfsf_explodezat1(op,lhs);
      if !*rlsiexpl then <<
	 if op eq 'assoc and (sop eq 'and or null sop) then
	    return dvfsf_explodezat1(op,lhs);
      	 if op eq 'nassoc and (sop eq 'or or null sop) then
	    return dvfsf_explodezat1(op,lhs);
      >>;
      return at
   end;

procedure dvfsf_explodezat1(op,lhs);
   % Discretely valued fields standard form explode z atomic formulas
   % subroutine. [op] id one of ['assoc], ['nassoc]; [lhs] is an
   % integer.
   rl_smkn(if op eq 'assoc then 'and else 'or,
      for each x in zfactor lhs collect dvfsf_mk2(op,car x,numr simp 1));

procedure dvfsf_mkcanonic(f);
   % Discretely valued fields standard form make canonic. [f] is an
   % variable free and quantifier free formula. Returns the unique and
   % canonic representation of [f].
   begin scalar facl,u,fu,xl,op,pr;
      if rl_tvalp f then
	 return f;
      facl := dvfsf_coeffacl(f);
      if null facl then
	 return cl_simpl(f,nil,-1);
      pr := nextprime lto_max facl;
      u := cl_simpl(dvfsf_subp(f,pr),nil,-1)
	 where dvfsf_p!*=pr;
      fu := cl_flip u;
      xl := for each fac in facl join
	 if ((cl_simpl(dvfsf_subp(f,fac),nil,-1))
	    where dvfsf_p!*=fac) eq fu
	 then
	    {fac};
      op := if u eq 'false then 'nassoc else 'assoc;
      xl := sort(xl,'lessp);
      return rl_smkn(if u eq 'false then 'or else 'and,
	 for each x in xl collect dvfsf_mk2(op,x,numr simp 1))
   end;

procedure dvfsf_coeffacl(f);
   % Discretely valued fields standard form coefficients factor list.
   % [f] is a formula. Returns the list of all ireducicble factors of
   % all integer coefficients.
   begin scalar cfml;
      cfml := cl_f2ml(f,'dvfsf_coeffaclat);
      return for each x in cfml collect car x
   end;

procedure dvfsf_coeffaclat(at);
   % Discretely valued fields standard form coefficients factor list
   % for atomic formulas. [f] is an atomic formula. Returns the list
   % of all ireducicble factors of all integer coefficients.
   lto_almerge({dvfsf_coeffaclf dvfsf_arg2l at,dvfsf_coeffaclf dvfsf_arg2r at},
      'plus2);

procedure dvfsf_coeffaclf(f);
   % Discretely valued fields standard form coefficients factor list
   % for standard forms. [f] is an SF. Returns the list of all
   % ireducicble factors of all integer coefficients.
   if null f then
      {}
   else if domainp f then
      dvfsf_coeffaclz f
   else if mvar f eq ' p then
      lto_almerge({dvfsf_coeffaclz lc f,dvfsf_coeffaclf red f},'plus2)
   else
      rederr "dvfsf_coeffaclf: unknown mvar";

procedure dvfsf_coeffaclz(z);
   % Discretely valued fields standard form coefficients factor list
   % for integers. [f] is an integer. Returns the list of all
   % positive ireducicble factors upto 1 and -1.
   if z=1 or z=-1 or z=0 then
      {}
   else if z < 0 then
      zfactor (-z)
   else
      zfactor z;

procedure dvfsf_subp(f,p);
   % Discretely valued fields standard form substitute for p. [f] is a
   % formula, [p] is a prime integer. Returns a formula in which all
   % occurences of ['p] are replaced by [p].
   cl_apply2ats1(f,'cl_subpat,{{'p . p}});

procedure cl_subpat(at,al);
   % Discretely valued fields standard form substitute for p for
   % atomic formulas. [at] is an atomic formula, al is an ALIST
   % describing the substitution of ['p] with an prime integer $p$.
   % Returns a formula in which all occurences of ['p] are replaced by
   % $p$.
   dvfsf_mk2(dvfsf_op at,
      numr subf(dvfsf_arg2l at,al),numr subf(dvfsf_arg2r at,al));

endmodule;  % [dvfsfmisc]

end;  % of file
