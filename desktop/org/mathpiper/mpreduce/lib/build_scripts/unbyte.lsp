
% RLISP to LISP converter. A C Norman 2004-2007


(linelength 72)



(de lprim (x) (print x))

(de no!-side!-effectp (u) (cond ((atom u) (or (numberp u) (and (idp u) (not (
or (fluidp u) (globalp u)))))) (t (cond ((eq (car u) (quote quote)) t) (t (
cond ((flagp!*!* (car u) (quote nosideeffects)) (no!-side!-effect!-listp u)) 
(t nil)))))))

(de no!-side!-effect!-listp (u) (or (null u) (and (no!-side!-effectp (car u))
(no!-side!-effect!-listp (cdr u)))))

(flag (quote (car cdr caar cadr cdar cddr caaar caadr cadar caddr cdaar cdadr
cddar cdddr cons)) (quote nosideeffects))

(de structchk (u) (prog (v) (prog nil lab1000 (progn (setq v (copy u)) (setq 
u (structchk1 u))) (cond ((null (equal u v)) (go lab1000)))) (return u)))

(de structchk1 (u) (prog (x) (cond ((or (atom u) (eq (car u) (quote quote))) 
(return u)) (t (cond ((and (atom (car u)) (setq x (get (car u) (quote 
structfn)))) (return (apply x (list u)))) (t (cond ((eq (car u) (quote lambda
)) (return (list (quote lambda) (cadr u) (structchk1 (caddr u))))) (t (cond (
(eq (car u) (quote procedure)) (return (list (quote procedure) (cadr u) (
caddr u) (cadddr u) (car (cddddr u)) (structchk1 (cadr (cddddr u)))))) (t (
return (prog (var1002 var1003) (setq var1002 u) lab1001 (cond ((null var1002)
(return (reversip var1003)))) (prog (x) (setq x (car var1002)) (setq var1003
(cons (structchk1 x) var1003))) (setq var1002 (cdr var1002)) (go lab1001))))
)))))))))

(put (quote cond) (quote structfn) (quote strcond))

(put (quote rblock) (quote structfn) (quote blockchk))

(put (quote prog) (quote structfn) (quote progchk))

(put (quote progn) (quote structfn) (quote prognchk))

(de strcond (u) (prog nil (setq u (prog (var1005 var1006) (setq var1005 (cdr 
u)) lab1004 (cond ((null var1005) (return (reversip var1006)))) (prog (x) (
setq x (car var1005)) (setq var1006 (cons (list (car x) (structchk1 (cadr x))
) var1006))) (setq var1005 (cdr var1005)) (go lab1004))) (cond ((and (equal (
length u) 2) (eqcar (cadar u) (quote cond)) (equal (caadr u) (quote t))) (
setq u (cons (list (mknot (caar u)) (cadadr u)) (cdadar u))))) (return (cons 
(quote cond) u))))

(de mknot (u) (cond ((and (not (atom u)) (memq (car u) (quote (not null)))) (
cadr u)) (t (list (quote not) u))))

(fluid (quote (flg lablist)))

(de addlbl (lbl) (cond ((atsoc lbl lablist) nil) (t (setq lablist (cons (list
lbl nil) lablist)))))

(de addblock (lst) (rplacd (cdr (atsoc (getlbl (caar lst)) lablist)) (cons (
cdar lst) (cdr lst))))

(de gochk (u) (cond ((or (atom u) (memq (car u) (quote (quote prog)))) nil) (
t (cond ((eq (car u) (quote go)) (updlbl (cadr u) u)) (t (progn (gochk (car u
)) (gochk (cdr u))))))))

(de updlbl (lbl exp) (prog (x) (setq x (atsoc lbl lablist)) (cond (x (rplaca 
(cdr x) (cons exp (cadr x)))) (t (setq lablist (cons (list lbl (list exp)) 
lablist))))))

(de transferp (u) (cond ((or (atom u) (not (idp (car u)))) nil) (t (cond ((
flagp (car u) (quote transfer)) (car u)) (t (cond ((eq (car u) (quote cond)) 
(condtranp (cdr u))) (t (cond ((memq (car u) (quote (prog2 progn))) (
transferp (car (reverse (cdr u))))) (t nil)))))))))

(flag (quote (go return rederr error errach)) (quote transfer))

(de condtranp (u) (cond ((null u) nil) (t (cond ((and (null (cdr u)) (eq (
caar u) t)) (transferp (cadar u))) (t (and (transferp (cadar u)) (condtranp (
cdr u))))))))

(de progchk (u) (blockchk1 u (quote prog)))

(de blockchk (u) (blockchk1 u (quote rblock)))

(de blockchk1 (u v) (prog (flg lablist laststat vars top x z) (setq vars (
cadr u)) (setq u (cddr u)) (cond ((null u) (lprie "empty block"))) (setq x u)
(prog nil lab1007 (cond ((null (cdr x)) (return nil))) (setq x (cdr x)) (go 
lab1007)) (prog nil lab1008 (cond ((null (and u (not (labelp (car u))))) (
return nil))) (progn (setq top (cons (car u) top)) (gochk (car u)) (setq u (
cdr u))) (go lab1008)) (cond ((null u) (progn (setq top (reversip top)) (go 
ret))) (t (cond ((or (null top) (not (transferp (car top)))) (progn (setq top
(cons (list (quote go) (getlbl (car u))) top)) (gochk (car top))))))) (setq 
top (reversip top)) (setq top (cons (list nil) (cons nil (cons top (car (
reverse top)))))) (prog nil lab1010 (cond ((null u) (return nil))) (cond ((
labelp (car u)) (progn (addlbl (getlbl (car u))) (cond ((or (null laststat) (
transferp laststat)) (progn (setq laststat nil) (setq x (list (car u))) (setq
u (cdr u)) (prog nil lab1009 (cond ((null (and u (not (transferp laststat)))
) (return nil))) (progn (cond ((labelp (car u)) (setq u (cons (list (quote go
) (getlbl (car u))) u)))) (gochk (car u)) (setq laststat (car u)) (setq x (
cons (car u) x)) (setq u (cdr u))) (go lab1009)) (addblock (cons (reversip x)
laststat)) (setq x nil)))))) (t (rederr (list "unreachable statement" (car u
))))) (go lab1010)) (setq lablist (reversip lablist)) a (setq flg nil) (prog 
(var1012) (setq var1012 (cons top lablist)) lab1011 (cond ((null var1012) (
return nil))) (prog (x) (setq x (car var1012)) (cond ((and (cdr x) (cddr x) (
eqcar (cdddr x) (quote go))) (condgochk (caddr x) (cdddr x))))) (setq var1012
(cdr var1012)) (go lab1011)) (setq x nil) (prog nil lab1013 (cond ((null 
lablist) (return nil))) (progn (setq z (length (cadar lablist))) (cond ((or (
equal z 0) (and (equal z 1) (equal (cdddar lablist) (caadar lablist)))) (
lprim (list "unreferenced block at label" (caar lablist)))) (t (cond ((equal 
z 1) (progn (setq flg t) (lprim (list "label" (caar lablist) "removed")) (
rplacw (caadar lablist) (prognchk1 (caddar lablist))))) (t (setq x (cons (car
lablist) x)))))) (setq lablist (cdr lablist))) (go lab1013)) (setq lablist (
reversip x)) (prog (var1015) (setq var1015 lablist) lab1014 (cond ((null 
var1015) (return nil))) (prog (z) (setq z (car var1015)) (cond ((and (equal (
cdddr z) (caadr z)) (eqcar (caaddr z) (quote cond)) (null (cddr (caaddr z))) 
(transferp (cadadr (caaddr z))) (notranp (cdaddr z))) (progn (setq flg t) (
rplaca (cdr z) (!&deleq (cdddr z) (cadr z))) (rplaca (cddr z) (list (whilechk
(mknull (caadr (caaddr z))) (cdr (reverse (cdaddr z)))) (cadadr (caaddr z)))
) (rplacd (cddr z) nil))))) (setq var1015 (cdr var1015)) (go lab1014)) (cond 
(flg (prog (var1018) (setq var1018 (cons top lablist)) lab1017 (cond ((null 
var1018) (return nil))) (prog (y) (setq y (car var1018)) (progn (setq z (
caddr y)) (prog nil lab1016 (cond ((null z) (return nil))) (cond ((eqcar (car
z) (quote progn)) (rplacw z (nconc (cdar z) (cdr z)))) (t (setq z (cdr z))))
(go lab1016)) (cond ((and (cdr y) (cddr y) (eqcar (cdddr y) (quote progn))) 
(rplacd (cddr y) (car (reverse (cdddr y)))))))) (setq var1018 (cdr var1018)) 
(go lab1017)))) (cond (flg (go a))) (setq top (caddr top)) (setq x top) (prog
nil lab1020 (cond ((null x) (return nil))) (progn (prog nil lab1019 (cond ((
null (cdr x)) (return nil))) (setq x (cdr x)) (go lab1019)) (cond ((and (
eqcar (car x) (quote go)) (setq z (atsoc (cadar x) lablist))) (progn (rplacw 
x (cond ((cdadr z) (cons (mklbl (car z)) (caddr z))) (t (progn (lprim (list 
"label" (caar lablist) "removed")) (caddr z))))) (setq lablist (delete z 
lablist)))) (t (cond (lablist (progn (rplacd x (cons (mklbl (caar lablist)) (
caddar lablist))) (setq lablist (cdr lablist)))) (t (setq x (cdr x))))))) (go
lab1020)) ret (setq top (miscchk (structchk1 top))) (cond ((and (null vars) 
(eqcar (car top) (quote return))) (return (cadar top))) (t (return (cons v (
cons vars top)))))))

