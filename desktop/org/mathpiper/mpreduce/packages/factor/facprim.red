module facprim;   % Factorize a primitive multivariate polynomial.

% Author: P. M. A. Moore, 1979.

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


% Modifications by: Arthur C. Norman, Anthony C. Hearn.

fluid '(!*force!-zero!-set
        !*overshoot
        !*overview
        !*trfac
        alphalist
        alphavec
        bad!-case
        best!-factor!-count
        best!-known!-factors
        best!-modulus
        best!-set!-pointer
        chosen!-prime
        current!-factor!-product
        deltam
        f!-numvec
        factor!-level
        factor!-trace!-list
        factored!-lc
        factorvec
        facvec
        fhatvec
        forbidden!-primes
        forbidden!-sets
        full!-gcd
        hensel!-growth!-size
        image!-content
        image!-factors
        image!-lc
        image!-mod!-p
        image!-poly
        image!-set
        image!-set!-modulus
        input!-leading!-coefficient
        input!-polynomial
        inverted
        inverted!-sign
        irreducible
        known!-factors
        kord!*
        m!-image!-variable
        modfvec
        modular!-info
        multivariate!-factors
        multivariate!-input!-poly
        no!-of!-best!-sets
        no!-of!-primes!-to!-try
        no!-of!-random!-sets
        non!-monic
        null!-space!-basis
        number!-of!-factors
        one!-complete!-deg!-analysis!-done
        othervars
        poly!-mod!-p
        polynomial!-to!-factor
        previous!-degree!-map
        prime!-base
        reconstructing!-gcd
        reduction!-count
        save!-zset
        split!-list
        target!-factor!-count
        true!-leading!-coeffts
        usable!-set!-found
        valid!-image!-sets
        vars!-to!-kill
        zero!-set!-tried
        zerovarset
        zset);

global '(largest!-small!-modulus);


%***********************************************************************
%
%    Primitive multivariate polynomial factorization more or less as
%    described by Paul Wang in:  Math. Comp. vol.32 no.144 oct 1978 pp.
%    1215-1231 'An Improved Multivariate Polynomial Factoring Algorithm'
%
%***********************************************************************

%-----------------------------------------------------------------------
%   This code works by using a local database of fluid variables
%   whose meaning is (hopefully) obvious.
%   they are used as follows:
%
%   global name:            set in:               comments:
%
% m!-factored!-leading!    create!.images        only set if non-numeric
%  -coefft
% m!-factored!-images      factorize!.images     vector
% m!-input!-polynomial     factorize!-primitive!
%                           -polynomial
% m!-best!-image!-pointer  choose!.best!.image
% m!-image!-factors        choose!.best!.image   vector
% m!-true!-leading!        choose!.best!.image   vector
%  -coeffts
% m!-prime                 choose!.best!.image
% irreducible              factorize!.images     predicate
% inverted                 create!.images        predicate
% m!-inverted!-sign        create!-images        +1 or -1
% non!-monic               determine!-leading!   predicate
%                           -coeffts
%                          (also reconstruct!-over!
%                           -integers)
% m!-number!-of!-factors   choose!.best!.image
% m!-image!-variable       square!.free!.factorize
%                          or factorize!-form
% m!-image!-sets           create!.images        vector
% this last contains the images of m!-input!-polynomial and the
% numbers associated with the factors of lc m!-input!-polynomial (to be
% used later) the latter existing only when the lc m!-input!-polynomial
% is non-integral. ie.:
%    m!-image!-sets=< ... , (( d . u ), a, d) , ... >   ( a vector)
% where: a = an image set (=association list);
%        d = cont(m!-input!-polynomial image wrt a);
%        u = prim.part.(same) which is non-trivial square-free
%            by choice of image set.;
%        d = vector of numbers associated with factors in lc
%            m!-input!-polynomial (these depend on a as well);
% the number of entries in m!-image!-sets is defined by the fluid
% variable, no.of.random.sets.


