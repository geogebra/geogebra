% ----------------------------------------------------------------------
% $Id: pasf.red 1850 2012-11-20 14:37:43Z mkosta $
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
   fluid '(pasf_rcsid!* pasf_copyright!*);
   pasf_rcsid!* := "$Id: pasf.red 1850 2012-11-20 14:37:43Z mkosta $";
   pasf_copyright!* :=
      "(c) 2002-2009 A. Dolzmann, A. Seidl, T. Sturm, 2010 T. Sturm"
>>;

module pasf;
% Presburger arithmetic standard form main module. Algorithms on first-order
% formulas over the language of rings together with congruences. Binary
% relations (operators) are [equal], [neq], [leq], [geq], [lessp],
% [greaterp]. Ternary relations are [cong] and [ncong].

create!-package('(pasf pasfbnf pasfmisc pasfnf pasfsiat
   pasfqe pasfsism pasfopt),nil);

fluid '(!*rlnzden !*rlposden !*rladdcond !*rlqeasri !*rlsusi !*rlsifac !*utf8);

load!-package 'redlog;
loadtime load!-package 'cl;
loadtime load!-package 'rltools;

imports rltools,cl;

fluid '(!*rlverbose secondvalue!*);
flag('(pasf),'rl_package);
flag('(pasf_chsimpat),'full);
flag('(pasf_simpat),'full);
flag('(equal neq leq geq lessp greaterp),'spaced);

% QE-Switches

% QE call to dnf from the procedure pasf_qeexblock
switch rlpasfdnfqeexblock;
off1 'rlpasfdnfqeexblock;
% QE call to DNF on the input formula's matrix
switch rlpasfdnffirst;
off1 'rlpasfdnffirst;
% Expand bounded quantifiers inside QE, if possible; Not used in the current
% implementation
switch rlpasfexpand;
off1 'rlpasfexpand;
% Simplify intermediate results
switch rlpasfsimplify;
on1 'rlpasfsimplify;
% Approximate bounds by maximal and minimal values
switch rlpasfbapprox;
on1 'rlpasfbapprox;
% Gauss elimination
switch rlpasfgauss;
on1 'rlpasfgauss;
% Full gauss condensing
switch rlpasfgc;
on1 'rlpasfgc;
% Structural condensing
switch rlpasfsc;
on1 'rlpasfsc;
% Structural elimination sets
switch rlpasfses;
on1 'rlpasfses;
% Conflation of structural elimination sets
switch rlpasfconf;
on1 'rlpasfconf;
% If on constrained virtual substitution uses infinity symbols instead of
% cauchy bounds
switch rlqesubi;
on1 'rlqesubi;
% Trun on the old probabilistic mode
switch rlpqeold;
off1 'rlpqeold;

% Force cl_qe to make the formula prenex
switch rlqepnf;  % hack for now - TS
on1 'rlqepnf;

% Verboseswitches

% General verbose switch
switch rlpasfvb;
off1 'rlpasfvb;

% Smart simplification verbose
switch rlsiverbose;
off1 'rlsiverbose;

% Switches automaticly handled on context change
put('pasf,'rl_cswitches,'(
   (rlsism . t)
   (rlsusi . t)));

% Parameters
put('pasf,'rl_params,'(
   (rl_subat!* . pasf_subat)
   (rl_subalchk!* . pasf_subalchk)
   (rl_eqnrhskernels!* . pasf_eqnrhskernels)
   (rl_simplat1!* . pasf_simplat1)
   (rl_fctrat!* . pasf_fctrat)
   (rl_ordatp!* . pasf_ordatp)
   (rl_op!* . pasf_op)
   (rl_simplb!* . pasf_simplb)
   (rl_varsubstat!* . pasf_varsubstat)
   (rl_negateat!* . pasf_negateat)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_tordp!* . ordp)
   (rl_termmlat!* . pasf_termmlat)
   (rl_sacat!* . pasf_sacat)
   (rl_sacatlp!* . cl_sacatlp)
   (rl_varlat!* . pasf_varlat)
   (rl_smupdknowl!* . pasf_smwupdknowl)
   (rl_smrmknowl!* . pasf_smwrmknowl)
   (rl_smcpknowl!* . pasf_smwcpknowl)
   (rl_smmkatl!* . pasf_smwmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_susibin!* . pasf_susibin)
   (rl_susipost!* . pasf_susipost)
   (rl_susitf!* . pasf_susitf)
   (rl_b2terml!* . pasf_b2terml)
   (rl_b2atl!* . pasf_b2atl)
   (rl_bsatp!* . pasf_bsatp)
   (rl_rxffn!* . pasf_rxffn)));

