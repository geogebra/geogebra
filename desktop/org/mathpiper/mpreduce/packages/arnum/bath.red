%%%%%%%%%%%%%%%%%%%%%%%%%

% module README

 An Algebraic Number and Factorizer Package for REDUCE 3.2

      This code is copyright the authors and
        the University of Bath 1985


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

This is a short guide to the installation and use of the algebraic number
package.  Some familiarity with terms from Galois theory is assumed as is
familiarity with REDUCE. The routines were developed under REDUCE 3.1 and
3.2, and rely on various built-in functions. Occasionally a bug in one of
these functions may cause a confusing error message to  be  produced;  we
include a few fixes to some of these functions.

To  load  all the algebraic number code enter REDUCE and type
           in algin$
This will load and compile (when the compiler is  on)  all  the  relevant
pieces  of  the  package.   The  code  can  of  course  be  included as a
collection of modules in the usual way.  If the factoriser is not  needed
then the arithmetic functions alone may be loaded by typing
         in arithin$

To start using algebraic numbers, type
        on algebraics;
and this indicates that the algebraic domain is to be used. The call
      alpha := algof(f);
will set alpha to a representative of a root of f where f is a univariate
(irreducible)  polynomial with integral or algebraic coefficients.  Alpha
can then be used in calculations much as one might expect.  Alpha  is  in
general  a  polynomial  in algebraic kernels, particularly in the case of
quadratic roots. Higher degree numbers may be shifted or scaled if it  is
thought the result has a 'simpler' minimal polynomial than the given one.
The polynomial f is NOT checked for irreducibility (this would  take  too
long),  it  is  left  to the user to do this.  In particular, it is quite
possible to create dependent algebraics i.e.  one minimal  polynomial  is
reducible  over  an  extension  of  Q  by  some  of the other algebraics.
Provided dependent algebraics are  kept  separate  no  problems  will  be
encountered,  but care should be taken not to mix dependent algebraics in
an expression  as  non-trivial  representations  of  0  may  occur;  also
division  may  fail  if  such  expressions  are  used  (this produces the
message: "Unexpected factor of a minimal polynomial").

Once all the code has been loaded, a short test is available: use
          in algtest$
If all is well, this should respond with a few timings for the tests.  If
not,  some  sort  of  error  message  should be produced. This might help
pinpoint the area in need of attention. Of course, an error-free  run  of
the test does not imply error-free code...





Below is a summary of the functions supplied in this package:

alpha := algof(f);    this assigns to alpha an algebraic which is a
                      root of the univariate polynomial f.

showalgs();       print the minimal polynomials of the currently created
                      algebraics.

polyof(alpha, x);     result is the minimal polynomial of alpha over Z as a
                      univariate polynomial in the kernel x.

algfactor(f,a,b,..);  factorize the polynomial f over Q(a,b,..).
                      Any algebraic occurring in a coefficient of f must be
                      included in the list a, b, ...
                      If a, b, ... are omitted then factorization is over Z.

norm(f,a,b,..);       find the norm of f over Q(a,b,..).
                      If a, b, ... are omitted then the result is just f.

on tralg;             switches on tracing of the factorizer,
off tralg;            switches off ...

Interface to algebraic factorizer in symbolic mode:

The  top-level  function  is  algfactorf(f,l)  (in  algfac)  where f is a
standard form, and l is a list of algebraic kernels of the  type  (alg  .
algn),  in  the  order  "most  recently  created first" (this ordering is
crucial to correct operation). This returns (c . flist), where c  is  the
numeric content, and flist is a list of pairs (factor . multiplicity).

The funtion normf(f,alg) (in algnorm) produces the the standard form that
is the norm of the standard form f over the extension  by  the  algebraic
kernel  alpha.  The function normf1(f,alglist) returns the norm of f over
the extension by the list of kernels alglist.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGTEST

% some tests of the algebraic number and factorizer package

on algebraics$

% arithmetic;
a := algof(x*x-2)$
b := algof(x*x-3)$
c := algof(x*x-5)$

d := 1/(a+b+c)$
if d*(a+b+c) neq 1 then write "****arithmetic error****"$

% creation;
aa := algof(x*x-a)$
bb := algof(2*x*x-1)$
cc := algof(a*x*x-1)$
dd := algof(x**3+x*x+1)$

% factorizer;
a := algof(x*x+1)$
% b := algof(x*x-3)$
c := algof(x*x+5)$

on time$
<< write "factorizer problem 1:"; algfactor(x*x+x+1,a,b,c) >>$
if factor1*factor2 neq x*x+x+1 then
  write "****factorizer error (1st problem)****"$

% a := algof(x*x+1)$
b := algof(x*x-a)$       % NB sqrt a MUST be created AFTER a

<< write "factorizer problem 2:"; algfactor(x*x-a,a,b) >>$
if factor1*factor2 neq x*x-a then
  write "****factorizer error (2nd problem)****"$

off time$

end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% module ARITHIN

% File to read in algebraic arithmetic.

% the next two should be fluid - not globals!
unglobal '(dmode!* kord!*);
fluid '(dmode!* kord!*);

% alg-kord* is used to record the order of creation of algebraics -
% this is essential to correct manipulation of polynomials in
% algebraics.
lisp global '(alg!-kord!*);

in algmacros$  % macro definitions
in algsupport$  % miscellaneous functions
in algdom$  % domain definitions
in algarith$  % main arithmetic
in algrecip$  % calculation of reciprocals
in algcreate$  % creation of algebraics
in algnorm$  % calculation of norms
in fixes$  % fixes to cope with domains

end;


%%%%%%%%%%%%%%%%%%%%

% module trailc

% these are the patches for trailing coefficient and other tests in
% the factorize algorithm;

SYMBOLIC PROCEDURE FACTOR!-TRIALDIV(POLY,FLIST,M,LLIST);
% Combines the factors in FLIST mod M and test divides the result
% into POLY (over integers) to see if it goes. If it doesn't
% then DIDNTGO is returned, else the pair (D . Q) is
% returned where Q is the quotient obtained and D is the product
% of the factors mod M;
  IF POLYZEROP POLY THEN ERRORF "Test dividing into zero?"
  ELSE BEGIN SCALAR D,Q,tcpoly,tcoeff,x,oldmod,w,poly1,try1;
    factor!-trace <<
      prin2!* "We combine factors ";
      for each ff in flist do <<
        w:=assoc(ff,llist);
        prin2!* "f(";
        prin2!* cdr w;
        prin2!* "), " >> ;
      prin2!* "and try dividing : " >>;
    x := mvar poly;
    tcpoly :=trailing!.coefft(poly,x);
    tcoeff := 1;
    oldmod := set!-general!-modulus m;
    for each fac in flist do
      tcoeff := general!-modular!-times(tcoeff,trailing!.coefft(fac,x));
    if not zerop remainder(tcpoly,tcoeff) then <<
      factor!-trace printstr " it didn't go (tc test)";
      set!-modulus oldmod;
      return 'DIDNTGO >>;
  % it has passed the tc test - now try evaluating at 1;
    poly1 := eval!-at!-1 poly;
    try1 := 1;
    for each fac in flist do
      try1 := general!-modular!-times(try1,eval!-at!-1 fac);
    set!-modulus oldmod;
    if (zerop try1 and not zerop poly1) or
       not zerop remainder(poly1,try1) then <<
      factor!-trace printstr " it didn't go (test at 1)";
      return 'DIDNTGO >>;
  % it has passed both tests - work out longhand;
    D:=COMBINE(FLIST,M,LLIST);
    IF DIDNTGO(Q:=QUOTF(POLY,CAR D)) THEN <<
      FACTOR!-TRACE PRINTSTR " it didn't go (division fail)";
      RETURN 'DIDNTGO >>
    ELSE <<
      FACTOR!-TRACE PRINTSTR " it worked !";
      RETURN (CAR D . QUOTF(Q,CDR D)) >>
  END;



