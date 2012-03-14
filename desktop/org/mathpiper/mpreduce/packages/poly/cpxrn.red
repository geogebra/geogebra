module cpxrn; % *** Support for Complex Rationals.

% Authors: Anthony C. Hearn and Stanley L. Kameny.

% Copyright (c) 1989 The RAND Corporation.  All rights reserved.

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


Comment this module defines a complex rational as:

      (<tag>. (<structure> . <structure>>).

The <tag> is '!:crn!: and the <structure> is (n . d) where n and d are
integers;

fluid '(!:prec!:);

global '(bfone!* epsqrt!*);

fluid '(dmode!* !*bfspace !*numval);

switch bfspace,numval; !*bfspace := !*numval := t;

global '(domainlist!*);

domainlist!* := union('(!:crn!:),domainlist!*);

fluid '(!*complex!-rational);

put('complex!-rational,'tag,'!:crn!:);
put('!:crn!:,'dname,'complex!-rational);
flag('(!:crn!:),'field);
put('!:crn!:,'i2d,'i2crn!*);
put('!:crn!:,'plus,'crn!:plus);
put('!:crn!:,'times,'crn!:times);
put('!:crn!:,'difference,'crn!:differ);
put('!:crn!:,'quotient,'crn!:quotient);
put('!:crn!:,'zerop,'crn!:zerop);
put('!:crn!:,'onep,'crn!:onep);
put('!:crn!:,'prepfn,'crn!:prep);
put('!:crn!:,'prifn,'crn!:prin);
put('!:crn!:,'minus,'crn!:minus);
put('!:crn!:,'factorfn,'crn!:factor);
put('!:crn!:,'rationalizefn,'girationalize!:);
put('!:crn!:,'!:rn!:,'!*crn2rn);
put('!:rn!:,'!:crn!:,'!*rn2crn);
put('!:rd!:,'!:crn!:,'!*rd2crn);
put('!:crn!:,'!:rd!:,'!*crn2rd);
put('!:gi!:,'!:crn!:,'!*gi2crn);
put('!:crn!:,'cmpxfn,'mkcrn);
put('!:crn!:,'ivalue,'mkdcrn);
put('!:crn!:,'intequivfn,'crnequiv);
put('!:crn!:,'realtype,'!:rn!:);
put('!:rn!:,'cmpxtype,'!:crn!:);

put('!:crn!:,'minusp,'crn!:minusp);

symbolic procedure crn!:minusp u; caddr u=0 and minusp caadr u;

symbolic procedure mkcrn(u,v); '!:crn!: . u . v;

symbolic smacro procedure crntag x; '!:crn!: . x;

symbolic smacro procedure rntag x; '!:rn!: . x;

symbolic smacro procedure crnrl x; cadr x;

symbolic smacro procedure crnim x; cddr x;

symbolic procedure crn!:simp u; (crntag u) ./ 1;

put('!:crn!:,'simpfn,'crn!:simp);

symbolic procedure mkdcrn u;
   ('!:crn!: . ((0 . 1) . (1 . 1))) ./ 1;

symbolic procedure i2crn!* u; mkcrn(u . 1,0 . 1);
   %converts integer U to tagged crn form.

symbolic procedure !*crn2rn n;
   % Converts a crn number n into a rational if possible.
   if not(car crnim n=0) then cr2rderr() else '!:rn!: . crnrl n;

symbolic procedure !*rn2crn u; mkcrn(cdr u,0 . 1);
   % Converts the (tagged) rational u/v into a (tagged) crn.

symbolic procedure !*crn2rd n;
   if not(car crnim n=0) then cr2rderr() else
     mkround chkrn!* r2bf crnrl n;

symbolic procedure !*rd2crn u; mkcrn(realrat x,0 . 1) where x=round!* u;

symbolic procedure !*gi2crn u; mkcrn((cadr u) . 1,(cddr u) . 1);

symbolic procedure crn!:plus(u,v);
   mkcrn(cdr rnplus!:(rntag crnrl u,rntag crnrl v),
         cdr rnplus!:(rntag crnim u,rntag crnim v));

symbolic procedure crn!:differ(u,v);
   mkcrn(cdr rndifference!:(rntag crnrl u,rntag crnrl v),
         cdr rndifference!:(rntag crnim u,rntag crnim v));

symbolic procedure crn!:times(u,v);
   mkcrn(cdr rndifference!:(rntimes!:(ru,rv),rntimes!:(iu,iv)),
         cdr rnplus!:(rntimes!:(ru,iv),rntimes!:(rv,iu)))
   where ru=rntag crnrl u,iu=rntag crnim u,
         rv=rntag crnrl v,iv=rntag crnim v;

symbolic procedure crn!:quotient(u,v);
 <<v := rnplus!:(rntimes!:(rv,rv),rntimes!:(iv,iv));
  mkcrn(cdr rnquotient!:(rnplus!:(rntimes!:(ru,rv),rntimes!:(iu,iv)),v),
cdr rnquotient!:(rndifference!:(rntimes!:(iu,rv),rntimes!:(ru,iv)),v))>>
  where ru=rntag crnrl u,iu=rntag crnim u,
        rv=rntag crnrl v,iv=rntag crnim v;

symbolic procedure crn!:minus u;
   mkcrn((-car ru) . cdr ru,(-car iu) . cdr iu)
   where ru=crnrl u,iu=crnim u;

symbolic procedure crn!:zerop u; car crnrl u=0 and car crnim u=0;

symbolic procedure crn!:onep u;  car crnim u=0 and crnrl u='(1 . 1);

symbolic procedure crn!:prep u;
   crnprep1((rntag crnrl u) . rntag crnim u);

symbolic procedure crn!:factor u;
   (begin scalar m,n,p,x,y;
     setdmode('rational,nil) where !*msg = nil;
     x := subf(u,nil);
     y := fctrf numr x;
     n := car y;
     setdmode('rational,t) where !*msg = nil;
     y := for each j in cdr y collect
           <<p := numr subf(car j,nil);
             n := multd(n,m := exptf(lnc ckrn p,cdr j));
             quotfd(p,m) . cdr j>>;
     return int!-equiv!-chk quotfd(n,denr x) . y
   end) where dmode!*=dmode!*;

symbolic procedure crnprimp u;
   if rnonep!: u then 'i
   else if rnonep!: rnminus!: u then list('minus,'i)
   else list('times,rnprep!: u,'i);

symbolic procedure crnprep1 u;
   if rnzerop!: cdr u then rnprep!: car u
   else if rnzerop!: car u then crnprimp cdr u
   else if rnminusp!: cdr u
      then list('difference,rnprep!: car u,crnprimp rnminus!: cdr u)
   else list('plus,rnprep!: car u,crnprimp cdr u);

symbolic procedure crn!:prin u;
   (if atom v or car v eq 'times or car v memq domainlist!*
      then maprin v
    else <<prin2!* "("; maprin v; prin2!* ")">>) where v=crn!:prep u;

symbolic procedure crnequiv u;
   % Returns an equivalent integer if possible.
   if cadr(u := cdr u) = 0 and cdar u = 1 then caar u else nil;

initdmode 'complex!-rational;

endmodule;

end;
