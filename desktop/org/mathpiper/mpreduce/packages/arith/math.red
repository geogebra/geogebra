module math;  % Mathematical Package for REDUCE.

% Author: Stanley L. Kameny <valley!stan@rand.org>,
% and Arthur C. Norman.

% Modifications by: John Abbott.

% Version and Date:  Mod 1.63, 23 June 1993.

% Copyright (c) 1987, 1988, 1989, 1990, 1991, 1993 Stanley L. Kameny.
% All Rights Reserved.

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


%*******************************************************************
%**                                                               **
%**  This math package will compute the floating point values of  **
%**  the usual elementary functions, namely:                      **
%**     sin     asin     sind    asind     sinh    asinh          **
%**     cos     acos     cosd    acosd     cosh    acosh          **
%**     tan     atan     tand    atand     tanh    atanh          **
%**     cot     acot     cotd    acotd     coth    acoth          **
%**     sec     asec     secd    asecd     sech    asech          **
%**     csc     acsc     cscd    acscd     csch    acsch          **
%**             atan2            atan2d                           **
%**     exp     ln       sqrt    factorial                        **
%**     expt    log      cbrt                                     **
%**     logb    hypot                                             **
%**     log10   floor                                             **
%**             ceiling                                           **
%**             round                                             **
%**                                                               **
%**  All functions are computed to the accuracy of the floating-  **
%**  point precision of the system set up at the time.            **
%**                                                               **
%*******************************************************************

% Revisions:

%     1 May 93  expt improved; fix2 eliminated (not needed).
%    15 Sep 92  expt, hypot, log improved. factorial added.
%    25 May 91  atan2d added.  Function list updated.
%     6 Feb 90  exp, expt, and hyperbolic functions improved.
%     6 Nov 90  find!!nfpd improved; fl2int eliminated (now in bfauxil.)
%    31 Mar 90  fl2int speeded up for very large argument.
%    25 Feb 90  expt modified.
%    15 Oct 89  atan2 and sind,asind family added.
%     8 Oct 89  !!scalsintan,sqrt,expt,and hyperbolics modified.
%     8 Oct 89  hypot,floor,ceiling,round added
%    26 Aug 89  zerop test used in expt
%    20 Jul 89  !!scalsintan revised: same limits for deg and rad
%    17 May 89  find!!nfpd revised (for binary or decimal floats)
%    18 Apr 89  global log10 -> logten (to avoid name conflict)
%    18 Apr 89  !!scalsintan revised (per: Rainer Schoepf)
%    27 Nov 88  log,expt,sqrt revised for speed
%     5 Jun 88  log -> log10; ln -> log; exptfp -> expt (revised)
%    11 Nov 87  hyperbolic fns rewritten: speeded up; improved
%     6 Nov 87  ln,atan rewritten: speeded up. sq!-1  eliminated
%    30 Oct 87  sin,(cos),tan,(cot),exp  rewritten; speeded up

%*******************************************************************
%**                       Basic functions                         **
%*******************************************************************

exports acos, acosd, acosh, acot, acotd, acoth, acsc, acscd, acsch,
        asec, asecd, asech, asin, asind, asinh, atan, atan2, atan2d,
        atand, atanh, cbrt, ceiling, cos, cosd, cosh, cot, cotd, coth,
        csc, cscd, csch, exp, factorial, floor, hypot, log, log10,
        logb, sec, secd, sech, sin, sind, sinh, sqrt, tan, tand, tanh,
        terrlst;

imports !!shbinflp, fl2int, geq, leq, neq, rerror, sgn;

global '(math!!label);
math!!label := "Math package mod 1.7, 1 May 93";

symbolic procedure terrlst (x,y); error(0,list(x," invalid for ",y));

global '(!!nfpd !!flint !!epsqrt !!flprec !!floatbits);

if not !!nfpd then flag('(!!nfpd),'share);

