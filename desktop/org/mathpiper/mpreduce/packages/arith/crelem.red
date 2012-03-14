module crelem; % Complex elementary functions for complex rounded.

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


imports !*rd2cr, bflessp, bfminusp, cr!:differ, cr!:minus, cr!:plus,
        cr!:quotient, cr!:times, cr!:zerop, cr2i!*, crhalf!*, cri!*,
        cri!/2, crprcd, crrl, deg2rad!*, gf2cr!:, gfsqrt, i2cr!*,
        i2rd!*, mkcr, rad2deg!*, rd!:minus, rd!:quotient, rd!:times,
        rdatan2!*, rdatan2d!*, rdcos!*, rdcosd!*, rdcosh!*, rde!*,
        rdexp!*, rdhalf!*, rdhypot!*, rdlog!*, rdone!*, rdpi!*,
        rdsin!*, rdsind!*, rdsinh!*, rdtwo!*, rdzero!*, retag,
        round!*, tagim, tagrl;

fluid '(!*!*roundbf);

global '(!!flprec !!rdprec bfz!* bftwo!* bfone!* bfhalf!*);

deflist('((expt crexpt!*) (sin crsin!*) (cos crcos!*) (tan crtan!*)
          (asin crasin!*) (acos cracos!*) (atan cratan!*)
          (cot crcot!*) (acot cracot!*) (sec crsec!*) (asec crasec!*)
          (csc crcsc!*) (acsc cracsc!*) (sinh crsinh!*) (cosh crcosh!*)
          (asinh crasinh!*) (acosh cracosh!*) (tanh crtanh!*)
          (coth crcoth!*) (atanh cratanh!*) (acoth cracoth!*)
          (sech crsech!*) (csch crcsch!*) (asech crasech!*)
          (acsch cracsch!*) (atan2 cratan2!*) (arg crarg!*)
          (sqrt crsqrt!*) (norm crnorm!*) (arg crarg!*) (log crlog!*) (log10 crlog10!*)
          (exp crexp!*) (logb crlogb!*) (e cre!*) (pi crpi!*)),'!:cr!:);

% deflist('((sind crsind!*) (cosd crcosd!*) (tand crtand!*)
%           (asind crasind!*) (acosd cracosd!*) (atand cratand!*)
%           (cotd crcotd!*) (acotd cracotd!*) (secd crsecd!*)
%           (cscd crcscd!*) (acscd cracscd!*)
%           (asecd crasecd!*) (argd crargd!*)),'!:cr!:);

symbolic procedure cre!*; mkcr(rde!*(),rdzero!*());

symbolic procedure crpi!*; mkcr(rdpi!*(),rdzero!*());

symbolic procedure crexpt!*(u,v);
   if cr!:zerop(cr!:differ(v,crhalf!*())) then crsqrt!* u
   else crexp!* cr!:times(v,crlog!* u);

symbolic procedure crnorm!* u; rdhypot!*(tagrl u,tagim u);

symbolic procedure crarg!* u; rdatan2!*(tagim u,tagrl u);

% symbolic procedure crargd!* u; rdatan2d!*(tagim u,tagrl u);

symbolic procedure crsqrt!* u; gf2cr!: gfsqrt crprcd u;

symbolic procedure crr2d!* u; mkcr(rad2deg!* tagrl u,rad2deg!* tagim u);

symbolic procedure crd2r!* u; mkcr(deg2rad!* tagrl u,deg2rad!* tagim u);

symbolic procedure crsin!* u;
   mkcr(rd!:times(rdsin!* rl,rdcosh!* im),
        rd!:times(rdcos!* rl,rdsinh!* im))
    where rl=tagrl u,im=tagim u;

% symbolic procedure crsind!* u;
%    mkcr(rd!:times(rdsind!* rl,rdcosh!* deg2rad!* im),
%         rd!:times(rdcos!* rl,rdsinh!* deg2rad!* im))
%     where rl=tagrl u,im=tagim u;

