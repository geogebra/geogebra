% ----------------------------------------------------------------------
% $Id: ofsfsiat.red 1257 2011-08-14 12:04:39Z thomas-sturm $
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
   fluid '(ofsf_siat_rcsid!* ofsf_siat_copyright!*);
   ofsf_siat_rcsid!* :=
      "$Id: ofsfsiat.red 1257 2011-08-14 12:04:39Z thomas-sturm $";
   ofsf_siat_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module ofsfsiat;
% Ordered field standard form simplification. Submodule of [ofsf].

procedure ofsf_simplat1(f,sop);
   % Ordered field standard form simplify atomic formula. [f] is an
   % atomic formula; [sop] is the boolean operator [f] occurs with or
   % [nil]. Accesses switches [rlsiatadv], [rlsipd], [rlsiexpl], and
   % [rlsiexpla]. Returns a quantifier-free formula that is a
   % simplified equivalent of [f].
   begin scalar rel,lhs;
      rel := ofsf_op f;
      if not (rel memq '(equal neq leq geq lessp greaterp)) then
 	 return nil;
      lhs := ofsf_arg2l f;
      if domainp lhs then
 	 return if ofsf_evalatp(rel,lhs) then 'true else 'false;
      lhs := quotf(lhs,sfto_dcontentf lhs);
      if minusf lhs then <<
    	 lhs := negf lhs;
    	 rel := ofsf_anegrel rel
      >>;
      if null !*rlsiatadv then return ofsf_0mk2(rel,lhs);
      if rel eq 'equal then return ofsf_simplequal(lhs,sop);
      if rel eq 'neq then return ofsf_simplneq(lhs,sop);
      if rel eq 'leq then return ofsf_simplleq(lhs,sop);
      if rel eq 'geq then return ofsf_simplgeq(lhs,sop);
      if rel eq 'lessp then return ofsf_simpllessp(lhs,sop);
      if rel eq 'greaterp then return ofsf_simplgreaterp(lhs,sop)
   end;

procedure ofsf_simplequal(lhs,sop);
   % Ordered field standard form simplify [equal]-atomic formula.
   % [lhs] is a term. Returns a quantifier-free formula.
   begin scalar w,ff,ww;
      w := ofsf_posdefp lhs;
      if w eq 'stsq then return 'false;
      ff := sfto_sqfpartf lhs;
      ww := ofsf_posdefp ff;
      if ww eq 'stsq then return 'false;
      if !*rlsitsqspl and (!*rlsiexpla or !*rlsiexpl and sop = 'and) then <<
	 if ww eq 'tsq then return ofsf_tsqsplequal ff;
	 if w eq 'tsq then return ofsf_tsqsplequal lhs
      >>;
      return ofsf_facequal!*(ff,sop)
   end;

procedure ofsf_tsqsplequal(f);
   % Trivial square sum split [equal] case.
   begin scalar w;
      w := ofsf_getsqsummons(f);
      if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and null cdr w) then
	 return rl_smkn('and,for each m in w collect
	    rl_smkn('or,for each v in m collect ofsf_0mk2('equal,v)));
      return rl_smkn('and,for each m in w collect
 	 ofsf_0mk2('equal,sfto_lmultf m))
   end;

procedure ofsf_facequal!*(f,sop);
   if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and sop = 'or) then
      ofsf_facequal f
   else
      ofsf_0mk2('equal,f);

procedure ofsf_facequal(f);
   % Left hand side factorization [equal] case.
   rl_smkn('or,for each x in cdr fctrf f collect ofsf_0mk2('equal,car x));

procedure ofsf_simplneq(lhs,sop);
   % Ordered field standard form simplify [neq]-atomic formula.
   % [lhs] is a term. Returns a quantifier-free formula.
   begin scalar w,ff,ww;
      w := ofsf_posdefp lhs;
      if w eq 'stsq then return 'true;
      ff := sfto_sqfpartf lhs;
      ww := ofsf_posdefp ff;
      if ww eq 'stsq then return 'true;
      if !*rlsitsqspl and (!*rlsiexpla or !*rlsiexpl and sop = 'or) then <<
	 if ww eq 'tsq then return ofsf_tsqsplneq ff;
	 if w eq 'tsq then return ofsf_tsqsplneq lhs
      >>;
      return ofsf_facneq!*(ff,sop)
   end;

procedure ofsf_tsqsplneq(f);
   % Trivial square sum split [neq] case.
   begin scalar w;
      w := ofsf_getsqsummons(f);
      if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and null cdr w) then
	 return rl_smkn('or,for each m in w collect
	    rl_smkn('and,for each v in m collect ofsf_0mk2('neq,v)));
      return rl_smkn('or,for each m in w collect
 	 ofsf_0mk2('neq,sfto_lmultf m))
   end;

procedure ofsf_facneq!*(f,sop);
   if !*rlsifac and (!*rlsiexpla or !*rlsiexpl and sop = 'and) then
      ofsf_facneq f
   else
      ofsf_0mk2('neq,f);

procedure ofsf_facneq(f);
   % Left hand side factorization [neq] case.
   rl_smkn('and,for each x in cdr fctrf f collect ofsf_0mk2('neq,car x));

procedure ofsf_getsqsummons(f);
   % Ordered field standard form get squaresum monomials. [f] is an
   % SF. Returns a list of SFs.
   begin scalar v,w;
      if null f then return nil;
      if domainp f then return {nil};  % i.e. {1}
      w := ofsf_getsqsummons(red f);
      v := !*k2f mvar f;
      for each x in ofsf_getsqsummons lc f do
	 w := (v . x) . w;
      return w
   end;

procedure ofsf_simplleq(lhs,sop);
   % Ordered field standard form simplify [leq]-atomic formula. [lhs]
   % is a term, [sop] is a boolean operator or [nil]. Accesses
   % switches [rlsipd], [rlexpl], and [rlexpla]. Returns a
   % quantifier-free formula.
   begin scalar s1,s2,w,x;
      if (s1 := ofsf_posdefp lhs) eq 'stsq then
 	 return 'false;
      w := sfto_sqfpartf lhs;
      if (s2 := ofsf_posdefp w) eq 'stsq then
 	 return 'false;
      if !*rlsitsqspl and (!*rlsiexpla or !*rlsiexpl and sop = 'and) then <<
	 if s2 then return ofsf_tsqsplequal w;
	 if s1 then return ofsf_tsqsplequal lhs
      >>;
      if s1 or s2 then
 	 return ofsf_facequal!*(w,sop);
      if null !*rlsipd and null !*rlsifaco then
 	 return ofsf_0mk2('leq,lhs);
      x := sfto_pdecf lhs;
      s1 := ofsf_posdefp car x;
      if s1 eq 'stsq then
	 return ofsf_facequal!*(cdr x,sop);
      if s1 then
	 return ofsf_facequal!*(w,sop);
      if ofsf_posdefp cdr x eq 'stsq then
	 cdr x := 1;
      if !*rlsifaco then <<
	 car x := sfto_lmultf ofsf_facsimpl car x;
	 cdr x := ofsf_facsimpl cdr x
      >> else
	 cdr x := if not eqn(cdr x,1) then {cdr x};
      if ofsf_posdefp car x eq 'stsq then
	 car x := 1;
      w := sfto_lmultf cdr x;
      if ofsf_posdefp w eq 'stsq then <<
	 cdr x := nil;
	 w := 1
      >>;
      if eqn(car x,1) and eqn(w,1) then
	 return 'false;
      if !*rlsiexpla or (!*rlsiexpl and (sop eq 'or)) then
	 return rl_smkn('or,ofsf_0mk2('leq,car x) .
	    for each fac in cdr x collect ofsf_0mk2('equal,fac));
      return ofsf_0mk2('leq,multf(car x,exptf(w,2)))
   end;

procedure ofsf_facsimpl(u);
   for each x in cdr fctrf u join
      if not (ofsf_posdefp car x eq 'stsq) then
      	 {car x};

procedure ofsf_simplgeq(lhs,sop);
   % Ordered field standard form simplify [geq]-atomic formula. [lhs]
   % is a term, [sop] is a boolean operator or [nil]. Accesses
   % switches [rlsipd], [rlexpl], and [rlexpla]. Returns a
   % quantifier-free formula.
   begin scalar x,w,s1,s2;
      if ofsf_posdefp lhs or ofsf_posdefp sfto_sqfpartf lhs then
 	 return 'true;
      if not !*rlsipd and not !*rlsifaco then
      	 return ofsf_0mk2('geq,lhs);
      x := sfto_pdecf lhs;
      if ofsf_posdefp car x then
	 return 'true;
      if ofsf_posdefp cdr x eq 'stsq then
	 cdr x := 1;
      if !*rlsifaco then <<
	 car x := sfto_lmultf ofsf_facsimpl car x;
	 cdr x := ofsf_facsimpl cdr x
      >> else
	 cdr x := if not eqn(cdr x,1) then {cdr x};
      w := sfto_lmultf cdr x;
      s1 := ofsf_posdefp car x;
      s2 := ofsf_posdefp w;
      if s1 and s2 then
	 return 'true;
      if s1 eq 'stsq then
      	 car x := 1
      else if s2 eq 'stsq then <<
	 cdr x := nil;
	 w := 1
      >>;
      if !*rlsiexpla or (!*rlsiexpl and (sop eq 'or)) then
	 return rl_smkn('or,ofsf_0mk2('geq,car x) .
	    for each fac in cdr x collect ofsf_0mk2('equal,fac));
      return ofsf_0mk2('geq,multf(car x,exptf(w,2)))
   end;

procedure ofsf_simpllessp(lhs,sop);
   % Ordered field standard form simplify [lessp]-atomic formula.
   % [lhs] is a term, [sop] is a boolean operator or [nil]. Accesses
   % switches [rlsipd], [rlexpl], and [rlexpla]. Returns a
   % quantifier-free formula.
   begin scalar x,w,s1,s2;
      if ofsf_posdefp lhs or ofsf_posdefp sfto_sqfpartf lhs then
 	 return 'false;
      if not !*rlsipd and not !*rlsifaco then
      	 return ofsf_0mk2('lessp,lhs);
      x := sfto_pdecf lhs;
      if ofsf_posdefp car x then
	 return 'false;
      if ofsf_posdefp cdr x eq 'stsq then
	 cdr x := 1;
      if !*rlsifaco then <<
	 car x := sfto_lmultf ofsf_facsimpl car x;
	 cdr x := ofsf_facsimpl cdr x
      >> else
	 cdr x := if not eqn(cdr x,1) then {cdr x};
      w := sfto_lmultf cdr x;
      s1 := ofsf_posdefp car x;
      s2 := ofsf_posdefp w;
      if s1 and s2 then
	 return 'false;
      if s1 eq 'stsq then
      	 car x := 1
      else if s2 eq 'stsq then <<
	 cdr x := nil;
	 w := 1
      >>;
      if !*rlsiexpla or (!*rlsiexpl and (sop eq 'and)) then
	 return rl_smkn('and,ofsf_0mk2('lessp,car x) .
	    for each fac in cdr x collect ofsf_0mk2('neq,fac));
      return ofsf_0mk2('lessp,multf(car x,exptf(w,2)))
   end;

procedure ofsf_simplgreaterp(lhs,sop);
   % Ordered field standard form simplify [greaterp]-atomic formula.
   % [lhs] is a term, [sop] is a boolean operator or [nil]. Accesses
   % switches [rlsipd], [rlexpl], and [rlexpla]. Returns a
   % quantifier-free formula.
   begin scalar s1,s2,w,x;
      if !*rlpos and sfto_varf lhs then
 	 return ofsf_0mk2('greaterp,lhs);
      if (s1 := ofsf_posdefp lhs) eq 'stsq then
 	 return 'true;
      w := sfto_sqfpartf lhs;
      if (s2 := ofsf_posdefp w) eq 'stsq then  % Proposition 3.3 (ii)
 	 return 'true;
      if !*rlsitsqspl and (!*rlsiexpla or !*rlsiexpl and sop = 'or) then <<
	 if s2 then return ofsf_tsqsplneq w;
	 if s1 then return ofsf_tsqsplneq lhs
      >>;
      if s1 or s2 then
 	 return ofsf_facneq!*(w,sop);
      if null !*rlsipd and null !*rlsifaco then
 	 return ofsf_0mk2('greaterp,lhs);
      x := sfto_pdecf lhs;
      s1 := ofsf_posdefp car x;  % could return better fac info for free
      if s1 eq 'stsq then  % in particular, 1 is an stsq.
	 return ofsf_facneq!*(cdr x,sop);
      if s1 then
	 return ofsf_facneq!*(w,sop);
      if ofsf_posdefp cdr x eq 'stsq then
	 cdr x := 1;
      if !*rlsifaco then <<
	 car x := sfto_lmultf ofsf_facsimpl car x;
	 cdr x := ofsf_facsimpl cdr x
      >> else
	 cdr x := if not eqn(cdr x,1) then {cdr x};
      if ofsf_posdefp car x eq 'stsq then
	 car x := 1;
      w := sfto_lmultf cdr x;
      if ofsf_posdefp w eq 'stsq then <<
	 cdr x := nil;
	 w := 1
      >>;
      if eqn(car x,1) and eqn(w,1) then
	 return 'true;
      if !*rlsiexpla or (!*rlsiexpl and (sop eq 'and)) then
	 return rl_smkn('and,ofsf_0mk2('greaterp,car x) .
	    for each fac in cdr x collect ofsf_0mk2('neq,fac));
      return ofsf_0mk2('greaterp,multf(car x,exptf(w,2)))
   end;

procedure ofsf_evalatp(rel,lhs);
   % Ordered field standard form evaluate atomic formula. [rel] is a
   % relation; [lhs] is a domain element. Returns a truth value
   % equivalent to $[rel]([lhs],0)$.
   if rel eq 'equal then null lhs
   else if rel eq 'neq then not null lhs
   else if rel eq 'leq then minusf lhs or null lhs
   else if rel eq 'geq then not minusf lhs
   else if rel eq 'lessp then minusf lhs
   else if rel eq 'greaterp then not (minusf lhs or null lhs)
   else rederr {"ofsf_evalatp: unknown operator ",rel};

procedure ofsf_posdefp(u);
   % Ordered field standard form positive definite predicate. [u] is
   % an SF. Returns ['stsq] if [u] is positive definite, ['tsq] if [u]
   % is postive semidefinite, and [nil] else. The return values origin
   % from "(strict) trivial square sum" but are also used for positive
   % QE, where also other polynomials are classified positive.
   if !*rlpos then ofsf_posdefp!-pos u else sfto_tsqsumf u;

procedure ofsf_posdefp!-pos(u);
   % Ordered field standard form positive definite predicate for
   % positive QE. [u] is an SF. Returns ['stsq] if [u] is positive
   % definite, ['tsq] if [u] is postive semidefinite, and [nil] else.
   % Essentially like sfto_tsqsumf but parity of degrees does not
   % matter.
   if null u then 'tsq else ofsf_posdefp!-pos1 u;

procedure ofsf_posdefp!-pos1(u);
   % Ordered field standard form positive definite predicate for
   % positive QE subroutine. [u] is an SF. Returns ['stsq] if [u] is
   % positive definite and [nil] else.
   if domainp u then
      (if not minusf u then 'stsq)
   else
      ofsf_posdefp!-pos1 lc u and ofsf_posdefp!-pos1 red u;

endmodule;  % [ofsfsiat]

end;  % of file
