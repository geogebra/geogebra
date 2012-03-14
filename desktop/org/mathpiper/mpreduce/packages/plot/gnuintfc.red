module gnuintfc; % REDUCE-gnuplot interface.

% Authors:  Anthony C. Hearn, Herbert Melenk, Arthur C. Norman.

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


% The file complements the (notionally) generic file "gnuplot.red" to
% provide system-specific interfaces between REDUCE and the gnuplot
% package itself.

fluid '(plotstyle!*);

global '(!*plotinterrupts !*plotpause !*plotusepipe plotheader!*
         plotcleanup!* plottmp!*);

%%%%%%%%%%%%%%%%%%%%%%%%%%% PSL %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
!#if (member 'psl lispsystem!*)
   !#if (member 'unix lispsystem!*)

   !*plotusepipe:=t;               % pipes: yes
   load pipes;
   !*plotpause:=nil;                 % pause: no
   plottmp!* := "/tmp/";
   if getenv "LOGNAME" then
      plottmp!* := bldmsg("%w%w.",plottmp!*,getenv "LOGNAME")
    else if getenv "USER" then
      plottmp!* := bldmsg("%w%w.",plottmp!*,getenv "USER");
   plotdta!* := for i:=1:10 collect
         bldmsg("%wplotdta%w*",plottmp!*,i); % scratch data files
   plotcmds!* :=bldmsg("%wplotcmds*",plottmp!*); % if pipes not accessible

     % select header lines for setting the appropriate GNUPLOT
     % terminal type.

   if null plotheader!* then
   << if null x then
      if getenv "DISPLAY" then x := '(nil . 
"if(strstrt(GPVAL_TERMINALS,""aqua"")!=0)set terminal aqua;else set term x11;" )
                        else x:='(nil."dumb");
      if wlessp (strlen strinf cdr x, 20) 
                 then  plotheader!* :=bldmsg("set term %w",cdr x)
                       else plotheader!* := cdr x ;
   >>
     where x =
      assoc(getenv "TERM",
           '(
          %% You may want to extend or modify the terminal list above
                ("xterm" . 
"if(strstrt(GPVAL_TERMINALS,""aqua"")!=0)set terminal aqua;else set term x11;")
                ("xterm-color" . 
"if(strstrt(GPVAL_TERMINALS,""aqua"")!=0)set terminal aqua;else set term x11;")
                ("sun-cmd" . "x11")  ("sun" . "x11")
                ("hpterm" . "x11")
                ("vt52"  . "tek40xx")
                ("vt100" . "tek40xx")
                ("vt102" . "tek40xx")
           ));

      % add $reduce/plot to the path when using X11 (gnuplot will load a child).
   plotcommand!*:=
    begin scalar p;
     if not(p:=getenv "gnuplot") then
        p:=bldmsg("%w/plot",getenv "reduce");
     return bldmsg("PATH=$PATH:%w;export PATH;gnuplot",p);
    end;

   plotcleanup!* :=                  % delete scratch files
       {"rm /tmp/plotdt*","rm /tmp/plotcmds!*"};
   !*plotinterrupts := '(10002);

   !#elif (intersection '(dos os2 win32 winnt alphant) lispsystem!*)

