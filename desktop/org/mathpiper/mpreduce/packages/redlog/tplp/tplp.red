% ----------------------------------------------------------------------
% $Id: tplp.red 1275 2011-08-16 14:47:01Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 Thomas Sturm
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
   fluid '(tplp_rcsid!* tplp_copyright!*);
   tplp_rcsid!* := "$Id: tplp.red 1275 2011-08-16 14:47:01Z thomas-sturm $";
   tplp_copyright!* := "Copyright (c) 2007-2009 T. Sturm"
>>;

module tplp;
% Theorem proving lisp prefix. Main module. Algorithms on first-order
% formulas over a finite language. The terms are represented in lisp
% prefix.

create!-package('(tplp tplpkapur),nil);

load!-package 'redlog;  % for rl_texmacsp()
loadtime load!-package 'cl;
loadtime load!-package 'rltools;

imports rltools,cl;

global '(tplp_fsyml!* tplp_rsyml!*);

flag('(tplp),'rl_package);

% Parameters
put('tplp,'rl_params,'(
   (rl_op!* . tplp_op)));

% Services
put('tplp,'rl_services,'(
   (rl_kapur!* . tplp_kapur)
   (rl_miniscope!* . tplp_miniscope)
   (rl_skolemize!* . tplp_skolemize)
   (rl_subfof!* . cl_subfof)
   (rl_ex!* . cl_ex)
   (rl_all!* . cl_all)
   (rl_atnum!* . tplp_atnum)
   (rl_qnum!* . cl_qnum)
   (rl_atl!* . cl_atl)
   (rl_atml!* . cl_atml)
   (rl_terml!* . cl_terml)
   (rl_termml!* . cl_termml)
   (rl_cnf!* . tplp_cnf)
   (rl_dnf!* . tplp_dnf)
   (rl_pnf!* . cl_pnf)
   (rl_apnf!* . cl_apnf)
   (rl_nnf!* . cl_nnf)
   (rl_nnfnot!* . cl_nnfnot)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_sacat!* . cl_sacat)
   (rl_sacatlp!* . cl_sacatlp)
   (rl_negateat!* . tplp_negateat)
   (rl_simpl!* . cl_simpl)
   (rl_smupdknowl!* . cl_smupdknowl)
   (rl_smrmknowl!* . cl_smrmknowl)
   (rl_smcpknowl!* . cl_smcpknowl)
   (rl_smmkatl!* . cl_smmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_simplat1!* . tplp_simplat1)
   (rl_ordatp!* . ordop)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_subat!* . tplp_subat)
   (rl_eqnrhskernels!* . tplp_eqnrhskernels)
   (rl_subalchk!* . tplp_subalchk)
   (rl_varlat!* . tplp_varlat)
   (rl_matrix!* . cl_matrix)));

