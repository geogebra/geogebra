module bfelem;  % Bigfloat elementary constants and functions.

% Last change date: 1 Jan 1993.

% Author: T. Sasaki, 1979.

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


% Modifications by: Anthony C. Hearn, Jed B. Marti, Stanley L. Kameny.
% Changed for binary arithmetic by Iain Beckingham and Rainer M. Schoepf

exports !:cbrt10, !:cbrt2, !:cbrt3, !:cbrt5, !:cbrte, !:cbrtpi,
        !:e, !:log10, !:log2, !:log3, !:log5, !:logpi, !:pi, !:sqrt10,
        !:sqrt2, !:sqrt3, !:sqrt5, !:sqrte, !:sqrtpi, acos!*,
        asin!*, atan!*, cos!*, e!*,
        exp!*, exp!:, get!:const, log!*, log!:, pi!*,
        sin!*, sqrt!:, tan!*;

imports !*q2f, abs!:, bflerrmsg, bfp!:, bfzerop!:, conv!:bf2i,
        conv!:mt, cut!:ep, cut!:mt, decimal2internal, difference!:,
        divide!:, ep!:, equal!:, geq, greaterp!:, i2bf!:, leq, lessp!:,
        lshift, make!:ibf, minus!:, minusp!:, mksq, mt!:, multd, neq,
        numr, order!:, plus!:, preci!:, quotient!:, round!:mt, simp,
        texpt!:, texpt!:any, times!:;


fluid '(!:prec!: !:bprec!: !!scls !!sclc);

global '(bfsaveprec!*);

global '(bfz!* bfhalf!* bfone!* bftwo!* bfthree!* bffive!* bften!*
         !:bf!-0!.0625 !:bf!-0!.25 !:bf0!.419921875);

% *** Tables for Elementary Function and Constant Values ***

symbolic procedure allfixp l;
   % Returns T if all of L are FIXP.
   null l or fixp car l and allfixp cdr l;

symbolic procedure read!:lnum(l);
% This function reads a long number "n" represented by a list in a way
% described below, and constructs a BIG-FLOAT representation of "n".
% L is a list of integers, the first element of which gives the order of
% "n" and all the next elements when concatenated give the mantissa of
% "n".
% **** ORDER(n)=k if 10**k <= ABS(n) < 10**(k+1).
% **** Except for the first element, all integers in L
% ****      should not begin with "0" because some
% ****      systems suppress leading zeros.
% JBM: Fix some kludgy coding here.
% JBM: Add BFSAVEPREC!* precision saver.
if not allfixp l then bflerrmsg 'read!:lnum
 else begin scalar mt, ep, k, sign, u, v, dcnt;
          mt := dcnt := 0;      %JBM
%          ep := car(u := l) + 1;   %JBM
          u := l;
          ep := add1 car u;
          sign := if minusp cadr l then -1 else 1;   %JBM
          while u:=cdr u do
            << k := length explode(v := abs car u);  %JBM
%               k := 0;  %JBM
%               while v do << k := k + 1; v := cdr v >>;  %JBM
               mt := mt * 10**k + v; %JBM
               ep := ep - k;
               dcnt := dcnt +  k;    % JBM
               if bfsaveprec!* and dcnt > bfsaveprec!* then  %JBM
                  u := '(nil) >>;     %JBM
          return decimal2internal (sign * mt, ep)
                   where !:bprec!: := msd!: mt;
    end;


