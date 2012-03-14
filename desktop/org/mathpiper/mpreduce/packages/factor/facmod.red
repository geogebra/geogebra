module facmod; % Modular factorization: discover the factor count mod p.

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


fluid '(current!-modulus
        dpoly
        dwork1
        dwork2
        known!-factors
        linear!-factors
        m!-image!-variable
        modular!-info
        null!-space!-basis
        number!-needed
        poly!-mod!-p
        poly!-vector
        safe!-flag
        split!-list
        work!-vector1
        work!-vector2);


safe!-flag:= carcheck 0; % For speed of array access - important here.
carcheck 0;              % and again for fasl purposes (in case carcheck
                         % is flagged EVAL).

symbolic procedure get!-factor!-count!-mod!-p
                              (n,poly!-mod!-p,p,x!-is!-factor);
% gets the factor count mod p from the nth image using the
% first half of Berlekamp's method;
  begin scalar old!-m,f!-count;
    old!-m:=set!-modulus p;
%    PRIN2 "prime = ";% prin2t current!-modulus;
%    PRIN2 "degree = ";% prin2t ldeg poly!-mod!-p;
%   trace!-time display!-time("Entered GET-FACTOR-COUNT after ",time());
%   wtime:=time();
    f!-count:=modular!-factor!-count();
