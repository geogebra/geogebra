module facmisc;  % Miscellaneous routines used from several sections.

% Authors: A. C. Norman and P. M. A. Moore, 1979.

fluid '(current!-modulus
        image!-set!-modulus
        modulus!/2
        othervars
        polyzero
%       pt
        save!-zset
        zerovarset);

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


global '(largest!-small!-modulus pseudo!-primes teeny!-primes);


% (1) Investigate variables in polynomial.

symbolic procedure multivariatep(a,v);
    if domainp a then nil
    else if not(mvar a eq v) then t
    else if multivariatep(lc a,v) then t
    else multivariatep(red a,v);

symbolic procedure variables!-in!-form a;
% collect variables that occur in the form a;
    variables!.in!.form(a,nil);

symbolic procedure get!.coefft!.bound(poly,degbd);
% Calculates a coefft bound for the factors of poly.  This simple
% bound is that suggested by Paul Wang and Linda p. Rothschild in
% Math. Comp. Vol 29 July 75, p.940, due to Gel'fond.
% Note that for tiny polynomials the bound is forced up to be
% larger than any prime that will get used in the mod-p splitting;
  max(get!-height poly * fixexpfloat sumof degbd,110);

symbolic procedure sumof degbd;
  if null degbd then 0 else cdar degbd + sumof cdr degbd;

symbolic procedure fixexpfloat n;
   % Compute exponential function e**n for potentially large N,
   % rounding result up somewhat. Note that exp(10)=22027 or so,
   % so if the basic floating point exponential function is accurate
   % to 6 or so digits we are protected here against roundoff.
   % This could be replaced by ceiling exp n, but is written this
   % way to avoid floating point overflow.
%  if n>10 then (fixexpfloat(n2)*fixexpfloat(n-n2) where n2 = n/2)
%   else 1+fix exp n;
   if n>10 then 22027*fixexpfloat(n-10) else ceiling exp float n;

% (2) Minor variations on ordinary algebraic operations.

symbolic procedure quotfail(a,b);
% version of quotf that fails if the division does;
  if polyzerop a then polyzero
  else begin scalar w;
    w:=quotf(a,b);
    if didntgo w then errorf list("UNEXPECTED DIVISION FAILURE",a,b)
    else return w
  end;

symbolic procedure quotfail1(a,b,msg);
% version of quotf that fails if the division does, and gives
% custom message;
  if polyzerop a then polyzero
  else begin scalar w;
    w:=quotf(a,b);
    if didntgo w then errorf msg
    else return w
  end;


% (3) pseudo-random prime numbers - small and large.

symbolic procedure set!-teeny!-primes();
  begin scalar i;
    i:=-1;
    teeny!-primes:=mkvect 9;
    putv(teeny!-primes,i:=iadd1 i,3);
    putv(teeny!-primes,i:=iadd1 i,5);
    putv(teeny!-primes,i:=iadd1 i,7);
    putv(teeny!-primes,i:=iadd1 i,11);
    putv(teeny!-primes,i:=iadd1 i,13);
    putv(teeny!-primes,i:=iadd1 i,17);
    putv(teeny!-primes,i:=iadd1 i,19);
    putv(teeny!-primes,i:=iadd1 i,23);
    putv(teeny!-primes,i:=iadd1 i,29);
    putv(teeny!-primes,i:=iadd1 i,31)
  end;

set!-teeny!-primes();

symbolic procedure random!-small!-prime();
  begin
    scalar p;
    repeat <<p:=small!-random!-number(); if evenp p then p := iadd1 p>>
       until primep p;
    return p
  end;

symbolic procedure small!-random!-number();
% Returns a smallish number from a distribution strongly favouring
% smaller numbers;
  begin scalar w;
% The next lines generate a random value in the range 0 to 1000000.
    w := remainder(next!-random!-number(), 1000);
    w := remainder(next!-random!-number(),1000) + 1000*w;
    if w < 0 then w := w + 1000000;
    w:=1.0+1.5*float w/1000000.0;  % 1.0 to 2.5
    w:=times(w,w);                 % In range 1.0 to 6.25
    return fix exp w;              % Should be in range 3 to 518,
                                   % < 21 about half the time;
  end;

