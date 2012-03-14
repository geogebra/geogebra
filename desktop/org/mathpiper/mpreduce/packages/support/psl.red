module psl;

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


imports big2sys, bigp, floatloworder, floathighorder, gtneg, gtpos,
        i2bf!:, idifference, igetv, ilessp, iminus, inf, iplus, isub1,
        itimes, land, lshift, make!:ibf, neq, sys2int, trimbignum,
        vecinf, veclen, wand, wdifference, wminus, wor, wplus2, wputv,
        wquotient, wshift;

exports ashift, msd!:, fl2bf, integerp!:, normbf, oddintp, preci!:;

fluid '(bbits!*);

global '(bfz!* bitsperword);

compiletime
  global '(!!fleps1exp !!plumaxexp !!pluminexp !!timmaxexp !!timminexp);

remflag ('(ashift msd!: fl2bf ff0 ff1
           bf!-bits bf!-bits!-mask integerp!: normbf oddintp preci!:),
         'lose);

flag('(cond),'eval);   % Enable conditional compilation.

%-------------------------------------------------------------------

% The following routines support fast float operations by exploiting
% the IEEE number format explicitly.

compiletime
 if 'ieee member lispsystem!* then
   remflag('(safe!-fp!-plus safe!-fp!-times safe!-fp!-quot),'lose)
     else
   flag('(safe!-fp!-plus safe!-fp!-times safe!-fp!-quot),'lose);

% Currently 32 and 64 bit IEEE machines are supported.
%
% The following macros assume that on 64 bit machines floathighorder
% and floatloworder both load the full 64 bit floating point number.

compiletime
<<
  define!-constant(ieeeshift,12 - bitsperword);  % 32 bits:-20
  define!-constant(signshift,1 - bitsperword);   % 32 bits:-31
  define!-constant(ieeebias,1023);
  define!-constant(ieeemask,2047);
  ds(floathiword,x(),floathighorder inf x);
  ds(floatloword,x(),floatloworder inf x);

  if bitsperword=32 then
  <<
    ds(ieeezerop,u(), weq(0,floathiword u) and weq(0,floatloword u));
    ds(ieeeequal,u(v),
           weq(floathiword u,floathiword v)
       and weq(floatloword u,floatloword v));
    ds(ieeemant,f(),
       (lor(lshift(
               wor(wshift(wand (floathiword f, 1048575), % 16#FFFFF
                          6),
                   wshift(lf,-26)),
               26),
               wand(lshift(-1,-6), lf))
         where lf := floatloword f));
   >>
   else if bitsperword=64 then
   <<
    ds(ieeezerop,u(), weq(0,floathiword u));
    ds(ieeeequal,u(v), weq(floathiword u,floathiword v));
    ds(ieeemant,f(), wand (floathiword f,
                           4503599627370495)); % 16#FFFFFFFFFFFFF
 >>
   else error(99,"#### unknown bit size");

  ds(ieeeexpt,u(),
      wdifference(wand(ieeemask,
                       wshift(floathiword u,ieeeshift)),
                 ieeebias));
  ds(ieeesign,u(),wshift(floathiword u,signshift));
     % ieeemant is the mantissa part of the upper 32 bit group.

  define!-constant(!!plumaxexp,1018);
  define!-constant(!!pluminexp,-979);
  define!-constant(!!timmaxexp,509);
  define!-constant(!!timminexp,-510);
  define!-constant(!!fleps1exp,-40)
>>;

symbolic procedure safe!-fp!-plus(x,y);
   if ieeezerop x then y
    else if ieeezerop y then x
    else begin scalar u,ex,ey,sx,sy;
            ex := ieeeexpt x;
            ey := ieeeexpt y;
            if (sx := ieeesign x) eq (sy := ieeesign y)
              then if ilessp(ex,!!plumaxexp) and ilessp(ey,!!plumaxexp)
                    then go to ret else return nil;
            if ilessp(ex,!!pluminexp) and ilessp(ey,!!pluminexp)
              then return nil;
          ret:
            u := floatplus2(x,y);
        return
            if sx eq sy or ieeezerop u then u
             else if ilessp(ieeeexpt u,iplus2(ex,!!fleps1exp)) then 0.0
             else u
         end;

