module algint; % Header for REDUCE algebraic integration package.

% Authors: J. Davenport and A. C. Hearn.

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


create!-package('(algint afactor algfn antisubs coates coatesid findmagc
                  findres finitise fixsubf fracdi genus intbasis jhddiff
                  jhdriver linrel log2atan maninp modify modlineq nagell
                  nbasis places precoats removecm sqfrnorm substns
                  inttaylr torsionb wstrass zmodule),
                  % algnums hidden phantoms primes
                '(int alg));

% Other packages needed.

load!-package 'int;

% Various functions used in the algebraic integrator.

symbolic smacro procedure divsf(u,v); sqrt2top(u ./ v);

symbolic smacro procedure maninp(u,v,w);
   interr "MANINP called -- not implemented in this version";

symbolic smacro procedure readclock; time();

symbolic procedure superprint u; prettyprint u;


% Various selectors written as macros.

symbolic smacro procedure argof u;
   % Argument of a unary function.
   cadr u;

symbolic smacro procedure lsubs u; car u;

symbolic smacro procedure rsubs u; cdr u;

symbolic smacro procedure lfirstsubs u; caar u;

symbolic smacro procedure rfirstsubs u; cdar u;


% Selectors for the Taylor series structure.

% Format is:
%function.((first.last computed so far) . assoc list of computed terms).

% ***store-hack-1***:
% remove this macro if more store is available.

symbolic smacro procedure tayshorten u; nil;

symbolic smacro procedure taylordefn u; car u;

symbolic smacro procedure taylorfunction u; caar u;

symbolic smacro procedure taylornumbers u; cadr u;

symbolic smacro procedure taylorfirst u; caadr u;

symbolic smacro procedure taylorlast u; cdadr u;

symbolic smacro procedure taylorlist u; cddr u;

symbolic smacro procedure taylormake(fn,nums,alist);
   fn . (nums . alist);

endmodule;

end;
