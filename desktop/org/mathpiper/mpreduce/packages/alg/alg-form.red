module alg!-form;   % Some particular algebraic mode analysis functions.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND.  All rights reserved.

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


global '(inputbuflis!* resultbuflis!* ws);

symbolic procedure forminput(u,vars,mode);
   begin scalar x;
      if length cdr u neq 1
        then rerror('alg,1,list("input called with",
                                length cdr u,
                                "arguments instead of 1"));
      u := cadr u; if eqcar(u,'!:int!:) then u := cadr u;
      if null(x := assoc(u,inputbuflis!*))
        then rerror(alg,1,list("Entry",u,"not found"));
      return caddr x
   end;

put('input,'formfn,'forminput);

symbolic procedure formws(u,vars,mode);
   begin scalar x;
      if length cdr u neq 1
        then rerror('alg,1,list("ws called with",
                                length cdr u,
                                "arguments instead of 1"));
      u := cadr u; if eqcar(u,'!:int!:) then u := cadr u;
      if x := assoc(u,resultbuflis!*) then return mkquote cdr x
       else rerror(alg,2,list("Entry",u,"not found"))
   end;

put('ws,'formfn,'formws);

endmodule;

end;
