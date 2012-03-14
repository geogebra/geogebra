% ----------------------------------------------------------------------
% $Id: talpsism.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(talp_sism_rcsid!* talp_sism_copyright!*);
   talp_sism_rcsid!* :=
      "$Id: talpsism.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   talp_sism_copyright!* :=
      "Copyright (c) 2004-2009 A. Dolzmann, T. Sturm"
>>;

module talpsism;
% Term algebra Lisp prefix simplify smart. Submodule of [talp].

procedure talp_smwupdknowl(op,atl,knowl,n);
   % Term algebra Lisp prefix smart simplification wrapper update
   % knowledge.
   if !*rlsusi then
      cl_smupdknowl(op,atl,knowl,n)
   else
      talp_smupdknowl(op,atl,knowl,n);

procedure talp_smupdknowl(op,atl,knowl,n);
   % Term algebra Lisp prefix smart simplifier update knowledge. [op]
   % is one of the operators [and], [or]; [atl] is a list of atomic
   % formulas; [knowl] is knowledge base; [n] is an integer. Returns a
   % knowledge base. If [op] is [and], then all knowledge in [atl] is
   % added to the [knowl] with the tag [n]. If [op] is [or], then the
   % negation of all knowledge in [atl] is added to [knowl].
   begin scalar at;
      while atl do <<
	 at := car atl;
	 atl := cdr atl;
      	 knowl := talp_smupdknowl1(op,at,knowl,n);
	 if knowl eq 'false then <<
	    atl := nil;
	    at := 'break
	 >>
      >>;
      if at eq 'break then
	 return 'false
      else
      	 return knowl
   end;

procedure talp_smupdknowl1(op,at,knowl,n);
   % Term algebra Lisp prefix smart simplifier update knowledge
   % subroutine. [op] is one of the operators [and], [or]; [atl] is a
   % list of atomic formulas; [knowl] is knowledge base; [n] is an
   % integer. Returns a knowledge base. If [op] is [and], then all
   % knowledge in [atl] is added to the [knowl] with the tag [n]. If
   % [op] is [or], then the negation of all knowledge in [atl] is
   % added to [knowl].
   begin scalar ent,contra;
      if op eq 'or then <<
      	 ent := rl_negateat at;
      	 contra := at;
      >> else <<
      	 ent := at;
      	 contra := rl_negateat at;
      >>;
      if assoc(contra,knowl) then
	 return 'false;
      if talp_chkknowl(ent,knowl) then
	 return 'false;
      if assoc(ent,knowl) then
	 return knowl;
      return knowl := (ent . n) . knowl
   end;

procedure talp_chkknowl(atf,knowl);
   % Term algebra Lisp prefix check knowledge. [atf] is an atomic
   % formula; [knowl] is knowledge base. Returns a Boolean value.
   % Returns true if [knowl] and [atf] are contradictory.
   begin scalar invt,fs,tvar,stop,at,atop,tmp,result;
      if talp_candp atf then <<
      	 invt := if atom talp_arg2l atf then <<
	    tvar := talp_arg2l atf;
	    talp_arg2r atf
      	 >> else << 
	    tvar := talp_arg2r atf;
	    talp_arg2l atf
      	 >>;
      	 fs := talp_invf invt;
	 atop := talp_op atf;
	 tmp := knowl;
	 while tmp and not stop do <<
	    at := caar tmp;
	    if talp_candp at then <<
	       invt := if talp_invp talp_arg2l at then 
	       	  talp_arg2l at
	       else talp_arg2r at;	       
       	       if talp_invarg invt eq tvar then
	       	  if rl_op at eq atop and atop eq 'neq then
		     if talp_invf invt neq fs then <<
		    	result := 'true;
		    	stop := T
	 	     >>
	    >>;
	    tmp := cdr tmp
	 >>;
      >> else return nil;
      return result
   end;

