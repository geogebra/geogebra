module intfac;   % Interface between integrator and factorizer.

% Author:  Anthony C. Hearn.

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


% Based on earlier versions by James Davenport, Mary Ann Moore and
% Arthur Norman.

fluid '(!*intfac !*surds kord!* zlist);  % clogflag

exports int!-fac;

symbolic procedure int!-fac x;
   % X is a primitive, square-free polynomial, except for monomial
   % factors.  Result is a list of 'factors' wrt zlist.
   % Throughout most of the integrator we want to add new surds, so we
   % turn surds on.  However, we use *intfac to inhibit use of quadratic
   % factorizer in the poly/primfac module, since things don't work
   % properly if this is used.
   begin scalar !*intfac,!*surds;
      !*intfac := !*surds := t;
      return int!-fac!-inner x;
   end;

symbolic procedure int!-fac!-inner x;
   % X is a primitive, square-free polynomial, except for monomial
   % factors.  Result is a list of  'factors' wrt zlist.
   begin scalar factors;
      factors := fctrf x;
      factors := cdr factors; % Ignore monomial factor.
      % Make sure x really square-free.
      factors := for each u in factors
                    collect if cdr u=1 then car u
                             else interr list(x,"not square free");
      % It seems we need the logs ordered ahead of atans, hence reverse.
      return reversip for each u in factors join fac2int u
   end;

symbolic procedure fac2int u;
   % Returns a list of all the arctangents and logarithms arising from
   % an attempt to take the one irreducible (but not necessarily the
   % absolutely irreducible) factor u.
   begin scalar degrees,x;
      degrees := for each w in zlist collect (degreef(u,w) . w);
      if assoc(1,degrees) then return list ('log . (u ./ 1))
      % An irreducible polynomial of degree 1 is absolutely irreducible.
       else if x := assoc(2,degrees) then return int!-quadterm(u,cdr x)
       else if assoc(0,degrees) then return list('log . (u ./ 1));
      % This suggests a surd occurs. Should that be an error?
      if !*trint
        then <<printc "*** Polynomial";
               printsf u;
               printc "has not been completely factored">>;
      return list ('log . (u ./ 1))
   end;

symbolic procedure int!-quadterm(pol,var);
   % Add in logs and atans corresponding to splitting the polynomial pol
   % given it is quadratic wrt var.  Does not assume pol is univariate.
   % We need to rootxf!* so that
   % int(1/(x**2*y0+x**2+x*y0**2+3*x*y0+x+y0**2+y0),x) comes out in
   % terms of real functions.
   begin scalar a,b,c,discrim,kord,res,w;
      kord := setkorder(var . kord!*);   % It shouldn't matter if
                                         % var occurs twice.
      c := reorder pol;
      if ldeg c neq 2
        then <<setkorder kord;
               rerror(int,5,"Invalid polynomial in int-quadterm")>>;
      a := lc c;
      c := red c;
      if not domainp c and mvar c = var and ldeg c = 1
        then <<b := lc c; c := red c>>;
      setkorder kord;
      discrim := powsubsf addf(multf(b,b),multd(-4,multf(a,c)));
      if null discrim then interr "discrim is zero in quadterm";
      % A quadratic usually implies an atan term.
%     if not clogflag
%       then <<w := rootxf(negf discrim,2);
%              if not(w eq 'failed) then go to atancase>>;
      w := rootxf!*(negf discrim,2);
      if not(w eq 'failed) then go to atancase;
      w := rootxf!*(discrim,2);   % Maybe only rootxf is needed here.
      if w eq 'failed then return list ('log . !*f2q pol);
%     if w eq 'failed then rederr "Integration failure in int-quadterm";
      discrim := w;
      w := multpf(mksp(var,1),a);
      w := addf(multd(2,w),b);   % 2*a*x + b.
      a := addf(w,discrim);
      b := addf(w,negf discrim);
      % Remove monomial multipliers.
      a := quotf(a,cdr comfac a);
      b := quotf(b,cdr comfac b);
      return ('log . !*f2q a) . ('log . !*f2q b) . res;
   atancase:
      res := ('log . !*f2q pol) . res;   % One part of answer.
      a := multpf(mksp(var,1),a);
      a := addf(b,multd(2,a));
      a := fquotf(a,w);
      return ('atan . a) . res
   end;

symbolic procedure rootxf!*(u,n);
   (if x eq 'failed or smemq('i,x) and not smemq('i,u)
      then (rootxf(u,n) where !*surds=nil)
     else x)
    where x=rootxf(u,n);

endmodule;

end;