% symbolic procedure fac!-exp u;
%    % Simple exp routine.  Assumes that Lisp has a routine for
%    % exponentiation of floats by integers.  Relative accuracy 4.e-5.
%    begin scalar x; integer n;
%      n := fix u;
%      if (x := (u - float n)) > 0.5 then <<x := x - 1.0; n := n + 1>>;
%      u := ee***n;
%      return u*((x+6.0)*x+12.0)/((x-6.0)*x+12.0)
%    end;

symbolic procedure random!-teeny!-prime l;
% get one of the first 10 primes at random providing it is
% not in the list L or that L says we have tried them all;
  if l='all or (length l = 10) then nil
  else begin scalar p;
    repeat
       p:=getv(teeny!-primes,remainder(next!-random!-number(),10))
    until not member(p,l);
    return p
  end;

% symbolic procedure primep n;
% Test if prime. Only for use on small integers.
%    n=2 or
%    (n>2 and not evenp n and primetest(n,3));

% symbolic procedure primetest(n,trial);
%    if igreaterp(itimes(trial,trial),n) then t
%    else if iremainder(n,trial)=0 then nil
%    else primetest(n,iplus2(trial,2));


% PSEUDO-PRIMES will be a list of all composite numbers which are
% less than 2^24 and where 2926^(n-1) = 3315^(n-1) = 1 mod n.

pseudo!-primes:=mkvect 87;

begin
  scalar i,l;
  i:=0;
  l:= '(2047     4033     33227    38503    56033
        137149   145351   146611   188191   226801
        252601   294409   328021   399001   410041
        488881   512461   556421   597871   636641
        665281   722261   742813   873181   950797
        1047619  1084201  1141141  1152271  1193221
        1373653  1398101  1461241  1584133  1615681
        1627921  1755001  1857241  1909001  2327041
        2508013  3057601  3363121  3542533  3581761
        3828001  4069297  4209661  4335241  4510507
        4588033  4650049  4877641  5049001  5148001
        5176153  5444489  5481451  5892511  5968873
        6186403  6189121  6733693  6868261  6955541
        7398151  7519441  8086231  8134561  8140513
        8333333  8725753  8927101  9439201  9494101
        10024561 10185841 10267951 10606681 11972017
        13390081 14063281 14469841 14676481 14913991
        15247621 15829633 16253551);
    while l do <<
       putv(pseudo!-primes,i,car l);
       i:=i+1;
       l:=cdr l >>
  end;

symbolic procedure random!-prime();
  begin
% I want a random prime that is smaller than largest-small-modulus.
% I do this by generating random odd integers in the range lsm/2 to
% lsm and filtering them for primality. Prime testing is done using
% a Fermat test followed by lookup in an exception table that was
% laboriously precomputed. This process should be distinctly faster
% than trial-division testing of candidate primes, but the exception
% table is tedious to compute, so I limit lsm to 2**24 here. This is
% both the value that Cambridge Lisp can support directly, an indication
% of how large an exception table I computed using 48 hours of CPU time
% and large enough that primes selected this way will hardly ever
% be unlucky just through being too small.
    scalar p,w,oldmod,lsm, lsm2;
    lsm := largest!-small!-modulus;
    if lsm > 2**24 then lsm := 2**24;
    lsm2 := lsm/2;
    % W will become 1 when P is prime;
    oldmod := current!-modulus;
    while not (w=1) do <<
      p := remainder(next!-random!-number(), lsm);
      if p < lsm2 then p := p + lsm2;
      if evenp p then p := p + 1;
      set!-modulus p;
      w:=modular!-expt(modular!-number 2926,isub1 p);
      if w=1
         and (modular!-expt(modular!-number 3315,isub1 p) neq 1
                 or pseudo!-prime!-p p)
        then w:=0>>;
    set!-modulus oldmod;
    return p
  end;

symbolic procedure pseudo!-prime!-p n;
  begin
    scalar low,mid,high,v;
    low:=0;
    high:=87; % Size of vector of pseudo-primes;
    while not (high=low) do << % Binary search in table;
      mid:=iquotient(iplus2(iadd1 high,low),2);
         % Mid point of (low,high);
      v:=getv(pseudo!-primes,mid);
      if igreaterp(v,n) then high:=isub1 mid else low:=mid >>;
    return (getv(pseudo!-primes,low)=n)
  end;


