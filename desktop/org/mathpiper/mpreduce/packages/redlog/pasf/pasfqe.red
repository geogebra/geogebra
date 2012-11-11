% ----------------------------------------------------------------------
% $Id: pasfqe.red 1815 2012-11-02 13:20:27Z thomas-sturm $
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
   fluid '(pasf_qe_rcsid!* pasf_qe_copyright!*);
   pasf_qe_rcsid!* :=
      "$Id: pasfqe.red 1815 2012-11-02 13:20:27Z thomas-sturm $";
   pasf_qe_copyright!* :=
      "(c) 2002-2009 A. Dolzmann, A. Seidl, T. Sturm, 2010 T. Sturm"
>>;

module pasfqe;
% Presburger arithmetic standard form quantifier elimination. Submodule of
% PASF.

% ---- Quantifier elimination control ----------------------------------------

procedure pasf_qe(phi,theo);
   % Presburger arithmetic standard form quantifier elimination. [phi]
   % is a formula; [theo] is an explicit theory. Returns a strictly
   % quantifier-free formula equivalent to [phi].
   if null pasf_uprap phi then
      pasf_expand pasf_gqe(phi,theo,nil,simp 1)
   else
      rederr{"Only weak quantifier elimination possible"};

procedure pasf_wqe(phi,theo);
   % Presburger arithmetic standard form weak quantifier elimination.
   % [phi] is a formula; [theo] is an explicit theory. Returns a weakly
   % quantifier-free formula equivalent to [phi].
   pasf_gqe(phi,theo,nil,simp 1);

procedure pasf_qea(phi,theo);
   % Presburger arithmetic standard form quantifier elimination with
   % answers. [phi] is a formula; [theo] is an explicit theory. Returns
   % an answer to the quantifier elimination.
   if null pasf_uprap phi then
      pasf_expanda(pasf_wqea(phi,theo),phi)
   else
      rederr{"Only weak quantifier elimination possible"};

