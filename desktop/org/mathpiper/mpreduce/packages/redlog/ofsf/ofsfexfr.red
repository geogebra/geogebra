% ----------------------------------------------------------------------
% $Id: ofsfexfr.red 1856 2012-11-26 15:21:04Z mkosta $
% ----------------------------------------------------------------------
% Copyright (c) 2012 M. Kosta, T. Sturm
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
   fluid '(ofsf_exfr_rcsid!* ofsf_exfr_copyright!*);
   ofsf_exfr_rcsid!* :=
      "$Id: ofsfexfr.red 1856 2012-11-26 15:21:04Z mkosta $";
   ofsf_exfr_copyright!* := "(c) 2012 M. Kosta, T. Sturm"
>>;

module ofsfexfr;

load!-package 'assert;
on1 'assert;

%DS
% State ::= ('rl_state, Mode, Target, Trail, TaggedClauseList, VariableList, Level)
% Mode ::= 'models | 'vdash | nil
% Target ::= Clause | nil
% TaggedClause ::= (nil | t . Clause) (* nil means input, t means learned *)
% Level ::= Number
% Trail ::= TrailElementList
% TrailElement ::= TrailLit | VarAss
% TrailLit ::= PropLit | DecLit
% PropLit ::= ('rl_proplit, Clause | CadInput, AtomicFormula) (* AtomicFormula in Clause *)
% DecLit ::= ('rl_declit, nil, AtomicFormula)
% VarAss ::= ('rl_varass, Kernel, Anu)

procedure state_mk(m, ta, trl, tcl, vl, n);
   % [m] is 'models, 'vdash or nil; [ta] is Clause or nil; [trl] is
   % TrailElementList; [tcl] is TaggedClauseList; [vl] is a VariableList
   % containing unassigned variables; [n] is Level. Returns a state.
   {'rl_state, m, ta, trl, for each tc in tcl collect nil . tc, vl, n};

smacro procedure state_mode(s);
   cadr s;

smacro procedure state_target(s);
   caddr s; % to do with nth(s, 3)

smacro procedure state_trail(s);
   cadddr s;

smacro procedure state_tcl(s);
   car cddddr s;

smacro procedure state_vl(s);
   car cdr cddddr s;

smacro procedure state_level(s);
   car cdr cdr cddddr s;

smacro procedure trail_push(tre, trl);
   tre . trl;

smacro procedure trail_pop(trl);
   trl;

procedure tlit_mk(tag, e, atf);
   % [tag] is 'rl_declit or 'rl_proplit; [e] is a Clause or CadInput or [nil];
   % [atf] is an atomic formula. Returns TrailLit.
   {tag, e, atf};

smacro procedure tlit_tag(tl);
   car tl;

smacro procedure tlit_e(tl);
   cadr tl;

smacro procedure tlit_atf(tl);
   caddr tl;

smacro procedure tre_varassp(tre);
   eqcar(tre, 'rl_varass);

smacro procedure tre_declitp(tre);
   eqcar(tre, 'rl_declit);

smacro procedure tre_proplitp(tre);
   eqcar(tre, 'rl_proplit);

smacro procedure tre_litp(tre);
   tre_proplitp tre or tre_declitp tre;

procedure tre_lit(tre);
   cadr tre;

smacro procedure varass_mk(k, anu);
   {'rl_varass, k, anu};

smacro procedure varass_k(va);
   cadr va;

smacro procedure varass_value(va);
   caddr va;

smacro procedure declit_mk(lit);
   {'rl_declit, lit};

smacro procedure proplit_mk(lit);
   {'rl_proplit, lit};

procedure ofsf_selectClause(s);
   begin scalar tcl, c;
      if state_mode s then
	 return nil;
      % TODO: Is there a strategy for selecting c?
      tcl := state_tcl s;
      while tcl do <<
	 c := cdr pop tcl;
	 if ofsf_undef(c, state_trail s) then
	    tcl := nil;
      >>;
      if not c then
	 return nil;
      % Rule is applicable.
      return state_mk('models, c, state_trail s, state_tcl s, state_vl s,
	 state_level s)
   end;

procedure ofsf_conflict(s);
   begin scalar tcl, c;
      if state_mode s then
	 return nil;
      % TODO: Is there a strategy for selecting c?
      tcl := state_tcl s;
      while tcl do <<
	 c := cdr pop tcl;
	 if ofsf_value(c, state_trail s) eq 'false then
	    tcl := nil;
      >>;
      if not c then
	 return nil;
      % Rule is applicable.
      return state_mk('vdash, c, state_trail s, state_tcl s, state_vl s,
	 state_level s)
   end;

procedure ofsf_sat(s);
   begin scalar eql;
      if state_mode s or state_vl s then
	 return nil;
      eql := for each tre in state_trail s join
	 if tre_varassp tre then
	    {tre};
      return state_mk('sat, nil, eql, nil, state_vl s, state_level s + 1)
   end;

procedure ofsf_liftLevel(s);
   begin scalar xk, a, vl;
      if state_mode s then
	 return nil;
      vl := state_vl s;
      xk := pop vl;
      a := ofsf_feasible(state_trail s);
      if not a then
	 return nil;
      state_mk(nil, nil,
	 trail_push(state_trail s, varass_mk(xk, a)),
 	 state_tcl s, vl, state_level s + 1)
   end;

procedure ofsf_feasible(trl);
   % [trl] is a trail. Returns a finite list of intervals with Anu bounds.
   begin scalar fl, gl, assal, lit;
      for each tre in trl do
	 if tre_varassp tre then
	    assal := (varass_k tre . varass_value tre) . assal
	 else <<  % we know tre_litp tre
	    lit := tre_lit tre;
	    if ofsf_op lit eq 'equal then
	       fl := lit . fl
	    else
	       gl := lit . gl
	 >>;
      fl := for each f in fl collect
	 ofsf_anusubf(ofsf_arg2l f, assal) . 'equal;
      gl := for each g in gl collect
	 ofsf_anusubf(ofsf_arg2l g, assal) . ofsf_op g;
      return if fl then
	 ofsf_feasible1(fl, gl)
      else
	 ofsf_feasible2(gl)
   end;

procedure ofsf_feasible1(fl, gl);
   % [fl] is a list of pairs [f . 'equal] where [f] is an Aex; [gl] is a list of
   % pairs [f . op] where [f] is an Aex and [op] is an ofsf operator different
   % from ['equal].
   begin scalar xk, f, l, fidl;
      f := car pop fl;
      fidl := aex_freeids f;
      assert(fidl and not cdr fidl);
      xk := car fidl;
      l := for each anu in aex_findroots(f, xk) join
	 if ofsf_feasible11(anu, xk, fl, gl) then
	    {anu};
      return l
   end;

procedure ofsf_feasible11(anu, xk, fl, gl);
   begin scalar cnt, f, g, op;
      cnt := t; while cnt and fl do <<
	 f := car pop fl;
	 if not aex_nullp aex_bind(f, xk, anu) then
	    cnt := nil
      >>;
      while cnt and gl do <<
	 g . op := pop gl;
	 if not aex_evalsgn(aex_bind(g, xk, anu), op) then
	    cnt := nil
      >>;
      return cnt
   end;

procedure ofsf_feasible2(gl);
   % [gl] is a list of pairs [f . op] where [f] is an Aex and [op] is an ofsf
   % operator different from ['equal].
   ;

procedure aex_evalsgn(aex, op);
   % [aex] is a constant Aex; [op] is an ofsf operator. Returns Boolean.
   begin scalar sgn;
      assert(aex_constp aex);
      sgn := aex_sgn aex;
      if eqn(sgn, 0) then
	 sgn := nil;
      return ofsf_evalatp(op, sgn)
   end;

procedure ofsf_forget(s);
   begin scalar tcl, tc, cnt, ntcl;
      if state_mode s then
	 return nil;
      % TODO: Is there a strategy for selecting c?
      tcl := state_tcl s;
      cnt := t; while cnt and tcl do <<
	 tc := pop tcl;
	 if not car tc then
	    ntcl := tc . ntcl
	 else <<
	    cnt := nil;
	    ntcl := nconc(reversip ntcl, tcl);
	 >>
      >>;
      if cnt then % No learned clauses.
	 return nil;
      % Rule is applicable.
      return state_mk(nil, nil, state_trail s, ntcl, state_vl s, state_level s)
   end;

procedure ofsf_undefp(c, trl);
   % [c] is a Clause; [trl] is a Trail.
   begin scalar avl, at, defp;
      avl := for each tre in trl join
	 if tre_varassp tre then
	    {varass_k tre};
      defp := t; while defp and c do <<
	 at := pop c;
	 if not lto_subsetq(ofsf_varlat at, avl) then
	    defp := nil
      >>;
      return not defp
   end;

procedure ofsf_value(c, trl);
   begin scalar eqal, cnt, at, e;
      if ofsf_undefp(c, trl) then
      	 return 'undef;
      eqal := for each tre in trl join
	 if tre_varassp tre then
	    {varass_k tre . varass_value tre};
      cnt := t; while cnt and c do <<
	 at := pop c;
	 if ofsf_valueat(at, eqal) eq 'true then
	    cnt := nil
      >>;
      return if cnt then 'false else 'true
   end;

procedure ofsf_valueat(at, eqal);
   if ofsf_evalatp(ofsf_op at, ofsf_subalf(ofsf_arg2l at, eqal)) then
      'true
   else
      'false;

procedure ofsf_subalf(f, al);
   % [f] is an SF; [al] is an Alist. Returns an SQ. The keys of [al]
   % are a superset of the variables in [f]; the values are SQs.
   begin scalar nred, nlc;
      if domainp f then
      	 return !*f2q f;
      nred := ofsf_subalf(red f, al);
      nlc := ofsf_subalf(lc f, al);
      return addsq(multsq(nlc, exptsq(cdr atsoc(mvar f, al), ldeg f)), nred)
   end;

procedure ofsf_anusubf(f, al);
   % [f] is a SF; [al] is an Alist, where the keys are kernels and the values
   % are Anu. Returns an Aex.
   begin scalar aex;
      aex := aex_fromrp ratpoly_fromsf f;
      for each pr in al do
	 aex := aex_bind(aex, car pr, cdr pr);
      return aex
   end;

endmodule;

end;  % of file
