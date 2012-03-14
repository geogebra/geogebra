module complx;
%                               Wed Dec. 17, 1986 by F. Kako;
%********************************************************************;

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


%******************************************************************
%*******        SPLIT REAL AND IMAGINARY PART    ******************
%******************************************************************


symbolic procedure real!-imag!-sq u;
%U is a standard quotient,
%Value is the standard quotient real part and imaginary part of U.
   begin scalar x,y;
      x := real!-imag!-f numr u;
      y := real!-imag!-f denr u;
      u := addf(multf(car y, car y),
                multf(cdr y, cdr y));         % Re Y **2 + Im Y **2;
      return (cancel(addf(multf(car x, car y),
                          multf(cdr x, cdr y))
                     ./ u) .
              cancel(addf(multf(car y, cdr x),
                          negf multf(car x, cdr y))
                     ./ u))
   end;

symbolic procedure real!-imag!-f u;
   %U is a standard form.
   %Value is the standard form real and imag part of U.
   begin scalar x;
      if domainp u then return u . nil;
      x := setkorder list 'i;
      u := reorder u;
      u := if mvar u eq 'i and ldeg u = 1 then red u . lc u
            else u . nil;
      setkorder x;
      return (reorder car u . reorder cdr u)
   end;

%*****************************************************************
%       hyperbolic functions
%*****************************************************************;

symbolic procedure real!-imag!-sincos u;
   begin scalar v,w,z;
      v := real!-imag!-sq u;
      if null cadr v then <<
                u := prepsq u;
                return simp!* list('sinh,u)
                        . simp!* list('cosh,u)>>
       else if null caar v then <<
                u := prepsq cdr v;
                return (multsq(!*k2q 'i, simp!* list('sin,u))
                         . simp!* list('cos,u))>>;
      u := prepsq cdr v;
      v := prepsq car v;
      w := simp!* list('cos,u);
      u := simp!* list('sin,u);
      u := multsq(!*k2q 'i,u);
      z := simp!* list('cosh,v);
      v := simp!* list('sinh,v);
      return (addsq (multsq(w, v), multsq(u,z)))
                . (addsq (multsq(w,z),multsq(u,v)))
   end;


% xxxxxxxxxxxxxxxxxxxxxxxx

%*********************************************************************
%       log and exponential term splitting for summation and product
%********************************************************************;


symbolic procedure sum!-split!-log(u,v);
   begin scalar x,y,z,lst,llst,mlst;
      lst := sum!-term!-split(u,v);
   a:
      if null lst then return (llst. mlst);
      u := car lst;
      lst := cdr lst;
      z := numr u;
      if domainp z or red z or not (tdeg (z := lt z) = 1) or
          atom tvar z or not ((car tvar z) eq 'log)
          or depend!-f(tc z,v) or depend!-f(denr u,v)
        then <<mlst := u . mlst;go to a>>;
      y := reorder tc z ./ reorder denr u;
      z := simp!* cadr tvar z;
      if x := assoc(y,llst) then rplacd(x,multsq(cdr x,z))
       else if x := assoc(negsq y,llst)
             then rplacd(x,multsq(cdr x,invsq z))
       else llst := (y . z) . llst;
      go to a
   end;


symbolic procedure prod!-split!-exp(u,v);
   begin scalar x,y,z,w,klst,lst;
%     lst := kernels(numr u,nil);
      lst := kernels numr u;
%     lst := kernels1denr u,lst);
      lst := kernels1(denr u,lst);
   a:
      if null lst then go to b;
      z := car lst;
      if not atom z and car z eq 'expt and
         not depend!-p(cadr z,v) and depend!-p(caddr z,v)
        then klst := z . klst;
      lst := cdr lst;
      go to a;
   b:
      if null klst then return (nil . list u);
      x := setkorder klst;
      z := reorder numr u;
      y := reorder denr u;
   c:
      if domainp z or red z or not memq(w := mvar z,klst)
       then go to d;
      v := multsq(tdeg lt z ./ 1,simp!* caddr w);
      w := cadr w;
      if u := assoc(w,lst) then rplacd(u,addsq(cdr u,v))
       else lst := (w . v) . lst;
      z := tc lt z;
      go to c;
   d:
      if domainp y or red y or not memq(w := mvar y,klst)
       then go to e;
      v := multsq(tdeg lt y ./ 1,negsq simp!* caddr w);
      w := cadr w;
      if u := assoc(w,lst) then rplacd(u,addsq(cdr u,v))
       else lst := (w . v) . lst;
      y := tc lt y;
      go to d;
   e:
      setkorder x;
      u := reorder z ./ reorder y;
      return (lst . list u)
   end;

endmodule;

end;
