module mhensfns;

% Authors: A. C. Norman and P. M. A. Moore, 1979.

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


fluid '(!*trfac
        alphalist
        current!-modulus
        degree!-bounds
        delfvec
        factor!-level
        factor!-trace!-list
        forbidden!-primes
        hensel!-growth!-size
        image!-factors
        max!-unknowns
        multivariate!-input!-poly
        non!-monic
        number!-of!-factors
        number!-of!-unknowns
        polyzero
%       pt
        prime!-base);


%**********************************************************************;
%    This section contains some of the functions used in
%    the multivariate hensel growth. (ie they are called from
%    section MULTIHEN or function RECONSTRUCT-MULTIVARIATE-FACTORS). ;



symbolic procedure set!-degree!-bounds(v, p1, p2);
  degree!-bounds:=for each var in v collect
    (car var . max(degree!-in!-variable(p1, car var),
           degree!-in!-variable(p2, car var)));

symbolic procedure get!-degree!-bound v;
  begin scalar w;
    w:=atsoc(v,degree!-bounds);
    if null w then errorf(list("Degree bound not found for ",
        v," in ",degree!-bounds));
    return cdr w
  end;

symbolic procedure choose!-larger!-prime n;
% our prime base in the multivariate hensel must be greater than n so
% this sets a new prime to be that (previous one was found to be no
% good). We also set up various fluids e.g. the Alphas;
% the primes we can choose are < 2**24 so if n is bigger
% we collapse;
  if n > 2**24-1 then
    errorf list("CANNOT CHOOSE PRIME > GIVEN NUMBER:",n)
  else begin scalar p,flist!-mod!-p,k,fvec!-mod!-p,forbidden!-primes;
trynewprime:
    if p then forbidden!-primes:=p . forbidden!-primes;
    p:=random!-prime();
            % this chooses a word-size prime (currently 24 bits);
    set!-modulus p;
    if not(p>n) or member(p,forbidden!-primes) or
      polyzerop reduce!-mod!-p lc multivariate!-input!-poly then
       goto trynewprime;
    for i:=1:number!-of!-factors do
      flist!-mod!-p:=(reduce!-mod!-p getv(image!-factors,i) .
                       flist!-mod!-p);
    alphalist:=alphas(number!-of!-factors,flist!-mod!-p,1);
    if alphalist='factors! not! coprime then goto trynewprime;
    hensel!-growth!-size:=p;
    prime!-base:=p;
    factor!-trace <<
      prin2!* "New prime chosen: ";
      printstr hensel!-growth!-size >>;
    k:=number!-of!-factors;
    fvec!-mod!-p:=mkvect k;
    for each w in flist!-mod!-p do <<
      putv(fvec!-mod!-p,k,w); k:=isub1 k >>;
    return fvec!-mod!-p
  end;

symbolic procedure binomial!-coefft!-mod!-p(n,r);
  if n<r then nil
  else if n=r then 1
  else if r=1 then !*n2f modular!-number n
  else begin scalar n!-c!-r,b,j;
    n!-c!-r:=1;
    b:=min(r,n-r);
    n:=modular!-number n;
    r:=modular!-number r;
    for i:=1:b do <<
      j:=modular!-number i;
      n!-c!-r:=modular!-quotient(
        modular!-times(n!-c!-r,
          modular!-difference(n,modular!-difference(j,1))),
        j) >>;
    return !*n2f n!-c!-r
  end;

symbolic procedure make!-multivariate!-hatvec!-mod!-p(bvec,n);
% makes a vector whose ith elt is product over j [ BVEC(j) ] / BVEC(i);
% NB. we must NOT actually do the division here as we are likely
% to be working mod p**n (some n > 1) and the division can involve
% a division by p.;
  begin scalar bhatvec,r;
    bhatvec:=mkvect n;
    for i:=1:n do <<
      r:=1;
      for j:=1:n do if not(j=i) then r:=times!-mod!-p(r,getv(bvec,j));
      putv(bhatvec,i,r) >>;
    return bhatvec
  end;

