MODULE RINTRO!-TXT;  % Description of non-local variables used in RLISP.

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


% These must be initialized at the top level of the program.

%CRBUFLIS!* := NIL;     %terminal input buffer;
%CMSG!* := NIL;         %shows that continuation msg has been printed;
%DFPRINT!* := NIL;      %used to define special output process;
%ERFG!* := NIL;         %indicates that an input error has occurred;
%INPUTBUFLIS!* := NIL;  %association list for storing input commands;
%LETL!* := NIL;         %used in algebraic mode for special delimiters;
%LREADFN!* := NIL;      %used to define special reading function;
%OUTL!* := NIL;         %storage for output of input line;
%RESULTBUFLIS!* := NIL;  %association list for storing command outputs;
%TECHO!* := NIL;        %terminal echo status;
%TSLIN!* := NIL;        %stack of input reading functions;
%!*BACKTRACE := NIL;    %if ON, prints a LISP backtrace;
%!*BLANKNOTOK!* := NIL; %if ON, disables blank as CEDIT character;
%!*COMPOSITES := NIL;   %used to indicate the use of composite numbers;
%!*FORCE := NIL;        %causes all macros to expand;
%!*MSG:=NIL;            %flag to indicate whether messages should be
                        %printed;
%!*NAT := NIL;          %used in algebraic mode to denote 'natural'
                        %output. Must be on in symbolic mode to
                        %ensure input echoing;
%NAT!*!* := NIL;        %temporary variable used in algebraic mode;
%!*NOSAVE!*             %used to denote a command not to be saved in
                        %input history;
%!*SLIN := NIL;         %indicates that LISP code should be read;

% These are initialized within some function, although they may not
% appear in that function's variable list.

% CRCHAR!*               next character in input line
% CURSYM!*               current symbol (i. e. identifier, parenthesis,
%                       delimiter, e.t.c,) in input line
% FNAME!*                name of a procedure being read
% FTYPES!*               list of regular procedure types
% IFL!*           input file/channel pair - set in BEGIN to NIL
% IPL!*           input file list- set in BEGIN to NIL
% NXTSYM!*               next symbol read in TOKEN
% OFL!*           output file/channel pair - set in BEGIN to NIL
% OPL!*           output file list- set in BEGIN to NIL
% PROGRAM!*              current input program
% PROGRAML!*             stores input program when error occurs for a
%                       later restart
% SEMIC!*                current delimiter character (used to decide
%                       whether to print result of calculation)
% TTYPE!*               current token type
% !*BLOCKP!*            keeps track of which block is active
% !*MODE         current mode of calculation
;

endmodulel;

end;
