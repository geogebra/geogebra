% ----------------------------------------------------------------------
% $Id: guardian.red 475 2009-11-28 14:03:08Z arthurcnorman $
% ----------------------------------------------------------------------
% (c) 1999 Andreas Dolzmann, 1999, 2009 Thomas Sturm
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
   fluid '(gd_rcsid!* gd_copyright!*);
   gd_rcsid!* := "$Id: guardian.red 475 2009-11-28 14:03:08Z arthurcnorman $";
   gd_copyright!* := "(c) 1999 A. Dolzmann, 1999, 2009 T. Sturm"
>>;

module guardian;

create!-package('(guardian guardianschemes guardianprint),nil);

load!-package 'matrix;
load!-package 'redlog;
rl_set '(ofsf);

switch guardian,gdqe,gdsmart;
on1 'guardian;
on1 'gdqe;
on1 'gdsmart;

procedure cquotegex(x);
   % Conditional quote guarded expression.
   if !*guardian then 'gex;

put('gex,'evfn,'gd_reval);
put('gex,'lengthfn,'lengthcdr);

algebraic operator ge,gec,geg,ger;

put('ge,'rtypefn,'cquotegex);

operator mkge;

procedure mkge(x);
   gd_reval(x,nil);

procedure gd_reval(u,v);
   % Reval.
   begin scalar w;
      w := gd_revaleval(u,v);
      w := gd_eta w;
      w := gd_revalsimpl w;
      if !*gdsmart then
	 w := gd_revalsm w;
      return w
   end;

procedure gd_revaleval(u,v);
   % Reval evaluation part.
   if atom u then
      gd_revalatom(u,v)
   else if car u eq 'ge then
      gd_gesimpl gd_flatten('ge . for each x in cdr u collect
 	 {car x,cadr x,gd_revaleval(caddr x,v)})
   else
      gd_gesimpl gd_flatten gd_revalevalop(car u,for each x in cdr u collect
	 gd_revaleval(x,v));

procedure gd_revalatom(u,v);
   % [u] is an atom.
   begin scalar w;
      if null u then typerr("nil","gex");
      if stringp u then typerr({"string",u},"gex");
      if (w := rl_gettype(u)) then <<
	 if w eq 'scalar then
	    return gd_revaleval(reval cadr get(u,'avalue),v)
 	       where !*guardian=nil;
	 if w eq 'gex then
	    return gd_revaleval(cadr get(u,'avalue),v);
	 typerr({w,u},"gex")
      >>;
      % [u] algebraically unbound.
      return {'ge,{'geg,'true,u}}
   end;

procedure gd_flatten(nge);
   % Flatten nested guarded expression.
   begin scalar w;
      return 'ge . for each case in cdr nge join
	 for each subcase in cdr caddr case join <<
	    w := gd_newtype(car case,car subcase);
	    if w then
 	       {{w,{'and,cadr case,cadr subcase},caddr subcase}}
	 >>
   end;

procedure gd_newtype(t1,t2);
   % Compute the type of the new branch. [inner] and [outer] are
   % branch types. [nil] means drop.
   if t1 eq t2 then
      t1
   else if t1 eq 'gec then
      if t2 eq 'geg then
	 'gec
      else  % [t2 eq 'ger]
	 nil
   else if t1 eq 'geg then
      t2
   else  % [t1 eq 'ger]
      if t2 eq 'gec then
	 nil
      else  % [t2 eq 'geg]
	 'ger;

procedure gd_revalevalop(op,gel);
   begin scalar gtag,rgel,gcgammal,gctl;
      gtag := 'geg;
      for each ge in gel do <<
	 if car car cdr ge eq 'ger then <<
	    gtag := 'ger;
	    rgel := ('ge . cdr cdr ge) . rgel
	 >> else  % [car car cdr ge eq 'geg]
	    rgel := ge . rgel;
	 gcgammal := cadr car cdr ge . gcgammal;
	 gctl := caddr car cdr ge . gctl
      >>;
      gcgammal := reversip gcgammal;
      gctl := reversip gctl;
      rgel := reversip rgel;
      return gd_revalevalop1(op,gtag,{gcgammal,gctl},rgel)
   end;

procedure gd_revalevalop1(op,gtag,gcase,gel);
   % [op] is an $n$-ary operator; [gtag] is one of [geg] or [ger];
   % [gcase] is a list $((... \gamma_i ...),(t_1,...,t_n))$; [gel] is
   % a GEL. Returns an NGE.
   begin scalar w,g;
      g := {gtag,'and . car gcase,gd_applyscheme(op,cadr gcase)};
      w := gd_cartprod gel;
      if gtag eq 'geg then
	 w := cdr w;
      return 'ge . g . for each case in w collect
	 {'gec,'and . car case,gd_applyscheme(op,cadr case)}
   end;

procedure gd_applyscheme(op,tl);
   % Returns a GE.
   begin scalar al; integer n;
      for each x in tl do <<
	 n := n + 1;
	 al := (mkid('a,n) . car tl) . al;
	 tl := cdr tl
      >>;
      return sublis(al,gd_getscheme(op,n))
   end;

procedure gd_cartprod(gel);
   % Cartesian product. [gel] is a list of GE's. Returns a list
   % $(...((... \gamma_i ...),(... t_i ...))...)$. The first
   % combination is actually the composition of the first [gel] cases.
   begin scalar w;
      if null cdr gel then
      	 return for each case in cdr car gel collect
 	    {{cadr case},{caddr case}};
      w := gd_cartprod cdr gel;
      return for each case in cdr car gel join
      	 for each x in w collect {cadr case . car x,caddr case . cadr x}
   end;

procedure gd_gesimpl(ge);
   'ge . gd_gcasesimpl cadr ge .
      for each case in cddr ge join gd_casesimpll case;

procedure gd_gcasesimpl(gcase);
   {car gcase,gd_simpl cadr gcase,caddr gcase};

procedure gd_casesimpll(case);
   (if w neq 'false then {{car case,w,caddr case}}) where w=gd_simpl cadr case;

procedure gd_simpl(f);
   rl_prepfof rl_simpl(rl_simp f,nil,-1)
      where !*guardian=nil,!*rlnzden=T,!*rladdcond=nil;

procedure gd_eta(ge);
   % The algebraic evaluator.
   ('ge . for each case in cdr ge collect
      {car case,cadr case,reval caddr case}) where !*guardian=nil;

procedure gd_revalsimpl(ge);
   % Reval sophisticated simplification part.
   gd_revalsimplrmf gd_revalsimplrect gd_revalsimplcc ge;

procedure gd_revalsimplcc(ge);
   % Contract cases.
   begin scalar nw,sc,c;
      for each case in cdr cdr ge do <<
      	 sc := nw;
      	 c := T; while sc and c do <<
	    if caddr car sc = caddr case then <<
	       cadr car sc := gd_simpl {'or,cadr car sc,cadr case};
	       c := nil
	    >>;
	    sc := cdr sc
      	 >>;
      	 if c then
	    nw := case . nw;
      >>;
      return 'ge . car cdr ge . reversip nw
   end;

procedure gd_revalsimplrmf(ge);
   begin scalar ngcase;
      if null !*gdqe then
 	 return ge;
      ngcase := if gd_falsep cadr car cdr ge then
	 {car car cdr ge,'false,caddr car cdr ge}
      else
	 car cdr ge;
      return 'ge . ngcase . for each case in cdr cdr ge join
	 if not gd_falsep cadr case then {case}
   end;

procedure gd_falsep(f);
   % [f] is a quantifier-free formula in Lisp prefix.
   begin scalar !*guardian,!*rlverbose;
      if gd_ckernp f then
	 return nil;
      return rl_prepfof rl_qe(rl_ex(rl_simp f,nil),nil) eq 'false
   end;

procedure gd_revalsimplrect(ge);
   % Recognize true.
   begin scalar sc;
      if gd_truep cadr car cdr ge then
	 ge := 'ge . {car car cdr ge,'true,caddr car cdr ge} . cddr ge;
      sc := cddr ge;
      while sc do <<
	 if gd_truep cadr car sc then <<
	    ge := 'ge . cadr ge . {{car car sc,'true,caddr car sc}};
	    sc := nil
	 >> else
	    sc := cdr sc
      >>;
      return ge
   end;

procedure gd_truep(f);
   % [f] is a quantifier-free formula in Lisp prefix.
   begin scalar !*guardian,!*rlverbose;
      if f eq 'true then
	 return T;
      if null !*gdqe or gd_ckernp f then
	 return nil;
      return rl_prepfof rl_qe(rl_all(rl_simp f,nil),nil) eq 'true
   end;

procedure gd_ckernp(f);
   % Complex kernel predicate. [f] is a quantifier-free formula.
   % [!*guardian] must be zero.
   begin scalar vl,ckern;
      vl := rl_fvarl rl_simp f;
      while vl do
	 if pairp car vl then <<
	    vl := nil;
	    ckern := T
	 >> else
	    vl := cdr vl;
      return ckern
   end;

procedure gd_revalsm(ge);
   % Reval smart. [ge] is a guarded expression. Return a guarded
   % expression.
   begin scalar gcond,scge,thiscase,newgcase;
      gcond := cadr cadr ge;
      scge := cddr ge;
      while scge and not newgcase do <<
	 thiscase := car scge;
	 scge := cdr scge;
	 if cadr thiscase eq 'true or cadr thiscase = gcond then
	    newgcase := 'geg . cdr thiscase
      >>;
      if newgcase then
	 return {'ge,newgcase};
      if not !*gdqe then
	 return ge;
      scge := cddr ge;
      while scge and not newgcase do <<
	 thiscase := car scge;
	 scge := cdr scge;
	 if gd_truep {'impl,gcond,cadr thiscase} then
	    newgcase := 'geg . cdr thiscase
      >>;
      if newgcase then
	 return {'ge,newgcase};
      return ge;
   end;

endmodule;  % [guardian]

end;  % of file
