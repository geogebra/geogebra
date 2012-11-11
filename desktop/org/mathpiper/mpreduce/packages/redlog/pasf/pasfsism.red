% ----------------------------------------------------------------------
% $Id: pasfsism.red 1815 2012-11-02 13:20:27Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2003-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(pasf_sism_rcsid!* pasf_sism_copyright!*);
   pasf_sism_rcsid!* :=
      "$Id: pasfsism.red 1815 2012-11-02 13:20:27Z thomas-sturm $";
   pasf_sism_copyright!* :=
      "Copyright (c) 2003-2009 A. Dolzmann. A. Seidl and T. Sturm"
>>;

module pasfsism;
% Presburger arithmetic standard form smart simplification. Submodule of PASF.

procedure pasf_smwupdknowl(op,atl,knowl,n);
   % Presburger arithmetic standard form update knowledge. [op] is an
   % operator; [atl] is the list of atomic formulas to add to the knowledge;
   % [knowl] is a knowledge; [n] is the level. Returns modified knowledge.
   if !*rlsusi then
      cl_susiupdknowl(op,atl,knowl,n)
   else
      cl_smupdknowl(op,atl,knowl,n);

procedure pasf_smwrmknowl(knowl,v);
   % Presburger arithmetic standard form remove variable from the
   % knowledge. [knowl] is a knowledge; [v] is the variable to remove. Returns
   % modified knowledge.
   if !*rlsusi then
      pasf_susirmknowl(knowl,v)
   else
      cl_smrmknowl(knowl,v);

procedure pasf_smwcpknowl(knowl);
   % Presburger arithmetic standard form copy knowledge. [knowl] is a
   % knowledge. Returns a copy of the knowledge.
   if !*rlsusi then
      cl_susicpknowl(knowl)
   else
      cl_smcpknowl(knowl);

procedure pasf_smwmkatl(op,knowl,newknowl,n);
   % Presburger arithmetic standard form make atomic formula list. [op] is an
   % operator; [knowl] is a knowledge; [newknowl] is a knowledge; [n] is the
   % current level. Returns an atomic formula list. For detailed documentation
   % refer to clsimpl.red.
   if !*rlsusi then
      cl_susimkatl(op,knowl,newknowl,n)
   else
      cl_smmkatl(op,knowl,newknowl,n);

procedure pasf_susirmknowl(knowl,v);
   % Presburger arithmetic standard form remove knowledge. [knowl] is a
   % knowledge; [v] is a variable. Returns a knowledge. Removes all
   % information about [v] from [knowl].
   for each p in knowl join
      if v memq pasf_varlat car p then nil else {p};

procedure pasf_susibin(old,new);
   % Presburger arithmetic standard form susi binary smart
   % simplification. [old] is a LAT; [new] is a LAT. Returns 'false or a
   % SUSIPROG.
   pasf_susibinad(old,new);

