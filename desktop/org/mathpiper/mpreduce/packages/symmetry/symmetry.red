module symmetry;
%
% ----------------------------------------------------------
%                       Symmetry Package
% ----------------------------------------------------------
%
% Author : Karin Gatermann
%          Konrad-Zuse-Zentrum fuer
%          Informationstechnik Berlin
%          Heilbronner Str. 10
%          W-1000 Berlin 31
%          Germany
%          Email: Gatermann@sc.ZIB-Berlin.de
%
% Version 1.0   9. December 1991
%

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


% Abstract:
% ---------
% This program is an implementation of the algorithm
% for computation of symmetry adapted bases from the
% theory of linear representations of finite grous.
% Projections for the computation of block diagonal form
% of matrices are computed having the symmetry of a group.
%
%
% REDUCE 3.4 is required.
%
% References:
% -----------
% J.-P. Serre, Linear Representations of Finite Groups.
% Springer, New York (1977).
% E. Stiefel, A. F{\"a}ssler, Gruppentheoretische
%  Methoden und ihre Anwendung. Teubner, Stuttgart (1979).
%  (English translation to appear by Birkh\"auser (1992)).
%
% Keywords:
% --------
%           linear representations, symmetry adapted bases,
%           matrix with the symmetry of a group,
%           block diagonalization
%
% symmetry.red
% definition of available algebraic operators


% To build a fast loading version of this package, the following
% sequence of commands should be used:

create!-package('(symmetry symdata1 symdata2),'(contrib symmetry));

load!-package 'symaux;

endmodule;

end;
