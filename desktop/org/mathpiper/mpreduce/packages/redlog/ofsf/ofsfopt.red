% ----------------------------------------------------------------------
% $Id: ofsfopt.red 81 2009-02-06 18:22:31Z thomas-sturm $
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
   fluid '(ofsf_opt_rcsid!* ofsf_opt_copyright!*);
   ofsf_opt_rcsid!* :=
      "$Id: ofsfopt.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   ofsf_opt_copyright!* := "Copyright (c) 1995-1999 A. Dolzmann and T. Sturm"
>>;

module ofsfopt;
% Ordered field standard form optimization. Submodule of [ofsf].

smacro procedure ofsf_cvl(x);
   car x;

smacro procedure ofsf_al(x);
   cadr x;

smacro procedure ofsf_pl(x);
   caddr x;

smacro procedure ofsf_an(x);
   cdddr x;

smacro procedure ofsf_mkentry(cvl,al,pl,an);
   cvl . al . pl . an;

procedure ofsf_sendtask(hd,p,z);
   remote_call!*(hd,'ofsf_opt2,{
      ofsf_cvl p,ofsf_al p,ofsf_pl p,ofsf_an p,z,nil,nil},1) ;

procedure ofsf_optmaster(cvl,al,z,nproc);
   % Ordered field standard form optimization master. [cvl] is a list
   % $(x_1,...,x_n)$ of variables; [al] is a list of active constraints
   % already containg an artificial constraint that encodes the target
   % function $f(x_1,...,x_n)$; [z] is the identifier that serves as
   % artificial variable for optimization, [nproc] is a positive
   % integer, the number of slaves to use; it must be less than the
   % number of available processors. Returns a list containing a form
   % $(v . ((x_1 . p_1) ... (x_n . p_n)))$ where $v$ is the minimal
   % value of $f$ subject to the contraints, $(p_1,...,p_n)$ is one
   % point such that $f(p_1,...,p_n)=v$.
   begin scalar w,pbase,finl,fp,hdl,p,pending,sol,hd;
      pbase := ofsf_opt2(cvl,al,nil,nil,z,3*nproc) where !*rlqedfs=nil;
      remote_process nil;
      hdl := for i:=1:nproc collect pvm_mytid() + i;
      pvm_initsend(1);
%%      if !*rlverbose then ioto_prin2t {"handlel is ",hdl};
      fp := hdl;
      pending := nil;
      if !*rlverbose then ioto_tprin2t {"initial pbase size is ",length pbase};
      while pbase or pending do <<
         while pbase and fp do <<
            hd := car fp;
            fp := cdr fp;
            p := car pbase;
            pbase := cdr pbase;
	    if !*rlverbose then ioto_prin2t {"sending task to ",land(hd,4095)};
            pending := (ofsf_sendtask(hd,p,z) . hd) . pending
         >>;
         if pending then <<
	    if !*rlverbose then
 	       ioto_tprin2 {length pbase," problems left, waiting for ",
   		  for each x in pending collect land(cdr x,4095)," ... "};
            finl := remote_wait();
	    if !*rlverbose then ioto_prin2t "ready";
            for each fin in finl do <<
	       if (w := remote_receive(fin)) and
 		  (null sol or minusf numr subtrsq(car w,car sol))
 	       then
		  sol := w;
               fp := cdr assoc(fin,pending) . fp;
               pending := delasc(fin,pending)
            >>
         >>
      >>;
      return sol
   end;

%%procedure ofsf_optgen(cl,targ,sz,fn);
%%   begin scalar w,!*rlqedfs,!*rlsisqf;
%%      w := ofsf_opt1(cl,targ,nil);
%%      return ofsf_opt2(car w,cadr w,nil,nil,caddr w,sz,fn)
%%   end;

procedure ofsf_opt(cl,targ,parml,nproc);
   begin scalar svrlqedfs,w;
      if !*rlqeheu then <<
	 svrlqedfs := !*rlqedfs;
	 !*rlqedfs := T
      >>;
      w := ofsf_opt0(cl,targ,parml,nproc);
      if !*rlqeheu then
	 !*rlqedfs := svrlqedfs;
      return w
   end;

