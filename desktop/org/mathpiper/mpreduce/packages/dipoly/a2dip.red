module a2dip;
% Convert an algebraic (prefix) form to distributive polynomial

% Authors: R. Gebauer, A. C. Hearn, H. Kredel

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

% Repeat of smacros defined in vdp2dip.

smacro procedure dipfmon(a,e);
   e . a . nil;

smacro procedure vevzero!? u;
   null u or(car u=0 and vevzero!?1 cdr u);

symbolic procedure a2dip u;
%     Converts the algebraic (prefix) form u to a distributive poly.
%     We assume that all variables used have been previously
%     defined in dipvars!*, but a check is also made for this
if atom u then a2dipatom u
    else if not atom car u or not idp car u
     then typerr(car u,"dipoly operator")
       % Handling expt separately because the exponents should
       % not be simplified as domain elements.
    else if car u='expt
     then if vevzero!? car a2dip cadr u and vevzero!? car a2dip caddr u
	    then dipfmon(simp!* u,evzero())
	   else dipfnpow(a2dip cadr u,caddr u)
    else (if x then apply(x,list for each y in cdr u collect a2dip y)
         else a2dipatom u)
          where x=get(car u,'dipfn);

symbolic procedure a2dipatom u;
%   Converts the atom (or kernel) u into a distributive polynomial
if u=0 then dipzero
    else if numberp u or not(u member dipvars!*)
      then dipfmon(a2bc u,evzero())
    else dipfmon(a2bc 1,mkexpvec u);

symbolic procedure dipfnsum u;
%    U is a list of dip expressions. Result is the distributive poly
%    representation for the sum
(<<for each y in cdr u do x:=dipsum(x,y);x>>)where x=car u;

put('plus,'dipfn,'dipfnsum);

symbolic procedure dipfnprod u;
%    U is a list of dip expressions. Result is the distributive poly
%    representation for the product
%    Maybe we should check for a zero
(<<for each y in cdr u do x:=dipprod(x,y);x>>)where x=car u;

put('times,'dipfn,'dipfnprod);

symbolic procedure dipfndif u;
%    U is a list of two dip expressions. Result is the distributive
%    polynomial representation for the difference
dipsum(car u,dipneg cadr u);

put('difference,'dipfn,'dipfndif);

symbolic procedure dipfnpow(v,n);
%  V is a dip. Result is the distributive poly v**n.
(if not fixp n or n<0
     then typerr(n,"distributive polynomial exponent")
    else if n=0 then if dipzero!? v then rerror(dipoly,1,"0**0 invalid")
                  else w
    else if dipzero!? v or n=1 then v
    else if dipzero!? dipmred v
     then dipfmon(bcpow(diplbc v,n),intevprod(n,dipevlmon v))
    else <<while n>0 do
         <<if not evenp n then w:=dipprod(w,v);
             n:=n/2;
           if n>0 then v:=dipprod(v,v)>>;
         w>>)
    where w:=dipfmon(a2bc 1,evzero());

% put('expt,'dipfn,'dipfnpow);

symbolic procedure dipfnneg u;
%    U is a list of one dip expression. Result is the distributive
%    polynomial representation for the negative
(if dipzero!? v then v
    else dipmoncomp(bcneg diplbc v,dipevlmon v,dipmred v))
    where v=car u;

put('minus,'dipfn,'dipfnneg);

symbolic procedure dipfnquot u;
%    U is a list of two dip expressions. Result is the distributive
%    polynomial representation for the quotient
if dipzero!? cadr u or not dipzero!? dipmred cadr u
       or not evzero!? dipevlmon cadr u
       or (!*vdpinteger and not bcone!? diplbc cadr u)
      then typerr(dip2a cadr u,"distributive polynomial denominator")
    else dipfnquot1(car u,diplbc cadr u);

symbolic procedure dipfnquot1(u,v);
if dipzero!? u then u
    else dipmoncomp(bcquot(diplbc u,v),
                dipevlmon u,
                dipfnquot1(dipmred u,v));

put('quotient,'dipfn,'dipfnquot);

endmodule;;end ;
