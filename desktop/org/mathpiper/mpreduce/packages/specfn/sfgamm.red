module sfgamm;   % Gamma function procedures and rules for REDUCE.

% Author: Chris Cannam, Sept/Oct '92

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
   complex!*restore!*switch, sf!*eval;

exports do!*gamma, do!*pochhammer, do!*poch!*conj!*calc;

fluid '(COMPUTE!-BERNOULLI intlogrem);

%
%   Rule set for the gamma function.
%

%
% Comments:
%
%   Base cases are provided for gamma(1) and gamma(1/2).
%   The rules will convert gammas to factorials where appropriate.
%   A numerical value is always computed if rounded mode is on.
%

algebraic operator gamma,m_gamma; % m_gamma is the incomplete gamma
 % function which happens to be produced by definite integration.

symbolic (operator do!*gamma);


algebraic (gamma!*rules := {

   gamma(~x)  =>  1 when numberp x and x = 1,
   gamma(~x)  =>  sqrt(pi) when numberp x and x = (1/2),

   gamma(~x)  =>  factorial (x-1)
      when numberp x and impart x = 0
         and x = floor x and x > 0,

%  gamma(~x)  =>  infinity
%     when numberp x and impart x = 0
%        and x = floor x and x < 1,

   gamma(~x)  =>  gamma(x-1) * (x-1)
      when numberp x and not symbolic !*rounded
       and impart x = 0 and (64*x) = floor(64*x) and x > 1 and x < 50,

   gamma(~x)  =>  pi / (sin(pi*x) * gamma(-x) * (-x))
      when numberp x and x < 0 and not (fixp x and x < 1),

   gamma(~x)  =>  do!*gamma(x)
      when numberp x and not (fixp x and x < 1) and symbolic !*rounded,

   df(gamma(~x), x)  =>  gamma(x) * psi(x)

})$

algebraic (let gamma!*rules);


algebraic operator beta;

algebraic (beta!*rules := {

beta(~z,~w)  =>  (gamma(z) * gamma(w)) / gamma(z+w)
   when (numberp z and numberp w and impart z = 0 and impart w = 0
         and not ((z = floor z and z < 1)
               or (w = floor w and w < 1)
               or (z+w = floor (z+w) and (z+w) < 1)))
     or (numberp z and numberp w
         and (impart z neq 0 or impart w neq 0))
     or not (numberp z and numberp w),

beta(~z,~w)  =>  0
   when numberp z and numberp w and impart z = 0 and impart w = 0
      and not ((z = floor z and z < 1)
            or (w = floor w and w < 1))
      and (z+w = floor (z+w) and (z+w) < 1)

%beta(~z,~w)  =>  Infinity
%   when numberp z and numberp w and impart z = 0 and impart w = 0
%      and ((z = floor z and z < 1)
%        or (w = floor w and w < 1))
%      and not (z+w = floor (z+w) and (z+w) < 1)

})$

algebraic (let beta!*rules);



Comment Ruleset for calculating the Pochhammer symbol
        Author:  Wolfram Koepf, Freie Universitaet Berlin 1992,
        Translated to Reduce syntax by Winfried Neun, ZIB Berlin.
        Made generally safer (and uglier) by Chris Cannam, ZIB.
        ;


algebraic operator pochhammer;
symbolic (operator do!*pochhammer, do!*poch!*conj!*calc);

