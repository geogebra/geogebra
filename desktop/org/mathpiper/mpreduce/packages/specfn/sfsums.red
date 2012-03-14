module sfsums;   % Calculation of infinite sums of reciprocal
                 % Powers, see e.g. Abramowitz/Stegun ch 23.
%
% Author: Winfried Neun, Sep 1993

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


algebraic <<

 let{
  sum((-1)^~k /(2*(~k)-1)^~n,~k,1,infinity) =>
        Pi^n*abs(Euler(n-1))/(factorial(n-1) * 2^(n+1))
        when fixp n and n > 0 and not evenp n,

  sum((-1)^~k /(2*(~k)-1)^2,~k,1,infinity) =>  - Catalan,

  sum((-1)^~k /(2*(~k)+1)^2,~k,0,infinity) =>  Catalan,

  sum(1/(2*(~k)-1)^~n,~k,1,infinity) => Zeta(n) *(1-2^(-n))
        when fixp n and n > 0 and evenp n,

  sum(1/~k^~s,~k,1,infinity) =>  Zeta(s),

  sum((-1)^~k/~k^~n,~k,1,infinity) =>  Zeta(n)* (1-2^(1-n))
        when fixp n and n > 0 and evenp n

} ;

% from  Abigail Leeves    Sep 15, 97

let {

prod((1+(1/~n**2)),~n,~r,infinity) =>
(((sinh(pi))/pi)/(prod((1+(1/~n**2)),~n,1,(~r-1))))
when (fixp r and r>=1 and r<15),

prod((1+(1/~n**3)),~n,~r,infinity) =>
(((cosh((sqrt(3)*pi)/2))/pi)/(prod((1+(1/~n**3)),~n,1,(~r-1))))
when (fixp r and r>=1 and r< 15),

prod((1+(1/~n**4)),~n,~r,infinity) =>
(((cosh(sqrt(2)*pi))-(cos(sqrt(2)*pi)))/(2*pi**2))/
        (prod((1+(1/~n**4)),~n,1,(~r-1)))
when (fixp r and r>=1 and r<15),

prod((1+(1/~n**5)),~n,~r,infinity) =>
((((Gamma(exp((2*pi*i)/5)))*(Gamma(exp((6*pi*i)/5))))**-2)/
        (prod((1+(1/~n**5)),~n,1,(~r-1))))
when (fixp r and r>=1 and r<15),

prod((1-(4/~n**2)),~n,~r,infinity) =>
(1/6)/(prod((1-(4/~n**2)),~n,3,(~r-1)))
when (fixp r and r<15 and r>=3),

prod((1-(8/~n**3)),~n,~r,infinity) =>
((sinh(sqrt(3)*pi))/(42*sqrt(3)*pi))/(prod((1-(8/~n**3)),~n,3,(~r-1)))
when (fixp r and r<15 and r>=3),

prod((1-(16/~n**4)),~n,~r,infinity) =>
((sinh(2*pi))/(120*pi))/(prod((1-(16/~n**4)),~n,3,(~r-1)))
when (fixp r and r<15 and r>=3),

prod((1-(32/~n**5)),~n,~r,infinity) =>
((1/1240)*((Gamma(2*exp((pi*i)/5)))*
(Gamma(2*exp((7*pi*i)/5)))**-2))/(prod((1-(32/~n**5)),~n,3,(~r-1)))
when (fixp r and r<15 and r>=3)

};

>>;

endmodule;

end;




