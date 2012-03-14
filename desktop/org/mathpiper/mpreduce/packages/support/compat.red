MODULE COMPAT;

% Author: Anthony C. Hearn;

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


FLUID '(!*USERMODE);

GLOBAL '(SPARE!*);

SPARE!* := 10;

% This file defines functions and variables that are needed to
% make REDUCE and the underlying PSL system compatible. It should
% be loaded as the first file whenever REDUCE services are required.

% Definitions of functions already defined in PSL

% PSL doesn't need PRINTPROMPT

REMFLAG('(PRINTPROMPT),'LOSE);

symbolic procedure printprompt u; nil;

flag('(printprompt),'lose);

% The following are all supported by PSL:

flag('(atsoc eqcar delasc mkquote aconc prin2t reversip union geq leq
        neq putc yesp),
      'lose);

flag('(rblock for foreach lprim repeat while),'user);  % to permit redef

symbolic procedure !*s2i u; u;

% These are needed until the PSL syslisp and trace modules are changed:

symbolic procedure definebop u; u;

symbolic procedure definerop u; u;

endmodule;

end;


