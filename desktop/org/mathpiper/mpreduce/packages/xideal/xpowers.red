module xpowers;

% Powers, including div relation and lcm.

% Author: David Hartley

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


Comment.  Factor ordering within a product is decided using the current
kernel order.  Term ordering is decided by ordering of the valuation of
terms in the commutative monoid.  The valuation of a poly is simply the
list of factors in the leading power.  Monoid ordering can be either
lex or gradlex.  The div, // and lcm operations are performed within
the monoid.

Monoid elements are given by the type mon:

        mon ::= list of kernel | {1}

endcomment;

fluid '(xdegreelist!* xvarlist!*);


rlistat '(xorder);

symbolic procedure xorder u;
   if u = {nil} then compress pnth(explode get('wedge,'xorder),6)
   else if (idp(u := car u) or idp(u := reval u)) and
           getd mkid('xord_,u) then
      <<put('wedge,'xorder,mkid('xord_,u)); u>>
   else typerr(u,"xorder");


put('wedge,'xorder,'xord_deglex);


symbolic procedure xval f;
   % f:pf -> xval:mon
   wedgefax lpow f;


symbolic procedure pfordp(f,g);
   % f,g:pf -> pfordp:bool
   % partial ordering based on term ordering
   % returns t if f > g, otherwise nil (even when no ordering defined)
   if null f then nil
   else if null g then lpow f neq 1 % == termordp(lpow f,1)
   else if not(lpow f eq lpow g) then termordp(lpow f,lpow g)
   else pfordp(red f,red g);


symbolic procedure termordp(u,v);
   % u,v:lpow pf -> termordp:bool
   % returns t if u > v
   monordp(wedgefax u,wedgefax v);


symbolic procedure monordp(u,v);
   % u,v:mon -> monordp:bool
   % returns t if u > v
   apply2(get('wedge,'xorder),u,v);


symbolic procedure factorordp(u,v);
   % u,v:kernel -> factorordp:bool
   % same as worder, but with strict inequality
   % returns t if u > v
   if u eq v then nil
%%?   else if xvarlist!* then v memq (u memq xvarlist!*)
   else worderp(u,v);


symbolic procedure xord_lex(u,v);
   % u,v:mon -> xord_lex:bool
   if null u or car u = 1 then nil
   else if null v or car v = 1 then t
   else if car u eq car v then xord_lex(cdr u,cdr v)
   else factorordp(car u,car v);


symbolic procedure xord_gradlex(u,v);
   % u,v:mon -> xord_gradlex:bool
   if car u = 1 then nil
   else if car v = 1 then t
   else if length u = length v then xord_lex(u,v)
   else length u > length v;


symbolic procedure xord_deglex(u,v);
   % u,v:mon -> xord_deglex:bool
   if car u = 1 then nil
   else if car v = 1 then t
   else (if du = dv then xord_lex(u,v)
   else du > dv) where du = xdegreemon u,
                       dv = xdegreemon v;


symbolic procedure xdegreemon u;
   % u:mon -> xdegreemon:int
   % special degree routine for faster deglex ordering
   if null xdegreelist!* then xdegree mknwedge u
   else foreach k in u sum cdr atsoc(k,xdegreelist!*);


symbolic procedure xord_deggradlex(u,v);
   % u,v:mon -> xord_deggradlex:bool
   if car u = 1 then nil
   else if car v = 1 then t
   else (if du = dv then xord_gradlex(u,v)
   else du > dv) where du = xdegree mknwedge u,
                       dv = xdegree mknwedge v;


symbolic procedure xlcm(r,s);
   % r,s:mon -> xlcm:mon
   % lowest common multiple
   if null r or car r = 1 then s
   else if null s or car s = 1 then r
   else if car r eq car s then car r . xlcm(cdr r,cdr s)
   else if factorordp(car r,car s) then car r . xlcm(cdr r,s)
   else car s . xlcm(r,cdr s);


symbolic procedure xdiv(r,s);
   % r,s:mon -> xdiv:nil|mon
   % returns s//r if r div s, else nil
   if r = {1} then s
   else if sublistp(r,s) then
      if s := listdiff(s,r) then s else {1};


symbolic procedure listunion(x,y);
   % x,y:list -> listunion:list
   % A version of union which takes multiplicities into account.
   % If item z occurs m(x) times in x and m(y) times in y, then it
   % occurs max(m(x),m(y)) times in listunion(x,y). Ordering is x,(y\x).
   % NB. union({z,z},{z}) gives {z}, while union({z},{z,z}) gives {z,z}.
   if null x then y
   else if null y then x
   else car x . listunion(cdr x,
                         if car x member y then delete(car x,y) else y);


symbolic procedure sublistp(x,y);
   % x,y:list -> sublistp:bool
   null x or car x member y and sublistp(cdr x,delete(car x,y));


symbolic procedure listdiff(x,y);
   % x,y:list -> listdiff:list
   if null y then x
   else if null x then nil
   else listdiff(delete(car y,x),cdr y);

endmodule;

end;
