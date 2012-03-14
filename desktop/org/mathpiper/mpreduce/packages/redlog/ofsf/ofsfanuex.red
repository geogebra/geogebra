% ----------------------------------------------------------------------
% $Id: ofsfanuex.red 385 2009-07-15 07:17:21Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2001-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(ofsf_anuex_rcsid!* ofsf_anuex_copyright!*);
   ofsf_anuex_rcsid!* :=
      "$Id: ofsfanuex.red 385 2009-07-15 07:17:21Z thomas-sturm $";
   ofsf_anuex_copyright!* := "(c) 2001-2009 A. Dolzmann, A. Seidl, T. Sturm"
>>;

module ofsfanuex;
% ANUEX - a package for primitive or hierarchical representation of
% algebraic numbers and expressions in algebraic numbers (algebraic
% polynomials). the features include incremental real root isolation
% and factorisation.

procedure ofsf_anuexverbosep();
   % CAD verbose predicate.
   !*rlverbose and !*rlanuexverbose;

procedure type_of(te);
   % Type of. [te] is a typed expressions
   car te;

% module generic;

procedure generic_sort(al,sortfn);
   % Sort. [l] is a list of some type ALPHA, [sortfn] is a
   % function that takes two arguments [a1], [a2] of type ALPHA and
   % returns 1, if [a1] is bigger than [a2], -1, if [a2] is bigger
   % than [a1] and 0 if they are equal. returns a list of type ALPHA.
   begin scalar all;      
      if null al or null cdr al then return al;
      % now there are at least 2 elements in al.
      all := for each a in al collect {a};
      repeat <<	 
      	 all := generic_doublesort(all,sortfn) 
      >> until null cdr all;
      return car all % the only element of all is the list of sorted roots 
   end;

procedure generic_doublesort(all,sortfn);
   begin scalar res;
      % as long as there at least two elements in all:
      while not (null all or null cdr all) do <<
	 res := generic_doublesort1(car all,cadr all,sortfn) . res;
	 all := cddr all;
      >>;
      if all then res := car all . res;
      return res;
   end;

procedure generic_doublesort1(al1,al2,sortfn);
   % Algebraic number doublesort. Takes two sorted lists. Returns a
   % sorted list.
   begin scalar rl,cmp;
      if null al1 then
	 return al2;
      if null al2 then
	 return al1;
      while al1 and al2 do <<
	 cmp := apply2(sortfn,car al1,car al2);
	 if cmp eq 0 then <<
	    rl := car al1 . rl;  al1 := cdr al1; %al2 := cdr al2
	 >> else if cmp eq -1 then << % car al1 < car al2
	    rl := car al1 . rl; al1 := cdr al1
	 >> else <<
	    rl := car al2 . rl; al2 := cdr al2
	 >>
      >>;
      return nconc(nconc(reversip rl,al1),al2)
   end;

% module iv;
% Interval

%DS
% <INT> ::= (<RAT> . <RAT>)
% <RAT> ::= <SQ> over the integers

procedure iv_mk(l,r);
   % Interval make. [l] and [r] are RAT's. Returns an INT.
   l . r;

procedure iv_mk4(n1,d1,n2,d2);
   iv_mk(rat_mk(n1,d1),rat_mk(n2,d2));

procedure iv_lb(i);
   % Interval left bound. [i] is an INT. Returns a RAT. Returns the
   % left bound of [i].
   car i;

procedure iv_rb(iv);
   % Interval right bound. [i] is an INT. Returns a RAT. Returns the
   % right bound of [i].
   cdr iv;

procedure iv_print(iv);
   << prin2 "]"; rat_print iv_lb iv; prin2 ",";
      rat_print iv_rb iv; prin2 "[";
   >>;

procedure iv_neg(i);
   % Interval negate. [i] is an INT. Returns an INT. Returns $-[i]$,
   % i.e., the interval mirrored at 0.
   iv_mk(negsq iv_rb I,negsq iv_lb I);

procedure iv_add(iv1,iv2);
   % Interval addition. [iv1] and [iv2] are INT. Returns an INT.
   iv_mk(addsq(iv_lb iv1,iv_lb iv2),addsq(iv_rb iv1,iv_rb iv2));

procedure iv_mapadd(ivl);
   % Interval map addition. [ivl] is a list of INT. Returns an interval.
   % Recommendation: [ivl] is non-empty
   if null ivl then
      iv_mk(rat_fromnum 0,rat_fromnum 0)
   else
      iv_add(car ivl,iv_mapadd cdr ivl);

procedure iv_mult(iv1,iv2);
   % Interval multiplication. [iv1] and [iv2] are INT. Returns an INT.
   iv_mk(rat_min4(rr,rl,lr,ll),rat_max4(rr,rl,lr,ll)) where
      rr = multsq(iv_rb iv1,iv_rb iv2),
      rl = multsq(iv_rb iv1,iv_lb iv2),
      lr = multsq(iv_lb iv1,iv_rb iv2),
      ll = multsq(iv_lb iv1,iv_lb iv2);

procedure iv_med(iv);
   % Point in the middle of an interval.
   rat_mult(rat_add(iv_lb iv,iv_rb iv),rat_mk(1,2));

procedure iv_tothen(iv,n);
   % Interval to the n. [iv] is an interval, [n] a positive natural
   % number. Returns an INT.
   if n=0 then
      iv_mk(rat_mk(1,1),rat_mk(1,1)) 
   else
      iv_mult(iv,iv_tothen(iv,n-1));
   
procedure iv_containszero(iv);
   % Interval contains zero. Returns [t] or [nil].
   if rat_leq(iv_lb iv,rat_0()) and rat_leq(rat_0(),iv_rb iv) then
      t
   else
      nil;

procedure iv_comp(iv1,iv2);
   % Interval compare intervals. [iv1], [iv2] are intervals. Returns -1,
   % if [iv1] is below [iv2], 1, if [iv2] is above [iv1] and 0, if the
   % intersection of [iv1] and [iv2] is non- empty. [iv1], [iv2] are
   % regarded as open.
   if rat_leq(iv_rb iv1,iv_lb iv2) then
      -1
   else if rat_leq(iv_rb iv2,iv_lb iv1) then
      1
   else 0;
   
procedure iv_maxabs(iv);
   % Interval maximal absolut value. [iv] is an INT. Returns a RAT.
   rat_max(rat_abs iv_lb iv,rat_abs iv_rb iv);

procedure iv_minabs(iv);
   % Interval minimal absolut value. [iv] is an INT. Returns a RAT.
   if iv_containszero iv then
      rat_0()
   else
      rat_min(rat_abs iv_lb iv,rat_abs iv_rb iv);

procedure iv_minus(iv1,iv2);
   % Returns a list of IV, resulting from subtracting [iv2] from [iv1].
   nconc(
      if rat_less(iv_lb iv1,iv_lb iv2) then
	 {iv_mk(iv_lb iv1,rat_min(iv_lb iv2,iv_rb iv1))},
      if rat_less(iv_rb iv2,iv_rb iv1) then
	 {iv_mk(rat_max(iv_rb iv2,iv_lb iv1),iv_rb iv1)});

procedure iv_minuslist(iv1,ivl2);
   % ivl2 is a list of distinct intervals. Returns a list of IV.
   if null ivl2 then
      {iv1}
   else
      iv_listminuslist(iv_minus(iv1,car ivl2),cdr ivl2);

procedure iv_listminuslist(ivl1,ivl2);
   % ivl1, ivl2 are each lists of distinct intervals.
   for each iv1 in ivl1 join iv_minuslist(iv1,ivl2);

% module num;
%DS
% <NUM> ::= integer

procedure num_even(n);
   n eq n/2*2;

procedure num_sgnch(l);
   % Integer number sign changes. [l] is a list of NUM. Returns a
   % NUM.
   num_sgnch1(for each n in l join if sgn n = 0 then nil else n . nil);

procedure num_sgnch1(l);
   % Integer number sign changes 1. [l] is a list of positive or
   % negative NUM.  Returns a non-negative NUM. Counts the number
   % of sign changes.
   if null l or null cdr l then
      0 % in a list with at most one element there are no sign changes
   else if sgn car l eq sgn cadr l then
      num_sgnch1 cdr l
   else
      1 + num_sgnch1 cdr l;

procedure num_sortfn(n1,n2);
   % for use in generic_sort.
   sgn(n1-n2);

% module numpoly;

%DS
% <NUMPOLY> ::= <SF> over the integers

procedure numpoly_null;
   % Integer number polynomial null. Returns a NUMPOLY.
   nil;

procedure numpoly_nullp(p);
   % Integer number polynomial null predicate. [p] is a
   % NUMPOLY. Returns [t] or [nil]. Double null is allowed for.
   null p or p eq 0;

procedure numpoly_lc(p);
   % Integer number polynomial leading coefficient. [p] is a
   % NUMPOLY. Returns a NUMPOLY.  Caveat: extended semantics wrt. lc
   % for sf's.
   if null p then
      p
   else if numberp p then
      p
   else
      lc p;

procedure numpoly_red(p);
   % Integer number polynomial reductum. p is a numpoly. Returns a
   % numpoly.
   if null p or numberp p then
      numpoly_null()
   else
      red p;

procedure numpoly_mvartest(p,x);
   % Integer number polynomial main variable test. [p] is a NUMPOLY,
   % [x] is an identifier.  Returns [t] or [nil]. Checks, whether [x]
   % is the main variable of [p]
   if null p then
      nil
   else if numberp p then
      nil
   else
      (mvar p eq x);

