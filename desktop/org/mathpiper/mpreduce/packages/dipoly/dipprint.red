module dipprint;   %/* printing routines for distributive polynomials*/

% Authors: R. Gebauer, A. C. Hearn, H. Kredel

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


symbolic procedure diplprint u;
%    Prints a list of distributive polynomials using dipprint
     for each v in u do dipprint v;

symbolic procedure dipprint u;
%   Prints a distributive polynomial in infix form
   <<terpri(); dipprint1(u,nil); terpri(); terpri()>>;

symbolic procedure dipprint1(u,v);
%     Prints a distributive polynomial in infix form.
%     U is a distributive form. V is a flag which is true if a term
%     has preceded current form
   if dipzero!? u then if null v then dipprin2 0 else nil
    else begin scalar bool,w;
       w := diplbc u;
       if bcminus!? w then <<bool := t; w := bcneg w>>;
       if bool then dipprin2 " - " else if v then dipprin2 " + ";
       (if not bcone!? w or evzero!? x then <<bcprin w; dipevlpri(x,t)>>
         else dipevlpri(x,nil))
           where x = dipevlmon u;
       dipprint1(dipmred u,t) end;

symbolic procedure dipprin2 u;
%   Prints u, preceding by two EOL's if we have reached column 70
   <<if posn()>69 then <<terpri(); terpri()>>; prin2 u>>;

endmodule;;end;
