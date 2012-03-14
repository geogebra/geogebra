% ----------------------------------------------------------------------
% $Id: rlsl.red 773 2010-10-05 21:33:20Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2010 Thomas Sturm
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
   fluid '(rl_sl_rcsid!* rl_sl_copyright!*);
   rl_sl_rcsid!* := "$Id: rlsl.red 773 2010-10-05 21:33:20Z thomas-sturm $";
   rl_sl_copyright!* := "Copyright (c) 2010 T. Sturm"
>>;

module rlsl;

global '(sl_maxline!*);
sl_maxline!* := 100;

switch slat;
switch sllast;

off1 'slat;
off1 'sllast;

put('slprog,'tag,'!*slp);
put('slprog,'evfn,'sl_reval);
put('slprog,'lengthfn,'sl_length);

put('slp,'rtypefn,'quoteslprog);
put('slp,'sl_simpfn,'sl_simpslp);
put('slp,'rl_simpfn,'rl_simpslp);
put('slp,'prifn,'sl_pri);

algebraic infix setsl;
flag('(setsl),'spaced);
newtok '((!: !: !=) setsl) where !*msg=nil;

put('!*slp,'rtypefn,'quoteslprog);
put('!*slp,'sl_simpfn,'sl_simp!*slp);
put('!*slp,'rl_simpfn,'rl_simp!*slp);
put('!*slp,'prifn,'sl_print!*slp);

put('not,'sl_simpfn,'sl_simpbop);
put('and,'sl_simpfn,'sl_simpbop);
put('or,'sl_simpfn,'sl_simpbop);
put('impl,'sl_simpfn,'sl_simpbop);
put('repl,'sl_simpfn,'sl_simpbop);
put('equiv,'sl_simpfn,'sl_simpbop);

put('ex,'sl_simpfn,'sl_simpq);
put('all,'sl_simpfn,'sl_simpq);

put('equal,'sl_simpfn,'sl_simpat);

put('!*fof,'sl_simpfn,'sl_simp!*fof);

flag('(sl_simpbop sl_simpq sl_simpat),'full);

procedure quoteslprog(u);
   'slprog;

procedure sll_mk(rhs);
   {'sll,slv_new(),rhs};

procedure sll_mkx(lhs,rhs);
   {'sll,lhs,rhs};

procedure sll_lhs(sl);
   cadr sl;

procedure sll_rhs(sl);
   caddr sl;

procedure sl_length(u);
   % [u] is an SLPROG in (pseudo) Lisp prefix. Returns a number. The
   % result is the number of SL in [u].
   length sl_simp u;

procedure sl_pri(u);
   begin scalar lst;
      if not !*nat then
	 return 'failed;
      prin2!* "begin";
      terpri!* t;
      u := reverse sl_simp u;
      if u then <<
      	 lst := pop u;
      	 u := reversip u;
      	 for each line in u do <<
	    prin2!* "   ";
	    maprin sl_prepsll line;
	    terpri!* t
      	 >>;
      	 prin2!* "   ";
	 maprin if !*sllast or rl_atnum sll_rhs lst < sl_maxline!* then
	    sl_prepsll lst
 	 else
 	    {'setsl,rl_prepfof1 sll_lhs lst,lto_sconcat {
	       "[",lto_at2str rl_atnum sll_rhs lst," atomic formulas]"}};
	 terpri!* t
      >>;
      prin2!* "end"
   end;

procedure sl_print!*slp(u);
   maprin reval u;

procedure sl_reval(u,v);
   % SLP [reval]. [u] is an SLPROG in some mixed pseudo Lisp prefix form
   % where [car u] is either ['!*fof] or a first-order operator; [v] is
   % bool. Returns Lisp prefix of [u] in case [v] is non-[nil], and
   % pseudo Lisp prefix of [u] else.
   if v then sl_prep sl_simp u else sl_mk!*slp sl_simp u;

procedure sl_prep(slp);
   % [slp] is an SLPROG. Returns Lisp prefix of [slp].
   'slp . for each sl in slp collect
      sl_prepsll sl;

procedure sl_prepsll(sl);
   {'setsl,rl_prepfof1 sll_lhs sl,rl_prepfof1 sll_rhs sl};

procedure sl_mk!*slp(u);
   % [slp] make psueo Lisp prefix. [slp] is an SLPROG. Returns pseudo Lisp
   % prefix of [slp].
   '!*slp . rl_cid!* . u . if !*resubs then !*sqvar!* else {nil};

procedure sl_simp(u);
   % [u] is (pseudo) Lisp prefix of a formula. Returns the SLPROG
   % encoded by [u].
   begin scalar w;
      if null rl_cid!* then
 	 rederr {"select a context"};
      if atom u then
 	 return sl_simpatom u;
      argnochk u;
      if (w := get(car u,'sl_simpfn)) then
 	 return if flagp(w,'full) then apply(w,{u}) else apply(w,{cdr u});
      if (w := get(car u,get(car rl_cid!*,'simpfnname))) then
	 return if flagp(w,'full) then apply(w,{u}) else apply(w,{cdr u});
      if (w := get(car u,'psopfn)) then
 	 return sl_simp apply1(w,cdr u);
      if flagp(car u,'opfn) then
	 return sl_simp apply(car u,for each x in cdr u collect reval x);
      if (w := get(car u,'prepfn2)) then
 	 return sl_simp apply(w,{u});
      typerr(u,"slprog")
   end;

procedure sl_simpatom(u);
   % SLP simp atom. [u] is an atom. Returns the SLPROG encoded by [u].
   begin scalar w;
      if null u then typerr("nil","slprog");
      if numberp u then typerr({"number",u},"slprog");
      if stringp u then typerr({"string",u},"slprog");
      if (w := rl_gettype(u)) then <<
	 if w memq '(slprog logical) then
	    return sl_simp cadr get(u,'avalue);
	 typerr({w,u},"slprog")
      >>;
      % [u] algebraically unbound.
      if boundp u then return sl_simp eval u;
      typerr({"unbound id",u},"slprog")
   end;

procedure sl_simpslp(u);
   sl_reduce for each line in u collect
      % TODO: Check if variable is already bound, etc.!
      sll_mkx(slv_simp cadr line,rl_simp caddr line);

procedure rl_simpslp(u);
   sl_unstraightify sl_simp u;

procedure sl_simpbop(u);
   begin scalar w,tslp,lst; integer c;
      c := 1;
      for each slp in cdr u do <<
	 w := reversip sl_renum1(sl_simp slp,c);
	 c := slv_n sll_lhs car w;
	 lst := sll_rhs car w . lst;
	 w := cdr w;
	 tslp := nconc(w,tslp)
      >>;
      tslp := sll_mkx(slv_mk c,rl_mkn(car u,reversip lst)) . tslp;
      return sl_reduce reversip tslp
   end;

procedure sl_simpq(u);
   begin scalar vl,tslp,lst,rhs;
      vl := reval cadr u;
      if eqcar(vl,'list) then
	 vl := cdr vl
      else
	 vl := {vl};
      tslp := reversip sl_simp caddr u;
      lst := car tslp;
      tslp := cdr tslp;
      rhs := sll_rhs lst;
      for each x in reverse vl do <<
      	 rl_qvarchk x;
	 rhs := rl_mkq(car u,x,rhs)
      >>;
      flag(vl,'used!*);
      lst := sll_mkx(sll_lhs lst,rhs);
      tslp := lst . tslp;
      return reversip tslp
   end;

procedure sl_simpat(u);
   sl_reduce {sll_mk rl_simp u};

procedure sl_renum(slp);
   sl_renum1(slp,1);

procedure sl_renum1(slp,slv0);
   begin scalar al;
      slp := sl_apply2slv(slp,function slv_neg,nil);
      slv0 := slv0 - 1;
      al := for each line in slp collect
	 sll_lhs line . slv_mk(slv0 := slv0 + 1);
      return sl_apply2slv(slp,function slv_sub,{al})
   end;

procedure sl_apply2slv(slp,cl,clpl);
   for each l in slp collect
      sll_mkx(apply(cl,sll_lhs l . clpl),rl_apply2slv(sll_rhs l,cl,clpl));

procedure rl_apply2slv(f,cl,clpl);
   begin scalar op,w;
      op := rl_op f;
      if op eq 'slv then
	 return apply(cl,f . clpl);
      if rl_tvalp op then
 	 return f;
      if rl_quap op then
    	 return rl_mkq(op,rl_var f,rl_apply2slv(rl_mat f,cl,clpl));
      if rl_bquap op then
    	 return rl_mkbq(op,
	    rl_var f,rl_apply2slv(rl_b f,cl,clpl),
	    rl_apply2slv(rl_mat f,cl,clpl));
      if rl_boolp op then
    	 return rl_mkn(op,for each subf in rl_argn f collect
	    rl_apply2slv(subf,cl,clpl));
      % [f] is an atomic formula.
      return f
   end;

procedure sl_reduce(slp);
   sl_renum sl_reduce3 sl_reduce2 sl_reduce1 slp;

procedure sl_reduce1(slp);
   % Step 1: Delete slv(n) ::= slv(m)
   begin scalar tslp,sal,l,w,v;
      for each l in slp do
	 if eqcar(sll_rhs l,'slv) then <<
	    w := assoc(sll_rhs l,sal);
	    v := if w then cdr w else sll_rhs l;
	    sal := (sll_lhs l . v) . sal
	 >> else
	    tslp := l . tslp;
      return sl_apply2slv(reversip tslp,function slv_sub,{sal})
   end;

procedure sl_reduce2(slp);
   % Step 2: Delete duplicate rhs
   begin scalar tslp,sal,lal,l,w,v;
      tslp := for each l in slp join
	 if (w := assoc(sll_rhs l,lal)) then <<
	    sal := (sll_lhs l . cdr w) . sal;
	    nil
	 >> else <<
	    lal := (sll_rhs l . sll_lhs l) . lal;
	    {l}
	 >>;
      return sl_apply2slv(tslp,function slv_sub,{sal})
   end;

procedure sl_reduce3(slp);
   % Step 3: Delete slv that are not used at least twice
   begin scalar tslp,sal,lal,l,w,v;
      while slp do <<
	 l := car slp;
	 slp := cdr slp;
	 if not slp or sl_twicep(slp,sll_lhs l) then
	    tslp := l . tslp
	 else
	    sal := (sll_lhs l . sll_rhs l) . sal
      >>;
      return sl_apply2slv(reversip tslp,function slv_sub,{sal})
   end;

procedure sl_twicep(slp,slv);
   begin scalar br,l; integer c;
      while not br and slp do <<
	 l := car slp;
	 slp := cdr slp;
	 c := sl_twicep1(sll_rhs l,slv,c);
	 if c geq 2 then
	    br := t
      >>;
      return br
   end;

procedure sl_twicep1(f,slv,c);
   begin scalar op,w,br,a,argl;
      op := rl_op f;
      if op eq 'slv then
	 if f = slv then <<
	    c := c+1;
	    return c
	 >>;
      if rl_quap op then
    	 return sl_twicep1(rl_mat f,slv,c);
      if rl_bquap op then <<
	 c := sl_twicep1(rl_b f,slv,c);
	 if c geq 2 then
	    return c;
	 c := sl_twicep1(rl_mat f,slv,c);
	 return c
      >>;
      if rl_boolp op then <<
	 argl := rl_argn f;
      	 while not br and argl do <<
	    a := car argl;
	    argl := cdr argl;
	    c := sl_twicep1(a,slv,c);
	    if c geq 2 then
	       br := t
	 >>
      >>;
      % [f] is an atomic formula or truth value.
      return c
   end;

procedure sl_simp!*slp(u);
   % Simp [!*slp] operator. [u] is of the form [(tag,s,!*sqvar!*)] where
   % [tag] is a context tag [s] is an SLPROG.
   begin scalar tag,s,w;
      if caddr u then  % [!*sqvar!*=T]
 	 return cadr u;
      tag := car u;
      s := cadr u;
      if tag neq rl_cid!* then <<
	 w := rl_set tag where !*msg=nil;
	 s := sl_prep s;
	 rl_set w where !*msg=nil;
	 return sl_simp s
      >>;
      return sl_resimp s
   end;

procedure rl_simp!*slp(u);
   sl_unstraightify sl_simp!*slp u;

procedure sl_resimp(slp);
   % Resimp. [slp] is an SLPROG.
   for each sl in slp collect
      sll_mkx(sll_lhs sl,rl_resimp sll_rhs sl);

procedure sl_simp!*fof(u);
   sl_reduce {sll_mk rl_simp!*fof u};

procedure sl_unstraightify(slp);
   sll_rhs car sl_unstraightify1(slp,function(lambda(x); t));

procedure sl_unstraightify1(slp,cdp);
   begin scalar l,tslp,sal,w;
      for each rslp on slp do <<
	 l := car rslp;
	 if cdr rslp and apply(cdp,{sll_rhs l}) then <<
	    w := rl_apply2slv(sll_rhs l,function slv_sub,{sal});
	    sal := (sll_lhs l . w) . sal
      	 >> else
	    tslp := l . tslp
      >>;
      slp := sl_apply2slv(reversip tslp,function slv_sub,{sal});
      return sl_reduce slp
   end;

procedure slpp(slp);
   % TODO
   not slp or
      pairp slp and
      pairp car slp and eqcar(slp,'sll) and
      cdar slp and idp cadar slp and
      cddar slp and formulap caddar slp and
      not cdddr slp;

procedure formulap(f);
   t;

endmodule;  % rlsl

end;  % of file
