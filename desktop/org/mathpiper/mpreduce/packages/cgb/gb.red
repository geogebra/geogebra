% ----------------------------------------------------------------------
% $Id: gb.red 535 2010-01-28 12:25:54Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1999-2009 Andreas Dolzmann and Thomas Sturm
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
   fluid '(gb_rcsid!* gb_copyright!*);
   gb_rcsid!* := "$Id: gb.red 535 2010-01-28 12:25:54Z thomas-sturm $";
   gb_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

module gb;

load!-package 'ezgcd;

switch gbltbasis,groebopt,cgbstat,cgbfullred,cgbverbose,cgbcontred,
   cgbcounthf,cgbcheckg;

fluid '(!*gltbasis !*groebopt !*cgbstat !*cgbfullred !*cgbverbose
   !*cgbcontred !*cgbcounthf !*cgbcheckg !*cgbsloppy !*cgbsugar);

off1 'cgbstat;
on1 'cgbfullred;
off1 'cgbverbose;
off1 'cgbcontred;
off1 'cgbcounthf;
off1 'cgbcheckg;

!*cgbsloppy := T;
!*cgbsugar := nil;  % Indicator for using sugar property of VDP's.
		    % This is on for gb computation and off for using
		    % the code from external procedures. Do not
		    % intermix this with !*gsugar from the groebner
		    % package. if !*cgbsugar is nil then the all
		    % sugars are treated as zero.

switch cgbupdb;
fluid '(cgb_updbcount!* cgb_updbcountp!* cgb_updbcalls!* !*cgbupdb);
off1 'cgbupdb;

fluid '(intvdpvars!*);

fluid '(!*gcd !*ezgcd !*factor !*exp dmode!* depl!* !*backtrace);

fluid '(secondvalue!* thirdvalue!*);

fluid '(dip_vars!* vdp_pcount!*);

fluid '(cgb_hcount!* cgb_hzerocount!* cgb_tr1count!* cgb_tr2count!*
   cgb_tr3count!* cgb_b4count!* cgb_strangecount!* cgb_paircount!*
   cgb_hfaccount!* cgb_gcount!* cgb_gstat!* cgb_gbcount!*);

global '(gvarslast gltb);

