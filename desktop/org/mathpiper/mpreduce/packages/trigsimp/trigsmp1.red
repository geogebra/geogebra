module trigsmp1$  % Collection of rule sets.

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


% Revised by FJW, 22 June 1998

algebraic$

clearrules(trig_imag_rules)$            % FJW: pre-defined

%% trig_normalize!* :=
%% {
%%   cos(~a)^2  => 1 - sin(a)^2  when trig_preference=sin,
%%   sin(~a)^2  => 1 - cos(a)^2  when trig_preference=cos,
%%   cosh(~a)^2 => 1 + sinh(a)^2 when hyp_preference=sinh,
%%   sinh(~a)^2 => cosh(a)^2 - 1 when hyp_preference=cosh
%% }$

trig_normalize2sin!*  := {cos(~a)^2  => 1 - sin(a)^2}$ % FJW
trig_normalize2cos!*  := {sin(~a)^2  => 1 - cos(a)^2}$ % FJW
trig_normalize2sinh!* := {cosh(~a)^2 => 1 + sinh(a)^2}$ % FJW
trig_normalize2cosh!* := {sinh(~a)^2 => cosh(a)^2 - 1}$ % FJW


trig_expand_addition!* :=               % additions theorems
{
  sin((~a+~b)/~~m)
     => sin(a/m)*cos(b/m) + cos(a/m)*sin(b/m),

  cos((~a+~b)/~~m)
     => cos(a/m)*cos(b/m) - sin(a/m)*sin(b/m),

  tan((~a+~b)/~~m)
     => (tan(a/m)+tan(b/m))/(1-tan(a/m)*tan(b/m)),

  cot((~a+~b)/~~m)
     => (cot(a/m)*cot(b/m)-1)/(cot(a/m)+cot(b/m)),

  sec((~a+~b)/~~m)
     => 1/(1/(sec(a/m)*sec(b/m))-1/(csc(a/m)*csc(b/m))),

  csc((~a+~b)/~~m)
     => 1/(1/(sec(b/m)*csc(a/m))+1/(sec(a/m)*csc(b/m))),

  tanh((~a+~b)/~~m)
     => (tanh(a/m)+tanh(b/m))/(1+tanh(a/m)*tanh(b/m)),

  coth((~a+~b)/~~m)
     => (coth(a/m)*coth(b/m)+1)/(coth(a/m)+coth(b/m)),

  sinh((~a+~b)/~~m)
     => sinh(a/m)*cosh(b/m) + cosh(a/m)*sinh(b/m),

  cosh((~a+~b)/~~m)
     => cosh(a/m)*cosh(b/m) + sinh(a/m)*sinh(b/m),

  sech((~a+~b)/~~m)
     => 1/(1/(sech(a/m)*sech(b/m))+1/(csch(a/m)*csch(b/m))),

  csch((~a+~b)/~~m)
     => 1/(1/(sech(a/m)*csch(b/m))+1/(sech(b/m)*csch(a/m)))
}$


