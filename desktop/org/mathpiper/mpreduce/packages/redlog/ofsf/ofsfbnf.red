% ----------------------------------------------------------------------
% $Id: ofsfbnf.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(ofsf_bnf_rcsid!* ofsf_bnf_copyright!*);
   ofsf_bnf_rcsid!* :=
      "$Id: ofsfbnf.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   ofsf_bnf_copyright!* :=
      "Copyright (c) 1995-2009 A. Dolzmann, A. Seidl, T. Sturm"
>>;

module ofsfbnf;
% Ordered field standard form boolean normal forms. Submodule of [ofsf].

procedure ofsf_dnf(f);
   % Ordered field standard form conjunctive normal form. [f] is a
   % formula. Returns a DNF of [f].
   if !*rlbnfsac then
      (cl_dnf f) where !*rlsiso=T
   else
      cl_dnf f;

procedure ofsf_cnf(f);
   % Ordered field standard form conjunctive normal form. [f] is a
   % formula. Returns a CNF of [f].
   if !*rlbnfsac then
      (cl_cnf f) where !*rlsiso=T
   else
      cl_cnf f;

procedure ofsf_subsumption(l1,l2,gor);
   % Ordered field standard form subsume. [l1] and [l2] are lists of
   % atomic formulas. Returns one of [imp], [rep], [nil].
   if gor eq 'or then (
      if ofsf_subsumep!-and(l1,l2) then
 	 'keep2
      else if ofsf_subsumep!-and(l2,l1) then
	 'keep1
   ) else  % [gor eq 'and]
      if ofsf_subsumep!-or(l1,l2) then
	 'keep1
      else if ofsf_subsumep!-or(l2,l1) then
	 'keep2;

procedure ofsf_subsumep!-and(l1,l2);
   % Ordered field standard form subsume [and] case. [l1] and [l2] are
   % lists of atomic formulas.
   begin scalar a;
      while l2 do <<
	 a := car l2;
	 l2 := cdr l2;
	 if cl_simpl(a,l1,-1) neq 'true then a := l2 := nil
      >>;
      return a
   end;

procedure ofsf_subsumep!-or(l1,l2);
   % Ordered field standard form subsume [or] case. [l1] and [l2] are
   % lists of atomic formulas.
   begin scalar a;
      while l1 do <<
	 a := car l1;
	 l1 := cdr l1;
	 if cl_simpl(rl_smkn('or,l2),{a},-1) neq 'true then a := l1 := nil
      >>;
      return a
   end;

procedure ofsf_sacatlp(a,l);
   % Ordered field standard form subsume and cut atomic formula list
   % predicate. [a] is an atomic formula; [l] is a list of atomic
   % formulas. [T] is returned if a subsumption or cut beween [a] and
   % an element of [l] is possible.
   not ((ofsf_arg2l a neq ofsf_arg2l w) and ordp(ofsf_arg2l a,ofsf_arg2l w))
      where w=car l;

procedure ofsf_sacat(a1,a2,gor);
   % Ordered field standard form subsume and cut atomic formula. [a1]
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
      if ofsf_arg2l a1 neq ofsf_arg2l a2 then return nil;
      w := ofsf_sacrel(ofsf_op a1, ofsf_op a2,gor);
      if w memq '(drop keep keep1 keep2) then return w;
      return ofsf_0mk2(w,ofsf_arg2l a1)
   end;

procedure ofsf_sacrel(r1,r2,gor);
   % Ordered field standard form subsume and cut relation. [r1] and
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
      ofsf_sacrel!-or(r1,r2)
   else
      ofsf_sacrel!-and(r1,r2);

procedure ofsf_sacrel!-or(r1,r2);
   % Ordered field standard form subsume and cut relation or. [r1] and
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
      w:= '( (lessp . ( (lessp . keep) (leq . keep1) (equal . leq)
                        (neq . keep1) (geq . drop) (greaterp . neq)))
	     (leq   . ( (lessp . keep2) (leq . keep) (equal . keep2)
		        (neq . drop) (geq . drop) (greaterp . drop)))
	     (equal . ( (lessp . leq) (leq . keep1) (equal . keep)
		        (neq . drop) (geq . keep1) (greaterp . geq)))
	     (neq   . ( (lessp . keep2) (leq . drop) (equal . drop)
		        (neq . keep) (geq . drop) (greaterp . keep2)))
	     (geq   . ( (lessp . drop) (leq . drop) (equal . keep2)
		        (neq . drop) (geq . keep) (greaterp . keep2)))
	     (greaterp . ( (lessp . neq)  (leq . drop)  (equal . geq)
	             	(neq . keep1) (geq . keep1) (greaterp . keep))));
      return cdr atsoc(r1,cdr atsoc(r2,w));
   end;

