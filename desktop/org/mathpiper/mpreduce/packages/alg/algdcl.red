module algdcl;  % Various declarations.

% Author: Anthony C. Hearn.

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


global '(preclis!* ws);

symbolic procedure formopr(u,vars,mode);
   if mode eq 'symbolic
     then list('flag,mkquote cdr u,mkquote 'opfn)
    else list('operator,mkarg(cdr u,vars));

put('operator,'formfn,'formopr);

symbolic procedure operator u; for each j in u do mkop j;

rlistat '(operator);

symbolic procedure remopr u;
   % Remove all operator related properties from id u.
   begin
      remprop(u,'alt);
      remprop(u,'infix);
      remprop(u,'op);
      remprop(u,'prtch);
      remprop(u,'simpfn);
      remprop(u,'unary);
      remflag(list u,'linear);
      remflag(list u,'nary);
      remflag(list u,'opfn);
      remflag(list u,'antisymmetric);
      remflag(list u,'symmetric);
      remflag(list u,'right);
      preclis!* := delete(u,preclis!*)
   end;

flag('(remopr),'eval);

symbolic procedure den u;
   mk!*sq (denr simp!* u ./ 1);

symbolic procedure num u;
   mk!*sq (numr simp!* u ./ 1);

flag('(den num),'opfn);

flag('(den num),'noval);

put('saveas,'formfn,'formsaveas);

symbolic procedure formsaveas(u,vars,mode);
   list('saveas,formclear1(cdr u,vars,mode));

symbolic procedure saveas u;
   let00 list list(if smemq('!~,car u) then 'replaceby else 'equal,
                   car u,
                   if eqcar(ws,'!*sq)
                      and smemql(for each x in frasc!* collect car x,
                                 cadr ws)
                     then list('!*sq,cadr ws,nil)
                    else ws);

rlistat '(saveas);

endmodule;

end;
