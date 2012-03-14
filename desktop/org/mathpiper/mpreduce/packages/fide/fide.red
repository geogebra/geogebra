module fide; % FInite difference method for partial Differential Eqn
             % systems.

% Author: Richard Liska.

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


% Version: 1.1.2 for REDUCE 3.6, May 29, 1995.

%***********************************************************************
%**  (C) 1991-1995, Richard Liska                                     **
%**      Faculty of Nuclear Science and Physical Engineering          **
%**      Technical University of Prague                               **
%**      Brehova 7                                                    **
%**      115 19 Prague 1                                              **
%**      Czech Republic                                               **
%**      Email: Richard Liska <liska@siduri.fjfi.cvut.cz>             **
%**  This package can be distributed through REDUCE Network Library.  **
%***********************************************************************

% The FIDE package consists of the following modules:
%
%  DISCRET rules for discretization.
%  EXPRES  for transforming PDES into any orthogonal coordinate system.
%  IIMET   for discretization of PDES by integro-interpolation method.
%  APPROX  for determining the order of approximation of difference
%          scheme
%  CHARPOL for calculation of amplification matrix and characteristic
%          polynomial of difference scheme, which are needed in Fourier
%          stability analysis.
%  HURWP   for polynomial roots locating necessary in verifying the von
%          Neumann stability condition.
%  LINBAND for generating the block of FORTRAN code, which solves a
%          system of linear algebraic equations with band matrix
%          appearing quite often in difference schemes.
%

% Changes since version 1.1:
% Patches in SIMPINTERPOL and SIMPINTT               13/06/91
% Patch in TDIFPAIR                                  08/07/91
% Two FEXPR routines F2VAL, FPLUS changed to MACROs  17/03/92
% Patches in IIM1, AMPMAT, HURW, CHARPOL for 3.5     01/11/93
% Version 1.1.1 of the FIDE package is the result of porting the FIDE
% package version 1.1.0 to REDUCE 3.5.
% Infix uses of NOT removed (not a memq b  ->  not(a memq b) ) ACH
% MAP* functions replaced by FOR EACH syntax         16/03/94
% Version 1.1.2 of the FIDE package is the result of porting the FIDE
% package version 1.1.1 to REDUCE 3.6.


create!-package('(fide discret approx charpol hurwp linband),
                '(contrib fide));

load!-package 'matrix;

load!-package 'fide1;   % We need this loaded.

endmodule;

end;
