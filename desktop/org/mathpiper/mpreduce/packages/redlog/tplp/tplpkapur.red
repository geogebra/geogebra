% ----------------------------------------------------------------------
% $Id: tplpkapur.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 Thomas Sturm
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
   fluid '(tplp_kapur_rcsid!* tplp_kapur_copyright!*);
   tplp_kapur_rcsid!* :=
      "$Id: tplpkapur.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   tplp_kapur_copyright!* := "Copyright (c) 2007-2009 T. Sturm"
>>;

module tplpkapur;
% Author Stefan Kaeser

% global vars
fluid '(tplp_kapuroptions!* tplp_kapuratf0!* tplp_kapuratf1!*);

% own switches
fluid '(!*rlkapurmultimon !*rlkapursplitequiv);
switch rlkapurmultimon,rlkapursplitequiv;
on1 'rlkapursplitequiv;
on1 'rlkapurmultimon;

% import needed switches and global settings
fluid '(!*rlverbose !*modular);

procedure tplp_setkapuroption(opt,val);
   % Set Kapur option. [opt] is an identifier. [val] is any. Returns
   % any (old setting or nil).
   begin scalar oldopt,oldval;
     if oldopt := atsoc(opt,tplp_kapuroptions!*) then <<
     	oldval := cdr oldopt;
     	cdr oldopt := val
     >>
     else
     	tplp_kapuroptions!* := (opt . val) . tplp_kapuroptions!*;
     return oldval
   end;

procedure tplp_getkapuroption(opt);
   % Get Kapur option. [opt] is an identifier. Returns any.
   lto_catsoc(opt,tplp_kapuroptions!*);

