module where;  % Support for a where construct.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


% global '(fixedpreclis!*);

symbolic procedure formwhere(u,vars,mode);
   begin scalar expn,equivs,y,z;
     expn := cadr u;
     equivs := remcomma caddr u;
     if not(mode eq 'symbolic)
       then return formc(list('whereexp,'list . equivs,expn),vars,mode);
     for each j in equivs do
        if not atom j and car j memq '(equal setq)
          then <<y := caddr j . y; z := cadr j . z>>
         else rerror(rlisp,17,list(j,"invalid in WHERE statement"));
     return formc(list('lambda,reversip z,expn) . reversip y,vars,mode)
   end;

put('where,'formfn,'formwhere);

% fixedpreclis!* := 'where . fixedpreclis!*;  % Where has special place.

% mkprec();

endmodule;

end;
