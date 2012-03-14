module allroot; % Routines for solving real polynomials by iteration.

% Author: Stanley L. Kameny <stan_kameny@rand.org>.

% Version and Date:  Mod 1.96, 30 March 1995.

% Copyright (c) 1988,1989,1990,1991,1992,1993,1994,1995.
% Stanley L. Kameny.  All Rights Reserved.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


Comment   modules bfauxil, bfdoer, bfdoer2, complxp, rootaux, realroot,
 nrstroot and multroot needed also;

exports accuroot, allroots, gfnewton, gfrootfind, sizatom;

imports !!mfefix, a2gf, accupr, allout, automod, bfabs, bfdivide,
        bfeqp, bfleqp, bflessp, bfloat, bfloatem, bfmax, bfnewton,
        bfnump, bfnzp, bfp!:, bfprim, bfrlmult, bfrndem, bfsqrt,
        bftimes, bfzp, ceillog, cexpand, ckacc, ckpzro, cpxp, csep,
        cvt5, decimal2internal, deflate1, deflate1c, deflate2, divbf,
        domainp, dsply, ep!:, errorp, errorset!*, geq, getprec, gf2bf,
        gf2flt, gfdiff, gfdiffer, gfdot, gfeqp, gfexit, gfgetmin,
        gfim, gfminus, gfnewtset, gfplus, gfquotient, gfrl, gfrlmult,
        gfrootset, gfrsq, gfrtrnd, gfshift, gfsqfrf, gfsqfrf1, gfsqrt,
        gfstorval, gftimes, gfval, gfzerop, im2gf, lastpair,
        leq, lprim, minbnd1, minprec, mkquote, ncpxp, nwterr,
        nwterrfx, orgshift, pbfprint, pconstr, pflupd, pmsg, powerchk,
        rerror, restorefl, rl2gf, rlrtno, rlval, rootrnd, round!:mt,
        rrpwr, rxgfc, rxgfrl, rxrl, seteps, setflbf, setprec, smpart,
        sqrt, timbf, trmsg1, trmsg10, trmsg11, trmsg12, trmsg13,
        trmsg2, trmsg4, trmsg6, trmsg7, trmsg8, trmsg9, unshift,
        xnshift, xnsiz, xoshift;

fluid '(!*trroot !*bftag !*rootmsg !*multiroot !*powergcd !*hardtst
 !*nosturm !2loop !*noinvert);
switch trroot,rootmsg,multiroot,nosturm;
fluid '(!*xnlist !*pfsav !*xmax !*xmax2 !*gfp pgcd!# allrl!#);
fluid '(!*pcmp prec!# acc!# sprec!# !*xn eps!# accm!# !*xobf froot!#);
fluid '(nwmax!# lgmax!# !*xo !*keepimp !1rp !*zx1 !*mb tht!# prm!#
 !*bfsh !*strm mltr!# emsg!# lims!# incmsg!$ cpxt!# sh!# pfl!# acfl!#
 pfactor!# rprec!# rr!# ss!# prx!# nrst!$ !*xd !*zp intv!# pnn!#);
global '(bfone!* bfhalf!* bfz!* cpval!# polnix!$ polrem!$ lm!#);
nwmax!# := 200; lgmax!# := 100;
!*multiroot := !*powergcd := t;