%   trace!-time display!-time("Factor count obtained in ",time()-wtime);
    split!-list:=
      ((if x!-is!-factor then car f!-count#+1 else car f!-count) . n)
        . split!-list;
    putv(modular!-info,n,cdr f!-count);
    set!-modulus old!-m
  end;

symbolic procedure modular!-factor!-count();
  begin
    scalar poly!-vector,wvec1,wvec2,x!-to!-p,
      n,w,lin!-f!-count,null!-space!-basis;
    known!-factors:=nil;
    dpoly:=ldeg poly!-mod!-p;
    wvec1:=mkvect (2#*dpoly);
    wvec2:=mkvect (2#*dpoly);
    x!-to!-p:=mkvect dpoly;
    poly!-vector:=mkvect dpoly;
    for i:=0:dpoly do putv(poly!-vector,i,0);
    poly!-to!-vector poly!-mod!-p;
    w:=count!-linear!-factors!-mod!-p(wvec1,wvec2,x!-to!-p);
    lin!-f!-count:=car w;
    if dpoly#<4 then return
       (if dpoly=0 then lin!-f!-count
        else lin!-f!-count#+1) .
        list(lin!-f!-count . cadr w,
             dpoly . poly!-vector,
             nil);
% When I use Berlekamp I certainly know that the polynomial
% involved has no linear factors;
%   wtime:=time();
    null!-space!-basis:=use!-berlekamp(x!-to!-p,caddr w,wvec1);
%   trace!-time display!-time("Berlekamp done in ",time()-wtime);
    n:=lin!-f!-count #+ length null!-space!-basis #+ 1;
            % there is always 1 more factor than the number of
            % null vectors we have picked up;
    return n . list(
     lin!-f!-count . cadr w,
     dpoly . poly!-vector,
     null!-space!-basis)
  end;

%**********************************************************************;
% Extraction of linear factors is done specially;

symbolic procedure count!-linear!-factors!-mod!-p(wvec1,wvec2,x!-to!-p);
% Compute gcd(x**p-x,u). It will be the product of all the
% linear factors of u mod p;
  begin scalar dx!-to!-p,lin!-f!-count,linear!-factors;
    for i:=0:dpoly do putv(wvec2,i,getv(poly!-vector,i));
    dx!-to!-p:=make!-x!-to!-p(current!-modulus,wvec1,x!-to!-p);
    for i:=0:dx!-to!-p do putv(wvec1,i,getv(x!-to!-p,i));
    if dx!-to!-p#<1 then <<
        if dx!-to!-p#<0 then putv(wvec1,0,0);
        putv(wvec1,1,modular!-minus 1);
        dx!-to!-p:=1 >>
    else <<
      putv(wvec1,1,modular!-difference(getv(wvec1,1),1));
      if dx!-to!-p=1 and getv(wvec1,1)=0 then
         if getv(wvec1,0)=0 then dx!-to!-p:=-1
         else dx!-to!-p:=0 >>;
    if dx!-to!-p#<0 then
      lin!-f!-count:=copy!-vector(wvec2,dpoly,wvec1)
    else lin!-f!-count:=gcd!-in!-vector(wvec1,dx!-to!-p,
      wvec2,dpoly);
    linear!-factors:=mkvect lin!-f!-count;
    for i:=0:lin!-f!-count do
      putv(linear!-factors,i,getv(wvec1,i));
    dpoly:=quotfail!-in!-vector(poly!-vector,dpoly,
        linear!-factors,lin!-f!-count);
    return list(lin!-f!-count,linear!-factors,dx!-to!-p)
  end;

symbolic procedure make!-x!-to!-p(p,wvec1,x!-to!-p);
  begin scalar dx!-to!-p,dw1;
    if p#<dpoly then <<
       for i:=0:p#-1 do putv(x!-to!-p,i,0);
       putv(x!-to!-p,p,1);
       return p >>;
    dx!-to!-p:=make!-x!-to!-p(p/2,wvec1,x!-to!-p);
    dw1:=times!-in!-vector(x!-to!-p,dx!-to!-p,x!-to!-p,dx!-to!-p,wvec1);
    dw1:=remainder!-in!-vector(wvec1,dw1,poly!-vector,dpoly);
    if not(iremainder(p,2)=0) then <<
       for i:=dw1 step -1 until 0 do putv(wvec1,i#+1,getv(wvec1,i));
       putv(wvec1,0,0);
       dw1:=remainder!-in!-vector(wvec1,dw1#+1,poly!-vector,dpoly)>>;
    for i:=0:dw1 do putv(x!-to!-p,i,getv(wvec1,i));
    return dw1
  end;

symbolic procedure find!-linear!-factors!-mod!-p(p,n);
% P is a vector representing a polynomial of degree N which has
% only linear factors. Find all the factors and return a list of
% them;
  begin
    scalar root,var,w,vec1;
    if n#<1 then return nil;
    vec1:=mkvect 1;
    putv(vec1,1,1);
    root:=0;
    while (n#>1) and not (root #> current!-modulus) do <<
        w:=evaluate!-in!-vector(p,n,root);
        if w=0 then << %a factor has been found!!;
          if var=nil then
             var:=mksp(m!-image!-variable,1) . 1;
          w:=!*f2mod
            adjoin!-term(car var,cdr var,!*n2f modular!-minus root);
          known!-factors:=w . known!-factors;
          putv(vec1,0,modular!-minus root);
          n:=quotfail!-in!-vector(p,n,vec1,1) >>;
        root:=root#+1 >>;
    known!-factors:=
        vector!-to!-poly(p,n,m!-image!-variable) . known!-factors
  end;


%**********************************************************************;
% Berlekamp's algorithm part 1: find null space basis giving factor
% count;


symbolic procedure use!-berlekamp(x!-to!-p,dx!-to!-p,wvec1);
% Set up a basis for the set of remaining (nonlinear) factors
% using Berlekamp's algorithm;
  begin
    scalar berl!-m,berl!-m!-size,w,dcurrent,current!-power;
    berl!-m!-size:=dpoly#-1;
    berl!-m:=mkvect berl!-m!-size;
    for i:=0:berl!-m!-size do <<
      w:=mkvect berl!-m!-size;
      for j:=0:berl!-m!-size do putv(w,j,0); %initialize to zero;
      putv(berl!-m,i,w) >>;
% Note that column zero of the matrix (as used in the
% standard version of Berlekamp's algorithm) is not in fact
% needed and is not used here;
% I want to set up a matrix that has entries
%  x**p, x**(2*p), ... , x**((n-1)*p)
% as its columns,
% where n is the degree of poly-mod-p
% and all the entries are reduced mod poly-mod-p;
% Since I computed x**p I have taken out some linear factors,
% so reduce it further;
    dx!-to!-p:=remainder!-in!-vector(x!-to!-p,dx!-to!-p,
      poly!-vector,dpoly);
    dcurrent:=0;
    current!-power:=mkvect berl!-m!-size;
    putv(current!-power,0,1);
    for i:=1:berl!-m!-size do <<
       if current!-modulus#>dpoly then
         dcurrent:=times!-in!-vector(
            current!-power,dcurrent,
            x!-to!-p,dx!-to!-p,
            wvec1)
       else << % Multiply by shifting;
         for i:=0:current!-modulus#-1 do
           putv(wvec1,i,0);
         for i:=0:dcurrent do
           putv(wvec1,current!-modulus#+i,
             getv(current!-power,i));
         dcurrent:=dcurrent#+current!-modulus >>;
       dcurrent:=remainder!-in!-vector(
         wvec1,dcurrent,
         poly!-vector,dpoly);
       for j:=0:dcurrent do
          putv(getv(berl!-m,j),i,putv(current!-power,j,
            getv(wvec1,j)));
% also I need to subtract 1 from the diagonal of the matrix;
       putv(getv(berl!-m,i),i,
         modular!-difference(getv(getv(berl!-m,i),i),1)) >>;
%   wtime:=time();
%   print!-m("Q matrix",berl!-m,berl!-m!-size);
    w := find!-null!-space(berl!-m,berl!-m!-size);
%   trace!-time display!-time("Null space found in ",time()-wtime);
    return w
  end;


symbolic procedure find!-null!-space(berl!-m,berl!-m!-size);
% Diagonalize the matrix to find its rank and hence the number of
% factors the input polynomial had;
  begin scalar null!-space!-basis;
% find a basis for the null-space of the matrix;
    for i:=1:berl!-m!-size do
      null!-space!-basis:=
        clear!-column(i,null!-space!-basis,berl!-m,berl!-m!-size);
%    print!-m("Null vectored",berl!-m,berl!-m!-size);
    return
      tidy!-up!-null!-vectors(null!-space!-basis,berl!-m,berl!-m!-size)
  end;

symbolic procedure print!-m(m,berl!-m,berl!-m!-size);
 << prin2t m;
    for i:=0:berl!-m!-size do <<
      for j:=0:berl!-m!-size do <<
        prin2 getv(getv(berl!-m,i),j);
        ttab((4#*j)#+4) >>;
      terpri() >> >>;



symbolic procedure clear!-column(i,
                    null!-space!-basis,berl!-m,berl!-m!-size);
% Process column I of the matrix so that (if possible) it
% just has a '1' in row I and zeros elsewhere;
  begin
    scalar ii,w;
% I want to bring a non-zero pivot to the position (i,i)
% and then add multiples of row i to all other rows to make
% all but the i'th element of column i zero. First look for
% a suitable pivot;
    ii:=0;
search!-for!-pivot:
    if getv(getv(berl!-m,ii),i)=0 or
       ((ii#<i) and not(getv(getv(berl!-m,ii),ii)=0)) then
          if (ii:=ii#+1)#>berl!-m!-size then
              return (i . null!-space!-basis)
          else go to search!-for!-pivot;
% Here ii references a row containing a suitable pivot element for
% column i. Permute rows in the matrix so as to bring the pivot onto
% the diagonal;
    w:=getv(berl!-m,ii);
    putv(berl!-m,ii,getv(berl!-m,i));
    putv(berl!-m,i,w);
            % swop rows ii and i ;
    w:=modular!-minus modular!-reciprocal getv(getv(berl!-m,i),i);
% w = -1/pivot, and is used in zeroing out the rest of column i;
    for row:=0:berl!-m!-size do
      if row neq i then begin
         scalar r; %process one row;
         r:=getv(getv(berl!-m,row),i);
         if not(r=0) then <<
           r:=modular!-times(r,w);
   %that is now the multiple of row i that must be added to row ii;
           for col:=i:berl!-m!-size do
             putv(getv(berl!-m,row),col,
               modular!-plus(getv(getv(berl!-m,row),col),
               modular!-times(r,getv(getv(berl!-m,i),col)))) >>
         end;
    for col:=i:berl!-m!-size do
        putv(getv(berl!-m,i),col,
           modular!-times(getv(getv(berl!-m,i),col),w));
    return null!-space!-basis
  end;


symbolic procedure tidy!-up!-null!-vectors(null!-space!-basis,
                    berl!-m,berl!-m!-size);
  begin
    scalar row!-to!-use;
    row!-to!-use:=berl!-m!-size#+1;
    null!-space!-basis:=
      for each null!-vector in null!-space!-basis collect
        build!-null!-vector(null!-vector,
            getv(berl!-m,row!-to!-use:=row!-to!-use#-1),berl!-m);
    berl!-m:=nil; % Release the store for full matrix;
%    prin2 "Null vectors: ";
%    print null!-space!-basis;
    return null!-space!-basis
  end;

symbolic procedure build!-null!-vector(n,vec1,berl!-m);
% At the end of the elimination process (the CLEAR-COLUMN loop)
% certain columns, indicated by the entries in NULL-SPACE-BASIS
% will be null vectors, save for the fact that they need a '1'
% inserted on the diagonal of the matrix. This procedure copies
% these null-vectors into some of the vectors that represented
% rows of the Berlekamp matrix;
  begin
%   putv(vec1,0,0); % Not used later!!;
    for i:=1:n#-1 do
      putv(vec1,i,getv(getv(berl!-m,i),n));
    putv(vec1,n,1);
%   for i:=n#+1:berl!-m!-size do
%     putv(vec1,i,0);
    return vec1 . n
  end;



%**********************************************************************;
% Berlekamp's algorithm part 2: retrieving the factors mod p;


symbolic procedure get!-factors!-mod!-p(n,p);
% given the modular info (for the nth image) generated by the
% previous half of Berlekamp's method we can reconstruct the
% actual factors mod p;
  begin scalar nth!-modular!-info,old!-m;
    nth!-modular!-info:=getv(modular!-info,n);
    old!-m:=set!-modulus p;
%   wtime:=time();
    putv(modular!-info,n,
      convert!-null!-vectors!-to!-factors nth!-modular!-info);
%   trace!-time display!-time("Factors constructed in ",time()-wtime);
    set!-modulus old!-m
  end;

symbolic procedure convert!-null!-vectors!-to!-factors m!-info;
% Using the null space found, complete the job
% of finding modular factors by taking gcd's of the
% modular input polynomial and variants on the
% null space generators;
  begin
    scalar number!-needed,factors,
      work!-vector1,dwork1,work!-vector2,dwork2;
    known!-factors:=nil;
%   wtime:=time();
    find!-linear!-factors!-mod!-p(cdar m!-info,caar m!-info);
%   trace!-time display!-time("Linear factors found in ",time()-wtime);
    dpoly:=caadr m!-info;
    poly!-vector:=cdadr m!-info;
    null!-space!-basis:=caddr m!-info;
    if dpoly=0 then return known!-factors; % All factors were linear;
    if null null!-space!-basis then
      return known!-factors:=
          vector!-to!-poly(poly!-vector,dpoly,m!-image!-variable) .
            known!-factors;
    number!-needed:=length null!-space!-basis;
% count showing how many more factors I need to find;
    work!-vector1:=mkvect dpoly;
    work!-vector2:=mkvect dpoly;
    factors:=list (poly!-vector . dpoly);
try!-next!-null:
    if null!-space!-basis=nil then
      errorf "RAN OUT OF NULL VECTORS TOO EARLY";
%   wtime:=time();
    factors:=try!-all!-constants(factors,
        caar null!-space!-basis,cdar null!-space!-basis);
%   trace!-time display!-time("All constants tried in ",time()-wtime);
    if number!-needed=0 then
       return known!-factors:=append!-new!-factors(factors,
            known!-factors);
    null!-space!-basis:=cdr null!-space!-basis;
    go to try!-next!-null
  end;


symbolic procedure try!-all!-constants(list!-of!-polys,v,dv);
% use gcd's of v, v+1, v+2, ... to try to split up the
% polynomials in the given list;
  begin
    scalar a,b,aa,s;
% aa is a list of factors that can not be improved using this v,
% b is a list that might be;
    aa:=nil; b:=list!-of!-polys;
    s:=0;
try!-next!-constant:
    putv(v,0,s); % Fix constant term of V to be S;
%    wtime:=time();
    a:=split!-further(b,v,dv);
%    trace!-time display!-time("Polys split further in ",time()-wtime);
    b:=cdr a; a:=car a;
    aa:=nconc(a,aa);
% Keep aa up to date as a list of polynomials that this poly
% v can not help further with;
    if b=nil then return aa; % no more progress possible here;
    if number!-needed=0 then return nconc(b,aa);
      % no more progress needed;
    s:=s#+1;
    if s#<current!-modulus then go to try!-next!-constant;
% Here I have run out of choices for the constant
% coefficient in v without splitting everything;
    return nconc(b,aa)
  end;

symbolic procedure split!-further(list!-of!-polys,v,dv);
% list-of-polys is a list of polynomials. try to split
% its members further by taking gcd's with the polynomial
% v. return (a . b) where the polys in a can not possibly
% be split using v+constant, but the polys in b might
% be;
    if null list!-of!-polys then nil . nil
    else begin
      scalar a,b,gg,q;
      a:=split!-further(cdr list!-of!-polys,v,dv);
      b:=cdr a; a:=car a;
      if number!-needed=0 then go to no!-split;
      % if all required factors have been found there is no need to
      % search further;
      dwork1:=copy!-vector(v,dv,work!-vector1);
      dwork2:=copy!-vector(caar list!-of!-polys,cdar list!-of!-polys,
        work!-vector2);
      dwork1:=gcd!-in!-vector(work!-vector1,dwork1,
         work!-vector2,dwork2);
      if dwork1=0 or dwork1=cdar list!-of!-polys then go to no!-split;
      dwork2:=copy!-vector(caar list!-of!-polys,cdar list!-of!-polys,
        work!-vector2);
      dwork2:=quotfail!-in!-vector(work!-vector2,dwork2,
        work!-vector1,dwork1);
% Here I have a splitting;
      gg:=mkvect dwork1;
      copy!-vector(work!-vector1,dwork1,gg);
      a:=((gg . dwork1) . a);
      copy!-vector(work!-vector2,dwork2,q:=mkvect dwork2);
      b:=((q . dwork2) . b);
      number!-needed:=number!-needed#-1;
      return (a . b);
   no!-split:
      return (a . ((car list!-of!-polys) . b))
    end;

symbolic procedure append!-new!-factors(a,b);
% Convert to REDUCE (rather than vector) form;
    if null a then b
    else
      vector!-to!-poly(caar a,cdar a,m!-image!-variable) .
        append!-new!-factors(cdr a,b);



carcheck safe!-flag; % Restore status quo.

endmodule;


end;
