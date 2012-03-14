module partdf;   % Adaption of df module.

% Author: Eberhard Schruefer.
% Modifications by: David Hartley.

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


fluid '(alglist!* depl!* frlis!* posn!* subfg!* wtl!* fancy!-pos!*
        fancy!-line!*);

global '(naturalvector2framevector keepl!* !*product!-rule);

newtok '((!@) partdf);

symbolic procedure simppartdf0 u;
   begin scalar v;
     if null cdr u then
           if coordp(u := reval car u)
              and (v := atsoc(u,naturalvector2framevector))
              then return !*pf2sq !*pfsq2pf cdr v
            else return mksq(list('partdf,u),1);
     if null subfg!* or freeindp car u or freeindp cadr u
                     or (cddr u and freeindp caddr u)
           then return mksq('partdf . revlis u,1);
     v := cdr u;
     u := simp!* car u;
     for each j in v do
         u := partdfsq(u,!*a2k j);
     return u
   end;

put('partdf,'simpfn,'simppartdf);

put('partdf,'rtypefn,'getrtypeor);

put('partdf,'partitfn,'partitpartdf);

symbolic procedure partitpartdf u;
   if null cdr u then mknatvec !*a2k car u
    else 1 .* simppartdf0 u .+ nil;

symbolic procedure simppartdf u;
   !*pf2sq partitpartdf u;

symbolic procedure mknatvec u;
   begin scalar x,y;
     return if x := atsoc(u,naturalvector2framevector)
               then !*pfsq2pf cdr x
             else if x := opmtch(y := list('partdf,u))
               then partitop x
             else mkupf y
   end;

symbolic procedure partdfsq(u,v);
   multsq(addsq(partdff(numr u,v),
                  multsq(u,partdff(negf denr u,v))),
                 1 ./ denr u);

symbolic procedure partdff(u,v);
   if domainp u then nil ./ 1
    else addsq(if null !*product!-rule then partdft(lt u,v)
                else addsq(multpq(lpow u,partdff(lc u,v)),
                           multsq(partdfpow(lpow u,v),lc u ./ 1)),
                partdff(red u,v));

symbolic procedure partdft(u,v);
   begin scalar x,y;
   x := partdft1(!*t2q u,v);
   y := nil ./ 1;
   for each j on x do
     if null domainp ldpf j then
        y := addsq(multsq(if domainp lc ldpf j then
                             multsq(partdfpow(lpow ldpf j,v),
                                    lc ldpf j ./ 1)
                           else mksq(list('partdf,prepf ldpf j,v),1),
                          lc j),y);
   return y
   end;

symbolic procedure partdft1(u,v);
   (if null x then nil
     else if domainp x then 1 .* u .+ nil
     else addpsf(if sfp mvar x and numr partdfpow(lpow mvar x,v)
                     then multpsf(exptpsf(partdft1(mvar u ./ 1,v),
                                          ldeg x),
                             partdft1(cancel(lc x ./ y),v))
                 else if null sfp mvar x and numr partdfpow(lpow x,v)
                          then multpsf(!*p2f lpow x .* (1 ./ 1)  .+ nil,
                                       partdft1(cancel(lc x ./ y),v))
                 else multsqpsf(!*p2q lpow x,
                              partdft1(cancel(lc x ./ y),v)),
                partdft1(cancel(red x ./ y),v)))
    where x = numr u, y = denr u;

symbolic procedure partdfpow(u,v);
   begin scalar x,z; integer n;
       n := cdr u;
       u := car u;
       z := nil ./ 1;
       if u eq v then z := 1 ./ 1
        else if atomf u then
                if x := assoc(u,keepl!*) then
                       begin scalar alglist!*;
                         z := partdfsq(simp0 cdr x,v)
                       end
                 else if ndepends(if x := get(lid u,'varlist)
                                     then lid u . cdr x
                                   else lid u,v)
                      then z := mksq(list('partdf,u,v),1)
                 else return nil ./ 1
        else if sfp u then z := partdff(u,v)
        else if car u eq '!*sq then z := partdfsq(cadr u,v)
        else if x := get(car u,dfn_prop u) then
                 for each j in
                    for each k in cdr u collect partdfsq(simp k,v)
                  do <<if numr j then
                        z := addsq(multsq(j,simp
                                     subla(pair(caar x,cdr u),cdar x)),
                                   z);
                 x := cdr x>>
        else if car u eq 'partdf then
                if ndepends(lid cadr u,v) then
% Too restrictive...
%                   if assoc(list('partdf,cadr u,v),
%                            get('partdf,'kvalue)) then
%                       <<z := mksq(list('partdf,cadr u,v),1);
%                         for each j in cddr u do
%                             z := partdfsq(z,j)>>
% More general matching...
                   if x := partdfsplit(u,v,get('partdf,'kvalue)) then
                       <<z := mksq(car x,1);
                         for each j in cdr x do
                             z := partdfsq(z,j)>>
                    else
                       <<z := 'partdf . cadr u . ordn(v . cddr u);
                         z := if x := opmtch z then simp x
                               else mksq(z,1)>>
                 else return nil ./ 1;
       if x := atsoc(u,wtl!*) then z := multpq('k!* to (-cdr x),z);
       return if n=1 then z else multsq(!*t2q((u to (n-1)) .* n),z)
   end;

symbolic procedure partdfsplit(u,v,k);
   % u,v:kernel, k:alist -> partdfsplit:list of kernel.
   % Input u is (partdf f ...), v is kernel on which f depends, k is
   % kvalue list for partdf.  Result is nil unless some subderivative
   % of (partdf f ... v) is known, in which case, the kernel whose
   % derivative is known is the first return value and the remaining
   % variables form the rest.
   if null k then nil
   else if cadr caar k eq cadr u and
                 v memq cddr caar k and
           sublistp(delete(v,cddr caar k),cddr u) then
      caar k . listdiff(cddr u,delete(v,cddr caar k))
   else partdfsplit(u,v,cdr k);

symbolic procedure sublistp(x,y);
   % x,y:list -> sublistp:bool
   null x or car x member y and sublistp(cdr x,delete(car x,y));

symbolic procedure listdiff(x,y);
   % x,y:list -> listdiff:list
   if null y then x
   else if null x then nil
   else listdiff(delete(car y,x),cdr y);

symbolic procedure ndepends(u,v);
   if null u or numberp u or numberp v then nil
    else if u=v then u
    else if atom u and u memq frlis!* then t
    else if (lambda x; x and lndepends(cdr x,v)) assoc(u,depl!*)
     then t
    else if not atom u and idp car u and get(car u,'dname) then nil
    else if not atomf u
      and (lndepends(cdr u,v) or ndepends(car u,v)) then t
    else if atomf v or idp car v and get(car v,'dname) then nil
    else ndependsl(u,cdr v);

symbolic procedure lndepends(u,v);
   u and (ndepends(car u,v) or lndepends(cdr u,v));

symbolic procedure ndependsl(u,v);
   u and (ndepends(u,car v) or ndependsl(u,cdr v));

symbolic procedure partdfprn u;
    if null !*nat then <<prin2!* '!@;
                         prin2!* "(";
                         if cddr u then inprint('!*comma!*,0,cdr u)
                          else maprin cadr u;
                         prin2!* ")" >>
     else begin scalar y; integer l;
            l := flatsizec flatindxl cdr u+1;
            if l>(linelength nil-spare!*)-posn!* then terpri!* t;
            %avoids breaking of the operator over a line;
            y := ycoord!*;
            prin2!* '!@;
            ycoord!* :=  y - if (null cddr u and indexvp cadr u) or
                                (cddr u and indexvp caddr u) then 2
                              else 1;
                if ycoord!*<ymin!* then ymin!* := ycoord!*;
                if null cddr u then <<maprin cadr u;
                                     ycoord!* := y>>
                 else <<for each j on cddr u do
                          <<maprin car j;
                            if cdr j then prin2!* " ">>;
                        ycoord!* := y;
                        if atom cadr u then prin2!* cadr u
                         else <<prin2!* "(";
                                maprin cadr u;
                                prin2!* ")">>>>
          end;

put('partdf,'prifn,'partdfprn);

symbolic procedure indexvp u;
   null atom u and flagp(car u,'indexvar);

symbolic procedure xpartdfprn(u,l);
   fancy!-level(if null cddr u
                   then begin scalar w;
                          w := fancy!-prefix!-operator 'partial!-df;
                          if w eq 'failed then return 'failed;
                          return fancy!-print!-indexlist1(cdr u,'!_,nil)
                        end
                 else fancy!-dfpri0(car u . cadr u .
                            deradpdf cddr u,l,'partial!-df));

symbolic procedure deradpdf u;
   if null cdr u then u
    else begin scalar x;
           x := derad(car u,{cadr u});
           for each j in cddr u do x := derad(j,x);
           return x
         end;

put('partdf,'fancy!-pprifn,'xpartdfprn);

endmodule;

end;
