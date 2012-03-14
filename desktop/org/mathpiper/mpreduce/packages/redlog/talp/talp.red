% ----------------------------------------------------------------------
% $Id: talp.red 1275 2011-08-16 14:47:01Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2004-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(talp_rcsid!* talp_copyright!*);
   talp_rcsid!* := "$Id: talp.red 1275 2011-08-16 14:47:01Z thomas-sturm $";
   talp_copyright!* := "Copyright (c) 2004-2009 A. Dolzmann and T. Sturm"
>>;

module talp;
% Term algebra lisp prefix. Main module. Algorithms on first-order
% formulas over a finite language with at least one constant [c], at
% least one function symbol of positive arity and binary relations
% [equal], [neq]. The terms are represented in lisp prefix.

create!-package('(talp talpsiat talpmisc talpbnf talpsism talpqe),nil);

loadtime load!-package 'cl;
loadtime load!-package 'rltools;

exports talp_enter,talp_exit,talp_mkalop,talp_unmkalop,talp_get!-invs,
   talp_mkinvop,talp_getl,talp_getextl,talp_prepat,talp_lengthat,
   talp_simpterm,talp_resimpterm,talp_prepterm,talp_simpat,talp_resimpat,
   talp_op,talp_fop,talp_arg2l,talp_arg2r,talp_argl,talp_invp,talp_invf,
   talp_invn,talp_invarg,talp_mkinv,talp_mk2,talp_mkn,talp_mktn,talp_fargl;

imports cl,rltools;

global '(talp_lang!* talp_extlang!*);

fluid '(!*rlsusi);

flag('(talp),'rl_package);

switch talpqegauss;
on1 'talpqegauss;

switch talpqp;
on1 'talpqp;

% Parameters
put('talp,'rl_params,'(
   (rl_smupdknowl!* . talp_smwupdknowl)
   (rl_smrmknowl!* . talp_smwrmknowl)
   (rl_smcpknowl!* . talp_smwcpknowl)
   (rl_smmkatl!* . talp_smwmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_susibin!* . talp_susibin)
   (rl_susipost!* . talp_susipost)
   (rl_susitf!* . talp_susitf)
   (rl_a2cdl!* . talp_a2cdl)
   (rl_tordp!* . talp_tordp)
   (rl_subsumption!* . talp_subsumption)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_sacat!* . cl_sacat)
   (rl_sacatlp!* . cl_sacatlp)
   (rl_negateat!* . talp_negateat)
   (rl_termmlat!* . talp_termmlat)
   (rl_subat!* . talp_subat)
   (rl_subalchk!* . talp_subalchk)
   (rl_eqnrhskernels!* . talp_eqnrhskernels)
   (rl_simplat1!* . talp_simplat1)
   (rl_varlat!* . talp_varlat)
   (rl_varsubstat!* . talp_varsubstat)
   (rl_ordatp!* . talp_ordatp)
   (rl_op!* . talp_op) ));

% Switches
put('talp,'rl_cswitches, '(
   (rlverbose . t)
   (rlsism . t) ));

% Services
put('talp,'rl_services,'(
   (rl_tab!* . talp_tab)
   (rl_atab!* . talp_atab)
   (rl_itab!* . talp_itab)
   (rl_identifyonoff!* . cl_identifyonoff)
   (rl_subfof!* . cl_subfof)
   (rl_ex!* . cl_ex)
   (rl_all!* . cl_all)
   (rl_atnum!* . talp_atnum)
   (rl_atl!* . cl_atl)
   (rl_atml!* . cl_atml)
   (rl_terml!* . cl_terml)
   (rl_termml!* . cl_termml)
   (rl_cnf!* . cl_cnf)
   (rl_dnf!* . cl_dnf)
   (rl_pnf!* . cl_pnf)
   (rl_apnf!* . cl_apnf)
   (rl_nnf!* . cl_nnf)
   (rl_rnf!* . talp_rnf)
   (rl_nnfnot!* . cl_nnfnot)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_matrix!* . cl_matrix)
   (rl_qe!* . talp_qe)
   (rl_qea!* . talp_qea)
   (rl_simpl!* . cl_simpl) ));

