module sfkummer; % Functions and rules for the Kummer M and U functions.

% Author: Chris Cannam, Sept/Oct 1992.

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


imports complex!*on!*switch, complex!*off!*switch,
   complex!*restore!*switch, sq2bf!*;

exports kummerm!*calc;

% Provides algebraic things for both functions, and numeric for (only)
% the M function.  The amount of non-working code for the U function I
% had to cut out of this before getting this version was a sight to
% behold.

algebraic (operator kummerM, kummerU);

symbolic operator kummerm!*calc;


algebraic (kummer!*rules := {

kummerU(~a,~b,~z)  =>  ( pi / sin (pi * b)) *
      ( (kummerM(a,b,z) / (gamma(1+a-b) * gamma(b))) -
         ((z**(1-b)) * (kummerM(1+a-b,2-b,z)/(gamma(a) * gamma(2-b)))))
   when numberp b and (impart b neq 0 or b neq floor b)
      and numberp a and (impart a neq 0 or a neq floor a or a > 0)
         and not(z=0 and repart(1-b) < 0)
               and ((a-b) neq floor repart (a-b) or (a-b) > -1),

kummerU(~a,~b,~z)  =>  ( pi / sin (pi * b)) *
      ( -((z**(1-b)) * (kummerM(1+a-b,2-b,z)/(gamma(a) * gamma(2-b)))))
   when numberp b and (impart b neq 0 or b neq floor b)
      and not(z=0 and repart(1-b) < 0)
                % ComplexInfinity otherwise, but we can't calculate with
                % CI.
      and numberp a and (impart a neq 0 or a neq floor a or a > 0),



kummerM(~a,~a,~z) => exp(z) when not(fixp a and a <= 0) ,

%% fix by FJW : old form: kummerM(~a,~b,~z)  =>  exp z       when a = b,

kummerM(~a,~b,~z)  =>  ((2 * exp (z/2)) / z) * sinh (z/2)
   when numberp a and numberp b and numberp z
      and a = 1 and b = 2 and impart z = 0 and z neq 0,

kummerM(~a,~b,~z)  =>  ((-2 * i * exp (z/2)) / z) * sin (-z / (2*i))
   when numberp a and numberp b and numberp z
      and a = 1 and b = 2 and repart z = 0 and z neq 0,

kummerM(~a,~b,~z)  =>  infinity
   when numberp a and numberp b
      and impart b = 0 and b < 0 and b = floor b
               and not (impart a = 0 and a < 0 and a = floor a and a >= b),

kummerM(~a,~b,~z)  =>  do!*kummerm(a,b,z)
   when symbolic !*rounded
      and numberp a and numberp b and numberp z
               and b neq 0
                  and impart a = 0 and impart b = 0 and impart z = 0
               and not (repart b = floor repart b
                                          and repart a = floor repart a
                                          and repart a < 0 and repart b < 0
                                          and repart a >= repart b),

%%df(kummerM(~a,~b,~z),z)  =>  (a/b) * kummerM(a+1, b+1, z),

%%df(kummerU(~a,~b,~z),z)  =>  -a * kummerU(a+1,b+1,z)

% AS (13.4.13)
df(KummerM(~a,~b,~z),z)  =>
        1/z*((b-a)*KummerM(a-1,b,z)-(b-a-z)*KummerM(a,b,z)),
% AS (13.4.26)
df(KummerU(~a,~b,~z),z)  =>
        (- KummerU(a-1,b,z) + (a-b+z)*KummerU(a,b,z))/z

})$


algebraic (let kummer!*rules);



algebraic procedure do!*kummerm(a,b,z);
   algebraic sf!*eval('kummerm!*calc, {a,b,z});


algebraic procedure kummerm!*calc(a,b,z);
   begin scalar a0, b0, z0, result, alglist!*;
      integer prepre, precom;
      precom := complex!*off!*switch();
      prepre := precision 0;
      if prepre < !!nfpd then precision (!!nfpd + 1)
      else precision (prepre + 2);
      a0 := a; b0 := b; z0 := z;
      result :=
               algebraic symbolic kummerm!*calc!*sub(a0,b0,z0);
      complex!*restore!*switch(precom);
      precision prepre;
      return result;
   end;


symbolic procedure kummerm!*calc!*sub(a,b,z);
   begin scalar result, this,
               admissable, pAmod, pBmod;
      integer rp, orda, k;
      a := sq2bf!* a; b := sq2bf!* b; z := sq2bf!* z;
      result := bfone!*; k := 1;
      pAmod := timbf(a,z); pBmod := b;
      admissable := divbf(bfone!*,
               i2bf!: (bf!*base**(5 + c!:prec!:())));
      orda := order!: admissable - 5;
      this := bfone!*; rp := c!:prec!:();
      while greaterp!: (abs!: this, admissable) do
               << this := divide!:(times!:(this,pAmod),
                            times!:(pBmod, i2bf!: k),rp);
                  rp := order!: this - orda;
                  result := plus!:(result, this);
                  k := k + 1;
                  pAmod := plus!:(pAmod, z);
                  pBmod := plus!:(pBmod, bfone!*);
               >>;
      return mk!*sq !*f2q mkround result;
   end;

endmodule;

end;