procedure talp_smwrmknowl(knowl,v);
   if !*rlsusi then
      cl_susirmknowl(knowl,v)
   else
      cl_smrmknowl(knowl,v);

procedure talp_smwcpknowl(knowl);
   if !*rlsusi then
      cl_susicpknowl(knowl)
   else
      cl_smcpknowl(knowl);

procedure talp_smwmkatl(op,knowl,newknowl,n);
   if !*rlsusi then
      cl_susimkatl(op,knowl,newknowl,n)
   else
      cl_smmkatl(op,knowl,newknowl,n);

procedure talp_susibin(old,new);
   nil;

procedure talp_susipost(atl,knowl);
   atl;

procedure talp_susitf(at,knowl);
   at;

operator rltrygs;

procedure rltrygs(f);
   % Term algebra Lisp prefix try gauss simplification. [f] is a
   % first-order formula. Returns a first-order formula. The procedure
   % tries to simplify [f] via repeated substitution. The substitution
   % information is obtained from equations $t = t'$ between terms
   % $t,t'$ occurring in conjunctions. In order to reduce the depths
   % of terms of [f], we substitute terms with lower or equal depth
   % for terms with higher depth.
   talp_try f;

procedure talp_try(f);
   % Term algebra Lisp prefix try gauss simplification subroutine.
   talp_try1 talp_lssimpl talp_invtscsimpl rl_pnf f;

procedure talp_try1(f);
   % Term algebra Lisp prefix try gauss simplification subroutine.
   begin scalar tmp,op;
      if atom f then return f;
      tmp := talp_rnf f;
      if rl_tvalp tmp or talp_atfp tmp then return tmp;
      op := talp_op tmp;
      return if op eq 'or then
      	 cl_simpl(cl_nnfnot talp_try2( 'and .
	    for each sf in talp_argl tmp collect 
	       talp_try1 cl_nnfnot sf),nil,-1)
      else if op eq 'and then
	 cl_simpl(talp_try2( op .
	    for each sf in talp_argl tmp collect talp_try1 sf),nil,-1)
      else cl_simpl(op . rl_var tmp . {talp_try1(rl_mat tmp)},nil,-1)
   end;

procedure talp_try2(f);
   % Term algebra Lisp prefix try gauss simplification subroutine.
   begin scalar bvars,fvars,vars;
      bvars := cl_bvarl f;
      fvars := cl_fvarl f;
      vars := append(bvars,fvars);
      return if vars then talp_try3(f,vars) else f
   end;

procedure talp_try3(f,vars);
   % Term algebra Lisp prefix try gauss simplification subroutine.
   begin scalar res,subpairs,equs,extobj,contlvar,contrvar,lhs,rhs;
      if rl_tvalp f or talp_atfp f then return f;
      res := f;
      if rl_op res eq 'and then
	 for each subf in talp_argl res do
	    if null atom subf and talp_op subf eq 'equal then <<
	       lhs := talp_arg2l subf;
	       rhs := talp_arg2r subf;
	       for each x in vars do <<
		  if atom lhs and talp_contains(rhs,x) or 
		     talp_td lhs < talp_td rhs then contrvar := T;
		  if atom rhs and talp_contains(lhs,x) or
		     talp_td rhs < talp_td lhs then contlvar := T
	       >>;
	       if contlvar then <<
		  equs := subf . equs;
		  subpairs := (talp_arg2l subf . talp_arg2r subf) . subpairs
	       >>;
	       if contrvar then <<
		  equs := subf . equs;
		  subpairs := (talp_arg2r subf . talp_arg2l subf) . subpairs
	       >>;
	       contlvar := nil;
	       contrvar := nil;
	    >>;
      if subpairs then <<
	 extobj := talp_extlftrs(subpairs,equs,vars);
	 subpairs := car extobj;
	 equs := cdr extobj;
      >>;
      return if subpairs then
	 (if cdr x then talp_try1 car x else car x)
	    where x=talp_chsbstres(res,subpairs,equs)
      else talp_rnf res
   end;

