module farith;  % Operators for fast arithmetic.

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


symbolic procedure iplus2(u,v); u+v;

symbolic procedure itimes2(u,v); u*v;

symbolic procedure isub1 a; a-1;

symbolic procedure iadd1 a; a+1;

remprop('iminus,'infix);

symbolic procedure iminus a; -a;

symbolic procedure idifference(a,b); a-b;

symbolic procedure iquotient(a,b); a/b;

symbolic procedure iremainder(a,b); remainder(a,b);

symbolic procedure igreaterp(a,b); a>b;

symbolic procedure ilessp(a,b); a<b;

symbolic procedure iminusp a; a<0;

symbolic smacro procedure iequal(u,v); eqn(u,v);

newtok '((!#) hash);
newtok '((!# !+) iplus2);
newtok '((!# !-) idifference);
newtok '((!# !*) itimes2);
newtok '((!# !/) iquotient);
newtok '((!# !>) igreaterp);
newtok '((!# !<) ilessp);
newtok '((!# !=) iequal);

infix #+,#-,#*,#/,#>,#<,#=;

precedence #+,+;
precedence #-,-;
precedence #*,*;
precedence #/,/;
precedence #>,>;
precedence #<,<;
precedence #=,=;

deflist('((idifference iminus)),'unary);

deflist('((iminus iminus)),'unary);

deflist('((iminus iplus2)), 'alt);

endmodule;

end;
