% ----------------------------------------------------------------------
% $Id: mriqe.red 1814 2012-11-02 13:17:09Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008-2009 Thomas Sturm
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
   fluid '(mri_qe_rcsid!* mri_qe_copyright!*);
   mri_qe_rcsid!* :=
      "$Id: mriqe.red 1814 2012-11-02 13:17:09Z thomas-sturm $";
   mri_qe_copyright!* := "Copyright (c) 2008-2009 T. Sturm"
>>;

module mriqe;

procedure mri_qe(f,theo);
   begin scalar w,q,ql,varl,varll;
      if !*rlverbose then
	 ioto_tprin2t {"++++++ MRI entering mri_qe"};
%      if !*rlqepnf then f := rl_pnf f;
      if theo then lprim {"mri_mriqe: ignoring theory"};
      f := cl_simpl(f,nil,-1);
      if not rl_quap rl_op f then
	 return f;
      w := mri_splitqf f; ql := car w; varll := cadr w; f := caddr w;
      while ql do <<
      	 q := car ql;
      	 ql := cdr ql;
      	 varl := car varll;
      	 varll := cdr varll;
      	 if !*rlverbose then
      	    ioto_tprin2 {"+++++ MRI current block is ", q . reverse varl};
 	 f := mri_qeblock(f,q,varl);
      >>;
      return f
   end;

procedure mri_splitqf(f);
   % Split [f] into a quantifier list, a list of variable lists, and
   % its matrix.
   begin scalar q,op,ql,varl,varll;
      q := op := rl_op f;
      repeat <<
   	 if op neq q then <<
      	    ql := q . ql;
      	    varll := varl . varll;
      	    q := op;
      	    varl := nil
   	 >>;
   	 varl := rl_var f . varl;
   	 f := rl_mat f
      >> until not rl_quap(op := rl_op f);
      ql := q . ql;
      varll := varl . varll;
      return {ql,varll,f}
   end;

procedure mri_qeblock(f,q,varl);
   if q eq 'ex then
      mri_qeblock1(f,varl)
   else
      cl_nnfnot mri_qeblock1(cl_nnfnot f,varl);

procedure mri_qeblock1(f,varl);
   begin scalar v,w;
      while varl do <<
	 v := mri_varsel varl;
	 varl := delq(v,varl);
	 if mri_realvarp v then <<
	    mri_vbin("+++ MRI expanding bounded quantifiers for real qe",f);
	    w := mri_expand f;
	    mri_vbout(f,w);
	    w := mri_qereal(w,v);
	    f := car w;
	    varl := cdr w . varl
	 >> else
 	    f := mri_qeint(f,v)
      >>;
      return f
   end;

procedure mri_varsel(varl);
   begin scalar w;
      if !*rlverbose and !*rlmrivb then
      	 ioto_tprin2 {"++++ MRI picking next variable from ",reverse varl,
	    " ... "};
      w := mri_varselreal varl;
      ioto_prin2 if w then {w," (real)"} else {car varl," (integer)"};
      return w or car varl
   end;

procedure mri_varselreal(varl);
   if varl then
      if mri_realvarp car varl then
 	 car varl
      else
 	 mri_varselreal cdr varl;

