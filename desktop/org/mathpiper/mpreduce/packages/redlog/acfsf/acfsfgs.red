% ----------------------------------------------------------------------
% $Id: acfsfgs.red 67 2009-02-05 18:55:15Z thomas-sturm $
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
   fluid '(acfsf_gs_rcsid!* acfsf_gs_copyright!*);
   acfsf_gs_rcsid!* :=
      "$Id: acfsfgs.red 67 2009-02-05 18:55:15Z thomas-sturm $";
   acfsf_gs_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module acfsfgs;
% Algebraically closed field standard form Groebner simplifier.
% Submodule of [acfsf].

%DS
% <CIMPL> ::= (<GP>, <PROD1>, <PROD2>, <OTHER>)
% <GP> ::= ((<GB> . <PROD>) . <OTHER>)
% <GB> ::= (<SF>,...)
% <PROD> ::= <SF>
% <PROD1> ::= <SF>
% <PROD2> ::= <SF>
% <OTHER> ::= (<ATOMIC_FORMULA>,...)

procedure acfsf_gsc(f,atl);
   % Algebraically closed field Groebner simplification via
   % conjunctive normal form. [f] is a formula; [atl] is a theory.
   % Returns [inconsistent] or a formula equivalent to [f]. The
   % returned formula is somehow simpler than [f]. This procedure
   % temporarily turns off the switches [groebopt] and [rlsiexpla].
   % Accesses the switches [rlverbose], [rlgsvb], [rlgsbnf],
   % [rlgsrad], [rlgssub], [rlgsred], [rlgsprod], [rlgserf],
   % [rlgsutord], and the fluid [rlradmemv!*].
   begin scalar w,svrlgsvb;
      svrlgsvb := !*rlgsvb;
      if !*rlverbose and !*rlgsvb then on1 'rlgsvb else off1 'rlgsvb;
      w := acfsf_gsc1(f,atl);
      onoff('rlgsvb,svrlgsvb);
      return w
   end;

procedure acfsf_gsc1(f,atl);
   % Algebraically closed field Groebner simplification via
   % conjunctive normal form subroutine. [f] is a formula; [atl] is a
   % theory. Returns [inconsistent] or a formula equivalent to [f].
   % The returned formula is somehow simpler than [f]. This procedure
   % temporarily turns off the switches [groebopt] and [rlsiexpla].
   % Accesses the switches [rlgsvb], [rlgsbnf], [rlgsrad], [rlgssub],
   % [rlgsred], [rlgsprod], [rlgserf], [rlgsutord], and the fluid
   % [rlradmemv!*].
   begin scalar phi,!*rlsiexpla;  % Hack, but otherwise phi is not a bnf!
      if !*rlgsbnf then <<
      	 if !*rlgsvb then ioto_prin2 "[CNF";
      	 phi := cl_simpl(cl_cnf cl_nnf f,atl,-1);
      	 if !*rlgsvb then ioto_prin2 "] "
      >> else
	 phi := cl_simpl(f,atl,-1);
      if phi eq 'inctheo then return 'inctheo;
      if rl_tvalp phi then
	 return phi;
      phi := acfsf_gssimplify0(phi,atl);
      if phi eq 'inctheo then return 'inctheo;
      return cl_simpl(phi,atl,-1)
   end;

procedure acfsf_gsd(f,atl);
   % Algebraically closed field Groebner simplification via
   % disjunctive normal form. [f] is a formula; [atl] is a theory.
   % Returns [inconsistent] or a formula equivalent to [f]. The
   % returned formula is somehow simpler than [f]. This procedure
   % temporarily turns off the switches [groebopt] and [rlsiexpla].
   % Accesses the switches [rlverbose], [rlgsvb], [rlgsbnf],
   % [rlgsrad], [rlgssub], [rlgsred], [rlgsprod], [rlgserf],
   % [rlgsutord], and the fluid [rlradmemv!*].
   begin scalar w,svrlgsvb;
      svrlgsvb := !*rlgsvb;
      if !*rlverbose and !*rlgsvb then on1 'rlgsvb else off1 'rlgsvb;
      w := acfsf_gsd1(f,atl);
      onoff('rlgsvb,svrlgsvb);
      return w
   end;

procedure acfsf_gsd1(f,atl);
   % Algebraically closed field Groebner simplification via
   % disjunctive normal form subroutine. [f] is a formula; [atl] is a
   % theory. Returns [inconsistent] or a formula equivalent to [f].
   % The returned formula is somehow simpler than [f]. This procedure
   % temporarily turns off the switches [groebopt] and [rlsiexpla].
   % Accesses the switches [rlgsvb], [rlgsbnf], [rlgsrad], [rlgssub],
   % [rlgsred], [rlgsprod], [rlgserf], [rlgsutord], and the fluid
   % [rlradmemv!*].
   begin scalar phi,!*rlsiexpla;  % Hack, but otherwise phi is not a bnf!
      if !*rlgsbnf then <<
      	 if !*rlgsvb then ioto_prin2 "[DNF";
      	 phi := cl_simpl(cl_nnfnot cl_dnf f,atl,-1);
      	 if !*rlgsvb then ioto_prin2 "] ";
      >> else
      	 phi := cl_simpl(cl_nnfnot f,atl,-1);
      if phi eq 'inctheo then return 'inctheo;
      if rl_tvalp phi then
   	 return cl_nnfnot phi;
      phi := acfsf_gssimplify0(phi,atl);
      if phi eq 'inctheo then return 'inctheo;
      return cl_simpl(cl_nnfnot phi,atl,-1)
   end;

procedure acfsf_gsn(f,atl);
   % Algebraically closed field Groebner simplification via normal
   % form. [f] is a formula; [atl] is a theory. Returns [inconsistent]
   % or a formula equivalent to [f]. The returned formula is somehow
   % simpler than [f]. The normal form used depends on the toplevel
   % operator of [f]. This procedure temporarily turns off the
   % switches [groebopt] and [rlsiexpla]. Accesses the switches
   % [rlverbose], [rlgsvb], [rlgsbnf], [rlgsrad], [rlgssub],
   % [rlgsred], [rlgsprod], [rlgserf], [rlgsutord], and the fluid
   % [rlradmemv!*].
   if rl_tvalp f then
      f
   else if cl_atflp(rl_argn f) then
      if rl_op(f) eq 'and then acfsf_gsd(f,atl) else acfsf_gsc(f,atl)
   else
      if rl_op(f) eq 'and then acfsf_gsc(f,atl) else acfsf_gsd(f,atl);

procedure acfsf_gssimplify0(f,atl);
   % Algebraically closed field Groebner simplify. [f] is a
   % conjunction of disjunctions of atomic formulas, a disjunction of
   % atomic formulas, or an atomic formula; [atl] is a theory. Returns
   % [inctheo] or a formula. Accesses the switches [rlgsvb], [rlgsrad],
   % [rlgssub], [rlgsred], [rlgsprod], [rlgserf], [rlgsutord], and the
   % fluid [rlradmemv!*].
   begin scalar acfsf_gstv!*,!*cgbverbose,!*groebopt;
      return acfsf_gssimplify(f,atl)
   end;

procedure acfsf_gssimplify(f,atl);
   % Algebraically closed field Groebner simplify. [f] is a
   % conjunction of disjunctions of atomic formulas, a disjunction of
   % atomic formulas, or an atomic formula; [atl] is a theory. Returns
   % [inctheo] or a formula. Accesses the switches [rlgsvb],
   % [rlgsrad], [rlgssub], [rlgsred], [rlgsprod], [rlgserf], and the
   % fluid [rlradmemv!*].
   begin scalar al,gp,ipart,npart,w,gprem,gprodal,gatl;
      atl := cl_sitheo atl;
      if atl eq 'inctheo or acfsf_gsinctheop(atl) then
	 return 'inctheo;
      if (cl_atfp f) or (rl_op f eq 'or) then  % degenerated cnf
	 al := acfsf_gssplit!-cnf {f}
      else
      	 al := acfsf_gssplit!-cnf rl_argn f;
      if w := lto_catsoc('gprem,al) then <<
      	 gp := acfsf_gsextract!-gp atl;
      	 gprem := acfsf_gsgprem(w,gp);
	 if gprem eq 'false then return 'false;
      >>;
      gatl := append(atl,lto_catsoc('gprem,al));
      gp := acfsf_gsextract!-gp(gatl);
      caar gp := acfsf_gsgbf caar gp;
      ipart := lto_catsoc('impl,al);
      npart := lto_catsoc('noneq,al);
      if ipart then
	 ipart := acfsf_gspart(ipart,gp);
      if npart and gatl then
      	 npart := acfsf_gspart(npart,gp);
      if gprem then <<
 	 if null !*rlgsprod then <<
	    gprodal := lto_catsoc('gprodal,al);
	    gprem := acfsf_gssimulateprod(gprem,gprodal)
	 >>;
      	 return rl_smkn('and,gprem . nconc(ipart,npart))
      >>;
      return rl_smkn('and,nconc(ipart,npart))
   end;

procedure acfsf_gspreducef(f,gl);
   numr gb_reducef(f,gl,acfsf_gsvl(),acfsf_gssm(),acfsf_gssx());

procedure acfsf_gsgreducef(f,gl);
   acfsf_gspreducef(f,gb_gbf(gl,acfsf_gsvl(),acfsf_gssm(),acfsf_gssx()));

procedure acfsf_gsgbf(fl);
   gb_gbf(fl,acfsf_gsvl(),acfsf_gssm(),acfsf_gssx());

procedure acfsf_gsvl();
   if !*rlgsutord then append(td_vars(),{acfsf_gstv!*}) else nil;

procedure acfsf_gssm();
   if !*rlgsutord then td_sortmode() else 'revgradlex;

procedure acfsf_gssx();
   if !*rlgsutord then td_sortextension() else nil;


procedure acfsf_gsinctheop(atl);
   % Algebraically closed field standard form Groebner simplifier
   % inconsistent theory predicate. [atl] is a list of atomic
   % formulas. [T] or [nil] is returned.
   begin scalar w;
      if null atl then
     	 return nil;
      if !*rlgsvb then ioto_prin2 "Inctheop... ";
      w := cl_nnfnot acfsf_gsimplication(
       	 cl_nnfnot rl_smkn('and,atl),'((nil . 1) . nil));
      if !*rlgsvb then ioto_prin2t "done.";
      return w eq 'false
   end;

procedure acfsf_gssplit!-cnf(f);
   % Algebraically closed field standard form Groebner simplifier
   % split conjunctive normal form. [f] is an list of disjunctions of
   % atomic formulas. An assoc list is returned. The returned assoc
   % list have the following items. [('impl . imp)] where [imp] is the
   % list off all disjunctions containing at least one inequation,
   % [('gprem . gprem)] where [gprem] is the list of all atomic
   % formulas occuring in [f] and atomic formulas equivalent to
   % disjunctions of inequalities occuring in [f], [('noneq . noneq)]
   % where [noneq] is a list of disjunctions of atomic formulas
   % containing no inequations, and [('gprodal . gprodal)]. The value
   % [gprodal] is a assoc list containing to each equation the product
   % representation, if the equation was extracted from a disjunction.
   begin scalar noneq,imp,prod,gprodal,gprem,w,x;
      for each phi in f do
	 if rl_op phi memq '(and or) then  % [phi] is not an atomic formula
	    if (w := acfsf_gsdis!-type rl_argn phi) eq 'impl then
	       imp := phi . imp
	    else if w eq 'noneq then
	       noneq := phi . noneq
	    else << % [if w eq 'equal then]
	       prod := 1;
	       for each atf in rl_argn phi do
	       	  prod := multf(prod,acfsf_arg2l atf);
	       x := acfsf_0mk2('equal,prod);
	       gprem := x . gprem;
	       gprodal := (x . phi) . gprodal
	    >>
	 else
	    gprem := phi . gprem;
      if !*rlgsvb then <<
	 ioto_tprin2t {"global: ",length gprem,"; impl: ",length imp,
 	    "; no neq: ",length noneq, "; glob-prod-al: ",length gprodal,"."}
      >>;
      return { 'impl . imp, 'noneq . noneq, 'gprem . gprem, 'gprodal . gprodal}
   end;

procedure acfsf_gsdis!-type(atl);
   % Algebraically closed field standard form Groebner simplifier
   % disjunction type. [atl] is a non null list of atomic formulas.
   % ['equal], ['impl], or ['noneq] is returned. ['equal] is returned
   % if and only if all atomic formulas have the relation [equal];
   % [impl] is returned, if and only if one of the atomic formula is
   % an equality, otherwise [noneq] is returned.
   begin scalar op,w;
      if null atl then return 'equal;
      op := acfsf_op car atl;
      if op eq 'neq then return 'impl;
      w := acfsf_gsdis!-type cdr atl;
      if w eq 'impl then return 'impl;
      if op eq 'equal and w eq 'equal then return 'equal;
      return 'noneq
   end;

procedure acfsf_gsextract!-gp(atl);
   % Algebraically closed field standard form extract global premise.
   % [atl] is a list of atomic formulas. A GP is returned.
   begin scalar w;
      w := acfsf_gsdis2impl(for each at in atl collect acfsf_negateat(at));
      return ( (car w . multf(cadr w, caddr w)) . cadddr w)
   end;

procedure acfsf_gsgprem(atl,gp);
   % Algebraically closed field standard form Groebner simplifier
   % simplify global premise. [atl] is a list of atomic formulas; [gp]
   % is a GP. A formula is returned.
   begin scalar w;
      if !*rlgsvb then ioto_prin2 "[GP";
      w := cl_nnfnot acfsf_gsimplication(cl_nnfnot rl_smkn('and,atl),gp);
      if !*rlgsvb then ioto_prin2 "] ";
      return w
   end;

procedure acfsf_gspart(part,gp);
   % Algebraically closed field standard form Groebner simplify
   % simplify part. [part] is a list of disjunctions of atomic
   % formulas and atomic formulas. [gp] is a GP. A list [l] of
   % disjunctions of atomic formulas and atomic formulas is returned.
   % The formula on position $i$ in [l] is somehow simpler than the
   % formula on the position $i$ in part. Supposed that the formula
   % $\bigwedge(g_i=0)$ is true where $g_i$ are the terms in [gp] then
   % the positional corresponding fomulas in the two lists [part] and
   % [l] are equivalent.
   begin scalar w,curlen,res;
      if !*rlgsvb then curlen := length part;
      res := for each phi in part collect <<
	 if !*rlgsvb then ioto_prin2 {"[",curlen};
      	 w := acfsf_gsimplication(phi,gp);
	 if !*rlgsvb then << curlen := curlen - 1; ioto_prin2 {"] "} >>;
	 w
      >>;
      if !*rlgsvb then ioto_cterpri();
      return res
   end;

procedure acfsf_gsimplication(f,gp);
   % Algebraically closed field standard form Groebner simplification
   % implication. [f] is a disjunction of atomic formulas or an atomic
   % formula. [gp] is a GP. Returns a formula. It is a truth value, an
   % atomic formula or a disjunction of atomic formulas, unless the
   % simplification of an atomic formula yields a complex formula.
   begin scalar prem,prod1,prod2,gprod,rprod,iprem,w,z,atl,natl;
      if cl_cxfp f then atl := rl_argn f else atl := {f};
      w := acfsf_gsdis2impl atl;
      iprem := car w;
      prod1 := cadr w;
      prod2 := caddr w;
      gprod := cdar gp;
      prem := append(iprem,caar gp);
      if null prem then return f;
      prem := acfsf_gsgbf prem;
      z := numr simp acfsf_gsmkradvar();
      rprod := acfsf_gseqprod(prod1,prod2,gprod,prem,z);
      if rprod eq 'true then <<
	 if !*rlgsvb then ioto_prin2 "!";
	 return 'true
      >>;
      w := acfsf_gsusepremise(cdr gp,prem,z);
      if w eq 'true then <<
      	 if !*rlgsvb then ioto_prin2 "!";
	 return 'true
      >>;
      natl := acfsf_gsredatl(atl,prem,z,rprod);
      if natl eq 'true then <<
      	 if !*rlgsvb then ioto_prin2 "!";
	 return 'true
      >>;
      if rprod and rprod neq 'false then natl := rprod . natl;
      natl := nconc(natl,acfsf_gspremise(iprem,caar gp));
      return rl_smkn('or,natl)
   end;

procedure acfsf_gsredatl(atl,prem,z,rprod);
   % Algebraically closed field standard form reduce atomic formula
   % list. [atl] is a list of SF's; [prem] is a Groebner basis; [z] is
   % a kernel; [rprod] is a flag. Returns ['true] or a list of atomic
   % formulas.
   begin scalar a,w,natl;
      while atl do <<
	 a := car atl;
      	 atl := cdr atl;
	 w := acfsf_gsredat(a,prem,z,rprod);
	 if w eq 'true then
	    atl := nil
	 else if w and w neq 'false then
	    natl := w . natl
      >>;
      if w eq 'true then return 'true;
      return natl
   end;

procedure acfsf_gsusepremise(atl,prem,z);
   % Algebraically closed field standard form use premise. [atl] is a
   % list of atomic formulas; [prem] is a Groebner basis; [z] is a
   % kernel. returns [nil] or ['true].
   begin scalar w;
      while atl do <<
	 w := acfsf_gsredat(car atl,prem,z,nil);
	 if w eq 'true then
	    atl := nil
	 else
	    atl := cdr atl;
      >>;
      if w eq 'true then return 'true;
   end;

procedure acfsf_gseqprod(iprod1,iprod2,gprod,prem,z);
   % Algebraically closed field standard form equation product.
   % [iprod1], [iprod2], and [prem] are SF's; [prem] is a list of
   % SF's; [z] is a kernel. Returns [nil] or a formula.
   begin scalar p,w;
      p := multf(iprod1,multf(iprod2,gprod));
      % Comment the test on [!*rlgsrad] out if the radical membership
      % test should always be performed for the equation product.
      if !*rlgsrad and
	 (null acfsf_gsgreducef(1,addf(1,negf multf(p,z)) . prem))
      then
	 return 'true;
      w := acfsf_gstryeval('equal,acfsf_gspreducef(p,prem));
      if rl_tvalp w then return w;
      if null !*rlgsprod then return nil;
      if !*rlgsred then
	 return  acfsf_0mk2('equal,acfsf_gspreducef(iprod1,prem));
      return acfsf_0mk2('equal,iprod1);
   end;

procedure acfsf_gsmkradvar();
   % Algebraically closed field standard form Groebner simplifier make
   % radical memebership test variable. Returns an identifier that is
   % not used as an algebraic mode variable.
   begin scalar w; integer n;
      w := 'rlgsradmemv!*;
      while get(w,'avalue) do
	 w := mkid(w,n := n+1);
      if !*rlgsutord then
	 acfsf_gsupdtorder w;
      return w;
   end;

procedure acfsf_gsupdtorder(v);
   % Algebraically closed field standard form Groebner simplifier
   % update term order. [v] is a kernel. Inserts the main variable [v]
   % into the variable list of the global fixed term order of the
   % [groebner] package. Not all torders are supported, if a variable
   % list is present. To get over this problem one can insert the tag
   % variable [v] in the variable list before calling the Groebner
   % simplifier.
   if td_vars() and v memq td_vars() then   % vl needs update
      if not(td_sortmode() memq '(lex gradlex revgradlex gradlexgradlex
	 gradlexrevgradlex lexgradlex lexrevgradlex weighted))
      then
	 rederr {"term order",td_sortmode(), "not supported"}
      else
	 acfsf_gstv!* := v;

procedure acfsf_gstryeval(rel,lhs);
   % Algebraically closed field standard form try evaluation. [rel] is
   % an acfsf-relation; [lhs] a SF. returns [nil], a truth value or an
   % atomic formula. In the first case the atomic formula $([lhs]
   % [rel] 0)$ cannot be evaluated or should be ignored. In the other
   % case the returned value is equivalent to the the atomic formula.
   begin scalar w,!*rlsiexpla;
      if !*rlgserf then <<
      	 w := cl_simplat(acfsf_0mk2(rel,lhs),nil);
      	 return if rl_tvalp w then w
      >>;
      if domainp lhs then
	 return cl_simplat(acfsf_0mk2(rel,lhs),nil)
   end;

procedure acfsf_gsdis2impl(atl);
   % Algebraically closed field standard form Groebner simplifier
   % disjunction to implication. [atl] is a list of atomic formulas. A
   % CIMPL is returned. The classification of the atomic formulas in
   % [atl] is done by [acfsf_attype].
   begin scalar prem,prod1,prod2,other,w,a;
      prod1 := prod2 := 1;
      for each at in atl do <<
	 w := acfsf_gsattype at;
	 if w then <<
	    a := car w;
	    if a eq 'equal then
	       prod1 := multf(cdr w,prod1)
	    else if a eq 'cequal then
	       prod2 := multf(cdr w,prod2)
	    else if a eq 'neq then
	       prem := cdr w . prem
	    else
	       rederr {"BUG IN ACFSF_GSDIS2IMPL",car w}
	 >>;
	 if not (w memq '(equal neq)) then
   	    other := at . other
      >>;
      return {prem, prod1, prod2, other};
   end;

procedure acfsf_gsattype(at);
   % Algebraically closed field standard form Groebner simplifier
   % atomic formula type. [at] is an atomic formula. [nil] or a pair
   % $(\rho,p)$ is returned. $\rho$ is either ['equal], ['neq], or
   % ['cequal]; $p$ is a SF.
   (if w eq 'equal then
      ('equal . acfsf_arg2l at)
   else if w memq '(geq leq) then
      ('cequal . acfsf_arg2l at)
   else if w eq 'neq then
      ('neq . acfsf_arg2l at)) where w=acfsf_op at;

procedure acfsf_gsredat(at,gb,z,flag);
   % Algebraically closed field standard form Groebner simplifier
   % reduce atomic formula. [at] is an atomic formula; [gb] is a
   % Groebner basis; [z] is a variable; [flag] is a flag. [nil], a
   % truth value or an atomic formula is returned. The behavior of
   % this procedure depends on the switches [rl_gsred] and [rl_gsrad].
   % [nil] is returned if the atomic formula belongs to the premise or
   % [flag] is [T] and [at] is an equation. Is [flag] is non [nil]
   % then equations can be ignored. In the other cases the returned
   % value is equivalent to [at]. The intention of this procedure is
   % the reduction of [at] wrt. the radical generated by [gb].
   begin scalar w,x,op,arg,nat;
      op := acfsf_op at;
      if (op eq 'neq) or (flag and op eq 'equal) then return nil;
      arg := acfsf_arg2l at;
      w := acfsf_gspreducef(arg,gb);
      if !*rlgsred then
      	 nat := cl_simplat(acfsf_0mk2(op,w),nil)
      else
	 if x := acfsf_gstryeval(op,w) then
	    nat := x
      	 else
	    nat := at;
      if (rl_tvalp nat) or (op eq 'equal) or (null !*rlgsrad) then
	 return nat;
      if null acfsf_greducef(1,addf(1,negf multf(w,z)) . gb) then
	 return cl_simplat(acfsf_0mk2(op,nil),nil);
      return nat;
   end;

procedure acfsf_gspremise(tl,gp);
   % Algebraically closed field standard form Groebner simplify
   % premise. [tl] and [gp] are lists of SF's. A list of atomic
   % formulas is returned. The behavior of this procedure depends on
   % the switches [rl_gsred] and [rl_gssub]. The conjunction over the
   % returned formulas is equivalent to the formula $\bigvee(t_i \neq
   % 0)$ supposed that $\bigwedge(g_j = 0)$, where $t_i$ are the terms
   % in [tl] and $g_j$ are the terms in [gp]. If the switch [rl_gsred]
   % is on then all terms $t_i$ are reduced modulo Id([gp]). If the
   % switch [!*rl_gssub] is on, the term list is substituted by the
   % reduced Groebner base of the term list.
   begin scalar gb,rtl,w;
      if !*rlgsred then <<
	 gb := acfsf_gsgbf gp;
	 for each sf in tl do
	    if w := acfsf_gspreducef(sf,gb) then
	       rtl := lto_insert(w,rtl);
      >> else
	 rtl := tl;
      if !*rlgssub then
	 return for each sf in acfsf_gsgbf rtl collect
	    acfsf_0mk2('neq,sf);
      return for each sf in rtl collect
	    acfsf_0mk2('neq,sf)
   end;

procedure acfsf_gssimulateprod(prem,prodal);
   % Algebraically closed field standard form simulate rlprod switch.
   % [prem] is a quantifier free formula. [prodal] is an assoc list
   % containing to some equations its product representation.
   begin scalar w,res;
      if rl_tvalp prem then return prem;
      if cl_atfp prem  and (w := lto_cassoc(prem,prodal)) then
	 return w;
      res := for each f in rl_argn prem collect
	 if cl_atfp f and (w := lto_cassoc(f,prodal)) then w else f;
      return rl_mkn(rl_op prem,res)
   end;

endmodule;  % [acfsfgs]

end;  % of file
