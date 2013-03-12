% ----------------------------------------------------------------------
% $Id: ofsfdecdeg.red 1839 2012-11-19 12:19:26Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 A. Dolzmann, T. Sturm, 2010-2011 T. Sturm
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
   fluid '(ofsf_decdeg_rcsid!* ofsf_decdeg_copyright!*);
   ofsf_decdeg_rcsid!* :=
      "$Id: ofsfdecdeg.red 1839 2012-11-19 12:19:26Z thomas-sturm $";
   ofsf_decdeg_copyright!* :=
      "(c) 1995-2009 A. Dolzmann T. Sturm, 2010-2011 T. Sturm"
>>;

module ofsfdecdeg;
% Ordered field standard form decdeg service for cl_qe. Submodule of
% [ofsf].

procedure ofsf_decdeg(f);
   % Ordered field standard form decrease degrees. [f] is a formula.
   % Returns a formula equivalent to [f], hopefully decreasing the
   % degrees of the bound variables.
   car ofsf_decdeg0 cl_rename!-vars f;

procedure ofsf_decdeg0(f);
   % Ordered field standard form decrease degrees subroutine. [f] is a
   % formula. Returns a pair $(\phi . l)$; $\phi$ is a formula, and $l$
   % is a list of pairs $(v . d)$ where $v$ is a bound variable from
   % [f], and $d$ is an integer. $\phi$ is equivalent to [f]. It is
   % obtained from [f] by replacing $v^d$ with $v$ for each $(v . d)$ in
   % $l$ and adding appropriate positivity conditions for even $d$.
   begin scalar op,w,gamma,newmat,dvl,nargl;
      op := rl_op f;
      if rl_boolp op then <<
	 nargl := for each subfo in rl_argn f collect <<
   	    w := ofsf_decdeg0 subfo;
	    dvl := nconc(dvl,cdr w);
	    car w
	 >>;
	 return rl_mkn(op,nargl) . dvl
      >>;
      if rl_quap op then <<
	 w := ofsf_decdeg0 rl_mat f;
	 dvl := cdr w;
	 w := ofsf_decdeg1(car w,{rl_var f});
	 dvl := nconc(dvl,cdr w);
	 newmat := if null cdr w or not evenp cdr car cdr w then
	    car w
	 else <<
	    gamma := ofsf_0mk2('geq,numr simp car car cdr w);
	    rl_mkn(if op eq 'ex then 'and else 'impl,{gamma,car w})
	 >>;
	 return rl_mkq(op,rl_var f,newmat) . dvl
      >>;
      % [f] is not complex.
      return f . nil
   end;

procedure ofsf_decdeg1(f,vl);
   % Ordered field standard form decrease degrees subroutine. [f] is a
   % formula; [vl] is a list of variables $v$ such that $v$ does not
   % occur boundly in [f], or ['fvarl]; ['fvarl] stands for the list of
   % all free variables in [f]. Returns a pair $(\phi . l)$; $\phi$ is a
   % formula, and $l$ is a list of pairs $(v . d)$ where $v$ in [vl] and
   % $d$ is an integer. $\phi$ is obtained from [f] by replacing $v^d$
   % with $v$ for each $(v . d)$ in $l$. Consequently, we have $\exists
   % [vl] [f]$ equivalent to $\exists [vl] (\phi \land \bigwedge_{(v .
   % d) \in [vl']}(v \geq 0))$, where [vl'] is the subset of pairs in
   % [vl] with even $d$.
   begin scalar posp, dvl; integer n;
      if vl eq 'fvarl then
	 vl := cl_fvarl1 f;
      for each v in vl do <<
	 posp := ofsf_posvarp(f,v);
	 n := ofsf_decdeg2(f,v,posp);
	 if n > 1 then <<
	    f := ofsf_decdeg3(f,v,n,posp);
	    dvl := (v . n) . dvl
	 >>
      >>;
      return f . dvl
   end;

procedure ofsf_decdeg2(f,v,posp);
   % Decrement degree subroutine. [f] is a formula; [v] is a variable.
   % Returns an INTEGER $n$. The degree of [v] in [f] can be decremented
   % using the replacement $[v]^n=v$, provided that [v] is quantified
   % from outside. Note that for even $n$ positive conditions have to be
   % added.
   begin scalar a,w,atl,!*gcd,oddp; integer dgcd;
      !*gcd := T;
      if !*rlbrkcxk then
	 dgcd := ofsf_cxkdgcd(f,v);
      atl := cl_atl1 f;
      while atl and not eqn(dgcd,1) do <<
	 a := pop atl;
	 w := ofsf_ignshift(a,v,posp);
	 if w eq 'odd and null oddp then
	    % We have found $R(c*v^k,0)$ with odd $k$ and $R$ an
	    % ordering relation for the first time.
	    oddp := 'odd
	 else if null w then <<
	    % We have not found $R(c*v^k,0)$.
	    a := sfto_reorder(ofsf_arg2l a,v);
	    while (not domainp a) and (mvar a eq v) and dgcd neq 1 do <<
	       dgcd := gcdf(dgcd,ldeg a);
	       a := red a
	    >>
      	 >>;
	 if dgcd > 0 and oddp eq 'odd then <<
	    % We have found $R(c*v^k,0)$ with odd $k$ and $R$ an
	    % ordering relation for the first time.
	    oddp := T;
	    while w := quotf(dgcd,2) do
	       dgcd := w
	 >>
      >>;
      if dgcd = 0 then
	 return 1;
      return dgcd
   end;

procedure ofsf_cxkdgcd(f,v);
   % Complex kernel degree gcd. [f] is a formula, [v] is a variable.
   % Returns an integer. The result is the gcd of the powers of
   % occurrences of [v] within the scope of all complex kernels in [f].
   ofsf_cxkdgcd1(cl_fvarl1 f,v,0) where !*rlbrkcxk=nil;

procedure ofsf_cxkdgcd1(kl,v,dgcd);
   % Complex kernel degree gcd subroutine. [kl] is a list of complex
   % kernels, [v] is a variable, [dgcd] is an integer. Returns an
   % integer. The result is the gcd of [dgcd] and the powers of
   % occurrences of [v] within the scope of the complex kernels in [kl].
   begin scalar u;
      for each k in kl do
	 if pairp k then
      	    for each arg in cdr k do <<
	       % I am assuming that there are no quotients in complex kernels.
	       u := numr simp arg;
	       dgcd := ofsf_cxkdgcd1(kernels u,v,dgcd);
	       u := sfto_reorder(u,v);
	       while not domainp u and mvar u eq v do <<
	       	  dgcd := gcdf(ldeg u,dgcd);
	       	  u := red u
	       >>
	    >>;
      return dgcd
   end;

procedure ofsf_transform(v, f, vl, an, theo, ans, bvl);
   % Ordered field standard form transform formula. [f] is a quantifier-free
   % formula; [v] is a variable. Returns a pair $(\phi . a)$. $\phi$ is a
   % formula such that $\exists [v]([f])$ is equivalent to $\exists [v](\phi)$.
   % $a$ is either [nil] or a pair $([v] . d)$. If $a$ is not [nil] then the
   % degree $d'$ of [v] in [f] is reduced to $d'/d$. If $a$ is nil then
   % $[f]=\phi$.
   begin scalar posp, dgcd, v_shift, w;
      posp := ofsf_posvarp(f,v);
      dgcd := ofsf_decdeg2(f,v,posp);
      if dgcd = 1 then
	 return nil;
      if !*rlverbose and !*rlqevb and (not !*rlqedfs or !*rlqevbold) then
 	 ioto_prin2 {"(",v,"^",dgcd,")"};
      f := ofsf_decdeg3(f,v,dgcd,posp);
      if evenp dgcd then
	 f := rl_mkn('and, {ofsf_0mk2('geq, numr simp v), f});
      if ans then <<
      	 repeat v_shift := intern gensym() until not flagp(v_shift, 'used!*);
	 flag({v_shift}, 'rl_qeansvar);
      	 f := cl_subfof({v . v_shift}, f);
	 vl := for each vv in vl collect if vv eq v then v_shift else vv;
      	 w := simp {'expt, v_shift, {'quotient,1,dgcd}};
	 an := cl_updans(v,'ofsf_qesubcq,{'true,w},an,ans)
      >>;
      return {f, vl, an, theo, ans, bvl}
   end;

procedure ofsf_ignshift(at,v,posp);
   % Orderd field standard form ignore shift. [at] is an atomic
   % formula; [v] is a variable. Returns [nil], ['ignore], or ['odd].
   begin scalar w;
      w := sfto_reorder(ofsf_arg2l at,v);
      if not domainp w and null red w and mvar w eq v then
	 if !*rlpos or posp or ofsf_op at memq '(equal neq) or evenp ldeg w then
	    return 'ignore
	 else
	    return 'odd
   end;

procedure ofsf_decdeg3(f,v,n,posp);
   % Ordered field standard form decrement degree. [f] is a formula;
   % [v] is a variable; [n] is an integer. Returns a formula.
   cl_apply2ats1(f,'ofsf_decdegat,{v,n,posp});

procedure ofsf_decdegat(atf,v,n,posp);
   % Ordered field standard form decrement degree atomic formula. [f]
   % is an atomic formula; [v] is a variable; [n] is an integer. Returns
   % an atomic formula.
   if ofsf_ignshift(atf,v,posp) then
      atf
   else
      ofsf_0mk2(ofsf_op atf,sfto_decdegf(ofsf_arg2l atf,v,n));

endmodule;

end;  % of file