procedure talp_specsub(p,f);
   % Term algebra Lisp prefix special substitution. [p] is a pair of
   % terms, [f] is a formula. Returns a formula. Replaces all
   % occurrences of the first element of [p] within [f] by the second
   % element of [p].
   begin scalar res,op;
      if rl_tvalp f then return f;
      if talp_atfp f then return talp_specsubat(car p,cdr p,f);
      op := talp_op f;
      res := op . for each subf in talp_argl f collect
	 talp_specsub(p,subf);
      return res
   end;

procedure talp_specsubat(old,new,atf);
   % Term algebra Lisp prefix special substitution atomic
   % formula. [old] and [new] are terms. [atf] is an atomic
   % formula. Returns a formula. Replaces all occurrences of [old]
   % within [f] by [new].
   talp_simpat talp_mk2(talp_op atf,
      talp_specsubt(old,new,talp_arg2l atf),
      talp_specsubt(old,new,talp_arg2r atf));

procedure talp_specsubt(old,new,term);
   % Term algebra Lisp prefix special substitution term.  [old] and
   % [new] are terms. [term] is a term.  Returns a term. Replaces all
   % occurrences of [old] within [term] by [new].
   begin scalar tmp;
      if atom term then return if term eq old then new else term;
      if talp_eqtp(old,term) then return new;
      tmp := car term . for each elem in cdr term collect
	 talp_specsubt(old,new,elem);
      return tmp
   end;

procedure talp_eqtp(t1,t2);
   % Term algebra Lisp prefix equal terms predicate.  [t1] and [t2]
   % are terms. Returns a boolean value. Returns T if [t1] and [t2]
   % are identical, nil otherwise.
   if atom t1 or atom t2 then
      if t1 eq t2 then T else nil
   else if atom car t1 and atom car t2 then
      if car t1 eq car t2 then talp_eqtp(cdr t1,cdr t2) else nil
   else talp_eqtp(car t1,car t2) and talp_eqtp(cdr t1,cdr t2);