(de miscchk (u) (prog (v w) (setq v u) (prog nil lab1021 (cond ((null v) (
return nil))) (cond ((and (eqcar (car v) (quote setq)) (neq (setq w (setqchk 
(car v) (cdr v))) v)) (rplacw v w)) (t (cond ((and (cdr v) (eqcar (car v) (
quote cond)) (null (cddar v)) (eqcar (cadr (cadar v)) (quote return)) (eqcar 
(cadr v) (quote return))) (rplacw v (cons (list (quote return) (list (quote 
cond) (list (caadar v) (cadr (cadr (cadar v)))) (list (quote t) (cadr (cadr v
))))) (cddr v)))) (t (setq v (cdr v)))))) (go lab1021)) (return u)))

(de setqchk (u v) (prog (x y z) (setq x (cadr u)) (setq y (caddr u)) (cond ((
not (no!-side!-effectp y)) (return (cons u v)))) a (cond ((null v) (return (
cons u (reversip z)))) (t (cond ((and (eqcar (car v) (quote return)) (
used!-oncep x (cadar v))) (progn (lprim (list "assignment for" x "removed")) 
(return (nconc (reversip z) (cons (substq x y (car v)) (cdr v)))))) (t (cond 
((not (smemq x (car v))) (progn (setq z (cons (car v) z)) (setq v (cdr v)) (
go a))) (t (return (cons u (nconc (reversip z) v)))))))))))

(de used!-oncep (u v) (cond ((atom v) t) (t (cond ((eq (car v) (quote quote))
t) (t (cond ((eq u (car v)) (not (smemq u (cdr v)))) (t (used!-oncep u (cdr 
v)))))))))

(de substq (u v w) (cond ((atom w) (cond ((eq u w) v) (t w))) (t (cond ((eq (
car w) (quote quote)) w) (t (cond ((eq u (car w)) (cons v (cdr w))) (t (cond 
((not (atom (car w))) (cons (substq u v (car w)) (substq u v (cdr w)))) (t (
cons (car w) (substq u v (cdr w))))))))))))

(de labelp (u) (or (atom u) (eq (car u) (quote !*label))))

(de getlbl (u) (cond ((atom u) u) (t (cadr u))))

(de mklbl (u) (list (quote !*label) u))

(de notranp (u) (null (smemqlp (quote (go return)) (cdr (reverse u)))))

(de !&deleq (u v) (cond ((null v) nil) (t (cond ((eq u (car v)) (cdr v)) (t (
cons (car v) (!&deleq u (cdr v))))))))

(de prognchk (u) (prognchk1 (cdr u)))

(de prognchk1 (u) (cond ((or (null (cdr u)) (null (cdr (setq u (miscchk u))))
) (car u)) (t (cons (quote progn) u))))

(de mknull (u) (cond ((and (not (atom u)) (memq (car u) (quote (null not)))) 
(cadr u)) (t (list (quote null) u))))

(de condgochk (u v) (cond ((null u) nil) (t (progn (condgochk (cdr u) v) (
cond ((eqcar (car u) (quote cond)) (cgchk1 (cdar u) u v)))))))

(de cgchk1 (u v w) (cond ((null u) nil) (t (cond ((not (transferp (cadar u)))
nil) (t (prog (x y z) (cgchk1 (cdr u) v w) (setq x (cadar u)) (cond ((or (
equal x w) (and (eqcar x (quote progn)) (equal (setq x (car (reverse x))) w) 
(setq y (reverse (cdr (reverse (cdadar u))))))) (progn (setq flg t) (setq z (
atsoc (cadr w) lablist)) (rplaca (cdr z) (!&deleq x (cadr z))) (rplaca (car u
) (mknull (caar u))) (setq z (reverse (cdr (reverse (cdr v))))) (cond ((cdr u
) (progn (setq z (cons (cons (quote cond) (cdr u)) z)) (rplacd u nil)))) (
cond (y (rplacd u (list (list t (prognchk1 y)))))) (rplaca (cdar u) (
prognchk1 z)) (rplacd v (list w)))) (t nil))))))))

(de mapox (u) (mapsox u (quote on) (quote do)))

(de mapcox (u) (mapsox u (quote in) (quote do)))

(de maplistox (u) (mapsox u (quote on) (quote collect)))

(de mapcarox (u) (mapsox u (quote in) (quote collect)))

(de mapconox (u) (mapsox u (quote on) (quote conc)))

(de mapcanox (u) (mapsox u (quote in) (quote conc)))

(de mapsox (u v w) (prog (x y z) (setq x (cadr u)) (setq y (caddr u)) (cond (
(not (eqcar y (quote function))) (rederr (list 
"syntax error in map expression" u)))) (setq y (cadr y)) (cond ((atom y) (
progn (setq z (quote x)) (setq y (list y z)))) (t (cond ((or (not (eq (car y)
(quote lambda))) (null (cadr y)) (cdadr y)) (rederr (list 
"syntax error in map expression" u))) (t (progn (setq z (caadr y)) (setq y (
caddr y))))))) (return (list (quote foreach) z v x w y))))

(put (quote map) (quote structfn) (quote mapox))

(put (quote mapc) (quote structfn) (quote mapcox))

(put (quote maplist) (quote structfn) (quote maplistox))

(put (quote mapcar) (quote structfn) (quote mapcarox))

(put (quote mapcan) (quote structfn) (quote mapcanox))

(put (quote mapcon) (quote structfn) (quote mapconox))

(de whilechk (u v) (prog (w) (return (cond ((and (idp u) (equal (car v) (list
(quote setq) u (list (quote cdr) u))) (not (eq (setq w (caronly u (cdr v) (
quote j))) (quote !*failed!*)))) (list (quote progn) (list (quote foreach) (
quote j) (quote in) u (quote do) (prognchk1 (reversip w))) (list (quote setq)
u nil))) (t (list (quote while) u (prognchk1 (reversip v))))))))

(de caronly (u v w) (prog (x) (return (cond ((not (smemq u v)) v) (t (cond ((
atom v) (cond ((eq u v) (quote !*failed!*)) (t v))) (t (cond ((or (not (idp (
car v))) (not (and (eqcar (cdr v) u) (cdr v) (null (cddr v)) (setq x (get (
car v) (quote carfn)))))) (cmerge (caronly u (car v) w) (caronly u (cdr v) w)
)) (t (cond ((eq (car v) (quote car)) w) (t (list x w))))))))))))

(deflist (quote ((car t) (caar car) (cdar cdr) (caaar caar) (cadar cadr) (
cdaar cdar) (cddar cddr) (caaaar caaar) (caadar caadr) (cadaar cadar) (caddar
caddr) (cdaaar cdaar) (cdadar cdadr) (cddaar cddar) (cdddar cdddr))) (quote 
carfn))

(de cmerge (u v) (cond ((or (eq u (quote !*failed!*)) (eq v (quote !*failed!*
))) (quote !*failed!*)) (t (cons u v))))

(fluid (quote (all_jumps)))

(fluid (quote (!@a !@b !@w !@stack !@catch)))

(global (quote (opnames)))

