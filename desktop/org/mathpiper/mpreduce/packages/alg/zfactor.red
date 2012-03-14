xmodule zfactor;  % Integer factorization.

% Author: Julian Padget.
% Modifications by: Fran Burstall, John Abbott, Herbert Melenk,
%                   Arthur Norman.

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


exports nextprime, primep, zfactor, zfactor1, nrootnn;

% nextprime - returns the next prime GREATER than its argument;
% primep - determines whether argument is prime or not;
% zfactor - returns an alist of factors dotted with their multiplicities

imports evenp, gcdn, general!-modular!-expt, general!-modular!-times,
idifference, igreaterp, ilessp, iplus2, iroot, isqrt, leq,
modular!-expt, modular!-times, neq, prepf, prin2t, random, reversip,
set!-general!-modulus, set!-modulus, set!-small!-modulus, typerr;

% needs bigmod,smallmod;
%
% internal-functions add-factor, general-primep, mcfactor!*,
% internal-primep, mcfactor, small-primep;

% Parameters to this module are:
%
%    !*confidence!* - controls the computation in the primality test.
%        Probability that a number is composite when test says it is
%        prime is 1/(2**(2*!*confidence!*)).
%
%    !*maxtrys!* - controls the maximum number of attempts to be made
%        at factorisation (using mcfactor) whilst varying the polynomial
%        used as part of the Monte-Carlo technique.  When !*maxtrys!* is
%        exceeded assumes n is prime (case will most likely occur when
%        primality test fails).
%
%    !*mod!* - controls the modulus of the numbers emitted by the random
%        number generator.  It is important that the number being tested
%        for primality should lie in [0,!*mod!*].
%
% Globals private to this module are:
%
%    !*primelist!* - a list of the first xxx prime numbers used in the
%        first part of the factorization where trial division is
%        employed.
%
%    !*last!-prime!-in!-list!* - the largest prime in the !*primelist!*

fluid '(!*maxtrys!* !*confidence!*);

!*maxtrys!*:=10; !*confidence!* := 40;

global '(!*last!-prime!-squared!* !*primelist!*
         !*last!-prime!-in!-list!* largest!-small!-modulus);

!*primelist!*:='(
2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97
101 103 107 109 113 127 131 137 139 149 151 157 163 167 173 179 181 191
193 197 199 211 223 227 229 233 239 241 251 257 263 269 271 277 281 283
293 307 311 313 317 331 337 347 349 353 359 367 373 379 383 389 397 401
409 419 421 431 433 439 443 449 457 461 463 467 479 487 491 499 503 509
521 523 541 547 557 563 569 571 577 587 593 599 601 607 613 617 619 631
641 643 647 653 659 661 673 677 683 691 701 709 719 727 733 739 743 751
757 761 769 773 787 797 809 811 821 823 827 829 839 853 857 859 863 877
881 883 887 907 911 919 929 937 941 947 953 967 971 977 983 991 997 1009
1013 1019 1021 1031 1033 1039 1049 1051 1061 1063 1069 1087 1091 1093
1097 1103 1109 1117 1123 1129 1151 1153 1163 1171 1181 1187 1193 1201
1213 1217 1223 1229 1231 1237 1249 1259 1277 1279 1283 1289 1291 1297
1301 1303 1307 1319 1321 1327 1361 1367 1373 1381 1399 1409 1423 1427
1429 1433 1439 1447 1451 1453 1459 1471 1481 1483 1487 1489 1493 1499
1511 1523 1531 1543 1549 1553 1559 1567 1571 1579 1583 1597 1601 1607
1609 1613 1619 1621 1627 1637 1657 1663 1667 1669 1693 1697 1699 1709
1721 1723 1733 1741 1747 1753 1759 1777 1783 1787 1789 1801 1811 1823
1831 1847 1861 1867 1871 1873 1877 1879 1889 1901 1907 1913 1931 1933
1949 1951 1973 1979 1987 1993 1997 1999 2003 2011 2017 2027 2029 2039
2053 2063 2069 2081 2083 2087 2089 2099 2111 2113 2129 2131 2137 2141
2143 2153 2161 2179 2203 2207 2213 2221 2237 2239 2243 2251 2267 2269
2273 2281 2287 2293 2297 2309 2311 2333 2339 2341 2347 2351 2357 2371
2377 2381 2383 2389 2393 2399 2411 2417 2423 2437 2441 2447 2459 2467
2473 2477 2503 2521 2531 2539 2543 2549 2551 2557 2579 2591 2593 2609
2617 2621 2633 2647 2657 2659 2663 2671 2677 2683 2687 2689 2693 2699
2707 2711 2713 2719 2729 2731 2741 2749 2753 2767 2777 2789 2791 2797
2801 2803 2819 2833 2837 2843 2851 2857 2861 2879 2887 2897 2903 2909
2917 2927 2939 2953 2957 2963 2969 2971 2999 3001 3011 3019 3023 3037
3041 3049 3061 3067 3079 3083 3089 3109 3119 3121 3137 3163 3167 3169
3181 3187 3191 3203 3209 3217 3221 3229 3251 3253 3257 3259 3271 3299
3301 3307 3313 3319 3323 3329 3331 3343 3347 3359 3361 3371 3373 3389
3391 3407 3413 3433 3449 3457 3461 3463 3467 3469 3491 3499 3511 3517
3527 3529 3533 3539 3541 3547 3557 3559 3571 )$

