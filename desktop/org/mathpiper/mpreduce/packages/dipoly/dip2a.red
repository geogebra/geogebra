module dip2a;

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


% Functions for converting distributive forms into prefix forms

%Authors: R. Gebauer, A. C. Hearn, H. Kredel

symbolic procedure dip2a u;
%    Returns prefix equivalent of distributive polynomial u.
   if dipzero!? u then 0 else dipreplus dip2a1 u;

symbolic procedure dip2a1 u;
   if dipzero!? u then nil
    else ((if bcminus!? x then list('minus,dipretimes(bc2a bcneg x.y))
           else dipretimes(bc2a x.y))
         where x = diplbc u, y = expvec2a dipevlmon u)
           .dip2a1 dipmred u;

symbolic procedure dipreplus u;
   if atom u then u else if null cdr u then car u else 'plus . u;

symbolic procedure dipretimes u;
%   /* U is a list of prefix expressions the first of which is a number.
%     Result is prefix representation for their product*/
   if car u = 1 then if cdr u then dipretimes cdr u else 1
    else if null cdr u then car u
    else 'times.u;

endmodule;;end;
