% ----------------------------------------------------------------------
% $Id: cgb.red 1275 2011-08-16 14:47:01Z thomas-sturm $
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
   fluid '(cgb_rcsid!* cgb_copyright!*);
   cgb_rcsid!* := "$Id: cgb.red 1275 2011-08-16 14:47:01Z thomas-sturm $";
   cgb_copyright!* := "Copyright (c) 1999-2009 A. Dolzmann and T. Sturm"
>>;

% TODO:
% - Normalize green groebner systems: Detect branches containing a unit
% - Detect green monomials in RP
% - Final simplification with groebner simplifier
% - Computing reduced or pseudo reduced groeber systems.
% - Computing relatively generic and local groebner systems.

module cgb;
% Comprehensive Groebner Bases.

create!-package('(cgb gb dp gbsc),nil);

!#if (and (memq 'psl lispsystem!*) (not (getd 'modulep)))
fluid '(!*lower loadextentions!*);

procedure modulep(u);
   begin scalar found,ld,le,!*lower;
      !*lower := t;
      ld := loaddirectories!*;
      while ld and not found do <<
	 le := loadextensions!*;
	 while le and not found do <<
	    if filep bldmsg("%w%w%w",first ld,u,car first le) then
	       found := cdr first le;
	       le := rest le
	 >>;
	 ld := rest ld
      >>;
      return not null found
   end;
!#endif

loadtime load!-package 'ezgcd;

loadtime load!-package 'groebner;  % for torder

load!-package 'redlog;

switch cgbstat,cgbfullred,cgbverbose,cgbcontred,cgbgs,cgbreal,
   cgbsgreen,cgbfaithful;

fluid '(!*cgbstat !*cgbfullred !*cgbverbose !*cgbcontred !*cgbgs !*cgbreal
   !*cgbgen !*cgbsloppy !*cgbcdsimpl !*cgbfaithful);

off1 'cgbstat;
on1 'cgbfullred;
off1 'cgbverbose;
off1 'cgbcontred;
off1 'cgbgs;
off1 'cgbreal;
off1 'cgbsgreen;  % Simulate green: Compute Gsys and color it green.
off1 'cgbfaithful;

!*cgbsloppy := T;
!*cgbcdsimpl := T;

fluid '(!*cgbgreen);  % pseudo switch for computing green Gsys'

fluid '(!*gcd !*ezgcd !*factor !*exp dmode!* !*msg !*backtrace);

fluid '(cgp_pcount!* cgb_hashsize!*);

cgb_hashsize!* := 65521;  % The size of the hash table for BETA (in gbsc).

fluid '(cgb_hcount!* cgb_hzerocount!* cgb_tr1count!* cgb_tr2count!*
   cgb_tr3count!* cgb_b4count!* cgb_strangecount!* cgb_paircount!*
   cgb_gcount!* cgb_gbcount!*);

fluid '(cgb_cd!* cgb_mincontred!* cgb_contcount!*);
cgb_mincontred!* := 20;  % originally 10

fluid '(!*rlgsvb !*rlspgs !*rlsithok !*rlsiexpla);

