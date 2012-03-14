module bfauxil; % Support for the roots package and ROUNDED domain.

% Author: Stanley L. Kameny <valley!stan@rand.org>.
% Definitions of ilog2, irootn, icbrt, isqrt and support supplied by
% John Abbott.

% Copyright (c) 1988,1989,1990. Stanley L. Kameny. All Rights Reserved.

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


Comment   support for modules allroot and isoroot, and for ROUNDED
 domain logic;

exports !!shbinflp, bf2flr, bfdiffer, bfdivide, bfinverse, bflessp,
        bfminus, bfsqrt, cflot, conv!:bf2i, difbf, exptbf, fl2int,
        gf2bf, gf2fl, gfdiffer, gfdot, gfminus, gfplus, gfquotient,
        gfsqrt, gftimes, grpbf, icbrt, ilog2, invbf, irootn, isqrt,
        normbf, plubf, r2bf, r2fl, realrat;

imports abs!:, ashift, bflerrmsg, bfloat, bfminusp, bfnzp, bfp!:,
        bfzerop!:, bfzp, conv!:mt, cut!:ep, decprec!:, difference!:,
        divbf, divide!:, ep!:, eqcar, error1, errorp, errorset!*,
        evenp, fl2bf, gcdn, geq, gfim, gfrl, gfzerop, greaterp!:,
        hypot, i2bf!:, leq, lshift, make!:ibf, minus!:, minusp!:,
        msd!:, mt!:, order!:, plus!:, preci!:, read!:num, rndpwr,
        round!:mt, sgn, sqrt, terrlst, timbf, times!:, typerr;

fluid '(!:prec!: !:bprec!:);

global '(bfone!* bfhalf!* bfz!*);

global '(!!nfpd !!nbfpd !!shbinfl vv!! !!flbint);


global '(!!minflbf !!maxflbf);

symbolic procedure normbf x;
   begin scalar mt,s,r;integer ep,ep1;
      if (mt := mt!: x)=0 then go to ret;
      if mt<0 then <<mt := -mt; s := t>>;
      ep := ep!: x;
      while remainder(mt,1073741824)=0 do << % 2**30
        mt := lshift(mt,-30);
        ep := ep+30 >>;
      while remainder(mt,256)=0 do <<
        mt := lshift(mt,-8);
        ep := ep+8 >>;
      while not oddintp mt do <<
        mt := lshift(mt,-1);
        ep := ep+1>>;
      if s then mt := -mt;
ret:    return make!:ibf(mt,ep) end;

%symbolic procedure divbf(u,v); normbf divide!:(u,v,!:bprec!:);

symbolic procedure bfdivide(u,v);
   if atom u then u/v else divbf(u,v);

%symbolic procedure timbf(u,v); rndpwr times!:(u,v);

symbolic procedure bftimes(u,v); if atom u then u*v else timbf(u,v);

symbolic procedure plubf(a,b);
% this function calculates the normalized rounded sum of a and b,
% but avoids generating large numbers if magnitude difference is large.
  rndpwr
    begin scalar ma,mb,ea,eb,d,ld,p;
      if (ma:=mt!: a)=0 then return b;
      if (mb:=mt!: b)=0 then return a;
      if (d := (ea := ep!: a)-(eb := ep!: b))=0
        then return make!:ibf(ma+mb,ea);
      ld := d+msd!: abs ma - msd!: abs mb;
      p := !:bprec!:+1;
      if ld>p then return a;
      if ld<-p then return b;
      if d>0 then return make!:ibf(ashift(ma,d)+mb,eb)
        else return make!:ibf(ma+ashift(mb,-d),ea) end;

symbolic procedure bfplus(u,v); if atom u then u+v else plubf(u,v);

symbolic procedure difbf(a,b);
% this function calculates the normalized rounded difference of a and b,
% but avoids generating large numbers if magnitude difference is large.
  rndpwr
    begin scalar ma,mb,ea,eb,d,ld,p;
      if (ma:=mt!: a)=0 then return minus!: b;
      if (mb:=mt!: b)=0 then return a;
      if (d := (ea := ep!: a)-(eb := ep!: b))=0
        then return make!:ibf(ma - mb,ea);
      ld := d+msd!: abs ma - msd!: abs mb;
      p := !:bprec!:+1;
      if ld>p then return a;
      if ld<-p then return minus!: b;
      if d>0 then return make!:ibf(ashift(ma,d) - mb,eb)
        else return make!:ibf(ma - ashift(mb,-d),ea) end;

