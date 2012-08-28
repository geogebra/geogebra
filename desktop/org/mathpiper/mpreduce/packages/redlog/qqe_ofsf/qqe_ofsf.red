% ----------------------------------------------------------------------
% $Id: qqe_ofsf.red 1713 2012-06-22 07:42:38Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(qqe_ofsf_rcsid!* qqe_ofsf_copyright!*);
   qqe_ofsf_rcsid!* :=
      "$Id: qqe_ofsf.red 1713 2012-06-22 07:42:38Z thomas-sturm $";
   qqe_ofsf_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;

module qqe_ofsf;

create!-package('(qqe_ofsf),nil);

load!-package 'ofsf;

flag('(qqe_ofsf),'rl_package);

%% put('qqe_ofsf,'rl_prepat,'qqe_ofsf_prepat);
%% put('qqe_ofsf,'rl_resimpat,'qqe_ofsf_resimpat); %!!!
%% put('qqe_ofsf,'rl_lengthat,'qqe_ofsf_lengthat);

% Parameters
put('qqe_ofsf,'rl_params,'(
   (rl_prepat!* . qqe_ofsf_prepat)
   (rl_simpat!* . qqe_ofsf_simpat)
   (rl_subat!* . qqe_ofsf_subat) %% to do
   (rl_subalchk!* . qqe_ofsf_subalchk) %% to do
   (rl_eqnrhskernels!* . qqe_ofsf_eqnrhskernels) %% to do
   (rl_ordatp!* . qqe_ofsf_ordatp) %% to do
   (rl_qemkans!* . ofsf_qemkans) %% belongs to qe -should this be wrapped?
   (rl_simplat1!* . qqe_ofsf_simplat1)
   (rl_smupdknowl!* . qqe_ofsf_smwupdknowl) %!!!
   (rl_smrmknowl!* . qqe_ofsf_smwrmknowl) %!!!
   (rl_smcpknowl!* . qqe_ofsf_smwcpknowl)
   (rl_smmkatl!* . qqe_ofsf_smwmkatl)
   (rl_smsimpl!-impl!* . cl_smsimpl!-impl) % to check
   (rl_smsimpl!-equiv1!* . cl_smsimpl!-equiv1) % to check
%%   (rl_susipost!* . ofsf_susipost) % susi will not be needed (?)
%%   (rl_susitf!* . ofsf_susitf) %!!!
%%   (rl_susibin!* . ofsf_susibin)
   (rl_negateat!* . qqe_ofsf_negateat)
   (rl_varlat!* . qqe_ofsf_varlat)
   (rl_varsubstat!* . qqe_ofsf_varsubstat)
   (rl_translat!* . ofsf_translat) % belongs to qe - !?
   (rl_elimset!*. ofsf_elimset)
   (rl_trygauss!* . cl_trygauss)
   (rl_varsel!* . ofsf_varsel) % belongs to qe - !?
   (rl_betterp!* . cl_betterp)
   (rl_subsumption!* . ofsf_subsumption) % bnf function maybe ok
   (rl_bnfsimpl!* . cl_bnfsimpl)
   (rl_sacat!* . cl_sacat)   % was before ofsf_sacat : to be modified still
   (rl_sacatlp!* . ofsf_sacatlp)
   (rl_qstrycons!* . ofsf_qstrycons)
   (rl_qscsaat!* . cl_qscsaat)
   (rl_qssubat!* . ofsf_qssubat)
   (rl_qsconsens!* . cl_qsnconsens)
   (rl_qsimpltestccl!* . cl_qsimpltestccl)
   (rl_qssubsumep!* . cl_qssusubyit)
   (rl_qssubsumep!* . cl_qssusubytab)
   (rl_qstautp!* . cl_qstautp)
   (rl_qssusuat!* . ofsf_qssusuat)
   (rl_qssimpl!* . cl_qssimpl)
   (rl_qssiadd!* . ofsf_qssiadd)
   (rl_fctrat!* . qqe_ofsf_fctrat) % to do
   (rl_tordp!* . ordp)
   (rl_transform!* . ofsf_transform) % belongs to qe
   (rl_updatr!* . ofsf_updatr) % belongs to qe
   (rl_a2cdl!* . ofsf_a2cdl) %% not possible for example for qequal
   (rl_t2cdl!* . ofsf_t2cdl)
   (rl_getineq!* . ofsf_getineq)
   (rl_qefsolset!* . ofsf_qefsolset)
   (rl_bettergaussp!* . ofsf_bettergaussp)
   (rl_bestgaussp!* . ofsf_bestgaussp)
   (rl_esetunion!* . ofsf_esetunion)
   (rl_structat!* . qqe_ofsf_structat) %% to do
   (rl_ifstructat!* . ofsf_ifstructat) %% to do
   (rl_termmlat!* . ofsf_termmlat) %% to do
   (rl_multsurep!* . ofsf_multsurep) % !?
   (rl_specelim!* . ofsf_specelim)
   (rl_fbqe!* . ofsf_fbqe)));

% Services
put('qqe_ofsf,'rl_services,'(
   (rl_subfof!* . cl_subfof)
   (rl_identifyonoff!* . cl_identifyonoff)
   (rl_simpl!* . cl_simpl)
   (rl_thsimpl!* . ofsf_thsimpl) %% belongs to qe
   (rl_nnf!* . cl_nnf)
   (rl_nnfnot!* . cl_nnfnot)
   (rl_pnf!* . cl_pnf)
   (rl_cnf!* . cl_cnf)   % was before ofsf_cnf : but ofsf_sacat has to be
                         % modified first
   (rl_dnf!* . cl_dnf)   % same here
   (rl_all!* . cl_all)
   (rl_ex!* . cl_ex)
   (rl_atnum!* . cl_atnum)
   (rl_qnum!* . cl_qnum)
   (rl_tab!* . cl_tab)
   (rl_atab!* . cl_atab)
   (rl_itab!* . cl_itab)
%%   (rl_gsc!* . ofsf_gsc) %% groebner simplifier !?
%%   (rl_gsd!* . ofsf_gsd)
%%   (rl_gsn!* . ofsf_gsn)
   (rl_qe!* . qqe_ofsf_qe) %% to do
   (rl_qea!* . ofsf_qea) %% what is this function doing?
   (rl_gqe!* . cl_gqe)
   (rl_gqea!* . cl_gqea)
   (rl_qeipo!* . cl_qeipo)
   (rl_qews!* . cl_qews)
%%   (rl_opt!* . ofsf_opt) % standard form optimization !?
   (rl_ifacl!* . cl_ifacl)
   (rl_ifacml!* . cl_ifacml)
   (rl_matrix!* . cl_matrix)
   (rl_apnf!* . cl_apnf)
   (rl_atml!* . cl_atml)
   (rl_tnf!* . cl_tnf)
   (rl_atl!* . cl_atl)
   (rl_struct!* . cl_struct)
   (rl_ifstruct!* . cl_ifstruct)
   (rl_termml!* . cl_termml)
   (rl_terml!* . cl_terml)
   (rl_varl!* . cl_varl)
   (rl_fvarl!* . cl_fvarl)
   (rl_bvarl!* . cl_bvarl)
   (rl_gentheo!* . cl_gentheo)
   (rl_decdeg!* . ofsf_decdeg) %belongs to qe
   (rl_decdeg1!* . ofsf_decdeg1)
   (rl_surep!* . cl_surep)
   (rl_siaddatl!* . cl_siaddatl)
%%   (rl_cad!* . ofsf_cad) % later
%%   (rl_gcad!* . ofsf_gcad) % later
%%   (rl_cadswitches!* . ofsf_cadswitches)
   (rl_lqe!* . cl_lqe)
   (rl_xqe!* . ofsf_xopt!-qe)
   (rl_xqea!* . ofsf_xopt!-qea)
   (rl_lthsimpl!* . ofsf_lthsimpl) %% belongs to qe
   (rl_lthsimpl!* . ofsf_lthsimpl)
   (rl_quine!* . cl_quine)
%%   (rl_cadporder!* . ofsf_cadporder)
%%   (rl_gcadporder!* . ofsf_gcadporder)
%%   (rl_cadproj!* . ofsf_cadproj)
   (rl_hqe!* . ofsf_hqe)
   (rl_ghqe!* . ofsf_ghqe)));

%algebraic infix equal;
put('equal,'rl_prepfn,'ofsf_prepat);
put('equal,'rl_simpfn,'qqe_ofsf_chsimpat);
%put('equal,'number!-of!-args,2);
put('equal,'rtypefn,'quotelog);
remprop('equal,'psopfn); % temporary - to make arg check, also important
                         % in rlsimp1!!!

%algebraic infix neq;
put('neq,'rl_prepfn,'ofsf_prepat);
put('neq, 'rl_simpfn, 'qqe_ofsf_chsimpat);
%put('neq,'number!-of!-args,2);
put('neq,'rtypefn,'quotelog);
newtok '((!< !>) neq) where !*msg=nil;

%algebraic infix leq;
put('leq,'rl_prepfn,'ofsf_prepat);
put('leq,'rl_simpfn,'qqe_ofsf_chsimpat);
%put('leq,'number!-of!-args,2);
put('leq,'rtypefn,'quotelog);

%algebraic infix geq;
put('geq,'rl_prepfn,'ofsf_prepat);
put('geq,'rl_simpfn,'qqe_ofsf_chsimpat);
%put('geq,'number!-of!-args,2);
put('geq,'rtypefn,'quotelog);

%algebraic infix lessp;
put('lessp,'rl_prepfn,'ofsf_prepat);
put('lessp,'rl_simpfn,'qqe_ofsf_chsimpat);
%put('lessp,'number!-of!-args,2);
put('lessp,'rtypefn,'quotelog);

%algebraic infix greaterp;
put('greaterp,'rl_prepfn,'ofsf_prepat);
put('greaterp,'rl_simpfn,'qqe_ofsf_chsimpat);
%put('greaterp,'number!-of!-args,2);
put('greaterp,'rtypefn,'quotelog);

put('qequal,'infix,31);
put('qneq,'infix,32);

flag('(qqe_ofsf_chsimpat),'full);

%% TODO: other infixes from ofsf

procedure qqe_ofsf_prepat(f);
   if qqe_rqopp qqe_op f then qqe_prepat f
   else ofsf_prepat f;

procedure qqe_ofsf_resimpat(f);
   if qqe_rqopp qqe_op f then f
   else qqe_mk2(qqe_op f,
      numr resimp !*f2q qqe_arg2l f,numr resimp !*f2q qqe_arg2r f);

procedure qqe_ofsf_chsimpat(u);
   rl_smkn('and,for each x in ofsf_chsimpat1 u collect qqe_ofsf_simpat x);

procedure qqe_ofsf_chsimpat1(u);
   begin scalar leftl,rightl,lhs,rhs;
      lhs := cadr u;
      if pairp lhs and ofsf_opp car lhs then <<
	 leftl := ofsf_chsimpat1 lhs;
	 lhs := caddr lastcar leftl
      >>;
      rhs := caddr u;
      if pairp rhs and ofsf_opp car rhs then <<
	 rightl := ofsf_chsimpat1 rhs;
	 rhs := cadr car rightl
      >>;
      return nconc(leftl,{car u,lhs,rhs} . rightl)
   end;

procedure qqe_ofsf_simpat(u);
   begin
      qqe_ofsf_chsimpterm qqe_arg2l u;
      qqe_ofsf_chsimpterm qqe_arg2r u;
      qqe_arg!-check u;
      if qqe_op u memq '(qequal qneq) then return qqe_simpat u
      else return ofsf_simpat(u)
   end;

procedure qqe_ofsf_chsimpterm(term);
   begin scalar x;
      if atom term then return term
      else <<
         x := cdr term;
         while x do <<
            if pairp car x and qqe_op car x eq 'expt and
               pairp qqe_arg2l car x
               and qqe_op qqe_arg2l car x memq '(ltail rtail) then
                  car x := qqe_chsimpterm car x
            else if pairp car x then qqe_ofsf_chsimpterm car x;
            x := cdr x;
         >>;
      >>;
   end;

procedure qqe_ofsf_simplat1(f,sop);
   << if (x memq '(true false)) or (qqe_rqopp qqe_op f) then x
   else ofsf_simplat1(x, sop) >>
      where x=qqe_simplat1(f, sop);

procedure qqe_ofsf_simplqequal(f);
   qqe_mk2('qequal,ofsf_arg2l f,ofsf_arg2r f );

procedure qqe_ofsf_simplqneq(f);
   qqe_mk2('qneq,ofsf_arg2l f, ofsf_arg2r f);

%CS Ab hier, besser richtige Wrapper schreiben

procedure qqe_ofsf_canegrel(r,flg);
   % QQE Ordered field standard form conditionally algberaically negate
   % relation. [r] is a relation. Returns a relation $R$. If [flg] is
   % non-[nil], then $[R]=[r]$. If [flg] is [nil], then [R] is a
   % relation, such that $R(-t,0)$ is equivalent to $[r](t,0)$ for
   % every term $t$.
   if flg then r else qqe_anegrel r;

procedure qqe_ofsf_anegrel(r);
   % QQE Ordered field standard form algebraically negate relation. [r] is
   % a relation. Returns a relation $R$ such that $R(-t,0)$ is
   % equivalent to $[r](t,0)$ for a term $t$.
   cdr atsoc(r,'((qequal . qequal) (qneq . qneq))) or ofsf_anegrl r;

procedure qqe_ofsf_clnegrel(r,flg);
   % QQE Ordered field standard form conditionally logically negate
   % relation. [r] is a relation; [flg] is bool. Return a relation
   % $R$. If [flg] is non-[nil] [r] is returned. Othewise a relation
   % $R$ is returned such that for terms $t_1$, $t_2$ we have
   % $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$.
   if flg then r else qqe_ofsf_lnegrel r;

procedure qqe_ofsf_lnegrel(r);
   % QQE Ordered field standard form logically negate relation. [r] is a
   % relation. Returns a relation $R$ such that for terms $t_1$, $t_2$
   % we have $R(t_1,t_2)$ equivalent to $\lnot [r](t_1,t_2)$.
   if r eq 'qequal then 'qneq
   else if r eq 'qneq then 'qequal
   else ofsf_lnegrel r;

procedure qqe_ofsf_negateat(f);
   % QQE Ordered field standard form negate atomic formula. [f] is an
   % atomic formula. Returns an atomic formula equivalent to $\lnot
   % [f]$.
   ofsf_mkn(qqe_ofsf_lnegrel qqe_op f,ofsf_argn f);

procedure qqe_ofsf_varsubstat(atf,new,old);
   % QQE ordered field standard form substitute variable for variable
   % in atomic formula. [atf] is an atomic formula; [new] and [old]
   % are variables. Returns an atomic formula equivalent to [atf]
   % where [old] is substituted with [new].
   if qqe_rqopp qqe_op atf then qqe_varsubstat(atf, new,old)
   else ofsf_varsubstat(atf,new,old);

procedure qqe_ofsf_varlterm(term, list);
   % QQE ordered field standard form variable list term.
   % kernels {numr simp qqe_arg2l atform, numr simp qqe_arg2r atform};
   % should be done with dfs...i think here is no better wrapping
   % possible.
   begin scalar list;

      if (atom term) and idp term and not(term eq 'qepsilon)
      then list := lto_insertq(term, list)

      else if not atom term then
         for each x in cdr term do list := qqe_ofsf_varlterm(x, list);

      return list;
   end;


procedure qqe_ofsf_ordatp(a1,a2);
   % Ordered field standard form atomic formula predicate. [a1] and
   % [a2] are atomic formulas. Returns [t] iff [a1] is less than [a2].
   begin scalar lhs1,lhs2;
      lhs1 := ofsf_arg2l a1;
      lhs2 := ofsf_arg2l a2;
      if lhs1 neq lhs2 then return not ordp(lhs1,lhs2);
      return qqe_ofsf_ordrelp(ofsf_op a1,ofsf_op a2)
   end;

procedure qqe_ofsf_ordrelp(r1,r2);
   % Ordered field standard form relation order predicate.
   % [r1] and [r2] are ofsf-relations. Returns a [T] iff $[r1] < [r2]$.
   r2 memq cdr (r1 memq '(equal neq leq lessp geq greaterp qequal qneq));

procedure qqe_ofsf_varlat(atform);
   % later
   % begin scalar lhs, rhs;
   union(qqe_ofsf_varlterm(qqe_arg2l at,nil),
      qqe_ofsf_varlterm(qqe_arg2r at, nil))
         where at=qqe_ofsf_prepat atform;

%ENDCS Ab hier, besser richtige Wrapper schreiben

% originally module qqesism;
% qqe smart simplification. Submodule of [qqe].

procedure qqe_ofsf_smwupdknowl(op,atl,knowl,n);
   % Ordered field standard form smart simplification wrapper update
   % knowledge.
   if !*rlsusi then
      cl_susiupdknowl(op,atl,knowl,n)
   else
      qqe_ofsf_smupdknowl(op,atl,knowl,n);

procedure qqe_ofsf_smwrmknowl(knowl,v);
%%    if !*rlsusi then %% susi shouldn't be set
%%       qqe_susirmknowl(knowl,v)
      qqe_ofsf_smrmknowl(knowl,v);

procedure qqe_ofsf_smwcpknowl(knowl);
%%    if !*rlsusi then %% susi shouldn't be set
%%       cl_susicpknowl(knowl)
%%    else
      qqe_ofsf_smcpknowl(knowl);

procedure qqe_ofsf_smwmkatl(op,knowl,newknowl,n);
%%    if !*rlsusi then %% susi shouldn't be set
%%       cl_susimkatl(op,knowl,newknowl,n)
%%    else
      qqe_ofsf_smmkatl(op,knowl,newknowl,n);

% The black boxes are rl_smsimpl!-impl and rl_smsimpl!-equiv1 are set
% correctly for both the regular smart simplifier and for susi.

%DS
% <irl> ::= (<ir>,...)
% <ir> ::= <para> . <db>
% <db> ::= (<le>,...)
% <le> ::= <label> . <entry>
% <label> ::= <integer>
% <entry> ::= <of relation> . <standard quotient>

procedure qqe_ofsf_smrmknowl(knowl,v);
   % Qqe remove from knowledge. [knowl] is an
   % IRL; [v] is a variable. Returns an IRL. Destructively removes any
   % information about [v] from [knowl].
   if null knowl then
      nil
   else if v member kernels caar knowl then
      qqe_ofsf_smrmknowl(cdr knowl,v)
   else <<
      cdr knowl := qqe_ofsf_smrmknowl(cdr knowl,v);
      knowl
   >>;

procedure qqe_ofsf_smcpknowl(knowl);
   for each ir in knowl collect
      car ir . append(cdr ir,nil);

procedure qqe_ofsf_smupdknowl(op,atl,knowl,n);
   % Qqe update knowledge. [op] is one of
   % [and], [or]; [atl] is a list of (simplified) atomic formulas;
   % [knowl] is a conjunctive IRL; [n] is the current level. Returns
   % an IRL. Destructively updates [knowl] wrt. the [atl] information.
   begin scalar w,ir,a;
      while atl do <<
	 a := if op eq 'and then car atl else qqe_ofsf_negateat car atl;
	 atl := cdr atl;
	 ir := qqe_at2ir(a,n);
	 if w := assoc(car ir,knowl) then <<
	    cdr w := ofsf_sminsert(cadr ir,cdr w);
	    if cdr w eq 'false then <<
	       atl := nil;
	       knowl := 'false
	    >>  % else [ofsf_sminsert] has updated [cdr w] destructively.
	 >> else
	    knowl := ir . knowl
      >>;
      return knowl
   end;

procedure qqe_ofsf_smmkatl(op,oldknowl,newknowl,n);
   % Qqe make atomic formula list. [op] is one
   % of [and], [or]; [oldknowl] and [newknowl] are IRL's; [n] is an
   % integer. Returns a list of atomic formulas. Depends on switch
   % [rlsipw].
   if op eq 'and then
      qqe_ofsf_smmkatl!-and(oldknowl,newknowl,n)
   else  % [op eq 'or]
      qqe_ofsf_smmkatl!-or(oldknowl,newknowl,n);

procedure qqe_ofsf_smmkatl!-and(oldknowl,newknowl,n);
   begin scalar w;
      if not !*rlsipw and !*rlsipo then
	 return qqe_ofsf_irl2atl('and,newknowl,n);
      return for each ir in newknowl join <<
	 w := atsoc(car ir,oldknowl);
	 if null w then qqe_ofsf_ir2atl('and,ir,n) else
            qqe_ofsf_smmkatl!-and1(w,ir,n)
      >>;
   end;

procedure qqe_ofsf_smmkatl!-and1(oir,nir,n);
   begin scalar w,parasq;
      parasq := !*f2q car nir;
      return for each le in cdr nir join
      if car le = n then <<
	 if cadr le memq '(lessp greaterp) and
 	    (w := qqe_ofsf_smmkat!-and2(cdr oir,cdr le,parasq))
 	 then
	    {w}
	 else
	    {qqe_ofsf_entry2at('and,cdr le,parasq)}
      >>
   end;

procedure qqe_ofsf_smmkat!-and2(odb,ne,parasq);
   % Qqe smart simplify make atomic formula.
   % [odb] is a DB; [ne] is an entry with its relation being one of
   % [lessp], [greaterp]; [parasq] is a numerical SQ. Returns an
   % atomic formula.
   begin scalar w;
      w := qqe_ofsf_smdbgetrel(cdr ne,odb);
      if w eq 'neq then
	 (if !*rlsipw then <<
      	    if car ne eq 'lessp then
 	       return qqe_ofsf_entry2at('and,'leq . cdr ne,parasq);
	    % We know [car ne eq 'greaterp].
	    return qqe_ofsf_entry2at('and,'geq . cdr ne,parasq)
      	 >>)
      else if w memq '(leq geq) then
	 if not !*rlsipo then
 	    return qqe_ofsf_entry2at('and,'neq . cdr ne,parasq)
   end;

procedure qqe_ofsf_smmkatl!-or(oldknowl,newknowl,n);
   begin scalar w;
      return for each ir in newknowl join <<
	 w := atsoc(car ir,oldknowl);
	 if null w then qqe_ofsf_ir2atl('or,ir,n)
         else qqe_ofsf_smmkatl!-or1(w,ir,n)
      >>;
   end;

procedure qqe_ofsf_smmkatl!-or1(oir,nir,n);
   begin scalar w,parasq;
      parasq := !*f2q car nir;
      return for each le in cdr nir join
      if car le = n then <<
	 if cadr le memq '(lessp greaterp equal) and
 	    (w := ofsf_ofsf_smmkat!-or2(cdr oir,cdr le,parasq))
 	 then
	    {w}
	 else
	    {qqe_ofsf_entry2at('or,cdr le,parasq)}
      >>
   end;

procedure qqe_ofsf_smmkat!-or2(odb,ne,parasq);
   begin scalar w;
      w := qqe_ofsf_smdbgetrel(cdr ne,odb);
      if w eq 'neq then
	 (if not !*rlsipw then <<
      	    if car ne eq 'lessp then
 	       return qqe_ofsf_entry2at('or,'leq . cdr ne,parasq);
	    % We know [car ne eq 'greaterp]!
	    return qqe_ofsf_entry2at('or,'geq . cdr ne,parasq)
      	 >>)
      else if w memq '(leq geq) then <<
	 if car ne memq '(lessp greaterp) then
	    return qqe_ofsf_entry2at('or,'neq . cdr ne,parasq);
      	 % We know [car ne eq 'equal].
	 if !*rlsipo then
	    return qqe_ofsf_entry2at('or,ofsf_ofsf_anegrel w . cdr ne,parasq)
      >>
   end;

procedure qqe_ofsf_smdbgetrel(abssq,db);
   if abssq = cddar db then
      cadar db
   else if cdr db then
      qqe_ofsf_smdbgetrel(abssq,cdr db);

procedure qqe_ofsf_at2ir(atf,n);
   % Qqe atomic formula to IR. [atf] is an
   % atomic formula; [n] is an integer. Returns the IR representing
   % [atf] on level [n].
   begin scalar op,par,abs,c;
      op := ofsf_op atf;
      abs := par := ofsf_arg2l atf;
      while not domainp abs do abs := red abs;
      par := addf(par,negf abs);
      c := sfto_dcontentf(par);
      par := quotf(par,c);
      abs := quotsq(!*f2q abs,!*f2q c);
      return par . {n . (op . abs)}
   end;

procedure qqe_ofsf_irl2atl(op,irl,n);
   % Qqe IRL to atomic formula list. [irl] is
   % an IRL; [n] is an integer. Returns a list of atomic formulas
   % containing the level-[n] atforms encoded in IRL.
   for each ir in irl join qqe_ofsf_ir2atl(op,ir,n);

procedure qqe_ofsf_ir2atl(op,ir,n);
   (for each le in cdr ir join
      if car le = n then {qqe_ofsf_entry2at(op,cdr le,a)}) where a=!*f2q car ir;

procedure qqe_ofsf_entry2at(op,entry,parasq);
   if !*rlidentify then
      cl_identifyat qqe_ofsf_entry2at1(op,entry,parasq)
   else
      qqe_ofsf_entry2at1(op,entry,parasq);

procedure qqe_ofsf_entry2at1(op,entry,parasq);
   ofsf_0mk2(qqe_ofsf_clnegrel(car entry,op eq 'and),numr addsq(parasq,cdr entry));

% originally endmodule ofsfsism;

procedure qqe_ofsf_mk2(op,lhs,rhs);
   % later
   qqe_mk2(op,lhs,rhs);

procedure qqe_ofsf_qe(f,theo);
   % QQE ordered field standard form quantifier elimination.
   begin
      return qqe_qe(f);
      % ofsf_qe(f);  %<--- should be decided by some switch
   end;

procedure qqe_qe!-basic(f);
   ofsf_qe(f,nil);

endmodule;  % qqe_ofsf

end;  % of file
