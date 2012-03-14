module vardf;

% Author: Eberhard Schruefer.

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


fluid '(depl!* kord!*);

global '(keepl!* bndeq!*);

symbolic procedure simpvardf u;
   if indvarpf numr simp0 cadr u then mksq('vardf . u,1)
    else begin scalar b,r,v,w,x,y,z;
         v := !*a2k cadr u;
         if null cddr u
          then w := intern compress append(explode '!',
                           explode if atom v then v
                                    else car v)
          else w := caddr u;
         if null atom v then w := w . cdr v;
         putform(w,prepf deg!*form v);
         kord!* := append(list(w := !*a2k w),kord!*);
         if x := assoc(v,depl!*) then
            for each j in cdr x do depend1(w,j,t);
         x := varysq(simp!* car u,v,w);
         b := y := nil ./ 1;
          while x do
              if (z := mvar ldpf x) eq w then
                              <<y := addsq(lc x,y);
                                x := red x>>
               else if eqcar(z,'wedge) then
                        if cadr z eq w then
                           <<y := addsq(multsq(!*kk2q('wedge . cddr z),
                                               lc x),y);
                             x := red x>>
                         else if eqcar(cadr z,'d) then
                             <<y := addsq(simp list('wedge,list('d,
                                           list('times,'wedge . cddr z,
                                                 prepsq lc x))),y);
                               b := addsq(multsq(!*kk2q('wedge . w .
                                                       cddr z),lc x),
                                          b);
                               x := red x>>
                        else rerror(excalc,11,list("Wrong ordering ",z))
               else if eqcar(z,'partdf) then
                     <<r := reval list('innerprod,
                                        list('partdf,caddr z),
                                        prepsq lc x);
                       x := addpsf((if cdddr z then
                                      !*kk2f('partdf . w . cdddr z)
                                     else !*k2f w)
                                      .* negsq simp list('d,r)
                                      .+ nil,red x);
                       b := addsq(multsq(if cdddr z then
                                          !*kk2q('partdf . w . cdddr z)
                                          else !*k2q w,simp r),b)>>
               else << b := addsq(multsq(simp cadr z,lc x),b);
                       x := red x>>;
     kord!* := cdr kord!*;
     bndeq!* := mk!*sq b;
     return y
     end;

put('vardf,'simpfn,'simpvardf);

put('vardf,'rtypefn,'getrtypeor);

put('vardf,'partitfn,'partitvardf);

symbolic procedure partitvardf u;
   partitsq!* simpvardf u;

symbolic procedure varysq(u,v,w);
   multpsf(addpsf(varyf(numr u,v,w),
                  multpsf(1 .* u .+ nil,varyf(negf denr u,v,w))),
           1 .* (1 ./ denr u) .+ nil);

symbolic procedure varyf(u,v,w);
   if domainp u then nil
    else addpsf(addpsf(multpsf(1 .* !*p2q lpow u .+ nil,
                               varyf(lc u,v,w)),
                       multpsf(varyp(lpow u,v,w),
                               1 .* (lc u ./ 1) .+ nil)),
                varyf(red u,v,w));

symbolic procedure varyp(u,v,w);
   begin scalar x,z; integer n;
       n := cdr u;
       u := car u;
       if u eq v then z := !*k2f w .* (1 ./ 1) .+ nil
        else if atomf u then
                if x := assoc(u,keepl!*) then
                   begin scalar alglist!*;
                         z := varysq(simp0 cdr x,v,w)
                   end
                 else if null atom u and null atom v then
                         if u=v then !*k2f w .* (1 ./ 1) .+ nil
                          else nil
                 else if null atom v then nil
                 else if depends(u,v) then
                         z := !*k2f w .* simp list('partdf,u,v) .+ nil
                 else nil
        else if sfp u then z := varyf(u,v,w)
        else if car u eq '!*sq then z := varysq(cadr u,v,w)
        else if x := get(car u,dfn_prop u) then
                 for each j in
                    for each k in cdr u collect varysq(simp k,v,w)
                  do <<if j then
                        z := addpsf(multpsf(j,1 .* simp
                                     subla(pair(caar x,cdr u),cdar x)
                                   .+ nil),z);
                 x := cdr x>>
        else if x := get(car u,'varyfn) then z := apply3(x,cdr u,v,w)
        else if ndepends(u,v) then
                   z := !*k2f w .* simp list('partdf,u,v) .+ nil
        else nil;
   return if n=1 then z
           else multpsf(1 .* !*t2q((u to (n-1)) .* n) .+ nil,z)
   end;

symbolic procedure !*pf2psf(u,v);
   if null u then nil
    else if domainp u then multsq(u ./ 1,v)
    else !*k2f ldpf u .* multsq(lc u,v) .+ !*pf2psf(red u,v);

symbolic procedure varywedge(u,v,w);
   begin scalar x,y,z;
   x := list 'wedge;
   for each j on u do
     begin
       y := varysq(simp car j,v,w);
       a: if y then
           z := addpsf(if deg!*form w then
                       !*pf2psf(partitop append(x,prepf ldpf y . cdr j),
                                lc y)
                     else ldpf y .* multsq(1 ./ denr lc y,simp
                             append(x,prepf numr lc y . cdr j))
                             .+ nil,z);
          if y and (y := red y) then go to a;
       x := append(x,list car j);
     end;
   return z
   end;

put('wedge,'varyfn,'varywedge);

symbolic procedure varyexdf(u,v,w);
   begin scalar x;
    for each j on varysq(simp car u,v,w) do
      if j then
       x := addpsf(!*pf2psf(partitop list('d,mvar ldpf j),lc j),x);
   return x
   end;

put('d,'varyfn,'varyexdf);

symbolic procedure varyhodge(u,v,w);
   begin scalar x;
    for each j on varysq(simp car u,v,w) do
      if j then
       x := addpsf(!*pf2psf(partitop list('hodge,mvar ldpf j),lc j),x);
   return x
   end;

put('hodge,'varyfn,'varyhodge);

symbolic procedure varypartdf(u,v,w);
   begin scalar x;
    for each j on varysq(simp car u,v,w) do
      if j then
       x := addpsf(!*a2f('partdf . mvar ldpf j . cdr u) .* lc j .+ nil,
                   x);
   return x
   end;

put('partdf,'varyfn,'varypartdf);

symbolic procedure simpnoether u;
   if indvarpf numr simp0 caddr u then mksq('noether . u,1)
    else begin scalar x,y;
           simpvardf list(car u,cadr u);
           x := simp!* bndeq!*;
           y := intern compress append(explode '!',
                                       explode if atom cadr u
                                                  then cadr u
                                                else caadr u);
           if null atom cadr u then y := y . cdadr u;
           y := list(y . list('liedf,caddr u,cadr u));
           return addsq(multsq(subf(numr x,y),1 ./ denr x),
                        negsq simp list('innerprod,caddr u,car u))
         end;

put('noether,'simpfn,'simpnoether);

symbolic procedure noetherind u;
   caddr u;

put('noether,'indexfun,'noetherind);

put('noether,'rtypefn,'getrtypeor);

endmodule;

end;

