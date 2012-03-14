module coeffts;

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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
        best!-known!-factor!-list
        best!-known!-factors
        coefft!-vectors
        deg!-of!-unknown
        difference!-for!-unknown
        divisor!-for!-unknown
        factor!-level
        factor!-trace!-list
        full!-gcd
        hensel!-growth!-size
        image!-factors
        m!-image!-variable
        multivariate!-factors
        multivariate!-input!-poly
        non!-monic
        number!-of!-factors
        polyzero
        reconstructing!-gcd
        true!-leading!-coeffts
        unknown
        unknowns!-list);


%**********************************************************************;
%  Code for trying to determine more multivariate coefficients
%  by inspection before using multivariate hensel construction.


symbolic procedure determine!-more!-coeffts();
% ...
  begin scalar unknowns!-list,uv,r,w,best!-known!-factor!-list;
    best!-known!-factors:=mkvect number!-of!-factors;
    uv:=mkvect number!-of!-factors;
    for i:=number!-of!-factors step -1 until 1 do
      putv(uv,i,convert!-factor!-to!-termvector(
        getv(image!-factors,i),getv(true!-leading!-coeffts,i)));
    r:=red multivariate!-input!-poly;
            % we know all about the leading coeffts;
    if not depends!-on!-var(r,m!-image!-variable)
      or null(w:=try!-first!-coefft(
              ldeg r,lc r,unknowns!-list,uv)) then <<
      for i:=1:number!-of!-factors do
        putv(best!-known!-factors,i,force!-lc(
          getv(image!-factors,i),getv(true!-leading!-coeffts,i)));
      coefft!-vectors:=uv;
      return nil >>;
    factor!-trace <<
      printstr
         "By exploiting any sparsity wrt the main variable in the";
      printstr "factors, we can try guessing some of the multivariate";
      printstr "coefficients." >>;
    try!-other!-coeffts(r,unknowns!-list,uv);
    w:=convert!-and!-trial!-divide uv;
%   trace!-time
%     if full!-gcd then prin2t "Possible gcd found"
%     else prin2t "Have found some coefficients";
    return set!-up!-globals(uv,w)
  end;

