module imageset;

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
        !*force!-zero!-set
        !*trfac
        bad!-case
        chosen!-prime
        current!-modulus
        f!-numvec
        factor!-level
        factor!-trace!-list
        factor!-x
        factored!-lc
        forbidden!-primes
        forbidden!-sets
        image!-content
        image!-lc
        image!-mod!-p
        image!-poly
        image!-set
        image!-set!-modulus
        kord!*
        m!-image!-variable
        modulus!/2
        multivariate!-input!-poly
        no!-of!-primes!-to!-try
        othervars
        polyzero
        save!-zset
        usable!-set!-found
        vars!-to!-kill
        zero!-set!-tried
        zerovarset
        zset);


%*******************************************************************;
%
%      this section deals with the image sets used in
%      factorising multivariate polynomials according
%      to wang's theories.
%       ref:  math. comp. vol.32 no.144 oct 1978 pp 1217-1220
%        'an improved multivariate polynomial factoring algorithm'
%
%*******************************************************************;


%*******************************************************************;
%    first we have routines for generating the sets
%*******************************************************************;


symbolic procedure generate!-an!-image!-set!-with!-prime
                      good!-set!-needed;
% given a multivariate poly (in a fluid) we generate an image set
% to make it univariate and also a random prime to use in the
% modular factorization. these numbers are random except that
% we will not allow anything in forbidden!-sets or forbidden!-primes;
  begin scalar currently!-forbidden!-sets,u;
    u:=multivariate!-input!-poly;
            % a bit of a handful to type otherwise!!!!   ;
    image!-set:=nil;
    currently!-forbidden!-sets:=forbidden!-sets;
tryanotherset:
    if image!-set then
      currently!-forbidden!-sets:=image!-set .
                                currently!-forbidden!-sets;
%   wtime:=time();
    image!-set:=get!-new!-set currently!-forbidden!-sets;
%           princ "Trying imageset= ";
%           prin2t image!-set;
%   trace!-time <<
%     display!-time("    New image set found in ",time()-wtime);
%     wtime:=time() >>;
    image!-lc:=make!-image!-lc!-list(lc u,image!-set);
            % list of image lc's wrt different variables in IMAGE-SET;
%    princ "Image set to try is:";% prin2t image!-set;
%    prin2!* "L.C. of poly is:";% printsf lc u;
%    prin2t "Image l.c.s with variables substituted on order:";
%    for each imlc in image!-lc do printsf imlc;
%   trace!-time
%     display!-time("    Image of lc made in ",time()-wtime);
    if (caar image!-lc)=0 then goto tryanotherset;
%   wtime:=time();
    image!-poly:=make!-image(u,image!-set);
%   trace!-time <<
%     display!-time("    Image poly made in ",time()-wtime);
%     wtime:=time() >>;
    image!-content:=get!.content image!-poly;
            % note: the content contains the image variable if it
            % is a factor of the image poly;
%   trace!-time
%     display!-time("    Content found in ",time()-wtime);
    image!-poly:=quotfail(image!-poly,image!-content);
            % make sure the image polynomial is primitive which includes
            % making the leading coefft positive (-ve content if
            % necessary).
            % If the image polynomial was of the form k*v^2 where v is
            % the image variable then GET.CONTENT will have taken out
            % one v and the k leaving the polynomial v here.
            % Divisibility by v here thus indicates that the image was
            % not square free, and so we will not be able to find a
            % sensible prime to use.
    if not didntgo quotf(image!-poly,!*k2f m!-image!-variable) then
        go to tryanotherset;
%   wtime:=time();
    image!-mod!-p:=find!-a!-valid!-prime(image!-lc,image!-poly,
      not numberp image!-content);
    if image!-mod!-p='not!-square!-free then goto tryanotherset;
%   trace!-time <<
%     display!-time("    Prime and image mod p found in ",time()-wtime);
%     wtime:=time() >>;
    if factored!-lc then
      if f!-numvec:=unique!-f!-nos(factored!-lc,image!-content,
          image!-set) then usable!-set!-found:=t