symbolic procedure max!-degree!-in!-var(fvec,v);
  begin scalar r,d;
    r:=0;
    for i:=1:number!-of!-factors do
      if r<(d:=degree!-in!-variable(getv(fvec,i),v)) then r:=d;
    return r
  end;

symbolic procedure make!-growth!-factor pt;
% pt is of form (v . n) where v is a variable. we make the s.f. v-n;
  if cdr pt=0 then !*f2mod !*k2f car pt
  else plus!-mod!-p(!*f2mod !*k2f car pt,modular!-minus cdr pt);

symbolic procedure terms!-done!-mod!-p(fvec,delfvec,delfactor);
% calculate the terms introduced by the corrections in DELFVEC;
  begin scalar flist,delflist;
    for i:=1:number!-of!-factors do <<
      flist:=getv(fvec,i) . flist;
      delflist:=getv(delfvec,i) . delflist >>;
    return terms!-done1!-mod!-p(number!-of!-factors,flist,delflist,
      number!-of!-factors,delfactor)
  end;

symbolic procedure terms!-done1!-mod!-p(n,flist,delflist,r,m);
  if n=1 then (car flist) . (car delflist)
  else begin scalar k,i,f1,f2,delf1,delf2;
    k:=n/2; i:=1;
    for each f in flist do
    << if i>k then f2:=(f . f2)
       else f1:=(f . f1);
       i:=i+1 >>;
    i:=1;
    for each delf in delflist do
    << if i>k then delf2:=(delf . delf2)
       else delf1:=(delf . delf1);
       i:=i+1 >>;
    f1:=terms!-done1!-mod!-p(k,f1,delf1,r,m);
    delf1:=cdr f1; f1:=car f1;
    f2:=terms!-done1!-mod!-p(n-k,f2,delf2,r,m);
    delf2:=cdr f2; f2:=car f2;
    delf1:=
      plus!-mod!-p(plus!-mod!-p(
        times!-mod!-p(f1,delf2),
        times!-mod!-p(f2,delf1)),
        times!-mod!-p(times!-mod!-p(delf1,m),delf2));
    if n=r then return delf1;
    return (times!-mod!-p(f1,f2) . delf1)
  end;

symbolic procedure primitive!.parts(flist,var,univariate!-inputs);
% finds the prim.part of each factor in flist wrt variable var;
% Note that FLIST may contain univariate or multivariate S.F.s
% (according to UNIVARIATE!-INPUTS) - in the former case we correct the
% ALPHALIST if necessary;
  begin scalar c,primf;
    if null var then
      errorf "Must take primitive parts wrt some non-null variable";
    if non!-monic then
      factor!-trace <<
        printstr "Because we multiplied the original primitive";
        printstr "polynomial by a multiple of its leading coefficient";
        printstr "(see (a) above), the factors we have now are not";
        printstr "necessarily primitive. However the required factors";
        printstr "are merely their primitive parts." >>;
    return for each fw in flist collect
    << if not depends!-on!-var(fw,var) then
            errorf list("WRONG VARIABLE",var,fw);
       c:=comfac fw;
       if car c then errorf(list(
         "FACTOR DIVISIBLE BY MAIN VARIABLE:",fw,car c));
       primf:=quotfail(fw,cdr c);
       if not(cdr c=1) and univariate!-inputs then
         multiply!-alphas(cdr c,fw,primf);
       primf >>
  end;


symbolic procedure make!-predicted!-forms(pfs,v);
% PFS is a vector of S.F.s which represents the sparsity of
% the associated polynomials wrt V. Here PFS is adjusted to a
% suitable form for handling this sparsity. ie. we record the
% degrees of V in a vector for each poly in PFS. Each
% monomial (in V) represents an unknown (its coefft) in the predicted
% form of the associated poly. We count the maximum no of unknowns for
% each poly and return the maximum of these;
  begin scalar l,n,pvec,j,w;
    max!-unknowns:=0;
    for i:=1:number!-of!-factors do <<
      w:=getv(pfs,i);  % get the ith poly;
      l:=sort(spreadvar(w,v,nil),function lessp);
            % Pick out the monomials in V from this poly and order
            % them in increasing degree;
      n:=iadd1 length l; % no of unknowns in predicted poly - we add
                         % one for the constant term;
      number!-of!-unknowns:=(n . i) . number!-of!-unknowns;
      if max!-unknowns<n then max!-unknowns:=n;
      pvec:=mkvect isub1 n;
            % get space for the info on this poly;
      j:=0;
      putv(pvec,j,isub1 n);
            % put in the length of this vector which will vary
            % from poly to poly;
      for each m in l do putv(pvec,j:=iadd1 j,m);
            % put in the monomial info;
      putv(pfs,i,pvec);
            % overwrite the S.F. in PFS with the more compact vector;
      >>;
    number!-of!-unknowns:=sort(number!-of!-unknowns,function lesspcar);
    return max!-unknowns
  end;

