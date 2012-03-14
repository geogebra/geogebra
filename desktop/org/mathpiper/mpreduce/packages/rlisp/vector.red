module vector; % Definition of RLISP vectors and operations on them.

% Author: Anthony C. Hearn.

% Copyright (c) 1988 The RAND Corporation.  All rights reserved.

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


fluid '(!*fastvector);

global '(cursym!*);

switch fastvector;

% Add to system table.

flag('(vec!*),'vecfn);

% Parsing interface.

symbolic procedure xreadvec;
   % Expects a list of expressions enclosed by [, ].
   begin scalar cursym,delim,lst;
        if scan() eq '!*rsqb!* then <<scan(); return list 'list>>;
    a:      lst := aconc(lst,xread1 'group);
        cursym := cursym!*;
        scan();
        if cursym eq '!*rsqb!*
          then return if delim eq '!*semicol!* then 'progn . lst
                       else list('vec!*,'list . lst)
         else if null delim then delim := cursym
         else if not(delim eq cursym)
          then symerr("Syntax error: mixed , and ; in vector",nil);
        go to a
   end;

put('!*lsqb!*,'stat,'xreadvec);

newtok '((![) !*lsqb!*);

newtok '((!]) !*rsqb!*);

flag('(!*rsqb!*),'delim);

flag('(!*rsqb!*),'nodel);

symbolic procedure vec!* u;
   % Make a vector out of elements of u.
   begin scalar n,x;
      n := length u - 1;
      x := mkvect n;
      for i:= 0:n do <<putv(x,i,car u); u := cdr u>>;
      return x
   end;

% Evaluation interface.

% symbolic procedure setv(u,v);
%   <<set(u,v); put(u,'rtype,'vector); v>>;


% Length interface.


% Printing interface.


% Definitions of operations on vectors.

symbolic procedure getvect(u,vars,mode);
   expandgetv(car u,formlis(evalvecarg cdr u,vars,mode));

symbolic procedure expandgetv(u,v);
   if null v then u
    else expandgetv(list(if !*fastvector then 'igetv else 'getv,
                         u,car v),
                    cdr v);

symbolic procedure putvect(u,vars,mode);
   expandputv(caar u,formlis(evalvecarg cdar u,vars,mode),
              form1(cadr u,vars,mode));

symbolic procedure expandputv(u,v,w);
   if null cdr v
     then list(if !*fastvector then 'iputv else 'putv,u,car v,w)
    else expandputv(list(if !*fastvector then 'igetv else 'getv,
                         u,car v),
                    cdr v,w);

symbolic procedure evalvecarg u;
%   if u and null cdr u and vectorp car u
%     then for i:=0:upbv car u collect getv(car u,i) else
   if u and null cdr u and eqcar(car u,'vec!*)
       and eqcar(cadar u,'list)
     then cdadar u
    else u;

% Support for arrays defined in terms of vectors.

symbolic macro procedure mkarray u; {'mkar1,'list . cdr u};

symbolic procedure mkar1 u;
   begin scalar x;
      x := mkvect car u;
      if cdr u then for i:= 0:upbv x do putv(x,i,mkar1 cdr u);
      return x
   end;

symbolic macro procedure array u;
   % Create an array from the elements in u.
   list('vec!*,'list . cdr u);

endmodule;

end;
