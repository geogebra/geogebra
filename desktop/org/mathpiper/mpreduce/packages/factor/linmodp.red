module linmodp;  % Routines for solving linear equations mod p.

% Authors: A. C. Norman and P. M. A. Moore, 1979.

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


fluid '(prime!-base);

symbolic procedure lu!-factorize!-mod!-p(a,n);
   % A is a matrix of size N*N. Overwrite it with its LU factorization.
   begin scalar w;
    for i:=1:n do begin
     scalar ii,pivot;
     if w eq 'singular then return nil;
     ii:=i;
     while n>=ii and ((pivot:=getm2(a,ii,i))=0
        or iremainder(pivot,prime!-base)=0) do ii := ii+1;
     if ii>n then return(w := 'singular);
     if not(ii=i) then begin
         scalar temp;
         temp:=getv(a,i);
         putv(a,i,getv(a,ii));
         putv(a,ii,temp) end;
     putm2(a,i,0,ii); % Remember pivoting information;
     pivot:=modular!-reciprocal pivot;
     putm2(a,i,i,pivot);
     for j:=i+1:n do
       putm2(a,i,j,modular!-times(pivot,getm2(a,i,j)));
     for ii:=i+1:n do begin
        scalar multiple;
        multiple:=getm2(a,ii,i);
        for j:=i+1:n do
           putm2(a,ii,j,modular!-difference(getm2(a,ii,j),
             modular!-times(multiple,getm2(a,i,j)))) end end;
     return w
   end;

symbolic procedure back!-substitute(a,v,n);
   % A is an N*N matrix as produced by LU-FACTORIZE-MOD-P, and V is a
   % vector of length N. Overwrite V with solution to linear equations.
   begin
     for i:=1:n do
         begin scalar ii;
            ii:=getm2(a,i,0); % Pivot control;
            if ii neq i
              then begin scalar temp;
                      temp:=getv(v,i);
                      putv(v,i,getv(v,ii));
                      putv(v,ii,temp)
                   end
         end;
     for i:=1:n do begin
         putv(v,i,times!-mod!-p(!*n2f getm2(a,i,i),getv(v,i)));
         for ii:=i+1:n do
            putv(v,ii,difference!-mod!-p(getv(v,ii),
               times!-mod!-p(getv(v,i),!*n2f getm2(a,ii,i)))) end;
             % Now do the actual back substitution;
     for i:=n-1 step -1 until 1 do
       for j:=i+1:n do
         putv(v,i,difference!-mod!-p(getv(v,i),
           times!-mod!-p(!*n2f getm2(a,i,j),getv(v,j))));
     return v
   end;

endmodule;

end;