symbolic procedure sqrt x;
 % Computes sqrt x by Newton's method, avoiding magnitude problems.
   if x<0 then terrlst(x,'sqrt) else
   begin  scalar trv,nx,g,l,o,c1,c2,f1; f1 := nx := o := 1.0;
          if (x := float x)=0.0 or x=o then return x;
          if x<o then <<x := o/x; l := t>>;
          c1 := 8192.0; c2 := c1*c1;
          while x>c2 do <<x := x/c2; f1 := f1*c1>>;
    loop: trv := nx; nx := (trv + x/trv)/2;
          if g and nx>=trv then go to ret;
          g := t; go to loop;
     ret: nx := nx*f1; return if l then o/nx else nx end;

symbolic procedure cbrt x;
   begin scalar s,l,o,g,trv,nx,c1,c2,f1; f1 := nx := o := 1.0;
         if (x := float x)=0.0 or abs x=o then return x
         else if x<0 then x := -x else s := t;
         if x<o then <<x := o/x; l := t>> else if x=o then go to ret;
         c1 := 1024.0; c2 := c1*c1*c1;
         while x>c2 do <<x := x/c2; f1 := f1*c1>>;
   loop: trv := nx; nx := trv/1.5+x/(trv*trv*3);
         if g and nx>=trv then go to ret;
         g := t; go to loop;
    ret: nx := nx*f1; if l then nx := o/nx;
         return if s then nx else -nx end;

symbolic procedure hypot(p,q);
   % Hypot(p,q)=sqrt(p*p+q*q) but avoids intermediate overflow.
   begin scalar r;
         if (p := float p)<0 then p := -p;
         if (q := float q)<0 then q := -q;
         if zerop p then return q
         else if zerop q then return p
         else if p<q then <<r := p; p := q; q := r>>;
         if p+q=p then return p else r := q/p;
         return if r<!!epsqrt then p else p*sqrt(1+r*r) end;

symbolic procedure floor x;
   % Returns the largest integer less than or equal to x
   % (i.e. the "greatest integer" function.)
   % Note the trickiness to compensate for fact that (unlike APL's
   % "FLOOR" function) FIX truncates towards zero.
   % A definition of fix(x+sgn(x)*0.5) has also been suggested.
   if fixp x then x
    else (if x = float n then n else if x >= 0 then n else n - 1)
       where n = fix x;

symbolic procedure ceiling x;
   % Returns the smallest integer greater than or equal to X.
   % Note the trickiness to compensate for fact that (unlike APL's
   % "FLOOR" function) FIX truncates towards zero.
   if fixp x then x
    else (if x = float n then n else if x >= 0 then n+1 else n)
       where n = fix x;

symbolic procedure round x;
   % Rounds to the closest integer.
   % Kind of sloppy -- it's biased when the digit causing rounding is a
   % five.  (Changed to work properly for X<0.  SLK)
   if fixp x then x else if x<0 then -round(-x) else floor(x+0.5);

symbolic procedure rounddec (x,p);
   % Rounds x to p decimal places, unless x must already be an integer.
   if abs x>=!!flint then x
    else  begin scalar xl,xr,sc;
            sc := 10.0**p; xl := fix(x := x*sc); xr := x - xl;
            if x>0 and xr>=0.5 then xl := xl+1;
            if x<0 and xr<-0.5 then xl := xl - 1;
            return xl/sc
          end;

global '(log2 sq2 sq2!-1 logsq2 logten log1000 log1e9);
global '(log1e81 log1e27);

sq2 := sqrt 2.0; sq2!-1 := 1/(1+sq2);

symbolic procedure log x;
   begin scalar s,lx; integer p;
      if fixp(x) and (lx := ilog2(x)) > !!floatbits
        then return log2*(lx - !!floatbits)
                      + log(x/2^(lx - !!floatbits))
       else if (x := float x)<=0.0 then terrlst(x,'log)
       else if x - 1<0 then x := 1/x else s := t;
      lx := 0.0;
      while x>1.0e81 do <<x := x/1.0e81; lx := lx+log1e81>>;
      while x>1.0e27 do <<x := x/1.0e27; lx := lx+log1e27>>;
      while x>1.0e9 do <<x := x/1.0e9; lx := lx+log1e9>>;
      while x>1000 do <<x := x/1000; lx := lx+log1000>>;
      while x>10 do <<x := x/10; lx := lx+logten>>;
      while x>2 do <<x := x/2; lx := lx+log2>>;
      if x>sq2 then <<x := x/sq2; lx := lx+logsq2>>;
      lx := lx+sclogx!-1(x - 1);
      return if s then lx else -lx end;

symbolic procedure sclogx!-1 x;
   begin scalar tx,px,lx,st,sl; integer p;
         tx := px := x; p := 1; lx := 0.0;
         st := x*(1 - x/2);
         while st+abs tx>st do
            <<sl := tx . sl; tx := (px:=-px*x)/(p := p+1)>>;
         for each i in sl do lx := lx+i;
         return lx end;

log2 := 2*(logsq2 := sclogx!-1 sq2!-1);
log1e81 := 3*(log1e27 := 3*(log1e9 := 3*(log1000 :=
  3*(logten := log 10.0))));

global '(!!pilist);

global(!!pilist := '(!!pii !!pii2 !!pii3 !!pii4 !!pii6 !!twopi
                     !!rad2deg !!deg2rad));

remflag(!!pilist,'reserved);

symbolic procedure atan x;
   begin scalar arg,term,termp,trv,s,g,y;
      integer p;
      if (x := float x)< 0 then x := -x
       else s := t;
      if x > 1 then x:=1/x else g:=t;
      if x < !!epsqrt then go to quad;
      term := if x<0.43 then (arg := x)
         else (arg := x/(1+sqrt(1+x*x)))*2;
      arg := arg*arg; p := 1; trv := (termp := term)*(1 - arg/3);
      while trv+abs termp >trv do
        <<y := termp . y; termp := (term := -term*arg)/(p := p+2)>>;
      x := 0;
      for each i in y do x := x+i;
quad: if not g then x := !!pii2 - x;
      if not s then x := -x;
      return x end;

symbolic procedure atand x; !!rad2deg * atan x;

!!twopi := 2*(!!pii := 2*(!!pii2 := 2*(!!pii4:=atan 1.0)));
!!pii3 := 2*(!!pii6 := !!pii2/3);
!!deg2rad:=!!pii4/45; !!rad2deg:=45/!!pii4;

flag(!!pilist,'reserved);

fluid '(!*ddf!* !*df!* !*sf!* !*qf!*);

symbolic procedure sin x;
  begin scalar !*sf!*,!*qf!*;integer p;
   % test for 90 deg -> 1.0
      x := !!scalsintan(x,t);
      if !*qf!* then <<x := 1.0; go to ret>>;
   % for x>45, compute cos of complement, else compute sin.
      if x>!!pii4 then x := !!pii2 - x else p := 1;
      x := !!sints(x,p);
   ret: return if !*sf!* then x else -x end;

symbolic procedure sind x;
  begin scalar !*sf!*,!*qf!*;integer p;
   % test for 90 deg -> 1.0
      x := !!scalsintand(x,t);
      if !*qf!* then <<x := 1.0; go to ret>>;
   % for x>45, compute cos of complement, else compute sin.
      if x>45.0 then x := 90.0 - x else p := 1;
      x := !!sints(x*!!deg2rad,p);
   ret: return if !*sf!* then x else -x end;

symbolic procedure tan x;
   begin scalar y,inv,!*sf!*,!*qf!*;
      y:=x; x:= !!scalsintan(x,nil);
      if !*qf!* then terrlst(y,'tan);
      if x>!!pii4 then x := !!pii2 - x else inv := t;
 % For scaled x>45, compute cot else compute tan.
      if x>!!epsqrt then <<x := !!sints(x,1); x := x/sqrt(1 - x*x)>>;
      if not inv then x := 1/x;
      return if !*sf!* then -x else x end;

symbolic procedure tand x;
   begin scalar y,inv,!*sf!*,!*qf!*;
      y:=x; x:= !!scalsintand(x,nil);
      if !*qf!* then terrlst(y,'tand);
      if x>45.0 then x := 90.0 - x else inv := t;
 % For scaled x>45, compute cot else compute tan.
      x := x*!!deg2rad;
      if x>!!epsqrt then <<x := !!sints(x,1); x := x/sqrt(1 - x*x)>>;
      if not inv then x := 1.0/x;
      return if !*sf!* then -x else x end;

global '(max!-trig!-fact); max!-trig!-fact := 10**(!!nfpd/2);

fluid '(!:prec!:);

symbolic procedure !!scalsintan(x,w);
% x is scaled to 0<=x<=90 deg, with !*sf!* = {sin>0 or tan<0}.
% w true for sin, false for tan.
%modified to avoid infinite loop for large x, after Rainer Schoepf's
%suggestion, adjusted so degree and radian input agrees- SLK.
   begin scalar xf,x0;
      if x<0 then x := -x else !*sf!* := t; x0 := x;
      if (xf := fix(x/!!twopi))>max!-trig!-fact then
         terrlst(if !*sf!* then x else -x,if w then 'sin else 'tan);
      x := x - float xf * !!twopi;
      if x>!!pii then (if w then
           <<x := !!twopi - x; !*sf!* := not !*sf!*>>
       else x := x - !!pii);
      if x>!!pii2 then x:=!!pii - x
       else if not w then !*sf!*:=not !*sf!*;
      !*qf!* := x>=!!pii2;
 % the remaining tests and scaling are done separately by sin and tan
      if x<x0/10.0**(!:prec!: - 3) then x := 0.0;
      return x end;

symbolic procedure !!scalsintand(x,w);
% x is scaled to 0<=x<=90 deg, with !*sf!* = {sin>0 or tan<0}.
% w true for sin, false for tan.
%modified to avoid infinite loop for large x, after Rainer Schoepf's
%suggestion, adjusted so degree and radian input agrees- SLK.
   begin scalar xf,x0;
      if x<0 then x := -x else !*sf!* := t; x0 := x;
      if (xf := fix(x/360.0))>max!-trig!-fact then
         terrlst(if !*sf!* then x else -x,if w then 'sin else 'tan);
      x := x - float xf * 360.0;
      if x>180.0 then (if w then
           <<x := 360.0 - x; !*sf!* := not !*sf!*>> else x:= x - 180.0);
      if x>90.0 then x:=180.0 - x else if not w then !*sf!*:=not !*sf!*;
      !*qf!* := x>=90.0;
 % the remaining tests and scaling are done separately by sin and tan
      if x<x0/10.0**(!:prec!: - 3) then x := 0.0;
      return x end;

symbolic procedure !!sints (x,p);
 % Does the actual computation of the sin or cos series.
   begin scalar sl,sq,st,term;
        x := float x;
        if x<!!epsqrt then return if p=1 then x else 1.0;
        sq := x*x;
        st := if p=1 then (term:=x)*(1 - sq/6)
               else (term:=1.0)*(1 - sq/2);
        while st + abs term > st do
           <<sl:=term . sl;
             p:=p+2.0;
             term:=-term*sq/((p - 1.0)*p) >>;
        x:=0.0;
        for each i in sl do x:=x+i;
        return x end;

symbolic procedure !!sinhts x;
 % Does the actual computation of the sinh for 0<x<0.91.
   begin scalar p,sl,sq,st,term;
        if x<2*!!epsqrt then return x;
        st:= (term := x)*(1 - (sq := x*x)/6); p := 1;
        while st + term > st do
           <<sl:=term . sl;
             p:=p+2.0;
             term:=term*sq/((p - 1.0)*p) >>;
        x := 0.0;
        for each i in sl do x := x+i;
        return x end;

global '(!!ee);

symbolic procedure exp v;
   begin scalar d,nr,mr,fr,st;integer p,ip;
         mr := fr := 1.0; v := float v;
         if abs v>1 then <<ip := fix v; v := v - ip; fr := !!ee**ip>>;
         if abs v>0.5 then v := v/2 else d := t;
         if v=0.0 then go to ret;
         st := mr+v;
         while st+abs mr > st do
               <<nr := mr . nr; mr := mr*v/(p := p+1)>>;
         mr := 0.0;
         for each i in nr do mr := mr+i;
    ret: if not d then mr := mr*mr;
         return fr*mr end;

remflag('(!!ee),'reserved); !!ee := exp 1.0; flag('(!!ee),'reserved);

put('expt,'number!-of!-args,2);

% NOTE that any Lisp system with a very good implementation of EXPT is
% entitled to replace the following definition of EXPT with it, but they
% should also arrange that fexpt gets redirected to the same good
% built-in function.

symbolic procedure iexpt(x,n);
   % Calculate x**n where n is a strictly positive integer.  This uses
   % repeated squaring.  It is appropriate for use when x is an integer,
   % and can be used for floating x provided that n is not too large.
   % John Abbott reported some slow calculations.  He added: The
   % problem is the line containing "lshift".  I tried replacing
   % remainder(x,2)=0 with evenp(x), and that made it go about twice as
   % fast.  Then I removed the line altogether, and the problem went
   % away.  I think that line would be useful only if n is quite large
   % and x is divisible by a moderately high power of 2.
   if not (n > 0) then error(0, "iexpt argument <= 0")
    else if n=1 or x=1 then x
%   else if fixp x and remainder(x,2)=0 then lshift(iexpt(x/2,n),n)
    else if remainder(n,2)=0 then (y*y) where y=iexpt(x,n/2)
%   else if evenp n then (y*y) where y=iexpt(x,n/2)
    else (x*y*y) where y=iexpt(x,(n - 1)/2) ;

symbolic procedure expt(x,y);
   % Computes x**y.  Valid for any x provided that y is an integer,
   % but only for positive x if y is floating.
   begin integer iy,p; scalar sy,fy,r;
      % Some of the initial tests here are subsumed by those in rexpt,
      % and could be removed - but I prefer to implement a proper
      % general version of expt, even though doing so adds (slightly)
      % to the cost of using this portable version.  Note that getting
      % accurate answers from expt in a portable way is a real pain,
      % and I will not do a 100% good job here... see Cody and Waite
      % for a discussion of the issues involved.
      if zerop y then
         if zerop x then error(0,"0**0 undefined")
          else return if floatp x or floatp y then 1.0 else 1
       else if zerop x then
         if y>0 then return if floatp x or floatp y then 0.0 else 0
          else error(0,"divide by zero in EXPT")
       else if fixp y then
         return if fixp x then
                  if y < 0 then 0 else if x = 1 then 1 else iexpt(x,y)
      % See comments with the function FEXPT for an explanation of the
      % tests here - I deem exponents of less than 50 to be small
      % enough to handle the simple (and cheap) way.
                   else if y > 50 then fexpt(x, y)
                   else if y > 0 then iexpt(x, y)
                   else if y < -50 then 1.0/fexpt(x,-y)
                 else 1.0/iexpt(x,-y);
      % Since y is floating, float x if fixed.
      if fixp x then x := float x;
      if x<0.0 then
        error(0,"attempt to raise negative value to floating power");
      % Record the sign of y, but do not invert x yet, since it is
      % important not to corrupt the value of x by even one unit in the
      % last place.  Note that this will leave me with a risk that
      % (e.g.) 10.0**(-1000.0) will try to compute 10.0**1000.0 (which
      % will overflow) and only then take its reciprocal, while
      % possible had I inverted x here I would have had a silent
      % arithmetic underflow.  For now I will argue that arithmetic
      % underflow is really an error too and that the exception
      % deserved to be raised.
      if y < 0.0 then <<sy := t; y := -y>>;
      % Still use multiplication if y has integral value.
      iy := fix y;
      fy := float iy;
      if y = fy then <<
         if iy > 50 then x := fexpt(x, iy)
          else x := iexpt(x, iy);
         if sy then return 1.0/x
          else return x>>;
      % For x fairly close to 1.0 and smallish values of y I can use
      % the simple formula with exp and log, and I will not lose
      % overmuch accuracy.  The limits I apply here are a compromise
      % between wanting to use this cheap recipe as often as possible
      % and the desire to get best possible accuracy.
      if 0.1 < x and x < 10.0 and y < 5.0
        then <<if sy then y := -y; return exp(y*log x)>>;
      % Now scale x as 2^p * something
      p := 0;
      while x < 0.005524 do <<x := x*256.0; p := p - 8>>;
      while x < 0.707106781 do <<x := x*2.0; p := p - 1>>;
      while x >= 181.02 do <<x := x/256.0; p := p+8>>;
      while x >= 1.414213562 do <<x := x/2.0; p := p+1>>;
      % Now x is in the range 0.707 <= x < 1.414, so log x is fairly
      % small.  I can compute x**iy my multiplication, x**(y - iy) by
      % logs, and that just leaves 2**(y*p) to worry about.
      if (y - fy) > 0.5 then <<fy := fy+1.0; iy := iy+1>>;
      r := exp((y - fy)*log x);
      if iy > 50 then r := r*fexpt(x, iy)
       else if iy > 0 then r := r*iexpt(x, iy);
      y := p*y;
      iy := fix y;
      fy := y - float iy; % fractional part of y.
      % Now I need to compute 2**iy * 2**fy.
      r := r * exp(fy*log2);
      % I can afford to use iexpt() here since powers of 2.0 have exact
      % representations as floats (with binary machines!) so there
      % should be no rounding errors in what follows.
      if iy > 0 then r := r*iexpt(2.0, iy)
       else if iy < 0 then r := r*iexpt(0.5, -iy);
      if sy then r := 1.0/r;
      return r
   end;

Comment
   Consider the calculation z = 1.01 ** 16384.  I have chosen the
   exponent to be a power of 2 for simplicity of explanation, but other
   values will suffer the same way.  The value of z will be computed as
   (1.01*1.01) raised to the power 8192.  The multiplication 1.01*1.01
   will introduce an error of about e = 1/2 unit in the last place
   (around 1.0e-16 perhaps).  If all calculations after that very first
   multiplication are then performed exactly, the final result (6.3e70
   or so) will have a relative error of around 8000 units in the last
   place.  To avoid this sort of trouble it is necessary to use extra
   precision in the multiplications - something that slows us down but
   which is needed.  I only use this expensive code if I am going to
   raise a float to a power greater than 50 (a rather arbitrary
   cut-off) so that speed of calculation involving small powers is not
   hurt too badly;

symbolic procedure fsplit x;
   % This decomposes a floating point value x into two parts x1 and x2
   % such that x = x1+x2 and x1 is a number with at most 12 significant
   % bits in its mantissa.  I choose to keep 12 bits here since I then
   % expect (i.e., REQUIRE) that products of pairs of such numbers get
   % formed without any rounding at all.  This should be so even on
   % IEEE single precision arithmetic (25 bits of mantissa).  For IBM
   % mainframe single precision even more effort would be needed.
   begin scalar xx, n;
      if x = 0.0 then return (0.0 . 0.0);
      xx := x; n := 1.0;
      if x < 0.0 then xx := -xx;
      while xx < 8.0 do << xx := xx*256.0; n := n*256.0>>;
      while xx < 2048.0 do << xx := xx*2.0; n := n*2.0>>;
      while xx >= 4096.0 do << xx := xx*0.5; n := n*0.5>>;
      xx := float fix xx/n;
      if x < 0.0 then xx := -xx;
      return (xx . (x - xx))
   end;

symbolic procedure f_multiply(a, b);
   % a and b are split-up floating point values as generated by fsplit.
   % Multiply them together and return the result as an fsplit-num.
   begin scalar h, l;
      h := fsplit(car a*car b);
      l := cdr h + car a*cdr b + (car b + cdr b)*cdr a;
      return (car h . l)
   end;

symbolic procedure fexpt(x, n);
   % Like iexpt, this raises x to the (positive integer) power n.  But
   % it uses fplit-num arithmetic to get about 12 bits of extra
   % precision in the calculation, which should preserve reasonable
   % accuracy until n gets to be much bigger than 5000.
   begin scalar w;
      w := fexpt1(fsplit x, n);
      return car w + cdr w
   end;

symbolic procedure fexpt1(x, n);
   % Calculate x**n where n is a strictly positive integer, using extra
   % precision arithmetic.
   if not (n > 0) then error(0, "fexpt1 argument <= 0")
    else if n = 1 then x
    else if remainder(n, 2) = 0 then fexpt1(f_multiply(x, x), n/2)
    else f_multiply(x, fexpt1(f_multiply(x, x), (n - 1)/2));

symbolic procedure rexpt(x,y);
   % Computes x**y in for argument sets that yield real values.  In
   % particular if x is negative but y is a floating point value that
   % is sufficiently close to a rational number then a real result will
   % be computed, where the system-level expt function might have
   % reported an error.  This also picks up various marginal or error
   % cases (e.g. 0**0) so that their treatment is precisely defined in
   % REDUCE.
   begin scalar s,q; integer p;
      if zerop y then
         if zerop x then error(0,"0**0 undefined")
         else return if floatp x or floatp y then 1.0 else 1
      else if zerop x then
         if y>0 then return if floatp x or floatp y then 0.0 else 0
         else error(0,"divide by zero in EXPT")
      else if fixp y then <<
         if fixp x then <<
            if y<0 then return 0
            else if x = 1 then return 1
            else return iexpt(x, y) >>;
      % Floating numbers raised to integer powers are still pretty
      % painful.  If the base is negative then the sign of the result
      % depends on whether the power was odd or even.  For large
      % exponents I use fexpt() for extra accuracy (but at significant
      % extra cost).
         s := 0;
         if y < 0 then <<s:=1; y := -y>>;
         if x < 0 then <<s:=s+2; x := -x>>;
         if y > 50 then x := fexpt(x, y) else x := iexpt(x, y);
         if s=1 or s=3 then x := 1.0/x;
         if s>1 and remainder(y,2) neq 0 then x := -x;
         return x>>;
      % Since y is floating, float x if fixed.
      if fixp x then x := float x;
      % Invert here if exponent is negative float.
      if y<0.0 then <<x := 1.0/x; y := -y>>;
      % Still use integer exponentiation if y has integral value.
      if zerop(y - (p := fix y)) then return iexpt(x, p);
      % If y=0.5 use sqrt function, which may be easier
      if y = 0.5 then return sqrt x;
      % If x < 0 then x**y only yields a real result if y is a rational
      % number.  We already know that y is not an integer, so call
      % ft2rn1 to see if a good rational approximation to y exists.  A
      % previous version of this code called ft2rn1 for all floating
      % point values of y and then used combination sof sqrt/cbrt to
      % evaluate x**y in some cases.  This version bets that the cost
      % of ft2rn1 would exceed the savings of using sqrt, and so only
      % does the expensive thing when x < 0 and thus when there might
      % otherwise have been an error.
      if x<0.0 then <<
         q := ft2rn1 y; p := car q; q := cdr q;
         x := -x;
         if not(abs p<10 or q<10
             or 2*max(length explode q,length explode p) < !!flprec+1)
             or remainder(q,2)=0
               then error (0,list (-x,"**",y," not real"))
           else if remainder(p,2)=1 then s := t >>;
      if y = 0.5 then x := sqrt x  % sqrt safer if applicable?
      else x := expt(x, y);        % Use the lower level expt function
      return (if s then -x else x)
 end;

symbolic procedure ft2rn1 n;
  if n < 0.0 then ((-car r) . cdr r) where r = ft2rn2(-n)
   else ft2rn2 n;

symbolic procedure ft2rn2 n;
  % Here, the positive input n is a float.
   begin scalar a,p0,p1,q0,q1,w,nn,r0,r1,flpr;
      flpr := abs n*100.0/!!flint;
      a := fix n;
      nn := n - a;
      p0 := 1; p1 := a; q0 := 0; q1 := 1;
      r0 := n + 1.0;
 top: r1 := abs(n - float p1/float q1);
      if nn=0.0 or r1=0.0 or not (r1 > flpr) then return p1 . q1
       else if not (r1 < r0) then return p0 . q0;
      nn := 1.0/nn;
      a := fix nn;
      nn := nn - a;
      w := p0 + a*p1; p0 := p1; p1 := w;
      w := q0 + a*q1; q0 := q1; q1 := w;
      r0 := r1;
      go to top
  end;


%**********************************************************************
%**             Functions derived from basic functions               **
%**********************************************************************

symbolic procedure cos x; sin(!!pii2 - x);

symbolic procedure cot x; tan(!!pii2 - x);

symbolic procedure sec x; 1.0/cos x;

symbolic procedure csc x; 1.0/sin x;

symbolic procedure acot x; !!pii2 - atan x;

symbolic procedure asin x;
   if abs x<1 then
      atan(if abs x<!!epsqrt then x else x/sqrt(1 - x*x))
   else if abs x>1 then terrlst (x,'asin)
   else if x>0 then !!pii2 else -!!pii2;

symbolic procedure acos x; !!pii2 - asin x;

symbolic procedure acsc x;
   if abs x>=1 then asin(1.0/x) else terrlst(x,'acsc);

symbolic procedure asec x;
   if abs x<1 then terrlst(x,'asec) else !!pii2 - asin(1.0/x);

symbolic procedure cosd x; sind(90.0 - x);

symbolic procedure cotd x; tand(90.0 - x);

symbolic procedure secd x; 1/cosd x;

symbolic procedure cscd x; 1/sind x;

symbolic procedure acotd x; 90.0 - atand x;

symbolic procedure asind x; !!rad2deg * asin x;

symbolic procedure acosd x; 90.0 - asind x;

symbolic procedure acscd x;
   if abs x>=1 then asind(1.0/x) else terrlst(x,'acscd);

symbolic procedure asecd x;
   if abs x<1 then terrlst(x,'asecd) else 90.0 - asind(1.0/x);

symbolic procedure sinh x;
   begin scalar s;
      if x<0.0 then x:=-x else s:=t;
      if (x := float x)<0.91 then <<x := !!sinhts x; go to ret>>;
      x := exp(-x); x := (1.0/x - x)/2;
 ret: return if s then x else -x end;

symbolic procedure cosh x; <<x := exp(-abs x),(x+1.0/x)/2>>;

symbolic procedure tanh x;
   if x<0.0 then -tanh(-x) else
      <<x := exp(-2.0*x); (1.0 - x)/(1.0+x)>>;

symbolic procedure coth x;
   if x<0.0 then -coth(-x) else
      <<x := exp(-2.0*x); (1.0+x)/(1.0 - x)>>;

symbolic procedure asinh x; begin scalar s;
   if x<0 then x:=-x else s:=t;
   x:=if x<!!epsqrt then x else log (x+if x<2 then sqrt(x*x+1)
           else if 1/x<!!epsqrt then x else x*sqrt(1+1/(x*x)));
   return if s then x else -x end;

symbolic procedure acosh x; if x<1 then terrlst(x,'acosh)
   else log (x+if 1/x<!!epsqrt then x else x*sqrt(1 - 1/(x*x)));

symbolic procedure atanh x; if abs x>=1 then terrlst(x,'atanh)
   else if abs x<!!epsqrt then x else 0.5*log((1+x)/(1 - x));

symbolic procedure acoth x;
   if abs x<=1 then terrlst(x,'acoth) else atanh (1.0/x);

symbolic procedure sech x;1/cosh x;

symbolic procedure csch x;1/sinh x;

symbolic procedure asech x;
   if x<=0 or x>1 then terrlst(x,'asech) else acosh (1.0/x);

symbolic procedure acsch x;
   if (x:= float x)=0.0 then terrlst(x,'acsch) else asinh(1/x);

symbolic procedure ln x; log x;

symbolic procedure log10 x;
   if x>0 then log x/logten else terrlst(x,'logten);

symbolic procedure logb (x,b); %log x to base b;
   begin scalar a,s; a:=x>0; s:=not(b<=0 or zerop(b - 1));
         if a and s then return log x/log b
         else terrlst((if a then list ('base,b)
            else if s then list('arg,x) else list(x,b)),'logb) end;

symbolic procedure atan2(y,x);
   if zerop x then !!pii2*sgn y else
   <<(if x>0 then a else if y<0 then a - !!pii else a+!!pii)
     where a=atan(y/x)>>;

symbolic procedure atan2d(y,x);
   if zerop x then 90.0*sgn y else
   <<(if x>0 then a else if y<0 then a - 180.0 else a+180.0)
     where a=!!rad2deg*atan(y/x)>>;

% A numerical factorial function.

symbolic procedure factorial n;
   if not fixp n or n<0
     then rerror(arith,4,list(n,"invalid factorial argument"))
    else nfactorial n;

symbolic procedure nfactorial n;
   % Numerical factorial function.  It is assumed that n is numerical
   % and non-negative.
   if n>20 then fac!-part(1,n)
    else begin scalar m;
       m:=1;
       for i:=1:n do m:=m*i;
       return m;
     end;

symbolic procedure fac!-part (m,n);
    if m=n then m
     else if m=n - 1 then m*n
     else (fac!-part(m,p)*fac!-part(p+1,n)) where p=(m+n)/2;

endmodule;

end;
