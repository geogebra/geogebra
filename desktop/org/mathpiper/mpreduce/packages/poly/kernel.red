module kernel;   % Functions for operations on kernels.

% Author: Anthony C. Hearn.

% Copyright (c) 1990 The RAND Corporation.  All rights reserved.

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


global '(exlist!* kprops!*);

symbolic procedure fkern u;
   % Finds the unique "p-list" reference to the kernel U.  The choice of
   % the search and merge used here has a strong influence on some
   % timings.  The ordered list used here is also used by prepsq* to
   % order factors in printed output, so cannot be unilaterally changed.
   begin scalar x,y;
        if atom u then return list(u,nil)
         else if x := get(car u,'fkernfn) then return apply1(x,u);
        y := if atom car u then get(car u,'klist) else exlist!*;
        if not (x := assoc(u,y))
          then <<x := list(u,nil);
                 y := ordad(x,y);
                 if atom car u
                   then <<kprops!* := union(list car u,kprops!*);
                          put(car u,'klist,y)>>
                  else exlist!* := y>>;
        return x
   end;

symbolic procedure kernels u;
   % Returns list of kernels in standard form u.
   kernels1(u,nil);

symbolic procedure kernels1(u,v);
   % We append to end of list to put kernels in the right order, even
   % though a cons on the front of the list would be faster.
   if domainp u then v
    else kernels1(lc u,
                  kernels1(red u,
                           if x memq v then v else append(v,list x)))
         where x=mvar u;

symbolic procedure kernp u;
   % True if U is standard quotient representation for a kernel.
   denr u=1 and not domainp(u := numr u)
        and null red u and lc u=1 and ldeg u=1;     % onep

endmodule;

end;
