% ----------------------------------------------------------------------
% $Id: pasfbnf.red 1765 2012-10-11 14:58:03Z mkosta $
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
   fluid '(pasf_bnf_rcsid!* pasf_bnf_copyright!*);
   pasf_bnf_rcsid!* :=
      "$Id: pasfbnf.red 1765 2012-10-11 14:58:03Z mkosta $";
   pasf_bnf_copyright!* :=
      "Copyright (c) 1995-2009 A. Dolzmann, A. Seidl, T. Sturm"
>>;

module pasfbnf;
% Methods for DNF and CNF computation. For now pseudo- DNF and CNF are
% computed. A pseudo DNF (CNF) is a formula in PNF with matrix in DNF (CNF).

procedure pasf_sacat(a1,a2,gor);
   % Presburger arithmetic standard form subsume and cut atomic formula. [a1]
   % is an atomic formula; [a2] is an atomic formula; [gor] is one of 'or,
   % 'and. Returns for the first trivially nil.
   nil;

procedure pasf_dnf(phi);
   % Presburger arithmetic standard form disjunctive normal form. [phi] is a
   % quantifier free frmula. Returns a pseudo DNF of [phi].
   if pasf_puregconp(phi, 'and) then phi else pasf_pbnf(pasf_pnf phi,'dnf);

procedure pasf_cnf(phi);
   % Presburger arithmetic standard form conjunctive normal form. [phi] is a
   % quantifier free formula. Returns a pseudo DNF of [phi].
   if pasf_puregconp(phi, 'or) then phi else pasf_pbnf(pasf_pnf phi,'cnf);

procedure pasf_puregconp(f, gand);
   % Pure generic conjunction predicate. [f] is a quantifier-free formula.
   % Returns Boolean.
   begin scalar c,fl,a;
      if rl_tvalp f then return t;
      if pasf_atfp f then return t;
      if pairp f and rl_op f eq gand then <<
      	 fl := rl_argn f;
      	 c := t; while c and fl do <<
	    a := pop fl;
	    if not pasf_atfp a then c := nil
      	 >>;
      	 return c
      >>;
      return nil
   end;

procedure pasf_pbnf(phi,flag);
   % Presburger arithmetic standard form pseudo boolean normal form
   % computation. [phi] is a formula in PNF; [flag] is one of 'dnf or
   % 'cnf. Returns a pseudo boolean normal form of [phi] according to flag.
   begin
      if rl_bquap rl_op phi then
	 return rl_mkbq(rl_op phi,rl_var phi,rl_b phi,
	    pasf_pbnf(rl_mat phi,flag));
      if rl_quap rl_op phi then
	 return  rl_mkq(rl_op phi,rl_var phi,
	    pasf_pbnf(rl_mat phi,flag));
      % Now assuming that the formula is in PNF the formula is strong
      % quantifier free
      return if flag eq 'dnf then cl_dnf phi else cl_cnf phi
   end;

endmodule; % pasfbnf

end; % of file