symbolic procedure crcos!* u;
   mkcr(rd!:times(rdcos!* rl,rdcosh!* im),
        rd!:minus rd!:times(rdsin!* rl,rdsinh!* im))
    where rl=tagrl u,im=tagim u;

% symbolic procedure crcosd!* u;
%    mkcr(rd!:times(rdcosd!* rl,rdcosh!* deg2rad!* im),
%         rd!:minus rd!:times(rdsind!* rl,rdsinh!* deg2rad!* im))
%     where rl=tagrl u,im=tagim u;

symbolic procedure crtan!* u;
   cr!:times(cri!*(),cr!:quotient(cr!:differ(y,x),cr!:plus(y,x)))
   where x=crexp!*(cr!:times(cr2i!*(),u)),y=i2cr!* 1;

% symbolic procedure crtand!* u;
%    cr!:times(cri!*(),cr!:quotient(cr!:differ(y,x),cr!:plus(y,x)))
%    where x=crexp!*(cr!:times(cr2i!*(),crd2r!* u)),y=i2cr!* 1;

symbolic procedure crcot!* u;
   cr!:times(cri!*(),cr!:quotient(cr!:plus(x,y),cr!:differ(x,y)))
   where x=crexp!*(cr!:times(cr2i!*(),u)),y=i2cr!* 1;

% symbolic procedure crcotd!* u;
%    cr!:times(cri!*(),cr!:quotient(cr!:plus(x,y),cr!:differ(x,y)))
%    where x=crexp!*(cr!:times(cr2i!*(),crd2r!* u)),y=i2cr!* 1;

