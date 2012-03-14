module invbase; % Computing involutive basis of polynomial system.

% Authors:  Alexey Yu. Zharkov, Yuri A. Blinkov
%           Saratov University, Astrakhanskaya 83,
%           Saratov 410071, Russia
%           e-mail: postmaster@scnit.saratov.su

% Copyright A. Zharkov, Y. Blinkov;
%           all rights reserved.

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


% Minor fixes by John Fitch.

create!-package('(invbase invbint invbcomp),'(contrib invbase));

fluid '(CONDS GV HV BV NG GG VARLIST VJETS NC);            % globals
fluid '(ORDERING REDTAILS);                                % modes
fluid '(PATH TRED STARS);                                  % tracing
fluid '(REDUCTIONS NFORMS ZEROS MAXORD TITLE);             % statistics
fluid '(invsysvars!* !*trinvbase alfa beta shortway thirdway
         invtempbasis);

share invtempbasis;

ordering := 'grev;

switch trinvbase;

gv:=mkvect(1000)$  % p o l y n o m i a l s
bv:=mkvect(1000)$  % f l a g  (n e w  p r o l o n g a t i o n s)

endmodule;

end;