SYMBOLIC PROCEDURE COMBINE(FLIST,M,L);
% multiply factors in flist mod m;
% L is a list of the factors for use in FACTOR!-TRACE;
  BEGIN SCALAR OM,RES,W,LCF,LCFINV,LCFPROD;
%    FACTOR!-TRACE <<                         )
%      PRIN2!* "We combine factors ";         )  RJB:
%      FOR EACH FF IN FLIST DO <<             )  Moved to factor-trialdiv;
%        W:=ASSOC(FF,L);                      )  This is the only change to
%        PRIN2!* "f(";                        )  this routine.
%        PRIN2!* cdr w;                       )
%        PRIN2!* "), " >> ;                   )
%      PRIN2!* "and try dividing : " >>;      )
    LCF := LC CAR FLIST; % ALL LEADING COEFFTS SHOULD BE THE SAME;
    LCFPROD := 1;
% This is one of only two places in the entire factorizer where
% it is ever necessary to use a modulus larger than word-size;
    IF M>LARGEST!-SMALL!-MODULUS THEN <<
      OM:=SET!-GENERAL!-MODULUS M;
      LCFINV := GENERAL!-MODULAR!-RECIPROCAL LCF;
      RES:=GENERAL!-REDUCE!-MOD!-P CAR FLIST;
      FOR EACH FF IN CDR FLIST DO <<
        IF NOT LCF=LC FF THEN ERRORF "BAD LC IN FLIST";
        RES:=GENERAL!-TIMES!-MOD!-P(
            GENERAL!-TIMES!-MOD!-P(LCFINV,
                GENERAL!-REDUCE!-MOD!-P FF),RES);
        LCFPROD := LCFPROD*LCF >>;
      RES:=GENERAL!-MAKE!-MODULAR!-SYMMETRIC RES;
      SET!-MODULUS OM;
      RETURN (RES . LCFPROD) >>
    ELSE <<
      OM:=SET!-MODULUS M;
      LCFINV := MODULAR!-RECIPROCAL LCF;
      RES:=REDUCE!-MOD!-P CAR FLIST;
      FOR EACH FF IN CDR FLIST DO <<
        IF NOT LCF=LC FF THEN ERRORF "BAD LC IN FLIST";
        RES:=TIMES!-MOD!-P(TIMES!-MOD!-P(LCFINV,REDUCE!-MOD!-P FF),RES);
        LCFPROD := LCFPROD*LCF >>;
      RES:=MAKE!-MODULAR!-SYMMETRIC RES;
      SET!-MODULUS OM;
      RETURN (RES . LCFPROD) >>
  END;

symbolic procedure eval!-at!-1 f;
  % f a univariate standard form over Z;
  % return f(1);
  % NB: this is only called when f(1) neq 0, ie no nil to worry about.
  if atom f then f else (lc f) + eval!-at!-1(red f);

symbolic procedure try!.combining(l,poly,m,sofar);
  try!.combining1(l,poly,m,sofar,2);