symbolic procedure convert!-factor!-to!-termvector(u,tlc);
% ...
  begin scalar termlist,res,n,slist;
    termlist:=(ldeg u . tlc) . list!-terms!-in!-factor red u;
    res:=mkvect (n:=length termlist);
    for i:=1:n do <<
      slist:=(caar termlist . i) . slist;
      putv(res,i,car termlist);
      termlist:=cdr termlist >>;
    putv(res,0,(n . (n #- 1)));
    unknowns!-list:=(reversip slist) . unknowns!-list;
    return res
  end;

symbolic procedure try!-first!-coefft(n,c,slist,uv);
% ...
  begin scalar combns,unknown,w,l,d,v,m;
    combns:=get!-term(n,slist);
    if (combns='no) or not null cdr combns then return nil;
    l:=car combns;
    for i:=1:number!-of!-factors do <<
      w:=getv(getv(uv,i),car l);    % degree . coefft ;
      if null cdr w then <<
         if unknown then <<c := nil; i := number!-of!-factors + 1>>
          else <<unknown := i . car l; d := car w>>>>
      else <<
        c:=quotf(c,cdr w);
        if didntgo c then i := number!-of!-factors+1>>;
      l:=cdr l >>;
    if didntgo c then return nil;
    putv(v:=getv(uv,car unknown),cdr unknown,(d . c));
    m:=getv(v,0);
    putv(v,0,(car m . (cdr m #- 1)));
    if cdr m = 1 and factors!-complete uv then return 'complete;
    return c
  end;

symbolic procedure solve!-next!-coefft(n,c,slist,uv);
% ...
  begin scalar combns,w,unknown,deg!-of!-unknown,divisor!-for!-unknown,
    difference!-for!-unknown,v;
    difference!-for!-unknown:=polyzero;
    divisor!-for!-unknown:=polyzero;
    combns:=get!-term(n,slist);
    if combns='no then return 'nogood;
    while combns do <<
      w:=split!-term!-list(car combns,uv);
      if w='nogood then combns := nil else combns:=cdr combns >>;
    if w='nogood then return w;
    if null unknown then return;
    w:=quotf(addf(c,negf difference!-for!-unknown),
             divisor!-for!-unknown);
    if didntgo w then return 'nogood;
    putv(v:=getv(uv,car unknown),cdr unknown,(deg!-of!-unknown . w));
    n:=getv(v,0);
    putv(v,0,(car n . (cdr n #- 1)));
    if cdr n = 1 and factors!-complete uv then return 'complete;
    return w
  end;

symbolic procedure split!-term!-list(term!-combn,uv);
% ...
  begin scalar a,v,w;
    a:=1;
    for i:=1:number!-of!-factors do <<
      w:=getv(getv(uv,i),car term!-combn);  % degree . coefft ;
      if null cdr w then
        if v or (unknown and not((i.car term!-combn)=unknown)) then
          <<v:='nogood; i := number!-of!-factors+1>>
        else <<
          unknown:=(i . car term!-combn);
          deg!-of!-unknown:=car w;
          v:=unknown >>
      else a:=multf(a,cdr w);
      if not(v eq 'nogood) then term!-combn:=cdr term!-combn >>;
    if v='nogood then return v;
    if v then divisor!-for!-unknown:=addf(divisor!-for!-unknown,a)
    else difference!-for!-unknown:=addf(difference!-for!-unknown,a);
    return 'ok
  end;

symbolic procedure factors!-complete uv;
% ...
  begin scalar factor!-not!-done,r;
    r:=t;
    for i:=1:number!-of!-factors do
      if not(cdr getv(getv(uv,i),0)=0) then
        if factor!-not!-done then <<r:=nil; i:=number!-of!-factors+1>>
        else factor!-not!-done:=t;
    return r
  end;

symbolic procedure convert!-and!-trial!-divide uv;
% ...
  begin scalar w,r,fdone!-product!-mod!-p,om;
    om:=set!-modulus hensel!-growth!-size;
    fdone!-product!-mod!-p:=1;
    for i:=1:number!-of!-factors do <<
      w:=getv(uv,i);
      w:= if (cdr getv(w,0))=0 then termvector2sf w
        else merge!-terms(getv(image!-factors,i),w);
      r:=quotf(multivariate!-input!-poly,w);
      if didntgo r then best!-known!-factor!-list:=
        ((i . w) . best!-known!-factor!-list)
      else if reconstructing!-gcd and i=1
       then <<full!-gcd:=if non!-monic then car primitive!.parts(
          list w,m!-image!-variable,nil) else w;
          i := number!-of!-factors+1>>
      else <<
        multivariate!-factors:=w . multivariate!-factors;
        fdone!-product!-mod!-p:=times!-mod!-p(
          reduce!-mod!-p getv(image!-factors,i),
          fdone!-product!-mod!-p);
        multivariate!-input!-poly:=r >> >>;
    if full!-gcd then return;
    if null best!-known!-factor!-list then multivariate!-factors:=
      primitive!.parts(multivariate!-factors,m!-image!-variable,nil)
    else if null cdr best!-known!-factor!-list then <<
      if reconstructing!-gcd then
        if not(caar best!-known!-factor!-list=1) then
          errorf("gcd is jiggered in determining other coeffts")
        else full!-gcd:=if non!-monic then car primitive!.parts(
          list multivariate!-input!-poly,
          m!-image!-variable,nil)
          else multivariate!-input!-poly
      else multivariate!-factors:=primitive!.parts(
        multivariate!-input!-poly . multivariate!-factors,
        m!-image!-variable,nil);
      best!-known!-factor!-list:=nil >>;
    factor!-trace <<
      if null best!-known!-factor!-list then
        printstr
           "We have completely determined all the factors this way"
      else if multivariate!-factors then <<
        prin2!* "We have completely determined the following factor";
        printstr if (length multivariate!-factors)=1 then ":" else "s:";
        for each ww in multivariate!-factors do printsf ww >> >>;
    set!-modulus om;
    return fdone!-product!-mod!-p
  end;

symbolic procedure set!-up!-globals(uv,f!-product);
  if null best!-known!-factor!-list or full!-gcd then 'done
  else begin scalar i,r,n,k,flist!-mod!-p,imf,om,savek;
    n:=length best!-known!-factor!-list;
    best!-known!-factors:=mkvect n;
    coefft!-vectors:=mkvect n;
    r:=mkvect n;
    k:=if reconstructing!-gcd then 1 else 0;
    om:=set!-modulus hensel!-growth!-size;
    for each w in best!-known!-factor!-list do <<
      i:=car w; w:=cdr w;
      if reconstructing!-gcd and i=1 then << savek:=k; k:=1 >>
      else k:=k #+ 1;
            % in case we are reconstructing gcd we had better know
            % which is the gcd and which the cofactor - so don't move
            % move the gcd from elt one;
      putv(r,k,imf:=getv(image!-factors,i));
      flist!-mod!-p:=(reduce!-mod!-p imf) . flist!-mod!-p;
      putv(best!-known!-factors,k,w);
      putv(coefft!-vectors,k,getv(uv,i));
      if reconstructing!-gcd and k=1 then k:=savek;
            % restore k if necessary;
      >>;
    if not(n=number!-of!-factors) then <<
      alphalist:=for each modf in flist!-mod!-p collect
        (modf . remainder!-mod!-p(times!-mod!-p(f!-product,
          cdr get!-alpha modf),modf));
      number!-of!-factors:=n >>;
    set!-modulus om;
    image!-factors:=r;
    return 'need! to! reconstruct
  end;

symbolic procedure get!-term(n,l);
% ...
  if n#<0 then 'no
  else if null cdr l then get!-term!-n(n,car l)
  else begin scalar w,res;
    for each fterm in car l do <<
      w:=get!-term(n#-car fterm,cdr l);
      if not(w='no) then res:=
        append(for each v in w collect (cdr fterm . v),res) >>;
    return if null res then 'no else res
  end;

symbolic procedure get!-term!-n(n,u);
  if null u or n #> caar u then 'no
  else if caar u = n then list(cdar u . nil)
  else get!-term!-n(n,cdr u);

endmodule;

end;
