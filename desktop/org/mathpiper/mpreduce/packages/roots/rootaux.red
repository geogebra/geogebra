module rootaux; % Support for allroot, previously in realroot.

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


global '(!!nfpd max!-acc!-incr lm!#);

max!-acc!-incr := 8;

fluid '(!*xn !1rp acc!# ss!# rootacc!#!# rprec!# cpxt!# pfactor!# prx!#
  nrst!$ intv!# rlrt!# lims!# pnn!# rr!# !*strm !*xnlist sh!#
  !*nosturm);

fluid '(!*compxroots !*bftag !*roundbf !*msg);

symbolic procedure accupr1(y,p);
 % if acc!# is insufficient to separate this root from roots of
 % other factors of !1rp, increase accuracy.
   <<!*xn := y;
     while (acr := accupr(p,bfloatem !1rp,!*xn))>acc!# do
          <<acc!# := acr; y := accuroot(y,p,rl2gf 0)>>;
     y . (acc!#+ss!#)>> where acr=acc!#;

symbolic procedure uniroots(p,rrts);
   <<!!mfefix();
     if eqcar(aeval p,'list) then
        (algebraic multroot(pr,p)
         where pr=if rootacc!#!# then rootacc!#!# else precision 0,
               !*compxroots=if rrts=0 then nil else t)
     else if rrts=0 then
        (<<rprec!# := max(!!nfpd,rprec!# or 7);
           uniroot0(p,0)>>
         where !*bftag=t,!*roundbf=t,rprec!#=rprec!#)
     else uniroot0(p,rrts)>> where !*msg=nil;

symbolic procedure uniroot0(p,rrts);
   begin
    scalar c,lim,n,p1,pp,q,r,r1,rr,x,cp,m,cpxt!#,pfactor!#,acc,s,
       prx!#,m1,nrst!$,intv!#,rlrt!#,rrc;
    integer ss!#;
      p := cdr(c := ckpzro p);
      if (c := car c) then c := {({(caaar c) . 6}. cdar c)};
     % lims!# code is applicable only when realroots is called.
      if lims!# then if not cdr lims!# or
         <<r := car lims!#; r1 := cadr lims!#;
           r neq 'minfty and
              (if xclp r then cadr r>=0 else car r>0)
           or r1 neq 'infty and
              (if xclp r1 then cadr r1<=0 else car r1<0)>>
            then c := nil;
      if atom p then
        <<r := if c then c else {(nil . 1)}; go to ret>>;
      if cpxp p then cpxt!# := t;
      m := powerchk p; % top level powergcd factoring.
      p := if !*nosturm and rrts neq 0 then {(!1rp := p) . 1}
         else gfsqfrf p;
      automod !1rp; n := pnn!#;
      p1 := !1rp; % save original one-factor polynomial.
      if length p>1 then pfactor!# := prx!# := n;
      if m then
         <<p := if !*nosturm and rrts neq 0 then {(!1rp := cdr m) . 1}
              else gfsqfrf cdr m;
           m := car m; ss!# := s := ceillog m>>;
      lim := acc!#+max!-acc!-incr;
      q := p; r1 := nil; r := c; acc := acc!#;
loop: pp := automod car(x := car q); cp := nil;
      if cpxp pp then
         <<pp := car(cp := csep pp); cp := cdr cp;
           if atom pp then go to cpr else pfactor!# := prx!# := n>>;
     % first find the real roots and complex pairs, if any.
 mod: pp := automod pp;
     % powerchk may succeed after sqfrf or csep succeeds.
      if (m1 := powerchk pp) then <<pp := cdr m1; m1 := car m1>>;
      if not m and not m1 then
         <<rr := doroots(pp,rrts,t); go to col>>;
      rr := if m1 then
         rtpass2(m1,rtpass1(pp,m1,rrts,if m then m1*m else m1),
           rrts,p1,acc,m)
            else rtpass1(pp,m,rrts,m);
      if m then rr := rtpass2(m,rr,rrts,p1,acc,nil);
 col: rrc := for each y in rr collect car y;
     % the following test should never succeed!
      for each y in rrc do if member(y,r1) then rooterr y;
      r1 := append(r1,rrc);
      r := append(r,list(rr . cdr x));
 cpr: if cp and rrts>0 then
     % now find roots of cp, which has only complex roots.
         <<pp := cp; cp := nil; go to mod>>;
      if (q := cdr q) and not domainp caar q then go to loop;
 ret: return outecho r end;

symbolic procedure rtpass1(pp,m,rrts,m2);
   doroots(pp,rrts,nil) where lims!#=limadj m2,ss!#=ceillog m;

symbolic procedure rtpass2(m,rr,rrts,p1,acc,m2);
   begin scalar pp,s; s := ceillog m;
      return for each y in rr join
         (<<pp := pconstr(m,car y); doroots(pp,rrts,not m2)>>
           where !1rp=p1,acc!#=max(acc,cdr y-s),rr!#=1,
             ss!#=0,pfactor!#=(pfactor!# or cdr y-s>acc),
             lims!#=limadj m2) end;

symbolic procedure doroots(pp,r,s);
   if r=0 then rtsreal(pp,s) else allroots(pp,s);

symbolic procedure rooterr y;
   lprim list(y,"is false repeated root.  Send input to S. L. Kameny")
    where !*msg=t;

symbolic procedure schinf z;
   begin scalar v,v1; integer r;
      v := schinf1(car !*strm,z := sgn z);
      if v=0 then return schplus realrat z;
      for each p in cdr !*strm do
         <<v1:= if atom p then p else schinf1(p,z);
           if v*v1<0 then r := r+1; if v1 neq 0 then v := v1>>;
      return r end;

symbolic procedure schplus z; sch ratplus(z,offsetr(caar !*strm,z));

symbolic procedure schinf1(p,z);
   if z=0 then car lastpair p else (z**car p)*sgn cadr p;

symbolic procedure bfnewton (p,p1,nx,ri,kmax);
   begin scalar ri,px,pf,pf0,x0,xe,k,xk,xr,lp; integer m;
      !*xnlist := nil; lm!# := 0;
      lm!# := nwterrfx(caar lastpair p,nil);
      gfstorval(pf0 := bfabs(px := rlval(p,nx)),nx);
      if bfzp pf0 then <<trmsg1('nwt,nx); go to ret>>;
newt: x0 := nx; if bfzp(xe := rlval(p1,nx)) then go to ret1;
      nx := bfplus(nx,xe := bfminus bfdivide(px,xe));
      px := rlval(p,nx);
     % if realroot case, check range of nx.
      if not ri then go to tst2;
      if ratleqp(car ri,xr := realrat nx) and ratleqp(xr,cdr ri)
         then go to tst;
     % fall through if nx out of range.
      nx := tighten(ri,p,bfabs px,sh!#);
      if null !*xnlist then go to ret2;
      movebds(ri,xr := ratmean(car ri,cdr ri),sh!#);
      px := rlval(p,nx := r2flbf xr);
      lp := k := xk := pf := nil; go to newt;
 tst: movebds(ri,xr,sh!#);
      if bdstest ri then go to ret;
     % test for start of loop unless already in loop.
tst2: pf0 := pf; pf := bfabs px;
      if (not lp) and pf0 and bfleqp(pf0,pf) then
          <<if kmax<2 then go to ret3 else lp := t>>;
      trmsg2 (if lp then 'loop else 'nwt,nx,px);
      if bfzp pf then <<trmsg1 ('nwt,nx); go to ret>>;
      if bfeqp(nx,x0) then <<trmsg3 ('nwt,nx);go to ret>>;
      gfstorval(pf,nx);
     % next line initializes or updates loop variables.
      if k then <<xk := bfplus(xk,nx); k := k+1>>
         else if lp then <<k := 1; xk := nx>>;
      if k=kmax then
         <<nx := bfrlmult(1.0/k,xk);
           gfstorval(bfabs (px := rlval(p,nx)),nx);
           trmsg6(k,nx,px); go to ret3>>;
      nwterr(m := m+1); go to newt;
ret3: nx := gfgetmin(); trmsg7(nx);goto ret;
ret2: if nx then go to ret;
ret1: trmsg10 'nwt;
 ret: !*xnlist := nil; return nx end;

endmodule;

end;
