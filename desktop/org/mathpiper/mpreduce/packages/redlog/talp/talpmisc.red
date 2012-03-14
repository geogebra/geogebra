% ----------------------------------------------------------------------
% $Id: talpmisc.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(talp_misc_rcsid!* talp_misc_copyright!*);
   talp_misc_rcsid!* :=
      "$Id: talpmisc.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   talp_misc_copyright!* := "Copyright (c) 2004-2009 A. Dolzmann and T. Sturm"
>>;

module talpmisc;
% Term algebra Lisp prefix miscellaneous. Submodule of [talp].

procedure talp_ordatp(a1,a2);
   % Term algebra Lisp prefix ordering of atomic formulas
   % predicate. [a1] and [a2] are atomic formulas. Returns [T] iff
   % [a1] is less than or equal to [a2].
   begin scalar w1,w2;
      a1 := talp_simpat a1;
      a2 := talp_simpat a2;
      w1 := talp_arg2l a1;
      w2 := talp_arg2l a2;
      if w1 neq w2 then
      	 return talp_tordp(talp_arg2l a1,talp_arg2l a2);
      w1 := talp_arg2r a1;
      w2 := talp_arg2r a2;
      if w1 neq w2 then
      	 return talp_tordp(talp_arg2r a1,talp_arg2r a2);
      return 'true
   end;

procedure talp_tordp(t1,t2);
   % Term algebra Lisp prefix ordering of terms predicate. [t1] and
   % [t2] are Lisp prefix terms. Returns [T] iff [t1] is less than or
   % equal to [t2].
   ordp(t1,t2);

procedure talp_termmlat(at);
   % Term algebra Lisp prefix term multiplicity list of atomic
   % formula. [at] is an atomic formula. Returns the multiplicity list
   % off all non-zero terms in [at].
   begin scalar r,w;
      w := talp_arg2l at;
      if w then
	 r := (w . 1) . r;
      w := talp_arg2r at;
      if w then
	 r := (w . 1) . r;
      return r
   end;

procedure talp_atnum(f);
   % Term algebra Lisp prefix atomic formula number. [f] is a
   % formula. Returns the number of atomic formulas in [f].
   begin scalar op;
      op := rl_op f;
      if rl_boolp op then
 	 return for each subf in rl_argn f sum
    	    talp_atnum(subf);
      if rl_quap op then
    	 return talp_atnum(rl_mat f);
      if rl_tvalp op then return 0;
      % [f] is an atomic formula.
      return 1
   end;

procedure talp_varlat(atf);
   % Term algebra Lisp prefix variable list atomic formula. [atf] is
   % an atomic formula. Returns a list of identifiers. The set of
   % variables occurring in [atf].
   union(talp_varlt talp_arg2l atf, talp_varlt talp_arg2r atf);

procedure talp_varlt(term);
   % Term algebra Lisp prefix variable list term. [term] is a term in
   % Lisp prefix. Returns a list of identifiers. The set of variables
   % occurring in [term].
   talp_varlt1(term,nil);

procedure talp_varlt1(term,vl);
   % Term algebra Lisp prefix variable list term. [term] is a term in
   % Lisp prefix. [vl] is a list of identifiers. Returns a list of
   % identifiers. The set of variables occurring in [term] added to
   % [vl].
   begin
      if atom term then
	 if not atsoc(term,talp_getl()) then 
	    return lto_insert(term,vl)
	 else return nil;
      for each arg in talp_argl term do
	 vl := union(talp_varlt1(arg,vl),vl);
      return vl
   end;

procedure talp_subat(al,atf);
   % Term algebra Lisp prefix substitute in atomic formula. [al] is an
   % alist, [atf] is an atomic formula.  Returns an atomic formula.
   talp_mk2(talp_op atf,
      talp_subt(al,talp_arg2l atf),talp_subt(al,talp_arg2r atf));

procedure talp_subt(al,u);
   % Term algebra Lisp prefix substitute.
   begin scalar w;
      if idp u and (w := atsoc(u,al)) then
      	 return cdr w;
      if atom u then
      	 return u;
      return car u . for each arg in cdr u collect talp_subt(al,arg)
   end;

procedure talp_subalchk(al);
   % Term algebra Lisp prefix substitution alist check.
   ;

procedure talp_eqnrhskernels(x);
   % Term algebra Lisp prefix equation right hand side
   % kernels. [x] is an equation. Returns a list of all kernels
   % contained in the right hand side of [x].
   talp_varlt cdr x;

