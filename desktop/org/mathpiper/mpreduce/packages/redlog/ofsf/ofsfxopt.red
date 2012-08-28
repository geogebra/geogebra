% ----------------------------------------------------------------------
% $Id: ofsfxopt.red 1713 2012-06-22 07:42:38Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1998-2009 Andreas Dolzmann
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
   fluid '(ofsf_xopt_rcsid!* ofsf_xopt_copyright!*);
   ofsf_xopt_rcsid!* :=
      "$Id: ofsfxopt.red 1713 2012-06-22 07:42:38Z thomas-sturm $";
   ofsf_xopt_copyright!* := "Copyright (c) 1998-2009 A. Dolzmann"
>>;

module ofsfxopt;
% Ordered field standard form extended optimization. A very restricted
% form of the QE by virtual substitution. Pnf of input must be an
% existential quantified weak parametric linear formula containing only
% weak relations.

% Global variables: used only for statistics
% fluid '(ofsf_xopt!-nodes!* ofsf_xopt!-delnodes!* ofsf_xopt!-plnodes!*
%   ofsf_xopt!-fnodes!* ofsf_xopt!-thcof!* !*rlxoptqe);
%
% Switches used by xopt:
% on1 'rlxopt;      % ofsf uses xopt procedures
% on1 'rlxoptsb;    % select boundary type
% on1 'rlxoptpl;    % passive list
% on1 'rlxoptri;    % result inheritance
% off1 'rlxoptric;  % result inheritance to conatiner
% off1 'rlxoptrir;  % result inheritance to result
% on1 'rlxoptses;   % structural elimination sets.

%DS
% AnswerList ::= (..., Answer, ...)
% <Answer> ::= (<Guard> . <Point>)
% <Guard> ::= <Quantifier-Free Formula>
% <Point> ::= (..., <Ct>, ...)
% <Ct> ::= (<Variable> . <Value>)
% <Value> ::= <LispPrefix>

smacro procedure ofsf_xopt!-ansl!-mk(l);
   % Answer list make. [l] is a list. Returns an <Answer List>.
   l;

smacro procedure ofsf_xopt!-ansl!-ansl(ansl);
   % Answer list extract answer list. [ansl] is an <Answer List>. Returns a list
   % of <Answer>.
   ansl;

smacro procedure ofsf_xopt!-ans!-mk(gd,pt);
   % Answer make. [gd] is a <Guard>, [pt] is a <Point>. Returns an <Answer>.
   gd . pt;

smacro procedure ofsf_xopt!-ans!-gd(ans);
   % Answer extract guard. [ans] is an ANS. Returns a GD.
   car ans;

smacro procedure ofsf_xopt!-ans!-pt(ans);
   % Answer extract point. [ans] is an ANS. Returns a PT.
   cdr ans;

smacro procedure ofsf_xopt!-pt!-ctl(pt);
   % Point extract CTL. [pt] is PT. Returns a list of CT.
   pt;

smacro procedure ofsf_xopt!-pt!-mk(ctl);
   % Point make. [ctl] is a list of CT. Returns a PT.
   ctl;

smacro procedure ofsf_xopt!-ct!-mk(v,vl);
   % CT make. [v] is a variable, [vl] is Lisp prefix. Returns a CT.
   v . vl;

smacro procedure ofsf_xopt!-ct!-var(ct);
   % CT variable. [ct] is a CT. Returns a variable.
   car ct;

smacro procedure ofsf_xopt!-ct!-value(ct);
   % CT value. [ct] is a CT. Returns Lisp prefix.
   cdr ct;

%DS
% <CO> ::= (..., <CE>, ...)
% <CE> ::= (<VL>, <FORMULA>, <PT>, <PL>)
% <VL> ::= <List of variables> (* to be eliminated *)
% <FORMULA> ::= <Redlog formula>
% <PL> ::= (..., <Atomic formula>, ...)

smacro procedure ofsf_xopt!-co!-mk(cel);
   % Container make. [cel] is a CE. Returns a CO.
   cel;

smacro procedure ofsf_xopt!-co!-cel(co);
   % Container extract container element list. [co] is a CO. Returns a
   % list of CE.
   co;

smacro procedure ofsf_xopt!-ce!-mk(vl,f,pt,pl);
   % Container element make. [vl] is a VL, [f] is a formula, [pt] is a
   % PT and [pl] is a PL. Returns a CE.
   {vl,f,pt,pl};

smacro procedure ofsf_xopt!-ce!-vl(ce);
   % Container element extract variable list. [ce] is a CE. Returns a
   % VL.
   car ce;

smacro procedure ofsf_xopt!-ce!-f(ce);
   % Container element extract formula. [ce] is a CE. Returns a formula.
   cadr ce;

