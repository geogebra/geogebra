% ----------------------------------------------------------------------
% $Id: dvfsf.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(dvfsf_rcsid!* dvfsf_copyright!*);
   dvfsf_rcsid!* := "$Id: dvfsf.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   dvfsf_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module dvfsf;
% Discretely valued field standard form. Main module. Algorithms on
% first-order formulas over the language of fields together with a
% constant [p] and binary relations [equal], [neq], [div], [sdiv],
% [assoc], and [nassoc]. The terms are SF's.

create!-package('(dvfsf dvfsfsiat dvfsfsism dvfsfqe dvfsfmisc),nil);

load!-package 'cl;
load!-package 'rltools;

exports dvfsf_enter,dvfsf_exit,dvfsf_simpterm,dvfsf_prepat,dvfsf_resimpat,
   dvfsf_lengthat,dvfsf_chsimpat,dvfsf_simpat,dvfsf_op,dvfsf_arg2l,dvfsf_arg2r,
   dvfsf_argn,dvfsf_mk2,dvfsf_0mk2,dvfsf_mkn,dvfsf_opp,dvfsf_simplat1,
   dvfsf_smupdknowl,dvfsf_smrmknowl,dvfsf_smcpknowl,dvfsf_smmkatl,
   dvfsf_susirmknowl,dvfsf_varsel,dvfsf_translat,dvfsf_elimset,dvfsf_qesubcq,
   dvfsf_qesubq,dvfsf_transform,dvfsf_trygauss,dvfsf_qemkans,
   dvfsf_ordatp,dvfsf_varlat,dvfsf_varsubstat,dvfsf_negateat,dvfsf_fctrat,
   dvfsf_dnf,dvfsf_cnf,dvfsf_subsumption,dvfsf_a2cdl,dvfsf_subat,dvfsf_subalchk,
   dvfsf_eqnrhskernels,dvfsf_structat,dvfsf_ifstructat,dvfsf_termmlat,
   dvfsf_explats,dvfsf_mkcanonic;

imports rltools,cl;

fluid '(!*rlverbose dvfsf_p!* !*rlsiexpl !*rlsiexpla !*rlsifac !*rlsusi);

flag('(dvfsf),'rl_package);

% Parameters
put('dvfsf,'rl_params,'(
   (rl_smupdknowl!* . dvfsf_smupdknowl)
   (rl_smrmknowl!* . dvfsf_smrmknowl)
   (rl_smcpknowl!* . dvfsf_smcpknowl)
   (rl_smmkatl!* . dvfsf_smmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_sacatlp!* . cl_sacatlp)
   (rl_sacat!* . cl_sacat)
   (rl_ordatp!* . dvfsf_ordatp)
   (rl_tordp!* . ordp)
   (rl_simplat1!* . dvfsf_simplat1)
   (rl_negateat!* . dvfsf_negateat)
   (rl_varlat!* . dvfsf_varlat)
   (rl_varsubstat!* . dvfsf_varsubstat)
   (rl_translat!* . dvfsf_translat)
   (rl_transform!* . dvfsf_transform)
   (rl_elimset!*. dvfsf_elimset)
   (rl_trygauss!* . dvfsf_trygauss)
   (rl_subsumption!* . dvfsf_subsumption)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_fctrat!* . dvfsf_fctrat)
   (rl_varsel!* . dvfsf_varsel)
   (rl_betterp!* . cl_betterp)
   (rl_a2cdl!* . dvfsf_a2cdl)
   (rl_qemkans!* . dvfsf_qemkans)
   (rl_termmlat!* . dvfsf_termmlat)
   (rl_structat!* . dvfsf_structat)
   (rl_ifstructat!* . dvfsf_ifstructat)
   (rl_subat!* . dvfsf_subat)
   (rl_subalchk!* . dvfsf_subalchk)
   (rl_eqnrhskernels!* . dvfsf_eqnrhskernels)
   (rl_susipost!* . dvfsf_susipost)
   (rl_susitf!* . dvfsf_susitf)
   (rl_susibin!* . dvfsf_susibin)
   (rl_specelim!* . cl_specelim)
   (rl_fbqe!* . cl_fbqe)));

% Switches
put('dvfsf,'rl_cswitches,'(
   (rlqeheu . nil)
   (rlqedfs . nil)
   (rlsusi . T)
));

