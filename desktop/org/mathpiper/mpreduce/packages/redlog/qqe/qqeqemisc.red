% ----------------------------------------------------------------------
% $Id: qqeqemisc.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2005-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(qqe_qemisc_rcsid!* qqe_qemisc_copyright!*);
   qqe_qemisc_rcsid!* :=
      "$Id: qqeqemisc.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   qqe_qemisc_copyright!* := "Copyright (c) 2005-2009 A. Dolzmann and T. Sturm"
>>;
 
module qqeqemisc;
% Quantifier elimination for queues miscellaneous helping functions.

% do i have to list them a second time here!?! as these are fluids of
% module qqeqe.red and qqeqemisc is only submodule of that. !!!
fluid '(qqe_resf!*  % list of atomic formulas not containing the 
                       % bounded variable -qvar-, which actually has
                       % to be eliminated
   qqe_qvarf!*      % list of atomic formulas containing qvar
   qqe_atf!-qequal!-ext!-c!* % list of atomic formulas of form: q == p
   qqe_atf!-qequal!-ext!-p!* % list of atomic formulas of form: Lq == p
   qqe_atf!-equal!-ext!* % list of at. formulas of form: t( ..,Lq,..) = ..
   qqe_atf!-equal!-int!* % list of atomic formulas of form: 
                         % t(...,Lq,...)=t(...,Mq,...)
   qqe_atf!-misc!-basic!* % list of atf of basic type not = or neq
   qqe_atf!-qequal!-int!*   % list of atomic formulas of form: Lq == Mq
   qqe_la!*         % left area
   qqe_ra!*         % right area
   qqe_pat!-lengths!* % list of pattern lengths
   qqe_sf!*         % formula to be quantifiereliminated
   qqe_atf!-qneq!-int!* % list of atomic formulas of form: Lq<<>>Mq
   qqe_atf!-qneq!-ext!* % list of atomic formulas of form: Lq<<>>p
   qqe_atf!-qneq!-ext!-qepsilon!* % list of atform. of form Lq <<>> qeps.
   qqe_atf!-neq!*    % list of atomic formulas of form: t(..,Lq,..) neq ..
   % qqe_atf!-neq!-int!-s!* % list of atomic formulas of form: 
                          % head(..(p)) neq head(..(p))
   qqe_var!*         % quantifier which binds qqe_var!* has to be 
                        % eliminated
   qqe_qqu!-list!*    % list of quantifiers of type queue which have to
                        % to be eliminated
   qqe_bqu!-list!*    % list of quantifiers of basic type
   % qqe_id_counter!*   % counts the number of new id's already used
   );


% sorting procedures --------------------------------------------- %

procedure qqe_insert!-resf(u);
   qqe_resf!* := u . qqe_resf!*;

procedure qqe_insert!-qvarf(u);
   qqe_qvarf!* := u . qqe_qvarf!*;
   
procedure qqe_sort!-resf!-qvarf(u, qvar);
   % queue quantifier elimination rest formula qvar formula.  [u] is a
   % formula, which is considered to be a conjunction of atomic
   % formulas, or an atomic formula. [qvar] is an atom - a variable.
   % The function sorts atomic formulas containing qvar in the list
   % [qqe_qvarf!*] and such not containing qvar in the list
   % [qqe_resf!*].
   begin scalar x;
      if qqe_op u neq 'and then x := {u}
      else x := cdr u; 
      
      while x neq nil do
      <<
         if qqe_dfs(qqe_arg2l rl_prepat car x, qvar) then
            qqe_insert!-qvarf car x
         else if qqe_dfs(qqe_arg2r rl_prepat car x, qvar) then
         <<
            qqe_insert!-qvarf {caar x,qqe_arg2r car x, 
               qqe_arg2l car x};
         >>
         else 
         <<
            qqe_insert!-resf car x;
         >>;
         x := cdr x;
      >>;
   end;

