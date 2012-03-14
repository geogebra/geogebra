module array; % Array statement.

% Author: Anthony C. Hearn.
% Modifications by: Nancy Kirkwood.

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


% These definitions are very careful about bounds checking. Appropriate
% optimizations in a given system might really speed things up.

fluid '(!*rlisp88);

global '(erfg!*);

symbolic procedure getel u;
   % Returns the value of the array element U.
   (if length n neq length cdr u
      then rerror(rlisp,21,"Incorrect array reference")
     else getel1(cadr get(car u,'avalue),cdr u,n))
    where n=get(car u,'dimension);

symbolic procedure getel1(u,v,dims);
   if null v then u
    else if not fixp car v then typerr(car v,"array index")
    else if car v geq car dims or car v < 0
     then rerror(rlisp,21,"Array out of bounds")
    else getel1(getv(u,car v),cdr v,cdr dims);

symbolic procedure setel(u,v);
   % Sets array element U to V and returns V.
   (if length n neq length cdr u
           then rerror(rlisp,22,"Incorrect array reference")
     else setel1(cadr get(car u,'avalue),cdr u,v,n))
    where n=get(car u,'dimension);

symbolic procedure setel1(u,v,w,dims);
   if not fixp car v then typerr(car v,"array index")
    else if car v geq car dims or car v < 0
     then rerror(rlisp,23,"Array out of bounds")
    else if null cdr v then putv(u,car v,w)
    else setel1(getv(u,car v),cdr v,w,cdr dims);

symbolic procedure dimension u; get(u,'dimension);


comment further support for REDUCE arrays;

symbolic procedure typechk(u,v);
   begin scalar x;
      if (x := gettype u) eq v or x eq 'parameter
        then lprim list(v,u,"redefined")
       else if x then typerr(list(x,u),v)
   end;

symbolic procedure arrayfn(u,v);
   % U is the defining mode, V a list of lists, assumed syntactically
   % correct. ARRAYFN declares each element as an array unless a
   % semantic mismatch occurs.
   begin scalar y;
      for each x in v do
         <<typechk(car x,'array);
       y := add1lis for each z in cdr x collect lispeval z;
       if null erfg!*
         then <<put(car x,'rtype,'array);
            put(car x,'avalue,list('array,mkarray1(y,u)));
            put(car x,'dimension,y)>>>>
   end;

flag('(arrayfn),'nochange);

symbolic procedure add1lis u;
   if null u then nil else (car u+1) . add1lis cdr u;

symbolic macro procedure mkarray u;
   if null !*rlisp88 then mkarray1(u,'algebraic) else
     list('mkar1,'list . cdr u);

symbolic procedure mkarray1(u,v);
   % U is a list of positive integers representing array bounds, V
   % the defining mode. Value is an array structure.
   if null u then if v eq 'symbolic then nil else 0
    else begin integer n; scalar x;
      n := car u - 1;
      x := mkvect n;
      for i:=0:n do putv(x,i,mkarray1(cdr u,v));
      return x
   end;

put('array,'stat,'rlis);

flag ('(array arrayfn),'eval);

symbolic procedure formarray(u,vars,mode);
   begin scalar x;
      x := cdr u;
      while x do <<if atom x then typerr(x,"Array List")
                  else if atom car x or not idp caar x
                         or not listp cdar x
                  then typerr(car x,"Array declaration");
                   x := cdr x>>;
      u := for each z in cdr u collect intargfn(z,vars,mode);
      %ARRAY arguments must be returned as quoted structures;
      return list('arrayfn,mkquote mode,'list . u)
   end;

put('array,'formfn,'formarray);

put('array,'rtypefn,'arraychk);

symbolic procedure arraychk u;
   % If arraychk receives NIL, it means that array name is being used
   % as an identifier. We no longer permit this.
   if null u then 'array else nil;
%  nil;

put('array,'evfn,'arrayeval);

symbolic procedure arrayeval(u,v);
   % Eventually we'll support this properly.
   if not atom u then rerror(rlisp,24,"Array arithmetic not defined")
    else u;

put('array,'lengthfn,'arraylength);

symbolic procedure arraylength u; 'list . get(u,'dimension);

endmodule;

end;
