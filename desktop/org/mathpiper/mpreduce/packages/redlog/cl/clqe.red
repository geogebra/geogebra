% ----------------------------------------------------------------------
% $Id: clqe.red 1842 2012-11-19 12:31:57Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 A. Dolzmann and T. Sturm, 2010-2011 T. Sturm
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
   fluid '(cl_qe_rcsid!* cl_qe_copyright!*);
   cl_qe_rcsid!* := "$Id: clqe.red 1842 2012-11-19 12:31:57Z thomas-sturm $";
   cl_qe_copyright!* := "(c) 1995-2009 A. Dolzmann, T. Sturm, 2010-2011 T. Sturm"
>>;

module clqe;
% Common logic quantifier elimination by elimination sets. Submodule of [cl].
% Currently limited to quadratic formulas.

struct Formula;
struct QfFormula;
struct QfFormulaL checked by listp;
struct Theory checked by listp;
struct TheoryFormulaPair checked by pairp;
struct KernelL checked by listp;
struct Quantifier checked by idp;


%DS
% TaggedContainerElementL ::= Status . ContainerElementL
% Status ::= "elim" | "failed" | "local" | "nonocc"
% ContainerElementL ::= (ContainerElement, ...)
% ContainerElement ::= VarList . QfFormula . Answer
% VarList ::= VariableL | "'break"
% Answer ::= (SubstTriplet, ...) (* "nil" if not ans *)
% SubstTriplet ::= (Variable, SubstFunction, ArgumentList)
% AnswerTranslation ::= (* context dependent, "nil" if not ans *)

struct TaggedContainerElementL checked by taggedContainerElementLP;
struct ContainerElementL checked by listp;
struct ContainerElement checked by containerElementP;
struct VarList checked by varListP;
struct Answer checked by listp;
struct SubstTriplet checked by substTripletP;
struct AnswerTranslation;


procedure taggedContainerElementLP(x);
   pairp x and car x memq '(elim failed local nonocc) and listp cdr x;

procedure containerElementP(x);
   pairp x and pairp cdr x and varListP car x and listp cddr x;

procedure varListP(x);
   x eq 'break or listp x;

procedure substTripletP(x);
   listp x and eqn(length x,3);


%DS
% Container ::= (ContainerElementL . QfFormulaL)

struct Container checked by pairp;


%DS
% Point ::= (Coordinate, ...)
% Coordinate ::= Equation (* Kernel = Integer *)

struct Point checked by listp;


%DS
% EliminationResult ::= (Theory . ExtendedQeResult)
% ExtendedQeResult ::= (..., (QfFormula, SamplePoint), ...)
% SamplePoint ::= EquationL (* with equations of the form kernel = ... *)

struct EliminationResult checked by pairp;
struct ExtendedQeResult checked by alistp;


declare ce_mk: (VarList,QfFormula,Answer,AnswerTranslation) -> ContainerElement;

smacro procedure ce_mk(vl,f,an);
   % Container element make.
   vl . f . an;


declare ce_vl: (ContainerElement) -> VarList;

smacro procedure ce_vl(x);
   % Container element variable list.
   car x;


declare ce_f: (ContainerElement) -> QfFormula;

smacro procedure ce_f(x);
   % Container element formula.
   cadr x;


declare ce_ans: (ContainerElement) -> Answer;

smacro procedure ce_ans(x);
   % Container element answer.
   cddr x;


declare co_new: () -> Container;

procedure co_new();
   % Container make.
   nil . nil;


smacro procedure co_data(co);
   car co;

smacro procedure co_dynl(co);
   cdr co;

smacro procedure co_setData(co,data);
   rplaca(co,data);

smacro procedure co_setDynl(co,dynl);
   rplacd(co,dynl);


declare co_save: (Container,ContainerElementL) -> Container;

procedure co_save(co,dol);
   % Container save.
   if !*rlqedfs and !*rlqedyn then
      co_dynpush(co,dol)
   else if !*rlqedfs then
      co_push(co,dol)
   else co_enqueue(co,dol);


declare co_push: (Container,ContainerElementL) -> Container;

procedure co_push(co,dol);
   % Container push.
   <<
      for each ce in dol do
      	 co := co_insert(co,ce);
      co
   >>;


declare co_insert: (Container,ContainerElement) -> Container;

procedure co_insert(co,ce);
   % Insert 1 element into container.
   co_setData(co,co_insert1(co_data co,ce));


declare co_insert1: (Container,ContainerElement) -> Container;

procedure co_insert1(co,ce);
   % Insert 1 element into container.
   if co_member(ce,co) then <<
      if !*rlverbose and !*rlqevb and !*rlqevbold then
	 ioto_prin2 "@";
      co
   >> else
      ce . co;


declare co_dynPush: (Container,ContainerElementL) -> Container;

procedure co_dynPush(co,dol);
   % Container dynamic programming push.
   <<
      for each ce in dol do
      	 co := co_dynPush1(co,ce);
      co
   >>;


declare co_dynPush1: (Container,ContainerElement) -> Container;

