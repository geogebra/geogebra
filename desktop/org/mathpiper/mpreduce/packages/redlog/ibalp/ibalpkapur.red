% ----------------------------------------------------------------------
% $Id: ibalpkapur.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(ibalp_kapur_rcsid!* ibalp_kapur_copyright!*);
   ibalp_kapur_rcsid!* :=
      "$Id: ibalpkapur.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   ibalp_kapur_copyright!* := "Copyright (c) 2007-2009 A. Dolzmann and T. Sturm"
>>;

module ibalpkapur;
% Author Stefan Kaeser

% own switches and global vars
fluid '(ibalp_kapuroptions!* !*ibalp_kapurgb !*rlkapurchktaut !*rlkapurchkcont);
switch ibalp_kapurgb,rlkapurchktaut,rlkapurchkcont;

% debug switches
fluid '(!*ibalp_kapurgbdegd !*ibalp_kapurdisablegb);
switch ibalp_kapurgbdegd,ibalp_kapurdisablegb;

% import needed switches and global settings
fluid '(!*rlverbose !*modular vdpsortmode!*);

procedure ibalp_setkapuroption(opt,val);
   % Set Kapur option. [opt] is an identifier. [val] is any. Returns
   % any (old setting or nil).
   begin scalar oldopt,oldval;
     if oldopt := atsoc(opt,ibalp_kapuroptions!*) then <<
     	oldval := cdr oldopt;
     	cdr oldopt := val
     >>
     else
     	ibalp_kapuroptions!* := (opt . val) . ibalp_kapuroptions!*;
     return oldval
   end;

procedure ibalp_getkapuroption(opt);
   % Get Kapur option. [opt] is an identifier. Returns any.
   lto_catsoc(opt,ibalp_kapuroptions!*);

