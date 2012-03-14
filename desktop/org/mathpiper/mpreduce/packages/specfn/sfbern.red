module sfbern;   % Procedures for computing Bernoulli numbers.
%
% Author: Chris Cannam, Oct 1992.

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


% Module for Euler numbers added by Kerry Gaskell, Sep 1993

%
% Note there is currently no Bernoulli polynomial function.
% There was one in an older version but it won't convert directly.
% This is Something To Be Done.

fluid '(compute!-bernoulli);

imports complex!*on!*switch, complex!*off!*switch,
   complex!*restore!*switch;

exports nearest!-int!-to!-bf, bernoulli!*calc, multi!*bern,
   single!*bern, retrieve!*bern;


algebraic operator bernoulli;
symbolic operator bernoulli!*calc;


algebraic (bernoullirules := {

   bernoulli(~n)  =>  1 when numberp n and n = 0,
   bernoulli(~n)  =>  -1/2 when numberp n and n = 1,
   bernoulli(~n)  =>  0 when numberp n and impart n = 0
      and n = floor n and n/2 neq floor (n/2) and n > 0,
   bernoulli(~n)  =>  bernoulli!*calc n when numberp n
      and impart n = 0 and n = floor n and n > 0

})$

algebraic (let bernoullirules);

algebraic procedure bernoulli!*calc n;
   begin scalar precom, result, prepre;
% Loading the SPECFAUX module will do two things.  First it will
% set compute!-bernoulli to true, so that there is no future attempt
% to load it.  Then it will set up a table of values in the variable
% bernoulli!-alist, where the table is computed at compile time rather
% than load or run-time.  This will make compiling specfaux.red a fairly
% slow process.  It also has bad consequences for any attempt to run
% this code interpreted.
% Note: ACN find the "algebraic symbolic" stuff here pretty heavy
% and confusing, but without it REDUCE sticks in calls to aeval (etc)
% in places where that is not wanted.  Maybe a future version of the
% language will make mixed algebraic/symbolic mode code less delicate.
      if (lisp null compute!-bernoulli) then
         symbolic <<errorset!*('(load_package '(specfaux)), nil); nil>>;
      precom := complex!*off!*switch();
      if (prepre := precision(0)) < !!nfpd
      then precision (!!nfpd + 1);
      result := algebraic symbolic retrieve!*bern(n);
      precision prepre;
      complex!*restore!*switch(precom);
      return result;
   end;

symbolic procedure retrieve!*bern n;
   begin scalar info, result;
      integer heldpre;
      info := assoc(n, bernoulli!-alist);
      if not info then result := bern!*calc (n, '(() () ()))
      else
         << info := cdr info;
            if !*rounded then
               if (heldpre := cadr info) and heldpre >= c!:prec!:() then
                     result := mk!*sq !*f2q rd!:prep caddr info
               else if (result := car info) then
                     result := mk!*sq !*f2q mkround
                                 divbf(i2bf!: caadr result,
                                       i2bf!: cdadr result)
               else result := bern!*calc(n, info)
            else if not (result := car info) then
                  result := bern!*calc(n,info) >>;
      return result;
   end;


symbolic procedure bern!*calc(n, info);
   begin scalar result;
      result := single!*bern(n/2);
      if !*rounded then
         info := list (car info, c!:prec!:(), result)
      else info := list (result, cadr info, caddr info);
      bernoulli!-alist := (n . info) . bernoulli!-alist;
      return result;
   end;



%
%  Computation of Bernoulli numbers using the algorithms of
%     one Herbert S. Wilf, presented by Sandra Fillebrown in the
%     Journal of Algorithms 13 (1992)
%
%  Chris Cannam, October 1992
%


%
% Useful auxiliary fn.
%

symbolic procedure nearest!-int!-to!-bf(x);
   (conv!:bf2i rb)
      where rb = (if lessp!:(x,bfz!*)
         then difference!:(x,bfhalf!*)
         else plus!:(x,bfhalf!*));



%
% Procedure to compute B(2k) for k = 2 ... n
%
% Returns list of even bernoullis from B(4) to B(2n),
% in reverse order; only works when compiled, owing
% to reliance upon msd!:, which is a compiled inline
% macro.
%
% If called with rounded mode off, it computes the
% exact quotient; otherwise it will usually approximate
% (to the correct precision) if it saves time to do so.
%


symbolic procedure multi!*bern(n);
   begin scalar results, primes, tprimes, r0, rk, rkm1, b2k,
         tpi, pie, tk, n2k;
       integer thisp, gn, prepre, prernd, p2k, k2, plim, d2k;

      results := nil;
      prernd := !*rounded;
      if not prernd then on rounded;
      prepre := precision 0;

      if new!*bfs then
         << gn := 2 * n * msd!: n;
            if gn < (log2of10*!!nfpd) then precision (!!nfpd + 2)
            else if prepre > (gn/3) or not prernd then
                     precision (gn/3 + 1)
            else precision (prepre + 2) >>
      else
         << gn := 2 * n * length explode n;
            if gn < !!nfpd then precision (!!nfpd + 2)
            else if prepre > gn or not prernd then
                     precision (gn + 2)
            else precision (prepre + 2) >>;

      tpi := pi!*(); pie := divbf(bfone!*, timbf(tpi, e!*()));

      if n < 1786 then primes := !*primelist!*
      else
         << primes := nil;
            for thisp := 3573 step 2 until (2*n + 1) do
               if primep thisp then primes := thisp . primes;
            primes := append(!*primelist!*, reverse primes) >>;

      r0 := sq2bf!* algebraic ((2*pi)**(-2));
      rkm1 := timbf(i2bf!: 4, r0);

      for k := 2:n do
         << k2 := 2*k;
            rk := timbf(i2bf!:(k2*(k2 - 1)), timbf(r0, rkm1));
            rkm1 := rk;

            tk := bfone!*; d2k := 1;
            plim := 1 + conv!:bf2i timbf(i2bf!: k2, pie);

            tprimes := cdr primes; thisp := car primes;
            while thisp <= plim do
               << p2k := thisp ** k2;
                  tk := timbf(tk, divbf(i2bf!: p2k, i2bf!: (p2k - 1)));
                  thisp := car tprimes;
                  tprimes := cdr tprimes >>;

            tprimes := cdr primes; thisp := car primes;
            while thisp <= k+1 do
               << if cdr divide (k2, thisp - 1) = 0 then
                     d2k := d2k * thisp;
                  thisp := car tprimes;
                  tprimes := cdr tprimes >>;
            if primep (k2 + 1) then d2k := d2k * (k2 + 1);

            n2k := timbf(timbf(rk, tk), i2bf!: d2k);
            if prernd then
               b2k := mk!*sq !*f2q mkround
                        divbf (i2bf!: (((-1)**(k+1)) *
                              nearest!-int!-to!-bf n2k),
                           i2bf!: d2k)
            else b2k := list ('!*sq, (((-1)**(k+1)) *
                           nearest!-int!-to!-bf n2k) . d2k, t);
            results := b2k . results >>;

      precision prepre;
      if not prernd then off rounded;
      return results;

   end;