procedure ofsf_opt0(cl,targ,parml,nproc);
   % Linear optimization. [cl] is a list of parameter-free linear
   % atomic formulas with weak ordering relation; [targ] is an SQ with
   % constant denominator; [parm] is a dummy argument; [nproc] is the
   % number of processors available. Returns [infeasible],
   % [-infinity], or a list $(\mu,l_1,...,l_n)$ where the $l_i$ are
   % lists of equations. Minimizes [targ] subject to [cl].
   begin scalar w,nproc,!*rlsiatadv,!*rlsiso;
      w := ofsf_opt1(cl,targ,parml);
      if !*rlparallel then
	 return ofsf_optmkans ofsf_optmaster(car w,cadr w,caddr w,nproc - 1);
      return ofsf_optmkans ofsf_opt2(car w,cadr w,nil,nil,caddr w,nil)
   end;

procedure ofsf_opt1(cl,targ,parml);
   begin scalar z,qvl;
      z := intern gensym();
      cl := ofsf_0mk2('geq,
	 addf(multf(denr targ,numr simp z),negf numr targ)) . cl;
      qvl := setdiff(ofsf_varl cl,z . parml);
      return {qvl,cl,z}
   end;

procedure ofsf_varl(l);
   begin scalar w;
      for each x in l do
 	 w := union(w,ofsf_varlat x);
      return w
   end;

procedure ofsf_opt2(cvl,al,pl,an,z,sz);
   begin scalar w,co,ansl,junct,best,m,theo; integer c,vlv,nodes,dpth;
      if !*rlverbose and !*rlparallel then
 	 ioto_tprin2 "entering opt2 ... ";
      if !*rlverbose and !*rlqedfs and not !*rlparallel then <<
	 dpth := length cvl;
	 vlv :=  dpth / 4;
	 ioto_tprin2t {"+++ Depth is ",dpth,", watching level ",dpth - vlv}
      >>;
      co := ofsf_save(co,{ofsf_mkentry(cvl,al,pl,an)});
      while co do <<
	 w := ofsf_get(co); co := cdr w; w := car w;
	 cvl := ofsf_cvl w; al := ofsf_al w; pl := ofsf_pl w; an := ofsf_an w;
	 if !*rlverbose and not !*rlparallel then
 	    nodes := nodes + 1;
	 if !*rlverbose and !*rlqedfs and eqn(vlv,length cvl) and
 	    not !*rlparallel
 	 then
	    ioto_tprin2t {"+++ Crossing level ",dpth - vlv};
	 if !*rlverbose and null !*rlqedfs and not !*rlparallel then <<
	    if eqn(c,0) then <<
	       c := ofsf_colength(co) + 1;
	       ioto_tprin2t {"+++ ",length cvl," variables left for this block"}
	    >>;
	    ioto_prin2 {"[",c};
	    c := c - 1
	 >>;
	 if !*rlverbose and !*rlqedfs and not !*rlparallel then
	    ioto_prin2 {"[",dpth - length cvl};
	 junct := ofsf_qevar(cvl,al,pl,an,z,theo);
	 if junct eq 'break then
	    co := nil
	 else if junct and ofsf_cvl car junct then
	    co := ofsf_save(co,junct)
	 else <<
	    if !*rlverbose and not !*rlparallel and null junct then
 	       ioto_prin2 "#";
	    for each x in junct do <<
	       if m := ofsf_getvalue(ofsf_al x,ofsf_pl x) then <<
	       	  if ansl and (minusf (w := numr subtrsq(m,best)) or
		     null w and !*rlopt1s)
		  then
		     ansl := nil;
	       	  if null ansl or null w then <<
		     best := m;
	 	     theo := {ofsf_0mk2('leq,addf(
			multf(denr best,numr simp z),negf numr best))};
		     ansl := ofsf_an x . ansl;  % insert instead?
		     if !*rlverbose and not !*rlparallel then
		     	ioto_tprin2t {"min=",numr m or 0,"/",denr m}
	       	  >>
	       >>
	    >>;
	    if !*rlverbose and !*rlqedfs and not !*rlparallel then
 	       ioto_prin2 "."
	 >>;
	 if !*rlverbose and not !*rlparallel then ioto_prin2 "] ";
      	 if sz and ofsf_colength co >= sz then <<
	    if ansl then
 	       rederr "ofsf_opt2: found solutions during pbase generation";
	    junct := 'pbase;
	    ansl := cdr co;
	    co := nil
      	 >>
      >>;
      if junct eq 'pbase then
	 w := ansl
      else if junct eq 'break then
         w := junct
      else <<
	 w := nil;
         for each x in ansl do
	    w := lto_insert(ofsf_backsub(x,z,best),w);
	 w := {best,w}
      >>;
      if !*rlverbose and not !*rlparallel then
 	 ioto_tprin2t {"+++ ",nodes," nodes computed"};
      if !*rlverbose and !*rlparallel then
 	 ioto_prin2t "exiting opt2";
      return w
   end;

