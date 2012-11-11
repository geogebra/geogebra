% ----------------------------------------------------------------------
% $Id: pasfsiat.red 1815 2012-11-02 13:20:27Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2002-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(pasf_siat_rcsid!* pasf_siat_copyright!*);
   pasf_siat_rcsid!* :=
      "$Id: pasfsiat.red 1815 2012-11-02 13:20:27Z thomas-sturm $";
   pasf_siat_copyright!* :=
      "Copyright (c) 2002-2009 A. Dolzmann, A. Seidl, and T. Sturm"
>>;

module pasfsiat;
% Presburger arithmetic standard form atomic formula simplification. Submodule
% of PASF.

procedure pasf_simplat1(atf,sop);
   % Presburger arithmetic standard form simplify atomic formula. [atf] is an
   % atomic formula; [sop] is the boolean operator [atf] occurs with or
   % nil. Returns a quantifier-free formula that is a simplified equivalent of
   % [atf].
   begin
      % Conversion to normal form (NF) and evaluation of variable free atomic
      % formulas
      atf := pasf_vf pasf_dt pasf_mkpos pasf_zcong atf;
      if rl_tvalp atf then return atf;
      % Congruences are treated differently as non-congruences
      if pasf_congp atf then
	 % Total modulo reduction possible; content elimination for
	 % congruences (CEcong)
	 atf := pasf_cecong pasf_vf pasf_mr atf
      else (if pasf_opn atf memq '(equal neq) then
	 atf := pasf_ceeq atf
      else
	 atf := pasf_cein atf);
      % Checking if done yet
      if rl_tvalp atf then return atf;
      % Advanced simplification
      atf := if pasf_opn atf memq '(cong ncong) then
	 % Solvability of congruences (SECong)
	 pasf_sc atf
      else if pasf_opn atf memq '(equal neq) then
	 % Solvability of diophantine (in-)equations (SE-Rule)
	 pasf_se atf
      else
	 % Order relation reduction
	 pasf_or atf;
      if not !*rlsifac then
	 return atf;
      % Factorization check
      return pasf_fact atf;
   end;

procedure pasf_zcong(atf);
   % Presburger arithmetic standard form zero congruences. [atf] is an atomic
   % formula. Returns an equality if modulus of the congruence is zero.
   if pasf_congp atf then (
      if null pasf_m atf then
      	 pasf_0mk2(if pasf_opn atf eq 'cong then 'equal else 'neq,
	    pasf_arg2l atf)
      else if null pasf_arg2l atf and pasf_opn atf eq 'cong then 'true
      else if null pasf_arg2l atf and pasf_opn atf eq 'ncong then 'false
      else atf)
   else
      atf;

procedure pasf_mkpos(atf);
   % Presburger arithmetic standard form make atomic formula positive. [atf]
   % is an atomic formula. Returns an equivalent atomic formula with a
   % positive leading coefficient.
   begin scalar res;
      % Left handside
      res := if not(rl_tvalp atf) and minusf pasf_arg2l atf then
      	 pasf_anegateat atf
      else
	 atf;
      % Congruences with negative modulus
      if pasf_congp res and minusf pasf_m res then
	 res := pasf_0mk2(((pasf_opn res) . (negf pasf_m res)),pasf_arg2l res);
      return res
   end;

procedure pasf_vf(atf);
   % Presburger arithmetic standard form evaluation of variable free atomic
   % formulas. [atf] is an atomic formula. Returns [atf] if it is not
   % variable-free or a truth value.
   begin
      if (not(rl_tvalp atf) and domainp pasf_arg2l atf) then <<
	 % Parametric modulus
	 if pasf_congp atf and null domainp pasf_m atf then
	    if null pasf_arg2l atf then
	       return 'false
	    else
	       return atf;
      	 return if pasf_evalatp(pasf_op atf,pasf_arg2l atf) then
   	    'true
      	 else
   	    'false
      >>;
      return atf
   end;

