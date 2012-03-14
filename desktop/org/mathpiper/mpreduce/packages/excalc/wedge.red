module wedge;

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


global '(dimex!* lftshft!* wedgemtch!*);

newtok '((!^) wedge);

flag('(wedge),'nary);

infix wedge;

precedence wedge,times;

smacro procedure wedgeordp(u,v); worderp(u,v);

put('wedge,'simpfn,'simpwedge);

put('wedge,'rtypefn,'getrtypeor);

put('wedge,'partitfn,'partitwedge);

symbolic procedure partitwedge u;
   if null cdr u then partitop car u
            else mkuniquewedge xpndwedge u;


symbolic procedure oddp m;
   if not fixp m then typerr(m,"integer") else remainder(m,2) neq 0;

symbolic procedure mksgnsq u;
   if null (u := evenfree u) then 1 ./ 1
    else if u = 1 then (-1) ./ 1
    else simpexpt list(-1,mk!*sq(u ./ 1));

symbolic procedure evenfree u;
   if null u then nil
    else if numberp u then absf cdr qremd(u,2)
    else addf(absf cdr qremd(!*t2f lt u,2),evenfree red u);

symbolic procedure mkwedge u; !*k2pf u;

symbolic procedure wedgemtch u;
   begin scalar x,y,z;
     y := u;
     a: x := car y . x;
    if z := assoc(reverse x,wedgemtch!*) then
       return if cdr z then if cdr y then
                               'wedge . append(cdr z,cdr y)
                             else cdr z
               else 0;
    y := cdr y;
    if y then go to a else return nil
   end;


symbolic procedure simpwedge u;
   !*pf2sq partitwedge u;

symbolic procedure xpndwedge u;
   if null cdr u
      then mkunarywedge partitop car u
    else wedgepf2(partitop car u,xpndwedge cdr u);

symbolic procedure mkunarywedge u;
   if null u then nil
    else list ldpf u .* lc u .+ mkunarywedge red u;

symbolic procedure mkuniquewedge u;
   if null u then nil
    else addpf(multpfsq(mkuniquewedge1 ldpf u,lc u),
               mkuniquewedge red u);

symbolic procedure mkuniquewedge1 u;
   if null cdr u
      then mkupf car u
    else begin scalar x;
           return if wedgemtch!* and (x := wedgemtch u)
                     then partitop x
                   else mkupf('wedge . u)
         end;

symbolic procedure wedgepf2(u,v);
   %Basic binary exterior product routine.
   %v is an exterior product (without wedge tag), u a form.
   if null u or null v then nil
    else addpf(wedget2(lt u,lt v),
               addpf(wedgepf2(lt u .+ nil,red v),wedgepf2(red u,v)));

smacro procedure multwedgesq(u,v);
   %possible entry for lazy multiplication.
   multsq(u,v);

symbolic procedure wedget2(u,v);
   if car u = 1 then car v .* multsq(cdr u,cdr v) .+ nil
    else if caar v = 1 then list car u .* multsq(cdr u,cdr v) .+ nil
    else multpfsq(wedgek2(car u,car v,nil),multwedgesq(tc u,tc v));

symbolic procedure wedgek2(u,v,w);
   if u eq car v and null eqcar(u,'wedge)
      then if (fixp n and oddp n) where n = deg!*form u then nil
            else multpfsq(wedgef(u . v),mksgnsq w)
    else if eqcar(car v,'wedge) then wedgek2(u,cdar v,w)
    else if eqcar(u,'wedge)
            then multpfsq(wedgewedge(cdr u,v),mksgnsq w)
    else if wedgeordp(u,car v)
            then multpfsq(wedgef(u . v),mksgnsq w)
    else if cdr v
            then wedgepf2(!*k2pf car v,
                          wedgek2(u,cdr v,addf(w,multf(deg!*form u,
                                                   deg!*form car v))))
    else multpfsq(wedgef list(car v,u),
                  mksgnsq addf(w,multf(deg!*form u,deg!*form car v)));

symbolic procedure wedgewedge(u,v);
   if null cdr u then wedgepf2(!*k2pf car u,!*k2pf v)
    else wedgepf2(!*k2pf car u,wedgewedge(cdr u,v));


symbolic procedure wedgef u;
     if dim!<deg u then nil
      else if eqcar(car u,'hodge) then
              (if m = deg!*farg cdr u then
                  multpfsq(wedgepf2(!*k2pf cadar u,
                                    mkunarywedge
                                     hodgepf if cddr u
                                              then mkuniquewedge1 cdr u
                                              else !*k2pf cadr u),
                           mksgnsq multf(m,addf(m,negf dimex!*)))
                else mkwedge u)
               where m = deg!*form cadar u
      else if eqcar(car u,'d) and (flagp('d,'noxpnd)
              or lftshftp cadar u) then
                    addpf(mkunarywedge dwedge(cadar u . cdr u),
                          multpfsq(wedgepf2(!*k2pf cadar u,
                                            mkunarywedge
                                              if cddr u
                                                 then dwedge cdr u
                                               else exdfk cadr u),
                                   negsq mksgnsq deg!*form cadar u))
      else mkwedge u;

!#if (memq 'csl lispsystem!*)
put('wedge,'fancy!-infix!-symbol,"\wedge ");
!#else
put('wedge,'fancy!-infix!-symbol,217);
!#endif

endmodule;

end;