procedure ofsf_sacrel!-and(r1,r2);
   % Ordered field standard form subsume and cut relation and. [r1] and
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
      w:= '( (lessp . ( (lessp . keep) (leq . keep2) (equal . drop)
                        (neq . keep2) (geq . drop) (greaterp . drop)))
	     (leq   . ( (lessp . keep1) (leq . keep) (equal . keep1)
		        (neq . lessp) (geq . equal) (greaterp . drop)))
	     (equal . ( (lessp . drop) (leq . keep2) (equal . keep)
		        (neq . drop) (geq . keep2) (greaterp . drop)))
	     (neq   . ( (lessp . keep1) (leq . lessp) (equal . drop)
		        (neq . keep) (geq . greaterp) (greaterp . keep1)))
	     (geq   . ( (lessp . drop) (leq . equal) (equal . keep1)
		        (neq . greaterp) (geq . keep) (greaterp . keep1)))
	     (greaterp . ( (lessp . drop)  (leq . drop)  (equal . drop)
	             	(neq . keep2) (geq . keep2) (greaterp . keep))));
      return cdr atsoc(r1,cdr atsoc(r2,w))
   end;

% ----------------------------------------------------------------------
% Simplification of Boolean normal forms in the style of W. V. Quine.
% ----------------------------------------------------------------------

procedure ofsf_qssubat(pl,a);
   begin scalar w,r;
      w := ofsf_qssubatfind(pl,a);
      if not w then
	 return a;
      r := ofsf_qssubatrel(ofsf_op w,ofsf_op a);      
      return if rl_tvalp r then
	 r
      else
	 ofsf_0mk2(r,ofsf_arg2l a)
   end;

procedure ofsf_qssubatfind(pl,a);
   begin scalar r,la;
      la := ofsf_arg2l a;
      while pl do <<
	 if ofsf_arg2l car pl = la then <<
	    r := car pl;
	    pl := nil
	 >> else
	    pl := cdr pl
      >>;
      return r
   end;

% TODO: Reuse tables of ofsfsism
procedure ofsf_qssubatrel(r1,r2);
   begin scalar w;
      % Printen mit: for each x in w do << for each y in cdr x do <<
      % prin2 cdr y; prin2 "  " >>; prin2t ""; >>;
      w:= '( (lessp . ( (lessp . true) (leq . true) (equal . false)
                        (neq . true) (geq . false) (greaterp . false)))
	     (leq   . ( (lessp . neq) (leq . true) (equal . geq)
		        (neq . neq) (geq . geq) (greaterp . false)))
	     (equal . ( (lessp . false) (leq . true) (equal . true)
		        (neq . false) (geq . true) (greaterp . false)))
	     (neq   . ( (lessp . leq) (leq . leq) (equal . false)
		        (neq . true) (geq . geq) (greaterp . geq)))
	     (geq   . ( (lessp . false) (leq . leq) (equal . leq)
		        (neq . neq) (geq . true) (greaterp . neq)))
	     (greaterp . ( (lessp . false)  (leq . false)  (equal . false)
	             	(neq . true) (geq . true) (greaterp . true))));
      return cdr atsoc(r2,cdr atsoc(r1,w))
   end;
   
procedure ofsf_qstrycons(a,c1,c2,op);
   % quine simplification try consensus. [a] is an atomic formula,
   % [c1] and [c2] are clauses, op is one of ['and], ['or]. Returns
   % [T], [nil] or [break]. [c1] contains [a].
   begin scalar r,cc1,cc2,w;
      w := ofsf_qstrycons!-find(a,c2);
      if null w then
	 return nil;
      r := ofsf_qstrycons!-or(ofsf_op a,ofsf_op w);
      if null r or r eq 'false then
	 return nil;
      cc1 := delete(a,c1);  % Copy... % TODO: delq or delete?
      cc2 := delete(w,c2);  % Copy... % TODO: delq or delete?
      w := append(cc1,cc2);  % TODO: nconc
      if r neq 'true then
      	 w := ofsf_0mk2(r,ofsf_arg2l a) . w;	    
      w := cl_qssimplc(w,nil,op);  % TODO: CNF
      if w eq 'false then
	 return nil;
      if w eq 'true then
	 return 'break;