%       trace!-time
%         display!-time("    Nos for lc found in ",time()-wtime) >>
      else <<
%       trace!-time display!-time("    Nos for lc failed in ",
%           time()-wtime);
        if (not usable!-set!-found) and good!-set!-needed then
          goto tryanotherset >>
  end;


symbolic procedure get!-new!-set forbidden!-s;
% associate each variable in vars-to-kill with a random no. mod
% image-set-modulus. If the boolean tagged with a variable is true then
% a value of 1 or 0 is no good and so rejected, however all other
% variables can take these values so they are tried exhaustively before
% using truly random values. sets in forbidden!-s not allowed;
  begin scalar old!.m,alist,n,nextzset,w;
    if zero!-set!-tried then <<
      if !*force!-zero!-set then
        errorf "Zero set tried - possibly it was invalid";
      image!-set!-modulus:=iadd1 image!-set!-modulus;
      old!.m:=set!-modulus image!-set!-modulus;
      alist:=for each v in vars!-to!-kill collect
      << n:=modular!-number next!-random!-number();
         if n>modulus!/2 then n:=n-current!-modulus;
         if cdr v then <<
           while n=0
              or n=1
              or (n = (isub1 current!-modulus)) do
             n:=modular!-number next!-random!-number();
           if n>modulus!/2 then n:=n-current!-modulus >>;
         car v . n >> >>
    else <<
      old!.m:=set!-modulus image!-set!-modulus;
      nextzset:=car zset;
      alist:=for each zv in zerovarset collect <<
        w:=zv . car nextzset;
        nextzset:=cdr nextzset;
        w >>;
      if othervars then alist:=
        append(alist,for each v in othervars collect <<
          n:=modular!-number next!-random!-number();
          while n=0
             or n=1
             or (n = (isub1 current!-modulus)) do
            n:=modular!-number next!-random!-number();
          if n>modulus!/2 then n:=n-current!-modulus;
          v . n >>);
      if null(zset:=cdr zset) then
        if null save!-zset then zero!-set!-tried:=t
        else zset:=make!-next!-zset save!-zset;
      alist:=for each v in cdr kord!* collect atsoc(v,alist);
            % Puts the variables in alist in the right order;
      >>;
    set!-modulus old!.m;
    return if member(alist,forbidden!-s) then
        get!-new!-set forbidden!-s
      else alist
  end;


%**********************************************************************
% now given an image/univariate polynomial find a suitable random prime;


symbolic procedure find!-a!-valid!-prime(lc!-u,u,factor!-x);
% finds a suitable random prime for reducing a poly mod p.
% u is the image/univariate poly. we are not allowed to use
% any of the primes in forbidden!-primes (fluid).
% lc!-u is either numeric or (in the multivariate case) a list of
% images of the lc;
  begin scalar currently!-forbidden!-primes,res,prime!-count,v,w;
    if factor!-x then u:=multf(u,v:=!*k2f m!-image!-variable);
    chosen!-prime:=nil;
    currently!-forbidden!-primes:=forbidden!-primes;
    prime!-count:=1;
