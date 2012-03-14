module nestdom; %
                % nested domain: domain elements are standard quotients.
                % Coefficients are taken from the integers or another
                % dnest.
                %
                % This module was written by H. Melenk.
                %

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


%%%%%%%%%
% Adaption to allow convertion between arnum and nested.
%%%%%%%%%
symbolic procedure ident(x);x;
PUT('!:ar!:,'!:nest!:,'ident);
%%%%%%%%%


% data structure:
%  a domain element is a list
%      ('!:nest!: level# dmode* . sq)

smacro procedure nestlevel u; cadr u;
smacro procedure nestdmode u; caddr u;
smacro procedure nestsq u; cdddr u;

GLOBAL '(DOMAINLIST!*);

FLUID '(alglist!* nestlevel!*);
nestlevel!* := 0;

switch nested;

DOMAINLIST!* := UNION('(!:nest!:),DOMAINLIST!*);

PUT('NESTED,'TAG,'!:nest!:);
PUT('!:nest!:,'DNAME,'NESTED);
FLAG('(!:nest!:),'FIELD);
FLAG('(!:nest!:),'CONVERT);
PUT('!:nest!:,'I2D,'!*I2nest);
% PUT('!:nest!:,'!:BF!:,'nestCNV);
% PUT('!:nest!:,'!:FT!:,'nestCNV);
% PUT('!:nest!:,'!:RN!:,'nestCNV);
PUT('!:nest!:,'!:BF!:,mkdmoderr('!:nest!:,'!:BF!:));
PUT('!:nest!:,'!:FT!:,mkdmoderr('!:nest!:,'!:ft!:));
PUT('!:nest!:,'!:RN!:,mkdmoderr('!:nest!:,'!:RN!:));
PUT('!:nest!:,'MINUSP,'nestMINUSP!:);
PUT('!:nest!:,'PLUS,'nestPLUS!:);
PUT('!:nest!:,'TIMES,'nestTIMES!:);
PUT('!:nest!:,'DIFFERENCE,'nestDIFFERENCE!:);
PUT('!:nest!:,'QUOTIENT,'nestQUOTIENT!:);
PUT('!:nest!:,'divide,'nestdivide!:);
% PUT('!:nest!:,'gcd,'nestgcd!:);
PUT('!:nest!:,'ZEROP,'nestZEROP!:);
PUT('!:nest!:,'ONEP,'nestONEP!:);
% PUT('!:nest!:,'factorfn,'factornest!:);
PUT('!:nest!:,'PREPFN,'nestPREP!:);
PUT('!:nest!:,'PRIFN,'PRIN2);
PUT('!:RN!:,'!:nest!:,'RN2nest);

SYMBOLIC PROCEDURE !*I2nest U;
   %converts integer U to nested form;
   if domainp u then u else
   '!:nest!: . 0 . dmode!* . (u ./ 1);

SYMBOLIC PROCEDURE RN2nest U;
   %converts integer U to nested form;
   if domainp u then u else
   '!:nest!: . 0 . dmode!* . (cdr u);

SYMBOLIC PROCEDURE nestCNV U;
   REDERR LIST("Conversion between `nested' and",
                GET(CAR U,'DNAME),"not defined");

SYMBOLIC PROCEDURE nestMINUSP!: U;
   nestlevel u = 0 and minusf car nestsq u;

SYMBOLIC PROCEDURE sq2nestedf sq;
  '!:nest!: . nestlevel!* . dmode!* . sq;

SYMBOLIC PROCEDURE nest2op!:(U,V,op);
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
            else if domainp numr r and denr r = 1 then numr r
            else '!:nest!: . nlr . dm . r;
     if dm then setdmode (dm,nil);
     return r;
    end )  where dmode!* = nil;

SYMBOLIC PROCEDURE nestPLUS!:(u,v); nest2op!:(u,v,'addsq);

SYMBOLIC PROCEDURE nestTIMES!:(U,V); nest2op!:(u,v,'multsq);

SYMBOLIC PROCEDURE nestDIFFERENCE!:(U,V);
    nest2op!:(u,v,function (lambda(x,y); addsq(x,negsq y)));

symbolic procedure nestdivide!:(u,v); nest2op!:(u,v,'quotsq) . 1;

%symbolic procedure nestgcd!:(u,v); !*i2nest 1;

SYMBOLIC PROCEDURE nestQUOTIENT!:(U,V); nest2op!:(u,v,'quotsq);

SYMBOLIC PROCEDURE nestZEROP!: U; null numr nestsq u;

SYMBOLIC PROCEDURE nestONEP!: U;
       (car v = 1 and cdr v = 1) where v = nestsq u;

INITDMODE 'nested;


% nested routines are defined in the GENnest nestule with the exception
% of the following:

SYMBOLIC PROCEDURE SETnest U;
   begin
      u := reval u;
      if not fixp u then typerr(u,"nestulus");
      nestlevel!* := u;
   end;

FLAG('(SETnest),'OPFN);   %to make it a symbolic operator;

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

symbolic procedure nestPREP!: u; list('co,nestlevel u,prepsq nestsq u);

endmodule;

end;

