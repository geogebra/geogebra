% ----------------------------------------------------------------------
% $Id: talpqe.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(talp_qe_rcsid!* talp_qe_copyright!*);
   talp_qe_rcsid!* := 
      "$Id: talpqe.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   talp_qe_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module talpqe;
% Term algebra Lisp prefix quantifier elimination. Submodule of [talp].

procedure talp_qe(phi,theo);
   % Term algebra Lisp prefix quantifier elimination. [phi] is a
   % fof. Returns a formula. Computes a quantifier-free formula
   % equivalent to [phi].
   <<
      phi := cl_simpl(cl_pnf phi,nil,-1);
      talp_qe1 phi
   >>;

procedure talp_qea(phi,theo);
   % Term algebra Lisp prefix quantifier elimination with
   % answer. [phi] is a fof. Returns a list of pairs $(..., (c_i,A_i),
   % ...)$. The $c_i$ are quantifier-free formulas and the $A_i$ are
   % lists of equations. Computes a quantifier-free formula equivalent
   % to [phi] and sample points for an outermost existential
   % quantifier block.
   begin scalar !*rlqeans;
      !*rlqeans := T;
      phi := cl_simpl(cl_pnf phi,nil,-1);
      return talp_qe1 phi
   end;

procedure talp_qe1(phi);
   % Term algebra Lisp prefix quantifier elimination subroutine. [phi]
   % is a fof. Returns a quantifier-free formula. Computes a
   % quantifier-free formula equivalent to [phi]. With [rlqeans] on, a
   % list of pairs $(..., (c_i,A_i), ...)$ is computed.
   begin scalar split,sphi;
      sphi := talp_try phi;
      if not rl_quap rl_op sphi then
	 return if !*rlqeans then {{sphi,nil}} else sphi;
      split := cl_splt phi;
      return talp_qe2(car split,cadr split)
   end;

procedure talp_qe2(qbl,mtrx);
   % Term algebra Lisp prefix quantifier elimination subroutine. [qbl]
   % is a list of quantifier blocks. [mtrx] is a quantifier-free
   % formula. Returns a quantifier-free formula or a list of pairs
   % $(..., (c_i,A_i), ...)$.
   begin scalar result,tmp,bvarl,qtf,qb,qbl;
      tmp := mtrx;
      % successively eliminate all quantifier blocks
      while qbl do <<
	 qb := car qbl;
	 qtf := car qb;
	 bvarl := cdr qb;
	 qbl := cdr qbl;
	 if !*rlverbose then
	    ioto_prin2t {"+++ eliminate block ",qtf,reverse bvarl};
	 tmp := talp_qeblock(qtf,bvarl,tmp,!*rlqeans and null qbl);
	 result := tmp
      >>;
      return if !*rlqeans then talp_getans(result) else result
   end;