% Services
put('dvfsf,'rl_services,'(
   (rl_subfof!* . cl_subfof)
   (rl_identifyonoff!* . cl_identifyonoff)
   (rl_simpl!* . cl_simpl)
   (rl_nnf!* . cl_nnf)
   (rl_nnfnot!* . cl_nnfnot)
   (rl_pnf!* . cl_pnf)
   (rl_cnf!* . dvfsf_cnf)
   (rl_dnf!* . dvfsf_dnf)
   (rl_all!* . cl_all)
   (rl_ex!* . cl_ex)
   (rl_atnum!* . cl_atnum)
   (rl_qnum!* . cl_qnum)
   (rl_ifacl!* . cl_ifacl)
   (rl_ifacml!* . cl_ifacml)
   (rl_matrix!* . cl_matrix)
   (rl_apnf!* . cl_apnf)
   (rl_atml!* . cl_atml)
   (rl_atl!* . cl_atl)
   (rl_qe!* . cl_qe)
   (rl_qeipo!* . cl_qeipo)
   (rl_qews!* . cl_qews)
   (rl_qea!* . cl_qea)
   (rl_tab!* . cl_tab)
   (rl_atab!* . cl_atab)
   (rl_termml!* . cl_termml)
   (rl_terml!* . cl_terml)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_struct!* . cl_struct)
   (rl_ifstruct!* . cl_ifstruct)
   (rl_explats!* . dvfsf_explats)
   (rl_mkcanonic!* . dvfsf_mkcanonic)
   (rl_itab!* . cl_itab)));