symbolic procedure safe!-fp!-times(x,y);
   if ieeezerop x or ieeezerop y then 0.0
    else if ieeeequal(x,1.0) then y
    else if ieeeequal(y,1.0) then x
    else begin scalar u,v;
            u := ieeeexpt x;
            v := ieeeexpt y;
            if igreaterp(u,!!timmaxexp)
              then if ilessp(v,0) then go to ret else return nil;
            if igreaterp(u,0)
              then if ilessp(v,!!timmaxexp) then go to ret
                    else return nil;
            if igreaterp(u,!!timminexp)
              then if igreaterp(v,!!timminexp) then go to ret
                    else return nil;
            if ilessp(v,0) then return nil;
          ret:
            return floattimes2(x,y)
         end;

symbolic procedure safe!-fp!-quot(x,y);
   if ieeezerop y then rdqoterr()
    else if ieeezerop x then 0.0
    else if ieeeequal(y,1.0) then x
    else begin scalar u,v;
            u := ieeeexpt x;
            v := ieeeexpt y;
            if igreaterp(u,!!timmaxexp)
              then if igreaterp(v,0) then go to ret
                    else return nil;
            if igreaterp(u,0)
              then if igreaterp(v,!!timminexp) then go to ret
                    else return nil;
            if igreaterp(u,!!timminexp)
              then if ilessp(v,!!timmaxexp) then go to ret
                    else return nil;
            if igreaterp(v,0) then return nil;
          ret:
            return floatquotient(x,y)
         end;

compiletime
 if 'ieee member lispsystem!* then
  flag('(safe!-fp!-plus safe!-fp!-times safe!-fp!-quot),'lose)
   else
  remflag('(safe!-fp!-plus safe!-fp!-times safe!-fp!-quot),'lose);

%---------------------------------------------------------------

deflist('((iminus iminus)),'unary);

symbolic smacro procedure ashift (m,d);
  if negintp m then -lshift(-m,d) else lshift(m,d);

symbolic smacro procedure oddintp x;
   wand(if bigp x then wgetv(inf x,2)
         else if fixnp x then fixval inf x
         else x,1) eq 1;