algebraic (pochhammer!*rules := {

df(pochhammer(~z,~k),~z) => pochhammer(~z,~k) * (Psi(z+k)-Psi(z)),

pochhammer(~z,~k)  => (-1)^k*factorial(-z)/factorial(-z-k)
   when fixp z and z<0,

pochhammer(~z,~k)  =>  ( for i:=0:(k-1) product(z + i))
   when numberp k and k < 20 and k > 0,

pochhammer(~z,~k)  =>  1
   when numberp k and k = 0,

pochhammer(~z,~k)  => factorial(z+k-1)/factorial(z-1)
   when fixp z and z > 0,

pochhammer(~z,~k -1)  =>
   2 * pochhammer(1/2,k) / (2*k -1)
      when numberp z and z = 1/2,

pochhammer(~a,~k)  =>
   factorial(2k)/((4^k) * factorial(k))
      when numberp a and a = 1/2,

pochhammer(~n,~k)  =>
   do!*pochhammer(n,k)
      when numberp n and numberp k
         and impart n = 0 and impart k = 0
            and n = floor n and k = floor k
               and n > -1 and k > 0,

pochhammer(~a,~k)  =>
   do!*pochhammer(a,k)
      when symbolic !*rounded
         and numberp k and numberp a
            and impart a = 0 and impart k = 0
               and ((a neq floor a) or (a > 0))
                  and k = floor k and k > 0,

pochhammer(~n,~k)  =>
   (-1)^k * factorial(-n) / factorial(-n-k)
      when numberp n and numberp k
         and impart n = 0
            and n = floor n and n < 1 and (-n-k) >= 0,

pochhammer(~a,~k)  =>
   pochhammer(2*a-1,2k)/((4^k) * pochhammer((2 a -1)/2,k))
      when numberp a and impart a = 0
         and (a+1/2) = floor (a+1/2) and a > 0,

pochhammer(~a,~k)  =>
   (-1)^(-a+1/2) * Pochhammer(1-a-(-a+1/2),(-a+1/2)) *
                   Pochhammer(a+(-a+1/2),k-(-a+1/2))
      when numberp a and impart a = 0
         and (a+1/2) = floor (a+1/2) and a < 0});


algebraic (special!*pochhammer!*rules := {

        % these special rules are normally disabled because
        % they produce a lot of load for the algebraic mode

pochhammer(~a,~k)*pochhammer(~b,~k)  =>
   pochhammer(2a,2k)/(4^k)
      when (b-a)=1/2,

pochhammer(~a,~k)  =>
   (-1)^(-a+1/2) * pochhammer(1-a-(-a+1/2),-a+1/2) *
      pochhammer(a +(-a +1/2),k-(-a+1/2))
         when numberp a and impart a = 0
            and (a+1/2) = floor (a+1/2) and a<0,

pochhammer(~z,~k) * pochhammer(~cz,~k)  =>
   do!*poch!*conj!*calc(z,k)
      when numberp z and numberp cz and numberp k
         and not(impart z = 0) and z = conj cz
            and impart k = 0 and k = floor k and k >= 0,

pochhammer(~a,~k)*pochhammer(~aa,~k)  =>
   factorial(3 k)/(factorial(k) * 27^k)
      when numberp a and a = 1/3 and numberp aa and aa = 2/3,

pochhammer(~a,~k) * pochhammer(~aa,~k)  =>
   factorial(1 + 3 k)/(27 ^k * factorial(k))
      when numberp a and a = 2/3 and numberp aa and aa = 4/3,

pochhammer(~b,~k) * pochhammer(~c,~k)  =>
   pochhammer(3*b,3*k)/( 27^k * pochhammer(b +2/3,k))
      when numberp b and numberp c
         and (c-b)=1/3 and (b-1/3) = floor (b-1/3) and not (b-1/3 = 0),

pochhammer(~a,~k)*pochhammer(~aa,~k)*pochhammer(~aaa,~k)  =>
   factorial(4*k)/(factorial(k) * 64^k)
      when numberp a and numberp aa and numberp aaa
         and a = 1/4 and aa = 1/2 and aaa = 3/4,

pochhammer(~a,~k)*pochhammer(~aa,~k)*
      pochhammer(~aaa,~k)*pochhammer(~aaaa,~k)  =>
   factorial(5*k)/(factorial(k) * 3125^k)
      when numberp a and numberp aa
         and numberp aaa and numberp aaaa
            and a = 1/5 and aa = 2/5 and aaa = 3/5 and aaaa = 4/5,

pochhammer(~a,~k)*pochhammer(~aa,~k)*
      pochhammer(~aaa,~k)*pochhammer(~aaaa,~k)  =>
   5*(1/5 +k)*factorial(5*k)/(factorial(k) * 3125^k)
      when numberp a and numberp aa
         and numberp aaa and numberp aaaa
            and a = 2/5 and aa = 3/5 and aaa = 4/5 and aaaa = 6/5,

pochhammer(~a,~k)*pochhammer(~aa,~k)*
      pochhammer(~aaa,~k)*pochhammer(~aaaa,~k)  =>
   (25 *(1/5+k)*(2/5 +k)*factorial(5*k)) / (factorial(k) * 2* 3125^k)
      when numberp a and numberp aa
         and numberp aaa and numberp aaaa
            and a = 3/5 and aa = 4/5 and aaa = 6/5 and aaaa = 7/5,

pochhammer(~a,~k)*pochhammer(~aa,~k)*
      pochhammer(~aaa,~k)*pochhammer(~aaaa,~k)  =>
   (125*(1/5+k)*(2/5+k)*(3/5+k)*factorial(5*k)) /
      (factorial(k) * 6 *3125^k)
         when numberp a and numberp aa
            and numberp aaa and numberp aaaa
               and a = 4/5 and aa = 6/5 and aaa = 7/5 and aaaa = 8/5,

pochhammer(~a,~k)*pochhammer(~aa,~k)*
      pochhammer(~aaa,~k)*pochhammer(~aaaa,~k)  =>
   (625*(1/5+k)*(2/5+k)*(3/5+k)*(4/5+k)*factorial(5*k)) /
      (factorial(k) * 24 *3125^k)
         when numberp a and numberp aa
            and numberp aaa and numberp aaaa
               and a = 6/5 and aa = 7/5 and aaa = 8/5 and aaaa = 9/5,

Pochhammer(~a,~k)//Pochhammer(~b,~k)  => (a + k -1)/(a - 1)
                        when (a - b)=1,

Pochhammer(~a,~k)//Pochhammer(~b,~k)  => (b - 1)/(b + k -1)
                        when (b - a)=1
})$