(de unbyte (name) (prog (pc code len env byte r entry_stack w w1 w2 args 
nargs stack deepest locals all_jumps !@a !@b !@w !@stack !@catch) (setq !@a (
gensym)) (setq !@b (gensym)) (setq !@w (gensym)) (setq !@stack (gensym)) (
setq code (symbol!-env name)) (setq nargs (symbol!-argcount name)) (cond ((or
(atom code) (not (bpsp (car code)))) (return nil))) (setq env (cdr code)) (
setq code (car code)) (setq len (bps!-upbv code)) (cond ((fixp nargs) (progn 
(setq entry_stack nargs) (cond ((lessp nargs 4) (setq pc 0)) (t (setq pc 1)))
)) (t (progn (setq entry_stack (cadr nargs)) (cond ((neq (logand (caddr nargs
) 2) 0) (setq entry_stack (plus entry_stack 1)))) (setq pc 2)))) (setq r nil)
(setq all_jumps (list nil pc)) (prog nil lab1022 (cond ((null (leq pc len)) 
(return nil))) (progn (setq byte (bps!-getv code pc)) (setq w (funcall (getv 
opnames byte) (plus pc 1) code env)) (cond (r (setq w1 (caddr (car r)))) (t (
setq w1 nil))) (cond ((eqcar w1 (quote if)) (setq r (cons (cons pc (cons (
cadr (cadddr w1)) (cdr w))) r))) (t (setq r (cons (cons pc (cons nil (cdr w))
) r)))) (setq pc (plus pc (car w)))) (go lab1022)) (prog nil lab1023 (cond ((
null all_jumps) (return nil))) (progn (setq w (assoc (cadr all_jumps) r)) (
cond ((null w) (error 1 "Branch destination not found"))) (cond ((null (cadr 
w)) (rplaca (cdr w) (gensym)))) (rplaca (cdr all_jumps) (cadr w)) (setq w (
car all_jumps)) (rplaca all_jumps (quote go)) (setq all_jumps w)) (go lab1023
)) (setq w nil) (prog nil lab1026 (cond ((null r) (return nil))) (progn (setq
w1 (cddar r)) (setq w2 w1) (prog nil lab1024 (cond ((null (cdr w2)) (return 
nil))) (setq w2 (cdr w2)) (go lab1024)) (setq w2 (car w2)) (cond ((and w (not
(or (eqcar w2 (quote if)) (eqcar w2 (quote go)) (eqcar w2 (quote return)) (
eqcar w2 (quote throw))))) (progn (setq w1 (append w1 (list (list (quote go) 
(caar w)))))))) (prog nil lab1025 (cond ((null (null (cadar r))) (return nil)
)) (progn (setq r (cdr r)) (setq w1 (append (cddar r) w1))) (go lab1025)) (
setq w (cons (cons (cadar r) (cons nil w1)) w)) (setq r (cdr r))) (go lab1026
)) (rplaca (cdar w) (list nil)) (setq r (list (caar w))) (prog nil lab1029 (
cond ((null r) (return nil))) (prog (n) (setq w1 (assoc (car r) w)) (setq r (
cdr r)) (setq n (caadr w1)) (prog (var1028) (setq var1028 (cddr w1)) lab1027 
(cond ((null var1028) (return nil))) (prog (z) (setq z (car var1028)) (progn 
(cond ((eqcar z (quote freebind)) (setq n (cons (cadr z) n))) (t (cond ((
eqcar z (quote freerstr)) (progn (rplaca (cdr z) (car n)) (setq n (cdr n)))) 
(t (cond ((eqcar z (quote if)) (progn (setq r (set_bind (assoc (cadr (caddr z
)) w) r n)) (setq r (set_bind (assoc (cadr (cadddr z)) w) r n)))) (t (cond ((
eqcar z (quote go)) (setq r (set_bind (assoc (cadr z) w) r n)))))))))))) (
setq var1028 (cdr var1028)) (go lab1027))) (go lab1029)) (prog (var1031) (
setq var1031 w) lab1030 (cond ((null var1031) (return nil))) (prog (z) (setq 
z (car var1031)) (rplaca (cdr z) nil)) (setq var1031 (cdr var1031)) (go 
lab1030)) (rplaca (cdar w) entry_stack) (setq deepest entry_stack) (setq r (
list (caar w))) (prog nil lab1034 (cond ((null r) (return nil))) (prog (n) (
setq w1 (assoc (car r) w)) (cond ((null w1) (progn (prin (car r)) (princ 
" not found in ") (print w) (error 1 r)))) (setq r (cdr r)) (setq n (cadr w1)
) (cond ((greaterp n deepest) (setq deepest n))) (prog (var1033) (setq 
var1033 (cddr w1)) lab1032 (cond ((null var1033) (return nil))) (prog (z) (
setq z (car var1033)) (progn (cond ((equal z (quote push)) (setq n (plus n 1)
)) (t (cond ((equal z (quote lose)) (setq n (difference n 1))) (t (cond ((
eqcar z (quote freebind)) (setq n (plus n 2 (length (cadr z))))) (t (cond ((
equal z (quote pvbind)) (setq n (plus n 2))) (t (cond ((eqcar z (quote 
freerstr)) (setq n (difference (difference n 2) (length (cadr z))))) (t (cond
((equal z (quote pvrestore)) (setq n (difference n 2))) (t (cond ((or (equal
z (quote uncatch)) (equal z (quote unprotect))) (setq n (difference n 3))) (
t (cond ((eqcar z (quote if)) (progn (cond ((eqcar (cadr z) !@catch) (progn (
setq n (plus n 3)) (rplaca z (quote ifcatch))))) (setq r (set_stack (assoc (
cadr (caddr z)) w) r n)) (setq r (set_stack (assoc (cadr (cadddr z)) w) r n))
)) (t (cond ((eqcar z (quote go)) (setq r (set_stack (assoc (cadr z) w) r n))
)))))))))))))))))) (cond ((lessp n entry_stack) (error 1 
"Too many POPs in the codestream")) (t (cond ((greaterp n deepest) (setq 
deepest n))))))) (setq var1033 (cdr var1033)) (go lab1032))) (go lab1034)) (
setq args (setq stack (setq locals nil))) (cond ((fixp nargs) (progn (prog (i
) (setq i 1) lab1035 (cond ((minusp (times 1 (difference nargs i))) (return 
nil))) (setq stack (cons (gensym) stack)) (setq i (plus i 1)) (go lab1035)) (
setq args (reverse stack)))) (t (progn (prog (i) (setq i 1) lab1036 (cond ((
minusp (times 1 (difference (car nargs) i))) (return nil))) (setq stack (cons
(gensym) stack)) (setq i (plus i 1)) (go lab1036)) (setq args stack) (cond (
(not (equal (cadr nargs) (car nargs))) (progn (setq args (cons (quote 
!&optional) args)) (prog (i) (setq i (plus (car nargs) 1)) lab1037 (cond ((
minusp (times 1 (difference (cadr nargs) i))) (return nil))) (progn (setq w1 
(gensym)) (setq stack (cons w1 stack)) (cond ((equal (logand (caddr nargs) 1)
0) (setq args (cons w1 args))) (t (setq args (cons (list w1 (quote (quote 
!*spid!*))) args))))) (setq i (plus i 1)) (go lab1037)) (cond ((neq (logand (
caddr nargs) 2) 0) (progn (setq w1 (gensym)) (setq stack (cons w1 stack)) (
setq args (cons w1 (cons (quote !&rest) args))))))))) (setq args (reverse 
args))))) (setq locals (list !@a !@b !@w)) (prog (i) (setq i (plus 1 (length 
stack))) lab1038 (cond ((minusp (times 1 (difference deepest i))) (return nil
))) (setq locals (cons (gensym) locals)) (setq i (plus i 1)) (go lab1038)) (
prog (var1042) (setq var1042 w) lab1041 (cond ((null var1042) (return nil))) 
(prog (b) (setq b (car var1042)) (prog (m z1) (setq m (cadr b)) (cond ((not (
fixp m)) (error 1 "Unreferenced code block"))) (prog (var1040) (setq var1040 
(cddr b)) lab1039 (cond ((null var1040) (return nil))) (prog (z) (setq z (car
var1040)) (progn (cond ((equal z (quote push)) (setq m (plus m 1))) (t (cond
((equal z (quote lose)) (setq m (difference m 1))) (t (cond ((eqcar z (quote
freebind)) (setq m (plus m 2 (length (cadr z))))) (t (cond ((equal z (quote 
pvbind)) (setq m (plus m 2))) (t (cond ((eqcar z (quote freerstr)) (setq m (
difference (difference m 2) (length (cadr z))))) (t (cond ((equal z (quote 
pvrestore)) (setq m (difference m 2))) (t (cond ((or (equal z (quote uncatch)
) (equal z (quote unprotect))) (setq m (difference m 3))) (t (progn (setq z1 
(stackref z m stack locals entry_stack)) (rplaca z (car z1)) (rplacd z (cdr 
z1))))))))))))))))))) (setq var1040 (cdr var1040)) (go lab1039)))) (setq 
var1042 (cdr var1042)) (go lab1041)) (setq w (fix_free_bindings w)) (setq w (
optimise_blocks w stack locals)) (setq r (cons (quote prog) (cons locals (
flowgraph_to_lisp w)))) (terpri) (princ "=> ") (prettyprint r) (setq w (
errorset (list (quote structchk) (mkquote r)) t t)) (cond ((not (atom w)) (
setq r (car w)))) (setq r (list (quote de) name args r)) (terpri) (princ 
"Finally: ") (prettyprint r) (return nil)))

(de flowgraph_to_lisp (w) (prog (r) (prog (var1046) (setq var1046 w) lab1045 
(cond ((null var1046) (return nil))) (prog (i) (setq i (car var1046)) (progn 
(setq r (cons (car i) r)) (prog (var1044) (setq var1044 (cddr i)) lab1043 (
cond ((null var1044) (return nil))) (prog (j) (setq j (car var1044)) (progn (
cond ((eqcar j (quote prog)) (setq r (cons (cons (quote prog) (cons (cadr j) 
(flowgraph_to_lisp (cddr j)))) r))) (t (cond ((eqcar j (quote if)) (setq r (
cons (list (quote cond) (list (cadr j) (caddr j)) (list (quote t) (cadddr j))
) r))) (t (cond ((or (eqcar j (quote freerstr)) (eqcar j (quote progexits))) 
nil) (t (cond ((not (member j (quote (push lose)))) (setq r (cons j r))))))))
)))) (setq var1044 (cdr var1044)) (go lab1043)))) (setq var1046 (cdr var1046)
) (go lab1045)) (return (reversip r))))

(de set_stack (block r n) (cond ((null (cadr block)) (progn (rplaca (cdr 
block) n) (cons (car block) r))) (t (cond ((not (equal (cadr block) n)) (
progn (printc "++++ Stack confusion") (prin n) (princ " vs. ") (print block) 
r)) (t r)))))

(de set_bind (block r n) (cond ((null (cadr block)) (progn (rplaca (cdr block
) (list n)) (cons (car block) r))) (t (cond ((not (equal (caadr block) n)) (
progn (printc "++++ Binding confusion") (prin n) (princ " vs. ") (print block
) r)) (t r)))))