trig_expand_multiplication!* :=         % multiplication theorems
{
  sin(~n*~a/~~m)
     => sin(a/m)*cos((n-1)*a/m) + cos(a/m)*sin((n-1)*a/m)
          when fixp n and n>1 and n<=15,

  sin(~n*~a/~~m)
     => 2*sin(n/2*a/m)*cos(n/2*a/m)
          when fixp n and mod(n,2)=0 and n>15,

  sin(~n*~a/~~m)
     => sin((n-1)/2*a/m)*cos((n+1)/2*a/m) +
        sin((n+1)/2*a/m)*cos((n-1)/2*a/m)
          when fixp n and mod(n,2)=1 and n>15,

  cos(~n*~a/~~m)
     => cos(a/m)*cos((n-1)*a/m) - sin(a/m)*sin((n-1)*a/m)
          when fixp n and n>1 and n<=15,

  cos(~n*~a/~~m)
      => 2*cos(n/2*a/m)**2-1
          when fixp n and mod(n,2)=0 and n>15,

  cos(~n*~a/~~m)
     => cos((n-1)/2*a/m)*cos((n+1)/2*a/m) -
        sin((n-1)/2*a/m)*sin((n+1)/2*a/m)
          when fixp n and mod(n,2)=1 and n>15,

  sinh(~n*~a/~~m)
     => sinh(a/m)*cosh((n-1)*a/m)+cosh(a/m)*sinh((n-1)*a/m)
          when fixp n and n<=15 and n>1,

  sinh(~n*~a/~~m)
     => 2*sinh(n/2*a/m)*cosh(n/2*a/m)
          when fixp n and mod(n,2)=0 and n>15,

  sinh(~n*~a/~~m)
     => sinh((n-1)/2*a/m)*cosh((n+1)/2*a/m) +
        sinh((n+1)/2*a/m)*cosh((n-1)/2*a/m)
          when fixp n and mod(n,2)=1 and n>15,

  cosh(~n*~a/~~m)
     => cosh(a/m)*cosh((n-1)*a/m) + sinh(a/m)*sinh((n-1)*a/m)
          when fixp n and n>1 and n<=15,

  cosh(~n*~a/~~m)
      => 2*cosh(n/2*a/m)**2-1
          when fixp n and mod(n,2)=0 and n>15,

  cosh(~n*~a/~~m)
     => cosh((n-1)/2*a/m)*cosh((n+1)/2*a/m)+
        sinh((n-1)/2*a/m)*sinh((n+1)/2*a/m)
          when fixp n and mod(n,2)=1 and n>15,

  tan(~n*~a/~~m)
     => (tan(a/m)+tan((n-1)*a/m))/(1-tan(a/m)*tan((n-1)*a/m))
          when fixp n and n>1 and n<=15,

  tan(~n*~a/~~m)
      => 2*tan(n/2*a/m)/(1-tan(n/2*a/m)**2)
          when fixp n and mod(n,2)=0 and n>15,

  tan(~n*~a/~~m)
     => ( tan((n-1)/2*a/m)+tan((n+1)/2*a/m) ) /
        (1-tan((n-1)/2*a/m)*tan((n+1)/2*a/m))
          when fixp n and mod(n,2)=1 and n>15,

  tanh(~n*~a/~~m)
     => (tanh(a/m)+tanh((n-1)*a/m))/(1+tanh(a/m)*tanh((n-1)*a/m))
          when fixp n and n>1 and n<=15,

  tanh(~n*~a/~~m)
      => 2*tanh(n/2*a/m)/(1+tanh(n/2*a/m)**2)
          when fixp n and mod(n,2)=0 and n>15,

  tanh(~n*~a/~~m)
     => ( tanh((n-1)/2*a/m)+tanh((n+1)/2*a/m) ) /
        (1+tanh((n-1)/2*a/m)*tanh((n+1)/2*a/m))
          when fixp n and mod(n,2)=1 and n>15,

  cot(~n*~a/~~m)
     => (cot(a/m)*cot((n-1)*a/m)-1)/(cot(a/m)+cot((n-1)*a/m))
          when fixp n and n>1 and n<=15,

  cot(~n*~a/~~m)
      => (cot(n/2*a/m)**2-1)/(2cot(n/2*a/m))
          when fixp n and mod(n,2)=0 and n>15,

  cot(~n*~a/~~m)
     => ( cot((n-1)/2*a/m)*cot((n+1)/2*a/m)-1 ) /
        (cot((n-1)/2*a/m)+cot((n+1)/2*a/m))
          when fixp n and mod(n,2)=1 and n>15,

  coth(~n*~a/~~m)
     => (coth(a/m)*coth((n-1)*a/m)+1)/(coth(a/m)+coth((n-1)*a/m))
          when fixp n and n>1 and n<=15,

  coth(~n*~a/~~m)
      => (coth(n/2*a/m)**2+1)/(2coth(n/2*a/m))
          when fixp n and mod(n,2)=0 and n>15,

  coth(~n*~a/~~m)
     => ( coth((n-1)/2*a/m)*coth((n+1)/2*a/m)+1 ) /
        (coth((n-1)/2*a/m)+coth((n+1)/2*a/m))
          when fixp n and mod(n,2)=1 and n>15,

  sec(~n*~a/~~m)
     => 1/(1/(sec(a/m)*sec((n-1)*a/m))-1/(csc(a/m)*csc((n-1)*a/m)))
          when fixp n and n>1 and n<=15,

  sec(~n*~a/~~m)
     =>1/(1/sec(n/2*a/m)**2-1/csc(n/2*a/m)**2)
          when fixp n and mod(n,2)=0 and n>15,

  sec(~n*~a/~~m)
     => 1/(1/(sec((n-1)/2*a/m)*sec((n+1)/2*a/m))-
        1/(csc((n-1)/2*a/m)*csc((n+1)/2*a/m)))
          when fixp n and mod(n,2)=1 and n>15,

  csc(~n*~a/~~m)
     => 1/(1/(sec(a/m)*csc((n-1)*a/m))+1/(csc(a/m)*sec((n-1)*a/m)))
          when fixp n and n>1 and n<=15,

  csc(~n*~a/~~m)
     => sec(n/2*a/m)*csc(n/2*a/m)/2
          when fixp n and mod(n,2)=0,

  csc(~n*~a/~~m)
     => 1/(1/(sec((n-1)/2*a/m)*csc((n+1)/2*a/m))+
        1/(csc((n-1)/2*a/m)*sec((n+1)/2*a/m)))
          when fixp n and mod(n,2)=1 and n>15,

  sech(~n*~a/~~m)
     => 1/(1/(sech(a/m)*sech((n-1)*a/m))+1/(csch(a/m)*csch((n-1)*a/m)))
          when fixp n and n>1 and n<=15,

  sech(~n*~a/~~m)
     => 1/(1/sech(n/2*a/m)**2+1/csch(n/2*a/m)**2)
          when fixp n and mod(n,2)=0 and n>15,

  sech(~n*~a/~~m)
     => 1/(1/(sech((n-1)/2*a/m)*sech((n+1)/2*a/m))+
        1/(csch((n-1)/2*a/m)*csch((n+1)/2*a/m)))
          when fixp n and mod(n,2)=1 and n>15,

  csch(~n*~a/~~m)
     => 1/(1/(sech(a/m)*csch((n-1)*a/m))+1/(csch(a/m)*sech((n-1)*a/m)))
          when fixp n and n>1 and n<=15,

  csch(~n*~a/~~m)
     => sech(n/2*a/m)*csch(n/2*a/m)/2
          when fixp n and mod(n,2)=0 and n>15,

  csch(~n*~a/~~m)
     => 1/(1/(sech((n-1)/2*a/m)*csch((n+1)/2*a/m))+
        1/(csch((n-1)/2*a/m)*sech((n+1)/2*a/m)))
          when fixp n and mod(n,2)=1 and n>15
}$

