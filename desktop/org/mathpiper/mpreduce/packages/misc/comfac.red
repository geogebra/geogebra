module comfac;   % Multivariate common factor/content routines.

% Author: Anthony C. Hearn.

% Copyright (c) 1989 The RAND Corporation.  All Rights Reserved.

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


symbolic smacro procedure domain!-gcd(u,v); gcdn(u,v);

symbolic smacro procedure domain!-onep u; onep u;

symbolic procedure mv!-pow!-zerop u;
   null u or zerop car u and mv!-pow!-zerop cdr u;

symbolic procedure mv!-pow!-gcd(u,v);
   if null u then nil
    else min(car u,car v) . mv!-pow!-gcd(cdr u,cdr v);

symbolic procedure mv!-content u;
   % Finds the term that is the content of u.
      if null u then nil
       else begin scalar x,y;
         x := mv!-lc u;
         y := mv!-lpow u;
      a: u := mv!-red u;
         if null u or domain!-onep x and mv!-pow!-zerop y
           then return mv!-!.!*(y,x);
         x := domain!-gcd(x,mv!-lc u);
         y := mv!-pow!-gcd(y,mv!-lpow u);
         go to a
       end;

endmodule;

end;
