module algbool; % Evaluation functions for algebraic boolean operators.

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


symbolic procedure evalequal(u,v);
   begin scalar x;
      return if (x := getrtype u) neq getrtype v then nil
              else if null x
               then numberp(x := reval list('difference,u,v))
                       and zerop x
              else u=v
   end;

put('equal,'boolfn,'evalequal);

% symbolic procedure equalreval u; 'equal . revlis u;  % defined in eqn.

% put('equal,'psopfn,'equalreval);

put('equal,'rtypefn,'quoteequation);

symbolic procedure quoteequation u; 'equation;

symbolic procedure evalgreaterp(u,v);
   (lambda x;
    if not atom denr x or not domainp numr x
      then typerr(mk!*sq if minusf numr x then negsq x else x,"number")
     else numr x and !:minusp numr x)
        simp!* list('difference,v,u);

put('greaterp,'boolfn,'evalgreaterp);

symbolic procedure evalgeq(u,v); not evallessp(u,v);

put('geq,'boolfn,'evalgeq);

symbolic procedure evallessp(u,v); evalgreaterp(v,u);

put('lessp,'boolfn,'evallessp);

symbolic procedure evalleq(u,v); not evalgreaterp(u,v);

put('leq,'boolfn,'evalleq);

symbolic procedure evalneq(u,v); not evalequal(u,v);

put('neq,'boolfn,'evalneq);

symbolic procedure evalnumberp u;
   (if atom x then numberp x
     else if not(car x eq '!*sq) or not atom denr cadr x then nil
     else (atom y or flagp(car y,'numbertag)) where y=numr cadr x)
    where x=aeval u;

put('numberp,'boolfn,'evalnumberp);

% Number tags.

flag('(!:rd!: !:cr!: !:rn!: !:crn!: !:mod!: !:gi!:),'numbertag);

symbolic procedure ratnump x;
   % Returns T iff any prefix expression x is a rational number.
   atom numr(x := simp!* x) and atom denr x;

flag ('(ratnump), 'boolean);

endmodule;

end;