procedure ofsf_qevar(cvl,al,pl,an,z,theo);
   begin scalar w,v,an,eset;
      if (w := ofsf_optgauss(cvl,al,pl,an,theo)) then return cdr w;
      % elimination set method
      w := ofsf_opteset(cvl,al,pl,z);
      v := car w;
      eset := cdr w;
      if eset then <<  % [v] actually occurs in [f].
	 if v eq z then <<
	    if !*rlverbose and not !*rlparallel then ioto_prin2 "z";
	    return ofsf_zesetsubst(cvl,al,pl,an,eset,theo)
	 >>;
	 if !*rlverbose and not !*rlparallel then ioto_prin2 "e";
	 return ofsf_esetsubst(cvl,al,pl,an,v,eset,theo)
      >>;
      % [v] does not occur in [f]. Reinsert [f] with updated
      % variable list.
      if !*rlverbose then ioto_prin2 "*";
      if v memq ofsf_varl pl then <<
	 if !*rlverbose and not !*rlparallel then ioto_prin2 "!";
	 return nil  % Maybe wrong!
      >>;
      return {ofsf_mkentry(delq(v,cvl),al,pl,an)}
   end;

procedure ofsf_optgauss(cvl,al,pl,an,theo);
   begin scalar v,w,sc;
      sc := cvl;
      while sc do <<
	 v := car sc;
	 sc := cdr sc;
	 if (w := ofsf_optfindeqsol(al,v)) then sc := nil
      >>;
      if w then <<
	 if !*rlverbose and not !*rlparallel then ioto_prin2 "g";
	 return T . ofsf_esetsubst(cvl,al,pl,an,v,{w},theo)
      >>
   end;

procedure ofsf_optfindeqsol(al,v);
   begin scalar a,w;
      a := car al;
      if ofsf_op a eq 'equal and v memq ofsf_varlat a then <<
	 w := ofsf_optmksol(ofsf_arg2l a,v);
	 return a . quotsq(!*f2q car w,!*f2q cdr w)
      >>;
      if cdr al then
      	 return ofsf_optfindeqsol(cdr al,v)
   end;

%DS
% <eset> ::= (<entry>,...)
% <entry> ::= (<atomic formula> . <standard quotient>)

procedure ofsf_esetsubst(cvl,al,pl,an,v,eset,theo);
   begin scalar w,scpl,zonly,nal,npl,junct,x;
      cvl := delq(v,cvl);
      while eset do <<
	 x := car eset;
	 eset := cdr eset;
	 if cdr x memq '(pinf minf) then <<
	    nal := ofsf_simpl(for each atf in al collect
 	       ofsf_qesubiat(atf,v,cdr x),theo);
	    npl := ofsf_simpl(for each atf in pl collect
 	       ofsf_qesubiat(atf,v,cdr x),theo)
	 >> else <<
	    nal := ofsf_simpl(for each y in al collect
 	       ofsf_optsubstat(y,cdr x,v),theo);
	    npl := ofsf_simpl(for each y in pl collect
 	       ofsf_optsubstat(y,cdr x,v),theo);
	    al := delq(car x,al);
	    pl := car x . pl
	 >>;
	 if null nal and null npl then <<
	    junct := 'break;
	    eset := nil
	 >> else if null nal then <<
	    zonly := T;
	    scpl := pl;
	    while scpl do <<
	       w := ofsf_varlat car scpl;
	       scpl := cdr scpl;
	       if w neq '(z) then scpl := zonly := nil
	    >>;
	    if zonly then rederr "BUG IN OFSF_ESETSUBST";
	    if !*rlverbose and not !*rlparallel then ioto_prin2 "!"
	 >> else if nal neq 'false and npl neq 'false then
	    junct := ofsf_mkentry(cvl,nal,npl,(v . cdr x) . an) . junct
      >>;
      return junct
   end;