procedure pasf_susibinad(old,new);
   % Presburger standard form additive smart simplification. [old] is the old
   % atomic formula in the theory; [new] is the new atomic formula
   % found. Returns a SUSIPROG that simplifies the formula.
   begin scalar od,nd,level,olevel,kn,ko;
      level := cl_susiminlevel(cdr old,cdr new);
      olevel := cdr old;
      old := car old;
      new := car new;
      % Check for truth value of the level formula
      if new eq 'false then
	 return 'false;
      if new eq 'true then
	 return {'(delete . T)};
      % Equal left handsides simplification
      if pasf_arg2l old = pasf_arg2l new then
	 return pasf_susibineq(pasf_arg2l old,pasf_op old,pasf_op new,level);
      % Decomposing both atomic formulas for additive simplification
      od := pasf_dec pasf_arg2l old;
      nd := pasf_dec pasf_arg2l new;
      if car od = car nd then
	 % Equal parametric parts
	 return pasf_susibinord(
	    pasf_op old,car od,if cdr od then cdr od else 0,
	    pasf_op new,car nd,if cdr nd then cdr nd else 0,level);
      ko := kernels car od;
      kn := kernels car nd;
      % Integer substitution
      if pasf_op old eq 'equal and null cdr ko and car ko memq kn then
	 return pasf_susibinad1(pasf_subfof1(new,car ko,negf cdr od),level,t);
      if pasf_op new eq 'equal and null cdr kn and car kn memq ko then
	 return pasf_susibinad1(pasf_subfof1(old,car kn,negf cdr nd),level,nil);
      return nil
   end;

procedure pasf_susibinad1(sb,level,flag);
   begin scalar ssb;
      ssb := pasf_simplat1(sb,nil);
      if rl_op ssb eq 'and then
	 return {'delete . flag,
	    for each at in rl_argn ssb collect ('add . (at . level))};
      if rl_cxp rl_op ssb then
	 ssb := pasf_simplat1(sb,nil) where !*rlsifac=nil;
      return {'delete . flag,'add . (ssb . level)}
   end;

procedure pasf_susibineq(u,oop,nop,level);
   % Presburger arithmetic standard form smart simplification with equal left
   % handside terms. [u] is the (common) left handside term; [oop] is the old
   % operator in the theory; [nop] is the new operator in the found atomic
   % formula; [level] is the recursion level of the new found atomic
   % formula. Returns a SUSIPROG that simplifies the formula.
   begin scalar w;
      % Congruences with different moduli
      if pairp oop and pairp nop and cdr oop neq cdr nop then
	 return pasf_susibineqcong(u,oop,nop,level);
      % ASSUMPTION: A congruence is never in the output of pasf_smeqtable
      w := pasf_smeqtable(
	 if pairp oop then car oop else oop,
	 if pairp nop then car nop else nop);
      if car w eq nil then
     	 % Nothing can be done
	 return nil
      else if car w eq 'false then
      	 % Contradiction found
	 return 'false
      else if eqn(car w,1) then
	 % Remove new atomic formula from the level
 	 return {'(delete . T)}
      else if eqn(car w,2) then
 	 % Remove old atomic formula from the theory, add new atomic
 	 % formula to the knowledge
	 return {'(delete . nil)}
      else if eqn(car w,3) then
	 % Remove old atomic formula from the theory, remove new
	 % atomic formula from the level, add modified atomic formula to
	 % the level
	 return {'(delete . nil), '(delete . T),
	    ('add . (pasf_0mk2(cdr w, u) . level))}
      else if eqn(car w,4) then
	 % Remove new atomic formula from the level, add modified
	 % atomic formula to the level
	 return {'(delete . T),
	    ('add . (pasf_0mk2(cdr w, u) . level))}
      else
	 % Remove old atomic formula from the theory, add modified
	 % atomic formula to the level
	 return {'(delete . nil),
	    ('add . (pasf_0mk2(cdr w, u) . level))}
   end;

procedure pasf_susibineqcong(u,oop,nop,level);
   % Presburger arithmetic standard form smart equal simplification with equal
   % left handside terms in congruences with different moduli. [u] is the
   % (common) left handside term; [oop] is the old operator in the theory;
   % [nop] is the new operator in the found atomic formula; [level] is the
   % recursion level of the new found atomic formula. Returns a SUSIPROG that
   % simplifies the formula.
   begin scalar n,m,mo,atf;
      n := cdr oop;
      m := cdr nop;
      % For parametric moduli nothing yet
      if null domainp n or null domainp m then return nil;
      % Both formulas are congruences
      if car oop eq 'cong and car nop eq 'cong then
	 return{'(delete . nil),'(delete . T),
	    ('add . (pasf_0mk2(pasf_mkop('cong,lcm(m,n)),u) . level))};
      % Old formula is a congruence and new is a incongruence
      if car oop eq 'cong and car nop eq 'ncong then <<
	 if (nil and m = 2*n) then
	    return{'(delete . T),('delete . nil),('add .
	       (pasf_0mk2(pasf_mkop('ncong,m),addf(u,negf n)) . level))}
	 else <<
	    % Making sure changes are really applied
	    mo := pasf_susibineqcong1(m,n);
	    if mo neq m then <<
	       atf := pasf_simplat1(pasf_0mk2(pasf_mkop('ncong,mo),u),nil)
		  where !*rlsifac=nil;
	       if atf eq 'false then
		  return atf
	       else if atf eq 'true then
		  return nil
	       else
	       	  return{'(delete . T),('add . (atf . level))}
	    >> else
	       return nil
	 >>
      >>;
      % Old formula is an incongruence and new is a congurence
      if car oop eq 'ncong and car nop eq 'cong then <<
	 if (nil and n = 2*m) then
	    return{'(delete . nil),'(delete . T),('add .
	       (pasf_0mk2(pasf_mkop('ncong,n),addf(u,negf m)) . level))}
	 else <<
	    % Making sure changes are really applied
	    mo := pasf_susibineqcong1(n,m);
	    if mo neq m then <<
	       atf := pasf_simplat1(pasf_0mk2(pasf_mkop('ncong,mo),u),nil)
	       	  where !*rlsifac=nil;
	       if atf eq 'false then
		  return atf
	       else if atf eq 'true then
		  return nil
	       else
	       	  return{'(delete . nil), ('add . (atf . level))}
	    >> else
	       return nil
	 >>
      >>;
      % Both formulas are incongruences
      if remainder(m,n) = 0 then
	 return {'(delete . T)}
      else if remainder(n,m) = 0 then
	 return {'(delete . nil)}
      else
	 return nil
   end;

procedure pasf_susibineqcong1(m,n);
   % Presburger arithmetic standard form smart equal simplification with equal
   % left handside terms in congruences with different moduli subprocedure.
   % [m] is the modulus of the incongruence; [n] is the modulus of the
   % congruence. Returns the reduced modulus (see the diplom thesis of lasaruk
   % for details).
   begin scalar p;
      % For parametric moduli nothing yet
      if null domainp n or null domainp m then return nil;
      % ASSERTION: m,n are greater than 1 (due to atomic formula normal form)
      if (m <= 1 or n <= 1) then
	 rederr{"pasf_susibineqcong1: wrong modulus in input"};
      p := zfactor(n);
      for each f in p do
	 % Factor is present in m with minor power
	 if remainder(m,car f) = 0 and
	 remainder(m,(car f)^(cdr f)) neq 0 then
	    while (remainder(m,car f) = 0) do
	       m := m / car f;
      return m
   end;

procedure pasf_susibinord(oop,ot,oabs,nop,nt,nabs,level);
   % Presburger arithmetic standard form additive simplification. [oop] is the
   % old relation operator; [nop] is the new relation operator; [ot] is the
   % left handside of the old formula; [nt] is the left handside of the new
   % formula; [oabs] is the constant part of the old formula; [nabs] is the
   % constant parts of the new formula; [level] is the recursion
   % level. Returns a SUSIPROG that simplifies the two atomic formulas.
   begin scalar w,oabsv,nabsv;
      % Congruences are treated differently
      if pairp oop and pairp nop then
	 if cdr oop = cdr nop then
	    return pasf_susibinordcongeq(oop,nop)
	 else
	    return pasf_susibinordcong(oop,ot,oabs,nop,nt,nabs,level);
      % Nothing to do for congruences times order relations
      if pairp oop or pairp nop then
	 return nil;
      % Special cases
      oabsv := if null oabs then 0 else oabs;
      nabsv := if null nabs then 0 else nabs;
      % Special case: strict inequalities with an emptyset gap
      if (oop eq 'lessp and nop eq 'greaterp and oabsv + 1 = nabsv) or
       	 (nop eq 'lessp and oop eq 'greaterp and nabsv + 1 = oabsv) then
	    return 'false;
      % Special case: inequalities with single point satisfaction set
      if oop eq 'geq and nop eq 'lessp and nabsv + 1 = oabsv then
	 return {'(delete . T), '(delete . nil),
	    ('add . (pasf_0mk2('equal, addf(ot,numr simp oabs)) . level))};
      if nop eq 'geq and oop eq 'lessp and oabsv + 1 = nabsv then
	 return {'(delete . T), '(delete . nil),
	    ('add . (pasf_0mk2('equal, addf(ot,numr simp nabs)) . level))};
      if oop eq 'leq and nop eq 'greaterp and oabsv + 1 = nabsv then
	 return {'(delete . T), '(delete . nil),
	    ('add . (pasf_0mk2('equal, addf(ot,numr simp oabs)) . level))};
      if nop eq 'leq and oop eq 'greaterp and nabsv + 1 = oabsv then
	 return {'(delete . T), '(delete . nil),
	    ('add . (pasf_0mk2('equal, addf(ot,numr simp nabs)) . level))};
      w := pasf_smordtable(oop,nop,oabs,nabs);
      if car w eq nil then
     	 % Nothing can be done
	 return nil
      else if car w eq 'false then
      	 % Contradiction found
	 return 'false
      else if eqn(car w,1) then
	 % Remove new atomic formula from the level
 	 return {'(delete . T)}
      else if eqn(car w,2) then
 	 % Remove old atomic formula from the theory, add new atomic formula
 	 % to the knowledge
       	 return {'(delete . nil)};
      reutrn nil
   end;

procedure pasf_susibinordcongeq(oop,nop);
   % Presburger arithmetic standard form smart additive simplification with
   % equal left handside terms in congruences with equai moduli. [oop] is the
   % old relation operator; [nop] is the new relation operator. Returns a
   % SUSIPROG that simplifies the formula.
   begin scalar n,m;
      n := cdr oop;
      m := cdr nop;
      % For parametric moduli nothing yet
      if null domainp n or null domainp m then return nil;
      % Both formulas are congruences
      if car oop eq 'cong and car nop eq 'cong then
	 return 'false;
      % Old formula is a congruence and new is an incongruence
      if car oop eq 'cong and car nop eq 'ncong then
	 return {'(delete . T)};
      % Old formula is an incongruence and new is a congurence
      if car oop eq 'ncong and car nop eq 'cong then
	 return {'(delete . nil)};
      % Both formulas are incongruences
      return nil
   end;

procedure pasf_susibinordcong(oop,ot,oabs,nop,nt,nabs,level);
   % Presburger arithmetic standard form additive simplification. [oop] is the
   % old relation operator; [nop] is the new relation operator; [ot] is the
   % left handside of the old formula; [nt] is the left handside of the new
   % formula; [oabs] is the constant part of the old formula; [nabs] is the
   % constant part of the new formula; [level] is the recursion
   % level. Returns a SUSIPROG that simplifies the two atomic formulas.
   begin scalar n,m,eucd,lhs,op,at;
      n := cdr oop;
      m := cdr nop;
      % For parametric moduli nothing yet
      if null domainp n or null domainp m then return nil;
      if car oop eq 'cong and car nop eq 'cong and gcdf(n,m) = 1 then <<
	 op := pasf_mkop('cong,numr simp (n*m));
	 eucd := sfto_exteucd(n,m);
	 lhs := addf(ot,numr simp(n*cadr eucd*nabs + m*caddr
	    eucd*oabs));
	 at := pasf_simplat1(pasf_0mk2(op,lhs),nil) where !*rlsifac=nil;
	 return {'(delete . T),'(delete . nil), 'add . (at . level)}
      >>;
      return nil
   end;

procedure pasf_susipost(atl,knowl);
   % Presburger arithmetic standad form susi post simplification. [atl] is a
   % list of atomic formulas; [knowl] is a knowledge. Returns a list $\lambda$
   % of atomic formulas, such that $\bigwedge [knowl] \land \bigwedge \lambda$
   % is equivalent to $\bigwedge [knowl] \land \bigwedge [atl]$.
   atl;

procedure pasf_susitf(at,knowl);
   % Presburger arithmetic standard form susi transform. [at] is an atomic
   % formula; [knowl] is a knowledge. Returns an atomic formula $\alpha$ such
   % that $\alpha \land \bigwedge [knowl]$ is equivalent to $[at] \land
   % \bigwedge [knowl]$ ($\alpha$ has possibly a more convenient relation than
   % [at]).
   at;

procedure pasf_smeqtable(r_1,r_2);
   % Presburger arithmetic standard form smart simplify equal absolute
   % summands table. [r_1] is a relation; [r_2] is a relation. Returns 'false
   % or a relation $r$ such that $r(t,0)$ is equivalent to $[r_1](t,0) \land
   % [r_2](t,0)$.
   begin scalar al;
      al := '(
	 (equal .
	    ((equal . (1 . nil))
	     (neq . (false . nil))
	     (geq . (1 . nil))
             (leq . (1 . nil))
	     (greaterp . (false . nil))
	     (lessp . (false . nil))
	     (cong . (1 . nil))
	     (ncong . (false . nil))))
         (neq .
	    ((equal . (false . nil))
	     (neq . (1 . nil))
	     (geq . (3 . greaterp))
             (leq . (3 . lessp))
	     (greaterp . (2 . nil))
	     (lessp . (2 . nil))
	     (cong . (nil . nil))
	     (ncong . (2 . nil))))
         (geq .
	    ((equal . (2 . nil))
	     (neq . (3 . greaterp))
	     (geq . (1 . nil))
             (leq . (3 . equal))
	     (greaterp . (2 . nil))
	     (lessp . (false . nil))
	     (cong . (nil . nil))
	     (ncong . (5 . greaterp))))
         (leq .
	    ((equal . (2 . nil))
	     (neq . (3 . lessp))
	     (geq . (3 . equal))
             (leq . (1 . nil))
	     (greaterp . (false . nil))
	     (lessp . (2 . nil))
	     (cong . (nil . nil))
	     (ncong . (5 . lessp))))
         (greaterp .
 	    ((equal . (false . nil))
	     (neq . (1 . nil))
	     (geq . (1 . nil))
             (leq . (false . nil))
	     (greaterp . (1 . nil))
	     (lessp . (false . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
         (lessp .
	     ((equal . (false . nil))
	     (neq . (1 . nil))
	     (geq . (false . nil))
             (leq . (1 . nil))
	     (greaterp . (false . nil))
	     (lessp . (1 . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (cong .
	    ((equal . (2 . nil))
	     (neq . (nil . nil))
	     (geq . (nil . nil))
             (leq . (nil . nil))
	     (greaterp . (nil . nil))
	     (lessp . (nil . nil))
	     (cong . (1 . nil))
	     (ncong . (false . nil))))
	 (ncong .
	    ((equal . (false . nil))
	     (neq . (1 . nil))
	     (geq . (4 . greaterp))
             (leq . (4 . lessp))
	     (greaterp . (nil . nil))
	     (lessp . (nil . nil))
	     (cong . (false . nil))
	     (ncong . (1 . nil)))));
      return cdr (atsoc(r_2,atsoc(r_1,al)))
   end;

procedure pasf_smordtable(r1,r2,s,tt);
   % Presburger arithmetic standard form smart simplify ordered absolute
   % summands. [r1] is a relation; [r2] is a relation; [s] is the constant
   % part of [r1]; [t] is the constant part of [r2]. Returns '(nil . nil) if
   % no simplification is possible; '(false . nil) if contradiction was found;
   % '(1 . nil) if the new formula does not bring any knowledge and can be so
   % removed from the actual level; '(2 . nil) if the old formula should be
   % removed and the new added.
   if s < tt then
      pasf_smordtable2(r1,r2)
   else if s > tt then
      pasf_smordtable1(r1,r2)
   else
      rederr {"abused smordtable"};

procedure pasf_smordtable1(r1,r2);
   % Smart simplify ordered absolute summands table if the absolute summand of
   % [r1] is less than that of [r2]. [r1], [r2] are relations. Returns '(nil .
   % nil) if no simplification is possible; '(false . nil) if contradiction was
   % found; '(1 . nil) if the new formula does not bring any knowledge and can
   % be so removed from the actual level; '(2 . nil) if the old formula should
   % be removed and the new one added.
   begin scalar al;
      al := '(
	 (lessp .
	    ((lessp . (1 . nil))
             (leq . (1 . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (false . nil))
	     (greaterp . (false . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (leq .
	    ((lessp . (1 . nil))
             (leq . (1 . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (false . nil))
	     (greaterp . (false . nil))
             (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (equal .
	    ((lessp . (1 . nil))
             (leq . (1 . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (false . nil))
	     (greaterp . (false . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (neq .
	    ((lessp . (nil . nil))
             (leq . (nil . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (2 . nil))
	     (greaterp . (2 . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (geq .
	    ((lessp . (nil . nil))
             (leq . (nil . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (2 . nil))
	     (greaterp . (2 . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (greaterp .
	    ((lessp . (nil . nil))
             (leq . (nil . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (2 . nil))
	     (greaterp . (2 . nil))
	     (cong . (nil . nil))
    	     (ncong . (nil . nil)))));
      return cdr (atsoc(r2,atsoc(r1,al)))
   end;

procedure pasf_smordtable2(r1,r2);
   % Presburger arithmetic standard form smart simplify ordered absolute
   % summands table if absoulte summand of $r1$ is less as the one of $r2$.
   % [r1] is a relaton; [r2] is a relation. Returns '(nil . nil) if no
   % simplification is possible; '(false . nil) if contradiction was found;
   % '(1 . nil) if the new formula does not bring any knowledge and can be so
   % removed from the actual level; '(2 . nil) if the old formula should be
   % removed and the new added.
   begin scalar al;
      al := '(
	 (lessp .
	    ((lessp . (2 . nil))
             (leq . (2 . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (nil . nil))
	     (greaterp . (nil . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (leq .
	    ((lessp . (2 . nil))
             (leq . (2 . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (nil . nil))
	     (greaterp . (nil . nil))
             (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (equal .
	    ((lessp . (false . nil))
             (leq . (false . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (1 . nil))
	     (greaterp . (1 . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (neq .
	    ((lessp . (2 . nil))
             (leq . (2 . nil))
	     (equal . (2 . nil))
   	     (neq . (nil . nil))
	     (geq . (nil . nil))
	     (greaterp . (nil . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (geq .
	    ((lessp . (false . nil))
             (leq . (false . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (1 . nil))
	     (greaterp . (1 . nil))
	     (cong . (nil . nil))
	     (ncong . (nil . nil))))
	 (greaterp .
	    ((lessp . (false . nil))
             (leq . (false . nil))
	     (equal . (false . nil))
   	     (neq . (1 . nil))
	     (geq . (1 . nil))
	     (greaterp . (1 . nil))
	     (cong . (nil . nil))
    	     (ncong . (nil . nil)))));
      return cdr (atsoc(r2,atsoc(r1,al)))
   end;

endmodule; % [pasfsism]

end; % of file
