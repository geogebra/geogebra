% ----------------------------------------------------------------------
% $Id: acfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(acfsf_misc_rcsid!* acfsf_misc_copyright!*);
   acfsf_misc_rcsid!* :=
      "$Id: acfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   acfsf_misc_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module acfsfmisc;
% Algebraically closed field standard form other. Submodule of [acfsf].

procedure acfsf_termprint(u);
   % Algebraically closed field term print. [u] is a
   % term. The return value is not specified. Prints [u] AM-like.
   <<
      sqprint !*f2q u where !*nat=nil;
      ioto_prin2 nil
   >>;

procedure acfsf_clnegrel(r,flg);
   % Algebraically closed field conditionally logically negate
   % relation. [r] is a relation. Returns for [flg] equal to [nil] a
   % relation $R$ such that for terms $t_1$, $t_2$ we have
   % $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$. Returns [r] for
   % non-[nil] [flg].
   if flg then r else acfsf_lnegrel r;

procedure acfsf_lnegrel(r);
   % Algebraically closed field logically negate relation. [r] is a
   % relation. Returns a relation $R$ such that for terms $t_1$, $t_2$
   % we have $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$.
   if r eq 'equal then 'neq
   else if r eq 'neq then 'equal
   else rederr {"acfsf_lnegrel: unknown operator ",r};

procedure acfsf_fctrat(atf);
   % Algebraically closed field factorize atomic formula. [atf] is an
   % atomic formula. Returns the factorized left hand side of [atf] as
   % a list $(...,(f_i . n_i),...)$, where the $f_i$ are the factors
   % as SF's and the $n_i$ are the corresponding multiplicities. The
   % integer content is dropped.
   cdr fctrf acfsf_arg2l atf;

procedure acfsf_negateat(f);
   % Algebraically closed field negate atomic formula. [f] is an
   % atomic formula. Returns an atomic formula equivalent to $\lnot
   % [f]$.
   acfsf_mkn(acfsf_lnegrel acfsf_op f,acfsf_argn f);

procedure acfsf_varlat(atform);
   % Algebraically closed field variable list of atomic formula.
   % [atform] is an atomic formula. Returns the set of variables
   % contained in [atform] as a list.
   kernels acfsf_arg2l(atform);

procedure acfsf_varsubstat(atf,new,old);
   % Algebraically closed substitute variable for variable in atomic
   % formula. [atf] is an atomic formula; [new] and [old] are
   % variables. Returns [atf] with [new] substituted for [old].
   acfsf_0mk2(acfsf_op atf,numr subf(acfsf_arg2l atf,{old . new}));

procedure acfsf_ordatp(a1,a2);
   % Algebraically closed field order predicate for atomic formulas.
   % [a1] and [a2] are atomic formulas. Returns [T] iff [a1] is
   % strictly less than [a2] wrt. some syntactical ordering; returns
   % [nil] else. The specification that [nil] is returned if
   % $[a1]=[a2]$ is used in [acfsf_subsumeandcut].
   begin scalar lhs1,lhs2;
      lhs1 := acfsf_arg2l a1;
      lhs2 := acfsf_arg2l a2;
      if lhs1 neq lhs2 then return ordp(lhs1,lhs2);
      return acfsf_ordrelp(acfsf_op a1,acfsf_op a2)
   end;

procedure acfsf_ordrelp(r1,r2);
   % Algebraically closed field standard form relation order
   % predicate. [r1] and [r2] are acfsf-relations. Returns a [T] iff
   % $[r1] <= [r2]$.
   r1 eq r2 or r1 eq 'equal;

procedure acfsf_a2cdl(atml);
   % Algebraically closed field atomic formulas to case distinction
   % lists. [atml] is a multiplicity list of atomic formulas. Returns
   % a list $(...,(t_i = 0, t_i \neq 0),...)$ of case distinctions
   % lists, where the $t_i$ are the right hand side terms of the
   % atomic formulas in [atml].
   begin scalar x;
      return for each pr in atml collect <<
	 x := acfsf_arg2l car pr;
	 {acfsf_0mk2('equal,x),acfsf_0mk2('neq,x)}
      >>
   end;

procedure acfsf_t2cdl(term);
   % Algebraically closed field term to case distinction list. [term]
   % is a term. Returns a case distinction list $([term] = 0, [term]
   % \neq 0)$ wrt. [term].
   {acfsf_0mk2('equal,term),acfsf_0mk2('neq,term)};

