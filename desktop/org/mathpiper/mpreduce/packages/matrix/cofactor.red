module cofactor;   % Cofactor operator.

% Author: Alan Barnes <barnesa@kirk.aston.ac.uk>.

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


comment

Syntax:  COFACTOR(MATRIX:matrix,ROW:integer,COLUMN:integer):algebraic

The cofactor of the element in row ROW and column COLUMN of matrix
MATRIX is returned.  Errors occur if ROW or COLUMN do not simplify to
integer expressions or if MATRIX is not square;

symbolic procedure cofactorq (u,i,j);
   begin integer len;
      len:= length u;
      if not(i>0 and i<len+1)
        then rerror(matrix,20,"Row number out of range");
      if not(j>0 and j<len+1)
       then rerror(matrix,21,"Column number out of range");
       foreach x in u do
         if length x neq len then rerror(matrix,22,"non-square matrix");
      u := remove(u,i);
      matrix_clrhash();
      u := detq1(u,len-1,2**(j-1));
      matrix_clrhash();
      if remainder(i+j,2)=1 then u := negsq u;
      return u;
  end;

put ('cofactor,'simpfn,'simpcofactor);

symbolic procedure simpcofactor u;
   cofactorq(matsm car u,ieval cadr u,ieval carx(cddr u,'cofactor));

endmodule;

end;