(de stackref (u m stack locals entry_stack) (cond ((or (atom u) (eqcar u (
quote quote))) u) (t (cond ((eqcar u !@stack) (prog (n x) (setq n (cadr u)) (
setq x (plus (difference n m) entry_stack)) (cond ((geq x 0) (progn (cond ((
geq x entry_stack) (error 1 "Reference outside stack-frame"))) (prog (i) (
setq i 1) lab1047 (cond ((minusp (times 1 (difference x i))) (return nil))) (
setq stack (cdr stack)) (setq i (plus i 1)) (go lab1047)) (return (car stack)
))) (t (progn (prog (i) (setq i 1) lab1048 (cond ((minusp (times 1 (
difference (minus (plus x 1)) i))) (return nil))) (setq locals (cdr locals)) 
(setq i (plus i 1)) (go lab1048)) (return (car locals))))))) (t (prog (
var1050 var1051) (setq var1050 u) lab1049 (cond ((null var1050) (return (
reversip var1051)))) (prog (x) (setq x (car var1050)) (setq var1051 (cons (
stackref x m stack locals entry_stack) var1051))) (setq var1050 (cdr var1050)
) (go lab1049)))))))

(setq opnames (mkvect 255))



(setq s!:opcodelist (quote (LOADLOC LOADLOC0 LOADLOC1 LOADLOC2 LOADLOC3 
LOADLOC4 LOADLOC5 LOADLOC6 LOADLOC7 LOADLOC8 LOADLOC9 LOADLOC10 LOADLOC11 
LOC0LOC1 LOC1LOC2 LOC2LOC3 LOC1LOC0 LOC2LOC1 LOC3LOC2 VNIL LOADLIT LOADLIT1 
LOADLIT2 LOADLIT3 LOADLIT4 LOADLIT5 LOADLIT6 LOADLIT7 LOADFREE LOADFREE1 
LOADFREE2 LOADFREE3 LOADFREE4 STORELOC STORELOC0 STORELOC1 STORELOC2 
STORELOC3 STORELOC4 STORELOC5 STORELOC6 STORELOC7 STOREFREE STOREFREE1 
STOREFREE2 STOREFREE3 LOADLEX STORELEX CLOSURE CARLOC0 CARLOC1 CARLOC2 
CARLOC3 CARLOC4 CARLOC5 CARLOC6 CARLOC7 CARLOC8 CARLOC9 CARLOC10 CARLOC11 
CDRLOC0 CDRLOC1 CDRLOC2 CDRLOC3 CDRLOC4 CDRLOC5 CAARLOC0 CAARLOC1 CAARLOC2 
CAARLOC3 CALL0 CALL1 CALL2 CALL2R CALL3 CALLN CALL0_0 CALL0_1 CALL0_2 CALL0_3
CALL1_0 CALL1_1 CALL1_2 CALL1_3 CALL1_4 CALL1_5 CALL2_0 CALL2_1 CALL2_2 
CALL2_3 CALL2_4 BUILTIN0 BUILTIN1 BUILTIN2 BUILTIN2R BUILTIN3 APPLY1 APPLY2 
APPLY3 APPLY4 JCALL JCALLN JUMP JUMP_B JUMP_L JUMP_BL JUMPNIL JUMPNIL_B 
JUMPNIL_L JUMPNIL_BL JUMPT JUMPT_B JUMPT_L JUMPT_BL JUMPATOM JUMPATOM_B 
JUMPATOM_L JUMPATOM_BL JUMPNATOM JUMPNATOM_B JUMPNATOM_L JUMPNATOM_BL JUMPEQ 
JUMPEQ_B JUMPEQ_L JUMPEQ_BL JUMPNE JUMPNE_B JUMPNE_L JUMPNE_BL JUMPEQUAL 
JUMPEQUAL_B JUMPEQUAL_L JUMPEQUAL_BL JUMPNEQUAL JUMPNEQUAL_B JUMPNEQUAL_L 
JUMPNEQUAL_BL JUMPL0NIL JUMPL0T JUMPL1NIL JUMPL1T JUMPL2NIL JUMPL2T JUMPL3NIL
JUMPL3T JUMPL4NIL JUMPL4T JUMPST0NIL JUMPST0T JUMPST1NIL JUMPST1T JUMPST2NIL
JUMPST2T JUMPL0ATOM JUMPL0NATOM JUMPL1ATOM JUMPL1NATOM JUMPL2ATOM 
JUMPL2NATOM JUMPL3ATOM JUMPL3NATOM JUMPFREE1NIL JUMPFREE1T JUMPFREE2NIL 
JUMPFREE2T JUMPFREE3NIL JUMPFREE3T JUMPFREE4NIL JUMPFREE4T JUMPFREENIL 
JUMPFREET JUMPLIT1EQ JUMPLIT1NE JUMPLIT2EQ JUMPLIT2NE JUMPLIT3EQ JUMPLIT3NE 
JUMPLIT4EQ JUMPLIT4NE JUMPLITEQ JUMPLITNE JUMPB1NIL JUMPB1T JUMPB2NIL JUMPB2T
JUMPFLAGP JUMPNFLAGP JUMPEQCAR JUMPNEQCAR CATCH CATCH_B CATCH_L CATCH_BL 
UNCATCH THROW PROTECT UNPROTECT PVBIND PVRESTORE FREEBIND FREERSTR EXIT 
NILEXIT LOC0EXIT LOC1EXIT LOC2EXIT PUSH PUSHNIL PUSHNIL2 PUSHNIL3 PUSHNILS 
POP LOSE LOSE2 LOSE3 LOSES SWOP EQ EQCAR EQUAL NUMBERP CAR CDR CAAR CADR CDAR
CDDR CONS NCONS XCONS ACONS LENGTH LIST2 LIST2STAR LIST3 PLUS2 ADD1 
DIFFERENCE SUB1 TIMES2 GREATERP LESSP FLAGP GET LITGET GETV QGETV QGETVN 
BIGSTACK BIGCALL ICASE FASTGET SPARE1 SPARE2)))

(prog (w) (setq w s!:opcodelist) (prog (i) (setq i 0) lab1054 (cond ((minusp 
(times 1 (difference 255 i))) (return nil))) (progn (putv opnames i (compress
(cons (quote h) (cons (quote !!) (cons (quote !:) (explode (car w))))))) (
setq w (cdr w))) (setq i (plus i 1)) (go lab1054)))

(global (quote (builtin0 builtin1 builtin2 builtin3)))

(setq builtin0 (mkvect 255))

(setq builtin1 (mkvect 255))

(setq builtin2 (mkvect 255))

(setq builtin3 (mkvect 255))

(prog (var1056) (setq var1056 (oblist)) lab1055 (cond ((null var1056) (return
nil))) (prog (x) (setq x (car var1056)) (prog (w) (cond ((setq w (get x (
quote s!:builtin0))) (putv builtin0 w x)) (t (cond ((setq w (get x (quote 
s!:builtin1))) (putv builtin1 w x)) (t (cond ((setq w (get x (quote 
s!:builtin2))) (putv builtin2 w x)) (t (cond ((setq w (get x (quote 
s!:builtin3))) (putv builtin3 w x))))))))))) (setq var1056 (cdr var1056)) (go
lab1055))

(off echo)

(de byte1 nil (bps!-getv code pc))

(de byte2 nil (bps!-getv code (plus pc 1)))

(de twobytes nil (plus (times 256 (byte1)) (byte2)))

(de makeif (why loc) (list (quote if) why loc (list (quote go) (gensym))))

(de jumpto (x) (setq all_jumps (list all_jumps x)))

(de jumpop (why) (list 2 (makeif why (jumpto (plus pc (byte1) 1)))))

(de jumpopb (why) (list 2 (makeif why (jumpto (plus (difference pc (byte1)) 1
)))))

(de jumpopl (why) (list 3 (makeif why (jumpto (plus pc (twobytes) 1)))))

(de jumpopbl (why) (list 3 (makeif why (jumpto (plus (difference pc (twobytes
)) 1)))))