% Admin
put('dvfsf,'rl_enter,'dvfsf_enter);
put('dvfsf,'rl_exit,'dvfsf_exit);

put('dvfsf,'simpfnname,'dvfsf_simpfn);

put('dvfsf,'rl_prepat,'dvfsf_prepat);
put('dvfsf,'rl_resimpat,'dvfsf_resimpat);
put('dvfsf,'rl_lengthat,'dvfsf_lengthat);

put('dvfsf,'rl_prepterm,'prepf);
put('dvfsf,'rl_simpterm,'dvfsf_simpterm);

algebraic infix equal;
put('equal,'dvfsf_simpfn,'dvfsf_chsimpat);
put('equal,'number!-of!-args,2);

algebraic infix neq;
put('neq,'dvfsf_simpfn,'dvfsf_chsimpat);
put('neq,'number!-of!-args,2);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq);

algebraic infix sdiv;
put('sdiv,'dvfsf_simpfn,'dvfsf_chsimpat);
put('sdiv,'number!-of!-args,2);
put('sdiv,'rtypefn,'quotelog);
precedence sdiv,neq;
newtok '((| |) sdiv);

algebraic infix div;
put('div,'dvfsf_simpfn,'dvfsf_chsimpat);
put('div,'number!-of!-args,2);
put('div,'rtypefn,'quotelog);
precedence div,sdiv;
newtok '((|) div);

algebraic infix assoc;
put('assoc,'dvfsf_simpfn,'dvfsf_chsimpat);
put('assoc,'number!-of!-args,2);
put('assoc,'rtypefn,'quotelog);
precedence assoc,div;
newtok '((~) assoc);

algebraic infix nassoc;
put('nassoc,'dvfsf_simpfn,'dvfsf_chsimpat);
put('nassoc,'number!-of!-args,2);
put('nassoc,'rtypefn,'quotelog);
precedence nassoc,assoc;
newtok '((/ ~) nassoc);

flag('(equal neq sdiv div assoc nassoc),'spaced);

flag('(dvfsf_chsimpat),'full);

procedure dvfsf_enter(argl);
   % Discretely valued field enter context. [argl] is either [nil] or
   % it evaluates to a list containing an integer $n$ such that $n=0$
   % or $|n|$ prime. Returns a pair $(f . l)$; if $f$ is [nil], then
   % $l$ contains an error message; if $f$ is non-[nil], then $l$ is
   % the new value for the fluid [rl_argl!*]. Modifies the algebraic
   % variable [p] and the fluid [dvfsf_p!*]. The argument $n$
   % describes the range of considered $p$-adic valuations for
   % elimination and simplification. With $n=0$ these functions are
   % uniformly correct for all $p$-adic valuations; with $n<0$ they
   % are correct for all $p$-adic valuations with $p \leq |n|$. For
   % $n>0$ the $n$-adic valuation is selected, and both the algebraic
   % variable [p] and the fluid [dvfsf_p!*] are set to $n$; then
   % simplification and quantifier elimination are correct only for
   % this $n$-adic valuation.
   begin scalar n;
      n := if argl then reval car argl else 0;
      if argl and cdr argl then <<
	 lprim {"extra",ioto_cplu("argument",cddr argl),"ignored"};
	 argl := {car argl}
      >>;
      if not (n=0 or primep n) then
 	 return nil . "dvfsf extra argument must be 0 or prime";
      if n <= 0 then <<
	 lprim "p is being cleared";
      	 clear 'p;
      >> else <<
	 lprim {"p is set to",n};
      	 algebraic(p := n);
      >>;
      flag('(p),'reserved);
      dvfsf_p!* := n;
      return T . argl
   end;

procedure dvfsf_exit();
   % Discretely valued field exit context. No arguments. The return
   % value is unspecified. Modified the algebraic variable [p].
   <<
      remflag('(p),'share);
      remflag('(p),'reserved)
   >>;

procedure dvfsf_simpterm(u);
   % Discretely valued field simp term. [u] is Lisp Prefix. Returns
   % the [u] as a DVFSF term.
   numr simp u;

procedure dvfsf_prepat(f);
   % Discretely valued field prep atomic formula. [f] is a DVFSF
   % atomic formula. Returns [f] in Lisp prefix form.
   {dvfsf_op f,prepf dvfsf_arg2l f,prepf dvfsf_arg2r f};

procedure dvfsf_resimpat(f);
   % Discretely valued field resimp atomic formula. [f] is an DVFSF
   % atomic formula. Returns the atomic formula [f] with resimplified
   % terms.
   dvfsf_mk2(dvfsf_op f,
      numr resimp !*f2q dvfsf_arg2l f,numr resimp !*f2q dvfsf_arg2r f);

procedure dvfsf_lengthat(f);
   % Discretely valued field length of atomic formula. [f] is an
   % atomic formula. Returns a number, the length of [f].
   2;

procedure dvfsf_chsimpat(u);
   % Discretely valued field chain simp atomic formula. [u] is the
   % Lisp prefix representation of a chain of atomic formulas, i.e.,
   % the operators are nested right associatively. Returns a formula,
   % which is the corresponding conjunction.
   rl_smkn('and,for each x in dvfsf_chsimpat1 u collect dvfsf_simpat x);

procedure dvfsf_chsimpat1(u);
   % Discretely valued field chain simp atomic formula subroutine. [u]
   % is the Lisp prefix representation of a chain of atomic formulas,
   % i.e., the operators are nested right associatively.
   begin scalar leftl,rightl,lhs,rhs;
      lhs := cadr u;
      if pairp lhs and dvfsf_opp car lhs then <<
	 leftl := dvfsf_chsimpat1 lhs;
	 lhs := caddr lastcar leftl
      >>;
      rhs := caddr u;
      if pairp rhs and dvfsf_opp car rhs then <<
	 rightl := dvfsf_chsimpat1 rhs;
	 rhs := cadr car rightl
      >>;
      return nconc(leftl,{car u,lhs,rhs} . rightl)
   end;

procedure dvfsf_simpat(u);
   % Discretely valued field simp atomic formula. [u] is Lisp prefix.
   % Returns [u] as an atomic formula.
   begin scalar op,lhs,rhs,w;
      op := car u;
      lhs := simp cadr u;
      if not (numberp denr lhs) then
 	 typerr(u,"atomic formula");
      rhs := simp caddr u;
      if not (numberp denr rhs) then
 	 typerr(u,"atomic formula");
      if op memq '(equal neq) then
      	 return dvfsf_0mk2(op,numr subtrsq(lhs,rhs));
      w := lcm(denr lhs,denr rhs);
      return dvfsf_mk2(op,numr multsq(lhs,!*f2q w),numr multsq(rhs,!*f2q w))
   end;

procedure dvfsf_op(atf);
   % Discretely valued field operator. [atf] is an atomic formula
   % $R(t_1,t_2)$. Returns $R$.
   car atf;

procedure dvfsf_arg2l(atf);
   % Discretely valued field binary operator left hand side argument.
   % [atf] is an atomic formula $R(t_1,t_2)$. Returns $t_1$.
   cadr atf;

procedure dvfsf_arg2r(atf);
   % Discretely valued field binary operator right hand side argument.
   % [atf] is an atomic formula $R(t_1,t_2)$. Returns $t_2$.
   caddr atf;

procedure dvfsf_argn(atf);
   % Discretely valued field n-ary operator argument list. [atf] is an
   % atomic formula $R(t_1,...,t_n)$. Returns the list
   % $(t_1,...,t_n)$.
   {cadr atf,caddr atf};

procedure dvfsf_mk2(op,lhs,rhs);
   % Discretely valued field make atomic formula for binary operator.
   % [op] is one of the operators [equal], [neq], [div], [sdiv],
   % [assoc], and [nassoc]; [lhs] and [rhs] are terms. Returns the
   % atomic formula $[op]([lhs],[rhs])$.
   {op,lhs,rhs};

procedure dvfsf_0mk2(op,lhs);
   % Discretely valued field make zero right hand atomic formula for
   % binary operator. [op] is one of the operators [equal], [neq],
   % [div], [sdiv], [assoc], and [nassoc]; [lhs] is a term. Returns
   % the atomic formula $[op]([lhs],0)$.
   {op,lhs,nil};

procedure dvfsf_mkn(op,argl);
   % Discretely valued field make atomic formula for n-ary operator.
   % [op] is one of the operators [equal], [neq], [div], [sdiv],
   % [assoc], and [nassoc]; [argl] is a list $(t_1,t_2)$ of terms.
   % Returns the atomic formula $[op](t_1,t_2)$.
   {op,car argl,cadr argl};

procedure dvfsf_opp(op);
   % Discretely valued field operator predicate. [op] is an
   % S-expression. Returns non-[nil] iff op is a relation.
   op memq '(equal neq div sdiv assoc nassoc);

endmodule;  % [dvfsf]

end;  % of file
