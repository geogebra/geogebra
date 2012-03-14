module int!-table;  % Definition of integrals by means of patterns.

% Authors: John P. Fitch and Anthony C. Hearn.

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

%Patterns for integration of various logarithmic cases;

for all x,a,b,c,d such that a freeof x and b freeof x and c freeof x
    and d freeof x
   let int(log(a*x+b)/(c*x+d),x)=
           log(c*x+d)*log((b*c-a*d)/c)/c- dilog(c*(a*x+b)/(b*c-a*d))/c;
% A=1;
for all x,b,c,d such that b freeof x and c freeof x and d freeof x
   let int(log(x+b)/(c*x+d),x)=
           log(c*x+d)*log((b*c-d)/c)/c - dilog(c*(x+b)/(b*c-d))/c;
% B=0;
for all x,a,c,d such that a freeof x and c freeof x and d freeof x
   let int(log(a*x)/(c*x+d),x)=
           log(c*x+d)*log(-a*d/c)/c - dilog(-c*x/d)/c;
% C=1;
for all x,a,b,d such that a freeof x and b freeof x and d freeof x
   let int(log(a*x+b)/(x+d),x)=
           log(x+d)*log(b-a*d)-dilog((a*x+b)/(b-a*d));
% D=0;
for all x,a,b,c such that a freeof x and b freeof x and c freeof x
   let int(log(a*x+b)/(c*x),x)= log(c*x)*log(b)/c - dilog((a*x+b)/b)/c;
% A=1, B=0;
for all x,c,d such that c freeof x and d freeof x
   let int(log(x)/(c*x+d),x)= log(c*x+d)*log(-d/c)/c - dilog(-c*x/d)/c;
% A=1, C=1;
for all x,b,d such that b freeof x and d freeof x
   let int(log(x+b)/(x+d),x)= log(x+d)*log(b-d) - dilog((x+b)/(b-d));
% A=1, D=0;
for all x,b,c such that b freeof x and c freeof x
   let int(log(x+b)/(c*x),x)= log(c*x)*log(b)/c - dilog((x+b)/b)/c;
% B=0, C=1;
for all x,a,d such that a freeof x and d freeof x
   let int(log(a*x)/(x+d),x)= log(x+d)*log(-a*d) - dilog(-x/d);
% C=1, D=0;
for all x,a,b such that a freeof x and b freeof x
   let int(log(a*x+b)/x,x)= log(x+d)*log(-d) - dilog(-x/d);
% A=1, C=1, D=0;
for all x,b such that b freeof x
   let int(log(x+b)/x,x)= log(x)*log(b) - dilog((x+b)/b);
% A=1, B=0, C=1;
for all x,d such that d freeof x
   let int(log(x)/(x+d),x)= log(x+d)*log(-d) - dilog(-x/d);

endmodule;

end;
