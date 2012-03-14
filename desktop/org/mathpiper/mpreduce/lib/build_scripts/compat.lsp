% This file defines functions and variables needed to make REDUCE
% and the underlying CSL system compatible. it should
% be loaded as the first file whenever REDUCE services are required.



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

(setpchar "> ")

(remflag '(geq leq neq logand logor logxor leftshift princ printc
	evenp reversip seprp atsoc eqcar flagp!*!* flagpcar get!*
	prin1 prin2 apply0 apply1 apply2 apply3 smemq spaces
	subla gcdn lcmn printprompt pair putc) 'lose)

(symbol!-make!-fastget 32)
(symbol!-make!-fastget 'noncom 0)  % built into the kernel
(symbol!-make!-fastget 'lose   1)

(flag '(raise lower echo comp plap pgwd pwrds savedef) 'switch)

(make!-special '!*echo)
(setq !*echo nil)
(make!-special '!*raise)
(setq !*raise nil)
(make!-special '!*lower)
(setq !*lower t)
(make!-special '!*savedef)
% I only nil out !*savedef if it is not already present because of
% some bootstrapping delicacies when this file is re-loaded.
(if (not (boundp '!*savedef)) (setq !*savedef nil))
(make!-special '!*comp)
(setq !*comp nil)
(make!-special '!*plap)
(setq !*plap nil)
(make!-special '!*pgwd)
(setq !*pgwd nil)
(make!-special '!*pwrds)
(setq !*pwrds t)

% Until the following lines have been executed the
% bitwise operations listed here will not work.

(progn
   (symbol!-set!-env 'logand 1)
   (symbol!-set!-env 'logxor 6)
   (symbol!-set!-env 'logor 7)
   (symbol!-set!-env 'logeqv 9))

(make!-special '!!fleps1)
(setq !!fleps1 1.0e-12)
(symbol!-set!-env 'safe!-fp!-plus '!!fleps1)

(de rplacw (a b) (progn (rplaca a (car b)) (rplacd a (cdr b))))

(de expand (l fn)
   (cond
      ((null (cdr l)) (car l))
      (t (list fn (car l) (expand (cdr l) fn)))))

(dm plus (a) 
   (cond ((null (cdr a)) 0)
         (t (expand (cdr a) 'plus2))))

(dm times (a) 
   (cond ((null (cdr a)) 1)
         (t (expand (cdr a) 'times2))))

(de mapcar (l fn)
  (prog (r)
 top (cond ((null l) (return (reversip r))))
     (setq r (cons (funcall fn (car l)) r))
     (setq l (cdr l))
     (go top)))

(de maplist (l fn)
  (prog (r)
 top (cond ((null l) (return (reversip r))))
     (setq r (cons (funcall fn l) r))
     (setq l (cdr l))
     (go top)))

(de mapcan (l fn)
  (cond ((null l) nil)
	(t (nconc (funcall fn (car l)) (mapcan (cdr l) fn)))))

(de mapcon (l fn)
  (cond ((null l) nil)
	(t (nconc (funcall fn l) (mapcon (cdr l) fn)))))

(de mapc (l fn)
  (prog ()
 top (cond ((null l) (return nil)))
     (funcall fn (car l))
     (setq l (cdr l))
     (go top)))

(de map (l fn)
  (prog ()
 top (cond ((null l) (return nil)))
     (funcall fn l)
     (setq l (cdr l))
     (go top)))

(de copy (a)
   (cond
      ((atom a) a)
      (t (cons (copy (car a)) (copy (cdr a))))))

(de sassoc (a l fn)
  (cond
     ((atom l) (funcall fn))
     ((equal a (caar l)) (car l))
     (t (sassoc a (cdr l) fn))))

(de rassoc (x l)        % Not in Standard Lisp
   (prog ()
loop  (cond ((atom l) (return nil))
	    ((equal x (cdar l)) (return (car l)))
	    (t (setq l (cdr l)) (go loop))) ))

(de lastcar (x)         % Not in Standard Lisp
   (cond
      ((null x) nil)
      ((null (cdr x)) (car x))
      (t (lastcar (cdr x)))))


% The system-coded primitive function INTERNAL-OPEN opens a file, and takes
% a second argument that shows what options are wanted. See "print.c" for an
% explanation of the bits.

(de open (a b)
   (cond
     ((eq b 'input) (internal!-open a (plus 1 64)))
                     % if-does-not-exist error
     ((eq b 'output) (internal!-open a (plus 2 20 32))) 
                     % if-does-not-exist create,
                     % if-exists new-version
     ((eq b 'append) (internal!-open a (plus 2 8 32)))
                     % if-exists append
     (t (error "bad direction ~A in open" b))))

(de binopen (a b)
   (cond
     ((eq b 'input) (internal!-open a (plus 1 64 128)))
     ((eq b 'output) (internal!-open a (plus 2 20 32 128)))
     ((eq b 'append) (internal!-open a (plus 2 8 32 128)))
     (t (error "bad direction ~A in binopen" b))))

(de pipe!-open (c d)
   (cond
     ((eq d 'input) (internal!-open c (plus 1 256)))
     ((eq d 'output) (internal!-open c (plus 2 256)))
     (t (error "bad direction ~A in pipe-open" d))))

(de putd (a type b)
  (progn
     (cond
	((eqcar b 'funarg) (setq b (cons 'lambda (cddr b)))))
     (cond
        ((flagp a 'lose) (progn
           (terpri) (princ "+++ ") (prin a)
           (printc " not defined (LOSE flag)")
           nil))
        (t (progn
             (cond
                ((and !*redefmsg (getd a)) (progn
                   (terpri) (princ "+++ ") (prin a) (printc " redefined"))))
             (cond
                ((eq type 'expr) (symbol!-set!-definition a b))
		((eq type 'subr) (symbol!-set!-definition a b))
                ((and (eq type 'macro) (eqcar b 'lambda))
                   (eval (list!* 'dm a (cdr b))))
% CSL does not really support user-defined special forms and so at some
% stage I will make "df" a macro that makes some attempt to simulate the
% desired behaviour using a macro.
                ((and (eq type 'fexpr) (eqcar b 'lambda))
                   (eval (list!* 'df a (cdr b))))
                (t (error "Bad type ~S in putd" type)))
             a))))))

% A version of this in rlisp/rsupport.red tries to compile the
% odd sort of definition involved, but I will not!
(de putc (a b c)
   (put a b c))

(de traceset1 (name)
   (prog (w !*comp)
      (setq w (getd name))
      (cond ((not (and (eqcar w 'expr) (eqcar (cdr w) 'lambda)))
         (princ "+++++ ") (prin name)
         (printc " should be interpreted for traceset to work")
         (return nil)))
      (putd name 'expr (subst 'noisy!-setq 'setq (cdr w)))
      (trace (list name))))

(de untraceset1 (name)
   (prog (w !*comp)
      (setq w (getd name))
      (cond ((not (and (eqcar w 'expr) (eqcar (cdr w) 'lambda)))
         (princ "+++++ ") (prin name)
         (printc " should be interpreted for untraceset to work")
         (return nil)))
      (putd name 'expr (subst 'setq 'noisy!-setq (cdr w)))
      (untrace (list name))))

(de traceset (l)
   (mapc l (function traceset1)))

(de untraceset (l)
   (mapc l (function untraceset1)))

(de deflist (a b)
  (prog (r)
top (cond ((null a) (return (reversip r))))
    (put (caar a) b (cadar a))
    (setq r (cons (caar a) r))
    (setq a (cdr a))
    (go top)))

(de global (l)
   (prog nil
 top  (cond ((null l) (return nil)))
      (make!-global (car l))
      (cond ((not (boundp (car l))) (set (car l) nil)))
      (setq l (cdr l))
      (go top)))

(de fluid (l)
   (prog nil
 top  (cond ((null l) (return nil)))
      (make!-special (car l))
      (cond ((not (boundp (car l))) (set (car l) nil)))
      (setq l (cdr l))
      (go top)))

(de unglobal (l)
   (prog ()
 top  (cond ((null l) (return nil)))
      (unmake!-global (car l))
      (setq l (cdr l))
      (go top)))

(de unfluid (l)
   (prog ()
 top  (cond ((null l) (return nil)))
      (unmake!-special (car l))
      (setq l (cdr l))
      (go top)))

(global '(ofl!*))

(de printprompt (u) nil)

(global '(program!* ttype!* eof!*))

(global '(crbuf!*))

(global '(blank !$eol!$ tab !$eof!$ esc!*))

(fluid '(!*notailcall !*carcheckflag))

(fluid '(!*terminal!-io!* !*standard!-input!* !*standard!-output!* 
         !*error!-output!* !*trace!-output!* !*debug!-io!* !*query!-io!*))

(setq !*notailcall nil)
(setq !*carcheckflag t)

(de carcheck (n)
   (prog (old)
      (cond ((zerop n) (setq n nil)))
      (setq old !*carcheckflag)
      (setq !*carcheckflag n)
      (return old)))

(progn
% The "special-char" numeric codes here are all very odd and are of no
% relevance beyond the initial build stages of this Lisp. In particular they
% have little or no resemblance to any widely used character code schemes.
   (setq blank   (compress (list '!! (special!-char 0))))
   (setq !$eol!$ (compress (list '!! (special!-char 1))))
   (setq tab     (compress (list '!! (special!-char 3))))
   (setq esc!*   (compress (list '!! (special!-char 9))))
   (setq !$eof!$ (special!-char 8))
   nil)

(setq crbuf!* (list !$eol!$))    % may not be necessary

% Since this should never get called I will just not define it here!

%(de symerr (u v)
%  (progn (terpri)
%     (print (list 'symerr u v))
%     (error 'failure)))

(global '(!*full!-oblist))

(setq !*full!-oblist nil)

(de s!:oblist (v r)
   (prog (n a)
      (setq n (upbv v))
top   (cond ((minusp n) (return r)))
      (setq a (getv v n))
      (cond
	 ((and (idp a)
% I list things that have a function value of some sort or that have
% a non-empty property-list.  Symbols that have been mentioned but which do
% not have properties or values are missed out since they are dull and
% seeing them listed is probably not very helpful.  People may disagree
% about that... if so it would be very easy to remove the tests here and
% end up listing everything. Eg some symbols exist and are used as property-
% names (via PUT and GET) but are not used for anything else...
%
% Well, the flag !*full!-oblist can be set to force inclusion of
% everything!
	       (or !*full!-oblist
                   (symbol!-function a)
		   (macro!-function a)
		   (special!-form!-p a)
		   (fluidp a)
		   (globalp a)
		   (not (null (plist a)))))
	  (setq r (cons a r))))
      (setq n (sub1 n))
      (go top)))

(de s!:oblist1 (v r)
   (cond
      ((null v) r)
      ((vectorp v) (s!:oblist v r))
% This allows for segmented object-vectors
      (t (s!:oblist (car v) (s!:oblist1 (cdr v) r)))))

(de oblist ()
   (sort (s!:oblist1 (getv !*package!* 1) nil)
	 (function orderp)))


% Now a few things not needed by Standard Lisp but maybe helpful
% when using Lisp directly.

(de s!:make!-psetq!-vars (u)
   (if (null u)
       nil
       (if (null (cdr u))
           (error "odd number of items in psetq")
           (cons (gensym) (s!:make!-psetq!-vars (cddr u))))))

(de s!:make!-psetq!-bindings (vars u)
   (if (null u)
       nil
       (cons
          (list (car vars) (cadr u))
          (s!:make!-psetq!-bindings (cdr vars) (cddr u)))))

(de s!:make!-psetq!-assignments (vars u)
   (if (null u)
       nil
       (cons
          (list 'setq (car u) (car vars))
          (s!:make!-psetq!-assignments (cdr vars) (cddr u)))))

(dm psetq (x)
   (!~let ((vars (s!:make!-psetq!-vars (cdr x))))
      `(let!* ,(s!:make!-psetq!-bindings vars (cdr x))
         ,@(s!:make!-psetq!-assignments vars (cdr x)))))

% (do ((v i s) ..)
%     (end result ...)
%     body)

(de s!:do!-bindings (u)
   (if (null u)
       nil
       (if (atom (car u))
           (cons (car u) (s!:do!-bindings (cdr u)))
           (if (null (cdar u))
               (cons (list (caar u) nil) (s!:do!-bindings (cdr u)))
               (cons (list (caar u) (cadar u)) (s!:do!-bindings (cdr u)))))))

(de s!:do!-endtest (u)
   (if (null u)
       nil
       (car u)))

(de s!:do!-result (u)
   (if (null u)
       nil
       (cdr u)))

(de s!:do!-updates (u)
   (if (null u)
       nil
       (!~let ((v (car u))
             (x (s!:do!-updates (cdr u))))
          (if (or (atom v)
                  (null (cdr v))
                  (null (cddr v)))
              x
              (cons (car v) (cons (caddr v) x))))))


(de s!:expand!-do (u letter setter)
   (let!* ((bindings (s!:do!-bindings (car u)))
           (result (s!:do!-result (cadr u)))
           (updates (s!:do!-updates (car u)))
           (body (cddr u))
           (endtest (s!:do!-endtest (cadr u)))
           (upd (if updates (list (cons setter updates)) nil))
           (res (if (null result)
                    nil
                    (if (null (cdr result))
                        (car result)
                        (cons 'progn result))))
           (x (if (null endtest) nil
                  `((if ,endtest (return ,res)))))
           (g (gensym)))
      (if bindings
         `(,letter ,bindings
               (prog nil
            ,g    ,@x
                  ,@body
                  ,@upd
                  (go ,g)))
         `(prog nil
         ,g    ,@x
               ,@body
               ,@upd
               (go ,g)))))

(dm do (u) (s!:expand!-do (cdr u) '!~let 'psetq))

(dm do!* (u) (s!:expand!-do (cdr u) 'let!* 'setq))

(de s!:expand!-dolist (vir b)
   (prog (l v var init res)
     (setq var (car vir))
     (setq init (car (setq vir (cdr vir))))
     (setq res (cdr vir))
     (setq v (gensym))
     (setq l (gensym))
     (return `(prog (,v ,var)
                (setq ,v ,init)
           ,l   (cond ((null ,v) (return (progn ,@res))))
                (setq ,var (car ,v))
                ,@b
                (setq ,v (cdr ,v))
                (go ,l)))))

(dm dolist (u) (s!:expand!-dolist (cadr u) (cddr u)))

(de s!:expand!-dotimes (vnr b)
   (prog (l v var count res)
     (setq var (car vnr))
     (setq count (car (setq vnr (cdr vnr))))
     (setq res (cdr vnr))
     (setq v (gensym))
     (setq l (gensym))
     (return `(prog (,v ,var)
                (setq ,v ,count)
                (setq ,var 0)
           ,l   (cond ((not (lessp ,var ,v)) (return (progn ,@res))))
                ,@b
                (setq ,var (add1 ,var))
                (go ,l)))))

(dm dotimes (u) (s!:expand!-dotimes (cadr u) (cddr u)))

(flag '(geq leq neq logand logor logxor leftshift princ printc
	evenp reversip seprp atsoc eqcar flagp!*!* flagpcar get!*
	prin1 prin2 apply0 apply1 apply2 apply3 smemq spaces
	subla gcdn lcmn printprompt pair putc) 'lose)

% end of compat.lsp

