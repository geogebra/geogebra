module xvect; % Support for vectors with adaptive length.

% Author: Herbert Melenk, ZIB-Berlin.

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


% Note: CSL version uses 1024, PSL 128.

symbolic procedure mkxvect(); {mkvect(1024)};

symbolic procedure xputv(v,n,w);
  begin scalar i,j;
    i:=iquotient(n,1024); j:=iremainder(n,1024);
    while(i>0) do
    <<if null cdr v then cdr v:= mkxvect();
      v:=cdr v; i:=i #- 1>>;
    iputv(car v,j,w);
    return w;
  end;

symbolic procedure xgetv(v,n);
  begin scalar i,j,w;
    i:=iquotient(n,1024); j:=iremainder(n,1024);
    while(i>0 and v) do
    <<v:=cdr v; i:=i #- 1>>;
    w:=if v then igetv(car v,j);
    return w
  end;

endmodule;

end;
