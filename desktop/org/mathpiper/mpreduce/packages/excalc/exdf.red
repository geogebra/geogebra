module exdf;

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


fluid '(subfg!*);

global '(naturalframe2coframe dbaseform2base2form basisforml!* dimex!*);

put('d,'simpfn,'simpexdf);

put('d,'rtypefn,'getrtypecar);

put('d,'partitfn,'partitexdf);

symbolic procedure partitexdf u;
   exdfpf partitop car u;

symbolic procedure simpexdf u;
   !*pf2sq partitexdf u;

symbolic procedure mkexdf u;
   begin scalar x,y;
     return if x := opmtch(y := list('d,u))
               then partitop x
             else mkupf y
   end;

symbolic procedure exdfpf u;
   if null u then nil
    else addpf(if ldpf u = 1
                  then exdf0 lc u
                else addpf(multpfsq(exdfk ldpf u,lc u),
                           mkuniquewedge wedgepf2(exdf0 lc u,
                                                  !*k2pf list ldpf u)),
               exdfpf red u);

symbolic procedure exdfk u;
   if u = 1 or eqcar(u,'d) or dim!<!=deg u
            or flagp(lid u,'closed) then nil
    else if flagp('d,'noxpnd) or lftshftp u then mkexdf u
    else if atomf u then
           if (not flagp('partdf,'noxpnd)) and
                   flagp(lid u,'impfun)
              then dimpfun(u,get!-impfun!-args lid u)
    else if coordp u then
            if subfg!*
               then !*pfsq2pf cdr atsoc(u,naturalframe2coframe)
             else mkexdf u
    else if basisformp u and dbaseform2base2form then
             !*pfsq2pf cdr atsoc(u,dbaseform2base2form)
          else mkexdf u
    else if (car u eq 'wedge) then dwedge cdr u
    else if car u memq '(hodge innerprod liedf) then mkexdf u
    else if car u eq 'partdf then
           if not flagp('partdf,'noxpnd) and atomf cadr u
              then dimpfun(u,get!-impfun!-args lid cadr u)
          else mkexdf u
    else begin scalar x,y,z;
           if null(x := get(car u,dfn_prop u)) then return mkexdf u;
           z := cdr u;
           for each j in
               for each k in z collect partitexdf list k do
            <<if j then
               y := addpf(multpfsq(j,simp subla(pair(caar x,z),cdar x)),
                          y);
              x := cdr x>>;
           return y
         end;

symbolic procedure lid u;
   if atom u then u else car u;

symbolic procedure atomf u;
   atom u or flagp(car u,'indexvar);

symbolic procedure dim!<!=deg u;
   (null x or (fixp x and x<=0))
    where x = addf(dimex!*,negf deg!*form u);

symbolic procedure dim!<deg u;
   begin scalar x;
     x := addf(dimex!*,negf deg!*farg u);
     return if numberp x and minusp x then t
             else nil
   end;

symbolic procedure dimpfun(u,v);
   if null v then nil
    else addpf(multpfsq(exdfp0(car v . 1),partdfsq(simp u,car v)),
               dimpfun(u,cdr v));

symbolic procedure exdf0 u;
   multpfsq(addpf(exdff0 numr u,multpfsq(exdff0 negf denr u,u)),
            1 ./ denr u);

symbolic procedure exdff0 u;
   if domainp u then nil
    else addpf(addpf(multsqpf(!*p2q lpow u,exdff0 lc u),
                     multpfsq(exdfp0 lpow u,lc u ./ 1)),
               exdff0 red u);

symbolic procedure exdfp0 u; %weighted vars ??
   begin scalar pv,n,z;
     pv := car u;
      n := pdeg u;
     return if (sfp pv or exformp pv or null subfg!*)
               and (z := if sfp pv then exdff0 pv
                          else exdfk pv)
               then if n = 1 then z
                     else multpfsq(z,!*t2q((pv to (n - 1)) .* n))
             else nil
   end;

symbolic procedure dwedge u;
   if flagp('d,'noxpnd)
      then noxpnddwedge partitwedge u
    else mkuniquewedge dwedge1(u,nil);

symbolic procedure noxpnddwedge u;
   if null u then nil
    else addpf(multpfsq(exdfk ldpf u,lc u),noxpnddwedge red u);

symbolic procedure dwedge1(u,v);
   if null rwf u
      then mkunarywedge multpfsq(exdfk lwf u,mksgnsq v)
    else addpf(wedgepf2(!*k2pf lwf u,
                       dwedge1(rwf u,addf(v,deg!*form lwf u))),
               multpfsq(wedgepf2(exdfk lwf u,!*k2pf rwf u),mksgnsq v));

symbolic procedure exdfprn u;
   <<prin2!* "d"; rembras cadr u>>;

put('d,'prifn,'exdfprn);

symbolic procedure xexdfprn u;
   begin scalar w;
     w := fancy!-prin2!*("\,d\,",2);
     return fancy!-maprint(cadr u,0)
   end;

put('d,'fancy!-prifn,'xexdfprn);


endmodule;

end;
