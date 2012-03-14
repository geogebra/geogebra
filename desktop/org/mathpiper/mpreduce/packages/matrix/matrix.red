module matrix;  % Header for matrix package.

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


% This module has one reference to rplaca.

create!-package('(matrix matsm matpri extops bareiss det glmat nullsp
                  rank nestdom resultnt cofactor),nil);

fluid '(!*sub2 subfg!*);

global '(nxtsym!*);

symbolic procedure matrix u;
   % Declares list U as matrices.
   begin scalar w,x,y;
        for each j in u do
           if atom j then if null (x := gettype j)
                            then put(j,'rtype,'matrix)
                           else if x eq 'matrix
                            then <<lprim list(x,j,"redefined");
                                   put(j,'rtype,'matrix)>>
                           else typerr(list(x,j),"matrix")
            else if not idp car j then errpri2(j,'hold)
            else if not (x := gettype car j) or x eq 'matrix
             then <<if length j neq 3 then typerr(j,'matrix);
                    x := reval_without_mod cadr j;
                    if not fixp x or x<=0 then typerr(x,"positive integer");
                    y := reval_without_mod caddr j;
                    if not fixp y or y<=0 then typerr(y,"positive integer");
                    w := nil; for n := 1:x do w := nzero y . w;
                    put(car j,'rtype,'matrix);
                    put(car j,'avalue,list('matrix,'mat . w))>>
            else typerr(list(x,car j),"matrix")
   end;

rlistat '(matrix);

symbolic procedure nzero n;
   % Returns a list of N zeros.
   if n=0 then nil else 0 . nzero(n-1);

% Parsing interface.

symbolic procedure matstat;
   % Read a matrix.
   begin scalar x,y;
      if not (nxtsym!* eq '!() then symerr("Syntax error",nil);
   a: scan();
      if not (scan() eq '!*lpar!*) then symerr("Syntax error",nil);
      y := xread 'paren;
      if not eqcar(y,'!*comma!*) then y := list y else y := remcomma y;
      x := y . x;
      if nxtsym!* eq '!)
        then return <<scan(); scan(); 'mat . reversip x>>
       else if not(nxtsym!* eq '!,) then symerr("Syntax error",nil);
      go to a
   end;

put('mat,'stat,'matstat);

symbolic procedure formmat(u,vars,mode);
   'list . mkquote car u
     . for each x in cdr u collect('list . formlis(x,vars,mode));

put('mat,'formfn,'formmat);

put('mat,'i2d,'mkscalmat);

put('mat,'inversefn,'matinverse);

put('mat,'lnrsolvefn,'lnrsolve);

put('mat,'rtypefn,'quotematrix);

symbolic procedure quotematrix u; 'matrix;

flag('(mat tp),'matflg);

flag('(mat),'noncommuting);

put('mat,'prifn,'matpri);

flag('(mat),'struct);      % for parsing

put('matrix,'fn,'matflg);

put('matrix,'evfn,'matsm!*);

flag('(matrix),'sprifn);

put('matrix,'tag,'mat);

put('matrix,'lengthfn,'matlength);

put('matrix,'getelemfn,'getmatelem);

put('matrix,'setelemfn,'setmatelem);

symbolic procedure mkscalmat u;
   % Converts id u to 1 by 1 matrix.
   list('mat,list u);

symbolic procedure getmatelem u;
   % This differs from setmatelem in that let x=y, where y is a
   % matrix, should work.
   begin scalar x,y;
      if length u neq 3 then typerr(u,"matrix element");
      x := get(car u,'avalue);
      if null x or not(car x eq 'matrix) then typerr(car u,"matrix")
       else if not eqcar(x := cadr x,'mat)
        then if idp x then return getmatelem (x . cdr u)
         else rerror(matrix,1,list("Matrix",car u,"not set"));
      y := reval_without_mod cadr u;
      if not fixp y or y<=0 then typerr(y,"positive integer");
      x := nth(cdr x,y);
      y := reval_without_mod caddr u;
      if not fixp y or y<=0 then typerr(y,"positive integer");
      return nth(x,y)
   end;

symbolic procedure setmatelem(u,v);
   begin scalar x,y;
      if length u neq 3 then typerr(u,"matrix element");
      x := get(car u,'avalue);
      if null x or not(car x eq 'matrix) then typerr(car u,"matrix")
       else if not eqcar(x := cadr x,'mat)
        then rerror(matrix,10,list("Matrix",car u,"not set"));
      y := reval_without_mod cadr u;
      if not fixp y or y<=0 then typerr(y,"positive integer");
      x := nth(cdr x,y);
      y := reval_without_mod caddr u;
      if not fixp y or y<=0 then typerr(y,"positive integer");
      return rplaca(pnth(x,y),v)
   end;

symbolic procedure matlength u;
   if not eqcar(u,'mat) then rerror(matrix,2,list("Matrix",u,"not set"))
    else list('list,length cdr u,length cadr u);

% Aggregate Property.  Commented out for now.

symbolic procedure matrixmap(u,v);
   if flagp(car u,'matmapfn)
    then matsm!*1 for each j in matsm cadr u collect
            for each k in j collect simp!*(car u . mk!*sq k . cddr u)
   else if flagp(car u,'matfn) then reval2(u,v)
    else typerr(car u,"matrix operator");

put('matrix,'aggregatefn,'matrixmap);

flag('(df int taylor),'matmapfn);

flag('(det trace),'matfn);

% symbolic procedure mk!*sq2 u;
%    begin scalar x;
%        x := !*sub2;   % Since we need value for each element.
%        u := subs2 u;
%        !*sub2 := x;
%        return mk!*sq u
%   end;

endmodule;

end;
