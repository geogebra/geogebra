module nssimp;  % Simplification functions for non-scalar quantities.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation. All rights reserved.

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


fluid '(!*div frlis!* subfg!*);

% Several inessential uses of ACONC, NCONC, and MAPping "JOIN". Latter
% not yet changed.

symbolic procedure nssimp(u,v);
   %U is a prefix expression involving non-commuting quantities.
   %V is the type of U.  Result is an expression of the form
   % SUM R(I)*PRODUCT M(I,J) where the R(I) are standard
   %quotients and the M(I,J) non-commuting expressions;
   %N. B: the products in M(I,J) are returned in reverse order
   %(to facilitate, e.g., matrix augmentation);
   begin scalar r,s,w,x,y,z;
        u := dsimp(u,v);
    a:  if null u then return z;
        w := car u;
    c:  if null w then go to d
         else if numberp(r := car w)
                or not(eqcar(r,'!*div) or
                       (if (s := getrtype r) eq 'yetunknowntype
                          then getrtype(r :=
                                        eval!-yetunknowntypeexpr(r,nil))
                         else s) eq v)
          then x := aconc!*(x,r)
         else y := aconc!*(y,r);
        w := cdr w;
        go to c;
    d:  if null y then go to er;
    e:  z := addns(((if null x then 1 ./ 1 else simptimes x) . y),z);
        u := cdr u;
        x := y:= nil;
        go to a;
    er: y := v;
        if idp car x
          then if not flagp(car x,get(y,'fn)) then redmsg(car x,y)
            else rerror(alg,30,list(y,x,"not set"))
         else if w := get(get(y,'tag),'i2d)
          then <<y := list apply1(w,1); go to e>>
         %to allow a scalar to be a 1 by 1 matrix;
         else msgpri(list("Missing",y,"in"),car x,nil,nil,t);
        put(car x,'rtype,y);
        y := list car x;
        x := cdr x;
        go to e
   end;

symbolic procedure dsimp(u,v);
   %result is a list of lists representing a sum of products;
   %N. B: symbols are in reverse order in product list;
   if numberp u then list list u
    else if atom u
     then (if x and subfg!* then dsimp(cadr x,v)
            else if flagp(u,'share) then dsimp(lispeval u,v)
            else <<flag(list u,'used!*); list list u>>)
      where x= get(u,'avalue)
    else if car u eq 'plus
     then for each j in cdr u join dsimp(j,v)
    else if car u eq 'difference
     then nconc!*(dsimp(cadr u,v),
                dsimp('minus . cddr u,v))
    else if car u eq 'minus
     then dsimptimes(list(-1,carx(cdr u,'dsimp)),v)
    else if car u eq 'times then dsimptimes(cdr u,v)
    else if car u eq 'quotient
     then dsimptimes(list(cadr u,list('recip,carx(cddr u,'dsimp))),v)
    else if not(getrtype u eq v) then list list u
    else if car u eq 'recip
     then list list list('!*div,carx(cdr u,'dsimp))
    else if car u eq 'expt then (lambda z;
       if not numberp z then errpri2(u,t)
        else if z<0
         then list list list('!*div,'times . nlist(cadr u,-z))
         else if z=0 then list list list('!*div,cadr u,1)
        else dsimptimes(nlist(cadr u,z),v))
      reval_without_mod caddr u
    else if flagp(car u,'noncommuting) then list list u
    else if arrayp car u
       then dsimp(getelv u,v)
    else (if x then dsimp(x,v)
           else ((if z then dsimp(z,v) else {{y}})
                  where z=opmtch y) where y=revop1 u)
          where x=opmtch u;

symbolic procedure dsimptimes(u,v);
   if null u then errach 'dsimptimes
    else if null cdr u then dsimp(car u,v)
    else (lambda j; for each k in dsimptimes(cdr u,v) join mappend(j,k))
          dsimp(car u,v);

symbolic procedure addns(u,v);
   if null v then list u
    else if cdr u=cdar v
       then (lambda x; % if null car x then cdr v else;
                         (x . cdr u) . cdr v)
       addsq(car u,caar v)
    else if ordp(cdr u,cdar v) then u . v
    else car v . addns(u,cdr v);

symbolic procedure getelx u;
   %to take care of free variables in LET statements;
   if smemqlp(frlis!*,cdr u) then nil
    else if null(u := getelv u) then 0
    else reval u;

endmodule;

end;
