module slfns;  % Complete list of Standard LISP functions.

% Author: Anthony C. Hearn.

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
%       (df 3)                     conflicts with algebraic operator DF
        (difference 2)
        (digit 1)
        (divide 2)
        (dm 3)
%       (dn 3)
%       (ds 3)
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

end;
