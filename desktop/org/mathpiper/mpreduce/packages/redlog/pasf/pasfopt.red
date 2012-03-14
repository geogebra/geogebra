% ----------------------------------------------------------------------
% $Id: pasfopt.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 A. Dolzmann and T. Sturm
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
   fluid '(pasf_opt_rcsid!* pasf_opt_copyright!*);
   pasf_misc_rcsid!* :=
      "$Id: pasfopt.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   pasf_misc_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann, T. Sturm"
>>;

module pasfopf;
% Presburger arithmetic standard form optimization. This module was introduced
% by lasaruk to experiment with linear optimization.

procedure pasf_opt(cl,targ,parml,nproc);
   % Presburger arithmetic standard form linear optimization. [cl] is a list
   % of constraints; [targ] is the cost function; [paraml] is the list of
   % parameters; [nproc] is NOT COMMENTED. Returns optimal solutions of the
   % problem if any exist.
   rederr {"Linear optimization not yet implemented in context PASF"};

endmodule; % pasfopt

end; % of file
