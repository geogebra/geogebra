% ----------------------------------------------------------------------
% $Id: dcfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(dcfsf_misc_rcsid!* dcfsf_misc_copyright!*);
   dcfsf_misc_rcsid!* :=
      "$Id: dcfsfmisc.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   dcfsf_misc_copyright!* := "Copyright (c) 2004-2009 A. Dolzmann and T. Sturm"
>>;

module dcfsfmisc;
% Differentially closed field standard form miscellaneous. Submodule
% of [dcfsf].

procedure dcfsf_termprint(u);
   % Differentialally closed field term print. [u] is a
   % term. The return value is not specified. Prints [u] AM-like.
   <<
      sqprint !*f2q u where !*nat=nil;
      ioto_prin2 nil
   >>;

procedure dcfsf_clnegrel(r,flg);
   % Differentialally closed field conditionally logically negate
   % relation. [r] is a relation. Returns for [flg] equal to [nil] a
   % relation $R$ such that for terms $t_1$, $t_2$ we have
   % $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$. Returns [r] for
   % non-[nil] [flg].
   if flg then r else dcfsf_lnegrel r;

procedure dcfsf_lnegrel(r);
   % Differentialally closed field logically negate relation. [r] is a
   % relation. Returns a relation $R$ such that for terms $t_1$, $t_2$
   % we have $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$.
   if r eq 'equal then 'neq
   else if r eq 'neq then 'equal
   else rederr {"dcfsf_lnegrel: unknown operator ",r};

procedure dcfsf_fctrat(atf);
   % Differentialally closed field factorize atomic formula. [atf] is an
   % atomic formula. Returns the factorized left hand side of [atf] as
   % a list $(...,(f_i . n_i),...)$, where the $f_i$ are the factors
   % as SF's and the $n_i$ are the corresponding multiplicities. The
   % integer content is dropped.
   cdr fctrf dcfsf_arg2l atf;

procedure dcfsf_negateat(f);
   % Differentialally closed field negate atomic formula. [f] is an
   % atomic formula. Returns an atomic formula equivalent to $\lnot
   % [f]$.
   dcfsf_mkn(dcfsf_lnegrel dcfsf_op f,dcfsf_argn f);

procedure dcfsf_varlat(atform);
   % Differentialally closed field variable list of atomic formula.
   % [atform] is an atomic formula. Returns the set of variables
   % contained in [atform] as a list.
   dcfsf_varlat1 kernels dcfsf_arg2l(atform);

procedure dcfsf_varlat1(kl);
   foreach k in kl join
      if pairp k and car k eq 'd then
	  {cadr k}
      else
	 {k};

procedure dcfsf_varsubstat(atf,new,old);
   % Differentialally closed substitute variable for variable in atomic
   % formula. [atf] is an atomic formula; [new] and [old] are
   % variables. Returns [atf] with [new] substituted for [old].
   dcfsf_0mk2(dcfsf_op atf,numr subf(dcfsf_arg2l atf,{old . new}));

procedure dcfsf_ordatp(a1,a2);
   % Differentialally closed field order predicate for atomic formulas.
   % [a1] and [a2] are atomic formulas. Returns [T] iff [a1] is
   % strictly less than [a2] wrt. some syntactical ordering; returns
   % [nil] else. The specification that [nil] is returned if
   % $[a1]=[a2]$ is used in [dcfsf_subsumeandcut].
   begin scalar lhs1,lhs2;
      lhs1 := dcfsf_arg2l a1;
      lhs2 := dcfsf_arg2l a2;
      if lhs1 neq lhs2 then return ordp(lhs1,lhs2);
      return dcfsf_ordrelp(dcfsf_op a1,dcfsf_op a2)
   end;

procedure dcfsf_ordrelp(r1,r2);
   % Differentialally closed field standard form relation order
   % predicate. [r1] and [r2] are dcfsf-relations. Returns a [T] iff
   % $[r1] <= [r2]$.
   r1 eq r2 or r1 eq 'equal;

procedure dcfsf_subalchk(al);
   % Differentialally closed field substitution ALIST check. [al] is
   % an ALIST $(..., (v_i . t_i), ...)$, where the $v_i$ are kernels,
   % and the $t_i$ are Lisp prefix terms. The return value is
   % unspecified. Raises an error if some $t_i$ contains a parametric
   % denominator.
   for each x in al do
      if not domainp denr simp cdr x then
	 rederr "parametric denominator in substituted term"
      else if not idp car x then
	 typerr({"expression ",car x},"variable") where !*nat=nil;

procedure dcfsf_eqnrhskernels(x);
   % Differentialally closed field equation right hand side kernels.
   % [x] is an equation. Returns the set of kernels contained in the
   % right hand side of [x] as a list.
   nconc(kernels numr w,kernels denr w) where w=simp cdr x;

procedure dcfsf_subat(al,f);
   % Differentialally closed field substitute into atomic formula.
   % [al] is an ALIST $(..., (v_i . t_i), ...)$, where the $v_i$ are
   % kernels, and the $t_i$ are Lisp prefix terms; [f] is an atomic
   % formula. Returns [f] with $t_i$ substituted for each occurrence
   % of $v_i$. The $t_i$ must be such that the substitution does not
   % yield parametric denominators.
   begin scalar nlhs;
      nlhs := subf(dcfsf_arg2l f,al);
      if not domainp denr nlhs then
	 rederr "parametric denominator after substitution";
      return dcfsf_0mk2(acfsf_op f,numr nlhs)
   end;

endmodule;  % [dcfsfmisc]

end;  % of file