gltb := {'list};

fluid '(cgb_contcount!* cgb_mincontred!*);
cgb_mincontred!* := 20;  % originally 10

% Generate the following interfaces for AM:
% - gb: List of Polyomials -> List of Polynomials
% - gbgsys: List of Polyomials -> Groebner System
% - reduce: Polynomial X List of Polynomials -> Polynomial
% - ltb: List of Polynomials -> List of Terms (as Polynomials)
% and the following interfaces for the SM:
% - gbf,gsysf,reducef,ltbf. All polynomials are represented as SF's,
% except the return valute of reducef, which is an SQ. All Procedures
% expect three dditional paramters: List of variable, term order, and
% additional arguments to term order.

macro procedure gb_mkinterface(argl);
   begin
      scalar a2sl1,a2sl2,defl,xvfn,s2a,s2s,s,modes,
	 args,bname,len,sm,prgn,ami,smi,psval,postfix;
      bname := eval nth(argl,2);
      a2sl1 := eval nth(argl,3);
      a2sl2 := eval nth(argl,4);
      defl := eval nth(argl,5);
      xvfn := eval nth(argl,6);
      s2a := eval nth(argl,7);
      s2s := eval nth(argl,8);
      s := eval nth(argl,9);
      postfix := eval nth(argl,10);
      modes := eval nth(argl,11);      
      len := length a2sl1;
      args := for i := 1:len+3 collect mkid('a,i);
      sm := intern compress append('(!g !b !_),explode bname);
      % Define the symbolic mode interface
      if (null modes or modes eq 'sm) then <<
      	 smi := intern compress nconc(explode sm,explode postfix);      
      	 prgn := {'put,mkquote smi,''number!-of!-args,len+3} . prgn;
      	 prgn := {'de,smi,args,{'gb_interface!$,mkquote sm, mkquote a2sl1,
	    mkquote a2sl2,mkquote defl,mkquote xvfn,mkquote s2a,mkquote s2s,
	    mkquote s,T,'list . args}} . prgn
      >>;
      if (null modes or modes eq 'am) then <<
      	 % Define the algebraic mode interface      
      	 ami := bname;
      	 %      ami := intern compress append('(!g !b),explode bname);
      	 psval := intern compress nconc(explode ami,'(!! !$));
      	 prgn := {'put,mkquote ami,''psopfn,mkquote psval} . prgn;
      	 prgn := {'put,mkquote psval,''number!-of!-args,1} . prgn;
      	 prgn := {'put,mkquote psval,''cleanupfn,''gb_cleanup} . prgn;
      	 prgn := {'de,psval,'(argl),{'gb_interface!$,mkquote sm, mkquote a2sl1,
	    mkquote a2sl2,mkquote defl,mkquote xvfn,mkquote s2a, mkquote s2s,
	    mkquote s,nil,'argl}} . prgn
      >>;
      return 'progn . prgn
   end;

gb_mkinterface('gb,'(gb_a2s!-psys),'(gb_a2s2!-psys),
   nil,'gb_xvars!-psys,'gb_s2a!-gbx,'gb_s2s!-gb,T,'f,nil);

%% gb_mkinterface('gbgsys,'(gb_a2s!-psys),'(gb_a2s2!-psys),
%%    nil,'gb_xvars!-psys,'gb_s2a!-gsys,'gb_s2s!-gsys,T,'f,nil);

gb_mkinterface('reduce,'(gb_a2s!-pol gb_a2s!-psys),
   '(gb_a2s2!-pol gb_a2s2!-psys),
   nil,'gb_xvars!-ppsys,'gb_s2a!-pol,'gb_s2s!-pol,T,'f,nil);

gb_mkinterface('ltb,'(gb_a2s!-psys),'(gb_a2s2!-psys),
   nil,'gb_xvars!-psys,'gb_s2a!-gb,'gb_s2s!-gb,T,'f,nil);

procedure gb_a2s!-psys(l);
   % Groebner bases algebraic mode to symbolic mode polynomial system.
   % [l] is an AMPSYS. Returns an FPSYS.
   begin scalar w,resl;
      for each j in getrlist reval l do <<
      	 w := numr simp j;
	 if w and not(w member resl) then
	    resl := w . resl
      >>;
      return sort(resl,'ordp)
   end;

procedure gb_a2s2!-psys(fl);
   for each x in fl collect vdp_f2vdp x;

procedure gb_s2a!-gb(u);
   % Groebner bases symbolic mode to algebraic mode GB. [u] is a list
   % of CGP's. Returns an AMPSYS.
   'list . for each x in u collect vdp_2a x;

procedure gb_s2a!-gbx(l);
   % symbolic to algebraic mode groebner base extended version. [l] is
   % a list of VDP's. Returns an AM object. This procedure sets as a
   % side effet the global variable gltb provided !*gltbasis is on.
   <<
      if !*gltbasis then
      	 gltb := gb_gb2gltb l;      
      gb_s2a!-gb l
   >>;

procedure gb_s2s!-gb(l);
   gb_gb!-sfl l;

procedure gb_s2a!-gsys(l);
   'list . for each x in l collect
      {'list,rl_mk!*fof rl_smkn('and,car x),gb_s2a!-gb cadr x};

procedure gb_s2s!-gsys(l);
   for each x in l collect
      {rl_smkn('and,car x),gb_s2s!-gb cadr x};

procedure gb_a2s!-pol(p);
   numr simp reval p;

procedure gb_a2s2!-pol(p);
   vdp_f2vdp p;

procedure gb_s2a!-pol(p);
   vdp_2a p;

procedure gb_s2s!-pol(p);
   vdp_2sq p;

procedure gb_dummy1(dummy);
   nil;

procedure gb_xvars!-psys(l,vl);
   gb_vars(l,vl);

procedure gb_xvars!-psys2(l,cd,vl);
   gb_vars(l,vl);

procedure gb_xvars!-psys3(l,cd,xvarl,vl);
   gb_vars(l,vl);

procedure gb_xvars!-ppsys(p,l,vl);
   gb_vars(p . l,vl);

procedure gb_cleanup(u,v);
   u;

procedure gb_interface!$(fname,a2sl1,a2sl2,defl,xvfn,s2a,s2s,s,smp,argl);
   % fname is a function, the name of the procedure to be called;
   % [a2sl1] and [as2sl2] are a list of functions, called to be
   % transform algebraic arguments to symbolic arguments; [defl] is a
   % list of algebraic defualt arguments; xvfn is a procedure for
   % extracting the variables from all arguments; [s2a] is procedure
   % for transforming the symbolic return value to an algebraic mode
   % return value; [argl] is the list of arguments; [s] is a flag;
   % [smp] is a flag. Return an S-expr. If [s] is on then second stage
   % of argument processing is done with the results of the first one.
   begin scalar w,vl,nargl,oenv,m,c,x;
      if not smp then <<
	 nargl := gb_am!-pargl(fname,a2sl1,argl,defl);
	 vl := apply(xvfn,append(nargl,{td_vars()}));
	 oenv := vdp_init(car vl,td_sortmode(),td_sortextension());
      	 gvarslast := 'list . car vl;      
      >> else <<
	 w := gb_sm!-pargl(argl);
	 nargl := car w;
	 m := cadr w;
	 c := caddr w;
	 x := cadddr w;
	 vl := apply(xvfn,append(nargl,{m}));
	 oenv := vdp_init(car vl,c,x);	       
      >>;	    
      w := errorset({'gb_interface1!$,
	 mkquote fname,mkquote a2sl2,mkquote s2a,mkquote s2s,mkquote s,
	 mkquote smp,mkquote argl, mkquote nargl,mkquote car vl,
	 mkquote cdr vl},T,!*backtrace);
      vdp_cleanup oenv;
      if errorp w then
      	 rederr {"Error during ",fname};
      return car w
   end;

procedure gb_sm!-pargl(argl);
   begin scalar nargl,m,c,x;
      nargl := reverse argl;
      x := car nargl;
      nargl := cdr nargl;
      c := car nargl;
      nargl := cdr nargl;
      m := car nargl;
      nargl := cdr nargl;
      nargl := reversip nargl;
      return {nargl,m,c,x}
   end;

procedure gb_am!-pargl(fname,a2sl1,argl,defl);
   % process argument list for algebraic mode.
   begin integer l1,l2,l3; scalar w,nargl,scargl,scdefl;
      l1 := length argl;
      l2 := length a2sl1;
      l3 := l2 - length defl;
      if l1 < l3 or l1 > l2 then
	 rederr {fname,"called with",l1,"arguments instead of",l3,"-",l2};
      scargl := argl;
      scdefl := defl;
      nargl := for each x in a2sl1 collect <<
	 if scargl then <<
	    w := car scargl;
	    scargl := cdr scargl
	 >> else <<
	    w := car scdefl;
	    scdefl := cdr scdefl
	 >>;
	 apply(x,{w})
      >>;
      return nargl
   end;

procedure gb_interface1!$(fname,a2sl2,s2a,s2s,s,smp,argl,nargl,m,p);
   begin scalar w,pl;
      pl := if s then nargl else argl;
      argl := for each x in a2sl2 collect <<
	 w := car pl;
	 pl := cdr pl;
	 apply(x,{w})
      >>;
%      w := apply(fname,nconc(argl,{m,p}));
      w := apply(fname,argl);
      w := if smp then
	 apply(s2s,{w})
      else
      	 apply(s2a,{w});
      return w
   end;

smacro procedure gb_tt(s1,s2);
   % lcm of leading terms of s1 and s2
   ev_lcm(vdp_evlmon s1,vdp_evlmon s2);

procedure gb_gb!-sfl(u);
   % Groebner bases GB to SF list. [u] is a list of CGP's. Returns a
   % list of SF's.
   for each p in u collect vdp_2f p;

procedure gb_domainchk();
   % Groebner bases domain check. No argument. Return value not
   % defined. Raises an error if the current domain is not valid for
   % GB computations.
   if not memq(dmode!*,'(nil)) then
      rederr bldmsg("gb does not support domain: %w",get(dmode!*,'dname));
      
procedure gb_vars(l,vl);     %DROPPED: depend,rules,zero divisors.
   % Groebner bases variables. [l] is a list of SF's; [vl] is the list
   % of main variables. Returns a pair $(m . p)$ where $m$ and $p$ are
   % list of variables. $m$ is the list of used main variables and $p$
   % is the list of used parameters. 
   begin scalar w,m,p;
      for each f in l do
	 w := union(w,kernels f);
      if vl then <<
      	 m := gb_intersection(vl,w);
      	 p := setdiff(w,vl)
      >> else
	 m := w;
      return gb_varsopt(l,m) . p
   end;

procedure gb_intersection(a,b);
   % Groebner bases intersection. [a] and [b] are lists. Returns a
   % list. The returned list contains all elements occuring in [a] and
   % in [b]. The order of the elements is the same as in [a].
   for each x in a join
      if x member b then
	 {x};

procedure gb_varsopt(l,vl);
   % Groebner bases variables optimize. [l] is a list of SF's; [vl] is
   % the list of main variables. Returns a possibly reorderd list of
   % main variables.
   if !*groebopt and td_sortmode() memq '(lex gradlex revgradlex) then
      gb_vdpvordopt(l,vl)
   else
      vl;

procedure gb_gb2gltb(base);
   'list . for each j in base collect
      vdp_2a vdp_fmon(bc_a2bc 1,vdp_evlmon j);

procedure gb_ltb(l);
   for each p in l collect
      vdp_fmon(bc_a2bc 1,vdp_evlmon p);

procedure gb_gbggsys0(p,dummy,dummy);
   gb_gbgsys p;

procedure gb_gbgsys0(p,dummy);
   gb_gbgsys p;

procedure gb_gbgsys(p);
   {{nil,gb_gb p,nil}};

procedure gb_gb0(p,dummy);
   gb_gb p;
   
procedure gb_gb(p);
   begin scalar spac,p1,savetime,!*factor,!*exp,intvdpvars!*,!*gcd,!*ezgcd,
	 dip_vars!*,secondvalue!*,thirdvalue!*,cgb_gstat!*,!*cgbsugar;
      integer vdp_pcount!*,cgb_contcount!*,cgb_hcount!*,cgb_hzerocount!*,
	 cgb_tr1count!*,cgb_tr2count!*,cgb_tr3count!*,cgb_b4count!*,
	 cgb_strangecount!*,cgb_paircount!*,cgb_hfaccount!*,cgb_gcount!*,
	 cgb_gbcount!*,cgb_updbcount!*,cgb_updbcountp!*,cgb_updbcalls!*;
      !*exp := !*gcd := !*ezgcd := T;
      !*cgbsugar := T;
      if !*cgbstat then
      	 savetime := time();
      if !*cgbcheckg then	 
      	 cgb_gstat!* := nil;
      cgb_contcount!* := cgb_mincontred!*;
      if !*cgbstat then
      	 spac := gctime();
      p1 := if !*cgbupdb then
 	 gb_traverso!-sturm!-experimental p
      else
 	 gb_traverso p;
      if !*cgbstat then <<
	 ioto_tprin2t "Statistics for GB computation:";
	 ioto_prin2t {"Time: ",time() - savetime," ms plus GC time: ",
	    gctime() - spac," ms"};
	 ioto_prin2t {"H-polynomials total: ",cgb_hcount!*};
	 ioto_prin2t {"H-polynomials zero: ",cgb_hzerocount!*};
	 if !*cgbcounthf then
	    ioto_prin2t {"H-polynomials reducible: ",cgb_hfaccount!*};
	 if !*cgbcheckg then
	    ioto_prin2t {"H-polynomials gaussible: ",cgb_gcount!*,
	       " ",cgb_gstat!*};
	 ioto_prin2t {"Crit Tr1 hits: ",cgb_tr1count!*};
	 ioto_prin2t {"Crit B4 hits: ",cgb_b4count!*," (Buchberger 1)"};
	 ioto_prin2t {"Crit Tr2 hits: ",cgb_tr2count!*};
	 ioto_prin2t {"Crit Tr3 hits: ",cgb_tr3count!*};
	 if !*cgbupdb then
	    ioto_prin2t {"updbase: calls ",cgb_updbcalls!*,", del ",
	       cgb_updbcountp!*,"/",cgb_updbcount!*};
	 ioto_prin2t {"Strange reductions: ",cgb_strangecount!*}
      >>;
      return p1
   end;

procedure gb_traverso!-sturm!-experimental(g0);
   begin scalar gall,g,d,s,h,p;
      g0 := for each fj in g0 join
	 if not vdp_zero!? fj then
	    {vdp_setsugar(vdp_enumerate vdp_simpcont fj,vdp_tdeg fj)};
      for each h in g0 do <<  % create initial critical pairs
	 p := {nil,h,h};
      	 h := gb_enumerate h;
      	 d := gb_traverso!-pairlist(h,g,d);
	 g := nconc(g,{h});
	 gall := nconc(gall,{h});
      >>;
      while d do <<  % critical pairs left
	 if !*cgbverbose then <<
	    ioto_prin2 {"[",cgb_paircount!*,"] "};
	    cgb_paircount!* := cgb_paircount!* #- 1
	 >>;
	 p := car d;
	 d := cdr d;
	 s := gb_spolynomial(p);
	 h := gb_simpcontnormalform gb_normalform(s,gall);
	 cgb_hcount!* := cgb_hcount!* #+ 1;
	 if vdp_zero!? h then <<
	    cgb_hzerocount!* := cgb_hzerocount!* #+ 1;
	 >> else if ev_zero!? vdp_evlmon h then <<  % base 1 found
	    h := gb_enumerate h;
	    d := nil;
	    g := {h}
	 >> else <<
	    if !*cgbcounthf then
	       if cddr fctrf vdp_2f h then
		  cgb_hfaccount!* := cgb_hfaccount!* #+ 1;
      	    h := gb_enumerate h;
      	    d := gb_traverso!-pairlist(h,g,d);
      	    gall := gb_updbase(gall,h);
      	    g := nconc(g,{h})
	 >>
      >>;
      return gb_traverso!-final g
   end;

procedure gb_traverso(g0);
   begin scalar g,d,s,h,p,gstat;
      g0 := for each fj in g0 join
	 if not vdp_zero!? fj then
	    {vdp_setsugar(vdp_enumerate vdp_simpcont fj,vdp_tdeg fj)};
      for each h in g0 do <<  % create initial critical pairs
	 p := {nil,h,h};
      	 h := gb_enumerate h;
      	 d := gb_traverso!-pairlist(h,g,d);
	 g := nconc(g,{h})
      >>;
      if !*cgbverbose then
	 cgb_gbcount!* := length g;
      while d do <<  % critical pairs left
	 if !*cgbverbose then <<
	    ioto_prin2 {"[",cgb_paircount!*,"] "};
	    cgb_paircount!* := cgb_paircount!* #- 1
	 >>;
	 p := car d;
	 d := cdr d;
	 s := gb_spolynomial(p);
	 h := gb_simpcontnormalform gb_normalform(s,g);
	 if !*cgbstat then
	    cgb_hcount!* := cgb_hcount!* #+ 1;
	 if vdp_zero!? h then
	    (if !*cgbstat then
	       cgb_hzerocount!* := cgb_hzerocount!* #+ 1)	    
	 else if vdp_unit!? h then <<
	    h := gb_enumerate h;
	    d := nil;
	    g := {h}
	 >> else <<
	    if !*cgbcounthf then
	       if cddr fctrf vdp_2f h then
		  cgb_hfaccount!* := cgb_hfaccount!* #+ 1;
	    if !*cgbcheckg then <<
	       gstat := gb_chkgauss vdp_poly h;
	       if 1 member gstat then <<
		  cgb_gcount!* := cgb_gcount!* #+ 1;
	       	  cgb_gstat!* := gb_chkgauss!-stat2vl gstat . cgb_gstat!*
	       >>
	    >>;
      	    h := gb_enumerate h;
      	    d := gb_traverso!-pairlist(h,g,d);
      	    g := nconc(g,{h});
	    if !*cgbverbose then
	       cgb_gbcount!* := cgb_gbcount!* #+ 1
	 >>
      >>;
      return gb_traverso!-final g
   end;

procedure gb_updbase(g,h);
   begin scalar hev,oc;
      if !*cgbstat then
      	 oc := cgb_updbcountp!*;
      hev := vdp_evlmon h;
      g := for each p in g join
      	 if not ev_divides!?(hev,vdp_evlmon p) then
 	    {p}
	 else <<
	    if !*cgbverbose then
	       ioto_prin2 "#";
	    if !*cgbstat then
	       cgb_updbcountp!* := cgb_updbcountp!* #+ 1;
	    nil
	 >>;
      if !*cgbstat then <<
      	 if not (oc #= cgb_updbcountp!*) then
	    cgb_updbcount!* := cgb_updbcount!* #+ 1;
	 cgb_updbcalls!* := cgb_updbcalls!* + 1
      >>;
      return nconc(g,{h})
   end;

procedure gb_chkgauss(p);
   % [p] is a dipoly.
   begin scalar stat;
      stat := for each x in dip_vars!* collect 0;  %TODO: Reference to global var
      while p do <<
	 stat := gb_chkgauss1(dip_evlmon p,stat);
   	 p := dip_mred p
      >>;
      return stat
   end;

procedure gb_chkgauss1(ev,stat);
   begin scalar nstat,e,s,td;
      td := ev_tdeg ev;
      if td = 0 then
	 return stat;
      td := if td #= 1 then 1 else -1;
      while ev do <<
	 e := car ev;
	 ev := cdr ev;
	 s := car stat;
	 stat := cdr stat;
	 if e #=0 then
	    nstat := s . nstat
	 else if e #> 1 or s #= -1 then
	    nstat := (-1) . nstat
	 else if e #= 1 and s #= 0 then
	    nstat := td . nstat
	 else if s #=0 and e #=0 then
	    nstat := 0 . nstat
	 else
	    rederr "ich sehe es anders"
      >>;
      return reversip nstat
   end;

procedure gb_chkgauss!-stat2vl(gstat);
   begin scalar scdv,r;
      scdv := dip_vars!*;
      while gstat do <<
	 if eqcar(gstat,1) then
 	    r := car scdv . r;
	 gstat := cdr gstat;
	 scdv := cdr scdv
      >>;
      return reversip r
   end;

procedure gb_enumerate(f);
   % f is a temporary result. Prepare it for medium range storage, and
   % assign a number.
   if vdp_zero!? f then
      f
   else <<
      vdp_condense f;
      if vdp_number f #= 0 then
	 f := vdp_setnumber(f,vdp_pcount!* := vdp_pcount!* #+ 1);
      f
   >>;

procedure gb_traverso!-pairlist(gk,g,d);
   % gk: new polynomial, g: current basis, d: old pair list.
   begin scalar ev,r,n;
      d := gb_traverso!-pairs!-discard1(gk,d);
      % build new pair list:
      ev := vdp_evlmon gk;
      for each p in g do
     	 if not gb_buchcrit4t(ev,vdp_evlmon p) then <<
	    if !*cgbstat then
	       cgb_b4count!* := cgb_b4count!* #+ 1;
 	    r := ev_lcm(ev,vdp_evlmon p) . r
       	 >> else
 	    n := gb_makepair(p,gk) . n;
      n := gb_tr2crit(n,r);
      n := gb_cplistsort(n,!*cgbsloppy);
      n := gb_tr3crit n;
      if !*cgbverbose and n then <<
	 cgb_paircount!* := cgb_paircount!* #+ length n;
	 ioto_cterpri();
	 ioto_prin2 {"(",cgb_gbcount!*,") "}
      >>;
      return gb_cplistmerge(d,reversip n)
   end;

procedure gb_tr2crit(n,r);
   % delete equivalents to coprime lcm
   for each p in n join 
      if ev_member(car p,r) then <<
	 if !*cgbstat then
	    cgb_tr2count!* := cgb_tr2count!* #+ 1;
	 nil
      >> else
	 {p};

procedure gb_tr3crit(n);
   begin scalar newn,scannewn,q;
      for each p in n do <<
	 scannewn := newn;
	 q := nil;
 	 while scannewn do
	    if ev_divides!?(caar scannewn,car p) then <<
	       q := t;
	       scannewn := nil;
	       if !*cgbstat then
	       	  cgb_tr3count!* := cgb_tr3count!* #+ 1
	    >> else
	       scannewn := cdr scannewn;
	 if not q then
	    newn := gb_cplistsortin(p,newn,nil)
      >>;
      return newn
   end;

procedure gb_traverso!-pairs!-discard1(gk,d);
   % crit B. Delete triange relations.
   for each pij in d join
      if gb_traverso!-trianglep(cadr pij,caddr pij,gk,car pij) then <<
	 if !*cgbstat then
	    cgb_tr1count!* := cgb_tr1count!* #+ 1;
	 if !*cgbverbose then
	    cgb_paircount!* := cgb_paircount!* #- 1;
	 nil
      >> else
	 {pij};

procedure gb_traverso!-trianglep(gi,gj,gk,tij);
   ev_sdivp(gb_tt(gi,gk),tij) and ev_sdivp(gb_tt(gj,gk),tij);

procedure gb_traverso!-final(g);
   % Final reduction and sorting.
   for each rg on vdp_lsort g join
      if not gb_searchinlist(vdp_evlmon car rg,cdr rg) then
	 {vdp_remplist gb_simpcontnormalform gb_normalform(car rg,cdr rg)};

procedure gb_buchcrit4t(e1,e2);
   % nonconstructive test of lcm(e1,e2) = e1 + e2 equivalent: no
   % matches of nonzero elements.
   not ev_disjointp(e1,e2);
   
procedure gb_cplistsort(g,sloppy);
   begin scalar gg;
      for each p in g do
 	 gg := gb_cplistsortin(p,gg,sloppy);
      return gg
   end;

procedure gb_cplistsortin(p,pl,sloppy);
   % Distributive polynomial critical pair list sort. pl is a special
   % list for Groebner calculation, p is a pair. Returns the updated
   % list pl (p sorted into).
   if null pl then
      {p}
   else <<
      gb_cplistsortin1(p,pl,sloppy);
      pl
   >>;

procedure gb_cplistsortin1(p,pl,sloppy);
   % Destructive insert of p into nonnull pl.
   if not gb_cpcompless!?(car pl,p,sloppy) then <<
      rplacd(pl,car pl . cdr pl);
      rplaca(pl,p)
   >> else if null cdr pl then
      rplacd(pl,{p})
   else
      gb_cplistsortin1(p,cdr pl,sloppy);

procedure gb_cpcompless!?(p1,p2,sloppy);
   % Compare 2 pairs wrt. their sugar (=cadddr) or their lcm (=car).
   if sloppy then
      ev_compless!?(car p1,car p2)
   else
      gb_cpcompless!?s(p1,p2,cadddr p1 #- cadddr p2,ev_comp(car p1,car p2));

procedure gb_cpcompless!?s(p1,p2,d,q);
   if d neq 0 then
      d #< 0
   else if q neq 0 then
      q < 0
   else
      vdp_number(caddr p1) #< vdp_number(caddr p2);

procedure gb_cplistmerge(pl1,pl2);
   % Distributive polynomial critical pair list merge. pl1 and pl2 are
   % critical pair lists used in the Groebner calculation.
   % groebcplistmerge(pl1,pl2) returns the merged list.
   begin scalar cpl1,cpl2;
      if null pl1 then
 	 return pl2;
      if null pl2 then
 	 return pl1;
      cpl1 := car pl1;
      cpl2 := car pl2;
      return if gb_cpcompless!?(cpl1,cpl2,nil) then
 	 cpl1 . gb_cplistmerge(cdr pl1,pl2)
      else
 	 cpl2 . gb_cplistmerge(pl1,cdr pl2)
   end;

procedure gb_makepair(f,h);
   % Construct a pair from polynomials f and h.
   begin scalar ttt,sf,sh;
      ttt := gb_tt(f,h);
      sf := vdp_sugar(f) #+ ev_tdeg ev_dif(ttt,vdp_evlmon f);
      sh := vdp_sugar(h) #+ ev_tdeg ev_dif(ttt,vdp_evlmon h);
      return {ttt,f,h,ev_max!#(sf,sh)}
   end;

procedure gb_spolynomial(pr);
   begin scalar p1,p2,s;
      p1 := cadr pr;
      p2 := caddr pr;
      s := gb_spolynomial1(p1,p2);   % TODO: Switch for strange reduction
      if vdp_zero!? s or vdp_unit!? s then
 	 return s;
%      return vdp_setsugar(gb_strange!-reduction(s,p1,p2),cadddr pr)
      return gb_strange!-reduction(s,p1,p2)  % TODO: normal suger for
					     % special cases. 
   end;

procedure gb_spolynomial1(p1,p2);
   begin scalar ep1,ep2,ep,rp1,rp2,db1,db2,x;
      ep1 := vdp_evlmon p1;
      ep2 := vdp_evlmon p2;
      ep := ev_lcm(ep1,ep2);
      rp1 := vdp_mred p1;
      rp2 := vdp_mred p2;
      if vdp_zero!? rp1 and vdp_zero!? rp2 then
 	 return rp1;
      if vdp_zero!? rp1 then
	 return vdp_prod(rp2,vdp_fmon(bc_a2bc 1,ev_dif(ep,ep2)));
      if vdp_zero!? rp2 then
	 return vdp_prod(rp1,vdp_fmon(bc_a2bc 1,ev_dif(ep,ep1)));
      db1 := vdp_lbc p1;
      db2 := vdp_lbc p2;
      x := bc_gcd(db1,db2);
      if not bc_one!? x then <<
	 db1 := bc_quot(db1,x);
	 db2 := bc_quot(db2,x)
      >>;
      return vdp_ilcomb(rp2,db1,ev_dif(ep,ep2),rp1,bc_neg db2,ev_dif(ep,ep1))
   end;

procedure gb_strange!-reduction(s,p1,p2);
   % Subtracts multiples of p2 from the s-polynomial such that head
   % terms are eliminated early.
   begin scalar tp1,tp2,ts,c,saves;
      saves := s;
      tp1 := vdp_evlmon p1;
      tp2 := vdp_evlmon p2;
      c := T; while c and not vdp_zero!? s do <<
	 ts := vdp_evlmon s;
	 if gb_buch!-ev_divides!?(tp2,ts) then
	    s := gb_reduceonestepint(s,vdp_zero(),vdp_lbc s,ts,p2)
	 else if gb_buch!-ev_divides!?(tp1,ts) then
	    s := gb_reduceonestepint(s,vdp_zero(),vdp_lbc s,ts,p1)
	 else
	    c := nil
      >>;
      if !*cgbstat and not (s eq saves) then
	 cgb_strangecount!* := cgb_strangecount!* #+ 1;
      return s
   end;

procedure gb_buch!-ev_divides!?(vev1,vev2);
   % Test if vev1 divides vev2 for exponent vectors vev1 and vev2.
   ev_mtest!?(vev2,vev1);

procedure gb_normalform(f,g);
   % General procedure for the reduction of one polynomial modulo a
   % set. f is a polynomial, g is a set of polynomials. Procedure
   % behaves like type='list. f is reduced modulo g. f is possibly
   % multiplied by an factor.
   begin scalar f1,c,vev,divisor,tai,fold; integer n;
      fold := f;
      f1 := vdp_setsugar(vdp_zero(),vdp_sugar f);
      while not vdp_zero!? f do <<
	 vev := vdp_evlmon f;
 	 c := vdp_lbc f;
	 divisor := gb_searchinlist(vev,g);
	 if divisor then <<
	    tai := T;
	    if vdp_monp divisor then
	       f := vdp_cancelmev(f,vdp_evlmon divisor)
	    else <<
	       f := gb_reduceonestepint(f,f1,c,vev,divisor);
	       f1 := secondvalue!*;
	       if !*cgbcontred then <<
	       	  f := gb_adtssimpcont(f,f1,n);
	       	  f1 := secondvalue!*;
	       	  n := thirdvalue!*
	       >>
	    >>
	 >> else if !*cgbfullred then <<
	    f := gb_shift(f,f1);
	    f1 := secondvalue!*
	 >> else <<
	    f1 := vdp_sum(f1,f);
 	    f := vdp_zero()
	 >>
      >>;
      return if tai then f1 else fold
   end;

procedure gb_searchinlist(vev,g);
   % search for a polynomial in the list G, such that the lcm divides
   % vev; G is expected to be sorted in descending sequence.
   if null g then
      nil
   else if gb_buch!-ev_divides!?(vdp_evlmon car g, vev) then
      car g
   else
      gb_searchinlist(vev,cdr g);

procedure gb_adtssimpcont(f,f1,n);
   begin scalar f0;
      if vdp_zero!? f then <<
      	 secondvalue!* := f1;
      	 thirdvalue!* := 0;
      	 return f
      >>;
      n := n + 1;
      if n #> cgb_contcount!* then <<
      	 f0 := f;
      	 f := gb_simpcont2(f,f1);
      	 f1 := secondvalue!*;
      	 gb_contentcontrol(f neq f0);
	 n := 0
      >>;
      secondvalue!* := f1;
      thirdvalue!* := n;
      return f
   end;

procedure gb_simpcont2(f,f1);
   % Simplify two polynomials with the gcd of their contents. [f] is
   % not zero.
   begin scalar c,s1,s2;
      c := vdp_content f;
      if bc_one!? bc_abs c then <<
	 secondvalue!* := f1;
	 return f
      >>;
      s1 := vdp_sugar f;
      s2 := vdp_sugar f1;
      if not vdp_zero!? f1 then <<
 	 c := vdp_content1(f1,c);
	 if bc_one!? bc_abs c then <<
	    secondvalue!* := f1;
	    return f
      	 >>;
	 f1 := vdp_bcquot(f1,c)
      >>;
      f := vdp_bcquot(f,c);
      vdp_setsugar(f,s1);
      vdp_setsugar(f1,s2);
      secondvalue!* := f1;
      return f
    end;

procedure gb_contentcontrol(u);
   % u indicates, that a substantial content reduction was done;
   % update content reduction limit from u.
   <<
      cgb_contcount!* := if u then
      	 ev_max!#(0,cgb_contcount!* #- 1)
      else
      	 gb_min!#(cgb_mincontred!*,cgb_contcount!* #+ 1);
      if !*cgbverbose then
	 ioto_prin2 {"<",cgb_contcount!*,"> "}
   >>;

procedure gb_min!#(a,b);
   if a #< b then a else b;

procedure gb_shift(f,f1);
   begin scalar s,s1;
      s := vdp_sugar f;
      s1 := vdp_sugar f1;
      f1 := vdp_nconcmon(f1,vdp_lbc f,vdp_evlmon f);
      f := vdp_mred f;
      vdp_setsugar(f,s);
      vdp_setsugar(f1,ev_max!#(s1,s));
      secondvalue!* := f1;
      return f
   end;

procedure gb_reduceonestepint(f,f1,c,vev,g1);
   % Reduction step for integer case: calculate f = a*f - b*g a, b
   % such that leading term vanishes (vev of lvbc g divides vev of
   % lvbc f) and calculate f1 = a*f1. Return value=f, secondvalue=f1.
   begin scalar vevcof,a,b,cg,x,rg1;
      rg1 := vdp_mred g1;
      if vdp_zero!? rg1 then <<  % g1 is monomial
	 f := vdp_mred f;
	 secondvalue!* := f1;
	 return f
      >>;
      vevcof := ev_dif(vev,vdp_evlmon g1);  % nix lcm
      cg := vdp_lbc g1;
      x := bc_gcd(c,cg);
      a := bc_quot(cg,x);
      b := bc_quot(c,x);
      % multiply relevant parts of f and f1 by a (vbc)
      if not vdp_zero!? f1 then
 	 f1 := vdp_bcprod(f1,a);
      f := vdp_ilcombr(vdp_mred f,a,rg1,bc_neg b,vevcof);
      secondvalue!*:= f1;
      return f
   end;

procedure gb_simpcontnormalform(h);
   % simpCont version preserving the property SUGAR.
   if vdp_zero!? h then
      h
   else
      vdp_setsugar(vdp_simpcont h,vdp_sugar h);

procedure gb_vdpvordopt(w,vars);
   % w : list of polynomials (standard forms)
   % vars: list of variables
   % return vars
   begin scalar c;
      vars := sort(vars,'ordop);
      c := for each x in vars collect x . 0 . 0;
      for each poly in w do
 	 gb_vdpvordopt1(poly,vars,c);
      c := sort(c,function gb_vdpvordopt2);
      intvdpvars!* := for each v in c collect car v;
      vars := gb_vdpvordopt31 intvdpvars!*;
      return vars
   end;
 
procedure gb_vdpvordopt31(u);
   begin scalar v,y;
      if null u then
 	 return nil;
      v := for each x in u join <<
 	 y := assoc(x,depl!*);
	 if null y or null xnp(cdr y,u) then
 	    {x}
      >>;
      return nconc(gb_vdpvordopt31 setdiff(u,v),v)
   end;

procedure gb_vdpvordopt1(p,vl,c);
   if null p then
      0
   else if domainp p or null vl then
      1
   else if mvar p neq car vl then
      gb_vdpvordopt1(p,cdr vl,c)
   else begin scalar var,pow,slot; integer n;
      n := gb_vdpvordopt1 (lc p,cdr vl,c);
      var := mvar p;
      pow := ldeg p;
      slot := assoc(var,c);
      if pow #> cadr slot then <<
	 rplaca(cdr slot,pow);
 	 rplacd(cdr slot,n)
      >> else
	 rplacd(cdr slot,n #+ cddr slot);
      return n #+ gb_vdpvordopt1 (red p,vl,c)
   end;

procedure gb_vdpvordopt2(sl1,sl2);
   % compare two slots from the power table
   <<
      sl1 := cdr sl1;
      sl2 := cdr sl2;
      car sl1 #< car sl2 or car sl1 = car sl2 and cdr sl1 #< cdr sl2
   >>;

endmodule;  % gb


module vdp;

%DS
% VDP ::= ('vdp,EV,BC,DP,PLIST)

%DS TERM
% A VDP, such that the polynomial contains one monomial with base
% coefficient 1.

%DS MONOMIAL
% A VDP, such that the polynomial contains one monomial.

procedure vdp_lbc(u);
   caddr u;

procedure vdp_evlmon(u);
   cadr u;

procedure vdp_poly(u);
   car cdddr u;

procedure vdp_zero!?(u);
   null vdp_poly u;

procedure vdp_plist(u);
   cadr cdddr u;

procedure vdp_remplist(u);
   % virtual distributive polynomial remove plist. [u] is a VDP.
   % Returns a VDP. Sets the plist of [u] in-plce to [nil].
   <<
      nth(u,5) := nil;
      u
   >>;

procedure vdp_number(f);
   vdp_getprop(f,'number) or 0;

procedure vdp_sugar(f);
   if (vdp_zero!? f or not(!*cgbsugar)) then 0 else vdp_getprop(f,'sugar) or 0;

procedure vdp_unit!?(p);
   not vdp_zero!? p and ev_zero!? vdp_evlmon p;

procedure vdp_make(vbc,vev,form);
   {'vdp,vev,vbc,form,nil};

procedure vdp_monp(u);
   dip_monp vdp_poly u;

procedure vdp_tdeg(u);
   dip_tdeg vdp_poly u;

procedure vdp_fdip(u);
   if null u then
      vdp_zero()
   else
      vdp_make(dip_lbc u,dip_evlmon u,u);

procedure vdp_appendmon(vdp,coef,vev);
   % Add a monomial to the end of a vdp (vev remains unchanged).
   if vdp_zero!? vdp then
      vdp_fmon(coef,vev)
   else if bc_zero!? coef then
      vdp
   else
      vdp_make(vdp_lbc vdp,vdp_evlmon vdp,dip_appendmon(vdp_poly vdp,coef,vev));

procedure vdp_nconcmon(vdp,coef,vev);
   % Add a monomial to the end of a vdp (vev remains unchanged).
   if vdp_zero!? vdp then
      vdp_fmon(coef,vev)
   else if bc_zero!? coef then
      vdp
   else
      vdp_make(vdp_lbc vdp,vdp_evlmon vdp,dip_nconcmon(vdp_poly vdp,coef,vev));

procedure vdp_bcquot(p,c);
   begin scalar r;
      r := vdp_fdip dip_bcquot(vdp_poly p,c);
      vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_content(p);
   dip_contenti vdp_poly p;

procedure vdp_content1(d,c);
   dip_contenti1(vdp_poly d,c);

procedure vdp_length(f);
   dip_length vdp_poly f;

procedure vdp_bcprod(p,b);
   begin scalar r;
      r := vdp_fdip dip_bcprod(vdp_poly p,b);
      vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_cancelmev(p,vev);
   begin scalar r;
      r := vdp_fdip dip_cancelmev(vdp_poly p,vev);
      vdp_setsugar(r,vdp_sugar p);
      return r
   end;

procedure vdp_sum(d1,d2);
   begin scalar r;
      r := vdp_fdip dip_sum(vdp_poly d1,vdp_poly d2);
      vdp_setsugar(r,ev_max!#(vdp_sugar d1,vdp_sugar d2));
      return r
   end;

procedure vdp_prod(d1,d2);
   begin scalar r;
      r := vdp_fdip dip_prod(vdp_poly d1,vdp_poly d2);
      vdp_setsugar(r,vdp_sugar d1 #+ vdp_sugar d2);
      return r
   end;

procedure vdp_zero();
   vdp_make('invalid,'invalid,nil);   

procedure vdp_mred(u);
   begin scalar r;
      r := dip_mred vdp_poly u;
      if null r then
 	 return vdp_zero();
      r := vdp_make(dip_lbc r,dip_evlmon r,r);
      vdp_setsugar(r,vdp_sugar u);
      return r
  end;

procedure vdp_condense(f);
   dip_condense vdp_poly f;

procedure vdp_setsugar(p,s);
   % virtual distributive polynomial set sugar. [p] is a VDP, s is a
   % machine integer. Returns a VDP. The sugar
   % property of [p] is set to [s].
   if not !*cgbsugar then p else vdp_putprop(p,'sugar,s);

procedure vdp_setnumber(p,n);
   vdp_putprop(p,'number,n);

procedure vdp_putprop(poly,prop,val);
   begin scalar c,p;
      c := cdr cdddr poly;
      p := atsoc(prop,car c);
      if p then
 	 rplacd(p,val)
      else
 	 rplaca(c,(prop . val) . car c);
      return poly
   end;

procedure vdp_getprop(poly,prop);
   (if p then cdr p else nil) where p=atsoc(prop,vdp_plist poly);

procedure vdp_fmon(coef,vev);
   % Virtual distributive polynomial from monomial. [coef] is a BC;
   % [vev] is EV. Returns a VDP, representing the monomial
   % $[coef]^[vev]$
   begin scalar r;
      r := vdp_make(coef,vev,dip_fmon(coef,vev));
      vdp_setsugar(r,ev_tdeg vev);
      return r
   end;

procedure vdp_2a(u);
   dip_2a vdp_poly u;

procedure vdp_2f(u);
   dip_2f vdp_poly u;

procedure vdp_2sq(u);
   dip_2sq vdp_poly u;

procedure vdp_init(vars,sm,sx);
   % Initializing vdp-dip polynomial package.
   dip_init(vars,sm,sx);

procedure vdp_cleanup(l);
   dip_cleanup(l);

procedure vdp_f2vdp(u);
   begin scalar dip;
      dip := dip_f2dip u;
      if null dip then
	 return vdp_zero();
      return vdp_make(dip_lbc dip,dip_evlmon dip,dip)
   end;

procedure vdp_enumerate(f);
  % f is a temporary result. Prepare it for medium range storage and
  % assign a number.
  if vdp_zero!? f or vdp_getprop(f,'number) then
     f
  else
     vdp_putprop(f,'number,(vdp_pcount!* := vdp_pcount!* #+ 1));

procedure vdp_simpcont(p);
   begin scalar q;
      q := vdp_poly p;
      if null q then
 	 return p;
      return vdp_fdip dip_simpcont q
   end;

procedure vdp_lsort(pl);
   % Distributive polynomial list sort. pl is a list of distributive
   % polynomials. vdplsort(pl) returns the sorted distributive
   % polynomial list of pl.
   sort(pl,function vdp_evlcomp);

procedure vdp_evlcomp(p1,p2);  % nicht auf das HM?
   dip_evlcomp(vdp_poly p1,vdp_poly p2);

procedure vdp_ilcomb(v1,c1,t1,v2,c2,t2);
   begin scalar r;
      r := vdp_fdip dip_ilcomb(vdp_poly v1,c1,t1,vdp_poly v2,c2,t2);
      vdp_setsugar(r,ev_max!#(
	 vdp_sugar v1 #+ ev_tdeg t1,vdp_sugar v2 #+ ev_tdeg t2));
      return r
   end;

procedure vdp_ilcombr(v1,c1,v2,c2,t2);
   begin scalar r;
      r := vdp_fdip dip_ilcombr(vdp_poly v1,c1,vdp_poly v2,c2,t2);
      vdp_setsugar(r,ev_max!#(vdp_sugar v1,vdp_sugar v2 #+ ev_tdeg t2));
      return r
   end;

% The following procedures are not used for the Groebner basis
% computation but are useful anyway.

procedure gb_reduce(f,g);
   % Groebner basis reduce. [f] is a VDP, [g] is a list of VDP's.
   % Returns a VDP, a normal form of [f] wrt. [g]. Note that the
   % result contains in general denominators.
   begin scalar f1,c,vev,divisor,tai,fold;
      fold := f;
      f1 := vdp_setsugar(vdp_zero(),vdp_sugar f);
      while not vdp_zero!? f do <<
	 vev := vdp_evlmon f;
 	 c := vdp_lbc f;
	 divisor := gb_searchinlist(vev,g);
	 if divisor then <<
	    tai := T;
	    if vdp_monp divisor then
	       f := vdp_cancelmev(f,vdp_evlmon divisor)
	    else
	       f := gb_reduceonesteprat(f,c,vev,divisor);
	 >> else <<
	    f := gb_shift(f,f1);
	    f1 := secondvalue!*
	 >> 
      >>;
      return if tai then f1 else fold
   end;

procedure gb_reduceonesteprat(f,c,vev,g1);
   % Groebner basis reduce one step rational. [f] is VDP; [c] is BC,
   % the head coefficient of [f]; [vev] is a EV, the head term of [f];
   % [g1] is a VDP such that its head monomial divides the head
   % monomial of [f]. Returns a VDP $h$. $h$ is constructed from [f]
   % by reducing the head monomial of [f] by [g1].
   begin scalar b,rg1,vevcof;
      rg1 := vdp_mred g1;
      if vdp_zero!? rg1 then  % g1 is monomial
	 return vdp_mred f; 
      b := bc_quot(c,vdp_lbc g1);
      vevcof := ev_dif(vev,vdp_evlmon g1);
      return vdp_ilcombr(vdp_mred f,bc_a2bc 1,rg1,bc_neg b,vevcof);
   end;

endmodule;  % vdp

end;  % of file
