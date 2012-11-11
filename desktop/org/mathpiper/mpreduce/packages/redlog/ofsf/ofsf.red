% ----------------------------------------------------------------------
% $Id: ofsf.red 1815 2012-11-02 13:20:27Z thomas-sturm $
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
   fluid '(ofsf_rcsid!* ofsf_copyright!*);
   ofsf_rcsid!* :=
      "$Id: ofsf.red 1815 2012-11-02 13:20:27Z thomas-sturm $";
   ofsf_copyright!* := "(c) 1995-2009 A. Dolzmann, T. Sturm, 2010 T. Sturm"
>>;

module ofsf;
% Ordered field standard form. Main module. Algorithms on first-order
% formulas over ordered fields. The language contains binary relations
% ['equal], ['neq], ['greaterp], ['lessp], ['geq], ['leq].

create!-package('(ofsf ofsfsiat ofsfsism ofsfbnf ofsfqe ofsfopt ofsfgs
   ofsfmisc ofsfcad ofsfcadproj ofsfanuex ofsfxopt ofsfdet ofsftfc ofsfhqe
   ofsfdecdeg),
   nil);

load!-package 'redlog;
loadtime load!-package 'cl;
loadtime load!-package 'rltools;
loadtime load!-package 'linalg;
loadtime load!-package 'matrix;
loadtime load!-package 'factor;
loadtime load!-package 'cgb;

load!-package 'assert;
on1 'assert;

exports ofsf_simpterm,ofsf_prepat,ofsf_resimpat,ofsf_lengthat,ofsf_chsimpat,
   ofsf_simpat,ofsf_op,ofsf_arg2l,ofsf_arg2r,ofsf_argn,ofsf_mk2,ofsf_0mk2,
   ofsf_mkn,ofsf_opp,ofsf_mkstrict,ofsf_simplat1,ofsf_smrmknowl,ofsf_smcpknowl,
   ofsf_smupdknowl,ofsf_smmkatl,ofsf_dnf,ofsf_cnf,ofsf_subsumption,
   ofsf_sacatlp,ofsf_sacat,ofsf_varsel,ofsf_qesubcr1,ofsf_qesubcr2,ofsf_qesubr,
   ofsf_qesubcq,ofsf_qesubq,ofsf_qesubi,ofsf_qesubcrpe1,ofsf_qesubcrme1,
   ofsf_qesubcrpe2,ofsf_qesubcrme2,ofsf_qesubrpe,ofsf_qesubrme,ofsf_qesubcqpe,
   ofsf_qesubcqme,ofsf_qesubqpe,ofsf_qesubqme,ofsf_valassp,ofsf_translat,
   ofsf_surep,ofsf_elimset,ofsf_bettergaussp,ofsf_esetunion,ofsf_bestgaussp,
   ofsf_qefsolset,ofsf_qemkans,ofsf_preprexpr,ofsf_decdeg,ofsf_decdeg1,
   ofsf_transform,ofsf_thsimpl,ofsf_specelim,ofsf_opt,ofsf_gsn,
   ofsf_gsc,ofsf_gsd,ofsf_gssimplify,ofsf_gssimplify0,ofsf_termprint,
   ofsf_canegrel,ofsf_anegrel,ofsf_clnegrel,ofsf_lnegrel,ofsf_fctrat,
   ofsf_negateat,ofsf_varlat,ofsf_varsubstat,ofsf_ordatp,ofsf_ordrelp,
   ofsf_a2cdl,ofsf_t2cdl,ofsf_subat,ofsf_subalchk,ofsf_eqnrhskernels,
   ofsf_getineq,ofsf_structat,ofsf_ifstructat,ofsf_termmlat,ofsf_multsurep,
   ofsf_cad,ofsf_cadswitches;

imports cl,rltools;