procedure acfsf_subat(al,f);
   % Algebraically closed field substitute into atomic formula. [al]
   % is an ALIST $(..., (v_i . t_i), ...)$, where the $v_i$ are
   % kernels, and the $t_i$ are Lisp prefix terms; [f] is an atomic
   % formula. Returns [f] with $t_i$ substituted for each occurrence
   % of $v_i$. The $t_i$ must be such that the substitution does not
   % yield parametric denominators.
   begin scalar nlhs;
      nlhs := subf(acfsf_arg2l f,al);
      if not domainp denr nlhs then
	 rederr "parametric denominator after substitution";
      return acfsf_0mk2(acfsf_op f,numr nlhs)
   end;

procedure acfsf_subalchk(al);
   % Algebraically closed field substitution ALIST check. [al] is an
   % ALIST $(..., (v_i . t_i), ...)$, where the $v_i$ are kernels, and
   % the $t_i$ are Lisp prefix terms. The return value is unspecified.
   % Raises an error if some $t_i$ contains a parametric denominator.
   for each x in al do
      if not domainp denr simp cdr x then
	 rederr "parametric denominator in substituted term";

procedure acfsf_eqnrhskernels(x);
   % Algebraically closed field equation right hand side kernels. [x]
   % is an equation. Returns the set of kernels contained in the right
   % hand side of [x] as a list.
   nconc(kernels numr w,kernels denr w) where w=simp cdr x;

procedure acfsf_getineq(f,bvl);
   % Algebraically closed field generate theory get inequalities. [f]
   % is a formula; [bvl] is a list of variables. Returns the list of
   % all inequalities occuring in [f] that do not contain any of the
   % variables in [bvl].
   begin scalar atml,atf,cdl;
      atml := cl_atml f;
      while atml do <<
         atf := caar atml;
         atml := cdr atml;
	 if acfsf_op atf eq 'neq and
	    null intersection(bvl, kernels acfsf_arg2l atf) then
	    cdl := atf . cdl
      >>;
      return cdl
   end;

procedure acfsf_structat(at,al);
   % Algebraically closed field structure of an atomic formula. [at]
   % is an atomic formula $R(t,0)$; [al] is an ALIST. Returns an
   % atomic formula. [al] is of the form $(..., (t_i . v_i), ...)$,
   % where the $t_i$ are SF's and the $v_i$ are variables. The left
   % hand side $t$ of [at] matches one of the $t_i$ in [al]. Returns
   % the atomic formula $R(v_i,0)$.
   begin scalar lhs;
      lhs := acfsf_arg2l at;
      if domainp lhs then
	 return at;
      return acfsf_0mk2(acfsf_op at, numr simp cdr assoc(lhs,al))
   end;

procedure acfsf_ifstructat(at,al);
   % Algebraically closed field irreducible factor structure of an
   % atomic formula. [at] is an atomic formula $R(t,0)$ where $t = c
   % ... s_j ...$ is a factorization of $t$ into irreducible factors;
   % [al] is an ALIST. Returns an atomic formula. [al] is of the form
   % $(..., (t_i . v_i), ...)$, where the $t_i$ are SF's and the $v_i$
   % are variables. Each factor $s_j$ of the left hand side $t$ of
   % [at] matches one of the $t_i$ in [al]. Returns the atomic formula
   % $R(c ... v_i ..., 0)$.
   begin scalar w,r;
      w := fctrf acfsf_arg2l at;
      r := car w;
      for each x in cdr w do
	 r := multf(r,expf(numr simp cdr assoc(car x,al),cdr x));
      return acfsf_0mk2(acfsf_op at,r)
   end;

procedure acfsf_termmlat(at);
   % Algebraically closed field term multiplicity list of atomic
   % formula. [at] is an atomic formula. Returns the multiplicity list
   % of all non-zero terms in [at].
   if acfsf_arg2l at then
      {(acfsf_arg2l at . 1)};

procedure acfsf_decdeg(f);
   % Algebraically closed field decrease degrees. [f] is a formula.
   % Returns a formula equivalent to [f], hopefully decreasing the
   % degrees of the bound variables.
   acfsf_decdeg0 cl_rename!-vars f;

procedure acfsf_decdeg0(f);
   begin scalar op,w,gamma,newmat;
      op := rl_op f;
      if rl_boolp op then
	 return rl_mkn(op,for each subfo in rl_argn f collect
   	    acfsf_decdeg0 subfo);
      if rl_quap op then
	 return rl_mkq(op,rl_var f,
	    car acfsf_decdeg1(acfsf_decdeg0 rl_mat f,{rl_var f}));
      % [f] is not complex.
      return f
   end;

