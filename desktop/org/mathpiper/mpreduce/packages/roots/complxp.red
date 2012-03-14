module complxp; % Support for complex polynomial solution.

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


Comment   support for modules allroot and realroot;

exports a2gf, a2rat, accupr, bdstest, bfprim, bfrndem, calcprec, csep,
        cvt2, cvt5, deflate1, deflate1c, dsply, getroot, gffinitr,
        gfgetmin, gfshift, gfstorval, invpoly, leadatom, limchk,
        mkgirat, mkinteg, mkpoly, orgshift, p1rmult, rlrtno2, rrpwr,
        rxgfc, rxgfrl, rxrl, schnok, sturm1, ungffc, xoshift;

imports !!mfefix, !*f2q, abs!:, ashift, automod, bfabs, bfdivide,
        bfixup, bflessp, bfloat, bfloatem, bfnump, bfnzp, bfsqrt,
        bfzp, ceiling, ceillog, cflot, cflotem, cpxp,
        decimal2internal, deflate2, denr, divbf, divide!:, domainp,
        ep!:, eqcar, equal!:, errorp, errorset!*, floor, ftout, gcdn,
        geq, gf2bf, gffinit, gfim, gfplus, gfplusn, gfquotient, gfrsq,
        gfrtrnd, gfsimp, gftimes, gftimesn, gfval, gfzerop,
        greaterp!:, gtag, hypot, intdiff, lastpair, lcm, lessp!:, log,
        make!:ibf, minbnd1, minus!:, mk!*sq, mkquote, mkratl, mkxcl,
        mt!:, n2gf, ncoeffs, ncpxp, neq, num, numr, plubf, plus!:,
        pmsg, preci!:, prepsq, primp, primpn, r2bf, r2flbf, ratlessp,
        realrat, rerror, reval, reversip, rl2gf, rndpwr,
        rootrnd, round!:mt, sch, schinf, sgn, sgn1, simp!*, sqrt,
        sturm, timbf, times!:, typerr, xclp;


fluid '(acc!# !*intp !*multrt !*strm sprec!# !*xo !*rvar rootacc!#!#);
fluid '(!*bftag !*rootmsg lims!# pfactor!# !*xnlist accm!# pflt!#);
global '(limlst!# bfone!*);

symbolic procedure rxgfrl(p,x);
   <<x := gfabs x;
     for each i in cdr p do
        <<c := gftimes(c,x);
          if i then c := gfplus(c,rl2gf bfabs i)>>;
     bfsqrt gfrsq c>>
    where c=rl2gf bfabs car(p := cdr ncoeffs p);

symbolic procedure rxgfc(p,x);
   <<x := gfabs x;
     for each i in cdr p do
        <<c := gfabstim(x,c); if i then c := gfplus(c,gfabs i)>>;
     bfsqrt gfrsq c>>
   where c=gfabs car(p := cdr ncoeffs p);

symbolic procedure gfabs x; (bfabs car x) . bfabs cdr x;

symbolic procedure gfabstim(b,c);
 <<(plubf(timbf(ba,ca),timbf(bd,cd))).plubf(timbf(ba,cd),timbf(bd,ca))>>
 where ba=car b,bd=cdr b,ca=car c,cd=cdr c;

symbolic procedure rxrl(p,r);
   <<r := bfabs r;
     for each i in cdr p do
        <<c := timbf(r,c); if i then c := plubf(c,bfabs i)>>;
     c>>
   where c=bfabs car(p := cdr ncoeffs p);

symbolic procedure csep p;
   %separate gfform p into real p and im whose roots are all complex.
   <<p := primpc mkinteg p;
     (<<if not atom g then <<p := gfcpquo(p,g); pfactor!# := t>>;
        (n2gf g) . p>>
 where g=gfgcd(primcoef fillz for each s in p collect (car s) . cadr s,
          primcoef fillz for each s in p collect (car s) . cddr s))>>;

symbolic procedure fillz p; if atom p then p else
   for each c in ncoeffs p collect if null c then 0 else c;

symbolic procedure primcoef p; if atom p then p else
   %trim leading and trailing zero coeffs, and make prim.
   begin integer d;
      p := reverse cdr p;
      while p and car p=0 do p := cdr p; if null p then return 0;
      p := reverse p; while p and car p=0 do p := cdr p;
      if null cdr p then return 1;
      for each c in p do d := gcdn(d,c);
      return
       (((length p-1) . for each c in p collect s*c/d)
        where s=sgn car p) end;