fluid '(!*rlsiatadv !*rlsipd !*rlsiexpl !*rlsiexpla !*rlgssub !*rlsiso !*rlqesr
   !*rlgsrad !*rlgsred !*rlgsprod !*rlgserf !*rlverbose !*rlqedfs !*rlsipw
   !*rlsipo !*rlparallel !*rlopt1s !*rlsifac !*rlbnfsac !*rlgsvb !*rlqegen
   !*rlsitsqspl !*rlgsbnf !*rlgsutord !*rlqegenct !*rlqeheu !*rlnzden
   !*rlposden !*rladdcond !*rlqeqsc !*rlqesqsc !*rlsusi !*rlsusimult !*rlsusigs
   !*rlsusiadd !*rlcadfac !*rlcaddnfformula !*rlcadpreponly !*rlcadbaseonly
   !*rlcadextonly !*rlcadprojonly !*rlcadverbose !*rlcaddebug !*rlcadpartial
   !*rlcadfulldimonly !*rlcadtrimtree !*rlcadrawformula !*rlcadisoallroots
   !*rlcadaproj !*rlcadaprojalways !*rlcadhongproj !*exp !*rlanuexverbose
   !*rlanuexdifferentroots !*rlanuexdebug !*rlanuexpsremseq
   !*rlanuexgcdnormalize !*rlanuexsgnopt !*rlcaddecdeg !*rlcadte !*rlcadpbfvs
   !*rlvmatvb ofsf_cadtheo!* cl_pal!* cl_lps!* cl_theo!* !*rlcadfasteval
   ofsf_xopt!-nodes!* ofsf_xopt!-delnodes!* ofsf_xopt!-plnodes!*
   ofsf_xopt!-fnodes!* ofsf_xopt!-thcof!* !*rlxoptqe !*rlxopt !*rlxoptsb
   !*rlxoptpl !*rlxoptri !*rlxoptric !*rlxoptrir !*rlxoptses !*rlourdet
   ofsf_gstv!* !*cgbverbose !*groebopt !*rlhqetfcsplit !*rlhqetfcfullsplit
   !*rlhqetfcfast !*rlhqevb !*rlhqevarsel !*rlhqevarselx !*rlhqedim0
   !*rlhqetheory !*rlhqegbred !*rlhqeconnect !*rlhqestrconst !*rlhqegbdimmin
   !*rlhqegen !*cgbfaithful !*rlqeaprecise !*rlqefilterbounds !*rlpos
   !*rlsifaco !*rlqeans !*rlqelog rlqelog!* !*rlqeprecise !*rlqevarseltry
   !*rlqefbqepcad !*rlqefbmma !*rlqefbslfq !*msg !*rlbrkcxk);

fluid '(!*rlqegen1 !*rlcadmcproj !*rlpscsgen); % temporary for CAD
fluid '(ofsf_hqetheo!* ofsf_hqexvars!*);         % temporary for HQE

flag('(ofsf),'rl_package);

% Parameters
put('ofsf,'rl_params,'(
   (rl_subat!* . ofsf_subat)
   (rl_subalchk!* . ofsf_subalchk)
   (rl_eqnrhskernels!* . ofsf_eqnrhskernels)
   (rl_ordatp!* . ofsf_ordatp)
   (rl_qemkans!* . ofsf_qemkans)
   (rl_simplat1!* . ofsf_simplat1)
   (rl_smupdknowl!* . ofsf_smwupdknowl)
   (rl_smrmknowl!* . ofsf_smwrmknowl)
   (rl_smcpknowl!* . ofsf_smwcpknowl)
   (rl_smmkatl!* . ofsf_smwmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl)
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1)
   (rl_susipost!* . ofsf_susipost)
   (rl_susitf!* . ofsf_susitf)
   (rl_susibin!* . ofsf_susibin)
   (rl_negateat!* . ofsf_negateat)
   (rl_varlat!* . ofsf_varlat)
   (rl_varsubstat!* . ofsf_varsubstat)
   (rl_translat!* . ofsf_translat)
   (rl_elimset!*. ofsf_elimset)
   (rl_trygauss!* . cl_trygauss)
   (rl_varsel!* . ofsf_varsel)
   (rl_betterp!* . cl_betterp)
   (rl_subsumption!* . ofsf_subsumption)
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_sacat!* . ofsf_sacat)
   (rl_sacatlp!* . ofsf_sacatlp)
   (rl_qstrycons!* . ofsf_qstrycons)
   (rl_qscsaat!* . cl_qscsaat)
   (rl_qssubat!* . ofsf_qssubat)
   (rl_qsconsens!* . cl_qsnconsens)
   (rl_qsimpltestccl!* . cl_qsimpltestccl)