procedure talp_qeblock(qtf,bvarl,mtrx,ansp);
   % Term algebra Lisp prefix quantifier eliminate block. [qtf] is a
   % quantifier. [bvarl] is a list of (bound) variables. [mtrx] is a
   % quantifier-free formula. [ansp] is Boolean. Returns a
   % quantifier-free formula or a list of pairs $(..., (c_i,A_i),
   % ...)$ depending on [ansp]. Eliminates all variables of [bvarl]
   % within [mtrx].
   begin scalar tmp,answer,newmtrx,l,stop,blk,result,nosp;
      newmtrx := if qtf eq 'ex then mtrx else cl_nnfnot mtrx;
      l := if rl_op newmtrx eq 'or then rl_argn newmtrx else {newmtrx};
      nosp := length l;
      % handle the disjuncts of [mtrx] separately
      while l and not stop do <<
	 blk := car l;
	 l := cdr l;
	 if !*rlverbose then ioto_prin2t {"    [",nosp,"] subproblems"};
	 tmp := talp_qeblock1(bvarl,blk,ansp);
	 if ansp then <<
	    if cdr tmp eq 'true then <<
	       stop := T;
	       result := car tmp
	    >> else if cdr tmp neq 'false then
	       result := if result then append(result,car tmp) else car tmp;
      	 >> else result := cl_simpl(
	    if result then rl_mk2('or,result,cdr tmp) else cdr tmp,nil,-1);
	 if result eq 'true then stop := T;
	 nosp := nosp - 1
      >>;
      if null result then result := if ansp then {nil . 'false} else 'false;
      if qtf eq 'all then
	 if ansp then <<
	    answer := for each x in result collect car x . cl_nnfnot cdr x;
	    return answer
	 >> else return cl_nnfnot result
      else return result
   end;

procedure talp_qeblock1(bvarl,mtrx,ansp);
   % Term algebra Lisp prefix quantifier eliminate block subroutine.
   begin scalar bvl;
      % delete variables which do not occur within mtrx
      bvl := for each var in bvarl join if talp_contains(mtrx,var) then {var};
      % permute variables
      if !*talpqp then bvl := talp_permbvarl(bvl,mtrx);
      if null bvl then
	 return nil . mtrx;
      if !*rlverbose and cdr bvl and !*talpqp then
	 ioto_prin2t {"    new order for processing bound variables: ",bvl};
      return talp_qeexblock(bvl,mtrx,nil,ansp)
   end;

procedure talp_qeexblock(bvl,mtrx,ans,ansp);
   % Term algebra Lisp prefix quantifier eliminate existential
   % block. [bvl] is a list of (existentially bound) variables. [mtrx]
   % is a quantifier-free formula. [ans] is a (list of) pair(s) $x
   % . t$, where $x$ is a variable and $t$ is a term. [ansp] is
   % Boolean. Returns a pair $a . r$, where $a$ is either nil or a
   % list of pairs $(..., (c_i,A_i), ...)$ depending on [ansp], and
   % $r$ is a quantifier-free formula. Eliminates all variables of
   % [bvarl] within [mtrx].
   begin scalar new,tmp,tmp2,result,stop,aset,aps,nosp;
      % eliminate first variable of [bvl] from [mtrx]
      tmp := talp_qevar(car bvl,mtrx,ansp);
      if ansp then <<
	 aps := talp_getpairs(car tmp,cdr tmp);
 	 if ans then aps := talp_inserteq(aps,ans);
      >>;
      if null cdr bvl then return aps . cdr tmp;
      if null ansp then return talp_qeexblock(cdr bvl,cdr tmp,ans,ansp);
      tmp := aps;
      nosp := length tmp;
      % handle each answer separately
      while tmp and not stop do <<
	 if !*rlverbose then ioto_prin2t {"   [",nosp,"] answers"};
	 nosp := nosp - 1;
	 new := car tmp;
	 tmp := cdr tmp;
	 tmp2 := talp_qeexblock(cdr bvl,cdr new,car new,ansp);
	 if cdr tmp2 eq 'true then <<
	    stop := T;
	    result := 'true;
	    aset := car tmp2
	 >> else if cdr tmp2 neq 'false then
	    aset := if aset then append(aset,car tmp2) else car tmp2;
      >>;
      if null result then result := cdr tmp2;
      if null aset then aset := car tmp2;
      return aset . result
   end;

procedure talp_qevar(bvar,mtrx,ansp);
   % Term algebra Lisp prefix elimination of one variable. [bvar] is a
   % variable. [mtrx] is a quantifier-free formula. [ansp] is
   % Boolean. Returns a pair $a . r$, where $a$ is either nil or a
   % list of pairs $(..., (c_i,A_i), ...)$ depending on [ansp], and
   % $r$ is a quantifier-free formula. Eliminates [bvar] from [mtrx].
   begin scalar tmp,tmp2,nosp,stop,new,fvarl,maxd,noft,ltype,result,aset;
      tmp := if rl_op mtrx eq 'or then rl_argn mtrx else {mtrx};
      nosp := length tmp;
      ltype := talp_gettype();
      % handle each disjunct separately
      while tmp and not stop do <<
	 if !*rlverbose then ioto_prin2t {"++ [",nosp,"] subproblems"};
	 nosp := nosp - 1;
	 new := car tmp;
	 tmp := cdr tmp;
	 % try deep Gauss elimination for [new] w.r.t. [bvar]
      	 if !*talpqegauss then tmp2 := talp_tryqegauss(bvar,mtrx,ansp);
	 if null !*talpqegauss or tmp2 eq 'failed then <<
	    fvarl := setdiff(cl_fvarl new,{bvar});
	    maxd := talp_depthbound(new,ltype);
	    noft := talp_numberbound(fvarl,maxd,ltype);
	    if !*rlverbose then <<
	       if !*talpqegauss then ioto_prin2t "failed";
	       ioto_prin2 {if !*talpqegauss then "  " else "+",
	       	  "standard QE for ",bvar,": substitute max ","[",noft,
	       	  "] terms of depth <= ",maxd," ... "};
	    >>;
	    % perform standard elimination for [new] w.r.t. [bvar]
	    tmp2 := talp_qevar1(bvar,new,ansp,fvarl,maxd)
	 >>;
	 if cdr tmp2 eq 'true then <<
	    stop := T;
	    aset := car tmp2;
	    result := 'true
	 >> else if cdr tmp2 neq 'false then <<
	    if ansp then
	       aset := if aset then append(aset,car tmp2) else car tmp2
	    else <<
	       result := cl_simpl(if result then 
	       	  rl_mk2('or,cdr tmp2,result) else cdr tmp2,nil,-1);
	       if result eq 'true then stop := T
	    >>
	 >>
      >>;
      if null result then result := cdr tmp2;
      if null aset then aset := car tmp2;
      return aset . result
   end;

procedure talp_qevar1(bvar,mtrx,ansp,fvarl,maxd);
   % Term algebra Lisp prefix quantifier eliminate one variable
   % subroutine. [bvar] is a variable. [mtrx] is a quantifier-free
   % formula. [ansp] is Boolean. [fvarl] is a list of (free)
   % variables.  [maxd] is an integer. Returns a pair $a . r$, where
   % $a$ is either nil or a list of pairs $x . t$, and $r$ is either a
   % quantifier-free formula or a list of quantifier-free formulas,
   % depending on [ansp].
   begin scalar result,answer,tmp,t2sub,stop,subpair,new;
      t2sub := talp_nextt(nil,maxd,fvarl);
      % eliminate [bvar] from [mtrx] by substitution of enumerated test terms
      while t2sub and null stop do <<
	 subpair := bvar . t2sub;
	 tmp := talp_try cl_subfof({subpair},mtrx);
	 if tmp eq 'true then <<
	    if ansp then answer := subpair;
	    result := tmp;
	    stop := T
	 >> else if tmp neq 'false then <<
	    if ansp then <<
	       answer := subpair . answer;
	       result := tmp . result
	    >> else <<
	       result := cl_simpl(
		  if result then rl_mk2('or,result,tmp) else tmp,nil,-1);
	       if result eq 'true then stop := T
	    >>
	 >>;
	 new := talp_copy t2sub;
	 t2sub := talp_nextt(new,maxd,fvarl)
      >>;
      if !*rlverbose then ioto_prin2t "succeeded";
      if null result then result := 'false;
      return answer . result
   end;

procedure talp_permbvarl(bvarl,mtrx);
   % Term algebra Lisp prefix permute list of bound variables. [bvarl]
   % is a list of variables; [mtrx] is a quantifier-free
   % formula. Returns a list of variables. Permutes [bvarl] according
   % to the possibility to apply Gauss elimination and the number of
   % occurrences the variables have in [mtrx].
   begin scalar tmp,newbvarl,n;
      if rl_tvalp mtrx or null bvarl then return bvarl;
      n := 2 * length rl_atl mtrx + 1;
      tmp := for each var in bvarl join
	 if talp_contains(mtrx,var) then
      	    if !*talpqegauss and not (talp_trygaussvar(var,mtrx,nil) 
	       memq '(failed ignore) or talp_trygaussvar(var, talp_rnf mtrx,
	       nil) memq '(failed ignore)) then
		  {var . n}
	    else {var . talp_cocc(mtrx,var)};
      if tmp then <<
	 tmp := talp_mergesort tmp;
	 newbvarl := for each x in tmp collect car x;
      >> else return nil;
      return newbvarl
   end;

procedure talp_cocc(f,var);
   % Term algebra Lisp prefix count occurrences. [f] is a
   % formula. [var] is a variable. Returns an integer. Computes the
   % number of occurrences of [var] in [f].
   begin integer noccs;
      noccs := 0;
      if pairp f then
      	 for each x in f do
	    if pairp x then noccs := noccs + talp_cocc(x,var)
	    else (if x eq var then noccs := noccs + 1)
      else if var eq f then return 1;
      return noccs      
   end;

procedure talp_mergesort(l);
   % Term algebra Lisp prefix mergesort. [l] is a list of
   % pairs. Returns a list. Sorts the elements of [l] in descending
   % order w.r.t. their second entries.
   begin scalar crit,s1,s2;
      if null l or null cdr l then return l;
      crit := car l;
      for each entry in cdr l do
	 if cdr entry > cdr crit then
 	    s1 := entry . s1
 	 else
 	    s2 := entry . s2;
      return nconc(talp_mergesort reversip s1,
	 crit . talp_mergesort reversip s2)
   end;

procedure talp_getpairs(answerset,resultset);
   % Term algebra Lisp prefix get pairs. [answerset] is a a list of
   % pairs; [resultset] is a list of quantifier-free formulas. Returns
   % a list of pairs. Combines each element of [answerset] with the
   % corresponding element of [resultset].
   begin scalar tmp,answer,result;
      if atom resultset then
	 return {answerset . resultset};
      while resultset do <<
	 answer := car answerset;
	 result := car resultset;
	 tmp := if tmp then (answer . result) . tmp else {answer . result};
	 resultset := cdr resultset;
	 answerset := cdr answerset
      >>;
      return tmp
   end;

procedure talp_copy(l);
   % Term algebra Lisp prefix copy. [l] is any. Returns a copy of [l].
    begin scalar nl;
       if atom l then 
	  nl := l
       else nl := for each x in l collect 
	  if atom x then
	     x
	  else talp_copy x;
       return nl
    end;

procedure talp_inserteq(ans,equa);
   % Term algebra Lisp prefix insert equation. [ans] is a list of
   % pairs; [equa] is a (list of) pair(s). Returns a list of
   % pairs. Inserts [equa] into [ans].
   begin scalar tmp,equs;
      if cdar ans eq 'false then return ans;
      tmp := ans;
      for each elem in tmp do <<
	 equs := talp_updateinfo(elem,if atom car equa then {equa} else equa);
	 car elem := if atom caar elem then 
	    car elem . equs
	 else append(car elem,equs)
      >>;
      return tmp
   end;

procedure talp_updateinfo(ans,equs);
   % Term algebra Lisp prefix insert equation. [ans] is a list of
   % pairs; [equs] is a list of pairs. Returns a list of
   % pairs. Updates equs according to the information obtained from
   % [ans].
   begin scalar tmp,result;
      tmp := car ans;
      result := for each elem in equs collect
	 car elem . talp_simplt talp_specsubt(car tmp, cdr tmp,cdr elem);
      return result
   end;

procedure talp_getans(ansinfo);
   % Term algebra Lisp prefix get answer. [ansinfo] is a list of
   % pairs. Returns an answer set. Generates an answerset for RLQEA
   % from [ansinfo].
   begin scalar answer,result,equa,equat,info;
      if null car ansinfo then
	 return {{cdr ansinfo,nil}};
      while ansinfo do <<
	 info := car ansinfo;
	 equat := car info;
	 result := cdr info;
  	 equa := if equat then
	    if atom car equat then
	       {{'equal,car equat,cdr equat}}
	    else for each x in equat collect {'equal,car x,cdr x};
	 answer := {result,equa} . answer;
	 ansinfo := cdr ansinfo
      >>;
      return answer
   end;

procedure talp_gettype();
   % Term algebra Lisp prefix get type. No parameters. Returns an
   % identifier ([U1],[UN] or [NN]) corresponding to the underlying
   % language type.
   begin scalar lang,unacount,done;
      unacount := 0;
      lang := talp_getl();
      while lang and not done do <<
	 if cdar lang > 1 then done := T;
	 if cdar lang eq 1 then unacount := unacount + 1;
	 lang := cdr lang
      >>;
      return if done then 'NN else if unacount > 1 then 'UN else 'U1;
   end;

procedure talp_depthbound(f,langtype);
   % Term algebra Lisp prefix depth-bound. [f] is a
   % formula. [langtype] is an identifier. Returns an integer. Returns
   % an estimated maximum depth a candidate term must have
   % w.r.t. [langtype].
   if langtype eq 'NN then
      talp_maxd f * (rl_atnum f + 1) + ceiling log(rl_atnum f + 1)
   else if langtype eq 'UN then
      2 * talp_maxd f + ceiling log(rl_atnum f + 1)
   else 2 * talp_maxd f + rl_atnum f;

procedure talp_numberbound(fvarl,depth,langtype);
   % Term algebra Lisp prefix number bound. [fvarl] is a list of
   % atomic formulas. [depth] is an integer. [langtype] is an
   % identifier. Returns an integer. Returns an estimated upper bound
   % on the number of terms of depth $\leq$ [depth] that have be
   % computed by talp_nextt.
   if langtype eq 'U1 then
      talp_nbu1(fvarl,depth)
   else if langtype eq 'UN then
      talp_nbun(fvarl,depth)
   else
      talp_nbnn(fvarl,depth);

procedure talp_nbu1(fvarl,depth);
   % Term algebra Lisp prefix number bound for type U1. [fvarl] is a
   % list of atomic formulas. [depth] is an integer. Returns an
   % integer. Returns an upper bound on the number of terms of depth
   % $\leq$ [depth].
   begin scalar noft,nocts,novars,tmp;
      nocts := talp_getnofcts();
      novars := length fvarl;
      noft := nocts + novars;
      tmp := noft + novars;
      while depth > 0 do <<
	 noft := tmp + noft;
	 tmp := tmp + novars;
	 depth := depth - 1
      >>;
      return noft
   end;

procedure talp_nbun(fvarl,depth);
   % Term algebra Lisp prefix number bound for type UN. [fvarl] is a
   % list of atomic formulas. [depth] is an integer. Returns an
   % integer. Returns an upper bound on the number of terms of depth
   % $\leq$ [depth].
   (length fvarl + length talp_getextl())**(if depth > 0 then depth else 1);

procedure talp_nbnn(fvarl,depth);
   % Term algebra Lisp prefix number bound for type NN. [fvarl] is a
   % list of atomic formulas. [depth] is an integer. Returns an
   % integer. Returns an upper bound on the number of terms of depth
   % $\leq$ [depth].
   begin scalar ma;
      ma := talp_getmaxar();
      return (length talp_getextl() + length fvarl)**ma**(
	 if depth > 0 then depth else 1)
   end;

procedure talp_getnofcts();
   % Term algebra Lisp prefix get number of constants. Returns an
   % integer. Returns the number of constants in the given language.
   for each x in talp_getl() sum
      if cdr x eq 0 then 1 else 0;

procedure talp_getmaxar();
   % Term algebra Lisp prefix get maximum arity. Returns an
   % integer. Returns the maximum arity of all function symbols of the
   % given language.
   begin scalar arity;
      arity := 0;
      for each x in talp_getl() do
	 if cdr x > arity then arity := cdr x;
      return arity
   end;

procedure talp_maxd(f);
   % Term algebra Lisp prefix maximum depth. [f] is a
   % formula. Returns an integer. Returns the maximum depth of all
   % terms in [f].
   begin scalar tmp; integer md,mdtmp;
      if atom f then return 0;
      tmp := rl_atl f;
      while tmp do <<
	 mdtmp := max2(talp_td talp_arg2l car tmp, talp_td talp_arg2r car tmp);
	 if mdtmp > md then md := mdtmp;
	 tmp := cdr tmp;
      >>;
      return md
   end;

procedure talp_td(term);
   % Term algebra Lisp prefix term depth. [term] is a term. Returns an
   % integer. Returns the depth of [term].
   if atom term then 
      0
   else 1 + lto_max for each arg in talp_fargl term collect talp_td arg;

procedure talp_contains(f,var);
   % Term algebra Lisp prefix contains. [f] is a formula, [var] is an
   % identifier. Returns a boolean value. Returns true if [f] contains
   % [var], nil otherwise.
   begin scalar cv;
      if pairp f then
      	 for each x in f do
	    if pairp x and not cv then cv := talp_contains(x,var)
	    else (if x eq var then cv := t)
      else if var eq f then return t;
      return cv
   end;


%------- Term Enumeration Part -------

procedure talp_nextt(last,md,vl);
   % Term algebra Lisp prefix next term. [last] is a term represented
   % in Lisp prefix form. [md] is an integer. [vl] is a list of
   % variables. Returns a Lisp prefix term. Returns the next term
   % within depth-bound [md] following [last].
   begin scalar cl,cvv,vv,fv,ifv;
      fv := talp_list2vec for each x in talp_getl() join
	 if cdr x eq 0 then 
	    << cl := car x . cl; nil >> 
	 else talp_mk!-invs(vl,x);
      ifv := talp_list2vec for i:=0 : upbv fv join
	 (if talp_invp y then {y}) where y=getv(fv,i);
      cvv := talp_list2vec nconc(reversip cl,vl);
      vv := talp_list2vec vl;
      return if last then (if car z then cdr z) 
	    where z=talp_nextt1(last,0,md,cvv,vv,fv,ifv,nil)
      else if upbv cvv > -1 then getv(cvv,0)
   end;

procedure talp_nextt1(last,cd,md,cvv,vv,fv,ifv,invp);
   % Term algebra Lisp prefix next term subroutine. [last] is a term
   % represented in Lisp prefix form. [cd] and [md] are
   % integers. [cvv],[vv],[fv] and [ifv] are vectors. [invp] is a
   % boolean value. Returns a term represented in Lisp prefix
   % form. Returns next term within depth bound [md] following [last].
   begin scalar done,reset,temp;
      if atom last then
	 return talp_nextt!-atom(last,cd,md,cvv,vv,fv,ifv,invp);
      if talp_invp last then
	 (if car x then << cdr last := {cdr x}; return t . last >>) 
	       where x=talp_nextt1(car talp_fargl last,cd+1,md,cvv,vv,fv,ifv,t)
      else <<
      	 temp := talp_fargl last;
	 reset := getv(cvv,0);
     	 while temp and not done do <<
	    (if car x then << 
	       done := t; car temp := cdr x 
	    >> else car temp := reset) 
	       where x=talp_nextt1(car temp,cd+1,md,cvv,vv,fv,ifv,nil);
	    temp := cdr temp
      	 >>
      >>;
      if not done then
	 if invp then
	    (if i < upbv ifv then return t . talp_get!-minfct(i+1,ifv,vv,cvv)) 
	       where i=talp_get!-idx(last,ifv)
	 else (if i < upbv fv then return t . talp_get!-minfct(i+1,fv,vv,cvv))
	       where i=talp_get!-idx(last,fv);
      return done . last
   end;

procedure talp_nextt!-atom(last,cd,md,cvv,vv,fv,ifv,invp);
   % Term algebra Lisp prefix next term atom. As above. Handles atomic
   % [last]s.
   if invp then
      if talp_get!-idx(last,vv) < upbv vv then
	 t . getv(vv,talp_get!-idx(last,vv) + 1)
      else if cd < md then t . talp_get!-minfct(0,ifv,vv,cvv) else nil . last
   else if talp_get!-idx(last,cvv) < upbv cvv then
      t . getv(cvv,talp_get!-idx(last,cvv) + 1)
   else if cd < md then t . talp_get!-minfct(0,fv,vv,cvv) else nil . last; 

procedure talp_list2vec(l);
   % Term algebra Lisp prefix list to vector. [l] is a list. Returns
   % vector. Transforms [l] to Lisp vector.
   begin scalar vec;
      vec := mkvect (length l - 1);
      for i:=0 : upbv vec do << putv(vec,i,car l); l := cdr l >>;
      return vec
   end;

procedure talp_mk!-invs(vp,f);
   % Term algebra Lisp prefix make inverses. [f] is an alist
   % element. Returns list of inverse-functions corresponding to [f].
   if vp then f . for i:=1 : cdr f collect talp_mkinvop(car f,i) . 1 else {f};

procedure talp_get!-idx(last,vec);
   % Term algebra Lisp prefix get index. [last] is a term represented
   % in Lisp prefix form. [vec] is a vector. Returns an
   % integer. Returns position of [last] within [vec].
   begin scalar found; integer pos;
      while pos <= upbv vec and not found do
      	 if atom last then
	    if getv(vec,pos) eq last then found := t else pos:=pos+1
	 else if pairp talp_fop last then
	    (if pairp talp_fop x and talp_invf talp_fop x eq 
	       talp_invf talp_fop last and talp_invn talp_fop x eq 
		  talp_invn talp_fop last then found := t else pos:=pos+1)
		  where x=getv(vec,pos)
	 else if talp_fop getv(vec,pos) eq talp_fop last then 
	    found := t 
	 else pos:=pos+1;
      return if found then pos else -1
   end;

procedure talp_get!-minfct(idx,fv,vv,cv);
   % Term algebra Lisp prefix get minimal function. [idx] is an
   % integer.  [fv],[vv],[cv] are vectors. Returns term represented in
   % Lisp prefix form. Returns function at position [idx] in [fvec]
   % initialized with corresponding argument(s).
   (if talp_invp x then 
      talp_fop x . {getv(vv,0)}
   else talp_mktn(talp_fop x,for i:=1 : cdr x collect getv(cv,0)))
      where x = getv(fv,idx);


%------- Gauss Elimination Part -------

procedure talp_tryqegauss(bvar,mtrx,ansp);
   % Term algebra Lisp prefix try deep Gauss elimination. [bvar] is a
   % variable; [mtrx] is a quantifier-free formula; [ansp] is
   % Boolean. Returns 'failed, a quantifier-free formula, or a pair $a
   % . r$, where $a$ is a (list of) pair(s) $x . t$ and $t$ is a (list
   % of) quantifier-free formula(s). If possible, [bvar] is eliminated
   % from [mtrx] via deep Gauss elimination, otherwise 'failed is
   % returned.
   begin scalar gauss;
      if !*rlverbose then
	 ioto_prin2 {"+ try gauss elimination for ",bvar," ... "};
      gauss := talp_trygauss(bvar,mtrx,ansp);
      if gauss eq 'failed then <<
	 if !*rlverbose then <<
	    ioto_prin2t "failed";
	    ioto_prin2 {"  try gauss elimination for ",bvar,
	       " with transformed input formula ... "};
	 >>;
	 mtrx := talp_rnf mtrx;
	 gauss := talp_trygauss(bvar,mtrx,ansp)
      >>;
      if gauss neq 'failed then <<
	 if !*rlverbose then ioto_prin2t "succeeded";
	 return gauss
      >>;
      return 'failed
   end;

procedure talp_trygauss(bv,f,ansp);
   % Term algebra Lisp prefix try gauss elimination. [bv] is a
   % (existentially bound) variable. [f] is a quantifier-free
   % formula. [ansp] is Boolean. Returns 'failed, a list of pairs $a_i
   % . f_i$, where $a_i$ are answers and $p_i$ are quantifier-free
   % formulas or $nil . f_1$ where f_1 is a quantifier-free formula.
   begin scalar bv,tmp,elimset;
      tmp := talp_trygaussvar(bv,f,ansp);
      if tmp eq 'failed or tmp eq 'ignore then return 'failed;
      elimset := if listp tmp and null atom car tmp then tmp else {tmp};
      return talp_trygauss1(elimset,f,ansp)
   end;

procedure talp_trygauss1(es,f,ansp);
   % Term algebra Lisp prefix try gauss elimination subroutine. [es]
   % is an elimination set for one variable. [f] is a quantifier-free
   % formula. [ansp] is Boolean. Returns 'failed, a list of pairs $a_i
   % . f_i$, where $a_i$ are answers and $p_i$ are quantifier-free
   % formulas or $nil . f_1$ where f_1 is a quantifier-free formula.
   begin scalar subpair,result,answer,tmp,stop;
      while es and null stop do <<
	 subpair := car es;
	 tmp := talp_try cl_subfof({subpair},f);
	 if tmp eq 'true then <<
	    if ansp then answer := subpair;
	    result := tmp;
	    stop := T
	 >> else if tmp neq 'false then <<
	    if ansp then <<
	       answer := subpair . answer;
	       result := tmp . result
	    >> else <<
	       result := cl_simpl(if result then 
		  rl_mk2('or,result,tmp) else tmp,nil,-1);
	       if result eq 'true then stop := T
	    >>
	 >>;
	 es := cdr es
      >>;      
      if null result then result := 'false;
      return answer . result
   end;

procedure talp_trygaussvar(bv,f,ansp);
   % Term algebra Lisp prefix try gauss elimination of one
   % variable. [bv] is a (existentially bound) variable. [f] is a
   % quantifier-free formula. [ansp] is Boolean. Returns 'failed, a
   % list of pairs $a_i . f_i$, where $a_i$ are answers and $p_i$ are
   % quantifier-free formulas or $nil . f_1$ where f_1 is a
   % quantifier-free formula.
   if talp_atfp f then
      talp_qesolset(bv,f)
   else if rl_op f eq 'and then
      talp_gaussand(bv,rl_argn f,ansp)
   else if rl_op f eq 'or then
      talp_gaussor(bv,rl_argn f,ansp)
   else 'failed;

procedure talp_qesolset(v,atf);
   % Term algebra Lisp prefix quantifier elimination solution set. [v]
   % is a variable; [atf] is an atomic formula. Return 'failed,
   % 'ignore or a pair $x . t$, where $x$ is a variable and $t$ is a
   % term.
   begin scalar subs,lhs,rhs;
      lhs := talp_arg2l atf;
      rhs := talp_arg2r atf;
      if not (talp_contains(lhs,v) or talp_contains(rhs,v)) then 
	 return 'ignore;
      if rl_op atf neq 'equal then
	 return 'failed;
      if talp_contains(lhs,v) and talp_contains(rhs,v) then
	 return 'failed;
      if lhs neq v and rhs neq v then
	 return 'failed;
      subs := if lhs eq v then rhs else lhs;
      return v . subs
   end;

procedure talp_gaussand(v,fl,ansp);
   % Term algebra Lisp prefix gauss elimination and case. [v] is a
   % variable; [fl] is a list of formulas; [ansp] is Boolean.
   begin scalar w, curr,stop;
      curr := talp_trygaussvar(v,car fl,ansp);
      if curr eq 'failed or curr eq 'ignore then <<
      	 fl := cdr fl;
      	 while fl and not stop do <<
	    w := talp_trygaussvar(v,car fl,ansp);
	    if w neq 'failed and w neq 'ignore then <<
	       stop := T;
	       curr := w
	    >>;
	    fl := cdr fl
      	 >>
      >>;
      return if curr then curr else 'failed
   end;

procedure talp_gaussor(v,fl,ansp);
   % Term algebra Lisp prefix gauss elimination and case. [v] is a
   % variable; [fl] is a list of formulas; [ansp] is Boolean.
   begin scalar w,curr,stop;
      curr := talp_trygaussvar(v,car fl,ansp);
      if curr neq 'failed then <<
      	 fl := cdr fl;
      	 while fl and not stop do <<
	    w := talp_trygaussvar(v,car fl,ansp);
	    if w eq 'failed then <<
	       curr := nil;
	       stop := T;
	    >> else if w neq 'ignore then 
	       curr := if curr neq 'ignore then w . {curr} else w;
	    fl := cdr fl;
      	 >>
      >>;
      return if curr then curr else 'failed
   end;

endmodule;  % [talpqe]

end;  % of file
