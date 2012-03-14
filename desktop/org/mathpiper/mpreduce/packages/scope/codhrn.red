module ghorner;   % Generalized Horner support.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Author  :   M.C. van Heerwaarden.                                   ;
% ------------------------------------------------------------------- ;
% ------------------------------------------------------------------- ;
% This module contains procedures which implement a generalized Horner;
% scheme. There are two generalizations:                              ;
% 1. It is possible to offer a set of assignment statements. Each RHS ;
%    will be transformed into a Horner scheme.;                       ;
% 2. A list of identifiers is accepted as input.The polynomial will be;
%    hornered w.r.t. the first identifier in the list, then the       ;
%    coefficients are hornered w.r.t. the second identifier, etc.     ;
%                                                                     ;
% The following steps are taken to achieve this result.               ;
%                                                                     ;
% The polynomial P is expanded by turning on the switch EXP and by    ;
% applying Aeval on P. Each term of the expanded polynomial is brought;
% in a normal form. The terms are sorted using a binary tree represen-;
% tation. From this tree a list of terms is extracted with the powers ;
% in descending order.This list is rewritten into a Horner scheme.    ;
%                                                                     ;
% The 'normal form' of a term is:                                     ;
%      (TIMES COEF (EXPT X N))                                        ;
% It may be degenerated to:                                           ;
%      (EXPT X N)                for COEF = 1                         ;
%      (TIMES COEF X)            for N = 1                            ;
%      (COEF)                    for N = 0                            ;
% When a term is a minus term, the minus is handled as a part of the  ;
% coefficient.                                                        ;
% ------------------------------------------------------------------- ;

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


global '(!*algpri autohorn);

switch algpri;

!*algpri := t;

% ------------------------------------------------------------------- ;
% ALGEBRAIC MODE COMMAND PARSER                                       ;
% ------------------------------------------------------------------- ;
% The -STAT and FORM- procedures provide an interface with the        ;
% algebraic mode. To horner a set of expressions, one can use the     ;
% HORNER command, which has the following syntax:                     ;
%   <HORNER command> ::= GHORNER <ass. list> [VORDER <ID-list>]       ;
%   <ass. list>      ::= <assignment statement> |                     ;
%                        << <assignment statement>                    ;
%                                        {; <assignment statement>} >>;
%   <ID-list>        ::= <ID> | <ID> {, <ID>}                         ;
% When the switch ALGPRI is ON, results are printed using Assgnpri,   ;
% When used inside a SCOPE-command the switch ALGPRI is turned OFF    ;
% automatically. However the current ALGPRI-setting is automatically  ;
% restored by SCOPE.                                                  ;
% ------------------------------------------------------------------- ;

