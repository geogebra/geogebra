

module templt;    %%  GENTRAN Template Processing Routines  %%

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

% Entry Points:  ProcCTem, ProcFortTem, ProcRatTem
% Moved to separate language modules - JHD December 1987

symbolic$

% User-Accessible Global Variables %
global '(gentranlang!* !$!#)$
fluid '(!*gendecs)$
share gentranlang!*, !$!#$
gentranlang!* := 'fortran$
!$!# := 0$
switch gendecs$

global '(!*space!* !*stdout!* !$eof!$ !$eol!$)$
        % GENTRAN Global Variables      %

!*space!* := '! $

fluid '(!*mode)$


%%                          %%
%% Text Processing Routines %%
%%                          %%

%%                                   %%
%% Template File Active Part Handler %%
%%                                   %%

symbolic procedure procactive;
% active parts:  ;BEGIN; ... ;END; %
% eof markers:   ;END;             %
begin scalar c, buf, mode, och, !*int,!*errcont;
% By turning INT off we avoid some excess blank lines, and avoid trouble
% with END being caught by BEGIN1.  We use !*errcont to recover
% gracefully when an error is caught in the template.
!*errcont := 't;
c := readch();
if c eq 'e then
    if (c := readch()) eq 'n then
        if (c := readch()) eq 'd then
            if (c := readch()) eq '!; then
                return !$eof!$
            else buf := '!;end
        else buf := '!;en
    else buf := '!;e
else if c eq 'b then
    if (c := readch()) eq 'e then
        if (c := readch()) eq 'g then
            if (c := readch()) eq 'i then
                if (c := readch()) eq 'n then
                    if (c := readch()) eq '!; then
                    <<
                        mode := !*mode;
                        !*mode := 'algebraic;
                        och := wrs cdr !*stdout!*;
                        begin1();
                        wrs och;
                        !*mode := mode;
                        linelength 150;
                        return if (c := readch()) eq !$eol!$
                                  then readch()
                                  else c
                    >>
                    else buf := '!;begin
                else buf := '!;begi
            else buf := '!;beg
        else buf := '!;be
    else buf := '!;b
else buf := '!;;
pprin2 buf;
return c
end$

endmodule;

end;