(progn (de h!:LOADLOC (pc code env) (list 2 (list (quote setq) !@b !@a) (list
(quote setq) !@a (list !@stack (byte1))))) (de h!:LOADLOC0 (pc code env) (
list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 0))))
(de h!:LOADLOC1 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (
quote setq) !@a (list !@stack 1)))) (de h!:LOADLOC2 (pc code env) (list 1 (
list (quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 2)))) (de 
h!:LOADLOC3 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list !@stack 3)))) (de h!:LOADLOC4 (pc code env) (list 1 (list (
quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 4)))) (de 
h!:LOADLOC5 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list !@stack 5)))) (de h!:LOADLOC6 (pc code env) (list 1 (list (
quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 6)))) (de 
h!:LOADLOC7 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list !@stack 7)))) (de h!:LOADLOC8 (pc code env) (list 1 (list (
quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 8)))) (de 
h!:LOADLOC9 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list !@stack 9)))) (de h!:LOADLOC10 (pc code env) (list 1 (list (
quote setq) !@b !@a) (list (quote setq) !@a (list !@stack 10)))) (de 
h!:LOADLOC11 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list !@stack 11)))) (de h!:LOC0LOC1 (pc code env) (list 1 (list (
quote setq) !@b (list !@stack 0)) (list (quote setq) !@a (list !@stack 1)))) 
(de h!:LOC1LOC2 (pc code env) (list 1 (list (quote setq) !@b (list !@stack 1)
) (list (quote setq) !@a (list !@stack 2)))) (de h!:LOC2LOC3 (pc code env) (
list 1 (list (quote setq) !@b (list !@stack 2)) (list (quote setq) !@a (list 
!@stack 3)))) (de h!:LOC1LOC0 (pc code env) (list 1 (list (quote setq) !@b (
list !@stack 1)) (list (quote setq) !@a (list !@stack 1)))) (de h!:LOC2LOC1 (
pc code env) (list 1 (list (quote setq) !@b (list !@stack 2)) (list (quote 
setq) !@a (list !@stack 1)))) (de h!:LOC3LOC2 (pc code env) (list 1 (list (
quote setq) !@b (list !@stack 3)) (list (quote setq) !@a (list !@stack 2)))) 
(de h!:VNIL (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a nil))) (de freeref (env n) (cond ((or (lessp n 0) (greaterp n (upbv
env))) (error 1 "free variable (etc) reference failure")) (t (getv env n))))
(de litref (env n) (cond ((or (lessp n 0) (greaterp n (upbv env))) (error 1 
"literal reference failure")) (t (mkquote (getv env n))))) (de h!:LOADLIT (pc
code env) (list 2 (list (quote setq) !@b !@a) (list (quote setq) !@a (litref
env (byte1))))) (de h!:LOADLIT1 (pc code env) (list 1 (list (quote setq) !@b
!@a) (list (quote setq) !@a (litref env 1)))) (de h!:LOADLIT2 (pc code env) 
(list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (litref env 2)))) 
(de h!:LOADLIT3 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (
quote setq) !@a (litref env 3)))) (de h!:LOADLIT4 (pc code env) (list 1 (list
(quote setq) !@b !@a) (list (quote setq) !@a (litref env 4)))) (de 
h!:LOADLIT5 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (litref env 5)))) (de h!:LOADLIT6 (pc code env) (list 1 (list (
quote setq) !@b !@a) (list (quote setq) !@a (litref env 6)))) (de h!:LOADLIT7
(pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (
litref env 7)))) (de h!:LOADFREE (pc code env) (list 2 (list (quote setq) !@b
!@a) (list (quote setq) !@a (freeref env (byte1))))) (de h!:LOADFREE1 (pc 
code env) (list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (freeref
env 1)))) (de h!:LOADFREE2 (pc code env) (list 1 (list (quote setq) !@b !@a)
(list (quote setq) !@a (freeref env 2)))) (de h!:LOADFREE3 (pc code env) (
list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (freeref env 3)))) 
(de h!:LOADFREE4 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (
quote setq) !@a (freeref env 4)))) (de h!:STORELOC (pc code env) (list 2 (
list (quote setq) (list !@stack (byte1)) !@a))) (de h!:STORELOC0 (pc code env
) (list 1 (list (quote setq) (list !@stack 0) !@a))) (de h!:STORELOC1 (pc 
code env) (list 1 (list (quote setq) (list !@stack 1) !@a))) (de h!:STORELOC2
(pc code env) (list 1 (list (quote setq) (list !@stack 2) !@a))) (de 
h!:STORELOC3 (pc code env) (list 1 (list (quote setq) (list !@stack 3) !@a)))
(de h!:STORELOC4 (pc code env) (list 1 (list (quote setq) (list !@stack 4) 
!@a))) (de h!:STORELOC5 (pc code env) (list 1 (list (quote setq) (list 
!@stack 5) !@a))) (de h!:STORELOC6 (pc code env) (list 1 (list (quote setq) (
list !@stack 6) !@a))) (de h!:STORELOC7 (pc code env) (list 1 (list (quote 
setq) (list !@stack 7) !@a))) (de h!:STOREFREE (pc code env) (list 2 (list (
quote setq) (freeref env (byte1)) !@a))) (de h!:STOREFREE1 (pc code env) (
list 1 (list (quote setq) (freeref env 1) !@a))) (de h!:STOREFREE2 (pc code 
env) (list 1 (list (quote setq) (freeref env 2) !@a))) (de h!:STOREFREE3 (pc 
code env) (list 1 (list (quote setq) (freeref env 3) !@a))) (de h!:LOADLEX (
pc code env) (prog nil (error 1 "loadlex") (return (list 3 (quote loadlex))))
) (de h!:STORELEX (pc code env) (prog nil (error 1 "storelex") (return (list 
3 (quote storelex))))) (de h!:CLOSURE (pc code env) (prog nil (error 1 
"closure") (return (list 2 (quote closure))))) (de h!:CARLOC0 (pc code env) (
list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote car) (
list !@stack 0))))) (de h!:CARLOC1 (pc code env) (list 1 (list (quote setq) 
!@b !@a) (list (quote setq) !@a (list (quote car) (list !@stack 1))))) (de 
h!:CARLOC2 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote car) (list !@stack 2))))) (de h!:CARLOC3 (pc code env)
(list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote car)
(list !@stack 3))))) (de h!:CARLOC4 (pc code env) (list 1 (list (quote setq)
!@b !@a) (list (quote setq) !@a (list (quote car) (list !@stack 4))))) (de 
h!:CARLOC5 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote car) (list !@stack 5))))) (de h!:CARLOC6 (pc code env)
(list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote car)
(list !@stack 6))))) (de h!:CARLOC7 (pc code env) (list 1 (list (quote setq)
!@b !@a) (list (quote setq) !@a (list (quote car) (list !@stack 7))))) (de 
h!:CARLOC8 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote car) (list !@stack 8))))) (de h!:CARLOC9 (pc code env)
(list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote car)
(list !@stack 9))))) (de h!:CARLOC10 (pc code env) (list 1 (list (quote setq
) !@b !@a) (list (quote setq) !@a (list (quote car) (list !@stack 10))))) (de
h!:CARLOC11 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote car) (list !@stack 11))))) (de h!:CDRLOC0 (pc code env
) (list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote cdr
) (list !@stack 0))))) (de h!:CDRLOC1 (pc code env) (list 1 (list (quote setq
) !@b !@a) (list (quote setq) !@a (list (quote cdr) (list !@stack 1))))) (de 
h!:CDRLOC2 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote cdr) (list !@stack 2))))) (de h!:CDRLOC3 (pc code env)
(list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote cdr)
(list !@stack 3))))) (de h!:CDRLOC4 (pc code env) (list 1 (list (quote setq)
!@b !@a) (list (quote setq) !@a (list (quote cdr) (list !@stack 4))))) (de 
h!:CDRLOC5 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (quote 
setq) !@a (list (quote cdr) (list !@stack 5))))) (de h!:CAARLOC0 (pc code env
) (list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (quote 
caar) (list !@stack 0))))) (de h!:CAARLOC1 (pc code env) (list 1 (list (quote
setq) !@b !@a) (list (quote setq) !@a (list (quote caar) (list !@stack 1))))
) (de h!:CAARLOC2 (pc code env) (list 1 (list (quote setq) !@b !@a) (list (
quote setq) !@a (list (quote caar) (list !@stack 2))))) (de h!:CAARLOC3 (pc 
code env) (list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (
quote car) (list !@stack 3))))) (de h!:CALL0 (pc code env) (list 2 (list (
quote setq) !@b !@a) (list (quote setq) !@a (list (freeref env (byte1)))))) (
de h!:CALL1 (pc code env) (list 2 (list (quote setq) !@a (list (freeref env (
byte1)) !@a)))) (de h!:CALL2 (pc code env) (list 2 (list (quote setq) !@a (
list (freeref env (byte1)) !@b !@a)))) (de h!:CALL2R (pc code env) (list 2 (
list (quote setq) !@a (list (freeref env (byte1)) !@a !@b)))) (de h!:CALL3 (
pc code env) (list 2 (list (quote setq) !@a (expand_call 3 (freeref env (
byte1)))) (quote lose))) (de h!:CALLN (pc code env) (prog (n w) (setq n (
byte1)) (prog (i) (setq i 1) lab1057 (cond ((minusp (times 1 (difference (
difference n 2) i))) (return nil))) (setq w (cons (quote lose) w)) (setq i (
plus i 1)) (go lab1057)) (return (list!* 3 (list (quote setq) !@a (
expand_call n (freeref env (byte2)))) w)))) (de h!:CALL0_0 (pc code env) (
list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (freeref env 
0))))) (de h!:CALL0_1 (pc code env) (list 1 (list (quote setq) !@b !@a) (list
(quote setq) !@a (list (freeref env 1))))) (de h!:CALL0_2 (pc code env) (
list 1 (list (quote setq) !@b !@a) (list (quote setq) !@a (list (freeref env 
2))))) (de h!:CALL0_3 (pc code env) (list 1 (list (quote setq) !@b !@a) (list
(quote setq) !@a (list (freeref env 3))))) (de h!:CALL1_0 (pc code env) (
list 1 (list (quote setq) !@a (list (freeref env 0) !@a)))) (de h!:CALL1_1 (
pc code env) (list 1 (list (quote setq) !@a (list (freeref env 1) !@a)))) (de
h!:CALL1_2 (pc code env) (list 1 (list (quote setq) !@a (list (freeref env 2
) !@a)))) (de h!:CALL1_3 (pc code env) (list 1 (list (quote setq) !@a (list (
freeref env 3) !@a)))) (de h!:CALL1_4 (pc code env) (list 1 (list (quote setq
) !@a (list (freeref env 4) !@a)))) (de h!:CALL1_5 (pc code env) (list 1 (
list (quote setq) !@a (list (freeref env 5) !@a)))) (de h!:CALL2_0 (pc code 
env) (list 1 (list (quote setq) !@a (list (freeref env 0) !@b !@a)))) (de 
h!:CALL2_1 (pc code env) (list 1 (list (quote setq) !@a (list (freeref env 1)
!@b !@a)))) (de h!:CALL2_2 (pc code env) (list 1 (list (quote setq) !@a (
list (freeref env 2) !@b !@a)))) (de h!:CALL2_3 (pc code env) (list 1 (list (
quote setq) !@a (list (freeref env 3) !@b !@a)))) (de h!:CALL2_4 (pc code env
) (list 1 (list (quote setq) !@a (list (freeref env 4) !@b !@a)))) (de 
h!:BUILTIN0 (pc code env) (prog (w) (setq w (getv builtin0 (byte1))) (cond ((
null w) (error 1 "Invalid builtin-function specifier"))) (return (list 2 (
list (quote setq) !@a (list w)))))) (de h!:BUILTIN1 (pc code env) (prog (w) (
setq w (getv builtin1 (byte1))) (cond ((null w) (error 1 
"Invalid builtin-function specifier"))) (return (list 2 (list (quote setq) 
!@a (list w !@a)))))) (de h!:BUILTIN2 (pc code env) (prog (w) (setq w (getv 
builtin2 (byte1))) (cond ((null w) (error 1 
"Invalid builtin-function specifier"))) (return (list 2 (list (quote setq) 
!@a (list w !@b !@a)))))) (de h!:BUILTIN2R (pc code env) (prog (w) (setq w (
getv builtin2 (byte1))) (cond ((null w) (error 1 
"Invalid builtin-function specifier"))) (return (list 2 (list (quote setq) 
!@a (list w !@a !@b)))))) (de h!:BUILTIN3 (pc code env) (prog (w) (setq w (
getv builtin3 (byte1))) (cond ((null w) (error 1 
"Invalid builtin-function specifier"))) (return (list 2 (list (quote setq) 
!@a (expand_call 3 w)) (quote lose))))) (de h!:APPLY1 (pc code env) (list 1 (
list (quote setq) !@a (list (quote apply) !@b !@a)))) (de h!:APPLY2 (pc code 
env) (list 1 (list (quote setq) !@a (list (quote apply) (list !@stack 0) !@b 
!@a)) (quote lose))) (de h!:APPLY3 (pc code env) (list 1 (list (quote setq) 
!@a (list (quote apply) (list !@stack 0) (list !@stack 1) !@b !@a)) (quote 
lose) (quote lose))) (de h!:APPLY4 (pc code env) (list 1 (list (quote setq) 
!@a (list (quote apply) (list !@stack 0) (list !@stack 1) (list !@stack 2) 
!@b !@a)) (quote lose) (quote lose) (quote lose))) (de h!:JCALL (pc code env)
(prog (nargs dest) (setq nargs (byte1)) (setq dest (freeref env (logand 
nargs 31))) (setq nargs (irightshift nargs 5)) (return (list 2 (expand_jcall 
nargs dest))))) (de h!:JCALLN (pc code env) (list 3 (expand_jcall (byte2) (
freeref env (byte1))))) (de expand_jcall (nargs dest) (list (quote return) (
expand_call nargs dest))) (de expand_call (nargs dest) (cond ((equal nargs 0)
(list dest)) (t (cond ((equal nargs 1) (list dest !@a)) (t (cond ((equal 
nargs 2) (list dest !@b !@a)) (t (prog (w) (setq w (list !@b !@a)) (prog (i) 
(setq i 1) lab1058 (cond ((minusp (times 1 (difference (difference nargs 2) i
))) (return nil))) (setq w (cons (list !@stack i) w)) (setq i (plus i 1)) (go
lab1058)) (return (cons dest w)))))))))) (de h!:JUMP (pc code env) (list 2 (
jumpto (plus pc (byte1) 1)))) (de h!:JUMP_B (pc code env) (list 2 (jumpto (
plus (difference pc (byte1)) 1)))) (de h!:JUMP_L (pc code env) (list 3 (
jumpto (plus pc (twobytes) 1)))) (de h!:JUMP_BL (pc code env) (list 3 (jumpto
(plus (difference pc (twobytes)) 1)))) (de h!:JUMPNIL (pc code env) (jumpop 
(list (quote null) !@a))) (de h!:JUMPNIL_B (pc code env) (jumpopb (list (
quote null) !@a))) (de h!:JUMPNIL_L (pc code env) (jumpopl (list (quote null)
!@a))) (de h!:JUMPNIL_BL (pc code env) (jumpopbl (list (quote null) !@a))) (
de h!:JUMPT (pc code env) (jumpop !@a)) (de h!:JUMPT_B (pc code env) (jumpopb
!@a)) (de h!:JUMPT_L (pc code env) (jumpopl !@a)) (de h!:JUMPT_BL (pc code 
env) (jumpopbl !@a)) (de h!:JUMPATOM (pc code env) (jumpop (list (quote atom)
!@a))) (de h!:JUMPATOM_B (pc code env) (jumpopb (list (quote atom) !@a))) (
de h!:JUMPATOM_L (pc code env) (jumpopl (list (quote atom) !@a))) (de 
h!:JUMPATOM_BL (pc code env) (jumpopbl (list (quote atom) !@a))) (de 
h!:JUMPNATOM (pc code env) (jumpop (list (quote not) (list (quote atom) !@a))
)) (de h!:JUMPNATOM_B (pc code env) (jumpopb (list (quote not) (list (quote 
atom) !@a)))) (de h!:JUMPNATOM_L (pc code env) (jumpopl (list (quote not) (
list (quote atom) !@a)))) (de h!:JUMPNATOM_BL (pc code env) (jumpopbl (list (
quote not) (list (quote atom) !@a)))) (de h!:JUMPEQ (pc code env) (jumpop (
list (quote eq) !@b !@a))) (de h!:JUMPEQ_B (pc code env) (jumpopb (list (
quote eq) !@b !@a))) (de h!:JUMPEQ_L (pc code env) (jumpopl (list (quote eq) 
!@b !@a))) (de h!:JUMPEQ_BL (pc code env) (jumpopbl (list (quote eq) !@b !@a)
)) (de h!:JUMPNE (pc code env) (jumpop (list (quote not) (list (quote eq) !@b
!@a)))) (de h!:JUMPNE_B (pc code env) (jumpopb (list (quote not) (list (
quote eq) !@b !@a)))) (de h!:JUMPNE_L (pc code env) (jumpopl (list (quote not
) (list (quote eq) !@b !@a)))) (de h!:JUMPNE_BL (pc code env) (jumpopbl (list
(quote not) (list (quote eq) !@b !@a)))) (de h!:JUMPEQUAL (pc code env) (
jumpop (list (quote equal) !@b !@a))) (de h!:JUMPEQUAL_B (pc code env) (
jumpopb (list (quote equal) !@b !@a))) (de h!:JUMPEQUAL_L (pc code env) (
jumpopl (list (quote equal) !@b !@a))) (de h!:JUMPEQUAL_BL (pc code env) (
jumpopbl (list (quote equal) !@b !@a))) (de h!:JUMPNEQUAL (pc code env) (
jumpop (list (quote not) (list (quote equal) !@b !@a)))) (de h!:JUMPNEQUAL_B 
(pc code env) (jumpopb (list (quote not) (list (quote equal) !@b !@a)))) (de 
h!:JUMPNEQUAL_L (pc code env) (jumpopl (list (quote not) (list (quote equal) 
!@b !@a)))) (de h!:JUMPNEQUAL_BL (pc code env) (jumpopbl (list (quote not) (
list (quote equal) !@b !@a)))) (de h!:JUMPL0NIL (pc code env) (jumpop (list (
quote null) (list !@stack 0)))) (de h!:JUMPL0T (pc code env) (jumpop (list 
!@stack 0))) (de h!:JUMPL1NIL (pc code env) (jumpop (list (quote null) (list 
!@stack 1)))) (de h!:JUMPL1T (pc code env) (jumpop (list !@stack 1))) (de 
h!:JUMPL2NIL (pc code env) (jumpop (list (quote null) (list !@stack 2)))) (de
h!:JUMPL2T (pc code env) (jumpop (list !@stack 2))) (de h!:JUMPL3NIL (pc 
code env) (jumpop (list (quote null) (list !@stack 3)))) (de h!:JUMPL3T (pc 
code env) (jumpop (list !@stack 3))) (de h!:JUMPL4NIL (pc code env) (jumpop (
list (quote null) (list !@stack 4)))) (de h!:JUMPL4T (pc code env) (jumpop (
list !@stack 4))) (de h!:JUMPST0NIL (pc code env) (jumpop (list (quote null) 
(list (quote setq) (list !@stack 0) !@a)))) (de h!:JUMPST0T (pc code env) (
jumpop (list (quote setq) (list !@stack 0) !@a))) (de h!:JUMPST1NIL (pc code 
env) (jumpop (list (quote null) (list (quote setq) (list !@stack 1) !@a)))) (
de h!:JUMPST1T (pc code env) (jumpop (list (quote setq) (list !@stack 1) !@a)
)) (de h!:JUMPST2NIL (pc code env) (jumpop (list (quote null) (list (quote 
setq) (list !@stack 2) !@a)))) (de h!:JUMPST2T (pc code env) (jumpop (list (
quote setq) (list !@stack 2) !@a))) (de h!:JUMPL0ATOM (pc code env) (jumpop (
list (quote atom) (list !@stack 0)))) (de h!:JUMPL0NATOM (pc code env) (
jumpop (list (quote not) (list (quote atom) (list !@stack 0))))) (de 
h!:JUMPL1ATOM (pc code env) (jumpop (list (quote atom) (list !@stack 1)))) (
de h!:JUMPL1NATOM (pc code env) (jumpop (list (quote not) (list (quote atom) 
(list !@stack 1))))) (de h!:JUMPL2ATOM (pc code env) (jumpop (list (quote 
atom) (list !@stack 2)))) (de h!:JUMPL2NATOM (pc code env) (jumpop (list (
quote not) (list (quote atom) (list !@stack 2))))) (de h!:JUMPL3ATOM (pc code
env) (jumpop (list (quote atom) (list !@stack 3)))) (de h!:JUMPL3NATOM (pc 
code env) (jumpop (list (quote not) (list (quote atom) (list !@stack 3))))) (
de h!:JUMPFREE1NIL (pc code env) (jumpop (list (quote null) (freeref env 1)))
) (de h!:JUMPFREE1T (pc code env) (jumpop (freeref env 1))) (de 
h!:JUMPFREE2NIL (pc code env) (jumpop (list (quote null) (freeref env 2)))) (
de h!:JUMPFREE2T (pc code env) (jumpop (freeref env 2))) (de h!:JUMPFREE3NIL 
(pc code env) (jumpop (list (quote null) (freeref env 3)))) (de h!:JUMPFREE3T
(pc code env) (jumpop (freeref env 3))) (de h!:JUMPFREE4NIL (pc code env) (
jumpop (list (quote null) (freeref env 4)))) (de h!:JUMPFREE4T (pc code env) 
(jumpop (freeref env 4))) (de h!:JUMPFREENIL (pc code env) (list 3 (makeif (
list (quote null) (freeref env (byte1))) (jumpto (plus pc (byte2) 2))))) (de 
h!:JUMPFREET (pc code env) (list 3 (makeif (freeref env (byte1)) (jumpto (
plus pc (byte2) 2))))) (de h!:JUMPLIT1EQ (pc code env) (jumpop (list (quote 
eq) !@a (litref env 1)))) (de h!:JUMPLIT1NE (pc code env) (jumpop (list (
quote not) (list (quote eq) !@a (litref env 1))))) (de h!:JUMPLIT2EQ (pc code
env) (jumpop (list (quote eq) !@a (litref env 2)))) (de h!:JUMPLIT2NE (pc 
code env) (jumpop (list (quote not) (list (quote eq) !@a (litref env 1))))) (
de h!:JUMPLIT3EQ (pc code env) (jumpop (list (quote eq) !@a (litref env 3))))
(de h!:JUMPLIT3NE (pc code env) (jumpop (list (quote not) (list (quote eq) 
!@a (litref env 1))))) (de h!:JUMPLIT4EQ (pc code env) (jumpop (list (quote 
eq) !@a (litref env 4)))) (de h!:JUMPLIT4NE (pc code env) (jumpop (list (
quote not) (list (quote eq) !@a (litref env 1))))) (de h!:JUMPLITEQ (pc code 
env) (list 3 (makeif (list (quote eq) !@a (litref env (byte1))) (jumpto (plus
pc (byte2) 2))))) (de h!:JUMPLITNE (pc code env) (list 3 (makeif (list (
quote not) (list (quote eq) !@a (litref env (byte1)))) (jumpto (plus pc (
byte2) 2))))) (de h!:JUMPB1NIL (pc code env) (prog (w) (setq w (elt builtin1 
(byte1))) (cond ((null w) (error 1 "Bad in JUMPB1NIL"))) (return (list 3 (
makeif (list (quote null) (list w !@a)) (jumpto (plus pc (byte2) 2))))))) (de
h!:JUMPB1T (pc code env) (prog (w) (setq w (elt builtin1 (byte1))) (cond ((
null w) (error 1 "Bad in JUMPB1T"))) (return (list 3 (makeif (list w !@a) (
jumpto (plus pc (byte2) 2))))))) (de h!:JUMPB2NIL (pc code env) (prog (w) (
setq w (elt builtin2 (byte1))) (cond ((null w) (error 1 "Bad in JUMPB2NIL")))
(return (list 3 (makeif (list (quote null) (list w !@b !@a)) (jumpto (plus 
pc (byte2) 2))))))) (de h!:JUMPB2T (pc code env) (prog (w) (setq w (elt 
builtin2 (byte1))) (cond ((null w) (error 1 "Bad in JUMPB2T"))) (return (list
3 (makeif (list w !@b !@a) (jumpto (plus pc (byte2) 2))))))) (de 
h!:JUMPFLAGP (pc code env) (jumpop (list (quote flagp) !@b !@a))) (de 
h!:JUMPNFLAGP (pc code env) (jumpop (list (quote not) (list (quote flagp) !@b
!@a)))) (de h!:JUMPEQCAR (pc code env) (list 3 (makeif (list (quote eqcar) 
!@a (litref env (byte1))) (jumpto (plus pc (byte2) 2))))) (de h!:JUMPNEQCAR (
pc code env) (list 3 (makeif (list (quote not) (list (quote eqcar) !@a (
litref env (byte1)))) (jumpto (plus pc (byte2) 2))))) (de h!:CATCH (pc code 
env) (jumpop (list !@catch !@a))) (de h!:CATCH_B (pc code env) (jumpopb (list
!@catch !@a))) (de h!:CATCH_L (pc code env) (jumpopl (list !@catch !@a))) (
de h!:CATCH_BL (pc code env) (jumpopbl (list !@catch !@a))) (de h!:UNCATCH (
pc code env) (list 1 (quote uncatch) (jumpto pc))) (de h!:THROW (pc code env)
(quote (1 throw))) (de h!:PROTECT (pc code env) (list 1 (quote protect) (
jumpto pc))) (de h!:UNPROTECT (pc code env) (list 1 (quote unprotect) (jumpto
pc))) (de h!:PVBIND (pc code env) (list 1 (quote pvbind) (jumpto pc))) (de 
h!:PVRESTORE (pc code env) (list 1 (quote pvrestore) (jumpto pc))) (de 
vector_to_list (v) (cond ((not (vectorp v)) (error 1 
"Error in binding fluid variables")) (t (prog (r) (prog (i) (setq i 0) 
lab1059 (cond ((minusp (times 1 (difference (upbv v) i))) (return nil))) (
setq r (cons (getv v i) r)) (setq i (plus i 1)) (go lab1059)) (return (
reversip r)))))) (de h!:FREEBIND (pc code env) (list 2 (list (quote freebind)
(vector_to_list (freeref env (byte1)))) (jumpto (plus pc 1)))) (de 
h!:FREERSTR (pc code env) (list 1 (quote (freerstr !*)) (jumpto pc))) (de 
h!:EXIT (pc code env) (list 1 (list (quote return) !@a))) (de h!:NILEXIT (pc 
code env) (list 1 (list (quote return) nil))) (de h!:LOC0EXIT (pc code env) (
list 1 (list (quote return) (list !@stack 0)))) (de h!:LOC1EXIT (pc code env)
(list 1 (list (quote return) (list !@stack 1)))) (de h!:LOC2EXIT (pc code 
env) (list 1 (list (quote return) (list !@stack 2)))) (de h!:PUSH (pc code 
env) (list 1 (quote push) (list (quote setq) (list !@stack 0) !@a))) (de 
h!:PUSHNIL (pc code env) (list 1 (quote push) (list (quote setq) (list 
!@stack 0) nil))) (de h!:PUSHNIL2 (pc code env) (list 1 (quote push) (list (
quote setq) (list !@stack 0) nil) (quote push) (list (quote setq) (list 
!@stack 0) nil))) (de h!:PUSHNIL3 (pc code env) (list 1 (quote push) (list (
quote setq) (list !@stack 0) nil) (quote push) (list (quote setq) (list 
!@stack 0) nil) (quote push) (list (quote setq) (list !@stack 0) nil))) (de 
h!:PUSHNILS (pc code env) (prog (n w) (setq n (byte1)) (prog (i) (setq i 1) 
lab1060 (cond ((minusp (times 1 (difference n i))) (return nil))) (setq w (
cons (quote push) (cons (list (quote setq) (list !@stack 0) nil) w))) (setq i
(plus i 1)) (go lab1060)) (return (cons 2 w)))) (de h!:POP (pc code env) (
list 1 (list (quote setq) (list (quote !@stack) 0)) (quote lose))) (de 
h!:LOSE (pc code env) (quote (1 lose))) (de h!:LOSE2 (pc code env) (quote (1 
lose lose))) (de h!:LOSE3 (pc code env) (quote (1 lose lose lose))) (de 
h!:LOSES (pc code env) (prog (n w) (setq n (byte1)) (prog (i) (setq i 1) 
lab1061 (cond ((minusp (times 1 (difference n i))) (return nil))) (setq w (
cons (quote lose) w)) (setq i (plus i 1)) (go lab1061)) (return (cons 2 w))))
(de h!:SWOP (pc code env) (list 1 (list (quote setq) !@w !@a) (list (quote 
setq) !@a !@b) (list (quote setq) !@b !@w))) (de h!:EQ (pc code env) (list 1 
(list (quote setq) !@a (list (quote eq) !@b !@a)))) (de h!:EQCAR (pc code env
) (list 1 (list (quote setq) !@a (list (quote eqcar) !@b !@a)))) (de h!:EQUAL
(pc code env) (list 1 (list (quote setq) !@a (list (quote equal) !@b !@a))))
(de h!:NUMBERP (pc code env) (list 1 (list (quote setq) !@a (list (quote 
numberp) !@a)))) (de h!:CAR (pc code env) (list 1 (list (quote setq) !@a (
list (quote car) !@a)))) (de h!:CDR (pc code env) (list 1 (list (quote setq) 
!@a (list (quote cdr) !@a)))) (de h!:CAAR (pc code env) (list 1 (list (quote 
setq) !@a (list (quote caar) !@a)))) (de h!:CADR (pc code env) (list 1 (list 
(quote setq) !@a (list (quote cadr) !@a)))) (de h!:CDAR (pc code env) (list 1
(list (quote setq) !@a (list (quote cdar) !@a)))) (de h!:CDDR (pc code env) 
(list 1 (list (quote setq) !@a (list (quote cddr) !@a)))) (de h!:CONS (pc 
code env) (list 1 (list (quote setq) !@a (list (quote cons) !@b !@a)))) (de 
h!:NCONS (pc code env) (list 1 (list (quote setq) !@a (list (quote ncons) !@a
)))) (de h!:XCONS (pc code env) (list 1 (list (quote setq) !@a (list (quote 
cons) !@a !@b)))) (de h!:ACONS (pc code env) (list 1 (list (quote setq) !@a (
list (quote acons) !@b !@a (list !@stack 0))) (quote lose))) (de h!:LENGTH (
pc code env) (list 1 (list (quote setq) !@a (list (quote length) !@a)))) (de 
h!:LIST2 (pc code env) (list 1 (list (quote setq) !@a (list (quote list) !@b 
!@a)))) (de h!:LIST2STAR (pc code env) (list 1 (list (quote setq) !@a (list (
quote list!*) !@b !@a (list !@stack 0))) (quote lose))) (de h!:LIST3 (pc code
env) (list 1 (list (quote setq) !@a (list (quote list) !@b !@a (list !@stack
0))) (quote lose))) (de h!:PLUS2 (pc code env) (list 1 (list (quote setq) 
!@a (list (quote plus) !@b !@a)))) (de h!:ADD1 (pc code env) (list 1 (list (
quote setq) !@a (list (quote add1) !@a)))) (de h!:DIFFERENCE (pc code env) (
list 1 (list (quote setq) !@a (list (quote difference) !@b !@a)))) (de 
h!:SUB1 (pc code env) (list 1 (list (quote setq) !@a (list (quote sub1) !@a))
)) (de h!:TIMES2 (pc code env) (list 1 (list (quote setq) !@a (list (quote 
times) !@b !@a)))) (de h!:GREATERP (pc code env) (list 1 (list (quote setq) 
!@a (list (quote greaterp) !@b !@a)))) (de h!:LESSP (pc code env) (list 1 (
list (quote setq) !@a (list (quote lessp) !@b !@a)))) (de h!:FLAGP (pc code 
env) (list 1 (list (quote setq) !@a (list (quote flagp) !@b !@a)))) (de 
h!:GET (pc code env) (list 1 (list (quote setq) !@a (list (quote get) !@b !@a
)))) (de h!:LITGET (pc code env) (list 2 (list (quote setq) !@a (list (quote 
get) !@a (litref env (byte1)))))) (de h!:GETV (pc code env) (list 1 (list (
quote setq) !@a (list (quote getv) !@b !@a)))) (de h!:QGETV (pc code env) (
list 1 (list (quote setq) !@a (list (quote qgetv) !@b !@a)))) (de h!:QGETVN (
pc code env) (list 2 (list (quote setq) !@a (list (quote qgetv) !@a (byte1)))
)) (de h!:BIGSTACK (pc code env) (prog nil (error 1 "bigstack") (return (list
3 (quote bigstack))))) (de h!:BIGCALL (pc code env) (prog nil (error 1 
"bigcall") (return (list 3 (quote bigcall))))) (de h!:ICASE (pc code env) (
prog nil (error 1 "ICASE opcode found") (return (list (plus 4 (times 2 (byte1
))) (quote icase))))) (de h!:FASTGET (pc code env) (prog nil (error 1 
"fastget") (return (list 2 (quote fastget))))) (de h!:SPARE1 (pc code env) (
error 1 "Invalid (spare) opcode found in byte-stream")) (de h!:SPARE2 (pc 
code env) (error 1 "Invalid (spare) opcode found in byte-stream")) 
"All helper functions present")

