module fastmath;  % Definitions of key functions in the math module of
                  % arith.red using C versions.  This file should be
                  % loaded into REDUCE before the math module is loaded.

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


global '(!!deg2rad !!rad2deg !!floatbits);

compiletime
  global '(!!fleps1exp !!plumaxexp !!pluminexp !!timmaxexp !!timminexp);

symbolic smacro procedure degreestoradians x; times2(x,!!deg2rad);

symbolic smacro procedure radianstodegrees x; times2(x,!!rad2deg);

remflag('(sin cos tan sind cosd tand cotd secd cscd asin acos atan
       asecd acscd atan2d atan2 sqrt exp log hypot cosh sinh tanh),
      'lose);

% ***** REMOVE THE FOLLOWING LINE WHEN FLOAT.C/EXTERNALS.SL UPDATED.

flag('(hypot cosh sinh tanh),'lose);

% ***** REMOVE THE FOLLOWING LINE WHEN WE KNOW HOW TO HANDLE COMPLEX
%       VALUES FOR ACOS, ASIN.

flag('(acos asin),'lose);

% Trig functions in radians.

symbolic procedure cos x;
   begin scalar result;
      x := float x;    % We put this here to make sure no GC can happen
                       % between gtfltn and mkfltn.
      result := gtfltn();
      uxcos(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure sin x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxsin(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure tan x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxtan(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure acos x;
   begin scalar result;
      if abs x> 1.0
        then error(99,list("argument to ACOS too large:",x));
      x := float x;
      result := gtfltn();
      uxacos(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure asin x;
   begin scalar result;
      if abs x> 1.0
        then error(99,list("argument to ASIN too large:",x));
      x := float x;
      result := gtfltn();
      uxasin(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure atan x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxatan(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure atan2(y,x);
   begin scalar result;
      x := float x;
      y := float y;
      result := gtfltn();
      uxatan2(floatbase result,floatbase fltinf y,floatbase fltinf x);
      return mkfltn result
   end;

% ASEC defined in math.red.


% Trig functions in degrees.

symbolic procedure sind x;
   sin degreestoradians x;

symbolic procedure cosd x;
   cos degreestoradians x;

symbolic procedure tand x;
   tan degreestoradians x;

symbolic procedure cotd x;
   cot degreestoradians x;

symbolic procedure secd x;
   sec degreestoradians x;

symbolic procedure cscd x;
   csc degreestoradians x;

symbolic procedure asecd x;
   radianstodegrees asec x;

symbolic procedure acscd x;
   radianstodegrees acsc x;

symbolic procedure atan2d(y,x);
   radianstodegrees atan2(y,x);


% Exponential, logarithm, power, square root, hypotenuse.

symbolic procedure exp x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxexp(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure log x;
   begin scalar result, ilog2x;
      if x <= 0.0
        then error(99,list("non-positive argument to LOG:",x))
       else if fixp(x) and (ilog2x:=ilog2(x)) > !!floatbits
        then return log2*(ilog2x - !!floatbits)
                 + log(x/2^(ilog2x - !!floatbits));
      x := float x;
      result := gtfltn();
      uxlog(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

% LOG10 in math.red.

symbolic procedure sqrt x;
   begin scalar result;
      if x < 0.0
        then error(99,list("negative argument to SQRT:",x));
      x := float x;
      result := gtfltn();
      uxsqrt(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure hypot(x,y);
   begin scalar result;
      x := float x;
      y := float y;
      result := gtfltn();
      uxhypot(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;


% Hyperbolic functions.

symbolic procedure cosh x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxcosh(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure sinh x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxsinh(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

symbolic procedure tanh x;
   begin scalar result;
      x := float x;
      result := gtfltn();
      uxtanh(floatbase result,floatbase fltinf x);
      return mkfltn result
   end;

(for each u in
   '(sin cos tan sind cosd tand cotd secd cscd asin acos atan
     asecd acscd atan2d atan2 sqrt exp log hypot cosh sinh tanh)
          do
     if getd intern bldmsg("%w%w",'ux,u) then flag(list u,'lose)
   ) where !*lower=nil;


% ***** REMOVE THE FOLLOWING LINE WHEN FLOAT.C/EXTERNALS.SL UPDATED.

REMFLAG('(HYPOT COSH SINH TANH),'LOSE);

% ***** REMOVE THE FOLLOWING LINE WHEN WE KNOW HOW TO HANDLE COMPLEX
%       VALUES FOR ACOS, ASIN.

REMFLAG('(ACOS ASIN),'LOSE);

remflag('(cond),'eval);

endmodule;

end;

