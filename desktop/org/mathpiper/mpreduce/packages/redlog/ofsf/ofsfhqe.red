% ----------------------------------------------------------------------
% $Id: ofsfhqe.red 454 2009-11-20 19:46:35Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2003-2009 Andreas Dolzmann and Lorenz Gilch
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
   fluid '(ofsf_hqe_rcsid!* ofsf_hqe_copyright!*);
   ofsf_hqe_rcsid!* :=
      "$Id: ofsfhqe.red 454 2009-11-20 19:46:35Z thomas-sturm $";
   ofsf_hqe_copyright!* := "Copyright (c) 2003-2009 A. Dolzmann and L. Gilch"
>>;

module ofsfhqe;
% Ordered fields standard form Hermitian quantifier elimination.

% Real Root Counting Quantifier Elimination.
% Input: A First-order formula
% Output: A quantifier-free formula, which is equivalent to the input formula.

procedure ofsf_ghqe(f);
   % Generic Real Root Counting Quantifier Elimination. [f] is a first-order
   % formula. Returns an equivalent quantifier-free formula.
   begin scalar res,ofsf_hqetheo!*,ofsf_hqexvars!*,!*rlhqegen;
      ofsf_hqetheo!* := nil;
      ofsf_hqexvars!* := nil;
      !*rlhqegen := T;
      res := ofsf_hqe f;
      !*rlhqegen := nil;
      ofsf_hqexvars!* := nil;
      return {rl_smkn('and,rl_thsimpl ofsf_hqetheo!*),res}
   end;

procedure ofsf_hqe(phi);
   % Real Root Counting Quantifier Elimination. [phi] is a first-order
   % formula. Returns a quantifier-free formula. Wrapper for
   % [ofsf_hqe0] managing [rlqgvb].
   begin scalar w,svrlhqevb,svcgbverbose;
      svrlhqevb := !*rlhqevb;
      svcgbverbose := !*cgbverbose;
      if not !*rlverbose then
	 off1 'rlhqevb;
      if not(!*cgbverbose and !*rlverbose and !*rlhqevb) then
	 off1 'cgbverbose;      
      w := ofsf_hqe0 phi;
      onoff('rlhqevb,svrlhqevb);
      onoff('cgbverbose,svcgbverbose);
      return w
end;

procedure ofsf_hqe0(phi);
   % Real Root Counting Quantifier Elimination. [phi] is a first-order formula.
   % Returns a quantifier-free formula.
   begin scalar phi1,ql,firstblock;integer n;
      if !*rlhqevb then
	 ioto_tprin2 {"+++++ simplifying input formula of RRCQE with ",
            cl_atnum phi," atomic formulas... "};
      phi1 := cl_simpl(phi,nil,-1);
      if !*rlhqevb then
	 ioto_prin2 {"done. Number of atomic formulas now: ",cl_atnum phi1};
      if phi1 member {'true,'false} then
	 return phi1;
      if !*rlhqevb then
	 ioto_tprin2 "+++++ building prenex normal form... ";
      phi1 := cl_pnf phi1;
      if !*rlhqevb then
	 ioto_prin2 "done.";
      ql := cl_splt phi1;
      phi1 := cadr ql;
      ql := car ql;
      if !*rlhqegen and null ofsf_hqexvars!* then
	 ofsf_hqexvars!* := ofsf_genvar ql;
      while not null ql and not firstblock do <<
	 if !*rlverbose then <<
	    n:=n+1;
	    ioto_tprin2 {"+++++ Eliminating ",car ql," (",length ql -n+1,
	       ioto_cplu(" block",length ql -n+1)," left)"}
	 >>;
	 phi1 := ofsf_rrcnfblockqe(car ql, phi1);
	 if phi1 member {'true,'false} then
	    ql := nil
	 else <<
	    if !*rlhqedim0 then
	       firstblock := t;
	    ql := cdr ql
	 >>
      >>;
      if !*rlhqevb then
	 ioto_tprin2 {"+++++ leaving RRCQE: ",
	    cl_atnum phi1," atomic formulas"};
      return ofsf_rqrequantify(ql,phi1)
   end;

procedure ofsf_rqrequantify(ql,phi);
   % Construction of a quantified formula. [ql] is a list of the form
   % $ (Q v_{1} ... v_{k})$, where $Q$ is either $ex$ or $all$ and
   % $v_{1}, ... v_{k}$ are identifiers, [phi] is a formula. Returns a
   % formula.
   if not !*rlhqedim0 or null ql then
      phi
   else
      ofsf_rqrequantify2(ql,phi);

procedure ofsf_rqrequantify2(ql,phi);
   % Subroutine of ofsf_rqrequantify. [ql] is a list of the form
   % $ (Q v_{1} ... v_{k})$, where $Q$ is either $ex$ or $all$ and
   % $v_{1}, ... v_{k}$ are identifiers, [phi] is a formula. Returns a
   % formula.
   begin;
      while not null ql do <<
	 phi := ofsf_rqrequantify3(caar ql,cdar ql,phi);
	 ql := cdr ql
      >>;
      return phi
   end;

procedure ofsf_rqrequantify3(q,varl,phi);
   % Subroutine of ofsf_rqrequantify2. [q] is one of $ex$ or $all$,
   % [varl] is a list of identifiers, [phi] is a formula. Returns a
   % formula.
   if null varl then
      phi
   else
      ofsf_rqrequantify3(q,cdr varl,rl_mkq(q,car varl,phi));

procedure ofsf_rrcnfblockqe(ql,phi);
   % Elimination of one quantifier block. [ql] is a list of the form
   % $ (Q v_{1} ... v_{k})$, where $Q$ is either $ex$ or $all$ and $v_{1}, ...
   % v_{k}$ are identifiers, [phi] is a quantifier-free formula. Returns a
   % quantifier-free formula.
   begin scalar psi,gamma; integer i;
      if !*rlhqevb then
	 ioto_tprin2 "+++++ building positive normal form ... ";
      if car ql eq 'all then
	 psi := cl_nnfnot phi
      else
	 psi := cl_nnf phi;
      if !*rlhqevb then <<
	 ioto_prin2 {"done, now ",cl_atnum psi," atomic formulas"};
	 ioto_tprin2 "+++++ building RRC normal forms ..."
      >>;
      gamma := ofsf_connect ofsf_rrcnf(psi,cdr ql);
      if gamma member {'true,'false} then
	psi := gamma
      else <<
        if !*rlhqevb then
	   ioto_prin2 {"done."};
%	if !*rlverbose then
%	   ioto_tprin2 {"+++++ ",length gamma," conjunctions to eliminate."};
	psi := 'false;
        for each elem in gamma do <<
	   if !*rlverbose then <<
	      i := i+1;
	      ioto_tprin2 {"++++ ",length gamma -i +1,
		 ioto_cplu(" conjunction", length gamma -i +1 neq 1)," left"}
	   >>;
	   psi := ofsf_rqsimpl(rl_smkn('or,
	      {psi,ofsf_rrcnfeliminate(elem,cdr ql)}));
%	   if !*rlverbose then
%	      ioto_tprin2 {"+++++ Eliminated conjunction ",i}
        >>
      >>;
      if car ql eq 'all then
	 psi := cl_nnfnot psi;
      return psi
   end;

procedure ofsf_rrcnfeliminate(rrcnf,varl);
   % Elimination of a conjunction. [rrcnf] is a RRCNF, [varl] is a list
   % of identifiers. Returns a quantifier-free formula.
   if null car rrcnf and null cadr rrcnf and null caddr rrcnf then
      ofsf_rqsimpl ofsf_smkn('and,cadddr rrcnf)
   else if !*rlhqeconnect then
      if ofsf_conjp cadddr rrcnf then
	 ofsf_rqsimpl rl_smkn('and,{cadddr rrcnf,ofsf_qenf(cadddr rrcnf,
	    car rrcnf,cadr rrcnf,caddr rrcnf,varl)})
      else
	 ofsf_rqsimpl rl_smkn('and,{cadddr rrcnf,ofsf_qenf('true,car rrcnf,
	    cadr rrcnf,caddr rrcnf,varl)})
   else
      ofsf_rqsimpl rl_smkn('and,{rl_smkn('and,cadddr rrcnf),ofsf_qenf(
	 rl_smkn('and,cadddr rrcnf),car rrcnf,cadr rrcnf,
	 caddr rrcnf,varl)});

