module sfbinom;   % Procedures for computing Binomial coefficients
                  % Stirling numbers and such
%
% Author: Winfried Neun, Feb 1993, Sep 1993

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


% algebraic operator binomial;  % Now in entry.red.
deflist('((binomial simpiden)),'simpfn);

algebraic <<
 let { binomial (~n,~k) => ((for l:=0:(k-1) product (n-l))/factorial k)
                        when fixp n and fixp k and  k >=0,
       binomial (~n,~k) => 1
                when fixp n and fixp k and n >= k and k=0,
       binomial (~n,~k) => 0
                when fixp n and fixp k and n<k and n >=0,
       binomial (~n,~k) => 0
                when fixp n and fixp k and k < 0,
       binomial (~n,~k) => Gamma(n+1) / Gamma (k+1) / Gamma(n-k+1)
                when numberp n and  numberp k and not(fixp (n - k) and
                (n-k) < 0),
       df(binomial(~c,~k),c) => binomial(c,k)*(Psi(1+c)-Psi(1+c-k))
     } >>;

% Some rules for quotients of binomials are still missing

algebraic operator Stirling1, Stirling2;

algebraic
 let {Stirling1(~n,~n) => 1,
      Stirling1(~n,0)  => 0 when not(n=0),
      Stirling1(~n,~n-1) => - binomial(n,2),
      Stirling1(~n,~m) => 0 when fixp n and fixp m and n < m,
      Stirling1(~n,~m) => (for k:=0:(n-m) sum
                        ( (-1)^k * binomial(n-1+k,n-m+k) *
                          binomial(2*n-m,n-m-k) * Stirling2(n-m+k,k)))
                when fixp n and fixp m and n > m,
% This rather naive implementation will cause problem
% when m - n is large !
      Stirling2(~n,~n) => 1,
      Stirling2(~n,0)  => 0 when not(n=0),
      Stirling2(~n,~n-1) => binomial(n,2),
      Stirling2(~n,~m) => 0 when fixp n and fixp m and n < m,
      Stirling2(~n,~m) => calc_stirling2(n,m)
                        when fixp n and fixp m and n >m };

algebraic procedure calc_stirling2 (n,m);

begin scalar bin_row;
bin_row := binomial_row(m);
return
((for k:=0:m sum ( (-1)^(m-k)
  * part(bin_row,k+1)*k^n))/factorial(m));
end;


symbolic procedure binomial_row (n);

        % computes nth row of the Pascal triangle

begin scalar list_of_bincoeff, newlist, old, curr;

if (not fixp n) or (n < 0) then return nil;

list_of_bincoeff := { 1 };

while N > 0 do
     << old := 0;
        newlist := {};
        while not(list_of_bincoeff = {}) do
            <<  curr := car list_of_bincoeff;
                newlist := (old + curr) . newlist;
                old := curr;
                list_of_bincoeff := rest list_of_bincoeff;
            >>;
        list_of_bincoeff := 1 . newlist;
        n := n -1
     >>;
return 'list . list_of_bincoeff;

end;

flag('(binomial_row),'opfn);

symbolic procedure Motzkin(n);

if (n:= reval n)=0 then 1 else if n=1 then 1 else
% ((3*n-3)*Motzkin(n-2) + (2*n+1)* Motzkin(n-1))/(n+2);
if not fixp n or n <0 then
         mk!*sq((list((list('motzkin,n) . 1) . 1)) . 1)
   else begin scalar vsop,oldv,newv;
    newv := oldv :=1;
    for i:=2:n do <<
       vsop := oldv;
       oldv := newv;
       newv:= ((3*i-3) * vsop + (2*i +1)*oldv)/(i+2);
       >>;
    return newv;
end;

flag('(motzkin),'opfn);

endmodule;

end;
