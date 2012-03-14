module tok; % Identifier and reserved character reading.

% Author: Anthony C. Hearn.
% Modifications by: Arthur Norman.

% Copyright (c) 2001 Anthony C. Hearn.  All rights reserved.

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

% Parts of the coding style here look antique and ugly with lots of
% goto statements. The notation "<< ... >>" for a local block is not
% used. What is worse is that there are not even "for" and "while" loops.
% This is not because we do not care about style, but because in the
% process of bootstrapping Reduce this file is first read by a somewhat
% minimalist cut-down version of the parser that does not support the
% full language syntax.

fluid '(!*adjprec !*comment !*defn !*eoldelimp !*lower !*minusliter
        !*quotenewnam semic!* !*report!_colons);

!*report!_colons := t;

% Note *raise is global in the SL Report, but treated as fluid here.

global '(!$eof!$
         !$eol!$
         !*micro!-version
         !*raise
         !*savecomments!*
         comment!*
         crbuf!*
         crbuf1!*
         crchar!*
         peekchar!*
         curline!*
         cursym!*
         eof!*
         ifl!*
         nxtsym!*
         outl!*
         ttype!*);

flag('(adjprec),'switch);

!*quotenewnam := t;

crchar!* := '! ;
peekchar!* := nil;

curline!* := 1;

% The function TOKEN defined below is used for reading identifiers
% and reserved characters (such as parentheses and infix operators).
% It is called by the function SCAN, which translates reserved
% characters into their internal name, and sets up the output of the
% input line.  The following definitions of TOKEN and SCAN are quite
% general, but also inefficient.  The reading process can often be
% speeded up considerably if these functions (especially token) are
% written in terms of the explicit LISP used.

symbolic procedure prin2x u;
  outl!* := u . outl!*;