procedure ofsf_connect(rrcnfl);
   % Connects RRCNF's with same EPL, GPL, NPL. [rrcnfl] is a list of RRCNF's.
   % Returns a list of RRCNF's.
   begin scalar samephi,newlist,l;
      if rrcnfl member {'true,'false} or not !*rlhqeconnect then
      	 return rrcnfl;
      l := for each elem in rrcnfl collect {sort(car elem,'ordp),
      	 sort(cadr elem,'ordp),sort(caddr elem,'ordp),
	 rl_smkn('and,cadddr elem)};
      while not null l do <<
	 samephi := ofsf_samephi(car l,cdr l);
	 newlist := (car samephi) . newlist;
	 l := cadr samephi
      >>;
      return newlist
   end;

procedure ofsf_samephi(rrcnf,rrcnfl);
   % Connects RRCNF's with same EPL, GPL, NPL as in first argument. [rrcnf] is
   % a RRCNF, [rrcnfl] is a list of RRCNF's. Returns a list of the form:
   % $(rrcnf1 rrcnfl1)$, where $rrcnf1$ is a RRCNF and $rrcnfl1$ is a
   % list of RRCNF's.
   begin scalar rrcnf1,rrcnfl1;
      rrcnf1 := rrcnf;
      while not null rrcnfl do <<
	 if ofsf_samephip(rrcnf,car rrcnfl) then
   	    rrcnf1 := ofsf_connectrrcnf(rrcnf, car rrcnfl)
	 else
	    rrcnfl1 := (car rrcnfl) . rrcnfl1;
	 rrcnfl := cdr rrcnfl
      >>;
      return {rrcnf1, rrcnfl1}
   end;

procedure ofsf_samephip(r1,r2);
   % Predicate, if two RRCNF's have same EPL, GPL, NPL. [r1] and [r2] are
   % RRCNF's. Returns T or NIL.
   (car r1 = car r2) and (cadr r1 = cadr r2) and (caddr r1 = caddr r2);

procedure ofsf_connectrrcnf(r1,r2);
   % Connects two RRCNF's with same EPL, GPL, NPL. [r1] and [r2] are
   % RRCNF's. Returns a RRCNF.
   if cadddr r1 = 'true or cadddr r2 = 'true then
      {car r1, cadr r1, caddr r1, 'true}
   else
      {car r1, cadr r1, caddr r1,
      	 rl_smkn('or,{cadddr r1,cadddr r2})};

procedure ofsf_smkn(op,phi);
   % Formula construction. [op] is either OR or AND, [phi] is either a THEORY
   % or a formula. Returns the formula $(op phi)$, only if rlhqeconnect is off,
   % else [phi] itself.
   if not !*rlhqeconnect then
      rl_smkn(op,phi)
   else
      phi;

procedure ofsf_conjp(f);
   % Tests, if a a formula is a conjunction. [f] is a formula. Returns T or
   % NIL.
   if listp f and rl_op f eq 'or then
	 nil
   else
      t;

procedure ofsf_genvar(l);
   % Get variables from quantifier blocks. [l] is a list of the form
   % $(... (q v_{1} ... v_{k}) ...)$, where $q$ is a quantifier and $v_{1}$,
   % ..., $v_{k}$ are identifiers. Returns a list of identifiers, the list of
   % all identifiers.
   if null l then
      nil
   else
      append(cdar l, ofsf_genvar cdr l);


% Procedures for RRCNF computations:

procedure ofsf_rrcnf(m,vl);
   % Ordered field standard form real root counting normal form. [m]
   % is a quantifier-free formula; [vl] is a list of variables.
   % Returns a truth value or a list of triplets
   % $(\eta,\gamma,\nu,\theta)$, where $\eta$, $\gamma$, and $\nu$ are
   % lists of polynomials coming from equations, $>$-constraints, and
   % $\neq$-constraints, respectively; $\theta$ is a theory.
   begin scalar w;
      if !*rlhqevb then
	 {"++++ computing DNF of formula with ",cl_atnum m," atomic formulas"};
      w := cl_dnf m;
      if !*rlhqevb then
	 {"++++ finished DNF Computation: ",cl_atnum w," atomic formulas"};
      if rl_tvalp w then
	 return w;
      w := if rl_op w eq 'or then
	 cdr w
      else
	 {w};
      return for each br in w join
	 if rl_op br eq 'and then
	    ofsf_rrcnf1(cdr br,vl)
	 else
	    ofsf_rrcnf1({br},vl)
   end;


%DS
% <RRCNF>  ::= (...,(<EPL>,<GPL>,<NPL>,<THEORY>),...)
% <EPL>    ::= (...,SF,...) left hand sides of equations
% <GPL>    ::= (...,SF,...) left hand sides of $>$-ATF's
% <NPL>    ::= (...,SF,...) left hand sides of $\neq$-ATF's
% <THEORY> ::= (...,ATF,...) ATF's containing only parameters

procedure ofsf_new();
   {{nil,nil,nil,nil}};

procedure ofsf_addequal(rrcnf,lhs);
   <<
      for each v in rrcnf do
      	 car v :=  lhs . car v;
      rrcnf
   >>;

procedure ofsf_addgreaterp(rrcnf,lhs);
   <<
      for each v in rrcnf do
      	 cadr v :=  lhs . cadr v;
      rrcnf
   >>;

procedure ofsf_addlessp(rrcnf,lhs);
   begin scalar neglhs;
      neglhs := negf lhs;
      for each v in rrcnf do
      	 cadr v :=  neglhs . cadr v;
      return rrcnf
   end;

procedure ofsf_addneq(rrcnf,lhs);
   <<
      for each v in rrcnf do
      	 caddr v :=  lhs . caddr v;
      rrcnf
   >>;

procedure ofsf_addgeq(rrcnf,lhs);
   for each v in rrcnf join
      {{lhs . car v,cadr v,caddr v,cadddr v},
	 {car v,lhs . cadr v,caddr v,cadddr v}};

procedure ofsf_addleq(rrcnf,lhs);
   begin scalar neglhs;
      neglhs := negf lhs;
      return for each v in rrcnf join
	 {{lhs . car v,cadr v,caddr v,cadddr v},
 	    {car v,neglhs . cadr v,caddr v,cadddr v}}
   end;

procedure ofsf_addtheo(rrcnf,at);
   <<
      for each v in rrcnf do
      	 cadddr v :=  at . cadddr v;
      rrcnf
   >>;

procedure ofsf_rrcnf1(atl,vl);
   % Ordered field standard form real root counting normal form
   % subroutine. [atl] is a list of atomic formulas; [vl] is a list of
   % variables. Returns a list of lists $(\eta,\gamma,\nu,\theta)$,
   % where $\eta$, $\gamma$, and $\nu$ are lists of polynomials coming
   % from equations, $>$-constraints, and $\neq$-constraints,
   % respectively; $\theta$ is a theory.
   begin scalar resl,op,lhs;
      resl := ofsf_new();
      for each at in atl do <<
	 op := rl_op at;
	 lhs := ofsf_arg2l at;
	 resl := if null intersection(kernels lhs,vl) then
	    ofsf_addtheo(resl,at)
 	 else if op eq 'equal then
	    ofsf_addequal(resl,lhs)
	 else if op eq 'greaterp then
	    ofsf_addgreaterp(resl,lhs)
	 else if op eq 'lessp then
	    ofsf_addlessp(resl,lhs)
	 else if op eq 'neq then
	    ofsf_addneq(resl,lhs)
	 else if op eq 'geq then
	    ofsf_addgeq(resl,lhs)
	 else if op eq 'leq then
	    ofsf_addleq(resl,lhs)
      >>;
      return resl
   end;


% -----------------------------------------------------------------------------
% Quantifier Elimination of a first-order formula in elimination normal form
% -----------------------------------------------------------------------------

% Quantifier elimination of formulas in normal form.
% Input: A first-order formula $ phi $ and a formula $ xi $, which is a
% conjunction of atomic formulas, whose identifiers are independent of the
% bounded identifiers in $ phi $. The atomic formulas of $ phi $ contains only
% relations with $ > $, $ = $ or $ neq $. Every relation of $ phi$
% contains at least one bounded variable. The polynomials of $ xi $ are square-
% primitiv, and the polynomials of equalities and inequalities are squarefree.
% Output: a quantifier-free formula $ psi $, so that
% $ psi AND xi $ is equivalent to $ phi AND xi $.

procedure ofsf_qenf(xi,flist,glist,hlist,varl);
   % Quantifier elimination of formulas in elimination normal form.
   % [xi] is a conjunction of atomic formulas, [flist], [glist] and [hlist]
   % are lists of SF's, [varl] is a list of identifiers. Returns
   % a quantifier-free formula.
   begin;
      if !*rlverbose then
	 ioto_tprin2 {"+++ entering QENF: theo:",
	    length xi," r:",length flist," s:",
	    length glist," t:",length hlist};
      if xi = 'false then <<
	 if !*rlverbose then
	    ioto_tprin2 {"+++ leaving QENF (0)"};
	 return 'false
      >> else if null flist and null glist and null hlist then <<
	 if !*rlverbose then
	    ioto_tprin2 {"+++ leaving QENF (0)"};
	 return 'true
      >> else if null flist then
	 return ofsf_caser0(xi,varl,glist,hlist)
      else
	 return ofsf_eliminategsys(xi,flist,glist,hlist,varl)
   end;

procedure ofsf_eliminategsys(xi,flist,glist,hlist,varl);
   % Elimination of all branches of corresponding Green Groebner System. [xi]
   % is a conjunctionof atomic formulas. [flist], [glist] and [hlist]
   % are lists of SF's, [varl] is a list of identifiers. Returns a
   % quantifier-free formula.
   begin scalar s,ita,gb,dim,psi;integer i;
      if !*rlverbose then
	 ioto_tprin2 {"++ computing green Groebner system ... "};
      s := ofsf_ggsys(flist,varl,xi);
      if !*rlverbose then
	 ioto_prin2 {"done"};
      psi := 'false;
      for each branch in s do <<
	 if !*rlverbose then <<
	    i := i+1;
	    ioto_tprin2 {"++ ",length s - i + 1, " branch(es) left"}
	 >>;
	 ita := ofsf_mkconj car branch;
      	 gb := cadr branch;
	 if !*rlhqevb then
	    ioto_tprin2 {"++ computing dimension of branch ... "};
	 dim := ofsf_dim(gb,varl);
	 if !*rlhqevb then
	    ioto_tprin2 "done";
	 psi := ofsf_or(psi,ofsf_eliminatedim(gb,glist,hlist,varl,dim,ita,xi))
      >>;
      if !*rlverbose then
	 ioto_tprin2 {"+++ leaving QENF (",cl_atnum psi, ")"};
      return psi
   end;

procedure ofsf_eliminatedim(gb,glist,hlist,varl,dim,ita,xi);
   % Case disjunction depending of dimension of $Id(gb)$. [gb],[glist],and
   % [hlist] are lists of SF's, [varl] is a list of identifiers, [dim] is a
   % list of the form $(d ivarl)$, where $d$ indicates the dimension of the
   % ideal and $ivarl$ a maximally independent set of identifiers, [ita] is
   % a list of atomic formulas, [xi] is a conjunction of atomic formulas.
   % Returns a quantifier-free formula.
   if car dim = -1 then
      'false
   else if car dim = 0 then
      ofsf_and(ita,ofsf_d0main(gb,varl,glist,hlist))
   else if car dim = length varl then
      ofsf_and(ita,ofsf_casedimn(ita,xi,varl,glist,hlist))
   else
      ofsf_and(ita,ofsf_casedim(ita,xi,ofsf_remvarl(varl,cadr dim),
	 cadr dim,gb,glist,hlist));

procedure ofsf_caser0(xi,varl,glist,hlist);
   % Case, that there are no equations in phi.
   % [xi] is a conjunction of atomic formulas, [varl] is a list of identifiers,
   % [glist] and [hlist] are lists of SF's. Returns a
   % quantifier-free formula.
   begin scalar xn,remvarl,psi,phi12,phi3,phi4,neqlist;
%      if !*rlverbose then
%	 ioto_prin2 "[case #f = 0]";
      if !*rlhqevarsel then <<
	 xn := ofsf_selectxn(varl,glist);
	 remvarl := setdiff(varl,{xn})
      >>
      else <<
	 xn := car varl;
	 remvarl := cdr varl
      >>;
      if !*rlhqevb then
	 ioto_tprin2 "++ transforming Matrix in case #f = 0 ...";
      psi := ofsf_transformmatrix(xn,glist,hlist);
      if !*rlhqevb then
	 ioto_tprin2 " done";
      neqlist := car psi;
      phi12 := cadr psi;
      phi3 := caddr psi;
      phi4 := cadddr psi;
      psi := ofsf_and(neqlist,phi12);
      if psi = 'true then <<
      	 if !*rlverbose then
	    ioto_tprin2 {"+++ leaving QENF [r=0] (0)"};
      	 return psi
      >>;
      if !*rlhqevb then
	 ioto_tprin2 {"++ Eliminating phi3: ",length phi3," subformulas"};
      psi := ofsf_or(psi,ofsf_eliminatephi34(xi,phi3,xn,glist,neqlist));
      if !*rlhqevb then <<
	 ioto_tprin2 {"++ phi3 eliminated."};
      	 ioto_tprin2 {"++ eliminating phi4: ",length phi4," subformulas"}
      >>;
      psi := ofsf_or(psi,ofsf_eliminatephi34(xi,phi4,xn,glist,neqlist));
      if !*rlhqevb then
	 ioto_tprin2 {"++ phi4 eliminated."};
      if !*rlverbose then
	    ioto_tprin2 {"+++ leaving QENF [r=0] (",cl_atnum psi,
	       ")"};
      if psi = 'true or psi = 'false then
	 return psi
      else if not null remvarl then
	 if !*rlhqedim0 then
	    return ofsf_mknewf2(remvarl,psi)
	 else
	    return ofsf_hqe0 ofsf_and(xi,ofsf_mknewf2(remvarl,psi))
      else
	 return psi
   end;

procedure ofsf_eliminatephi34(xi,phi34,xn,glist,neqlist);
   % Eliminate conjunctions of $phi3$ and $phi4$. [xi] is a conjunction of
   % atomic formulas, [phi34] is a list of the form
   % $(... (condl f) ...)$, where $condl$ is a list of lists of atomic
   % formulas and $f$ a SF, [xn] is an identifier, [glist] is a list of SF's,
   % [neqlist] is a quantifier-free formula. Returns a quantifier-free formula.
   begin scalar foundtrue,condl,c,phi;
      phi := 'false;
      while not null phi34 and not foundtrue do <<
	 condl := caar phi34;
	 if !*rlhqevb then
	    ioto_tprin2 {"+ Eliminating subformula of phi3/phi4, ",
	       length condl," cases..."};
	 while not null condl and not foundtrue do <<
	    if !*rlhqevb then
	       ioto_tprin2 "checking consistence ... ";
	    c := ofsf_consistent(xi,car condl);
	    if !*rlhqevb then
	       ioto_prin2 "done.";
	    if car c then
	       phi := ofsf_or(phi,ofsf_and(neqlist,ofsf_and(cadr c,
		  ofsf_qenfcase0(car condl,xn,glist,cadar phi34))));
	    if phi = 'true then
	       foundtrue := t;
	    condl := cdr condl
	 >>;
	 phi34 := cdr phi34
      >>;
      return phi
   end;

procedure ofsf_selectxn(varl,glist);
   % Selects variable, depending on switch rlhqevarselx;.
   % [varl] is a list of identifiers, [glist] is a list of SF's. Returns an
   % identifier.
   begin scalar res;
     if !*rlhqevb then
       ioto_prin2 "selecting Xn in case r=0 ...";
     if !*rlhqevarselx then
       res := ofsf_selectxn2(varl, glist)
     else
       res := ofsf_selectxn1(varl,glist);
     if !*rlhqevb then
       ioto_prin2 " done.";
     return res
   end;

procedure ofsf_selectxn1(varl,glist);
   % Select variable, so that the residue class rings have minimal dimension.
   % [varl] is a list of identifiers, [glist] is a list of SF's. Returns an
   % identifier.
   begin scalar vl2,d,oldorder,d2,du,elem1;integer d1;
      if null glist or null cdr varl then
      	 return car varl;
      vl2 := varl;
      while not null vl2 do <<
	 oldorder := setkorder {car vl2};
	 d1 := 0;
	 du := nil;
	 for each elem in glist do <<
	    elem1 := reorder elem;
	    if domainp elem1 or mvar elem1 neq car vl2 then
 	       d2 := 0
	    else
	       d2 := ldeg elem1;
	    if d2 > d1 then
	       d1 := d2;
	    if null du or d2 < du then
               du := d2;
	 >>;
	 if null d or d1 <= car d then <<
	    d := {du,d1,car vl2};
            vl2 := cdr vl2
         >> else <<
            vl2 := nil;
            d := nil
         >>;
	 setkorder oldorder
      >>;
      if not null d then
         return caddr d
      else
         return car varl
   end;

procedure ofsf_selectxn2(varl,glist);
   % Alternative version of ofsf_selectxn. [varl] is a list of identifiers,
   % [glist] is a list of SF's. Returns an identifier.
   begin scalar vl2,l,xl,oldorder,elem1,dl;
     if null glist or null cdr varl then
      	 return car varl;
     vl2 := varl;
     dl := ofsf_difference glist;
     while not null vl2 do <<
	 oldorder := setkorder {car vl2};
	 xl := ofsf_gethexponent(car vl2,glist);
	 for each elem in dl do <<
	    elem1 := reorder elem;
	    if domainp elem1 or mvar elem1 neq car vl2 then
 	       xl := 0 . xl
	    else
	       xl := (ldeg elem1) . xl
	 >>;
         l := ((car vl2) . sort(xl,'geq)) . l;
         setkorder oldorder;
         vl2 := cdr vl2
     >>;
     return ofsf_getminvar l
   end;

procedure ofsf_difference(l);
   % Computes all differences from elements of a list. [l] is a list of SF's.
   % Returns a list.
   begin scalar res;
      while not null cdr l do <<
	 for each elem in cdr l do
	    res := lto_insert(addf(car l, negf elem),res);
	 l := cdr l
      >>;
      return res
   end;

procedure ofsf_gethexponent(x,l);
   % Gets highest exponent of a variable of each elemnt of a list. [x] is an
   % identifier, [l] is a list of SF's. Returns a list of integers.
   begin scalar xl,elem1;
      for each elem in l do <<
      	 elem1 := reorder elem;
	 if domainp elem1 or mvar elem1 neq x then
 	       xl := 0 . xl
	    else
	    xl := (ldeg elem1 - 1) . xl
      >>;
      return xl
   end;

procedure ofsf_getminvar(l);
   % Subroutine of ofsf_selectxn. [l] is a list of the form $(v i1 ... is)$,
   % where $v$ is an identifier and $i1$,...,$in$ are integers. Returns an
   % identifier.
   begin scalar res,m;
      if null cdar l then
	 return caar l;
      for each elem in l do <<
       	 if null res or cadr elem < m then <<
            res := {(car elem) . cddr elem};
            m := cadr elem
       	 >> else if cadr elem = m then
            res := ((car elem) . cddr elem) . res
      >>;
      if length res = 1 then
       	 return caar res
      else
       	 return ofsf_getminvar res
   end;

procedure ofsf_consistent(xi,cond);
   % Tests, if both arguments are consistent. [xi] and [cond] are formulas.
   % Returns a list with first element T or NIL and second element the
   % difference of [cond] and [xi].
   begin scalar xi2,cond2;
      if xi eq 'true then
	 return {t, cond}
      else if cond eq 'true then
	    return {t,'true};
      xi2 := if rl_op xi neq 'and then
	 {xi}
      else
	 cdr xi;
      cond2 := if rl_op cond neq 'and then
	 {cond}
      else
	 cdr cond;
      return ofsf_consistent1(list2set xi2,cond2)
   end;

procedure ofsf_consistent1(xi,cond);
   % Subroutine of ofsf_consistent. [xi] and [cond] are lists of atomic
   % formulas. Returns a list with first element T or NIL and second element
   % the difference of [cond] and [xi].
   begin scalar found,xi1,cond1;
      xi1 := xi;
      cond1 := cond;
      while not found and not null xi1 do <<
	 while not null cond1 do <<
	    if rl_op car xi1 eq 'equal and rl_op car cond1 eq 'neq and
	       quotf(ofsf_arg2l car cond1, ofsf_arg2l car xi1) then <<
	       	  cond1 := nil;
	       	  found := t
	       >>
	    else if rl_op car cond1 eq 'equal and
		  rl_op car xi1 member {'neq,'greaterp,'lessp} and
		     quotf(ofsf_arg2l car xi1, ofsf_arg2l car cond1) then <<
		     	cond1 := nil;
		     	found := t
		     >>
	    else
	       cond1 := cdr cond1
	 >>;
	 cond1 := cond;
	 xi1 := cdr xi1
      >>;
      if found then
	 return {not found}
      else
      	 return {not found, rl_smkn('and,setdiff(cond,xi))}
   end;

procedure ofsf_transformmatrix(xn,gl,hl);
   % Matrix transformation w.r.t inner bounded variable. [xn] is an
   % identifier, [gl] and [hl] are lists of SF's.
   % Returns a list: $ ( (and (neq hk 0) ... ) (or phi1 phi2) phi3 phi4) $.
   begin scalar xn,neqlist,glist,phi1,phi2,phi3,phi4,
	 phi3phi4,phi1orphi2;
      neqlist := hl;
      glist := gl;
      if not null glist then <<
	 phi3phi4 := ofsf_getphi3phi4(xn,glist);
	 phi3 := car phi3phi4;
	 phi4 := cadr phi3phi4;
	 phi1 := ofsf_buildphi1(xn,glist);
	 phi2 := ofsf_buildphi2(xn,glist)
      >>;
      neqlist := ofsf_buildhkneq0(xn,neqlist);
      if null phi1 then
	 phi1orphi2 := 'true
      else
	 phi1orphi2 := ofsf_or(phi1, phi2);
      return {neqlist, phi1orphi2, phi3,phi4}
   end;

procedure ofsf_getphi3phi4(xn,phi);
   % Construction of conditions for formulas $ phi3 $ and $ phi4 $.
   % [xn] is an identifier,[phi] is a list of SF's. Result has
   % the form: $ (..(condl equation)..) $, where $condl$ is a list of atomic
   % formulas and $equation$ is a SF.
   begin scalar conj,phi3,phi4;
      for each formulal on phi do <<
	 conj := ofsf_getconj3(xn,car formulal);
	 if conj neq 'false then
	    phi3 := conj . phi3;
	 for each formula in cdr formulal do <<
	    conj := ofsf_getconj4(xn,car formulal,formula);
  	    if conj neq 'false then
	       	  phi4 := conj . phi4
	 >>
      >>;
      phi3 := list2set phi3;
      phi4 := list2set phi4;
      return {phi3, phi4}
   end;

procedure ofsf_getconj3(xn,f);
   % Subroutine of ofsf_getphi3phi4. [xn] is an identifier, [f] is a SF.
   % Result is FALSE or a list of the form $ (cond f)$,
   % where $cond$ is list of formulas and $f$ is a SF.
   % If one formula of $cond$ is true, then f isn't a
   % constant function. $cond$ may also be {TRUE}, if f definitly isn't a
   % constant function wrt. [xn].
   begin scalar d,notconst;
      d := numr difff(f,xn);
      notconst := ofsf_getnotconst(xn,d);
      if notconst = 'true then
	 return {{'true}, d}
      else if notconst = 'false then
	 return 'false
      else
	 return {notconst,d}
   end;

procedure ofsf_getneq0f(xn,f);
   % Conditions for not zero-function.[xn] is an identifier, [f] is a SF.
   % Result is the formula, that f isn't the constant function 0 without OR.
   begin scalar res,df,oldorder;
      oldorder := setkorder(xn . kord!*);
      df := reorder f;
      setkorder oldorder;
      if null df then
	 return 'false;
      while not null df do
	 if domainp df then <<
	    df := nil;
	    res := nil
	 >>
	 else if mvar df neq xn then <<
	    res := ofsf_0mk2('neq, df) . res;
	    df := nil
	 >>
	 else if domainp lc df then <<
	    df := nil;
	    res := nil
	 >>
	 else <<
	    res := ofsf_0mk2('neq, lc df) . res;
	    df := red df
	 >>;
      if null res then
	 return 'true
      else
	 return list2set res
   end;


procedure ofsf_getneq0fgen(xn,f);
   % Conditions for not zero-function in generic case.[xn] is an
   % identifier, [f] is a SF. Result is the formula, that f isn't the
   % constant function 0 without OR.
   begin scalar res,df,oldorder;
      oldorder := setkorder(xn . kord!*);
      df := reorder f;
      setkorder oldorder;
      if null df then
	 return 'false;
      while not null df do
	 if domainp df then <<
	    df := nil;
	    res := nil
	 >>
	 else if mvar df neq xn then <<
	    res := ofsf_0mk2('neq, df) . res;
	    df := nil
	 >>
	 else if domainp lc df then <<
	    df := nil;
	    res := nil
	 >>
         else if not intersection(kernels lc df, ofsf_hqexvars!*) then <<
            ofsf_hqetheo!* := ofsf_0mk2('neq, lc df) . ofsf_hqetheo!*;
            df := nil;
            res := nil
         >> else <<
	    res := ofsf_0mk2('neq, lc df) . res;
	    df := red df
	 >>;
      if null res then
	 return 'true
      else
	 return list2set res
   end;

procedure ofsf_getconj4(xn,f1,f2);
   % Subroutine of ofsf_getphi3phi4. [xn] is an identifier, [f1], [f2] are
   % SF's. Result is FALSE or a list of the form $ (cond d)$,
   % where $cond$ is list of formulas and $d$ is a SF.
   % If one formula of $cond$ is true,
   % then $d := f1 - f2$ isn't a constant function. $cond$ may also be {TRUE},
   % if $f1 - f2$ definitly isn't a constant function wrt to [xn].
   begin scalar s,notconst;
      s := addf(f1,negf f2);
      notconst := ofsf_getnotconst(xn,s);
      if notconst = 'true then
	 return {{'true}, s}
      else if notconst = 'false then
	 return 'false
      else
	 return {notconst,s}
   end;

procedure ofsf_getnotconst(xn,f);
   % Conditions for "not constant function" wrt to [xn]. [xn] is an identifier,
   % [f] is a SF. Result is the formula, that f isn't a
   % constant function wrt. [xn] without OR.
   begin scalar res,df,oldorder;
      oldorder := setkorder(xn . kord!*);
      df := reorder f;
      if !*rlhqegen then
         res := ofsf_getnotconstfgen(xn,df,nil)
      else
         res := ofsf_getnotconstf(xn,df,nil);
      setkorder oldorder;
      return res
   end;

procedure ofsf_getnotconstf(xn,f,l);
   % Conditions for "not constant function" wrt to [xn]. [xn] is an identifier,
   % [f] is a SF, [l] is a list. Result is the formula, that f isn't a
   % constant function wrt. [xn] without OR.
   if domainp f or mvar f neq xn then
      if null l then
	 'false
      else
	 cdr l
   else if domainp lc f then
      if null l then
	 'true
      else l
   else if null l then
      ofsf_getnotconstf(xn,red f,
	 {ofsf_0mk2('equal, ofsf_normcond lc f),
	    ofsf_0mk2('neq, ofsf_normcond lc f)})
   else if car l eq 'false then
      cdr l
   else if car l eq 'true then
      l
   else if ofsf_quottest(sfto_sqfpartf lc f,car l) then
      ofsf_getnotconstf(xn,red f,l)
   else
      ofsf_getnotconstf(xn,red f,
	 ofsf_and1(ofsf_0mk2('equal, ofsf_normcond lc f),car l) .
	    ofsf_and1(ofsf_0mk2('neq, ofsf_normcond lc f),
	       car l) . cdr l);

procedure ofsf_normcond(f);
   % Normalize Condition. [f] is a SF. Returns a SF,
   % its squarefree part without primitive part.
   sfto_sqfpartf sfto_dprpartf f;

procedure ofsf_getnotconstfgen(xn,f,l);
   % Conditions for "not constant function" wrt to [xn] in generic
   % case. [xn] is an identifier, [f] is a SF. Result is the formula,
   % that f isn't a constant function wrt. [xn] without OR.
   if domainp f or mvar f neq xn then
      if null l then
	 'false
      else
	 cdr l
   else if domainp lc f then
      if null l then
	 'true
      else l
   else if not intersection(kernels lc f, ofsf_hqexvars!*) then <<
      ofsf_hqetheo!* := ofsf_0mk2('neq, lc f) . ofsf_hqetheo!*;
      if null l then
         'true
      else l
   >> else if null l then
      ofsf_getnotconstf(xn,red f,
	 {ofsf_0mk2('equal, ofsf_normcond lc f),
	    ofsf_0mk2('neq, ofsf_normcond lc f)})
   else if car l eq 'false then
      cdr l
   else if car l eq 'true then
      l
   else if ofsf_quottest(sfto_sqfpartf lc f,car l) then
      ofsf_getnotconstf(xn,red f,l)
   else
      ofsf_getnotconstf(xn,red f,
	 ofsf_and1(ofsf_0mk2('equal, ofsf_normcond lc f),car l) .
	    ofsf_and1(ofsf_0mk2('neq, ofsf_normcond lc f),
	       car l) . cdr l);

procedure ofsf_quottest(f1,f2);
   % Tests, if an element of [f2] divides [f1]. [f1] is a SF, [f2] is a
   % conjunction of equalities. Returns T or NIL.
   if rl_op f2 eq 'equal then
      ofsf_quottest1(f1,{ofsf_arg2l f2})
   else
      ofsf_quottest1(f1,for each elem in cdr f2 collect ofsf_arg2l elem);

procedure ofsf_quottest1(f1,f2);
   % Tests, if an element of [f2] divides [f1]. [f1] is a SF, [f2] is a
   % list of SF's. Returns T or NIL.
   if null f2 then
      nil
   else if quotf(f1,car f2) then
      t
   else
      ofsf_quottest1(f1,cdr f2);

procedure ofsf_inf(xn,f);
   % Formula, so that f is positive for x->oo. [xn] is an identifier,
   % [f] is SF. Result is a formula.
   begin scalar res, oldorder;
      oldorder := setkorder(xn . kord!*);
      res := ofsf_inf1(xn,reorder f);
      setkorder oldorder;
      return res
   end;

procedure ofsf_inf1(xn,f);
   % Formula, so that f is positive for x->oo. [xn] is an identifier,
   % [f] is a SF. Result is a formula.
   if null f then
      'false
   else if domainp f then
      if f > 0 then
	 'true
      else
	 'false
   else if mvar f neq xn then
      ofsf_0mk2('greaterp, f)
   else if domainp lc f then
      if (lc f) > 0 then
	 'true
      else
	 'false
   else
      ofsf_or(ofsf_0mk2('greaterp, lc f),
	 ofsf_and(ofsf_0mk2('equal, lc f),
	    ofsf_inf1(xn,red f)));

procedure ofsf_minf(xn,f);
   % Formula, so that f is positive for x->-oo. [xn] is ans identifier,
   % [f] is a SF. Result is a formula.
   begin scalar res, oldorder;
      oldorder := setkorder(xn . kord!*);
      res := ofsf_minf1(xn,reorder f);
      setkorder oldorder;
      return res
   end;

procedure ofsf_minf1(xn,f);
   % Formula, so that f is positive for x->-oo. [xn] is an identifier,
   % [f] is a SF. Result is a formula.
   if null f then
      'false
   else if domainp f then
      if f > 0 then
	 'true
      else
	 'false
   else if mvar f neq xn then
      ofsf_0mk2('greaterp, f)
   else if domainp lc f then
      if multf(lc f,exptf(numr simp '(minus 1),ldeg f)) > 0 then
	 'true
      else
	 'false
   else
      ofsf_or(ofsf_0mk2('greaterp,
	 multf(lc f,exptf(numr simp '(minus 1),ldeg f))),
	 ofsf_and(ofsf_0mk2('equal, lc f),ofsf_minf1(xn,red f)));

procedure ofsf_buildphi1(xn,glist);
   % Construction of $ phi1 $. [xn] is an identifier, [glist] is a list of
   % SF's. Result is a formula.
   begin scalar glist2,conj1,phi1;
      glist2 := glist;
      phi1 := 'true;
      while not null glist2 do <<
	 conj1 := ofsf_inf(xn,car glist2);
	 if conj1 = 'false then <<
	    glist2 := nil;
	    phi1 := 'false
	 >>
	 else <<
	    glist2 := cdr glist2;
	    phi1 := ofsf_and(phi1,conj1)
	 >>
      >>;
      return phi1
   end;

procedure ofsf_buildphi2(xn,glist);
   % Construction of $ phi2 $. [xn] is an identifier, [glist] is a list of
   % SF's. Result is a formula.
   begin scalar phi2,conj1,glist2;
      glist2 := glist;
      phi2 := 'true;
      while not null glist2 do <<
	 conj1 := ofsf_minf(xn,car glist2);
	 if conj1 = 'false then <<
	    glist2 := nil;
	    phi2 := 'false
	 >>
	 else <<
	    glist2 := cdr glist2;
	    phi2 := ofsf_and(phi2,conj1)
	 >>
      >>;
      return phi2
   end;

procedure ofsf_buildhkneq0(xn,neqlist);
   % Construction of conjunction, that each $ h_{k} $ isn't the zero-function.
   % [xn] is an identifier, [neqlist] is a list of SF's. Result is a
   % formula.
   begin scalar res,n2,conj1;
      n2 := neqlist;
      while not null n2 do <<
         if !*rlhqegen then
            conj1 :=  ofsf_getneq0fgen(xn,car n2)
         else
    	    conj1 := ofsf_getneq0f(xn,car n2);
	 if conj1 = 'true then
	    n2 := cdr n2
	 else if conj1 = 'false then <<
	    res := 'false;
	    n2 := nil
	 >>
	 else <<
	    res := rl_smkn('or,conj1) . res;
	    n2 := cdr n2
	 >>
      >>;
      if res member {'true,'false} then
	 return res
      else
         return ofsf_rqsimpl rl_smkn('and,res)
   end;

procedure ofsf_and(f1,f2);
   % Conjunction. [f1] and [f2] are formulas. Returns the conjunction
   % $ f1 and f2 $.
   begin scalar phi;
      if !*rlhqevb then
	 ioto_tprin2 {"simplifying a conjunction ... "};
      phi := cl_simpl(rl_smkn('and,{f1,f2}),nil,-1);
      if !*rlhqevb then
	 ioto_prin2 "done";
      return phi
   end;

procedure ofsf_or(f1,f2);
   % Conjunction. [f1] and [f2] are formulas. Returns the disjunction
   % $ f1 and f2 $.
   begin scalar !*rlsiexpla,!*rlsipw,phi;
      if !*rlhqevb then
	 ioto_tprin2 {"simplifying a disjunction ... "};
      phi := cl_simpl(rl_smkn('or,{f1,f2}),nil,-1);
      if !*rlhqevb then
	 ioto_prin2 "done";
      return phi
   end;

procedure ofsf_and1(f1,f2);
   % Conjunction. [f1] and [f2] are formulas. Returns the disjunction
   % $ f1 and f2 $ without changing atomic formula relations.
   begin scalar !*rlsiexpla,!*rlsipw,phi;
      if !*rlhqevb then
	 ioto_tprin2 {"simplifying a disjunction ... "};
      phi := cl_simpl(rl_smkn('and,{f1,f2}),nil,-1);
      if !*rlhqevb then
	 ioto_prin2 "done.";
      return phi
   end;

procedure ofsf_mknewf2(varl,f);
   % Construction of new formula. [varl] is a list of identifiers, [f] is
   % a quantifier-free formula. Returns a formula, $ ex(x1,...,ex(xn,f))$.
   % [varl] has the form $ (xn ... x1) $.
   if null varl then f else
      ofsf_mknewf2(cdr varl,rl_mkq('ex, car varl, f));

procedure ofsf_qenfcase0(condl,xn,gl,f);
   % Quantifier Elimination in the case of r=0. [condl] is a conj. of
   % conditions, [xn] is an identifier, [gl] is a list of SF's, [f] is a SF.
   % Returns a formula.
   begin scalar oldorder,psi,sf_condl,sf_f;
%      if !*rlverbose then
%	 ioto_tprin2 {"++++ Eliminating case #f = 0 with #g:",length gl};
      oldorder := setkorder(xn . kord!*);
      sf_f := reorder f;
      if condl = 'true then
	 sf_condl := nil
      else if rl_op condl neq 'and then
	 sf_condl := {condl}
      else
	 sf_condl := cdr condl;
      sf_f := ofsf_deletemon(xn,sf_f,sf_condl,sf_condl);
      setkorder oldorder;
      sf_f := reorder sf_f;
      psi := ofsf_d0main({sf_f},{xn},gl,nil);
%      if !*rlverbose then
%	 ioto_tprin2 {"+++++ Case #f = 0 eliminated."};
      return psi
   end;

procedure ofsf_deletemon(xn,f,l1,l2);
   % Delete leading monomials, which are Zero in [l]. [xn] is an identifier,
   % [f] is a SF, [l1]  and [l2] are lists of atomic formulas. Returns a SF.
   % [f] has at least degree 1 wrt. [xn].
   if null l2 or domainp f then
      f
   else if rl_op car l2 eq 'equal then
      if mvar f neq xn then
	 if quotf(f,ofsf_arg2l car l2) then
	    nil
	 else
	    ofsf_deletemon(xn,f,l1,cdr l2)
      else if quotf(lc f,ofsf_arg2l car l2) then
	 ofsf_deletemon(xn,red f,l1,l1)
      else
	 ofsf_deletemon(xn,f,l1,cdr l2)
   else
      ofsf_deletemon(xn,f,l1,cdr l2);

procedure ofsf_ggsys(l,varl,xi);
   % Green Groebner system computation. [l] is a list of SF's, [varl]
   % is a list of identifiers, [xi] is a conjunction of atomic
   % formulas. Returns a list of the form: $ (.. (cond gb) ...)$,
   % where $cond$ is a list of conditions and $gb$ a groebner basis
   % with SF's. [xi] should be the initial condition. ATTENTION: Due
   % to historical accident ofsf_ggsys means _green_ (non-faithful)
   % gsys.
   begin scalar !*cgbreal,!*cgbfaithful,cdl,gsys;%!*cgbgs;
      !*cgbreal := t;
      % !*cgbgs := t;
      if !*rlhqetheory then
	 cdl := ofsf_mkcondlist xi;
      if !*rlhqegen then <<
         gsys :=  cgb_ggsysf(l,cdl,ofsf_hqexvars!*,varl,'revgradlex,nil);
	 ofsf_hqetheo!* := append(ofsf_buildtheory(gsys,cdl),ofsf_hqetheo!*);
	 gsys := ofsf_buildgenggsys gsys
      >> else
      	 gsys := cgb_gsysf(l,cdl,varl,'revgradlex,nil);
      if !*rlhqegbred then
      	 return ofsf_gbred(gsys,varl)
      else
	 return gsys
   end;

procedure ofsf_gbred(gsys,varl);
   % Computation of a reduced Groebner system. [gsys] is a Groebner system,
   % [varl] is a list of identifiers. Returns a Groebner system.
   for each elem in gsys collect {car elem, ofsf_gbred1(cadr elem,varl)};

procedure ofsf_gbred1(gb,varl);
   % Computation of a reduced Groebner basis. [gb] is a list of SF's.
   % [varl] is a list of identifiers. Returns a list of SF's.
   begin scalar old,gb1;
      old := vdp_init(varl,'revgradlex,nil);
      gb1 := for each elem in gb collect vdp_f2vdp elem;
      gb1 := gb_traverso!-final gb1;
      gb1 := for each elem in gb1 collect vdp_2f elem;
      vdp_cleanup old;
      return gb1
   end;

procedure ofsf_mkconj(condlist);
   % Make conjunction. [condlist] is a list of conditions. Returns a formula.
   if null condlist or condlist eq 'true then
      'true
   else if null cdr condlist then
      car condlist
   else
      rl_smkn('and,condlist);

procedure ofsf_casedimn(ita,xi,varl,glist,hlist);
   % Construction of the formula in the case, that ideal has dimension n.
   % [ita], [xi] are conjunctions of atomic formulas, [varl] is a list of
   % identifiers, [glist] and [hlist] are lists of SF's. Returns a formula.
   if !*rlhqedim0 then
      ofsf_mknewf2(varl,ofsf_sfl2f(nil,glist,hlist))
   else
      ofsf_qenf(ofsf_and1(ita,xi),nil,glist,hlist,varl);

procedure ofsf_sfl2f(fl,gl,hl);
   % Build conjunction of $>$-relations and inequalities. [fl], [gl] and [hl]
   % are lists of SF's. Returns a formula.
   begin scalar fl1,gl1,hl1;
      fl1 := for each elem in fl collect ofsf_0mk2('equal,elem);
      gl1 := for each elem in gl collect ofsf_0mk2('greaterp,elem);
      hl1 := for each elem in hl collect ofsf_0mk2('neq,elem);
      return rl_smkn('and,append(fl1,append(gl1,hl1)))
   end;

procedure ofsf_remvarl(varl,ivarl);
   % Gets remainder variables of maximal set of independent variables.
   % [varl] and [ivarl] are lists of identifiers. Returns a list of
   % identifiers.
   setdiff(varl,ivarl);

procedure ofsf_casedim(ita,xi,bvarl,ivarl,gb,glist,hlist);
   % Construction of the formula in the case, that ideal has dimension 1,..n-1.
   % [ita], [xi] are conjunctions of atomic formulas, [bvarl] and [ivarl] are
   % lists of identifiers, [gb], [glist] and [hlist] are lists of SF's. Returns
   % a formula.
   begin scalar newpsi,newgb,newglist,newhlist,theo;
      if !*rlhqedim0 then
	 return ofsf_mknewf2(append(bvarl,ivarl),ofsf_sfl2f(gb,glist,hlist));
      newgb := ofsf_sort(gb,bvarl);
      newglist := ofsf_sort(glist,bvarl);
      newhlist := ofsf_sort(hlist,bvarl);
      theo := ofsf_mktheo(car newgb,car newglist,car newhlist);
      newpsi := ofsf_rqsimpl(rl_smkn('and,{theo,ofsf_qenf(ofsf_and1(theo,
	 ofsf_and1(ita,xi)),cadr newgb,cadr newglist,cadr newhlist,bvarl)}));
      return ofsf_hqe0 ofsf_mknewf2(ivarl,newpsi)
   end;

procedure ofsf_rqsimpl(phi);
   % Formula Simplifier. [phi] is a formula. Returns a formula.
   begin scalar phi1;
      if !*rlhqevb then
	 ioto_tprin2 {"simplifying formula with ",cl_atnum phi,
	    " atomic formulas ... "};
      phi1 := cl_simpl(phi,nil,-1);
      if !*rlhqevb then
	 ioto_prin2 {"done (", cl_atnum phi1, ")"};
      return phi1
   end;


procedure ofsf_sort(l,varl);
   % Sorts list depending, if elements have variables of [varl]. [l] is a list
   % of SF's and [varl] is a list of identifiers. Returns a list of the form
   % $ (il dl) $, where $il$ are all elements of [l], which haven't variables
   % of [varl], $dl$ are the remaining elements.
   begin scalar l1,il,dl;
      l1 := l;
      while not null l1 do <<
   	 if intersection(kernels car l1,varl) then
	    dl := car l1 . dl
	 else
	    il := car l1 . il;
	 l1 := cdr l1
      >>;
      return {il,dl}
   end;

procedure ofsf_mktheo(fl,gl,hl);
   % Make conjunction. [fl], [gl] and [hl] are lists of SF's. Returns a
   % formula.
   begin scalar res;
      res := for each elem in fl collect ofsf_0mk2('equal,elem);
      res := append(for each elem in gl collect ofsf_0mk2('greaterp,elem),res);
      res := append(for each elem in hl collect ofsf_0mk2('neq,elem),res);
      return rl_smkn('and,res)
   end;

procedure ofsf_mkcondlist(conj);
   % Build condition list for call of cgb_gsys. [c] is a conjunction of
   % atomic formulas. Returns a list of atomic formulas. All $<$- and
   % $>$-relations become inequations.
   begin scalar l,cdl,cd;
      if conj eq 'true then
      	 return nil;
      l := if rl_op conj neq 'and then
      	 {conj}
      else
      	 cdr conj;
      while not null l do <<
      	 cd := ofsf_getcdform car l;
      	 if not null cd then
	    cdl := cd . cdl;
      	 l := cdr l
      >>;
      return cdl
   end;

procedure ofsf_getcdform(af);
   % Subroutine of ofsf_mkcdlist. [af] is an atomic formula. Returns an
   % atomic formula.
   if rl_op af member {'equal,'neq} then
      af
   else if rl_op af member {'greaterp,'lessp} then
      ofsf_0mk2('neq, ofsf_arg2l af);

procedure ofsf_buildtheory(gsys,icd);
   % Construction of a Theory. [gsys] Is a Groebner System, [cd] is a
   % initial condition. Returns a Theory, a list of inequations.
   begin scalar cdl,res;
      cdl := for each elem in gsys join car elem;
      cdl := lto_setminus(cdl,rl_thsimpl icd);
      for each elem in cdl do <<
	 if rl_op elem eq 'neq and
	    not intersection(kernels ofsf_arg2l elem,ofsf_hqexvars!*) then
	    res := elem . res
      >>;
      return rl_thsimpl res
   end;

procedure ofsf_buildgenggsys(gsys);
   % Removes Conditions from branches, which are in a theory. [gsys] is a
   % Groebner System. Returns a Groebner System.
   for each branch in gsys collect
      if eqcar(branch,'true) then
	 branch
      else
      	 {lto_setminus(car branch,ofsf_hqetheo!*),cadr branch};

% -----------------------------------------------------------------------------
% Case of zero-dimensional ideal
% -----------------------------------------------------------------------------

% Computes a formula, so that the ideal has a zero and the conditions hold.
%
% Input: A Groebner Basis $ gb $ of the ideal, a list $ varl $ of main
% variables, a list $ greaterlist $ of polynomials, which should have positive
% values, and a list $ neqlist $ of polynomials, which shouldn't bee zero.
%
% Output: A quantifier-free formula

procedure ofsf_d0main(gb,varl,greaterlist,neqlist);
   % [gb],[greaterlist] and [neqlist] are lists of SF's, [varl] is a list of
   % identifiers. Returns a list of SQ's, the coefficients of the
   % characteristic polynomial in the order $ c_{0},...,c_{n} $.
   begin scalar beta,chi,helist,y,coeffl,qf,vdp_gb,vsbasis,old,oldorder;
      integer i;
      if !*rlverbose then
	 ioto_tprin2 {"+ begin d0: r:",length gb," n:",
	    length varl," s:",length greaterlist," t:",length neqlist};
      helist := ofsf_buildhelist(greaterlist,neqlist);
      old := vdp_init(varl,'revgradlex,nil);
      vdp_gb := ofsf_redgroebner for each elem in gb collect vdp_f2vdp elem;
      if !*rlhqevb then
      	 ioto_tprin2
	    "+ computing residue class ring vector space basis ... ";
      vsbasis := ofsf_gvsbasis(ofsf_gb2gltb vdp_gb,varl);
      if !*rlhqevb then
      	 ioto_prin2 {"done. Dimension: ",length vsbasis};
      if !*rlhqestrconst then <<
	 vsbasis := for each elem in vsbasis collect vdp_f2vdp elem;
	 beta := gbsc_strconst(vsbasis,vdp_gb,4)
      >>;
      y := intern gensym();
      oldorder := setkorder(y . kord!*);
      chi := if not !*rlhqetfcsplit then simp 1;
      if !*rlverbose then
	 ioto_tprin2 {"computing characteristic ",
	    ioto_cplu("polynomial",length helist neq 1),":"};
      for each elem in helist do <<
 	 if !*rlverbose then <<
	    i := i+1;
	    ioto_prin2 {" ",length helist-i+1};
	 >>;
	 if !*rlhqetfcsplit then
	    chi := ofsf_d0main1(vdp_gb,vsbasis,beta,elem,y) . chi
	 else
	    chi := multsq(chi,ofsf_d0main1(vdp_gb,vsbasis,beta,elem,y))
      >>;
      if !*rlverbose then
	 ioto_prin2 " done.";
      if not !*rlhqetfcsplit then <<
      	 coeffl := reversip ofsf_coefflist(chi,y);
      	 if !*rlhqevb then
	    ioto_prin2 "Done.";
      	 if !*rlverbose then
	    ioto_tprin2 {"computing type formula of length ",
	       length coeffl," ... "};
      	 qf := ofsf_tfc coeffl
      >> else <<
	 if !*rlverbose then <<
	    ioto_prin2 "constructing disjunction of type formulas ... ";
	 >>;
	 qf := ofsf_tfcmain(chi,y);
      >>;
      setkorder oldorder;
      if !*rlverbose then
%	 ioto_tprin2 {"+++++ Type Formula Computation finished: ",cl_atnum qf};
	 ioto_prin2t {"done (",cl_atnum qf,")"};
      qf := cl_simpl(rl_mk1('not,qf),nil,-1);
      if !*rlverbose then
	 ioto_tprin2 {"+ end of d0 (",cl_atnum qf,")"};
      vdp_cleanup old;
      return qf
   end;

procedure ofsf_d0main1(vdpgb,vsbasis,beta,he,charX);
   % Main procedure for zero-dimensional case.
   % [vdpgb] is a list of VDP's, [vsbasis] is a list of SF's or VDP's, [beta]
   % is a BETA, [he] is a SF, [charX] is an identifier. Returns a SF, the
   % characteristic polynomial wrt. arguments.
   ofsf_charpoly('mat . ofsf_buildq(vdpgb,he,vsbasis,beta),charX);

procedure ofsf_gb2gltb(vdpgb);
   % Head terms from Groebner basis. [vdpgb] is a list of VDP's, Returns a
   % list of SF's.
   begin scalar basis2;
      basis2 := for each elem in vdpgb collect vdp_fmon(bc_a2bc 1,
	 vdp_evlmon elem);
      basis2 := for each elem in basis2 collect vdp_2f elem;
      return basis2
   end;

procedure ofsf_gvsbasis(ltb,varl);
   % Groebner vector space basis. [ltb] is a list of SF's, [varl] is a list of
   % identifiers. Returns a list of SF's. Computes a vector space basis of
   % $ K[X]/I $: the reduced terms basis.
   begin scalar htl,basis,basis2,v,d,tt;
      htl := ofsf_mvp(ltb, varl);
      if length htl neq length varl then
	 rederr "ideal not zero dimensional";
      basis := {numr simp 1};
      for each term in htl do <<
	 v := car term;
	 d := cdr term;
	 basis2 := basis;
	 for each elem in basis do
	    for i:=1:(d-1) do <<
	       tt := multf(elem,exptf(numr simp v,i));
	       if not ofsf_redp(tt,ltb) then
		  basis := tt . basis
	    >>
      >>;
      return basis
   end;

procedure ofsf_mvp(ltb,varl);
   % Minimum variable powers. [ltb] is a list of SF's, [varl] is a list of
   % identifiers. Returns an Alist $ (...(v . d)...)$ such
   % that $v^d$ is in [ltb] and vice versa.
   begin scalar htlist,var,v,d,w;
      for each term in ltb do <<
	 var := kernels term;
	 if (length var = 1) and member(car var,varl) then <<
	    v := car var;
	    d := ldeg term;
	    w := assoc(v,htlist);
	    if w then
	       cdr w := min(cdr w, d)
	    else
	       htlist := (v . d) . htlist
	 >>
      >>;
      return htlist
   end;

procedure ofsf_redp(tt,ltb);
   % Reduction predicate. [tt] is a SF, [ltb] is a list of SF's. Returns t
   % iff [tt] is reducible w.r.t. [ltb] else NIL.
   begin scalar c;
      c := t;
      while c and ltb do <<
	 if null cdr qremf(tt,car ltb) then
	    c := nil
	 else
	    ltb := cdr ltb
      >>;
      return not c
   end;

procedure ofsf_trace(vdpgb,he,vi,vj,vsbasis);
   % Trace of $ m_{he,vi,vj} $. [vdpgb] is a list of VDP's, [he], [vi], [vj]
   % are SF's, [vsbasis] is a list of SF's. Returns a SQ.
   begin scalar res;
      res := simp 0;
      for each elem in vsbasis do
	 res := addsq(res,ofsf_trace1(vdpgb,he,vi,vj,elem));
      return res
   end;

procedure ofsf_trace1(vdpgb,he,vi,vj,elem);
   % Trace computation subroutine without use of structure constants. [he],
   % [vi], [vj], [elem] are SF's, [vdpgb] is a list of VDP's. Returns a SQ.
   ofsf_getcoeff(ofsf_prod(he,vi,vj,elem,vdpgb),vdp_f2vdp elem);

procedure ofsf_prod(he,vi,vj,elem,vdpgb);
   % Reduction step of ofsf_trace2. [he], [vi], [vj], [elem] are SF's, [vdpgb]
   % is a list of VDP's. Returns a VDP.
   gb_reduce(vdp_f2vdp multf(he,multf(vi,multf(vj,elem))),vdpgb);

procedure ofsf_getcoeff(f,vi);
   % Coefficient of [v_i] in [f]. [f], [vi] are VDP's. Returns a SQ.
   if vdp_zero!? f then
      simp 0
   else if vdp_evlmon f = vdp_evlmon vi then
      vdp_lbc f
   else
      ofsf_getcoeff(vdp_mred f,vi);

procedure ofsf_charpoly(q,x);
   % Characteristic polynomial. [q] is a matrix, a list of lists of Lisp Prefix
   % form with 'mat-Tag, [x] is an identifier. Returns a SQ.
   simp aeval {'char_poly,q,x};

procedure ofsf_coefflist(p,x);
   % Extract coefficients of polynomial. [p] is a SQ, [x] is an identifier.
   % Returns a list of SQ's.
   % Result has form: $ (c_0 c_1 ... c_d) $.
   begin scalar res,q1,q2,d;
      q1 := reorder numr p;
      q2 := denr p;
      d := ldeg q1;
      res := ofsf_coefflist1(q1,x,d);
      res := for each elem in res collect
	 quotsq(simp prepf elem,simp prepf q2);
      return res
   end;

procedure ofsf_coefflist1(p,x,d);
   % Subroutine of ofsf_coefflist. [p] is a SF, [x] is an identifier, [d] is
   % an integer. Returns a list of SQ's.
   if (domainp p or mvar p neq x) and d=0 then
      {p}
   else if not domainp p and ldeg p = d then
      (lc p) . ofsf_coefflist1(red p,x,d-1)
   else
      nil . ofsf_coefflist1(p,x,d-1);

procedure ofsf_buildq(vdpgb,he,vsbasis,beta);
   % Computation of matrix Q. [vsbasis] is a list of SF's or VDP's, [vdpgb]
   % is a list of VDP's, [he] is a SF, [beta] is a BETA.
   % Returns a list of lists of Lisp Prefix forms.
   if !*rlhqestrconst then
      ofsf_buildqsc(vdpgb,he,vsbasis,beta)
   else
      ofsf_buildq1(vdpgb,he,vsbasis);

procedure ofsf_buildq1(vdpgb,he,vsbasis);
   % Computation of matrix Q without using structure constants.
   % [vsbasis] is a list of SF's, [vdpgb] is a list of VDP's, [he] is a SF.
   % Returns a list of lists of Lisp Prefix forms.
   begin scalar redhe,q,dim;integer i;
      if !*rlhqevb then
	 ioto_tprin2 {"computing matrix Q of dimension ",length vsbasis};
      redhe := he;
      q := for each vlist on vsbasis collect
	 for each vj in vlist collect
	    prepsq ofsf_trace(vdpgb,redhe,car vlist,vj,vsbasis);
      q := for each line in q collect <<
	 i := i+1;
	 nconc(for j := 1:i-1 collect nil,line)
      >>;
      dim := length vsbasis;
      for y := 2:dim do
	 for x := 1:y-1 do
	    nth(nth(q,y),x) := nth(nth(q,x),y);
      if !*rlhqevb then
	 ioto_tprin2 " done";
      return q
   end;

procedure ofsf_buildhelist(greaterlist,neqlist);
   % Computation of all necessary products of polynomials in nequations and
   % >-relations. [greaterlist] and [neqlist] are lists of SF's. Returns a
   % list of SF's.
   begin scalar helist,chi1,glist;
      if null greaterlist and null neqlist then
	 return {1};
      chi1 := 1;
      for each elem in neqlist do
	 chi1 := multf(chi1,exptf(elem,2));
      glist := ofsf_buildglist greaterlist;
      helist := for each elem in glist collect multf(elem,chi1);
      return helist
   end;

procedure ofsf_buildglist(greaterlist);
   % Computation of all necessary products of polynomials in >-relations.
   % [greaterlist] is a list of SF's. Returns a list of SF's.
   begin scalar recres;
      if null greaterlist then
	 return {1}
      else if length greaterlist = 1 then
	 return {exptf(car greaterlist,2),car greaterlist};
      recres := ofsf_buildglist cdr greaterlist;
      return append(ofsf_buildglist1(exptf(car greaterlist,2),recres),
	 ofsf_buildglist1(car greaterlist,recres))
   end;

procedure ofsf_buildglist1(pol,pollist);
   % Subroutine of ofsf_buildglist. [pol] is a SF, [pollist] is a list of SF's.
   % Returns a list of SF's.
   for each poly in pollist collect multf(pol,poly);


% Following procedures are used for building matrix Q with use of structure
% constants:

procedure ofsf_redgroebner(vdpgb);
   % Reduction of a Groebner Basis. [vdpgb] is a list of VDP's. Returns a list
   % of VDP's. Throws all elements of [vdpgb] out, if their head term is
   % divided by another one.
   begin scalar h,f,f0,evf0;
      f := vdpgb;
      while not null f do <<
	 f0 := car f;
	 evf0 := vdp_evlmon f0;
	 f := cdr f;
	 if (not gb_searchinlist(evf0,f)) and
		(not gb_searchinlist(evf0,h)) then
	    h := f0 . h
      >>;
      return h
   end;

procedure ofsf_buildqsc(vdpgb,he,vsbasis,beta);
   % Computation of matrix Q using structure constants.
   % [vsbasis] is a list of SF's, [vdpgb] is a list of VDP's,
   % [he] is a SF, [beta] is a BETA. Returns a list of lists of Lisp
   % Prefix forms.
   begin scalar redhe,q,dim;integer i;
      if !*rlhqevb then
	 ioto_tprin2 {"computing matrix Q of dimension ",length vsbasis};
      redhe := gb_reduce(vdp_f2vdp he,vdpgb);
      q := for each vlist on vsbasis collect
	     for each vj in vlist collect
	    	mk!*sq ofsf_tracesc(redhe,car vlist,vj,vsbasis,beta);
      q := for each line in q collect <<
	 i := i+1;
	 nconc(for j := 1:i-1 collect nil,line)
      >>;
      dim := length vsbasis;
      for y := 2:dim do
	 for x := 1:y-1 do
	    nth(nth(q,y),x) := nth(nth(q,x),y);
      if !*rlhqevb then
	 ioto_tprin2 " done";
      return q
   end;

procedure ofsf_tracesc(redhe,vi,vj,vdpvsbasis,beta);
   % Trace Computation. [vdpvsbasis] is a list of VDP's, [redhe],
   % [vi] and [vj] are VDP's, [beta] is a BETA. Returns a SQ.
   begin scalar res;
      res := simp 0;
      for each bk in vdpvsbasis do
	 res := addsq(res,ofsf_tracesc1(redhe,vi,vj,bk,vdpvsbasis,beta));
      return res
   end;

procedure ofsf_tracesc1(redhe,vi,vj,bk,vdpvsbasis,beta);
   % Trace Computation subroutine. [vdpvsbasis] is a list of VDP's,
   % [redhe],[vi], [vj] and [bk] are VDP's, [beta] is a BETA. Returns a SQ.
   begin scalar traceelem,vivjbk;
      traceelem := simp 0;
      vivjbk := vdp_prod(vi,vdp_prod(vj,bk));
      for each bl in vdpvsbasis do
	 traceelem := addsq(traceelem,ofsf_tracesc2(redhe,bk,bl,vivjbk,beta));
      return traceelem
   end;

procedure ofsf_tracesc2(redhe,bk,bl,vivjbk,beta);
   % Trace Computation subroutine.[redhe],[bk], [bk] and [vivjbk]are VDP's,
   % [beta] is a BETA. Returns a SQ.
   multsq(gbsc_getlincombc(bl,redhe),
      gbsc_betaget(beta,vdp_prod(bl,vivjbk),bk));


% -----------------------------------------------------------------------------
% Computation of the dimension and a maximally independent set of variables
% of the ideal.
% -----------------------------------------------------------------------------

% Input: A lust of SF's, representing a Groebner Basis wrt. the $ revgradlex $
% term order, and a list of main variables.
% Output: Dimension $ d $ and a set of maximally independent variables.

procedure ofsf_dim(gb,varl);
   % Dimension and maximal set of independent variables of an ideal.
   % [gb] is a list of SF's, [varl] is a list of identifiers. Returns
   % a list of the form $ (d U) $, where $ d $ is the dimension of the
   % ideal and $ U $ is a set of maximally independent variables.
   if null gb then
      {length varl,varl}
   else if ofsf_proper(gb,varl) then
      ofsf_dimmain(gb,varl)
   else
      {-1,nil};

procedure ofsf_proper(gb,varl);
   % Tests, if the ideal constructed by [gb] is proper, e.g. not the
   % whole ring. [gb] is a list of SF's, [varl] is a list of identifiers.
   % Returns T or NIL.
   if null gb then
      t
   else if intersection(kernels car gb,varl) then
      ofsf_proper(cdr gb,varl)
   else
      nil;

procedure ofsf_dimmain(gb,varl);
   % Dimension and maximal set of independent variables of an ideal.
   % [gb] is a list of SF's, [varl] is a list of identifiers.
   % Returns a list of the form $ (d U) $, where $ d $ is the dimension
   % of the ideal and $ U $ is a set of maximally independent variables.
   begin scalar htl, m;
      htl := ofsf_htl(gb,varl);
      m := ofsf_dimrec(htl,varl,1,nil,nil);
      if !*rlhqegbdimmin then
	 return ofsf_getmin m
      else
	 return ofsf_getmax m
   end;

procedure ofsf_htl(gb,varl);
   % Head Terms of a Groebner Basis. [gb] is a list of SF's, [varl] is a
   % list of identifiers. Returns a list of SF's.
   begin scalar old, vdpgb, res;
      old := vdp_init(varl,'revgradlex,nil);
      vdpgb := for each elem in gb collect vdp_f2vdp elem;
      res := for each elem in vdpgb collect vdp_fmon(bc_a2bc 1,
	 vdp_evlmon elem);
      res := for each elem in res collect vdp_2f elem;
      vdp_cleanup old;
      return res
   end;

procedure ofsf_dimrec(s,varl,k,u,m);
   % Subroutine of Algorithm DIMENSION. [s] is a list of SF's, [varl] is
   % a list of identifiers, [k] is a
   % positive integer, [u] is a list of identifiers, [m] is a list of
   % lists of identifiers. Returns a list of lists of identifiers.
   begin scalar m2;
      m2 := m;
      for i:=k:length varl do
	 if not ofsf_intersectionp(
	    list2set(ofsf_getxi(varl,i) . u),s) then <<
	       m2 := ofsf_dimrec(s,varl,i+1,
		  list2set(ofsf_getxi(varl,i) . u),m2)>>;
      if not ofsf_subsetp(u,m2) then
	 m2 := u . m2;
      return m2
   end;

procedure ofsf_getxi(varl,i);
   % Get i-th variable X_{i}. [varl] is a list of identifiers, [i] is a
   % positive integer. Returns an identifier.
   nth(varl,i);

procedure ofsf_getmax(m);
   % Get maximal length of an element from a list of lists. [m] is a list
   % of lists of identifiers. Returns a list of the form $ (d u) $, where
   % $ d $ is an integer, the dimension of the ideal, and $ u $ is a
   % maximally independent set of variables of maximal cardinality.
   begin scalar u; integer d,lengthU;
      while not null m do <<
	 lengthU := length car m;
	 if lengthU > d then <<
	    d := lengthU;
	    u := car m
	 >>;
	 m := cdr m
      >>;
      return {d, u}
   end;

procedure ofsf_getmin(m);
   % Get minimal length of an element from a list of lists. [m] is a list
   % of lists of identifiers. Returns a list of the form $ (d u) $, where
   % $ d $ is an integer, the dimension of the ideal, and $ u $ is a
   % maximally independent set of variables of minimal cardinality.
   begin scalar u; integer d,du,lengthU;
      if not null m then <<
	 d := length car m;
	 du := d;
	 u := car m;
	 m := cdr m
      >>;
      while not null m do <<
	 lengthU := length car m;
	 if lengthU > d then
	    d := lengthU
	 else if lengthU < du then <<
	    du := lengthU;
	    u := car m
	 >>;
	 m := cdr m
      >>;
      return {d, u}
   end;

procedure ofsf_intersectionp(vl,s);
   % Test, if intersection of terms wrt. variables in [vl] and s isn't
   % empty. [vl] is a list of identifiers, [s] is a list of SF's. Returns
   % T or NIL.
   begin scalar news,found;
      news := s;
      while not null news do <<
	 if subsetp(kernels car news,vl) then <<
	    news := nil;
	    found := t
	 >>
	 else
	    news := cdr news
      >>;
      return found
   end;

procedure ofsf_subsetp(u,m2);
   % Tests, if [u] is contained in any element of [m2]. [u] is a list
   % of identifiers, [m2] is a list of lists of identifiers. Returns T
   % or NIL.
   begin scalar newm2,found;
      newm2 := m2;
      while not null newm2 do <<
	 if subsetp(u,car newm2) then <<
	    newm2 := nil;
	    found := t
	 >>
	 else
	    newm2 := cdr newm2
      >>;
      return found
   end;

endmodule;  % [ofsfhqe]

end; % of file
