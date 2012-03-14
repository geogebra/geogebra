module sets;  % Operators for basic set theory.

%% Author: F.J.Wright@Maths.QMW.ac.uk.
%% Date: 20 Feb 1994.

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


%% WARNING: This module patches mk!*sq.

%% To do:
%% Improve symbolic set-Boolean analysis.
%% Rationalize the coding?

%% A nice illustration of fancy maths printing in the graphics mode
%% of PSL-REDUCE under MS-Windows and X, but it works properly only with
%% interface versions compiled from sources dated after 14 Feb 1994.

%% Defines the set-valued infix operators (with synonyms):
%%   union, intersection (intersect), setdiff (minus, \),
%% and the Boolean-valued infix operators:
%%   member, subset_eq, subset, set_eq.
%% Arguments may be algebraic-mode lists representing explicit sets,
%% or identifiers representing symbolic sets, or set-valued expressions.
%% Lists are converted to sets by deleting any duplicate elements, and
%% sets are sorted into a canonical ordering before being returned.
%% This can also be done explicitly by applying the unary operator
%% mkset.  The set-valued operators may remain symbolic, but
%% REDUCE does not currently support this concept for Boolean-valued
%% operators, and so neither does this package (although it could).
%% Set-theoretic simplifications are performed, but probably not fully.

%% A naive power set procedure is included as an algebraic example
%% in the test file (sets.tst).


%% A proposed new coding style:
deflist('((local scalar)), 'newnam);
%% (DEFLIST used because flagged eval -- PUT does not work during
%% faslout!)

%% One good reason not to use `\' in place of `!' ?
newtok '((!\) setdiff);
%% NOTE that this works in graphics mode under Windows or X PSL-REDUCE
%% ONLY with versions compiled from sources dated after 14 Feb 1994.
%% The following statement should really be in fmprint.red:
put('setdiff, 'fancy!-infix!-symbol, "\backslash");

%% A set is sorted before it is returned for purely cosmetic reasons,
%% except that together with duplicate elimination this makes the repre-
%% sentation canonical and so list equality can be used as set equality.

create!-package('(sets),'(contrib misc));

symbolic smacro procedure sort!-set l;
   sort(l, function set!-ordp);

symbolic procedure set!-ordp(u, v);
   %% Ordp alone (as used by ordn to implement symmetry) looks strange.
   %% This seems like a reasonable compromise.
   if numberp u and numberp v then u < v else ordp(u, v);


