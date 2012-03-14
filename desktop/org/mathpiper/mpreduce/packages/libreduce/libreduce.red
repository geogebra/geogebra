% ----------------------------------------------------------------------
% $Id: libreduce.red 1257 2011-08-14 12:04:39Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008-2009 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

lisp <<
   fluid '(lr_rcsid!* lr_copyright!*);
   lr_rcsid!* := "$Id: libreduce.red 1257 2011-08-14 12:04:39Z thomas-sturm $";
   lr_copyright!* := "(c) 2008 T. Sturm"
>>;

module coloutput;

fluid '(!*mode);

global '(statcounter);

copyd('lr_add2resultbuf,'add2resultbuf);

on comp;

procedure lr_sprint(u);
   prin2 u;

procedure lr_aprint(u);
   prin2 reval u;

procedure lr_result();
   <<
      prin2 int2id 3;
   >>;

procedure lr_statcounter();
   <<
      prin2 int2id 4
   >>;

procedure lr_mode();
   <<
      prin2 int2id 5
   >>;

procedure lr_posttext();
   <<
      prin2 int2id 6
   >>;

procedure lr_printer(u,mode);
   <<
      if mode eq 'symbolic then
	 (if u or !*mode eq 'symbolic then
	    lr_sprint u)
      else
	 lr_aprint u
   >>;

procedure add2resultbuf(u,mode);
   <<
      lr_result();
      if null(semic!* eq '!$) then lr_printer(u,mode);
      lr_statcounter();
      prin2 statcounter;
      lr_mode();
      prin2 if !*mode eq 'symbolic then 1 else 0;
      lr_posttext();
      terpri();
      lr_add2resultbuf(u,mode)
   >>;

off comp;

endmodule;  % coloutput;

module redfront;
% Prompt coloring for redfront.
% Written by Andreas Dolzmann and Thomas Sturm, 1998

fluid '(promptstring!* lr_switches!* lr_switches!-this!-sl!*
   lispsystem!*);

lr_switches!* := {!*msg,!*output};

off1 'msg;
off1 'output;

procedure lr_pslp();
   'psl memq lispsystem!*;

if lr_pslp() then <<
   lr_switches!-this!-sl!* := {!*usermode};
   off1 'usermode
>>;

procedure lr_color(c);
   if stringp c then
      compress('!" . int2id 1 .
      	 reversip('!" . int2id 2 . cdr reversip cdr explode c))
   else
      intern compress(int2id 1 . nconc(explode c,{int2id 2}));

procedure lr_uncolor(c);
   if stringp c then
      compress('!" . reversip('!" . cddr reversip cddr explode c))
   else
      intern compress('!! . reversip cdr reversip cdr explode c);

procedure lr_setpchar!-psl(c);
   begin scalar w;
      w := lr_setpchar!-orig c;
      promptstring!* := lr_color promptstring!*;
      return lr_uncolor w
   end;

procedure lr_setpchar!-csl(c);
   lr_uncolor lr_setpchar!-orig lr_color c;

copyd('lr_setpchar!-orig,'setpchar);

if lr_pslp() then
   copyd('setpchar,'lr_setpchar!-psl)
else
   copyd('setpchar,'lr_setpchar!-csl);

procedure lr_yesp!-psl(u);
   begin scalar ifl,ofl,x,y;
      if ifl!* then <<
	 ifl := ifl!* := {car ifl!*,cadr ifl!*,curline!*};
	 rds nil
      >>;
      if ofl!* then <<
	 ofl:= ofl!*;
 	 wrs nil
      >>;
      if null !*lessspace then
 	 terpri();
      if atom u then
 	 prin2 u
      else
 	 lpri u;
      if null !*lessspace then
 	 terpri();
      y := setpchar "?";
      x := yesp1();
      setpchar y;
      if ofl then wrs cdr ofl;
      if ifl then rds cadr ifl;
      cursym!* := '!*semicol!*;
      return x
   end;

if lr_pslp() then <<
   remflag('(yesp),'lose);
   copyd('lr_yesp!-orig,'yesp);
   copyd('yesp,'lr_yesp!-psl);
   flag('(yesp),'lose)
>>;

% Color PSL prompts, in case user falls through:

!#if (memq 'psl lispsystem!*)

procedure lr_compute!-prompt!-string(count,level);
   lr_color lr_compute!-prompt!-string!-orig(count,level);

if lr_pslp() then <<
   copyd('lr_compute!-prompt!-string!-orig,'compute!-prompt!-string);
   copyd('compute!-prompt!-string,'lr_compute!-prompt!-string)
>>;

procedure lr_break_prompt();
   <<
      prin2 "break["; prin2 breaklevel!*; prin2 "]";
      promptstring!* := lr_color promptstring!*
   >>;

!#endif

if lr_pslp() then <<
   copyd('break_prompt,'lr_break_prompt);
   flag('(break_prompt),'lose);
>>;

if lr_pslp() then
   onoff('usermode,car lr_switches!-this!-sl!*);

onoff('msg,car lr_switches!*);
onoff('output,cadr lr_switches!*);

crbuf!* := nil;
inputbuflis!* := nil;
lessspace!* := t;
statcounter := 0;

off1 'msg;
off1 'output;
on1 'time;

endmodule;

end;