% Services
put('pasf,'rl_services,'(
   (rl_subfof!* . cl_subfof)
   (rl_apnf!* . cl_apnf)
   (rl_atml!* . cl_atml)
   (rl_terml!* . cl_terml)
   (rl_termml!* . cl_termml)
   (rl_ifacl!* . cl_ifacl)
   (rl_ifacml!* . cl_ifacml)
   (rl_tnf!* . cl_tnf)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_all!* . cl_all)
   (rl_ex!* . cl_ex)
   (rl_simpl!* . cl_simpl)
   (rl_atnum!* . cl_atnum)
   (rl_qnum!* . cl_qnum)
   (rl_matrix!* . cl_matrix)
   (rl_qe!* . pasf_qe)
   (rl_wqe!* . pasf_wqe)
   (rl_expand!* . pasf_expand)
   (rl_atl!* . cl_atl)
   (rl_pnf!* . pasf_pnf)
   (rl_dnf!* . pasf_dnf)
   (rl_cnf!* . pasf_cnf)
   (rl_nnf!* . cl_nnf)
   (rl_opt!* . pasf_opt)
   (rl_qea!* . pasf_qea)
   (rl_pqea!* . pasf_pqea)
   (rl_wqea!* . pasf_wqea)
   (rl_pqe!* . pasf_pqe)
   (rl_stex!* . pasf_stex)
   (rl_expanda!* . pasf_expanda)
   (rl_zsimpl!* . pasf_zsimpl)
   (rl_resolve!* . cl_resolve)
   (rl_depth!* . cl_depth)));

