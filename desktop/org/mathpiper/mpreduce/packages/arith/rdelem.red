module rdelem;  % Elementary functions in rounded domain.

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


exports deg2rad!*, quotient!:, rad2deg!*, rdacos!*, rdacosd!*,
        rdacosh!*, rdacot!*, rdacotd!*, rdacoth!*, rdacsc!*, rdacscd!*,
        rdacsch!*, rdarg!*, rdasec!*, rdasecd!*, rdasech!*, rdasin!*,
        rdasind!*, rdasinh!*, rdatan!*, rdatan2!*, rdatan2d!*,
        rdatand!*, rdatanh!*, rdcbrt!*, rdcos!*, rdcosd!*, rdcosh!*,
        rdcot!*, rdcotd!*, rdcoth!*, rdcsc!*, rdcscd!*, rdcsch!*,
        rde!*, rdexp!*, rdexpt!*, rdhalf!*, rdhypot!*, rdlog!*,
        rdlog10!*, rdlogb!*, rdnorm!*, rdone!*, rdpi!*, rdsec!*,
        rdsecd!*, rdsech!*, rdsin!*, rdsind!*, rdsinh!*,
        rdsqrt!*, rdtan!*, rdtand!*, rdtanh!*, rdtwo!*, rdzero!*,
        texpt!:, texpt!:any;

imports !*f2q, abs!:, acos, acos!*, acosd, acosh, acot, acotd, acoth,
        acsc, acscd, acsch, asec, asecd, asech, asin, asin!*, asind,
        asinh, atan, atan!*, atan2, atan2d, atand, atanh, bflerrmsg,
        bfloat, bfp!:, bfsqrt, cbrt, conv!:bf2i, conv!:bf2i, conv!:mt,
        convprec, cos, cos!*, cosd, cosh, cot, cotd, coth, csc, cscd,
        csch, difbf, divbf, e!*, ep!:, eqcar, equal!:, exp, exp!*,
        exp!:, exptbf, geq, greaterp!:, hypot, i2rd!*, incprec!:,
        invbf, leq, leq!:, lessp!:, log, log!*, log10, log!:,
        logb, logfp, lshift, make!:ibf, minus!:, minusp!:, mk!*sq,
        mkround, mt!:, neq, pi!*, plubf, preci!:, rd!:minus,
        rd!:minusp, read!:num, rndbfon, round!*, round!:last,
        round!:mt, sec, secd, sech, sgn, simprd, sin, sin!*, sind,
        sinh, sqrt, sqrt!:, tan, tan!*, tand, tanh, terrlst, timbf,
        times!:;

fluid '(!:prec!: !:bprec!: !*!*roundbf);

global '(bfz!* bfone!* bften!* bfhalf!* !:180!* !:bf1!.5!* bfthree!*
 !:bf60!* epsqrt!* bftwo!* !!pii !!flprec !!rdprec !!shbinfl
 pi!/180 !180!/pi !!ee !!maxarg);

pi!/180 := !!pii/180;  !180!/pi := 180/!!pii;

fluid '(!*numval);

deflist('((exp rdexp!*) (expt rdexpt!*) (log rdlog!*) (sin rdsin!*)
   (cos rdcos!*) (tan rdtan!*) (asin rdasin!*) (acos rdacos!*)
   (atan rdatan!*) (sqrt rdsqrt!*) (sinh rdsinh!*) (cosh rdcosh!*)
   (sec rdsec!*) (csc rdcsc!*) (cot rdcot!*) (tanh rdtanh!*)
   (coth rdcoth!*) (sech rdsech!*) (csch rdcsch!*) (asinh rdasinh!*)
   (acosh rdacosh!*) (acot rdacot!*) (asec rdasec!*) (acsc rdacsc!*)
   (atanh rdatanh!*) (acoth rdacoth!*) (asech rdasech!*)
   (acsch rdacsch!*) (logb rdlogb!*) (log10 rdlog10!*) (ln rdlog!*)
   (atan2 rdatan2!*) (hypot rdhypot!*) % (cbrt rdcbrt!*)
   (deg2rad deg2rad!*) (rad2deg rad2deg!*) (deg2dms deg2dms!*)
   (rad2dms rad2dms!*) (dms2deg dms2deg!*) (dms2rad dms2rad!*)
   (norm rdnorm!*) (arg rdarg!*) (e rde!*) (pi rdpi!*)),
   '!:rd!:);

