module primitive; % Include primitive module alterations to solve.

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


fluid '(!*cramer bareiss!-step!-size!*);

symbolic procedure primitivesf(xl,vl);
   % xl:list of sf, vl:list of kernel -> primitivesf:sf
   % Returns each x in xl divided by gcd of the coefficients of vl.
   % x is ordered wrt vl, and linear in vl.
   foreach x in xl collect
      quotf!*(x,coeffgcd(x,vl));

symbolic procedure coeffgcd(x,vl);
   % x:sf, vl:list of kernel -> coeffgcd:sf
   % returns gcd of coefficients of vl (including degree 0) in x
   if domainp x or not(mvar x memq vl) then x
   else if null red x then lc x
   else gcdf(lc x,coeffgcd(red x,vl));

symbolic procedure solvelnrsys(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvelnrsys: tagged solution list
   % Check the system for sparsity, then decide whether to use the
   % Cramer or Bareiss method.  Using the Bareiss method on sparse
   % systems, 4-step elimination seems to be faster than 2-step.
   % The Bareiss code is not good at handling surds at the moment,
   % hence exptexpflistp test.
   begin scalar w,method;
   exlis := primitivesf(exlis,varlis);
   if w := solvesparsecheck(exlis,varlis) then exlis := w
     else exlis := exlis . varlis;
   if null !*cramer and null exptexpflistp exlis
      then method := 'solvebareiss
     else method := 'solvecramer;
   exlis := apply2(method,car exlis,cdr exlis)
               where bareiss!-step!-size!* = if w then 4 else 2;
   return solvesyspost(exlis,varlis);
   end;

endmodule;

end;
