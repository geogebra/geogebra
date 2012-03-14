module rprint;  % The Standard LISP to REDUCE pretty-printer.

% Author: Anthony C. Hearn.
% Modifications by: Francis J. Wright.

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


create!-package('(rprint),'(util));

fluid '(!*lower !*n buffp combuff!* curmark curpos orig pretop
        pretoprinf rmar rprifn!* rterfn!*);

comment RPRIFN!* allows output from RPRINT to be handled differently,
        RTERFN!* allows end of lines to be handled differently;

pretop := 'op; pretoprinf := 'oprinf;

symbolic procedure rprint u;
   begin integer !*n; scalar buff,buffp,curmark,rmar,x;
      curmark := 0;
      buff := buffp := list list(0,0);
      rmar := linelength nil;
      x := get('!*semicol!*,pretop);
      !*n := 0;
      mprino1(u,list(caar x,cadar x));
      prin2ox ";";
      omarko curmark;
      prinos buff
   end;

symbolic procedure rprin1 u;
   begin scalar buff,buffp,curmark,x;
      curmark := 0;
      buff := buffp := list list(0,0);
      x := get('!*semicol!*,pretop);
      mprino1(u,list(caar x,cadar x));
      omarko curmark;
      prinos buff
   end;

symbolic procedure mprino u; mprino1(u,list(0,0));

symbolic procedure mprino1(u,v);
   begin scalar x;
        if x := atsoc(u,combuff!*)
          then <<for each y in cdr x do comprox y;
                 combuff!* := delete(x,combuff!*)>>;
      if numberp u and u<0 and (x := get('difference,pretop))
        then return begin scalar p;
        x := car x;
        p := not(car x>cadr v) or not(cadr x>car v);
        if p then prin2ox "(";
        prinox u;
        if p then prinox ")"
       end
       else if atom u then return prinox u
      else if not atom car u and (x:=strangeop u)
          then return mprino1(x,v)
      else if not atom car u
           then <<curmark := curmark+1;
          prin2ox "("; mprino car u; prin2ox ")";
          omark list(curmark,3); curmark := curmark - 1>>
       else if x := get(car u,pretoprinf)
        then return begin scalar p;
           p := car v>0
                 and not(car u memq
                           '(list procedure prog quote rblock string));
           if p then prin2ox "(";
           apply1(x,cdr u);
           if p then prin2ox ")"
         end
       else if x := get(car u,pretop)
        then return if car x then inprinox(u,car x,v)
% Next line commented out since not all user infix operators are binary.
%                    else if cddr u then rederr "Syntax error"
                     else if null cadr x then inprinox(u,list(100,1),v)
                     else inprinox(u,list(100,cadr x),v)
       else if flagp(car u,'modefn) and eqcar(cadr u,'procedure)
        then return proceox(cadadr u . car u . cdr cddadr u)
       else prinox car u;
      if rlistatp car u then return rlpri cdr u;
      u := cdr u;
      if null u then prin2ox "()"
      else mprargs(u,v)
   end;

symbolic procedure strangeop u;
  % U is a non-atomic operator; try to find a better print form for it.
  % The commented definition doesn't check the complexity of the
  % argument, and so can lead to more computation.
% if caar u='lambda and length cadar u=1 then
%   subst(cadr u,car cadar u,car cddar u);
   nil;

symbolic procedure mprargs(u,v);
   if null cdr u then <<prin2ox " "; mprino1(car u,list(100,100))>>
   else inprinox('!*comma!* . u,list(0,0),v);

symbolic procedure inprinox(u,x,v);
   begin scalar p;
      p := not(car x>cadr v) or not(cadr x>car v);
      if p then prin2ox "("; omark '(m u);
      inprino(car u,x,cdr u);
      if p then prin2ox ")"; omark '(m d)
   end;

symbolic procedure inprino(opr,v,l);
   begin scalar flg,x;
      curmark := curmark+2;
      x := get(opr,pretop);
      if x and car x
        then <<mprino1(car l,list(car v,0)); l := cdr l; flg := t>>;
      while l do
        <<if opr eq '!*comma!* then <<prin2ox ","; omarko curmark>>
           else if opr eq 'setq
            then <<prin2ox " := "; omark list(curmark,1)>>
        else if atom car l or not(opr eq get(caar l,'alt))
        then <<omark list(curmark,1); oprino(opr,flg); flg := t>>;
      mprino1(car l,list(if null cdr l then 0 else car v,
                          if null flg then 0 else cadr v));
         l := cdr l>>;
      curmark := curmark - 2
   end;

symbolic procedure oprino(opr,b);
   (lambda x; if null x
                 then <<if b then prin2ox " "; prinox opr; prin2ox " ">>
               else <<if y then prin2ox " "; prin2ox x;
                      if y then prin2ox " ">>
                     where y = flagp(opr,'spaced))
   get(opr,'prtch);

flag('(cons),'spaced);

flag('(add mult over to),'spaced);  % So that we don't have 1./1 etc.

symbolic procedure prin2ox u;
   <<rplacd(buffp,explodex u);
     while cdr buffp do buffp := cdr buffp>>;

symbolic procedure explodex u;
   % "Explodes" atom U without including escape characters.
   if numberp u then explode u
    else if stringp u then reversip cdr reversip cdr explode u
    else explodex1 explode u;

symbolic procedure explodex1 u;
   if null u then nil
    else if car u eq '!! then cadr u . explodex1 cddr u
    else check!-downcase car u . explodex1 cdr u;

symbolic procedure explodey u;
   begin scalar v;
      v := explode u;
      if idp u then v := for each x in v collect check!-downcase x;
      return v
   end;

symbolic procedure check!-downcase u;
   begin scalar z;
      return if liter u
                and (z := atsoc(u,
                     '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e)
                       (!F . !f) (!G . !g) (!H . !h) (!I . !i) (!J . !j)
                       (!K . !k) (!L . !l) (!M . !m) (!N . !n) (!O . !o)
                       (!P . !p) (!Q . !q) (!R . !r) (!S . !s) (!T . !t)
                       (!U . !u) (!V . !v) (!W . !w) (!X . !x) (!Y . !y)
                       (!Z . !z))))
               then cdr z
              else u
   end;

symbolic procedure prinox u;
   <<if x then u := x;
     rplacd(buffp,explodey u);
     while cdr buffp do buffp := cdr buffp>>
    where x = get(u,'oldnam);

symbolic procedure omark u;
   <<rplacd(buffp,list u); buffp := cdr buffp>>;

symbolic procedure omarko u; omark list(u,0);

symbolic procedure comprox u;
   begin scalar x;
        if car buffp = '(0 0)
          then return <<for each j in u do prin2ox j;
                        omark '(0 0)>>;
        x := car buffp;
        rplaca(buffp,list(curmark+1,3));
        for each j in u do prin2ox j;
        omark x
   end;

symbolic procedure rlistatp u;
   get(u,'stat) member '(endstat rlis);

symbolic procedure rlpri u;
   if null u then nil
    else begin
      prin2ox " ";
      omark '(m u);
      inprino('!*comma!*,list(0,0),u);
      omark '(m d)
   end;

symbolic procedure condox u;
   begin scalar x;
      omark '(m u);
      curmark := curmark+2;
      while u do
        <<prin2ox "if "; mprino caar u; omark list(curmark,1);
          prin2ox " then ";
          if cdr u and eqcar(cadar u,'cond)
                 and not eqcar(car reverse cadar u,'t)
           then <<x := t; prin2ox "(">>;
          mprino cadar u;
          if x then prin2ox ")";
          u := cdr u;
          if u then <<omarko(curmark - 1); prin2ox " else ">>;
          if u and null cdr u and caar u eq 't
            then <<mprino cadar u; u := nil>>>>;
      curmark := curmark - 2;
      omark '(m d)
   end;

put('cond,pretoprinf,'condox);

symbolic procedure ifox u;
  begin
% Common Lisp stype IF.
    scalar p, a, b;
    p := car u;
    u := cdr u;
    if u then << a := car u; u := cdr u >>;
    if u then << b := car u; u := cdr u >>;
    condox list(list(p, a), list(t, b))
  end;

put('if,pretoprinf,'ifox);

symbolic procedure blockox u;
   begin
      omark '(m u);
      curmark := curmark+2;
      prin2ox "begin ";
      if car u then varprx car u;
      u := labchk cdr u;
      omark list(curmark,if eqcar(car u,'!*label) then 1 else 3);
      while u do
        <<mprino car u;
        if not eqcar(car u,'!*label) and cdr u then prin2ox "; ";
        u := cdr u;
        if u
          then omark list(curmark,
                          if eqcar(car u,'!*label) then 1 else 3)>>;
      omark list(curmark - 1,-1);
      prin2ox " end";
      curmark := curmark - 2;
      omark '(m d)
   end;

symbolic procedure retox u;
   begin
      omark '(m u);
      curmark := curmark+2;
      prin2ox "return ";
      omark '(m u);
      mprino car u;
      curmark := curmark - 2;
      omark '(m d);
      omark '(m d)
   end;

put('return,pretoprinf,'retox);

symbolic procedure varprx u;
   begin scalar typ;
       while u do
        <<if cdar u eq typ
            then <<prin2ox ","; omarko(curmark+1); prinox caar u>>
           else <<if typ then <<prin2ox "; "; omark '(m d)>>;
                prinox (typ := cdar u);
                  prin2ox " "; omark '(m u); prinox caar u>>;
           u := cdr u>>;
      prin2ox "; ";
      omark '(m d)
   end;

put('rblock,pretoprinf,'blockox);

symbolic procedure progox u;
%  The reverse in the following seems wrong.
%  blockox((for each j in reverse car u collect j . 'scalar) . cdr u);
   blockox((for each j in car u collect j . 'scalar) . cdr u);

symbolic procedure labchk u;
   begin scalar x;
      for each z in u do if atom z
        then x := list('!*label,z) . x else x := z . x;
       return reversip x
   end;

put('prog,pretoprinf,'progox);

symbolic procedure gox u;
   <<prin2ox "go to "; prinox car u>>;

put('go,pretoprinf,'gox);

symbolic procedure labox u;
   <<prinox car u; prin2ox ": ">>;

put('!*label,pretoprinf,'labox);

symbolic procedure quotoxx u;
   begin
      if stringp u then return prinox u;
      prin2ox "'";
      u := car u;
      if atom u then return prinox u;
      curmark := curmark+1;
      prin2ox "(";
      omark '(m u);
  a:  if atom u then <<prin2ox " . "; prinox u; u := nil>>
       else <<mprino car u; u := cdr u;
              if u then <<prin2ox '! ; omarko curmark>>>>;
      if u then go to a;
      omark '(m d);
      prin2ox ")";
      curmark := curmark - 1
   end;

symbolic procedure quotox u;
   if stringp u then prinox u else <<prin2ox "'"; prinsox car u>>;

symbolic procedure prinsox u;
   if atom u then prinox u
    else <<curmark := curmark+1;
           prin2ox "(";
           omark '(m u);
           while u do <<prinsox car u;
                        u := cdr u;
                        if u then <<omark list(curmark,-1);
                        if atom u
                          then <<prin2ox " . "; prinsox u; u := nil>>
                         else prin2ox " ">>>>;
           omark '(m d);
           prin2ox ")";
           curmark := curmark - 1>>;

put('quote,pretoprinf,'quotox);

symbolic procedure prognox u;
   begin
      curmark := curmark+1;
      prin2ox "<<";
      omark '(m u);
      while u do <<mprino car u; u := cdr u;
                if u then <<prin2ox "; "; omarko curmark>>>>;
      omark '(m d);
      prin2ox ">>";
      curmark := curmark - 1
   end;

put('prog2,pretoprinf,'prognox);

put('progn,pretoprinf,'prognox);

symbolic procedure listox u;
   begin
      curmark := curmark+1;
      prin2ox "{";
      omark '(m u);
      while u do <<mprino car u; u := cdr u;
%               if u then <<prin2ox ", "; omarko curmark>>>>;
                if u then <<prin2ox ","; omarko curmark>>>>;
      omark '(m d);
      prin2ox "}";
      curmark := curmark - 1
   end;

put('list,pretoprinf,'listox);

symbolic procedure repeatox u;
   begin
      curmark := curmark+1;
      omark '(m u);
      prin2ox "repeat ";
      mprino car u;
      prin2ox " until ";
      omark list(curmark,3);
      mprino cadr u;
      omark '(m d);
      curmark := curmark - 1
   end;

put('repeat,pretoprinf,'repeatox);

symbolic procedure whileox u;
   begin
      curmark := curmark+1;
     omark '(m u);
      prin2ox "while ";
      mprino car u;
      prin2ox " do ";
      omark list(curmark,3);
      mprino cadr u;
      omark '(m d);
      curmark := curmark - 1
   end;

put('while,pretoprinf,'whileox);

symbolic procedure procox u;
   begin
      omark '(m u);
      curmark := curmark+1;
      if cadddr cdr u then <<mprino cadddr cdr u; prin2ox " ">>;
      prin2ox "procedure ";
      procox1(car u,cadr u,caddr u)
   end;

symbolic procedure procox1(u,v,w);
   begin
      prinox u;
      if v then mprargs(v,list(0,0));
      prin2ox "; ";
      omark list(curmark,3);
      mprino w;
      curmark := curmark - 1;
      omark '(m d)
   end;

put('proc,pretoprinf,'procox);

symbolic procedure proceox u;
   begin
      omark '(m u);
      curmark := curmark+1;
      if cadr u then <<mprino cadr u; prin2ox " ">>;
      if not(caddr u eq 'expr) then <<mprino caddr u; prin2ox " ">>;
      prin2ox "procedure ";
      proceox1(car u,cadddr u,car cddddr u)
   end;

symbolic procedure proceox1(u,v,w);
   % Prettyprint the procedure's argument list, any active annotation,
   % and its body.
   begin scalar annot;
      prinox u;
      if v
        then <<if not atom car v then v := mapovercar v;
               %allows for typing to be included with proc arguments;
               mprargs(v,list(0,0))>>;
      prin2ox "; ";
      if annot := get(u,'active!-annotation) then
            <<omark list(curmark,3);
              prin2ox "/* ";
              princom car annot;
              prin2ox " */";
              omark '(m d)>>;
      omark list(curmark,3);
      mprino w;
      curmark := curmark - 1;
      omark '(m d)
   end;

put('procedure,pretoprinf,'proceox);

symbolic procedure proceox0(u,v,w,x);
   proceox list(u,'symbolic,v,for each j in w collect j . 'symbolic,x);

symbolic procedure deox u;
   proceox0(car u,'expr,cadr u,caddr u);

put('de,pretoprinf,'deox);

% symbolic procedure dfox u;
%   proceox0(car u,'fexpr,cadr u,caddr u);

%put('df,pretoprinf,'dfox);  % Commented out because of confusion with
                             % differentiation.  We also want to
                             % discourage use of fexpr in REDUCE.

symbolic procedure dsox u;
   proceox0(car u,'smacro,cadr u,caddr u);

put('ds,pretoprinf,'dsox);

symbolic procedure stringox u;
   <<prin2ox '!"; prin2ox car u; prin2ox '!">>;

put('string,pretoprinf,'stringox);

symbolic procedure lambdox u;
   begin
      omark '(m u);
      curmark := curmark+1;
      procox1('lambda,car u,cadr u)
   end;

put('lambda,pretoprinf,'lambdox);

symbolic procedure eachox u;
   <<prin2ox "for each ";
     while cdr u do <<mprino car u; prin2ox " "; u := cdr u>>;
     mprino car u>>;

put('foreach,pretoprinf,'eachox);

symbolic procedure forox u;
   begin
      curmark := curmark+1;
      omark '(m u);
      prin2ox "for ";
      mprino car u;
      prin2ox " := ";
      mprino caadr u;
      if cadr cadr u neq 1
        then <<prin2ox " step "; mprino cadr cadr u; prin2ox " until ">>
       else prin2ox ":";
      mprino caddr cadr u;
      prin2ox " ";
      mprino caddr u;
      prin2ox " ";
      omark list(curmark,3);
      mprino cadddr u;
      omark '(m d);
      curmark := curmark - 1
   end;

put('for,pretoprinf,'forox);

symbolic procedure forallox u;
   begin
      curmark := curmark+1;
      omark '(m u);
      prin2ox "for all ";
      inprino('!*comma!*,list(0,0),car u);
      if cadr u
        then <<omark list(curmark,3);
               prin2ox " such that ";
               mprino cadr u>>;
      prin2ox " ";
      omark list(curmark,3);
      mprino caddr u;
      omark '(m d);
      curmark := curmark - 1
   end;

put('forall,pretoprinf,'forallox);

comment Support for printing algebraic mode code;

put('aeval!*,pretoprinf,'aevalox);

put('aeval,pretoprinf,'aevalox);

put('revalx,pretoprinf,'aevalox);       % FJW.

symbolic procedure aevalox(u);
      mprino aevalox1 car u;

symbolic procedure aevalox1 u;
  % unquote and listify.
  if eqcar(u,'quote) then cadr u else
  if eqcar(u,'list) then
    for each q in u collect aevalox1 q else u;

symbolic procedure minuspox u;
  if eqcar(car u,'difference) then
     mprino('lessp.cdar u) else mprino('lessp.car u.'(0));

put('minusp,pretoprinf,'minuspox);
put('aminusp!:,pretoprinf,'minuspox);
put('evalequal,pretoprinf,function (lambda u;mprino('equal.u)));
put('evalgreaterp,pretoprinf,function (lambda u;mprino('greaterp.u)));
put('evalgeq,pretoprinf,function (lambda u;mprino('geq.u)));
put('evallessp,pretoprinf,function (lambda u;mprino('lessp.u)));
put('evalleq,pretoprinf,function (lambda u;mprino('leq.u)));
put('evalneq,pretoprinf,function (lambda u;mprino('neq.u)));
put('!:dn!:,pretoprinf,function (lambda u;
                mprino(float car u*expt(float 10,cdr u))));
put('!:rd!:,pretoprinf,function (lambda u;
                mprino(if atom u then u else
                        float car u*expt(float 2,cdr u))));
put('plus2,pretoprinf,function(lambda u;mprino('plus.u)));


comment Declarations needed by old parser;

if null get('!*semicol!*,'op)
  then <<put('!*semicol!*,'op,'((-1 0)));
         put('!*comma!*,'op,'((5 6)))>>;

% Code for printing active comments.

symbolic procedure princom u;
   % Print an active comment.
   begin scalar w,x,y,z; integer n;
      x := explode2 u;
      % Process first line.
      while car x eq '!  do x := cdr x;
      while x and car x neq !$eol!$ do <<y := car x . y; x := cdr x>>;
      while y and car y eq '!  do y := cdr y;
      w := reversip!* y;    % Header line.
      % Process remaining lines.
      while x and (x := cdr x) do
       <<y := nil;
         n := 0;
         while car x eq '!  do <<x := cdr x; n := n+1>>;
         while x and car x neq !$eol!$ do <<y := car x . y; x:= cdr x>>;
         while y and car y eq '!  do y := cdr y;
         z := (n . reversip!* y) . z>>;
      % Find line with least blanks.
      y := z;
      if y
        then <<n := caar y; while (y := cdr y) do n := min(n,caar y)>>;
      while z do <<y := addblanks(cdar z,caar z - n) . y; z := cdr z>>;
      % Now merge lines where possible.
      while y do
       <<z := car y;
         if not(car z eq '!  ) and not(car w eq '! )
           then <<z := '!  . z; w := nconc!*(w,z)>>
          else <<x := w . x; w := z>>;
         y := cdr y>>;
      x := w . x;
      % Final processing.
      x := reversip!* x;
      while x do <<addmarks car x; x := cdr x;
                   if x then omark list(curmark,3)>>
   end;

symbolic procedure addblanks(u,n);
   if n=0 then u else '!  . addblanks(u,n - 1);

symbolic procedure addmarks u;
   begin scalar bool,x;
      while u do
         <<if car u eq '!  then (if null bool
                then <<bool := t; x := {'M,'L} . x>>)
               else bool := nil;
           x := car u . x; u := cdr u>>;
      rplacd(buffp,reversip!* x);
      while cdr buffp do buffp := cdr buffp
   end;


comment RPRINT MODULE, Part 2;

fluid '(orig curpos);

symbolic procedure prinos u;
   begin integer curpos;
        scalar !*lower,orig;
      orig := list posn();
      curpos := car orig;
      prinoy(u,0);
      terpri0x()
   end;

symbolic procedure prinoy(u,n);
   begin scalar x;
      if car(x := spaceleft(u,n)) then return prinom(u,n)
       else if null cdr x then return if car orig<10 then prinom(u,n)
       else <<orig := 9 . cdr orig;
                terpri0x();
                spaces20x(curpos := 9+cadar u);
                prinoy(u,n)>>
      else begin
        a: u := prinoy(u,n+1);
           if null cdr u or caar u<=n then return;
           terpri0x();
           spaces20x(curpos := car orig+cadar u);
           go to a end;
      return u
   end;

symbolic procedure spaceleft(u,mark);
   %U is an expanded buffer of characters delimited by non-atom marks
   %of the form: '(M ...) or '(INT INT))
   %MARK is an integer;
   begin integer n; scalar flg,mflg;
      n := rmar - curpos;
      u := cdr u;   %move over the first mark;
      while u and not flg and n>=0 do
        <<if atom car u then n := n - 1
           else if caar u eq 'm then nil
           else if mark>=caar u then <<flg := t; u := nil . u>>
           else mflg := t;
          u := cdr u>>;
      return ((n>=0) . mflg)
   end;

symbolic procedure prinom(u,mark);
   begin integer n; scalar flg,x;
      n := curpos;
      u := cdr u;
      while u and not flg do
        <<if atom car u then <<x := prin20x car u; n := n+1>>
          else if caar u eq 'm
           then if cadar u eq 'u then orig := n . orig
                 else if cadar u eq 'l
                  then (if chars2 cdr u > (rmar - posn())
                          then <<terpri0x(); spaces20x(curmark+5)>>)
                 else orig := cdr orig
           % Check for long thin lists.
           else if mark>=caar u
              and not(x memq '(!,) % '(!, ! )
                       and rmar - n - 6>charspace(u,x,mark))
            then <<flg := t; u := nil . u>>;
          u := cdr u>>;
      curpos := n;
        if mark=0 and cdr u
          then <<terpri0x();
%                terpri0x();
                 orig := list 0; curpos := 0; prinoy(u,mark)>>;
          %must be a top level constant;
      return u
   end;

symbolic procedure chars2 u; chars21(u,0);

symbolic procedure chars21(u,n);
   if eqcar(car u,'m) then n else chars21(cdr u,n+1);

symbolic procedure charspace(u,char,mark);
   % Determines if there is space until the next character CHAR.
   begin integer n;
      n := 0;
      while u do
        <<if car u = char then u := list nil
           else if atom car u then n := n+1
           else if car u='(m u) then <<n := 1000; u := list nil>>
           else if numberp caar u and caar u<mark then u := list nil;
          u := cdr u>>;
      return n
   end;

symbolic procedure spaces20x n;
   %for i := 1:n do prin20x '! ;
   while n>0 do <<prin20x '! ; n := n - 1>>;

symbolic procedure prin2rox u;
   begin integer m,n; scalar x,y;
      m := rmar - 12;
      n := rmar - 1;
      while u do
        if car u eq '!"
          then <<if not stringspace(cdr u,n - !*n)
                   then <<terpri0x(); !*n := 0>>
                  else nil;
                 prin20x '!";
                 u := cdr u;
                 while not(car u eq '!") do
                   <<prin20x car u; u := cdr u; !*n := !*n+1>>;
                 prin20x '!";
                 u := cdr u;
                 !*n := !*n+2;
                 x := y := nil>>
         else if atom car u and not(car u eq '!  and (!*n=0 or null x
              or cdr u and breakp cadr u or breakp x and not(y eq '!!)))
          then <<y := x; prin20x(x := car u); !*n := !*n+1;
         u := cdr u;
         if !*n=n or !*n>m and not breakp car u and nospace(u,n - !*n)
          then <<terpri0x(); x := y := nil>> else nil>>
         else u := cdr u
   end;

symbolic procedure nospace(u,n);
   if n<1 then t
    else if null u then nil
    else if not atom car u then nospace(cdr u,n)
    else if not(car u eq '!!) and (cadr u eq '!  or breakp cadr u)
     then nil
    else nospace(cdr u,n - 1);

symbolic procedure breakp u;
   u member '(!< !> !; !: != !) !+ !- !, !' !");

symbolic procedure stringspace(u,n);
   if n<1 then nil else car u eq '!" or stringspace(cdr u,n - 1);


comment Some interfaces needed;

symbolic procedure prin20x u;
   if rprifn!* then apply1(rprifn!*,u) else prin2 u;

symbolic procedure terpri0x;
   if rterfn!* then lispeval {rterfn!*} else terpri();

endmodule;

end;
