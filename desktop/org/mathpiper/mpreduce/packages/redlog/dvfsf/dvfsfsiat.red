% ----------------------------------------------------------------------
% $Id: dvfsfsiat.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(dvfsf_siat_rcsid!* dvfsf_siat_copyright!*);
   dvfsf_siat_rcsid!* :=
      "$Id: dvfsfsiat.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   dvfsf_siat_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module dvfsfsiat;
% Discretely valued field standard form simplify atomic formulas.
% Submodule of [dvfsf]. This submodule provides the black box
% [rl_simplat1] to [cl_simpl].

procedure dvfsf_simplat1(atf,sop);
   % Discretely valued field simplify atomic formula. [atf] is an
   % atomic formula; [sop] is the complex formula operator [atf]
   % occurs with or [nil]. Returns a quantifier-free formula that is a
   % simplified equivalent of [atf]. Accesses the fluid [dvfsf_p!*]
   % and the switches [rlsifac], [rlsiexpl], [rlsiexpla].
   begin scalar op;
      op := dvfsf_op atf;
      if op eq 'equal or op eq 'neq then
 	 return dvfsf_safield(op,dvfsf_arg2l atf,sop);
      return dvfsf_saval(op,dvfsf_arg2l atf,dvfsf_arg2r atf,sop)
   end;

