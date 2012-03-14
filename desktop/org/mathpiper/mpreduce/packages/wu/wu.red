module wu;   % Simple implementation of the Wu algorithm.

% Author: Russell Bradford
%         School of Mathematical Sciences
%         University of Bath
%         Bath
%         Avon BA2 7AY
%         United Kingdom
%         E-mail: rjb@maths.bath.ac.uk

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


% First distributed version: 8 July 90
% Bug fixes in wupseudodivide, and misc other changes: 28 Aug 90

% This is a simple implementation of the Wu algorithm, intended to help
% myself understand the method.  As such, there is little optimization,
% and indeed, only implements the basic version from
%
% "A Zero Structure Theorem for Polynomial-Equations-Solving",
%  Wu Wen-tsun, Institute of Systems Science, Academia Sinica, Beijing

% Interface:
% much as the Groebner basis package:
%
% wu({x*y-a, x^y+y^2-b}, {x, y});
%
% uses Wu on the named polynomials with ordering on the variables x > y.
% returns a list of pairs { characteristic set, initial }
%
% { {{a^2 - b*y^2 + y^4}, y} }
%
% The zeros of the input polynomials are the the union of the zeros of
% the characteristic sets, subject to the initials being non-zero.
% Thus the zeros of {x*y-a, x^y+y^2-b} are the zeros of
% {a^2 - b*y^2 + y^4, a - x*y} subject to y neq 0.
%
% The switch
%
% on trwu;
%
% prints some tracing of the algorithm as it works, in particular the
% choice of basic sets, and the computation of characteristic sets.

% This package runs on Reduce 3.3.

% Keywords: polynomial reduction characteristic set sets initial
%           ascending
% chrstrem Wu

% All improvements and bug fixes are welcomed!!

% Possible bug fixes, improvements:
% Should use distributed polys, then class is an integer;
% rather than use union, use an insertion sort;
% return a list of {{polys},{initials}};
% fix pseudo divide for when there is a non-trivial content in the
%   remainder;
% many opportunities for reusing data from a previous iteration, e.g.,
%   when a new polynomial added into a basic set is less than all
%   current members of the basic set, and they are reduced wrt it.
% factor out monomials and numeric contents

create!-package('(wu),'(contrib misc));

fluid '(!*trwu !*trchrstrem wuvarlist!* kord!*);

switch trwu, trchrstrem;

procedure wuconstantp f;
   % A constant is a poly that does not involve any of the interesting
   % variables.
   domainp f or not memq(mvar f, wuvarlist!*);

smacro procedure wuclass f;
  if wuconstantp f then nil else mvar f;

smacro procedure wudeg f;
  if wuconstantp f then 0 else ldeg f;

smacro procedure wuinitial f;
  if wuconstantp f then f else lc f;

procedure wureducedpolysp(f, polylist);
% if f reduced wrt the polys in polylist?
  null polylist or
  (wureducedp(f, car polylist) and wureducedpolysp(f, cdr polylist));

procedure wureducedp(g, f);
% is g reduced wrt f?
  wuconstantp f or
  wuconstantp g or
  deginvar(g, wuclass f) < ldeg f;

procedure deginvar(f, x);
% the degree of x in f
  if wuconstantp f then 0
  else if mvar f = x then ldeg f
  else begin scalar kord!*;
    kord!* := list x;
    f := reorder f;
    return if mvar f = x then ldeg f else 0
  end;

% wukord* = '(x y a) means: all other symbols < x < y < a
fluid '(wukord!*);

procedure symbollessp(x, y);
% an ordering on symbols: Cambs lisp and PSL orderp differ on nils
  if null y then nil
  else if null x then t
  else if wukord!* then wuorderp(x, y)
  else not orderp(x, y);

