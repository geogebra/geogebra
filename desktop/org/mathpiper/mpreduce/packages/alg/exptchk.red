module exptchk;   % Check expt products for further simplification.

% Author: Anthony C. Hearn.

% Copyright (c) 2005, Anthony C. Hearn.  All rights reserved.

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


fluid '(!*combineexpt);

switch combineexpt;

put('combineexpt,'simpfg,'((t (rmsubs)) (nil (rmsubs))));

symbolic procedure exptchksq u;
   % U is a standard quotient. Result is u with possible expt
   % simplifications.
   if null !*combineexpt then u
    else multsq(exptchk numr u,invsq exptchk denr u);

symbolic procedure exptchk u;
   if domainp u then u ./ 1
    else (if length v<2 then u ./ 1 else exptchk0(u,nil,v)) where v=comm_kernels u;

symbolic procedure exptchk0(u,v,w);
   if null u then nil ./ 1
    else if domainp u then exptunwind(u,v)
    else if expttermp(mvar u,w)
     then addsq(exptchk0(lc u,lpow u . v,w),exptchk0(red u,v,w))
    else addsq(multsq(!*p2f lpow u ./ 1,exptchk0(lc u,v,w)),exptchk0(red u,v,w));

symbolic procedure expttermp(u,v);
   if eqcar(u,'expt) then expttermp1(cadr u,v) else expttermp1(u,v);

symbolic procedure expttermp1(u,v);
   v and (u=car v or (eqcar(car v,'expt) and u=cadar v)
             or expttermp1(u,cdr v));

symbolic procedure exptunwind(u,v);
   begin integer n; scalar w,x;
   % U is a standard form, v a list of powers.
   % Result is a standard form of product(v) * u.
   % This function is the key to a better treatment of surds.
      n := 1;
      while v do
   %%%   <<if !*combineexpt and cdr v and (w := meldx(car v,cdr v))
         <<if cdr v and (w := meldx(car v,cdr v))
             then <<x := mergex(car v,w);
                    if fixp x then <<n := x*n; v := delete(w,cdr v)>>
                     else v := x . delete(w,cdr v)>>
            else <<u := multpf(car v, u); v := cdr v>>>>; 
      u := rm_neg_pow u;
      return multsq(n ./ 1,u)
   end;

symbolic procedure rm_neg_pow u;
   if domainp u then u ./ 1
    else if minusp ldeg u 
            then addsq(multsq(1 ./ (mvar u .^ (-ldeg u) .* 1 .+ nil),rm_neg_pow lc u),
                       rm_neg_pow red u)
          else addsq(multsq(!*p2f lpow u ./ 1,rm_neg_pow lc u),rm_neg_pow red u);

symbolic procedure mergex(u,v);
   if eqcar(car u,'expt)
     then if eqcar(car v,'expt)
              then if cadar u=cadar v
                     then mergey(cadar u,caddar u,caddar v,cdr u,cdr v)
                    else if caddar u=caddar v and cdr u=cdr v
               then mksp({'expt,{'times,cadar u,cadar v},caddar u},cdr u)
                    else rederr 'foo
%           else mergey(cadar u,caddar u,car v,cdr u,cdr v)
           else mergey(cadar u,caddar u,1,cdr u,cdr v)
    else if eqcar(car v,'expt) then mergey(car u,1,caddar v,cdr u,cdr v)
    else rederr {'mergex,u,v};

symbolic procedure mergey(u,v,w,x,y);
   begin
      x := simp!*{'plus,{'times,v,x},{'times,w,y}};
      if (y:= intcoeff numr x) neq 1 then x := quotf(numr x,y) ./ denr x;
      x := prepsq!* x;
      return if fixp u and fixp x then (u^x)^y else mksp({'expt,u,x},y)
   end;

symbolic procedure intcoeff u;
   % Returns an integer multiplier of standard form u.
   if domainp u then if fixp u then u else 1
    else (if null red u then n else gcdn(n,intcoeff red u))
             where n = intcoeff lc u;

symbolic procedure meldx(u,v);
   if eqcar(car u,'expt)
     then (if w then w
            else if eqcar(caar v,'expt) then meldx1(u,delete(u,v))
            else nil) where w=meldx0(cadar u,delete(u,v))
    else meldx0(car u,delete(u,v));

symbolic procedure meldx0(u,v);
   if null v then nil
    else if (eqcar(caar v,'expt) and u=cadaar v) or u=caar v then car v
    else meldx0(u,cdr v);

symbolic procedure meldx1(u,v);
   % Look for equal exponents.
   if null v then nil
    else if eqcar(car v,'expt) and caddar u=caddar car v and cdr u=cdar v
     then car v
    else meldx1(u,cdr v);

symbolic procedure comm_kernels u;
   % Returns list of commutative kernels in standard form u.
   comm_kernels1(u,nil);

symbolic procedure comm_kernels1(u,v);
   % We append to end of list to put kernels in the right order, even
   % though a cons on the front of the list would be faster.
   if domainp u then v
    else comm_kernels1(lc u,
                       comm_kernels1(red u,
                                     if x memq v or noncomp x
                                        then v else append(v,list x)))
         where x=mvar u;

endmodule;

end;
