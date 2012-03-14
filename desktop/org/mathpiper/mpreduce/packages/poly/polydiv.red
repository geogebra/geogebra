module polydiv;  % Enhanced polynomial division.

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


% F.J.Wright@Maths.QMW.ac.uk, 6 Nov 1995.

% Defines (or redefines) the following polynomial division operators:
%   divide, div and remainder (mod),
%   pseudo_divide, pseudo_quotient (pseudo_div) and pseudo_remainder.

% However, for now, div has been commented out, since it conflicts with
% other packages (avector and fide).

% ===================================================================

% Enhanced algebraic-mode operators for performing polynomial division
% over the current coefficient domain, based on the operator REMAINDER
% currently defined in poly.red by  put('remainder,'polyfn,'remf);

% divide(p,q) or p divide q returns an algebraic list {quotient,
% remainder} of p divided by q as polynomials over the current domain.

% div(p,q) or p div q returns only the quotient.
% remainder(p,q) or p mod q returns only the remainder.

% div and mod are the operator names used in Pascal for Euclidean
% (integer) division.

% An optional third argument (for the prefix forms) specifies the
% variable to treat as the leading variable for the (effectively
% univariate) polynomial division.


% Interface code
% ==============

% Regular division:
% ----------------

put('divide, 'psopfn, 'poly!-divide);
symbolic procedure poly!-divide u;
   poly!-divide!*(u, nil, nil);

remprop('remainder, 'polyfn);
put('remainder, 'psopfn, 'poly!-remainder);
put('mod, 'psopfn, 'poly!-remainder);   % name from Pascal
symbolic procedure poly!-remainder u;
   poly!-divide!*(u, 'remainder, nil);

% put('div, 'psopfn, 'poly!-quotient);    % name from Pascal
symbolic procedure poly!-quotient u;
   poly!-divide!*(u, 'quotient, nil);

infix divide, mod;
% infix div;
% Set a relatively low precedence:
precedence divide, freeof;  % higher than freeof, lower than +
% precedence div, divide;
% precedence mod, div;


% Pseudo-division:
% ---------------

put('pseudo_divide, 'psopfn, 'pseudo!-divide);
symbolic procedure pseudo!-divide u;
   poly!-divide!*(u, nil, t);

put('pseudo_remainder, 'psopfn, 'pseudo!-remainder);
symbolic procedure pseudo!-remainder u;
   poly!-divide!*(u, 'remainder, t);

put('pseudo_div, 'psopfn, 'pseudo!-quotient);
put('pseudo_quotient, 'psopfn, 'pseudo!-quotient);
symbolic procedure pseudo!-quotient u;
   poly!-divide!*(u, 'quotient, t);


fluid '(kord!*);

symbolic procedure poly!-divide!*(u, fn, pseudo);  % u = (p, q, x)
   % Returns the quotient and remainder of p (pseudo-)divided by q.
   % If specified, x is made the leading variable before dividing,
   % otherwise the first variable found is used.
   begin scalar p, q, x, new_kord;
      if null cdr u then RedErr "Divisor required";
      if length u > 3 then
         RedErr "Division operators take 2 or 3 arguments.";
      if null (q := !*a2f cadr u) then RedErr "Zero divisor";
      p := !*a2f car u;
      if cddr u and (x := !*a2k caddr u) and
         not(kord!* and x eq car kord!*) then <<
            new_kord := t;  updkorder x;
            p := reorder p;  q := reorder q
         >> where kord!* = kord!*;      % preserve environment
      u := if pseudo then pseudo!-qremf(p, q, x) else qremf(p, q);
      p := car u;  q := cdr u;
      if new_kord then <<
         if not(fn eq 'remainder) then p := reorder p;
         if not(fn eq 'quotient) then q := reorder q
      >>;
      return
         if fn eq 'remainder then mk!*sq (q ./ 1)
         else if fn eq 'quotient then mk!*sq (p ./ 1)
         else {'list, mk!*sq (p ./ 1), mk!*sq (q ./ 1)}
   end;


% Pseudo-division code
% ====================

symbolic procedure pseudo!-qremf(u, v, var);
   % Returns quotient and remainder of u pseudo-divided by v wrt var.
   % u and v are standard forms, var is a kernel or nil.
   % If var = nil then var := first kernel found.
   % Internally, polynomials are represented as coeff lists wrt var,
   % i.e. as lists of forms.
   % (Knuth 1981, Seminumerical Algorithms, Algorithm R, page 407.)
   begin scalar no_var, m, n, k, q0, q, car_v, car_u, vv;
      no_var := null var;
      m := if domainp u or (var and not(mvar u eq var)) then  0
      else << if not var then var := mvar u;  ldeg u >>;
      n := if domainp v or (var and not(mvar v eq var)) then  0
      else << if not var then var := mvar v;  ldeg v >>;

      %% The following special-case code for n = 0 and m < n is not
      %% necessary, but could be a cheap efficiency measure.
      %% if zerop n then return multf(exptf(v,m), u) . nil;
      %% if minusp(k := m - n) then return nil . u;

      u := if zerop m then {u} else coeffs u;
      v := if zerop n then {v} else coeffs v;
      if no_var and not(domainp_list v and domainp_list u) then
         msgpri("Main division variable selected is", var,
            nil, nil, nil);
      k := m - n;  car_v := car v;
      while k >= 0 do <<
         %% Compute the quotient q EFFICIENTLY.
         %% q0 = (q_0 ... q_k) without powers of v_n
         q0 := (car_u := car u) . q0;
         vv := cdr v;
         u := for each c in cdr u collect <<
            c := multf(c, car_v);
            if vv then <<
               c := subtrf(c, multf(car_u, car vv));
               vv := cdr vv
            >>;
            c
         >>;
         k := k - 1
      >>;
      if q0 then <<
         %% Reverse q0 and multiply in powers of v_n:
         q := car q0 . nil;  vv := 1;   % v_n^0
         while (q0 := cdr q0) do
            q := multf(car q0, (vv := multf(vv, car_v))) . q
      >>;
      return coeffs!-to!-form(q, var) . coeffs!-to!-form(u, var)
   end;

symbolic procedure coeffs!-to!-form(coeff_list, var);
   % Convert a coefficient list in DESCENDING power order to a
   % standard form wrt the specified variable var:
   coeff_list and
      coeffs!-to!-form1(coeff_list, var, length coeff_list - 1);

symbolic procedure coeffs!-to!-form1(coeff_list, var, d);
   if d > 0 then
      ( if car coeff_list then
         ((var .^ d) .* (car coeff_list)) .+ reductum
      else reductum )
         where reductum =
            coeffs!-to!-form1(cdr coeff_list, var, d - 1)
   else car coeff_list;

symbolic procedure domainp_list coeff_list;
   % Returns true if argument is a list of domain elements:
   null coeff_list or
      (domainp car coeff_list and domainp_list cdr coeff_list);

endmodule;

end;
