;
; Create RLISP.  Use this via mkrlisp0.lsp or mkrlisp1.lsp
;


; Standard LISP equivalent of BOOT.RED.

(proclaim '(special *blockp *mode))

(proclaim '(special oldchan*))

(proclaim '(special crchar* cursym* fname* nxtsym* ttype* $eol$ !$eof!$))

(setq *blockp nil) (setq *mode nil) (setq oldchan* nil)
(setq crchar* nil) (setq cursym* nil) (setq fname* nil)
(setq nxtsym* nil) (setq ttype* nil)

(defun compress (l)
   (prog (r c)
      (setq c (car l))
      (if (or (eq c '|"|) (digit c)) (return (compress1 l)))
      (while l
         (setq c (car l))
         (setq l (cdr l))
         (when (eq c '|!|) (setq c (car l)) (setq l (cdr l)))
         (setq r (cons c (cons '|\\| r))))
      (return (compress1 (nreverse r)))))

(setq $eol$ (compress1 (list '|\\| (special-char 1))))

(setq $eof$ (special-char 8))

(put '|;| 'switch* '(nil *semicol*))

(put '|(| 'switch* '(nil *lpar*))

(put '|)| 'switch* '(nil *rpar*))

(put '|,| 'switch* '(nil *comma*))

(put '|.| 'switch* '(nil cons))

(put '|:| 'switch* '(((= nil setq)) *colon*))

(put '*comma* 'infix 1)

(put 'setq 'infix 2)

(put 'cons 'infix 3)

(flag '(*comma*) 'nary)

(flag '(*colon* *semicol* end then else) 'delim)

(put 'begin 'stat 'blockstat)

(put 'if 'stat 'ifstat)

(put 'symbolic 'stat 'procstat)

(de begin2 nil
   (prog nil
      (setq cursym* '*semicol*)
a     (cond
         ((eq cursym* 'end) (progn (rds oldchan*) (return nil)))
         (t (prin2 (errorset '(eval (form (xread nil))) t t)) ))
      (go a)))

(de form (u) u)

(de xread (u) (progn (scan) (xread1 u)))

(de xread1 (u)
   (prog (v w x y z z2)
a     (setq z cursym*)
a1    (cond
         ((or (null (atom z)) (numberp z)) (setq y nil))
         ((flagp z 'delim) (go end1))
         ((eq z '*lpar*) (go lparen))
         ((eq z '*rpar*) (go end1))
         ((setq y (get z 'infix)) (go infx))
         ((setq y (get z 'stat)) (go stat)))
a3    (setq w (cons z w))
next  (setq z (scan))
      (go a1)
lparen(setq y nil)
      (cond
         ((eq (scan) '*rpar*)
            (and w (setq w (cons (list (car w)) (cdr w)))) )
         ((eqcar (setq z (xread1 'paren)) '*comma*)
            (setq w (cons (cons (car w) (cdr z)) (cdr w))))
         (t (go a3)))
      (go next)
infx  (setq z2 (mkvar (car w) z))
un1   (setq w (cdr w))
      (cond
         ((null w) (go un2))
         (t (setq z2 (cons (car w) (list z2)))) )
      (go un1)
un2   (setq v (cons z2 v))
preced(cond ((null x) (go pr4)) ((lessp y (car (car x))) (go pr2)))
pr1   (setq x (cons (cons y z) x))
      (go next)
pr2   (setq v
         (cons
            (cond
               ((and (eqcar (car v) (cdar x)) (flagp (cdar x) 'nary))
                  (cons (cdar x) (cons (cadr v) (cdar v))))
               (t (cons (cdar x) (list (cadr v) (car v)))) )
            (cdr (cdr v))))
      (setq x (cdr x))
      (go preced)
stat  (setq w (cons (eval (list y)) w))
      (setq y nil)
      (go a)
end1  (cond
         ((and (and (null v) (null w)) (null x)) (return nil))
         (t (setq y 0)))
      (go infx)
pr4   (cond ((null (equal y 0)) (go pr1)) (t (return (car v)))) ))

(de eqcar (u v) (and (null (atom u)) (eq (car u) v)))

(de mksetq (u v) (list 'setq u v))

(de mkvar (u v) u)

(de rread nil
   (prog (x)
      (setq x (token))
      (return
         (cond
            ((and (equal ttype* 3) (eq x '|(|)) (rrdls))
            (t x)))) )

(de reverse2 (a b)
   (prog nil
  a   (cond ((null a) (return b)))
      (setq b (cons (car a) b))
      (setq a (cdr a))))

(de rrdls nil
   (prog (x r)
a     (setq x (rread))
      (cond
         ((null (equal ttype* 3)) (go b))
         ((eq x '|)|) (return (reversip r)))
         ((null (eq x '|.|)) (go b)))
      (setq x (rread))
      (token)
      (return (reverse2 r x))
b     (setq r (cons x r))
      (go a)))

(de token nil
   (prog (x y)
      (setq x crchar*)
a     (cond
         ((seprp x) (go sepr))
         ((digit x) (go number))
         ((liter x) (go letter))
         ((eq x '|%|) (go coment))
         ((eq x '|!|) (go escape))
         ((eq x '|'|) (go quote))
         ((eq x '|"|) (go string)))
      (setq ttype* 3)
      (cond ((delcp x) (go d)))
      (setq nxtsym* x)
a1    (setq crchar* (readch))
      (go c)
escape(setq y (cons x y))
      (setq x (readch))
letter(setq ttype* 0)
let1  (setq y (cons x y))
      (cond
         ((or (digit (setq x (readch))) (liter x)) (go let1))
         ((eq x '|!|) (go escape)))
      (setq nxtsym* (intern (compress (reverse y))))
b     (setq crchar* x)
c     (return nxtsym*)
number(setq ttype* 2)
num1  (setq y (cons x y))
      (cond ((digit (setq x (readch))) (go num1)))
      (setq nxtsym* (compress (reverse y)))
      (go b)
quote (setq crchar* (readch))
      (setq nxtsym* (list 'quote (rread)))
      (setq ttype* 4)
      (go c)
string(prog (raise)
         (setq raise *raise)
         (setq *raise nil)
   strinx(setq y (cons x y))
         (cond ((null (eq (setq x (readch)) '|"|)) (go strinx)))
         (setq y (cons x y))
         (setq nxtsym* (mkstrng (compress (reverse y))))
         (setq *raise raise))
      (setq ttype* 1)
      (go a1)
coment(cond ((null (eq (readch) $eol$)) (go coment)))
sepr  (setq x (readch))
      (go a)
d     (setq nxtsym* x)
      (setq crchar* '| |)
      (go c)))

(setq crchar* '| |)

(de delcp (u) (or (eq u '|;|) (eq u '|$|)))

(de mkstrng (u) u)

(de scan nil
   (prog (x y)
      (cond ((null (eq cursym* '*semicol*)) (go b)))
a     (setq nxtsym* (token))
b     (cond
         ((or (null (atom nxtsym*)) (numberp nxtsym*)) (go l))
         ((and (setq x (get nxtsym* 'newnam)) (setq nxtsym* x))
            (go b))
         ((eq nxtsym* 'comment) (go comm))
         ((and
             (eq nxtsym* '|'|)
             (setq cursym* (list 'quote (rread))))
            (go l1))
         ((null (setq x (get nxtsym* 'switch*))) (go l))
         ((eq (cadr x) '*semicol*)
            (return (setq cursym* (cadr x)))) )
sw1   (setq nxtsym* (token))
      (cond
         ((or
             (null (car x))
             (null (setq y (assoc nxtsym* (car x)))) )
            (return (setq cursym* (cadr x)))) )
      (setq x (cdr y))
      (go sw1)
comm  (cond ((eq (readch) '|;|) (setq crchar* '| |)) (t (go comm)))
      (go a)
l     (setq cursym*
         (cond
            ((null (eqcar nxtsym* 'string)) nxtsym*)
            (t (cons 'quote (cdr nxtsym*)))) )
l1    (setq nxtsym* (token))
      (return cursym*)))

(de ifstat nil
   (prog (condx condit)
a     (setq condx (xread t))
      (setq condit (nconc condit (list (list condx (xread t)))) )
      (cond
         ((null (eq cursym* 'else)) (go b))
         ((eq (scan) 'if) (go a))
         (t (setq condit
               (nconc condit (list (list t (xread1 t)))) )))
b     (return (cons 'cond condit))))

(de procstat nil
   (prog (x y)
      (cond ((eq cursym* 'symbolic) (scan)))
      (cond
         ((eq cursym* '*semicol*)
            (return (null (setq *mode 'symbolic)))) )
      (setq fname* (scan))
      (cond ((atom (setq x (xread1 nil))) (setq x (list x))))
      (setq y (xread nil))
      (cond ((flagp (car x) 'lose) (return nil)))
      (putd (car x) 'expr (list 'lambda (cdr x) y))
      (setq fname* nil)
      (return (list 'quote (car x)))) )

(de blockstat nil
   (prog (x hold varlis *blockp)
a0    (setq *blockp t)
      (scan)
      (cond
         ((null (or (eq cursym* 'integer) (eq cursym* 'scalar)))
            (go a)))
      (setq x (xread nil))
      (setq varlis
         (nconc
            (cond ((eqcar x '*comma*) (cdr x)) (t (list x)))
            varlis))
      (go a0)
a     (setq hold (nconc hold (list (xread1 nil))))
      (setq x cursym*)
      (scan)
      (cond ((not (eq x 'end)) (go a)))
      (return (mkprog varlis hold))))

(de mkprog (u v) (cons 'prog (cons u v)))

(de gostat nil
   (prog (x) (scan) (setq x (scan)) (scan) (return (list 'go x))))

(put 'go 'stat 'gostat)

(de rlis nil
   (prog (x)
      (setq x cursym*)
      (return (cond ((not (flagp (scan) 'delim))
                     (list x (list 'quote (list (xread1 t)))))
                    (t (list x))))))

(begin2)
%
% This file is "rlisp.red" taken from the REDUCE 3.3 sources, and can be
% used to reconstruct the CSL files compiler.lsp and ccomp.lsp from
% the associated RLISP source files.
%

% module module; % Support for module use.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*mode);

global '(exportslist!* importslist!* module!-name!* old!-mode!*);

!*mode := 'symbolic;   % initial value.

symbolic procedure exports u;
   begin exportslist!* := union(u,exportslist!*); end;

symbolic procedure imports u;
   begin importslist!* := union(u,importslist!*); end;

symbolic procedure module u;
   %Sets up a module definition;
   begin
      if null module!-name!* then old!-mode!* := !*mode;
      module!-name!* := car u . module!-name!*;
      !*mode := 'symbolic
   end;

symbolic procedure endmodule;
   begin
      if null module!-name!*
        then rederr  "ENDMODULE called outside module";
      exportslist!* := nil;
      importslist!* := nil;
      module!-name!* := cdr module!-name!*;
      if module!-name!* then return nil;
      !*mode := old!-mode!*;
      old!-mode!* := nil
   end;

deflist('((exports rlis) (imports rlis) (module rlis)),'stat);

if null get('endmodule, 'stat) then put('endmodule,'stat,'rlis);
% For bootstrap only

flag('(endmodule),'go);

% endmodule;


module newtok;  % Functions for introducing infix tokens to the system.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*redeflg!*);

global '(!*msg preclis!*);

%Several operators in REDUCE are used in an infix form  (e.g.,
%+,- ). The internal alphanumeric names associated with these
%operators are introduced by the function NEWTOK defined below.
%This association, and the precedence of each infix operator, is
%initialized in this section. We also associate printing characters
%with each internal alphanumeric name as well;

preclis!*:= '(or and not member memq equal neq eq geq greaterp leq
              lessp freeof plus difference times quotient expt cons);

deflist ('(
   (not not)
   (plus plus)
   (difference minus)
   (minus minus)
   (times times)
   (quotient recip)
   (recip recip)
 ), 'unary);

flag ('(and or !*comma!* plus times),'nary);

flag ('(cons setq plus times),'right);

deflist ('((minus plus) (recip times)),'alt);

symbolic procedure mkprec;
   begin scalar x,y,z;
        x := 'where . ('!*comma!* . ('setq . preclis!*));
        y := 1;
    a:  if null x then return nil;
        put(car x,'infix,y);
        put(car x,'op,list list(y,y));   %for RPRINT;
        if z := get(car x,'unary) then put(z,'infix,y);
        if and(z,null flagp(z,'nary)) then put(z,'op,list(nil,y));
        x := cdr x;
        y := add1 y;
        go to a
   end;

mkprec();

symbolic procedure newtok u;
   begin scalar !*redeflg!*,x,y;
      if atom u or atom car u or null idp caar u
        then typerr(u,"NEWTOK argument");
      % set up SWITCH* property.
      put(caar u,'switch!*,
          cdr newtok1(car u,cadr u,get(caar u,'switch!*)));
      % set up PRTCH property.
      y := intern compress consescc car u;
      if !*redeflg!* then lprim list(y,"redefined");
      put(cadr u,'prtch,y);
      if x := get(cadr u,'unary) then put(x,'prtch,y)
   end;

symbolic procedure newtok1(charlist,name,propy);
      if null propy then lstchr(charlist,name)
       else if null cdr charlist
        then begin
                if cdr propy and !*msg then !*redeflg!* := t;
                return list(car charlist,car propy,name)
             end
       else car charlist . newtok2(cdr charlist,name,car propy)
                         . cdr propy;

symbolic procedure newtok2(charlist,name,assoclist);
   if null assoclist then list lstchr(charlist,name)
    else if car charlist eq caar assoclist
     then newtok1(charlist,name,cdar assoclist) . cdr assoclist
    else car assoclist . newtok2(charlist,name,cdr assoclist);

symbolic procedure consescc u;
   if null u then nil else '!! . car u . consescc cdr u;

symbolic procedure lstchr(u,v);
   if null cdr u then list(car u,nil,v)
    else list(car u,list lstchr(cdr u,v));

newtok '((!$) !*semicol!*);
newtok '((!;) !*semicol!*);
newtok '((!+) plus);
newtok '((!-) difference);
newtok '((!*) times);
newtok '((!^) expt);
newtok '((!* !*) expt);
newtok '((!/) quotient);
newtok '((!=) equal);
newtok '((!,) !*comma!*);
newtok '((!() !*lpar!*);
newtok '((!)) !*rpar!*);
newtok '((!:) !*colon!*);
newtok '((!: !=) setq);
newtok '((!.) cons);
newtok '((!<) lessp);
newtok '((!< !=) leq);
newtok '((!< !<) !*lsqb!*);
newtok '((!>) greaterp);
newtok '((!> !=) geq);
newtok '((!> !>) !*rsqb!*);

put('expt,'prtch,'!*!*);   % To ensure that FORTRAN output is correct.

flag('(difference minus plus setq),'spaced);

flag('(newtok),'eval);

endmodule;


module support;   % Basic functions needed to support RLISP and REDUCE.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

symbolic procedure aconc(u,v);
   %adds element v to the tail of u. u is destroyed in process;
   nconc(u,list v);

symbolic procedure r!-arrayp u; get(u,'rtype) eq 'array;

symbolic procedure idlistp u;
   % True if u is a list of id's.
   null u or null atom u and idp car u and idlistp cdr u;

symbolic procedure mkprog(u,v); 'prog . (u . v);

symbolic procedure mksetq(u,v); list('setq,u,v);

symbolic procedure pairvars(u,vars,mode);
   % Sets up pairings of parameters and modes.
   begin scalar x;
   a: if null u then return append(reversip!* x,vars)
       else if null idp car u then symerr("Invalid parameter",nil);
      x := (car u . mode) . x;
      u := cdr u;
      go to a
   end;

symbolic procedure prin2t u; progn(prin2 u, terpri(), u);

symbolic procedure smemq(u,v);
   %true if id U is a member of V at any level (excluding
   %quoted expressions);
   if atom v then u eq v
    else if car v eq 'quote then nil
    else smemq(u,car v) or smemq(u,cdr v);

symbolic procedure u neq v; null(u=v);

symbolic procedure setdiff(u,v);
   if null v then u else setdiff(delete(car v,u),cdr v);

% List changing alternates (may also be defined as copying functions)

symbolic procedure aconc!*(u,v); nconc(u,list v);  % append(u,list v);

symbolic procedure nconc!*(u,v); nconc(u,v);       % append(u,v);

symbolic procedure reversip!* u; reversip u;       % reverse u;

symbolic procedure rplaca!*(u,v); rplaca(u,v);     % v . cdr u;

symbolic procedure rplacd!*(u,v); rplacd(u,v);     % car u . v;

% The following functions should be provided in the compiler for
% efficient coding.

%symbolic procedure apply1(u,v); apply(u,list v);

%symbolic procedure apply2(u,v,w); apply(u,list(v,w));

%symbolic procedure apply3(u,v,w,x); apply(u,list(v,w,x));

% The following function is needed by several modules. It is more
% REDUCE-specific than other functions in this module, but since it
% needs to be defined early on, it might as well go here.

symbolic procedure gettype u;
   % Returns a REDUCE-related type for the expression U.
   % It needs to be more table driven than the current definition.
   if numberp u then 'number
    else if null atom u or null u or null idp u then 'form
    else if get(u,'simpfn) then 'operator
    else if get(u,'avalue) then 'variable
    else if getd u then 'procedure
    else if globalp u then 'global
    else if fluidp u then 'fluid
    else if flagp(u,'parm) then 'parameter
    else get(u,'rtype);

endmodule;


module slfns;  % Complete list of Standard LISP functions.

% Author: Anthony C. Hearn.

global '(!*argnochk slfns!*);

slfns!* := '(
        (abs 1)
        (add1 1)
        (append 2)
        (apply 2)
        (assoc 2)
        (atom 1)
        (car 1)
        (cdr 1)
        (caar 1)
        (cadr 1)
        (cdar 1)
        (cddr 1)
        (caaar 1)
        (caadr 1)
        (cadar 1)
        (caddr 1)
        (cdaar 1)
        (cdadr 1)
        (cddar 1)
        (cdddr 1)
        (caaaar 1)
        (caaadr 1)
        (caadar 1)
        (caaddr 1)
        (cadaar 1)
        (cadadr 1)
        (caddar 1)
        (cadddr 1)
        (cdaaar 1)
        (cdaadr 1)
        (cdadar 1)
        (cdaddr 1)
        (cddaar 1)
        (cddadr 1)
        (cdddar 1)
        (cddddr 1)
        (close 1)
        (codep 1)
        (compress 1)
        (cons 2)
        (constantp 1)
        (de 3)
        (deflist 2)
        (delete 2)
%       (DF 3)                     conflicts with algebraic operator DF
        (difference 2)
        (digit 1)
        (divide 2)
        (dm 3)
        (dn 3)
        (ds 3)
        (eject 0)
        (eq 2)
        (eqn 2)
        (equal 2)
        (error 2)
        (errorset 3)
        (eval 1)
        (evlis 1)
        (expand 2)
        (explode 1)
        (expt 2)
        (fix 1)
        (fixp 1)
        (flag 2)
        (flagp 2)
        (float 1)
        (floatp 1)
        (fluid 1)
        (fluidp 1)
        (function 1)
        (gensym 0)
        (get 2)
        (getd 1)
        (getv 2)
        (global 1)
        (globalp 1)
        (go 1)
        (greaterp 2)
        (idp 1)
        (intern 1)
        (length 1)
        (lessp 2)
        (linelength 1)
        (liter 1)
        (lposn 0)
        (map 2)
        (mapc 2)
        (mapcan 2)
        (mapcar 2)
        (mapcon 2)
        (maplist 2)
        (max2 2)
        (member 2)
        (memq 2)
        (minus 1)
        (minusp 1)
        (min2 2)
        (mkvect 1)
        (nconc 2)
        (not 1)
        (null 1)
        (numberp 1)
        (onep 1)
        (open 2)
        (pagelength 1)
        (pair 2)
        (pairp 1)
        (plus2 2)
        (posn 0)
        (print 1)
        (prin1 1)
        (prin2 1)
        (prog2 2)
        (put 3)
        (putd 3)
        (putv 3)
        (quote 1)
        (quotient 2)
        (rds 1)
        (read 0)
        (readch 0)
        (remainder 2)
        (remd 1)
        (remflag 2)
        (remob 1)
        (remprop 2)
        (return 1)
        (reverse 1)
        (rplaca 2)
        (rplacd 2)
        (sassoc 3)
        (set 2)
        (setq 2)
        (stringp 1)
        (sublis 2)
        (subst 3)
        (sub1 1)
        (terpri 0)
        (times2 2)
        (unfluid 1)
        (upbv 1)
        (vectorp 1)
        (wrs 1)
        (zerop 1)
        );

if !*argnochk then deflist(slfns!*,'number!-of!-args);

endmodule;


module superv; % REDUCE supervisory functions.

% Author: Anthony C. Hearn.

% Modified by: Jed B. Marti.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*backtrace
        !*defn
        !*errcont
        !*int
        !*mode
        !*slin
        !*time
        dfprint!*
        lreadfn!*
        semic!*
        tslin!*);

global '(!$eof!$
         !*byeflag!*
         !*demo
         !*echo
         !*extraecho
         !*lessspace
         !*micro!-version
         !*nosave!*
         !*output
         !*pret
         !*rlisp2
         !*strind
         !*struct
         cloc!*
         cmsg!*
         crbuf!*
         crbuflis!*
         crbuf1!*
         cursym!*
         eof!*
         erfg!*
         ifl!*
         ipl!*
         initl!*
         inputbuflis!*
         key!*
         ofl!*
         opl!*
         ogctime!*
         otime!*
         program!*
         programl!*
         resultbuflis!*
         st!*
         statcounter
         symchar!*
         tok!*
         ttype!*
         ws);

!*output := t;
eof!* := 0;
initl!* := '(fname!* outl!*);
statcounter := 0;

% The true REDUCE supervisory function is BEGIN, again defined in the
% system dependent part of this program.  However, most of the work is
% done by BEGIN1, which is called by BEGIN for every file encountered
% on input;

symbolic procedure errorp u;
   %returns true if U is an ERRORSET error format;
   atom u or cdr u;

symbolic procedure flagp!*!*(u,v); idp u and flagp(u,v);

symbolic procedure setcloc!*;
   % Used to set for file input a global variable CLOC!* to dotted pair
   % of file name and dotted pair of line and page being read.
   % Currently a place holder for system specific function, since not
   % supported in Standard LISP.  CLOC!* is used in the INTER and RCREF
   % modules.
   cloc!* := if null ifl!* then nil else car ifl!* . nil;

symbolic procedure command;
   begin scalar x;
        if !*demo and (x := ifl!*)
          then progn(terpri(),rds nil,readch(),rds cadr x);
        if null !*slin
         then if !*rlisp2
                then progn(s!&(),
                           key!* := tok!*,
                           m!-metarlisp(),
                           (if st!* then x := car st!* else x := nil),
                           st!* := nil)
               else progn(scan(), setcloc!*(), key!* := cursym!*,
                          x := xread1 nil)
         else progn(key!* := (semic!* := '!;),
                    setcloc!*(),
                    x := (if lreadfn!* then apply(lreadfn!*,nil)
                          else read()),
                    if key!* eq '!;
                      then key!* := if atom x then x else car x);
        if !*struct then x := structchk x;
        if !*pret then progn(terpri(),rprint x);
        if null !*slin then x := form x;
        return x
   end;

symbolic procedure begin1;
   begin scalar mode,parserr,result,x;
        if !*rlisp2 then prolog 'm!-metarlisp;
        otime!* := time();
        % the next line is that way for bootstrapping purposes.
        if getd 'gctime then ogctime!* := gctime() else ogctime!* := 0;
    a0: cursym!* := '!*semicol!*;
    a:  if null terminalp() or !*nosave!* then go to b
         else if statcounter>0 then add2buflis();
        statcounter := statcounter + 1;
        crbuf1!* := nil;   % For input string editor.
        !*strind := 0;     % Used by some versions of input editor.
        setpchar compress1('!| . append(explodec statcounter,
                       if null symchar!* or !*mode eq 'algebraic
                                 then '(!: !  !|) else '(!* !  !|)));
    b:  parserr := nil;
        !*nosave!* := nil;
        if !*time then eval '(showtime);   %Since a STAT;
        if !*output and null ofl!* and terminalp() and null !*defn
           and null !*lessspace
          then terpri();
        if tslin!*
          then progn(!*slin := car tslin!*,
                     lreadfn!* := cdr tslin!*,
                     tslin!* := nil);
        mapcar(function sinitl, initl!*);
        if !*int then erfg!* := nil;    %to make editing work properly;
        if null !*rlisp2 and cursym!* eq 'end
          then progn(comm1 'end, return nil);
        program!* := errorset('(command),t,!*backtrace);
        if !*rlisp2
          then if tok!* eq '!*semic!* then semic!* := '!;
                else semic!* := '!$;
        condterpri();
        if errorp program!* then go to err1;
        program!* := car program!*;
        if eofcheck() then go to c else eof!* := 0;
        if !*rlisp2 then if program!* = '(end) then return nil else nil
          else if cursym!* eq 'end
           then if !*micro!-version and terminalp() then go to a0
                 else progn(comm1 'end, return nil)
         else if eqcar(program!*,'retry) then program!* := programl!*;
        %The following section decides what the target mode should be.
        %That mode is also assumed to be the printing mode;
        if flagp!*!*(key!*,'modefn) then mode := key!*
         else if null atom program!* % and null !*micro!-version
          and null(car program!* eq 'quote)
           and (null(idp car program!*
                   and (flagp(car program!*,'nochange)
                         or flagp(car program!*,'intfn)
                         or car program!* eq 'list))
             or car program!* memq '(setq setel setf)
                     and eqcar(caddr program!*,'quote))
          then mode := 'symbolic
         else if key!* eq 'input
            and (x := rassoc!*(program!*,inputbuflis!*))
          then mode := cddr x
         else mode := !*mode;
        program!* := convertmode1(program!*,nil,'symbolic,mode);
        add2inputbuf(program!*,!*mode);
           % This used to be MODE, but then ED n wouldn't work.
        if null !*rlisp2 and null atom program!*
            and car program!* memq '(bye quit)
          then if getd 'bye then progn(eval program!*, go to b)
                else progn(!*byeflag!* := t, return nil)
         else if null !*rlisp2 and eqcar(program!*,'ed)
          then progn((if getd 'cedit and terminalp()
                        then cedit cdr program!*
                       else lprim "ED not supported"),
                     go to b)
         else if !*defn
          then if erfg!* then go to a
                else if null flagp!*!*(key!*,'ignore)
                  and null eqcar(program!*,'quote)
                 then go to d;
    b1: if !*output and ifl!* and !*echo and null !*lessspace
          then terpri();
        result := errorset((if mode eq 'symbolic then program!*
                            else list('assgneval,mkquote program!*)),
                           t,!*backtrace);
        if errorp result or erfg!*
          then progn(programl!* := program!*,go to err2)
         else if !*defn then go to a;
        if null(mode eq 'symbolic)
         then progn(program!* := cdar result,
                    result := list caar result);
        add2resultbuf(car result,mode);
        if null !*output then go to a
         else if (null !*rlisp2 and semic!* eq '!;)
           or (!*rlisp2 and tok!* eq '!*semic!*)
          then if mode eq 'symbolic
                then if null car result and null(!*mode eq 'symbolic)
                       then nil
                 else begin
                    terpri();
                    result := errorset(list('print,mkquote car result),
                                       t,!*backtrace)
                      end
         else if car result
          then result := errorset(list('varpri,mkquote car result,
                                     mkquote program!*,
                                     mkquote 'only),
                        t,!*backtrace);
        if errorp result then go to err3 else go to a;
    c:  if crbuf1!* then
          progn(lprim "Closing object improperly removed. Redo edit.",
                  crbuf1!* := nil, go to a)
          else if eof!*>4
           then progn(lprim "End-of-file read", return eval '(bye))
         else if terminalp() then progn(crbuf!* := nil, go to b)
         else return nil;
    d:  if program!* then dfprint program!*;
        if null flagp!*!*(key!*,'eval) then go to a else go to b1;
    err1:
        if eofcheck() or eof!*>0 then go to c
         else if program!*="BEGIN invalid" then go to a;
        parserr := t;
    err2:
        resetparser();  %in case parser needs to be modified;
    err3:
        erfg!* := t;
        if null !*int and null !*errcont
          then progn(!*defn := t,
                     !*echo := t,
                     (if null cmsg!*
                        then lprie "Continuing with parsing only ..."),
                     cmsg!* := t)
         else if null !*errcont
          then progn(result := pause1 parserr,
                     (if result then return null eval result),
                     erfg!* := nil)
         else erfg!* := nil;
        go to a
   end;

flag ('(deflist flag fluid global remflag remprop unfluid),'eval);

symbolic procedure assgneval u;
   % Evaluate (possible) assignment statements and return results in a
   % form that allows required printing of such assignments.
   begin scalar x,y;
   a: if atom u then go to b
       else if car u eq 'setq then x := ('setq . cadr u) . x
       else if car u eq 'setel
        then x := ('setel . mkquote eval cadr u) . x
       else if car u eq 'setk
        then x := ('setk . mkquote if atom (y := eval cadr u) then y
                                    else car y . revlis cdr y) . x
       else go to b;
      u := caddr u;
      go to a;
   b: u := mkquote eval u;
   c: if null x then return(eval u . u);
      u := list(caar x,cdar x,u);
      x := cdr x;
      go to c
   end;

symbolic procedure rassoc!*(u,v);
   % Finds term in which U is the first term in the right part of a term
   % in the association list V, or NIL if term is not found;
   if null v then nil
    else if u = cadar v then car v
    else rassoc!*(u,cdr v);

symbolic procedure close!-input!-files;
   % Close all input files currently open;
   begin
      if ifl!* then progn(rds nil,ifl!* := nil);
  aa: if null ipl!* then return nil;
      close cdar ipl!*;
      ipl!* := cdr ipl!*;
      go to aa
   end;

symbolic procedure close!-output!-files;
   % Close all output files currently open;
   begin
      if ofl!* then progn(wrs nil,ofl!* := nil);
  aa: if null opl!* then return nil;
      close cdar opl!*;
      opl!* := cdr opl!*;
      go to aa
   end;

symbolic procedure add2buflis;
   begin
      if null crbuf!* then return nil;
      crbuf!* := reversip crbuf!*;   %put in right order;
   a: if crbuf!* and seprp car crbuf!*
       then progn(crbuf!* := cdr crbuf!*, go to a);
      crbuflis!* := (statcounter . crbuf!*) . crbuflis!*;
      crbuf!* := nil
   end;

symbolic procedure add2inputbuf(u,mode);
   begin
      if null terminalp() or !*nosave!* then return nil;
      inputbuflis!* := (statcounter . u . mode) . inputbuflis!*
   end;

symbolic procedure add2resultbuf(u,mode);
   begin
      if mode eq 'symbolic or null u or !*nosave!* then return nil;
      ws := u;
      if terminalp()
        then resultbuflis!* := (statcounter . u) . resultbuflis!*
   end;

symbolic procedure condterpri;
   !*output and !*echo and !*extraecho and (null !*int or ifl!*)
        and null !*defn and terpri();

symbolic procedure eofcheck;
   % true if an end-of-file has been read in current input sequence;
   program!* eq !$eof!$ and ttype!*=3 and (eof!* := eof!*+1);

symbolic procedure resetparser;
   %resets the parser after an error;
   if null !*slin then comm1 t;

symbolic procedure terminalp;
   %true if input is coming from an interactive terminal;
   !*int and null ifl!*;

symbolic procedure dfprint u;
   %Looks for special action on a form, otherwise prettyprints it;
   if dfprint!* then apply(dfprint!*,list u)
    else if cmsg!* then nil
    else if null eqcar(u,'progn) then prettyprint u
    else begin
            a:  u := cdr u;
                if null u then return nil;
                dfprint car u;
                go to a
         end;

symbolic procedure showtime;
   begin scalar x,y;
      x := otime!*;
      otime!* := time();
      x := otime!*-x;
      y := ogctime!*;
      ogctime!* := gctime();
      y := ogctime!* - y;
      x := x - y;
      terpri();
      prin2 "Time: "; prin2 x; prin2 " ms";
      if y = 0 then return terpri();
      prin2 "  plus GC time: "; prin2 y; prin2 " ms"
   end;

symbolic procedure sinitl u;
   set(u,get(u,'initl));

endmodule;

module tok; % Identifier and reserved character reading.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(semic!*);

global '(!$eof!$
         !$eol!$
         !*quotenewnam
         !*raise
         !*lower
         crbuf!*
         crbuf1!*
         crchar!*
         curline!*
         cursym!*
         eof!*
         ifl!*
         nxtsym!*
         outl!*
         ttype!*);

!*quotenewnam := t;

crchar!* := '! ;

curline!* := 1;

% The function TOKEN defined below is used for reading identifiers
% and reserved characters (such as parentheses and infix operators).
% It is called by the function SCAN, which translates reserved
% characters into their internal name, and sets up the output of the
% input line.  The following definitions of TOKEN and SCAN are quite
% general, but also inefficient.  The reading process can often be
% speeded up considerably if these functions (especially token) are
% written in terms of the explicit LISP used.

symbolic procedure prin2x u;
  outl!* := u . outl!*;

symbolic procedure mkstrng u;
   %converts the uninterned id U into a string;
   %if strings are not constants, this should be replaced by
   %list('string,u);
   u;

symbolic procedure readch1;
   begin scalar x;
      if null terminalp()
        then progn(x := readch(),
                   x eq !$eol!$ and (curline!* := curline!*+1),
                   return x)
       else if crbuf1!*
        then begin x := car crbuf1!*; crbuf1!* := cdr crbuf1!* end
       else x := readch();
      crbuf!* := x . crbuf!*;
      return x
   end;

symbolic procedure token1;
   begin scalar x,y,z;
        x := crchar!*;
    a:  if seprp x then progn(x := readch1(), go to a)
         else if digit x then go to number
         else if liter x or x eq '!_ then go to letter
         else if x eq '!% then go to coment
         else if x eq '!! then go to escape
         else if x eq '!'
          then progn(crchar!* := readch1(),
                     nxtsym!* := mkquote rread(),
                     ttype!* := 4,
                     return nxtsym!*)
         else if x eq '!" then go to string;
        ttype!* := 3;
        if x eq !$eof!$ then prog2(crchar!* := '! ,filenderr());
        nxtsym!* := x;
    a1: if delcp x then crchar!*:= '!  else crchar!*:= readch1();
        go to c;
    escape:
        begin scalar raise, !*lower;
           raise := !*raise;
           !*raise := !*lower := nil;
           y := x . y;
           x := readch1();
           !*raise := raise
        end;
    letter:
        ttype!* := 0;
    let1:
        y := x . y;
        if digit (x := readch1()) or liter x or x eq '!_ then go to let1
         else if x eq '!! then go to escape;
        nxtsym!* := intern compress reversip!* y;
    b:  crchar!* := x;
    c:  return nxtsym!*;
    number:
        ttype!* := 2;
    num1:
        y := x . y;
        z := x;
        if digit (x := readch1())
           or x eq '!.
           or x eq 'e
           or z eq 'e
          then go to num1;
        nxtsym!* := compress reversip!* y;
        go to b;
    string:
        begin scalar raise, !*lower;
           raise := !*raise;
           !*raise := !*lower := nil;
       strinx:
           y := x . y;
           if null((x := readch1()) eq '!") then go to strinx;
           y := x . y;
           nxtsym!* := mkstrng compress reversip!* y;
           !*raise := raise
         end;
        ttype!* := 1;
        go to a1;
    coment:
        if null(readch1() eq !$eol!$) then go to coment;
        x := readch1();
        go to a
   end;

symbolic procedure token;
   %This provides a hook for a faster TOKEN;
   token1();

symbolic procedure filenderr;
   begin
      eof!* := eof!*+1;
      if terminalp() then error1()
       else error(99,if ifl!*
                       then list("End-of-file read in file",car ifl!*)
                      else "End-of-file read")
   end;

symbolic procedure ptoken;
   begin scalar x;
        x := token();
        if x eq '!) and eqcar(outl!*,'! ) then outl!*:= cdr outl!*;
           %an explicit reference to OUTL!* used here;
        prin2x x;
        if null ((x eq '!() or (x eq '!))) then prin2x '! ;
        return x
   end;

symbolic procedure rread1;
   % Modified to use QUOTENEWNAM's for ids.
   begin scalar x,y;
        x := ptoken();
        if null (ttype!*=3)
          then return if null idp x
                         or null !*quotenewnam
                         or null(y := get(x,'quotenewnam))
                        then x
                       else y
         else if x eq '!( then return rrdls()
         else if null (x eq '!+ or x eq '!-) then return x;
        y := ptoken();
        if null numberp y
          then progn(nxtsym!* := " ",
                     symerr("Syntax error: improper number",nil))
         else if x eq '!- then y := apply('minus,list y);
           %we need this construct for bootstrapping purposes;
        return y
   end;

symbolic procedure rrdls;
   begin scalar x,y,z;
    a:  x := rread1();
        if null (ttype!*=3) then go to b
         else if x eq '!) then return z
         else if null (x eq '!.) then go to b;
        x := rread1();
        y := ptoken();
        if null (ttype!*=3) or null (y eq '!))
          then progn(nxtsym!* := " ",symerr("Invalid S-expression",nil))
         else return nconc(z,x);
    b: z := nconc(z,list x);
       go to a
   end;

symbolic procedure rread;
   progn(prin2x " '",rread1());

%-- symbolic procedure scan;
%--    begin scalar x,y;
%--         if null (cursym!* eq '!*semicol!*) then go to b;
%--     a:  nxtsym!* := token();
%--     b:  if null atom nxtsym!* then go to q1
%--          else if nxtsym!* eq 'else or cursym!* eq '!*semicol!*
%--          then outl!* := nil;
%--         prin2x nxtsym!*;
%--     c:  if null idp nxtsym!* then go to l
%--          else if (x:=get(nxtsym!*,'newnam)) and
%--                         (null (x=nxtsym!*)) then go to new
%--          else if nxtsym!* eq 'comment OR NXTSYM!* EQ '!% AND TTYPE!*=3
%--           THEN GO TO COMM
%--          ELSE IF NULL(TTYPE!* = 3) THEN GO TO L
%--          ELSE IF NXTSYM!* EQ !$eof!$ then return filenderr()
%--          else if nxtsym!* eq '!' then go to quote
%--          else if null (x:= get(nxtsym!*,'switch!*)) then go to l
%--          else if eqcar(cdr x,'!*semicol!*) then go to delim;
%--    sw1: nxtsym!* := token();
%--         if null(ttype!* = 3) then go to sw2
%--          else if nxtsym!* eq !$eof!$ then return filenderr()
%--          else if car x then go to sw3;
%--    sw2: cursym!*:=cadr x;
%--         if cursym!* eq '!*rpar!* then go to l2
%--          else return cursym!*;
%--    sw3: if null (y:= atsoc(nxtsym!*,car x)) then go to sw2;
%--         prin2x nxtsym!*;
%--         x := cdr y;
%--         go to sw1;
%--   comm: if delcp crchar!* then go to com1;
%--         crchar!* := readch();
%--         go to comm;
%--   com1: crchar!* := '! ;
%--         condterpri();
%--         go to a;
%--   delim:
%--         semic!*:=nxtsym!*;
%--         return (cursym!*:='!*semicol!*);
%--   new:  nxtsym!* := x;
%--         if stringp x then go to l
%--         else if atom x then go to c
%--         else go to l;
%--   quote:
%--         nxtsym!* := mkquote rread1();
%--         go to l;
%--   q1:   if null (car nxtsym!* eq 'string) then go to l;
%--         prin2x " ";
%--         prin2x cadr(nxtsym!* := mkquote cadr nxtsym!*);
%--   l:    cursym!*:=nxtsym!*;
%--   l1:   nxtsym!* := token();
%--         if nxtsym!* eq !$eof!$ and ttype!* = 3 then return filenderr();
%--   l2:   if numberp nxtsym!*
%--            or (atom nxtsym!* and null get(nxtsym!*,'switch!*))
%--           then prin2x " ";
%--         return cursym!*
%--    end;

global '(!*eoldelimp comment!*);

symbolic procedure scan;
   begin scalar bool,x,y;
        if null (cursym!* eq '!*semicol!*) then go to b;
    a:  nxtsym!* := token();
    b:  if null atom nxtsym!* then go to q1
         else if nxtsym!* eq 'else or cursym!* eq '!*semicol!*
         then outl!* := nil;
        prin2x nxtsym!*;
    c:  if null idp nxtsym!* then go to l
         else if (x:=get(nxtsym!*,'newnam)) and
                        (null (x=nxtsym!*)) then go to new
         else if nxtsym!* eq 'Comment then go to comm
         else if nxtsym!* eq '!#if then go to conditional
         else if nxtsym!* eq '!#else then progn(nxtsym!* := x := nil,
                                                go to skipping)
         else if nxtsym!* eq '!#endif then go to a
         else if nxtsym!* eq '!% and ttype!*=3
%         then progn(prin2t "****** Tell Hearn you got to SCAN comment",
%                    go to comm)
          then go to comm
         else if null(ttype!* = 3) then go to l
         else if nxtsym!* eq !$eof!$ then return filenderr()
         else if nxtsym!* eq '!' then rederr "Invalid QUOTE"
         else if !*eoldelimp and nxtsym!* eq !$eol!$ then go to delim
         else if null (x:= get(nxtsym!*,'switch!*)) then go to l
         else if eqcar(cdr x,'!*semicol!*) then go to delim;
        bool := seprp crchar!*;
   sw1: nxtsym!* := token();
        if null(ttype!* = 3) then go to sw2
         else if nxtsym!* eq !$eof!$ then return filenderr()
         else if car x then go to sw3;
   sw2: cursym!*:=cadr x;
        bool := nil;
        if cursym!* eq '!*rpar!* then go to l2 else return cursym!*;
   sw3: if bool or null (y:= atsoc(nxtsym!*,car x)) then go to sw2;
        prin2x nxtsym!*;
        x := cdr y;
        if null car x and cadr x eq '!*Comment!*
          then progn(comment!* := read!-comment(),go to a);
        go to sw1;
  conditional:
% The conditional expression used here must be written in Lisp form
        x := errorset(rread(), !*backtrace, nil);
% errors in evaluation count as NIL
        if null errorp x and car x then go to a;
        x := nil;
  skipping:
% I support nesting of conditional inclusion.
        if nxtsym!* eq '!#endif then
           if null x then go to a else x := cdr x
        else if nxtsym!* eq '!#if then x := nil . x
        else if (nxtsym!* eq '!#else) and null x then go to a;
        nxtsym!* := token();
        go to skipping;
  comm: if delcp crchar!* and null(crchar!* eq !$eol!$)
          then progn(crchar!* := '! , condterpri(), go to a);
        crchar!* := readch();
        go to comm;
  delim:
        semic!*:=nxtsym!*;
        return (cursym!*:='!*semicol!*);
  new:  nxtsym!* := x;
        if stringp x then go to l
        else if atom x then go to c
        else go to l;
  q1:   if null (car nxtsym!* eq 'string) then go to l;
        prin2x " ";
        prin2x cadr(nxtsym!* := mkquote cadr nxtsym!*);
  l:    cursym!*:=nxtsym!*;
        nxtsym!* := token();
        if nxtsym!* eq !$eof!$ and ttype!* = 3 then return filenderr();
  l2:   if numberp nxtsym!*
           or (atom nxtsym!* and null get(nxtsym!*,'switch!*))
          then prin2x " ";
        return cursym!*
   end;


endmodule;


module xread; % Routines for parsing REDUCE input.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*blockp);

global '(cursym!* nxtsym!*);

% The conversion of a REDUCE expression to LISP prefix form is carried
% out by the function XREAD.  This function initiates the scanning
% process, and then calls the auxiliary function XREAD1 to perform the
% actual parsing.  Both XREAD and XREAD1 are used by many functions
% whenever an expression must be read;

flag ('(end !*colon!* !*semicol!*),'delim);

symbolic procedure chknewnam u;
   % Check to see if U has a newnam, and return it else return U.
   begin scalar x;
      return if null(x := get(u,'newnam)) or x eq u then u
              else if idp x then chknewnam x
              else x
   end;

symbolic procedure mkvar(u,v); u;

symbolic procedure remcomma u;
   if eqcar(u,'!*comma!*) then cdr u else list u;

symbolic procedure xread1 u;
   begin scalar v,w,x,y,z,z1,z2;
        % v: expression being built
        % w: prefix operator stack
        % x: infix operator stack
        % y: infix value or stat property
        % z: current symbol
        % z1: next symbol
        % z2: temporary storage;
  a:    z := cursym!*;
  a1:   if null idp z then nil
         else if z eq '!*lpar!* then go to lparen
         else if z eq '!*rpar!* then go to rparen
         else if y := get(z,'infix) then go to infx
         % The next line now commented out was intended to allow a STAT
         % to be used as a label. However, it prevents the definition of
         % a diphthong whose first character is a colon.
%        else if nxtsym!* eq '!: then nil
         else if flagp(z,'delim) then go to delimit
         else if y := get(z,'stat) then go to stat;
  a2:   y := nil;
  a3:   w := z . w;
        if numberp z
           and idp (z1 := chknewnam nxtsym!*)
           and null flagp(z1,'delim)
           and null(get(z1,'switch!*) and null(z1 eq '!())
           and null get(z1,'infix)
         then progn(cursym!* := 'times, go to a);
           % allow for implicit * after a number.
  next: z := scan();
        go to a1;
  lparen:
        y := nil;
        if scan() eq '!*rpar!* then go to lp1    % no args
         else if flagpcar(w,'struct) then z := xread1 car w
         else z := xread1 'paren;
        if flagp(u,'struct) then progn(z := remcomma z, go to a3)
         else if null eqcar(z,'!*comma!*) then go to a3
         else if null w
           then (if u eq 'lambda then go to a3
                 else symerr("Improper delimiter",nil))
         else w := (car w . cdr z) . cdr w;
        go to next;
  lp1:  if w then w := list car w . cdr w;  %function of no args;
        go to next;
  rparen:
        if null u or u eq 'group or u eq 'proc
          then symerr("Too many right parentheses",nil)
         else go to end1;
  infx: if z eq '!*comma!* or null atom (z1 := scan())
                or numberp z1 then go to in1
         else if z1 eq '!*rpar!*%infix operator used as variable;
                or z1 eq '!*comma!*
                or flagp(z1,'delim)
          then go to in2
         else if z1 eq '!*lpar!*%infix operator in prefix position;
                    and null atom(z1 := xread 'paren)
                    and car z1 eq '!*comma!*
                    and (z := z . cdr z1)
          then go to a1;
  in1:  if w then go to unwind
         else if null(z := get(z,'unary))
          then symerr("Redundant operator",nil);
        v := '!*!*un!*!* . v;
        go to pr1;
  in2:  y := nil;
        w := z . w;
  in3:  z := z1;
        go to a1;
  unwind:
        z2 := mkvar(car w,z);
  un1:  w:= cdr w;
        if null w then go to un2
         else if numberp car w then symerr("Missing operator",nil);
        z2 := list(car w,z2);
        go to un1;
  un2:  v:= z2 . v;
  preced:
        if null x then if y=0 then go to end2 else nil
         else if y<caar x
           or (y=caar x
               and ((z eq cdar x and null flagp(z,'nary)
                                 and null flagp(z,'right))
                             or get(cdar x,'alt)))
          then go to pr2;
  pr1:  x:= (y . z) . x;
        if null(z eq '!*comma!*) then go to in3
         else if cdr x or null u or u memq '(lambda paren)
            or flagp(u,'struct)
          then go to next
         else go to end2;
  pr2:  %if cdar x eq 'setq then go to assign else;
        if cadr v eq '!*!*un!*!*
          then (if car v eq '!*!*un!*!* then go to pr1
                else z2 := list(cdar x,car v))
         else z2 := cdar x .
                     if eqcar(car v,cdar x) and flagp(cdar x,'nary)
                       then (cadr v . cdar v)
                      else list(cadr v,car v);
        x:= cdr x;
        v := z2 . cddr v;
        go to preced;
  stat: if null(flagp(z,'go)
           or null(u eq 'proc) and (flagp(y,'endstat)
                or (null delcp nxtsym!* and null (nxtsym!* eq '!,))))
          then go to a2;
        w := apply(y,nil) . w;
        y := nil;
        go to a;
  delimit:
        if z eq '!*colon!* and null(u eq 'for)
              and (null !*blockp or null w or null atom car w or cdr w)
           or flagp(z,'nodel)
              and (null u
                   or u eq 'group and null (z memq '(!*rsqb!* !*rcbkt!*)))
          then symerr("Improper delimiter",nil)
         else if idp u and (u eq 'paren or flagp(u,'struct))
          then symerr("Too few right parentheses",nil);
  end1: if y then symerr("Improper delimiter",nil)
         else if null v and null w and null x then return nil;
        y := 0;
        go to unwind;
  end2: if null cdr v then return car v
         else symerr("Improper delimiter",nil)
   end;

%symbolic procedure getels u;
%   getel(car u . !*evlis cdr u);

%symbolic procedure !*evlis u;
%   mapcar(u,function eval);

flag ('(endstat retstat),'endstat);

flag ('(else until),'nodel);

flag ('(begin),'go);

symbolic procedure xread u;
   progn(scan(),xread1 u);

flag('(xread),'opfn);   %to make it an operator;

endmodule;


module lpri; % Functions for printing diagnostic and error messages.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*defn !*int);

global '(!*echo !*fort !*msg !*nat !*rlisp2 cursym!* erfg!* ofl!*
         outl!*);

symbolic procedure lpri u;
   begin
    a:  if null u then return nil;
        prin2 car u;
        prin2 " ";
        u := cdr u;
        go to a
   end;

symbolic procedure lpriw (u,v);
   begin scalar x;
        u := u . if v and atom v then list v else v;
        if ofl!* and (!*fort or not !*nat or !*defn) then go to c;
        terpri();
    a:  lpri u;
        terpri();
        if null x then go to b;
        wrs cdr x;
        return nil;
    b:  if null ofl!* then return nil;
    c:  x := ofl!*;
        wrs nil;
        go to a
   end;

symbolic procedure lprim u;
   !*msg and lpriw("***",u);

symbolic procedure lprie u;
   begin scalar x;
        if !*int then go to a;
        x:= !*defn;
        !*defn := nil;
    a:  erfg!* := t;
        lpriw ("*****",u);
        if null !*int then !*defn := x
   end;

symbolic procedure printty u;
   begin scalar ofl;
        if null !*fort and !*nat then print u;
        if null ofl!* then return nil;
        ofl := ofl!*;
        wrs nil;
        print u;
        wrs cdr ofl
   end;

symbolic procedure rederr u;
   begin lprie u; error1() end;

symbolic procedure symerr(u,v);
   begin scalar x;
        erfg!* := t;
        if numberp cursym!* or not(x := get(cursym!*,'prtch))
          then x := cursym!*;
        terpri();
        if !*echo then terpri();
        outl!*:=car outl!* . '!$!$!$ . cdr outl!*;
        comm1 t;
        mapcar(function prin2, reversip!* outl!*);
        terpri();
        outl!* := nil;
        if null v then rederr u
         else rederr(x . ("invalid" .
                     (if u then list("in",u,"statement") else nil)))
   end;

symbolic procedure typerr(u,v); rederr list(u,"invalid as",v);

endmodule;


module parser;  % Functions for parsing RLISP expressions.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*backtrace !*mode);

global '(cursym!* letl!* nxtsym!*);

%With the exception of assignment statements, which are handled by
%XREAD, statements in REDUCE are introduced by a key-word, which
%initiates a reading process peculiar to that statement.  The key-word
%is recognized (in XREAD1) by the indicator STAT on its property list.
%The corresponding property is the name of the function (of no
%arguments) which carries out the reading sequence.

% ***** COMMENTS *****

symbolic procedure comm1 u;
   begin scalar bool;
        if u eq 'end then go to b;
  a:    if cursym!* eq '!*semicol!*
           or u eq 'end
                and cursym!* memq
                   '(end else then until !*rpar!* !*rsqb!*)
          then return nil
         else if u eq 'end and null bool
          then progn(lprim list("END-COMMENT NO LONGER SUPPORTED"),
                     bool := t);
  b:    scan();
        go to a
   end;


% ***** CONDITIONAL STATEMENT *****

symbolic procedure ifstat;
   begin scalar condx,condit;
    a:  condx := xread t;
        if not cursym!* eq 'then then symerr('if,t);
        condit := aconc!*(condit,list(condx,xread t));
        if not cursym!* eq 'else then nil
         else if scan() eq 'if then go to a
         else condit := aconc!*(condit,list(t,xread1 t));
        return ('cond . condit)
   end;

put('if,'stat,'ifstat);

flag ('(then else),'delim);


% ***** LAMBDA STATEMENT *****

symbolic procedure lamstat;
   begin scalar x,y;
        x:= xread 'lambda;
%       x := flagtype(if null x then nil else remcomma x,'scalar);
        if x then x := remcomma x;
        y := list('lambda,x,xread t);
%       remtype x;
        return y
   end;

put ('lambda,'stat,'lamstat);


% ***** GROUP STATEMENT *****

symbolic procedure mkprogn;
   %Expects a list of statements terminated by a >>;
   begin scalar lst;
    a:  lst := aconc!*(lst,xread 'group);
        if null(cursym!* eq '!*rsqb!*) then go to a;
        scan();
        return ('progn . lst)
   end;

put('!*lsqb!*,'stat,'mkprogn);

flag('(!*rsqb!*),'delim);

flag('(!*rsqb!*),'nodel);


% ***** END STATEMENT *****

symbolic procedure endstat;
  %This procedure can also be used for any key-words  which  take  no
  %arguments;
   begin scalar x; x := cursym!*; comm1 'end; return list x end;

put('end,'stat,'endstat);

put('endmodule,'stat,'endstat);

put('bye,'stat,'endstat);

put('quit,'stat,'endstat);

flag('(bye quit),'eval);

put('showtime,'stat,'endstat);

endmodule;


module block;   % Block statement and related operators.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*blockp);

global '(!*vars!* cursym!* nxtsym!*);

% ***** GO statement *****

symbolic procedure gostat;
   begin scalar var;
        var := if eq(scan(),'to) then scan() else cursym!*;
        scan();
        return list('go,var)
   end;

put('go,'stat,'gostat);

put('goto,'newnam,'go);


% ***** Declaration Statement *****

symbolic procedure decl u;
   begin scalar varlis,w;
    a:  if cursym!* eq '!*semicol!* then go to c
         else if not flagp!*!*(cursym!*,'type) then return varlis
         else if cursym!* eq 'dcl then go to dclr;
        w := cursym!*;
        if scan() eq 'procedure then return procstat1 w;
        varlis := append(varlis,pairvars(remcomma xread1 nil,nil,w));
    b:  if not cursym!* eq '!*semicol!* then symerr(nil,t)
         else if null u then return list('dcl,mkquote varlis);
                %top level declaration;
    c:  scan();
        go to a;
    dclr: varlis := append(varlis,dclstat1());
        go to b
   end;

flag ('(dcl real integer scalar),'type);

symbolic procedure dclstat; list('dcl,mkquote dclstat1());

symbolic procedure dclstat1;
   begin scalar x,y;
    a:  x := xread nil;
        if not cursym!* eq '!*colon!* then symerr('dcl,t);
        y := append(y,pairvars(remcomma x,nil,scan()));
        if scan() eq '!*semicol!* then return y
         else if not cursym!* eq '!*comma!* then symerr('dcl,t)
         else go to a
   end;

symbolic procedure dcl u;
   %U is a list of (id, mode) pairs, which are declared as global vars;
   begin scalar x;
      !*vars!* := append(u,!*vars!*);
      x := mapcar(function car, u);
      global x;
      flag(x,'share);
   a: if null u then return nil;
      set(caar u,get(cdar u,'initvalue));
      u := cdr u;
      go to a
   end;

put('integer,'initvalue,0);

put('dcl,'stat,'dclstat);

symbolic procedure decstat;
   %only called if a declaration occurs at the top level or not first
   %in a block;
   begin scalar x,y,z;
      if !*blockp then symerr('block,t);
      x := cursym!*;
      y := nxtsym!*;
      z := decl nil;
      if y neq 'procedure then rederr list(x,"invalid outside block");
      return z
   end;

put('integer,'stat,'decstat);

put('real,'stat,'decstat);

put('scalar,'stat,'decstat);


% ***** Block Statement *****

symbolic procedure blockstat;
   begin scalar hold,varlis,x,!*blockp;
        !*blockp := t;
        scan();
        if cursym!* memq '(nil !*rpar!*) then rederr "BEGIN invalid";
        varlis := decl t;
    a:  if cursym!* eq 'end and not nxtsym!* eq '!: then go to b;
        x := xread1 nil;
        if eqcar(x,'end) then go to c;
        not cursym!* eq 'end and scan();
        if x then hold := aconc!*(hold,x);
        go to a;
    b:  comm1 'end;
    c:  return mkblock(varlis,hold)
   end;

symbolic procedure mkblock(u,v); 'reduce!-block . (u . v);

putd('reduce!-block,'macro,
 '(lambda (u) (cons 'prog
                 (cons (mapcar (function car) (cadr u)) (cddr u)))));

symbolic procedure formblock(u,vars,mode);
   'prog . append(initprogvars cadr u,
              formprog1(cddr u,append(cadr u,vars),mode));

symbolic procedure initprogvars u;
   begin scalar x,y,z;
    a: if null u then return(reversip!* x . reversip!* y)
       else if z := get(cdar u,'initvalue)
        then y := mksetq(caar u,z) . y;
      x := caar u . x;
      u := cdr u;
      go to a
   end;

symbolic procedure formprog(u,vars,mode);
   'prog . cadr u . formprog1(cddr u,pairvars(cadr u,vars,mode),mode);

symbolic procedure formprog1(u,vars,mode);
   if null u then nil
    else if atom car u then car u . formprog1(cdr u,vars,mode)
    else if idp caar u and flagp(caar u,'modefn)
     then formc(cadar u,vars,caar u) . formprog1(cdr u,vars,mode)
    else formc(car u,vars,mode) . formprog1(cdr u,vars,mode);

put('reduce!-block,'formfn,'formblock);

put('prog,'formfn,'formprog);

put('begin,'stat,'blockstat);


% ***** Return Statement *****

symbolic procedure retstat;
   if not !*blockp then symerr(nil,t)
    else list('return,
              if flagp!*!*(scan(),'delim) then nil else xread1 t);

put('return,'stat,'retstat);

endmodule;

module form;  % Performs a mode analysis of parsed forms.

% Author: Anthony C. Hearn.

% Modifications by: Jed Marti.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*!*a2sfn !*cref !*defn !*mode current!-modulus);

global '(!*argnochk !*composites !*force !*micro!-version !*vars!*);

!*!*a2sfn := 'aeval;

flag('(algebraic symbolic),'modefn);

symbolic procedure formcond(u,vars,mode);
   'cond . formcond1(cdr u,vars,mode);

symbolic procedure formcond1(u,vars,mode);
   if null u then nil
    else list(formbool(caar u,vars,mode),form1(cadar u,vars,mode))
       % FORMC here would add REVAL
              . formcond1(cdr u,vars,mode);

put('cond,'formfn,'formcond);

symbolic procedure formlamb(u,vars,mode);
   list('lambda,cadr u,form1(caddr u,pairvars(cadr u,vars,mode),mode));

put('lambda,'formfn,'formlamb);

symbolic procedure formprogn(u,vars,mode);
   'progn . formclis(cdr u,vars,mode);

put('progn,'formfn,'formprogn);

symbolic procedure expdrmacro u;
   %returns the macro form for U if expansion is permitted;
   begin scalar x;
      if null(x := getrmacro u) or flagp(u,'noexpand) then return nil
       else if null !*cref and (null !*defn or car x eq 'smacro)
          or flagp(u,'expand) or !*force
        then return x
       else return nil
   end;

symbolic procedure getrmacro u;
   %returns a Reduce macro definition for U, if one exists,
   %in GETD format;
   begin scalar x;
      return if not idp u then nil
       else if (x := getd u) and car x eq 'macro then x
       else if (x := get(u,'smacro)) then 'smacro . x
%       else if (x := get(u,'nmacro)) then 'nmacro . x;
       else nil
   end;

symbolic procedure applmacro(u,v,w); apply1(u,w . v);

%symbolic procedure applnmacro(u,v,w);
%   apply(u,if flagp(w,'nospread) then list v else v);

% symbolic procedure applsmacro(u,v,w);
%  %We could use an atom sublis here, eg SUBLA;
%  sublis(pair(cadr u,v),caddr u);

put('macro,'macrofn,'applmacro);

%put('nmacro,'macrofn,'applnmacro);

put('smacro,'macrofn,'applsmacro);

flag('(ed go quote),'noform);

symbolic procedure set!-global!-mode u;
   begin !*mode := u end;

symbolic procedure form1(u,vars,mode);
   begin scalar x,y;
      if atom u
        then return if not idp u then u
                     else if u eq 'ed then list u
                     else if flagp(u,'modefn) then set!-global!-mode u
                     else if x:= get(mode,'idfn)
                      then apply2(x,u,vars)
                     else u
       else if not atom car u
        then if caar u eq 'lambda then return formlis(u,vars,mode)
              else typerr(car u,"operator")
       else if not idp car u then typerr(car u,"operator")
       else if get(car u, 'localfnname)
        then return form1(get(car u,'localfnname) . cdr u,vars,mode)
       else if flagp(car u,'noform) then return u
       else if r!-arrayp car u
          and (mode eq 'symbolic or intexprlisp(cdr u,vars))
        then return list('getel,intargfn(u,vars,mode))
       else if flagp(car u,'modefn)
        then return convertmode(cadr u,vars,mode,car u)
       else if (x := get(car u,'formfn))
        then return macrochk(apply(x,list(u,vars,mode)),mode)
       else if get(car u,'stat) eq 'rlis
        then return macrochk(formrlis(u,vars,mode),mode)
%      else if (x := getd car u) and eqcar(x, 'macro) and
%              not(mode eq 'algebraic) then
%            return << x := apply(cdr x, list(u, vars, mode));
%                      formc(x, vars, mode) >>
        ;
      argnochk u;
      x := formlis(cdr u,vars,mode);
      y := if x=cdr u then u else car u . x;
      return if mode eq 'symbolic
              or get(car u,'stat)
              or cdr u and eqcar(cadr u,'quote)
                       and null !*micro!-version
              or intexprnp(y,vars) and null !*composites
                 and null current!-modulus
               then macrochk(y,mode)
              else if not(mode eq 'algebraic)
               then convertmode(y,vars,mode,'algebraic)
              else ('list . algid(car u,vars) . x)
   end;

symbolic procedure argnochk u;
   begin scalar x;
      if null !*argnochk then nil
       else if (x := argsofopr car u) and x neq length cdr u
        then rederr list(car u,"called with",
                         length cdr u,
                         if length cdr u=1 then "argument"
                          else "arguments",
                         "instead of",x)
   end;

symbolic procedure argsofopr u;
   % This function may be optimizable in various implementations.
   get(u,'number!-of!-args);

symbolic procedure intexprnp(u,vars);
   %determines if U is an integer expression;
    if atom u then if numberp u then fixp u
                   else if (u := atsoc(u,vars)) then cdr u eq 'integer
                   else nil
     else idp car u and flagp(car u,'intfn) and intexprlisp(cdr u,vars);

symbolic procedure intexprlisp(u,vars);
   null u or intexprnp(car u,vars) and intexprlisp(cdr u,vars);

flag('(difference minus plus times),'intfn);
   % EXPT is not included in this list, because a negative exponent can
   % cause problems (i.e., result can be rational);

symbolic procedure formlis(u,vars,mode);
   mapcar(function (lambda x; form1(x,vars,mode)), u);

symbolic procedure formclis(u,vars,mode);
   mapcar(function (lambda x; formc(x,vars,mode)), u);

symbolic procedure form u; form1(u,!*vars!*,!*mode);

symbolic procedure macrochk(u,mode);
   begin scalar y;
   %expands U if CAR U is a macro and expansion allowed;
      if atom u then return u
       else if (y := expdrmacro car u)
        and (mode eq 'symbolic or idp car u and flagp(car u,'opfn))
        then return apply(get(car y,'macrofn),list(cdr y,cdr u,car u))
       else return u
   end;

put('symbolic,'idfn,'symbid);

symbolic procedure symbid(u,vars); u;
%   if atsoc(u,vars) or fluidp u or globalp u or u memq '(nil t)
%       or flagp(u,'share) then u
%    else <<lprim list(u,"Non-Local Identifier");% u>>;

put('algebraic,'idfn,'algid);

symbolic procedure algid(u,vars);
   if atsoc(u,vars) or flagp(u,'share) then u else mkquote u;

put('integer,'idfn,'intid);

symbolic procedure intid(u,vars);
   begin scalar x,y;
      return if (x := atsoc(u,vars))
        then if cdr x eq 'integer then u
               else if y := get(cdr x,'integer)
                then apply2(y,u,vars)
               else if cdr x eq 'scalar then !*!*a2i(u,vars)
               else rederr list(cdr x,"not convertable to INTEGER")
      else !*!*a2i(mkquote u,vars)
   end;

symbolic procedure convertmode(exprn,vars,target,source);
   convertmode1(form1(exprn,vars,source),vars,target,source);

symbolic procedure convertmode1(exprn,vars,target,source);
   begin scalar x;
      if source eq 'real then source := 'algebraic;
      if target eq 'real then target := 'algebraic;
      if target eq source then return exprn
       else if idp exprn and (x := atsoc(exprn,vars))
          and not(cdr x memq '(integer scalar real))
          and not(cdr x eq source)
        then return convertmode(exprn,vars,target,cdr x)
       else if not (x := get(source,target))
        then typerr(source,target)
       else return apply2(x,exprn,vars)
   end;

put('algebraic,'symbolic,'!*!*a2s);

put('symbolic,'algebraic,'!*!*s2a);

symbolic procedure !*!*a2s(u,vars);
   % It would be nice if we could include the ATSOC(U,VARS) line,
   % since in many cases that would save recomputation. However,
   % in any sequential process, assignments or subsititution rules
   % can change the value of a variable, so we have to check its
   % value again.  More comprehensive analysis could certainly
   % optimize this.
   if u = '(quote nil) then nil
    else if null u or constantp u and null fixp u
      or intexprnp(u,vars) and null !*composites
                 and null current!-modulus
      or not atom u and idp car u
         and flagp(car u,'nochange) and not(car u eq 'getel)
%     or atsoc(u,vars)      % means it was already evaluated
     then u
    else list(!*!*a2sfn,u);

symbolic procedure !*!*s2a(u,vars); u;

symbolic procedure formc(u,vars,mode);
   %this needs to be generalized;
   if mode eq 'algebraic and intexprnp(u,vars) then u
    else convertmode(u,vars,'symbolic,mode);

symbolic procedure intargfn(u,vars,mode);
   % transforms array element U into expression with integer arguments.
   % Array name is treated as an algebraic variable;
   'list . form1(car u,vars,'algebraic) .
       mapcar(function (lambda x;
                        convertmode(x,vars,'integer,mode)), cdr u);

put('algebraic,'integer,'!*!*a2i);

symbolic procedure !*!*a2i(u,vars);
   if intexprnp(u,vars) then u else list('ieval,u);

symbolic procedure ieval u; !*s2i reval u;

flag('(ieval),'opfn);   % To make it a symbolic operator.

flag('(ieval),'nochange);

put('symbolic,'integer,'!*!*s2i);

symbolic procedure !*!*s2i(u,vars);
   if fixp u then u else list('!*s2i,u);

symbolic procedure !*s2i u;
   if fixp u then u else typerr(u,"integer");

put('integer,'symbolic,'ridentity);

symbolic procedure ridentity(u,vars); u;

symbolic procedure formbool(u,vars,mode);
   if mode eq 'symbolic then form1(u,vars,mode)
    else if atom u then if not idp u or atsoc(u,vars) or u eq 't
           then u
          else formc!*(u,vars,mode)
    else if intexprlisp(cdr u,vars) and get(car u,'boolfn) then u
    else if idp car u and get(car u,'boolfn)
     then get(car u,'boolfn) . formclis(cdr u,vars,mode)
    else if idp car u and flagp(car u,'boolean)
        then car u .
          mapcar(function (lambda x;
            if flagp(car u,'boolargs)
                      then formbool(x,vars,mode)
                     else formc!*(x,vars,mode)), cdr u)
    else formc!*(u,vars,mode);

symbolic procedure formc!*(u,vars,mode);
   begin scalar !*!*a2sfn;
      !*!*a2sfn := 'reval;
      return formc(u,vars,mode)
   end;

% Functions with side effects must be handled carefully in this model,
% otherwise they are not always evaluated within blocks.

symbolic procedure formrederr(u,vars,mode);
   begin scalar x;
      x := formc!*(cadr u,vars,mode);
      return list('rederr,x)
   end;

put('rederr,'formfn,'formrederr);

symbolic procedure formreturn(u,vars,mode);
   begin scalar x;
      x := form1(cadr u,vars,mode);  % FORMC here would add REVAL
      if not(mode memq '(symbolic integer real))
         and eqcar(x,'setq)             % Should this be more general?
        then x := list(!*!*a2sfn,x);
      return list('return,x)
   end;

put('return,'formfn,'formreturn);

symbolic procedure formsetq(u,vars,mode);
   begin scalar target,x,y;
     u := cdr u;
     if eqcar(cadr u,'quote) then mode := 'symbolic;
      if idp car u
           and (y := atsoc(car u,vars)) and not(cdr y eq 'scalar)
        then target :=  'symbolic   % used to be CDR Y
      else target := 'symbolic;
      % Make target always SYMBOLIC so that algebraic expressions
      % are evaluated before being stored.
      x := convertmode(cadr u,vars,target,mode);
      return if not atom car u
        then if not idp caar u then typerr(car u,"assignment")
          else if r!-arrayp caar u
           then list('setel,intargfn(car u,vars,mode),x)
          else if y := get(caar u,'setqfn)
           then form1((y . append(cdar u,cdr u)),vars,mode)
%         else if y := get(caar u, 'access)
%          then list('m!-setf,
%                    list(caar u, form1(cadar u, vars, mode)),
%                    x)
          else list('setk,form1(car u,vars,'algebraic),x)
             % algebraic needed above, since SETK expects it.
    else if not idp car u then typerr(car u,"assignment")
    else if mode eq 'symbolic or y or flagp(car u,'share)
         or eqcar(x,'quote)
     then mksetq(car u,x)
    else list('setk,mkquote car u,x)
   end;

put('car,'setqfn,'rplaca);

put('cdr,'setqfn,'rplacd);

put('setq,'formfn,'formsetq);

symbolic procedure formfunc(u,vars,mode);
   if idp cadr u then if getrmacro cadr u
     then rederr list("Macro",cadr u,"Used as Function")
        else list('function,cadr u)
    else list('function,form1(cadr u,vars,mode));

put('function,'formfn,'formfunc);

% RLIS is a parser function that reads a list of arguments and returns
% this list as one argument.  It needs to be defined in this module for
% bootstrapping purposes since this definition only works with its form
% function.

symbolic procedure rlis;
   begin scalar x;
        x := cursym!*;
        return if flagp!*!*(scan(),'delim) then list(x,nil)
                else x . remcomma xread1 'lambda
   end;

symbolic procedure flagop u; begin flag(u,'flagop); rlistat u end;

symbolic procedure rlistat u;
   begin
    a:  if null u then return nil;
        put(car u,'stat,'rlis);
        u := cdr u;
        go to a
   end;

rlistat '(flagop);

symbolic procedure formrlis(u,vars,mode);
   if not flagp(car u,'flagop)
        then list(car u,'list . formlis(cdr u,vars,'algebraic))
    else if not idlistp cdr u
     then typerr('!*comma!* . cdr u,"identifier list")
    else mkprog(nil,list('flag,mkquote cdr u,mkquote car u)
                             . get(car u,'simpfg));

symbolic procedure mkarg(u,vars);
   % Returns the "unevaled" form of U.
   if null u or constantp u then u
    else if atom u then if atsoc(u,vars) then u else mkquote u
    else if car u eq 'quote then mkquote u
    else 'list . mapcar(function (lambda x; mkarg(x,vars)), u);

endmodule;


module proc;   % Procedure statement.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*backtrace);

global '(!*argnochk !*comp !*lose cursym!* erfg!* fname!* ftypes!*);

fluid '(!*defn);

!*lose := t;

ftypes!* := '(expr fexpr macro);

symbolic procedure putc(name,type,body);
   %defines a non-standard function, such as an smacro. Returns NAME;
   begin
      if !*comp and flagp(type,'compile) then compd(name,type,body)
       else put(name,type,body);
      return name
   end;

% flag('(putc),'eval);

symbolic procedure formproc(u,vars,mode);
   begin scalar body,name,type,varlis,x,y;
        u := cdr u;
        name := car u;
        if cadr u then mode := cadr u;   % overwrite previous mode
        u := cddr u;
        type := car u;
        if flagp(name,'lose) and (!*lose or null !*defn)
          then return progn(lprim list(name,
                            "not defined (LOSE flag)"),
                        nil);
        varlis := cadr u;
        u := caddr u;
        x := if eqcar(u,'reduce!-block) then cadr u else nil;
        y := pairxvars(varlis,x,vars,mode);
        if x then u := car u . rplaca!*(cdr u,cdr y);
        body:= form1(u,car y,mode);   % FORMC here would add REVAL
        if type eq 'expr then body := list('de,name,varlis,body)
         else if type eq 'fexpr then error(0, "FEXPR definition")
         else if type eq 'macro then body := list('defmacro,name,'!&whole . varlis,body)
         else if type eq 'emb then return embfn(name,varlis,body)
         else body := list('put,
                           mkquote name,
                           mkquote type,
                           mkquote list('lambda,varlis,body));
        if not(mode eq 'symbolic)
          then body := list('progn,
                         list('flag,mkquote list name,mkquote 'opfn),
                          body);
        if !*argnochk and type memq '(expr smacro)
          then body := list('progn,
                        list('put,mkquote name,
                                  mkquote 'number!-of!-args,
                                  length varlis),
                          body);
        if !*defn and type memq '(fexpr macro smacro) then eval body;
        return body
   end;

put('procedure,'formfn,'formproc);

symbolic procedure pairxvars(u,v,vars,mode);
   %Pairs procedure variables and their modes, taking into account
   %the convention which allows a top level prog to change the mode
   %of such a variable;
   begin scalar x,y;
   a: if null u then return append(reversip!* x,vars) . v
       else if (y := atsoc(car u,v))
        then <<v := delete(y,v);
               if not(cdr y eq 'scalar) then x := (car u . cdr y) . x
                else x := (car u . mode) . x>>
       else x := (car u . mode) . x;
      u := cdr u;
      go to a
   end;

symbolic procedure procstat1 mode;
   begin scalar bool,u,type,x,y,z;
        bool := erfg!*;
        if fname!* then go to b
         else if cursym!* eq 'procedure then type := 'expr
         else progn(type := cursym!*,scan());
        if not cursym!* eq 'procedure then go to c;
        x := errorset('(xread (quote proc)),nil,!*backtrace);
        if errorp x then go to a
         else if atom (x := car x) then x := list x;   %no arguments;
        fname!* := car x;   %function name;
        if idp fname!* %AND NOT(TYPE MEMQ FTYPES!*);
          then if null fname!* or (z := gettype fname!*)
                        and not z memq '(procedure operator)
                then go to d
              else if not getd fname!* then flag(list fname!*,'fnc);
           %to prevent invalid use of function name in body;
        u := cdr x;
        y := u;
        x := car x . y;
    a:  z := errorset('(xread t),nil,!*backtrace);
        if not errorp z then z := car z;
        if null erfg!* then z:=list('procedure,car x,mode,type,y,z);
        remflag(list fname!*,'fnc);
        fname!*:=nil;
        if erfg!* then progn(z := nil,if not bool then error1());
        return z;
    b:  bool := t;
    c:  errorset('(symerr (quote procedure) t),nil,!*backtrace);
        go to a;
    d:  typerr(list(z,fname!*),"procedure");
        go to a
   end;

symbolic procedure procstat; procstat1 nil;

deflist ('((procedure procstat) (expr procstat) (fexpr procstat)
           (emb procstat) (macro procstat) (smacro procstat)),
        'stat);

% Next line refers to bootstrapping process.

if get('symbolic,'stat) eq 'procstat then remprop('symbolic,'stat);

deflist('((lisp symbolic)),'newnam);

endmodule;


module forstat;   % Definition of REDUCE FOR loops.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*blockp);

global '(cursym!* foractions!*);

comment the syntax of the FOR statement is as follows:

                 {step i3 until}
        {i := i1 {             } i2 }
        {        {      :      }    }
   for  {                           } <action> <expr>
        {        { in }             }
        { each i {    }  <list>     }
                 { on }

In all cases, the <expr> is evaluated algebraically within the scope of
the current value of i.  If <action> is DO, then nothing else happens.
In other cases, <action> is a binary operator that causes a result to be
built up and returned by FOR.  In each case, the loop is initialized to
a default value.  The test for the end condition is made before any
action is taken.

The effect of the definition here is to replace all for loops by
semantically equivalent blocks.  As a result, none of the mapping
functions are needed in REDUCE.

To declare a set of actions, one says;

foractions!* := '(do collect conc product sum);

remflag(foractions!*,'delim);    % For bootstrapping purposes.

% To associate a binary function with an action, one says:

deflist('((product times) (sum plus)),'bin);

% And to give these an initial value in a loop:

deflist('((product 1) (sum 0)),'initval);

% NB:  We need to reset for and let delims if an error occurs.  It's
% probably best to do this in the begin1 loop.

flag('(for),'nochange);

symbolic procedure forstat;
   begin scalar !*blockp;
      return if scan() eq 'all then forallstat()
              else if cursym!* eq 'each then foreachstat()
              else forloop()
   end;

put('for,'stat,'forstat);

symbolic procedure forloop;
   begin scalar action,bool,incr,var,x;
      flag('(step),'delim);
      x := errorset('(xread1 'for),t,t);
      remflag('(step),'delim);
      if errorp x then error1() else x := car x;
      if not eqcar(x,'setq) or not idp(var := cadr x)
        then symerr('for,t);
      x := caddr x;
      if cursym!* eq 'step
        then <<flag('(until),'delim);
               incr := xread t;
               remflag('(until),'delim);
               if not cursym!* eq 'until then symerr('for,t)>>
       else if cursym!* eq '!*colon!* then incr := 1
       else symerr('for,t);
      if flagp(car foractions!*,'delim) then bool := t % nested loop
       else flag(foractions!*,'delim);
      incr := list(x,incr,xread t);
      if null bool then remflag(foractions!*,'delim);
      if not((action := cursym!*) memq foractions!*)
        then symerr('for,t);
      return list('for,var,incr,action,xread t)
   end;

symbolic procedure formfor(u,vars,mode);
   begin scalar action,algp,body,endval,incr,initval,var,x;
        %ALGP is used to determine if the loop calculation must be
        %done algebraically or not;
      var := cadr u;
      incr := caddr u;
      incr := list(formc(car incr,vars,mode),
                   formc(cadr incr,vars,mode),
                   formc(caddr incr,vars,mode));
      if intexprnp(car incr,vars) and intexprnp(cadr incr,vars)
         and not atsoc(var,vars)
        then vars := (var . 'integer) . vars;
      action := cadddr u;
      body :=
         formc(car cddddr u,
               (var .
                if intexprlisp(caddr u,vars) then 'integer else mode)
                   . vars,mode);
      algp := algmodep car incr or algmodep cadr incr
                 or algmodep caddr incr;
      initval := car incr;
      endval := caddr incr;
      incr := cadr incr;
      x := if algp then list('list,''difference,endval,var)
            else list('difference,endval,var);
      if incr neq 1
        then x := if algp then list('list,''times,incr,x)
                   else list('times,incr,x);
      % We could consider simplifying X here (via reval).
      x := if algp then list('aminusp!:,x) else list('minusp,x);
      return forformat(action,body,initval,x,
                       list('plus2,incr),var,vars,mode)
   end;

put('for,'formfn,'formfor);

symbolic procedure algmodep u; eqcar(u,'aeval);

symbolic procedure aminusp!: u;
   begin scalar x;
      u := aeval u;
      x := u;
      if fixp x then return minusp x
       else if not eqcar(x,'!*sq)
        then msgpri(nil,reval u,"invalid in FOR statement",nil,t);
      x := cadr x;
      if fixp car x and fixp cdr x then return minusp car x
       else if not cdr x = 1
             or not (atom(x := car x) or atom car x)
         % Should be DOMAINP, but SMACROs not yet defined.
        then msgpri(nil,reval u,"invalid in FOR statement",nil,t)
       else return apply('!:minusp,list x)
   end;

symbolic procedure foreachstat;
   begin scalar w,x,y,z;
        if not idp(x := scan()) or not (y := scan()) memq '(in on)
          then symerr("FOR EACH",t)
         else if flagp(car foractions!*,'delim) then w := t
         else flag(foractions!*,'delim);
        z := xread t;
        if null w then remflag(foractions!*,'delim);
        w := cursym!*;
        if not w memq foractions!* then symerr("FOR EACH",t);
        return list('foreach,x,y,z,w,xread t)
   end;

put('foreach,'stat,'foreachstat);

symbolic procedure formforeach(u,vars,mode);
   begin scalar action,body,lst,mod,var;
        var := cadr u; u := cddr u;
        mod := car u; u := cdr u;
        lst := formc(car u,vars,mode); u := cdr u;
        if not(mode eq 'symbolic) then lst := list('getrlist,lst);
        action := car u; u := cdr u;
        body := formc(car u,(var . mode) . vars,mode);
        if mod eq 'in
          then body := list(list('lambda,list var,body),list('car,var))
         else if not(mode eq 'symbolic) then typerr(mod,'action);
        return forformat(action,body,lst,
                         list('null,var),list 'cdr,var,vars,mode)
   end;

put('foreach,'formfn,'formforeach);

symbolic procedure forformat(action,body,initval,
                             testexp,updform,var,vars,mode);
   begin scalar result;
      result := gensym();
      return
         sublis(list('body2 .
                if mode eq 'symbolic or intexprnp(body,vars)
                  then list(get(action,'bin),body,result)
                 else list('aeval,list('list,mkquote get(action,'bin),
                            body,result)),
               'body3 .
                   if mode eq 'symbolic then body
                      else list('getrlist,body),
               'body . body,
               'initval . initval,
               'nillist . if mode eq 'symbolic then nil else ''(list),
               'result . result,
               'initresult . get(action,'initval),
               'resultlist . if mode eq 'symbolic then result
                              else list('cons,''list,result),
               'testexp . testexp,
               'updfn . car updform,
               'updval . cdr updform,
               'var . var),
          if action eq 'do
            then '(prog (var)
                  (setq var initval)
              lab (cond (testexp (return nil)))
                  body
                  (setq var (updfn var . updval))
                  (go lab))
           else if action eq 'collect
            then '(prog (var result endptr)
                  (setq var initval)
                  (cond (testexp (return nillist)))
                  (setq result (setq endptr (cons body nil)))
                looplabel
                  (setq var (updfn var . updval))
                  (cond (testexp (return resultlist)))
                  (rplacd endptr (cons body nil))
                  (setq endptr (cdr endptr))
                  (go looplabel))
           else if action eq 'conc
            then '(prog (var result endptr)
                  (setq var initval)
               startover
                  (cond (testexp (return nillist)))
                  (setq result body)
                  (setq endptr (lastpair resultlist))
                  (setq var (updfn var . updval))
                  (cond ((atom endptr) (go startover)))
                looplabel
                  (cond (testexp (return result)))
                  (rplacd endptr body3)
                  (setq endptr (lastpair endptr))
                  (setq var (updfn var . updval))
                  (go looplabel))
           else '(prog (var result)
                 (setq var initval)
                 (setq result initresult)
              lab1
                 (cond (testexp (return result)))
                 (setq result body2)
                 (setq var (updfn var . updval))
                 (go lab1)))
   end;

symbolic procedure lastpair u;
   % Return the last pair of the list u.
   if atom u or atom cdr u then u else lastpair cdr u;

put('join,'newnam,'conc);   % alternative for CONC

endmodule;

module loops;  % Looping forms other than the FOR statement.

% Author: Anthony C. Hearn

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*blockp);

global '(cursym!*);


% ***** REPEAT STATEMENT *****

symbolic procedure repeatstat;
  begin scalar body,!*blockp;
        flag('(until),'delim);
        body:= xread t;
        remflag('(until),'delim);
        if not cursym!* eq 'until then symerr('repeat,t);
        return list('repeat,body,xread t);
   end;

symbolic macro procedure repeat u;
   begin scalar body,bool,lab;
        body := cadr u; bool := caddr u;
        lab := gensym();
        return mkprog(nil,list(lab,body,
                list('cond,list(list('not,bool),list('go,lab)))))
   end;

put('repeat,'stat,'repeatstat);

flag('(repeat),'nochange);

symbolic procedure formrepeat(u,vars,mode);
   list('repeat,formc(cadr u,vars,mode),formbool(caddr u,vars,mode));

put('repeat,'formfn,'formrepeat);


% ***** WHILE STATEMENT *****

symbolic procedure whilstat;
   begin scalar bool,!*blockp;
        flag('(do),'delim);
        bool := xread t;
        remflag('(do),'delim);
        if not cursym!* eq 'do then symerr('while,t);
        return list('while,bool,xread t)
   end;

symbolic macro procedure while u;
   begin scalar body,bool,lab;
        bool := cadr u; body := caddr u;
        lab := gensym();
        return mkprog(nil,list(lab,list('cond,list(list('not,bool),
                list('return,nil))),body,list('go,lab)))
   end;

put('while,'stat,'whilstat);

flag('(while),'nochange);

symbolic procedure formwhile(u,vars,mode);
   list('while,formbool(cadr u,vars,mode),formc(caddr u,vars,mode));

put('while,'formfn,'formwhile);

endmodule;


module write;  % Miscellaneous statement definitions.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

% ***** DEFINE STATEMENT *****

remprop('define,'stat);

symbolic procedure define u;
   for each x in u do
      if not eqcar(x,'equal) or not idp cadr x
        then typerr(x,"DEFINE declaration")
       else put(cadr x,'newnam,caddr x);

put('define,'stat,'rlis);

flag('(define),'eval);

% ***** WRITE STATEMENT *****

symbolic procedure formwrite(u,vars,mode);
   begin scalar bool1,bool2,x,z;
      u := cdr u;
      bool1 := mode eq 'symbolic;
      while u do
        <<x := formc(car u,vars,mode);
          z := (if bool1 then list('prin2,x)
                      else list('writepri,mkarg1(x,vars),
          if not cdr u then if not bool2 then ''only else ''last
           else if not bool2 then ''first else nil)) .
                             z;
          bool2 := t;
          u := cdr u>>;
        return mkprog(nil,reversip!* z)
   end;

symbolic procedure writepri(u,v);
   begin scalar x; x := assgneval u; return varpri(car x,cdr x,v) end;

symbolic procedure mkarg1(u,vars);
   % Returns the "unevaled" form of U for the WRITE command.
   if null u or constantp u then u
    else if atom u then if atsoc(u,vars)
     then list('mkquote,u) else mkquote u
    else if car u eq 'quote then mkquote u
    else if car u eq 'setq
     then list('list,''setq,mkquote cadr u,mkarg1(caddr u,vars))
    else 'list . for each x in u collect mkarg1(x,vars);

put('write,'stat,'rlis);

put('write,'formfn,'formwrite);

endmodule;


module smacro;  % Support for SMACRO expansion.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

symbolic procedure applsmacro(u,vals,name);
   % U is smacro body of form (lambda <varlist> <body>), VALS is
   % argument list, NAME is name of smacro.
   begin scalar body,remvars,varlist,w;
      varlist := cadr u;
      body := caddr u;
      if length varlist neq length vals
        then rederr list("Argument mismatch for SMACRO",name);
      if no!-side!-effect!-listp vals or one!-entry!-listp(varlist,body)
        then return subla!-q(pair(varlist,vals),body)
       else if length varlist>1
        then <<w := for each x in varlist collect (x . gensym());
               body := subla!-q(w,body);
               varlist := for each x in w collect cdr x>>;
      for each x in vals do
         <<if no!-side!-effectp x or one!-entryp(car varlist,body)
             then body := subla!-q(list(car varlist . x),body)
            else remvars := aconc(remvars,car varlist . x);
           varlist := cdr varlist>>;
      if null remvars then return body
       else <<w := list('lambda,
                         for each x in remvars collect car x,
                         body) .
                    for each x in remvars collect cdr x;
%             IF NOT EQCAR(CADR W,'SETQ)
%               THEN <<PRIN2 "*** SMACRO: "; PRINT CDR W>>;
              return w>>
   end;

symbolic procedure no!-side!-effectp u;
   if atom u then numberp u or idp u and not(fluidp u or globalp u)
    else if car u eq 'quote then t
    else if flagp!*!*(car u,'nosideeffects)
     then no!-side!-effect!-listp u
    else nil;

symbolic procedure no!-side!-effect!-listp u;
   null u or no!-side!-effectp car u and no!-side!-effect!-listp cdr u;

flag('(car cdr caar cadr cdar cddr caaar caadr cadar caddr cdaar cdadr
       cddar cdddr cons),'nosideeffects);

symbolic procedure one!-entryp(u,v);
   % determines if id U occurs less than twice in V.
   if atom v then t
    else if smemq(u,car v)
     then if smemq(u,cdr v) then nil else one!-entryp(u,car v)
    else one!-entryp(u,cdr v);

symbolic procedure one!-entry!-listp(u,v);
   null u or one!-entryp(car u,v) and one!-entry!-listp(cdr u,v);

symbolic procedure subla!-q(u,v);
   begin scalar x;
        if null u or null v then return v
         else if atom v
                 then return if x:= atsoc(v,u) then cdr x else v
         else if car v eq 'quote then return v
         else return(subla!-q(u,car v) . subla!-q(u,cdr v))
   end;

endmodule;


module infix; % Functions for introducing new infix operators.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*mode);

global '(preclis!*);

symbolic procedure infix x;
   begin scalar y;
    a: if null x then go to b;
      y := car x;
      if !*mode eq 'algebraic then mkop y;
      if not(y member preclis!*) then preclis!* := y . preclis!*;
      x := cdr x;
      go to a;
    b: mkprec()
   end;

symbolic procedure precedence u;
   begin scalar x,y,z;
      preclis!* := delete(car u,preclis!*);
      y := cadr u;
      x := preclis!*;
   a: if null x then rederr list (y,"not found")
       else if y eq car x
        then <<preclis!* :=
                  nconc!*(reversip!* z,car x . (car u . cdr x));
               mkprec();
               return nil>>;
      z := car x . z;
      x := cdr x;
      go to a
   end;

deflist('((infix rlis) (precedence rlis)),'stat);

flag('(infix precedence),'eval);

endmodule;


module where;  % Support for a where construct.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

symbolic procedure formwhere(u,vars,mode);
   begin scalar expn,equivs,y,z;
     expn := cadr u;
     equivs := caddr u;
     if eqcar(equivs,'!*comma!*) then equivs := cdr equivs
      else equivs := list equivs;
     for each x in equivs do
        if not atom x and car x memq '(equal setq)
          then <<y := caddr x . y; z := cadr x . z>>
         else rederr list(x,"invalid in WHERE statement");
     return formc(list('lambda,reversip z,expn) . reversip y,
                  vars,mode)
   end;

put('where,'formfn,'formwhere);

% infix where;   % We do this explicitly to avoid changing preclis*.

deflist('((where 1)),'infix);

put('where,'op,'((1 1)));

endmodule;


module list; % Define a list as a list of expressions in curly brackets.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(orig!* posn!*);

global '(cursym!*);

% Add to system table.

put('list,'tag,'list);

put('list,'rtypefn,'(lambda (x) 'list));

% Parsing interface.

symbolic procedure xreadlist;
   % expects a list of expressions enclosed by {, }.
   % also allows expressions separated by ; --- treats these as progn.
   begin scalar cursym,delim,lst;
        if scan() eq '!*rcbkt!* then <<scan(); return list 'list>>;
    a:  lst := aconc(lst,xread1 'group);
        cursym := cursym!*;
        scan();
        if cursym eq '!*rcbkt!*
          then return if delim eq '!*semicol!* then 'progn . lst
                       else 'list . lst
         else if null delim then delim := cursym
         else if not(delim eq cursym)
          then symerr("syntax error: mixed , and ; in list",nil);
        go to a
   end;

put('!*lcbkt!*,'stat,'xreadlist);

newtok '((!{) !*lcbkt!*);

newtok '((!}) !*rcbkt!*);

flag('(!*rcbkt!*),'delim);

flag('(!*rcbkt!*),'nodel);

% Evaluation interface.

put('list,'evfn,'listeval);

symbolic procedure getrlist u;
   if eqcar(u,'list) then cdr u
    else typerr(if eqcar(u,'!*sq) then prepsq cadr u else u,"list");

symbolic procedure listeval(u,v);
   if atom u then listeval(get(u,'rvalue),v)
    else car u . for each j in cdr u collect reval1(j,v);

% Length interface.

put('list,'lengthfn,'(lambda (x) (length (cdr x))));


% Printing interface.

put('list,'prifn,'listpri);

symbolic procedure listpri l;
   % This definition is basically that of INPRINT, except that it
   % decides when to split at the comma by looking at the size of
   % the argument.
   begin scalar orig,split,u;
      u := l;
      l := cdr l;
      prin2!* get('!*lcbkt!*,'prtch);
         % Do it this way so table can change.
      orig := orig!*;
      orig!* := if posn!*<18 then posn!* else orig!*+3;
      if null l then go to b;
      split := treesizep(l,40);   % 40 is arbitrary choice.
   a: maprint(negnumberchk car l,0);
      l := cdr l;
      if null l then go to b;
      oprin '!*comma!*;
      if split then terpri!* t;
      go to a;
   b: prin2!* get('!*rcbkt!*,'prtch);
%     terpri!* nil;
      orig!* := orig;
      return u
   end;

symbolic procedure treesizep(u,n);
   % true if u has recursively more pairs than n.
   treesizep1(u,n)=0;

symbolic procedure treesizep1(u,n);
   if atom u then n-1
    else if (n := treesizep1(car u,n))>0 then treesizep1(cdr u,n)
    else 0;

% Definitions of operations on lists

symbolic procedure rfirst u;
   <<argnochk ('first . u);
     if null(getrtype(u := reval car u) eq 'list)
       then typerr(u,"list")
      else if null cdr u then parterr(u,1)
      else cadr u>>;

put('first,'psopfn,'rfirst);

symbolic procedure parterr(u,v);
   msgpri("Expression",u,"does not have part",v,t);

symbolic procedure rsecond u;
   <<argnochk ('second . u);
     if null(getrtype(u := reval car u) eq 'list) then typerr(u,"list")
      else if null cdr u or null cddr u then parterr(u,2)
      else caddr u>>;

put('second,'psopfn,'rsecond);

symbolic procedure rthird u;
   <<argnochk ('third . u);
     if null(getrtype(u := reval car u) eq 'list) then typerr(u,"list")
      else if null cdr u or null cddr u or null cdddr u
       then parterr(u,3)
      else cadddr u>>;

put('third,'psopfn,'rthird);

symbolic procedure rrest u;
   <<argnochk ('rest . u);
     if null(getrtype(u := reval car u) eq 'list) then typerr(u,"list")
      else if null cdr u then typerr(u,"non-empty list")
      else 'list . cddr u>>;

put('rest,'psopfn,'rrest);

symbolic procedure rappend u;
   begin scalar x,y;
      argnochk ('append . u);
      if null(getrtype(x := reval car u) eq 'list)
        then typerr(x,"list")
      else if null(getrtype(y := reval cadr u) eq 'list)
       then typerr(y,"list")
      else return 'list .append(cdr x,cdr y)
   end;

put('append,'psopfn,'rappend);

symbolic procedure rcons u;
   begin scalar x,y;
      argnochk ('cons . u);
      if (y := getrtype(x := reval cadr u)) eq 'vector
        then return prepsq simpdot u
       else if not(y eq 'list) then typerr(x,"list")
       else return 'list . reval car u . cdr x
   end;

put('cons,'psopfn,'rcons);

symbolic procedure rreverse u;
   <<argnochk ('reverse . u);
     if null(getrtype(u := reval car u) eq 'list) then typerr(u,"list")
      else 'list . reverse cdr u>>;

put('reverse,'psopfn,'rreverse);

endmodule;


module array; % Array statement.

% Author: Anthony C. Hearn.
% Modifications by: Nancy Kirkwood.

% These definitions are very careful about bounds checking. Appropriate
% optimizations in a given system might really speed things up.

global '(erfg!*);

symbolic procedure getel u;
   % Returns the value of the array element U.
   getel1(get(car u,'rvalue),cdr u,get(car u,'dimension));

symbolic procedure getel1(u,v,dims);
   if length v neq length dims
     then rederr "Incorrect array reference"
    else if null v then u
    else if car v geq car dims then rederr "Array out of bounds"
    else getel1(getv(u,car v),cdr v,cdr dims);

symbolic procedure setel(u,v);
   % Sets array element U to V and returns V.
   setel1(get(car u,'rvalue),cdr u,v,get(car u,'dimension));

symbolic procedure setel1(u,v,w,dims);
   if length v neq length dims then rederr "Incorrect array reference"
     else if car v geq car dims then rederr "Array out of bounds"
     else if null cdr v then putv(u,car v,w)
     else setel1(getv(u,car v),cdr v,w,cdr dims);

symbolic procedure dimension u; get(u,'dimension);


comment further support for REDUCE arrays;

symbolic procedure typechk(u,v);
   begin scalar x;
      if (x := gettype u) eq v or x eq 'parameter
        then lprim list(v,u,"redefined")
       else if x then typerr(list(x,u),v)
   end;

symbolic procedure arrayfn(u,v);
   % U is the defining mode, V a list of lists, assumed syntactically
   % correct. ARRAYFN declares each element as an array unless a
   % semantic mismatch occurs.
   begin scalar y;
      for each x in v do
         <<typechk(car x,'array);
           y := add1lis for each z in cdr x collect eval z;
           if null erfg!*
             then <<put(car x,'rtype,'array);
                    put(car x,'rvalue,mkarray(y,u));
                    put(car x,'dimension,y)>>>>
   end;

symbolic procedure add1lis u;
   if null u then nil else (car u+1) . add1lis cdr u;

symbolic procedure mkarray(u,v);
   %U is a list of positive integers representing array bounds, V
   %the defining mode. Value is an array structure;
   if null u then if v eq 'symbolic then nil else 0
    else begin integer n; scalar x;
      n := car u-1;
      x := mkvect n;
      for i:=0:n do putv(x,i,mkarray(cdr u,v));
      return x
   end;

rlistat '(array);

flag ('(array arrayfn),'eval);

symbolic procedure formarray(u,vars,mode);
   begin scalar x;
      x := cdr u;
      while x do <<if atom x then typerr(x,"Array List")
                  else if atom car x or not idp caar x
                         or not listp cdar x
                  then typerr(car x,"Array declaration");
                   x := cdr x>>;
      u := for each z in cdr u collect intargfn(z,vars,mode);
      %ARRAY arguments must be returned as quoted structures;
      return list('arrayfn,mkquote mode,'list . u)
   end;

symbolic procedure listp u;
   % Returns T if U is a top level list.
   null u or not atom u and listp cdr u;

put('array,'formfn,'formarray);

put('array,'rtypefn,'arraychk);

symbolic procedure arraychk u;
   % If arraychk receives NIL, it means that array name is being used
   % as an identifier. We no longer permit this.
   if null u then 'array else nil;
%  nil;

put('array,'evfn,'arrayeval);

symbolic procedure arrayeval(u,v);
   % Eventually we'll support this.
   rederr "Array arithmetic not defined";

put('array,'lengthfn,'arraylength);

symbolic procedure arraylength u; 'list . get(u,'dimension);

endmodule;


module switch;  % Support for switches and ON and OFF statements.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

global '(!*switchcheck switchlist!*);

% No references to RPLAC-based functions in this module.

symbolic procedure on u; onoff(u,t);

symbolic procedure off u; onoff(u,nil);

symbolic procedure onoff(u,bool);
   for each j in u do
      begin scalar x,y;
         if not idp j then typerr(j,"switch")
          else if not flagp(j,'switch)
           then if !*switchcheck
                  then rederr list(j,"not defined as switch")
                 else lpriw("*****",list(j,"not defined as switch"));
         x := intern compress append(explode '!*,explode j);
         if !*switchcheck and eval x eq bool then return nil
          else if y := atsoc(bool,get(j,'simpfg))
           then eval mkprog(nil,cdr y);
          set(x,bool)
      end;

symbolic procedure switch u;
   % Declare list u as switches.
   for each x in u do
      begin scalar y;
         if not idp x then typerr(x,"switch");
         if not u memq switchlist!*
           then switchlist!* := x . switchlist!*;
         flag(list x,'switch);
         y := intern compress append(explode '!*,explode x);
         if not fluidp y and not globalp y then fluid list y
      end;

deflist('((switch rlis)),'stat);   % we use deflist since it's flagged
                                   % eval
rlistat '(off on);

flag ('(off on),'ignore);

% Symbolic mode switches:

switch backtrace,comp,defn,demo,echo,errcont,int,msg,output,pret,
       quotenewnam,raise,time;    % switchcheck.

% The following are compiler switches that may not be supported in all
% versions:

switch pgwd,plap,pwrds;

% flag('(switch),'eval);

endmodule;

module io; % Reduce functions for handling input and output of files.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*backtrace !*int semic!*);

global '(!*echo contl!* curline!* ifl!* ipl!* linelist!* ofl!* opl!*
         techo!*);

symbolic procedure file!-transform(u,v);
   % Performs a transformation on the file u.  V is name of function
   % used for the transformation;
   begin scalar echo,ichan,oldichan,val;
      echo := !*echo;
      !*echo := nil;
      ichan := open(u,'input);
      oldichan := rds ichan;
      val := errorset(list v,t,!*backtrace);
      !*echo := echo;
      close ichan;
      rds oldichan;
      if not errorp val then return car val
   end;

symbolic procedure infile u;
   % loads the single file u into REDUCE without echoing;
   begin scalar !*int;
   return file!-transform(u,function begin1)
   end;

symbolic procedure in u;
   begin scalar chan,echo,echop,type;
    echop := semic!* eq '!;;   %record echo character from input;
    echo := !*echo;   %save current echo status;
    if null ifl!* then techo!* := !*echo;   %terminal echo status;
    for each fl in u do
      <<if fl eq 't then fl := nil;
        if null fl then <<!*echo := techo!*; rds nil; ifl!* := nil>>
         else <<chan := open(fl := mkfil fl,'input);
                rds chan;
%               if assoc(fl,linelist!*) then nil;
                curline!* := 1;
                ifl!* := list(fl,chan,1)>>;
        ipl!* := ifl!* . ipl!*;  %add to input file stack;
        !*echo := echop;
        type := filetype fl;
        if type and (type := get(type,'action)) then eval list type
         else begin1();
        if chan then close chan;
        if fl eq caar ipl!* then ipl!* := cdr ipl!*
         else errach list("FILE STACK CONFUSION",fl,ipl!*)>>;
    !*echo := echo;   %restore echo status;
    if ipl!* and null contl!* then ifl!* := car ipl!*
     else ifl!* := nil;
    if ifl!* then <<rds cadr ifl!*; curline!* := caddr ifl!*>>
     else rds nil
   end;

symbolic procedure out u;
   %U is a list of one file;
   begin integer n; scalar chan,fl,x;
        n := linelength nil;
        if null u then return nil
         else if car u eq 't then return <<wrs(ofl!* := nil); nil>>;
        fl := mkfil car u;
        if not (x := assoc(fl,opl!*))
          then <<chan := open(fl,'output);
                 if chan
                   then <<ofl!*:= fl . chan; opl!*:= ofl!* . opl!*>>>>
         else ofl!* := x;
        wrs cdr ofl!*;
        linelength n
   end;

symbolic procedure shut u;
   %U is a list of names of files to be shut;
   begin scalar fl1;
      for each fl in u do
       <<if fl1 := assoc((fl := mkfil fl),opl!*)
           then <<opl!* := delete(fl1,opl!*);
                  if fl1=ofl!* then <<ofl!* := nil; wrs nil>>;
                  close cdr fl1>>
         else if not (fl1 := assoc(fl,ipl!*))
          then rederr list(fl,"not open")
         else if fl1 neq ifl!*
          then <<close cadr fl1; ipl!* := delete(fl1,ipl!*)>>
         else rederr list("Cannot shut current input file",car fl1)>>
   end;

deflist ('((in rlis) (out rlis) (shut rlis)),'stat);

flag ('(in out shut),'eval);

flag ('(in out shut),'ignore);

endmodule;


module inter; % Functions for interactive support.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

fluid '(!*int);

global '(!$eof!$
         !*echo
         !*lessspace
         cloc!*
         contl!*
         curline!*
         edit!*
         eof!*
         erfg!*
         flg!*
         ifl!*
         ipl!*
         key!*
         ofl!*
         opl!*
         techo!*);

symbolic procedure pause;
   %Must appear at the top-most level;
   if null !*int then nil
    else if key!* eq 'pause then pause1 nil
    else %typerr('pause,"lower level command");
         pause1 nil;   %Allow at lower level for now;

symbolic procedure pause1 bool;
   begin
      if bool then
        if getd 'edit1 and erfg!* and cloc!* and yesp "Edit?"
          then return <<contl!* := nil;
           if ofl!* then <<lprim list(car ofl!*,'shut);
                           close cdr ofl!*;
                           opl!* := delete(ofl!*,opl!*);
                           ofl!* := nil>>;
           edit1(cloc!*,nil)>>
         else if flg!* then return (edit!* := nil);
      if null ifl!* or yesp "Cont?" then return nil;
      ifl!* := list(car ifl!*,cadr ifl!*,curline!*);
      contl!* := ifl!* . !*echo . contl!*;
      rds (ifl!* := nil);
      !*echo := techo!*
   end;

symbolic procedure yesp u;
   begin scalar bool,ifl,ofl,x,y,z;
        if ifl!*
          then <<ifl := ifl!* := list(car ifl!*,cadr ifl!*,curline!*);
                 rds nil>>;
        if ofl!* then <<ofl:= ofl!*; wrs nil>>;
        if null !*lessspace then terpri();
        if atom u then prin2 u else lpri u;
        prin2t " (Y or N)";
        if null !*lessspace then terpri();
        z := setpchar '!?;
    a:  x := read();
        % Assume an end-of-file is the same as "yes".
        if (y := x eq 'y or x eq !$eof!$) or x eq 'n then go to b;
        if null bool then prin2t "TYPE Y OR N";
        bool := t;
        go to a;
    b:  setpchar z;
        if ofl then wrs cdr ofl;
        if ifl then rds cadr ifl;
        cursym!* := '!*semicol!*;
        return y
   end;

symbolic procedure cont;
   begin scalar fl,techo;
        if ifl!* then return nil   %CONT only active from terminal;
         else if null contl!* then rederr "No file open";
        fl := car contl!*;
        techo := cadr contl!*;
        contl!* := cddr contl!*;
        if car fl=caar ipl!* and cadr fl=cadar ipl!*
          then <<ifl!* := fl;
                 if fl then <<rds cadr fl; curline!* := caddr fl>>
                  else rds nil;
                 !*echo := techo>>
         else <<eof!* := 1; lprim list(fl,"not open"); error1()>>
   end;

deflist ('((cont endstat) (pause endstat) (retry endstat)),'stat);

flag ('(cont),'ignore);

endmodule;

comment Codemist Standard Lisp based REDUCE "back-end";

% this file defines the system dependent code necessary to run REDUCE
% under Codemist Standard Lisp

comment the following functions, which are referenced in the basic
REDUCE source (rlisp, alg1, alg2, matr and phys) should be defined to
complete the definition of REDUCE:

	bye
        delcp
	error1
	filetype
        mkfil
	orderp
	quit
	random
	seprp
	setpchar.

prototypical descriptions of these functions are as follows;

remprop('bye,'stat);

symbolic procedure bye;
   % returns control to the computer's operating system command level.
   % the current REDUCE job cannot be restarted;
   stop 0;

deflist('((bye endstat)),'stat);

remprop('quit,'stat);

symbolic procedure quit;
   % returns control to the computer's operating system command level.
   % the current REDUCE job cannot be restarted;
   stop 0;

deflist('((quit endstat)),'stat);

symbolic procedure delcp u;
   if u = '!; or u = '!$ then t else nil;

symbolic procedure filetype u; nil;

symbolic procedure mkfil u;
   % converts file descriptor u into valid system filename;
   if idp u then u
    else if stringp u then string2file u
    else if eqcar(u,'quote) then mkfil cadr u
    else if atom u then nil
    else for each z in u collect mkfil z;

symbolic procedure string2file s;
   % converts a string into a valid file name.
   s;

comment the following functions are only referenced if various flags are
set, or the functions are actually defined. they are defined in another
module, which is not needed to build the basic system. the name of the
flag follows the function name, enclosed in parentheses:

        bfquotient!: (bigfloat)
	cedit (?)
	compd (comp)
	edit1	this function provides a link to an editor. however, a
		definition is not necessary, since REDUCE checks to see
		if it has a function value.
	embfn (?)
	ezgcdf (ezgcd)
	factorf (factor)
	load!-module (?)
	prettyprint (defn --- also called by dfprint)
		this function is used in particular for output of RLISP
		expressions in Lisp syntax. if that feature is needed,
		and the prettyprint module is not available, then it
		should be defined as print
        rprint (pret)
	texpt!: (bigfloat)
        texpt!:any (bigfloat)
	time (time) returns elapsed time from some arbitrary initial
		    point in milliseconds;


comment we also need to define a function begin, which acts as the top-
level call to REDUCE, and sets the appropriate variables.  the following is
a minimum definition;

fluid '(!*int !*mode);

global '(crchar!* date!* ifl!* ipl!* ofl!* !*extraecho !*echo);

remflag('(begin),'go);

symbolic procedure begin;
   begin
      !*int := not batchp();
      !*echo := not !*int;
      !*extraecho := t;
      ifl!* := ipl!* := ofl!* := nil;
      if null date!* then go to a;
%     verbos nil;   % leave verbos flag as it had been
      linelength 79;
      prin2 date!*;
      prin2t " ...";
      !*mode := if getd 'addsq then 'algebraic else 'symbolic;
      initreduce();  % resets date!*;
      erfg!* := !*defn := cmsg!* := nil;  % reset error status;
a:    crchar!* := '! ;
      if errorp errorset('(begin1),t,!*backtrace) then go to a;
      if not yesp "are you sure you want to leave REDUCE & enter Lisp?" then
         go to a;
      prin2t "entering Lisp ... ";
      prin2t "type (begin) to re-enter REDUCE, (stop) to exit from Lisp";
 end;

flag('(begin),'go);

comment initial setups for REDUCE;

global '(spare!* statcounter);

spare!* := 10;

symbolic procedure initreduce;        %. initial declarations for REDUCE
  <<statcounter := 0;
    date!* := compress('!" . append(explode2 "Bootstrap RLISP, ",
                  append(explode2 date(),list '!")));
    crbuf!* := crbuflis!* := inputbuflis!* := resultbuflis!* := nil>>;

flag('(reclaim rdf),'opfn);

% flag('(explode2 printc princ ttab unglobal random next!-random!-number), 'lose);

% redo showtime for Codemist Lisp interpretation of gctime vs. time

% remflag('(showtime), 'lose);

symbolic procedure showtime;
   begin scalar x,y;
      x := otime!*;
      otime!* := time();
      x := otime!*-x;
      y := ogctime!*;
      ogctime!* := gctime();
      y := ogctime!* - y;
%     x := x - y;    % not for Codemist Lisp
      terpri();
      prin2 "Time: "; prin2 x; prin2 " ms";
      if y = 0 then return terpri();
      prin2 "  plus GC time: "; prin2 y; prin2 " ms"
   end;

% flag('(lengthc), 'lose);

symbolic procedure typerr(u, v);
% redefined when I get to alg1.red, but smashes before then
% need some support, I guess
 << terpri();
    princ "+++++ typerr: ";
    prin u;
    princ " ";
    prin v;
    terpri();
    error "typerr called"
 >>;

symbolic procedure flush();
  begin
    scalar c;
    while (c:=readch()) neq !$eol!$ and c neq !$eof!$ do nil;
    return nil
  end;

% flag('(cedit1), 'lose);

% redefinition of yesp to improve prompt generation

% remflag('(yesp), 'lose);

symbolic procedure yesp u;
   begin scalar bool,ifl,ofl,x,y,z;
        if ifl!*
          then <<ifl := ifl!* := list(car ifl!*,cadr ifl!*,curline!*);
                 rds nil>>;
        if ofl!* then <<ofl:= ofl!*; wrs nil>>;
        if null !*lessspace then terpri();
        if atom u then prin2 u else lpri u;
        prin2t " (y or n)";
        if null !*lessspace then terpri();
        z := setpchar '!?;
    a:  prin2 '!?;
        x := read();
        % assume an end-of-file is the same as "yes".
        if (y := x eq 'y or x eq !$eof!$) or x eq 'n then go to b;
        if null bool then prin2t "type y or n";
        bool := t;
        go to a;
    b:  setpchar z;
        if ofl then wrs cdr ofl;
        if ifl then rds cadr ifl;
        cursym!* := '!*semicol!*;
        return y
   end;

% support for some more floating point stuff

global '(ft!-tolerance!* e!-value!* pi!-value!*);

ft!-tolerance!* := float 1 / float 1000000000000;

e!-value!* := exp 1.0;

pi!-value!* := 4.0 * atan 1.0;

comment now set the system name;

global '(systemname!*);

systemname!* := 'ccl;


initreduce();

preserve 'begin;


