module smlbflot;   % Basic support for bigfloat arithmetic.

% Authors: S.L. Kameny and T. Sasaki.
% Modified for binary bigfloat arithmetic by Iain Beckingham and Rainer
% Schoepf.
% Modified for double precision printing by Herbert Melenk.
% Modified to allow *very* large numbers to be compressed (for PSL) by
% Winfried Neun.

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


% Last change made Oct 6, 1999.

exports abs!:, bfexplode0, bflerrmsg, bfprin!:, bftrim!:, bfzerop!:,
        conv!:mt, cut!:ep, cut!:mt, decimal2internal, decprec!:,
        difference!:, divide!:, equal!:, fl2bf, greaterp!:, incprec!:,
        leq!:, lessp!:, max!:, max!:, max2!:, min!:, min2!:, minus!:,
        minusp!:, order!:, plus!:, read!:num, round!:mt, round!:last,
        times!:;

imports aconc, ashift, bfp!:, ceiling, conv!:bf2i, ep!:, eqcar, floor,
        geq, i2bf!:, leq, lshift, make!:ibf, msd!:, mt!:, neq, normbf,
        oddintp, preci!:, precision, prin2!*, rerror, retag, reversip;

fluid '(!*bfspace !*fullprec !*nat !:prec!: !:bprec!: !:print!-prec!:
        !:upper!-sci!: !:lower!-sci!:);

global '(!!nfpd !!nbfpd bften!* bfz!* fort_exponent);

switch bfspace,fullprec;

flag('(fort_exponent),'share);

!*bfspace := nil;

% !*fullprec := t;

!:lower!-sci!: := 10;

!:upper!-sci!: := 5;

symbolic procedure bflerrmsg u;
   % Revised error message for BFLOAT module, using error, not rederr.
   error(0,{"Invalid argument to",u});

symbolic procedure bfzerop!: u;
   % This is possibly too restricted a definition.
   mt!: u = 0;

symbolic procedure fl2bf x;
  (if zerop x then bfz!* else
   begin scalar s,r; integer d;
     if x<0 then <<x := -x; s := t>>;
     % convert x to an integer equivalent;
      r := normbf read!:num x;
      d := ep!: r+msd!: mt!: r;
      x := x*2.0**-d; x := x + 0.5/2.0**!:bprec!:;
      x := fix(x*2.0**!:bprec!:);
      return make!:ibf (if s then -x else x, d - !:bprec!:) end)
        where !:bprec!:=!!nbfpd;

symbolic procedure bfprin!: u;
%  if preci!: u>!!nbfpd then bfprin0 u
%   else (bfprin0 u where !*bfspace=nil);
   bfprin0 u;

symbolic procedure divide!-by!-power!-of!-ten (x, n);
  if n < 0 then bflerrmsg 'divide!-by!-power!-of!-ten
   else <<
     while n > 0 do <<
       if oddintp n then x := normbf divide!: (x, f, !:bprec!:);
       n := lshift (n, -1);
       f := normbf cut!:mt (times!: (f, f), !:bprec!:) >>;
     x >> where f := bften!*;

symbolic procedure multiply!-by!-power!-of!-ten (x, n);
  if n < 0 then bflerrmsg 'multiply!-by!-power!-of!-ten
   else <<
     while n > 0 do <<
       if oddintp n then x := normbf times!: (x, f);
       n := lshift (n, -1);
       f := normbf cut!:mt (times!: (f, f), !:bprec!:) >>;
     normbf cut!:mt (x, !:bprec!:) >> where f := bften!*;

global '(log2of10);

symbolic procedure round!:dec (x, p);
  %
  % rounds bigfloat x to p decimal places
  %
  begin scalar setpr; integer m, ex;
    if null p then p := if !:print!-prec!: then !:print!-prec!:
                         else !:prec!: - 2
     else if p > precision 0 then setpr := precision p;
    x := round!:dec1 (x,p);
    m := car x; ex := cdr x;
    x := i2bf!: m;
    if ex < 0 then x := divide!-by!-power!-of!-ten (x, -ex)
     else if ex > 0 then x := multiply!-by!-power!-of!-ten (x, ex);
    if setpr then precision setpr;
    return round!:mt (x, ceiling (p * log2of10))
  end;