procedure co_dynPush1(co,ce);
   % Container dynamic programming push 1 element.
   begin scalar f,vl;
      f := ce_f ce;
      vl := ce_vl ce;
      if lto_hmember(vl . f,co_dynl co,'co_hfn) then <<
	 if !*rlverbose and !*rlqevb and !*rlqevbold then
	    ioto_prin2 "@";
	 return co
      >>;
      co_setDynl(co,lto_hinsert(vl . f,co_dynl co,'co_hfn));
      co_setData(co,ce . co_data co);
      return co
   end;


procedure co_hfn(item);
   % Container hash function.
   {cl_fvarl1 cdr item,rl_atnum cdr item};


declare co_enqueue: (Container,ContainerElementL) -> Container;

procedure co_enqueue(co,dol);
   % Container enqueue.
   co_setData(co,co_enqueue1(co_data co,dol));


declare co_enqueue: (Container,ContainerElementL) -> Container;

procedure co_enqueue1(co,dol);
   % Container enqueue.
   <<
      if null co and dol then <<
	 co := {nil,car dol};
	 car co := cdr co;
	 dol := cdr dol
      >>;
      for each x in dol do
	 if not co_member(x,cdr co) then
	    car co := (cdar co := {x});
      co
   >>;


declare co_get: (Container) -> DottedPair;

procedure co_get(co);
   % Container get. Returns $(e . c)$ where $e$ is a container element and $c$
   % is the container [co] without the entry $e$.
   if !*rlqedfs then co_pop co else co_dequeue co;


declare co_pop: (Container) -> DottedPair;

procedure co_pop(co);
   % Container pop. Returns $(e . c)$ where $e$ is a container element and $c$
   % is the container [co] without the entry $e$.
   begin scalar a;
      a := car co_data co ;
      co_setData(co,cdr co_data co);
      return a . co
   end;


declare co_dequeue: (Container) -> DottedPair;

procedure co_dequeue(co);
   begin scalar a,d;
      a . d := co_dequeue1 co_data co;
      co_setData(co,d);
      return a . co
   end;

procedure co_dequeue1(co);
   % Container dequeue. Returns $(e . c)$ where $e$ is a container element and
   % $c$ is the container [co] without the entry $e$.
   if co then cadr co . if cddr co then (car co . cddr co);


declare co_length: (Container) -> Integer;

procedure co_length(co);
   % Container length. Returns the number of elements in [co].
   if !*rlqedfs or null co_data co then
      length co_data co
   else
      length co_data co - 1;


declare co_member: (ContainerElement,ContainerElementL) -> ExtraBoolean;

procedure co_member(ce,l);
   % Container member. Returns non-[nil], if there is an container element $e$
   % in [l], such that the formula and the variable list of $e$ are equal to the
   % formula and variable list of [ce]. This procedure does not use the access
   % functions.
   l and (ce_vl ce = ce_vl car l and ce_f ce = ce_f car l
      or co_member(ce,cdr l));

procedure co_stat(co);
   begin scalar al,w; integer n;
      for each ce in co_data co do <<
	 n := length ce_vl ce;
	 w := assoc(n,al);
	 if w then cdr w := cdr w + 1 else al := (n . 1) . al
      >>;
      return sort(al,function(lambda(x,y); car x >= car y))
   end;

%DS
% JunctionL ::= (Junction, ...)
% Junction ::= QfFormula . Answer

struct JunctionL checked by listp;
struct Junction checked by pairp;


declare cl_mkJ: (QfFormula,Answer) -> Junction;

smacro procedure cl_mkJ(f,an);
   % Make junction.
   f . an;


declare cl_jF: (Junction) -> QfFormula;

smacro procedure cl_jF(j);
   % Junction formula.
   car j;


declare cl_jA: (Junction) -> Answer;

smacro procedure cl_jA(j);
   % Junction answer.
   cdr j;


declare cl_co2J: (ContainerElement) -> Junction;

smacro procedure cl_co2J(x);
   % Container to junction. Returns the S-expression [ce_f(x) . ce_ans(x)]
   cdr x;


declare cl_erTh: (EliminationResult) -> Theory;

smacro procedure cl_erTh(er);
   % Elimination result theory.
   car er;


declare cl_erEQR: (EliminationResult) -> ExtendedQeResult;

smacro procedure cl_erEQR(er);
   % Elimination result extended qe result.
   cdr er;


declare cl_mkER: (Theory,ExtendedQeResult) -> EliminationResult;

smacro procedure cl_mkER(theo,eqr);
   % Make elimination Result.
   theo . eqr;


declare cl_mk1EQR: (Formula,EquationL) -> ExtendedQeResult;

smacro procedure cl_mk1EQR(f,eql);
   % Make singleton extended qe result.
   {{f,eql}};


declare cl_gqe: (Formula,Theory,KernelL) -> TheoryFormulaPair;

procedure cl_gqe(f,theo,xbvl);
   % Generic quantifier elimination. Returns a pair $\Theta . \phi$. $\Theta$ is
   % a THEORY extending [theo] by assumptions on free variables of [f] that are
   % not in [xbvl]; $\phi$ is a formula. We have $\Theta \models [f]
   % \longleftrightarrow \phi$. $\phi$ is obtained from [f] by eliminating as
   % many quantifiers as possible. Accesses the switch [rlqepnf]; if [rlqepnf]
   % is on, then [f] must be prenex.
   begin scalar er,theo,!*rlqegen,!*rlsipw,!*rlsipo;
      !*rlsipw := !*rlqegen := T;
      er := cl_qe1(f,theo,xbvl);
      if rl_exceptionp er then
	 return er;
      theo := rl_thsimpl cl_erTh er;
      return theo . rl_simpl(caar cl_erEQR er,theo,-1)
   end;


declare cl_gqea: (Formula,Theory,ListofKernel) -> EliminationResult;

procedure cl_gqea(f,theo,xbvl);
   % Generic quantifier elimination with answer. Returns a pair $\Theta . \Phi$.
   % $\Theta$ extends [theo] by assumptions on free variables of [f] that are
   % not in [xbvl]; $\Phi$ is a list $(..., (c_i, A_i), ...)$, where the $c_i$
   % are QfFormula, and the $A_i$ are lists of equations. We have $\Theta
   % \models \bigvee_i c_i \longleftrightarrow [f]$. Whenever some $c_i$ holds
   % for an interpretation of the parameters, then [f] holds, and $A_i$
   % describes a satisfying sample point. Accesses the switch [rlqepnf]; if
   % [rlqepnf] is on, then [f] must be prenex.
   begin scalar er,!*rlqegen,!*rlsipw,!*rlsipo,!*rlqeans;
      !*rlsipw := !*rlqegen := !*rlqeans := T;
      er := cl_qe1(f,theo,xbvl);
      if rl_exceptionp er then
	 return er;
      return cl_mkER(rl_thsimpl cl_erTh er,cl_erEQR er)
   end;


declare cl_lqe: (Formula,Theory,Point) -> TheoryFormulaPair;

procedure cl_lqe(f,theo,pt);
   % Local quantifier elimination. [pt] is the suggested value for the local
   % parameter $v$. Returns a pair $\Theta . \phi$. $\Theta$ extends [theo];
   % $\phi$ is a formula. We have $\Theta \models [f] \longleftrightarrow \phi$.
   % $\phi$ is obtained from [f] by eliminating as much quantifiers as possible.
   % Accesses the switch [rlqepnf]; if [rlqepnf] is on, then [f] has to be
   % prenex. Accesses the fluids [cl_pal!*], [cl_lps!*], and [cl_theo!*].
   % [cl_lps!*] is the list of local parameters; [cl_pal] is an Alist containing
   % the suggested values for the local parameters; and [cl_theo!*] is the
   % theory generated by the local quantifier elimination.
   begin scalar w,er,theo,!*rlqelocal,!*rlsipw,!*rlsipo,cl_pal!*,cl_lps!*,
	 cl_theo!*;
      !*rlsipw := !*rlqelocal := T;
      cl_pal!* := pt;
      cl_lps!* := for each x in pt collect car x;
      cl_theo!* := nil;
      w := for each x in theo collect rl_subat(cl_pal!*,x);
      w := rl_simpl(rl_smkn('and,w),nil,-1);
      if w eq 'false then
	 rederr "rllqe: inconsistent theory";
      er := cl_qe1(f,theo,nil);
      theo := nconc(cl_theo!*,theo);
      cl_pal!* := cl_lps!* := cl_theo!* := nil;
      if rl_exceptionp er then
	 return er;
      return rl_lthsimpl(theo) . rl_simpl(caar cl_erEQR er,theo,-1)
   end;


declare cl_aqe: (Formula,Theory,Point) -> TheoryFormulaPair;

procedure cl_aqe(f,theo,pt);
   % Approximate quantifier elimination. [f] is a formula; [theo] is a
   % THEORY; [pt] is a list of equations $v=z$, where $v$ is a variable
   % and $z$ is an SQ encoding a rational number, namely the suggested
   % value for the existential variable $v$. Returns a pair $\Theta .
   % \phi$. $\Theta$ is a THEORY extending [theo]; $\phi$ is a formula.
   % Accesses the switch [rlqepnf]; if [rlqepnf] is on, then [f] must be
   % prenex. Accesses the fluids [cl_pal!*], [cl_lps!*], and
   % [cl_theo!*]. [cl_lps!*] is the list of existential variables for
   % which values are suggested; [cl_pal!*] is a corresponding ALIST
   % containing also the suggested values; [cl_theo!*] is the theory
   % generated by the local quantifier elimination. These 3 fluids have
   % been hijacked from cl_lqe, where they have got a different
   % semantics.
   begin scalar w,theo,!*rlqeapprox,cl_pal!*,cl_lps!*,cl_theo!*;
      !*rlqeapprox := t;
      cl_pal!* := pt;
      cl_lps!* := for each x in pt collect car x;
      cl_theo!* := nil;
      w := rl_simpl(rl_smkn('and,w),nil,-1);
      if w eq 'false then
	 rederr "rllqe: inconsistent theory";
      w := cl_qe1(f,theo,nil);
      theo := nconc(cl_theo!*,theo);
      w := cl_erEQR w;
      cl_pal!* := cl_lps!* := cl_theo!* := nil;
      return rl_lthsimpl(theo) . rl_simpl(w,theo,-1)
   end;


declare cl_qe: (Formula,Theory) -> Formula;

procedure cl_qe(f,theo);
   % Quantifier elimination. Returns a formula $\phi$ such that $[theo] \models
   % [f] \longleftrightarrow \phi$. $\phi$ is obtained from [f] by eliminating
   % as many quantifiers as possible. Accesses the switch [rlqepnf]; if
   % [rlqepnf] is on, then [f] has to be prenex.
   begin scalar er,!*rlsipw,!*rlsipo;
      !*rlsipw := !*rlsipo := T;
      er := cl_qe1(f,theo,nil);
      if rl_exceptionp er then
	 return er;
      return caar cl_erEQR er
   end;


declare cl_qea: (Formula,Theory) -> ExtendedQeResult;

procedure cl_qea(f,theo);
   % Quantifier elimination with answer. Returns a list of pairs $(..., (c_i,
   % A_i), ...)$. The $c_i$ are quantifier-free formulas, and the $A_i$ are
   % lists of equations. We have $[theo] \models \bigvee_i c_i
   % \longleftrightarrow [f]$. Whenever some $c_i$ holds for an interpretation
   % of the parameters, [f] holds, and $A_i$ describes a satisfying sample
   % point. Accesses the switch [rlqepnf]; if [rlqepnf] is on, then [f] has to
   % be prenex.
   begin scalar er,!*rlsipw,!*rlsipo,!*rlqeans;
      !*rlsipw := !*rlsipo := !*rlqeans := T;
      er := cl_qe1(f,theo,nil);
      if rl_exceptionp er then
	 return er;
      return cl_erEQR er
   end;


declare cl_qe1: (Formula,Theory,KernelL) -> EliminationResult;

procedure cl_qe1(f,theo,xbvl);
   % Quantifier elimination. [f] must be prenex if the switch [rlqepnf] is off;
   % [theo] serves as background theory.
   begin scalar q,ql,varl,varll,bvl,result,w,rvl,jl; integer n;
      if !*rlqepnf then
	 f := rl_pnf f;
      f := rl_simpl(f,theo,-1);
      if f eq 'inctheo then
	 return rl_exception 'inctheo;
      if not rl_quap rl_op f then
	 return cl_mkER(theo,cl_mk1EQR(f,nil));
      {ql,varll,f,bvl} := cl_split f;
      % Remove from the theory atomic formulas containing quantified variables:
      theo := for each atf in theo join
	 if null intersection(rl_varlat atf,bvl) then {atf};
      bvl := union(bvl,xbvl);
      {ql,varll,q,rvl,jl,theo} := cl_qe1!-iterate(ql,varll,f,theo,bvl);
      jl := cl_qe1!-requantify(ql,varll,q,rvl,jl);
      if !*rlqeans and null ql then <<
	 if !*rlverbose then <<
	    ioto_tprin2 "+++ Postprocessing answer:";
	    n := length jl
	 >>;
 	 result := for each j in jl join <<
	    if !*rlverbose then ioto_prin2 {" [",n:=n-1};
 	    w := cl_mk1EQR(cl_jF j,rl_qemkans cl_jA j);
	    if !*rlverbose then ioto_prin2 {"]"};
	    w
	 >>;
      >> else <<
	 f := cl_jF car jl;
	 if !*rlverbose then
	    ioto_tprin2 {"+++ Final simplification ... ",cl_atnum f," -> "};
	 f := rl_simpl(f,theo,-1);
	 if !*rlverbose then
 	    ioto_prin2t cl_atnum f;
	 if !*rlqefb and rvl then <<
	    if not rl_quap rl_op f then <<
	       if !*rlverbose then
 		  ioto_tprin2t "++++ No more quantifiers after simplification";
	       result := f
	    >> else <<
	       if !*rlverbose then
		  ioto_tprin2 {"++++ Entering fallback QE: "};
	       result := rl_fbqe f
	    >>
	 >> else
	    result := f;
	 result := cl_mk1EQR(result,nil);
      >>;
      return cl_mkER(theo,result)
   end;


declare cl_split: (Formula) -> List4;

procedure cl_split(f);
   % Split. [f] is a prenex formula. Returns a list of length 4 splitting [f]
   % into a quantifier list, a list of lists of quantified variables, the
   % matrix, and a flat list of all quantified variables.
   begin scalar q,op,ql,varl,varll,bvl;
      q := op := rl_op f;
      if not rl_quap q then
	 return {nil,nil,f,nil};
      repeat <<
   	 if op neq q then <<
      	    push(q,ql);
	    push(varl,varll);
      	    q := op;
      	    varl := nil
   	 >>;
	 push(rl_var f,varl);
	 push(rl_var f,bvl);
   	 f := rl_mat f
      >> until not rl_quap(op := rl_op f);
      push(q,ql);
      push(varl,varll);
      return {ql,varll,f,bvl}
   end;

procedure cl_unsplit(ql,varll,f);
   begin scalar res,varl;
      res := f;
      for each q in ql do <<
	 varl := pop varll;
	 for each v in varl do
	    res := rl_mkq(q,v,res)
      >>;
      return res
   end;

declare cl_qe1!-iterate: (List,List,Formula,Theory,KernelL) -> List6;

procedure cl_qe1!-iterate(ql,varll,f,theo,bvl);
   % Iteratively apply [cl_qeblock] to the quantifier blocks.
   begin scalar svrlidentify,svrlqeprecise,svrlqeaprecise,q,varl,rvl,jl;
      svrlidentify := !*rlidentify;
      jl := {cl_mkJ(f,nil)};
      while null rvl and ql do <<
      	 f := cl_jF car jl;
      	 q := pop ql;
      	 varl := pop varll;
      	 if !*rlverbose then
      	    ioto_tprin2 {"---- ",(q . reverse varl)};
	 svrlqeprecise := !*rlqeprecise;
	 svrlqeaprecise := !*rlqeaprecise;
	 if ql then <<  % Should better be an argument of qeblock ...
	    off1 'rlqeprecise;
	    off1 'rlqeaprecise
	 >>;
      	 {rvl,jl,theo} := cl_qeblock(f,q,varl,theo,!*rlqeans and null ql,bvl);
	 if ql then <<
	    onoff('rlqeprecise,svrlqeprecise);
	    onoff('rlqeaprecise,svrlqeaprecise)
	 >>;
      >>;
      onoff('rlidentify,svrlidentify);
      return {ql,varll,q,rvl,jl,theo}
   end;


declare cl_qe1!-requantify: (List,List,Quantifier,KernelL,JunctionL) -> JunctionL;

procedure cl_qe1!-requantify(ql,varll,q,rvl,jl);
   % Requantify with the variables that could not be eliminated.
   begin scalar xx,xxv,scvarll,varl;
      if not rvl then
	 return jl;
      if !*rlverbose then
	 ioto_tprin2 "+++ Requantification ... ";
      jl := for each j in jl collect <<
	 xx := cl_jF j;
	 xxv := cl_fvarl xx;
	 for each v in rvl do
	    if v memq xxv then
	       xx := rl_mkq(q,v,xx);
	 scvarll := varll;
	 for each q in ql do <<
	    varl := car scvarll;
	    scvarll := cdr scvarll;
	    for each v in varl do
	       if v memq xxv then
		  xx := rl_mkq(q,v,xx)
	 >>;
	 cl_mkJ(xx,cl_jA j)
      >>;
      if !*rlverbose then
	 ioto_prin2t "done";
      return jl
   end;


declare cl_qeblock: (QfFormula,Quantifier,KernelL,Theory,Boolean,KernelL) -> List3;

procedure cl_qeblock(f,q,varl,theo,ans,bvl);
   % Quantifier elimination for one block. The result contains the list of
   % variables for which elimination failed, the (possibly partial) elimination
   % result as a JunctionL, and the new theory.
   begin scalar rvl,jl;
      if q eq 'ex then
 	 return cl_qeblock1(rl_simpl(f,theo,-1),varl,theo,ans,bvl);
      % [q eq 'all]
      {rvl,jl,theo} := cl_qeblock1(rl_simpl(rl_nnfnot f,theo,-1),varl,theo,ans,bvl);
      return {rvl, for each x in jl collect rl_nnfnot car x . cdr x, theo}
   end;


declare cl_qeblock1: (QfFormula,KernelL,Theory,Boolean,KernelL) -> List3;

procedure cl_qeblock1(f,varl,theo,ans,bvl);
   % Quantifier elimination for one block subroutine. The result contains the
   % list of variables for which elimination failed, the (possibly partial)
   % possibly negated elimination result as a JunctionL, and the new theory.
   if !*rlqeheu then
      cl_qeblock2(f,varl,theo,ans,bvl)
   else
      cl_qeblock3(f,varl,theo,ans,bvl);


declare cl_qeblock2: (QfFormula,KernelL,Theory,Boolean,KernelL) -> List3;

procedure cl_qeblock2(f,varl,theo,ans,bvl);
   % Quantifier elimination for one block subroutine. The result contains the
   % list of variables for which elimination failed, the (possibly partial)
   % possibly negated elimination result as a JunctionL, and the new theory.
   % With [rlqeheu] on, this is in intermediate step checking for decision
   % problems and switching to DFS in the positive case.
   begin scalar !*rlqedfs,atl;
      atl := cl_atl1 f;
      !*rlqedfs := T;
      while atl do
	 if setdiff(rl_varlat car atl,varl) then
	    !*rlqedfs := atl := nil
	 else
	    atl := cdr atl;
      return cl_qeblock3(f,varl,theo,ans,bvl)
   end;

procedure cl_qeblock3(f,varl,theo,ans,bvl);
   begin scalar w; integer vlv, dpth;
      if !*rlverbose then <<
      	 if !*rlqedfs then <<
	    ioto_prin2 {" [DFS"};
	    if !*rlqedyn then
	       ioto_prin2 {" DYN"};
	    if !*rlqevbold then  <<
	       dpth := length varl;
	       vlv :=  dpth / 4;
	       ioto_prin2t {": depth ",dpth,", watching ",dpth - vlv,"]"}
	    >> else
	       ioto_prin2t {"]"}
      	 >> else
	    ioto_prin2t {" [BFS: depth ",dpth,"]"}
      >>;
      return cl_qeblock4(f,varl,theo,ans,bvl,dpth,vlv)
   end;


declare cl_qeblock4: (QfFormula,KernelL,Theory,Boolean,KernelL) -> List3;

procedure cl_qeblock4(f,varl,theo,ans,bvl,dpth,vlv);
   % Quantifier elimination for one block soubroutine. Arguments are as
   % in [cl_qeblock], where [q] has been dropped. Return value as well.
   begin scalar w,co,remvl,newj,cvl,coe,ww;
      integer c,count,delc,oldcol,comax,comaxn;
      if !*rlqegsd then
 	 f := rl_gsd(f,theo);
      cvl := varl;
      co := co_new();
      if rl_op f eq 'or then
	 for each x in rl_argn f do
	    co := co_save(co,{ce_mk(cvl,x,nil)})
      else
      	 co := co_save(co,{ce_mk(cvl,f,nil)});
      while co_data co do <<
	 if !*rlverbose and !*rlqedfs and not !*rlqevbold then <<
	    ww := car co_stat co;
	    if comax = 0 or car ww < comax or
	       (car ww = comax and cdr ww < comaxn)
	    then <<
	       comax := car ww;
	       comaxn := cdr ww;
	       ioto_prin2 {"[",comax,":",comaxn,"] "}
	    >>
	 >>;
	 if !*rlqeidentify then on1 'rlidentify;
	 coe . co := co_get co;
    	 cvl := ce_vl coe;
	 count := count + 1;
         if !*rlverbose then
   	    if !*rlqedfs then
 	       (if !*rlqevbold then <<
 	       	  if vlv = length cvl then
	       	     ioto_tprin2t {"-- crossing: ",dpth - vlv};
	       	  ioto_prin2 {"[",dpth - length cvl}
	       >>)
	    else <<
	       if c=0 then <<
	       	  ioto_tprin2t {"-- left: ",length cvl};
		  c := co_length(co) + 1
	       >>;
	       ioto_nterpri(length explode c + 4);
	       ioto_prin2 {"[",c};
	       c := c - 1
	    >>;
	 w . theo := cl_qevar(ce_f coe,ce_vl coe,ce_ans coe,theo,ans,bvl);
	 if car w then <<  % We have found a suitable variable.
	    w := cdr w;
	    if w then
	       if ce_vl car w eq 'break then <<
	       	  co := co_new();
	       	  newj := {cl_co2J car w}
	       >> else if cdr cvl then <<
		  if !*rlverbose then oldcol := co_length co;
	       	  co := co_save(co,w);
 		  if !*rlverbose then
		     delc := delc + oldcol + length w - co_length(co)
	       >> else
   		  for each x in w do newj := lto_insert(cl_co2J x,newj)
	 >> else <<
	    % There is no eliminable variable. Invalidate this entry, and save
	    % its variables for later requantification.
	    if !*rlverbose then ioto_prin2 append("[Failed:" . cdr w,{"] "});
	    remvl := union(cvl,remvl);
	    newj := lto_insert(cl_co2J coe,newj)
	 >>;
	 if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then <<
	    ioto_prin2 "] ";
	    if !*rlqedfs and null cvl then ioto_prin2 ". "
	 >>
      >>;
      if !*rlverbose then ioto_prin2{"[DEL:",delc,"/",count,"]"};
      if ans then return {remvl, newj, theo};
      % I am building the formula here rather than later because one might want
      % to do some incremental simplification at some point.
      return {remvl,
	 {cl_mkJ(rl_smkn('or,for each x in newj collect car x),nil)}, theo}
   end;


declare cl_qevar: (QfFormula,KernelL,Answer,AnswerTranslation,Theory,Boolean,KernelL) -> DottedPair;

procedure cl_qevar(f,vl,an,theo,ans,bvl);
   % Quantifier eliminate one variable. [f] is a quantifier-free formula; [vl]
   % is a non-empty list of variables; [an] is an answer; [theo] is a list of
   % atomic formulas; [ans] is Boolean. Returns a pair $a . p$. Either $a=[T]$
   % and $p$ is a pair of a list of container elements and a theory or $a=[nil]$
   % and $p$ is an error message. If there is a container element with ['break]
   % as varlist, this is the only one.
   begin scalar w,candvl,status; integer len;
      if (w := cl_transform(f,vl,an,theo,ans,bvl)) then
      	 {f,vl,an,theo,ans,bvl} := w;
      if (w := cl_gauss(f,vl,an,theo,ans,bvl)) then
	 return w;
      if (w := rl_specelim(f,vl,theo,ans,bvl)) neq 'failed then
	 return w;
      % Elimination set method
      candvl := cl_varsel(f,vl,theo);
      if !*rlverbose and !*rlqevb and (not !*rlqedfs or !*rlqevbold)
 	 and (len := length candvl) > 1
      then
	 ioto_prin2 {"{",len,":"};
      status . w := cl_process!-candvl(f,vl,an,theo,ans,bvl,candvl);
      if !*rlverbose and !*rlqevb and (not !*rlqedfs or !*rlqevbold)
 	 and len>1
      then
	 ioto_prin2 {"}"};
      if status eq 'nonocc then
	 return (t . w) . theo;
      if status eq 'failed then
	 return (nil . w) . theo;
      if status eq 'local then
      	 return (t . car w) . cl_theo!*;
      if status eq 'elim then
	 return (t . car w) . cdr w;
      rederr {"cl_qevar: bad status",status}
   end;

procedure cl_transform(f, vl, an, theo, ans, bvl);
   begin scalar w;
      for each v in vl do <<
	 w := rl_transform(v, f, vl, an, theo, ans, bvl);
	 if w then
 	    {f, vl, an, theo, ans, bvl} := w
      >>;
      return {f, vl, an, theo, ans, bvl}
   end;

procedure cl_gauss(f,vl,an,theo,ans,bvl);
   begin scalar w,ww;
      w := rl_trygauss(f,vl,theo,ans,bvl);
      if w neq 'failed then <<
	 theo := cdr w;
	 w := car w;
      	 if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then ioto_prin2 "g";
	 vl := delq(car w,vl);
	 ww := cl_esetsubst(f,car w,cdr w,vl,an,theo,ans,bvl);
	 if !*rlqelocal then
	    return (T . car ww) . cl_theo!*
	 else
	    return (T . car ww) . cdr ww
      >>
   end;

procedure cl_varsel(f,vl,theo);
   begin scalar candvl; integer len;
      if null cdr vl then
      	 candvl := vl
      else if !*rlqevarsel then
      	 candvl := rl_varsel(f,vl,theo)
      else
	 candvl := {car vl};
      return candvl
   end;


declare cl_process!-candvl: (QfFormula,KernelL,Answer,AnswerTranslation,Theory,Boolean,KernelL,KernelL) -> TaggedContainerElementL;

procedure cl_process!-candvl(f,vl,an,theo,ans,bvl,candvl);
   begin scalar w,ww,v,alp,hit,ww,status;
      while candvl do <<
	 v := pop candvl;
      	 alp := cl_qeatal(f,v,theo,ans);
      	 if alp = '(nil . nil) then <<  % [v] does not occur in [f].
      	    if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then
 	       ioto_prin2 "*";
      	    w := {ce_mk(delq(v,vl),f,ans and an)};
	    status := 'nonocc;
	    candvl := nil
      	 >> else if car alp = 'failed then
	    (if null w then <<
	       w := cdr alp;
	       status := 'failed
	    >>)
	 else <<
      	    if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then
 	       ioto_prin2 "e";
      	    ww := cl_esetsubst(f,v,rl_elimset(v,alp),delq(v,vl),an,
	       theo,ans,bvl);
	    if !*rlqelocal then <<
	       candvl := nil;
	       w := ww;
	       status := 'local
	    >> else if rl_betterp(ww,w) then <<
	       w := ww;
	       status := 'elim
	    >>
      	 >>
      >>;
      return status . w
   end;

procedure cl_esetsubst(f,v,eset,vl,an,theo,ans,bvl);
   % Elimination set substitution. [f] is a quantifier-free formula; [v] is a
   % kernel; [eset] is an elimination set; [an] is an answer; [theo] is the
   % current theory; [ans] is Boolean. Returns a pair $l . \Theta$, where
   % $\Theta$ is the new theory and $l$ is a list of container elements. If
   % there is a container element with ['break] as varlist, this is the only
   % one.
   begin scalar a,d,u,elimres,junct,bvl,w;
      while eset do <<
	 a . d := pop eset;
	 while d do <<
	    u := pop d;
	    w := apply(a,bvl . theo . f . v . u);
	    theo := union(theo,car w);
	    elimres := rl_simpl(cdr w,theo,-1);
	    if !*rlqegsd then
	       elimres := rl_gsd(elimres,theo);
	    if elimres eq 'true then <<
	       junct := {ce_mk('break,elimres,cl_updans(v,a,u,an,ans))};
	       eset := d := nil
	    >> else if elimres neq 'false then
	       if rl_op elimres eq 'or then
		  for each subf in rl_argn elimres do
		     junct := ce_mk(vl,subf,cl_updans(v,a,u,an,ans)) . junct
	       else
		  junct := ce_mk(vl,elimres,cl_updans(v,a,u,an,ans)) . junct;
      	 >>
      >>;
      return junct . theo
   end;

procedure cl_updans(v,a,u,an,ans);
   if ans then {v,a,u} . an;

procedure cl_qeatal(f,v,theo,ans);
   % Quantifier elimination atomic formula list. [f] is a formula; [v]
   % is a variable; [theo] is the current theory, [ans] is Boolean.
   % Returns an ALP.
   cl_qeatal1(f,v,theo,T,ans);

switch rlataltheo;
on1 'rlataltheo;

procedure cl_qeatal1(f,v,theo,flg,ans);
   % Quantifier elimination atomic formula list. [f] is aformula; [v] is a
   % variable; [theo] is the current theory, [flg] and [ans] are Boolean.
   % Returns an ALP. If [flg] is non-[nil] [f] has to be considered negated.
   begin scalar op,w,ww;
      op := rl_op f;
      w := if rl_tvalp op then
	 {nil . nil}
      else if op eq 'not then
      	 {cl_qeatal1(rl_arg1 f,v,theo,not flg,ans)}
      else if op eq 'and then <<
	 if !*rlataltheo then
	    for each subf in rl_argn f do
	       if cl_atfp subf and not memq(v, rl_varlat subf) then
	       	  theo := lto_insert(subf, theo);
      	 for each subf in rl_argn f collect
      	    cl_qeatal1(subf,v,theo,flg,ans)
      >> else if op eq 'or then <<
	 if !*rlataltheo then
	    for each subf in rl_argn f do
	       if cl_atfp subf and not memq(v, rl_varlat subf) then
	       	  theo := lto_insert(rl_negateat subf, theo);
      	 for each subf in rl_argn f collect
      	    cl_qeatal1(subf,v,theo,flg,ans)
      >> else if op eq 'impl then
      	 {cl_qeatal1(rl_arg2l f,v,theo,not flg,ans),
	    cl_qeatal1(rl_arg2r f,v,theo,flg,ans)}
      else if op eq 'repl then
	 {cl_qeatal1(rl_arg2l f,v,theo,flg,ans),
	    cl_qeatal1(rl_arg2r f,v,theo,not flg,ans)}
      else if op eq 'equiv then
	 {cl_qeatal1(rl_arg2l f,v,theo,not flg,ans),
	    cl_qeatal1(rl_arg2r f,v,theo,flg,ans),
            cl_qeatal1(rl_arg2l f,v,theo,flg,ans),
	    cl_qeatal1(rl_arg2r f,v,theo,not flg,ans)}
      else if rl_quap op then
	 rederr "argument formula not prenex"
      else  % [f] is an atomic formula.
      	 {rl_translat(f,v,theo,flg,ans)};
      if (ww := atsoc('failed,w)) then return ww;
      return cl_alpunion w
   end;

procedure cl_alpunion(pl);
   % Alp union. [pl] is a list of ALP's. Returns the union of all ALP's
   % in [pl].
   begin scalar uall,pall;
      for each pair in pl do <<
	 uall := car pair . uall;
	 pall := cdr pair . pall
      >>;
      return lto_alunion(uall) . lto_almerge(pall,'plus2)
   end;

procedure cl_betterp(new,old);
   begin integer atn;
      atn := cl_betterp!-count car new;
      if !*rlverbose and !*rlqevb and (not !*rlqedfs or !*rlqevbold) then
 	 ioto_prin2 {"(",atn,")"};
      return null old or atn < cl_betterp!-count car old
   end;

procedure cl_betterp!-count(coell);
   % [coell] is a list of container elements.
   for each x in coell sum rl_atnum ce_f x;

procedure cl_qeipo(f,theo);
   % Quantifier elimination in position. [f] is a positive formula;
   % [theo] is a THEORY. Returns a quantifier-free formula equivalent to
   % [f] wrt. [theo] by recursively making [f] anti-prenex and
   % eliminating the quantifiers.
   begin scalar w,!*rlqeans;
      repeat <<
	 w := cl_qeipo1(cl_apnf rl_simpl(f,theo,-1),theo);
	 f := cdr w
      >> until not car w;
      return f
   end;

procedure cl_qeipo1(f,theo);
   % Quantifier eliminate in position subroutine.
   begin scalar op,nf,a,argl,ntheo;
      op := rl_op f;
      if rl_quap op then <<
	 for each subf in theo do
	    if not(rl_var f memq rl_varlat subf) then
 	       ntheo := subf . ntheo;
	 nf := cl_qeipo1(rl_mat f,ntheo);
	 if car nf then
	    return T . rl_mkq(op,rl_var f,cdr nf);
	 a := rl_qe(rl_mkq(op,rl_var f,cdr nf),ntheo);
	 if rl_quap rl_op a then
	    rederr "cl_qeipo1: Could not eliminate quantifier";
	 return T . a
      >>;
      if rl_junctp op then <<
      	 argl := rl_argn f;
	 if op eq 'and then
	    for each subf in argl do
	       if cl_atfp subf then theo := subf . theo;
	 if op eq 'or then
	    for each subf in argl do
	       if cl_atfp subf then theo := rl_negateat subf . theo;
	 while argl do <<
	    a := cl_qeipo1(car argl,theo);
	    nf := cdr a . nf;
	    argl := cdr argl;
	    if car a then <<
	       nf := nconc(reversip nf,argl);
	       argl := nil
	    >>
	 >>;
	 return
	    if car a then
	       T . rl_mkn(op,nf)
	    else
 	       nil . rl_mkn(op,reversip nf)
      >>;
      % f is atomic.
      return nil . f
   end;

procedure cl_qews(f,theo);
   % Quantifier elimination with selection. [f] is a formula; [theo] is
   % a THEORY. Returns a quantifier-free formula equivalent to [f] wrt.
   % [theo] by selecting a quantifier from the innermost block, moving
   % it inside as far as possible and eliminating it. Accesses the
   % switch [rlqepnf]; if [rlqepnf] is on, then [f] has to be prenex.
   begin scalar q,op,ql,varl,varll,!*rlqeans;
      if !*rlqepnf then
	 f := rl_pnf f;
      f := rl_simpl(f,theo,-1);
      if not rl_quap rl_op f then
	 return f;
      {ql,varll,f} := cl_split f;  % drop bvl
      while ql do <<
	 q := pop ql;
	 varl := pop varll;
	 f := if q eq 'ex then
	    cl_qews1(varl,f,theo)
	 else
	    rl_nnfnot cl_qews1(varl,rl_nnfnot f,theo)
      >>;
      return f
   end;

procedure cl_qews1(varl,mtx,theo);
   % Quantifier eliminate with selection subroutine. [varl] is a list of
   % variables; [mtx] is a quantifier-free formula; [theo] is a list of
   % atomic formulas. Returns a formula, where all existentially
   % quantified variables from [varl] are eliminated.
   begin scalar v,w;
      while varl do <<
	 w := rl_trygauss(mtx,varl,theo,nil,nil);
	 if w eq 'failed then <<
	    v := rl_varsel(mtx,varl,theo);
 	    mtx := cl_qeipo(rl_mkq('ex,v,mtx),theo)
	 >> else <<
	    v := caar w;
	    mtx := rl_qe(rl_mkq('ex,v,mtx),theo)
	 >>;
	 varl := delete(v,varl)
      >>;
      return mtx
   end;

%DS
% <GRV> ::= ['failed] | (<KERNEL> . <ELIMINATION SET>) . <THEORY>
% <IGRV> ::= (['failed] . [nil]) |
%    ['gignore] . ([nil] . <THEORY SUPPLEMENT>) |
%    <GAUSS TYPE IDENTIFICATION> . (<ELIMINATION SET> . <THEORY SUPPLEMENT>)
% <GAUSS TYPE IDENTIFICATION> ::= ("verbose output", <DATA>,...)

procedure cl_trygauss(f,vl,theo,ans,bvl);
   % Try Gauss elimination. [f] is a quantifier-free formula; [vl] is a
   % list of variables existentially quantified in the current block;
   % [theo] a THEORY; [ans] is bool; [bvl] is a list of variables.
   % Returns a GRV, where no assumption on the variables in [bvl] are
   % made.
   begin scalar w;
      w := cl_trygauss1(f,vl,theo,ans,bvl);
      if w eq 'failed then return 'failed;
      return car w . union(cdr w,theo)
   end;

switch rlgaussdebug;

procedure cl_trygauss1(f,vl,theo,ans,bvl);
   % Try deep Gauss elimination. [f] is a quantifier-free formula; [vl] is
   % the current existential variable block; [theo] is a list of
   % atomic formulas, the current theory; [ans] is Boolean; [bvl] is a
   % list of variables that are considered non-parametric. Returns
   % a GRV.
   begin scalar w,v,csol,ev;
      csol := '(failed . nil);
      if null !*rlqevarsel then
	 vl := {car vl};
      while vl do <<
	 v := pop vl;
	 w := cl_trygaussvar(f,v,theo,ans,bvl);
	 if car w neq 'gignore and rl_bettergaussp(w,csol) then <<
	    csol := w;
	    ev := v;
	    if rl_bestgaussp csol then
	       vl := nil
	 >>
      >>;
      if car csol eq 'failed then
 	 return 'failed;
      if !*rlverbose and !*rlqevb and (not !*rlqedfs or !*rlqevbold) then
	 ioto_prin2 caar csol;
      if !*rlgaussdebug then
	 ioto_tprin2t {"DEBUG: cl_trygauss1 eliminates ", ev,
 	    " with verbose output ", caar csol};
      return (ev . cadr csol) . cddr csol
   end;

procedure cl_trygaussvar(f,v,theo,ans,bvl);
   % Try Gauss elimination wrt. one variable. [f] is a formula; [v]
   % is a kernel; [theo] is a theory; [ans] is Boolean; [bvl] is a
   % list of kernels. Returns a IGRV.
   <<
      if cl_atfp f then
	 rl_qefsolset(f,v,theo,ans,bvl)
      else if rl_op f eq 'and then
	 cl_gaussand(rl_argn f,v,theo,ans,bvl)
      else if rl_op f eq 'or then
	 cl_gaussor(rl_argn f,v,theo,ans,bvl)
      else % TODO: Gauss elimination for formulas with extended Boolean op's
	 '(failed . nil)
   >>;

switch rlgausstheo;
on1 'rlgausstheo;

procedure cl_gaussand(fl,v,theo,ans,bvl);
   begin scalar w, curr;
      if !*rlgausstheo then
      	 for each subf in fl do
	    if cl_atfp subf and not memq(v,rl_varlat subf) then
	       theo := lto_insert(subf, theo);
      curr := cl_trygaussvar(car fl,v,theo,ans,bvl);
      fl := cdr fl;
      while fl and not(rl_bestgaussp curr) do <<
      	 w := cl_trygaussvar(car fl,v,theo,ans,bvl);
	 curr := cl_gaussintersection(w,curr);
	 fl := cdr fl
      >>;
      return curr
   end;

procedure cl_gaussor(fl,v,theo,ans,bvl);
   begin scalar w,curr;
      if !*rlgausstheo then
      	 for each subf in fl do
	    if cl_atfp subf and not memq(v,rl_varlat subf) then
	       theo := lto_insert(rl_negateat subf, theo);
      curr := cl_trygaussvar(car fl,v,theo,ans,bvl);
      fl := cdr fl;
      while fl and (car curr neq 'failed) do <<
	 w := cl_trygaussvar(car fl,v,theo,ans,bvl);
	 fl := cdr fl;
	 curr := cl_gaussunion(curr,w)
      >>;
      return curr
   end;

procedure cl_gaussunion(grv1,grv2);
   begin scalar tag,eset,theo;
      if car grv1 eq 'failed or car grv2 eq 'failed then
	 return '(failed . nil);
      tag := if car grv1 eq 'gignore then
	 car grv2
      else if car grv2 eq 'gignore then
	 car grv1
      else if rl_bettergaussp(grv1,grv2) then
	 car grv2
      else
	 car grv1;
      eset := rl_esetunion(cadr grv1,cadr grv2);
      theo := union(cddr grv1,cddr grv2);
      return tag . ( eset . theo )
   end;

procedure cl_gaussintersection(grv1,grv2);
   if car grv1 eq 'gignore and car grv2 eq 'gignore then
      if length cddr grv1 < length cddr grv2 then grv1 else grv2
   else if car grv1 eq 'gignore then grv2
   else if car grv2 eq 'gignore then grv1
   else if rl_bettergaussp(grv1,grv2) then grv1 else grv2;

procedure cl_specelim(f,vl,theo,ans,bvl);
   % Special elimination. [f] is a quantifier-free formula; [vl] is a
   % list of variables existentially quantified in the current block;
   % [theo] a THEORY; [ans] is bool; [bvl] is a list of variables.
   % Returns a GRV.
   'failed;

procedure cl_fbqe(f);
   % Fallback quantifier elimination. [f] is a formula. returns a
   % formula equivalent to [f].
   <<
      if !*rlverbose then
	 ioto_tprin2t "+++ no fallback QE specified";
      f
   >>;

endmodule;  % [clqe]

end;  % of file