put('ghorner, 'stat, 'ghornerstat);

symbolic procedure ghornerstat;
begin
    scalar x,y;
    % --------------------------------------------------------------- ;
    % GHORNER has already been read.                                  ;
    % --------------------------------------------------------------- ;
    flag('(vorder), 'delim);
    flag('(!*rsqb), 'delim);
    if car(x := xread t) = 'progn   % Read expressions;
    then x := cdr x                 % Remove keyword PROGN;
    else x := list x;               % An assignment is also an asslist;
    if not(cursym!* eq 'vorder)
    then if cursym!* eq '!*semicol!*
         then autohorn := t
         else symerr('ghorner, t)
    else << autohorn := nil;
            y := remcomma xread nil   % Read variable ordering list;
         >>;
    remflag('(vorder), 'delim);
    remflag('(!*rsqb), 'delim);
    return list('ghorner, x, y)
end;

put('ghorner, 'formfn, 'formghorner);

symbolic procedure formghorner(u, vars, mode);
    list('ghorner, mkquote cadr u, mkquote caddr u);

symbolic procedure ghorner(assset, varlist);
% ------------------------------------------------------------------- ;
% arg: assset = set of assignment statements                          ;
%      varlist = a list of variables                                  ;
% eff: For each assignment statement in assset, the RHS is turned into;
%      a Horner scheme. When varlist is not empty, the first variable ;
%      from varlist is used to form the scheme. The cdr of varlist is ;
%      used to transform the coefficients into a Horner scheme.       ;
%      Implicitly, the assignment is executed by putting the SQ-form  ;
%      of the Horner scheme on the property-list of the LHS-variable. ;
%      This means that the Horner scheme is available in the algebraic;
%      mode. When the switch ALGPRI is ON, the list of assignment     ;
%      statements is printed.                                         ;
% res: If ALGPRI is OFF the list with hornered assignment statements  ;
%      is returned. Nothing is returned when ALGPRI is ON.            ;
% ------------------------------------------------------------------- ;

begin
    scalar h, hexp, res;
    hexp := !*exp;
    !*exp := nil;
    res := for each ass in assset collect
               if not eqcar(ass, 'setq)
               then
                rederr("Assignment statement expected")
               else
                << h:=inithorner(caddr ass, varlist);
                   if !*algpri
                    then << if eqcar(h, 'quotient)
                             then
                              put(cadr ass,'avalue,
                               list('scalar,
                                     mk!*sq(numr !*f2q !*a2f cadr h ./
                                            numr !*f2q !*a2f caddr h)))
                              else
                               put(cadr ass,'avalue,
                                list('scalar, mk!*sq !*f2q !*a2f h));
                             assgnpri(h, list cadr ass, t);
                             terpri()
                          >>
                     else list(car ass,cadr ass,h)
                 >>;
    autohorn := nil;
    !*exp := hexp;
    if not !*algpri
    then return res
end;

symbolic procedure inithorner(p, varlist);
% ------------------------------------------------------------------- ;
% arg: p = polynomial                                                 ;
%      varlist = list of variables                                    ;
% eff: p is expanded and hornered to the various variables            ;
% res: the hornered version of p                                      ;
% ------------------------------------------------------------------- ;
begin scalar n, d, hmcd, res;
    hmcd := !*mcd;
    !*mcd := t;
    p := reval p;
    res := hornersums(p, varlist);
    !*mcd := hmcd;
    return res
end;

symbolic procedure hornersums(p, varlist);
    if (atom(p) or domprop(p))      % JB 9/3/94
    then p
    else if eqcar(p, 'plus)
         then horner(p, varlist)
         else append(list car p,
                     for each elt in cdr p
                     collect hornersums(elt, varlist));

symbolic procedure horner(p, varlist);
% ------------------------------------------------------------------- ;
% arg: p = polynomial                                                 ;
%      varlist = a list of variables for which the scheme must be made;
% res: A Horner scheme of p with respect to first variable in varlist ;
% ------------------------------------------------------------------- ;
begin
    scalar hexp, tree, var;
    hexp := !*exp;
    !*exp := t;
    p := reval p;
    tree := '(nil nil nil);
    var := if varlist
           then car varlist
           else if autohorn
                then mainvar2 p
                else nil;
    if var
    then << for each kterm in cdr p
            do tree := puttree(tree,
                               orderterm(kterm, var),
                               var);
            p := gathertree(tree, var . cdr varlist);
            p := schema(p, var, kpow(car p, var))
          >>;
    !*exp := hexp;
    return p
end;


symbolic procedure hornercoef(term, varlist);
% ------------------------------------------------------------------- ;
% arg: term = term of a polynomial in 'normal form'                   ;
%      varlist = the list of variables, including the one which just  ;
%                has been used to create the scheme.                  ;
% res: The same term is returned, but the coefficient has been turned ;
%      into a Horner scheme, using the second variable of varlist as  ;
%      main variable.                                                 ;
% ------------------------------------------------------------------- ;
begin
    scalar n, cof;
    return if null(cof := kcof(term, (n := kpow(term, car varlist))))
           then nil
           else if atom cof
                then term
                else if n = 0
                     then hornersums(cof, cdr varlist)
                     else list(car term,
                               hornersums(cof, cdr varlist),
                               caddr term)
end;

symbolic procedure puttree(tree, term, var);
% ------------------------------------------------------------------- ;
% arg: tree = tree structure ( node, left edge, right edge), in which ;
%             the ordered terms are present.                          ;
%      term = the term which has to be put in                         ;
%      var = the variable for which the Horner scheme must be made    ;
% res: When the power of term is higher than the power of the node of ;
%      the root, puttree is called to place term in the right subtree ;
%      If the power is lower, term is placed in the left subtree. If  ;
%      the powers are equal the coefficients are added.               ;
% ------------------------------------------------------------------- ;
begin
    scalar c, n, m;
    return if null tree or null car tree
           then list (term, nil, nil)
           else if (n := kpow(term, var)) < (m := kpow(car tree, var))
                then list(car tree,
                          puttree(cadr tree, term, var),
                          caddr tree)
                else if n > m
                     then list(car tree,
                               cadr tree,
                               puttree(caddr tree, term, var))
                     else << % n = m so at least one term has been    ;
                             % inserted. Terms are added using only   ;
                             % one plus.                              ;
                             c := kcof(car tree, n);
                             if pairp c and car c = 'plus
                             then c := cdr c
                             else c := list c;
                             if n = 0
                             then
                              list(append('(plus),
                                         append(list(kcof(term,n)),c)),
                                   cadr tree,
                                   caddr tree)
                             else
                              list(list('times,
                                         append('(plus),
                                         append(list(kcof(term,n)),c)),
                                         if car c = 1
                                          then car tree
                                          else caddar tree
                                        ),
                                   cadr tree,
                                   caddr tree)>>
end;


symbolic procedure gathertree(tree, varlist);
% ------------------------------------------------------------------- ;
% arg: tree = a tree as made by puttree                               ;
%      varlist = list of variables                                    ;
% res: a list of the terms which are stored in the tree. The term with;
%      the highest power is first in the list. For every term found, a;
%      Horner-scheme is made for the coefficients of this term.At this;
%      point the current variable remains on varlist.                 ;
% ------------------------------------------------------------------- ;
begin
    % This is a reversed depth-first search;
    return if null tree
           then nil
           else append(gathertree(caddr tree, varlist),
                       append(list hornercoef(car tree, varlist),
                              gathertree(cadr tree, varlist)))
end;


symbolic procedure orderterm(tt, var);
% ------------------------------------------------------------------- ;
% arg: tt = one term from the expanded polynomial                     ;
%      var = the variable for which the Horner scheme must be made    ;
% res: the term tt is returned in the 'normal form' which is described;
%      in the opening section.                                        ;
% ------------------------------------------------------------------- ;
begin
    scalar h, res, factr, min;
    min := nil;
    if tt = var
    then res := tt
    else << if eqcar(tt, 'minus)
            then << min := t;
                    tt := cadr tt
                 >>;
            if not eqcar(tt,'times)
            then if min
                 then if tt=var or (eqcar(tt,'expt) and cadr tt=var)
                      then res := list('times, '(minus 1), tt)
                      else res := list('minus, tt)
                 else res := tt
            else << while not null (tt := cdr tt)
                    do << if pairp(h := car tt) and eqcar(h, 'minus)
                          then << min := not min;
                                  h := cadr h
                               >>;
                          if h = var
                          then factr := h
                          else << if eqcar(h, 'expt) and cadr h = var
                                  then factr := h
                                  else res := append(res, list h)
                               >>
                       >>;
                    if min
                    then << h := list('minus, car res);
                            if null cdr res
                            then res := list h
                            else res := append(list h, cdr res)
                         >>;
                    res := if null factr
                           then cons('times, res)
                           else if null cdr res
                                then list('times, car res, factr)
                                else list('times,
                                          append('(times), res),
                                          factr)
                 >>
         >>;
    return res
end;

symbolic procedure schema(pn, var, n);
% ------------------------------------------------------------------- ;
% arg: pn  = the polynomial pn given as a list of terms in 'normal    ;
%            form' in decsending order w.r.t. the powers of these     ;
%            terms.                                                   ;
%      var = the Horner-scheme variable.                              ;
%      n   = degree of the polynomial.                                ;
% eff: Some effort is made to change "(TIMES var 1)" to "var" and to  ;
%      turn "...(TIMES var (TIMES var..." into                        ;
%           "...(TIMES (EXPT var n) ..."                              ;
% res: Horner scheme for the polynomial pn.                           ;
% ------------------------------------------------------------------- ;
begin
    scalar hn, k, k!+1mis;
    hn := kcof(car pn, n); % The n-th term always exists;
    if null (pn := cdr pn) then pn:=list(nil);
       % Else car(NIL) could be evaluated.
    for k := (n - 1) step -1 until 0
    do << % --------------------------------------------------------- ;
          % hn contains the coefficients for the terms var^n upto     ;
          % var^(k+1). The var for term var^(k+1) is still missing.   ;
          % This is correct when for k=0 the last var will be added.  ;
          % --------------------------------------------------------- ;
          if kpow(car pn, var) = k
          then << % k-th term exists;
                  hn := list('plus, kcof(car pn, k),
                             if hn = 1
                             then var
                             else if not (k = n-1) and k!+1mis
                                  then
                                   if pairp hn and car hn = 'times
                                    then list('times,list('expt,var,
                                              kpow(cadr hn, var) + 1),
                                                 caddr hn)
                                    else list('expt,var,
                                                 kpow(hn, var) + 1)
                                  else list('times, var, hn)
                            );
                  k!+1mis := nil;
                  if null (pn := cdr pn) then pn:=list(nil)
               >>
          else << % k-th term misses;
                  hn := if hn = 1
                        then var
                        else if not (k = n-1) and k!+1mis
                             then
                              if pairp hn and car hn = 'times
                               then list('times,list('expt,var,
                                               kpow(cadr hn, var) + 1),
                                          caddr hn)
                               else list('expt, var, kpow(hn, var) + 1)
                             else list('times, var, hn);
                  k!+1mis := t
               >>
       >>;
    return hn
end;


symbolic procedure kpow(term, var);
% ------------------------------------------------------------------- ;
% arg: term = term of a polynomial in 'normal form'                   ;
%      var  = the variable for which the Horner scheme must be made   ;
% res: the power of var in term                                       ;
% ------------------------------------------------------------------- ;
begin
    scalar h;
    return if null term
           then nil
           else if (h := term) = var
                then 1
                else if eqcar(h, 'expt) and eqcar(cdr h, var)
                     then caddr h
                     else if eqcar(h, 'times)
                          then if (h := caddr h) = var
                               then 1
                               else if not atom h and eqcar(cdr h, var)
                                    then caddr h
                                    else 0
                          else 0
end;


symbolic procedure kcof(term, n);
% ------------------------------------------------------------------- ;
% arg: term = term of a polynomial in 'normal form'                   ;
%      n    = the power of term                                       ;
% res: the coefficient of var in term                                 ;
% ------------------------------------------------------------------- ;
    if null n
    then nil
    else if n = 0
         then term
         else if n = 1
              then if not eqcar(term, 'times)
                   then 1
                   else cadr term
              else if eqcar(term, 'expt)
                   then 1
                   else cadr term;

symbolic procedure mainvar2 u;
% ------------------------------------------------------------------- ;
% Same procedure as mainvar from ALG2.RED, but returns NIL instead of ;
% 0 and does not allow a mainvar of the form (EXPT E X) to be returned;
% ------------------------------------------------------------------- ;
begin
   scalar res;
   res := if domainp(u := numr simp!* u)
          then nil
          else if sfp(u := mvar u)
               then prepf u
               else u;
   if eqcar(res, 'expt)
   then res := nil;
   return res
 end;


%-----------------------------------------------------------------------
% Algebraic mode psop function definition.
%-----------------------------------------------------------------------

symbolic procedure alghornereval u;
% -------------------------------------------------------------------- ;
% Variant of ghorner-command.  Accepts 1 or 2 arguments.  The first has
% to be a list of equations.  Their rhs's have to be hornered.  The
% second argument is optional.  It defines the list of identifiers to
% be used for the ordering.

% -------------------------------------------------------------------- ;
begin scalar algpri,assset,res,varlist; integer nargs;
 nargs:=length u;
 if nargs<3
  then << assset:=foreach el in (if atom car u
                                    then cdr reval car u
                                    else cdar u )collect
                  list('setq,cadr el,caddr el);
          if nargs=2 then varlist:=cdadr u
       >>
  else assset:='!*!*error!*!*;
 if eq(assset,'!*!*error!*!*)
  then rederr("WRONG NUMBER OF ARGUMENTS ALGHORNER")
  else << algpri:=!*algpri; !*algpri:=nil;
          res:=apply('ghorner,list(assset,varlist));
          if (!*algpri:=algpri)
           then
           return algresults1(foreach el in res
                                 collect cons(cadr el,caddr el))
           else return res
       >>
end;

put('alghorner,'psopfn,'alghornereval)$


%------------------------------------------------------------------
%       Construction of Krukyov Horner's form of polynomial  % JB 9/3/94
%------------------------------------------------------------------

algebraic procedure horner0(p,x)$
  %----------------------------------------------------------
  %       p is a polynomial,
  %       x is a Horner's variable$
  %       return p transformed to Horner's form
  %----------------------------------------------------------
  begin scalar c,h$
    on exp$
    p:=p$
    c:=reverse coeff(p,x)$
    off exp$
    h:=0$
    while c neq {} do <<
      h:=h*x+first c$
      c:=rest c$
    >>$
    return h$
end$

algebraic procedure horner1(p)$
  %----------------------------------------------------------
  %       p is a polynomial,
  %       return p transformed to Horner's form
  %       the MAINVAR of p use as a Horner's variable
  %----------------------------------------------------------
 if numberp p then p else
  begin scalar c,h,x$
    on exp$
    p:=p$
    x:=mainvar p$
    c:=reverse coeff(p,x)$
    off exp$
    h:=0$
    while c neq {} do <<
      h:=h*x+horner1 first c$
      c:=rest c$
    >>$
    return h$
end$

lisp global '(hvlst)$     % use for debug purposes.

algebraic procedure horner2(p)$
  %----------------------------------------------------------
  %       p is a polynomial,
  %       return p transformed to Horner's form
  %       Horner's variable is defined by HVAR1 procedure.
  %       Outside effect: clear HVLST variable.
  %       HVLST variable content the result of work of HVAR1
  %          (use for debug purposes).
  %----------------------------------------------------------
  << clhvlist()$ horner20 p >>$

algebraic procedure horner20(p)$
  %----------------------------------------------------------
  %       p is a polynomial,
  %       return p transformed to Horner's form
  %       Horner's variable is defined by HVAR1 procedure.
  %----------------------------------------------------------
 if numberp p then p
  else begin scalar q,x,c$
         on exp$
           q:=p$
           x:=hvar1 q$
           c:=sub(x=0,q)$
           q:=(q-c)/x$
         off exp$
         q:=horner20(q)*x+horner20(c)$
         return q$
       end$

symbolic procedure hvar1 q$
  %----------------------------------------------------------
  %       q is a polynomial,
  %       return Horner's variable.
  %       Outside effect: set HVLST variable (use for debug only).
  %         HVLST::=((expr . alst)...)
  %         expr::=polynomial
  %         alst::=((var.number)...)
  %       Here the Horner's variable define as a variable
  %       that entry to q in more tems then others.
  %       For example: X+X**2+Y+1. The Horner's variable is X.
  %----------------------------------------------------------
 if numberp q then rederr "HVAR1: impossible!" else
  begin scalar x,y,v$
    q:=reval q$              % usually it is not needed.
    if null atom q and car q eq 'plus then q:=cdr q
      else q:=list q$
    for each z in q do <<
         if null atom z and car z eq 'minus then z:=cadr z$
         if null atom z and car z eq 'times then z:=cdr z
           else z:=list z$
         for each w in z do <<
             if null atom w and car w eq 'expt then w:=cadr w
              else if numberp w then w:=nil$
             if w and (y:=assoc(w,v)) then rplacD(y,cdr y + 1)
              else if w then v:=(w . 1).v$
         >>$
    >>$
    x:=car v$
    for each z in cdr v do if cdr z > cdr x then x:=z$
    hvlst:=(q.v).hvlst$
    return car x$
end$

algebraic procedure khorner20(p,vlst)$
  %----------------------------------------------------------
  %       p is a polynomial, vlst is a list of horner-variables.
  %       return p transformed to Horner's form
  %       Horner's variable is defined by the khvar1-procedure.
  %----------------------------------------------------------
if numberp p then p
else
 begin scalar q,x,c;
  on exp;
  q:=p;
  if (x:=khvar1(q,vlst))
   then
    << c:=sub(x=0,q);
       q:=(q-c)/x;
       off exp;
       return(khorner20(q,vlst)*x+khorner20(c,vlst))
    >>
   else
    << off exp;
       return(nestedfac q)
    >>
 end$

symbolic procedure khvar1(q,vlst);
  %----------------------------------------------------------
  %       q is a polynomial, vlst is a list of horner-variables.
  %       return Horner's variable.
  %       Here the Horner's variable is defined as a variable
  %       that occurs in more q-terms than the others.
  %       For example: X  in q = X+X**2+Y+1.
  %----------------------------------------------------------
if numberp q then rederr "HVAR1: impossible!"
 else
  begin scalar x,y,v;
   vlst:=cdr vlst; q:=reval q;% redefinition q usually not needed.
   if null atom q and car q eq 'plus
    then q:=cdr q else q:=list q;
   foreach z in q
    do << if null atom z and car z eq 'minus
           then z:=cadr z;
          if null atom z and car z eq 'times
           then z:=cdr z else z:=list z;
          for each w in z
           do << if null atom w and car w eq 'expt
                  then w:=cadr w else if numberp w then w:=nil;
                 if w and memq(w,vlst)
                  then if (y:=assoc(w,v))
                        then rplacd(y,cdr y + 1)
                        else v:=(w . 1).v
              >>
       >>;
   if v
    then << x:=car v;
            foreach z in cdr v do if cdr z > cdr x then x:=z$
            return car x
         >>
    else return nil
  end$

symbolic procedure hvlist()$
  %----------------------------------------------------------
  %       Procedure for printing HVLST variable.
  %       Debug utility.
  %----------------------------------------------------------
  for each x in hvlst do print x$

symbolic procedure clhvlist()$
  %----------------------------------------------------------
  %       Procedure for clearing HVLST variable.
  %       Debug utility.
  %----------------------------------------------------------
  hvlst:=nil$

symbolic operator khvar1,hvar1,hvlist,clhvlist$ % Interface with REDUCE

% -------------------------------------------------------------------
% Interface for generalised facilities, based on the use of the
% procedure gkhorner. This procedure can be used with one argument
% only, being a list of equations of the form lhsi=rhsi, where the
% i-th lhs is a name and the i-th rhs a (multivariate) polynomial,
% to be hornered either exhaustively using horner20(rhsi) or restric-
% tively using the second argument vlst, being a list of horner-
% variables, and procedure khorner20. When further vlst variables are
% absent the remaining parts of q are further polished using nestedfac.
% -------------------------------------------------------------------

symbolic procedure khornereval u;
begin scalar poly,varlst; integer nargs;
 nargs:=length u;
 if nargs<3
  then << poly:=aeval car u;
          if nargs=2 then varlst:=aeval cadr u>>
  else poly:='!*!*error!*!*;
 if eq(poly,'!*!*error!*!*)
  then rederr("WRONG NUMBER OF ARGUMENTS KHORNER")
  else return if nargs=1
               then reval horner2 poly
               else reval khorner20(poly,varlst)
end;

put('khorner,'psopfn,'khornereval)$

symbolic procedure gkhornereval u;
begin scalar poly_s,varlst; integer nargs;
 nargs:=length u;
 if nargs<3
  then << poly_s:=cdar u;
          if nargs=2 then varlst:=cadr u>>
  else poly_s:='!*!*error!*!*;
 if eq(poly_s,'!*!*error!*!*)
  then rederr("WRONG NUMBER OF ARGUMENTS GKHORNER")
  else
  return if pairp(car poly_s) and eq(caar poly_s,'equal)
   then append(list('list),
                    foreach poly in poly_s collect
                     list('equal,
                           cadr poly,
                           khornereval if nargs=1 then cddr poly
                                       else list(caddr poly,varlst)))
   else append(list('list),
                    foreach poly in poly_s collect
                     khornereval if nargs=1 then list(poly)
                                       else list(poly,varlst))
end$

put('gkhorner,'psopfn,'gkhornereval)$

symbolic procedure alggkhornereval u;
begin scalar poly_s,varlst; integer nargs;
 nargs:=length u;
 if nargs<3
  then << poly_s:=cdar u;
          if nargs=2 then varlst:=cadr u
       >>
  else poly_s:='!*!*error!*!*;
 if eq(poly_s,'!*!*error!*!*)
  then rederr("WRONG NUMBER OF ARGUMENTS GKHORNER")
  else
   return
     algresults1(foreach poly in poly_s
                  collect cons(cadr poly,
                               khornereval if nargs=1 then cddr poly
                                          else list(caddr poly,varlst)))
end;

put('alggkhorner,'psopfn,'alggkhornereval)$

endmodule;

end;