%%       prin2t "Konsens von";
%%       mathprint rl_prepfof rl_smkn('and,c1);
%%       prin2t "Mit";
%%       mathprint rl_prepfof rl_smkn('and,c2);
%%       prin2t "Ueber";
%%       mathprint rl_prepfof a;
%%       prin2t "Ergibt";
%%       mathprint rl_prepfof rl_smkn('and,w);
      return w
   end;

procedure ofsf_qstrycons!-or(r1,r2);
   begin scalar w;      
      w := ofsf_qsflip ofsf_smeqtable(ofsf_qsflip r1,ofsf_qsflip r2);
      return if w eq r1 or w eq r2 then nil else w
   end;	 

procedure ofsf_qsflip(r);
   if rl_tvalp r then
      cl_flip r
   else
      ofsf_lnegrel r;
	 
% (TODO) REMARK: Letters occurs only once.

procedure ofsf_qstrycons!-find(a,c2);
   begin scalar r,la;
      la := ofsf_arg2l a;
      while c2 do <<
	 if ofsf_arg2l car c2 = la then <<
	    r := car c2;
	    c2 := nil
	 >> else
	    c2 := cdr c2;
      >>;
      return r
   end;

procedure ofsf_qssusuat(a1,a2,op);
   ofsf_arg2l a1 = ofsf_arg2l a2 and ofsf_qssusutab(ofsf_op a1,ofsf_op a2);

% TODO: Abbrechen bei offensichtlichem widerspruch
procedure ofsf_qssusutab(r1,r2);
   begin scalar w;
      % Printen mit: for each x in w do << for each y in cdr x do <<
      % prin2 cdr y; prin2 "  " >>; prin2t ""; >>;
      w:= '( (lessp . ( (lessp . T) (leq . T) (equal . nil)
                        (neq . T) (geq . nil) (greaterp . nil)))
	     (leq   . ( (lessp . nil) (leq . T) (equal . nil)
		        (neq . nil) (geq . nil) (greaterp . nil)))
	     (equal . ( (lessp . nil) (leq . T) (equal . T)
		        (neq . nil) (geq . T) (greaterp . nil)))
	     (neq   . ( (lessp . nil) (leq . nil) (equal . nil)
		        (neq . T) (geq . nil) (greaterp . nil)))
	     (geq   . ( (lessp . nil) (leq . nil) (equal . nil)
		        (neq . nil) (geq . T) (greaterp . nil)))
	     (greaterp . ( (lessp . nil)  (leq . nil)  (equal . nil)
	             	(neq . T) (geq . T) (greaterp . T))));
      return cdr atsoc(r2,cdr atsoc(r1,w))
   end;

procedure ofsf_qssiadd(a,c,theo);
   begin scalar w;
      w := ofsf_qssifind(a,c);
      if null w then
	 return a . c;
      c := delq(w,c);
      w := ofsf_qssibin(a,w);
      return if rl_tvalp w then
	 w
      else
	 w . c
   end;

procedure ofsf_qssibin(a1,a2);
   begin scalar w;
      w := ofsf_qssirel(ofsf_op a1,ofsf_op a2);
      if rl_tvalp w then
	 return w;
      return ofsf_0mk2(w,ofsf_arg2l a1);
   end;

procedure ofsf_qssifind(a,c);
   begin scalar r,la;
      la := ofsf_arg2l a;
      while c do <<
	 if ofsf_arg2l car c = la then <<
	    r := car c;
	    c := nil
	 >> else
	    c := cdr c
      >>;
      return r
   end;

procedure ofsf_qssirel(r1,r2);
   begin scalar w;
      % Printen mit: for each x in w do << for each y in cdr x do <<
      % prin2 cdr y; prin2 "  " >>; prin2t ""; >>;
      w:= '( (lessp . ( (lessp . lessp) (leq . lessp) (equal . false)
                        (neq . lessp) (geq . false) (greaterp . false)))
	     (leq   . ( (lessp . lessp) (leq . leq) (equal . equal)
		        (neq . lessp) (geq . equal) (greaterp . false)))
	     (equal . ( (lessp . false) (leq . equal) (equal . equal)
		        (neq . false) (geq . equal) (greaterp . false)))
	     (neq   . ( (lessp . lessp) (leq . lessp) (equal . false)
		        (neq . neq) (geq . greaterp) (greaterp . greaterp)))
	     (geq   . ( (lessp . false) (leq . equal) (equal . equal)
		        (neq . greaterp) (geq . geq) (greaterp . greaterp)))
	     (greaterp . ( (lessp . false)  (leq . false)  (equal . false)
		        (neq . greaterp) (geq . greaterp) (greaterp . greaterp)
		   )));
      return cdr atsoc(r2,cdr atsoc(r1,w))
   end;

endmodule;  % [ofsfbnf]

end;  % of file
