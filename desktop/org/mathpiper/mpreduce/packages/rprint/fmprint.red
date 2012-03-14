module fmprint; % Fancy output package for symbolic expressions.
                % using TEX as intermediate language.

% Author: Herbert Melenk, using ideas of maprin.red (A.C.H, A.C.N).

% Modifications:
%               fancy!-mode!* commented out, since it applies only to
%               very old versions. /

% Copyright (c) 2003 Anthony C. Hearn, Konrad-Zuse-Zentrum.
%           All rights reserved.

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


%   8-Sep-94
%               introduced data driven formatting (print-format)

%  12-Apr-94
%               removed print function for dfp
%               removed some unused local variables
%               corrected output for conditional expressions and
%                 aeval/aeval* forms

%  17_Mar-94    corrected line breaks in Taylor expressions
%               rational exponents use /
%               vertical bar for SUB expressions
%               explicit * for product of two quotients (Taylor)

% switches
%
%    ON FANCY          enable algebraic output processing by this module
%
%    ON FANCY_TEX      under ON FANCY: display TEX equivalent
%

% properties used in this module:
%
%     fancy-prifn      print function for an operator
%
%     fancy-pprifn     print function for an oeprator including current
%                      operator precedence for infix printing
%
%     fancy!-flatprifn print function for objects which require
%                      special printing if prefix operator form
%                      would have been used, e.g. matrix, list
%
%     fancy-prtch      string for infix printing of an operator
%
%     fancy-special-symbol
%                      print expression for a non-indexed item
%                      string with TEX expression  "\alpha"
%                         or
%                      number referring ASCII symbol code
%
%     fancy-infix-symbol    special-symbol for infix operators
%
%     fancy-prefix-symbol   special symbol for prefix operators
%
%     fancy!-symbol!-length  the number of horizontal units needed for
%                      the symbol.  A standard character has 2 units.


%  94-Jan-26 - Output for Taylor series repaired.
%  94-Jan-17 - printing of index for Bessel function repaired.
%            - New functions for local encapsulation of printing
%              independent of smacro fancy!-level.
%            - Allow printing of upper case symbols locally
%              controlled by *fancy-lower

%  93-Dec-22 Vectors printed with square brackets.

create!-package('(fmprint),nil);

fluid  '(
         !*list
         !*nat
         !*nosplit
         !*ratpri
         !*revpri
         overflowed!*
         p!*!*
         testing!-width!*
         tablevel!*
         sumlevel!*
         outputhandler!*
         outputhandler!-stack!*
         posn!*
         obrkp!*    % outside-brackets-p
         );

global '(!*eraise charassoc!* initl!* nat!*!* spare!* ofl!*);

switch list,ratpri,revpri,nosplit;

% Global variables initialized in this section.

fluid '(
      fancy!-switch!-on!*
      fancy!-switch!-off!*
          !*fancy!-mode
          fancy!-pos!*
          fancy!-line!*
          fancy!-page!*
          fancy!-bstack!*
          !*fancy_tex
          !*fancy!-lower    % control of conversion to lower case
%         fancy!-mode!*
      );

switch fancy_tex; % output TEX equivalent.

% fancy!-mode!* := if '!6 = car reverse explode2 getenv "reduce" then 36
%                  else 35;

% fancy!-mode!* := 36;   % This needs to be more than 35.
fancy!-switch!-on!* := int2id 16$
fancy!-switch!-off!* := int2id 17$
!*fancy!-lower := t;

global '(fancy_lower_digits fancy_print_df);

share fancy_lower_digits; % T, NIL or ALL.

if null fancy_lower_digits then fancy_lower_digits:=t;

share fancy_print_df;     % PARTIAL, TOTAL, INDEXED.

if null fancy_print_df then  fancy_print_df := 'partial;
switch fancy;

put('fancy,'simpfg,
  '((t (fmp!-switch t))
    (nil (fmp!-switch nil)) ));


symbolic procedure fmp!-switch mode;
      if mode then
        <<if outputhandler!* neq 'fancy!-output then
          <<outputhandler!-stack!* :=
                outputhandler!* . outputhandler!-stack!*;
           outputhandler!* := 'fancy!-output;
          >>
        >>
      else
        <<if outputhandler!* = 'fancy!-output then
          <<outputhandler!* := car outputhandler!-stack!*;
            outputhandler!-stack!* := cdr outputhandler!-stack!*;
          >> else
          rederr "FANCY is not current output handler"
        >>;

symbolic procedure fancy!-out!-header();
    if not !*fancy_tex then prin2 fancy!-switch!-on!*;

symbolic procedure fancy!-out!-trailer();
  <<if not !*fancy_tex then prin2 fancy!-switch!-off!*;
     terpri()>>;

symbolic procedure fancy!-tex s;
  % test output: print tex string.
   <<prin2 fancy!-switch!-on!*;
     for each x in explode2 s do prin2 x;
     prin2t fancy!-switch!-off!*;
   >>;

