module superv; % REDUCE supervisory functions.

% Author: Anthony C. Hearn.

% Modified by: Jed B. Marti, Francis J. Wright.

% Copyright (c) 1998 Anthony C. Hearn.  All rights reserved.

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


fluid '(!*debug
        !*defn
        !*demo
        !*echo
        !*errcont
        !*int
        !*lisp!_hook
        !*mode
        !*output
        !*pret
        !*reduce4
        !*slin
        !*time
        !*rlisp88
        dfprint!*
        errmsg!*
        lispsystem!*
        loopdelimslist!*
        lreadfn!*
        newrule!*
        semic!*
        tslin!*);

global '(!$eof!$
         !*byeflag!*
         !*extraecho
         !*lessspace
         !*micro!-version
         !*nosave!*
         !*strind
         !*struct
         cloc!*
         cmsg!*
         crbuf!*
         crbuflis!*
         crbuf1!*
         curline!*
         cursym!*
         eof!*
         erfg!*
         forkeywords!*
         ifl!*
         ipl!*
         initl!*
         inputbuflis!*
         key!*
         ofl!*
         opl!*
         ogctime!*
         otime!*
         ogctime1!*
         otime1!*
         ogctime2!*
         otime2!*
         ogctime3!*
         otime3!*
         program!*
         programl!*
         promptexp!*
         repeatkeywords!*
         resultbuflis!*
         st!*
         statcounter
         symchar!*
         tok!*
         ttype!*
         whilekeywords!*
         ws);

!*output := t;
eof!* := 0;
initl!* := '(fname!* outl!*);
statcounter := 0;

% The true REDUCE supervisory function is BEGIN, again defined in the
% system dependent part of this program.  However, most of the work is
% done by BEGIN1, which is called by BEGIN for every file encountered
% on input;

symbolic procedure errorp u;
   %returns true if U is an ERRORSET error format;
   atom u or cdr u;

symbolic procedure printprompt u;
   %Prints the prompt expression for input;
   progn(ofl!* and wrs nil, prin2 u, ofl!* and wrs cdr ofl!*);

symbolic procedure setcloc!*;
   % Used to set for file input a global variable CLOC!* to dotted pair
   % of file name and dotted pair of line and page being read.
   % Currently a place holder for system specific function, since not
   % supported in Standard LISP.  CLOC!* is used in the INTER and RCREF
   % modules.
   cloc!* := if null ifl!* then nil else car ifl!* . (1 . curline!*);