procedure dvfsf_saval(op,lhs,rhs,sop);
   % Discretely valued field simplify atomic formula built with
   % valuation relation. [op] is one of ['assoc], ['nasso], ['div], or
   % ['sdiv]; [lhs] and [rhs] are SF's; [sop] is a boolean operator.
   % Returns a formula equivalent to $[op]([lhs],[rhs])$.
   begin
      if minusf lhs then lhs := negf lhs;
      if minusf rhs then rhs := negf rhs;
      if lhs = rhs then
 	 return if op eq 'sdiv or op eq 'nassoc then 'false else 'true;
      % At most one of [lhs], [rhs] is zero. The following third reatment
      % of zero sides is probably redundant.
      if null lhs then <<
    	 if op eq 'sdiv then return 'false;
	 if op eq 'nassoc then return dvfsf_safield('neq,rhs,sop);
    	 % We know [op] is one of ['div], ['assoc].
    	 return dvfsf_safield('equal,rhs,sop)
      >>;
      if null rhs then <<
    	 if op eq 'sdiv or op eq 'nassoc then
 	    return dvfsf_safield('neq,lhs,sop);
    	 if op eq 'assoc then
 	    return dvfsf_safield('equal,lhs,sop);
    	 % We know [op eq 'div] now.
    	 return 'true
      >>;
      return dvfsf_saval1(op,lhs,rhs)
   end;

procedure dvfsf_saval1(op,lhs,rhs);
   % Discretely valued field simplify atomic formula built with
   % valuation relation subroutine. [op] is one of [div], [sdiv],
   % [assoc], [nassoc]; [lhs] and [rhs] are nonzero SF's. Returns a
   % formula equivalent to $[op]([lhs],[rhs])$.
   begin scalar gcd,natf1,sff,junct;
      junct := if op eq 'sdiv or op eq 'nassoc then 'and else 'or;
      lhs := dvfsf_vsimpf lhs;
      rhs := dvfsf_vsimpf rhs;
      gcd := sfto_gcdf!*(lhs,rhs);
      lhs := quotf(lhs,gcd);
      rhs := quotf(rhs,gcd);
      natf1 := dvfsf_saval2(op,lhs,rhs);
      if junct eq 'and then <<
	 if natf1 eq 'false then return 'false;
    	 sff := dvfsf_safield('neq,gcd,'and);
      	 return dvfsf_compose('and,natf1,sff)
      >>;
      % We know [junct eq 'or].
      if natf1 eq 'true then return natf1;
      sff := dvfsf_safield('equal,gcd,'or);
      return dvfsf_compose('or,natf1,sff)
   end;

procedure dvfsf_compose(op,at,f);
   % Discretely valued field compose. [op] is either ['or] or ['and];
   % [at] is an atomic formula; [f] is a formula. Returns a formula
   % equivalent to $[op](at,f)$.
   if op eq 'or and (at eq 'true or f eq 'true) then
      'true
   else if op eq 'and and (at eq 'false or f eq 'false) then
      'false
   else if (at eq 'true) or (at eq 'false) then
      f
   else if (f eq 'true) or (f eq 'false) then
      at
   else if (op neq rl_op f) or not(cl_cxfp f) then
      rl_mk2(op,at,f)
   else
      rl_mkn(op,at . rl_argn f);

procedure dvfsf_saval2(op,lhs,rhs);
   % Discretely valued field simplify atomic formula built with
   % valuation relation subroutine. [op] is one of [div], [sdiv],
   % [assoc], [nassoc]; [lhs] and [rhs] are nonzero SF's such that
   % [lhs] and [rhs] are relatively prime. Returns a formula
   % equivalent to $[op]([lhs],[rhs])$.
   begin scalar natf1,w;
      if dvfsf_p!* > 0 then  % Concrete valuation given.
	 natf1 := dvfsf_sacval(op,lhs,rhs)
      else <<
	 w := dvfsf_saaval(op,lhs,rhs);
	 if rl_tvalp w then
	    natf1 := w
	 else if w neq 'failed then <<
	    natf1 := w;   % TODO: Repeat the trivial simplifications for [w].
	    op := dvfsf_op natf1;
	    lhs := dvfsf_arg2l natf1;
	    rhs := dvfsf_arg2r natf1
      	 >> else
	    natf1 := dvfsf_mk2(op,lhs,rhs)
      >>;
      if (op eq 'assor or op eq 'nassoc) and ordp(rhs,lhs) then
	 natf1 := dvfsf_mk2(op,rhs,lhs);
      return natf1
   end;

procedure dvfsf_sacval(op,lhs,rhs);
   % Discretely valued field simplify atomic formula built with
   % valuation relation for concrete given valuation. [op] is one of
   % [div], [sdiv], [assoc], [nassoc]; [lhs] and [rhs] are nonzero
   % SF's. Returns a formula equivalent to $[op]([lhs],[rhs])$.
   % Evaluate variable free atomic formulas and move the domain
   % content to one side.
   begin scalar lcont,rcont,lv,rv;
      if domainp lhs and domainp rhs then <<
	 if op eq 'assoc then
	    return if dvfsf_v lhs = dvfsf_v rhs then 'true else 'false;
	 if op eq 'nassoc then
	    return if dvfsf_v lhs neq dvfsf_v rhs then 'true else 'false;
	 if op eq 'sdiv then
	    return if dvfsf_v lhs < dvfsf_v rhs then 'true else 'false;
	 % We know [op eq 'div] now.
	 return if dvfsf_v lhs <= dvfsf_v rhs then 'true else 'false
      >>;
      % The content is non-zero.
      lcont := sfto_dcontentf lhs;
      lhs := quotf(lhs,lcont);
      rcont := sfto_dcontentf rhs;
      rhs := quotf(rhs,rcont);
      lv := dvfsf_v lcont;
      rv := dvfsf_v rcont;
      if lv >= rv then
 	 lhs := multf(numr simp ((dvfsf_p!*)**(lv-rv)),lhs)
      else
 	 rhs := multf(numr simp ((dvfsf_p!*)**(rv-lv)),rhs);
      return dvfsf_mk2(op,lhs,rhs)
   end;

procedure dvfsf_safield(op,lhs,sop);
   % Discretely valued field simplify atomic formula with field
   % relation. [op] is either ['equal] or ['neq]; [lhs] is an SF;
   % [sop] is a boolean operator.
   % Returns a quantifier-free formula equivalent to $[op]([lhs],0)$.
   begin
      lhs := dvfsf_vsimpf lhs;
      if domainp lhs then
	 if op eq 'equal then
 	    return if null lhs then 'true else 'false
	 else  % [op eq 'neq]
	    return if null lhs then 'false else 'true;
      lhs := sfto_dprpartf lhs;
      if dvfsf_p!*>0 then
 	 return dvfsf_sapfacf(op,lhs,sop);
      lhs := dvfsf_dppower lhs;
      if domainp lhs then
	 return if op eq 'equal then 'false else 'true;
      return dvfsf_sapfacf(op,lhs,sop)
   end;

procedure dvfsf_sapfacf(op,lhs,sop);
   % Discretely valued field standard form polynomial factorization
   % atomic formula with field relation. [op] is either ['equal] or
   % [neq]. [lhs] is an SF. Returns a formula equivalent to
   % $[op](lhs,0)$; [sop] is a boolean operator. This procedure
   % possibly factorize [lhs] to explode the respective atomic
   % formula.
   begin scalar w,junct;
      junct := if op eq 'equal then 'or else 'and;
      if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and sop = junct) then
      	 return rl_smkn(junct,
	    for each x in cdr fctrf lhs collect dvfsf_sapeq(op,car x));
      return dvfsf_sapeq(op,lhs)
   end;

procedure dvfsf_dppower(f);
   % Discretely valued field drop p power. [f] is an SF of the form
   % $(p^n f')$. Returns $f'$ as an SF.
   begin scalar qr,p;
      p := numr simp 'p;
      qr := qremf(f,p);
      while null(cdr qr) do <<
	 f := car qr;
 	 qr := qremf(car qr,p);
      >>;
      return f
   end;

procedure dvfsf_saaval(op,lhs,rhs);
   % Discretely valued field simplify atomic formula for abstract
   % valuation. [op] is one of [div], [sdiv], [assoc], [nassoc]; [lhs]
   % and [rhs] are SF's. Returns ['failed] or an atomic formula
   % equivalent $[op]([lhs],[rhs])$.
   begin scalar w;
      w := dvfsf_sappow(op,lhs,rhs);
      if w neq 'failed then
	 return w;
      w := dvfsf_savpc(op,lhs,rhs);
      if w neq 'failed then
	 return w;
      w := dvfsf_sapureintc(op,lhs,rhs);
      if w neq 'failed then
	 return w;
      w := dvfsf_sapfactor(op,lhs,rhs);
      if w neq 'failed then
	 return w;
      return 'failed
   end;

procedure dvfsf_sappow(op,lhs,rhs);
   % Discretely valued field simplify powers of p in atomic formula.
   % [op] is one of ['div], ['sdiv], ['nassoc], ['assoc]; [lhs] and
   % [rhs] are SF's. Returns ['failed] or a truth value. Simplifies
   % atomic formulas which relates 1 to $z p^n$, for an integer [z]
   % and a positive integer $n$ to a truth value. Otherwise ['failed]
   % is returned.
   begin scalar lp,rp;
      lp := dvfsf_ppowerp lhs;
      rp := dvfsf_ppowerp rhs;
      if rp and lhs = numr simp 1 then
    	 return if op = 'assoc then 'false else 'true;
      if lp and rhs = numr simp 1 then
    	 return if op = 'nassoc then 'true else 'false;
      return 'failed
   end;

procedure dvfsf_savpc(op,lhs,rhs);
   % Discretely valued field simplify atomic formulas build with
   % valuation relation, p, and a constant. [op] is one of [div],
   % [sdiv], [assoc], [nassoc]; [lhs] and [rhs] are SF's, such that
   % [lhs] and [rhs] are relatively prime. Returns ['failed] or an
   % atomic formula equivalent to $[op]([lhs],[rhs])$. The atomic
   % formula is simplified, if it relates $z p^n$ to a constant;
   % otherwise ['failed] is returned.
   %
   % WARNING: This simplifier is correct only for p-adic valued fields.
   %
   begin scalar w,op;
      if not !*rlsifac then
	 return 'failed;
      w := dvfsf_savpc1(op,lhs,rhs);
      if w eq 'failed then
	 return 'failed;
      op := dvfsf_op w;
      if dvfsf_arg2l w = dvfsf_arg2r w then
 	 return if op eq 'nassoc then 'false else 'true;
      return w
   end;

procedure dvfsf_savpc1(op,lhs,rhs);
   % Discretely valued field simplify atomic formulas build with
   % valuation relation, p, and a constant subroutine. [op] is one of
   % [div], [sdiv], [assoc], [nassoc]; [lhs] and [rhs] are SF's, such
   % that [lhs] and [rhs] are relatively prime. Returns ['failed] or
   % an atomic formula equivalent to $[op]([lhs],[rhs])$. The atomic
   % formula is simplified, if it relates $z p^n$ to a constant;
   % otherwise ['failed] is returned.
   begin scalar n;
      return if (domainp rhs) and (n := dvfsf_ppowerp lhs) then <<
      	 if op eq 'assoc then
      	    dvfsf_mk2('nassoc,sfto_zdeqn(rhs,n),numr simp 1)
      	 else if op eq 'nassoc then
      	    dvfsf_mk2('assoc,sfto_zdeqn(rhs,n),numr simp 1)
      	 else if op eq 'div then
      	    dvfsf_mk2('nassoc,sfto_zdgen(rhs,n),numr simp 1)
      	 else if op eq 'sdiv then
      	    dvfsf_mk2('nassoc,sfto_zdgtn(rhs,n),numr simp 1)
      	 else
	    rederr "Bug in dvfsf_savpc"
      >> else if (domainp lhs) and (n := dvfsf_ppowerp rhs) then <<
      	 if op eq 'assoc then
      	    dvfsf_mk2('nassoc,sfto_zdeqn(lhs,n),numr simp 1)
      	 else if op eq 'nassoc then
      	    dvfsf_mk2('assoc,sfto_zdeqn(lhs,n),numr simp 1)
      	 else if op eq 'div then
      	    dvfsf_mk2('assoc,sfto_zdgtn(lhs,n),numr simp 1)
      	 else if op eq 'sdiv then
      	    dvfsf_mk2('assoc,sfto_zdgen(lhs,n),numr simp 1)
      	 else
	    rederr "Bug in dvfsf_savpc"
      >> else
      	 'failed
   end;

procedure dvfsf_ppowerp(u);
   % Discretely valued field power of p predicate. [u] is an SF.
   % Returns [nil] or a positive integer $n$. Tests [u] on
   % representing a polynomial $z p^n$ for an integer $z$ and a
   % natural number $n$.
   begin scalar rou,w;
      rou := sfto_reorder(u,'p);
      w := (not domainp rou and mvar rou = 'p and
     	 domainp lc rou and null red rou);
      if w then
	 return ldeg u
   end;

procedure dvfsf_ppolyp(f);
   % Discretely valued field p polynomial predicate. [f] is an SF.
   % Returns [T] if [f] is a domian element or is a polynomial
   % containing only the variable ['p] otherwise [nil] is returned.
   begin scalar w;
      if domainp f then
	 return T;
      w := kernels f;
      return null cdr w and car w eq 'p
   end;

procedure dvfsf_ppolydec(f);
   % Discretely valued field p polynomial decomposition. [f] is an SF.
   % Returns a list $(...,(a_i,n_i),...)$ such that $[f]=\sum_i
   % a_ip^n_i$ and $n_i>n_{i+1}$.
   if null f then
      nil
   else if domainp f then
      {(f . 0)}
   else
      (lc f . ldeg f) . dvfsf_ppolydec red f;

procedure dvfsf_vsimpf(f);
   % Discretely valued field valuation simplification standard form.
   % [f] is an SF. Returns an SF $f'$, such that for all terms $g$ we
   % have $[f] \mathrel{\varrho} g$ if and only if $f'
   % \mathrel{\varrho} g$.
   begin scalar w,fdc,cep,c,n,cdc;
      if domainp f then
	 return f;
      if not dvfsf_ppolyp f then
	 return f;
      fdc := reversip dvfsf_ppolydec(f);
      w := car fdc;
      fdc := cdr fdc;
      if null fdc then
	 return f;
      n := cdr w;
      c := car w;
      if (c = 1) or (c = -1) then
	 return numr simp {'expt,'p,n};
      cdc := sort(zfactor c,function(lambda(x,y); (cdr x > cdr y)));
      if n + cdr car cdc < cdr car fdc then
	 return numr simp {'times,c,{'expt,'p,n}};
      while cdc do <<
	 cep := car cdc;
	 w := dvfsf_vsimpf1(car cep,cdr cep,n,fdc);
	 if w eq 'failed then
	    cdc := nil
	 else
	    cdc := cdr cdc;
      >>;
      if w eq 'failed then
	 return f;
      return numr simp {'times,c,{'expt,'p,n}};
   end;

procedure dvfsf_vsimpf1(q,m,n,fdc);
   % Discretely valued field valuation simplification standard form
   % subroutine. [f] is an SF. Returns an SF $f'$, such that for all
   % terms $g$ we have $[f] \mathrel{\varrho} g$ if and only if $f'
   % \mathrel{\varrho} g$.
   begin scalar w,k,qp;
      while fdc do <<
	 w := car fdc;  % a pair $(a . n)$ with $a*p^n$ is monomial of [f]
	 k := cdr w - n;
	 if m<k then <<
	    w := nil;
	    fdc := nil
	 >> else <<
	    qp := abs(q)^(m-k+1);
	    if gcdf!*(qp,car w) = qp then
	       fdc := cdr fdc
	    else <<
	       w := 'failed;
	       fdc := nil;
	    >>
      	 >>
      >>;
      if w eq 'failed then
	 return 'failed
   end;

procedure dvfsf_sapureintc(op,lhs,rhs);
   % Discretely valued field simplifiy atomic formulas pure integer
   % constraints. [op] is one of ['assoc], ['nassoc], ['div] or
   % ['sdiv]; [lhs] and [rhs] are SF's. Returns ['failed] or an atomic
   % formula. This procedure simplifies atomic formulas which relates
   % integers.
   if not !*rlsifac then
      'failed
   else if not(domainp lhs and domainp rhs) then
      'failed
   else if op eq 'assoc or op eq 'nassoc then
      dvfsf_mk2(op,sfto_sqfpartz(lhs*rhs),numr simp 1)
   else if op eq 'div then
      dvfsf_mk2('assoc,sfto_sqfpartz lhs,numr simp 1)
   else if op eq 'sdiv then
      dvfsf_mk2('nassoc,sfto_sqfpartz rhs,numr simp 1)
   else
      rederr "bug dvfsf_sapureintc";

procedure dvfsf_sapeq(op,lhs);
   % Discretely valued field simplify atomic formula p equation. [op]
   % is either ['equal] or ['neq]. [lhs] is an SF. Returns an atomic
   % formula equivalent to $[op]([lhs],0)$. This procedures
   % simplifies atomic formulas of the form $[op](z1 p + z2,0)$, with
   % $z1$ and $z2$ relatively prime.
   if not(not(domainp lhs) and mvar lhs eq 'p and ldeg lhs = 1
      and domainp lc lhs and domainp red lhs)
   then
      dvfsf_mk2(op,lhs,nil)
   else if (lc lhs neq 1) or not(minusf red lhs) or not(primep red lhs) then
      if op eq 'equal then 'false else 'true
   else
      dvfsf_mk2(if op eq 'equal then 'nassoc else 'assoc,
	 negf red lhs,numr simp 1);

procedure dvfsf_sapfactor(op,lhs,rhs);
   % Discretely valued field simplify atomic formula p factor. [op] is
   % one of ['assoc], ['nassoc], ['div], or ['sdiv]. [lhs] and [rhs]
   % are SF's. Returns ['failed] or atomic formula equivalent to
   % $[op]([lhs],[rhs])$. This procedures simplifies atomic formulas
   % of the form $f || p*g$, $p*f | g$.
   begin scalar w;
      if op eq 'sdiv then <<
      	 w := qremf(rhs,numr simp 'p);
      	 if null cdr w then
	    return dvfsf_mk2('div,lhs,car w);
	 return 'failed
      >>;
      if op eq 'div then <<
      	 w := qremf(lhs,numr simp 'p);
      	 if null cdr w then
	    return dvfsf_mk2('sdiv,car w,rhs);
	 return 'failed
      >>;
      return 'failed
   end;

endmodule;  % [dvfsfsiat]

end;  % of file
