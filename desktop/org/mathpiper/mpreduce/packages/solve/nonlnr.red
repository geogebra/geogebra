module nonlnr; % Interface to Groebner code for solving non-linear eqns.

% Authors: Anthony C. Hearn and Herbert Melenk.

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


fluid '(!*trnonlnr vars!*);

global '(loaded!-packages!*);

switch trnonlnr;

symbolic procedure solvenonlnrsys(u,vars!*);
   % Solve list of expressions u with respect to variable list vars!*.
   begin scalar solutions,p,s;
      if not('groebner memq loaded!-packages!*)
        then load!-package 'groebner;
      if !*trnonlnr then lprim "Entering Groebner ...";
      solutions :=
         groesolveeval(list('list . for each x in u collect prepf x,
                            'list . vars!*));
      if !*trnonlnr then lprim "Leaving Groebner ...";
      % Reform result so that !*solvelist2solveeqlist understands it.
%     return for each solu in cdr solutions collect
      return t . for each solu in cdr solutions collect
           <<s := nil;
              reverse
                (for each eqn in reverse cdr solu collect
                     <<p := subsq(simp caddr eqn,s);
                       if member(cadr eqn,vars!*) then
                           s := (cadr eqn . prepsq p) . s;
                       p>>)
                    . (for each eqn in cdr solu collect cadr eqn)
                    . list 1
           >>;
   end;

endmodule;

end;
