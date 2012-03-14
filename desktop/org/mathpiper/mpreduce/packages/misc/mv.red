module mv; % Operations on multivariate forms.

% Author: Anthony C. Hearn.

% Copyright (c) 1989 The RAND Corporation.  All Rights Reserved.

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


% These smacros are local to this module.

symbolic smacro procedure mv!-term!-coeff u; cdr u;

symbolic smacro procedure mv!-term!-pow u; car u;

symbolic smacro procedure mv!-tpow u; car u;

symbolic smacro procedure mv!-tc u; cdr u;

symbolic procedure mv!-!+(u,v);
   if null u then v
    else if null v then u
    else if mv!-lpow u= mv!-lpow v
     then (lambda x;
            if x=0 then mv!-!+(mv!-red u,mv!-red v)
             else mv!-!.!+(mv!-!.!*(mv!-lpow u,x),
                           mv!-!+(mv!-red u,mv!-red v)))
          (mv!-lc u + mv!-lc v)
    else if mv!-pow!-!>(mv!-lpow u,mv!-lpow v)
     then mv!-!.!+(mv!-lt u,mv!-!+(mv!-red u,v))
    else mv!-!.!+(mv!-lt v,mv!-!+(u,mv!-red v));

symbolic smacro procedure domain!-!*(u,v); u*v;

symbolic smacro procedure domain!-!/(u,v); u/v;

symbolic procedure mv!-term!-!*(u,v);
   % U is a (non-zero) term and v a multivariate form. Result is
   % product of u and v.
   if null v then nil
    else mv!-!.!+(mv!-!.!*(mv!-pow!-!+(mv!-tpow u,mv!-lpow v),
                           domain!-!*(mv!-tc u,mv!-lc v)),
                  mv!-term!-!*(u,mv!-red v));

symbolic procedure mv!-term!-!/(u,v);
   % Returns the result of the (exact) division of u by term v.
   if null u then nil
    else mv!-!.!+(mv!-!.!*(mv!-pow!-!-(mv!-lpow u,mv!-tpow v),
                           domain!-!/(mv!-lc u,mv!-tc v)),
                  mv!-term!-!/(mv!-red u,v));

symbolic procedure mv!-domainlist u;
   if null u then nil
    else mv!-lc u . mv!-domainlist mv!-red u;

symbolic procedure mv!-pow!-mv!-!+(u,v);
   if null v then nil
    else mv!-!.!+(mv!-pow!-mv!-term!-!+(u,mv!-lt v),
                  mv!-pow!-mv!-!+(u,mv!-red v));

symbolic procedure mv!-pow!-mv!-term!-!+(u,v);
   mv!-!.!*(mv!-pow!-!+(u,mv!-term!-pow v), mv!-term!-coeff v);

symbolic procedure mv!-pow!-!+(u,v);
   if null u then nil
    else (car u+car v) . mv!-pow!-!+(cdr u,cdr v);

symbolic procedure mv!-pow!-!-(u,v);
   if null u then nil
    else (car u-car v) . mv!-pow!-!-(cdr u,cdr v);

symbolic procedure mv!-pow!-!*(u,v);
   if null v then nil
    else (u*car v) . mv!-pow!-!*(u,cdr v);

symbolic procedure mv!-pow!-minusp u;
   if null u then nil
    else car u<0 or mv!-pow!-minusp cdr u;

symbolic procedure mv!-pow!-!>(u,v);
   if null u then nil
    else if car u=car v then mv!-pow!-!>(cdr u,cdr v)
    else car u>car v;

symbolic procedure mv!-reduced!-coeffs u;
   % reduce coefficients of u to lowest terms.
   begin scalar x,y;
      x := mv!-lc u;
      y := mv!-red u;
      while y and x neq 1 do <<x := gcdn(x,mv!-lc y); y := mv!-red y>>;
      return if x=1 then u else mv!-!/(u,x)
   end;

symbolic procedure mv!-!/(u,v);
   if null u then nil
    else mv!-!.!+(mv!-!.!*(mv!-lpow u,mv!-lc u/v),mv!-!/(mv!-red u,v));


% Functions that convert between standard forms and multivariate forms.

symbolic procedure sf2mv(u,varlist);
   % Converts the standard form u to a multivariate form wrt varlist.
   sf2mv1(u,nil,varlist);

symbolic procedure sf2mv1(u,powers,varlist);
   if null u then nil
    else if domainp u
     then list(append(powers,nzeros length varlist) . u)
    else if mvar u = car varlist     % This should be eq, but seems to
                                     % need equal.
     then append(sf2mv1(lc u,append(powers,list ldeg u),cdr varlist),
                 sf2mv1(red u,powers,varlist))
    else sf2mv1(u,append(powers,list 0),cdr varlist);

symbolic procedure nzeros n; if n=0 then nil else 0 . nzeros(n-1);

symbolic procedure mv2sf(u,varlist);
   % converts the multivariate form u to a standard form wrt varlist.
   % This version uses addf to fold terms - there is probably a more
   % direct method.
   if null u then nil
    else addf(mv2sf1(mv!-lpow u,cdar u,varlist),mv2sf(cdr u,varlist));

symbolic procedure mv2sf1(powers,cf,varlist);
   if null powers then cf
    else if car powers=0 then mv2sf1(cdr powers,cf,cdr varlist)
    else !*t2f((car varlist .** car powers)
                 .* mv2sf1(cdr powers,cf,cdr varlist));

endmodule;

end;
