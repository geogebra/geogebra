module rounded; % *** Support for Arbitrary Rounded Arithmetic.

% Authors: Anthony C. Hearn and Stanley L. Kameny.

% Last updated: 23 June 1993.

% Copyright (c) 2000, Anthony C. Hearn.  All rights reserved.

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


Comment this module defines a rounded object as a list with two fields:

      (<tag>.<structure>).

The <structure> depends on the precision.  It is either a floating point
number or the stripped bfloat (mt . ep);

exports chkint!*, chkrn!*, convprec, convprec!*, deg2rad!*,
        i2rd!*, logfp, mkround, rd!:difference, rd!:minus, rd!:minusp,
        rd!:onep, rd!:plus, rd!:prep, rd!:prin, rd!:quotient,
        rd!:simp, rd!:times, rd!:zerop, rdprep1, rdqoterr, rdzchk,
        rndbfon, round!*, roundbfoff, roundbfon, roundconstants,
        safe!-fp!-times;

imports !*d2q, !:difference, !:minus, !:minusp, !:zerop, abs!:, aeval,
        apply1, bf2flr, bfdiffer, bfexplode0, bfinverse, bflessp,
        bfloat, bfminus, bfminusp, bfprin!:, bftrim!:, bfzerop!:,
        bfzp, ceiling, copyd, deg2rad!*, difbf, divbf, dmoderr,
        ep!:, eqcar, equal!:, errorp, errorset!*, fl2int,
        fl2rd, float!-bfp, floor, ft2rn1, geq, greaterp!:, grpbf,
        i2bf!:, initdmode, invbf, leq, lessp!:, log, lprim, lshift,
        make!:ibf, make!:rd, minus!:, minusp!:, mkquote, msgpri, mt!:,
        neq, normbf, off1, on1, over, plubf, preci!:, r2bf, rd2fl,
        rd!:forcebf, realrat, rerror, retag, rmsubs, round!:mt, setk,
        sqrt, timbf, times!:, union;

fluid '(!:prec!: !:bprec!: !:print!-prec!: minprec!# rootacc!#!#);

fluid '(dmode!* !*bfspace !*numval !*roundbf !*!*roundbf !*norndbf);

fluid '(!*noconvert);

global '(bfone!* epsqrt!* log2of10);

global '(domainlist!* !!nfpd !!nbfpd !!flprec !!rdprec mxflbf!!
         mnflbf!!);

global '(!!plumax !!plumin !!timmax !!timmin !!maxflbf !!minflbf
         !!fleps1 !!fleps2 !!flint !!maxbflexp log2 !!maxarg);

global '(rd!-tolerance!* cr!-tolerance!* yy!! bfz!* !!smlsin);

switch rounded;

%Set value for !!flprec. It never changes.
!!flprec := !!nfpd - 3;

!!smlsin := 10.0^-(2+!!flprec);

symbolic procedure logfp x;
  % floating log of x**(1/n) using bfloat logic as boost.
  (log(m/float lshift(1,p))+(p+ep!: x)*log2)
    where p=(preci!: x - 1) where m=mt!: x;

symbolic procedure roundconstants;
   <<!!plumax := 2.0**(!!maxbflexp -1);
     !!minflbf := invbf(!!maxflbf := make!:ibf (1,!!maxbflexp));
    % plumin must be large enough to avoid underflow from difference.
     !!plumin := 10.0**!!flprec/!!plumax;
     !!timmin := 1/(!!timmax := sqrt(!!plumax));
     !!maxarg := logfp !!maxflbf>>;

switch bfspace,numval,roundbf; % norndbf.

!*bfspace := nil;
!*numval := t;

