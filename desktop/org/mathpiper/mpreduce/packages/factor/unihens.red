module unihens;  % Univariate case of Hensel code with quadratic growth.

% Author: P. M. A. Moore, 1979.
% Modifications by J.H. Davenport 1988 following a 1985 draft by Abbott,
% Bradford and Davenport.

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


fluid '(!*linear
        !*overshoot
        !*overview
        !*trfac
        alphalist
        alphavec
        coefftbd
        current!-factor!-product
        current!-modulus
        delfvec
        deltam
        factor!-level
        factor!-trace!-list
        factors!-done
        factorvec
        facvec
        fhatvec
        hensel!-growth!-size
        hensel!-poly
        irreducible
        m!-image!-variable
        modfvec
        multivariate!-input!-poly
        non!-monic
        number!-of!-factors
        polyzero
        prime!-base
        reconstructing!-gcd);

global '(largest!-small!-modulus);


symbolic procedure uhensel!.extend(poly,best!-flist,lclist,p);
% Extend poly=product(factors in best!-flist) mod p even if poly is
% non-monic.  Return a list (ok. list of factors) if factors can be
% extended to be correct over the integers, otherwise return a list
% (failed <reason> <reason>).
  begin scalar w,k,old!-modulus,alphavec,modular!-flist,factorvec,
        modfvec,coefftbd,fcount,fhatvec,deltam,mod!-symm!-flist,
        current!-factor!-product,facvec,factors!-done,hensel!-poly;
    prime!-base:=p;
    old!-modulus:=set!-modulus p;