%symbolic procedure bfexpt!:(u,v);
%   % Calculates u**v, including case u<0.
%   if minusp!: u
%     then multd(texpt!:any(minus!: u,v),
%                !*q2f if null numr simp list('difference,v,
%                                             '(quotient 1 2))
%                        then simp 'i
%                       else mksq(list('expt,'(minus 1),v),1))
%    else texpt!:any(u,v);

symbolic procedure exp!* u; exp!:(u,!:bprec!:);

symbolic procedure log!* u; log!:(u,!:bprec!:);

symbolic procedure sin!* u; sin!:(u,!:bprec!:);

symbolic procedure cos!* u; cos!:(u,!:bprec!:);

symbolic procedure tan!* u; tan!:(u,!:bprec!:);

symbolic procedure asin!* u; asin!:(u,!:bprec!:);

symbolic procedure acos!* u; acos!:(u,!:bprec!:);

symbolic procedure atan!* u; atan!:(u,!:bprec!:);

symbolic procedure sqrt!* u; sqrt!:(u,!:bprec!:);

symbolic procedure pi!*;
   if !:prec!:>1000 then !:bigpi !:bprec!: else !:pi !:bprec!:;

symbolic procedure e!*; !:e !:bprec!:;


%*************************************************************
%**                                                         **
%** 3-1. Elementary CONSTANTS.                              **
%**                                                         **
%*************************************************************

symbolic procedure !:pi k;
% This function calculates the value of the circular
%      constant "PI", with the precision K, by
%      using Machin's well known identity:
%         PI = 16*atan(1/5) - 4*atan(1/239).
%      Calculation is performed mainly on integers.
% K is a positive integer.
if not fixp k or k <= 0 then bflerrmsg '!:pi
 else
    begin integer k3,s,ss,m,n,x,test;  scalar u;
          u := get!:const('!:pi, k);
% The original version of this code used the string "NOT FOUND" as the
% marker value that get!;const could return. An effect of that was two
% very minor uglinesses. Firstly there will have been multiple copies of
% the string using up space, and secondly it relied on NEQ being the
% converse of EQUAL not EQ and that that then checked string contents.
% By using a symbol 'not_found there will be a very very minor improvement
% in both speed and code clarity! 
          if u neq 'not_found then return u;
          ss := n := 2 ** (k3 := k + 3) / 5;
          x := -5 ** 2;
          m := 1;
          while n neq 0 do <<n := n/x; ss := ss + n/(m := m + 2)>>;
          s := n := 2 ** k3 / 239;
          x := -239 ** 2;
          m := 1;
          while n neq 0 do << n := n / x; s := s + n / (m := m + 2) >>;
          u := round!:mt(make!:ibf(test := 16 * ss - 4 * s, - k3), k);
          save!:const('!:pi, u);
          return u;
    end;

symbolic procedure !:bigpi k;
% This function calculates the value of the circular
%      constant "PI", with the binary precision K, by the
%      arithmetic-geometric mean method.  (See,
%      R. Brent, JACM Vol.23, #2, pp.242-251(1976).)
% K is a positive integer.
% **** This function should be used only when you
% ****      need "PI" of precision higher than 1000.
if not fixp k or k <= 0 then bflerrmsg '!:bigpi
 else begin integer k7, n;  scalar dcut, half, x, y, u, v;
          u := get!:const('!:pi, k);
          if u neq 'not_found then return u;
          k7 := k + 7;
          half := bfhalf!*;    %JBM
          dcut := make!:ibf(2, - k7);
          n := 1;
          x := bfone!*;
          y := divide!:(x, !:sqrt2 k7, k7);
          u := !:bf!-0!.25;    %JBM
          while greaterp!:(abs!: difference!:(x, y), dcut) do
            << v := x;
               x := times!:(plus!:(x, y), half);
               y := sqrt!:(cut!:ep(times!:(y, v), - k7), k7);
               v := difference!:(x, v);
               v := times!:(times!:(v, v),i2bf!: n);
               u := difference!:(u, cut!:ep(v, - k7));
               n := 2*n>> ;
          v := cut!:mt(texpt!:(plus!:(x, y), 2), k7);
          u := divide!:(v, times!:(i2bf!: 4, u), k);
          save!:const('!:pi, u);
          return u;
    end;

symbolic procedure !:e k;
% This function calculates the value of "e", the base
%      of the natural logarithm, with the binary precision K,
%      by summing the Taylor series for exp(x=1).
%      Calculation is performed mainly on integers.
% K is a positive integer.
if not fixp k or k <= 0 then bflerrmsg '!:e
 else begin integer k7, ans, m, n;  scalar u;
          u := get!:const('!:e, k);
          if u neq 'not_found then return u;
          k7 := k + 7;
          m := 1;
          n := lshift (1, k7); % 2**k7
          ans := 0;
          while n neq 0  do ans := ans + (n := n / (m := m + 1));
          ans := ans + lshift (1, k7 + 1); % 2 * 2**k7
          u := round!:mt(make!:ibf(ans, - k7), k);
          save!:const('!:e2, u);
          return u;
       end;

symbolic procedure !:e0625(k);
% This function calculates exp(0.0625), the value of the
%      exponential function at the point 0.0625, with
%      the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:e0625, k);
  if u neq 'not_found then return u;
  u := exp!:(!:bf!-0!.0625, k);    %JBM
  save!:const('!:e0625, u);
  return u;
end;

symbolic procedure !:log2 k;
% This function calculates log(2), the natural
%      logarithm of 2, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:log2, k);
  if u neq 'not_found then return u;
  u := log!:(bftwo!*, k);
  save!:const('!:log2, u);
  return u;
end;

symbolic procedure !:log3 k;
% This function calculates log(3), the natural
%      logarithm of 3, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:log3, k);
  if u neq 'not_found then return u;
  u := log!:(bfthree!*, k);
  save!:const('!:log3, u);
  return u;
end;

symbolic procedure !:log5 k;
% This function calculates log(5), the natural
%      logarithm of 5, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:log5, k);
  if u neq 'not_found then return u;
  u := log!:(bffive!*, k);
  save!:const('!:log5, u);
  return u;
end;

symbolic procedure !:log10 k;
% This function calculates log(10), the natural
%      logarithm of 10, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:log10,  k);
  if u neq 'not_found then return u;
  u := log!:(bften!*, k);
  save!:const('!:log10, u);
  return u;
end;

symbolic procedure !:logpi k;
% This function calculates log(PI), the natural
%      logarithm of "PI", with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:logpi, k);
  if u neq 'not_found then return u;
  u := log!:(!:pi(k + 2), k);
  save!:const('!:logpi, u);
  return u
end;

symbolic procedure !:sqrt2(k);
% This function calculates SQRT(2), the square root
%      of 2, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:sqrt2, k);
  if u neq 'not_found then return u;
  u := sqrt!:(bftwo!*, k);
  save!:const('!:sqrt2, u);
  return u;
end;

symbolic procedure !:sqrt3(k);
% This function calculates SQRT(3), the square root
%      of 3, with the precision K.
% K is a positive integer.
begin scalar u;
  u:=get!:const('!:sqrt3, k);
  if u neq 'not_found then return u;
  u := sqrt!:(bfthree!*, k);
  save!:const('!:sqrt3, u);
  return u;
end;

symbolic procedure !:sqrt5 k;
% This function calculates SQRT(5), the square root
%      of 5, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:sqrt5, k);
  if u neq 'not_found then return u;
  u := sqrt!:(bffive!*, k);
  save!:const('!:sqrt5, u);
  return u;
end;

symbolic procedure !:sqrt10 k;
% This function calculates SQRT(10), the square root
%      of 10, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:sqrt10, k);
  if u neq 'not_found then return u;
  u := sqrt!:(bften!*, k);
  save!:const('!:sqrt10, u);
  return u;
end;

symbolic procedure !:sqrtpi k;
% This function calculates SQRT(PI), the square root
%      of "PI", with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:sqrtpi, k);
  if u neq 'not_found then return u;
  u := sqrt!:(!:pi(k + 2), k);
  save!:const('!:sqrtpi, u);
  return u;
end;

symbolic procedure !:sqrte k;
% This function calculates SQRT(e), the square root
%      of "e", with the precision K.
% K is a positive integer.
begin scalar u;
  u:=get!:const('!:sqrte, k);
  if u neq 'not_found then return u;
  u := sqrt!:(!:e(k + 2), k);
  save!:const('!:sqrte, u);
  return u;
end;

symbolic procedure !:cbrt2 k;
% This function calculates CBRT(2), the cube root
%      of 2, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrt2, k);
  if u neq 'not_found then return u;
  u := cbrt!:(bftwo!*, k);
  save!:const('!:cbrt2, u);
  return u;
end;

symbolic procedure !:cbrt3 k;
% This function calculates CBRT(3), the cube root
%      of 3, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrt3, k);
  if u neq 'not_found then return u;
  u := cbrt!:(bfthree!*, k);
  save!:const('!:cbrt3, u);
  return u;
end;

symbolic procedure !:cbrt5 k;
% This function calculates CBRT(5), the cube root
%    of 5, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrt5, k);
  if u neq 'not_found then return u;
  u := cbrt!:(bffive!*, k);
  save!:const('!:cbrt5, u);
  return u;
end;

symbolic procedure !:cbrt10 k;
% This function calculates CBRT(10), the cube root
%      of 10, with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrt10, k);
  if u neq 'not_found then return u;
  u := cbrt!:(bften!*, k);
  save!:const('!:cbrt10, u);
  return u;
end;

symbolic procedure !:cbrtpi k;
% This function calculates CBRT(PI), the cube root
%      of "PI", with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrtpi, k);
  if u neq 'not_found then return u;
  u := cbrt!:(!:pi(k + 2), k);
  save!:const('!:cbrtpi, u);
  return u;
end;

symbolic procedure !:cbrte k;
% This function calculates CBRT(e), the cube root
%      of "e", with the precision K.
% K is a positive integer.
begin scalar u;
  u := get!:const('!:cbrte, k);
  if u neq 'not_found then return u;
  u := cbrt!:(!:e(k + 2), k);
  save!:const('!:cbrte, u);
  return u;
end;

%*************************************************************
%**                                                         **
%** 3-2. Routines for saving CONSTANTS.                     **
%**                                                         **
%*************************************************************

symbolic procedure get!:const(cnst, k);
% This function returns the value of constant CNST
%      of the precision K, if it was calculated
%      previously with, at least, the precision K,
%      else it returns 'not_found.
% CNST is the name of the constant (to be quoted).
% K is a positive integer.
  if atom cnst and fixp k and k > 0 then
    begin scalar u;
          u := get(cnst, 'save!:c);
          if null u or car u < k then return 'not_found
           else if car u = k then return cdr u
                             else return round!:mt(cdr u, k);
    end
   else bflerrmsg 'get!:const$

symbolic procedure save!:const(cnst, nmbr);
% This function saves the value of constant CNST
%      for the later use.
% CNST is the name of the constant (to be quoted).
% NMBR is a BIG-FLOAT representation of the value.
  if atom cnst and bfp!: nmbr then
       put(cnst, 'save!:c, preci!: nmbr . nmbr)
   else bflerrmsg 'save!:const$

symbolic procedure set!:const(cnst, l);
% This function sets the value of constant CNST.
% CNST is the name of the constant (to be quoted).
% L is a list of integers, which represents the
%      value of the constant in the way described
%      in the function READ!:LNUM.
     save!:const(cnst, read!:lnum l)$


% Setting the constants.

set!:const( '!:pi    , '( 0   3141 59265 35897 93238 46264
     33832 79502 88419 71693 99375 105820 9749 44592 30781
     64062 86208 99862 80348 25342 11706 79821 48086 51328
     23066 47093 84460 95505 82231 72535 94081 28481 1174
    5028410 2701 93852 11055 59644 62294 89549 30381 96442
     88109 8) )$

set!:const( '!:e     , '( 0   2718 28182 84590 45235 36028
     74713 52662 49775 72470 93699 95957 49669 67627 72407
     66303 53547 59457 13821 78525 16642 74274 66391 93200
     30599 21817 41359 66290 43572 90033 42952 60595 63073
     81323 28627 943490 7632 33829 88075 31952 510190 1157
     38341 9) )$

set!:const( '!:e0625   , '( 0 1064 49445 89178 59429 563390
      5946 42889 673100 7254 43649 35330 151930 7510 63556
     39368 2816600 633 42934 35506 87662 43755 1) );

set!:const( '!:log2  , '(-1   6931 47180 55994 53094 17232
     12145 81765 68075 50013 43602 55254 1206 800094 93393
     62196 96947 15605 86332 69964 18687 54200 2) )$

set!:const( '!:log3  , '( 0   1098 61228 866810 9691 39524
     52369 22525 70464 74905 57822 74945 17346 94333 63749
     42932 18608 96687 36157 54813 73208 87879 7) )$

set!:const( '!:log5  , '( 0   1609 43791 2434100 374 60075
     93332 26187 63952 56013 54268 51772 19126 47891 47417
     898770 7657 764630 1338 78093 179610 7999 7) )$

set!:const( '!:log10 , '( 0   2302 58509 29940 456840 1799
     14546 84364 20760 11014 88628 77297 60333 27900 96757
     26096 77352 48023 599720 5089 59829 83419 7) )$

set!:const( '!:logpi , '( 0   1144 72988 5849400 174 14342
     73513 53058 71164 72948 12915 31157 15136 23071 47213
     77698 848260 7978 36232 70275 48970 77020 1) )$

set!:const( '!:sqrt2 , '( 0   1414 21356 23730 95048 80168
     872420 96980 7856 96718 75376 94807 31766 79737 99073
     24784 621070 38850 3875 34327 64157 27350 1) )$

set!:const( '!:sqrt3 , '( 0   17320 5080 75688 77293 52744
     634150 5872 36694 28052 53810 38062 805580 6979 45193
     301690 88000 3708 11461 86757 24857 56756 3) )$

set!:const( '!:sqrt5 , '( 0   22360 6797 74997 89696 40917
     36687 31276 235440 6183 59611 52572 42708 97245 4105
    209256 37804 89941 441440 8378 78227 49695 1) )$

set!:const( '!:sqrt10, '( 0   3162 277660 1683 79331 99889
     35444 32718 53371 95551 39325 21682 685750 4852 79259
     44386 39238 22134 424810 8379 30029 51873 47))$

set!:const( '!:sqrtpi, '( 0   1772 453850 9055 16027 29816
     74833 41145 18279 75494 56122 38712 821380 7789 85291
     12845 91032 18137 49506 56738 54466 54162 3) )$

set!:const( '!:sqrte , '( 0   1648 721270 7001 28146 8486
    507878 14163 57165 3776100 710 14801 15750 79311 64066
     10211 94215 60863 27765 20056 36664 30028 7) )$

set!:const( '!:cbrt2 , '( 0   1259 92104 98948 73164 7672
    106072 78228 350570 2514 64701 5079800 819 75112 15529
     96765 13959 48372 93965 62436 25509 41543 1) )$

set!:const( '!:cbrt3 , '( 0   1442 249570 30740 8382 32163
     83107 80109 58839 18692 53499 35057 75464 16194 54168
     75968 29997 33985 47554 79705 64525 66868 4) )$

set!:const( '!:cbrt5 , '( 0   1709 97594 66766 96989 35310
     88725 43860 10986 80551 105430 5492 43828 61707 44429
     592050 4173 21625 71870 10020 18900 220450 ) )$

set!:const( '!:cbrt10, '( 0   2154 4346900 318 83721 75929
     35665 19350 49525 93449 42192 10858 24892 35506 34641
     11066 48340 80018 544150 3543 24327 61012 6) )$

set!:const( '!:cbrtpi, '( 0   1464 59188 75615 232630 2014
     25272 63790 39173 85968 55627 93717 43572 55937 13839
     36497 98286 26614 56820 67820 353820 89750 ) )$

set!:const( '!:cbrte , '( 0   1395 61242 50860 89528 62812
     531960 2586 83759 79065 15199 40698 26175 167060 3173
     90156 45951 84696 97888 17295 83022 41352 1) )$


%*************************************************************
%**                                                         **
%** 4-1. Elementary FUNCTIONS.                              **
%**                                                         **
%*************************************************************

symbolic procedure sqrt!:(x, k);
% This function calculates SQRT(x), the square root
%      of "x", with the binary precision K, by Newton's
%      iteration method.
% X is a BIG-FLOAT representation of "x", x >= 0.
% K is a positive integer.
  if minusp!: x or not fixp k or k <= 0 then bflerrmsg 'sqrt!:
    else if bfzerop!: x then bfz!*
    else begin integer k7,ncut,nfig;  scalar dcut,half,dy,y,y0,u;
          k7 := k + 7;
          ncut := k7 - (order!: x + 1) / 2;
          half := bfhalf!*;    %JBM
          dcut := make!:ibf(2, - ncut);
          dy := make!:ibf(4, - ncut);
          %y0 := incprec!:(x,1);
          y0 := conv!:mt(x, 2);
          if remainder(ep!: y0, 2) = 0 then
                y0 := make!:ibf((2 + 3 * mt!: y0)/5,  ep!: y0/2)
           else y0 := make!:ibf((9 + 5 * mt!: y0)/10, (ep!: y0 - 1)/2);
          nfig := 1;
          while nfig < k7 or greaterp!:(abs!: dy, dcut) do
            << if (nfig := 2 * nfig) > k7 then nfig := k7;
               u := divide!:(x, y0, nfig);
               y := times!:(plus!:(y0, u), half);
               dy := difference!:(y, y0);
               y0 := y >>;
          return round!:mt(y, k);
    end;


symbolic procedure cbrt!:(x, k);
% This function calculates CBRT(x), the cube root
%      of "x", with the binary precision K, by Newton's
%      iteration method.
% X is a BIG-FLOAT representation of any real "x".
% K is a positive integer.
   if not fixp k or k <= 0 then bflerrmsg 'cbrt!:
   else if bfzerop!: x then bfz!*
   else if minusp!: x then minus!: cbrt!:(minus!: x, k)
   else begin integer k7, ncut, nfig, j;  scalar dcut, thre, dy, y, u;
          k7 := k + 7;
          ncut := k7 - (order!: x + 2) / 3;
          thre := bfthree!*;
          dcut := make!:ibf(2, - ncut);
          dy := make!:ibf(4, - ncut);
          y := conv!:mt(x, 3);
          if (j := remainder(ep!: y, 3)) = 0 then
               y := make!:ibf((12 + mt!: y ) / 10, ep!: y / 3)
           else if j = 1 or j = -2 then
                y := make!:ibf((17 + 4 * mt!: y)/16, (ep!: y - 1)/3)
           else y := make!:ibf((15 + 4 * mt!: y)/12, (ep!: y - 2)/3);
          nfig := 1;
          while nfig < k7 or greaterp!:(abs!: dy, dcut) do
            << if (nfig := 2 * nfig) > k7 then nfig := k7;
               u := cut!:mt(times!:(y, y), nfig);
               u := divide!:(x, u, nfig);
               j := order!:(u := difference!:(u, y)) + ncut - k7;
               dy := divide!:(u, thre, max(1, nfig + j));
               y := plus!:(y, dy) >>;
          return round!:mt(y, k);
    end;


symbolic procedure exp!:(x, k);
% This function calculates exp(x), the value of
%      the exponential function at the point "x",
%      with the binary precision K, by summing terms of
%      the Taylor series for exp(z), 0 < z < 1.
% X is a BINARY BIG-FLOAT representation of any real "x".
% K is a positive integer.
   if not fixp k or k <= 0 then bflerrmsg 'exp!:
   else if bfzerop!: x then bfone!*
   else begin integer k7, m;  scalar q, r, y, yq, yr;
          q := i2bf!:(m := conv!:bf2i(y := abs!: x));
          r := difference!:(y, q);
          k7 := k + msd!: m + 7;
          r := difference!:(y, q);
          if bfzerop!: q then yq := bfone!*
           else (yq := texpt!:(!:e k7, m) where !:bprec!: := k7);
          if bfzerop!: r then yr:=bfone!*
           else begin integer j, n;  scalar dcut, fctrial, ri, tm;
              dcut := make!:ibf(2, - k7);
              yr := ri := tm := bfone!*;
              m := 1;
              j := 0;
              while greaterp!:(tm, dcut) do
                << fctrial := i2bf!:(m := m * (j := j + 1));
                   ri := cut!:ep(times!:(ri, r), - k7);
                   n := max(1, k7 - order!: fctrial + order!: ri);
                   tm := divide!:(ri, fctrial, n);
                   yr := plus!:(yr,tm);
                   if remainder(j,10)=0 then yr := cut!:ep(yr, - k7) >>;
        end;
          y := cut!:mt(times!:(yq, yr), k + 1);
          return (if minusp!: x then divide!:(bfone!*, y, k)
                  else round!:mt (y,k));
    end;


symbolic procedure log!:(x, k);
% This function calculates log(x), the value of the
%      logarithmic function at the point "x", with
%      the precision K, by summing terms of the
%      Taylor series for log(1+z), 0 < z < 0.10518.
% X is a BIG-FLOAT representation of "x", x > 0.
% K is a positive integer.
   if minusp!: x or bfzerop!: x or
     not fixp k or k <= 0 then bflerrmsg 'log!:
   else if equal!:(x,bfone!*) then bfz!*
   else begin integer k7,m;  scalar eee,es,sign,l,y,z;
          k7 := k + 7;
          eee := !:e k7;
          es := !:e0625 k7;
          if greaterp!:(x, bfone!*) then << sign := bfone!*; y := x >>
           else <<sign := minus!: bfone!*;
                  y := divide!:(bfone!*, x, k7)>>;
          if lessp!:(y, eee) then << m := 0; z := y >>
           else << if (m := (order!: y * 69) / 100) = 0 then z := y
                    else (z := divide!:(y, texpt!:(eee, m), k7)
                             where !:bprec!: := k7);
                    while greaterp!:(z, eee) do
                       << m := m+1; z := divide!:(z, eee, k7) >> >>;
          l := i2bf!: m;
          y := !:bf!-0!.0625;
          while greaterp!:(z, es) do
            << l := plus!:(l, y); z := divide!:(z, es, k7) >>;
          z := difference!:(z, bfone!*);
        begin integer n;  scalar dcut, tm, zi;
              y := tm := zi := z;
              z := minus!: z;
              dcut := make!:ibf(2, - k7);
              m := 1;
              while greaterp!:(abs!: tm, dcut) do
               << zi := cut!:ep(times!:(zi, z), - k7);
                  n := max(1, k7 + order!: zi);
                  tm := divide!:(zi,i2bf!:(m := m + 1), n);
                  y := plus!:(y, tm);
                  if zerop remainder(m,10) then y := cut!:ep(y,-k7)>>;
        end;
          y := plus!:(y, l);
          return round!:mt(times!:(sign, y), k);
    end;


symbolic procedure sin!:(x, k);
% This function calculates sin(x), the value of
%      the sine function at the point "x", with
%      the binary precision K, by summing terms of the
%      Taylor series for sin(z), 0 < z < PI/4.
% X is a BIG-FLOAT representation of any real "x".
% K is a positive integer.  (revised SLK)                         %<===
   if not fixp k or k <= 0 then bflerrmsg 'sin!:
   else if bfzerop!: x then bfz!*
   else if minusp!: x then minus!: sin!:(minus!: x, k)
   else begin integer k7, m;  scalar pi4, sign, q, r, y, !!scls;  %<===
           k7 := k + 7;
          m := preci!: x;
          pi4 := times!:(!:pi(k7 + m), !:bf!-0!.25);
          if lessp!:(x, pi4) then << m := 0; r := x >>
           else << m := conv!:bf2i(q := quotient!:(x, pi4));
                   r := difference!:(x, times!:(q, pi4)) >>;
          sign := bfone!*;
          if m >= 8 then m := remainder(m, 8);
          if m >= 4 then << sign := minus!: sign; m := m - 4>>;
          if m = 0 then <<!!scls := x; go to sn>>                  %<===
           else if onep m then go to m1
           else if m = 2 then go to m2
           else go to m3;;
      m1: r := cut!:mt(difference!:(pi4, r), k7);
          return times!:(sign, cos!:(r, k));
      m2: r := cut!:mt(r, k7);
          return times!:(sign, cos!:(r, k));
      m3: r := cut!:mt(difference!:(pi4, r), k7); !!scls := x;     %<===
    sn: x := if !!sclc then !!sclc else !!scls;                    %<===
        if x and lessp!:(r,times!:(x,make!:ibf(1, 3 - k)))         %<===
          then return bfz!* else                                   %<===
        begin integer j, n, ncut;  scalar dcut, fctrial, ri, tm;
              ncut := k7 - min(0, order!: r + 1);
              dcut := make!:ibf(2, - ncut);
              y := ri := tm := r;
              r := minus!: cut!:ep(times!:(r, r), - ncut);
              m := j := 1;
              while greaterp!:(abs!: tm, dcut) do
               << j := j + 2;
                  fctrial := i2bf!:(m := m*j*(j - 1));
                  ri := cut!:ep(times!:(ri, r), - ncut);
                  n := max(1,k7 - order!: fctrial + order!: ri);
                  tm := divide!:(ri, fctrial, n);
                  y := plus!:(y, tm);
                  if zerop remainder(j,20) then y := cut!:ep(y,-ncut)>>;
        end;
          return round!:mt(times!:(sign, y), k);
    end;

symbolic procedure cos!:(x, k);
% This function calculates cos(x), the value of
%      the cosine function at the point "x", with
%      the binary precision K, by summing terms of the
%      Taylor series for cos(z), 0 < z < PI/4.
% X is a BIG-FLOAT representation of any real "x".
% K is a positive integer.  (revised SLK)                          %<===
   if not fixp k or k <= 0 then bflerrmsg 'cos!:
    else if bfzerop!: x then bfone!*
    else if minusp!: x then cos!:(minus!: x, k)
    else begin integer k7, m;  scalar pi4, sign, q, r, y, !!sclc;  %<===
          k7 := k + 7;
          m := preci!: x;
          pi4 := times!:(!:pi(k7 + m), !:bf!-0!.25);
          if lessp!:(x, pi4) then << m := 0; r := x >>
           else << m := conv!:bf2i(q := quotient!:(x, pi4));
                   r := difference!:(x, times!:(q, pi4)) >>;
          sign := bfone!*;
          if m >= 8 then m := remainder(m, 8);
          if m >= 4 then << sign := minus!: sign; m := m - 4 >>;
          if m >= 2 then sign := minus!: sign;
          if m = 0 then go to cs
           else if m = 1 then go to m1
           else if m = 2 then go to m2
           else go to m3;
      m1: r := cut!:mt(difference!:(pi4, r), k7); !!sclc := x;     %<===
          return times!:(sign, sin!:(r, k));
      m2: r := cut!:mt(r, k7); !!sclc := x;                        %<===
          return times!:(sign, sin!:(r, k));
      m3: r := cut!:mt(difference!:(pi4, r), k7);
    cs: begin integer j, n;  scalar dcut, fctrial, ri, tm;
              dcut := make!:ibf(2, - k7);
              y := ri := tm := bfone!*;
              r := minus!: cut!:ep(times!:(r, r), - k7);
              m := 1;
              j := 0;
              while greaterp!:(abs!: tm, dcut) do
               << j := j + 2;
                  fctrial := i2bf!:(m := m * j * (j - 1));
                  ri := cut!:ep(times!:(ri, r), - k7);
                  n := max(1, k7 - order!: fctrial + order!: ri);
                  tm := divide!:(ri, fctrial, n);
                  y := plus!:(y, tm);
                  if zerop remainder(j,20) then y := cut!:ep(y,-k7)>>;
        end;
          return round!:mt(times!:(sign, y), k);
    end;

symbolic procedure tan!:(x, k);
% This function calculates tan(x), the value of
%      the tangent function at the point "x",
%      with the binary precision K, by calculating
%         sin(x)  or  cos(x) = sin(PI/2-x).
% X is a BIG-FLOAT representation of any real "x",
% K is a positive integer.
   if not fixp k or k <= 0 then bflerrmsg 'tan!:
   else if bfzerop!: x then bfz!*
   else if minusp!: x then minus!: tan!:(minus!: x, k)
   else begin integer k7, m;  scalar pi4, sign, q, r;
          k7 := k + 7;
          m := preci!: x;
          pi4 := times!:(!:pi(k7 + m), !:bf!-0!.25);
          if lessp!:(x, pi4) then << m := 0; r := x >>
           else << m := conv!:bf2i(q := quotient!:(x, pi4));
                   r := difference!:(x, times!:(q, pi4)) >>;
          if m >= 4 then m := remainder(m, 4);
          if m >= 2 then sign := minus!: bfone!* else sign := bfone!*;
          if m = 1 or m = 3 then r := difference!:(pi4, r);
          r := cut!:mt(r, k7);
          if m = 0 or m = 3 then go to m03 else go to m12;
     m03: r := sin!:(r, k7);
          q := difference!:(bfone!*, times!:(r, r));
          q := sqrt!:(cut!:mt(q, k7), k7);
          return times!:(sign, divide!:(r, q, k));
     m12: r := sin!:(r, k7);
          q := difference!:(bfone!*, times!:(r, r));
          q := sqrt!:(cut!:mt(q, k7), k7);
          return times!:(sign, divide!:(q, r, k));
    end;

symbolic procedure asin!:(x, k);
% This function calculates asin(x), the value of
%      the arcsine function at the point "x",
%      with the binary precision K, by calculating
%         atan(x/SQRT(1-x**2))  by ATAN!:.
%      The answer is in the range [-PI/2 , PI/2].
% X is a BIG-FLOAT representation of "x", IxI <= 1;
% K is a positive integer.
   if greaterp!:(abs!: x, bfone!*) or
     not fixp k or k <= 0 then bflerrmsg 'asin!:
   else if minusp!: x then minus!: asin!:(minus!: x, k)
   else begin integer k7;  scalar y;
          k7 := k + 7;
          if lessp!:(difference!:(bfone!*, x), make!:ibf(2, - k7))
            then return round!:mt(times!:(!:pi add1 k,bfhalf!*),k);
                 %JBM
          y := cut!:mt(difference!:(bfone!*, times!:(x, x)), k7);
          y := divide!:(x, sqrt!:(y, k7), k7);
          return atan!:(y, k);
    end;

symbolic procedure acos!:(x, k);
% This function calculates acos(x), the value of
%      the arccosine function at the point "x",
%      with the precision K, by calculating
%         atan(SQRT(1-x**2)/x)  if  x > 0  or
%         atan(SQRT(1-x**2)/x) + PI  if  x < 0.
%      The answer is in the range [0 , PI].
% X is a BIG-FLOAT representation of "x", IxI <= 1.
% K is a positive integer.
   if greaterp!:(abs!: x, bfone!*) or
     not fixp k or k <= 0 then bflerrmsg 'acos!:
   else begin integer k7;  scalar y;
          k7 := k + 7;
          if lessp!:(abs!: x, make!:ibf(2, - k7))  %%%%% 5 * base = 5*2
           then return round!:mt(times!:(!:pi add1 k,bfhalf!*),k);
                %JBM
          y := difference!:(bfone!*, times!:(x, x));
          y := cut!:mt(y, k7);
          y := divide!:(sqrt!:(y, k7), abs!: x, k7);
          return (if minusp!: x then
                   round!:mt(difference!:(!:pi(k + 1), atan!:(y, k)), k)
                  else atan!:(y, k) );
    end;

symbolic procedure atan!:(x, k);
% This function calculates atan(x), the value of the
%      arctangent function at the point "x", with
%      the precision K, by summing terms of the
%      Taylor series for atan(z)  if  0 < z < 0.419921875.
%      Otherwise the following identities are used:
%         atan(x) = PI/2 - atan(1/x)  if  1 < x  and
%         atan(x) = 2*atan(x/(1+SQRT(1+x**2)))
%            if  0.419921875 <= x <= 1.
%      The answer is in the range [-PI/2 , PI/2].
% X is a BIG-FLOAT representation of any real "x".
% K is a positive integer.
   if not fixp k or k <= 0 then bflerrmsg 'atan!:
   else if bfzerop!: x then bfz!*
   else if minusp!: x then minus!: atan!:(minus!: x, k)
   else begin integer k7;  scalar pi4, y, z;
          k7 := k + 7;
          pi4 := times!:(!:pi k7, !:bf!-0!.25);    %JBM
          if equal!:(x, bfone!*) then return round!:mt(pi4, k);
          if greaterp!:(x, bfone!*) then return
           round!:mt(difference!:(plus!:(pi4, pi4),
                               atan!:(divide!:(bfone!*,x,k7),k + 1)),k);
          if lessp!:(x, !:bf0!.419921875) then go to at;
          y := plus!:(bfone!*, cut!:mt(times!:(x, x), k7));
          y := plus!:(bfone!*, sqrt!:(y, k7));
          y := atan!:(divide!:(x, y, k7), k + 1);
          return round!:mt(times!:(y, bftwo!*), k);
    at: begin integer m, n, ncut;  scalar dcut, tm, zi;
              ncut := k7 - min(0, order!: x + 1);
              y := tm := zi := x;
              z := minus!: cut!:ep(times!:(x, x), - ncut);
              dcut := make!:ibf(2, - ncut);
              m := 1;
              while greaterp!:(abs!: tm, dcut) do
               << zi := cut!:ep(times!:(zi, z), - ncut);
                  n := max(1, k7 + order!: zi);
                  tm := divide!:(zi, i2bf!:(m := m + 2), n);
                  y := plus!:(y, tm);
                  if zerop remainder(m,20) then y := cut!:ep(y,-ncut)>>;
        end;
          return round!:mt(y, k)
    end;

endmodule;


end;