(de find_freebind (x) (cond ((null x) nil) (t (cond ((eqcar (car x) (quote 
freebind)) x) (t (find_freebind (cdr x)))))))

(de find_freerstr (x) (cond ((null x) nil) (t (cond ((eqcar (car x) (quote 
freerstr)) x) (t (find_freerstr (cdr x)))))))

(de mark_restores (w lab) (prog (b) (setq b (assoc lab w)) (cond ((null b) (
error 1 "block not found"))) (cond ((cadr b) (return nil))) (rplaca (cdr b) t
) (cond ((find_freerstr (cddr b)) (return nil)) (t (cond ((find_freebind (
cddr b)) (return t))))) (prog nil lab1062 (cond ((null (not (atom (cdr b)))) 
(return nil))) (setq b (cdr b)) (go lab1062)) (setq b (car b)) (cond ((eqcar 
b (quote go)) (return (mark_restores w (cadr b)))) (t (cond ((eqcar b (quote 
if)) (progn (cond ((mark_restores w (cadr (caddr b))) (return t)) (t (return 
(mark_restores w (cadr (cadddr b)))))))) (t (cond ((eqcar b (quote progexits)
) (return (mark_several_restores w (cdr b)))) (t (return nil)))))))))

(de mark_several_restores (w l) (cond ((null l) nil) (t (cond ((mark_restores
w (car l)) t) (t (mark_several_restores w (cdr l)))))))