%   (rl_qssubsumep!* . cl_qssusubyit)
   (rl_qssubsumep!* . cl_qssusubytab)
   (rl_qstautp!* . cl_qstautp)
   (rl_qssusuat!* . ofsf_qssusuat)
   (rl_qssimpl!* . cl_qssimpl)
   (rl_qssiadd!* . ofsf_qssiadd)
   (rl_fctrat!* . ofsf_fctrat)
   (rl_tordp!* . ordp)
   (rl_transform!* . ofsf_transform)
   (rl_a2cdl!* . ofsf_a2cdl)
   (rl_t2cdl!* . ofsf_t2cdl)
   (rl_getineq!* . ofsf_getineq)
   (rl_qefsolset!* . ofsf_qefsolset)
   (rl_bettergaussp!* . ofsf_bettergaussp)
   (rl_bestgaussp!* . ofsf_bestgaussp)
   (rl_esetunion!* . ofsf_esetunion)
   (rl_structat!* . ofsf_structat)
   (rl_ifstructat!* . ofsf_ifstructat)
   (rl_termmlat!* . ofsf_termmlat)
   (rl_multsurep!* . ofsf_multsurep)
   (rl_specelim!* . ofsf_specelim)
   (rl_fbqe!* . ofsf_fbqe)
   (rl_mkequation!* . ofsf_mkequation)
   (rl_dfgPrintV!* . ofsf_dfgPrintV)
   (rl_dfgPrintAt!* . ofsf_dfgPrintAt)
   (rl_smt2PrintAt!* . ofsf_smt2PrintAt)
   (rl_smt2ReadAt!* . ofsf_smt2ReadAt)
   (rl_rxffn!* . ofsf_rxffn)));

% Services
put('ofsf,'rl_services,'(
   (rl_subfof!* . cl_subfof)
   (rl_identifyonoff!* . cl_identifyonoff)
   (rl_simpl!* . cl_simpl)
   (rl_thsimpl!* . ofsf_thsimpl)
   (rl_nnf!* . cl_nnf)
   (rl_nnfnot!* . cl_nnfnot)
   (rl_pnf!* . cl_pnf)
   (rl_cnf!* . ofsf_cnf)
   (rl_dnf!* . ofsf_dnf)
   (rl_all!* . cl_all)
   (rl_ex!* . cl_ex)
   (rl_ex2!* . cl_ex2)
   (rl_atnum!* . cl_atnum)
   (rl_qnum!* . cl_qnum)
   (rl_tab!* . cl_tab)
   (rl_atab!* . cl_atab)
   (rl_itab!* . cl_itab)
   (rl_gsc!* . ofsf_gsc)
   (rl_gsd!* . ofsf_gsd)
   (rl_gsn!* . ofsf_gsn)
   (rl_posqe!* . ofsf_posqe)
   (rl_qe!* . ofsf_qe)
   (rl_posqea!* . ofsf_posqea)
   (rl_qea!* . ofsf_qea)
   (rl_posgqe!* . ofsf_posgqe)
   (rl_gqe!* . cl_gqe)
   (rl_qeg!* . ofsf_qeg)
   (rl_posgqea!* . ofsf_posgqea)
   (rl_gqea!* . cl_gqea)
   (rl_qeipo!* . cl_qeipo)
   (rl_qews!* . cl_qews)
   (rl_opt!* . ofsf_opt)
   (rl_ifacl!* . cl_ifacl)
   (rl_ifacml!* . cl_ifacml)
   (rl_matrix!* . cl_matrix)
   (rl_apnf!* . cl_apnf)
   (rl_atml!* . cl_atml)
   (rl_tnf!* . cl_tnf)
   (rl_atl!* . cl_atl)
   (rl_struct!* . cl_struct)
   (rl_ifstruct!* . cl_ifstruct)
   (rl_termml!* . cl_termml)
   (rl_terml!* . cl_terml)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_gentheo!* . cl_gentheo)
   (rl_decdeg!* . ofsf_decdeg)
   (rl_decdeg1!* . ofsf_decdeg1)
   (rl_surep!* . cl_surep)
   (rl_siaddatl!* . cl_siaddatl)
   (rl_cad!* . ofsf_cad)
   (rl_gcad!* . ofsf_gcad)
   (rl_cadswitches!* . ofsf_cadswitches)
   (rl_lqe!* . cl_lqe)
   (rl_xqe!* . ofsf_xopt!-qe)
   (rl_xqea!* . ofsf_xopt!-qea)
   (rl_lthsimpl!* . ofsf_lthsimpl)
   (rl_lthsimpl!* . ofsf_lthsimpl)
   (rl_quine!* . cl_quine)
   (rl_cadporder!* . ofsf_cadporder)
   (rl_gcadporder!* . ofsf_gcadporder)
   (rl_cadproj!* . ofsf_cadproj)
   (rl_hqe!* . ofsf_hqe)
   (rl_ghqe!* . ofsf_ghqe)
   (rl_resolve!* . ofsf_resolve)
   (rl_posresolve!* . ofsf_posresolve)
   (rl_tan2!* . ofsf_tan2)
   (rl_depth!* . cl_depth)
   (rl_qesil!* . cl_qesil)
   (rl_straightify!* . cl_straightify)
   (sl_straightify!* . cl_sstraightify)
   (sl_simpl!* . cl_ssimpl)
   (sl_atnum!* . cl_satnum)
   (sl_pnf!* . cl_spnf)
   (rl_dfgprint!* . cl_dfgPrint)
   (rl_smt2Print!* . cl_smt2Print)
   (rl_smt2Read!* . cl_smt2Read)
   (sl_unstraightify!* . sl_unstraightify)));