% Admin
put('talp,'rl_enter,'talp_enter);
put('talp,'rl_exit,'talp_exit);
put('talp,'simpfnname,'talp_simpfn);
put('talp,'rl_prepat,'talp_prepat);
put('talp,'rl_resimpat,'talp_resimpat);
put('talp,'rl_lengthat,'talp_lengthat);
put('talp,'rl_prepterm,'talp_prepterm);
put('talp,'rl_simpterm,'talp_simpterm);

algebraic infix equal;
put('equal,'number!-of!-args,2);
put('equal,'talp_simpfn,'talp_simpat);

algebraic infix neq;
put('neq,'number!-of!-args,2);
put('neq,'talp_simpfn,'talp_simpat);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq);

flag('(equal neq),'spaced);
flag('(talp_simpat), 'full);


procedure talp_enter(argl);
   % Term algebra lisp prefix enter context. [argl] is a list
   % containing lists representing language elements. Returns a pair
   % $(f . l)$. If $f$ is nil then $l$ contains an error message, else
   % $l$ is the new value of [rl_argl!*].
   <<
      talp_lang!* := for each x in argl collect cadr x . caddr x;
      talp_extlang!* := for each x in argl join
	 if caddr x > 0 then
	    (cadr x . caddr x) . talp_get!-invs(cdr x)
	 else {cadr x . caddr x};
      for each x in talp_extlang!* do
	 if cdr x > 0 then talp_mkalop x;
      T . argl
   >>;

procedure talp_exit();
   % Term algebra lisp prefix exit context. No arguments.
   <<
      for each x in talp_getextl() do
	 if cdr x neq 0 then talp_unmkalop car x;
      talp_lang!* := nil;
      talp_extlang!* := nil;
   >>;

procedure talp_mkalop(f);
   % Term algebra lisp prefix make algebraic operator. [f] is a dotted
   % pair of the form $(op arity)$. Returns an identifier.
   <<
      f := if pairp car f then talp_mkinvop(cadar f,caddar f) else car f;
      algebraic operator f;
      f
   >>;

procedure talp_unmkalop(f);
   % Term algebra lisp prefix unmake algebraic operator. [f] is an
   % identifier. Return value unspecified.
   algebraic clear f;

procedure talp_get!-invs(f);
   % Term algebra Lisp prefix get inverses. [f] is a list encoding a
   % function. Returns the list of inverse-functions corresponding to
   % [f].
   for i:=1 : cadr f collect talp_mkinvop(car f,i) . 1;

procedure talp_mkinvop(f,i);
   % Term algebra Lisp prefix make inverse operator. [f] is an
   % operator. [i] is an integer. Returns an operator composed of
   % given parts starting with INV_.
   intern compress nconc(explode compress nconc(explode 'inv_,{f}),explode i);

procedure talp_getl();
   % Term algebra Lisp prefix get language.
   talp_lang!*;

procedure talp_getextl();
   % Term algebra Lisp prefix get extended language.
   talp_extlang!*;

procedure talp_prepat(atf);
   % Term algebra Lisp prefix prep atomic formula. [atf] is an atomic
   % formula.  Returns [atf] in Lisp prefix form.
   if atf then
      {talp_op atf,talp_prepterm talp_arg2l atf, talp_prepterm talp_arg2r atf}
   else atf;

procedure talp_lengthat(atf);
   % Term algebra Lisp prefix length of atomic formula. [atf] is an
   % atomic formula. Returns length of [atf].
   length talp_argl atf;

procedure talp_simpterm(term);
   % Term algebra Lisp prefix simplify term. [term] is a Lisp prefix
   % term.  Returns simplified [term].
   begin scalar arity,obj;
      if atom term then return term;
      obj := atsoc(talp_op term,talp_getextl());
      if null obj then rederr {talp_op term, "not declared as operator"};
      arity := cdr obj;
      if length (talp_fargl term) neq arity then
	 rederr {talp_op term, "  defined as  ", arity, "- ary"}
      else obj := for each arg in talp_fargl term collect talp_simpterm arg;
      return talp_mkn(talp_op term,obj)
   end;

procedure talp_resimpterm(term);
   % Term algebra Lisp prefix resimplify term. [term] is a Lisp prefix
   % term.  Returns resimplified [term].
   talp_simpterm term;

procedure talp_prepterm(term);
   % Term algebra Lisp prefix prep term. [term] is a Lisp prefix term.
   % Returns Lisp prefix term.
   talp_simpterm term;

procedure talp_simpat(atf);
   % Term algebra Lisp prefix simplify atomic formula. [atf] is an
   % atomic formula. Returns an atomic formula.
   begin scalar lhs,rhs;
      if talp_tordp(talp_arg2l atf,talp_arg2r atf) then <<
	 rhs := talp_arg2l atf;
	 lhs := talp_arg2r atf
      >> else <<
	 rhs := talp_arg2r atf;
	 lhs := talp_arg2l atf
      >>;
      return talp_mk2(talp_op atf,lhs,rhs)
   end;

procedure talp_resimpat(atf);
   % Term algebra Lisp prefix simplify atomic formula. [atf] is an
   % atomic formula. Returns atomic formula with resimplified terms.
   talp_mk2(talp_op atf,
      talp_resimpterm talp_arg2l atf,talp_resimpterm talp_arg2r atf);

procedure talp_opp(op);
   % Term algebra Lisp prefix operator predicate. [op] is an
   % S-expression. Returns [nil] if op is not a relation.
   op memq '(equal neq);

procedure talp_op(at);
   % Term algebra Lisp prefix operator. [at] is an atomic formula
   % $R(lhs,rhs)$. Returns $R$.
   car at;

procedure talp_fop(term);
   % Term algebra Lisp prefix function operator. [term] is a term $(F
   % args)$. Returns $F$.
   car term;

procedure talp_arg2l(at);
   % Term algebra Lisp prefix argument binary operator left hand
   % side. [at] is an atomic formula $R(lhs,rhs)$. Returns $lhs$.
   cadr at;

procedure talp_arg2r(at);
   % Term algebra Lisp prefix argument binary operator right hand
   % side. [at] is an atomic formula $R(lhs,rhs)$. Returns $rhs$.
   caddr at;

procedure talp_argl(f);
   % Term algebra Lisp prefix argument list. [f] is a formula.
   % Returns the list of arguments of [f].
   cdr f;

procedure talp_invp(term);
   % Term algeba Lisp prefix ['inv]-term predicate. [term] is a
   % term. Returns t if [term] is a term $inv_{fs,no}(arg)$.
   pairp term and not atsoc(talp_op term,talp_getl())
      and atsoc(talp_op term,talp_getextl());

procedure talp_invf(term);
   % Term algebra Lisp prefix ['inv]-term's function symbol. [term] is
   % a term $inv_{fs,no}(arg)$. Returns the corresponding function
   % symbol $fs$.
   car cddddr explode2 talp_op term;

procedure talp_invn(term);
   % Term algebra Lisp prefix ['inv]-term's number. [term] is a term
   % $inv_{fs,no}(arg)$. Returns the corresponding number $no$.
   compress {cadr cddddr explode2 talp_op term};

procedure talp_invarg(term);
   % Term algebra Lisp prefix ['inv]-term's argument. [term] is a term
   % $inv_{fs,no}(arg)$. Returns the argument $arg$.
   cadr term;

procedure talp_mkinv(inv,term);
   % Term algebra Lisp prefix make ['inv]-term. [inv] is $inv_fi$,
   % [term] is a term. Returns the term $inv_fi (term)$.
   inv . {term};

procedure talp_mk2(op,lhs,rhs);
   % Term algebra Lisp prefix make atomic formula for binary
   % operator. [op] is ['equal] or ['neq], [lhs] and [rhs] are
   % terms. Returns the atomic formula $[op]([lhs],[rhs])$.
   {op,lhs,rhs};

procedure talp_mkn(op,argl);
   % Term algebra Lisp prefix make atomic formula for n-ary operator.
   % [op] is ['equal] or ['neq], [argl] is a list $(lhs,rhs)$ of
   % terms.  Returns the atomic formula $[op](lhs,rhs)$.
   op . argl;

procedure talp_mktn(op,argl);
   % Term algebra Lisp prefix make term for n-ary operator.  [op] is
   % an identifier, [argl] is a list of terms.  Returns the term
   % $([op] [argl])$.
   op . argl;

procedure talp_fargl(term);
   % Term algebra Lisp prefix function's argument list. [term] is
   % $f(argl)$. Returns $argl$.
   cdr term;

endmodule;  % [talp]

end;  % of file