trig_combine!* :=
{
  sin(~a)*sin(~b) => 1/2*(cos(a-b) - cos(a+b)),
  cos(~a)*cos(~b) => 1/2*(cos(a-b) + cos(a+b)),
  sin(~a)*cos(~b) => 1/2*(sin(a-b) + sin(a+b)),
  sin(~a)^2       => 1/2*(1-cos(2*a)),
  cos(~a)^2       => 1/2*(1+cos(2*a)),

  sinh(~a)*sinh(~b) => 1/2*(cosh(a+b) - cosh(a-b)),
  cosh(~a)*cosh(~b) => 1/2*(cosh(a-b) + cosh(a+b)),
  sinh(~a)*cosh(~b) => 1/2*(sinh(a-b) + sinh(a+b)),
  sinh(~a)^2        => 1/2*(cosh(2*a)-1),
  cosh(~a)^2        => 1/2*(1+cosh(2*a))
}$

trig_standardize!* :=
{
  tan(~a)  => sin(a)/cos(a),
  cot(~a)  => cos(a)/sin(a),
  tanh(~a) => sinh(a)/cosh(a),
  coth(~a) => cosh(a)/sinh(a),
  sec(~a)  => 1/cos(a),
  csc(~a)  => 1/sin(a),
  sech(~a) => 1/cosh(a),
  csch(~a) => 1/sinh(a)
}$

trig2exp!* :=
{
  cos(~a)  => (e^(i*a) + e^(-i*a))/2,
  sin(~a)  => -i*(e^(i*a) - e^(-i*a))/2,
  cosh(~a) => (e^(a) + e^(-a))/2,
  sinh(~a) => (e^(a) - e^(-a))/2
}$

pow2quot!* := { (~a/~b)^~c => (a^c)/(b^c) }$
exp2trig1!* := { e**(~x) => cos(x/i)+i*sin(x/i) }$
exp2trig2!* := { e**(~x) => 1/(cos(x/i)-i*sin(x/i)) }$

trig2hyp!* :=
{
  sin(~a)  => -i*sinh(i*a),
  cos(~a)  => cosh(i*a),
  tan(~a)  => -i*tanh(i*a),
  cot(~a)  => i*coth(i*a),
  sec(~a)  => sech(i*a),
  csc(~a)  => i*csch(i*a),
  asin(~a) => -i*asinh(i*a),
  acos(~a) => -i*acosh(a)
}$

hyp2trig!* :=
{
  sinh(~a)  => -i*sin(i*a),
  cosh(~a)  => cos(i*a),
  asinh(~a) => i*asin(-i*a),
  acosh(~a) => i*acos(a)
}$

subtan!* :=
{
  sin(~x)  => cos(x)*tan(x)   when trig_preference=cos,
  cos(~x)  => sin(x)/tan(x)   when trig_preference=sin,
  sinh(~x) => cosh(x)*tanh(x) when hyp_preference=cosh,
  cosh(~x) => sinh(x)/tanh(x) when hyp_preference=sinh
}$

endmodule$

end$

% FJW: For debugging using the rtrace package:

trrlid trig_normalize2sin!*, trig_normalize2cos!*,
   trig_normalize2sinh!*, trig_normalize2cosh!*,
   trig_expand_addition!*, trig_expand_multiplication!*,
   trig_combine!*, trig_standardize!*,
   trig2exp!*, exp2trig1!*, exp2trig2!*,
   trig2hyp!*, hyp2trig!*, subtan!*;