procedure pasf_dt(atf);
   % Presburger arithmetic standard form evaluation of definite terms. [atf]
   % is an atomic formula. Returns [atf] if no simplification is possible or a
   % truth value.
   begin scalar pdp,opn;
      if rl_tvalp atf then return atf;
      pdp := pasf_pdp pasf_arg2l atf;
      opn := pasf_opn atf;
      % Positive and negative definite terms
      if pdp eq 'pdef and opn memq '(equal lessp leq) then return 'false;
      if pdp eq 'ndef and opn memq '(equal greaterp geq) then return 'false;
      if pdp eq 'pdef and opn memq '(neq greaterp geq) then return 'true;
      if pdp eq 'ndef and opn memq '(neq lessp leq) then return 'true;
      % Positive and negative semidefinite terms
      if pdp eq 'psdef and opn eq 'lessp then return 'false;
      if pdp eq 'nsdef and opn eq 'greaterp then return 'false;
      if pdp eq 'psdef and opn eq 'geq then return 'true;
      if pdp eq 'nsdef and opn eq 'leq then return 'true;
      if pdp eq 'psdef and opn eq 'neq then return
	 pasf_0mk2('greaterp,pasf_arg2l atf);
      if pdp eq 'nsdef and opn eq 'neq then return
	 pasf_0mk2('lessp,pasf_arg2l atf);
      return atf
   end;

procedure pasf_mr(atf);
   % Presburger arithmetic standard form modulo reduction. [atf] is an atomic
   % formula. Returns a modulo free formula equivalent to [atf]. For
   % non-congruences nothing can be done.
   if not rl_tvalp atf and pasf_congp atf and domainp pasf_m atf then
      pasf_0mk2(pasf_op atf,pasf_premf(pasf_arg2l atf,pasf_m atf))
   else
      % For non-congruences nothing can be done
      atf;

procedure pasf_premf(f,m);
   % Positive remainder.
   pasf_premf1(remf(f,m),m);

procedure pasf_premf1(r,m);
   begin scalar c,v,d,rr;
      if domainp r then
	 return if minusf r then addf(r,m) else r;
      c := pasf_premf1(lc r,m);
      v := !*k2f mvar r;
      d := ldeg r;
      rr := pasf_premf1(red r,m);
      return addf(multf(c,exptf(v,d)),rr)
   end;

procedure pasf_ceeq(atf);
   % Presburger arithmetic standard form content elimination (CE) for
   % equalities. [atf] is an atomic formula. Returns an equivalent atomic
   % formula.
   begin scalar g;
      % Nothing to do for non-equalities
      if rl_tvalp atf or not(pasf_opn atf memq '(equal neq)) then
	 return atf;
      % Computing the domain valued content of the coefficients
      g := sfto_dcontentf pasf_arg2l atf;
      return pasf_0mk2(pasf_op atf,quotfx(pasf_arg2l atf, numr simp g))
   end;

procedure pasf_cein(atf);
   % Presburger arithmetic standard form content elimination (CE) for
   % non-equalities. [atf] is an atomic formula. Returns an equivalent atomic
   % formula.
   begin scalar g,decp;
      if rl_tvalp atf or not(pasf_opn atf memq '(leq greaterp geq lessp)) then
	 return atf;
      % Computing the content of the parametric part
      decp := pasf_deci pasf_arg2l atf;
      g := sfto_dcontentf car decp;
      return pasf_0mk2(pasf_op atf,
	 addf(quotfx(car decp,numr simp g),
	    if pasf_opn atf memq '(leq greaterp) then
	       negf pasf_floor(-(cdr decp),g)
	    else if pasf_opn atf memq '(geq lessp) then
	       negf pasf_ceil(-(cdr decp),g)))
   end;

procedure pasf_cecong(atf);
   % Presburger arithmetic standard form content elimination (CE) for
   % congruences. [atf] is an atomic formula. Returns equivalent atomic
   % formula.
   begin scalar inv,m,g;
      % For non-congruences nothing to do
      if rl_tvalp atf or not pasf_congp atf then
	    return atf;
      m := pasf_m atf;
      g := gcdf(m,sfto_dcontentf pasf_arg2l atf);
      atf := pasf_0mk2(pasf_mkop(pasf_opn atf,quotfx(m,numr simp g)),
	 quotfx(pasf_arg2l atf,numr simp g));
      m := pasf_m atf;
      g := sfto_dcontentf pasf_arg2l atf;
      inv := domainp m and gcdf(m,g) = 1;
      % Check if the content has an inverse
      return if inv then
	 % Division is always possible
      	 pasf_0mk2(pasf_op atf,quotfx(pasf_arg2l atf,numr simp g))
      else
	 atf
   end;

procedure pasf_se(atf);
   % Presburger arithmetic standard form (un-)solvability check for
   % (in-)equalities. [atf] is an atomic formula. Returns a truth value or
   % [atf].
   begin scalar decp,g;
      % For non-equalities nothing to do
      if rl_tvalp atf or not(pasf_opn atf memq '(neq equal)) then
	 return atf;
      % Computing the content
      decp := pasf_deci pasf_arg2l atf;
      g := sfto_dcontentf car decp;
      if remainder(cdr decp,g) neq 0 and pasf_opn atf eq 'neq then
	 return 'true;
      if remainder(cdr decp,g) neq 0 and pasf_opn atf eq 'equal then
	 return 'false;
      return atf
   end;

procedure pasf_or(atf);
   % Presburger arithmetic standard form order relation reduction. [atf] is an
   % atomic formula. Returns equivalent atomic formula.
   begin scalar decp;
      % For non orderings nothing to do
      if rl_tvalp atf or not(pasf_opn atf memq '(lessp greaterp leq geq)) then
	 return atf;
      % Decomposing the atomic formula
      decp := pasf_deci pasf_arg2l atf;
      if pasf_opn atf eq 'lessp and cdr decp < 0 then
	 return pasf_0mk2('leq, addf(pasf_arg2l atf, numr simp 1));
      if pasf_opn atf eq 'leq and cdr decp > 0 then
	 return pasf_0mk2('lessp, addf(pasf_arg2l atf, negf numr simp 1));
      if pasf_opn atf eq 'greaterp and cdr decp > 0 then
	 return pasf_0mk2('geq, addf(pasf_arg2l atf, negf numr simp 1));
      if pasf_opn atf eq 'geq and cdr decp < 0 then
	 return pasf_0mk2('greaterp, addf(pasf_arg2l atf, numr simp 1));
      return atf
   end;

procedure pasf_sc(atf);
   % Presburger arithmetic standard form (un-)solvability check for
   % (in-)congruences. [atf] is an atomic formula. Returns a truth value or
   % [atf].
   begin scalar g,res,m,decp;
      % For noncongruences nothing to do
      if rl_tvalp atf or not(pasf_opn atf memq '(cong ncong)) or
      	 % For congruences with non-domainvalued modulus nothing is done yet
	 null domainp pasf_m atf then
	    return atf;
      % Decomposing the formula
      decp := pasf_deci pasf_arg2l atf;
      % Computing the content
      g := sfto_dcontentf car decp;
      m := pasf_m atf;
      % Verbose check for simplification
      res := t;
      for j := 0 : m do
	 res := res and (remainder(cdr decp + j*g,m) neq 0);
      if res and pasf_opn atf eq 'cong then
	 return 'false;
      if res and pasf_opn atf eq 'ncong then
	 return 'true;
      return atf
   end;

procedure pasf_evalatp(rel,lhs);
   % Presburger arithmetic standard form evaluate atomic formula. [rel] is a
   % relation; [lhs] is a domain element. Returns a truth value equivalent to
   % $[rel]([lhs],0)$.
   if pairp rel and car rel memq '(cong ncong) then
      % Only congruences with nonparametric modulus are allowed
      (if domainp cdr rel then pasf_evalatpm(car rel,lhs,cdr rel)
      else rederr{"pasf_evalatp : parametric modulus in input"})
   else
      pasf_evalatpm(rel,lhs,nil);

procedure pasf_evalatpm(rel,lhs,m);
   % Presburger arithmetic standard form evaluate atomic formula
   % subroutine. [rel] is a relation; [lhs] is a domain element; [m] is an
   % optional modulus. Returns a truth value equivalent to $[rel]([lhs],0)$.
   if rel eq 'equal then null lhs or lhs = 0
   else if rel eq 'neq then not (null lhs or lhs = 0)
   else if rel eq 'leq then minusf lhs or (null lhs or lhs = 0)
   else if rel eq 'geq then not minusf lhs
   else if rel eq 'lessp then minusf lhs
   else if rel eq 'greaterp then not (minusf lhs or null lhs or lhs = 0)
   else if rel eq 'cong then
      (null lhs or lhs = 0) or 0 = remainder(lhs,m)
   else if rel eq 'ncong then
      not ((null lhs or lhs = 0) or 0 = remainder(lhs,m))
   else rederr {"pasf_evalatp: unknown operator",rel};


procedure pasf_fact(atf);
   % Presburger arithmetic standard form factorization of atomic formulas.
   % [atf] is an atomic formula. Returns atf if no factorization can be done
   % and an equivalent quantifier-free formula else.
   begin scalar fac,op,m;
      if rl_tvalp atf then
	 return atf;
      op := pasf_op atf;
      fac := fctrf pasf_arg2l atf;
      if length fac < 3 then
	 return atf;
      if op memq '(equal neq) then
	 return rl_mkn(if op eq 'equal then 'or else 'and,
	    for each fct in cdr fac collect
	       pasf_0mk2(op,car fct));
      if op memq '(leq lessp geq greaterp) then
	 return pasf_fact1(cdr fac,
	    if minusf car fac then pasf_anegrel op else op);
      return atf;
   end;

procedure pasf_fact1(fac,op);
   % Presburger arithmetic standard form factorization of atomic formulas
   % subprocedure. [fac] is a factorization of an atomic formula; [op] is the
   % operator. Returns an equivalent formula to $\prod_i fac(i) op 0$.
   if null cdr fac then
      pasf_0mk2(op,caar fac)
   else if remainder(cdar fac,2) neq 0 then
      rl_mkn('or,{
	 rl_mkn('and,{pasf_0mk2(op,caar fac),
	    if op memq '(geq greaterp) then
	       pasf_fact1(cdr fac,op)
	    else
	       pasf_fact1(cdr fac,pasf_anegrel op)}),
	 rl_mkn('and,{pasf_0mk2(pasf_anegrel op,caar fac),
	    if op memq '(geq greaterp) then
	       pasf_fact1(cdr fac,pasf_anegrel op)
	    else
	       pasf_fact1(cdr fac,op)})})
   else
      pasf_fact1(cdr fac,op);

endmodule; % [pasfsiat]

end; % of file
