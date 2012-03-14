module inter; % Functions for interactive support.

% Author: Anthony C. Hearn.

% Copyright (c) 1993 RAND.  All rights reserved.

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


fluid '(!*echo !*int);

global '(!$eof!$
         !$eol!$
         !*lessspace
         cloc!*
         contl!*
         curline!*
         edit!*
         eof!*
         erfg!*
         flg!*
         ifl!*
         ipl!*
         key!*
         ofl!*
         opl!*
         techo!*);

symbolic procedure pause;
   %Must appear at the top-most level;
   if null !*int then nil
    else if key!* eq 'pause then pause1 nil
    else %typerr('pause,"lower level command");
         pause1 nil;   % Allow at lower level for now.

symbolic procedure pause1 bool;
   begin scalar x;
      if bool then
        if getd 'edit1 and erfg!* and cloc!* and yesp "Edit?"
          then return <<contl!* := nil;
           if ofl!* then <<lprim list(car ofl!*,'shut);
                           close cdr ofl!*;
                           opl!* := delete(ofl!*,opl!*);
                           ofl!* := nil>>;
           edit1(cloc!*,nil)>>
         else if flg!* then return (edit!* := nil);
      if null ifl!* or yesp "Cont?" then return nil;
      ifl!* := list(car ifl!*,cadr ifl!*,curline!*);
      if x := assoccar(car ifl!*,contl!*)
        then <<contl!* := delete(x,contl!*); close cadar x>>;
      contl!* := (ifl!* . cdr ipl!* . !*echo) . contl!*;
      ifl!* := ipl!* := nil;
      rds nil;
      !*echo := techo!*
   end;

symbolic procedure assoccar(u,v);
   % Returns element of v in which caar of that element = u.
   if null v then nil
    else if u=caaar v then car v
    else assoccar(u,cdr v);

symbolic procedure yesp u;
   begin scalar ifl,ofl,x,y;
        if ifl!*
          then <<ifl := ifl!* := list(car ifl!*,cadr ifl!*,curline!*);
                 rds nil>>;
        if ofl!* then <<ofl:= ofl!*; wrs nil>>;
        if null !*lessspace then terpri();
        if atom u then prin2 u else lpri u;
        prin2t " (Y or N)";
        if null !*lessspace then terpri();
        y := setpchar '!?;
        x := yesp1();
        setpchar y;
        if ofl then wrs cdr ofl;
        if ifl then rds cadr ifl;
        cursym!* := '!*semicol!*;
        return x
   end;

symbolic procedure yesp1;
   % Basic loop for reading response.
   begin scalar bool,x,y;
    a:  x := readch();
        if x eq !$eol!$ then go to a
        % Assume an end-of-file means lost control and exit.
         else if x eq !$eof!$ then eval '(bye)
         %% else if (y := x eq 'y) or x eq 'n then return y
         else if (y := x memq '(!y !Y)) or x memq '(!n !N)
          then return y % F.J. Wright.
         else if null bool then <<prin2t "Type Y or N"; bool := t>>;
        go to a
   end;

symbolic procedure cont;
   begin scalar fl,techo;
        if ifl!* then return nil   % CONT only active from terminal.
         else if null contl!* then rerror(rlisp,28,"No file open");
        fl := caar contl!*;
        ipl!* := fl . cadar contl!*;
        techo := cddar contl!*;
        contl!* := cdr contl!*;
        if car fl=caar ipl!* and cadr fl=cadar ipl!*
          then <<ifl!* := fl;
                 if fl then <<rds cadr fl; curline!* := caddr fl>>
                  else rds nil;
                 !*echo := techo>>
         else <<eof!* := 1; lprim list(fl,"not open"); error1()>>
   end;

deflist ('((cont endstat) (pause endstat) (retry endstat)),'stat);

flag ('(cont),'ignore);

endmodule;

end;