procedure ibalp_initkapuroptions();
   % Initialise Kapur options. Returns a list.
   <<
      ibalp_kapuroptions!* := {
         ('torder . vdpsortmode!*),
         ('polygenmode . 'kapur)}; % other are 'direct, 'knf, 'kapurknf
      if !*rlkapurchktaut and !*rlkapurchkcont then
         ibalp_setkapuroption('checkmode,'full)
      else if !*rlkapurchktaut then
         ibalp_setkapuroption('checkmode,'taut)
      else if !*rlkapurchkcont then
         ibalp_setkapuroption('checkmode,'cont)
      else
         ibalp_setkapuroption('checkmode,'sat)
   >>;

procedure ibalp_kapur(f,umode);
   % Kapur algebraic interface. [f] is a formula. [checkmode] is an
   % identifier. [umode] is an identifier. Returns a formula.
   begin scalar oldmod,oldswitch,newf;
      oldmod := setmod 2;
      oldswitch := !*modular;
      on1 'modular;
      ibalp_initkapuroptions();
      ibalp_setkapuroption('polygenmode,umode);
      if !*rlverbose then <<
         ioto_tprin2t "++++ Starting ibalp_kapur";
         ioto_prin2t {"Polynomial generation method: ",
            ibalp_getkapuroption 'polygenmode};
         ioto_tprin2t "-------------------------"
      >>;
      f := cl_simpl(f,nil,-1); % fast simplify once
      if ibalp_getkapuroption 'checkmode memq '(taut full) then <<
         if !*rlverbose then ioto_prin2t "---- Check for tautology";
         newf := ibalp_regformula(ibalp_kapur1(f,0),0,f)
      >>;
      if ibalp_getkapuroption 'checkmode memq '(cont full sat) and
            not rl_tvalp rl_op newf then <<
         if !*rlverbose then ioto_prin2t "---- Check for contradiction";
         newf := ibalp_regformula(ibalp_kapur1(f,1),1,f)
      >>;
      setmod oldmod;
      if null oldswitch then off1 'modular;
      return newf
   end;

procedure ibalp_regformula(pl,trthval,origf);
   % Regenerate formula. [pl] is a list of polynomials. [trthval] is 0
   % or 1. [origf] is a formula. Returns a formula.
   if eqn(trthval,0) then
      if 1 member pl then
         'true
      else
         origf
   else
      if 1 member pl then
         'false
      else if ibalp_getkapuroption 'checkmode eq 'sat then
         'true
      else
         origf;

procedure ibalp_kapur1(f,trthval);
   % Kapur subprocedure 1. [f] is a formula. [trthval] is 0 if check
   % for tautology or 1 if check for contradiction. Returns a Groebner
   % Basis of an equivalent set of polynomials.
   begin scalar polylist;
      if rl_qnum f > 0 then f := cl_qe(f,nil);
      if !*rlverbose then ioto_prin2t "--- Generate polynomials...";
      polylist := ibalp_polyset(f,trthval);
      polylist := nconc(polylist,ibalp_genidemppolylist polylist);
      if !*rlverbose then <<
         ioto_prin2t {"-- Generated ",length polylist," polynomials"};
         ioto_prin2t {"--- Compute Groebner Basis (",vdpsortmode!*,")..."}
      >>;
      polylist := ibalp_groebnereval polylist;
      if !*rlverbose then <<
         ioto_prin2t {"-- Generated ",length polylist," polynomials"}
      >>;
      return polylist
   end;

procedure ibalp_polyset(f,trthval);
   % Generate set of polynomials. [f] is a formula. [trthval] is 0 or
   % 1. Returns a list of polynomials equivalent to [f].
   if ibalp_getkapuroption 'polygenmode eq 'knf then
      ibalp_pset3knf(f,trthval)
   else if ibalp_getkapuroption 'polygenmode eq 'direct then
      ibalp_psetdirekt(f,trthval)
   else if ibalp_getkapuroption 'polygenmode memq '(kapur kapurknf) then
      ibalp_psetkapur(f,trthval)
   else
      ibalp_psetkapur(f,trthval);

procedure ibalp_formulaform(p);
   % Formula Form. [p] is a polynomial without exponents. Returns a
   % formula.
   if eqn(p,1) then
      'true
   else if eqn(p,0) then
      'false
   else if idp p then
      ibalp_1mk2('equal,p)
   else if eqcar(p,'times) then
      rl_smkn('and,for each x in cdr p collect ibalp_formulaform x)
   else if eqcar(p,'plus) then
      rl_mk1('not,rl_mk2('equiv,ibalp_formulaform cadr p,
         ibalp_formulaform kpoly_norm ('plus . cddr p)));

procedure ibalp_polyform(f);
   % Polynomial form. [f] is a quantifier-free formula. Returns a
   % polynomial.
   begin scalar a,b;
   if rl_tvalp rl_op f then
      return if rl_op f eq 'true then 1 else 0;
   if ibalp_atfp f then
      return ibalp_polyformatf f;
   if rl_op f eq 'not then
      return kpoly_plus {1,ibalp_polyform rl_arg1 f};
   if rl_junctp rl_op f then <<
      if rl_op f eq 'and then
         return kpoly_times ibalp_polyformlist rl_argn f;
      return kpoly_plus {1,kpoly_times for each j in
         ibalp_polyformlist rl_argn f collect kpoly_plus {1,j}}
   >>;
   a := ibalp_polyform rl_arg2l f;
   b := ibalp_polyform rl_arg2r f;
   if rl_op f eq 'impl then
      return kpoly_plus {1,kpoly_times {a,b},ibalp_clonestruct a};
   if rl_op f eq 'repl then
      return kpoly_plus {1,kpoly_times {a,b},ibalp_clonestruct b};
   if rl_op f eq 'equiv then
      return kpoly_plus {1,a,b};
   if rl_op f eq 'xor then
      return kpoly_plus {a,b}
   end;

procedure ibalp_polyformatf(f);
   % Polynomial form of an atomic formula. [f] is an atomic formula.
   % Returns a polynomial.
   if ibalp_op f eq 'equal then
      if eqn(ibalp_arg2r f,1) then
         ibalp_arg2l f
      else
         kpoly_plus {1,ibalp_arg2l f}
   else % ibalp_op eq 'neq
      if eqn(ibalp_arg2r f,0) then
         ibalp_arg2l f
      else
         kpoly_plus {1,ibalp_arg2l f};

procedure ibalp_remnested(pl,op);
   % Remove nested. [l] is a list. [op] is an identifier. Returns a
   % list where no sublist ist starting with [op] anymore by merging
   % into [pl] (applying the associative law).
   for each j in pl join
      if eqcar(j,op) then
         ibalp_remnested(cdr j,op)
      else
         {j};

procedure ibalp_polyformlist(l);
   % Polynomialform list. [l] is a list of formulae. Returns a list of
   % polynomials.
   for each x in l collect ibalp_polyform x;

procedure ibalp_groebnereval(pl);
   % Groebner Basis evaluation. [pl] is a list of polynomials. Returns
   % a list of polynomials which is a Groebner Basis of [pl].
   if null pl then
      {0}
   else if !*ibalp_kapurdisablegb then
      pl
   else if !*ibalp_kapurgbdegd then
      ibalp_gbdegd(pl,20)
   else if !*ibalp_kapurgb then
      ibalp_gb pl
   else
      cdr groebnereval {'list . pl};

procedure ibalp_torderp(a,b);
   % Termorder predicate. [a] and [b] are monomials. Returns boolean.
   if eqn(b,0) then
      t
   else if eqn(a,0) then
      nil
   else if eqn(b,1) then
      t
   else if eqn(a,1) then
      nil
   else if a = b then
      t
   else if ibalp_getkapuroption 'torder eq 'lex then
      ibalp_torderlexp(a,b)
   else if ibalp_getkapuroption 'torder eq 'gradlex then
      ibalp_tordergradlexp(a,b)
   else %use gradlex per default
      ibalp_tordergradlexp(a,b);

procedure ibalp_torderlexp(a,b);
   % Termorder Lexicographic. [a] and [b] are monomials different from
   % 0 or 1. Returns boolean.
   if atom a and atom b then
      ordop(a,b)
   else if atom a and pairp b then
      if a eq cadr b then nil else ordop(a,cadr b)
   else if pairp a and atom b then
      ordop(cadr a,b)
   else if pairp a and pairp b and cdr a and cdr b then
      if cadr a eq cadr b then
         if cddr a and cddr b then
            ibalp_torderp('times . cddr a,'times . cddr b)
         else if cddr a then
            t
         else if cddr b then
            nil
         else
            t
      else
         ordop(cadr a,cadr b)
   else
      if cdr a then t else nil;

procedure ibalp_tordergradlexp(a,b);
   % Termorder Gradlex. [a] and [b] are monomials different from 0 or
   % 1. Returns boolean.
   if atom a and atom b then
      ordop(a,b)
   else if atom a and pairp b then
      nil
   else if atom b and pairp a then
      t
   else if length a > length b then
      t
   else if length a < length b then
      nil
   else if pairp a and pairp b and cdr a and cdr b then
      if cadr a eq cadr b then
         if cddr a and cddr b then
            ibalp_tordergradlexp('times . cddr a,'times . cddr b)
         else if cddr a then
            t
         else if cddr b then
            nil
         else
            t
      else
         ordop(cadr a,cadr b);

procedure ibalp_gbdegd(pl,maxdeg);
   % Degree-d Groebner Basis. [pl] is a non-empty list of polynomials.
   % [maxdeg] is a positive integer. Returns a list of polynomials
   % which is a Degree-[maxdeg] Groebner Basis of [pl].
   begin scalar glist,glistend,slist,pol,newrule,srule;
      glist := {krule_poly2rule car pl};
      glistend := glist;
      slist := cdr pl;
      while slist do <<
         pol := car slist;
         slist := cdr slist;
         pol := ibalp_gbreducepoly(pol,glist);
         if not eqn(pol,0) then <<
            newrule := krule_poly2rule pol;
            % add new rule at the end of gb
            cdr glistend := (newrule . nil);
            glistend := cdr glistend;
            % check if new overlap should be added
            for each j in glist do <<
               srule := ibalp_gboverlaprules(j,newrule,nil);
               if (atom car srule and not(eqn(car srule,0) or eqn(car srule,1)))
                  or (listp car srule and length cdar srule < maxdeg + 1) then
                  slist := (krule_rule2poly srule) . slist
            >>
         >>
      >>;
      return for each j in glist collect krule_rule2poly j
   end;

procedure ibalp_gb(pl);
   % Groebner Basis. [pl] is a list of polynomials. Returns a list of
   % polynomials which is a Groebner Basis of [pl].
   begin scalar allrules,newrules,newrule,newrules2;
      if null pl then return '(0);
      if null cdr pl then return pl;
      allrules := ibalp_gbinitrules pl;
      newrules := cdr allrules;
      while newrules do <<
         if !*rlverbose then ioto_tprin2t {"- ",length newrules," new rules"};
         newrules2 := newrules;
         newrules := nil;
         for each j in newrules2 do for each k in allrules do <<
            newrule := ibalp_gboverlaprules(j,k,append(allrules,newrules));
            if newrule = '(1 . 0) then <<
               if !*rlverbose then ioto_tprin2t "-- 1 in GB generation";
               allrules := '((1 . 0));
               newrules := nil
            >> else if eqn(cdr newrule,1) and eqcar(car newrule,'times) then
               newrules := nconc(for each k in cdar newrule collect (k . 1),
                  newrules)
            else if newrule neq '(0 . 0) then <<
               if !*rlverbose then
                  ioto_tprin2t {car newrule," -> ",cdr newrule};
               newrules := newrule . newrules
            >>
         >>;
         if newrules then <<
            newrules := ibalp_gbsimplifyall newrules;
            allrules := ibalp_gbsimplifyall append(allrules,newrules)
         >>
      >>;
      return for each j in allrules collect krule_rule2poly j
   end;

procedure ibalp_gbsimplifyall(rules);
   % Groebner Basis simplify all rules. [rules] is a non-empty list of
   % rules. Returns a list of rules, where every rule is in normalform
   % in regard to all other rules in the list.
   begin scalar currule,beforr,afterr,newp; integer curlength,cntr;
      if !*rlverbose then ioto_tprin2t {"-- Simplifing ",length rules," Rules"};
      if null cdr rules then return rules;
      curlength := 1;
      cntr := 0;
      currule := rules;
      beforr := rules;
      afterr := cdr rules;
      while cdr beforr do <<
         curlength := add1 curlength;
         beforr := cdr beforr
      >>;
      while cntr < curlength do <<
         newp := ibalp_gbreducepoly(krule_rule2poly car currule,afterr);
        if eqn(newp,1) then <<
            currule := {krule_poly2rule 1};
            cntr := curlength + 25
         >> else if eqn(newp,0) then <<
            curlength := sub1 curlength;
            currule := afterr;
            afterr := cdr afterr
         >> else <<
            cdr beforr := (krule_poly2rule newp) . nil;
            cntr := if cadr beforr = car currule then add1 cntr else 0;
            beforr := cdr beforr;
            currule := afterr;
            afterr := cdr afterr
         >>
      >>;
      return currule
   end;

procedure ibalp_gboverlaprules(r1,r2,rlist);
   % Groebner Basis overlap rules. [r1] and [r2] are rules. [rlist] is
   % a list of rules. Returns a rule which is the result of [r1] and
   % [r2] beeing overlapped and the S-Polynomial is reduced using
   % [rlist].
   begin scalar spoly,head1,tail1,head2,tail2;
      if ibalp_gboverlapruleszcritp(r1,r2) then return krule_poly2rule 0;
      head1 := krule_head r1;
      head2 := krule_head r2;
      tail1 := krule_tail r1;
      tail2 := krule_tail r2;
      spoly :=
         if head1 = head2 then
            kpoly_plus {tail1,tail2}
         else if atom head1 and pairp head2 and head1 memq cdr head2 then
            kpoly_plus {tail2,kpoly_times {tail1,delete(head1,head2)}}
         else if atom head2 and pairp head1 and head2 memq cdr head1 then
            kpoly_plus {tail1,kpoly_times {tail2,delete(head2,head1)}}
         else <<
            spoly := kpoly_times union(cdr head1,cdr head2); % lcm
            kpoly_plus {ibalp_gbapplyrule(spoly,r1),ibalp_gbapplyrule(spoly,r2)}
         >>;
      return krule_poly2rule ibalp_gbreducepoly(spoly,rlist)
   end;

procedure ibalp_gboverlapruleszcritp(r1,r2);
   % Groebner Basis overlap rules zero criteria Predicate. [r1] and
   % [r2] are rules. Returns non-nil if the S-Polynomial of [r1] and
   % [r2] can be reduced to 0 easily.
   (r1 = r2) or
      (atom car r1 and atom car r2 and not(eqcar(r1,car r2))) or
      (eqn(cdr r1,0) and eqn(cdr r2,0)) or
      (atom car r1 and pairp car r2 and not(car r1 memq cdar r2)) or
      (atom car r2 and pairp car r1 and not(car r2 memq cdar r1)) or
      (pairp car r1 and pairp car r2 and null intersection(cdar r1,cdar r2));

procedure ibalp_gbreducepoly(p,rules);
   % Groebner Basis reduce polynomial. [p] is a polynomial. [rules] is
   % a list of rules. Returns a polynomial which is in normalform
   % according to the [rules].
   begin scalar chnge,p1,p2;
      chnge := t;
      p1 := p;
      p2 := p;
      while chnge do <<
         chnge := nil;
         for each j in rules do p1 := ibalp_gbapplyrule(p1,j);
         if p1 neq p2 then <<
            chnge := t;
            p2 := p1
         >>
      >>;
      return p1
   end;

procedure ibalp_gbapplyrule(p,rule);
   % Groebner Basis apply rule. [p] is a polynomial. [rule] is a rule.
   % Returns a polynomial.
   begin scalar w;
      if rule = krule_poly2rule 1 then return 0;
      if kpoly_monomialp p then return ibalp_gbapplyrulem(p,rule);
      w := cdr p;
      while w do
         if ibalp_torderp(car w,krule_head rule) then <<
            car w := ibalp_gbapplyrulem(car w,rule);
            w := cdr w
         >>
         else
            w := nil;
      return kpoly_plus cdr p
   end;

procedure ibalp_gbapplyrulem(m,rule);
   % Groebner Basis apply rule monomial. [m] is a monomial. [rule] is
   % a rule. Returns a polynomial.
   if rule = krule_poly2rule 1 then
      0
   else if atom m then
      if eqcar(rule,m) then krule_tail rule else m
   else if atom krule_head rule then
      if krule_head rule memq m then
         kpoly_times {delq(krule_head rule,m),krule_tail rule}
      else
         m
   else if kpoly_mondivp(m,krule_head rule) then <<
      for each j in cdr krule_head rule do m := delq(j,m);
      kpoly_times {m,krule_tail rule}
   >> else
      m;

procedure ibalp_gbinitrules(pl);
   % Groebner Basis init ruleslist. [pl] is a non-empty list of
   % polynomials. Returns a list of rules, generated by the
   % polynomials in [pl].
   begin scalar rules,newp;
      rules := {krule_poly2rule car pl};
      pl := cdr pl;
      while pl do <<
         newp := ibalp_gbreducepoly(car pl,rules);
         pl := cdr pl;
         if eqn(newp,1) then <<
            if !*rlverbose then ioto_tprin2t "-- 1 in Ideal Initialisation";
            rules := {krule_poly2rule 1};
            pl := nil
         >>
         else if not eqn(newp,0) then
            rules := (krule_poly2rule newp) . rules
      >>;
      rules := ibalp_gbsimplifyall rules;
      return rules
   end;

procedure ibalp_genpolyform(f,trthval);
   % Generate polynomial form. [f] is a quantifier free formula.
   % [trthval] is 0 or 1. Returns a polynomial without exponents.
   if eqn(trthval,1) then
      kpoly_plus {1,ibalp_polyform f}
   else
      ibalp_polyform f;

procedure ibalp_genidemppolylist(l);
   % Generate idempotential polynomial list. [l] is a list of
   % polynomials. Returns a list of polynomials containing the
   % idempotential polynomials for each variable in [l].
   begin scalar vl;
      for each j in l do
         if idp j then
            vl := lto_insert(j,vl)
         else if eqcar(j,'times) then
            for each k in cdr j do vl := lto_insert(k,vl)
         else if eqcar(j,'plus) then
            for each k in cdr j do
               if idp k then
                  vl := lto_insert(k,vl)
               else if eqcar(k,'times) then
                  for each m in cdr k do
                     vl := lto_insert(m,vl);
      return for each j in vl collect kpoly_idemppoly j;
   end;

% umode 3KNF

procedure ibalp_pset3knf(f,trthval);
   % Generate set of polynomials 3KNF. [f] is a formula. [trthval] is
   % 0 or 1. Returns a list of polynomials by transforming [f] into a
   % into a conjunctive clausal form, containing max 3 variables per
   % clause.
   begin scalar newf;
      newf := if eqn(trthval,1) then f else rl_mk1('not,f);
      newf := ibalp_pset3knfnf newf;
      newf := ibalp_pset3knf2(newf,nil);
      newf := if rl_op newf eq 'and then
            rl_mkn('and,for each j in rl_argn newf join
               ibalp_pset3knf3(j,nil))
         else
            rl_smkn('and,ibalp_pset3knf3(newf,nil));
      return
         if rl_op newf eq 'and then
            for each j in rl_argn newf collect ibalp_genpolyform(j,1)
	 else
	    {ibalp_genpolyform(newf,1)}
   end;

procedure ibalp_pset3knfnf(f);
   % Generate set of polynomials 3KNF negated form. [f] is a formula.
   % Returns a formula in negated form
   if rl_tvalp rl_op f or ibalp_atfp f then
      f
   else if rl_op f eq 'not then
      if ibalp_atfp rl_arg1 f then
         f
      else
         ibalp_pset3knfnf1 rl_arg1 f
   else if rl_junctp rl_op f then
      rl_mkn(rl_op f, for each j in rl_argn f collect
         ibalp_pset3knfnf j)
   else if rl_op f eq 'impl then
      rl_mk2('or,ibalp_pset3knf1 rl_mk1('not,rl_arg2l f),
         ibalp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'repl then
      rl_mk2('or,ibalp_pset3knfnf rl_mk1('not,rl_arg2r f),
         ibalp_pset3knfnf rl_arg2l f)
   else
      rl_mk2(rl_op f, ibalp_pset3knfnf rl_arg2l f,
         ibalp_pset3knfnf rl_arg2r f);

procedure ibalp_pset3knfnf1(f);
   % Generate set of polynomials 3KNF negated form subprocedure 1. [f]
   % is a formula, but not an atomic formula. Returns a formula in
   % negated form assuming the operator before [f] was a 'not.
   if rl_tvalp rl_op f then
      cl_flip rl_op f
   else if rl_op f eq 'not then
      ibalp_pset3knfnf rl_arg1 f
   else if rl_junctp rl_op f then
      rl_mkn(cl_flip rl_op f,
         for each j in rl_argn f collect ibalp_pset3knfnf rl_mk1('not,j))
   else if rl_op f eq 'impl then
      rl_mk2('and,ibalp_pset3knfnf rl_arg2l f,ibalp_pset3knfnf
         rl_mk1('not,rl_arg2r f))
   else if rl_op f eq 'repl then
      rl_mk2('and,ibalp_pset3knfnf rl_mk1('not,rl_arg2l f),
         ibalp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'equiv then
      rl_mk2('equiv,ibalp_pset3knfnf rl_mk1('not,rl_arg2l f),
         ibalp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'xor then
      rl_mk2('equiv,ibalp_pset3knfnf rl_arg2l f,ibalp_pset3knfnf rl_arg2r f);

procedure ibalp_pset3knf2(f,intree);
   % Generate set of polynomials 3KNF subprocedure 2. [f] is a formula
   % in negated form. [intree] is boolean. Returns a formula where
   % only the top-level operator 'and is n-ary.
   begin scalar partlists,g;
   if rl_tvalp rl_op f or rl_op f eq 'not or ibalp_atfp f then
      return f;
   if null intree and rl_op f eq 'and then
      return rl_smkn('and, for each j in rl_argn f join <<
         g := ibalp_pset3knf2(j,nil);
         if rl_op g eq 'and then rl_argn g else {g}
      >>);
   if rl_junctp rl_op f and lto_lengthp(rl_argn f,3,'geq) then <<
      if lto_lengthp(rl_argn f,4,'geq) then <<
         partlists := ibalp_splitlist rl_argn f;
         return rl_mk2(rl_op f,
            ibalp_pset3knf2(rl_mkn(rl_op f,car partlists),t),
               ibalp_pset3knf2(rl_mkn(rl_op f,cdr partlists),t))
      >>;
      return rl_mk2(rl_op f,ibalp_pset3knf2(car rl_argn f,t),
         rl_mk2(rl_op f,ibalp_pset3knf2(cadr rl_argn f,t),
            ibalp_pset3knf2(caddr rl_argn f, t)))
   >>;
   return rl_mk2(rl_op f,ibalp_pset3knf2(rl_arg2l f,t),
         ibalp_pset3knf2(rl_arg2r f,t));
   end;

procedure ibalp_pset3knf3(f,clausevar);
   % Generate set of polynomials 3KNF subprocedure 3. [f] is a formula
   % in binary tree negated form. [clausevar] is an identifier or nil.
   % Returns a list of formulae with max three vars in each clause.
   begin scalar nvarl,nvarr,returnlist;
      if rl_tvalp rl_op f then
         return {f};
      if rl_op f eq 'not or ibalp_atfp f then
         return {f};
      if null clausevar then <<
         clausevar := ibalp_1mk2('equal,gensym());
         returnlist := clausevar . returnlist
      >>;
      if rl_op rl_arg2l f eq 'not or ibalp_atfp rl_arg2l f then
         nvarl := rl_arg2l f
      else <<
         nvarl := ibalp_1mk2('equal,gensym());
         returnlist := nconc(returnlist,ibalp_pset3knf3(rl_arg2l f,nvarl))
      >>;
      if rl_op rl_arg2r f eq 'not or ibalp_atfp rl_arg2r f then
         nvarr := rl_arg2r f
      else <<
         nvarr := ibalp_1mk2('equal,gensym());
         returnlist := nconc(returnlist,ibalp_pset3knf3(rl_arg2r f,nvarr))
      >>;
      return rl_mk2('equiv,clausevar,rl_mk2(rl_op f,nvarl,nvarr)) . returnlist;
   end;

% umode Kapur

procedure ibalp_psetkapur(f,trthval);
   % Generate set of polynomials Kapur. [f] is a formula. [trthval] is
   % 0 or 1. Returns a list of polynomials by transforming [f] using
   % Kapur and Narendrans optimized Method. [trthval] is the trthvalue
   % which should be achieved.
   if rl_op f eq 'not then
      ibalp_psetkapur(rl_arg1 f,ibalp_flip01 trthval)
   else if eqn(trthval,1) then
      ibalp_psetkapurcont f
   else
      ibalp_psetkapurtaut f;

procedure ibalp_psetkapurtaut(f);
   % Generate set of polynomials Kapur tautology. [f] is a formula.
   % Returns a list of polynomials.
   if rl_op f eq 'impl then
      nconc(ibalp_psetkapur(rl_arg2l f,1),ibalp_psetkapur(rl_arg2r f,0))
   else if rl_op f eq 'repl then
      nconc(ibalp_psetkapur(rl_arg2l f,0),ibalp_psetkapur(rl_arg2r f,1))
   else if rl_op f eq 'or then
      for each j in rl_argn f join ibalp_psetkapur(j,0)
   else if rl_op f eq 'and then
      ibalp_psetkapurnary(f,0)
   else
      ibalp_psetkapurnoopt(f,0);

procedure ibalp_psetkapurcont(f);
   % Generate set of polynomials Kapur contradiction. [f] is a formula.
   % Returns a list of polynomials.
   if rl_op f eq 'and then
      for each j in rl_argn f join ibalp_psetkapur(j,1)
   else if rl_op f eq 'impl and rl_op rl_arg2r f eq 'and then
      ibalp_psetkapurdistleft(f,1)
   else if rl_op f eq 'repl and rl_op rl_arg2l f eq 'and then
      ibalp_psetkapurdistright(f,1)
   else if rl_op f eq 'or then
      ibalp_psetkapurnary(f,1)
   else
      ibalp_psetkapurnoopt(f,1);

procedure ibalp_psetkapurnary(f,trthval);
   % Generate set of polynomials Kapur n-Ary subprocedure 1. [f] is a
   % formula with an n-ary toplevel operator. [trthval] is 0 or 1.
   % Returns a list of polynomials by spliting a n-ary boolean
   % formulae into two equivalent polynomials adding auxiliary vars.
   begin scalar distop;
      distop := cl_flip rl_op f;
      if lto_lengthp(rl_argn f,4,'geq) then
         return ibalp_psetkapurnary1(f,trthval);
      if lto_lengthp(rl_argn f,2,'eqn) then
         return
            if rl_op rl_arg2r f eq distop then
              ibalp_psetkapurdistleft(f,trthval)
            else if rl_op rl_arg2l f eq distop then
               ibalp_psetkapurdistright(f,trthval)
            else
               ibalp_psetkapurnoopt(f,trthval);
      return ibalp_psetkapurnoopt(f,trthval)
   end;

procedure ibalp_psetkapurnary1(f,trthval);
   % Generate set of polynomials Kapur n-Ary subprocedure 1. [f] is a
   % formula with an n-ary toplevel operator. [trthval] is 0 or 1.
   % Returns a list of polynomials by spliting a n-ary boolean
   % formulae into two equivalent polynomials adding auxiliary vars.
   begin scalar partlists,newvar,l1,l2;
      partlists := ibalp_splitlist rl_argn f;
      l1 := car partlists;
      l2 := cdr partlists;
      newvar := gensym();
      l1 := rl_mkn(rl_op f,ibalp_1mk2('equal,newvar) . l1);
      l2 := rl_mkn(rl_op f,rl_mk1('not,ibalp_1mk2('equal,newvar)) . l2);
      return nconc(ibalp_psetkapur(l1,trthval),ibalp_psetkapur(l2,trthval))
   end;

procedure ibalp_psetkapurnoopt(f,trthval);
   % Generate set of polynomials Kapur without possible optimizations.
   % [f] is a formula. [trthval] is 0 or 1. Returns a list of
   % polynomials.
   begin scalar p;
      if ibalp_getkapuroption 'polygenmode eq 'kapurknf then
         return ibalp_pset3knf(f,trthval);
      p := ibalp_genpolyform(f,trthval);
      return if not eqn(p,0) then {p}
   end;

procedure ibalp_psetkapurdistleft(f,trthval);
   % Generate set of polynomials Kapur left distributivity. [f] is a
   % formula. [trthval] is 0 or 1. Returns a list of polynomials by
   % applying the distributivity rule first.
   for each j in rl_argn rl_arg2r f join
      ibalp_psetkapur(rl_mk2(rl_op f,rl_arg2l f,j),trthval);

procedure ibalp_psetkapurdistright(f,trthval);
   % Generate set of polynomials Kapur right distributivity. [f] is a
   % formula. [trthval] is 0 or 1. Returns a list of polynomials by
   % applying the distributivity rule first.
   for each j in rl_argn rl_arg2l f join
      ibalp_psetkapur(rl_mk2(rl_op f,rl_arg2r f,j),trthval);

procedure ibalp_psetdirekt(f, trthval);
   % Generate set of polynomials directly. [f] is a formula. [trthval]
   % is 0 or 1. Returns a list of polynomials.
   {ibalp_genpolyform(f,trthval)};

procedure ibalp_splitlist(l);
   % Split list. [l] is a list. Returns a pair of lists. Devides a
   % list into two lists of equal length containing all elements of
   % [l].
   begin scalar elm,l2; integer lgt,cnt;
      if null l then
         return (nil . nil);
      if null cdr l then
         return (l . nil);
      lgt := length l;
      cnt := 1;
      elm := l;
      while cnt < lgt / 2 do <<
         cnt := add1 cnt;
         elm := cdr elm
      >>;
      l2 := cdr elm;
      cdr elm := nil;
      return (l . l2);
   end;

procedure ibalp_clonestruct(s);
   % Clone structure. [s] is any. Returns any, which is a clone of [s]
   % in a constructive way.
   if atom s then s else (ibalp_clonestruct car s) . (ibalp_clonestruct cdr s);

endmodule; %ibalpkapur

module krule;
% Kapur Rewriterules

% DS
% <RULE> ::= (<MONOMIAL> . <POLY>)

procedure krule_head(r);
   % Headmonomial. [r] is a rule. Returns the head of the rule.
   car r;

procedure krule_tail(r);
   % Tailpolynomial. [r] is a rule. Returns the tail of the rule.
   cdr r;

procedure krule_genrule(h,tt);
   % Generate rule. [h] is a monomial, [t] is a polynomial. Returns a
   % rule with [h] as head and [t] as tail.
   (h . tt);

procedure krule_rule2poly(r);
   % Convert rule into a polynomial. [r] is a rule. Returns a
   % polynomial.
   kpoly_plus {krule_head r,krule_tail r};

procedure krule_poly2rule(p);
   % Convert a polynomial into a rule. [p] is a polynomial. Returns a
   % rule or 'failed if no unique head monomial can be choosen.
   begin scalar monlist;
      if kpoly_monomialp p then return (p . 0);
      monlist := sort(cdr p,'ibalp_torderp);
      return krule_genrule(car monlist,kpoly_plus cdr monlist)
   end;

endmodule; %[krule]


module kpoly;
% Kapur Polynomials

% DS
% <POLY> ::= <MONOMIAL> | ('plus,...,<MONOMIAL>,...)
% <MONOMIAL> ::= 0 | 1 | <ID> | ('times,...,<ID>,...)

procedure kpoly_times(l);
   % Polynomial times. [l] is a non-empty list of polynomials. Returns
   % the product of the polynomials in [l].
   begin scalar setlvar,setlsum,curpoly;
      l := ibalp_remnested(l,'times);
      if 0 member l then return 0;
      for each j in l do
         if atom j and not eqn(j,1) then
            setlvar := lto_insert(j,setlvar)
         else if eqcar(j,'plus) then
            setlsum := lto_insert(j,setlsum);
      setlvar := sort(setlvar,'ordop);
      if null setlsum then
         return kpoly_norm ('times . setlvar);
      if null setlvar and null cdr setlsum then
         return car setlsum;
      if setlvar then
         curpoly := kpoly_norm ('times . setlvar)
      else <<
         curpoly := car setlsum;
         setlsum := cdr setlsum
      >>;
      while setlsum do <<
         curpoly := kpoly_times2(curpoly,car setlsum);
         setlsum := if not eqn(curpoly,0) then cdr setlsum
      >>;
      return curpoly
   end;

procedure kpoly_times2(p1,p2);
   % Polynomial times 2. [p1] and [p2] are polynomials. Returns a
   % polynomial which is the product of [p1] and [p2].
   if kpoly_monomialp p1 and kpoly_monomialp p2 then
      kpoly_times2monoms(p1,p2)
   else if kpoly_monomialp p1 then
      kpoly_times2monomsum(p1,p2)
   else if kpoly_monomialp p2 then
      kpoly_times2monomsum(p2,p1)
   else
      kpoly_times2sums(p1,p2);

procedure kpoly_times2sums(s1,s2);
   % Polynomial times 2 sums. [s1] and [s2] are lists starting with
   % 'plus. Returns a polynomial being the multiplication of [s1] and
   % [s2].
   kpoly_plus for each j in cdr s1 collect
      kpoly_times2monomsum(j,s2);

procedure kpoly_times2monomsum(m,s);
   % Polynomial times2 monomial and sum. [m] is a monomial. [s] is a
   % list starting with 'plus. Returns a polynomial which is the
   % product of [m] and [s].
   if kpoly_monomialp s then
      kpoly_times2monoms(m,s)
   else
      kpoly_plus for each j in cdr s collect
         kpoly_times2monoms(m,j);

procedure kpoly_times2monoms(m1,m2);
   % Polynomial times 2 monomials. [m1] and [m2] are monomials.
   % Returns a monomial containing all identifiers of [s1] and [s2].
   % The result list is sorted lexicographically.
   begin scalar setl;
      if atom m1 then
         if eqn(m1,1) then
            return m2
         else
            setl := lto_insert(m1,setl)
      else
      	 for each j in cdr m1 do setl := lto_insert(j,setl);
      if atom m2 then
         if eqn(m2,1) then
            return m1
         else
            setl := lto_insert(m2,setl)
      else
      	 for each j in cdr m2 do setl := lto_insert(j,setl);
      return kpoly_norm ('times . sort(setl,'ordop))
   end;

procedure kpoly_plus(l);
   % Polynomial plus. [l] is a non-empty list of polynomials. Returns
   % a polynomial equated the addition of the polynomials in [l]. The
   % result is sorted using current torder.
   begin scalar tmpl,w;
      tmpl := sort(ibalp_remnested(l,'plus),'ibalp_torderp);
      w := tmpl;
      % remove multiple occurences
      while w and cdr w do
         if eqn(car w,0) then <<
            car w := cadr w;
            cdr w := cddr w
         >> else if car w = cadr w then
           if cddr w then <<
               car w := caddr w;
               cdr w := cdddr w
            >> else <<
               car w := 0;
               cdr w := nil
            >>
         else
            w := cdr w;
      tmpl := delete(0,tmpl);
      return kpoly_norm ('plus . tmpl)
    end;

procedure kpoly_monomialp(p);
   % Monomial predicate. [p] is a polynomial. Returns non-nil if [p]
   % is an atom or a list starting with 'times.
   atom p or eqcar(p,'times);

procedure kpoly_idemppoly(var);
   % Idempotential polynomial. [var] is an identifier. Returns the
   % polynomial var^2 + var.
   {'plus,{'times,var,var},var};

procedure kpoly_norm(p);
   % Normalise. [p] is a polynomial. Returns a polynomial which
   % is in a normalized form (no list if atom).
   if atom p then
      p
   else if null cdr p then
      if eqcar(p,'times) then 1 else 0
   else if null cddr p then
      cadr p
   else
      p;

procedure kpoly_mondivp(m1,m2);
   % Monomial divide predicate. Returns non-nil if [m2] divides [m1].
   begin scalar e1,e2,rsl;
      if eqn(m1,0) or eqn(m2,1) or m1 = m2 then return t;
      if eqn(m2,0) then return nil;
      if atom m1 and atom m2 then return m1 = m2;
      if atom m1 then return nil;
      if atom m2 then return m2 member m1;
      e1 := cdr m1;
      e2 := cdr m2;
      rsl := t;
      while e1 and e2 and rsl do
         if car e1 = car e2 then <<
            e1 := cdr e1;
            e2 := cdr e2
         >>
         else if ordop(car e1,car e2) then
            e1 := cdr e1
         else
            rsl := nil;
      return null e2 and rsl
   end;

endmodule; %kpoly

end;  % of file