procedure wuorderp(x, y);
% an order on the symbols has been specified
% return T if x < y
% circumlocutions abound
begin scalar kord, answ;
   if x eq y then return nil;
   kord := wukord!*;
   while kord and not answ do
     if x eq car kord
       then answ := if memq(y, cdr kord) then 'yes else 'no
     else if y eq car kord
       then answ := if memq(x, cdr kord) then 'no else 'yes
     else kord := cdr kord;
   return if answ then answ eq 'yes else not orderp(x, y)
end;

smacro procedure classlessp(c1, c2);
% an order on classes, which are symbols in this implementation
  symbollessp(c1, c2);

procedure wulessp(f, g);
% standard forms f and g
% a partial order
  classlessp(wuclass f, wuclass g) or
  (wuclass f = wuclass g and wudeg f < wudeg g);

procedure wulessp!*(f, g);
% as above, but use some arbitrary means to complete to a total order
  if wulessp(f, g) then t
  else if wulessp(g, f) then nil
  else totallessp(f, g);

smacro procedure nil2zero f;
  f or 0;

procedure totallessp(f, g);
% a total order on polynomials
  totalcompare(f, g) = 'less;

procedure totalcompare(f, g);
% order f and g
% horrid bit of code
  if f = g then 'equal
  else if wulessp(f, g) then 'less
  else if wulessp(g, f) then 'greater
  else if wuconstantp f then                % and so wuconstantp g
    totalcompareconstants(f, g)
  else begin scalar answ;
    answ := totalcompare(lc f, lc g);
    if answ neq 'equal then return answ;
    return totalcompare(red f, red g)
  end;

procedure totalcompareconstants(f, g);
% order the constants f and g
  if f = g then 'equal
  else if domainp f then
    if domainp g then      % Assumption of ints
      if nil2zero f < nil2zero g then 'less else 'greater
    else 'less
  else if domainp g then 'greater
  else begin scalar wukord!*, wuvarlist!*, answ;
    if symbollessp(mvar f, mvar g) then return 'less
    else if symbollessp(mvar g, mvar f) then return 'greater
    else answ := totalcompareconstants(lc f, lc g);
    if answ neq 'equal then return answ;
    return totalcompareconstants(red f, red g)
  end;

