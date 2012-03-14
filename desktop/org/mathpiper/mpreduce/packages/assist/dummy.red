module dummy; % Header Module for REDUCE versions from 3.5 to 3.7.

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


create!-package('(dummy perms backtrck dummycnt),'(contrib assist));

% % *****************************************************************
%
%            Author: A. Dresse
%
%          Revision: H. Caprasse
%
% All problems should be submitted to H. Caprasse:
%     hubert.caprasse@ulg.ac.be
%
% Version and Date:  Version 1.1, 15 January 1999.
%
% This package is built on top of ASSIST.RED version 2.31 which runs in
% REDUCE 3.6 and REDUCE 3.7. and is available inside the REDUCE library.
%
% Revision history to versions 1.1 :
% %   ****************************************************************
% 30/03/95 : reference to totalcopy eliminated and replaced by
% FULLCOPY
% 15/09/98 : NODUM_VARP and LIST_IS_ALL_FREE created
%          : DV_SKELPROD corrected and modified to allow extension
%          : to tensor-like objects (!~dva introduced).
%          : DUMMY_BASE and DUMMY_NAMES modified
%          : SHOW_DUMMY_NAMES to display dummy names has been created.
%          : Several local variables eliminated.
% 01/01/99 : DV_SKEL2FACTOR1 modified.
% % ******************************************************************


load_package assist;


symbolic procedure fullcopy s;
   % A subset of the PSL totalcopy function.
   if pairp s then fullcopy car s . fullcopy cdr s
    else if vectorp s then
        begin scalar cop; integer si;
        si:=upbv s;
        cop:=mkvect si;
        for i:=0:si do putv(cop,i,fullcopy getv(s,i));
        return cop end
    else s;

endmodule;

end;