procedure tplp_setkapurumode(umode);
   % Set Kapur umode. [umode] is an identifier or nil. Returns any.
   if umode memq '(kapur direct knf kapurknf) then
      tplp_setkapuroption('polygenmode,umode)
   else
      tplp_setkapuroption('polygenmode,'kapur);

procedure tplp_initkapuroptions();
   % Initialise Kapur options. Returns any.
   <<
      tplp_kapuratf0!* := nil;
      tplp_kapuratf1!* := nil;
      tplp_kapuroptions!* := {
         ('polygenmode . 'kapur)
      }
   >>;

procedure tplp_kapur(f,umode);
   % AM interface for rlkapur. [f] is a formula, [umode] is an identifier.
   % Returns ['true] or ['false].
   begin scalar oldmod,oldswitch,newf,polyset;
      oldmod := setmod 2;
      oldswitch := !*modular;
      on1 'modular;
      tplp_initkapuroptions();
      tplp_setkapurumode(umode);
      if !*rlverbose then <<
         ioto_tprin2t "++++ Starting Kapur theorem proving algorithm.";
         ioto_prin2t {"Polynomial generation method: ",
            tplp_getkapuroption 'polygenmode};
         ioto_tprin2t "-------------------------"
      >>;
      newf := tplp_cons2func rl_simpl(f,nil,-1);
      newf := if rl_qnum newf > 0 then tplp_miniscope newf else newf;
      if !*rlkapursplitequiv then
         newf := for each x in tplp_splitequiv newf collect
            tplp_skolemize(x,nil)
      else
         newf := {tplp_skolemize(newf,nil)};
      if !*rlverbose then
         ioto_tprin2t {"-- Splitting and Skolemisation: ",length newf,
            " Formula(e)"};
      polyset := for each x in newf join tplp_polyset(x,0);
      if !*rlverbose then <<
         ioto_tprin2t {"-- Generated polynomials: ",length polyset};
         ioto_tprin2t "-- Compute Groebner Basis..."
      >>;
      polyset := tplp_gb polyset;
      newf := if 1 member polyset then 'true else 'false;
      setmod oldmod;
      if null oldswitch then off1 'modular;
      return newf
   end;

procedure tplp_splitlist(l);
   % Split list. [l] is a list. Returns a pair of lists. Devides a
   % list into two lists with equal length containing all elements of
   % [l]. New lists are created constructive.
   if null l or null cdr l then
      (l . nil)
   else
      (((car l . car w) . (cadr l . cdr w)) where w=tplp_splitlist cddr l);

procedure tplp_binarize(f);
   % Binarize a formula. [f] is a formula. Returns a formula with just
   % binary operators using associative laws.
   if rl_tvalp rl_op f then
      f
   else if rl_quap rl_op f then
      rl_mkq(rl_op f,rl_var f,tplp_binarize rl_mat f)
   else if rl_op f eq 'not then
      rl_mk1('not,tplp_binarize rl_arg1 f)
   else if rl_junctp rl_op f and lto_lengthp(rl_argn f,3,'geq) then
      (rl_mk2(rl_op f,tplp_binarize rl_smkn(rl_op f,car splitl),
         tplp_binarize rl_smkn(rl_op f,cdr splitl))
            where splitl=tplp_splitlist rl_argn f)
   else if rl_boolp rl_op f then
      rl_mk2(rl_op f,tplp_binarize rl_arg2l f,tplp_binarize rl_arg2r f)
   else
      f;

procedure tplp_miniscope(f);
   % Miniscope. [f] is a formula. Returns a formula
   % where each quantifier is as far inside as possible.
   if rl_quap rl_op f then
      tplp_miniscopeq f
   else if rl_boolp rl_op f then
      rl_mkn(rl_op f,for each x in rl_argn f collect tplp_miniscope x)
   else
      f;

procedure tplp_miniscopeq(f);
   % Miniscope qantifier. [f] is a formula of the form $Q x (\alpha)$.
   % Returns a formula where each quantifier is miniscoped.
   begin scalar var,q,mat;
      q := rl_op f;
      var := rl_var f;
      mat := rl_mat f;
      if not(var memq rl_fvarl mat) then
         return tplp_miniscope mat;
      if rl_op mat eq 'not then
         return rl_mk1('not,tplp_miniscope rl_mkq(cl_flip q,var,rl_arg1 mat));
      if rl_quap rl_op mat then
         return tplp_miniscopeqq f;
      if rl_op mat eq 'equiv then
         return tplp_miniscopeqequiv f;
      if not rl_cxp rl_op mat then
         return f;
      if q eq 'ex then
         return tplp_miniscopeex f;
      return tplp_miniscopeall f
   end;

procedure tplp_miniscopeex(f);
   % Miniscope Exquantor. [f] is a formula of the form $'ex x (\alpha)$.
   % Returns a formula where each quantifier is miniscoped.
   begin scalar g,var,b,free,bound;
      g := rl_mat f;
      var := rl_var f;
      if rl_op g eq 'or then
         return rl_mkn('or,for each x in rl_argn g collect
            tplp_miniscope rl_mkq('ex,var,x));
      if rl_op g eq 'impl then
         return rl_mk2('or,rl_mk1('not,tplp_miniscope
            rl_mkq('all,var,rl_arg2l g)),tplp_miniscope
               rl_mkq('ex,var,rl_arg2r g));
      if rl_op g eq 'repl then
         return rl_mk2('or,tplp_miniscope rl_mkq('ex,var,rl_arg2l g),
            rl_mk1('not,tplp_miniscope rl_mkq('all,var,rl_arg2r g)));
      if rl_op g eq 'and then <<
         for each x in rl_argn g do <<
            b := tplp_miniscope x;
            if var memq rl_fvarl b then
               bound := b . bound
            else
               free := b . free
         >>;
         free := rl_mkq('ex,var,rl_smkn('and,reversip bound)) . free;
         return rl_smkn('and,reversip free)
      >>;
      if rl_boolp rl_op g then
         return rl_mkq('ex,var,rl_mkn(rl_op g,for each x in rl_argn g collect
            tplp_miniscope x));
      return f
   end;

procedure tplp_miniscopeall(f);
   % Miniscope Allquantor. [f] is a formula of the form $'all x (\alpha)$.
   % Returns a formula where each quantifier is miniscoped.
   begin scalar g,var,b,free,bound;
      g := rl_mat f;
      var := rl_var f;
      if rl_op g eq 'and then
         return rl_mkn('and,for each x in rl_argn g collect
            tplp_miniscope rl_mkq('all,var,x));
      if rl_op g eq 'or then <<
         for each x in rl_argn g do <<
            b := tplp_miniscope x;
            if var memq rl_fvarl b then
               bound := b . bound
            else
               free := b . free
         >>;
         free := rl_mkq('all,var,rl_smkn('or,reversip bound)) . free;
         return rl_smkn('or,reversip free)
      >>;
      if not(var memq rl_fvarl rl_arg2l g) then
         return tplp_miniscopeqr f;
      if not(var memq rl_fvarl rl_arg2r g) then
         return tplp_miniscopeql f;
      if rl_boolp rl_op g then
         return rl_mkq('all,var,rl_mkn(rl_op g,for each x in rl_argn g collect
            tplp_miniscope x));
      return f
   end;

procedure tplp_miniscopeqequiv(f);
   % Miniscope quantor over equivalence. [f] is a formula of the form
   % $Q(\alpha 'equiv \beta)$. Returns a formula.
   begin scalar a,b,mat,var,q,arg1,arg2;
      mat := rl_mat f;
      var := rl_var f;
      q := rl_op f;
      a := tplp_miniscope rl_arg2l mat;
      b := tplp_miniscope rl_arg2r mat;
      if var memq rl_fvarl a and var memq rl_fvarl b then
         return rl_mkq(q,var,rl_mk2('equiv,a,b));
      if q eq 'ex then <<
         arg1 := tplp_miniscope rl_mkq('ex,var,rl_mk2('and,
            tplp_clonestruct a,tplp_clonestruct b));
         arg2 := rl_mk1('not,
            tplp_miniscope rl_mkq('all,var,rl_mk2('or,a,b)));
         return rl_mk2('or,arg1,arg2)
      >>;
      arg1 := tplp_miniscope rl_mkq('all,var,rl_mk2('impl,
         tplp_clonestruct a,tplp_clonestruct b));
      arg2 := tplp_miniscope rl_mkq('all,var,rl_mk2('repl,a,b));
      return rl_mk2('and,arg1,arg2)
   end;

procedure tplp_miniscopeqr(f);
   % Miniscope quantor right. [f] is a formula of the form $Qx(\alpha
   % \circ \beta)$ where $x$ does not appear free in $\alpha$. Returns
   % a formula.
   begin scalar newq,op,mat;
      op := rl_op rl_mat f;
      mat := rl_mat f;
      newq := if op eq 'repl then cl_flip rl_op f else rl_op f;
      return tplp_miniscope rl_mk2(op,rl_arg2l mat,
         rl_mkq(newq,rl_var f,rl_arg2r mat))
   end;

procedure tplp_miniscopeql(f);
   % Miniscope quantor left. [f] is a formula of the form $Qx(\alpha
   % \circ \beta)$ where $x$ does not appear free in $\beta$. Returns
   % a formula.
   begin scalar newq,op,mat;
      op := rl_op rl_mat f;
      mat := rl_mat f;
      newq := if op eq 'impl then cl_flip rl_op f else rl_op f;
      return tplp_miniscope rl_mk2(op,rl_mkq(newq,rl_var f,rl_arg2l mat),
         rl_arg2r mat)
   end;

procedure tplp_miniscopeqq(f);
   % Miniscope two quantifiers in row. [f] is a formula with two or
   % more quantifiers as toplevel operators. Returns a formula.
   begin scalar g,var,q1;
      q1 := rl_op f;
      var := rl_var f;
      g := tplp_miniscope rl_mat f;
      if rl_quap rl_op g then
         if rl_op g eq q1 then
            g := rl_mkq(q1,rl_var g,tplp_miniscope rl_mkq(q1,var,rl_mat g))
         else
            g := rl_mkq(q1,var,g)
      else
         g := tplp_miniscope rl_mkq(q1,var,g);
      return g
   end;

procedure tplp_skolemize(f,mark);
   % Skolemize. [f] is a formula, [mark] is boolean. Returns a formula
   % without quantifiers.
   if eqn(rl_qnum f,0) or rl_tvalp rl_op f then
      f
   else if rl_op f eq 'not then
      rl_mk1('not,tplp_skolemize(rl_arg1 f,null mark))
   else if rl_boolp rl_op f then
      if rl_junctp rl_op f then
         rl_mkn(rl_op f,for each x in rl_argn f collect tplp_skolemize(x,mark))
      else if rl_op f eq 'impl then
         rl_mk2('impl,tplp_skolemize(rl_arg2l f,null mark),tplp_skolemize(
            rl_arg2r f,mark))
      else if rl_op f eq 'repl then
         rl_mk2('repl,tplp_skolemize(rl_arg2l f,mark),tplp_skolemize(
            rl_arg2r f,null mark))
      else if rl_op f eq 'equiv then
         tplp_skolemize(rl_mk2('and,rl_mk2('impl,rl_arg2l f,rl_arg2r f),rl_mk2(
            'repl,tplp_clonestruct rl_arg2l f,tplp_clonestruct rl_arg2r f)),
               mark)
      else
         f
   else if rl_quap rl_op f then
      tplp_skolemizeq(f,if rl_op f eq 'ex then null mark else mark,mark)
   else
      f;

procedure tplp_skolemizeq(f,markq,markmat);
   % Skolemize quantifier. Subprocedure of tplp_skolemize. [f] is a
   % formula with a quantifier as toplevel operator. [markq] and
   % [markmat] are boolean. Returns a quantifier-free formula.
   if markq then
      cl_subfof({(rl_var f . tplp_skolemizenewvar())},
         tplp_skolemize(rl_mat f,markmat))
   else
      cl_subfof({(rl_var f . tplp_skolemizenewfkt rl_fvarl f)},
         tplp_skolemize(rl_mat f,markmat));

procedure tplp_genauxpred(varl);
   % Generate auxiliary predicate. [varl] is a list of variables.
   % Returns an atomic formula.
   tplp_mkn(compress ('p . cdr explode gensym()),varl);

procedure tplp_skolemizenewvar();
   % Skolemize generate new variable. Returns an identifier.
   compress ('v . cdr explode gensym());

procedure tplp_skolemizenewfkt(varl);
   % Skolemize generate new skolemfkt. [varl] is a list of variables.
   % Returns a term.
   tplp_fmkn(compress ('s . ('k . cdr explode gensym())),varl);

procedure tplp_remnested(pl,op);
   % Remove nested. [pl] is a list, [op] is an identifier. Returns a
   % list where no sublist is starting with [op] anymore by merging
   % into [pl] (applying the associative law).
   for each j in pl join
      if eqcar(j,op) then
         tplp_remnested(cdr j,op)
      else
         {j};

% change formula into set of polynomials
procedure tplp_polyform(f);
   % Polynomial form. [f] is a quantifier-free formula. Returns a
   % polynomial.
   begin scalar a,b;
      if rl_tvalp rl_op f then
         return if rl_op f eq 'true then 1 else 0;
      if rl_op f eq 'not then
         return kpoly_plus {1,tplp_polyform rl_arg1 f};
      if rl_junctp rl_op f then <<
         if rl_op f eq 'and then
            return kpoly_times tplp_polyformlist rl_argn f;
         return kpoly_plus {1,kpoly_times for each j in
            tplp_polyformlist rl_argn f collect kpoly_plus {1,j}}
      >>;
      if rl_boolp rl_op f then <<
         a := tplp_polyform rl_arg2l f;
         b := tplp_polyform rl_arg2r f;
         if rl_op f eq 'impl then
            return kpoly_plus {1,kpoly_times {a,b},tplp_clonestruct a};
         if rl_op f eq 'repl then
            return kpoly_plus {1,kpoly_times {a,b},tplp_clonestruct b};
         if rl_op f eq 'equiv then
            return kpoly_plus {1,a,b};
         if rl_op f eq 'xor then
            return kpoly_plus {a,b}
      >>;
      % f is an atomic formula
      return tplp_polyformatf f
   end;

procedure tplp_polyformatf(atf);
   % Polynomialform of an atomic formula. [atf] is a atomic formula.
   % Returns a polynomial.
   begin scalar negp,patf;
      if tplp_op atf eq 'negp then <<
         negp := 1;
         patf := tplp_argl atf
      >> else <<
         negp := 0;
         patf := atf
      >>;
      if patf member tplp_kapuratf0!* then return kpoly_plus {0,negp};
      if patf member tplp_kapuratf1!* then return kpoly_plus {1,negp};
      return kpoly_atf2poly atf
   end;

procedure tplp_polyformlist(l);
   % Polynomialform list. [l] is a list of formulae. Returns a list of
   % polynomials.
   for each x in l collect tplp_polyform x;

procedure tplp_genpolyform(f,trthval);
   % Generate polynomial form. [f] is a quantifier free formula.
   % [trthval] is 0 or 1. Returns a polynomial without exponents.
   if eqn(trthval,1) then
      kpoly_plus {1,tplp_polyform f}
   else
      tplp_polyform f;

procedure tplp_splitequiv(f);
   % Split formula on equiv. [f] is a formula. Returns a list of formulae.
   % The first entry is the original formula, where all arguments to equiv
   % are replaced by new predicates. Cdr of the list are additional formulae
   % to ensure the conditions of [f]
   begin scalar argn,newargn,newvar,newarg,fl;
      if rl_tvalp rl_op f then
         return {f};
      if rl_quap rl_op f then
         return {f};
      if rl_op f eq 'equiv then <<
         argn := rl_argn f;
         newargn := for each x in argn collect
            if rl_op x eq 'equiv then
               tplp_splitequiv x
            else if not rl_cxp rl_op x and null tplp_argl x then
               {x}
            else <<
               newvar := tplp_genauxpred rl_fvarl x;
               newarg := tplp_splitequiv x;
               newvar . (rl_mk2('equiv,newvar,car newarg) . cdr newarg)
            >>;
         fl := for each x in newargn join cdr x;
         return (rl_mkn(rl_op f,for each x in newargn collect car x) . fl)
      >>;
      if rl_boolp rl_op f then <<
         argn := rl_argn f;
         newargn := for each x in argn collect tplp_splitequiv x;
         fl := for each x in newargn join cdr x;
         return (rl_mkn(rl_op f,for each x in newargn collect car x) . fl)
      >>;
      return {f}
   end;

procedure tplp_polyset(f,trthval);
   % Generate set of polynomials. [f] is a formula. [trthval] is 0 or
   % 1. Returns a list of polynomials equivalent to [f].
   if tplp_getkapuroption 'polygenmode eq 'knf then
      tplp_pset3knf(f,trthval)
   else if tplp_getkapuroption 'polygenmode eq 'direct then
      tplp_psetdirect(f,trthval)
   else if tplp_getkapuroption 'polygenmode memq '(kapur kapurknf) then
      tplp_psetkapur(f,trthval)
   else
      tplp_psetkapur(f,trthval);

procedure tplp_psetsplitnf(f,trthval);
   % Set of polynomials split normalform. [f] is a formula. [trthval]
   % is 0 or 1. Returns a list of formulae, by transforming [f] into a
   % boolean normalform and splitting on basic junctor.
   begin scalar nfop;
      nfop := if eqn(trthval,0) then 'or else 'and;
      f := if eqn(trthval,0) then tplp_dnf f else tplp_cnf f;
      if rl_op f eq nfop then
         return rl_argn f;
      return {f};
   end;

% umode 3KNF

procedure tplp_pset3knf(f,trthval);
   % Generate set of polynomials 3KNF. [f] is a formula, [trthval] is
   % 0 or 1. Returns a list of polynomials by transforming [f] into a
   % conjunctive clausal form, containing max 3 variables per clause.
   begin scalar newf;
      newf := if eqn(trthval,1) then f else rl_mk1('not,f);
      newf := tplp_pset3knfnf newf;
      newf := tplp_pset3knf2(newf,nil);
      if rl_op newf eq 'and then
         newf :=
            rl_mkn('and,for each j in rl_argn newf join tplp_pset3knf3(j,nil))
      else
         newf := rl_smkn('and,tplp_pset3knf3(newf,nil));
      if null !*rlkapurmultimon then
         if rl_op newf eq 'and then
            newf := rl_mkn('and,for each x in rl_argn newf join
               tplp_psetsplitnf(x,1))
         else
            return for each x in tplp_psetsplitnf(newf,1) collect
               tplp_genpolyform(x,1);
      if rl_op newf eq 'and then
         return
            for each j in rl_argn newf collect tplp_genpolyform(j,1);
      return {tplp_genpolyform(newf,1)}
   end;

procedure tplp_pset3knfnf(f);
   % Generate set of polynomials 3KNF negated form. [f] is a formula.
   % Returns a formula in negated form
   if rl_tvalp rl_op f or null rl_boolp rl_op f then
      f
   else if rl_op f eq 'not then
      if null rl_boolp rl_op rl_arg1 f then
         f
      else
         tplp_pset3knfnf1 rl_arg1 f
   else if rl_junctp rl_op f then
      rl_mkn(rl_op f, for each j in rl_argn f collect tplp_pset3knfnf j)
   else if rl_op f eq 'impl then
      rl_mk2('or,tplp_pset3knfnf rl_mk1('not,rl_arg2l f),
         tplp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'repl then
      rl_mk2('or,tplp_pset3knfnf rl_mk1('not,rl_arg2r f),
         tplp_pset3knfnf rl_arg2l f)
   else
      rl_mk2(rl_op f,tplp_pset3knfnf rl_arg2l f,tplp_pset3knfnf rl_arg2r f);

procedure tplp_pset3knfnf1(f);
   % Generate set of polynomials 3KNF negated form subprocedure 1. [f]
   % is a formula, but not an atomic formula. Returns a formula in
   % negated form assuming the operator before [f] was a 'not.
   if rl_tvalp rl_op f then
      cl_flip rl_op f
   else if rl_op f eq 'not then
      tplp_pset3knfnf rl_arg1 f
   else if rl_junctp rl_op f then
      rl_mkn(cl_flip rl_op f,
         for each j in rl_argn f collect tplp_pset3knfnf rl_mk1('not,j))
   else if rl_op f eq 'impl then
      rl_mk2('and,tplp_pset3knfnf rl_arg2l f,tplp_pset3knfnf
         rl_mk1('not,rl_arg2r f))
   else if rl_op f eq 'repl then
      rl_mk2('and,tplp_pset3knfnf rl_mk1('not,rl_arg2l f),
         tplp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'equiv then
      rl_mk2('equiv,tplp_pset3knfnf rl_mk1('not,rl_arg2l f),
         tplp_pset3knfnf rl_arg2r f)
   else if rl_op f eq 'xor then
      rl_mk2('equiv,tplp_pset3knfnf rl_arg2l f,tplp_pset3knfnf rl_arg2r f);

procedure tplp_pset3knf2(f,intree);
   % Generate set of polynomials 3KNF subprocedure 2. [f] is a formula
   % in negated form, [intree] is boolean. Returns a formula where
   % only the top-level operator 'and is n-ary.
   if null intree and rl_op f eq 'and then
      rl_smkn('and,for each j in rl_argn f join
         ((if rl_op g eq 'and then rl_argn g else {g}) where
            g=tplp_pset3knf2(j,nil)))
   else
      tplp_binarize f;

procedure tplp_pset3knf3(f,clausevar);
   % Generate set of polynomials 3KNF subprocedure 3. [f] is a formula
   % in binary tree negated form. [clausevar] is an identifier or nil.
   % Returns a list of formulae with max three vars per clause.
   begin scalar nvarl,nvarr,returnlist;
      if rl_tvalp rl_op f then
         return {f};
      if rl_op f eq 'not or null rl_boolp rl_op f then
         return {f};
      if null clausevar then <<
         clausevar := tplp_genauxpred rl_fvarl f;
         returnlist := clausevar . returnlist
      >>;
      if rl_op rl_arg2l f eq 'not or null rl_boolp rl_op rl_arg2l f then
         nvarl := rl_arg2l f
      else <<
         nvarl := tplp_genauxpred rl_fvarl rl_arg2l f;
         returnlist := nconc(returnlist,tplp_pset3knf3(rl_arg2l f,nvarl))
      >>;
      if rl_op rl_arg2r f eq 'not or null rl_boolp rl_op rl_arg2r f then
         nvarr := rl_arg2r f
      else <<
         nvarr := tplp_genauxpred rl_fvarl rl_arg2r f;
         returnlist := nconc(returnlist,tplp_pset3knf3(rl_arg2r f,nvarr))
      >>;
      return rl_mk2('equiv,clausevar,rl_mk2(rl_op f,nvarl,nvarr)) . returnlist;
   end;

% umode Kapur

procedure tplp_psetkapur(f,trthval);
   % Generate set of polynomials Kapur. [f] is a formula. [trthval] is
   % 0 or 1. Returns a list of polynomials by transforming [f] using
   % Kapur and Narendrans optimized Method. [trthval] is the trthvalue
   % which should be achieved.
   if tplp_kapuratf0!* = {1} then
      nil
   else if rl_op f eq 'not then
      tplp_psetkapur(rl_arg1 f,if eqn(trthval,0) then 1 else 0)
   else if eqn(trthval,1) then
      tplp_psetkapurcont f
   else
      tplp_psetkapurtaut f;

procedure tplp_psetkapurtaut(f);
   % Generate set of polynomials Kapur tautology. [f] is a formula.
   % Returns a list of polynomials.
   if rl_op f eq 'impl then
      nconc(tplp_psetkapur(rl_arg2l f,1),tplp_psetkapur(rl_arg2r f,0))
   else if rl_op f eq 'repl then
      nconc(tplp_psetkapur(rl_arg2l f,0),tplp_psetkapur(rl_arg2r f,1))
   else if rl_op f eq 'or then
      for each j in rl_argn f join tplp_psetkapur(j,0)
   else if rl_op f eq 'and then
      tplp_psetkapurnary(f,0)
   else
      tplp_psetkapurnoopt(f,0);

procedure tplp_psetkapurcont(f);
   % Generate set of polynomials Kapur contradiction. [f] is a
   % formula. Returns a list of polynomials.
   if rl_op f eq 'and then
      for each j in rl_argn f join tplp_psetkapur(j,1)
   else if rl_op f eq 'impl and rl_op rl_arg2r f eq 'and then
      tplp_psetkapurdistleft(f,1)
   else if rl_op f eq 'repl and rl_op rl_arg2l f eq 'and then
      tplp_psetkapurdistright(f,1)
   else if rl_op f eq 'or then
      tplp_psetkapurnary(f,1)
   else
      tplp_psetkapurnoopt(f,1);

procedure tplp_psetkapurnary(f,trthval);
   % Generate set of polynomials Kapur n-ary subprocedure 1. [f] is a
   % formula with an n-ary toplevel operator. [trthval] is 0 or 1.
   % Returns a list of polynomials by splitting a n-ary boolean
   % formulae into two equivalent polynomials adding auxiliary vars.
   begin scalar distop,argn,newf;
      argn := tplp_remnested(rl_argn f,rl_op f);
      newf := rl_mkn(rl_op f,argn);
      distop := cl_flip rl_op f;
      if lto_lengthp(argn,4,'geq) then
         return tplp_psetkapurnary1(newf,trthval);
      if lto_lengthp(argn,2,'eqn) then
         return
            if rl_op rl_arg2r f eq distop then
              tplp_psetkapurdistleft(newf,trthval)
            else if rl_op rl_arg2l f eq distop then
               tplp_psetkapurdistright(newf,trthval)
            else
               tplp_psetkapurnoopt(newf,trthval);
      return tplp_psetkapurnoopt(newf,trthval)
   end;

procedure tplp_psetkapurnary1(f,trthval);
   % Generate set of polynomials Kapur n-ary subprocedure 1. [f] is a
   % formula with an n-ary toplevel operator. [trthval] is 0 or 1.
   % Returns a list of polynomials by spliting a n-ary boolean
   % formulae into two equivalent polynomials adding auxiliary vars.
   begin scalar partlists,newvar,l1,l2;
      partlists := tplp_splitlist rl_argn f;
      l1 := car partlists;
      l2 := cdr partlists;
      newvar := tplp_genauxpred nil;
      l1 := rl_mkn(rl_op f,newvar . l1);
      l2 := rl_mkn(rl_op f,rl_mk1('not,newvar) . l2);
      return nconc(tplp_psetkapur(l1,trthval),tplp_psetkapur(l2,trthval))
   end;

procedure tplp_psetkapurnoopt(f,trthval);
   % Generate set of polynomials Kapur without possible optimizations.
   % [f] is a formula. [trthval] is 0 or 1. Returns a list of
   % polynomials.
   begin scalar p,fl,expop;
      if rl_boolp rl_op f then <<
         if tplp_getkapuroption 'polygenmode eq 'kapurknf then
            return tplp_pset3knf(f,trthval);
         if null !*rlkapurmultimon then <<
            expop := if eqn(trthval,1) then 'or else 'and;
            fl := tplp_psetsplitnf(f,trthval);
            return for each x in fl join
               if rl_op x eq expop and lto_lengthp(rl_argn x,4,'geq) then
                  tplp_psetkapur(x,trthval)
               else
                  {tplp_genpolyform(x,trthval)}
         >>
      >>;
      p := tplp_genpolyform(f,trthval);
      if not(eqn(p,0)) then return tplp_psetkapurnoopt1(p)
   end;

procedure tplp_psetkapurnoopt1(p);
   % Generate set of polynomials Kapur without possible optimizations 1.
   % [p] is a non-zero polynomial. Returns a list of polynomials.
   if eqn(p,1) then <<
      tplp_kapuratf0!* := {1};
      {1}
   >> else if kpoly_monomialp p and null cdr kpoly_atfl p then <<
      tplp_kapuratf0!* := (car kpoly_atfl p) . tplp_kapuratf0!*;
      {p}
   >> else if cdr kpoly_monlist p and not cddr kpoly_monlist p then
      if eqn(cadr kpoly_monlist p,1) then <<
            tplp_kapuratf1!* := append(tplp_kapuratf1!*,kpoly_atfl
               car kpoly_monlist p) ;
            {p}
      >> else
         {p}
   else
      {p};

procedure tplp_psetkapurdistleft(f,trthval);
   % Generate set of polynomials Kapur left distributivity. [f] is a
   % formula. [trthval] is 0 or 1. Returns a list of polynomials by
   % applying the distributivity rule first.
   for each j in rl_argn rl_arg2r f join
      tplp_psetkapur(rl_mk2(rl_op f,rl_arg2l f,j),trthval);

procedure tplp_psetkapurdistright(f,trthval);
   % Generate set of polynomials Kapur right distributivity. [f] is a
   % formula. [trthval] is 0 or 1. Returns a list of polynomials by
   % applying the distributivity rule first.
   for each j in rl_argn rl_arg2l f join
      tplp_psetkapur(rl_mk2(rl_op f,rl_arg2r f,j),trthval);

procedure tplp_psetdirect(f,trthval);
   % Generate set of polynomials directly. [f] is a formula. [trthval]
   % is 0 or 1. Returns a list of polynomials.
   if null !*rlkapurmultimon then
      for each x in tplp_psetsplitnf(f,trthval) collect
         tplp_genpolyform(x,trthval)
   else
      {tplp_genpolyform(f,trthval)};

% groebner basis procedures
procedure tplp_gb(pl);
   % Groebner Basis. [pl] is a list of first-order polynomials.
   % Returns a list of polynomials which is a Groebner Basis of [pl].
   begin scalar rules,currules,spolyl,lastrule,redpoly,rls1;
      if null pl then return '(0);
      rls1 := {krule_poly2rule 1};
      rules := tplp_gbinitrules pl;
      currules := rules;
      lastrule := lastpair currules;
      while currules and rules neq rls1 do <<
         spolyl := tplp_gbgenspolyl(car currules,rules);
         while spolyl do <<
            redpoly := tplp_gbreducepoly(car spolyl,rules);
            if eqn(redpoly,1) then <<
               rules := rls1;
               cdr spolyl := nil;
               cdr currules := nil
            >> else <<
               cdr lastrule := tplp_gbgenrules redpoly;
               lastrule := lastpair lastrule
            >>;
            spolyl := cdr spolyl
         >>;
         rules := tplp_gbsimplifyonce(rules,car currules);
         currules := cdr currules
      >>;
      return for each j in rules collect krule_rule2poly j
   end;

procedure tplp_gbgenrules(p);
   % Groebner Basis generate rules. [p] is a polynomial not equal to 1.
   % Returns a list of rules which can be generated by [p].
   begin scalar rule;
      if eqn(p,0) then return nil;
      rule := krule_poly2rule p;
      if rule eq 'failed then return nil;
      if eqn(krule_tail rule,1) and kpoly_monomialp krule_head rule then
         return for each x in kpoly_atfl krule_head rule collect
            krule_mkrule(kpoly_atf2poly x,1);
      return {rule}
   end;

procedure tplp_gbreducepoly(p,rules);
   % Groebner Basis reduce polynomial. [p] is a polynomial. [rules] is
   % a list of rules. Returns a polynomial which is in normalform
   % according to the [rules].
   tplp_gbreducepoly1(p,rules,nil);

procedure tplp_gbreducepoly1(p,rules,currule);
   % Groebner Basis reduce polynomial 1. [p] is a polynomial, [rules] is
   % a list of rules, [currule] is a rule or nil. Returns a polynomial.
   begin scalar chnge,p1,p2;
      chnge := t;
      p1 := p;
      p2 := tplp_clonestruct p;
      while chnge do <<
         for each j in rules do
            if null currule or not(j eq currule) then p1 := krule_apply(p1,j);
         chnge := p1 neq p2 and not(eqn(p1,0));
         if chnge then p2 := tplp_clonestruct p1
      >>;
      return p1
   end;

procedure tplp_gbgenspolyl(rule,rules);
   % Groebner Basis generate s-poly list. [rule] is a rule, [rules] is
   % a list of rules which must contain [rule]. Returns a list of
   % polynomials, which can be generated by [rule] and overlaps.
   begin scalar rpoly,spolyl,olaprules,lastpoly,newrule;
      rpoly := tplp_gbreducepoly1(krule_rule2poly rule,rules,rule);
      if numberp rpoly then return {rpoly};
      newrule := krule_poly2rule rpoly;
      newrule := if newrule eq 'failed then rule else newrule;
      spolyl := {0};
      lastpoly := spolyl;
      cdr lastpoly := tplp_gbspolylidemp newrule;
      lastpoly := lastpair lastpoly;
      cdr lastpoly := tplp_gbspolylself newrule;
      lastpoly := lastpair lastpoly;
      olaprules := rules;
      while not(car olaprules eq rule) do <<
         cdr lastpoly := tplp_gbspolyl(newrule,car olaprules);
         lastpoly := lastpair lastpoly;
         olaprules := cdr olaprules
      >>;
      return list2set spolyl
   end;

procedure tplp_gbspolylidemp(rule);
   % Groebner Basis generate s-poly list idempotentials. [rule] is a rule.
   % Returns a list of polynomials, beeing the s-polynomials overlapping
   % [rule] with idempotential rules.
   list2set for each y in kpoly_monlist krule_head rule join
      for each x in kpoly_atfl y join tplp_gbspolyl(rule,krule_idemprule x);

procedure tplp_gbspolylself(rule);
   % Groebner Basis generate s-poly list self-overlap. [rule] is a rule.
   % Returns a list of polynomials, beeing the s-polynomials overlapping
   % [rule] with itself.
   begin scalar plist,ovp;
      plist := krule_headplist rule;
      while cdr plist do <<
         if eqcar(plist,cadr plist) then ovp := t;
         plist := cdr plist
      >>;
      if null ovp then return nil;
      return list2set tplp_gbspolyl(rule,rule)
   end;

procedure tplp_gbspolyl(rule1,rule2);
   % Groebner Basis generate s-poly list. [rule1] and [rule2] are rules.
   % Returns a list of polynomials, which can be generated by overlapping
   % [rule1] and [rule2].
   if null !*rlkapurmultimon or (kpoly_monomialp krule_head rule1
      and kpoly_monomialp krule_head rule2) then
      tplp_gbspolylmm(rule1,rule2)
   else
      tplp_gbspolylmult(rule1,rule2);

procedure tplp_gbspolylmult(rule1,rule2);
   % Groebner Basis generate s-poly list multi monomials. [rule1] and [rule2]
   % are rules with at least one rule has more than one head monomial.
   % Returns a list of polynomials, which can be generated by overlapping
   % [rule1] and [rule2].
   begin scalar unify1p,ml1,ml2,unifl,spolyl,m1,m2,g,spoly;
      ml1 := kpoly_monlist krule_head rule1;
      ml2 := kpoly_monlist krule_head rule2;
      unify1p := cdr ml1 and cdr ml2;
      for each mon1 in ml1 do for each mon2 in ml2 do <<
         unifl := tplp_gbgetunifierlist(mon1,mon2);
         if unify1p then unifl := nil . unifl;
         for each unif in unifl do <<
            m2 := kpoly_subst(unif,mon1);
            m1 := kpoly_subst(unif,mon2);
            g := intersection(kpoly_atfl m1,kpoly_atfl m2);
            m1 := kpoly_atfl2mon setdiff(kpoly_atfl m1,g);
            m2 := kpoly_atfl2mon setdiff(kpoly_atfl m2,g);
            spoly := kpoly_plus {
               kpoly_times {m1,kpoly_subst(unif,krule_tail rule1)},
               kpoly_times {m2,kpoly_subst(unif,krule_tail rule2)},
               kpoly_times {m1,kpoly_plus for each x in ml1 collect
                  if x eq mon1 then 0 else kpoly_subst(unif,x)},
               kpoly_times {m2,kpoly_plus for each x in ml2 collect
                  if x eq mon2 then 0 else kpoly_subst(unif,x)}
            };
            if not eqn(spoly,0) then spolyl := spoly . spolyl
         >>
      >>;
      return list2set spolyl
   end;

procedure tplp_gbgetunifierlist(mon1,mon2);
   % Groebner Basis get list of unifiers. [mon1] and [mon2] are monomials not
   % equal to 0 or 1. Returns a list of unifiers, to unify [mon1] and [mon2]
   % in all possible ways.
   begin scalar unif,unifl;
      for each atf1 in kpoly_atfl mon1 do
         for each atf2 in kpoly_atfl mon2 do <<
            if tplp_op atf1 eq tplp_op atf2 then <<
               unif := tplp_gbgetunifieratf(atf1,atf2);
               if not(unif eq 'failed) then unifl := unif . unifl
            >>
         >>;
      return unifl
   end;

procedure tplp_gbspolylmm(rule1,rule2);
   % Groebner Basis generate s-poly list two monomials. [rule1] and [rule2]
   % are rules with just one head monomial. Returns a list of polynomials,
   % which can be generated by overlapping [rule1] and [rule2].
   begin scalar spolyl,plist1,plist2,sublist;
      plist1 := krule_headplist rule1;
      plist2 := krule_headplist rule2;
      if null intersection(plist1,plist2) then return nil;
      sublist := tplp_gbgetunifierlist(krule_head rule1,krule_head rule2);
      spolyl := for each mgu in sublist collect
         tplp_gbspolyl1(krule_subst(mgu,rule1),krule_subst(mgu,rule2));
      return list2set spolyl
   end;

procedure tplp_gbspolyl1(rule1,rule2);
   % Groebner Basis generate s-poly list subprocedure. [rule1] and [rule2]
   % are overlapping rules. Returns a polynomial, by overlapping [rule1]
   % and [rule2].
   begin scalar headatfl1,tail1,headatfl2,tail2,g,m1,m2;
      tail1 := krule_tail rule1;
      tail2 := krule_tail rule2;
      if krule_head rule1 = krule_head rule2 then
         return kpoly_plus {tail1,tail2};
      headatfl1 := kpoly_atfl krule_head rule1;
      headatfl2 := kpoly_atfl krule_head rule2;
      g := intersection(headatfl1,headatfl2);
      m1 := kpoly_atfl2mon setdiff(headatfl2,g);
      m2 := kpoly_atfl2mon setdiff(headatfl1,g);
      return kpoly_plus {kpoly_times {m1,tail1},kpoly_times {m2,tail2}}
   end;

procedure tplp_mgu(t1,t2);
   % Most general unifier. [t1] and [t2] are terms. Returns an
   % alist or 'failed.
   if idp t1 then
      tplp_mgu1(t1,t2)
   else if idp t2 then
      tplp_mgu1(t2,t1)
   else if tplp_fop t1 eq tplp_fop t2 then
      tplp_mgulist(tplp_fargl t1,tplp_fargl t2)
   else
      'failed;

procedure tplp_mgulist(l1,l2);
   % Most general unifier. [l1] and [l2] are equal length list of terms.
   % Returns an alist or 'failed.
   begin scalar unif,unif2;
      while l1 and not (unif eq 'failed) do <<
         unif2 := tplp_mgu(tplp_subt(unif,car l1),tplp_subt(unif,car l2));
         if unif2 eq 'failed then
            unif := 'failed
         else
            unif := nconc(unif,unif2);
         l1 := cdr l1;
         l2 := cdr l2
      >>;
      return unif
   end;

procedure tplp_mgu1(v,term);
   % Most general unifier 1. [v] is an identifier, [term] is a term.
   % Returns an alist or 'failed.
   if v eq term then
      nil
   else if v memq tplp_varlterm term then
      'failed
   else
      {(v . term)};

procedure tplp_gbgetunifieratf(atf1,atf2);
   % Groebner Basis get atomic formula unifier. [atf1] and [atf2] are
   % atomic formulae. Returns an alist to unifiy [atf1] and [atf2] or
   % 'failed if no unification is possible.
   if tplp_op atf1 eq tplp_op atf2 then
      tplp_mgulist(tplp_argl atf1,tplp_argl atf2)
   else
      'failed;

procedure tplp_gbinitrules(pl);
   % Groebner Basis init ruleslist. [pl] is a non-empty list of polynomials.
   % Returns a list of rules, generated by the polynomials in [pl].
   begin scalar rules,newrule,newp;
      rules := {krule_poly2rule car pl};
      while pl := cdr pl do <<
         newp := tplp_gbreducepoly(tplp_clonestruct car pl,rules);
         if eqn(newp,1) then <<
            if !*rlverbose then ioto_tprin2t "-- 1 in Ideal Initialisation";
            rules := {krule_poly2rule 1};
            pl := nil . nil
         >>
         else if not eqn(newp,0) then <<
            newrule := krule_poly2rule newp;
            if newrule eq 'failed then newrule := krule_poly2rule car pl;
            rules := newrule . rules
         >>
      >>;
      return rules
   end;

procedure tplp_gbsimplifyonce(rules,currule);
   % Groebner Basis simplify rules. [rules] is a list of rules, [currule]
   % is a rule in [rules]. Returns a list of rules, containing [currule]
   % and all other rules are reduced.
   begin scalar w,poly,nrule,head,tail,rule,stopp; integer remr;
      while stopp := null stopp do <<
         w := rules;
         while w and cdr w and cddr w do <<
            if car w eq currule then w := cdr w;
            rule := car w;
            if null !*rlkapurmultimon then <<
               head := tplp_gbreducepoly1(krule_head rule,rules,rule);
               tail := tplp_gbreducepoly1(krule_tail rule,rules,rule);
               poly := kpoly_plus {head,tail}
            >> else
               poly := tplp_gbreducepoly1(krule_rule2poly rule,rules,rule);
            if eqn(poly,0) then <<
               remr := add1 remr;
               car w := cadr w;
               cdr w := cddr w;
               stopp := nil
            >> else if eqn(poly,1) then <<
               remr := -1;
               car w := krule_poly2rule 1;
               rules := {car w};
               w := nil
            >> else <<
               nrule := krule_poly2rule poly;
               if null !*rlkapurmultimon and nrule eq 'failed then
                  nrule := krule_mkrule(krule_head rule,tail);
               car w := nrule;
               w := cdr w
            >>
         >>
      >>;
      if !*rlverbose then
         ioto_prin2 {" [s",if eqn(remr,-1) then "all" else remr,"]"};
      return rules
   end;

procedure tplp_tordertotalp(m1,m2);
   % Totally termorder predicate. [m1] and [m2] are monomials. Returns
   % t if [m1] > [m2], nil if [m1] < [m2] and ordop([m1],[m2]) if [m1]
   % and [m2] are equal or not comparable regarding the current
   % torder.
   (if ord eq 'eq then ordop(m1,m2) else ord) where ord=tplp_torderp(m1,m2);

procedure tplp_tordertotalatfp(m1,m2);
   % Totally Order atomic formula predicate. [m1] and [m2] are atomic
   % formulae. Returns t if [m1] > [m2], nil if [m1] < [m2] and
   % ordop([m1],[m2]) if [m1] and [m2] are equal or not comparable
   % regarding the current torder.
   if tplp_op m1 eq tplp_op m2 then ordop(m1,m2) else tplp_torderatfp(m1,m2);

procedure tplp_torderp(m1,m2);
   % Termorder predicate. [m1] and [m2] are monomials. Returns t if
   % [m1] > [m2], nil if [m1] < [m2] and 'eq if [m1] and [m2] are
   % equal or not comparable regarding the current torder.
   begin scalar curord,atfl1,atfl2; integer l1,l2;
      curord := tplp_torderp1(m1,m2);
      if not(curord eq 'eq) then return curord;
      if m2 = m1 then return 'eq;
      atfl1 := kpoly_atfl m1;
      atfl2 := kpoly_atfl m2;
      l1 := length atfl1;
      l2 := length atfl2;
      if not eqn(l1,l2) then return l1 > l2;
      l1 := for each x in atfl1 sum length tplp_argl x;
      l2 := for each x in atfl2 sum length tplp_argl x;
      if not eqn(l1,l2) then return l1 > l2;
      if kpoly_plist m1 = kpoly_plist m2 then
         return tplp_ordermsetp(atfl1,atfl2,'tplp_torderatfp);
      return tplp_orderlex(kpoly_plist m1,kpoly_plist m2,'tplp_torderordop)
   end;

procedure tplp_torderp1(m1,m2);
   % Termorder predicate 1. [m1] and [m2] are monomials. Returns t if
   % [m1] > [m2], nil if [m1] < [m2] and 'eq if [m1] and [m2] are
   % equal or not comparable in a fast way regarding the current
   % torder.
   if eqn(m2,0) then t
   else if eqn(m1,0) then nil
   else if eqn(m2,1) then t
   else if eqn(m1,1) then nil
   else 'eq;

procedure tplp_torderordop(id1,id2);
   % Termorder ordop. [id1] and [id2] are identifiers. Returns 'eq
   % if [id1] eq [id2], else [ordop(id1,id2)].
   if id1 eq id2 then 'eq else ordop(id1,id2);

procedure tplp_torderatfp(atf1,atf2);
   % Termorder atomic formula predicate. [atf1] and [atf2] are atomic
   % formulae. Returns t if [atf1] > [atf2], nil if [atf1] < [atf2]
   % and 'eq if [atf1] and [atf2] are equal or not comparable
   % regarding the current torder.
   begin scalar t1,t2; integer l1,l2;
      if tplp_op atf1 eq tplp_op atf2 then <<
         t1 := tplp_fmkn('id,tplp_argl atf1);
         t2 := tplp_fmkn('id,tplp_argl atf2);
         return tplp_tordertpb(t1,t2)
      >>;
      l1 := length tplp_argl atf1;
      l2 := length tplp_argl atf2;
      if not eqn(l1,l2) then return l1 > l2;
      return ordop(tplp_op atf1,tplp_op atf2)
   end;

procedure tplp_tordertp(t1,t2);
   % Termorder term predicate. [t1] and [t2] are terms. Returns t if
   % [t1] > [t2], nil if [t1] < [t2] and 'eq if [t1] and [t2] are
   % equal or not comparable regarding the current torder.
   begin scalar preord; integer l1,l2;
      if t1 = t2 then return 'eq;
      if idp t1 or idp t2 then return tplp_tordervtp(t1,t2);
      if tplp_fop t1 eq tplp_fop t2 then return tplp_tordertpb(t1,t2);
      l1 := length tplp_fargl t1;
      l2 := length tplp_fargl t2;
      if l1 > l2 or (eqn(l1,l2) and ordop(tplp_fop t1,tplp_fop t2)) then
         preord := tplp_tordertpa(t1,t2)
      else
         preord := tplp_torderflip tplp_tordertpa(t2,t1);
      if preord eq 'eq then
         return tplp_tordertpc(t1,t2)
      else
         return preord
   end;

procedure tplp_torderflip(ord);
   % Termorder flip. [ord] is boolean or 'eq. Return boolean or 'eq.
   if ord eq 'eq then 'eq else null ord;

procedure tplp_tordervtp(t1,t2);
   % Termorder variable term predicate. [t1] and [t2] are terms where
   % at least one has to be a variable. Returns t if [t1] > [t2], nil
   % if [t1] < [t2] and 'eq if [t1] and [t2] are equal or not
   % comparable regarding the current torder.
   if idp t1 and idp t2 then
      'eq
   else if idp t1 then
      if t1 memq tplp_varlterm t2 then
         nil
      else
         'eq
   else
      if t2 memq tplp_varlterm t1 then
         t
      else
         'eq;

procedure tplp_tordertpa(t1,t2);
   % Termorder term predicate case a. [t1] and [t2] are terms. Returns
   % t if [t1] > [t2], nil if [t1] < [t2] and 'eq if [t1] and [t2] are
   % equal or not comparable regarding the current torder. Case a of
   % recursive path ordering means the function symbol of [t1] is
   % greater than [t2].
   begin scalar m2,trth;
      trth := t;
      m2 := tplp_fargl t2;
      while m2 do <<
         trth := tplp_tordertp(t1,car m2);
         if null trth or trth eq 'eq then
            m2 := nil
         else
            m2 := cdr m2
      >>;
      return trth
  end;

procedure tplp_tordertpb(t1,t2);
   % Termorder term predicate case b. [t1] and [t2] are terms. Returns
   % t if [t1] > [t2], nil if [t1] < [t2] and 'eq if [t1] and [t2] are
   % equal or not comparable regarding the current torder. Case b of
   % recursive path ordering means the function symbols of [t1] and
   % [t2] are equivalent.
   tplp_ordermsetp(tplp_fargl t1,tplp_fargl t2,'tplp_tordertp);

procedure tplp_tordertpc(t1,t2);
   % Termorder term predicate case c. [t1] and [t2] are terms. Returns
   % t if [t1] > [t2], nil if [t1] < [t2] and 'eq if [t1] and [t2] are
   % equal or not comparable regarding the current torder. Case c of
   % recursive path ordering.
   begin scalar m1,trth,curord;
      m1 := tplp_fargl t1;
      trth := 'eq;
      while m1 do <<
         curord := tplp_tordertp(car m1,t2);
         if null curord or curord eq 'eq then
            m1 := cdr m1
         else <<
            trth := t;
            m1 := nil
         >>
      >>;
      return trth
   end;

procedure tplp_orderlex(l1,l2,orderproc);
   % Lexicographic ordering. [l1] and [l2] are list, [orderproc] is a
   % function that implements an ordering. Returns t if [l1] > [l2],
   % nil if [l1] < [l2] and 'eq if [l1] and [l2] are equal or not
   % comparable regarding [orderproc].
   begin scalar trth;
      trth := 'eq;
      while l1 and l2 and trth eq 'eq do <<
         trth := apply(orderproc,{car l1,car l2});
         l1 := cdr l1;
         l2 := cdr l2
      >>;
      if trth eq 'eq and null l1 and l2 then return nil;
      if trth eq 'eq and l1 and null l2 then return t;
      return trth
   end;

procedure tplp_ordermsetp(ms1,ms2,orderproc);
   % Multiset ordering predicate. [ms1] and [ms2] are multisets,
   % [orderproc] is a function that implements an ordering. Returns
   % t if [l1] > [l2], nil if [l1] < [l2] and 'eq if [l1] and [l2]
   % are equal or not comparable regarding [orderproc].
   begin scalar isect,chkl;
      isect := intersection(ms1,ms2);
      ms1 := setdiff(ms1,isect);
      ms2 := setdiff(ms2,isect);
      if null ms1 and null ms2 then return 'eq;
      if null ms1 then return nil;
      if null ms2 then return t;
      chkl := ms2;
      for each x in ms1 do chkl := for each y in chkl join
         if apply(orderproc,{y,x}) then {y};
      if null chkl then return t;
      chkl := ms1;
      for each x in ms2 do chkl := for each y in chkl join
         if apply(orderproc,{y,x}) then {y};
      if null chkl then return nil;
      return 'eq
   end;

procedure tplp_permlist(l);
   % List of permutations. [l] is a list. Returns a list containing
   % all Permutations of [l]
   if null l then
      l
   else if null cdr l then
      {l}
   else
      for each x in l join
         for each j in tplp_permlist delete(x,l) collect (x . j);

procedure tplp_permlistn(l,n);
   % List of permutations n. [l] is a list, [n] is a non-negative integer.
   % Returns a list, which is a sublist of all permutations of [l].
   % Just the first [n] positions of [l] will go through all permutations,
   % positions [n]+1 won't change. Example: l = '(1 2 3), n = 1. This
   % will return '((1 2 3) (2 1 3) (3 1 2))).
   if null l then
      l
   else if eqn(n,0) or null cdr l then
      {l}
   else
      for each x in l join
         for each j in tplp_permlistn(delete(x,l),n-1) collect (x . j);


endmodule;  % [tplpkapur]

module krule;
% Kapur Rewriterules

% DS
% <RULE> ::= (<MONOMIAL> . <POLYNOMIAL>) if null !*rlkapurmultimon
% <RULE> ::= (<POLYNOMIAL> . <POLYNOMIAL>) if !*rlkapurmultimon

procedure krule_mkrule(head,tail);
   % Make rule. [head] and [tail] are polynomials. Returns a rule.
   (head . tail);

procedure krule_head(r);
   % Headpolynomial. [r] is a rule. Returns the head of the rule.
   car r;

procedure krule_tail(r);
   % Tailpolynomial. [r] is a rule. Returns the tail of the rule.
   cdr r;

procedure krule_rule2poly(r);
   % Convert rule into a polynomial. [r] is a rule. Returns a
   % polynomial.
   kpoly_plus {krule_head r,krule_tail r};

procedure krule_idemprule(atf);
   % Idempotential rule. [atf] is an atomic formula. Returns the
   % idempotential rule atf^2 -> atf.
   krule_mkrule({'times,kpoly_atf2poly atf,kpoly_atf2poly atf},
      kpoly_atf2poly atf);

procedure krule_poly2rule(p);
   % Convert a polynomial into a rule. [p] is a polynomial. Returns a
   % rule or 'failed if no unique head monomial can be choosen and
   % !*rlkapurmultimon is nil.
   begin scalar monlist,maxmonlist;
      if kpoly_monomialp p then return (p . 0);
      monlist := kpoly_monlist p;
      maxmonlist := kpoly_maxmonlist p;
      if null !*rlkapurmultimon and cdr maxmonlist then
         return 'failed;
      return krule_mkrule(kpoly_plus maxmonlist,
         kpoly_plus setdiff(monlist,maxmonlist))
   end;

procedure krule_subst(al,r);
   % Substitute. [al] is an alist, [r] is an rule. Returns [r] where
   % all substitutions of [al] are used on head and tail.
   krule_mkrule(kpoly_subst(al,krule_head r),kpoly_subst(al,krule_tail r));

procedure krule_apply(p,rule);
   % Apply rule. [p] is a polynomial, [rule] is a rule. Returns a
   % polynomial.
   if rule = '(1 . 0) then
      0
   else if null !*rlkapurmultimon or kpoly_monomialp krule_head rule then
      krule_applymonhead(p,rule)
   else if kpoly_monomialp p then
      p
   else
      krule_applymulthead(p,rule);

procedure krule_applymulthead(p,rule);
   % Apply rule with more than one head monomial. [p] is a polynomial,
   % [rule] is a rule with at least two headmonomials. Returns
   % a polynomial.
   begin scalar restmon,posmonal,headplist,monplist,headlgth,redpolyl;
      if numberp p or kpoly_monomialp p then return p;
      headlgth := length kpoly_monlist krule_head rule;
      if length kpoly_monlist p < headlgth then return p;
      headplist := krule_headplist rule;
      for each mon in kpoly_monlist p do <<
         monplist := kpoly_plist mon;
         if not lto_sublistp(monplist,headplist) then
            restmon := mon . restmon
         else
            posmonal := lto_alinsert(monplist,mon,posmonal)
      >>;
      if null posmonal then return p;
      for each x in posmonal do
         if length cdr x < headlgth then
            restmon := nconc(restmon,cdr x)
         else
            redpolyl := krule_applymulthead1(cdr x,rule) . redpolyl;
      if null redpolyl then return p;
      return kpoly_plus nconc(restmon,redpolyl)
   end;

procedure krule_applymulthead1(mlist,rule);
   % Apply rule with more than one head monomial subprocedure. [mlist] is
   % a list of monomials which shares the same predicate symbols and list
   % length is greater or equal to headlength of [rule].
   % Returns a polynomial.
   begin scalar plist,multp;
      plist := kpoly_plist car mlist;
      while null multp and cdr plist do <<
         multp := car plist eq cadr plist;
         plist := cdr plist
      >>;
      if null multp then return krule_applymulthead1simp(mlist,rule);
      return krule_applymulthead1full(mlist,rule)
   end;

procedure krule_applymulthead1simp(mlist,rule);
   % Apply rule with more than one head monomial subprocedure simple.
   % [mlist] is a list of monomials which shares the same predicate
   % symbols and list length is greater or equal to headlength of [rule].
   % No predicate symbol can appear more than once in a monomial.
   % Returns a polynomial.
   begin scalar perml,monfac,curmon,headml,subal;
      headml := kpoly_monlist krule_head rule;
      perml := tplp_permlistn(mlist,length headml);
      subal := 'failed;
      while perml and subal eq 'failed do <<
         monfac := nil;
         curmon := krule_apply(caar perml,krule_mkrule(car headml,1));
         if curmon neq caar perml then <<
            monfac := curmon;
            subal := krule_applymulthead1simp1(car perml,headml,monfac);
         >>;
         perml := cdr perml
      >>;
      if subal eq 'failed or null monfac then return kpoly_plus mlist;
      curmon := kpoly_times {monfac,krule_rule2poly krule_subst(subal,rule)};
      return kpoly_plus (curmon . mlist)
   end;

procedure krule_applymulthead1simp1(mlist,headml,monfac);
   % Apply multi head monomial1simpl1. [mlist] and [headml] are lists
   % of monomials, [monfac] is a monomial not equal to 0. Returns an
   % alist beeing a substitution, so [monfac]*[headml] will match [mlist]
   % or 'failed if such a substitution is not possible.
   begin scalar al,curmon;
     while headml and not(al eq 'failed) do
         if null kpoly_mondivp(car mlist,monfac) then
            al := 'failed
         else <<
            curmon := kpoly_divmon(car mlist,monfac);
            al :=
               krule_appgetsubaldir(kpoly_atfl curmon,kpoly_atfl car headml,al);
            mlist := cdr mlist;
            headml := cdr headml
         >>;
      return al
   end;

procedure krule_applymulthead1full(mlist,rule);
   % Apply rule with more than one head monomial subprocedure. [mlist]
   % is a list of monomials which shares the same predicate symbols and
   % list length is greater or equal to headlength of [rule].
   % Returns a polynomial.
   begin scalar perml,monfac,headml,subal,curmon;
      headml := kpoly_monlist krule_head rule;
      perml := tplp_permlistn(mlist,length headml);
      subal := 'failed;
      while perml and subal eq 'failed do <<
         subal := krule_applymulthead1full1(car perml,headml);
         if not(subal eq 'failed) then
            monfac := kpoly_divmon(caar perml,kpoly_subst(subal,car headml));
         perml := cdr perml
      >>;
      if subal eq 'failed or null monfac then return kpoly_plus mlist;
      curmon := kpoly_times {monfac,krule_rule2poly krule_subst(subal,rule)};
      return kpoly_plus (curmon . mlist)
   end;

procedure krule_applymulthead1full1(mlist,headml);
   % Apply multi head monomial1full1. [mlist] and [headml] are lists
   % of monomials. Returns an alist beeing a substitution, so
   % [monfac]*[headml] will match [mlist] or 'failed if such a
   % substitution is not possible.
   begin scalar facal,monfac;
      facal := krule_applymongetfacal(
         kpoly_atfl car mlist,kpoly_atfl car headml,kpoly_plist car mlist);
      monfac := lto_catsoc('cofactor,facal);
      monfac := if null monfac then 1 else kpoly_times monfac;
      return krule_applymulthead1full2(mlist,headml,monfac,nil);
   end;

procedure krule_applymulthead1full2(mlist,headml,monfac,subal);
   % Apply multi head monomial1full2. [mlist] and [headml] are lists
   % of monomials. [monfac] is a non-zero monomial and [subal] is an
   % substitution alist. Returns an alist or 'failed.
   begin scalar curmon,curheadmon,suball,nsubal,cursub,curmonfac;
      if null headml or subal eq 'failed then return subal;
      if null kpoly_mondivp(car mlist,monfac) then return 'failed;
      curheadmon := car headml;
      curmon := kpoly_divmon(car mlist,monfac);
      nsubal := 'failed;
      suball := list2set(krule_appgetsuball(curmon,curheadmon,subal));
      while suball and nsubal eq 'failed do <<
         cursub := car suball;
         curmonfac := kpoly_times {monfac,kpoly_divmon(curmon,
            kpoly_subst(cursub,curheadmon))};
         nsubal :=
            krule_applymulthead1full2(cdr mlist,cdr headml,curmonfac,cursub);
         suball := cdr suball
      >>;
      return nsubal;
   end;

procedure krule_appgetsuball(curmon,curheadmon,subal);
   % Apply rule get all substitution alists. [curmon] and [curheadmon]
   % are monomials. [subal] is an alist. Returns a list of alists,
   % containing all possible substitutions for [curheadmon] dividing
   % [curmon].
   begin scalar nsub,suball;
      if subal eq 'failed then return nil;
      if eqn(curheadmon,1) then return {subal};
      for each headx in kpoly_atfl curheadmon do
         for each monx in kpoly_atfl curmon do <<
            nsub := krule_appgetsubal(monx,headx,subal);
            if not(nsub eq 'failed) then
               suball := nconc(suball,krule_appgetsuball(
                  kpoly_divmon(curmon,kpoly_atf2poly monx),
                  kpoly_divmon(curheadmon,kpoly_atf2poly headx),nsub))
            >>;
      return suball
   end;

procedure krule_applymonhead(p,rule);
   % Apply rule with one headmonomial. [p] is a polynomial. [rule] is
   % a rule having a monomial as head. Returns a polynomial.
   if numberp p then
      p
   else if kpoly_monomialp p then
      krule_applymon(p,rule)
   else
      kpoly_plus for each x in kpoly_monlist p collect krule_applymon(x,rule);

procedure krule_applymon(m,rule);
   % Apply rule on monomial. [m] is a monomial, [rule] is a rule not
   % equal to (1 -> 0). Returns a polynomial which is created by
   % applying [rule] once on [m].
   begin scalar plistrule,subal,facal,w,v;
      if numberp m then
         return m;
      plistrule := krule_headplist rule;
      if null lto_sublistp(kpoly_plist m,plistrule) then
         return m;
      if kpoly_mondivp(m,krule_head rule) then return krule_applymon1(m,rule);
      facal := krule_applymongetfacal(kpoly_atfl m,kpoly_atfl krule_head rule,
         plistrule);
      if w := lto_catsoc('singlm,facal) then
         subal := krule_appgetsubaldir(w,lto_catsoc('singlh,facal),nil);
      if subal eq 'failed then
         return m;
      if w := lto_catsoc('multm,facal) then
         subal := krule_appgetsubalmult(w,lto_catsoc('multh,facal),subal);
      if subal eq 'failed then
         return m;
      rule := krule_subst(subal,rule);
      v := kpoly_atfl2mon
         setdiff(lto_catsoc('multm,facal),kpoly_atfl krule_head rule);
      w := kpoly_atfl2mon lto_catsoc('cofactor,facal);
      return kpoly_times {v,w,krule_tail rule};
   end;

procedure krule_appgetsubaldir(atfm,atfr,subal);
   % Apply rule, get substitution alist direct. [atfm] and [atfr] are
   % lists of atomic formulae, having the same predicate symbols,
   % within the same order, [subal] is an alist with already fixed
   % substitutions. Returns an alist which substitutes all
   % vars in [atfr] in a way that [atfr] will be equal to [atfm] after
   % substitution. If such a substitution is not possible, 'failed
   % will be returned.
   if subal eq 'failed or null atfm or null atfr then
      subal
   else
      krule_appgetsubaldir(cdr atfm,cdr atfr,
         krule_appgetsubal(car atfm,car atfr,subal));

procedure krule_appgetsubalmult(atfm,atfr,subal);
   % Apply rule, get substitution alist multi. [atfm] and [atfr] are
   % lists of atomic formulae, having the same predicate symbols.
   % [subal] is an alist of already fixed substitutions. Returns an
   % alist which substitutes all vars in [atfr] in a way that a
   % sublist of [atfm] will be equal to [atfm] after substitution. If
   % such a substitution is not possible, 'failed will be returned.
   begin scalar alatfm,alatfr;
      for each x in atfm do alatfm := lto_alinsert(tplp_op x,x,alatfm);
      for each x in atfr do alatfr := lto_alinsert(tplp_op x,x,alatfr);
      return krule_appgetsubalmult1(alatfm,alatfr,subal);
   end;

procedure krule_appgetsubalmult1(alatfm,alatfr,subal);
   % Apply rule, get substitution alist multi subprocedure. [alatfm]
   % and [alatfr] are alists, car is the predicate symbol, cdr a list
   % of atomic formulae. [subal] is an alist of already fixed
   % substitutions. Returns an alist.
   begin scalar subal1,permlist,atfm,atfr;
      if null alatfm then return subal;
      atfm := cdar alatfm;
      atfr := cdar alatfr;
      permlist := tplp_permlistn(atfm,length atfr);
      subal1 := 'failed;
      while permlist and subal1 eq 'failed do <<
         subal1 := krule_appgetsubaldir(car permlist,atfr,subal);
         if not(subal1 eq 'failed) then
            subal1 := krule_appgetsubalmult1(cdr alatfm,cdr alatfr,subal1);
         permlist := cdr permlist
      >>;
      return subal1
   end;

procedure krule_applymongetfacal(m,head,plistrule);
   % Apply rule on monomial get factors alist. [m] and [head] are
   % lists of atfs,
   % [plistrule] is a list of predicatesymbols in [head]. Returns an
   % alist containing five entries:
   % 'cofactor -> sublist of [m], containing all atfs not appearing in head;
   % 'singlm -> sublist of [m], atfs which appear in [m] once;
   % 'multm -> rest of [m];
   % 'singlh -> sublist of [head] appearing once in head;
   % 'multh -> rest of h.
   begin scalar multl,w,al;
      al := {('cofactor . nil),('singlm . nil),('multm . nil),('singlh . nil),
         ('multh . nil)};
      for each w on m do
         if not(tplp_op car w memq plistrule) then
            al := lto_alinsert('cofactor,car w,al)
         else if cdr w and tplp_op car w eq tplp_op cadr w then <<
            multl := lto_insertq(tplp_op car w,multl);
            al := lto_alinsert('multm,car w,al)
         >> else if tplp_op car w memq multl then
            al := lto_alinsert('multm,car w,al)
         else
            al := lto_alinsert('singlm,car w,al);
      for each x in head do
         if tplp_op x memq multl then
            al := lto_alinsert('multh,x,al)
         else
            al := lto_alinsert('singlh,x,al);
      return for each x in al collect (car x . reversip cdr x)
   end;

procedure krule_appgetsubal(atfm,atfr,al);
   % Apply rule, get substitution alist. [atfm] and [atfr] are atomic
   % formulae. [al] is an alist which might have substitutions already.
   % Returns an alist which substitutes all vars in [atfr] in a way that
   % [atfr] will be equal to [atfm] after substitution. If such a
   % substitution is not possible, 'failed will be returned.
   if tplp_op atfm eq tplp_op atfr then
      krule_appgetsubal2(tplp_argl atfm,tplp_argl atfr,al)
   else
      'failed;

procedure krule_appgetsubal1(termm,termr,al);
   % Apply rule, get substitution alist 1. [termm] and [termr] are
   % terms, [al] is an alist. Returns an alist or 'failed.
   begin scalar olds;
      if atom termr then <<
         olds := atsoc(termr,al);
         if olds and termm neq cdr olds then return 'failed;
         if not olds then return (termr . termm) . al;
         return al
      >>;
      if atom termm then return 'failed;
      if tplp_fop termm neq tplp_fop termr then return 'failed;
      return krule_appgetsubal2(tplp_fargl termm,tplp_fargl termr,al);
   end;

procedure krule_appgetsubal2(termlm,termlr,al);
   % Apply rule, get substitution alist 2. [termlm] and [termlr] are
   % lists of terms with equal length, [al] is an alist.
   % Returns an alist or 'failed.
   if al eq 'failed or null termlm then
      al
   else
      krule_appgetsubal2(cdr termlm,cdr termlr,
         krule_appgetsubal1(car termlm,car termlr,al));

procedure krule_applymon1(m,rule);
   % Apply rule on monomial 1. [m] is a monomial and [rule] is a rule
   % with a single head monomial which divides [m]. Returns a polynomial.
   if rule = '(1 . 0) then
      0
   else if rule = '(1 . 1) then
      m
   else if numberp m then
      m
   else if m = krule_head rule then
      tplp_clonestruct krule_tail rule
   else <<
      for each j in kpoly_atfl krule_head rule do m := delete(j,m);
      kpoly_times {m,tplp_clonestruct krule_tail rule}
   >>;

procedure krule_headplist(rule);
   % Headmonomial predicate list. [rule] is a rule. Returns the list
   % of predicate symbols in the headmonomial(s).
   if kpoly_monomialp krule_head rule then
      kpoly_plist krule_head rule
   else
      kpoly_plist car kpoly_monlist krule_head rule;

endmodule; %[krule]

module kpoly;
% Kapur Polynomials

% DS
% <POLY> ::= <MONOMIAL> | ('plus,...,<MONOMIAL>,...)
% <MONOMIAL> ::= 0 | 1 | <ATF> | ('times,...,<ATF>,...)

procedure kpoly_atf2poly(f);
   % Polynomial form of an atomic formula. [f] is an atomic formula.
   % Returns a monomial.
   if tplp_op f eq 'negp then
      kpoly_plus {1,tplp_clonestruct tplp_argl f}
   else
      tplp_clonestruct f;

procedure kpoly_atfl2mon(atfl);
   % List of atomic formulae to monomial. [atfl] is a list of atomic
   % formulae. Returns a monomial.
   if null atfl then
      1
   else if null cdr atfl then
      car atfl
   else
      kpoly_times atfl;

procedure kpoly_atfl(m);
   % Get list of atomic formulae. [m] is a monomial. Returns a list of
   % atomic formulae appearing in [m].
   if eqcar(m,'times) then cdr m else if not numberp m then {m};

procedure kpoly_monlist(p);
   % Get list of monomials. [p] is a polynomial. Returns a list of
   % monomials in [p].
   if kpoly_monomialp p then
      {p}
   else
      cdr p;

procedure kpoly_times(l);
   % Polynomial times. [l] is a non-empty list of polynomials. Returns
   % the product of the polynomials in [l].
   begin scalar setlvar,setlsum,curpoly;
      l := tplp_remnested(l,'times);
      if 0 member l then return 0;
      for each j in l do
         if null kpoly_monomialp j then
            setlsum := lto_insert(j,setlsum)
         else if not eqn(j,1) then
            setlvar := lto_insert(j,setlvar);
      setlvar := sort(setlvar,'tplp_tordertotalatfp);
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
   % Polynomial times2. [p1] and [p2] are polynomials. Returns a
   % polynomial beeing the product of [p1] and [p2].
   if kpoly_monomialp p1 and kpoly_monomialp p2 then
      kpoly_times2monoms(p1,p2)
   else if kpoly_monomialp p1 then
      kpoly_times2monomsum(p1,p2)
   else if kpoly_monomialp p2 then
      kpoly_times2monomsum(p2,p1)
   else
      kpoly_times2sums(p1,p2);

procedure kpoly_times2sums(s1,s2);
   % Polynomial times 2 sums. [s1] and [s2] are polynomials.
   % Returns a polynomial being the multiplication of [s1] and [s2].
   kpoly_plus for each j in kpoly_monlist s1 collect kpoly_times2monomsum(j,s2);

procedure kpoly_times2monomsum(m,s);
   % Polynomial times2 monomial and sum. [m] is a monomial, [s] is a
   % polynomial. Returns a polynomial beeing the product of [m] and [s].
   if kpoly_monomialp s then
      kpoly_times2monoms(m,s)
   else
      kpoly_plus for each j in kpoly_monlist s collect kpoly_times2monoms(m,j);

procedure kpoly_times2monoms(m1,m2);
   % Polynomial times2 monomials. [m1] and [m2] are monomials.
   % Returns a monomial containing all ATFs of [m1] and [m2]. The
   % result list is sorted.
   if eqn(m1,0) or eqn(m2,0) then
      0
   else if eqn(m1,1) then
      m2
   else if eqn(m2,1) then
      m1
   else
      kpoly_norm ('times .
         sort(union(kpoly_atfl m1,kpoly_atfl m2),'tplp_tordertotalatfp));

procedure kpoly_plus(l);
   % Polynomial plus. [l] is a non-empty list of polynomials. Returns
   % a polynomial equated the addition of the polynomials in [l]. The
   % result is sorted using ordop().
   begin scalar tmpl,w;
      tmpl := 0 . sort(0 . tplp_remnested(l,'plus),'ordop);
      w := tmpl;
      while cdr w do
         if eqn(cadr w,0) then
            cdr w := nil
         else if cadr w = caddr w then
            cdr w := cdddr w
         else
            w := cdr w;
      return kpoly_norm ('plus . cdr tmpl)
    end;

procedure kpoly_divmon(m1,m2);
   % Divide monomial. [m1] and [m2] are monomials where [m2] divides [m1].
   % Returns the monomial [m1/m2].
   if eqn(m2,1) or eqn(m1,0) then
      m1
   else
      kpoly_norm ('times . for each x in kpoly_atfl m1 join
         if not(x member kpoly_atfl m2) then {x});

procedure kpoly_monomialp(p);
   % Monomial predicate. [p] is a polynomial. Returns non-nil if [p]
   % is not starting with 'plus.
   not eqcar(p,'plus);

procedure kpoly_subst(al,p);
   % Substitution. [al] is an alist, [p] is a polynomial. Returns a
   % polynomial.
   if numberp p then
      p
   else if kpoly_monomialp p then
      kpoly_times for each j in kpoly_atfl p collect tplp_subat(al,j)
   else
      kpoly_plus for each j in kpoly_monlist p collect kpoly_subst(al,j);

procedure kpoly_norm(p);
   % Normalise. [p] is a polynomial. Returns a polynomial which is in
   % a normalized form.
   if eqcar(p,'times) then
      if null cdr p then
         1
      else if null cddr p then
         cadr p
      else
         p
   else if eqcar(p,'plus) then
      if null cdr p then
         0
      else if null cddr p then
         cadr p
      else
         p
   else
      p;

procedure kpoly_mondivp(m1,m2);
   % Monomial divide predicate. [m1] and [m2] are monomials.
   % Returns non-nil if [m2] divides [m1].
   eqn(m1,0) or eqn(m2,1) or m1 = m2 or
      (not eqn(m2,0) and not eqn(m1,1) and
      lto_sublistp(kpoly_atfl m1,kpoly_atfl m2));

procedure kpoly_plist(m);
   % Predicate list. [m] is a monomial. Returns the list of
   % predicate symbols in [m].
   if not numberp m then for each j in kpoly_atfl m collect tplp_op j;

procedure kpoly_maxmonlist(p);
   % List of maximal monomials. [p] is a polynomial. Returns the list
   % of maximal monomials in [p].
   begin scalar ml,maxml,curord,insertp;
      if kpoly_monomialp p then return {p};
      ml := kpoly_monlist p;
      maxml := {car ml};
      for each x in cdr ml do <<
         insertp := t;
         for each y in maxml do <<
            curord := tplp_torderp(x,y);
            if curord and not(curord eq 'eq) then
               maxml := delete(y,maxml)
            else if null curord then
               insertp := nil
         >>;
         if insertp then maxml := x . maxml
      >>;
      return maxml
   end;

endmodule; %kpoly

end;  % of file
