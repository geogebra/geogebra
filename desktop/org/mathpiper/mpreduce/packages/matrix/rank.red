module rank;

% Author: Eberhard Schruefer.

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


% Module for calculating the rank of a matrix or a system of linear
% equations.
% Format: rank <matrix> : rank <list of equations>.

symbolic procedure rank!-eval u;
   begin scalar n;
      if cdr u then rerror(matrix,17,"Wrong number of arguments")
       else if getrtype (u := car u) eq 'matrix
         then return rank!-matrix matsm u
       else if null eqcar(u := aeval u,'list) then typerr(u,"matrix")
       else return rank!-matrix
         for each row in cdr u collect
           if not eqcar(row,'list)
               then rerror(matrix,15,"list not in matrix shape")
            else <<row := cdr row;
                   if null n then n := length row
                    else if n neq length row
                     then rerror(matrix,151,"list not in matrix shape");
                   for each j in row collect simp j>>
   end;


put('rank,'psopfn,'rank!-eval);

symbolic procedure rank!-matrix u;
   begin scalar x,y,z; integer m,n;
     z := 1;
     for each v in u do
          <<y := 1;
            for each w in v do y := lcm(y,denr w);
            m := 1;
            x := nil;
            for each j in v do
              <<if numr j then
                x := list m .* multf(numr j,quotf(y,denr j)) .+ x;
                m := m + 1>>;
            if y := c!:extmult(x,z)
               then <<z := y; n := n + 1>>>>;
     return n
    end;

endmodule;

end;
