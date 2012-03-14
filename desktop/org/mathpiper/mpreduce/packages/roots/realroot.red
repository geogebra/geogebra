module realroot; % Routines for finding real roots of polynomials,
                 % using Sturm series, together with iteration.

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


Comment   modules bfauxil, bfdoer, bfdoer2, complxp, allroot and rootaux
 needed also;

exports accupr1, bfnewton, isolatep, schinf, schplus, sgn1, sturm,
        sturm0, uniroots;

imports !!mfefix, abs!:, accupr, accuroot, allroots, automod, bdstest,
        bfabs, bfdivide, bfeqp, bfleqp, bfloat, bfloatem, bfmax,
        bfminus, bfminusp, bfplus, bfrlmult, bfsgn, bfsqrt, bfzp,
        ceillog, ckpzro, cpxp, csep, difbf, divbf,
        domainp, dsply, eqcar, equal!:, errach, geq, getprec, gfdiff,
        gffinitr, gfgetmin, gfrl, gfrootfind, gfsqfrf, gfstorval,
        greaterp!:, lastpair, leq, lprim, minbnd1, minprec, mk!*sq,
        multroot,
        neq, nwterr, nwterrfx, outecho, pconstr, plubf, powerchk,
        r2bf, r2flbf, ratdif, ratleqp, ratlessp, ratmax, ratmean,
        ratmin, ratminus, ratplus, realrat, rerror, rl2gf, rlval,
        round!:mt, sch, schnok, setprec, sgn, stuffr, sturm1, timbf,
        trmsg1, trmsg10, trmsg2, trmsg3, trmsg4, trmsg6, trmsg7,
        trmsg8, xclp;


global '(!!nfpd !!flim bfhalf!* max!-acc!-incr bfone!* rlval!#);

fluid '(!*gfp !*xnlist !*intp tht!# !*strm lims!# mltr!# pfactor!#
 prec!# !*rvar acc!# !*xo !1rp accm!# !*xn intv!# sh!# rprec!# rlrt!#
 prm!# pfl!# acfl!# pgcd!#);

fluid '(!*trroot !*bftag !*compxroots !*msg);

flag('(positive negative infinity),'reserved);
global '(limlst!# lm!#);
limlst!# := '(positive negative infinity (minus infinity));

symbolic procedure isolatep p;
   begin scalar n,q,zr,a,b,c,ril,va,vb,vc,v0,w,elem,l,u,i,j,lr,ur,
         xcli,xclj,ol,ou;
      if null sturm p or schinf(-1)-schinf 1=0 then go to ret;
    % limits +/-1.0001*maxbound p to give working room for rootfind.
      n := car(q := car !*strm);
      l := ratminus(u := realrat bfrlmult(1.0001,bfmax p));
      if (zr := l=u) and (lr := l) and not lims!# then go to zrt;
      if lims!# then
         <<i := car lims!#; if cdr lims!# then
            <<j := cadr lims!#;  % both limits given.
              if i eq 'minfty then xcli := t else
                 <<if xclp i then <<xcli := t; i := cdr i>>;
                   l := ratmax(l,i)>>;
              if j eq 'infty then xclj := t else
                 <<if xclp j then <<xclj := t; j := cdr j>>;
                   u := ratmin(u,j)>>;
              if zr then if ratlessp(u,l) then go to ret
                 else go to zrt;
              if sgn1(q,l)=0 then       % root at l.
                 <<ol := offsetr(n,l);
                   if xcli then l := ratplus(l,ol) else lr := l>>;
              if l neq u and sgn1(q,u)=0 then  % root at u.
                 <<ou := offsetr(n,u);
                   if xclj then u := ratdif(u,ou) else ur := u>> >>
           else if zr then go to ret else
              <<if sgn1(q,ol := realrat 0)=0 then ol := offsetr(n,ol);
                if i<0 then u := ratminus ol  % negative roots.
                   else l := ol>> >>;  % positive roots.;
      n := (va := sch l+if lr then 1 else 0)-(vb := sch u);
      trmsg4(n);
      if n=0 then go to ret;
      if n=1 then ril := list list(l,u)
         else for j:=1:n do ril := nil . ril;
      v0 := vb+n-1;
      if lr then
         <<stuffrt(l,u,lr,ol,v0,va,vb,nil,ril);
           l := ratplus(lr,ol); va := va-1>>;
      if ur then
         <<stuffrt(l,u,ur,ou,v0,va,vb,nil,ril);
           u := ratdif(ur,ou); vb := vb+1>>;
      w := list list(l,u,va,vb);
      if n>1 then while w do
        <<elem := car w; w := cdr w; a := car elem; b := cadr elem;
          va := caddr elem; vb := cadddr elem; c := ratmean(a,b);
          if sgn1(q,c)=0 then % c is a root.
             w := stuffrt(a,b,c,offsetr(n,c),v0,va,vb,w,ril) else
          % root not found; stuff isolating interval and update work.
             <<vc := sch c;
               if va = vc+1 then <<stuffr(v0-vc,list(a,c),ril)>>;
               if va > vc+1 then w := list(a,c,va,vc) . w;
               if vb = vc-1 then <<stuffr(v0-vb,list(c,b),ril)>>;
               if vb < vc-1 then w := list(c,b,vc,vb) . w>> >>;
      ril := for each i in ril collect (car i) . cadr i;
 ret: return ril;
 zrt: return list (lr . lr) end;

