module facstr;   % Reconstruction of factors.

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

fluid '(!*trfac
        alphavec
        bad!-case
        best!-known!-factors
        best!-set!-pointer
        current!-modulus
        degree!-bounds
        factor!-level
        factor!-trace!-list
        fhatvec
        full!-gcd
        hensel!-growth!-size
        image!-factors
        irreducible
        m!-image!-variable
        multivariate!-factors
        multivariate!-input!-poly
        non!-monic
        number!-of!-factors
        predictions
        prime!-base
        reconstructing!-gcd
        target!-factor!-count
        valid!-image!-sets);

symbolic procedure reconstruct!-multivariate!-factors vset!-mod!-p;
   % Hensel construction for multivariate case.
   % Full univariate split has already been prepared (if factoring),
   % but we only need the modular factors and the true leading coeffts.
  (lambda factor!-level; begin
    scalar s,om,u0,alphavec,predictions,
      best!-factors!-mod!-p,fhatvec,w1,fvec!-mod!-p,d,degree!-bounds,
      lc!-vec;
    alphavec:=mkvect number!-of!-factors;
    best!-factors!-mod!-p:=mkvect number!-of!-factors;
    lc!-vec := mkvect number!-of!-factors;
        % This will preserve the LCs of the factors while we are working
        % mod p since they may contain numbers that are bigger than the
        % modulus.
    if not(
      (d:=max!-degree(multivariate!-input!-poly,0)) < prime!-base) then
      fvec!-mod!-p:=choose!-larger!-prime d;
    om:=set!-modulus hensel!-growth!-size;
    if null fvec!-mod!-p then <<
      fvec!-mod!-p:=mkvect number!-of!-factors;
      for i:=1:number!-of!-factors do
        putv(fvec!-mod!-p,i,reduce!-mod!-p getv(image!-factors,i))>>;
    for i:=1:number!-of!-factors do <<
      putv(alphavec,i,cdr get!-alpha getv(fvec!-mod!-p,i));
      putv(best!-factors!-mod!-p,i,
        reduce!-mod!-p getv(best!-known!-factors,i));
      putv(lc!-vec,i,lc getv(best!-known!-factors,i))>>;
         % Set up the Alphas, input factors mod p and remember to save
         % the LCs for use after finding the multivariate factors mod p.
    if not reconstructing!-gcd then <<
      s:=getv(valid!-image!-sets,best!-set!-pointer);
      vset!-mod!-p:=for each v in get!-image!-set s collect
        (car v . modular!-number cdr v)>>;
%    princ "kord* =";% print kord!*;
%    princ "order of variable substitution=";% print vset!-mod!-p;
    u0:=reduce!-mod!-p multivariate!-input!-poly;
    s := 1;
    for i := 1:number!-of!-factors do
       s := multf(s,getv(best!-known!-factors,i));
    set!-degree!-bounds(vset!-mod!-p,multivariate!-input!-poly,s);
%   wtime:=time();
    factor!-trace <<
      printstr
         "We use the Hensel Construction to grow univariate modular";
      printstr
         "factors into multivariate modular factors, which will in";
      printstr "turn be used in the later Hensel construction.  The";
      printstr "starting modular factors are:";
      printvec(" f(",number!-of!-factors,")=",best!-factors!-mod!-p);
      prin2!* "The modulus is "; printstr current!-modulus >>;
    find!-multivariate!-factors!-mod!-p(u0,
      best!-factors!-mod!-p,
      vset!-mod!-p);
    if bad!-case then <<
%     trace!-time <<
%       display!-time(" Multivariate modular factors failed in ",
%         time()-wtime);
%       wtime:=time()>>;
      target!-factor!-count:=number!-of!-factors - 1;
      if target!-factor!-count=1 then irreducible:=t;
      set!-modulus om;
      return bad!-case>>;
%   trace!-time <<
%     display!-time(" Multivariate modular factors found in ",
%       time()-wtime);
%     wtime:=time()>>;
    fhatvec:=make!-multivariate!-hatvec!-mod!-p(best!-factors!-mod!-p,
      number!-of!-factors);
    for i:=1:number!-of!-factors do
      putv(fvec!-mod!-p,i,getv(best!-factors!-mod!-p,i));
    make!-vec!-modular!-symmetric(best!-factors!-mod!-p,
      number!-of!-factors);
    for i:=1:number!-of!-factors do <<
%      w1:=getv(coefft!-vectors,i);
%      putv(best!-known!-factors,i,
%        merge!-terms(getv(best!-factors!-mod!-p,i),w1));
      putv(best!-known!-factors,i,
        force!-lc(getv(best!-factors!-mod!-p,i),getv(lc!-vec,i)));
         % Now we put back the LCs before growing the multivariate
         % factors to be correct over the integers giving the final
         % result.
      >>;
%   wtime:=time();
    w1:=hensel!-mod!-p(
      multivariate!-input!-poly,
      fvec!-mod!-p,
      best!-known!-factors,
      get!.coefft!.bound(multivariate!-input!-poly,
        total!-degree!-in!-powers(multivariate!-input!-poly,nil)),
      vset!-mod!-p,
      hensel!-growth!-size);
    if car w1='overshot then <<
%     trace!-time <<
%       display!-time(" Full factors failed in ",time()-wtime);
%       wtime:=time() >>;
      target!-factor!-count:=number!-of!-factors - 1;
      if target!-factor!-count=1 then irreducible:=t;
      set!-modulus om;
      return bad!-case:=t >>;
    if not(car w1='ok) then errorf w1;
%   trace!-time <<
%     display!-time(" Full factors found in ",time()-wtime);
%     wtime:=time() >>;
    if reconstructing!-gcd then <<
      full!-gcd:=if non!-monic then car primitive!.parts(
          list getv(cdr w1,1),m!-image!-variable,nil)
        else getv(cdr w1,1);
      set!-modulus om;
      return full!-gcd >>;
    for i:=1:getv(cdr w1,0) do
      multivariate!-factors:=getv(cdr w1,i) . multivariate!-factors;
    if non!-monic then multivariate!-factors:=
      primitive!.parts(multivariate!-factors,m!-image!-variable,nil);
    factor!-trace <<
      printstr "The full multivariate factors are:";
      for each x in multivariate!-factors do printsf x >>;
    set!-modulus om;
  end) (factor!-level*100);

endmodule;

end;