% deflist('((sind rdsind!*) (cosd rdcosd!*) (asind rdasind!*) (acosd
%    rdacosd!*) (tand rdtand!*) (cotd rdcotd!*) (atand rdatand!*) (acotd
%    rdacotd!*) (secd rdsecd!*) (cscd rdcscd!*) (asecd rdasecd!*) (acscd
%    rdacscd!*) (atan2d rdatan2d!*)),'!:rd!:);



for each n in '(exp sin cos tan asin acos atan sinh cosh  % log
    sec csc cot tanh coth sech csch asinh acosh acot asec acsc atanh
    acoth asech acsch logb log10 ln atan2 hypot
%   sind cosd asind acosd tand cotd atand acotd secd cscd asecd acscd
%   atan2d cbrt
    deg2rad rad2deg deg2dms rad2dms dms2deg dms2rad norm arg argd)
       do put(n,'simpfn,'simpiden);

flag('(dms2deg dms2rad),'listargp);

deflist('((dms2deg!* simpdms) (dms2rad!* simpdms)), 'simparg);

deflist('((atan2 2) (hypot 2) (atan2d 2) (logb 2)),
  'number!-of!-args);

flag('(acsc sind asind tand atand cotd acotd cscd acscd csch
       acsch deg2rad rad2deg),'odd);   % sgn.

flag('(cosd secd),'even);

flag('(cotd sech),'nonzero);

symbolic procedure rdexp!* u; mkround
  (if not atom x then exp!* x
   else if x>!!maxarg then <<rndbfon(); exp!* bfloat x>>
   else if x<-!!maxarg then 0.0 else exp x)
   where x=convprec u;

symbolic procedure rdsqrt!* u;
   mkround(if atom x then sqrt x else bfsqrt x)
   where x=convprec u;

symbolic procedure rdexpt!*(u,v);
   mkround
     (if not atom x then texpt!:any(x,y) else
       if zerop x then if zerop y then rederr "0**0 formed" else u else
      ((if z>!!maxarg then <<rndbfon(); texpt!:any(bfloat x,bfloat y)>>
        else if z<-!!maxarg then 0.0 else rexpt(x,y))
        where z=y*logfp bfloat abs x))
      where x=convprec u,y=convprec v;

symbolic procedure rdlog!* u;
   mkround(if atom x then log x else log!* x)
   where x=convprec u;

% symbolic procedure rdsgn!* u;
%   (if atom x then sgn x else sgn mt!: x) where x=round!* u;

symbolic procedure rdatan2!*(u,v);
   if !:zerop u and !:zerop v
     then rerror(arith,8,"0/0 formed")
    else (mkround(if atom x then atan2(x,y) else atan2!*(x,y))
          where x=convprec u,y=convprec v);

% symbolic procedure rdatan2d!*(u,v);
%    mkround(if atom x then atan2d(x,y) else rad2deg!: atan2!*(x,y))
%    where x=convprec u,y=convprec v;

symbolic procedure atan2!*(y,x);
   if mt!: x=0 then if (y := mt!: y)=0 then bfz!* else
      <<x := pi!/2!*(); if y<0 then minus!: x else x>>
   else <<(if mt!: x>0 then a
      else if mt!: y<0 then difbf(a,pi!*())
         else plubf(a,pi!*()))
     where a=atan!* divbf(y,x)>>;

% symbolic procedure atan2d!*(y,x);
%    if mt!: x=0 then if (y := mt!: y)=0 then bfz!* else
%       <<x := timbf(!:180!*,bfhalf!*); if y<0 then minus!: x else x>>
%    else <<(if mt!: x>0 then a
%       else if mt!: y<0 then difbf(a,!:180!*) else plubf(a,!:180!*))
%      where a=rad2deg!: atan!* divbf(y,x)>>;

symbolic procedure rde!*; mkround if !*!*roundbf then e!*() else !!ee;

symbolic procedure rdpi!*;
   mkround if !*!*roundbf then pi!*() else !!pii;

symbolic procedure pi!/2!*; timbf(bfhalf!*,pi!*());

symbolic procedure deg2rad!* u;
   mkround(if atom x then deg2rad x else deg2rad!: x)
   where x=convprec u;

symbolic procedure rad2deg!* u;
   mkround(if atom x then rad2deg x else rad2deg!: x)
   where x=convprec u;

symbolic procedure deg2rad x; x*pi!/180;

symbolic procedure rad2deg x; x*!180!/pi;

symbolic procedure deg2rad!: x; divbf(timbf(x,pi!*()),!:180!*);

symbolic procedure rad2deg!: x; divbf(timbf(x,!:180!*),pi!*());

symbolic procedure rdsin!* u;
   mkround (if atom x then sin x else sin!* x)
   where x=convprec u;

% symbolic procedure rdsind!* u;
%    mkround (if atom x then sind x else sin!* deg2rad!: x)
%    where x=convprec u;

symbolic procedure rdcos!* u;
   mkround(if atom x then cos x else cos!* x)
   where x=convprec u;

% symbolic procedure rdcosd!* u;
%    mkround(if atom x then cosd x else cos!* deg2rad!: x)
%   where x=convprec u;

symbolic procedure rdtan!* u;
   mkround(if atom x then tan x else tan!* x)
   where x=convprec u;

% symbolic procedure rdtand!* u;
%    mkround(if atom x then tand x else tan!* deg2rad!: x)
%   where x=convprec u;

symbolic procedure rdasin!* u;
   mkround(if atom x then asin x else asin!* x)
   where x=convprec u;

% symbolic procedure rdasind!* u;
%    mkround(if atom x then asind x else rad2deg!: asin!* x)
%    where x=convprec u;

symbolic procedure rdacos!* u;
   mkround(if atom x then acos x else acos!* x)
   where x=convprec u;

% symbolic procedure rdacosd!* u;
%    mkround(if atom x then acosd x else rad2deg!: acos!* x)
%    where x=convprec u;

symbolic procedure rdatan!* u;
   mkround(if atom x then atan x else atan!* x)
   where x=convprec u;

% symbolic procedure rdatand!* u;
%    mkround(if atom x then atand x else rad2deg!: atan!* x)
%   where x=convprec u;

symbolic procedure rdsinh!* u;
   mkround(if atom x then sinh x else sinh!* x)
   where x=convprec u;

symbolic procedure rdcosh!* u;
   mkround(if atom x then cosh x else cosh!* x)
   where x=convprec u;

% these redefine functions that are in bfelem, and are faster.

symbolic procedure sinh!* x;
   timbf(bfhalf!*,difbf(y,invbf y)) where y=exp!* x;

symbolic procedure cosh!* x;
   timbf(bfhalf!*,plubf(y,invbf y)) where y=exp!* x;


% no bfelem functions after this point.

symbolic procedure rdsec!* u;
   mkround(if atom x then sec x else invbf cos!* x)
   where x=convprec u;

% symbolic procedure rdsecd!* u;
%    mkround(if atom x then secd x else invbf cos!* deg2rad!: x)
%    where x=convprec u;

symbolic procedure rdcsc!* u;
   mkround(if atom x then csc x else invbf sin!* x)
   where x=convprec u;

% symbolic procedure rdcscd!* u;
%    mkround(if atom x then cscd x else invbf sin!* deg2rad!: x)
%   where x=convprec u;

symbolic procedure rdcot!* u;
   mkround(if atom x then cot x else tan!* difbf(pi!/2!*(),x))
   where x=convprec u;

% symbolic procedure rdcotd!* u;
%   mkround(if atom x then cotd x else tan!* difbf(pi!/2!*(),
%           deg2rad!: x))
%    where x=convprec u;

symbolic procedure rdtanh!* u;
   mkround(if atom x then tanh x else divbf(sinh!* x,cosh!* x))
   where x=convprec u;

symbolic procedure rdcoth!* u;
   mkround(if atom x then coth x else divbf(cosh!* x,sinh!* x))
   where x=convprec u;

symbolic procedure rdsech!* u;
   mkround(if atom x then sech x else invbf cosh!* x)
   where x=convprec u;

symbolic procedure rdcsch!* u;
   mkround(if atom x then csch x else invbf sinh!* x)
   where x=convprec u;

symbolic procedure rdasinh!* u;
   mkround(if atom x then asinh x else asinh!* x)
   where x=convprec u;

symbolic procedure rdacosh!* u;
   mkround(if atom x then acosh x else acosh!* x)
   where x=convprec u;

symbolic procedure asinh!* x; begin scalar s;
   if minusp!: x then x := minus!: x else s := t;
   x := if leq!:(x,epsqrt!*) then x
      else log!* plubf(x,
         if lessp!:(x,bftwo!*) then bfsqrt plubf(timbf(x,x),bfone!*)
         else if lessp!:(invbf x,epsqrt!*) then x
         else timbf(x,bfsqrt plubf(bfone!*,divbf(bfone!*,timbf(x,x)))));
   return if s then x else minus!: x end;

symbolic procedure acosh!* x;
   if lessp!:(x,bfone!*) then terrlst(x,'acosh)
   else log!* plubf(x,if leq!:(invbf x,epsqrt!*) then x
      else timbf(x,bfsqrt difbf(bfone!*,divbf(bfone!*,timbf(x,x)))));

symbolic procedure rdacot!* u;
   mkround(if atom x then acot x
      else difbf(pi!/2!*(),atan!* x))
   where x=convprec u;

% symbolic procedure rdacotd!* u;
%   mkround(if atom x then acotd x
%       else rad2deg!: difbf(pi!/2!*(),atan!* x))
%    where x=convprec u;

symbolic procedure rdasec!* u;  % not yet
   mkround(if atom x then asec x else
      difbf(pi!/2!*(),asin!* invbf x))
   where x=convprec u;

% symbolic procedure rdasecd!* u;  % not yet
%    mkround(if atom x then asecd x else
%       rad2deg!: difbf(pi!/2!*(),asin!* invbf x))
%    where x=convprec u;

symbolic procedure rdacsc!* u;
   mkround(if atom x then acsc x else asin!* invbf x)
   where x=convprec u;

% symbolic procedure rdacscd!* u;
%   mkround(if atom x then acscd x else rad2deg!: asin!* invbf x)
%   where x=convprec u;

symbolic procedure rdatanh!* u;
   mkround(if atom x then atanh x else atanh!* x)
   where x=convprec u;

symbolic procedure atanh!* x;
   if not greaterp!:(bfone!*,abs!: x) then terrlst(x,'atanh)
   else if leq!:(abs!: x,epsqrt!*) then x
   else timbf(bfhalf!*,
      log!* divbf(plubf(bfone!*,x),difbf(bfone!*,x)));

symbolic procedure rdacoth!* u;
   mkround(if atom x then acoth x else atanh!* invbf x)
   where x=convprec u;

symbolic procedure rdasech!* u;   % not from here down
   mkround(if atom x then asech x
      else if leq!:(x,bfz!*) or greaterp!:(x,bfone!*)
         then terrlst(x,'asech) else acosh!* invbf x)
   where x=convprec u;

symbolic procedure rdacsch!* u;
   mkround(if atom x then acsch x
      else if mt!: x=0 then terrlst(x,'acsh) else asinh!* invbf x)
   where x=convprec u;

symbolic procedure rdlogb!*(u,v);
   mkround(if atom x then logb(x,b) else logb!*(x,b))
   where x=convprec u,b=convprec v;

symbolic procedure rdlog10!* u;
   mkround(if atom x then log10 x else logb!*(x,bften!*))
   where x=convprec u;

symbolic procedure logb!* (x,b); %log x to base b;
   begin scalar a,s;
      a := greaterp!:(x,bfz!*);
      s := not(leq!:(b,bfz!*) or equal!:(b,bfone!*));
      if a and s then return divbf(log!* x,log!* b)
         else terrlst((if a then list ('base,b)
            else if s then list('arg,x) else list(x,b)),'logb) end;

% symbolic procedure rdcbrt!* u;
%    mkround(if atom x then cbrt x else cbrt!* x)
%    where x=convprec u;

% symbolic procedure cbrt!* x;
%    begin scalar s,l,g,u,nx,r; u := bfone!*;
%          if mt!: x=0 or equal!:(abs!: x,u) then return x
%          else if minusp!: x then x := minus!: x else s := t;
%          if lessp!:(x,u) then <<x := divbf(u,x); l := t>>
%             else if equal!:(x,u) then go to ret;
%          nx := '!:bf!: .
%             <<r := remainder(ep!:(nx := conv!:mt(x,3)),3);
%               if r=0 then (5+mt!: nx/179) . (ep!: nx/3)
%               else if r=1 or r=-2
%                then (10+mt!: nx/74) . ((ep!: nx-1)/3)
%               else (22+mt!: nx/35) . ((ep!: nx-2)/3)>>;
%    loop: r := nx;
%          nx := plubf(divbf(r,!:bf1!.5!*),
%             divbf(x,timbf(r,timbf(r,bfthree!*))));
%          if g and leq!:(r,nx) then go to ret;
%          g := t; go to loop;
%     ret: if l then nx := divbf(u,nx);
%          return if s then nx else minus!: nx end;

symbolic procedure rdhypot!*(u,v);
   mkround(if atom p then hypot(p,q) else hypot!*(p,q))
   where p=convprec u,q=convprec v;

symbolic procedure hypot!*(p,q);
 % Hypot(p,q)=sqrt(p*p+q*q) but avoids intermediate swell.
 begin scalar r;
   if minusp!: p then p := minus!: p; if minusp!: q then q := minus!: q;
   if mt!: p=0 then return q else if mt!: q=0 then return p
   else if lessp!:(p,q) then <<r := p; p := q; q := r>>;
   r := divbf(q,p);
   return if lessp!:(r,epsqrt!*) then p
      else timbf(p,bfsqrt plubf(bfone!*,timbf(r,r))) end;

symbolic procedure simpdms l;
   % Converts argument of form ({d,m,s}) to rd ((d m s)) if possible.
   if cdr l or atom (l := car l) or not eqcar(l,'list)
      or length l neq 4 then nil else
   begin scalar fl;
      l := for each a in cdr l collect
          if not (null(a := simprd list a) and (fl := t))
             then a := car a;
      if not fl then return list l end;

symbolic procedure round2a!* a; if atom a then a else round!* a;

symbolic procedure dms2rad!* u; deg2rad!* dms2deg!* u;

symbolic procedure dms2deg!* u;
   mkround(if atom caddr l then dms2deg l else dms2deg!: l)
   where l=list(round2a!* car u,round2a!* cadr u,round!* caddr u);

symbolic procedure dms2deg l; ((caddr l/60.0+cadr l)/60.0+car l);

symbolic procedure dms2deg!: l;
   plubf(bfloat car l,divbf(plubf(bfloat cadr l,
      divbf(bfloat caddr l,!:bf60!*)),!:bf60!*));

symbolic procedure rad2dms x; deg2dms rad2deg x;

symbolic procedure rad2dms!* u; deg2dms!* rad2deg!* u;

symbolic procedure deg2dms!* u;
   mklist3!*(if atom x then deg2dms x else deg2dms!: x)
   where x=round2a!* u;

symbolic procedure mklist3!* x; % floats seconds if not integer.
   'list . list(car x,cadr x,
      <<(if atom s and zerop(s-fix s) then fix s
         else if not atom s and integerp!: s then conv!:bf2i s
         else mk!*sq !*f2q mkround s) where s=caddr x>>);

symbolic procedure deg2dms x; % dms output in form list(d,m,s);
   begin integer d,m;
%     m := fix(x := 60.0*(x-(d := fix2 x)));
      m := fix(x := 60.0*(x-(d := fix x)));
      return list(d,m,60.0*(x-m)) end;

symbolic procedure deg2dms!: x; % dms output in form list(d,m,s).
   begin integer d,m;
      d := conv!:bf2i x;
      m := conv!:bf2i(x := timbf(!:bf60!*,difbf(x,bfloat d)));
      return list(d,m,timbf(!:bf60!*,difbf(x,bfloat m))) end;

symbolic procedure rdnorm!* u; if rd!:minusp u then rd!:minus u else u;

symbolic procedure rdarg!* u;
   if rd!:minusp u then rdpi!*() else rdzero!*();

% the following bfloat definitions are needed in addition to files
% smbflot and bfelem.red to support rdelem.

global '(!:bfone!* bftwo!* bfhalf!* bfz!* !:bf!-0!.25);

symbolic procedure rdone!*; if !*!*roundbf then bfone!* else 1.0;

symbolic procedure rdtwo!*; if !*!*roundbf then bftwo!* else 2.0;

symbolic procedure rdhalf!*; if !*!*roundbf then bfhalf!* else 0.5;

symbolic procedure rdzero!*; if !*!*roundbf then bfz!* else 0.0;

symbolic procedure texpt!:(nmbr, k);
% This function calculates the Kth power of "n" up to the
% binary precision specified by !:BPREC!:. %SK
% NMBR is a BINARY BIG-FLOAT representation of "n" and K an integer.
   if not fixp k then bflerrmsg 'texpt!:  % use texpt!:any in this case.
    else if k=0 then bfone!*
    else if k=1 then nmbr
    else if k<0 then invbf texpt!:(nmbr,-k) %SK
    else exptbf(nmbr,k,bfone!*); %SK

symbolic procedure quotient!:(n1, n2);
% This function calculates the integer quotient of "n1"
%      and "n2", just as the "QUOTIENT" for integers does.
% **** For calculating the quotient up to a necessary
% ****      precision, please use DIVIDE!:.
% N1 and N2 are BIG-FLOAT representations of "n1" and "n2".
begin integer e1, e2;
  if (e1 := ep!: n1) = (e2 := ep!: n2) then return
             make!:ibf(mt!: n1 / mt!: n2, 0)
   else if e1 > e2 then return
             quotient!:(incprec!:(n1, e1 - e2) , n2)
   else return
             quotient!:(n1, incprec!:(n2, e2 - e1));
end$

symbolic procedure texpt!:any(x, y);
  %modified by SK to use bfsqrt and exp!*, invbf and timbf.
% This function calculates the power x**y, where "x"
%      and "y" are any numbers.  The precision of
%      the result is specified by !:PREC!:. % SK
% **** For a negative "x", this function returns
% ****      -(-x)**y unless "y" is an integer.
% X is a BIG-FLOAT representation of "x".
% Y is either an integer, a floating-point number,
%      or a BIG-FLOAT number, i.e., a BIG-FLOAT
%      representation of "y".
    if equal!:(x,e!*()) then exp!* bfloat y
    else if fixp y then texpt!:(x, y)
    else if integerp!: y then texpt!:(x,conv!:bf2i y)
    else if not(bfp!: y or bfp!:(y := read!:num y))
     then bflerrmsg 'texpt!:any     % read!:num probably not necessary.
    else if minusp!: y then invbf texpt!:any(x,minus!: y) %SK
    else if equal!:(y,bfhalf!*) then bfsqrt x   %SK
    else if equal!:(y,!:bf!-0!.25) then bfsqrt bfsqrt x   %SK
    else begin integer n;  scalar xp, yp;
          n := (if !:bprec!: then !:bprec!:
                else max(preci!: x, preci!: y));
%          if minusp!: x then xp:=minus!: x else xp := x;
          if minusp!: x then bflerrmsg 'texpt!:any
           else xp := x;
          if integerp!: times!:(y,bftwo!*) then
             << xp := incprec!:(xp, 1);
                yp := texpt!:(xp, conv!:bf2i y);
                yp := times!:(yp, sqrt!:(xp, n + 1));
                yp := round!:mt(yp, n) >>
          else
             << yp := timbf(y, log!:(xp, n + 1)); %SK
                yp := exp!:(yp, n) >>;
          return (if minusp!: x then minus!: yp else yp);
     end;

symbolic procedure integerp!: x;
% This function returns T if X is a BINARY BIG-FLOAT
%      representing an integer, else it returns NIL.
% X is any LISP entity.
bfp!: x and
  (ep!: x >= 0 or
    preci!: x > - ep!: x and remainder(mt!: x,lshift(1,-ep!: x)) = 0);

endmodule;

end;