%***********************************************************************
% Multivariate factorization part 1. entry point for this code:
% (** NB ** the polynomial is assumed to be non-trivial, primitive and
% square free.)
%***********************************************************************

symbolic procedure factorize!-primitive!-polynomial u;
% U is primitive square free and at least linear in
% m!-image!-variable. M!-image!-variable is the variable preserved in
% the univariate images. This function determines a random set of
% integers and a prime to create a univariate modular image of u,
% factorize it and determine the leading coeffts of the factors in the
% full factorization of u. Finally the modular image factors are grown
% up to the full multivariates ones using the hensel construction.
% Result is simple list of irreducible factors.
  if not(m!-image!-variable eq mvar u) then errach "factorize variable"
   else if degree!-in!-variable(u,m!-image!-variable) = 1 then list u
   else if degree!-in!-variable(u,m!-image!-variable) = 2
    then factorize!-quadratic u
   else if fac!-univariatep u then univariate!-factorize u
   else begin scalar
    valid!-image!-sets,factored!-lc,image!-factors,prime!-base,
    one!-complete!-deg!-analysis!-done,zset,zerovarset,othervars,
    multivariate!-input!-poly,best!-set!-pointer,reduction!-count,
    true!-leading!-coeffts,number!-of!-factors,
    inverted!-sign,irreducible,inverted,vars!-to!-kill,
    forbidden!-sets,zero!-set!-tried,non!-monic,
    no!-of!-best!-sets,no!-of!-random!-sets,bad!-case,
    target!-factor!-count,modular!-info,multivariate!-factors,
    hensel!-growth!-size,alphalist,
    previous!-degree!-map,image!-set!-modulus,
    best!-known!-factors,reconstructing!-gcd,full!-gcd;
%   base!-timer:=time();
%   trace!-time display!-time(
%     " Entered multivariate primitive polynomial code after ",
%     base!-timer - base!-time);
    % Note that this code works by using a local database of fluid
    % variables that are updated by the subroutines directly called
    % here.  This allows for the relatively complicated interaction
    % between flow of data and control that occurs in the factorization
    % algorithm.
    factor!-trace <<
      printstr "From now on we shall refer to this polynomial as U.";
      printstr
         "We now create an image of U by picking suitable values ";
      printstr "for all but one of the variables in U.";
      prin2!* "The variable preserved in the image is ";
      prinvar m!-image!-variable; terpri!*(nil) >>;
    initialize!-fluids u;
            % set up the fluids to start things off.
%   w!-time:=time();
tryagain:
    get!-some!-random!-sets();
    choose!-the!-best!-set();
%     trace!-time <<
%       display!-time("Modular factoring and best set chosen in ",
%         time()-w!-time);
%       w!-time:=time() >>;
      if irreducible then return list u
      else if bad!-case then <<
        if !*overshoot then prin2t "Bad image sets - loop";
        bad!-case:=nil; goto tryagain >>;
    reconstruct!-image!-factors!-over!-integers();
%     trace!-time <<
%       display!-time("Image factors reconstructed in ",time()-w!-time);
%       w!-time:=time() >>;
      if irreducible then return list u
      else if bad!-case then <<
        if !*overshoot then prin2t "Bad image factors - loop";
        bad!-case:=nil; goto tryagain >>;
    determine!.leading!.coeffts();
%     trace!-time <<
%       display!-time("Leading coefficients distributed in ",
%         time()-w!-time);
%       w!-time:=time() >>;
      if irreducible then
        return list u
      else if bad!-case then <<
        if !*overshoot then prin2t "Bad split shown by LC distribution";
        bad!-case:=nil; goto tryagain >>;
    if determine!-more!-coeffts()='done then <<
%     trace!-time <<
%       display!-time("All the coefficients distributed in ",
%         time()-w!-time);
%       w!-time:=time() >>;
      return check!-inverted multivariate!-factors >>;