!*last!-prime!-in!-list!* := car reverse !*primelist!*;

!*last!-prime!-squared!* := !*last!-prime!-in!-list!*^2;

symbolic procedure add!-factor(n,l);
   (lambda (p); if p then << rplacd(p,add1 cdr p); l>> else (n . 1) . l)
      if pairp l then if n>(caar l) then nil else assoc(n,l) else nil;

symbolic procedure zfactor n; zfactor1(n,t);

symbolic procedure zfactor1(n,bool);
   % If bool is NIL, mcfactor!* isn't used.
   if n<0 then ((-1) . 1) . zfactor1(-n,bool)
    else if n<4 then list(n . 1)
    else begin scalar primelist,factor!-list,p,qr;
       primelist := !*primelist!*;
       factor!-list := nil;
       while primelist do
        <<p := car primelist; primelist := cdr primelist;
          while cdr(qr := divide(n, p)) = 0 do
            <<n:= car qr; factor!-list:= add!-factor(p,factor!-list)>>;
       if n neq 1 and p*p>n
         then <<primelist := nil;
                factor!-list:=add!-factor(n,factor!-list);
                n := 1>>>>;
       return if n=1 then factor!-list
               else if null bool then (n . 1) . factor!-list
               else mcfactor!*(n,factor!-list)
     end;

symbolic procedure mcfactor!*(n,factors!-so!-far);
   if internal!-primep n then add!-factor(n,factors!-so!-far)
    else <<n:=(lambda (p,tries); <<
         while (atom p) and (tries<!*maxtrys!*) do <<
            tries:=tries+1;
            p:=mcfactor(n,tries)>>;
         if tries>!*maxtrys!* then <<
            prin2 "ZFACTOR(mcfactor!*): Assuming ";
            prin2 n; prin2t " is prime";
            p:=list n>>
         else p>>)
          (mcfactor(n,1),1);
   if atom n then add!-factor(n,factors!-so!-far)
   else if car n < cdr n then
      mcfactor!*(cdr n,mcfactor!*(car n,factors!-so!-far))
   else mcfactor!*(car n,mcfactor!*(cdr n,factors!-so!-far))>>;

symbolic procedure mcfactor(n,p);
% Based on "An Improved Monte-Carlo Factorisation Algorithm" by
% R.P.Brent in BIT 20 (1980) pp 176-184.  Argument n is the number to
% factor, p specifies the constant term of the polynomial.  There are
% supposed to be optimal p's for each n, but in general p=1 works well.
begin scalar gg,k,m,q,r,x,y,ys;
   m := 20;
   y:=0; r:=q:=1;
outer:
   x:=y;
   for i:=1:r do y:=remainder(y*y+p,n);
   k:=0;
inner:
   ys:=y;
   for i:=1:(if m<(r-k) then m else r-k) do <<
      y:=remainder(y*y+p,n);
      q:=remainder(q*abs(x-y),n)
   >>;
   gg:=gcdn(q,n);
   k:=k+m;
   if (k<r) and (gg leq 1) then goto inner;
   r:=2*r;
   if gg leq 1 then goto outer;
   if gg=n then begin
   loop:
      ys:=remainder(ys*ys+p,n);
      gg:=gcdn(abs(x-ys),n);
      if gg leq 1 then goto loop
   end;
   return if gg=n then n else gg . (n/gg)
end;

symbolic procedure primep n;
   % Returns T if n is prime (an integer that is not zero or a unit).
   if not fixp n then typerr(n,"integer")
%    then <<lprim list("No primep function defined for",n); nil>>
    else if n<0 then primep(-n)
    else if n=1 then nil
    else if n<=!*last!-prime!-in!-list!* then n member !*primelist!*
    else if n<=!*last!-prime!-squared!*
     then begin scalar p;
             p := !*primelist!*;
             while p and remainder(n,car p) neq 0 do p := cdr p;
             return null p
          end
    else if n>largest!-small!-modulus then general!-primep n
    else small!-primep n;

flag('(primep),'boolean);

symbolic procedure internal!-primep n;
   if n>largest!-small!-modulus then general!-primep n
    else small!-primep n;

% This is a version of primep written by FEB for inclusion in zfactor.
% It provides small-primep and general-primep with the following
% corrections of the distribution versions:
% (1) random number zero excluded as a potential witness
% (2) correct range of powers of seed provided
% (3) inspection for -1 replacing gcd's.


