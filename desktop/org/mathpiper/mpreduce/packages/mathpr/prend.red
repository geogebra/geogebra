module prend;

% Author: Arthur C. Norman.

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


fluid '(!*clisp);

switch clisp;

% These parts come from rend.red.....

comment
vdu(23, 235,   0,   0, 224,  48,  24,  24,  24,  24, % mat-top-r
    23, 236,  24,  24,  24,  24,  12,   7,   0,   0, % mat-bottom-l
    23, 237,   0,  30,  51,  97,  97,  51,  30,   0, % infinity left
    23, 238,   0, 120, 204, 134, 134, 204, 120,   0, % infinity right
    23, 239,   24, 24,  24,  24,   0,   0,   0,   0, % pi bottom
    23, 240,   0,   0, 127,  54,  54,  54,  54,   0, % pi
    23, 241,   0,   0,   0, 255,   0,   0,   0,   0, % fraction bar
    23, 242,   0,   0,   7,  12,  24,  24,  24,  24, % int-top
    23, 243,  24,  24,  24,  24,  24,  24,  24,  24, % int-mid
    23, 244,  24,  24,  24,  24,  48, 224,   0,   0, % int-bottom
    23, 245,  28,   6,   6,  62, 102, 102,  60,   0, % curly d
    23, 246,   6,   6,  12,  12, 236,  56,  24,   0, % square root
    23, 247,   0,   0,   0, 127,  96,  48,  24,  12, % sigma top left
    23, 248,   0,   0,   0, 254,   6,   0,   0,   0, % sigma top right
    23, 249,   6,   3,   1,   0,   1,   3,   6,  12, % sigma mid left
    23, 250,   0,   0, 128, 192, 128,   0,   0,   0, % sigma middle
    23, 251,  24,  48,  96, 127,   0,   0,   0,   0, % sigma bottom left
    23, 252,   0,   0,   6, 254,   0,   0,   0,   0, % sigma bottom rght
    23, 253,   0,   0,   0, 127,  24,  24,  24,  24, % pi top left
    23, 254,   0,   0,   0, 254,  24,  24,  24,  24  % pi top right
    );


% The following four functions are local to this module, and need to
% be defined in CLISP systems (and clisp turned on).

symbolic procedure character u; nil;

symbolic procedure clearbuff; nil;

symbolic procedure packbyte u; nil;

symbolic procedure mkatom; nil;

put('!.pi, 'clisp!-character, character 240);
put('bar, 'clisp!-character, character 241);
put('int!-top, 'clisp!-character, character 242);
put('int!-mid, 'clisp!-character, character 243);
put('int!-low, 'clisp!-character, character 244);
put('d, 'clisp!-character, character 245);
put('sqrt, 'clisp!-character, character 246);
put('vbar, 'clisp!-character, character 243);
put('sum!-top, 'clisp!-character,
    << clearbuff();
       packbyte 247; packbyte 241; packbyte 248;
       mkatom() >>);
put('sum!-mid, 'clisp!-character,
    << clearbuff();
       packbyte 249; packbyte 250; packbyte 32;
       mkatom() >>);
put('sum!-low, 'clisp!-character,
    << clearbuff();
       packbyte 251; packbyte 241; packbyte 252;
       mkatom() >>);
put('prod!-top, 'clisp!-character,
    << clearbuff();
       packbyte 253; packbyte 241; packbyte 254;
       mkatom() >>);
put('prod!-mid, 'clisp!-character,
    << clearbuff();
       packbyte 243; packbyte 32; packbyte 243;
       mkatom() >>);
put('prod!-low, 'clisp!-character,
    << clearbuff();
       packbyte 239; packbyte 32; packbyte 239;
       mkatom() >>);
put('infinity, 'clisp!-character,
    << clearbuff();
       packbyte 237; packbyte 238;
       mkatom() >>);

put('mat!-top!-l, 'clisp!-character, character 242);
put('mat!-top!-r, 'clisp!-character, character 235);
put('mat!-low!-l, 'clisp!-character, character 236);
put('mat!-low!-r, 'clisp!-character, character 244);

endmodule;

end;
