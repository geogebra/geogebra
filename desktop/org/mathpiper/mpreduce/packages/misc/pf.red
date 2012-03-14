module pf;  % Compute partial fractions for an expression.

% Author: Anthony C. Hearn.

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


Comment PF is the top level operator for finding the partial fractions
of an expression.  It returns the partial fractions as a list.

The algorithms used here are relatively unsophisticated, and use the
extended Euclidean algorithm to break up expressions into factors.
Much more sophisticated algorithms exist in the literature;

fluid '(!*exp !*limitedfactors !*gcd kord!*);

symbolic operator pf;

flag('(pf),'noval);   % Since PF will do its own simplification.

symbolic procedure pf(u,var);
   % Convert an algebraic expression into partial fractions.
   begin scalar !*exp,!*gcd,kord!*,!*limitedfactors,polypart,rfactor,
                u1,u2,u3,u4,var,x,xx,y;
      !*exp := !*gcd := t;
      xx := updkorder var;         % Make var the main variable.
      x := subs2 resimp simp!* u;  % To allow for OFF EXP forms.
      u1 := denr x;
      if degr(u1,var) = 0 then <<setkorder xx; return list('list,u)>>;
      u2 := qremsq(!*f2q numr x,!*f2q u1,var); %Extract polynomial part.
      if caar u2 then polypart := car u2;
      rfactor := 1 ./ 1;           % Factor for rational part.
      u2 := cdr u2;
      u3 := fctrf u1;              % Factorize denominator.
      x := cdr u3;
      u3 := car u3;
      % Process monomial part.
      while not domainp u3 do
       <<if mvar u3 eq var then x := (!*k2f mvar u3 . ldeg u3) . x
          else <<u4 := !*p2f lpow u3;
                 rfactor := numr rfactor ./ multf(u4,denr rfactor);
                 u1 := quotf(u1,u4)>>;
         u3 := lc u3>>;
      if u3 neq 1 then <<rfactor := numr rfactor
                                       ./ multf(u3,denr rfactor);
                         u1 := quotf(u1,u3)>>;
      % Separate power factors in denominator.
      while length x>1 do
       <<u3 := exptf(caar x,cdar x);
         u1 := quotf(u1,u3);
         if degr(u3,var)=0
           then rfactor := numr rfactor ./ multf(u3,denr rfactor)
      %    then <<rfactor := numr rfactor ./ multf(u3,denr rfactor);
      %           u2 := nil>>
          else <<u4 := xeucl(u1,u3,var);
                 % Remove spurious polynomial in numerator.
                 y := (multsq(remsq(multsq(car u4,u2),!*f2q u3,var),
                              rfactor) . car x)
                        . y;
                 u2 := multsq(cdr u4,u2)>>;
         x := cdr x>>;
      u3 := exptf(caar x,cdar x);
      if u2 = (nil ./ 1) then nil
       else if degr(u3,var)=0
        then rfactor := numr rfactor ./ multf(u3,denr rfactor)
      % Remove spurious polynomial in numerator.
       else y := (multsq(rfactor,remsq(u2,!*f2q u3,var)) . car x) . y;
      x := nil;
      % Finally break down non-linear terms in denominator.
      for each j in y do
         if cddr j =1 then x := j . x
          else x := append(pfpower(car j,cadr j,cddr j,var),x);
      x := for each j in x
              collect list('quotient,prepsq!* car j,
                           if cddr j=1 then prepf cadr j
                            else list('expt,prepf cadr j,cddr j));
      if polypart then x := prepsq!* polypart . x;
      setkorder xx;
      return 'list . x
   end;

symbolic procedure xeucl(u,v,var);
   % Extended Euclidean algorithm with rational coefficients.
   % I.e., find polynomials Q, R in var with rational coefficients (as
   % standard quotients) such that Q*u + R*v = 1, where u and v are
   % relatively prime standard forms in variable var.  Returns Q . R.
   begin scalar q,r,s,w;
      q := list(1 ./ 1,nil ./ 1);
      r := list(nil ./ 1,1 ./ 1);
      if degr(u,var) < degr(v,var)
        then <<s := u; u := v; v := s; s := q; q := r; r := s>>;
      u := !*f2q u; v := !*f2q v;
      while numr v do
       <<if degr(numr v,var)=0 then w := quotsq(u,v) . (nil ./ 1)
          else w := qremsq(u,v,var);
         s := list(addsq(car q,negsq multsq(car w,car r)),
                   addsq(cadr q,negsq multsq(car w,cadr r)));
         u := v;
         v := cdr w;
         q := r;
         r := s>>;
      v := lnc numr u ./ denr u;   % Is it possible for this not to be
                                   % in lowest terms, and, if so, does
                                   % it matter?
      r := quotsq(v,u);
      return multsq(r,quotsq(car q,v)) . multsq(r,quotsq(cadr q,v))
  end;

symbolic procedure qremsq(u,v,var);
   % Find rational quotient and remainder (as standard quotients)
   % dividing standard quotients u by v wrt var.
   % This should really be done more directly without using quotsq.
   (quotsq(addsq(u,negsq x),v) . x) where x=remsq(u,v,var);

symbolic procedure remsq(u,v,var);
   % Find rational and remainder (as a standard quotient) on
   % dividing standard quotients u by v wrt var.
   begin integer m,n; scalar x;
      n := degr(numr v,var);
      if n=0 then rederr list "Remsq given zero degree polynomial";
      while (m := degr(numr u,var))>= n do
       <<if m=n then x := v
          else x := multsq(!*p2q(var.**(m-n)),v);
         u := addsq(u,
                    negsq multsq(multf(lc numr u,denr v)
                                   ./ multf(lc numr v,denr u),
                                 x))>>;
      return u
   end;

symbolic procedure pfpower(u,v,n,var);
   % Convert u/v^n into partial fractions.
   begin scalar x,z;
      while degr(numr u,var)>0 do
       <<x := qremsq(u,!*f2q v,var);
         z := (cdr x . v . n) . z;
         n := n-1;
         u := car x>>;
      if numr u then z := (u . v . n) . z;
      return z
   end;

endmodule;

end;
