module ftr;  % Various utilities for working with files and modules.

% ACN 2008: I BELIEVE that really all this code is now historic and
% not of great current use, but there were times in the past where it
% was used to re-structure the REDUCE source files...






% Author: Anthony C. Hearn.

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


% NOTE: This module uses Standard Lisp global *RAISE as a fluid.

% This module supports several applications of file-transform.
% Currently we have:

% make-dist-files:

% module_file_split:

% downcase_file:

% trunc:


create!-package('(ftr),'(util));

fluid '(!*echo !*downcase !*upcase current!-char!* previous!-char!*
        member!-channel!* old!-channel!*);

global '(!*raise charassoc!*);

global '(dir!*);  % output directory name.

% global '(dirchar!*);

switch downcase,upcase;

dir!* := "";   % default.

% dirchar!* := "/";


% ***** utility functions *****.

symbolic procedure s!-match(u,v);
   % Returns true if list of characters u begins with same characters
   % (regardless of case) as lower case string v.
   s!-match1(u,explode2 v);

symbolic procedure s!-match1(u,v);
   null v
      or u and (car u eq car v
                   or red!-uppercasep car u
                      and red!-char!-downcase car u eq car v)
         and s!-match1(cdr u,cdr v);

symbolic procedure reverse!-chars!-to!-string u;
   compress('!" . reversip('!" . u));

symbolic procedure red!-lowercasep u;
   u memq '(!a !b !c !d !e !f !g !h !i !j !k !l !m !n !o !p !q !r !s
            !t !u !v !w !x !y !z);

symbolic procedure red!-uppercasep u;
   u memq '(!A !B !C !D !E !F !G !H !I !J !K !L !M !N !O !P !Q !R !S !T
            !U !V !W !X !Y !Z);

symbolic procedure red!-char!-downcase u;
   (if x then cdr x else u) where x = atsoc(u,charassoc!*);

