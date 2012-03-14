module conj;  % Rationalize denoms of standard quotients by conjugate
              % computation.

% Author: Anthony C. Hearn.

% Modifications by: Eberhard Schruefer.

% Copyright (c) 1992 RAND.  All rights reserved.

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


fluid '(!*algint !*rationalize !*structure dmode!* kord!* powlis!*);

put('rationalize,'simpfg,'((t (rmsubs)) (nil (rmsubs))));

symbolic smacro procedure subtrf(u,v);
   % Returns u - v for standard forms u and v.
   addf(u,negf v);

symbolic procedure rationalizesq u;
   % Rationalize the standard quotient u.
   begin scalar !*structure,!*sub2,v,x;  % Modified by R. Liska.
      % We need structure off to form rationalized denominator properly
      % in subs2f1.
      % ACH had hoped that the cost of having GCD on here was small,
      % since the consequences can be large (e.g., df(log((sqrt(a^2+x^2)
      % +2*sqrt(sqrt(a^2+x^2)*a+a*x)+a+x)/(sqrt(a^2+x^2) - a + x)),x)).
      % However, limit((sqrt(x^(2/5) +1) - x^(1/3)-1)/x^(1/3),x,0) takes
      % too long.
      if x := get(dmode!*,'rationalizefn) then u := apply1(x,u);
      % We need the following in case we are in sparse_bareiss.
      powlis!* := '(i 2 (nil . t) -1 nil) . powlis!*;
      v := subs2q u;
      powlis!* := cdr powlis!*;
      % We need the subs2 to get rid of surd powers.
      % We also need to check if u has changed from the example
      % df((1/x)**(2/3),x).
      return if domainp denr v then v
              else if (x := rationalizef denr v) neq 1
               then <<v := multf(numr v,x) ./ multf(denr v,x);
                      % We need the gcdchk so that df((1/x)**(2/3),x)
                      % is not in a loop.  However, algint needs all
                      % factors for some reason.
                      if null !*algint and null !*rationalize
                        then v := gcdchk v;
                      % There used to be an exptchk here, but that led
                      % to loops (e.g., in df(int(1/(3*x**4+7),x),x)).
                      % We need to suppress following to avoid a non-
                      % terminating evaluation of df(tan((sqrt(1-x^2)
                      %   *asin acos x + 2*sqrt(1-x^2)*x)/x),x).
%                     v := subs2q v;
%                     if not domainp numr quotsq(u,v)
%                       then rationalizesq v else v>>
                      subs2q v>>
             else u
   end;

symbolic procedure lowertowerp(u,v);
   % True if v is potentially an algebraic component of a member of v.
   if null u then nil
    else if atom car u or cdar u = v then lowertowerp(cdr u,v)
    else if caar u eq 'expt
       and eqcar(caddar u,'quotient)
       and cadr caddar u = cadr cadr v  % numerator of quotient.
       and fixp caddr caddar u and fixp caddr cadr v
       and cdr divide(caddr caddar u,caddr cadr v) = 0  % denominator.
       and lowertowerp1(cadar u,car v)
     then car u
    else lowertowerp(cdr u,v);

symbolic procedure lowertowerp1(u,v);
   % This procedure decides if u can be an algebraic extension of v.
   % The = case is decidedly heuristic at the moment.
   % We could think of this as a membership test (including =).
   % However, different SQRT representations complicate things.
   (if x>y then t
     else if numberp u and numberp v then not(gcdn(u,v)=1)
     else x=y)
    where x=exprsize u,y=exprsize v;

symbolic procedure exprsize u;
   % Get size of u.  Iterative to avoid excessive recursion.
   begin integer n;
   a: if null u then return n else if atom u then return n+1;
      n := exprsize car u + n;
      u := cdr u;
      go to a
   end;

symbolic procedure rationalizef u;
   % Look for I and sqrts, cbrts, quartics at present.
   % I'm not sure I in the presence of (-1)^(1/4) say is handled
   % properly.
   % It is assumed that any surd powers have been reduced before
   % entering this procedure.
   begin scalar x,y,z;
      x := z := kernels u;
   a: if null x then return 1;
      y := car x;
      if eqcar(y,'expt) and eqcar(caddr y,'quotient)
          and lowertowerp(z,cdr y)
        then nil
       else if y eq 'i or eqcar(y,'expt) and caddr y = '(quotient 1 2)
          or eqcar(y,'sqrt)
        then return conjquadratic(mkmain(u,y),y)
       else if eqcar(y,'expt) and caddr y = '(quotient 1 3)
        then return conjcubic(mkmain(u,y),y)
       else if eqcar(y,'expt) and caddr y = '(quotient 1 4)
        then return conjquartic(mkmain(u,y),y);
      x := cdr x;
      go to a
   end;

symbolic procedure conjquadratic(u,v);
   if ldeg u = 1
      then subtrf(multf(!*k2f v,reorder lc u),reorder red u)
    else errach list(ldeg u,"invalid power in rationalizef");

symbolic procedure conjcubic(u,v);
   begin scalar c1,c2,c3,w;
     if ldeg u = 2 then <<c1 := reorder lc u;
                           if degr(red u,v) = 1
                              then <<c2 := reorder lc red u;
                                     c3 := reorder red red u>>
                            else c3 := reorder red u>>
      else <<c2 := reorder lc u;
             c3 := reorder red u>>;
     w := conj2 v;
     if w eq 'failed then return u;
     v := !*k2f v;
     return addf(multf(exptf(v,2),subtrf(exptf(c2,2),multf(c1,c3))),
                 addf(multf(v,subtrf(multf(w,exptf(c1,2)),
                                     multf(c2,c3))),
                      subtrf(exptf(c3,2),multf(w,multf(c1,c2)))))
  end;

symbolic procedure conj2 u;
%  (if not domainp denr v then errach list("conj2",u)
   (if not domainp denr v then 'failed
     else if denr v neq 1 then multd(!:recip denr v,numr v)
     else numr v)
   where v = simp cadr u;

symbolic procedure conjquartic(u,v);
   begin scalar c1,c3,c4,q1,q2,q3,q4,w;
     if ldeg u = 3
        then <<c1 := reorder lc u;
               if degr(red u,v) = 1
                  then <<c3 := reorder lc red u;
                         c4 := reorder red red u>>
                else c4 := reorder red u>>
      else if ldeg u = 1
              then <<c3 := reorder lc u;
                     c4 := reorder red u>>;
     w := conj2 v;
     if w eq 'failed then return u;
     v := !*k2f v;
     q1 := subtrf(addf(exptf(c3,3),multf(c1,exptf(c4,2))),
                  multf(w,multf(c3,exptf(c1,2))));
     q2 := negf addf(multf(w,multf(c4,exptf(c1,2))),
                     multf(exptf(c3,2),c4));
     q3 := addf(multf(c3,exptf(c4,2)),
                subtrf(multf(exptf(w,2),exptf(c1,3)),
                       multf(w,multf(c1,exptf(c3,2)))));
     q4 := subtrf(multf(w,multf(multd(2,c1),multf(c3,c4))),exptf(c4,3));
     return addf(multf(exptf(v,3),q1),
                 addf(multf(exptf(v,2),q2),addf(multf(v,q3),q4)))
    end;

symbolic procedure mkmain(u,var);
   % Make kernel var the main variable of u.
   begin scalar kord!*; kord!* := list var; return reorder u end;

endmodule;

end;
