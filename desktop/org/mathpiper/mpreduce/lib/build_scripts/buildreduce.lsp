% "buildreduce.lsp"
%
% Build a CSL REDUCE.
%
% Depending on how this file is used it will EITHER create a bootstrap
% version of REDUCE or a full and optimised one.
%
% The behaviour is determined by whether the version of CSL used to
% run it has a full complement of functions in the modules u01.c to u60.c.
%
%
%           bootstrapreduce -z buildreduce.lsp -D@srcdir=<DIR>
%                                                                                                                                                                                                        
% Builds a system "bootstrapreduce.img" that does not depend on any
% custom C code. The main use of this slow system is for profiling
% REDUCE and then compiling the hot-spots into C. Once that has been
% done this image is logically unnecessary.
%
%
%           reduce -z buildreduce.lsp -D@srcdir=<DIR>
%
% Here the files u01.c to u60.c and u01.lsp to u60.lsp must already
% have been created, and that the reduce executable has them compiled in.
% The REDUCE source files that are compiled *MUST* be the same as those used
% to create this C code.

% Author: Anthony C. Hearn, Stanley L. Kameny and Arthur Norman

(verbos 3)

(window!-heading "basic CSL")

(setq !*savedef (and (not (memq 'embedded lispsystem!*))
                     (zerop (cdr (assoc 'c!-code lispsystem!*)))))
(make!-special '!*native_code)
(setq !*native_code nil)

(cond ((and (null !*savedef) (null (memq 'embedded lispsystem!*))) (progn

   (de c!:install (name env c!-version !&optional c1)
      (cond
        (c1 (check!-c!-code name env c!-version c1))
        (t (progn
              (put name 'c!-version c!-version)
              (cond (env (prog (v n)
                 (setq v (mkvect (sub1 (length env))))
                 (setq n 0)
            top  (cond
                    ((null env) (progn
                     (put name 'funarg v)
                     (return (symbol!-set!-env name v)))))
                 (putv v n (car env))
                 (setq n (add1 n))
                 (setq env (cdr env))
                 (go top))))
              name))))

   (rdf "$srcdir/cslbuild/generated-c/u01.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u02.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u03.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u04.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u05.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u06.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u07.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u08.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u09.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u10.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u11.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u12.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u13.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u14.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u15.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u16.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u17.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u18.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u19.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u20.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u21.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u22.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u23.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u24.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u25.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u26.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u27.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u28.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u29.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u30.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u31.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u32.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u33.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u34.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u35.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u36.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u37.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u38.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u39.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u40.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u41.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u42.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u43.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u44.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u45.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u46.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u47.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u48.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u49.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u50.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u51.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u52.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u53.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u54.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u55.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u56.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u57.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u58.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u59.lsp")
   (rdf "$srcdir/cslbuild/generated-c/u60.lsp")
    )))

(rdf "$srcdir/fastgets.lsp")
(rdf "$srcdir/compat.lsp")
(rdf "$srcdir/extras.lsp")
(rdf "$srcdir/compiler.lsp")

(compile!-all)

(setq !*comp t)               % It's faster if we compile the boot file.

% Tidy up be deleting any modules that are left over in this image

(dolist (a (library!-members)) (delete!-module a))

% Build fasl files for the compatibility code and the two
% versions of the compiler.

(faslout 'cslcompat)
(rdf "$srcdir/fastgets.lsp")
(rdf "$srcdir/compat.lsp")
(rdf "$srcdir/extras.lsp")
(faslend)

(faslout 'compiler)
(rdf "$srcdir/compiler.lsp")
(faslend)

(setq !*comp t)

(de concat (u v)
    (compress (cons '!" (append (explode2 u)
                                (nconc (explode2 v) (list '!"))))))

(global '(oldchan!*))

(setq prolog_file 'cslprolo)

(setq rend_file 'cslrend)

(setq !*argnochk t)

(setq !*int nil)                    % Prevents input buffer being saved.

(setq !*msg nil)

(window!-heading "bootstrap RLISP")

% This is dervived fron the Standard LISP BOOT File.
% Author: Anthony C. Hearn.
% Copyright (c) 1991 RAND.  All Rights Reserved.

(fluid '(fname!* !*blockp !*lower !*mode))

(global '(oldchan!*))

(global '(!*raise crchar!* cursym!* nxtsym!* ttype!* !$eol!$))

(put '!; 'switch!* '(nil !*semicol!*))

(put '!( 'switch!* '(nil !*lpar!*))

(put '!) 'switch!* '(nil !*rpar!*))

(put '!, 'switch!* '(nil !*comma!*))

(put '!. 'switch!* '(nil cons))

(put '!: 'switch!* '(((!= nil setq)) !*colon!*))

(put '!*comma!* 'infix 1)

(put 'setq 'infix 2)

(put 'cons 'infix 3)

(flag '(!*comma!*) 'nary)

(flag '(!*colon!* !*semicol!* end then else) 'delim)

(put 'begin 'stat 'blockstat)

(put 'if 'stat 'ifstat)

(put 'symbolic 'stat 'procstat)

(de begin2 nil
   (prog nil
      (setq cursym!* '!*semicol!*)
a     (cond
         ((eq cursym!* 'end) (progn (rds oldchan!*) (return nil)))
         (t (prin2 (errorset '(eval (form (xread nil))) t t)) ))
      (go a)))

(de form (u) u)

(de xread (u) (progn (scan) (xread1 u)))

(de xread1 (u)
   (prog (v w x y z z2)
a     (setq z cursym!*)
a1    (cond
         ((or (null (atom z)) (numberp z)) (setq y nil))
         ((flagp z 'delim) (go end1))
         ((eq z '!*lpar!*) (go lparen))
         ((eq z '!*rpar!*) (go end1))
         ((setq y (get z 'infix)) (go infx))
         ((setq y (get z 'stat)) (go stat)))
a3    (setq w (cons z w))
next  (setq z (scan))
      (go a1)
lparen(setq y nil)
      (cond
         ((eq (scan) '!*rpar!*)
            (and w (setq w (cons (list (car w)) (cdr w)))) )
         ((eqcar (setq z (xread1 'paren)) '!*comma!*)
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
            ((and (equal ttype!* 3) (eq x '!()) (rrdls))
            (t x)))) )

(de rrdls nil
   (prog (x r)
a     (setq x (rread))
      (cond
         ((null (equal ttype!* 3)) (go b))
         ((eq x '!)) (return (reverse r)))   % REVERSIP not yet defined.
         ((null (eq x '!.)) (go b)))
      (setq x (rread))
      (token)
      (return (nconc (reverse r) x))
b     (setq r (cons x r))
      (go a)))

(de token nil
   (prog (x y)
      (setq x crchar!*)
a     (cond
         ((seprp x) (go sepr))
         ((digit x) (go number))
         ((liter x) (go letter))
         ((eq x '!%) (go coment))
         ((eq x '!!) (go escape))
         ((eq x '!') (go quote))
         ((eq x '!") (go string)))
      (setq ttype!* 3)
      (cond ((delcp x) (go d)))
      (setq nxtsym!* x)
a1    (setq crchar!* (readch))
      (go c)
escape(setq y (cons x y))
      (setq x (readch))
letter(setq ttype!* 0)
let1  (setq y (cons x y))
      (cond
         ((or (digit (setq x (readch))) (liter x)) (go let1))
         ((eq x '!!) (go escape)))
      (setq nxtsym!* (intern (compress (reverse y))))
b     (setq crchar!* x)
c     (return nxtsym!*)
number(setq ttype!* 2)
num1  (setq y (cons x y))
      (cond ((digit (setq x (readch))) (go num1)))
      (setq nxtsym!* (compress (reverse y)))
      (go b)
quote (setq crchar!* (readch))
      (setq nxtsym!* (list 'quote (rread)))
      (setq ttype!* 4)
      (go c)
string(prog (raise !*lower)
         (setq raise !*raise)
         (setq !*raise nil)
   strinx(setq y (cons x y))
         (cond ((null (eq (setq x (readch)) '!")) (go strinx)))
         (setq y (cons x y))
         (setq nxtsym!* (mkstrng (compress (reverse y))))
         (setq !*raise raise))
      (setq ttype!* 1)
      (go a1)
coment(cond ((null (eq (readch) !$eol!$)) (go coment)))
sepr  (setq x (readch))
      (go a)
d     (setq nxtsym!* x)
      (setq crchar!* '! )
      (go c)))

(setq crchar!* '! )

(de delcp (u) (or (eq u '!;) (eq u '!$)))

(de mkstrng (u) u)

(de seprp (u) (or (eq u blank) (eq u tab) (eq u !$eol!$)))

(de scan nil
   (prog (x y)
      (cond ((null (eq cursym!* '!*semicol!*)) (go b)))
a     (setq nxtsym!* (token))
b     (cond
         ((or (null (atom nxtsym!*)) (numberp nxtsym!*)) (go l))
         ((and (setq x (get nxtsym!* 'newnam)) (setq nxtsym!* x))
            (go b))
         ((eq nxtsym!* 'comment) (go comm))
         ((and
             (eq nxtsym!* '!')
             (setq cursym!* (list 'quote (rread))))
            (go l1))
         ((null (setq x (get nxtsym!* 'switch!*))) (go l))
         ((eq (cadr x) '!*semicol!*)
            (return (setq cursym!* (cadr x)))) )
sw1   (setq nxtsym!* (token))
      (cond
         ((or
             (null (car x))
             (null (setq y (assoc nxtsym!* (car x)))) )
            (return (setq cursym!* (cadr x)))) )
      (setq x (cdr y))
      (go sw1)
comm  (cond ((eq (readch) '!;) (setq crchar!* '! )) (t (go comm)))
      (go a)
l     (setq cursym!*
         (cond
            ((null (eqcar nxtsym!* 'string)) nxtsym!*)
            (t (cons 'quote (cdr nxtsym!*)))) )
l1    (setq nxtsym!* (token))
      (return cursym!*)))

(de ifstat nil
   (prog (condx condit)
a     (setq condx (xread t))
      (setq condit (nconc condit (list (list condx (xread t)))) )
      (cond
         ((null (eq cursym!* 'else)) (go b))
         ((eq (scan) 'if) (go a))
         (t (setq condit
               (nconc condit (list (list t (xread1 t)))) )))
b     (return (cons 'cond condit))))

(de procstat nil
   (prog (x y)
      (cond ((eq cursym!* 'symbolic) (scan)))
      (cond
         ((eq cursym!* '!*semicol!*)
            (return (null (setq !*mode 'symbolic)))) )
      (setq fname!* (scan))
      (cond ((atom (setq x (xread1 nil))) (setq x (list x))))
      (setq y (xread nil))
      (cond ((flagp (car x) 'lose) (return nil)))
      (putd (car x) 'expr (list 'lambda (cdr x) y))
      (setq fname!* nil)
      (return (list 'quote (car x)))) )

(de blockstat nil
   (prog (x hold varlis !*blockp)
a0    (setq !*blockp t)
      (scan)
      (cond
         ((null (or (eq cursym!* 'integer) (eq cursym!* 'scalar)))
            (go a)))
      (setq x (xread nil))
      (setq varlis
         (nconc
            (cond ((eqcar x '!*comma!*) (cdr x)) (t (list x)))
            varlis))
      (go a0)
a     (setq hold (nconc hold (list (xread1 nil))))
      (setq x cursym!*)
      (scan)
      (cond ((not (eq x 'end)) (go a)))
      (return (mkprog varlis hold))))

(de mkprog (u v) (cons 'prog (cons u v)))

(de gostat nil
   (prog (x) (scan) (setq x (scan)) (scan) (return (list 'go x))))

(put 'go 'stat 'gostat)

(de rlis nil
   (prog (x)
      (setq x cursym!*)
      (return (list x (list 'quote (list (xread t)))))))

(de endstat nil (prog (x) (setq x cursym!*) (scan) (return (list x))))

% Now we have just enough to be able to start to express ourselves in
% (a subset of) rlisp.

(begin2)

!@reduce := concat(!@srcdir, "/../..");

rds(xxx := open("$reduce/packages/support/build.red",'input));

(close xxx)

(load!-package!-sources prolog_file 'support)

(load!-package!-sources 'rlisp 'rlisp)

(load!-package!-sources 'smacros 'support)

(load!-package!-sources rend_file 'support)

(load!-package!-sources 'poly 'poly)

(load!-package!-sources 'alg 'alg)

(load!-package!-sources 'arith 'arith)  %  Needed by roots, specfn*, (psl).

(load!-package!-sources 'entry 'support)

(load!-package!-sources 'remake 'support)

(setq !*comp nil)



(begin)

symbolic;

!#if (and (not (memq 'embedded lispsystem!*)) (not !*savedef))

faslout 'user;

%
% The "user" module is only useful when building a full system, since
% in the bootstrap the files u01.lsp to u60.lsp will probably not exist
% and it is CERTAIN that they are not useful.
%

if modulep 'cslcompat then load!-module 'cslcompat;


symbolic procedure c!:install(name, env, c!-version, !&optional, c1);
  begin
    scalar v, n;
    if c1 then return check!-c!-code(name, env, c!-version, c1);
    put(name, 'c!-version, c!-version);
    if null env then return name;
    v := mkvect sub1 length env;
    n := 0;
    while env do <<
      putv(v, n, car env);
      n := n + 1;
      env := cdr env >>;
% I only instate the environment if there is nothing useful there at
% present. Actually this is even stronger. When a built-in function is
% set up it gets NIL in its environment cell by default. Things that are
% not defined at all have themselves there.
    if symbol!-env name = nil then symbol!-set!-env(name, v);
    put(name, 'funarg, v);
    return name;
  end;

rdf "$srcdir/../../cslbuild/generated-c/u01.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u02.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u03.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u04.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u05.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u06.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u07.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u08.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u09.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u10.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u11.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u12.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u13.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u14.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u15.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u16.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u17.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u18.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u19.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u20.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u21.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u22.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u23.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u24.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u25.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u26.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u27.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u28.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u29.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u30.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u31.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u32.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u33.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u34.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u35.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u36.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u37.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u38.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u39.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u40.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u41.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u42.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u43.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u44.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u45.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u46.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u47.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u48.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u49.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u50.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u51.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u52.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u53.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u54.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u55.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u56.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u57.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u58.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u59.lsp"$
rdf "$srcdir/../../cslbuild/generated-c/u60.lsp"$

if modulep 'smacros then load!-module 'smacros;

faslend;
!#endif

faslout 'remake;

!#if (and (not (memq 'embedded lispsystem!*)) (not !*savedef))

load!-module "user";

!#endif

!@reduce := concat(!@srcdir, "/../..");

in "$reduce/packages/support/remake.red"$

global '(reduce_base_modules reduce_extra_modules reduce_test_cases);
 
symbolic procedure get_configuration_data();
% Read data from a configuration file that lists the modules that must
% be processed.  NOTE that this and the next few funtions will ONLY
% work properly if REDUCE had been started up with the correct
% working directory. This is (just about) acceptable because these are
% system maintainance functions rather than generally flexible things
% for arbitrary use.
  begin
    scalar i, w, e;
% Configuration information is held in a file called something like
% "package.map".                                                              
    if boundp 'microreduce and symbol!-value 'microreduce then
        i := "$srcdir/../../packages/micropackage.map"
    else if boundp 'minireduce and symbol!-value 'minireduce then
        i := "$srcdir/../../packages/minipackage.map"
    else i := "$srcdir/../../packages/package.map";
    i := open(i, 'input);
    i := rds i;
    e := !*echo;
    !*echo := nil;
    w := read();
    !*echo := e;
    i := rds i;
    close i;
    reduce_base_modules :=
      for each x in w conc
         if member('core, cddr x) and
            member('csl, cddr x) then list car x else nil;
    reduce_extra_modules :=
      for each x in w conc
         if not member('core, cddr x) and
            member('csl, cddr x) then list car x else nil;
    reduce_test_cases :=
      for each x in w conc
         if member('test, cddr x) and
            member('csl, cddr x) then list car x else nil;
    for each x in w do
       if member('csl, cddr x) then put(car x, 'folder, cadr x);
 %  princ "reduce_base_modules: "; print reduce_base_modules;
 %  princ "reduce_extra_modules: "; print reduce_extra_modules;
 %  princ "reduce_test_cases: "; print reduce_test_cases;
    return;
  end;

symbolic procedure build_reduce_modules names;
  begin
    scalar w;
!#if !*savedef
    !*savedef := t;
!#else
    !*savedef := nil;
!#endif
    make!-special '!*native_code;
    !*native_code := nil;
    get_configuration_data();
    
    
    window!-heading list!-to!-string explodec car names;
!#if !*savedef
% When building the bootstrap version I want to record what switches
% get declared...
    if not getd 'original!-switch then <<
       w := getd 'switch;
       putd('original!-switch, car w, cdr w);
       putd('switch, 'expr, 
          '(lambda (x) 
              (dolist (y x) (princ "+++ Declaring a switch: ") (print y)) 
              (original!-switch x))) >>;
!#endif
    package!-remake car names;
    if null (names := cdr names) then <<
        printc "Recompilation complete";
        window!-heading  "Recompilation complete" >>;
!#if (or !*savedef (memq 'embedded lispsystem!*))
    if null names then restart!-csl 'begin
    else restart!-csl('(remake build_reduce_modules), names)
!#else
    if null names then restart!-csl '(user begin)
    else restart!-csl('(remake build_reduce_modules), names)
!#endif
  end;

symbolic procedure test_a_package names;
  begin
    scalar packge, logname, logtmp, logfile, start_time, start_gctime, gt;
    scalar redef, quitfn, oll, rr;
    princ "TESTING: "; print car names;
    window!-heading list!-to!-string explodec car names;
    !*backtrace := nil;
    !*errcont := t;
    !*extraecho := t;    % Ensure standard environment for the test...
    !*int := nil;        % ... so that results are predictable.
    packge := car names;
    verbos nil;
%    load!-latest!-patches();
% Normally logs from testing go in testlogs/name.rlg, however you may
% may sometimes want to put them somewhere else. If you do then launch reduce
% along the lines
%    reduce -D@log="mylogs" ...
% and ensure that <top>/mylogs exists.
    if boundp '!@log and stringp symbol!-value '!@log then
        logname := symbol!-value '!@log
    else logname := "testlogs";
    logname := concat(logname, "/");
    logtmp  := concat(logname, concat(car names, ".tmp"));
    logname := concat(logname, concat(car names,".rlg"));
    logfile := open(logtmp, 'output);
    get_configuration_data();
    begin
       scalar !*terminal!-io!*, !*standard!-output!*, !*error!-output!*,
              !*trace!-output!*, !*debug!-io!*, !*query!-io!*, !*errcont,
              outputhandler!*;
       !*terminal!-io!* := !*standard!-output!* := !*error!-output!* := logfile;
       !*trace!-output!* := !*debug!-io!* := !*query!-io!* := logfile;
       oll := linelength 80;
       princ date(); princ " run on "; printc cdr assoc('name, lispsystem!*);
       load!-package packge;
       if get(packge,'folder) then packge := get(packge,'folder);
       packge := concat("$srcdir/../../packages/",
                   concat(packge,
                     concat("/",
                       concat(car names,".tst"))));
       redef := !*redefmsg;
       !*redefmsg := nil;
       quitfn := getd 'quit;
% At least at one stage at least one test file ends in "quit;" rather than
% "end;" and the normal effect would be that this leads it to cancel
% all execution instantly. To avoid that I will undefine the function
% "quit", but restore it after the test. I reset !*redefmsg to avoid getting
% messages about this. I redefined quit to something (specifically "posn")
% that does not need an argument and that is "harmless".
       remd 'quit;
       putd('quit, 'expr, 'posn);
       start_time := time();
       start_gctime := gctime();
       !*mode := 'algebraic;
       !*extraecho := t;    % Ensure standard environment for the test...
       !*int := nil;        % ... so that results are predictable.
       !*errcont := t;
% resource!-limit is a special feature in CSL so that potentially wild
% code can be run with it being stopped harshly if it gets stuck.
% The first argument is an expression to evaluate. The next 4 are
%    a time limit, in seconds
%    a "cons" limit, in megaconses
%    a limit on the number of thousands of I/O bytes that can be
%            performed, with both reading and printing counted
%    a limit on the number of Lisp-level errors that can be raised.
%            note that that can be large if errorset is used to trap them.
%
% If a limit is specified as a negative value (typically -1) then that
% resource is not applied.
% The first 3 limits are applied in an APPROXIMATE way, and the first
% is seriously sensitive the the speed of the computer you are running
% on, so should be used with real care. At the end the return value
% is atomic if a limit expired, otherwise ncons of the regular value.
% A global variable *resources* should end up a list of 4 values
% showing the usage in each category.

% The settings here are intended to be fairly conservative...
%  Time:   On an Intel Q6600 CPU the longest test runs in under 20 seconds,
%          so allowing 3 minutes gives almost a factor of 10 slack. If
%          many people are running slow(ish) machines still I can increase
%          the limit.
%  Space:  The amount of space used ought to be pretty independent of
%          the computer used. Measuring on 32 and 64-bit systems will
%          give minor differences. But the limit given here seems to allow
%          all the current tests to run with a factor of 2 headroom
%          in case the test-scripts are updated.
%  IO:     The "crack" package has code in it that checkpoints its state
%          to disc periodically, and tests that activate that use amazingly
%          more IO than the others. The limit at 10 Mbytes suits the
%          relevant current tests. If a broken package leads to a test
%          script looping this then means that the resulting log file is no
%          larger than (about) 10 Mbytes, which is ugly but managable.
%  Errors: Some REDUCE packages make extensive use of errorset and
%          predictable use of "error" (for lack of use of catch and throw,
%          usually). So I do not constrain errors here. But if things were
%          ever such that no errors were expected I could enforce that
%          condition here.

       rr := resource!-limit(list('in_list1, mkquote packge, t),
                             300,  % allow 5 minutes per test
                             200,  % allow 200 megaconses
                             10000,% allow ten megabytes of I/O
                             -1);  % Do not limit Lisp-level errors at all 
       erfg!* := nil;
       terpri();
       putd('quit, car quitfn, cdr quitfn);
       !*redefmsg := redef;
       terpri();
       prin2 "Time for test: ";
       gt := time() - start_time;
% I ensure that the reported time is at least 1 millisecond.
       if gt = 0 then gt := 1;
       prin2 gt;
       prin2 " ms";
       if (gt := gctime() - start_gctime) > 0 then <<
           prin2 ", plus GC time: ";
           prin2 gt;
           prin2 " ms" >>;
       terpri();
% Temp while I watch things
       if atom rr then printc "+++++ Error: Resource limit exceeded";
       princ "@@@@@ Resources used: "; print !*resources!*;
       linelength oll
    end;
    close logfile;
    delete!-file logname;
    rename!-file(logtmp, logname);
    names := cdr names;
    if null names then <<
        printc "Testing complete";
        window!-heading "Testing complete";
        restart!-csl t >>
    else restart!-csl('(remake test_a_package), names)
  end;

symbolic procedure report_incomplete_tests names;
  begin
% Displays information about what "complete_tests" would do
    scalar packge, tfile, logname;
    scalar date1, date2, date3;
    get_configuration_data();
    for each packge in names do <<
       tfile := packge;
       if get(packge,'folder) then tfile := get(packge,'folder);
       tfile := concat("$srcdir/../../packages/",
                   concat(tfile,
                      concat("/",
                        concat(packge,".tst"))));
       if boundp '!@log and stringp symbol!-value '!@log then
           logname := symbol!-value '!@log
       else logname := "testlogs";
       logname := concat(logname, concat("/", concat(packge,".rlg")));
       date1 := filedate "reduce.img";
       date2 := filedate tfile;
       date3 := filedate logname;
       if null date1 then date1 := date();
       if null date2 then date2 := date();
       if null date3 or
          datelessp(date3, date1) or datelessp(date3, date2) then <<
             princ "NEED TO TEST: "; print packge >> >>
  end;

symbolic procedure complete_tests names;
  begin
% Just like the previous testing code except that logs that are already up
% to date are not re-generated.
    scalar packge, tfile, logname, logfile, logtmp,
           start_time, start_gctime, gt, rr;
    scalar date1, date2, date3, oll;
    !*backtrace := nil;
    !*errcont := t;
    !*extraecho := t;    % Ensure standard environment for the test...
    !*int := nil;        % ... so that results are predictable.
    verbos nil;
    get_configuraion_data();
top:
    tfile := packge := car names;
    if get(tfile,'folder) then tfile := get(tfile,'folder);
    tfile := concat("$srcdir/../../packages/",
                concat(tfile,
                  concat("/",
                    concat(packge,".tst"))));
    if boundp '!@log and stringp symbol!-value '!@log then
        logname := symbol!-value '!@log
    else logname := "testlogs";
    logname := concat(logname, "/");
    logtmp  := concat(logname, concat(packge, ".tmp"));
    logname := concat(logname, concat(packge, ".rlg"));
    date1 := filedate "reduce.img";
    date2 := filedate tfile;
    date3 := filedate logname;
    if null date1 then date1 := date();
    if null date2 then date2 := date();
    if null date3 or
       datelessp(date3, date1) or datelessp(date3, date2) then <<
       princ "TESTING: "; print packge;
       window!-heading list!-to!-string explodec packge;
       logfile := open(logtmp, 'output);
       start_time := time();
       start_gctime := gctime();
       begin
          scalar !*terminal!-io!*, !*standard!-output!*, !*error!-output!*,
                 !*trace!-output!*, !*debug!-io!*, !*query!-io!*, !*errcont,
                 outputhandler!*, redef, quitfn;
          !*terminal!-io!* := !*standard!-output!* := !*error!-output!* := logfile;
          !*trace!-output!* := !*debug!-io!* := !*query!-io!* := logfile;
          oll := linelength 80;
          princ date(); princ " run on ";
          printc cdr assoc('name, lispsystem!*);
          load!-package packge;
          !*mode := 'algebraic;
          !*extraecho := t;    % Ensure standard environment for the test...
          !*int := nil;        % ... so that results are predictable.
          redef := !*redefmsg;
          !*redefmsg := nil;
          quitfn := getd 'quit;
          remd 'quit;
          putd('quit, 'expr, 'posn);
          !*errcont := t;
          rr := resource!-limit(list('in_list1, mkquote tfile, t),
                                300,  % allow 5 minutes per test
                                200,  % allow 200 megaconses
                                10000,% allow ten megabytes of I/O
                                -1);  % Do not limit Lisp-level errors at all 
          erfg!* := nil;
          terpri();
          putd('quit, car quitfn, cdr quitfn);
          !*redefmsg := redef;
          terpri();
          prin2 "Time for test: ";
          gt := time() - start_time;
          if gt = 0 then gt := 1;
          prin2 gt;
          prin2 " ms";
          if (gt := gctime() - start_gctime) > 0 then <<
              prin2 ", plus GC time: ";
              prin2 gt;
              prin2 " ms" >>;
          if atom rr then printc "+++++ Error: Resource limit exceeded";
          princ "@@@@@ Resources used: "; print !*resources!*;
          terpri();
          linelength oll
       end;
       close logfile;
       delete!-file logname;
       rename!-file(logtmp, logname) >>
    else if cdr names then <<
       names := cdr names;
       go to top >>;
    names := cdr names;
    if null names then restart!-csl t
    else restart!-csl('(remake complete_tests), names)
  end;

symbolic procedure profile_compare_fn(p, q);
   (float caddr p/float cadr p) < (float caddr q/float cadr q);

%
% This function runs a test file and sorts out what the top 350
% functions in it. It appends their names to "profile.dat".
%

% I need to talk a little about the interaction between profiling and
% patching.  Well firstly I arrange that whenever I run a profiling job
% I rebuild REDUCE with the latest paches. This may involve re-compiling
% the patches.red source.  Thus when a test is run the current patches
% will be in place. Patched functions are first defined with funny names
% (including a hash based on their definition) and then copied into place
% when a package is loaded. However MAPSTORE and the CSL instrumentation
% attributes their cost to the hash-extended name even though the
% functions may have been called via the simple one. Thus in the face
% of patches one can expect the profile data to refer to some names that
% are long and curious looking. Throughout all this I assume that there will
% never be embarassing collisions in my hash functions.

symbolic procedure profile_a_package names;
  begin
    scalar packge, oll, w, w1, w2, quitfn, !*errcont, rr;
    princ "PROFILING: "; print car names;
    !*backtrace := nil;
    !*errcont := t;
    !*int := nil;
    packge := car names;
    verbos nil;
    load!-package packge;
    get_configuration_data();
    if get(packge,'folder) then packge := get(packge,'folder);
    packge := concat("$srcdir/../../packages/",
                concat(packge,
                  concat("/",
                    concat(car names,".tst"))));
    oll := linelength 80;
    !*mode := 'algebraic;
    window!-heading list!-to!-string explodec car names;
    quitfn := getd 'quit;
    remd 'quit;
    putd('quit, 'expr, 'posn);
    mapstore 4;  % reset counts;
    !*errcont := t;
% I try hard to arrange that even if the test fails I can continue and that
% input & output file selection is not messed up for me.
    w := wrs nil;   w1 := rds nil;
    wrs w;          rds w1;
    rr := resource!-limit(list('errorset,
                               mkquote list('in_list1, mkquote packge, t),
                               nil, nil),
                          300,  % allow 5 minutes per test
                          200,  % allow 200 megaconses
                          10000,% allow ten megabytes of I/O
                          -1);  % Do not limit Lisp-level errors at all 
    wrs w;          rds w1;
    erfg!* := nil;
    terpri();
    putd('quit, car quitfn, cdr quitfn);
    w := sort(mapstore 2, function profile_compare_fn);
    w1 := nil;
    while w do <<
        w2 := get(caar w, '!*savedef);
%       if eqcar(w2, 'lambda) then <<
%           princ "md60: "; print (caar w . cdr w2);
%           princ "= "; print md60 (caar w . cdr w2) >>;
        if eqcar(w2, 'lambda) then w1 := (caar w . md60 (caar w . cdr w2) .
                                          cadar w . caddar w) . w1;
        w := cdr w >>;
    w := w1;
    % I collect the top 350 functions as used by each test, not because all
    % that many will be wanted but because I might as well record plenty
    % of information here and discard unwanted parts later on.
    for i := 1:349 do if w1 then w1 := cdr w1;
    if w1 then rplacd(w1, nil);
    % princ "MODULE "; prin car names; princ " suggests ";
    % print for each z in w collect car z;
    w1 := open("profile.dat", 'append);
    w1 := wrs w1;
    linelength 80;
    if atom rr then printc "% +++++ Error: Resource limit exceeded";
    princ "% @@@@@ Resources used: "; print !*resources!*;
    princ "("; prin car names; terpri();
    for each n in w do <<
        princ "  ("; prin car n; princ " ";
        if posn() > 30 then << terpri(); ttab 30 >>;
        prin cadr n;
        % I also display the counts just to help me debug & for interest.
        princ " "; prin caddr n; princ " "; princ cdddr n;
        printc ")" >>;
    printc "  )";
    terpri();
    close wrs w1;
    linelength oll;
    names := cdr names;
    if null names then <<
        printc "Profiling complete";
        window!-heading "Profiling complete";
        restart!-csl t >>
    else restart!-csl('(remake profile_a_package), names)
  end;

symbolic procedure trim_prefix(a, b);
  begin
    while a and b and car a = car b do <<
      a := cdr a;
      b := cdr b >>;
    if null a then return b
    else return nil
  end;

fluid '(time_info);

symbolic procedure read_file f1;
  begin
% I take the view that I can afford to read the whole of a file into
% memory at the start of processing. This makes life easier for me
% and the REDUCE log files are small compared with current main memory sizes.
    scalar r, w, w1, n, x;
    scalar p1, p2, p3, p4, p5, p6, p7;
% To make comparisons between my CSL logs and some of the Hearn "reference
% logs", which are created using a different script, I will discard
% lines that match certain patterns!  Note that if the reference logs change
% the particular tests I perform here could become out of date! Also if any
% legitimate test output happened to match one of the following strings
% I would lose out slightly.
    p1 := explodec "REDUCE 3.8,";
    p2 := explodec "1: 1:";
    p3 := explodec "2: 2: 2:";
    p4 := explodec "3: 3: ";    % a prefix to first real line of output.
    p5 := explodec "4: 4: 4:";
    p6 := explodec "5: 5:";
    p7 := explodec "Quittin";   % nb left so that the "g" remains!
                                % this is so that the match is detected.
    r := nil;
    n := 0;
    while not ((w := readline f1) = !$eof!$) do <<
       w1 := explodec w;
       if x := trim_prefix(p4, w1) then
           r := ((n := n + 1) . list!-to!-string x) . r
       else if trim_prefix(p1, w1) or
               trim_prefix(p2, w1) or
               trim_prefix(p3, w1) or
               trim_prefix(p5, w1) or
               trim_prefix(p6, w1) or
               trim_prefix(p7, w1) then nil
       else r := ((n := n + 1) . w) . r >>;
    w := r;
% The text scanned for here is expected to match that generated by the
% test script. I locate the last match in a file, extract the numbers
% and eventually write them to testlogs/times.log
    n := explodec "Time for test:";
    while w and null (x := trim_prefix(n, explodec cdar w)) do w := cdr w;
    if null w then <<
       time_info := nil;
       return reversip r >>;
    while eqcar(x, '! ) do x := cdr x;
    w := n := nil;
    while digit car x do << w := car x . w; x := cdr x >>;
    while eqcar(x, '! ) do x := cdr x;
    if x := trim_prefix(explodec "ms, plus GC time:", x) then <<
        while eqcar(x, '! ) do x := cdr x;
        while digit car x do << n := car x . n; x := cdr x >> >>;
    if null w then w := '(!0);
    if null n then n := '(!0);
    time_info := compress reverse w . compress reverse n;
    return reversip r;
  end;

symbolic procedure roughly_equal(a, b);
  begin
% a and b are strings repesenting lines of text. I want to test if they
% match subject to some floating point slop.
    scalar wa, wb, adot, bdot;
    if a = b then return t;
    a := explodec a;
    b := explodec b;
top:
% First deal with end of line matters.
    if null a and null b then return t
    else if null a or null b then return nil;
% next split off any bits of a and b up to a digit
    wa := wb := nil;
    while a and not digit car a do <<
       wa := car a . wa;
       a := cdr a >>;
    while b and not digit car b do <<
       wb := car b . wb;
       b := cdr b >>;
    if not (wa = wb) then return nil;
% now both a and b start with digits. I will seek a chunk of the
% form nnn.mmmE+xxx where E<sign>xxx is optional...
% Note that any leading sign on the float has been checked already!
    wa := wb := nil;
    adot := bdot := nil;
    while a and digit car a do <<
       wa := car a . wa;
       a := cdr a >>;
    if eqcar(a, '!.) then <<
       adot := t;
       wa := car a . wa;
       a := cdr a >>;
    while a and digit car a do <<
       wa := car a . wa;
       a := cdr a >>;
    if eqcar(a, '!e) or eqcar(a, '!E) then <<
       adot := t;
       wa := car a . wa;
       a := cdr a;
       if eqcar(a, '!+) or eqcar(a, '!-) then <<
          wa := car a . wa;
          a := cdr a >>;
       while a and digit car a do <<
          wa := car a . wa;
          a := cdr a >> >>;
% Now all the same to grab a float from b
    while b and digit car b do <<
       wb := car b . wb;
       b := cdr b >>;
    if eqcar(b, '!.) then <<
       bdot := t;
       wb := car b . wb;
       b := cdr b >>;
    while b and digit car b do <<
       wb := car b . wb;
       b := cdr b >>;
    if eqcar(b, '!e) or eqcar(b, '!E) then <<
       bdot := t;
       wb := car b . wb;
       b := cdr b;
       if eqcar(b, '!+) or eqcar(b, '!-) then <<
          wb := car b . wb;
          b := cdr b >>;
       while b and digit car b do <<
          wb := car b . wb;
          b := cdr b >> >>;
% Now one possibility is that I had an integer not a float,
% and in that case I want an exact match
    if not adot or not bdot then <<
       if wa = wb then goto top
       else return nil >>;
    if wa = wb then goto top;   % textual match on floating point values
    wa := compress reversip wa;
    wb := compress reversip wb;
    if fixp wa then wa := float wa;
    if fixp wb then wb := float wb;
    if not (floatp wa and floatp wb) then return nil; % messed up somehow!
    if wa = wb then goto top;
% now the crucial approximate floating point test - note that both numbers
% are positive, but that they may be extreme in range.
% As a cop-out I am going to insist that if values are either very very big
% or very very small that they match as text.
    if wa > 1.0e100 or wb > 1.0e100 then return nil;
    if wa < 1.0e-100 or wb < 1.0e-100 then return nil;
    wa := (wa - wb)/(wa + wb);
    if wa < 0 then wa := -wa;
    if wa > 0.0001 then return nil; % pretty crude!
    goto top
  end;

symbolic procedure in_sync(d1, n1, d2, n2);
  begin
    for i := 1:n1 do if d1 then <<  % skip n1 lines from d1
       d1 := cdr d1 >>;
    for i := 1:n2 do if d2 then <<  % skip n2 lines from d2
       d2 := cdr d2 >>;
% If one is ended but the other is not then we do not have a match. If
% both are ended we do have one.
    if null d1 then return null d2
    else if null d2 then return nil;
% Here I insist on 3 lines that agree before I count a match as
% having been re-established.
    if not roughly_equal(cdar d1, cdar d2) then return nil;
    d1 := cdr d1; d2 := cdr d2;
    if null d1 then return null d2
    else if null d2 then return nil;
    if not roughly_equal(cdar d1, cdar d2) then return nil;
    d1 := cdr d1; d2 := cdr d2;
    if null d1 then return null d2
    else if null d2 then return nil;
    if not roughly_equal(cdar d1, cdar d2) then return nil;
    d1 := cdr d1; d2 := cdr d2;
    if null d1 then return null d2
    else if null d2 then return nil
    else return t
  end;

fluid '(time_data time_ratio gc_time_ratio log_count);

symbolic procedure prinright(x, w);
  begin
    scalar xx, xl;
    xx := explodec x;
    xl := length xx;
    while w > xl do << princ " "; xl := xl + 1 >>;
    princ x;
  end;

symbolic procedure file_compare(f1, f2, name);
  begin
    scalar i, j, d1, d2, t1, gt1, t2, gt2, time_info;
    d1 := read_file f1;
    if null time_info then t1 := gt1 := 0
    else << t1 := car time_info; gt1 := cdr time_info >>;
    d2 := read_file f2;
    if null time_info then t2 := gt2 := 0
    else << t2 := car time_info; gt2 := cdr time_info >>;
    i := wrs time_data;
    j := set!-print!-precision 3;
    prin name;
    ttab 17;
    if zerop t1 then princ "     ---"
    else << prinright(t1, 8);
% Tag the time with an asterisk if it will not participate in the
% eventual overall timing report.
            if t1<=200 then princ "*";
            ttab 30; prinright(gt1, 8) >>;
    ttab 40;
    if zerop t2 then princ "     ---"
    else << prinright(t2, 9);
            if t2<=200 then princ "*";
            ttab 50; prinright(gt2, 8) >>;
    ttab 60;
    if zerop t1 or zerop t2 then princ "     ***       ***"
    else begin
       scalar r1, gr1, w;
       r1 := float t1 / float t2;
       gr1 := float (t1+gt1)/float (t2+gt2);
% I will only use tests where the time taken was over 200ms in my eventual
% composite summary of timings, since measurement accuracy can leave the
% really short tests pretty meaningless.
       if t1 > 200 and t2 > 200 then <<
% But I will go further than that and give less weight to any test whose time
% is under 1 second, so that the cut-off is gradual rather than abrupt.
          w := min(t1, t2);
% This means that if w (the smaller time) = 200 then then 
% the test does not contribute to the average, while if w>=1000
% it contributes fully.
          if w < 1000.0 then w := (w - 200.0)/800.0
          else w := 1.0;
          time_ratio := time_ratio * expt(r1, w);
          gc_time_ratio := gc_time_ratio * expt(gr1, w);
          log_count := log_count + w >>;
       princ r1;
       ttab 70;
       princ gr1;
       end;
    terpri();
    set!-print!-precision j;
    wrs i;
% The next segment of code is a version of "diff" to report ways in which
% reference and recent log files match or diverge.
% I can not see a neat way to get a "structured" control structure
% here easily.  Ah well, drop back to GOTO statements!
top:
    if null d1 then <<      % end of one file
       if d2 then terpri();
       i := 0;
       while d2 and i < 20 do <<
          princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
          d2 := cdr d2;
          i := i + 1 >>;
       if d2 then printc "...";
       return >>;
    if null d2 then <<      % end of other file
       i := 0;
       while d1 and i < 20 do <<
          princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
          d1 := cdr d1;
          i := i + 1 >>;
       if d1 then printc "...";
       return >>;
% The test "roughly_equal" compares allowing some tolerance on floating
% point values. This is because REDUCE uses platform libraries for
% floating point elementary functions and printing, so small differences
% are expected. This is perhaps uncomfortable, but is part of reality, and
% the test here makes comparison output much more useful in that the
% differences shown up are better limited towards "real" ones.
    if roughly_equal(cdar d1, cdar d2) then <<
       d1 := cdr d1;
       d2 := cdr d2;
       go to top >>;
% I will first see if there are just a few blank lines inserted into
% one or other file. This special case is addressed here because it
% appears more common a possibility than I had expected.
    if cdar d1 = "" and cdr d1 and roughly_equal(cdadr d1, cdar d2) then <<
       princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
       d1 := cdr d1;
       go to top >>
    else if cdar d1 = "" and cdr d1 and cdadr d1 = "" and cddr d1 and
       roughly_equal(cdaddr d1, cdar d2) then <<
       princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
       d1 := cdr d1;
       princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
       d1 := cdr d1;
       go to top >>
    else if cdar d2 = "" and cdr d2 and
       roughly_equal(cdadr d2, cdar d1) then <<
       princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
       d2 := cdr d2;
       go to top >>
    else if cdar d2 = "" and cdr d2 and cdadr d2 = "" and cddr d2 and
       roughly_equal(cdaddr d2, cdar d1) then <<
       princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
       d2 := cdr d2;
       princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
       d2 := cdr d2;
       go to top >>;
    i := 1;
seek_rematch:
    j := 0;
inner:
    if in_sync(d1, i, d2, j) then <<
       terpri();
       for k := 1:i do <<
          princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
          d1 := cdr d1 >>;
       for k := 1:j do <<
          princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
          d2 := cdr d2 >>;
% Should be in step again here.
       if null d1 then return
       else go to top >>;
    j := j + 1;
    i := i - 1;
    if i >= 0 then go to inner;
    i := j;
% I am prepared to seek 80 lines ahead on each side before I give up.
% The number 80 is pretty much arbitrary.
    if i < 80 then goto seek_rematch;
    terpri();
    i := 0;
    while d2 and i < 20 do <<
       princ "REF "; princ caar d2; princ ":"; ttab 10; printc cdar d2;
       d2 := cdr d2;
       i := i+1 >>;
    if d2 then printc "...";
    i := 0;
    while d1 and i < 20 do <<
       princ "NEW "; princ caar d1; princ ":"; ttab 10; printc cdar d1;
       d1 := cdr d1;
       i := i+1 >>;
    if d1 then printc "...";
    printc "Comparison failed."
  end;

fluid '(which_module);

symbolic procedure check_a_package;
  begin
    scalar oll, names, p1, logname, mylogname, mylog, reflogname, reflog,
           time_data, time_ratio, gc_time_ratio, log_count;
    get_configuration_data();
    if boundp 'which_module and symbol!-value 'which_module and
       not (symbol!-value 'which_module = "") then <<
       names := compress explodec symbol!-value 'which_module;
       if member(names, reduce_test_cases) then names := list names
       else error(0, list("unknown module to check", which_module)) >>
    else names := reduce_test_cases;
% I write a summary of timing information into csllogs/times.log
    time_data := open("testlogs/times.log", 'output);
    p1 := wrs time_data;
    princ "MODULE";
    ttab 21; princ "Local";     ttab 32; princ "(GC)";
    ttab 40; princ "Reference"; ttab 52; princ "(GC)";
    ttab 55; princ "Ratio";     ttab 65; printc "inc GC";
    wrs p1;
    terpri();
    oll := linelength 100;
    printc "=== Comparison results ===";
    time_ratio := gc_time_ratio := 1.0; log_count := 0.0;
    for each packge in names do <<
       terpri();
       princ "CHECKING: "; print packge;
       if boundp '!@log and stringp symbol!-value '!@log then
           logname := symbol!-value '!@log
       else logname := "testlogs";
       mylogname := concat(logname, concat("/", concat(packge, ".rlg")));
       if get(packge,'folder) then p1 := get(packge,'folder)
       else p1 := packge;
       reflogname := concat("$srcdir/../../packages/",
                       concat(p1,
                         concat("/",
                           concat(packge,".rlg"))));
       mylog := errorset(list('open, mkquote mylogname, ''input), nil, nil);
       reflog := errorset(list('open, mkquote reflogname, ''input), nil, nil);
       if errorp mylog then <<
          if not errorp reflog then close car reflog;
          princ "No current log in "; print mylogname >>
       else if errorp reflog then <<
          close car mylog;
          princ "No reference log in "; print reflogname >>
       else <<
          princ "LOGS: "; princ mylogname; princ " "; printc reflogname;
          mylog := car mylog; reflog := car reflog;
          file_compare(mylog, reflog, packge);
          close mylog;
          close reflog >> >>;
     time_data := wrs time_data;
     if not zerop log_count then <<
        time_ratio := expt(time_ratio, 1.0/log_count);
        gc_time_ratio := expt(gc_time_ratio, 1.0/log_count);
        terpri();
        p1 := set!-print!-precision 3;
        princ "Over "; prin log_count; princ " tests the speed ratio was ";
        print time_ratio;
        princ "    (or ";
        prin gc_time_ratio;
        printc " is garbage collection costs are included)";
        set!-print!-precision p1 >>;
     close wrs time_data;
     linelength oll;
  end;


faslend;

% faslout 'cslhelp;
% 
% module cslhelp;
% 
% global '(!*force);
% 
% flag('(force),'switch);
% flag('(on),'eval);
% 
% on force;
% 
% symbolic procedure formhelp(u,vars,mode);
%    list('help, 'list . for each x in cdr u collect mkquote x);
% 
% if member('help, lispsystem!*) then <<
%    put('help, 'stat, 'rlis);
%    flag('(help), 'go);
%    put('help, 'formfn, 'formhelp) >>;
% 
% off force;
% remflag('(on),'eval);
% 
% endmodule;
% 
% faslend;


load!-module 'remake;

<< initreduce();
   date!* := "Bootstrap version";
   !@reduce := symbol!-value gensym();
   preserve('begin, "REDUCE", t) >>;
   
symbolic;

!#if (and (not (memq 'embedded lispsystem!*)) (not !*savedef))
load!-module 'user;
!#endif

!@reduce := concat(!@srcdir, "/../..");

get_configuration_data();

package!-remake2(prolog_file,'support);

package!-remake2(rend_file,'support);

package!-remake2('entry,'support);

package!-remake2('smacros,'support);

package!-remake2('remake,'support);


% The next lines have LOTS of hidden depth!  They restart CSL repeatedly
% so that each of the modules that has to be processed gets dealt with in
% a fresh uncluttered environment. The list of modules is fetched from
% a configuration file which must have 3 s-expressions in it. The first
% is a list of basic modules that must be built to get a core version of
% REDUCE. The second list identifies modules that can be built one the core
% is ready for use, while the last list indicates which modules have
% associated test scripts.
%
% when the modules have been rebuild the system does a restart that
% kicks it back into REDUCE by calling begin(). This then continues
% reading from the stream that had been the standard input when this
% job started. Thus this script MUST be invoked as
%           ./csl -obootstrapreduce.img -z buildreduce.lsp
% with the file buildreduce.lsp specified on the command line in the call. It
% will not work if you start csl manually and then do a (rdf ..) [say]
% on buildreduce.lsp.  I told you that it was a little delicate.

!#if !*savedef
% Some switches may be in the utter core and not introduced via the
% "switch" declaration...
for each y in oblist() do
  if flagp(y, 'switch) then <<
     princ "+++ Declaring a switch: ";
     print y >>; 
!#endif

get_configuration_data();

build_reduce_modules reduce_base_modules;

% Now I want to do a cold-start so that I can create a sensible
% image for use in the subsequent build steps. This image should not
% contain ANYTHING extraneous.

symbolic restart!-csl nil;

(setq !*savedef (and (null (memq 'embedded lispsystem!*))
                     (zerop (cdr (assoc 'c!-code lispsystem!*)))))
(make!-special '!*native_code)
(setq !*native_code nil)

(setq !*backtrace t)

(cond ((and (null !*savedef)
            (null (memq 'embedded lispsystem!*)))
       (load!-module 'user)))

(load!-module 'cslcompat)

(setq !*comp nil)

(load!-module 'module)            % Definition of load_package, etc.

(load!-module 'cslprolo)          % CSL specific code.

(setq loaded!-packages!* '(cslcompat user cslprolo))

% NB I will re-load the "patches" module when REDUCE is started
% if there is a version newer than the one I load up here. Note that
% if there had not been a "patches.red" file I will not have a module to load
% here.
%
% (cond
%    ((modulep 'patches) (load!-module 'patches)))

(load!-package 'rlisp)

(load!-package 'cslrend)

(load!-package 'smacros)

(load!-package 'poly)

(load!-package 'arith)

(load!-package 'alg)

(load!-package 'mathpr)

(cond 
   ((modulep 'tmprint) (load!-package 'tmprint)))

(load!-package 'entry)

% (write!-help!-module "$srcdir/../../util/reduce.inf" nil)
%
% (load!-module 'cslhelp)

(setq version!* "Reduce (Free CSL version)")

(setq date!*  (date t))

(setq !*backtrace nil)

(initreduce)

(setq no_init_file nil)

(setq !@csl (setq !@reduce (symbol!-value (gensym))))

% If the user compiles a new FASL module then I will let it
% generate native code by default. I build the bulk of REDUCE
% without that since I have statically-selected hot-spot compilation
% that gives me what I believe to be a better speed/space tradeoff.

% Oh well, let's change that and disable it by dafault since at least on
% windows there are problems with windows vs cygwin file-names.

(fluid '(!*native_code))
(setq !*native_code nil)   % Try T if you are VERY keen...

(preserve 'begin (bldmsg "") t)
% Note that (preserve) here arranges to reload the image that it
% creates, and it then runs (begin) the start-up function. This will
% leave us running Reduce in algebraic mode...


%
% See the fairly length comments given a bit earlier about the
% delicacy of the next few lines!
%

symbolic;

load!-module 'remake;

get_configuration_data();

build_reduce_modules reduce_extra_modules;

symbolic;
no_init_file := t;

"**** **** REDUCE FULLY REBUILD **** ****";

% At this stage I have a complete workable REDUCE. If built using a
% basic CSL (I call it "bootstrapreduce" here)  nothing has been compiled into C
% (everything is bytecoded), and it is big because it has retained all
% Lisp source code in the image file. If however I built using a version
% of CSL ("reduce") that did have things compiled into C then these will
% be exploited and the original Lisp source will be omitted from the
% image, leaving a production version.

bye;