(de lift_free_binding (w fb) (prog (r1 r2 w1) (prog nil lab1063 (cond ((null 
w) (return nil))) (progn (setq w1 (cdr w)) (cond ((cadar w) (progn (rplaca (
cdar w) nil) (rplacd w r1) (setq r1 w))) (t (progn (rplacd w r2) (setq r2 w))
)) (setq w w1)) (go lab1063)) (setq r1 (reversip r1)) (rplaca fb (cons (quote
prog) (cons (cadar fb) r1))) (rplacd fb (list (cons (quote progexits) (
free_exits r1)))) (return (reversip r2))))

(de free_exits (b) (prog (r r1) (prog (var1066) (setq var1066 b) lab1065 (
cond ((null var1066) (return nil))) (prog (i) (setq i (car var1066)) (progn (
prog nil lab1064 (cond ((null (not (atom (cdr i)))) (return nil))) (setq i (
cdr i)) (go lab1064)) (setq i (car i)) (cond ((eqcar i (quote go)) (setq r (
union (cdr i) r))) (t (cond ((eqcar i (quote if)) (setq r (union (cdr (caddr 
i)) (union (cdr (cadddr i)) r)))) (t (cond ((eqcar i (quote progexits)) (setq
r (union (cdr i) r)))))))))) (setq var1066 (cdr var1066)) (go lab1065)) (
prog (var1068) (setq var1068 r) lab1067 (cond ((null var1068) (return nil))) 
(prog (i) (setq i (car var1068)) (cond ((null (assoc i b)) (setq r1 (cons i 
r1))))) (setq var1068 (cdr var1068)) (go lab1067)) (return r1)))

