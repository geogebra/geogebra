% ----------------------------------------------------------------------
% $Id: mri_ofsf.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008-2009 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(mri_ofsf_rcsid!* mri_ofsf_copyright!*);
   mri_ofsf_rcsid!* :=
      "$Id: mri_ofsf.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   mri_ofsf_copyright!* := "Copyright (c) 2008-2009 T. Sturm"
>>;

module mri_ofsf;

load!-package 'redlog;
load!-package 'ofsf;

rl_copyc('mri_ofsf,'ofsf);

rl_bbiadd('mri_ofsf,'rl_simplat1!*,'mri_simplat1);
rl_bbiadd('mri_ofsf,'rl_negateat!*,'mri_negateat);
rl_bbiadd('mri_ofsf,'rl_simplb!*,'mri_simplb);  % for now
rl_bbiadd('mri_ofsf,'rl_bsatp!*,'mri_bsatp);    % for now

rl_cswadd('mri_ofsf,'rlsism,nil);
rl_cswadd('mri_ofsf,'rlsusi,nil);

endmodule;

end;  % of file
