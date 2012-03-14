module inspect;   % Rlisp88 Code inspector.

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


%  Author: Jed Marti.

%  Description: Formats and displays the active annotation associated
%   with various RLISP data structures.
%  Notes: Things left to work on:
%    DEFINE constants.
%    SWITCH
%    CLASS, instances, scripts, etc.
%    The line numbers are pretty much the input expression numbers
%    (where comments are counted). Fixing this would require a
%    modification to the RLISP lexical scanner.
%  Dependencies:
%  Revision History: (Created Fri Jan  3 08:40:29 1992)
%    Wed Feb 26 09:39:28 1992 Add file/line numbers to functions.
%          Upgrade comments.
%    Sun Mar  1 11:09:30 1992 Try GLOBAL and FLUID declarations. Also
%          clear COMMENT!* after each use.
%    Fri Mar 13 17:28:41 1992 Add the comment reformatting routine
%          fmtcmt.
%    Fri Oct  8 12:06:00 1993 Fix use if ifl!*, remove printf's. Make
%      work with old RLISP syntax first.  No active comments in this
%      code.

expr procedure describe x;
   % DESCRIBE(X) -- Inspect any data structure X. This main routine
   % farms out the work accordingly.
   if pairp x then << prin2t "A dotted-pair or list"; nil >>
    else if vectorp x then
     if i!&recordinstp x then i!&recordinst x
      else  <<prin2 "A vector with "; prin1 add1 upbv x;
              prin2t " elements"; nil>>
   else if codep x then <<prin2t "A code-pointer"; nil>>
   else if numberp x then
      if fixp x then <<prin2t "A fixed number"; nil>>
      else if floatp x then <<prin2t "A floating-point number"; nil>>
      else <<prin2t "An unknown type of number"; nil>>
   else if stringp x then <<prin2t "A string"; nil>>
   else if idp x then
     if i!&recordp x then i!&record x
     else if i!&functionp x then i!&function x
     else if i!&constantp x then i!&constant x
     else if i!&modulep x then i!&module x
     else if get(x, 'newnam) then i!&idnewnam x
     else i!&id x
   else <<prin2t "Can't inspect data structures of this type";nil>>;

expr procedure i!&idnewnam x;
   % I!&IDNEWNAM(X) - This is the result of a define.
   <<prin1 x;
     prin2 " is a constant defined as ";
     print get(x,'newnam);
     if x := get(x,'active!-annotation) then
        if pairp x then i!&dump car x else i!&dump x>>;

expr procedure i!&recordp x;
   % I!&RECORDP(X) -- X is an id. Returns T if this looks like an RLISP
   % record.
   get(x,'formfn) eq 'form!_record!_constructor;

expr procedure i!&record x;
% I!&RECORD(X) -- X is an id and the name of a record constructor. Try
%  and display as much about the record as possible. Note that record
%  instances are handled by the vector case temporarily.
<< prin1 x; prin2t " is a record constructor with the following fields";
   prin2t "** not implemented. **";
   nil >>;

expr procedure i!&recordinstp x;
% I!&RECORDINSTP(X) -- Returns T if X (a vector) looks like a record
% instance.
begin scalar tmp;
  if not idp getv(x,0) then return nil;
  if not (tmp :=  getd getv(x,0)) then return nil;
  if not eqcar(getd getv(x,0),'macro) then return nil;
  if atom (tmp := errorset({getv(x,0)},nil,nil)) then return nil;
  if upbv x neq upbv car tmp then return nil;
  return t
end;

expr procedure i!&recordinst x;
% x is identified as a record.
<< prin2 "A "; prin1 getv(x,0); prin2t " record with ";
   for i:=1:upbv x
       do << prin2 "   "; prin1 i; prin2 ": "; print getv(x,i)>>;
   nil >>;

expr procedure i!&functionp x;
% I!&FUNCTIONP(X) -- X is an id. Returns T if it is also the name of a
%  function or SMACRO.
get(x, 'smacro) or getd x;

expr procedure i!&function x;
% I!&FUNCTION(X) - X is a function or SMACRO name. Farm out the
%  description based on its type.
if get(x, 'smacro) then i!&function!-smacro x
 else (if eqcar(w, 'macro) then i!&function!-macro(x, cdr w)
       else if eqcar(w, 'expr) then i!&function!-expr(x, cdr w)
       else if eqcar(w, 'fexpr) then i!&function!-fexpr(x, cdr w)
       else i!&function!-unknown(x, w)) where w := getd x;

expr procedure i!&function!-smacro x;
% I!&FUNCTION!-SMACRO(X) -- X is the name of an SMACRO. Display what we
%  know about it.
begin scalar tmp, d;
  d := get(x, 'smacro);
  prin1 x; prin2 " is an SMACRO with ";
  if not (tmp := get(x, 'number!-of!-args)) then
   if eqcar(d, 'lambda) and cdr d then tmp := length cadr d
   else tmp := nil;
  if onep tmp then prin2t "one argument"
    else if not tmp then prin2t "an unknown number of arguments"
    else << prin1 tmp; prin2t " arguments" >>;
  if tmp := get(x, 'active!-annotation) then
     << i!&whereis tmp; i!&dump if pairp tmp then car tmp else tmp>>
end;


expr procedure i!&function!-expr(x, d);
%  I!&FUNCTION!-EXPR(X, D) -- X is the name of an EXPR type function and
%  D is it's definition. Display what we know about it.
begin scalar tmp;
  prin1 x; prin2 " is an EXPR with ";
  if not (tmp := get(x, 'number!-of!-args)) then
   if eqcar(d, 'lambda) and cdr d then tmp := length cadr d
   else tmp := nil;
  if onep tmp then prin2t "one argument"
    else if not tmp then prin2t "an unknown number of arguments"
    else << prin1 tmp; prin2t " arguments" >>;
  if tmp := get(x, 'active!-annotation) then
     << i!&whereis tmp; i!&dump if pairp tmp then car tmp else tmp >>
end;


expr procedure i!&function!-fexpr(x, d);
%  I!&FUNCTION!-FEXPR(X, D) -- X is the name of an FEXPR type function
%  and D is its definition. Display what we know about it.
begin scalar tmp;
  prin1 x; prin2t " is an FEXPR";
  if tmp := get(x, 'active!-annotation) then
     << i!&whereis tmp; i!&dump if pairp tmp then car tmp else tmp >>
end;


expr procedure i!&function!-macro(x, d);
% I!&FUNCTION!-MACRO(X, D) -- X is the name of a MACRO type function and
%  D its definition. Display what we know.
begin scalar tmp;
  prin1 x; prin2t " is a MACRO";
  if tmp := get(x, 'active!-annotation) then
     << i!&whereis tmp; i!&dump if pairp tmp then car tmp else tmp >>
end;


expr procedure i!&whereis x;
% I!&WHEREIS(X) -- We might have a (comment line-number file). If so,
%  display this information.
if length x = 3 then
  << prin2 "Function ends on line "; prin1 cadr x;
     prin2 " in file "; prin2t caddr x >>;


expr procedure i!&constantp x;
% I!&CONSTANTP(X) - Returns T if X is a constant.
  constantp x;

expr procedure i!&id x;
% I!&ID(X) -- X is an id see if we can find out anything about it.
if globalp x then i!&id1(x, 'global)
 else if fluidp x then i!&id1(x, 'fluid)
 else << prin2 "Don't know anything about "; print x; nil >>;

expr procedure i!&id1(x, ty);
% I!&ID1(X, TY) -- X is TY (global or fluid). Print out what we know
%  about this id.
begin scalar a;
  prin2 "Identifier '"; prin1 x; prin2 "' is "; prin2 ty;
  if a := get(x, 'active!-annotation) then
    if length a = 3 then
       << prin2 " defined line "; prin1 cadr a;
          prin2 " in file "; prin2t caddr a;
          i!&dump car a >>
    else i!&dump a
  else terpri()
end;

expr procedure i!&constant x;
% I!&CONSTANT(X) - X is some sort of constant. Not much we can say about
% it.
   <<prin1 x; prin2t " is a constant">>;

expr procedure i!&modulep x;
% I!&MODULEP(X) - Returns T if x looks like a module.
flagp(x, 'module);

expr procedure i!&module x;
% I!&MODULE(X) - Display the facts about a module.
(if filep r88 then  i!&module1(x, i!&moduleb x, r88)
  else if filep rd then i!&module1(x, i!&moduleb x, rd)
  else i!&module2 x)
where r88 := string!-downcase
                compress nconc('!" . explode2 x, '(!. !r !8 !8 !")),
      rd := string!-downcase
                compress nconc('!" . explode2 x, '(!. !r !e !d !"));


expr procedure i!&module1(mname, bfile, sfile);
% I!&MODULE(MNAME, BFILE, SFILE) - Display data about module MNAME
%  with object file BFILE, source file SFILE. PSL/UNIX specific.
begin scalar sfs, bfs;
  if sfile then sfs := filestatus sfile;
  if bfile then bfs := filestatus bfile;
  if sfile then
     if bfile then
      << prin2 "Module ";
         prin1 mname;
         prin2 " source file ";
         prin2 sfile;
         prin2 " fasl file ";
         prin2 bfile;
         prin2 " and is ";
         print i!&dcomp(sfs, bfs) >>
     else
      << prin2 "Module ";
         prin1 mname;
         prin2 " has source file ";
         prin2 sfile;
         prin2 " written ";
         prin2t i!&sdt sfs >>
  else if bfile then
     << prin2 "Module ";
        prin1 mname;
        prin2 " has fasl file ";
        prin2 bfile;
        prin2 " written ";
        prin2t i!&sdt bfs >>
  else
   << prin2 "Module ";
      prin1 mname;
      prin2t ", can't find any files." >>;
  if sfs := get(mname, 'active!-annotation) then
    if pairp sfs then i!&dump car sfs else i!&dump sfs;
end;


expr procedure i!&module2 mname;
% I!&MODULE2(MNAME) - called when we don't know much about a module.
<< prin2 "Can't find source or fasl file for module ";
   print mname;
   if sfs then if pairp sfs then i!&dump car sfs else i!&dump sfs >>
        where sfs := get(mname, 'active!-annotation);


expr procedure i!&dcomp(s1, s2);
% I!&DCOMP(S1, S2) -- two PSL file statuses. Compare the WRITETIMES
%  and return " OUT OF DATE." or " UP TO DATE.".
if i!&dt s1 > i!&dt s2 then " out of date." else " up to date.";

expr procedure i!&dt x;
(if w then cddr w else 0) where w := atsoc('writetime, x);

expr procedure i!&sdt x;
(if w then cadr w else "no date") where w := atsoc('writetime, x);

expr procedure i!&moduleb x;
% I!&MODULEB(X) - Find which directory LOADDIRECTORIES!* the .b file
%  is and return the file name.
begin scalar fs, fn;
  fs := loaddirectories!*
  while pairp fs
   do << fn := string!-downcase
                nconc('!" . explode2 car fs,
                      nconc(explode2 x, '(!. !b !")));
         if filep fn then fs := fn
           else fs := cdr fs >>;
  return fs
end;


%-----------------------------------------------------------------------
% Basic active comment formatting. Remove the leading blank from the
% first line, all blanks at start of each subsequent line, but only
% of the shortest line.

expr procedure i!&dump x;
% I!&DUMP(X) - X is a string or something. Display its characters but
%  dump blanks at the beginning of each line as appropriate.
begin scalar lnes, minsp, v;
  lnes := reversip i!&makelines(explode2 x, {nil});
  minsp := 5000;
  for each x in cdr lnes do
      if (v:= i!&spcount x) < minsp then minsp := v;
  i!&prn i!&delspace(5000, car lnes);
  for each l in cdr lnes do i!&prn i!&delspace(minsp, l)
end;

expr procedure i!&makelines(x, l);
%  I!&MAKELINES(X, L) -- Remove EOL's form x and convert to a list of
% sentences. L is used to build this list, call this with L = NIL.
  if null x then reversip car l . cdr l
   else if eqcar(x, !$eol!$) then
        i!&makelines(cdr x, nil . (reversip car l . cdr l))
   else << car l := car x . car l; i!&makelines(cdr x, l) >>;

expr procedure i!&spcount l;
% I!&SPCOUNT(l) -- Count spaces in front of line l and return.
if null l then 0
  else if eqcar(l, '! ) then add1 i!&spcount cdr l
  else 0;

expr procedure i!&delspace(n, l);
%  I!&DELSPACE(N, L) -- Delete n spaces from the front of line L and
%  return a new list. Quit if the list is short or runs into some
%  non-blank character.
if null l then nil
  else if zerop n then l
  else if eqcar(l, '! ) then i!&delspace(n - 1, cdr l)
  else l;

expr procedure i!&prn x;
% I!&PRN(x) -- Display the characters of list x and then terminate the
%  line.
<< for each c in x do prin2 c;
   terpri() >>;


%-----------------------------------------------------------------------
% Hacks to make active comments work.

fluid '(!*saveactives);
switch saveactives;


expr procedure i!&makeComment;
% I!&MAKECOMMENT() - returns (comment line file) for packing active
%  annotation data away.
mkquote {cadr Comment!*, curline!*,
         if ifl!* then car ifl!* else "unknown"};


expr procedure nformproc(a, b, c);
% NFORMPROC(A, B, C) -- Temporary wrapper for FORMPROC to save the
%  function active annotation if the SAVEACTIVES switch is on. Also
%  put the file name and current line out there.
begin scalar v,w;
  v := if !*saveactives and comment!* then
      <<w := i!&makecomment();
        put(cadr a,'active!-annotation,eval w);
        {'progn,
          {'cond,{'!*saveactives,
                  {'put,mkquote cadr a,mkquote 'active!-annotation,w}}},
           formproc(a, b, c)}>>
        else formproc(a, b, c);
  comment!* := nil;
  return v
end;

put('procedure,'formfn,'nformproc);


expr procedure formmodule(u, vars, mode);
% FORMMODULE(U,VARS,MODE) - Save any active annotation on the property
%  of the module. Clear comment after use.
begin scalar x;
  x := if !*saveactives and Comment!* then
     {'progn, {'cond, {'!*saveactives,
                       {'put, mkquote cadr u,
                         mkquote 'active!-annotation,
                         i!&makecomment()}}},
              {'flag, mkquote {cadr u}, mkquote 'MODULE},
              {'module, mkquote{cdr u}}}
  else {'module, mkquote cdr u};
  Comment!* := nil;
  return x
end;

% put('module, 'formfn, 'formmodule);


expr procedure formglobalfluid(u, vars, mode);
% FORMGLOBALFLUID(U, VARS, MODE) -- Attach active annotation to the
%  variables declared.
if !*saveactives and Comment!* then
 {{'lambda, {'!$v!$},
     {'progn,
       {'cond, {'!*saveactives,
                {'mapcar, '!$v!$,
                   {'function,
                     {'lambda, {'!$u!$}, {'put, '!$u!$,
                                           mkquote 'active!-annotation,
                                           i!&makeComment()}}}}}},
       {car u, '!$v!$}}}, formc(cadr u, vars, mode)}
 else {car u, formc(cadr u, vars, mode)};

% put('global, 'formfn, 'formglobalfluid);

% put('fluid, 'formfn, 'formglobalfluid);


expr procedure fmtcmt(ano, ind, rm);
begin scalar la, ind3, tcs, c, coll, colle, curbl, cbl;
  la := explode2 ano;
  if (ind3 := ind + 3) > (rm - 10) then error(0, "margins too small");
  tcs := rm - ind3;

  % Remove extra blanks from front.
%  la := deblank la;

  % STATE 1: Now scan the lines dumping tokens to the output.
  spaces ind;
  prin2 "/* ";
loop: if null la then return prin2 " */";
  if c := fmtfulllineof(car la, la) then
     << la := fmtremoveline la;
        for i:=1:tcs do prin2 c;
        terpri();
        spaces ind3;
        go to loop >>
  else if fmtblankline la then
     << if posn() > ind3 then terpri();
        terpri();
        spaces ind3;
        la := fmtremoveline la;
        go to loop >>
  else if eqcar(la, !$eol!$) then
     << terpri(); spaces ind3;go to loop >>
  else if eqcar(la, '! ) then go to state4;

  % STATE 2: Collect characters to EOL, blank, or NIL.
state2: coll := colle := {car la};
  la := cdr la;

state2a: if null la then
       << fmtdumptok(coll, ind3, rm);
          go to loop >>
    else if eqcar(la, !$eol!$) then
       << fmtdumptok(coll, ind3, rm);
          la := cdr la;
          go to loop >>
    else if eqcar(la, '! ) then
       << fmtdumptok(coll, ind3, rm);
          go to state3 >>;
    cdr colle := {car la};
    colle := cdr colle;
    la := cdr la;
    go to state2a;

  % STATE 3: Skip blanks to NIL, EOL, or next token.
state3: if null la then go to loop
     else if eqcar(la, !$eol!$) then << la := cdr la; go to loop >>
     else if eqcar(la, '! ) then << la := cdr la; go to state3 >>
     else go to state2;


  % STATE 4: We've got a line that starts with a blank. Dump it to the
  % output line.
state4: curbl := 0; cbl := t;
state4a: prin2 car la;
  if cbl and eqcar(la, '! ) then curbl := add1 curbl else cbl := nil;
  la := cdr la;
  if null la then go to loop;
  if eqcar(la, !$eol!$) then
    << terpri(); spaces ind3;la := cdr la; go to loop >>;
  if posn() >= rm then << terpri(); spaces(1 + ind3 + curbl) >>;
  go to state4a
end;



expr procedure fmtblankline l;
% FMTBLANKLINE(L) -- returns T if the rest of the current line is
%  all blanks.
if null l or eqcar(l, !$eol!$) then t
  else if eqcar(l, '! ) then fmtblankline cdr l;


expr procedure fmtfulllineof(c, la);
% FMTFULLLINEOF(C, LA) -- Returns C if LA up to the end or !$EOL!$ is
%  all one character.
if null la then c
  else if eqcar(la, c) then fmtfulllineof(c, cdr la)
  else if eqcar(la, !$eol!$) then c
  else nil;



expr procedure fmtremoveline la;
% FMTREMOVELINE(LA) -- returns the remainder of LA up to the end or the
%  first !$EOL!$.
if la and not eqcar(la, !$eol!$) then fmtremoveline cdr la else cdr la;


expr procedure fmtdumptok(l, ind, rm);
if (length l + posn()) > rm then
  << terpri(); spaces ind; for each x in l do prin2 x; prin2 " " >>
else << for each x in l do prin2 x;
        if posn() <= rm then prin2 " " >>;

endmodule;

end;
