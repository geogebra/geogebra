module xread; % Routines for parsing RLISP input.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 The RAND Corporation.  All rights reserved.

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


% NOTE: For bootstrapping purposes, this file should not have any tab
%       characters in it.

fluid '(!*blockp !*eoldelimp !*reduce4 commentlist!*);   % !*ignoreeol

global '(cursym!* nxtsym!*);

% The conversion of an RLISP expression to LISP prefix form is carried
% out by the function XREAD.  This function initiates the scanning
% process, and then calls the auxiliary function XREAD1 to perform the
% actual parsing.  Both XREAD and XREAD1 are used by many functions
% whenever an expression must be read;

flag ('(end !*colon!* !*semicol!*),'delim);

symbolic procedure chknewnam u;
   % Check to see if U has a newnam, and return it else return U.
   begin scalar x;
      return if null(x := get(u,'newnam)) or x eq u then u
              else if idp x then chknewnam x
              else x
   end;

symbolic procedure mkvar(u,v); u;

symbolic procedure remcomma u;
   if eqcar(u,'!*comma!*) then cdr u else list u;

symbolic procedure eolcheck;
   if null !*eoldelimp then nil
    else begin
   a: if nxtsym!* eq !$eol!$
        then progn(nxtsym!* := (if cursym!* eq 'end then '!;
                                 else token()),
                   go to a)
     end;

symbolic procedure xcomment(u,commentlist);
   progn((if commentlist
           then u := 'comment . aconc(reversip commentlist,u)),
         u);

symbolic procedure xread1 u;
   begin scalar v,w,x,y,z,z1,z2,commentlist;
        % This is the basic function for parsing RLISP input, once
        % tokens have been read by TOKEN and SCAN.  Its one argument
        % U can take a number of values:
        %   FOR:     Parsing of FOR statements
        %   GROUP:   Parsing of group statements after keyword <<
        %   LAMBDA:  Parsing of lambda expressions after keyword lambda
        %   NIL:     Parsing of expressions which can have a comma at
        %            the end for example.
        %   PROC:    Parsing of procedures after keyword PROCEDURE
        %   T:       Default case with standard parsing.
        % Also, if U is flagged STRUCT, it is assumed that the arguments
        % are lists of lists, and so commas are removed.  At present,
        % only MAT is tagged in this manner.
        % The local variables are used as follows:
        % v: expression being built
        % w: prefix operator stack
        % x: infix operator stack
        % y: infix value or stat property
        % z: current symbol
        % z1: next symbol
        % z2: temporary storage;
        % commentlist: association list of read comments.
        if commentlist!*
          then progn(commentlist := commentlist!*,
                     commentlist!* := nil);
  a:    z := cursym!*;
  a1:   if null idp z then nil
         else if z eq '!*lpar!* then go to lparen
         else if z eq '!*rpar!* then go to rparen
         else if y := get(z,'infix) then go to infx
         % The next line now commented out was intended to allow a STAT
         % to be used as a label. However, it prevents the definition of
         % a diphthong whose first character is a colon.
%        else if nxtsym!* eq '!: then nil
         else if flagp(z,'delim) then go to delimit
         else if y := get(z,'stat) then go to stat
         else if null !*reduce4 and flagp(z,'type)
          then progn(w := lispapply('decstat,nil) . w, go to a);
  a2:   y := nil;
  a3:   w := z . w;
        % allow for implicit * after a number.
        if toknump z
           and null(z1 eq !$eol!$)
           and idp (z1 := chknewnam nxtsym!*)
           and null flagp(z1,'delim)
           and null(get(z1,'switch!*) and null(z1 eq '!())
           and null get(z1,'infix)
           and null (!*eoldelimp and z1 eq !$eol!$)
          then progn(cursym!* := 'times, go to a)
         else if u eq 'proc and length w > 2
          then symerr("Syntax error in procedure header",nil);
  next: z := scan();
        go to a1;
  lparen:
        eolcheck();
        y := nil;
        if scan() eq '!*rpar!* then go to lp1    % no args
         else if flagpcar(w,'struct) then z := xread1 car w
         else z := xread1 'paren;
        if flagp(u,'struct) then progn(z := remcomma z, go to a3)
         else if null eqcar(z,'!*comma!*) then go to a3
         else if null w         % then go to a3
           then (if u eq 'lambda then go to a3
                 else symerr("Improper delimiter",nil))
         else w := (car w . cdr z) . cdr w;
        go to next;
  lp1:  if w then w := list car w . cdr w;  % Function of no args.
        go to next;
  rparen:
        if null u or u eq 'group
           or u eq 'proc % and null !*reduce4
          then symerr("Too many right parentheses",nil)
         else go to end1;
  infx: eolcheck();
        if z eq '!*comma!* or null atom (z1 := scan())
                or toknump z1 then go to in1
         else if z1 eq '!*rpar!* % Infix operator used as variable.
                or z1 eq '!*comma!*
                or flagp(z1,'delim)
          then go to in2
         else if z1 eq '!*lpar!* % Infix operator in prefix position.
                    and null eolcheck()     % Side effect important
                    and null atom(z1 := xread 'paren)
                    and car z1 eq '!*comma!*
                    and (z := z . cdr z1)
          then go to a1;
  in1:  if w then go to unwind
         else if null(z := get(z,'unary))
          then symerr("Redundant operator",nil);
        v := '!*!*un!*!* . v;
        go to pr1;
% in2:  if y then if !*ignoreeol then y := nil
%                  else symerr("Redundant operator",nil);
  in2:  if y then y := nil;
        w := z . w;
  in3:  z := z1;
        go to a1;
  unwind:
        % Null w implies a delimiter was found, say, after a comma.
        if null w then symerr("Improper delimiter",nil);
        z2 := mkvar(car w,z);
  un1:  w:= cdr w;
        if null w then go to un2
        % Next line used to be toknump car w, but this test catches more
%        else if null idp car w and null eqcar(car w,'lambda)
         else if atom car w and null idp car w
%           and null eqcar(car w,'lambda)
          then symerr("Missing operator",nil);
        z2 := list(car w,z2);
        go to un1;
  un2:  v:= z2 . v;
  preced:
        if null x then if y=0 then go to end2 else nil
%        else if z eq 'setq then nil
        % Makes parsing a + b := c more natural.
         else if y<caar x
           or (y=caar x
               and ((z eq cdar x and null flagp(z,'nary)
                                 and null flagp(z,'right))
                             or get(cdar x,'alt)))
          then go to pr2;
  pr1:  x:= (y . z) . x;
        if null(z eq '!*comma!*) then go to in3
         else if cdr x or null u or u memq '(lambda paren)
            or flagp(u,'struct)
          then go to next
         else go to end2;
  pr2:  %if cdar x eq 'setq then go to assign else;
        % Check for NOT used as infix operator.
        if eqcar(cadr v,'not) and caar x >= get('member,'infix)
          then typerr("NOT","infix operator");
        if cadr v eq '!*!*un!*!*
          then (if car v eq '!*!*un!*!* then go to pr1
                else z2 := list(cdar x,car v))
         else z2 := cdar x .
                     if eqcar(car v,cdar x) and flagp(cdar x,'nary)
                       then (cadr v . cdar v)
                      else list(cadr v,car v);
        x:= cdr x;
        v := z2 . cddr v;
        go to preced;
  stat: if null(y eq 'endstat) then eolcheck();
        if null(flagp(z,'go)
%          or (flagp(y,'endstatfn)
           or null(u eq 'proc) and (flagp(y,'endstatfn)
                or (null delcp nxtsym!* and null (nxtsym!* eq '!,))))
          then go to a2;
        if z eq 'procedure and !*reduce4
          then if w then if cdr w or !*reduce4
                           then symerr("proc form",nil)
                          else w := list procstat1 car w
                else w := list procstat1 nil
         else w := lispapply(y,nil) . w;
        y := nil;
        go to a;
  delimit:
        if null(cursym!* eq '!*semicol!*) then eolcheck();
        if z eq '!*colon!* and null(u eq 'for)
              and (null !*blockp or null w or null atom car w or cdr w)
           or flagp(z,'nodel)
              and (null u
                      or u eq 'group
                        and null(z memq
                                   '(!*rsqbkt!* !*rcbkt!* !*rsqb!*)))
          then symerr("Improper delimiter",nil)
         else if idp u and (u eq 'paren or flagp(u,'struct))
          then symerr("Too few right parentheses",nil);
  end1:
        if y then symerr("Improper delimiter",nil) % Probably ,).
         else if null v and null w and null x
          then return xcomment(nil,commentlist);
        y := 0;
        go to unwind;
  end2: if null cdr v then return xcomment(car v,commentlist)
         else print "Please send hearn@rand.org your program!!";
        symerr("Improper delimiter",nil)
   end;

%symbolic procedure getels u;
%   getel(car u . !*evlis cdr u);

%symbolic procedure !*evlis u;
%   mapcar(u,function lispeval);

flag ('(endstat retstat),'endstatfn);

flag ('(else then until),'nodel);

flag ('(begin),'go);

symbolic procedure xread u;
   begin
   a: scan();
      if !*eoldelimp and cursym!* eq '!*semicol!* then go to a;
      return xread1 u
   end;

symbolic procedure expread;  xread t;

flag('(expread xread),'opfn);   % To make them operators.

endmodule;

end;
