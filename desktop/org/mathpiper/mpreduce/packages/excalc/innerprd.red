module innerprd;

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


fluid '(subfg!*);

global '(basisvectorl!* keepl!*);

newtok '((!_ !|) innerprod);

infix innerprod;

precedence innerprod,times;

%flag('(innerprod),'nary); %not done for now, but might be worthwhile.

flag('(innerprod),'spaced);

put('innerprod,'simpfn,'simpinnerprod);

put('innerprod,'rtypefn,'getrtypeor);

put('innerprod,'partitfn,'partitinnerprod);

symbolic procedure partitinnerprod u;
   innerprodpf(partitop car u,
               partitop cadr u);

symbolic procedure mkinnerprod(u,v);
   begin scalar x,y;
     return if x := opmtch(y := list('innerprod,u,v))
               then partitop x
             else if deg!*form v = 1
                     then if numr(x := mksq(y,1)) then 1 .* x .+ nil
                           else nil
                   else mkupf y
   end;

symbolic procedure simpinnerprod u;
   !*pf2sq partitinnerprod u;


symbolic procedure innerprodpf(u,v);
   if null u or null v then nil
    else if ldpf v = 1 then nil
    else
      begin scalar res,x;
        for each j on u do
          for each k on v do
            if x := innerprodf(ldpf j,ldpf k)
               then res := addpf(multpfsq(x,multsq(lc j,lc k)),res);
        return res
      end;

symbolic procedure basisvectorp u;
   null atom u and u memq basisvectorl!*;

symbolic procedure tvectorp u;
   (numberp x and x<0) where x = deg!*form ldpf u;

symbolic procedure innerprodf(u,v);
   %Inner product dispatching routine.
   if null tvectorp !*k2pf u then
        rerror(excalc,8,
               "First argument of inner product must be a vector")
    else if v = 1 then nil %is this test necessary??
    else if eqcar(v,'wedge)
            then innerprodwedge(u,cdr v)
    else if eqcar(u,'partdf) and null freeindp cadr u
            then innerprodnvec(u,v)
    else if basisvectorp u and basisformp v
            then innerprodbasis(u,v)
    else if eqcar(v,'innerprod)
            then if u eq cadr v then nil
                  else if ordop(u,cadr v) then mkinnerprod(u,v)
                        else negpf innerprodpf(!*k2pf cadr v,
                                               innerprodf(u,caddr v))
    else mkinnerprod(u,v);

symbolic procedure innerprodwedge(u,v);
   mkuniquewedge innerprodwedge1(u,v,nil);

symbolic procedure innerprodwedge1(u,v,w);
   if null rwf v then mkunarywedge
                      multpfsq(innerprodf(u,lwf v),mksgnsq w)
    else addpf(if null rwf rwf v and (deg!*form lwf rwf v = 1)
                  then multpfsq(!*k2pf list lwf v,
                       multsq(mksgnsq addf(deg!*form lwf v,w),
                              !*pf2sq innerprodf(u,lwf rwf v)))
                else wedgepf2(!*k2pf lwf v,
                              innerprodwedge1(u,rwf v,
                                    addf(w,deg!*form lwf v))),
               if deg!*form lwf v = 1
                  then multpfsq(!*k2pf rwf v,
                                multsq(!*pf2sq innerprodf(u,lwf v),
                                       mksgnsq w))
                else wedgepf2(innerprodf(u,lwf v),
                              rwf v .* mksgnsq w .+ nil));


symbolic procedure innerprodnvec(u,v);
   if eqcar(v,'d) and null deg!*form cadr v
      and null freeindp cadr v
      then if cadr u eq cadr v then 1 .* (1 ./ 1) .+ nil
            else nil
    else if basisformp v
            then begin scalar x,osubfg;
                   osubfg := subfg!*;
                   subfg!* := nil;
                   x := innerprodpf(!*k2pf u,
                                    partitop cdr assoc(v,keepl!*));
                   subfg!* := osubfg;
                   return repartit x
                 end;

symbolic procedure innerprodbasis(u,v);
   if freeindp u or freeindp v then mkinnerprod(u,v)
    else if cadadr u eq cadr v then 1 .* (1 ./ 1) .+ nil
          else nil;


endmodule;

end;
