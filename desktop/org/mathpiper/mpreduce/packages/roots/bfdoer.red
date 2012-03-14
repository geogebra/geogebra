module bfdoer; % routines for doing bfloat arithmetic, mixed float
               % and bf arithmetic, gf and gbf arithmetic, rational
               % arithmetic and fast polynomial manipulations and form
               % conversion.

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


Comment  support for allroot and realroot modules;

exports bfeqp, bfleqp, bfloatem, bfmin, bfsgn, cflotem,
        ckacc, deflate2, firstroot, gfconj, gfdiff, gfeqp, gfexit,
        gfnewt, gfnewtset, gfplusn, gfrlmult, gfroot, gfrootset,
        gftimesn, gfval, gtag, gzero, intdiff, isolater, listecho,
        maxbnd1, maxbound, minbnd1, minbound, mkratl, ncoeffs,
        nearestroot, powerchk, primp, primpn, r2flbf, ratdif,
        ratleqp, ratlessp, ratmax, ratmean, ratmin, ratminus, ratplus,
        realroots, rlrootno, rlrtno, rlval, root_val, rtreorder, sch,
        simpgi, univar, ungfform, unshift, xnshift;

imports a2gf, abs!:, accupr, accupr1, accuroot, automod, bf2flr, bfabs,
        bfinverse, bfloat, bfminus, bfnump, bfnzp, bfp!:, bfrlmult,
        bfsqrt, bfzp, cflot, ckprec, cpxp, cvt2, cvt5, difference!:,
        divbf, domainp, ep!:, eqcar, equal!:, errorp, errorset!*, exp,
        exptbf, gbfmult, gcdn, getprec, gf2bf, gfdiffer, gffinit,
        gffmult, gffplus, gfftimes, gfim, gfnewton, gfplus, gfrl,
        gfrsq, gfrtrnd, gftimes, gfzerop, grpbf, im2gf, infinityp,
        invbf, invpoly, isolatep, lastpair, leadatom, leq, lessp!:,
        limchk, log, make!:ibf, min!:, minus!:, minusp!:, mk!*sq,
        mkquote, mkxcl, msd!:, mt!:, ncpxp, neq, nrstroot, numr, off,
        on, orgshift, outmode, over, plubf, plus!:, preci!:, precision,
        r2bf, r2fl, realrat, rerror, restorefl, rl2gf, rlrtno2, rndpwr,
        rootrnd, roots, schinf, schplus, setflbf, setprec, sgn, sgn1,
        simp!*, sturm1, timbf, times!:, trmsg1, trmsg12, trmsg13,
        trmsg3, uniroots, univariatep;


fluid '(!*bftag !*pcmp !*rvar !*strm lims!# mltr!# emsg!# !*xo !*gfp
        !*powergcd pfl!# acfl!# accm!# froot!# !*backtrace);

fluid '(acc!# sprec!# pfactor!# rr!# ss!# !*xn !*zp !*xobf !*noeqns
        !*msg rootacc!# rootacc!#!#);

fluid '(iniprec!#);

global '(bfz!* bfone!* bfhalf!* bftwo!* prd!% log2 bfee!* !!ee);

bfee!* := bfloat !!ee;

