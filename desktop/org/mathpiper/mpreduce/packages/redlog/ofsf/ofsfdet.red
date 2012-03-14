% ----------------------------------------------------------------------
% $Id: ofsfdet.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2003-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(ofsf_det_rcsid!* ofsf_det_copyright!*);
   ofsf_det_rcsid!* :=
      "$Id: ofsfdet.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   ofsf_det_copyright!* := "(c) 2003-2009 A. Dolzmann, A. Seidl, T. Sturm"
>>;

module ofsfdet;

procedure ofsf_det(m);
   % Determinant. [m] is a list of lists of standard forms, where
   % each element has the same length as [m].
   % Returns a standard form.
   if !*rlourdet then
      ofsf_newbareiss2 m
   else
      ofsf_bareiss m;

procedure ofsf_bareiss(nu);
   % Compute a determinant using the Bareiss code. [nu] is a matrix
   % given as a list of lists of SF's. Returns an SF, the determinant
   % of [nu].
   begin scalar nu,bu,n,ok,v,!*exp;
      !*exp := t;
      nu := for each line in nu collect
      	 for each elem in line collect
	    elem;
      nu := sort(nu,'ofsf_linesort3);
      n := length nu;  % We need it later once more ...
      if eqn(n,1) then
      	 return caar nu;
      v := for i:=1:n collect gensym();
      % Cannot rely on the ordering of the gensyms.
      ok := setkorder append(v,kord!*);
      nu := for each r in nu collect prsum(v,r);
      bu := cdr sparse_bareiss(nu,v,bareiss!-step!-size!*);
      bu := if length bu = n then
	 lc car bu
      else
	 nil;
      setkorder ok;
      return bu
   end;

procedure ofsf_linesort1(l1,l2);
   if null l1 then
      t
   else if null car l1 and not null car l2 then
      t
   else if not null car l1 and  null car l2 then
      nil
   else ofsf_linesort1(cdr l1,cdr l2);

procedure ofsf_linesort2(l1,l2);
   begin scalar z1,z2;
      z1 := for each x in l1 sum if null x then 1 else 0;
      z2 := for each x in l2 sum if null x then 1 else 0;
      if z1>z2 then
	 return t;
      if z2>z1 then
	 return nil;
      return ofsf_linesort1(l1,l2)
   end;

procedure ofsf_linesort3(l1,l2);
   not ofsf_linesort2(l1,l2);

procedure ofsf_newbareiss(m);
   begin scalar vm,w,mik,mkk,mk1k1; integer n;
      n := length m;
      vm := vmat_mk m;
      vmat_put(vm,0,0,numr simp 1);
      for k := 1:n-1 do <<
%%	 vmat_print vm;
	 ioto_prin2 {"[",n-1-k,"] "};
	 w := ofsf_goodlcpair(vm,k,n);
   	 if not w then
	    rederr "zero determinant";
	 if not eqn(k,cdr w) then <<
	    vmat_swapc(vm,k,cdr w);
	    ioto_prin2 {"(",cdr w,"<-c->",k,")"}
	 >>;
	 if not eqn(k,car w) then <<
	    vmat_swapl(vm,k,car w);
	    ioto_prin2 {"(",car w,"<-l->",k,")"}
	 >>;
%%	 vmat_print vm;
	 mkk := vmat_get(vm,k,k);
	 mk1k1 := vmat_get(vm,k-1,k-1);
	 for i := k+1:n do <<
	    mik := vmat_get(vm,i,k);
 	    for j := k+1:n do <<
	       w := addf(
		  multf(vmat_get(vm,i,j),mkk),
		  negf multf(mik,vmat_get(vm,k,j)));
	       if w then
		  w := quotfx(w,mk1k1);
	       vmat_put(vm,i,j,w)
	    >>
	 >>
      >>;
%%      vmat_print vm;
      return vmat_get(vm,n,n)
   end;

procedure ofsf_newbareiss2(m);
   begin scalar vm,sign,w,k,cnt,doit; integer n;
      n := length m;
      vm := vmat_mk m;
      sign := numr simp 1;
      vmat_put(vm,0,0,sign);
      if !*rlvmatvb then
      	 ioto_cterpri();
      cnt := t; k := 1; while cnt and k < n do <<
	 if !*rlvmatvb then
	    ioto_prin2 {"[",n-1-k};
	 w := ofsf_goodlcpair(vm,k,n);
   	 if not w then <<
	    if !*rlvmatvb then
	       ioto_prin2 "zero]";
	    cnt := nil
	 >> else <<
	    sign := ofsf_bareiss!-pivot(vm,k,w,sign);
	    if doit then
	       ofsf_bareiss!-step(vm,k,n);
	    if !*rlvmatvb then
	       ioto_prin2 "] ";
	    doit := not doit;
	    k := k+1
      	 >>
      >>;
      if not cnt then
	 return nil;
      if doit then <<
	 if !*rlvmatvb then
	    ioto_prin2 "[final";
	 w := ofsf_cdet2(vmat_get(vm,n-1,n-1),vmat_get(vm,n-1,n),
	    vmat_get(vm,n,n-1),vmat_get(vm,n,n));
	 if w then
	    w := quotfx(w,vmat_get(vm,n-2,n-2));
	 vmat_put(vm,n,n,w);
	 if !*rlvmatvb then
	    ioto_prin2 "]"
      >>;
      return multf(sign,vmat_get(vm,n,n))
   end;

procedure ofsf_bareiss!-pivot(vm,k,w,sign);
   <<
      if not eqn(k,cdr w) then <<
      	 vmat_swapc(vm,k,cdr w);
      	 sign := negf sign;
      	 if !*rlvmatvb then
	    ioto_prin2 {"(",cdr w,"<-c->",k,")"}
      >>;
      if not eqn(k,car w) then <<
   	 vmat_swapl(vm,k,car w);
      	 sign := negf sign;
   	 if !*rlvmatvb then
      	    ioto_prin2 {"(",car w,"<-l->",k,")"}
      >>;
      sign
   >>;

procedure ofsf_bareiss!-step(vm,k,n);
   begin scalar c0,ci1,ci2,w;
      c0 := ofsf_cdet2(vmat_get(vm,k-1,k-1),vmat_get(vm,k-1,k),
	 vmat_get(vm,k,k-1),vmat_get(vm,k,k));
      if c0 then
	 c0 := quotfx(c0,vmat_get(vm,k-2,k-2));
      for i := k+1:n do <<
	 ci1 := negf ofsf_cdet2(vmat_get(vm,k-1,k-1),vmat_get(vm,k-1,k),
	    vmat_get(vm,i,k-1),vmat_get(vm,i,k));
	 if ci1 then
	    ci1 := quotfx(ci1,vmat_get(vm,k-2,k-2));
	 ci2 := ofsf_cdet2(vmat_get(vm,k,k-1),vmat_get(vm,k,k),
	    vmat_get(vm,i,k-1),vmat_get(vm,i,k));
	 if ci2 then
	    ci2 := quotfx(ci2,vmat_get(vm,k-2,k-2));
	 for j := k+1:n do <<
	    w := addf(addf(
	       multf(vmat_get(vm,i,j),c0),
	       multf(vmat_get(vm,k,j),ci1)),
	       multf(vmat_get(vm,k-1,j),ci2));
	    if w then
	       w := quotfx(w,vmat_get(vm,k-2,k-2));
	    vmat_put(vm,i,j,w)
	 >>
      >>;
      w := ofsf_cdet2(vmat_get(vm,k-1,k-1),vmat_get(vm,k-1,k),
	 vmat_get(vm,k,k-1),vmat_get(vm,k,k));
      if w then
	 w := quotfx(w,vmat_get(vm,k-2,k-2));
      vmat_put(vm,k,k,w)
   end;

procedure ofsf_cdet2(a11,a12,a21,a22);
   addf(multf(a11,a22),negf multf(a12,a21));

procedure ofsf_goodline(m,k,n);
   begin integer bestl,maxz,z;
      maxz := -1;
      for l := k:n do <<
	 if not null vmat_get(m,l,k) then <<
	    z := for j := k+1:n sum if null vmat_get(m,l,j) then 1 else 0;
      	    if z > maxz then <<
	       maxz := z;
	       bestl := l
	    >>
	 >>
      >>;
      if not eqn(maxz,-1) then
	 return bestl
   end;

procedure ofsf_goodcolumn(m,k,n);
   begin integer bestc,maxz,z;
      maxz := -1;
      for c := k:n do <<
	 if not null vmat_get(m,k,c) then <<
	    z := for i := k+1:n sum if null vmat_get(m,i,c) then 1 else 0;
      	    if z > maxz then <<
	       maxz := z;
	       bestc := c
	    >>
	 >>
      >>;
      if not eqn(maxz,-1) then
	 return bestc
   end;

%% procedure ofsf_goodlcpair(m,k,n);
%%    begin scalar bestlc; integer maxz,max1z,cz,lz,z;
%%       maxz := max1z := -1;
%%       for i:=k:n do
%% 	 for j:=k:n do
%% 	    if vmat_get(m,i,j) then <<
%% 	       lz := for jj := k+1:n sum if null vmat_get(m,i,jj) then 1 else 0;
%% 	       cz := for ii := k+1:n sum if null vmat_get(m,ii,j) then 1 else 0;
%% 	       z := lz + cz;
%% 	       if z > maxz or (eqn(z,maxz) and max(lz,cz) > max1z) then <<
%% 		  maxz := z;
%% 		  max1z := max(lz,cz);
%% 		  bestlc := i . j
%% 	       >>
%% 	    >>;
%%       if not eqn(maxz,-1) then
%% 	 return bestlc
%%    end;

procedure ofsf_goodlcpair(m,k,n);
   begin scalar bestlc; integer minz,min1z,cz,lz,z;
      minz := min1z := 6*n+1;
      for i:=k:n do
	 for j:=k:n do
	    if vmat_get(m,i,j) then <<
	       lz := for jj := k+1:n sum ofsf_quality vmat_get(m,i,jj);
	       cz := for ii := k+1:n sum ofsf_quality vmat_get(m,ii,j);
	       z := lz + cz;
	       if z < minz or (eqn(z,minz) and max(lz,cz) < min1z) then <<
		  minz := z;
		  min1z := max(lz,cz);
		  bestlc := i . j
	       >>
	    >>;
      if not eqn(minz,6*n+1) then
	 return bestlc
   end;

procedure ofsf_quality(f);
   if null f then 1 else if numberp f then 0 else 0;

%DS
% <VMAT> ::= [...,[...,<SF>,...],...]
% First line and first column exist for pivot.
% Last line is permutation info.

procedure vmat_print(vm);
   mathprint vmat_prep vm;

procedure vmat_prep(vm);
   begin integer n;
      n := upbv getv(vm,0) - 1;
      return 'mat . for i := 1:n collect
	 for j := 1:n collect
	    prepf vmat_get(vm,i,j)
   end;
      
procedure vmat_mk(m);
   % [m] is a list of lists of SF.
   begin scalar line,vmat; integer n,i,j;
      n := length m;
      vmat := mkvect(n+1);
      line := mkvect(n+1);
      putv(vmat,0,line);
      line := mkvect(n+1);
      for j := 0:n do
	 putv(line,j,j);
      putv(vmat,n+1,line);
      for each l in m do <<
	 i := i + 1;
	 line := mkvect(n+1);
	 j := 0;
	 for each c in l do <<
	    j := j + 1;
	    putv(line,j,c)
	 >>;
	 putv(vmat,i,line)
      >>;
      return vmat
   end;

procedure vmat_get(m,i,j);
   getv(getv(m,i),vmat_cmap(m,j));

procedure vmat_put(m,i,j,c);
   putv(getv(m,i),vmat_cmap(m,j),c);

procedure vmat_cmap(m,j);
   getv(getv(m,upbv m),j);

procedure vmat_swapl(m,i1,i2);
   begin scalar w;
      w := getv(m,i1);
      putv(m,i1,getv(m,i2));
      putv(m,i2,w)
   end;

procedure vmat_swapc(m,j1,j2);
   begin scalar w,map;
      map := getv(m,upbv m);
      w := getv(map,j1);
      putv(map,j1,getv(map,j2));
      putv(map,j2,w)
   end;

operator bdet;

procedure bdet1(m);
   prepf ofsf_newbareiss for each l in cdr m collect
      for each c in l collect
	 numr simp c;

operator bdet1;

procedure bdet(m);
   prepf ofsf_newbareiss2 for each l in cdr m collect
      for each c in l collect
	 numr simp c;

operator gmat;

procedure gmat(n);
   'mat . for i := 1:n collect for j:=1:n collect mkid(mkid('a,i),j);

endmodule;

end;  % of file
