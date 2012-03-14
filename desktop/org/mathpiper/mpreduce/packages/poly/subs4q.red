module subs4q; % Routines for matching quotients.

% Author: Anthony C. Hearn.
%         modification to more general quotient matching: Herbert Melenk

% Copyright (c) 1992 RAND.  All rights reserved.

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


symbolic procedure subs4q u;
   % U is a standard quotient,
   % Value is a standard quotient with all quotient substitutions made.
   begin scalar x,w,q,d;
      if null(x:=get('slash,'opmtch)) then return u;
      w := prepsq u;
      remprop('slash,'opmtch); % to prevent endless recursion.
      put('slash!*,'opmtch,x);
      while w and eqcar(q:=w,'quotient) do
          <<w:=opmtch ('slash!* . cdr w) or
               smemq('minus,caddr w) and
                  opmtch{'slash!*,reval{'minus,cadr w},
                                  reval{'minus,caddr w}};
            d:=d or w>>;
      u:= if d then simp!* q else u;
      put('slash,'opmtch,x);
      return u;
   end;

endmodule;

end;
