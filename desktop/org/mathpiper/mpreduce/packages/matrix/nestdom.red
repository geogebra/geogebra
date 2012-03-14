module nestdom; % nested domain: domain elements are standard quotients
                % coefficients are taken from the integers or another
                % dnest.

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


% Original version by Herbert Melenk, 1993(?)

% Improved version with Rainer mod.

% Changes to nestlevel, nestdmode and nestsq by Winfried Neun, 1998.


%%%%%%%%%
% Adaption to allow convertion between arnum and nested.
%%%%%%%%%
symbolic procedure ident(x);x;
put('!:ar!:,'!:nest!:,'ident);
%%%%%%%%%

% data structure:
%  a domain element is a list
%      ('!:nest!: level# dmode* . sq)

smacro procedure nestlevel u; if fixp u then 0 else cadr u;
smacro procedure nestdmode u; if fixp u then nil else caddr u;
smacro procedure nestsq u; if fixp u then simp u else cdddr u;

global '(domainlist!*);

fluid '(alglist!* nestlevel!*);
nestlevel!* := 0;

switch nested;

domainlist!* := union('(!:nest!:),domainlist!*);

put('nested,'tag,'!:nest!:);
put('!:nest!:,'dname,'nested);
flag('(!:nest!:),'field);
flag('(!:nest!:),'convert);
put('!:nest!:,'i2d,'!*i2nest);
%put('!:nest!:,'!:bf!:,'nestcnv);
%put('!:nest!:,'!:ft!:,'nestcnv);
%put('!:nest!:,'!:rn!:,'nestcnv);
put('!:nest!:,'!:bf!:,mkdmoderr('!:nest!:,'!:bf!:));
put('!:nest!:,'!:ft!:,mkdmoderr('!:nest!:,'!:ft!:));
put('!:nest!:,'!:rn!:,mkdmoderr('!:nest!:,'!:rn!:));
put('!:nest!:,'minusp,'nestminusp!:);
put('!:nest!:,'plus,'nestplus!:);
put('!:nest!:,'times,'nesttimes!:);
put('!:nest!:,'difference,'nestdifference!:);
put('!:nest!:,'quotient,'nestquotient!:);
put('!:nest!:,'divide,'nestdivide!:);
% put('!:nest!:,'gcd,'nestgcd!:);
put('!:nest!:,'zerop,'nestzerop!:);
put('!:nest!:,'onep,'nestonep!:);
% put('!:nest!:,'factorfn,'factornest!:);
put('!:nest!:,'prepfn,'nestprep!:);
put('!:nest!:,'prifn,'prin2);
put('!:rn!:,'!:nest!:,'rn2nest);

symbolic procedure !*i2nest u;
   %converts integer u to nested form;
   if domainp u then u else
   '!:nest!: . 0 . dmode!* . (u ./ 1);

symbolic procedure rn2nest u;
   %converts integer u to nested form;
   if domainp u then u else
   '!:nest!: . 0 . dmode!* . (cdr u);


symbolic procedure nestcnv u;
   rederr list("Conversion between `nested' and",
                get(car u,'dname),"not defined");

symbolic procedure nestminusp!: u;
         nestlevel u = 0 and minusf car nestsq u;

symbolic procedure sq2nestedf sq;
   '!:nest!: . nestlevel!* . dmode!* . sq;

symbolic procedure nest2op!:(u,v,op);
  (begin scalar r,nlu,nlv,nlr,dm,nestlevel!*;
     nlu := if not eqcar (u,'!:nest!:) then 0 else nestlevel u;
     nlv := if not eqcar (v,'!:nest!:) then 0 else nestlevel v;
     if nlu = nlv then goto case1
     else if nlu #> nlv then goto case2
     else goto case3;
   case1:    % same level for u and v
            dm := nestdmode u;
            if dm then setdmode(dm,t);
            nlr := nlu;
            nestlevel!* := nlu - 1;
            r := apply(op,list(nestsq u,nestsq v));
            goto ready;
   case2:    % v below u
            dm := nestdmode u;
            if dm then setdmode(dm,t);
            nlr := nlu;
            nestlevel!* := nlv;
            r := apply(op,list (nestsq u, v ./ 1));
            goto ready;
   case3:     % u below v
            dm := nestdmode v;
            if dm then setdmode(dm,t);
            nlr := nlv;
            nestlevel!* := nlu;
            r := apply(op,list (u ./ 1,nestsq v));
   ready:
            r := if null numr r then nil
            % The next line was commented out for a while, but is
            % needed for the normform tests.
                else if domainp numr r and denr r = 1 then numr r
                else '!:nest!: . nlr . dm . r;
            if dm then setdmode (dm,nil);
            return r;
   end )  where dmode!* = nil;

symbolic procedure nestplus!:(u,v); nest2op!:(u,v,'addsq);

symbolic procedure nesttimes!:(u,v); nest2op!:(u,v,'multsq);

symbolic procedure nestdifference!:(u,v);
    nest2op!:(u,v,function (lambda(x,y); addsq(x,negsq y)));

symbolic procedure nestdivide!:(u,v); nest2op!:(u,v,'quotsq) . 1;

% symbolic procedure nestgcd!:(u,v); !*i2nest 1;

symbolic procedure nestquotient!:(u,v); nest2op!:(u,v,'quotsq);

symbolic procedure nestzerop!: u; null numr nestsq u;

symbolic procedure nestonep!: u;
       (car v = 1 and cdr v = 1) where v = nestsq u;

initdmode 'nested;


% nested routines are defined in the gennest nestule with the exception
% of the following:

symbolic procedure setnest u;
   begin
      u := reval u;
      if not fixp u then typerr(u,"nestulus");
      nestlevel!* := u;
   end;

flag('(setnest),'opfn);   %to make it a symbolic operator;

flag('(setnest),'noval);

algebraic operator co;

symbolic procedure simpco u;
 % conmvert an expression to a nested coefficient
   begin scalar  sq,lev;
     if not (length u = 2 and fixp car u) then
             typerr(u,"nested coefficient");
     sq := simp cadr u;
     lev := car u;
     return (if null numr sq then nil else ('!:nest!: . lev . dmode!* .
                                                 sq)) ./ 1;
   end;

put('co,'simpfn,'simpco);

symbolic procedure nestprep!: u; list('co,nestlevel u,prepsq nestsq u);


endmodule;

end;