% Administration definitions
put('pasf,'simpfnname,'pasf_simpfn);
put('pasf,'rl_prepat,'pasf_prepat);
put('pasf,'rl_resimpat,'pasf_resimpat);
put('pasf,'rl_lengthat,'pasf_lengthat);
put('pasf,'rl_prepterm,'prepf);
put('pasf,'rl_simpterm,'pasf_simpterm);

algebraic infix equal;
put('equal,'pasf_simpfn,'pasf_chsimpat);
put('equal,'number!-of!-args,2);

algebraic infix neq;
put('neq,'pasf_simpfn,'pasf_chsimpat);
put('neq,'number!-of!-args,2);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq);

algebraic infix leq;
put('leq,'pasf_simpfn,'pasf_chsimpat);
put('leq,'number!-of!-args,2);
put('leq,'rtypefn,'quotelog);

algebraic infix geq;
put('geq,'pasf_simpfn,'pasf_chsimpat);
put('geq,'number!-of!-args,2);
put('geq,'rtypefn,'quotelog);

algebraic infix lessp;
put('lessp,'pasf_simpfn,'pasf_chsimpat);
put('lessp,'number!-of!-args,2);
put('lessp,'rtypefn,'quotelog);

algebraic infix greaterp;
put('greaterp,'pasf_simpfn,'pasf_chsimpat);
put('greaterp,'number!-of!-args,2);
put('greaterp,'rtypefn,'quotelog);

algebraic operator cong;
put('cong,'prifn,'pasf_pricong);
put('cong,'pasf_simpfn,'pasf_simpat);
put('cong,'number!-of!-args,3);
put('cong,'rtypefn,'quotelog);
put('cong,'fancy!-prifn,'pasf_fancy!-pricong);

algebraic operator ncong;
put('ncong,'prifn,'pasf_princong);
put('ncong,'pasf_simpfn,'pasf_simpat);
put('ncong,'number!-of!-args,3);
put('ncong,'rtypefn,'quotelog);
put('ncong,'fancy!-prifn,'pasf_fancy!-pricong);

algebraic operator rnd;
put('rnd,'simpfn,'pasf_simprnd);
put('rnd,'number!-of!-args,2);

smacro procedure pasf_op(atf);
   % Presburger arithmetic standard form operator. [atf] is an atomic formula
   % $r(t_1,t_2)$ or $r(t_1,t_2,m)$. Returns $r$ or in case of a congruence
   % the pair $(r . m)$.
   car atf;

smacro procedure pasf_opp(op);
   % Presburger arithmetic standard form operator predicate. [op] is an
   % expression. Returns t iff the name of [op] is a legal operator or
   % relation name. Hardly ever used.
   op memq '(equal neq lessp leq greaterp geq) or
      (pairp op and car op memq '(cong ncong));

smacro procedure pasf_m(atf);
   % Presburger arithmetic standard form modulus operator. [atf] is an atomic
   % formula $t_1 \equiv_m t_2$. Returns $m$.
   cdar atf;

smacro procedure pasf_arg2l(atf);
   % Presburger arithmetic standard form left hand side argument. [atf] is an
   % atomic formula $r(t_1,t_2)$. Returns $t_1$.
   cadr atf;

smacro procedure pasf_arg2r(atf);
   % Presburger arithmetic standard form right hand side argument. [atf] is an
   % atomic formula $r(t_1,t_2)$. Returns $t_2$.
   caddr atf;

smacro procedure pasf_mk2(op,lhs,rhs);
   % Presburger arithmetic standard form make atomic formula. [op] is an
   % operator; [lhs] is the left handside term; [rhs] is the right handside
   % term. Returns the atomic formula $[op]([lhs],[rhs])$.
   {op,lhs,rhs};

smacro procedure pasf_0mk2(op,lhs);
   % Presburger arithmetic standard form make zero right hand atomic
   % formula. [op] is an operator; [lhs] is a term. Returns the atomic formula
   % $[op]([lhs],0)$.
   {op,lhs,nil};

smacro procedure pasf_opn(atf);
   % Presburger arithmetic standard form operator name. [atf] is an
   % atomic formula $r(t_1,t_2)$ or $r(t_1,t_2,m)$. Returns $r$. Used
   % heavily.
   if rl_tvalp atf then
      atf
   else if pairp car atf then
      caar atf
   else
      car atf;

smacro procedure pasf_atfp(f);
   % Presburger arithmetic standard form atomic formula predicate. [f] is a
   % formula. Returns t iff [f] has a legal relation name.
   (pasf_opn f) memq '(equal neq leq geq lessp greaterp
      cong ncong);

smacro procedure pasf_congopp(op);
   op memq '(cong ncong);

smacro procedure pasf_equopp(op);
   op memq '(equal neq);

smacro procedure pasf_congp(atf);
   % Presburger arithmetic standard form congruence atomic formula
   % predicate. [atf] is an atomic formula. Returns t iff the operator
   % is 'cong or 'ncong.
   pairp atf and pairp car atf and pasf_congopp caar atf;

procedure pasf_mkop(op,m);
   % Presburger arithmetic standard form make operator. [op] is an operator;
   % [m] is an optional modulus. Returns $op$ if the operator is not 'cong or
   % 'ncong and $([op] . [m])$ otherwise.
   if op memq '(cong ncong) then
      (op . if null m then
	 % User should use equations instead of congruences modulo 0
      	 rederr{"Modulo 0 congruence created"}
      else
	 m)
   else
      op;

procedure pasf_mkrng(v,lb,ub);
   % Presburger arithmetic standard form make interval range formula. [v] is a
   % variable; [lb] is a lower bound; [ub] is an upper bound. Returns the
   % formula $[lb] \leq [v] \leq [ub]$.
   if lb eq ub then
      pasf_0mk2('equal,addf(v,negf lb))
   else rl_mkn('and,{
      pasf_0mk2('geq,addf(v,negf lb)),
      pasf_0mk2('leq,addf(v,negf ub))});

procedure pasf_simprnd(u);
   % [u] is Lisp Prefix. Returns an SQ.
   <<
      if null u or null cdr u or cddr u then
      	 rederr {"rnd called with",length u,"arguments instead of 2"};
      if not idp cadr u then
	 rederr {"second argument of rnd must be an identifier"};
      mksq({'rnd,reval car u,cadr u},1)
   >>;

procedure pasf_mkrndf(u,key);
   % [u] is an SF; [key] is an interned identifier. Returns an SF.
   numr simp {'rnd,prepf u,key};

procedure pasf_pricong(l);
   % Presburger arithmetic standard form print a congruence. [l] is a lisp
   % prefix. Returns 'failed iff printing failed.
   if null !*nat then
      'failed
   else if !*utf8 then
      pasf_gpricong l
   else <<
      maprin cadr l;
      prin2!* " ~";
      maprin cadddr l;
      prin2!* "~ ";
      maprin caddr l
   >>;

procedure pasf_gpricong(l);
   if numberp cadddr l then <<
      maprin cadr l;
      prin2!* " ";
      prin2!* intern compress nconc(explode car l,explode cadddr l);
      prin2!* " ";
      maprin caddr l
   >> else <<
      maprin cadr l;
      prin2!* " ";
      prin2!* car l;
      prin2!* " ";
      maprin caddr l;
      prin2!* " mod ";
      maprin cadddr l
   >>;

procedure pasf_princong(l);
   % Presburger arithmetic standard form print an incongruence. [l] is a lisp
   % prefix. Returns 'failed iff printing failed.
   if null !*nat then
      'failed
   else if !*utf8 then
      pasf_gpricong l
   else <<
      maprin cadr l;
      prin2!* " #";
      maprin cadddr l;
      prin2!* "# ";
      maprin caddr l
   >>;

procedure pasf_fancy!-pricong(l);
   % Presburger arithmetic standard form texmacs print a congruence. [l] is a
   % lisp prefix. Returns 'failed iff printing failed.
   if rl_texmacsp() then
      pasf_fancy!-pricong!-texmacs l
   else
      pasf_fancy!-pricong!-fm l;

procedure pasf_fancy!-pricong!-texmacs(l);
   % Presburger arithmetic standard form texmacs print a congruence. [l] is a
   % lisp prefix. Returns 'failed iff printing failed.
   if null !*nat then
      'failed
   else <<
      maprin cadr l; % lhs
      if car l eq 'cong then
	 fancy!-prin2 "\equiv"
      else
	 fancy!-prin2 "\not\equiv";
      fancy!-prin2!-underscore();
      fancy!-prin2 "{";
      maprin cadddr l; % modulus
      fancy!-prin2 "}";
      maprin caddr l; % rhs
   >>;

procedure pasf_fancy!-pricong!-fm(l);
   % Presburger arithmetic standard form texmacs print a congruence. [l] is a
   % lisp prefix. Returns 'failed iff printing failed.
   if null !*nat then
      'failed
   else <<
      maprin cadr l;
      if car l eq 'cong then
      	 fancy!-special!-symbol(186,2)
      else <<
	 fancy!-prin2 "/";
	 fancy!-special!-symbol(186,2)
      >>;
      maprin caddr l;
      fancy!-prin2 " (";
      maprin cadddr l;
      fancy!-prin2 ")"
   >>;

procedure pasf_verbosep();
   % Presburger arithmetic standard form verbose switch. Returns t iff the
   % main switch rlverbose is on and the switch rlpasfvb is on.
   !*rlverbose and !*rlpasfvb;

procedure pasf_simpterm(l);
   % Presburger arithmetic standard form simp term. [l] is lisp
   % prefix. Returns [l] as a PASF term.
   numr simp l;

procedure pasf_prepat(atf);
   % Presburger arithmetic standard form prep atomic formula. [atf] is a PASF
   % atomic formula. Returns [atf] in Lisp prefix form.
   if pasf_congp atf then
       {pasf_opn atf,prepf pasf_arg2l atf,prepf pasf_arg2r atf,
	  prepf pasf_m atf}
   else
      pasf_opn atf . for each arg in rl_argn atf collect prepf arg;

procedure pasf_resimpat(atf);
   % Presburger arithmetic standard form resimp atomic formula. [atf] is a
   % PASF atomic formula. Returns the atomic formula [atf] with resimplified
   % terms.
   pasf_mk2(if pasf_congp atf then
      (pasf_opn atf . numr resimp !*f2q pasf_m atf)
   else
      pasf_op atf,
      numr resimp !*f2q pasf_arg2l atf, numr resimp !*f2q pasf_arg2r atf);

procedure pasf_lengthat(atf);
   % Presburger arithmetic standard form length of an atomic formula. [atf] is
   % an atomic formula. Returns a number, the length of [atf].
   length rl_argn atf;
   % Note: This procedure is added only for code compatibility and is not used
   % inside PASF yet.

procedure pasf_chsimpat(l);
   % Presburger arithmetic standard form chain simp. [l] is a lisp prefix.
   % Returns [l] as a conjunction of atomic formulas.
   rl_smkn('and,for each x in pasf_chsimpat1 l collect pasf_simpat x);

procedure pasf_chsimpat1(l);
   % Presburger arithmetic standard form chain simp subprocedure. [l] is a
   % lisp prefix. Returns [l] without chains.
   begin scalar leftl,rightl,lhs,rhs;
      lhs := cadr l;
      if pairp lhs and pasf_opp car lhs then <<
	 leftl := pasf_chsimpat1 lhs;
	 lhs := caddr lastcar leftl
      >>;
      rhs := caddr l;
      if pairp rhs and pasf_opp car rhs then <<
	 rightl := pasf_chsimpat1 rhs;
	 rhs := cadr car rightl
      >>;
      return nconc(leftl,{car l,lhs,rhs} . rightl)
   end;

procedure pasf_simpat(u);
   % Simp atomic formula. [u] is Lisp prefix. Returns an atomic
   % formula.
   begin scalar op,lhs,rhs,nlhs,f,m;
      op := car u;
      if op memq '(cong ncong) then <<
	 if length u neq 4 then
 	    rederr("invalid length in congruence");
	 lhs := subtrsq(simp cadr u,simp caddr u);
	 m := simp cadddr u;
	 if denr lhs neq 1 or denr m neq 1 then
 	    rederr("denominators in congruence");
	 return pasf_0mk2(op . numr m,numr lhs)
      >>;
      lhs := simp cadr u;
      if not (!*rlnzden or !*rlposden or (domainp denr lhs)) then
 	 typerr(u,"atomic formula");
      rhs := simp caddr u;
      if not (!*rlnzden or !*rlposden or (domainp denr rhs)) then
 	 typerr(u,"atomic formula");
      lhs := subtrsq(lhs,rhs);
      nlhs := numr lhs;
      if !*rlposden and not domainp denr lhs then <<
	 f := pasf_0mk2(op,nlhs);
	 if !*rladdcond then
	    f := if op memq '(lessp leq greaterp geq) then
	       rl_mkn('and,{pasf_0mk2('greaterp,denr lhs),f})
	    else
	       rl_mkn('and,{pasf_0mk2('neq,denr lhs),f});
	 return f
      >>;
      if !*rlnzden and not domainp denr lhs then <<
	 if op memq '(lessp leq greaterp geq) then
	    nlhs := multf(nlhs,denr lhs);
	 f := pasf_0mk2(op,nlhs);
	 if !*rladdcond then
	    f := rl_mkn('and,{pasf_0mk2('neq,denr lhs),f});
	 return f
      >>;
      return pasf_0mk2(op,nlhs)
   end;

procedure pasf_termp(exps,exclst);
   % Presburger arithmetic standard form test for a correct presburger
   % term. [exps] is an expression supposed to be a PASF term; [exclst] is an
   % exception list of variables, that are not allowed to be
   % non-linear. Returns t iff the term is a correct UPrA term.
   begin scalar p,errc,oldord;
      oldord:= setkorder({});
      for each var in kernels exps do <<
	 setkorder({var});
	 p := reorder(exps);
	 if var memq exclst then <<
	    % Testing for degree of the variable
	    %if ldeg p > 1 then
	    %   rederr{"Illegal UPrA formula :",
	    %   	  "Quantified variable",var,"with degreee",ldeg p};
	    % Testing for other quantified variables in exception list
	    for each v in exclst do
	       if v neq var and v memq kernels lc p then
 	       	  rederr{"Illegal UPrA formula :",
		  "Quantified variables",var,"and",v,"multiplied"}
	 >>;
	 % Testing for parametric coefficients
	 if not domainp lc p then
	    errc := t
      >>;
      % Term is correct
      setkorder(oldord);
      return errc
   end;

procedure pasf_uprap(f);
   % Presburger arithmetic standard form test for uniform presburger
   % arithmetic formula. [f] is a formula. Returns t only if the formula is
   % in UPrA and not in PrA and raises an error if the formula is neither in
   % PrA nor in UPrA.
   pasf_uprap1(f,nil);

procedure pasf_uprap1(f,bvarl);
   % Presburger arithmetic standard form test for uniform presburger
   % arithmetic formula subprocedure. [f] is a formula; [bvarl] is a list of
   % bounded variables. Returns t only if the formula is in UPrA and not in
   % PrA and raises an error if the formula is neither in PrA nor in UPrA.
   begin scalar s;
      if rl_tvalp f then
      	 return nil;
      if rl_boolp rl_op f then <<
	 % If one of the arguments is in UPrA then the whole formula too
	 for each arg in rl_argn f do
	    s := s or pasf_uprap1(arg,bvarl);
	 return s
      >>;
      if rl_quap rl_op f then
	 return pasf_uprap1(rl_mat f,rl_var f . bvarl);
      if rl_bquap rl_op f then
	 return (pasf_uprap1(rl_mat f,rl_var f . bvarl) or
	    pasf_uprap1(rl_b f,bvarl));
      % Atomic formulas
      return if pasf_congp f then
	 pasf_termp(pasf_arg2l f,bvarl) or not domainp pasf_m f
      else
	 pasf_termp(pasf_arg2l f,bvarl)
   end;

procedure pasf_univnlfp(f,x);
   % Presburger arithmetic standard form univariate nonlinear formula
   % predicate. [f] is a formula; [x] is a variable. Returns t iff [f] is a
   % univariate formula and contains a term, that is not linear in [x].
   begin scalar res;
      for each atf in rl_atl f do
      	 res := res or pasf_univnlp(atf,x);
      return res;
   end;

procedure pasf_univnlp(atf,x);
   % Presburger arithmetic standard form univariate nonlinear atomic formula
   % predicate. [atf] is an atomic formula; [x] is a variable. Returns t iff
   % [atf] is a univariate formula and contains a term, that is not linear in
   % [x].
   begin scalar oldord,res;
      oldord := setkorder({x});
      % quick fix to avoid car on nil (TS)
      if not domainp pasf_arg2l atf and ldeg reorder pasf_arg2l atf > 1 then
	 res := t;
      setkorder oldord;
      return res;
   end;

endmodule; % [pasf]

end; % of file