procedure talp_chsbstres(f,lst,equs);
   % Term algebra Lisp prefix choose best result. [f] is a first-order
   % formula. [lst] is a list of replacement pairs. [equs] is a list
   % of equations. Returns a formula. Returns the best formula
   % (w.r.t. the sum of depths, the maximum depth, the number of
   % atomic formulas) among [f], and the formulas obtained from [lst].
   begin scalar atnum,curratnum,currdepth,stop,currsum,maxd,chosen,sumd,curr;
      atnum := talp_atnum f;
      maxd := talp_maxd f;
      sumd := talp_sumd f;
      while lst and not stop do <<
	 curr := talp_mkn('and, car equs . {talp_specsub(car lst,f)});
	 curr := talp_rnf curr;
	 equs := cdr equs;
	 lst := cdr lst;
	 if rl_tvalp curr then <<
	    stop := T;
	    chosen := curr
	 >> else <<
	    currsum := talp_sumd curr;
	    if currsum < sumd then <<
	       chosen := curr;
	       sumd := currsum
	    >> else if currsum = sumd then <<
	       currdepth := talp_maxd curr;
	       if currdepth < maxd then <<
		  chosen := curr;
		  maxd := currdepth
	       >> else if currdepth = maxd then <<
	       	  curratnum := talp_atnum curr;
	       	  if curratnum < atnum then <<
		     chosen := curr;
		     atnum := curratnum
	       	  >>
	       >>
	    >>
	 >>
      >>;
      return if chosen then chosen . T else f . nil
   end;

procedure talp_extlftrs(subl,eql,fvars);
   % Term algebra Lisp prefix extend list for transitivity. [subl] is
   % a list of pairs $x . y$ where x is a variable for which $y$ will
   % be substituted. [eql] is a list of equations. Returns a pair $l1
   % . l2$ where $l1$ is the extended substitution pair list and $l2$
   % is the extended list of equations from which the pairs
   % arise. Extends [subl] with substitution pairs which arise from
   % the given pairs via transitivity.
   begin scalar pw2v,rst,newsubl,neweql,transl;
      for each pair in subl do
	 if car pair memq fvars and cdr pair memq fvars then
	    pw2v := pair . pw2v
	 else rst := pair . rst;
      if not (pw2v and rst) then return subl . eql;
      newsubl := subl;
      neweql := eql;
      for each pair in rst do <<
	 transl := talp_gettransl(car pair,pw2v,nil);
	 for each elem in transl do <<
	    newsubl := (elem . cdr pair) . newsubl;
	    neweql := talp_simpat(talp_mk2('equal,elem,cdr pair)) . neweql
	 >>
      >>;
      return newsubl . neweql
   end;

procedure talp_gettransl(var,pl,result);
   % Term algebra Lisp prefix get transitivity list. [var] is a
   % variable, [pl] is a list of pairs. Returns a list of
   % variables. Returns the list of variables with which the
   % substitution list has to be extended.
   begin scalar varl,newpl;
      for each x in pl do
	 if car x eq var then varl := x . varl;
      for each x in varl do
	 if cdr x neq var and not (cdr x memq result) then
	    result := cdr x . result;
      if varl then for each x in pl do
	 if not talp_ctns(x,varl) then
	    newpl := x . newpl;
      return if newpl then
	 talp_gettransl(caar newpl,newpl,result)
      else result
   end;

procedure talp_sumd(f);
   % Term algebra Lisp prefix sum depths. [f] is a formula. Returns an
   % integer. Returns the sum of depths of all terms within [f].
   begin scalar tmp; integer sd;
      if atom f then return 0;
      tmp := rl_atl f;
      while tmp do <<
	 sd := sd + talp_td talp_arg2l car tmp + talp_td talp_arg2r car tmp;
	 tmp := cdr tmp;
      >>;
      return sd
   end;

procedure talp_ctns(pair,pairl);
   % Term algebra Lisp prefix contains. [pair] is a pair, [pairl] is a
   % list of pairs. Returns a boolean value. Checks whether [pairl]
   % contains [pair].
   begin scalar found;
      while pairl and not found do
	 if caar pairl eq car pair and cdar pairl eq cdr pair then
	    found := T
	 else pairl := cdr pairl;
      return found
   end;   

operator talp_lssimpl;

procedure talp_lssimpl(f);
   % Term algebra Lisp prefix language specific simplification. [f] is
   % a first-order formula. Returns a possibly simplified first-order
   % formula equivalent to [f].
   begin scalar op;
      f := talp_rnf f;
      if atom f or talp_atfp f then return f;
      op := talp_op f;
      if op eq 'or or op eq 'and then
      	 return talp_rnf talp_lssimpl1( op .
	    for each sf in talp_argl f collect talp_lssimpl sf)
      else if op memq '(ex all) then
	 return talp_rnf (op . rl_var f . {talp_lssimpl(rl_mat f)})
   end;

procedure talp_lssimpl1(f);
   % Term algebra Lisp prefix language specific simplification
   % subroutine.
   begin scalar tmp,tmp2,knowl,op;
      op := rl_op f;
      for each subf in f do
	 if talp_atfp subf then
	    if talp_candp subf then knowl := subf . knowl;
      tmp := talp_op f . for each subf in talp_argl f collect <<
	 if rl_tvalp subf then
	    subf
	 else if talp_atfp subf then
	    if talp_candp subf then
	       talp_tcandt(subf,knowl,op)
	    else subf
	 else <<
	    tmp2 := talp_lssimpl1 subf;
	    if talp_atfp tmp2 then
	       if talp_candp tmp2 then <<
		  knowl := tmp2 . knowl;
	       	  talp_tcandt(tmp2,knowl,op)
	       >> else tmp2
	    else tmp2
	 >>
      >>;
      return talp_rnf tmp
   end;

procedure talp_candp(atf);
   % Term algebra Lisp prefix candidate predicate. [atf] is an atomic
   % formula. Returns a Boolean value. Returns T if [atf] is of the
   % form $inv(x) = x$ or $inv(x) neq x$, where $inv(x)$ is an inverse
   % term having the variable $x$ as its argument.
   begin scalar lhs,rhs;
      lhs := talp_arg2l atf;
      rhs := talp_arg2r atf;
      if atom lhs and atom rhs then return nil;
      if atom lhs then
      	 if null talp_invp rhs then
	    return nil
	 else (if lhs neq talp_invarg rhs then return nil)
      else if atom rhs then
      	 if null talp_invp lhs then
	    return nil
	 else (if rhs neq talp_invarg lhs then return nil)
      else return nil;
      return 'true
   end;

procedure talp_tcandt(cand,knowl,op);
   % Term algebra Lisp prefix test candidate for refinement. [cand] is
   % an atomic formula of the form described above; [knowl] is a set
   % of atomic formulas; [op] is one of {and,or}. Returns a
   % formula. The procedure tries to simplify [cand] according to
   % [knowl] and the underlying language.
   if null knowl then cand else talp_tcandt1(cand,talp_op cand,knowl,op);

procedure talp_tcandt1(cand,cop,knowl,op);
   % Term algebra Lisp prefix test candidate for refinement
   % subroutine.
   begin scalar invt,tvar,cfs,result,scop;
      invt := if atom talp_arg2l cand then <<
	 tvar := talp_arg2l cand;
	 talp_arg2r cand
      >> else << 
	 tvar := talp_arg2r cand;
	 talp_arg2l cand
      >>;
      scop := if cop eq 'equal then 'neq else 'equal;
      cfs := talp_invf invt;
      if op eq 'and then
	 if cop eq 'equal then
   	    if talp_testknowl(invt,tvar,cop,knowl) then
	       result := rl_smkn('or,for each c in talp_getcts() collect
		  talp_simpat rl_mk2('equal,tvar,c))
	    else result := cand
	 else result := cand
      else if talp_testknowl(invt,tvar,cop,knowl) then
	 result := rl_smkn('and,for each c in talp_getcts() collect
	    talp_simpat rl_mk2('neq,tvar,c))
      else result := cand;
      return if result then result else cand
   end;

procedure talp_testknowl(term,var,atop,knowl);
   % Term algebra Lisp prefix test knowledge base. [term] is a term;
   % [var] is a variable; [atop] is one of {equal,neq}; [knowl] is a
   % knowledge base. Returns a Boolean value. Returns T if the
   % occurrence of [term] together with [knowl] allows simplification.
   begin scalar tmp,invfs,atf,invt;
      tmp := knowl;
      invfs := talp_getinvfts();
      invfs := delete(talp_op term,invfs);
      while tmp and invfs do <<
	 atf := car tmp;
	 if talp_candp atf then <<
	    invt := if talp_invp talp_arg2l atf then 
	       talp_arg2l atf 
	    else talp_arg2r atf;
	    if atop eq talp_op atf and talp_invarg invt eq var then 
	       invfs := delete(talp_op invt,invfs)
	 >>;
	 tmp := cdr tmp
      >>;
      if null invfs then return 'true;
      return nil
   end;

procedure talp_getinvfts();
   % Term algebra Lisp prefix get inverse functions. Returns a
   % list. Returns the list of all inverse functions with index 1.
   begin scalar tmp,invfset;
      tmp := setdiff(talp_getextl(),talp_getl());
      invfset := for each x in tmp join
      	 if (talp_getinvn car x) eq 1 then {car x};
      return invfset
   end;

procedure talp_getinvn(fsym);
   % Term algebra Lisp prefix get ['inv]-term's number. [fsym] is an
   % inverse function symbol. Returns the corresponding number.
   compress {cadr cddddr explode2 fsym};


operator talp_invtscsimpl;

procedure talp_invtscsimpl(f);
   % Term algebra Lisp prefix inverse term special case
   % simplification. [f] is a first-order formula. Returns a
   % first-order formula. Returns [f] with simplified special atomic
   % formulas.
   begin scalar op;
      f := talp_rnf f;
      if atom f then return f;
      if talp_atfp f then
	 if talp_invtscc f then 
	    return talp_rnf talp_invtscsimplat talp_simpat f
	 else return talp_simpat f;
      op := talp_op f;
      if op eq 'or or op eq 'and then
      	 return talp_rnf (
	    op . for each sf in talp_argl f collect talp_invtscsimpl sf)
      else if op memq '(ex all) then
	 return talp_rnf(op . rl_var f . {talp_invtscsimpl(rl_mat f)})
   end;

procedure talp_invtscsimplat(atf);
   % Term algebra Lisp prefix inverse term special case simplify
   % atomic formula. [atf] is an atomic formula. Returns a simplified
   % equivalent of [atf].
   begin scalar len,res,op,fctsyml,var,fctsym,nextfctsym,invt,candidate,pure;
      candidate := talp_invtscc atf;
      if not candidate then return atf;
      invt := cdr candidate;
      var := car candidate;
      op := car atf;
      pure := T;
      fctsym := talp_invf invt;
      fctsyml := fctsym . fctsyml;
      while not (atom talp_invarg invt) do <<
	 invt := talp_invarg invt;
	 nextfctsym := talp_invf invt;
	 if not memq(nextfctsym,fctsyml) then <<
	    fctsyml := nextfctsym . fctsyml;
	    pure := nil
	 >>
      >>;
      len := length fctsyml;
      if pure and len > 1 then 
      	 return talp_simpat 
	    talp_mk2(op,talp_mkinv(talp_getinvfsym(fctsym,1),var),var);
      res := for i:= 1 : len collect <<
	 fctsym := car fctsyml;
	 fctsyml := cdr fctsyml;
	 talp_simpat talp_mk2(op,talp_mkinv(talp_getinvfsym(fctsym,1),var),var)
      >>;
      return if talp_noffcts() eq length res then
	 talp_mkn(if op eq 'equal then 'or else 'and,
	    for each elem in talp_getcts() collect
	       talp_simpat talp_mk2(op,var,elem)) 
      else if op eq 'equal then 
	 talp_mkn('and,res)
      else talp_mkn('or,res)
   end;

procedure talp_invtscc(atf);
   % Term algebra Lisp prefix inverse term special case candidate.
   % [atf] is an atomic formula. Returns a Boolean value. Returns a
   % pair $var . invt$ if [atf] is of one of the forms
   % $inv_x1(...(inv_xn(y)))) = y$ or $inv_x1(...(inv_xn(y)))) <> y$,
   % nil otherwise.
   begin scalar var,invt,tmp,nof;
      var := if atom talp_arg2l atf then 
	 talp_arg2l atf 
      else if atom talp_arg2r atf then talp_arg2r atf else return nil;
      invt := if atom talp_arg2l atf then talp_arg2r atf else talp_arg2l atf;
      nof := talp_noffcts();
      if not (talp_invp invt) or 
	 not (talp_td invt > 1 or nof = 1) then return nil;
      tmp := invt;
      while not (atom talp_invarg tmp) do tmp := talp_invarg tmp;
      if var neq talp_invarg tmp then return nil;
      return var . invt
   end;

procedure talp_noffcts();
   % Term algebra Lisp prefix number of functions. Returns the number
   % of function symbols in the given language.
   begin integer nof;
      for each x in talp_getl() do
	 if cdr x > 0 then nof := nof + 1;
      return nof
   end;

procedure talp_getcts();
   % Term algebra Lisp prefix get constants. Returns a list. Returns
   % the list of constants in the given language.
   for each x in talp_getl() join
      if cdr x eq 0 then {car x};

endmodule;  % [talpsism]

end;  % of file
