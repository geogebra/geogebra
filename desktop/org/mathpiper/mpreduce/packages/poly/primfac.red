module primfac;   % Primitive square free polynomial factorization.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation. All rights reserved.

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


fluid '(!*intfac !*surds dmode!* intvar kernlist!* knowndiscrimsign);

symbolic procedure prsqfrfacf u;
   % U is a non-trivial form which is primitive in all its variables,
   % is square free, and has a positive leading numerical coefficient.
   % Result is a list of factors of u, the first a monomial.
   % We order kernels in increasing powers unless kernlist!* has a
   % non-NIL value in which case we use that order (needed by SOLVE).
   % NOTE: For the time being, we bypass this code if the coefficient
   % domain is other than integer.
   begin scalar bool,knowndiscrimsign,v,w;
      if dmode!* then return list(1,u);
      v := if intvar then list intvar           % Integration in effect.
            else if kernlist!* then kernlist!*
            else reverse kernord!-sort powers u;
      % order highest power first.
      % Note: if this procedure ever fails, the korder is then incorrect.
      w := setkorder v;
      u := reorder u;
      if minusf u then <<bool := t; u := negf u>>;
      u := factor!-ordered!-sqfree!-prim!-f u;
      setkorder w;
%     w := resimp car u;
      u := for each x in u collect
              begin
                 v := reorder x;
                 if bool and minusf v
                   then <<v := negf v; bool := nil>>;
                 return v
              end;
      if bool then u := negf car u . cdr u;
      % We couldn't fold the minus sign.
      return u
   end;

symbolic procedure factor!-ordered!-sqfree!-prim!-f pol;
   % U is a non-trivial form which is primitive in all its variables,
   % is square free, has a positive leading numerical coefficient,
   % and has a main variable of lowest degree in the form.
   % Result is a list of factors of u, the first a monomial.
   begin integer n; scalar q,res,w;
      if ldeg pol = 1 then return factor!-coeffs pol
       else if univariatep pol
        then <<while car(q := linfacf pol) do
                <<res := car q . res; pol := cdr q>>;
               while car(q := quadfacf pol) do
                <<res := car q . res; pol := cdr q>>>>;
      if null pol then return 1 . res
       else if length(w := special!-case!-factor pol)>2
        then <<res := car w . res;   % constant.
               for each j in cdr w
                  do res:=fac!-merge(factor!-ordered!-sqfree!-prim!-f j,
                                     res);
               return res>>
       else if ldeg pol < 4 or (n := degreegcd pol) = 1
          then return 1 . pol . res;
      w := cdr sort(dfactors n,function lessp);
      % 1 is always first factor.
      knowndiscrimsign := 'negative;
   a: if null w then <<knowndiscrimsign := nil;
                       return 1 . pol . res>>
       else if length (q := factor!-ordered!-sqfree!-prim!-f
                               downpower(pol,car w))>2
        then <<res := car q . res;
               for each j in cdr q
                  do res := fac!-merge(factor!-ordered!-sqfree!-prim!-f
                                        uppower(j,mvar pol,car w),
                                     res);
               knowndiscrimsign := nil;
               return res>>;
      w := cdr w;
      go to a
   end;

symbolic procedure downpower(pol,n);
    % Reduce the power of each term in pol wrt main variable by factor
    % n.
   downpower1(pol,mvar pol,n);

symbolic procedure downpower1(pol,mv,n);
   if domainp pol or not(mvar pol eq mv) then pol
    else (mv .** (ldeg pol/n)) .* lc pol .+ downpower1(red pol,mv,n);

symbolic procedure uppower(pol,var,n);
    % Raise the power of each term in pol wrt var by factor n.
   if mvar pol = var then uppower1(pol,var,n) else uppower2(pol,var,n);

symbolic procedure uppower1(pol,mv,n);
   if domainp pol or not(mvar pol eq mv) then pol
    else (mv .** (ldeg pol*n)) .* lc pol .+ uppower1(red pol,mv,n);

symbolic procedure uppower2(pol,var,n);
   if domainp pol then pol
    else if mvar pol = var
     then (mvar pol .** (ldeg pol*n)) .* lc pol
                .+ uppower2(red pol,var,n)
    else lpow pol .* uppower2(lc pol,var,n) .+ uppower2(red pol,var,n);

symbolic procedure univariatep pol;
   % True if pol is not a domain element and is univariate with respect
   % to its main variable.
   not domainp pol and univariatep1(pol,mvar pol);

symbolic procedure univariatep1(pol,mv);
   domainp pol
      or mvar pol eq mv and domainp lc pol and univariatep1(red pol,mv);

symbolic procedure special!-case!-factor pol;
   % When integrator calls this, it doesn't want to use the quadratic
   % code.
%  (if degree = 2 and (null !*surds or clogflag) then quadraticf pol
   (if degree = 2 and null !*intfac then quadraticf pol
     else if degree= 3 then cubicf pol
       else if degree = 4 then quarticf pol
       else list(1,pol))
    where degree = ldeg pol;

symbolic procedure degreegcd pol;
   % Returns gcd of degrees of pol with respect to main variable.
   begin integer n; scalar mv;
      mv := mvar pol;
      n := ldeg pol;
      while n>1 and not domainp(pol := red pol) and mvar pol eq mv
         do n := gcdn(n,ldeg pol);
      return n
   end;

symbolic procedure factor!-coeffs u;
   % factor the primitive, square free polynomial U wrt main variable.
   % dummy for now.
   list(1,u);

endmodule;

end;
