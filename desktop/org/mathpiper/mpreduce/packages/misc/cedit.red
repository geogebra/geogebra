module cedit; % REDUCE input string editor.

% Author: Anthony C. Hearn;

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


create!-package('(cedit),'(util));

fluid '(!*mode rprifn!* rterfn!*);

global '(!$eol!$
         !*blanknotok!*
         !*eagain
         !*full
         crbuf!*
         crbuf1!*
         crbuflis!*
         esc!*
         inputbuflis!*
         statcounter);


%esc!* := intern ascii 125;   %this is system dependent and defines
                              %a terminator for strings.

symbolic procedure rplacw(u,v);
   if atom u or atom v then errach list('rplacw,u,v)
    else rplacd(rplaca(u,car v),cdr v);

symbolic procedure cedit n;
   begin scalar x,ochan;
      if null terminalp() then rederr "Edit must be from a terminal";
      ochan := wrs nil;
      if n eq 'fn then x := reversip crbuf!*
       else if null n
        then if null crbuflis!*
               then <<statcounter := statcounter-1;
                      rederr "No previous entry">>
              else x := cdar crbuflis!*
       else if (x := assoc(car n,crbuflis!*))
        then x := cedit0(cdr x,car n)
       else <<statcounter := statcounter-1;
              rederr list("Entry",car n,"not found")>>;
      crbuf!* := nil;
      x := for each j in x collect j;   %to make a copy.
      terpri();
      editp x;
      terpri();
      x := cedit1 x;
      wrs ochan;
      if x eq 'failed then nil else crbuf1!* := x
   end;

symbolic procedure cedit0(u,n);
   % Returns input string augmented by appropriate mode.
   begin scalar x;
      if not(x := assoc(n,inputbuflis!*)) or ((x := cadr x) eq !*mode)
        then return u
       else return append(explode x,append(cdr explode '! ,u))
   end;

symbolic procedure cedit1 u;
   begin scalar x,y,z;
      z := setpchar '!>;
      if not !*eagain
        then <<prin2t "For help, type ?"; !*eagain := t>>;
      while u and (car u eq !$eol!$) do u := cdr u;
      u := append(u,list '! );   %to avoid 'last char' problem.
      if !*full then editp u;
    top:
      x := u;   %current pointer position.
    a:
      y := readch();   %current command.
      if y eq '!P or y eq '!p then editp x
       else if y eq '!I or y eq '!i then editi x
       else if y eq '!C or y eq '!c then editc x
       else if y eq '!D or y eq '!d then editd x
       else if y eq '!F or y eq '!f then x := editf(x,nil)
       else if y eq '!E or y eq '!e
        then <<terpri(); editp1 u; setpchar z; return u>>
       else if y eq '!Q or y eq '!q then <<setpchar z; return 'failed>>
       else if y eq '!? then edith()
       else if y eq '!B or y eq '!b then go to top
       else if y eq '!K or y eq '!k then editf(x,t)
       else if y eq '!S or y eq '!s then x := edits x
       else if y eq '!  and not !*blanknotok!* or y eq '!X or y eq '!x
        then x := editn x
       else if y eq '!  and !*blanknotok!* then go to a
       else if y eq !$eol!$ then go to a
       else lprim!* list(y,"Invalid editor character");
      go to a
   end;

symbolic procedure editc x;
   if null cdr x then lprim!* "No more characters"
    else rplaca(x,readch());

symbolic procedure editd x;
   if null cdr x then lprim!* "No more characters"
    else rplacw(x,cadr x . cddr x);

symbolic procedure editf(x,bool);
   begin scalar y,z;
      y := cdr x;
      z := readch();
      if null y then return <<lprim!* list(z,"Not found"); x>>;
      while cdr y and not(z eq car y) do y := cdr y;
      return if null cdr y then <<lprim!* list(z,"Not found"); x>>
                else if bool then rplacw(x,car y . cdr y)
                else y
   end;

symbolic procedure edith;
   <<prin2t "THE FOLLOWING COMMANDS ARE SUPPORTED:";
     prin2t "   B              move pointer to beginning";
     prin2t "   C<character>   replace next character by <character>";
     prin2t "   D              delete next character";
     prin2t "   E              end editing and reread text";
     prin2t
    "   F<character>   move pointer to next occurrence of <character>";
     prin2t
       "   I<string><escape>   insert <string> in front of pointer";
     prin2t "   K<character>   delete all chars until <character>";
     prin2t "   P              print string from current pointer";
     prin2t "   Q              give up with error exit";
     prin2t
       "   S<string><escape> search for first occurrence of <string>";
     prin2t "                      positioning pointer just before it";
     prin2t "   <space> or X   move pointer right one character";
     terpri();
     prin2t
       "ALL COMMAND SEQUENCES SHOULD BE FOLLOWED BY A CARRIAGE RETURN";
     prin2t "    TO BECOME EFFECTIVE">>;

symbolic procedure editi x;
   begin scalar y,z;
      while (y := readch()) neq esc!* do z := y . z;
      rplacw(x,nconc(reversip z,car x . cdr x))
   end;

symbolic procedure editn x;
   if null cdr x then lprim!* "NO MORE CHARACTERS"
    else cdr x;

symbolic procedure editp u;
   <<editp1 u; terpri()>>;

symbolic procedure editp1 u;
   for each x in u do if x eq !$eol!$ then terpri() else prin2 x;

symbolic procedure edits u;
   begin scalar x,y,z;
      x := u;
      while (y := readch()) neq esc!* do z := y . z;
      z := reversip z;
  a:  if null x then return <<lprim!* "not found"; u>>
       else if edmatch(z,x) then return x;
      x := cdr x;
      go to a
   end;

symbolic procedure edmatch(u,v);
   % Matches list of characters U against V. Returns rest of V if
   % match occurs or NIL otherwise.
   if null u then v
    else if null v then nil
    else if car u=car v then edmatch(cdr u,cdr v)
    else nil;

symbolic procedure lprim!* u; <<lprim u; terpri()>>;

Comment Editing Function Definitions;

remprop('editdef,'stat);

symbolic procedure editdef u; editdef1 car u;

symbolic procedure editdef1 u;
   begin scalar type,x;
      if null(x := getd u) then return lprim list(u,"not defined")
       else if codep cdr x or not eqcar(cdr x,'lambda)
        then return lprim list(u,"cannot be edited");
      type := car x;
      x := cdr x;
      if type eq 'expr then x := 'de . u . cdr x
       else if type eq 'fexpr then x := 'df . u . cdr x
       else if type eq 'macro then x := 'dm . u . cdr x
       else rederr list("strange function type",type);
      rprifn!* := 'add2buf;
      rterfn!* := 'addter2buf;
      crbuf!* := nil;
      x := errorset!*(list('rprint,mkquote x),t);
      rprifn!* := nil;
      rterfn!* := nil;
      if errorp x then return (crbuf!* := nil);
      crbuf!* := cedit 'fn;
      return nil
   end;

symbolic procedure add2buf u; crbuf!* := u . crbuf!*;

symbolic procedure addter2buf; crbuf!* := !$eol!$ . crbuf!*;

put('editdef,'stat,'rlis);

Comment Displaying past input expressions;

put('display,'stat,'rlis);

symbolic procedure display u;
   % Displays input stack in reverse order.
   % Modification to reverse list added by F. Kako.
  begin scalar x,w;
      u := car u;
      x := crbuflis!*;
      terpri();
      if not numberp u then u := length x;
      while u>0 and x do
       <<w := car x . w; x := cdr x; u := u - 1>>;
      for each j in w do
       <<prin2 car j; prin2 ": "; editp cdr j; terpri()>>
  end;

endmodule;

end;
