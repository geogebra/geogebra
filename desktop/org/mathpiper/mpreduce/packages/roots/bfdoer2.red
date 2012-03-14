module bfdoer2; % routines for doing bfloat arithmetic, mixed float
                % and bf arithmetic, gf and gbf arithmetic, rational
                % arithmetic and fast polynomial manipulations and form
                % conversion, part 2.

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

exports allout, automod, bfixup, bfmax, cexpand, ckprec, ckpzro, csize,
        ftout, gf2flt, gffinit, gfrtrnd, gfsimp, gfsqfrf, mkdn,
        n2gf, nbfout, nwterr, nwterrfx, outecho, outmode, pbfprint,
        pconstr, pflupd, restorefl, rootprec, rootrnd, seteps, smpart,
        stuffr, trmsg10a, trmsg11a, trmsg12a, trmsg13a, trmsg1a,
        trmsg2a, trmsg3a, trmsg4a, trmsg6a, trmsg7a, trmsg8a, trmsg9,
        trmsg17, xnsiz;

imports !*crn2cr, !*f2q, abs!:, bfloat, bfloatem, bfmin, bfminus,
        bfnump, bfnzp, bfp!:, bfplus, bfprin0, bfrndem, bfsiz, bftimes,
        bfzp, calcprec, ceiling, ceillog, cflot, conv2gi2, conv2gid,
        cpxp, crp, divbf, dmconv0, dmconv1, domainp, ep!:, eqcar,
        errorp, errorset!*, find!!nfpd, floor, getprec, gf2bf, gf2fl,
        gfconj, gffinitr, gfim, gfminus, gfrl, gzero, i2bf!:, lastpair,
        lessp!:, lispeval, listecho, log10, lprim, make!:ibf, make!:rd,
        maxbnd1, minprec, mk!*sq, mkgirat, mkinteg, mkquote, mt!:,
        mvar, neq, normbf, numr, off, on, p1rmult, pmsg, preci!:,
        precision, precmsg, r2bf, rdp, rerror, reversip, rl2gf,
        round!:mt, rtreorder, setflbf, setprec, simp!*, sizatom, sqfrf,
        sqrt, tagim, tagrl, times!:, ungffc, ungfform, univar, unshift;

fluid '(!*trroot !*rootmsg !*multiroot !*roundbf !:bprec!: !*complex
  !*msg !*bftag !*sqfree !*ratroot !*nosqfr rootacc!# iniprec!#);
switch trroot,rootmsg,multiroot,ratroot;
global '(!!nfpd !!nbfpd !!flim bfz!* log2of10 rlval!# cpval!#);
global '(!!shbinfl rootsreal rootscomplex den!* lm!# !!flprec);
flag('(rootsreal rootscomplex),'share);
fluid '(!*pfsav pr!# acc!# bfl!# emsg!# eps!# rootacc!#!#);
fluid '(!*xmax !*xmax2 !*rvar nwmax!# lgmax!# nozro!# !1rp);
fluid '(!*xo !*exp !*pcmp !*multrt lgmax!# nwmax!# rnd!# rndl!#);
fluid '(!*keepimp sqf!# exp!# sprec!# rprec!# !*bfsh incmsg!$
  cpx!# pfactor!# rr!# pnn!# nn!# prx!# rlrt!# !*noeqns);

global '(!*rootorder); symbolic (!*rootorder := t);

