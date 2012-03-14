module igamma;

% Author : Daniel Hobbs , University of Bath, 1995 - 1996
%
%--------------------------------------------------------------------------
%
%  The incomplete gamma function.
%
%  igamma_iter_series(a,x,iter,sum,last_term) - iteratively computes the
%               value of an approximation to an infinite series used in
%                igamma (for x<=1 or x<a).
%
%  igamma_cont_frac(a,x,iter,iter_max) - iteratively computes the value of
%       the continuous fraction used in igamma (for other values of x).
%
%  igamma_eval(a,x) - returns the value at point x of the
%               incomplete gamma function of order ord.
%
%  The incomplete beta function.
%
%  ibeta_cont_frac(iter,iter_max,a,b,x) - recursively computes
%               the value of the continuous fraction used to
%               approximate to the incomplete beta function.
%
%  ibeta_eval(a,b,x) - returns the value of the incomplete beta
%               function with
%               parameters a and b at point x, by approximating to the
%               incomplete beta function using a continued fraction.
%
%--------------------------------------------------------------------------

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


% --------------------------- global variables ----------------------------

fluid '(sfiterations);

global '(ibeta_max_iter);

algebraic <<

operator igamma , igamma_eval, ibeta, ibeta_eval;

% Set the maximum number of iterations for the continued fraction
% used in ibeta to be 200.

ibeta_max_iter := 200;

% Set up rule definitions for igamma and ibeta functions.

let
{
 igamma(~a,~x) => igamma_eval(a,x)
        when numberp(a) and numberp(x) and a>0 and x>=0 and lisp !*rounded
};

let
{
 ibeta(~a,~b,~x) => ibeta_eval(a,b,x)
        when numberp(a) and numberp(b) and numberp(x) and lisp !*rounded
             and repart(a)>0 and repart(b)>0 and x>=0 and x<=1
};


% Function igamma_iter_series:     --  cum_gamma_iter        x^i
%                                  \                       -------------
%                                  /                       (a+1)...(a+i)
%                                  --  i=1
% Uses Battacharjee's method (1970) (computed recursively).

expr procedure igamma_iter_series(a,x,iter,sum,last_term);
begin
 scalar value,this_term;

 if (last_term < 10^-(precision(0)+3)) then
  value := sum
 else
 <<
  this_term := (last_term * x / (a+iter));
  value := igamma_iter_series(a,x,iter+1,sum+this_term,this_term)
 >>;

 return value;
end;


% Function igamma_cont_frac:            1   1-a   1   2-a   2
%                                      ---  ---  ---  ---  ---  ...
%                                        x +  1 +  x +  1 +  x +
% Recursively computes fraction using
% Abramowitz and Stegun's method (1964).

expr procedure igamma_cont_frac(a,x,iter,iter_max);
begin
 scalar value;

 if (iter>iter_max) then
  value := 0
 else
  value := (iter - a)/
              (1 +      (iter/
                    (x + igamma_cont_frac(a,x,iter + 1,iter_max))));

 return value;
end;


% Function igamma_eval: returns the value at point x of the
% incomplete gamma function with order ord.

expr procedure igamma_eval(a,x);
begin
 scalar arg,frac,last_frac,acc,value;

 % Decide whether to use a series expansion or a continued fraction.
 if (x<=1 or x<a+2) then
 <<
  value := (exp(-x) * x^a) * (1 + igamma_iter_series(a,x,1,0,1)) /
             gamma(a + 1)
 >>
 else
 <<
  % Set required accuracy to be 3 decimal places more than
  % current precision.
  acc := 10 ^ -(precision(0)+3);
  % Obtain a starting value.
  frac := igamma_cont_frac(a,x,1,1);
  sfiterations := 1;
  % Repeat loop until successive results of continued fraction converge.
  repeat
  <<
   sfiterations := sfiterations + 1;
   last_frac := frac;
   frac := igamma_cont_frac(a,x,1,sfiterations)
  >>
  until (last_frac - frac) < acc;

  arg := exp(-x) * x^a / gamma(a);
  value := 1 - arg / (x + frac)
 >>;

 return value;
end;


% Function ibeta_cont_frac: calculates  1   c(2)  c(3)
%                                      ---  ----  ----  ...
%                                      1 +  1  +  1  +
% where
%        c(2i) =  - (a + i - 1) (b - i)   *   x
%                ---------------------------------
%                (a + 2i - 2) (a + 2i - 1) (1 - x)
% and
%      c(2i+1) =  i (a + b + i - 1)   *   x
%                -----------------------------
%                (a + 2i - 1) (a + 2i) (1 - x)

expr procedure ibeta_cont_frac(iter,iter_max,a,b,x);
begin
 scalar value,c_odd,c_even;

 if not (fixp(iter) and fixp(iter_max) and numberp(x)) then
  rederr("ibeta_cont_frac called illegally");

 if (iter>iter_max) then
  value := 0
 else
 <<
  c_even := -(a+iter-1)*(b-iter)*x / ((a+2*iter-2)*(a+2*iter-1)*(1-x));
  c_odd := iter*(a+b+iter-1)*x / ((a+2*iter-1)*(a+2*iter)*(1-x));
  value := c_even /
               (1 + (c_odd /
                       (1 + ibeta_cont_frac(iter+1,iter_max,a,b,x))))
 >>;

 return value;
end;


% Function ibeta_eval: returns the value of the incomplete beta%
% function with parameters a and b at point x. Method due to Muller (1931).

expr procedure ibeta_eval(a,b,x);
begin
 scalar last_value,value,arg,sfiterations;

 if (x=0 or x=1) then
  value := x
 else
 <<
  %
  if (repart(a+b)-2)*x > (repart(a)-1) then
   value := 1 - ibeta(b,a,1-x)
  else
  <<
   arg := gamma(a+b) * x^a * (1-x)^(b-1) / (a * gamma(a) * gamma(b));
   % A starting point of 30 levels of continued fraction.
   sfiterations := 30;
   % Starting value that will force calculation a second time at least.
   value := -1;
   repeat
   <<
    last_value := value;
    value := arg * (1/(1 + ibeta_cont_frac(1,sfiterations,a,b,x)));
    sfiterations := sfiterations + 10
   >>
   until (abs(value - last_value) < 10^-(precision(0)+3))
    or sfiterations > ibeta_max_iter;
  >>
 >>;

 % Error condition should not occur, but in case it does...
 if sfiterations > ibeta_max_iter then
 write
 "*** Warning: max iteration limit exceeded; result may not be accurate";

 return value;
end;

>>;

endmodule;

end;

