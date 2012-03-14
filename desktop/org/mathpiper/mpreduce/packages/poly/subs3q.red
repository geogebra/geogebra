module subs3q; % Routines for matching products.

% Author: Anthony C. Hearn.

% Copyright (c) 1992 RAND.  All rights reserved.

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


fluid '(!*mcd powlis1!* !*sub2 subfg!*);

global '(!*match !*resubs mchfg!*);

symbolic procedure subs3q u;
   %U is a standard quotient.
   %Value is a standard quotient with all product substitutions made;
   begin scalar x;
        x := mchfg!*;   %save value in case we are in inner loop;
        mchfg!* := nil;
        u := quotsq(subs3f numr u,subs3f denr u);
        mchfg!* := x;
        return u
   end;

symbolic procedure subs3f u;
   %U is a standard form.
   %Value is a standard quotient with all product substitutions made;
   subs3f1(u,!*match,t);

symbolic procedure subs3f1(u,l,bool);
   %U is a standard form.
   %L is a list of possible matches.
   %BOOL is a boolean variable which is true if we are at top level.
   %Value is a standard quotient with all product substitutions made;
   begin scalar x,z;
        z := nil ./ 1;
    a:  if null u then return z
         else if domainp u then return addsq(z,u ./ 1)
         else if bool and domainp lc u then go to c;
        x := subs3t(lt u,l);
        if not bool                             %not top level;
         or not mchfg!* then go to b;           %no replacement made;
        mchfg!* := nil;
        if numr x = u and denr x = 1 then <<x := u ./ 1; go to b>>
         % also shows no replacement made (sometimes true with non
         % commuting expressions)
         else if null !*resubs then go to b
         else if !*sub2 or powlis1!* then x := subs2q x;
           %make another pass;
        x := subs3q x;
    b:  z := addsq(z,x);
        u := cdr u;
        go to a;
    c:  x := list lt u ./ 1;
        go to b
   end;

symbolic procedure subs3t(u,v);
   % U is a standard term, V a list of matching templates.
   % Value is a standard quotient for the substituted term.
   begin scalar bool,w,x,y,z;
        x := mtchk(car u,if domainp cdr u then sizchk(v,1) else v);
        if null x then go to a                  %lpow doesn't match;
         else if null caar x then go to b;      %complete match found;
        y := subs3f1(cdr u,x,nil);              %check tc for match;
        if mchfg!* then return multpq(car u,y);
    a:  return list u . 1;                      %no match;
    b:  x := cddar x;           %list(<subst value>,<denoms>);
        z := caadr x;           %leading denom;
        mchfg!* := nil;         %initialize for tc check;
        y := subs3f1(cdr u,!*match,nil);
        mchfg!* := t;
        if car z neq caar u then go to e
         else if z neq car u    %powers don't match;
          then y := multpq(caar u .** (cdar u-cdr z),y);
    b1: y := multsq(simpcar x,y);
        x := cdadr x;
        if null x then return y;
        z := 1;                 %unwind remaining denoms;
    c:  if null x then go to d;
        w:= if atom caar x or sfp caar x then caar x else
             ((lambda ww;
                if kernp ww and eqcar(ww := mvar numr ww,car caar x)
                  then ww
                 else revop1 caar x)
               (simp caar x) where subfg!* = nil);
        % In the non-commutative case we have to be very careful about
        % order of terms in a product. Introducing negative powers
        % solves this problem.
        if noncomp w or not !*mcd then bool := t;
%       z := multpf(mksp(w,if null bool then cdar x else -cdar x),z);
%       original line
        z := multf(z,!*p2f mksp(w,
                                if null bool then cdar x else -cdar x));
        % kernel CAAR X is not unique here. Earlier versions used just
        % CAAR X, but this leads to sums of terms in the wrong order.
        % The code here is probably still not correct in all cases, and
        % may lead to unbounded calculations. Maybe SIMP should be used
        % instead of REVOP1, with appropriate adjustments in the code
        % to construct Z.
        x := cdr x;
        go to c;
    d:  return if not bool then car y . multf(z,cdr y)
                else multf(z,car y) . cdr y;
    e:  if simp car z neq simp caar u then errach list('subs3t,u,x,z);
        %maybe arguments were in different order, otherwise it's fatal;
        if cdr z neq cdar u
          then y:= multpq(caar u .** (cdar u-cdr z),y);
        go to b1
   end;

symbolic procedure sizchk(u,n);
   if null u then nil
    else if length caar u>n then sizchk(cdr u,n)
    else car u . sizchk(cdr u,n);

symbolic procedure mtchk(u,v);
   %U is a standard power, V a list of matching templates.
   %If a match is made, value is of the form:
   %list list(NIL,<boolean form>,<subst value>,<denoms>),
   %otherwise value is an updated list of templates;
   begin scalar flg,v1,w,x,y,z, lastpairz;
        flg := noncomp car u;
    a0: if null v then return z;
        v1 := car v;
        w := car v1;
    a:  if null w then go to d;
        x := mtchp1(u,car w,caadr v1,cdadr v1);
    b:  if null x then go to c
         else if car (y := subla(car x,delete(car w,car v1))
                                . list(subla(car x,cadr v1),
                                      subla(car x,caddr v1),
                                      subla(car x,car w)
                                          . cadddr v1))
          then << z := y . z;
                  if null lastpairz then lastpairz := z >>
         else if lispeval subla(car x,cdadr v1) then return list y;
        x := cdr x;
        go to b;
    c:  if null flg then <<w := cdr w; go to a>>
         else if cadddr v1 and nocp w then go to e;
    d:  % z :=aconc(z,v1);   % Could also be append(z,list v1).
% The above use of nconc tended to be painful when list of matching
% templates was long, so I now track the end of the list and tack items
% on without any need to scan what is already there.
        if null z then z := lastpairz := list v1
        else << rplacd(lastpairz, list v1);
                lastpairz := cdr lastpairz >>;
    e:  v := cdr v;
        go to a0
   end;

symbolic procedure nocp u;
   null u or (noncomp caar u and nocp cdr u);

endmodule;

end;