procedure wusort polylist;
% sort a list of polys into Wu order
  sort(polylist, 'wulessp!*);

procedure collectvars polylist;
% make a list of the variables appearing in the list of polys
begin scalar varlist;
  varlist := for each poly in polylist conc collectpolyvars poly;
  return sort(union(varlist, nil), 'symbollessp)
end;

procedure collectpolyvars poly;
  collectpolyvarsaux(poly, nil);

procedure collectpolyvarsaux(poly, sofar);
  if domainp poly then sofar
  else union(
         union(sofar, list mvar poly),
         union(collectpolyvarsaux(lc poly, nil),
               collectpolyvarsaux(red poly, nil)));

procedure pickbasicset polylist;
% find a basic set from the ordered list of polys
begin scalar basicset;
  foreach var in wuvarlist!* do <<
    while polylist and symbollessp(mvar car polylist, var) do
      polylist := cdr polylist;
    while polylist and var = mvar car polylist and
          not wureducedpolysp(car polylist, basicset) do
      polylist := cdr polylist;
    if polylist and var = mvar car polylist then <<
      basicset := car polylist . basicset;
      polylist := cdr polylist
    >>
  >>;
  return reversip basicset
end;

procedure wupseudodivide(f, g, x);
% not a true pseudo divide---multiply f by the smallest power
% of lc g necessary to make a fraction-free division
begin scalar origf, oldkord, lcoeff, degf, degg, answ, fudge;
  origf := f;
  oldkord := setkorder list x;
  f := reorder f;
  if wuconstantp f or mvar f neq x then <<
    setkorder oldkord;
    return nil . origf
  >>;
  g := reorder g;
  if wuconstantp g or mvar g neq x then <<
    f := multf(f, quotf(g, gcdf!*(lc f, g)));
    setkorder oldkord;
    return reorder f . nil
  >>;
  degf := ldeg f;
  degg := ldeg g;
  if degf - degg + 1 < 0 then <<
    setkorder oldkord;
    return nil . origf
  >>;
  lcoeff := lc g;
  lcoeff := exptf(lcoeff, degf - degg + 1);
  answ := qremf(multf(lcoeff, f), g);
  fudge := gcdf!*(gcdf!*(lcoeff, cdr answ), car answ);
  answ := quotf(car answ, fudge) . quotf(cdr answ, fudge);
  setkorder oldkord;
  return reorder car answ . reorder cdr answ;
end;

procedure simpwupseudodivide u;
begin scalar f, g, x, answ;
  f := !*a2f car u;
  g := !*a2f cadr u;
  x := if cddr u then !*a2k caddr u else mvar f;
  answ := wupseudodivide(f, g, x);
  return list('list, mk!*sq !*f2q car answ,
                     mk!*sq !*f2q cdr answ)
end;

put('wudiv, 'psopfn, 'simpwupseudodivide);

procedure findremainder(f, polylist);
% form the Wu-remainder of f wrt those polys in polylist
<< foreach poly in polylist do
     f := cdr wupseudodivide(f, poly, mvar poly);
   f
>>;

procedure prin2t!* u;
% a useful procedure
<< prin2!* u;
   terpri!* t
>>;

procedure chrstrem polylist;
% polylist a list of polynomials, to be Wu'd
% horrible circumlocutions here
begin scalar revbasicset, pols, rem, remainders;

  if !*trwu or !*trchrstrem then <<
    terpri!* t;
    prin2t!* "--------------------------------------------------------";
  >>;

  repeat <<
    polylist := wusort polylist;

    if !*trwu or !*trchrstrem then <<
      prin2t!* "The new pol-set in ascending order is";
      foreach poly in polylist do printsf poly;
      terpri!* t;
    >>;

    if wuconstantp car polylist then <<
      if !*trwu then prin2t!* "which is trivially trivial";
      remainders := 'inconsistent;
      revbasicset := list 1;
    >>
    else <<
      remainders := nil;
      % Keep in reverse order.
      revbasicset := reversip pickbasicset polylist;
    >>;

    if !*trwu and null remainders then <<
      prin2t!* "A basic set is";
      foreach poly in reverse revbasicset do printsf poly;
      terpri!* t;
    >>;

    pols := setdiff(polylist, revbasicset);
    foreach poly in pols do
      if remainders neq 'inconsistent then <<

        if !*trwu then <<
          prin2!* "The remainder of ";
          printsf poly;
          prin2!* "wrt the basic set is "
        >>;

        rem := findremainder(poly, revbasicset);

        if !*trwu then <<
          printsf rem;
        >>;

        if rem then
          if wuconstantp rem then <<
            remainders := 'inconsistent;

            if !*trwu then <<
              prin2t "which is a non-zero constant, and so";
              prin2t "the equations are inconsistent."
            >>

          >>
          else remainders := union(list absf rem, remainders);
      >>;
    if remainders and remainders neq 'inconsistent then
      polylist := append(polylist, remainders)
  >> until null remainders or remainders = 'inconsistent;

  if remainders = 'inconsistent then revbasicset := list 1;

  if !*trwu or !*trchrstrem then <<
    terpri!* t;terpri!* t;
    prin2t!* "The final characteristic set is:";
    foreach poly in reverse revbasicset do printsf poly
  >>;

  return reversip foreach poly in revbasicset collect absf poly
end;

procedure simpchrstrem u;
begin scalar answ, polylist, wuvarlist!*;
  polylist := foreach f in u collect !*a2f f;
  wuvarlist!* := colectvars polylist;
  answ := chrstrem polylist;
  return 'list . foreach f in answ collect mk!*sq !*f2q f;
end;

put('chrstrem, 'psopfn, 'simpchrstrem);

procedure wu(polylist, varlist);
% Do the Wu algorithm.
% Vars in varlist arranged in increasing order.
% Return (((poly, poly, ... ) . initial) ... ), a list of characteristic
% sets dotted onto the product of their initials.
% Very parallelizable.
  begin scalar stufftodo, answ, polset, chrset, initialset, initial,
        wuvarlist!*;
  stufftodo := list delete(nil,
                       union(foreach poly in polylist collect absf poly,
                             nil));
  if null car stufftodo then <<
    if !*trwu then prin2t!* "trivial CHS";
    return list(list nil . 1);
  >>;
  if null varlist then <<
    if !*trwu then prin2t!* "trivial CHS";
    return list(list 1 . 1);
  >>;
  wuvarlist!* := varlist;
  while stufftodo do <<
     polset := wusort car stufftodo;
     stufftodo := cdr stufftodo;
     chrset := chrstrem polset;
     if chrset neq '(1) then <<
       initialset := foreach pol in chrset collect wuinitial pol;
       initial := 1;
       foreach pol in initialset do initial := multf(initial, pol);

       if !*trwu then <<
         prin2!* "with initial ";
         printsf initial;
       >>;

       if member(initial, chrset) then <<
         if !*trwu then prin2t!*
           "which we discard, as the initial is a member of the CHS";
       >>
       else answ := union(list(chrset . initial), answ);

       foreach initial in initialset do
         if not wuconstantp initial then <<
           if member(initial, polset) then <<
             prin2t!*
            "*** Something awry: the initial is a member of the polset";
             answ := union(list(polset . 1), answ) % unsure of this one.
           >>
           else stufftodo := union(list wusort(initial . polset),
                                   stufftodo)
         >>
     >>
  >>;
  if null answ then answ := list(list 1 . 1);

  if !*trwu then <<
    terpri!* t;terpri!* t;
    prin2t!* "--------------------------------------------------------";
    prin2t!* "Final result:";
    foreach zset in answ do <<
      prin2t!* "Ascending set";
      foreach f in car zset do printsf f;
      prin2!* "with initial ";
      printsf cdr zset;
      terpri!* t
    >>
  >>;

  return answ;
end;

procedure simpwu u;
% rebind kord* to reflect the wu order of kernels
begin scalar pols, vars, oldkord, answ, nargs;
  nargs := length u;
  if nargs = 0 or nargs > 2 then
    rederr "Wu called with wrong number of arguments";

  pols := aeval car u;
  if nargs = 2 then vars := aeval cadr u;
  if (nargs = 1 and not eqcar(pols, 'list)) or
     (nargs = 2 and not eqcar(vars, 'list)) then
    rederr "Wu: syntax wu({poly, ...}) or wu({poly, ...}, {var, ...})";

  oldkord := kord!*;

  if nargs = 1 then
    begin scalar kord!*, polset, vars;
      kord!* := if wukord!* then reverse wukord!* else oldkord;
      polset := foreach f in cdr pols collect reorder !*a2f f;
      vars := collectvars polset;

      if !*trwu then <<
        terpri!* t;
        prin2!* "Wu variables in decreasing order: ";
        foreach id in reverse vars do <<
          prin2!* id;
          prin2!* " "
        >>;
        terpri!* t
      >>;

      answ := wu(polset, vars)
    end
  else     % nargs = 2
    begin scalar kord!*, polset, wukord!*;
      kord!* := foreach k in cdr vars collect !*a2k k;
      wukord!* := reverse kord!*;
      polset := foreach f in cdr pols collect reorder !*a2f f;
      answ := wu(polset, wukord!*)
    end;

    return 'list . foreach zset in answ collect
             'list . list('list . foreach f in car zset collect
                                  mk!*sq !*f2q absf reorder f,
                          mk!*sq !*f2q absf reorder cdr zset)
end;

put('wu, 'psopfn, 'simpwu);
remprop('wu, 'number!-of!-args);

%procedure wukord u;
%% hack to specify order of kernels in Wu
%% wukord a,y,x => other kernels < a < y < x
%  wukord!* := if u = '(nil) then nil
%              else foreach x in u collect !*a2k x;
%
%rlistat '(wukord);

algebraic;

endmodule;

end;
