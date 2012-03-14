% ----------------------------------------------------------------------
% $Id: ofsfcad.red 385 2009-07-15 07:17:21Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2000-2009 A. Dolzmann, A. Seidl, and T. Sturm
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
   fluid '(ofsf_cad_rcsid!* ofsf_cad_copyright!*);
   ofsf_cad_rcsid!* :=
      "$Id: ofsfcad.red 385 2009-07-15 07:17:21Z thomas-sturm $";
   ofsf_cad_copyright!* := "(c) 2000-2009 A. Dolzmann, A. Seidl, T. Sturm"
>>;

module ofsfcad;

switch rlqegen1,rlcadmcproj,rlpscsgen,rlcadans,rlcadtv,rlcadtree2dot,rlcadrmwc;
fluid '(rlcadguessnoc!*);

off1 'rlqegen1;
off1 'rlcadmcproj;
on1 'rlpscsgen;
off1 'rlcadans;
on1 'rlcadtv;
off1 'rlcadtree2dot;
on1 'rlcadrmwc;

procedure ofsf_cadverbosep();
   % CAD verbose predicate.
   !*rlverbose and !*rlcadverbose;

procedure ofsf_gcad(phi,mkvarlres,aaplus);
   % Ordered field standard form generic cylindrical algebraic
   % decomposition. [phi] is a formula, [mkvarlres] is a projection
   % order, and [aaplus] is a list of SF. Returns a pair
   % $(\Theta,\phi')$, where $\Theta$ is a list of inequalities, and
   % $\phi'$ is a quantifier-free formula. We have
   % $\bigwedge\Theta\longrightarrow([phi]\longleftrightarrow\phi')$.
   begin scalar !*rlqegen;
      !*rlqegen := t;
      return ofsf_cad(phi,mkvarlres,aaplus);
   end;

fluid '(kord!*);

procedure ofsf_cad(phi,mkvarlres,aaplus);
   % Ordered field standard form cylindrical algebraic decomposition.
   % [f] is a formula, [n] is a non-negative integer. If [f] is
   % closed (and rlcadanuex is switched on) a quantifier-free formula
   % equivalent to [f] is returned, otherwise [f] is returned in
   % prenex normal form.
   begin scalar cd;
	 %ophi,phiprime,tmp,varl,qal,k,r,oldorder,ff,ffr,dd,psi,w;
      % Preparation phase. Kernel order is changed.
      cd := ofsf_cadpreparation(phi,mkvarlres,aaplus);
      if !*rlverbose then caddata_print1 cd;
      if !*rlcadpreponly then return ofsf_cadfinish cd;
      % Projection phase
      ofsf_cadprojection cd;
      if !*rlcadprojonly then <<
	 ioto_prin2t {"+ Theory: "};
	 for each x in caddata_theo cd do mathprint rl_prepfof x;
	 return ofsf_cadfinish cd
      >>;
      % Extension phase (build partial cad tree)
      ofsf_cadextension(cd);
      if !*rlcadextonly then return ofsf_cadfinish cd;
      % Solution formula construction
      ofsf_solutionformula cd;
      % Finish cad. Kernel order is restored.
      return ofsf_cadfinish cd;
   end;

procedure ofsf_cadpnum(phi,mkvarlres);
   % Ordered field standard form cylindrical algebraic decomposition.
   % [f] is a formula, [n] is a non-negative integer. If [f] is
   % closed (and rlcadanuex is switched on) a quantifier-free formula
   % equivalent to [f] is returned, otherwise [f] is returned in
   % prenex normal form.
   begin scalar cd,!*rlcadtrimtree,kord!*;
      % Preparation phase. Kernel order is changed.
      cd := ofsf_cadpreparation(phi,mkvarlres,nil);
      % Projection phase
      ofsf_cadprojection cd;
      % Extension phase (build partial cad tree)
      ofsf_cadextension(cd);
      return length atree_yield caddata_dd cd
   end;

procedure ofsf_cadproj(phi,mkvarlres);
   begin scalar cd,oldorder,phiprime,ophi,theo,ffl;
      % Preparation phase. Kernel order is changed.
      cd := ofsf_cadpreparation(phi,mkvarlres,nil);
      if !*rlverbose then caddata_print1 cd;
      % Projection phase
      ofsf_cadprojection cd;
      % Finish
      for each x in caddata_theo cd do mathprint rl_prepfof x;
      oldorder := caddata_oldorder cd;
      phiprime := caddata_phiprime cd;
      ophi := caddata_ophi cd;
      if oldorder neq 'undefined then setkorder oldorder;
      if !*rlverbose and ofsf_cadverbosep() then
	 ioto_tprin2 {"+ Order restored."};
      theo := for each fo in caddata_theo cd collect cl_apply2ats(fo,
	    function(lambda(x); ofsf_0mk2(ofsf_op x,reorder ofsf_arg2l x)));
      % Fr,...,F1
      ffl := reversip for j := 1 : caddata_r cd collect
	 for each f in caddata_ffj(cd,j) collect reorder f;
      return theo . ffl
   end;
  
procedure ofsf_cadpreparation(phi,mkvarlres,aaplus);
   % Cad preparation. [phi] is a ofsf formula; [mkvarlres] is a list
   % of variables encoding an addmitted projection order. Returns a
   % CADDATA.
   begin scalar w,ophi,tmp,varl,qal,r,k,oldorder,psi,ffr,ff,r,l,cd;
      if !*rlverbose then ioto_tprin2t "+++ Preparation Phase";
      if !*rlcaddecdeg then <<
	 if !*rlverbose then ioto_prin2 "+ decrease degrees: ";
      	 w := ofsf_decdeg0 phi;
      	 phi := car w;
      	 if !*rlverbose then
	    ioto_prin2 for each x in cdr w join {"(",car x,"^",cdr x,")"};
      >>;
      ophi := phi := cl_pnf phi; % ophi with old term order
      tmp := ofsf_mkvarl phi;  % ((xr,...,x_1).((x_r.Q_r),...,(x_k+1.Q_k+1)))
      if mkvarlres then <<
	 varl := mkvarlres;
	 qal := for each x in cdr tmp collect << %%% dodgy, use assoc
	    w := car mkvarlres;
	    mkvarlres := cdr mkvarlres;
	    w . cdr x
	 >>
      >> else <<
      	 varl := car tmp; % (xr,...,x_1)
      	 qal := cdr tmp   % ((x_r.Q_r),...,(x_k+1.Q_k+1))
      >>;
      if !*rlverbose and aaplus then
	 ioto_prin2t {"+++ ",length aaplus," proj. polynomials to add"};
      r := length varl;
      k := r - length qal;
      % reordering wrt. x_(c+1),xr,...,x_1
      oldorder := setkorder varl; %setkorder('xcplus1 . varl);
      varl := reverse varl; % (x1,...,xr) % reversip would affect kernel order
      qal := reversip qal; % ((x_k+1.Q_k+1),...,(x_r.Q_r))
      phi := cl_apply2ats(phi,
	 function(lambda(x); ofsf_0mk2(ofsf_op x,reorder ofsf_arg2l x)));
      psi := cl_matrix phi; % phi = Q_k+1 x_k+1 ... Q_r x_r psi
      ffr := for each f in cl_terml phi collect f;      
      ff := mkvect(r);
      putv(ff,r,ffr);
      l := (if bvb then k + length car bvb else 0)
	 where bvb:=cdr reverse ofsf_cadvbl1 phi; %[xk..]..[..xr]
      %ioto_prin2t {"l: ",l}; %%% should depend on rlverbose!*
      cd := caddata_mkblank();      
      caddata_putphi(cd,phi);
      caddata_putk(cd,k);
      caddata_putr(cd,r);
      caddata_putvarl(cd,varl);
      caddata_putqal(cd,qal);
      caddata_putpsi(cd,psi);
      caddata_putff(cd,ff);
      caddata_putaa(cd,union(ffr,for each f in aaplus collect reorder f));
      caddata_putaaplus(cd,for each f in aaplus collect reorder f);
      caddata_putdd(cd,'undefined);
      caddata_putphiprime(cd,'undefined);
      caddata_putoldorder(cd,oldorder);
      caddata_putophi(cd,ophi);
      caddata_putjj(cd,'undefined);
      caddata_puttheo(cd,nil);
      caddata_puthh(cd,nil);
      caddata_putl(cd,l);
      return cd
   end;

procedure ofsf_cadprojection(cd);
   begin scalar varl,k,r,ff;
      varl := caddata_varl cd;
      k := caddata_k cd;
      r := caddata_r cd;
      %ff := caddata_ff cd; % list (nil,...,nil,Fr)
      %ffr := getv(ff,r);
      if !*rlverbose then ioto_tprin2t {"+++ Projection Phase"};     
      ofsf_cadprojection1(cd); % here F and J is computed
      ff := caddata_ffl cd; % list (F1,...,Fr)
      if !*rlcadprojonly and ofsf_cadverbosep() then for i := 1 : r do <<
	 ioto_tprin2t {"+ projection factors level ",r-i+1};
	 mathprint ('list . for each f in nth(ff,r-i+1) collect prepf f);
	 %for each f in nth(ff,r-i+1) do mathprint prepf f
      >>;	 
      if !*rlverbose  then <<
	 ioto_tprin2t {"+ (#F1,...,#Fr)=",
	    for each ffi in ff collect length ffi}; >>;
      if !*rlcadprojonly then <<
	 ioto_tprin2t "+ switch rlcadprojonly: stopped, order unchanged.";
	 %setkorder oldorder;
	 return ; % rl_mk!*fof ophi;
      >>;
      %caddata_putff(cd,list2vector (nil . ff));
      return nil;
   end;

procedure ofsf_cadextension(cd);
   begin scalar r,dd;
      r := caddata_r cd;
      if !*rlverbose then
	 ioto_tprin2t {"+++ Partial CAD (extension and truth values)"};
      dd := ofsf_tree cd;
      if !*rlverbose then 
	 ioto_tprin2t {"+ (#D0,...,#Dr)=",
	    for i := 0 : r collect length atree_levellabels(dd,i)};
      if !*rlcadtree2dot then atree_2dot dd;
      if !*rlcadextonly then <<
	 %setkorder oldorder;
	 ioto_tprin2t "+ switch rlcadextonly: stopped, order restored.";
	 return dd; %rl_mk!*fof ophi;
      >>;
      caddata_putdd(cd,dd);
      return nil;
   end;

procedure ofsf_cadfinish(cd);
   % Finish. [cd] is CADDATA. Returns a foformula. Depending on
   % whether a phiprime was found, a q.f. fof or simply the input
   % formula is returned.
   begin scalar oldorder,phiprime,ophi,theo;
      if !*rlverbose then
	 ioto_tprin2t {"+++ Finish CAD"};
      oldorder := caddata_oldorder cd;
      phiprime := caddata_phiprime cd;
      ophi := caddata_ophi cd;
      %%% 2=rlcadgenlevel
      if !*rlqegen1 then
	 for j := 1 : min2(1,caddata_k cd) do 
            caddata_puttheo(cd,append(for each f in caddata_ffj(cd,j) collect
	       ofsf_0mk2('neq,f),caddata_theo cd));
      if oldorder neq 'undefined then setkorder oldorder;
      if !*rlverbose and ofsf_cadverbosep() then
	 ioto_tprin2 {"+ Order restored."};
      theo := for each fo in caddata_theo cd collect cl_apply2ats(fo,
	    function(lambda(x); ofsf_0mk2(ofsf_op x,reorder ofsf_arg2l x)));
      if phiprime neq 'undefined then phiprime := cl_apply2ats(phiprime,
	 function(lambda(x); ofsf_0mk2(ofsf_op x,reorder ofsf_arg2l x)));      
      if !*rlqegen then theo := ofsf_thsimpl theo;
      return (if phiprime neq 'undefined then
	 theo . cl_simpl(phiprime,theo,-1)
      else
	 theo . ophi)
   end;

procedure ofsf_cadswitches();
   % Switches. Prints the status of all switches relevant for cad.
   <<
      ioto_tprin2 ".:: list of switches ::.";
      ioto_tprin2 "------------------------";
      ioto_tprin2 "verbose switches:";
      ofsf_cadswitchprint(!*rlverbose); ioto_prin2 "rlverbose"; 
      ofsf_cadswitchprint(ofsf_cadverbosep()); ioto_prin2 "rlcadverbose"; 
      %ofsf_cadswitchprint(!*anuexverbose); ioto_prin2 "anuexverbose"; 
      ioto_tprin2 "switches for preparation phase:";
      ofsf_cadswitchprint(!*rlcaddecdeg);      ioto_prin2 "rlcaddecdeg"; 
      %ioto_tprin2 "switches for projection phase:";
      %ofsf_cadswitchprint(!*rlcadaproj);      ioto_prin2 "rlcadaproj"; 
      %ofsf_cadswitchprint(!*rlcadaprojalways);ioto_prin2 "rlcadaprojalways";
      %ofsf_cadswitchprint(!*rlcadhongproj);   ioto_prin2 "rlcadhongproj";
      %ofsf_cadswitchprint(!*rlcadfac);  ioto_prin2 "rlcadfac";
      ioto_tprin2 "switches for extension phase";
      ofsf_cadswitchprint(!*rlcadpartial); ioto_prin2 "rlcadpartial"; 
      ofsf_cadswitchprint(!*rlcadte); ioto_prin2 "rlcadte"; 
      ofsf_cadswitchprint(!*rlcadpbfvs); ioto_prin2 "rlcadpbfvs"; 
      ofsf_cadswitchprint(!*rlcadisoallroots); ioto_prin2 "rlcadisoallroots"; 
      ofsf_cadswitchprint(!*rlcadtrimtree); ioto_prin2 "rlcadtrimtree";
      ofsf_cadswitchprint(!*rlcadfasteval); ioto_prin2 "rlcadfasteval"; 
      ofsf_cadswitchprint(!*rlcadfulldimonly); ioto_prin2 "rlcadfulldimonly"; 
      ofsf_cadswitchprint(!*rlcadrmwc); ioto_prin2 "rlcadrmwc"; 
      ioto_tprin2 "switches for solution formula construction phase";
      ofsf_cadswitchprint(!*rlcadrawformula); ioto_prin2 "rlcadrawformula"; 
      ofsf_cadswitchprint(!*rlcaddnfformula); ioto_prin2 "rlcaddnfformula"; 
      ioto_tprin2 "general switches:";
      ofsf_cadswitchprint(!*rlcadpreponly); ioto_prin2 "rlcadpreponly"; 
      ofsf_cadswitchprint(!*rlcadprojonly); ioto_prin2 "rlcadprojonly"; 
      %ofsf_cadswitchprint(!*rlcadbaseonly); ioto_prin2 "rlcadbaseonly"; 
      ofsf_cadswitchprint(!*rlcadextonly);  ioto_prin2 "rlcadextonly";
      %ioto_tprin2 "switches for algebraic numbers (do not change)";
   >>;

procedure ofsf_cadswitchprint(b);   
   % Switch print. [b] is a BOOL.
   if b then ioto_tprin2 " ON  -- " else ioto_tprin2 " OFF -- ";
   
% module acell;
% Andreas' cell. Constructors and access functions for data type ACELL
% plus the code for decomposing the cylinder over a given cell.

%DS
% <CAD> ::= (..., <CDEC>, ...)
% <CDEC> ::= (..., <ACELL>, ...)
% <ACELL> ::= (<IDX> . (<SP> . <TV> . <DESC> . <TL> . nil)) (an alist)
% <IDX> ::= List of natural numbers
% <SP> ::= List of ANU's
% <DESC> ::= "quantifier-free formula"
% <TL> ::= "tag list"

procedure acell_mk(idx,tp,tv,desc,tl);
   % Cell make. [idx] is an IDX, [tp] is a SP, [tv] is [true] or
   % [false], [desc] is a DESC and [tl] is a tag list. Returns an
   % ACELL.
   {idx, tp, tv, desc, tl};

procedure acell_getidx(c);
   % Cell get index. [c] is an ACELL. Returns a IDX
   car c;

procedure acell_getsp(c);
   % Cell get sample point. [c] is an ACELL. Returns a SP.
   cadr c;

procedure acell_gettv(c);
   % Cell get truth value. [c] is an ACELL. Returns [true] or [false].
   caddr c;

procedure acell_getdesc(c);
   % Cell get index. [c] is an ACELL. Returns a quantifier-free formula.
   cadddr c;   

procedure acell_gettl(c);
   % Cell get tag list. [c] is an ACELL. Returns a tag list.
   cadddr cdr c;

procedure acell_sri(c);
   % Symbolic root information. [c] is a cell. 
   atsoc('root,tl) or atsoc('between,tl) or atsoc('below,tl) or
   atsoc('beyond,tl) or atsoc('arbitrary,tl) where tl=acell_gettl c; 

procedure acell_putsp(c,sp);
   % Cell put truth value. [c] is an ACELL, [sp] is a SP. Returns [c]
   % with the truth value changed.
   cadr c := sp;

procedure acell_puttv(c,tv);
   % Cell put truth value. [c] is an ACELL, [tv] is [true] or
   % [false]. Returns [c] with the truth value changed.
   caddr c := tv;

procedure acell_putdesc(c,desc);
   % Cell put description. [c] is an ACELL, [desc] is a signature or a
   % quantifier-free formula. Returns [c] with the description
   % changed.
   cadddr c := desc;

procedure acell_puttl(c,tl);
   % Cell put tag list. [c] is an ACELL, [tl] is a tag list Returns
   % [c] with the tag list changed.
   cadddr cdr c := tl;

procedure acell_addtagip(c,tg);
   %taglist := tg . taglist where taglist=acell_gettl c;
   cadddr cdr c := tg . acell_gettl c;

procedure acell_tvasstring(c);
   % truth value as string
   if acell_gettv c eq 'true then "T" else
      if acell_gettv c eq 'false then "F" else
	 "?";

procedure acell_med(c);
   % Middle point of the interval of the sample point's last component.
   iv_med anu_iv car acell_getsp c;

procedure acell_sortfn(c1,c2);
   rat_leq(acell_med c1,acell_med c2);
   
% module cadtree;

procedure ofsf_tree(cd);   
   ofsf_treeovercell(basecell,cl_nnf caddata_psi cd,cd)
      where basecell=acell_mk(nil,nil,nil,'true,nil); % the only cell of D0

procedure ofsf_treeovercell(basecell,psi,cd);
   % Cad tree over cell. basecell is a cell in D_j-1, fff is Fj,...,Fr,
   % varl is x1,...,x_r, qal is x_{k+1}.theta_{k+1},...,x_r,theta_r,
   % and psi the qf. part of the input formula. Returns a tree over
   % the basecell. Intuition: Finds for a cell C in D_j a partial cad
   % tree that has C as a root; if j>=k then C has a truth value.
   begin scalar r,k,l,j,varl,qal,hh,fffj,xj,sp,cell,treel,thetaj,neutral,
	 nrdata,ncbuffer,res,tv,w;
      integer d,n;
      sp := acell_getsp basecell; j := length sp + 1;      
      if ofsf_cadverbosep() then ioto_prin2 {"(",j-1};
      %ioto_tprin2t {"tree over: ",basecell}; % verbose output, somet. needed
      r := caddata_r cd; k := caddata_k cd; l := caddata_l cd;
      varl := caddata_varl cd; qal := caddata_qal cd;
      hh := caddata_hh cd; % tagged projection factors
      % ioto_tprin2t {"Hj-1: ",getv(hh,j-1)};
      % 1. BASE CASE: j=r, evaluation case. The base cell is a leaf.
      if j > r then <<
	 if caddata_tv cd then acell_puttv(basecell,ofsf_evalqff(psi,sp,varl));
	 if ofsf_cadverbosep() then ioto_prin2 ")";
         %if w eq 'true then ioto_tprin2t {"tree over: ",basecell};
      	 return atree_new basecell
      >>;
      % trial evaluation
      if caddata_tv cd and !*rlcadte then <<
	 %ioto_tprin2t "----------";
	 %ioto_prin2t {"sp: ",sp};
	 %ioto_prin2t {"psi: ",psi};
      	 psi := cl_simpl(cl_apply2ats1(psi,
	    function(lambda x,sp; ofsf_0mk2(ofsf_op x,
	       ofsf_trialevalsgnf(ofsf_arg2l x,sp))),{sp}),nil,-1);
      	 if (psi eq 'true or psi eq 'false) then <<
	    acell_puttv(basecell,psi);
	    if ofsf_cadverbosep() then ioto_prin2 "TE)";
	    return atree_new basecell
      	 >>;
	 %ioto_prin2t "------noTE";	 
      >>;
      % Prepare Polynomials
      xj := nth(varl,j);
      fffj := ofsf_tocprepare(getv(hh,j),xj,sp,varl);
      %fffj := for each te in fffj collect tag_object te;
      % now tag aware
      nrdata := rip_init(fffj,xj); % initialize next root data
      % remove p.sc's without roots; no problem with tags.
      rip_putpscl(nrdata,for each psc in rip_pscl nrdata join
	 if (d := aex_stchsgnch(cdr psc,xj,'minfty) -
	    aex_stchsgnch(cdr psc,xj,'infty)) neq 0 then <<n := n+d;{psc}>>);
      if ofsf_cadverbosep() then ioto_prin2 {":",2*n+1};
      % mildly generic CAD: add assumptions
      %if !*rlqegen1 and j<=min2(1,k) then
%	 ofsf_cadtheo!* := append(for each f in fffj collect
%	    ofsf_0mk2('neq,numr aex_ex f),ofsf_cadtheo!*); 
      ncbuffer := ofsf_ncinit();
      % 2. RECURSION CASE: j<=r
      % 2a. 0<=j<=k or no truth values
      if (0<=j and j<=k) or (not caddata_tv cd) then
	 while cell := ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k) do
	 <<
	    if not ofsf_iswhitecell(cell,cd) then 
	       treel := ofsf_treeovercell(cell,psi,cd) . treel
	 >>
      else << % 2b. k<=j<r
      	 % set tv of basecell to neutral element wrt theta_j+1
      	 thetaj := cdr nth(qal,j-k);
      	 neutral := if thetaj eq 'all then 'true else 'false;
      	 acell_puttv(basecell,neutral);
      	 % as long as the truth value of the basecell is unknown
      	 while (acell_gettv basecell eq neutral) and (cell :=
	    ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k)) do <<	 
	       treel := ofsf_treeovercell(cell,psi,cd) . treel; 
	       acell_puttv(basecell,acell_gettv atree_rootlabel car treel)
	    >>;
	 % add remaining cells, if wanted
	 if not !*rlcadpartial then
	    while cell := ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k) do
	       treel := ofsf_treeovercell(cell,psi,cd) . treel;
      >>;
      %if !*rlcadisoallroots then while aex_nextroot(nrdata,xj) do ;
      if !*rlcadisoallroots then
	 while cell := ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k) do
	    treel := atree_new(cell) . treel;
      treel := sort(treel,function atree_sortfn);
      %ioto_tprin2t {treel};
      if caddata_ans cd then ofsf_addanswers(basecell,treel,j,cd);
      if ofsf_cadverbosep() then ioto_prin2 {"_",(2*n+1)-length treel,")"};
      % Most likely the following will never happen.
      if null treel then rederr "GCAD: stack full of white cells occured.";
      % construct result tree
      if !*rlcadtrimtree and j > k then res := atree_new basecell
	 else res := atree_addchildlistip(atree_new basecell,treel);
      % propagation below free variable space
      if not (caddata_tv cd and !*rlcadpbfvs) then return res;
      tv := list2set for each b in treel collect acell_gettv atree_rootlabel b;
      if length tv eq 1 then acell_puttv(atree_rootlabel res, car tv); 
      return res
   end;

procedure ofsf_iswhitecell(cell,cd);
   % Is white cell. [cell] is a ACELL, [cd] is [CADDATA]. Returns t or nil.
   begin scalar theta, sp;
      if (caddata_theo cd eq 'undefined) or (not !*rlcadrmwc) then return nil;
      theta := rl_smkn('and,caddata_theo(cd));
      sp := acell_getsp(cell);
      theta := cl_simpl(cl_apply2ats1(theta,
	 function(lambda x,sp; ofsf_0mk2(ofsf_op x,
	    ofsf_trialevalsgnf(ofsf_arg2l x,sp))),{sp}),nil,-1);
      if theta eq 'false then <<
	 % acell_puttv(basecell,'white);
	 if ofsf_cadverbosep() then ioto_prin2 {"(",length sp,":W)"};
	 return t
      >> else
	 return nil
   end;

procedure ofsf_addanswers(basecell,treel,j,cd);
   % Add root info and answers to cells.
   (if !*rlcadisoallroots and k+1<=j and j<=l then <<
      % 1. add root information
      ofsf_addrootinfo(treel,getv(caddata_ffid cd,j));
      %if ofsf_cadverbosep() then ioto_tprin2t {for each tt in treel collect
	% acell_sri atree_rootlabel tt};
      % 2. tag answers in case treel contains cells of level l
      if j=l then for each tt in treel do
	 if acell_gettv atree_rootlabel tt eq 'true then
	    acell_addtagip(atree_rootlabel tt, 'answers . {nil});
      acell_addtagip(basecell, 'answers . for each tt in treel join
	 if acell_gettv atree_rootlabel tt eq 'true then
	    for each a in cdr atsoc('answers,acell_gettl atree_rootlabel tt)
	       collect acell_sri atree_rootlabel tt . a);
   >>) where k := caddata_k cd, l := caddata_l cd;

procedure ofsf_addrootinfo(treel,ffids);
   % Add root information. [treel] is a non-empty list of ATREEs of
   % ACELLs. The argument is changed in-place. We add the "symbolic
   % root information" to the root labels of these trees. We assume
   % that cells, which have a 0-dim last component, have inherited
   % their tags from the appropriate projection factors.
   begin scalar rnl,rtg,ltg;
      % treel will always be an odd number of trees
      % if there is only one cell, then 'sri . 'arbitrary is there already
      if null cdr treel then return ;
      % so we have at least 3 cells
      % rnl (root number list) is an alist id . num.
      rnl := for each id in ffids collect id . 0;      
      % the leftmost cell is special.
      % tag the second cell from the left
      rtg := ofsf_addrootinfo0dim(atree_rootlabel cadr treel,rnl,ffids);
      % tag the leftmost cell
      acell_addtagip(atree_rootlabel car treel, 'below . rtg);
	 %'below . atsoc('root,acell_gettl atree_rootlabel cadr treel));
      treel := cddr treel;
      ltg := rtg;
      while cdr treel do << % there are at least 3 cells
         rtg := ofsf_addrootinfo0dim(atree_rootlabel cadr treel,rnl,ffids);
	 acell_addtagip(atree_rootlabel car treel, 'between . ltg . rtg);
	 treel := cddr treel;
         ltg := rtg;
      >>;% until null cdr treel; % until there is only one cell left.
      acell_addtagip(atree_rootlabel car treel,'beyond . ltg);
   end;

procedure ofsf_addrootinfo0dim(cell,rnl,ffids);
   % Add root info to cell with 0-dim last component in-place. Returns
   % the added tag. This return value is used.
   begin scalar tl,ri;
      tl := acell_gettl cell;
      ofsf_rnlinc(rnl,tl);                     
      ri := 'root . for each tg in intersection(ffids,tl) collect
	 tg . cdr atsoc(tg,rnl);
      acell_addtagip(cell,ri);
      return ri
   end;

procedure ofsf_rnlinc(rnl,tl);
   % Increment rnl.
   if rnl then <<
      ofsf_rnlinc(cdr rnl,tl);
      if memq(caar rnl,tl) then cdar rnl := cdar rnl+1
   >>;

      
algebraic procedure doc(pl,ql);
   % Degree of correspondence. [pl] and [ql] are LIST(NUM).
   begin scalar np,qp,caa,cbb,ccc,p_i,pj,qi,qj,sd;
      caa := cbb := ccc := 0;
      np := length pl;
      ng := length ql;
      if np neq ng then rederr "doc: parameter lists have different lengths";
      for i := 1 : np-1 do
	 for j := i+1 : ng do
	 <<
	    p_i := mynth(pl,i); pj := mynth(pl,j);
	    qi := mynth(ql,i); qj := mynth(ql,j);
	    if p_i neq -1 and pj neq -1 and qi neq -1 and qj neq -1 then
	    <<
	       caa := caa+1;
	       sd := abs(sign(p_i-pj)-sign(qi-qj));
	       if sd=0 then cbb := cbb+1
	       else if sd=1 then ccc := ccc+1
	    >>
	 >>;
      return (cbb+1/2*ccc)/caa
   end;


algebraic procedure degreeofcorrespondence(pl,gl);
   begin scalar np,ng;
      np := length pl;
      ng := length gl;
      n := for i := 1 : np-1 sum
	 for j := i+1 : ng sum
	    quality1(mynth(pl,i),mynth(pl,j),mynth(gl,i),mynth(gl,j));
      d := for i := 1 : np-1 sum for j := i+1 : ng sum
	 if mynth(pl,i) neq mynth(pl,j) then 1;
      write d;
      if d=0 then return 1;
      return n/d
   end;

algebraic procedure quality1(p1,p2,g1,g2);
   if p1=p2 then 0
   else if p1<p2 then
      (if g1>g2 then 0 else if g1=g2 then 1/2 else 1)
   else % p1>p2
      quality1(p2,p1,g2,g1);

algebraic procedure rlcadnumauto(phi);
%   rlcadnum(rlcadproj(phi,pord),pord)
%      where pord=>for each vb in rlcadvbl phi join vb;
   begin scalar plist,pord;
      pord := for each vb in rlcadvbl rlpnf phi join vb;
      plist := rlcadproj(phi,pord);
      return rlcadnum(plist,pord)
   end;

algebraic procedure rlcadnumepo(phi);
   % Number of projection polynomials for full CAD with efficient
   % projection order.
   begin scalar plist,pord;
      pord := rlcadporder phi;
      plist := rlcadproj(phi,pord);
      return rlcadnum(plist,pord)
   end;

algebraic procedure rlcaddefaultorder(phi);
   for each vb in rlcadvbl rlpnf phi join vb;

algebraic procedure rlcadnum(pl,pord);
   first rlcadnum1(pl,pord);

algebraic procedure rlcadguessauto(phi);
   begin scalar plist,pord;
      pord := for each vb in rlcadvbl rlpnf phi join vb;
      plist := rlcadproj(phi,pord);
      return rlcadguess(plist,pord)
   end;

algebraic procedure rlcadguessepo(phi);
   begin scalar plist,pord;
      pord := rlcadporder phi;
      plist := rlcadproj(phi,pord);
      return rlcadguess(plist,pord)
   end;

algebraic procedure rlcadguess(pl,pord);
   for each n in rlcadguess1(pl,pord) product n;

symbolic operator rlcadnum1;
lisp procedure rlcadnum1(pl,pord);
   % . [pl] is a list (Fr,...,F1) of projection factors, [pord] is a
   % list (xr,...,x1) of variables. Returns a list (#Dr,...,#D1) of numbers.
   begin scalar oldorder,w,ff,varl;
      oldorder := setkorder pord;
      w := for each ffj in cdr pl collect
 	 for each f in cdr ffj collect reorder numr simp f;
      ff := reverse w;
      varl := reverse cdr pord;
      w := reversip ofsf_cadnum1(ff,varl,nil); % (#Dr,...,#D1)
      setkorder oldorder;
      return 'list . w;
   end;

symbolic operator rlcadpnum;
procedure rlcadpnum(phi,pord);
   ofsf_cadpnum(rl_simp phi,cdr pord);
   
symbolic operator rlcadguess1;
lisp procedure rlcadguess1(pl,pord);
   % . [pl] is a list (Fr,...,F1) of projection factors, [pord] is a
   % list (xr,...,x1) of variables. Returns a list (#Dr,...,#D1) of numbers.
   begin scalar oldorder,w,ff,varl,!*rlacdguessnoc;
      oldorder := setkorder pord;
      w := for each ffj in cdr pl collect
 	 for each f in cdr ffj collect reorder numr simp f;
      ff := reverse w;
      varl := reverse cdr pord;
      %on1 !*rlcadguessnoc;      
      w := ofsf_cadnum1(ff,varl,t);
      %off1 !*rlcadguessnoc;
      w := reversip w; % (#Dr,...,#D1)
      setkorder oldorder;
      return 'list . w;
   end;

procedure ofsf_cadnum1(ff,varl,probe);
   % Cad num subroutine. [ff] is a list (F1,...,Fr) of lists of
   % polynomials. Returns a list of natural numbers of length r+1.
   begin scalar w;
      w := ofsf_fulltree(
	 list2vector(nil . for each ffj in ff collect
	    for each f in ffj collect tag_(f,{'dummytag})),
	 varl,probe);
%	 list2vector(nil . ff),varl,probe);
      return for i := 0 : length varl collect length atree_levellabels(w,i)
   end;

procedure ofsf_fulltree(ff,varl,probe);
   % ff is a VECTOR(LIST(TAG(SF))) of the form [nil,F1,...,Fr].
   ofsf_fulltreeovercell(basecell,ff,varl,'unknown,'unknown,probe)
	 % this is the only cell of D0
	 where basecell=acell_mk(nil,nil,nil,'true,nil);

procedure ofsf_fulltreeovercell(basecell,ff,varl,qal,psi,probe);
   % Cad full tree over cell. basecell is a cell in D_j-1, ff is a
   % vector [nil, F1,...,Fr] where the elements are LIST(TAG(SF)),
   % varl is (x1,...,x_r), qal and psi are not needed. Returns a tree
   % over the basecell. Intuition: Finds for a cell C in D_j a full
   % cad tree that has C as a root; each cell has a sample point, but
   % no truth values.
   begin scalar r,k,j,ffj,fffj,xj,sp,cell,treel,nrdata,ncbuffer;
      integer d,n;
      if ofsf_cadverbosep() then ioto_prin2 {"(",length acell_getsp basecell};
      %ioto_tprin2t {"tree over: ",basecell}; % vb output, sometimes needed
      r := length varl;
      k := r; % not needed, nextcell would need it for fulldimonly and gen1
      j := length acell_getsp(basecell)+1;
      sp := acell_getsp basecell;
      % 1. BASE CASE: j=r, evaluation case. The base cell is a leaf.
      if j > r then <<
	 if ofsf_cadverbosep() then ioto_prin2 ")";
      	 return atree_new basecell
      >>;
      % Prepare Polynomials
      xj := nth(varl,j);
      fffj := ofsf_tocprepare(list2vector getv(ff,j),xj,sp,varl);
      %ffj := for each te in ffj collect tag_object te;
      %ffj := ofsf_tocprepare(ff,xj,sp,varl);
      nrdata := rip_init(fffj,xj); % initialize next root data
      rip_putpscl(nrdata,for each psc in rip_pscl nrdata join
	 if (d := aex_stchsgnch(cdr psc,xj,'minfty) -
	    aex_stchsgnch(cdr psc,xj,'infty)) neq 0 then <<n := n+d;{psc}>>);
      if ofsf_cadverbosep() then ioto_prin2 {":",2*n+1};
      ncbuffer := ofsf_ncinit();
      % 2. RECURSION CASE: j<=r
      if 0<=j and j<=r then % full dec., no truth values
	 if probe then <<
	    cell := ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k);
	    treel := ofsf_fulltreeovercell(cell,ff,varl,qal,psi,probe) . treel;
	    for i := 1 : (2*n+1)-1 do
	       treel := atree_new basecell . treel;
	 >> else while cell := ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k) do
	    treel := ofsf_fulltreeovercell(cell,ff,varl,qal,psi,probe) . treel;
      % construct result tree
      return atree_addchildlistip(atree_new basecell,treel);
   end;

procedure ofsf_tocprepare(hhj,xj,sp,varl);
   % Tree over cell prepare polynomials. [hhj] is a VECTOR(TAG(SF)), [xj]
   % is a variable. Returns a LIST(TAG(AEX)).
   begin scalar w;
      w := vector2list hhj;
      if null sp then <<
	 if ofsf_cadverbosep() then ioto_prin2 "(base case)";
	 return for each te in w collect
	    tag_(aex_fromsf tag_object te,tag_taglist te);
      >> else <<
      	 % convert SF to AEX and substitute sample point from basecell
      	 w := for each te in w collect
	    tag_(ofsf_subsp(aex_fromsf tag_object te,sp,varl),tag_taglist te);
      	 % make elements of ffj smaller and throw away null and const polys
      	 w := for each ae in w collect
	    tag_(aex_mklcnt aex_reduce tag_object ae,tag_taglist ae);
      	 w := for each ae in w join
	    if not aex_simplenumberp tag_object ae then
	       {tag_(aex_reduce aex_sqfree(tag_object ae,xj),tag_taglist ae)};
	 w := tglist2set w;
      	 %if !*rlcaddebug then
	 %   for each f in ffj do if aex_sgn aex_lc(f,xj) eq 0 then
	 %      prin2t "ofsf_tocprepare: smaller but lc trivial";
      	 %if !*rlcaddebug then
	 %   for each f in ffj do if aex_simplenumberp f then
	 %      prin2t "ofsf_tocprepare: smaller but number";
      	 % make elements of ffj pairwise prime (paarweise teilerfremd)
      	 w := aex_tgpairwiseprime(w,xj);
      	 %if !*rlcaddebug then
	 %   for each f in ffj do if aex_sgn aex_lc(f,xj) eq 0 then
	 %      prin2t "ofsf_tocprepare: pairwiseprime but lc trivial";
      	 %if !*rlcaddebug then
	 %   for each f in ffj do if aex_simplenumberp f then
	 %      prin2t "ofsf_tocprepare: pairwiseprime but number";
      >>;
      return w;
   end;

procedure caddata_mkblank();
   % Blank caddata. No arguments. Returns a CADDATA. Undefined entries
   % should have the value $'undefined$. Returns CADDATA.
   begin scalar cd;
      cd := mkvect(31);
      putv(cd,1, 'undefined); % [phi] is an ofsf formula in PNF
      putv(cd,2, 'undefined); % [k] is an integer
      putv(cd,3, 'undefined); % [r] is an integer
      putv(cd,4, 'undefined); % [varl] is a list of identifiers
      putv(cd,5, 'undefined); % [qal] is a list of dotted pairs (x . q)
      putv(cd,6, 'undefined); % [psi] is a q.-free fof
      putv(cd,7, 'undefined); % [ff] is a vector of SF
      putv(cd,8, 'undefined); % [dd] is an atree
      putv(cd,9, 'undefined); % [phiprime] is a q.-free fof
      putv(cd,10,'undefined); % [oldorder] is a list of kernels
      putv(cd,11,'undefined); % [ophi] is a fof, the original input formula
      putv(cd,12,'undefined); % [J] unused
      putv(cd,13,'undefined); % [theo] is a list of negated atoms
      putv(cd,14,'undefined); % [hh] is a vector of vectors of tagged sf
      putv(cd,15,'undefined); % [l] is an integer
      % we can have no more arguments to this procededure
      %putv(cd,16,ffid); % [Fid] is a vector of lists of ids.
      putv(cd,17,!*rlcadverbose); % [cadverbose] truth value
      putv(cd,18,!*rlcaddecdeg);
      putv(cd,19,!*rlcadpartial);
      putv(cd,20,!*rlcadisoallroots);
      putv(cd,21,!*rlcadtrimtree);
      putv(cd,22,!*rlcadfasteval);
      putv(cd,23,!*rlcadfulldimonly);
      putv(cd,24,!*rlcadte);
      putv(cd,25,!*rlcadpbfvs);
      putv(cd,26,!*rlcadrawformula);
      putv(cd,27,!*rlcaddnfformula);
      putv(cd,28,!*rlcadans);
      putv(cd,29,!*rlcadtv);
      putv(cd,30,'undefined); % [A] list of SF
      putv(cd,31,'undefined); % [A+] list of SF
      return cd
   end;

%%% --- basic access functions --- %%%

procedure caddata_phi(cd);      getv(cd,1);
procedure caddata_k(cd);        getv(cd,2);
procedure caddata_r(cd);        getv(cd,3);
procedure caddata_varl(cd);     getv(cd,4);
procedure caddata_xj(cd,j);     nth(getv(cd,4),j);
procedure caddata_qal(cd);      getv(cd,5);
procedure caddata_psi(cd);      getv(cd,6);
procedure caddata_ff(cd);       getv(cd,7);
procedure caddata_ffj(cd,j);    getv(getv(cd,7),j);
procedure caddata_ffv(cd);      getv(cd,7);
procedure caddata_ffl(cd);      cdr vector2list getv(cd,7);
procedure caddata_dd(cd);       getv(cd,8);
procedure caddata_phiprime(cd); getv(cd,9);
procedure caddata_oldorder(cd); getv(cd,10);
procedure caddata_ophi(cd);     getv(cd,11);
procedure caddata_jj(cd);       getv(cd,12);
procedure caddata_theo(cd);     getv(cd,13);
procedure caddata_hh(cd);       getv(cd,14);
procedure caddata_l(cd);        getv(cd,15);
procedure caddata_ffid(cd);     getv(cd,16);
procedure caddata_verbose(cd);	getv(cd,17);
procedure caddata_decdeg(cd);	getv(cd,18);
procedure caddata_partial(cd);	getv(cd,19);
procedure caddata_isoallroots(cd);	getv(cd,20);
procedure caddata_trimtree(cd);	getv(cd,21);
procedure caddata_fasteval(cd);	getv(cd,22);
procedure caddata_fulldimonly(cd);	getv(cd,23);
procedure caddata_te(cd);	getv(cd,24);
procedure caddata_pbfvs(cd);	getv(cd,25);
procedure caddata_rawformula(cd);	getv(cd,26);
procedure caddata_dnfformula(cd);	getv(cd,27);
procedure caddata_ans(cd);	getv(cd,28);
procedure caddata_tv(cd);	getv(cd,29);
procedure caddata_aa(cd);	getv(cd,30);
procedure caddata_aaplus(cd);	getv(cd,31);
 
procedure caddata_bvl(cd);
   % bound variable list
   for i := caddata_k cd + 1:caddata_r cd collect nth(caddata_varl cd,i); 

procedure caddata_fvl(cd);
   % free variable list
   for i := 1:caddata_k cd collect nth(caddata_varl cd,i); 

procedure caddata_putphi(cd,phi);           putv(cd,1,phi);
procedure caddata_putk(cd,k);               putv(cd,2,k);
procedure caddata_putr(cd,r);               putv(cd,3,r);
procedure caddata_putvarl(cd,varl);         putv(cd,4,varl);
procedure caddata_putqal(cd,qal);           putv(cd,5,qal);
procedure caddata_putpsi(cd,psi);           putv(cd,6,psi);
procedure caddata_putff(cd,ff);             putv(cd,7,ff);
procedure caddata_putdd(cd,dd);             putv(cd,8,dd);
procedure caddata_putphiprime(cd,phiprime); putv(cd,9,phiprime);
procedure caddata_putoldorder(cd,oldorder); putv(cd,10,oldorder);
procedure caddata_putophi(cd,phi);          putv(cd,11,phi);
procedure caddata_putjj(cd,jj);             putv(cd,12,jj);
procedure caddata_puttheo(cd,theo);         putv(cd,13,theo);
procedure caddata_puthh(cd,hh);             putv(cd,14,hh);
procedure caddata_putl(cd,l);               putv(cd,15,l);
procedure caddata_putffid(cd,ffid);         putv(cd,16,ffid);
procedure caddata_putverbose(cd,a);	putv(cd,17,a);
procedure caddata_putdecdeg(cd,a);	putv(cd,18,a);
procedure caddata_putpartial(cd,a);	putv(cd,19,a);
procedure caddata_putisoallroots(cd,a);	putv(cd,20,a);
procedure caddata_puttrimtree(cd,a);	putv(cd,21,a);
procedure caddata_putfasteval(cd,a);	putv(cd,22,a);
procedure caddata_putfulldimonly(cd,a);	putv(cd,23,a);
procedure caddata_putte(cd,a);          putv(cd,24,a);
procedure caddata_putpbfvs(cd,a);	putv(cd,25,a);
procedure caddata_putrawformula(cd,a);	putv(cd,26,a);
procedure caddata_putdnfformula(cd,a);	putv(cd,27,a);
procedure caddata_putans(cd,a);	        putv(cd,28,a);
procedure caddata_puttv(cd,a);	        putv(cd,29,a);
procedure caddata_putaa(cd,a);	        putv(cd,30,a);
procedure caddata_putaaplus(cd,a);	putv(cd,31,a);

procedure caddata_print(cd);
   begin
      ioto_prin2t "+ caddata:";
      if caddata_phi cd neq 'undefined then ioto_prin2t{"phi := ",caddata_phi cd};
      if caddata_k cd neq 'undefined then ioto_prin2t{"k := ",caddata_k cd};
      if caddata_r cd neq 'undefined then ioto_prin2t{"r := ",caddata_r cd};
      if caddata_varl cd neq 'undefined then ioto_prin2t{"varl := ",caddata_varl cd};
      if caddata_qal cd neq 'undefined then ioto_prin2t{"qal := ",caddata_qal cd};
      if caddata_psi cd neq 'undefined then ioto_prin2t{"psi := ",caddata_psi cd};
      if caddata_ff cd neq 'undefined then ioto_prin2t{"ff := ",caddata_ff cd};
      if caddata_dd cd neq 'undefined then ioto_prin2t{"dd := ",caddata_dd cd};
      if caddata_phiprime cd neq 'undefined then ioto_prin2t{"phiprime := ",caddata_phiprime cd};
      if caddata_oldorder cd neq 'undefined then ioto_prin2t{"oldorder := ",caddata_oldorder cd};
      if caddata_ophi cd neq 'undefined then ioto_prin2t{"ophi := ",caddata_ophi cd};
      %if caddata_ cd neq 'undefined then ioto_prin2t{"",caddata_ cd};
   end;

procedure caddata_print1(cd);
   begin
      ioto_prin2t "+ caddata:";
      if caddata_k cd neq 'undefined then ioto_prin2t{"k := ",caddata_k cd};
      if caddata_r cd neq 'undefined then ioto_prin2t{"r := ",caddata_r cd};
      if caddata_varl cd neq 'undefined then ioto_prin2t{"varl := ",caddata_varl cd};
      if caddata_qal cd neq 'undefined then ioto_prin2t{"qal := ",caddata_qal cd};
      if caddata_oldorder cd neq 'undefined then ioto_prin2t{"oldorder := ",caddata_oldorder cd};
   end;

%%% --- root isolation --- %%%

procedure ofsf_ncinit;
   % ncbuffer nets to hold at most one cell
   {nil};

procedure ofsf_nextcell(ncbuffer,sp,nrdata,xj,j,k);
   % Returns a cell. Caveat: j eq length sp +1, i.e. j is the level of
   % the newly generated cells.
   begin scalar tgroot,root,cell,w;
      if car ncbuffer eq 'finished then return nil;
      %ioto_prin2 {"[",length sp,"]"};
      % there is a cell left in ncbuffer (a 0-dim one)
      %   (!*rlcadfulldimonly and j>k): throw away
      %   (!*rlqegen1 and j<=k): throw away, generic cad
      if car ncbuffer then 
	 if not((!*rlcadfulldimonly and j>k) or
	    (!*rlqegen1 and j<=min2(1,k))) then <<
	       cell := car ncbuffer;
	       car ncbuffer := nil;
	       return cell;
      	    >> else <<
	       car ncbuffer := nil;
	       if !*rlverbose and !*rlqegen1 and j<=min2(1,k) then
	       	  ioto_prin2 {"(",j,"!gen)"};
	       if !*rlverbose and !*rlcadfulldimonly and j>k then
	       	  ioto_prin2 {"(",j,"!fdo)"};
	    >>;
      % there is no cell left, so we need to get a root to get the next
      % two cells.
      if tgroot := aex_nextroot(nrdata,xj) then <<
	 root := tag_object tgroot; %%% 
	 % drop one cell in buffer (the 0-dim one)...
	 car ncbuffer := acell_mk(2*(length rip_rootl nrdata)-1,
	    root . sp,nil,nil,tag_taglist tgroot);
	 % ... and return the other cell (the full-dim one) with rat. sp
	 w := iv_lb anu_iv root;
	 return acell_mk(2*(length rip_rootl nrdata)-2,
	    anu_fromrat(xj,w,iv_mk(w,w)) . sp,nil,nil,nil);
      >>;
      % there is no cell and no root left.      
      car ncbuffer := 'finished;
      % if there was no root, make cell with 0 as sample point
      if null rip_rootl nrdata then
	 return acell_mk(0,anu_fromrat(xj,rat_0(),
	    iv_mk(rat_0(),rat_0())) . sp,nil,nil,{{'arbitrary}});
      % search rootlist for the maximum of all right bounds. make cell.
      w := rat_mapmax for each r in rip_rootlnotags nrdata collect
	 iv_rb anu_iv r;
      return acell_mk(2*(length rip_rootl nrdata),
	 anu_fromrat(xj,w,iv_mk(w,w)) . sp,nil,nil,nil);      
   end;

procedure ofsf_subsp(ae,sp,varl);
   % ae is an AEX, sp is a sample point of length j-1, varl is
   % x1,...,x_{j-1} or a longer list. Returns an AEX, an univariate
   % algebraic polynomial.
   begin
%      if !*rlcaddebug and not (length sp eq length varl) then
%	 prin2 "***** aecad_subsp: length of sp and val do not match";
      sp := reverse sp;
      while sp do <<
      	 ae := aex_bind(ae,car varl,car sp);
      	 sp := cdr sp; varl := cdr varl
      >>;
      return ae;
   end;

procedure ofsf_subsp!*(ae,sp);
   % In ae, each anu is bound to the main variable of its defining
   % polynomial. Henct no variable list required.
   << sp := reverse sp;
      while sp do << %%% for loop now possible
      	 ae := aex_bind(ae,aex_mvar anu_dp car sp,car sp);
      	 sp := cdr sp;
      >>;
      ae
   >>;

% - module atree - %

%DS
% <ATREE> ::= 'tree . <rootlabel> . <childlist>

procedure atree_new(a);
   % New tree. [a] is the label of the tree's root.
   {'tree,a};

procedure atree_rootlabel(tt);
   cadr tt;

procedure atree_sortfn(t1,t2);
   % Sort function. [t1], [t2] are trees of acells.
   acell_sortfn(atree_rootlabel t1,atree_rootlabel t2);

procedure atree_addchildip(tt,c);
   cddr tt := c . cddr tt;

procedure atree_addchildlistip(tt,cl);
   << cddr tt := nconc(cl,cddr tt); tt >>;
   
procedure atree_childlist(tt);
   cddr tt;

procedure atree_firstchild(tt);
   if atree_childlist tt then car atree_childlist tt;

procedure atree_levellabels(tt,n);
   if n eq 0 then
      {atree_rootlabel tt}
   else
      for each child in atree_childlist tt join
      	 atree_levellabels(child,n-1);

procedure atree_yield(tt);
   % Truth value yield labels. [tt] is a tree. Returns a list of ACELL.
   if null atree_childlist tt then
      {atree_rootlabel tt}
   else
      for each subtt in atree_childlist tt join atree_yield subtt;

procedure atree_tvyield(tt);
   % Truth value yield labels. [tt] is a tree. Returns a list of ACELL.
   if acell_gettv atree_rootlabel tt neq nil then % undef. tv: nil
      {atree_rootlabel tt}
   else
      for each subtt in atree_childlist tt join atree_tvyield subtt;

procedure atree_print(tt);
for each e in atree_print1(tt,0) do ioto_tprin2t e;

%% procedure atree_print1(tt,l2sfn);
%%    % acell_tvasstring atree_rootlabel dd;
%%    % atree_print1(tt,nil);
%%    % l2sfn is a function which converts a label to a string. returns a
%%    % list of strings.
%%    begin scalar childl, rootlabel;
%%       childl := atree_childlist(tt);
%%       rootlabel := atree_rootlabel(tt);
%%       if null childl then %return %{apply1(l2sfn,rootlabel)};
%%       return {acell_tvasstring rootlabel};
%%       return for each s in childl collect
%% 	 (acell_tvasstring rootlabel . ("---" . atree_print1(s,l2sfn)))
%%    end;

procedure atree_print1(tt,d);
   % atree_print(dd);
   % l2sfn is a function which converts a label to a string. returns a
   % list of strings. returns a list of list of strings.
   begin scalar childl,rootlabel,w;
      childl := atree_childlist(tt);
      rootlabel := atree_rootlabel(tt);
      if null childl then return {{acell_tvasstring rootlabel}};      
      w := for each s in childl join
	 for each l in atree_print1(s,d+1) collect (" " . "   " . l);
      caar w := acell_tvasstring rootlabel;
      cadar w := "---";
      return w
   end;

procedure atree_print1raw(tt,d);
   % returns a list of strings.
   begin scalar childl,w;
      childl := atree_childlist(tt);
      if null childl then return {{d}};      
      w := for each s in childl join
	 for each l in atree_print1raw(s,d+1) collect (" " . l);
      caar w := d;
      return w
   end;

procedure atree_printlin(tt,d);
   % print linear.
   if null atree_childlist tt then
      ioto_prin2 {"(",d,")"}
   else <<
      ioto_prin2 {"(",d};
      for each c in atree_childlist tt do atree_printlin(c,d+1);
      ioto_prin2 ")";
   >>;

procedure atree_2dot(tt);
   % Output tree as dot format. [tt] is an ATREE. Returns nil.
   begin
      out("cadtree.dot");
      ioto_prin2t("digraph cadtree {");
      atree_2dot1(tt,nil);
      ioto_prin2t("}");
      shut("cadtree.dot")
   end;

procedure atree_2dot1(tt,idx);
   begin scalar i,childlist,n,idxc;
      % Print basecell
      atree_prinnode idx; atree_2dotnodetail atree_rootlabel tt;
      i := 1;
      childlist := atree_childlist tt;
      n := length childlist;
      while i<=n do <<
	 idxc := append(idx,{i});
	 atree_prinnode idx; prin2 "->"; atree_prinnode idxc; ioto_prin2t "";
      	 atree_2dot1(car childlist,idxc);
	 childlist := cdr childlist;
	 i := i+1;
      >>;
   end;

procedure atree_prinnode(idx);
   % Print node. [idx] is a list of integers.
   begin scalar i;
      prin2 "C";
      for each i in idx do prin2 i;
   end;
   
procedure atree_2dotnodetail(c);
   % Print node detail. [c] is an acell. Return value is not interesting.
   if acell_gettv c eq 'true then
      ioto_prin2t(" [label=T shape=circle style=filled color=green]")
   else if acell_gettv c eq 'false then
      ioto_prin2t(" [label=F shape=circle style=filled color=red]")
   else
      ioto_prin2t(" [label=""-"" shape=circle style=filled color=grey]");
   
%procedure lto_take(l,n);
%   begin scalar rl; % result list
%      while n>0 do <<
%	 rl := car l . rl; l := cdr l; n := n-1;
%      >>;
%      return reversip rl
%   end;

% module acadextension;
% Andreas' CAD extension phase. Includes additional explicit base phase
% code, which is used for efficiency (factorization is possible).
% Extension code is also correct for the special case of the base
% phase.

procedure ofsf_mkvarl(f);
   % Make variable list. [f] is a prenex formula. Returns a dotted
   % pair [(vl . qal)], where [vl] corresponds to the free variables
   % and [qal] is a list of dotted pairs [x . Q], where [x] is a
   % kernel and [Q] the corresponding Quantifier. The first pair in
   % [qal] corresponds to the innermost variable plus quantifier
   begin scalar qal,vl;
      vl := cl_fvarl1 f;  % list of free variables
      while rl_quap rl_op f do <<
	 qal := (rl_var f . rl_op f) . qal;
	 vl := rl_var f . vl;
	 f := rl_mat f
      >>;
      return vl . qal
   end;
   	    
% module asolution;
% Andreas' solution formula construction for the decision case. That
% is, the solution formula is either [true] or [false].

procedure ofsf_solutionformula_old(cd);
   % old version which looks only at cells of level ddk 
   % ff is (F_1,...,Fr), dd is a cad tree
   begin scalar ff,dd,k,ddk,ffk,phiprime,cellstogo;
      if !*rlverbose then
	 ioto_tprin2t "+++ Solution Formula Construction Phase";
      ff := cdr vector2list caddata_ff cd;
      dd := caddata_dd cd;
      k := caddata_k cd;
      if k eq 0 then <<
	 caddata_putphiprime(cd,acell_gettv atree_rootlabel dd);
	 return nil
      >>;
      ddk := atree_levellabels(dd,k);
      %ffk := nth(ff,k);
      ffk := for i := 1 : k join nth(ff,i); %%% too much?
      % acdecl = (D_k,...,D_0). ff = (F_1,...,Fr)
      if !*rlverbose then
	 ioto_tprin2t {"+ generating signatures for ", length ffk,
	    " polynomials in ",length ddk," cells"};
      cellstogo := length ddk; % for verbose output
      for each cell in ddk do <<
	 acell_putdesc(cell,ofsf_signature(ffk,acell_getsp cell));
	 if ofsf_cadverbosep() then
	    ioto_prin2 {"[",cellstogo,%"idx",acell_getidx cell," ",
	       (if acell_gettv cell eq 'true then
	       " tt" else " ff"),%acell_getdesc cell,
	       "] "};
	 cellstogo := cellstogo-1;
      >>;
      phiprime := ofsf_sfchong(ffk,ddk);
      if phiprime eq 'ssfcfailed then <<
	 if !*rlverbose then
	 ioto_tprin2t "+ Solution formula construction ssfc failed. ";
	 return nil
      >> else <<
	 if !*rlverbose then
	    ioto_tprin2t "+ Solution formula construction ssfc successful.";
	 if !*rlcadrawformula then
	    caddata_putphiprime(cd,phiprime)
	 else
	    caddata_putphiprime(cd,rl_dnf phiprime)
      >>;
      return nil;	 
   end;

procedure ofsf_solutionformula(cd);
   % Solution formula construction. cd is CADDATA. Returns nil, the
   % argument is changed.
   % ff is (F_1,...,Fr), dd is a cad tree
   begin scalar ff,dd,k,yy,yyi,ffk,phiprime,cellstogo;
      if !*rlverbose then
	 ioto_tprin2t "+++ Solution Formula Construction Phase";
      ff := cdr vector2list caddata_ff cd;
      dd := caddata_dd cd;
      k := caddata_k cd;
      if memq(acell_gettv atree_rootlabel dd,{'true,'false}) then <<
	 if ofsf_cadverbosep() and caddata_ans cd then
	    ioto_prin2t {"ANSWERS for decision problem: ",
	      cdr atsoc('answers,acell_gettl atree_rootlabel dd)};
	 caddata_putphiprime(cd,acell_gettv atree_rootlabel dd);
	 return nil
      >>;
      %ddk := atree_levellabels(dd,k);
      yy := atree_tvyield(dd);
      % some verbose output for qe with answers...
      if ofsf_cadverbosep() and caddata_ans cd then <<
         ioto_prin2t "+++ ANSWERS: ";
	 ioto_prin2t {"yy: ",yy}; 
%      for each cell in yy do
%	 if acell_gettv cell eq 'true then
% 	    ioto_prin2t {"ANSWERS: ", atsoc('answers,acell_gettl cell)};
      	 for each c in list2set (for each cell in yy join 
             if acell_gettv cell eq 'true then 
                {atsoc('answers,acell_gettl cell)}) do 
         	   ioto_prin2t {"ANSWERS: ",c};
      >>;
      %%% ffk: "projection polynomials"
      yyi := list2set for each cell in yy collect length acell_getsp cell;
      if !*rlverbose then ioto_prin2t {"+ Levels to be considered: ",yyi};
      ffk := for each i in yyi join append(nth(ff,i),nil);
      phiprime := ofsf_solutionformula1(dd,ff,ffk,yy,yyi,k);
      if phiprime eq 'ssfcfailed then <<
	 if !*rlverbose then
	 ioto_tprin2t "++ SSFC failed, trying all possible projection factors.";
	 yyi := for i := 1 : k collect i;
	 if !*rlverbose then ioto_prin2t {"+ Levels to be considered: ",yyi};
	 ffk := for each i in yyi join append(nth(ff,i),nil);
	 phiprime := ofsf_solutionformula1(dd,ff,ffk,yy,yyi,k);
      >>;      
      if phiprime eq 'ssfcfailed then <<
	 if !*rlverbose then
	 ioto_tprin2t "++ Simple solution formula construction failed. ";
	 return nil
      >> else <<
	 if !*rlverbose then
	    ioto_tprin2t "++ Simple solution formula construction successful.";
	 if !*rlcadrawformula then
	    caddata_putphiprime(cd,phiprime)
	 else
	    caddata_putphiprime(cd,rl_dnf phiprime)
      >>;
      return nil;	 
   end;

procedure ofsf_solutionformula1(dd,ff,ffk,yy,yyi,k);
   begin scalar cellstogo;
      if !*rlverbose then
	 ioto_tprin2t {"+ generating signatures for ", length ffk,
	    " polynomials in ",length yy," cells; (Dk: ",
	    length atree_levellabels(dd,k)," cells)"};
      cellstogo := length yy; % for verbose output
      for each cell in yy do <<
	 acell_putdesc(cell,ofsf_signature4(ffk,acell_getsp cell));
	 if ofsf_cadverbosep() then
	    ioto_prin2 {"[",cellstogo,
	       %"idx",acell_getidx cell," ",%
	       "sig",acell_getdesc cell," ",
	       (if acell_gettv cell eq 'true then
	       " tt" else " ff"),%acell_getdesc cell,
	       "] "};
	 cellstogo := cellstogo-1;
      >>;
      return ofsf_ssfc2(ffk,yy);
   end;
      

procedure ofsf_sfchong(ffk,ddk);
   % Solution formula construction by Hong90. signatures are already
   % calculated.
   begin scalar wwt,wwf,wwc;
      wwt := ofsf_signaturesbytv(ddk,'true);
      wwf := ofsf_signaturesbytv(ddk,'false);
      wwc := intersection(wwt,wwf);
      if null wwc then
	 return rl_smkn('or,
	    for each sig in wwt collect
	       ofsf_sigbasedfo(ffk,sig))
      else
	 return 'ssfcfailed;
   end;

procedure ofsf_ssfc2(ffk,yy);
   % Solution formula construction by Hong90. signatures are already
   % calculated.
   begin scalar wwt,wwf,wwc;
      wwt := ofsf_signaturesbytv(yy,'true);
      wwf := ofsf_signaturesbytv(yy,'false);
      wwc := ofsf_compsig(wwt,wwf);
      if ofsf_cadverbosep() and wwc then
	 ioto_prin2t {"+ SSFC failed because of these compatible signatures: ",
	    wwc};
      if null wwc then
	 return rl_smkn('or,
	    for each sig in wwt collect
	       ofsf_sigbasedfo(ffk,sig))
      else
	 return 'ssfcfailed;
   end;

procedure ofsf_compsig(ww1,ww2);
   % Compatible signatures. ww1 and ww2 are lists of signatures; all
   % signatures have same length. Returns nil, if there are no
   % compatible signatures, (w1,w2), if w1 of ww1 is compatible to w2
   % of ww2.
   begin scalar ww2copy, retvalue;
      if null ww1 then return nil;
      while ww1 and not retvalue do <<
	 ww2copy := ww2;
	 while ww2copy and not retvalue do <<
	    if ofsf_compsig1(car ww1, car ww2copy) then
 	       retvalue := {car ww1, car ww2copy};
	    ww2copy := cdr ww2copy
	 >>;
	 ww1 := cdr ww1
      >>;
      return retvalue
   end;

procedure ofsf_compsig1(w1,w2);
   % compatible signatrues subroutine. w1, w2 are signatures of same
   % length; both should be not empty except for recursive calls.
   % Returns nil or t. Remark: this is a fold, the first element of
   % the disjunction is neutral wrt or for the base case.
   (null w1 or null w2) or
      ((car w1 eq car w2 or car w1 equal "?" or car w2 equal "?") and
 	 ofsf_compsig1(cdr w1,cdr w2));

procedure ofsf_sigbasedfo(ffk,sig);
   % signature based formula. ffk is a list of SF, sig is a signature
   % (can include "?"). returns a qf. FOF.
   begin scalar fo;
      for each f in ffk do <<
	 if not (car sig equal "?") then
	    fo := ofsf_signofpolyfo(f,car sig) . fo;
	 sig := cdr sig
      >>;
      return rl_smkn('and,fo)
   end;

procedure ofsf_signofpolyfo(sf,sigma);
   % sf is a standard form. sigma is in {-1,0,1}.
   if sigma eq -1 then
      ofsf_0mk2('lessp,sf)
   else if sigma eq 0 then
      ofsf_0mk2('equal,sf)
   else if sigma eq 1 then
      ofsf_0mk2('greaterp,sf);

procedure ofsf_evalqff(f,sp,idl);
   % Evaluate quantifier-free formula at sample point. [f] is a
   % quantifier-free OFSF formula; [sp] is a sample point. Returns
   % ['true] or ['false]. Returns the truth value of
   % $f(x_1,\ldots,x_r)$ at point [sp].
   if !*rlcadfasteval then
      ofsf_evalqff!-fast(f,sp,idl)
   else
      cl_simpl(cl_apply2ats1(f,function ofsf_subsignat,{sp,idl}),nil,-1);

%procedure ofsf_trialevalqff(psi,sp);
%   % [psi] is a qf fof, sp is a sample point. returns a simplified
%   % version of psi.
%   cl_apply2ats(psi,
%      function(lambda(x); ofsf_0mk2(ofsf_op x,reorder ofsf_arg2l x)));

procedure ofsf_evalsignf(f,s,idl);
   % Algebraic number evaluate sign of standard form at sample point.
   % f is a SF, s is a Samplepoint, idl is a list of ids. Returns a
   % SF.
   numr simp aex_sgn ofsf_subsp(aex_fromsf f,s,idl);

procedure ofsf_trialevalsgnf(f,sp);
   % Trial evaluation of sign of a sf at sample point. Returns a sf.
   % The sample point needs not to provide a number for each variable.
   <<
      f := ofsf_subsp!*(aex_fromsf f,sp);
      if aex_constp f then
	 numr simp aex_sgn f
      else
	 ratpoly_tosf aex_ex f
   >>;

procedure ofsf_sgnf4(f,sp);
  <<
      f := ofsf_subsp!*(aex_fromsf f,sp);
      if aex_constp f then
	 aex_sgn f
      else
	 "?"
   >>;

procedure ratpoly_tosf(rp);
   % warning: denominator might be negative.
   << %if abs denr rp neq 1 then prin2t "***** ratpolx_tosf: invalid argument";
      if sgn denr rp eq 1 then numr rp
      else multf((-1),numr rp)
   >>;

procedure ofsf_subsignat(at,sp,idl);
   % Substitute sign in atomic formula. [at] is an OFSF atomic
   % formula; [sp] is a sample point. Returns an OFSF atomic formula.
   % Returns [at] with the left-hand side $f$ replaced by the sign of
   % $f([sp])$, or a simpler version of f under the context of sp.
   ofsf_0mk2(ofsf_op at,ofsf_evalsignf(ofsf_arg2l at,sp,idl));

procedure ofsf_evalqff!-fast(f,sp,idl);
   % Evaluate quantifier-free formula at sample point fastly. [f] is a
   % quantifier-free OFSF formula in NNF; [sp] is a sample point;
   % [idl] is a LIST of identifiers. Returns ['true] or ['false].
   % Returns the truth value of $f(x_1,\ldots,x_r)$ at point [sp].
   if cl_atfp f then
      ofsf_simplat1(ofsf_subsignat(f,sp,idl),nil)
   else
      ofsf_evalqff!-fast1(f,sp,idl);

procedure ofsf_evalqff!-fast1(f,sp,idl);
   % Evaluate quantifier-free formula at sample point fastly
   % subroutine. [f] is a quantifier-free OFSF formula in NNF; [sp] is
   % a sample point; [idl] is a LIST of identifiers. Returns ['true]
   % or ['false]. Returns the truth value of $f(x_1,\ldots,x_r)$ at
   % point [sp].
   if rl_tvalp f then
      f
   else
      ofsf_evalqff!-gand(rl_op f,rl_argn f,sp,idl);

procedure ofsf_evalqff!-gand(gand,argl,sp,idl);
   % Evaluate quantifier-free formula at sample point generic and.
   % [gand] is one of [and], [or]; [argl] is a list of formulas; [sp]
   % is a sample point; [idl] is a LIST of identifiers. Returns
   % ['true] or ['false]. Returns the truth value of $([gand] .
   % [argl])(x_1,\ldots,x_r)$ at point [sp].
   begin scalar gfalse,arg,cargl,c;
      gfalse := cl_cflip('false,gand eq 'and);
      c := t; while argl and c do <<
	 arg := car argl;
	 argl := cdr argl;
	 if cl_atfp arg then
	    (if ofsf_simplat1(ofsf_subsignat(arg,sp,idl),nil) eq gfalse then
	       c := nil)
	 else
	    cargl := arg . cargl
      >>;
      if not c then
	 return gfalse;
      c := t; while cargl and c do <<
	 arg := car cargl;
	 cargl := cdr cargl;
	 if ofsf_evalqff!-fast1(arg,sp,idl) eq gfalse then
	    c := nil
      >>;
      if not c then
	 return gfalse;
      return cl_flip gfalse
   end;

procedure ofsf_signature(fk,sp);
   % signature. [fk] is a list of standard forms (in, say, variables
   % x1,...,xk), [sp] is sample point (a list (ak,...,a1) of algebraic
   % numbers. returns a list (sigma1,...,sigmak) of elements of
   % (-1,0,1}.
   for each f in fk collect
      aex_sgn ofsf_subsp!*(aex_fromsf f,sp);

procedure ofsf_signature4(fk,sp);
   % signature. [fk] is a list of standard forms (in, say, variables
   % x1,...,xk), [sp] is sample point (a list (al,...,a1) of algebraic
   % numbers with l <= k). returns a list (sigma1,...,sigmak) of
   % elements of (-1,0,1,?).
   for each f in fk collect
      ofsf_sgnf4(f,sp);

procedure ofsf_signaturesbytv(ddk,tv);
   % Signatures by truth value. [ddk] is a list of cells.
   for each c in ddk join
      if acell_gettv c eq tv then {acell_getdesc c};

%%% unused
procedure ofsf_sortsignatures(sl);
   %[sl] is a non-empty list of signatures.
   % {{0,1},{0,0},{1,1},{-1,1},{0,-1}}
   <<
      for i := length car sl step -1 until 1 do
      	 sl := append(ofsf_s1(sl,i,-1),append(ofsf_s1(sl,i,0),ofsf_s1(sl,i,1)));
      sl
   >>;

%%% unused
procedure lto_orderedlist2set(s);
   begin scalar cs;
      if null s or null cdr s then
      	 return s
      else <<
      	 cs := lto_orderedlist2set cdr s;
      	 if car s eq car cs then return cs else return car s . cs
      >>
   end;

procedure ofsf_s1(sl,n,sign);
   % picks the signatures from [sl], where the [n]th element equals [sign].
   for each s in sl join if nth(s,n) eq sign then {s};

%%% --- Types --- %%%

lisp procedure type_checking(ob,tp); type_checking1(ob,tp,"");

lisp procedure type_checking1(ob,tp,str);
   % Type checking. [ob] is anything, [tp] is a type, [str] is
   % anything. Return value is not used.
   if not type_check(ob,tp) then
      rederr {str,":",ob,"does not match type """,tp,""""};

lisp procedure type_check(ob,tp);
   % Type check. [ob] is anything, [tp] is a type. Returns a truth value.
   begin
      %if not type_typep tp then rederr {"type_check:",tp,"is not a type"};
      % atomic types
      if tp eq 'type then return type_typep ob;
      type_checking1(tp,'type,"type_check");
      if tp eq 'any then return t;
      if tp eq 'num then return numberp ob;
      if tp eq 'atom then return atom ob;
      if tp eq 'id then return idp ob;
      if tp eq 'float then return floatp ob;
      if tp eq 'string then return stringp ob;
      if tp eq 'sf then return type_sfp ob;
      if tp eq 'sq then return type_sqp ob;
      % algebraic types
      if atom tp then rederr "type_check: unknown atomic type specified";
      if car tp eq 'pair then
	 return pairp ob and type_check(car ob,cadr tp) and
	 type_check(cdr ob,caddr tp);
      if car tp eq 'list then
	 return type_listp ob and type_checkl1(ob,cadr tp);
      if car tp eq 'mat then
	 return type_check(ob,{'list,{'list,cadr tp}});
      if car tp eq 'sum then
	 return type_check1l(ob,cdr tp);      
   end;

lisp procedure type_s2a(ob,tp);
   % Symbolic to algebraic mode conversion wrt. a type. [ob] is
   % anything, [tp] is a type. Returns a truth value.
   begin
      %type_checking1(ob,tp,"type_s2a");
      % atomic type
      if tp memq '(type any num atom id) then return ob;
      if tp eq 'sf then return prep ob;
      if tp eq 'sq then return {'quotient,prep numr ob,prep denr ob};
      if tp eq 'fof then return rl_prep ob;
      % algebraic types
      if atom tp then rederr {"type_s2a: unknown atomic type",tp};
      if car tp eq 'pair then % make a two-element list
	 return {'list,type_s2a(car ob,cadr tp),type_s2a(cdr ob,caddr tp)};
      if car tp eq 'list then
	 return 'list . for each o in ob collect type_s2a(o,cadr tp);
      if car tp eq 'mat then
	 return 'mat . for each l in ob collect
	    for each c in l collect type_s2a(c,cadr tp);
      if car tp eq 'sum then
	 return type_s2a!-1l(ob,cdr tp);      
   end;

% type_s2a ( 1 . 2 ,'(pair num num));
% type_s2a ( {{1},{},{3,4}} ,'(list (list num)));
% type_s2a ( {1,2,{3,4}} ,'(list (sum num (list num))));

lisp procedure type_s2a!-1l(ob,tpl);
   % Symbolic to algebraic mode conversion subroutine. [ob] is any,
   % [tpl] is (list type).
   if null tpl then rederr "type_s2a!-1l: found no matching type"
   else if type_check(ob,car tpl) then type_s2a(ob,car tpl)
   else type_s2a!-1l(ob,cdr tpl);

lisp procedure type_a2s(ob,tp);
   % Algebraic to symbolic mode conversion wrt. a type. [ob] is
   % anything, [tp] is a type. Returns a truth value.
   begin
      % atomic type
      if tp memq '(type any num atom id) then return ob;
      if tp eq 'sf then return numr simp ob; % optional: check denr = 1
      if tp eq 'sq then return simp ob;
      if tp eq 'fof then return rl_simp ob;
      % algebraic types
      if atom tp then rederr {"type_a2s: unknown atomic type",tp};
      if car tp eq 'pair then % make pair from two-element list
	 return type_a2s(cadr ob,cadr tp) . type_a2s(caddr ob,caddr tp);
      if car tp eq 'list then
	 return for each o in cdr ob collect type_a2s(o,cadr tp);
      if car tp eq 'mat then
	 return for each l in cdr ob collect
	    for each c in l collect type_a2s(c,cadr tp);
%      if car tp eq 'sum then
%	 return type_a2s!-1l(ob,cdr tp);
      rederr {"type_a2s: unknown or yet unsupported type",tp};
   end;

% type_a2s('(quotient 1 2),'sq);
% type_a2s('(list 1 (quotient 1 2)),'(pair num sq));
% type_a2s('(list 1 2 3 4),'(list num));
% type_a2s('(list (list 1) (list) (list 3 4)),'(list (list num)));

lisp procedure type_checkl1(obl,tp);
   % Type check subroutine. [obl] is (list any), [tp] is a type.
   % Returns a truth value.
   null obl or (type_check(car obl,tp) and type_checkl1(cdr obl,tp));

lisp procedure type_check1l(ob,tpl);
   % Type check subroutine. [ob] is any, [tpl] is (list type). 
   % Returns a truth value.
    not null tpl and (type_check(ob,car tpl) or type_check1l(ob,cdr tpl));

lisp procedure type_typep(a);
   % Type predicate. [a] is anything. Returns a truth value.
   begin
      if a memq '(type any num atom id sf sq) then return t;
      if not pairp a then return nil;
      if car a eq 'pair and type_listp a and length a=3 and type_typep1 cdr a
	 then return t;
      if car a eq 'list and type_listp a and length a=2 and type_typep1 cdr a
	 then return t;
      if car a eq 'mat and type_listp a and length a=2 and type_typep1 cdr a
	 then return t;
      if car a eq 'sum and type_listp a and length a>1 and type_typep1 cdr a
	 then return t;
      % ...
      return nil; % sure, unnecessary
   end;

lisp procedure type_typep1(al);
   null al or (type_typep car al and type_typep1 cdr al);

% type_typep 'any;
%type_typep '(sum any);

lisp procedure type_listp(a);
   % List predicate. [a] is anything. Returns a truth value.
   null a or (pairp a and type_listp cdr a); 

procedure type_sfp(f);
   % Standard form with an identifier as a kernel predicate. [f] is
   % anything. Returns a truth value.
   null f or domainp f or
      (pairp f and type_sfp cdr f and
	 pairp car f and type_sfp cdar f and
	    pairp caar f and idp caaar f and numberp cdaar f);

procedure type_sqp(q);
   % Standard quotient with an identifier as a kernel predicate. [f]
   % is anything. Returns a truth value.
   pairp q and type_sfp car q and type_sfp cdr q;

% type_listp {1,2,3};
% type_check({1,2,3},'(list num));
% type_check({1,2,3},'(list any));
% type_check(5,'(sum id num iv));
% type_check(5,'(sum id (list any)));
% type_check({1,'x},'(list (sum id num)));
% type_check({{1,2},{3,4}},'(mat num));

endmodule;   % asolution

end;  % of file