%   trace!-time <<
%     display!-time("More coefficients distributed in ",
%       time()-w!-time);
%     w!-time:=time() >>;
    reconstruct!-multivariate!-factors(nil);
      if bad!-case and not irreducible then <<
        if !*overshoot then prin2t "Multivariate overshoot - restart";
         bad!-case:=nil; goto tryagain >>;
%     trace!-time
%       display!-time("Multivariate factors reconstructed in ",
%         time()-w!-time);
      if irreducible then return list u;
    return check!-inverted multivariate!-factors
   end;

symbolic procedure check!-inverted multi!-faclist;
  begin scalar inv!.sign,l;
    if inverted then <<
      inv!.sign:=1;
      multi!-faclist:=
        for each x in multi!-faclist collect <<
        l:=invert!.poly(x,m!-image!-variable);
        inv!.sign:=(car l) * inv!.sign;
        cdr l >>;
      if not(inv!.sign=inverted!-sign) then
        errorf list("INVERSION HAS LOST A SIGN",inv!.sign) >>;
      return multivariate!-factors:=multi!-faclist end;

symbolic procedure getcof(p, v, n);
% Get coeff of v^n in p.
% I bet this exists somewhere under a different name....
  if domainp p then if n=0 then p else nil
  else if mvar p = v then
    if ldeg p=n then lc p
    else getcof(red p, v, n)
  else addf(multf((lpow p .* 1) .+ nil, getcof(lc p, v, n)),
            getcof(red p, v, n));

symbolic procedure factorize!-quadratic u;
% U is a primitive square-free quadratic. It factors if and only if
% its discriminant is a perfect square.
  begin scalar a, b, c, discr, f1, f2, x;
% I am unreasonably cautious here - I THINK that the image variable
% should be the main var here, but in case things have got themselves
% reordered & to make myself bomb proof against future changes I will
% not assume same.
    a := getcof(u, m!-image!-variable, 2);
    b := getcof(u, m!-image!-variable, 1);
    c := getcof(u, m!-image!-variable, 0);
    if dmode!* = '!:mod!: and current!-modulus = 2 then % problems
       if b=1 and c=1 then return list u; % Irreducible.
    discr := addf(multf(b, b), multf(a, multf(-4, c)));
    discr := sqrtf2 discr;
    if discr=-1 then return list u; % Irreducible.
    x := addf(multf(a, multf(2, !*k2f m!-image!-variable)), b);
    f1 := addf(x, discr);
    f2 := addf(x, negf discr);
    f1 := quotf(f1,
               cdr contents!-with!-respect!-to(f1, m!-image!-variable));
    f2 := quotf(f2,
               cdr contents!-with!-respect!-to(f2, m!-image!-variable));
    return list(f1, f2)
  end;

symbolic procedure sqrtd2 d;
% Square root of domain element or -1 if it does not have an exact one.
% Possibly needs upgrades to deal with non-integer domains, e.g. in
% modular arithmetic just half of all values have square roots (= are
% quadratic residues), but finding the roots is (I think) HARD.  In
% floating point it could be taken that all positive values have square
% roots.  Anyway somebody can adjust this as necessary and I think that
% SQRTF2 will then behave properly...
  if d=nil then nil
  else if not fixp d or d<0 then -1
  else begin
    scalar q, r, rold;
    q := pmam!-sqrt d;        % Works even if D is really huge.
    r := q*q-d;
    repeat <<
      rold := abs r;
      q := q - (r+q)/(2*q);   % / truncates, so this rounds to nearest
      r := q*q-d >> until abs r >= rold;
    if r=0 then return q
    else return -1
  end;