symbolic macro procedure bf!-bits (x); {'quote, bbits!*};

%symbolic macro procedure bf!-bits!-mask (x);
%   {'quote, lshift(1, bf!-bits()) - 1};

%symbolic procedure ff1 (w,n);
%  if n eq 0 then w else
%  if wshift (w, wminus n) eq 0 then
%        ff1 (w,wquotient(n,2))
%    else iplus2(ff1 (wshift (w, wminus n),wquotient(n,2)),n) ;

symbolic smacro procedure ff1 (ww,nn);
  <<while not (n eq 0) do <<
      x := wshift(w,wminus n);
      if not (x eq 0) then % left half
        << w := x; y := iplus2(y,n) >>;   % Iplus2 etc. used for
      n := wquotient (n,2)                % bootstrapping.
    >>;
    iplus2(y,w) >>
    where w=ww,n=nn,x=nil,y=0;

%symbolic procedure ff0 (w,n);
%%   returns the number of 0 bits at the least significant end
%  if n eq 0 then w else
%   begin scalar lo;
%     lo := wand(w,isub1 wshift(1,n));
%  return if lo eq 0
%           then iplus2(n,ff0 (wshift(w,wminus n),wquotient(n,2)))
%          else ff0 (lo,wquotient(n,2)) ;
%  end;

comment ff0 determines the number of 0 bits at the least significant
        end of an integer, ie. the largest power of two by which the
        integer is divisible;

compiletime put('hu_hu_hu,'opencode,'((!*move (reg 1) (reg 1))));

symbolic smacro procedure ff0 (ww,nn);
  <<while not (n eq 0) do <<
      lo := wand(w,isub1 wshift(1,n));
      if lo eq 0 then % left half
        << w := wshift(w,wminus n);
           y := iplus2(y,n) >>;           % Iplus2 etc. used for
      n := wquotient (n,2)                % bootstrapping.
    >>;
    if not eq(w,0) then << w := 17; hu_hu_hu (w); y >> else iadd1 y >>
                    % we have to destroy w for gc !!
    where w=ww,n=nn,lo=nil,y=0;

% use wshift(bitsperword,-1) rather than bitsperword/2 as the former
% is open compiled


comment we split msd!: into two parts: one for bignums, one for
        machine words. That will greatly reduce the size of preci!:
        below;

symbolic smacro procedure word!-msd!: u;
   ff1(u,wshift(bitsperword,-1));

symbolic smacro procedure big!-msd!: u;
   iplus2(itimes2(bf!-bits(),isub1 s),word!-msd!: igetv(u,s))
       where s := veclen vecinf u;

symbolic smacro procedure msd!: u;
   if bigp u then big!-msd!: u
    else if fixnp u then word!-msd!: fixval inf u
    else word!-msd!: u;

%symbolic smacro procedure msd!: u;
%  % returns the most significant (binary) digit of a positive integer u
%  if bigp u
%    then iplus2(itimes2(bf!-bits(),isub1 s),
%                ff1(igetv(u,s),wshift(bitsperword,-1)))
%       where s := veclen vecinf u
%   else if fixnp u then ff1 (fixval inf u,wshift(bitsperword,-1))
%   else ff1 (u,wshift(bitsperword,-1));

symbolic smacro procedure mt!: u; cadr u;
symbolic smacro procedure ep!: u; cddr u;

symbolic smacro procedure preci!: nmbr;
   % This function counts the precision of a number "n". NMBR is a
   % binary bigfloat representation of "n".
   % msd!: abs mt!: nmbr
   (if bigp m then big!-msd!: m
     else if fixnp m
      then (word!-msd!:(if iminusp n then iminus n else n)
                         where n = fixval inf m)
     else if iminusp m then word!-msd!:(iminus m)
     else word!-msd!: m)
    where m = mt!: nmbr;

%symbolic smacro procedure preci!: nmbr;
%   % This function counts the precision of a number "n". NMBR is a
%   % binary bigfloat representation of "n".
%   % msd!: abs mt!: nmbr
%   (if bigp m then msd!: m
%     else if fixnp m
%      then (ff1(if iminusp n then iminus n else n,
%                wshift(bitsperword,-1))
%              where n = fixval inf m)
%     else if iminusp m then ff1(iminus m,wshift(bitsperword,-1))
%     else ff1(m,wshift(bitsperword,-1)))
%    where m = mt!: nmbr;

symbolic smacro procedure make!:ibf (mt, ep);
  '!:rd!: . (mt . ep);

if not('ieee memq lispsystem!*) then
     flag('(fl2bf),'lose);

!#if (eq bitsperword 64)

symbolic procedure fl2bf f;
  % u is a floating point number
  % result is a binary bigfloat
  if fixp f then i2bf!: f
   else begin scalar m,e;
      m := ieeemant f;
      e := ieeeexpt f;
      % if exponent <> -1023 add 16#10000000000000, implicit highest bit
      if e neq  -1023 then m := lor (m, lshift(1,52));
      return
        if izerop m then bfz!*
         else normbf make!:ibf (if ieeesign f eq 1 then -m else m,
                                idifference(e,52))
     end;

!#else

symbolic procedure fl2bf f;
  % u is a floating point number
  % result is a binary bigfloat
  if fixp f then i2bf!: f
   else begin scalar m,e;
      m:=
        lor(lshift(
               wor(wshift(wand (floathiword f, 1048575), % 16#FFFFF
                          6),
                   wshift(floatloword f,-26)),
               26),
            wand(lshift(-1,-6), floatloword f));
      %% m := ieeemant f;
      e := ieeeexpt f;
      % if exponent <> -1023 add 16#10000000000000, implicit highest bit
      if e neq  -1023 then m := lor (m, lshift(1,52));
      return
        if izerop m then bfz!*
         else normbf make!:ibf (if ieeesign f eq 1 then -m else m,
                                idifference(e,52))
     end;

!#endif

symbolic procedure normbf x;
   begin scalar mt,s;integer ep,ep1;
      if (mt := mt!: x)=0 then go to ret;
      if mt<0 then <<mt := -mt; s := t>>;
      ep := ep!: x;
%      ep1 := remainder(ep,bf!-bits());
%      if ep1 < 0 then ep1 := ep1 + bf!-bits();
%      if ep1 neq 0 then <<ep := ep - ep1; mt := lshift(mt,ep1)>>;
      while bigp mt and wgetv(inf mt,2) eq 0 do <<
        mt := lshift(mt,-bf!-bits());
        ep := ep+bf!-bits() >>;
      ep1 := ff0(if bigp mt then wgetv(inf mt,2)
                  else if fixnp mt then fixval inf mt
                  else mt,wshift(bitsperword,-1));
      if not (ep1 eq 0) then <<mt := lshift(mt,wminus ep1);
                               ep := ep + ep1>>;
      if s then mt := -mt;
ret:    return make!:ibf(mt,ep) end;

%symbolic procedure normbf x;
%   begin scalar mt,s;integer ep,ep1;
%      if (mt := mt!: x)=0 then go to ret;
%      if mt<0 then <<mt := -mt; s := t>>;
%      ep := ep!: x;
%      while bigp mt and land(mt,bf!-bits!-mask())=0 do <<
%        mt := lshift(mt,-bf!-bits());
%        ep := ep+bf!-bits() >>;
%      while land(mt,255)=0 do <<
%        mt := lshift(mt,-8);
%        ep := ep+8 >>;
%      while land(mt,1)=0 do <<
%        mt := lshift(mt,-1);
%        ep := ep+1>>;
%%      ep1 := remainder(ep,bf!-bits());
%%      if ep1 < 0 then ep1 := ep1 + bf!-bits();
%%      if ep1 neq 0 then <<ep := ep - ep1; mt := lshift(mt,ep1)>>;
%      if s then mt := -mt;
%ret:    return make!:ibf(mt,ep) end;

symbolic procedure integerp!: x;
% This function returns T if X is a BINARY BIG-FLOAT
%      representing an integer, else it returns NIL.
% X is any LISP entity.
bfp!: x and
  (ep!: x >= 0 or
    preci!: x > - ep!: x and
      land(abs mt!: x,lshift(2,-ep!: x) - 1) = 0);

flag ('(ashift lshift msd!: fl2bf ff0 ff1
        bf!-bits bf!-bits!-mask integerp!: normbf oddintp preci!:),
      'lose);

if not('ieee memq lispsystem!*) then remflag('(fl2bf),'lose);

% This belong in $pxu/nbig30a.

symbolic(bigfloathi!* :=  (2 ** 53 - 1) * 2 ** 971);

symbolic(bigfloatlow!* := - bigfloathi!*);

remflag('(cond),'eval);



% HP-Risc and IBM RS architectures need special handling of fltinf in
% fastmath.red

if 'HP!-Risc member lispsystem!* then
   <<remflag('(fltinf),'lose);
     ds(fltinf,x(),mkitem(vector!-tag,x));
     flag('(fltinf),'lose)>>;

if 'IBMRS member lispsystem!* then
   <<remflag('(fltinf),'lose);
     ds(fltinf,x(),mkstr x);
     flag('(fltinf),'lose)>>;

endmodule;

end;
