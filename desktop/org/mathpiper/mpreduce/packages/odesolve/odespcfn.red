module odespcfn$  % Linear special function ODEs

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


% F.J.Wright@Maths.QMW.ac.uk, Time-stamp: <14 September 2000>

% Old version temporarily preserved for testing other developments!

% A first attempt at pattern-matching solution of special (currently
% only second-order) linear odes.  Need to add more patterns.  But
% this approach may be too slow with more patterns anyway.

% If the specfn package is not loaded then we need this declaration:
algebraic operator Airy_Ai, Airy_Bi$

algebraic operator odesolve!-specfn!*$  % internal wrapper function

algebraic procedure ODESolve!-Specfn(odecoeffs1, driver1, x);
   %% Using monic coeffs for uniqueness.
   begin scalar ode, rules, soln;
      traceode1 "Looking for special-function solutions ...";
      ode := odesolve!-specfn!*(first odecoeffs1, second odecoeffs1);
      rules := {
         %% MUST use specific x, y (not ~x, ~y) for correct matching.
         %% ~~ does not seem to work in any of these rules.
         %% odesolve(df(y,x,2) - x*y, y, x);
         odesolve!-specfn!*(-x, 0) =>
            odesolve!-solns(Airy_Ai(x), Airy_Bi(x)),
         %% odesolve(df(y,x,2) - a3*x*y, y, x);
         odesolve!-specfn!*(-~a3*x, 0) =>
            odesolve!-solns(Airy_Ai(x), Airy_Bi(x), x=a3^(1/3)*x),
         %% odesolve(df(y,x,2) - (a3*x+a2b)*y, y, x);
         odesolve!-specfn!*(-(~a3*x+~a2b), 0) =>
            odesolve!-solns(Airy_Ai(x), Airy_Bi(x),
               x=a3^(1/3)*x+a2b/a3^(2/3)),

         %% The order of the following rules matters!

         %% odesolve(x^2*df(y,x,2) + x*df(y,x) - (x^2+n2)*y, y, x);
         odesolve!-specfn!*(-(1+~n2/x^2), 1/x)
            => odesolve!-solns(BesselI(n,x), BesselK(n,x),
               n = sqrt(n2)),
         %% odesolve(x^2*df(y,x,2) + x*df(y,x) + (x^2-n2)*y, y, x);
         odesolve!-specfn!*(1-~n2/x^2, 1/x)
            => odesolve!-solns(BesselJ(n,x), BesselY(n,x),
               n = sqrt(n2)),

         %% odesolve(x^2*df(y,x,2) + x*df(y,x) - (a2*x^2+n2)*y, y, x);
         odesolve!-specfn!*(-(~a2+~n2/x^2), 1/x)
            => odesolve!-solns(BesselI(n,a*x), BesselK(n,a*x),
               n = sqrt(n2), a = sqrt(a2)),
         %% odesolve(x^2*df(y,x,2) + x*df(y,x) + (a2*x^2-n2)*y, y, x);
         odesolve!-specfn!*(~a2-~n2/x^2, 1/x)
            => odesolve!-solns(BesselJ(n,a*x), BesselY(n,a*x),
               n = sqrt(n2), a = sqrt(a2)),

         %% odesolve(x*df(y,x,2) + df(y,x) - x*y, y, x);
%%          odesolve!-specfn!*(-1, 1/x)
%%             => odesolve!-solns(BesselI(0,x), BesselK(0,x)),
         %% odesolve(x*df(y,x,2) + df(y,x) - a2*x*y, y, x);
         odesolve!-specfn!*(-~a2, 1/x)
            => odesolve!-solns(BesselI(0,a*x), BesselK(0,a*x),
               a = sqrt(a2)),

         %% odesolve(x*df(y,x,2) + df(y,x) + x*y, y, x);
%%          odesolve!-specfn!*(1, 1/x)
%%             => odesolve!-solns(BesselJ(0,x), BesselY(0,x)),
         %% odesolve(x*df(y,x,2) + df(y,x) + a2*x*y, y, x);
         odesolve!-specfn!*(~a2, 1/x)
            => odesolve!-solns(BesselJ(0,a*x), BesselY(0,a*x),
               a = sqrt(a2))

                  }$
      soln := (ode where rules);        % `where' cannot produce a list!
      if soln neq ode then <<
         traceode
            "The reduced ODE can be solved in terms of special functions.";
         soln := part(soln, 1);
         %% if symbolic !*odesolve_load_specfn then load_package specfn;
         return if driver1 then
            %% BEWARE: This driver code is not well tested!
            %% traceode "But cannot currently handle the driver term! "
            { soln, ODESolve!-PI(soln, driver1, x) }
         else { soln }
      >>
   end$

algebraic operator ODESolve!-Solns!*$
listargp ODESolve!-Solns!*$

put('ODESolve!-Solns, 'psopfn, 'ODESolve!-Solns)$

symbolic procedure ODESolve!-Solns u; % (solns, subs)
   %% Avoid invalid lists on right of replacement rule, and build full
   %% optionally substituted basis data structure:
   begin scalar solns;
      %% u := revlis u;
      solns := {'list, car u, cadr u};  % algebraic list
      if (u := cddr u) then <<          % substitutions
         u := if cdr u then 'list . u else car u;
         solns := algebraic sub(u, solns)
      >>;
      return {'ODESolve!-Solns!*, solns}
   end$

endmodule$

end$
