% ----------------------------------------------------------------------
% $Id: acfsfbnf.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(acfsf_bnf_rcsid!* acfsf_bnf_copyright!*);
   acfsf_bnf_rcsid!* :=
      "$Id: acfsfbnf.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   acfsf_bnf_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module acfsfbnf;
% Algebraically closed field standard form Boolean normal forms.
% Submodule of [acfsf].

procedure acfsf_dnf(f);
   % Algebraically closed field disjunctive normal form. [f] is a
   % formula. Returns a DNF of [f]. Depends on switch [rlbnfsac].
   if !*rlbnfsac then
      (cl_dnf f) where !*rlsiso=T
   else
      cl_dnf f;

procedure acfsf_cnf(f);
   % Algebraically closed field conjunctive normal form. [f] is a
   % formula. Returns a CNF of [f]. Depends on switch [rlbnfsac].
   if !*rlbnfsac then
      (cl_cnf f) where !*rlsiso=T
   else
      cl_cnf f;

procedure acfsf_subsumption(l1,l2,gor);
   % Algebraically closed subsumption. [l1] and [l2] are lists of
   % atomic formulas; [gor] is one of [and], [or]. Returns one of
   % [keep1], [keep2], [nil].
   if gor eq 'or then (
      if acfsf_subsumep!-and(l1,l2) then
 	 'keep2
      else if acfsf_subsumep!-and(l2,l1) then
	 'keep1
   ) else  % [gor eq 'and]
      if acfsf_subsumep!-or(l1,l2) then
	 'keep1
      else if acfsf_subsumep!-or(l2,l1) then
	 'keep2;

procedure acfsf_subsumep!-and(l1,l2);
   % Algebraically closed field standard form subsume [and] case. [l1]
   % and [l2] are lists of atomic formulas.
   begin scalar a;
      while l2 do <<
	 a := car l2;
	 l2 := cdr l2;
	 if cl_simpl(a,l1,-1) neq 'true then a := l2 := nil
      >>;
      return a
   end;

procedure acfsf_subsumep!-or(l1,l2);
   % Algebraically closed field standard form subsume [or] case. [l1]
   % and [l2] are lists of atomic formulas.
   begin scalar a;
      while l1 do <<
	 a := car l1;
	 l1 := cdr l1;
	 if cl_simpl(rl_smkn('or,l2),{a},-1) neq 'true then a := l1 := nil
      >>;
      return a
   end;

procedure acfsf_sacatlp(a,l);
   % Algebraically closed field subsume and cut atomic formula list
   % predicate. [a] is an atomic formula; [l] is a list of atomic
   % formulas. Returns [T] iff a subsumption or a cut can be applied
   % between [a] and an element of [l].
   not ((acfsf_arg2l a neq acfsf_arg2l w) and ordp(acfsf_arg2l a,acfsf_arg2l w))
      where w=car l;

procedure acfsf_sacat(a1,a2,gor);
   % Algebraically closed field subsume and cut atomic formula. [a1]
   % and [a2] are atomic formulas; [gor] is one of [and], [or].
   % Returns [nil], [keep], [keep1], [keep2], [drop], or an atomic
   % formula. If [nil] is returned, then neither a cut nor a
   % subsumption can be applied. If [keep] is returned, then the
   % atomic formulas are identical. In the case of [keep1] or [keep2],
   % the corresponding atomic formula must be kept, but the other one
   % can be dropped. If an atomic formula, is returned then this
   % atomic formula is the result of the cut beween [a1] and [a2]. If
   % ['drop] is returned, then a cut with result [true] or [false] can
   % be performed.
   begin scalar w;
      if acfsf_arg2l a1 neq acfsf_arg2l a2 then return nil;
      w := acfsf_sacrel(acfsf_op a1, acfsf_op a2,gor);
      if w memq '(drop keep keep1 keep2) then return w;
      return acfsf_0mk2(w,acfsf_arg2l a1)
   end;

procedure acfsf_sacrel(r1,r2,gor);
   % Algebraically closed field standard form subsume and cut
   % relation. [r1] and [r2] are relations; [gor] is one of [or],
   % [and]. Returns ['keep], ['keep2], ['keep1], ['drop], or a
   % relation. [r1] and [r2] are considered as relations of atomic
   % formulas $[r1](t,0)$ and $[r2](t,0)$. If [keep] is returned then
   % the atomic formulas are identical, in the case of [keep1] or
   % [keep2] the respective atomic formula must be kept but the other
   % can be dropped, if a relation $\rho$ is returned a cut with
   % result $t\rho 0$ can be performed, where $t$ is the left hand
   % side of [a1] and [a2], if ['drop] is returned, a cut with result
   % ['true] or ['false] can be performed.
   if r1 eq r2 then 'keep else 'drop;

endmodule;  % [acfsfbnf]

end;  % of file
