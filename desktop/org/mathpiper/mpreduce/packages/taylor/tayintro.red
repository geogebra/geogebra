module TayIntro;

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


%*****************************************************************
%
%          General utility functions
%
%*****************************************************************


exports
        confusion, constant!-sq!-p, delete!-nth, delete!-nth!-nth,
        replace!-nth, replace!-nth!-nth, smemberlp, Taylor!-error,
        var!-is!-nth;

imports

% from REDUCE kernel
        constant_exprp, denr, domainp, error1, kernp, mvar, neq, numr,
        prepsq, prin2t, rerror,

% from the header module
        TayTpElVars;

fluid '(!*tayexpanding!* !*tayrestart!* Taylor!:date!* Taylor!:version);

symbolic procedure var!-is!-nth(tp,var);
  %
  % Determines in which part of template tp the kernel var occurs.
  % Returns a pair (n . m) of positive integers which means
  %  that var is the mth subkernel in nth element of template tp
  % This would look a lot better if the loop statements allowed
  %  the use of the return statement.
  %
  begin scalar el,found; integer n,m;
    repeat <<
      n := n + 1;
      el := TayTpElVars car tp;
      m := 1;
      while el do <<
        if var neq car el then <<el := cdr el; m := m + 1>>
         else <<el := nil; found := t>>>>;
      tp := cdr tp>>
    until null tp or found;
    if not found then confusion 'var!-is!-nth
     else return (n . m)
  end;

symbolic procedure delete!-nth (l, n);
  %
  % builds a new list with nth element of list l removed
  %
  if n = 1 then cdr l else car l . delete!-nth (cdr l, n - 1);

symbolic procedure delete!-nth!-nth (l, n, m);
  %
  % builds a new list with mth element of nth sublist of list l
  % removed
  %
  if n = 1 then delete!-nth (car l, m) . cdr l
   else car l . delete!-nth!-nth (cdr l, n - 1, m);

symbolic procedure replace!-nth (l, n, v);
  %
  % builds a new list with the nth element of list l replaced by v
  %
  if n = 1 then v . cdr l else car l . replace!-nth (cdr l, n - 1, v);

symbolic procedure replace!-nth!-nth (l, n, m, v);
  %
  % builds a new list with the mth element of nth sublist of list l
  % replaced by v
  %
  if n = 1 then replace!-nth (car l, m, v) . cdr l
   else car l . replace!-nth!-nth (cdr l, n - 1, m, v);

symbolic procedure constant!-sq!-p u;
  %
  % returns t if s.q. u represents a constant
  %
  numberp denr u and domainp numr u
      or kernp u and atom mvar u and flagp (mvar u, 'constant)
      or constant_exprp prepsq u;

symbolic procedure smemberlp (u, v);
  %
  % true if any member of list u is contained at any level in v
  %
  if null v then nil
   else if atom v then v member u
   else smemberlp (u, car v) or smemberlp (u, cdr v);

symbolic procedure confusion msg;
  %
  % called if an internal error occurs.
  % (I borrowed the name from Prof. Donald E. Knuth's TeX program)
  %
  << terpri ();
     prin2 "TAYLOR PACKAGE (version ";
     prin2 Taylor!:version;
     prin2 ", as of ";
     prin2 Taylor!:date!*;
     prin2t "):";
     prin2 "This can't happen (";
     prin2 msg;
     prin2t ") !";
     rerror (taylor, 1,
             "Please send input and output to Rainer M. Schoepf!") >>;

symbolic procedure Taylor!-error (type, info);
  %
  % called if a normal error occurs.
  % type is the type of error, info the error info.
  %
  begin scalar msg; integer errno;
    msg := if type eq 'not!-a!-unit then "Not a unit in argument to"
            else if type eq 'wrong!-no!-args
             then "Wrong number of arguments to"
            else if type eq 'expansion
             then "Error during expansion"
            else if type eq 'wrong!-type!-arg
             then "Wrong argument type"
            else if type eq 'no!-original
             then "Taylor kernel doesn't have an original part in"
            else if type eq 'zero!-denom
             then "Zero divisor in"
            else if type eq 'essential!-singularity
             then "Essential singularity in"
            else if type eq 'branch!-point
             then "Branch point detected in"
            else if type eq 'branch!-cut
             then "Expansion point lies on branch cut in"
%            else if type eq 'inttaylorwrttayvar
%             then
%              "Integration of Taylor kernel yields non-analytical term"
            else if type eq 'invalid!-subst
             then "Invalid substitution in Taylor kernel:"
            else if type eq 'tayrevert
             then "Reversion of Taylor series not possible:"
            else if type eq 'implicit_taylor
             then
              "Computation of Taylor series of implicit function failed"
            else if type eq 'inverse_taylor
             then
              "Computation of Taylor series of inverse function failed"
            else if type eq 'max_cycles
             then "Computation loops (recursive definition?):"
            else if type eq 'not!-implemented
             then "Not implemented yet"
            else confusion 'Taylor!-ERROR;
%    rerror (taylor, errno,
    rerror (taylor, 2,
            if null info then msg
             else if atom info then {msg, info}
             else msg . info);
  end;

symbolic procedure Taylor!-error!*(type,info);
   %
   % Like Taylor!-error, but calls sets !*tayrestart!* and calls
   %  error1 if !*tayexpanding!* indicates that expansion is going
   %  on and more terms might be necessary.
   %
   if !*tayexpanding!* then <<!*tayrestart!* := t; error1()>>
    else Taylor!-error(type,info);

endmodule;

end;