%   timer:=readtime();
    number!-of!-factors:=length best!-flist;
    w:=expt(lc poly,number!-of!-factors -1);
    if lc poly < 0 then errorf list("LC SHOULD NOT BE -VE",poly);
    coefftbd:=max(110,p+1,lc poly*get!-coefft!-bound(poly,ldeg poly));
    poly:=multf(poly,w);
    modular!-flist:=for each ff in best!-flist collect
      reduce!-mod!-p ff;
            % Modular factors have been multiplied by a constant to
            % fix the l.c.'s, so they may be out of range - this
            % fixes that.
      if not(w=1) then factor!-trace <<
        prin2!* "Altered univariate polynomial: "; printsf poly >>;
          % Make sure the leading coefft will not cause trouble
          % in the hensel construction.
    mod!-symm!-flist:=for each ff in modular!-flist collect
      make!-modular!-symmetric ff;
    if not !*overview then factor!-trace <<
      prin2!* "The factors mod "; prin2!* p;
      printstr " to start from are:";
      fcount:=1;
      for each ff in mod!-symm!-flist do <<
        prin2!* "   f("; prin2!* fcount; prin2!* ")=";
        printsf ff; fcount:=iadd1 fcount >>;
      terpri!*(nil) >>;
    alphalist:=alphas(number!-of!-factors,modular!-flist,1);
            % 'magic' polynomials associated with the image factors.
    if not !*overview then factor!-trace <<
      printstr
         "The following modular polynomials are chosen such that:";
      terpri();
      prin2!* "   a(1)*h(1) + ... + a(";
      prin2!* number!-of!-factors;
      prin2!* ")*h("; prin2!* number!-of!-factors;
      prin2!* ") = 1 mod "; printstr p;
      terpri();
      printstr "  where h(i)=(product of all f(j) [see below])/f(i)";
      printstr "    and degree of a(i) < degree of f(i).";
      fcount:=1;
      for each a in modular!-flist do <<
        prin2!* "   a("; prin2!* fcount; prin2!* ")=";
        printsf cdr get!-alpha a;
        prin2!* "   f("; prin2!* fcount; prin2!* ")=";
        printsf a;
        fcount:=iadd1 fcount >>
    >>;
    k:=0;
    factorvec:=mkvect number!-of!-factors;
    modfvec:=mkvect number!-of!-factors;
    alphavec:=mkvect number!-of!-factors;
    for each modsymmf in mod!-symm!-flist do
      << putv(factorvec,k:=k+1,force!-lc(modsymmf,car lclist));
         lclist:=cdr lclist
      >>;
    k:=0;
    for each modfactor in modular!-flist do
         << putv(modfvec,k:=k+1,modfactor);
         putv(alphavec,k,cdr get!-alpha modfactor);
         >>;
            % best!-fvec is now a vector of factors of poly correct
            % mod p with true l.c.s forced in.
    fhatvec:=mkvect number!-of!-factors;
    w:=hensel!-mod!-p(poly,modfvec,factorvec,coefftbd,nil,p);
    if car w='overshot then w := uhensel!.extend1(poly,w)
     else w := uhensel!.extend2 w;
   set!-modulus old!-modulus;
    if irreducible then <<
      factor!-trace
         printstr "Two factors and overshooting means irreducible";
      return t >>;
    factor!-trace begin scalar k;
      k:=0;
      printstr "Univariate factors, possibly with adjusted leading";
      printstr "coefficients, are:";
      for each ww in cdr w do <<
        prin2!* " f("; prin2!* (k:=k #+ 1);
        prin2!* ")="; printsf ww >>
    end;
    return if non!-monic then
        (car w . primitive!.parts(cdr w,m!-image!-variable,t))
      else w
  end;

symbolic procedure uhensel!.extend1(poly,w);
      begin scalar oklist,badlist,m,r,ff,om,pol;
        m:=cadr w; % the modulus.
        r:=getv(factorvec,0); % the number of factors.
        if r=2 then return (irreducible:=t);
        if factors!-done then <<
          poly:=hensel!-poly;
          for each ww in factors!-done do
            poly:=multf(poly,ww) >>;
        pol:=poly;
        om:=set!-modulus hensel!-growth!-size;
        alphalist:=nil;
        for i:=r step -1 until 1 do
          alphalist:=
             (reduce!-mod!-p getv(factorvec,i) . getv(alphavec,i))
                      . alphalist;
        set!-modulus om;
            % bring alphalist up to date.
        for i:=1:r do <<
          ff:=getv(factorvec,i);
          if not didntgo(w:=quotf(pol,ff)) then
          << oklist:=ff . oklist; pol:=w>>
          else badlist:=(i . ff) . badlist >>;
        if null badlist then w:='ok . oklist
        else <<
          if not !*overview then factor!-trace <<
            printstr "Overshot factors are:";
            for each f in badlist do <<
              prin2!* " f("; prin2!* car f; prin2!* ")=";
              printsf cdr f >>
          >>;
          w:=try!.combining(badlist,pol,m,nil);
          if car w='one! bad! factor then begin scalar x;
            w:=append(oklist,cdr w);
            x:=1;
            for each v in w do x:=multf(x,v);
            w:='ok . (quotfail(pol,x) . w)
          end
          else w:='ok . append(oklist,w) >>;
        if (not !*linear) and multivariate!-input!-poly then <<
          poly:=1;
          number!-of!-factors:=0;
          for each facc in cdr w do <<
            poly:=multf(poly,facc);
            number!-of!-factors:=1 #+ number!-of!-factors >>;
            % make sure poly is the product of the factors we have,
            % we recalculate it this way because we may have the wrong
            % lc in old value of poly.
          reset!-quadratic!-step!-fluids(poly,cdr w,
                                         number!-of!-factors);
          if m=deltam then errorf list("Coefft bound < prime ?",
              coefftbd,m);
          m:=deltam*deltam;
          while m<largest!-small!-modulus do <<
            quadratic!-step(m,number!-of!-factors);
            m:=m*deltam >>;
          hensel!-growth!-size:=deltam;
          om:=set!-modulus hensel!-growth!-size;
          alphalist:=nil;
          for i:=number!-of!-factors step -1 until 1 do
            alphalist:=
              (reduce!-mod!-p getv(factorvec,i) . getv(alphavec,i))
                      . alphalist;
          set!-modulus om >>;
         return w
      end;

symbolic procedure uhensel!.extend2 w;
   begin scalar r,faclist,om;
      r:=getv(factorvec,0); % no of factors.
      om:=set!-modulus hensel!-growth!-size;
      alphalist:=nil;
      for i:=r step -1 until 1 do
        alphalist:=(reduce!-mod!-p getv(factorvec,i) . getv(alphavec,i))
                    . alphalist;
      set!-modulus om;
            % bring alphalist up to date.
      for i:=r step -1 until 1 do
        faclist:=getv(factorvec,i) . faclist;
      return (car w . faclist)
    end;

symbolic procedure get!-coefft!-bound(poly,ddeg);
% This uses Mignotte's bound which is minimal I believe.
% NB. poly had better be univariate as bound only valid for this.
  binomial!-coefft(ddeg/2,ddeg/4) * root!-squares(poly,0);

symbolic procedure binomial!-coefft(n,r);
  if n<r then nil
  else if n=r then 1
  else if r=1 then n
  else begin scalar n!-c!-r,b;
    n!-c!-r:=1;
    b:=min(r,n-r);
    for i:=1:b do
      n!-c!-r:=(n!-c!-r * (n - i + 1)) / i;
    return n!-c!-r
  end;

symbolic procedure find!-alphas!-in!-a!-ring(n,mflist,fhatlist,gamma);
% Find the alphas (as below) given that the modulus may not be prime
% but is a prime power.
  begin scalar gg,m,ppow,i,gg!-mod!-p,modflist,wvec,alpha,alphazeros,w;
    if null prime!-base then errorf
      list("Prime base not set for finding alphas",
        current!-modulus,n,mflist);
    m:=set!-modulus prime!-base;
    modflist:= if m=prime!-base then mflist
      else for each fthing in mflist collect
        reduce!-mod!-p !*mod2f fthing;
    alphalist:=alphas(n,modflist,gamma);
    if m=prime!-base then <<
      set!-modulus m;
      return alphalist >>;
    i:=0;
    alphazeros:=mkvect n;
    wvec:=mkvect n;
    for each modfthing in modflist do <<
      putv(modfvec,i:=iadd1 i,modfthing);
      putv(alphavec,i,!*f2mod(alpha:=cdr get!-alpha modfthing));
      putv(alphazeros,i,alpha);
      putv(wvec,i,alpha);
      putv(fhatvec,i,car fhatlist);
      fhatlist:=cdr fhatlist >>;
    gg:=gamma;
    ppow:=prime!-base;
    while ppow<m do <<
      set!-modulus m;
      gg:=!*f2mod quotfail(!*mod2f difference!-mod!-p(gg,
          form!-sum!-and!-product!-mod!-m(wvec,fhatvec,n)),prime!-base);
      set!-modulus prime!-base;
      gg!-mod!-p:=reduce!-mod!-p !*mod2f gg;
      for k:=1:n do <<
        putv(wvec,k,w:=remainder!-mod!-p(
          times!-mod!-p(getv(alphazeros,k),gg!-mod!-p),
          getv(modfvec,k)));
        putv(alphavec,k,addf(getv(alphavec,k),multf(!*mod2f w,ppow)))>>;
      ppow:=ppow*prime!-base >>;
    set!-modulus m;
    i:=0;
    return (for each fthing in mflist collect
      (fthing . !*f2mod getv(alphavec,i:=iadd1 i)))
  end;

symbolic procedure alphas(n,flist,gamma);
% Finds alpha,beta,delta,... wrt factors f(i) in flist s.t.
%  alpha*g(1) + beta*g(2) + delta*g(3) + ... = gamma mod p,
% where g(i)=product(all the f(j) except f(i) itself).
% (cf. xgcd!-mod!-p below). n is number of factors in flist.
  if n=1 then list(car flist . gamma)
  else begin scalar k,w,f1,f2,i,gamma1,gamma2;
    k:=n/2;
    f1:=1; f2:=1;
    i:=1;
    for each f in flist do
    << if i>k then f2:=times!-mod!-p(f,f2)
       else f1:=times!-mod!-p(f,f1);
       i:=i+1 >>;
    w:=xgcd!-mod!-p(f1,f2,1,polyzero,polyzero,1);
    if atom w then
      return 'factors! not! coprime;
    gamma1:=remainder!-mod!-p(times!-mod!-p(cdr w,gamma),f1);
    gamma2:=remainder!-mod!-p(times!-mod!-p(car w,gamma),f2);
    i:=1; f1:=nil; f2:=nil;
    for each f in flist do
    << if i>k then f2:=f . f2
       else f1:=f . f1;
       i:=i+1 >>;
    return append(
      alphas(k,f1,gamma1),
      alphas(n-k,f2,gamma2))
  end;

symbolic procedure xgcd!-mod!-p(a,b,x1,y1,x2,y2);
% Finds alpha and beta s.t. alpha*a+beta*b=1.
% Returns alpha . beta or nil if a and b are not coprime.
    if null b then nil
    else if domainp b then begin
        b:=modular!-reciprocal b;
        x2:=multiply!-by!-constant!-mod!-p(x2,b);
        y2:=multiply!-by!-constant!-mod!-p(y2,b);
        return x2 . y2 end
    else begin scalar q;
        q:=quotient!-mod!-p(a,b); % Truncated quotient here.
        return xgcd!-mod!-p(b,difference!-mod!-p(a,times!-mod!-p(b,q)),
            x2,y2,
            difference!-mod!-p(x1,times!-mod!-p(x2,q)),
            difference!-mod!-p(y1,times!-mod!-p(y2,q)))
        end;

symbolic procedure hensel!-mod!-p(poly,mvec,fvec,cbd,vset,p);
% Hensel construction building up in powers of p.
% Given that poly=product(factors in factorvec) mod p, find the full
% factors over the integers.  Mvec contains the univariate factors mod p
% while fvec contains our best knowledge of the factors to date.
% Fvec includes leading coeffts (and in multivariate case possibly other
% coeffts) of the factors. return a list whose first element is a flag
% with one of the following values:
%  ok        construction worked, the cdr of the result is a list of
%            the correct factors.
%  failed    inputs must have been incorrect
%  overshot  factors are correct mod some power of p (say p**m),
%            but are not correct over the integers.
%            result is (overshot,p**m,list of factors so far).
  begin scalar w,u0,delfvec,old!.mod,res,m;
    u0:=initialize!-hensel(number!-of!-factors,p,poly,mvec,fvec,cbd);
            % u0 contains the product (over integers) of factors mod p.
    m := p;
    old!.mod := set!-modulus nil;
    if number!-of!-factors=1
      then <<putv(fvec,1,current!-factor!-product:=poly);
             % Added JHD 28.9.87
             return hensel!-exit(m,old!.mod,p,vset,w)>>;
            % only one factor to grow! but need to go this deep to
            % construct the alphas and set things up for the
            % multivariate growth which may follow.
    hensel!-msg1(p,u0);
    old!.mod:=set!-modulus p;
    res:=addf(hensel!-poly,negf u0);
            % calculate the residue. from now on this is always
            % kept in res.
    m:=p;
            % measure of how far we have built up factors - at this
            % stage we know the constant terms mod p in the factors.
   a: if polyzerop res then return hensel!-exit(m,old!.mod,p,vset,w);
      if (m/2)>coefftbd then
        <<
            % we started with a false split of the image so some
            % of the factors we have built up must amalgamate in
            % the complete factorization.
          if !*overshoot then <<
            prin2 if null vset then "Univariate " else "Multivariate ";
            prin2t "coefft bound overshoot" >>;
          if not !*overview then
        factor!-trace printstr "We have overshot the coefficient bound";
          return hensel!-exit(m,old!.mod,p,vset,'overshot)>>;
      res:=quotfail(res,deltam);
            % next term in residue.
      if not !*overview then factor!-trace <<
        prin2!* "Residue divided by "; prin2!* m; prin2!* " is ";
        printsf res >>;
      if (not !*linear) and null vset
        and m<=largest!-small!-modulus and m>p then
        quadratic!-step(m,number!-of!-factors);
      w:=reduce!-mod!-p res;
      if not !*overview then factor!-trace <<
          prin2!* "Next term in residue to kill is:";
          prinsf w; prin2!* " which is of size ";
          printsf (deltam*m);
          >>;
      solve!-for!-corrections(w,fhatvec,modfvec,delfvec,vset);
            % delfvec is vector of next correction terms to factors.
      make!-vec!-modular!-symmetric(delfvec,number!-of!-factors);
      if not !*overview then factor!-trace <<
        printstr "Correction terms are:";
        w:=1;
        for i:=1:number!-of!-factors do <<
          prin2!* "  To f("; prin2!* w; prin2!* "): ";
          printsf multf(m,getv(delfvec,i));
          w:=iadd1 w >>;
      >>;
      w:=terms!-done(factorvec,delfvec,m);
      res:=addf(res,negf w);
            % subtract out the terms generated by these corrections
            % from the residue.
      current!-factor!-product:=
         addf(current!-factor!-product,multf(m,w));
            % add in the correction terms to give new factor product.
      for i:=1:number!-of!-factors do
        putv(factorvec,i,
          addf(getv(factorvec,i),multf(getv(delfvec,i),m)));
            % add the corrections into the factors.
      if not !*overview then factor!-trace <<
        printstr "   giving new factors as:";
        w:=1;
        for i:=1:number!-of!-factors do <<
          prin2!* " f("; prin2!* w; prin2!* ")=";
          printsf getv(factorvec,i); w:=iadd1 w >>
        >>;
      m:=m*deltam;
      if not polyzerop res and null vset and
        not reconstructing!-gcd then
        begin scalar j,u,fac;
          j:=0;
          while (j:=j #+ 1)<=number!-of!-factors do
%            IF NULL GETV(DELFVEC,J) AND
            % - Try dividing out every time for now.
            if not didntgo
              (u:=quotf(hensel!-poly,fac:=getv(factorvec,j))) then <<
              hensel!-poly:=u;
              res:=adjust!-growth(fac,j,m);
              j:=number!-of!-factors >>
        end;
      go to a
  end;

symbolic procedure hensel!-exit(m,old!.mod,p,vset,w);
   begin
    if factors!-done then <<
      if not(w='overshot) then m:=p*p;
      set!-hensel!-fluids!-back p >>;
    if (not (w='overshot)) and null vset
      and (not !*linear) and multivariate!-input!-poly then
      while m<largest!-small!-modulus do <<
        if not(m=deltam) then quadratic!-step(m,number!-of!-factors);
        m:=m*deltam >>;
            % set up the alphas etc so that multivariate growth can
            % use a Hensel growth size of about word size.
    set!-modulus old!.mod;
            % reset the old modulus.
    hensel!-growth!-size:=deltam;
    putv(factorvec,0,number!-of!-factors);
    return
      if w='overshot then list('overshot,m,factorvec)
      else 'ok . factorvec
  end;

symbolic procedure hensel!-msg1(p,u0);
   begin scalar w;
    factor!-trace <<
      printstr
         "We are now ready to use the Hensel construction to grow";
      prin2!* "in powers of "; printstr current!-modulus;
      if not !*overview then <<prin2!* "Polynomial to factor (=U): ";
        printsf hensel!-poly>>;
      prin2!* "Initial factors mod "; prin2!* p;
      printstr " with some correct coefficients:";
      w:=1;
      for i:=1:number!-of!-factors do <<
        prin2!* " f("; prin2!* w; prin2!* ")=";
        printsf getv(factorvec,i); w:=iadd1 w >>;
      if not !*overview then << prin2!* "Coefficient bound = ";
        prin2!* coefftbd;
      terpri!*(nil);
      prin2!* "The product of factors over the integers is ";
      printsf u0;
      printstr "In each step below, the residue is U - (product of the";
      printstr
         "factors as far as we know them). The correction to each";
      printstr "factor, f(i), is (a(i)*v) mod f0(i) where f0(i) is";
      prin2!* "f(i) mod "; prin2!* p;
      printstr "(ie. the f(i) used in calculating the a(i))"
      >>>>
   end;

symbolic procedure initialize!-hensel(r,p,poly,mvec,fvec,cbd);
% Set up the vectors and initialize the fluids.
  begin scalar u0;
    delfvec:=mkvect r;
    facvec:=mkvect r;
    hensel!-poly:=poly;
    modfvec:=mvec;
    factorvec:=fvec;
    coefftbd:=cbd;
    factors!-done:=nil;
    deltam:=p;
    u0:=1;
    for i:=1:r do u0:=multf(getv(factorvec,i),u0);
    current!-factor!-product:=u0;
    return u0
  end;

% symbolic procedure reset!-quadratic!-step!-fluids(poly,faclist,n);
%   begin scalar i,om,modf;
%     current!-factor!-product:=poly;
%     om:=set!-modulus hensel!-growth!-size;
%     i:=0;
%     for each fac in faclist do <<
%       putv(factorvec,i:=iadd1 i,fac);
%       putv(modfvec,i,modf:=reduce!-mod!-p fac);
%       putv(alphavec,i,cdr get!-alpha modf) >>;
%      for i:=1:n do <<
%        prin2 "F("; % prin2 i; % prin2 ") = ";
%        printsf getv(factorvec,i);
%        prin2 "F("; % prin2 i; % prin2 ") MOD P = ";
%        printsf getv(modfvec,i);
%        prin2 "A("; % prin2 i; % prin2 ") = ";
%        printsf getv(alphavec,i) >>;
%     set!-modulus om
%   end;

symbolic procedure reset!-quadratic!-step!-fluids(poly,faclist,n);
  begin scalar i,om,facpairlist,cfp!-mod!-p,fhatlist;
    current!-factor!-product:=poly;
    om:=set!-modulus hensel!-growth!-size;
    cfp!-mod!-p:=reduce!-mod!-p current!-factor!-product;
    i:=0;
    facpairlist:=for each fac in faclist collect <<
      i:= i #+ 1;
      (fac . reduce!-mod!-p fac) >>;
    fhatlist:=for each facc in facpairlist collect
      quotfail!-mod!-p(cfp!-mod!-p,cdr facc);
    if factors!-done then alphalist:=
      find!-alphas!-in!-a!-ring(i,
        for each facpr in facpairlist collect cdr facpr,
        fhatlist,1);
          % a bug has surfaced such that the alphas get out of step.
          % In this case so recalculate them to stop the error for now.
    i:=0;
    for each facpair in facpairlist do <<
      putv(factorvec,i:=iadd1 i,car facpair);
      putv(modfvec,i,cdr facpair);
      putv(alphavec,i,cdr get!-alpha cdr facpair) >>;
%      for i:=1:n do <<
%        prin2 "f("; % prin2 i; % prin2 ") = ";
%        printsf getv(factorvec,i);
%        prin2 "f("; % prin2 i; % prin2 ") mod p = ";
%        printsf getv(modfvec,i);
%        prin2 "a("; % prin2 i; % prin2 ") = ";
%        printsf getv(alphavec,i) >>;
    set!-modulus om
  end;

symbolic procedure quadratic!-step(m,r);
% Code for adjusting the hensel variables to take quadratic steps in
% the growing process.
  begin scalar w,s,cfp!-mod!-p;
    set!-modulus m;
    cfp!-mod!-p:=reduce!-mod!-p current!-factor!-product;
    for i:=1:r do putv(facvec,i,reduce!-mod!-p getv(factorvec,i));
    for i:=1:r do putv(fhatvec,i,
      quotfail!-mod!-p(cfp!-mod!-p,getv(facvec,i)));
    w:=form!-sum!-and!-product!-mod!-m(alphavec,fhatvec,r);
    w:=!*mod2f plus!-mod!-p(1,minus!-mod!-p w);
    s:=quotfail(w,deltam);
    set!-modulus deltam;
    s:=!*f2mod s;
            % Boxes S up to look like a poly mod deltam.
    for i:=1:r do <<
      w:=remainder!-mod!-p(times!-mod!-p(s,getv(alphavec,i)),
        getv(modfvec,i));
      putv(alphavec,i,
        addf(!*mod2f getv(alphavec,i),multf(!*mod2f w,deltam))) >>;
    s:=modfvec;
    modfvec:=facvec;
    facvec:=s;
    deltam:=m;
            % this is our new growth rate.
    set!-modulus deltam;
    for i:=1:r do <<
      putv(facvec,i,"RUBBISH");
            % we will want to overwrite facvec next time so we
            % had better point it to the old (no longer needed)
            % modvec. Also mark it as containing rubbish for safety.
      putv(alphavec,i,!*f2mod getv(alphavec,i)) >>;
            % Make sure the alphas are boxed up as being mod new deltam.
    if not !*overview then factor!-trace <<
      printstr "The new modular polynomials are chosen such that:";
      terpri();
      prin2!* "   a(1)*h(1) + ... + a(";
      prin2!* r;
      prin2!* ")*h("; prin2!* r;
      prin2!* ") = 1 mod "; printstr m;
      terpri();
      printstr "  where h(i)=(product of all f(j) [see below])/f(i)";
      printstr "    and degree of a(i) < degree of f(i).";
      for i:=1:r do <<
        prin2!* "  a("; prin2!* i; prin2!* ")=";
        printsf getv(alphavec,i);
        prin2!* "   f("; prin2!* i; prin2!* ")=";
        printsf getv(modfvec,i) >>
    >>
  end;

symbolic procedure terms!-done(fvec,delfvec,m);
  begin scalar flist,delflist;
    for i:=1:number!-of!-factors do <<
      flist:=getv(fvec,i) . flist;
      delflist:=getv(delfvec,i) . delflist >>;
    return terms!.done(number!-of!-factors,flist,delflist,
                                 number!-of!-factors,m)
  end;

symbolic procedure terms!.done(n,flist,delflist,r,m);
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
    f1:=terms!.done(k,f1,delf1,r,m);
    delf1:=cdr f1; f1:=car f1;
    f2:=terms!.done(n-k,f2,delf2,r,m);
    delf2:=cdr f2; f2:=car f2;
    delf1:=
      addf(addf(
        multf(f1,delf2),
        multf(f2,delf1)),
        multf(multf(delf1,m),delf2));
    if n=r then return delf1;
    return (multf(f1,f2) . delf1)
  end;

symbolic procedure try!.combining(l,poly,m,sofar);
  try!.combining1(l,poly,m,sofar,2);

% The following code is not optimal. If we find a combination of k
% factors, we will re-rty all the previous combinations of k factors
% already tried.
% This is not frightfully serious, since in practice most such
% combinations will have had something in common with the set found, and
% so won't re-appear.  This is definitely better than the previous
% version, which re-tried all combinations.  JHD 14.1.88.

symbolic procedure try!.combining1(l,poly,m,sofar,k);
% l is a list of factors, f(i), s.t. (product of the f(i) mod m) = poly
% but no f(i) divides poly over the integers.  We find the combinations
% of the f(i) that yield the true factors of poly over the integers.
% Sofar is a list of these factors found so far.
% start combining them K at a time
  if poly=1 then
    if null l then sofar
    else errorf(list("TOO MANY BAD FACTORS:",l))
  else begin scalar n,res,ff,v,w,w1,combined!.factors,ll,lcfinv,oldmod;
    n:=length l;
    if n=1 then
      if ldeg car l > (ldeg poly)/2 then
        return ('one! bad! factor . sofar)
      else errorf(list("ONE BAD FACTOR DOES NOT FIT:",l));
    if n=2 or n=3 then <<
      w:=lc cdar l; % The LC of all the factors is the same.
      while not (w=lc poly) do poly:=quotfail(poly,w);
            % poly's LC may be a higher power of w than we want
            % and we must return a result with the same
            % LC as each of the combined factors.
      if not !*overview then factor!-trace <<
        printstr "We combine:";
         for each lf in l do printsf cdr lf;
         prin2!* " mod "; prin2!* m;
         printstr " to give correct factor:";
         printsf poly >>;
       combine!.alphas(l,t);
       return (poly . sofar) >>;
    ll:=for each ff in l collect (cdr ff . car ff);
%    K := 2; % K is now an argument
    oldmod := set!-general!-modulus m;
    lcfinv := general!-modular!-reciprocal lc cdar l;
    set!-general!-modulus oldmod;
  loop1:
      if k > n/2 then go to exit;
      w:=koutof(k,if 2*k=n then cdr l else l,nil);
                   % We needn't try a combination and its complement
      while w and
            (v:=factor!-trialdiv(poly,car w,m,ll,lcfinv))='didntgo do
      << w:=cdr w;
        while w and
            ((car w = '!*lazyadjoin) or (car w = '!*lazykoutof)) do
          if car w= '!*lazyadjoin then
            w:=lazy!-adjoin(cadr w,caddr w,cadr cddr w)
          else w:=koutof(cadr w,caddr w,cadr cddr w)
        >>;
      if not(v='didntgo) then <<
        ff:=car v; v:=cdr v;
        if not !*overview then factor!-trace <<
          printstr "We combine:";
           for each a in car w do printsf a;
         prin2!* " mod "; prin2!* m;
         printstr " to give correct factor:";
         printsf ff >>;
       for each a in car w do <<
         w1:=l;
         while not (a = cdar w1) do w1:=cdr w1;
         combined!.factors:=car w1 . combined!.factors;
         l:=delete(car w1,l) >>;
       combine!.alphas(combined!.factors,t);
%      Now combine the rest, starting with k-tuples
       res:=try!.combining1(l,v,m,ff . sofar,k);
       go to exit>>;
    k := k + 1;
    go to loop1;
  exit:
    if res then return res
    else <<
      w:=lc cdar l; % The LC of all the factors is the same.
      while not (w=lc poly) do poly:=quotfail(poly,w);
            % poly's LC may be a higher power of w than we want
            % and we must return a result with the same
            % LC as each of the combined factors.
      if not !*overview then factor!-trace <<
        printstr "We combine:";
          for each ff in l do printsf cdr ff;
          prin2!* " mod "; prin2!* m;
          printstr " to give correct factor:";
          printsf poly >>;
      combine!.alphas(l,t);
      return (poly . sofar) >>
  end;

symbolic procedure koutof(k,l,sofar);
% Produces all permutations of length k from list l accumulating them
% in sofar as we go.  We use lazy evaluation in that this results in
% a permutation dotted with:
%   ( '!*lazy . (argument for eval) )
%  except when k=1 when the permutations are explicitly given.
  if k=1 then append(
    for each f in l collect list cdr f,sofar)
  else if k>length l then sofar
  else <<
    while eqcar(l,'!*lazyadjoin) or eqcar(l,'!*lazykoutof) do
      if car l='!*lazyadjoin then
        l := lazy!-adjoin(cadr l,caddr l,cadr cddr l)
      else l := koutof(cadr l,caddr l,cadr cddr l);
    if k=length l then
      (for each ll in l collect cdr ll ) . sofar
    else koutof(k,cdr l,
      list('!*lazyadjoin,cdar l,
        list('!*lazykoutof,(k-1),cdr l,nil),
         sofar)) >>;

symbolic procedure lazy!-adjoin(item,l,tail);
% Dots item with each element in l using lazy evaluation on l.
% If l is null tail results.
 << while eqcar(l,'!*lazyadjoin) or eqcar(l,'!*lazykoutof) do
      if car l ='!*lazyadjoin then
        l:=lazy!-adjoin(cadr l,caddr l,cadr cddr l)
      else l:=koutof(cadr l,caddr l,cadr cddr l);
    if null l then tail
    else (item . car l) .
     if null cdr l then tail
     else list('!*lazyadjoin,item,cdr l,tail) >>;

symbolic procedure factor!-trialdiv(poly,flist,m,llist,lcfinv);
% Combines the factors in FLIST mod M and test divides the result
% into POLY (over integers) to see if it goes. If it doesn't
% then DIDNTGO is returned, else the pair (D . Q) is
% returned where Q is the quotient obtained and D is the product
% of the factors mod M;
% Abbott,J.A., Bradford,R.J. & Davenport,J.H.,
% A Remark on Factorisation.
% SIGSAM Bulletin 19(1985) 2, pp. 31-33, 37.
  if polyzerop poly then errorf "Test dividing into zero?"
  else begin scalar d,q,tcpoly,tcoeff,x,oldmod,w,poly1,try1;
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
    tcoeff := trailing!.coefft(car flist,x);
    oldmod := set!-general!-modulus m;
    for each fac in cdr flist do
      tcoeff := general!-modular!-times(
                  general!-modular!-times(tcoeff,lcfinv),
                                        trailing!.coefft(fac,x));
    if not zerop remainder(tcpoly,
              w:=general!-make!-modular!-symmetric tcoeff) then <<
      factor!-trace printstr " it didn't go (tc test)";
      set!-general!-modulus oldmod;
%      if not(w = trailing!.coefft(car COMBINE(FLIST,M,LLIST,lcfinv),x))
%             then <<
%         printstr "incompatibility: we have";
%         prin2!* w;
%         printstr "which should be the trailing coefficient of:";
%         prin2!* car combine(flist,m,llist,lcfinv) >>;
      return 'didntgo >>;
  % it has passed the tc test - now try evaluating at 1;
    poly1 := eval!-at!-1 poly;
    try1 := eval!-at!-1 car flist;
    for each fac in cdr flist do
      try1 := general!-modular!-times(
                general!-modular!-times(try1,lcfinv),eval!-at!-1 fac);
    if (zerop try1 and not zerop poly1) or
       not zerop remainder(poly1,general!-make!-modular!-symmetric try1)
         then <<
          factor!-trace printstr " it didn't go (test at 1)";
          set!-general!-modulus oldmod;
          return 'didntgo >>;
  % it has passed both tests - work out longhand;
    set!-general!-modulus oldmod;
    d:=combine(flist,m,llist,lcfinv);
    if didntgo(q:=quotf(poly,car d)) then <<
      factor!-trace printstr " it didn't go (division fail)";
      return 'didntgo >>
    else <<
      factor!-trace printstr " it worked !";
      return (car d . quotf(q,cdr d)) >>
  end;

symbolic procedure eval!-at!-1 f;
  % f a univariate standard form over Z with f(0) neq 0;
  % return the integer f(1);
  if atom f then f else (lc f) + eval!-at!-1(red f);

symbolic procedure combine(flist,m,l,lcfinv);
% Multiply factors in flist mod m.
% L is a list of the factors for use in FACTOR!-TRACE.
  begin scalar om,res,lcf,lcfprod;
    lcf := lc car flist; % all leading coeffts should be the same.
    lcfprod := 1;
% This is one of only two places in the entire factorizer where
% it is ever necessary to use a modulus larger than word-size.
    if m>largest!-small!-modulus then <<
      om:=set!-general!-modulus m;
%      lcfinv := general!-modular!-reciprocal lcf; Done once and for all
      res:=general!-reduce!-mod!-p car flist;
      for each ff in cdr flist do <<
        if not(lcf=lc ff) then errorf "BAD LC IN FLIST";
        res:=general!-times!-mod!-p(
            general!-times!-mod!-p(lcfinv,
                general!-reduce!-mod!-p ff),res);
        lcfprod := lcfprod*lcf >>;
      res:=general!-make!-modular!-symmetric res;
      set!-modulus om;
      return (res . lcfprod) >>
    else <<
      om:=set!-modulus m;
      lcfinv := modular!-reciprocal lcf;
      res:=reduce!-mod!-p car flist;
      for each ff in cdr flist do <<
        if not(lcf=lc ff) then errorf "BAD LC IN FLIST";
        res:=times!-mod!-p(times!-mod!-p(lcfinv,reduce!-mod!-p ff),res);
        lcfprod := lcfprod*lcf >>;
      res:=make!-modular!-symmetric res;
      set!-modulus om;
      return (res . lcfprod) >>
  end;

symbolic procedure combine!.alphas(flist,fixlcs);
% Combine the alphas associated with each of these factors to
% give the one alpha for their combination.
  begin scalar f1,a1,ff,aa,oldm,lcfac,lcfinv,saveflist;
    oldm:=set!-modulus hensel!-growth!-size;
    flist:=for each fac in flist collect <<
      saveflist:= (reduce!-mod!-p cdr fac) . saveflist;
      (car fac) . car saveflist >>;
    if fixlcs then <<
        lcfinv:=modular!-reciprocal lc cdar flist;
        lcfac:=modular!-expt(lc cdar flist,sub1 length flist)
      >>
      else << lcfinv:=1; lcfac:=1 >>;
            % If FIXLCS is set then we have combined n factors
            % (each with the same l.c.) to give one and we only need one
            % l.c. in the result, we have divided the combination by
            % lc**(n-1) and we must be sure to do the same for the
            % alphas.
    ff:=cdar flist;
    aa:=cdr get!-alpha ff;
    flist:=cdr flist;
    while flist do <<
      f1:=cdar flist;
      a1:=cdr get!-alpha f1;
      flist:=cdr flist;
      aa:=plus!-mod!-p(times!-mod!-p(aa,f1),times!-mod!-p(a1,ff));
      ff:=times!-mod!-p(ff,f1)
    >>;
    for each a in alphalist do
      if not member(car a,saveflist) then
        flist:=(car a . times!-mod!-p(cdr a,lcfac)) . flist;
    alphalist:=(quotient!-mod!-p(ff, lcfac) . aa) . flist;
    set!-modulus oldm
  end;

% The following code is for dividing out factors in the middle
% of the Hensel construction and adjusting all the associated
% variables that go with it.


symbolic procedure adjust!-growth(facdone,k,m);
% One factor (at least) divides out so we can reconfigure the
% problem for Hensel constrn giving a smaller growth and hopefully
% reducing the coefficient bound considerably.
  begin scalar w,u,bound!-scale,modflist,factorlist,fhatlist,
        modfdone,b;
    factorlist:=vec2list!-without!-k(factorvec,k);
    modflist:=vec2list!-without!-k(modfvec,k);
    fhatlist:=vec2list!-without!-k(fhatvec,k);
    w:=number!-of!-factors;
    modfdone:=getv(modfvec,k);
top:
    factors!-done:=facdone . factors!-done;
    if (number!-of!-factors:=number!-of!-factors #- 1)=1 then <<
      factors!-done:=hensel!-poly . factors!-done;
      number!-of!-factors:=0;
      hensel!-poly:=1;
      if not !*overview then factor!-trace <<
        printstr "    All factors found:";
        for each fd in factors!-done do printsf fd >>;
      return polyzero >>;
    fhatlist:=for each fhat in fhatlist collect
      quotfail!-mod!-p(if null fhat then polyzero else fhat,modfdone);
    u:=comfac facdone;  % Take contents and prim. parts.
    if car u then
      errorf(list("Factor divisible by main variable: ",facdone,car u));
    facdone:=quotfail(facdone,cdr u);
    bound!-scale:=cdr u;
    if not((b:=lc facdone)=1) then begin scalar b!-inv,old!-m;
      hensel!-poly:=quotfail(hensel!-poly,b**number!-of!-factors);
      b!-inv:=modular!-reciprocal modular!-number b;
      modflist:=for each modf in modflist collect
        times!-mod!-p(b!-inv,modf);
% This is one of only two places in the entire factorizer where
% it is ever necessary to use a modulus larger than word-size.
      if m>largest!-small!-modulus then <<
        old!-m:=set!-general!-modulus m;
        factorlist:=for each facc in factorlist collect
          adjoin!-term(lpow facc,quotfail(lc facc,b),
            general!-make!-modular!-symmetric(
              general!-times!-mod!-p(
            general!-modular!-reciprocal general!-modular!-number b,
                            general!-reduce!-mod!-p red facc))) >>
      else <<
        old!-m:=set!-modulus m;
        factorlist:=for each facc in factorlist collect
          adjoin!-term(lpow facc,quotfail(lc facc,b),
            make!-modular!-symmetric(
              times!-mod!-p(modular!-reciprocal modular!-number b,
                            reduce!-mod!-p red facc))) >>;
            % We must be careful not to destroy the information
            % that we have about the leading coefft.
      set!-modulus old!-m;
      fhatlist:=for each fhat in fhatlist collect
        times!-mod!-p(
          modular!-expt(b!-inv,number!-of!-factors #- 1),fhat)
    end;
try!-another!-factor:
    if (w:=w #- 1)>0 then
      if not didntgo
        (u:=quotf(hensel!-poly,facdone:=car factorlist)) then <<
        hensel!-poly:=u;
        factorlist:=cdr factorlist;
        modfdone:=car modflist;
        modflist:=cdr modflist;
        fhatlist:=cdr fhatlist;
        goto top >>
      else <<
        factorlist:=append(cdr factorlist,list car factorlist);
        modflist:=append(cdr modflist,list car modflist);
        fhatlist:=append(cdr fhatlist,list car fhatlist);
        goto try!-another!-factor >>;
    set!-fluids!-for!-newhensel(factorlist,fhatlist,modflist);
    bound!-scale:=
      bound!-scale * get!-coefft!-bound(
        quotfail(hensel!-poly,bound!-scale**(number!-of!-factors #- 1)),
        ldeg hensel!-poly);
    % We expect the new coefficient bound to be smaller, but on
    % dividing out a factor our polynomial's height may have grown
    % more than enough to compensate in the bound formula for
    % the drop in degree. Anyway, the bound we computed last time
    % will still be valid, so let's stick with the smaller.
    if bound!-scale < coefftbd then coefftbd := bound!-scale;
    w:=quotfail(addf(hensel!-poly,negf current!-factor!-product),
          m/deltam);
    if not !*overview then factor!-trace <<
      printstr "    Factors found to be correct:";
      for each fd in factors!-done do
        printsf fd;
      printstr "Remaining factors are:";
      printvec("    f(",number!-of!-factors,") = ",factorvec);
      prin2!* "New coefficient bound is "; printstr coefftbd;
      prin2!* " and the residue is now "; printsf w >>;
    return w
  end;

symbolic procedure vec2list!-without!-k(v,k);
% Turn a vector into a list leaving out Kth element.
  begin scalar w;
    for i:=1:number!-of!-factors do
      if not(i=k) then w:=getv(v,i) . w;
    return w
  end;

symbolic procedure set!-fluids!-for!-newhensel(flist,fhatlist,modflist);
<< current!-factor!-product:=1;
  alphalist:=
    find!-alphas!-in!-a!-ring(number!-of!-factors,modflist,fhatlist,1);
  for i:=number!-of!-factors step -1 until 1 do <<
    putv(factorvec,i,car flist);
    putv(modfvec,i,car modflist);
    putv(fhatvec,i,car fhatlist);
    putv(alphavec,i,cdr get!-alpha car modflist);
    current!-factor!-product:=multf(car flist,current!-factor!-product);
    flist:=cdr flist;
    modflist:=cdr modflist;
    fhatlist:=cdr fhatlist >>
>>;

symbolic procedure set!-hensel!-fluids!-back p;
% After the Hensel growth we must be careful to set back any fluids
% that have been changed when we divided out a factor in the middle
% of growing.  Since calculating the alphas involves modular division
% we cannot do it mod DELTAM which is generally a non-trivial power of
% P (prime). So we calculate them mod P and if necessary we can do a
% few quadratic growth steps later.
  begin scalar n,fd,modflist,fullf,modf;
    set!-modulus p;
    deltam:=p;
    n:=number!-of!-factors #+ length (fd:=factors!-done);
    current!-factor!-product:=hensel!-poly;
    for i:=(number!-of!-factors #+ 1):n do <<
      putv(factorvec,i,fullf:=car fd);
      putv(modfvec,i,modf:=reduce!-mod!-p fullf);
      current!-factor!-product:=multf(fullf,current!-factor!-product);
      modflist:=modf . modflist;
      fd:=cdr fd >>;
    for i:=1:number!-of!-factors do <<
      modf:=reduce!-mod!-p !*mod2f getv(modfvec,i);
            % need to 'unbox' a modpoly before reducing it mod p as we
            % know that the input modpoly is wrt a larger modulus
            % (otherwise this would be a stupid thing to do anyway!)
            % and so we are just pretending it is a full poly.
      modflist:=modf . modflist;
      putv(modfvec,i,modf) >>;
    alphalist:=alphas(n,modflist,1);
    for i:=1:n do putv(alphavec,i,cdr get!-alpha getv(modfvec,i));
    number!-of!-factors:=n
  end;

endmodule;

end;
