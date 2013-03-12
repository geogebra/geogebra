module solvetab;   % Simplification rules for SOLVE.

% Author: David R. Stoutemyer.

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


% Modifications by: Anthony C. Hearn, Donald R. Morrison, Rainer
%                   Schoepf, Herbert Melenk and Winfried Neun.

put('asin, 'inverse, 'sin);

put('acos, 'inverse, 'cos);

put('atan,'inverse,'tan);

put('acot,'inverse,'cot);

put('asec,'inverse,'sec);

put('acsc,'inverse,'csc);

algebraic;

Comment Rules for reducing the number of distinct kernels in an
   equation;

operator sol;

% for all a,b,c,d,x such that ratnump c and ratnump d let
%    sol(a**c-b**d, x) = a**(c*lcm(c,d)) - b**(d*lcm(c,d));

for all a,b,c,d,x such that not fixp c and ratnump c and
                            not fixp d and ratnump d let
   sol(a**c-b**d, x) = a**(c*lcm(den c,den d))
                     - b**(d*lcm(den c,den d));

for all a,b,c,d,x such that a freeof x and c freeof x let
   sol(a**b-c**d, x) = e**(b*log a - d*log c);

for all a,b,c,d,x such that a freeof x and c freeof x let
   sol(a*log b + c*log d, x) = b**a*d**c - 1;
%% sol(a*log b - c*log d, x) = b**a - d**c


for all a,b,c,d,f,x such that a freeof x and c freeof x let
   sol(a*log b + c*log d + f, x) = sol(log(b**a*d**c) + f, x);
%% sol(a*log b + c*log d - f, x) = sol(log(b**a*d**c) - f, x),
%% sol(a*log b - c*log d + f, x) = sol(log(b**a/d**c) + f, x),
%% sol(a*log b - c*log d - f, x) = sol(log(b**a/d**c) - f, x)


for all a,b,d,f,x such that a freeof x let
   sol(a*log b + log d + f, x) = sol(log(b**a*d) + f, x),
%% sol(a*log b + log d - f, x) = sol(log(b**a*d) - f, x),
   sol(a*log b - log d + f, x) = sol(log(b**a/d) + f, x);
%% sol(a*log b - log d - f, x) = sol(log(b**a/d) - f, x),
%% sol(log d - a*log b + f, x) = sol(log(d/b**a) + f, x),
%% sol(log d - a*log b - f, x) = sol(log(d/b**a) - f, x)


%%%%for all a,b,c,d,x such that a freeof x and c freeof x let
%%%%   sol(a*log b + c*log d, x) = b**a*d**c - 1,
%%%%   sol(a*log b - c*log d, x) = b**a - d**c;

for all a,b,d,x such that a freeof x let
   sol(a*log b + log d, x) = b**a*d - 1,
   sol(a*log b - log d, x) = b**a - d;
%% sol(log d - a*log b, x) = d - b**a;

for all a,b,c,x let
   sol(log a + log b + c, x) = sol(log(a*b) + c, x),
   sol(log a - log b + c, x) = sol(log(a/b) + c, x);
%% sol(log a + log b - c, x) = sol(log(a*b) - c, x),
%% sol(log a - log b - c, x) = sol(log(a/b) - c, x)


for all a,c,x such that c freeof x let
   sol(log a + c, x) = a - e**(-c);
%% sol(log a - c, x) = a - e**c;

for all a,b,x let
   sol(log a + log b, x) = a*b - 1,
   sol(log a - log b, x) = a - b,
%  sol(cos a - sin b, x) = sol(cos a - cos(pi/2-b), x),
%  sol(sin a + cos b, x) = sol(sin a - sin(b-pi/2), x),
%  sol(sin a - cos b, x) = sol(sin a - sin(pi/2-b), x),
   sol(sin a + sin b, x) = if !*allbranch then sin((a+b)/2)*
       cos((a-b)/2) else a+b,
   sol(sin a - sin b, x) = if !*allbranch then sin((a-b)/2)*
       cos((a+b)/2)  else a-b,
   sol(cos a + cos b, x) = cos((a+b)/2)*cos((a-b)/2),
   sol(cos a - cos b, x) = if !*allbranch then sin((a+b)/2)*
       sin((a-b)/2)  else a-b,
   sol(asin a - asin b, x) = a-b,
   sol(asin a + asin b, x) = a+b,
   sol(acos a - acos b, x) = a-b,
   sol(acos a + acos b, x) = a-b;

% Exponential equations
let {
   sol(~~b*~a^~x-~~d*~c^(~f*~x),~x) => b/d-(c^f/a)^x
      when a freeof x and b freeof x and c freeof x
         and d freeof x and f freeof x,
   sol(~~b*~a^~x-~~d*~c^~x,~x) => b/d-(c/a)^x
      when a freeof x and b freeof x and c freeof x and d freeof x,
   sol(~~b*~a^~x-~~d*~c^~x*~g,~x) => b/d*(a/c)^x-g
      when a freeof x and b freeof x and c freeof x and d freeof x,
   sol(~~b*~a^~x-~c^~x*~g,~x) => b*(a/c)^x-g
      when a freeof x and b freeof x and c freeof x
};

solve_trig_rules := {sin(~x + ~y) => sin x * cos y + cos x * sin y,
                     sin(~x - ~y) => sin x * cos y - cos x * sin y,
                     cos(~x + ~y) => cos x * cos y - sin x * sin y,
                     cos(~x - ~y) => cos x * cos y + sin x * sin y};

fluid '(solve_invtrig_soln!*);

share solve_invtrig_soln!*;

clear solve_invtrig_soln!*;

invtrig_solve_rules := {
   sol(asin(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,asin(x) + y,z),
   sol(acos(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,acos(x) + y,z),
   sol(atan(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('tan,atan(x) + y,z),
   sol(acos(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,acos(x) + y,z),
   sol(atan(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,atan(x) + y,z),
   sol(asin(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,asin(x) + y,z),
   sol(atan(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,atan(x) + y,z),
   sol(~n*asin(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,n*asin(x) + y,z),
   sol(~n*acos(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,n*acos(x) + y,z),
   sol(~n*acos(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,n*acos(x) + y,z),
   sol(~n*atan(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('sin,n*atan(x) + y,z),
   sol(~n*asin(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,n*asin(x) + y,z),
   sol(~n*atan(~x) + ~y,~z) => solve_invtrig_soln!*
                 when check_solve_inv_trig('cos,n*atan(x) + y,z)
};

let invtrig_solve_rules;

% The following rules allow REDUCE to solve some classes of equations
% where a variable appears inside and outside a log or an exponential.
% The results are based on Lambert's W (Omega) function which is fully
% supported in the specfn package. The ruleset has one central rule
% which produces the Omega function expression in the simplest (rather
% special) form, while the more general cases are mapped towards this
% rule by reforming the equation algebraically or by variable
% transformations.

lambert_rules := {
          % Basic solution of x=log(c*x/d)

    sol(~x + log(~~c*~x/~~d),~x) => x - lambert_w(d/c)
         when c freeof x and d freeof x,


          % General forms transformed to simpler ones.

    sol(~~a*~x^~n + ~~b*log(~~c*x),x)
             => x - e^(-1/n * lambert_w((n*a)/(b*c^n)))/c
         when (a neq 1 or b neq 1) and
          a freeof x and b freeof x and c freeof x
          and fixp n and n > 1,

    sol(~~a*~x + ~~b*log(~c) + ~w,x)
             => sol(a*x + b*log(c*e^(w/b)), x)
         when a freeof x and b freeof x and w freeof x
            and not(c freeof x),

    sol(~~a*~x + ~~b*log(~~c*x/~~d),x)
             => sub(x=a*x/b, sol(x + log(c*b*x/(a*d)),x))
         when (a neq 1 or b neq 1) and
          a freeof x and b freeof x and c freeof x and d freeof x,

    sol(~~a*~x + ~~b*log((~~c*x + ~u)/~~d),x)
             => sub(x=x+u/c, sol(num(a*(x-u/c) + b*log(c*x/d)),x))
         when
          a freeof x and b freeof x and c freeof x
             and d freeof x and u freeof x,

    sol(~~a*~x + ~~b*log((~~c*x^~n)/~~d),x)
              =>
          sol(num(a*x + n * b*log(newroot_of_unity n* x) + 1/n*log(c/d)),x)
             when
              a freeof x and b freeof x and c freeof x
                 and d freeof x and fixp n and n > 1,

    sol(~~a*~x + ~~b*log((~~c*x^~n)/~~d),x)
             =>
               sol(num(a*x + n*b*log x + 1/n*log(c/d)),x)
         when
          a freeof x and b freeof x and c freeof x
             and d freeof x and n freeof x,

    sol(~~a*~x^~~n + ~~b*e^(~~c*~x/~~d),x)
            =>
    sol(num(log(a) + n * log(newroot_of_unity n *x) - (log(-b)*d + c*x)/d), x)
         when
          a freeof x and b freeof x and c freeof x
             and d freeof x and fixp n and n > 1,

    sol(~~a*~x^~~n + ~~b*e^(~~c*~x/~~d),x)
            => sol(num(log(a) + n*log(x) - (log(-b)*d + c*x)/d), x)
         when
          a freeof x and b freeof x and c freeof x
             and d freeof x and n freeof x,

    sol(~~a*~x + ~~b*e^(~~c*~x/~~d) + ~f,x)
        => sub(x=a*x+f/a,sol(num(x + b*e^(-c*f/(a*d))*e^(c*x/(a*d))),x))
         when
          a freeof x and b freeof x and c freeof x
             and d freeof x and f freeof x
}$

% let lambert_rules;

symbolic procedure lambertp(e1,x);
   <<x; smemq('log,e1) or smemq('expt,e1)>>;

symbolic;

fluid '(sol!-rulesets!*);

sol!-rulesets!*:={{'lambertp,'lambert_rules}};

symbolic procedure solve!-apply!-rules(e1,var);
  begin scalar rules,u;
    u:=list('sol,mk!*sq(e1 ./ 1), var);
    for each r in sol!-rulesets!* do
      if apply(car r,{e1,var}) then rules := cadr r . rules;
    if null rules then return simp!* u;
    load!-package 'odesolve; % for the roots_of_unity
    return car evalletsub2({rules,{'simp!*, mkquote u}},nil);
  end;

endmodule;

end;
