
% RLISP to LISP converter. A C Norman 2004-2007


%
% This code may be used and modified, and redistributed in binary
% or source form, subject to the "CCL Public License", which should
% accompany it. This license is a variant on the BSD license, and thus
% permits use of code derived from this in either open and commercial
% projects: but it does require that updates to this code be made
% available back to the originators of the package.
% Before merging other code in with this or linking this code
% with other packages or libraries please check that the license terms
% of the other material are compatible with those of this.
%


(global (quote (!*fastvector !*unsafecar)))

(flag (quote (fastvector unsafecar)) (quote switch))

(fluid (quote (C_file L_file O_file L_contents Setup_name File_name)))

(dm c!:printf (u !&optional env) (list (quote c!:printf1) (cadr u) (cons (
quote list) (cddr u))))

(de c!:printf1 (fmt args) (prog (a c) (setq fmt (explode2 fmt)) (prog nil 
lab1000 (cond ((null fmt) (return nil))) (progn (setq c (car fmt)) (setq fmt 
(cdr fmt)) (cond ((and (equal c (quote !\)) (or (equal (car fmt) (quote !n)) 
(equal (car fmt) (quote !N)))) (progn (terpri) (setq fmt (cdr fmt)))) (t (
cond ((and (equal c (quote !\)) (or (equal (car fmt) (quote !q)) (equal (car 
fmt) (quote !Q)))) (progn (princ (quote !")) (setq fmt (cdr fmt)))) (t (cond 
((equal c (quote !%)) (progn (setq c (car fmt)) (cond ((null args) (setq a (
quote missing_arg))) (t (setq a (car args)))) (cond ((or (equal c (quote !v))
(equal c (quote !V))) (cond ((flagp a (quote c!:live_across_call)) (progn (
princ "stack[") (princ (minus (get a (quote c!:location)))) (princ "]"))) (t 
(princ a)))) (t (cond ((or (equal c (quote !c)) (equal c (quote !C))) (
c!:safeprin a)) (t (cond ((or (equal c (quote !a)) (equal c (quote !A))) (
prin a)) (t (cond ((or (equal c (quote !t)) (equal c (quote !T))) (ttab a)) (
t (princ a))))))))) (cond (args (setq args (cdr args)))) (setq fmt (cdr fmt))
)) (t (princ c)))))))) (go lab1000))))

(de c!:safeprin (x) (prog (a b) (setq a (explode x)) (prog nil lab1001 (cond 
((null a) (return nil))) (progn (cond ((and (eqcar a (quote !/)) b) (princ 
" "))) (princ (car a)) (setq b (eqcar a (quote !*))) (setq a (cdr a))) (go 
lab1001))))

(de c!:valid_fndef (args body) (cond ((or (memq (quote !&optional) args) (
memq (quote !&rest) args)) nil) (t (c!:valid_list body))))

(de c!:valid_list (x) (cond ((null x) t) (t (cond ((atom x) nil) (t (cond ((
not (c!:valid_expr (car x))) nil) (t (c!:valid_list (cdr x)))))))))

(de c!:valid_expr (x) (cond ((atom x) t) (t (cond ((not (atom (car x))) (
progn (cond ((not (c!:valid_list (cdr x))) nil) (t (cond ((not (eqcar (car x)
(quote lambda))) nil) (t (cond ((atom (cdar x)) nil) (t (c!:valid_fndef (
cadar x) (cddar x)))))))))) (t (cond ((not (idp (car x))) nil) (t (cond ((
eqcar x (quote quote)) t) (t (prog (h) (setq h (get (car x) (quote c!:valid))
) (cond ((null h) (return (c!:valid_list (cdr x))))) (return (funcall h (cdr 
x)))))))))))))

(de c!:cspecform (x env) (error 0 (list "special form" x)))

(de c!:valid_specform (x) nil)

(progn (put (quote and) (quote c!:code) (function c!:cspecform)) (put (quote 
catch) (quote c!:code) (function c!:cspecform)) (put (quote compiler!-let) (
quote c!:code) (function c!:cspecform)) (put (quote cond) (quote c!:code) (
function c!:cspecform)) (put (quote declare) (quote c!:code) (function 
c!:cspecform)) (put (quote de) (quote c!:code) (function c!:cspecform)) (put 
(quote eval!-when) (quote c!:code) (function c!:cspecform)) (put (quote flet)
(quote c!:code) (function c!:cspecform)) (put (quote function) (quote 
c!:code) (function c!:cspecform)) (put (quote go) (quote c!:code) (function 
c!:cspecform)) (put (quote if) (quote c!:code) (function c!:cspecform)) (put 
(quote labels) (quote c!:code) (function c!:cspecform)) (put (quote !~let) (
quote c!:code) (function c!:cspecform)) (put (quote let!*) (quote c!:code) (
function c!:cspecform)) (put (quote list) (quote c!:code) (function 
c!:cspecform)) (put (quote list!*) (quote c!:code) (function c!:cspecform)) (
put (quote macrolet) (quote c!:code) (function c!:cspecform)) (put (quote 
multiple!-value!-call) (quote c!:code) (function c!:cspecform)) (put (quote 
multiple!-value!-prog1) (quote c!:code) (function c!:cspecform)) (put (quote 
or) (quote c!:code) (function c!:cspecform)) (put (quote prog) (quote c!:code
) (function c!:cspecform)) (put (quote prog!*) (quote c!:code) (function 
c!:cspecform)) (put (quote prog1) (quote c!:code) (function c!:cspecform)) (
put (quote prog2) (quote c!:code) (function c!:cspecform)) (put (quote progn)
(quote c!:code) (function c!:cspecform)) (put (quote progv) (quote c!:code) 
(function c!:cspecform)) (put (quote quote) (quote c!:code) (function 
c!:cspecform)) (put (quote return) (quote c!:code) (function c!:cspecform)) (
put (quote return!-from) (quote c!:code) (function c!:cspecform)) (put (quote
setq) (quote c!:code) (function c!:cspecform)) (put (quote tagbody) (quote 
c!:code) (function c!:cspecform)) (put (quote the) (quote c!:code) (function 
c!:cspecform)) (put (quote throw) (quote c!:code) (function c!:cspecform)) (
put (quote unless) (quote c!:code) (function c!:cspecform)) (put (quote 
unwind!-protect) (quote c!:code) (function c!:cspecform)) (put (quote when) (
quote c!:code) (function c!:cspecform)) (put (quote catch) (quote c!:valid) (
function c!:valid_specform)) (put (quote compiler!-let) (quote c!:valid) (
function c!:valid_specform)) (put (quote cond) (quote c!:valid) (function 
c!:valid_specform)) (put (quote declare) (quote c!:valid) (function 
c!:valid_specform)) (put (quote de) (quote c!:valid) (function 
c!:valid_specform)) (put (quote eval!-when) (quote c!:valid) (function 
c!:valid_specform)) (put (quote flet) (quote c!:valid) (function 
c!:valid_specform)) (put (quote function) (quote c!:valid) (function 
c!:valid_specform)) (put (quote labels) (quote c!:valid) (function 
c!:valid_specform)) (put (quote !~let) (quote c!:valid) (function 
c!:valid_specform)) (put (quote let!*) (quote c!:valid) (function 
c!:valid_specform)) (put (quote macrolet) (quote c!:valid) (function 
c!:valid_specform)) (put (quote multiple!-value!-call) (quote c!:valid) (
function c!:valid_specform)) (put (quote multiple!-value!-prog1) (quote 
c!:valid) (function c!:valid_specform)) (put (quote prog) (quote c!:valid) (
function c!:valid_specform)) (put (quote prog!*) (quote c!:valid) (function 
c!:valid_specform)) (put (quote progv) (quote c!:valid) (function 
c!:valid_specform)) (put (quote quote) (quote c!:valid) (function 
c!:valid_specform)) (put (quote the) (quote c!:valid) (function 
c!:valid_specform)) (put (quote throw) (quote c!:valid) (function 
c!:valid_specform)) (put (quote unwind!-protect) (quote c!:valid) (function 
c!:valid_specform)))

(fluid (quote (current_procedure current_args current_block current_contents 
all_blocks registers stacklocs)))

(fluid (quote (available used)))

(setq available (setq used nil))

(de c!:reset_gensyms nil (progn (remflag used (quote c!:live_across_call)) (
remflag used (quote c!:visited)) (prog nil lab1002 (cond ((null used) (return
nil))) (progn (remprop (car used) (quote c!:contents)) (remprop (car used) (
quote c!:why)) (remprop (car used) (quote c!:where_to)) (remprop (car used) (
quote c!:count)) (remprop (car used) (quote c!:live)) (remprop (car used) (
quote c!:clash)) (remprop (car used) (quote c!:chosen)) (remprop (car used) (
quote c!:location)) (cond ((plist (car used)) (prog (o) (setq o (wrs nil)) (
princ "+++++ ") (prin (car used)) (princ " ") (prin (plist (car used))) (
terpri) (wrs o)))) (setq available (cons (car used) available)) (setq used (
cdr used))) (go lab1002))))

(de c!:my_gensym nil (prog (w) (cond (available (progn (setq w (car available
)) (setq available (cdr available)))) (t (setq w (gensym1 "v")))) (setq used 
(cons w used)) (cond ((plist w) (progn (princ "????? ") (prin w) (princ 
" => ") (prin (plist w)) (terpri)))) (return w)))

(de c!:newreg nil (prog (r) (setq r (c!:my_gensym)) (setq registers (cons r 
registers)) (return r)))

(de c!:startblock (s) (progn (setq current_block s) (setq current_contents 
nil)))

(de c!:outop (a b c d) (cond (current_block (setq current_contents (cons (
list a b c d) current_contents)))))

(de c!:endblock (why where_to) (cond (current_block (progn (put current_block
(quote c!:contents) current_contents) (put current_block (quote c!:why) why)
(put current_block (quote c!:where_to) where_to) (setq all_blocks (cons 
current_block all_blocks)) (setq current_contents nil) (setq current_block 
nil)))))

(de c!:cval_inner (x env) (prog (helper) (setq x (s!:improve x)) (cond ((atom
x) (return (c!:catom x env))) (t (cond ((eqcar (car x) (quote lambda)) (
return (c!:clambda (cadar x) (cons (quote progn) (cddar x)) (cdr x) env))) (t
(cond ((setq helper (get (car x) (quote c!:code))) (return (funcall helper x
env))) (t (cond ((and (setq helper (get (car x) (quote c!:compile_macro))) (
setq helper (funcall helper x))) (return (c!:cval helper env))) (t (cond ((
and (idp (car x)) (setq helper (macro!-function (car x)))) (return (c!:cval (
funcall helper x) env))) (t (return (c!:ccall (car x) (cdr x) env))))))))))))
))

(de c!:cval (x env) (prog (r) (setq r (c!:cval_inner x env)) (cond ((and r (
not (member!*!* r registers))) (error 0 (list r "not a register" x)))) (
return r)))

(de c!:clambda (bvl body args env) (prog (w fluids env1) (setq env1 (car env)
) (setq w (prog (var1004 var1005) (setq var1004 args) lab1003 (cond ((null 
var1004) (return (reversip var1005)))) (prog (a) (setq a (car var1004)) (setq
var1005 (cons (c!:cval a env) var1005))) (setq var1004 (cdr var1004)) (go 
lab1003))) (prog (var1007) (setq var1007 bvl) lab1006 (cond ((null var1007) (
return nil))) (prog (v) (setq v (car var1007)) (progn (cond ((globalp v) (
prog (oo) (setq oo (wrs nil)) (princ "+++++ ") (prin v) (princ 
" converted from GLOBAL to FLUID") (terpri) (wrs oo) (unglobal (list v)) (
fluid (list v))))) (cond ((fluidp v) (progn (setq fluids (cons (cons v (
c!:newreg)) fluids)) (flag (list (cdar fluids)) (quote c!:live_across_call)) 
(setq env1 (cons (cons (quote c!:dummy!:name) (cdar fluids)) env1)) (c!:outop
(quote ldrglob) (cdar fluids) v (c!:find_literal v)) (c!:outop (quote 
strglob) (car w) v (c!:find_literal v)))) (t (progn (setq env1 (cons (cons v 
(c!:newreg)) env1)) (c!:outop (quote movr) (cdar env1) nil (car w))))) (setq 
w (cdr w)))) (setq var1007 (cdr var1007)) (go lab1006)) (cond (fluids (
c!:outop (quote fluidbind) nil nil fluids))) (setq env (cons env1 (append 
fluids (cdr env)))) (setq w (c!:cval body env)) (prog (var1009) (setq var1009
fluids) lab1008 (cond ((null var1009) (return nil))) (prog (v) (setq v (car 
var1009)) (c!:outop (quote strglob) (cdr v) (car v) (c!:find_literal (car v))
)) (setq var1009 (cdr var1009)) (go lab1008)) (return w)))

(de c!:locally_bound (x env) (atsoc x (car env)))

(flag (quote (nil t)) (quote c!:constant))

(fluid (quote (literal_vector)))

(de c!:find_literal (x) (prog (n w) (setq w literal_vector) (setq n 0) (prog 
nil lab1010 (cond ((null (and w (not (equal (car w) x)))) (return nil))) (
progn (setq n (plus n 1)) (setq w (cdr w))) (go lab1010)) (cond ((null w) (
setq literal_vector (append literal_vector (list x))))) (return n)))

(de c!:catom (x env) (prog (v w) (setq v (c!:newreg)) (cond ((and (idp x) (
setq w (c!:locally_bound x env))) (c!:outop (quote movr) v nil (cdr w))) (t (
cond ((or (null x) (equal x (quote t)) (c!:small_number x)) (c!:outop (quote 
movk1) v nil x)) (t (cond ((or (not (idp x)) (flagp x (quote c!:constant))) (
c!:outop (quote movk) v x (c!:find_literal x))) (t (c!:outop (quote ldrglob) 
v x (c!:find_literal x)))))))) (return v)))

(de c!:cjumpif (x env d1 d2) (prog (helper r) (setq x (s!:improve x)) (cond (
(and (atom x) (or (not (idp x)) (and (flagp x (quote c!:constant)) (not (
c!:locally_bound x env))))) (c!:endblock (quote goto) (list (cond (x d1) (t 
d2))))) (t (cond ((and (not (atom x)) (setq helper (get (car x) (quote 
c!:ctest)))) (return (funcall helper x env d1 d2))) (t (progn (setq r (
c!:cval x env)) (c!:endblock (list (quote ifnull) r) (list d2 d1)))))))))

(fluid (quote (current)))

(de c!:ccall (fn args env) (c!:ccall1 fn args env))

(fluid (quote (visited)))

(de c!:has_calls (a b) (prog (visited) (return (c!:has_calls_1 a b))))

(de c!:has_calls_1 (a b) (cond ((or (equal a b) (not (atom a)) (memq a 
visited)) nil) (t (prog (has_call) (setq visited (cons a visited)) (prog (
var1012) (setq var1012 (get a (quote c!:contents))) lab1011 (cond ((null 
var1012) (return nil))) (prog (z) (setq z (car var1012)) (cond ((eqcar z (
quote call)) (setq has_call t)))) (setq var1012 (cdr var1012)) (go lab1011)) 
(cond (has_call (return (prog (visited) (return (c!:can_reach a b)))))) (prog
(var1014) (setq var1014 (get a (quote c!:where_to))) lab1013 (cond ((null 
var1014) (return nil))) (prog (d) (setq d (car var1014)) (cond ((
c!:has_calls_1 d b) (setq has_call t)))) (setq var1014 (cdr var1014)) (go 
lab1013)) (return has_call)))))

(de c!:can_reach (a b) (cond ((equal a b) t) (t (cond ((or (not (atom a)) (
memq a visited)) nil) (t (progn (setq visited (cons a visited)) (
c!:any_can_reach (get a (quote c!:where_to)) b)))))))

(de c!:any_can_reach (l b) (cond ((null l) nil) (t (cond ((c!:can_reach (car 
l) b) t) (t (c!:any_can_reach (cdr l) b))))))

(de c!:pareval (args env) (prog (tasks tasks1 merge split r) (setq tasks (
prog (var1016 var1017) (setq var1016 args) lab1015 (cond ((null var1016) (
return (reversip var1017)))) (prog (a) (setq a (car var1016)) (setq var1017 (
cons (cons (c!:my_gensym) (c!:my_gensym)) var1017))) (setq var1016 (cdr 
var1016)) (go lab1015))) (setq split (c!:my_gensym)) (c!:endblock (quote goto
) (list split)) (prog (var1019) (setq var1019 args) lab1018 (cond ((null 
var1019) (return nil))) (prog (a) (setq a (car var1019)) (prog (s) (setq s (
car tasks)) (setq tasks (cdr tasks)) (c!:startblock (car s)) (setq r (cons (
c!:cval a env) r)) (c!:endblock (quote goto) (list (cdr s))) (cond ((
c!:has_calls (car s) (cdr s)) (setq tasks1 (cons s tasks1))) (t (setq merge (
cons s merge)))))) (setq var1019 (cdr var1019)) (go lab1018)) (prog (var1021)
(setq var1021 tasks1) lab1020 (cond ((null var1021) (return nil))) (prog (z)
(setq z (car var1021)) (setq merge (cons z merge))) (setq var1021 (cdr 
var1021)) (go lab1020)) (prog (var1023) (setq var1023 merge) lab1022 (cond ((
null var1023) (return nil))) (prog (v) (setq v (car var1023)) (progn (
c!:startblock split) (c!:endblock (quote goto) (list (car v))) (setq split (
cdr v)))) (setq var1023 (cdr var1023)) (go lab1022)) (c!:startblock split) (
return (reversip r))))

(de c!:ccall1 (fn args env) (prog (tasks merge r val) (setq fn (list fn (cdr 
env))) (setq val (c!:newreg)) (cond ((null args) (c!:outop (quote call) val 
nil fn)) (t (cond ((null (cdr args)) (c!:outop (quote call) val (list (
c!:cval (car args) env)) fn)) (t (progn (setq r (c!:pareval args env)) (
c!:outop (quote call) val r fn)))))) (c!:outop (quote reloadenv) (quote env) 
nil nil) (return val)))

(fluid (quote (restart_label reloadenv does_call current_c_name)))

(fluid (quote (proglabs blockstack)))

(de c!:cfndef (current_procedure current_c_name argsbody checksum) (prog (env
n w current_args current_block restart_label current_contents all_blocks 
entrypoint exitpoint args1 registers stacklocs literal_vector reloadenv 
does_call blockstack proglabs args body) (setq args (car argsbody)) (setq 
body (cdr argsbody)) (cond ((atom body) (setq body nil)) (t (cond ((atom (cdr
body)) (setq body (car body))) (t (setq body (cons (quote progn) body)))))) 
(c!:reset_gensyms) (wrs C_file) (linelength 200) (c!:printf 
"\n\n/* Code for %a */\n\n" current_procedure) (c!:find_literal 
current_procedure) (cond (!*r2i (setq body (s!:r2i current_procedure args 
body)))) (setq current_args args) (prog (var1025) (setq var1025 args) lab1024
(cond ((null var1025) (return nil))) (prog (v) (setq v (car var1025)) (cond 
((or (equal v (quote !&optional)) (equal v (quote !&rest))) (error 0 
"&optional and &rest not supported by this compiler (yet)")) (t (cond ((
globalp v) (prog (oo) (setq oo (wrs nil)) (princ "+++++ ") (prin v) (princ 
" converted from GLOBAL to FLUID") (terpri) (wrs oo) (unglobal (list v)) (
fluid (list v)) (setq n (cons (cons v (c!:my_gensym)) n)))) (t (cond ((fluidp
v) (setq n (cons (cons v (c!:my_gensym)) n))))))))) (setq var1025 (cdr 
var1025)) (go lab1024)) (setq restart_label (c!:my_gensym)) (setq body (list 
(quote c!:private_tagbody) restart_label body)) (cond (n (progn (setq body (
list (list (quote return) body))) (setq args (subla n args)) (prog (var1027) 
(setq var1027 n) lab1026 (cond ((null var1027) (return nil))) (prog (v) (setq
v (car var1027)) (setq body (cons (list (quote setq) (car v) (cdr v)) body))
) (setq var1027 (cdr var1027)) (go lab1026)) (setq body (cons (quote prog) (
cons (prog (var1029 var1030) (setq var1029 (reverse n)) lab1028 (cond ((null 
var1029) (return (reversip var1030)))) (prog (v) (setq v (car var1029)) (setq
var1030 (cons (car v) var1030))) (setq var1029 (cdr var1029)) (go lab1028)) 
body)))))) (c!:printf "static Lisp_Object ") (cond ((or (null args) (geq (
length args) 3)) (c!:printf "MS_CDECL "))) (c!:printf "%s(Lisp_Object env" 
current_c_name) (cond ((or (null args) (geq (length args) 3)) (c!:printf 
", int nargs"))) (setq n t) (setq env nil) (prog (var1032) (setq var1032 args
) lab1031 (cond ((null var1032) (return nil))) (prog (x) (setq x (car var1032
)) (prog (aa) (c!:printf ",") (cond (n (progn (c!:printf 
"\n                        ") (setq n nil))) (t (setq n t))) (setq aa (
c!:my_gensym)) (setq env (cons (cons x aa) env)) (setq registers (cons aa 
registers)) (setq args1 (cons aa args1)) (c!:printf " Lisp_Object %s" aa))) (
setq var1032 (cdr var1032)) (go lab1031)) (cond ((or (null args) (geq (length
args) 3)) (c!:printf ", ..."))) (c!:printf ")\n{\n") (c!:startblock (setq 
entrypoint (c!:my_gensym))) (setq exitpoint current_block) (c!:endblock (
quote goto) (list (list (c!:cval body (cons env nil))))) (
c!:optimise_flowgraph entrypoint all_blocks env (cons (length args) 
current_procedure) args1) (c!:printf "}\n\n") (wrs O_file) (setq L_contents (
cons (cons current_procedure (cons literal_vector checksum)) L_contents)) (
return nil)))

(flag (quote (rds deflist flag fluid global remprop remflag unfluid unglobal 
dm carcheck C!-end)) (quote eval))

(flag (quote (rds)) (quote ignore))

(fluid (quote (!*backtrace)))

(de c!:ccompilesupervisor nil (prog (u w) top (setq u (errorset (quote (read)
) t !*backtrace)) (cond ((atom u) (return nil))) (setq u (car u)) (cond ((
equal u !$eof!$) (return nil))) (cond ((atom u) (go top)) (t (cond ((eqcar u 
(quote C!-end)) (return (apply (quote C!-end) nil))) (t (cond ((eqcar u (
quote rdf)) (progn (setq w (open (setq u (eval (cadr u))) (quote input))) (
cond (w (progn (terpri) (princ "Reading file ") (print u) (setq w (rds w)) (
c!:ccompilesupervisor) (princ "End of file ") (print u) (close (rds w)))) (t 
(progn (princ "Failed to open file ") (print u)))))) (t (c!:ccmpout1 u)))))))
(go top)))

(global (quote (c!:char_mappings)))

(setq c!:char_mappings (quote ((!  . !A) (!! . !B) (!# . !C) (!$ . !D) (!% . 
!E) (!^ . !F) (!& . !G) (!* . !H) (!( . !I) (!) . !J) (!- . !K) (!+ . !L) (!=
 . !M) (!\ . !N) (!| . !O) (!, . !P) (!. . !Q) (!< . !R) (!> . !S) (!: . !T) 
(!; . !U) (!/ . !V) (!? . !W) (!~ . !X) (!` . !Y))))

(fluid (quote (c!:names_so_far)))

(de c!:inv_name (n) (prog (r w) (cond ((setq w (assoc n c!:names_so_far)) (
setq w (plus (cdr w) 1))) (t (setq w 0))) (setq c!:names_so_far (cons (cons n
w) c!:names_so_far)) (setq r (quote (!C !C !"))) (cond ((not (zerop w)) (
setq r (append (reverse (explodec w)) r)))) (setq r (cons (quote !_) r)) (
prog (var1034) (setq var1034 (explode2 n)) lab1033 (cond ((null var1034) (
return nil))) (prog (c) (setq c (car var1034)) (progn (cond ((equal c (quote 
_)) (setq r (cons (quote _) r))) (t (cond ((or (liter c) (digit c)) (setq r (
cons c r))) (t (cond ((setq w (atsoc c c!:char_mappings)) (setq r (cons (cdr 
w) r))) (t (setq r (cons (quote !Z) r)))))))))) (setq var1034 (cdr var1034)) 
(go lab1033)) (setq r (cons (quote !") r)) (return (compress (reverse r)))))

(fluid (quote (defnames pending_functions)))

(de c!:ccmpout1 (u) (prog (pending_functions) (setq pending_functions (list u
)) (prog nil lab1035 (cond ((null pending_functions) (return nil))) (progn (
setq u (car pending_functions)) (setq pending_functions (cdr 
pending_functions)) (c!:ccmpout1a u)) (go lab1035))))

(de c!:ccmpout1a (u) (prog (w checksum) (cond ((atom u) (return nil)) (t (
cond ((eqcar u (quote progn)) (progn (prog (var1037) (setq var1037 (cdr u)) 
lab1036 (cond ((null var1037) (return nil))) (prog (v) (setq v (car var1037))
(c!:ccmpout1a v)) (setq var1037 (cdr var1037)) (go lab1036)) (return nil))) 
(t (cond ((eqcar u (quote C!-end)) nil) (t (cond ((or (flagp (car u) (quote 
eval)) (and (equal (car u) (quote setq)) (not (atom (caddr u))) (flagp (
caaddr u) (quote eval)))) (errorset u t !*backtrace))))))))) (cond ((eqcar u 
(quote rdf)) (prog nil (setq w (open (setq u (eval (cadr u))) (quote input)))
(cond (w (progn (princ "Reading file ") (print u) (setq w (rds w)) (
c!:ccompilesupervisor) (princ "End of file ") (print u) (close (rds w)))) (t 
(progn (princ "Failed to open file ") (print u)))))) (t (cond ((eqcar u (
quote de)) (progn (setq u (cdr u)) (setq checksum (md60 u)) (setq defnames (
cons (list (car u) (c!:inv_name (car u)) (length (cadr u)) checksum) defnames
)) (cond ((neq (posn) 0) (terpri))) (princ "Compiling ") (prin (caar defnames
)) (princ " ... ") (c!:cfndef (caar defnames) (cadar defnames) (cdr u) 
checksum) (terpri))))))))

(fluid (quote (!*defn dfprint!* dfprintsave)))

(de c!:concat (a b) (compress (cons (quote !") (append (explode2 a) (append (
explode2 b) (quote (!")))))))

(de c!:ccompilestart (name setupname dir hdrnow) (prog (o d w) (setq 
File_name (list!-to!-string (explodec name))) (setq Setup_name (explodec 
setupname)) (setq Setup_name (subst (quote !_) (quote !-) Setup_name)) (setq 
Setup_name (list!-to!-string Setup_name)) (cond (dir (progn (cond ((memq (
quote win32) lispsystem!*) (setq name (c!:concat dir (c!:concat "\" name)))) 
(t (setq name (c!:concat dir (c!:concat "/" name)))))))) (princ "C file = ") 
(print name) (setq C_file (open (c!:concat name ".c") (quote output))) (setq 
L_file (c!:concat name ".lsp")) (setq L_contents nil) (setq c!:names_so_far 
nil) (setq o (reverse (explode (date)))) (prog (i) (setq i 1) lab1038 (cond (
(minusp (times 1 (difference 5 i))) (return nil))) (progn (setq d (cons (car 
o) d)) (setq o (cdr o))) (setq i (plus i 1)) (go lab1038)) (setq d (cons (
quote !-) d)) (setq o (cdddr (cdddr (cddddr o)))) (setq w o) (setq o (cdddr o
)) (setq d (cons (caddr o) (cons (cadr o) (cons (car o) d)))) (setq d (
compress (cons (quote !") (cons (cadr w) (cons (car w) (cons (quote !-) d))))
)) (setq O_file (wrs C_file)) (setq defnames nil) (cond (hdrnow (c!:printf 
"\n/* Module: %s %tMachine generated C code */\n\n" setupname 25)) (t (
c!:printf "\n/* %s.c %tMachine generated C code */\n\n" name 25))) (c!:printf
"/* Signature: 00000000 %s */\n\n" d) (c!:printf "#include <stdio.h>\n") (
c!:printf "#include <stdlib.h>\n") (c!:printf "#include <string.h>\n") (
c!:printf "#include <ctype.h>\n") (c!:printf "#include <stdarg.h>\n") (
c!:printf "#include <time.h>\n") (c!:printf "#ifndef _cplusplus\n") (
c!:printf "#include <setjmp.h>\n") (c!:printf "#endif\n\n") (cond (hdrnow (
print!-config!-header)) (t (c!:printf "#include \qconfig.h\q\n\n"))) (
print!-csl!-headers) (cond (hdrnow (c!:print!-init))) (wrs O_file) (return 
nil)))

(de c!:print!-init nil (progn (c!:printf "\n") (c!:printf 
"Lisp_Object *C_nilp;\n") (c!:printf "Lisp_Object **C_stackp;\n") (c!:printf 
"Lisp_Object * volatile * stacklimitp;\n") (c!:printf "\n") (c!:printf 
"void init(Lisp_Object *a, Lisp_Object **b, Lisp_Object * volatile *c)\n") (
c!:printf "{\n") (c!:printf "    C_nilp = a;\n") (c!:printf 
"    C_stackp = b;\n") (c!:printf "    stacklimitp = c;\n") (c!:printf "}\n")
(c!:printf "\n") (c!:printf "#define C_nil (*C_nilp)\n") (c!:printf 
"#define C_stack  (*C_stackp)\n") (c!:printf 
"#define stacklimit (*stacklimitp)\n") (c!:printf "\n")))

(de C!-end nil (C!-end1 t))

(de C!-end1 (create_lfile) (prog (checksum c1 c2 c3) (wrs C_file) (cond (
create_lfile (c!:printf "\n\nsetup_type const %s_setup[] =\n{\n" Setup_name))
(t (c!:printf "\n\nsetup_type_1 const %s_setup[] =\n{\n" Setup_name))) (setq
defnames (reverse defnames)) (prog nil lab1039 (cond ((null defnames) (
return nil))) (prog (name nargs f1 f2 cast fn) (setq name (caar defnames)) (
setq checksum (cadddr (car defnames))) (setq f1 (cadar defnames)) (setq nargs
(caddar defnames)) (setq cast "(n_args *)") (cond ((equal nargs 1) (progn (
setq f2 (quote !t!o!o_!m!a!n!y_1)) (setq cast "") (setq fn (quote 
!w!r!o!n!g_!n!o_1)))) (t (cond ((equal nargs 2) (progn (setq f2 f1) (setq f1 
(quote !t!o!o_!f!e!w_2)) (setq cast "") (setq fn (quote !w!r!o!n!g_!n!o_2))))
(t (progn (setq fn f1) (setq f1 (quote !w!r!o!n!g_!n!o_!n!a)) (setq f2 (
quote !w!r!o!n!g_!n!o_!n!b))))))) (cond (create_lfile (c!:printf 
"    {\q%s\q,%t%s,%t%s,%t%s%s},\n" name 32 f1 48 f2 63 cast fn)) (t (prog (c1
c2) (setq c1 (divide checksum (expt 2 31))) (setq c2 (cdr c1)) (setq c1 (car
c1)) (c!:printf "    {\q%s\q, %t%s, %t%s, %t%s%s, %t%s, %t%s},\n" name 24 f1
40 f2 52 cast fn 64 c1 76 c2)))) (setq defnames (cdr defnames))) (go lab1039
)) (setq c3 (setq checksum (md60 L_contents))) (setq c1 (remainder c3 
10000000)) (setq c3 (quotient c3 10000000)) (setq c2 (remainder c3 10000000))
(setq c3 (quotient c3 10000000)) (setq checksum (list!-to!-string (append (
explodec c3) (cons (quote ! ) (append (explodec c2) (cons (quote ! ) (
explodec c1))))))) (c!:printf 
"    {NULL, (one_args *)%a, (two_args *)%a, 0}\n};\n\n" Setup_name checksum) 
(c!:printf "/* end of generated code */\n") (close C_file) (cond (
create_lfile (progn (setq L_file (open L_file (quote output))) (wrs L_file) (
linelength 72) (terpri) (princ "% ") (princ Setup_name) (princ ".lsp") (ttab 
20) (princ "Machine generated Lisp") (terpri) (terpri) (princ "(c!:install ")
(princ (quote !")) (princ Setup_name) (princ (quote !")) (princ " ") (princ 
checksum) (printc ")") (terpri) (prog (var1041) (setq var1041 (reverse 
L_contents)) lab1040 (cond ((null var1041) (return nil))) (prog (x) (setq x (
car var1041)) (progn (princ "(c!:install '") (prin (car x)) (princ " '") (
prin (cadr x)) (princ " ") (prin (cddr x)) (princ ")") (terpri) (terpri))) (
setq var1041 (cdr var1041)) (go lab1040)) (terpri) (princ 
"% End of generated Lisp code") (terpri) (terpri) (setq L_contents nil) (wrs 
O_file) (close L_file) (setq !*defn nil) (setq dfprint!* dfprintsave))) (t (
progn (setq checksum (cons checksum (reverse L_contents))) (setq L_contents 
nil) (return checksum))))))

(put (quote C!-end) (quote stat) (quote endstat))

(de C!-compile (u) (prog nil (terpri) (princ "C!-COMPILE ") (prin u) (princ 
": IN files;  or type in expressions") (terpri) (princ 
"When all done, execute C!-END;") (terpri) (verbos nil) (c!:ccompilestart (
car u) (car u) nil nil) (setq dfprintsave dfprint!*) (setq dfprint!* (quote 
c!:ccmpout1)) (setq !*defn t) (cond ((getd (quote begin)) (return nil))) (
c!:ccompilesupervisor)))

(put (quote C!-compile) (quote stat) (quote rlis))

(de c!:print_opcode (s depth) (prog (op r1 r2 r3 helper) (setq op (car s)) (
setq r1 (cadr s)) (setq r2 (caddr s)) (setq r3 (cadddr s)) (setq helper (get 
op (quote c!:opcode_printer))) (cond (helper (funcall helper op r1 r2 r3 
depth)) (t (progn (prin s) (terpri))))))

(de c!:print_exit_condition (why where_to depth) (prog (helper lab1 drop1 
lab2 drop2 negate) (cond ((equal why (quote goto)) (progn (setq where_to (car
where_to)) (cond ((atom where_to) (progn (c!:printf "    goto %s;\n" 
where_to) (c!:display_flowgraph where_to depth t))) (t (progn (c!:printf 
"    ") (c!:pgoto where_to depth)))) (return nil))) (t (cond ((eqcar (car why
) (quote call)) (return (prog (args locs g w) (cond ((setq w (get (cadar why)
(quote c!:direct_entrypoint))) (progn (prog (var1043) (setq var1043 (cdr why
)) lab1042 (cond ((null var1043) (return nil))) (prog (a) (setq a (car 
var1043)) (cond ((flagp a (quote c!:live_across_call)) (progn (cond ((null g)
(c!:printf "    {\n"))) (setq g (c!:my_gensym)) (c!:printf 
"        Lisp_Object %s = %v;\n" g a) (setq args (cons g args)))) (t (setq 
args (cons a args))))) (setq var1043 (cdr var1043)) (go lab1042)) (cond ((neq
depth 0) (progn (cond (g (c!:printf "    "))) (c!:printf "    popv(%s);\n" 
depth)))) (cond (g (c!:printf "    "))) (c!:printf "    return %s(" (cdr w)) 
(setq args (reversip args)) (cond (args (progn (c!:printf "%v" (car args)) (
prog (var1045) (setq var1045 (cdr args)) lab1044 (cond ((null var1045) (
return nil))) (prog (a) (setq a (car var1045)) (c!:printf ", %v" a)) (setq 
var1045 (cdr var1045)) (go lab1044))))) (c!:printf ");\n") (cond (g (
c!:printf "    }\n"))))) (t (cond ((setq w (get (cadar why) (quote 
c!:c_entrypoint))) (progn (prog (var1047) (setq var1047 (cdr why)) lab1046 (
cond ((null var1047) (return nil))) (prog (a) (setq a (car var1047)) (cond ((
flagp a (quote c!:live_across_call)) (progn (cond ((null g) (c!:printf 
"    {\n"))) (setq g (c!:my_gensym)) (c!:printf 
"        Lisp_Object %s = %v;\n" g a) (setq args (cons g args)))) (t (setq 
args (cons a args))))) (setq var1047 (cdr var1047)) (go lab1046)) (cond ((neq
depth 0) (c!:printf "        popv(%s);\n" depth))) (c!:printf 
"        return %s(nil" w) (cond ((or (null args) (geq (length args) 3)) (
c!:printf ", %s" (length args)))) (prog (var1049) (setq var1049 (reversip 
args)) lab1048 (cond ((null var1049) (return nil))) (prog (a) (setq a (car 
var1049)) (c!:printf ", %v" a)) (setq var1049 (cdr var1049)) (go lab1048)) (
c!:printf ");\n") (cond (g (c!:printf "    }\n"))))) (t (prog (nargs) (setq 
nargs (length (cdr why))) (c!:printf "    {\n") (prog (var1051) (setq var1051
(cdr why)) lab1050 (cond ((null var1051) (return nil))) (prog (a) (setq a (
car var1051)) (cond ((flagp a (quote c!:live_across_call)) (progn (setq g (
c!:my_gensym)) (c!:printf "        Lisp_Object %s = %v;\n" g a) (setq args (
cons g args)))) (t (setq args (cons a args))))) (setq var1051 (cdr var1051)) 
(go lab1050)) (cond ((neq depth 0) (c!:printf "        popv(%s);\n" depth))) 
(c!:printf "        fn = elt(env, %s); /* %c */\n" (c!:find_literal (cadar 
why)) (cadar why)) (cond ((equal nargs 1) (c!:printf 
"        return (*qfn1(fn))(qenv(fn)")) (t (cond ((equal nargs 2) (c!:printf 
"        return (*qfn2(fn))(qenv(fn)")) (t (c!:printf 
"        return (*qfnn(fn))(qenv(fn), %s" nargs))))) (prog (var1053) (setq 
var1053 (reversip args)) lab1052 (cond ((null var1053) (return nil))) (prog (
a) (setq a (car var1053)) (c!:printf ", %s" a)) (setq var1053 (cdr var1053)) 
(go lab1052)) (c!:printf ");\n    }\n")))))) (return nil))))))) (setq lab1 (
car where_to)) (setq drop1 (and (atom lab1) (not (flagp lab1 (quote 
c!:visited))))) (setq lab2 (cadr where_to)) (setq drop2 (and (atom lab2) (not
(flagp drop2 (quote c!:visited))))) (cond ((and drop2 (equal (get lab2 (
quote c!:count)) 1)) (progn (setq where_to (list lab2 lab1)) (setq drop1 t)))
(t (cond (drop1 (setq negate t))))) (setq helper (get (car why) (quote 
c!:exit_helper))) (cond ((null helper) (error 0 (list "Bad exit condition" 
why)))) (c!:printf "    if (") (cond (negate (progn (c!:printf "!(") (funcall
helper (cdr why) depth) (c!:printf ")"))) (t (funcall helper (cdr why) depth
))) (c!:printf ") ") (cond ((not drop1) (progn (c!:pgoto (car where_to) depth
) (c!:printf "    else ")))) (c!:pgoto (cadr where_to) depth) (cond ((atom (
car where_to)) (c!:display_flowgraph (car where_to) depth drop1))) (cond ((
atom (cadr where_to)) (c!:display_flowgraph (cadr where_to) depth nil)))))

(de c!:pmovr (op r1 r2 r3 depth) (c!:printf "    %v = %v;\n" r1 r3))

(put (quote movr) (quote c!:opcode_printer) (function c!:pmovr))

(de c!:pmovk (op r1 r2 r3 depth) (c!:printf 
"    %v = elt(env, %s); /* %c */\n" r1 r3 r2))

(put (quote movk) (quote c!:opcode_printer) (function c!:pmovk))

(de c!:pmovk1 (op r1 r2 r3 depth) (cond ((null r3) (c!:printf 
"    %v = nil;\n" r1)) (t (cond ((equal r3 (quote t)) (c!:printf 
"    %v = lisp_true;\n" r1)) (t (c!:printf 
"    %v = (Lisp_Object)%s; /* %c */\n" r1 (plus (times 16 r3) 1) r3))))))

(put (quote movk1) (quote c!:opcode_printer) (function c!:pmovk1))

(flag (quote (movk1)) (quote c!:uses_nil))

(de c!:preloadenv (op r1 r2 r3 depth) (c!:printf "    env = stack[%s];\n" (
minus reloadenv)))

(put (quote reloadenv) (quote c!:opcode_printer) (function c!:preloadenv))

(de c!:pldrglob (op r1 r2 r3 depth) (c!:printf 
"    %v = qvalue(elt(env, %s)); /* %c */\n" r1 r3 r2))

(put (quote ldrglob) (quote c!:opcode_printer) (function c!:pldrglob))

(de c!:pstrglob (op r1 r2 r3 depth) (c!:printf 
"    qvalue(elt(env, %s)) = %v; /* %c */\n" r3 r1 r2))

(put (quote strglob) (quote c!:opcode_printer) (function c!:pstrglob))

(de c!:pnilglob (op r1 r2 r3 depth) (c!:printf 
"    qvalue(elt(env, %s)) = nil; /* %c */\n" r3 r2))

(put (quote nilglob) (quote c!:opcode_printer) (function c!:pnilglob))

(flag (quote (nilglob)) (quote c!:uses_nil))

(de c!:pnull (op r1 r2 r3 depth) (c!:printf 
"    %v = (%v == nil ? lisp_true : nil);\n" r1 r3))

(put (quote null) (quote c!:opcode_printer) (function c!:pnull))

(put (quote not) (quote c!:opcode_printer) (function c!:pnull))

(flag (quote (null not)) (quote c!:uses_nil))

(de c!:pfastget (op r1 r2 r3 depth) (progn (c!:printf 
"    if (!symbolp(%v)) %v = nil;\n" r2 r1) (c!:printf 
"    else { %v = qfastgets(%v);\n" r1 r2) (c!:printf 
"           if (%v != nil) { %v = elt(%v, %s); /* %c */\n" r1 r1 r1 (car r3) 
(cdr r3)) (c!:printf "#ifdef RECORD_GET\n") (c!:printf 
"             if (%v != SPID_NOPROP)\n" r1) (c!:printf 
"                record_get(elt(fastget_names, %s), 1);\n" (car r3)) (
c!:printf "             else record_get(elt(fastget_names, %s), 0),\n" (car 
r3)) (c!:printf "                %v = nil; }\n" r1) (c!:printf 
"           else record_get(elt(fastget_names, %s), 0); }\n" (car r3)) (
c!:printf "#else\n") (c!:printf 
"             if (%v == SPID_NOPROP) %v = nil; }}\n" r1 r1) (c!:printf 
"#endif\n")))

(put (quote fastget) (quote c!:opcode_printer) (function c!:pfastget))

(flag (quote (fastget)) (quote c!:uses_nil))

(de c!:pfastflag (op r1 r2 r3 depth) (progn (c!:printf 
"    if (!symbolp(%v)) %v = nil;\n" r2 r1) (c!:printf 
"    else { %v = qfastgets(%v);\n" r1 r2) (c!:printf 
"           if (%v != nil) { %v = elt(%v, %s); /* %c */\n" r1 r1 r1 (car r3) 
(cdr r3)) (c!:printf "#ifdef RECORD_GET\n") (c!:printf 
"             if (%v == SPID_NOPROP)\n" r1) (c!:printf 
"                record_get(elt(fastget_names, %s), 0),\n" (car r3)) (
c!:printf "                %v = nil;\n" r1) (c!:printf 
"             else record_get(elt(fastget_names, %s), 1),\n" (car r3)) (
c!:printf "                %v = lisp_true; }\n" r1) (c!:printf 
"           else record_get(elt(fastget_names, %s), 0); }\n" (car r3)) (
c!:printf "#else\n") (c!:printf 
"             if (%v == SPID_NOPROP) %v = nil; else %v = lisp_true; }}\n" r1 
r1 r1) (c!:printf "#endif\n")))

(put (quote fastflag) (quote c!:opcode_printer) (function c!:pfastflag))

(flag (quote (fastflag)) (quote c!:uses_nil))

(de c!:pcar (op r1 r2 r3 depth) (prog nil (cond ((not !*unsafecar) (progn (
c!:printf "    if (!car_legal(%v)) " r3) (c!:pgoto (c!:find_error_label (list
(quote car) r3) r2 depth) depth)))) (c!:printf "    %v = qcar(%v);\n" r1 r3)
))

(put (quote car) (quote c!:opcode_printer) (function c!:pcar))

(de c!:pcdr (op r1 r2 r3 depth) (prog nil (cond ((not !*unsafecar) (progn (
c!:printf "    if (!car_legal(%v)) " r3) (c!:pgoto (c!:find_error_label (list
(quote cdr) r3) r2 depth) depth)))) (c!:printf "    %v = qcdr(%v);\n" r1 r3)
))

(put (quote cdr) (quote c!:opcode_printer) (function c!:pcdr))

(de c!:pqcar (op r1 r2 r3 depth) (c!:printf "    %v = qcar(%v);\n" r1 r3))

(put (quote qcar) (quote c!:opcode_printer) (function c!:pqcar))

(de c!:pqcdr (op r1 r2 r3 depth) (c!:printf "    %v = qcdr(%v);\n" r1 r3))

(put (quote qcdr) (quote c!:opcode_printer) (function c!:pqcdr))

(de c!:patom (op r1 r2 r3 depth) (c!:printf 
"    %v = (consp(%v) ? nil : lisp_true);\n" r1 r3))

(put (quote atom) (quote c!:opcode_printer) (function c!:patom))

(flag (quote (atom)) (quote c!:uses_nil))

(de c!:pnumberp (op r1 r2 r3 depth) (c!:printf 
"    %v = (is_number(%v) ? lisp_true : nil);\n" r1 r3))

(put (quote numberp) (quote c!:opcode_printer) (function c!:pnumberp))

(flag (quote (numberp)) (quote c!:uses_nil))

(de c!:pfixp (op r1 r2 r3 depth) (c!:printf "    %v = integerp(%v);\n" r1 r3)
)

(put (quote fixp) (quote c!:opcode_printer) (function c!:pfixp))

(flag (quote (fixp)) (quote c!:uses_nil))

(de c!:piminusp (op r1 r2 r3 depth) (c!:printf 
"    %v = ((int32_t)(%v) < 0 ? lisp_true : nil);\n" r1 r3))

(put (quote iminusp) (quote c!:opcode_printer) (function c!:piminusp))

(flag (quote (iminusp)) (quote c!:uses_nil))

(de c!:pilessp (op r1 r2 r3 depth) (c!:printf 
"    %v = ((int32_t)%v < (int32_t)%v) ? lisp_true : nil;\n" r1 r2 r3))

(put (quote ilessp) (quote c!:opcode_printer) (function c!:pilessp))

(flag (quote (ilessp)) (quote c!:uses_nil))

(de c!:pigreaterp (op r1 r2 r3 depth) (c!:printf 
"    %v = ((int32_t)%v > (int32_t)%v) ? lisp_true : nil;\n" r1 r2 r3))

(put (quote igreaterp) (quote c!:opcode_printer) (function c!:pigreaterp))

(flag (quote (igreaterp)) (quote c!:uses_nil))

(de c!:piminus (op r1 r2 r3 depth) (c!:printf 
"    %v = (Lisp_Object)(2-((int32_t)(%v)));\n" r1 r3))

(put (quote iminus) (quote c!:opcode_printer) (function c!:piminus))

(de c!:piadd1 (op r1 r2 r3 depth) (c!:printf 
"    %v = (Lisp_Object)((int32_t)(%v) + 0x10);\n" r1 r3))

(put (quote iadd1) (quote c!:opcode_printer) (function c!:piadd1))

(de c!:pisub1 (op r1 r2 r3 depth) (c!:printf 
"    %v = (Lisp_Object)((int32_t)(%v) - 0x10);\n" r1 r3))

(put (quote isub1) (quote c!:opcode_printer) (function c!:pisub1))

(de c!:piplus2 (op r1 r2 r3 depth) (c!:printf 
"    %v = (Lisp_Object)((int32_t)%v + (int32_t)%v - TAG_FIXNUM);\n" r1 r2 r3))

(put (quote iplus2) (quote c!:opcode_printer) (function c!:piplus2))

(de c!:pidifference (op r1 r2 r3 depth) (c!:printf 
"    %v = (Lisp_Object)((int32_t)%v - (int32_t)%v + TAG_FIXNUM);\n" r1 r2 r3))

(put (quote idifference) (quote c!:opcode_printer) (function c!:pidifference)
)

(de c!:pitimes2 (op r1 r2 r3 depth) (c!:printf 
"    %v = fixnum_of_int(int_of_fixnum(%v) * int_of_fixnum(%v));\n" r1 r2 r3))

(put (quote itimes2) (quote c!:opcode_printer) (function c!:pitimes2))

(de c!:pmodular_plus (op r1 r2 r3 depth) (progn (c!:printf 
"    {   int32_t w = int_of_fixnum(%v) + int_of_fixnum(%v);\n" r2 r3) (
c!:printf "        if (w >= current_modulus) w -= current_modulus;\n") (
c!:printf "        %v = fixnum_of_int(w);\n" r1) (c!:printf "    }\n")))

(put (quote modular!-plus) (quote c!:opcode_printer) (function 
c!:pmodular_plus))

(de c!:pmodular_difference (op r1 r2 r3 depth) (progn (c!:printf 
"    {   int32_t w = int_of_fixnum(%v) - int_of_fixnum(%v);\n" r2 r3) (
c!:printf "        if (w < 0) w += current_modulus;\n") (c!:printf 
"        %v = fixnum_of_int(w);\n" r1) (c!:printf "    }\n")))

(put (quote modular!-difference) (quote c!:opcode_printer) (function 
c!:pmodular_difference))

(de c!:pmodular_minus (op r1 r2 r3 depth) (progn (c!:printf 
"    {   int32_t w = int_of_fixnum(%v);\n" r3) (c!:printf 
"        if (w != 0) w = current_modulus - w;\n") (c!:printf 
"        %v = fixnum_of_int(w);\n" r1) (c!:printf "    }\n")))

(put (quote modular!-minus) (quote c!:opcode_printer) (function 
c!:pmodular_minus))

(de c!:passoc (op r1 r2 r3 depth) (c!:printf 
"    %v = Lassoc(nil, %v, %v);\n" r1 r2 r3))

(put (quote assoc) (quote c!:opcode_printer) (function c!:passoc))

(flag (quote (assoc)) (quote c!:uses_nil))

(de c!:patsoc (op r1 r2 r3 depth) (c!:printf 
"    %v = Latsoc(nil, %v, %v);\n" r1 r2 r3))

(put (quote atsoc) (quote c!:opcode_printer) (function c!:patsoc))

(flag (quote (atsoc)) (quote c!:uses_nil))

(de c!:pmember (op r1 r2 r3 depth) (c!:printf 
"    %v = Lmember(nil, %v, %v);\n" r1 r2 r3))

(put (quote member) (quote c!:opcode_printer) (function c!:pmember))

(flag (quote (member)) (quote c!:uses_nil))

(de c!:pmemq (op r1 r2 r3 depth) (c!:printf "    %v = Lmemq(nil, %v, %v);\n" 
r1 r2 r3))

(put (quote memq) (quote c!:opcode_printer) (function c!:pmemq))

(flag (quote (memq)) (quote c!:uses_nil))

(de c!:pget (op r1 r2 r3 depth) (c!:printf "    %v = get(%v, %v);\n" r1 r2 r3
))

(put (quote get) (quote c!:opcode_printer) (function c!:pget))

(de c!:pqgetv (op r1 r2 r3 depth) (progn (c!:printf 
"    %v = *(Lisp_Object *)((char *)%v + (CELL-TAG_VECTOR) +" r1 r2) (
c!:printf " ((int32_t)%v/(16/CELL)));\n" r3)))

(put (quote qgetv) (quote c!:opcode_printer) (function c!:pqgetv))

(de c!:pqputv (op r1 r2 r3 depth) (progn (c!:printf 
"    *(Lisp_Object *)((char *)%v + (CELL-TAG_VECTOR) +" r2) (c!:printf 
" ((int32_t)%v/(16/CELL))) = %v;\n" r3 r1)))

(put (quote qputv) (quote c!:opcode_printer) (function c!:pqputv))

(de c!:peq (op r1 r2 r3 depth) (c!:printf 
"    %v = (%v == %v ? lisp_true : nil);\n" r1 r2 r3))

(put (quote eq) (quote c!:opcode_printer) (function c!:peq))

(flag (quote (eq)) (quote c!:uses_nil))

(de c!:pequal (op r1 r2 r3 depth) (c!:printf 
"    %v = (equal(%v, %v) ? lisp_true : nil);\n" r1 r2 r3 r2 r3))

(put (quote equal) (quote c!:opcode_printer) (function c!:pequal))

(flag (quote (equal)) (quote c!:uses_nil))

(de c!:pfluidbind (op r1 r2 r3 depth) nil)

(put (quote fluidbind) (quote c!:opcode_printer) (function c!:pfluidbind))

(de c!:pcall (op r1 r2 r3 depth) (prog (w boolfn) (cond ((setq w (get (car r3
) (quote c!:direct_entrypoint))) (progn (c!:printf "    %v = %s(" r1 (cdr w))
(cond (r2 (progn (c!:printf "%v" (car r2)) (prog (var1055) (setq var1055 (
cdr r2)) lab1054 (cond ((null var1055) (return nil))) (prog (a) (setq a (car 
var1055)) (c!:printf ", %v" a)) (setq var1055 (cdr var1055)) (go lab1054)))))
(c!:printf ");\n"))) (t (cond ((setq w (get (car r3) (quote 
c!:direct_predicate))) (progn (setq boolfn t) (c!:printf 
"    %v = (Lisp_Object)%s(" r1 (cdr w)) (cond (r2 (progn (c!:printf "%v" (car
r2)) (prog (var1057) (setq var1057 (cdr r2)) lab1056 (cond ((null var1057) (
return nil))) (prog (a) (setq a (car var1057)) (c!:printf ", %v" a)) (setq 
var1057 (cdr var1057)) (go lab1056))))) (c!:printf ");\n"))) (t (cond ((equal
(car r3) current_procedure) (progn (setq r2 (c!:fix_nargs r2 current_args)) 
(c!:printf "    %v = %s(env" r1 current_c_name) (cond ((or (null r2) (geq (
length r2) 3)) (c!:printf ", %s" (length r2)))) (prog (var1059) (setq var1059
r2) lab1058 (cond ((null var1059) (return nil))) (prog (a) (setq a (car 
var1059)) (c!:printf ", %v" a)) (setq var1059 (cdr var1059)) (go lab1058)) (
c!:printf ");\n"))) (t (cond ((setq w (get (car r3) (quote c!:c_entrypoint)))
(progn (c!:printf "    %v = %s(nil" r1 w) (cond ((or (null r2) (geq (length 
r2) 3)) (c!:printf ", %s" (length r2)))) (prog (var1061) (setq var1061 r2) 
lab1060 (cond ((null var1061) (return nil))) (prog (a) (setq a (car var1061))
(c!:printf ", %v" a)) (setq var1061 (cdr var1061)) (go lab1060)) (c!:printf 
");\n"))) (t (prog (nargs) (setq nargs (length r2)) (c!:printf 
"    fn = elt(env, %s); /* %c */\n" (c!:find_literal (car r3)) (car r3)) (
cond ((equal nargs 1) (c!:printf "    %v = (*qfn1(fn))(qenv(fn)" r1)) (t (
cond ((equal nargs 2) (c!:printf "    %v = (*qfn2(fn))(qenv(fn)" r1)) (t (
c!:printf "    %v = (*qfnn(fn))(qenv(fn), %s" r1 nargs))))) (prog (var1063) (
setq var1063 r2) lab1062 (cond ((null var1063) (return nil))) (prog (a) (setq
a (car var1063)) (c!:printf ", %v" a)) (setq var1063 (cdr var1063)) (go 
lab1062)) (c!:printf ");\n")))))))))) (cond ((not (flagp (car r3) (quote 
c!:no_errors))) (progn (cond ((and (null (cadr r3)) (equal depth 0)) (
c!:printf "    errexit();\n")) (t (progn (c!:printf "    nil = C_nil;\n") (
c!:printf "    if (exception_pending()) ") (c!:pgoto (c!:find_error_label nil
(cadr r3) depth) depth))))))) (cond (boolfn (c!:printf 
"    %v = %v ? lisp_true : nil;\n" r1 r1)))))

(de c!:fix_nargs (r2 act) (cond ((null act) nil) (t (cond ((null r2) (cons 
nil (c!:fix_nargs nil (cdr act)))) (t (cons (car r2) (c!:fix_nargs (cdr r2) (
cdr act))))))))

(put (quote call) (quote c!:opcode_printer) (function c!:pcall))

(de c!:pgoto (lab depth) (prog nil (cond ((atom lab) (return (c!:printf 
"goto %s;\n" lab)))) (setq lab (get (car lab) (quote c!:chosen))) (cond ((
zerop depth) (c!:printf "return onevalue(%v);\n" lab)) (t (cond ((flagp lab (
quote c!:live_across_call)) (c!:printf 
"{ Lisp_Object res = %v; popv(%s); return onevalue(res); }\n" lab depth)) (t 
(c!:printf "{ popv(%s); return onevalue(%v); }\n" depth lab)))))))

(de c!:pifnull (s depth) (c!:printf "%v == nil" (car s)))

(put (quote ifnull) (quote c!:exit_helper) (function c!:pifnull))

(de c!:pifatom (s depth) (c!:printf "!consp(%v)" (car s)))

(put (quote ifatom) (quote c!:exit_helper) (function c!:pifatom))

(de c!:pifsymbol (s depth) (c!:printf "symbolp(%v)" (car s)))

(put (quote ifsymbol) (quote c!:exit_helper) (function c!:pifsymbol))

(de c!:pifnumber (s depth) (c!:printf "is_number(%v)" (car s)))

(put (quote ifnumber) (quote c!:exit_helper) (function c!:pifnumber))

(de c!:pifizerop (s depth) (c!:printf "(%v) == 1" (car s)))

(put (quote ifizerop) (quote c!:exit_helper) (function c!:pifizerop))

(de c!:pifeq (s depth) (c!:printf "%v == %v" (car s) (cadr s)))

(put (quote ifeq) (quote c!:exit_helper) (function c!:pifeq))

(de c!:pifequal (s depth) (c!:printf "equal(%v, %v)" (car s) (cadr s) (car s)
(cadr s)))

(put (quote ifequal) (quote c!:exit_helper) (function c!:pifequal))

(de c!:pifilessp (s depth) (c!:printf "((int32_t)(%v)) < ((int32_t)(%v))" (car s)
(cadr s)))

(put (quote ifilessp) (quote c!:exit_helper) (function c!:pifilessp))

(de c!:pifigreaterp (s depth) (c!:printf "((int32_t)(%v)) > ((int32_t)(%v))" (car
s) (cadr s)))

(put (quote ifigreaterp) (quote c!:exit_helper) (function c!:pifigreaterp))

(de c!:display_flowgraph (s depth dropping_through) (cond ((not (atom s)) (
progn (c!:printf "    ") (c!:pgoto s depth))) (t (cond ((not (flagp s (quote 
c!:visited))) (prog (why where_to) (flag (list s) (quote c!:visited)) (cond (
(or (not dropping_through) (not (equal (get s (quote c!:count)) 1))) (
c!:printf "\n%s:\n" s))) (prog (var1065) (setq var1065 (reverse (get s (quote
c!:contents)))) lab1064 (cond ((null var1065) (return nil))) (prog (k) (setq
k (car var1065)) (c!:print_opcode k depth)) (setq var1065 (cdr var1065)) (go
lab1064)) (setq why (get s (quote c!:why))) (setq where_to (get s (quote 
c!:where_to))) (cond ((and (equal why (quote goto)) (or (not (atom (car 
where_to))) (and (not (flagp (car where_to) (quote c!:visited))) (equal (get 
(car where_to) (quote c!:count)) 1)))) (c!:display_flowgraph (car where_to) 
depth t)) (t (c!:print_exit_condition why where_to depth)))))))))

(fluid (quote (startpoint)))

(de c!:branch_chain (s count) (prog (contents why where_to n) (cond ((not (
atom s)) (return s)) (t (cond ((flagp s (quote c!:visited)) (progn (setq n (
get s (quote c!:count))) (cond ((null n) (setq n 1)) (t (setq n (plus n 1))))
(put s (quote c!:count) n) (return s)))))) (flag (list s) (quote c!:visited)
) (setq contents (get s (quote c!:contents))) (setq why (get s (quote c!:why)
)) (setq where_to (prog (var1067 var1068) (setq var1067 (get s (quote 
c!:where_to))) lab1066 (cond ((null var1067) (return (reversip var1068)))) (
prog (z) (setq z (car var1067)) (setq var1068 (cons (c!:branch_chain z count)
var1068))) (setq var1067 (cdr var1067)) (go lab1066))) (prog nil lab1069 (
cond ((null (and contents (eqcar (car contents) (quote movr)) (equal why (
quote goto)) (not (atom (car where_to))) (equal (caar where_to) (cadr (car 
contents))))) (return nil))) (progn (setq where_to (list (list (cadddr (car 
contents))))) (setq contents (cdr contents))) (go lab1069)) (put s (quote 
c!:contents) contents) (put s (quote c!:where_to) where_to) (cond ((and (null
contents) (equal why (quote goto))) (progn (remflag (list s) (quote 
c!:visited)) (return (car where_to))))) (cond (count (progn (setq n (get s (
quote c!:count))) (cond ((null n) (setq n 1)) (t (setq n (plus n 1)))) (put s
(quote c!:count) n)))) (return s)))

(de c!:one_operand (op) (progn (flag (list op) (quote c!:set_r1)) (flag (list
op) (quote c!:read_r3)) (put op (quote c!:code) (function c!:builtin_one))))

(de c!:two_operands (op) (progn (flag (list op) (quote c!:set_r1)) (flag (
list op) (quote c!:read_r2)) (flag (list op) (quote c!:read_r3)) (put op (
quote c!:code) (function c!:builtin_two))))

(prog (var1071) (setq var1071 (quote (car cdr qcar qcdr null not atom numberp
fixp iminusp iminus iadd1 isub1 modular!-minus))) lab1070 (cond ((null 
var1071) (return nil))) (prog (n) (setq n (car var1071)) (c!:one_operand n)) 
(setq var1071 (cdr var1071)) (go lab1070))

(prog (var1073) (setq var1073 (quote (eq equal atsoc memq iplus2 idifference 
assoc member itimes2 ilessp igreaterp qgetv get modular!-plus 
modular!-difference))) lab1072 (cond ((null var1073) (return nil))) (prog (n)
(setq n (car var1073)) (c!:two_operands n)) (setq var1073 (cdr var1073)) (go
lab1072))

(flag (quote (movr movk movk1 ldrglob call reloadenv fastget fastflag)) (
quote c!:set_r1))

(flag (quote (strglob qputv)) (quote c!:read_r1))

(flag (quote (qputv fastget fastflag)) (quote c!:read_r2))

(flag (quote (movr qputv)) (quote c!:read_r3))

(flag (quote (ldrglob strglob nilglob movk call)) (quote c!:read_env))

(fluid (quote (fn_used nil_used nilbase_used)))

(de c!:live_variable_analysis (all_blocks) (prog (changed z) (prog nil 
lab1080 (progn (setq changed nil) (prog (var1079) (setq var1079 all_blocks) 
lab1078 (cond ((null var1079) (return nil))) (prog (b) (setq b (car var1079))
(prog (w live) (prog (var1075) (setq var1075 (get b (quote c!:where_to))) 
lab1074 (cond ((null var1075) (return nil))) (prog (x) (setq x (car var1075))
(cond ((atom x) (setq live (union live (get x (quote c!:live))))) (t (setq 
live (union live x))))) (setq var1075 (cdr var1075)) (go lab1074)) (setq w (
get b (quote c!:why))) (cond ((not (atom w)) (progn (cond ((or (eqcar w (
quote ifnull)) (eqcar w (quote ifequal))) (setq nil_used t))) (setq live (
union live (cdr w))) (cond ((and (eqcar (car w) (quote call)) (or (flagp (
cadar w) (quote c!:direct_predicate)) (and (flagp (cadar w) (quote 
c!:c_entrypoint)) (not (flagp (cadar w) (quote c!:direct_entrypoint)))))) (
setq nil_used t))) (cond ((and (eqcar (car w) (quote call)) (not (equal (
cadar w) current_procedure)) (not (get (cadar w) (quote c!:direct_entrypoint)
)) (not (get (cadar w) (quote c!:c_entrypoint)))) (progn (setq fn_used t) (
setq live (union (quote (env)) live)))))))) (prog (var1077) (setq var1077 (
get b (quote c!:contents))) lab1076 (cond ((null var1077) (return nil))) (
prog (s) (setq s (car var1077)) (prog (op r1 r2 r3) (setq op (car s)) (setq 
r1 (cadr s)) (setq r2 (caddr s)) (setq r3 (cadddr s)) (cond ((equal op (quote
movk1)) (progn (cond ((equal r3 nil) (setq nil_used t)) (t (cond ((equal r3 
(quote t)) (setq nilbase_used t))))))) (t (cond ((and (atom op) (flagp op (
quote c!:uses_nil))) (setq nil_used t))))) (cond ((flagp op (quote c!:set_r1)
) (cond ((memq r1 live) (setq live (delete r1 live))) (t (cond ((equal op (
quote call)) nil) (t (setq op (quote nop)))))))) (cond ((flagp op (quote 
c!:read_r1)) (setq live (union live (list r1))))) (cond ((flagp op (quote 
c!:read_r2)) (setq live (union live (list r2))))) (cond ((flagp op (quote 
c!:read_r3)) (setq live (union live (list r3))))) (cond ((equal op (quote 
call)) (progn (cond ((or (not (flagp (car r3) (quote c!:no_errors))) (flagp (
car r3) (quote c!:c_entrypoint)) (get (car r3) (quote c!:direct_predicate))) 
(setq nil_used t))) (setq does_call t) (cond ((and (not (eqcar r3 
current_procedure)) (not (get (car r3) (quote c!:direct_entrypoint))) (not (
get (car r3) (quote c!:c_entrypoint)))) (setq fn_used t))) (cond ((not (flagp
(car r3) (quote c!:no_errors))) (flag live (quote c!:live_across_call)))) (
setq live (union live r2))))) (cond ((flagp op (quote c!:read_env)) (setq 
live (union live (quote (env)))))))) (setq var1077 (cdr var1077)) (go lab1076
)) (setq live (sort live (function orderp))) (cond ((not (equal live (get b (
quote c!:live)))) (progn (put b (quote c!:live) live) (setq changed t)))))) (
setq var1079 (cdr var1079)) (go lab1078))) (cond ((null (not changed)) (go 
lab1080)))) (setq z registers) (setq registers (setq stacklocs nil)) (prog (
var1082) (setq var1082 z) lab1081 (cond ((null var1082) (return nil))) (prog 
(r) (setq r (car var1082)) (cond ((flagp r (quote c!:live_across_call)) (setq
stacklocs (cons r stacklocs))) (t (setq registers (cons r registers))))) (
setq var1082 (cdr var1082)) (go lab1081))))

(de c!:insert1 (a b) (cond ((memq a b) b) (t (cons a b))))

(de c!:clash (a b) (cond ((equal (flagp a (quote c!:live_across_call)) (flagp
b (quote c!:live_across_call))) (progn (put a (quote c!:clash) (c!:insert1 b
(get a (quote c!:clash)))) (put b (quote c!:clash) (c!:insert1 a (get b (
quote c!:clash))))))))

(de c!:build_clash_matrix (all_blocks) (prog nil (prog (var1090) (setq 
var1090 all_blocks) lab1089 (cond ((null var1090) (return nil))) (prog (b) (
setq b (car var1090)) (prog (live w) (prog (var1084) (setq var1084 (get b (
quote c!:where_to))) lab1083 (cond ((null var1084) (return nil))) (prog (x) (
setq x (car var1084)) (cond ((atom x) (setq live (union live (get x (quote 
c!:live))))) (t (setq live (union live x))))) (setq var1084 (cdr var1084)) (
go lab1083)) (setq w (get b (quote c!:why))) (cond ((not (atom w)) (progn (
setq live (union live (cdr w))) (cond ((and (eqcar (car w) (quote call)) (not
(get (cadar w) (quote c!:direct_entrypoint))) (not (get (cadar w) (quote 
c!:c_entrypoint)))) (setq live (union (quote (env)) live))))))) (prog (
var1088) (setq var1088 (get b (quote c!:contents))) lab1087 (cond ((null 
var1088) (return nil))) (prog (s) (setq s (car var1088)) (prog (op r1 r2 r3) 
(setq op (car s)) (setq r1 (cadr s)) (setq r2 (caddr s)) (setq r3 (cadddr s))
(cond ((flagp op (quote c!:set_r1)) (cond ((memq r1 live) (progn (setq live 
(delete r1 live)) (cond ((equal op (quote reloadenv)) (setq reloadenv t))) (
prog (var1086) (setq var1086 live) lab1085 (cond ((null var1086) (return nil)
)) (prog (v) (setq v (car var1086)) (c!:clash r1 v)) (setq var1086 (cdr 
var1086)) (go lab1085)))) (t (cond ((equal op (quote call)) nil) (t (progn (
setq op (quote nop)) (rplacd s (cons (car s) (cdr s))) (rplaca s op)))))))) (
cond ((flagp op (quote c!:read_r1)) (setq live (union live (list r1))))) (
cond ((flagp op (quote c!:read_r2)) (setq live (union live (list r2))))) (
cond ((flagp op (quote c!:read_r3)) (setq live (union live (list r3))))) (
cond ((equal op (quote call)) (setq live (union live r2)))) (cond ((flagp op 
(quote c!:read_env)) (setq live (union live (quote (env)))))))) (setq var1088
(cdr var1088)) (go lab1087)))) (setq var1090 (cdr var1090)) (go lab1089)) (
return nil)))

(de c!:allocate_registers (rl) (prog (schedule neighbours allocation) (setq 
neighbours 0) (prog nil lab1094 (cond ((null rl) (return nil))) (prog (w x) (
setq w rl) (prog nil lab1091 (cond ((null (and w (greaterp (length (setq x (
get (car w) (quote c!:clash)))) neighbours))) (return nil))) (setq w (cdr w))
(go lab1091)) (cond (w (progn (setq schedule (cons (car w) schedule)) (setq 
rl (deleq (car w) rl)) (prog (var1093) (setq var1093 x) lab1092 (cond ((null 
var1093) (return nil))) (prog (r) (setq r (car var1093)) (put r (quote 
c!:clash) (deleq (car w) (get r (quote c!:clash))))) (setq var1093 (cdr 
var1093)) (go lab1092)))) (t (setq neighbours (plus neighbours 1))))) (go 
lab1094)) (prog (var1098) (setq var1098 schedule) lab1097 (cond ((null 
var1098) (return nil))) (prog (r) (setq r (car var1098)) (prog (poss) (setq 
poss allocation) (prog (var1096) (setq var1096 (get r (quote c!:clash))) 
lab1095 (cond ((null var1096) (return nil))) (prog (x) (setq x (car var1096))
(setq poss (deleq (get x (quote c!:chosen)) poss))) (setq var1096 (cdr 
var1096)) (go lab1095)) (cond ((null poss) (progn (setq poss (c!:my_gensym)) 
(setq allocation (append allocation (list poss))))) (t (setq poss (car poss))
)) (put r (quote c!:chosen) poss))) (setq var1098 (cdr var1098)) (go lab1097)
) (return allocation)))

(de c!:remove_nops (all_blocks) (prog (var1108) (setq var1108 all_blocks) 
lab1107 (cond ((null var1108) (return nil))) (prog (b) (setq b (car var1108))
(prog (r) (prog (var1103) (setq var1103 (get b (quote c!:contents))) lab1102
(cond ((null var1103) (return nil))) (prog (s) (setq s (car var1103)) (cond 
((not (eqcar s (quote nop))) (prog (op r1 r2 r3) (setq op (car s)) (setq r1 (
cadr s)) (setq r2 (caddr s)) (setq r3 (cadddr s)) (cond ((or (flagp op (quote
c!:set_r1)) (flagp op (quote c!:read_r1))) (setq r1 (get r1 (quote c!:chosen
))))) (cond ((flagp op (quote c!:read_r2)) (setq r2 (get r2 (quote c!:chosen)
)))) (cond ((flagp op (quote c!:read_r3)) (setq r3 (get r3 (quote c!:chosen))
))) (cond ((equal op (quote call)) (setq r2 (prog (var1100 var1101) (setq 
var1100 r2) lab1099 (cond ((null var1100) (return (reversip var1101)))) (prog
(v) (setq v (car var1100)) (setq var1101 (cons (get v (quote c!:chosen)) 
var1101))) (setq var1100 (cdr var1100)) (go lab1099))))) (cond ((not (and (
equal op (quote movr)) (equal r1 r3))) (setq r (cons (list op r1 r2 r3) r))))
)))) (setq var1103 (cdr var1103)) (go lab1102)) (put b (quote c!:contents) (
reversip r)) (setq r (get b (quote c!:why))) (cond ((not (atom r)) (put b (
quote c!:why) (cons (car r) (prog (var1105 var1106) (setq var1105 (cdr r)) 
lab1104 (cond ((null var1105) (return (reversip var1106)))) (prog (v) (setq v
(car var1105)) (setq var1106 (cons (get v (quote c!:chosen)) var1106))) (
setq var1105 (cdr var1105)) (go lab1104)))))))) (setq var1108 (cdr var1108)) 
(go lab1107)))

(fluid (quote (error_labels)))

(de c!:find_error_label (why env depth) (prog (w z) (setq z (list why env 
depth)) (setq w (assoc!*!* z error_labels)) (cond ((null w) (progn (setq w (
cons z (c!:my_gensym))) (setq error_labels (cons w error_labels))))) (return 
(cdr w))))

(de c!:assign (u v c) (cond ((flagp u (quote fluid)) (cons (list (quote 
strglob) v u (c!:find_literal u)) c)) (t (cons (list (quote movr) u nil v) c)
)))

(de c!:insert_tailcall (b) (prog (why dest contents fcall res w) (setq why (
get b (quote c!:why))) (setq dest (get b (quote c!:where_to))) (setq contents
(get b (quote c!:contents))) (prog nil lab1109 (cond ((null (and contents (
not (eqcar (car contents) (quote call))))) (return nil))) (progn (setq w (
cons (car contents) w)) (setq contents (cdr contents))) (go lab1109)) (cond (
(null contents) (return nil))) (setq fcall (car contents)) (setq contents (
cdr contents)) (setq res (cadr fcall)) (prog nil lab1110 (cond ((null w) (
return nil))) (progn (cond ((eqcar (car w) (quote reloadenv)) (setq w (cdr w)
)) (t (cond ((and (eqcar (car w) (quote movr)) (equal (cadddr (car w)) res)) 
(progn (setq res (cadr (car w))) (setq w (cdr w)))) (t (setq res (setq w nil)
)))))) (go lab1110)) (cond ((null res) (return nil))) (cond ((c!:does_return 
res why dest) (cond ((equal (car (cadddr fcall)) current_procedure) (progn (
prog (var1112) (setq var1112 (pair current_args (caddr fcall))) lab1111 (cond
((null var1112) (return nil))) (prog (p) (setq p (car var1112)) (setq 
contents (c!:assign (car p) (cdr p) contents))) (setq var1112 (cdr var1112)) 
(go lab1111)) (put b (quote c!:contents) contents) (put b (quote c!:why) (
quote goto)) (put b (quote c!:where_to) (list restart_label)))) (t (progn (
setq nil_used t) (put b (quote c!:contents) contents) (put b (quote c!:why) (
cons (list (quote call) (car (cadddr fcall))) (caddr fcall))) (put b (quote 
c!:where_to) nil))))))))

(de c!:does_return (res why where_to) (cond ((not (equal why (quote goto))) 
nil) (t (cond ((not (atom (car where_to))) (equal res (caar where_to))) (t (
prog (contents) (setq where_to (car where_to)) (setq contents (reverse (get 
where_to (quote c!:contents)))) (setq why (get where_to (quote c!:why))) (
setq where_to (get where_to (quote c!:where_to))) (prog nil lab1113 (cond ((
null contents) (return nil))) (cond ((eqcar (car contents) (quote reloadenv))
(setq contents (cdr contents))) (t (cond ((and (eqcar (car contents) (quote 
movr)) (equal (cadddr (car contents)) res)) (progn (setq res (cadr (car 
contents))) (setq contents (cdr contents)))) (t (setq res (setq contents nil)
))))) (go lab1113)) (cond ((null res) (return nil)) (t (return (
c!:does_return res why where_to))))))))))

(de c!:pushpop (op v) (prog (n w) (cond ((null v) (return nil))) (setq n (
length v)) (prog nil lab1115 (cond ((null (greaterp n 0)) (return nil))) (
progn (setq w n) (cond ((greaterp w 6) (setq w 6))) (setq n (difference n w))
(cond ((equal w 1) (c!:printf "        %s(%s);\n" op (car v))) (t (progn (
c!:printf "        %s%d(%s" op w (car v)) (setq v (cdr v)) (prog (i) (setq i 
2) lab1114 (cond ((minusp (times 1 (difference w i))) (return nil))) (progn (
c!:printf ",%s" (car v)) (setq v (cdr v))) (setq i (plus i 1)) (go lab1114)) 
(c!:printf ");\n"))))) (go lab1115))))

(de c!:optimise_flowgraph (startpoint all_blocks env argch args) (prog (w n 
locs stacks error_labels fn_used nil_used nilbase_used) (prog (var1117) (setq
var1117 all_blocks) lab1116 (cond ((null var1117) (return nil))) (prog (b) (
setq b (car var1117)) (c!:insert_tailcall b)) (setq var1117 (cdr var1117)) (
go lab1116)) (setq startpoint (c!:branch_chain startpoint nil)) (remflag 
all_blocks (quote c!:visited)) (c!:live_variable_analysis all_blocks) (
c!:build_clash_matrix all_blocks) (cond ((and error_labels env) (setq 
reloadenv t))) (prog (var1121) (setq var1121 env) lab1120 (cond ((null 
var1121) (return nil))) (prog (u) (setq u (car var1121)) (prog (var1119) (
setq var1119 env) lab1118 (cond ((null var1119) (return nil))) (prog (v) (
setq v (car var1119)) (c!:clash (cdr u) (cdr v))) (setq var1119 (cdr var1119)
) (go lab1118))) (setq var1121 (cdr var1121)) (go lab1120)) (setq locs (
c!:allocate_registers registers)) (setq stacks (c!:allocate_registers 
stacklocs)) (flag stacks (quote c!:live_across_call)) (c!:remove_nops 
all_blocks) (setq startpoint (c!:branch_chain startpoint nil)) (remflag 
all_blocks (quote c!:visited)) (setq startpoint (c!:branch_chain startpoint t
)) (remflag all_blocks (quote c!:visited)) (cond (does_call (setq nil_used t)
)) (cond (nil_used (c!:printf "    Lisp_Object nil = C_nil;\n")) (t (cond (
nilbase_used (c!:printf "    nil_as_base\n"))))) (cond (locs (progn (
c!:printf "    Lisp_Object %s" (car locs)) (prog (var1123) (setq var1123 (cdr
locs)) lab1122 (cond ((null var1123) (return nil))) (prog (v) (setq v (car 
var1123)) (c!:printf ", %s" v)) (setq var1123 (cdr var1123)) (go lab1122)) (
c!:printf ";\n")))) (cond (fn_used (c!:printf "    Lisp_Object fn;\n"))) (
cond ((or (equal (car argch) 0) (geq (car argch) 3)) (c!:printf 
"    argcheck(nargs, %s, \q%s\q);\n" (car argch) (cdr argch)))) (cond (
does_call (progn (c!:printf "    if (stack >= stacklimit)\n") (c!:printf 
"    {\n") (c!:pushpop (quote push) args) (c!:printf 
"        env = reclaim(env, \qstack\q, GC_STACK, 0);\n") (c!:pushpop (quote 
pop) (reverse args)) (c!:printf "        nil = C_nil;\n") (c!:printf 
"        if (exception_pending()) return nil;\n") (c!:printf "    }\n")))) (
cond (reloadenv (c!:printf "    push(env);\n")) (t (c!:printf 
"    CSL_IGNORE(env);\n"))) (setq n 0) (cond (stacks (progn (c!:printf 
"/* space for vars preserved across procedure calls */\n") (prog (var1125) (
setq var1125 stacks) lab1124 (cond ((null var1125) (return nil))) (prog (v) (
setq v (car var1125)) (progn (put v (quote c!:location) n) (setq n (plus n 1)
))) (setq var1125 (cdr var1125)) (go lab1124)) (setq w n) (prog nil lab1126 (
cond ((null (geq w 5)) (return nil))) (progn (c!:printf 
"    push5(nil, nil, nil, nil, nil);\n") (setq w (difference w 5))) (go 
lab1126)) (cond ((neq w 0) (progn (cond ((equal w 1) (c!:printf 
"    push(nil);\n")) (t (progn (c!:printf "    push%s(nil" w) (prog (i) (setq
i 2) lab1127 (cond ((minusp (times 1 (difference w i))) (return nil))) (
c!:printf ", nil") (setq i (plus i 1)) (go lab1127)) (c!:printf ");\n")))))))
))) (cond (reloadenv (progn (setq reloadenv n) (setq n (plus n 1))))) (cond (
env (c!:printf "/* copy arguments values to proper place */\n"))) (prog (
var1129) (setq var1129 env) lab1128 (cond ((null var1129) (return nil))) (
prog (v) (setq v (car var1129)) (cond ((flagp (cdr v) (quote 
c!:live_across_call)) (c!:printf "    stack[%s] = %s;\n" (minus (get (get (
cdr v) (quote c!:chosen)) (quote c!:location))) (cdr v))) (t (c!:printf 
"    %s = %s;\n" (get (cdr v) (quote c!:chosen)) (cdr v))))) (setq var1129 (
cdr var1129)) (go lab1128)) (c!:printf "/* end of prologue */\n") (
c!:display_flowgraph startpoint n t) (cond (error_labels (progn (c!:printf 
"/* error exit handlers */\n") (prog (var1131) (setq var1131 error_labels) 
lab1130 (cond ((null var1131) (return nil))) (prog (x) (setq x (car var1131))
(progn (c!:printf "%s:\n" (cdr x)) (c!:print_error_return (caar x) (cadar x)
(caddar x)))) (setq var1131 (cdr var1131)) (go lab1130))))) (remflag 
all_blocks (quote c!:visited))))

(de c!:print_error_return (why env depth) (prog nil (cond ((and reloadenv env
) (c!:printf "    env = stack[%s];\n" (minus reloadenv)))) (cond ((null why) 
(progn (prog (var1133) (setq var1133 env) lab1132 (cond ((null var1133) (
return nil))) (prog (v) (setq v (car var1133)) (c!:printf 
"    qvalue(elt(env, %s)) = %v; /* %c */\n" (c!:find_literal (car v)) (get (
cdr v) (quote c!:chosen)) (car v))) (setq var1133 (cdr var1133)) (go lab1132)
) (cond ((neq depth 0) (c!:printf "    popv(%s);\n" depth))) (c!:printf 
"    return nil;\n"))) (t (cond ((flagp (cadr why) (quote c!:live_across_call
)) (progn (c!:printf "    {   Lisp_Object res = %v;\n" (cadr why)) (prog (
var1135) (setq var1135 env) lab1134 (cond ((null var1135) (return nil))) (
prog (v) (setq v (car var1135)) (c!:printf 
"        qvalue(elt(env, %s)) = %v;\n" (c!:find_literal (car v)) (get (cdr v)
(quote c!:chosen)))) (setq var1135 (cdr var1135)) (go lab1134)) (cond ((neq 
depth 0) (c!:printf "        popv(%s);\n" depth))) (c!:printf 
"        return error(1, %s, res); }\n" (cond ((eqcar why (quote car)) 
"err_bad_car") (t (cond ((eqcar why (quote cdr)) "err_bad_cdr") (t (error 0 (
list why "unknown_error"))))))))) (t (progn (prog (var1137) (setq var1137 env
) lab1136 (cond ((null var1137) (return nil))) (prog (v) (setq v (car var1137
)) (c!:printf "    qvalue(elt(env, %s)) = %v;\n" (c!:find_literal (car v)) (
get (cdr v) (quote c!:chosen)))) (setq var1137 (cdr var1137)) (go lab1136)) (
cond ((neq depth 0) (c!:printf "    popv(%s);\n" depth))) (c!:printf 
"    return error(1, %s, %v);\n" (cond ((eqcar why (quote car)) "err_bad_car"
) (t (cond ((eqcar why (quote cdr)) "err_bad_cdr") (t (error 0 (list why 
"unknown_error")))))) (cadr why)))))))))

(de c!:cand (u env) (prog (w r) (setq w (reverse (cdr u))) (cond ((null w) (
return (c!:cval nil env)))) (setq r (list (list (quote t) (car w)))) (setq w 
(cdr w)) (prog (var1139) (setq var1139 w) lab1138 (cond ((null var1139) (
return nil))) (prog (z) (setq z (car var1139)) (setq r (cons (list (list (
quote null) z) nil) r))) (setq var1139 (cdr var1139)) (go lab1138)) (setq r (
cons (quote cond) r)) (return (c!:cval r env))))

(put (quote and) (quote c!:code) (function c!:cand))

(de c!:ccatch (u env) (error 0 "catch"))

(put (quote catch) (quote c!:code) (function c!:ccatch))

(de c!:ccompile_let (u env) (error 0 "compiler-let"))

(put (quote compiler!-let) (quote c!:code) (function c!:ccompiler_let))

(de c!:ccond (u env) (prog (v join) (setq v (c!:newreg)) (setq join (
c!:my_gensym)) (prog (var1141) (setq var1141 (cdr u)) lab1140 (cond ((null 
var1141) (return nil))) (prog (c) (setq c (car var1141)) (prog (l1 l2) (setq 
l1 (c!:my_gensym)) (setq l2 (c!:my_gensym)) (cond ((atom (cdr c)) (progn (
c!:outop (quote movr) v nil (c!:cval (car c) env)) (c!:endblock (list (quote 
ifnull) v) (list l2 join)))) (t (progn (c!:cjumpif (car c) env l1 l2) (
c!:startblock l1) (c!:outop (quote movr) v nil (c!:cval (cons (quote progn) (
cdr c)) env)) (c!:endblock (quote goto) (list join))))) (c!:startblock l2))) 
(setq var1141 (cdr var1141)) (go lab1140)) (c!:outop (quote movk1) v nil nil)
(c!:endblock (quote goto) (list join)) (c!:startblock join) (return v)))

(put (quote cond) (quote c!:code) (function c!:ccond))

(de c!:valid_cond (x) (cond ((null x) t) (t (cond ((not (c!:valid_list (car x
))) nil) (t (c!:valid_cond (cdr x)))))))

(put (quote cond) (quote c!:valid) (function c!:valid_cond))

(de c!:cdeclare (u env) (error 0 "declare"))

(put (quote declare) (quote c!:code) (function c!:cdeclare))

(de c!:cde (u env) (error 0 "de"))

(put (quote de) (quote c!:code) (function c!:cde))

(de c!:cdefun (u env) (error 0 "defun"))

(put (quote !~defun) (quote c!:code) (function c!:cdefun))

(de c!:ceval_when (u env) (error 0 "eval-when"))

(put (quote eval!-when) (quote c!:code) (function c!:ceval_when))

(de c!:cflet (u env) (error 0 "flet"))

(put (quote flet) (quote c!:code) (function c!:cflet))

(de c!:cfunction (u env) (prog (v) (setq u (cadr u)) (cond ((not (atom u)) (
progn (cond ((not (eqcar u (quote lambda))) (error 0 (list 
"lambda expression needed" u)))) (setq v (dated!-name (quote lambda))) (setq 
pending_functions (cons (cons (quote de) (cons v (cdr u))) pending_functions)
) (setq u v)))) (setq v (c!:newreg)) (c!:outop (quote movk) v u (
c!:find_literal u)) (return v)))

(de c!:valid_function (x) (cond ((atom x) nil) (t (cond ((not (null (cdr x)))
nil) (t (cond ((idp (car x)) t) (t (cond ((atom (car x)) nil) (t (cond ((not
(eqcar (car x) (quote lambda))) nil) (t (cond ((atom (cdar x)) nil) (t (
c!:valid_fndef (cadar x) (cddar x)))))))))))))))

(put (quote function) (quote c!:code) (function c!:cfunction))

(put (quote function) (quote c!:valid) (function c!:valid_function))

(de c!:cgo (u env) (prog (w w1) (setq w1 proglabs) (prog nil lab1142 (cond ((
null (and (null w) w1)) (return nil))) (progn (setq w (assoc!*!* (cadr u) (
car w1))) (setq w1 (cdr w1))) (go lab1142)) (cond ((null w) (error 0 (list u 
"label not set")))) (c!:endblock (quote goto) (list (cadr w))) (return nil)))

(put (quote go) (quote c!:code) (function c!:cgo))

(put (quote go) (quote c!:valid) (function c!:valid_quote))

(de c!:cif (u env) (prog (v join l1 l2 w) (setq v (c!:newreg)) (setq join (
c!:my_gensym)) (setq l1 (c!:my_gensym)) (setq l2 (c!:my_gensym)) (c!:cjumpif 
(car (setq u (cdr u))) env l1 l2) (c!:startblock l1) (c!:outop (quote movr) v
nil (c!:cval (car (setq u (cdr u))) env)) (c!:endblock (quote goto) (list 
join)) (c!:startblock l2) (setq u (cdr u)) (cond (u (setq u (car u)))) (
c!:outop (quote movr) v nil (c!:cval u env)) (c!:endblock (quote goto) (list 
join)) (c!:startblock join) (return v)))

(put (quote if) (quote c!:code) (function c!:cif))

(de c!:clabels (u env) (error 0 "labels"))

(put (quote labels) (quote c!:code) (function c!:clabels))

(de c!:expand!-let (vl b) (cond ((null vl) (cons (quote progn) b)) (t (cond (
(null (cdr vl)) (c!:expand!-let!* vl b)) (t (prog (vars vals) (prog (var1144)
(setq var1144 vl) lab1143 (cond ((null var1144) (return nil))) (prog (v) (
setq v (car var1144)) (cond ((atom v) (progn (setq vars (cons v vars)) (setq 
vals (cons nil vals)))) (t (cond ((atom (cdr v)) (progn (setq vars (cons (car
v) vars)) (setq vals (cons nil vals)))) (t (progn (setq vars (cons (car v) 
vars)) (setq vals (cons (cadr v) vals)))))))) (setq var1144 (cdr var1144)) (
go lab1143)) (return (cons (cons (quote lambda) (cons vars b)) vals))))))))

(de c!:clet (x env) (c!:cval (c!:expand!-let (cadr x) (cddr x)) env))

(de c!:valid_let (x) (cond ((null x) t) (t (cond ((not (c!:valid_cond (car x)
)) nil) (t (c!:valid_list (cdr x)))))))

(put (quote !~let) (quote c!:code) (function c!:clet))

(put (quote !~let) (quote c!:valid) (function c!:valid_let))

(de c!:expand!-let!* (vl b) (cond ((null vl) (cons (quote progn) b)) (t (prog
(var val) (setq var (car vl)) (cond ((not (atom var)) (progn (setq val (cdr 
var)) (setq var (car var)) (cond ((not (atom val)) (setq val (car val))))))) 
(setq b (list (list (quote return) (c!:expand!-let!* (cdr vl) b)))) (cond (
val (setq b (cons (list (quote setq) var val) b)))) (return (cons (quote prog
) (cons (list var) b)))))))

(de c!:clet!* (x env) (c!:cval (c!:expand!-let!* (cadr x) (cddr x)) env))

(put (quote let!*) (quote c!:code) (function c!:clet!*))

(put (quote let!*) (quote c!:valid) (function c!:valid_let))

(de c!:clist (u env) (cond ((null (cdr u)) (c!:cval nil env)) (t (cond ((null
(cddr u)) (c!:cval (cons (quote ncons) (cdr u)) env)) (t (cond ((eqcar (cadr
u) (quote cons)) (c!:cval (list (quote acons) (cadr (cadr u)) (caddr (cadr u
)) (cons (quote list) (cddr u))) env)) (t (cond ((null (cdddr u)) (c!:cval (
cons (quote list2) (cdr u)) env)) (t (c!:cval (list (quote list2!*) (cadr u) 
(caddr u) (cons (quote list) (cdddr u))) env))))))))))

(put (quote list) (quote c!:code) (function c!:clist))

(de c!:clist!* (u env) (prog (v) (setq u (reverse (cdr u))) (setq v (car u)) 
(prog (var1146) (setq var1146 (cdr u)) lab1145 (cond ((null var1146) (return 
nil))) (prog (a) (setq a (car var1146)) (setq v (list (quote cons) a v))) (
setq var1146 (cdr var1146)) (go lab1145)) (return (c!:cval v env))))

(put (quote list!*) (quote c!:code) (function c!:clist!*))

(de c!:ccons (u env) (prog (a1 a2) (setq a1 (s!:improve (cadr u))) (setq a2 (
s!:improve (caddr u))) (cond ((or (equal a2 nil) (equal a2 (quote (quote nil)
)) (equal a2 (quote (list)))) (return (c!:cval (list (quote ncons) a1) env)))
) (cond ((eqcar a1 (quote cons)) (return (c!:cval (list (quote acons) (cadr 
a1) (caddr a1) a2) env)))) (cond ((eqcar a2 (quote cons)) (return (c!:cval (
list (quote list2!*) a1 (cadr a2) (caddr a2)) env)))) (cond ((eqcar a2 (quote
list)) (return (c!:cval (list (quote cons) a1 (list (quote cons) (cadr a2) (
cons (quote list) (cddr a2)))) env)))) (return (c!:ccall (car u) (cdr u) env)
)))

(put (quote cons) (quote c!:code) (function c!:ccons))

(de c!:cget (u env) (prog (a1 a2 w r r1) (setq a1 (s!:improve (cadr u))) (
setq a2 (s!:improve (caddr u))) (cond ((and (eqcar a2 (quote quote)) (idp (
setq w (cadr a2))) (setq w (symbol!-make!-fastget w nil))) (progn (setq r (
c!:newreg)) (c!:outop (quote fastget) r (c!:cval a1 env) (cons w (cadr a2))) 
(return r))) (t (return (c!:ccall (car u) (cdr u) env))))))

(put (quote get) (quote c!:code) (function c!:cget))

(de c!:cflag (u env) (prog (a1 a2 w r r1) (setq a1 (s!:improve (cadr u))) (
setq a2 (s!:improve (caddr u))) (cond ((and (eqcar a2 (quote quote)) (idp (
setq w (cadr a2))) (setq w (symbol!-make!-fastget w nil))) (progn (setq r (
c!:newreg)) (c!:outop (quote fastflag) r (c!:cval a1 env) (cons w (cadr a2)))
(return r))) (t (return (c!:ccall (car u) (cdr u) env))))))

(put (quote flagp) (quote c!:code) (function c!:cflag))

(de c!:cgetv (u env) (cond ((not !*fastvector) (c!:ccall (car u) (cdr u) env)
) (t (c!:cval (cons (quote qgetv) (cdr u)) env))))

(put (quote getv) (quote c!:code) (function c!:cgetv))

(de c!:cputv (u env) (cond ((not !*fastvector) (c!:ccall (car u) (cdr u) env)
) (t (c!:cval (cons (quote qputv) (cdr u)) env))))

(put (quote putv) (quote c!:code) (function c!:cputv))

(de c!:cqputv (x env) (prog (rr) (setq rr (c!:pareval (cdr x) env)) (c!:outop
(quote qputv) (caddr rr) (car rr) (cadr rr)) (return (caddr rr))))

(put (quote qputv) (quote c!:code) (function c!:cqputv))

(de c!:cmacrolet (u env) (error 0 "macrolet"))

(put (quote macrolet) (quote c!:code) (function c!:cmacrolet))

(de c!:cmultiple_value_call (u env) (error 0 "multiple_value_call"))

(put (quote multiple!-value!-call) (quote c!:code) (function 
c!:cmultiple_value_call))

(de c!:cmultiple_value_prog1 (u env) (error 0 "multiple_value_prog1"))

(put (quote multiple!-value!-prog1) (quote c!:code) (function 
c!:cmultiple_value_prog1))

(de c!:cor (u env) (prog (next done v r) (setq v (c!:newreg)) (setq done (
c!:my_gensym)) (setq u (cdr u)) (prog nil lab1147 (cond ((null (cdr u)) (
return nil))) (progn (setq next (c!:my_gensym)) (c!:outop (quote movr) v nil 
(c!:cval (car u) env)) (setq u (cdr u)) (c!:endblock (list (quote ifnull) v) 
(list next done)) (c!:startblock next)) (go lab1147)) (c!:outop (quote movr) 
v nil (c!:cval (car u) env)) (c!:endblock (quote goto) (list done)) (
c!:startblock done) (return v)))

(put (quote or) (quote c!:code) (function c!:cor))

(de c!:cprog (u env) (prog (w w1 bvl local_proglabs progret progexit fluids 
env1) (setq env1 (car env)) (setq bvl (cadr u)) (prog (var1149) (setq var1149
bvl) lab1148 (cond ((null var1149) (return nil))) (prog (v) (setq v (car 
var1149)) (progn (cond ((globalp v) (prog (oo) (setq oo (wrs nil)) (princ 
"+++++ ") (prin v) (princ " converted from GLOBAL to FLUID") (terpri) (wrs oo
) (unglobal (list v)) (fluid (list v))))) (cond ((fluidp v) (progn (setq 
fluids (cons (cons v (c!:newreg)) fluids)) (flag (list (cdar fluids)) (quote 
c!:live_across_call)) (setq env1 (cons (cons (quote c!:dummy!:name) (cdar 
fluids)) env1)) (c!:outop (quote ldrglob) (cdar fluids) v (c!:find_literal v)
) (c!:outop (quote nilglob) nil v (c!:find_literal v)))) (t (progn (setq env1
(cons (cons v (c!:newreg)) env1)) (c!:outop (quote movk1) (cdar env1) nil 
nil)))))) (setq var1149 (cdr var1149)) (go lab1148)) (cond (fluids (c!:outop 
(quote fluidbind) nil nil fluids))) (setq env (cons env1 (append fluids (cdr 
env)))) (setq u (cddr u)) (setq progret (c!:newreg)) (setq progexit (
c!:my_gensym)) (setq blockstack (cons (cons nil (cons progret progexit)) 
blockstack)) (prog (var1151) (setq var1151 u) lab1150 (cond ((null var1151) (
return nil))) (prog (a) (setq a (car var1151)) (cond ((atom a) (cond ((atsoc 
a local_proglabs) (progn (cond ((not (null a)) (progn (setq w (wrs nil)) (
princ "+++++ multiply defined label: ") (prin a) (terpri) (wrs w)))))) (t (
setq local_proglabs (cons (list a (c!:my_gensym)) local_proglabs))))))) (setq
var1151 (cdr var1151)) (go lab1150)) (setq proglabs (cons local_proglabs 
proglabs)) (prog (var1153) (setq var1153 u) lab1152 (cond ((null var1153) (
return nil))) (prog (a) (setq a (car var1153)) (cond ((atom a) (progn (setq w
(cdr (assoc!*!* a local_proglabs))) (cond ((null (cdr w)) (progn (rplacd w t
) (c!:endblock (quote goto) (list (car w))) (c!:startblock (car w))))))) (t (
c!:cval a env)))) (setq var1153 (cdr var1153)) (go lab1152)) (c!:outop (quote
movk1) progret nil nil) (c!:endblock (quote goto) (list progexit)) (
c!:startblock progexit) (prog (var1155) (setq var1155 fluids) lab1154 (cond (
(null var1155) (return nil))) (prog (v) (setq v (car var1155)) (c!:outop (
quote strglob) (cdr v) (car v) (c!:find_literal (car v)))) (setq var1155 (cdr
var1155)) (go lab1154)) (setq blockstack (cdr blockstack)) (setq proglabs (
cdr proglabs)) (return progret)))

(put (quote prog) (quote c!:code) (function c!:cprog))

(de c!:valid_prog (x) (c!:valid_list (cdr x)))

(put (quote prog) (quote c!:valid) (function c!:valid_prog))

(de c!:cprog!* (u env) (error 0 "prog*"))

(put (quote prog!*) (quote c!:code) (function c!:cprog!*))

(de c!:cprog1 (u env) (prog (g) (setq g (c!:my_gensym)) (setq g (list (quote 
prog) (list g) (list (quote setq) g (cadr u)) (cons (quote progn) (cddr u)) (
list (quote return) g))) (return (c!:cval g env))))

(put (quote prog1) (quote c!:code) (function c!:cprog1))

(de c!:cprog2 (u env) (prog (g) (setq u (cdr u)) (setq g (c!:my_gensym)) (
setq g (list (quote prog) (list g) (list (quote setq) g (cadr u)) (cons (
quote progn) (cddr u)) (list (quote return) g))) (setq g (list (quote progn) 
(car u) g)) (return (c!:cval g env))))

(put (quote prog2) (quote c!:code) (function c!:cprog2))

(de c!:cprogn (u env) (prog (r) (setq u (cdr u)) (cond ((equal u nil) (setq u
(quote (nil))))) (prog (var1157) (setq var1157 u) lab1156 (cond ((null 
var1157) (return nil))) (prog (s) (setq s (car var1157)) (setq r (c!:cval s 
env))) (setq var1157 (cdr var1157)) (go lab1156)) (return r)))

(put (quote progn) (quote c!:code) (function c!:cprogn))

(de c!:cprogv (u env) (error 0 "progv"))

(put (quote progv) (quote c!:code) (function c!:cprogv))

(de c!:cquote (u env) (prog (v) (setq u (cadr u)) (setq v (c!:newreg)) (cond 
((or (null u) (equal u (quote t)) (c!:small_number u)) (c!:outop (quote movk1
) v nil u)) (t (c!:outop (quote movk) v u (c!:find_literal u)))) (return v)))

(de c!:valid_quote (x) t)

(put (quote quote) (quote c!:code) (function c!:cquote))

(put (quote quote) (quote c!:valid) (function c!:valid_quote))

(de c!:creturn (u env) (prog (w) (setq w (assoc!*!* nil blockstack)) (cond ((
null w) (error 0 "RETURN out of context"))) (c!:outop (quote movr) (cadr w) 
nil (c!:cval (cadr u) env)) (c!:endblock (quote goto) (list (cddr w))) (
return nil)))

(put (quote return) (quote c!:code) (function c!:creturn))

(put (quote return!-from) (quote c!:code) (function c!:creturn_from))

(de c!:csetq (u env) (prog (v w) (setq v (c!:cval (caddr u) env)) (setq u (
cadr u)) (cond ((not (idp u)) (error 0 (list u "bad variable in setq"))) (t (
cond ((setq w (c!:locally_bound u env)) (c!:outop (quote movr) (cdr w) nil v)
) (t (cond ((flagp u (quote c!:constant)) (error 0 (list u 
"attempt to use setq on a constant"))) (t (c!:outop (quote strglob) v u (
c!:find_literal u)))))))) (return v)))

(put (quote setq) (quote c!:code) (function c!:csetq))

(put (quote noisy!-setq) (quote c!:code) (function c!:csetq))

(de c!:cprivate_tagbody (u env) (prog nil (setq u (cdr u)) (c!:endblock (
quote goto) (list (car u))) (c!:startblock (car u)) (setq current_args (prog 
(var1159 var1160) (setq var1159 current_args) lab1158 (cond ((null var1159) (
return (reversip var1160)))) (prog (v) (setq v (car var1159)) (setq var1160 (
cons (prog (z) (setq z (assoc!*!* v (car env))) (return (cond (z (cdr z)) (t 
v)))) var1160))) (setq var1159 (cdr var1159)) (go lab1158))) (return (c!:cval
(cadr u) env))))

(put (quote c!:private_tagbody) (quote c!:code) (function c!:cprivate_tagbody
))

(de c!:cthe (u env) (c!:cval (caddr u) env))

(put (quote the) (quote c!:code) (function c!:cthe))

(de c!:cthrow (u env) (error 0 "throw"))

(put (quote throw) (quote c!:code) (function c!:cthrow))

(de c!:cunless (u env) (prog (v join l1 l2) (setq v (c!:newreg)) (setq join (
c!:my_gensym)) (setq l1 (c!:my_gensym)) (setq l2 (c!:my_gensym)) (c!:cjumpif 
(cadr u) env l2 l1) (c!:startblock l1) (c!:outop (quote movr) v nil (c!:cval 
(cons (quote progn) (cddr u)) env)) (c!:endblock (quote goto) (list join)) (
c!:startblock l2) (c!:outop (quote movk1) v nil nil) (c!:endblock (quote goto
) (list join)) (c!:startblock join) (return v)))

(put (quote unless) (quote c!:code) (function c!:cunless))

(de c!:cunwind_protect (u env) (error 0 "unwind_protect"))

(put (quote unwind!-protect) (quote c!:code) (function c!:cunwind_protect))

(de c!:cwhen (u env) (prog (v join l1 l2) (setq v (c!:newreg)) (setq join (
c!:my_gensym)) (setq l1 (c!:my_gensym)) (setq l2 (c!:my_gensym)) (c!:cjumpif 
(cadr u) env l1 l2) (c!:startblock l1) (c!:outop (quote movr) v nil (c!:cval 
(cons (quote progn) (cddr u)) env)) (c!:endblock (quote goto) (list join)) (
c!:startblock l2) (c!:outop (quote movk1) v nil nil) (c!:endblock (quote goto
) (list join)) (c!:startblock join) (return v)))

(put (quote when) (quote c!:code) (function c!:cwhen))

(de c!:expand_map (fnargs) (prog (carp fn fn1 args var avar moveon l1 r s 
closed) (setq fn (car fnargs)) (cond ((or (equal fn (quote mapc)) (equal fn (
quote mapcar)) (equal fn (quote mapcan))) (setq carp t))) (setq fnargs (cdr 
fnargs)) (cond ((atom fnargs) (error 0 "bad arguments to map function"))) (
setq fn1 (cadr fnargs)) (prog nil lab1161 (cond ((null (or (eqcar fn1 (quote 
function)) (and (eqcar fn1 (quote quote)) (eqcar (cadr fn1) (quote lambda))))
) (return nil))) (progn (setq fn1 (cadr fn1)) (setq closed t)) (go lab1161)) 
(setq args (car fnargs)) (setq l1 (c!:my_gensym)) (setq r (c!:my_gensym)) (
setq s (c!:my_gensym)) (setq var (c!:my_gensym)) (setq avar var) (cond (carp 
(setq avar (list (quote car) avar)))) (cond (closed (setq fn1 (list fn1 avar)
)) (t (setq fn1 (list (quote apply1) fn1 avar)))) (setq moveon (list (quote 
setq) var (list (quote cdr) var))) (cond ((or (equal fn (quote map)) (equal 
fn (quote mapc))) (setq fn (sublis (list (cons (quote l1) l1) (cons (quote 
var) var) (cons (quote fn) fn1) (cons (quote args) args) (cons (quote moveon)
moveon)) (quote (prog (var) (setq var args) l1 (cond ((not var) (return nil)
)) fn moveon (go l1)))))) (t (cond ((or (equal fn (quote maplist)) (equal fn 
(quote mapcar))) (setq fn (sublis (list (cons (quote l1) l1) (cons (quote var
) var) (cons (quote fn) fn1) (cons (quote args) args) (cons (quote moveon) 
moveon) (cons (quote r) r)) (quote (prog (var r) (setq var args) l1 (cond ((
not var) (return (reversip r)))) (setq r (cons fn r)) moveon (go l1)))))) (t 
(setq fn (sublis (list (cons (quote l1) l1) (cons (quote l2) (c!:my_gensym)) 
(cons (quote var) var) (cons (quote fn) fn1) (cons (quote args) args) (cons (
quote moveon) moveon) (cons (quote r) (c!:my_gensym)) (cons (quote s) (
c!:my_gensym))) (quote (prog (var r s) (setq var args) (setq r (setq s (list 
nil))) l1 (cond ((not var) (return (cdr r)))) (rplacd s fn) l2 (cond ((not (
atom (cdr s))) (setq s (cdr s)) (go l2))) moveon (go l1))))))))) (return fn))
)

(put (quote map) (quote c!:compile_macro) (function c!:expand_map))

(put (quote maplist) (quote c!:compile_macro) (function c!:expand_map))

(put (quote mapc) (quote c!:compile_macro) (function c!:expand_map))

(put (quote mapcar) (quote c!:compile_macro) (function c!:expand_map))

(put (quote mapcon) (quote c!:compile_macro) (function c!:expand_map))

(put (quote mapcan) (quote c!:compile_macro) (function c!:expand_map))

(de c!:expand_carcdr (x) (prog (name) (setq name (cdr (reverse (cdr (explode2
(car x)))))) (setq x (cadr x)) (prog (var1163) (setq var1163 name) lab1162 (
cond ((null var1163) (return nil))) (prog (v) (setq v (car var1163)) (setq x 
(list (cond ((equal v (quote a)) (quote car)) (t (quote cdr))) x))) (setq 
var1163 (cdr var1163)) (go lab1162)) (return x)))

(progn (put (quote caar) (quote c!:compile_macro) (function c!:expand_carcdr)
) (put (quote cadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (
put (quote cdar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (
quote cddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote
caaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cadar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cddar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caaaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caaadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caadar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caaddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cadaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cadadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
caddar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cadddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdaaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdaadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdadar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdaddr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cddaar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cddadr) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cdddar) (quote c!:compile_macro) (function c!:expand_carcdr)) (put (quote 
cddddr) (quote c!:compile_macro) (function c!:expand_carcdr)))

(de c!:builtin_one (x env) (prog (r1 r2) (setq r1 (c!:cval (cadr x) env)) (
c!:outop (car x) (setq r2 (c!:newreg)) (cdr env) r1) (return r2)))

(de c!:builtin_two (x env) (prog (a1 a2 r rr) (setq a1 (cadr x)) (setq a2 (
caddr x)) (setq rr (c!:pareval (list a1 a2) env)) (c!:outop (car x) (setq r (
c!:newreg)) (car rr) (cadr rr)) (return r)))

(de c!:narg (x env) (c!:cval (expand (cdr x) (get (car x) (quote 
c!:binary_version))) env))

(prog (var1165) (setq var1165 (quote ((plus plus2) (times times2) (iplus 
iplus2) (itimes itimes2)))) lab1164 (cond ((null var1165) (return nil))) (
prog (n) (setq n (car var1165)) (progn (put (car n) (quote c!:binary_version)
(cadr n)) (put (car n) (quote c!:code) (function c!:narg)))) (setq var1165 (
cdr var1165)) (go lab1164))

(de c!:cplus2 (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq b (
s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (c!:cval 
(plus a b) env)) (t (cond ((equal a 0) (c!:cval b env)) (t (cond ((equal a 1)
(c!:cval (list (quote add1) b) env)) (t (cond ((equal b 0) (c!:cval a env)) 
(t (cond ((equal b 1) (c!:cval (list (quote add1) a) env)) (t (cond ((equal b
(minus 1)) (c!:cval (list (quote sub1) a) env)) (t (c!:ccall (car u) (cdr u)
env))))))))))))))))

(put (quote plus2) (quote c!:code) (function c!:cplus2))

(de c!:ciplus2 (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq b (
s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (c!:cval 
(plus a b) env)) (t (cond ((equal a 0) (c!:cval b env)) (t (cond ((equal a 1)
(c!:cval (list (quote iadd1) b) env)) (t (cond ((equal b 0) (c!:cval a env))
(t (cond ((equal b 1) (c!:cval (list (quote iadd1) a) env)) (t (cond ((equal
b (minus 1)) (c!:cval (list (quote isub1) a) env)) (t (c!:builtin_two u env)
)))))))))))))))

(put (quote iplus2) (quote c!:code) (function c!:ciplus2))

(de c!:cdifference (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq b
(s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (
c!:cval (difference a b) env)) (t (cond ((equal a 0) (c!:cval (list (quote 
minus) b) env)) (t (cond ((equal b 0) (c!:cval a env)) (t (cond ((equal b 1) 
(c!:cval (list (quote sub1) a) env)) (t (cond ((equal b (minus 1)) (c!:cval (
list (quote add1) a) env)) (t (c!:ccall (car u) (cdr u) env))))))))))))))

(put (quote difference) (quote c!:code) (function c!:cdifference))

(de c!:cidifference (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq 
b (s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (
c!:cval (difference a b) env)) (t (cond ((equal a 0) (c!:cval (list (quote 
iminus) b) env)) (t (cond ((equal b 0) (c!:cval a env)) (t (cond ((equal b 1)
(c!:cval (list (quote isub1) a) env)) (t (cond ((equal b (minus 1)) (c!:cval
(list (quote iadd1) a) env)) (t (c!:builtin_two u env))))))))))))))

(put (quote idifference) (quote c!:code) (function c!:cidifference))

(de c!:ctimes2 (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq b (
s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (c!:cval 
(times a b) env)) (t (cond ((or (equal a 0) (equal b 0)) (c!:cval 0 env)) (t 
(cond ((equal a 1) (c!:cval b env)) (t (cond ((equal b 1) (c!:cval a env)) (t
(cond ((equal a (minus 1)) (c!:cval (list (quote minus) b) env)) (t (cond ((
equal b (minus 1)) (c!:cval (list (quote minus) a) env)) (t (c!:ccall (car u)
(cdr u) env))))))))))))))))

(put (quote times2) (quote c!:code) (function c!:ctimes2))

(de c!:citimes2 (u env) (prog (a b) (setq a (s!:improve (cadr u))) (setq b (
s!:improve (caddr u))) (return (cond ((and (numberp a) (numberp b)) (c!:cval 
(times a b) env)) (t (cond ((or (equal a 0) (equal b 0)) (c!:cval 0 env)) (t 
(cond ((equal a 1) (c!:cval b env)) (t (cond ((equal b 1) (c!:cval a env)) (t
(cond ((equal a (minus 1)) (c!:cval (list (quote iminus) b) env)) (t (cond (
(equal b (minus 1)) (c!:cval (list (quote iminus) a) env)) (t (c!:builtin_two
u env))))))))))))))))

(put (quote itimes2) (quote c!:code) (function c!:citimes2))

(de c!:cminus (u env) (prog (a b) (setq a (s!:improve (cadr u))) (return (
cond ((numberp a) (c!:cval (minus a) env)) (t (cond ((eqcar a (quote minus)) 
(c!:cval (cadr a) env)) (t (c!:ccall (car u) (cdr u) env))))))))

(put (quote minus) (quote c!:code) (function c!:cminus))

(de c!:ceq (x env) (prog (a1 a2 r rr) (setq a1 (s!:improve (cadr x))) (setq 
a2 (s!:improve (caddr x))) (cond ((equal a1 nil) (return (c!:cval (list (
quote null) a2) env))) (t (cond ((equal a2 nil) (return (c!:cval (list (quote
null) a1) env)))))) (setq rr (c!:pareval (list a1 a2) env)) (c!:outop (quote
eq) (setq r (c!:newreg)) (car rr) (cadr rr)) (return r)))

(put (quote eq) (quote c!:code) (function c!:ceq))

(de c!:cequal (x env) (prog (a1 a2 r rr) (setq a1 (s!:improve (cadr x))) (
setq a2 (s!:improve (caddr x))) (cond ((equal a1 nil) (return (c!:cval (list 
(quote null) a2) env))) (t (cond ((equal a2 nil) (return (c!:cval (list (
quote null) a1) env)))))) (setq rr (c!:pareval (list a1 a2) env)) (c!:outop (
cond ((or (c!:eqvalid a1) (c!:eqvalid a2)) (quote eq)) (t (quote equal))) (
setq r (c!:newreg)) (car rr) (cadr rr)) (return r)))

(put (quote equal) (quote c!:code) (function c!:cequal))

(de c!:is_fixnum (x) (and (fixp x) (geq x (minus 134217728)) (leq x 134217727
)))

(de c!:certainlyatom (x) (or (null x) (equal x t) (c!:is_fixnum x) (and (
eqcar x (quote quote)) (or (symbolp (cadr x)) (c!:is_fixnum (cadr x))))))

(de c!:atomlist1 (u) (or (atom u) (and (or (symbolp (car u)) (c!:is_fixnum (
car u))) (c!:atomlist1 (cdr u)))))

(de c!:atomlist (x) (or (null x) (and (eqcar x (quote quote)) (c!:atomlist1 (
cadr x))) (and (eqcar x (quote list)) (or (null (cdr x)) (and (
c!:certainlyatom (cadr x)) (c!:atomlist (cons (quote list) (cddr x)))))) (and
(eqcar x (quote cons)) (c!:certainlyatom (cadr x)) (c!:atomlist (caddr x))))
)

(de c!:atomcar (x) (and (or (eqcar x (quote cons)) (eqcar x (quote list))) (
not (null (cdr x))) (c!:certainlyatom (cadr x))))

(de c!:atomkeys1 (u) (or (atom u) (and (not (atom (car u))) (or (symbolp (
caar u)) (c!:is_fixnum (caar u))) (c!:atomlist1 (cdr u)))))

(de c!:atomkeys (x) (or (null x) (and (eqcar x (quote quote)) (c!:atomkeys1 (
cadr x))) (and (eqcar x (quote list)) (or (null (cdr x)) (and (c!:atomcar (
cadr x)) (c!:atomkeys (cons (quote list) (cddr x)))))) (and (eqcar x (quote 
cons)) (c!:atomcar (cadr x)) (c!:atomkeys (caddr x)))))

(de c!:comsublis (x) (cond ((c!:atomkeys (cadr x)) (cons (quote subla) (cdr x
))) (t nil)))

(put (quote sublis) (quote c!:compile_macro) (function c!:comsublis))

(de c!:comassoc (x) (cond ((or (c!:certainlyatom (cadr x)) (c!:atomkeys (
caddr x))) (cons (quote atsoc) (cdr x))) (t nil)))

(put (quote assoc) (quote c!:compile_macro) (function c!:comassoc))

(put (quote assoc!*!*) (quote c!:compile_macro) (function c!:comassoc))

(de c!:commember (x) (cond ((or (c!:certainlyatom (cadr x)) (c!:atomlist (
caddr x))) (cons (quote memq) (cdr x))) (t nil)))

(put (quote member) (quote c!:compile_macro) (function c!:commember))

(de c!:comdelete (x) (cond ((or (c!:certainlyatom (cadr x)) (c!:atomlist (
caddr x))) (cons (quote deleq) (cdr x))) (t nil)))

(put (quote delete) (quote c!:compile_macro) (function c!:comdelete))

(de c!:ctestif (x env d1 d2) (prog (l1 l2) (setq l1 (c!:my_gensym)) (setq l2 
(c!:my_gensym)) (c!:jumpif (cadr x) l1 l2) (setq x (cddr x)) (c!:startblock 
l1) (c!:jumpif (car x) d1 d2) (c!:startblock l2) (c!:jumpif (cadr x) d1 d2)))

(put (quote if) (quote c!:ctest) (function c!:ctestif))

(de c!:ctestnull (x env d1 d2) (c!:cjumpif (cadr x) env d2 d1))

(put (quote null) (quote c!:ctest) (function c!:ctestnull))

(put (quote not) (quote c!:ctest) (function c!:ctestnull))

(de c!:ctestatom (x env d1 d2) (prog nil (setq x (c!:cval (cadr x) env)) (
c!:endblock (list (quote ifatom) x) (list d1 d2))))

(put (quote atom) (quote c!:ctest) (function c!:ctestatom))

(de c!:ctestconsp (x env d1 d2) (prog nil (setq x (c!:cval (cadr x) env)) (
c!:endblock (list (quote ifatom) x) (list d2 d1))))

(put (quote consp) (quote c!:ctest) (function c!:ctestconsp))

(de c!:ctestsymbol (x env d1 d2) (prog nil (setq x (c!:cval (cadr x) env)) (
c!:endblock (list (quote ifsymbol) x) (list d1 d2))))

(put (quote idp) (quote c!:ctest) (function c!:ctestsymbol))

(de c!:ctestnumberp (x env d1 d2) (prog nil (setq x (c!:cval (cadr x) env)) (
c!:endblock (list (quote ifnumber) x) (list d1 d2))))

(put (quote numberp) (quote c!:ctest) (function c!:ctestnumberp))

(de c!:ctestizerop (x env d1 d2) (prog nil (setq x (c!:cval (cadr x) env)) (
c!:endblock (list (quote ifizerop) x) (list d1 d2))))

(put (quote izerop) (quote c!:ctest) (function c!:ctestizerop))

(de c!:ctesteq (x env d1 d2) (prog (a1 a2 r) (setq a1 (cadr x)) (setq a2 (
caddr x)) (cond ((equal a1 nil) (return (c!:cjumpif a2 env d2 d1))) (t (cond 
((equal a2 nil) (return (c!:cjumpif a1 env d2 d1)))))) (setq r (c!:pareval (
list a1 a2) env)) (c!:endblock (cons (quote ifeq) r) (list d1 d2))))

(put (quote eq) (quote c!:ctest) (function c!:ctesteq))

(de c!:ctesteqcar (x env d1 d2) (prog (a1 a2 r d3) (setq a1 (cadr x)) (setq 
a2 (caddr x)) (setq d3 (c!:my_gensym)) (setq r (c!:pareval (list a1 a2) env))
(c!:endblock (list (quote ifatom) (car r)) (list d2 d3)) (c!:startblock d3) 
(c!:outop (quote qcar) (car r) nil (car r)) (c!:endblock (cons (quote ifeq) r
) (list d1 d2))))

(put (quote eqcar) (quote c!:ctest) (function c!:ctesteqcar))

(global (quote (least_fixnum greatest_fixnum)))

(setq least_fixnum (minus (expt 2 27)))

(setq greatest_fixnum (difference (expt 2 27) 1))

(de c!:small_number (x) (and (fixp x) (geq x least_fixnum) (leq x 
greatest_fixnum)))

(de c!:eqvalid (x) (cond ((atom x) (c!:small_number x)) (t (cond ((flagp (car
x) (quote c!:fixnum_fn)) t) (t (and (equal (car x) (quote quote)) (or (idp (
cadr x)) (c!:small_number (cadr x)))))))))

(flag (quote (iplus iplus2 idifference iminus itimes itimes2)) (quote 
c!:fixnum_fn))

(de c!:ctestequal (x env d1 d2) (prog (a1 a2 r) (setq a1 (s!:improve (cadr x)
)) (setq a2 (s!:improve (caddr x))) (cond ((equal a1 nil) (return (c!:cjumpif
a2 env d2 d1))) (t (cond ((equal a2 nil) (return (c!:cjumpif a1 env d2 d1)))
))) (setq r (c!:pareval (list a1 a2) env)) (c!:endblock (cons (cond ((or (
c!:eqvalid a1) (c!:eqvalid a2)) (quote ifeq)) (t (quote ifequal))) r) (list 
d1 d2))))

(put (quote equal) (quote c!:ctest) (function c!:ctestequal))

(de c!:ctestilessp (x env d1 d2) (prog (r) (setq r (c!:pareval (list (cadr x)
(caddr x)) env)) (c!:endblock (cons (quote ifilessp) r) (list d1 d2))))

(put (quote ilessp) (quote c!:ctest) (function c!:ctestilessp))

(de c!:ctestigreaterp (x env d1 d2) (prog (r) (setq r (c!:pareval (list (cadr
x) (caddr x)) env)) (c!:endblock (cons (quote ifigreaterp) r) (list d1 d2)))
)

(put (quote igreaterp) (quote c!:ctest) (function c!:ctestigreaterp))

(de c!:ctestand (x env d1 d2) (prog (next) (prog (var1167) (setq var1167 (cdr
x)) lab1166 (cond ((null var1167) (return nil))) (prog (a) (setq a (car 
var1167)) (progn (setq next (c!:my_gensym)) (c!:cjumpif a env next d2) (
c!:startblock next))) (setq var1167 (cdr var1167)) (go lab1166)) (c!:endblock
(quote goto) (list d1))))

(put (quote and) (quote c!:ctest) (function c!:ctestand))

(de c!:ctestor (x env d1 d2) (prog (next) (prog (var1169) (setq var1169 (cdr 
x)) lab1168 (cond ((null var1169) (return nil))) (prog (a) (setq a (car 
var1169)) (progn (setq next (c!:my_gensym)) (c!:cjumpif a env d1 next) (
c!:startblock next))) (setq var1169 (cdr var1169)) (go lab1168)) (c!:endblock
(quote goto) (list d2))))

(put (quote or) (quote c!:ctest) (function c!:ctestor))

(fluid (quote (c!:c_entrypoint_list)))

(null (setq c!:c_entrypoint_list (quote ((abs c!:c_entrypoint "Labsval") (
apply0 c!:c_entrypoint "Lapply0") (apply1 c!:c_entrypoint "Lapply1") (apply2 
c!:c_entrypoint "Lapply2") (apply3 c!:c_entrypoint "Lapply3") (ash1 
c!:c_entrypoint "Lash1") (atan c!:c_entrypoint "Latan") (atom c!:c_entrypoint
"Latom") (atsoc c!:c_entrypoint "Latsoc") (batchp c!:c_entrypoint "Lbatchp")
(boundp c!:c_entrypoint "Lboundp") (bps!-putv c!:c_entrypoint "Lbpsputv") (
caaaar c!:c_entrypoint "Lcaaaar") (caaadr c!:c_entrypoint "Lcaaadr") (caaar 
c!:c_entrypoint "Lcaaar") (caadar c!:c_entrypoint "Lcaadar") (caaddr 
c!:c_entrypoint "Lcaaddr") (caadr c!:c_entrypoint "Lcaadr") (caar 
c!:c_entrypoint "Lcaar") (cadaar c!:c_entrypoint "Lcadaar") (cadadr 
c!:c_entrypoint "Lcadadr") (cadar c!:c_entrypoint "Lcadar") (caddar 
c!:c_entrypoint "Lcaddar") (cadddr c!:c_entrypoint "Lcadddr") (caddr 
c!:c_entrypoint "Lcaddr") (cadr c!:c_entrypoint "Lcadr") (car c!:c_entrypoint
"Lcar") (cdaaar c!:c_entrypoint "Lcdaaar") (cdaadr c!:c_entrypoint "Lcdaadr"
) (cdaar c!:c_entrypoint "Lcdaar") (cdadar c!:c_entrypoint "Lcdadar") (cdaddr
c!:c_entrypoint "Lcdaddr") (cdadr c!:c_entrypoint "Lcdadr") (cdar 
c!:c_entrypoint "Lcdar") (cddaar c!:c_entrypoint "Lcddaar") (cddadr 
c!:c_entrypoint "Lcddadr") (cddar c!:c_entrypoint "Lcddar") (cdddar 
c!:c_entrypoint "Lcdddar") (cddddr c!:c_entrypoint "Lcddddr") (cdddr 
c!:c_entrypoint "Lcdddr") (cddr c!:c_entrypoint "Lcddr") (cdr c!:c_entrypoint
"Lcdr") (char!-code c!:c_entrypoint "Lchar_code") (close c!:c_entrypoint 
"Lclose") (codep c!:c_entrypoint "Lcodep") (constantp c!:c_entrypoint 
"Lconstantp") (date c!:c_entrypoint "Ldate") (deleq c!:c_entrypoint "Ldeleq")
(digit c!:c_entrypoint "Ldigitp") (eject c!:c_entrypoint "Leject") (endp 
c!:c_entrypoint "Lendp") (eq c!:c_entrypoint "Leq") (eqcar c!:c_entrypoint 
"Leqcar") (eql c!:c_entrypoint "Leql") (eqn c!:c_entrypoint "Leqn") (error1 
c!:c_entrypoint "Lerror0") (evenp c!:c_entrypoint "Levenp") (evlis 
c!:c_entrypoint "Levlis") (explode c!:c_entrypoint "Lexplode") (explode2 
c!:c_entrypoint "Lexplodec") (explodec c!:c_entrypoint "Lexplodec") (expt 
c!:c_entrypoint "Lexpt") (fix c!:c_entrypoint "Ltruncate") (fixp 
c!:c_entrypoint "Lfixp") (flag c!:c_entrypoint "Lflag") (flagp!*!* 
c!:c_entrypoint "Lflagp") (flagp c!:c_entrypoint "Lflagp") (flagpcar 
c!:c_entrypoint "Lflagpcar") (float c!:c_entrypoint "Lfloat") (floatp 
c!:c_entrypoint "Lfloatp") (fluidp c!:c_entrypoint "Lsymbol_specialp") (gcdn 
c!:c_entrypoint "Lgcd") (gctime c!:c_entrypoint "Lgctime") (gensym 
c!:c_entrypoint "Lgensym") (gensym1 c!:c_entrypoint "Lgensym1") (geq 
c!:c_entrypoint "Lgeq") (get!* c!:c_entrypoint "Lget") (getenv 
c!:c_entrypoint "Lgetenv") (getv c!:c_entrypoint "Lgetv") (svref 
c!:c_entrypoint "Lgetv") (globalp c!:c_entrypoint "Lsymbol_globalp") (
greaterp c!:c_entrypoint "Lgreaterp") (iadd1 c!:c_entrypoint "Liadd1") (
idifference c!:c_entrypoint "Lidifference") (idp c!:c_entrypoint "Lsymbolp") 
(igreaterp c!:c_entrypoint "Ligreaterp") (ilessp c!:c_entrypoint "Lilessp") (
iminus c!:c_entrypoint "Liminus") (iminusp c!:c_entrypoint "Liminusp") (
indirect c!:c_entrypoint "Lindirect") (integerp c!:c_entrypoint "Lintegerp") 
(iplus2 c!:c_entrypoint "Liplus2") (iquotient c!:c_entrypoint "Liquotient") (
iremainder c!:c_entrypoint "Liremainder") (irightshift c!:c_entrypoint 
"Lirightshift") (isub1 c!:c_entrypoint "Lisub1") (itimes2 c!:c_entrypoint 
"Litimes2") (length c!:c_entrypoint "Llength") (lengthc c!:c_entrypoint 
"Llengthc") (leq c!:c_entrypoint "Lleq") (lessp c!:c_entrypoint "Llessp") (
linelength c!:c_entrypoint "Llinelength") (load!-module c!:c_entrypoint 
"Lload_module") (lposn c!:c_entrypoint "Llposn") (macro!-function 
c!:c_entrypoint "Lmacro_function") (macroexpand!-1 c!:c_entrypoint 
"Lmacroexpand_1") (macroexpand c!:c_entrypoint "Lmacroexpand") (make!-bps 
c!:c_entrypoint "Lget_bps") (make!-global c!:c_entrypoint "Lmake_global") (
make!-simple!-string c!:c_entrypoint "Lsmkvect") (make!-special 
c!:c_entrypoint "Lmake_special") (mapstore c!:c_entrypoint "Lmapstore") (max2
c!:c_entrypoint "Lmax2") (memq c!:c_entrypoint "Lmemq") (min2 
c!:c_entrypoint "Lmin2") (minus c!:c_entrypoint "Lminus") (minusp 
c!:c_entrypoint "Lminusp") (mkquote c!:c_entrypoint "Lmkquote") (mkvect 
c!:c_entrypoint "Lmkvect") (mod c!:c_entrypoint "Lmod") (modular!-difference 
c!:c_entrypoint "Lmodular_difference") (modular!-expt c!:c_entrypoint 
"Lmodular_expt") (modular!-minus c!:c_entrypoint "Lmodular_minus") (
modular!-number c!:c_entrypoint "Lmodular_number") (modular!-plus 
c!:c_entrypoint "Lmodular_plus") (modular!-quotient c!:c_entrypoint 
"Lmodular_quotient") (modular!-reciprocal c!:c_entrypoint 
"Lmodular_reciprocal") (modular!-times c!:c_entrypoint "Lmodular_times") (
nconc c!:c_entrypoint "Lnconc") (neq c!:c_entrypoint "Lneq") (not 
c!:c_entrypoint "Lnull") (null c!:c_entrypoint "Lnull") (numberp 
c!:c_entrypoint "Lnumberp") (oddp c!:c_entrypoint "Loddp") (onep 
c!:c_entrypoint "Lonep") (orderp c!:c_entrypoint "Lorderp") (pagelength 
c!:c_entrypoint "Lpagelength") (pairp c!:c_entrypoint "Lconsp") (plist 
c!:c_entrypoint "Lplist") (plusp c!:c_entrypoint "Lplusp") (posn 
c!:c_entrypoint "Lposn") (put c!:c_entrypoint "Lputprop") (putv!-char 
c!:c_entrypoint "Lsputv") (putv c!:c_entrypoint "Lputv") (qcaar 
c!:c_entrypoint "Lcaar") (qcadr c!:c_entrypoint "Lcadr") (qcar 
c!:c_entrypoint "Lcar") (qcdar c!:c_entrypoint "Lcdar") (qcddr 
c!:c_entrypoint "Lcddr") (qcdr c!:c_entrypoint "Lcdr") (qgetv c!:c_entrypoint
"Lgetv") (rds c!:c_entrypoint "Lrds") (reclaim c!:c_entrypoint "Lgc") (remd 
c!:c_entrypoint "Lremd") (remflag c!:c_entrypoint "Lremflag") (remob 
c!:c_entrypoint "Lunintern") (remprop c!:c_entrypoint "Lremprop") (reverse 
c!:c_entrypoint "Lreverse") (reversip c!:c_entrypoint "Lnreverse") (rplaca 
c!:c_entrypoint "Lrplaca") (rplacd c!:c_entrypoint "Lrplacd") (schar 
c!:c_entrypoint "Lsgetv") (seprp c!:c_entrypoint "Lwhitespace_char_p") (
set!-small!-modulus c!:c_entrypoint "Lset_small_modulus") (set 
c!:c_entrypoint "Lset") (smemq c!:c_entrypoint "Lsmemq") (spaces 
c!:c_entrypoint "Lxtab") (special!-char c!:c_entrypoint "Lspecial_char") (
special!-form!-p c!:c_entrypoint "Lspecial_form_p") (spool c!:c_entrypoint 
"Lspool") (stop c!:c_entrypoint "Lstop") (stringp c!:c_entrypoint "Lstringp")
(subla c!:c_entrypoint "Lsubla") (subst c!:c_entrypoint "Lsubst") (
symbol!-env c!:c_entrypoint "Lsymbol_env") (symbol!-function c!:c_entrypoint 
"Lsymbol_function") (symbol!-name c!:c_entrypoint "Lsymbol_name") (
symbol!-set!-definition c!:c_entrypoint "Lsymbol_set_definition") (
symbol!-set!-env c!:c_entrypoint "Lsymbol_set_env") (symbol!-value 
c!:c_entrypoint "Lsymbol_value") (system c!:c_entrypoint "Lsystem") (terpri 
c!:c_entrypoint "Lterpri") (threevectorp c!:c_entrypoint "Lthreevectorp") (
time c!:c_entrypoint "Ltime") (ttab c!:c_entrypoint "Lttab") (tyo 
c!:c_entrypoint "Ltyo") (unmake!-global c!:c_entrypoint "Lunmake_global") (
unmake!-special c!:c_entrypoint "Lunmake_special") (upbv c!:c_entrypoint 
"Lupbv") (verbos c!:c_entrypoint "Lverbos") (wrs c!:c_entrypoint "Lwrs") (
xcons c!:c_entrypoint "Lxcons") (xtab c!:c_entrypoint "Lxtab") (zerop 
c!:c_entrypoint "Lzerop") (cons c!:direct_entrypoint (2 . "cons")) (ncons 
c!:direct_entrypoint (1 . "ncons")) (list2 c!:direct_entrypoint (2 . "list2")
) (list2!* c!:direct_entrypoint (3 . "list2star")) (acons 
c!:direct_entrypoint (3 . "acons")) (list3 c!:direct_entrypoint (3 . "list3")
) (plus2 c!:direct_entrypoint (2 . "plus2")) (difference c!:direct_entrypoint
(2 . "difference2")) (add1 c!:direct_entrypoint (1 . "add1")) (sub1 
c!:direct_entrypoint (1 . "sub1")) (lognot c!:direct_entrypoint (1 . "lognot"
)) (ash c!:direct_entrypoint (2 . "ash")) (quotient c!:direct_entrypoint (2 .
"quot2")) (remainder c!:direct_entrypoint (2 . "Cremainder")) (times2 
c!:direct_entrypoint (2 . "times2")) (minus c!:direct_entrypoint (1 . 
"negate")) (lessp c!:direct_predicate (2 . "lessp2")) (leq 
c!:direct_predicate (2 . "lesseq2")) (greaterp c!:direct_predicate (2 . 
"greaterp2")) (geq c!:direct_predicate (2 . "geq2")) (zerop 
c!:direct_predicate (1 . "zerop"))))))

(null (setq c!:c_entrypoint_list (append c!:c_entrypoint_list (quote ((append
c!:c_entrypoint "Lappend") (assoc c!:c_entrypoint "Lassoc") (compress 
c!:c_entrypoint "Lcompress") (delete c!:c_entrypoint "Ldelete") (divide 
c!:c_entrypoint "Ldivide") (equal c!:c_entrypoint "Lequal") (intern 
c!:c_entrypoint "Lintern") (liter c!:c_entrypoint "Lalpha_char_p") (member 
c!:c_entrypoint "Lmember") (prin c!:c_entrypoint "Lprin") (prin1 
c!:c_entrypoint "Lprin") (prin2 c!:c_entrypoint "Lprinc") (princ 
c!:c_entrypoint "Lprinc") (print c!:c_entrypoint "Lprint") (printc 
c!:c_entrypoint "Lprintc") (read c!:c_entrypoint "Lread") (readch 
c!:c_entrypoint "Lreadch") (sublis c!:c_entrypoint "Lsublis") (vectorp 
c!:c_entrypoint "Lsimple_vectorp") (get c!:direct_entrypoint (2 . "get"))))))
)

(prog (var1171) (setq var1171 c!:c_entrypoint_list) lab1170 (cond ((null 
var1171) (return nil))) (prog (x) (setq x (car var1171)) (put (car x) (cadr x
) (caddr x))) (setq var1171 (cdr var1171)) (go lab1170))

(flag (quote (atom atsoc codep constantp deleq digit endp eq eqcar evenp eql 
fixp flagp flagpcar floatp get globalp iadd1 idifference idp igreaterp ilessp
iminus iminusp indirect integerp iplus2 irightshift isub1 itimes2 liter memq
minusp modular!-difference modular!-expt modular!-minus modular!-number 
modular!-plus modular!-times not null numberp onep pairp plusp qcaar qcadr 
qcar qcdar qcddr qcdr remflag remprop reversip seprp special!-form!-p stringp
symbol!-env symbol!-name symbol!-value threevectorp vectorp zerop)) (quote 
c!:no_errors))


% end of file
