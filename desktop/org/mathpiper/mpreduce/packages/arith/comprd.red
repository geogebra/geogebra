module comprd; % *** Support for Complex Rounded Arithmetic.

% Authors: Anthony C. Hearn and Stanley L. Kameny.

% Last updated: 23 June 1993.

% Copyright (c) 1989, 1993 The RAND Corporation.  All rights reserved.

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


Comment this module defines a complex rounded as:

  (<tag>. (<structure> . <structure>>)  or  ('!:cr!: . (rl . im))

The <structure> depends on the precision.  It is either a floating point
number or the stripped bfloat (mt . ep);


exports !*cr2crn, !*cr2rd, !*cr2rn, !*crn2cr, !*gi2cr, !*rd2cr,
        !*rn2cr, cr!:differ, cr!:minus, cr!:minusp, cr!:onep,
        cr!:plus, cr!:prep, cr!:prin, cr!:quotient, cr!:times,
        cr!:zerop, cr2i!*, crhalf!*, cri!*, cri!/2, crone!*, crprcd,
        gf2cr!:, i2cr!*, mkcr;

imports bfloat, bfnzp, bftrim!:, bfzp, chkint!*, chkrn!*, convprec,
        convprec!*, crim, crrl, ep!:, errorp, errorset!*,
        gf2bf, gfdiffer, gfminus, gfplus, gfquotient, gftimes,
        gfzerop, initdmode, leq, lessp!:, make!:cr, make!:rd, maprin,
        mkcrn, mkquote, mkrn, mkround, normbf, over, plubf, preci!:,
        prin2!*, r2bf, rd!:minus, rd!:minusp, rd!:onep, rd!:prep,
        rd!:zerop, rdprep1, rdhalf!*, rdone!*, rdqoterr, rdtwo!*,
        rdzchk, rdzero!*, realrat, retag, rndbfon, round!:mt,
        safe!-fp!-plus, safe!-fp!-times, timbf, union;

fluid '(!:prec!: !:bprec!:);

global '(bfone!* epsqrt!*);

fluid '(dmode!* !*bfspace !*numval !*roundbf !*!*roundbf);

global '(cr!-tolerance!* domainlist!* !!nfpd !!flprec !!rdprec bfz!*
         yy!!);

domainlist!* := union('(!:cr!:),domainlist!*);

fluid '(!*complex!-rounded);

put('complex!-rounded,'tag,'!:cr!:);
put('!:cr!:,'dname,'complex!-rounded);
flag('(!:cr!:),'field);
put('!:cr!:,'i2d,'i2cr!*);
put('!:cr!:,'plus,'cr!:plus);
put('!:cr!:,'times,'cr!:times);
put('!:cr!:,'difference,'cr!:differ);
put('!:cr!:,'quotient,'cr!:quotient);
put('!:cr!:,'zerop,'cr!:zerop);
put('!:cr!:,'onep,'cr!:onep);
put('!:cr!:,'prepfn,'cr!:prep);
put('!:cr!:,'prifn,'cr!:prin);
put('!:cr!:,'minus,'cr!:minus);
put('!:cr!:,'minusp,'cr!:minusp);
% put('!:cr!:,'rationalizefn,'girationalize!:);  % Needs something
                                                 % different.
put('!:cr!:,'!:rn!:,'!*cr2rn);
put('!:rn!:,'!:cr!:,'!*rn2cr);
put('!:rd!:,'!:cr!:,'!*rd2cr);
put('!:cr!:,'!:rd!:,'!*cr2rd);
put('!:cr!:,'!:crn!:,'!*cr2crn);
put('!:crn!:,'!:cr!:,'!*crn2cr);
put('!:gi!:,'!:cr!:,'!*gi2cr);
put('!:cr!:,'cmpxfn,'mkcr);
put('!:cr!:,'ivalue,'mkdcrn);
put('!:cr!:,'realtype,'!:rd!:);
put('!:rd!:,'cmpxtype,'!:cr!:);

symbolic procedure cr!:minusp u;
   (if atom x then zerop y and x<0 else zerop car y and car x<0)
    where x=cadr u,y=cddr u;

symbolic procedure striptag u; if atom u then u else cdr u;

symbolic procedure mkcr(u,v); make!:cr (striptag u, striptag v);

symbolic procedure gf2cr!: x;
   make!:cr (striptag car x, striptag cdr x);

symbolic procedure crprcd u;
   (rl . im) where rl=convprec!* crrl u,im=convprec!* crim u;

symbolic procedure crprcd2(x,y);
   <<x := crprcd x; yy!! := crprcd y; x>>;

% simp must call convprec!*, since precision may have changed.
symbolic procedure cr!:simp u; (gf2cr!: crprcd u) ./ 1;

put('!:cr!:,'simpfn,'cr!:simp);

%symbolic procedure mkdcr u; cri!*() ./ 1;

symbolic procedure i2cr!* u;
   %converts integer U to tagged cr form.
   <<u := chkint!* u; mkcr(u,if atom u then 0.0 else bfz!*)>>;

symbolic procedure trimcrrl n; trimcr crrl n;

symbolic procedure trimcr n;
   bftrim!: if atom n then bfloat n else retag n;

symbolic procedure cr2rderr;
   error(0,
   "complex to real type conversion requires zero imaginary part");

symbolic procedure !*cr2rn n;
   % Converts a cr number n into a rational if possible.
   if bfnzp retag crim n then cr2rderr() else
   <<n := realrat trimcrrl n; mkrn(car n,cdr n)>>;

symbolic procedure !*rn2cr u;
   % Converts the (tagged) rational u/v into a (tagged) rounded complex
   % number to the system precision.
   <<u := chkrn!* r2bf cdr u; mkcr(u,if atom u then 0.0 else bfz!*)>>;

symbolic procedure !*cr2crn u;
   % Converts a (tagged) cr number u into a (tagged) crn.
   mkcrn(realrat trimcrrl u,realrat trimcr crim u);

symbolic procedure !*crn2cr u;
   % Converts a (tagged) crn number u into a (tagged) cr.
    mkcr(rl,if !*roundbf then bfloat im else im)
     where rl=chkrn!* r2bf cadr u where im=chkrn!* r2bf cddr u;

symbolic procedure !*cr2rd n;
   if bfnzp retag crim n then cr2rderr() else make!:rd crrl n;

symbolic procedure !*rd2cr u;
   mkcr(x,if atom x then 0.0 else bfz!*) where x=convprec u;

symbolic procedure !*gi2cr u;
   mkcr(rl,if !*roundbf then bfloat im else im)
    where rl=chkint!* cadr u where im=chkint!* cddr u;

symbolic procedure bfrsq u;
   (if atom x then x*x+y*y else plubf(timbf(x,x),timbf(y,y)))
    where x=car u,y=cdr u;

symbolic procedure crzchk(u,x,y);
 begin
    if atom car u then
      if bfrsq u<(bfrsq x)*!!fleps2 then return 0.0 . 0.0 else go to ck;
    if lessp!:(bfrsq u,timbf(bfrsq x,cr!-tolerance!*)) then
      return bfz!* . bfz!*;
ck: return rdzchk(car u,car x,car y) . rdzchk(cdr u,cdr x,cdr y) end;

symbolic procedure cr!:plus(u,v);
 begin scalar x,y; x := crprcd2(u,v); y := yy!!;
   u := if !*!*roundbf then gfplus(x,y)
        else if (v := safe!-crfp!-plus(x,y)) then v else
     ((if errorp r then
       <<rndbfon(); gfplus(x := gf2bf x,y := gf2bf y)>> else car r)
     where r=errorset(list('gfplus,mkquote x,mkquote y),nil,nil));
   return gf2cr!: crzchk(u,x,y) end;

symbolic procedure cr!:differ(u,v);
 begin scalar x,y; x := crprcd2(u,v); y := yy!!;
   u := if !*!*roundbf then gfdiffer(x,y)
        else if (v := safe!-crfp!-diff(x,y)) then v else
     ((if errorp r then
       <<rndbfon(); gfplus(x := gf2bf x,y := gf2bf y)>> else car r)
     where r=errorset(list('gfdiffer,mkquote x,mkquote y),nil,nil));
   return gf2cr!: crzchk(u,x,gfminus y) end;

symbolic procedure cr!:times(u,v);
 gf2cr!:
 (if !*!*roundbf then gftimes(x,yy!!)
  else if (u := safe!-crfp!-times(x,yy!!)) then u else
 ((if errorp r then <<rndbfon(); gftimes(gf2bf x,gf2bf yy!!)>>
      else car r)
    where r=errorset!*(list('gftimes,mkquote x,mkquote yy!!),nil)))
   where x=crprcd2(u,v);

symbolic procedure cr!:quotient(u,v);
 gf2cr!:
 (if gfzerop yy!! then rdqoterr()
  else if !*!*roundbf then gfquotient(x,yy!!)
  else if (u := safe!-crfp!-quot(x,yy!!)) then u else
 ((if errorp r then
      <<rndbfon(); gfquotient(gf2bf x,gf2bf yy!!)>> else car r)
    where r=errorset!*(list('gfquotient,mkquote x,mkquote yy!!),nil)))
   where x=crprcd2(u,v);

symbolic procedure safe!-crfp!-plus(u,v);
   (if x and y then crzchk(x . y,u,v))
   where x=safe!-fp!-plus(car u,car v),y=safe!-fp!-plus(cdr u,cdr v);

symbolic procedure safe!-crfp!-diff(u,v);
   (if x and y then crzchk(x . y,u,gfminus v))
   where x=safe!-fp!-plus(car u,-car v),y=safe!-fp!-plus(cdr u,-cdr v);

symbolic procedure safe!-crfp!-times(u,v);
   begin scalar ru,iu,rv,iv,a,b;
      ru := car u; iu := cdr u; rv := car v; iv := cdr v;
      if not (a := safe!-fp!-times(ru,rv)) or
         not (b := safe!-fp!-times(iu,iv)) then return nil;
      if not(u := safe!-fp!-plus(a,-b)) then return nil;
      u := rdzchk(u,a,-b);
      if not (a := safe!-fp!-times(ru,iv)) or
         not (b := safe!-fp!-times(iu,rv)) then return nil;
      if not(v := safe!-fp!-plus(a,b)) then return nil;
      return u . rdzchk(v,a,b) end;

symbolic procedure safe!-crfp!-quot(u,v);
  % compute u * inverse v.
   begin scalar ru,iu,rv,iv,a,b,dd;
      ru := car u; iu := cdr u; rv := car v; iv := cdr v;
      if not (a := safe!-fp!-times(rv,rv)) or
         not (b := safe!-fp!-times(iv,iv)) or
         not (dd := safe!-fp!-plus(a,b)) then return nil;
      rv := rv/dd; iv := iv/dd;
      if not (a := safe!-fp!-times(ru,rv)) or
         not (b := safe!-fp!-times(iu,iv)) or
         not (u := safe!-fp!-plus(a,b)) then return nil;
      u := rdzchk(u,a,b);
      if not (a := safe!-fp!-times(ru,-iv)) or
         not (b := safe!-fp!-times(iu,rv)) or
         not (v := safe!-fp!-plus(a,b)) then return nil;
      return u . rdzchk(v,a,b) end;

symbolic procedure cr!:minus u; gf2cr!: gfminus crprcd u;

symbolic procedure cr!:zerop u;
   bfzp retag crrl u and bfzp retag crim u;

symbolic procedure cr!:onep u;
   bfzp retag crim u and rd!:onep mkround retag crrl u;

% prep works entirely in bfloat, to avoid floating point conversion
% errors.

symbolic procedure cr!:prep u;
   crprep1((rd!:prep tagrl u) . rd!:prep tagim u);

symbolic procedure crprep1 u;
 % a and d are 1,-1,or rounded.
  (if not numberp d and rd!:zerop d then a else
     <<d := if numberp d or not rd!:minusp d
        then crprimp d else {'minus,crprimp rd!:minus d};
       if not numberp a and rd!:zerop a then d else
         <<if a = -1 then a := {'minus,1}
             else if not numberp a and rd!:minusp a
               then a := {'minus,rd!:minus a};
           {'plus,a,d}>> >>) where a=car u,d=cdr u;

symbolic procedure crprimp u;
   if u=1 then 'i else if u= -1 then {'minus,'i} else {'times,u,'i};

symbolic procedure cr!:prin v;
   if atom (v := cr!:prep v)
     or car v eq 'times or car v memq domainlist!*
      then maprin v
    else <<prin2!* "("; maprin v; prin2!* ")">>;

initdmode 'complex!-rounded;

symbolic procedure crone!*; mkcr(rdone!*(),rdzero!*());

symbolic procedure crhalf!*; mkcr(rdhalf!*(),rdzero!*());

symbolic procedure cri!*; mkcr(rdzero!*(),rdone!*());

symbolic procedure cri!/2; mkcr(rdzero!*(),rdhalf!*());

symbolic procedure cr2i!*; mkcr(rdzero!*(),rdtwo!*());

endmodule;

end;
