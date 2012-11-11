module mprint; % Basic output package for symbolic expressions.

% Authors: Anthony C. Hearn and Arthur C. Norman.

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid  '(!*fort
         !*list
         !*nat
         !*nosplit
         !*ratpri
         !*revpri
         bool!-functions!*
         obrkp!*
         overflowed!*
         orig!*
         outputhandler!*
         pline!*
         posn!*
         p!*!*
         testing!-width!*
         ycoord!*
         ymax!*
         ymin!*
         rprifn!*
         rterfn!*);

fluid '(!*TeX);

global '(!*eraise initl!* nat!*!* spare!* !*asterisk);

switch list,ratpri,revpri,nosplit,asterisk;

% Global variables initialized in this section.

% SPARE!* should be set in the system dependent code module,
% but is now assumed to be zero.

!*asterisk := t;
!*eraise := t;
!*nat := nat!*!* := t;
!*nosplit := t;            % Expensive, maybe??
obrkp!* := t;
orig!*:=0;
posn!* := 0;
ycoord!* := 0;
ymax!* := 0;
ymin!* := 0;

initl!* := append('(orig!* pline!*),initl!*);

put('orig!*,'initl,0);

flag('(linelength),'opfn);  %to make it a symbolic operator;

symbolic procedure mathprint l;
   << terpri!* t;
      maprin l;
      terpri!* t >>;

symbolic procedure maprin u;
   if outputhandler!* then apply2(outputhandler!*,'maprin,u)
    else if not overflowed!* then maprint(u,0);

symbolic procedure maprint(l,p!*!*);
   % Print expression l at bracket level p!*!* without terminating
   % print line.  Special cases are handled by:
   %    pprifn: a print function that includes bracket level as 2nd arg.
   %     prifn: a print function with one argument.
   begin scalar p,x,y;
        p := p!*!*;     % p!*!* needed for (expt a (quotient ...)) case.
        if null l then return nil
         else if atom l
          then <<if vectorp l then vec!-maprin(l,p!*!*)
                  else if not numberp l
                     or (not(l<0) or p<=get('minus,'infix))
                   then prin2!* l
                  else <<prin2!* "("; prin2!* l; prin2!* ")">>;
                 return l >>
         else if not atom car l then maprint(car l,p)
         else if ((x := get(car l,'pprifn)) and
                   not(apply2(x,l,p) eq 'failed)) or
                 ((x := get(car l,'prifn)) and
                   not(apply1(x,l) eq 'failed))
          then return l
         else if x := get(car l,'infix) then <<
           p := not(x>p);
           if p then <<
             y := orig!*;
             prin2!* "(";
             orig!* := if posn!*<18 then posn!* else orig!*+3 >>;
% (expt a b) was dealt with using a pprifn sometime earlier than this
           inprint(car l,x,cdr l);
           if p then <<
              prin2!* ")";
              orig!* := y >>;
           return l >>
         else prin2!* car l;
        prin2!* "(";
        obrkp!* := nil;
        y := orig!*;
        orig!* := if posn!*<18 then posn!* else orig!*+3;
        if cdr l then inprint('!*comma!*,0,cdr l);
        obrkp!* := t;
        orig!* := y;
        prin2!* ")";
        return l
    end;

symbolic procedure vec!-maprin(u,p!*!*);
   <<prin2!* '![;
     for j:=0:(upbv(u)-1)
        do <<maprint(getv(u,j),p!*!*); oprin '!*comma!*>>;
     maprint(getv(u,upbv(u)),p!*!*);
     prin2!* '!]>>;

symbolic procedure exptpri(l,p);
% Prints expression in an exponent notation.
   begin scalar !*list,x,pp,q,w1,w2;
      if not !*nat or !*fort then return 'failed;
      pp := not((q:=get('expt,'infix))>p);  % Need to parenthesize
      w1 := cadr l;
      w2 := caddr l;
      if !*eraise and not atom w1 and
         (x := get(car w1, 'prifn)) and
         get(x, 'expt) = 'inbrackets then
% Special treatment here to avoid muddle between exponents and
% raised indices
            w1 := layout!-formula(w1, 0, 'inbrackets)
% Very special treatment for things that will be displayed with
% subscripts
       else if x = 'indexprin and not (indexpower(w1, w2)='failed)
         then return nil
       else w1 := layout!-formula(w1, q, nil);
      if null w1 then return 'failed;
      begin scalar !*ratpri;
% I do not display fractions with fraction bars in exponent
% expressions, since it usually seems excessive. Also (-p)/q gets
% turned into -(p/q) for printing here
         if eqcar(w2,'quotient) and eqcar(cadr w2,'minus)
          then w2 := list('minus,list(car w2,cadadr w2,caddr w2))
          else w2 := negnumberchk w2;
         w2 := layout!-formula(w2, if !*eraise then 0 else q, nil)
      end;
      if null w2 then return 'failed;
      l := cdar w1 + cdar w2;
      if pp then l := l + 2;
      if l > linelength nil - spare!* - orig!* then return 'failed;
      if l > linelength nil - spare!* - posn!* then terpri!* t;
      if pp then prin2!* "(";
      putpline w1;
      if !*eraise then l := 1 - cadr w2
       else << oprin 'expt; l := 0 >>;
      putpline ((update!-pline(0, l, caar w2) . cdar w2) .
                ((cadr w2 + l) . (cddr w2 + l)));
      if pp then prin2!* ")"
   end;

put('expt,'pprifn,'exptpri);

symbolic procedure inprint(op,p,l);
   begin scalar x,y,z;
        if op='times and !*nat and null !*asterisk then
        <<op:='times2; put('times2,'infix,get('times,'infix));
          put('times2,'prtch," ")>>;
        if op eq 'plus and !*revpri then l := reverse l;
            % print sum arguments in reverse order.
        if not get(op,'alt) then <<
        if op eq 'not then oprin op else
          if op eq 'setq and not atom (x := car reverse l)
             and idp car x and (y := getrtype x)
             and (y := get(get(y,'tag),'setprifn))
            then return apply2(y,car l,x);
          if null atom car l and idp caar l
              and !*nat and
              ((x := get(caar l,'prifn)) or (x := get(caar l,'pprifn)))
              and (get(x,op) eq 'inbrackets)
            % to avoid mix up of indices and exponents.
            then<<prin2!* "("; maprint(car l,p); prin2!* ")">>
           else if !*nosplit and not testing!-width!* then
                prinfit(car l, p, nil)
           else maprint(car l, p);
          l := cdr l >>;
        if !*nosplit and not testing!-width!* then
% The code here goes to a certain amount of trouble to try to arrange
% that terms are never split across lines. This will slow
% printing down a bit, but I hope the improvement in formatting will
% be worth that.
              for each v in l do
               if atom v or not(op eq get(car v,'alt))
                then <<
% It seems to me that it looks nicer to put +, - etc on the second
% line, but := and comma usually look better on the first one when I
% need to split things.
                   if op memq '(setq !*comma!*) then <<
                      oprin op;
                      prinfit(negnumberchk v, p, nil) >>
                    else prinfit(negnumberchk v, p, op) >>
                else prinfit(v, p, nil)
         else for each v in l do <<
               if atom v or not(op eq get(car v,'alt))
                then <<oprin op; maprint(negnumberchk v,p)>>
              % difficult problem of negative numbers needing to be in
              % prefix form for pattern matching.
               else maprint(v,p) >>
   end;

symbolic procedure flatsizec u;
   if null u then 0
    else if atom u then lengthc u
    else flatsizec car u + flatsizec cdr u + 1;

symbolic procedure oprin op;
   (lambda x;
         if null x then <<prin2!* " "; prin2!* op; prin2!* " ">>
          else if !*fort then prin2!* x
          else if !*list and obrkp!* and op memq '(plus minus)
           then if testing!-width!* then overflowed!* := t
                 else <<terpri!* t; prin2!* x>>
          else if flagp(op,'spaced)
           then <<prin2!* " "; prin2!* x; prin2!* " ">>
          else prin2!* x)
   get(op,'prtch);

symbolic procedure prin2!* u;
    if outputhandler!* then apply2(outputhandler!*,'prin2!*,u)
     else begin integer m,n,p; scalar x;
      if x := get(u,'oldnam) then u := x;
      if overflowed!* then return 'overflowed
      else if !*fort then return fprin2!* u
      else if !*nat then <<
        if u = 'pi then u := symbol '!.pi
         else if u = 'infinity then u := symbol 'infinity>>;
      n := lengthc u;
      % Suggested by Wolfram Koepf:
      if fixp u and n>50 and !*rounded then return rd!:prin i2rd!* u;
      m := posn!* #+ n;
      p := linelength nil - spare!*;
      return if m<=p
                or (not testing!-width!*
       % The next line controls whether to add a newline before a long id.
       % At present it causes one in front of a number too.
                   and <<not fixp u and terpri!* t; (m := posn!* #+ n)<=p>>)
               then add_prin_char(u,m)
             % Identifier longer than one line.
              else if testing!-width!*
               then <<overflowed!* := t;'overflowed>>
              else prin2lint(u,posn!* #+ 1,p #- 1)
   end;

symbolic procedure add_prin_char(u,n);
   if null !*nat then if stringp u or get(u,'switch!*) or digit u
                        or get(car explode2 u,'switch!*) then prin2 u
                       else prin1 u
    else <<pline!* := (((posn!* . n) . ycoord!*) . u) .  pline!*;
           posn!* := n>>;

symbolic procedure prin2lint(u,m,n);
   begin scalar v,bool;
      % bool prevents an initial backslash.
      v := explode2 u;
      if null !*nat then <<terpri(); posn!* := orig!*>>;
   a: if not(m#<n and v) then go to b
       else if car v eq !$eol!$ then <<v := cdr v; go to c>>;
      bool := t; add_prin_char(car v,m);
      v := cdr v; m := m #+ 1;
      go to a;
   b: if null v then return(posn!* := m #- 1)
       else if bool then add_prin_char("\",m);
   c: if !*nat then terpri!* nil else <<terpri(); posn!* := orig!*>>;
      m := posn!* #+ 1;
      go to a
  end;

symbolic procedure terpri!* u;
   begin integer n;
        if outputhandler!* then return apply2(outputhandler!*,'terpri,u)
         else if testing!-width!* then return overflowed!* := t
         else if !*fort then return fterpri(u)
         else if !*nat and pline!*
          then <<
           pline!* := reverse pline!*;
           for n := ymax!* step -1 until ymin!* do <<
             scprint(pline!*,n);
             terpri() >>;
           pline!* := nil >>;
        if u then terpri();
        posn!* := orig!*;
        ycoord!* := ymax!* := ymin!* := 0
   end;

symbolic procedure scprint(u,n);
   begin scalar m;
        posn!* := 0;
        for each v in u do <<
           if cdar v=n then <<
             if not((m:= caaar v-posn!*)<0) then spaces m;
             prin2 cdr v;
             posn!* := cdaar v >> >>
   end;


% Formatted printing of expressions.

% This one should be eliminated.

symbolic procedure writepri(u,v); assgnpri(eval u,nil,v);

symbolic procedure exppri(u,v); assgnpri(u,nil,v);

% would-be-huge is used as a filter to judge which expressions can
% be displayed by CSL in "fancy" mode. What might really like it to do is
% to return TRUE in the case that the expression passed would display as
% a single unit that was more than about a page long. Simple sums such
% as (1+x)^50 may be very long but tmprint.red manages to display them as
% a sequence of independent lines, so it is not necessary to return TRUE
% for them.
% As a first attempt I will just pick up on matrices that have what I will
% hold to be "too many" rows, columns or merely elements. But extra tests
% can go in here as and when further limitations to the behaviour of the
% fancy printing emerge. Or the tests here can be scaled back if printing
% can be made more robust!

symbolic procedure would!-be!-huge u;
  begin
    scalar w, n, m;
    if eqcar(u, 'mat) then <<
       n := m := 1;
       for each x in cdr u do <<
          n := n + 1;
          w := length x;
          if w > m then m := w >>;
       if n > 20 or m > 20 or m*n=100 then return t
       else return 0 >>;
    return nil;
  end;

symbolic procedure assgnpri(u,v,w);
   begin scalar x, tm;
   % U is expression being printed.
   % V is a list of expressions assigned to U.
   % W is an id that indicates if U is the first, only or last element
   %  in the current set (or NIL otherwise).
   % Returns NIL.
    testing!-width!* := overflowed!* := nil;
    if null u then u := 0;
    if !*nero and u=0 then return nil;
    % Special cases.  These tests need to be generalized.
    if !*TeX then return texpri(u,v,w)
     else if getd 'vecp and vecp u then return vecpri(u,'mat);
   % The following is a bit of a mess. "fancy" output using latex style
   % in CSL has real difficulty when given really large expressions,
   % including big matrices. To avoid that leading to malformed output
   % and crashes I detect the case where I am running under CSL, fancy
   % output mode is available and enabled and the expression to to
   % printed is "huge". In that case I temporarily switch back to
   % old fashioned output format.
    if memq('csl, lispsystem!*) and
       getd 'math!-display and
       math!-display 0 and
       outputhandler!* = 'fancy!-output and
       would!-be!-huge u then <<
       fmp!-switch nil;
       tm := t >>;
    if (x := getrtype u) and flagp(x,'sprifn) and null outputhandler!*
      then <<if null v then apply1(get(get(x,'tag),'prifn),u)
             else maprin list('setq,car v,u) >>
    else <<
      if w memq '(first only) then terpri!* t;
      v := evalvars v;
      if !*fort then <<
        fvarpri(u,v,w);
        if tm then fmp!-switch t;
        return nil>>;
      maprin if v then 'setq . aconc(v,u) else u;
      if null w or w eq 'first then <<
        if tm then fmp!-switch t;
        return nil >>
       else if not !*nat then prin2!* "$";
      terpri!*(not !*nat) >>;
    if tm then fmp!-switch t;
    return nil
   end;

symbolic procedure evalvars u;
   % Used only in ASSGNPRI. We may need to expand the second test.
   % At the moment, it catches things like x-y:=0.
   if null u then nil
    else if atom car u or flagp(caar u,'intfn)
     then car u . evalvars cdr u
    else if get(get(caar u,'rtype),'setelemfn)
     then (caar u . revlis_without_mode cdar u) . evalvars cdr u
    else (caar u . revlis cdar u) . evalvars cdr u;

symbolic procedure revlis_without_mode u;
   for each j in u collect (reval j where dmode!* := nil);


% Definition of some symbols and their access function.

symbolic procedure symbol s;
   get(s,'symbol!-character);

put('!.pi, 'symbol!-character, 'pi);
put('bar, 'symbol!-character, '!-);
put('int!-top, 'symbol!-character, '!/);
put('int!-mid, 'symbol!-character, '!|);
put('int!-low, 'symbol!-character, '!/);
put('d, 'symbol!-character, '!d);       % This MUST be lower case
%%put('sqrt, 'symbol!-character, 'sqrt);% No useful fallback here
put('vbar, 'symbol!-character, '!|);
put('sum!-top, 'symbol!-character, "---");
put('sum!-mid, 'symbol!-character, ">  ");
put('sum!-low, 'symbol!-character, "---");
put('prod!-top, 'symbol!-character, "---");
put('prod!-mid, 'symbol!-character, "| |");
put('prod!-low, 'symbol!-character, "| |");
put('infinity, 'symbol!-character, 'infinity);
    % In effect nothing special

put('mat!-top!-l, 'symbol!-character, '![);
put('mat!-top!-r, 'symbol!-character, '!]);
put('mat!-mid!-l, 'symbol!-character, '![);
put('mat!-mid!-r, 'symbol!-character, '!]);
put('mat!-low!-l, 'symbol!-character, '![);
put('mat!-low!-r, 'symbol!-character, '!]);


% The following definitions allow for more natural printing of
% conditional expressions within rule lists.

bool!-functions!* :=
  for each x in {'equal,'greaterp,'lessp,'geq,'leq,'neq,'numberp}
      collect get(x,'boolfn) . x;

symbolic procedure condpri(u,p);
   <<if p>0 then prin2!* "(";
     while (u := cdr u) do
        <<if not(caar u eq 't)
            then <<prin2!* 'if; prin2!* " ";
                   maprin sublis(bool!-functions!*,caar u);
                   prin2!* " "; prin2!* 'then; prin2!* " ">>;
          maprin cadar u;
          if cdr u then <<prin2!* " "; prin2!* 'else; prin2!* " ">>>>;
     if p>0 then prin2!* ")">>;

put('cond,'pprifn,'condpri);

symbolic procedure revalpri u;
   maprin eval cadr u;

put('aeval,'prifn,'revalpri);

put('reval,'prifn,'revalpri);

symbolic procedure boolvalpri u;
   maprin cadr u;

put('boolvalue!*,'prifn,'boolvalpri);

put('prog,'prifn,'progpri);

put('progn,'prifn,'progpri);

symbolic procedure progpri u;
   (rprint u) where rprifn!* = 'prin2!*,
       rterfn!* = function(lambda();terpri!* nil);

put('!*hold, 'prifn, 'holdpri);

symbolic procedure holdpri u;
  << if not atom cadr u then prin2!* "(";
     maprin cadr u;
     if not atom cadr u then prin2!* ")" >>;


endmodule;

end;
