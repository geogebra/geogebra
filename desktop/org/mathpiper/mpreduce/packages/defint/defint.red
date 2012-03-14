module defint;  % Special functions integrator package for REDUCE.

% Author:  Kerry Gaskell 1993/94.
%          Winfried Neun, Jan 1995 ...
%          contribution from Victor Adamchik (WRI)

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


load_package limits,specfn2;

algebraic operator m_jacobip,m_gegenbauerp,m_laguerrep,m_hermitep,
                m_chebyshevu,m_chebyshevt,m_legendrep,
                struveh2,mycosh,mysinh;

global '(spec_cond unknown_tst product_tst transform_tst transform_lst);


create!-package ('(defint definta defintc defintf definti defint0
                defintd defintg defintj defintb definte
                definth defintk defintx),
%               definth defintk),
                 '(contrib defint));

!#if (memq 'psl lispsystem!*)
  flag('(definta defintb definte defintf definti defintk),'lap);
!#endif

fluid '(MELLINCOEF);
SHARE MELLINCOEF$

endmodule;

end;