symbolic procedure mkstrng u;
   %converts the uninterned id U into a string;
   %if strings are not constants, this should be replaced by
   %list('string,u);
   u;

symbolic procedure readch1;
   begin scalar x;
% This one-character look-ahead is used when parsing names that
% have colons within them, as in abc:def. In particular it is active when
% input text such as "... abc::? ...". If the character written as "?" there
% is a letter, digit or underscore then it continues the symbol that
% started with "abc". Otherwise the symbol will stop as just "abc" with
% the first ":" left in crchar!* and peekchar!* set to '(!: ?).
% I will not preserve peekchar!* across file-selection (I note that
% curchr!* is not preserved either). This is OK provided that the Reduce
% directive to read in a file (ie 'in "filename";') can not properly end in
% a symbol that has colons at the end of it. And also that the "in" command
% can not end with something that messed with backslashes. This is UGLY and
% delicate but probably OK at present. 
      if peekchar!* then progn(x := car peekchar!*,
                              peekchar!* := cdr peekchar!*,
                              return x);
      if null terminalp()
        then progn(x := readch(),
                   x eq !$eol!$ and (curline!* := curline!*+1),
                   return x)
       else if crbuf1!*
        then begin x := car crbuf1!*; crbuf1!* := cdr crbuf1!* end
       else x := readch();
      crbuf!* := x . crbuf!*;
      return x
   end;

symbolic procedure tokquote;
   begin
      crchar!* := readch1();
      nxtsym!* := mkquote rread();
      ttype!* := 4;
      return nxtsym!*
   end;

put('!','tokprop,'tokquote);

symbolic procedure token!-number x;
   % Read and return a valid number from input.
   % Adjusted by A.C. Norman to be less sensitive to input case and to
   % support hex numbers.
   begin scalar dotp,power,sign,y,z;
      power := 0;
      ttype!* := 2;
    num1:
      if y or null(x eq '!)) then y := x . y;
      if dotp then power := power - 1;
    num2:
      if (x := readch1()) eq '!.
         then if dotp
                then rerror('rlisp,3,"Syntax error: improper number")
               else progn(dotp := t, go to num2)
       else if digit x then go to num1
       else if y = '(!0) and (x eq '!x or x eq '!X) then go to hexnum
% For whatever original reason this ignores backslashes within numbers. This
% I guess lets one write 12\34567\89000 and group digits in fives if you like.
% I can not see this mentioned in the manual and wonder if anybody uses it.
       else if x eq '!\ then progn(readch1(), go to num2)
       else if null(x eq '!e or x eq '!E) then go to ret;
      % Case of number with embedded or trailing E.
      dotp := t;
      if (x := readch1()) eq '!- then sign := t
       else if x eq '!+ then nil
       else if null digit x then go to ret
       else z := list x;
   nume1:
      if null digit(x := readch1()) then go to nume2;
      z := x . z;
      go to nume1;
   hexnum:
      y := 0;
   hexnum1:
      if not (z := get(x := readch1(), 'hexdigit)) then go to ret1;
      y := 16*y + z;
      go to hexnum1;
   nume2:
      if null z then rerror('rlisp,4,"Syntax error: improper number");
      z := compress reversip!* z;
      if sign then power := power - z else power := power + z;
   ret:
      y := compress reversip!* y;
   ret1:
      nxtsym!* := if dotp then '!:dn!: . (y . power)
                   else if !*adjprec then '!:int!: . (y . nil)
                   else y;
      crchar!* := x;
      return nxtsym!*
   end;

deflist(
 '((!0 0)  (!1 1)  (!2 2)  (!3 3)  (!4 4)
   (!5 5)  (!6 6)  (!7 7)  (!8 8)  (!9 9)
   (!a 10) (!b 11) (!c 12) (!d 13) (!e 14) (!f 15)
   (!A 10) (!B 11) (!C 12) (!D 13) (!E 14) (!F 15)), 'hexdigit);

% The special marker !_line!_ will be replaced in the input stream of
% tokens by the current line number. Note that if terminalp() returns
% true that input lines are not counted - ie if !*int is on (to signal
% interactive use) and input is direct from whatever is standard when
% reduce starts then !_line!_ will always expand to 1. Go "off int;" or
% put your material in a file that you read using "in" if this matters
% to you.

fluid '(!*line!-marker);
!*line!-marker := intern compress '(!! !_ l i n e !! !_);

symbolic procedure token1;
%
% The current syntax for an identifier is that it starts with a letter (or
% an escaped character) and can continue with letters, digits or underscores.
% I wish to support identifiers that contain an internal unescaped "::"
% (but just one such).
% When I started considering this I had wondered about also allowing
% an embedded single ":" too. However it became clear that that would cause
% trouble in common occuring cases such in
%         label:x:=y;
% and     for i:=m:n dp ...
%
% I believe that little existing code will be troubled if double colons are
% treated specially. The only use of them that I can see in the current source
% is where gentran enables :: to indicate a range in a declaration. If both
% ends of the range have alpabetic names then extra whitespace would be
% required.
%
% When (and if) this is ever activated the case of a single colon
% will merely be treated as it used to be (so avoiding incompatibility),
% while perhaps the usage
%   package::name
% can be used for something interesting. But initially package::name will
% merely denote a name that has embedded colons.
%
   begin scalar x,y,z;
        x := crchar!*;
    a:  if seprp x and null(x eq !$eol!$ and !*eoldelimp)
          then progn(x := readch1(), go to a)
         else if digit x then return token!-number x
         else if liter x then go to letter
         else if (y := get(x,'tokprop)) then return lispapply(y,nil)
         else if x eq '!% and null !*savecomments!* then go to coment
         else if x eq '!! and null(!*micro!-version and null !*defn)
          then go to escape
         else if x eq '!" then go to string
         else if x eq '!\ then go to backslash;
        ttype!* := 3;
        if x eq !$eof!$ then prog2(crchar!* := '! ,filenderr());
        nxtsym!* := x;
        if delcp x then crchar!*:= '!  else crchar!*:= readch1();
        if null(x eq '!- and digit crchar!* and !*minusliter)
          then go to c;
        x := token!-number crchar!*;
        if numberp x then return apply1('minus,x);  % For bootstrapping.
        rplaca(cdr x,apply1('minus,cadr x));        % Also for booting.
        return x;
    escape:
        begin scalar raise,!*lower;
           raise := !*raise;
           !*raise := nil;
           y := x . y;
           x := readch1();
           !*raise := raise
        end;
    letter:
        ttype!* := 0;
    let1:
        y := x . y;
        if digit (x := readch1()) or liter x then go to let1
         else if x eq '!! then go to escape
         else if x eq '!- and !*minusliter
          then progn(y := '!! . y, go to let1)
         else if x eq '!_ then go to let1     % Allow _ as letter.
         else if x eq '!: then go to maybepackage;
    ordinarysym:
        y := intern compress reversip!* y;
% If I implement a package system I might want to check if the name
% y here should map onto ppp:y for some package ppp.
        if y = !*line!-marker then nxtsym!* := curline!*
        else nxtsym!* := y;
        crchar!* := x;
    c:  return nxtsym!*;
    backslash:
        y := '(!\ e n d !{ r e d u c e !});
        z := nil;
    bsloop:
        z := x . z;
        x := readch1();
        y := cdr y;
        if null y then go to bsfound
        else if x eq car y then go to bsloop;
% Here I need to set things back so that all the peeked-ahead stuff is
% put ready for re-scanning and I can merely return the "\".
        peekchar!* := cdr reverse (x . z); 
        ttype!* := 3;
        crchar!* := readch1();
        nxtsym!* := '!\;
        return nxtsym!*;
    bsfound:
% At this stage I have just found the text "\end{reduce}" and what I will do
% is to discard all stuff until I find either end of file of "\begin{reduce}".
% However note that at this stage I am expecting to be scanning TeX code and
% so if there are any TeX comments (introduced by "%") I do not want to
% detect the magic within them.
        y := '(!\ b e g i n !{ r e d u c e !});
        ttype!* := 3;
    bssrch:
        if x eq '!% then go to bscomm
        else if x eq !$eof!$ then progn(
           crchar!* := '! ,
           filenderr(),
           nxtsym!* := x,
           return x);
% If I have found \begin{reduce} go back to scanning input normally.
        if null y then go to a;
        z := x;
        x := readch1();
        if not (z eq car y) then go to bsfound;
        y := cdr y;
        go to bssrch;
    bscomm:
        x := readch1();
        if x eq !$eof!$ then go to bssrch
        else if x eq !$eol!$ then go to bsfound
        else go to bscomm;
    maybepackage:                               % Seen abc:
        x := readch1();
        if x eq '!: then go to maybeextpackage;
        peekchar!* := list x;
        x := '!:;
        go to ordinarysym; 
    maybeextpackage:                            % Seen abc::
        x := readch1();
        if liter x then go to isextpackage;
        peekchar!* := list('!:, x);
        x := '!:;
        go to ordinarysym;
    isextpackage:
% What follows lexes a name of the form ppp::xxx
        z := intern compress reverse y;
% In case it is useful I set z to the name of the "package" part ppp
        y := '!: . '!! . '!: . '!! . y;
    extpackmore:
        y := x . y;
        if digit (x := readch1()) or liter x then go to extpackmore
         else if x eq '!! then go to extpackescape
         else if x eq '!- and !*minusliter
          then progn(y := '!! . y, go to extpackmore)
         else if x eq '!_ then go to extpackmore;    % Allow _ as letter.
        y := intern compress reversip!* y;
% At this stage I will always display a message reporting what I have seen.
        lprim list("Name with double colon in detected:", y);
        nxtsym!* := y;
        crchar!* := x;
% This is where I merely return the symbol with an embedded "::". If I
% ever implement a genuine package system this should be where I put a
% major hook that manages extra symbol tables.
        return nxtsym!*;
    extpackescape:
        begin scalar raise,!*lower;
           raise := !*raise;
           !*raise := nil;
           y := x . y;
           x := readch1();
           !*raise := raise
        end;
        go to extpackmore;
    string:
        begin scalar raise,!*lower;
           raise := !*raise;
           !*raise := nil;
       strinx:
           y := x . y;
           if (x := readch1()) eq !$eof!$
             then progn(!*raise := raise,
                        crchar!* := '! ,
                        lpriw("***** End-of-file in string",nil),
                        filenderr())
            else if null(x eq '!") then go to strinx;
           y := x . y;
           % Now check for embedded string character.
           x := readch1();
           if x eq '!" then go to strinx;
           nxtsym!* := mkstrng compress reversip!* y;
           !*raise := raise
         end;
        ttype!* := 1;
        crchar!* := x;
        go to c;
    coment:
        begin scalar !*lower,raise;
        raise := !*raise;
        !*raise := nil;
    comm1: if null(readch1() eq !$eol!$) then go to comm1;
        !*raise := raise
        end;
        x := readch1();
        go to a
   end;

symbolic procedure tokbquote;
   begin
     crchar!* := readch1();
      nxtsym!* := list('backquote,rread());
      ttype!* := 3;
      return nxtsym!*
   end;

put('!`,'tokprop,'tokbquote);

symbolic procedure token;
   %This provides a hook for a faster TOKEN;
   token1();

symbolic procedure filenderr;
   begin
      eof!* := eof!*+1;
      if terminalp() then error1()
       else error(99,if ifl!*
                       then list("End-of-file read in file",car ifl!*)
                      else "End-of-file read")
   end;

symbolic procedure ptoken;
   begin scalar x;
        x := token();
        if x eq '!) and eqcar(outl!*,'! ) then outl!*:= cdr outl!*;
           %an explicit reference to OUTL!* used here;
        prin2x x;
        if null ((x eq '!() or (x eq '!))) then prin2x '! ;
        return x
   end;

symbolic procedure rread1;
   % Modified to use QUOTENEWNAM's for ids.
   % Note that handling of reals uses symbolic mode, regardless of
   % actual mode.
   begin scalar x,y;
        x := ptoken();
        if null (ttype!*=3)
          then return if idp x
                        then if !*quotenewnam
                                and (y := get(x,'quotenewnam))
                               then y
                              else x
                       else if eqcar(x,'!:dn!:)
                        then dnform(x,nil,'symbolic)
                       else x
         else if x eq '!( then return rrdls()
         else if null (x eq '!+ or x eq '!-) then return x;
        y := ptoken();
        if eqcar(y,'!:dn!:) then y := dnform(y,nil,'symbolic);
        if null numberp y
          then progn(nxtsym!* := " ",
                     symerr("Syntax error: improper number",nil))
         else if x eq '!- then y := apply1('minus,y);
           % We need this construct for bootstrapping purposes.
        return y
   end;

symbolic procedure rrdls;
   begin scalar x,y,z;
    a:  x := rread1();
        if null (ttype!*=3) then go to b
         else if x eq '!) then return z
         else if null (x eq '!.) then go to b;
        x := rread1();
        y := ptoken();
        if null (ttype!*=3) or null (y eq '!))
          then progn(nxtsym!* := " ",symerr("Invalid S-expression",nil))
         else return nconc(z,x);
    b: z := nconc(z,list x);
       go to a
   end;

symbolic procedure rread;
   progn(prin2x " '",rread1());

symbolic procedure delcp u;
   % Returns true if U is a semicolon, dollar sign, or other delimiter.
   % This definition replaces the one in the BOOT file.
   flagp(u,'delchar);

flag('(!; !$),'delchar);

symbolic procedure toknump x;
   numberp x or eqcar(x,'!:dn!:) or eqcar(x,'!:int!:);

% The following version of SCAN provides RLISP with a facility for
% conditional compilation.  The protocol is that text is included or
% excluded at the level of tokens.  Control by use of new reserved
% tokens !#if, !#else and !#endif.  These are used in the form:
%    !#if (some Lisp expression for use as a condition)
%    ... RLISP input ...
%    !#else
%    ... alternative RLISP input ...
%    !#endif
%
% The form
%    !#if C1 ... !#elif C2 ... !#elif C3 ... !#else ... !#endif
% is also supported.
%
% Conditional compilation can be nested.  If the Lisp expression used
% to guard a condition causes an error it is taken to be a FALSE
% condition. It is not necessary to have an !#else before !#endif if no
% alternative text is needed.  Although the examples here put !#if etc
% at the start of lines this is not necessary (though it may count as
% good style?).  Since the condition will be read using RLISPs own
% list-reader there could be conditional compilation guarding parts of
% it - the exploitation of that possibility is to be discouraged!

% Making the condition a raw Lisp expression makes sure that parsing it
% is easy. It makes it possible to express arbitrary conditions, but it
% is hoped that most conditions will not be very elaborate - things like
%    !#if (member 'psl lispsystem!*)
%         magic();
%    !#else
%         error();
%    !#endif
% or
%    !#if debugging!-mode  % NB if variable is unset that counts as nil
%    print "message";      % so care should be taken to select the most
%    !#endif               % useful default sense for such tests
% should be about as complicated as reasonable people need.
%
% Two further facilities are provided:
%    !#eval (any lisp expression)
% causes that expression to be evaluated at parse time.  Apart from any
% side-effects in the evaluation the text involved is all ignored. It is
% expected that this will only be needed in rather curious cases, for
% instance to set system-specific options for a compiler.

%    !#define symbol value
% where the value should be another symbol, a string or a number,
% causes the first symbol to be mapped onto the second value wherever
% it occurs in subsequent input.  This uses exactly the same mechanism
% as the existing REDUCE "define" statement and so has the same
% limitations.  The use of a hook in SCAN to support this ensures that
% the !#define can be written anywhere in REDUCE source code (eg within
% a procedure definition) and will still apply while the program
% involved is parsed.  No special facility for undoing the effect of a
% !#define is provided, but the general-purpose !#eval could be used to
% remove the 'newnam property that is involved.

symbolic procedure addcomment u;
 %  if commentlist!*
 %    then cursym!* := 'comment . aconc(reversip commentlist!*,u)
 %   else
   cursym!* := u;

symbolic procedure scan;
   begin scalar bool,x,y;
        if null (cursym!* eq '!*semicol!*) then go to b;
    a:  nxtsym!* := token();
    b:  if null atom nxtsym!* and null toknump nxtsym!*
          then go to q1
         else if nxtsym!* eq 'else or cursym!* eq '!*semicol!*
         then outl!* := nil;
        prin2x nxtsym!*;
    c:  if null idp nxtsym!* then go to l
         else if (x:=get(nxtsym!*,'newnam)) and
                        (null (x=nxtsym!*)) then go to new
         else if nxtsym!* eq 'comment
          then progn(x := read!-comment1 'comment,
                     if !*comment then return x else go to a)
         else if nxtsym!* eq '!% and ttype!*=3
          then progn(x := read!-comment1 'percent!_comment,
                     if !*comment then return x else go to a)
         else if nxtsym!* eq '!#if then go to conditional
         else if nxtsym!* eq '!#else or
                 nxtsym!* eq '!#elif then progn(nxtsym!* := x := nil,
                                                go to skipping)
         else if nxtsym!* eq '!#endif then go to a
         else if nxtsym!* eq '!#eval then progn(
                     errorset(rread(), !*backtrace, nil),
                     go to a)
         else if nxtsym!* eq '!#define then progn(
                     x := errorset(rread(), !*backtrace, nil),
                     progn(if errorp x then go to a),
                     y := errorset(rread(), !*backtrace, nil),
                     progn(if errorp y then go to a),
                     put(x, 'newnam, y),
                     go to a)
         else if null(ttype!* = 3) then go to l
         else if nxtsym!* eq !$eof!$ then return filenderr()
         else if nxtsym!* eq '!' then rederr "Invalid QUOTE"
         else if !*eoldelimp and nxtsym!* eq !$eol!$ then go to delim
         else if null (x:= get(nxtsym!*,'switch!*)) then go to l
         else if eqcar(cdr x,'!*semicol!*) then go to delim;
        bool := seprp crchar!*;
   sw1: nxtsym!* := token();
        if null(ttype!* = 3) then go to sw2
         else if nxtsym!* eq !$eof!$ then return filenderr()
         else if car x then go to sw3;
   sw2: cursym!*:=cadr x;
        bool := nil;
        if cursym!* eq '!*rpar!* then go to l2
         else return addcomment cursym!*;
   sw3: if bool or null (y:= atsoc(nxtsym!*,car x)) then go to sw2;
        prin2x nxtsym!*;
        x := cdr y;
        if null car x and cadr x eq '!*Comment!*
          then progn(comment!* := read!-comment(),go to a);
        go to sw1;
  conditional:
% The conditional expression used here must be written in Lisp form
        x := errorset(rread(), !*backtrace, nil);
% errors in evaluation count as NIL
        if null errorp x and car x then go to a;
        x := nil;
  skipping:
% I support nesting of conditional inclusion.
        if nxtsym!* eq '!#endif then
           if null x then go to a else x := cdr x
        else if nxtsym!* eq '!#if then x := nil . x
        else if (nxtsym!* eq '!#else) and null x then go to a
        else if (nxtsym!* eq '!#elif) and null x then go to conditional;
        nxtsym!* := token();
        if (ttype!*=3) and (nxtsym!* eq !$eof!$)
          then return filenderr()
         else go to skipping;
  delim:
        semic!*:=nxtsym!*;
        return addcomment '!*semicol!*;
  new:  nxtsym!* := x;
        if stringp x then go to l
        else if atom x then go to c
        else go to l;
  q1:   if null (car nxtsym!* eq 'string) then go to l;
        prin2x " ";
        prin2x cadr(nxtsym!* := mkquote cadr nxtsym!*);
  l:    cursym!*:=nxtsym!*;
        nxtsym!* := token();
        if nxtsym!* eq !$eof!$ and ttype!* = 3 then return filenderr();
  l2:   if numberp nxtsym!*
           or (atom nxtsym!* and null get(nxtsym!*,'switch!*))
          then prin2x " ";
        return addcomment cursym!*
   end;

symbolic procedure read!-comment1 u;
   begin scalar !*lower,raise;
      raise := !*raise;
      !*raise := nil;
 comm1: if null(delcp crchar!* and null(crchar!* eq !$eol!$))
          then progn(crchar!* := readch1(), go to comm1);
      crchar!* := '! ;
      !*raise := raise;
      condterpri()
   end;

endmodule;

end;
