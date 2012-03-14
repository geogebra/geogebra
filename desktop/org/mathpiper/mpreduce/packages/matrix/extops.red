module extops;  % Support for exterior multiplication.

% Author: Eberhard Schrufer.
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


Comment. Data structure for simple exterior forms is

        ex ::= nil | lpow ex .* lc ex .+ ex
        lpow ex ::= list of kernel
        lc ex    ::= sf

All forms have degree > 0. lpow ex is a list of factors in a basis form;

symbolic procedure !*sf2ex(u,v);
   %Converts standardform u into a form distributed w.r.t. v
%*** Should we check here if lc is free of v?
   if null u then nil
    else if domainp u or null(mvar u memq v) then list nil .* u .+ nil
    else list mvar u .* lc u .+ !*sf2ex(red u,v);

symbolic procedure !*ex2sf u;
   % u: ex -> !*ex2sf: sf
   % reconverts 1-form u, but doesn't check ordering
   if null u then nil
   else if car lpow u = nil then subs2chk lc u
   else car lpow u .** 1 .* subs2chk lc u .+ !*ex2sf red u;

symbolic procedure extmult(u,v);
   % u,v: ex -> extmult: ex
   % Special exterior multiplication routine.  Degree of form v is
   % arbitrary, u is a one-form.
   if null u or null v then nil
    else (if x then cdr x .* (if car x then negf c!:subs2multf(lc u,lc v)
                               else c!:subs2multf(lc u,lc v))
                          .+ extadd(extmult(!*t2f lt u,red v),
                                    extmult(red u,v))
           else extadd(extmult(red u,v),extmult(!*t2f lt u,red v)))
          where x = ordexn(car lpow u,lpow v);

symbolic procedure extadd(u,v);
   % u,v: ex -> extadd: ex
   % a non-recursive exterior addition routine
   % u and v are of same degree
   % relies on setq functions for red
   if null u then v
   else if null v then u
   else
      begin scalar s,w,z;
      s := z := nil .+ nil;
      while u and v do
         if lpow v = lpow u then               % add coefficients
          <<if w := addf(lc u,lc v) then        % replace coefficient
               <<red z := lpow u .* w .+ nil; z := red z>>;
            u := red u; v := red v>>
         else if ordexp(lpow v,lpow u) then   % swap v and u
          <<red z := lt v .+ nil; v := red v; z := red z>>
         else
          <<red z := lt u .+ nil; u := red u; z := red z>>;
      red z := if u then u else v;
      return red s;
      end;

symbolic procedure ordexp(u,v);
   if null u then t
    else if car u eq car v then ordexp(cdr u,cdr v)
    else if null car u then nil
    else if null car v then t
    else ordop(car u,car v);

symbolic procedure ordexn(u,v);
   %u is a single variable, v a list. Returns nil if u is a member
   %of v or a dotted pair of a permutation indicator and the ordered
   %list of u merged into v.
   begin scalar s,x;
     a: if null v then return(s . reverse(u . x))
         else if u eq car v then return nil
         else if u and ordop(u,car v) then
                 return(s . append(reverse(u . x),v))
         else  <<x := car v . x;
                 v := cdr v;
                 s := not s>>;
         go to a
   end;

symbolic procedure quotexf!*(u,v);
   % u: ex, v: sf -> quotexf!*: ex
   % catastrophe if division fails
   if null u then nil
   else lpow u .* quotfexf!*1(lc u,v) .+ quotexf!*(red u,v);

symbolic procedure quotfexf!*1(u,v);
   % We do the rationalizesq step to allow for surd divisors.
   if null u then nil
    else (if x then x
           else (if denr y = 1 then numr y
                 % Try once more.
                  else if denr (y := (rationalizesq y
                                         where !*rationalize = t))=1
                   then numr y
                  else rerror(matrix,11,
                              "Catastrophic division failure"))
                 where y=rationalizesq(u ./ v))
          where x=quotf(u,v);

symbolic procedure negex u;
   % u: ex -> negex: ex
   if null u then nil
   else lpow u .* negf lc u .+ negex red u;

symbolic procedure splitup(u,v);
   % u: ex, v: list of kernel -> splitup: {ex,ex}
   % split 1-form u into part free of v (not containing nil), and rest
   % assumes u ordered wrt v
   if null u then {nil,nil}
   else if null x or memq(x,v) where x = car lpow u then {nil,u}
   else {lt u .+ car x, cadr x} where x = splitup(red u,v);


symbolic procedure innprodpex(v,u);
   % v: lpow ex, u: ex -> innprodpex: ex
   % v _| u = v _| lt u .+ v _| red u (order is correct)
   if null u then nil else
   (if x then cdr x .* (if car x then negf lc u else lc u)
                    .+ innprodpex(v,red u)
    else innprodpex(v,red u))
    where x = innprodp2(v,lpow u);


symbolic procedure innprodp2(v,u);
   % u,v: lpow ex -> innprodp2: nil or bool . lpow ex
   % returns sign of permutation as well
   % (x^y) _| u =  y _| (x _| u)
   begin
   u := nil . u;
   while v and u do
    <<u := innprodkp(nil,car v,cdr u,car u);
      v := cdr v>>;
   return u;
   end;


symbolic procedure innprodkp(w,v,u,s);
   % w,u: lpow ex or nil, v: kernel, s: bool
   % -> innprodkp: nil or bool . lpow ex
   % w,u are exterior forms, v is vector in dual space
   % calulates w^(v _| u), assuming degree u > 1 and returns sign
   % permutation as well
   if null u then nil
   else if v = car u then s . reversip2(w,cdr u)
   else innprodkp(car u . w,v,cdr u,not s);


symbolic procedure subs2chkex u;
   % u:ex -> subs2chkex:ex
   % Leading coefficient of return value has been subs2chk'ed
   if null u then nil
   else (if x then lpow u .* x .+ red u else subs2chkex red u)
         where x = subs2chk lc u;


symbolic procedure subs2chk u;
   % This definition allows for a power substitution that can lead to
   % a denominator in subs2.  We omit the test for !*sub2 and powlis1!*
   % to make sure the check is made.  Maybe this can be optimized.
   begin scalar x;
      if subfg!* and denr(x := subs2f u)=1 then u := numr x;
      return u
     end;

endmodule;

end;
