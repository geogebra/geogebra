module comment;   % Routines for handling active comments.

% Author:  Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


% This module supports the concept of active comments. Such comments are
% delimited by the comment brackets /* and */.  Everything read between
% those brackets is converted to a string (including eol), and the
% expression returned as the list (*comment* <comment string>).

symbolic procedure read!-comment;
   begin scalar ollength,raise,x,y,z;
      raise := !*raise;
      !*raise := nil;
      ollength := linelength 150;
      z := list(crchar!*,'!");
   a: if (x := readch()) eq '!*
        then if (y := readch()) eq '!/ then go to b
              else z := y . x . z
       else if x = !$eof!$
        then <<!*raise := raise; rederr "EOF encountered in comment">>
       else z := x . z;
      go to a;
   b:
      !*raise := raise;
      crchar!* := readch();
      z := '!" . z;
      z := list('!*comment!*,mkstrng compress reversip z);
      linelength ollength;
      return z
   end;

newtok '((!/ !*) !*comment!*);

endmodule;

end;