symbolic procedure fancy!-out!-item(it);
  if atom it then prin2 it else
  if eqcar(it,'ascii) then writechar(cadr it) else
  if eqcar(it,'tab) then
     for i:=1:cdr it do prin2 "\>"
    else
  if eqcar(it,'bkt) then
     begin scalar m,b,l; integer n;
      m:=cadr it; b:=caddr it; n:=cadddr it;
      l := b member '( !( !{ );
   %  if m then prin2 if l then "\left" else "\right"
   % else
      if n> 0 then
      <<prin2 if n=1 then "\big" else if n=2 then "\Big" else
             if n=3 then "\bigg" else "\Bigg";
        prin2 if l then "l" else "r";
      >>;
      if b member '(!{ !}) then prin2 "\";
      prin2 b;
    end
    else
      rederr "unknown print item";

symbolic procedure set!-fancymode bool;
  if bool neq !*fancy!-mode then
    <<!*fancy!-mode:=bool;
      fancy!-pos!*:=0;
      fancy!-page!*:=nil;
      fancy!-line!*:=nil;
      overflowed!* := nil;
        % new: with tab
      fancy!-line!*:= '((tab . 1));
      fancy!-pos!* := 10;
      sumlevel!* := tablevel!* := 1;
   >>;

symbolic procedure fancy!-output(mode,l);
  % Interface routine.
  if ofl!* or posn!*>2 or not !*nat then
     % not terminal handler or current output line non-empty.
   <<if mode = 'maprin then maprin l
     else
     terpri!*(l)
   >> where outputhandler!* = nil
     else
   <<set!-fancymode t;
     if mode = 'maprin then fancy!-maprin0 l
     else
     fancy!-flush();
    >>;

symbolic procedure fancy!-flush();
     << fancy!-terpri!* t;
        for each line in reverse fancy!-page!* do
        if line and not eqcar(car line,'tab) then
        <<fancy!-out!-header();
          for each it in reverse line do fancy!-out!-item it;
          fancy!-out!-trailer();
        >>;
        set!-fancymode nil;
      >> where !*lower=nil;

%---------------- primitives -----------------------------------

symbolic procedure fancy!-special!-symbol(u,n);
   if numberp u then
     <<fancy!-prin2!*("\symb{",n);
       fancy!-prin2!*(u,0);
       fancy!-prin2!*("}",0);
     >>
    else fancy!-prin2!*(u,n);

symbolic procedure fancy!-prin2 u;
    fancy!-prin2!*(u,nil);

symbolic procedure fancy!-prin2!*(u,n);
  if numberp u and not testing!-width!* then fancy!-prin2number u
     else
  (begin scalar str,id; integer l;
    str := stringp u; id := idp u and not digit u;
    u:= if atom u then explode2 u where !*lower=!*fancy!-lower
        else {u};
    l := if numberp n then n else 2*length u;
    if id and not numberp n then
       u:=fancy!-lower!-digits(fancy!-esc u);
    for each x in u do
    <<if str and (x='!    or x='!_)
         then fancy!-line!* := '!\ . fancy!-line!*;
      fancy!-line!* :=
        (if id and !*fancy!-lower
          then red!-char!-downcase x else x) . fancy!-line!*;
    >>;
    fancy!-pos!* := fancy!-pos!* #+ l;
    if fancy!-pos!* #> 2 #* (linelength nil #+1 ) then overflowed!*:=t;
  end) where !*lower = !*lower;

symbolic procedure fancy!-last!-symbol();
   if fancy!-line!* then car fancy!-line!*;

charassoc!* :=
         '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e) (!F . !f)
           (!G . !g) (!H . !h) (!I . !i) (!J . !j) (!K . !k) (!L . !l)
           (!M . !m) (!N . !n) (!O . !o) (!P . !p) (!Q . !q) (!R . !r)
           (!S . !s) (!T . !t) (!U . !u) (!V . !v) (!W . !w) (!X . !x)
           (!Y . !y) (!Z . !z));

symbolic procedure red!-char!-downcase u;
   (if x then cdr x else u) where x = atsoc(u,charassoc!*);

symbolic procedure fancy!-prin2number u;
  % we print a number eventually causing a line break
  % for very big numbers.
  if testing!-width!* then  fancy!-prin2!*(u,t) else
     fancy!-prin2number1 (if atom u then explode2 u else u);

symbolic procedure fancy!-prin2number1 u;
  begin integer c,ll;
   ll := 2 #* (linelength nil #+1 );
   while u do
   <<c:=c+1;
     if c>10 and fancy!-pos!* #> ll then fancy!-terpri!*(t);
     fancy!-prin2!*(car u,2); u:=cdr u;
   >>;
  end;

symbolic procedure fancy!-esc u;
   if not('!_ memq u) then u else
   (if car u eq '!_ then '!\ . w else w)
      where w = car u . fancy!-esc cdr u;

symbolic procedure fancy!-lower!-digits u;
    (if null m then u else if m = 'all or
        fancy!-lower!-digitstrail(u,nil) then
           fancy!-lower!-digits1(u,nil)
     else u
     ) where m=fancy!-mode 'fancy_lower_digits;

symbolic procedure fancy!-lower!-digits1(u,s);
  begin scalar c,q,r,w,x;
 loop:
    if u then <<c:=car u; u:=cdr u>> else c:=nil;
    if null s then
      if not digit c and c then w:=c.w else
      << % need to close the symbol w;
         w:=reversip w;
         q:=intern compress w;
         if stringp (x:=get(q,'fancy!-special!-symbol))
            then w:=explode2 x;
         r:=nconc(r,w);
         if digit c then <<s:=t; w:={c}>> else w:=nil;
      >>
    else
      if digit c then w:=c.w else
      << % need to close the number w.
        w:='!_ . '!{ . reversip('!} . w);
        r:=nconc(r,w);
        if c then <<s:=nil; w:={c}>> else w:=nil;
      >>;
    if w then goto loop;
    return r;
  end;




symbolic procedure fancy!-lower!-digitstrail(u,s);
   if null u then s else
   if not s and digit car u then
          fancy!-lower!-digitstrail(cdr u,t) else
   if s and not digit car u then nil
   else fancy!-lower!-digitstrail(cdr u,s);

symbolic procedure fancy!-terpri!* u;
   <<
     if fancy!-line!* then
         fancy!-page!* := fancy!-line!* . fancy!-page!*;
     fancy!-pos!* :=tablevel!* #* 10;
     fancy!-line!*:= {'tab . tablevel!*};
     overflowed!* := nil
   >>;

symbolic macro procedure fancy!-level u;
 % unwind-protect for special output functions.
  {'prog,'(pos fl w),
      '(setq pos fancy!-pos!*),
      '(setq fl fancy!-line!*),
      {'setq,'w,cadr u},
      '(cond ((eq w 'failed)
              (setq fancy!-line!* fl)
              (setq fancy!-pos!* pos))),
       '(return w)};

symbolic procedure fancy!-begin();
  % collect current status of fancy output. Return as a list
  % for later recovery.
  {fancy!-pos!*,fancy!-line!*};

symbolic procedure fancy!-end(r,s);
  % terminates a fancy print sequence. Eventually resets
  % the output status from status record <s> if the result <r>
  % signals an overflow.
  <<if r='failed then
     <<fancy!-line!*:=car s; fancy!-pos!*:=cadr s>>;
     r>>;

symbolic procedure fancy!-mode u;
  begin scalar m;
     m:= lispeval u;
     if eqcar(m,'!*sq) then m:=reval m;
     return m;
  end;

%---------------- central formula converter --------------------

symbolic procedure fancy!-maprin0 u;
   if not overflowed!* then fancy!-maprint(u,0) where !*lower=nil;

symbolic procedure fancy!-maprint(l,p!*!*);
   % Print expression l at bracket level p!*!* without terminating
   % print line.  Special cases are handled by:
   %    pprifn: a print function that includes bracket level as 2nd arg.
   %     prifn: a print function with one argument.
  (begin scalar p,x,w,pos,fl;
        p := p!*!*;     % p!*!* needed for (expt a (quotient ...)) case.
        if null l then return nil;
        if atom l then return fancy!-maprint!-atom(l,p);
        pos := fancy!-pos!*; fl := fancy!-line!*;

        if not atom car l then return fancy!-maprint(car l,p);

        l := fancy!-convert(l,nil);

        if (x:=get(car l,'fancy!-reform)) then
          return fancy!-maprint(apply1(x,l),p);
        if ((x := get(car l,'fancy!-pprifn)) and
                   not(apply2(x,l,p) eq 'failed))
          or ((x := get(car l,'fancy!-prifn)) and
                   not(apply1(x,l) eq 'failed))
          or (get(car l,'print!-format)
                 and fancy!-print!-format(l,p) neq 'failed)
          then return nil;

        if testing!-width!* and overflowed!*
           or w='failed then return fancy!-fail(pos,fl);

        % eventually convert expression to a different form
        % for printing.

        l := fancy!-convert(l,'infix);

        % printing operators with integer argument in index form.
        if flagp(car l,'print!-indexed) then
        << fancy!-prefix!-operator(car l);
           w :=fancy!-print!-indexlist cdr l
        >>

        else if x := get(car l,'infix) then
        << p := not(x>p);
          w:= if p then fancy!-in!-brackets(
            {'fancy!-inprint,mkquote car l,x,mkquote cdr l},
               '!(,'!))
              else
            fancy!-inprint(car l,x,cdr l);
        >>
        else if x:= get(car l,'fancy!-flatprifn) then
            w:=apply(x,{l})
        else
        <<
           w:=fancy!-prefix!-operator(car l);
           obrkp!* := nil;
           if w neq 'failed then
             w:=fancy!-print!-function!-arguments cdr l;
        >>;

        return if testing!-width!* and overflowed!*
              or w='failed then fancy!-fail(pos,fl) else nil;
    end ) where obrkp!*=obrkp!*;

symbolic procedure fancy!-convert(l,m);
  % special converters.
  if eqcar(l,'expt) and cadr l= 'e and
     ( m='infix or treesizep(l,20) )
        then {'exp,caddr l}
    else l;

symbolic procedure fancy!-print!-function!-arguments u;
  % u is a parameter list for a function.
    fancy!-in!-brackets(
       u and {'fancy!-inprint, mkquote '!*comma!*,0,mkquote u},
            '!(,'!));

symbolic procedure fancy!-maprint!-atom(l,p);
 fancy!-level
  begin scalar x;
     if(x:=get(l,'fancy!-special!-symbol))
           then fancy!-special!-symbol(x,
                get(l,'fancy!-special!-symbol!-size) or 2)
     else
     if vectorp l then
       <<fancy!-prin2!*("[",0);
         l:=for i:=0:upbv l collect getv(l,i);
         x:=fancy!-inprint(",",0,l);
         fancy!-prin2!*("]",0);
        return x>>
     else
     if not numberp l or (not (l<0) or p<=get('minus,'infix))
         then fancy!-prin2!*(l,'index)
     else
     fancy!-in!-brackets(
          {'fancy!-prin2!*,mkquote l,t}, '!(,'!));
     return if testing!-width!* and overflowed!* then 'failed
              else nil;
  end;

put('print_indexed,'psopfn,'(lambda(u)(flag u 'print!-indexed)));

symbolic procedure fancy!-print!-indexlist l;
   fancy!-print!-indexlist1(l,'!_,nil);

symbolic procedure fancy!-print!-indexlist1(l,op,sep);
  % print index or exponent lists, with or without separator.
 fancy!-level
  begin scalar w,testing!-width!*,obrkp!*;
    testing!-width!* :=t;
    fancy!-prin2!*(op,0);
    fancy!-prin2!*('!{,0);
    w:=fancy!-inprint(sep or 'times,0,l);
    fancy!-prin2!*("}",0);
    return w;
  end;

symbolic procedure fancy!-print!-one!-index i;
 fancy!-level
  begin scalar w,testing!-width!*,obrkp!*;
    testing!-width!* :=t;
    fancy!-prin2!*('!_,0);
    fancy!-prin2!*('!{,0);
    w:=fancy!-inprint('times,0,{i});
    fancy!-prin2!*("}",0);
    return w;
  end;

symbolic procedure fancy!-in!-brackets(u,l,r);
  % put form into brackets (round, curly,...).
  % u: form to be evaluated,
  % l,r: left and right brackets to be inserted.
  fancy!-level
   (begin scalar fp,w,r1,r2,rec;
     rec := {0};
     fancy!-bstack!* := rec . fancy!-bstack!*;
     fancy!-adjust!-bkt!-levels fancy!-bstack!*;
     fp := length fancy!-page!*;
     fancy!-prin2!* (r1:='bkt.nil.l.rec, 2);
     w := eval u;
     fancy!-prin2!* (r2:='bkt.nil.r.rec, 2);
       % no line break: use \left( .. \right) pair.
     if fp = length fancy!-page!* then
     <<car cdr r1:= t; car cdr r2:= t>>;
     return w;
   end)
    where fancy!-bstack!* = fancy!-bstack!*;


symbolic procedure fancy!-adjust!-bkt!-levels u;
   if null u or null cdr u then nil
   else if caar u >= caadr u then
    <<car cadr u := car cadr u +1;
      fancy!-adjust!-bkt!-levels cdr u; >>;

symbolic procedure fancy!-exptpri(l,p);
% Prints expression in an exponent notation.
   (begin scalar !*list,pp,q,w,w1,w2,pos,fl;
      pos:=fancy!-pos!*; fl:=fancy!-line!*;
      pp := not((q:=get('expt,'infix))>p);  % Need to parenthesize
      w1 := cadr l; w2 := caddr l;
      testing!-width!* := t;
      if eqcar(w2,'quotient) and cadr w2 = 1
          and (fixp caddr w2 or liter caddr w2) then
         return fancy!-sqrtpri!*(w1,caddr w2);
      if eqcar(w2,'quotient) and eqcar(cadr w2,'minus)
          then w2 := list('minus,list(car w2,cadadr w2,caddr w2))
          else w2 := negnumberchk w2;
      if fancy!-maprint(w1,q)='failed
            then return fancy!-fail(pos,fl);
     fancy!-prin2!*("^",0);
     if eqcar(w2,'quotient) and fixp cadr w2 and fixp caddr w2 then
      <<fancy!-prin2!*("{",0); w:=fancy!-inprint('!/,0,cdr w2);
                 fancy!-prin2!*("}",0)>>
           else w:=fancy!-maprint!-tex!-bkt(w2,0,nil);
     if w='failed then return fancy!-fail(pos,fl) ;
    end) where !*ratpri=!*ratpri,
           testing!-width!*=testing!-width!*;

put('expt,'fancy!-pprifn,'fancy!-exptpri);

symbolic procedure fancy!-inprint(op,p,l);
  (begin scalar x,y,w, pos,fl;
     pos:=fancy!-pos!*;
     fl:=fancy!-line!*;
      % print product of quotients using *.
     if op = 'times and eqcar(car l,'quotient) and
       cdr l and eqcar(cadr l,'quotient) then
        op:='!*;
     if op eq 'plus and !*revpri then l := reverse l;
     if not get(op,'alt) then
     <<
        if op eq 'not then
         << fancy!-oprin op;
            return  fancy!-maprint(car l,get('not,'infix));
         >>;
        if op eq 'setq and not atom (x := car reverse l)
             and idp car x and (y := getrtype x)
             and (y := get(get(y,'tag),'fancy!-setprifn))
            then return apply2(y,car l,x);
        if not atom car l and idp caar l
              and
           ((x := get(caar l,'fancy!-prifn))
                   or (x := get(caar l,'fancy!-pprifn)))
              and (get(x,op) eq 'inbrackets)
            % to avoid mix up of indices and exponents.
          then<<
               fancy!-in!-brackets(
                {'fancy!-maprint,mkquote car l,p}, '!(,'!));
              >>
           else if !*nosplit and not testing!-width!* then
                fancy!-prinfit(car l, p, nil)
           else w:=fancy!-maprint(car l, p);
          l := cdr l
      >>;
     if testing!-width!* and (overflowed!* or w='failed)
            then return fancy!-fail(pos,fl);
     if !*list and obrkp!* and memq(op,'(plus minus)) then
        <<sumlevel!*:=sumlevel!*+1;
          tablevel!* := tablevel!* #+ 1>>;
     if !*nosplit and not testing!-width!* then
          % main line:
         fancy!-inprint1(op,p,l)
     else w:=fancy!-inprint2(op,p,l);
     if testing!-width!* and w='failed then return fancy!-fail(pos,fl);
   end
   ) where tablevel!*=tablevel!*, sumlevel!*=sumlevel!*;


symbolic procedure fancy!-inprint1(op,p,l);
   % main line (top level) infix printing, allow line break;
  begin scalar lop,space;
   space := flagp(op,'spaced);
   for each v in l do
   <<lop := op;
     if op='plus and eqcar(v,'minus) then
       <<lop := 'minus; v:= cadr v>>;
     if space then fancy!-prin2!*("\,",1);
     if 'failed = fancy!-oprin lop then
      <<fancy!-terpri!* nil; fancy!-oprin lop>>;
     if space then fancy!-prin2!*("\,",1);
     fancy!-prinfit(negnumberchk v, p, nil)
   >>;
  end;

symbolic procedure fancy!-inprint2(op,p,l);
   % second line
  begin scalar lop,space,w;
   space := flagp(op,'spaced);
   for each v in l do
    if not testing!-width!* or w neq 'failed then
     <<lop:=op;
       if op='plus and eqcar(v,'minus) then
              <<lop := 'minus; v:= cadr v>>;
       if space then fancy!-prin2!*("\,",1);
       fancy!-oprin lop;
       if space then fancy!-prin2!*("\,",1);
       if w neq 'failed then w:=fancy!-maprint(negnumberchk v,p)
     >>;
   return w;
  end;

symbolic procedure fancy!-inprintlist(op,p,l);
   % inside algebraic list
fancy!-level
 begin scalar fst,w,v;
  loop:
   if null l then return w;
   v := car l; l:= cdr l;
   if fst then
       << fancy!-prin2!*("\,",1);
          w:=fancy!-oprin op;
          fancy!-prin2!*("\,",1);
       >>;
   if w eq 'failed  and testing!-width!* then return w;
   w:= if w eq 'failed then fancy!-prinfit(v,0,op)
                    else fancy!-prinfit(v,0,nil);
   if w eq 'failed  and testing!-width!* then return w;
   fst := t;
   goto loop;
  end;

put('times,'fancy!-prtch,"\,");

symbolic procedure fancy!-oprin op;
 fancy!-level
  begin scalar x;
    if (x:=get(op,'fancy!-prtch)) then fancy!-prin2!*(x,1)
      else
    if (x:=get(op,'fancy!-infix!-symbol))
           then fancy!-special!-symbol(x,get(op,'fancy!-symbol!-length)
                                            or 4)
      else
    if null(x:=get(op,'prtch)) then fancy!-prin2!*(op,t)
      else
    << if !*list and obrkp!* and op memq '(plus minus)
        and sumlevel!*=2
       then
        if testing!-width!* then return 'failed
            else fancy!-terpri!* t;
       fancy!-prin2!*(x,t);
    >>;
    if overflowed!* then return 'failed
   end;

put('alpha,'fancy!-special!-symbol,"\alpha");
put('beta,'fancy!-special!-symbol,"\beta");
put('gamma,'fancy!-special!-symbol,"\gamma");
put('delta,'fancy!-special!-symbol,"\delta");
put('epsilon,'fancy!-special!-symbol,"\epsilon");
put('zeta,'fancy!-special!-symbol,"\zeta");
put('eta,'fancy!-special!-symbol,"\eta");
put('theta,'fancy!-special!-symbol,"\theta");
put('iota,'fancy!-special!-symbol,"\iota");
put('kappa,'fancy!-special!-symbol,"\kappa");
put('lambda,'fancy!-special!-symbol,"\lambda");
put('mu,'fancy!-special!-symbol,"\mu");
put('nu,'fancy!-special!-symbol,"\nu");
put('xi,'fancy!-special!-symbol,"\xi");
put('pi,'fancy!-special!-symbol,"\pi");
put('rho,'fancy!-special!-symbol,"\rho");
put('sigma,'fancy!-special!-symbol,"\sigma");
put('tau,'fancy!-special!-symbol,"\tau");
put('upsilon,'fancy!-special!-symbol,"\upsilon");
put('phi,'fancy!-special!-symbol,"\phi");
put('chi,'fancy!-special!-symbol,"\chi");
put('psi,'fancy!-special!-symbol,"\psi");
put('omega,'fancy!-special!-symbol,"\omega");

if 'a neq '!A then deflist('(
    (!Alpha 65) (!Beta 66) (!Chi 67) (!Delta 68)
    (!Epsilon 69)(!Phi 70) (!Gamma 71)(!Eta 72)
    (!Iota 73) (!vartheta 74)(!Kappa 75)(!Lambda 76)
    (!Mu 77)(!Nu 78)(!O 79)(!Pi 80)(!Theta 81)
    (!Rho 82)(!Sigma 83)(!Tau 84)(!Upsilon 85)
    (!Omega 87) (!Xi 88)(!Psi 89)(!Zeta 90)
    (!varphi 106)
       ),'fancy!-special!-symbol);

put('infinity,'fancy!-special!-symbol,"\infty");

% some symbols form the upper ASCII part of the symbol font

put('partial!-df,'fancy!-special!-symbol,182);
put('partial!-df,'fancy!-symbol!-length,8);
put('empty!-set,'fancy!-special!-symbol,198);
put('not,'fancy!-special!-symbol,216);
put('not,'fancy!-infix!-symbol,216);

 % symbols as infix opertors
put('leq,'fancy!-infix!-symbol,163);
put('geq,'fancy!-infix!-symbol,179);
put('neq,'fancy!-infix!-symbol,185);
put('intersection,'fancy!-infix!-symbol,199);
put('union,'fancy!-infix!-symbol,200);
put('member,'fancy!-infix!-symbol,206);
put('and,'fancy!-infix!-symbol,217);
put('or,'fancy!-infix!-symbol,218);
put('when,'fancy!-infix!-symbol,239);
put('!*wcomma!*,'fancy!-infix!-symbol,",\,");

put('replaceby,'fancy!-infix!-symbol,222);
put('replaceby,'fancy!-symbol!-length,8);

 % symbols as prefix functions
% put('gamma,'fancy!-functionsymbol,71);  % big Gamma
%
put('!~,'fancy!-functionsymbol,34);     % forall
put('!~,'fancy!-symbol!-length,8);

 % arbint, arbcomplex.
put('arbcomplex,'fancy!-functionsymbol,227);
put('arbint,'fancy!-functionsymbol,226);

flag('(arbcomplex arbint),'print!-indexed);

% flag('(delta),'print!-indexed);         % Dirac delta symbol.
% David Hartley voted against..

% The following definitions allow for more natural printing of
% conditional expressions within rule lists.

symbolic procedure fancy!-condpri0 u;
   fancy!-condpri(u,0);

symbolic procedure fancy!-condpri(u,p);
 fancy!-level
  begin scalar w;
    if p>0 then fancy!-prin2 "\bigl(";
    while (u := cdr u) and w neq 'failed do
      <<if not(caar u eq 't)
            then <<fancy!-prin2 'if; fancy!-prin2 " ";
                   w:=fancy!-maprin0 caar u;
                   fancy!-prin2 "\,"; fancy!-prin2 'then;
                   fancy!-prin2 "\,">>;
          if w neq 'failed then w := fancy!-maprin0 cadar u;
          if cdr u then <<fancy!-prin2 "\,";
                       fancy!-prin2 'else; fancy!-prin2 "\,">>>>;
     if p>0 then fancy!-prin2 "\bigr)";
     if overflowed!* or w='failed then return 'failed;
   end;

put('cond,'fancy!-pprifn,'fancy!-condpri);
put('cond,'fancy!-flatprifn,'fancy!-condpri0);

symbolic procedure fancy!-revalpri u;
   fancy!-maprin0 fancy!-unquote cadr u;

symbolic procedure fancy!-unquote u;
  if eqcar(u,'list) then for each x in cdr u collect
      fancy!-unquote x
  else if eqcar(u,'quote) then cadr u else u;

put('aeval,'fancy!-prifn,'fancy!-revalpri);
put('aeval!*,'fancy!-prifn,'fancy!-revalpri);
put('reval,'fancy!-prifn,'fancy!-revalpri);
put('reval!*,'fancy!-prifn,'fancy!-revalpri);

put('aminusp!:,'fancy!-prifn,'fancy!-patpri);
put('aminusp!:,'fancy!-pat,'(lessp !&1 0));

symbolic procedure fancy!-patpri u;
  begin scalar p;
    p:=subst(fancy!-unquote  cadr u,'!&1,
             get(car u,'fancy!-pat));
    return fancy!-maprin0 p;
  end;

symbolic procedure fancy!-boolvalpri u;
   fancy!-maprin0 cadr u;

put('boolvalue!*,'fancy!-prifn,'fancy!-boolvalpri);

symbolic procedure fancy!-quotpri u;
   begin scalar n1,n2,fl,w,pos,testing!-width!*;
     if overflowed!* then return 'failed;
     testing!-width!*:=t;
     pos:=fancy!-pos!*;
     fl:=fancy!-line!*;
     fancy!-prin2!*("\frac",0);
     w:=fancy!-maprint!-tex!-bkt(cadr u,0,t);
     n1 := fancy!-pos!*;
     if w='failed
       then return fancy!-fail(pos,fl);
     fancy!-pos!* := pos;
     w := fancy!-maprint!-tex!-bkt(caddr u,0,nil);
     n2 := fancy!-pos!*;
     if w='failed
       then return fancy!-fail(pos,fl);
     fancy!-pos!* := max(n1,n2);
     return t;
  end;

symbolic procedure fancy!-maprint!-tex!-bkt(u,p,m);
  % Produce expression with tex brackets {...} if
  % necessary. Ensure that {} unit is in same formula.
  % If m=t brackets will be inserted in any case.
  begin scalar w,pos,fl,testing!-width!*;
    testing!-width!*:=t;
    pos:=fancy!-pos!*;
    fl:=fancy!-line!*;
   if not m and (numberp u and 0<=u and u <=9 or liter u) then
   << fancy!-prin2!*(u,t);
      return if overflowed!* then fancy!-fail(pos,fl);
   >>;
   fancy!-prin2!*("{",0);
   w := fancy!-maprint(u,p);
   fancy!-prin2!*("}",0);
   if w='failed then return fancy!-fail(pos,fl);
  end;

symbolic procedure fancy!-fail(pos,fl);
 <<
     overflowed!* := nil;
     fancy!-pos!* := pos;
     fancy!-line!* := fl;
     'failed
 >>;

put('quotient,'fancy!-prifn,'fancy!-quotpri);

symbolic procedure fancy!-prinfit(u, p, op);
% Display u (as with maprint) with op in front of it, but starting
% a new line before it if there would be overflow otherwise.
   begin scalar pos,fl,w,ll,f;
     if pairp u and (f:=get(car u,'fancy!-prinfit)) then
        return apply(f,{u,p,op});
     pos:=fancy!-pos!*;
     fl:=fancy!-line!*;
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       if w neq 'failed then w := fancy!-maprint(u,p);
     end;
     if w neq 'failed then return t;
     fancy!-line!*:=fl; fancy!-pos!*:=pos;
     if testing!-width!* and w eq 'failed then return w;

     if op='plus and eqcar(u,'minus) then <<op := 'minus; u:=cadr u>>;
     w:=if op then fancy!-oprin op;
       % if the operator causes the overflow, we break the line now.
     if w eq 'failed then
     <<fancy!-terpri!* nil;
       if op then fancy!-oprin op;
       return fancy!-maprint(u, p);>>;
       % if at least half the line is still free and the
       % object causing the overflow has been a number,
       % let it break.
     if fancy!-pos!* < (ll:=linelength(nil)) then
             if numberp u then return fancy!-prin2number u else
         if eqcar(u,'!:rd!:) then return fancy!-rdprin u;
       % generate a line break if we are not just behind an
       % opening bracket at the beginning of a line.
     if fancy!-pos!* > linelength nil #/ 2 or
          not eqcar(fancy!-last!-symbol(),'bkt) then
           fancy!-terpri!* nil;
     return fancy!-maprint(u, p);
   end;

%-----------------------------------------------------------
%
%   support for print format property
%
%-----------------------------------------------------------

symbolic procedure print_format(f,pat);
  % Assign a print pattern p to the operator form f.
put(car f, 'print!-format, (cdr f . pat) . get(car f, 'print!-format));

symbolic operator print_format;

symbolic procedure fancy!-print!-format(u,p);
 fancy!-level
  begin scalar fmt,fmtl,a;
   fmtl:=get(car u,'print!-format);
 l:
   if null fmtl then return 'failed;
   fmt := car fmtl; fmtl := cdr fmtl;
   if length(car fmt) neq length cdr u then goto l;
   a:=pair(car fmt,cdr u);
   return fancy!-print!-format1(cdr fmt,p,a);
  end;

symbolic procedure fancy!-print!-format1(u,p,a);
  begin scalar w,x,y,pl,bkt,obkt,q;
   if eqcar(u,'list) then u:= cdr u;
   while u and w neq 'failed do
   <<x:=car u; u:=cdr u;
     if eqcar(x,'list) then x:=cdr x;
     obkt := bkt; bkt:=nil;
     if obkt then fancy!-prin2!*('!{,0);
     w:=if pairp x then fancy!-print!-format1(x,p,a) else
        if memq(x,'(!( !) !, !. !|)) then
         <<if x eq '!( then <<pl:=p.pl; p:=0>> else
           if x eq '!) then <<p:=car pl; pl:=cdr pl>>;
           fancy!-prin2!*(x,1)>> else
        if x eq '!_ or x eq '!^ then <<bkt:=t;fancy!-prin2!*(x,0)>> else
        if q:=assoc(x,a) then fancy!-maprint(cdr q,p) else
        fancy!-maprint(x,p);
     if obkt then fancy!-prin2!*('!},0);
    >>;
    return w;
  end;


%-----------------------------------------------------------
%
%   some operator specific print functions
%
%-----------------------------------------------------------

symbolic procedure fancy!-prefix!-operator(u);
 % Print as function, but with a special character.
   begin scalar sy;
     sy :=
       get(u,'fancy!-functionsymbol) or get(u,'fancy!-special!-symbol);
     if sy
      then fancy!-special!-symbol(sy,get(u,'fancy!-symbol!-length) or 2)
      else fancy!-prin2!*(u,t);
   end;

put('sqrt,'fancy!-prifn,'fancy!-sqrtpri);

symbolic procedure fancy!-sqrtpri(u);
    fancy!-sqrtpri!*(cadr u,2);

symbolic procedure fancy!-sqrtpri!*(u,n);
  fancy!-level
   begin
     if not numberp n and not liter n then return 'failed;
     fancy!-prin2!*("\sqrt",0);
     if n neq 2 then
     <<fancy!-prin2!*("[",0);
       fancy!-prin2!*("\,",1);
       fancy!-prin2!*(n,t);
       fancy!-prin2!*("]",0);
     >>;
     return fancy!-maprint!-tex!-bkt(u,0,t);
   end;


symbolic procedure fancy!-sub(l,p);
% Prints expression in an exponent notation.
  if get('expt,'infix)<=p then
      fancy!-in!-brackets({'fancy!-sub,mkquote l,0},'!(,'!))
    else
   fancy!-level
    begin scalar eqs,w;
      l:=cdr l;
      while cdr l do <<eqs:=append(eqs,{car l}); l:=cdr l>>;
      l:=car l;
      testing!-width!* := t;
      w := fancy!-maprint(l,get('expt,'infix));
      if w='failed then return w;
      fancy!-prin2!*("\bigl",0);
      fancy!-prin2!*("|",1);
      fancy!-prin2!*('!_,0);
      fancy!-prin2!*("{",0);
      w:=fancy!-inprint('!*comma!*,0,eqs);
      fancy!-prin2!*("}",0);
      return w;
   end;

put('sub,'fancy!-pprifn,'fancy!-sub);


put('factorial,'fancy!-pprifn,'fancy!-factorial);

symbolic procedure fancy!-factorial(u,n);
  fancy!-level
   begin scalar w;
     w := (if atom cadr u then fancy!-maprint(cadr u,9999)
              else
           fancy!-in!-brackets({'fancy!-maprint,mkquote cadr u,0},
                               '!(,'!))
          );
     fancy!-prin2!*("!",2);
     return w;
   end;

put('binomial,'fancy!-prifn,'fancy!-binomial);

symbolic procedure fancy!-binomial(u,n);
  fancy!-level
   begin scalar w1,w2;
     fancy!-prin2!*("\left(\begin{array}{c}",2);
     w1 := fancy!-maprint(cadr u,0);
     fancy!-prin2!*("\\",0);
     w2 := fancy!-maprint(caddr u,0);
     fancy!-prin2!*("\end{array}\right)",2);
     if w1='failed or w2='failed then return 'failed;
   end;

symbolic procedure fancy!-intpri(u,p);
  if p>get('times,'infix) then
    fancy!-in!-brackets({'fancy!-intpri,mkquote u,0},'!(,'!))
   else
  fancy!-level
  begin scalar w1,w2,lo,hi,var;
     var := caddr u;
     if cdddr u then lo:=cadddr u;
     if lo and cddddr u then hi := car cddddr u;
     if fancy!-height(cadr u,1.0) > 3 then
         fancy!-prin2!*("\Int ",0)
       else
         fancy!-prin2!*("\int ",0);
     if lo then  << fancy!-prin2!*('!_,0);
                  fancy!-maprint!-tex!-bkt(lo,0,nil);
                 >>;
     if hi then << fancy!-prin2!*('!^,0);
                  fancy!-maprint!-tex!-bkt(hi,0,nil);
                 >>;
     w1:=fancy!-maprint(cadr u,0);
     fancy!-prin2!*("\,d\,",2);
     w2:=fancy!-maprint(caddr u,0);
     if w1='failed or w2='failed then return 'failed;
   end;

symbolic procedure fancy!-height(u,h);
  % estimate the height of an expression.
    if atom u then h
    else if car u = 'minus then fancy!-height(cadr u,h)
    else if car u = 'plus or car u = 'times then
      eval('max. for each w in cdr u collect fancy!-height(w,h))
    else if car u = 'expt then
         fancy!-height(cadr u,h) + fancy!-height(caddr u,h*0.8)
    else if car u = 'quotient then
         fancy!-height(cadr u,h) + fancy!-height(caddr u,h)
    else if get(car u,'simpfn) then fancy!-height(cadr u,h)
    else h;

put('int,'fancy!-pprifn,'fancy!-intpri);

symbolic procedure fancy!-sumpri!*(u,p,mode);
  if p>get('minus,'infix) then
    fancy!-in!-brackets({'fancy!-sumpri!*,mkquote u,0,mkquote mode},
                         '!(,'!))
   else
  fancy!-level
   begin scalar w,w0,w1,lo,hi,var;
     var := caddr u;
     if cdddr u then lo:=cadddr u;
     if lo and cddddr u then hi := car cddddr u;
     w:=if lo then {'equal,var,lo} else var;
     if mode = 'sum then
        fancy!-prin2!*("\sum",0) % big SIGMA
     else if mode = 'prod then
        fancy!-prin2!*("\prod",0); % big PI
     fancy!-prin2!*('!_,0);
     fancy!-prin2!*('!{,0);
     if w then w0:=fancy!-maprint(w,0);
     fancy!-prin2!*('!},0);
     if hi then <<fancy!-prin2!*('!^,0);
                  fancy!-maprint!-tex!-bkt(hi,0,nil);
                 >>;
     fancy!-prin2!*('!\!, ,1);
     w1:=fancy!-maprint(cadr u,0);
     if w0='failed or w1='failed then return 'failed;
   end;

symbolic procedure fancy!-sumpri(u,p); fancy!-sumpri!*(u,p,'sum);

put('sum,'fancy!-pprifn,'fancy!-sumpri);
put('infsum,'fancy!-pprifn,'fancy!-sumpri);

symbolic procedure fancy!-prodpri(u,p); fancy!-sumpri!*(u,p,'prod);

put('prod,'fancy!-pprifn,'fancy!-prodpri);

symbolic procedure fancy!-limpri(u,p);
  if p>get('minus,'infix) then
    fancy!-in!-brackets({'fancy!-limpri,mkquote u,0},'!(,'!))
   else
  fancy!-level
   begin scalar w,lo,var;
     var := caddr u;
     if cdddr u then lo:=cadddr u;
     fancy!-prin2!*("\lim",6);
     fancy!-prin2!*('!_,0);
     fancy!-prin2!*('!{,0);
     fancy!-maprint(var,0);
     fancy!-prin2!*("\to",0);
     fancy!-maprint(lo,0);
     fancy!-prin2!*('!},0);
     w:=fancy!-maprint(cadr u,0);
     return w;
   end;

put('limit,'fancy!-pprifn,'fancy!-limpri);

symbolic procedure fancy!-listpri(u);
 fancy!-level
 (if null cdr u then fancy!-maprint('empty!-set,0)
   else
  fancy!-in!-brackets(
   {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote cdr u},
               '!{,'!})
  );

put('list,'fancy!-prifn,'fancy!-listpri);
put('list,'fancy!-flatprifn,'fancy!-listpri);

put('!*sq,'fancy!-reform,'fancy!-sqreform);

symbolic procedure fancy!-sqreform u;
    prepsq!* sqhorner!* cadr u;

put('df,'fancy!-pprifn,'fancy!-dfpri);

% 9-Dec-93: 'total repaired

symbolic procedure fancy!-dfpri(u,l);
  (if flagp(cadr u,'print!-indexed) or
      pairp cadr u and flagp(caadr u,'print!-indexed)
    then fancy!-dfpriindexed(u,l)
   else if m = 'partial then fancy!-dfpri0(u,l,'partial!-df)
   else if m = 'total then fancy!-dfpri0(u,l,'!d)
   else if m = 'indexed then fancy!-dfpriindexed(u,l)
   else rederr "unknown print mode for DF")
        where m=fancy!-mode('fancy_print_df);

symbolic procedure fancy!-partialdfpri(u,l);
     fancy!-dfpri0(u,l,'partial!-df);

symbolic procedure fancy!-dfpri0(u,l,symb);
 if null cddr u then fancy!-maprin0{'times,symb,cadr u} else
 if l >= get('expt,'infix) then % brackets if exponented
  fancy!-in!-brackets({'fancy!-dfpri0,mkquote u,0,mkquote symb},
                      '!(,'!))
   else
 fancy!-level
  begin scalar x,d,q; integer n,m;
    u:=cdr u;
    q:=car u;
    u:=cdr u;
    while u do
    <<x:=car u; u:=cdr u;
      if u and numberp car u then
      <<m:=car u; u := cdr u>> else m:=1;
      n:=n+m;
      d:= append(d,{symb,if m=1 then x else {'expt,x,m}});
    >>;
    return fancy!-maprin0
    {'quotient, {'times,if n=1 then symb else
                                    {'expt,symb,n},q},
       'times. d};
  end;

symbolic procedure fancy!-dfpriindexed(u,l);
   if null cddr u then fancy!-maprin0{'times,'partial!-df,cadr u} else
   begin scalar w;
      w:=fancy!-maprin0 cadr u;
      if testing!-width!* and w='failed then return w;
      w :=fancy!-print!-indexlist fancy!-dfpriindexedx(cddr u,nil);
      return w;
   end;

symbolic procedure fancy!-dfpriindexedx(u,p);
  if null u then nil else
  if numberp car u then
   append(for i:=2:car u collect p,fancy!-dfpriindexedx(cdr u,p))
     else
  car u . fancy!-dfpriindexedx(cdr u,car u);

put('!:rd!:,'fancy!-prifn,'fancy!-rdprin);
put('!:rd!:,'fancy!-flatprifn,'fancy!-rdprin);

symbolic procedure fancy!-rdprin u;
 fancy!-level
  begin scalar digits; integer dotpos,xp;
   u:=rd!:explode u;
   digits := car u; xp := cadr u; dotpos := caddr u;
   return fancy!-rdprin1(digits,xp,dotpos);
  end;

symbolic procedure fancy!-rdprin1(digits,xp,dotpos);
  begin scalar str;
   if xp>0 and dotpos+xp<length digits-1 then
      <<dotpos := dotpos+xp; xp:=0>>;
    % build character string from number.
   for i:=1:dotpos do
   <<str := car digits . str;
     digits := cdr digits; if null digits then digits:='(!0);
   >>;
   str := '!. . str;
   for each c in digits do str :=c.str;
   if not(xp=0) then
   <<str:='!e.str;
     for each c in explode2 xp do str:=c.str>>;
   if testing!-width!* and
      fancy!-pos!* + 2#*length str > 2 #* linelength nil then
        return 'failed;
   fancy!-prin2number1 reversip str;
  end;

put('!:cr!:,'fancy!-pprifn,'fancy!-cmpxprin);
put('!:cr!:,'fancy!-pprifn,'fancy!-cmpxprin);

symbolic procedure fancy!-cmpxprin(u,l);
   begin scalar rp,ip;
     rp:=reval {'repart,u}; ip:=reval {'impart,u};
     return fancy!-maprint(
       if ip=0 then rp else
       if rp=0 then {'times,ip,'!i} else
        {'plus,rp,{'times,ip,'!i}},l);
   end;

symbolic procedure fancy!-dn!:prin u;
 begin scalar lst; integer dotpos,ex;
  lst := bfexplode0x (cadr u, cddr u);
  ex := cadr lst;
  dotpos := caddr lst;
  lst := car lst;
  return fancy!-rdprin1 (lst,ex,dotpos)
 end;

put ('!:dn!:, 'fancy!-prifn, 'fancy!-dn!:prin);

fmp!-switch t;

endmodule;


%-------------------------------------------------------

module f;   % Matrix printing routines.


fluid '(!*nat);

fluid '(obrkp!*);

symbolic procedure fancy!-setmatpri(u,v);
   fancy!-matpri1(cdr v,u);

put('mat,'fancy!-setprifn,'fancy!-setmatpri);

symbolic procedure fancy!-matpri u;
   fancy!-matpri1(cdr u,nil);


put('mat,'fancy!-prifn,'fancy!-matpri);

symbolic procedure fancy!-matpri1(u,x);
   % Prints a matrix canonical form U with name X.
   % Tries to do fancy display if nat flag is on.
  begin scalar w;
     w := fancy!-matpri2(u,x,nil);
     if w neq 'failed or testing!-width!* then return w;
     fancy!-matpri3(u,x);
  end;

symbolic procedure fancy!-matpri2(u,x,bkt);
  % Tries to print matrix as compact block.
  fancy!-level
    begin scalar w,testing!-width!*,fl,fp,fmat,row,elt,fail;
      integer cols,rows,rw,maxpos;
      testing!-width!*:=t;
      rows := length u;
      cols := length car u;
      if cols*rows>400 then return 'failed;

      if x then
      << fancy!-maprint(x,0); fancy!-prin2!*(":=",4) >>;
      fl := fancy!-line!*; fp := fancy!-pos!*;
         %  remaining room for the columns.
      rw := linelength(nil)-2 -(fancy!-pos!*+2);
      rw := rw/cols;
      fmat := for each row in u collect
        for each elt in row collect
          if not fail then
          <<fancy!-line!*:=nil; fancy!-pos!*:=0;
            w:=fancy!-maprint(elt,0);
            if fancy!-pos!*>maxpos then maxpos:=fancy!-pos!*;
            if w='failed or fancy!-pos!*>rw
              then fail:=t else
               (fancy!-line!*.fancy!-pos!*)
          >>;
     if fail then return 'failed;
     testing!-width!* := nil;
       % restore output line.
     fancy!-pos!* := fp; fancy!-line!* := fl;
       % TEX header
     fancy!-prin2!*(bldmsg("\left%w\begin{array}{",
                        if bkt then car bkt else "("),0);
     for i:=1:cols do fancy!-prin2!*("c",0);
     fancy!-prin2!*("}",0);
       % join elements.
     while fmat do
     <<row := car fmat; fmat:=cdr fmat;
       while row do
       <<elt:=car row; row:=cdr row;
         fancy!-line!* := append(car elt,fancy!-line!*);
         if row then fancy!-line!* :='!& . fancy!-line!*
          else if fmat then
             fancy!-line!* := "\\". fancy!-line!*;
       >>;
     >>;
     fancy!-prin2!*(bldmsg("\end{array}\right%w",
                        if bkt then cdr bkt else ")"),0);
      % compute total horizontal extent of matrix
     fancy!-pos!* := fp + maxpos*(cols+1);
    return t;
    end;


symbolic procedure fancy!-matpri3(u,x);
  if null x then fancy!-matpriflat('mat.u) else
   begin scalar obrkp!*,!*list;
      integer r,c;
      obrkp!* := nil;
      if null x then x:='mat;
      fancy!-terpri!*;
      for each row in u do
      <<r:=r+1; c:=0;
        for each elt in row do
        << c:=c+1;
           if not !*nero then
           << fancy!-prin2!*(x,t);
              fancy!-print!-indexlist {r,c};
              fancy!-prin2!*(":=",t);
              fancy!-maprint(elt,0);
              fancy!-terpri!* t;
           >>;
        >>;
      >>;
   end;

symbolic procedure fancy!-matpriflat(u);
 begin
  fancy!-oprin 'mat;
  fancy!-in!-brackets(
   {'fancy!-matpriflat1,mkquote '!*wcomma!*,0,mkquote cdr u},
               '!(,'!));
 end;

symbolic procedure fancy!-matpriflat1(op,p,l);
   % inside algebraic list
 begin scalar fst,w;
   for each v in l do
     <<if fst then
       << fancy!-prin2!*("\,",1);
          fancy!-oprin op;
          fancy!-prin2!*("\,",1);
       >>;
  % if the next row does not fit on the current print line
  % we move it completely to a new line.
       if fst then
        w:= fancy!-level
         fancy!-in!-brackets(
          {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote v},
            '!(,'!)) where testing!-width!*=t;
       if w eq 'failed then fancy!-terpri!* t;
       if not fst or w eq 'failed then
         fancy!-in!-brackets(
          {'fancy!-inprintlist,mkquote '!*wcomma!*,0,mkquote v},
            '!(,'!));
       fst := t;
     >>;
  end;

put('mat,'fancy!-flatprifn,'fancy!-matpriflat);

symbolic procedure fancy!-matfit(u,p,op);
% Prinfit routine for matrix.
% a new line before it if there would be overflow otherwise.
 fancy!-level
   begin scalar pos,fl,fp,w,ll;
     pos:=fancy!-pos!*;
     fl:=fancy!-line!*;
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       if w neq 'failed then w := fancy!-matpri(u);
     end;
     if w neq 'failed or
       (w eq 'failed and testing!-width!*) then return w;
     fancy!-line!*:=fl; fancy!-pos!*:=pos; w:=nil;
     fp := fancy!-page!*;
% matrix: give us a second chance with a fresh line
     begin scalar testing!-width!*;
       testing!-width!*:=t;
       if op then w:=fancy!-oprin op;
       fancy!-terpri!* nil;
       if w neq 'failed then w := fancy!-matpri u;
     end;
     if w neq 'failed then return t;
     fancy!-line!*:=fl; fancy!-pos!*:=pos; fancy!-page!*:=fp;

     ll:=linelength nil;
     if op then fancy!-oprin op;
     if atom u or fancy!-pos!* > ll #/ 2 then fancy!-terpri!* nil;
     return fancy!-matpriflat(u);
   end;

put('mat,'fancy!-prinfit,'fancy!-matfit);

put('taylor!*,'fancy!-reform,'Taylor!*print1);

endmodule;

module fancy_specfn;

put('besseli,'fancy!-prifn,'fancy!-bessel);
put('besselj,'fancy!-prifn,'fancy!-bessel);
put('bessely,'fancy!-prifn,'fancy!-bessel);
put('besselk,'fancy!-prifn,'fancy!-bessel);
put('besseli,'fancy!-functionsymbol,'(ascii 73));
put('besselj,'fancy!-functionsymbol,'(ascii 74));
put('bessely,'fancy!-functionsymbol,'(ascii 89));
put('besselk,'fancy!-functionsymbol,'(ascii 75));

symbolic procedure fancy!-bessel(u);
 fancy!-level
  begin scalar w;
   fancy!-prefix!-operator car u;
   w:=fancy!-print!-one!-index cadr u;
   if testing!-width!* and w eq 'failed then return w;
   return fancy!-print!-function!-arguments cddr u;
  end;

% Hypergeometric functions.

put('empty!*,'fancy!-special!-symbol,32);

put('hypergeometric,'fancy!-prifn,'fancy!-hypergeometric);

symbolic procedure fancy!-hypergeometric u;
 fancy!-level
  begin scalar w,a1,a2,a3;
   a1 :=cdr cadr u;
   a2 := cdr caddr u;
   a3 := cadddr u;
   fancy!-special!-symbol(get('empty!*,'fancy!-special!-symbol),nil);
   w:=fancy!-print!-one!-index length a1;
   if testing!-width!* and w eq 'failed then return w;
   fancy!-prin2!*("F",nil);
   w:=fancy!-print!-one!-index length a2;
   if testing!-width!* and w eq 'failed then return w;
   fancy!-prin2!*("(",nil);
   w := w eq 'failed or fancy!-print!-indexlist1(a1,'!^,'!*comma!*);
   w := w eq 'failed or fancy!-print!-indexlist1(a2,'!_,'!*comma!*);
   fancy!-prin2!*("\,",1);
   w := w eq 'failed or fancy!-special!-symbol(124,1);    % vertical bar
   fancy!-prin2!*("\,",1);
   w := w eq 'failed or fancy!-prinfit(a3,0,nil);
   fancy!-prin2!*(")",nil);
   return w;
  end;

% hypergeometric({1,2,u/w,v},{5,6},sqrt x);

put('meijerg,'fancy!-prifn,'fancy!-meijerG);

symbolic procedure fancy!-meijerG u;
 fancy!-level
  begin scalar w,a1,a2,a3;
   integer n,m,p,q;
   a1 :=cdr cadr u;
   a2 := cdr caddr u;
   a3 := cadddr u;
   m:=length cdar a2;
   n:=length cdar a1;
   a1 := append(cdar a1 , cdr a1);
   a2 := append(cdar a2 , cdr a2);
   p:=length a1; q:=length a2;
   fancy!-prin2!*("G",nil);
   w := w eq 'failed or
        fancy!-print!-indexlist1({m,n},'!^,nil);
   w := w eq 'failed or
        fancy!-print!-indexlist1({p,q},'!_,nil);
   fancy!-prin2!*("(",nil);
   w := w eq 'failed or fancy!-prinfit(a3,0,nil);
   w := w eq 'failed or fancy!-special!-symbol(124,1);    % vertical bar
   w := w eq 'failed or fancy!-print!-indexlist1(a1,'!^,'!*comma!*);
   w := w eq 'failed or fancy!-print!-indexlist1(a2,'!_,'!*comma!*);
   fancy!-prin2!*(")",nil);
   return w;
  end;

% meijerg({{},1},{{0}},x);

endmodule;

end;