procedure pasf_wqea(phi,theo);
   % Presburger arithmetic standard form weak quantifier elimination
   % with answers. [phi] is a formula; [theo] is an explicit theory.
   % Returns an answer to the quantifier elimination.
   begin scalar res,ret;
      res := pasf_gqe(phi,theo,t,simp 1);
      for each r in res do
	 ret := {answ_f r,for each b in answ_bl r collect b,
      	    for each eqn in answ_tl r collect
	       pasf_mk2('equal,prepf pasf_arg2l eqn,prepsq pasf_arg2r eqn)} . ret;
      return ret
   end;

procedure pasf_pqe(phi,p,theo);
   % Presburger arithmetic standard form probabilistic weak quantifier
   % elimination. [phi] is a formula; [theo] is an explicit theory; [p]
   % is a probability for PQE. Returns a $p$-equivalent quantifier-free
   % formula.
   if null pasf_uprap phi then
      pasf_gqe(phi,theo,nil,p)
   else
      rederr{"Probabilistic quantifier elimination impossible"};

procedure pasf_pqea(phi,p,theo);
   % Presburger arithmetic standard form probabilistic weak quantifier
   % eliminationwith answers. [phi] is a formula; [theo] is an explicit
   % theory; [p] is a probability for PQE. Returns a $p$-equivalent
   % quantifier-free formula.
   if null pasf_uprap phi then
      pasf_expanda(pasf_pqea1(phi,theo,p),phi)
   else
      rederr{"Probabilistic quantifier elimination impossible"};

procedure pasf_pqea1(phi,theo,p);
   % Presburger arithmetic standard form probabilistic quantifier
   % elimination with answers subprocedure. [phi] is a formula; [theo]
   % is an explicit theory; [p] is a probability for PQE. Returns an
   % answer to the probabilistic quantifier elimination.
   begin scalar res,ret;
      res := pasf_gqe(phi,theo,t,p);
      for each r in res do
	 ret := {answ_f r,for each b in answ_bl r collect b,
      	    for each eqn in answ_tl r collect
	       pasf_mk2('equal,prepf pasf_arg2l eqn,prepsq pasf_arg2r eqn)} . ret;
      return ret
   end;

procedure pasf_gqe(phi,theo,answ,p);
   % Presburger arithmetic standard form generic compute a
   % quantifier-free formula equivalent. [phi] is a formula; [theo] is
   % the explicit theory; [answ] should be set to nil iff no answers are
   % required; [p] is the probability for PQE. Returns a quantifier-free
   % formula $\psi$ equivalent to $\phi$ if [answ] is nil and a pair
   % $(\psi . a)$ where $a$ is an answer for the last quantifier block
   % otherwise.
   begin scalar rslt,pt,retn,tmp,bl,tl;
      if !*rlverbose then ioto_tprin2 "++++ Entering pasf_qe";
      % Tests for correct UPrA form.
      % pasf_uprap(phi);
      % The formula is always simplified via input theory
      phi := cl_simpl(phi,theo,-1);
      % Performing DNF on the matrix if wanted
      rslt := if !*rlpasfdnffirst then
	 % Note: a pseudo DNF computation is performed. In the second case the
	 % pseudo-DNF is also in PNF, so a PNF is computed in any case.
    	 pasf_dnf phi
      else
      	 pasf_pnf phi;
      % Determining the problem type for answers
      if rl_op rslt eq 'ex or phi then pt := 'existential
      else if rl_op rslt eq 'all or phi then pt := 'universal
      % For now user has to specify the formula with a non-bounded quantifier
      % in front to get answers
      else if answ then rederr{"QE with answers impossible"};
      rslt := pasf_inplaceqe(rslt,theo,answ,p);
      % Tuning rslt to fit QE with answers result type
      if answ and rl_tvalp rslt then
	 rslt := {answ_new(rslt,nil,nil)};
      % The last step is always simplified via input theory
      if answ then <<
	 for each an in rslt do <<
	    tmp :=  cl_simpl(answ_f an,theo,-1);
	    bl := answ_bl an;
	    tl := answ_tl an;
	    % Results with false guard for existential and with true guard for
	    % universal problems will be ignored
	    if pt eq 'existential and tmp neq 'false or
	       pt eq 'universal and tmp neq 'true then
	    	  retn := lto_insert(answ_new(tmp,bl,tl),retn)
	 >>;
	 if null retn then retn := {answ_new('false,nil,nil)};
 	 return retn
      >> else
	 return cl_simpl(rslt,theo,-1)
   end;

procedure pasf_inplaceqe(phi,theo,answ,p);
   % Presburger arithmetic standard form inplace quantifier elimination.
   % [phi] is a formula; [theo] is a theory; [answ] is the answer flag,
   % [p] is the probability for PQE. Returns a quantifier-free
   % equivalent or an answer according to answ flag.
   begin scalar res;
      res := pasf_inplaceqe1(phi,theo,p);
      if cdr res then
      	 % The outermost block is eliminated explicitly
      	 return pasf_qeblock(cadr res,cddr res,car res,theo,
	    if answ then answ_new('true,nil,nil) else nil,p);
      return car res
   end;

procedure pasf_inplaceqe1(phi,theo,p);
   % Presburger arithmetic standard form inplace quantifier elimination
   % subprocedure. [phi] is a formula; [theo] is a theory; [p] is the
   % probability for PQE. Returns a quantifier-free equivalent.
   begin scalar tmp,f;
      % Note: We can ignore the answer because all the blocks qe called inside
      % this procedure with are not the outter-most
      if rl_bquap rl_op phi then <<
	 tmp := pasf_inplaceqe1(rl_mat phi,theo,p);
	 if cdr tmp then
	    % A normal quantifier block has ended by outter bounded quantifier
	    f := pasf_qeblock(cadr tmp,cddr tmp,car tmp,theo,nil,p)
	 else
	    f := car tmp;
	 return (rl_mkbq(rl_op phi,rl_var phi,rl_b phi,f) . nil)
      >>;
      if rl_quap rl_op phi then <<
	 tmp := pasf_inplaceqe1(rl_mat phi,theo,p);
	 return if cdr tmp then
 	    (if cadr tmp neq rl_op phi then
	       (pasf_qeblock(cadr tmp,cddr tmp,car tmp,theo,nil,p) .
	       	  (rl_op phi . {rl_var phi}))
	    else
	       (car tmp . (cadr tmp . (rl_var phi . cddr tmp))))
	 else
	    (car tmp . (rl_op phi . {rl_var phi}))
      >>;
      % Now, assuming that the formula is in PNF, the formula is strong
      % quantifier-free
      return (phi . nil)
   end;

procedure pasf_qeblock(theta,varl,psi,theo,answ,p);
   % Presburger arithmetic standrd form eliminate a block of
   % quantifiers. [theta] if the quantifier type; [varl] is a list of
   % bounded variables by the quantifier; [psi] is the matrix of the
   % formula; [theo] is the current theory; [answ] should be set to nil
   % if no answers are required; [p] is the probability for PQE. Returns
   % an equivalent quantifier-free formula or a pair $(\psi . a)$ where
   % $a$ is an answer.
   begin scalar res;integer dpth,vlv;
      if !*rlverbose then
	 ioto_tprin2 {"---- ",theta . reverse varl};
      if theta eq 'ex then
      	 res := pasf_qeexblock(varl,psi,dpth,vlv,theo,answ,p)
      else <<
	 % Handling of the all operator
      	 res := pasf_qeexblock(varl,cl_nnfnot psi,dpth,vlv,theo,answ,p);
      	 res := if answ then
	    for each an in res collect
	       answ_new(cl_nnfnot answ_f an,answ_bl an,answ_tl an)
      	 else
	    cl_nnfnot res
      >>;
      return res
   end;

procedure pasf_qeexblock(varl,psi,dpth,vlv,theo,answ,p);
   % Presburger arithmetic standrd form eliminate a block of existential
   % quantifiers. [varl] are the bounded variables; [psi] is the matrix
   % of the formula; [dpth] ist the recursion depth; [vlv] is a list of
   % variables; [theo] is a theory; [answ] is nil if no answers are
   % required; [p] is the probability for PQE. Returns an equivalent
   % quantifier-free formula or a pair $(\psi . a)$ where $a$ is an
   % answer.
   begin
      scalar co,cvl,w,coe,f,newj,v,ans,ww;
      integer c,vlv,dpth,delc,oldcol,count,comax,comaxn;
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
      cvl := varl;
      co := co_new();
      if rl_op psi eq 'or then
	 for each x in rl_argn psi do
	    co := co_save(co,{ce_mk(cvl,x,answ)})
      else
      	 co := co_save(co,{ce_mk(cvl,psi,answ)});
      while co_data co do <<
	 if !*rlverbose and not !*rlqevbold then
   	    if !*rlqedfs then <<
	       ww := car co_stat co;
	       if comax = 0 or car ww < comax or
 		  (car ww = comax and cdr ww < comaxn)
	       then <<
		  comax := car ww;
		  comaxn := cdr ww;
		  ioto_prin2 {"[",comax,":",comaxn,"] "}
	       >>
	    >>;
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
	 % Variable selection
	 v := pop cvl;
	 % Eliminating the selected variable
	 ans := pasf_qeex(ce_f coe,v,theo,ce_ans coe,cvl,p);
	 if cvl then <<
	    if !*rlverbose then oldcol := co_length(co);
	    co := co_save(co,ans);
	    if !*rlverbose then
	       delc := delc + oldcol + length ans - co_length co
	 >> else <<
	    if answ then
	       for each an in ans do
		  newj := lto_insert(ce_ans an,newj)
	    else
	       for each an in ans do newj := lto_insert(ce_f an,newj)
	 >>;
	 if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then <<
	    ioto_prin2 "] ";
	    if !*rlqedfs and null cvl then ioto_prin2 ". "
      	 >>
      >>;
      if !*rlverbose then ioto_prin2{"[DEL:",delc,"/",count,"]"};
      return if answ then newj else rl_smkn('or,newj)
   end;

procedure pasf_qeex(psi,x,theo,answ,cvlm,p);
   % Presburger arithmetic standard form eliminate an existential
   % quantifier in front of a quantifier free formula. [psi] is a
   % formula; [x] is the quantified variable; [theo] is the current
   % theory; [answ] is an ANSW structure; [cvlm] is the variable list;
   % [p] is the probability for PQE. Returns a pair $(a . p) . theo'$
   % where $a=t$ and $p$ is a list of container elements and $theo'$ a
   % theory.
   begin scalar eset,dec,f,res,pcc,tmp;
      % PNF must be applied because of guards added during the substitution
      psi := pasf_pnf psi;
      if not (x memq cl_fvarl1 psi) then <<
      	 % The formula does not contain the quantified variable
	 if !*rlverbose  and (not !*rlqedfs or !*rlqevbold) then
 	    ioto_prin2 "*";
	 return {ce_mk(cvlm,psi,answ_new(psi,nil,
	    if answ then
	       pasf_mk2('equal,numr simp x,simp 0) . answ_tl answ else nil))}
      >>;
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then
 	 ioto_prin2 "e";
      % Computing a gauss decomposition of the input formula
      dec := if !*rlpasfgauss then
	 pasf_gaussdec(psi,x,theo)
      else
	 (nil . psi);
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold) and car dec then
 	 ioto_prin2 "g";
      % f is the formula resulting from psi by replacing all gauss subformulas
      % by false
      f := cl_simpl(cdr dec,theo,-1);
      if not (x memq cl_fvarl1 f) then <<
 	 % The non-gauss part of the formula does not contain the quantified
 	 % variable or is even possibly trivial
	 if !*rlverbose and (not !*rlqedfs or !*rlqevbold) then
 	    ioto_prin2 "#"
      >> else
      	 % Computing the elimination set of the input without gauss-formulas
      	 eset := pasf_elimset(f,x,theo,p);
      % Computing the quantifier-free equivalent. Each item of the following
      % list contains an answer to the corresponding substitution point.
      pcc := 0;
      res := append(
	 % Substitution of points from non gauss formulas
	 if null eset and f neq 'false then {answ_new(f,nil,
	    if answ then
	       pasf_mk2('equal,numr simp x,simp 0) . answ_tl answ else nil)} else
	    for each elimpt in eset collect
	       pasf_vs(if !*rlpasfsc then <<
		  tmp := pasf_condense(f,elimpt_pos elimpt);
		  pcc := pcc + cdr tmp;
		  car tmp
	       >> else f,x,elimpt),
	 % Substitution of points from gauss formulas
	 for each elimpt in car dec collect
	    pasf_vs(if !*rlpasfgc then <<
	       tmp := pasf_condense(psi,elimpt_pos elimpt);
	       pcc := pcc + cdr tmp;
	       car tmp
	    >> else psi,x,elimpt));
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold) and pcc > 0 then <<
	 ioto_prin2 "c";
	 ioto_prin2 pcc
      >>;
      % Simplifying the results
      res := for each rs in res collect
	 answ_new(if !*rlpasfsimplify then
	    cl_simpl(answ_f rs,theo,-1) else answ_f rs,answ_bl rs,answ_tl rs);
      % Answers represent directly the output disjunction
      return for each an in res collect
	 ce_mk(cvlm,answ_f an,answ_backsubst(an,answ))
   end;

% ---- Virtual substitution --------------------------------------------------

procedure pasf_vs(f,x,elimpt);
   % Presburger arithmetic standard form virtual substitution. [f] is a
   % positive quantifier-free formula; [x] is a variable; [elimptl] is
   % an ELIMPT. Returns a list of ANSW structures.
   begin scalar res,tf,bvl,sf;
      % Creating the formula to substitute
      sf := cl_apply2ats1(f,'pasf_vsubstatf,
	 {x,elimpt_den elimpt,elimpt_nom elimpt,elimpt_unif elimpt});
      tf := rl_smkn('and, {sf,elimpt_guard elimpt});
      % Checking if the substitution is trivial eg. gauss substitution.
      if elimpt_bvl elimpt then <<
	 % There are bounded quantifiers to create
	 bvl := elimpt_bvl elimpt;
	 for each bv in bvl do
	    tf := rl_mkbq('bex,cdr bv,car bv,tf)
      >>;
      res := answ_new(tf,for each bv in bvl collect car bv,
     	 {pasf_mk2('equal,numr simp x,
	    (elimpt_nom elimpt . elimpt_den elimpt))});
      % mathprint rl_mk!*fof answ_f res;
      return res
   end;

procedure pasf_vsubstatf(atf,x,n_j,a_j,unif);
   % Presburger arithmetic standard form virtual stubstitution in atomic
   % formula. [atf] is an atomic formula; [x] is the eliminated
   % variable; [n_j] is a substitution parameter; [a_j] is a
   % substitution parameter; [unif] is a flag that is t iff the formula
   % represents cauchy bounds. Returns the substituted atomic formula.
   begin scalar n_i,a_i,dc,d,degr;
      % Decomposing the atomic formula
      dc := repr_atfnew(atf,x,nil);
      % Highest degree of the polynomial
      degr := repr_ldeg dc;
      % Constrained substitution if univariate formula
      if degr > 1 and not unif then
	 return pasf_vsubstcatf(atf,x,n_j,a_j);
      if degr <= 1 then <<
      	 n_i := repr_n dc;
      	 a_i := repr_a dc;
      	 % Returning unchanged formula if no quantified variable in the formula
      	 if null n_i then return atf;
      	 d := pasf_pdp n_j;
      	 return if pasf_congp atf then
	    % Multiplying the modulus
      	    pasf_0mk2(pasf_mkop(pasf_opn atf,multf(pasf_m atf,n_j)),
	       addf(multf(n_i,a_j),negf multf(n_j,a_i)))
      	 else if pasf_op atf memq '(leq lessp geq greaterp) then
	    (if d memq '(pdef psdef) then
	       pasf_0mk2(repr_op dc,addf(multf(n_i,a_j), negf multf(n_j,a_i)))
      	    else if d memq '(ndef nsdef) then
	       pasf_0mk2(anegrel repr_op dc,addf(multf(n_i,a_j),
	       	  negf multf(negf n_j,a_i)))
	    else if d eq 'indef then
	       % For inequalities with indefinite denominator the denominator
	       % must be made positive
      	       pasf_0mk2(repr_op dc,addf(multf(multf(n_i,n_j),a_j),
	       	  negf multf(multf(n_j,n_j),a_i))))
      	 else
	    pasf_0mk2(repr_op dc,addf(multf(n_i,a_j), negf multf(n_j,a_i)))
      >> else <<
	 % Trivial substitution
	 return pasf_subat({(x . prepf a_j)},atf);
      >>;
   end;

procedure pasf_vsubstcatf(atf,x,n_j,a_j);
   % Presburger arithmetic standard form constrained virtual
   % stubstitution in a univariate nonlinear atomic formula. [atf] is an
   % atomic formula; [x] is the eliminated variable; [n_j] is the test
   % point nominator; [a_j] is the test point denominator. Returns a
   % formula.
   begin scalar cl,cb,cbadd,lcoeff;
      %if pasf_congp atf then
      %rederr{"For now no congruences with univariate polynomials allowed"};
      cl := pasf_coeflst(pasf_arg2l atf,x);
      cb := pasf_cauchybndcl(cl);
      cbadd := multf(multf(n_j,n_j),cb);
      lcoeff := car cl;
      if domainp car lcoeff and remainder(cdr lcoeff,2) = 0 then
	 return rl_smkn('or,
	    {rl_smkn('and,{pasf_0mk2('leq,addf(a_j,cbadd)),
	       pasf_0mk2(pasf_op atf,car lcoeff)}),
	       rl_smkn('and,{pasf_0mk2('geq,addf(a_j,negf cbadd)),
		  pasf_0mk2(pasf_op atf,car lcoeff)})});
      if !*rlqesubi then
	 return rl_smkn('or,
	    {rl_smkn('and,{pasf_0mk2('leq,addf(a_j,cbadd)),
	    pasf_qesubiat(atf,x,'minf)}),
	       rl_smkn('and,{pasf_0mk2('geq,addf(a_j,negf cbadd)),
	    pasf_qesubiat(atf,x,'pinf)})})
      else
	 return rl_smkn('or,
	    {rl_smkn('and,{pasf_0mk2('leq,addf(a_j,cbadd)),
	    pasf_subat({(x . prepf negf cb)},atf)}),
	       rl_smkn('and,{pasf_0mk2('geq,addf(a_j,negf cbadd)),
	    pasf_subat({(x . prepf cb)},atf)})})
   end;

procedure pasf_qesubi(f,v,inf);
   % Presburger arithmetic standard form quantifier elimination
   % substitute infinite element. [bvl] is a list of variables, [theo]
   % is the current theory; [f] is a quantifier-free formula; [v] is a
   % variable; [inf] is one of ['minf], ['pinf] which stand for
   % $-\infty$ and $\infty$ resp. Returns a pair $(\Theta' . \phi)$
   % where $\Theta'$ is a theory and $\phi$ is a quantifier-free
   % formula. $\phi$ is equivalent to $[f]([v]/[inf])$ under the theory
   % $[th] \cup \Theta'$. $\Theta' is currently always [nil].
   cl_apply2ats1(f,'pasf_qesubiat,{v,inf});

procedure pasf_qesubiat(atf,v,inf);
   % Presburger arithmetic standard form quantifier elimination
   % substitute infinite element into atomic formula. [atf] is an atomic
   % formula; [v] is a variable; [inf] is one of ['minf], ['pinf] which
   % stand for $-\infty$ and $\infty$ resp. Returns a quantifier-free
   % formula equivalent to $[atf]([v]/[inf])$.
   begin scalar op,lhs;
      if not (v memq pasf_varlat atf) then return atf;
      op := pasf_op atf;
      lhs := pasf_arg2l atf;
      if op eq 'equal or op eq 'neq then
	 return pasf_qesubtranseq(op,lhs,v);
      % [op] is an ordering relation.
      return pasf_qesubiord(op,lhs,v,inf)
   end;

procedure pasf_qesubtranseq(op,lhs,v);
   % Presburger arithmetic standard form quantifier elimination
   % substitute transcendental element with equality relation. [op] is
   % one of ['equal], ['neq]; [lhs] is an SF; [v] is a variable. Returns
   % a quantifier-free formula equivalent to $[r]([lhs],0)([v]/\alpha)$
   % for any transcendental $\alpha$.
   if op eq 'equal then
      pasf_qesubtransequal(lhs,v)
   else  % [op eq 'neq]
      cl_nnfnot pasf_qesubtransequal(lhs,v);

procedure pasf_qesubtransequal(lhs,v);
   % Presburger arithmetic standard form quantifier elimination
   % substitute transcendental element into equation. [lhs] is an SF;
   % [v] is a variable. Returns a quantifier-free formula equivalent to
   % $[lhs]([v]/\alpha)=0$ for any transcendental $\alpha$.
   pasf_qesubtransequal1(sfto_reorder(lhs,v),v);

procedure pasf_qesubtransequal1(lhs,v);
   % Presburger arithmetic standard form quantifier elimination
   % substitute transcendental element into equation. [lhs] is an SF
   % reordered wrt. [v]; [v] is a variable. Returns a quantifier-free
   % formula equivalent to $[lhs]([v]/\alpha)=0$ for any transcendental
   % $\alpha$.
   begin scalar cl;
      while not domainp lhs and mvar lhs eq v do <<
	 cl := pasf_0mk2('equal,reorder lc lhs) . cl;
	 lhs := red lhs
      >>;
      cl := pasf_0mk2('equal,reorder lhs) . cl;
      return rl_smkn('and,cl)
   end;

procedure pasf_qesubiord(op,f,v,inf);
   % Presburger arithmetic standard form quantifier elimination
   % substitute infinite element with ordering relation. [op] is an
   % ordering relation. [f] is an SF; [v] is a variable; [inf] is one of
   % ['minf], ['pinf] which stand for $-\infty$ and $\infty$ resp.
   % Returns a quantifier-free formula equivalent to
   % $[op]([lhs]([v]/[inf]),0)$.
   pasf_qesubiord1(op,sfto_reorder(f,v),v,inf);

procedure pasf_qesubiord1(op,f,v,inf);
   % Presburger arithmetic standard form quantifier elimination
   % substitute infinite element with ordering relation subroutine. [op]
   % is an ordering relation. [f] is an SF, which is reordered wrt. [v];
   % [v] is a variable; [inf] is one of ['minf], ['pinf] which stand for
   % $-\infty$ and $\infty$ resp. Returns a quantifier-free formula
   % equivalent to $[op]([lhs]([v]/[inf]),0)$.
   begin scalar an;
      if domainp f or mvar f neq v then
      	 return pasf_0mk2(op,reorder f);
      an := if inf eq 'minf and not evenp ldeg f then
 	 negf reorder lc f
      else
 	 reorder lc f;
      % The use of [an] is correct in the equal case.   % Generic QE!
      return rl_mkn('or,{pasf_0mk2(pasf_mkstrict op,an),rl_mkn(
	 'and,{pasf_0mk2('equal,an),pasf_qesubiord1(op,red f,v,inf)})})
   end;

% ---- Condensing operator --------------------------------------------------

procedure pasf_condense(f,pl);
   % Presburger arithmetic standard form condensing operator. [f] is a
   % positive quantifier-free formula; [pl] is a list of tree positions
   % of formulas to condense. Returns a pair $(f' . c)$ where $f'$
   % results from [f] by replacing each subformula, that is not
   % conjunctively associated to [pl], with false and $c$ is the total
   % amount of condensed subtrees.
   begin scalar r,c,tmp,cm;
      % We have found the formula producing the resulting test point
      if null pl then return (f . 0);
      % In disjunctions we remove all points that are not conjunctively
      % associated to the formula producing the test point
      if rl_op f eq 'or then <<
	 c := 0;
	 for each sf in rl_argn f do <<
	    if c = car pl then
	       r := pasf_condense(sf,cdr pl);
	    c := c + 1
	 >>;
	 if c = 0 then
	    rederr{"Bug in pasf_condense, reference leads to nothing"};
	 return (car r . (cdr r + c - 1))
      >>;
      % In conjunctions we proceed with condensing on the way to the
      % formula, that produced the testpoint, without replacing anything
      % on the current level
      if rl_op f eq 'and then <<
	 c := 0;
	 cm := 0;
	 for each sf in rl_argn f do <<
	    if c = car pl then <<
	       tmp := pasf_condense(sf,cdr pl);
	       r := (car tmp) . r;
	       cm := cdr tmp
	    >> else
	       r := sf . r;
	    c := c + 1
	 >>;
	 return (rl_smkn('and,r) . cm)
      >>;
      % Note: Universal bounded quantifiers stay as they are even if one
      % tries to condense something inside such a quantifier. It could
      % make sence to raise an error, if one tries to do so, but this
      % implementation avoids condensing of universal bounded
      % quantifiers with other tools
      if rl_op f eq 'bex then <<
	 tmp := pasf_condense(rl_mat f,cdr pl);
	 return (rl_mkbq(rl_op f,rl_var f,rl_b f,car tmp) . cdr tmp)
      >>;
      return (f . 0)
   end;

% ---- Elimination set computation -------------------------------------------

procedure pasf_elimset(f,x,theo,p);
   % Presburger arithmetic standard form elimination set computation.
   % [f] is a forumla; [x] is a variable; [theo] is a theory; [p] is the
   % probability for PQE. Returns an ELIMPT list.
   begin scalar reprl,reprls,m,tempm,pdp,rl,res,vl,tz,toc;
      % Probabilistic mode is on
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold) and p neq simp 1 then
 	 ioto_prin2 "p";
      reprls := pasf_rep(f,x);
      % Create all new variables. This prevents running out of variables:
      vl := for i := 1 : length fdec_bvl car reprls + 1 collect
	 pasf_newvar(nil);
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold)
 	 and length cdr reprls > 1
      then <<
	 ioto_prin2 "s";
	 ioto_prin2 length cdr reprls
      >>;
      for each reprl in cdr reprls do <<
      	 % Compute the approximation for the moduli period:
      	 m := 1;
	 rl := nil;
	 toc := t;
      	 for each repr in reprl do
	    % Only representants containing the quantified variable
	    % concerned:
      	    if repr_n repr then <<
               if pairp repr_op repr and
 	       	  car repr_op repr memq '(cong ncong) then <<
		     % Getting the modulus
		     tempm := cdr repr_op repr;
		     pdp := pasf_pdp tempm;
		     m := if pdp eq 'pdef then
		     	% For definite moduli no approximation needed
			lcm(m,tempm)
		     else if pdp eq 'ndef then
			lcm(m,negf tempm)
		     else if pdp eq 'psdef then
			% For semidefinite moduli just adding 1
 			lcm(m,addf(tempm,1))
		     else if pdp eq 'nsdef then
			% Approximate the modulus by it's square plus 1
 			lcm(m,addf(negf tempm,1))
		     else
 			lcm(m,addf(multf(tempm,tempm),1));
		     % Add the congruence to the representant list if it
		     % can become zero:
		     if not (pdp memq '(pdef ndef)) then <<
			toc := t;
			rl := repr . rl
		     >>
	     	  >> else
		     rl := repr . rl
      	    >>;
	 res := append(pasf_testpt(fdec_bvl car reprls,rl,m,vl,toc,p),res)
      >>;
      tz := length res;
      res := if !*rlpasfconf then pasf_conflate res else res;
      if !*rlverbose and (not !*rlqedfs or !*rlqevbold) and
 	 !*rlpasfconf and tz-length res > 0
      then <<
	 ioto_prin2 "t";
	 ioto_prin2 (tz-length res)
      >>;
      if null res then rederr{"error in elimination set creation"};
      % Add the zero case only in case of uniform input and
      % non-univariate formula:
      return if pasf_uprap f and not pasf_univnlfp(f,x) then
	 elimpt_new(nil,'true,nil,1,nil,nil) . res
      else res
   end;

procedure pasf_testpt(b,l,m,vl,toc,p);
   % Presburger arithmetic standard form elimination test points. [b] is
   % a list of bound/bound variable pairs; [l] is the list of
   % representants that will be used for test point generation; [m] is a
   % congruence period approximation, which can be not positive definite
   % only in case of generic elimination; [vl] is a list of new
   % varibles; [toc] is a flag that signals if the congruence case has
   % to be added; [p] is the probability for PQE. Returns an ELIMSET.
   begin scalar v,res,cp,nsv,rnd,rng,n;
      v := car vl;
      nsv := numr simp v;
      % The congruences case
      res := if null l or null toc then
	 if p neq simp 1 then
	    pasf_testptpqe(nil,0,1,0,m,p,nil)
         else
	    {elimpt_new(nil,'true,numr simp v,1,
	       {(rl_smkn('or,{pasf_mkrng(numr simp v,nil,m),
	    	  pasf_mkrng(nsv,nil,negf m)}) . v)},nil)};
      for each repr in l do <<
         % DEBUG Test for correct representants
	 if repr_ldeg repr = 0 then
	    rederr{"pasf_testpt: representant with leading degree 0"};
	 % Probabilistic test point
	 if p neq simp 1 and repr_ldeg repr = 1 then
	    res := pasf_testptpqe(repr_pos repr,repr_r repr,repr_n repr,
	       -m*repr_n repr,m*repr_n repr,p,t)
      	 else if repr_ldeg repr = 1 then
	    % Simple test point
	    res := elimpt_new(
	       % Position of the subformula
	       	  repr_pos repr,
       	    	  % Guards for each representant
	    	  rl_smkn('and,{pasf_0mk2('neq,repr_n repr),
	       	     pasf_0mk2(('cong . repr_n repr),
		  	addf(repr_r repr, nsv))}),
	    	  % Substitution point
	    	  addf(repr_r repr,nsv),repr_n repr,
	    	  % Bounds
	    	  pasf_substb(b,repr_t repr,v,m,repr_n repr,cdr vl),nil) . res
      	 else <<
	    % Univariate test point. Note: assuming m to be positive
	    cp := addf(pasf_cauchybndcl repr_cl repr,m);
	    res := elimpt_new(
	       % Position of the subformula
	       repr_pos repr,
	       % Guards for the substitution
	       'true,
	       % Substitution point
	       nsv,1,
	       % Bounds are the Cauchy-bounds
	       {(pasf_mkrng(nsv,negf cp,cp) . v)},t) . res;
	 >>
      >>;
      return res
   end;

procedure pasf_testptpqe(pos,nom,den,a,b,p,g);
   % Presburger arithmetic standard form elimination test points for
   % pqe. [pos] is the position of the formula; [nom] is the numerator
   % term; [den] is the denominator; [a] is the lower interval boundary;
   % [b] is the upper interval boundary; [p] is the probability for PQE;
   % [g] is nil iff there are no guards to create. Returns a
   % probabilistic elimination set.
   if !*rlpqeold then
	pasf_testptpqeold(pos,nom,den,a,b,p,g)
   else
	pasf_testptpqenew(pos,nom,den,a,b,p,g);

procedure pasf_testptpqenew(pos,nom,den,a,b,p,g);
   % Presburger arithmetic standard form elimination test points for
   % pqe. [pos] is the position of the formula; [nom] is the numerator
   % term; [den] is the denominator; [a] is the lower interval boundary;
   % [b] is the upper interval boundary; [p] is the probability for PQE;
   % [g] is nil iff there are no guards to create. Returns an ELIMSET
   % which comes from the substitution of a random test term.
   begin scalar n,r,res;
	 r := pasf_mkrndf(b,pasf_newvar('false));
	 res := {elimpt_new(pos,
	    if g then
	       rl_smkn('and,{pasf_0mk2('neq,den),
	    	  pasf_0mk2(('cong . den),addf(nom,r))})
	    else
	       'true,addf(nom,r),den,nil,nil),
	 elimpt_new(pos,
	    if g then
	       rl_smkn('and,{pasf_0mk2('neq,den),
	    	  pasf_0mk2(('cong . den),addf(nom,negf r))})
	    else
	       'true,addf(nom,negf r),den,nil,nil)};
      return res;
   end;

procedure pasf_testptpqeold(pos,nom,den,a,b,p,g);
   % Presburger arithmetic standard form elimination test points for
   % pqe. [pos] is the position of the formula; [nom] is the numerator
   % term; [den] is the denominator; [a] is the lower interval boundary;
   % [b] is the upper interval boundary; [p] is the probability for PQE;
   % [g] is nil iff there are no guards to create. Returns an ELIMSET
   % which contains random points from the range $[t+a,t+b]$ such that
   % each term is hit with probability [p].
   begin scalar n,rnd,res;
      n := max2(ceiling(ln(1.0-numr p*1.0/denr p)/ln(1.0-1.0/(b-a+1))-1),1);
      for i := 1 : n do <<
	 rnd := numr simp (random(b-a+1)+a);
	 res := elimpt_new(pos,
	    if g then
	       rl_smkn('and,{pasf_0mk2('neq,den),
	    	  pasf_0mk2(('cong . den),addf(nom,rnd))})
	    else
	       'true,addf(nom,rnd),den,nil,nil) . res
      >>;
      return res
   end;

procedure pasf_substb(b,term,v,m,n_j,vl);
   % Presburger arithmetic standard form bound substitution. [b] is a
   % list of bound/bound variable pairs; [term] is the term of linear
   % combinations of bounded variables in b; [m] is an approximation of
   % all moduli; [n_j] is the coefficient of the representant; [vl] is a
   % list of new variables. Returns a list of bounds where $v$ runs in
   % some range about all values of [term] in [b].
   begin scalar nb,nv,nt1,nt2,res,sb,nbb,tmp,pdp;
      % Collecting all variables for substitution
      for each bnd in b do <<
	 sb := (cdr bnd . car vl) . sb;
	 vl := cdr vl
      >>;
      % Duplicating the term
      term := numr subf(term,sb);
      % Duplicating the bounds
      for each bnd in b do <<
	 nbb := car bnd;
	 nv := nil;
	 for each s in sb do <<
	    if car s eq cdr bnd then nv := cdr s;
	    % Note: Bounds are strong quantifier-free
	    nbb := pasf_subfof(car s,cdr s,nbb)
	 >>;
	 if null nv then rederr {"bug in bound substitution"};
	 nb := (nbb . nv) . nb
      >>;
      if !*rlpasfbapprox then <<
	 % Bound approximation
	 tmp := pasf_bapprox(nb,term,v,m,n_j);
	 if tmp then return tmp
      >>;
      % Note: nt1 assumes m and n_j to be both positive
      nt1 := multf(n_j,m);
      % Note: nt2 assumes analog n_j to be negative
      nt2 := multf(negf n_j,m);
      % Bound substitution
      pdp := pasf_pdp n_j;
      res := rl_smkn('or,
      	 if pdp eq 'pdef then
	    {pasf_mkrng(addf(numr simp v,negf term),negf nt1,nt1)}
	 else if pdp eq 'ndef then
	    {pasf_mkrng(addf(numr simp v,negf term),negf nt2,nt2)}
	 else
	    {pasf_mkrng(addf(numr simp v,negf term),negf nt1,nt1),
      	       pasf_mkrng(addf(numr simp v,negf term),negf nt2,nt2)});
      return ((res . v) . reverse nb)
   end;

procedure pasf_bapprox(b,term,v,l,n_j);
   % Presburger arithmetic standard form bound approximation. [b] is a
   % list of bound/bound variable pairs; [term] is the term of linear
   % combinations of bounded variables in [b]; [l] is the lcm of all
   % nonzero coefficients; [n_j] is the coefficient of the representant.
   % Returns a new bound in [v] where [v] runs in some range about all
   % values of [term] in [b].
   begin scalar tmin,tmax,tmp,flag,tpool,tnpool,res,fvl;
      % For now only the real non uniform case
      if null domainp l then return nil;
      if null domainp n_j then return nil;
      if pasf_termp(term,nil) then return nil;
      tpool := {term};
      % Collecting all ranges of the bounds
      for each bnd in b do <<
	 fvl := cl_fvarl car bnd;
	 if length fvl > 1 then flag := t;
	 if length fvl = 1 and car fvl neq cdr bnd then
	    rederror{"bug in bound approximation"};
	 if null flag then <<
	    tmp := pasf_brng(car bnd,cdr bnd);
	    tnpool := nil;
	    for each tm in tpool do <<
	       tnpool := numr subf(tm,{(cdr bnd . car tmp)}) . tnpool;
	       tnpool := numr subf(tm,{(cdr bnd . cdr tmp)}) . tnpool
	    >>
	 >>;
	 tpool := tnpool
      >>;
      % If parametric bounds appear substitution fails
      if flag then return nil;
      % Looking for minimum and maximum in the term list
      tmax := 'minf;
      tmin := 'pinf;
      for each tm in tpool do <<
	 if pasf_leqp(tm,tmin) then tmin := tm;
	 if pasf_leqp(tmax,tm) then tmax := tm
      >>;
      if minusf n_j then n_j := negf n_j;
      if minusf l then l := negf l;
      res := pasf_mkrng(numr simp v,
	 addf(tmin,negf multf(n_j,l)),
	 addf(tmax,multf(n_j,l)));
      return {(res . v)}
   end;

procedure pasf_conflate(elsl);
   % Presburger arithmetic standard form conflation of elimination sets.
   % [elsl] is a list of test points. Returns a conflated elimination
   % set.
   begin scalar tmp,res;
      while elsl do <<
	 tmp := pasf_conflate1(cdr elsl,car elsl);
	 res := car tmp . res;
	 elsl := cdr tmp
      >>;
      return res
   end;

procedure pasf_conflate1(elsl,els1);
   % Presburger arithmetic standard form conflation of elimination sets
   % subprocedure. [elsl] is a list of test points; [els1] is a point to
   % conflate with. Returns a conflated elimination set.
   begin scalar r,rev1,rev2;
      for each els2 in elsl do <<
	 if (elimpt_nom els1 = elimpt_nom els2) and
	 (elimpt_den els1 = elimpt_den els2) and
	 (elimpt_guard els1 = elimpt_guard els2) and
	 (elimpt_unif els1 = elimpt_unif els2) then <<
	    rev1 := elimpt_bvl els1;
	    rev2 := elimpt_bvl els2;
	    els1 := elimpt_new(elimpt_cpos(els1,els2),
	       elimpt_guard els1,elimpt_nom els1,elimpt_den els1,
	       % Note: This part uses the special form of the
	       % elimination set of the QE-method (refer to Lasaruk's
	       % diploma thesis)
	       if rev1 and rev2 then
		  ((pasf_ssmk2('or,caar rev1,caar rev2) .
		     cdar rev1) . cdr rev1)
	       else if rev1 then rev1
	       else rev2,elimpt_unif els1)
	 >> else r := els2 . r
      >>;
      return (els1 . r)
   end;

procedure pasf_ssmk2(op,a1,a2);
    if a1 = a2 then
       a1
    else
      if rl_op a1 eq op and rl_op a2 eq op then
      rl_mkn(op,append(rl_argn a1,rl_argn a2))
   else if rl_op a1 eq op then
      rl_mkn(op,a2 . rl_argn a1)
   else if rl_op a2 eq op then
      rl_mkn(op,a1 . rl_argn a2)
   else
      rl_mkn(op,{a1,a2});

% ---- Representant computation ---------------------------------------------

procedure pasf_rep(f,x);
   % Presburger arithmetic standard form search for representants. [f]
   % is a weak quantifier-free formula in PNF; [x] is the eliminated
   % variable. Returns a pair of a FDEC structure and a list of REPR
   % structures.
   begin scalar fdec,ball;
      % Compute the matrix and the list of bounded variables:
      fdec := fdec_new(f,x);
      for each b in fdec_bopl fdec do if b eq 'ball then ball := t;
      % Perform structural elimination only in existential problems.
      % This specially avoids condensing of formulas with universal
      % bounded quantifiers:
      return if !*rlpasfses and null ball then
	 (fdec . pasf_ses(fdec_mat fdec,x,fdec_pos fdec,fdec_bvl fdec))
      else
      	 (fdec . {pasf_rep1(fdec_mat fdec,x,fdec_pos fdec,fdec_bvl fdec)})
   end;

procedure pasf_rep1(f,x,pos,bvl);
   % Presburger arithmetic standard form search for representants
   % subprocedure. [f] a strong quantifier-free formula; [x] is the
   % eliminatied variable; [pos] is the current position inside the
   % formula; [bvar] is the list of bounded variables. Returns the
   % elimindation data.
   begin scalar n,res;
      % Note: pos is reserved for future implementation of positional
      % condensing.
      n := 0;
      if rl_bquap rl_op f or rl_bquap rl_op f then
      	 % Input formula should be strong quantifier-free
      	 rederr{"pasf_canrep : quantifier illegal inside a formula's matrix"};
      if rl_boolp rl_op f then <<
      	 for each arg in rl_argn f do <<
	    % For now condensing only in structural elimination sets
	    res := append(pasf_rep1(arg,x,nil,bvl),res);
	    n := n+1
	 >>;
	 return res
      >>;
      % Atomic formula reached
      if pasf_congp f and x memq kernels pasf_m f then
	 rederr{"Quantified variable ",x," is not allowed in modulus"};
      return {repr_atfbnew(f,x,nil,bvl)}
   end;

procedure pasf_ses(f,x,pos,bvl);
   % Presburger arithmetic standard form search for representants with
   % structural elimination sets. [f] a strong quantifier-free formula;
   % [x] is the eliminatied variable; [pos] is the current position
   % inside the formula; [bvar] is the list of bounded variables.
   % Returns the elimindation data.
   begin scalar n,res,tmp,lmax,smax;
      n := 0;
      if rl_quap rl_op f or rl_bquap rl_op f then
      	 % Input formula should be strong quantifier-free
      	 rederr{"bug in pasf_canrep"};
      if rl_op f eq 'and then <<
	 lmax := 0;
      	 for each arg in rl_argn f do <<
	    tmp := pasf_ses(arg,x,append(pos,{n}),bvl);
	    if length tmp > lmax then <<
	       for each sm in smax do res := append(sm,res);
	       lmax := length tmp;
	       smax := tmp
	    >> else
	       for each sm in tmp do res := append(sm,res);
	    n := n+1
	 >>;
	 return for each esl in smax collect
	    append(esl,for each r in res collect
	       repr_setpos(r,repr_pos car esl))
      >>;
      if rl_op f eq 'or then <<
     	 for each arg in rl_argn f do <<
	    res := append(pasf_ses(arg,x,append(pos,{n}),bvl),res);
	    n := n+1
	 >>;
	 return res
      >>;
      % Atomic formula reached
      if pasf_congp f and x memq kernels pasf_m f then
	 rederr{"Quantified variable",x,"is not allowed in modulus"};
      return {{repr_atfbnew(f,x,pos,bvl)}}
   end;

% ---- Gauss decomposition ---------------------------------------------------

procedure pasf_gaussdec(f,x,theo);
   % Presburger arithmetic standard form gauss decomposition. [f] is a
   % positive weakly quantifier-free formula; [x] is a variable; [theo]
   % is a theory. Returns a pair $(l . \psi)$ where $l$ is a list of $(p
   % . es)$ where $p$ is the position of a gauss formula in $f$ and $es$
   % is it's elimination set and $\psi$ is the formula resulting from
   % $f$ by replacing every gauss formula by false.
   begin scalar r,fdec,f,opl,stp,vl;
      % Note : Using the fact the formula is in PNF
      fdec := fdec_new(f,x);
      % Gauss elimination does not work for now with univariate formulas
      if pasf_univnlfp(fdec_mat fdec,x) then return (nil . f);
      opl := fdec_bopl fdec;
      % Cancelling gauss elimination for universal bounded quantifiers
      for each op in opl do
	 if op eq 'ball then stp := t;
      if stp then return (nil . f);
      % Creating new variables
      vl := for i := 1 : length fdec_bvl fdec + 1 collect pasf_newvar(nil);
      r := pasf_gaussdec1(fdec_mat fdec,x,theo,fdec_pos fdec,fdec_bvl fdec,vl);
      f := caddr r;
      for each bv in fdec_bvl fdec do <<
	 f := rl_mkbq(car opl,cdr bv,car bv,f);
	 opl := cdr opl
      >>;
      return (cadr r . f)
   end;

procedure pasf_gaussdec1(f,x,theo,pos,bvar,vl);
   % Presburger arithmetic standard form gauss decomposition
   % subprocedure. [f] is a formula; [x] is a variable; [theo] is a
   % theory; [pos] is a position; [bvar] is a list of bounded variable
   % and bound pairs; [vl] is the new variable list. Returns list $\{flg
   % , l , \psi\}$ where $flg$ is t iff the formula is a gauss formula,
   % $l$ is a list of $(p . es)$ where $p$ is the position of a gauss
   % formula in [f] and $es$ is it's elimination set and $\psi$ is the
   % formula resulting from [f] by replacing every gauss formula by
   % 'false.
   begin scalar c,tmp,r;
      if f eq 'false then
	 return{t,nil,f};
      if f eq 'true then
	 return{nil,nil,f};
      if rl_op f eq 'and then <<
 	 % It is sufficient to find one gauss argument
	 c := 0;
	 % Internal datastructure {a,b,c}. First element is t iff a
	 % gauss-formula was found. The second is a list of ELIMPT of
	 % nested gauss formulas till now. The third is the formula
	 % without gauss formulas inside
	 tmp := {nil,nil,nil};
	 for each sf in rl_argn f do <<
	    % Among gauss subformulas we choose the elimination set with a
	    % corresponding heuristic
	    r := pasf_gaussdec1(sf,x,theo,append(pos,{c}),bvar,vl);
	    if car r then
	       % Found a new gauss subformula
	       tmp := {t,pasf_gaussesord(cadr tmp,cadr r),'false}
	    else if null car tmp then
 	       % There for now no gauss subformulas found and the current one
 	       % is a non-gauss-formula
	       tmp := {nil,append(cadr tmp,cadr r),caddr r . caddr tmp};
	    % Note: Non-gauss subformulas are ignored if one is already found
	    c := c + 1
	 >>;
	 if car tmp then
	    % This formula is a gauss formula
	    return tmp
	 else
	    return {nil,cadr tmp,rl_smkn('and,caddr tmp)}
      >>;
      if rl_op f eq 'or then <<
	 % All arguments have to be gauss formulas
	 c := 0;
	 tmp := {t,nil,nil};
	 for each sf in rl_argn f do <<
	    r := pasf_gaussdec1(sf,x,theo,append(pos,{c}),bvar,vl);
	    if car r then
	       % Found a new gauss subformula
	       tmp := {car tmp,append(cadr tmp,cadr r),caddr r . caddr tmp}
	    else
	       tmp := {nil,append(cadr tmp,cadr r),caddr r . caddr tmp};
	    c := c + 1
 	 >>;
	 if car tmp then
	    % The formula is a gauss formula
	    return {t,cadr tmp,'false}
	 else
	    return {nil,cadr tmp,rl_smkn('or,caddr tmp)}
      >>;
      % There are no bounded quantifiers inside the pnf matrix
      if rl_bquap rl_op f then rederr{"Bug in gauss decomposition"};
      % Gauss atomic formulas are only equations
      if pasf_atfp f then
 	 if pasf_opn f eq 'equal then
	    return pasf_gaussdec2(f,x,bvar,pos,vl)
	 else
 	    return {nil,nil,f};
      % This code should not be reached at runtime, because that would mean
      % there is a negation, extended boolean operator or a quantifier in the
      % formula.
      rederr{"Bug in gauss decomposition. Code assumed dead reached"}
   end;

procedure pasf_gaussdec2(atf,x,bvar,pos,vl);
   % Presburger arithmetic standard form gauss decomposition
   % subprocedure for the treatment of gauss-equations. [bvar] is a list
   % of bounded variables; [x] is the eliminated variable; [atf] is an
   % atomic gauss equation; [pos] is the position of this gauss formula;
   % [vl] is the new variable list. Returns the gauss decomposition of
   % the atomic formula.
   begin scalar repr,a_i,b;
      repr := repr_atfbnew(atf,x,pos,bvar);
      a_i := repr_r repr;
      % Bound for gauss formula
      b := pasf_substb(bvar,repr_t repr,car vl,nil,nil,cdr vl);
      if bvar then a_i := addf(a_i,numr simp car vl);
      if repr_n repr and domainp repr_n repr then
	 return {t,{elimpt_new(
	    % Position of the gauss formula
	    pos,
	    % Guard for gauss formulas
	    rl_mkn('and,{pasf_0mk2(('cong . repr_n repr),a_i),
	       pasf_0mk2('neq,repr_n repr)}),
	       % Test point for gauss formulas
	       a_i, repr_n repr,
	    if bvar then b else nil,nil)},'false};
      % Nothing can be done
      return {nil,nil,atf}
   end;

procedure pasf_gaussesord(a,b);
   % Presburger arithmetic standard form gauss elimination set ordering.
   % [a] and [b] are lists of ELIMPT. Returns one of [a] or [b]
   % according to the length of the elimination sets term form.
   begin
      if null a and b then return b
      else if null a and null b then return nil
      else if a and null b then return a
      else if length cdar b < length cdar a then return b
      else if length cdar b > length cdar a then return a;
      % Now the only case is the equality of lengths
      return b
   end;

endmodule; % pasfqe.red

end; % of file