symbolic procedure pmam!-sqrt n;
   % Find the square root of n and return integer part + 1.  N is fixed
   % pt on input.  As it may be very large, i.e. > largest allowed
   % floating pt number, it is scaled appropriately.
   begin scalar s,ten!*!*6,ten!*!*12,ten!*!*14;
      s:=0;
      ten!*!*6:=10**6;
      ten!*!*12:=ten!*!*6**2;
      ten!*!*14:=100*ten!*!*12;
      while n>ten!*!*14 do << s:=iadd1 s; n:=1+n/ten!*!*12 >>;
      return (fix sqrt float n + 1)*10**(6*s)
   end;

symbolic procedure sqrtf2 p;
% Return square root of the polynomial P if there is an exact one,
% else returns -1 to indicate failure.
  if domainp p then sqrtd2 p
   else begin
    scalar v, d, qlc, q, r, w;
    if not evenp (d := ldeg p) or
       (qlc := sqrtf2 lc p) = -1 then return -1;
    d := d/2;
    v := mvar p;
    q := (mksp(v, d) .* qlc) .+ nil;      % First approx to sqrt(P)
    r := multf(2, q);
    p := red p;                           % Residue
    while not domainp p and
          mvar p = v and
          ldeg p >= d and
          (w := quotf(lt p .+ nil, r)) neq nil do
        <<  p := addf(p, multf(negf w, addf(multf(2, q), w)));
            q := addf(q, w) >>;
    if null p then return q else return -1
  end;

symbolic procedure initialize!-fluids u;
% Set up the fluids to be used in factoring primitive poly.
  begin scalar w,w1;
    if !*force!-zero!-set then <<
      no!-of!-random!-sets:=1;
      no!-of!-best!-sets:=1 >>
    else <<
      no!-of!-random!-sets:=9;
      % we generate this many and calculate their factor counts.
      no!-of!-best!-sets:=5;
            % we find the modular factors of this many.
      >>;
    image!-set!-modulus:=5;
    vars!-to!-kill:=variables!-to!-kill lc u;
    multivariate!-input!-poly:=u;
    no!-of!-primes!-to!-try := 5;
    target!-factor!-count:=degree!-in!-variable(u,m!-image!-variable);
    if not domainp lc multivariate!-input!-poly then
      if domainp (w:=
        trailing!.coefft(multivariate!-input!-poly,
                         m!-image!-variable)) then
    << inverted:=t;
        % note that we are 'inverting' the poly m!-input!-polynomial.
      w1:=invert!.poly(multivariate!-input!-poly,m!-image!-variable);
      multivariate!-input!-poly:=cdr w1;
      inverted!-sign:=car w1;
            % to ease the lc problem, m!-input!-polynomial <- poly
            % produced by taking numerator of (m!-input!-polynomial
            % with 1/m!-image!-variable substituted for
            % m!-image!-variable).
            % m!-inverted!-sign is -1 if we have inverted the sign of
            % the resulting poly to keep it +ve, else +1.
      factor!-trace <<
        prin2!* "The trailing coefficient of U wrt ";
        prinvar m!-image!-variable; prin2!* "(="; prin2!* w;
        printstr ") is purely numeric so we 'invert' U to give: ";
        prin2!* "  U <- "; printsf multivariate!-input!-poly;
        printstr "This simplifies any problems with the leading ";
        printstr "coefficient of U." >>
    >>
    else <<
%     trace!-time prin2t "Factoring the leading coefficient:";
%     wtime:=time();
      factored!-lc:=
        factorize!-form!-recursion lc multivariate!-input!-poly;
%     trace!-time display!-time("Leading coefficient factored in ",
%       time()-wtime);
            % factorize the lc of m!-input!-polynomial completely.
      factor!-trace <<
        printstr
           "The leading coefficient of U is non-trivial so we must ";
        printstr "factor it before we can decide how it is distributed";
        printstr "over the leading coefficients of the factors of U.";
        printstr "So the factors of this leading coefficient are:";
        fac!-printfactors factored!-lc >>
    >>;
   make!-zerovarset vars!-to!-kill;
            % Sets ZEROVARSET and OTHERVARS.
   if null zerovarset then zero!-set!-tried:=t
   else <<
    zset:=make!-zeroset!-list length zerovarset;
    save!-zset:=zset >>
  end;