% Admin
put('ofsf,'simpfnname,'ofsf_simpfn);
put('ofsf,'simpdefault,'ofsf_simprel);

put('ofsf,'rl_prepat,'ofsf_prepat);
put('ofsf,'rl_resimpat,'ofsf_resimpat);
put('ofsf,'rl_lengthat,'ofsf_lengthat);

put('ofsf,'rl_prepterm,'prepf);
put('ofsf,'rl_simpterm,'ofsf_simpterm);

% algebraic infix equal;
put('equal,'ofsf_simpfn,'ofsf_chsimpat);
% put('equal,'number!-of!-args,2);

% algebraic infix neq;
put('neq,'ofsf_simpfn,'ofsf_chsimpat);
% put('neq,'number!-of!-args,2);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq) where !*msg=nil;
if rl_texmacsp() then
   put('neq,'fancy!-infix!-symbol,"\,\neq\, ");

% algebraic infix leq;
put('leq,'ofsf_simpfn,'ofsf_chsimpat);
% put('leq,'number!-of!-args,2);
put('leq,'rtypefn,'quotelog);
if rl_texmacsp() then
   put('leq,'fancy!-infix!-symbol,"\,\leq\, ");

% algebraic infix geq;
put('geq,'ofsf_simpfn,'ofsf_chsimpat);
% put('geq,'number!-of!-args,2);
put('geq,'rtypefn,'quotelog);
if rl_texmacsp() then
   put('geq,'fancy!-infix!-symbol,"\,\geq\, ");

% algebraic infix lessp;
put('lessp,'ofsf_simpfn,'ofsf_chsimpat);
% put('lessp,'number!-of!-args,2);
put('lessp,'rtypefn,'quotelog);
if rl_texmacsp() then
   put('lessp,'fancy!-infix!-symbol,"\,<\, ");

% algebraic infix greaterp;
put('greaterp,'ofsf_simpfn,'ofsf_chsimpat);
% put('greaterp,'number!-of!-args,2);
put('greaterp,'rtypefn,'quotelog);
if rl_texmacsp() then
   put('greaterp,'fancy!-infix!-symbol,"\,>\, ");

flag('(equal neq leq geq lessp greaterp),'spaced);
flag('(ofsf_chsimpat),'full);

smacro procedure ofsf_op(atf);
   % Ordered field operator. [atf] is an atomic formula
   % $R(t_1,t_2)$. Returns $R$.
   car atf;

smacro procedure ofsf_arg2l(atf);
   % Ordered field binary operator left hand side argument. [atf] is
   % an atomic formula $R(t_1,t_2)$. Returns $t_1$.
   cadr atf;

smacro procedure ofsf_arg2r(atf);
   % Ordered field binary operator right hand side argument. [atf] is
   % an atomic formula $R(t_1,t_2)$. Returns $t_2$.
   caddr atf;

smacro procedure ofsf_argn(atf);
   % Ordered field binary operator right hand side argument. [atf] is
   % an atomic formula $R(t_1,t_2)$. Returns the list $(t_1,t_2)$.
   {cadr atf,caddr atf};

smacro procedure ofsf_mk2(op,lhs,rhs);
   % Ordered field constructor for binary operator. [op] is a relation
   % [lhs] and [rhs] are terms. Returns the atomic formula
   % $[op]([lhs],[rhs])$.
   {op,lhs,rhs};

smacro procedure ofsf_0mk2(op,lhs);
   % Ordered field zero constructor for binary operator. [op] is a
   % relation [lhs] is a term. Returns the atomic formula
   % $[op]([lhs],0)$.
   {op,lhs,nil};

smacro procedure ofsf_mkn(op,argl);
   % Ordered field constructor for binary operator. [op] is a relation
   % [argl] is a list $(t_1,t_2)$ of terms. Returns the atomic formula
   % $[op](t_1,t_2)$.
   {op,car argl,cadr argl};

smacro procedure ofsf_opp(op);
   % Orderd field standard form operator predicate. [op] is an
   % S-expression. Returns [nil] if op is not a relation.
   op memq '(lessp leq equal neq geq greaterp);

procedure ofsf_simpterm(u);
   numr simp u;

procedure ofsf_prepat(f);
   {ofsf_op f,prepf ofsf_arg2l f,prepf ofsf_arg2r f};

procedure ofsf_resimpat(f);
   ofsf_mk2(ofsf_op f,
      numr resimp !*f2q ofsf_arg2l f,numr resimp !*f2q ofsf_arg2r f);

procedure ofsf_lengthat(f);
   2;

procedure ofsf_chsimpat(u);
   rl_smkn('and,for each x in ofsf_chsimpat1 u collect ofsf_simpat x);

procedure ofsf_chsimpat1(u);
   begin scalar leftl,rightl,lhs,rhs;
      lhs := cadr u;
      if pairp lhs and ofsf_opp car lhs then <<
	 leftl := ofsf_chsimpat1 lhs;
	 lhs := caddr lastcar leftl
      >>;
      rhs := caddr u;
      if pairp rhs and ofsf_opp car rhs then <<
	 rightl := ofsf_chsimpat1 rhs;
	 rhs := cadr car rightl
      >>;
      return nconc(leftl,{car u,lhs,rhs} . rightl)
   end;

procedure ofsf_simpat(u);
   % Ordered field simp atomic formula. [u] is Lisp prefix. Returns an
   % atomic formula.
   begin scalar op,lhs,rhs,nlhs,f;
      op := car u;
      lhs := simp cadr u;
      if not (!*rlnzden or !*rlposden or (domainp denr lhs)) then
 	 typerr(u,"atomic formula");
      rhs := simp caddr u;
      if not (!*rlnzden or !*rlposden or (domainp denr rhs)) then
 	 typerr(u,"atomic formula");
      lhs := subtrsq(lhs,rhs);
      nlhs := numr lhs;
      if !*rlposden and not domainp denr lhs then <<
	 f := ofsf_0mk2(op,nlhs);
	 if !*rladdcond then
	    f := if op memq '(lessp leq greaterp geq) then
	       rl_mkn('and,{ofsf_0mk2('greaterp,denr lhs),f})
	    else
	       rl_mkn('and,{ofsf_0mk2('neq,denr lhs),f});
	 return f
      >>;
      if !*rlnzden and not domainp denr lhs then <<
	 if op memq '(lessp leq greaterp geq) then
	    nlhs := multf(nlhs,denr lhs);
	 f := ofsf_0mk2(op,nlhs);
	 if !*rladdcond then
	    f := rl_mkn('and,{ofsf_0mk2('neq,denr lhs),f});
	 return f
      >>;
      return ofsf_0mk2(op,nlhs)
   end;

procedure ofsf_mkstrict(r);
   % Ordered field standard form make strict. [r] is an ordering
   % relation. Returns the strict part of [r].
   if r eq 'leq then 'lessp else if r eq 'geq then 'greaterp else r;

procedure ofsf_mkequation(lhs,rhs);
   begin scalar w;
      w := subtrsq(simp lhs,simp rhs);
      if !*rlposden or !*rlnzden or domainp denr w then
   	 return ofsf_0mk2('equal,numr w);
      rederr {"ofsf_mkequation: parametric denominator in",w}
   end;

endmodule;  % [ofsf]

end;  % of file
