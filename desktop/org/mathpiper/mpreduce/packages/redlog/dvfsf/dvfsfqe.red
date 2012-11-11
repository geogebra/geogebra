% ----------------------------------------------------------------------
% $Id: dvfsfqe.red 1815 2012-11-02 13:20:27Z thomas-sturm $
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
   fluid '(dvfsf_qe_rcsid!* dvfsf_qe_copyright!*);
   dvfsf_qe_rcsid!* :=
      "$Id: dvfsfqe.red 1815 2012-11-02 13:20:27Z thomas-sturm $";
   dvfsf_qe_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module dvfsfqe;
% Discretely valued field standard form quantifier elimination.
% Submodule of [dvfsf].

procedure dvfsf_varsel(f,vl,theo);
   % Discretely valued field variable selection. [f] is a
   % quantifier-free formula; [vl] is a list of variables to be
   % considered existentially quantified in [f]; [theo] is the current
   % THEORY. Returns a variable that is a good choice to be eliminated
   % next.
   {car vl};

procedure dvfsf_pmultf(u);
   multf(u,numr simp 'p);

procedure dvfsf_pmultsq(u);
   multsq(u,simp 'p);

procedure dvfsf_ipmultsq(u);
   quotsq(u,simp 'p);

%DS
% <ATF_TRANSLATION> ::= (..., (<KEY> . <GUARDED_POINTS>), ...)
% <KEY> ::= [equal] | [c] | [d] | [e] | [f]
% <GUARDED_POINTS> ::= (..., (<GUARD>, <POINT>), ...)
% <GUARD> ::= <ATOMIC_FORMULA>
% <POINT> ::= <SQ>

procedure dvfsf_translat(atf,v,theo,pos,ans);
   % Discretely valued field translate atomic formula. [atf] is an
   % atomic formula $\rho(t_1,t_2)$; [v] is a variable; [theo] is the
   % current THEORY; [pos], [ans] are bool. Returns an
   % ATF_TRANSLATION. If [pos] is non-[nil], then [atf] is treated as
   % $\lnot [atf]$. If [ans] is non-[nil], then the switch [rlqesr] is
   % temporarily turned on.
   begin scalar op,lhs,rhs;
      if not (v memq dvfsf_varlat atf) then
      	 return nil . nil;
      if not pos then atf := dvfsf_negateat atf;
      op := dvfsf_op atf;
      lhs := dvfsf_arg2l atf;
      rhs := dvfsf_arg2r atf;
      if op eq 'neq then
	 return dvfsf_translat2(v,'sdiv,{lhs . rhs});
      if op = 'sdiv or op = 'div or op = 'equal then
	 return dvfsf_translat2(v,op,{lhs . rhs});
      if op eq 'assoc then
	 return dvfsf_translat2(v,'div,{lhs . rhs,rhs . lhs});
      if op eq 'nassoc then
	 return dvfsf_translat2(v,'sdiv,{lhs . rhs,rhs . lhs});
      rederr "BUG IN dvfsf_translat"
   end;

procedure dvfsf_translat2(v,op,l);
   begin scalar m,eqsoll,pr,cl,dl,el,fl,a,aa,b,bb,co;
      if op = 'equal then
	 return {'equal . {dvfsf_mkcsol dvfsf_mkpair(caar l,v)}} . nil;
      for each x in l do <<
	 pr := dvfsf_mkpair(car x,v);
	 a := car pr;
	 b := cdr pr;
	 pr := dvfsf_mkpair(cdr x,v);
	 aa := car pr;
	 bb := cdr pr;
	 if null aa then <<
	    cl := lto_insert(dvfsf_mkcpoint(negf b,a),cl);
	    dl := lto_insert(dvfsf_mkcpoint(bb,a),dl)
	 >> else if null a then <<
	    el := lto_insert(dvfsf_mkcpoint(b,aa),el);
	    fl := lto_insert(dvfsf_mkcpoint(negf bb,aa),fl)
	 >> else <<
	    cl := lto_insert(dvfsf_mkcpoint(negf b,a),cl);
	    dl := lto_insert(dvfsf_mkcpoint(bb,a),dl);
	    el := lto_insert(dvfsf_mkcpoint(b,aa),el);
	    fl := lto_insert(dvfsf_mkcpoint(negf bb,aa),fl);
	    fl := lto_insert(dvfsf_mkcpoint(negf bb,a),fl);
	    co := rl_mkn('and,{dvfsf_0mk2('neq,a),dvfsf_0mk2('neq,aa)});
	    a := !*f2q a;
 	    b := !*f2q b;
   	    aa := !*f2q aa;
 	    bb := !*f2q bb;
	    m := subtrsq(quotsq(bb,aa),quotsq(b,a));
	    el := lto_insert({co,multsq(quotsq(a,aa),m)},el);
	    dl := lto_insert({co,multsq(quotsq(aa,a),m)},dl)
	 >>
      >>;
      return {'c . cl,'d . dl,'e . el,'f . fl} . nil
   end;

procedure dvfsf_mkpair(u,v);
   begin scalar d;
      u := sfto_reorder(u,v);
      d := degr(u,v);
      if d=0 then
	 return nil . reorder u;
      if d=1 then
	 return reorder lc u . reorder red u;
      rederr {"degree violation in",prepf reorder u}
   end;

procedure dvfsf_mkcsol(pr);
   {dvfsf_0mk2('neq,car pr),quotsq(!*f2q negf cdr pr,!*f2q car pr)};

procedure dvfsf_mkcpoint(b,a);
   {dvfsf_0mk2('neq,a),quotsq(!*f2q b,!*f2q a)};

%DS
% <ELIMINATION_SET> ::= (..., <SUBST_CALLS>, ...)
% <SUBST_CALLS> ::= (<FUNCTION> . (..., (<GUARD>,<POINT>), ...))
% <FUNCTION> ::= [dvfsf_qesubcq]([bvl],[theo],[f],[v],<GUARD>,<POINT>)
% <GUARD> ::= <FORMULA>
% <POINT> :: <SQ>

procedure dvfsf_elimset(v,alp);
   % Discretely valued field elimination set. [v] is a variable; [alp]
   % is an ATF_TRANSLATION. Returns an ELIMINATION_SET.
   begin scalar w,esetl,atal,cl,el,m1,m2; integer i;
      atal := car alp;
      cl := lto_catsoc('c,atal);
      el := lto_catsoc('e,atal);
      if !*rlverbose and null el then ioto_prin2 "#u";  % only upper bounds
      esetl := {'true,simp 1} . lto_catsoc('equal,atal);  % 1
      esetl := nconc(lto_catsoc('f,atal),esetl);  % F
      for each d in lto_catsoc('d,atal) do
	 esetl := {car d,dvfsf_ipmultsq cadr d} . esetl;  % p^{-1} d
      for each c in cl do <<
	 esetl := {car c,dvfsf_ipmultsq cadr c} . esetl;  % p^{-1} c
	 if el then <<  % also lower bounds
      	    for each e in el do
	       esetl := {rl_mkn('and,{car c,car e}),
	       	  addsq(cadr e,cadr c)} . esetl;  % e+c
	    for each cc in cl do <<
	       m2 := cadr cc;
	       m1 := subtrsq(cadr c,m2);
	       esetl := {rl_mkn('and,{car c,car cc}),
	       	  addsq(dvfsf_pmultsq m1,m2)} . esetl;  % p(c-cc)+cc
	       % Avoid cosets.
	       w := if dvfsf_p!* neq 0 then
	       	  min(length cl,abs(dvfsf_p!*)-1)
	       else
	       	  length cl;
	       for z := 1:w do
	       	  esetl := {rl_mkn('and,{car c,car cc}),
	       	     addsq(multsq(simp z,m1),m2)} . esetl  % z(c-cc)+cc
	    >>
	 >>
      >>;
      if el then  % also lower bounds
	 esetl := nconc(cl,esetl);  % C
      return {'dvfsf_qesubcq . esetl}
   end;

procedure dvfsf_qesubcq(bvl,theo,f,v,c,u);
   % Substitute conditionally 1 quotient. [f] is a formula; [v] is a
   % variable; [c] is a formula which implies that the denominator of
   % [u] is nonzero; [u] is an SQ. Returns a quantifier-free formula
   % equivalent to $[c] \land [f]([v]/[u])$.
   if (c := cl_simpl(c,nil,-1)) eq 'false then
      nil . 'false
   else
      nil . rl_mkn('and,{c,dvfsf_qesubq(f,v,u)});

procedure dvfsf_qesubq(f,v,u);
   % Substitute quotient. [f] is a formula; [v] is a variable; [u] is
   % an SQ. Returns a quantifier-free formula equivalent to
   % $[f]([v]/[u])$ provided that the denominator of [u] is nonzero.
   cl_apply2ats1(f,'dvfsf_qesubqat,{v,u});

procedure dvfsf_qesubqat(atf,v,u);
   % Substitute quotient into atomic formula. [atf] is an atomic
   % formula; [v] is a variable; [u] is an SQ. Returns a
   % quantifier-free formula equivalent to $[atf]([v]/[u])$ provided
   % that the denominator of [u] is nonzero.
   begin scalar op,al,lhs,rhs;
      op := dvfsf_op atf;
      al := {v . prepsq u};
      lhs := subf(dvfsf_arg2l atf,al);
      if op memq '(equal neq) then
	 return dvfsf_0mk2(op,numr lhs);
      rhs := subf(dvfsf_arg2r atf,al);
      return dvfsf_mk2(op,multf(numr lhs,denr rhs),multf(numr rhs,denr lhs))
   end;

procedure dvfsf_transform(v, f, vl, an, theo, ans, bvl);
   nil;

procedure dvfsf_trygauss(f,vl,theo,ans,bvl);
   begin scalar w,v,fargl;
      if rl_op f = 'and then
      	 fargl := rl_argn f
      else if cl_atfp f then
	 fargl := {f}
      else
	 return 'failed;
      while vl do <<
	 v := car vl;
	 vl := cdr vl;
	 w := dvfsf_findeqsol(fargl,v,theo,ans);
	 if w neq 'failed then <<
 	    w := (v . w) . theo;
 	    vl := nil
 	 >>
      >>;
      return w
   end;

procedure dvfsf_findeqsol(fl,v,theo,ans);
   begin scalar w;
      w := dvfsf_findeqsol1(car fl,v,theo,ans);
      if w neq 'failed then return w;
      if cdr fl then return dvfsf_findeqsol(cdr fl,v,theo,ans);
      return 'failed
   end;

procedure dvfsf_findeqsol1(a,v,theo,ans);
   if cl_cxfp a or dvfsf_op a neq 'equal or not (v memq dvfsf_varlat a) then
      'failed
   else
      dvfsf_gelimset(a,v);

procedure dvfsf_gelimset(a,v);
   begin scalar pr;
      pr := dvfsf_mkpair(dvfsf_arg2l a,v);
      if numberp car pr then
	 return {'dvfsf_qesubcq . {dvfsf_mkcsol pr}};
      return 'failed
   end;

procedure dvfsf_qemkans(an);
   sort(dvfsf_qebacksub dvfsf_qemkans1 an,
      function(lambda(x,y); ordp(cadr x,cadr y)));

procedure dvfsf_qemkans1(an);
   % [an] is an answer. Returns a list of equations.
   for each y in an collect
      % cadr y = 'dvfsf_qesubcq, cadddr y = nil (atr)
      {'equal,car y,prepsq cadr caddr y};

procedure dvfsf_qebacksub(eql);
   % Quantifier elimination back substitution. [eql] is a list of
   % equations. Returns a list of equations.
   begin scalar subl,rhs,w;
      return for each e in eql collect <<
	 rhs := simp caddr e;
	 w := {'equal,cadr e,prepsq subsq(rhs,subl)};
	 subl := (cadr w . caddr w) . subl;
	 w
      >>
   end;

endmodule;  % [dvfsfqe]

end;  % of file