symbolic smacro procedure dnp x; eqcar(x,'!:dn!:);

symbolic procedure bfleqp(a,b); if atom a then a<=b else not grpbf(a,b);

symbolic procedure bfeqp(a,b); if atom a then a=b else
   ((zerop ma and zerop mb or ep!: a=ep!: b and ma=mb)
    where ma=mt!: a,mb=mt!: b);

symbolic procedure bfsgn u;
   if atom u then sgn u else sgn mt!: u;

symbolic procedure bfmin(u,v);
   if atom u then min(u,v) else min!:(u,v);

symbolic procedure gfconj u; (car u) . (bfminus cdr u);

symbolic procedure gfrlmult(r,u); % multiplies real*gf or real*gbf.
   if atom car u then gffmult(r,u) else gbfmult(bfloat r,u);

symbolic procedure gfeqp(u,v); gfzerop gfdiffer(u,v);

symbolic procedure ncoeffs p;
    begin scalar n,q; integer d;
          for each i in p do
             <<n := car i;
               while d<n  do <<q:= nil . q; d := d+1>>;
               d := d+1; q := (cdr i) . q>>;
          return n . q end;

symbolic procedure rlval(p,r);
 % evaluate real polynomial for floating or bigfloat value.
   if atom p or atom car p then p
      else if bfzp r then (if caar p=0 then cdar p else r2flbf 0) else
   begin scalar c,bf; bf := bfp!: r;
         c := car (p := cdr ncoeffs p);
         for each i in cdr p do
            <<c := if bf then times!:(r,c) else r*c;
              if i then
                 c := (if bf then plus!:(i,c) else c + i)>>;
         return if bf then rndpwr c else c end;

symbolic smacro procedure sqr!: a; times!:(a,a);

symbolic procedure deflate2 (p,u);
 % deflate real bf polynomial by one pair of gbf roots.
 % no rounding is done.
    begin scalar q,n,b,c,f,g,h,j;
       b := times!:(bftwo!*,gfrl u);
       c := minus!: plus!:(sqr!: gfrl u,sqr!: gfim u);
       g := h := bfz!*; n := car(p := ncoeffs p)-1; p := cdr p;
       while n>0 do
          <<n := n-1;
            f := plus!:(times!:(b,g),times!:(c,h));
            if (j := car p) then f := plus!:(j,f);
            if mt!: f neq 0 then q := (n . f) . q;
            h := g; g := f; p := cdr p>>;
       return q end;

symbolic procedure primp p; if atom p then sgn p else
   begin integer d;
         for each y in p do d := gcdn(d,cdr y);
         return for each y in p collect (car y).(cdr y/d) end;

symbolic procedure primpn p;
   begin scalar n,g; n := car p; p := cdr p; g := 0;
         while p and car p=0 do <<p := cdr p; n := n-1>>;
         if n<0 then return 0 else if n=0 then return sgn car p;
         for each y in p do g := gcdn(y,g);
         return n . for each y in p collect y/g end;

symbolic procedure r2flbf u; if !*bftag then r2bf u else r2fl u;
 % translate any real number object to float or bigfloat.

symbolic procedure intdiff p;
   <<if caar p=0 then p := cdr p;
     for each y in p collect (car y-1) . (car y*cdr y)>>;

symbolic procedure ratminus r; (-car r) . (cdr r);

symbolic procedure ratdif(r,s); ratplusm(r,ratminus s,nil);

symbolic procedure ratplus(r,s); ratplusm(r,s,nil);

symbolic procedure ratmean(r,s); ratplusm(r,s,t);

symbolic procedure ratplusm(r,s,m);
 % computes sum or mean of two realrats.
   begin scalar ra,rd,sa,sd,a,d,g;
         ra := car r; rd := cdr r; sa := car s; sd := cdr s;
         if rd=sd then <<a := ra+sa; d := rd>> else
         <<g := gcdn(rd,sd); a := sd/g*ra+rd/g*sa; d := rd/g*sd>>;
         if m then d := d+d;
         if a=0 then return (0 . 1);
         g := gcdn(a,d); return (a/g) . (d/g) end;

symbolic procedure ratmin(a,b); if ratlessp(a,b) then a else b;

symbolic procedure ratmax(a,b); if ratlessp(a,b) then b else a;

symbolic procedure ratlessp(a,b); car ratdif(a,b)<0;

symbolic procedure ratleqp(a,b); car ratdif(a,b)<=0;

symbolic procedure listecho(l,n); if n<2 then l else
   begin scalar c; for each x in l do
         <<for i := 1:n do c := append(c, list x)>>; return c end;

symbolic procedure bfloatem p; if p then
   (<<for each c in p collect (car c) .
       if cp then (bfloat cadr c) . bfloat cddr c else bfloat cdr c>>
   where cp=cpxp p);

symbolic procedure cflotem p;
   <<for each c in p collect (car c) .
      if cp then (cflot cadr c) . cflot cddr c else cflot cdr c>>
   where cp=cpxp p;

symbolic procedure gfdiff p;
 % differentiates the gfform of real or complex polynomial.
   <<if caar p=0 then p:= cdr p;
  if ncpxp p then
     for each a in p collect (car a-1) . bfrlmult(car a,cdr a) else
     for each a in p collect (car a-1) . gftimes(rl2gf car a,cdr a)>>;

symbolic procedure gfval(p,x);
   <<p := if cpxp p then gfcval(p,x) else gfrval(p,x);
     if atom gfrl p and (infinityp gfrl p or infinityp gfim p)
        then error(0,"gfval -> infinity") else p>>;

symbolic smacro procedure rndpwrxc(x,c);
   if atom gfrl x then c else (rndpwr gfrl c) . rndpwr gfim c;

symbolic procedure gfrval(p,x);
 % evaluate real polynomial for gf or gbf value x.
   if gfzerop x then rl2gf(if caar p=0 then cdar p else 0)
   else if bfzp gfim x then rl2gf rlval(p,gfrl x) else
   begin scalar c;
         c := rl2gf car (p := cdr ncoeffs p);
         for each i in cdr p do
            <<c := gftimesn(x,c);
              if i then c := ((bfplusn(i,gfrl c)) . gfim c)>>;
         return rndpwrxc(x,c) end;

symbolic procedure gfcval(p,x);
 % evaluate complex polynomial for gf or gbf value x.
   if gfzerop x then (if caar p=0 then cdar p else rl2gf 0) else
   begin scalar c;
         c := car (p := cdr ncoeffs p);
         for each i in cdr p do
            <<c := gftimesn(x,c); if i then c := gfplusn(i,c)>>;
         return rndpwrxc(x,c) end;

symbolic procedure bfplusn(u,v); if atom u then u+v else plus!:(u,v);

symbolic procedure gfplusn(u,v);
   if atom gfrl u then gffplus(u,v) else
     (plus!:(gfrl u,gfrl v)) . plus!:(gfim u,gfim v);

symbolic procedure gftimesn(u,v);
   if atom gfrl u then gfftimes(u,v) else
     begin scalar ru,iu,rv,iv;
        ru := gfrl u; iu := gfim u; rv := gfrl v; iv := gfim v;
        return (difference!:(times!:(ru,rv),times!:(iu,iv))) .
           plus!:(times!:(ru,iv),times!:(iu,rv)) end;

symbolic procedure minbound (p,o);
 % estimate min root distance from origin o for polynomial p.
   <<p := if atom (p := automod ckprec p) then nil else minbnd1(p,o);
     !*bftag := bf; restorefl(); outmode p>> where bf=!*bftag,acc!#=6;

symbolic operator maxbound,minbound;

symbolic procedure maxbound p;
  <<p := maxbnd1 ckprec p; restorefl(); outmode p>>;

symbolic procedure maxbnd1 p;
 % maxbound of roots of real or complex float polynomial,
 % in floating point avoiding under/ and over/ flows.
   begin scalar nc,bf,m,pr;
      bf := !*bftag; pr := getprec();
      if atom (p:= gffinit p) then return nil;
      setprec 8; p := bfloatrd p;
      nc := ncpxp p; p := reverse p;
      !*bftag := bf;
      m := bfrlmult(2,maxbdbf(p,nc));
      setprec pr;
      return m end;

symbolic procedure minbnd1(p,org);
   begin scalar b,c; b := !*bftag;
         setflbf bfp!: if not bfnump(c := cdar p) then car c else c;
         org := a2gf org;
         if ncpxp p then if bfzp gfim org then org := gfrl org else
            p := for each r in p collect (car r) . rl2gf cdr r;
         p := bfinverse maxbnd1 invpoly orgshift(p,org);
         setflbf b; return p end;

symbolic procedure maxbdbf(p,nc);
   begin scalar an,al,m,mm,n;
   % selection of critical term uses bfloat arithmetic; final
   % computation uses float.
      n := car (an := car p);
      an := if nc then bfabs cdr an else bfsqrt gfrsq cdr an;
      while (p := cdr p) do
       <<al := if nc then bfabs cdar p else bfsqrt gfrsq cdar p;
         mm := not zerop mt!: al and logrtn(divbf(al,an),n-caar p);
         if not m or mm and mm>m then m := mm>>;
      m := fl2bfexp m;
      return m end;

symbolic procedure bfloatrd p;
   <<for each c in p collect (car c) .
      if cp then (rndpwr bfloat cadr c) . rndpwr bfloat cddr c
         else rndpwr bfloat cdr c>>
   where cp=cpxp p;

symbolic procedure logrtn(x,n);
  % floating log of x**(1/n) using bfloat logic as boost.
  (y/n) where y=log(m/2.0**p)+(p+ep!: x)*log2
        where p=msd!: m-1 where m=mt!: x;

symbolic procedure fl2bfexp m; if !*bftag then expfl2bf m else exp m;

symbolic procedure expfl2bf m; if m<0 then invbf expfl2bf(-m) else
   exptbf(bfee!*,mi,bfloat exp mf) where mf=m-mi where mi=fix m;

symbolic procedure ungfform p;
   begin scalar r;
      if caar p=0 then <<if bfnzp cdar p then r := cdar p; p := cdr p>>;
      for each i in p do
        if bfnzp cdr i then r := (((!*rvar or 'x) . car i) . cdr i) . r;
      return r end;

symbolic procedure gtag c; if fixp c then '!:gi!: else '!:cr!:;

symbolic procedure gzero c;
   if fixp c then 0 else if floatp c then 0.0 else bfz!*;

symbolic procedure simpgi u; ('!:gi!: . u) ./ 1;

put('!:gi!:,'simpfn,'simpgi);

symbolic procedure rlrtno p;
   <<sturm1 p; p := schinf(-1)-schinf 1; !*strm := nil; p>>;

symbolic procedure roots p;
   <<lims!# := nil; uniroots(car p,1)>> where froot!#=nil;

symbolic procedure firstroot p;
   <<lims!# := nil; uniroots(car p,1)>> where froot!#=t;

symbolic procedure root_val x;
  % Produces list of root values at system precision (or greater if
  % required to separate roots.)
   roots x
      where rootacc!#!#=p,iniprec!#=p where !*msg=nil,p=precision 0;

for each n in '(roots firstroot root_val) do put(n,'psopfn,n);

symbolic procedure outril p;
   'list . for each i in p collect 'list . {mk!*sq car i,mk!*sq cdr i};

symbolic procedure gfrootset(p,r,b);
 if errorp
   (r := errorset!*({'gfrootfind,mkquote p,mkquote r},!*backtrace))
    then gfsetmsg(r,b,'gfrootfind) else car r;

symbolic procedure gfsetmsg(r,b,n);
   if (r := emsg!#) then <<emsg!# := nil; rerror(roots,1,r)>>
   else if b
    then rerror(roots,2,list(n,": error in bfloat computation"))
   else nil;

symbolic procedure sch z;
   begin scalar v,v1; integer r;
         v := sgn1(car !*strm,z);
         if v=0 and mltr!# then return schplus z;
         for each q in cdr !*strm do
            <<if v*(v1:= sgn1(q,z))<0 then r := r+1;
              if v1 neq 0 then v := v1>>;
         return r end;

symbolic procedure gfnewtset(n,p,y,xo,b);
   begin scalar y1,b;
      if (b := !*bftag) then go to ret;
      if not atom car y then go to mbf;
      if not errorp (y1 := errorset!*
        ({'gfns1,n,mkquote p,mkquote y,mkquote xo},!*backtrace))
         then return car y1;
 mbf: gfsetmsg(y1,b,'gfnewton);
      p := !*gfp; !*xo := xo := gf2bf xo; y := gf2bf y; !*bftag := t;
 ret: y := gfns1(n,p,y,xo); !*bftag := b; return y end;

symbolic procedure gfns1(n,p,y,xo);
  <<!*xo := xo; trmsg13(n,xnshift y,gfval(p,y)); gfnewton(p,y,4)>>;

symbolic procedure gfnewt args;
   nrstroot(gffinit p,r,if cpx then 0 else t)
    where rootacc!#!#=pr,rprec!#=pr
    where p=car args,r=cadr args,cpx=caddr args,pr=precision 0;
   % direct call to gfnewton. If cpx then retain imaginary part, no
   % matter how small (but either p or r must be complex).

symbolic procedure gfroot args;
   nrstroot(gffinit p,r,list if cpx then 0 else t)
    where rootacc!#!#=pr,rprec!#=pr
    where p=car args,r=cadr args,cpx=caddr args,pr=precision 0;
   % direct call to gfrootfind. If cpx then retain imaginary part, no
   % matter how small (but precision will have to be high enough).

symbolic(for each n in '(gfnewt gfroot) do put(n,'psopfn,n));

symbolic procedure univar y;
   (if domainp (y := numr p) then 0 else
    if univariatep y or
       <<on complex; univariatep (y := numr resimp p)>> or
       <<on rounded; univariatep (y := numr resimp p)>> then y)
    where p=simp!* y,!*msg=nil;

symbolic procedure ckacc(q,p,r);   % p,q,r,!*xo,!*xn all bfloat
   if not(r and caar lastpair p>1 and (rr!#>1 or pfactor!#)) then r
   else if caar lastpair q=1 then
      <<acc!# := cdr(r := accupr1(r,q)); car r>> else
   begin scalar ac,rl,s,nx; rl := bfzp gfim r;
      r := <<if not pfl!# then accuroot(r,q,!*xobf); !*xn>>;
loop: ac := accupr(q,p,r);
      if pfl!# then
         <<if (s := acc!# - (ac := ac + ss!#))>0 then
              <<acfl!# := t; accm!# := accm!#-s>>;
           acc!# := ac;
           r := if rl then rootrnd gfrl r else gfrtrnd r;
           trmsg12 r;
           return r>>
      else if ac>acc!# then <<acc!# := ac; go to gfr>>;
      if s or ss!#=0 then return r;
      s := t; acc!# := acc!# + ss!#:
 gfr: nx := r; r := <<accuroot(r,q,!*xobf); !*xn>>;
      if gfeqp(nx,r)
         or s and not(rl and not(rl := bfzp gfim r))
         then return r else go to loop end;

symbolic procedure gfadjust x; if !*pcmp or not !*bftag
   or not lessp!:(abs!: gfrl x,sprec!#) then x else im2gf gfim x;

symbolic procedure xnshift x;
   if gfzerop !*xo then x else gfadjust gfdiffer(x,!*xo);

symbolic procedure unshift x;
   if gfzerop !*xo then x else gfadjust gfplus(x,!*xo);

symbolic procedure gfexit(pf,nx,x0,m);
 if bfzp pf then <<trmsg1 (m,nx); t>>
   else if gfeqp(unshift nx,unshift x0) then <<trmsg3 (m,nx); nx>> else
   begin scalar rl,r1,r0,im,i1,i0;
      r1 := bfloat(rl := gfrl(nx := unshift nx));
      i1 := bfloat(im := gfim nx);
      r0 := bfloat gfrl(x0 := unshift x0); i0 := bfloat gfim x0;
     return
       if eqprts(r1,r0) then
          if mt!: i1*mt!: i0<0 then rl2gf rl
          else if cvt2(i1,i0) then zptst rl2gf rl else nil
       else if eqprts(i1,i0) then
          if mt!: r1*mt!: r0<0 then im2gf im
          else if cvt2(r1,r0) then zptst im2gf im else nil
       else <<!*zp := 0; nil>>  end;

symbolic procedure zptst x;
   if !*zp>4 then x else <<!*zp := !*zp+1; nil>>;

symbolic procedure eqprts(a,b); bfnzp a and (equal!:(a,b) or cvt5(a,b));

symbolic procedure powerchk p;
  % reduce degree of polynomial if powergcd > 1.
   <<if !*powergcd and length p>2 then
        for each x in cdr p do g := gcdn(g,car x);
     if g>1 then g . for each x in p collect (car x/g) . cdr x>>
   where g=0; % returns (powergcd . <reduced polynomial>) or nil.

symbolic procedure rtreorder cc; if cc then
   if dnp caar cc or not bfnump caar cc and dnp caaar cc then
    (<<for each j in cc do p := max(p,rrsiz car j);
     prd!% := 2*p; sort(cc,function dnafterp)>> where p=0)
   else sort(cc,function bfnafterp);

symbolic procedure bfnafterp(a,b);
  (if bfnump ca then
     if bfnump cb then rd!:minusp rd!:difference(cb,ca)
     else ((if rd!:zerop d then rd!:minusp cdr cb else rd!:minusp d)
           where d=rd!:difference(car cb,ca))
   else if bfnump cb then
     ((if rd!:zerop d then not rd!:minusp cdr ca else rd!:minusp d)
      where d=rd!:difference(cb,car ca))
   else
   ((if rd!:zerop d then rd!:minusp rd!:difference(cdr cb,cdr ca)
     else rd!:minusp d) where d=rd!:difference(car cb,car ca)))
  where ca=car a,cb=car b;

symbolic procedure dnafterp(a,b);
  (if dnp ca then
     if dnp cb then dnafterp1(ca,cb)
     else if dnequal(ca,car cb) then mt!: cdr cb<0
       else dnafterp1(ca,car cb)
   else if dnp cb then
     if dnequal(car ca,cb) then mt!: cdr ca>0 else dnafterp1(car ca,cb)
   else
   ((if dnequal(cca,ccb) then dnafterp1(cdr ca,cdr cb)
     else dnafterp1(cca,ccb))
    where cca=car ca,ccb=car cb))
  where ca=car a,cb=car b;

symbolic procedure dnequal(a,b);
   mt!: a=0 and mt!: b=0 or ep!: a=ep!: b and mt!: a=mt!: b;

symbolic procedure dnafterp1(a,b);
   if ep!: a=ep!: b then mt!: a>mt!: b else
     ((if d=0 then ma>mb else
         if d>prd!% then ma>0
         else if d<-prd!% then mb<0
         else if d>0 then ma*10**d>mb
         else ma>mb*10**-d)
       where d=ep!: a - ep!: b, ma=mt!: a, mb=mt!: b);

endmodule;

end;