symbolic procedure string!-upcase u;
   begin scalar z;
      if not stringp u then u := '!" . append(explode2 u,'(!"))
       else u := explode u;
      for each x in u do z := red!-char!-upcase x . z;
      return compress reverse z
   end;

symbolic procedure red!-char!-upcase u;
   (if x then car x else u) where x = rassoc(u,charassoc!*);


% ***** functions for manipulating regular REDUCE module files *****.

symbolic procedure module_file_split u;
   file!-transform(u,function module!-file!-split1);

symbolic procedure module!-file!-split1;
   begin scalar x,!*raise;
      while not errorp (x := errorset!*('(uread),t))
            and (x := car x) neq !$eof!$
            and x neq 'END!; do
         if x neq 'MODULE then rerror(ftr,1,"Invalid module format")
          else begin scalar ochan,oldochan,y;
             y := xread t;   % Should be module name.
             ochan:= open(concat(dir!*,concat(mkfil y,".red")),'output);
             oldochan := wrs ochan;
             prin2 "module "; prin2 y; prin2 ";";
             read!-module();
             wrs oldochan;
             close ochan
            end
   end;

symbolic procedure uread;
   begin scalar !*raise; !*raise := t; return read() end;

symbolic procedure read!-module;
   begin integer eolcount; scalar x,y;
      eolcount := 0;
   a: if errorp (x := errorset!*('(readch),t))
         or (x := car x) = !$eof!$
         or eolcount > 20
        then rerror(ftr,2,"Invalid module format")
       else if x = !$eol!$ then eolcount := eolcount+1
       else eolcount := 0;
      prin2 x;
      if x memq '(!e !E)
         then if y = '(L U D O M D N E)
                  or y = '(!l !u !d !o !m !d !n !e)
                then <<prin2 readch();
                       terpri();
                       terpri();
                       prin2t "end;";
                       return nil>>
               else y := list x
        else if x memq '(N D M O U L !n !d !m !o !u !l)
         then y := x . y
        else y := nil;
     go to a
   end;

symbolic procedure make!-dist!-files u;
   % Makes a set of distribution files from the list of packages u.
   % Setting u to packages* in $rsrc/build/packages.red makes complete
   % set.
   for each x in u do make_dist_file x;

symbolic procedure make_dist_file x;
   begin scalar !*downcase,!*echo,!*int,!*lower,msg,!*raise,ochan,
                oldochan,v;
      !*downcase := t;
      v := concat(string!-downcase x,".red");
      prin2 "Creating ";
      prin2 v;
      prin2t " ...";
      ochan := open(mkfil v,'output);
      oldochan := wrs ochan;
      evload list x;   % To get package list.
      v := get(x,'package);
      if null v then v := list x;
      for each j in v do
	 file!-transform(module2file(j,x),function write_module);
      prin2t if !*downcase then "end;" else "END;";
      wrs oldochan;
      close ochan
   end;

symbolic procedure module2file(u,v);
   % Converts the module u to a fully rooted file name with v the
   % package name, assuming files exist on $rsrc followed by path
   % defined by package given by associate of u in modules!*.
   begin scalar x;
      x := "$reduce/src/";
      for each j in get(v,'path) do
%        x := concat(x,concat(string!-downcase j,dirchar!*));
	 x := concat(x,concat(string!-downcase j,"/"));
      return concat(x,concat(string!-downcase u,".red"))
   end;

symbolic procedure write_module;
   begin scalar x; repeat (x := write!-line nil) until x eq 'done end;

symbolic procedure write!-line bool;
   begin integer countr; scalar x,y;
      countr := 0;
      % EOF kludge.
      while (x := readline()) = "" and countr<10 do countr := countr+1;
      if countr=10 then return 'done
       else if countr>0 then for i:=1:countr do terpri();
      y := explode2 x;
      if null bool and s!-match(y,"endmodule;")
%        or bool and s!-match(x,"end;")
        then <<prin2t if !*upcase then string!-upcase x
                       else if !*downcase then string!-downcase x
                       else x;
               if null bool then <<terpri(); terpri()>>;
               return 'done>>;
      x := y;
  a:  if null x then return terpri();
      y := car x;
  b:  if y = '!% then return <<for each j in x do prin2 j; terpri()>>
       else if y = '!"
        then <<prin2 y;
               x := write!-until(cdr x,'(!"))>>
       else if y = '!!
        then <<prin2 y;
               x := cdr x;
               if null x then rerror(ftr,3,"Missing character after !");
               prin2 car x>>
       else if s!-match(x,"comment")
        then <<if !*upcase then prin2 "COMMENT" else prin2 "comment";
               for j := 1:7 do x := cdr x;
               x := write!-until(x,'(!; !$))>>
       else if y = '!  then
          <<countr := 1;
            while (x := cdr x) and (y := car x) = '!  do
               countr := countr + 1;
            if null x then return terpri();   % Trailing blanks.
            for i:=1:countr do prin2 " ";
            go to b>>
       else <<prin2 if !*upcase and red!-lowercasep y
                      then red!-char!-upcase y
                     else if !*downcase and red!-uppercasep y
                      then red!-char!-downcase y
                     else y>>;
      x := cdr x;
      go to a
   end;

symbolic procedure write!-until(x,u);
   begin scalar y;
 a:   if null x
        then <<terpri();
               x := explode2 readline(); go to a>>;
      y := car x;
      prin2 y;
      if y memq u then return x;
      x := cdr x;
      go to a
   end;


% ***** Converting a file to lower case *****.

symbolic procedure downcase_file u;
   % Convert file named u to lower case version.
   begin scalar ochan,oldochan,!*downcase,!*echo,!*int,!*raise;
      prin2t "*** Output is in file 'output'";
      !*downcase := t;
      ochan := open("output",'output);
      oldochan := wrs ochan;
      file!-transform(u,function write!-file);
      wrs oldochan;
      close ochan
   end;

symbolic procedure write!-file;
   begin scalar x;
      repeat (x := write!-line t) until x eq 'done end;


% ***** truncating a file to 80 characters *****.

symbolic procedure trunc u;
   % Truncate a file to 80 characters.
   <<lprim "output is in file 'output'";
     file!-transform(u,function read!-trunc)>>;

symbolic procedure read!-trunc;
   begin integer count;
         scalar !*echo,!*int,!*raise,bool,ochan,oldochan,x;
      oldochan := wrs (ochan := open("output",'output));
      while (x := readch()) neq !$eof!$ do
         if x eq !$eol!$ then <<bool := nil; count := 0; terpri()>>
          else if null bool
           then <<prin2 x; bool := (count := count+1)>79>>;
      write oldochan;
      close ochan
   end;

endmodule;

end;

