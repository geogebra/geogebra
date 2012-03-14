module specbess;  % Special functions package; Bessel and relatives.

% Author:  Chris Cannam, Sept-Nov 1992.
%          Winfried Neun, Nov 1992 ...
%          contribution from various authors ...

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


remprop('besseli,'simpfn);
remprop('besselj,'simpfn);
remprop('bessely,'simpfn);
remprop('besseli,'simpfn);
remprop('besselk,'simpfn);
remprop('hankel1,'simpfn);
remprop('hankel2,'simpfn);
remprop('kummerM,'simpfn);
remprop('kummerU,'simpfn);
remprop('struveh,'simpfn);
remprop('struvel,'simpfn);
remprop('lommel1,'simpfn);
remprop('lommel2,'simpfn);
remprop('whittakerm,'simpfn);
remprop('whittakerw,'simpfn);
remprop('Airy_Ai,'simpfn);
remprop('Airy_Bi,'simpfn);
remprop('Airy_AiPrime,'simpfn);
remprop('Airy_biprime,'simpfn);


create!-package ('(specbess sfbes sfkummer sfother sfairy),
                 '(contrib specfn));

symbolic smacro procedure sq2bf!*(x);
   (if fixp x then i2bf!: x
      else ((if car y neq '!:rd!: then retag cdr !*rn2rd y
               else retag cdr y) where y = !*a2f x));

symbolic smacro procedure c!:prec!:;
   (if new!*bfs then lispeval '!:bprec!: else !:prec!:);

% These functions are needed in other modules.

algebraic procedure complex!*on!*switch;
   if not symbolic !*complex then
      if symbolic !*msg then
         << off msg;
            on complex;
            on msg >>
      else on complex
   else t;

algebraic procedure complex!*off!*switch;
   if symbolic !*complex then
      if symbolic !*msg then
         << off msg; off complex; on msg >>
      else off complex
   else t;

algebraic procedure complex!*restore!*switch(fl);
   if not fl then
      if symbolic !*msg then
         << off msg;
            if symbolic !*complex then
               off complex
            else on complex;
            on msg >>
      else if symbolic !*complex then
            off complex
         else on complex;

endmodule;

end;