procedure talp_varsubstat(atf,new,old);
   % Term algebra Lisp prefix substitute variable for variable in
   % atomic formula. [atf] is an atomic formula; [old] is a variable;
   % [new] is a variable.
   talp_mk2(talp_op atf, talp_varsubstat1(talp_arg2l atf,old,new), 
      talp_varsubstat1(talp_arg2r atf,old,new));

procedure talp_varsubstat1(u,v,w);
   % Term algebra Lisp prefix substitute variable for variable in
   % atomic formula subroutine. [u] is a term, [v],[w] are variables.
   if u eq v then
      w
   else if atom u then
      u
   else
      car u . for each arg in cdr u collect talp_varsubstat1(arg,v,w);

procedure talp_negateat(atf);
   % Term algebra Lisp prefix negate atomic formula. [atf] is an
   % atomic formula. Returns an atomic formula equivalent to $\lnot
   % [atf]$.
   begin scalar op;
      op := talp_op atf;
      if op eq 'equal then op := 'neq else op := 'equal;
      return talp_mk2(op,talp_arg2l atf,talp_arg2r atf)
   end;

procedure talp_tab(f,cdl);
   % Term algebra Lisp prefix tableau; simplification. [f] is a
   % formula, [cdl] is a list of atomic formulas. Returns a
   % formula. The result is a case distinction on the atomic formulas
   % in [cdl] in conjunction with corresponding specializations of
   % [f].
   (if x neq 'false then cl_tab(f,cdl) else x) where x=rl_simpl(f,nil,-1);

procedure talp_atab(f);
   % Term algebra Lisp prefix automatic tableau; simplification. [f]
   % is a formula.  Returns a simplified equivalent of [f] or [f]
   % itself. The result is obtained by trying [cl_tab] with case
   % distictions on the signs of terms in [f] as [cdl].
   begin scalar w;
      w := talp_atab1 f;
      return if w then
	 cl_mktf w
      else
	 f  
   end;

procedure talp_atab1(f);
   % Term algebra Lisp prefix automatic tableau subroutine. [f] is a
   % formula.  Returns [nil] or a resl.
   begin scalar cdl,cdll,atnum,atnumold,atnumnf,nresl,resl,dpth;
      atnum := talp_atnum f;
      atnumold := atnum;
      cdll:= talp_a2cdl cl_atml f;
      if !*rlverbose then <<
	 ioto_tprin2t {atnum," = 100%"};
	 dpth := length cdll
      >>;
      while cdll do <<
 	 cdl := car cdll;
 	 cdll := cdr cdll;
	 nresl := cl_tab1(f,cdl);
 	 atnumnf := talp_atnum cl_mktf nresl;
      	 if !*rlverbose then <<
	    ioto_prin2 {"[",dpth,": ",atnumnf,"] "};
	    dpth := dpth - 1
	 >>;
	 if atnumnf < atnum then <<
    	    resl := nresl;
    	    atnum := atnumnf
	 >>
      >>;
      if !*rlverbose then
	 if atnum < atnumold then
	    ioto_tprin2t {"Success: ",atnumold," -> ",atnum}
      	 else
	    ioto_tprin2t {"No success, returning the original formula"};
      return resl
   end;

procedure talp_itab(f);
   % Term algebra Lisp prefix iterative tableau; simplification. [f]
   % is a formula.  Returns a simplified equivalent of [f] or [f]
   % itself. The result is obtained by iterative application of
   % [cl_atab]. Depends on the switch [rltabib]. With [rltabib] on,
   % the iteration is not performed on the entire formula but on the
   % single sepcialization branches.
   if !*rltabib then talp_itab2 f else talp_itab1 f;

procedure talp_itab1(f);
   % Term algebra Lisp prefix iterative tableau subroutine. [f] is a formula.
   % The switch [rltabib] is off. Returns a formula.
   begin scalar w,res;
      w := talp_atab1 f;
      while w do <<
	 res := cl_mktf w;
      	 if !*rlverbose then
	    ioto_tprin2t {"Recomputing tableau."};
	 w:= talp_atab1 res
      >>;
      return res or f
   end;

procedure talp_itab2(f);
   % Term algebra Lisp prefix iterative tableau subroutine. [f] is a formula.
   % Iterate branchwise. Returns a formula.
   begin scalar w;
      w := talp_atab1 f;
      return if w then
	 cl_mktf for each res in w collect (talp_itab2 car res) . cdr res
      else
	 f
   end;

procedure talp_a2cdl(atml);
   % Term algebra Lisp prefix atomic to case distinction list. [atml]
   % is an atomic formula multiplicity list. Returns a list of atomic
   % formulas.
   begin scalar atfs;
      while atml do <<
         atfs := caar atml . atfs;
      	 atml := cdr atml;
      >>;
      return for each x in atfs collect
      	 {talp_mk2('equal,talp_arg2l x,talp_arg2r x),
	    talp_mk2('neq,talp_arg2l x, talp_arg2r x)}
   end;

procedure talp_rnf(f);
   % Term algebra Lisp prefix refined normal form. [f] is a
   % formula. Returns a formula. Computes refined normal form of [f];
   % i.e. after computation, [f] solely consists of atomic formulas of
   % the form $w = v$, $w <> v$ respectively, where $w,v$ are inverse
   % terms or constants.
   talp_rnf1 cl_simpl(f,nil,-1);

procedure talp_rnf1(f);
   % Term algebra Lisp prefix refined normal form subroutine. [f] is a
   % formula. Returns a formula. Computes refined normal form of [f];
   % i.e. after computation, [f] solely consists of atomic formulas of
   % the form $w = v$, $w <> v$ respectively, where $w,v$ are inverse
   % terms or constants.
   begin scalar tmp;
      if atom f then return f;
      tmp := cl_simpl(f,nil,-1);
      if rl_tvalp tmp then return tmp;
      if talp_atfp tmp then
	 return if talp_acfrp tmp then 
	    cl_simpl(talp_raf tmp,nil,-1) 
	 else tmp;
      return cl_simpl(talp_op tmp .
	 for each sf in talp_argl tmp collect talp_rnf1 sf,nil,-1)
   end;

procedure talp_acfrp(atf);
   % Term algebra Lisp prefix atomic formula candidate for refinement
   % predicate. [atf] is a formula. Returns boolean value. Returns
   % true if [atf] includes one non-constant, non-inverse term; false
   % otherwise.
   talp_tcfrp talp_arg2l atf or talp_tcfrp talp_arg2r atf;

procedure talp_tcfrp(tt);
   % Term algebra Lisp prefix term candidate for refinement
   % predicate. [t] is a term. Returns boolean value. Returns true if
   % [t] is not an inverse term or a constant, false otherwise.
   not (atom tt or talp_invp tt);

procedure talp_raf(atf);
   % Term algebra Lisp prefix refine atomic formula.  [atf] is an
   % atomic formula. Returns a formula. Returns the refined normal
   % form of [atf].
   begin scalar rel,f,lhs,rhs,tmp,fst,snd,tmp2;
      rel := talp_op atf;
      % lhs is a term in nf, rhs is an inverse term
      lhs := if talp_tcfrp talp_arg2l atf then <<
	 rhs := talp_arg2r atf;
	 talp_arg2l atf >>
      else << rhs := talp_arg2l atf; talp_arg2r atf >>;
      f := talp_mkinv(talp_getinvfsym(talp_op lhs,1),rhs);
      fst := talp_simpat if rel eq 'equal then 
	 talp_mk2('neq,f,rhs) 
      else talp_mk2('equal,f,rhs);
      snd := for i:=1 : cdr atsoc(talp_op lhs,talp_getl()) collect <<
	 tmp := talp_getinvfsym(talp_op lhs,i);
	 talp_simpat talp_mk2(rel,talp_mkinv(tmp,rhs),nth(talp_fargl lhs,i))
      >>;
      snd := for each x in snd collect talp_rnf1 x;
      tmp2 := talp_mkn(if rel eq 'equal then 'and else 'or, fst . snd);
      return tmp2
   end;

procedure talp_atfp(f);
   % Term algebra Lisp prefix atomic formula predicate. [f] is an
   % formula. Returns boolean value. Returns true if [f] is an atomic
   % formula, false otherwise.
   pairp f and memq(talp_op f,'(neq equal));

procedure talp_getinvfsym(id,i);
   % Term algebra Lisp prefix get inverse function symbol. [id] is an
   % identifier denoting an operator symbol, [i] is an
   % integer. Returns an identifier. Returns the [i]-th inverse
   % operator symbol corresponding to [id].
   begin scalar tmp;
      tmp := talp_getextl();
      while caar tmp neq id do tmp := cdr tmp;
      for j:= 1 : i do tmp := cdr tmp;
      return caar tmp
   end;

endmodule;  % [talpmisc]

end;  % of file
