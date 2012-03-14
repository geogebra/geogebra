module partitsf;

% Author: Eberhard Schruefer;

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


fluid '(alglist!* !*exp);

symbolic procedure partitop u;
   begin scalar x,alglist!*;
   return
   if atom u then if x := get(u,'avalue)
                     then partitsq!* simp!* cadr x
                   else if get!*fdeg u then mkupf u
                   else if numr(x := simp!* u)
                           then 1 .* x .+ nil
                   else nil
    else if x := get(car u,'partitfn)
            then if flagp(car u,'full) then apply1(x,u)
                  else apply1(x,cdr u)
    else if car u eq '!*sq then partitsq!* simp!* u
    else if car u eq 'plus then
            <<for each j in cdr u do
                x := addpf(partitop j,x); x>>
    else if car u eq 'minus then negpf partitop cadr u
    else if car u eq 'difference then
            addpf(partitop cadr u,
                  negpf partitop caddr u)
    else if car u eq 'times then
            <<x := partitop cadr u;
              for each j in cddr u do
                x := multpfs(x,partitop j);
              x>>
    else if car u eq 'quotient then
               multpfsq(partitop cadr u,simprecip cddr u)
    else if car u eq 'recip then
               1 .* simprecip cdr u .+ nil
    else if numr(x := simp!* u)
            then 1 .* x .+ nil
    else nil
  end;

symbolic procedure mkupf u;
   begin scalar x;
     x := mksq(u,1);
     return if null numr x then nil
             else if domainp numr x then 1 .* x .+ nil
             else if (denr x = 1) and (lc numr x = 1)
                     and null red numr x and null sfp mvar numr x
                     then !*k2pf mvar numr x
             else partitsq!* x
   end;


symbolic procedure partitsq(u,v);
   %U is a standardquotient. Result is a form in which expressions
   %satisfying the test v are distributed and the rest is kept
   %recursive. Leaves unexpanded structure if possible;
   (if null x then nil
     else if domainp x then 1 .* u .+ nil
     else addpsf(if sfp mvar x and apply1(v,mvar x)
                     then multpsf(exptpsf(partitsq(mvar x ./ 1,v),
                                          ldeg x),
                             partitsq(cancel(lc x ./ y),v))
                 else if null sfp mvar x and apply1(v,!*k2f mvar x)
                          then multpsf(!*p2f lpow x .* (1 ./ 1)  .+ nil,
                                       partitsq(cancel(lc x ./ y),v))
                 else multsqpsf(!*p2q lpow x,
                              partitsq(cancel(lc x ./ y),v)),
                partitsq(cancel(red x ./ y),v)))
    where x = numr u, y = denr u;


symbolic procedure exptpsf(u,n);
   begin scalar x;
    x := u;
    while (n := n-1) > 0 do x := multpsf(u,x);
   return x
   end;

symbolic procedure exptpf(u,n);
   begin scalar x;
    x := u;
    while (n := n-1) > 0 do x := multpfs(u,x);
   return x
   end;

symbolic procedure addpsf(u,v);
   if null u then v
    else if null v then u
    else if domainp ldpf u then addmpsf(u,v)
    else if domainp ldpf v then addmpsf(v,u)
    else if ldpf u = ldpf v then
       (lambda x,y;
        if null numr x then y else ldpf u .* x .+ y)
       (addsq(lc u,lc v),addpsf(red u,red v))
    else if ordpp(lpow ldpf u,lpow ldpf v) then lt u .+ addpsf(red u,v)
    else lt v .+ addpsf(u,red v);

symbolic procedure addpf(u,v);
   if null u then v
    else if null v then u
    else if ldpf u = 1 then addmpf(u,v)
    else if ldpf v = 1 then addmpf(v,u)
    else if ldpf u = ldpf v then
       (lambda x,y;
        if null numr x then y else ldpf u .* x .+ y)
       (addsq(lc u,lc v),addpf(red u,red v))
    else if ordop(ldpf u,ldpf v) then lt u .+ addpf(red u,v)
    else lt v .+ addpf(u,red v);

symbolic procedure addmpf(u,v);
   if null v then u
    else if ldpf v = 1 then 1 .* addsq(lc u,lc v) .+ nil
    else lt v .+ addmpf(u,red v);

symbolic procedure addmpsf(u,v);
   if null v then u else
   if domainp ldpf v then 1 .* addsq(multsq(ldpf u ./ 1,lc u),
                                     multsq(ldpf v ./ 1,lc v)) .+ nil
    else lt v .+ addmpsf(u,red v);

symbolic procedure multpsf(u,v);
   if null u or null v then nil
    else addpsf(addpsf(multtpsf(lt u,lt v),multpsf(red u,v)),
                multpsf(!*t2f lt u,red v));

symbolic procedure multpfs(u,v);
   if null u or null v then nil
    else if ldpf u = 1 then multsqpf(lc u,v)
    else if ldpf v = 1 then multpfsq(u,lc v)
    else addpf(addpf(multttpf(lt u,lt v),multpfs(red u,v)),
               multpfs(lt u .+ nil,red v));

symbolic procedure multttpf(u,v);
   if car u = 1 then car v .* multsq(tc u,tc v) .+ nil
    else if car v = 1 then car u .* multsq(tc u,tc v) .+ nil
    else rerror(excalc,10,"Illegal factor in pf");

symbolic procedure multpfsq(u,v);
   if null u or null numr v then nil
    else ldpf u .* multsq(lc u,v) .+ multpfsq(red u,v);

symbolic procedure multsqpf(u,v);
   if null v or null numr u then nil
    else ldpf v .* multsq(u,lc v) .+ multsqpf(u,red v);

symbolic procedure multtpsf(u,v);
   begin scalar x,xexp;
    xexp := !*exp;
    !*exp := t;
    x := if car u = 1 then car v
          else if car v = 1 then car u
          else multf(tpsf u,tpsf v);
    !*exp := xexp;
   return multsqpsf(multsq(tc u,tc v),x .* (1 ./ 1)  .+ nil)
   end;

symbolic procedure multsqpsf(u,v);
   if null numr u or null v then nil
    else ldpf v .* multsq(u,lc v) .+ multsqpsf(u,red v);

symbolic procedure repartit u;
   if null u then nil
    else addpf(multpfsq(partitop ldpf u,lc u),repartit red u);

symbolic procedure partitsq!* u;
   %U is a standardquotient. Partitfunction for *sq's.
   %Leaves unexpanded structure if possible;
   (if null x then nil
     else if domainp x then 1 .* u .+ nil
     else addpf(if sfp mvar x and sfexform1p lt mvar x
                     then multpfsq(exptpf(partitsq!*(mvar x ./ 1),
                                         ldeg x),
                                   cancel(lc x ./ y))
                 else if null sfp mvar x and deg!*form mvar x
                          then mvar x .* cancel(lc x ./ y) .+ nil
                 else multsqpf(!*p2q lpow x,partitsq!*(lc x ./ y)),
                partitsq!*(red x ./ y)))
    where x = numr u, y = denr u;

symbolic procedure sfexform1p u;
   (if sfp tvar u then sfexform1p lt tvar u
     else deg!*form tvar u)
   or (null domainp tc u and sfexform1p lt tc u);

symbolic procedure !*pf2sq u;
   begin scalar res;
     res := nil ./ 1;
     if null u then return res;
     for each j on u do
       res := addsq(multsq(if ldpf j = 1 then 1 ./ 1
                            else !*k2q ldpf j,lc j),res);
     return res
   end;

symbolic procedure mk!*sqpf u;
   if null u then nil
    else ldpf u .* mk!*sq lc u .+ mk!*sqpf red u;

symbolic procedure !*pfsq2pf u;
   if null u then nil
    else (lambda x;
          if numr x
             then ldpf u .* x .+ !*pfsq2pf red u
           else !*pfsq2pf red u)
          simp!* lc u;

endmodule;

end;