symbolic procedure round!:dec1 (x, p);
  %
  % rounds bigfloat x to p decimal places
  % returns pair (m . ex) of mantissa and exponent to base 10,
  % m having exactly p digits
  % performs all calculations at at least current precision,
  % but increases the precision of the calculations to log10(x)
  % if this is larger
  %
  if bfzerop!: x then cdr x
   else (begin scalar exact, lo, sign; integer ex, k, m, n, l;
    %
    % We need to calculate the number k so that 10^(k+1) > |x| >= 10^k
    %  k = floor (log10 |x|) = floor (log2 |x| / log2of10);
    % We can easily compute n so that 2^(n+1) > |x| >= 2^n,
    %  i.e., n = floor (log2 |x|), since this is just order!:(x).
    % Since n+1 > log2 |x| >= n, it follows that
    %  floor ((n+1) / log2of10) >= k >= floor (n / log2of10)
    % I.e., if both bounds agree, we know k, otherwise we have to check.
    %
    if mt!: x < 0 then <<sign := t; x := minus!: x>>;
    n := order!: x;
    %
    % The division by log2of10 has to be done with precision larger than
    % the precision of n. In particular, log2of10 has to be calculated
    % to a larger precision.  Instead of dividing by log2of10, we
    % multiply by log10of2.
    %
    l := msd!: abs n;
    <<lo := divide!: (!:log2 !:bprec!:, !:log10 !:bprec!:, !:bprec!:);
      k := conv!:bf2i times!: (i2bf!: n, lo);
      if k = conv!:bf2i times!: (i2bf!: (n+1), lo) then exact := t>>
        where !:bprec!: := max (!!nbfpd, l + 7);
    %
    % For the following calculation the precision must be increased by
    % the precision of n. The is necessary to ensure that the mantissa
    % is calculated correctly for large values of the exponent. This is
    % due to the fact that if we multiply the number x by 10^n its
    % precision will be decreased by n.
    %
    !:bprec!: := !:bprec!: + l;
    %
    % since conv!:bf2i rounds always towards 0, we must correct for n<0
    %
    if n < 0 then k := k - 1;
    ex := k - p + 1;
    if ex < 0 then x := multiply!-by!-power!-of!-ten (x, -ex)
     else if ex > 0 then x := divide!-by!-power!-of!-ten (x, ex);
    if exact then nil
     else <<l := length explode conv!:bf2i x;
            if l < p then <<x := times!: (x, bften!*); ex := ex - 1>>
             else if l > p then <<x := divide!: (x, bften!*, !:bprec!:);
                                  ex := ex + 1>>>>;
    %
    % do rounding
    %
    x := plus!:(x, bfhalf!*);
    % Add an "epsilon" just to be sure (e.g., for on complex,rounded;
    % print_precision 15; 3.23456789012345+7).
    x := plus!:(x,fl2bf(0.1^18));
    m := conv!:bf2i x;
    if length explode m > p then <<m := (m + 5) / 10; ex := ex + 1>>;
    if sign then m := -m;
    return (m . ex);
  end) where !:bprec!: := !:bprec!:;

% symbolic procedure internal2decimal (x, p);
  %
  % converts bigfloat x to decimal format, with precision p
  % Result is a pair (m . e), so that x = m*10^e, with
  % m having exactly p decimal digits.
  % Calculation is done with the current precision,
  % but at least with p + 2.
  %
%   begin scalar setpr;
%     if null p then p := if !:print!-prec!: then !:print!-prec!:
%                          else !:prec!: - 2
%      else if p > precision 0 then setpr := precision p;
%     x := round!:dec1 (x,p);
%     if setpr then precision setpr;
%     return x
%   end;


symbolic procedure bfprin0 u;
  begin scalar r; integer m, ex;
    r := round!:dec1 (u, if !:print!-prec!: then !:print!-prec!:
                          else !:prec!: - 2);
    m := car r; ex := cdr r;
    bfprin0x (m, ex)
  end;

symbolic procedure bfprin0x(m,ex);
  begin scalar lst; integer dotpos;
    lst := bfexplode0x(m,ex);
    ex := cadr lst;
    dotpos := caddr lst;
    lst := car lst;
    return bfprin!:lst (lst,ex,dotpos)
  end;