symbolic procedure cratan2!*(y,x);
    begin scalar q,p;
       q := crsqrt!* cr!:plus(cr!:times(y,y),cr!:times(x,x));
      if cr!:zerop q then error(0,list("invalid arguments to ",'atan2));
       y := cr!:quotient(y,q); x := cr!:quotient(x,q); p := rdpi!*();
       if cr!:zerop x then
          <<q := rd!:quotient(p,i2rd!* 2);
            return if bfminusp retag crrl y then rd!:minus q else q>>;
       q := cratan!* cr!:quotient(y,x);
       if bfminusp retag crrl x then
          <<p := !*rd2cr p;
            q := if bfminusp retag crrl y
               then cr!:differ(q,p) else cr!:plus(q,p)>>;
          %  bfzp x is probably impossible?
       return q end;

symbolic procedure crlog!* u;
   mkcr(rdlog!* crnorm!* u,crarg!* u);

symbolic procedure crlog10!* u; cr!:quotient(crlog!* u,crlog!* i2cr!* 10);

symbolic procedure crlogb!*(u,b); cr!:quotient(crlog!* u,crlog!* b);

symbolic procedure timesi!* u; cr!:times(cri!*(),u);

symbolic procedure crasin!* u; cr!:minus timesi!* crasinh!* timesi!* u;

% symbolic procedure crasind!* u;
%    crr2d!* cr!:minus timesi!* crasinh!* timesi!* u;

symbolic procedure cracos!* u;
   cr!:plus(cr!:times(crhalf!*(),crpi!*()),
      timesi!* crasinh!* timesi!* u);

% symbolic procedure cracosd!* u;
%    crr2d!* cr!:plus(cr!:times(crhalf!*(),crpi!*()),
%       timesi!* crasinh!* timesi!* u);

symbolic procedure cratan!* u;
   cr!:times(cri!/2(),crlog!* cr!:quotient(
      cr!:plus(cri!*(),u),cr!:differ(cri!*(),u)));

% symbolic procedure cratand!* u;
%    crr2d!* cr!:times(cri!/2(),crlog!* cr!:quotient(
%       cr!:plus(cri!*(),u),cr!:differ(cri!*(),u)));

symbolic procedure cracot!* u;
   cr!:times(cri!/2(),crlog!* cr!:quotient(
      cr!:differ(u,cri!*()),cr!:plus(cri!*(),u)));

% symbolic procedure cracotd!* u;
%    crr2d!* cr!:times(cri!/2(),crlog!* cr!:quotient(
%       cr!:differ(u,cri!*()),cr!:plus(cri!*(),u)));

symbolic procedure crsec!* u; cr!:quotient(i2cr!* 1,crcos!* u);

% symbolic procedure crsecd!* u;
%    cr!:quotient(i2cr!* 1,crcos!* crd2r!* u);

symbolic procedure crcsc!* u; cr!:quotient(i2cr!* 1,crsin!* u);

% symbolic procedure crcscd!* u;
%   cr!:quotient(i2cr!* 1,crsin!* crd2r!* u);

symbolic procedure crasec!* u; cracos!* cr!:quotient(i2cr!* 1,u);

% symbolic procedure crasecd!* u;
%   crr2d!* cracos!* cr!:quotient(i2cr!* 1,u);

symbolic procedure cracsc!* u; crasin!* cr!:quotient(i2cr!* 1,u);

% symbolic procedure cracscd!* u;
%   crr2d!* crasin!* cr!:quotient(i2cr!* 1,u);

symbolic procedure crsinh!* u;
   cr!:times(crhalf!*(),cr!:differ(y,cr!:quotient(i2cr!* 1,y)))
   where y=crexp!* u;

symbolic procedure crcosh!* u;
   cr!:times(crhalf!*(),cr!:plus(y,cr!:quotient(i2cr!* 1,y)))
   where y=crexp!* u;

symbolic procedure crtanh!* u;
   cr!:quotient(cr!:differ(x,y),cr!:plus(x,y))
   where x=crexp!*(cr!:times(i2cr!* 2,u)),y=i2cr!* 1;

symbolic procedure crcoth!* u;
   cr!:quotient(cr!:plus(x,y),cr!:differ(x,y))
   where x=crexp!*(cr!:times(i2cr!* 2,u)),y=i2cr!* 1;

symbolic procedure crsech!* u;
   cr!:quotient(i2cr!* 2,cr!:plus(y,cr!:quotient(i2cr!* 1,y)))
   where y=crexp!* u;

symbolic procedure crcsch!* u;
   cr!:quotient(i2cr!* 2,cr!:differ(y,cr!:quotient(i2cr!* 1,y)))
   where y=crexp!* u;

symbolic procedure crasinh!* u;
   crlog!* cr!:plus(u,
      if bflessp(round!* crnorm!* u,rdtwo!*())
         then crsqrt!* cr!:plus(i2cr!* 1,s)
         else cr!:times(u,
            crsqrt!* cr!:plus(i2cr!* 1,cr!:quotient(i2cr!* 1,s))))
   where s=cr!:times(u,u);

symbolic procedure cracosh!* u;
   crlog!* cr!:plus(u,crsqrt!* cr!:differ(cr!:times(u,u),i2cr!* 1));

symbolic procedure cratanh!* u;
   cr!:times(crhalf!*(),crlog!* cr!:quotient(cr!:plus(i2cr!* 1,u),
      cr!:differ(i2cr!* 1,u)));

symbolic procedure cracoth!* u;
   cr!:times(crhalf!*(),crlog!* cr!:quotient(cr!:plus(i2cr!* 1,u),
      cr!:differ(u,i2cr!* 1)));

symbolic procedure crasech!* u; cracosh!* cr!:quotient(i2cr!* 1,u);

symbolic procedure cracsch!* u; crasinh!* cr!:quotient(i2cr!* 1,u);

symbolic procedure crexp!* u;
   <<u := tagim u; mkcr(rd!:times(r,rdcos!* u),rd!:times(r,rdsin!* u))>>
   where r=rdexp!* tagrl u;

endmodule;

end;