fluid '(!*!*windows);

     % the following system selection must be done at load time

   plottmp!* := bldmsg("%w\",getenv "tmp" or getenv "temp");
   if null plottmp!* then
   <<prin2t "warning: no TMP directory found. Using >C:<";
     plottmp!*:="c:";
   >>;
   !*plotpause:=nil;                       % no pause
   plotdta!* := for i:=1:10 collect
         bldmsg("%wplotdt%w.dat",plottmp!*,i); % scratch data files

   if member ('win95,lispsystem!*) then
    <<
      % for Win95 until we have something better
      %   pass commands as parameter file
      %   write command file and data to directory /tmp

     !*plotusepipe:=nil;                   % no pipes
     plotcmds!* := bldmsg("%w\plotcmds",plottmp!*);
     plotcommand!* :=
%    fnexpand bldmsg("$reduce\plot\wgnuplot.exe %w",plotcmds!*);
     fnexpand bldmsg("$reduce\wutil\win32\wgnuplot.exe %w",plotcmds!*);
     plotheader!* :=  "";
     plotcleanup!* := NIL;                 % delete scratch files
     !*plotpause := -1;
     copyd('plot!-exec,'system);
     flag('(plot!-exec),'lose);
    >> else

   if !*!*windows eq 1 or member('winnt,lispsystem!*)
                       or member('win32 ,lispsystem!*) then
   <<
      % for windows:
      %  use plot pipe
     load w!-pipes;
     !*plotusepipe:=t;
     plotcmds!* := bldmsg("%w\plotcmds",plottmp!*);;
     plotcommand!* := bldmsg(
%      "%w\plot\wgnupl32.exe;wgnuplot_parent;wgnuplot_text",
%      "%w\plot\wgnuplot.exe;wgnuplot_parent;wgnuplot_text",
       if member('dos,lispsystem!*) then
        "%w\wutil\dos386\wgnuplot.exe;wgnuplot_parent;wgnuplot_text"
        else if member('alphant,lispsystem!*) then
        "%w\wutil\alphant\wgnuplot.exe;wgnuplot_parent;wgnuplot_text"
        else
        "%w\wutil\win32\wgnuplot.exe;wgnuplot_parent;wgnuplot_text",
        getenv("reduce"));
     plotheader!* := "";
     plotcleanup!* :=                     % delete scratch files
       bldmsg("del %w",plotcmds!*) .
        for each f in plotdta!* collect bldmsg("del %w",f);
    >>
  else if member('os2,lispsystem!*) then
    <<
     !*plotusepipe:=nil;                   % no pipes
     plotcmds!* := bldmsg("%w\plotcmds",plottmp!*);
     plotcommand!* := "";
     plotheader!* := "";

     symbolic procedure plot!-exec u;
       <<
         prin2 "====> invoke GNUPLOT for file ";
         prin2t plotcmds!*;
       >>;

     loadtime flag('(plot!-exec),'lose);
    >>
  else
    <<
      % for dos:
      %   pass commands as parameter file
      %   write command file and data to directory /tmp

     !*plotusepipe:=nil;                   % no pipes
     plotcmds!* := bldmsg("%w\plotcmds",plottmp!*);
     plotcommand!* :=
     fnexpand bldmsg("$reduce\wutil\dos386\gnuplot.exe %w",plotcmds!*);
     plotheader!* :=  "set term vga";
     plotcleanup!* :=                     % delete scratch files
       bldmsg("del %w",plotcmds!*).
        for each f in plotdta!* collect bldmsg("del %w",f);
    >>;


  plotmax!* := 3e36;                    % IEEE single precision
  !*plotinterrupts := '(10002);

   !#elif (member 'vms lispsystem!*)

   !*plotusepipe:=NIL;               % pipes: yes
   !*plotpause:=nil;                 % pause: no
   plottmp!* := "SYS$SCRATCH";
   plotdta!* := for i:=1:10 collect
         bldmsg("%w:plotdt%w",plottmp!*,i); % scratch data files
   plotcmds!* :=bldmsg("%w:plotcmds",plottmp!*); % if pipes not accessible
   plotcommand!* := bldmsg("gnuplot %w",plotcmds!*);
   plotheader!* :=    "set term x11;";

   plotcleanup!* :=                  % delete scratch files
       {"del SYS$SCRATCH:plotdt*;*","del SYS$SCRATCH:plotcmds*;*"};
   !*plotinterrupts := '(10002);

  !#endif

%%%%%%%%%%%%%%%%%%%%%%%%%%% CSL %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
!#elif (member 'csl lispsystem!*)

%
% If the user has set environment variables called "tmp" or "temp" then
% that specify (in that priority order) directories to place temporary data
% in. If neither is set then the current directory will be used. If tmp or temp
% is set but does not name a directory then everything probably fails.
%

%
% In this code opsys!* comes from "assoc('opsys, lispsystem!*)" with
% 'unknown as its default value.
% This means that with CSL 6.0 its value is usually HOST_OS as determined by
% the GNU autoconf utilities. However for some systems I explicitly override
% that and eg on Windows it should be win32. In circumstances that should no
% longer ever arise it could become dos, finder, riscos or unix. These cases
% are no longer supported! In fact the only fuss here will be that for win32
% special action is taken - all other cases go a generic route that was once
% labelled as "unix".
%

%
% If a shell variable "gnuplot" is set then thet must be the directory in
% which the gnuplot binary will be found. On Windows at least a search will
% be made for "wgnuplot" in the directory that REDUCE was launched from, but
% on Unix-like systems it may be necessary either to set the gnuplot shell
% variable or to place the gnuplot binary on your PATH.
%

global '(plotdir!* dirchar!* opsys!* tempdir!*);

symbolic procedure channelflush x;
  << x := wrs x; flush(); wrs x >>;

symbolic procedure gtmpnam base;
  if null tempdir!* then base
  else compress ('!" . append(explodec tempdir!*,
                              car explodec dirchar!* . cdr explode base));

% In general I want the initialisation actions indicated here to be
% obeyed at load-time. I achieve this by packaging them all inside
% a function, which I then call.

symbolic procedure init_gnuplot();
<<

!*plotpause := -1;                % wait for newline from user

% plotcleanup!* is a list of commands to be obeyed after a gnuplot run.
% it is mainly needed to get rid of the data files (used even when pipes
% are available).
plotcleanup!* := {};

tempdir!* := getenv 'tmp;
if null tempdir!* then tempdir!* := getenv 'temp;

dirchar!* := "/";
plotcommand!* := "gnuplot";
opsys!* := assoc('opsys, lispsystem!*);
if null opsys!* then opsys!* := 'unknown
else opsys!* := cdr opsys!*;

% I USED to have special case code for Acorn RISCOS, for DOS, for Win32 and
% "unix", and moaned if none of those where detected. These days it seems
% more sense to make win32 a special case but otherwise assume a "unix-like"
% environment. This will cause pain if you run under Unix but without an
% X session, but in that case maybe the diagnostic about the inability to
% open a window can come from gnuplot rather than me!

if opsys!* neq 'win32 then opsys!* := 'unix;

% If a shell variable "gnuplot" is set it is expected to be the
% directory within which gnuplot binaries can be found.
if getenv "gnuplot" then plotdir!* := getenv "gnuplot"
% Otherwise, and this is what I hope and expect to happen more often, I
% will expect to find gnuplot in the same directory that REDUCE was
% loaded from. This will all work wonderfully under Windows, but under Unix
% there is a much greater chance that I will need to set an environment
% variable "gnuplot". However, I still want to leave open the possibility
% of plotdir!* being set.
 else if null plotdir!* and not (opsys!* = 'unix)
  then plotdir!* := get!-lisp!-directory();


if opsys!* = 'win32 then <<
% For Microsoft Windows use I try to use "wgnuplot" rather than "gnuplot",
% and the options provided are copied from the PSL case above.
    plotcommand!* := "wgnuplot";
    plotheader!* := "";
    dirchar!* := "\";
    plotdta!* := for each n in
       {"gnutmp.tm1", "gnutmp.tm2", "gnutmp.tm3", "gnutmp.tm4",
        "gnutmp.tm5", "gnutmp.tm6", "gnutmp.tm7", "gnutmp.tm8"}
       collect gtmpnam n;
    plotcleanup!* := if null tempdir!* then {"erase gnutmp.tm*"}
                     else {bldmsg("erase %w\gnutmp.tm*", tempdir!*)} >>

else <<  % Assume Unix with X11 in general, but if the version of GNUPLOT
         % being used knows about the "aqua" terminal type then assume that
         % we are on a Macintosh with that capability available and best.
    plotheader!* :=
"if(strstrt(GPVAL_TERMINALS,""aqua"")!=0)set terminal aqua;else set term x11;";
    plotdta!* := for i:=1:10 collect tmpnam();
    plotcmds!*:= tmpnam();
    plotcleanup!* :=
       bldmsg("rm %w", plotcmds!*) .
          for each f in plotdta!* collect bldmsg("rm %w", f) >>;

% If there are no pipes available gnuplot will need a command-line
% argument indicating where its input file is.

if 'pipes member lispsystem!* then !*plotusepipe:=t
else plotcommand!* := bldmsg("%w %w", plotcommand!*, plotcmds!*);

% If I have a directory to look for gnuplot in I either just give a
% full path or (under Unix) I temporarily switch current directories
% to there.

if plotdir!* then
    plotcommand!* := bldmsg("%w%w%w",
                            plotdir!*, dirchar!*, plotcommand!*);

   nil >>;

init_gnuplot();

!#endif

endmodule;

end;