symbolic procedure bfexplode0 u;
  % returns a list (lst ex dotpos) where
  %  lst    = list of characters in mantissa
  %            (ie optional sign and digits)
  %  ex     = decimal exponent
  %  dotpos = position of decimal point in lst
  %            (note that the sign is counted)
  begin scalar r; integer m, ex;
    r := round!:dec1 (u,if !:print!-prec!: then !:print!-prec!:
                         else !:prec!: - 2);
    m := car r; ex := cdr r;
    return bfexplode0x (m, ex)
  end;


symbolic procedure bfexplode0x (m, ex);
  begin scalar lst, s; integer dotpos, l;
    if m<0 then <<s := t; m := -m>>;
    lst := explode m;
    l := length lst;
    if ex neq 0 and (l+ex < -!:lower!-sci!: or l+ex > !:upper!-sci!:)
      then <<dotpos := 1;
             ex := ex + l - 1>>
     else <<dotpos := l + ex;
            ex := 0;
            if dotpos > l - 1
              %
              % add dotpos - l + 1 zeroes at the end
              %
              then lst := nconc!*(lst,nlist('!0,dotpos - l + 1))
             else while dotpos < 1 do <<lst := '!0 . lst;
                                        dotpos := dotpos + 1>>>>;
    if s then <<lst := '!- . lst; dotpos := dotpos + 1>>;
    if null !*fullprec
      then <<lst := reversip lst;
             while lst and car lst eq '!0 and length lst > dotpos + 1 do
                lst := cdr lst;
             lst := reversip lst>>;
    return {lst, ex, dotpos}
  end;

symbolic procedure bfprin!:lst (lst, ex, dotpos);
  begin scalar result,ee,w; integer j;
    ee:='E;
    if !*fort and liter(w:=reval fort_exponent) then ee:=w else w:=nil;
    if car lst eq '!- and dotpos = 1 then <<dotpos := 2; ex := ex - 1>>;
    if ex neq 0 then if car lst eq '!- then <<ex := ex + dotpos - 2;
                                              dotpos := 2>>
                      else <<ex := ex + dotpos - 1; dotpos := 1>>
     else if dotpos = length lst then dotpos := -1;
    for each char in lst do <<
      result := char . result; j := j + 1; dotpos := dotpos - 1;
      if j=5 then <<if !*nat and !*bfspace then result := '!  . result;
                    j := 0>>;
      if dotpos = 0 then <<result := '!. . result; j := j + 1>>;
      if j=5 then <<if !*nat and !*bfspace then result := '!  . result;
                    j := 0>>>>;
    if ex neq 0 or w then <<
    if not (!*nat and !*bfspace) then result := ee . result
     else if j=0 then <<result := '!  . ee . result; j := 2>>
     else if j=1 then <<result := '!  . ee . '!  . result; j := 4>>
     else if j=2
      then <<result := '!  . '!  . ee . '!  . result; j := 0>>
     else if j=3 then <<result := '!  . ee . '!  . result; j := 0>>
     else if j=4 then <<result := '!  . ee . '!  . result; j := 2>>;
    lst := if ex > 0 then '!+ . explode ex else explode ex;
    for each char in lst do <<
      result := char . result; j := j + 1;
      if j=5 then <<if !*nat and !*bfspace then result := '!  . result;
                    j := 0>>>>>>;
 %  if !*nat then for each char in reversip result do prin2!* char else
    prin2!* smallcompress reversip result
  end;

symbolic procedure smallcompress (li);
   begin scalar ll;
      if (ll := length li)>1000
        then <<li := smallsplit(li,ll/2);
              return concat(smallcompress car li,smallcompress cdr li)>>
       else return compress ('!" . reversip('!" .  reversip li))
   end;

symbolic procedure smallsplit (li,ln);
    begin scalar aa;
       for i:=1:ln do <<aa := car li . aa; li := rest li>>;
       return (reversip aa) . li
    end;

symbolic procedure scientific_notation n;
   begin scalar oldu,oldl;
     oldu := !:upper!-sci!:; oldl := !:lower!-sci!: + 1;
     if fixp n
       then <<
         if n<0
           then rerror(arith,1,
                       {"Invalid argument to scientific_notation:",n});
         !:lower!-sci!: := n - 1; !:upper!-sci!: := n;
      >>
      else if eqcar(n,'list) and length n=3
       then if not (fixp cadr n and fixp caddr n)
              then rerror(arith,2,
                        {"Invalid argument to scientific_notation:",n})
             else <<!:upper!-sci!: := cadr n;
                    !:lower!-sci!: := caddr n - 1 >>;
     return {'list,oldu,oldl}   % Return previous range.
  end;

flag('(scientific_notation), 'opfn);

symbolic procedure order!: nmbr;
   % This function counts the order of a number "n".  NMBR is a bigfloat
   %  representation of "n".
   % **** ORDER(n)=k if 2**k <= ABS(n) < 2**(k+1)
   % ****     when n is not 0, and ORDER(0)=0.
   %
   if mt!: nmbr = 0 then 0 else preci!: nmbr + ep!: nmbr - 1;

symbolic smacro procedure decprec!:(nmbr, k);
   make!:ibf(ashift(mt!: nmbr,-k), ep!: nmbr + k);

symbolic smacro procedure incprec!:(nmbr, k);
   make!:ibf(ashift(mt!: nmbr,k), ep!: nmbr - k);

symbolic procedure conv!:mt(nmbr, k);
   % This function converts a number "n" to an equivalent number of
   % binary precision K by rounding "n" or adding "0"s to "n".
   % NMBR is a binary bigfloat representation of "n".
   %  K is a positive integer.
   if bfp!: nmbr and fixp k and k > 0
     then if (k := preci!: nmbr - k) = 0 then nmbr
           else if k < 0 then incprec!:(nmbr, -k)
           else round!:last(decprec!:(nmbr, k - 1))
    else bflerrmsg 'conv!:mt;

symbolic procedure round!:mt(nmbr, k);
   % This function rounds a number "n" at the (K+1)th place and returns
   % an equivalent number of binary precision K if the precision of "n"
   % is greater than K, else it returns the given number unchanged.
   % NMBR is a bigfloat representation of "n".  K is a positive integer.
   if bfp!: nmbr and fixp k and k > 0 then
      if (k := preci!: nmbr - k - 1) < 0 then nmbr
      else if k = 0 then round!:last nmbr
      else round!:last decprec!:(nmbr, k)
   else bflerrmsg 'round!:mt;

symbolic procedure round!:ep(nmbr, k);
% This function rounds a number "n" and returns an
%      equivalent number having the exponent K if
%      the exponent of "n" is less than K, else
%      it returns the given number unchanged.
% NMBR is a BINARY BIG-FLOAT representation of "n".
% K is an integer (positive or negative).
  if bfp!: nmbr and fixp k then
    if (k := k - 1 - ep!: nmbr) < 0 then nmbr
      else if k = 0 then round!:last nmbr
      else round!:last decprec!:(nmbr, k)
   else bflerrmsg 'round!:ep$

symbolic procedure round!:last nmbr;
   % This function rounds a number "n" at its last place.
   % NMBR is a binary bigfloat representation of "n".
   << if m < 0 then << m := -m; s := t >>;
      m := if oddintp m then lshift (m, -1) + 1
            else lshift (m, -1);
      if s then m := -m;
      make!:ibf (m, e) >>
       where m := mt!: nmbr, e := ep!: nmbr + 1, s := nil;

symbolic procedure cut!:mt(nmbr,k);
% This function returns a given number "n" unchanged
%      if its binary precision is not greater than K, else it
%      cuts off its mantissa at the (K+1)th place and
%      returns an equivalent number of precision K.
% **** CAUTION!  No rounding is made.
% NMBR is a BINARY BIG-FLOAT representation of "n".
% K is a positive integer.
  if bfp!: nmbr and fixp k and k > 0 then
     if (k := preci!: nmbr - k) <= 0 then nmbr
             else decprec!:(nmbr, k)
   else bflerrmsg 'cut!:mt$

symbolic procedure cut!:ep(nmbr, k);
% This function returns a given number "n" unchanged
%      if its exponent is not less than K, else it
%      cuts off its mantissa and returns an equivalent
%      number of exponent K.
% **** CAUTION!  No rounding is made.
% NMBR is a BINARY BIG-FLOAT representation of "n".
% K is an integer (positive or negative).
  if bfp!: nmbr and fixp k then
     if (k := k - ep!: nmbr) <= 0 then nmbr
        else decprec!:(nmbr, k)
   else bflerrmsg 'cut!:ep$

symbolic procedure bftrim!: v;
   normbf round!:mt(v,!:bprec!: - 3);

symbolic procedure decimal2internal (base10,exp10);
  if exp10 >= 0 then i2bf!: (base10 * 10**exp10)
   else divide!-by!-power!-of!-ten (i2bf!: base10, -exp10);

symbolic procedure read!:num(n);
   % This function reads a number or a number-like entity N
   %      and constructs a bigfloat representation of it.
   % N is an integer, a floating-point number, or a string
   %      representing a number.
   % **** If the system does not accept or may incorrectly
   % ****      accept the floating-point numbers, you can
   % ****      input them as strings such as "1.234E-56",
   % ****      "-78.90 D+12" , "+3456 B -78", or "901/234".
   % **** A rational number in a string form is converted
   % ****      to a bigfloat of precision !:PREC!: if
   % ****      !:PREC!: is not NIL, else the precision of
   % ****      the result is set 170.
   % **** Some systems set the maximum size of strings.  If
   % ****      you want to input long numbers exceeding
   % ****      such a maximum size, please use READ!:LNUM.
   if fixp n then make!:ibf(n, 0)
    else if not(numberp n or stringp n) then bflerrmsg 'read!:num
    else begin integer j,m,sign;  scalar ch,u,v,l,appear!.,appear!/;
          j := m := 0;
          sign := 1;
          u := v := appear!. := appear!/ := nil;
          l := explode n;
    loop: ch := car l;
          if digit ch then << u := ch . u; j := j + 1 >>
           else if ch eq '!. then << appear!. := t; j := 0 >>
           else if ch eq '!/ then << appear!/ := t; v := u; u := nil >>
           else if ch eq '!- then sign := -1
           else if ch memq '(!E !D !B !e !d !b) then go to jump;  %JBM
           if l := cdr l then goto loop else goto make;
    jump: while l := cdr l do
            <<if digit(ch := car l) or ch eq '!-
                 then v := ch . v >>;
          l := reverse v;
          % Was erroneously smallcompress.
          if car l eq '!- then m := - compress cdr l
                          else m:= compress l;
    make: u := reverse u;
          v := reverse v;
          if appear!/ then
            return conv!:r2bf(make!:ratnum(sign*compress v,compress u),
                              if !:bprec!: then !:bprec!: else 170);
          if appear!. then j := - j else j := 0;
          if sign = 1 then u := compress u else u := - compress u;
          return round!:mt (decimal2internal (u, j + m), !:bprec!:)
                   where !:bprec!: := if !:bprec!: then !:bprec!:
                                       else msd!: abs u
    end;

symbolic procedure abs!: nmbr;
   % This function makes the absolute value of "n".  N is a binary
   % bigfloat representation of "n".
   if mt!: nmbr > 0 then nmbr else make!:ibf(- mt!: nmbr, ep!: nmbr);

symbolic procedure minus!: nmbr;
   % This function makes the minus number of "n".  N is a binary
   % bigfloat representation of "n".
   make!:ibf(- mt!: nmbr, ep!: nmbr);

symbolic procedure plus!:(n1,n2);
   begin scalar m1,m2,e1,e2,d; return
      if (m1 := mt!: n1)=0 then n2
      else if (m2 := mt!: n2)=0 then n1
      else if (d := (e1 := ep!: n1)-(e2 := ep!: n2))=0
         then make!:ibf(m1+m2, e1)
      else if d>0 then make!:ibf(ashift(m1,d)+m2,e2)
      else make!:ibf(m1+ashift(m2,-d),e1) end;

symbolic procedure difference!:(n1,n2);
   begin scalar m1,m2,e1,e2,d; return
      if (m1 := mt!: n1)=0 then minus!: n2
      else if (m2 := mt!: n2)=0 then n1
      else if (d := (e1 := ep!: n1)-(e2 := ep!: n2))=0
         then make!:ibf(m1 - m2, e1)
      else if d>0 then make!:ibf(ashift(m1,d) - m2,e2)
      else make!:ibf(m1 - ashift(m2,-d),e1) end;

symbolic procedure times!:(n1, n2);
   % This function calculates the product of "n1" and "n2".
   % N1 and N2 are bigfloat representations of "n1" and "n2".
   make!:ibf(mt!: n1 * mt!: n2, ep!: n1 + ep!: n2);

symbolic procedure divide!:(n1,n2,k);
   % This function calculates the quotient of "n1" and "n2", with the
   % precision K, by rounding the ratio of "n1" and "n2" at the (K+1)th
   % place.  N1 and N2 are bigfloat representations of "n1" and "n2".
   % K is any positive integer.
   begin
      n1 := conv!:mt(n1, k + preci!: n2 + 1);
      n1 := make!:ibf(mt!: n1 / mt!: n2, ep!: n1 - ep!: n2);
      return round!:mt(n1, k)
   end;

symbolic procedure max2!:(a,b);
   % This function returns the larger of "n1" and "n2".
   % N1 and N2 are bigfloat representations of "n1" and "n2".
   if greaterp!:(a,b) then a else b;

macro procedure max!: x; expand(cdr x,'max2!:);

symbolic procedure min2!:(a,b);
   % This function returns the smaller of "n1" and "n2".
   % N1 and N2 are binary bigfloat representations of "n1" and "n2".
   if greaterp!:(a,b) then b else a;

macro procedure min!: x; expand(cdr x,'min2!:);

symbolic procedure greaterp!:(a,b);
% this function calculates the a > b, but avoids
% generating large numbers if magnitude difference is large.
    if mt!: a=0 then mt!: b<0
     else if mt!: b=0 then mt!: a>0
     else if ep!: a=ep!: b then mt!: a>mt!: b else
       (((if d=0 then ma>mb else
          ((if d>p2 then ma>0 else if d<-p2 then mb<0
            else if d>0 then ashift(ma,d)>mb
            else ma>ashift(mb,-d))
          where p2=2*!:bprec!:))
         where d=ep!: a - ep!: b, ma=mt!: a, mb=mt!: b)
        where a= normbf a, b=normbf b);

symbolic procedure equal!:(a,b);
  %tests bfloats for a=b rapidly without generating digits. %SK
   zerop mt!: a and zerop mt!: b or
   ep!:(a := normbf a)=ep!:(b := normbf b) and mt!: a=mt!: b;

symbolic procedure lessp!:(n1, n2);
   % This function returns T if "n1" < "n2" else returns NIL.
   % N1 and N2 are bigfloat representations of "n1" and "n2".
   greaterp!:(n2, n1);

symbolic procedure leq!:(n1, n2);
   % This function returns T if "n1" <= "n2" else returns NIL.
   % N1 and N2 are bigfloat representations of "n1" and "n2".
   not greaterp!:(n1, n2);

symbolic procedure minusp!: x;
   % This function returns T if "x"<0 else returns NIL.
   % X is any Lisp entity.
   bfp!: x and mt!: x < 0;

symbolic procedure make!:ratnum(nm,dn);
   % This function constructs an internal representation
   %      of a rational number composed of the numerator
   %      NM and the denominator DN.
   % NM and DN are any integers (positive or negative).
   % **** Four routines in this section are temporary.
   % ****      That is, if your system has own routines
   % ****      for rational number arithmetic, you can
   % ****      accommodate our system to yours only by
   % ****      redefining these four routines.
   if zerop dn then rerror(arith,3,"Zero divisor in make:ratnum")
    else if dn > 0 then '!:ratnum!: . (nm . dn)
    else '!:ratnum!: . (-nm . -dn);

symbolic procedure ratnump!:(x);
   % This function returns T if X is a rational number
   % representation, else NIL.
   % X is any Lisp entity.
   eqcar(x,'!:ratnum!:);                   %JBM Change to EQCAR.

symbolic smacro procedure numr!: rnmbr;
   % This function selects the numerator of a rational number "n".
   % RNMBR is a rational number representation of "n".
   cadr rnmbr;

symbolic smacro procedure denm!: rnmbr;
   % This function selects the denominator of a rational number "n".
   % RNMBR is a rational number representation of "n".
   cddr rnmbr;

symbolic procedure conv!:r2bf(rnmbr,k);
   % This function converts a rational number RNMBR to a bigfloat of
   % precision K, i.e., a bigfloat representation with a given
   % precision.  RNMBR is a rational number representation.  K is a
   % positive integer.
   if ratnump!: rnmbr and fixp k and k > 0
     then divide!:(make!:ibf( numr!: rnmbr, 0),
                   make!:ibf( denm!: rnmbr, 0),k)
    else bflerrmsg 'conv!:r2bf;

endmodule;

end;
