% ----------------------------------------------------------------------
% $Id: clprl.red 1277 2011-08-17 10:02:14Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2001, 2011 Thomas Sturm
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
   fluid '(clprl_rcsid!* clprl_copyright!*);
   clprl_rcsid!* := "$Id: clprl.red 1277 2011-08-17 10:02:14Z thomas-sturm $";
   clprl_copyright!* :=  "Copyright (c) 2001, 2011 T. Sturm"
>>;

module clprl;

load!-package 'redlog;
load!-package 'dvfsf;
load!-package 'ofsf where !*msg=nil;

rl_set '(ofsf);

fluid '(!*nat !*rlverbose !*clprlverbose !*clprlproject clprl_ressteps!* !*msg);

switch clprlverbose,clprlproject;

on1 'clprlverbose;
on1 'clprlproject;
off1 'rlverbose;

algebraic infix hornand;
algebraic precedence equiv,hornand;
newtok '((!&) hornand) where !*msg=nil;
flag('(hornand),'spaced);
flag('(hornand),'nary);

algebraic infix hornrepl;
put('hornrepl,'number!-of!-args,2);
put('hornrepl,'rtypefn,'quotehorn);
algebraic precedence hornand,hornrepl;
newtok '((!: !-) hornrepl) where !*msg=nil;
flag('(hornrepl),'spaced);

put('!*hornclause,'rtypefn,'quotehorn);
put('!*hornclause,'prifn,
   function(lambda x; maprin clprl_prephornclause cadr x));

procedure quotehorn(x);
   'horn;

put('horn,'evfn,'clprl_reval);

put('clp,'psopfn,'clprl_clp0);

% endmodule;  % clprl


% module clprlhornclause;

procedure hc_mk(head,body,constr);
   % Horn clause make. [head] is a GOAL, [body] is a list of GOAL's,
   % [constr] is a FORMULA. Returns a HORNCLAUSE.
   {head,body,constr};

procedure hc_head(hc);
   % Horn clause head. [hc] is a HORNCLAUSE. Returns a GOAL, the head
   % of [hc].
   car hc;

procedure hc_body(hc);
   % Horn clause body. [hc] is a HORNCLAUSE. Returns a list of GOAL's,
   % the body of [hc].
   cadr hc;

procedure hc_constr(hc);
   % Horn clause constraint. [hc] is a HORNCLAUSE. Returns a FORMULA,
   % the constraint of [hc].
   caddr hc;

procedure goal_mk(op,argl);
   % Goal make. [op] is a predicate symbol, [argl] is a list of SQ's.
   % Returns a GOAL.
   op . argl;

procedure goal_op(goal);
   % Goal operator. [goal] is a GOAL. Returns an OPERATOR.
   car goal;

procedure goal_argl(goal);
   % Goal operator. [goal] is a GOAL. Returns a list of SQ's.
   cdr goal;

% endmodule;  % clprlhornclause


% module clprami;

procedure clprl_simp(u);
   % Constraint logic programming [simp] horn clause. [u] is the Lisp
   % Prefix form of a horn clause. Returns a HORNCLAUSE.
   begin scalar op,head,w;
      if atom u then
	 return clprl_simpatom u;
      op := car u;
      if op eq '!*hornclause then
	 return cadr u;
      head := clprl_simphead cadr u;
      w := clprl_simpbody caddr u;
      return hc_mk(head,car w,cdr w)
   end;

procedure clprl_simpatom(u);
   % Constraint logic programming [simp] horn clause. [u] is a Lisp
   % atom. Returns a HORNCLAUSE.
   begin scalar w;
      if null u then typerr("nil","horn clause");
      if numberp u then typerr({"number",u},"horn clause");
      if stringp u then typerr({"string",u},"horn clause");
      if (w := rl_gettype u) then <<
	 if w eq 'horn then
	    return clprl_simp cadr get(u,'avalue);
	 typerr({w,u},"horn clause")
      >>;
      if boundp u then return eval u;
      typerr({"unbound id",u},"horn clause")
   end;

procedure clprl_simphead(head);
   % Constraint logic programming [simp] horn clause head. [head] is
   % the Lisp Prefix form of a horn clause head. Returns a GOAL.
   begin scalar op;
      if head eq 'false then
	 return head;
      if head eq 'true then
      	 rederr "syntax error: `true' invalid as horn clause head";
      if atom head then
	 rederr "syntax error: Lisp atom as horn clause head";
      op := car head;
      if rl_boolp op or rl_quap op or ofsf_opp op or dvfsf_opp op then
	 rederr "syntax error: constraint in horn clause head";
      if op eq 'hornand then
	 rederr "syntax error: conjunctive horn clause head";
      return goal_mk(car head,for each arg in cdr head collect simp arg)
   end;

procedure clprl_simpbody(body);
   % Constraint logic programming [simp] horn clause body. [body] is
   % the Lisp Prefix form of a horn clause body, possibly mixed with
   % several horn constraints. Returns a pair, where the [car] is a
   % list of GOAL's and the [cdr] is a FORMULA.
   if body eq 'false then
      rederr "syntax error: `false' invalid in horn clause body"
   else if body eq 'true then
      nil . 'true
   else if atom body then
      rederr "syntax error: Lisp atom in horn clause body"
   else
      clprl_splithcbody body;

procedure clprl_splithcbody(body);
   % Constraint logic programming split horn clause body. [body] is
   % the Lisp Prefix form of a horn clause body, possibly mixed with
   % several horn constraints; we know that [body] is not a Lisp atom.
   % Returns a pair, where the [car] is a list of GOAL's and the [cdr]
   % is a FORMULA.
   begin scalar op,goall,constrl,w;
      op := car body;
      if op eq 'hornand then <<
	 for each x in cdr body do <<
	    w := clprl_splithcbody1 x;
	    if car w then
	       goall := car w . goall;
	    if cdr w then
	       constrl := cdr w . constrl
	 >>;
	 return goall . rl_smkn('and,constrl)
      >>;
      w := clprl_splithcbody1 body;
      return (if car w then {car w}) . (cdr w or 'true)
   end;

procedure clprl_splithcbody1(goc);
   % Constraint logic programming split horn clause body subroutine.
   % [goc] is the Lisp Prefix form of either a GOAL or a FORMULA; we
   % know that [goc] is not a Lisp atom. Returns a pair, where the
   % [car] is either a GOAL or [nil, and the [cdr] is either a FORMULA
   % or [nil]; one of [car], [cdr] is [nil].
   begin scalar op;
      op := car goc;
      if rl_cxp op or ofsf_opp op or dvfsf_opp op then
	 return nil . rl_simp goc;
      return (car goc . for each x in cdr goc collect clprl_ssimp x) . nil
   end;

procedure clprl_ssimp(u);
   % Constraint logic programming save [simp]. [u] is Lisp prefix.
   % Returns an SQ. Return [simp u] but raising an error in case of a
   % parametric denominator.
   begin scalar w;
      w := simp u;
      if not domainp denr w then
	 rederr "Syntax error: Parametric denominator in predicate";
      return w
   end;

procedure clprl_reval(u,v);
   % Constraint logic programming [reval] horn clause. [u] is a
   % (pseudo) Lisp prefix representation of a HORNCLAUSE; [v] is BOOL.
   % Returns evaluated Lisp prefix of [u] for non-[nil] [v], evaluated
   % pseudo Lisp prefix else.
   if v then
      clprl_prephornclause clprl_simp u
   else
      clprl_mk!*hornclause clprl_simp u;

procedure clprl_prephornclause(hc);
   % Constraint logic programming [prep] horn clause. [hc] is a
   % HORNCLAUSE. Returns a Lisp prefix representation for [hc].
   begin scalar w;
      w := for each gl in hc_body hc collect clprl_prephorngoal gl;
      if null w or hc_constr hc neq 'true then
	 w := nconc(w,{rl_prepfof hc_constr hc});
      w := if cdr w then 'hornand . w else car w;
      return {'hornrepl,clprl_prephorngoal hc_head hc,w}
   end;

procedure clprl_prephorngoal(gl);
   % Constraint logic programming [prep] horn goal. [gl] is a GOAL.
   % Returns a Lisp prefix representation for [gl].
   if atom gl then
      gl
   else
      car gl . for each arg in cdr gl collect prepsq arg;

procedure clprl_mk!*hornclause(hc);
   % Constraint logic programming make horn clause. [hc] is a
   % HORNCLAUSE. Returns a pseudo Lisp prefix representation for [hc].
   {'!*hornclause,hc};

% endmodule;  % clprlami;


% module clprlclp;

procedure state_mk(goall,constr);
   % STATE make. [goall] is a list of GOAL's; [constr] is a FORMULA.
   % Returns a [state]
   goall. constr;

procedure state_goall(state);
   % STATE goal list. [state] is a STATE. Returns the list of GOALS
   % from state.
   car state;

procedure state_constr(state);
   % STATE constraint. [state] is a STATE. Returns the CONSTRAINT from
   % state.
   cdr state;

procedure clprl_clp0(l);
   % Constraint logic programming [psopfn] entry point. [l] is an
   % unevaluated AM argument containing a PROGRAM and a QUERY. Returns
   % a pseudo Lisp prefix FORMULA.
   begin scalar progm,query,state;
      progm := for each hc in cdr aeval car l collect
	 clprl_simp hc;
      query := clprl_simp cadr l;
      if hc_head query neq 'false then
	 rederr "syntax error: non-false query head";
      state := state_mk(hc_body query,hc_constr query);
      return rl_mk!*fof clprl_clp(progm,state)
   end;

procedure clprl_clp(progm,state);
   % Constraint logic programming entry point. [prog] is a PROGRAM;
   % [state] is an initial STATE. Returns a FORMULA.
   begin scalar varl,goalvarl,varal,res;
      for each hc in progm do
	 varl := union(varl,clprl_hcvarl hc);
      varal := for each v in varl collect v . 0;
      for each hc in state_goall state do
      	 goalvarl := union(goalvarl,clprl_horngoalvarl hc);
      if !*clprlverbose then
      	 clprl_ressteps!* := 0;
      res := clprl_clp1(progm,state,varal,goalvarl);
      if !*clprlverbose then
	 ioto_tprin2t {"Number of resolution steps: ",clprl_ressteps!*};
      return cl_qe(cl_ex(state_constr res,goalvarl),nil)
   end;

procedure clprl_hcvarl(hc);
   % Constraint logic programming horn clause variable list. [hc] is a
   % HORNCLAUSE. Returns a list, the set of variables contained in
   % [hc].
   union(clprl_horngoalvarl hc_head hc,
      union(clprl_horngoallvarl hc_body hc,cl_fvarl hc_constr hc));

procedure clprl_horngoallvarl(gll);
   % Constraint logic programming horn goal list variable list. [gll]
   % is a list of GOAL'S. Returns a list, the set of variables
   % contained in the GOAL's in [gll].
   begin scalar varl;
      for each goal in gll do
      	 varl := union(varl,clprl_horngoalvarl goal);
      return varl
   end;

procedure clprl_horngoalvarl(gl);
   % Constraint logic programming horn goal variable list. [gl] is a
   % GOAL. Returns a list, the set of variables contained in [gl].
   begin scalar varl;
      if atom gl then
	 return nil;
      for each arg in cdr gl do
	 varl := union(varl,kernels numr arg);
      return varl
   end;

procedure clprl_clp1(progm,state,varal,goalvarl);
   % Constraint logic programming main procedure. [prog] is a PROGRAM;
   % [state] is a STATE; [varal] is an ALIST where the keys are the
   % variables in [prog] and [state] and the values are used for
   % constructing suitable numbers for standardizing apart; [goalvarl]
   % is a list, the set of variables in the initial QUERY. Returns a
   % STATE.
   begin
      scalar a,gll,c,op,scprog,cnt,hc,hcop,hcop,hchead,unif,newgll,newc,w,rec;
      state := clprl_simplstate(state,goalvarl);
      if !*clprlverbose then
	 clprl_pristate state;
      if clprl_finalstatep state then
	 return state;
      a := car state_goall state;
      gll := cdr state_goall state;
      c := state_constr state;
      op := goal_op a;
      scprog := progm;
      cnt := t; while cnt and scprog do <<
	 hc := car scprog;
	 scprog := cdr scprog;
	 hcop := goal_op hc_head hc;
	 if op eq hcop then <<
	    w := clprl_stapart(hc,varal);
	    hc := car w;
	    varal := cdr w;
	    if !*clprlverbose then
	       clprl_prihornclause hc;
	    hchead := hc_head hc;
	    unif := clprl_unif(goal_argl a,goal_argl hchead);
	    if clprl_compatiblep(unif,c) then <<
	       if !*clprlverbose then <<
		  clprl_ressteps!* := clprl_ressteps!* + 1;
	       	  ioto_tprin2t "Match:";
		  maprin rl_prepfof unif;
		  terpri!* t
	       >>;
	       newgll := append(hc_body hc,gll);
	       newc := rl_mkn('and,{unif,c,hc_constr hc});
	       rec := clprl_clp1(progm,state_mk(newgll,newc),varal,goalvarl);
	       if  clprl_successfulstatep rec then
		  cnt := nil
	    >>
	 >>
      >>;
      return rec or state_mk(state_goall state,'false)
   end;

procedure clprl_simplstate(state,goalvarl);
   % Constraint logic programming simplify state. [state] is a STATE,
   % [goalvarl] is the list of variables in the initial QUERY. Returns
   % a STATE equivalent to [state] but with a simpler CONSTRAINT.
   state_goall state . if !*clprlproject then
      cl_qe(cl_ex(state_constr state,
	 nconc(clprl_horngoallvarl state_goall state,goalvarl)),nil)
   else
      cl_simpl(state_constr state,nil,-1);

procedure clprl_finalstatep(state);
   % Constraint logic programming final state predicate. [state] is a
   % STATE. Returns non-[nil] iff [state] is a final state.
   null state_goall state or state_constr state eq 'false;

procedure clprl_successfulstatep(state);
   % Constraint logic programming successful state predicate. [state]
   % is a STATE. Returns non-[nil] iff [state] is a successful final
   % state.
   null state_goall state and state_constr state neq 'false;

procedure clprl_stapart(hc,varal);
   % Constraint logic programming standardize apart. [hc] is a
   % HORNCLAUSE; [varal] is an ALIST where the keys are the variables
   % in the program and the current state, and the values are used for
   % constructing suitable numbers for standardizing apart. Returns a
   % pair, where the [car] is an instance of [hc] with fresh variable,
   % and the [cdr] is the updated [varal].
   begin scalar nvaral,subal,a,n;
      nvaral := varal;
      subal := for each v in clprl_hcvarl hc collect <<
	 a := atsoc(v,varal);
	 n := cdr a + 1;
      	 nvaral := (car a . n) . delq(a,nvaral);
      	 v . intern compress lto_nconcn {{'!!,'!_},explode v,{'!_},explode n}
      >>;
      if null subal then
	 return hc . varal;
      hc := clprl_stapart1(hc,subal);
      return hc . nvaral
   end;

procedure clprl_stapart1(hc,subal);
   % Constraint logic programming standardize apart subroutine. [hc]
   % is a HORNCLAUSE; [subal] is a SUBSTITUTION ALIST. Returns a
   % HORNCLAUSE obtained from [hc] by applying the substitutions in
   % [subal].
   hc_mk(clprl_substgoal(hc_head hc,subal),
      for each gl in hc_body hc collect clprl_substgoal(gl,subal),
      cl_subfof(subal,hc_constr hc));

procedure clprl_substgoal(goal,subal);
   % Constraint logic programming substitute into goal. [goal] is a
   % GOAL; [subal] is a SUBSTITUTION ALIST. Returns a GOAL obtained
   % from [goal] by applying the substitutions in [subal].
   if atom goal then
      goal
   else
      goal_mk(goal_op goal,
	 for each arg in goal_argl goal collect subsq(arg,subal));

procedure clprl_unif(argl1,argl2);
   % Constraint logic programming unify. [argl1] and [argl2] are lists
   % of SQ's. Returns a FORMULA with pairwise equations for the
   % corresponding arguments in [argl1] and [argl2].
   begin scalar unifl,a;
      unifl := for each x in argl1 collect <<
	 a := car argl2;
	 argl2 := cdr argl2;
	 clprl_mkequation(x,a)
      >>;
      return rl_smkn('and,unifl)
   end;

procedure clprl_mkequation(x,a);
   % Constraint logic programming make equation. [x] and [a] are SQ's.
   % Returns a FORMULA equivalent to $[x]=[a]$.
   ofsf_0mk2('equal,addf(multf(denr a,numr x),negf multf(denr x,numr a)));

procedure clprl_compatiblep(unif,c);
   % Constraint logic programming compatible predicate. [unif] and [c]
   % are FORMULAS. Returns non-[nil] iff $[unif] \land [c]$ is
   % satisfyable.
   cl_qe(cl_ex(rl_mkn('and,{unif,c}),nil),nil) eq 'true;

procedure clprl_pristate(state);
   % Constraint logic programming print state. [state] is a STATE.
   % Print [state] in AM style.
   <<
      ioto_tprin2t {"Goals:"};
      for each gl in state_goall state do
	 maprin clprl_prephorngoal gl;
      terpri!* t;
      ioto_tprin2t {"Constraint:"};
      maprin rl_mk!*fof state_constr state;
      terpri!* t
   >>;

procedure clprl_prihornclause(hc);
   % Constraint logic programming print horn clause. [hc] is a
   % HORNCLAUSE. Print [hc] in AM style.
   <<
      maprin clprl_prephornclause hc;
      terpri!* t
   >>;

endmodule;

end;  % of file
