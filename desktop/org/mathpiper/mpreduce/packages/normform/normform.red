module normform; % Package for the computation of several normal forms.
%                                                                      %
% This file contains routines for computation of the following         %
% normal forms of matrices:                                            %
%                                                                      %
%  - smithex_int                                                       %
%  - smithex                                                           %
%  - frobenius                                                         %
%  - ratjordan                                                         %
%  - jordansymbolic                                                    %
%  - jordan                                                            %
%                                                                      %
% The manual for this package is found in the normform.tex file.       %
% It includes descriptions of the various normal forms.                %
%                                                                      %
% Further examples are found in the normform.log file.                 %
%                                                                      %
% For a description of the algorithms see the comments.                %
%                                                                      %
%                                                                      %
% Author: Matt Rebbeck   Nov '93 - Feb '94                             %
%                                                                      %
% This code has been converted from the normform and Normform packages %
% written by T.M.L. Mulders and A.H.M. Levelt for Maple.               %
%                                                                      %
% normform contains one switch: looking_good. If using xr, the X       %
% interface for REDUCE, switching this on will improve the appearance  %
% of the output. The switch serves no (useful) purpose in standard     %
% REDUCE (ie: not using xr).                                           %
%                                                                      %
%**********************************************************************%

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


create!-package('(normform jordan jordsymb ratjord froben matarg
                  nestdom smithex smithex1),'(contrib normform));

endmodule;

end;




















