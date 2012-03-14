module paraset;   % Parameter determining module.

% Author: Stanley L. Kameny.

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


% Last change date: 23 June 1993.

% Paraset.red determines the parameters !!nfpd, !!nbfpd, and !!maxbflexp
% for floating point numbers.  !!nfpd, !!nbfpd, and !!maxbflexp are
% computed at load, but !!maxbflexp may have to be fixed up at run time
% if !!flexperr is true.

imports errorp, errorset!*, neq, roundconstants;

exports !!mfefix, find!!nbfpd, find!!nfpd, infinityp;

global '(!!nfpd !!nbfpd !!!~xx !!yy !!maxbflexp !!flexperr !!plumax
         !!epsqrt !!flint !!flbint !!floatbits);

flag('(!!nfpd !!nbfpd !!maxflexp),'share);

symbolic procedure find!!nfpd;
   begin scalar x,y,z;integer i; x:=y:=9.0;
      repeat <<x := 10.0*x+y; i := i+1>> until (z := x+1.0)=x;
% The following line and the corresponding one in find!!nbfpd can call
% FIX on a a value that is around 10^16 (if floats are IEEE 64-bit values) and
% that mighht lead to unreliable conseqences. If arithmetic is IEEE (as maybe
% it mostly will be these days) then the test would fail anyway, so
% in that case commenting it out can not have adverse effects!
%     if 10.0*fix(z/10) - 1.0 neq x then i := i - 1;
      return !!nfpd:=i end;

symbolic procedure find!!nbfpd;
   begin scalar x,y,z;integer i; x:=y:=1.0;
      repeat <<x := 2.0*x+y; i := i+1>> until (z := x+1.0)=x;
%     if 2.0*fix(z/2) - 1.0 neq x then i := i-1;
      return !!nbfpd:=i end;

symbolic procedure find!!maxbflexp;
   begin scalar z; integer n;
      !!!~xx := 1.0;
      while not errorp
       (z := errorset!*(
          '(progn (setq !!yy (plus 1.0 (times !!!~xx 2.0)))
                  (and (not (infinityp !!yy))
                       (greaterp !!yy !!!~xx))),nil))
         and car z do
            <<n := n+1; !!!~xx := !!yy>>;
      !!flexperr := not errorp z and not car z;
      return !!maxbflexp := n end;

symbolic procedure infinityp u;
   % Check for a representation of an IEEE floating point infinity.
   not(x eq '!- or digit x) where x=car explode u;

symbolic procedure !!mfefix;
  <<if !!flexperr then
      begin integer n;
        !!flexperr := !!plumax := nil;
        while errorp errorset!*('(explode !!!~xx),nil) do
          <<!!!~xx := !!!~xx/2.0; n := n+1>>;
        !!maxbflexp := !!maxbflexp - n;
      end;
    if not !!plumax then roundconstants()>>;

find!!nfpd(); find!!nbfpd();
find!!maxbflexp();

!!epsqrt := 10.0**((-1 - !!nfpd)/2);

!!flint := 10.0**!!nfpd;

!!flbint := 2.0 ** !!nbfpd;

!!floatbits := (10*(!!nfpd + 1))/3;  % Smallest power of 2 that does
               % not fit in mantissa.  Note that 10/3 > log(10)/log(2).

endmodule;

end;