symbolic procedure bfdiffer(u,v); if atom u then u - v else difbf(u,v);

symbolic procedure invbf u; divbf(bfone!*,u);

symbolic procedure bfinverse u; if atom u then 1.0/u else invbf u;

symbolic procedure bfminus u; if atom u then -u else minus!: u;

symbolic procedure bflessp(a,b); if atom a then a<b else grpbf(b,a);

symbolic procedure grpbf(a,b);
  % this function calculates a > b, but avoids generating large numbers
  % if magnitude difference is large.
   <<if ma=0 then mb<0
     else if mb=0 then ma>0
     else if ma>0 and mb<0 then t
     else if ma<0 and mb>0 then nil
     else (if do>0 then ma>0           % the case |a| > |b|
            else if do<0 then ma<0     % the case |a| < |b|
            else if de=0 then ma>mb    % exponents are the same
            else if de>0 then ashift(ma,de)>mb
            else ma>ashift(mb,-de))
          where do=order!: a - order!: b,
                de=ep!: a - ep!: b>>
    where ma=mt!: a,mb=mt!: b;

%symbolic procedure bfminusp u; if atom u then minusp u else minusp!: u;

%symbolic procedure bfzp u; if atom u then zerop u else mt!: u=0;

%symbolic procedure bf!:zerop u; if atom u then zerop u else mt!: u=0;

%symbolic procedure bfnzp u; not bfzp u;

%symbolic procedure bfloat x; if floatp x then fl2bf x else
%normbf(if atom x then if fixp x then i2bf!: x else read!:num x else x);