%DS
% <zeset> ::= (<zentry>,...)
% <zentry> ::= (<atomic formula> . <substitution>)
% <substitution> ::= (<variable> . <standard quotient>)

procedure ofsf_zesetsubst(cvl,al,pl,an,zeset,theo);
   begin scalar w,scpl,zonly,nal,npl,junct,x,v;
      while zeset do <<
	 x := car zeset;
	 zeset := cdr zeset;
	 v := cadr x;
	 nal := ofsf_simpl(for each y in al collect
	    ofsf_optsubstat(y,cddr x,v),theo);
	 npl := ofsf_simpl(for each y in pl collect
	    ofsf_optsubstat(y,cddr x,v),theo);
	 al := delq(car x,al);
	 pl := car x . pl;
	 if null nal and null npl then <<
	    junct := 'break;
	    zeset := nil
	 >> else if null nal then <<
	    zonly := T;
	    scpl := pl;
	    while scpl do <<
	       w := ofsf_varlat car scpl;
	       scpl := cdr scpl;
	       if w neq '(z) then scpl := zonly := nil
	    >>;
	    if zonly then rederr "BUG IN OFSF_ZESETSUBST";
	    if !*rlverbose and not !*rlparallel then ioto_prin2 "!"
	 >> else if nal neq 'false and npl neq 'false then
	    junct := ofsf_mkentry(delq(v,cvl),nal,npl,cdr x . an) . junct
      >>;
      return junct
   end;

procedure ofsf_optsubstat(atf,sq,v);
   % SQ denominator is a positive domain element.
%%   ofsf_qesubqat(atf,v,sq);
   begin scalar w;
      if null (w := ofsf_optsplitterm(ofsf_arg2l atf,v)) then return atf;
      return ofsf_0mk2(ofsf_op atf,
	 addf(multf(car w,numr sq),multf(cdr w,denr sq)))
   end;

procedure ofsf_optsplitterm(u,v);
   % Ordered fields standard form split term. [u] is a term $a[v]+b$;
   % [v] is a variable. Returns the pair $(a . b)$.
   begin scalar w;
      u := sfto_reorder(u,v);
      if (w := degr(u,v)) = 0 then return nil;
      if w > 1 then
 	 rederr {"ofsf_optsplitterm:",v,"has degree",w,"in",u};
      return reorder lc u . reorder red u
   end;

