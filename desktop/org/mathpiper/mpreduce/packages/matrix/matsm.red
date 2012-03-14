module matsm; % Simplification of matrices.

% Author: Anthony C. Hearn.

% Copyright (c) 1998 Anthony C. Hearn. All rights reserved.

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


% This module is rife with essential references to RPLAC-based
% functions.

symbolic procedure matsm!*(u,v);
   % Matrix expression simplification function.
   matsm!*1 matsm u;

% symbolic procedure matsm!*1 u;
%    begin scalar sub2;
%       sub2 := !*sub2;  % Since we need value for each element.
%       u := 'mat . for each j in u collect
%                      for each k in j
%                         collect <<!*sub2 := sub2; !*q2a subs2 k>>;
%       !*sub2 := nil;   % Since all substitutions done.
%       return u
%    end;

symbolic procedure matsm!*1 u;
   begin
      % We use subs2!* to make sure each element simplified fully.
      u := 'mat . for each j in u collect
                     for each k in j collect !*q2a subs2!* k;
      !*sub2 := nil;   % Since all substitutions done.
      return u
   end;

symbolic procedure matsm u;
   begin scalar x,y;
      for each j in nssimp(u,'matrix) do
         <<y := multsm(car j,matsm1 cdr j);
           x := if null x then y else addm(x,y)>>;
      return x
   end;

symbolic procedure matsm1 u;
   %returns matrix canonical form for matrix symbol product U;
   begin scalar x,y,z; integer n;
    a:  if null u then return z
         else if eqcar(car u,'!*div) then go to d
         else if atom car u then go to er
         else if caar u eq 'mat then go to c1
         else if flagp(caar u,'matmapfn) and cdar u
            and getrtype cadar u eq 'matrix
          then x := matsm matrixmap(car u,nil)
         else <<x := lispapply(caar u,cdar u);
          if eqcar(x,'mat) then x := matsm x>>;
    b:  z := if null z then x
              else if null cdr z and null cdar z then multsm(caar z,x)
              else multm(x,z);
    c:  u := cdr u;
        go to a;
    c1: if not lchk cdar u then rerror(matrix,3,"Matrix mismatch");
        x := for each j in cdar u collect
                for each k in j collect xsimp k;
        go to b;
    d:  y := matsm cadar u;
        if (n := length car y) neq length y
          then rerror(matrix,4,"Non square matrix")
         else if (z and n neq length z)
          then rerror(matrix,5,"Matrix mismatch")
         else if cddar u then go to h
         else if null cdr y and null cdar y then go to e;
        x := subfg!*;
        subfg!* := nil;
        if null z then z := apply1(get('mat,'inversefn),y)
         else if null(x := get('mat,'lnrsolvefn))
          then z := multm(apply1(get('mat,'inversefn),y),z)
         else z := apply2(get('mat,'lnrsolvefn),y,z);
        subfg!* := x;
        % Make sure there are no power substitutions.
        z := for each j in z collect for each k in j collect
                 <<!*sub2 := t; subs2 k>>;
        go to c;
    e:  if null caaar y then rerror(matrix,6,"Zero divisor");
        y := revpr caar y;
        z := if null z then list list y else multsm(y,z);
        go to c;
     h: if null z then z := generateident n;
        go  to c;
    er: rerror(matrix,7,list("Matrix",car u,"not set"))
   end;

symbolic procedure lchk u;
   begin integer n;
        if null u or atom car u then return nil;
        n := length car u;
        repeat u := cdr u
           until null u or atom car u or length car u neq n;
        return null u
   end;

symbolic procedure addm(u,v);
   % Returns sum of two matrix canonical forms U and V.
   % Returns U + 0 as U. Patch by Francis Wright.
   if v = '(((nil . 1))) then u else       % FJW.
   for each j in addm1(u,v,function cons)
      collect addm1(car j,cdr j,function addsq);

symbolic procedure addm1(u,v,w);
   if null u and null v then nil
    else if null u or null v then rerror(matrix,8,"Matrix mismatch")
    else apply2(w,car u,car v) . addm1(cdr u,cdr v,w);

symbolic procedure tp u; tp1 matsm u;

put('tp,'rtypefn,'getrtypecar);

symbolic procedure tp1 u;
   %returns transpose of the matrix canonical form U;
   %U is destroyed in the process;
   begin scalar v,w,x,y,z;
        v := w := list nil;
        while car u do
         <<x := u;
           y := z := list nil;
           while x do
             <<z := cdr rplacd(z,list caar x);
               x := cdr rplaca(x,cdar x)>>;
           w := cdr rplacd(w,list cdr y)>>;
        return cdr v
   end;

symbolic procedure scalprod(u,v);
   %returns scalar product of two lists (vectors) U and V;
   if null u and null v then nil ./ 1
    else if null u or null v then rerror(matrix,9,"Matrix mismatch")
    else addsq(multsq(car u,car v),scalprod(cdr u,cdr v));

symbolic procedure multm(u,v);
   %returns matrix product of two matrix canonical forms U and V;
   (for each y in u
      collect for each k in x collect subs2 scalprod(y,k))
     where x = tp1 v;

symbolic procedure multsm(u,v);
   %returns product of standard quotient U and matrix standard form V;
   if u = (1 ./ 1) then v
    else for each j in v collect for each k in j collect multsq(u,k);

% Explicit substitution code for matrices.

symbolic procedure matsub(u,v);
   'mat . for each x in cdr v collect
              for each y in x collect subeval1(u,y);

put('matrix,'subfn,'matsub);

endmodule;

end;
