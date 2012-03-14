module xmodule; % Support for "exemplary" module use.

% Author: Anthony C. Hearn.

% Copyright (c) 1995 RAND.  All rights reserved.

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


remprop('xmodule,'newnam);

load_package rcref;

fluid '(!*nocrefpri intfns!* modulename!*);

fluid '(!*backtrace !*mode !*nocrefpri);

global '(exportslist!* importslist!* loaded!-packages!* mode!-list!*);

global '(entpts!* gseen!* seen!* tseen!* xseen!* nolist!* undefg!*);

symbolic procedure xmodule u;
   % Sets up an "exemplary" module definition.
   begin scalar x,y;
      modulename!* := u;
      mode!-list!* := !*mode . mode!-list!*;
      !*mode := 'symbolic;
      while (y := command()) neq '(symbolic (endmodule)) do
         progn(if eqcar(cadr y,'progn)
                 then x := append(reversip for each j in cdadr y
                                     collect list(car y,j),x)
                else x := y . x,
               if null atom cadr y and caadr y memq '(exports imports)
                 then eval cadr y);
      x := reversip x;
      begin scalar !*defn, dfprint!*,!*nocrefpri;
         !*nocrefpri := t;
         crefon();
         for each j in x do refprint cdr j;
         crefoff1()
      end;
      lprim list("Encountered non-SL functions:",idsort seen!*);
      lprim list("Encountered extended SL functions:",idsort xseen!*);
      lprim list("Globals seen:",idsort gseen!*);
      % Find internal functions.
      intfns!* := idsort setdiff(setdiff(setdiff(seen!*,undefns!*),
                                importslist!*),exportslist!*);
      lprim list("Internal functions:",intfns!*);
      if (y := setdiff(entpts!*,exportslist!*))
        then lprim list("Defined but not used:",idsort y);
      if tseen!* then lprim list("Encountered types not fn:",tseen!*);
      if undefg!*
        then lprim list("Undeclared globals:",idsort undefg!*);
      if (y := setdiff(undefns!*,importslist!*))
        then lprim list("Functions not defined:",idsort y);
      if pretitl!* then lprim list("Errors, etc.:",pretitl!*);
      return x
end;

deflist('((xmodule rlis)),'stat);

symbolic procedure xmodloop u;
   begin scalar x;
      flag(intfns!*,'internalfunction);
   a: if null u then go to b;
      x := cadar u;
      if null atom x
          and ((car x eq 'put
               and caddr x = mkquote 'number!-of!-args
               and memq(cadadr x,intfns!*))
           or car x memq '(exports imports))
        then nil
       else if errorp(x := errorset!*(list('begin11,mkquote car u),t))
        then progn(u := 'err2,go to b)
       else if car x then progn(u := car x,go to b);
      u := cdr u;
      go to a;
   b: remflag(intfns!*,'internalfunction);
      return u
   end;

% Augment list of functions not needing "imports" references.

nolist!* := append('(atsoc exports imports neq),nolist!*)$

endmodule;

end;
