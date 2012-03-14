module dilog;

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


% Dilogarithm Integral and Polylogarithm function
% Lerch Phi

% Collected (most items) from Abramowitz-Stegun (27.7)
% by Winfried Neun , ZIB Berlin

% Lerch Phi from Wolfram's book

algebraic <<
operator fps;
operator Lerch_phi;
operator polylog;

let { fps(dilog ~x,~x,1) => infsum((-1)^k*(x-1)^k/k^2,k,1,infinity)};
let { df(dilog(~x),~x) => - LOG(X)/(x-1)};
let { int(log(~tt)/(~tt-1),~tt,1,~x) => -dilog x };
let { Lerch_phi(~z,~s,0) => polylog(s,z) };
let { Lerch_phi(1,~s,0) => zeta(s) };

let { dilog(exp(-~t)) => - dilog(exp t) - t^2/2,
      dilog(1/e^(~t)) => - dilog(exp t) - t^2/2,
      dilog(-~x+1) => - dilog(x) -log x * log (1-x) + pi^2/6
                        when numberp x and geq(x,0) and geq(1,x),
      dilog(~x)   => - dilog(1-x) - log (x) * log(1-x) + pi^2/6
                        when numberp x and (x > 0) and geq(1,x)
                        and not fixp(1/x),
      dilog(1/~x) => - dilog(x) -(log x)^2/2
                        when numberp x and geq(x,0),
      dilog(~x) =>   dilog(x-1) - log (x - 1) *
                        log (x)-pi^2/12-dilog( (x-1)^2)/2
                        when numberp x and geq(x,1) and geq(2,x)
                        and not (x = 0) and not fixp(1/x),
      dilog(~x) => compute_dilog(x)
                 when numberp x and lisp !*rounded and x>=0,
      dilog 2 => -pi^2/12,
      dilog 1 => 0,
      dilog 0 => pi^2/6,
      dilog(-1) => pi^2/4-i*pi*log(2)
};

%let { fps(polylog(~s,~x),~x,0) => infsum((-1)^k*x^k/k^s,k,1,infinity)};

let { polylog(1,~z) => -log(1-z),
      polylog(~n,~z) => z*df(polylog(n+1,z),z) when fixp n and n<=0,
      polylog(1,1/2) => log(2),
      polylog(2,-1) => -pi^2/12,
      polylog(2,0) => 0,
      polylog(2,1/2) => (pi^2 - 6*log(2)^2)/12,
      polylog(2,1) => pi^2/6,
      polylog(2,2) => pi^2/4-i*pi*log(2),
      polylog(3,1/2) => (4*log(2)^3 - 2*pi^2*log(2) + 21*zeta(3))/24,
      polylog(~s,1) => zeta(s),
      df(polylog(~n,~z),~z) => polylog(n-1,z)/z when fixp n and n>1
};

let { Lerch_Phi (~z,~s,~a) => compute_lerch_phi(z,s,a)
              when lisp !*rounded and numberp z and abs(z)<1
                     and numberp s and numberp a,
      polylog(~n,~z) =>  compute_lerch_phi(z,n,0)
              when lisp !*rounded and numberp z and abs(z)<1 and numberp n };

procedure compute_dilog(x);
   if x = 0.0 then  pi^2/6
    else if x = 1.0 then  0
    else if x = 2.0 then  -pi^2/12
    else if (x >= 1.9 and x < 2.0) then
                 compute_dilog(1-(x-1)^2)/2 - compute_dilog(1-(x-1))
    else if (x > 1.9 or x < -1.0) then
                -(log x)^2/2 -compute_dilog(1/x)
    else if (x < 0.5 and x > 0.0)
                 then -log(1-x)*log(x) + pi^2/6 - compute_dilog(1-x)
    else if (x > 0.5 and x < 1.0 )
                then  -(log x)^2/2 -compute_dilog(1/x)
    else begin scalar !*uncached,yy,summa,ii,term,altern ,xm1,xm1!^ii;
                !*uncached :=t;
                yy := 10^-(lisp !:prec!:);
                summa := 0; xm1 := x-1.0; xm1!^ii := xm1;
                ii :=1; altern := -1;
                while abs(term :=(altern * xm1!^ii/(ii*ii))) >  yy do <<
                 summa := summa +  term; ii:=ii+1 ;
                 altern := -1 * altern; xm1!^ii := xm1!^ii *xm1>>;
                return summa; end;
>>;

algebraic procedure compute_lerch_phi(z,s,a);
    begin scalar !*uncached,yy,summa,k,term,pow;
           !*uncached :=t;
           term := 1; pow := 1;
           yy := 10^(-(lisp !:prec!:) -3);
           k := 0;
           summa := 0;
           while term > yy do <<
                if (a + k) neq 0 then
                << term := pow / (a+k)^s;
                   summa := summa + term>>;
                pow := pow * z;
                k := k + 1; >>;
           return summa;
    end;

endmodule;
end;