(de fix_free_bindings (w) (prog (changed aborted p fb) (setq changed t) (prog
nil lab1072 (cond ((null changed) (return nil))) (progn (setq changed nil) (
prog (var1070) (setq var1070 w) lab1069 (cond ((null var1070) (return nil))) 
(prog (z) (setq z (car var1070)) (rplaca (cdr z) nil)) (setq var1070 (cdr 
var1070)) (go lab1069)) (cond (aborted (setq p (cdr p))) (t (setq p w))) (
setq aborted nil) (prog nil lab1071 (cond ((null (and p (not (setq fb (
find_freebind (cddar p)))))) (return nil))) (setq p (cdr p)) (go lab1071)) (
cond (p (progn (setq changed t) (cond ((mark_restores w (cadr (cadr fb))) (
setq aborted t)) (t (setq w (lift_free_binding w fb)))))))) (go lab1072)) (
return w)))

(de optimise_blocks (w args locals) (prog (vars changed avail) (setq vars (
append args locals)) (prog (var1074) (setq var1074 w) lab1073 (cond ((null 
var1074) (return nil))) (prog (z) (setq z (car var1074)) (rplaca (cdr z) (
quote unknown))) (setq var1074 (cdr var1074)) (go lab1073)) (rplaca (cdar w) 
nil) (setq changed t) (prog nil lab1079 (cond ((null changed) (return nil))) 
(progn (setq changed nil) (prog (var1078) (setq var1078 w) lab1077 (cond ((
null var1078) (return nil))) (prog (z) (setq z (car var1078)) (progn (setq 
avail (cadr z)) (prog (var1076) (setq var1076 (cddr z)) lab1075 (cond ((null 
var1076) (return nil))) (prog (q) (setq q (car var1076)) (progn nil)) (setq 
var1076 (cdr var1076)) (go lab1075)))) (setq var1078 (cdr var1078)) (go 
lab1077))) (go lab1079)) (return w)))

(setq !*echo (setq !*plap t))

(de simple (x) (cond ((atom x) x) (t (cond ((null (cdr x)) (car x)) (t (
simple (cdr x)))))))

(fluid (quote (x y)))

(de mylast (x) (cond ((atom x) x) (t (cond ((null (cdr x)) (car x)) (t (
mylast (cdr x)))))))

(de test (a) (prog (x) (setq x (plus a a a)) (setq x (prog (y) (setq y (times
x x)) (print (list x y)) (return y))) (return (quotient x a))))

(unfluid (quote (x y)))

(setq !*plap nil)

(unbyte (quote simple))

(unbyte (quote mylast))

(unbyte (quote test))


% end of file