algebraic (let pochhammer!*rules);



algebraic procedure do!*gamma(z);
   (if impart z = 0
    then algebraic sf!*eval('gamma!*calc!*s,{z})
    else algebraic sf!*eval('gamma!*calc,{z}));



algebraic procedure gamma!*calc!*s(z);
   begin scalar scale, result, alglist!*;
      integer p, precom;
      precom := complex!*off!*switch();
      p := precision(0);
      op := lisp c!:prec!:();
      if p < !!nfpd then precision (!!nfpd + 1);
%      else precision(p+3);
      if p > 49 then
         scale := 500 + p
      else scale := 10 * (p+1);
      if z > scale then scale := 2;
      result := gamma!*calc!*s!*sub(z,scale,op);
      precision p;
      complex!*restore!*switch(precom);
      return result;
   end;



algebraic procedure gamma!*calc!*s!*sub(z,scale,op);
   begin scalar za, z1, result; integer z0;
      za := z; z0 := floor (z+1); z1 := z + scale;
      result := algebraic symbolic log!*gamma(z1,z0);
      result := (exp result / pochhammer(z,scale));
      return result;
   end;



symbolic procedure log!*gamma(z, zint);
   begin scalar result, this, zpwr, zsq, admissable, abk;
      integer k, rp, orda, magn;

      magn := bf!*base ** c!:prec!:();

      if zint < 1000 then
         if new!*bfs
         then admissable := divbf
                  (i2bf!: msd!: (1 + (factorial zint / 3)),
                   i2bf!: magn)
         else admissable := divbf
                  (i2bf!: length explode factorial zint,
                   i2bf!: magn)
      else
         admissable := divbf
               (difbf(log!:(timbf(plubf(bftwo!*,bfhalf!*),
                     sqrt!:(exptbf(i2bf!: zint,2*zint+1,bfone!*),8)),8),
                  i2bf!: zint),i2bf!: magn);

      z := sq2bf!* z;
      result := timbf(log!* z, difference!:(z, bfhalf!*));
      result := plubf((difference!: (result, z)), timbf(bfhalf!*,
         log!* timbf(pi!*(), bftwo!*)));
      this := plubf (admissable, bfone!*);
      rp := c!:prec!:(); orda := order!: admissable - 5;
      k := 2; zpwr := z; zsq := timbf (z, z);

      if (lisp null compute!-bernoulli) then
         symbolic <<errorset!*('(load_package '(specfaux)), nil); nil>>;

      while greaterp!:(abs!: this, admissable) do
         << abk := retag cdr !*a2f retrieve!*bern k;
            this := divide!: (abk,
               timbf (i2bf!: (k * (k-1)), zpwr), rp);
            rp := order!: this - orda;
            result := plubf(result, this);
            zpwr := timbf (zpwr, zsq);
            k := k + 2; >>;

      return mk!*sq !*f2q mkround result;
   end;




%
% algebraic procedure loggamma!*calc!*sub(z);
%
% Procedure to calculate ln(gamma(z)); returns a 2-list of
%   the value of ln(gamma) and the final term used in the
%   constructing series. (This term is used by gamma!*calc!*sub
%   to compute the error.)
%
% Also requires to be fed the indices for the first and last
%     terms to be used in constructing the portion of the
%     series that it will construct.  Both of these values should
%     be even; if the first term's index is 2, then the initial
%     terms to construct the log gamma will also be included.
%

algebraic procedure loggamma!*calc!*sub(z, premier, dernier);
   begin scalar result, ft, sofar, div;
      if premier = 2 then result := ((z - (1/2)) * log z) - z +
         ((1/2) * log (2*pi))
      else result := 0;
      sofar := z ** (dernier-1);
      div := z ** 2;
      result := result + (ft := (bernoulli!*calc(dernier) /
         ((dernier -1) * dernier * sofar)));
      for n := (dernier-2) step -2 until premier
         do << sofar := sofar / div;
               result := result + (bernoulli!*calc(n) /
                  (n * (n-1) * sofar)) >>;
      return { result, ft };
   end;



%
% algebraic procedure gamma!*calc!*sub(z,scale);
%
% Procedure to calculate values of gamma. Given the value
%   at which to calculate and the amount by which to scale
%   up in calculation, returns (eventually) a 2-list of the value
%   of gamma and the maximum error on this value. Needs a
%   better interface -- see the gamma!*calc procedure, below.
%

algebraic procedure gamma!*calc!*sub(z,scale);
   begin scalar result, expresult, ft, err, newerr, rescale,
         admissable, alglist!*;
      integer prepre, premier, dernier;
      prepre := precision(0);
%      precision (prepre + 4);
      rescale := for k := 1:scale product (z+k-1);
      admissable := 1 / (10 ** (prepre + 4));
      err := admissable + 1;
      premier := 2;
      dernier := 50;
      result := 0;
      while (err > admissable) do
         << ft := loggamma!*calc!*sub(z+scale, premier, dernier);
            result := result + first ft;
            ft := second ft;
            expresult := exp result;
            newerr := (abs ((expresult/(exp ft)) - expresult))/rescale;
            if newerr > err or (dernier > 180 and
                     newerr > (admissable * 1000)) then
               << scale := scale * 3;
                  rescale := (for m := 1:scale product (z+m-1));
                  write ("Scaling up to scale=", scale,
                           " (from ", scale/3, ")");
                  result := 0;
                  premier := 2;
                  dernier := 100;
                  err := admissable + 1 >>
               else
               << err := newerr;
                  premier := dernier + 2;
                  dernier := dernier + 30 >> ;
         >>;
      result := expresult / rescale;
%      precision prepre;
      return { result, err };
   end;



%
% algebraic procedure gamma!*calc(z);
%
% Procedure to calculate values of gamma to (one hopes)
%   an error within the tolerance allowed by the current
%   precision. Calls gamma!*calc!*sub (above) with a scale worked
%   out by slightly ad-hoc (but apparently fairly good) methods
%   and will be generally OK for z between 1e-7 and infinity.
%
% Only works for positive z, and only in rounded mode.
%

algebraic procedure gamma!*calc(z);
   if precision(0) > 49
     then first gamma!*calc!*sub(z,500+4*precision(0))
    else first gamma!*calc!*sub(z, ceiling (exp(precision(0)/10) * 2));





%
% Functions to compute Pochhammer(a,k).
%


algebraic procedure do!*pochhammer(a,k);
   algebraic sf!*eval('pochhammer!*calc,{a,k});

algebraic procedure do!*poch!*conj!*calc(z,n);
   algebraic sf!*eval('poch!*conj!*calc,{z,n});


algebraic procedure pochhammer!*calc(a,k);
   (if fixp a and not symbolic !*rounded
   then (symbolic fac!-part(a, a+k-1))
   else pochhammer!*calc!*sub(a,k));


algebraic procedure pochhammer!*calc!*sub(a,k);
   begin scalar result, prepre, precom, a0;
      precom := complex!*off!*switch();
      prepre := precision 0;
      if prepre < !!nfpd then precision (1+!!nfpd);
      a0 := a;
      result := if (symbolic new!*bfs)
         then algebraic symbolic pochhammer!*calc!*sub!*sub!*newbf(a,k)
         else algebraic symbolic pochhammer!*calc!*sub!*sub!*oldbf(a,k);
      precision prepre;
      complex!*restore!*switch(precom);
      return result;
   end;


symbolic procedure pochhammer!*calc!*sub!*sub!*oldbf(a,k);
   begin scalar result;
      if fixp a
       then result := poch!*sub!*2(0, k-1, i2bf!: a)
       else << a := sq2bf!* a;
               if order!: a < - !:prec!:
                  then result := poch!*sub!*2(0, k-1, bfone!*)
                  else if length explode mt!: a < !:prec!:/2
                              and order!: a > -2
                          then result := poch!*sub!*2(0, k-1, a)
                          else result := poch!*sub!*1(a, k-1,bfone!*)>>;
      return mk!*sq !*f2q mkround result;
   end;


symbolic procedure pochhammer!*calc!*sub!*sub!*newbf(a,k);
   begin scalar result;
      if fixp a
       then result := poch!*sub!*2(0, k-1, i2bf!: a)
       else << a := sq2bf!* a;
               if order!: a < - c!:prec!:()
                  then result := poch!*sub!*2(0, k-1, bfone!*)
                  else if preci!: a < c!:prec!:()/2 and order!: a>-2
                         then result := poch!*sub!*2(0, k-1, a)
                        else result := poch!*sub!*1(a,k-1,bfone!*)>>;
      return mk!*sq !*f2q result;
   end;


symbolic procedure poch!*sub!*1(a,k,tot);
   if k=0 then timbf(tot,a)
   else poch!*sub!*1(plus!:(a,bfone!*),k-1,timbf(tot,a));


symbolic procedure poch!*sub!*2(m,n,a);
   if m=n then plus!:(a,i2bf!: m)
   else if m = n-1 then
      timbf(plus!:(a,i2bf!: m), plus!:(a,i2bf!: n))
   else (timbf(poch!*sub!*2(m,p,a),poch!*sub!*2(p+1,n,a)))
      where p=(m+n)/2;


algebraic procedure poch!*conj!*calc(z,n);
   for i := 1:n product ((repart z + (i-1))**2 + (impart z)**2);


% lets prod (in misc package) know about gamma.

algebraic
  let { prod(~n,~n,~anf,~ende) => Gamma(ende + 1)/Gamma(anf)
                when not( fixp anf and anf < 0) ,

        prod(~n,~n,~anf) => Gamma(n+1)/Gamma(anf)
                when not( fixp anf and anf < 0),

        prod(~k +~n,k,~nanf,~nend) =>
                 gamma(nend + 1 + n)/gamma (nanf + n)
                when numberp nanf and numberp n and nanf + n > 0,

        prod(~k +~n,k,~nanf,~nend) => 0
                when numberp nanf and numberp n and nanf= - n,

        prod(~~a*~k +~n,k,~nanf,~nend) => prod(a,k,nanf,nend)*
                 gamma(nend + 1 + n/a)/gamma (nanf + n/a)
                 when freeof(a,k) and freeof (n,k),
%               when not(numberp nanf and numberp n),


%       prod(~n,~n) =>  gamma(n+1)},

        (~~u*gamma(~x+~~n0))//(~~v*gamma(x +~~n1)) =>
        (u*gamma(~x+n0))/(v*(x+n1-1)*Gamma(x+n1-1))
                 when not (numberp x and x eq 0)
                 and (fixp n0 and fixp n1 and n0<n1 and (n1 -n0)< 6),

        (~~u*gamma(~x+~~n0))//(~~v*gamma(x +~~n1)) =>
        (u*gamma(~x+n0-1)*(x+n0-1))/(v*Gamma(x+n1))
                when not (numberp x and x eq 0)
                 and (fixp n0 and fixp n1 and n0>n1  and (n0-n1)< 6)
};

endmodule;

end;

