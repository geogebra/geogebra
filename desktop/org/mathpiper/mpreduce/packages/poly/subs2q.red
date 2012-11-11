module subs2q;  % Routines for substituting for powers.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation. All rights reserved.

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


fluid '(!*exp !*mcd !*structure !*sub2 alglist!* dmode!* frlis!*);

fluid '(powlis!* powlis1!*);

global '(!*resubs simpcount!* simplimit!*);

Comment If STRUCTURE is ON, then expressions like (a**(b/2))**2 are not
simplified, to allow some attempt at a structure theorem use, especially
in the integrator;

symbolic procedure subs2q u;
   % Perform power substitutions on u. Check whether substitions
   % on numerator and denominator change these before doing
   % quotient (to avoid undoing rationalization of denominator).
   ((if denr x=1 and denr y=1 and numr x=v and numr y=w then u
      else quotsq(x,y))
     where x=subs2f v, y=subs2f w)
    where v=numr u, w=denr u;

symbolic procedure subs2f u;
   begin scalar x;
        if simpcount!*>simplimit!*
         then <<simpcount!* := 0;
                rerror(poly,21,"Simplification recursion too deep")>>;
        simpcount!* := simpcount!*+1;
        !*sub2 := nil;
        x := subs2f1 u;
        if (!*sub2 or powlis1!*) and !*resubs
           then if numr x=u and denr x=1 then !*sub2 := nil
                else x := subs2q x;
        simpcount!* := simpcount!*-1;
        return x
   end;

symbolic procedure subs2f1 u;
   if domainp u then !*d2q u
    else begin scalar kern,v,w,x,y,z,stack;
    s:  v := w := x := y := nil;
        kern := mvar u;
        z := nil ./ 1;
    a:  if null u or degr(u,kern)=0 then go to a1;
        y := lt u .+ y;
        u := red u;
        go to a;
    a1: x := powlis!*;
    a2: if null x then go to b
         else if caaar y = caar x
          then <<w := subs2p(caar y,cadar x,cadddr car x); go to e1>>
%        else if eqcar(kern,'sqrt) and cadr kern = caar x
%         then <<w := raddsq(subs2p(cadr kern . cdaar y,
%                            cadar x,cadddr car x),2);% go to e1>>;
         else if eqcar(kern,'expt)
                and cadr kern = caar x
                and eqcar(caddr kern,'quotient)
                and cadr caddr kern = 1
                and numberp caddr caddr kern
          then <<v := divide(cdaar y,caddr caddr kern);
%       if car v neq 0 then w := mksq(cadr kern,car v)
        % Use simp/exptsq to make sure I converted in complex mode.
        if car v neq 0 then w := exptsq(simp cadr kern,car v)
                  else w := 1 ./ 1;
                 if cdr v neq 0
                   then <<begin scalar alglist!*,dmode!*;
                          % We must do exponent arithmetic in integer
                          % mode.
                             v := cancel(cdr v.caddr caddr kern)
                          end;
                         w := multsq(raddsq(subs2p(cadr kern . car v,
                                        cadar x,cadddr car x),
                                cdr v),w)>>;
                 go to e1>>;
        x := cdr x;
        go to a2;
    b:  x := powlis1!*;
    l2: if null x then go to l3
         else if w:= mtchp(caar y,caar x,caddar x,caadar x,cdadar x)
          then go to e1;
        x := cdr x;
        go to l2;
    l3: if eqcar(kern,'expt) and not !*structure then go to l1;
        z := addsq(multpq(caar y,subs2f1 cdar y),z);
    c:  y := cdr y;
        if y then go to a1;
    d:  if domainp u then <<
          y := !*d2q u;
          go to x >>;
        stack := z . stack;
        go to s;
    x:  % mkprod checks structure in "constant" term.
        if null !*exp then y := mkprod numr y ./ mkprod denr y;
        y := addsq(z,y);
        if stack then <<
          z := car stack;
          stack := cdr stack;
          go to x >>; 
        return y;
    e1: z := addsq(multsq(w,subs2f1 cdar y),z);
        go to c;
    l1: if cdaar y=1 and not eqcar(cadr kern,'expt)     % ONEP
          then w := mksq(kern,1)
         else w := simpexpt list(cadr kern,
                                 list('times,caddr kern,cdaar y));
        z := addsq(multsq(w,subs2f1 cdar y),z);
        y := cdr y;
        if y then go to l1 else go to d;
    end;

symbolic procedure subs2p(u,v,w);
   % U is a power, V an integer, and W an algebraic expression, such
   % that CAR U**V=W. Value is standard quotient for U with this
   % substitution.
   begin
      if not fixp cdr u or car(v := divide(cdr u,v))=0
        then return !*p2q u;
      w := exptsq(simp w,car v);
      return if cdr v=0 then w else multpq(car u .** cdr v,w)
   end;

symbolic procedure raddsq(u,n);
   %U is a standard quotient, N and integer. Value is sq for U**(1/N);
   simpexpt list(mk!*sq u,list('quotient,1,n));

symbolic procedure mtchp(u,v,w,flg,bool);
   %U is a standard power, V a power to be matched against.
   %W is the replacement expression.
   %FLG is a flag which is T if an exact power match required.
   %BOOL is a boolean expression to be satisfied for substitution.
   %Value is the substitution standard quotient if a match found,
   %NIL otherwise;
   begin scalar x;
        x := mtchp1(u,v,flg,bool);
    a:  if null x then return nil
         else if lispeval subla(car x,bool) then go to b;
        x := cdr x;
        go to a;
    b:  v := divide(cdr u,subla(car x,cdr v));
        w := exptsq(simp subla(car x,w),car v);
        if cdr v neq 0 then w := multpq(car u .** cdr v,w);
        return w
   end;

symbolic procedure mtchp1(u,v,flg,bool);
   %U is a standard power, V a power to be matched against.
   %FLG is a flag which is T if an exact power match required.
   %BOOL is a boolean expression to be satisfied for substitution.
   %Value is a list of possible free variable pairings which
   %match conditions;
   begin scalar x;
        if u=v then return list nil
         else if not (x:= mchk!*(car u,car v)) then return nil
         else if cdr v memq frlis!*
             % do not match a free power to 1 or a conflicting match.
          then if cdr u=1 or not(x:= powmtch(cdr v,x,cdr u))
                 then return nil
                else return mapcons(x,cdr v . cdr u)
         else if (flg and not(cdr u=cdr v))
                or not numberp cdr v or not numberp cdr u
                or (if !*mcd then cdr u<cdr v
                     else (cdr u*cdr v)<0 or
                        %implements explicit sign matching;
                            abs cdr u<abs cdr v)
          then return nil
         else return x
   end;

symbolic procedure powmtch(u,v,w);
   % Match a free power u against list of pairings v for value w.
   % Note from ACH:  I have not yet found a case where this process
   % results in a match, even if a non-NIL value is returned.  An
   % example with this procedure being necessary would be appreciated.
   if null v then nil
    else (if null x or cdr x=w then car v . powmtch(u,cdr v,w)
           else powmtch(u,cdr v,w))
        where x=atsoc(u,car v);

symbolic procedure mchk!*(u,v);
   begin scalar x;
      if x := mchk(u,v) then return x
       else if !*mcd or not (sfp u and sfp v) then return nil
       else return mchk(prepf u,prepf v)
   end;

endmodule;

end;