smacro procedure ofsf_xopt!-ce!-pt(ce);
   % Container element extract point. [ce] is a CE. Returns a PT.
   caddr ce;

smacro procedure ofsf_xopt!-ce!-pl(ce);
   % Container element extract PL. [ce] is a CE. Returns a PL.
   cadddr ce;

smacro procedure ofsf_xopt!-co!-put(co,ce);
   % Container put. [co] is a CO, [ce] is a CE. Returns a CO.
   ce . co;

procedure ofsf_xopt!-co!-putl(co,cel);
   % Container put list. [co] is a CO. [cel] is a list of CE. Returns a
   % CO.
   for each ce in cel collect ofsf_xopt!-co!-put(co,ce);

smacro procedure ofsf_xopt!-co!-get(co);
   % Container get. [co] is a CO. Returns a CO.
   co;  % car co . cdr co;

smacro procedure ofsf_xopt!-co!-length(co);
   % Container length. [co] is a CO. Returns a number.
   length co;

%DS
% <CS> ::= (<UBL>, <LBL>, <EQL>)
% <UBL> ::= (..., <CP>, ...)  % "upper bounds"
% <LBL> ::= (..., <CP>, ...)  % "lower bounds"
% <EQL> ::= (..., <CP>, ...)  % "equations"
% <CP> ::= "minimal polynomial" | minf | pinf

smacro procedure ofsf_xopt!-cs!-mk(ubl,lbl,eql);
   % CS make. [ubl] us an UBL, [lbl] is an LBL, [eql] is an EQL. Returns
   % a CS.
   {ubl,lbl,eql};

smacro procedure ofsf_xopt!-cs!-ubl(cs);
   % CS extract upper bound list. [cs] is a CS. Returns a UBL.
   car cs;

smacro procedure ofsf_xopt!-cs!-lbl(cs);
   % CS extract upper lower list. [cs] is a CS. Returns an LBL.
   cadr cs;

smacro procedure ofsf_xopt!-cs!-eql(cs);
   % CS extract equation list. [cs] is a CS. Returns EQL.
   caddr cs;

smacro procedure ofsf_xopt!-cp!-mk(p);
   % CP make. Returns a CP.
   p;

smacro procedure ofsf_xopt!-cp!-p(cp);
   % CP extract p. Returns a CP.
   cp;

procedure ofsf_xopt!-cs!-null(cs);
   % CS null. [cs] is a CS. Returns extended Boolean.
   null car cs and null cadr cs and null caddr cs;

%DS
% <ES> ::= (...,<CP>,...)

smacro procedure ofsf_xopt!-es!-mk(cpl);
   % Elimination set make. [cpl] is a list of CP. Returns an ES.
   cpl;

smacro procedure ofsf_xopt!-es!-cpl(es);
   % Elimination set CP list. [es] is an elimination set. Returns a list
   % of CP.
   es;

procedure ofsf_xopt!-check(f);
   % Check. [f] is a formula. Returns non-[nil] if [f] can be eliminated
   % by using xopt.
   begin scalar !*rlsiatadv,!*rlsitsqspl,!*rlsifac,!*rldavgcd,!*rlsipd,
	 !*rlsipw;
      !*rlsipw := T;
      return ofsf_xopt!-check1(cl_simpl(cl_pnf f,nil,-1),nil,T)
   end;

procedure ofsf_xopt!-check1(f,vl,p);
   % Check subroutine. [f] is a formula, [vl] is a list of variables,
   % [p] is boolean. Returns non-[nil] if [f] can be eliminated by using
   % xopt.
   begin scalar op,argl,r;
      if f eq 'true or f eq 'false then
	 return nil;
      op := rl_op f;
%       if op eq 'ex or op eq 'all then
      if op eq 'ex  then
	 return p and ofsf_xopt!-check1(rl_mat f,rl_var f . vl,T);
      if op eq 'all  then
      	 return nil;
      if rl_cxp op then <<
      	 argl := rl_argn f;
	 r := t;
      	 while argl and r do <<
	    r := ofsf_xopt!-check1(car argl,vl,nil);
	    argl := cdr argl
	 >>;
      	 return r
      >>;
      return ofsf_op f memq '(leq,geq,equal) and
	 sfto_linwpp(ofsf_arg2l f,vl)
   end;

procedure ofsf_xopt!-qea(f);
   % Quantifier elimination with answer. [f] is an existentially
   % quantified, weak parametric, linear formula with only weak
   % relations. Returns a list of pairs $(..., (c_i, A_i), ...)$. The
   % $c_i$ are quantifier-free formulas, and the $A_i$ are lists of
   % equations. Entry point for ofsf_qea.
   begin scalar !*rlxoptqe;
      return ofsf_xopt!-trans!-ansl ofsf_xopt!-xopt f
   end;

