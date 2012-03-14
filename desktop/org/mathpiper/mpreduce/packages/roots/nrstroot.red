module nrstroot; % Routines for finding the root of a polynomial which
 % is nearest to a given value (providing the root is sufficiently
 % close.)

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


Comment   modules bfdoer, bfdoer2, complxp, allroot, and rootaux of
roots.red needed also;

global '(bfz!* cpval!#);

fluid '(!*msg !*multiroot cpxt!# pfactor!# nrst!$ intv!# pfl!# !*bftag
 ss!# accm!# prm!# prec!# !*mb !*gfp !*keepimp !*xo nrst!$ pnn!# pfx!#
 !1rp !*complex !*resimp pgcd!# !*xn !*xobf prec!# !*hardtst acc!# rr!#
 prx!#);

symbolic procedure nearestroot args; % args = (p,rr)
 % finds root nearest to rr, unless accuroot abort occurs.  rr may be
 % real or complex. p can have zero root or multiple roots.  Most useful
 % for refining the value of a known root, by setting rootacc before
 % calling nearestroot.
  nrstroot(p,rr,nil) where p=car args,rr=cadr args;

put('nearestroot,'psopfn,'nearestroot);

symbolic procedure nrstroot(p,rr,trib);
   begin
      scalar x,c,d,dx,xm,r,cp,n,m,s,p1,prx!#,acc,pp,m1,!*msg,
        !*multiroot,cpxt!#,pfactor!#,nrst!$,intv!#,pfl!#,acfl!#,!*bftag;
      integer ss!#,accm!#,prm!#,prec!#;
      !!mfefix();
      p := cdr(c := ckpzro p); c := car c; !*mb := nil;
      if atom p then <<if c then c := 0; go to ret>>;
      if null rr then rr := 0;
      if trib then
        <<acc!# := max(acc!#,rr2acc rr);
          !*gfp := p := automod p; !*bftag := t;
          if atom trib then
        % test branch that calls gfnewton only.
           <<!*keepimp := numberp trib; !*xo := rl2gf 0;
             c := gfnewton(p,a2gf rr,4); go to ret>>
           else % test branch that goes through gfrootfind only
             <<!*keepimp := numberp car trib;
               c := gfrootfind(p,a2gf rr); go to ret>> >>;
      r := a2gf rr; acc := acc!# := max(acc!#,rr2acc rr);
      if c then if gfzerop r then <<c := 0; go to ret>> else
         <<d := bfloat gfrsq r; xm := bfz!*>>;
      m := powerchk p; nrst!$ := t;
      p := gfsqfrf p; automod !1rp; n := pnn!#; p1 := bfloatem !1rp;
      if length p>1 then pfactor!# := prx!# := n;
      if cpxp p then cpxt!# := t;
      if m then
         <<p := gfsqfrf cdr m; m := car m;
           ss!# := s := ceillog m>>;
loop: pp := automod car(x := car p); cp := nil;
      if cpxp pp then
         <<pp := car(cp := csep pp); cp := cdr cp;
           if atom pp then go to cpr else pfactor!# := prx!# := n>>;
 mod: pp := automod pp; r := gf2bf r;
     % powerchk may succeed after sqfrf or csep succeeds.
      if (m1 := powerchk pp) then <<pp := cdr m1; m1 := car m1>>;
      if not m and not m1 then
         <<x := nrstrt0(pp,r,p1); go to col>>;
      x := if m1 then
         nrpass2(m1,nrpass1(pp,r,if m then m1*m else m1),r,p1,acc)
            else nrpass1(pp,r,m);
      if m then x := nrpass2(m,x,r,p1,acc);
     % however we get to the next line, cpval!# has been set.
 col: x := cdr x;
      dx := gfrsq gfdiffer(if bfnump x then (x := x . bfz!*) else x,
         gf2bf r);
      if not d or d and bfleqp(dx,d) then
        <<d := dx; xm := mkdn cpval!#>>;
 cpr: if cp then <<pp := cp; cp := nil; go to mod>>;
      if (p := cdr p) and not domainp caar p then go to loop;
      c := xm;
 ret: return allout if c then {(c . acc!#)} else nil end;

symbolic procedure rr2acc rr;
  (begin scalar !*msg,c;
    c := !*complex; on complex;
    for each n in rr2nl rr do form1(n,nil,'algebraic);
    (simp!* rr) where !*resimp=t;
    if not c then off complex;
    pr := precision pr; return pr end)
    where pr=precision 6;

symbolic procedure rr2nl rr; rr2nl1(rr,nil);

symbolic procedure rr2nl1(rr,nl);
   if numberp rr then {'!:int!:,rr} . nl
     else if atom rr then nl
     else if car rr eq '!:dn!: then {'!:int!:,cadr rr} . nl
     else rr2nl1(car rr,rr2nl1(cdr rr,nl));

symbolic procedure nrstrt0(q,r,p1);
   begin scalar rr,x,b,pr,ps,p2,qf; pmsg pbfprint q; b := !*bftag;
      ps := getprec(); pr := minprec(); pgcd!# := not p1;
      p2 := gfzerop(rr := a2gf r); !*gfp := qf := q;
      if b then go to r2;
      if errorp(q := errorset!*({'cflotem,mkquote qf},nil))
        or errorp(r := errorset!*({'gf2fl,mkquote rr},nil))
         then go to r1 else <<q := car q; r := car r>>;
      if (x := gfrootset(q,r,b)) then
         <<q := qf; !*xn := gf2bf !*xn;
           !*xobf := !*xo := gf2bf !*xo; go to r3>>;
  r1: q := qf; b := !*bftag := t; r := gf2bf rr;
  r2: x := gfrootfind(q,r); !*xobf := !*xo := gf2bf !*xo;
  r3: if not !*hardtst then x := ckacc(q,if p1 then p1 else q,!*xn);
      x := accuroot(
      if bfzp gfim r then (car !*xn) . bfabs cdr !*xn else !*xn,q,!*xo);
      if prec!#<pr+2 then
         <<setflbf b; setprec ps; return acc!# . x>>;
      setprec(pr := prec!#);
      if not !*bftag then b := !*bftag := t;
      if p2 then go to r2 else <<p2 := t; go to r1>> end;

symbolic procedure nrpass1(pp,rr,m);
   nrstrt0(pp,rrpwr(rr,m),nil) where ss!#=ceillog m;

symbolic procedure nrpass2(m,x,rr,p1,acc);
   begin scalar s; s := ceillog m;
      return (nrstrt0(pconstr(m,cdr x),rr,p1)
         where acc!#=max(acc,car x-s),rr!#=1,
            ss!#=0,pfactor!#=(pfactor!# or car x-s>acc)) end;

endmodule;


end;
