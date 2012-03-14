% ----------------------------------------------------------------------
% $Id: talpbnf.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(talp_bnf_rcsid!* talp_bnf_copyright!*);
   talp_bnf_rcsid!* := 
      "$Id: talpbnf.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   talp_bnf_copyright!* :=
      "Copyright (c) 2004-2009 by A. Dolzmann, T. Sturm"
>>;

module talpbnf;
% Term algebra Lisp prefix boolean normal forms. Submodule of [talp].

procedure talp_dnf(f);
   % Term algebra Lisp prefix disjunctive normal form. [f] is a
   % formula. Returns a DNF of [f].
   if !*rlbnfsac then
      (cl_dnf f) where !*rlsiso=T
   else
      cl_dnf f;

procedure talp_cnf(f);
   % Term algebra Lisp prefix conjunctive normal form. [f] is a
   % formula. Returns a CNF of [f].
   if !*rlbnfsac then
      (cl_cnf f) where !*rlsiso=T
   else
      cl_cnf f;

procedure talp_subsumption(l1,l2,gor);
   % Term algebra Lisp prefix subsume. [l1] and [l2] are lists of
   % atomic formulas. Returns one of [keep1], [keep2], [nil].
   if gor eq 'or then (
      if talp_subsumep!-and(l1,l2) then
 	 'keep2
      else if talp_subsumep!-and(l2,l1) then
	 'keep1
   ) else  % [gor eq 'and]
      if talp_subsumep!-or(l1,l2) then
	 'keep1
      else if talp_subsumep!-or(l2,l1) then
	 'keep2;

procedure talp_subsumep!-and(l1,l2);
   % Term algebra Lisp prefix subsume [and] case. [l1] and [l2] are
   % lists of atomic formulas.
   begin scalar a;
      while l2 do <<
	 a := car l2;
	 l2 := cdr l2;
	 if cl_simpl(a,l1,-1) neq 'true then a := l2 := nil
      >>;
      return a
   end;

procedure talp_subsumep!-or(l1,l2);
   % Term algebra Lisp prefix subsume [or] case. [l1] and [l2] are
   % lists of atomic formulas.
   begin scalar a;
      while l1 do <<
	 a := car l1;
	 l1 := cdr l1;
	 if cl_simpl(rl_smkn('or,l2),{a},-1) neq 'true then a := l1 := nil
      >>;
      return a
   end;

procedure talp_sacatlp(a,l);
   % Term algebra Lisp prefix subsume and cut atomic formula list
   % predicate. [a] is an atomic formula; [l] is a list of atomic
   % formulas. [T] is returned if a subsumption or cut beween [a] and
   % an element of [l] is possible.
   not ((talp_arg2l a neq talp_arg2l w) and ordp(talp_arg2l a,talp_arg2l w))
      where w=car l;

procedure talp_sacat(a1,a2,gor);
   % Term algebra Lisp prefix subsume and cut atomic formula. [a1]
   % and [a2] are atomic formulas; [gor] is one of [or], [and].
   % Returns [nil], ['keep], ['keep2], ['keep1], ['drop], or an atomic
   % formula. If [nil] is returned then neither a cut nor a
   % subsumption can be applied, if [keep] is returned then the atomic
   % formuas are identical, in the case of [keep1] or [keep2] the
   % respective atomic formula must be kept but the other can be
   % dropped. If an atomic formula $a$ is returned then it is the
   % result of the cut beween [a1] and [a2], if ['drop] is returned, a
   % cut with result ['true] or ['false] can be performed.
   begin scalar w;
      if talp_arg2l a1 neq talp_arg2l a2 then return nil;
      w := talp_sacrel(talp_op a1, talp_op a2,gor);
      if w memq '(drop keep keep1 keep2) then return w;
      return talp_mk2(w,talp_arg2l a1,talp_arg2r a2)
   end;

procedure talp_sacrel(r1,r2,gor);
   % Term algebra Lisp prefix subsume and cut relation. [r1] and
   % [r2] are relations; [gor] is one of [or], [and]. Returns ['keep],
   % ['keep2], ['keep1], ['drop], or a relation. [r1] and [r2] are
   % considered as relations of atomic formulas $[r1](t,0)$ and
   % $[r2](t,0)$. If [keep] is returned then the atomic formulas are
   % identical, in the case of [keep1] or [keep2] the respective
   % atomic formula must be kept but the other can be dropped, if a
   % relation $\rho$ is returned a cut with result $t\rho 0$ can be
   % performed, where $t$ is the left hand side of [a1] and [a2], if
   % ['drop] is returned, a cut with result ['true] or ['false] can be
   % performed.
   if gor eq 'or then
      talp_sacrel!-or(r1,r2)
   else
      talp_sacrel!-and(r1,r2);

procedure talp_sacrel!-or(r1,r2);
   % Term algebra Lisp prefix subsume and cut relation or. [r1] and
   % [r2] are relations. ['keep], ['keep2], ['keep1], ['drop], or a
   % relation is returned. [r1] and [r2] are considered as relations
   % of atomic formulas $[r1](t,0)$ and $[r2](t,0)$. If [keep] is
   % returned then the atomic formulas are identical, in the case of
   % [keep1] or [keep2] the respective atomic formula must be kept but
   % the other can be dropped, if a relation $\rho$ is returned a cut
   % with result $t\rho 0$ can be performed, where $t$ is the left
   % hand side of [a1] and [a2], if ['drop] is returned a cut with
   % result ['true] can be performed.
   begin scalar w;
      w:= '( (equal . ( (equal . keep) (neq . drop)))
	     (neq   . ( (equal . drop) (neq . keep))));
      return cdr atsoc(r1,cdr atsoc(r2,w));
   end;

procedure talp_sacrel!-and(r1,r2);
   % Term algebra Lisp prefix subsume and cut relation and. [r1] and
   % [r2] are relations. ['keep], ['keep2], ['keep1], ['drop], or a
   % relation is returned. [r1] and [r2] are considered as relations
   % of atomic formulas $[r1](t,0)$ and $[r2](t,0)$. If [keep] is
   % returned then the atomic formulas are identical, in the case of
   % [keep1] or [keep2] the respective atomic formula must be kept but
   % the other can be dropped, if a relation $\rho$ is returned a cut
   % with result $t\rho 0$ can be performed, where $t$ is the left
   % hand side of [a1] and [a2], if ['drop] is returned a cut with
   % result ['false] can be performed.
   begin scalar w;
      w:= '( (equal . ( (equal . keep) (neq . drop)))
	     (neq   . ( (equal . drop) (neq . keep))));
      return cdr atsoc(r1,cdr atsoc(r2,w))
   end;

endmodule;  % [talpbnf]

end;  % of file
