module polineq; % Solve univariate polynomial inequality systems;

% Author: Herbert Melenk, ZIB Berlin.

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


% All rights reserved.

% Method: compute the real roots of all numerators and denominators
% and check the intervals between them.

global '(!!arbint);

if not get('arbreal,'simpfn) then mkop 'arbreal;

symbolic procedure polineqeval u;
 begin scalar w,x;
   w:=reval car u;
   if eqcar(w,'list) then w:=for each q in cdr w collect reval q
      else w:={w};
   if cdr u then x:=reval cadr u;
   if eqcar(x,'list) then if cddr x then typerr(x,"variable")
       else x:=cadr x;
   return polineq0(w,x);
  end;

symbolic procedure polineq0(ul,x);
  begin scalar b,n,d,l,w,wl,op,u,r,s,x,y,z;
   loop:
     u:=car ul; ul:=cdr ul;
     if not pairp u or not((op:=car u) memq '(geq greaterp leq lessp))
      then go to typerr;
     s:= s or op = 'greaterp or op = 'lessp;
     w:=simp if op='greaterp or op='geq
               then {'difference,cadr u,caddr u}
              else {'difference,caddr u,cadr u};
     wl := w.wl;
     y:=(not domainp numr w and mvar numr w) or
        (not domainp denr w and mvar denr w);
      % check for a polynomial in a free variable.
     if null y or x and x neq y
        or pairp y and (get(car y,'!:rd!:) or get(car y,'opmtch))
         then go to typerr;
     x:=y;
     n:= append(n,polineq!-realroots(numr w,x));
     d:= append(d,polineq!-realroots(denr w,x));
   if ul then go to loop;
   for each y in append(n,d) do if not(y member b) then b:=y.b;
   if null b then return if polineqcheck(wl,{x . 0})
         then {'list,{'equal,x,{'arbreal,!!arbint := !!arbint+1}}}
                   else '(list);
    b:=sort(b,'evallessp);
      % Create the intervals;
    while b do
    <<if null l then l:= {{{'difference,car b,1},nil,car b}};
      z:= if cdr b then {'quotient,{'plus,car b,cadr b},2}
            else {'plus,car b,1};
      l:={z,car b,if cdr b then cadr b}.l;
      b:=cdr b;
    >>;
      % check and collect the intervals;
    for each v in l do
    << if polineqcheck(wl,{x.car v}) then
       r:=(if null cadr v then
             {if s then 'lessp else 'leq, x, caddr v}
           else if null caddr v then
             {if s then 'greaterp else 'geq, x, cadr v}
           else {'equal,x, '!*interval!*.cdr v}) . r
    >>;
    return 'list.r;
typerr: rederr("wrong arguments for polynomial inequality solver");
  end;

symbolic procedure polineqcheck(wl,p);
   null wl or not minusf numr subsq(car wl,p)
                and polineqcheck(cdr wl,p);

symbolic procedure polineq!-realroots(u,x);
  % return real roots of u, if possible as rational numbers.
 if domainp u then nil else
 for each f in cdr fctrf u join
 <<f:= car f;
   if mvar f neq x then rederr "too many variables";
   if ldeg f = 1 then {reval{'quotient,prepf negf red f,prepf lc f}}
    else
   for each y in cdr realroots list prepf f collect caddr y
 >>;

endmodule;

end;