symbolic procedure commdemo;
   begin scalar echo,x,y,z,!*demo;
      echo := !*echo;
      !*echo := nil;
      x := ifl!*;
      terpri();
      rds nil;
      y:=readch();
      if null seprp y then
             % Read command line from terminal.
          begin scalar crbuf,crbuf1,crchar,ifl;
                crbuf := crbuf!*;
                crbuf!* := nil;
                crbuf1 := crbuf1!*;
                crbuf1!* := list y;
                crchar := crchar!*;
                crchar!* := '! ;
                ifl := ifl!*;
                ifl!* := nil;
                z := errorset!*('(command),t);
                z := if errorp z then '(algebraic(aeval 0))
                     else car z;
                     % eat rest of line quietly.
             q: y  := readch();
                if y neq !$eol!$ then go to q;
                rds cadr x;
                crbuf!* := crbuf;
                crbuf1!* := crbuf1;
                crchar!* := crchar;
                ifl!* := ifl;
                !*echo := echo;
          end
       else
             % Read command from current input.
          progn(rds cadr x, !*echo := echo, z := command());
       return z
   end;

symbolic procedure command1;
   % Innermost part of COMMAND. Can be used as hook to editor if needed.
   begin
      scan();
      setcloc!*();
      key!* := cursym!*;
      return xread1 nil
   end;

symbolic procedure command;
   begin scalar errmsg!*,loopdelimslist!*,mode,x,y;
      if !*demo and ifl!* then return commdemo()
       else if null !*slin or !*reduce4 then go to a;
      % Note key!* not set in this case.
      setcloc!*();
      y := if lreadfn!* then lispapply(lreadfn!*,nil) else read();
      go to b;
   a: crchar!* := readch1();  % Initialize crchar!*.
      if crchar!* = !$eol!$ then go to a;
      % Parse input.
      y := command1();
   b: if !*reduce4 then go to c
       else if !*struct then y := structchk y;
      if !*pret and (atom y or null (car y memq '(in out shut)))
        then if null y and cursym!* eq 'end then rprint 'end
              else progn(rprint y,terpri());
      if !*slin then return list('symbolic,y);
      x := form y;
      % Determine target mode.
      if flagp(key!*,'modefn) then mode := key!*
       else if null atom x % and null !*micro!-version
         and null(car x eq 'quote)
         and (null(idp car x
                 and (flagp(car x,'nochange)
                       or flagp(car x,'intfn)
                       or car x eq 'list))
           or car x memq '(setq setel setf)
                   and eqcar(caddr x,'quote))
        then mode := 'symbolic
       else mode := !*mode;
      return list(mode,convertmode1(x,nil,'symbolic,mode));
   c: if !*debug then progn(prin2 "Parse: ",prettyprint y);
    % Mode analyze input.
      if key!* eq '!*semicol!* then go to a;  % Should be a comment.
      if null !*reduce4 then y := form y else y := n!_form y;
%     y := n!_form y;
      if !*debug then progn(terpri(),prin2 "Form: ",prettyprint y);
      return y
   end;

symbolic procedure update!_prompt;
   begin
      statcounter := statcounter + 1;
      promptexp!* :=
         compress('!! . append(explode statcounter,
                     explode if null symchar!* or !*mode eq 'algebraic
                               then '!:!  else '!*! ));
      setpchar promptexp!*
   end;

symbolic procedure begin1;
   begin1a nil;


symbolic procedure begin1a prefixchars;
   begin scalar parserr,result,x;
      otime!* := time();
      % The next line is that way for bootstrapping purposes.
      if getd 'gctime then ogctime!* := gctime() else ogctime!* := 0;
      otime1!* := otime2!* := otime3!* := otime!*;
      ogctime1!* := ogctime2!* := ogctime3!* := ogctime!*;
      peekchar!* := prefixchars;
      cursym!* := '!*semicol!*;
  a:  if terminalp()
        then progn((if !*nosave!* or statcounter=0 then nil
                     else add2buflis()),
                   update!_prompt());
      !*nosave!* := nil;
      !*strind := 0;     % Used by some versions of input editor.
      parserr := nil;
      if !*time then lispeval '(showtime);   % Since a STAT.
      if !*output and null ofl!* and terminalp() and null !*defn
         and null !*lessspace
        then terpri();
      if tslin!*
        then progn(!*slin := car tslin!*,
                   lreadfn!* := cdr tslin!*,
                   tslin!* := nil);
      x := initl!*;
 b:   if x then progn(sinitl car x, x := cdr x, go to b);
      remflag(forkeywords!*,'delim);
      remflag(repeatkeywords!*,'delim);
      remflag( whilekeywords!*,'delim);
      if !*int then erfg!* := nil;   % To make editing work properly.
      if cursym!* eq 'end then progn(comm1 'end, return nil)
       % Note that key* was set from *previous* command in following.
       else if terminalp() and null(key!* eq 'ed)
        then printprompt promptexp!*;
      x := errorset!*('(command),t);
      condterpri();
      if errorp x then go to err1;
      x := car x;
      if car x eq 'symbolic and eqcar(cadr x,'xmodule)
        then result := xmodloop eval cadr x
       else result := begin11 x;
      if null result then go to a
       else if result eq 'end then return nil
       else if result eq 'err2 then go to err2
       else if result eq 'err3 then go to err3;
  c:  if crbuf1!* then
        progn(lprim "Closing object improperly removed. Redo edit.",
                crbuf1!* := nil, return nil)
        else if eof!*>4
         then progn(lprim "End-of-file read", return lispeval '(bye))
       else if terminalp()
        then progn(crbuf!* := nil,!*nosave!* := t,go to a)
       else return nil;
  err1:
      if eofcheck() or eof!*>0 then go to c
       else if x="BEGIN invalid" then go to a;
      parserr := t;
  err2:
      resetparser();  % In case parser needs to be modified.
  err3:
      erfg!* := t;
      if null !*int and null !*errcont
        then progn(!*defn := t,
                   !*echo := t,
                   (if null cmsg!*
                      then lprie "Continuing with parsing only ..."),
                   cmsg!* := t)
       else if null !*errcont
        then progn(result := pause1 parserr,
                   (if result then return null lispeval result),
                   erfg!* := nil)
       else erfg!* := nil;
      go to a
   end;

% Newrule!* is initialized in the following function, since it is not
% always reinitialized by the rule code.

symbolic procedure begin11 x;
   begin scalar errmsg!*,mode,result,newrule!*;
      if cursym!* eq 'end
         then if terminalp() and null !*lisp!_hook
                then progn(cursym!* := '!*semicol!*, !*nosave!* := t,
                           return nil)
               else progn(comm1 'end, return 'end)
       else if eqcar((if !*reduce4 then x else cadr x),'retry)
        then if programl!* then x := programl!*
              else progn(lprim "No previous expression",return nil);
      if null !*reduce4 then progn(mode := car x,x := cadr x);
      program!* := x;    % Keep it around for debugging purposes.
      if eofcheck() then return 'c else eof!* := 0;
      add2inputbuf(x,if !*reduce4 then nil else mode);
      if null atom x
          and car x memq '(bye quit)
        then if getd 'bye
               then progn(lispeval x, !*nosave!* := t, return nil)
              else progn(!*byeflag!* := t, return nil)
       else if null !*reduce4 and eqcar(x,'ed)
        then progn((if getd 'cedit and terminalp()
                      then cedit cdr x
                     else lprim "ED not supported"),
                   !*nosave!* := t, return nil)
       else if !*defn
        then if erfg!* then return nil
              else if null flagp(key!*,'ignore)
                and null eqcar(x,'quote)
               then progn((if x then dfprint x else nil),
                          if null flagp(key!*,'eval) then return nil);
      if !*output and ifl!* and !*echo and null !*lessspace
        then terpri();
      result := errorset!*(x,t);
      if errorp result or erfg!*
        then progn(programl!* := list(mode,x),return 'err2)
       else if !*defn then return nil;
      if null !*reduce4
        then if null(mode eq 'symbolic) then x := getsetvars x else nil
       else progn(result := car result,
                  (if null result then result := mkobject(nil,'noval)),
                  mode := type result,
                  result := value result);
      add2resultbuf((if null !*reduce4 then car result else result),
                    mode);
      if null !*output then return nil
       else if null(semic!* eq '!$)
        then if !*reduce4 then (begin
                   terpri();
                   if mode eq 'noval then return nil
                    else if !*debug then prin2t "Value:";
                   rapply1('print,list list(mode,result))
                 end)
       else if mode eq 'symbolic
              then if null car result and null(!*mode eq 'symbolic)
                     then nil
              else begin
                  terpri();
                  result:=
                       errorset!*(list('print,mkquote car result),t)
                    end
       else if car result
        then result := errorset!*(list('assgnpri,mkquote car result,
                                       (if x then 'list . x else nil),
                                       mkquote 'only),
                                  t);
      if null !*reduce4
        then return if errorp result then 'err3 else nil
       else if null(!*mode eq 'noval) % and !*debug
        then progn(terpri(), prin2 "of type: ", print mode);
      return nil
   end;

symbolic procedure getsetvarlis u;
   if null u then nil
    else if atom u then errach list("getsetvarlis",u)
    else if atom car u then car u . getsetvarlis cdr u
    else if caar u memq '(setel setk)   % setk0.
     then getsetvarlis cadar u . getsetvarlis cdr u
    else if caar u eq 'setq then mkquote cadar u . getsetvarlis cdr u
    else car u . getsetvarlis cdr u;

symbolic procedure getsetvars u;
   if atom u then nil
    else if car u memq '(setel setk)   % setk0.
     then getsetvarlis cadr u . getsetvars caddr u
    else if car u eq 'setq then mkquote cadr u . getsetvars caddr u
    else nil;

flag ('(deflist flag fluid global remflag remprop unfluid),'eval);

symbolic procedure close!-input!-files;
   % Close all input files currently open;
   begin
      if ifl!* then progn(rds nil,ifl!* := nil);
  aa: if null ipl!* then return nil;
      close cadar ipl!*;
      ipl!* := cdr ipl!*;
      go to aa
   end;

symbolic procedure close!-output!-files;
   % Close all output files currently open;
   begin
      if ofl!* then progn(wrs nil,ofl!* := nil);
  aa: if null opl!* then return nil;
      close cdar opl!*;
      opl!* := cdr opl!*;
      go to aa
   end;

symbolic procedure add2buflis;
   begin
      if null crbuf!* then return nil;
      crbuf!* := reversip crbuf!*;   %put in right order;
   a: if crbuf!* and seprp car crbuf!*
        then progn(crbuf!* := cdr crbuf!*, go to a);
      crbuflis!* := (statcounter . crbuf!*) . crbuflis!*;
      crbuf!* := nil
   end;

symbolic procedure add2inputbuf(u,mode);
   begin
      if null terminalp() or !*nosave!* then return nil;
      inputbuflis!* := list(statcounter,mode,u) . inputbuflis!*
   end;

symbolic procedure add2resultbuf(u,mode);
   begin
      if mode eq 'symbolic
       or (null u and (null !*reduce4 or null(mode eq 'empty!_list)))
       or !*nosave!* then return nil;
      if !*reduce4 then putobject('ws,u,mode) else ws := u;
      if terminalp()
        then resultbuflis!* := (statcounter . u) . resultbuflis!*
   end;

symbolic procedure condterpri;
   !*output and !*echo and !*extraecho and (null !*int or ifl!*)
        and null !*defn and null !*demo and terpri();

symbolic procedure eofcheck;
   % true if an end-of-file has been read in current input sequence;
   program!* eq !$eof!$ and ttype!*=3 and (eof!* := eof!*+1);

symbolic procedure resetparser;
   %resets the parser after an error;
   if null !*slin then comm1 t;

symbolic procedure terminalp;
   %true if input is coming from an interactive terminal;
   !*int and null ifl!*;

symbolic procedure dfprint u;
   % Looks for special action on a form, otherwise prettyprints it.
   if dfprint!* then lispapply(dfprint!*,list u)
    else if cmsg!* then nil
    else if null eqcar(u,'progn) then prettyprint u
    else begin
            a:  u := cdr u;
                if null u then return nil;
                dfprint car u;
                go to a
         end;


symbolic procedure showtime;
   begin scalar x,y;
      x := otime!*;
      otime!* := time();
      x := otime!* - x;
      y := ogctime!*;
      ogctime!* := gctime();
      y := ogctime!* - y;
      if 'psl memq lispsystem!* then x := x - y;
      terpri();
      prin2 "Time: "; prin2 x; prin2 " ms";
      if null(y=0)
        then progn(prin2 "  plus GC time: ", prin2 y, prin2 " ms");
      terpri();
      return if !*reduce4 then mknovalobj() else nil
   end;

% OK so what is this all about...
% Well for benchmarking I would like to record the time spent in
% a test script. However some test scripts use "showtime" and that
% then interferes. So I introduce a variant on showtime specifically
% for my use that does just the same but that will be independent of
% the original version. I call this "showtime1". And then in a spirit
% of future-proofing I provide two further versions for other people
% to use too. So
%   showtime1;
%   showtime;
%   part A
%   showtime;    % time just for part A
%   part B
%   showtime;    % time just for part B
%   showtime1;   % total time.
% Because the counters used differ (obviously) I found this easiest to
% do my replicating code rather than having a single parameterised
% function.

symbolic procedure showtime1;
   begin scalar x,y;
      x := otime1!*;
      otime1!* := time();
      x := otime1!* - x;
      y := ogctime1!*;
      ogctime1!* := gctime();
      y := ogctime1!* - y;
      if 'psl memq lispsystem!* then x := x - y;
      terpri();
      prin2 "Time (counter 1): "; prin2 x; prin2 " ms";
      if null(y=0)
        then progn(prin2 "  plus GC time: ", prin2 y, prin2 " ms");
      terpri();
      return if !*reduce4 then mknovalobj() else nil
   end;

symbolic procedure showtime2;
   begin scalar x,y;
      x := otime2!*;
      otime2!* := time();
      x := otime2!* - x;
      y := ogctime2!*;
      ogctime2!* := gctime();
      y := ogctime2!* - y;
      if 'psl memq lispsystem!* then x := x - y;
      terpri();
      prin2 "Time (counter 2): "; prin2 x; prin2 " ms";
      if null(y=0)
        then progn(prin2 "  plus GC time: ", prin2 y, prin2 " ms");
      terpri();
      return if !*reduce4 then mknovalobj() else nil
   end;

symbolic procedure showtime3;
   begin scalar x,y;
      x := otime3!*;
      otime3!* := time();
      x := otime3!* - x;
      y := ogctime3!*;
      ogctime3!* := gctime();
      y := ogctime3!* - y;
      if 'psl memq lispsystem!* then x := x - y;
      terpri();
      prin2 "Time (counter 3): "; prin2 x; prin2 " ms";
      if null(y=0)
        then progn(prin2 "  plus GC time: ", prin2 y, prin2 " ms");
      terpri();
      return if !*reduce4 then mknovalobj() else nil
   end;

symbolic procedure resettime;
% Beware - because of bootstrapping restrictions I can not use
% << ... >> for progn here!
  progn(otime!* := time(),
       ogctime!* := gctime(),
       if !*reduce4 then mknovalobj() else nil);

symbolic procedure resettime1;
  progn(otime1!* := time(),
       ogctime1!* := gctime(),
       if !*reduce4 then mknovalobj() else nil);

symbolic procedure resettime2;
  progn(otime2!* := time(),
       ogctime2!* := gctime(),
       if !*reduce4 then mknovalobj() else nil);

symbolic procedure resettime3;
  progn(otime3!* := time(),
       ogctime3!* := gctime(),
       if !*reduce4 then mknovalobj() else nil);


symbolic procedure sinitl u;
   set(u,eval get(u,'initl));

symbolic procedure read!-init!-file name;
  % Read a resource file in REDUCE syntax. Quiet input.
  % Algebraic mode is used unless rlisp88 is on.
  % Look for file in home directory. If no home directory
  % is defined, use the current directory.
  begin scalar !*errcont,!*int,base,fname,oldmode,x,y;
   base := getenv "home" or getenv "HOME" or
           ((x := getenv "HOMEDRIVE") and (y := getenv "HOMEPATH")
              and concat2(x,y)) or ".";
   if not(car reversip explode2 base eq '!/)
     then base := concat2(base,"/"); % FJW
   fname := if filep(x := concat2(base,concat2(".", % FJW
                                                concat2(name,"rc"))))
               then x
             else if filep(x := concat2(base,concat2(name,".rc"))) % FJW
              then x
             else if filep
                     (x := concat2(getenv "HOME",concat2(name,".INI")))
               then x; % for (Open) VMS
   if null fname then return nil
    else if !*mode neq 'algebraic and null !*rlisp88
     then progn(oldmode := !*mode, !*mode := 'algebraic);
   x := errorset(list('in!_list1,fname,nil),nil,nil);
   if errorp x or erfg!* then
     progn(terpri(),
           prin2 "***** Error processing resource file ",
           prin2t fname);
   close!-input!-files();
   erfg!*:= cmsg!* := !*defn := nil;
   if oldmode then !*mode := oldmode;
   terpri();
   statcounter := 0
  end;

endmodule;

end;