procedure qqe_atf!-qequal!-ext!-c(x,qvar);
   % Queue quantifier elimination atomic formula with qequal to an
   % external queue -that means: not a part queue of qvar- complete,
   % in the sense that for qvar we have no leading prefix.
   (qqe_op x eq 'qequal) and (atom qqe_arg2l x) 
      and not (qqe_dfs(qqe_arg2r x, qvar));

procedure qqe_atf!-qequal!-ext!-p(x, qvar);
  % Queue quantifier elimination atomic formula with qequal to an
  % external element - that means: not a part queue of qvar- ,
  % partial, in the sense that for qvar we have a leading prefix. 
  (qqe_op x eq 'qequal) and not (atom qqe_arg2l x) 
     and (qqe_qoptailp car qqe_arg2l x)
        and not (qqe_dfs(qqe_arg2r x, qvar));
  
procedure qqe_atf!-equal!-ext(x, qvar);
   % Queue quantifier elimination atomic formula with equal. 
   (qqe_op x eq 'equal) and not qqe_dfs(qqe_arg2r x,qvar);

procedure qqe_atf!-equal!-int(x);
   % qqe_atf!-equal!-ext supposed to run first
   (qqe_op x eq 'equal); % and qqe_dfs(qqe_arg2r x, qvar);
 
procedure qqe_atf!-qequal!-int(x,qvar);
   % Queue quantifier elimination atomic formula with qequal to an
   % internal element - that means a part queue of qvar.
   (qqe_op x eq 'qequal) and not (atom qqe_arg2l x)
         and (qqe_qoptailp car qqe_arg2l x) 
            and (qqe_dfs(qqe_arg2l x, qvar))
               and (not atom qqe_arg2r x)
                  and (qqe_qoptailp car qqe_arg2r x)
                     and (qqe_dfs(qqe_arg2r x, qvar));
  
procedure qqe_atf!-qneq!-ext(x,qvar);
   % Queue quantifier elimination atomic formula with qneq to an external
   % element - that means: not a part queue of qvar.
   (qqe_op x eq 'qneq) and not(qqe_dfs(qqe_arg2r x,qvar)) and 
      qqe_arg2r x neq 'qepsilon;
      
procedure qqe_atf!-qneq!-ext!-qepsilon(x);
   (qqe_op x eq 'qneq) and qqe_arg2r x eq 'qepsilon;

procedure qqe_atf!-neq(x);
   % Queue quantifier elimination conjuncts with neq.
   (qqe_op x eq 'neq);
   
procedure qqe_atf!-qneq!-int(x,qvar);
   % Queue quantifier elimination atomic formula with qneq to an internal
   % element - that means a part queue of qvar. 
   (qqe_op x eq 'qneq) and (qqe_dfs(qqe_arg2r x, qvar));

procedure qqe_atf!-misc!-basic(x);
   % Queue quantifier elimination atomic formula with miscellaneous basic
   % type relation.
   not (qqe_op x memq '(equal neq qequal qneq)); 

procedure qqe_sort!-atf(u, qvar);
   % Queue quantifier elimination sort conjuncts. [u] is a formula, more
   % precisely a conjunction of atomic formulas. [qvar] is an atom, a
   % variable.  The function sorts the atomic formulas in the lists
   % [qqe_qvarf!*] and [qqe_resf!*].
   begin 
      qqe_sort!-resf!-qvarf(u,qvar);
     
      for each x in qqe_qvarf!* do
      <<
         if qqe_atf!-qequal!-ext!-c(x, qvar) 
         then qqe_atf!-qequal!-ext!-c!* := x . qqe_atf!-qequal!-ext!-c!* 
         else if qqe_atf!-qequal!-ext!-p(x, qvar)
         then qqe_atf!-qequal!-ext!-p!* := x . qqe_atf!-qequal!-ext!-p!*
         else if qqe_atf!-equal!-ext(x, qvar)
         then qqe_atf!-equal!-ext!* := x .qqe_atf!-equal!-ext!*
         else if qqe_atf!-equal!-int(x)
         then qqe_atf!-equal!-int!* := x . qqe_atf!-equal!-int!*
         else if qqe_atf!-qequal!-int(x, qvar)
         then qqe_atf!-qequal!-int!* := x . qqe_atf!-qequal!-int!*
         else if qqe_atf!-qneq!-ext!-qepsilon(x)
         then qqe_atf!-qneq!-ext!-qepsilon!* := 
            x . qqe_atf!-qneq!-ext!-qepsilon!*
         else if qqe_atf!-qneq!-ext(x, qvar)
         then qqe_atf!-qneq!-ext!* := x . qqe_atf!-qneq!-ext!*
         else if qqe_atf!-neq(x)
         then qqe_atf!-neq!* := x . qqe_atf!-neq!*
         else if qqe_atf!-qneq!-int(x, qvar)
         then qqe_atf!-qneq!-int!* := x . qqe_atf!-qneq!-int!*
         else if qqe_atf!-misc!-basic(x)
         then qqe_atf!-misc!-basic!* := x . qqe_atf!-misc!-basic!*;
         % else prin2t"sort not sorted";
      >>;
   end;

% calculation of values for different key concepts ----------------- %


% left and right area ---------------------------------------------- %
procedure qqe_la();
   % Queue quantifier elimination left area. Returns the length of the 
   % left area for the formula [qqe_sf!*]. The variable [qqe_la!*]
   % is updated.
   begin scalar la;
      la := 0;
      la := max(la,qqe_max!-lefts!-lhs(qqe_atf!-qequal!-ext!-p!*));
      la := max(la,qqe_max!-lefts!-lhs(qqe_atf!-qequal!-ext!-c!*));
      la := max(la,qqe_max!-lefts!-ls!-recursive(qqe_atf!-equal!-ext!*));
      la := max(la,qqe_max!-lefts!-ls!-recursive(qqe_atf!-equal!-int!*));
      la := max(la,qqe_max!-lefts!-bs(qqe_atf!-qequal!-int!*));
      la := max(la,qqe_max!-lefts!-bs!-recursive(qqe_atf!-neq!*));
      la := max(la,qqe_max!-lefts!-lhs(qqe_atf!-qneq!-ext!*));
      la := max(la,qqe_max!-lefts!-bs(qqe_atf!-qneq!-int!*));
      la := 
         max(la,qqe_max!-lefts!-bs!-recursive(qqe_atf!-misc!-basic!*));
      qqe_la!* := la;
      return la;
   end;

procedure qqe_ra();
   % Queue quantifier elimination right area. Returns the length of the 
   % right area for the formula [qqe_sf!*]. The variable [qqe_ra!*]
   % is updated.
   begin scalar ra;
      ra := 0;
      ra := max(ra,qqe_max!-rights!-lhs(qqe_atf!-qequal!-ext!-p!*));
      ra := max(ra,qqe_max!-rights!-lhs(qqe_atf!-qequal!-ext!-c!*));
      ra := max(ra,qqe_max!-rights!-ls!-recursive(qqe_atf!-equal!-ext!*));
      ra := max(ra,qqe_max!-rights!-bs!-recursive(qqe_atf!-equal!-int!*));
      ra := max(ra,qqe_max!-rights!-bs(qqe_atf!-qequal!-int!*));
      ra := max(ra,qqe_max!-rights!-bs!-recursive(qqe_atf!-neq!*));
      ra := max(ra,qqe_max!-rights!-lhs(qqe_atf!-qneq!-ext!*));
      ra := max(ra,qqe_max!-rights!-bs(qqe_atf!-qneq!-int!*));
      ra := 
         max(ra,qqe_max!-rights!-bs!-recursive(qqe_atf!-misc!-basic!*));
      qqe_ra!* := ra;
      return ra;
   end;

% max lefts for different types of atomic formulas ----------------- %

procedure qqe_max!-lefts!-bs!-recursive(atflist);
   % Queue quantifier elimination max lefts both sides
   % recursive. Returns maximal lefts for all terms appearing in list
   % of atomic romulas [atflist].
   begin scalar max;
      max := 0;
      if null atflist then return 0;
      for each x in atflist do
      <<
         max := max(max, max(
            qqe_max!-lefts!-term!-recursive(qqe_arg2l rl_prepat x, 0),
            qqe_max!-lefts!-term!-recursive(qqe_arg2r rl_prepat x, 0)));
      >>;
      return max;
   end;

procedure qqe_max!-lefts!-ls!-recursive(atflist);
   % Queue quantifier elimination max lefts left side
   % recursive. Returns maximal lefts for all terms appearing in list
   % of atomic formulas [atflist].
   begin scalar max;
      max := 0;
      if null atflist then return 0;
      for each x in atflist do
         max := max(max,qqe_max!-lefts!-term!-recursive 
            (qqe_arg2l rl_prepat x, 0));
      return max;
   end;

procedure qqe_max!-lefts!-term!-recursive(term, lefts);
   % Queue quantifier elimination max lefts term recursive. Make dfs
   % through [term] and return max of all lefts.
   begin scalar x;
      x := term;
      if atom x then return lefts;
      if qqe_op x memq '(lhead rhead) 
      then return max(lefts, qqe_prefix!-lefts(x));
      
      x := cdr x;
      
      while x do
      <<
         if (not atom car x) and (qqe_op car x memq '(lhead rhead)) 
            and (qqe_dfs(qqe_arg2l car x, qqe_var!*))
         then lefts := max(lefts, qqe_prefix!-lefts(car x))
         else lefts := qqe_max!-lefts!-term!-recursive(car x, lefts);
         
         x := cdr x;
      >>;
      return lefts;
   end;
         

procedure qqe_max!-lefts!-lhs(u);
   % Queue quantifier elimination max length of left prefix in lhs. 
   % [u] is a formula in lisp prefix.
   begin scalar l,x;
      if null u then x := nil
      else x := u;

      l := 0;

      while x neq nil do
      <<
         l := max(l,qqe_prefix!-lefts(qqe_arg2l rl_prepat car x));
         x := cdr x;
      >>;
      
      return l;
   end;

procedure qqe_max!-lefts!-bs(u);
   % Queue quantifier elimination max length of left prefix considering 
   % both sides of the formula. 
   % [u] is a formula in lisp prefix.
   begin scalar l,x;
      if null u then x := nil
      else x := u;
 
      l := 0;
 
      while x neq nil do
      <<
         l := max(l,max(qqe_prefix!-lefts(
            qqe_arg2l rl_prepat car x), 
            qqe_prefix!-lefts(qqe_arg2r rl_prepat car x)));
         x := cdr x;
      >>;
      
      return l;
   end;

procedure qqe_max!-rights!-lhs(u);
   % Queue quantifier elimination max length of rights prefix in lhs. 
   % [u] is a formula in lisp prefix.
   begin scalar l,x;
      if null u then x := nil
      else x := u;
 
      l := 0;
 
      while x neq nil do
      <<
         l := max(l,qqe_prefix!-rights(qqe_arg2l rl_prepat car x));
         x := cdr x;
      >>;
      
      return l;
   end;

procedure qqe_max!-rights!-bs(u);
   % Queue quantifier elimination max length of rights in prefix
   % considering both sides of the formula.  [u] is a formula in lisp
   % prefix.
   begin scalar l,x;
      if null u then x := nil
      else x := u;
 
      l := 0;
 
      while x neq nil do
      <<
         l := max(l,max(qqe_prefix!-rights(
            qqe_arg2l rl_prepat car x), 
            qqe_prefix!-rights(qqe_arg2r rl_prepat car x)));
         x := cdr x;
      >>;
      
      return l;
   end;

procedure qqe_max!-rights!-bs!-recursive(atflist);
   % Queue quantifier elimination max length of rights in prefix
   % considering both sides of the formula recursively. Returns
   % maximal rights for all terms appearing in list of atomic formulas
   % [atflist].
   begin scalar max;
      if null atflist then return 0;
      max := 0;
      for each x in atflist do
      <<
         max(max,max(qqe_max!-rights!-term!-recursive(
            qqe_arg2l rl_prepat x, 0),
            qqe_max!-rights!-term!-recursive(qqe_arg2r rl_prepat x, 0)));
      >>;
      return max;
   end;

procedure qqe_max!-rights!-ls!-recursive(atflist);
   % Queue quantifier elimination max length of rights in prefix
   % considering left side of the formula recursively. Returns maximal
   % rights for all terms appearing in list of atomic formulas
   % [atflist].
   begin scalar max;
      if null atflist then return 0;
      max := 0;
      for each x in atflist do
      <<
         max := max(max,qqe_max!-rights!-term!-recursive(
            qqe_arg2l rl_prepat x, 0));
      >>;
      return max;
   end;

procedure qqe_max!-rights!-term!-recursive(term, rights);
   % Queue quantifier elimination max length of rights in prefix
   % appearing in term recursively.  [term] is a term in lisp prefix.
   begin scalar x;
      x := term;
      if atom x then return rights;
      if qqe_op x memq '(lhead rhead) 
      then return max(rights, qqe_prefix!-rights(x));
      
      x := cdr x;
      
      while x do
      <<
         if (not atom car x) and 
            (qqe_op car x memq '(lhead rhead)) and
            (qqe_dfs(qqe_arg2l car x, qqe_var!*))
         then rights := max(rights, qqe_prefix!-rights(car x))
         else rights := qqe_max!-rights!-term!-recursive(car x, rights);
         
         x := cdr x;
      >>;
      return rights;
   end;
         
procedure qqe_max!-prefix!-length!-term!-recursive(term, l);
   % Queue quantifier elimination max prefix length in term recursively.
   %  [term] is a term in lisp prefix. -- obsolete !?
    begin % scalar x;
      % x := term;
      if atom term then return l;
      if qqe_op term memq '(lhead rhead)
      then return max(l, qqe_prefix!-length term);

      for each x in cdr term do
      <<
         if (not atom x) and (qqe_op x memq '(lhead rhead))
            and (qqe_dfs(qqe_arg2l x, qqe_var!*))
         then l := max(l, qqe_prefix!-length x)
         else qqe_max!-prefix!-length!-term!-recursive(x, l);
      >>;
      return l;
   end;

procedure qqe_patpos(at, patlength, pos);
   % QQE pattern position. [at] is a atomic formula. [patlength] is a
   % natural number denoting the length of the pattern represented
   % with [at]. [pos] is a positive integer.
   begin scalar l, rem;
      l := min(qqe_prefix!-lefts(qqe_arg2l at), 
         qqe_prefix!-lefts(qqe_arg2r at));
      rem := remainder(pos-l, patlength);
      if pos < l then return -1
      else if rem eq 0 then return patlength
      else return rem;
   end;
   

procedure qqe_pat!-lengths(l);
   % Queue quantifier elimination pattern lengths.[l] is an integer.
   % Returns list of integers, representing the pattern lengths 
   % according to [l] and the list of patterns [qqe_atf!-qequal!-int!*].
   begin scalar list, x;
      x := qqe_atf!-qequal!-int!*;
      if null x then x := qqe_atf!-qneq!-int!*;
      if null x then return nil;

      list := {qqe_pat!-length(car x,l)};
      x := cdr x;
      while x do
      <<
         list := append(list, {qqe_pat!-length(car x,l)});
         x := cdr x;
      >>;    
      x := qqe_atf!-qneq!-int!*;
      if null x then return list;
      while x do
      <<
         list := append(list, {qqe_pat!-length(car x,l)});
         x := cdr x;
      >>;
      return list;
   end;
         

procedure qqe_pat!-length(pat,l);
   % Queue quantifier elimination pattern length. [pat] is a pattern in 
   % the form of an atomic formula in lisp prefix. [l] is an integer.
   % Returns the length of pattern [pat] according to queue length [l].
   begin scalar a, prepat;
      prepat := rl_prepat pat;
      a := qqe_prefix!-length(qqe_arg2l prepat);
      if a < l then
      <<
         if (max(qqe_prefix!-lefts(qqe_arg2l prepat), 
            qqe_prefix!-lefts(qqe_arg2r prepat))) <=
               (l - max(qqe_prefix!-rights(qqe_arg2l prepat), 
                  qqe_prefix!-rights(qqe_arg2r prepat)))
         then return
            abs(qqe_prefix!-lefts(qqe_arg2l prepat) -
               qqe_prefix!-lefts(qqe_arg2r prepat))
         else return l - a;
      >>  

      else return 0;
      
   end;
  
procedure qqe_get!-quantifier!-sequenz(u);
   % returns list of quantifiers and the quantifierfree part of formula
   % [u].  works for pnf.  returns list (y,x) with y is list of
   % quantifiers and x is a quantifier free formula.
   begin scalar x,y;
      
      if atom u then return {nil,nil};
      
      x := u;
      
      while car x memq '(ex all) do
      << %!!!
         y := append(y, {{car x, cadr x}});
         if get(cadr x,'idtype) eq 'qt then 
            qqe_qqu!-list!* := 
               lto_insertq({car x, cadr x},qqe_qqu!-list!*)
         else  
         qqe_bqu!-list!* := lto_insertq({car x, cadr x} ,qqe_bqu!-list!*);
         x := caddr x;
      >>;
   
      return {y,x};
   end;

procedure qqe_atf!-qequal!-ext!-min!-prefix();
   % QQE atomic formula qequal external minimal prefix. Returns [(m
   % term)], where [term] is the term with minimal prefix appearing on
   % lhs of atomic formulas in [qqe_atf!-qequal!-ext!*] and [m] is a
   % natural number denoting the prefix length of [term].
   begin scalar m, mm, term, list;
      
      m := qqe_prefix!-length qqe_arg2l car qqe_atf!-qequal!-ext!-p!*;
      term := car qqe_atf!-qequal!-ext!-p!*;
      list := cdr qqe_atf!-qequal!-ext!-p!*;
      if list then
      <<
         for each x in list do
         <<
            mm := qqe_prefix!-length qqe_arg2l x;
            if mm < m
            then <<
               m := mm;
               term := x;
            >>;
         >>;
      >>;
      return {m, term};
   end;

procedure qqe_atf!-qequal!-ext!-p!-min!-lefts(minlength, minpref);
   % Queue quantifier elimination conjuncts qequal external partial 
   % minimum of left prefixes.
   begin scalar m,mm, term, list, l;
      l := qqe_prefix!-lefts qqe_arg2l minpref;
      m := qqe_prefix!-lefts qqe_arg2l minpref;
      term := minpref;
      % m := qqe_prefix!-lefts qqe_arg2l car qqe_atf!-qequal!-ext!-p!*;
      % term := car qqe_atf!-qequal!-ext!-p!*;
      list := qqe_atf!-qequal!-ext!-p!*;
      if list then
      <<
         for each x in list do
         <<
            mm := qqe_prefix!-lefts qqe_arg2l x;
            if (mm < m) and (minlength >= l + 
               qqe_prefix!-rights qqe_arg2l x)
            then <<
               m := mm;
               term := x;
            >>;
         >>;
      >>;
      return {m,term};
   end;

procedure qqe_atf!-qequal!-ext!-p!-min!-rights(minlength, minpref);
   % Queue quantifier elimination conjuncts qequal external partial 
   % minimum of right prefixes.
   begin scalar m,mm,term, list, r;
      r := qqe_prefix!-rights qqe_arg2l minpref;
      m := qqe_prefix!-rights qqe_arg2l minpref;
      term := minpref;
      % qqe_prefix!-rights qqe_arg2l car qqe_atf!-qequal!-ext!-p!*;
      % temp := car qqe_atf!-qequal!-ext!-p!*;
      list := qqe_atf!-qequal!-ext!-p!*;
      for each x in list do
      <<
         mm := qqe_prefix!-rights qqe_arg2l x;
         if (mm < m) and (minlength >= r + 
            qqe_prefix!-lefts qqe_arg2l x)
         then
         <<
            m := mm;
            term := x;
         >>;
      >>;
      return {m,term};
   end;



procedure qqe_new!-ids!-x(num, u);
   % Queue quantifier elimination make new identifiers. [num] is
   % an integer. Function returns a list of [num] man identifiers,
   % which are not yet in formula [qqe_sf!*]. The list
   % has the form ( x1 x2 x3 x4...).
   begin scalar j,jj, x, idlist;
      j := 1;
      jj := 1;
      while j <= num do
      <<
         if (null smemq(x := qqe_make!-id!-x(jj), u)) then
         <<
            j := j+1;
            jj := jj+1;
            if idlist then idlist := append(idlist, {x})
            else idlist := {x};
         >>
         else 
         <<
            jj := jj+1;
         >>;
      >>;
      return idlist;
   end;

procedure qqe_make!-id!-x(num);
   % Queue quantifier elimination make new identifier x[num].
   % [num] is an integer. Function returns the identifier
   % x[num].
   intern compress append({cadr a},cdddr a) 
      where a=explode{'x,num};

procedure qqe_list!-take!-n(list,length,n);
   % QQE list take list of length of list minus n elements of list
   % beginning from left. [list] is a list. [length] is the length of
   % the list. [n] is a natural number.
   begin
      for i:=1:length-n do
         list := cdr list;
      return list;
   end;

% QQE substitution procedures -------------------------------------- %

procedure qqe_subst(num,new_ids);
   % QQE substitute. [num] is a natural number. [new_ids] is a list of
   % identifiers. Return [qqe_sf!*] substituted by |x1 ... xn|, where
   % list is (x1 ... xn). Returns equivalent formula to the
   % substitution result, for which variable xi only appear in basic
   % type atomic formulas.
   begin scalar f;
      if num eq 0 then return subst('qepsilon,qqe_var!*, qqe_sf!*)
      else <<
         if qqe_atf!-qequal!-ext!-p!* then 
            f := qqe_subst!-qequal!-ext!-p(num,new_ids) . f;
         if qqe_atf!-equal!-ext!* then 
            f := qqe_subst!-batf(num,new_ids,qqe_atf!-equal!-ext!*) . f;
         if qqe_atf!-equal!-int!* then 
            f := qqe_subst!-batf(num,new_ids,qqe_atf!-equal!-int!*) . f;
         if qqe_atf!-qequal!-int!* then  
            f := qqe_subst!-qequal!-int(num,new_ids) . f;
         if qqe_atf!-qneq!-ext!* then  
            f := qqe_subst!-qneq!-ext(num,new_ids) . f;
         if qqe_atf!-qneq!-ext!-qepsilon!* then
            f := 'true . f;
         if qqe_atf!-neq!* then 
            f := qqe_subst!-batf(num,new_ids,qqe_atf!-neq!*) . f;
         if qqe_atf!-qneq!-int!* then  % !!! <- here we get or! 
            f := qqe_subst!-qneq!-int(num, new_ids) . f;
         if qqe_atf!-misc!-basic!* then 
            f := qqe_subst!-batf(num,new_ids,qqe_atf!-misc!-basic!*) . f;
      >>;
      
      if cdr qqe_qvarf!* then f := 'and . f else f := car f;
      
      return f;
   end;

procedure qqe_subst!-batf(num,new_ids,atlist);
  % QQE substitution basic type atomic formula. [num] is a natural
  % number. [new_ids] is a list of identifiers. [atlist] is a list of
  % atomic formulas. Substitute [qqe_var!*] with |x_1
  % ... x_num|. Transform the result in a equivalent form, where x_i
  % only occur in basic type atomic formulas.
  begin scalar list;
     for each f in atlist do <<
        f := rl_prepat f;
        list := rl_simpat {qqe_op f, 
           qqe_subst!-bterm(num,new_ids, qqe_arg2l f),
        qqe_subst!-bterm(num,new_ids, qqe_arg2r f)} . list;
     >>;
     return if cdr atlist then 'and . list else car list;
  end;

procedure qqe_subst!-bterm(num,new_ids,term);
   % QQE substitution basic type term. [num] is a natural
   % number. [new_ids] is a list of identifiers. Substitute
   % [qqe_var!*] with |x_1 ... x_num|.
   begin scalar term3;
      if null term or atom term then return term
      else if qqe_op term memq '(lhead rhead) and 
         qqe_qprefix!-var term eq qqe_var!* then <<
         return qqe_subst!-simplterm(term,num,new_ids);
      >>
      else for each term2 in cdr term do 
         term3 := append(term3,{qqe_subst!-bterm(num,new_ids,term2)});
      return car term . term3;
   end;
                     
procedure qqe_subst!-qequal!-ext!-p(num,new_ids);
   % QQE substitution basic type atomic formula for atomic formulas in
   % [qqe_atf!-qequal!-ext!-p]. [num] is a natural number. [new_ids]
   % is a list of identifiers. Substitute [qqe_var!*] with |x_1
   % ... x_num|. Transform the result in a equivalent form, where x_i
   % only occur in basic type atomic formulas.
   begin scalar list, g;
      for each f in qqe_atf!-qequal!-ext!-p!* do <<
         list := qqe_subst!-simplterm(qqe_arg2l f,num,new_ids);
         if null list then g := {'qequal,qqe_arg2r f,'qepsilon} . g
         else g := qqe_makef!-termlength!-l(qqe_arg2r f, num) . 
            qqe_makef!-termlength!-g(qqe_arg2r f, num-1) . 
               qqe_makef!-qequal2equal(qqe_arg2r f, list) . g;
      >>;
      % return g;
      %return if cdr g then g else car g;
      return if cdr qqe_atf!-qequal!-ext!-p!* then 'and . g else car g;
   end;

procedure qqe_subst!-qneq!-ext(num,new_ids);
   % QQE substitution basic type atomic formula for atomic formulas in
   % [qqe_atf!-qneq!-ext!]. [num] is a natural number. [new_ids]
   % is a list of identifiers. Substitute [qqe_var!*] with |x_1
   % ... x_num|. Transform the result in a equivalent form, where x_i
   % only occur in basic type atomic formulas.
    begin scalar list, g;
       for each f in qqe_atf!-qneq!-ext!* do <<
          list := qqe_subst!-simplterm(qqe_arg2l f,num,new_ids);
          
          if null list then g := {'qneq,qqe_arg2r f,'qepsilon} . g
          else g := {'or, qqe_makef!-termlength!-g(qqe_arg2r f, num+1),
             qqe_makef!-termlength!-l(qqe_arg2r f, num-1), 
             {'and, qqe_makef!-qneq2equal(qqe_arg2r f, list), 
                qqe_makef!-termlength!-l(qqe_arg2r f, num),
                qqe_makef!-termlength!-g(qqe_arg2r f, num-1)}} . g;
       >>;
       return if cdr qqe_atf!-qneq!-ext!* then 'and . g else car g;
       % return if cdr g then g else car g;
    end;

procedure qqe_makef!-qneq2equal(term, list);
   % QQE make formula: transform atomic formula with qneq into atomic
   % formula with neq. [term] is a queue type term. [list] is a list
   % of variables. term <<>> |x1 ... xn| is transformed in
   % bigvee_{i=1}^n xi <> lhead ltail^{i-1} term.
   begin scalar f, tail, list2;
      list2 := list;
      tail := term;
      while list2 do <<
         f := rl_simpat({'neq,'lhead . {tail}, car list2}) . f;
         tail := 'ltail . {tail};
         list2 := cdr list2;
      >>;
      if null cdr list then return car f
      else return 'or . f;
   end;

procedure qqe_subst!-qneq!-int(num, new_ids);
   % QQE substitution basic type atomic formula for atomic formulas in
   % [qqe_atf!-qneq!-int!]. [num] is a natural number. [new_ids]
   % is a list of identifiers. Substitute [qqe_var!*] with |x_1
   % ... x_num|. Transform the result in a equivalent form, where x_i
   % only occur in basic type atomic formulas.
   begin scalar list1, list2, g;
      for each f in qqe_atf!-qneq!-int!* do <<
         list1 := qqe_subst!-simplterm(qqe_arg2l f,num, new_ids);
         list2 := qqe_subst!-simplterm(qqe_arg2r f,num, new_ids);
         if length(list1) neq length(list2) then g := 'true . g
         else if null list1 then g := 'false . g
         else g := qqe_makef!-list2qneq(list1,list2) .  g;
      >>;
      return if cdr qqe_atf!-qneq!-int!* then 'and . g else car g;
   end;

procedure qqe_subst!-qequal!-int(num,new_ids);
   % QQE substitution basic type atomic formula for atomic formulas in
   % [qqe_atf!-qequal!-int!]. [num] is a natural number. [new_ids]
   % is a list of identifiers. Substitute [qqe_var!*] with |x_1
   % ... x_num|. Transform the result in a equivalent form, where x_i
   % only occur in basic type atomic formulas.
   begin scalar list1, list2, g, list, f;
      list := qqe_atf!-qequal!-int!*;
      while list and g neq 'false do <<
         % for each f in qqe_atf!-qequal!-int!* do <<
         f := car list;
         list1 := qqe_subst!-simplterm(qqe_arg2l f,num,new_ids);
         list2 := qqe_subst!-simplterm(qqe_arg2r f,num,new_ids);
         if null list1 and null list2 then g := 'true . g
         else if null list1 or null list2 then g := 'false %%shortcut!!!
         else if qqe_prefix!-length(qqe_arg2l f) neq 
            qqe_prefix!-length(qqe_arg2r f) then g := 'false
         else g := qqe_makef!-list2equal(list1,list2) . g;
         list := cdr list;
      >>;

      return if cdr qqe_atf!-qequal!-int!* then 'and . g else car g;
   end;

procedure qqe_makef!-list2qneq(list1,list2);
   % QQE make formula: transform atomic formula with qneq into
   % disjunction of atomic formulas with neq. [list1], [list2] are
   % lists of variables. |y1 ... yn| <<>> |x1 ... xn| is transformed
   % in bigvee_{i=1}^n xi <> yi.
   begin scalar f, list;
      list := list1;
      while list1 do <<
         f := rl_simpat({'neq, car list1, car list2}) . f;
         list1 := cdr list1;
         list2 := cdr list2;
      >>;
      if null cdr list then return car f
      else return 'or . f;
   end;

procedure qqe_makef!-list2equal(list1, list2);
   % QQE make formula: transform atomic formula with qneq into atomic
   % formula with equal. [list1], [list2] are lists
   % of variables. |y1 ... yn| <<>> |x1 ... xn| is transformed in
   % bigvee_{i=1}^n xi <> yi.
   begin scalar f, list;
      list := list1;
      while list1 do <<
         f := rl_simpat({'equal,car list1, car list2}) . f;
         list1 := cdr list1;
         list2 := cdr list2;
      >>;

      if null cdr list then return car f
      else return 'and . f;
   end;

procedure qqe_makef!-qequal2equal(term,list);
   % QQE make formula: transform atomic formula with qequal into
   % atomic formula with equal. [term] is a queue type term. [list1],
   % [list2] are lists of variables. |y1 .. y2| <<>> |x1 ... xn| is
   % transformed in bigvee_{i=1}^n xi = yi.
   begin scalar f, tail, list2;
      tail := term;
      list2 := list;
      while list2 do <<
         f := rl_simpat({'equal,'lhead . {tail},car list2}) . f;
         tail := 'ltail . {tail};
         list2 := cdr list2;
      >>;

      if null cdr list then return car f
      else return 'and . f;
   end;

procedure qqe_makef!-termlength!-l(term,num);
   % length(term)<num ---> ltail^num(term) == qepsilon
   {'qequal, qqe_iterate!-ltail(num,term), 'qepsilon};

procedure qqe_makef!-termlength!-g(term,num);
   % length(term) > num ---> ltail^num(term) <<>> qepsilon
   {'qneq, qqe_iterate!-ltail(num,term), 'qepsilon};

procedure qqe_subst!-simplterm(old_term,num,new_ids);
   % QQE substitute and simplify term. [old_term] is a term, [num] is
   % a natural number, [new_ids] is a list of identifiers.  For a term
   % M p we return the result of the evaluation of M |x1 .. xn|.  
   % For example with new_ids=(x1 .. x5) : lhead ltail rtail p --> x2
   begin scalar l,r;
      if atom old_term then return new_ids;
      l := qqe_prefix!-lefts old_term;
      r := qqe_prefix!-rights old_term;
      
      % because we only look at harmless formulas: this case shouldn't
      % be considered. !!! (still not?!)
      % But as we don't have a perfect test for harmless formulas
      if l + r > num then if car old_term memq '(lhead rhead)then
         return {'eta}
      else return nil;

      if car old_term eq 'lhead then 
         return qqe_subst!-simplterm!-lhead(l-1,new_ids);
      if car old_term eq 'rhead then
         return qqe_subst!-simplterm!-rhead(r-1,new_ids);

      % num > l + r
      if l > 0 then
         for i:=1:l do
            new_ids := cdr new_ids;
      
      if r > 0 then <<
         new_ids := reverse new_ids;
         for i:=1:r do
            new_ids := cdr new_ids;
         new_ids := reverse new_ids;
      >>;

      return new_ids;
   end;

procedure qqe_subst!-simplterm!-lhead(lefts, new_ids);
   % QQE substitute and simplify term with leading operation being
   % lhead. [lefts] is a natural number, [new_ids]
   % is a list of identifiers.  For a term M p we return the result of
   % the evaluation of M |x1 .. xn|.  For example with new_ids=(x1
   % .. x5) : lhead ltail rtail p --> x2
   begin 
      if lefts > 0 then
         for i:=1:lefts do
            new_ids := cdr new_ids;
      return car new_ids;
   end;

procedure qqe_subst!-simplterm!-rhead(rights, new_ids);
   % QQE substitute and simplify term with leading operation being
   % rhead. [rights] is a natural number, [new_ids]
   % is a list of identifiers.  For a term M p we return the result of
   % the evaluation of M |x1 .. xn|.  For example with new_ids=(x1
   % .. x5) : rhead ltail rtail p --> x4
   begin 
      new_ids := reverse new_ids;
      if rights > 0 then
         for i:=1:rights do
            new_ids := cdr new_ids;
      return car new_ids;
   end;



procedure qqe_iterate!-quantifier!-ex(l,list, arg);
   % Queue quantifier elimination iterate quantifier existence.
   % [l] is an integer, [list] is a list of identifiers,
   % [arg] is a lisp prefix.
   % Returns ex l(1) (ex l(2) .... (ex(l(|l|)) arg))).
   begin scalar q,x;
      q := arg;
      x := list;

      for j:=1:l do
      <<
         q := {'ex, car x, q};
         x := cdr x;
      >>;
      return q;
   end;

procedure qqe_iterate!-quantifier(qlist,f);
   % QQE iterate quantifier. [qlist] is a list of pairs (q, v), where
   % q is a quantifier (ex, all) and v is a variable. [f] is a
   % formula. Returns formula with leading sequence of quantifier with
   % scopus being f. For example: qlist: ((all q) (ex p)), f: q = p
   % ---> (all q (ex p (equal q p))).
   begin
      for each x in qlist do
         f := {car x, cadr x, f};
      return f;
   end;


procedure qqe_simpl!-dnf(u);
   % QQE simplify disjunctive normalform. [u] is a dnf.  Simplify [u]
   % with help of length graph. The function is mainly needed at the
   % end of the QE procedure. 
   begin scalar list, clauses, length_list;
      
      if car u eq 'or then <<
         list := cdr u;
         
         for each clause in list do <<
            length_list := qqe_clause!-update!-lengths(clause,nil);
            if length_list eq 'false then <<
               clauses := append(clauses,{'false});
            >>
            else << 
               clauses := append(clauses,{qqe_simpl!-clause clause});
               qqe_length!-graph!-delete length_list;
            >>;
         >>;
         list := append({'or}, clauses);
      >>
      else list := qqe_simpl!-clause u;
      
      return rl_simpl(list,nil,-1);
   end;
         

procedure qqe_simpl!-clause(u);
   % QQE simplify clause. [u] is a conjunction of atomic formulas.
   % Simplify [u] with help of length graph. The function is mainly
   % needed in the QE procedure. The length graph is considered to be
   % correct.
   begin scalar list, flag, at, atlist_rest,varlist, x;
      flag := t;
      list := if qqe_op u eq 'and then cdr u else {u};
      while flag and list do <<
         at := car list;
        
         if pairp at and qqe_op at memq '(qequal qneq) then <<
            at := {qqe_op at, 
               qqe_simpl!-clause!-term qqe_simplterm qqe_arg2l at,
               qqe_simpl!-clause!-term qqe_simplterm qqe_arg2r at};
            if qqe_qprefix!-var qqe_arg2l at eq 'qepsilon or
               qqe_qprefix!-var qqe_arg2r at eq 'qepsilon then <<
                  if qqe_op at eq 'qequal then
                     x := qqe_simpl!-clause!-qequal(at)
                  else 
                     x := qqe_simpl!-clause!-qneq(at);
                  if null x then flag := nil
                  else if x neq 'true then <<
                     atlist_rest := at  . atlist_rest; 
                     if x neq t then
                        varlist := x . varlist;
                  >>;
               >>
            else atlist_rest := at . atlist_rest;
         >>
         else atlist_rest := at . atlist_rest;
         list := cdr list;
      >>;
      
      qqe_simpl!-clause!-remprop varlist;

      if flag then <<
         if null atlist_rest then return 'true
         else if null cdr atlist_rest then return car atlist_rest
         else return 'and . atlist_rest;
      >>
      else return 'false;
   end;

procedure qqe_simpl!-clause!-remprop(varlist);
   % QQE simplify clause remove properties from list of variables 
   % [varlist].
   for each x in varlist do <<
      remprop(x,'qqeqemisceq);
      remprop(x,'qqeqemiscneq);
   >>;

procedure qqe_simpl!-clause!-qequal(at);
   % QQE simplify clause qequal. [at] is an atomic formula.  this is a
   % subroutine of [qqe_simpl!-clause] for atomic formulas with
   % [qequal].
   begin scalar var;
      if (qqe_arg2l at eq 'qepsilon) and 
         (qqe_arg2r at eq 'qepsilon) 
      then  return 'true
      
      else if (qqe_arg2l at eq 'qepsilon) and
         (qqe_qprefix!-var qqe_arg2r at eq 'qepsilon) and
         (qqe_number!-of!-adds!-in!-qterm qqe_arg2r at <= 
            qqe_number!-of!-tails!-in!-qterm qqe_arg2r at) 
      then return 'true

      else if (qqe_arg2r at eq 'qepsilon) and
               (qqe_qprefix!-var qqe_arg2l at eq 'qepsilon) and 
               (qqe_number!-of!-adds!-in!-qterm qqe_arg2l at <=
                  qqe_number!-of!-tails!-in!-qterm qqe_arg2l at) 
      then return 'true
      
      else if (qqe_arg2l at eq 'qepsilon) and 
         (qqe_number!-of!-adds!-in!-qterm qqe_arg2r at = 0) then <<
         var := qqe_qprefix!-var qqe_arg2r at;
         if null get(var,'qqeqemisceq) and 
            qqe_lesseq!-length(qqe_prefix!-length qqe_arg2r at,
               qqe_maxlength!-var var) then <<
                  put(var, 'qqeqemisceq, t);
                  return var;
               >>
         else return 'true;
      >>
        
      else if (qqe_arg2r at eq 'qepsilon) and
         (qqe_number!-of!-adds!-in!-qterm qqe_arg2l at = 0) then <<
            var := qqe_qprefix!-var qqe_arg2l at;
            if null get(var,'qqeqemisceq) and 
               qqe_lesseq!-length(qqe_prefix!-length qqe_arg2l at,
                  qqe_maxlength!-var var) then <<
                     put(var,'qqeqemisceq,t);
                     return var;
                  >>
            else return 'true;
         >>
      else return t;
   end;

procedure qqe_simpl!-clause!-qneq(at);
    % QQE simplify clause qneq. [at] is an atomic formula.  this is a
   % subroutine of [qqe_simpl!-clause] for atomic formulas with
   % [qneq].
   begin scalar var;
      if (qqe_arg2l at eq 'qepsilon) and 
         (qqe_arg2r at eq 'qepsilon) 
      then return nil
      
      else if (qqe_arg2l at eq 'qepsilon) and
         (qqe_qprefix!-var qqe_arg2r at eq 'qepsilon) and
         (qqe_number!-of!-adds!-in!-qterm qqe_arg2r at <= 
            qqe_number!-of!-tails!-in!-qterm qqe_arg2r at) 
      then return nil

      else if (qqe_arg2r at eq 'qepsilon) and
               (qqe_qprefix!-var qqe_arg2l at eq 'qepsilon) and 
               (qqe_number!-of!-adds!-in!-qterm qqe_arg2l at <=
                  qqe_number!-of!-tails!-in!-qterm qqe_arg2l at) 
      then return nil
      
      else if (qqe_arg2l at eq 'qepsilon) and 
      (qqe_number!-of!-adds!-in!-qterm qqe_arg2r at = 0) then <<
         var := qqe_qprefix!-var qqe_arg2r at;
         if null get(var,'qqeqemiscneq) and 
            qqe_greatereq!-length(qqe_prefix!-length qqe_arg2r at+1,
               qqe_minlength!-var var) then <<
                  put(var, 'qqeqemiscneq, t);
                  return var;
               >>
         else return 'true;
      >>
        
      else if (qqe_arg2r at eq 'qepsilon) and 
      (qqe_number!-of!-adds!-in!-qterm qqe_arg2l at = 0) then <<
         var := qqe_qprefix!-var qqe_arg2l at;
         if null get(var,'qqeqemiscneq) and 
            qqe_greatereq!-length(qqe_prefix!-length qqe_arg2l at+1,
               qqe_minlength!-var var) then <<
                  put(var,'qqeqemiscneq,t);
                  return var;
               >>
         else return 'true;
      >>
      else return t;
   end;

procedure qqe_simpl!-clause!-term(term);
   % QQE simplify clause: simplify a term.  With the help of the
   % information from the length graph a term [term] of type queue is
   % simplified.
   begin scalar var, maxlength;
      if atom term then return term;
      var := qqe_qprefix!-var term;
      maxlength := get(var,'maxlength);
      if null maxlength then return term;
      if (qqe_number!-of!-tails!-in!-qterm(term) - 
         qqe_number!-of!-adds!-in!-qterm(term)) > maxlength then
            return 'qepsilon
      else return term;
   end;

% -------------------- critical point graph ---------------------------------%%%%% Rest of module : : UNDER CONSTRUCTION %%%%%%

%% procedure qqe_make!-cpg(list);
%%    % outer loop  cpg -- critical point graph
%%    begin scalar graph;
%%       list := qqe_quicksort!-dbl!-crit list;
%%       graph := qqe_cpg!-make!-root();
%%       for each x in list do 
%%          qqe_cpg!-insert(qqe_cpg!-make!-node x, graph);
%%       return graph;
%%    end;
%% 
%% procedure qqe_cpg!-make!-root();
%%    qqe_cpg!-make!-supernode();
%% 
%% procedure qqe_cpg!-make!-node(var);
%%    % incl-pointer, excl-pointer, var
%%    {nil,nil,var};
%% 
%% procedure qqe_cpg!-make!-supernode();
%%    % last-pointer, listofnodes
%%    {nil,nil, {nil}};
%% 
%% procedure qqe_cpg!-supernode!-last(snode);
%%    % TODO
%%    cadr snode;
%% 
%% procedure qqe_cpg!-supernode!-first(snode);
%%    car snode;
%% 
%% procedure qqe_cpg!-incl(node);
%%    car node;
%% 
%% procedure qqe_cpg!-excl(node);
%%    cadr node;
%% 
%% procedure qqe_cpg!-make!-incl(node);
%%    car node := qqe_cpg!-make!-supernode();
%% 
%% procedure qqe_cpg!-insert(node,graph);
%%    qqe_cpg!-insert!-incl(node,graph, 'infty);
%% 
%% procedure qqe_cpg!-make!-excl(node_in, node);
%%    cadr node := node_in;
%% 
%% procedure qqe_cpg!-empty!-graph(graph);
%%    if car graph then nil
%%    else t;
%% 
%% procedure qqe_cpg!-insert!-supernode(node_in,snode);
%%    <<
%%       if car snode then caddr snode := caddr snode . {car snode};
%%       cadr snode := node_in;
%%       if null qqe_cpg!-supernode!-first snode then 
%%          car snode := cadr snode;
%%       %%       cadr snode := append(cadr snode,{node_in});
%%       %%       car snode := node_in;
%%       prin2t{"::insert!-supernode: snode=",snode};
%%    >>;
%% 
%% procedure qqe_cpg!-minlength!-node(node);
%%    qqe_minlength!-var qqe_cpg!-var node;
%% 
%% procedure qqe_cpg!-maxlength!-node(node);
%%    qqe_maxlength!-var qqe_cpg!-var node;
%% 
%% procedure qqe_cpg!-var(node);
%%    caddr node;
%% 
%% procedure qqe_cpg!-insert!-incl(node, graph, max_border);
%%    % TODO 
%%    begin scalar minlength, maxlength, maxlength_last, last_node;
%%       prin2t "_";
%%       prin2t {":::qqe_cpg!-insert!-incl with var_node=",qqe_cpg!-var node,
%%          " and graph= ",if qqe_cpg!-supernode!-last graph then qqe_cpg!-var 
%%             qqe_cpg!-supernode!-last graph else nil,
%%          "and max_border=", max_border};
%%       minlength := qqe_cpg!-minlength!-node node;  
%%       maxlength := qqe_cpg!-maxlength!-node node;  
%%       if qqe_cpg!-empty!-graph graph then <<
%%          prin2t " branch :: emty_graph";
%%          qqe_cpg!-insert!-supernode(node,graph)
%%       >>
%% %%       else if minlength eq 0 then <<
%% %%          prin2t "branch :: minlength=0";
%% %%          qqe_cpg!-make!-incl qqe_cpg!-supernode!-first graph;
%% %%          qqe_cpg!-insert!-incl(node,qqe_cpg!-incl 
%% %%             qqe_cpg!-supernode!-first graph, max_border)
%% %%       >>
%%       else <<
%%          prin2t "branch :: elsewise";
%%          last_node := qqe_cpg!-supernode!-last graph;
%%          maxlength_last := qqe_cpg!-maxlength!-node last_node;
%%          if qqe_less!-length(maxlength_last,minlength) then <<
%%             prin2t "branch :: elsewise with no intersection in incs";
%%             qqe_cpg!-supernode!-insert(graph,node);
%%          >>
%%          else <<
%%             prin2t "branch :: elsewise intersection exists";
%%             if null qqe_cpg!-incl last_node then 
%%                qqe_cpg!-make!-incl last_node;
%%             qqe_cpg!-insert!-incl(node,qqe_cpg!-incl last_node, 
%%                qqe_min!-length(maxlength_last, max_border));
%%             if qqe_less!-length(maxlength_last, maxlength) and 
%%                qqe_less!-length(maxlength_last,max_border) then <<
%%                prin2t {"branch :: elsewise also excl part with last=", 
%%                   qqe_cpg!-var last_node};
%%                if null qqe_cpg!-excl last_node then 
%%                   qqe_cpg!-make!-excl(qqe_cpg!-make!-node qqe_cpg!-var node, 
%%                      last_node)
%%                else qqe_cpg!-insert!-excl(qqe_cpg!-make!-node 
%%                   qqe_cpg!-var node, qqe_cpg!-excl last_node, 
%%                   max_border);
%%             >>;
%% 
%%          >>;
%%       >>;
%%             
%% %%          if qqe_less_length(qqe_maxlength!-var qqe_cpg!-var 
%% %%             qqe_cpg!-most!-exclusive last_node,minlength) then
%%    end;
%% 
%% procedure qqe_cpg!-insert!-excl(node_in,node,max_border);
%%    % TODO
%%    begin scalar minlength_in, maxlength_in, minlength, maxlength;
%%       prin2t {"entering qqe_cpg!-insert!-excl with node_in =", 
%%          qqe_cpg!-var node_in, "and 
%%          node=", qqe_cpg!-var node};
%%       pause;
%%       minlength_in := qqe_cpg!-minlength!-node node_in;
%%       minlength := qqe_cpg!-minlength!-node node;
%%       maxlength_in := qqe_cpg!-maxlength!-node node_in;
%%       maxlength := qqe_cpg!-maxlength!-node node;
%%       
%%       % if qqe_less!-length(maxlength_in, maxlength) then <<
%%       if null qqe_cpg!-incl node then qqe_cpg!-make!-incl node;
%%       qqe_cpg!-insert!-incl(node_in,qqe_cpg!-incl node, max_border);
%%       % >>;
%%       if qqe_less!-length(maxlength, maxlength_in) then  <<
%%          if null qqe_cpg!-excl node then qqe_cpg!-make!-excl node;
%%          qqe_cpg!-insert!-excl(node_in, qqe_cpg!-excl node,max_border);
%%       >>;
%%    end;
%% 
%% procedure qqe_cpg!-min!-solution(start, lcm);
%%    % TODO
%%    ;
%% 
%% procedure qqe_cpg!-check!-chain(start,lcm,length_of_chain, graph);
%%    % TODO first
%%    begin scalar next, solution;
%%       if length_of_chain eq 1 then 
%%          return qqe_cpg!-check!-chain1(start,lcm,graph);
%%       while right neighbor on supernode and not solution do <<
%%          proiers mit dem;
%%          if not solution do <<
%%             while excl-child and not solution do this <<
%%                % next := qqe_cpg!-get!-next graph;
%%                % while next and not solution do
%%                solution := qqe_cpg!-check!-chain(start,lcm, 
%%             length_of_chain-1,next);
%%             >>;
%%          >>;
%%       >>;
%%       return solution;
%%    end;
%% 
%% procedure qqe_cpg!-supernode!-get!-next(snode,node);
%%    % TODO
%%    begin scalar temp;
%%       temp := qqe_cpg!-superode!-list snode;
%%       while temp and car temp neq node do 
%%          temp := cdr temp;
%%       if temp and car temp eq node then return if cdr temp then car temp 
%%          else nil
%%       else return nil;
%%    end;
%% 
%% procedure qqe_cpg!-check!-chain1(start,lcm,graph);
%%    % TODO
%%    ;
%% 
%% procedure qqe_cpg!-get!-next(list,node);
%%    % TODO
%%    begin
%%       if qqe_cpg!-incl node then 
%%          return qqe_cpg!-incl qqe_cpg!-supernode!-first node
%%       else if qqe_cpg!-supernode!-get!-next(qqe_cpg!-list!-get!-prev node, % dafuer muesste ein incl Knoten als Liste uebergeben werden
%%          then return {'e,qqe_cpg!-minlength!-node car cdr node}
%%       else qqe_cpg!-get!-next!-reiterate(list,node);
%%    end;
%% 
%% procedure qqe_cpg!-get!-next!-reiterate(list,node);
%%    begin scalar next;
%%       list := reverse list;
%%       % where do i get the father from - it must be the predecessor in the list
%%       % not always !
%%       while cdr list and null next do << 
%%          % null next sollte car next memq (nil v)
%%          % next := qqe_cpg!-get!-next!-reiterate!-check!-node(list,car list);
%%          list := cdr list;
%%          if qqe_cpg!-excl car list then 
%%             next := qqe_cpg!-get!-next!-down(list);
%%       >>;
%%       if null next then qqe_cpg!-get!-next!-max car list;
%%    end;
%% 
%% procedure qqe_cpg!-get!-down(list);
%%    % TODO
%%    begin scalar max, temp;
%%       max := qqe_get!-maxlength car list;
%%       temp := qqe_cpg!-incl car list;
%%       list := cdr list;
%%       
%%       list := for each x in list collect 
%%          if qqe_get!-maxlength car list > max  then caar list;
%% 
%%       
%%       while qqe_cpg!-incl temp and null next do
%%          if qqe_cpg!-var car temp neq car list then 
%%             next := {'e, qqe_get!-min car list}
%%          else <<
%%             temp := qqe_cpg!-incl temp;
%%             list := cdr list;
%%          >>;
%%       
%%       return next;
%% 
%%    end;
%% 
%% procedure qqe_cpg!-get!-next!-reiterate!-check!-node(list, node);
%%    % i need the whole list down!
%%    begin 
%%       while null qqe_cpg!-excl car list do 
%%          list := cdr list;
%%       %either last or excl exists
%%       if qqe_cpg!-excl car then qqe_cpg!-get!-next!-down(list, car list)
%%       else return qqe_cpg!-get!-next!-max(list);
%%    end;
%% 
%% %% procedure qqe_cpg!-get!-alternative();
%% %%    % TODO
%% %%    begin
%% %%       while right neighbor on supernode try that
%% %%          while excl-child do this;
%% %%       
%% %%    end;
%% 
%% procedure qqe_cpg!-get!-solution!-for!-intervall(a,o,start,lcm, steps);
%%    % TODO
%%    begin temp, counter, lengths, s;
%%       if start > o then return nil;
%%       
%%       temp := start;
%%       while temp < a do
%%          temp := temp + lcm;
%% 
%%       if temp > o then return nil
%%       counter := 1; 
%%       lengths := {temp};
%% 
%%       while temp < o and counter < steps do <<
%%          temp := temp + lcm;
%%          if temp < o then << 
%%             counter := counter + 1;
%%             lengths := lengths . {temp};
%%          >>;
%%       >>;
%%       if counter eq steps then <<
%%          s := 0;
%%          for each l in lenghts do s := l + s;
%%          return {s ,lengths};
%%       >>
%%       else return nil;
%%    end;

endmodule;  % [qqeqemisc]

end;  % of file
