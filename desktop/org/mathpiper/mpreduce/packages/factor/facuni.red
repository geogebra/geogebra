module facuni;

% Authors: A. C. Norman and P. M. A. Moore, 1979;

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


fluid '(!*force!-prime
        !*trfac
        alphalist
        bad!-case
        best!-factor!-count
        best!-known!-factors
        best!-modulus
        best!-set!-pointer
        chosen!-prime
        factor!-level
        factor!-trace!-list
        forbidden!-primes
        hensel!-growth!-size
        input!-leading!-coefficient
        input!-polynomial
        irreducible
        known!-factors
        m!-image!-variable
        modular!-info
        no!-of!-best!-primes
        no!-of!-random!-primes
        non!-monic
        null!-space!-basis
        number!-of!-factors
        one!-complete!-deg!-analysis!-done
        poly!-mod!-p
        previous!-degree!-map
        reduction!-count
        split!-list
        target!-factor!-count
        univariate!-factors
        univariate!-input!-poly
        valid!-primes);


symbolic procedure univariate!-factorize poly;
% input poly a primitive square-free univariate polynomial at least
% quadratic and with +ve lc.  output is a list of the factors of poly
% over the integers ;
  if testx!*!*n!+1 poly then
    factorizex!*!*n!+1(m!-image!-variable,ldeg poly,1)
  else if testx!*!*n!-1 poly then
    factorizex!*!*n!-1(m!-image!-variable,ldeg poly,1)
  else univariate!-factorize1 poly;

symbolic procedure univariate!-factorize1 poly;
  begin scalar
    valid!-primes,univariate!-input!-poly,best!-set!-pointer,
    number!-of!-factors,irreducible,forbidden!-primes,
    no!-of!-best!-primes,no!-of!-random!-primes,bad!-case,
    target!-factor!-count,modular!-info,univariate!-factors,
    hensel!-growth!-size,alphalist,previous!-degree!-map,
    one!-complete!-deg!-analysis!-done,reduction!-count,
    multivariate!-input!-poly;
%note that this code works by using a local database of
%fluid variables that are updated by the subroutines directly
%called here. this allows for the relativly complicated
%interaction between flow of data and control that occurs in
%the factorization algorithm;
    factor!-trace <<
      prin2!* "Univariate polynomial="; printsf poly;
      printstr
         "The polynomial is univariate, primitive and square-free";
      printstr "so we can treat it slightly more specifically. We";
      printstr "factorise mod several primes,then pick the best one";
      printstr "to use in the Hensel construction." >>;
    initialize!-univariate!-fluids poly;
            % set up the fluids to start things off;
tryagain:
    get!-some!-random!-primes();
    choose!-the!-best!-prime();
      if irreducible then <<
        univariate!-factors:=list univariate!-input!-poly;
        goto exit >>
      else if bad!-case then <<
        bad!-case:=nil; goto tryagain >>;
    reconstruct!-factors!-over!-integers();
      if irreducible then <<
        univariate!-factors:=list univariate!-input!-poly;
        goto exit >>;
exit:
    factor!-trace <<
      printstr "The univariate factors are:";
      for each ff in univariate!-factors do printsf ff >>;
    return univariate!-factors
   end;


%**********************************************************************
% univariate factorization part 1. initialization and setting fluids;


symbolic procedure initialize!-univariate!-fluids u;
% Set up the fluids to be used in factoring primitive poly;
  begin
    if !*force!-prime then <<
      no!-of!-random!-primes:=1;
      no!-of!-best!-primes:=1 >>
    else <<
      no!-of!-random!-primes:=5;
            % we generate this many modular images and calculate
            % their factor counts;
      no!-of!-best!-primes:=3;
            % we find the modular factors of this many;
      >>;
    univariate!-input!-poly:=u;
    target!-factor!-count:=ldeg u
  end;


%**********************************************************************;
% univariate factorization part 2. creating modular images and picking
%  the best one;