procedure mri_qereal(f,v);
   begin scalar w,vint,vtrunc;
      w := mri_truncate(f,v);
      f := car w;
      vtrunc := cadr w;
      vint := caddr w;
      if !*rlverbose then
	 ioto_tprin2 {"++++ MRI introduced new quantified variables ",vtrunc,
	    " and ",vint," for ",v};
      w := rl_mkq('ex,vtrunc,mri_case2(mri_lemma33(f,vtrunc),vtrunc));
      if !*rlverbose then
	 ioto_tprin2 {"+++ MRI entering real qe for ",vtrunc," ... "};
      w := mri_qereal1 w;
      if !*rlverbose then <<
	 ioto_prin2 "finished";
	 mathprint rl_mk!*fof w
      >>;
      return w . vint
   end;

procedure mri_qereal1(f);
   begin scalar w,!*rlverbose,!*msg;
      !*rlverbose := !*rlmrivb2;
      rl_set '(mri_ofsf);
      w := rl_qe(f,nil);
      rl_set '(mri);
      return w
   end;

procedure mri_truncate(f,u);
   begin scalar utrunc,uint,w;
      mri_vbin({"+++ MRI truncating ",u},f);
      utrunc := intern lto_idconcat2(u,'!_trunc);
      mri_putreal utrunc;
      uint := intern lto_idconcat2(u,'!_int);
      w := mri_smartand {mri_0mk2('geq,!*k2f utrunc,'real),
	 mri_0mk2('lessp,addf(!*k2f utrunc,negf 1),'real),
	 rl_subfof({u . {'plus,uint,utrunc}},f)};
      mri_vbout(f,w);
      return {w,utrunc,uint}
   end;

procedure mri_smartand(l);
   rl_smkn('and,for each f in l join
      if rl_op f eq 'and then append(rl_argn f,nil) else {f});

procedure mri_vbin(msg,f);
   if !*rlverbose then
      if !*rlmrivb then <<
	 ioto_tprin2 msg;
	 if !*rlmrivbio then <<
	    ioto_tprin2 "+++ in:";
      	    mathprint rl_mk!*fof f
	 >>
      >>;
   
procedure mri_vbout(f,w);
   if !*rlverbose then
      if !*rlmrivb then <<
      	 if !*rlmrivbio then <<
	    ioto_prin2 "+++ out:";
	    mathprint rl_mk!*fof w
      	 >> else if w neq f then
	    mathprint rl_mk!*fof w
      	 else
	    ioto_prin2 " - no changes"
      >>;

procedure mri_lemma33(f,u);
   begin scalar w;
      mri_vbin({"+++ MRI applying Lemma 3.3 (remove ",u," from floors)"},f);
      w := cl_apply2ats1(f,function mri_lemma33at,{u});
      mri_vbout(f,w);
      return w
   end;

procedure mri_lemma33at(at,u);
   begin scalar lhs,cd,phi,s,n;
      lhs := mri_arg2l at;
      if not mri_floorkernelp lhs then
	 return at;
      cd := mri_lemma33f(lhs,u);
      return rl_smkn('or,for each c in cd collect <<
	 phi := car c;
	 s := cadr c;
	 n := caddr c;
	 rl_mkn('and,{phi,mri_0mk2(mri_op at,addf(multf(n,!*k2f u),s),'real)})
      >>)
   end;

procedure mri_lemma33f(f,u);
   begin scalar cdlc,cdkern,cdred;
      if domainp f then
	 return {{'true,f,nil}};
      cdlc := mri_lemma33f(lc f,u);
      cdkern := mri_lemma33k(mvar f,u);
      cdred := mri_lemma33f(red f,u);
      return mri_add33(mri_mult33(cdlc,mri_expt33(cdkern,ldeg f)),cdred)
   end;

procedure mri_lemma33k(ker,u);
   begin scalar cd,phi,s,n,phij,fs,fsj,fsj1,ss;
      if ker eq u then
	 return {{'true,nil,1}};
      if idp ker then
	 return {{'true,!*k2f ker,nil}};
      if not eqcar(ker,'floor) then
	 rederr {"invalid kernel",ker};
      cd := mri_lemma33f(numr simp cadr ker,u);
      if cdr cd then rederr "Check!";
      cd := car cd;  % Check!
      phi := car cd;
      s := cadr cd;
      n := caddr cd;
      if not domainp n then  % mri_mult33 should have complained before
	 rederr "mri_lemma33k: real variable with parametric coefficient";
      return for j:=0:n collect <<
	 fs := !*k2f !*a2k {'floor,prepf s};
	 fsj := addf(fs,j);
	 fsj1 := addf(fsj,1);
	 ss := addf(multf(n,!*k2f u),fs);
	 phij := rl_mkn('and,{phi,
	    mri_0mk2('leq,addf(fsj,negf ss),'real),
	    mri_0mk2('lessp,addf(ss,negf fsj1),'real)});
	 {phij,fsj,nil}
      >>
   end;

procedure mri_add33(cd1,cd2);
   begin scalar phi1,phi2,s1,s2,n1,n2;
      return for each t1 in cd1 join <<
	 phi1 := car t1;
	 s1 := cadr t1;
	 n1 := caddr t1;
      	 for each t2 in cd2 collect <<
	    phi2 := car t2;
	    s2 := cadr t2;
	    n2 := caddr t2;
	    {rl_mkn('and,{phi1,phi2}),addf(s1,s2),addf(n1,n2)}
	 >>
      >>
   end;

procedure mri_mult33(cd1,cd2);
   begin scalar phi1,phi2,s1,s2,n1,n2;
      return for each t1 in cd1 join <<
	 phi1 := car t1;
	 s1 := cadr t1;
	 n1 := caddr t1;
      	 for each t2 in cd2 collect <<
	    phi2 := car t2;
	    s2 := cadr t2;
	    n2 := caddr t2;
	    if n1 and n2 then
	       rederr "mri_mult33: real variable with degree > 1";
	    if (n1 or n2) and (not domainp s1 or not domainp s2) then
	       rederr "mri_mult33: real variable with parametric coefficient";
	    {rl_mkn('and,{phi1,phi2}),
	       multf(s1,s2),addf(multf(n1,s2),multf(n2,s1))}
      	 >>
      >>
   end;

procedure mri_expt33(cd,k);
   begin scalar phi,s,n;
      return for each c in cd collect <<
      	 phi := car c;
      	 s := cadr c;
      	 n := caddr c;
      	 if n and k>1 then
	    rederr "mri_expt33: real variable with degree > 1";
	 {phi,exptf(s,k),n}
      >>
   end;
      
procedure mri_qeint(f,v);
   begin scalar w;
      w := rl_mkq('ex,v,mri_case1(mri_lemma32(f,v),v));
      if !*rlverbose then
	 ioto_tprin2 {"+++ MRI entering integer qe for ",v," ... "};
      w := mri_qeint1(w);
      if !*rlverbose then <<
	 ioto_prin2 "finished";
	 mathprint rl_mk!*fof w
      >>;
      return w
   end;

procedure mri_qeint1(f);
   begin scalar w,!*msg,!*rlverbose;
      !*rlverbose := !*rlmrivb2;
      rl_set '(mri_pasf);
      w := pasf_wqe(f,nil);
      rl_set '(mri);
      return w
   end;

procedure mri_lemma32(f,xi);
   begin scalar w;
      mri_vbin({"+++ MRI applying Lemma 3.2 (remove ",xi," from floors)"},f);
      w := cl_apply2ats1(f,function mri_lemma32at,{xi});
      mri_vbout(f,w);
      return w
   end;
	 
procedure mri_lemma32at(f,xi);
   mri_0mk2(mri_op f,mri_lemma32f(mri_arg2l f,xi),mri_type f);

procedure mri_lemma32f(u,xi);
   begin scalar w,c,v,r,xpnd;
      if domainp u then
 	 return u;
      c := mri_lemma32f(lc u,xi);
      r := mri_lemma32f(red u,xi);
      v := mvar u;
      if idp v then
 	 return addf(exptf(multf(c,!*k2f v),ldeg u),r);
      w := sfto_reorder(numr simp cadr v,xi);
      if domainp w or not (mvar w eq xi) then
 	 return addf(exptf(multf(c,!*k2f v),ldeg u),r);
      xpnd := addf(multf(lc w,!*k2f xi),numr simp {'floor,prepf red w});
      return addf(exptf(multf(c,xpnd),ldeg u),r)
   end;

procedure mri_case1(f,xi);
   begin scalar w;
      mri_vbin({"+++ MRI applying Theorem 3.1 Case 1 (restrict ",xi,
	 " to integer atfs)"},f);
      w := cl_simpl(cl_apply2ats1(f,function mri_case1at,{xi}),nil,-1);
      mri_vbout(f,w);
      return w
   end;      

procedure mri_case1at(f,xi);
   begin scalar lhs,nxi,s,fs,op;
      lhs := sfto_reorder(mri_arg2l f,xi);
      if domainp lhs or not (mvar lhs eq xi) then
 	 return f;
      nxi := multf(lc lhs,!*k2f mvar lhs);
      s := negf red lhs;
      fs := numr simp {'floor,prepf s};
      op := mri_op f;
      return mri_case1at1(op,nxi,s,fs)
   end;

procedure mri_case1at1(op,nxi,s,fs);
   if op eq 'neq then
      cl_nnfnot mri_case1at2('equal,nxi,s,fs)
   else if op eq 'geq then
      cl_nnfnot mri_case1at2('lessp,nxi,s,fs)
   else if eqcar(op,'ncong) then
      cl_nnfnot mri_case1at2(mri_mkop('cong,cdr op),nxi,s,fs)
%   else if op eq 'leq then
%      rl_mkn('or,{mri_case1at2('lessp,nxi,s,fs),mri_case1at2('equal,nxi,s,fs)})
   else if op eq 'greaterp then
%      cl_nnfnot rl_mkn('or,
%	 {mri_case1at2('lessp,nxi,s,fs),mri_case1at2('equal,nxi,s,fs)})
      cl_nnfnot mri_case1at2('leq,nxi,s,fs)
   else mri_case1at2(op,nxi,s,fs);

procedure mri_case1at2(op,nxi,s,fs);
   begin scalar w,ww,www;
      w := mri_0mk2(op,addf(nxi,negf fs),'int);
      if op eq 'equal or eqcar(op,'cong) then
      	 return rl_mkn('and,{w,mri_0mk2('equal,addf(s,negf fs),nil)});
      if op eq 'leq then
	 return w;
      % op eq 'lessp
      ww := mri_0mk2('equal,addf(nxi,negf fs),'int);
      www := mri_0mk2('lessp,addf(fs,negf s),nil);
      return rl_mkn('or,{w,rl_mkn('and,{ww,www})})
   end;
      
procedure mri_case2(f,u);
   begin scalar w;
      mri_vbin({"+++ MRI applying Theorem 3.1 Case 2 (remove ",u,
	 " from congruences)"},f);
      w := cl_simpl(cl_apply2ats1(f,function mri_case2at,{u}),nil,-1);
      mri_vbout(f,w);
      return w
   end;      

procedure mri_case2at(at,u);
   begin scalar w,n,s,fs,fsi,nums;
      if not mri_congp at or not (u memq kernels mri_arg2l at) then
	 return at;
      w := sfto_reorder(mri_arg2l at,u);
      if ldeg w neq 1 then
	 rederr "mri_case2at: real variable with degree > 1";
      n := lc w;
      if not domainp n then
	 rederr "mri_case2at: real variable with parametric coefficient";
      s := negf red w;
      fs := !*k2f !*a2k {'floor,prepf s};
      nums := addf(multf(n,!*k2f u),negf s);
      return rl_smkn('or,for i:=0:n collect <<
	 fsi := addf(fs,negf i);
      	 w := addf(nums,fsi);
	 rl_mkn('and,{mri_0mk2('equal,w,nil),mri_0mk2(mri_op at,fsi,'int)})
      >>)
   end;
      
endmodule;

end;