% Admin
put('tplp,'rl_enter,'tplp_enter);
put('tplp,'rl_exit,'tplp_exit);
put('tplp,'simpfnname,'tplp_simpfn);
put('tplp,'rl_prepat,'tplp_prepat);
put('tplp,'rl_resimpat,'tplp_resimpat);
put('tplp,'rl_lengthat,'tplp_lengthat);
put('tplp,'rl_prepterm,'tplp_prepterm);
put('tplp,'rl_simpterm,'tplp_simpterm);

algebraic infix equal;
put('equal,'number!-of!-args,2);
put('equal,'tplp_simpfn,'tplp_simpat);

algebraic infix neq;
put('neq,'number!-of!-args,2);
put('neq,'tplp_simpfn,'tplp_simpat);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq);

flag('(equal neq),'spaced);
flag('(tplp_simpat),'full);

procedure tplp_enter(argl);
   % Theorem proving lisp prefix enter context. [argl] is a list
   % containing lists representing language elements. Returns a pair
   % $(f . l)$. If $f$ is nil then $l$ contains an error message, else
   % $l$ is the new value of [rl_argl!*].
   begin scalar op;
      if not eqn(length argl,2) then
 	 return nil . "wrong number of arguments";
      tplp_fsyml!* := for each x in cdar argl collect <<
	 op := cadr x;
	 if not idp op then
	    typerr(op,"function symbol");
	 if eqn(caddr x,0) then <<
	    lprim {op,"is being reserved"};
	    flag ({op},'reserved)
	 >> else
 	    tplp_mkalop cdr x;
	 cadr x . caddr x
      >>;
      tplp_rsyml!* := for each x in cdadr argl collect <<
	 tplp_mkalop cdr x;
	 tplp_mkpredicate cdr x;
      	 cadr x . caddr x
      >>;
      return T . argl
   end;

procedure tplp_exit();
   % Theorem proving lisp prefix exit context.
   <<
      for each x in tplp_fsyml!* do
	 if eqn(cdr x,0) then
	    remflag({car x},'reserved)
	 else
	    tplp_unmkalop car x;
      for each x in tplp_rsyml!* do <<
	 tplp_unmkalop car x;
	 tplp_unmkpredicate car x
      >>;
      tplp_rsyml!* := nil;
      tplp_fsyml!* := nil;
      nil
   >>;

procedure tplp_mkalop(f);
   % Theorem proving lisp prefix make algebraic operator. [f] is a
   % dotted pair of the form $(op . arity)$.
   (algebraic operator op) where op=car f;

procedure tplp_unmkalop(f);
   % Theorem proving lisp prefix unmake algebraic operator. [f] is an
   % identifier.
   algebraic clear f;

procedure tplp_mkpredicate(r);
   % Theorem proving lisp prefix make predicate. [r] is a
   % dotted pair of the form $(op . arity)$.
   put(car r,'tplp_simpfn,'tplp_simpat);

procedure tplp_unmkpredicate(r);
   % Theorem proving lisp prefix unmake predicate. [r] is an
   % identifier.
   remprop(r,'tplp_simpfn);

procedure tplp_fsyml();
   % Theorem proving Lisp prefix get language.
   tplp_fsyml!*;

procedure tplp_rsyml();
   % Theorem proving Lisp prefix get language.
   tplp_rsyml!*;

procedure tplp_prepat(atf);
   % Theorem proving Lisp prefix prep atomic formula. [atf] is an atomic
   % formula.  Returns [atf] in Lisp prefix form.
   atf;

procedure tplp_lengthat(atf);
   % Theorem proving Lisp prefix length of atomic formula. [atf] is an
   % atomic formula. Returns length of [atf].
   length cdr atf;

procedure tplp_simpterm(term);
   % Theorem proving Lisp prefix simplify term. [term] is a Lisp
   % prefix term. Returns context-specific representation of [term],
   % which is Lisp prefix here. Apart from syntax-checking, reval
   % would work here. We have to take care of rebound atoms.
   begin scalar w; integer arity;
      if atom term then
         return reval term;
      w := atsoc(car term,tplp_fsyml());
      if null w then
	 rederr {car term,"not declared as function symbol"};
      arity := cdr w;
      if not eqn(length cdr term,arity) then
	 rederr {car term, "requires", arity,"arguments"};
      return car term . for each arg in cdr term collect
 	 tplp_simpterm arg
   end;

procedure tplp_resimpterm(term);
   % Theorem proving Lisp prefix resimplify term. [term] is a term.
   % Returns resimplified [term]. We try to be somewhat more efficient
   % than reval.
   if atom term then
      reval term
   else
      car term . for each x in cdr term collect tplp_resimpterm x;

procedure tplp_prepterm(term);
   % Theorem proving Lisp prefix prep term. [term] is a term. Returns
   % the Lisp prefix representaion of term.
   term;

procedure tplp_simpat(atf);
   % Theorem proving Lisp prefix simplify atomic formula. [atf] is
   % Lisp prefix. Returns an atomic formula.
   begin scalar op;
      op := car atf;
      if not (op and atom op) then
 	 typerr (op,"predicate symbol");
      return op . for each x in cdr atf collect tplp_simpterm x
   end;

procedure tplp_resimpat(atf);
   % Theorem proving Lisp prefix simplify atomic formula. [atf] is an
   % atomic formula. Returns atomic formula with resimplified terms.
   car atf . for each x in cdr atf collect tplp_resimpterm x;

procedure tplp_opp(op);
   % Theorem proving Lisp prefix operator predicate. [op] is an atom.
   % Returns non-[nil] if op is a relation.
   atsoc(op,tplp_rsyml());

procedure tplp_op(at);
   % Theorem proving Lisp prefix operator. [at] is an atomic formula.
   % Returns the relation symbol of [at].
   car at;

procedure tplp_arg2l(at);
   % Theorem proving Lisp prefix argument binary operator left hand
   % side. [at] is an atomic formula $R(lhs,rhs)$. Returns $lhs$.
   cadr at;

procedure tplp_arg2r(at);
   % Theorem proving Lisp prefix argument binary operator right hand
   % side. [at] is an atomic formula $R(lhs,rhs)$. Returns $rhs$.
   caddr at;

procedure tplp_argl(f);
   % Theorem proving Lisp prefix argument list. [f] is a formula.
   % Returns the list of arguments of [f].
   cdr f;

procedure tplp_mk2(op,lhs,rhs);
   % Theorem proving Lisp prefix make atomic formula for binary
   % operator. [op] is ['equal] or ['neq], [lhs] and [rhs] are
   % terms. Returns the atomic formula $[op]([lhs],[rhs])$.
   {op,lhs,rhs};

procedure tplp_mkn(op,argl);
   % Theorem proving Lisp prefix make atomic formula for n-ary operator.
   % [op] is ['equal], ['neq] or a predicate symbol, [argl] is a list of
   % terms.  Returns the atomic formula $[op]([argl])$.
   op . argl;

procedure tplp_fop(term);
   % Theorem proving Lisp prefix function operator. [term] is a term $(F
   % args)$. Returns $F$.
   car term;

procedure tplp_fmkn(op,argl);
   % Theorem proving Lisp prefix function make for n-ary operator.
   % [op] is an identifier, [argl] is a list of terms.
   op . argl;

procedure tplp_fargl(term);
   % Theorem proving Lisp prefix function's argument list. [term] is a
   % term. Return the list of argument terms.
   cdr term;

procedure tplp_varlat(atf);
   % Variable list atomic formula. [atf] is an atomic formula. Returns a
   % list of identifiers. The set of variables ocurring in [atf].
   begin scalar l;
      for each x in tplp_argl atf do
         if idp x and null tplp_funcp x then
            l := lto_insertq(x,l)
         else if pairp x and tplp_funcp tplp_fop x then
            l := union(l,tplp_varlterm x);
      return l
   end;

procedure tplp_funcp(op);
   % Theorem proving Lisp prefix function predicate. [op] is an identifier.
   % Returns non-[nil] if op is a function.
   atsoc(op,tplp_fsyml());

procedure tplp_varlterm(term);
   % Variable list term. [term] is a term. Returns a
   % list of identifiers. The set of variables ocurring in [term].
   begin scalar l;
      if idp term then
         return if null tplp_funcp term then {term};
      for each x in tplp_fargl term do
         if idp x and null tplp_funcp x then
            l := lto_insertq(x,l)
         else if pairp x then
            l := union(l,tplp_varlterm x);
      return l
   end;

procedure tplp_subat(al,atf);
   % Substitute in atomic formula. [al] is an
   % alist, [atf] is an atomic formula. Returns an atomic formula.
   tplp_mkn(tplp_op atf,for each x in tplp_argl atf collect tplp_subt(al,x));

procedure tplp_subt(al,u);
   % Substitute in term. [al] is an alist, [u] is a term. Returns a term.
   begin scalar w;
      if idp u and (w := atsoc(u,al)) then
      	 return tplp_clonestruct cdr w;
      if atom u then
      	 return u;
      return tplp_fmkn(tplp_fop u,for each arg in tplp_fargl u collect
         tplp_subt(al,arg))
   end;

procedure tplp_clonestruct(s);
   % Clone structure. [s] is any. Returns any, which is a clone of [s] in a
   % constructive way.
   if atom s then s else (tplp_clonestruct car s) . (tplp_clonestruct cdr s);

procedure tplp_eqnrhskernels(x);
   % Equation right hand side
   % kernels. [x] is an equation. Returns a list of all kernels
   % contained in the right hand side of [x].
   tplp_varlterm cdr x;

procedure tplp_subalchk(al);
   % Substitution alist check.
   ;

procedure tplp_atnum(f);
   % Atomic formula number. [f] is a
   % formula. Returns the number of atomic formulas in [f].
   begin scalar op;
      op := rl_op f;
      if rl_boolp op then
 	 return for each subf in rl_argn f sum tplp_atnum subf;
      if rl_quap op then
    	 return tplp_atnum rl_mat f;
      if rl_tvalp op then return 0;
      % [f] is an atomic formula.
      return 1
   end;

procedure tplp_negateat(atf);
   % Negate atomic formula. [atf] is an atomic formula. Returns the negation
   % of [atf].
   if tplp_op atf eq 'negp then tplp_argl atf else tplp_mkn('negp,atf);

procedure tplp_cnf(f);
   % Conjunctive normalform. [f] is a formula. Returns a formula.
   tplp_removenegp cl_cnf f;

procedure tplp_dnf(f);
   % Disjunctive normalform. [f] is a formula. Returns a formula.
   tplp_removenegp cl_dnf f;

procedure tplp_removenegp(f);
  % Remove help-predicate negp. [f] is a formula. Returns a formula where each
  % negated predicate is replaced by 'not . p.
  if rl_tvalp rl_op f then
     f
  else if rl_boolp rl_op f then
     rl_mkn(rl_op f,for each x in rl_argn f collect tplp_removenegp x)
  else if rl_quap rl_op f then
     rl_mkq(rl_op f,rl_var f,tplp_removenegp rl_mat f)
  else if tplp_op f eq 'negp then
     rl_mk1('not,tplp_argl f)
  else
     f;

procedure tplp_simplat1(at,sop);
   % Simplify atomic formula. (no simplification)
   at;

procedure tplp_constp(term);
   % Constant predicate. [term] is a term. Returns non-nil if [term]
   % is a constant term.
   (idp term and tplp_funcp term) or (pairp term and null tplp_fargl term);

procedure tplp_cons2func(f);
   % Constant to function. [f] is a formula. Returns a formula where
   % every constant is represented as a 0-ary function.
   if rl_tvalp f then
      f
   else if rl_boolp rl_op f then
     rl_mkn(rl_op f,for each x in rl_argn f collect tplp_cons2func x)
   else if rl_quap rl_op f then
      rl_mkq(rl_op f,rl_var f,tplp_cons2func rl_mat f)
   else if tplp_op f eq 'negp then
      rl_mk1('not,tplp_mkn(tplp_op tplp_argl f,for each x in tplp_argl
         tplp_argl f collect tplp_fcons2func x))
   else
      tplp_mkn(tplp_op f,for each x in tplp_argl f collect tplp_fcons2func x);

procedure tplp_fcons2func(term);
   % Term Constant to function. [term] is a term. Returns a term where
   % every constant is represented as a 0-ary function.
   if idp term and tplp_funcp term then
      tplp_fmkn(term,nil)
   else if idp term then
      term
   else
      tplp_fmkn(tplp_fop term,for each x in tplp_fargl term collect
         tplp_fcons2func x);

endmodule;  % [tplp]

end;  % of file