symbolic procedure gf2flt a;
  % force into float format, except if !shbinfl or float error.
   if !*roundbf or !!shbinfl then a else
      <<(if errorp gx then a else car gx)
         where gx=errorset!*(list('gf2fl,mkquote a),nil)>>;

symbolic procedure gfbfxn nx;
 gf2bf if !*bfsh then
      if bfnump nx then bfplus(nx,gfrl !*xo) else gfrl unshift nx
   else if bfnump nx then nx else unshift nx;

symbolic procedure print_the_number xn;
  if atom xn then write xn
   else if numberp car xn then <<write car xn;
                              if cdr xn >=0 then write "+";
                              write cdr xn;
                              write "*I" >>
   else if rdp xn then bfprin0a xn
    else <<bfprin0a car xn;
           if mt!: cdr xn >= 0 then write "+";
           bfprin0a cdr xn;
           write "*I" >>;

symbolic procedure bfprin0a u;
  bfprin0 u where !*nat = nil;

symbolic procedure trmsg1a (a,nx);
   if !*trroot then <<
     write a,",px=0 => ";
     print_the_number gfbfxn nx;
     terpri()
   >>;

symbolic procedure trmsg2a (a,xn,px);
   if !*trroot then <<
     write a," -> xn=";
     print_the_number gfbfxn xn;
     trmsg5(xn,px)
   >>;

symbolic procedure trmsg3a (a,xn);
   if !*trroot then <<
     write a,",xn=x0 => ";
     print_the_number gfbfxn xn;
     terpri()
   >>;

symbolic procedure trmsg4a req; if !*trroot then
   <<write "number of "; if nozro!# then write "nonzero ";
     write "real roots = ",req; terpri()>>;

symbolic procedure trmsg5(nx,px);
   <<terpri(); write "      "," px="; print_the_number px; terpri()>>;

symbolic procedure trmsg6a(k,xn,px);
   if !*trroot then
      <<write "mean of ",k," xn->"; print_the_number gfbfxn xn;
        trmsg5(xn,px)>>;

symbolic procedure trmsg7a xn;
   if !*trroot
    then <<write "best value ="; print_the_number gfbfxn xn; terpri()>>;

symbolic procedure trmsg8a;
   if !*trroot then
      <<if !*bftag then write "Precision is ",getprec()
           else write "!nfpd = ",!!nfpd; terpri()>>;

symbolic procedure trmsg9 a;
   lprim{"Roots precision increase to ",a};

symbolic procedure trmsg10a a;
   <<lprim{"restart at higher precision called by function ",a};
     terpri()>>;

symbolic procedure trmsg11a (xn,n);
   if !*trroot
     then <<write "n=",n," -> "; print_the_number gfbfxn xn; terpri()>>;

symbolic procedure trmsg12a z;
   if !*trroot then <<write "acc(",acc!#,") ->";
                      print_the_number outtrim z; terpri()>>;

symbolic procedure trmsg13a(n,xn,px);
   if !*trroot
     then <<write "n=",n,",xn="; print_the_number gfbfxn xn;
            trmsg5(xn,px)>>;

symbolic procedure trmsg17 y;
   lprim list("Roots ",y," equal to acc ",acc!#);

symbolic procedure nwterr m;
 if m>nwmax!#+lm!# then
    <<restorefl();
      emsg!# := list("max NWT limit",nwmax!#+lm!#,"exceeded");
      error(0,emsg!#)>>;

symbolic procedure nwterrfx(n,cp);
  if n<3 then 0 else
   fix((n-2)*sqrt max(0,0-15+minprec())*if cp then 4 else 1);

symbolic procedure seteps;
 eps!# := make!:ibf(1,-(if !*bftag then !:bprec!: else !!nbfpd));

symbolic procedure pconstr(m,r);
 % set !*bftag and return equivalent of x^m-r in bfloat form.
  bfloatem prxflot(if prx!# then max(prx!#,ac) else ac,
   {0 . if rl then bfminus if bfnump r then r else car r else gfminus r,
         m . if rl then 1.0 else rl2gf 1.0})
   where rl=(bfnump r or bfzp cdr r),ac=acc!#+2+ceillog m;

symbolic procedure prxflot(pr,p);
   <<setprec(if setflbf(rlrt!# or pr>!!nfpd) then pr else !!nfpd);
     bfrndem bfloatem p>>;

symbolic procedure smpart y;
   (if mt!: a>0 and mt!: b>0 then
      (if lessp!:(b,times!:(a,c)) then t
       else if lessp!:(a,times!:(b,c)) then 0))
    where a=abs!: round!:mt(gfrl y,20),b=abs!: round!:mt(gfim y,20),
       c := make!:ibf (1, -!:bprec!:);

symbolic procedure stuffr(n,v,ar);
 <<pmsg v; while n>0 do <<pt := cdr pt; n := n-1>>; rplaca(pt,v); ar>>
   where pt=ar;

symbolic procedure n2gf p; if atom p then p else
   begin scalar f,n; n := car p;
       for each y in cdr p do
          <<if y and y neq 0 then f := (n . y) . f; n := n-1>>;
       return f end;

symbolic procedure pbfprint x;
   begin scalar n,d,c;
     if not (atom(d := cdar x) or bfp!: d) then
        <<c := 'complex; d := car d>>;
     if floatp d then n := 'float else if fixp d then n := 'fix;
     x := for each p in x collect (car p) . gf2bf cdr p;
     if n then x := n . x; if c then x := c . x; return x end;

symbolic procedure pflupd pf;
   <<!*pfsav := append(lastpair !*pfsav,list pf);
     bfmin(pf,car !*pfsav)>>;

symbolic procedure rootacc n;
  if null n or numberp n and (n := abs fix n)>=6 then
     rootacc!#!# := n else
    rerror(roots,1,{"nil or numeric input >= 6 required."});

symbolic procedure rootprec n;
  if null n or numberp n and (n := abs fix n)>=!!flprec+4 then
     rprec!# := n else
    rerror(roots,2,{"nil or numeric input >=",!!flprec+4,"required."});

symbolic procedure csize p;
   begin integer n;
     for each c in gffinitr p do n := max(n,xnsiz cdr c); return n end;

symbolic operator rootacc, rootprec, csize;

symbolic procedure ckpzro p;
 % solve for zero roots and reduce p.
   begin scalar c,n; p := ckprec p;
         if numberp (p := gffinit p) then p := nil
            else if atom p then <<c := '(((0) . 1)); p := nil>>
            else if atom car p then p := nil
            else if (n:= caar p)>0 then go to zrt;
         go to ret;
    zrt: c := list(list 0 . n); % eliminate and solve for zero roots.
       % if there are no other roots, we're done.
         if not p or null cdr p then <<p := nil; go to ret>>;
       % otherwise, reduce polynomial degree.
         p := for each j in p collect (car j-n) . cdr j;
    ret: nozro!# := t; return c . p end;

symbolic procedure ckprec p;
   <<find!!flim();
     !*rvar := nil;
     rootsreal := rootscomplex := nil; cpx!# := !*complex;
     bfl!# := !*bftag;
     acc!# := rootacc!# := rootacc!#!# or 6;
    % the following line is corrected so :prec: restores properly.
     pr!# := getprec();
    % prevent floating of polynomial with decimal or fractional
    % coefficients when rounded is on initially.
     <<if (rnd!# := !*rounded) then off rounded;
       if (rndl!# := !*roundall) then off roundall>> where !*msg=nil;
     sqf!# := !*sqfree; exp!# := !*exp; !*sqfree := !*exp := t;
    % simp!* is called on p in univar only.  Result is not dependent on
    % system precision unless p must be evaluated with rounded on.
     if null(p := univar p) then
       <<restorefl();
         rerror(roots,3,"Univariate polynomial required")>>;
     if !*rounded then msgpri(nil,
      "Polynomial simplified in ROUNDED mode at precision",precision 0,
      ":       root locations depend on system precision.",nil)
       where !*msg=t;
    % next line corrected so internal precision is correct at start.
     setprec max(rprec!# or (!!nfpd+2),acc!#+2); p>>;

symbolic procedure restorefl;
   <<setprec pr!#;
     !*sqfree := sqf!#; !*exp := exp!#;
    % restore input dmode.
     <<if !*complex and not cpx!# then off complex;
       if rnd!# then on rounded else if !*rounded then off rounded;
       if rndl!# then on roundall>>
      where !*msg=nil;
     nozro!# := pr!# := sqf!# := exp!# := cpx!# := rnd!# :=
       rndl!# :=  nil>>;

symbolic procedure mkequal l;
  'list . (for each y in l collect {'equal,!*rvar or 'x,outmode y});

symbolic procedure outmode j;
   if null j then j
   else if bfnump j and bfzp j then 0 else if fixp j then j
   else mk!*sq if !*ratroot then mkgirat j else !*f2q
      if floatp j then make!:rd j
       else if eqcar(j,'!:dn!:) then decimal2internal(cadr j,cddr j)
       else if domainp j then j
       else if eqcar(car j,'!:dn!:) then
         '!:cr!: . (cdr decimal2internal(cadar j,cddar j)) .
                   cdr decimal2internal(caddr j,cdddr j)
       else '!:cr!: . if bfp!: car j then (cdar j) . cddr j else j;

symbolic procedure allout c;
   begin scalar rl,cmp; integer a;
      c := for each j in c collect car j;
      if c and not !*ratroot and
       ((pairp r and (not bfnump r and car r eq '!:dn!:
           or not bfnump car r and caar r eq '!:dn!:)) where r=car c)
        then for each j in c do a := max(a,rrsiz j);
      restorefl(); % precision has been restored to initial value.
      for each x in c do
         if atom x or eqcar(x,'!:dn!:) then rl := x . rl
            else cmp := x . cmp;
      !*msg := t;
   % Increase system precision if too low to print out all roots.
      precmsg a;
   % If system precision is already high, warn about inputting values.
      if a<precision 0 and a>max(rootacc!#!# or 6,!!flprec)
       then msgpri(nil,
        "input of these values may require precision >= ",a,nil,nil);
       !*msg := nil;
      % the following change improves roots, solve interface.
      c := if !*noeqns then
         <<rootscomplex := rootsreal := nil;
           'list . for each j in c collect outmode j>>
         else
           <<rootscomplex := mkequal reversip cmp;
             rootsreal := mkequal reversip rl; mkequal c>>;
      return c end;

symbolic procedure rrsiz u;
  % determine precision needed for printing results.
   if numberp u then length explode abs u
   else if u eq 'i then 0
   else if atom u then rrsiz sizatom u
   else if eqcar(u,'minus) then rrsiz cadr u else
     ((if not atom y then
         if eqcar(y,'!:dn!:) then max(rrsiz car u,rrsiz cdr u)
         else rerror(roots,7,"unknown structure")
       else if y memq '(plus difference) then
         begin integer r;
           for each n in cdr u do r := max(r,rrsiz n);
           return r end
       else if y memq '(times quotient) then
         for each n in cdr u sum rrsiz n
       else if y eq '!:dn!: then length explode abs car normdec cdr u
       else rerror(roots,7,"unknown structure")) where y=car u);

symbolic procedure outecho r;
   allout for each c in r join
      listecho(car c,if !*multiroot then cdr c else 1);

symbolic procedure find!!flim;
   <<!!flim := 0; repeat <<n := n/10; !!flim := !!flim+1>>
       until explode(1.0+n)=explode 1.0; !!flim>> where n=1.0;

symbolic procedure xnsiz x;
  ceiling (xnsiz1 x / log2of10);

symbolic procedure xnsiz1 x;
   if bfnump x then bfsiz x
   else if bfzp gfim x then bfsiz gfrl x
   else if bfzp gfrl x then bfsiz gfim x else
   <<x := gf2bf x;
     ((max(preci!: gfrl x+e1,preci!: gfim x+e2)-min(e1,e2))
      where e1=ep!: gfrl x,e2=ep!: gfim x)>>;

symbolic procedure outtrim j;
   if !*roundbf or acc!#>!!flim then gf2bf j else
     ((if errorp d then gf2bf j else car d)
       where d=errorset!*({'gf2fl,mkquote j},nil));

symbolic procedure bfmax p;
  <<!*xmax := maxbnd1 p;
    ((!*xmax2 := bftimes(m,m))
     where m=if !*bftag then bfloat !*xmax else cflot !*xmax); !*xmax>>;

symbolic procedure nbfout x; bfloat ftout x;

symbolic procedure bfixup x; if !*bftag then gf2bf x else gf2fl x;

symbolic procedure ftout x;
   if atom x then cflot x
   else if rdp x then cdr x else x;

find!!flim();

symbolic procedure cexpand cc;
   begin scalar c;
      if !*rootorder then cc := rtreorder cc;
      for each r in cc do
         <<if (not !*pcmp) then
             if not bfnump car r and bfnump caar r
               then c := ((gfconj car r) . cdr r) . c
             else if not eqcar(car r,'!:dn!:) and eqcar(caar r,'!:dn!:)
               then c := ((cdnconj car r) . cdr r) . c;
           c := r . c>>;
      return c end;

symbolic procedure cdnconj u;
  (car u) . (cadr u . ((minus caddr u) . cdddr u));

symbolic procedure mkdn u;
 if atom car u then '!:dn!: . normdec u else (mkdn car u) . mkdn cdr u;

symbolic procedure normdec x;
   begin scalar mt,s;integer ep;
      if (mt := car x)=0 then go to ret;
      if mt<0 then <<mt := -mt; s := t>>;
      ep := cdr x;
      mt := reversip explode mt;
      while car mt eq '!0 do <<mt := cdr mt; ep := ep+1>>;
      mt := compress reversip mt;
      if s then mt := -mt;
 ret: return mt . ep end;

symbolic procedure rootrnd y; rtrnda(y,acc!#);

symbolic procedure rtrnda(r,a);
  if bfzp r then <<rlval!# := 0 . 0; r>> else
  ((decimal2internal(car (rlval!# := u),cdr u))
   where u=round!:dec1(r,a));

symbolic procedure gfrtrnd y;
   (begin scalar rl,rld,im; y := cdr y;
      rl := rtrnda(a,acc!#); rld := rlval!#;
      im := rtrnda(y,acc!#);
      cpval!# := if car rlval!# = 0 then rld else rld . rlval!#;
      return rl . im end)
    where a=car y;

symbolic procedure gfsqfrf p;
   begin scalar m,cp,q,dmd;
      if caar lastpair p=1 or !*nosqfr then go to nof;
      cp := cpxp(q := mkinteg p);
      dmd := dmode!*; if !*complex then dmd := get(dmd,'realtype);
      m := !*msg; off msg;
      if dmd then lispeval {'off,mkquote list(dmd := get(dmd,'dname))};
      q := sqfrf if cp then ungffc q else ungfform q;
      if dmd then lispeval {'on,mkquote list dmd}; if m then on msg;
      if cdr q then pfactor!# := t else if cdar q=1 then go to nof;
      !1rp := p1rmult q; return q;
 nof: q := list(p . 1); !1rp := p; return q end;

symbolic procedure automod p; % p is always returned in bfloat form.
   if bfnump (p := gffinit p) then p else
   begin integer n,s,s2; scalar a,d,m,nl,pr,nc,dd; rr!# := 0;
      if null cdr p then <<n := getprec(); go to sel>>;
    % determine precision of calculation and set mode.
    % first find minimum precision for normalizing p.
      m := car(d := car lastpair(p := bfloatem p)); d := cdr d;
      for each c in cdr reverse p do n := max(n,xnsiz cdr c);
      pr := getprec();
      setprec(if (nc := bfnump d) and abs mt!: d=1
         or not nc and
            ((a := mt!: car d)=0 and abs(dd := mt!: cdr d)=1 or
              dd=0 and abs a=1) then n else 2+max(n,xnsiz d));
      n := 0;
    % now calculate necessary precision for gfrootfind.
  nl := for each c in cdr reverse p collect xnsiz cdr c;
      for each c in nl do
         <<rr!# := rr!#+1; s := s+c; s2 := s2+c*c; n := max(n,c)>>;
      n := calcprec(m,nn!# := n,rr!#,float s/rr!#,
         if n>1 then float s2/(2*n*(n-1)) else 0);
      if rprec!# then n := max(n,rprec!#);
      pnn!# := n;
      if n>!!nfpd or !*roundbf then go to bfl;
      setflbf nil; setprec pr;
 cfl: if errorp errorset!*({'cflotem,mkquote p},nil)
         then go to bfl else return p;
 sel: if not !*bftag then go to cfl;
 bfl: setflbf t; setprec n; return p end;

symbolic procedure gffinit p;
   if not domainp p and numberp caar p then p
   else if numberp p
      or not atom p and member(car p,domainlist!*) then 0
   else
     begin scalar !*msg,cp;
       cp := !*complex; on complex;
       p := gfform p;
       if not cp then off complex; return reformup p end;

symbolic procedure clrdenom p; % convert p to integer polynomial.
   <<dmconv0 if !*complex then '!:crn!: else '!:rn!:;
     den!* := conv2gid(p := dmconv1 p,1); conv2gi2 p>>;

symbolic procedure gfform p;
   if domainp p then 0 else if atom caar p then p else
   begin scalar q; !*rvar := mvar p; p := clrdenom p;
loop: if cdar p then q := ((cdaar p) . gfsimp cdar p). q;
      if null (p := cdr p) then return q
      else if domainp p then <<q := (0 . gfsimp p) . q; return q>>
      else go to loop end;

symbolic procedure gfsimp u;
 % strip domain tags and strip zero im part but restore :bf: if needed.
   if bfnump u or rdp u then u
    else if eqcar(u,'!:rn!:) then r2bf cdr u
    else
      <<if eqcar(u,'!:crn!:) then u := !*crn2cr u;
        u := if crp u
             then (tagrl u) . tagim u
          else if eqcar(u,'!:gi!:)
             then (normbf i2bf!: cadr u) . normbf i2bf!: cddr u
          else cdr u;
        if bfzp cdr u then car u else u>>;

symbolic procedure reformup q; if domainp q then q else
  % returned q will be bfloat.
   begin scalar c,fg,d; integer n;
      for each v in q do  % check for complex, float, bfloat.
         <<v := cdr v; n := max(n,xnsiz v);
           if floatp v or bfp!: v then c := gzero v
           else if not atom v then
              <<fg := t;
                if not fixp (v := car v) then c := gzero v >> >>;
       % make coefficients homogeneous in type and assure
       % adequate precision;
       % convert coefficients to all real or all complex.
      if fg then
         <<q := for each v in q collect (car v) .
            <<d := cdr v; if bfnump d then d . 0 else d>>;
           d := q;
           repeat if bfnzp cadar d then fg := nil
                 until not fg or null(d := cdr d);
           if fg then
              q := for each v in q collect (car v) . cddr v>>;
      if bfp!: c or n>!!nfpd then <<q := bfloatem q; setflbf t>>
         else if floatp c then <<q := bfloatem q; setflbf nil>>;
      if n+2>getprec() then setprec(n+2);
      return q end;

endmodule;


end;
