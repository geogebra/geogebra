% ----------------------------------------------------------------------
% $Id: ofsfcadproj.red 1840 2012-11-19 12:26:04Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2000-2009 A. Dolzmann, L. Gilch, A. Seidl, and T. Sturm
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
   fluid '(ofsf_cadproj_rcsid!* ofsf_cadproj_copyright!*);
   ofsf_cadproj_rcsid!* :=
      "$Id: ofsfcadproj.red 1840 2012-11-19 12:26:04Z thomas-sturm $";
   ofsf_cadproj_copyright!* :=
      "(c) 2000-2009 A. Dolzmann, L. Gilch, A. Seidl, T. Sturm"
>>;

fluid '(ofsf_cadbvl!* !*rlpos);

switch rlcadmc3;
on1 'rlcadmc3;

module ofsfcadproj;
% CAD projection. [ffr] is a list of sf's over identifiers contained
% in [varl], [varl] is a list of identifiers (xr,...,x1), [k] is an
% integer. Returns a list of list of sf's. The kernel order is
% expected to be (...,xr,...,x1), and all elements of $F_r$ are
% ordered in this respect. If switch rlcadaproj is turned off,
% augmented projection will never be made. Otherwise, if rlcadaproj is
% turned on: If rlcadaprojalways is turned off, augemented projection
% is used for generating $F_{k-1},...,F_1$, otherwise, if
% rlcadaprojalways is turned on, augmented projection is used for
% $F_{r-1},...,F_1$. Intuition: Projection phase, maps Fr to
% (F1,...,Fr). CAVEAT: below, varl is $x_r,...,x2$???

%%% --- projection order code --- %%%

algebraic procedure rlcadporders(phi);
   ordersfromvbl rlcadvbl phi;

algebraic procedure ordersfromvbl(vbl);
   if vbl={} then {{}} else
      for each vl1 in perturbations first vbl join
	 for each vl2 in ordersfromvbl rest vbl collect
	    append(vl1,vl2);

symbolic operator rlcadpordersnum;
lisp procedure rlcadpordersnum(phi);
   for each n in (for each b in ofsf_cadvbl1 rl_simp phi collect length b)
      product factorial n;

symbolic operator rlcadvbl2pord;
lisp procedure rlcadvbl2pord(vbl);
   for each vb in vbl join vb;

symbolic operator rlcadvbl;
procedure rlcadvbl(phi);
   'list . for each l in ofsf_cadvbl rl_simp phi collect 'list . l;

procedure ofsf_cadvbl(phi);
   % Variable-block-list. Checks if [phi] is a prenex. Returns a list of
   % lists [[xr..][..xk]..[xk..x1]] of IDs.
   <<
      if not cl_prenexp phi then
      	 rederr "formula is not prenex, please use rlpnf beforehand";
      ofsf_cadvbl1(phi)
   >>;