procedure ofsf_xopt!-trans!-ansl(u);
   % Translate ansl. [u] is a ANSL. Returns a answer as required by
   % [cl_qea].
   for each ans in ofsf_xopt!-ansl!-ansl u collect
      {ofsf_xopt!-ans!-gd ans,
	 for each ct in ofsf_xopt!-pt!-ctl ofsf_xopt!-ans!-pt ans collect
	    {'equal,ofsf_xopt!-ct!-var ct,ofsf_xopt!-ct!-value ct}};

procedure ofsf_xopt!-qe(f);
   % Quantifier elimination with answer. [f] is an existentially
   % quantified, weak parametric, linear formula with only weak
   % relations. Returns a quantifier free formula equivalent to [f].
   % Entry point for ofsf_qe.
   begin scalar !*rlxoptqe;
      !*rlxoptqe := T;
      return ofsf_xopt!-xopt f
   end;

procedure ofsf_xopt!-xopt(f);
   % Extended optimization. [f] is an existentially quantified, weak
   % parametric, linear formula with only weak relations; Returns a
   % ANSL or a quantifier free formual.
   begin scalar exl,mtr,w,co;
      scalar !*rlsiatadv,!*rlsitsqspl,!*rlsifac,!*rldavgcd,!*rlsipd,!*rlsipw;
      integer ofsf_xopt!-delnodes!*,ofsf_xopt!-plnodes!*,ofsf_xopt!-fnodes!*,
	 ofsf_xopt!-thcof!*,ofsf_xopt!-nodes!*;
      !*rlsipw := T;
      f := cl_simpl(cl_pnf f,nil,-1);
      w := cl_splt f;
      exl := car w;
      if null exl then
	 if !*rlxoptqe then
	    return f
	 else
      	    return ofsf_xopt!-ansl!-mk
	       {ofsf_xopt!-ans!-mk(f,ofsf_xopt!-pt!-mk nil)};
      if cdr exl then
	 rederr "ofsf_xopt!-xopt: more than one quantifier block";
      exl := car exl;
      if car exl neq 'ex then
	 rederr "ofsf_xopt!-xopt: not an existential formula";
      exl := cdr exl;
      mtr := cadr w;
      co := ofsf_xopt!-co!-mk if rl_op mtr neq 'or then
	 {ofsf_xopt!-ce!-mk(exl,mtr,nil,nil)}
      else
	 for each x in rl_argn mtr collect
	    ofsf_xopt!-ce!-mk(exl,x,nil,nil);
      if not !*rlxoptqe then
      	 return ofsf_xopt!-backsub ofsf_xopt!-elim co;
      w := ofsf_xopt!-backsub ofsf_xopt!-elim co;
      if !*rlverbose then
	 ioto_prin2 "Constructing result formula ... ";
      w := for each ans in ofsf_xopt!-ansl!-ansl w collect
	 ofsf_xopt!-ans!-gd ans;
      w := cl_simpl(rl_smkn('or,w),nil,-1);
      if !*rlverbose then
	 ioto_prin2t "done.";
      return w
   end;

procedure ofsf_xopt!-elim(co);
   % Quantifier elimination. [co] is a CO. Returns an ANSL.
   begin scalar w,ce,cel,resl,theo; integer n;
      if !*rlverbose then
      	 ofsf_xopt!-nodes!* := ofsf_xopt!-co!-length co;
      while co do <<
	 if !*rlverbose then
	    n := n+1;
	 % -- Get from container --
	 w := ofsf_xopt!-co!-get co;
      	 ce := car w;
	 co := cdr w;
	 if !*rlverbose then
	    ioto_prin2 {"[",n,"/",ofsf_xopt!-nodes!*};
	 % -- Eliminate --
	 cel := ofsf_xopt!-qevar(ce,theo);
	 % Update container ans resl
	 w := ofsf_xopt!-updco(cel,co,resl,theo);
	 co := car w;
	 resl := cadr w;
	 theo := caddr w;
	 % -- Finish --
	 if !*rlverbose then
	    ioto_prin2 "] ";
      >>;
      if !*rlverbose then <<
	 ioto_cterpri();
	 ioto_prin2t {"Number of computed nodes: ",ofsf_xopt!-nodes!*};
	 ioto_prin2t {"Number of PL hits: ",ofsf_xopt!-plnodes!*};
	 ioto_prin2t {"Number of identical CE's: ",ofsf_xopt!-delnodes!*};
	 ioto_prin2t {"Number of FALSE results: ",ofsf_xopt!-fnodes!*};
	 ioto_prin2t {"Number of CE's deleted by theo: ",ofsf_xopt!-thcof!*};
      >>;
      return ofsf_xopt!-cel2ansl resl;
   end;

procedure ofsf_xopt!-qevar(ce,theo);
   % Quantifier elimination eliminate variable. [ce] is a CE, [theo]
   % is a theory. Returns a list of CE's.
   begin scalar v,cs,es,cel;
      v := ofsf_xopt!-varsel ce;
      cs := ofsf_xopt!-cset(ofsf_xopt!-ce!-f ce,v);
      if ofsf_xopt!-cs!-null cs then
      	 return {ofsf_xopt!-ce!-mk(delq(v,ofsf_xopt!-ce!-vl ce),
	    ofsf_xopt!-ce!-f ce,
	    ofsf_xopt!-pt!-mk(ofsf_xopt!-ct!-mk(v,'arbitrary) .
	       ofsf_xopt!-pt!-ctl ofsf_xopt!-ce!-pt ce),
	    ofsf_xopt!-ce!-pl ce)};
      if !*rlxoptpl then <<
	 cs := ofsf_xopt!-applypl(cs,ofsf_xopt!-ce!-pl ce);
      	 if ofsf_xopt!-cs!-null cs then
	    return {ofsf_xopt!-ce!-mk(nil,'false,nil,nil)}
      >>;
      es := ofsf_xopt!-eset cs;
      cel := ofsf_xopt!-succs(ce,es,v,theo);
      return cel
   end;

procedure ofsf_xopt!-varsel(ce);
   % Variable selection. [ce] is a CE. Returns a variable.
   car ofsf_xopt!-ce!-vl ce;

procedure ofsf_xopt!-cset(f,v);
   % Candidate set. [f] is a formula, [v] is a variable. Returns a CS.
   if !*rlxoptses then
      ofsf_xopt!-scset(f,v)
   else
      ofsf_xopt!-csettrad(f,v);

procedure ofsf_xopt!-scset(f,v);
   % Structural candidate set. [f] is a formula; [v] is a variable;
   % Returns a CS.
   begin scalar w;
      w := ofsf_xopt!-scset1(f,v);
      if !*rlverbose and (cdr w eq 'finite) then
	 ioto_prin2 "g";
      return car w
   end;

procedure ofsf_xopt!-scset1(f,v);
   % Structural candidate set subroutine. [f] is a formula; [v] is a
   % variable; Returns a pair $(\Gamma,\tau)$, where $\gamma$ is a CS
   % and $\tau$ is either ['finite] or [nil].
   if rl_junctp rl_op f then
      ofsf_xopt!-scsetjunct(f,v)
   else if cl_atfp(f) then
      ofsf_xopt!-scsetat(f,v)
   else
      rederr {"ofsf_xopt!-scset1: Unexpected operator",rl_op f};

procedure ofsf_xopt!-scsetjunct(f,v);
   % Structural candidate set junction. [f] is a junction; [v] is a
   % variable; Returns a pair $(\Gamma,\tau)$, where $\gamma$ is a CS
   % and $\tau$ is either ['finite] or [nil].
   begin scalar w,fl,nl,r;
      r := ofsf_xopt!-cs!-mk(nil,nil,nil);
      for each x in rl_argn f do <<
	 w := ofsf_xopt!-scset1(x,v);
	 if cdr w then
	    fl := car w . fl
	 else
	    nl := car w . nl
      >>;
      if fl then
	 if rl_op f eq 'and then <<
	    if !*rlverbose and nl then
	       ioto_prin2 "s";
	    return ofsf_xopt!-scsetselect fl . 'finite
      	 >> else if rl_op f eq 'or and null nl then <<
      	    for each cs in fl do
	       r := ofsf_xopt!-scsetunion(cs,r);
      	    return r . 'finite
	 >>;
      for each cs in fl do
	 r := ofsf_xopt!-scsetunion(cs,r);
      for each cs in nl do
	 r := ofsf_xopt!-scsetunion(cs,r);
      return r . nil
   end;

procedure ofsf_xopt!-scsetselect(csl);
   % Structural candidate set select. [csl] is a list of CS's. Returns
   % a CS.
   car csl;

procedure ofsf_xopt!-scsetunion(cs1,cs2);
   % Structural candidate set union. [cs1] and [cs2] are CS's. Returns a CS.
   ofsf_xopt!-cs!-mk(
      union(ofsf_xopt!-cs!-ubl cs1,ofsf_xopt!-cs!-ubl cs2),
      union(ofsf_xopt!-cs!-lbl cs1,ofsf_xopt!-cs!-lbl cs2),
      union(ofsf_xopt!-cs!-eql cs1,ofsf_xopt!-cs!-eql cs2));

procedure ofsf_xopt!-scsetat(at,v);
   % Structural candidate set atomic formula. [at] is an atomic
   % formula; [v] is a variable; Returns a pair $(\Gamma,\tau)$, where
   % $\gamma$ is a CS and $\tau$ is either ['finite] or [nil].
   begin scalar bt,p;
      bt := ofsf_xopt!-boundarytype(at,v);
      p := ofsf_arg2l at;
      return if bt eq 'ub then
	 ofsf_xopt!-cs!-mk({p},nil,nil) . nil
      else if bt eq 'lb then
	 ofsf_xopt!-cs!-mk(nil,{p},nil) . nil
      else if bt eq 'equ then
	 ofsf_xopt!-cs!-mk(nil,nil,{p}) . 'finite
      else
	 ofsf_xopt!-cs!-mk(nil,nil,nil) . nil
   end;

procedure ofsf_xopt!-boundarytype(at,v);
   % Boundary type. [at] is an atomic formula; [v] is a variable.
   % Returns ['ub], ['lb], or ['equ].
   begin scalar rel,p,rp,w;
      rel := ofsf_op at;
      p := ofsf_arg2l at;
      if domainp p then
	 return nil;
      rp := sfto_reorder(p,v);
      if mvar rp neq v then
	 return nil;
      if rel eq 'equal then
	 return 'equ;
      w := lc rp;
      if not domainp w then
	 rederr "ofsf_xopt!-boundarytype: parametric coefficient";
      if not(rel memq '(geq leq)) then
	 rederr {"ofsf_xopt!-boundarytype: unknown relation",rel};
      return if minusf w then
	 if rel eq 'geq then
	    'ub
	 else
	    'lb
      else
	 if rel eq 'geq then
	    'lb
	 else
	    'ub;
   end;

procedure ofsf_xopt!-csettrad(f,v);
   % Candidate set traditional style. [f] is a formula, v is a
   % variable. Returns a CS.
   begin scalar atl,bt,ubl,lbl,eql,p;
      atl := cl_atl1(f);
      for each at in atl do <<
	 bt := ofsf_xopt!-boundarytype(at,v);
	 p := ofsf_arg2l at;
	 if bt eq 'ub then
	    ubl := p . ubl
	 else if bt eq 'lb then
	    lbl := p . lbl
	 else if bt eq 'equ then
	    eql := p . eql
      >>;
      return ofsf_xopt!-cs!-mk(ubl,lbl,eql)
   end;

procedure ofsf_xopt!-applypl(cs,pl);  % TODO: Keine Spezialfaelle von ESET durch PL?
   % Apply passive list. [cs] is a CS; [pl] is a PL. Returns a CS.
   if not !*rlxoptpl then
      cs
   else if null pl then
      cs
   else
      ofsf_xopt!-cs!-mk(ofsf_xopt!-applypl1(ofsf_xopt!-cs!-ubl cs,pl),
      	 ofsf_xopt!-applypl1(ofsf_xopt!-cs!-lbl cs,pl),
      	 ofsf_xopt!-applypl1(ofsf_xopt!-cs!-eql cs,pl));

procedure ofsf_xopt!-applypl1(cpl,pl);
   % Apply passive list subroutine. [cpl] is a list of CP's; [pl] is a
   % PL. Returns a list of CP's.
   for each cp in cpl join
      if cl_simpl(ofsf_0mk2('equal,ofsf_xopt!-cp!-p cp),pl,-1) eq 'false then <<
	 ofsf_xopt!-plnodes!* := ofsf_xopt!-plnodes!*+1;
      >> else
	 {cp};

procedure ofsf_xopt!-eset(cs);
   % Elimination set. [cs] is a CS. Retuns an ES.
   if !*rlxoptsb then
      ofsf_xopt!-esetos(cs)
   else
      ofsf_xopt!-esetbs(cs);

procedure ofsf_xopt!-esetos(cs);
   % Elimination set one sides. [cs] is a CS. Retuns an ES.
   begin scalar ubl,lbl,eql;
      ubl := ofsf_xopt!-cs!-ubl cs;
      lbl := ofsf_xopt!-cs!-lbl cs;
      eql := ofsf_xopt!-cs!-eql cs;
      return ofsf_xopt!-es!-mk if null ubl and null lbl then
	 ofsf_xopt!-es!-mk eql
      else if null ubl then
	 'pinf . eql
      else if null lbl then
	 'minf . eql
      else if length ubl <= length lbl then
	 'pinf . union(ubl,eql)
      else
      	 'minf . union(lbl,eql)
   end;

procedure ofsf_xopt!-esetbs(cs);
   % Elimination set both sides. [cs] is a CS. Retuns an ES.
   ofsf_xopt!-es!-mk union(
      union(ofsf_xopt!-cs!-ubl cs,ofsf_xopt!-cs!-lbl cs),ofsf_xopt!-cs!-eql cs);

procedure ofsf_xopt!-succs(ce,es,v,theo);
   % Successors. [ce] is a CE; [es] is an ES; [v] is a variable;
   % [theo] is a list of atomic formulas. Returns a list of CE's.
   begin scalar cel,npl;
      npl := ofsf_xopt!-ce!-pl ce;
      for each cp in ofsf_xopt!-es!-cpl es do <<   % TODO: Abbruch bei TRUE
      	 cel := ofsf_xopt!-succs1(ce,cp,v,npl,theo) . cel;
	 npl := ofsf_xopt!-updpl(cp,npl);   % TODO: Am Ende ueberfluessig.
      >>;
      return reversip cel
   end;

procedure ofsf_xopt!-succs1(ce,cp,v,npl,theo);
   % Successors subroutine. [ce] is an CE; [cp] is a CP; [v] is a
   % variable; [npl] is a PL; [theo] is a theory. Returns a CE.
   begin scalar p,f,w;
      p := ofsf_xopt!-cp!-p cp;
      f := ofsf_xopt!-sub(ofsf_xopt!-ce!-f ce,v,p,theo);
      return if w then
	 ofsf_xopt!-ce!-mk(nil,'false,nil,nil)
      else
      	 ofsf_xopt!-ce!-mk(delq(v,ofsf_xopt!-ce!-vl ce),
	    f,
	    ofsf_xopt!-pt!-mk(ofsf_xopt!-ct!-mk(
	       v,ofsf_xopt!-solv(p,v)) .
		  ofsf_xopt!-pt!-ctl ofsf_xopt!-ce!-pt ce),
	    ofsf_xopt!-plsub(npl,v,p))
   end;

procedure ofsf_xopt!-sub(f,v,sol,theo);  % TODO: Ist das teuer!
   % Substitution. [f] is a formula; [v] is a variable; [sol] is a SF;
   % [theo] is a theory. Returns a formula.
   begin scalar w;
      w := ofsf_xopt!-sub1(f,v,sol);
      if not(!*rlxoptrir) or not(!*rlxoptri) then
	 return cl_simpl(w,theo,-1);
      w := cl_simpl(w,nil,-1);
      return if cl_simpl(w,theo,-1) eq 'false then
       	 'false
      else
	 w
   end;

procedure ofsf_xopt!-sub1(f,v,sol);
   % Substitution subroutine. [f] is a formula; [v] is a variable; [sol]
   % is a SF. Returns a formula.
   if sol memq '(minf pinf) then
      cl_apply2ats1(f,'ofsf_xopt!-subiat,{v,sol})
   else
% cl_apply2ats1(f,'ofsf_xopt!-subat,{v,sfto_reorder(sol,v)});
      cl_apply2ats1(f,'ofsf_xopt!-subat,{v,sol});

procedure ofsf_xopt!-subiat(atf,v,it);
   % Substitution of infinity into atomic formula. [atf] is an atomic
   % formula; [v] is a variable; [it] is either ['pinf] or ['minf].
   % Returns a formula.
   begin scalar rel,p,rp,pos;
      rel := ofsf_op atf;
      if rel eq 'equal then
	 return if v memq ofsf_varlat atf then
	    'false
	 else
	    atf;
      if rel eq 'neq then
	 return if v memq ofsf_varlat atf then
	    'true
	 else
	    atf;
      p := ofsf_arg2l atf;
      if domainp p then
	 return atf;
      rp := sfto_reorder(p,v);
      if mvar rp neq v then
	 return atf;
      pos := if it eq 'pinf then not minusf lc rp else minusf lc rp;
      return if (pos and rel memq '(geq greaterp)) or
	 (not pos and rel memq '(leq lessp))
      then
	 'true
      else
	 'false
   end;

procedure ofsf_xopt!-subat(atf,v,sol);
   % Substution in atomic formula. [atf] is an atomic formula; [v] is
   % a variable; [sol] is a SF. Returns an atomic formula.
   ofsf_0mk2(ofsf_op atf,ofsf_xopt!-sublf(ofsf_arg2l atf,v,sol));

procedure ofsf_xopt!-sublf(p,v,mp);
   % Substitution linear forms. [p] is a SF, [v] is a kernel, [mp] is
   % a SF. Returns a SF.
   begin scalar oldorder,rmp,rp,r;
      if not(v memq kernels p) then
	 return p;
      oldorder := setkorder {v};
      rmp := reorder mp;
%      rmp := mp;
      rp := reorder p;
      r := if minusf lc rmp then
	 addf(multf(lc rp,red rmp),negf multf(lc rmp,red rp))
      else
	 addf(negf multf(lc rp,red rmp),multf(lc rmp,red rp));
      setkorder oldorder;
      return reorder r
   end;

procedure ofsf_xopt!-solv(p,v);
   % Solve. [p] is either a SF, ['pinf], or ['minf]; [v] is a variable.
   % Returns Lisp-prefix.
   begin scalar rp;
      if p memq '(minf pinf) then return p;
      rp := sfto_reorder(p,v);
      return prepsq quotsq(!*f2q reorder negf red rp,!*f2q reorder lc rp)
   end;

procedure ofsf_xopt!-plsub(pl,v,sol);
   % Passive list substitution. [pl] is a [PL]; [v] is a variable; [sol]
   % is a SF. Returns a PL.
   begin scalar w;
      if not !*rlxoptpl then return nil;
      if null pl then return nil;
      w := rl_smkn('and,pl);
      w := cl_simpl(ofsf_xopt!-sub(w,v,sol,nil),nil,-1);
      if w eq 'true then
	 return nil;
      if w eq 'false then
	 rederr {"ofsf_xopt!-plsub: Result",w};
      if cl_atfp w then
	 w := {w}
      else if rl_op w neq 'and then
	 rederr {"ofsf_xopt!-plsub: unexpected operator",rl_op w}
      else
	 w := rl_argn w;
      return for each f in w join
	 if cl_atfp f then
	    {f}
	 else <<
	    if !*rlverbose then ioto_prin2 "C";
      	    nil
	 >>
   end;

procedure ofsf_xopt!-updpl(cp,pl);
   % Update passive list. [cp] is a CP; [pl] is a PL. Returns a [PL].
   begin scalar w;
      if not !*rlxoptpl then return nil;
      w := ofsf_xopt!-cp!-p cp;
      if w memq '(pinf minf) then
      	 return pl;
      return ofsf_0mk2('neq,w) . pl
   end;

procedure ofsf_xopt!-updco(cel,co,resl,theo);
   % Update container. [cel] is a list of CE's; [co] is a container;
   % [resl] is a list of CE's; [theo] is a theory. Returns ['false] or
   % a list $(C,R,\Theta,m)$, where $C$ is the updated [co], $R$ is
   % the updated [resl], $\Theta$ is the updated [theo], and $m$ is
   % the number of conatiner elementes added.
   begin scalar ce,f,w;
      while cel do <<
	 ce := car cel;
	 cel := cdr cel;
	 f := ofsf_xopt!-ce!-f ce;
	 if f eq 'false then <<
	    ofsf_xopt!-fnodes!* := ofsf_xopt!-fnodes!*+1;
	    nil
	 >> else if f eq 'true then <<
	    resl := {ce};
	    co := nil;
	    cel := nil
	 >> else if null ofsf_xopt!-ce!-vl ce then <<
	    w := ofsf_xopt!-resinherit(ce,resl,co,theo);
	    resl := car w;
	    co := cadr w;
	    theo := caddr w
	 >> else if rl_op f eq 'or then
	    for each ff in rl_argn f do <<
	       co := ofsf_xopt!-ccoput(co,
		  ofsf_xopt!-ce!-mk(ofsf_xopt!-ce!-vl ce,
		     ff,
		     ofsf_xopt!-ce!-pt ce,
		     ofsf_xopt!-ce!-pl ce));
	    >>
	 else
	    co := ofsf_xopt!-ccoput(co,ce);
      >>;
      return {co,resl,theo}
   end;

procedure ofsf_xopt!-resinherit(ce,resl,co,theo);  % TODO: Splitting OR's???
   % Result inheritance. [ce] is a CE, [resl] is a list of CE's, [co]
   % is a CO, and [theo] is a theory. Returns a list $(R,C,\Theta)$,
   % where $R$ is the updated [resl], $C$ is the updated [co] and
   % $\Theta$ is the updated [theo].
   begin scalar f;
      if ofsf_xopt!-celmember(ce,resl) then
	 return {resl,co,theo};
      if not !*rlxoptri then <<
	 if !*rlverbose then ioto_prin2 ".";
	 return {ce . resl,co,theo}
      >>;
      f := ofsf_xopt!-ce!-f ce;
      if rl_op f eq 'and then <<
	 if !*rlverbose then ioto_prin2 ".";
	 return {ce . resl,co,theo}
      >>;
      theo := cl_simpl(rl_smkn('and,cl_nnfnot f . theo),nil,-1);
      if cl_atfp theo then
	 theo := {theo}
      else if rl_op theo neq 'and then
	 rederr {"ofsf_xopt!-resinherit: Unexpected operator",rl_op theo}
      else
      	 theo := for each atf in rl_argn theo join
	    if cl_atfp atf then {atf};
      if !*rlxoptrir then
      	 resl := ofsf_xopt!-thapplycel(resl,theo);
      if !*rlxoptric then
      	 co := ofsf_xopt!-thapplyco(co,theo);
% mathprint rl_prepfof rl_smkn('and,theo);
      if !*rlverbose then ioto_prin2 ".";
      return {ce . resl,co,theo}
   end;

procedure ofsf_xopt!-celmember(ce,cel);
   % Container element list member. [ce] is a CE; [cel] is a list of
   % CE's. Returns [nil] or non-[nil].
   begin scalar f,a,scel,flg;
      f := ofsf_xopt!-ce!-f ce;
      scel := cel;
      while scel do <<
	 a := car scel;
	 scel := cdr scel;
	 if f = ofsf_xopt!-ce!-f a then <<
	    scel := nil;
	    flg := T
	 >>
      >>;
      return flg
   end;

procedure ofsf_xopt!-thapplycel(cel,theo);
   % Apply theory to contaioner element list. [cel] is a list of CE's;
   % [theo] is a theory. Returns a list of CE's.
   for each ce in cel join
      if cl_simpl(ofsf_xopt!-ce!-f ce,theo,-1) neq 'false then
	 {ce};

procedure ofsf_xopt!-thapplyco(co,theo);
   % Apply theory to container. [co] is an CO. [th] is a theory.
   % Returns a [CO].
   begin scalar co,ce,w,r,f;
      while co do <<
	 w := ofsf_xopt!-co!-get co;
	 ce := car w;
	 co := cdr w;
	 f := cl_simpl(ofsf_xopt!-ce!-f ce,theo,-1);
	 if f eq 'false then
	    ofsf_xopt!-thcof!* := ofsf_xopt!-thcof!* +1
	 else
	    r := ofsf_xopt!-co!-put(r,ofsf_xopt!-ce!-mk(ofsf_xopt!-ce!-vl ce,
	       f,
	       ofsf_xopt!-ce!-pt ce,
	       ofsf_xopt!-ce!-pl ce))
      >>;
      return r
   end;

procedure ofsf_xopt!-ccoput(co,ce);
   % Conditionally container put. [ce] is a CE; [co] is a CO. Returns a CO.
   begin scalar sco,w,f,flg,pl;
      f := ofsf_xopt!-ce!-f ce;
      sco := co;
      while sco do <<
	 w := ofsf_xopt!-co!-get(sco);
	 sco := cdr w;
	 w := car w;
	 if ofsf_xopt!-ce!-f w = f then <<
	    sco := nil;
	    flg := T
	 >>
      >>;
      if flg then <<
      	 co := delq(w,co);
      	 pl := intersection(ofsf_xopt!-ce!-pl ce,ofsf_xopt!-ce!-pl w);
      	 ce := ofsf_xopt!-ce!-mk(
	    ofsf_xopt!-ce!-vl ce,f,ofsf_xopt!-ce!-pt ce,pl);
	 ofsf_xopt!-delnodes!* := ofsf_xopt!-delnodes!*+1
      >> else
	 ofsf_xopt!-nodes!* := ofsf_xopt!-nodes!*+1;
      return ofsf_xopt!-co!-put(co,ce)
   end;

procedure ofsf_xopt!-cel2ansl(cel);
   % Container element list to answer list. [cel] is a list of CE's.
   % Returns an ANSL.
   ofsf_xopt!-ansl!-mk for each ce in cel collect
      ofsf_xopt!-ans!-mk(ofsf_xopt!-ce!-f ce,ofsf_xopt!-ce!-pt ce);

procedure ofsf_xopt!-backsub(ansl);
   % Back substitution answer list. [ansl] is an ANSL. Returns an
   % ANSL. The returned answer list is in a more convenient form.
   ofsf_xopt!-ansl!-mk for each ans in ofsf_xopt!-ansl!-ansl ansl collect
      ofsf_xopt!-backsubans ans;

procedure ofsf_xopt!-backsubans(ans);
   % Back substitution answer. [ans] is an ANS. Returns an ANS. The
   % returned answer is in a more convenient form.
   ofsf_xopt!-ans!-mk(
      ofsf_xopt!-ans!-gd ans,ofsf_xopt!-backsubpt ofsf_xopt!-ans!-pt ans);

procedure ofsf_xopt!-backsubpt(pt);
   % Back substitution point. [pt] is a PT. Returns a PT. The returned
   % point is in a more convenient form.
   begin scalar subl,w;
      return ofsf_xopt!-pt!-mk for each ct in ofsf_xopt!-pt!-ctl pt collect <<
   	 w := prepsq subsq(simp ofsf_xopt!-ct!-value ct,subl);
      	 subl := (ofsf_xopt!-ct!-var ct . w) . subl;
	 ofsf_xopt!-ct!-mk(ofsf_xopt!-ct!-var ct,w)
      >>
   end;

endmodule;  % ofsfxopt

end;  % of file
