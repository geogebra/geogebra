module bquote;   % Support for backquote.

% Author:  Anthony C. Hearn.

% Copyright (c) 1993 The RAND Corporation.  All rights reserved.

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


% Lisp parsing case.

symbolic procedure tokbquote;
   begin
     crchar!* := readch1();
      nxtsym!* := list('backq,rread());
      ttype!* := 3;
      return nxtsym!*
   end;

put('!`,'tokprop,'tokbquote);

put('backq,'formfn,'formbquote);

symbolic procedure formbquote(u,vars,mode); mkbquote cadr u;

symbolic procedure mkbquote u;
   % Returns the "unevaled" form of u.
   if null u or constantp u then u
    else if atom u then mkquote u
    else if car u eq 'quote
     then if cadr u eq '!# then rederr "Invalid use of # after '"
           else mkquote u
    else if car u eq 'listify then mkbquote cdr u
    else if car u eq '!#
     then if eqcar(cdr u,'!@)
            then if null cdddr u then caddr u
                  else list('append,caddr u,mkbquote cdddr u)
           else list('cons,cadr u,mkbquote cddr u)
    else if car u eq '!@ then rederr "Invalid use of @"
    else list('cons,mkbquote car u,mkbquote cdr u);


% Rlisp parsing case.

put('backquote,'stat,'bquotstat);

symbolic procedure bquotstat; list('backquote,rl2l cadr rlis());

symbolic procedure rl2l u;
   if atom u then u
    else if atom car u then car u . rl2l cdr u
    else if caar u eq 'hash or caar u eq '!#
     then if eqcar(cadar u,'!@)
            then '!# . '!@ . cadr cadar u . rl2l cdr u
           else '!# . cadar u . rl2l cdr u
    else rl2l car u . rl2l cdr u;

put('backquote,'formfn,'formbquote);

endmodule;

end;
