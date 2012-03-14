module pfacmult; % multivariate modular factorization.

% Author: Herbert Melenk.

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


% Reduction of multivariate modular factorization to univariate
% factorization by Kroneckers map.
% See Kaltofen: Factorization of Polynomials, in: Buchberger,
% Collins, Loos: Computer Algebra, Springer, 1982.

% This module should be removed as soon as a multivariate modular
% factorizer based on Hensel lifting has been written.

fluid '(!*trfac);

symbolic procedure fctrfkronm f;
  begin scalar sub,tra,k,x,xx,x0,y,z,r,q,f0,fl,fs,dmode!*;
       integer d,d0;
    k:=kernels f;
    dmode!*:='!:mod!:;
    for each z in decomposedegr(f,for each x in k collect (x. 0))
         do if cdr z >d then d:=cdr z;
    d:=d+1; d0:=d; x0:=car k;
    for each x in cdr k do
    <<sub:=(x . {'expt,x0,d0}).sub; tra:=(x.d0).tra; d0:=d0*d>>;
    fs:=numr subf(f,sub);
    if !*trfac then
     <<writepri("Kronecker mapped form:",'first);
       writepri(mkquote prepf fs,'last)>>;
    fl:=decomposefctrf fs;
    if null cdr fl then return {1,f.1};
    f0:=numr resimp (f ./ 1);
    for each fc in fl do if not domainp f0 then
    <<y:=fctrfmk1(fc,tra);
      y:=numr resimp(y ./ 1);
      x := fctrfmk3 y;
      if x then y:= quotf(y, x);
      if !*trfac then
      <<writepri("test next candidate ",'first);
        writepri(mkquote prepf y,'last)>>;
     if (q:=quotf(f0,y)) then
        <<f0:=q; if(z:=assoc(y,r)) then cdr z:=cdr z+1
                   else r:=(y. 1).r>>>>;
    if null r then return {1,f. 1};
    if domainp f0 then return (f0 .r);
    if !*trfac then
    <<writepri("descend in recursion with",'only);
      writepri(mkquote prepf f0, 'only)>>;
    fl := fctrfkronm f0;
    if !*trfac then
    <<writepri("return from recursion; numeric factor ",'first);
      writepri(mkquote prepf car fl, 'last);
      for each fc in cdr fl do
      <<writepri("polynomial factor: ",'first);
        writepri(mkquote prepf car fc, nil);
        writepri(" multiplicity ", nil);
        writepri(mkquote prepf cdr fc, 'last)>> >>;
    x := car fl; xx := cdr fl;
    if null cdr xx and cdar xx = 1 and fctrfmk4 x then
    <<y := fctrfmk3 car xx;
      if y then
      <<x := y;
        xx := list(quotf(caar xx, x) . 1);
        if !*trfac then
        <<writepri("number correction; numeric factor ",'first);
          writepri(mkquote x,'last);
          writepri("polynomial factor ",'first);
          writepri(mkquote prepf caar xx,'last)>> >> >>;
    for each fc in xx do
    <<y:=numr resimp(car fc ./ 1);
      if !*trfac then
      <<writepri("next division: ",'first);
        writepri(mkquote prepf y,'last)>>;
        f0:=quotf(f0,y);
        if(z:=assoc(y,r)) then cdr z:=cdr z+cdr fc
                   else r:=(y. cdr fc).r>>;
    x := quotf(x, f0);
    return x . r
  end;

symbolic procedure fctrfmk1(f,tra);
  % Kronecker backtransform.
   if domainp f then f else
      addf(multf(lc f,fctrfmk2(mvar f,ldeg f,tra)),fctrfmk1(red f,tra));

symbolic procedure fctrfmk2(x,n,tra);
  if n=0 then 1 else
  if null tra then x.**n .* 1 .+ nil else
  if n>=cdar tra then multf(caar tra .** (n/cdar tra) .* 1 .+nil,
            fctrfmk2(x,remainder(n,cdar tra),cdr tra))
   else fctrfmk2(x,n,cdr tra);

symbolic procedure fctrfmk3 f;
% Extract the leading coefficient.
  if domainp f then (if fctrfmk4 f then nil else f) else fctrfmk3 lc f;

symbolic procedure fctrfmk4 u;
% Test u=1 in modular mode;
    numberp u and u = 1 or
      not atom u and car u = '!:mod!: and modonep!: u;

endmodule;

end;
