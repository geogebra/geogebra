module pmrules;   % Basic rules for PM pattern matcher.

% Author:  Kevin McIsaac.

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


algebraic;

% Define logical operators;
% These routines are used so often they should be coded in LISP
% for efficiency.

operator ~; deflist('((!~ !~)),'unary); %precedence ~,not;

infix &; deflist('((!& !&)),'unary); precedence &, and;

remprop('!&,'rtypefn);  % Interference with FIDE package.

infix |; deflist('((!| !|)),'unary); precedence |, or;

flag('( & |), 'nary);

flag('( & |),'symmetric);

&(t) :- t; % We must have this else the fourth rule => &(t) -> &() -> 0

&(0) :- 0;

&(0, ??b) :- 0;

&(t, ??b) ::- &(??b);

&(?a,?a,??b) ::- &(?a,??b);

&(?a,~?a,??b) ::- 0;

|(t) :- t;
|(0) :- 0;
|(t,??a) :- t;
|(0,??a) ::- |(??a);
|(?a,?a,??b) ::- |(?a,??b);
|(?a,~?a) :- t;
|(?a,~?a,??b) ::- |(??b);

~(t) :- 0;
~(0) :- t;


% Define SMP predicates in terms of their REDUCE equivalents.


symbolic procedure simpbool u;
  begin scalar x;
  x := get(car u,'boolfn) or car u;
  u := for each j in cdr u collect reval j;
  u := apply (x, u);
  return (if u then !*k2f T else 0) ./ 1
 end;

flag('(numberp fixp), 'full);

put('numberp,'simpfn,'simpbool);
put('fixp,'simpfn,'simpbool);



operator numbp, posp, intp, natp, oddp, evnp, complexp, listp;

numbp(?n _=numberp(?n)) :- t;
numbp(?n/?m _=(numberp(?n)&numberp(?m))) :- t;

posp(?n _=(numbp(?n)&?n > 0)) :- t;
posp(?n _=(numbp(?n)&~(?n > 0))) :- 0;

intp(?n _=(numbp(?n)&fixp(?n))) :- t;
intp(?n _=(numbp(?n)&~ fixp(?n))) :- 0;

natp(?i _=(numbp(?i)& intp(?i)&?i>0)) :-t;
natp(?i _=(numbp(?i)&~(intp(?i)&?i>0))) :- 0;

oddp(?x _=(numbp(?x)&intp((?x+1)/2))) :- t;
oddp(?x _=(numbp(?x)&~ intp((?x+1)/2))) :- 0;

evnp(?x _=(numbp(?x)&intp(?x/2))) :- t;
evnp(?x _=(numbp(?x)&~ intp(?x/2))) :- 0;

complexp(i) :- t;
complexp(??b*i) :- t;
complexp(??a + i) :- t;
complexp(??a + ??b*i) :- t;

listp({??x}) :- t;
listp(?x) :- 'nil;

%Polyp
%Primep
%Projp
%Ratp
%Contp
%Fullp
%Symbp

endmodule;

end;
