module infix; % Functions for introducing new infix operators.

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


fluid '(!*mode);

global '(preclis!*);

symbolic procedure forminfix(u,vars,mode);
   begin scalar x;
      if null(mode eq 'symbolic)
       then x := for each j in cdr u collect list('mkop,mkarg(j,vars));
      u := list(car u,mkarg(cdr u,vars));
      return if x then 'progn . aconc(x,u) else u
   end;

put('infix,'formfn,'forminfix);

symbolic procedure infix x;
   <<for each j in x do
        if not(j member preclis!*) then preclis!* := j . preclis!*;
     mkprec()>>;

symbolic procedure precedence u;
   begin scalar x,y,z;
      preclis!* := delete(car u,preclis!*);
      y := cadr u;
      x := preclis!*;
   a: if null x then rerror(rlisp,16,list (y,"not found"))
       else if y eq car x
        then <<preclis!* :=
                  nconc!*(reversip!* z,car x . (car u . cdr x));
               mkprec();
               return nil>>;
      z := car x . z;
      x := cdr x;
      go to a
   end;

deflist('((infix rlis) (precedence rlis)),'stat);

flag('(infix precedence),'eval);

endmodule;

end;