symbolic procedure small!-primep n;
% Based on an algorithm of M.Rabin published in the Journal of Number
% Theory Vol 12, pp 128-138 (1980).
  begin integer i,l,m,x,y,w,save; scalar result;
% Filter out some easy cases first
    if evenp n or remainder(n,3) = 0 then return nil;
    m := n-1;
    save := set!-small!-modulus n;
% Express n-1 = (2^l)*m
    l:=0;
    while evenp m do <<m := m/2; l := l+1>>;
    i:=1;
    result:=t;
    while result and i<=!*confidence!* do <<
% Select a potential witness, noting 0, 1 and -1 are not liable to help.
       w := 1 + random(n-2);
% Raise to the odd power.
       x := modular!-expt(w, m);
% From here I can complete the calculation of w^(n-1) by doing a
% sequence of squaring operations.  While I do that I check to see if I
% come across a non-trivial square root of 1, and if I do then I know n
% could not have been prime.  In fact in that case I could exhibit a
% factor, but that does not concern me here.
       if x neq 1 then <<
         for k:=1:l do <<
           y := modular!-times(x,x);
% It is tolerable to continue round the loop after setting result=nil
% because I will then be repeating a squaring of 1, which is cheap.
           if y=1 and x neq (n-1) and x neq 1 then result := nil
           else x := y >>;
% Also if I do not get to 1 at the end then the number is composite, but
% I have no clue as to any factor.
         if x neq 1 then result := nil >>;
       i:=i+1 >>;
    set!-small!-modulus save;
    return result
  end;

symbolic procedure general!-primep n;
% Based on an algorithm of M.Rabin published in the Journal of Number
% Theory Vol 12, pp 128-138 (1980).
  begin integer i,l,m,x,y,w,save; scalar result;
% Filter out some easy cases first
    if evenp n or remainder(n,3) = 0 then return nil;
    m := n-1;
    save := set!-general!-modulus n;
% Express n-1 = (2^l)*m
    l:=0;
    while evenp m do <<m := m/2; l := l+1>>;
    i:=1;
    result:=t;
    while result and i<=!*confidence!* do <<
% Select a potential witness, noting 0, 1 and -1 are not liable to help.
       w := 1 + random(n-2);
% Raise to the odd power.
       x:=general!-modular!-expt(w, m);
% From here I can complete the calculation of w^(n-1) by doing a
% sequence of squaring operations.  While I do that I check to see if I
% come across a non-trivial square root of 1, and if I do then I know n
% could not have been prime.  In fact in that case I could exhibit a
% factor, but that does not concern me here.
       if x neq 1 then <<
         for k:=1:l do <<
           y:=general!-modular!-times(x,x);
% It is tolerable to continue round the loop after setting result=nil
% because I will then be repeating a squaring of 1, which is cheap.
           if y=1 and x neq (n-1) and x neq 1 then result := nil
           else x := y >>;
% Also if I do not get to 1 at the end then the number is composite, but
% I have no clue as to any factor.
         if x neq 1 then result := nil >>;
       i:=i+1 >>;
    set!-general!-modulus save;
    return result
  end;

% The next function comes from J.H. Davenport.

symbolic procedure nextprime p;
   % Returns the next prime number bigger than p.
   if null p or p=0 or p=1 or p=-1 or p=-2 then 2
    else if p=-3 then -2
    else if not fixp p then typerr(!*f2a p,"integer")
    else begin
       if evenp p then p:=p+1 else p:=p+2;
       while not primep p do p:=p+2;
       return p
   end;

put('nextprime,'polyfn,'nextprime);

% The following definition has been added by Herbert Melenk.

symbolic procedure nrootnn(n,x);
   % N is an integer, x a positive integer. Value is a pair
   % of integers r,s such that r*s**(1/x)=n**(1/x). The decomposition
   % may be incomplete if the number is too big. The extraction of
   % the members of primelist* is complete.
   begin scalar pl,signn,qr,w; integer r,s,p,q;
     r := 1; s := 1;
     if n<0 then <<n := -n; if evenp x then signn := t else r := -1>>;
     pl:= !*primelist!*;
loop:
     p:=car pl; pl:=cdr pl; q:=0;
     while cdr (qr:=divide(n,p))=0 do <<n:=car qr; q:=q #+ 1>>;
     if not (q #< x) then
         <<w:=divide(q,x); r:=r*(p**car w); q:=cdr w>>;
     while q #> 0 do <<s:=s*p; q:=q #- 1>>;
     if car qr < p then << s:=n*s; goto done>>;
     if pl then goto loop;
       % heuristic bound for complete factorization.
     if 10^20 > n then
     <<q:=mcfactor!*(n,nil);
       for each j in q do
         <<w := divide(cdr j,x);
           r := car j**car w*r;
           s := car j**cdr w*s>>;
     >>
      else if (q:=iroot(n,x)) then r:=r*q
      else s:=n*s;
done:
     if signn then s := -s;
     return r . s
   end;

endmodule;

end;