put('roundbf,'simpfg,'((t (roundbfon)) (nil (roundbfoff))));

symbolic procedure roundbfon; !*!*roundbf := t;

symbolic procedure roundbfoff; !*!*roundbf := !!rdprec > !!flprec;

%  put('rounded,'package!-name,'arith);  % Use if ARITH autoloaded.

domainlist!* := union('(!:rd!:),domainlist!*);

put('rounded,'tag,'!:rd!:);
put('!:rd!:,'dname,'rounded);
flag('(!:rd!:),'field);
put('!:rd!:,'i2d,'i2rd!*);
put('!:rd!:,'minusp,'rd!:minusp);
put('!:rd!:,'plus,'rd!:plus);
put('!:rd!:,'times,'rd!:times);
put('!:rd!:,'difference,'rd!:difference);
put('!:rd!:,'quotient,'rd!:quotient);
put('!:rd!:,'zerop,'rd!:zerop);
put('!:rd!:,'onep,'rd!:onep);
put('!:rd!:,'prepfn,'rd!:prep);
put('!:rd!:,'prifn,'rd!:prin);
put('!:rd!:,'minus,'rd!:minus);
put('!:rd!:,'rootfn,'rd!:root);
put('!:rd!:,'!:rn!:,'!*rd2rn);
put('!:rn!:,'!:rd!:,'!*rn2rd);

symbolic procedure round!* x;
   % Returns actual number representation, as either float or bfloat.
%   retag cdr x;
   if float!-bfp x then rd2fl x else x;

symbolic procedure mkround u;
   % inverse operation to round!*, i.e. tags a naked float
   if atom u then make!:rd u else u;

%symbolic procedure roundbfp; !*roundbf or !!rdprec > !!flprec;

symbolic procedure print!-precision n;
   % Set the system printing precision !:print!-prec!:.
   % Returns previous value.
   begin scalar oldprec;
      if n=0 then return !:print!-prec!:;
      if n<0 then
         << oldprec := !:print!-prec!:;
            !:print!-prec!: := nil;
            return oldprec >>;
      if n > !:prec!: then
         << msgpri(nil,"attempt to set print!-precision greater than",
                       "precision ignored",nil,nil);
            return nil >>;
      oldprec := !:print!-prec!:;
      !:print!-prec!: := n;
      return oldprec
   end;

symbolic procedure print_precision n;
   % Alternative name.
   print!-precision n;

symbolic procedure precision0 n;
  % called from algebraic call of precision.
   if n member '((nil) () (reset))
      then <<rootacc!#!# := nil; precision !!flprec>>
   else if cdr n
     or not numberp(n := prepsq simp!* aeval {'fix,prepsq simp!* car n})
     or n<0 then
       rerror(arith,5,"positive numeric value or `RESET' required")
   else <<if n>0 then rootacc!#!# := max(n,6); precision n>>;

put('precision,'psopfn,'precision0);

symbolic procedure precision n;
   % Set the system precision !!rdprec, bfloat precision !:prec!:,
   % and rd!:onep tolerance. Returns previous value.
   <<if not numberp n or n<0
       then rerror(arith,6,"positive number required");
     precision1(n,t)>>;

log2of10 := log 10 / log 2;

symbolic procedure decprec2internal p;
   ceiling(p * log2of10) + 3;

% symbolic procedure internal2decprec p;
%    floor ((p - 3) / log2of10);

symbolic procedure precision1(n,bool);
   begin scalar oldprec;
      if n=0 then return !!rdprec;
      if bool then rmsubs();  % So that old results are resimplified.
      oldprec := !!rdprec;
      !:prec!: :=
        (!!rdprec := if !*roundbf then n else max(n,minprec!#))+2;
      if !:print!-prec!: and n < !:print!-prec!:+2
         then !:print!-prec!: := nil; %unset
      !:bprec!: := decprec2internal !:prec!:;
      epsqrt!* := make!:ibf(1, -!:bprec!:/2);
      rd!-tolerance!* := make!:ibf(1, 6-!:bprec!:);
      cr!-tolerance!* := make!:ibf(1, 2*(6-!:bprec!:));
%     if !!rdprec <= !!flprec then
%        <<!!fleps1 := 1.0/float(2.0**(!:bprec!: - 2));
%          !!fleps2 := !!fleps1**2>>;
      !*!*roundbf := !!rdprec > !!flprec or !*roundbf;
      return oldprec end;

flag('(print!-precision),'opfn); % Symbolic operator print!-precision.
flag('(print_precision),'opfn);  % Symbolic operator print_precision.

symbolic procedure !*rd2rn x;
 % Converts a rounded number N into a rational to the system precision.
 % Elegant form: uses both rd2rn1 and realrat... and choses the best,
 %  but uses a heuristic to avoid the extra work when not needed.
   begin scalar n,p,r,r1,r2,d1,d2,ov;
     if rd!:zerop x then return '!:rn!: . (0 . 1);
     p := precision 0;
     r := rd2rn1 x;
     r1 := '!:rn!: . r;
     if abs car r<10 or cdr r<10
       or 2*max(length explode cdr r,length explode abs car r)<p+1
         then go to ret;
     r2 := '!:rn!: . realrat bftrim!: rd!:forcebf x;
     precision(2+p);
     d1 := !:difference(x,r1); if !:minusp d1 then d1 := !:minus d1;
     d2 := !:difference(x,r2); if !:minusp d2 then d2 := !:minus d2;
     if !:zerop d2 or !:minusp !:difference(d2,d1) then ov := t;
     precision p;
ret: return if ov then r2 else r1 end;

symbolic procedure rd2rn1 n;
   if float!-bfp n then ft2rn1 rd2fl n else bf2rn1 n;

symbolic procedure bf2rn1 n;
  % Here, the nonzero input n is always a binary bigfloat
   begin scalar negp,a,p0,p1,q0,q1,w,flagg,nn,r0,r1;
      if mt!: n<0 then <<negp := t; n := minus!: n>>;
      nn := n;
 top: a := ((if d=0 then m else lshift(m,d))
              where m=mt!: n,d=ep!: n);
      n := difbf(n,normbf i2bf!: a);
      if not flagg
        then <<flagg := t; p0 := 1; p1 := a; q0 := 0; q1 := 1>>
       else <<w := p0 + a*p1; p0 := p1; p1 := w; r0 := r1;
              w := q0 + a*q1; q0 := q1; q1 := w>>;
      r1 := abs!: difbf(nn,divbf(i2bf!: p1,i2bf!: q1));
     % temporary write statement here
    % if !*trrd2rn1 then << write p1 . q1," -> ",r1; terpri()>>;
      if bfzerop!: n or bfzerop!: r1
          then return if negp then (-p1) . q1 else p1 . q1
       else if r0 and not greaterp!:(r0,r1)
          then return if negp then (-p0) . q0 else p0 . q0;
      n := invbf n;
      go to top
  end;

symbolic procedure !*rn2rd u;
   % Converts the (tagged) rational u/v into a (tagged) rounded
   % number to the system precision, after testing to number
   mkround chkrn!* r2bf cdr u;

minprec!# := min(6,!!flprec-2);

precision1(!!flprec,nil);        % Initial value = effective float prec.

%!!fleps1 := 1.0/float(2.0**(!:bprec!: - 6));
!!fleps1 := 2.0**(6 - !:bprec!:);
!!fleps2 := !!fleps1**2;

symbolic procedure precmsg pr;
   if pr>!!rdprec then
      <<msgpri(nil,"precision increased to",pr,nil,nil);
        precision1(pr,t)>>;

symbolic procedure rd!:simp u;
   if null atom u and car u=0 then nil ./ 1
    else if null dmode!* or dmode!* eq '!:gi!:
     then (if eqcar(x,'!:rn!:) then cdr x else x ./ 1)
           where x = !*rd2rn make!:rd u
    else if dmode!* memq '(!:rd!: !:cr!:)
     then (mkround convprec!* u) ./ 1 % Must call convprec!*, since
                                      % precision may have changed.
    else (if y then !*d2q apply1(y,make!:rd u)
           else dmoderr('!:rd!:,dmode!*))
          where y = get('!:rd!:,dmode!*);

put('!:rd!:,'simpfn,'rd!:simp);

symbolic procedure rndbfon; if not !*norndbf then
   <<!*!*roundbf := t;
     if !:prec!:<!!flprec+3 then
         <<!*roundbf := t;
           lprim "ROUNDBF turned on to increase accuracy">>>>;

symbolic procedure i2rd!* u;
% Converts integer U to tagged rounded form.
 mkround chkint!* u;

symbolic procedure chkint!* u;
    if !*!*roundbf then bfloat u else
     ((if floatp u then u   % Added by ACN to make i2rd!* work with floats.
       else if msd!: x <= !!maxbflexp then float u
       else <<rndbfon(); bfloat u>>) where x=abs u);

mnflbf!! := invbf(mxflbf!! := make!:ibf (1, 800));

symbolic procedure chkrn!* u;
   if !*!*roundbf then u else bf2flck u;

symbolic procedure bf2flck u;
   if !*!*roundbf then u
   else if mt!: u=0 then 0.0 else
    ((if not grpbf(!!minflbf,r) and not grpbf(r,!!maxflbf)
         then bf2flr u
      else <<rndbfon(); u>>) where r := abs!: u);

symbolic procedure convchk x;
   if !*!*roundbf then if atom x then bfloat x else x
   else if atom x then x else bf2flck x;

symbolic procedure convprec!* u;
   convchk retag u;

symbolic procedure convprec u; convchk round!* u;

symbolic procedure rd!:minusp u;
  % bfminusp round!* u;
   if float!-bfp u then minusp rd2fl u else minusp!: u;

symbolic procedure convprc2(u,v);
   <<u := convprec u; yy!! := convprec v;
     if !*roundbf then <<yy!! := bfloat yy!!; bfloat u>> else u>>;

symbolic procedure rdzchk(u,x,y);
 if atom u then
    if u=0.0 or x>0.0 and y>0.0 or x<0.0 and y<0.0 then u
    else if abs u<(abs x)*!!fleps1 then 0.0 else u
 else
    if mt!: u=0 or mt!: x>0 and mt!: y>0 or mt!: x<0 and mt!: y<0 then u
    else if lessp!:(abs!: u,times!:(abs!: x,rd!-tolerance!*)) then bfz!*
    else u;

symbolic procedure rd!:plus(u,v);
  (if not !*!*roundbf and atom cdr u and atom cdr v
      and (z := safe!-fp!-plus(cdr u,cdr v)) then make!:rd z else
   begin scalar x,y;
      x := convprc2(u,v); y := yy!!;
      u := if not atom x then plubf(x,y) else
         <<z := errorset!*(list('plus2,mkquote x,mkquote y),nil);
           if errorp z
             then <<rndbfon(); plubf(x := bfloat x,y := bfloat y)>>
             else car z>>;
      return mkround rdzchk(u,x,y) end) where z=nil;

symbolic procedure rd!:difference(u,v);
  (if not !*!*roundbf and atom cdr u and atom cdr v
      and (z := safe!-fp!-plus(cdr u,-cdr v)) then make!:rd z else
   begin scalar x,y;
      x := convprc2(u,v); y := yy!!;
      u := if not atom x then difbf(x,y) else
         <<z := errorset!*(list('difference,mkquote x,mkquote y),nil);
           if errorp z
             then <<rndbfon(); difbf(x := bfloat x,y := bfloat y)>>
             else car z>>;
      return mkround rdzchk(u,x,if atom y then -y else minus!: y) end)
   where z=nil;

symbolic procedure rd!:times(u,v);
  (if not !*!*roundbf and atom cdr u and atom cdr v
      and (z := safe!-fp!-times(cdr u,cdr v)) then make!:rd z else
   begin scalar x,y;
      x := convprc2(u,v); y := yy!!;
      return mkround if not atom x then timbf(x,y) else
         <<z := errorset!*(list('times2,mkquote x,mkquote y),nil);
           if errorp z then <<rndbfon(); timbf(bfloat x,bfloat y)>>
              else car z>> end) where z=nil;

symbolic procedure rd!:quotient(u,v);
  if !:zerop v then rerror(arith,7,"division by zero") else
  (if not !*!*roundbf and atom cdr u and atom cdr v
      and (z := safe!-fp!-quot(cdr u,cdr v)) then make!:rd z else
   begin scalar x,y;
      x := convprc2(u,v); y := yy!!;
      if atom x and zerop y then rdqoterr();
      return mkround if not atom x then
         if mt!: y=0 then rdqoterr() else divbf(x,y)
         else
           <<z := errorset!*(list('quotient,mkquote x,mkquote y),nil);
             if errorp z then <<rndbfon(); divbf(bfloat x,bfloat y)>>
                else car z>> end) where z=nil;

symbolic procedure rdqoterr; error(0,"zero divisor in quotient");

% symbolic procedure safe!-fp!-plus(x,y);
%    if zerop x then y else if zerop y then x else
%    begin scalar u;
%       if x>0.0 and y>0.0 then
%          if x<!!plumax and y<!!plumax then go to ret else return nil;
%       if x<0.0 and y<0.0 then
%         if -x<!!plumax and -y<!!plumax then go to ret else return nil;
%       if abs x<!!plumin and abs y<!!plumin then return nil;
%  ret: return
%         if (u := plus2(x,y))=0.0
%            or x>0.0 and y>0.0 or x<0.0 and y<0.0 then u
%         else if abs u<(abs x)*!!fleps1 then 0.0 else u end;

symbolic procedure safe!-fp!-plus(x,y);
   if zerop x then y
    else if zerop y then x
    else if x>0.0 and y>0.0
     then if x<!!plumax and y<!!plumax then plus2(x,y) else nil
    else if x<0.0 and y<0.0
     then if -x<!!plumax and -y<!!plumax then plus2(x,y) else nil
    else if abs x<!!plumin and abs y<!!plumin then nil
    else (if u=0.0 then u else if abs u<!!fleps1*abs x then 0.0 else u)
         where u = plus2(x,y);

symbolic procedure safe!-fp!-times(x,y);
 if zerop x or zerop y then 0.0
 else if x=1.0 then y else if y=1.0 then x else
   begin scalar u,v; u := abs x; v := abs y;
      if u>=1.0 and u<=!!timmax then
         if v<=!!timmax then go to ret else return nil;
      if u>!!timmax then if v<=1.0 then go to ret else return nil;
      if u<1.0 and u>=!!timmin then
         if v>=!!timmin then go to ret else return nil;
      if u<!!timmin and v<1.0 then return nil;
 ret: return times2(x,y) end;

symbolic procedure safe!-fp!-quot(x,y);
 if zerop y then rdqoterr()
 else if zerop x then 0.0 else if y=1.0 then x else
   begin scalar u,v; u := abs x; v := abs y;
      if u>=1.0 and u<=!!timmax then
         if v>=!!timmin then go to ret else return nil;
      if u>!!timmax then if v>=1.0 then go to ret else return nil;
      if u<1.0 and u>=!!timmin then
         if v<=!!timmax then go to ret else return nil;
      if u<!!timmin and v>1.0 then return nil;
 ret: return quotient(x,y) end;

symbolic procedure rd!:zerop u;
  % bfzp round!* u;
   if float!-bfp u then zerop rd2fl u else mt!: u = 0;

symbolic procedure rd!:minus u;
  % mkround bfminus round!* u;
   if float!-bfp u then fl2rd (- rd2fl u) else minus!: u;

symbolic procedure rd!:onep u;
   % We need the tolerance test since some LISPs (e.g. PSL) can print
   % a number as 1.0, but it doesn't equal 1.0!
   if float!-bfp u then abs(1.0 - rd2fl u)<!!fleps1
    else equal!:(bfone!*,bftrim!: u);

symbolic procedure rd!:root(u,n);
   if float!-bfp u then fl2rd expt(rd2fl u,recip float n)
    else texpt!:any(u,quotient!:(bfone!*,i2bf!: n));

% Since decimal input -> :rd: in all dmodes, dmode!* must be used to
% determine whether to round to current precision,  but input never gets
% truncated, since precision is always increased at input time.
% to avoid inaccuracies in floating point representation, rd!:prep
% returns values in bfloat format.

symbolic procedure rd!:prep u;
   if !*noconvert then rdprep1 u
    else if rd!:onep u then 1
   else if rd!:onep rd!:minus u then -1 else rdprep1 u;

%symbolic procedure rdprep1 u;
%   if float!-bfp u then
%     if not dmode!* memq '(!:rd!: !:cr!:) or !*!*roundbf
%       then round!:mt(bfloat rd2fl u,min(!:bprec!:,!!nbfpd))
%      else if !:bprec!:>!!nbfpd then u
%      else fl2rd bf2flr round!:mt(bfloat rd2fl u,!:bprec!:)
%    else round!:mt(u,!:bprec!:);

symbolic procedure rdprep1 u;
   % Using cdr u to get actual float leads to various glitches.
   if float!-bfp u then u else round!:mt(u,!:bprec!:);

symbolic procedure rd!:prin u;
  % Printed output is rounded to 2 fewer places than internal value.
   bfprin!: bftrim!: rd!:forcebf u;

symbolic procedure rd!:explode u;
   bfexplode0 bftrim!: rd!:forcebf u;

initdmode 'rounded;

put('evalf,'psopfn,'evalf0);

procedure evalf0(u);
   % Return first argument as a float wrt. the current precision even
   % with off rounded. Optional second argument overrides the current
   % precision.
   begin scalar sp,w;
      if cdr u then
	 sp := precision0 cdr u;
      if !*rounded then
      	 w := aeval car u
      else <<
      	 on1 'rounded;
      	 w := aeval car u;
      	 off1 'rounded;
      >>;
      if cdr u then <<
	 if cadr u > sp then <<
	    prin2 "*** required accuracy exceeds current precision (";
	    prin2 sp;
	    prin2t ")";
 	    prin2t "*** printing with required accuracy ...";
	    mathprint w;
	    prin2t "*** finished printing"
	 >>;
	 precision0 {sp}
      >>;
      return w
   end;

put('evalnum,'psopfn,'evalnum0);

procedure evalnum0(u);
   % Return the exact algebraic representation of the first argument
   % rounded to the current precision. Optional second argument
   % overrides the current precision.
   begin scalar sp,w;
      if cdr u then
	 sp := precision0 cdr u;
      if !*rounded then
      	 w := aeval car u
      else <<
      	 on1 'rounded;
      	 w := aeval car u;
      	 off1 'rounded;
      	 w := aeval w
      >>;
      if cdr u then
      	 precision0 {sp};
      return w
   end;

!#if (memq 'psl lispsystem!*)

symbolic procedure list!-to!-string u;
  compress ('!" . append(u, '(!")));

symbolic procedure hexdig w;
  cdr assoc(w, '((0 . !0) (1 . !1) (2 . !2) (3 . !3)
                 (4 . !4) (5 . !5) (6 . !6) (7 . !7)
                 (8 . !8) (9 . !9) (10 . !a) (11 . !b)
                 (12 . !c) (13 . !d) (14 . !e) (15 . !f)));

symbolic procedure explodehex n;
  begin
% Only for use with integers
    scalar r, s;
    if n = 0 then return "0";
    if n < 0 then << n := -n; s = t >>;
    while not zerop n do <<
       r := hexdig remainder(n, 16) . r;
       n := n / 16 >>;
    if s then r := '!- . r;
    return r
  end;

!#endif

symbolic procedure hexfloat1 w1;
% hexfloat may be useful from symbolic mode
  if floatp w1 then
    begin
      scalar w, s, x, m1, m2, m3, m4, n;
      if w1 = 0.0 then return "0.0";
% The test that follows appears to behave OK on at least CSL and PSL on
% some Linux systems...
      if not eqn(w1, w1) then return "NaN"
      else if w1 = 0.5*w1 then <<
         if w1 > 0.0 then return "inf"
         else if w1 < 0.0 then return "-inf"
         else return "NaN" >>;
      if w1 < 0.0 then << s := t; w1 := -w1 >>;
      x := 0;
      n := 0;
      while w1 < 0.5 and n < 5000 do << w1 := 2.0*w1; x := x-1; n := n + 1 >>;
      while w1 >= 1.0 and n < 5000 do << w1 := w1/2.0; x := x+1; n := n + 1 >>;
      if n >= 5000 then return "hexfloat failed";
      w1 := w1 * 32.0;
      m1 := fix w1;
      w1 := w1 - float m1;
      w1 := w1 * 65536.0;
      m2 := fix w1;
      w1 := w1 - float m2;
      w1 := w1 * 65536.0;
      m3 := fix w1;
      w1 := w1 - float m3;
      w1 := w1 * 65536.0;
      m4 := fix w1;
      w1 := w1 - float m4;
      if not zerop w1 then error(1, "Floating point oddity in hexfloat");
% I should now have 5+16+16+16=53 bits of mantissa;
      m1 := explodehex m1;
      m2 := cdr explodehex (m2 + 65536);
      m3 := cdr explodehex (m3 + 65536);
      m4 := cdr explodehex (m4 + 65536);
      w := '!B . explode x;
      w := append(m4, '!_ . w);
      w := append(m3, '!_ . w);
      w := append(m2, '!_ . w);
      w := append(m1, '!_ . w);
      if s then w := '!- . w;
      return list!-to!-string w
    end
  else if atom w1 then w1
  else hexfloat1 car w1 . hexfloat1 cdr w1;

symbolic procedure hexfloat u;
% hexfloat tries to be a little generous about its args since it will
% be used in debugging context - but it is inteded to be given a rounded
% valoue and it returns a string...
  begin
    scalar w, w1;
    if numberp u then return hexfloat1 u
    else if atom u then return u
    else <<
      w := aeval car u;
      w1 := prepsq simp w >>;
    if eqcar(w1, '!:rd!:) and floatp cdr w1 then return hexfloat1 cdr w1
    else return w;
  end;

put('hexfloat, 'psopfn, 'hexfloat);

endmodule;

end;