symbolic procedure stuffrt(a,b,c,m,v0,va,vb,w,ril);
   begin scalar vcm,vcp;  % stuff root and update work.
      vcm := 1+(vcp := sch ratplus(c,m));
      stuffr(v0-vcp,list(c,c),ril);
      if va = vcm+1 then stuffr(v0-vcm,list(a,ratdif(c,m)),ril);
      if va > vcm+1 then w := list(a,ratdif(c,m),va,vcm) . w;
      if vb = vcp-1 then stuffr(v0-vb,list(ratplus(c,m),b),ril);
      if vb < vcp-1 then w := list(ratplus(c,m),b,vcp,vb) . w;
      return w end;

symbolic procedure offsetr(n,r);
   realrat if n=1 then 1 else minbnd1(!*gfp,mk!*sq r);

symbolic procedure sturm p;
   <<if cpxp (p := gffinitr p) then
        <<p := car csep p; if not atom p then p := bfloatem p>>;
     if not atom p then sturm1(!*gfp := p)>>;

put('sturm,'psopfn,'sturm0);

symbolic procedure sturm0 p;
   <<p := sturm ckprec car p; restorefl();
     'list . for each a in p collect if atom a then a else 'list . a>>;

symbolic procedure sgn1(p,r); if atom p then sgn p else
 % Evaluate sign of one sturm polynomial for rational r=(u . d)
   begin scalar m,c,u,d; u := car r; d := cdr r;
      c := 0; m := 1; p := cdr p;
      repeat <<c := m*car p + u*c; m := m*d>> until null(p := cdr p);
      return sgn c end;

symbolic procedure r2flimbf x;
   if acc!#<=!!flim then r2flbf x else r2bf x;

symbolic procedure rootfind(p,i);
  % finds real roots in either float or bigfloat modes.
  % p is in gfform. i is a pointer to a rational interval pair;
   begin scalar p1,p2,px,x1,x2,x3,x0,nx,xr,fg,n,s,sh;
      scalar xd,xe,qt,xnlist,pf,pf0; integer m,tht!#;
      n := caar lastpair p; !*xnlist := nil;
      if car i=cdr i then
         <<nx := r2flbf cdr i; go to lg4>>;
      xr := ratmean(car i,cdr i);
      if !*trroot then
         <<write "real root ",list(r2flimbf car i,r2flimbf cdr i);
           terpri()>>; trmsg8();
      if ratlessp(cdr i,car i) then
         errach "lx > hx ***error in roots package";
      movebds(i,xr,sh!# := sh := sgn1(!*intp,cdr i));
      p2 := gfdiff(p1 := gfdiff p);
lag0: if bndtst (px := rlval(p,nx := r2flbf xr)) then go to tht
         else if bfzp px then go to lg4;
 lag: % check for proper slope at nx.
      if bndtst (x1 := rlval(p1,nx))
         or (s := bfsgn x1) neq sh then go to tht;
     % if lag not converging, go to newt.
      pf := bfabs px; if pf0 and bfleqp(pf0,pf) then go to newt;
      gfstorval(pf,nx); x1 := bfabs x1;
      if bndtst (x3 := rlval(p2,nx)) then go to tht;
   % bigfloat computations: is newton cheaper?
      if fg and
         <<qt := divbf(px,x1);
           xe := timbf(qt,timbf(qt,(divbf(x3,x1))));
           equal!:(nx,plubf(nx,timbf(bfhalf!*,xe)))>>
              then go to newt;
    % check whether laguerre iteration will work.
      x2 := difbf(bfrlmult(n-1.0,timbf(x1,x1)),
           bfrlmult(n,timbf(px,x3)));
      if bfminusp x2 then go to tht;
   % nx has met all tests, so continue.
      x0 := nx;
      xd := divbf(bfrlmult(-n*s,px),
         plubf(x1,bfsqrt(bfrlmult(n-1,x2))));
      nx := plubf(x0,xd);
 lg3: fg := t;
      if ratlessp(xr := realrat nx,car i) or ratlessp(cdr i,xr)
         then go to tht;
      if bndtst (px := rlval(p,nx)) then go to tht;
      movebds(i,xr,sh); trmsg2 ('lag,nx,px);
      if bdstest i then go to ret;
      if bfzp px then go to lg4;
      if bfeqp(nx,x0) then <<trmsg3('lag,nx); go to ret>>;
      if xnlist and member(nx,xnlist) then go to newt;
      xnlist := nx . xnlist; pf0 := pf;
      if(m := m+1)<10 or
        <<m := 0;
          equal!:(bfone!*,round!:mt(divbf(bfloat nx,bfloat x0),13))>>
            then go to lag;
 tht: nx := tighten(i,p,pf,sh); m := 0;
      if !*xnlist then
         <<pf0 := nil;
           movebds(i,xr := ratmean(car i,cdr i),sh); go to lag0>>;
 lg4: trmsg1('lag,nx);
 ret: !*xnlist := nil; if not nx then trmsg10 'lag; go to ret2;
newt: nx := bfnewton(p,p1,gfgetmin(),i,4);
ret2: !*xn := rl2gf nx; return nx end;

global '(tentothetenth!*!*);

tentothetenth!*!* := normbf i2bf!: 10000000000;

symbolic procedure bndtst x;
  greaterp!: (abs!: x, tentothetenth!*!*);

symbolic procedure movebds(i,xr,sh);
   if sgn1(!*intp,xr)=sh then rplacd(i,xr) else rplaca(i,xr);

symbolic procedure tighten(i,p,pf,sh);
   begin scalar j,x0,nx,px,sn,x;
      nx := car i;
tht0: j := 4;
tht1: x0 := nx; nx := ratmean(car i,cdr i);
      if (sn := sgn1(!*intp,nx))=0 then
         <<x := r2flbf nx;trmsg1 ('tht,x); go to ret>>;
      if 0=car ratdif(nx,x0) then
         <<x := r2flbf nx;trmsg3 ('tht,x); go to ret>>;
      if sn=sh then rplacd(i,nx) else rplaca(i,nx);
      if (sn := bdstest i) then <<x := r2flbf sn; go to ret>>;
      if (j := j-1)>0 then go to tht1;
      if bndtst (px := rlval(p,x := r2flbf nx)) then
         <<j := 4; go to tht1>>;
      gfstorval(bfabs px,x);
      trmsg2('tht,x,px);
      if bfzp px then go to ret
         else if pf and bfleqp(pf,bfabs px) then go to tht0
         else return x;
 ret: !*xnlist := nil; return x end;

symbolic procedure rtsreal(p,s);
 % Finds real roots of univariate square-free real polynomial p, using
 % sturm series, isolater and rootfind.
   begin scalar acr,acs,n,q,r,x,y,!*strm,pr,apr,!*bftag,pfl!#,
        acfl!#,xout,x1;
      integer accn,accn1,accm!#,prec!#,prm!#; pr := getprec();
      !*bftag := rlrt!# := t; pgcd!# := not s;
      r := isolatep p; % r is a list of rational number pairs.
      if null r then go to ret;
      if (n := caar lastpair p)>1 then go to gr1;
      y := rootrnd gfrl gfrootfind(p,nil);
      if pfactor!# then
        <<y := accupr1(y,p); y := (rootrnd car y) . cdr y>>;
     % note that rlval!# was set by the last operation of rootrnd.
      xout := {if s then (mkdn rlval!#) . acc!#
                 else if pfactor!# then y
                   else y . acc!# % this can't happen
                };
      if !*trroot then terpri(); go to ret;
 gr1: !*xo := rl2gf 0;
      q := r; acs := acc!#;
 lag: % increase accuracy for this root and the next root if current
      % accuracy is not sufficient for the interroot interval.
      if cdr q then  % no test if this is the last real root.
        <<setprec acs;
          while schnok q do setprec (getprec()+1);
          accn1 := getprec()>>;
      acc!# := max(acs,accn,accn1);
      accn := if accn1>acs then accn1 else 0;
      setprec max(rprec!#,acc!#+2);
      y := rootfind(p,intv!# := car q); apr := t;
      if null y then rerror(roots,8,"Realroots abort");
 acc: y := accuroot(gfrl !*xn,p,!*xo);
     % if acc!# is insufficient for this root, for any reason,
     % increase accuracy and tighten.
      if apr then
        <<if (acr := accupr(p,!1rp,!*xn))>acc!# then acc!# := acr
            else if acr<=acc!# then <<acc!# := acr; apr := nil>>;
          go to acc>>;
      xout := ((x1 := if s then mkdn rlval!# else y) . acc!#) . xout;
     % x is root list. Check for equal roots should fail!
      if x and x1=car x then rooterr x1;
      x := x1 . x;
      dsply y;
      acc!# := acs;
      if (q := cdr q) then <<accn1 := 0; go to lag>>;
 ret: setprec pr; return reverse xout end;

symbolic procedure lval x; if xclp x then cdr x else x;

symbolic procedure lpwr(l,m);
   if eqcar(l,'list) then 'list . lpwr(cdr l,m)
   else if atom l then l else ((car l)**m) . ((cdr l)**m);

symbolic procedure schnok r;
  %true if precision is inadequate to separate two adjacent real roots.
   (l neq h and (sch l neq sch r2flbf2r l or sch h neq sch r2flbf2r h))
   where l=caar r,h=cdar r;

symbolic procedure limchk x;
   <<!!mfefix();
     if null (x := for each y in x collect
        if member(y,limlst!#) then y
        else if eqcar(y := reval y,'list)
         then 'list . list limchk1 cadr y
        else limchk1 y) then nil
     else if x and not cdr x then
        if car x eq 'positive then list 1
        else if car x eq 'negative then list (-1) else limerr()
     else <<x := mkratl x; limchk2(car x,cadr x)>>>>;

symbolic procedure limchk1 y;
 if errorp(y := errorset!*({'a2rat,mkquote y},nil))
    then rerror(roots,5,"Real root function limits must be real")
 else car y;

symbolic procedure limchk2(a,b);
 <<if member(a,l) and member(b,l) then if a neq b then nil else limerr()
   else if member(a,limlst!#) then
      if member(b,limlst!#) then limerr() else limchk2(b,a)
   else if member(b,limlst!#) then
      if b eq 'negative then list('minfty,mkxcl a)
      else if b eq 'positive then list(mkxcl a,'infty)
      else if b eq 'infinity then list(a,'infty) else list('minfty,a)
   else if ratv b=ratv a and (xclp a or xclp b) then t
   else if ratlessp(ratv b,ratv a) then list(b,a) else list(a,b)>>
   where l = cddr limlst!#;

symbolic procedure limerr;
   rerror(roots,6,"Illegal region specification");

symbolic procedure ratv a; if xclp a then cdr a else a;

symbolic procedure a2rat x;
   if numberp x then x . 1
   else if atom x then limerr()
   else if eqcar(x,'quotient) then
     ((if numberp n then n
       else if eqcar(n,'minus) then - cadr n
       else rerror(roots,10,"illegal limit")) where n=cadr x) . caddr x
   else if car x eq '!:rn!: then cdr x
   else
    ((if car x memq domainlist!* and y then cdr(apply1(y,x))
       else limerr())
    where y=get(car x,'!:rn!:));

symbolic procedure rlrootno a;
   <<mltr!# := t; lims!# := limchk cdr a; a := ckprec car a;
     a := rlrtno2 if lims!#=t then 0 else a;
     restorefl(); mltr!# := nil; a>>;

put('rlrootno,'psopfn,'rlrootno);

symbolic procedure realroots a;
   <<lims!# := limchk cdr a;
     uniroots(if lims!#=t then 0 else car a,0)>>;

put('realroots,'psopfn,'realroots);

symbolic procedure isolater p;
   <<mltr!# := t; lims!# := limchk cdr p; p := ckprec car p;
     p := isolatep if lims!#=t then 0 else p;
     restorefl(); mltr!# := nil; outril p>>;

put('isolater,'psopfn,'isolater);

symbolic procedure mkratl l; for each a in l collect
   if member(a,limlst!#) then a else
   if eqcar(a,'list) then
      if member(a := cadr a,limlst!#) then a else mkxcl a
      else a;

symbolic procedure exclude x; {'list, x};

symbolic operator exclude;

endmodule;

end;
