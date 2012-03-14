

module goutput;  % GENTRAN Code Formatting & Printing and Error Handler

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


%%  Author:  Barbara L. Gates  %%
%%  December 1986              %%

% Entry Points:  FormatC, FormatFort, FormatRat, GentranErr, FormatPasc
% All format routines moved to individual language modules
% JHD December 1987

symbolic$

fluid '(!*errcont)$

% GENTRAN Global Variables %
global '(!*errchan!* !*outchanl!* gentranlang!*
         !*posn!* !*stdin!* !*stdout!* !$eol!$)$
!*errchan!*     := nil$   %error channel number
!*posn!*        := 0$     %current position on output line

%%                            %%
%% General Printing Functions %%
%%                            %%

% Pprin2 and pterpri changed by F.Kako.
% Original did not work in SLISP/370, since output must be buffered.

global '(!*pprinbuf!*);

procedure pprin2 arg;
   begin
      !*pprinbuf!* := arg . !*pprinbuf!*;
      !*posn!* := !*posn!* + length explode2 arg;
   end$

procedure pterpri;
   begin
      scalar ch,pbuf;
      ch := wrs nil;
      pbuf := reversip !*pprinbuf!*;
      for each c in !*outchanl!* do
         <<wrs c;
           for each a in pbuf do
              if gentranlang!* eq 'fortran then fprin2 a else prin2 a;
         terpri()>>;
      !*posn!* := 0;
      !*pprinbuf!* := nil;
      wrs ch
   end$


%%               %%
%% Error Handler %%
%%               %%


%% Error & Warning Message Printing Routine %%


symbolic procedure gentranerr(msgtype, exp, msg1, msg2);
% Added check for !*errcont to aid graceful recovery from errors
% occurring in templates MCD 11.4.94
begin scalar holdich, holdoch, resp;
holdich := rds !*errchan!*;
holdoch := wrs !*errchan!*;
terpri();
if exp then prettyprint exp;
if (msgtype eq 'e) and not !*errcont then
<<
    rds cdr !*stdin!*;
    wrs cdr !*stdout!*;
    rederr msg1
>>;
prin2 "*** ";
prin2t msg1;
if msg2 then resp := yesp msg2;
wrs holdoch;
rds holdich;
if not(resp or !*errcont)  then error1()
end$


%%                 %%
%% Misc. Functions %%
%%                 %%


procedure min0(n1, n2);
max(min(n1, n2), 0)$

procedure nspaces n;
   % Note n is assumed > 0 here.
   begin scalar s;
      for i := 1:n do s := ('!! . '!  . s);
      return intern compress s
   end$


endmodule;


end;