symbolic procedure gfgcd(p,q); if atom p or atom q then 1 else
   if car q>car p then gfgcdr(q,p) else gfgcdr(p,q);

symbolic procedure gfgcdr(p1,p2);
   <<if r=0 then p2 else if atom r then 1 else gfgcdr(p2,r)>>
   where r=pqrem(p1,p2);

symbolic procedure pqrem(p1,p2);
    %primitive pseudoremainder of p1,p2 in intcoeff form.
    begin scalar a,g,n,n2,m,m1,m2;
       n := car p1-(n2 := car p2); p1 := cdr p1;
       p2 := cdr p2; m2 := car p2;
   lp: g := gcdn(m1 := car p1,m2);
       m1 := m2/g;
       a := p1 := for each y in p1 collect y*m1; m := car a/m2;
       for each y in p2 do
          <<rplaca(a,car a-y*m); a := cdr a>>;
       p1 := cdr p1; if (n := n-1)>=0 then go to lp;
       return primcoef ((length p1-1) . p1) end;

symbolic procedure negprem(p1,p2);
   %computes the negative pseudoremainder of p1,p2 in fillz form.
   begin scalar a,g,n,n2,m,m1,m2;
      n := car p1-(n2 := car p2); p1 := cdr p1;
      p2 := cdr p2; m2 := car p2;
  lp: g := gcdn(m1 := car p1,m2); m1 := abs(m2/g);
      p1 := for each y in p1 collect y*m1; a := p1; m := car a/m2;
      for each y in p2 do <<rplaca(a,car a-y*m); a := cdr a>>;
      p1 := cdr p1; if (n := n-1)>=0 then go to lp;
      return primpn((n2-1) . for each y in p1 collect -y) end;

symbolic procedure gfcpquo(p,q);
 %quotient of gi poly p and integer poly q, a factor in fillz form.
   begin scalar n,c,a,d,f,z,pp; z := 0 . 0;
      n := car(p := ncoeffs p)-car q;
      pp := for each r in cdr p collect if r then r else z;
      c := car(q := cdr q);
loop: a := (caar pp)/c; d := (cdar pp)/c;
      if a neq 0 or d neq 0 then f := (n.(a . d)) . f;
      if (n := n-1)<0 then return f;
      p := pp;
      for each r in q do
         <<rplaca(p,(caar p-a*r).(cdar p-d*r)); p := cdr p>>;
      pp := cdr pp; go to loop end;

symbolic procedure deflate1(p,r);
 %fast rem . quotient function for real polynomial with real r.
 %all arithmetic is bf with no rounding.
   begin scalar q,n,c;
      n := car(p := ncoeffs p); p := cdr p; c := car p;
      for each i in cdr p do
         <<n := n-1; if mt!: c neq 0 then q := (n . c) . q;
           c := times!:(r,c); if i then c := plus!:(i,c)>>;
      return c . q end;

symbolic procedure deflate1c(p,r);
 %rem . quotient function for complex polynomial, with complex r.
 %all arithmetic is bf with no rounding.
   begin scalar q,n,c;
      n := car(p := ncoeffs p); p := cdr p; c := car p;
      for each j in cdr p do
         <<n := n-1; if not gfzerop c then q := (n . c) . q;
           c := gftimesn(r,c); if j then c := gfplusn(c,j)>>;
      return c . q end;

symbolic smacro procedure rl2gfc x;
   x . if atom x then 0.0 else bfz!*;

