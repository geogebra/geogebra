module ezgcdf; % Polynomial GCD algorithms.

% Author: A. C. Norman, 1981.

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


fluid '(!*exp
        !*gcd
        !*heugcd
        !*overview
        !*trfac
        alphalist
        bad!-case
        best!-known!-factors
        current!-modulus
        dmode!*
        factor!-level
        factor!-trace!-list
        full!-gcd
        hensel!-growth!-size
        image!-factors
        image!-set
        irreducible
        kord!*
        m!-image!-variable
        multivariate!-factors
        multivariate!-input!-poly
        non!-monic
        no!-of!-primes!-to!-try
        number!-of!-factors
        prime!-base
        reconstructing!-gcd
        reduced!-degree!-lclst
        reduction!-count
        target!-factor!-count
        true!-leading!-coeffts
        unlucky!-case);

global '(erfg!*);

symbolic procedure ezgcdf(u,v);
   % Entry point for REDUCE call in GCDF. We have to make sure that
   % the kernel order is correct if an error occurs in ezgcdf1.
   begin scalar erfgx,kordx,x;
      erfgx := erfg!*;
      kordx := kord!*;
      x := errorset2{'ezgcdf1,mkquote u,mkquote v};
      if null errorp x then return first x;
      % If ezgcdf fails, erfg!* can be set to t,
      % (e.g., in invlap(c/(p^3/8-9p^2/4+27/2*p-27)^2,p,t)), and
      % the kernel order not properly reset.
      erfg!* := erfgx;
      setkorder kordx;
      return gcdf1(u,v)
   end;

symbolic procedure ezgcdf1(u,v);
   % Entry point for REDUCE call in GCDF.
   poly!-abs gcdlist list(u,v) where factor!-level=0;

%symbolic procedure simpezgcd u;
% calculate the gcd of the polynomials given as arguments;
%  begin
%    scalar factor!-level,w;
%    factor!-level:=0;
%    u := for each p in u collect <<
%        w := simp!* p;
%        if (denr w neq 1) then
%           rederr "EZGCD requires polynomial arguments";
%        numr w >>;
%    return (poly!-abs gcdlist u) ./ 1
%  end;

%put('ezgcd,'simpfn,'simpezgcd);

symbolic procedure simpnprimitive p;
% Remove any simple numeric factors from the expression P;
  begin
    scalar np,dp;
    if atom p or not atom cdr p then
       rerror(ezgcd,2,"NPRIMITIVE requires just one argument");
    p := simp!* car p;
    if polyzerop(numr p) then return nil ./ 1;
    np := quotfail(numr p,numeric!-content numr p);
    dp := quotfail(denr p,numeric!-content denr p);
    return (np ./ dp)
  end;

put('nprimitive,'simpfn,'simpnprimitive);


symbolic procedure poly!-gcd(u,v);
   % U and V are standard forms.
   % Value is the gcd of U and V.
   begin scalar !*exp,z;
        if polyzerop u then return poly!-abs v
         else if polyzerop v then return poly!-abs u
         else if u=1 or v=1 then return 1;
        !*exp := t;
        % The case of one argument exactly dividing the other is
        % detected specially here because it is perhaps a fairly
        % common circumstance.
        if quotf1(u,v) then z := v
         else if quotf1(v,u) then z := u
         else if !*gcd then z := gcdlist list(u,v)
         else z := 1;
        return poly!-abs z
   end;

% moved('gcdf,'poly!-gcd);

symbolic procedure ezgcd!-comfac p;
  %P is a standard form
  %CAR of result is lowest common power of leading kernel in
  %every term in P (or NIL). CDR is gcd of all coefficients of
  %powers of leading kernel;
  if domainp p then nil . poly!-abs p
  else if null red p then lpow p . poly!-abs lc p
  else begin
    scalar power,coeflist,var;
    % POWER will be the first part of the answer returned,
    % COEFLIST will collect a list of all coefs in the polynomial
    % P viewed as a poly in its main variable,
    % VAR is the main variable concerned;
    var := mvar p;
    while mvar p=var and not domainp red p do <<
      coeflist := lc p . coeflist;
      p:=red p >>;
    if mvar p=var then <<
      coeflist := lc p . coeflist;
      if null red p then power := lpow p
      else coeflist := red p . coeflist >>
    else coeflist := p . coeflist;
    return power . gcdlist coeflist
  end;

symbolic procedure gcd!-with!-number(n,a);
% n is a number, a is a polynomial - return their gcd, given that
% n is non-zero;
    if n=1 or not atom n or flagp(dmode!*,'field) then 1
    else if domainp a
     then if a=nil then abs n
           else if not atom a then 1
           else gcddd(n,a)
    else gcd!-with!-number(gcd!-with!-number(n,lc a),red a);

% moved('gcdfd,'gcd!-with!-number);


symbolic procedure contents!-with!-respect!-to(p,v);
    if domainp p then nil . poly!-abs p
    else if mvar p=v then ezgcd!-comfac p
    else begin
      scalar w,y;
      y := updkorder v;
      w := ezgcd!-comfac reorder p;
      setkorder y;
      return w
    end;

symbolic procedure numeric!-content form;
% Find numeric content of non-zero polynomial.
   if domainp form then absf form
   else if null red form then numeric!-content lc form
   else begin
     scalar g1;
     g1 := numeric!-content lc form;
     if not (g1=1) then g1 := gcddd(g1,numeric!-content red form);
     return g1
   end;

symbolic procedure gcdlist l;
% Return the GCD of all the polynomials in the list L.
%
% First find all variables mentioned in the polynomials in L,
% and remove monomial content from them all. If in the process
% a constant poly is found, take special action. If then there
% is some variable that is mentioned in all the polys in L, and
% which occurs only linearly in one of them establish that as
% main variable and proceed to GCDLIST3 (which will take
% a special case exit). Otherwise, if there are any variables that
% do not occur in all the polys in L they can not occur in the GCD,
% so take coefficients with respect to them to get a longer list of
% smaller polynomials - restart. Finally we have a set of polys
% all involving exactly the same set of variables;
  if null l then nil
  else if null cdr l then poly!-abs car l
  else if domainp car l then gcdld(cdr l,car l)
  else begin
    scalar l1,gcont,x;
    % Copy L to L1, but on the way detect any domain elements
    % and deal with them specially;
    while not null l do <<
        if null car l then l := cdr l
        else if domainp car l then <<
          l1 := list list gcdld(cdr l,gcdld(mapcarcar l1,car l));
          l := nil >>
        else <<
          l1 := (car l . powers1 car l) . l1;
          l := cdr l >> >>;
    if null l1 then return nil
    else if null cdr l1 then return poly!-abs caar l1;
    % Now L1 is a list where each polynomial is paired with information
    % about the powers of variables in it;
    gcont := nil; % Compute monomial content on things in L;
    x := nil; % First time round flag;
    l := for each p in l1 collect begin
        scalar gcont1,gcont2,w;
        % Set GCONT1 to least power information, and W to power
        % difference;
        w := for each y in cdr p
                collect << gcont1 := (car y . cddr y) . gcont1;
                           car y . (cadr y-cddr y) >>;
        % Now get the monomial content as a standard form (in GCONT2);
        gcont2 := numeric!-content car p;
        if null x then << gcont := gcont1; x := gcont2 >>
        else << gcont := vintersection(gcont,gcont1);
                   % Accumulate monomial gcd;
                x := gcddd(x,gcont2) >>;
        for each q in gcont1 do if not(cdr q=0) then
            gcont2 := multf(gcont2,!*p2f mksp(car q,cdr q));
        return quotfail1(car p,gcont2,"Term content division failed")
                  . w
        end;
    % Here X is the numeric part of the final GCD.
    for each q in gcont do x := multf(x,!*p2f mksp(car q,cdr q));
%   trace!-time <<
%     prin2!* "Term gcd = ";
%     printsf x >>;
    return poly!-abs multf(x,gcdlist1 l)
  end;


symbolic procedure gcdlist1 l;
% Items in L are monomial-primitive, and paired with power information.
% Find out what variables are common to all polynomials in L and
% remove all others;
  begin
    scalar unionv,intersectionv,vord,x,l1,reduction!-count;
    unionv := intersectionv := cdar l;
    for each p in cdr l do <<
       unionv := vunion(unionv,cdr p);
       intersectionv := vintersection(intersectionv,cdr p) >>;
    if null intersectionv then return 1;
    for each v in intersectionv do
       unionv := vdelete(v,unionv);
    % Now UNIONV is list of those variables mentioned that
    % are not common to all polynomials;
    intersectionv := sort(intersectionv,function lesspcdr);
    if cdar intersectionv=1 then <<
       % I have found something that is linear in one of its variables;
       vord := mapcarcar append(intersectionv,unionv);
       l1 := setkorder vord;
%      trace!-time <<
%        prin2 "Selecting "; prin2 caar intersectionv;
%        prin2t " as main because some poly is linear in it" >>;
       x := gcdlist3(for each p in l collect reorder car p,nil,vord);
       setkorder l1;
       return reorder x >>
    else if null unionv then return gcdlist2(l,intersectionv);
%   trace!-time <<
%     prin2 "The variables "; prin2 unionv; prin2t " can be removed" >>;
    vord := setkorder mapcarcar append(unionv,intersectionv);
    l1 := nil;
    for each p in l do
        l1:=split!-wrt!-variables(reorder car p,mapcarcar unionv,l1);
    setkorder vord;
    return gcdlist1(for each p in l1 collect
      (reorder p . total!-degree!-in!-powers(p,nil)))
  end;


symbolic procedure gcdlist2(l,vars);
% Here all the variables in VARS are used in every polynomial
% in L. Select a good variable ordering;
  begin
    scalar x,x1,gg,lmodp,onestep,vord,oldmod,image!-set,gcdpow,
           unlucky!-case;
% In the univariate case I do not need to think very hard about
% the selection of a main variable!! ;
    if null cdr vars
      then return if !*heugcd and (x := heu!-gcd!-list(mapcarcar l))
                    then x
                   else gcdlist3(mapcarcar l,nil,list caar vars);
    oldmod := set!-modulus nil;
% If some variable appears at most to degree two in some pair of the
% polynomials then that will do as a main variable.  Note that this is
% not so useful if the two polynomials happen to be duplicates of each
% other, but still... ;

    vars := mapcarcar sort(vars,function greaterpcdr);
% Vars is now arranged with the variable that appears to highest
% degree anywhere in L first, and the rest in descending order;
    l := for each p in l collect car p .
      sort(cdr p,function lesspcdr);
    l := sort(l,function lesspcdadr);
% Each list of degree information in L is sorted with lowest degree
% vars first, and the polynomial with the lowest degree variable
% of all will come first;
    x := intersection(deg2vars(cdar l),deg2vars(cdadr l));
    if not null x then <<
%      trace!-time << prin2 "Two inputs are at worst quadratic in ";
%                     prin2t car x >>;
      go to x!-to!-top >>;   % Here I have found two polys with a common
                             % variable that they are quadratic in;
% Now generate modular images of the gcd to guess its degree wrt
% all possible variables;

% If either (a) modular gcd=1 or (b) modular gcd can be computed with
% just 1 reduction step, use that information to choose a main variable;
try!-again:  % Modular images may be degenerate;
    set!-modulus random!-prime();
    unlucky!-case := nil;
    image!-set := for each v in vars
                   collect (v . modular!-number next!-random!-number());
%   trace!-time <<
%     prin2 "Select variable ordering using P=";
%     prin2 current!-modulus;
%     prin2 " and substitutions from ";
%     prin2t image!-set >>;
    x1 := vars;
try!-vars:
    if null x1 then go to images!-tried;
    lmodp := for each p in l collect make!-image!-mod!-p(car p,car x1);
    if unlucky!-case then go to try!-again;
    lmodp := sort(lmodp,function lesspdeg);
    gg := gcdlist!-mod!-p(car lmodp,cdr lmodp);
    if domainp gg or (reduction!-count<2 and (onestep:=t)) then <<
%          trace!-time << prin2 "Select "; prin2t car x1 >>;
           x := list car x1; go to x!-to!-top >>;
    gcdpow := (car x1 . ldeg gg) . gcdpow;
    x1 := cdr x1;
    go to try!-vars;
images!-tried:
  % In default of anything better to do, use image variable such that
  % degree of gcd wrt it is as large as possible;
    vord := mapcarcar sort(gcdpow,function greaterpcdr);
%   trace!-time << prin2 "Select order by degrees: ";
%                  prin2t gcdpow >>;
    go to order!-chosen;

x!-to!-top:
    for each v in x do vars := delete(v,vars);
    vord := append(x,vars);
order!-chosen:
%   trace!-time << prin2 "Selected Var order = "; prin2t vord >>;
    set!-modulus oldmod;
    vars := setkorder vord;
    x := gcdlist3(for each p in l collect reorder car p,onestep,vord);
    setkorder vars;
    return reorder x
  end;

symbolic procedure gcdlist!-mod!-p(gg,l);
   if null l then gg
   else if gg=1 then 1
   else gcdlist!-mod!-p(gcd!-mod!-p(gg,car l),cdr l);

symbolic procedure deg2vars l;
    if null l then nil
    else if cdar l>2 then nil
    else caar l . deg2vars cdr l;

symbolic procedure vdelete(a,b);
    if null b then nil
    else if car a=caar b then cdr b
    else car b . vdelete(a,cdr b);

symbolic procedure vintersection(a,b);
  begin
    scalar c;
    return if null a then nil
    else if null (c:=assoc(caar a,b)) then vintersection(cdr a,b)
    else if cdar a>cdr c then
      if cdr c=0 then vintersection(cdr a,b)
      else c . vintersection(cdr a,b)
    else if cdar a=0 then vintersection(cdr a,b)
    else car a . vintersection(cdr a,b)
  end;


symbolic procedure vunion(a,b);
  begin
    scalar c;
    return if null a then b
    else if null (c:=assoc(caar a,b)) then car a . vunion(cdr a,b)
    else if cdar a>cdr c then car a . vunion(cdr a,delete(c,b))
    else c . vunion(cdr a,delete(c,b))
  end;


symbolic procedure mapcarcar l;
    for each x in l collect car x;


symbolic procedure gcdld(l,n);
% GCD of the domain element N and all the polys in L;
    if n=1 or n=-1 then 1
    else if l=nil then abs n
    else if car l=nil then gcdld(cdr l,n)
    else gcdld(cdr l,gcd!-with!-number(n,car l));

symbolic procedure split!-wrt!-variables(p,vl,l);
% Push all the coeffs in P wrt variables in VL onto the list L
% Stop if 1 is found as a coeff;
    if p=nil then l
    else if not null l and car l=1 then l
    else if domainp p then abs p . l
    else if member(mvar p,vl) then
        split!-wrt!-variables(red p,vl,split!-wrt!-variables(lc p,vl,l))
    else p . l;

symbolic procedure gcdlist3(l,onestep,vlist);
   % GCD of the nontrivial polys in the list L given that they all
   % involve all the variables that any of them mention,
   % and they are all monomial-primitive.
   % ONESTEP is true if it is predicted that only one PRS step
   % will be needed to compute the gcd - if so try that PRS step.
  begin
    scalar unlucky!-case,image!-set,gg,gcont,l1,w,w1,w2,
           reduced!-degree!-lclst,p1,p2;
    % Make all the polys primitive;
    l1:=for each p in l collect p . ezgcd!-comfac p;
    l:=for each c in l1 collect
        quotfail1(car c,comfac!-to!-poly cdr c,
                  "Content divison in GCDLIST3 failed");
    % All polys in L are now primitive.
    % Because all polys were monomial-primitive, there should
    % be no power of V to go in the result.
    gcont:=gcdlist for each c in l1 collect cddr c;
    if domainp gcont then if not(gcont=1)
      then errorf "GCONT has numeric part";
    % GCD of contents complete now;
    % Now I will remove duplicates from the list;
%   trace!-time <<
%      prin2t "GCDLIST3 on the polynomials";
%      for each p in l do print p >>;
    l := sort(for each p in l collect poly!-abs p,function ordp);
    w := nil;
    while l do <<
       w := car l . w;
       repeat l := cdr l until null l or not(car w = car l)>>;
    l := reversip w;
    w := nil;
%   trace!-time <<
%      prin2t "Made positive, with duplicates removed...";
%      for each p in l do print p >>;
    if null cdr l then return multf(gcont,car l);
       % That left just one poly;
    if domainp (gg:=car (l:=sort(l,function degree!-order))) then
      return gcont;
         % Primitive part of one poly is a constant (must be +/-1);
    if ldeg gg=1 then <<
    % True gcd is either GG or 1;
       if division!-test(gg,l) then return multf(poly!-abs gg,gcont)
       else return gcont >>;
    % All polys are now primitive and nontrivial. Use a modular
    % method to extract GCD;
    if onestep then <<
       % Try to take gcd in just one pseudoremainder step, because some
       % previous modular test suggests it may be possible;
       p1 := poly!-abs car l; p2 := poly!-abs cadr l;
       % Because polynomials are primitive and they have been normalised
       % wrt sign the only way that just one PRS step could lead to zero
       % would be if the two polys are identical.  In which case that
       % should be the GCD. Note that because I got to gcdlist3 at all
       % both polys should use (all of) the same set of variables, and
       % in particular should have the same main variable.
       if p1=p2 then <<
             if division!-test(p1,cddr l) then return multf(p1,gcont)>>
       else <<
%      trace!-time prin2t "Just one pseudoremainder step needed?";
       gg := poly!-gcd(lc p1,lc p2);
       w1 := multf(red p1, quotfail1(lc p2, gg,
        "Division failure when just one pseudoremainder step needed"));
       w2 := multf(red p2,negf quotfail1(lc p1, gg,
        "Division failure when just one pseudoremainder step needed"));
       w := ldeg p1 - ldeg p2;
       if w > 0 then w2 := multf(w2, (mksp(mvar p2, w) .* 1) .+ nil)
       else if w < 0
        then w1 := multf(w1, (mksp(mvar p1, -w) .* 1) .+ nil);
       gg := ezgcd!-pp addf(w1, w2);
%      trace!-time printsf gg;
       if division!-test(gg,l) then return multf(gg,gcont) >>>>;
       return gcdlist31(l,vlist,gcont,gg,l1)
   end;

symbolic procedure gcdlist31(l,vlist,gcont,gg,l1);
   begin scalar cofactor,lcg,old!-modulus,prime,w,w1,zeros!-list;
    old!-modulus:=set!-modulus nil; %Remember modulus;
    lcg:=for each poly in l collect lc poly;
%    trace!-time << prin2t "L.C.S OF L ARE:";
%      for each lcpoly in lcg do printsf lcpoly >>;
    lcg:=gcdlist lcg;
%    trace!-time << prin2!* "LCG (=GCD OF THESE) = ";
%      printsf lcg >>;
try!-again:
    unlucky!-case:=nil;
    image!-set:=nil;
    set!-modulus(prime:=random!-prime());
    % Produce random univariate modular images of all the
    % polynomials;
    w:=l;
    if not zeros!-list then <<
      image!-set:=
         zeros!-list:=try!-max!-zeros!-for!-image!-set(w,vlist);
%     trace!-time << prin2t image!-set;
%       prin2 " Zeros-list = ";
%       prin2t zeros!-list >>
      >>;
%   trace!-time prin2t list("IMAGE SET",image!-set);
    gg:=make!-image!-mod!-p(car w,car vlist);
%   trace!-time prin2t list("IMAGE SET",image!-set," GG",gg);
    if unlucky!-case then <<
%     trace!-time << prin2t "Unlucky case, try again";
%       print image!-set >>;
      go to try!-again >>;
    l1:=list(car w . gg);
make!-images:
    if null (w:=cdr w) then go to images!-created!-successfully;
    l1:=(car w . make!-image!-mod!-p(car w,car vlist)) . l1;
    if unlucky!-case then <<
%    trace!-time << prin2t "UNLUCKY AGAIN...";
%      prin2t l1;
%      print image!-set >>;
      go to try!-again >>;
    gg:=gcd!-mod!-p(gg,cdar l1);
    if domainp gg then <<
      set!-modulus old!-modulus;
%     trace!-time print "Primitive parts are coprime";
      return gcont >>;
    go to make!-images;
images!-created!-successfully:
    l1:=reversip l1; % Put back in order with smallest first;
    % If degree of gcd seems to be same as that of smallest item
    % in input list, that item should be the gcd;
    if ldeg gg=ldeg car l then <<
        gg:=poly!-abs car l;
%       trace!-time <<
%         prin2!* "Probable GCD = ";
%         printsf gg >>;
        go to result >>
    else if (ldeg car l=add1 ldeg gg) and
            (ldeg car l=ldeg cadr l) then <<
    % Here it seems that I have just one pseudoremainder step to
    % perform, so I might as well do it;
%       trace!-time <<
%          prin2t "Just one pseudoremainder step needed"
%          >>;
        gg := poly!-gcd(lc car l,lc cadr l);
        gg := ezgcd!-pp addf(multf(red car l,
            quotfail1(lc cadr l,gg,
         "Division failure when just one pseudoremainder step needed")),
         multf(red cadr l,negf quotfail1(lc car l,gg,
         "Divison failure when just one pseudoremainder step needed")));
%       trace!-time printsf gg;
        go to result >>;
    w:=l1;
find!-good!-cofactor:
    if null w then go to special!-case; % No good cofactor available;
    if domainp gcd!-mod!-p(gg,cofactor:=quotient!-mod!-p(cdar w,gg))
      then go to good!-cofactor!-found;
    w:=cdr w;
    go to find!-good!-cofactor;
good!-cofactor!-found:
    cofactor:=monic!-mod!-p cofactor;
%   trace!-time prin2t "*** Good cofactor found";
    w:=caar w;
%    trace!-time << prin2!* "W= ";
%      printsf w;
%      prin2!* "GG= ";
%      printsf gg;
%      prin2!* "COFACTOR= ";
%      printsf cofactor >>;
    image!-set:=sort(image!-set,function ordopcar);
%    trace!-time << prin2 "IMAGE-SET = ";
%      prin2t image!-set;
%      prin2 "PRIME= ";   prin2t prime;
%      prin2t "L (=POLYLIST) IS:";
%      for each ll in l do printsf ll >>;
    gg:=reconstruct!-gcd(w,gg,cofactor,prime,image!-set,lcg);
    if gg='nogood then go to try!-again;
    go to result;
special!-case: % Here I have to do the first step of a PRS method;
%   trace!-time << prin2t "*** SPECIAL CASE IN GCD ***";
%     prin2t l;
%     prin2t "----->";
%     prin2t gg >>;
    reduced!-degree!-lclst:=nil;
try!-reduced!-degree!-again:
%   trace!-time << prin2t "L1 =";
%     for each ell in l1 do print ell >>;
    w1:=reduced!-degree(caadr l1,caar l1);
    w:=car w1; w1:=cdr w1;
    if not domainp w and
       (domainp w1 or ldeg w neq ldeg w1) then go to try!-again;
%   trace!-time << prin2 "REDUCED!-DEGREE = "; printsf w;
%     prin2 " and its image = "; printsf w1 >>;
            % reduce the degree of the 2nd poly using the 1st. Result is
            % a pair : (new poly . image new poly);
    if domainp w and not null w then <<
      set!-modulus old!-modulus; return gcont >>;
            % we're done as they're coprime;
    if w and ldeg w = ldeg gg then <<
      gg:=w; go to result >>;
            % possible gcd;
    if null w then <<
            % the first poly divided the second one;
      l1:=(car l1 . cddr l1);  % discard second poly;
      if null cdr l1 then <<
         gg := poly!-abs caar l1;
         go to result >>;
      go to try!-reduced!-degree!-again >>;
            % haven't made progress yet so repeat with new polys;
    if ldeg w<=ldeg gg then <<
       gg := poly!-abs w;
       go to result >>
    else if domainp gcd!-mod!-p(gg,cofactor:=quotient!-mod!-p(w1,gg))
     then <<
       w := list list w;
       go to good!-cofactor!-found >>;
    l1:= if ldeg w <= ldeg caar l1 then
      ((w . w1) . (car l1 . cddr l1))
      else (car l1 . ((w . w1) . cddr l1));
            % replace first two polys by the reduced poly and the first
            % poly ordering according to degree;
    go to try!-reduced!-degree!-again;
            % need to repeat as we still haven't found a good cofactor;
result: % Here GG holds a tentative gcd for the primitive parts of
        % all input polys, and GCONT holds a proper one for the content;
    if division!-test(gg,l) then <<
      set!-modulus old!-modulus;
      return multf(gg,gcont) >>;
%   trace!-time prin2t list("Trial division by ",gg," failed");
    go to try!-again
  end;

symbolic procedure make!-a!-list!-of!-variables l;
  begin scalar vlist;
    for each ll in l do vlist:=variables!.in!.form(ll,vlist);
    return make!-order!-consistent(vlist,kord!*)
  end;

symbolic procedure make!-order!-consistent(l,m);
% L is a subset of M. Make its order consistent with that
% of M;
    if null l then nil
    else if null m then errorf("Variable missing from KORD*")
    else if car m member l then car m .
       make!-order!-consistent(delete(car m,l),cdr m)
    else make!-order!-consistent(l,cdr m);

symbolic procedure try!-max!-zeros!-for!-image!-set(l,vlist);
  if null vlist then error(50,"VLIST NOT SET IN TRY-MAX-ZEROS-...")
  else begin scalar z;
    z:=for each v in cdr vlist collect
      if domainp lc car l or null quotf(lc car l,!*k2f v) then
        (v . 0) else (v . modular!-number next!-random!-number());
    for each ff in cdr l do
      z:=for each w in z collect
        if zerop cdr w then
          if domainp lc ff or null quotf(lc ff,!*k2f car w) then w
          else (car w . modular!-number next!-random!-number())
        else w;
    return z
  end;

symbolic procedure
   reconstruct!-gcd(full!-poly,gg,cofactor,p,imset,lcg);
  if null addf(full!-poly,negf multf(gg,cofactor)) then gg
  else (lambda factor!-level;
    begin scalar number!-of!-factors,image!-factors,
    true!-leading!-coeffts,multivariate!-input!-poly,
    no!-of!-primes!-to!-try,
    irreducible,non!-monic,bad!-case,target!-factor!-count,
    multivariate!-factors,hensel!-growth!-size,alphalist,
    best!-known!-factors,prime!-base,
    m!-image!-variable, reconstructing!-gcd,full!-gcd;
    if not(current!-modulus=p) then
      errorf("GCDLIST HAS NOT RESTORED THE MODULUS");
            % *WARNING* GCDLIST does not restore the modulus so
              % I had better reset it here!  ;
    if poly!-minusp lcg then error(50,list("Negative GCD: ",lcg));
    full!-poly:=poly!-abs full!-poly;
    initialise!-hensel!-fluids(full!-poly,gg,cofactor,p,lcg);
%    trace!-time << prin2t "TRUE LEADING COEFFTS ARE:";
%      for i:=1:2 do <<
%        printsf getv(image!-factors,i);
%        prin2!* " WITH L.C.:";
%        printsf getv(true!-leading!-coeffts,i) >> >>;
    if determine!-more!-coeffts()='done then
      return full!-gcd;
    if null alphalist then alphalist:=alphas(2,
      list(getv(image!-factors,1),getv(image!-factors,2)),1);
    if alphalist='factors! not! coprime then
      errorf list("image factors not coprime?",image!-factors);
    if not !*overview then factor!-trace <<
      printstr
         "The following modular polynomials are chosen such that:";
      terpri();
      prin2!* "   a(2)*f(1) + a(1)*f(2) = 1 mod ";
      printstr hensel!-growth!-size;
      terpri();
      printstr "  where degree of a(1) < degree of f(1),";
      printstr "    and degree of a(2) < degree of f(2),";
      printstr "    and";
      for i:=1:2 do <<
        prin2!* "    a("; prin2!* i; prin2!* ")=";
        printsf cdr get!-alpha getv(image!-factors,i);
        prin2!* "and f("; prin2!* i; prin2!* ")=";
        printsf getv(image!-factors,i);
        terpri!* t >>
    >>;
    reconstruct!-multivariate!-factors(
      for each v in imset collect (car v . modular!-number cdr v));
    if irreducible or bad!-case then return 'nogood
    else return full!-gcd
  end) (factor!-level+1) ;

symbolic procedure initialise!-hensel!-fluids(fpoly,fac1,fac2,p,lcf1);
% ... ;
  begin scalar lc1!-image,lc2!-image;
    reconstructing!-gcd:=t;
    multivariate!-input!-poly:=multf(fpoly,lcf1);
    no!-of!-primes!-to!-try := 5;
    prime!-base:=hensel!-growth!-size:=p;
    number!-of!-factors:=2;
    lc1!-image:=make!-numeric!-image!-mod!-p lcf1;
    lc2!-image:=make!-numeric!-image!-mod!-p lc fpoly;
% Neither of the above leading coefficients will vanish;
    fac1:=times!-mod!-p(lc1!-image,fac1);
    fac2:=times!-mod!-p(lc2!-image,fac2);
    image!-factors:=mkvect 2;
    true!-leading!-coeffts:=mkvect 2;
    putv(image!-factors,1,fac1);
    putv(image!-factors,2,fac2);
    putv(true!-leading!-coeffts,1,lcf1);
    putv(true!-leading!-coeffts,2,lc fpoly);
    % If the GCD is going to be monic, we know the lc
    % of both cofactors exactly;
    non!-monic:=not(lcf1=1);
    m!-image!-variable:=mvar fpoly
  end;

symbolic procedure division!-test(gg,l);
% Predicate to test if GG divides all the polynomials in the list L;
    if null l then t
    else if null quotf(car l,gg) then nil
    else division!-test(gg,cdr l);



symbolic procedure degree!-order(a,b);
% Order standard forms using their degrees wrt main vars;
    if domainp a then t
    else if domainp b then nil
    else ldeg a<ldeg b;

symbolic procedure make!-image!-mod!-p(p,v);
% Form univariate image, set UNLUCKY!-CASE if leading coefficient
% gets destroyed;
  begin
    scalar lp;
    lp := degree!-in!-variable(p,v);
    p := make!-univariate!-image!-mod!-p(p,v);
    if not(degree!-in!-variable(p,v)=lp) then unlucky!-case := t;
    return p
  end;


symbolic procedure make!-univariate!-image!-mod!-p(p,v);
% Make a modular image of P, keeping only the variable V;
  if domainp p then
     if p=nil then nil
     else !*n2f modular!-number p
  else if mvar p=v then
     adjoin!-term(lpow p,
                  make!-univariate!-image!-mod!-p(lc p,v),
                  make!-univariate!-image!-mod!-p(red p,v))
    else plus!-mod!-p(
      times!-mod!-p(image!-of!-power(mvar p,ldeg p),
                    make!-univariate!-image!-mod!-p(lc p,v)),
      make!-univariate!-image!-mod!-p(red p,v));

symbolic procedure image!-of!-power(v,n);
  begin
    scalar w;
    w := assoc(v,image!-set);
    if null w then <<
       w := modular!-number next!-random!-number();
       image!-set := (v . w) . image!-set >>
    else w := cdr w;
    return modular!-expt(w,n)
  end;

symbolic procedure make!-numeric!-image!-mod!-p p;
% Make a modular image of P;
  if domainp p then
     if p=nil then 0
     else modular!-number p
    else modular!-plus(
      modular!-times(image!-of!-power(mvar p,ldeg p),
                    make!-numeric!-image!-mod!-p lc p),
      make!-numeric!-image!-mod!-p red p);


symbolic procedure total!-degree!-in!-powers(form,powlst);
% Returns a list where each variable mentioned in FORM is paired
% with the maximum degree it has. POWLST collects the list, and should
% normally be NIL on initial entry;
  if null form or domainp form then powlst
  else begin scalar x;
    if (x := atsoc(mvar form,powlst))
      then ldeg form>cdr x and rplacd(x,ldeg form)
    else powlst := (mvar form . ldeg form) . powlst;
    return total!-degree!-in!-powers(red form,
      total!-degree!-in!-powers(lc form,powlst))
  end;


symbolic procedure powers1 form;
% For each variable V in FORM collect (V . (MAX . MIN)) where
% MAX and MIN are limits to the degrees V has in FORM;
  powers2(form,powers3(form,nil),nil);

symbolic procedure powers3(form,l);
% Start of POWERS1 by collecting power information for
% the leading monomial in FORM;
    if domainp form then l
    else powers3(lc form,(mvar form . (ldeg form . ldeg form)) . l);

symbolic procedure powers2(form,powlst,thismonomial);
    if domainp form then
        if null form then powlst else powers4(thismonomial,powlst)
    else powers2(lc form,
                 powers2(red form,powlst,thismonomial),
                 lpow form . thismonomial);

symbolic procedure powers4(new,old);
% Merge information from new monomial into old information,
% updating MAX and MIN details;
  if null new then for each v in old collect (car v . (cadr v . 0))
  else if null old then for each v in new collect (car v . (cdr v . 0))
  else if caar new=caar old then <<
    % variables match - do MAX and MIN on degree information;
    if cdar new>cadar old then rplaca(cdar old,cdar new);
    if cdar new<cddar old then rplacd(cdar old,cdar new);
    rplacd(old,powers4(cdr new,cdr old)) >>
  else if ordop(caar new,caar old) then <<
    rplacd(cdar old,0); % Some variable not mentioned in new monomial;
    rplacd(old,powers4(new,cdr old)) >>
  else (caar new . (cdar new  . 0)) . powers4(cdr new,old);


symbolic procedure ezgcd!-pp u;
   %returns the primitive part of the polynomial U wrt leading var;
   quotf1(u,comfac!-to!-poly ezgcd!-comfac u);

symbolic procedure ezgcd!-sqfrf p;
   %P is a primitive standard form;
   %value is a list of square free factors;
  begin
    scalar pdash,p1,d,v;
    pdash := diff(p,v := mvar p);
    d := poly!-gcd(p,pdash); % p2*p3**2*p4**3*... ;
    if domainp d then return list p;
    p := quotfail1(p,d,"GCD division in FACTOR-SQFRF failed");
    p1 := poly!-gcd(p,
       addf(quotfail1(pdash,d,"GCD division in FACTOR-SQFRF failed"),
            negf diff(p,v)));
    return p1 . ezgcd!-sqfrf d
  end;

symbolic procedure reduced!-degree(u,v);
   %U and V are primitive polynomials in the main variable VAR;
   %result is pair: (reduced poly of U by V . its image) where by
   % reduced I mean using V to kill the leading term of U;
   begin scalar var,w,x;
%   trace!-time << prin2t "ARGS FOR REDUCED!-DEGREE ARE:";
%    printsf u;  printsf v >>;
    if u=v or quotf1(u,v) then return (nil . nil)
    else if ldeg v=1 then return (1 . 1);
%   trace!-time prin2t "CASE NON-TRIVIAL SO TAKE A REDUCED!-DEGREE:";
    var := mvar u;
    if ldeg u=ldeg v then x := negf lc u
    else x:=(mksp(var,ldeg u - ldeg v) .* negf lc u) .+ nil;
    w:=addf(multf(lc v,u),multf(x,v));
%   trace!-time printsf w;
    if degr(w,var)=0 then return (1 . 1);
%   trace!-time << prin2 "REDUCED!-DEGREE-LCLST = ";
%     print reduced!-degree!-lclst >>;
    reduced!-degree!-lclst := addlc(v,reduced!-degree!-lclst);
%   trace!-time << prin2 "REDUCED!-DEGREE-LCLST = ";
%     print reduced!-degree!-lclst >>;
    if x := quotf1(w,lc w) then w := x
    else for each y in reduced!-degree!-lclst do
      while (x := quotf1(w,y)) do w := x;
    u := v; v := ezgcd!-pp w;
%   trace!-time << prin2t "U AND V ARE NOW:";
%     printsf u; printsf v >>;
    if degr(v,var)=0 then return (1 . 1)
    else return (v . make!-univariate!-image!-mod!-p(v,var))
  end;


% moved('comfac,'ezgcd!-comfac);

% moved('pp,'ezgcd!-pp);

endmodule;

end;
