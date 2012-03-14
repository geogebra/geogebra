module transfns;

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

algebraic procedure trigexpand wws;
  wws where { sin(~x+~y) => sin(x)*cos(y)+cos(x)*sin(y),
              cos(~x+~y) => cos(x)*cos(y)-sin(x)*sin(y),
              sin((~n)*~x) => sin(x)*cos((n-1)*x)+cos(x)*sin((n-1)*x)
                   when fixp n and n>1,
              cos((~n)*~x) => cos(x)*cos((n-1)*x)-sin(x)*sin((n-1)*x)
                   when fixp n and n>1 };

algebraic procedure hypexpand wws;
  wws where {sinh(~x+~y) => sinh(x)*cosh(y)+cosh(x)*sinh(y),
             cosh(~x+~y) => cosh(x)*cosh(y)+sinh(x)*sinh(y),
           sinh((~n)*~x) => sinh(x)*cosh((n-1)*x)+cosh(x)*sinh((n-1)*x)
                   when fixp n and n>1,
           cosh((~n)*~x) => cosh(x)*cosh((n-1)*x)+sinh(x)*sinh((n-1)*x)
                   when fixp n and n>1 };

operator !#ei!&; !#ei!&(0):=1;

trig!#ei!& := {!#ei!&(~x)**(~n) => !#ei!&(n*x),
               !#ei!&(~x)*!#ei!&(~y) => !#ei!&(x+y)};

let trig!#ei!&;

algebraic procedure trigreduce wws;
        <<wws:=(wws where {cos(~x) => (!#ei!&(x)+!#ei!&(-x))/2,
                           sin(~x) => -i*(!#ei!&(x)-!#ei!&(-x))/2});
          wws:=(wws where {!#ei!&(~x) => cos x +i*sin x})>>;

algebraic procedure hypreduce wws;
        <<wws:=(wws where {cosh(~x) => (!#ei!&(x)+!#ei!&(-x))/2,
                           sinh(~x) => (!#ei!&(x)-!#ei!&(-x))/2});
          wws:=(wws where {!#ei!&(~x) => cosh(x)+sinh(x)})>>;

endmodule;

end;