procedure acfsf_decdeg1(f,vl);
   % Algebraically closed field decrease degrees. [f] is a formula;
   % [vl] is either a list of variables $v$ that do not occur boundly
   % in [f], or the identifier [fvarl]. Returns a pair $(\phi . l)$;
   % $l$ is a list of pairs $(..., (v_i . d_i), ...)$, with $v_i \in
   % [vl]$ and integer $d_i$; $\phi$ is obtained from [f] by replacing
   % powers $v_i^{d_i}$ by $v_i$. We have $\exists [vl] ([f])$
   % equivalent to $\exists [vl] (\phi)$. [fvarl] selects the list of
   % all free variables in [f] as [vl].
   begin scalar dvl; integer n;
      if vl eq 'fvarl then
	 vl := cl_fvarl1 f;
      for each v in vl do <<
	 n := acfsf_decdeg2(f,v);
	 if n>1 then <<
	    f := acfsf_decdeg3(f,v,n);
	    dvl := (v . n) . dvl
	 >>
      >>;
      return f . dvl
   end;

procedure acfsf_decdeg2(f,v);
   % Algebraically closed field standard form decrement degree
   % subroutine. [f] is a formula; [v] is a variable. Returns an
   % INTEGER $n$. The degree of [v] in [f] can be decremented using
   % the substitution $[v]^n=v$.
   begin scalar a,w,atl,dgcd,!*gcd,oddp;
      !*gcd := T;
      atl := cl_atl1 f;
      dgcd := 0;
      while atl and dgcd neq 1 do <<
	 a := car atl;
	 atl := cdr atl;
	 w := acfsf_ignshift(a,v);
	 if null w then <<  % [w neq 'ignore]
	    a := sfto_reorder(acfsf_arg2l a,v);
	    while (not domainp a) and (mvar a eq v) and dgcd neq 1 do <<
	       dgcd := gcdf(dgcd,ldeg a);
	       a := red a
	    >>
      	 >>
      >>;
      if dgcd = 0 then
	 return 1;
      return dgcd
   end;

procedure acfsf_ignshift(at,v);
   % Orderd field standard form ignore shift. [at] is an atomic
   % formula; [v] is a variable. Returns [nil] or ['ignore].
   begin scalar w;
      w := sfto_reorder(acfsf_arg2l at,v);
      if not domainp w and null red w and mvar w eq v then
	 return 'ignore
   end;

procedure acfsf_decdeg3(f,v,n);
   % Algebraically closed field standard form decrement degree. [f] is
   % a formula; [v] is a variable; [n] is an integer. Returns a
   % formula.
   cl_apply2ats1(f,'acfsf_decdegat,{v,n});

procedure acfsf_decdegat(atf,v,n);
   % Algebraically closed field standard form decrement degree atomic
   % formula. [f] is an atomic formula; [v] is a variable; [n] is an
   % integer. Returns an atomic formula.
   if acfsf_ignshift(atf,v) then
      atf
   else
      acfsf_0mk2(acfsf_op atf,sfto_decdegf(acfsf_arg2l atf,v,n));

procedure acfsf_multsurep(at,atl);
   % Algebraically closed field multplicative sure predicate. [at] is
   % an atomic formula; [atl] is a theory. Tries to prove [at] wrt.
   % [atl]. Returns non-[nil] in case of success.
   if acfsf_op at eq 'equal then
      acfsf_multsurep!-equal(at,atl)
   else
      acfsf_multsurep!-neq(at,atl);

procedure acfsf_multsurep!-equal(at,atl);
   begin scalar c,a;
      c := acfsf_arg2l at;
      while atl do <<
	 a := car atl;
	 atl := cdr atl;
	 if acfsf_op a eq 'equal and quotf(c,acfsf_arg2l a) then <<
	    a := 'found;
	    atl := nil
	 >>
      >>;
      return a eq 'found
   end;

procedure acfsf_multsurep!-neq(at,atl);
   begin scalar c,a;
      c := acfsf_arg2l at;
      while atl do <<
	 a := car atl;
	 atl := cdr atl;
	 if acfsf_op a eq 'neq and quotf(acfsf_arg2l a,c) then <<
	    a := 'found;
	    atl := nil
	 >>
      >>;
      return a eq 'found
   end;

endmodule;  % [acfsfmisc]

end;  % of file