symbolic procedure variables!-to!-kill lc!-u;
% Picks out all the variables in u except var. Also checks to see if
% any of these divide lc u: if they do they are dotted with t otherwise
% dotted with nil. result is list of these dotted pairs.
  for each w in cdr kord!* collect
    if (domainp lc!-u) or didntgo quotf(lc!-u,!*k2f w) then
       (w . nil) else (w . t);


%***********************************************************************
% Multivariate factorization part 2. Creating image sets and picking
%  the best one.

fluid '(usable!-set!-found);

symbolic procedure get!-some!-random!-sets();
% here we create a number of random sets to make the input
% poly univariate by killing all but 1 of the variables. at
% the same time we pick a random prime to reduce this image
% poly mod p.
  begin scalar image!-set,chosen!-prime,image!-lc,image!-mod!-p,
        image!-content,image!-poly,f!-numvec,forbidden!-primes,i,j,
        usable!-set!-found;
    valid!-image!-sets:=mkvect no!-of!-random!-sets;
    i:=0;
    while i < no!-of!-random!-sets do <<
%     wtime:=time();
      generate!-an!-image!-set!-with!-prime(
        if i<idifference(no!-of!-random!-sets,3) then nil else t);
%     trace!-time
%       display!-time("  Image set generated in ",time()-wtime);
      i:=iadd1 i;
      putv(valid!-image!-sets,i,list(
        image!-set,chosen!-prime,image!-lc,image!-mod!-p,image!-content,
        image!-poly,f!-numvec));
      forbidden!-sets:=image!-set . forbidden!-sets;
      forbidden!-primes:=list chosen!-prime;
      j:=1;
      while (j<3) and (i<no!-of!-random!-sets) do <<
