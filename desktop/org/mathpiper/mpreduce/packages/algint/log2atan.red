module log2atan;

% Author: James H. Davenport.

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


% Modified by:  Anthony C. Hearn.

fluid '(!*rationalize !*tra gaussiani !*trmin intvar);

exports combine!-logs;

symbolic procedure combine!-logs(coef,logarg);
% Attempts to produce a "simple" form for COEF*(LOGARG).  COEF is
% prefix, LOGARG an SQ (and already log'ged for historical reasons).
begin
  scalar ans,dencoef,parts,logs,lparts,!*rationalize,trueimag;
  !*rationalize:=t;    % A first attempt to use this technology.
  coef:=simp!* coef;
  if null numr logarg then return coef;
  parts:=split!-real!-imag numr coef;
  if null numr cdr parts then return multsq(coef,logarg);
  % Integrand was, or seemed to be, purely real.
  dencoef:=multf(denr coef,denr logarg);
  if !*tra then <<
     printc "attempting to find 'real' form for";
     mathprint list('times,list('plus,prepsq car parts,
                                      list('times,prepsq cdr parts,'i)),
                           prepsq logarg) >>;
  logarg:=numr logarg;
  logs:= 1 ./ 1;
  while pairp logarg do <<
        if ldeg logarg neq 1 then interr "what a log";
        if atom mvar logarg then interr "what a log";
        if car mvar logarg neq 'log then interr "what a log";
        logs:=!*multsq(logs,
                       !*exptsq(simp!* argof mvar logarg,lc logarg));
        logarg:=red logarg >>;
  logs:=rationalizesq logs;
  ans:=multsq(!*multsq(car parts,logs),1 ./ dencoef); % real part
  % Now to apply theory i*log(a+i*b) = atan(a/b) + (i/2 log (a^2+b^2))
  lparts:=split!-real!-imag numr logs;
  if numr difff(denr cdr lparts,intvar)
    then interr "unexpected denominator"
   else if null numr cdr lparts then return multsq(coef,logarg);
% The previous test arose from
%   combine!-logs('(times (sqrt (plus a (minus 1))) i),
%                 '(((((log (plus (sqrt (plus (expt x 2) 1))
%                  (minus x))) . 1) . 1) (((log (plus (sqrt (plus
%                  (expt x 2) 1)) x)) . 1) . -1)) . 2)
% from int((-sqrt(a-1)*sqrt(x**2+1)*i-x)/(x**2+1),x).
  lparts:=!*multsq(denr cdr lparts ./ 1,car lparts) . cdr lparts;
  if not onep denr car lparts then interr "unexpected denominator";
  % We have discarded the logarithm of a constant: good riddance
  trueimag:=quotsq(addf(!*exptf(numr car lparts,2),
                        !*exptf(numr cdr lparts,2)) ./ 1,
                   !*exptf(denr logs,2) ./ 1);
  if numr diffsq(trueimag,intvar)
     then ans:=!*addsq(ans,
                     !*multsq(gaussiani ./ multf(2,dencoef),
                              !*multsq(simplogsq trueimag,cdr parts)));
  trueimag:=!*multsq(car lparts,!*invsq(numr cdr lparts ./ 1));
  if numr diffsq(trueimag,intvar)
     then ans:=!*addsq(ans,!*multsq(!*multsq(cdr parts,1 ./ dencoef),
                                  !*k2q list('atan,prepsq!* trueimag)));
  return ans;
  end;

symbolic procedure split!-real!-imag sf;
   % Returns coef real.imag as SQs.
 if null sf then (lambda z; z . z) (nil ./ 1)
  else if numberp sf then (sf ./ 1) . (nil ./ 1)
  else if domainp sf then interr "can't handle arbitrary domains"
  else begin
    scalar cparts,rparts,mv,tmp;
    cparts:=split!-real!-imag lc sf;
    rparts:=split!-real!-imag red sf;
    mv:=split!-real!-imagvar mvar sf;
    if null numr cdr mv % main variable totally real
       then <<
          tmp:= lpow sf .* 1 .+ nil ./ 1;
          return !*addsq(!*multsq(car cparts,tmp),car rparts) .
                 !*addsq(!*multsq(cdr cparts,tmp),cdr rparts) >>;
    if null numr car mv then <<
       mv:=!*exptsq(cdr mv,ldeg sf);
       % deal with powers of i
       if not evenp(ldeg sf / 2) then mv:=negsq mv;
       if not evenp ldeg sf
         then return !*addsq(!*multsq(negsq cdr cparts,mv),car rparts) .
                     !*addsq(!*multsq(car cparts,mv),cdr rparts)
         else return !*addsq(!*multsq(car cparts,mv),car rparts) .
                     !*addsq(!*multsq(cdr cparts,mv),cdr rparts) >>;
    % Now we have to handle the general case.
    cparts:=mul!-complex(cparts,exp!-complex(mv,ldeg sf));
    return !*addsq(car cparts,car rparts) .
                   !*addsq(cdr cparts, cdr rparts)
    end;

symbolic procedure mul!-complex(a,b);
!*addsq(!*multsq(negsq cdr a,cdr b),!*multsq(car a,car b)) .
!*addsq(!*multsq(car a,cdr b),!*multsq(cdr a,car b));

symbolic procedure exp!-complex(a,n);
if n=1 then a
   else if evenp n then exp!-complex(mul!-complex(a,a),n/2)
   else mul!-complex(a,exp!-complex(mul!-complex(a,a),n/2));

symbolic procedure split!-real!-imagvar mv;
   % Returns a pair of sf.
 if mv eq 'i then (nil ./ 1) . (1 ./ 1)
   else if atom mv then (mv .** 1 .* 1 .+ nil ./ 1) . (nil ./ 1)
   else if car mv eq 'sqrt then begin
             scalar n,rp,innersqrt,c;
             n:=simp!* argof mv;
             if denr n neq 1 then interr "unexpected denominator";
             rp:=split!-real!-imag numr n;
             if null numr cdr rp and minusf numr car rp and
                null involvesf(numr car rp,intvar) then
                return (nil ./ 1) . simpsqrtsq negsq car rp;
             if null numr cdr rp
                then return (mv .** 1 .* 1 .+ nil ./ 1) . (nil ./ 1);
                            % totally real.
   % OK - it's a general (ish) complex number A+iB
   % its square root is going to be C+iD where
   % C^2 = (A+sqrt(A^2+B^2))/2 (+ve sign of sqrt to make C +ve)
   % C is square root of this
   % D is C * (sqrt(A(2+B^2) -A)/B
   % Note that D has a non-trivial denominator. We could avoid this at
   % the cost of generating non-independent square roots (yuck).
   % Note that the above checks have ensured this den. is non-zero.
              if numr car rp then
                 innersqrt:=simpsqrtsq !*addsq(!*exptsq(car rp,2),
                                               !*exptsq(cdr rp,2))
                 else innersqrt:=cdr rp; % pure imaginary case
              c:=simpsqrtsq multsq(!*addsq(car rp, innersqrt), 1 ./ 2);
              return c . !*multsq(!*multsq(c,!*invsq cdr rp),
                                  !*addsq(innersqrt,negsq car rp));
        end
   else (mv .** 1 .* 1 .+ nil ./ 1) . (nil ./ 1);
        % What the heck: pretend it's real.

endmodule;

end;