symbolic procedure make!-correction!-vectors(bfs,n);
% set up space for the vector of vectors to hold the correction
% terms as we generate them by the function SOLVE-FOR-CORRECTIONS.
% Also put in the starting values;
  begin scalar cvs,cv;
    cvs:=mkvect number!-of!-factors;
    for i:=1:number!-of!-factors do <<
      cv:=mkvect n;
            % each CV will hold the corrections for the ith factor;
            % the no of corrections we put in here depends on the
            % maximum no of unknowns we have in the predicted
            % forms, giving a set of soluble linear systems (hopefully);
      putv(cv,1,getv(bfs,i));
            % put in the first 'corrections';
      putv(cvs,i,cv) >>;
    return cvs
  end;

symbolic procedure construct!-soln!-matrices(pfs,val);
% Here we construct the matrices - one for each linear system
% we will have to solve to see if our predicted forms of the
% answer are correct. Each matrix is a vector of row-vectors
% - the ijth elt is in jth slot of ith row-vector (ie zero slots
% are not used here);
  begin scalar soln!-matrix,resvec,n,pv;
    resvec:=mkvect number!-of!-factors;
    for i:=1:number!-of!-factors do <<
      pv:=getv(pfs,i);
      soln!-matrix:=mkvect(n:=iadd1 getv(pv,0));
      construct!-ith!-matrix(soln!-matrix,pv,n,val);
      putv(resvec,i,soln!-matrix) >>;
    return resvec
  end;

symbolic procedure construct!-ith!-matrix(sm,pv,n,val);
  begin scalar mv;
    mv:=mkvect n;  %  this will be the first row;
    putv(mv,1,1);  % the first column represents the constant term;
    for j:=2:n do putv(mv,j,modular!-expt(val,getv(pv,isub1 j)));
            % first row is straight substitution;
    putv(sm,1,mv);
            % now for the rest of the rows:   ;
    for j:=2:n do <<
      mv:=mkvect n;
      putv(mv,1,0);
      construct!-matrix!-row(mv,isub1 j,pv,n,val);
      putv(sm,j,mv) >>
  end;

symbolic procedure construct!-matrix!-row(mrow,j,pv,n,val);
  begin scalar d;
    for k:=2:n do <<
      d:=getv(pv,isub1 k);  % degree representing the monomial;
      if d<j then putv(mrow,k,0)
      else <<
        d:=modular!-times(!*d2n binomial!-coefft!-mod!-p(d,j),
             modular!-expt(val,idifference(d,j)));
            % differentiate and substitute all at once;
        putv(mrow,k,d) >> >>
  end;

symbolic procedure print!-linear!-systems(soln!-m,correction!-v,
                                              predicted!-f,v);
<<
  for i:=1:number!-of!-factors do
    print!-linear!-system(i,soln!-m,correction!-v,predicted!-f,v);
  terpri!*(nil) >>;

