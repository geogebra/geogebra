module gauss;  % Solve linear system.

% Author: H. Melenk, ZIB, Berlin

% Copyright (c): ZIB Berlin 1994, all rights resrved

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


fluid '(!*noequiv accuracy!* !*exptexpand);
global '(iterations!* !*trnumeric erfg!*);

put('igetv,'setqfn,'(lambda (v i x) (iputv v i x)));

smacro procedure s(i,j); igetv(igetv(m,i),j);

symbolic procedure rdsolvelin (u,b);
     % U is a n*n matrix with numbers;
     % b is a righthand side. Result is a vector of solutions.
     % Gaussian elimination with partial pivoting.
  begin integer n,n1,k; scalar m,w,r,x,y,err;
    n:=length b; n1:=n#+1;
      % establish system matrix.
    m:=mkvect(n#+1); r:=mkvect(n#+1);
    for i:=1:n do
    <<w:=mkvect(n#+2);
      for j:=1:n do iputv(w,j,nth(nth(u,i),j));
      iputv(w,n1,nth(b,i));
      iputv(m,i,w);
    >>;
      % elimination loop.
    for i:=1:n-1 do if not err then
    << % pivot search
      x:=dm!: abs s(i,i); k:=i;
      for j:=i+1:n do dm!:
      dm!: if (y:=abs s(j,i))>x then <<x:=y; k:=j>>;
      if i neq k then
      <<y:=igetv(m,i); iputv(m,i,igetv(m,k)); iputv(m,k,y)>>;
        % row elimination
      if null s(i,i) then err:=t
      else
      <<x:=dm!: (1/s(i,i));
        for j:=i+1:n do
        <<y:=dm!:(s(j,i)*x);
          for k:=i:n1 do s(j,k):=dm!:(s(j,k) - y*s(i,k));
      >>>>;
     >>;

      % backsubstitution.
     for i:=n step -1 until 1 do if not err then
     <<w:=s(i,n1);
       for j:=i#+1 : n do
        dm!:(w:=w-s(i,j)*igetv(r,j));
       if null s(i,i) then err :=t else
         iputv(r,i,dm!:(w/s(i,i)));
     >>;

     return if err then nil else for i:=1:n collect igetv(r,i);
   end;

remprop('s,'smacro);
remprop('s,'number!-of!-args);

endmodule;

end;
