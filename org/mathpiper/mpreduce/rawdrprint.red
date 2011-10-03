module rprint;  % The Standard LISP to REDUCE pretty-printer.

% Author: Anthony C. Hearn.

create!-package('(rprint),'(util));

fluid '(!*lower !*n buffp combuff!* curmark curpos orig pretop rmar
        pretoprinf rmar rprifn!* rterfn!* labs lits blocks curblock
        numblocks);

Comment RPRIFN!* allows output from RPRINT to be handled differently,
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
%      prin2ox ";";
      omarko curmark;
%      prinos buff
      return buff
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
      if x := atsoc(u,combuff!*) then 
        <<for each y in cdr x do comprox y;
          combuff!* := delete(x,combuff!*)>>;
      if numberp u and u<0 and (x := get('difference,pretop)) then 
        return begin scalar p;
                  x := car x;
                  p := not(car x>cadr v) or not(cadr x>car v);
                  if p then prin2ox "(";
                  prinox u;
                  if p then prinox ")"
               end
      else if atom u then return prinox u
      else if not atom car u and (x:=strangeop u) then 
        return mprino1(x,v)
      else if not atom car u then 
        <<curmark := curmark+1;
          prin2ox "("; 
          mprino car u; 
          prin2ox ")";
          omark list(curmark,3); 
          curmark := curmark - 1>>
      else if x := get(car u,pretoprinf) then 
        return begin scalar p;
                  p := car v>0 and not(car u memq 
                           '(rblock procedure prog quote string));
                  if p then prin2ox "(";
                  apply1(x,cdr u);
                  if p then prin2ox ")"
               end
      else if x := get(car u,pretop) then 
        return if car x then inprinox(u,car x,v)
% Next line commented out since not all user infix operators are binary.
%              else if cddr u then rederr "Syntax error"
               else if null cadr x then inprinox(u,list(100,1),v)
               else inprinox(u,list(100,cadr x),v)
      else if flagp(car u,'modefn) and eqcar(cadr u,'procedure) then 
        return proceox(cadadr u . car u . cdr cddadr u)
      else prinlit u
%      if rlistatp car u then return rlpri cdr u;
%      u := cdr u;
%      if null u then prin2ox "()"
%      else mprargs(u,v)
   end;

symbolic procedure strangeop u;
  % U is a non-atomic operator; try to find a better print form for it.
  if caar u='lambda and length cadar u=1 then
    subst(cadr u,car cadar u,car cddar u);

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

symbolic procedure prinlit u;
   begin
      prin2ox "((Symbol)env[";
      prinox find_literal car u;
      prin2ox "]).fn.op";
      if not cdr u then prin2ox "0"
      else if not cddr u then prin2ox "1"
      else if not cdddr u then prin2ox "2"
      else prin2ox "n";
      prin2ox "(";
      u:=cdr u;
      while u do
        <<mprino car u;
          if cdr u then prin2ox ",";
          u:=cdr u>>;
      prin2ox ")"
   end;

symbolic procedure find_literal u;
   begin scalar w,n;
      w:=lits;
      n:=0;
      while not null w and not (car w eq u) do
         <<w:= cdr w; n:=n+1>>;
      if not null w then return n;
      lits:=append(lits,list u);
      return n
   end;



Comment THIS IS THE START OF THE STRUCTURE BLOCKS;


symbolic procedure proceox u;
   begin scalar lits,blocks,curblock,numblocks;
      omark '(m u);
      curmark := curmark+1;
      curblock:=0;
      numblocks:=0;
%      prinox u;
%      omark list(curmark,0);
%      if cadr u then <<mprino cadr u; prin2ox " ">>;
%      if not(caddr u eq 'expr) then <<mprino caddr u; prin2ox " ">>;
      prin2ox "class ";
      mprino car u;
      prin2ox " extends LispFunction";
      omark list(curmark,2);
      prin2ox "{ LispObject ";
      if not car cadddr u then prin2ox "op0()"
      else if not cdr cadddr u then
        <<prin2ox "op1(LispObject ";
          prinox caar cadddr u;
          prin2ox ")">>
      else if not cddr cadddr u then
        <<prin2ox "op2(LispObject ";
          prinox caar cadddr u;
          prin2ox ", LispObject ";
          prinox caadr cadddr u;
          prin2ox ")">>
      else prin2ox "opn(LispObject []args)";
      omark list(curmark,5);
      mprino car cddddr u;
      omark list(curmark,2); 
      prin2ox "}";
      omark list(curmark,0);
      prin2ox "lits are ";
      prinox lits;
      omarko curmark;
      prinox blocks;
      curmark := curmark - 1;
      omark '(m d)     
   end;

put('procedure,pretoprinf,'proceox);


symbolic procedure progox u;
   blockox((for each j in reverse car u collect j . 'scalar) . cdr u);

put('prog,pretoprinf,'progox);


symbolic procedure blockox u;
   begin
      scalar body,labs,nextlab,w;
      curmark := curmark+2;
      omark '(m u);
      numblocks := numblocks+1;
      blocks := list(numblocks,curblock,nil).blocks;
      curblock := numblocks;      
      body := labchk(gensym().cdr u);
      nextlab := 0;
      for each s in body do
         if eqcar(s,'!*label) then
           <<labs := (cadr s.nextlab).labs; 
             nextlab := nextlab+1>>;
      prin2ox "{ int nextlab";
      prinox curblock;
      prin2ox " := 0; ";
      omark list(curmark,2);
      prin2ox "LispObject retVal := Environment.nil;";
      omark list(curmark,2);
      if car u then varprx car u;
      prin2ox "for (;;)";
      omark list(curmark,2);
      prin2ox "{ switch (nextlab";
      prinox curblock;
      prin2ox ") {";
      for each s in body do
         <<if eqcar(s,'!*label) then 
            <<omark list(curmark,6);
              w := assoc(cadr s,labs);
              prin2ox "case ";
              mprino cdr w;
              prin2ox ": ">>
           else
            <<omark list(curmark,8);
              mprino s;
              if not (car s memq '(progn cond prog)) then prin2ox "; ">>
         >>;
      omark list(curmark,4);
      prin2ox "} ";
      omark list(curmark,4);
      prin2ox "break; ";
      omark list(curmark,2);
      prin2ox "}";
      if caddr assoc(curblock,blocks) and (curblock = 1) then 
        <<omark list(curmark,2);
          prin2ox "return retVal;">>;
      omarko curmark;
      prin2ox "}";
      curblock := cadr assoc(curblock,blocks);
      omark '(m d);
      curmark := curmark-2 
   end;

symbolic procedure labchk u;
   begin scalar x;
      for each z in u do 
        if atom z then x := list('!*label,z) . x else x := z . x;
      return reversip x
   end;

symbolic procedure varprx u;
   begin
      prin2ox "LispObject ";
      while u do
        <<prinox caar u;
          prin2ox " := Environment.nil";
          if cdr u then prin2ox ", " 
          else prin2ox "; ";
          u := cdr u>>;
      omark list(curmark,2)
   end; 

put('rblock,pretoprinf,'blockox);


symbolic procedure condox u;
   begin scalar w,x,y,z;
      y:=0;
      omark '(m u);
      curmark := curmark+2;
      while u do
        <<prin2ox "if ("; mprino caar u; omark list(curmark,1);
          prin2ox ") ";
%         if cdr u and eqcar(cadar u,'cond)
%           and not eqcar(car reverse cadar u,'t) then
%           <<x := t; prin2ox "{">>;
          if not atom cadar u and caadar u memq '(go return) then 
            <<prin2ox "{"; x:=t>>;
          mprino cadar u;
          if atom cadar u or not 
            (caadar u memq '(progn cond go return)) then 
            prin2ox "; ";
          if x then prin2ox ";} ";
          u := cdr u;
          if u then 
            <<omarko(curmark - 1); prin2ox "else ";
              if null cdr u and caar u eq 't then
                <<if not atom cadar u and 
                     caadar u memq '(go return) then 
                    <<prin2ox "{"; w:=t>>;
                  mprino cadar u;
                  if atom cadar u or not 
                    (caadar u memq '(progn cond go return)) then 
                    prin2ox "; ";
                  if w then prin2ox ";} ";
                  u := nil>>
               else <<prin2ox "{"; y:=y+1>>>>>>;
      for z:=1:y do prin2ox "}";    
      curmark := curmark - 2;
      omark '(m d)
   end;

put('cond,pretoprinf,'condox);


symbolic procedure gox u;
   begin scalar w;
      w:=assoc(car u,labs);
      prin2ox "nextlab";
      prinox curblock;
      prin2ox " := "; 
      mprino cdr w; 
      prin2ox "; ";
      omark list(curmark,6);
      prin2ox "continue"
   end;

put('go,pretoprinf,'gox);


symbolic procedure retox u;
   begin
      omark '(m u);
      curmark := curmark+2;
      prin2ox "retVal := ";
      omark '(m u);
      mprino car u;
      prin2ox "; ";
      omark list(curmark,0);
      prin2ox "break";
      caddr assoc(curblock,blocks) := 't;
      curmark := curmark - 2;
      omark '(m d);
      omark '(m d)
   end;

put('return,pretoprinf,'retox);
 

symbolic procedure labox u;
   <<prinox car u; prin2ox ": ">>;

put('!*label,pretoprinf,'labox);


symbolic procedure quotox u;
   if stringp u then prinox u 
   else <<prin2ox "env[";
          prinox find_literal car u;
          prin2ox "]">>;

put('quote,pretoprinf,'quotox);


symbolic procedure prognox u;
   begin
      curmark := curmark+1;
      prin2ox "{";
      omark '(m u);
      while u do 
        <<mprino car u;
          if atom car u or caar u neq 'cond then prin2ox ";";
          if cdr u then <<prin2ox " "; omarko curmark>>;
          u:=cdr u>>;
      omark '(m d);
      prin2ox "} ";
      curmark := curmark - 1
   end;

put('prog2,pretoprinf,'prognox);
put('progn,pretoprinf,'prognox);


symbolic procedure setqox u;
   begin
      mprino car u;
      prin2ox " := ";
      if null cddr u then mprino cadr u
      else mprino cdr u
   end;

put('setq,pretoprinf,'setqox);


symbolic procedure listox u;
   begin
      curmark := curmark+1;
      prin2ox "{";
      omark '(m u);
      while u do <<mprino car u; u := cdr u;
                if u then <<prin2ox ", "; omarko curmark>>>>;
      omark '(m d);
      prin2ox "}";
      curmark := curmark - 1
   end;

put('list,pretoprinf,'listox);


symbolic procedure repeatox u;
   begin
      curmark := curmark+1;
      omark '(m u);
      prin2ox "do ";
      mprino car u;
      prin2ox " while (NOT ";
      omark list(curmark,3);
      mprino cadr u;
      prin2ox ") ";
      omark '(m d);
      curmark := curmark - 1
   end;

put('repeat,pretoprinf,'repeatox);


symbolic procedure whileox u;
   begin
      curmark := curmark+1;
      omark '(m u);
      prin2ox "while (";
      mprino car u;
      prin2ox ") ";
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
      prin2ox "for (";
      mprino car u;
      prin2ox ":=";
      mprino caadr u;
      prin2ox "; (";
      mprino car u;
      prin2ox "==";
      mprino caddr cadr u;
      prin2ox "); ";
      mprino car u;
      prin2ox ":=";
      mprino car u;
      prin2ox "+";
      mprino cadr cadr u;
      prin2ox ") ";

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

Comment Support for printing algebraic mode code;

put('aeval!*,pretoprinf,'aevalox);

put('aeval,pretoprinf,'aevalox);

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


Comment Declarations needed by old parser;

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
                then <<bool := t; x := {'m,'l} . x>>)
               else bool := nil;
           x := car u . x; u := cdr u>>;
      rplacd(buffp,reversip!* x);
      while cdr buffp do buffp := cdr buffp
   end;


Comment RPRINT MODULE, Part 2;

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
           else if mark>=caar u
             and not(x='!, and rmar - n - 6>charspace(u,x,mark))
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
   %determines if there is space until the next character CHAR;
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


Comment Some interfaces needed;

symbolic procedure prin20x u;
   if rprifn!* then apply1(rprifn!*,u) else prin2 u;

symbolic procedure terpri0x;
   if rterfn!* then lispeval {rterfn!*} else terpri();


Comment OLD STUFF WHICH I HAVEN'T GOT RID OF YET;

%  symbolic procedure varprx u;
%        mapc(cdr u,function (lambda j;
%                          <<prin2ox car j;
%                          prin2ox " ";
%                          inprino('!*comma!*,list(0,0),cdr j);
%                          prin2ox "; ";
%                          omark list(curmark,6)>>));
%
%Comment a version for the old parser;

%symbolic procedure varprx1 u;
%   begin scalar typ;
%     u := reverse u;
%       while u do
%        <<if cdar u eq typ
%            then <<prin2ox ","; omarko(curmark+1); prinox caar u>>
%           else <<if typ then <<prin2ox "; "; omark '(m d)>>;
%                prinox (typ := cdar u);
%                  prin2ox " "; omark '(m u); prinox caar u>>;
%           u := cdr u>>;
%      prin2ox "; ";
%      omark '(m d)
%   end; 

%symbolic procedure prinsox u;
%   if atom u then prinox u
%    else <<prin2ox "(";
%           omark '(m u);
%           curmark := curmark+1;
%        while u do <<prinsox car u;
%                        u := cdr u;
%                        if u then <<omark list(curmark,-1);
%                        if atom u
%                          then <<prin2ox " . "; prinsox u; u := nil>>
%                         else prin2ox " ">>>>;
%           curmark := curmark - 1;
%           omark '(m d);
%        prin2ox ")">>;

%symbolic procedure proceox1(u,v,w);
%    Prettyprint the procedure's argument list, any active annotation,
%    and its body.
%   begin scalar annot;
%      prinox u;
%      if v
%        then <<if not atom car v then v := mapovercar v;
%               %allows for typing to be included with proc arguments;
%               mprargs(v,list(0,0))>>;
%      prin2ox "; ";
%      if annot := get(u,'active!-annotation) then
%            <<omark list(curmark,3);
%              prin2ox "/* ";
%              princom car annot;
%              prin2ox " */";
%              omark '(m d)>>;
%      omark list(curmark,3);
%      mprino w;
%      curmark := curmark - 3;
%      omark '(m d)
%   end;

%symbolic procedure blockox2 u;
%   begin
%      omark '(m u);
%      curmark := curmark+2;
%      prin2ox "begin ";
%      if car u then varprx car u;
%      u := labchk cdr u;
%      omark list(curmark,if eqcar(car u,'!*label) then 1 else 3);
%      while u do
%        <<mprino car u;
%        if not eqcar(car u,'!*label) and cdr u then prin2ox "; ";
%        u := cdr u;
%        if u
%          then omark list(curmark,
%                          if eqcar(car u,'!*label) then 1 else 3)>>;
%      omark list(curmark - 1,-1);
%      prin2ox " end";
%      curmark := curmark - 2;
%      omark '(m d)
%   end;


endmodule;

end;