symbolic procedure accupr(p,q,r);
   begin scalar cq,cp,rl,!*bftag,s; integer ac;
      if caar lastpair q<2 then return 1; !*bftag := t;
      r := gf2bf r; cq := cpxp (q := bfloatem q); cp := cpxp p;
      rl := bfnump r or bfzp gfim r and (r := car r);
      q := % deflate root r or complex pair but do not round.
       if rl then cdr if cq then deflate1c(q,rl2gf r) else deflate1(q,r)
       else if not cq then deflate2(q,r)
       else cdr
         if cp then deflate1c(q,r)
         else deflate1c(cdr deflate1c(q,r),(car r) . minus!: cdr r);
      if caar q>0 then <<ac := acc!#; go to ret>>;
      if rl then r := rl2gfc r;
      p := bfsqrt bfloat gfrsq r;
     % decimal computation proved to be more precise in critical cases.
      p := round!:dec1(p,acc!#+2);
      p := 1 + cdr p + length explode abs car p;
loop: s := if caar lastpair q>1 then bfloat minbnd1(q,r) else
         bfsqrt gfrsq gfplus(r,
           <<s := cdar bfprim q; if not cq then rl2gf s else s>>);
     % decimal computation used here also for precision.
      s := round!:dec1(s,acc!#+2);
      ac := max(ac,rootacc!#,p-cdr s-length explode abs car s);
     % repeat minbnd1 test for conj r only if r is a complex pair and
     % q is complex.
      if cq and not rl and not cp then
         <<r := (car r) . minus!: cdr r; rl := t; go to loop>>;
 ret: if rootacc!#!# then ac := max(ac,rootacc!#!#);
      accm!# := max(ac,accm!#); return ac end;

symbolic procedure orgshift(p,org);
   %shifts origin of real or complex polynomial to origin org,
   %with p and org of the same form.
   begin scalar s,cp; integer n;
      if gfzerop(if (cp := cpxp p) then org else rl2gf org)
         then return p;
      org := gf2bf org;
      if numberp leadatom cdar p then p := bfloatem p;
      if cp then while p do
         <<p := deflate1c(p,org);
           if not gfzerop car p then s := (n.car p).s;
           n := n+1; p := cdr p>>
         else while p do
            <<p := deflate1(p,org);
              if bfnzp car p then s := (n.car p).s;
              n := n+1; p := cdr p>>;
       return reversip if pflt!# then cflotem s else bfrndem s end;

symbolic procedure bfrndem s;
   (for each c in s collect (car c) .
      if cp then (rndpwr cadr c) . rndpwr cddr c else rndpwr cdr c)
   where cp=cpxp s;

symbolic procedure r2flbf2r x; realrat r2flbf x;

symbolic procedure bfprim p;
   <<if ncpxp p
      then for each y in p collect (car y) . bfdivide(cdr y,d)
      else for each y in p collect (car y) . gfquotient(cdr y,d)>>
   where d=cdar lastpair p;

symbolic procedure primpc p;
   %make complex p primitive.
   begin integer d;
      for each y in p do d := gcdn(cadr y,gcdn(d,cddr y));
      return for each y in p collect
         (car y) . ((cadr y/d) . (cddr y/d)) end;

symbolic procedure ungffc p;
   begin scalar r,c;
      c := gtag cadar p;
      if caar p=0 then
         <<if not gfzerop cdar p then r := c.cdar p; p := cdr p>>;
      for each i in p do
         if not gfzerop cdr i then r := ((!*rvar.car i).(c.cdr i)).r;
      return r end;

%symbolic procedure iscale(d,y); mt!: y*2**(d+ep!: y);
symbolic procedure iscale(d,y); ashift(mt!: y,d+ep!: y);

symbolic procedure mkinteg p;
  %converts a polynomial in gfform to the smallest exactly equivalent
  %polynomial with integral coefficients.
   (begin integer m;
       p := bfloatem p; %convert to bfloat.
      % then work with powers of 2 to convert to integer.
       for each y in p do m := if nc then max(m,-ep!: cdr y) else
          max(m,-ep!: cadr y,-ep!: cddr y);
       p := for each y in p collect (car y) .
          if nc then iscale(m,cdr y) else
             (iscale(m,cadr y)) . iscale(m,cddr y);
       return if nc then primp p else primpc p end)
    where nc=ncpxp p;

symbolic procedure mkgirat j;
   %convert a gf complex into the equivalent gi rational form.
   begin scalar ra,rd,ia,id,ro,io;
      if eqcar(j,'!:dn!:) then
        <<ra := cadr j; ro := cddr j; rd := 1;
          if ro<0 then rd := 10^(-ro) else if ro>0 then ra := 10^ro;
          return mkrn(ra,rd) ./ 1>>
      else if pairp j and eqcar(car j,'!:dn!:) then
        <<ra := cadar j; ro := cddar j; rd := 1;
          if ro<0 then rd := 10^(-ro) else if ro>0 then ra := 10^ro;
          ia := caddr j; io := cdddr j; id := 1;
          if io<0 then id := 10^(-io) else if io>0 then ia := 10^io;
          ra := car(rd := cdr mkrn(ra,rd)); rd := cdr rd;
          ia := car(id := cdr mkrn(ia,id)); id := cdr id;
          go to lcm>>;
      if bfnump(j := gf2bf j) then return cdr !*rd2rn rootrnd bfloat j;
      j := gfrtrnd gf2bf j;
      ra := car(rd := cdr !*rd2rn car j); rd := cdr rd;
      ia := car(id := cdr !*rd2rn cdr j); id := cdr id;
 lcm: j := id/gcdn(id,rd)*rd; ro := j/rd; io := j/id;
      return ('!:gi!: . ((ra*ro) . (ia*io))) . j end;

symbolic procedure mkpoly rtl;
  if eqcar (rtl, 'list) then num mkpoly1 cdr rtl
   else typerr(if eqcar(rtl,'!*sq) then prepsq cadr rtl else rtl,
               "list");

symbolic procedure mkpoly1 r;
   if null cdr r then mkdiffer car r
      else 'times . list(mkdiffer car r,mkpoly1 cdr r);

symbolic procedure getroot(n,r);
   if (n := fix n)<1 or n>length(r := cdr r) then
      rerror(roots,4,"n out of range") else
      <<while (n := n-1)>0 do r := cdr r; caddar r>>;

symbolic procedure mkdiffer r; 'difference . cdr r;

symbolic operator mkpoly,getroot;

symbolic procedure a2gf x;
   %convert any interpretable input value to gf form.
   bfixup if bfnump x then rl2gf x
      else if not atom x and bfnump car x
         then (r2flbf car x) .  r2flbf cdr x else
      <<(if errorp y or null(y := car y)
         then error(0,list(x,"is illegal as root parameter")) else y)
      where y=errorset!*(list('a2gf1,mkquote x),nil)>>;

symbolic procedure a2gf1 x;
   <<x := numr d; d := denr d;
      if domainp x then
        if eqcar(x,'!:gi!:) then
           if atom d then gfquotbf(cadr x,cddr x,d)
           else if eqcar(d,'!:gi!:) then
              gfquotient(gi2gf x,gi2gf d) else nil
           else if bfnump(x := gfsimp x)
              then rl2gf bfdivide(r2flbf x,r2flbf gfsimp d) else x
      else if equal(caar x,'(i . 1)) then gfquotbf(cdr x,cdar x,d)>>
    where d = simp!* x;

symbolic procedure gi2gf x; (bfloat cadr x) . bfloat cddr x;

symbolic procedure gfquotbf(rl,im,d);
   (if rl then quotbf(rl,d) else bfz!*) . quotbf(im,d);

symbolic procedure quotbf(n,d);
   <<if eqcar(n,'!:rn!:) then <<d := cddr n; n := cadr n>>
     else n := ftout n;  divbf(bfloat n,bfloat d)>>;

symbolic procedure sturm1 p;
   %produces the sturm sequence as a list of ncoeff's
   begin scalar b,c,s;
      b := fillz primp intdiff (p := mkinteg p);
      s := list(b,p := !*intp := fillz p);
      if not atom b then repeat
         <<c := negprem(p,b); s := c . s;
           p := b; b := c>> until atom c;
      !*multrt := c=0;
      return !*strm := reverse s end;

symbolic procedure gfshift p;
   begin scalar pr,n,org; sprec!# := make!:ibf (3,1-!:bprec!:);
      !*xo := rl2gf 0; if null p then return !*xo;
      n := car (pr := ncoeffs bfprim p); if n>1 then pr := caddr pr;
      if pr then if cpxp p
         then !*xo := org := gfquotient(pr,rl2gf(-n))
         else !*xo := rl2gf(org :=bfdivide(pr,r2flbf(-n)));
      if null pr then return p;
      return orgshift(p,org) where pflt!#=null !*bftag end;

symbolic procedure p1rmult p; automod numr simp!* p1rmult1 p;

symbolic procedure p1rmult1 p;
   if atom p then nil
   else if atom cdr p then reval mk!*sq !*f2q caar p
   else {'times,p1rmult1 list car p,p1rmult1 cdr p};

symbolic procedure xoshift(p,nx);
   begin scalar n,org,pr,cp,orgc,a,b;
     % shift if abs p(mean) < p(origin).
      n := car (pr := ncoeffs bfprim p); if n>1 then pr := caddr pr;
      if null pr then return nil;
      org := if (cp := cpxp p) then gfquotient(pr,rl2gf(-n))
         else bfdivide(pr,r2flbf(-n));
      orgc := if cp then org else rl2gf org;
      if errorp(b := errorset!*(
            {'gfrsq,{'gfval,mkquote p,mkquote orgc}},nil))
         then return bflessp(gfrsq gf2bf orgc,bfone!*)
        else b := car b;
      a := gfrsq gfval(p,rl2gf 0);
      pmsg list("a=",a," b=",b);
      return not bflessp(a,b) end;

symbolic procedure gffinitr p;
   %do gffinit p but restore *bftag.
   (gffinit p) where !*bftag = !*bftag;

symbolic procedure invpoly p;
   %remove zero roots of p and reverse coefficients of p.
   <<p := ncoeffs for each r in p collect (car r-caar p) . cdr r;
     n2gf((car p) . reversip cdr p)>>;

symbolic procedure bdstest i;
   begin scalar y;
      if equal!:(rootrnd r2bf car i,y := rootrnd r2bf cdr i)
         then return y end;

symbolic procedure rlrtno2 p;
  if null sturm p then 0
  else if null lims!# then schinf(-1)-schinf 1 else
  begin scalar a,b; a := car lims!#;
    if null cdr lims!# then
       return if a<0 then schinf(-1)-schinf 0-adjst realrat 0
          else schinf 0-schinf 1;
    return if (b := cadr lims!#)='infty then schxa a-schinf 1
       else if a='minfty then schinf(-1)-schxb b
       else schxa a-schxb b end;

symbolic procedure schxa a; if xclp a then sch cdr a else sch a+adjst a;

symbolic procedure schxb b;
   if xclp b then sch cdr b+adjst cdr b else sch b;

symbolic procedure adjst l; if sgn1(car !*strm,l)=0 then 1 else 0;

symbolic procedure limadj m;
   if not m then lims!#
   else if length lims!#<2 then
      if remainder(m,2)=0 then list 1 else nil else
   ((if ratlessp(lval b,lval a) then list(b,a) else list(a,b))
    where a=lpwr(car lims!#,m),b=lpwr(cadr lims!#,m));

symbolic procedure gfstorval(pf,xn); !*xnlist := (pf . xn) . !*xnlist;

symbolic procedure gfgetmin;
   begin scalar y,nx,l; l := !*xnlist; nx := car (y := car l);
      for each x in cdr l do
         if bflessp(car x,nx) then nx := car (y := x);
      return cdr y end;

symbolic procedure calcprec(m,n,r,av,s2);
   begin integer p;
     p := if m<2 then 1+max(acc!#,n)
       else max(acc!#+1,n+1+ceillog m +
         if r=1 then 0 else
           max(if s2>2.2 or s2<1.0 then 0 else if s2>1.7 then 2 else 3,
             if m>3 and 1.5*av>n+1 then
                 fix(0.7*max(acc!#,7)*log float m+0.5) else 0));
    pmsg list("m=",m," n=",n," a=",acc!#," r=",r," av=",av,
      " s2=",s2,"->",p);
    return p end;

symbolic procedure rrpwr(r,m);
   <<while (m := m-1)>0 do rr := gftimes(rr,r); rr>>
   where rr=(r := a2gf r);

symbolic procedure cvt2(a,b); mt!: a neq 0 and mt!: b neq 0 and
   <<a := divide!:(round!:mt(a,20),round!:mt(b,20),16);
     lessp!:(a,bfone!*) and greaterp!:(a,decimal2internal(1, -1))>>;

symbolic procedure dsply nx; if !*rootmsg then
   << << write "  prec is ",2+precision 0; terpri();
   print_the_number nx; terpri(); wrs n>> where n=wrs nil>>;

symbolic procedure leadatom x; if atom x then x else leadatom car x;

symbolic procedure cvt5(a,b); equal!:(round!:mt(a,20),round!:mt(b,20));

endmodule;

end;
