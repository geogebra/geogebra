module sfint;     % Assorted Integral Functions, Ei, Si, Ci, Li etc.
                  % Includes rules and limited rounded mode evaluation
                  % for Ei, Si, si (called s_i here!), Ci, Chi, Shi,
                  % Fresnel_S, Fresnel_C and Erf;
                  % the numerical part to be improved!
%
% Author: Winfried Neun, Jun 1993
% email : neun@ZIB.de

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


% added Li , Winfried Neun, Oct 1998

% For Math References, see e.g. Abramowitz/Stegun, chapter 5 and 7

% Exponential Integral etc.

algebraic operator Fresnel_C, Fresnel_S, erfc,erfi;

% algebraic operator Ci, Si, s_i, shi, chi;
% FJW: Ci, Si also defined in int (driver.red), so ...
symbolic((algebraic operator Ci, Si) where !*msg=nil);
algebraic operator s_i, shi, chi, li;

algebraic let limit(Si(~tt),~tt,infinity) => Pi/2;

algebraic
let { int(sin(~tt)/~tt,~tt,0,~z) => Si (z),
      int(cos(~a*~x)*sin(~b*~x)/x,x) => 1/2*Si(a*x+b*x)-1/2*Si(a*x-b*x),
      int(cos(~a*~x)*sin(~x)/x,x) => 1/2*Si(a*x+x)-1/2*Si(a*x-x),
      int(cos(~x)*sin(~b*~x)/x,x) => 1/2*Si(x+b*x)-1/2*Si(x-b*x),
      int(cos(~x)*sin(~x)/x,x) => 1/2*Si(2*x),
      Si(0) => 0,
      Si(-~x) => (- Si(x)),
      df(Si(~x),~x) => sin(x)/x  ,
      Si(~x) => compute_int_functions(x,Si)
                 when numberp x and lisp !*rounded
    };

algebraic
let { int(sin(~tt)/~tt,~tt,~z,infinity) => - s_i (z),
      limit(s_i(~tt),x,infinity) => 0,
      s_i(~x) => Si(x) - pi/2,
      df(s_i(~x),~x) => sin(x)/x
    };

algebraic
let { int(exp(~tt)/~tt,~tt,-infinity,~z) =>  Ei (z),
      df(Ei(~x),~x) => exp(x)/x,
      Ei(~x) => compute_int_functions(x,Ei)
         when numberp x and abs(x) <= 20 and lisp !*rounded
    };

algebraic
let { int(1/ln(~tt),~tt,0,~z) =>  li (z),
      li (~z) => Ei(log z)
    };

algebraic
let { int(cos(~tt)/~tt,~tt,~z,infinity) => - Ci (z),
      int((cos(~tt) -1)/~tt,~tt,0,~z) => Ci (z) + psi(1) -log(z),
% psi(1) may be replaced by euler_gamma one day ...
      Ci(-~x) => - Ci(x) -i*pi,
      df(Ci(~x),~x) => cos(x)/x,
      Ci(~x) => compute_int_functions(x,Ci)
         when numberp x and abs(x) <= 20 and lisp !*rounded
    };

algebraic
let { int(sinh(~tt)/~tt,~tt,0,~z) => shi (z),
      df(Shi(~x),~x) => sinh(x)/x  ,
      shi(~x) => compute_int_functions(x,shi)
         when numberp x and abs(x) <= 20 and lisp !*rounded
    };

algebraic
let { int((cosh(~tt) -1)/~tt,~tt,0,~z) => Chi (z) + psi(1) -log(z),
% psi(1) may be replaced by euler_gamma one day ...
      df(Chi(~x),~x) => cosh(x)/x  ,
      Chi(~x) => compute_int_functions(x,Chi)
         when numberp x and abs(x) <= 20 and lisp !*rounded
    };

algebraic
let { int(sin(Pi/2*~tt^2),~tt,0,~z) => Fresnel_S (z),
      Fresnel_S(-~x) => (- Fresnel_S (x)),
      Fresnel_S(i* ~x) => (-i*Fresnel_S (x)),
      limit(Fresnel_S(~tt),~tt,infinity) => 1/2,
      df(Fresnel_S(~x),~x) => sin(Pi/2*x^2) ,
      Fresnel_S (~x) => compute_int_functions(x,Fresnel_S)
              when numberp x and abs(x) <= 10 and lisp !*rounded };