procedure ofsf_simpl(l,theo);
   begin scalar w,op,!*rlsiexpla;
      w := cl_simpl(rl_smkn('and,l),theo,-1);
      if w eq 'false then return 'false;
      if w eq 'true then return nil;
      op := rl_op w;
      if op eq 'and then
	 return rl_argn(w);
      if rl_cxp op then
	 rederr {"BUG IN OFSF_SIMPL",op};
      return {w}
   end;

switch boese,useold,usez;
on1('useold);
on1('usez);

procedure ofsf_opteset(cvl,al,pl,z);
   begin scalar w,v,sel,lbl,ubl,eset; integer ub,lb,best;
      if not (!*usez or !*useold) then rederr "select usez or useold as method";
      if !*usez then <<
      	 for each x in al do
	    if (w := ofsf_zboundchk(x,z)) then <<
	       best := best + 1;
	       % [w] is a consed pair.
      	       eset := (x . w) . eset
	    >>;
      	 if eset then sel := z
      >>;
      if !*useold or null sel then <<
      	 while cvl do <<
	    v := car cvl;
	    cvl := cdr cvl;
	    lb := ub := 0;
	    lbl := ubl := nil;
	    for each x in al do
	       if (w := ofsf_boundchk(x,v)) then
	       	  if car w eq 'lb then <<
	       	     lb := lb + 1;
		     lbl := (x . cdr w) . lbl
	       	  >> else if car w eq 'ub then <<
	       	     ub := ub + 1;
		     ubl := (x . cdr w) . ubl
	       	  >> else
		     rederr "BUG 2 IN ofsf_opteset";
	    if null lbl and ubl then
 	       lbl := '((nil . minf));
	    if null ubl and lbl then
 	       ubl := '((nil . pinf));
	    if ub <= lb then <<
	       lb := ub;
	       lbl := ubl
	    >>;
      	    if null sel or lb < best or null !*boese and lb = best then <<
	       best := lb;
	       eset := lbl;
	       sel := v
	    >>;
	    if null lbl and null ubl then
 	       cvl := nil
      	 >>
      >>;
      return sel . eset
   end;

procedure ofsf_boundchk(atf,v);
   begin scalar u,oldorder,op,sol;
      oldorder := setkorder {v};
      u := reorder ofsf_arg2l atf;
      setkorder oldorder;
      if domainp u or mvar u neq v then return nil;
      if ldeg u neq 1 then rederr {"ofsf_boundchk:",v,"not linear"};
      sol := quotsq(!*f2q negf reorder red u,!*f2q reorder lc u);
      op := ofsf_op atf;
      if op eq 'equal then return 'equal . sol;
      if ofsf_xor(op eq 'geq,minusf lc u) then return 'lb . sol;
      return 'ub . sol
   end;

procedure ofsf_zboundchk(atf,z);
   begin scalar u,v,oldorder,op;
      op := ofsf_op atf;
      u := ofsf_arg2l atf;
      if domainp u then
 	 return nil;
      oldorder := setkorder {z};
      u := reorder u;
      setkorder oldorder;
      if ldeg u neq 1 then
 	 rederr {"ofsf_zboundchk:",z,"not linear"};
      if mvar u neq z or domainp red u then
 	 return nil;
      if not (op eq 'equal or ofsf_xor(op eq 'geq,minusf lc u)) then
	 return nil;
      v := mvar red u;
      oldorder := setkorder {v};
      u := reorder u;
      setkorder oldorder;
      return v . quotsq(!*f2q negf reorder red u,!*f2q reorder lc u)
   end;

procedure ofsf_xor(a,b);
   (a or b) and not(a and b);

procedure ofsf_getvalue(al,pl);
   begin scalar atf,w;
      atf := ofsf_simpl(append(al,pl),nil);
      if atf eq 'false then return nil;
      if cdr atf then <<
 	 if cddr atf then rederr {"BUG 1 IN OFSF_GETVALUE",atf};
	 w := cadr atf;
      	 if ofsf_optlbp w then <<
	    w := car atf;
	    if ofsf_optlbp w then
	       rederr {"BUG 2 IN OFSF_GETVALUE",atf};
 	    atf := cdr atf
	 >>
      >>;
      atf := car atf;
      if not ofsf_optlbp atf then
	 rederr {"BUG 3 IN OFSF_GETVALUE",atf};
      w := ofsf_arg2l atf;
      return quotsq(!*f2q negf red w,!*f2q lc w)
   end;

procedure ofsf_optlbp(atf);
   ofsf_op atf memq '(equal geq);

procedure ofsf_backsub(an,z,min);
   sort(ofsf_backsub1(an,z,min),function(lambda(x,y); ordp(car x,car y)));

procedure ofsf_backsub1(an,z,min);
   ofsf_backsub2 for each x in an collect
      car x . ofsf_optsubsq(cdr x,{z . min});

procedure ofsf_backsub2(an);
   if an then car an . ofsf_backsub2 for each x in cdr an collect
      car x . ofsf_optsubsq(cdr x,{caar an . cdar an});

procedure ofsf_optsubsq(sq,al);
   if cdar al memq '(minf pinf) or sq memq '(minf pinf) then sq
   else subsq(sq,{caar al . prepsq cdar al});

procedure ofsf_optmkans(ans);
   begin scalar w;
      if ans = '(nil nil) then return 'infeasible;
      if ans eq 'break then return {simp '(minus infinity),nil};
      return {car ans,for each x in cadr ans collect
      	 for each y in x collect <<
	    w := atsoc(cdr y,'((minf . (minus infinity)) (pinf . infinity)));
	    w := if w then cdr w else mk!*sq cdr y;
	    aeval {'equal,car y,w}
	 >>}
   end;

procedure ofsf_optmksol(u,v);
   % Ordered fields standard form make solution. [u] is a term
   % $a[v]+b$ with $[a] \neq 0$; [v] is a variable. Returns the pair
   % $(-b . a)$.
   begin scalar w;
      w := setkorder {v};
      u := reorder u;
      setkorder w;
      if degr(u,v) neq 1 then rederr {"ofsf_mksol:",v,"not linear"};
      return negf reorder red u . reorder lc u
   end;

procedure ofsf_save(co,dol);
   % Ordered field standard form save into container. [co] is a
   % container; [dol] is a list of container elements. Returns a
   % container.
   if !*rlqedfs then ofsf_push(co,dol) else ofsf_enqueue(co,dol);

procedure ofsf_push(co,dol);
   % Ordered field standard form push into container. [co] is a
   % container; [dol] is a list of container elements. Returns a
   % container.
   <<
      for each x in dol do co := ofsf_coinsert(co,x);
      co
   >>;

procedure ofsf_coinsert(co,ce);
   % Ordered field standard form insert into container. [co] is a
   % container; [ce] is a container element. Returns a container.
   if ofsf_comember(ce,co) then co else ce . co;

procedure ofsf_enqueue(co,dol);
   % Ordered field standard form enqueue into container. [co] is a
   % container; [dol] is a list of container elements. Returns a
   % container.
   <<
      if null co and dol then <<
	 co := {nil,car dol};
	 car co := cdr co;
	 dol := cdr dol
      >>;
      for each x in dol do
	 if not ofsf_comember(x,cdr co) then
	    car co := (cdar co := {x});
      co
   >>;

procedure ofsf_get(co);
   % Ordered field standard form get from container. [co] is a
   % container. Returns a pair $(e . c)$ where $e$ is a container
   % element and $c$ is the container [co] without the entry $e$.
   if !*rlqedfs then ofsf_pop(co) else ofsf_dequeue(co);

procedure ofsf_pop(co);
   % Ordered field standard form pop from container. [co] is a
   % container. Returns a pair $(e . c)$ where $e$ is a container
   % element and $c$ is the container [co] without the entry $e$.
   co;

procedure ofsf_dequeue(co);
   % Ordered field standard form dequeue from container. [co] is a
   % container. Returns a pair $(e . c)$ where $e$ is a container
   % element and $c$ is the container [co] without the entry $e$.
   if co then cadr co . if cddr co then (car co . cddr co);

procedure ofsf_colength(co);
   % Ordered field standard form container length. [co] is a
   % container. Returns the number of elements in [co].
   if !*rlqedfs or null co then length co else length co - 1;

procedure ofsf_comember(ce,l);
   % Ordered field standard form container memeber. [ce] is a
   % container element; [l] is a list of container elements. Returns
   % non-[nil], if there is an container element $e$ in [l], such that
   % the formula and the variable list of $e$ are equal to the formula
   % and variable list of [ce]. This procedure does not use the access
   % functions!
   begin scalar a;
      if null l then
	 return nil;
      a := car l;
      if ofsf_al ce = ofsf_al a and ofsf_pl ce = ofsf_pl a and
	 ofsf_cvl ce = ofsf_cvl a
      then
	 return l;
      return ofsf_comember(ce,cdr l)
   end;

endmodule;  % [ofsfopt]

end;  % of file