%% Set-valued operators:
%% ====================
infix union, intersection, setdiff;
put('intersect, 'newnam, 'intersection);
put('minus, 'newnam, 'setdiff);         % cf. Maple!
precedence setdiff, -;
precedence union, setdiff;
precedence intersection, union;

%% Must be simpfns for let rules to be applicable.
put('union, 'simpfn, 'simpunion);
put('intersection, 'simpfn, 'simpintersection);
put('setdiff, 'simpfn, 'simpsetdiff);

flag('(union intersection), 'nary);     % associativity
put('union, 'unary, 'union);            % for completeness
put('intersection, 'unary, 'intersection);
listargp union, intersection;           % necessary for unary case
%% Symmetry is normally implemented by simpiden, which is not
%% used here and the symmetry is implemented explicitly,
%% but the symmetric flag is also used when applying let rules.
flag('(union intersection), 'symmetric); % commutativity

%% Intersection distributes over union, which is implemented
%% as a rule list at the end of this file.

global '(empty_set);  symbolic(empty_set := '(list));

%% Below ordn sorts for symmetry as in simpiden for symmetric operators

symbolic procedure simpunion args;
   %% x union {} = x, union x = x
   !*kk2q(if car r eq 'union
      then if cdr(r := delete(empty_set, cdr r))
         then 'union . ordn r else car r
      else r)
   where r = applysetop('union, args);

symbolic procedure simpintersection args;
   %% x intersect {} = {}, intersection x = x
   !*kk2q(if car r eq 'intersection
      then if empty_set member(r := cdr r) then empty_set
         else if cdr r then 'intersection . ordn r else car r
      else r)
   where r = applysetop('intersection, args);

symbolic procedure simpsetdiff args;
   %% x setdiff x = {} setdiff x = {}, x setdiff {} = x.
   !*kk2q(if car r eq 'setdiff
      then if cadr r = caddr r or cadr r = empty_set then empty_set
         else if caddr r = empty_set then cadr r else r
      else r)
   where r = applysetop('setdiff, args);

%% The following mechanism allows unevaluated operators to remain
%% symbolic and supports n-ary union and intersection.

%% Allow set-valued expressions as sets:
flag('(union, intersection, setdiff), 'setvalued);

symbolic procedure applysetop(setop, args);
   %% Apply binary Lisp-level set functions to pairs of explicit
   %% set args and collect symbolic args:
   begin local set_arg, sym_args, setdiff_args;
      set_arg := 0;                    % cannot use nil as initial value
      setdiff_args := for each u in args collect
      %% reval form makes handling kernels and sorting easier:
         if eqcar(u := reval u, 'list) then
            << u := delete!-dups cdr u;
               set_arg := if set_arg = 0 then u
                  else apply2(setop, set_arg, u);
               make!-set u >>
         else if idp u or (pairp u and flagp(car u, 'setvalued)) then
            %% Implement idempotency for union and intersection:
            << if not(u member sym_args)
                 then sym_args := u . sym_args; u >>
         %% else typerr(if eqcar(u,'!*sq) then prepsq cadr u
         %%              else u,"set");
         else typerr(u, "set");         % u was reval'ed
      return if sym_args then
         setop . if setop eq 'setdiff then setdiff_args else
            if set_arg = 0 then sym_args
             else make!-set set_arg . sym_args
      else aeval make!-set set_arg      % aeval NEEDED for consistency
   end;

symbolic operator mkset;

symbolic procedure mkset rlist;
   %% Make a set from an algebraic-mode list:
   make!-set delete!-dups getrlist rlist;

%% The function list2set is already defined in PSL
%% to remove duplicates and PARTIALLY sort,
%% but it is not defined in the REDUCE sources.

symbolic procedure make!-set l;
   makelist sort!-set l;

symbolic procedure delete!-dups l;
   if l then
      if car l member cdr l then delete!-dups(cdr l)
      else car l . delete!-dups(cdr l);


%% Boolean-valued operators:
%% ========================
infix subset_eq, subset, set_eq;        % member already declared
precedence subset_eq, <;
precedence subset, subset_eq;
precedence set_eq, =;

put('member, 'boolfn, 'evalmember);
put('subset_eq, 'boolfn, 'evalsubset_eq);
put('subset, 'boolfn, 'evalsubset);
put('set_eq, 'boolfn, 'evalset_eq);

%% Boolfns get their arguments aeval'd automatically.

symbolic procedure evalmember(el, rlist);
   %% Special case -- only applicable to explicit lists.
   member(el, getrlist rlist);

symbolic procedure evalsubset_eq(u, v);
   (if atom r then r else apply(function equal, r) or evalsymsubset r)
      where r = evalsetbool('subset_eq, u, v);

put('subset_eq, 'setboolfn, function subsetp);

symbolic procedure evalsubset(u, v);
   (if atom r then r else evalsymsubset r)
      where r = evalsetbool('subset, u, v);

put('subset, 'setboolfn, function subsetneqp);

symbolic procedure subsetneqp(u, v);
   subsetp(u,v) and not subsetp(v,u);

symbolic procedure evalsymsubset args;
   %% This analysis assumes symbolic sets are non-empty, otherwise
   %% the relation may be equality rather than strict inclusion.
   %% Could or should this analysis be extended?
   ((eqcar(v, 'union) and u member cdr v) or
    (eqcar(u, 'intersection) and v member cdr u) or
    (eqcar(u, 'setdiff) and
       (cadr u = v or (eqcar(v, 'union) and cadr u member cdr v))))
      where u = car args, v = cadr args;

%% Set equality can use list equality provided the representation
%% is canonical (duplicate-free and ordered).  The following set
%% equality predicate is independent of set implementation,
%% and implements precisely the formal mathematical definition.

symbolic procedure evalset_eq(u, v);
   (if atom r then r else apply(function equal, r))
      where r = evalsetbool('set_eq, u, v);

put('set_eq, 'setboolfn, function setequal);

symbolic procedure setequal(u, v);
   subsetp(u,v) and subsetp(v,u);

symbolic procedure evalsetbool(setbool, u, v);
   begin local r, set_args, sym_args;
      r := for each el in {u, v} collect
         if eqcar(el, 'list) then
            << set_args := t;  cdr el >>
         %% reval form makes handling kernels easier:
         else if idp(el := reval el) or
               (pairp el and flagp(car el, 'setvalued)) then
            << sym_args := t;  el >>
         else typerr(el, "set");        % el was reval'ed
      return if set_args then
         if sym_args then               % RedErr
            msgpri("Cannot evaluate", {setbool, reval u, reval v},
               "as Boolean-valued set expression", nil, t)
         else apply(get(setbool,'setboolfn), r)
      else r
   end;


%% Boolean evaluation operator:
%% ===========================
%% Nothing to do with sets, but useful for testing Boolean operators:

symbolic operator evalb;                % cf. Maple
symbolic procedure evalb condition;
   if eval formbool(condition, nil, 'algebraic) then 'true else 'false;

flag('(evalb), 'noval);  % because evalb evals its argument itself

%% Note that this does not work - it generates the wrong code:
%% algebraic procedure evalb condition;
%%    if condition then true else false;


%% Set simplification rules:
%% ========================

algebraic;

set_distribution_rule := {~x intersection (~y union ~z) =>
   (x intersection y) union (x intersection z)};

endmodule;

end;