algebraic
let { int(cos(Pi/2*~tt^2),~tt,0,~z) => Fresnel_C (z),
      Fresnel_C(-~x) => (- Fresnel_C (x)),
      Fresnel_C(i* ~x) => (i*Fresnel_C (x)),
      limit(Fresnel_C(~tt),~tt,infinity) => 1/2,
      df(Fresnel_C(~x),~x) => cos(Pi/2*x^2) ,
      Fresnel_C (~x) => compute_int_functions(x,Fresnel_C)
               when numberp x and abs(x) <= 10 and lisp !*rounded };

algebraic
let { limit (erf(~x),~x,infinity) => 1,
      limit (erfc(~x),~x,infinity) => 0,
      erfc (~x) => 1-erf(x),
      erfi(~z)  => -i * erf(i*z),
      int(1/e^(~tt^2),~tt,0,~z) => erf(z)/2*sqrt(pi),
      int(1/e^(~tt^2),~tt,~z,infinity) => erfc(z)/2*sqrt(pi),
      erf (~x) => compute_int_functions(x,erf)
                when numberp x and abs(x)<5 and lisp !*rounded };

algebraic procedure compute_int_functions(x,f);
   begin scalar pre,!*uncached,scale,term,n,precis,result,interm;
   pre := precision 0; precision pre;
   precis := 10^(-2 * pre);
   lisp (!*uncached := t);
   if f = Si then
           if x < 0 then result :=
                        - compute_int_functions(-x,f) else
            << n:=1; term := x; result := x;
               while abs(term) > precis do
                 << term := -1 * (term * x*x)/(2n * (2n+1));
                    result := result + term/(2n+1);
                    n := n + 1>>; >>
    else if f = Ci then
           if x < 0 then result :=
                         - compute_int_functions(-x,f) -i*pi else
              << n:=1; term := 1; result := euler!*constant + log(x);
               while abs(term) > precis do
                 << term := -1 * (term * x*x)/((2n-1) * 2n);
                    result := result + term/(2n);
                    n := n + 1>>; >>
    else  if f = Ei then
          << n:=1; term := 1; result := euler!*constant + log(x);
               while abs(term) > precis do
                 << term := (term * x)/n;
                    result := result + term/n;
                    n := n + 1>>; >>
    else  if f = Shi then
            << n:=1; term := x; result := x;
               while abs(term) > precis do
                 << term := (term * x*x)/(2n * (2n+1));
                    result := result + term/(2n+1);
                    n := n + 1>>; >>
    else  if f = Chi then
            << n:=1; term := 1; result := euler!*constant + log(x);
               while abs(term) > precis do
                 << term := (term * x*x)/((2n-1) * 2n);
                    result := result + term/(2n);
                    n := n + 1>>; >>
    else if f = erf then
           if x < 0 then result := - compute_int_functions(-x,f) else
            << n:=1; term := x; result := x;
               if floor(x*7) > pre then precision  floor(x*7);
               interm := -1 *  x*x;
               while abs(term) > precis do
                 << term := (term * interm)/n;
                    result := result + term/(2n+1);
                    n := n + 1>>;
               precision pre;
               result := result*2/sqrt(pi) ;>>
    else  if f = Fresnel_S then
            << if x > 4.0 then precision max(pre,40);
               if x > 6.0 then precision max(pre,80);
               n:=1; term := x^3*pi/2; result := term/3;
                         interm := x^4*(pi/2)^2;
               while abs(term) > precis do
                 << term := -1 * (term * interm)/(2n * (2n+1));
                    result := result + term/(4n+3);
                    n := n + 1>>; >>
    else  if f = Fresnel_C then
            << if x > 4.0 then precision max(pre,40);
               if x > 6.0 then precision max(pre,80);
               n:=1; term := x; result := x; interm := x^4*(pi/2)^2;
               while abs(term) > precis do
                 << term := -1 * (term * interm)/(2n * (2n-1));
                    result := result + term/(4n+1);
                    n := n + 1>>;  >>;
    precision pre;
    return result
  end;

endmodule;

end;