procedure ofsf_cadvbl1(phi);
   % Variable-block-list. [phi] is a prenex fof. Returns a list of
   % lists [[xr..][..xk]..[xk..x1]] of IDs.
   begin scalar tmp,fvarl,ql,cq,cll,cl,a;
      tmp := ofsf_mkvarl phi; % ((xr,...,x_1).((x_r.Q_r),...,(x_k+1.Q_k+1)))
      fvarl := car tmp; % ((xr,...,x_1)
      ql := cdr tmp;
      if ql then <<
      	 cq := cdar ql; % current quantifier
      	 while ql do <<
	    a := car ql;
	    ql := cdr ql;
	    fvarl := cdr fvarl;
	    if cdr a neq cq then <<
	       cll := cl . cll;
	       cq := cdr a;
	       cl := nil
	    >>;
	    cl := car a . cl;
	 >>;
	 cll := reversip cl . cll
      >>;
      cll := fvarl . cll;
      cll := reversip cll;
      return cll
end;

procedure ofsf_gcadporder(phi);
   % Generic CAD projection order. [phi] is an OFSF FORMULA. Returns a
   % list of identifiers. The result is a list of all variables in a
   % PNF of [phi] encoding an order suitable for generic CAD
   % projection. We assume that [ofsf_gcad] uses [cl_pnf] for PNF
   % computation.
   begin scalar !*rlqegen;
      !*rlqegen := t;
      return ofsf_cadporder phi
   end;

procedure ofsf_cadporder(phi);
   ofsf_cadporder0(phi,'ofsf_cadporder!-rate,'ofsf_cadporder!-betterp);

switch dolzmann;

procedure ofsf_cadporder!-betterp(rating,optrating,theo,theoopt);
   if not !*dolzmann then
      not optrating or rating < optrating or
      (!*rlqegen and rating = optrating and length theo < length theoopt)
   else
      not optrating or rating > optrating or
      (!*rlqegen and rating = optrating and length theo < length theoopt);

procedure ofsf_cadporder!-rate(pset);
   % length pset;
   for each f in pset sum sf_stdeg f;

procedure ofsf_cadporder0(phi,rate,betterp);
   % CAD projection order. [phi] is an OFSF FORMULA. Returns a list of
   % identifiers. The result is a list of all variables in a PNF of
   % [phi] encoding an order suitable for CAD projection. We assume
   % that [ofsf_cad] uses [cl_pnf] for PNF computation.
   begin scalar cll,varl,!*rlcadverbose;
      if !*rlverbose then ioto_prin2t "+++ Optimizing projection order";
      if !*rlcaddecdeg then
	 phi := ofsf_caddecdeg phi;
      phi := cl_pnf phi;
      cll := ofsf_cadsplt phi;
      if !*rlverbose then <<
      	 ioto_tprin2 {"++ input order: ->"};
	 for each x in cll do
	    ioto_prin2 {" ",x," ->"}
      >>;
      cll := ofsf_cadporder1(ofsf_transfac(cl_terml phi,nil),cll,rate,betterp);
      if !*rlverbose then <<
      	 ioto_tprin2 {"++ optimized order: ->"};
	 for each x in cll do
	    ioto_prin2 {" ",x," ->"}
      >>;
      varl := for each cl in cll join cl;
      return varl
   end;

procedure ofsf_caddecdeg(phi);
   begin scalar w;
      if !*rlverbose then ioto_prin2 "- decrease degrees: ";
      w := ofsf_decdeg0 phi;
      phi := car w;
      if !*rlverbose then
	 ioto_prin2 for each x in cdr w join {"(",car x,"^",cdr x,")"};
      return phi
   end;

procedure ofsf_cadsplt(phi);
   begin scalar fvarl,ql,cq,cll,cl,a,tmp;
      tmp := ofsf_mkvarl phi;
      fvarl := car tmp;
      ql := cdr tmp;
      if ql then <<
      	 cq := cdar ql;
      	 while ql do <<
	    a := car ql;
	    ql := cdr ql;
	    fvarl := cdr fvarl;
	    if cdr a neq cq then <<
	       cll := cl . cll;
	       cq := cdr a;
	       cl := nil
	    >>;
	    cl := car a . cl;
	 >>;
	 cll := reversip cl . cll
      >>;
      cll := fvarl . cll;
      cll := reversip cll;
      return cll
   end;

procedure ofsf_cadporder1(tl,cll,rate,betterp);
   % CAD projection order subroutine. [tl] is a list of (irreducible)
   % SF's; [cll] is a LIST of lists of identifiers. Returns a LIST of
   % lists of identifers. The variable order is optimized for
   % projection within each list in [cll].
   begin scalar w,varl,ncll,cl,theo;
      varl := for each cl in cll join append(cl,nil);
      while cll do <<
	 cl := car cll;
	 cll := cdr cll;
	 if cl then <<
	    w := ofsf_cadporder2(tl,cl,varl,null cll or null car cll,theo,
	       rate,betterp);
	    tl := car w;
	    ncll := cadr w . ncll;
	    theo := caddr w
	 >> else
	    ncll := nil . ncll
      >>;
      ncll := reversip ncll;
      return ncll
   end;

procedure ofsf_cadporder2(tl,cl,varl,lastp,theo,rate,betterp);
   % CAD projection order subroutine. [tl] is a list of (irreducible)
   % SF's; [cl] is a LIST of identifiers; [varl] is a LIST of
   % IDENTIFIERS; [lastp] is BOOLEAN. Returns a pair $(T . V)$, where
   % $T$ is a LIST of SF's and $V$ is a LIST of IDENTIFIER's. [varl]
   % is the list of all variables in the original input formula in the
   % given input order, i.e., [cl] is a subsegment of [varl]. If
   % [lastp] is non-[nil], then we are in the last projection block.
   % $V$ contains the variables from [cl] in an order optimized for
   % projection, and $T$ is the projection set after projecting in
   % this order $V$.
   begin scalar w,ncl,lvarl;
      if !*rlverbose then
	 ioto_tprin2t {"+ Current input block: -> ",cl," ->"};
      lvarl := member(car cl,varl);
      while cl and (not lastp or cdr cl) do <<
      	 w := ofsf_cadporder3(tl,cl,lvarl,theo,rate,betterp);
	 tl := car w;
	 ncl := cadr w . ncl;
	 theo := caddr w;
	 cl := delete(cadr w,cl);
	 lvarl := delete(cadr w,lvarl)
      >>;
      if lastp then
	 ncl := car cl . ncl;
      ncl := reversip ncl;
      if !*rlverbose then
	 ioto_tprin2t {"+ Reordered block: ",ncl};
      return {tl,ncl,theo}
   end;

procedure ofsf_cadporder3(tl,cl,lvarl,theo,rate,betterp);
   % CAD projection order subroutine. [tl] is a list of (irreducible)
   % SF's; [cl] is a LIST of identifiers; [lvarl] is a LIST of
   % identifiers. Returns a pair $(T . v)$, where $T$ is a LIST of
   % SF's and $v$ is an IDENTIFIER. [lvarl] is the tail of the list of
   % all variables in the original input formula in the given input
   % order starting with [cl]. $v$ is the best variables in [cl] for
   % the next projection step and $T$ is the result of this projection
   % step.
   begin scalar pset,xopt,psetopt,optrating,theoopt,rating,j,psetpr;
      j := length lvarl;
      for each x in cl do <<
	 if !*rlverbose then
	    ioto_prin2 {"[",x,":"};
	 lvarl := x . delete(x,lvarl);
	 psetpr := ofsf_cadporder!-project(tl,x,lvarl,j,theo);
	 pset := car psetpr;
	 rating := apply(rate,{pset});
	 pset := union(car psetpr,cdr psetpr);
	 if !*rlverbose then <<
	    ioto_prin2 rating;
	    if !*rlqegen then
	       ioto_prin2 {"/",length theo};
	    ioto_prin2 "] "
	 >>;
	 if apply(betterp,{rating,optrating,theo,theoopt}) then <<
	    xopt := x;
	    psetopt := pset;
	    optrating := rating;
	    if !*rlqegen then theoopt := theo
	 >>;
      >>;
      if !*rlqegen then theo := theoopt;
      if !*rlverbose then ioto_prin2t {"choose ",xopt};
      return {psetopt,xopt,theo}
   end;

procedure ofsf_cadporder!-project(tl,x,lvarl,j,theo);
   begin scalar pset,oldorder;
      oldorder := setkorder {x};
      tl := for each f in tl collect
	 reorder f;
      if !*rlqegen then
	 theo := for each at in theo collect
	    ofsf_0mk2(ofsf_op at,reorder ofsf_arg2l at);
      pset := ofsf_cadporder!-project1(tl,x,lvarl,j,theo);
      setkorder oldorder;
      return pset
   end;

procedure ofsf_cadporder!-project1(tl,x,lvarl,j,theo);
   begin scalar ffj,ffi,pset,w;
      ffj := ffi := nil;
      for each f in tl do
	 if mvar f eq x then
	    ffj := f . ffj
	 else
	    ffi := f . ffi;
      pset :=  if !*rlqegen then <<
	 w := ofsf_projopcohogen(ffj,reverse lvarl,j,theo);
	 theo := cdr w;
	 ofsf_transfac(car w,x)
      >> else
	 ofsf_transfac(ofsf_projopcoho(ffj,reverse lvarl,j),x);
      return (pset . ffi)
   end;

%%% --- projection code --- %%%

% What the projection phase expects from CADDATA: aa, aaplus, varl, k.
% What is filled in: ff, hh

procedure ofsf_cadprojection1(cd);
   % CAD projection phase. [cd] is CADDATA.
   % extracted from the input formula); [varl] is the list x1,...,xr
   % of variables; [k] is the number of free variables. Returns (Fr,...,F1).
   begin scalar aa,varl,k,r,ff,jj,pp,w,theo,l,hh,ffid;
      varl := caddata_varl cd; % the list x1,...,xr
      k := caddata_k cd; % the number of free variables
      r := length varl; % the number of variables
      aa := getv(caddata_ff cd,r); % input formula polynomials
      ff := mkvect r; % here go the projection factors
      hh := mkvect r; % here go the tagged projection factors
      jj := mkvect r; % here go the projection polynomials
      ffid := mkvect(r); % here go the id's of the projection polynomials
      % hack: generic cad: new projection
      if !*rlqegen then <<
	 ofsf_cadbvl!* := lto_drop(varl,k); % better: caddata_bvl
	 w := ofsf_projsetcohogen(aa,varl,nil);
	 pp := car w;
	 theo := cdr w;
	 caddata_puttheo(cd,theo);
      >> else <<
      	 pp := ofsf_projsetcoho(aa,varl)
      >>;
      ofsf_mapdistribute(pp,ff,varl);
      ofsf_mapdistribute(pp,jj,varl); % unused
      for j := 1 : r do <<
	 l := 0;
         putv(hh,j,list2vector for each f in getv(ff,j) collect
	    %<< l := l+1; tag_(f,{{'ff,j,l}}) >>)
	    << l := l+1; tag_(f,{ofsf_ffji(j,l)}) >>);
	 putv(ffid,j,for l := 1:length getv(ff,j) collect ofsf_ffji(j,l));
      >>;
      caddata_putff(cd,ff); caddata_putjj(cd,jj);
      caddata_puthh(cd,hh); caddata_putffid(cd,ffid);
      if !*rlverbose then
	 if !*rlqegen then
	    ioto_tprin2 {"+ #P=",length pp,", #Theta=",length theo}
	 else
	    ioto_tprin2 {"+ #P=",length pp};
      return ; % caddata changed, nothing to return
   end;

procedure ofsf_ffji(j,l);
   % Make an identifier of the form Fj_l.
   intern compress lto_nconcn {explode 'ff, explode j,explode '!_, explode l};

procedure ofsf_level(f,varl); %%% candidate for sfto
   % Level of a polynomial wrt to the variable list. Returns 0, if $f$
   % is constant, the position of f's main variable in varl,
   % otherwise.
   if null varl then
      rederr "***** ofsf_level: invalid kernel"
   else if domainp f then
      0
   else if mvar f eq car varl then
      1
   else 1+ofsf_level(f,cdr varl);

procedure ofsf_mapdistribute(fl,ff,varl);
   for each f in fl do ofsf_distribute(f,ff,varl);

procedure ofsf_distribute(f,ff,varl);
   %%% test, if the polynomial is there already
%   (if l>0 then putv(ff,l,f . getv(ff,l))) where l=ofsf_level(f,varl);
   (if l>0 then if not (f member getv(ff,l)) then %%% memq?
      putv(ff,l,f . getv(ff,l))) where l=ofsf_level(f,varl);

%%% --- reducta, leading coefficients,... --- %%%

% removed stuff here

%%% --- to be included into sfto --- %%%

algebraic procedure delnth(l,n);
   % delete [n]-th element from list [l]
   if n=1 then rest l else append({first l},delnth(rest l,n-1));

algebraic procedure mynth(l,n);
   % nth lelement of a list
   if n=1 then first l else mynth(rest l,n-1);

algebraic procedure perturbations(l);
   if l={} then {{}} else
      for j := 1 : length l join
    	 for each p in perturbations(delnth(l,j)) collect
	    append({mynth(l,j)},p);

% - sum of total degrees of monomials - %

symbolic operator rlstdeg;
procedure rlstdeg(f);
   sf_stdeg numr simp f;

procedure sf_stdeg(f);
   % Sum of total degrees of a polynomial.
   if null f or f=0 then
      -1
   else sf_stdeg1 f;

procedure sf_stdeg1(f);
   if null f or f=0 then
      0 % a zero subpolynomial adds nothing to the total degree
   else if numberp f then
      0
   else
      sf_stdeg1(lc f)+ldeg(f)+sf_stdeg1(red f);

procedure sf_print(f);
   mathprint prepf f;

% - total degree - %

symbolic operator rltdeg;
procedure rltdeg(f,xl);
    prepf sf_tdeg!*(numr simp f,cdr xl);

procedure sf_tdeg!*(f,xl);
   % Total degree. [f] is a SF, [xl] is a LIST(ID). Returns a NUM.
   % Reordering is performed, so the result is correct.
   begin scalar oldorder,w;
      oldorder := setkorder xl;
      w := sf_tdeg(reorder f,xl);
      setkorder oldorder;
      return w
   end;

procedure sf_tdeg(f,xl);
   % Total degree. [f] is a SF, [xl] is a LIST(ID). Returns a NUM.
   % [f] has to be ordered in a way compatible to [xl].
   if null f or f=0 then
      -1
   else if null xl then
      0
   else
      sf_tdeg1(f,xl);

procedure sf_tdeg1(f,xl);
   % Total degree subroutine.
   if null f or f=0 then
      0
   else if null xl then
      0
   else
      max(sf_tdeg1(sf_lc(f,car xl),cdr xl)+sf_deg(f,car xl),
	 sf_tdeg1(sf_red(f,car xl),xl));

% - leading coefficient - %

symbolic operator rllc;
procedure rllc(f,x);
   prepf sf_lc!*(numr simp f,x);

procedure sf_lc!*(f,x);
   % Leading coefficient. [f] is a SF, [x] is a ID. Reordering is
   % performed, so the result is correct.
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := sf_lc(reorder f,x);
      setkorder oldorder;
      return reorder w
   end;

procedure sf_lc(f,x);
   % Leading coefficient. [f] is a SF, [x] is a ID. Returns a SF. [f]
   % has to be ordered in a way compatible to a list containing [x].
   if not domainp f and mvar f eq x then lc f else f;

% - reductum - %

symbolic operator rlred;
procedure rlred(f,x);
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := prepf sf_red(numr simp f,x);
      setkorder oldorder;
      return w
   end;

procedure sf_red(f,x);
   % Univariate reductum of a standard form.
   if not domainp f and mvar f eq x then red f else nil;

symbolic operator rldis;
procedure rldis(f,x);
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := prepf sf_discriminant(numr simp f,x);
      setkorder oldorder;
      return w
   end;

% - discriminant - %

procedure sf_discriminant(f,x);
   % discriminant. caveat: deg(f,x)>0 required.
   quotf(resultant(f,numr difff(f,x),x),lc f);

symbolic operator rlres;
procedure rlres(f,g,x);
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := prepf resultant(numr simp f,numr simp g,x);
      setkorder oldorder;
      return w
   end;

procedure sf_foldgcd(fl);
   % fold gcd. fl is a non-empty list of SF.
   if null cdr fl then car fl else gcdf(car fl,sf_foldgcd cdr fl);

%% procedure sf_coeffs(f,x);
%%    % Coefficients. f is a not null SF. Returns a not dense list of
%%    % coefficients.
%%    if not null f then
%%       if domainp f or mvar f neq x then {f} else lc f . sf_coeffs(red f,x);

procedure sf_densecoeffs(f,x);
   % Dense coefficient list.
   begin scalar clred;
      if sf_deg(f,x)<=0 then return {f};
      clred := sf_densecoeffs(red f,x);
      for i :=  max(0,sf_deg(red f,x))+1 : (ldeg f)-1  do clred := nil . clred;
      clred := lc f . clred;
      return clred
   end;

procedure sf_fromdensecoeffs(fl,k);
   % Standard form from dense coefficient list. [fl] is a non-empty
   % LIST(SF), k is a KERNEL. Returns a SF.
   begin scalar f;
      if null cdr fl then return car fl;
      if null car fl then return sf_fromdensecoeffs(cdr fl,k);
      f := sf_expt(k,length(fl)-1);
      lc f := car fl;
      red f := sf_fromdensecoeffs(cdr fl,k);
      return f
   end;

procedure sf_c(f);
   % Content. f is a SF.
   if domainp f then f else sf_foldgcd sf_coeffs(f,mvar f);

procedure sf_pp(f);
   % Primitive part.
   quotf(f,sf_c f);

%%%%%%%%%%%%%%%%%%
% - Regularity - %
%%%%%%%%%%%%%%%%%%

symbolic operator rlisregular;
procedure rlisregular(fl,xl);
   (if tv then 'true else 'false)
      where tv=sf_isregular!*(for each f in fl collect numr simp f,cdr xl);

procedure sf_isregular!*(fl,yl);
   % Regularity test. [f] is a SF, [yl] is a LIST(ID). Returns a truth
   % value. Reordering is performed, so the result is correct.
   begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_isregular(for each f in fl collect reorder f,yl);
      setkorder oldorder;
      return w
   end;

procedure sf_isregular(fl,yl);
   % Regularity test. [fl] is a LIST(SF), [yl] is a LIST(ID). Returns
   % a truth value. [f] has to be ordered in a way compatible to [yl].
   if null fl then
      t
   else
      sf_tdeg(sf_lc(car fl,car yl),cdr yl)<=0 and sf_isregular(cdr fl,yl);

symbolic operator rltransreg;
procedure rltransreg(fl,yl,cl);
   'list . for each fr in
      sf_transreg!*(for each f in cdr fl collect numr simp f,cdr yl,cdr cl)
	 collect prepf fr;

procedure sf_transreg!*(fl,yl,cl);
    begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_transreg(for each f in fl collect reorder f,yl,cl);
      setkorder oldorder;
      return reorder w
   end;

procedure sf_transreg(fl,yl,cl);
   % Transformation into a regular polynomial. [fl] is a LIST(SF),
   % [yl] is a LIST(ID) and [cl] is a LIST(NUM). Returns a LIST(SF).
   for each f in fl collect sf_transregf(f,yl,cl);

procedure sf_transregf(f,yl,cl);
   % Transformation into a regular polynomial. [f] is a SF, [yl] is a
   % LIST(ID) and [cl] is a LIST(NUM). Returns a SF.
   begin
      scalar y1,yj,cj,al;
      y1 := car yl;
      yl := cdr yl;
      while yl do <<
	 yj := car yl;
	 cj := car cl;
	 %al := (car yl . addf(!*k2f yj,multf(cj,!*k2f y1)) . al;
	 al := (yj . {'plus,yj,{'times,cj,y1}}) . al;
	 yl := cdr yl;
	 cl := cdr cl;
      >>;
      return numr subf(f,al)
   end;

symbolic operator rlregularize;
procedure rlregularize(fl,yl);
   ('list . for each f in w collect prepf f)
      where
	 w=sf_regularize!*(for each f in cdr fl collect numr simp f,cdr yl);

procedure sf_regularize!*(fl,yl);
    begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_regularize(for each f in fl collect reorder f,yl);
      setkorder oldorder;
      return for each f in w collect reorder f
   end;

procedure sf_regularize(fl,yl);
   % Regularize. [fl] is a LIST(SF), [yl] is a LIST(ID). Returns a
   % LIST(SF).
   car sf_regularize1(fl,yl);

symbolic operator rlregularize1;
procedure rlregularize1(fl,yl);
   {'list, 'list . for each f in car w collect prepf f, 'list . cdr w}
      where
	 w=sf_regularize1!*(for each f in cdr fl collect numr simp f,cdr yl);

procedure sf_regularize1!*(fl,yl);
    begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_regularize1(for each f in fl collect reorder f,yl);
      setkorder oldorder;
      return (for each f in car w collect reorder f) . cdr w
   end;

procedure sf_regularize1(fl,yl);
   % Regularize. [fl] is a LIST(SF), [yl] is a LIST(ID). Returns a
   % PAIR(LIST(SF),LIST(NUM)).
   begin
      scalar flr,cl1,cl2,cl;
      flr := fl;
      cl := for each y in cdr yl collect 1;
      cl1 := cl;
      while not sf_isregular(flr,yl) do <<
	 flr := sf_transreg(fl,yl,cl);
	 cl2 := cl;
	 cl := sf_nextcl cl;
      >>;
      if null cl2 then
	 return flr . for each y in cdr yl collect 0
      else
      	 return flr . cl2
   end;

procedure sf_nextcl(cl);
   % Next constant list (Subroutine for sf_regularize). [cl] is a
   % LIST(NUM). Returns a LIST(NUM). In an ideal world this would
   % enumerate $\mathbb{N}^{|cl|}$.
   begin
      scalar c1;
      c1 := car cl;
      return reversip ((c1+1) . (reverse cdr cl))
   end;

%symbolic operator rlregoptordf;
%procedure rlregoptordf(f,yl);
%   'list . for each a in sf_regoptordf(numr simp f, cdr yl) collect
%      'list . a;

procedure sf_regoptordf!*(f,yl);
   begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_regoptordf(reorder f,yl);
      setkorder oldorder;
      return reorder w
   end;

procedure sf_regoptordf(f,yl);
   % Optimal order for regularization (wrt. nom). Returns a
   % LIST(LIST(ID)).
   begin
      scalar ords;
      ords := lto_choose(cdr yl);
      ords := for each o in ords collect car yl . o;
      ords := for each o in ords collect
	 o . sf_nom(sf_regularize!*(f,o));
      ords := for each o in ords join if
   	 sf_isregular!*(sf_regularize!*(f,car o),yl) then {o};
      return reversip
	 sort(for each o in ords collect car o,function length);
   end;

% - begin baustelle

symbolic operator rlrorders;
procedure rlrorders(fl,yl);
   'list . for each o in
      sf_rorders!*(for each f in fl collect numr simp f, cdr yl) collect
	 'list . o;

procedure sf_rorders!*(fl,yl);
   begin scalar oldorder,w;
      oldorder := setkorder yl;
      w := sf_rorders(for each f in fl collect reorder f,yl);
      setkorder oldorder;
      return w
   end;

procedure sf_rorders(fl,yl);
   % Optimal orders for regularization (wrt. nom). [fl] is a LIST(SF).
   % Returns a non-empty LIST(LIST(ID)). This list starts with the
   % shortest order of minimal cost.
   begin
      scalar ords,flr,nom;
      if null yl then
	 rederr "empty regualrization order invalid";
      ords := lto_choose(cdr yl);
      ords := for each o in ords collect car yl . o;
      ords := for each o in ords collect <<
	 flr := sf_regularize!*(fl,o);
	 if sf_isregular!*(flr,yl) then
	    nom := for each f in flr sum sf_nom f
	 else
	    nom := -1;
	 o . nom
      >>;
      % remove illegal orders
      ords := for each p in ords join if cdr p >= 0 then {p};
      % sort by cost and length.
      ords := sort(ords,'ofsf_rordp);
      return for each p in ords collect car p
   end;

procedure ofsf_rordp(p1,p2);
   (cdr p1 < cdr p2) or (cdr p1 = cdr p2 and length car p1 < length car p2);

% - end baustelle

symbolic operator rlchoose;
procedure rlchoose(l);
   'list . for each a in lto_choose cdr l collect 'list . a;

procedure lto_choose(l);
   % Powerset. [l] is a LIST(ANY). Returns a LIST(LIST(ANY)).
   begin
      scalar w;
      if null l then return {{}};
      w := lto_choose cdr l;
      return append(w,for each a in w collect car l . a)
   end;

symbolic operator rlnom;
procedure rlnom(f);
   sf_nom(numr simp f);

procedure sf_nom(f);
   % Number of monomials. [f] is a SF. Returns a NUM.
   if null f then
      0
   else if domainp f then
      1
   else
      sf_nom lc f + sf_nom red f;


%%% --- To be included into lto --- %%%

procedure lto_select(fn,l);
   % Select elements from a list. [fn] is a function of type
   % ALPHA->BOOL, [l] is a list of ALPHA. Returns a list of ALPHA.
   lto_select1(fn,l,nil);

procedure lto_select1(fn,l,xarl);
   % Select elements from a list. [fn] is a function with
   % length([xarl])+1 arguments , [l] and [xarl] are LIST. Returns a
   % LIST.
   for each a in l join if apply(fn,a . xarl) then {a};

procedure lto_remove(fn,l);
   % Remove elements from a list. [fn] is a function of type
   % ALPHA->BOOL, [l] is a list of ALPHA. Returns a list of ALPHA.
   lto_remove1(fn,l,nil);

procedure lto_remove1(fn,l,xarl);
   % Remove elements from a list. [fn] is a function with
   % length([xarl])+1 arguments , [l] and [xarl] are LIST. Returns a
   % LIST.
   for each a in l join if not apply(fn,a . xarl) then {a};

procedure sf_rmconst(fl);
   % Remove constant. [fl] is a list of SF.
%   lto_select('(lambda (f) not domainp f),fl);
   for each f in fl join if not domainp f then {f};

%%% --- Access via canonical notation --- %%%

%%% --- Advanced programming techniques --- %%%

procedure foldr(fn,e,l);
   % Fold right. [fn] is a binary function of type $(a,a)->a$, [e] is
   % neutral and of type $a$, and [l] is a list of arguments, all of
   % type $a$. Returns a value of type $a$.
   % Example: foldr(function union,{},{{1,2},{2,3},{3,4}});
   if null l then e else apply(fn,{car l,foldr(fn,e,cdr l)});

procedure foldr1(fn,l);
   % Fold right with non-trivial list. Arguments as in foldr, exept l
   % is not nil. Return value as in foldr.
   % foldr1(function(lambda a,b;a+b),{1,2,3,4});
   if null cdr l then car l else apply(fn,{car l,foldr1(fn,cdr l)});

% MAP. Caveat: map(l,fn) applies fn to each cdr of l, which is
% unusual. mapc(l,fn) is what is usually known as map: fn is applied
% to each element of l. Example: mapc({1,2,3,4},function(print));
% Why does this not work: mapc({1,2,3,4},function(lambda x;x+1));

procedure mymap(fn,l);
   % map. [fn] in a function of type $a->b$, [l] is a list of type
   % $a$. Returns a list of type $b$.
   % Example: mymap(function(lambda n;n+1),{1,2,3,4});
   for each a in l collect apply(fn,{a});


%%% --- Datatype MTX (matrices) --- %%%
% a matrix is represented as a list of lines.

procedure mtx_0(m,n);
   % Zero matrix. [m] and [n] are INT. Returns a mxn-matrix MTX(SF).
   for l:=1:m collect
      for c:=1:n collect nil;

procedure mtx_1(n);
   % Unit matrix. [m] and [n] are INT. Returns a mxn-matrix MTX(SF).
   for l:=1:n collect
      for c:=1:n collect if c eq l then 1 else nil;

procedure mtx_froml(lst,n);
   % Make from list. [lst] is a LIST, [n] is an INT. Returns a MTX with
   % $n$ columns. Can be further improved.
   begin scalar mtx,m;
      m := length(lst)/n;
      if m*n neq length(lst) then error(nil,"mtx_froml: wrong list length");
      mtx := mtx_0(m,n);
      for l := 1 : m do
	 for c := 1 : n do
	    mtx_put(mtx,l,c,nth(lst,(l-1)*n+c));
	    %print((l-1)*n+c);
      return mtx;
   end;

procedure mtx_tol(mtx);
   % Matrix to list (destructive).
   for each l in mtx join l;

procedure mtx_nol(mtx);
   % Number of lines. [mtx] is a MTX. Returns an INT.
   length mtx;

procedure mtx_noc(mtx);
   % Number of lines. [mtx] is a MTX. Returns an INT.
   if null mtx then 0 else length car mtx;

procedure mtx_get(mtx,l,c);
   % Get matrix entry.
   nth(nth(mtx,l),c);

procedure mtx_put(mtx,l,c,a);
   % Put entry.
   nth(nth(mtx,l),c) := a;

procedure mtx_print(mtx);
   % Print.
   for each l in mtx do print l;

procedure lto_rmpos(lst,posl);
   % Remove positions. [lst] is a LIST, [posl] is a LIST(INT). Returns a LIST.
   begin scalar pos;
      pos := 0;
      return for each a in lst join <<
	 pos := pos+1;
	 if not memq(pos,posl) then {a}
      >>
   end;

procedure mtx_rmlscs(mtx,lines,columns);
   % Matrix remove lines and columns. [mtx] is a MTX, [lines] and
   % [columns] are LIST(INT). Returns a MTX.
   for each l in lto_rmpos(mtx,lines) collect lto_rmpos(l,columns);

procedure sf_coeffs(f,x);
   % List of all coefficients, even those that are zero. .Returns a
   % LIST(SF) of length max(0,degree(f,x).
   if domainp f or mvar f neq x then {f} else coeffs f;

procedure mtx_sylvester(f,g,x);
   % Sylvester matrix. [f], [g] are non-zero SF, [x] is an ID. Returns a MTX
   % (m+n lines and colums if m is degree of f in x and n is degree of
   % g in x).
   begin scalar m,n,syl,cf,cg;
      m := sf_deg(f,x);
      n := sf_deg(g,x);
      if m+n eq 0 then return mtx_0(0,0) else syl:= mtx_0(m+n,m+n);
      cf := sf_coeffs(f,x);
      cg := sf_coeffs(g,x);
      for l := 1 : n do
	 for c := l : l+m do
  	    mtx_put(syl,l,c,nth(cf,1+(c-l)));
      for l := n+1 : m+n do
	 for c := l-n : (l-n)+n do
  	    mtx_put(syl,l,c,nth(cg,1+(c-(l-n))));
      return syl;
   end;

procedure mtx_det(mtx); ofsf_det mtx;

procedure mtx_resultant(f,g,x);
   if null f or null g then
      0
   else if sf_deg(f,x)+sf_deg(g,x) eq 0 then
      1
   else
      mtx_det mtx_sylvester(f,g,x);

procedure mtx_mmji(f,g,x,j,i);
   % Modified Sylvester matrix Mji.
   begin scalar m,n,ltd1,ltd2,ctd1,ctd2; % ltd: lines to delete, ctd: columns to del.
      m := sf_deg(f,x);
      n := sf_deg(g,x);
      ltd1 := for k := (m+n)-j+1 : m+n collect k;
      ltd2 := for k := n-j+1 : n collect k;
      ctd1 := for k := (m+n-i-j)+1 : m+n collect k;
      ctd2 := for k := (m+n)-(2*j+1)+1 : (m+n-i-j)-1 collect k;
      return mtx_rmlscs(mtx_sylvester(f,g,x),union(ltd1,ltd2),union(ctd1,ctd2))
   end;

symbolic operator rlpsc;
procedure rlpsc(f,g,x,j);
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := prepf sf_psc(numr simp f,numr simp g,x,j);
      setkorder oldorder;
      return w
   end;

procedure sf_psc(f,g,x,j);
   % Principal subresultant coefficient. . Returns a SF, the [j]-th
   % psc of [f] and [g].
   mtx_det(mtx_mmji(f,g,x,j,j));

procedure sf_expt(k,n);
   % Raise a kernel to an exponent.
   mksp!*(numr simp k,n);

procedure sf_subresultant(f,g,x,j);
   % Subresultant.
   begin scalar summed;
      for i := 0 : j do
	 summed := addf(multf(mtx_det(mtx_mmji(f,g,x,j,i)),sf_expt(x,i)),summed);
      return summed
   end;

procedure sf_factorize(f);
   % Factorize. [f] is a SF. Returns a PAIR(DOM,LIST(PAIR(SF,INT))).
   fctrf f;

procedure sf_factors(f);
   % Factorize. [f] is a SF. Returns a LIST(SF).
   for each a in cdr sf_factorize f collect car a;

%%% --- module pair --- %%%
% pair of sets.

procedure pairunion(p1,p2);
   union(car p1,car p2) . union (cdr p1,cdr p2);

%%% --- module tag --- %%%

% implements the datatype TAG(alpha). such an element consists of a
% pair of an object (of type alpha) and a set of tags.

procedure tag_(a,l);
   % Tag item for the first time. [a] is ANY, [l] is a list. Returns a
   % TAG(ANY).
   a . list2set l;

procedure tag_object(te);
   % Tag list of an tagged item. [te] is TAG(ALPHA). Returns an ALPHA,
   % the object without tags.
   car te;

procedure tag_taglist(te);
   % Tag list of an tagged item. [te] is TAG. Retruns a TAG(ANY).
   cdr te;

procedure tag_add(te,a);
   % Add a tag to a tagged object. [te] is TAG, a is anything. Returns
   % a TAG.
   if member(a, tag_taglist te) then te
   else  tag_object te . (a . tag_taglist te);

% - tagged versions of common procedures - %

procedure tgdomainp(tf);
   % Domain predicate, tagged version. [tf] is a TAG(SF). Returns a
   % BOOL.
   domainp tag_object tf;

procedure sf_tgdeg(tf,x);
   % Tagged standard form degree. [tf] is a TAG(SF), [x]
   % is a kernel. Returns an INT.
   sf_deg(tag_object tf,x);

procedure sf_tglc(tf,x);
   % Tagged standard form leading coefficient. [tf] is a TAG(SF), [x]
   % is a kernel. Returns an INT.
   tag_(sf_lc(tag_object tf,x),tag_taglist tf);

procedure sf_tgred(tf,x);
   % Tagged standard form reductum. [t] is a TAG(SF), [x] is a kernel.
   % Returns a TAG(SF).
   tag_(sf_red(tag_object tf,x),tag_taglist tf);

procedure sf_tgdiscriminant(tf,x);
   % Tagged standard form discriminant. [tf] is a TAG(SF), [x] is a
   % kernel. Returns a TAG(SF).
   tag_(sf_discriminant(tag_object tf,x),tag_taglist tf);

procedure tgresultant(tf1,tf2,x);
   % Tagged standard form resultant. [tf1], [tf2] are TAG(SF), [x] is a
   % kernel. Returns a TAG(SF).
   tag_(resultant(tag_object tf1,tag_object tf2,x),
      union(tag_taglist tf1,tag_taglist tf2));

procedure tgunion(st1,st2);
   % Union of tagged expressions. [st1], [st2] are sets of tagged
   % expressions. Returns a set of tagged expressions.
   << if st1 then for each t1 in st1 do st2 := tgunion1(t1,st2); st2 >>;

procedure tgunion1(te,ste);
   % Union of tagged expressions subroutine. [te] is TAG, [set] is
   % SET(TAG). REturns SET(TAG).
   if null ste then
      {te}
   else if tag_object te = tag_object car ste then
      tag_(tag_object te,union(tag_taglist te,tag_taglist car ste)) . cdr ste
   else
      car ste . tgunion1(te,cdr ste);

procedure tglist2set(lte);
   % List to set for tagged expressions. [lte] is LIST(TAG). Returns
   % SET(TAG) s.t. no object occurs twice.
   tgunion(lte,{});

%%% --- Projection set and phase --- %%%

procedure rlprojamat2(fn,afl,l);
   % Algebraic mode access template 2. [fn] is a function of type
   % (LIST(SF),LIST(ID))->(LIST(SF), [afl] is a list of algebraic forms
   % (lisp prefix) and [l] is an list of identifiers. Returns a list of
   % algebraic forms.
   begin scalar oldorder,w;
      oldorder := setkorder reverse cdr l;
      w := apply(fn,{for each af in cdr afl collect numr simp af,cdr l});
      w := 'list . for each f in w collect prepf f;
      setkorder(oldorder);
      return w
   end;

procedure rltgprojamat2(fn,afl,l);
   % Algebraic mode access template 2, tagged version. [fn] is a
   % function of type (LIST(SF),LIST(ID))->(LIST(TAG(SF)), [afl] is a
   % list of algebraic forms (lisp prefix) and [l] is an list of
   % identifiers. Returns a list of lists, each list having an
   % algebraic form as first entry.
   begin scalar oldorder,w;
      oldorder := setkorder reverse cdr l;
      w := apply(fn,{for each af in cdr afl collect numr simp af,cdr l});
      w := 'list . for each tf in w collect
	 ('list . prepf tag_object tf . tag_taglist tf);
      setkorder(oldorder);
      return w
   end;

symbolic operator rlprojsetco;
procedure rlprojsetco(afl,l); rlprojamat2(function(ofsf_projsetco),afl,l);

procedure ofsf_projsetco(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopco,aa,varl);

symbolic operator rlprojsetcov22;
procedure rlprojsetcov22(afl,l); rlprojamat2(function(ofsf_projsetcov22),afl,l);

procedure ofsf_projsetcov22(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopcov22,aa,varl);

symbolic operator rlprojsetcov23;
procedure rlprojsetcov23(afl,l); rlprojamat2(function(ofsf_projsetcov23),afl,l);

procedure ofsf_projsetcov23(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopcov23,aa,varl);

symbolic operator rlprojsetcov33;
procedure rlprojsetcov33(afl,l); rlprojamat2(function(ofsf_projsetcov33),afl,l);

procedure ofsf_projsetcov33(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopcov33,aa,varl);

symbolic operator rlprojsetcoho;
procedure rlprojsetcoho(afl,l); rlprojamat2(function(ofsf_projsetcoho),afl,l);

procedure ofsf_projsetcoho(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopcoho,aa,varl);

%symbolic operator rlprojsetcohogen;
%procedure rlprojsetcohogen(afl,l); rlprojamat2(function(ofsf_projsetcohogen),afl,l);

procedure ofsf_projsetcohogen(aa,varl,theo);
   ofsf_genprojset('ofsf_transfac,'ofsf_projopcohogen,aa,varl,theo);

symbolic operator rlprojsetcolg;
procedure rlprojsetcolg(afl,l); rlprojamat2(function(ofsf_projsetcolg),afl,l);

procedure ofsf_projsetcolg(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopcolg,aa,varl);

symbolic operator rlprojsetmc;
procedure rlprojsetmc(afl,l); rlprojamat2(function(ofsf_projsetmc),afl,l);

procedure ofsf_projsetmc(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopmc,aa,varl);

symbolic operator rlprojsetmcbr;
procedure rlprojsetmcbr(afl,l); rlprojamat2(function(ofsf_projsetmcbr),afl,l);

procedure ofsf_projsetmcbr(aa,varl);
   ofsf_projset('ofsf_transfac,'ofsf_projopmcbr,aa,varl);

symbolic operator rltgprojsetmcbr;
procedure rltgprojsetmcbr(afl,l); rltgprojamat2(function(ofsf_tgprojsetmcbr),afl,l);

procedure ofsf_tgprojsetmcbr(aa,varl);
   % .[aa] is a LIST(TAG(SF)). Returns a LIST(TAG(SF)).
   ofsf_tgprojset('ofsf_tgtransfac,'ofsf_tgprojopmcbr,aa,varl);

procedure ofsf_projset(transfn,projopfn,aa,varl);
   % Projection set (as of Brown). [transfn] is a transformation on
   % the projection set. [projopfn] is a combined projection operator,
   % [aa] is the list of input polynomials and [varl] the list of
   % variables. Returns a list of SF.
   ofsf_projset1(transfn,projopfn,aa,varl,'ofsf_polyoflevel,'union);

procedure ofsf_tgprojset(tgtransfn,tgprojopfn,aa,varl);
   % Tagged projection set (as of Brown). [tgtransfn] is a tagged
   % transformation on the projection set, [tgprojopfn] is a tagged
   % combined projection operator, [aa] is the list of input
   % polynomials and [varl] the list of variables. Returns a
   % LIST(tag(SF)).
   ofsf_projset1(tgtransfn,tgprojopfn,
      for each a in aa collect tag_(a,{'input}),varl,
      'ofsf_tgpolyoflevel,'tgunion);

procedure ofsf_projset1(transfn,projopfn,aa,varl,polyoflevelfn,unionfn);
   begin scalar r,pp,ppj;
      r := length varl;
      pp := apply(transfn,{aa,nth(varl,r)});
      for j := r step -1 until 2 do <<
	 if ofsf_cadverbosep() then
	    ioto_tprin2t{"+ Projection  F",j," -> F",j-1,
	       ", variable: ",nth(varl,j),", variables left: ",j-1,
	       ", #P=",length pp};
	 ppj := apply(polyoflevelfn,{pp,varl,j});
	 pp := apply(unionfn,
	    {pp,apply(transfn,{apply(projopfn,{ppj,varl,j}),nth(varl,j-1)})});
      >>;
      return pp
   end;

%('ofsf_transfac,'ofsf_projopcohogen,aa,varl,theo);
procedure ofsf_genprojset(transfn,projopfn,aa,varl,theo);
   begin scalar r,pp,ppj,pp_theo,polyoflevelfn;
      polyoflevelfn := 'ofsf_polyoflevel;
      r := length varl;
      pp := apply(transfn,{aa,nth(varl,r)});
      for j := r step -1 until 2 do <<
	 if ofsf_cadverbosep() then
	    ioto_tprin2t{"+ genProjection  F",j," -> F",j-1,
	       ", variable: ",nth(varl,j),", variables left: ",j-1,
	       ", #P=",length pp};
	 ppj := apply(polyoflevelfn,{pp,varl,j});
	 %pp := apply(unionfn,
	 %   {pp,apply(transfn,{apply(projopfn,{ppj,varl,j}),nth(varl,j-1)})});
	 pp_theo := apply(projopfn,{ppj,varl,j,theo});
	 pp := union(apply(transfn,{car pp_theo,nth(varl,j-1)}),pp);
	 theo := union(cdr pp_theo,theo);
      >>;
      return pp . theo
   end;

procedure ofsf_polyoflevel(pp,varl,j);
   % Polynomials of level j. . Returns a list of SF.
   lto_select1(function(lambda p,varl,j;sf_deg(p,nth(varl,j))>0),pp,{varl,j});

procedure ofsf_tgpolyoflevel(pp,varl,j);
   % Tagged polynomials of level j. . Returns a list of TAG(SF).
   lto_select1(function(lambda p,varl,j;sf_tgdeg(p,nth(varl,j))>0),pp,{varl,j});

%%% --- Transformations on projection sets --- %%%
% PAIR(LIST(SF),ID)->LIST(SF)

procedure ofsf_transid(pp,x);
   % Identity transformation. [pp] is a LIST(SF), [x] is an ID.
   % Returns a LIST(SF).
   pp;

symbolic operator rltransfac;
procedure rltransfac(afl,x); rlprojamat(function(ofsf_transfac),afl,x);

procedure ofsf_transfac(pp,x);
   % Factorization transformation. [pp] is a LIST(SF), [x] is an ID.
   % Returns a LIST(SF).
   list2set for each p in pp join sf_factors p;

procedure ofsf_tgtransfac(tgpp,x);
   % Factorization transformation. [tgpp] is a LIST(TAG(SF)), [x] is an ID.
   % Returns a SET(TAG(SF)). %%% more efficient: successive tgunion
   tglist2set for each tgp in tgpp join
      for each f in sf_factors tag_object tgp collect
	 tag_(f,tag_taglist tgp);

%%% --- Combined projection operators --- %%%

procedure ofsf_projopco(aa,varl,j);
   % Combined Collins' projection operator.
   if j eq 2 then ofsf_projco2v(aa,nth(varl,j)) else ofsf_projco(aa,nth(varl,j));

procedure ofsf_projopcov22(aa,varl,j);
   % Combined Collins' projection operator.
   if j eq 2 then ofsf_projco2v(aa,nth(varl,j)) else ofsf_projcov22(aa,nth(varl,j));

procedure ofsf_projopcov23(aa,varl,j);
   % Combined Collins' projection operator, version 2-3.
   if j eq 2 then ofsf_projco2v(aa,nth(varl,j)) else ofsf_projcov23(aa,nth(varl,j));

procedure ofsf_projopcov33(aa,varl,j);
   % Combined Collins' projection operator, version 3-3.
   if j eq 2 then
      ofsf_projco2v(aa,nth(varl,j))
   else
      ofsf_projcov33(aa,lto_take(varl,j));

procedure lto_take(l,n);
   % Take the first n elements of l. [l] is a LIST, [n] is a natural
   % number. Returns a LIST.
   if l and n>0 then car l . lto_take(cdr l,n-1);

procedure lto_drop(l,n);
   % Drop the first n elements of l. [l] is a LIST, [n] is a natural
   % number. Returns a LIST.
   if l and n>0 then lto_drop(cdr l,n-1) else l;

procedure lto_init(l);
   % Initial part of a list, with the last element removed, error if l
   % is empty.
   if cdr l then car l . lto_init cdr l;

procedure ofsf_projopcoho(aa,varl,j);
   % Combined Collins' projection operator with Hong's improvement
   % (based on Collins version 3).
   if j eq 2 then ofsf_projco2v(aa,nth(varl,j)) else
      if j eq 3 and !*rlcadmc3 then ofsf_projmc(aa,nth(varl,j)) else
      	 ofsf_projcoho(aa,nth(varl,j));

procedure ofsf_projopcohogen(aa,varl,j,theo);
   % Combined Collins' projection operator with Hong's improvement
   % (based on Collins version 3).
   if j eq 2 then ofsf_projco2v(aa,nth(varl,j)) . theo else
      if j eq 3 and !*rlcadmc3 then ofsf_projmcgen(aa,nth(varl,j),theo) else
      	 ofsf_projcohogen(aa,nth(varl,j),theo);

% ofsf_projopcolg: legacy operator defined above

procedure ofsf_projopmc(aa,varl,j);
   % Combined McCallum's projection operator.
   ofsf_projmc(aa,nth(varl,j));

procedure ofsf_projopmcbr(aa,varl,j);
   % Combined Brown's improvement to McCallum's projection operator.
   ofsf_projmcbr(aa,nth(varl,j));

procedure ofsf_tgprojopmcbr(aa,varl,j);
   % Combined tagged Brown's improvement to McCallum's projection operator.
   ofsf_tgprojmcbr(aa,nth(varl,j));

%%% --- Projection operators --- %%%
% PAIR(LIST(SF),ID)->LIST(SF)

procedure notdomainp(f); not domainp(f);

symbolic operator rlprojco;
procedure rlprojco(afl,x); rlprojamat(function(ofsf_projco),afl,x);

procedure ofsf_projco(aa,x);
   % Collin's projection operator, simplest version.
   begin scalar bb,ll,ss1,ss2;
      bb := ofsf_projcobb(aa,x);
      ll := ofsf_projcoll(bb,x);
      ss1 := ofsf_projcoss1(bb,x);
      ss2 := ofsf_projcoss2(bb,x);
      return list2set lto_select('notdomainp,union(union(ll,ss1),ss2))
   end;

symbolic operator rlprojcov22;
procedure rlprojcov22(afl,x); rlprojamat(function(ofsf_projcov22),afl,x);

procedure ofsf_projcov22(aa,x);
   % Collin's projection operator, B and S1 version 2.
   begin scalar bb,ll,ss1,ss2;
      bb := ofsf_projcobbv2(aa,x);
      ll := ofsf_projcoll(bb,x);
      ss1 := ofsf_projcoss1(bb,x);
      ss2 := ofsf_projcoss2v2(bb,x);
      return list2set lto_select('notdomainp,union(union(ll,ss1),ss2))
   end;

symbolic operator rlprojcov23;
procedure rlprojcov23(afl,x); rlprojamat(function(ofsf_projcov23),afl,x);

procedure ofsf_projcov23(aa,x);
   % Collin's projection operator, version 3. . Returns a SET(SF) with
   % non-domain elements. Remark: union does not require the first
   % argument to be a set.
   begin scalar bb,ll,ss1,ss2;
      bb := ofsf_projcobbv2(aa,x);
      ll := ofsf_projcoll(bb,x);
      ss1 := ofsf_projcoss1(bb,x);
      ss2 := list2set ofsf_projcoss2v3(bb,x);
      return lto_select('notdomainp,union(union(ll,ss1),ss2))
   end;

symbolic operator rlprojcov33;
procedure rlprojcov33(afl,l); rlprojamat2(function(ofsf_projcov33),afl,l);

procedure ofsf_projcov33(aa,l);
   % Collin's projection operator, version 3. . Returns a SET(SF) with
   % non-domain elements.
   begin scalar bb,ll,ss1,ss2,x;
      bb := ofsf_projcobbv3(aa,l);
      x := lto_last l;
      ll := ofsf_projcoll(bb,x);
      ss1 := ofsf_projcoss1(bb,x);
      ss2 := list2set ofsf_projcoss2v3(bb,x);
      return lto_select('notdomainp,union(union(ll,ss1),ss2))
   end;

procedure lto_last(l);
   % Last element of a list. [l] is a non-empty list.
   if cdr l then lto_last cdr l else car l;

symbolic operator rlprojcoho;
procedure rlprojcoho(afl,x); rlprojamat(function(ofsf_projcoho),afl,x);

procedure ofsf_projcoho(aa,x);
   % Collin's projection operator with Hong's improvement to S2. .
   % Returns a SET(SF) with non-domain elements. Remark: union does
   % not require the first argument to be a set.
   begin scalar bb,ll,ss1,ss2;
      bb := ofsf_projcobbv2(aa,x);
      bb := ofsf_defpdel bb;
      ll := ofsf_projcoll(bb,x);
      ll := ofsf_defpdel ll;
      ss1 := ofsf_projcoss1(bb,x);
      ss1 := ofsf_defpdel ss1;
      ss2 := list2set ofsf_projhoss2(bb,x);
      ss2 := ofsf_defpdel ss2;
      return lto_select('notdomainp,union(union(ll,ss1),ss2))
   end;

procedure ofsf_defpdel(l);
   % Definite predicate deletion. [l] is a list of SFs. Returns a list
   % of SFs. Delete all elements from [l] for which surep can verify
   % definiteness.
      for each f in l join
	 if not ofsf_surep(ofsf_0mk2('neq,f),nil) then
	    {f}
	 else if !*rlverbose then <<
	    ioto_prin2 "*";
	    nil
	 >>;

%symbolic operator rlprojcohogen;
%procedure rlprojcohogen(afl,x); rlprojamat(function(ofsf_projcohogen),afl,x);

procedure ofsf_projcohogen(aa,x,theo);
   % Collin's projection operator with Hong's improvement to S2,
   % generic version. . Returns a SET(SF) with non-domain elements.
   % Remark: union does not require the first argument to be a set.
   begin scalar bb_theo,bb,ll,ss1_theo,ss1,ss2_theo,ss2;
      % Redukta
      bb_theo := ofsf_projcobbv2gen(aa,x,theo);
      bb := car bb_theo;
      theo := cdr bb_theo;
      % Leading coefficients
      ll := ofsf_projcoll(bb,x);
      % S1
      ss1_theo := ofsf_projcoss1gen(bb,x,theo);
      ss1 := car ss1_theo;
      theo := cdr ss1_theo;
      % S2
      ss2_theo := ofsf_projhoss2gen(bb,x,theo);
      ss2 := list2set car ss2_theo;
      theo := cdr ss2_theo;
      return lto_select('notdomainp,union(union(ll,ss1),ss2)) . theo
      %return lto_select(function(lambda p;notdomainp car p),
%	 pairunion(pairunion(ll . nil,ss1_theo),ss2_theo))
   end;

symbolic operator rlprojco2v;
procedure rlprojco2v(afl,x); rlprojamat(function(ofsf_projco2v),afl,x);

procedure ofsf_projco2v(aa,x);
   % Collins' projection operator for two variable case. Returns a
   % LIST(SF) without domain elements.
   begin scalar ll,dd,rr,jj1;
      if ofsf_cadverbosep() then ioto_prin2 "[projco2v ";
      ll := ofsf_projcoll(aa,x);
      dd := for each a in aa join if sf_deg(a,x)>=2 then {sf_discriminant(a,x)};
      rr := for each a1 on aa join for each a2 in cdr aa collect
	 resultant(car a1,a2,x);
      jj1 := list2set lto_remove('domainp,union(union(ll,dd),rr));
      if ofsf_cadverbosep() then ioto_prin2 {length jj1,"]"};
      return jj1
   end;

symbolic operator rlprojmc;
procedure rlprojmc(afl,x); rlprojamat(function(ofsf_projmc),afl,x);

procedure ofsf_projmc(aa,x);
   % McCallum's projection operator for squarefree basis. . Returns a
   % LIST(SF).
   begin scalar ll,dd,rr;
      %ll := ofsf_projmcll(aa,x);
      ll := ofsf_projcoll(ofsf_projcobbv2(aa,x),x);
      dd := for each a in aa collect sf_discriminant(a,x);
      rr := for each a1 on aa join for each a2 in cdr aa collect
	 resultant(car a1,a2,x);
      return list2set lto_remove('domainp,union(union(ll,dd),rr))
   end;

procedure ofsf_projmcgen(aa,x,theo);
   % McCallum's projection operator for squarefree basis, generic
   % version. . Returns a LIST(SF).
   begin scalar bb_theo,ll,dd,rr;
      %ll := ofsf_projmcll(aa,x);
      bb_theo := ofsf_projcobbv2gen(aa,x,theo);
      ll := ofsf_projcoll(car bb_theo,x);
      dd := for each a in aa collect sf_discriminant(a,x);
      rr := for each a1 on aa join for each a2 in cdr aa collect
	 resultant(car a1,a2,x);
      return list2set lto_remove('domainp,union(union(ll,dd),rr)) . cdr bb_theo
   end;

%symbolic operator rltgprojmc;
%procedure rltgprojmc(afl,x); rlprojamat(function(ofsf_tgprojmc),afl,x);

procedure ofsf_tgprojmc(tgaa,x);
   % McCallum's projection operator for squarefree basis, tagged
   % version. [tgaa] is a LIST(TAG(SF)). Returns a LIST(TAG(SF)).
   begin scalar aa,tgll,tgdd,tgrr;
      % strip off all the tags
      aa := for each te in tgaa join
	 if not domainp tag_object te then {tag_object te};
      % tag the leading coefficients
      tgll := for each f in ofsf_projmcll(aa,x) collect
	 tag_(sf_lc(f,x),{'lc});
      % tag the discriminants
      tgdd := for each a in aa collect tag_(sf_discriminant(a,x),{'dis});
      % tag the resultants
      tgrr := for each a1 on aa join for each a2 in cdr aa collect
	 tag_(resultant(car a1,a2,x),{'res});
      return lto_remove('tgdomainp,
	 tgunion(tgll,tgunion(tgdd,tglist2set tgrr)) )
   end;

symbolic operator rlprojmcbr;
procedure rlprojmcbr(afl,x); rlprojamat(function(ofsf_projmcbr),afl,x);

procedure ofsf_projmcbr(aa,x);
   % Brown's improvement to McCallum's projection operator for
   % squarefree basis. . Returns a LIST(SF).
   begin scalar bb,ll,dd,rr;
      bb := lto_remove('domainp,aa);
      ll := for each f in bb collect sf_lc(f,x);
      dd := for each a in bb collect sf_discriminant(a,x);
      rr := for each a1 on bb join for each a2 in cdr bb collect
	 resultant(car a1,a2,x);
      return list2set lto_remove('domainp,union(union(ll,dd),rr))
   end;

%symbolic operator rltgprojmcbr;
%procedure rltgprojmcbr(afl,x); rlprojamat(function(ofsf_tgprojmcbr),afl,x);

procedure ofsf_tgprojmcbr(tgaa,x);
   % Brown's improvement to McCallum's projection operator for
   % squarefree basis, tagged version. [tgaa] is a LIST(TAG(SF)).
   % Returns a LIST(TAG(SF)).
   begin scalar bb,tgll,tgdd,tgrr;
      %      bb := lto_remove('tgdomainp,tgaa);
      % strip off all the tags
      bb := for each te in tgaa join
	 if not domainp tag_object te then {tag_object te};
      % tag the leading coefficients
      tgll := for each f in bb collect tag_(sf_lc(f,x),{'lc});
      % tag the discriminants
      tgdd := for each a in bb collect tag_(sf_discriminant(a,x),{'dis});
      % tag the resultants
      tgrr := for each a1 on bb join for each a2 in cdr bb collect
	 tag_(resultant(car a1,a2,x),{'res});
      return lto_remove('tgdomainp,
	 tgunion(tgll,tgunion(tgdd,tglist2set tgrr)) )
   end;

%%% --- Projection subsets --- %%%
% PAIR(LIST(SF),ID)->LIST(SF)

procedure rlprojamat(fn,afl,x);
   % Algebraic mode access template. [fn] is a function of type
   % (LIST(SF),ID)->(LIST(SF), [afl] is a list of algebraic forms
   % (lisp prefix) and [x] is an identifier. Returns a list of
   % algebraic forms.
   begin scalar oldorder,w;
      oldorder := setkorder {x};
      w := apply(fn,{for each af in cdr afl collect numr simp af,x});
      w := 'list . for each f in w collect prepf f;
      setkorder(oldorder);
      return w
   end;

% - Collins -%

symbolic operator rlprojcobb;
procedure rlprojcobb(afl,x); rlprojamat(function(ofsf_projcobb),afl,x);

procedure ofsf_projcobb(aa,x);
   % Collins' projection set of redukta R (straightforward version).
   % [aa] is a list of SF, x is an identifier. Returns a set of SF.
   % Note that the output is compliant with ofsf_projcoss1v3.
   for each f in aa join ofsf_projcobb1(f,x);

procedure ofsf_projcobb1(f,x);
   % Collins' redukta (straightworward) subroutine R1. [f] is a SF,
   % [x] is an identifier. Returns a list of SF with positive total
   % degree, the list is ordered such that the degrees are descending.
   begin scalar redl;
      redl := nil;
      %while sf_deg(f,x)>=1 do << redl := f . redl; f := red(f) >>;
      while not domainp f do << redl := f . redl; f := sf_red(f,x) >>;
      return reversip redl
   end;

symbolic operator rlprojcobbv2;
procedure rlprojcobbv2(afl,x); rlprojamat(function(ofsf_projcobbv2),afl,x);

procedure ofsf_projcobbv2(aa,x);
   % Collins' projection set of redukta Rv2 (version 2). [aa] is a
   % list of SF, x is an identifier. Returns a set of SF. Note that
   % the output is compliant with ofsf_projcoss1v3. (list2set
   % possible?)
   begin scalar bb;
      if ofsf_cadverbosep() then ioto_prin2 "(Bv2: ";
      bb := for each f in aa join ofsf_projcobb1v2(f,x);
      if ofsf_cadverbosep() then ioto_prin2 {length bb,") "};
      return bb;
   end;


procedure ofsf_projcobb1v2(f,x);
   % Collins' redukta (version 2) subroutine R1v2. [f] is a
   % SF, [x] is an identifier. Returns a list of SF with positive
   % degree in [x], the list is ordered such that the degrees are
   % descending; furthermore, the first reduktum with domain
   % coefficient, is the last entry in the list.
   begin scalar rr1,rr1p;
      rr1 := ofsf_projcobb1(f,x);
      if null rr1 then return rr1;
      repeat <<
	 rr1p := car rr1 . rr1p;
	 rr1 := cdr rr1;
      >> until null rr1 or domainp sf_lc(car rr1p,x); % positive degree required
      return reversip rr1p;
   end;

symbolic operator rlprojcobbv3;
procedure rlprojcobbv3(afl,l); rlprojamat2(function(ofsf_projcobbv3),afl,l);

procedure ofsf_projcobbv3(aa,l);
   for each f in aa join ofsf_projcobb1v3(f,l);

procedure ofsf_projcobb1v3(f,varl);
   % Collins' redukta (version 2) subroutine R1v3. [f] is a SF, [varl]
   % is a list of identifiers. Returns a list of SF with positive
   % degree in [x], the list is ordered such that the degrees are
   % descending;
   begin scalar rr2,rr2p;
      rr2 := ofsf_projcobb1v2(f,nth(varl,length varl));
      if null rr2 then return rr2;
      repeat <<
	 rr2p := car rr2 . rr2p;
	 rr2 := cdr rr2;
      >> until null rr2 or sfto_zerodimp(rr2p,varl);
      return reversip rr2p;
   end;

symbolic operator rlzerodimp;
procedure rlzerodimp(afl,l);
   % . [afl] is a list of algebraic forms (lisp prefix) and [l] is an
   % list of identifiers. Returns a natural number or the empty set {}.
   begin scalar oldorder,w;
      oldorder := setkorder reverse cdr l;
      w := apply(function(sfto_zerodimp1),
	 {for each af in cdr afl collect numr simp af});
      setkorder(oldorder);
      return if null w then '(list) else w
   end;

procedure sfto_zerodimp(l,varl);
   % Zero dimensional predicate. [l] is a list of SF, varl is a list
   % of IDs. Returns a natural number (an upper limit for the
   % dimension) or nil (if the ideal is not zero dimensional).
   begin scalar oldorder,res;
      oldorder := setkorder reverse varl;
      res := sfto_zerodimp1(l);
      setkorder oldorder;
      return res
   end;

procedure sfto_zerodimp1(l);
   % Zero dimensional predicate. [l] is a list of SF. Returns a
   % natural number (an upper bound for the dimension) or nil (if the
   % ideal is not zero dimensional).
   begin scalar svkord,oldtorder,basis,htl,minexpl;
      svkord := kord!*;
      oldtorder := cdr torder {'list . kord!*,'revgradlex}; % gb notwendig
      basis := sfto_groebnerf l;
      torder oldtorder;
      kord!* := svkord;
      htl := for each f in basis collect sfto_hterm f;
      minexpl := for each x in kord!* collect sfto_zerodimp2(x,htl);
      if memq(nil,minexpl) then return nil;
      return foldr1(function(lambda a,b;a*b),minexpl);
   end;

procedure sfto_zerodimp2(x,htl);
   % . x is an ID, [htl] is a list of SF (head terms). Returns a
   % natural number or nil.
   begin scalar expl;
      expl := nil;
      for each ht in htl do
	 if domainp ht then
	    expl := 0 . expl
	 else if (mvar ht eq x and domainp sf_lc(ht,x)) then
	    expl := sf_deg(ht,x) .expl;
      if null expl then return nil else
	 return foldr1(function(lambda a,b;if a<b then a else b),expl);
   end;

procedure sfto_hterm(f);
   % Highest term. f is a SF. Returns a SF.
   if domainp f then f
   else multf(sf_lc(f,mvar f),sf_expt(mvar f,sf_deg(f,mvar f)));

procedure ofsf_projcobbv2gen(aa,x,theo);
   % . . Returns as dotted pair a LIST(SF) and a theory.
   begin scalar bb,bb_theo;
      if ofsf_cadverbosep() then ioto_prin2 "(B2gen: ";
      bb := for each f in aa join <<
	 bb_theo := ofsf_projcobbv2gen1(f,x,theo);
	 theo := cdr bb_theo;
	 car bb_theo
      >>;
      if ofsf_cadverbosep() then ioto_prin2 {length bb,") "};
      return bb . theo;
   end;

procedure ofsf_projcobbv2gen1(f,x,theo);
   % generic reducta for 1 poly. .Returns as dotted pair a LIST(SF)
   % and a theory.
   begin scalar redl, finished;
      if domainp f then return nil;
      repeat <<
	 redl := f . redl; f := sf_red(f,x);
	 if domainp f then << % f can be nil here
	    if ofsf_cadverbosep() then ioto_prin2 "(end)";
	    finished := t
	 >> else if ofsf_surep(ofsf_0mk2('neq,sf_lc(car redl,x)),theo)
	 then <<
	    if ofsf_cadverbosep() then if domainp sf_lc(car redl,x) then
   	       ioto_prin2 "(dom)" else ioto_prin2 "(=>)";
	    finished := t
	 >> else if ofsf_cadvalassp(ofsf_cadbvl!*,sf_lc(car redl,x)) then <<
	    if ofsf_cadverbosep() then ioto_prin2 "(>th)";
	    theo := ofsf_0mk2('neq,sf_lc(car redl,x)) . theo;
	    finished := t
	 >>;
      >> until finished;
      return reversip redl . theo;
   end;

procedure ofsf_cadvalassp(bvl,sf);
   % Ordered field standard form valid assumption. [bvl] is a list of
   % variables; [sf] is a standard form. Returns [T] if an assumption
   % containing [sf] is valid. Depends on switch [!*rlqegenct].
   (!*rlqegenct or sfto_monfp sf) and null intersection(bvl,kernels sf);

symbolic operator rlprojcoll;
procedure rlprojcoll(afl,x); rlprojamat(function(ofsf_projcoll),afl,x);

procedure ofsf_projcoll(bb,x);
   % Collins' projection set of leading coefficients L(B). [bb] is a
   % list of SF, [x] is an identifier. Returns a list of SF.
   begin scalar ll;
      if ofsf_cadverbosep() then ioto_prin2 "(coL: ";
      %ll := for each f in bb join if sf_deg(f,x)>=1 then {lc(f)};
      ll := for each f in bb collect sf_lc(f,x);
      if ofsf_cadverbosep() then ioto_prin2 {length ll,") "};
      return ll;
   end;

symbolic operator rlprojmcll;
procedure rlprojmcll(afl,x); rlprojamat(function(ofsf_projmcll),afl,x);

procedure ofsf_projmcll(aa,x);
   % McCallum's projection set of leading coefficients L(A). [aa] is a
   % list of SF, [x] is an identifier. Returns a list of SF.
   for each f in aa join
      for each cd in sf_cdl(f,x) join
	 if not domainp car cd then {car cd};

procedure sf_cdl(f,x);
   % Coefficient and degree list. [f] is a SF, [x] is an ID. Retuns a
   % LIST(PAIR(SF,INT)).
   if sf_deg(f,x)>=1 then
      (lc f . ldeg f) . sf_cdl(red f,x)
   else
      {(f . 0)};

procedure sf_fromcdl(cdl,k);
   % Standard form from coefficient and degree list. [cdl] is a
   % non-empty LIST(PAIR(SF,INT)), x in an ID. Returns a SF.
   begin scalar f;
      if null cdr cdl then return caar cdl;
      f := sf_expt(k,cdar cdl);
      lc f := caar cdl;
      red f := sf_fromcdl(cdr cdl,k);
      return f
   end;

procedure sf_pscs(a,b,x);
   % All pscs. . Returns a list of SF.
   for k := 0 : min(sf_deg(a,x),sf_deg(b,x))-1 collect sf_psc(a,b,x,k);

procedure sf_pscsgen(a,b,x,theo);
   % All pscs, generic version. . Returns as a dotted pair a list of
   % SF and a theory.
   begin scalar k,pscl,finished;
      if not !*rlpscsgen then return sf_pscs(a,b,x) . theo;
      k := 0;
      if k>min(sf_deg(a,x),sf_deg(b,x))-1 then return nil . theo;
      repeat <<
	 pscl := sf_psc(a,b,x,k) . pscl; k := k+1;
	 if k>min(sf_deg(a,x),sf_deg(b,x))-1 then <<
	    if ofsf_cadverbosep() then ioto_prin2 "(end)"
	 >> else if ofsf_surep(ofsf_0mk2('neq,car pscl),theo) then <<
	    if ofsf_cadverbosep() then if domainp car pscl then
	       ioto_prin2 "(dom)" else ioto_prin2 "(=>)";
	    finished := t
	 >> else if ofsf_cadvalassp(ofsf_cadbvl!*,car pscl) then <<
	    if ofsf_cadverbosep() then ioto_prin2 "(>th)";
	    theo := ofsf_0mk2('neq,car pscl) . theo;
	    finished := t
	 >>;
      >> until finished or k>min(sf_deg(a,x),sf_deg(b,x))-1;
%      if ofsf_cadverbosep() then ioto_prin2 {" (- ",min(sf_deg(a,x),sf_deg(b,x))-k,") "};
      return pscl . theo;
   end;

procedure sf_diff(f,x);
   numr difff(f,x);

symbolic operator rlprojcoss1;
procedure rlprojcoss1(afl,x); rlprojamat(function(ofsf_projcoss1),afl,x);

procedure ofsf_projcoss1(bb,x);
   % Collins' projection set S1(A). [bb] is a list of SF, [x] is an
   % identifier. Returns a list of SF.
   begin scalar ss1;
      if ofsf_cadverbosep() then ioto_prin2 "(S1: ";
      ss1 := for each b in bb join sf_pscs(b,sf_diff(b,x),x);
      if ofsf_cadverbosep() then ioto_prin2 {length ss1,") "};
      return ss1;
   end;

%symbolic operator rlprojcoss1gen;
%procedure rlprojcoss1gen(afl,x); rlprojamat(function(ofsf_projcoss1gen),afl,x);

procedure ofsf_projcoss1gen(bb,x,theo);
   % Collins' projection set S1(A) generic version. [bb] is a list of
   % SF, [x] is an identifier. Returns a list of SF.
   begin scalar ss,pscs_theo;
      if ofsf_cadverbosep() then ioto_prin2 "(coS1gen: ";
      ss := for each b in bb join <<
	 pscs_theo := sf_pscsgen(b,sf_diff(b,x),x,theo);
	 theo := cdr pscs_theo ;
	 car pscs_theo
      >>;
      if ofsf_cadverbosep() then ioto_prin2 {length ss,") "};
      return ss . theo;
   end;

symbolic operator rlprojcoss2;
procedure rlprojcoss2(afl,x); rlprojamat(function(ofsf_projcoss2),afl,x);

procedure ofsf_projcoss2(bb,x);
   % Collins' projection set S1 simplest version. [bb] is a list of
   % SF, [x] is an identifier. Returns a list of SF.
   for each b1 in bb join for each b2 in cdr bb join sf_pscs(b1,b2,x);

symbolic operator rlprojcoss2v2;
procedure rlprojcoss2v2(afl,x); rlprojamat(function(ofsf_projcoss2v2),afl,x);

procedure ofsf_projcoss2v2(bb,x);
   % Collins' projection set version 2. [bb] is a list of SF, [x] is an
   % identifier. Returns a list of SF.
   for each b1 on bb join for each b2 in cdr bb join sf_pscs(car b1,b2,x);

symbolic operator rlprojcoss2v3;
procedure rlprojcoss2v3(afl,x); rlprojamat(function(ofsf_projcoss2v3),afl,x);

procedure ofsf_projcoss2v3(bb,x);
   % Collins' projection set S1 version 3. [bb] is a list of SF, [x]
   % is an identifier. Returns a list of SF. Note that for this to
   % work properly, the list bb of reducta has to look like
   % {f,red(f),red(red(f)),...,g,red(g),red(red(g)),...}
   begin scalar ss2,redll;
      % 1. break bb up into sets containing an imput poly and its reducta
      redll := ofsf_splitredl(bb,x);
      % 2.
      ss2 := for each ll on redll join for each l in cdr ll join
	 % car ll and l are lists of SF
	 for each b1 in car ll join for each b2 in l join sf_pscs(b1,b2,x);
      return ss2
   end;

procedure ofsf_splitredl(bb,x);
   % Split redukta list (into list of lists of redukta). . .
   begin scalar redl,redll;
      % 1. break bb up into sets containing an imput poly and its reducta
      while bb do <<
      	 redl := {car bb}; bb := cdr bb;
      	 while bb and sf_red(car redl,x) = car bb do << % eq is possible here
	    redl := car bb . redl; bb := cdr bb;
      	 >>;
      	 redll := reversip redl . redll
      >>;
      % function(lambda(x,y); length x > length y)
      redll := sort(redll,function(ofsf_splitredlordp));
      return redll
   end;

procedure ofsf_splitredlordp(l1,l2);
   % We know l1, l2 are non-empty and their cars contain the current
   % variable as mvar.
   begin scalar le1, le2, x, hit, res,d1,d2;
      le1 := length l1;
      le2 := length l2;
      if le1 > le2 then return t;
      if le1 < le2 then return nil;
      x := mvar car l1;
      while l1 and not hit do <<
	 d1 := sf_deg(car l1,x);
	 d2 := sf_deg(car l2,x);
	 l1 := cdr l1;
	 l2 := cdr l2;
      	 if d1 > d2 then
	    res := hit := t;
	 if d1 < d2 then <<
	    res := nil;
	    hit := t
	 >>
      >>;
      return res
   end;

symbolic operator rlprojhoss2;
procedure rlprojhoss2(afl,x); rlprojamat(function(ofsf_projhoss2),afl,x);

procedure ofsf_projhoss2(bb,x);
   % Hong's projection set S1 version 3. [bb] is a list of SF, [x]
   % is an identifier. Returns a list of SF. Note that for this to
   % work properly, the list bb of reducta has to look like
   % {f,red(f),red(red(f)),...,g,red(g),red(red(g)),...}
   begin scalar ss2,redll;
      if ofsf_cadverbosep() then ioto_prin2 "(hoS2: ";
      % 1. break bb up into sets containing an imput poly and its reducta
      redll := ofsf_splitredl(bb,x);
      % 2.
      if ofsf_cadverbosep() then ioto_prin2 {"[",length redll,"]"};
      ss2 := for each ll on redll join for each l in cdr ll join
	 % car ll and l are lists of SF
	 for each b2 in l join sf_pscs(caar ll,b2,x);
      if ofsf_cadverbosep() then ioto_prin2 {length ss2,") "};
      return ss2
   end;

procedure ofsf_projhoss2gen(bb,x,theo);
   % Hong's projection set S1 version 3 generic. [bb] is a list of SF,
   % [x] is an identifier. Returns a list of SF. Note that for this to
   % work properly, the list bb of reducta has to look like
   % {f,red(f),red(red(f)),...,g,red(g),red(red(g)),...}
   begin scalar ss2,redll,pscs_theo;
      % 1. break bb up into sets containing an imput poly and its reducta
      redll := ofsf_splitredl(bb,x);
      % 2.
      if ofsf_cadverbosep() then ioto_prin2 "(hoS2gen: ";
      ss2 := for each ll on redll join for each l in cdr ll join
	 % car ll and l are lists of SF
	 for each b2 in l join <<
	    pscs_theo := sf_pscsgen(caar ll,b2,x,theo);
	    theo := cdr pscs_theo ;
	    car pscs_theo
	 >>;
      if ofsf_cadverbosep() then ioto_prin2 {length ss2,") "};
      return ss2 . theo
   end;

symbolic operator show;
procedure show(a); print a;

endmodule;  % ofsfcadprojection

end;  % of file
