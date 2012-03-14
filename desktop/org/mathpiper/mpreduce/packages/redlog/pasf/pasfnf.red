% ----------------------------------------------------------------------
% $Id: pasfnf.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2001-2009 A. Dolzmann, A. Lasaruk, A. Seidl, T. Sturm
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
   fluid '(pasf_nf_rcsid!* pasf_nf_copyright!*);
   pasf_nf_rcsid!* :=
      "$Id: pasfnf.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   pasf_nf_copyright!* :=
      "Copyright (c) 1995-2009 A. Dolzmann, A. Lasaruk, A. Seidl, and T. Sturm"
>>;

module pasfnf;
% PASF normal forms. Submodule of PASF. This module provides for now only the
% prenex normal form algorithmus. Possibly should be merged with pasfbnf
% module in the future.

procedure pasf_pnf(phi);
   % Presburger arithmetic standard form prenex normal form. [phi] is a
   % formula. Returns a prenex formula equivalent to [phi].
   pasf_pnf1 rl_nnf phi;

procedure pasf_pnf1(phi);
   % Presburger arithmetic standard form prenex normal form subroutine. [phi]
   % is a positive formula that does not contain any extended boolean
   % operator. Returns a prenex formula equivalent to [phi].
   <<
      if null cdr erg or pasf_qb car erg < pasf_qb cadr erg then
 	 car erg
      else
 	 cadr erg
   >> where erg=pasf_pnf2(cl_rename!-vars phi);

procedure pasf_pnf2(phi);
   % Presburger arithmetic standard form prenex normal form subroutine. [phi]
   % is a positive formula that does not contain any extended boolean
   % operator. Returns a list or prenex formulas equivalent to [phi].
   begin scalar op;
      op := rl_op phi;
      if rl_quap op or rl_bquap op then
 	 return pasf_pnf2!-quantifier(phi);
      if rl_junctp op then
 	 return pasf_pnf2!-junctor(phi);
      if rl_tvalp op then
 	 return {phi};
      if rl_cxp op then
 	 rederr{"pasf_pnf2():",op,"invalid as operator"};
      return {phi}
   end;

procedure pasf_pnf2!-quantifier(phi);
   % Presburger arithmetic standard form prenex normal form subroutine. [phi]
   % is a positive formula that does not contain any extended boolean
   % operator. Returns a list or prenex formulas equivalent to [phi].
   begin scalar pnfmat,tp;
      pnfmat := pasf_pnf2 rl_mat phi;
      % Bounded quantifiers are treated as normal quantifiers
      return if (null cdr pnfmat) or 
	 ((rl_op phi memq '(all ball) and rl_op car pnfmat memq '(all ball)) or
	    (rl_op phi memq '(ex bex) and rl_op car pnfmat memq '(ex bex))) 
      then
	 (if rl_bquap rl_op phi then
	    {rl_mkbq(rl_op phi,rl_var phi,rl_pnf rl_b phi,car pnfmat)}
	 else
       	    {rl_mkq(rl_op phi,rl_var phi,car pnfmat)}) 
      else
	 (if rl_bquap rl_op phi then
       	    {rl_mkbq(rl_op phi,rl_var phi,rl_pnf rl_b phi,cadr pnfmat)}
	 else
	    {rl_mkq(rl_op phi,rl_var phi,cadr pnfmat)});
   end;

procedure pasf_pnf2!-junctor(phi);
   % Presburger arithmetic standard form prenex normal form subroutine. [phi]
   % is a positive formula that does not contain any extended boolean
   % operator. Returns a list or prenex formulas equivalent to [phi].
   begin scalar args,junctor,e,l1,l2,onlyex,onlyall,phi1,phi2;
      integer m,qb;
      junctor := rl_op phi;
      args := rl_argn phi;
      % Preparing the PNF of arguments
      e := for each f in args collect pasf_pnf2(f);
      onlyex := T; onlyall := T;
      for each ej in e do <<
    	 qb := pasf_qb car ej;
    	 if qb > m then <<
 	    m := qb; onlyex := T; onlyall := T
 	 >>;
    	 if cdr ej then <<
 	    l1 := (car ej) . l1;
 	    l2 := (cadr ej) . l2
 	 >> else <<
 	    l1 := (car ej) . l1;
 	    l2 := (car ej) . l2
 	 >>;
	 % Bounded quantifiers are treated as normal quantifiers
    	 if eqn(m,qb) then <<
      	    if rl_op car l1 eq 'all  or rl_op car l1 eq 'ball then
	       onlyex := nil;
      	    if rl_op car l2 eq 'ex or rl_op car l1 eq 'bex then 
	       onlyall := nil
    	 >>
      >>;
      l1 := reversip l1;
      l2 := reversip l2;
      if eqn(m,0) then return {phi};
      if onlyex neq onlyall then
    	 if onlyex then
 	    return {pasf_interchange(l1,junctor,'ex)}
    	 else  % [onlyall]
 	    return {pasf_interchange(l2,junctor,'all)};
      phi1 := pasf_interchange(l1,junctor,'ex);
      phi2 := pasf_interchange(l2,junctor,'all);
      if car phi1 eq car phi2 then
 	 return {phi1}
      else
 	 return {phi1,phi2}
   end;

procedure pasf_qb(phi);
   % Presburger arithmetic standard form quantifier block count. [phi] is a
   % positive formula that does not contain any extended boolean
   % operator. Returns the amount of quantifier blocks in phi. Note that the
   % procedure returns the amount of universal or existential blocks without
   % performing a distinction between normal and bounded quantifiers.
   begin scalar q,tp; integer qb;
      while (rl_quap rl_op phi or rl_bquap rl_op phi) do <<
	 tp := if rl_op phi memq '(ball all) then 'all else 'ex;	       
    	 if tp neq q then <<
      	    qb := qb + 1;
      	    q := if rl_op phi memq '(ball all) then 'all else 'ex
    	 >>;
    	 phi := rl_mat phi
      >>;
      return qb
   end;

procedure pasf_interchange(l,junctor,a);
   % Presburger arithmetic standard form interchange. [l] list of argument
   % formulas; [junctor] is the junction type; [a] is the quantifier. Returns
   % a formula, where the quantifiers are interchanged with the junctor.
   begin scalar ql,b,result;
      while pasf_contains!-quantifier(l) do <<
    	 l := for each f in l collect <<
      	    while (a eq 'all and rl_op f memq '(ball all) or
 	       a eq 'ex and rl_op f memq '(bex ex)) do <<
	       % The list contains operator, variable and bound if there is
	       % one and nil in other case
               b := {rl_op f,rl_var f,if rl_bquap rl_op f then 
		  rl_b f else nil} . b;
               f := rl_mat f
      	    >>;
      	    f
    	 >>;
    	 ql := b . ql;
    	 b := nil;
    	 a := cl_flip a
      >>;
      result := rl_mkn(junctor,l);
      for each b in ql do <<
    	 for each v in b do 
	    if null caddr v then 
	       result := rl_mkq(car v,cadr v,result)
	    else
	       result := rl_mkbq(car v,cadr v,caddr v,result)
      >>;
      return result
   end;

procedure pasf_contains!-quantifier(l);
   % Presburger arithmetic standard form containing quantifier test. [l] is a
   % list of positive formulas that do not contain any extended boolean
   % operator. Returns t iff [l] contains any quantifiers.
   l and (rl_quap rl_op car l or rl_bquap rl_op car l or 
      pasf_contains!-quantifier cdr l);

endmodule; % pasfnf

end; % of the file
