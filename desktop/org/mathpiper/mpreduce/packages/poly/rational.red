module rational; % *** Tables for rational numbers ***.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


global '(domainlist!*);

switch rational;

domainlist!* := union('(!:rn!:),domainlist!*);
put('rational,'tag,'!:rn!:);
put('!:rn!:,'dname,'rational);
flag('(!:rn!:),'field);
put('!:rn!:,'i2d,'!*i2rn);
put('!:rn!:,'!:ft!:,'!*rn2ft);
put('!:rn!:,'minus,'rnminus!:);
put('!:rn!:,'minusp,'rnminusp!:);
put('!:rn!:,'plus,'rnplus!:);
put('!:rn!:,'times,'rntimes!:);
put('!:rn!:,'difference,'rndifference!:);
put('!:rn!:,'quotient,'rnquotient!:);
put('!:rn!:,'zerop,'rnzerop!:);
put('!:rn!:,'onep,'rnonep!:);
put('!:rn!:,'factorfn,'rnfactor!:);
put('!:rn!:,'expt,'rnexpt!:);
put('!:rn!:,'prepfn,'rnprep!:);
put('!:rn!:,'prifn,'rnprin);
put('!:rn!:,'intequivfn,'rnequiv);
put('!:rn!:,'rootfn,'rn!:root);
flag('(!:rn!:),'ratmode);

symbolic procedure rnexpt!:(u,n);
  % U is a tagged rational number, n an integer.
  begin scalar v;
     if n=0 then return 1;
     v:=cdr u;
     if (n<0) then <<
        n:=-n;
        if (car v < 0) then
           v:= (- cdr v) . (- car v)
           else v:= (cdr v) . (car v) >>;
     if (n=1) then return (car u) . v;
     return (car u) . ((car v ** n) . (cdr v ** n));
     % No more cancellation can take place in this exponentiation.
  end;

symbolic procedure mkratnum u;
   % U is a domain element. Value is equivalent real or complex
   % rational number.
   if atom u then !*i2rn u
    else if car u eq '!:gi!:
     then apply1(get('!:gi!:,'!:crn!:),u)
    else apply1(get(car u,'!:rn!:),u);

symbolic procedure mkrn(u,v);
   %converts two integers U and V into a rational number, an integer
   %or NIL;
   if v<0 then mkrn(-u,-v)
    else (lambda m; '!:rn!: . ((u/m) . (v/m))) gcdn(u,v);

symbolic procedure !*i2rn u;
   %converts integer U to rational number;
   '!:rn!: . (u . 1);

symbolic procedure rnminus!: u;
   % We must allow for a rational with structured arguments, since
   % lowest-terms can produce such objects.
   car u . !:minus cadr u . cddr u;

symbolic procedure rnminusp!: u;
   % We must allow for a rational with structured arguments, since
   % lowest-terms can produce such objects.
   if atom (u := cadr u) then u < 0 else apply1(get(car u,'minusp),u);

symbolic procedure rnplus!:(u,v);
   mkrn(cadr u*cddr v+cddr u*cadr v,cddr u*cddr v);

symbolic procedure rntimes!:(u,v);
   mkrn(cadr u*cadr v,cddr u*cddr v);

symbolic procedure rndifference!:(u,v);
   mkrn(cadr u*cddr v-cddr u*cadr v,cddr u*cddr v);

symbolic procedure rnquotient!:(u,v);
   mkrn(cadr u*cddr v,cddr u*cadr v);

symbolic procedure rnzerop!: u; cadr u=0;

symbolic procedure rnonep!: u; cadr u=1 and cddr u=1;

symbolic procedure rnfactor!: u;
   begin scalar x,y,dmode!*; integer m,n;
     x := subf(u,nil);
     if not domainp denr x then return {1,(u . 1)};
        % Don't know what else to do.
     y := factorf numr x;
     n := car y;
     dmode!* := '!:rn!:;
     y := for each j in cdr y collect
           <<n := n*(m := (lnc ckrn car j)**cdr j);
             quotfd(car j,m) . cdr j>>;
     return int!-equiv!-chk mkrn(n,denr x) . y
   end;

symbolic procedure rnprep!: u;
   % PREPF is called on arguments, since the LOWEST-TERMS code in extout
   % can create rational objects with structured arguments.
   (if cddr u=1 then x else list('quotient,x,prepf cddr u))
    where x = prepf cadr u;

symbolic procedure rnprin u;
   <<prin2!* cadr u; prin2!* "/"; prin2!* cddr u>>;

symbolic procedure rnequiv u;
   % Returns an equivalent integer if possible.
   if cdr(u := cdr u)=1 then car u else nil;

symbolic procedure rn!:root(u,n);
   (if x eq 'failed or y eq 'failed then 'failed else mkrn(x,y))
    where x=rootxf(cadr u,n), y=rootxf(cddr u,n);

initdmode 'rational;

endmodule;

end;
