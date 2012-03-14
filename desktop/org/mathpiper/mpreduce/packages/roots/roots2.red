module roots2; % Header module for roots2 package.

% Author: Stanley L. Kameny <stan_kameny@rand.org>.

% Version and Date:  Mod 1.96, 30 March 1995.

% Copyright (c) 1988,1989,1990,1991,1992,1993,1994,1995.
% Stanley L. Kameny.  All Rights Reserved.

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


Comment
  Revisions:
  30 March 95  Mod 1.96 adds to multroot the capability of solving
                polynomial trees which are determinate but whose
                structure may lack a stairstep pattern of variables,
                or may contain more polynomials than variables.
                Polynomials can now have denominators, which are
                ignored since only the numerators are used.
                  Spurious small real or imaginary parts of complex
                roots, which can arise due to numeric substitution, are
                now detected and eliminated.  However, vital small real
                or imaginary parts are retained (as in the roots
                program.)
                  Error handling is improved.  Each error now returns
                an error message and then multroot(pr,pl) where pr is
                the precision of answers and pl is the equivalent
                polynomial tree whose processing failed. ;

create!-package ('(roots2 realroot nrstroot multroot),
                 '(contrib roots));

symbolic procedure realroots u; nil; % to fool loader.

% Other packages needed.
load_package roots;

endmodule;

end;