tryanotherprime:
    if chosen!-prime then
      currently!-forbidden!-primes:=chosen!-prime .
                                 currently!-forbidden!-primes;
    chosen!-prime:=get!-new!-prime currently!-forbidden!-primes;
    set!-modulus chosen!-prime;
    if not atom lc!-u then <<
      w:=lc!-u;
      while w and
           ((domainp caar w and not(modular!-number caar w = 0))
              or not (domainp caar w or modular!-number lnc caar w=0))
            do w:=cdr w;
      if w then goto tryanotherprime >>
    else if modular!-number lc!-u=0 then goto tryanotherprime;
    res:=monic!-mod!-p reduce!-mod!-p u;
    if not square!-free!-mod!-p res then
      if multivariate!-input!-poly
         and (prime!-count:=prime!-count+1)>no!-of!-primes!-to!-try
        then <<no!-of!-primes!-to!-try := no!-of!-primes!-to!-try+1;
               res:='not!-square!-free>>
      else goto tryanotherprime;
    if factor!-x and not(res='not!-square!-free) then
      res:=quotfail!-mod!-p(res,!*f2mod v);
    return res
 end;

symbolic procedure get!-new!-prime forbidden!-p;
% get a small prime that is not in the list forbidden!-p;
% we pick one of the first 10 primes if we can;
  if !*force!-prime then !*force!-prime
  else begin scalar p,primes!-done;
    for each pp in forbidden!-p do
      if pp<32 then primes!-done:=pp.primes!-done;
tryagain:
    if null(p:=random!-teeny!-prime primes!-done) then <<
      p:=random!-small!-prime();
      primes!-done:='all >>
    else primes!-done:=p . primes!-done;
    if member(p,forbidden!-p) then goto tryagain;
    return p
  end;

%***********************************************************************
% find the numbers associated with each factor of the leading
% coefficient of our multivariate polynomial. this will help
% to distribute the leading coefficient later.;



symbolic procedure unique!-f!-nos(v,cont!.u0,im!.set);
% given an image set (im!.set), this finds the numbers associated with
% each factor in v subject to wang's condition (2) on the image set.
% this is an implementation of his algorithm n. if the condition
% is met the result is a vector containing the images of each factor
% in v, otherwise the result is nil;
  begin scalar d,k,q,r,lc!.image!.vec;
            % v's integer factor is at the front:  ;
    k:=length cdr v;
            % no. of non-trivial factors of v;
    if not numberp cont!.u0 then cont!.u0:=lc cont!.u0;
    putv(d:=mkvect k,0,abs(cont!.u0 * car v));
            % d will contain the special numbers to be used in the
            % loop below;
    putv(lc!.image!.vec:=mkvect k,0,abs(cont!.u0 * car v));
            % vector for result with 0th entry filled in;
    v:=cdr v;
            % throw away integer factor of v;
            % k is no. of non-trivial factors (say f(i)) in v;
            % d will contain the nos. associated with each f(i);
            % v is now a list of the f(i) (and their multiplicities);
    for i:=1:k do
    << q:=abs make!-image(caar v,im!.set);
       putv(lc!.image!.vec,i,q);
       v:=cdr v;
       for j:=isub1 i step -1 until 0 do
       << r:=getv(d,j);
          while not onep r do
          << r:=gcdn(r,q); q:=q/r >>;
          if onep q then <<lc!.image!.vec:=nil; j := -1>>
            % if q=1 here then we have failed the condition so exit;
          >>;
      if null lc!.image!.vec then i := k+1 else putv(d,i,q);
            % else q is the ith number we want;
   >>;
    return lc!.image!.vec
  end;

symbolic procedure get!.content u;
% u is a univariate square free poly. gets the content of u (=integer);
% if lc u is negative then the minus sign is pulled out as well;
% nb. the content includes the variable if it is a factor of u;
  begin scalar c;
    c:=if poly!-minusp u then -(numeric!-content u)
       else numeric!-content u;
    if not didntgo quotf(u,!*k2f m!-image!-variable) then
      c:=adjoin!-term(mksp(m!-image!-variable,1),c,polyzero);
    return c
  end;


%********************************************************************;
%    finally we have the routines that use the numbers generated
%    by unique.f.nos to determine the true leading coeffts in
%    the multivariate factorization we are doing and which image
%    factors will grow up to have which true leading coefft.
%********************************************************************;




symbolic procedure distribute!.lc(r,im!.factors,s,v);
% v is the factored lc of a poly, say u, whose image factors (r of
% them) are in the vector im.factors. s is a list containing the
% image information including the image set, the image poly etc.
%  this uses wang's ideas for distributing the factors in v over
% those in im.factors. result is (delta . vector of the lc's of
% the full factors of u) , where delta is the remaining integer part
% of the lc that we have been unable to distribute.             ;
  (lambda factor!-level;
  begin scalar k,delta,div!.count,q,uf,i,d,max!.mult,f,numvec,
               dvec,wvec,dtwid,w;
    delta:=get!-image!-content s;
            % the content of the u image poly;
    dist!.lc!.msg1(delta,im!.factors,r,s,v);
    v:=cdr v;
            % we are not interested in the numeric factors of v;
    k:=length v;
            % number of things to distribute;
    numvec:=get!-f!-numvec s;
            % nos. associated with factors in v;
    dvec:=mkvect r;
    wvec:=mkvect r;
    for j:=1:r do <<
      putv(dvec,j,1);
      putv(wvec,j,delta*lc getv(im!.factors,j)) >>;
            % result lc's will go into dvec which we initialize to 1's;
            % wvec is a work vector that we use in the division process
            % below;
    v:=reverse v;
    for j:=k step -1 until 1 do
    << % (for each factor in v, call it f(j) );
      f:=caar v;
            % f(j) itself;
      max!.mult:=cdar v;
            % multiplicity of f(j) in v (=lc u);
      v:=cdr v;
      d:=getv(numvec,j);
            % number associated with f(j);
      i:=1; % we trial divide d into lc of each image
            % factor starting with 1st;
      div!.count:=0;
            % no. of d's that have been distributed;
      factor!-trace <<
        prin2!* "f("; prin2!* j; prin2!* ")= "; printsf f;
        prin2!* "There are "; prin2!* max!.mult;
        printstr " of these in the leading coefficient.";
        prin2!* "The absolute value of the image of f("; prin2!* j;
        prin2!* ")= "; printstr d >>;
      while ilessp(div!.count,max!.mult)
        and not igreaterp(i,r) do
      << q:=divide(getv(wvec,i),d);
            % first trial division;
        factor!-trace <<
          prin2!* "  Trial divide into ";
          prin2!* getv(wvec,i); printstr " :" >>;
        while (zerop cdr q) and ilessp(div!.count,max!.mult) do
        << putv(dvec,i,multf(getv(dvec,i),f));
            % f(j) belongs in lc of ith factor;
          factor!-trace <<
            prin2!* "    It goes so an f("; prin2!* j;
            prin2!* ") belongs in ";
            printsf getv(im!.factors,i);
            printstr "  Try again..." >>;
          div!.count:=iadd1 div!.count;
            % another d done;
          putv(wvec,i,car q);
            % save the quotient for next factor to distribute;
          q:=divide(car q,d);
            % try again;
        >>;
        i:=iadd1 i;
            % as many d's as possible have gone into that
            % factor so now try next factor;
        factor!-trace
           <<printstr "    no good so try another factor ..." >>>>;
            % at this point the whole of f(j) should have been
            % distributed by dividing d the maximum no. of times
            % (= max!.mult), otherwise we have an extraneous factor;
      if ilessp(div!.count,max!.mult) then
        <<bad!-case:=t; div!.count := max!.mult>>
    >>;
    if bad!-case then return;
    dist!.lc!.msg2(dvec,im!.factors,r);
    if onep delta then
    << for j:=1:r do <<
         w:=lc getv(im!.factors,j) /
          evaluate!-in!-order(getv(dvec,j),get!-image!-set s);
         if w<0 then begin
           scalar oldpoly;
           delta:= -delta;
           oldpoly:=getv(im!.factors,j);
           putv(im!.factors,j,negf oldpoly);
            % to keep the leading coefficients positive we negate the
            % image factors when necessary;
           multiply!-alphas(-1,oldpoly,getv(im!.factors,j));
            % remember to fix the alphas as well;
         end;
         putv(dvec,j,multf(abs w,getv(dvec,j))) >>;
      dist!.lc!.msg3(dvec,im!.factors,r);
      return (delta . dvec)
    >>;
      % if delta=1 then we know the true lc's exactly so put in their
      % integer contents and return with result.
      % otherwise try spreading delta out over the factors:      ;
    dist!.lc!.msg4 delta;
    for j:=1:r do
    << dtwid:=evaluate!-in!-order(getv(dvec,j),get!-image!-set s);
       uf:=getv(im!.factors,j);
       d:=gcddd(lc uf,dtwid);
       putv(dvec,j,multf(lc uf/d,getv(dvec,j)));
       putv(im!.factors,j,multf(dtwid/d,uf));
            % have to fiddle the image factors by an integer multiple;
       multiply!-alphas!-recip(dtwid/d,uf,getv(im!.factors,j));
            % fix the alphas;
       delta:=delta/(dtwid/d)
    >>;
    % Now we've done all we can to distribute delta so we return with
    % what's left:
    if delta<=0 then <<
      factor!-trace <<
         prin2!* "FINAL DELTA IS -VE IN DISTRIBUTE!.LC";
         printstr delta >>;
      delta := 1 >>;
    factor!-trace <<
      printstr "     Finally we have:";
      for j:=1:r do <<
        prinsf getv(im!.factors,j);
        prin2!* " with l.c. ";
        printsf getv(dvec,j) >> >>;
    return (delta . dvec)
  end) (factor!-level * 10);

symbolic procedure dist!.lc!.msg1(delta,im!.factors,r,s,v);
    factor!-trace <<
      terpri(); terpri();
      printstr "We have a polynomial whose image factors (call";
      printstr "them the IM-factors) are:";
      prin2!* delta; printstr " (= numeric content, delta)";
      printvec(" f(",r,")= ",im!.factors);
      prin2!* "  wrt the image set: ";
      for each x in get!-image!-set s do <<
        prinvar car x; prin2!* "="; prin2!* cdr x; prin2!* ";" >>;
      terpri!*(nil);
      printstr "We also have its true multivariate leading";
      printstr "coefficient whose factors (call these the";
      printstr "LC-factors) are:";
      fac!-printfactors v;
      printstr "We want to determine how these LC-factors are";
      printstr "distributed over the leading coefficients of each";
      printstr "IM-factor.  This enables us to feed the resulting";
      printstr "image factors into a multivariate Hensel";
      printstr "construction.";
      printstr "We distribute each LC-factor in turn by dividing";
      printstr "its image into delta times the leading coefficient";
      printstr "of each IM-factor until it finds one that it";
      printstr "divides exactly. The image set is chosen such that";
      printstr "this will only happen for the IM-factors to which";
      printstr "this LC-factor belongs - (there may be more than";
      printstr "one if the LC-factor occurs several times in the";
      printstr "leading coefficient of the original polynomial).";
      printstr "This choice also requires that we distribute the";
      printstr "LC-factors in a specific order:"
      >>;

symbolic procedure dist!.lc!.msg2(dvec,im!.factors,r);
    factor!-trace <<
      printstr "The leading coefficients are now correct to within an";
      printstr "integer factor and are as follows:";
      for j:=1:r do <<
        prinsf getv(im!.factors,j);
        prin2!* " with l.c. ";
        printsf getv(dvec,j) >> >>;

symbolic procedure dist!.lc!.msg3(dvec,im!.factors,r);
      factor!-trace <<
        printstr "Since delta=1, we have no non-trivial content of the";
        printstr
          "image to deal with so we know the true leading coefficients";
        printstr
          "exactly.  We fix the signs of the IM-factors to match those";
        printstr "of their true leading coefficients:";
        for j:=1:r do <<
          prinsf getv(im!.factors,j);
          prin2!* " with l.c. ";
          printsf getv(dvec,j) >> >>;

symbolic procedure dist!.lc!.msg4 delta;
    factor!-trace <<
      prin2!* " Here delta is not 1 meaning that we have a content, ";
      printstr delta;
      printstr "of the image to distribute among the factors somehow.";
      printstr "For each IM-factor we can divide its leading";
      printstr "coefficient by the image of its determined leading";
      printstr "coefficient and see if there is a non-trivial result.";
      printstr "This will indicate a factor of delta belonging to this";
      printstr "IM-factor's leading coefficient." >>;

endmodule;


end;