% (4) useful routines for vectors.

symbolic procedure form!-sum!-and!-product!-mod!-p(avec,fvec,r);
% sum over i (avec(i) * fvec(i));
  begin scalar s;
    s:=polyzero;
    for i:=1:r do
      s:=plus!-mod!-p(times!-mod!-p(getv(avec,i),getv(fvec,i)),
        s);
    return s
  end;

symbolic procedure form!-sum!-and!-product!-mod!-m(avec,fvec,r);
% Same as above but AVEC holds alphas mod p and want to work
% mod m (m > p) so minor difference to change AVEC to AVEC mod m;
  begin scalar s;
    s:=polyzero;
    for i:=1:r do
      s:=plus!-mod!-p(times!-mod!-p(
        !*f2mod !*mod2f getv(avec,i),getv(fvec,i)),s);
    return s
  end;

symbolic procedure reduce!-vec!-by!-one!-var!-mod!-p(v,pt,n);
% Substitute for the given variable in all elements creating a
% new vector for the result. (All arithmetic is mod p).
  begin scalar newv;
    newv:=mkvect n;
    for i:=1:n do
      putv(newv,i,evaluate!-mod!-p(getv(v,i),car pt,cdr pt));
    return newv
  end;

symbolic procedure make!-bivariate!-vec!-mod!-p(v,imset,var,n);
  begin scalar newv;
    newv:=mkvect n;
    for i:=1:n do
      putv(newv,i,make!-bivariate!-mod!-p(getv(v,i),imset,var));
    return newv
  end;

symbolic procedure times!-vector!-mod!-p(v,n);
% product of all the elements in the vector mod p;
  begin scalar w;
    w:=1;
    for i:=1:n do w:=times!-mod!-p(getv(v,i),w);
    return w
  end;

symbolic procedure make!-vec!-modular!-symmetric(v,n);
% fold each elt of V which is current a modular poly in the
% range 0->(p-1) onto the symmetric range (-p/2)->(p/2);
  for i:=1:n do putv(v,i,make!-modular!-symmetric getv(v,i));

% (5) Combinatorial fns used in finding values for the variables.


symbolic procedure make!-zerovarset vlist;
% vlist is a list of pairs (v . tag) where v is a variable name and
% tag is a boolean tag. The procedure splits the list into two
% according to the tags: Zerovarset is set to a list of variables
% whose tag is false and othervars contains the rest;
  for each w in vlist do
    if cdr w then othervars:= car w . othervars
    else zerovarset:= car w . zerovarset;

symbolic procedure make!-zeroset!-list n;
% Produces a list of lists each of length n with all combinations of
% ones and zeroes;
  begin scalar w;
    for k:=0:n do w:=append(w,kcombns(k,n));
    return w
  end;

symbolic procedure kcombns(k,m);
% produces a list of all combinations of ones and zeroes with k ones
% in each;
  if k=0 or k=m then begin scalar w;
    if k=m then k:=1;
    for i:=1:m do w:=k.w;
    return list w
    end
  else if k=1 or k=isub1 m then <<
    if k=isub1 m then k:=0;
    list!-with!-one!-a(k,1 #- k,m) >>
  else append(
    for each x in kcombns(isub1 k,isub1 m) collect (1 . x),
    for each x in kcombns(k,isub1 m) collect (0 . x) );

symbolic procedure list!-with!-one!-a(a,b,m);
% Creates list of all lists with one a and m-1 b's in;
  begin scalar w,x,r;
    for i:=1:isub1 m do w:=b . w;
    r:=list(a . w);
    for i:=1:isub1 m do <<
      x:=(car w) . x; w:=cdr w;
      r:=append(x,(a . w)) . r >>;
    return r
  end;

symbolic procedure make!-next!-zset l;
  begin scalar k,w;
    image!-set!-modulus:=iadd1 image!-set!-modulus;
    set!-modulus image!-set!-modulus;
    w:=for each ll in cdr l collect
      for each n in ll collect
        if n=0 then n
        else <<
          k:=modular!-number next!-random!-number();
          while (zerop k) or (onep k) do
            k:=modular!-number next!-random!-number();
          if k>modulus!/2 then k:=k-current!-modulus;
           k >>;
    save!-zset:=nil;
    return w
  end;

endmodule;

end;