SYMBOLIC PROCEDURE TRY!.COMBINING1(L,POLY,M,SOFAR,k);
% l is a list of factors, f(i), s.t. (product of the f(i) mod m) = poly
% but no f(i) divides poly over the integers. we find the combinations
% of the f(i) that yield the true factors of poly over the integers.
% sofar is a list of these factors found so far.
% start combining K at a time.
  IF POLY=1 THEN
    IF NULL L THEN SOFAR
    ELSE ERRORF(LIST("TOO MANY BAD FACTORS:",L))
  ELSE BEGIN SCALAR N,RES,FF,V,W,W1,COMBINED!.FACTORS,LL; % K removed here;
    N:=LENGTH L;
    IF N=1 THEN
      IF LDEG CAR L > (LDEG POLY)/2 THEN
        RETURN ('ONE! BAD! FACTOR . SOFAR)
      ELSE ERRORF(LIST("ONE BAD FACTOR DOES NOT FIT:",L));
    IF N=2 OR N=3 THEN <<
      W:=LC CDAR L; % The LC of all the factors is the same;
      WHILE NOT (W=LC POLY) DO POLY:=QUOTFAIL(POLY,W);
            % poly's LC may be a higher power of w than we want
            % and we must return a result with the same
            % LC as each of the combined factors;
      IF NOT !*OVERVIEW THEN FACTOR!-TRACE <<
        PRINTSTR "We combine:";
         FOR EACH LF IN L DO FAC!-PRINTSF CDR LF;
         PRIN2!* " mod "; PRIN2!* M;
         PRINTSTR " to give correct factor:";
         FAC!-PRINTSF POLY >>;
       COMBINE!.ALPHAS(L,T);
       RETURN (POLY . SOFAR) >>;
    LL:=FOR EACH FF IN L COLLECT (CDR FF . CAR FF);
%    K := 2;  K is now an argument to try.combining1;
  LOOP1:
      IF K > N/2 THEN GO TO EXIT;
      W:=KOUTOF(K,IF 2*K=N THEN CDR L ELSE L,NIL);
      WHILE W AND (V:=FACTOR!-TRIALDIV(POLY,CAR W,M,LL))='DIDNTGO DO
      << W:=CDR W;
        WHILE W AND
            ((CAR W = '!*LAZYADJOIN) OR (CAR W = '!*LAZYKOUTOF)) DO
          IF CAR W= '!*LAZYADJOIN THEN
            W:=LAZY!-ADJOIN(CADR W,CADDR W,CADR CDDR W)
          ELSE W:=KOUTOF(CADR W,CADDR W,CADR CDDR W)
        >>;
      IF NOT(V='DIDNTGO) THEN <<
        FF:=CAR V; V:=CDR V;
        IF NOT !*OVERVIEW THEN FACTOR!-TRACE <<
          PRINTSTR "We combine:";
           FOR EACH A IN CAR W DO FAC!-PRINTSF A;
         PRIN2!* " mod "; PRIN2!* M;
         PRINTSTR " to give correct factor:";
         FAC!-PRINTSF FF >>;
       FOR EACH A IN CAR W DO <<
         W1:=L;
         WHILE NOT (A = CDAR W1) DO W1:=CDR W1;
         COMBINED!.FACTORS:=CAR W1 . COMBINED!.FACTORS;
         L:=DELETE(CAR W1,L) >>;
       COMBINE!.ALPHAS(COMBINED!.FACTORS,T);
%%% Now try combining the remaining factors, starting with k-tuples.
       RES:=try!.combining1(l,v,m,ff . sofar,k);
       GO TO EXIT>>;
    K := K + 1;
    GO TO LOOP1;
  EXIT:
    IF RES THEN RETURN RES
    ELSE <<
      W:=LC CDAR L; % The LC of all the factors is the same;
      WHILE NOT (W=LC POLY) DO POLY:=QUOTFAIL(POLY,W);
            % poly's LC may be a higher power of w than we want
            % and we must return a result with the same
            % LC as each of the combined factors;
      IF NOT !*OVERVIEW THEN FACTOR!-TRACE <<
        PRINTSTR "We combine:";
          FOR EACH FF IN L DO FAC!-PRINTSF CDR FF;
          PRIN2!* " mod "; PRIN2!* M;
          PRINTSTR " to give correct factor:";
          FAC!-PRINTSF POLY >>;
      COMBINE!.ALPHAS(L,T);
      RETURN (POLY . SOFAR) >>
  END;

end;


%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGIN
%
% File to read in algebraic arithmetic and algebraic factorizer.
% the next two should be fluid - not globals!
unglobal '(dmode!* kord!*);
fluid '(dmode!* kord!*);
% alg-kord* is used to record the order of creation of algebraics -
% this is essential to correct manipulation of polynomials in
% algebraics.
lisp global '(alg!-kord!*);
in algmacros$  % macro definitions
in algsupport$  % miscellaneous functions
in algdom$  % domain definitions
in algarith$  % main arithmetic
in algrecip$  % calculation of reciprocals
in algcreate$  % creation of algebraics
in algnorm$  % calculation of norms
in algfac$  % Trager factorizer
in trailc$  % update to integer factorizer
in fixes$  % fixes to cope with domains
end;

%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGSUPPORT
%
% Miscellaneous support functions for algebraics;

symbolic procedure degree!-in!-term(f,v);
  % f a standard form, v a kernel;
  % returns degree of f in v;
  if tvar f = v then tdeg f
  else degree!-in!-form(tc f,v);

symbolic procedure substitute(f,v);
  % f a univariate standard form, v an algebraic kernel;
  % substitutes v for mvar f in f;
  % returns a sf;
  if numberp f then f
  else if domainp f then numr cdr f
  else
    v .** ldeg f .* substitute(lc f,v) .+  substitute(red f,v);

symbolic procedure newsubf(f,g);
  % f a univariate sf, g an sq;
  % return the sq resulting from substituting g into f;
  if domainp f then !*f2q f
  else
    addsq(newsubf(red f,g),multsq(!*f2q lc f,exptsq(g,ldeg f)));

symbolic procedure cons!-count f;
  % a measure of the complexity of f;
  if atom f or algebraicp f then 0
  else cons!-count(car f) + cons!-count(cdr f) + 1;

symbolic procedure extract(f,v,n);
  % f a standard form, v a kernel;
  % returns coefficient of v**n in f;
  if domainp f then
    if n=0 then f else nil
  else if mvar f = v then <<
    while not domainp f and degree!-in!-form(f,v) > n do
      f := red f;
    if null f or degree!-in!-form(f,v) neq n then nil
    else if n=0 then f else lc f
  >>
  else if ordop(v,mvar f) then
    if n=0 then f else nil
  else addf( multf( !*p2f lpow f, extract(lc f,v,n) ),
             extract(red f,v,n) );

symbolic procedure degree!-in!-form(f,v);
  % f a standard form, v a kernel;
  % returns degree of f in v;
  if domainp f then 0
  else if mvar f = v then ldeg f
  else if ordop(v,mvar f) then 0
  else max( degree!-in!-form(lc f,v),
            degree!-in!-form(red f,v) );

symbolic procedure collect!-kernels f;
  % f standard form. Returns list of non-algebraic kernels
  % in f, or nil if none;
  if domainp f then nil
  else if algebraicp mvar f then
    union(collect!-kernels lc f,collect!-kernels red f)
  else
    union(list mvar f,
          union(collect!-kernels lc f,collect!-kernels red f));

symbolic procedure get!-mvar f;
  % f an sf with algebraics and kernels;
  % return most main non-algebraic kernel, or nil if none present;
  (lambda kerlist;
    if null kerlist then nil
    else car kerlist
  ) sort(collect!-kernels f,'ordp);

symbolic procedure adjust!-algebraics f;
  % f an sf, returns an sf
  % reorder f so that algebraic kernels are last;
  if domainp f then f
  else begin scalar ker,form;
    ker := get!-mvar f;
    if null ker then return f;
    (lambda coeff;
      if not null coeff then <<
        form := adjust!-algebraics coeff;
        f := addf(f,negf(coeff))
      >>
      else form := nil
    ) extract(f,ker,0);
    for i := 1:degree!-in!-form(f,ker) do
      (lambda coeff;
        if not null coeff then <<
          form := ((ker .** i) .* adjust!-algebraics coeff) . form;
          f := addf(f,negf( (ker .** i) .* coeff .+ nil ))
        >>
      ) extract(f,ker,i);
    return form
  end;

symbolic procedure num!-content f;
% find numeric content of non-zero polynomial;
  if domainp f then absf f
  else if null red f then num!-content lc f
  else begin scalar g1;
    g1 := num!-content lc f;
    if not (g1=1) then g1 := gcddd(g1,num!-content red f);
    return g1
  end;

end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% module FIXES

% Fixes for REDUCE 3.2 to cope properly with domains;
%
% The problem occurs when two domain elements are multiplied together
% to produce an integer, eg i*i -> -1, as the tagged domain type
% (:alg: ....) becomes an untagged integer. This results in (usually)
% an illegal car or cdr access to the integer that was believed to be
% tagged.

symbolic procedure multdm(u,v);
  % not all results are strictly domain elements...
  if atom u and atom v then times2(u,v)
  else int!-equiv!-chk dcombine(u,v,'times);

symbolic procedure !:expt(u,n);
   % raises domain element u to power n.  value is a domain element;
   if null u then if n=0 then rederr "0/0 formed" else nil
    else if n=0 then 1
    else if n<0
     then !:recip !:expt(if not fieldp u then mkratnum u else u,-n)
    else if atom u then u**n
    else begin scalar v,w,x;
%      v := apply1(get(car u,'i2d),1);   %unit element;
%      x := get(car u,'times);
      v := 1;
   a: w := divide(n,2);
      if cdr w=1 then v := multf(u,v);  % was "apply(x,list(u,v))";
      if car w=0 then return v;
      u := multf(u,u);    % was "apply(x,list(u,u))";
      n := car w;
      go to a
   end;

SYMBOLIC PROCEDURE MULTD(U,V);
   %U is a domain element, V a standard form.
   %Value is standard form for U*V;
   IF NULL V THEN NIL
    ELSE IF DOMAINP V THEN MULTDM(U,V)
    ELSE adjoin!-term(LPOW V, MULTD(U,LC V), MULTD(U,RED V));

%
% Not sure why we fix this one;
%
SYMBOLIC PROCEDURE RECIPROCAL!-BY!-GCD(A,B,X,Y);
%On input A and B should be coprime. This routine then
%finds X and Y such that A*X+B*Y=1, and returns the value Y
%on input A > B;
   IF B=0 THEN ERRORF "INVALID MODULAR DIVISION"
   ELSE IF B=1 THEN IF Y < 0 THEN Y+CURRENT!-MODULUS ELSE Y
   ELSE BEGIN SCALAR W;
%N.B. Invalid modular division is either:
% a)  attempt to divide by zero directly
% b)  modulus is not prime, and input is not
%     coprime with it;
     W:=divide(A,B); % quotient . remainder;
     RETURN RECIPROCAL!-BY!-GCD(B,cdr w,Y,X-Y*car W)
   END;

end;


%%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGARITH

% General arithmetic routines for algebraics;

symbolic procedure algmultsq(u,v);
  % u,v are both sqs with groundp denrs;
  % returns an sq;
  begin scalar numer,dener,hcf;
    numer := algmultf(numr u,numr v);
    dener := denr u * denr v;      % denr's don't contain algebraics;
    hcf := gcdf!*(numer,dener);    % can't be an algebraic hcf;
    numer := quotf(numer,hcf);
    dener := quotf(dener,hcf);
    return
      if minusp dener then
        negatef numer ./ -dener
      else
        numer ./ dener
  end;

symbolic procedure algmultf(u,v);
  % u,v both sf's, containing only algebraics and groundp's;
  % value is standard form for u*v;
  if null u or null v then nil
  else if onep u then v
  else if onep v then u
  else if numberp u then multn(u, v)
  else if numberp v then multn(v, u)
  else if mvar u = mvar v then algmultcancel(u, v)
  else if ordop(mvar u, mvar v)
         then lpow u .* algmultf(lc u, v) .+ algmultf(red u, v)
         else lpow v .* algmultf(lc v, u) .+ algmultf(red v, u);

symbolic procedure multn(n, f);
  % n is an integer, f is a SF over Z : result is SF for n*f.
  if null f then nil
  else if onep n then f
  else if numberp f then times2(n, f)
  else lpow f .* multn(n, lc f) .+ multn(n, red f);

symbolic procedure algmultcancel(u, v);
  % u, v are SFs over Z in only alg kernels with the same mvar.
  % Result is SF for u*v reduced mod the minpoly.
  begin scalar ans;
    ans := algmult(u, v);
    return
      if ldeg ans < degree!-of!-algebraic mvar ans then ans
      else algmodf(ans, min!-poly!-of mvar ans)
  end;

symbolic procedure algmult(u, v);
  % u, v are SFs over Z in only alg kernels with mvar u <= mvar v.
  % Result is SF for u*v with terms in the mvar not reduced mod the minpoly.
  if null u then nil
  else if numberp u then multn(u, v)
  else if mvar u neq mvar v then algmultf(u, v)
  else addf(algmult(red u, v), algmultt(lt u, v));

symbolic procedure algmultt(term, poly);
  % term is a term, poly is a SF over Z : result is SF for term*poly.
  if null poly then nil
  else if numberp poly then tpow term .* multn(poly, tc term) .+ nil
  else if tvar term neq mvar poly then
    tpow term .* algmultf(tc term, poly) .+ nil
  else
    mvar poly .** (tdeg term + ldeg poly) .* algmultf(lc poly, tc term)
                                          .+ algmultt(term, red poly);

symbolic procedure algmodf(f, g);
  % f, g are SFs over Z in only alg kernels : result is f mod g.
  if null f or numberp f or mvar f neq mvar g or ldeg f < ldeg g then f
  else if ldeg f = ldeg g then
    addf(f, algmultf(negf lc f, g))
  else
    algmodf(addf(f, algmultt(mvar f .** (ldeg f - ldeg g)
                                    .* negf lc f, g)),
            g);

symbolic procedure algquotientsq(u,v);
  % u,v both sq's, return an sq;
  if not algebraic!-mvarp numr v then
    if minusp numr v then
      algmultsq(u, -denr v ./  -numr v)
    else
      algmultsq(u,denr v ./ numr v)
  else
    algmultsq(u, algmultsq((denr v ./ 1),algrecip numr v));

symbolic procedure negatesq u;
  % u an sq, return -u;
  if null car u then u
  else (negatef car u) ./ cdr u;

symbolic procedure differencesq(u,v);
  % u,v sq's, return u - v;
  addsq(u,negatesq v);

symbolic procedure negatef u;
  % u a sf, return -u;
  if null u then nil
  else if domainp u then multdm(-1,u)
  else lpow u .* negatef(lc u) .+ negatef(red u);

symbolic procedure differencef(u,v);
  % u,v sfs, return u - v;
  addf(u,negatef v);

end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGDOM

% algebraics using domains 23/4/85;

symbolic;

fluid '(!*algebraics);      % 'on algebraics;' switches on algebraics;

global '(domainlist!*);
domainlist!* := union( '(!:alg!:), domainlist!*);

% an ":alg:" is (:alg: . sq), where the sq contains algebraics of the
% form (alg . algn) in the numerator, and only groundp's in the
% denominator;

put('algebraics,'tag,'!:alg!:);
put('!:alg!:,'dname,'algebraics);
flag( '(!:alg!:), 'field);
put('!:alg!:,'i2d,'!*i2alg);
put('!:alg!:,'minusp,'algminusp!:);
put('!:alg!:,'plus,'algplus!:);
put('!:alg!:,'times,'algtimes!:);
put('!:alg!:,'difference,'algdifference!:);
put('!:alg!:,'quotient,'algquotient!:);
put('!:alg!:,'zerop,'algzerop!:);
put('!:alg!:,'prepfn,'algprep!:);
put('!:alg!:,'specprn,'!:alg!:prin);
put('alg,'specprn,'algprin);

% conversion functions;

put('!:alg!:,'!:rn!:,'algcnv);
put('!:alg!:,'!:ft!:,'algcnv);
put('!:alg!:,'!:mod!:,'algcnv);

algebraic;

symbolic procedure !*i2alg u;
  % integer -> algebraic;
  '!:alg!: . ((if u = 0 then nil else u) ./ 1);

symbolic procedure mkalg u;
  % u an sq with groundp denominator, possibly containing kernels other
  % than algebraics in the numerator. Convert this to the standard tagged
  % form;
  (lambda (numer,dener);
    if null numer then nil
    else if has!-algebraic numer then
      (lambda kers;
        if null kers then mkalg1 u
        else mkalg2(adjust!-algebraics numer,dener)
      ) collect!-kernels numer
    else if onep dener then numer
    else mkalg1 u
  ) (numr u,denr u);

symbolic procedure has!-algebraic f;
  % untagged sf f: are there any (alg . algn)?
  if null f or numberp f then nil
  else
    (domainp f and eqcar(f,'alg)) or
    eqcar(mvar f,'alg) or
    has!-algebraic red f or
    has!-algebraic lc f;

symbolic procedure mkalg1 u;
  % u an sq;
  '!:alg!: . u;

symbolic procedure mkalg2(f,n);
  % f an sf containing untagged algebraics - tag them using n as
  % a denominator;
  if groundp f or domainp f then f
  else if algebraic!-mvarp f then mkalg1(f ./ n)
  else begin scalar newlc,newred;
    newlc := mkalg2(lc f,n);
    newred := mkalg2(red f,n);
    return
      if newlc eq lc f then
        if newred eq red f then f
        else lt f .+ newred
      else (lpow f) .* newlc .+ newred
  end;

symbolic procedure algcnv u;
  rederr list("Conversion between algebraics and",
               get(car u,'dname),"not defined");

symbolic procedure algminusp!: u;
  % not sure on this one;
  minusf cadr u;

symbolic procedure algplus!:(u,v);
  % nothing nasty happens here if we turn off dmode*.
  % We rebind kord* to the algebraic ordering;
  begin scalar dmode!*,kord!*;
    kord!* := alg!-kord!*;
    return mkalg addsq(cdr u,cdr v)
  end;

symbolic procedure algtimes!:(u,v);
  % but here we must use the new multiplier;
  % there is a fudge for :expt, which doesn't expect powers of domain
  % elements to be non-domain elements;
  begin scalar dmode!*,kord!*;
    kord!* := alg!-kord!*;
    if atom u or car u neq '!:alg!: then u := mkalg1(u ./ 1);
    if atom v or car v neq '!:alg!: then v := mkalg1(v ./ 1);
    return mkalg algmultsq(cdr u,cdr v)
  end;

symbolic procedure algdifference!:(u,v);
  begin scalar dmode!*,kord!*;
    kord!* := alg!-kord!*;
    return mkalg differencesq(cdr u,cdr v)
  end;

symbolic procedure algquotient!:(u,v);
  % and here the new quotient;
  begin scalar dmode!*,kord!*;
    kord!* := alg!-kord!*;
    return mkalg algquotientsq(cdr u,cdr v)
  end;

symbolic procedure algzerop!: u;
  null cadr u;

symbolic procedure algprep!: u;
  prepsq cdr u;

symbolic procedure !:alg!:prin u;
  % u a sq with algebraics perhaps;
  if cdr u = 1 then maprin prepsq!* u
  else <<
    prin2!* "(";
    maprin prepsq!* u;
    prin2!* ")"
  >>;

symbolic procedure algprin u;
  % u is a gensym;
  if ldeg gts u = 2 then <<
    prin2!* "Sqrt(";
    xprinf(negatef red gts u,nil,nil);
    prin2!* ")"
  >>
  else prin2!* u;

symbolic initdmode 'algebraics;

end;

%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGFAC

% Trager algorithm for factorization over algebraic number fields;

lisp fluid '(!*tralg algfac!-level recursedp trivial!-factors);

symbolic procedure my!-subf(f,x,c);
  % substitutes x + c for the kernel x in the standard form f,
  % c a domain element;
  % returns a standard form;
  if domainp f or (mvar f neq x and ordop(x,mvar f)) then f
  else begin scalar newred,newlc;
    newred := my!-subf(red f,x,c);
    newlc  := my!-subf(lc f,x,c);
    return
      if mvar f neq x then
        if newred eq red f and newlc eq lc f then f
        else addf(multf(newlc,!*p2f lpow f),newred)
      else addf(multf(newlc,exptf(x .** 1 .* 1 .+ c,ldeg f)),newred)
    end;

symbolic procedure my!-quotf(u,v);
  % u and v standard forms, probably with algebraics, v dividing u up to a
  % numeric factor. Returns u/v ignoring this factor;
  numr quotsq(!*f2q u,!*f2q v);

symbolic procedure variatep f;
  % does f contain a non-algebraic kernel?
  not null pick!-a!-kernel f;

symbolic procedure pick!-a!-kernel f;
  % f a standard form;
  % returns a non-algebraic kernel in f, or nil if none;
  if groundp f then nil
  else if not algebraicp mvar f then mvar f
  else
    (lambda (ker);
       if ker then ker else pick!-a!-kernel(red f) )
    (pick!-a!-kernel(lc f));

symbolic procedure sqfr!-norm(f,x,alg!-tower);
  % f a standard form over k(alpha),
  % where alpha = car alg!-tower, k = Q(cdr alg-tower);
  % value (s,g,R) where s is a non-negative integer,
  % g(x,alpha) = f(x-s*alpha,alpha), R(x) = Norm(g(x,alpha)),
  % and R is square-free, g and R are standard forms;
  % thus we map Norm:k(alpha) -> k;
  % Barry Trager's version of van der Waerden's algorithm;
  begin scalar alpha,s,g,R,minus!-alpha,!*ezgcd;
    alpha := car alg!-tower;
    minus!-alpha := multdm(mkalg1 !*k2q alpha,-1);
    s := 0;
    g := f;
    if not contains!-alpha(f,alpha) then << % we may increase s immediately;
      s := s+1;
      g := my!-subf(g,x,minus!-alpha)
    >>;
    R := normf(g,alpha);
    while degree!-in!-form( gcdf!*( R,diff(R,x) ),x ) neq 0 do <<
      repeat <<
        s := s+1;
        g := my!-subf(g,x,minus!-alpha)
      >> until contains!-alpha(g,alpha);
      R := normf(g,alpha)
    >>;
    if !*tralg then <<
      if s > 0 then <<
        prin2!* "we make a linear substitution ";
        prin2!* x; prin2!* " -> "; prin2!* x ;prin2!* " - ";
        if s > 1 then << prin2!* s; prin2!* "*" >>;
        algprin(cdr alpha);
        prin2!* " so that ";
      >>;
      printstr "the norm ";
      fac!-printsf R;
      printstr "is square-free, and we try to factorise this"
    >>;
    return list(s,g,R)
  end;

symbolic procedure pick!-minimal!-kernel f;
  % f a standard form. Picks non-algebraic kernel
  % of least degree, or errors if none;
  minimal!-ker( f,(lambda l;if null l then rederr "no kernel present"
                            else l)(collect!-kernels f) );

symbolic procedure minimal!-ker(f,l);
  % picks kernel of least degree in the standard form f
  % from the list of kernels l;
  if onep length l then car l
  else
    (lambda mink;
       if degree!-in!-form(f,car l) < degree!-in!-form(f,mink) then
         car l else mink)(minimal!-ker(f,cdr l));

symbolic procedure my!-factorf f;
  % f a standard form, square-free;
  % returns ( sf sf ... ), a list of the factors of f;
  begin scalar fac!-list;
    fac!-list :=                                        % remove content and
      for each fac in cdr factorf(f) collect car fac;   % multiplicities;
    if !*tralg then
      if length fac!-list = 1 then
        printstr "the norm is irreducible"
      else <<
        printstr "the norm factorises into";
        for each fac in fac!-list do
          fac!-printsf fac
      >>;
    return fac!-list
  end;

symbolic procedure normalise(u,x);
  % u a standard form, returns a standard form;
  if null u then nil
  else if groundp u then 1
  else my!-quotf(u,extract(u,x,degree!-in!-form(u,x)));

symbolic procedure alg!-factor(f,x,alg!-tower);
  % f a standard form over k(alpha), where car alg!-tower = alpha;
  % f is square-free;
  % returns a list of the factors of f over k(alpha),
  % as standard forms;
  % Barry Trager's version of van der Waerden's algorithm;
  begin scalar s,g,R,norm!-list,l,h,alpha,!*ezgcd;
    if degree!-in!-form(f,x) = 1 then <<
      if !*tralg then printstr "it is linear";
      return list f
    >>;
    if null alg!-tower and contains!-algebraic f then <<
      terpri!* t;prin2!* "***** ";algprin(cdr mvar f);terpri!*();
      rederr "unexpectedly found in factorisation"
    >>;
    if null alg!-tower then
      return my!-factorf(f);
    alpha := car alg!-tower;
    norm!-list := sqfr!-norm(f,x,alg!-tower);
    s := car norm!-list;
    g := cadr norm!-list;
    R := caddr norm!-list;
    l := alg!-factor(R,x,cdr alg!-tower);
         % we recurse down the tower;
    if length l = 1 then return list f;     % f irreducible;
    return
      for each h in l collect  <<
        h := gcdf!*(h,g);
        if s = 0 then normalise(h,x)
        else normalise(my!-subf(h,x,multdm(mkalg1 !*k2q alpha,s)),x)
      >>
  end;

symbolic procedure sqfr!-decompose(f,x);
  % f a standard form in x;
  % returns ( (sf . n) (sf . n) ... ), of the square-free parts
  % of f together with their multiplicities;
  sqfr!-decompose1(f,x,1);

symbolic procedure sqfr!-decompose1(f,x,n);
  % n a count of multiplicity so far;
  begin scalar q,r,s,!*ezgcd;
    r := gcdf!*( f,normalise(diff(f,x),x) );
    if degree!-in!-form(r,x) = 0 then return list( f . n );
    s := my!-quotf(f,r);
    q := my!-quotf(s,gcdf!*(r,s));
    return
      if q=1 then
        sqfr!-decompose1(r,x,n+1)
      else
        ( q . n ) . sqfr!-decompose1(r,x,n+1)
  end;

symbolic procedure simpfactorise u;
  % u is (sf . l), l a list of algebraics;
  if atom u then rederr "factorise needs arguments"
  else if null cdr u or null alg!-kord!* then
    simpfactorize u       % fall through to integer factorizer;
  else begin scalar f,l,algs,alg!-order;
    % get algebraics, and reorder correctly;
    alg!-order := alg!-kord!*;
    algs :=
      for each alg in cdr u collect
        (lambda a;
          if car a neq '!:alg!: then typerr(alg,'algebraic)
          else mvar cadr a) (!*a2f alg);
    while not null alg!-order do <<
      if member(car alg!-order,algs) then
        l := append(l,list(car alg!-order));
      alg!-order := cdr alg!-order
    >>;
    f := !*a2f car u;
    return algfactorf(f,l)
  end;

put('algfactor,'simpfn,'simpfactorise);

symbolic procedure algfactorf(f,algs);
  % f a standard form, algs a list of algebraic kernels;
  begin scalar algfac!-level,recursedp,factor!-list,z,factor!-count;
    algfac!-level := 0;                 % depth of recursion
    recursedp := nil;                   % flags for tralg;
    factor!-list := factorise1(f,algs);
    z := list( 0 . mk!*sq car(factor!-list) );
    factor!-count := 0;
    for each fff in cdr factor!-list do
      for i := 1:cdr fff do
        z := ((factor!-count := factor!-count + 1) .
               mk!*sq !*f2q(car fff)) . z;
    return multiple!-result(z,'factor)
  end;

symbolic procedure factorise1(f,alg!-tower);
  % f a standard form;
  % factorises f over Q(alg-tower);
  % returns (n . l): n numeric (non-variate) content as a standard quotient,
  % l a list of factors (standard forms) paired with their multiplicities;
  begin scalar x,fac!-list,con,n!-content;
    algfac!-level := algfac!-level + 1;
    x := pick!-minimal!-kernel f;
    if algfac!-level > 1 then
      recursedp := t;
    if !*tralg then <<
      if recursedp then prin2!* "now ";
      prin2!* "to factorise ";
      fac!-printsf f;
      prin2!* "we pick the kernel ";
      printvar x;
    >>;
    fac!-list := factorise2(f,alg!-tower,x);
    n!-content := 1;
    if algfac!-level = 1 then <<     % only bother with content at the end;
      con := 1;
      for each fac in fac!-list do
        for i:= 1:cdr fac do con := multf(con,lnc car fac);
      n!-content := quotsq(!*f2q lnc f,!*f2q con);
      if !*tralg then <<
        printstr "final result:";
        if n!-content neq (1 . 1) then <<
          prin2!* "the numeric content is ";
          printsq n!-content
        >>;
      printstr "the factors are";
      for each fac in fac!-list do
        fac!-printsf !*p2f mksp(prepf car fac,cdr fac)
      >>
    >>;
    algfac!-level := algfac!-level - 1;
    return n!-content . fac!-list
  end;

symbolic procedure factorise2(f,alg!-tower,x);
  % this is an entry for when x is already given;
  % we hand the polynomial to the routines that actually do the work
  % after a little tidying up;
  % returns ( (sf . n) (sf . n) ... );
  begin scalar fac!-list,unfac!-list,sqfr!-list,n!-content,p!-content,
               facl,trivial!-factors;
    if null extract(f,x,0) then   % we can divide out some x's;
      return factor!-by!-xes(f,x,alg!-tower);
    n!-content := num!-content f;
    f := quotfd(f,n!-content);
    p!-content := polynomial!-content(f,x);
    f := my!-quotf(f,p!-content);
    if variatep p!-content then <<
      if !*tralg then <<
        prin2!* "then extract a content of ";
        fac!-printsf p!-content
      >>;
      fac!-list := cdr factorise1(p!-content,alg!-tower);
        % fac-list is a list of the factored parts of f (with multiplicities);
    >>;
    if !*tralg and recursedp and algfac!-level = 1 then <<
      prin2!* "we now return to the polynomial ";
      fac!-printsf f
    >>;
    sqfr!-list := sqfr!-decompose(f,x);
    if length sqfr!-list > 1 or cdar sqfr!-list > 1 then <<
      trivial!-factors := t;
      if !*tralg then
        printstr "the polynomial decomposes into the square-free parts:";
        for each part in sqfr!-list do
          fac!-printsf !*p2f mksp(prepf car part,cdr part)
      >>
    else if !*tralg then printstr "the polynomial is square-free";
    for each part in sqfr!-list do <<
      facl := trivial!-factor(car part);
      if length facl > 1 then <<
        trivial!-factors := t;
        if !*tralg then <<
          prin2!* "we find ";
          fac!-printsf car part;
          printstr "factorises trivially as:";
          for each fac in facl do
            fac!-printsf fac
        >>
      >>;
      for each fac in facl do
        unfac!-list := (fac . cdr part) . unfac!-list
          % unfac-list is a list of the unfactored parts of f
          % (with multiplicities);
    >>;
    for each part in unfac!-list do <<
      if !*tralg and trivial!-factors then <<
        prin2!* "We consider the factor ";
        fac!-printsf car part
      >>;
      for each fac in alg!-factor(car part,x,alg!-tower) do
        fac!-list := (fac . cdr part) . fac!-list
    >>;
    return fac!-list
  end;

symbolic procedure polynomial!-content(f,x);
  % returns the polynomial content of f wrt x;
  begin scalar p!-content,!*ezgcd;
    for i:=0:degree!-in!-form(f,x) do
      p!-content := gcdf!*(p!-content,extract(f,x,i));
    return p!-content
  end;

symbolic procedure factor!-by!-xes(f,x,alg!-tower);
  % x = 0 is a n-fold root of f;
  % remove x**n and factor the remaining part;
  % returns ( (sf . n) (sf . n) ... )
  begin scalar xes;
    xes := 0;
    repeat
      xes := xes + 1
    until not null extract(f,x,xes);
    f := quotf(f,!*p2f(x .** xes));
    if !*tralg then <<
      prin2!* "divide out by ";
      prin2!* x;
      if xes > 1 then << prin2!* "**";prin2!* xes >>;
      terpri!* t       >>;
    return
      if variatep f then <<
        (!*k2f x . xes) . cdr factorise1(f,alg!-tower)
      >>
      else list(!*k2f x . xes)
  end;

symbolic smacro procedure cyclotomicp f;
  % f a standard form;
  testx!*!*n!+1 f or testx!*!*n!-1 f;

symbolic procedure trivial!-factor(f);
  % f a (square-free) standard form. We see if f factorises trivially
  % over the integers in some special cases;
  if not contains!-algebraic f or cyclotomicp f then
    for each fac!.n in cdr factorf(f) collect
      car fac!.n
  else list f;

symbolic procedure contains!-algebraic f;
  % does the sf f contain any algebraics?
  if groundp f then nil
  else if domainp f then
    car mvar numr cdr f = 'alg
  else contains!-algebraic(lc f) or contains!-algebraic(red f);

lisp unfluid '(algfac!-level recursedp trivial!-factors);

end;

%%%%%%%%%%%%%%%%%%

% module ALGCREATE

% Routines to create algebraics, and to show them;
%
% An algebraic kernel has the form alpha = (alg . algn) where algn is a
% gensym pointing to the minimal polynomial of alpha.
%

symbolic procedure make!-algebraic!-form(f,x);
  % f standard form in x, degree in x >= 2;
  % returns algebraic form of x, a standard quotient;
  begin scalar canf,alg!-name,alg!-form,degf,lcf,shift,scale;
    degf := degree!-in!-form(f,x);
    alg!-name := gensym1 'alg;          % viz algn;
    alg!-form := 'alg . alg!-name;      % algebraic kernel;
    canf := canon(f,x);
    if degf = 2 or cons!-count car canf < cons!-count f then <<
      shift := cadr canf;    % the shifted and scaled version of f is
      scale := cddr canf;    % better then the original;
      f := car canf
    >>
    else <<
      shift := nil;
      scale := 1;
      lcf := extract(f,x,degf);
      if lcf neq 1 then <<
        f := multf(f,exptf(lcf,degf-1));
        f := numr newsubf(f,quotsq(!*k2q x,!*f2q lcf));
        scale := lcf;
               % x := x/lcf to make f monic;
      >>
    >>;
% at this point f is monic and shifted when this is advantageous; shift
% and scale are set appropriately;
    set(alg!-name,substitute(f,alg!-form));
                          % replace x's by algs, and strip :alg:'s;
    alg!-kord!* := alg!-form . alg!-kord!*;
                          % record for ordering purposes;
    return quotsq(!*f2q differencef(mkalg1 !*k2q alg!-form,shift),
                  !*f2q scale);
  end;

symbolic procedure canon(f,x);
  % canonicise f wrt x, ie shift to remove x**(n-1) term, then monicise.
  % return the new f dotted on to a pair (shift . scale factor),
  % then oldx = (newx - shift)/scale;
  begin scalar degf,lcf,shift,scale;
    degf := ldeg f;               % may be a polynomial in algebraics;
    shift := extract(f,x,degf-1); % ditto
    lcf := lc f;
    if lcf = 1 and null shift then return f . (nil . 1);
    if not null shift then <<
      scale := multf(lcf,degf);
      f := numr newsubf(f,differencesq(!*k2q x,
                                       quotsq(!*f2q shift,
                                              !*f2q scale)));
           % x := x - a[n-1]/(lcf*degf);
      f := multf(f,multf(exptf(lcf,degf-1),degf**degf));  % clear denominator;
      f := numr newsubf(f,quotsq(!*k2q x,!*f2q scale));
         % x := x/(lcf*degf);
    >>
    else <<
      scale := lcf;
      if scale neq 1 then <<
        f := multf(f,exptf(lcf,degf-1));
        f := numr newsubf(f,quotsq(!*k2q x,!*f2q scale));
             % x := x/lcf;
      >>
    >>;
    return f . (shift . scale)
  end;

symbolic procedure simpalgof u;
  begin scalar f,x;
    f:= !*a2f car u;
    x:= mvar f;
    if multivariatep(f,x) then rederr "not univariate";
    if not !*algebraics then rederr "algebraics not selected";
    if degree!-in!-form(f,x) < 2 then
      rederr " degree is < 2"
    else return make!-algebraic!-form(f,x)
  end;

lisp put ('algof,'simpfn,'simpalgof);

symbolic procedure simppolyof u;
  % u is (alg . kernel).
  % return the minimal polynomial of alg using the kernel as
  % the variable (or x if absent);
  begin scalar alg,x,minpoly;
    alg := !*a2f car u;
    x := if null cdr u then 'x else cadr u;
    if not domainp alg or car alg neq '!:alg!: then
      typerr(alg,'algebraic);
    minpoly :=  min!-poly!-of mvar cadr alg;
    return mkalg(subst(x,mvar minpoly,minpoly) ./ 1) ./ 1
  end;

lisp put('polyof,'simpfn,'simppolyof);

symbolic procedure simpshow();
  % lists the current algebraics;
  if null alg!-kord!* then
    printstr "no algebraics created"
  else <<
    for each alg in alg!-kord!* do
      fac!-printsf min!-poly!-of alg;
    length alg!-kord!* ./ 1
  >>;

lisp put('showalgs,'simpfn,'simpshow);

lisp put('alg,'simpfn,'simpalg);

lisp flag('(alg),'full);
            % to get the entire kernel with alg passed to simpalg;

symbolic procedure simpalg(u);
  u .** 1 .* 1 .+ nil ./ 1;

end;

%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGMACROS

% Definition of macros used in algebraic number code;

symbolic smacro procedure groundp u;
  atom u;

symbolic smacro procedure algebraicp u;
  % u a kernel;
  domainp u and eqcar(u,'alg);

symbolic smacro procedure min!-poly!-of u;
  % u a kernel;
  gts cdr u;

symbolic smacro procedure degree!-of!-algebraic u;
  % u a kernel;
  ldeg min!-poly!-of u;

symbolic smacro procedure algebraic!-mvarp u;
  % u a sf;
  not groundp u and algebraicp mvar u;

end;

%%%%%%%%%%%%%%%%%%%%%%%

% module ALGNORM

symbolic procedure contains!-alpha(f,a);
  % does the sf f contain the algebraic a ?
  if groundp f then nil
  else if domainp f then
    (mvar numr cdr f = a) or contains!-alpha(numr cdr f,a)
  else contains!-alpha(lc f,a) or contains!-alpha(red f,a);

symbolic procedure normf(f,alpha);
  % find norm of f over alpha;
  my!-resultantf(min!-poly!-of alpha,f,alpha);

symbolic procedure normf1(f,alg!-tower);
  % produces the norm of the standard form f over
  % Q(alg!-tower);
  if null alg!-tower then f
  else
    (lambda alg;
      if contains!-alpha(f,alg) then
        normf1( my!-resultantf(min!-poly!-of alg,f,alg),cdr alg!-tower )
      else
        normf1( exptf(f,degree!-of!-algebraic alg),cdr alg!-tower )
    )(car alg!-tower);

symbolic procedure simpnorm u;
  % u is (f . list of algebraics).
  % Return the norm of f over the algebraics;
  if null u then nil ./ 1
  else if null cdr u then !*a2f car u ./ 1       % Norm over Z;
  else begin scalar f,algs;
    f := !*a2f car u;                            % collect algebraics;
    algs :=
      for each alg in cdr u collect
        (lambda a;
           if car a neq '!:alg!: then typerr(alg,'algebraic)
           else mvar cadr a)
        (!*a2f alg);
    return(normf1(f,algs) ./ 1)
  end;

put('norm,'simpfn,'simpnorm);

symbolic procedure my!-resultantf(f,g,alpha);
  % f is an untagged algebraic mimimum polynomial,
  % g some polynomial, alpha the algebraic to be 'normed' over;
  % resultant where we substitute ':x: for alpha in f and g;
  % we do this do overcome the problems of algebraics being hidden
  % behind :alg:'s.
  subresultant(resubstf('!:x!:, alpha, f),
               resimpf subst('!:x!:, alpha, g), '!:x!:);

symbolic procedure resubstf(x,alpha,f);
  % substitute x for alpha in the untagged mimimum polynomial f;
  if domainp f or mvar f neq alpha then
    mkalg !*f2q f
  else x .** ldeg f.* mkalg !*f2q lc f .+ resubstf(x,alpha,red f);

symbolic procedure resimpf f;
  % resimps the standard form f into a standard form;
  if domainp f then
    if eqcar(f,'!:alg!:) then mkalg cdr f
    else f
  else addf(resimpf red f,multf(resimpf lc f,!*p2f lpow f));

symbolic procedure resprem(f, g);
  % f, g are SFs.
  % Result is prem(f, g).
  if (domainp f) or (mvar f neq mvar g) or (ldeg f < ldeg g) then f
  else if ldeg f = ldeg g then
    addf(multf(lc g, red f), multf(negf lc f, red g))
  else begin scalar term, tmp, degtmp;
    term := mvar f .** (ldeg f - ldeg g) .* negf  lc f .+ nil;
    tmp := addf(multf(lc g, red f), multf(term, red g));
    degtmp := if domainp tmp or mvar tmp neq mvar f then 0 else ldeg tmp;
    if ldeg f neq 1 + degtmp then
      tmp := multf(exptf(lc g, ldeg f - degtmp - 1), tmp);
    return resprem(tmp, g)
  end;

symbolic procedure subresultant(u, v, x);
  % u, v are SFs : result is resultant of f and g with respect to x.
  % Method used is subresultant PRS.
  begin scalar g, h, d, r, oldkord;
    if mvar u neq x or mvar v neq x then <<
       oldkord := list setkorder list x;
       u := reorder u;
       v := reorder v;
       if mvar u neq x or mvar v neq x then
         REDERR "Subresultant: args must involve eliminating variable"
    >>;
    if ldeg u < ldeg v then <<
      g := u;
      u := v;
      v := g
    >>;
    g := h := 1;
    repeat <<
      d := ldeg u - ldeg v;
      r := resprem(u, v);
      u := v;
      v := quotfail(r, multf(g, exptf(h, d)));
      g := lc u;
      if onep d then h := g
      else if d neq 0 then
        h := quotfail(exptf(g, d),exptf(h, d-1));
    >> until domainp v or mvar u neq mvar v;
    if not null oldkord and not null car oldkord then <<
      setkorder car oldkord;
      v := reorder v
    >>;
    return v
  end;

end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% module ALGRECIP
%
% Calculation of the reciprocals of algebraics;

symbolic procedure algprem(f, g, cf, cg);
  % f, g, numr cf, numr cg are SFs over Z in only algebraic kernels,
  % denr cf & denr cg are positive integers.
  % Result is prem(f, g) . h where h=same lin comb of cf &
  % cg as prem is of f & g.
  if (domainp f) or (mvar f neq mvar g) or (ldeg f < ldeg g) then
    f . cf
  else if ldeg f = ldeg g then
    addf(algmultf(lc g, red f), algmultf(negf lc f, red g)) .
    addsq(quotsq(algmultf(lc g, numr cf) ./ 1, denr cf ./ 1),
          quotsq(algmultf(negf lc f, numr cg) ./ 1, denr cg ./ 1))
  else begin scalar term, tmp, newcofac, extra!-factor, degtmp;
    term := mvar f .** (ldeg f - ldeg g) .* negf  lc f .+ nil;
    tmp := addf(algmultf(lc g, red f), algmultf(term, red g));
    degtmp := if numberp tmp or mvar tmp neq mvar f then 0 else ldeg tmp;
    newcofac := addsq(quotsq(algmultf(lc g, numr cf) ./ 1, denr cf ./ 1),
                      quotsq(algmultf(term, numr cg) ./ 1, denr cg ./ 1));
    if ldeg f neq 1 + degtmp then <<
      extra!-factor := algexptf(lc g, ldeg f - degtmp - 1);
      tmp := algmultf(extra!-factor, tmp);
      newcofac := quotsq(algmultf(extra!-factor, numr newcofac) ./ 1,
                         denr newcofac ./ 1)
    >>;
    return algprem(tmp, g, newcofac, cg)
  end;

symbolic procedure algexptf(f, n);
  % f is a SF over Z in only alg kernels, n is a non-negative integer.
  % Result is SF over Z in only alg kernels for f**n.
  if zerop n then 1
  else if onep n then f
  else if evenp n then algexptf(algmultf(f, f), n/2)
  else algmultf(f, algexptf(algmultf(f, f), n/2));

symbolic procedure algrecip(f);
  % f is a SF involving an algebraic [unchecked, and ASSUMEd to be the mvar]
  % result is SQ for 1/f with all algebraics in the numerator.
  % Method used is subresultant PRS for finding gcd and the cofactor[=answer]
  if null f then
    rederr "attempt to take reciprocal of 0"
  else if numberp f then
    if minusp f then (-1) ./ (-f) else 1 ./ f
  else begin scalar dmode!*, cu, cv, u, v, g, h, d, tmp, invg, invh,
                    invg!*h!*!*d, r, oldh;
    dmode!* := nil; % change domain to integers - restored on exit
    g := h := 1;
    invg := invh := 1 ./ 1;
    u := min!-poly!-of mvar f;
    cu := nil ./ 1;
    v := f;
    cv := 1 ./ 1;
    repeat <<
      d := ldeg u - ldeg v;
      tmp := algprem(u, v, cu, cv);
      r := car tmp;
      if null r then
        rederr "unexpected factor of a minimal polynomial";
      if not(numberp r or mvar r neq mvar v) then <<
        u := v;
        cu := cv;
        invg!*h!*!*d := quotsq(algmultf(numr invg,
                                        algexptf(numr invh, d)) ./ 1,
                               (denr invg * denr(invh) ** d) ./ 1);
        v := quotfail(algmultf(r, numr invg!*h!*!*d), denr invg!*h!*!*d);
        cv := quotsq(algmultf(numr cdr tmp, numr invg!*h!*!*d) ./ 1,
                     denr cdr tmp * denr invg!*h!*!*d ./ 1);
        g := lc u;
        invg := algrecip g;
        if onep d then <<
          h := g;
          invh := invg
        >>
        else <<
          oldh := h;
          h := quotfail(algmultf(algexptf(g, d),
                                 algexptf(numr invh, d-1)),
                        denr(invh) ** (d-1));
          invh := quotsq(algmultf(algexptf(oldh, d-1),
                                  algexptf(numr invg, d)) ./ 1,
                         denr(invg) ** d ./ 1)
        >>
      >>
    >> until numberp r or mvar r neq mvar v;
    r := algrecip r;
    return quotsq(algmultf(numr cdr tmp, numr r) ./ 1,
                  (denr r * denr cdr tmp) ./ 1)
  end;

end;