%       wtime:=time();
        image!-mod!-p:=find!-a!-valid!-prime(image!-lc,image!-poly,
          not numberp image!-content);
        if not(image!-mod!-p='not!-square!-free) then <<
%         trace!-time
%           display!-time("  Prime and image mod p found in ",
%             time()-wtime);
          i:=iadd1 i;
          putv(valid!-image!-sets,i,list(
            image!-set,chosen!-prime,image!-lc,image!-mod!-p,
            image!-content,image!-poly,f!-numvec));
          forbidden!-primes:=chosen!-prime . forbidden!-primes >>;
        j:=iadd1 j
        >>
      >>
  end;

symbolic procedure choose!-the!-best!-set();
% Given several random sets we now choose the best by factoring
% each image mod its chosen prime and taking one with the
% lowest factor count as the best for hensel growth.
  begin scalar split!-list,poly!-mod!-p,null!-space!-basis,
               known!-factors,w,n,fnum,remaining!-split!-list;
    modular!-info:=mkvect no!-of!-random!-sets;
%   wtime:=time();
    for i:=1:no!-of!-random!-sets do <<
      w:=getv(valid!-image!-sets,i);
      get!-factor!-count!-mod!-p(i,get!-image!-mod!-p w,
        get!-chosen!-prime w,not numberp get!-image!-content w) >>;
    split!-list:=sort(split!-list,function lessppair);
            % this now contains a list of pairs (m . n) where
            % m is the no: of factors in image no: n. the list
            % is sorted with best split (smallest m) first.
%   trace!-time
%     display!-time("  Factor counts found in ",time()-wtime);
    if caar split!-list = 1 then <<
      irreducible:=t; return nil >>;
    w:=nil;
%   wtime:=time();
    for i:=1:no!-of!-best!-sets do <<
      n:=cdar split!-list;
      get!-factors!-mod!-p(n,
          get!-chosen!-prime getv(valid!-image!-sets,n));
      w:=(car split!-list) . w;
      split!-list:=cdr split!-list >>;
            % pick the best few of these and find out their
            % factors mod p.
%   trace!-time
%     display!-time("  Best factors mod p found in ",time()-wtime);
    remaining!-split!-list:=split!-list;
    split!-list:=reversip w;
            % keep only those images that are fully factored mod p.
%   wtime:=time();
    check!-degree!-sets(no!-of!-best!-sets,t);
            % the best image is pointed at by best!-set!-pointer.
%   trace!-time
%     display!-time("  Degree sets analysed in ",time()-wtime);
            % now if these didn't help try the rest to see
            % if we can avoid finding new image sets altogether:
    if bad!-case then <<
      bad!-case:=nil;
%     wtime:=time();
      while remaining!-split!-list do <<
        n:=cdar remaining!-split!-list;
        get!-factors!-mod!-p(n,
            get!-chosen!-prime getv(valid!-image!-sets,n));
        w:=(car remaining!-split!-list) . w;
        remaining!-split!-list:=cdr remaining!-split!-list >>;
%     trace!-time
%       display!-time("  More sets factored mod p in ",time()-wtime);
      split!-list:=reversip w;
%     wtime:=time();
      check!-degree!-sets(no!-of!-random!-sets - no!-of!-best!-sets,t);
            % best!-set!-pointer hopefully points at the best image.
%     trace!-time
%       display!-time("  More degree sets analysed in ",time()-wtime)
    >>;
    one!-complete!-deg!-analysis!-done:=t;
    factor!-trace <<
      w:=getv(valid!-image!-sets,best!-set!-pointer);
      prin2!* "The chosen image set is:  ";
      for each x in get!-image!-set w do <<
        prinvar car x; prin2!* "="; prin2!* cdr x; prin2!* "; " >>;
      terpri!*(nil);
      prin2!* "and chosen prime is "; printstr get!-chosen!-prime w;
      printstr "Image polynomial (made primitive) = ";
      printsf get!-image!-poly w;
      if not(get!-image!-content w=1) then <<
        prin2!* " with (extracted) content of ";
        printsf get!-image!-content w >>;
      prin2!* "The image polynomial mod "; prin2!* get!-chosen!-prime w;
      printstr ", made monic, is:";
      printsf get!-image!-mod!-p w;
      printstr "and factors of the primitive image mod this prime are:";
      for each x in getv(modular!-info,best!-set!-pointer)
         do printsf x;
      if (fnum:=get!-f!-numvec w) and not !*overview then <<
        printstr "The numeric images of each (square-free) factor of";
        printstr "the leading coefficient of the polynomial are as";
        prin2!* "follows (in order):";
        prin2!* "  ";
        for i:=1:length cdr factored!-lc do <<
          prin2!* getv(fnum,i); prin2!* "; " >>;
        terpri!*(nil) >>
      >>
  end;


%***********************************************************************
% Multivariate factorization part 3. Reconstruction of the
% chosen image over the integers.


symbolic procedure reconstruct!-image!-factors!-over!-integers();
% The Hensel construction from modular case to univariate
% over the integers.
  begin scalar best!-modulus,best!-factor!-count,input!-polynomial,
    input!-leading!-coefficient,best!-known!-factors,s,w,i,
    x!-is!-factor,x!-factor;
    s:=getv(valid!-image!-sets,best!-set!-pointer);
    best!-known!-factors:=getv(modular!-info,best!-set!-pointer);
    best!-modulus:=get!-chosen!-prime s;
    best!-factor!-count:=length best!-known!-factors;
    input!-polynomial:=get!-image!-poly s;
    if ldeg input!-polynomial=1 then
      if not(x!-is!-factor:=not numberp get!-image!-content s) then
        errorf list("Trying to factor a linear image poly: ",
          input!-polynomial)
      else begin scalar brecip,ww,om,x!-mod!-p;
        number!-of!-factors:=2;
        prime!-base:=best!-modulus;
        x!-factor:=!*k2f m!-image!-variable;
        putv(valid!-image!-sets,best!-set!-pointer,
          put!-image!-poly!-and!-content(s,lc get!-image!-content s,
            multf(x!-factor,get!-image!-poly s)));
        om:=set!-modulus best!-modulus;
        brecip:=modular!-reciprocal
          red (ww:=reduce!-mod!-p input!-polynomial);
        x!-mod!-p:=!*f2mod x!-factor;
        alphalist:=list(
          (x!-mod!-p . brecip),
          (ww . modular!-minus modular!-times(brecip,lc ww)));
        do!-quadratic!-growth(list(x!-factor,input!-polynomial),
          list(x!-mod!-p,ww),best!-modulus);
        w:=list input!-polynomial; % All factors apart from X-FACTOR.
        set!-modulus om
      end
    else <<
      input!-leading!-coefficient:=lc input!-polynomial;
      factor!-trace <<
        printstr
           "Next we use the Hensel Construction to grow these modular";
      printstr "factors into factors over the integers." >>;
      w:=reconstruct!.over!.integers();
      if irreducible then return t;
      if (x!-is!-factor:=not numberp get!-image!-content s) then <<
        number!-of!-factors:=length w + 1;
        x!-factor:=!*k2f m!-image!-variable;
        putv(valid!-image!-sets,best!-set!-pointer,
          put!-image!-poly!-and!-content(s,lc get!-image!-content s,
            multf(x!-factor,get!-image!-poly s)));
        fix!-alphas() >>
      else number!-of!-factors:=length w;
      if number!-of!-factors=1 then return irreducible:=t >>;
    if number!-of!-factors>target!-factor!-count then
      return bad!-case:=list get!-image!-set s;
    image!-factors:=mkvect number!-of!-factors;
    i:=1;
    factor!-trace
      printstr "The full factors of the image polynomial are:";
    for each im!-factor in w do <<
      putv(image!-factors,i,im!-factor);
      factor!-trace printsf im!-factor;
      i:=iadd1 i >>;
   if x!-is!-factor then <<
     putv(image!-factors,i,x!-factor);
     factor!-trace <<
       printsf x!-factor;
       printsf get!-image!-content
         getv(valid!-image!-sets,best!-set!-pointer) >> >>
  end;

symbolic procedure do!-quadratic!-growth(flist,modflist,p);
  begin scalar fhatvec,alphavec,factorvec,modfvec,facvec,
    current!-factor!-product,i,deltam,m;
    fhatvec:=mkvect number!-of!-factors;
    alphavec:=mkvect number!-of!-factors;
    factorvec:=mkvect number!-of!-factors;
    modfvec:=mkvect number!-of!-factors;
    facvec:=mkvect number!-of!-factors;
    current!-factor!-product:=1;
    i:=0;
    for each ff in flist do <<
      putv(factorvec,i:=iadd1 i,ff);
      current!-factor!-product:=multf(ff,current!-factor!-product) >>;
    i:=0;
    for each modff in modflist do <<
      putv(modfvec,i:=iadd1 i,modff);
      putv(alphavec,i,cdr get!-alpha modff) >>;
    deltam:=p;
    m:=deltam*deltam;
    while m<largest!-small!-modulus do <<
      quadratic!-step(m,number!-of!-factors);
      m:=m*deltam >>;
    hensel!-growth!-size:=deltam;
    alphalist:=nil;
    for j:=1:number!-of!-factors do
      alphalist:=(reduce!-mod!-p getv(factorvec,j) . getv(alphavec,j))
        . alphalist
  end;

symbolic procedure fix!-alphas();
% We extracted a factor x (where x is the image variable)
% before any alphas were calculated, we now need to put
% back this factor and its coresponding alpha which incidently
% will change the other alphas.
  begin scalar om,f1,x!-factor,a,arecip,b;
    om:=set!-modulus hensel!-growth!-size;
    f1:=reduce!-mod!-p input!-polynomial;
    x!-factor:=!*f2mod !*k2f m!-image!-variable;
    arecip:=modular!-reciprocal
      (a:=evaluate!-mod!-p(f1,m!-image!-variable,0));
    b:=times!-mod!-p(modular!-minus arecip,
      quotfail!-mod!-p(difference!-mod!-p(f1,a),x!-factor));
    alphalist:=(x!-factor . arecip) .
      (for each aa in alphalist collect
        ((car aa) . remainder!-mod!-p(times!-mod!-p(b,cdr aa),car aa)));
    set!-modulus om
  end;


%***********************************************************************
% Multivariate factorization part 4. Determining the leading
%  coefficients.

symbolic procedure determine!.leading!.coeffts();
% This function determines the leading coeffts to all but a constant
% factor which is spread over all of the factors before reconstruction.
  begin scalar delta,c,s;
    s:=getv(valid!-image!-sets,best!-set!-pointer);
    delta:=get!-image!-content s;
            % cont(the m!-input!-polynomial image).
    if not domainp lc multivariate!-input!-poly then
    << true!-leading!-coeffts:=
      distribute!.lc(number!-of!-factors,image!-factors,s,
        factored!-lc);
       if bad!-case then <<
         bad!-case:=list get!-image!-set s;
         target!-factor!-count:=number!-of!-factors - 1;
         if target!-factor!-count=1 then irreducible:=t;
         return bad!-case >>;
       delta:=car true!-leading!-coeffts;
       true!-leading!-coeffts:=cdr true!-leading!-coeffts;
            % if the lc problem exists then use Wang's algorithm to
            % distribute it over the factors.
       if not !*overview then factor!-trace <<
         printstr "We now determine the leading coefficients of the ";
         printstr "factors of U by using the factors of the leading";
         printstr "coefficient of U and their (square-free) images";
         printstr "referred to earlier:";
         for i:=1:number!-of!-factors do <<
           prinsf getv(image!-factors,i);
           prin2!* " with l.c.: ";
           printsf getv(true!-leading!-coeffts,i)
         >> >>;
       if not onep delta then factor!-trace <<
         if !*overview then
        << printstr
              "In determining the leading coefficients of the factors";
           prin2!* "of U, " >>;
         prin2!* "We have an integer factor, ";
         prin2!* delta;
         printstr ", left over that we ";
         printstr "cannot yet distribute correctly." >>
      >>
    else <<
      true!-leading!-coeffts:=mkvect number!-of!-factors;
      for i:=1:number!-of!-factors do
        putv(true!-leading!-coeffts,i,lc getv(image!-factors,i));
      if not onep delta then
        factor!-trace <<
          prin2!* "U has a leading coefficient = ";
          prin2!* delta;
          printstr " which we cannot ";
          printstr "yet distribute correctly over the image factors." >>
      >>;
    if not onep delta then
    << for i:=1:number!-of!-factors do
       << putv(image!-factors,i,multf(delta,getv(image!-factors,i)));
          putv(true!-leading!-coeffts,i,
            multf(delta,getv(true!-leading!-coeffts,i)))
       >>;
       divide!-all!-alphas delta;
       c:=expt(delta,isub1 number!-of!-factors);
       multivariate!-input!-poly:=multf(c,multivariate!-input!-poly);
       non!-monic:=t;
       factor!-trace <<
         printstr "(a) We multiply each of the image factors by the ";
         printstr "absolute value of this constant and multiply";
         prin2!* "U by ";
         if not(number!-of!-factors=2) then
           << prin2!* delta; prin2!* "**";
             prin2!* isub1 number!-of!-factors >>
         else prin2!* delta;
         printstr " giving new image factors";
         printstr "as follows: ";
         for i:=1:number!-of!-factors do
           printsf getv(image!-factors,i)
       >>
    >>;
            % If necessary, fiddle the remaining integer part of the
            % lc of m!-input!-polynomial.
  end;

endmodule;

end;