%
% Procedure to compute B(2n). If it is called with rounded
% mode off, it computes the exact quotient; otherwise it
% will approximate (to the correct precision) whenever it
% saves time to do so.
%

symbolic procedure single!*bern(n);
   begin scalar result, primes, tprimes, rn, tn, n2n, pie;
      integer d2n, thisp, gn, prepre, prernd, p2n, n2, plim;

      prernd := !*rounded;
      if not prernd then on rounded;
      prepre := precision 0;

      if new!*bfs then
         << gn := 2 * n * msd!: n;
            if gn < (log2of10*!!nfpd) then precision (!!nfpd + 2)
            else if prepre > (gn/3) or not prernd then
                      precision (gn/3 + 1)
                 else precision (prepre + 2) >>
      else
         << gn := 2 * n * length explode n;
            if gn < !!nfpd then precision (!!nfpd + 2)
            else if prepre > gn or not prernd then
                     precision (gn + 1)
                 else precision (prepre + 2) >>;

      pie := divbf(bfone!*, timbf(pi!*(), e!*()));

      if n < 1786 then primes := !*primelist!*
      else
         << primes := nil;
            for thisp := 3573 step 2 until (2*n + 1) do
               if primep thisp then primes := thisp . primes;
            primes := append(!*primelist!*, reverse primes) >>;

      n2 := 2*n;
      rn := divbf(i2bf!: (2 * factorial n2),
         sq2bf!* algebraic ((2*pi)**(n2)));

      tn := bfone!*; d2n := 1;
      plim := 1 + conv!:bf2i timbf(i2bf!: n2, pie);

      tprimes := cdr primes; thisp := car primes;
      while thisp <= plim do
         << p2n := thisp ** n2;
            tn := timbf(tn, divbf(i2bf!: p2n, i2bf!: (p2n - 1)));
            thisp := car tprimes;
            tprimes := cdr tprimes >>;

      tprimes := cdr primes; thisp := car primes;
      while thisp <= n+1 do
         << if cdr divide (n2, thisp - 1) = 0 then
               d2n := d2n * thisp;
            thisp := car tprimes;
            tprimes := cdr tprimes >>;
      if primep (n2 + 1) then d2n := d2n * (n2 + 1);

      n2n := timbf(timbf(rn, tn), i2bf!: d2n);

      precision prepre;
      if prernd then
         result := mkround divbf(i2bf!: (((-1)**(n+1)) *
                     nearest!-int!-to!-bf n2n),
                  i2bf!: d2n)
      else
         << off rounded;
            result := list ('!*sq, (((-1)**(n+1)) *
               nearest!-int!-to!-bf n2n) . d2n, t) >>;

      return result;

    end;



% Euler numbers module by Kerry Gaskell

algebraic operator Euler;

algebraic
 let {    Euler(0)  => 1,
        Euler(~n) => Euler_aux(n) when fixp n and n > 0};

flag('(euler_aux),'opfn);

symbolic procedure Euler_aux(n);

if not evenp n then 0 else

begin scalar nn,list_of_bincoeff, newlist, old, curr,eulers,sum;

list_of_bincoeff := { 1 };

eulers :={ -1,1};

nn := -2;

while N > 0 do
<<      nn := nn + 1;
        old := 0;
        newlist := {};
        while not(list_of_bincoeff = {}) do

        <<  curr := first list_of_bincoeff;
            newlist := (old + curr) . newlist;
            old := curr;
            list_of_bincoeff := rest list_of_bincoeff;
        >>;

        list_of_bincoeff := 1 . newlist;
        n := n -1        ;

% now that we have got the row of Pascal's triangle
% we can use it and compute the Next Euler number.

        if nn > 0 and evenp nn then <<
           curr := list_of_bincoeff;
           old := eulers; sum := 0;
           while old do <<
              curr := cddr curr;
              sum := sum - first old * first curr;
              old := cdr old;
              >>;
           eulers := sum . eulers;
          >>;
>>;

return first eulers;
end;

endmodule;

end;