%DS
% <AMCGB> ::= <AMPSYS>
% <AMPSYS> ::= ('list,...,<Lisp-prefix-form>,...)
% <AMGSYS> ::= ('list,...,<AMBRANCH>,...)
% <AMBRANCH> ::= ('list,<RL-Formula>,<AMPSYS>)
% <FPSYS> ::= (...,<SF>,...)
% <FGSYS> ::= (...,<FBRANCH>,...)
% <FBRANCH> ::= (<CDLIST>,<FPSYS>)
% <CDLIST> ::= (...,<RL_Formula>,...)

macro procedure cgb_mkinterface(argl);
   begin
      scalar a2sl1,a2sl2,defl,xvfn,s2a,s2s,s,
         args,bname,len,sm,prgn,ami,smi,psval,postfix,modes;
      bname := eval nth(argl,2); % basename of the proc. to be generated
      a2sl1 := eval nth(argl,3); % list of A->SF conv. proc. for all args
      a2sl2 := eval nth(argl,4);
      % list of A->CGP (s=nil)/SF->CGP (s=T) conv. proc. for all args
      defl := eval nth(argl,5);  % list of Lisp pref. default vals for opt. args
      xvfn := eval nth(argl,6);  % func. to extract (m . p) from all args
      s2a := eval nth(argl,7);   % S2A conv. proc for result
      s2s := eval nth(argl,8);   % CGB->Sf conv. proc for result
      s := eval nth(argl,9);     % see a2sl2
      postfix := eval nth(argl,10); % 'f for "(standard) form"
      modes := eval nth(argl,11);   % 'am, 'sm, or nil for both
      len := length a2sl1;
      args := for i := 1:len+3 collect mkid('a,i);
      if (null modes or modes eq 'sm) then <<
               sm := intern compress append('(!c !g !b !_),explode bname);
               % Define the symbolic mode interface
               smi := intern compress nconc(explode sm,explode postfix);
               prgn := {'put,mkquote smi,''number!-of!-args,len+3} . prgn;
               prgn := {'de,smi,args,{'cgb_interface!$,mkquote sm,mkquote a2sl1,
            mkquote a2sl2,mkquote defl,mkquote xvfn,mkquote
               s2a,mkquote s2s,mkquote s,T,'list . args}} . prgn
      >>;
      if (null modes or modes eq 'am) then <<
               % Define the algebraic mode interface
               ami := bname;
               %      ami := intern compress append('(!c !g !b),explode bname);
               psval := intern compress nconc(explode ami,'(!! !$));
               prgn := {'put,mkquote ami,''psopfn,mkquote psval} . prgn;
               prgn := {'put,mkquote psval,''number!-of!-args,1} . prgn;
%%             prgn := {'put,mkquote psval,''cleanupfn,''cgb_cleanup} . prgn;
               prgn := {'de,psval,'(formalargl),{'cgb_interface!$,mkquote sm,
            mkquote a2sl1,mkquote a2sl2,mkquote defl,mkquote
               xvfn,mkquote s2a,mkquote s2s,mkquote s,nil,'formalargl}} . prgn;
      >>;
      return 'progn . prgn
   end;

% cgb(polynomials,[theo])
%
% gsys(polynomials,[theo]) with switch cgbfaithful
%
% ggsys(polynomials,[theo[,xvarl]]) with switch cgbfaithful
%
% gsys2cgb(gsys)

cgb_mkinterface('cgb,'(cgb_a2s!-psys cgb_a2s!-cd),'(cgb_a2s2!-psys
   cgb_a2s2!-cd),{'true},'cgb_xvars!-psys2,'cgb_s2a!-cgb,'cgb_s2s!-cgb,
   T,'f,nil);

cgb_mkinterface('gsys,'(cgb_a2s!-psys cgb_a2s!-cd),'(cgb_a2s2!-psys
   cgb_a2s2!-cd),{'true},'cgb_xvars!-psys2,'cgb_s2a!-gsys,'cgb_s2s!-gsys,
   T,'f,nil);

cgb_mkinterface('ggsys,'(cgb_a2s!-psys cgb_a2s!-cd cgb_a2s!-varl),
   '(cgb_a2s2!-psys cgb_a2s2!-cd cgb_a2s2!-varl),
   {'true,'(list)},'cgb_xvars!-psys3,'cgb_s2a!-gsys,'cgb_s2s!-gsys,T,'f,nil);

cgb_mkinterface('gsys2cgb,'(cgb_a2s!-gsys),'(cgb_a2s2!-gsys),
   nil,'cgb_xvars!-gsys,'cgb_s2a!-cgb,'cgb_s2s!-cgb,T,'f,nil);

put('cgb_cgb,'gb_wrapper,{'gb_gb0,'(gb_a2s!-psys
   gb_dummy1),'(gb_a2s2!-psys gb_dummy1),{'dummy},'gb_xvars!-psys2,
   'gb_s2a!-gbx,'gb_s2s!-gb,T});

put('cgb_gsys,'gb_wrapper,{'gb_gbgsys0,'(gb_a2s!-psys
   gb_dummy1),'(gb_a2s2!-psys gb_dummy1),{'dummy},'gb_xvars!-psys2,
   'gb_s2a!-gsys,'gb_s2s!-gsys,T});

put('cgb_ggsys,'gb_wrapper,{'gb_gbggsys0,'(gb_a2s!-psys
   gb_dummy1 gb_dummy1),'(gb_a2s2!-psys gb_dummy1 gb_dummy1),{'dummy,'dummy},
   'gb_xvars!-psys3,'gb_s2a!-gsys,'gb_s2s!-gsys,T});

procedure cgb_a2s!-psys(l);
   % Comprehensive Groebner bases algebraic mode to symbolic mode
   % polynomial system. [l] is an AMPSYS. Returns an FPSYS.
   begin scalar w,resl;
      for each j in getrlist reval l do <<
               w := numr simp j;
         if w and not(w member resl) then
            resl := w . resl
      >>;
      return sort(resl,'ordp)
   end;

procedure cgb_a2s2!-psys(fl);
   for each x in fl collect cgp_f2cgp x;

procedure cgb_xvars!-psys(l,vl);
   cgb_vars(l,vl);

procedure cgb_xvars!-psys2(l,cd,vl);
   cgb_vars(l,vl);

procedure cgb_xvars!-psys3(l,cd,xvl,vl);
   cgb_vars(l,vl);

procedure cgb_s2a!-cgb(u);
   % Comprehensive Groebner bases symbolic mode to algebraic mode CGB.
   % [u] is a list of CGP's. Returns an AMPSYS.
   'list . for each x in u collect cgp_2a x;

procedure cgb_s2s!-cgb(l);
   cgb_cgb!-sfl l;

procedure cgb_s2a!-gsys(u);
   % Comprehensive Groebner bases symbolic mode to algebraic mode
   % Groebner system. [u] is a GSY. Returns an AMGSYS.
   'list . for each bra in u collect cgb_s2a!-bra bra;

procedure cgb_s2a!-bra(bra);
   % Comprehensive Groebner bases symbolic mode to algebraic mode
   % branch. [u] is a BRA. Returns an AMBRANCH.
   {'list,rl_mk!*fof rl_smkn('and,bra_cd bra),
      'list . for each x in bra_system bra collect cgp_2a x};

procedure cgb_s2s!-gsys(u);
   for each bra in u collect cgb_s2s!-bra bra;

procedure cgb_s2s!-bra(bra);
   {bra_cd bra,cgb_s2s!-cgb bra_system bra};

procedure cgb_a2s!-gsys(u);
   % Comprehensive Groebner bases algebraic mode to symbolic mode
   % Groebner system. [u] is AMGSYS. Returns an FGSYS.
   begin scalar sys,w;
      sys := getrlist reval u;
      return for each bra in sys collect <<
         w := getrlist bra;
         bra_mk(cd_for2cd rl_simp car w,cgb_a2s!-psys cadr w,nil)
      >>
   end;

procedure cgb_a2s2!-gsys(sys);
   for each bra in sys collect
      bra_mk(car bra,cgb_a2s2!-psys cadr bra,nil);

procedure cgb_xvars!-gsys(sys,vl);
   begin scalar w;
      w := for each bra in sys join
         bra_system bra;
      return cgb_vars(w,vl)
   end;

procedure cgb_a2s!-cd(cd);
   begin scalar w,!*rlsiexpla;
      w := rl_simpl(rl_simp reval cd,nil,-1);
      if not cgb_cdp w then
         rederr
             "CGB theory must be truth value or (conjunction of) atomic formulas";
      return cd_for2cd w
   end;

procedure cgb_cdp(cd);
   begin scalar err;
      if rl_tvalp cd or not rl_cxp rl_op cd then
         return T;
      if rl_op cd neq 'and then
         return nil;
      cd := rl_argn cd;
      while not err and cd do
         if (not rl_tvalp car cd and rl_cxp rl_op car cd) then
            err := T
         else
            cd := cdr cd;
      return not err
   end;

procedure cgb_a2s2!-cd(cd);
   cd;

procedure cgb_a2s!-varl(varl);
   cdr varl;

procedure cgb_a2s2!-varl(varl);
   varl;

procedure cgb_cleanup(u,v);  % Do not use reval.
   u;

procedure cgb_interface!$(fname,a2sl1,a2sl2,defl,xvfn,s2a,s2s,s,smp,argl);
   % fname is a function, the name of the procedure to be called;
   % [a2sl1] and [as2sl2] are a list of functions, called to be
   % transform algebraic arguments to symbolic arguments; [defl] is a
   % list of algebraic defualt arguments; xvfn is a procedure for
   % extracting the variables from all arguments; [s2a] is procedure
   % for transforming the symbolic return value to an algebraic mode
   % return value; [argl] is the list of arguments; [s] is a flag;
   % [smp] is a flag. Return an S-expr. If [s] is on then second stage
   % of argument processing is done with the results of the first one.
   begin scalar w,vl,nargl,oenv,ocdenv,m,c,x;
      ocdenv := cd_init();  % early setup for a2s procedures...
      if not smp then <<
         nargl := cgb_am!-pargl(fname,a2sl1,argl,defl);
         vl := apply(xvfn,append(nargl,{td_vars()}));
         if null cdr vl and (w:=get(fname,'gb_wrapper)) then <<
            cd_cleanup ocdenv;
            return apply('gb_interface!$,append(w,{smp,argl}))
         >>;
         oenv := cgp_init(car vl,td_sortmode(),td_sortextension());
      >> else <<
         w := cgb_sm!-pargl argl;
         nargl := car w;
         m := cadr w;
         c := caddr w;
         x := cadddr w;
         vl := apply(xvfn,append(nargl,{m}));
         if null cdr vl and (w:=get(fname,'gb_wrapper)) then <<
            cd_cleanup ocdenv;
            return apply('gb_interface!$,append(w,{smp,argl}))
         >>;
         oenv := cgp_init(car vl,c,x);
      >>;
      w := errorset({'cgb_interface1!$,
         mkquote fname,mkquote a2sl2,mkquote s2a,mkquote s2s,mkquote s,
         mkquote smp,mkquote argl, mkquote nargl,mkquote car vl,
         mkquote cdr vl},T,!*backtrace);
      cd_cleanup ocdenv;
      cgp_cleanup oenv;
      if errorp w then
               rederr {"error during errorset() on",fname};
      return car w
   end;

procedure cgb_sm!-pargl(argl);
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

procedure cgb_am!-pargl(fname,a2sl1,argl,defl);
   % process argument list for algebraic mode.
   begin integer l1,l2,l3,noa; scalar w,nargl,scargl,scdefl;
      l1 := length argl;  % number of passed args
      l2 := length a2sl1; % number of specified args
      l3 := l2 - length defl;  % lower bound for passed args
      if l1 < l3 or l1 > l2 then
         rederr {fname,"called with",l1,"arguments instead of",l3,"-",l2};
      scargl := argl;
      scdefl := defl;
      noa := 1;
      nargl := for each x in a2sl1 collect <<
         if scargl then <<
            w := car scargl;
            scargl := cdr scargl
         >> else <<
            w := car scdefl;
         >>;
         if noa > l3 then
            scdefl := cdr scdefl;
         noa := noa + 1;
         apply(x,{w})
      >>;
      return nargl
   end;

procedure cgb_interface1!$(fname,a2sl2,s2a,s2s,s,smp,argl,nargl,m,p);
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

procedure cgb_greengsysf(u,m,sm,sx,theo,xvarl);
   cgb_ggsysf(u,m,sm,sx,theo,xvarl);

procedure cgb_gsys2green(u,theo);
   % Comprehensive Groebner bases Groebner system to gree Groebner
   % system. [u] is a GSY; [theo] is a CD. Returns a GSY, in which
   % all polynomials are colored green, i.e., the green colore head
   % part is deleted.
   for each bra in u collect
      bra_mk(bra_cd bra,cgb_cgpl2green(bra_system bra,append(theo,bra_cd bra)),
         bra_cprl bra);

procedure cgb_cgpl2green(l,theo);  % TODO: delete green monomials in RP.
   % Comprehensive Groebner bases CGP list 2 green CGP list. [l] is a
   % list of CGP's; [theo] is a CD. Returns a list of CGP's. All CGP's
   % in the returned list are colred green, i.e., the green colored
   % head part is deleted.
   for each cgp in l collect
      cgp_green cgp;

procedure cgb_domainchk();
   % Comprehensive Groebner bases domain check. No argument. Return
   % value not defined. Raises an error if the current domain is not
   % valid for CGB computations.
   if not memq(dmode!*,'(nil)) then
      rederr bldmsg("cgb does not support domain: %w",get(dmode!*,'dname));

procedure cgb_vars(l,vl);
   % Comprehensive Groebner bases variables. [l] is a list of SF's;
   % [vl] is the list of main variables. Returns a pair $(m . p)$
   % where $m$ and $p$ are list of variables. $m$ is the list of used
   % main variables and $p$ is the list of used parameters.
   begin scalar w,m,p;
      for each f in l do
         w := union(w,kernels f);
      if vl then <<
               m := cgb_intersection(vl,w);
               p := setdiff(w,vl)
      >> else
         m := w;
      return m . p
   end;

procedure cgb_varsgsys(gsys,vl);
   % Comprehensive Groebner bases variables in a Groebner system.
   % [gsys] is FGSYS; [vl] is the list of main variables . Returns a
   % pair $(m . p)$ where $m$ and $p$ are list of variables. $m$ is
   % the list of used main variables and $p$ is the list of used
   % parameters.
   begin scalar w,m,p;
      for each bra in gsys do
               for each f in bra_system bra do
            w := union(w,kernels f);
      m := cgb_intersection(vl,w);
      p := setdiff(w,vl);
      return m . p
   end;

procedure cgb_intersection(a,b);
   % Comprehensive Groebner bases intersection. [a] and [b] are lists.
   % Returns a list. The returned list contains all elements occuring
   % in [a] and in [b]. The order of the elements is the same as in
   % [a].
   for each x in a join
      if x member b then
         {x};

procedure cgb_cgb(u,theo);
   % Comprehensive Groebner bases CGB computation. [u] is a list of
   % CGP's; [theo] is a condition. Returns a list of CGP's.
   cgb_gsys2cgb cgb_gsys(u,theo) where !*cgbfaithful=T;

procedure cgb_gsys2cgb(u);
   % Comprehensive Groebner bases CGB to Groebner system conversion.
   % [u] is a GSY. Returns a list of CGP's.
   begin scalar cgbase;
      for each bra in u do
         for each p in bra_system bra do
            if not (p member cgbase) then  % TODO: cgp_member?
               cgbase := p . cgbase;
      return cgp_lsort cgbase
   end;

procedure cgb_cgb!-sfl(u);
   % Comprehensive Groebner bases CGB to SF list. [u] is a list of
   % CGP's. Returns a list of SF's.
   for each p in u collect cgp_2f p;

smacro procedure cgb_tt(s1,s2);
   % Comprehensive Groebner bases tt. [s1] and [s2] are CGP's. Returns
   % an EV, the lcm of the leading terms of [s1] and [s2].
   ev_lcm(cgp_evlmon s1,cgp_evlmon s2);

%% procedure cgb_gsys(u,theo);
%%    % Comprehensive Groebner bases Groebner system computation. [u] is
%%    % a list of CGP's; [theo] is the inital theory. Returns a GSY, the
%%    % Groebner system of [u].
%%    gsy_normalize cgb_gsys1(cgp_lsort u,theo,nil) where
%%       !*cgbgen=nil,!*cgbgreen=not !*cgbfaithful;
%%
%% procedure cgb_ggsys(u,theo,xvarl);
%%    % Comprehensive Groebner bases Groebner system computation. [u] is
%%    % a list of CGP's; [theo] is the inital theory. Returns a GSY, the
%%    % Groebner system of [u].
%%    gsy_normalize cgb_gsys1(cgp_lsort u,theo,xvarl) where
%%       !*cgbgen=T,!*cgbgreen=not !*cgbfaithful;

procedure cgb_gsys(u,theo);
   % Comprehensive Groebner bases Groebner system computation. [u] is
   % a list of CGP's; [theo] is the inital theory. Returns a GSY, the
   % Groebner system of [u].
   if !*cgbfaithful then
      gsy_normalize cgb_gsys1(cgp_lsort u,theo,nil)
          where !*cgbgen=nil,!*cgbgreen=nil
   else
      cgb_nonfaithfulgsys(u,theo,nil) where !*cgbgen=nil;

procedure cgb_ggsys(u,theo,xvarl);
   % Comprehensive Groebner bases Groebner system computation. [u] is
   % a list of CGP's; [theo] is the inital theory. Returns a GSY, the
   % Groebner system of [u].
   if !*cgbfaithful then
      gsy_normalize cgb_gsys1(cgp_lsort u,theo,xvarl)
          where !*cgbgen=T,!*cgbgreen=nil
   else
      cgb_nonfaithfulgsys(u,theo,xvarl) where !*cgbgen=T;

procedure cgb_nonfaithfulgsys(u,theo,xvarl);
   % Comprehensive Groebner bases green Groebner system computation.
   % [u] is a list of CGP's; [theo] is the initial theory. Returns a
   % GSY, the green Groebner system of [u].
   if !*cgbsgreen then
      gsy_normalize cgb_gsys2green(cgb_gsys1(cgp_lsort u,theo,xvarl),theo)
         where !*cgbgreen=nil
   else
      gsy_normalize cgb_gsys1(cgp_lsort u,theo,xvarl) where !*cgbgreen=T;

procedure cgb_gsys1(u,theo,xvarl);
   % Comprehensive Groebner bases Groebner system computation
   % subroutine. [u] is a list of CGP's; [theo] is the initaila
   % theory. Returns a GSY, the Groebner system of [u].
   begin
      scalar spac,stime,p1,!*factor,!*exp,!*gcd,!*ezgcd,cgb_cd!*,!*cgbverbose;
      integer cgp_pcount!*,cgb_contcount!*,cgb_hcount!*,cgb_hzerocount!*,
         cgb_tr1count!*,cgb_tr2count!*,cgb_tr3count!*,cgb_b4count!*,
         cgb_strangecount!*,cgb_paircount!*,cgb_gbcount!*,cgb_contcount!*;
      if theo = '(false) then
         rederr "cgb_gsys1: inconsistent theory";
      !*exp := !*gcd := !*ezgcd := t;
      cgb_contcount!* := cgb_mincontred!*;
      if !*cgbstat then <<
               spac := gctime();
         stime := time()
      >>;
      p1 := cgb_traverso(u,theo,xvarl);
      if !*cgbstat then <<
         ioto_tprin2t "Statistics for GB computation:";
         ioto_prin2t {"Time: ",time() - stime," ms plus GC time: ",
            gctime() - spac," ms"};
         ioto_prin2t {"H-polynomials total: ",cgb_hcount!*};
         ioto_prin2t {"H-polynomials zero: ",cgb_hzerocount!*};
         ioto_prin2t {"Crit Tr1 hits: ",cgb_tr1count!*};
         ioto_prin2t {"Crit B4 hits: ",cgb_b4count!*," (Buchberger 1)"};
         ioto_prin2t {"Crit Tr2 hits: ",cgb_tr2count!*};
         ioto_prin2t {"Crit Tr3 hits: ",cgb_tr3count!*};
%        ioto_prin2t {"Strange reductions: ",cgb_strangecount!*}
      >>;
      return p1
   end;

procedure cgb_traverso(g0,theo,xvars);
   % Comprehensive Groebner bases Traverso. [g0] is a list of CGP's;
   % [theo] is a initial theory. Returns a GSY of [g0].
   begin scalar bra,gsys,resl,bral;
      g0 := for each fj in g0 collect
         cgp_simpdcont fj;
      gsys := gsy_init(g0,theo,xvars);
      while gsys do <<
         bra := car gsys;
         gsys := cdr gsys;
         if bra_cprl bra eq 'final or null bra_cprl bra then
            resl := bra . resl
         else <<
            bral := cgb_traverso1(bra,xvars);
            gsys := nconc(bral,gsys)
         >>
      >>;
      return resl  % TODO: reduction
   end;

procedure cgb_traverso1(bra,xvars);
   % Comprehensive Groebner bases Traverso subroutine. [bra] is a BRA.
   % Returns a GSY. Performs one step in the computation of a GSY.
   begin scalar g,d,s,h,p;
      cgb_cd!* := bra_cd bra;
      g := bra_system bra;
      d := bra_cprl bra;
      if !*cgbverbose then <<
               ioto_prin2 {"[",cgb_paircount!*,"] "};
         cgb_paircount!* := cgb_paircount!* #- 1
      >>;
      p := car d;
      d := cdr d;
      s := cgb_spolynomial p;
      h := cgb_normalform(s,g,xvars);
      h := cgp_simpdcont h;
      if !*cgbstat then
         cgb_hcount!* := cgb_hcount!* #+ 1;
      if cgp_zerop h then
          cgb_hzerocount!* := cgb_hzerocount!* #+ 1;
      return bra_split(bra_mk(cgb_cd!*,g,d),h,xvars)
   end;

procedure cgb_spolynomial(pr);
   % Comprehensive Groebner bases S-polynomial. [pr] is a CPR. Returns
   % a CGP the S-polynomial of [pr] possibly reduced wrt. the
   % polynomials in [pr].
   begin scalar s;
      s := cgb_spolynomial1 pr;  % TODO: updcondition
      return s;  % TODO: Strange reduction
   end;

procedure cgb_spolynomial1(pr);
   % Comprehensive Groebner bases S-polynomial subroutine. [pr] is a
   % CPR. Returns a CGP. the S-polynomial of [pr].
   begin scalar p1,p2,ep,ep1,ep2,rp1,rp2,db1,db2,x,spol;
      p1 := cpr_p1 pr;
      p2 := cpr_p2 pr;
      ep := cpr_lcm pr;
      ep1 := cgp_evlmon p1;
      ep2 := cgp_evlmon p2;
      rp1 := cgp_mred p1;
      rp2 := cgp_mred p2;
      if cgp_greenp rp1 and cgp_greenp rp2 then
          return cgp_zero();
      db1 := cgp_lbc p1;
      db2 := cgp_lbc p2;
      x := bc_gcd(db1,db2);
      db1 := bc_quot(db1,x);
      db2 := bc_quot(db2,x);
      spol := cgp_ilcomb(rp1,db2,ev_dif(ep,ep1),rp2,bc_neg db1,ev_dif(ep,ep2));
      if cgp_greenp spol then
         return cgp_zero();
      return spol
   end;

procedure cgb_normalform(f,g,xvars);
   % Comprehensive Groebner bases normal form computation. [f] is a
   % CGP; [g] is a list of CGP's with red HT's. Returns a CGP $p$.
   % Depends on switch [!*cgbfullred]. $p$ is computed by
   % reducing [f] with polynomials in [g].
   begin scalar fold,c,tai,divisor;
      if null g then
         return f;
      if cgp_greenp f then
         return cgp_zero();
      fold := f;
      f := cgp_hpcp f;
      f := cgp_shift(f,xvars);
      c := T; while c and cgp_rp f do <<
         divisor := cgb_searchinlist(cgp_evlmon f,g);
         if divisor then <<
            tai := T;
            f := cgb_reduce(f,divisor)
         >> else if !*cgbfullred then
            f := cgp_shiftwhite f
         else
            c := nil;
         if c then
            f := cgp_shift(f,xvars)
      >>;
      if not tai then
         return fold;
      return cgp_backshift f  % TODO: updccondition
   end;

procedure cgb_searchinlist(vev,g);
   % Comprehensive Groebner bases search for a polynomial in a list.
   % [vev] is a EV; [g] is a CGP. Returns a CGP $p$, such that the RP
   % of [g] is reducible wrt. $p$.
   if null g then
      nil
   else if cgb_buch!-ev_divides!?(cgp_evlmon car g,vev) then
      car g
   else
      cgb_searchinlist(vev,cdr g);

procedure cgb_buch!-ev_divides!?(vev1,vev2);
   % Comprehensive Groebner bases Buchberger exponent vector divides.
   % [vev1] and [vev2] are EV's. Returns non-[nil] if [vev1] divides
   % [vev2].
   ev_mtest!?(vev2,vev1);

procedure cgb_reduce(f,g1);
   % Comprehensive Groebner bases reduce. [f] is a CGP; [g1] is a CGP,
   % such that the RP of [f] is reducible wrt. [g1]. Returns a CGP
   % $p$. $p$ is computed by reducing [f] with [g1].
   if cgp_monp g1 then
      cgp_cancelmev(cgp_bcprod(f,cgp_lbc g1),cgp_evlmon g1)  % TODO: numberp
   else
      cgb_reduceonestep(f,g1);  % TODO: Content reduction

procedure cgb_reduceonestep(f,g);
   % Comprehensive Groebner bases reduce one step. [f] is a CGP; [g]
   % is a CGP, such that the RP of [f] is top-reducible wrt. [g].
   % Returns a CGP $p$. $p$ is computed by performing one
   % top-reduction.
   begin scalar cot,hcf,hcg,x,a,b;
      cot := ev_dif(cgp_evlmon f,cgp_evlmon g);
      hcf := cgp_lbc f;
      hcg := cgp_lbc g;
      x := bc_gcd(hcf,hcg);
      a := bc_quot(hcg,x);
      b := bc_quot(hcf,x);
      return cgp_setci(cgp_ilcombr(f,a,g,bc_neg b,cot),cgp_ci f)
   end;  % TODO: updccondition

endmodule;   % cgb;


module cd;
% Conditions.

% DS
% <CD> ::=  (false) | () | (...,<Atomic Formula>,...)

procedure cd_init();
   % Condition init. No argument. Return value describes the current
   % context. Depends on switch [!*cgbreal]. Sets up the environment
   % for handling conditions in the choosen context.
   (if !*cgbreal then rl_set '(ofsf) else rl_set '(acfsf)) where !*msg=nil;

procedure cd_cleanup(oc);
   % Condition clean-up. [oc] decsribes the context wich should be
   % selected. Return value unspecified.
   rl_set oc where !*msg=nil;

procedure cd_falsep(cd);
   % Condion false predicate. [cd] is a CD. Returns bool. If [t] is
   % retunred then the condion [cd] is inconsistent.
   eqcar(cd,'false);

procedure cd_siadd(atl,sicd);
   % Condion simplify add. [atl] is a list of atomic formulas; [sicd]
   % is a CD. Returns a CD, the union of [cd] and [atl].
   begin scalar w;
      if not !*cgbcdsimpl then
               return nconc(atl,sicd);
      w := if !*cgbgs then
         cd_gsd(rl_smkn('and,nconc(atl,sicd)),nil)
      else
         rl_siaddatl(atl,rl_smkn('and,sicd));
      return cd_for2cd w
   end;

procedure cd_for2cd(f);
   % Condition formula to condition. [f] is either ['false] , ['true],
   % or a conjunction of atomic formulas. Returns a CD equivalent to
   % [f]. Formula to condition.
   if f eq 'true then
      nil
   else if f eq 'false then
      '(false)
   else if cl_cxfp f then
      rl_argn f
   else
      {f};

procedure cd_surep(f,cd);
   % Condition sure predicate. [f] is an atomic formula; [cd] is a CD.
   % If [T] is returned, then [cd] implies [f].
   begin scalar !*rlgsvb;
      return rl_surep(f,cd) where !*rlspgs=!*cgbgs,!*rlsithok=T;
   end;

procedure cd_gsd(f,cd);
   % Condition Groebner simplifier. [f] is a formula; [cd] is a
   % condition. Simplies [f] wrt. the theory [cd].
   begin scalar !*rlgsvb;
      return rl_gsd(f,cd)
   end;

procedure cd_ordp(cd1,cd2);
   % Condition order predicate. [cd1] and [cd2] are conditions sorted
   % wrt. ['cd_ordatp]. Returns bool.
   if null cd1 then
      T
   else if null cd2 then
      nil
   else if car cd1 neq car cd2 then
      cd_ordatp(car cd1,car cd2)
   else
      cd_ordp(cdr cd1,cdr cd2);

procedure cd_ordatp(a1,a2);
   % Condition order atomic formula predicate. [a1] and [a2] are
   % atomic formulas. Returns bool.
   if car a1 eq 'neq and car a2 eq 'equal then
      T
   else if car a1 eq 'equal and car a2 eq 'neq then
      nil
   else
      ordp(cadr a1,cadr a2);

endmodule;  % cd


module cpr;
% Critical pairs.

%DS
% <CPRL> ::= (...,<CPR>,...)
% <CPR> ::= (<LCM>,<P1>,<P2>,<SUGAR>);

procedure cpr_mk(f,h);
   % Critical pair make. [f], and [h] are CGP's. Returns a CPR.
   % Construct a pair from polynomials [f] and [h].
   begin scalar ttt,sf,sh;
      ttt := cgb_tt(f,h);
      sf := cgp_sugar(f) #+ ev_tdeg ev_dif(ttt,cgp_evlmon f);
      sh := cgp_sugar(h) #+ ev_tdeg ev_dif(ttt,cgp_evlmon h);
      return cpr_mk1(ttt,f,h,ev_max!#(sf,sh))
   end;

procedure cpr_mk1(lcm,p1,p2,sugar);
   % Critical pair make subroutine. [lcm] is an EV, the lcm of [evlmon
   % p1] and [evlmon p2]; [p1] and [p2] are CGP's with red HC; [sugar]
   % is a machine integer, the sugar of the S-polynomials of [p1] and
   % [p2]. Returns a CPR.
   {lcm,p1,p2,sugar};

procedure cpr_lcm(cpr);
   % Critical pair lcm. [cpr] is a critical pair. Returns the lcm part
   % of [cpr].
   car cpr;

procedure cpr_p1(cpr);
   % Critical pair p1. [cpr] is a critical pair. Returns the p1 part
   % of [cpr].
   cadr cpr;

procedure cpr_p2(cpr);
   % Critical pair p2. [cpr] is a critical pair. Returns the p2 part
   % of [cpr].
   caddr cpr;

procedure cpr_sugar(cpr);
   % Critical pair suger. [cpr] is a critical pair. Returns the sugar
   % part of [cpr].
   cadddr cpr;

procedure cpr_traverso!-pairlist(gk,g,d);
   % Critical pair Travero pair list. [gk] is a CGP with red HT; [g]
   % is a list of CGP's with red HT's; [d] is a sorted list of CPR's.
   % Returns a sorted list of CPR's the result of updating [w] with
   % critical pairs construction by combining [gk] with polynomials in
   % [g].
   begin scalar ev,r,n;
      d := cpr_traverso!-pairs!-discard1(gk,d);
      % build new pair list:
      ev := cgp_evlmon gk;
      for each p in g do
              if not cpr_buchcrit4t(ev,cgp_evlmon p) then <<
            if !*cgbstat then
               cgb_b4count!* := cgb_b4count!* #+ 1;
             r := ev_lcm(ev,cgp_evlmon p) . r
                >> else
             n := cpr_mk(p,gk) . n;
      n := cpr_tr2crit(n,r);
      n := cpr_listsort(n,!*cgbsloppy);
      n := cpr_tr3crit n;
      if !*cgbverbose and n then <<
         cgb_paircount!* := cgb_paircount!* #+ length n;
         ioto_cterpri();
         ioto_prin2 {"(",cgb_gbcount!*,") "}
      >>;
      return cpr_listmerge(d,reversip n)
   end;

procedure cpr_tr2crit(n,r);
   % Critical pair Travero 2 criterion. [n] is a list of CPR's; [r] is
   % a list of EV's. Returns a list of CPR's. Delete equivalents to
   % coprime lcm
   for each p in n join
      if ev_member(cpr_lcm p,r) then <<
         if !*cgbstat then
            cgb_tr2count!* := cgb_tr2count!* #+ 1;
         nil
      >> else
         {p};

procedure cpr_tr3crit(n);
   % Critical pair Travero 3 criterion. [n] is a sorted list of CPR's;
   % [r] is a list of EV's. Returns a sorted list of CPR's.
   begin scalar newn,scannewn,q;
      for each p in n do <<
         scannewn := newn;
         q := nil;
          while scannewn do
            if ev_divides!?(cpr_lcm car scannewn,cpr_lcm p) then <<
               q := t;
               scannewn := nil;
               if !*cgbstat then
                         cgb_tr3count!* := cgb_tr3count!* #+ 1
            >> else
               scannewn := cdr scannewn;
         if not q then
            newn := cpr_listsortin(p,newn,nil)
      >>;
      return newn
   end;

procedure cpr_traverso!-pairs!-discard1(gk,d);
   % Critical pairs Traverso pairs discard 1. [gk] is a CGP with red
   % HT; [d] is a sorted list of CPR's. Returns a list of [cpr]'s.
   % Criterion B. Delete triange relations.
   for each pij in d join
      if cpr_traverso!-trianglep(cpr_p1 pij,cpr_p2 pij,gk,cpr_lcm pij) then <<
         if !*cgbstat then
            cgb_tr1count!* := cgb_tr1count!* #+ 1;
         if !*cgbverbose then
            cgb_paircount!* := cgb_paircount!* #- 1;
         nil
      >> else
         {pij};

procedure cpr_traverso!-trianglep(gi,gj,gk,tij);
   % Critical pairs Traverso triangle predicate. [gi], [gj], and [gk]
   % are CGP's with red HT; [tij] is an EV.
   ev_sdivp(cgb_tt(gi,gk),tij) and ev_sdivp(cgb_tt(gj,gk),tij);

procedure cpr_buchcrit4t(e1,e2);
   % Critical pair Buchbergers criterion 4. [e1], [e2] are EV's.
   % Returns [T] if [e1] and [e2] are disjoint.
   not ev_disjointp(e1,e2);

procedure cpr_listsort(g,sloppy);
   % Critical pair list sort. [g] is a list of CPR's, [sloppy] is
   % bool. Returns a list of CPR'S. Destructively sorts [g]
   begin scalar gg;
      for each p in g do
          gg := cpr_listsortin(p,gg,sloppy);
      return gg
   end;

procedure cpr_listsortin(p,pl,sloppy);
   % Critical pair list sort into. [p] is a CPR; [pl] is a sorted list
   % of CPR's, [sloppy] is bool. Destructively sorts [p] into [pl].
   if null pl then
      {p}
   else <<
      cpr_listsortin1(p,pl,sloppy);
      pl
   >>;

procedure cpr_listsortin1(p,pl,sloppy);
   % Critical pair list sort into. [p] is a CPR; [pl] is a non-empty,
   % sorted list of CPR's; [sloppy] is bool. Destructively sorts [p]
   % into [pl].
   if not cpr_lessp(car pl,p,sloppy) then <<
      rplacd(pl,car pl . cdr pl);
      rplaca(pl,p)
   >> else if null cdr pl then
      rplacd(pl,{p})
   else
      cpr_listsortin1(p,cdr pl,sloppy);

procedure cpr_lessp(pr1,pr2,sloppy);
   % Critical pair less predicate. [p1] and [p2] are CPR's; [sloppy]
   % is bool. Returns [T] is [p1] is less than [p2]. Compare 2 pairs
   % wrt. their sugar or their lcm.
   if sloppy then
      ev_compless!?(cpr_lcm pr1,cpr_lcm pr2)
   else
      cpr_lessp1(pr1,pr2,cpr_sugar pr1 #- cpr_sugar pr2,
         ev_comp(cpr_lcm pr1,cpr_lcm pr2));

procedure cpr_lessp1(pr1,pr2,d,q);
   % Critical pair less predicate subroutine. [p1] and [p2] are CPR's.
   % Returns [T] is [p1] is less than [p2]. Compare 2 pairs wrt. their
   % sugar or their lcm.
   if not(d #= 0) then
      d #< 0
   else if not(q #= 0) then
      q #< 0
   else
      cgp_number cpr_p2 pr1 #< cgp_number cpr_p2 pr2;

procedure cpr_listmerge(pl1,pl2);  % TODO: Rekursiv, konstruktiv !!!
   % Critical pair list merge. [pl1] and [pl2] are sorted list of
   % CPR's. Returns a sorted list of CPR's the restult of merging the
   % lists [pl1] and [pl2].
   begin scalar cpl1,cpl2;
      if null pl1 then
          return pl2;
      if null pl2 then
          return pl1;
      cpl1 := car pl1;
      cpl2 := car pl2;
      return if cpr_lessp(cpl1,cpl2,nil) then
          cpl1 . cpr_listmerge(cdr pl1,pl2)
      else
          cpl2 . cpr_listmerge(pl1,cdr pl2)
   end;

endmodule;  % cpr


module bra;

%DS
% <BRA> ::= (<CD>,<SYSTEM>,<CPRL>)

procedure bra_cd(br);
   % Branch condition. [br] is a BRA. Returns a CD, the condition part
   % of [br].
   car br;

procedure bra_system(br);
   % Branch system. [br] is a BRA. Returns a list of CGP's, the
   % system part of [br].
   cadr br;

procedure bra_cprl(br);
   % Branch critical pair list. [br] is a BRA. Returns a list of
   % CPR's, the pairs part of [br].
   caddr br;

procedure bra_mk(cd,system,cprl);
   % Branch make. [cd] is a CD; [system] is a list of CGP's with red
   % HT's; [cprl] is a list of CPR's. Returns a BRA.
   {cd,system,cprl};

procedure bra_split(bra,p,xvars);
   % Branch split. [bra] is a BRA; [p] is a CGP. Returns a GSY.
   if cgp_greenp p then
      {bra}
   else if bra_cprl bra eq 'final then
      {bra}
   else
      bra_split1(bra,cgp_enumerate cgp_condense p,xvars);

procedure bra_split1(bra,p,xvars);
   % Branch split subroutine. [bra] is a BRA; [p] is a CGP. Returns a GSY.
   for each pr in cgp_2scpl(p,bra_cd bra,xvars) collect
      bra_ext(bra,car pr,cdr pr);

procedure bra_ext(bra,cd,scp);
   % Branch extend. [bra] is a BRA; [cd] is a CD; [scp] is CGP with
   % red HT. Returns a BRA.
   begin scalar sy,d;
      if cgp_unitp scp then
         return bra_mk(cd,{scp},'final);
      sy := for each p in bra_system bra collect cgp_cp p;  % TODO: Copy?
      d := for each pr in bra_cprl bra collect pr;  % TODO: Copy?
      if cgp_greenp scp then
         return bra_mk(cd,sy,d);
      d := cpr_traverso!-pairlist(scp,sy,d);
      return bra_mk(cd,nconc(sy,{scp}),d)
   end;

procedure bra_ordp(b1,b2);
   % Branch order predicate. [b1] and [b2] are branches. Returns bool.
   cd_ordp(bra_cd b1,bra_cd b2);

endmodule;  % bra


module gsy;
% Groebner system.

%DS
% <GSY> ::= (...,<BRA>,...)

procedure gsy_init(l,theo,xvars);
   % Groebner system initialize. [l] is a list of CGP's. Returns a
   % GSY. We construct a case distinction wrt. to the parametric
   % coefficients in the elements of [l].
   begin scalar s;
      s := {bra_mk(theo,nil,nil)};
      for each x in l do
         s := for each y in s join
            bra_split(y,x,xvars);
      return s
   end;

procedure gsy_normalize(l);
   % Groebner system normalize. [l] is a GSY. Returns a GSY.
   sort(gsy_normalize1 l,'bra_ordp);

procedure gsy_normalize1(l);
   % Groebner system normalize subroutine. [l] is a GSY. Returns a GSY.
   for each bra in l collect
      bra_mk(sort(bra_cd bra,'cd_ordatp),
         cgp_lsort for each x in bra_system bra collect cgp_normalize x,
         bra_cprl bra);

endmodule;  % gsy


module cgp;
% Comprehensive Groebner basis polynomial.

%DS
% <CGP> ::= ('cgp,<HP>,<RP>,<SUGAR>,<NUMBER>,<CI>)
% <HP> ::= <DIP>
% <RP> ::= <DIP>
% <SUGAR> ::= <Machine Integer> | nil
% <NUMBER> ::= <Machine Integer> | nil
% <CI> ::= 'unknown | 'red | 'green | 'zero | ('mixed . <WTL>) | green_colored
% <WTL> ::= (...,<EV>,...)

procedure cgp_mk(hp,rp,sugar,number,ci);
   % CGP make. [hp] and [rp] are DIP's; [sugar] and [number] are
   % machine numbers; [ci] is an S-expr.
   {'cgp,hp,rp,sugar,number,ci};

procedure cgp_hp(cgp);
   % CGP head polynomial. [cgp] is a CGP. Returns a DIP, the head
   % polynomial part of [cgp].
   cadr cgp;

procedure cgp_rp(cgp);
   % CGP rest polynomial. [cgp] is a CGP. Returns a DIP, the rest
   % polynomial part of [cgp].
   caddr cgp;

procedure cgp_sugar(cgp);
   % CGP sugar. [cgp] is a CGP. Returns a machine number, the sugar
   % part of [cgp].
   cadddr cgp;

procedure cgp_number(cgp);
   % CGP number. [cgp] is a CGP. Returns a machine number, the number
   % part of [cgp].
   nth(cgp,5);

procedure cgp_ci(cgp);
   % CGP number. [cgp] is a CGP. Returns an S-expr, the coloring %
   %  information of [cgp].
   nth(cgp,6);

procedure cgp_init(vars,sm,sx);
   % CGP init. [vars] is a list of variables. Returns an S-expr.
   % Initializing the DIP package.
   dip_init(vars,sm,sx);

procedure cgp_cleanup(l);
   % CGP clean-up. [l] is an S-expr returned by calling [cgp_init].
   dip_cleanup(l);

procedure cgp_lbc(u);
   % CGP leading base coefficient. [u] is a CGP. Returns the HC of the
   % rest part of [u].
   dip_lbc cgp_rp u;

procedure cgp_evlmon(u);
   % CGP exponent vector of leading monomial. [u] is a CGP. Returns
   % the HT of the rest part of [u].
   dip_evlmon cgp_rp u;

procedure cgp_zerop(u);
   % CGP zero predicate. [u] is a CGP. Returns [T] if [u] is the zero
   % polynomial.
   null cgp_hp u and null cgp_rp u;

procedure cgp_greenp(u);
   % CGP green predicate. [u] is a CGP. Returns [T] if [u] is
   % completely green colored.
   null cgp_rp u;

procedure cgp_monp(u);
   % CGP monomial predicate. [u] is a CGP. Returns [T] if [u] is a monomial.
   null cgp_hp u and dip_monp cgp_rp u;

procedure cgp_zero();
   % CGP zero. No argument. Returns the zero polynomial.
   cgp_mk(nil,nil,nil,nil,'zero);

procedure cgp_one();
   % CGP one. No argument. Returns a CGP, the polynomial one in CGP
   % representation.
   cgp_mk(nil,dip_one(),0,nil,'red);

procedure cgp_tdeg(u);
   % CGP total degree. [u] is a CGP. Returns the total degree of the
   % rest polynomial of [u].
   dip_tdeg cgp_rp u;

procedure cgp_mred(cgp);
   % CGP monomial reductum. [cgp] is a CGP. Returns a CGP $p$. $p$ is
   % computed from [cgp] by deleting the HM of the rest part of [cgp].
   cgp_mk(cgp_hp cgp,dip_mred cgp_rp cgp,cgp_sugar cgp,nil,'unknown);

procedure cgp_cp(cgp);
   % CGP copy. [cgp] is a CGP. Returns a CGP, the top-level copy of
   % [cgpl
   cgp_mk(cgp_hp cgp,cgp_rp cgp,cgp_sugar cgp,cgp_number cgp,cgp_ci cgp);

procedure cgp_f2cgp(u);
   % CGP form to cgp. [u] is a SF. Returns a CGP.
   cgp_mk(nil,dip_f2dip u,nil,nil,'unknown);

procedure cgp_2a(u);
   % CGP to algebraic. [u] is a CGP. Returns the AM representation of
   % [u].
   dip_2a dip_append(cgp_hp u,cgp_rp u);

procedure cgp_2f(u);
   % CGP to algebraic. [u] is a CGP. Returns the AM representation of
   % [u].
   dip_2f dip_append(cgp_hp u,cgp_rp u);

procedure cgp_enumerate(p);
   % CGP enumerate. [p] is a CGP. Returns a CGP. Sets the number of
   % [p] destructively to the next free number.
   cgp_setnumber(p,cgp_pcount!* := cgp_pcount!* #+ 1);

procedure cgp_unitp(p);
   % CGP unit predicate. [p] is a CGP with red HT. Returns [T] if [p]
   % is a unit.
   cgp_rp p and ev_zero!? cgp_evlmon p;

procedure cgp_setnumber(p,n);
   % CGP set number. [p] is a CGP; [n] is a machine number. Returns a
   % CGP. Sets the number of [p] destructively to [n].
   <<
      nth(p,5) := n;
      p
   >>;

procedure cgp_setsugar(p,s);
   % CGP set sugar. [p] is a CGP; [s] is a machine number. Returns a
   % CGP. Sets the sugar of [p] destructively to [s].
   <<
      nth(p,4) := s;
      p
   >>;

procedure cgp_setci(p,tg);
   % CGP set coloring information. [p] is a CGP; [tg] is an S-expr.
   % Returns a CGP. Sets the coloring information of [p] destructively
   % to [s].
   <<
      nth(p,6) := tg;
      p
   >>;

procedure cgp_condense(p);
   % CGP condense. [p] is a CGP. Returns a CGP. Condenses both the
   % head and the rest polynomial of [p].
   <<
      dip_condense cgp_hp p;
      dip_condense cgp_rp p;
      p
   >>;

procedure cgp_2scpl(p,cd,xvars);
   % CGP to strong cpl. [p] is a CGP; [cd] is a CD. Returns a list of
   % pairs $(...,(\gamma . p'),...)$, where $\gamma$ is a condition
   % and $p'$ is a CGP with red HC.
   if !*cgbgen and null xvars then
      cgp_2scpl!-gen(p,cd)
   else
      cgp_2scpl1(p,cd,xvars);

procedure cgp_2scpl1(p,cd,xvars);
   % CGP to strong cpl subroutine. [p] is a CGP; [cd] is a CD. Returns
   % a list of pairs $(...,(\gamma . p'),...)$, where $\gamma$ is a
   % condition and $p'$ is a CGP with red HC.
   begin scalar hp,rp,s,n,hc,ht,l,ncdeq,ncdneq;
      hp := cgp_hp p;
      if !*cgbgreen and hp then
         rederr {"cgp_2scpl1: Non empty hp",p};
      rp := cgp_rp p;
      s := cgp_sugar p;
      n := cgp_number p;
      while rp do <<
         hc := dip_lbc rp;
         ht := dip_evlmon rp;
         ncdeq := ncdneq := nil;
         if cd_surep(bc_mkat('neq,hc),cd) or
            eqcar(ncdeq := cd_siadd({bc_mkat('equal,hc)},cd),'false)
         then <<
            l := (cd . cgp_mk(hp,rp,s or dip_tdeg rp,n,'red)) . l;
            hc := 'break;
            rp := nil
         >> else if !*cgbgen and null intersection(xvars,bc_vars hc) then <<
            ncdneq := cd_siadd({bc_mkat('neq,hc)},cd);
            l := (ncdneq . cgp_mk(hp,rp,s or dip_tdeg rp,n,'red)) . l;
            hc := 'break;
            rp := nil
         >> else <<
            if not (cd_surep(bc_mkat('equal,hc),cd) or
               eqcar(ncdneq := cd_siadd({bc_mkat('neq,hc)},cd),'false))
            then <<
               ncdneq := ncdneq or cd_siadd({bc_mkat('neq,hc)},cd);
               ncdeq := ncdeq or cd_siadd({bc_mkat('equal,hc)},cd);
               l := (ncdneq . cgp_mk(hp,rp,s or dip_tdeg rp,n,'red)) . l;
               cd := ncdeq;
            >>;
            rp := dip_mred rp;
            if not(!*cgbgreen) then
               hp := dip_appendmon(hp,hc,ht);
         >>
      >>;
      if hc neq 'break then
          l := (cd . cgp_zero()) . l;
      return reversip l
   end;

procedure cgp_2scpl!-gen(p,cd);
   % CGP to strong cpl generic case. [p] is a CGP; [cd] is a CD. Returns
   % a list of one pair $((\gamma . p'))$, where $\gamma$ is a
   % condition and $p'$ is a CGP with red HC.
   begin scalar hp,rp;
      hp := cgp_hp p;
      rp := cgp_rp p;
      if null rp then
         return {cd . cgp_zero()};
      cd := cd_siadd({bc_mkat('neq,dip_lbc rp)},cd);
      return {cd . cgp_mk(hp,rp,cgp_sugar p or dip_tdeg rp,cgp_number p,'red)}
   end;

procedure cgp_ilcomb(p1,c1,t1,p2,c2,t2);
   % CGP integer linear combination. [p1], [p2] are CGP's; [c1], [c2]
   % are BC's; [t1], [t2] are EV's. Returns a CGP. Computes
   % $p1*c1^t1+p2*c2^t2$.
   begin scalar hp,rp,s;
      hp := dip_ilcomb(cgp_hp p1,c1,t1,cgp_hp p2,c2,t2);
      rp := dip_ilcomb(cgp_rp p1,c1,t1,cgp_rp p2,c2,t2);
      s := ev_max!#(cgp_sugar p1 #+ ev_tdeg t1,cgp_sugar p2 #+ ev_tdeg t2);
      return cgp_mk(hp,rp,s,nil,'unknown)  % TODO: Summe ?????
   end;

procedure cgp_ilcombr(p1,c1,p2,c2,t2);
   % CGP integer linear combination for reduction. [p1], [p2] are
   % CGP's; [c1], [c2] are BC's; [t2] is a EV's. Returns a CGP.
   % Computes $p1*c1+p2*c2^t2$.
   begin scalar hp,rp,s;
      hp := dip_ilcombr(cgp_hp p1,c1,cgp_hp p2,c2,t2);
      rp := dip_ilcombr(cgp_rp p1,c1,cgp_rp p2,c2,t2);
      s := ev_max!#(cgp_sugar p1,cgp_sugar p2 #+ ev_tdeg t2);
      return cgp_mk(hp,rp,s,nil,'unknown)
   end;

procedure cgp_hpcp(cgp);
   % CGP head polynomial copy. [cgp] is a CGP. Returns a CGP, in which
   % the head polynomial is copied.
   cgp_mk(dip_cp cgp_hp cgp,cgp_rp cgp,cgp_sugar cgp,
      cgp_number cgp,cgp_ci cgp);

procedure cgp_shift(p,xvars);
   % CGP shift. [p] is a CGP, which is neither zero nor green. Returns
   % a [CGP]. Shifts all leading green monomials from the rest part
   % into the head part.
   if !*cgbgen and null xvars then
      cgp_shift!-gen p
   else
      cgp_shift1(p,xvars);

procedure cgp_shift1(p,xvars);
   % CGP shift subroutine. [p] is a CGP, which is neither zero nor
   % green. Returns a [CGP]. Shifts all leading green monomials from
   % the rest part into the head part.
   begin scalar hp,rp,ht,hc,c;
      hp := cgp_hp p;
      rp := cgp_rp p;
      c := T;
      while c and rp do <<
         ht := dip_evlmon rp;
         hc := dip_lbc rp;
         if cd_surep(bc_mkat('equal,hc),cgb_cd!*) then <<
            if not(!*cgbgreen) then
               hp := dip_nconcmon(hp,hc,ht);
            rp := dip_mred rp
         >> else
            c := nil
      >>;
      if null rp and idp cgp_ci p then
         return cgp_zero();
      return cgp_mk(hp,rp,cgp_sugar p,cgp_number p,cgp_ci p)
   end;

procedure cgp_shift!-gen(p);
   % CGP shift generic case. [p] is a CGP, which is neither zero nor
   % green. Returns a [CGP]. Shifts all leading green monomials from
   % the rest part into the head part, i.e. we do nothing because
   % there are no green BC's.
   p;

procedure cgp_shiftwhite(p);
   % CGP shift white. [p] is a CGP, which is neither zero nor green.
   % Returns a [CGP]. Shifts the leading white monomials from the rest
   % part into the head part and set the wtl accordingly.
   begin scalar nhp,nci;
      nhp := dip_nconcmon(cgp_hp p,cgp_lbc p,cgp_evlmon p);
      nci := cgp_ci p;
      nci := 'mixed . (cgp_evlmon p . if idp nci then nil else cdr nci);
      return cgp_mk(nhp,dip_mred cgp_rp p,cgp_sugar p,cgp_number p,nci)
   end;

procedure cgp_backshift(p);
   % CGP back shift. [p] is a CGP. Returns a CGP. Shifts all white
   % monomials from the head part into the rest part using the wtl.
   begin scalar ci;
      ci := cgp_ci p;
      if not pairp ci or pairp ci and null cdr ci then
         return p;
      if cgp_rp p then
         rederr "cgp_backshift: Rest polynomial must be zero";
      return cgp_backshift1 p
   end;

procedure cgp_backshift1(p);
   % CGP back shift subroutine. [p] is a CGP. Returns a CGP. Shifts
   % all white monomials from the head part into the rest part using
   % the wtl.
   begin scalar hp,wtl,nhp;
      hp := cgp_hp p;
      wtl := cdr cgp_ci p;
      % TODO: Update condition
      while hp and not ev_member(dip_evlmon hp,wtl) do <<  % TODO: Destructive?
         nhp := dip_nconcmon(nhp,dip_lbc hp,dip_evlmon hp);
         hp := dip_mred hp
      >>;
      if hp then
         return cgp_mk(nhp,hp,cgp_sugar p,cgp_number p,'unknown);
      return cgp_zero()
   end;

procedure cgp_cancelmev(p,ev);
   % CGP cancel monomoial ev's. [p] is a CGP; [ev] is an EV. Returns a
   % CGP. Cancels all monomials in f which are multiples of [ev].
   cgp_mk(cgp_hp p,dip_cancelmev(cgp_rp p,ev),
      cgp_sugar p,cgp_number p,cgp_ci p);

procedure cgp_bcquot(p,c);
   % CGP base coefficient procuct. [p] is a CGP; [c] is a BC. Returns
   % a CGP. Computes $(1/[c])[p]$.
   cgp_mk(dip_bcquot(cgp_hp p,c),dip_bcquot(cgp_rp p,c),
      cgp_sugar p,cgp_number p,cgp_ci p);

procedure cgp_bcprod(p,c);
   % CGP base coefficient procuct. [p] is a CGP; [c] is a BC. Returns
   % a CGP. Computes $[c][p]$.
   cgp_mk(dip_bcprod(cgp_hp p,c),dip_bcprod(cgp_rp p,c),
      cgp_sugar p,cgp_number p,cgp_ci p);

procedure cgp_simpdcont(p);
   % CGP simplify domain content. [p] is a CGP. Returns a CGP $p'$
   % such that $p'$ is primitive as a multivariate polynomial over Z
   % and there is an integer $c$ such that $[p]=cp'$.
   begin scalar c;
      if cgp_zerop p then
          return p;
      c := cgp_dcont p;
      if bc_minus!? cgp_rlbc p then
         c := bc_neg c;
      return cgp_mk(dip_bcquot(cgp_hp p,c),dip_bcquot(cgp_rp p,c),
               cgp_sugar p,cgp_number p,cgp_ci p)
   end;

procedure cgp_rlbc(p);
   % CGP real leading base coefficient. [p] is a CGP. Returns a BC,
   % the coefficient of the largest term in both the head polynomial
   % and the rest polynomial part.
   if cgp_zerop p then
      bc_fd 0
   else if cgp_hp p then
      dip_lbc cgp_hp p
   else
      cgp_lbc p;

procedure cgp_dcont(p);
   % CGP domain content. [p] is a CGP. Returns a BC, the domain
   % content of [p], i.e. the content of [p] considered as an
   % multivariate polynomial over Z.
   begin scalar c;
      c := dip_dcont cgp_hp p;
      if bc_one!? c then
         return c;
      return dip_dcont1(cgp_rp p,c)
   end;

procedure cgp_normalize(u);
   % CGP normalize. [u] is a CGP. Returns a unique representation of
   % [u] as a CGP.
   cgp_mk(nil,dip_append(cgp_hp u,cgp_rp u),nil,nil,'unknown);

procedure cgp_green(u);
   % CGP green. [u] is A CGP. Returns a green CGP, i.e. a CGP in which
   % the green head part is cancelled.
   cgp_mk(nil,cgp_rp u,nil,nil,'green_colored);

procedure cgp_lsort(pl);
   % CGP list sort. pl is a list of CGP's. Returns a list of CGP's.
   sort(pl,function cgp_comp);

procedure cgp_comp(p1,p2);
   dip_comp(cgp_rp p1,cgp_rp p2);

endmodule;  % cgp

end;  % of file
