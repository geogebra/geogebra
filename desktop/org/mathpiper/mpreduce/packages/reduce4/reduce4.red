module reduce4; % Support for REDUCE 4 interface to REDUCE 3.

% Author: Anthony C. Hearn.

% Copyright (c) 1998. Anthony C. Hearn. All rights reserved.

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


create!-package('(reduce4 form4 block4 proc4 forstat4 struct4 reval4
                  simp4 forall4 rankstat ranks tower package4),
                  nil);

% Setting differences between REDUCE 3 and 4.

fluid '(!*debug !*eoldelimp !*lower !*mode !*oldmode4 !*reduce4
        lispsystem!*);

% off quotenewnam;

symbolic procedure !%reduce4;
   begin
   %  load!-package 'reduce4;
      load!-package 'tables;
      flag(list !$eol!$,'delchar);
%     !*debug := t;
      !*eoldelimp := t;
      !*lower := t;
      !*oldmode4 := !*mode;
      !*mode := 'algebraic;
      !*reduce4 := t;
      remflag('(plus times),'nary);
 %     !#if (member 'psl lispsystem!*)
 %        <<remprop('off,'newnam);
 %          remprop('off,'quotenewnam);
 %          remprop('!~off,'oldnam);
 %          remprop('on,'newnam);
 %          remprop('on,'quotenewnam);
 %          remprop('!~on,'oldnam)>>;
 %     !#endif
   end;

symbolic procedure !%reduce3;
   begin
      remflag(list !$eol!$,'delchar);
%     !*debug := nil;
      !*eoldelimp := nil;
      if !*oldmode4 then !*mode := !*oldmode4 else !*mode := 'symbolic;
      !*reduce4 := nil;
      flag('(plus times),'nary);
 %     !#if (member 'psl lispsystem!*)
 %         define!-alias!-list '(off on);
 %     !#endif
   end;

on quotenewnam;

switch debug, reduce4;

put('reduce4,'simpfg,'((t (!%reduce4)) (nil (!%reduce3))));

% version!* := "REDUCE 4";

endmodule;

end;