symbolic procedure gfrootfind(p,nx);
% p is expected to be in the form returned by gfform, and
% nx should be in the form (rl . im).  returns nx in form (rl . im).
   begin scalar p1,p2,px,x1,x2,x3,x0,xd,nx,n1,gfqt,xz,rsc,njp,lgerr,
     pf,xn2,t1,t2,!*pfsav,pf0,pf1,pfn,lp,xlim,fg,fg2,ip,ip0,nxl2;
      integer n,r,m,ni; lm!# := 0;
      !*xnlist := emsg!# := !*xd := nil; pmsg pbfprint p; trmsg8();
      !*pcmp := cpxp p;
      if caar p>0 then
    <<restorefl();
      rerror(roots,7,"Roots error: zero root out of sequence!")>>;
      if (n := caar lastpair p)=1 then
         <<p := cdar bfprim p;
           nx := gfminus if !*pcmp then p else rl2gf p;
           gfshift nil; trmsg11(nx,1); go to ret1>>;
      if nx and bfp!: car nx and not !*bftag then
         <<p := !*gfp; !*bftag := t>>;
      !*xo := rl2gf 0; seteps();
      lm!# := 2*nwterrfx(n,nil);
      if n<3 or !*hardtst or not xoshift(p,nx) then gfshift nil
         else <<p := gfshift p; pmsg('xo . !*xo)>>;
      nx := if not nx then rl2gf 0 else xnshift nx;
      p1 := gfdiff p;
      if gfzerop nx then xz := t;
      px := gfval(p,nx); bfmax p; !*zp := 0;
strt: pf := gfrsq px; trmsg13(n,nx,px);
      if bfzp pf then <<trmsg1 ('lag,nx); go to ret0>>;
      x1 := gfval(p1,nx);
    % avoid bad starting point using minbnd1 for offset.
      if (!*zx1 := not !*mb and bfnzp gfrsq x1) then go to st2;
      !*mb := x2 := nil; x1 := bfrlmult(2.0,minbnd1(p,nx));
      if !*keepimp then
         <<if !*pcmp or bfnzp gfim nx then x2 := gfdiffer(nx,im2gf x1);
           px := gfval(p,nx := gfplus(nx,im2gf x1));
           if not x2 then go to st1>>
       else
         <<x2 := gfdiffer(nx,rl2gf x1);
           px := gfval(p,nx := gfplus(nx,rl2gf x1))>>;
      if bflessp(gfrsq(p2 := gfval(p,x2)),gfrsq px) then
         <<nx := x2; px := p2>>;
 st1: xz := nil; go to strt;
 st2: n1 := n-1; p2 := gfdiff p1; xlim := bfrlmult(100.0,!*xmax2);
      ip := bfnzp gfim nx; nxl2 := {nx};
 lag: x3 := gfval(p2,nx);
      if not fg or bfzp gfrsq x1 then <<pmsg 0; go to lag0>>;
      gfqt := gfquotient(px,x1);
    % if newton is good enough, do it: it's cheaper.
      xn2 := gftimes(gftimes(gfqt,gfqt),
          gfrlmult(0.5,gfquotient(x3,x1)));
      t1 := bfabs gfdot(nx,xn2);
      t2 := bfabs bfrlmult(0.002,gfdot(nx,gfqt));
      pmsg if bfnzp t2 then gf2flt bfdivide(t1,t2) else "nwt_del->0";
      if bflessp(t1,t2) then go to ret;
lag0: x2 := gfrlmult(n1,
         gfdiffer(gfrlmult(n1,gftimes(x1,x1)),
           gfrlmult(n,gftimes(px,x3))));
      x2 := gfsqrt x2;
      x0 := nx; xd := gfplus(x1,x2); x2 := gfdiffer(x1,x2);
    % determine correct sign of x2 for Laguerre iteration.
      if bflessp(gfrsq xd,gfrsq x2) then xd := x2;
      if bfzp(x2 := gfrsq xd) then
         <<trmsg10 'lag; return(!*xnlist := nil)>>;
      if bflessp(bftimes(xlim,x2),bfrlmult(n*n,gfrsq px))
         then <<lp := t; xn2 := gfrsq nx; go to lag1>>;
      xd := gfrlmult(-n,gfquotient(px,xd)); nx := gfplus(x0,xd);
    % constrain iteration to circle of radius !*xmax=maxbound p,
    % by scaling root to radius !*xmax/2.
      if bflessp(xn2 := gfrsq nx,!*xmax2) then go to lag2;
lag1: if rsc then go to lag2;
      pf1 := pf0 := fg2 := !*pfsav := !*xnlist := nil;
      if lp then
         <<if !*trroot then <<write "root scaled"; terpri()>>;
           nx := if xz then rl2gf bfrlmult(0.5,!*xmax) else
              <<xn2 := bfrlmult(0.5,
                   if atom xn2 then sqrt(!*xmax2/xn2)
              else bfsqrt divbf(!*xmax2,xn2));
                 gfrlmult(xn2,nx)>>; rsc := t>>
      else if bflessp(xlim,xn2) then lp := t;
      pf := gfrsq(px := gfval(p,nx)); go to lag3;
lag2: if !*xnlist then
         for each y in !*xnlist do if nx=cdr y then njp := t;
      pf := gfrsq(px := gfval(p,nx));
    % test for minimum in envelope of pf, but not on first iter.
      if not fg then fg := t else <<pf1 := pf0; pf0 := pfn>>;
    % if root has just turned complex, allow to settle.
      ip0 := ip; ip := bfnzp gfim nx;
      if ip and not ip0 then
         <<pf1 := pf0 := fg2 := !*pfsav := nil; ni := 1>>;
      pfn := pflupd pf;
      if pf1 then
         <<if not fg2 then <<if bflessp(pfn,pf0) then fg2 := t>> else
            <<if bflessp(pf0,pfn) or bfeqp(pf0,pf1) and bfeqp(pf0,pfn)
                then go to newt>> >>;
      if xz then xz := nil else gfstorval(pf,nx);
      pmsg mapcar(!*pfsav,function gf2flt);
lag3: trmsg2('lag,nx,px); r := r+1; ni := ni+1;
      if (xd := gfexit(pf,nx,x0,'lag))=t then go to ret0
        % m logic delays this exit to allow settling.
         else if xd and (m := m+1) > 5
         then
            <<if gfeqp(nx,xd) then go to ret0
              else if r>2 then <<!*xd := nx := xd; go to ret2>> >>
         else if njp then go to newt;
      if not xd then m := 0;
     % this logic looks for loops of length 2 or lag limit exceeded.
      if ni>5 and gfeqp(car nxl2,nx) or (lgerr := (r>lgmax!#+lm!#)) then
        <<lprim(if lgerr then {"max LAG limit",lgmax!#+lm!#,"exceeded"}
             else "lag loop of length 2 found.");
          !2loop := t; return(!*xnlist := nil)>> else
        if length(nxl2 := nconc(nxl2,{nx})) > 2 then nxl2 := cdr nxl2;
      x1 := gfval(p1,nx); go to lag;
ret1: nx := unshift nx; go to ret2;
newt: nx := gfgetmin();
 ret: return gfnewt2(p,p1,nx,4);
ret0: nx := unshift nx;
ret2: !*xnlist := nil; dsply nx; return !*xn := nx end;

symbolic procedure pshift p;
   orgshift(p,if cpxp p then !*xo else gfrl !*xo);

symbolic procedure gfnewton(p,nx,k);
   <<p := pshift p; gfnewt2(p,gfdiff p,xnshift nx,k)>>;

symbolic procedure gfnewt2(p,p1,nx,kmax);
   begin scalar pf0,pf,k,xk,loop,x0,x1,xd,px,rl; integer m,tht!#;
      !*xnlist := emsg!# := !*xd := nil; pmsg pbfprint p; trmsg8();
      if (rl := bfzp gfim nx) and ncpxp p then
         <<!*bfsh := t;
           nx := rl2gf bfnewton(p,p1,gfrl nx,intv!#,kmax);
           !*bfsh := nil; go to ret1>>;
      seteps(); !*zp := lm!# := 0;
      lm!# := nwterrfx(caar lastpair p,t);
      if gfzerop(px := gfval(p,nx)) then
         <<trmsg1('nwt,nx); go to ret1>>;
      gfstorval(gfrsq px,nx);
 ne0: x0 := nx;
      if gfzerop(x1 := gfval(p1,nx)) then
         <<trmsg10 'nwt; go to ret1>>;
      nx := gfdiffer(nx,gfquotient(px,x1)); pf0 := pf;
      for each y in !*xnlist do if nx=cdr y then loop := t;
      gfstorval(pf := gfrsq(px := gfval(p,nx)),nx);
      pmsg list gf2flt pf;
    % test for loop, but not on first iteration.
      if pf0 and bfleqp(pf0,pf) then
         <<loop := t;
           if kmax<2 then <<trmsg2('loop,nx,px); go to ret>> >>;
      trmsg2(if loop then 'loop else 'nwt,nx,px);
      if (xd := gfexit(pf,nx,x0,'nwt)) then
            <<if xd=t or gfeqp(nx,xd) then go to ret1
              else if m>0 then <<!*xd := nx := xd; go to ret2>> >>;
      if not loop then go to nlp;
    % next section updates loop variables.
      if k then <<xk := gfplus(xk,nx); k := k+1>>
         else <<k := 1; xk := nx>>;
      if k>=kmax then
         <<nx:=gfrlmult(1.0/k,xk);
           gfstorval(gfrsq(px := gfval(p,nx)),nx);
           trmsg6(k,nx,px); goto ret>>;
 nlp: nwterr(m := m+1); go to ne0;
 ret: nx := gfgetmin();trmsg7 nx;
ret1: nx := unshift nx;
ret2: !*xnlist := nil; dsply nx; return !*xn := nx end;

symbolic procedure accuroot(y,p,xo);  % p,xo,!*xn all bfloat
   begin scalar rprec,b,c,n,rl,x,pr0,ps,y0;
      ps := getprec(); rl := bfnump (y0 := y := gf2bf y); b := !*bftag;
      pr0 := minprec();
      !*xo := xo; !*bftag := t;
      if (n := caar lastpair p)<2 then
         <<setprec max(ps,acc!#+2);
           if prx!# then setprec max(getprec(),prx!#);
           y := gfrootfind(p,nil); go to ret>>;
      x := if rl then gfrl xnshift (y := rl2gf y) else xnshift y;
      if not(rprec := prreq(p,x,rl)) then <<setflbf b; return nil>>;
      if not (allrl!# or rl) and (bfzp gfim y or bfzp gfrl y)
         then !*xd := 1;
      if rprec<=pr0 then
         <<setprec pr0; if !*xd then go to bfp else go to ret>>;
      setprec rprec;
 bfp: y := if not rl and
            (rprec>=2*pr0 or bfzp gfim y or bfzp gfrl y)
         or !*xd then gfrootfind(p,y) else
            <<trmsg8(); gfnewton(p,y,if rl then 2 else 0)>>;
      if !*xd then <<setprec((3*getprec())/2);
        go to bfp>>;
 ret: if acfl!# then
         <<setprec(prec!# := prm!# := max(rprec,prm!#)); go to r3>>;
      prec!# := getprec();
      if rl or n<2 or not (c := smpart y) then go to r2;
      setprec(prec!# + 1);
      x := gfnewton(p,y := gf2bf !*xn,0);
      y := !*xn :=
         if c=t then
           if not !*pcmp and cvt5(gfrl y,gfrl x)
              and not cvt5(gfim y,gfim x)
             then rl2gf gfrl y else y
         else if cvt5(gfim y,gfim x) and not cvt5(gfrl y,gfrl x)
              then im2gf gfim y else y;
  r2: setprec ps;
      if not rl and
         (bfzp gfrl y and bfnzp gfrl y0
            or bfzp gfim y and bfnzp gfim y0)
        then acc!# := max(acc!#,accupr(p,if pgcd!# then p else !1rp,y));
  r3: y := if rl then rootrnd gfrl y else gfrtrnd y;
      trmsg12 y;
      setflbf b; !*xn := gf2bf !*xn;
      return y end;

symbolic procedure prreq(p,x,rl);
  % find required precision to find root at x in polynomial p.
   begin scalar p1,x1,rx;
      p1 := gfdiff pshift p;
      if rl and ncpxp p then
         <<x1 := bfabs rlval(p1,x); rx := rxrl(p1,x)>>
      else
         <<rx := if cpxp p then rxgfc(p1,x) else rxgfrl(p1,x);
           x1 := bfsqrt gfrsq gfval(p1,x)>>;
      return if bfzp x1 then nil else
        <<x := getprec(); setprec 8;
          rl := ep!: round!:mt(
              divbf(timbf(rx,decimal2internal (1, (acc!#+2))),x1),1);
          setprec x; 1 + ceiling (rl / log2of10)>> end;

symbolic procedure sizatom u;
   begin scalar c,x; c := !*complex; on complex;
     x := prepsq simp!* u;
     if not c then off complex;
     if x neq u then return x
       else rerror(roots,8,"non-numeric value") end;

symbolic procedure dsplyrtno m;
   (<< write "rootno. ",m; wrs n>> where n=wrs nil);

symbolic procedure allroots(p,p0); % p is always bfloated at this call.
Comment   With modifications for nosturm and offset iteration and root
    inversion.$
  % do the actual work of finding roots of p in appropriate environment.
   begin scalar q,n,n0,c,cc,cprq,rln,cpn,qf,ac,y,er,rl,z,mb,inc,prec,xo,
        pf,xof,qbf,sprec,b,red,sw,pfl!#,acfl,acfl!#,!*msg,prq,allrl!#,
        invp,invtd!*,pinv,!1rpinv,!1rp0,nmfg,p00,zi;
        integer req,npi,accm!#,prec!#,r15n,prm!#,k,rtn,invpb;
      prec := getprec(); polrem!$ := polnix!$ := nil; !*msg := t;
      ac := acc!#; n0 := caar lastpair p; pgcd!# := red := not p0;
      b := !*bftag; sprec := minprec(); invpb := n0/2;
      !*pcmp := cpxp p;
      if !*nosturm then req := nil else
         <<if not !*pcmp then req := if n0=1 then 1 else rlrtno p;
           if req>0 then trmsg4 req>>;
     % req = <no of real roots to determine unless nosturm is on>.
     % rtn is the number of separate root computations - 1 = max number
     % of restarts required.
      rtn := (if !*pcmp or !*nosturm then n0 else (n0+req)/2) - 1;
     % save original values of p and !1rp.
      p00 := p; !1rp0 := !1rp;
     %don't bother with inv mechanism if n0<11 unless !*noinvert="test".
      if !*noinvert="test" or n0>10 and not !*noinvert then
         <<pinv := invpoly p; !1rpinv := invpoly !1rp;
           if !*noinvert="test" or
                 not bfleqp(maxbnd1 pinv,maxbnd1 p) then  %% not perfect
              <<if !*trroot or !*rootmsg then
                   lprim "inverting initially!";
                   nmfg := t; go to inv>> >>;
      go to st0;
 tlp: if invtd!* then go to abrt else % prevents looping through tlp.
         <<if !*trroot or !*rootmsg then lprim
              "inverted polynomial tried";
           invtd!* := t>>;
 inv: k := 0;
      if not pinv then
         <<pinv := invpoly p00 ; !1rpinv := invpoly !1rp0>>;
     % toggle {p,!1rp,invp} from {p00,!1rp0,nil} to {pinv,!1rpinv,t}.
     % the first time only that invp is turned on, increase sprec.
      if (invp := not invp) then
         <<p := pinv; !1rp := !1rpinv>>
         else <<p := p00; !1rp := !1rp0>>;
     % increase precision the first time thru inv: when nmfb is off.
      if not nmfg and invpb neq 0 then
         <<prec := prec+invpb; setprec(prec-2); sprec := minprec();
           invpb := 0 ;if !*bftag then b := t>>;
strt: if prq and (k := k+1)>rtn then go to abrt; mb := nil;
      if (!*rootmsg or !*rootmsg) and not nmfg then
     (<<write "allroots restart at prec ",getprec(); terpri(); wrs ch>>
       where ch=wrs nil);
 st0: n := n0; !*gfp := qbf := p; c := cc := pf := prq := nil;
      if not !*nosturm then
         <<cprq := n-req; if not !*pcmp then cprq := cprq/2>>;
      rln := cpn := prm!# := 0;
root: qf := mb := !*mb := nmfg := nil;
      if not !*nosturm then allrl!# := cpn = cprq;
      if b then <<q := qbf; go to r0>>;
      q := if errorp(q := errorset!*({'cflotem,mkquote qbf},nil))
         then <<b := !*bftag := t; qbf>> else (qf := car q);
  r0: acc!# := ac; if not !*nosturm then !*keepimp := req-rln=0;
  r1: if !*rootmsg then dsplyrtno(1+n0-n);
      y := gfrootset(q,nil,b);
      if !2loop then <<!2loop := nil; go to tlp>>;
      r15n := 0;
      acfl := acfl!# := pfl!# := nil;
      if n=n0 then
         <<xo := !*xobf := gf2bf !*xo; p0 := qbf;
           if not b then <<xof := !*xo; pf := q>> >>;
      if not y then <<er := t; go to fl>>;
      if not (y := ckacc(qbf,if red then p0 else !1rp,gf2bf !*xn))
         then <<trmsg10 "ckacc"; go to inc0>>;
      if princreq(n,bfzp gfim y,sprec) then
        <<prq := t; sw := prec!#;
          if n>2 then sw := sw+n0; go to fl>>;
 r15: if(r15n := r15n+1)>3 then go to abrt;
      if invp or n0>2 and n0>n then
         <<if !*trroot
             then <<write "q(",n,") -> ";
                    print_the_number gf2bf y; terpri()>>;
           y := if not pf or bfp!: car !*xn
             then gfnewtset(n0,p0,!*xn,xo,b)
             else gfnewtset(n0,pf,!*xn,xof,b);
           if not y then <<trmsg10 "gfnewtset"; go to inc0>> >>;
      if acfl then
         <<pfl!# := t;
           y := ckacc(qbf,if red then p0 else !1rp,gf2bf !*xn)>>;
      if !*trroot
       then <<write "p(",n,") -> ";print_the_number gf2bf y; terpri()>>;
      if gfzerop y then <<incmsg!$ := "illegal zero root"; go to incr>>;
      if not (y := accuroot(!*xn,p0,xo)) then
            <<trmsg10 "accuroot"; go to inc0>>;
      rl := bfzp gfim y;
      if princreq(n,rl,sprec) then
        <<prq := t; sw := prec!#; if n>3 then sw := sw+2; go to fl>>;
  r2: if not !*nosturm then
         (if rl then
            <<y := gfrl y; if rln+1>req then
               <<incmsg!$ := "excess real root"; go to incr>> >>
            else if cpn+1>cprq then
               <<incmsg!$ := "excess complex root"; go to incr>>);
      z := gf2bf(if rl then gfrl !*xn else !*xn); % set by lag or nwt.
      if not rl and not !*pcmp then
       <<y := (car y) . bfabs cdr y; z := (car z) . bfabs cdr z>>;
      if c and member(y,c) then
         <<incmsg!$ := "equal roots found"; go to incr>>;
      if rl then rln := rln+1 else cpn := cpn+1;
      c := y . c; %mb := nil;
  Comment  If we are using the inverse polynomial, then we need to
    find roots at increased accuracy and precision, to allow for loss of
    accuracy in taking the inverse.  Gfrootfind always provides that
    additional accuracy in the unrounded root z (which is used for
    deflation), so it is a simple matter to invert the root before
    rounding.  When the rounding is done, using gfrtrnd, the binary
    bigfloat result is the output of gfrtrnd, and the decimal equivalent
is
    returned as the value of the global variable cpval!#. $
      if invp then
         <<zi := gf2bf z;
              if red then zi := if rl then bfquotient(bfone!*,zi)
                 else gfquotient(bfone!* . bfz!*,zi)
              else
                gfrtrnd gfquotient(bfone!* . bfz!*,
                  if rl then (zi . bfz!*) else zi)>>;
      if not (rl or red or !*pcmp) then
         cpval!# := (car cpval!#) . (abs cadr cpval!#) . cddr cpval!#;
      cc := ((if red then if invp then zi else z else mkdn cpval!#)
                  . acc!#) . cc;
     % the output list cc will be either z or :dn: objects, so
     % output functions will have to be clever!
     % c is rounded roots list used in testing for equal roots.
      if !*trroot then terpri();
     % firstroot computes first root found only. It could be wrong.
      if froot!# then goto ret;
    % new logic does all deflation in bfloat for greater accuracy.
      z := gf2bf z; q := bfloatem q;
      if (rl or !*pcmp) and (n := n-1)>0 and
         (q := cdr(if rl then deflate1(q,z) else deflate1c(q,z)))
         or (n := n-2)>0 and (q := deflate2(q,z)) then
            <<qbf := bfrndem q; go to root>>;
 ret: setprec max(prec,(acc!# := ac)+2);
      setflbf b; return cexpand cc;
incr: lprim incmsg!$; polnix!$ := q;
      if mb then go to tlp
         else if !*zx1 then
            <<lprim "offset iteration attempted";
              !*mb := mb := t; go to r1>>;
inc0: if (npi := npi+1)<=3 then go to inc1;
abrt: lprim list("root finding aborted. Deflate degree = ",n);
      lprim list("poly = ",q); terpri();
      if n0>n then polrem!$ := q; go to ret;
inc1: inc := max(n0,sprec/2);
      setprec(sprec := max(sprec+inc,2+2*acc!#)); trmsg9 sprec;
      if b then go to strt;
  fl: p := p0; xo := !*xo := gf2bf xo;
      b := !*bftag := t; !1rp := bfloatem !1rp;
      if er then <<er := nil; q := qbf; go to r1>>;
      acfl := t;
      if sw then
     % precision has increased: backup point depends on n.
         <<setprec (sprec := sw); sw := nil; q := p;
           if n=n0 then <<y := gf2bf y; go to r15>> >>;
      go to strt end;

symbolic procedure princreq(n,rl,sprec);
   (n>2 or (rl or !*pcmp) and n>1) and min(prec!#,2*(accm!#+1))>sprec;

endmodule;


end;