symbolic procedure get!-some!-random!-primes();
% here we create a number of random primes to reduce the input mod p;
  begin scalar chosen!-prime,poly!-mod!-p,i;
    valid!-primes:=mkvect no!-of!-random!-primes;
    i:=0;
    while i < no!-of!-random!-primes do <<
      poly!-mod!-p:=
        find!-a!-valid!-prime(lc univariate!-input!-poly,
                    univariate!-input!-poly,nil);
      if not(poly!-mod!-p='not!-square!-free) then <<
        i:=iadd1 i;
        putv(valid!-primes,i,chosen!-prime . poly!-mod!-p);
        forbidden!-primes:=chosen!-prime . forbidden!-primes
        >>
      >>
  end;

symbolic procedure choose!-the!-best!-prime();
% given several random primes we now choose the best by factoring
% the poly mod its chosen prime and taking one with the
% lowest factor count as the best for hensel growth;
  begin scalar split!-list,poly!-mod!-p,null!-space!-basis,
               known!-factors,w,n;
    modular!-info:=mkvect no!-of!-random!-primes;
    for i:=1:no!-of!-random!-primes do <<
      w:=getv(valid!-primes,i);
      get!-factor!-count!-mod!-p(i,cdr w,car w,nil) >>;
    split!-list:=sort(split!-list,function lessppair);
            % this now contains a list of pairs (m . n) where
            % m is the no: of factors in set no: n. the list
            % is sorted with best split (smallest m) first;
    if caar split!-list = 1 then <<
      irreducible:=t; return nil >>;
    w:=split!-list;
    for i:=1:no!-of!-best!-primes do <<
      n:=cdar w;
      get!-factors!-mod!-p(n,car getv(valid!-primes,n));
      w:=cdr w >>;
            % pick the best few of these and find out their
            % factors mod p;
    split!-list:=delete(w,split!-list);
            % throw away the other sets;
    check!-degree!-sets(no!-of!-best!-primes,nil);
            % the best set is pointed at by best!-set!-pointer;
    one!-complete!-deg!-analysis!-done:=t;
    factor!-trace <<
      w:=getv(valid!-primes,best!-set!-pointer);
      prin2!* "The chosen prime is "; printstr car w;
      prin2!* "The polynomial mod "; prin2!* car w;
      printstr ", made monic, is:";
      printsf cdr w;
      printstr "and the factors of this modular polynomial are:";
      for each x in getv(modular!-info,best!-set!-pointer)
         do printsf x;
      >>
  end;



%**********************************************************************;
% univariate factorization part 3. reconstruction of the
% chosen image over the integers;


symbolic procedure reconstruct!-factors!-over!-integers();
% the hensel construction from modular case to univariate
% over the integers;
  begin scalar best!-modulus,best!-factor!-count,input!-polynomial,
    input!-leading!-coefficient,best!-known!-factors,s;
    s:=getv(valid!-primes,best!-set!-pointer);
    best!-known!-factors:=getv(modular!-info,best!-set!-pointer);
    input!-leading!-coefficient:=lc univariate!-input!-poly;
    best!-modulus:=car s;
    best!-factor!-count:=length best!-known!-factors;
    input!-polynomial:=univariate!-input!-poly;
    univariate!-factors:=reconstruct!.over!.integers();
    if irreducible then return t;
    number!-of!-factors:=length univariate!-factors;
    if number!-of!-factors=1 then return irreducible:=t
  end;


symbolic procedure reconstruct!.over!.integers();
  begin scalar w,lclist,non!-monic;
    set!-modulus best!-modulus;
    for i:=1:best!-factor!-count do
      lclist:=input!-leading!-coefficient . lclist;
    if not (input!-leading!-coefficient=1) then <<
      best!-known!-factors:=
        for each ff in best!-known!-factors collect
          multf(input!-leading!-coefficient,!*mod2f ff);
      non!-monic:=t;
      factor!-trace <<
        printstr
           "(a) Now the polynomial is not monic so we multiply each";
        printstr
           "of the modular factors, f(i), by the absolute value of";
        prin2!* "the leading coefficient: ";
        prin2!* input!-leading!-coefficient; printstr '!.;
        printstr "To bring the polynomial into agreement with this, we";
        prin2!* "multiply it by ";
        if best!-factor!-count > 2 then
          << prin2!* input!-leading!-coefficient; prin2!* "**";
            printstr isub1 best!-factor!-count >>
        else printstr input!-leading!-coefficient >> >>;
    w:=uhensel!.extend(input!-polynomial,
      best!-known!-factors,lclist,best!-modulus);
    if irreducible then return t;
    if car w ='ok then return cdr w
    else errorf w
  end;


% Now some special treatment for cyclotomic polynomials;

symbolic procedure testx!*!*n!+1 u;
  not domainp u and (
    lc u=1 and
    red u = 1);


symbolic procedure testx!*!*n!-1 u;
  not domainp u and (
    lc u=1 and
    red u = -1);


symbolic procedure factorizex!*!*n!+1(var,degree,vorder);
% Deliver factors of (VAR**VORDER)**DEGREE+1 given that it is
% appropriate to treat VAR**VORDER as a kernel;
  if evenp degree then factorizex!*!*n!+1(var,degree/2,2*vorder)
  else begin
    scalar w;
    w := factorizex!*!*n!-1(var,degree,vorder);
    w := negf car w . cdr w;
    return for each p in w collect negate!-variable(var,2*vorder,p)
  end;

symbolic procedure negate!-variable(var,vorder,p);
% VAR**(VORDER/2) -> -VAR**(VORDER/2) in the polynomial P;
  if domainp p then p
  else if mvar p=var then
    if remainder(ldeg p,vorder)=0 then
            lt p .+ negate!-variable(var,vorder,red p)
    else (lpow p .* negf lc p) .+ negate!-variable(var,vorder,red p)
  else (lpow p .* negate!-variable(var,vorder,lc p)) .+
        negate!-variable(var,vorder,red p);


symbolic procedure integer!-factors n;
% Return integer factors of N, with attached multiplicities. Assumes
% that N is fairly small;
  begin
    scalar l,q,m,w;
% L is list of results generated so far, Q is current test divisor,
% and M is associated multiplicity;
    if n=1 then return '((1 . 1));
    q := 2; m := 0;
    % Test divide by 2,3,5,7,9,11,13,...
top:
    w := divide(n,q);
    while cdr w=0 do << n := car w; w := divide(n,q); m := m+1 >>;
    if not(m=0) then l := (q . m) . l;
    if q>car w then <<
      if not(n=1) then l := (n . 1) . l;
      return reversip l >>;
%   q := ilogor(1,iadd1 q);
    q := iadd1 q;
    if q #> 3 then q := iadd1 q;
    m := 0;
    go to top
  end;


symbolic procedure factored!-divisors fl;
% FL is an association list of primes and exponents. Return a list
% of all subsets of this list, i.e. of numbers dividing the
% original integer. Exclude '1' from the list;
  if null fl then nil
  else begin
    scalar l,w;
    w := factored!-divisors cdr fl;
    l := w;
    for i := 1:cdar fl do <<
      l := list (caar fl . i) . l;
      for each p in w do
        l := ((caar fl . i) . p) . l >>;
    return l
  end;

symbolic procedure factorizex!*!*n!-1(var,degree,vorder);
  if evenp degree then append(factorizex!*!*n!+1(var,degree/2,vorder),
                              factorizex!*!*n!-1(var,degree/2,vorder))
  else if degree=1 then list((mksp(var,vorder) .* 1) .+ (-1))
  else begin
    scalar facdeg;
    facdeg := '((1 . 1)) . factored!-divisors integer!-factors degree;
    return for each fl in facdeg
       collect cyclotomic!-polynomial(var,fl,vorder)
  end;

symbolic procedure cyclotomic!-polynomial(var,fl,vorder);
% Create Psi<degree>(var**order)
% where degree is given by the association list of primes and
% multiplicities FL;
  if not(cdar fl=1) then
    cyclotomic!-polynomial(var,(caar fl . sub1 cdar fl) . cdr fl,
                           vorder*caar fl)
  else if cdr fl=nil then
     if caar fl=1 then (mksp(var,vorder) .* 1) .+ (-1)
     else quotfail((mksp(var,vorder*caar fl) .* 1) .+ (-1),
                   (mksp(var,vorder) .* 1) .+ (-1))
  else quotfail(cyclotomic!-polynomial(var,cdr fl,vorder*caar fl),
                cyclotomic!-polynomial(var,cdr fl,vorder));



endmodule;


end;
