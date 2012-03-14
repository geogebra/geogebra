module weight; % Asymptotic command package.

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


% Modified by F.J. Wright@Maths.QMW.ac.uk, 18 May 1994,
% mainly to return the previous settings rather than nothing.

fluid '(asymplis!* wtl!*);

flag('(k!*),'reserved);

% Asymptotic list and weighted variable association lists.

symbolic procedure weight u;
   % Returns previous weight list for the argument variables, omitting
   % any unweighted variables.  Returns the current weight without
   % resetting it for any argument that is a variable rather than a
   % weight equation, and with no arguments returns all current
   % variable weights.
   makelist if null car u then
      for each x in wtl!* collect {'equal, car x, cdr x}
   else <<
      % Make sure asymplis!* is initialized.
      if null atsoc('k!*,asymplis!*)
         then asymplis!* := '(k!* . 2) . asymplis!*;
      rmsubs();
      % Build the output list while processing the input:
      for each x in u join
      begin scalar y,z;
         if eqexpr x then <<
            z := reval caddr x;
            if not fixp z or z<=0 then typerr(z,"weight");
            x := cadr x >>;
         y := !*a2kwoweight x;
         x := if (x := atsoc(y,wtl!*)) then {{'equal, car x, cdr x}};
         if z then wtl!* :=  (y . z) . delasc(y,wtl!*);
         return x
      end
   >>;


symbolic procedure wtlevel n;
   begin scalar oldn;
      % Returns previous wtlevel; with no arg returns current wtlevel
      % without resetting it.
      oldn := (if x then cdr x - 1 else 1)
              where x = atsoc('k!*,asymplis!*);
      if car n then <<
         n := reval car n;
         if not fixp n or n<0 then typerr(n,"weight level");
         if n<oldn then rmsubs();
         if n neq oldn
           then asymplis!*:= ('k!* . (n+1)) . delasc('k!*,asymplis!*)>>;
      return oldn
   end;

rlistat '(weight wtlevel);
% but preserve current mode as mode of result:
flag('(weight wtlevel), 'nochange);

% algebraic let k!***2=0;

endmodule;

end;