symbolic procedure !!shbinflp;
   begin integer n; vv!! := 9.0;
     while n<300 and not errorp errorset!*('(vv!!!*1e10),nil)
        do n := n+10;
     return n<300 end;

symbolic procedure vv!!!*1e10; vv!! := vv!!*1.0e10;

symbolic (!!shbinfl := !!shbinflp());

symbolic procedure bfsqrt x;
 % computes sqrt x by Newton's method.
  if minusp!: x then terrlst(x,'bfsqrt) else
  begin  scalar nx,dx,dc,k7,nf;
         if bfzerop!: x then return bfz!*;
         k7 := !:bprec!: + 7;
         dc := make!:ibf (1, (-k7+(order!: x + 10)/2));
         nx := if not oddintp ep!:(nx := conv!:mt(x,2))
            then make!:ibf((2+3*mt!: nx)/5, (ep!: nx/2))
            else make!:ibf((9+5*mt!: nx)/10, ((ep!: nx - 1)/2));
         nf := 1;
   loop: if (nf := 2*nf)>k7 then nf := k7;
         dx := times!:(bfhalf!*,plus!:(divide!:(x,nx,nf),nx));
         if nf>=k7 and not greaterp!:(abs!: difference!:(dx,nx),dc)
            then return rndpwr nx;
         nx := dx; go to loop end;

symbolic procedure realrat x;
   begin scalar d,g;
         if bfp!: x then go to bf;
         if eqcar(x,'quotient) then
            if fixp cadr x and fixp caddr x then
               <<x := if (d := caddr x)<0 then -cadr x else cadr x;
                 d := abs d; go to ret>>
            else x := cadr x/caddr x;
         if zerop x then return (0 . 1);
         if not floatp x then return (x . 1);
         x := bfloat x;
     bf: d := cddr(x := normbf x); x := cadr x;
         if x=0 then return (0 . 1);
         if d< 0 then d := lshift(1,-d)
          else <<x := ashift(x,d); d := 1>>;
    ret: g := gcdn(abs x,d); return (x/g) . (d/g) end;

remflag ('(fl2int),'lose);

symbolic procedure fl2int x;
   <<x := fl2bf x;
     (if d=0 then m else ashift(m,d))
      where m=mt!: x,d=ep!: x>>;

flag ('(fl2int),'lose);

symbolic procedure cflot x;
   if floatp x then x else if atom x then float x else bf2flr x;

symbolic procedure conv!:bf2i nmbr;
% This function converts a <BIG-FLOAT>, i.e., a BINARY BIG-FLOAT
%      representation of "n", to an integer.  The result
%      is the integer part of "n".
% **** For getting the nearest integer to "n", please use
% ****      the combination MT!:( CONV!:EP(NMBR,0)).
% NMBR is a BIG-FLOAT representation of the number "n".
%   if ep!:(nmbr := cut!:ep(nmbr, 0)) = 0 then mt!: nmbr
%    else
     ashift (mt!: nmbr, ep!: nmbr);

symbolic procedure bf2flr u;
   % u is always bigfloat.
   % Converts bfloat to float by rounding at !!nbfpd binary digits.
   % We use error1 rather than rerror, because we want to catch such an
   % error in an errorset.
   begin scalar ep,m,y;
      if bfzerop!: u then return 0.0;
      ep := ep!:(u := round!:mt(u,!!nbfpd));
      if grpbf(!!minflbf,y := abs!: u) or grpbf(y,!!maxflbf)
        then error1();
      if ep<0 then <<ep := ep+!!nbfpd; m := t>>;
      ep := 2.0**ep;
      if ep = 0.0 then error1(); % underflow
      return if not m then ep * mt!: u else ep * mt!: u / !!flbint
   end;

symbolic procedure gf2fl a; % force into float format.
   if atom a then a else if bfp!: a then bf2flr a
      else (gf2fl car a) . gf2fl cdr a;

symbolic procedure gf2bf a; if a then % force into bfloat format.
   if atom a then bfloat a else if bfp!: a then a
      else (gf2bf car a) . gf2bf cdr a;

symbolic procedure r2bf u;
  % translate any real number object to bigfloat.
    if atom u then bfloat u
       else if bfp!: u then u
       else if numberp car u then divbf(i2bf!: car u,i2bf!: cdr u)
       else if eqcar(u,'quotient) then
            divbf(i2bf!: cadr u,i2bf!: caddr u)
       else if eqcar(u,'!:rn!:) then r2bf cdr u
       else r2bf cadr u;

symbolic procedure r2fl u;
 % translate any real number object to float.
   if u=0 then 0.0
      else if atom u then float u
      else if numberp car u then (float car u)/cdr u
      else if eqcar(u,'quotient) then (float cadr u)/caddr u
      else if bfp!: u then bf2flr u
      else if eqcar(u,'!:rn!:) then r2fl cdr u
      else r2fl cadr u;

symbolic procedure gfplus(u,v);
   if atom car u then gffplus(u,v) else gbfplus(u,v);

symbolic procedure gffplus(u,v); (car u+car v) . (cdr u+cdr v);

symbolic procedure gbfplus(u,v);
   (plubf(car u,car v)) . plubf(cdr u,cdr v);

symbolic procedure gfdiffer(u,v);
   if atom car u then gffdiff(u,v) else gbfdiff(u,v);

symbolic procedure gffdiff(u,v); (car u - car v) . (cdr u - cdr v);

symbolic procedure gbfdiff(u,v);
   (difbf(car u,car v)) . difbf(cdr u,cdr v);

symbolic procedure gftimes(u,v);
   if atom car u then gfftimes(u,v) else gbftimes(u,v);

symbolic procedure gfftimes(u,v);
   begin scalar ru,iu,rv,iv;
         ru := car u; iu := cdr u; rv := car v; iv := cdr v;
         return (ru*rv - iu*iv) . (ru*iv+iu*rv) end;

symbolic procedure gbftimes(u,v);
   begin scalar ru,iu,rv,iv;
         ru := car u; iu := cdr u; rv := car v; iv := cdr v;
         return (difbf(timbf(ru,rv),timbf(iu,iv))) .
            plubf(timbf(ru,iv),timbf(iu,rv)) end;

symbolic procedure gfquotient(u,v);
   if atom car u then gffquot(u,v) else gbfquot(u,v);

symbolic procedure gffquot(u,v);
   begin scalar ru,iu,rv,iv,d;
         ru := car u; iu := cdr u; rv := car v; iv := cdr v;
         d := rv*rv+iv*iv;
         return ((ru*rv+iu*iv)/d) . ((iu*rv - ru*iv)/d) end;

symbolic procedure gbfquot(u,v);
   begin scalar ru,iu,rv,iv,d;
         ru := car u; iu := cdr u; rv := car v; iv := cdr v;
         d := plubf(timbf(rv,rv),timbf(iv,iv));
         return divbf(plubf(timbf(ru,rv),timbf(iu,iv)),d) .
            divbf(difbf(timbf(iu,rv),timbf(ru,iv)),d) end;

symbolic procedure gfminus u; (bfminus car u) . (bfminus cdr u);

symbolic procedure gfrotate u; (bfminus cdr u) . (car u);

%symbolic procedure gfrl u; car u;

%symbolic procedure gfim u; cdr u;

%symbolic procedure gfzerop u;
%   if not atom gfrl u then mt!: gfrl u = 0 and mt!: gfim u = 0
%      else equal(u,(0.0 . 0.0));

symbolic procedure gfdot(u,v);
   if atom car u then gffdot(u,v) else gbfdot(u,v);

symbolic procedure gffdot(u,v); car u*car v+cdr u*cdr v;

symbolic procedure gbfdot(u,v);
   plubf(timbf(car u,car v),timbf(cdr u,cdr v));

symbolic procedure gfrsq u; gfdot(u,u);

symbolic procedure gffrsq u; car u*car u+cdr u*cdr u;

symbolic procedure gbfrsq u;
   plubf(timbf(car u,car u),timbf(cdr u,cdr u));

symbolic procedure gffmult(r,u); (r*car u) . (r*cdr u);

symbolic procedure gffsqrt x;
   begin scalar x0,nx,xd,xd0,rl,im; rl := gfrl x; im := gfim x;
     rl := sqrt(hypot(rl,im)/2+rl/2); im := im/(2*rl); nx := rl . im;
     repeat
       <<x0 := nx;
         nx := gffmult(0.5,gffplus(x0,gffquot(x,x0)));
         xd0 := xd; xd := gffrsq gffdiff(x,gfftimes(nx,nx))>>
     until xd0 and xd0 - xd<=0.0; return x0 end;

symbolic procedure gbfmult(r,u);
   <<r := bfloat r; (timbf(r,car u)) . (timbf(r,cdr u))>>;

symbolic procedure gbfsqrt x;
   begin scalar x0,nx,xd,xd0,rl;
     nx :=
      <<rl := (bfsqrt timbf(bfhalf!*,plubf(bfsqrt gfrsq x,gfrl x)));
        rl . timbf(bfhalf!*,divbf(gfim x,rl))>>;
     repeat
       <<x0 := nx;
         nx := gbfmult(bfhalf!*,gbfplus(x0,gbfquot(x,x0)));
         xd0 := xd; xd := gbfrsq gbfdiff(x,gbftimes(nx,nx))>>
     until xd0 and mt!: difbf(xd0,xd)<=0; return x0 end;

symbolic smacro procedure rl2gfc x;
   x . if atom x then 0.0 else bfz!*;

symbolic procedure gfsqrt x;
 % computes gfsqrt x by Newton's method, for both gf and gbf.
   begin scalar xn,neg,negi;
         if gfzerop x then return x;
         if bfminusp gfrl x
           then <<x := gfminus x; neg := t;
                  if bfminusp gfim x then nil else negi := t >>;
         if bfzp gfim x then
            <<x := gfrl x;
              xn := rl2gfc(if atom x then sqrt x else bfsqrt x);
              go to ret>>;
         xn := if atom gfrl x then gffsqrt x else gbfsqrt x;
         if negi then xn := gfminus xn;
    ret: return if neg then gfrotate xn else xn end;

symbolic procedure sgn x; if x>0 then 1 else if x<0 then -1 else 0;

symbolic procedure exptbf(x,n,a);
  % Computes a*x**n in bfloat arithmetic for positive x
  % and positive integer n.
   begin
  lp: if oddintp n then a := timbf(a,x); % not evenp n
      n := lshift (n, -1);
      if n=0 then return a;
      x := timbf(x,x); go to lp end;

symbolic procedure icbrt(x);
   % x is a number : result is integer s approx cube root of x,
   % i.e. if x > 0 then s**3 <= n < (s+1)**3 o/w s**3 >= n > (s-1)**3.
   irootn(fix2(x),3);

symbolic procedure fix2 x; if fixp x then x else fl2int x;

symbolic procedure ilog2 n;
   % n is an integer.  Result is an integer r, s.t. 2**r <= abs(n)
   %  < 2**(r+1).
   begin scalar ans, powers!-of!-2, pwr;
      if n<=0 then terrlst(n,'ilog2);
      pwr := 2;
      powers!-of!-2 := pwr . nil;
      while n>pwr do
         <<powers!-of!-2 := pwr . powers!-of!-2; pwr := pwr*pwr>>;
      ans := 0;
      while(pwr := car(powers!-of!-2)) neq 2 do
        <<powers!-of!-2 := cdr(powers!-of!-2);
          if n >= pwr then << n := n/pwr; ans := ans + 1; >>;
          ans := ans*2>>;
      if n >= 2 then ans := ans + 1;
      return ans
   end;

symbolic procedure isqrt(x);
   % x is a number : result is integer s approx square root of x,
   % i.e. if x > 0 then s**2 <= n < (s+1)**2 o/w s**2 >= n > (s-1)**2.
   if x<=0 then terrlst(x,'isqrt) else irootn(fix2(x), 2);

symbolic procedure qroundup(m,n);
   % m, n are integers, n>0 : result is least integer >= m/n.
   if m<0 then -((-m)/n) else (m+n-1)/n;

symbolic procedure irootn(n,r);
    % n, r are integers : result is integer s approx r'th root of n,
    % i.e. if n > 0 then s**r <= n < (s+1)**r o/w s**r >= n > (s-1)**r.
    if not fixp n then typerr(n,"integer")
     else if not fixp r or r<=0 then typerr(r,"positive integer")
     else if n<0
      then if evenp r then typerr(r,"odd integer") else -irootn(-n,r)
     else if r = 1 then n
     else if n = 0 then 0
     else begin scalar ans;
             ans := irootn1(n,r,ilog2 n,(ilog2 r)/2);
             if ans**r>n then return ans - 1 else return ans
       end;

symbolic procedure irootn!-power2(p, q);
   % p, q are positive integers.
   % Result is an integer (slightly) greater than 2**(p/q).
   % Uses the first few terms of the Taylor expansion (tweaked a bit).
   % Error (of numans/denans) is at most 0.03%.
   begin scalar whole!-part, p1, numans, denans;
      whole!-part := (p+q/2)/q;
      p1 := p-q*whole!-part;
      numans := q^3/100 + 1000*q^3 + 693*q^2*p1 + 243*q*p1^2 + 57*p1^3;
      denans := 1000*q^3;
      return 1+ (2^whole!-part * numans)/denans
      % add 1 to force rounding up
   end;

symbolic procedure irootn1(n,r,logn,xs);
   % n, r integers >0, logn is ilog2(n), xs is the excess # bits in the
   % top 1/2.
   % result is s, s.t. s**r <= n < (s+1)**r or (s-1)**r <= n < s**r.
   begin scalar x, upb, size, tmp;
      size := logn / r;
      if size < 17 then upb := irootn!-power2(1+logn, r)
       else <<x := size/2 - xs;
              upb := irootn1(n/2**(x*r), r, logn-r*x, xs)*2**x;
              tmp := upb**(r-1);
              return (((r-1)*upb)*tmp+n)/(r*tmp)>>;
      repeat <<x := upb; upb := x - qroundup(x - n/x**(r-1),r)>>
         until upb >= x;
      return x
   end;

put('irootn,'number!-of!-args,2);   %  For VALUECHK.

endmodule;

end;