symbolic procedure print!-linear!-system(i,soln!-m,correction!-v,
                                              predicted!-f,v);
  begin scalar pv,sm,cv,mr,n,tt;
    terpri!*(t);
    prin2!* " i = "; printstr i;
    terpri!*(nil);
    sm:=getv(soln!-m,i);
    cv:=getv(correction!-v,i);
      pv:=getv(predicted!-f,i);
      n:=iadd1 getv(pv,0);
      for j:=1:n do << % for each row in matrix ... ;
        prin2!* "(  ";
        tt:=2;
        mr:=getv(sm,j);  % matrix row;
      for k:=1:n do << % for each elt in row ... ;
          prin2!* getv(mr,k);
          ttab!* (tt:=tt+10) >>;
        prin2!* ")  ( [";
        if j=1 then prin2!* 1
        else prinsf adjoin!-term(mksp(v,getv(pv,isub1 j)),1,polyzero);
      prin2!* "]";
      ttab!* (tt:=tt+10);
      prin2!* " )";
      if j=(n/2) then prin2!* "  =  (  " else prin2!* "     (  ";
      prinsf getv(cv,j);
      ttab!* (tt:=tt+30); printstr ")";
      if not(j=n) then <<
        tt:=2;
        prin2!* "(";
        ttab!* (tt:=tt+n*10);
        prin2!* ")  (";
        ttab!* (tt:=tt+10);
        prin2!* " )     (";
        ttab!* (tt:=tt+30);
        printstr ")" >> >>;
    terpri!*(t)
  end;

symbolic procedure try!-prediction(sm,cv,pv,n,i,poly,v,ff,ffhat);
  begin scalar w,ffi,fhati;
    sm:=getv(sm,i);
    cv:=getv(cv,i);
    pv:=getv(pv,i);
    if not(n=iadd1 getv(pv,0)) then
      errorf list("Predicted unknowns gone wrong? ",n,iadd1 getv(pv,0));
    if null getm2(sm,1,0) then <<
      w:=lu!-factorize!-mod!-p(sm,n);
      if w='singular then <<
        factor!-trace <<
          prin2!* "Prediction for ";
          prin2!* if null ff then 'f else 'a;
          prin2!* "("; prin2!* i;
          printstr ") failed due to singular matrix." >>;
        return (w . i) >> >>;
    back!-substitute(sm,cv,n);
    w:=
      if null ff then try!-factor(poly,cv,pv,n,v)
      else <<
        ffi := getv(ff,i);
        fhati := getv(ffhat,i); % The unfolding here is to get round
                                % a bug in the PSL compiler 12/9/82. It
                                % will be tidied back up as soon as
                                % possible;
        try!-alpha(poly,cv,pv,n,v,ffi,fhati) >>;
    if w='bad!-prediction then <<
      factor!-trace <<
        prin2!* "Prediction for ";
        prin2!* if null ff then 'f else 'a;
        prin2!* "("; prin2!* i;
        printstr ") was an inadequate guess." >>;
      return (w . i) >>;
    factor!-trace <<
      prin2!* "Prediction for ";
      prin2!* if null ff then 'f else 'a;
      prin2!* "("; prin2!* i; prin2!* ") worked: ";
      printsf car w >>;
    return (i . w)
  end;

symbolic procedure try!-factor(poly,testv,predictedf,n,v);
  begin scalar r,w;
    r:=getv(testv,1);
    for j:=2:n do <<
      w:=!*f2mod adjoin!-term(mksp(v,getv(predictedf,isub1 j)),1,
                              polyzero);
      r:=plus!-mod!-p(r,times!-mod!-p(w,getv(testv,j))) >>;
    w:=quotient!-mod!-p(poly,r);
    if didntgo w or
      not polyzerop difference!-mod!-p(poly,times!-mod!-p(w,r)) then
      return 'bad!-prediction
    else return list(r,w)
  end;

symbolic procedure try!-alpha(poly,testv,predictedf,n,v,fi,fhati);
  begin scalar r,w,wr;
    r:=getv(testv,1);
    for j:=2:n do <<
      w:=!*f2mod adjoin!-term(mksp(v,getv(predictedf,isub1 j)),1,
                              polyzero);
      r:=plus!-mod!-p(r,times!-mod!-p(w,getv(testv,j))) >>;
    if polyzerop
      (wr:=difference!-mod!-p(poly,times!-mod!-p(r,fhati))) then
      return list (r,wr);
    w:=quotient!-mod!-p(wr,fi);
    if didntgo w or
      not polyzerop difference!-mod!-p(wr,times!-mod!-p(w,fi)) then
      return 'bad!-prediction
    else return list(r,wr)
  end;



endmodule;


end;