% REMARK: the following holds: p a numpoly in x. then:
% p == (if mvartest(p,x) then mult(lc(p),xtothen('x,ldeg p))+red p

procedure numpoly_ldeg(p);
   % Integer number polynomial leading degree. [p] is a
   % NUMPOLY. Returns -1 or a positive integer.
   if numpoly_nullp p then
      -1
   else if numberp p then
      0
   else
      ldeg p;

procedure numpoly_factorize p;
   fctrf p;

% module rat;
% Rational numbers.

%DS
% <RAT> ::= <SQ> over integers

% datatype rat: rational numbers, represented as standard quotients of 
% integers.

procedure rat_mk(n,d);
   % Rational number make. [n] and [d] are NUM. Returns a RAT.
   quotsq(simp n,simp d);
   %   n . d;

procedure rat_numr(q);
   numr q;

procedure rat_denr(q);
   denr q;

procedure rat_normdenr(q);
   << if !*rlanuexdebug then if minusf rat_denr q then
      prin2 "***** rat_normdenr: denr is negative";
      rat_numr q ./ 1
   >>;

procedure rat_print r;
   << prin2 numr r; prin2 "/"; prin2 denr r; >>; 

procedure rat_fromnum(n);
   % Rational number from integer number. [n] is a NUM. Returns a RAT.
   simp n;

procedure rat_neg(p);
   % Rational number negation. [p] is a RAT. 
   negsq(p);

procedure rat_add(p,q);
   % Rational number addition. [p], [q] are RAT's. Returns a RAT. 
   addsq(p,q);

procedure rat_minus(p,q);
   % Rational number minus. [p], [q] are RAT's. Returns a RAT. 
   addsq(p,negsq q);

procedure rat_mapadd(pl);
   % Rational number map addition. [pl] is a list of RAT. Returns a RAT. 
   if pl=nil then %%%
      rat_0()
   else
      rat_add(car pl,rat_mapadd cdr pl);

procedure rat_mult(p,q);
   % Rational number multiplication. [p], [q] are RAT's. Returns a RAT. 
   multsq(p,q);

procedure rat_quot(p,q);
   % Rational number quotient. [p], [q] are RAT's, the latter has to
   % be non-zero. Returns a RAT.
   quotsq(p,q);

procedure rat_nullp(q);
   % Rational number null predicate. [q] is a RAT. Returns [t] or
   % [nil]. Double null is allowed for.
   null numr q or numr q = 0;

procedure rat_sgn(q);
   % Rational number sign. [q] is of type RAT. Returns -1, 0 or 1 
   if null numr q or numr q = 0 then
      0
   else if sgn numr q = sgn denr q then
      1
   else
      -1;

procedure rat_0;
   % Rational number 0. Returns a RAT.
   simp 0;

procedure rat_1;
   % Rational number 1. Returns a RAT.
   simp 1;

procedure rat_less(p,q);
   % Rational number less. [p],[q] are RAT's. Returns [t] or [nil].
   rat_sgn(addsq(p,negsq q)) = -1;

procedure rat_greater(p,q);
   % Rational number greater. [p],[q] are RAT's. Returns [t] or [nil].
   rat_sgn(addsq(p,negsq q)) = 1;

procedure rat_eq(p,q);
   % Rational number equal. [p], [q] are RAT's. Returns [t] or [nil].
   rat_sgn(rat_minus(p,q)) eq 0;

procedure rat_leq(p,q);
   % Rational number less equal. [p], [q] are RAT's. Returns [t] or
   % [nil].
   rat_sgn(addsq(p,negsq q)) < 1;

procedure rat_min(p,q);
   % Rational number minimum. [p], [q] are RAT's. Returns a RAT.
   if rat_leq(p,q) then
      p
   else
      q;

procedure rat_max(p,q);
   % Rational number minimum. [p], [q] are RAT's. Returns a RAT.
   if rat_leq(p,q) then
      q
   else
      p;

procedure rat_mapmax(pl);
   % Rational number map maximum. [pl] is a non-empty list of RAT.
   % Returns a RAT.
   if null cdr pl then
      car pl
   else
      rat_max(car pl,rat_mapmax cdr pl);

procedure rat_min4(a,b,c,d);
   % Rational number minimum of 4. [a], [b], [c], [d] are
   % RAT's. Returns a RAT.
   rat_min(rat_min(a,b),rat_min(c,d));

procedure rat_max4(a,b,c,d);
   % Rational number maximum of 4. [a], [b], [c], [d] are
   % RAT's. Returns a RAT.   
   rat_max(rat_max(a,b),rat_max(c,d));

procedure rat_abs(p);
   % Rational number absolut value. [p] is a RAT. Returns a RAT.
   absf numr p ./ denr p;

% module ratpoly;
   
%DS
% <RATPOLY> ::= <SQ> over integer, where the denominator is an integer

procedure ratpoly_fromatom(a);
   % Rational number polynomial atom to rational number
   % polynomial. [a] is an atom. Returns a RATPOLY.
   simp a;

procedure ratpoly_fromsf(f);
   % Rational number polynomial standard form to rational number
   % polynomial. [f] is a SF over integers. Returns an RATPOLY.
   !*f2q f;

procedure ratpoly_fromrat(r);
   % Rational number polynomial from rational number. [r] is a
   % RAT. Returns an RATPOLY.
   r;

procedure ratpoly_torat(r);
   << if !*rlanuexdebug and ratpoly_idl r then
      prin2t "***** ratpoly_torat: argument is not a rational";
      r
   >>;

procedure ratpoly_toaf(r);
   prepsq r;

procedure ratpoly_0();
   simp 0;

procedure ratpoly_1();
   simp 1; % ratpoly_fromatom(1);

procedure ratpoly_mklin(x,ba);
   % makes linear polynomial a*x-b, which has the root ba.
   ratpoly_fromsf addf(multf(numr simp x,rat_denr ba),negf rat_numr ba);

procedure ratpoly_print q;
   mathprint prepsq q;

procedure ratpoly_ratp(q);
   % Rational number polynomial rational number predicate. [q] is a
   % RATPOLY. Returns [t] or [nil].  Tests, if a RATPOLY is a RAT.
   numberp numr q or null numr q;

procedure ratpoly_null();
   % Rational number polynomial null. Returns a RATPOLY.
   simp 0;

procedure ratpoly_nullp(q);
   % Rational number polynomial null predicate. [q] is a
   % RATPOLY. Returns [t] or [nil].
   null numr q or numr q = 0;

procedure ratpoly_sgn q;
   %%% ratpolydebug
   if !*rlanuexdebug and not ratpoly_ratp q then
      prin2t "***** ratpoly_sgn: not a constant polynomial"
   else
      rat_sgn q;
      
procedure ratpoly_neg(q);
   % Rational number polynomial negation. [q] is a RATPOLY. Returns a
   % RATPOLY.
   negsq(q);

procedure ratpoly_add(q1,q2);
   % Rational number polynomial addition. [q1], [q2] are
   % RATPOLY's. Returns a RATPOLY.
   addsq(q1,q2);

procedure ratpoly_minus(q1,q2);
   % Rational number polynomial minus. [q1], [q2] are
   % RATPOLY's. Returns a RATPOLY.
   subtrsq(q1,q2);

procedure ratpoly_mult(q1,q2);
   % Rational number polynomial multiplication. [q1], [q2] are
   % RATPOLY's. Returns a RATPOLY.
   multsq(q1,q2);

procedure ratpoly_foldmult(ql);
   if null ql then
      ratpoly_1()
   else
      ratpoly_mult(car ql,ratpoly_foldmult(cdr ql));   

procedure ratpoly_quot(q1,q2);
   % Rational number polynomial quotient. [q1], [q2] are
   % RATPOLY's, [q2] is not null. Returns a RATPOLY.
   quotsq(q1,q2);

procedure ratpoly_pp(q);
   (sfto_dprpartksf numr q) . denr q;

procedure sf_univarp(f);
   if domainp f or (domainp lc f and sf_univarp1(red f,mvar f)) then
      t;

procedure sf_univarp1(f,x);
   if domainp f or (domainp lc f and mvar f eq x and sf_univarp1(red f,x)) then
      t;

procedure ratpoly_univarp(q);
   sf_univarp numr q;

procedure ratpoly_idl q;
   % Identifier list. 
   sf_idl numr q;

procedure ratpoly_mvartest(q,x);
   % Rational number polynomial main variable test. [q] is a RATPOLY,
   % [x] is an identifier. Returns [t] or [nil].
   if null numr q then
      nil
   else if numberp numr q then
      nil
   else
      (mvar numr q eq x);
   
procedure ratpoly_lc(q);
   % Rational number polynomial leading coefficient. [q] is a RATPOLY.
   % Returns a RATPOLY, the leading coefficient of $q$.
   numpoly_lc numr q ./ denr q;

procedure ratpoly_red(q);
   % Rational number polynomial reductum. [q] is a a RATPOLY.
   % Returns a RATPOLY, the reductum of $q$.
   numpoly_red numr q ./ denr q;

procedure ratpoly_ldeg(q);
   % Rational number polynomial leading degree. [q] is a
   % RATPOLY. Returns -1 or a natural number.
   numpoly_ldeg numr q;

procedure ratpoly_deg(q,x);
   % Degree of [q] in [x].
   if ratpoly_mvartest(q,x) then
      ratpoly_ldeg(q)
   else
      if ratpoly_nullp q then -1 else 0;

procedure ratpoly_exp(rp,n);
   if n eq 0 then
      ratpoly_1()
   else
      ratpoly_mult(rp,ratpoly_exp(rp,n-1));

procedure ratpoly_xtothen(x,n);
   % Rational number polynomial x to the n. [x] is an identifier, [n]
   % is a natural number. Returns a RATPOLY.
   if n = 0 then
      ratpoly_fromatom 1
   else
      ratpoly_mult(ratpoly_fromatom x,ratpoly_xtothen(x,n-1)); 

procedure ratpoly_diff(q,x);
   % Rational number polynomial derivation. [q] is a RATPOLY, [x] is
   % an identifier. Returns a RATPOLY.
   diffsq(q,x);
  
procedure ratpoly_subrat(q,k,r);
   % Rational number polynomial substitute rational number. [q] is a
   % RATPOLY, [k] an identifier (the kernel to be replaced) and [r] is
   % a SQ (the replacement). Returns a RATPOLY, the exact result of
   % the substitution.
   if domainp numr q or mvar numr q neq k then q else
% this might be faster here. test, if needed.
%      !*f2q(cdr qremf(multf(numr q,exptf(denr r,ldeg numr q)),
%	 addf(multf(!*k2f k,denr r),negf numr r)));
   begin scalar cl,res;
      cl := coeffs numr q; % (c0,...,cn)
      res := !*f2q car cl;
      for each c in cdr cl do 
	 res := addsq(!*f2q c,multsq(res,r));
      res := quotsq(res,!*f2q denr q);
      if !*rlanuexdebug and not ratpoly_nullp
      	 ratpoly_minus(res,subsq(q,{k . prepsq r})) then
      	 prin2 "***** ratpoly_subrat: faulty calculation";
      return res;
   end;

procedure ratpoly_subrat1(q,k,r);
   % Rational number polynomial substitute rational number. [q] is a
   % RATPOLY, [k] an identifier (the kernel to be replaced) and [r] is
   % a SQ (the replacement). Returns a RATPOLY. Remark: if opt is nil
   % then the the result is exact, otherwise, if opt is t, at least
   % the sign is correct. %subsq(q,{k . prepsq r});
   if domainp numr q or mvar numr q neq k then q else
   begin scalar cl,res;
      cl := coeffs numr q; % (c0,...,cn)
      res := !*f2q car cl;
      for each c in cdr cl do 
	 res := addsq(!*f2q c,multsq(res,r));
      if !*rlanuexdebug and not ratpoly_nullp
      	 ratpoly_minus(quotsq(res,!*f2q denr q),
	    subsq(q,{k . prepsq r})) then
      	 prin2 "***** ratpoly_subrat: faulty calculation";
      return res;
   end;

procedure ratpoly_sub(q,k,af);
   % Rational number polynomial substitute rational number. [q] is a
   % RATPOLY, [k] an identifier (the kernel to be replaced) and [af]
   % is an algebraic form (the replacement). Returns a RATPOLY.
   subsq(q,{k . af});

procedure ratpoly_tad(q);
   % Throw away denominator.
   numr q ./ 1;

procedure ratpoly_gcd(q1,q2); % plus
   % Rational number polynomial gretest common divisor. [q1], [q2] are
   % RATPOLY's. Returns a RATPOLY.
   begin scalar tmp1,tmp2,res;
      % convert to algebraic expression
      tmp1 := prepsq q1;
      tmp2 := prepsq q2;
      on1 'rational;
      % convert to sf over rationals 
      tmp1 := numr simp tmp1;
      tmp2 := numr simp tmp2;
      res := gcdf(tmp1,tmp2);
      % convert to algebraic expression
      res := prepf res;
      off1 'rational;
      return simp res;
   end;

procedure ratpoly_resultant(q1,q2,y); 
   % Rational number polynomial gretest common divisor. [q1], [q2] are
   % RATPOLY's. Returns a RATPOLY.
   begin scalar tmp1,tmp2,res;
      % convert to algebraic expression
      tmp1 := prepsq q1;
      tmp2 := prepsq q2;
      on1 'rational;
      % convert to sf over rationals 
      tmp1 := numr simp tmp1;
      tmp2 := numr simp tmp2;
      res := resultant(tmp1,tmp2,y);
      % convert to algebraic expression
      res := prepf res;
      off1 'rational;
      return simp res;
   end;

procedure ratpoly_factorize(q);
   % Type: ratpoly -> list(pair(ratpoly,num))
   begin scalar tmp;
      tmp := numpoly_factorize numr q; % gives a
      % const factor has multiplicity 1
      car tmp := ratpoly_fromrat rat_mk(car tmp,denr q) . 1;
      tmp := car tmp .
	 for each sfn in cdr tmp collect (ratpoly_fromsf car sfn) . cdr sfn;
      if !*rlanuexdebug and not
      	 ratpoly_nullp(ratpoly_minus(q,ratpoly_foldmult(
	    for each sfn in tmp collect ratpoly_exp(car sfn,cdr sfn)))) then
	 prin2t "***** ratpoly_factorize: selftest failed";
      return tmp;
   end;

% module sf;

procedure sf_deg(f,x);
   if domainp f then
      if null f then -1 else 0
   else
      if mvar f = x then ldeg f else 0;

% module ctx;
% Context

% IAL ::= LIST(PAIR(ID,ANU))
% Type: {'ctx, ial} where ial
% Description: 

procedure ctx_fromial(ia);
   {'ctx,ia};

procedure ctx_new;
   ctx_fromial nil;

procedure ctx_ial(c);
   cadr c;

procedure ctx_idl(c);
   for each ia in ctx_ial c collect car ia;

procedure ctx_print(c);
   <<
      prin2 "[(ctx) ";
      for each ia in ctx_ial c do
	 <<prin2 car ia; prin2 "->"; anu_print cdr ia>>;
      prin2 "] "
   >>;

procedure ctx_add(ia,c);
   % Add, no check. [ia] is a dotted pair (xj . a) where xj is an id
   % an a is an anu.
   {'ctx,ia . ctx_ial c};

procedure ctx_addip(ia,c);
   % Add, no check. [ia] is a dotted pair (xj . a) where xj is an id
   % an a is an anu.
   cadr c := ia . ctx_ial c;

procedure ctx_rm(x,c);
   ctx_fromial(for each ia in ctx_ial c join
      if car ia eq x then nil else {ia});

procedure ctx_free(x,c);
   % frees x and all higher variables. x has to be bound by c.
   begin scalar idorderv,ial;
      if !*rlanuexdebug and null ctx_get(x,c) then
	 prin2t "***** ctx_free: variable unbound";
      idorderv := setkorder nil;
      setkorder idorderv;
      ial := ctx_ial c;
      repeat <<
	 while caar ial neq car idorderv do idorderv := cdr idorderv;
	 ial := cdr ial; % free the highest pair	 
      >> until null idorderv or car idorderv eq x;
      return ctx_fromial ial;
   end;

procedure ctx_get(x,c);
   % Returns the algebraic number to which x is bound, nil if x is
   % unbound.
   begin scalar ial,res;
      if !*rlanuexdebug and not idp x then rederr "ctx_get: arguments invalid";
      ial := ctx_ial c;
      while ial and null res do <<
	 if caar ial eq x then res := cdar ial; ial := cdr ial
      >>;
      if !*rlanuexdebug and null res then
	 prin2 "***** ctx_get: variable unbound";
      return res;
   end;

procedure ctx_union(c1,c2);
   % Union of syntactically compatible contexts.
   % Syntactically/semantically compatible means: for every id x: if
   % (x,a1) in c1 and (x,a2) in c2 then syntactically/semantically
   % a1=a2.
   begin scalar idorderv,ial;      
      idorderv := setkorder nil;
      setkorder idorderv;
      c1 := ctx_ial c1; c2 := ctx_ial c2;
      for each id in idorderv do %%% while loop might be faster
	 if not null c1 and caar c1 eq id then <<
	    ial := car c1 . ial;
	    c1 := cdr c1;
	    if c2 then if caar c2 eq id then c2 := cdr c2;
	 >>
	 else if not null c2 and caar c2 eq id then <<
	    ial := car c2 . ial;
	    c2 := cdr c2;
	 >>;
      if c1 or c2 then prin2t "***** ctx_union: idorder not complete";
      return ctx_fromial reversip ial;
   end;

% module aex;
% Algebraic expressions.

%%% --- constructors and access functions --- %%%

procedure aex_fromrp rp;
   {'aex,rp,ctx_new(),t,t};

procedure aex_fromrat r;
   {'aex,ratpoly_fromrat r,ctx_new(),t,t};

procedure aex_0();
   aex_fromrp ratpoly_0();

procedure aex_1();
   aex_fromrp ratpoly_1();

procedure aex_mk(rp,c,lcnttag,reducedtag);
   % Make. [rp] is a ratpoly, [c] is a context. Return an aex.
   {'aex,rp,c,lcnttag,reducedtag};

procedure aex_mklin(x,ba);
   % Make linear poly a*x+b. x is an id, ba is a rat.
   %aex_fromsf addf(multf(numr simp x,rat_denr ba),negf rat_numr ba);
   aex_fromrp ratpoly_mklin(x,ba);
   
procedure aex_fromsf(f);
   aex_fromrp ratpoly_fromsf f;

procedure aex_mapfromsf(fl);
   for each f in fl collect aex_fromsf f;

procedure aex_ex(ae);
   cadr ae;

procedure aex_ctx(ae);
   caddr ae;

procedure aex_freeall(ae);
   aex_mk(aex_ex ae,ctx_new(),t,t);

procedure aex_free(ae,x);
   % Free x and all higher variables. 
   aex_mk(aex_ex ae,ctx_free(x,aex_ctx ae),nil,nil);

procedure aex_bind(ae,x,a);
   %%% todo: ensure [a] is defined with x
   % test if [a] is rational (then use aex_subrat)
   if null aex_boundids anu_dp a and aex_deg(anu_dp a,x) eq 1 then
      aex_subrp(ae,x,ratpoly_toaf aex_ex aex_linsolv(anu_dp a,x))
   else aex_mk(aex_ex ae,ctx_add(x . a,aex_ctx ae),nil,nil);

procedure aex_print(ae);
   <<
      ratpoly_print aex_ex ae;
      if ctx_ial aex_ctx ae then << prin2 ", where ";ctx_print aex_ctx ae; >>
   >>;

% - tags stuff - %

procedure aex_lcnttag ae;
   cadddr ae;

procedure aex_reducedtag ae;
   cadr cdddr ae;

procedure aex_putlcnttag(ae,v);
   cadddr ae := v;

procedure aex_putreducedtag(ae,v);
   cadr cdddr ae := v;


%% --- arithmetic with pseudodivision, sign, nullp, psgcd pssqfree --- %%%

procedure aex_neg(ae);
   aex_mk(ratpoly_neg aex_ex ae,aex_ctx ae,aex_lcnttag ae,aex_reducedtag ae);

procedure aex_add(ae1,ae2);
   % Add, contexts are assumed to be compatible and will be merged.
   % caveat: minimization will be needed.
   aex_mk(ratpoly_add(aex_ex ae1,aex_ex ae2),
      ctx_union(aex_ctx ae1, aex_ctx ae2),
      nil,aex_reducedtag ae1 and aex_reducedtag ae2);

procedure aex_addrat(ae,r);
   % Add rational number.
   aex_mk(ratpoly_add(aex_ex ae, ratpoly_fromrat r),aex_ctx ae,
      nil,nil); %%%?
   
procedure aex_minus(ae1,ae2);
   % Minus, contexts are assumed to be compatible and will be merged.
   aex_mk(ratpoly_minus(aex_ex ae1,aex_ex ae2),
      ctx_union(aex_ctx ae1, aex_ctx ae2),
      nil,aex_reducedtag ae1 and aex_reducedtag ae2);
  
procedure aex_mult(ae1,ae2);
   % Multiplication, contexts are assumed to be compatible and will be merged.
   aex_mk(ratpoly_mult(aex_ex ae1,aex_ex ae2),
      ctx_union(aex_ctx ae1, aex_ctx ae2),
      aex_lcnttag ae1 and aex_lcnttag ae2,nil);

procedure aex_foldmult(ael);
   if null ael then
      aex_1()
   else
      aex_mult(car ael,aex_foldmult cdr ael);

procedure aex_multrat(ae,r);
   % Multiply with rational number.
   aex_mk(ratpoly_mult(aex_ex ae, ratpoly_fromrat r),aex_ctx ae,
      nil,nil);

procedure aex_diff(ae,x);
   % differentiate. [ae] is an algebraic polynomial in [x].
   % Returns an AEX. 
   aex_mk(ratpoly_diff(aex_ex ae,x),aex_ctx ae,
      nil,nil);

procedure aex_subrp(ae,x,af);
   % Substitute algebraic form in algebraic expression.
   aex_mk(ratpoly_sub(aex_ex ae,x,af),aex_ctx ae,
      nil,nil); %%% minimize ex and ctx

procedure aex_subrat(ae,x,r);
   % exact
   aex_mk(ratpoly_subrat(aex_ex ae,x,r),aex_ctx ae,nil,nil);

procedure aex_subrat1(ae,x,r);
   % exact up to sign
   aex_mk(ratpoly_subrat1(aex_ex ae,x,r),aex_ctx ae,nil,nil);

procedure aex_tad(ae);
   % Throw away denominator.
   aex_mk(ratpoly_tad aex_ex ae,aex_ctx ae,nil,nil); 

procedure aex_xtothen(x,n);
   % x to the n.
   aex_mk(ratpoly_xtothen(x,n),ctx_new(),
      t,t);

procedure aex_deg(ae,x);
   % Degree of [ae] in [x].
   ratpoly_deg(aex_ex ae,x);

procedure aex_simpleratpolyp(ae);
   % Simple but uncomplete predicte to test, if ae represents a
   % (maybe constant) rational polynomial
   null ctx_ial aex_ctx ae or ratpoly_ratp aex_ex ae;

procedure aex_simpleratp ae;
   ratpoly_ratp aex_ex ae;
  
procedure aex_simplenullp(ae);
   % Simple but uncomplete null-predicate. It is complete, if ae is
   % minimized.
   ratpoly_nullp(aex_ex ae);

procedure aex_simplenumberp(ae);
   not aex_freeids ae;

procedure aex_ids(ae);
   ratpoly_idl aex_ex ae;

procedure aex_freeids(ae);
   % free identifiers, the id with highest kernel order first
   lto_setminus(ratpoly_idl aex_ex ae,ctx_idl aex_ctx ae);

procedure aex_boundids(ae);
   intersection(aex_ids ae,ctx_idl aex_ctx ae);

procedure aex_constp(ae);
   % Constant predicate. %%% faster!
   null aex_freeids ae;

procedure aex_nullp(ae);
   % Null predicate. [ae] is an AEX. Returns [t] or [nil].
   begin scalar tmp;
      % make the lc non-trivial
      tmp := aex_mklcnt ae; 
      % ae is a non-constant polynomial 
      if aex_freeids tmp then
	 return nil;
      % ae is a constant
      if aex_sgn tmp eq 0 then
	 return t
      else
	 return nil;
   end;

procedure aex_mvaroccurtest(ae,x);
   ratpoly_mvartest(aex_ex ae,x);

procedure aex_red(ae,x);
   % Reductum of [ae] wrt [x]. Needs not to be minimized.
   if aex_mvaroccurtest(ae,x) then
      aex_mk(ratpoly_red aex_ex ae,aex_ctx ae,nil,nil) %%% mklcnt
   else
      aex_0();

procedure aex_lc(ae,x);   
   if aex_mvaroccurtest(ae,x) then
      aex_mk(ratpoly_lc aex_ex ae,aex_ctx ae,nil,nil)
   else
      ae; % ctx needs not to be made smaller, as there are no singles

procedure aex_mvar ae;
   begin scalar idl;
      % semanticcheck
      if not (idl := aex_freeids ae) then
	 prin2t "***** aex_mvar: invalid argument";
      return car idl;
   end;

procedure aex_mklcnt(ae);
   % Make leading coefficient of non-constant polynomial non-trivial.
   % was: minimize.
   begin scalar idl;
      % quick win: obvious cases: ae is rat or contains no alg. numbers
      %% turn around, use ctx_ial
      if ratpoly_ratp aex_ex ae or null ctx_idl aex_ctx ae then
      	 return ae;
      % ae is a non-constant algebraic polynomial
      if idl := aex_freeids ae then
      	 if aex_nullp aex_lc(ae,car idl) then
      	    return aex_mklcnt aex_red(ae,car idl)
	 else
	    return ae;
      % ae is constant
      return %%% remark: this is the second edge in the dependence graph where the argument is passed and not made smaller. (this is not a problem) 
	 if aex_sgn ae eq 0 then aex_0() else ae
   end;

%procedure aex_minimize(ae);
%   % Algebraic expression minimize. [ae] is an AEX(c,d) with
%   % $c<d$. Returns an AEX(c,d), where the $r$-component is null or
%   % where the leading coefficient is non-trivial.
%   if aex_getr ae = ratpoly_null() then
%      aex_mkfromr(ae,ratpoly_null())
%   else if aex_nullp aex_lc ae then
%      aex_minimize aex_red ae
%   else
%      ae;

%procedure aex_reduce(ae);
%   % convention: the bound variable has to be defined by an anu using
%   % the same variable.
%   begin scalar ids,x,alpha,rlc,rred,tmp;
%      %%% care for special case aex_simpleratp !!!
%      % there are no bound variables: we have a (const.) rat. poly
%      if not aex_boundids ae then
%	 return ae;
%      % from now on there are bound variables
%      ids := aex_ids ae; x := car ids;
%      % there are free variables
%      if aex_freeids ae then <<
%	 rlc := aex_reduce(aex_lc(ae,x));
%	 rred := aex_reduce(aex_red(ae,x));
%	 return aex_add(aex_mult(rlc,aex_xtothen(x,aex_deg(ae,x))),rred)
%      >>;
%      % there are no free variables
%      if !*rlanuexdebug and not (x eq caar ctx_ial aex_ctx ae) then
%	 prin2t "aex_reduce: variable mismatch";
 %     %%%alpha := cdar ctx_ial aex_ctx ae; % context = {(x . alpha),...}
%      alpha := ctx_get(x,aex_ctx ae);
%      tmp := aex_free(ae,x);
%      tmp := aex_reduce tmp;
%      if aex_deg(tmp,x) >= aex_deg(anu_dp alpha,x) then
%	 return aex_bind(aex_rem(tmp,anu_dp alpha,x),x,alpha)
%      else
%	 return aex_bind(tmp,x,alpha)
%   end;

procedure aex_reduce ae;
   % convention: the bound variable has to be defined by an anu using
   % the same variable.
   begin scalar ids,x,alpha,rlc,rred,tmp;
      % there are no bound variables: we have a (const.) rat. poly
      if not aex_boundids ae then
	 tmp := ae;
      % from now on there are bound variables
      if null tmp then << ids := aex_ids ae; x := car ids >>;
      % there are free variables
      if null tmp and aex_freeids ae then <<
	 rlc := aex_reduce(aex_lc(ae,x));
	 rred := aex_reduce(aex_red(ae,x));
	 tmp := aex_add(aex_mult(rlc,aex_xtothen(x,aex_deg(ae,x))),rred)
      >>
      % there are no free variables
      else if null tmp then <<
      	 %if !*rlanuexdebug and not (x eq caar ctx_ial aex_ctx ae) then
	 %   prin2t "aex_reduce: variable mismatch";
      	 %alpha := cdar ctx_ial aex_ctx ae; % context = {(x . alpha),...}
	 alpha := ctx_get(x,aex_ctx ae);
      	 tmp := aex_free(ae,x);
      	 tmp := aex_reduce tmp;
      	 if aex_deg(tmp,x) >= aex_deg(anu_dp alpha,x) then
	    tmp := aex_bind(aex_rem(tmp,anu_dp alpha,x),x,alpha)
      	 else
	    tmp := aex_bind(tmp,x,alpha)
      >>;
%      if !*rlanuexdebug and not aex_nullp aex_minus(ae,tmp) then
%	 prin2t "aex_reduce: selftest failed";
      return tmp;
   end;

procedure aex_reducetest(ae);
   aex_nullp aex_minus(ae,aex_reduce ae);
	 
procedure aex_psquotrem1(f,p,x);
   % pseudo quotient remainder one step. [f], [p] are algebraic
   % polynomials in [x]. Returns [(q . r)] with $a_n^2f=qp+r$.
   begin scalar am,an,m,n,q;
      am := aex_lc(f,x); % a constant algebraic poly
      an := aex_lc(p,x); % dito
      m := aex_deg (f,x);
      n := aex_deg (p,x);
      if m<n or n=-1 then
	 return (aex_0(). f);
      % 0<=m<=n
      q := aex_mult(aex_xtothen(x,m-n),aex_mult(am,an));
      return (q . aex_minus(aex_mult(f,aex_mult(an,an)),aex_mult(p,q)));
%      return (q . aex_minus(aex_mult(aex_red f,aex_mult(an,an)),
%      	 aex_red aex_mult(p,q)));
   end;

procedure aex_psquotrem(f,p,x);
   % pseudo quotient remainder. [f], [p] are algebraic polynomials in
   % [x]. Returns [(q . r . i)] with $b_n^{2*i}f=qp+r$.
   begin scalar m,n,q,r,bn2,qr1; integer i,ii;
      m := aex_deg(f,x);
      n := aex_deg(p,x);
      q := aex_0();
      r := f;
      if m<n or aex_simplenullp(p) then
	 return (q . r . 0);
      bn2 := aex_mult(bn,bn) % bn2 needed below for selftest
      %bn2 := aex_multrat(bn,rat_fromnum aex_sgn bn) % wrong; change in psquotrem1 needed as well!
	 where bn = aex_lc(p,x);
      while (aex_deg(r,x) >= n) do <<
	 qr1 := aex_psquotrem1(r,p,x);
	 q := aex_add(aex_mult(q,bn2),car qr1);
	 i := i+1;
	 r := cdr qr1;
      >>;
      if !*rlanuexdebug then <<
      	 ii := i;
	 while (ii:=ii-1) >= 0 do  % ii times multply bn^2
	    f := aex_mult(f,bn2);
      	 if not aex_nullp(aex_minus(f,aex_add(aex_mult(q,p),r))) then
	    prin2t "***** aex_psquotrem: selftest failed";
      >>;
      return (q . r . i);
   end;

procedure aex_psquot(f,p,x);
   car aex_psquotrem(f,p,x);

procedure aex_psrem(f,p,x);
   cadr aex_psquotrem(f,p,x);

procedure aex_psquotremtest(f,p,x);
   % Algebraic expression pseudo quotient remainder test. 
   % Returns a BOOL.
   begin scalar m,n,ctx,q,r,bn2,qri,ii;
      m := aex_deg(f,x); n := aex_deg(p,x);
      ctx := aex_ctx f;
      qri := aex_psquotrem(f,p,x);
      q := car qri;
      r := cadr qri;
      ii := cddr qri;
      bn2 := aex_mult(bn,bn) where bn=aex_lc(p,x);
      while (ii:=ii-1) >= 0 do  % ii times multply bn^2
	 f := aex_mult(f,bn2);
      if not aex_nullp(aex_minus(f,aex_add(aex_mult(q,p),r))) then
	 prin2 "***** aex_psquotremtest: failed";
   end;

procedure aex_stdsturmchain(f,x);
   % standard sturm chain. [f] is an algebraic polynomial in [x].
   % Returns a list of algebraic polynomials.
   aex_sturmchain(f,aex_diff(f,x),x);

procedure aex_sturmchain(f,g,x);
   % pseudo sturm chain. [f], [g] are algebraic expressions in [x].
   % Returns a list of algebraic expressions.
   begin scalar sc;
      sc := reversip(aex_remseq({ aex_tad g,aex_tad f},x));
      %%% sc := reversip(aex_remseq({aex_pp aex_tad g,aex_pp aex_tad f},x));
      %if !*rlanuexdebug then aex_sturmchaincheck sc;
      return sc
   end;

procedure aex_sturmchaincheck(sc);
   % Algebraic expression sturm chain check. [sc] is a sturm
   % chain. Returns a BOOL, and prints an error message.
   begin scalar v,l;
      v := t;
      l := length sc;
      while sc and v do 
	 if aex_nullp car sc then <<
	    v := nil;
	    ioto_prin2 {"+++ aex_sturmchaincheck: nullpoly found. length:",
	       l," position:",l-length sc+1," +++"}
	 >> else
	    sc := cdr sc;
      return v
   end;

procedure aex_pp(ae);
   % primitive part. works only for univariate polynomials with
   % rational coefficients.
% ae;
   << if !*rlanuexdebug and aex_simpleratpolyp ae and
      length aex_ids ae > 1 then
      prin2 "***** aex_pp: argument not univariate";
   if ratpoly_univarp aex_ex ae then
      aex_mk(ratpoly_pp aex_ex ae,aex_ctx ae,nil,nil)
   else
      ae
   >>;

procedure aex_remseq(ael,x);
   % remainder sequence. [ael] is a list of algebraic polynomials in
   % [x], of at least length 2. Returns a list of algebraic
   % polynomials. Caveat: the list is built in reverse order.
   begin scalar rem;
      if !*rlanuexdebug and aex_simplenullp car ael then
	 prin2 "[remseq:null]";
      if aex_deg(car ael,x) <= 0 then 
      	 return ael;
      if !*rlanuexpsremseq then
	 rem := aex_psrem(cadr ael,car ael,x)
      else
	 rem := aex_pp aex_tad aex_rem(cadr ael,car ael,x);
      if aex_simplenullp rem then 
      	 return ael;
      return aex_remseq(aex_neg rem . ael,x)
   end;

procedure aex_sturmchainsgnch(sc,x,r);
   % Algebraic expression sturm chain sign changes. [sc] is a list of
   % AEX(c,c+1), [r] is a RAT.  Returns n integer, the number of sign
   % changes of $sc$ an $r$.
   num_sgnch for each e in sc collect aex_sgn aex_subrat1(e,x,r);

procedure aex_stchsgnch(sc,x,r);
   % sturm chain sign changes extended version. [r] is a rat or infty
   % or minfty.
   if r eq 'infty then
      num_sgnch for each e in sc collect aex_sgnatinfty(e,x)
   else if r eq 'minfty then 
      num_sgnch for each e in sc collect aex_sgnatminfty(e,x)
   else
      aex_sturmchainsgnch(sc,x,r);

procedure aex_sgnatinfty(ae,x);
   % Sign at infinity. [ae] has non-trivial lc or is simply null.
   begin scalar freeids;
      if aex_simplenullp ae then return 0;
      freeids := aex_freeids ae;
      if not freeids  then return aex_sgn ae;
      % from now on we have one free variable.
      if !*rlanuexdebug and car freeids neq x then
	 prin2 "***** aex_sgnatinfty: wrong main variable";
      if !*rlanuexdebug and length freeids > 1 then
	 prin2 "***** aex_sgnatinfty: not univariate";
      return aex_sgn aex_lc(ae,x);
   end;

procedure aex_sgnatminfty(ae,x);
   % Sign at minus infinity. [ae] has non-trivial lc or is simply null.
   begin scalar freeids;
      if aex_simplenullp ae then return 0;
      freeids := aex_freeids ae;
      if not freeids  then return aex_sgn ae;
      % from now on we have one free variable
      if !*rlanuexdebug and car freeids neq x then
	 prin2 "***** aex_sgnatminfty: wrong main variable";
      if !*rlanuexdebug and length freeids > 1 then
	 prin2 "***** aex_sgnatminfty: not univariate";
      if num_even aex_deg(ae,x) then
	 return aex_sgn aex_lc(ae,x);
      return (-1)*aex_sgn aex_lc(ae,x);
   end;

procedure aex_sgn(ae);
   % Remark: the case: ae has free ids, but is constant due to trivial
   % coefficients is not treated, although sgn would be sensible.
   % Possible optimization: use of aex_containment.
   begin scalar con,x,g,alpha,f,sc;      
      % semanticcheck
      if !*rlanuexdebug and (not (type_of ae eq 'aex) or aex_freeids ae) then
	 prin2t "***** aex_sgn: invalid argument";
      % ae is obviously rational %%% faster with _ratp
      if ratpoly_ratp aex_ex ae then
	 return ratpoly_sgn aex_ex ae;
      % possible optimization:
      if !*rlanuexsgnopt then <<
      	 con := aex_containment ae;
      	 if rat_less(rat_0(),iv_lb con) then return 1;
      	 if rat_less(iv_rb con,rat_0()) then return (-1);
      >>;
      % ae is algebraic and ae=(g(x),(x=f(y),etc))
      %x := caar ial; % flawed, if ctx is not minimal
      x := car ratpoly_idl aex_ex ae;
      % alpha := cdar ial; % flawed, if ctx is not minimal
      alpha := ctx_get(x,aex_ctx ae);
      g := aex_mklcnt aex_reduce aex_free(ae,x); %%% reduce somewhere else, eg bind
      if aex_simpleratp g then return ratpoly_sgn aex_ex g;
      if ofsf_anuexverbosep() and aex_deg(g,x)<=0 then prin2 "[aex_sgn:num!]";
      f := anu_dp alpha;
      f := aex_subrp(f,aex_mvar f,x); %unnecessary, aex_bind makes it already
      if !*rlanuexdebug and aex_sgn aex_lc(f,x) eq 0 then
	 prin2t "***** aex_sgn: f has trivial lc";
      if !*rlanuexdebug and aex_sgn aex_lc(g,x) eq 0 then
	  prin2t "***** aex_sgn: g has trivial lc";
      % sc is the  sturmchain for f(x), f'g(x)
      %%% optimization: aex_reduce after aex_diff
      sc := aex_sturmchain(f,aex_mult(aex_diff(f,x),g),x);
      return aex_sturmchainsgnch(sc,x,iv_lb anu_iv alpha)
	 - aex_sturmchainsgnch(sc,x,iv_rb anu_iv alpha)
   end;

procedure aex_pssqfree(f,x);
   % pseudo sqare-free part. [f] is an algebraic polynomial. Returns
   % an algebraic polynomial.
   if aex_deg(f,x) < 2 then f 
   else car aex_psquotrem(f,lastcar aex_stdsturmchain(f,x),x);

%procedure aex_psgcd(f,g,x);
%   % pseudo greatest common divisor. [f], [g] are algebraic
%   % polynomials with positive degree in [x]. Returns an algebraic
%   % polynomial.
%   %lastcar aex_sturmchain(f,g);
%   car aex_remseq({g,f},x);

%%% --- should be after exact arithmetic --- %%%

procedure sqfr_norm(f,x,y,palpha);
   % (RATPOLY,ID,ID,RATPOLY)->(NUM,RATPOLY,RATPOLY)
   begin scalar g,r; integer s,degree;
      s := 0; g := f;
      palpha := ratpoly_sub(palpha,x,y);
       repeat <<
	 r := ratpoly_resultant(palpha,g,y);
	 % ratpoly_deg works just if x is the leading kernel
	 degree := ratpoly_deg(ratpoly_gcd(r,ratpoly_diff(r,x)),x);
         if degree neq 0 then <<
	    s := s+1;
 	    g := ratpoly_sub(g,x,{'plus,x,{'minus,y}})
	 >>
         >> 
	 until degree = 0;
      return {s,g,r};
   end;

procedure aex_factorize(f,x); %%% rename to factors
   % Factors. [f] is a squarefree univariate alg. polynomial in [x].
   % Returns the list of non-constant factors of [f]. Remark: this
   % implements trager's alg_factor. Funthermore, if
   % [aex_simpleratpolyp] holds, [f] needs not to be squarefree.
   begin scalar tmp,y,alpha,s,g,r,l,aeg,aehi;
      if aex_simpleratpolyp f then <<
	 l := ratpoly_factorize aex_ex f;
	 % self-test done already in ratpoly_factorize.
	 return cdr for each rp_m in l collect aex_fromrp car rp_m;
      >>;
      tmp := car ctx_ial aex_ctx f; % y . alpha
      y := car tmp; alpha := cdr tmp; 
      %tmp := aex_sqfr_norm(f,x,y); % maybe in future...
      tmp := sqfr_norm(aex_ex f,x,y,aex_ex anu_dp alpha);
      s := car tmp; g := cadr tmp; r := caddr tmp;
      tmp := ratpoly_factorize r; %%%%%%%
      % throw away multiplicities and domain element
      l := cdr for each rp_m in tmp collect car rp_m; 
      %l := for each rp_m in l collect car rp_m; 
      aeg := aex_fromrp g; aeg := aex_bind(aeg,y,alpha); % g(x,alpha)
      l := for each hi in l collect <<
	 aehi := aex_fromrp hi; % h_i(x)
	 aehi := aex_gcd(aehi,aeg,x); % h_i(x,alpha)=gcd(h_i(x),g(x,alpha))
	 aeg  := aex_quot(aeg,aehi,x); 
	 aehi := aex_subrp(aehi,x,{'plus,x,{'times,s,y}}) 	    
      >>;
      if !*rlanuexdebug and not 
	aex_simpleratp aex_quot(f,aex_foldmult l,x) then
	 prin2t "***** aex_factorize: selftest failed (alg. case)";
      return l; %%% minimize every element first?
   end;

%%% --- exact arithmetic - with inv --- %%%

%procedure aex_quotrem(f,g,x);
%   % optimization: g is constant.
%   if null aex_freeids g then aex_mult(aex_inv g,f) . aex_0()
%   else aex_quotrem1(f,g,x);

procedure aex_quotrem(f,g,x);
   % f,g need to have non-trivial leading coefficient. Literatur: see
   % becker, weispfenning, kredel: groebner bases, p.81.
   % property: rr is always returned with non-trivial lc.
   begin scalar rr,gg,qq,an,bm,qqi; integer m,n;
      if aex_simplenullp g then
	 prin2t "***** aex_quotrem: g=0"; %%% return here 0 . f
      % optimization: g is constant.
      if null aex_freeids g then
	 return aex_mult(aex_inv g,f) . aex_0();
      if !*rlanuexdebug and aex_sgn aex_lc(f,x) eq 0 then
	 prin2t "***** aex_quotrem: first argument invalid";
      if !*rlanuexdebug and aex_sgn aex_lc(g,x) eq 0 then
	  prin2t "***** aex_quotrem: second argument invalid";
      rr := f; gg := g; qq := aex_0();
      n := aex_deg(rr,x); m := aex_deg(gg,x);
      while not aex_simplenullp rr and n >= m do <<
	 an := aex_lc(rr,x);
	 bm := aex_lc(gg,x);
	 qqi := aex_reduce aex_mult(aex_mult(an,aex_inv bm),
	    aex_xtothen(x,n-m)); % reduce added as optimization
	 rr := aex_mklcnt aex_minus(rr,aex_mult(qqi,gg)); %%% mit redukta
	 qq := aex_add(qq,qqi);
	 n := aex_deg(rr,x); m := aex_deg(gg,x);
      >>;
      if !*rlanuexdebug and not aex_nullp aex_minus(f,aex_add(aex_mult(qq,g),rr))
 	    then prin2t "aex_quotrem: selftest failed";
      return qq . rr
   end;

procedure aex_quotremtest(f,g,x);
   % should result in 0
   aex_minus(f,aex_add(aex_mult(car qr,g),cdr qr))
      where qr=aex_quotrem(f,g,x);

procedure aex_quot(f,g,x);
   car aex_quotrem(f,g,x);

procedure aex_rem(f,g,x);   
   %if null aex_freeids g then aex_0() else cdr aex_quotrem1(f,g,x);
   cdr aex_quotrem(f,g,x);

procedure aex_gcdext(a,b,x);
   aex_gcdext1(a,b,x,!*rlanuexgcdnormalize);

procedure aex_gcdext1(a,b,x,gcdnormalize);
   % extended euclidean algorithm, see becker, weispfenning, kredel:
   % groebner bases, p.83.
   begin scalar aa,bb,ss,tt,uu,vv,qr,ss1,tt1,tmp;      
      aa := a; bb := b;
      ss := aex_1(); tt := aex_0();
      uu := aex_0(); vv := aex_1();
      while not aex_simplenullp bb do << %%% simplenullp should suffice
	 qr := aex_quotrem(aa,bb,x);
	 aa := bb; bb := cdr qr; % attention: cadr with psquotrem
	 ss1 := ss; tt1 := tt;
	 ss := uu; tt := vv;
	 uu := aex_minus(ss1,aex_mult(car qr,uu));
	 vv := aex_minus(tt1,aex_mult(car qr,vv));
      >>;
      if gcdnormalize then <<
	 tmp := aex_inv aex_lc(aa,x); aa := aex_mult(aa,tmp);
	 ss := aex_mult(ss,tmp); tt := aex_mult(tt,tmp);
      >>;      
      if !*rlanuexdebug and not
	 aex_nullp aex_minus(aa,aex_add(aex_mult(ss,a),aex_mult(tt,b))) then
	 prin2t "***** aex_gcdext: selftest failed";
      return {aa,ss,tt}
   end;

procedure aex_gcd(a,b,x);
   begin scalar d;
      d := car aex_gcdext1(a,b,x,nil);
      % optimizatfion: if the gcd is not a poly, then it's 1
      if aex_deg(d,x)<1 then d := aex_1();
      if !*rlanuexdebug and aex_nullp aex_lc(d,x) then
	 prin2 "*** aex_gcd: lc non-trivial";
      return d;
   end;

procedure aex_gcd1(a,b,x,gcdnormalize);
   car aex_gcdext1(a,b,x,gcdnormalize);

procedure aex_gcdexttest(a,b,x);
   % should result in 0
   aex_minus(car ddsstt,
      aex_add(aex_mult(cadr ddsstt,a),aex_mult(caddr ddsstt,b)))
      	 where ddsstt = aex_gcdext(a,b,x);

procedure aex_pairwiseprime(ael,x); % needs testing
   % pairwise prime. ael is a list of AEX with lc non-trivial. Returns
   % a list of AEX with lc non-trivial.
   for each te in
      aex_tgpairwiseprime(for each ae in ael collect tag_(ae,nil),x) collect
	 tag_object te;
%   begin scalar pprestlist,tmp;
%      if length ael <= 1 then return ael;
%      pprestlist := aex_pairwiseprime(cdr ael,x);
%      tmp := aex_pairwiseprime1(car ael,pprestlist,x);
%      % lets assume aex_quot returns something with lc non-trivial.
%      return if not aex_simplenumberp tmp then
%	 tmp . pprestlist else pprestlist
%   end;

procedure aex_pairwiseprime1(ae1,ae2l,x); % unused
   % makes ae1 prime to ae2l.
   %   << for each ae2 in ae2l do ae1 := aex_quot(ae1,aex_gcd(ae1,ae2,x),x);
   %      ae1
   %   >>; 
   << while ae2l and not aex_simplenullp ae1 do << %%% simplenumberp !?
      ae1 := aex_quot(ae1,aex_gcd(ae1,car ae2l,x),x);
      ae2l := cdr ae2l;
   >>; ae1 >>;

procedure aex_tgpairwiseprime(ael,x);
   % pairwise prime. ael is a list of AEX with lc non-trivial. Returns
   % a list of AEX with lc non-trivial.
   begin scalar pprestlist,tmp;
      if length ael <= 1 then return ael;
      pprestlist := aex_tgpairwiseprime(cdr ael,x);
      tmp := aex_tgpairwiseprime1(car ael . pprestlist,x);
      % lets assume aex_quot returns something with lc non-trivial.
      return if not aex_simplenumberp tag_object car tmp then
	 tmp else cdr tmp
   end;

procedure aex_tgpairwiseprime1(ael,x);
   % makes ae1 prime to ae2l.
   begin scalar ae1,ae2,ae2l,ae2lnew,g,n1,n2;
      ae1 := car ael; ae2l := cdr ael; ae2lnew := nil;
      while ae2l and not aex_simplenumberp tag_object ae1 do <<
	 ae2 := car ae2l;
	 g := aex_gcd(tag_object ae1,tag_object ae2,x);
	 n1 := aex_deg(tag_object ae1,x);
      	 ae1 := tag_(aex_quot(tag_object ae1,g,x),tag_taglist ae1);
	 n2 := aex_deg(tag_object ae1,x);
	 if n1 > n2 then
	    ae2 := tag_(tag_object ae2,union(tag_taglist ae1,tag_taglist ae2));	 
 	 ae2lnew := ae2 . ae2lnew;
      	 ae2l := cdr ae2l;
      >>; 
      return ae1 . ae2lnew;
   end;
   
   
procedure aex_sqfree(f,x);
   % pseudo sqare-free part. [f] is an algebraic polynomial. Returns
   % an algebraic polynomial.
   % question: what if f not reduced?
   if aex_deg(f,x) < 2 then f 
      else car aex_quotrem(f,aex_gcd(f,aex_diff(f,x),x),x);
      
procedure aex_inv ae;
   % invert a constant, not-null polynomial.
   % ae represents an algebraic number, that is not null.
   %%% the case of a purely rational ae occurs very often. avoid the progn by splitting into two functions.
   begin scalar ia,x,alpha,g,f,d,f1,drs,tmp;
      % ae is obviously a rational
      if ratpoly_ratp aex_ex ae then
	 return aex_fromrp ratpoly_quot(ratpoly_1(),aex_ex ae);
      % ae is an constant algebraic polynomial
      ia := car ctx_ial aex_ctx ae; % (x . (anu f iv))
      x := car ia;
      alpha := cdr ia;
      g := aex_free(ae,x);
      f := anu_dp alpha;
      d := car aex_gcdext(f,g,x);
      %%% d := car aex_gcd(f,g,x); % why segmentation violation?
      f1 := car aex_quotrem(f,d,x);
      % apply ext. euclid. algorithm to f1,g, gives: 1=rf1+sg
      drs := aex_gcdext(f1,g,x);
      % caveat: car drs is non-zero rational, but i.g. <> 1
      if !*rlanuexgcdnormalize then
	 tmp := aex_bind(aex_mult(car drs,caddr drs),x,alpha) %%% here alphanew with f1
      else
      	 tmp := aex_bind(aex_mult(aex_inv car drs,caddr drs),x,alpha);
      if !*rlanuexdebug and
	 not aex_nullp aex_minus(aex_1(),aex_mult(ae,tmp)) then
	 rederr "aex_inv: selftest failed";
      return tmp;
   end;

procedure aex_invtest ae;
   % should result in 0
   aex_minus(aex_1(),aex_mult(ae,aex_inv ae));

procedure aex_linsolv(ae,x);
   % [x] is the only free variable in ae.
   <<
      if !*rlanuexdebug and not (aex_freeids ae equal {x}) then
         prin2t "aex_linsolv: invalid argument";
      aex_mult(aex_inv aex_lc(ae,x),aex_neg aex_red(ae,x))
   >>;

%%% --- root isolation --- %%%

procedure aex_coefl(ae,x);
   if aex_mvaroccurtest(ae,x) then
      aex_lc(ae,x) . aex_coefl(aex_red(ae,x),x)
   else
      {ae};

procedure aex_coefdegl(ae,x);
   % coefficients and degree list. ae is a polynomial in x.
   if aex_mvaroccurtest(ae,x) then
      (aex_lc(ae,x) . aex_deg(ae,x)) . aex_coefdegl(aex_red(ae,x),x)
   else
      {aex_lc(ae,x) . 0};

procedure aex_fromcoefdegl(cfdgl,x);
   begin scalar ae;
      ae := aex_0();
      for each cd in cfdgl do
	 ae := aex_add(ae,aex_mult(car cd,aex_xtothen(x,cdr cd)));
      return ae
   end;

procedure aex_coefdegltest(ae,x);
   aex_nullp aex_minus(ae,aex_fromcoefdegl(aex_coefdegl(ae,x),x));
	 
procedure aex_containment(ae);
   % Algebraic expression containment. [ae] is an AEX. Returns an
   % interval.  the algebraic number represented by ae is contained in
   % the interval, and the interval is regarded as closed.
   begin scalar ia,cfdgl,ctac,ivl,r;
      % coefficient and degree list, containment of a_c, interval list
      if !*rlanuexdebug and aex_freeids ae then
 	 prin2t "***** aex_containment: invalid argument";
      if null aex_boundids ae then <<
	 r := ratpoly_torat aex_ex ae;
	 return iv_mk(r,r);
      >>;
      % there is a bound variable now
      ia := car ctx_ial aex_ctx ae;
      ctac := anu_iv cdr ia;
      %ae := aex_free(ae,car ia);
      cfdgl := aex_coefdegl(aex_free(ae,car ia),car ia);
      if !*rlanuexdebug and not aex_coefdegltest(ae,car ia) then
	 prin2t "***** aex_containment: aex_coefdegltest failed";
      ivl := for each cfdg in cfdgl collect
	 iv_mult(aex_containment car cfdg,iv_tothen(ctac,cdr cfdg));
      return iv_mapadd ivl
   end;

procedure aex_distinguishpositivefromzero(ae,iv);
   % Algebraic expression distinguish positive from zero. [ae] is an
   % AEX(c,c), [iv] an intervall containing the positive algebraic number
   % represented by $ae$. Returns a RAT, which lies in $[0,ae]$
   begin scalar rb;
      rb := rat_mult(iv_rb iv,rat_mk(1,2));
      %%% repeat ... until saves a sign test
      while (aex_sgn aex_addrat(ae,rat_neg rb) neq 1) do
	 rb := rat_mult(rb,rat_mk(1,2));
      return rb
   end;
   
procedure aex_distinguishfromzero(ae,iv);
   % Algebraic expression distinguish from zero. [ae] is an AEX(c,c),
   % [iv] an intervall containing the algebraic number represented by
   % $ae$. Returns a RAT, which lies in $[0,ae]$ or $[ae,0]$,
   % respectively.
   begin scalar sgnae,r;
      sgnae := aex_sgn ae;
      if eqn(sgnae,0) then
	 return rat_0();
      r := if eqn(sgnae,1) then
	 aex_distinguishpositivefromzero(ae,iv)
      else
	 aex_distinguishpositivefromzero(aex_neg ae,iv_neg iv);
      return if eqn(sgnae,1) then r else rat_neg r
   end;

procedure aex_cauchybound(ae,x);
   % Algebraic expression cauchy bounds. [ae] is an univariate alg.
   % polynomial in [x] with non-trivial leading coefficient. Returns
   % an non-negative RAT, the minimum of the cauchy bounds of ae.
   begin scalar cfl,am,ctam,nb,ctl,ml,m,n,minabsam,cb,aesc;      
      if !*rlanuexdebug and aex_simplenullp aex_lc(ae,x) then
	 prin2t "***** aex_cauchybound: argument has trivial lc";
      if aex_deg(ae,x) <= 0 then return rat_1(); % avoids trivial sturmchains
      cfl := aex_coefl(ae,x); % has at least length 1
      am := car cfl;
      ctam := aex_containment am;
      if iv_containszero ctam then <<
	 if ofsf_anuexverbosep() then
	    prin2 "+++ aex_cauchybound: iteration case +++";
   	 nb := aex_distinguishfromzero(am,ctam);
	 ctam := if rat_less(nb,rat_0()) then
	    iv_mk(iv_lb ctam,nb)
	 else
	    iv_mk(nb,iv_rb ctam)
      >>;
      % now ctam is a containing interval for a_m without 0. 
      ctl := for each cf in cdr cfl collect aex_containment cf;
      ml := for each iv in ctl collect iv_maxabs iv;
      minabsam := iv_minabs ctam;
      m := rat_max(rat_1(),rat_quot(rat_mapadd ml,minabsam));
      n := if null ml then
	 rat_1()
      else
	 rat_add(rat_1(),
	    rat_mapmax for each a in ml collect rat_quot(a,minabsam));
      cb := rat_min(m,n);
      aesc := aex_stdsturmchain(ae,x);
      if !*rlanuexdebug and not (aex_sturmchainsgnch(aesc,x,cb) eq
	 aex_stchsgnch(aesc,x,'infty)) then
	 prin2 "aex_cauchybound: problem at right border";
      if !*rlanuexdebug and not (aex_stchsgnch(aesc,x,'minfty) eq
	 aex_sturmchainsgnch(aesc,x,rat_minus(rat_neg cb,rat_1()))) then
	 prin2 "aex_cauchybound: problem at left border";
      return cb;
   end;


procedure aex_findrootsoflist(ael,x);
   aex_findroots(aex_foldmult ael,x);

procedure aex_findroots(ae,x);
   % Algebraic expression find roots. [ae] is an AEX(c,c+1). Returns a
   % list of ANU(c+1). If [ae]'s ldeg is not positive, the empty list
   % will be returned. The interval to start with has to be slightly
   % enlarged.
   begin scalar cb,rootlist;
      if aex_deg(ae,x) < 1 then return nil;
      cb := rat_add(aex_cauchybound(ae,'x),rat_1()); % necessary to add 1.
      %cb := 256 . 1; %%% later cauchybound!!!
      rootlist := aex_findrootsiniv1(ae,x,iv_mk(rat_neg cb,cb),
	 aex_stdsturmchain(ae,x));
      return rootlist
   end;

procedure aex_findrootsiniv(ae,x,iv);
   % Algebraic expression find roots in interval. [ae] is an
   % AEX(c,c+1), [iv] is an INT. The ldeg of ae has to be positive,
   % i.e. [ae] must not represent a constant polynomial. Returns a
   % ordered list of ANU(c+1).
   aex_findrootsiniv1(ae,x,iv,aex_stdsturmchain(ae,x));

procedure aex_findrootsiniv1(ae,x,iv,sc);
   % Algebraic expression find roots in interval. [ae] is an
   % AEX(c,c+1), [iv] is an INT, [sc] is ae's sturmchain. Returns a
   % ordered list of ANU(c+1). In particular, the ldeg of ae is
   % positive, i.e. [ae].
   begin scalar lb,rb,sclb,scrb,m,ml,mr,retl,r;
      lb := iv_lb iv;
      rb := iv_rb iv;
      sclb := aex_sturmchainsgnch(sc,x,lb);
      if sclb = 0 then return nil;
      scrb := aex_sturmchainsgnch(sc,x,rb);
      if sclb - scrb = 0 then
	 return nil;
      if sclb - scrb = 1 then
	 return {anu_mk(ae,iv)};      
      m := rat_quot(rat_add(lb,rb),rat_fromnum 2);
      ml := mr := m;
      if aex_sgn aex_subrat1(ae,x,m) eq 0 then <<
	 r := aex_isoroot(ae,x,m,rat_mult(rat_minus(rb,lb),rat_mk(1,4)),sc);
	 ml := rat_minus(m,r);
	 mr := rat_add(m,r)
      >>;
      retl := aex_findrootsiniv1(ae,x,iv_mk(mr,rb),sc); 
      if not rat_eq(ml,mr) then
	 retl := anu_mk(ae,iv_mk(ml,mr)) . retl;
      retl := append(aex_findrootsiniv1(ae,x,iv_mk(lb,ml),sc),retl);% nconc!!!
      return retl
   end;

procedure aex_isoroot(ae,x,m,r,sc);
   % Algebraic expression isolate root. [ae] is an AEX(c,c+1), [m],
   % [r] are RAT, [sc] is [ae]'s sturmchain. Returns a RAT $s$ such
   % that [ae] has no root within the Interval $[m-s,m+s]$. In
   % particular, the ldeg of ae is positive, i.e. [ae].
   << while not (aex_atrat0p(ae,x,rat_minus(m,r)) and
      aex_atrat0p(ae,x,rat_add(m,r)) and
	 aex_sturmchainsgnch(sc,x,rat_minus(m,r))-
	    aex_sturmchainsgnch(sc,x,rat_add(m,r)) eq 1) do
      	       r := rat_mult(r,rat_mk(1,2));
      r
   >>;

procedure aex_atrat0p(ae,x,r);
   % Zero at rational point predicate. Check if [ae] is zero at [x].
   aex_sgn aex_subrat1(ae,x,r) neq 0; %%% eq 0?

%%% --- global root isolation --- %%%

%procedure aex_globalrootisolationinit(ff,x);
%   % [ff] is a (maybe empty) list of AEX (univariate alg.
%   % polynomials). [ff] is factorized, i.e. each root occures only in
%   % one polynomial.
%   {nil,nil,for each f in ff collect f . aex_stdsturmchain(f,x)};

procedure aex_nextroot(rip,x);
   % [rip] is of form {rootl,ivl,pscl}, where rootl is a list of ANU,
   % ivl a list of IV, pscl a list of dotted pais p . sc where p ia an
   % AEX (a univariate alg. polynomial) and sc is the corresponding
   % sturmchain. Returns t, if a root is found, nil otherwise. The
   % argument is changed in-place.
   begin scalar rootfound,cb;      
      while not rootfound and rip_pscl rip do <<
	 % pscl is not empty. if ivl is empty, mk new iv with cb.
	 while null rip_ivl rip and rip_pscl rip do <<
	    cb := rat_add(
	       aex_cauchybound(tag_object caar rip_pscl rip,x),rat_1());
	    rip_addivl(rip,iv_minuslist(iv_mk(rat_neg cb,cb),
	       for each a in rip_rootlnotags rip collect anu_iv a));
	    % maybe after minuslist there are no intervals left
	    if null rip_ivl rip then rip_poppscl rip;
	 >>;
	 % care for the case that there was no poly left with a root
	 if rip_pscl rip then << % there is at least one interval and one poly
	    if !*rlanuexdebug and (null rip_ivl rip or null rip_pscl rip) then
	       prin2t "***** aex_nextroot: no interval or no polynomial";
	    if aex_nextroot1(rip,x) then rootfound := t;
	    if null rip_ivl rip then rip_poppscl rip
	 >>
      >>;
      if !*rlanuexdebug and rootfound then
	 anu_check tag_object car rip_rootl rip;
      if rootfound then return car rip_rootl rip else return nil
   end;

procedure aex_nextroot1(rip,x);
   % There is an interval and there is a poly. this function attempts
   % to isolate at most one root. A root might be added to rootl and
   % intervals might be added to ivl, but pscl is not popped. Returns
   % t, if a root was found. Possible Optimazions: tagged intervals,
   % use anu_fromrat if a rat. root is found. rip contains tagged
   % polynomials.
   begin scalar iv,lb,rb,sc,sclb,scrb,m,ml,mr,r;
      % remove iv from ivl
      iv := rip_popivl rip; lb := iv_lb iv; rb := iv_rb iv;
      sc := cdar rip_pscl rip;
      sclb := aex_sturmchainsgnch(sc,x,lb);
      if sclb = 0 then return nil;
      scrb := aex_sturmchainsgnch(sc,x,rb);
      if sclb - scrb = 0 then return nil;
      if sclb - scrb = 1 then << 
	 rip_addroot(rip,
	    tag_(aex_refinewrt1(anu_mk(tag_object caar rip_pscl rip,iv),
	       cdr rip_psclnotags rip,cdar rip_pscl rip),
	       tag_taglist caar rip_pscl rip));
	 return t
      >>;
      % there are at least two roots.
      m := rat_quot(rat_add(lb,rb),rat_fromnum 2);
      ml := mr := m;
      if aex_sgn aex_subrat1(tag_object caar rip_pscl rip,x,m) eq 0 then <<
	 r := aex_isoratroot(m,rat_mult(rat_minus(rb,lb),rat_mk(1,4)),
	    rip_psclnotags rip,x); % cdr rip_pscl rip would be wrong
	 ml := rat_minus(m,r); mr := rat_add(m,r);
	 %rip_addroot(rip,anu_mk(caar rip_pscl rip,iv_mk(ml,mr)))
	 % instead, the following is only a very small optimization(1-2%):
	 rip_addroot(rip,tag_(anu_fromrat(x,m,iv_mk(ml,mr)),
	    tag_taglist caar rip_pscl rip))
      >>;
      rip_addivl(rip,{iv_mk(mr,rb)});
      rip_addivl(rip,{iv_mk(lb,ml)});
      return not rat_eq(ml,mr);
   end;

procedure aex_isoratroot(m,r,pscl,x);
   % [m] is a rational root of [caar pscl]. Returns a r such that no
   % sturmchain from cdr scl has a sign change in (m-r,m+r), and such
   % that car scl has 1 sign change in (m-r,m+r). No tags in here.
   << % Refine r until p's sc has only 1 sgn change.
      while aex_atratnullp(caar pscl,x,rat_minus(m,r)) or
      aex_atratnullp(caar pscl,x,rat_minus(m,r)) or
	 aex_deltastchsgnch(cdar pscl,x,
	    iv_mk(rat_minus(m,r),rat_add(m,r))) > 1 do
	       r := rat_mult(r,rat_mk(1,2));
      pscl := cdr pscl;
      % Refine r until the other sc's have no sgn changes.
      for each psc in pscl do
      while aex_atratnullp(car psc,x,rat_minus(m,r)) or
	 aex_atratnullp(car psc,x,rat_minus(m,r)) or
	    aex_deltastchsgnch(cdr psc,x,
	       iv_mk(rat_minus(m,r),rat_add(m,r))) > 0 do
		  r := rat_mult(r,rat_mk(1,2));
      r
   >>;

procedure aex_refinewrt1(alpha,pscl,scalpha);
   % Refine [alpha] st. the sturm chain [scl] has no sign change in
   % alpha's isolating interval. scalpha is the sturm chain for
   % alpha's defining polynomial. No tags in here.
   << for each psc in pscl do %%% null test necessary
      while aex_atratnullp(car psc,x,iv_lb anu_iv alpha) or
	 aex_atratnullp(car psc,x,iv_rb anu_iv alpha) or
	    aex_deltastchsgnch(cdr psc,x,anu_iv alpha) > 0 do
	 anu_refine1ip(alpha,scalpha);
      alpha
   >> where x=aex_mvar anu_dp alpha;

procedure aex_deltastchsgnch(sc,x,iv);
   % delta sturm chain sign changes in an interval
   begin scalar sclb,scrb;
      sclb := aex_sturmchainsgnch(sc,x,iv_lb iv);
      if sclb eq 0 then return 0;
      scrb := aex_sturmchainsgnch(sc,x,iv_rb iv);
      return sclb - scrb;
   end;

procedure aex_atratnullp(ae,x,r);
   aex_sgn aex_subrat1(ae,x,r) eq 0;

% - little helpers for global root isolation - %
% rip stands for root list, interval list, p.sc list

procedure psc_sortdesc(psc1,psc2);
   % Sort function descending. psc1 and psc2 are list of dotted pairs
   % p.sc phere p is a ratpoly and sc is a sturmchain. To be used by
   % generic_sort, yiels a sorted list with descending length of sturm
   % chains.
   sgn(length cdr psc2 - length cdr psc1);

procedure psc_sortasc(psc1,psc2);
   % Sort function asscending. psc1 and psc2 are list of dotted pairs
   % p.sc phere p is a ratpoly and sc is a sturmchain. To be used by
   % generic_sort, yiels a sorted list with ascending length of sturm
   % chains.
   sgn(length cdr psc1 - length cdr psc2);

procedure rip_init(ael,x);
   % [ael] is a (maybe empty) list of TAG(AEX) (tagged univariate alg.
   % polynomials). [ael] is factorized, i.e. each root occures only in
   % one polynomial.
   {nil,nil,for each ae in ael collect
      ae . aex_stdsturmchain(tag_object ae,x)};
%      generic_sort(for each ae in ael collect ae . aex_stdsturmchain(ae,x),
%	 'psc_sortasc)};

procedure rip_rootl rip;   
   car rip;

procedure rip_rootlnotags rip;   
   for each r in car rip collect tag_object r;

procedure rip_ivl rip;
   cadr rip;

procedure rip_pscl rip;
   caddr rip;

procedure rip_psclnotags rip;
   for each psc in caddr rip collect tag_object car psc . cdr psc;

procedure rip_putivl(rip,ivl);
   cadr rip := ivl;

procedure rip_putpscl(rip,pscl);
   caddr rip := pscl;

procedure rip_addroot(rip,root);
   car rip := root . car rip;

procedure rip_addivl(rip,ivl);
   cadr rip := nconc(ivl,rip_ivl rip);

procedure rip_poppscl rip;
   caddr rip := cdr caddr rip;

procedure rip_popivl rip;
   <<cadr rip := cdr cadr rip; carivl>> where carivl=car rip_ivl rip;

%procedure aex_ivlfromrootl rootl;
%   % interval list from root list. [rootl] is a list of ANU. 
%   for each a in rootl collect anu_iv a;

% module anu;

procedure anu_mk(ae,iv);
   {'anu,ae,iv};

procedure anu_dp(a);
   cadr a;

procedure anu_iv(a);
   caddr a;

procedure anu_putiv(a,iv);
   % Algebraic number get interval. [a] is an ANU. [iv] is an
   % INT. Changes the isolating interval in a nonconstructively.
   % The returned value is not of interest.
   caddr a := iv;

procedure anu_print a;
   <<
      prin2 "("; aex_print anu_dp a; prin2 ", ";
      iv_print anu_iv a; prin2 ")";
   >>;

procedure anu_check(a);
   % Algebraic number check.
   begin scalar dp,x,l,r,s,valid;
      valid := t;
      dp := anu_dp a;
      x := aex_freeids dp; % tmp
      if length x neq 1 then
	 prin2t "***** anu_check: def. poly corrupt";
      x := car x; l := iv_lb anu_iv a; r := iv_rb anu_iv a;
      s := aex_stdsturmchain(dp,aex_mvar dp);
      if aex_nullp aex_subrat1(dp,x,l) then <<
	 valid := nil;
      	 prin2t "***** anu_check: def. poly null at left bound";
      >>;
      if aex_nullp aex_subrat1(dp,x,r) then <<
	 valid := nil;
      	 prin2t "***** anu_check: def. poly null at right bound";
      >>;
      if aex_deltastchsgnch(s,x,anu_iv a) neq 1 then <<
	 valid := nil;
      	 prin2t "***** anu_check: no root";
      >>;
      return valid;
   end;

procedure anu_fromrat(x,r,iv);
   anu_mk(aex_fromrp ratpoly_mklin(x,r),iv);

procedure anu_refine1ip(a,s);
   % Algebraic number refine in place . [a] is an ANU, [s] is a
   % sturmchain.  Returns an [a] with smaller isolating intervall.
   % Remark: 1000 replaced by 4.
   begin scalar x,iv,lb,rb,m,scm;
      x := aex_mvar anu_dp a;
      iv := anu_iv a;
      lb := iv_lb iv; rb := iv_rb iv;
      m := rat_quot(rat_add(lb,rb),rat_mk(2,1));
      if aex_sgn aex_subrat1(anu_dp a,x,m) = 0 then <<
	 anu_putiv(a,iv_mk(
	    rat_minus(m,rat_mult(rat_mk(1,4),rat_minus(m,lb))),
	    rat_add(m,rat_mult(rat_mk(1,4),rat_minus(m,lb)))));
	 return a;
      >>;
      scm := aex_sturmchainsgnch(s,x,m);
      if (aex_sturmchainsgnch(s,x,lb)-scm) = 1 then <<
	 anu_putiv(a,iv_mk(lb,m));
	 return a;
      >>;
      anu_putiv(a,iv_mk(m,rb));
      return a
   end;
   
procedure sf_idl f;
   % if there is a main variable, then it will be the car.
   if not domainp f then
      mvar f . setunion(sf_idl lc f,lto_setminus(sf_idl red f,{mvar f}));

!#if (and (not (memq 'psl lispsystem!*)) (not (memq 'csl lispsystem!*)))
   procedure intersection(ss1,ss2);
      lto_setminus(ss1,lto_setminus(ss1,ss2));
!#endif
      
%procedure setunion(ss1,ss2);
%   append(lto_setminus(ss1,ss2),ss2);

procedure idorder();
   begin scalar idorderv;
      idorderv := setkorder nil;
      setkorder idorderv;
      return idorderv
   end;

endmodule;  % ofsfanuex

end;  % of file
