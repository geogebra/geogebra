%
% Compiler from Lisp into byte-codes for use with CSL/CCL.
%       Copyright (C) Codemist Ltd, 1990-2010
%


%%
%% Copyright (C) 2010, following the master REDUCE source files.          *
%%                                                                        *
%% Redistribution and use in source and binary forms, with or without     *
%% modification, are permitted provided that the following conditions are *
%% met:                                                                   *
%%                                                                        *
%%     * Redistributions of source code must retain the relevant          *
%%       copyright notice, this list of conditions and the following      *
%%       disclaimer.                                                      *
%%     * Redistributions in binary form must reproduce the above          *
%%       copyright notice, this list of conditions and the following      *
%%       disclaimer in the documentation and/or other materials provided  *
%%       with the distribution.                                           *
%%                                                                        *
%% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
%% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
%% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
%% FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
%% COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
%% INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
%% BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
%% OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
%% ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
%% TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
%% THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
%% DAMAGE.                                                                *
%%



% Pretty-well all internal functions defined here and all fluid and
% global variables have been written with names of the form s!:xxx. This
% might keep them away from most users.  In Common Lisp I may want to put
% them all in a package called "s".

global '(s!:opcodelist);

% The following list of opcodes must be kept in step with the corresponding
% C header file "bytes.h" in the CSL kernel code, and the source file
% "opnames.c".

in "$cslbase/opcodes.red"$

begin
  scalar n;
  n := 0;
  for each v in s!:opcodelist do <<
     put(v, 's!:opcode, n);
     n := n + 1 >>;
  return list(n, 'opcodes, 'allocated)
end;

s!:opcodelist := nil;

fluid '(s!:env_alist);

symbolic procedure s!:vecof l;
  begin
    scalar w;
    w := assoc(l, s!:env_alist);
    if w then return cdr w;
    w := s!:vecof1 l;
    s!:env_alist := (l . w) . s!:env_alist;
    return w
  end;

symbolic procedure s!:vecof1 l;
  begin
    scalar v, n;
    v := mkvect sub1 length l;
    n := 0;
    for each x in l do <<
       putv(v, n, x);
       n := n+1 >>;
    return v
  end;

<< put('batchp,                   's!:builtin0, 0);
   put('date,                     's!:builtin0, 1);
   put('eject,                    's!:builtin0, 2);
   put('error1,                   's!:builtin0, 3);
   put('gctime,                   's!:builtin0, 4);
%  put('gensym,                   's!:builtin0, 5);
   put('lposn,                    's!:builtin0, 6);
%  put('next!-random,             's!:builtin0, 7);
   put('posn,                     's!:builtin0, 8);
   put('read,                     's!:builtin0, 9);
   put('readch,                   's!:builtin0, 10);
   put('terpri,                   's!:builtin0, 11);
!#if (not common!-lisp!-mode)
   put('time,                     's!:builtin0, 12);
!#endif
   put('tyi,                      's!:builtin0, 13);
% load!-spid is not for use by an ordinary programmer - it is used in the
% compilation of unwind!-protect.
   put('load!-spid,               's!:builtin0, 14);

   put('abs,                      's!:builtin1, 0);
   put('add1,                     's!:builtin1, 1);
!#if common!-lisp!-mode
   put('!1!+,                     's!:builtin1, 1);
!#endif
!#if (not common!-lisp!-mode)
   put('atan,                     's!:builtin1, 2);
!#endif
   put('apply0,                   's!:builtin1, 3);
   put('atom,                     's!:builtin1, 4);
   put('boundp,                   's!:builtin1, 5);
   put('char!-code,               's!:builtin1, 6);
   put('close,                    's!:builtin1, 7);
   put('codep,                    's!:builtin1, 8);
!#if (not common!-lisp!-mode)
   put('compress,                 's!:builtin1, 9);
!#endif
   put('constantp,                's!:builtin1, 10);
   put('digit,                    's!:builtin1, 11);
   put('endp,                     's!:builtin1, 12);
   put('eval,                     's!:builtin1, 13);
   put('evenp,                    's!:builtin1, 14);
   put('evlis,                    's!:builtin1, 15);
   put('explode,                  's!:builtin1, 16);
   put('explode2lc,               's!:builtin1, 17);
   put('explode2,                 's!:builtin1, 18);
   put('explodec,                 's!:builtin1, 18);
   put('fixp,                     's!:builtin1, 19);
!#if (not common!-lisp!-mode)
   put('float,                    's!:builtin1, 20);
!#endif
   put('floatp,                   's!:builtin1, 21);
   put('symbol!-specialp,         's!:builtin1, 22);
   put('gc,                       's!:builtin1, 23);
   put('gensym1,                  's!:builtin1, 24);
   put('getenv,                   's!:builtin1, 25);
   put('symbol!-globalp,          's!:builtin1, 26);
   put('iadd1,                    's!:builtin1, 27);
   put('symbolp,                  's!:builtin1, 28);
   put('iminus,                   's!:builtin1, 29);
   put('iminusp,                  's!:builtin1, 30);
   put('indirect,                 's!:builtin1, 31);
   put('integerp,                 's!:builtin1, 32);
!#if (not common!-lisp!-mode)
   put('intern,                   's!:builtin1, 33);
!#endif
   put('isub1,                    's!:builtin1, 34);
   put('length,                   's!:builtin1, 35);
   put('lengthc,                  's!:builtin1, 36);
   put('linelength,               's!:builtin1, 37);
   put('liter,                    's!:builtin1, 38);
   put('load!-module,             's!:builtin1, 39);
   put('lognot,                   's!:builtin1, 40);
!#if (not common!-lisp!-mode)
   put('macroexpand,              's!:builtin1, 41);
   put('macroexpand!-1,           's!:builtin1, 42);
!#endif
   put('macro!-function,          's!:builtin1, 43);
   put('make!-bps,                's!:builtin1, 44);
   put('make!-global,             's!:builtin1, 45);
   put('make!-simple!-string,     's!:builtin1, 46);
   put('make!-special,            's!:builtin1, 47);
   put('minus,                    's!:builtin1, 48);
   put('minusp,                   's!:builtin1, 49);
   put('mkvect,                   's!:builtin1, 50);
   put('modular!-minus,           's!:builtin1, 51);
   put('modular!-number,          's!:builtin1, 52);
   put('modular!-reciprocal,      's!:builtin1, 53);
   put('null,                     's!:builtin1, 54);
   put('oddp,                     's!:builtin1, 55);
   put('onep,                     's!:builtin1, 56);
   put('pagelength,               's!:builtin1, 57);
   put('pairp,                    's!:builtin1, 58);
   put('plist,                    's!:builtin1, 59);
   put('plusp,                    's!:builtin1, 60);
!#if (not common!-lisp!-mode)
   put('prin,                     's!:builtin1, 61);
   put('princ,                    's!:builtin1, 62);
   put('print,                    's!:builtin1, 63);
   put('printc,                   's!:builtin1, 64);
!#endif
%  put('random,                   's!:builtin1, 65);
%  put('rational,                 's!:builtin1, 66);
%  put('load,                     's!:builtin1, 67);
   put('rds,                      's!:builtin1, 68);
   put('remd,                     's!:builtin1, 69);
!#if (not common!-lisp!-mode)
   put('reverse,                  's!:builtin1, 70);
!#endif
   put('reversip,                 's!:builtin1, 71);
   put('seprp,                    's!:builtin1, 72);
   put('set!-small!-modulus,      's!:builtin1, 73);
   put('spaces,                   's!:builtin1, 74);
   put('xtab,                     's!:builtin1, 74);   % = spaces?
   put('special!-char,            's!:builtin1, 75);
   put('special!-form!-p,         's!:builtin1, 76);
   put('spool,                    's!:builtin1, 77);
   put('stop,                     's!:builtin1, 78);
!#if (not common!-lisp!-mode)
   put('stringp,                  's!:builtin1, 79);
!#endif
   put('sub1,                     's!:builtin1, 80);
!#if common!-lisp!-mode
   put('!1!-,                     's!:builtin1, 80);
!#endif
   put('symbol!-env,              's!:builtin1, 81);
   put('symbol!-function,         's!:builtin1, 82);
   put('symbol!-name,             's!:builtin1, 83);
   put('symbol!-value,            's!:builtin1, 84);
   put('system,                   's!:builtin1, 85);
!#if (not common!-lisp!-mode)
   put('fix,                      's!:builtin1, 86);
!#endif
   put('ttab,                     's!:builtin1, 87);
   put('tyo,                      's!:builtin1, 88);
!#if (not common!-lisp!-mode)
   put('remob,                    's!:builtin1, 89);
!#endif
   put('unmake!-global,           's!:builtin1, 90);
   put('unmake!-special,          's!:builtin1, 91);
   put('upbv,                     's!:builtin1, 92);
!#if (not common!-lisp!-mode)
   put('vectorp,                  's!:builtin1, 93);
!#else
   put('simple!-vectorp,          's!:builtin1, 93);
!#endif
   put('verbos,                   's!:builtin1, 94);
   put('wrs,                      's!:builtin1, 95);
   put('zerop,                    's!:builtin1, 96);
% car, cdr etc will pretty-well always turn into single byte operations
% rather than the builtin calls listed here. So the next few lines are
% probably redundant.
   put('car,                      's!:builtin1, 97);
   put('cdr,                      's!:builtin1, 98);
   put('caar,                     's!:builtin1, 99);
   put('cadr,                     's!:builtin1, 100);
   put('cdar,                     's!:builtin1, 101);
   put('cddr,                     's!:builtin1, 102);
   put('qcar,                     's!:builtin1, 103);
   put('qcdr,                     's!:builtin1, 104);
   put('qcaar,                    's!:builtin1, 105);
   put('qcadr,                    's!:builtin1, 106);
   put('qcdar,                    's!:builtin1, 107);
   put('qcddr,                    's!:builtin1, 108);
   put('ncons,                    's!:builtin1, 109);
   put('numberp,                  's!:builtin1, 110);
% is!-spid and spid!-to!-nil are NOT for direct use by ordinary programmers.
% They are part of the support for &optional arguments.
   put('is!-spid,                 's!:builtin1, 111);
   put('spid!-to!-nil,            's!:builtin1, 112);
!#if common!-lisp!-mode
   put('mv!-list!*,               's!:builtin1, 113);
!#endif
   put('append,                   's!:builtin2, 0);
   put('ash,                      's!:builtin2, 1);
!#if (not common!-lisp!-mode)
   put('assoc,                    's!:builtin2, 2);
!#endif
   put('assoc!*!*,                's!:builtin2, 2);
   put('atsoc,                    's!:builtin2, 3);
   put('deleq,                    's!:builtin2, 4);
!#if (not common!-lisp!-mode)
   put('delete,                   's!:builtin2, 5);
   put('divide,                   's!:builtin2, 6);
!#endif
   put('eqcar,                    's!:builtin2, 7);
   put('eql,                      's!:builtin2, 8);
!#if (not common!-lisp!-mode)
   put('eqn,                      's!:builtin2, 9);
!#endif
   put('expt,                     's!:builtin2, 10);
   put('flag,                     's!:builtin2, 11);
   put('flagpcar,                 's!:builtin2, 12);
!#if (not common!-lisp!-mode)
   put('gcdn,                     's!:builtin2, 13);
!#endif
   put('geq,                      's!:builtin2, 14);
   put('getv,                     's!:builtin2, 15);
   put('greaterp,                 's!:builtin2, 16);
   put('idifference,              's!:builtin2, 17);
   put('igreaterp,                's!:builtin2, 18);
   put('ilessp,                   's!:builtin2, 19);
   put('imax,                     's!:builtin2, 20);
   put('imin,                     's!:builtin2, 21);
   put('iplus2,                   's!:builtin2, 22);
   put('iquotient,                's!:builtin2, 23);
   put('iremainder,               's!:builtin2, 24);
   put('irightshift,              's!:builtin2, 25);
   put('itimes2,                  's!:builtin2, 26);
!#if (not common!-lisp!-mode)
%  put('lcm,                      's!:builtin2, 27);
!#endif
   put('leq,                      's!:builtin2, 28);
   put('lessp,                    's!:builtin2, 29);
%  put('make!-random!-state,      's!:builtin2, 30);
   put('max2,                     's!:builtin2, 31);
!#if (not common!-lisp!-mode)
   put('member,                   's!:builtin2, 32);
!#endif
   put('member!*!*,               's!:builtin2, 32);
   put('memq,                     's!:builtin2, 33);
   put('min2,                     's!:builtin2, 34);
   put('mod,                      's!:builtin2, 35);
   put('modular!-difference,      's!:builtin2, 36);
   put('modular!-expt,            's!:builtin2, 37);
   put('modular!-plus,            's!:builtin2, 38);
   put('modular!-quotient,        's!:builtin2, 39);
   put('modular!-times,           's!:builtin2, 40);
   put('nconc,                    's!:builtin2, 41);
   put('neq,                      's!:builtin2, 42);
   put('orderp,                   's!:builtin2, 43);
%  put('ordp,                     's!:builtin2, 43); % alternative name
!#if (not common!-lisp!-mode)
   put('quotient,                 's!:builtin2, 44);
!#endif
   put('remainder,                's!:builtin2, 45);
   put('remflag,                  's!:builtin2, 46);
   put('remprop,                  's!:builtin2, 47);
   put('rplaca,                   's!:builtin2, 48);
   put('rplacd,                   's!:builtin2, 49);
   put('schar,                    's!:builtin2, 50);
   put('set,                      's!:builtin2, 51);
   put('smemq,                    's!:builtin2, 52);
   put('subla,                    's!:builtin2, 53);
   put('sublis,                   's!:builtin2, 54);
   put('symbol!-set!-definition,  's!:builtin2, 55);
   put('symbol!-set!-env,         's!:builtin2, 56);
   put('times2,                   's!:builtin2, 57);
   put('xcons,                    's!:builtin2, 58);
   put('equal,                    's!:builtin2, 59);
   put('eq,                       's!:builtin2, 60);
   put('cons,                     's!:builtin2, 61);
   put('list2,                    's!:builtin2, 62);
!#if (not common!-lisp!-mode)
   put('get,                      's!:builtin2, 63);
!#endif
   put('qgetv,                    's!:builtin2, 64);
   put('flagp,                    's!:builtin2, 65);
   put('apply1,                   's!:builtin2, 66);
   put('difference,               's!:builtin2, 67);
   put('plus2,                    's!:builtin2, 68);
   put('times2,                   's!:builtin2, 69);
   put('equalcar,                 's!:builtin2, 70);
   put('iequal,                   's!:builtin2, 71);
   put('nreverse,                 's!:builtin2, 72);

   put('bps!-putv,                's!:builtin3, 0);
   put('errorset,                 's!:builtin3, 1);
   put('list2!*,                  's!:builtin3, 2);
   put('list3,                    's!:builtin3, 3);
   put('putprop,                  's!:builtin3, 4);
   put('putv,                     's!:builtin3, 5);
   put('putv!-char,               's!:builtin3, 6);
   put('subst,                    's!:builtin3, 7);
   put('apply2,                   's!:builtin3, 8);
   put('acons,                    's!:builtin3, 9);
   nil >>;


% Hex printing, for use when displaying assembly code

symbolic procedure s!:prinhex1 n;
  princ schar("0123456789abcdef", logand(n, 15));

symbolic procedure s!:prinhex2 n;
 << s!:prinhex1 truncate(n, 16);
    s!:prinhex1 n >>;

symbolic procedure s!:prinhex4 n;
 << s!:prinhex2 truncate(n, 256);
    s!:prinhex2 n >>;

%
% The rather elaborate scheme here is to allow for the possibility that the
% horrid user may have defined one of these variables before loading in
% the compiler - I do not want to clobber the user's settings.
%

flag('(comp plap pgwd pwrds notailcall ord nocompile
       carcheckflag savedef carefuleq r2i
       native_code save_native strip_native), 'switch); % for RLISP

if not boundp '!*comp then <<      % compile automatically on "de"
   fluid '(!*comp);
   !*comp := t >>;

if not boundp '!*nocompile then << % do not compile when fasling
   fluid '(!*nocompile);
   !*nocompile := nil >>;

if not boundp '!*plap then <<      % print generated bytecodes
   fluid '(!*plap);
   !*plap := nil >>;

if not boundp '!*pgwd then <<      % equivalent to *plap here
   fluid '(!*pgwd);
   !*pgwd := nil >>;

if not boundp '!*pwrds then <<     % display size of generated code
   fluid '(!*pwrds);
   !*pwrds := t >>;

if not boundp '!*notailcall then << % disable an optimisation
   fluid '(!*notailcall);
   !*notailcall := nil >>;

if not boundp '!*ord then << % disable an optimisation wrt evaluation order
   fluid '(!*ord);
   !*ord := nil >>;

if not boundp '!*savedef then <<   % keep interpretable definition on p-list
   fluid '(!*savedef);
   !*savedef := nil >>;

if not boundp '!*carcheckflag then << % safety/speed control
   fluid '(!*carcheckflag);
   !*carcheckflag := t >>;

if not boundp '!*carefuleq then << % force EQ to be function call
   fluid '(!*carefuleq);           % to permit checking of (EQ number number)
   !*carefuleq := (boundp 'lispsystem!* and
                   not null (member('jlisp, lispsystem!*))) or
                  (boundp '!*features!* and
                   not null (member('!:jlisp, !*features!*))) >>;

if not boundp '!*r2i then << % apply Recursion to Iteration conversions
   fluid '(!*r2i);
   !*r2i := t >>;

% If this flag is set then I will generate C code for the functions that
% I compile as well as the usual bytecoded stuff for the FASL file.
% Making it all link up is a slight delicacy!

if not boundp '!*native_code then << % Compile via C
    fluid '(!*native_code);
% By default I will leave compilation into native code switched off
% at this level. When I build an image I will adjust the switch
% to set a more carefully selected application-specific default.
    !*native_code := nil >>;

if not boundp '!*save_native then << % Do not delete the C code (for debugging)
    fluid '(!*save_native);
    !*save_native := nil >>;

if not boundp '!*strip_native then << % strip symbols from C code
    fluid '(!*strip_native);
    !*strip_native := t >>; % At least on Windows not stripping uses a LOT of space

global '(s!:native_file);

fluid '(s!:current_function s!:current_label s!:current_block s!:current_size
        s!:current_procedure s!:other_defs s!:lexical_env s!:has_closure
        s!:recent_literals s!:used_lexicals s!:a_reg_values s!:current_count);

%
% s!:current_procedure is a list of basic blocks, with the entry-point
% implicit at the first block (that is to say at the END of the list
% while I am building it).. Each block is represented as a list
%  (label exit-condn size . byte-list)
% where the exit-condn can (at various stages during compilation) be
%    nil                            drop through
%    (exit)                         one-byte exit opcodes
%    (jump <label>)                 unconditional jump
%    (jumpcond <L1> <L2>)           two exits from this block
%    ((jumparg ...) <L1> <L2>)      two exits, extra data
%    (icase <L0> <L1> ... <Ln>)     multi-way branch
% furthermore <label> can be either an atom (for a regular label)
% or a list of the form (exit) for an exit condition.
%
% The byte-list is a list of atoms (for genuine bytes in the code
% stream) interleaved with lists that denote comments that appear in
% any assembly listing.

symbolic procedure s!:start_procedure(nargs, nopts, restarg);
 << s!:current_procedure := nil;
    s!:current_label := gensym();
    s!:a_reg_values := nil;
    if not zerop nopts or restarg then <<
       s!:current_block := list(list('OPTARGS, nopts), nopts,
                             list('ARGCOUNT, nargs), nargs);
       s!:current_size := 2 >>
    else if nargs > 3 then <<
       s!:current_block := list(list('ARGCOUNT, nargs), nargs);
       s!:current_size := 1 >>
    else <<
       s!:current_block := nil;
       s!:current_size := 0 >>
 >>;

symbolic procedure s!:set_label x;
 << if s!:current_label then begin scalar w;
       w := s!:current_size . s!:current_block;
       for each x in s!:recent_literals do rplaca(x, w);
       s!:recent_literals := nil;
       s!:current_procedure :=
          (s!:current_label . list('JUMP, x) . w) . s!:current_procedure;
       s!:current_block := nil;
       s!:current_size := 0 end;
    s!:current_label := x;
    s!:a_reg_values := nil
 >>;

symbolic procedure s!:outjump(op, lab);
 begin
    scalar g, w;
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
% unconditional jumps set s!:current_label to nil, which denotes
% a state where control can not reach.
    if op = 'JUMP then op := list(op, lab)
    else if op = 'ICASE then op := op . lab
    else op := list(op, lab, g := gensym());
    w := s!:current_size . s!:current_block;
    for each x in s!:recent_literals do rplaca(x, w);
    s!:recent_literals := nil;
    s!:current_procedure :=
       (s!:current_label . op . w) . s!:current_procedure;
    s!:current_block := nil;
    s!:current_size := 0;
    s!:current_label := g;
    return op
 end;

symbolic procedure s!:outexit();
  begin
    scalar w, op;
    op := '(EXIT);
    if null s!:current_label then return nil;
    w := s!:current_size . s!:current_block;
    for each x in s!:recent_literals do rplaca(x, w);
    s!:recent_literals := nil;
    s!:current_procedure := 
       (s!:current_label . op . w) . s!:current_procedure;
    s!:current_block := nil;
    s!:current_size := 0;
    s!:current_label := nil
 end;

flag('(PUSH PUSHNIL PUSHNIL2 PUSHNIL3 LOSE LOSE2 LOSE3 LOSES
       STORELOC STORELOC0 STORELOC1 STORELOC2 STORELOC3
       STORELOC4 STORELOC5 STORELOC6 STORELOC7
       JUMP JUMPT JUMPNIL 
       JUMPEQ JUMPEQUAL JUMPNE JUMPNEQUAL
       JUMPATOM JUMPNATOM),
     's!:preserves_a);

symbolic procedure s!:outopcode0(op, doc);
  begin
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
    s!:current_block := op . s!:current_block;
    s!:current_size := s!:current_size + 1;
    if !*plap or !*pgwd then s!:current_block := doc . s!:current_block;
  end;

symbolic procedure s!:outopcode1(op, arg, doc);
% doc is just a single item here.
  begin
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
    s!:current_block := arg . op . s!:current_block;
    s!:current_size := s!:current_size + 2;
    if !*plap or !*pgwd then s!:current_block := list(op, doc) . s!:current_block
  end;

% Whenever compiled code needs to refer to any Lisp object it does so
% via a literal table - this procedure manages the table. I common up
% literals if they are EQL.  In the table that I build here I associate
% each literal with a pair (n . l) where n is the number of times the
% literal is referenced (or some other measure of its importance) and l is
% a list of all the places in the codestream where it is referenced.

deflist(
  '((LOADLIT 1)
    (LOADFREE 2)
    (CALL0 2)
    (CALL1 2)
    (LITGET 2)
    (JUMPLITEQ 2)
    (JUMPLITNE 2)
    (JUMPLITEQ!* 2)
    (JUMPLITNE!* 2)
    (JUMPFREET 2)
    (JUMPFREENIL 2)),
   's!:short_form_bonus);

% s!:record_literal is called when a literal reference has just been
% pushed onto s!:current_block. It inserts a reference to the literal
% into a hash table so that it can be resolved into an offset into a literal
% vector later on.

symbolic procedure s!:record_literal env;
  begin
    scalar w, extra;
    w := gethash(car s!:current_block, car env);
    if null w then w := 0 . nil;
    extra := get(cadr s!:current_block, 's!:short_form_bonus);
    if null extra then extra := 10 else extra := extra + 10;
    s!:recent_literals := (nil . s!:current_block) . s!:recent_literals;
    puthash(car s!:current_block, car env,
       (car w+extra) . car s!:recent_literals . cdr w);
  end;

% record_literal_for_jump is used with x of the form (eg)
%    (JUMPLITEQ literal-value <comment>)
% where this list will be used in a jump instruction.

symbolic procedure s!:record_literal_for_jump(x, env, lab);
  begin
    scalar w, extra;
    if null s!:current_label then return nil;
    w := gethash(cadr x, car env);
    if null w then w := 0 . nil;
    extra := get(car x, 's!:short_form_bonus);
    if null extra then extra := 10 else extra := extra + 10;
    x := s!:outjump(x, lab);
    puthash(cadar x, car env, (car w+extra) . (nil . x) . cdr w)
  end;

symbolic procedure s!:outopcode1lit(op, arg, env);
  begin
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
    s!:current_block := arg . op . s!:current_block;
    s!:record_literal env;
    s!:current_size := s!:current_size + 2;
    if !*plap or !*pgwd then s!:current_block := list(op, arg) . s!:current_block
  end;

symbolic procedure s!:outopcode2(op, arg1, arg2, doc);
% This is only used for BIGSTACK
  begin
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
    s!:current_block := arg2 . arg1 . op . s!:current_block;
    s!:current_size := s!:current_size + 3;
    if !*plap or !*pgwd then s!:current_block := (op . doc) . s!:current_block
  end;

symbolic procedure s!:outopcode2lit(op, arg1, arg2, doc, env);
% This is only used for CALLN
  begin
    if not flagp(op, 's!:preserves_a) then s!:a_reg_values := nil;
    if null s!:current_label then return nil;
    s!:current_block := arg1 . op . s!:current_block;
    s!:record_literal env;
    s!:current_block := arg2 . s!:current_block;
    s!:current_size := s!:current_size + 3;
    if !*plap or !*pgwd then s!:current_block := (op . doc) . s!:current_block
  end;

symbolic procedure s!:outlexref(op, arg1, arg2, arg3, doc);
% Only used for LOADLEX and STORELEX
  begin
    scalar arg4;
    if null s!:current_label then return nil;
    if arg1 > 255 or arg2 > 255 or arg3 > 255 then <<
       if arg1 > 2047 or arg2 > 31 or arg3 > 2047 then
          error(0, "stack frame > 2047 or > 31 deep nesting");
       doc := list(op, doc);
       arg4 := logand(arg3, 255);
       arg3 := truncate(arg3,256) + 16*logand(arg1, 15);
       if op = 'LOADLEX then op := 192 + arg2
       else op := 224 + arg2;
       arg2 := truncate(arg1,16);
       arg1 := op;
       op := 'BIGSTACK >>
    else doc := list doc;
    s!:current_block := arg3 . arg2 . arg1 . op . s!:current_block;
    s!:current_size := s!:current_size + 4;
    if arg4 then <<
       s!:current_block := arg4 . s!:current_block;
       s!:current_size := s!:current_size + 1 >>;
    if !*plap or !*pgwd then s!:current_block := (op . doc) . s!:current_block
  end;

% Some opcodes that take a byte offset following them have special forms
% that cope with a few very small values of that offset. Here are tables
% that document what is available, and code that optimises the general case
% into the special opcodes when it can.

put('LOADLIT,    's!:shortform, '(1 . 7) .
        s!:vecof '(!- LOADLIT1 LOADLIT2 LOADLIT3 LOADLIT4
                      LOADLIT5 LOADLIT6 LOADLIT7));
put('LOADFREE,   's!:shortform, '(1 . 4) .
        s!:vecof '(!- LOADFREE1 LOADFREE2 LOADFREE3 LOADFREE4));
put('STOREFREE,  's!:shortform, '(1 . 3) .
        s!:vecof '(!- STOREFREE1 STOREFREE2 STOREFREE3));
put('CALL0,      's!:shortform, '(0 . 3) .
        s!:vecof '(CALL0_0 CALL0_1 CALL0_2 CALL0_3));
put('CALL1,      's!:shortform, '(0 . 5) .
        s!:vecof '(CALL1_0 CALL1_1 CALL1_2 CALL1_3 CALL1_4 CALL1_5));
put('CALL2,      's!:shortform, '(0 . 4) .
        s!:vecof '(CALL2_0 CALL2_1 CALL2_2 CALL2_3 CALL2_4));

put('JUMPFREET,  's!:shortform, '(1 . 4) .
        s!:vecof '(!- JUMPFREE1T JUMPFREE2T JUMPFREE3T JUMPFREE4T));
put('JUMPFREENIL, 's!:shortform, '(1 . 4) .
        s!:vecof '(!- JUMPFREE1NIL JUMPFREE2NIL JUMPFREE3NIL JUMPFREE4NIL));
put('JUMPLITEQ ,  's!:shortform, '(1 . 4) .
        s!:vecof '(!- JUMPLIT1EQ JUMPLIT2EQ JUMPLIT3EQ JUMPLIT4EQ));
put('JUMPLITNE ,  's!:shortform, '(1 . 4) .
        s!:vecof '(!- JUMPLIT1NE JUMPLIT2NE JUMPLIT3NE JUMPLIT4NE));
put('JUMPLITEQ!*, 's!:shortform, get('JUMPLITEQ, 's!:shortform));
put('JUMPLITNE!*, 's!:shortform, get('JUMPLITNE, 's!:shortform));

% These are sub opcodes used with BIGCALL
% The format used with the opcode is as follows:
%    BIGCALL (op:4/high:4) (low:8) ?(nargs:8)
% so the upper four bits of the byte after the BIGCALL opcode select what
% operation is actually performed (from the following list). There is a
% 12-bit literal-vector offset with its 4 high bits packed into that byte
% and the next 8 in the byte "low". Then just in the cases CALLN and JCALLN
% a further byte indicates how many arguments are actually being passed.

put('CALL0,     's!:longform,   0);     % 0
put('CALL1,     's!:longform,   16);    % 1
put('CALL2,     's!:longform,   32);    % 2
put('CALL3,     's!:longform,   48);    % 3
put('CALLN,     's!:longform,   64);    % 4
put('CALL2R,    's!:longform,   80);    % 5 (no JCALL version)
put('LOADFREE,  's!:longform,   96);
put('STOREFREE, 's!:longform,   112);
put('JCALL0,    's!:longform,   128);
put('JCALL1,    's!:longform,   144);
put('JCALL2,    's!:longform,   160);
put('JCALL3,    's!:longform,   176);
put('JCALLN,    's!:longform,   192);
put('FREEBIND,  's!:longform,   208);
put('LITGET,    's!:longform,   224);
put('LOADLIT,   's!:longform,   240);

symbolic procedure s!:literal_order(a, b);
    if cadr a = cadr b then orderp(car a, car b)
    else cadr a > cadr b;

symbolic procedure s!:resolve_literals(env, checksum);
  begin
    scalar w, op, opspec, n, litbytes;
    w := hashcontents car env;
% I sort the literals used in this function so that the ones that are
% used most often come first, and hence get allocated the smaller
% offsets within the table. Here I need to do something magic if there
% are over 256 literals since the regular opcodes only have that much
% addressability.
    w := sort(w, function s!:literal_order);
% I put a checksum of the function that was compiled at the end of the
% literal vector. It will never be referred to by the compiled code, but
% is relevant to native compilation since it can be used to verify that
% a native-code definition is in fact matching the bytecode definition
% that it is about to replace.
    w := append(w, list list(checksum, 0));
    n := length w;
    litbytes := 4*n;
    if n > 4096 then w := s!:too_many_literals(w, n);
    n := 0;
    for each x in w do <<
        rplaca(cdr x, n);    % Turn priorities into offsets
        n := n + 1 >>;
    for each x in w do <<
       n := cadr x;
       for each y in cddr x do <<
          if null car y then <<  % JUMP operation
% If I have a jump that refers to a literal (eg JUMPEQCAR) then I am
% entitled at this stage to leave a 12-bit value in the data structure,
% because as necessary s!:expand_jump will unwind it later to fit in with
% the bytecode restrictions.
             op := caadr y;
             opspec := get(op, 's!:shortform);
             if opspec and caar opspec <= n and n <= cdar opspec then
                 rplaca(cdr y, getv(cdr opspec, n))
             else rplaca(cdadr y, n) >>
          else <<
             op := caddr y;
             if n > 255 then <<
                 rplaca(car y, caar y + 1);          % block is now longer
                 op := get(op, 's!:longform) + truncate(n,256);
                 rplaca(cdr y, ilogand(n, 255));     % low byte offset
                 rplaca(cddr y, 'BIGCALL);           % splice in byte and...
                 rplacd(cdr y, op . cddr y) >>       % make a BIGCALL
             else if (opspec := get(op, 's!:shortform)) and
                     caar opspec <= n and 
                     n <= cdar opspec then <<        % short form available
                 rplaca(car y, caar y - 1);          % block is now shorter
                 rplaca(cdr y, getv(cdr opspec, n)); % replace opcode
                 rplacd(cdr y, cdddr y) >>           % splice out a byte
             else rplaca(cdr y, n) >> >> >>;  % OR just fill in offset
    for each x in w do rplacd(x, cadr x);
    rplaca(env, reversip w . litbytes)
  end;

% s!:too_many_literals is called when there are over 4096 literals called for.
% The simple bytecode instruction set can only cope with 256.
% There are two ways I get around this. To consider these it is useful to
% document the opcodes that reference literals:
%
% operations:  CALL0, CALL1, CALL2, CALL2R, CALL3, CALLN,
%              LOADLIT, LOADFREE, STOREFREE, FREEBIND, LITGET
% and jumps:   JUMPLITEQ, JUMPLITNE, JUMPFREENIL, JUMPFREET,
%              JUMPEQCAR, JUMPNEQCAR, JUMPFLAGP, JUMPNFLAGP
%
% The jumps involved are all versions that can only support short
% branch offsets, and which s!:expand_jump will elaborate into
% code that uses LOADLIT or LOADFREE as necessary. Eg
%
%   JUMPFREENIL var lab  =>  LOADFREE var; JUMPNIL lab
%   JUMPEQCAR lit lab    =>  LOADLIT lit; EQCAR; JUMPT lab
%
% Thus I do not have to worry too much about jumps if I can deal with the
% other operations.  The bytecode interpreter provides an operation
% called BIGCALL that is followed by two bytes. The first contains a 4-bit
% sub-opcode, while the remaining twelve bits provide for access within the
% first 2048 literals for all the above operations and for the JCALL
% operations corresponding to the CALL ones shown (except CALL2R which does
% not have a coresponding JCALL version). So normally I can just map
% references that need to address literals beyong the 256th onto these
% extended opcodes.
%
% Just to be super-careful I will make at least some allowance for things
% with over 2048 literals. In such cases I will identify excess literals
% that are ONLY referenced using the LOADLIT operation (and that directly,
% not via one of the JUMP combinations shown above). For a suitable number
% of these I migrate the literal values into one or more secondary vectors 
% (called v<i> here). These secondary vectors will be stored in the main
% literal vector, and I turn a reference
%           LOADLIT x
% into      LOADLIT v<i>
%           QGETVN  n
% for the offset n of the value x in the secondary vector v<i>.  Note that
% because v<i> will then probably be referenced very often it will stand
% a good chance of ending up within the first 8 slots in the main
% vector so "LOADLIT v<i>" will end turn into just one byte so the effect
% is as if I have a three byte opcode to load a literal from the extended
% pool.  With luck in truly huge procedures there will be a significant
% proportion of literals used in this way and the ones used in more general
% ways will then end up fitting within the first 2048.
%
% In reality the nastiest case I have seen so far has had around 800 literals,
% and that only because of some unduly clumsy compilation and (lack of)
% macro expansion.

symbolic procedure s!:only_loadlit l;
   if null l then t
   else if null caar l then nil
   else if not eqcar(cddar l, 'LOADLIT) then nil
   else s!:only_loadlit cdr l;

symbolic procedure s!:too_many_literals(w, n);
  begin
    scalar k, xvecs, l, r, newrefs, uses, z1;
    k := 0;             % Number of things in current overflow vector
    n := n + 1;
% I must not move the function name down into a sub-vector since it is
% essential (for debugging messages etc) that it ends up in position 0
% in the final literal vector. Hence the test for 10000000 here.
    while n > 4096 and (not null w) do <<
       if (not (cadar w = 10000000)) and s!:only_loadlit cddar w then <<
           l := car w . l;
           n := n-1;
           k := k + 1;
           if k = 256 then <<
              xvecs := l . xvecs;
              l := nil;
              k := 0;
              n := n+1 >> >>
       else r := car w . r;
       w := cdr w >>;
% Complain if migrating LOADLIT literals into sub-vectors does not bring me
% down to 12-bit addressability.
    if n > 4096 then error(0, "function uses too many literals (4096 is limit)");
    xvecs := l . xvecs;
    while r do <<
       w := car r . w;
       r := cdr r >>;
    for each v in xvecs do <<
       newrefs := nil;
       uses := 0;
       r := nil;
       k := 0;
       for each q in v do <<
          for each z in cddr q do <<
% Now z is (hdrp . [litval LOADLIT ...]) and I need to rewrite it to
% be +2 in the length and  (offset . QGETVN . [<vector> LOADLIT ...])
             if car z then rplaca(car z, caar z + 2);
             z1 := 'QGETVN . nil . cddr z;
             rplaca(cdr z, k);
             rplacd(cdr z, z1);
             rplacd(z, cdr z1);
             newrefs := z . newrefs;
             uses := uses + 11 >>;
          r := car q . r;
          k := k + 1 >>;
       newrefs := uses . newrefs;
       newrefs := (s!:vecof reversip r) . newrefs;
       w := newrefs . w >>;
    return sort(w, function s!:literal_order)
  end;

% The following variable is a hook that the Lisp to C compiler
% might like to use.

fluid '(s!:into_c);

symbolic procedure s!:endprocedure(name, env, checksum);
  begin
    scalar pc, labelvals, w, vec;
% First I finish off the final basic block by appending an EXIT operation
    s!:outexit();

    if s!:into_c then return (s!:current_procedure . env);

% Literals have just been collected in an unordered pool so far - now I
% should decide which ones go where in the literal vectors, and insert
% optimised forms of opcodes that can rely on them.
    s!:resolve_literals(env, checksum);

% Tidy up blocks by re-ordering them so as to try to remove chains of
% jumps and similar ugliness.
    s!:current_procedure := s!:tidy_flowgraph s!:current_procedure;

% Look for possible tail calls AFTER resolving literals so that I know the
% exact location in the literal vector of the names of the procedures
% chained to. This means that there can be BIGCALL operations present
% as well as regular CALL opcodes. It also means that come calls that
% address the first few items in the literal vector will have been collapsed
% onto one-byte opcodes.
    if (not !*notailcall) and (not s!:has_closure) then
       s!:current_procedure := s!:try_tailcall s!:current_procedure;

% In all cases I can now turn things like (NIL;EXIT) and (LOADLOC 1;EXIT)
% into single-byte instructions, and discard the LOSE from (LOSE;EXIT).
    s!:current_procedure := s!:tidy_exits s!:current_procedure;

% JUMP instructions are span-dependent, so I need to iterate to
% cope with that as well as with forward references.
    labelvals := s!:resolve_labels();
    pc := car labelvals; labelvals := cdr labelvals;

% Allocate space for the compiled code -  this is done in
% a separate heap.  Maybe soon I will put environment vectors
% in that heap too.  The code heap is not subject to garbage
% colection (at present).
    vec := make!-bps pc;
    pc := 0;
    if !*plap or !*pgwd then <<
       terpri(); ttab 23; princ "+++ "; prin name; princ " +++"; terpri() >>;

% The final pass assembles all the basic blocks and prints a
% listing (if desired)
    for each b in s!:current_procedure do <<
       if car b and flagp(car b, 'used_label) and (!*plap or !*pgwd) then <<
          ttab 20;
          prin car b;
          princ ":";
          terpri() >>;
       pc := s!:plant_basic_block(vec, pc, reverse cdddr b);
       b := cadr b;   % documentation
       if b and
          car b neq 'ICASE and 
          cdr b and
          cddr b then b := list(car b, cadr b);  % Trim unwanted second label
       pc := s!:plant_exit_code(vec, pc, b, labelvals) >>;

% At the end of a procedure I may display a message to record the fact
% that I have compiled it and to show how many bytes were generated
    if !*pwrds then <<
        if posn() neq 0 then terpri();
        princ "+++ "; prin name; princ " compiled, ";
        princ pc; princ " + "; princ (cdar env);
        princ " bytes"; terpri() >>;

% finally I manufacture a literal vector for use with this code segment
    env := caar env;
    if null env then w := nil
    else <<
       w := mkvect cdar env;
       while env do <<
          putv(w, cdar env, caar env);
          env := cdr env >> >>;
    return (vec . w);
  end;

symbolic procedure s!:add_pending(lab, pend, blocks);
  begin
    scalar w;
    if not atom lab then return
       list(gensym(), lab, 0) . pend;
    w := atsoc(lab, pend);
    if w then return w . deleq(w, pend)
    else return atsoc(lab, blocks) . pend
  end;

symbolic procedure s!:invent_exit(x, blocks);
  begin
    scalar w;
    w := blocks;
scan:
    if null w then go to not_found
    else if eqcar(cadar w, x) and caddar w = 0 then return caar w . blocks
    else w := cdr w;
    go to scan;
not_found:
    w := gensym();
    return w . list(w, list x, 0) . blocks
  end;

symbolic procedure s!:destination_label(lab, blocks);
% lab is a label - first find the associated block. If it is empty
% of executable code and it control leaves it unconditionally (either via
% an unconditional jump or an EXIT) then return a value that reflects
% going directly to that destination. Either an atom for a label or a
% non-atomic EXIT marker. In the case of chains of blocks that end up in
% and EXIT I want to ignore LOSE operations, while in all other cases LOSE
% operations are significant.
  begin
    scalar n, w, x;
    w := atsoc(lab, blocks);
    if s!:is_lose_and_exit(w, blocks) then return '(EXIT);
    x := cadr w;
    n := caddr w;
    w := cdddr w;
    if n neq 0 then return lab;  % is there any code?
    if null x or null cdr x then return x  % an exit block
    else if cadr x = lab then return lab   % Very direct loop
    else if null cddr x then return s!:destination_label(cadr x, blocks)
    else return lab
  end;

symbolic procedure s!:remlose b;
% If the instruction stream b has some LOSE opcodes at its tail return
% (q . b') where q is the number of bytes of instructions involved,
% and b' is the instruction sequence with the LOSE opcodes removed.
  begin
    scalar w;
    w := b;
    while w and not atom car w do w := cdr w;
    if null w then return (0 . b);
    if numberp car w and eqcar(cdr w, 'LOSES) then w := (2 . cddr w)
    else if car w = 'LOSE or car w = 'LOSE2 or car w = 'LOSE3 then
       w := (1 . cdr w)
    else return (0 . b);
    b := s!:remlose cdr w;
    return ((car w + car b) . cdr b);
  end;

put('CALL0_0,  's!:shortcall, '(0 . 0));
put('CALL0_1,  's!:shortcall, '(0 . 1));
put('CALL0_2,  's!:shortcall, '(0 . 2));
put('CALL0_3,  's!:shortcall, '(0 . 3));
put('CALL1_0,  's!:shortcall, '(1 . 0));
put('CALL1_1,  's!:shortcall, '(1 . 1));
put('CALL1_2,  's!:shortcall, '(1 . 2));
put('CALL1_3,  's!:shortcall, '(1 . 3));
put('CALL1_4,  's!:shortcall, '(1 . 4));
put('CALL1_5,  's!:shortcall, '(1 . 5));
put('CALL2_0,  's!:shortcall, '(2 . 0));
put('CALL2_1,  's!:shortcall, '(2 . 1));
put('CALL2_2,  's!:shortcall, '(2 . 2));
put('CALL2_3,  's!:shortcall, '(2 . 3));
put('CALL2_4,  's!:shortcall, '(2 . 4));

symbolic procedure s!:remcall b;
% If the instruction stream b has a CALL opcode at its head then
% return (p . q . r . s . b') where p is any comment assocated with
% the call, q is the number of arguments the called function expects,
% r is the literal-vector offset involved, s is the number of bytes
% in the codestream used up and b' is the code stream with the CALL deleted.
% Return NIL if no CALL is found.
  begin
    scalar w, p, q, r, s;
    while b and not atom car b do <<
       p := car b;    % Strip comments, leaves p=nil if none
       b := cdr b >>;
    if null b then return nil  % Nothing left
% The possible interesting cases here are:
%     CALL0_0 .... JCALL2_4   (1 byte opcodes)
%     CALL0 n .... CALL3 n    (2 bytes)
%     CALL2R n                (2 bytes, treat as (SWOP;CALL2 n))
%     CALLN m n               (3 bytes)
%     BIGCALL [CALL0..3] n    (3 bytes)
%     BIGCALL [CALL2R] n      (3 bytes, treat as (SWOP;BIGCALL [CALL2] n))
%     BIGCALL [CALLN] n m     (4 bytes)
    else if numberp car b then <<
       r := car b;
       s := 2;
       b := cdr b;
       if null b then return nil
       else if numberp car b then <<
          q := r;
          r := car b;
          s := 3;
          b := cdr b;
          if b and numberp (w := car b) and eqcar(cdr b, 'BIGCALL) and
             truncate(w, 16) = 4 then <<
              r := 256*logand(w, 15) + r;
              s := 4;
              b := cdr b >>
          else if eqcar(b, 'BIGCALL) then <<
              w := truncate(r,16);
              r := 256*logand(r, 15) + q;
              q := w;
              if q = 5 then <<   % BIGCALL [CALL2R]
                 q := 2;
                 s := s-1; % fudge for the inserted byte
                 b := 'BIGCALL . 'SWOP . cdr b >>;
              if q > 4 then return nil >>
          else if not eqcar(b, 'CALLN) then return nil >>
       else if car b = 'CALL0 then q := 0
       else if car b = 'CALL1 then q := 1
       else if car b = 'CALL2 then q := 2
       else if car b = 'CALL2R then <<
           q := 2;
           s := s-1; % fudge for the inserted byte
           b := 'CALL2 . 'SWOP . cdr b >>
       else if car b = 'CALL3 then q := 3
       else return nil;
       b := cdr b >>
    else if (q := get(car b, 's!:shortcall)) then <<
       r := cdr q;
       q := car q;
       s := 1;
       b := cdr b >>
    else return nil;
    return (p . q . r . s . b);
  end;


symbolic procedure s!:is_lose_and_exit(b, blocks);
% If the block b amounts to just a sequence of LOSE
% operations and then a real exit then return TRUE. Otherwise return NIL.
  begin
    scalar lab, exit;
    lab := car b;
    exit := cadr b;
    b := cdddr b;
    if null exit then return nil;
    b := s!:remlose b;
    b := cdr b;
    while b and not atom car b do b := cdr b;
    if b then return nil      % something in addition to the LOSEs
    else if car exit = 'EXIT then return t
    else if car exit = 'JUMP then <<
       if cadr exit = lab then nil              % very direct loop
       else return s!:is_lose_and_exit(atsoc(cadr exit, blocks), blocks) >>
    else return nil;
  end;

symbolic procedure s!:try_tail_1(b, blocks);
  begin
    scalar exit, size, body, w, w0, w1, w2, op;
    exit := cadr b;
    if null exit then return b
    else if not (car exit = 'EXIT) then <<
       if car exit = 'JUMP then <<
           if not s!:is_lose_and_exit(atsoc(cadr exit, blocks), blocks) then
               return b >>
       else return b >>;
% Here the relevant block either ended with an EXIT, or it ended in an
% unconditional jump to a block that contained no more than LOSE opcodes
% before an EXIT.
    size := caddr b;
    body := cdddr b;
    body := s!:remlose body;
    size := size - car body; body := cdr body;
    w := s!:remcall body;
    if null w then return b;
% w = (comment . nargs . target . bytes . other_bytes)
    w0 := cadr w;       % nargs
    w1 := caddr w;      % target
    body := cddddr w;   % byte-stream tail
    if w0 <= 7 and w1 <= 31 then <<
% Here I can use a version of JCALL that packs both operands into
% a single post-byte
       body := 'JCALL . body;
       body := (32*w0 + w1) . body;
       size := size-1 >>
% For reasonably short calls I can use a generic JCALL where both nargs
% and the target address use one byte each
    else if w1 < 256 then body := w0 . w1 . 'JCALLN . body
% When the offset required is over-large I use variants on BIGCALL.
    else <<
        body := 'BIGCALL . body;
        w2 := logand(w1, 255); w1 := truncate(w1,256);
        if w0 < 4 then body := w2 . (w1 + 16*w0 + 128) . body
        else <<
            body := w0 . w2 . (w1 + (16*4 + 128)) . body;
            size := size + 1 >> >>;
    if car w then body := append(car w, list('TAIL)) . body;
    rplaca(cdr b, nil);
    rplaca(cddr b, size-cadddr w+3);
    rplacd(cddr b, body);
    return b
  end;

symbolic procedure s!:try_tailcall b;
   for each v in b collect s!:try_tail_1(v, b);

symbolic procedure s!:tidy_exits_1(b, blocks);
  begin
    scalar exit, size, body, comm, w, w0, w1, w2, op;
    exit := cadr b;
    if null exit then return b
    else if not (car exit = 'EXIT) then <<
       if car exit = 'JUMP then <<
           if not s!:is_lose_and_exit(atsoc(cadr exit, blocks), blocks) then
               return b >>
       else return b >>;
% Here the relevant block either ended with an EXIT, or it ended in an
% unconditional jump to a block that contained no more than LOSE opcodes
% before an EXIT.
    size := caddr b;
    body := cdddr b;
    body := s!:remlose body;  % chucks away any LOSEs just before the EXIT
    size := size - car body; body := cdr body;
    while body and not atom car body do <<
        comm := car body;
        body := cdr body >>;
    if      eqcar(body, 'VNIL)     then w := 'NILEXIT
    else if eqcar(body, 'LOADLOC0) then w := 'LOC0EXIT
    else if eqcar(body, 'LOADLOC1) then w := 'LOC1EXIT
    else if eqcar(body, 'LOADLOC2) then w := 'LOC2EXIT
    else w := nil;
    if w then <<
        rplaca(cdr b, list w);
        body := cdr body;
        size := size - 1 >>
    else if comm then body := comm . body;
    rplaca(cddr b, size);
    rplacd(cddr b, body);
    return b
  end;

symbolic procedure s!:tidy_exits b;
   for each v in b collect s!:tidy_exits_1(v, b);

symbolic procedure s!:tidy_flowgraph b;
  begin
    scalar r, pending;
% The blocks are initially built up in reverse order - correct that here
    b := reverse b;
% The first block is where we enter the procedure, and so it always has to
% be the first thing emitted.
    pending := list car b;
    while pending do begin
      scalar c, x, l1, l2, done1, done2;
      c := car pending;             % next block to emit
      pending := cdr pending;
      flag(list car c, 'coded);     % this label has now been set
      x := cadr c;                  % exit status of current block
      if null x or null cdr x then
        r := c . r
      else if car x = 'ICASE then <<
% I reverse the list of case labels here so that I add pending blocks in
% and order that will typically arrange that the cases come out in the
% generated code in the "natural" order.
        rplacd(x, reversip cdr x);
        for each ll on cdr x do <<
          l1 := s!:destination_label(car ll, b);
          if not atom l1 then <<
             l1 := s!:invent_exit(car l1, b);
             b := cdr l1;
             l1 := cadr l1 >>;
          rplaca(ll, l1);
          done1 := flagp(l1, 'coded);
          flag(list l1, 'used_label);
          if not done1 then pending := s!:add_pending(l1, pending, b) >>;
        rplacd(x, reversip cdr x);
        r := c . r >>
      else if null cddr x then <<   % unconditional jump
        l1 := s!:destination_label(cadr x, b);
        if not atom l1 then         % goto exit turns into exit block
           c := car c . l1 . cddr c
        else if flagp(l1, 'coded) then <<
           flag(list l1, 'used_label);
           c := car c . list(car x, l1) . cddr c >>
        else <<
           c := car c . nil . cddr c;
           pending := s!:add_pending(l1, pending, b) >>;
        r := c . r >>
      else <<                       % conditional jump
        l1 := s!:destination_label(cadr x, b);
        l2 := s!:destination_label(caddr x, b);
        done1 := atom l1 and flagp(l1, 'coded);
        done2 := atom l2 and flagp(l2, 'coded);
        if done1 then <<
           if done2 then <<
              flag(list l1, 'used_label);
              rplaca(cdadr c, l1);
% Here I synthesize a block to carry the unconditional jump to L2 that I need
              pending := list(gensym(), list('JUMP, l2), 0) . pending >>
           else <<
              flag(list l1, 'used_label);
              rplaca(cdadr c, l1);
              pending := s!:add_pending(l2, pending, b) >> >>
        else <<
           if done2 then <<
              flag(list l2, 'used_label);
              rplaca(cadr c, s!:negate_jump car x);
              rplaca(cdadr c, l2);
              pending := s!:add_pending(l1, pending, b) >>
           else <<
% neither l1 nor l2 have been done - I make a somewhat random selection
% as to which I will emit first
              if not atom l1 then <<    % invent block for exit case
                 l1 := s!:invent_exit(car l1, b);
                 b := cdr l1;
                 l1 := car l1 >>;
              flag(list l1, 'used_label);
              rplaca(cdadr c, l1);
% it is possible here that l1 was an exit case and s!:invent_exit discovers a
% previously emitted suitable exit block, in which case l1 is now a reference
% to an already-set label, so it should not be pushed onto the list of
% pending blocks
              if not flagp(l1, 'coded) then
                  pending := s!:add_pending(l1, pending, b);
              pending := s!:add_pending(l2, pending, b) >> >>;
        r := c . r >>
      end;
    return reverse r
  end;

deflist('((JUMPNIL      JUMPT)
          (JUMPT        JUMPNIL)
          (JUMPATOM     JUMPNATOM)
          (JUMPNATOM    JUMPATOM)
          (JUMPEQ       JUMPNE)
          (JUMPNE       JUMPEQ)
          (JUMPEQUAL    JUMPNEQUAL)
          (JUMPNEQUAL   JUMPEQUAL)
          (JUMPL0NIL    JUMPL0T)
          (JUMPL0T      JUMPL0NIL)
          (JUMPL1NIL    JUMPL1T)
          (JUMPL1T      JUMPL1NIL)
          (JUMPL2NIL    JUMPL2T)
          (JUMPL2T      JUMPL2NIL)
          (JUMPL3NIL    JUMPL3T)
          (JUMPL3T      JUMPL3NIL)
          (JUMPL4NIL    JUMPL4T)
          (JUMPL4T      JUMPL4NIL)
          (JUMPL0ATOM   JUMPL0NATOM)
          (JUMPL0NATOM  JUMPL0ATOM)
          (JUMPL1ATOM   JUMPL1NATOM)
          (JUMPL1NATOM  JUMPL1ATOM)
          (JUMPL2ATOM   JUMPL2NATOM)
          (JUMPL2NATOM  JUMPL2ATOM)
          (JUMPL3ATOM   JUMPL3NATOM)
          (JUMPL3NATOM  JUMPL3ATOM)
          (JUMPST0NIL   JUMPST0T)
          (JUMPST0T     JUMPST0NIL)
          (JUMPST1NIL   JUMPST1T)
          (JUMPST1T     JUMPST1NIL)
          (JUMPST2NIL   JUMPST2T)
          (JUMPST2T     JUMPST2NIL)
          (JUMPFREE1NIL JUMPFREE1T)
          (JUMPFREE1T   JUMPFREE1NIL)
          (JUMPFREE2NIL JUMPFREE2T)
          (JUMPFREE2T   JUMPFREE2NIL)
          (JUMPFREE3NIL JUMPFREE3T)
          (JUMPFREE3T   JUMPFREE3NIL)
          (JUMPFREE4NIL JUMPFREE4T)
          (JUMPFREE4T   JUMPFREE4NIL)
          (JUMPFREENIL  JUMPFREET)
          (JUMPFREET    JUMPFREENIL)
          (JUMPLIT1EQ   JUMPLIT1NE)
          (JUMPLIT1NE   JUMPLIT1EQ)
          (JUMPLIT2EQ   JUMPLIT2NE)
          (JUMPLIT2NE   JUMPLIT2EQ)
          (JUMPLIT3EQ   JUMPLIT3NE)
          (JUMPLIT3NE   JUMPLIT3EQ)
          (JUMPLIT4EQ   JUMPLIT4NE)
          (JUMPLIT4NE   JUMPLIT4EQ)
          (JUMPLITEQ    JUMPLITNE)
          (JUMPLITNE    JUMPLITEQ)
          (JUMPLITEQ!*  JUMPLITNE!*)
          (JUMPLITNE!*  JUMPLITEQ!*)
          (JUMPB1NIL    JUMPB1T)
          (JUMPB1T      JUMPB1NIL)
          (JUMPB2NIL    JUMPB2T)
          (JUMPB2T      JUMPB2NIL)
          (JUMPFLAGP    JUMPNFLAGP)
          (JUMPNFLAGP   JUMPFLAGP)
          (JUMPEQCAR    JUMPNEQCAR)
          (JUMPNEQCAR   JUMPEQCAR)
                                    ), 'negjump);

symbolic procedure s!:negate_jump x;
   if atom x then get(x, 'negjump)
   else rplaca(x, get(car x, 'negjump));

symbolic procedure s!:resolve_labels();
  begin
    scalar w, labelvals, converged, pc, x;
    repeat <<
       converged := t;
       pc := 0;
       for each b in s!:current_procedure do <<
% Each block has a label at its head - set the label, or
% on subsequent passes check to see if its value has changed -
% if anything has happened clear the converged flag so that another
% pass will be taken
          w := assoc!*!*(car b, labelvals);
          if null w then <<
             converged := nil;
             w := car b . pc;
             labelvals := w . labelvals >>
          else if cdr w neq pc then <<
             rplacd(w, pc);
             converged := nil >>;

% move on pc by the length of the block excluding any exit code
          pc := pc + caddr b;

          x := cadr b;
          if null x then nil                      % no EXIT needed
          else if null cdr x then pc := pc + 1    % EXIT operation
          else if car x = 'ICASE then pc := pc + 2*length x
          else <<
% by this stage I demand that (a) the labels in jump instructions
% are simple atomic labels (and never (EXIT) psuedo-labels) and (b)
% there are no longer any 2-way jumps - everything is just (JUMPcond l)
% where the alternative case is handled by just dropping through.
% But note that the JUMPcond will sometimes be composite (eg in effect
% <LOADLOC 2/JUMPT>, eg)
             w := assoc!*!*(cadr x, labelvals);
             if null w then <<
                w := 128;           % will be a "short" offset
                converged := nil >>
             else w := cdr w - pc;  % the offset
             w := s!:expand_jump(car x, w);  % list of bytes to plant
             pc := pc + length w >> >>
       >> until converged;
    return (pc . labelvals)
  end;

symbolic procedure s!:plant_basic_block(vec, pc, b);
% For this to give a sensible display the list of bytes must have
% a comment/annotation after every operation in it.  This is ensured by
% s!:outop.
  begin
    scalar tagged;
    for each i in b do <<
       if atom i then <<
          if symbolp i then i := get(i, 's!:opcode);
          if not tagged and (!*plap or !*pgwd) then <<
             s!:prinhex4 pc; princ ":"; ttab 8; tagged := t >>;
          if not fixp i or i < 0 or i > 255 then error("bad byte to put", i);
          bps!-putv(vec, pc, i);
          if !*plap or !*pgwd then << s!:prinhex2 i; princ " " >>;
          pc := pc + 1 >>
       else if !*plap or !*pgwd then <<
          ttab 23;
          princ car i;
          for each w in cdr i do << princ " "; prin w >>;
          terpri(); tagged := nil >> >>;
    return pc
 end;

symbolic procedure s!:plant_bytes(vec, pc, bytelist, doc);
  begin
    if !*plap or !*pgwd then << s!:prinhex4 pc; princ ":"; ttab 8 >>;
    for each v in bytelist do <<
       if symbolp v then v := get(v, 's!:opcode);
       if not fixp v or v < 0 or v > 255 then error("bad byte to put", v);
       bps!-putv(vec, pc, v);
       if !*plap or !*pgwd then <<
           if posn() > 50 then << terpri(); ttab 8 >>;
           s!:prinhex2 v; princ " " >>;
       pc := pc + 1 >>;
    if !*plap or !*pgwd then <<
       if posn() > 23 then terpri();
       ttab 23;
       princ car doc;
       for each w in cdr doc do <<
           if posn() > 65 then << terpri(); ttab 23 >>;
           princ " "; prin w >>;
       terpri() >>;
    return pc
  end;

symbolic procedure s!:plant_exit_code(vec, pc, b, labelvals);
  begin
    scalar w, loc, low, high, r;
    if null b then return pc
    else if null cdr b then     % Simple EXIT
       return s!:plant_bytes(vec, pc, list get(car b, 's!:opcode), b)
    else if car b = 'ICASE then <<
       loc := pc + 3;
       for each ll in cdr b do <<
          w := cdr assoc!*!*(ll, labelvals) - loc;
          loc := loc + 2;
          if w < 0 then <<
             w := -w;
             low := ilogand(w, 255);
             high := 128 + truncate(w - low, 256) >>
          else <<
             low := ilogand(w, 255);
             high := truncate(w - low, 256) >>;
          r := low . high . r >>;
       r := get('ICASE, 's!:opcode) . length cddr b . reversip r;
       return s!:plant_bytes(vec, pc, r, b) >>;
    w := cdr assoc!*!*(cadr b, labelvals) - pc;
    w := s!:expand_jump(car b, w);  % list of bytes to plant
    return s!:plant_bytes(vec, pc, w, b)
  end;

deflist('(
          (JUMPL0NIL    ((LOADLOC0) JUMPNIL))
          (JUMPL0T      ((LOADLOC0) JUMPT))
          (JUMPL1NIL    ((LOADLOC1) JUMPNIL))
          (JUMPL1T      ((LOADLOC1) JUMPT))
          (JUMPL2NIL    ((LOADLOC2) JUMPNIL))
          (JUMPL2T      ((LOADLOC2) JUMPT))
          (JUMPL3NIL    ((LOADLOC3) JUMPNIL))
          (JUMPL3T      ((LOADLOC3) JUMPT))
          (JUMPL4NIL    ((LOADLOC4) JUMPNIL))
          (JUMPL4T      ((LOADLOC4) JUMPT))
          (JUMPL0ATOM   ((LOADLOC0) JUMPATOM))
          (JUMPL0NATOM  ((LOADLOC0) JUMPNATOM))
          (JUMPL1ATOM   ((LOADLOC1) JUMPATOM))
          (JUMPL1NATOM  ((LOADLOC1) JUMPNATOM))
          (JUMPL2ATOM   ((LOADLOC2) JUMPATOM))
          (JUMPL2NATOM  ((LOADLOC2) JUMPNATOM))
          (JUMPL3ATOM   ((LOADLOC3) JUMPATOM))
          (JUMPL3NATOM  ((LOADLOC3) JUMPNATOM))
          (JUMPST0NIL   ((STORELOC0) JUMPNIL))
          (JUMPST0T     ((STORELOC0) JUMPT))
          (JUMPST1NIL   ((STORELOC1) JUMPNIL))
          (JUMPST1T     ((STORELOC1) JUMPT))
          (JUMPST2NIL   ((STORELOC2) JUMPNIL))
          (JUMPST2T     ((STORELOC2) JUMPT))
          (JUMPFREE1NIL ((LOADFREE1) JUMPNIL))
          (JUMPFREE1T   ((LOADFREE1) JUMPT))
          (JUMPFREE2NIL ((LOADFREE2) JUMPNIL))
          (JUMPFREE2T   ((LOADFREE2) JUMPT))
          (JUMPFREE3NIL ((LOADFREE3) JUMPNIL))
          (JUMPFREE3T   ((LOADFREE3) JUMPT))
          (JUMPFREE4NIL ((LOADFREE4) JUMPNIL))
          (JUMPFREE4T   ((LOADFREE4) JUMPT))
          (JUMPFREENIL  ((LOADFREE !*) JUMPNIL))
          (JUMPFREET    ((LOADFREE !*) JUMPT))
          (JUMPLIT1EQ   ((LOADLIT1) JUMPEQ))
          (JUMPLIT1NE   ((LOADLIT1) JUMPNE))
          (JUMPLIT2EQ   ((LOADLIT2) JUMPEQ))
          (JUMPLIT2NE   ((LOADLIT2) JUMPNE))
          (JUMPLIT3EQ   ((LOADLIT3) JUMPEQ))
          (JUMPLIT3NE   ((LOADLIT3) JUMPNE))
          (JUMPLIT4EQ   ((LOADLIT4) JUMPEQ))
          (JUMPLIT4NE   ((LOADLIT4) JUMPNE))
          (JUMPLITEQ    ((LOADLIT !*) JUMPEQ))
          (JUMPLITNE    ((LOADLIT !*) JUMPNE))
          (JUMPLITEQ!*  ((LOADLIT !* SWOP) JUMPEQ))
          (JUMPLITNE!*  ((LOADLIT !* SWOP) JUMPNE))
          (JUMPB1NIL    ((BUILTIN1 !*) JUMPNIL))
          (JUMPB1T      ((BUILTIN1 !*) JUMPT))
          (JUMPB2NIL    ((BUILTIN2 !*) JUMPNIL))
          (JUMPB2T      ((BUILTIN2 !*) JUMPT))
          (JUMPFLAGP    ((LOADLIT !* FLAGP) JUMPT))
          (JUMPNFLAGP   ((LOADLIT !* FLAGP) JUMPNIL))
          (JUMPEQCAR    ((LOADLIT !* EQCAR) JUMPT))
          (JUMPNEQCAR   ((LOADLIT !* EQCAR) JUMPNIL))
          ), 's!:expand_jump);

fluid '(s!:backwards_jump s!:longer_jump);

<< s!:backwards_jump := make!-simple!-string 256;
   s!:longer_jump    := make!-simple!-string 256;
   nil >>;


for each op in '(
   (JUMP         JUMP_B       JUMP_L       JUMP_BL)
   (JUMPNIL      JUMPNIL_B    JUMPNIL_L    JUMPNIL_BL)
   (JUMPT        JUMPT_B      JUMPT_L      JUMPT_BL)
   (JUMPATOM     JUMPATOM_B   JUMPATOM_L   JUMPATOM_BL)
   (JUMPNATOM    JUMPNATOM_B  JUMPNATOM_L  JUMPNATOM_BL)
   (JUMPEQ       JUMPEQ_B     JUMPEQ_L     JUMPEQ_BL)
   (JUMPNE       JUMPNE_B     JUMPNE_L     JUMPNE_BL)
   (JUMPEQUAL    JUMPEQUAL_B  JUMPEQUAL_L  JUMPEQUAL_BL)
   (JUMPNEQUAL   JUMPNEQUAL_B JUMPNEQUAL_L JUMPNEQUAL_BL)
   (CATCH        CATCH_B      CATCH_L      CATCH_BL)) do <<
   putv!-char(s!:backwards_jump,
         get(car op, 's!:opcode), get(cadr op, 's!:opcode));
   putv!-char(s!:backwards_jump,
         get(caddr op, 's!:opcode), get(cadddr op, 's!:opcode));
   putv!-char(s!:longer_jump,
         get(car op, 's!:opcode), get(caddr op, 's!:opcode));
   putv!-char(s!:longer_jump,
         get(cadr op, 's!:opcode), get(cadddr op, 's!:opcode)) >>;


symbolic procedure s!:expand_jump(op, offset);
  begin
    scalar arg, low, high, opcode, expanded;
    if not atom op then <<
       arg := cadr op;
       op := car op;
       offset := offset - 1 >>;
    expanded := get(op, 's!:expand_jump);
% The special compact jumps only support forward jumps by up to 255 bytes -
% they do not allow for backwards or long jumps. I also expand the jumps
% to a longer form if the argument is 256 or higher (in which case I must
% have an expansion that uses LOADLIT or LOADFREE and I turn it into one
% of the longer sequences that can access up to position 2047 in the literal
% vector.
    if expanded and
       not (2 <= offset and offset < 256+2 and
            (null arg or arg < 256)) then <<
       % Here I need to expand the branch
       op := cadr expanded;
       expanded := car expanded;
       if arg then <<
          if arg > 2047 then
              error(0, "function uses too many literals (2048 limit)")
          else if arg > 255 then begin
             scalar high, low;
%            low := ilogand(expanded, 255);
%            high := truncate(expanded - low, 256);
             low := ilogand(arg, 255);
             high := truncate(arg - low, 256);
% LOADLIT and LOADFREE are encoded here as sub-types of the BIGCALL opcode.
             expanded := 'BIGCALL .
               (get(car expanded, 's!:longform) + high) .
               low . cddr expanded end
          else expanded := subst(arg, '!*, expanded);
          offset := offset + 1 >>;
       offset := offset - length expanded;
       arg := nil >>
    else expanded := nil;
    opcode := get(op, 's!:opcode);
    if null opcode then error(0, list(op, offset, "invalid block exit"));
    if -256+2 < offset and offset < 256+2 then offset := offset - 2
    else << high := t; offset := offset - 3 >>;
    if offset < 0 then <<
       opcode := byte!-getv(s!:backwards_jump, opcode);
       offset := -offset >>;
    if high then <<
       low := logand(offset, 255);
       high := truncate(offset - low,256) >>
    else if (low := offset) > 255 then error(0, "Bad offset in expand_jump");
    if arg then return list(opcode, arg, low)
    else if not high then return append(expanded, list(opcode, low))
    else return append(expanded,
         list(byte!-getv(s!:longer_jump, opcode), high, low))
  end;

%
% Each expression processed occurs in a context - for CSL we have
% a strict interpretation of 'program' context - and to allow
% some optimisations we also distinguish 'top level', 'normal'
% and 'void'.  Contexts are coded numerically, which is a long
% established hack, but pretty inscrutable - here are the codes..
%      0    a top-level expression, value is value of current fn
%      1    an expression whose value is needed, but not at top level
%      2    an expression whose value is not needed (e.g. in PROGN)
%      4    value not needed because in PROG context: top level PROG
%      5    in prog context, PROGs value was needed
%      6    in prog context, PROG was in void context
%
% Note that to support REDUCE I seem to have to allow GO and RETURN
% to appear anywhere within a progn that is itself in prog context,
% and not just in the final position.  For COMMON Lisp GO and
% RETURN-FROM statements can appear pretty-well anywhere.

symbolic procedure s!:comval(x, env, context);
% s!:comval is the central dispatch procedure in the compiler - calling
% it will generate code to load the value of x into the A register,
% pushing down previous value through B.
  begin
    scalar helper;
    x := s!:improve x;
    if atom x then return s!:comatom(x, env, context)
    else if eqcar(car x, 'lambda) then
        return s!:comlambda(cadar x, cddar x, cdr x, env, context)
    else if car x eq s!:current_function then s!:comcall(x, env, context)
!#if common!-lisp!-mode
    else if helper := s!:local_macro car x then <<
       if atom cdr helper then
          s!:comval('funcall . cdr helper . cdr x, env, context)
       else s!:comval(funcall('lambda . cdr helper, x), env, context) >>
!#endif
    else if (helper := get(car x, 's!:compilermacro)) and
            (helper := funcall(helper, x, env, context)) then
        return s!:comval(helper, env, context)
    else if (helper := get(car x, 's!:newname)) then
        return s!:comval(helper . cdr x, env, context)
    else if helper := get(car x, 's!:compfn) then
        return funcall(helper, x, env, context)
    else if helper := macro!-function car x then
       return s!:comval(funcall(helper, x), env, context)
    else return s!:comcall(x, env, context)
  end;

symbolic procedure s!:comspecform(x, env, context);
   error(0, list("special form", x));

% This establishes a default handler for each special form so that
% any that I forget to treat more directly will cause a tidy error
% if found in compiled code.  The conditional definition here is to
% allow me to re-load this file on top of itself during bootstrapping.
% The list here can be a reminder of ways that this compiler is
% incomplete.

if null get('and, 's!:compfn) then <<
    put('compiler!-let,          's!:compfn, function s!:comspecform);
    put('de,                     's!:compfn, function s!:comspecform);
    put('defun,                  's!:compfn, function s!:comspecform);
    put('eval!-when,             's!:compfn, function s!:comspecform);
    put('flet,                   's!:compfn, function s!:comspecform);
    put('labels,                 's!:compfn, function s!:comspecform);
    put('macrolet,               's!:compfn, function s!:comspecform);
!#if (not common!-lisp!-mode)
% In Common Lisp Mode I support there. In Standard Lisp mode they
% are not very meaningful so I do not, but I still reserve the names.
    put('multiple!-value!-call,  's!:compfn, function s!:comspecform);
    put('multiple!-value!-prog1, 's!:compfn, function s!:comspecform);
    put('prog!*,                 's!:compfn, function s!:comspecform);
    put('progv,                  's!:compfn, function s!:comspecform);
!#endif
    nil >>;

symbolic procedure s!:improve u;
  begin
    scalar w;
    if atom u then return u
    else if (w := get(car u, 's!:tidy_fn)) then
       return funcall(w, u)
    else if (w := get(car u, 's!:newname)) then
       return s!:improve (w . cdr u)
    else return u
  end;

symbolic procedure s!:imp_minus u;
  begin
    scalar a;
    a := s!:improve cadr u;
    return if numberp a then -a
      else if eqcar(a, 'minus) or eqcar(a, 'iminus) then cadr a
      else if eqcar(a, 'difference) then
         s!:improve list('difference, caddr a, cadr a)
      else if eqcar(a, 'idifference) then
         s!:improve list('idifference, caddr a, cadr a)
      else list(car u, a)
  end;

put('minus, 's!:tidy_fn, 's!:imp_minus);
put('iminus, 's!:tidy_fn, 's!:imp_minus);

!#if common!-lisp!-mode

symbolic procedure s!:imp_1!+ u;
  s!:improve ('add1 . cdr u);

put('!1!+, 's!:tidy_fn, 's!:imp_1!+);

symbolic procedure s!:imp_1!- u;
  s!:improve ('sub1 . cdr u);

put('!1!-, 's!:tidy_fn, 's!:imp_1!-);

!#endif

symbolic procedure s!:imp_times u;
  begin
    scalar a, b;
    if not (length u = 3) then
       return car u . for each v in cdr u collect s!:improve v;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if a = 1 then b
      else if b = 1 then a
      else if a = -1 then s!:imp_minus list('minus, b)
      else if b = -1 then s!:imp_minus list('minus, a)
      else list(car u, a, b)
  end;

put('times, 's!:tidy_fn, 's!:imp_times);

symbolic procedure s!:imp_itimes u;
  begin
    scalar a, b;
    if not (length u = 3) then
       return car u . for each v in cdr u collect s!:improve v;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if a = 1 then b
      else if b = 1 then a
      else if a = -1 then s!:imp_minus list('iminus, b)
      else if b = -1 then s!:imp_minus list('iminus, a)
      else list(car u, a, b)
  end;

put('itimes, 's!:tidy_fn, 's!:imp_itimes);

symbolic procedure s!:imp_difference u;
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if a = 0 then s!:imp_minus list('minus, b)
      else if b = 0 then a
      else list(car u, a, b)
  end;

put('difference, 's!:tidy_fn, 's!:imp_difference);

symbolic procedure s!:imp_idifference u;
  begin
    scalar a, b;
    a := s!:improve cadr u;
    b := s!:improve caddr u;
    return if a = 0 then s!:imp_minus list('iminus, b)
      else if b = 0 then a
      else list(car u, a, b)
  end;

put('idifference, 's!:tidy_fn, 's!:imp_idifference);

% s!:iseasy yields true if the given expression can be loaded without
% disturbing registers.

symbolic procedure s!:alwayseasy x;
   t;

put('quote, 's!:helpeasy, function s!:alwayseasy);
put('function, 's!:helpeasy, function s!:alwayseasy);

symbolic procedure s!:easyifarg x;
    null cdr x or (null cddr x and s!:iseasy cadr x);

put('ncons, 's!:helpeasy, function s!:easyifarg);

put('car, 's!:helpeasy, function s!:easyifarg);
put('cdr, 's!:helpeasy, function s!:easyifarg);
put('caar, 's!:helpeasy, function s!:easyifarg);
put('cadr, 's!:helpeasy, function s!:easyifarg);
put('cdar, 's!:helpeasy, function s!:easyifarg);
put('cddr, 's!:helpeasy, function s!:easyifarg);
put('caaar, 's!:helpeasy, function s!:easyifarg);
put('caadr, 's!:helpeasy, function s!:easyifarg);
put('cadar, 's!:helpeasy, function s!:easyifarg);
put('caddr, 's!:helpeasy, function s!:easyifarg);
put('cdaar, 's!:helpeasy, function s!:easyifarg);
put('cdadr, 's!:helpeasy, function s!:easyifarg);
put('cddar, 's!:helpeasy, function s!:easyifarg);
put('cdddr, 's!:helpeasy, function s!:easyifarg);
put('caaaar, 's!:helpeasy, function s!:easyifarg);
put('caaadr, 's!:helpeasy, function s!:easyifarg);
put('caadar, 's!:helpeasy, function s!:easyifarg);
put('caaddr, 's!:helpeasy, function s!:easyifarg);
put('cadaar, 's!:helpeasy, function s!:easyifarg);
put('cadadr, 's!:helpeasy, function s!:easyifarg);
put('caddar, 's!:helpeasy, function s!:easyifarg);
put('cadddr, 's!:helpeasy, function s!:easyifarg);
put('cdaaar, 's!:helpeasy, function s!:easyifarg);
put('cdaadr, 's!:helpeasy, function s!:easyifarg);
put('cdadar, 's!:helpeasy, function s!:easyifarg);
put('cdaddr, 's!:helpeasy, function s!:easyifarg);
put('cddaar, 's!:helpeasy, function s!:easyifarg);
put('cddadr, 's!:helpeasy, function s!:easyifarg);
put('cdddar, 's!:helpeasy, function s!:easyifarg);
put('cddddr, 's!:helpeasy, function s!:easyifarg);

%put('ncons, 's!:helpeasy, function s!:easyifarg);
%put('list, 's!:helpeasy, function s!:easyifarg);
%put('list!*, 's!:helpeasy, function s!:easyifarg);
%put('minus, 's!:helpeasy, function s!:easyifarg);
%put('minusp, 's!:helpeasy, function s!:easyifarg);

symbolic procedure s!:easygetv x;
  begin
    scalar a2;
    a2 := caddr x;
    if null !*carcheckflag and
       fixp a2 and a2 >= 0 and a2 < 256 then return s!:iseasy cadr x
    else return nil
  end;

put('getv, 's!:helpeasy, function s!:easygetv);
!#if common!-lisp!-mode
put('svref, 's!:heapeasy, function s!:easygetv);
!#endif

symbolic procedure s!:easyqgetv x;
  begin
    scalar a2;
    a2 := caddr x;
    if fixp a2 and a2 >= 0 and a2 < 256 then return s!:iseasy cadr x
    else return nil
  end;

put('qgetv, 's!:helpeasy, function s!:easyqgetv);
!#if common!-lisp!-mode
put('qsvref, 's!:heapeasy, function s!:easyqgetv);
!#endif

symbolic procedure s!:iseasy x;
  begin
    scalar h;
    if atom x then return t;
    if not atom car x then return nil;
    if h := get(car x, 's!:helpeasy) then return funcall(h, x)
    else return nil
  end;

symbolic procedure s!:instate_local_decs(v, d, w);
  begin
    scalar fg;
    if fluidp v then return w;
    for each z in d do
       if eqcar(z, 'special) and memq(v, cdr z) then fg := t;
    if fg then << 
        make!-special v;
        w := v . w >>;
    return w
  end;

symbolic procedure s!:residual_local_decs(d, w);
  begin
    for each z in d do
      if eqcar(z, 'special) then for each v in cdr z do
         if not fluidp v and not globalp v then <<
            make!-special v;
            w := v . w >>;
    return w
  end;

symbolic procedure s!:cancel_local_decs w;
   unfluid w;

symbolic procedure s!:find_local_decs(body, isprog);
  begin
    scalar w, local_decs;
% In my conversion from rlisp I wish to insert (DECLARE (SPECIAL ...))
% clauses. In the case of PROG all is well and I can insert the declaration
% at the start of the PROG block. For function definitions and free-standing
% uses of LAMBDA I cound do the same and rely on the "implied-progn" feature
% that I can support at the Lisp level. But there is a chance that other
% parts of REDUCE that analyse or manipulate code may still think in
% the old-fashioned style where you write (eg)
%    (de name (args) body)
% and body must be a single form. To cope with that I will, in that case,
% map onto
%    (de name (args) (progn (declare ...) original-body))
% where in normal Lisp terms that DECLARE is a bit hidden. But fairly
% simple code here can find it... If I have a form such as
% the above where there is exactly one item forming the body and it is
% a PROGN I uprate it to use an implied PROGN.
    if not isprog and body and null cdr body and eqcar(car body, 'progn) then
        body := cdar body;
    while body and (eqcar(car body, 'declare) or stringp car body) do <<
       if stringp car body then w := car body . w
       else local_decs := append(local_decs, cdar body);
       body := cdr body >>;
% I put back any strings since although MAYBE they are documentation also
% it could be that one was a result.
    while w do << body := car w . body; w := cdr w >>;
    return local_decs . body
  end;

symbolic procedure s!:comlambda(bvl, body, args, env, context);
% Handle embedded lambda expressions, which may well be serving as
% the construct that Common Lisp would write as (let ((x v)) ...)
% NOTE: I do not support &optional or &rest keywords with embedded
% lambda expressions. This is maybe just because I think that they would
% be a gross frivolity!  If I find an important piece of code that
% happens (say because of some macro-expansion) to use them I can
% process out the keywords here.
  begin
    scalar s, nbvl, fluids, fl1, w, local_decs;
    nbvl := s := cdr env;
    body := s!:find_local_decs(body, nil);
    local_decs := car body; body := cdr body;
    if atom body then body := nil
    else if atom cdr body then body := car body
    else body := 'progn . body;
    w := nil;
    for each v in bvl do w := s!:instate_local_decs(v, local_decs, w);
    for each v in bvl do <<
       if fluidp v or globalp v then begin
          scalar g;
          g := gensym();
          nbvl := g . nbvl;
          fl1 := v . fl1;
          fluids := (v . g) . fluids end
       else nbvl := v . nbvl;
% It would be even better to collect up NILs here and use s!:outstack with
% larger args (where possible), but at least this is a slight improvement!
       if car args = nil then s!:outstack 1
       else <<
          s!:comval(car args, env, 1);
          s!:outopcode0('PUSH, '(PUSH)) >>;
       rplacd(env, 0 . cdr env);
       args := cdr args >>;
    rplacd(env, nbvl);
    if fluids then <<
       fl1 := s!:vecof fl1;
       s!:outopcode1lit('FREEBIND, fl1, env);
       for each v in nil . fluids do rplacd(env, 0 . cdr env);
% The number in the environment map where a variable name would more
% normally be wanted marks a place where free variables are saved. It
% indicates how many stack locations are used by the free variable save block.
       rplacd(env, (2 + length fluids) . cdr env);
       for each v in fluids do
          s!:comval(list('setq, car v, cdr v), env, 2) >>;
    w := s!:residual_local_decs(local_decs, w);
% I use a context of 1 here (value needed) regardless of where I am. It avoids
% program context filtering down into embedded lambdas.
    s!:comval(body, env, 1);
    s!:cancel_local_decs w;
    if fluids then s!:outopcode0('FREERSTR, '(FREERSTR));
    s!:outlose length bvl;
    rplacd(env, s)
  end;

symbolic procedure s!:loadliteral(x, env);
  if member!*!*(list('quote, x), s!:a_reg_values) then nil
  else <<
    if x = nil then s!:outopcode0('VNIL, '(loadlit nil))
    else s!:outopcode1lit('LOADLIT, x, env);
    s!:a_reg_values := list list('quote, x) >>;

symbolic procedure s!:comquote(x, env, context);
  if context <= 1 then s!:loadliteral(cadr x, env);

put('quote, 's!:compfn, function s!:comquote);

fluid '(s!:current_exitlab s!:current_proglabels s!:local_macros);

!#if common!-lisp!-mode

symbolic procedure s!:comval_m(x, env, context, s!:local_macros);
    s!:comval(x, env, context);

symbolic procedure s!:comflet(x, env, context);
  begin
    scalar w, r, g, save;
    save := cdr env;
    for each d in cadr x do <<
       g := gensym();
       s!:comval(list('function, 'lambda . cdr d), env, context);
       s!:outopcode0('PUSH, '(PUSH));
       rplacd(env, g . cdr env);
       r := (car d . g) . r >>;
    s!:comval_m('progn . cddr x, env, context, append(r, s!:local_macros));
    s!:outlose length cadr x;
    rplacd(env, save)
  end;

put('flet, 's!:compfn, function s!:comflet);

symbolic procedure s!:comlabels(x, env, context);
  begin
    scalar w, w1, r, g;
    for each d in cadr x do <<
       g := gensym();
       w := list('setq, g, list('function, 'lambda . cdr d)) . w;
       w1 := list g . w1;
       r := (car d . g) . r >>;
    x := 'let . reverse w1 . append(w, cddr x);
    return s!:comval_m(x, env, context, append(r, s!:local_macros))
  end;

put('labels, 's!:compfn, function s!:comlabels);

symbolic procedure s!:commacrolet(x, env, context);
   s!:comval_m('progn . cddr x, env, context,
               append(cadr x, s!:local_macros));

put('macrolet, 's!:compfn, function s!:commacrolet);

symbolic procedure s!:local_macro fn;
  begin
    scalar w, y;
    w := list(nil, nil, nil, s!:local_macros) . s!:lexical_env;
    while w do <<
       y := atsoc(fn, cadddr car w);
       if y then w := nil else w := cdr w >>;
    return y
  end;

!#endif

symbolic procedure s!:comfunction(x, env, context);
  if context <= 1 then
  << x := cadr x;
     if eqcar(x, 'lambda) then begin
        scalar g, w, s!:used_lexicals;
        s!:has_closure := t;
% I base the name used on the current date, which probably makes
% it hard to have clashes.
        g := hashtagged!-name('lambda, cdr x);
% If I find an expression (FUNCTION (LAMBDA ...)) I will create a lexical
% closure.  In other cases FUNCTION behaves just like QUOTE.
        w := s!:compile1(g, cadr x, cddr x,
                         list(cdr env, s!:current_exitlab,
                              s!:current_proglabels, s!:local_macros) .
                           s!:lexical_env);
        if s!:used_lexicals then
            w := s!:compile1(g, gensym() . cadr x, cddr x,
                         list(cdr env, s!:current_exitlab,
                              s!:current_proglabels, s!:local_macros) .
                           s!:lexical_env);
        s!:other_defs := append(w, s!:other_defs);
        s!:loadliteral(g, env);
        w := length cdr env;
        if s!:used_lexicals then <<
% If the lambda expression did not use any non-local lexical references
% then it does not need a closure, so I can load its value slightly more
% efficiently and also permit tal=il-call optimisation in the function
% that loads it.
            s!:has_closure := t;
            if w > 4095 then error(0, "stack frame > 4095")
            else if w > 255 then
               s!:outopcode2('BIGSTACK, 128+truncate(w,256), logand(w, 255),
                             list('CLOSURE, w))
            else s!:outopcode1('CLOSURE, w, x) >> end
!#if common!-lisp!-mode
     else if context := s!:local_macro x then <<
        if atom cdr context then s!:comatom(cdr context, env, 1)
        else error(0, "(function <local macro>) is illegal") >>
!#endif
     else s!:loadliteral(x, env) >>;

put('function, 's!:compfn, function s!:comfunction);

symbolic procedure s!:should_be_fluid x;
   if not (fluidp x or globalp x) then <<
       if !*pwrds then <<  % The !*pwrds flag controls this verbosity too
          if posn() neq 0 then terpri();
          princ "+++ ";
          prin x;
!#if common!-lisp!-mode
          princ " treated as if locally SPECIAL";
!#else
          princ " declared fluid";
!#endif
          terpri() >>;
!#if (not common!-lisp!-mode)
       fluid list x;
!#endif
       nil >>;

symbolic procedure s!:find_lexical(x, lex, n);
  begin
    scalar p;
    if null lex then return nil;
    p := memq(x, caar lex);
    if p then <<
        if not memq(x, s!:used_lexicals) then
            s!:used_lexicals := x . s!:used_lexicals;
        return list(n, length p) >>
    else return s!:find_lexical(x, cdr lex, n+1)
  end;

global '(s!:loadlocs);

s!:loadlocs := s!:vecof '(LOADLOC0 LOADLOC1 LOADLOC2 LOADLOC3
                          LOADLOC4 LOADLOC5 LOADLOC6 LOADLOC7
                          LOADLOC8 LOADLOC9 LOADLOC10 LOADLOC11);

symbolic procedure s!:comatom(x, env, context);
  begin
    scalar n, w;
    if context > 1 then return nil
    else if null x or not symbolp x then return s!:loadliteral(x, env);
!#if common!-lisp!-mode
    if keywordp x then return s!:loadliteral(x, env);
!#endif
    n := 0;
    w := cdr env;
    while w and not eqcar(w, x) do << n := add1 n; w := cdr w >>;
    if w then <<
       w := 'loc . w;
       if member!*!*(w, s!:a_reg_values) then return nil
       else <<
          if n < 12 then s!:outopcode0(getv(s!:loadlocs, n),
                                       list('LOADLOC, x))
          else if n > 4095 then error(0, "stack frame > 4095")
          else if n > 255 then
             s!:outopcode2('BIGSTACK, truncate(n,256),
                           logand(n, 255), list('LOADLOC, x))
          else s!:outopcode1('LOADLOC, n, x);
          s!:a_reg_values := list w;
          return nil >> >>;
    if w := s!:find_lexical(x, s!:lexical_env, 0) then <<
        if member!*!*('lex . w, s!:a_reg_values) then return nil;
        s!:outlexref('LOADLEX, length cdr env, car w, cadr w, x);
        s!:a_reg_values := list('lex . w);
        return nil >>;
    s!:should_be_fluid x;
    if flagp(x, 'constant!?) then return s!:loadliteral(eval x, env);
    w := 'free . x;
    if member!*!*(w, s!:a_reg_values) then return nil;
    s!:outopcode1lit('LOADFREE, x, env);
    s!:a_reg_values := list w
  end;

flag('(t !$EOL!$ !$EOF!$), 'constant!?);

symbolic procedure s!:islocal(x, env);
% Returns a small integer if x is a local variable in the current environment.
% return 99999 otherwise. Yes I know that 99999 is a silly value to use.
  begin
    scalar n, w;
    if null x or not symbolp x or x eq t then return 99999;
    n := 0;
    w := cdr env;
    while w and not eqcar(w, x) do << n := add1 n; w := cdr w >>;
    if w then return n
    else return 99999
  end;

symbolic procedure s!:load2(a, b, env);
% s!:load2(a,b,env) calls s!:comval on a and then on b, so that
% a end up in the B register and b ends up in the A register(!).
% If processing b would corrupt the pre-loaded value of a it is
% necessary to issue PUSH and POP operations.
% If a final SWOP is needed then this returns T, otherwise NIL
 <<
    if s!:iseasy b then begin
       scalar wa, wb, w;
       wa := s!:islocal(a, env);
       wb := s!:islocal(b, env);
       if wa < 4 and wb < 4 then <<
          if wa = 0 and wb = 1 then w := 'LOC0LOC1
          else if wa = 1 and wb = 2 then w := 'LOC1LOC2   
          else if wa = 2 and wb = 3 then w := 'LOC2LOC3
          else if wa = 1 and wb = 0 then w := 'LOC1LOC0
          else if wa = 2 and wb = 1 then w := 'LOC2LOC1
          else if wa = 3 and wb = 2 then w := 'LOC3LOC2;
          if w then <<
             s!:outopcode0(w, list('LOCLOC, a, b));
             return nil >> >>;
       s!:comval(a, env, 1);
       s!:a_reg_values := nil;
       s!:comval(b, env, 1);
       return nil end
!#if common!-lisp!-mode
% For Common Lisp it seems that I *must* evaluate args strictly left-to-right.
% I can violate this rule if the item I move in evaluation order is something
% which has an utterly constant value.
    else if numberp a or 
            stringp a or 
            keywordp a or
            eqcar(a, 'quote) then <<
       s!:comval(b, env, 1);
       s!:a_reg_values := nil;
       s!:comval(a, env, 1);
       t >>
    else <<
       s!:comval(a, env, 1);
       s!:outopcode0('PUSH, '(PUSH));
       rplacd(env, 0 . cdr env);
       s!:a_reg_values := nil;
       s!:comval(b, env, 1);
       s!:outopcode0('POP, '(POP));
       rplacd(env, cddr env);
       t >>
!#else
% Here, in Standard Lisp mode, I will compile the arguments left to right
% if !*ord is set. Otherwise in the cases that get down here I can save
% some generated code (and hence both time and space) by working right
% to left.
    else if !*ord then <<
       s!:comval(a, env, 1);
       s!:outopcode0('PUSH, '(PUSH));
       rplacd(env, 0 . cdr env);
       s!:a_reg_values := nil;
       s!:comval(b, env, 1);
       s!:outopcode0('POP, '(POP));
       rplacd(env, cddr env);
       t >>
    else if s!:iseasy a then <<
       s!:comval(b, env, 1);
       s!:a_reg_values := nil;
       s!:comval(a, env, 1);
       t >>
    else <<
       s!:comval(b, env, 1);         % b is a complicated expression here
       s!:outopcode0('PUSH, '(PUSH));
       rplacd(env, 0 . cdr env);
       s!:a_reg_values := nil;
       s!:comval(a, env, 1);
       s!:outopcode0('POP, '(POP));
       rplacd(env, cddr env);        % this case saves a SWAP afterwards
       nil >>
!#endif
 >>;

global '(s!:carlocs s!:cdrlocs s!:caarlocs);

s!:carlocs  := s!:vecof '(CARLOC0  CARLOC1  CARLOC2  CARLOC3
                          CARLOC4  CARLOC5  CARLOC6  CARLOC7
                          CARLOC8  CARLOC9  CARLOC10 CARLOC11);
s!:cdrlocs  := s!:vecof '(CDRLOC0  CDRLOC1  CDRLOC2  CDRLOC3
                          CDRLOC4  CDRLOC5);
s!:caarlocs := s!:vecof '(CAARLOC0 CAARLOC1 CAARLOC2 CAARLOC3);

flag('(plus2 times2 eq equal), 's!:symmetric);

flag('(car cdr caar cadr cdar cddr
       ncons add1 sub1 numberp length), 's!:onearg);

flag('(cons xcons list2 get flagp plus2 difference times2
       greaterp lessp apply1 eq equal getv qgetv eqcar), 's!:twoarg);

flag('(apply2 list2!* list3 acons), 's!:threearg);

% The case of APPLY3 is handled by in-line code rather than a general flag,
% but I leave the flag statement here as a reminder that it is present as a
% special case. There is a byte-code allocated for APPLY4, but at present
% the compiler never generates it and the bytecode interpreter does not
% implement it. It is reserved in case that sort of use of APPLY/FUNCALL
% proves sufficiently important.
% flag('(apply3), 's!:fourarg);
% flag('(apply4), 's!:fivearg);

symbolic procedure s!:comcall(x, env, context);
% generate a procedure call - different CALL instructions
% and formats are used for different numbers of arguments.
  begin
    scalar fn, args, nargs, op, s, w1, w2, w3, sw;
    fn := car x;
    if not symbolp fn then error(0, "non-symbol used in function position");
    args := for each v in cdr x collect s!:improve v;
    nargs := length args;
% Standard Lisp only allows 15 arguments.  CSL supports 20 just
% to be on the safe side, but it tells the programmer when the lower
% limit is violated. Common Lisp can cope with rather more args, but I
% will still let people know if I think they have gone over the top.
    if nargs > 15 and !*pwrds then <<
        if posn() neq 0 then terpri();
        princ "+++ ";
        prin fn;
        princ " called with ";
        prin nargs;
        princ " from function ";
        prin s!:current_function;
        terpri() >>;
    s := cdr env;
    if nargs = 0 then
       if (w2 := get(fn, 's!:builtin0)) then s!:outopcode1('BUILTIN0, w2, fn)
       else s!:outopcode1lit('CALL0, fn, env)
    else if nargs = 1 then <<
       if fn = 'car and
          (w2 := s!:islocal(car args, env)) < 12 then
          s!:outopcode0(getv(s!:carlocs, w2), list('carloc, car args))
       else if fn = 'cdr and
          (w2 := s!:islocal(car args, env)) < 6 then
          s!:outopcode0(getv(s!:cdrlocs, w2), list('cdrloc, car args))
       else if fn = 'caar and
          (w2 := s!:islocal(car args, env)) < 4 then
          s!:outopcode0(getv(s!:caarlocs, w2), list('caarloc, car args))
       else <<
          s!:comval(car args, env, 1);
          if flagp(fn, 's!:onearg) then s!:outopcode0(fn, list fn)
          else if (w2 := get(fn, 's!:builtin1)) then
            s!:outopcode1('BUILTIN1, w2, fn)
          else s!:outopcode1lit('CALL1, fn, env) >> >>
    else if nargs = 2 then <<
        sw := s!:load2(car args, cadr args, env);
        if flagp(fn, 's!:symmetric) then sw := nil;
        if flagp(fn, 's!:twoarg) then <<
           if sw then s!:outopcode0('SWOP, '(SWOP));
           s!:outopcode0(fn, list fn) >>
        else <<
          w3 := get(fn, 's!:builtin2);
          if sw then <<
              if w3 then s!:outopcode1('BUILTIN2R, w3, fn)
              else s!:outopcode1lit('CALL2R, fn, env) >>
          else if w3 then s!:outopcode1('BUILTIN2, w3, fn)
          else s!:outopcode1lit('CALL2, fn, env) >> >>
    else if nargs = 3 then <<
        if car args = nil then s!:outstack 1
        else <<
           s!:comval(car args, env, 1);
           s!:outopcode0('PUSH, '(PUSHA3)) >>;
        rplacd(env, 0 . cdr env);
        s!:a_reg_values := nil;
        if s!:load2(cadr args, caddr args, env) then
           s!:outopcode0('SWOP, '(SWOP));
        if flagp(fn, 's!:threearg) then
           s!:outopcode0(if fn = 'list2!* then 'list2star else fn, list fn)
        else if w2 := get(fn, 's!:builtin3) then
           s!:outopcode1('BUILTIN3, w2, fn)
        else s!:outopcode1lit('CALL3, fn, env);
        rplacd(env, cddr env) >>
    else begin
% Functions with 4 or more arguments are called by pushing all their
% arguments onto the stack.  I expect that this will not be a common case.
      scalar largs;
      largs := reverse args;
      for each a in reverse cddr largs do <<
        if null a then s!:outstack 1
        else <<
           s!:comval(a, env, 1);
           if nargs = 4 then s!:outopcode0('PUSH, '(PUSHA4))
           else s!:outopcode0('PUSH, '(PUSHARG)) >>;
        rplacd(env, 0 . cdr env);
        s!:a_reg_values := nil >>;
      if s!:load2(cadr largs, car largs, env) then
         s!:outopcode0('SWOP, '(SWOP));
      if fn = 'apply3 and nargs = 4 then s!:outopcode0('APPLY3, '(APPLY3))
%     else if fn = 'apply4 and nargs = 5 then   % Not yet implemented.
%        s!:outopcode0('APPLY4, '(APPLY4));
      else if nargs > 255 then error(0, "Over 255 args in a function call")
      else s!:outopcode2lit('CALLN, fn, nargs, list(nargs, fn), env);
      rplacd(env, s) end
  end;

% caaar to cddddr get expanded into compositions of
% car, cdr, caar, cadr, cdar and cddr - which in turn get
% compiled into direct bytecodes

symbolic procedure s!:ad_name l;
  if car l = 'a then
    if cadr l = 'a then 'caar else 'cadr
  else if cadr l = 'a then 'cdar else 'cddr;

symbolic procedure s!:comcarcdr3(x, env, context);
  begin
    scalar name, outer, c1, c2;
    name := cdr explode2 car x;
% Turns (eg) (caddr x) into (cadr (cdr x))
    x := list(s!:ad_name name,
            list(if caddr name = 'a then 'car else 'cdr, cadr x));
    return s!:comval(x, env, context)
  end;

put('caaar, 's!:compfn, function s!:comcarcdr3);
put('caadr, 's!:compfn, function s!:comcarcdr3);
put('cadar, 's!:compfn, function s!:comcarcdr3);
put('caddr, 's!:compfn, function s!:comcarcdr3);
put('cdaar, 's!:compfn, function s!:comcarcdr3);
put('cdadr, 's!:compfn, function s!:comcarcdr3);
put('cddar, 's!:compfn, function s!:comcarcdr3);
put('cdddr, 's!:compfn, function s!:comcarcdr3);

symbolic procedure s!:comcarcdr4(x, env, context);
  begin
    scalar name, outer, c1, c2;
    name := cdr explode2 car x;
    x := list(s!:ad_name name, list(s!:ad_name cddr name, cadr x));
    return s!:comval(x, env, context)
  end;

put('caaaar, 's!:compfn, function s!:comcarcdr4);
put('caaadr, 's!:compfn, function s!:comcarcdr4);
put('caadar, 's!:compfn, function s!:comcarcdr4);
put('caaddr, 's!:compfn, function s!:comcarcdr4);
put('cadaar, 's!:compfn, function s!:comcarcdr4);
put('cadadr, 's!:compfn, function s!:comcarcdr4);
put('caddar, 's!:compfn, function s!:comcarcdr4);
put('cadddr, 's!:compfn, function s!:comcarcdr4);
put('cdaaar, 's!:compfn, function s!:comcarcdr4);
put('cdaadr, 's!:compfn, function s!:comcarcdr4);
put('cdadar, 's!:compfn, function s!:comcarcdr4);
put('cdaddr, 's!:compfn, function s!:comcarcdr4);
put('cddaar, 's!:compfn, function s!:comcarcdr4);
put('cddadr, 's!:compfn, function s!:comcarcdr4);
put('cdddar, 's!:compfn, function s!:comcarcdr4);
put('cddddr, 's!:compfn, function s!:comcarcdr4);

% The next chunk is commented out - the "carcheck" flag was at one stage
% there so that *carcheckflag = nil would cause compilation to give
% unchecked car/cdr access, which ought to be faster.  However with the
% bytecode interpreter model I dedicate plenty of opcodes to regular car and
% cdr combinations, while unchecked car is supported in a rather simpler
% way, so the small savings in missing out the check are outweighed by the
% extra overheads of invoking the unchecked operation!

% I leave the flag there and cause it to map vector access (getv in Standard
% lisp and svref in Common Lisp) into a cheaper version that omits checking.
% This is more profitable becase (a) I do less in the bytecode model to make
% getv special, and (b) the overheads on array bound checking are greater
% than those for car/cdr chaining.

% symbolic procedure s!:comcar(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcar . cdr x, env, context);
% 
% put('car, 's!:compfn, function s!:comcar);
% 
% symbolic procedure s!:comcdr(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcdr . cdr x, env, context);
% 
% put('cdr, 's!:compfn, function s!:comcdr);
% 
% symbolic procedure s!:comcaar(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcaar . cdr x, env, context);
% 
% put('caar, 's!:compfn, function s!:comcaar);
% 
% symbolic procedure s!:comcadr(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcadr . cdr x, env, context);
% 
% put('cadr, 's!:compfn, function s!:comcadr);
% 
% symbolic procedure s!:comcdar(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcdar . cdr x, env, context);
% 
% put('cdar, 's!:compfn, function s!:comcdar);
% 
% symbolic procedure s!:comcddr(x, env, context);
%   if !*carcheckflag then s!:comcall(x, env, context)
%   else s!:comval('qcddr . cdr x, env, context);
% 
% put('cddr, 's!:compfn, function s!:comcddr);

symbolic procedure s!:comgetv(x, env, context);
  if !*carcheckflag then s!:comcall(x, env, context)
  else s!:comval('qgetv . cdr x, env, context);

put('getv, 's!:compfn, function s!:comgetv);

symbolic procedure s!:comqgetv(x, env, context);
  if fixp caddr x and caddr x >= 0 and caddr x < 256 then <<
    s!:comval(cadr x, env, 1);
    s!:outopcode1('QGETVN, caddr x, caddr x) >>
  else s!:comcall(x, env, context);

put('qgetv, 's!:compfn, function s!:comqgetv);

symbolic procedure s!:comget(x, env, context);
  begin
    scalar a, b, c, w;
    a := cadr x;
    b := caddr x;
    c := cdddr x;
    if eqcar(b, 'quote) then <<
       b := cadr b;
       w := symbol!-make!-fastget(b, nil);
       if c then <<
          if w then <<
             if s!:load2(a, b, env) then
                s!:outopcode0('SWOP, '(SWOP));
             s!:outopcode1('FASTGET, logor(w, 64), b) >>
          else s!:comcall(x, env, context) >>
       else <<
          s!:comval(a, env, 1);
          if w then s!:outopcode1('FASTGET, w, b)
          else s!:outopcode1lit('LITGET, b, env) >> >>
    else s!:comcall(x, env, context)
  end;

put('get, 's!:compfn, function s!:comget);

symbolic procedure s!:comflagp(x, env, context);
  begin
    scalar a, b;
    a := cadr x;
    b := caddr x;
    if eqcar(b, 'quote) then <<
       b := cadr b;
       s!:comval(a, env, 1);
       a := symbol!-make!-fastget(b, nil);
       if a then s!:outopcode1('FASTGET, logor(a, 128), b)
       else s!:comcall(x, env, context) >>
    else s!:comcall(x, env, context)
  end;

put('flagp, 's!:compfn, function s!:comflagp);

% plus and times (and later on, I guess, logand, logor and a few more)
% get macroexpanded into calls to two-argument versions of the same
% operators.

symbolic procedure s!:complus(x, env, context);
   s!:comval(expand(cdr x, 'plus2), env, context);

put('plus, 's!:compfn, function s!:complus);

!#if common!-lisp!-mode
put('!+, 's!:compfn, function s!:complus);
!#endif

symbolic procedure s!:comtimes(x, env, context);
   s!:comval(expand(cdr x, 'times2), env, context);

put('times, 's!:compfn, function s!:comtimes);

!#if common!-lisp!-mode
put('!*, 's!:compfn, function s!:comtimes);
!#endif


symbolic procedure s!:comiplus(x, env, context);
   s!:comval(expand(cdr x, 'iplus2), env, context);

put('iplus, 's!:compfn, function s!:comiplus);

symbolic procedure s!:comitimes(x, env, context);
   s!:comval(expand(cdr x, 'itimes2), env, context);

put('itimes, 's!:compfn, function s!:comitimes);

symbolic procedure s!:complus2(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    return if numberp a and numberp b then s!:comval(a+b, env, context)
       else if a = 0 then s!:comval(b, env, context)
       else if a = 1 then s!:comval(list('add1, b), env, context)
       else if b = 0 then s!:comval(a, env, context)
       else if b = 1 then s!:comval(list('add1, a), env, context)
       else if b = -1 then s!:comval(list('sub1, a), env, context)
       else s!:comcall(x, env, context)
  end;

put('plus2, 's!:compfn, function s!:complus2);

symbolic procedure s!:comdifference(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    return if numberp a and numberp b then s!:comval(a-b, env, context)
       else if a = 0 then s!:comval(list('minus, b), env, context)
       else if b = 0 then s!:comval(a, env, context)
       else if b = 1 then s!:comval(list('sub1, a), env, context)
       else if b = -1 then s!:comval(list('add1, a), env, context)
       else s!:comcall(x, env, context)
  end;

put('difference, 's!:compfn, function s!:comdifference);

symbolic procedure s!:comiplus2(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    return if numberp a and numberp b then s!:comval(a+b, env, context)
       else if a = 1 then s!:comval(list('iadd1, b), env, context)
       else if b = 1 then s!:comval(list('iadd1, a), env, context)
       else if b = -1 then s!:comval(list('isub1, a), env, context)
       else s!:comcall(x, env, context)
  end;

put('iplus2, 's!:compfn, function s!:comiplus2);

symbolic procedure s!:comidifference(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    return if numberp a and numberp b then s!:comval(a-b, env, context)
       else if b = 1 then s!:comval(list('isub1, a), env, context)
       else if b = -1 then s!:comval(list('iadd1, a), env, context)
       else s!:comcall(x, env, context)
  end;

put('idifference, 's!:compfn, function s!:comidifference);

symbolic procedure s!:comtimes2(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    return if numberp a and numberp b then s!:comval(a*b, env, context)
       else if a = 1 then s!:comval(b, env, context)
       else if a = -1 then s!:comval(list('minus, b), env, context)
       else if b = 1 then s!:comval(a, env, context)
       else if b = -1 then s!:comval(list('minus, a), env, context)
       else s!:comcall(x, env, context)
  end;

put('times2, 's!:compfn, function s!:comtimes2);
put('itimes2, 's!:compfn, function s!:comtimes2);

symbolic procedure s!:comminus(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    return if numberp a then s!:comval(-a, env, context)
       else if eqcar(a, 'minus) then s!:comval(cadr a, env, context)
       else s!:comcall(x, env, context)
  end;

put('minus, 's!:compfn, function s!:comminus);

symbolic procedure s!:comminusp(x, env, context);
  begin
    scalar a;
    a := s!:improve cadr x;
    if eqcar(a, 'difference) then return
       s!:comval('lessp . cdr a, env, context)
    else return s!:comcall(x, env, context)
  end;

put('minusp, 's!:compfn, function s!:comminusp);

symbolic procedure s!:comlessp(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    if b = 0 then return
       s!:comval(list('minusp, a), env, context)
    else return s!:comcall(x, env, context)
  end;

put('lessp, 's!:compfn, function s!:comlessp);

symbolic procedure s!:comiminusp(x, env, context);
  begin
    scalar a;
    a := s!:improve cadr x;
    if eqcar(a, 'difference) then return
       s!:comval('ilessp . cdr a, env, context)
    else return s!:comcall(x, env, context)
  end;

put('iminusp, 's!:compfn, function s!:comiminusp);

symbolic procedure s!:comilessp(x, env, context);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    if b = 0 then return
       s!:comval(list('iminusp, a), env, context)
    else return s!:comcall(x, env, context)
  end;

put('ilessp, 's!:compfn, function s!:comilessp);

% s!:comprogn is used not only when I see an explicit progn in the
% code, but to handle the implicit ones in cond and after lambda.
% it switches evaluation mode to a void context for all but the
% last expression

symbolic procedure s!:comprogn(x, env, context);
 << x := cdr x;
    if null x then s!:comval(nil, env, context)
    else begin
      scalar a;
      a := car x;
      while x := cdr x do <<
         s!:comval(a, env, if context >= 4 then context else 2);
         a := car x >>;
      s!:comval(a, env, context)
    end
 >>;

put('progn, 's!:compfn, function s!:comprogn);

symbolic procedure s!:comprog1(x, env, context);
  begin
    x := cdr x;
    if null x then return s!:comval(nil, env, context);
    s!:comval(car x, env, context);
    if null (x := cdr x) then return nil;
    s!:outopcode0('PUSH, '(PUSH));
    rplacd(env, 0 . cdr env);
    for each a in x do
        s!:comval(a, env, if context >= 4 then context else 2);
    s!:outopcode0('POP, '(POP));
    rplacd(env, cddr env)
  end;

put('prog1, 's!:compfn, function s!:comprog1);

symbolic procedure s!:comprog2(x, env, context);
  begin
    scalar a;
    x := cdr x;
    if null x then return s!:comval(nil, env, context);
    a := car x;
    s!:comval(a, env, if context >= 4 then context else 2);
    s!:comprog1(x, env, context)
  end;

put('prog2, 's!:compfn, function s!:comprog2);

!#if common!-lisp!-mode

% REDUCE seems to introduce a function called IDENTITY that is not quite this
% one. Shame! hence only do this in Common mode.

symbolic procedure s!:comidentity(x, env, context);
   s!:comval(cadr x, env, context);

put('identity, 's!:compfn, function s!:comidentity);

!#endif

symbolic procedure s!:outstack n;
  begin
    scalar w, a;
    w := s!:current_block;
    while w and not atom car w do w := cdr w;
    if eqcar(w, 'PUSHNIL) then a := 1
    else if eqcar(w, 'PUSHNIL2) then a := 2
    else if eqcar(w, 'PUSHNIL3) then a := 3
% If I has a "PUSHNILS 255" already issued it would do no good at all
% to pick it off here and attempt to consolidate it with a further PUSH.
% Indeed that would probably lead to disaster.
    else if w and numberp (a := car w) and not (a = 255) and
            eqcar(cdr w, 'PUSHNILS) then <<
       w := cdr w;
       s!:current_size := s!:current_size - 1 >>
    else a := nil;
    if a then <<
       s!:current_block := cdr w;
       s!:current_size := s!:current_size - 1;
       n := n + a >>;
    if n = 1 then s!:outopcode0('PUSHNIL, '(PUSHNIL))
    else if n = 2 then s!:outopcode0('PUSHNIL2, '(PUSHNIL2))
    else if n = 3 then s!:outopcode0('PUSHNIL3, '(PUSHNIL3))
    else if n > 255 then <<
       s!:outopcode1('PUSHNILS, 255, 255);
       s!:outstack(n-255) >>
    else if n > 3 then s!:outopcode1('PUSHNILS, n, n)
  end;

symbolic procedure s!:outlose n;
  begin
    scalar w, a;
    w := s!:current_block;
    while w and not atom car w do w := cdr w;
    if eqcar(w, 'LOSE) then a := 1
    else if eqcar(w, 'LOSE2) then a := 2
    else if eqcar(w, 'LOSE3) then a := 3
    else if w and numberp (a := car w) and not (a = 255) and
            eqcar(cdr w, 'LOSES) then <<
       w := cdr w;
       s!:current_size := s!:current_size - 1 >>
    else a := nil;
    if a then <<
       s!:current_block := cdr w;
       s!:current_size := s!:current_size - 1;
       n := n + a >>;
    if n = 1 then s!:outopcode0('LOSE, '(LOSE))
    else if n = 2 then s!:outopcode0('LOSE2, '(LOSE2))
    else if n = 3 then s!:outopcode0('LOSE3, '(LOSE3))
    else if n > 255 then <<
       s!:outopcode1('LOSES, 255, 255);
       s!:outlose(n-255) >>
    else if n > 3 then s!:outopcode1('LOSES, n, n)
  end;

!#if (not common!-lisp!-mode)

% s!:comprog displays how much fun prog blocks are, in that it has to
% prepare support for go statements and returns, and it needs to
% handle fluid bindings.  The version here does not handle initialising forms
% (as required by Common Lisp) but in that case a macro that turns
% prog into a combination of BLOCK, LET and TAGBODY is used, so there is
% no serious loss.  Similarly PROG* is handled by macroexpansion rather
% than a variant on this direct support.

symbolic procedure s!:comprog(x, env, context);
  begin
    scalar labs, s, bvl, fluids, n, body, local_decs, w;
    body := s!:find_local_decs(cddr x, t);
    local_decs := car body; body := cdr body;
    n := 0;
    for each v in cadr x do w := s!:instate_local_decs(v, local_decs, w);
    for each v in cadr x do <<
       if globalp v then <<
          if !*pwrds then <<
             if posn() neq 0 then terpri();
             princ "+++++ global ";
             prin v;
             princ " converted to fluid";
             terpri() >>;
 % convert from global to fluid so that I can proceed
          unglobal list v;
          fluid list v >>;
       if fluidp v then fluids := v . fluids
       else << n := n + 1; bvl := v . bvl >> >>;
% save the environment that existed outside the prog so I can restore it later
    s := cdr env;
    s!:current_exitlab := (nil . (gensym() . s)) . s!:current_exitlab;
    s!:outstack n;
    rplacd(env, append(bvl, cdr env));
% bind the fluids
    if fluids then begin
       scalar fl1;
       fl1 := s!:vecof fluids;
       s!:outopcode1lit('FREEBIND, fl1, env);
       for each v in nil . fluids do rplacd(env, 0 . cdr env);
       rplacd(env, (2 + length fluids) . cdr env);
       if context = 0 then context := 1 end;
% use gensyms as internal names for the labels in this block
    for each a in body do
       if atom a then <<
          if atsoc(a, labs) then <<
             if not null a then <<
% I do not generate a message if NIL appears several times as a label,
% since in some generated PROG blocks people may have stuck in NIL thinking
% of it as a null expression rather than as a label.
                if posn() neq 0 then terpri();
                princ "+++++ label "; prin a;
                princ " multiply defined"; terpri() >> >>
          else labs := (a . ((gensym() . cdr env) . nil)) . labs >>; 
    s!:current_proglabels := labs . s!:current_proglabels;
    w := s!:residual_local_decs(local_decs, w);
% handle the body of the prog
    for each a in body do
       if not atom a then s!:comval(a, env, context+4)
       else begin
          scalar d;
          d := atsoc(a, labs);
          if null cddr d then <<
             rplacd(cdr d, t);
             s!:set_label caadr d >> end;
    s!:cancel_local_decs w;
% if I drop off the end of a prog block I must return nil, so
% load it up here
    s!:comval(nil, env, context);
    if fluids then s!:outopcode0('FREERSTR, '(FREERSTR));
    s!:outlose n;
    rplacd(env, s);
    s!:set_label cadar s!:current_exitlab;
    s!:current_exitlab := cdr s!:current_exitlab;
    s!:current_proglabels := cdr s!:current_proglabels
  end;

put('prog, 's!:compfn, function s!:comprog);

!#endif

% s!:comtagbody is put here next to s!:comprog since it is really a subset.

symbolic procedure s!:comtagbody(x, env, context);
  begin
    scalar labs;
% use gensyms as internal names for the labels in this block
    for each a in cdr x do
       if atom a then <<
          if atsoc(a, labs) then <<
             if not null a then <<
% I do not generate a message if NIL appears several times as a label,
% since in some generated PROG blocks people may have stuck in NIL thinking
% of it as a null expression rather than as a label.
                if posn() neq 0 then terpri();
                princ "+++++ label "; prin a;
                princ " multiply defined"; terpri() >> >>
          else labs := (a . ((gensym() . cdr env) . nil)) . labs >>;
    s!:current_proglabels := labs . s!:current_proglabels;
    for each a in cdr x do
       if not atom a then s!:comval(a, env, context+4)
       else begin
          scalar d;
          d := atsoc(a, labs);
          if null cddr d then <<
             rplacd(cdr d, t);
             s!:set_label caadr d >> end;
% if I drop off the end of a prog block I must return nil, so
% load it up here
    s!:comval(nil, env, context);
    s!:current_proglabels := cdr s!:current_proglabels
  end;

put('tagbody, 's!:compfn, function s!:comtagbody);

!#if common!-lisp!-mode

symbolic procedure s!:comprogv(x, env, context);
  begin
    x := cdr x;
    if s!:load2(car x, cadr x, env) then s!:outopcode0('SWOP, '(SWOP));
    s!:outopcode0('PVBIND, '(PVBIND));
    rplacd(env, '(pvbind) . 0 . cdr env);
    s!:comval('progn . cddr x, env, 1);
    s!:outopcode0('PVRESTORE, '(PVRESTORE));
    rplacd(env, cdddr env)
  end;

put('progv, 's!:compfn, function s!:comprogv);

symbolic procedure s!:comprog!*(x, env, context);
  begin
    scalar local_decs;
    local_decs := s!:find_local_decs(cddr x, t);
% Macroexpand as per CLTL trying to migrate declarations to the right place
    x := list('block, nil,
           list('let!*, cadr x,
              'declare . car local_decs,
              'tagbody . cdr local_decs));
    return s!:comval(x, env, context)
  end;

put('prog!*, 's!:compfn, function s!:comprog!*);

!#endif

% s!:comblock is just for RETURN to work with.

symbolic procedure s!:comblock(x, env, context);
  begin
    s!:current_exitlab := (cadr x . (gensym() . cdr env)) . s!:current_exitlab;
    s!:comval('progn . cddr x, env, context);
    s!:set_label cadar s!:current_exitlab;
    s!:current_exitlab := cdr s!:current_exitlab
  end;

!#if common!-lisp!-mode
put('block, 's!:compfn, function s!:comblock);
!#else
put('!~block, 's!:compfn, function s!:comblock);
!#endif

symbolic procedure s!:comcatch(x, env, context);
  begin
    scalar g;
    g := gensym();
    s!:comval(cadr x, env, 1);      % The catch tag
    s!:outjump('CATCH, g);          % Jumps to label if a THROW happens
    rplacd(env, '(catch) . 0 . 0 . cdr env);
    s!:comval('progn . cddr x, env, context);
    s!:outopcode0('UNCATCH, '(UNCATCH));
    rplacd(env, cddddr env);
    s!:set_label g
  end;

put('catch, 's!:compfn, 's!:comcatch);

symbolic procedure s!:comthrow(x, env, context);
  begin
    s!:comval(cadr x, env, 1);          % The tag
    s!:outopcode0('PUSH, '(PUSH));
    rplacd(env, 0 . cdr env);
    s!:comval(caddr x, env, 1);         % value to be returned
    s!:outopcode0('THROW, '(THROW));    % tag is on the stack
    rplacd(env, cddr env)
  end;

put('throw, 's!:compfn, 's!:comthrow);

symbolic procedure s!:comunwind!-protect(x, env, context);
  begin
    scalar g;
    g := gensym();
% UNWIND-PROTECT shares an opcode with CATCH by using an otherwise
% invalid value as a tag.  The function LOAD-SPID is not available
% in interpreted code but in compiled code it just loads such a value.
    s!:comval('(load!-spid), env, 1);  % The unwind!-protect tag
    s!:outjump('CATCH, g);             % Jumps to label if ANY unwind happens
    rplacd(env, list('unwind!-protect, cddr x) . 0 . 0 . cdr env);
    s!:comval(cadr x, env, context);
% PROTECT may use the top three stack locations, and must use them to
% store the current set of values and exit status.  It is implicitly done
% by the forced jump that is taken on a failure...
    s!:outopcode0('PROTECT, '(PROTECT));
    s!:set_label g;
    rplaca(cdr env, 0);
% A lexical exit here will just pop the stack, discarding the saved
% information that PROTECT had left behind.
    s!:comval('progn . cddr x, env, context);
    s!:outopcode0('UNPROTECT, '(UNPROTECT));
    rplacd(env, cddddr env)
  end;

put('unwind!-protect, 's!:compfn, 's!:comunwind!-protect);

symbolic procedure s!:comdeclare(x, env, context);
% I print a message if I find DECLARE where I am compiling things.
% I am supposed to have picked off all valid uses of DECLARE
% elsewhere, so this is probably an error - but I will make it just
% a gentle warning message for now.
  begin
    if !*pwrds then <<
       princ "+++ ";
       prin x;
       princ " ignored";
       terpri() >>
  end;

put('declare, 's!:compfn, function s!:comdeclare);

symbolic procedure s!:expand_let(vl, b);
%  if null vl then b
%  else if null cdr vl then s!:expand_let!*(vl, b)
%  else
  begin scalar vars, vals;
    for each v in vl do
      if atom v then << vars := v . vars; vals := nil . vals >>
      else if atom cdr v then << vars := car v . vars; vals := nil . vals >>
      else << vars := car v . vars; vals := cadr v . vals >>;
    return list(('lambda . vars . b) . vals)
  end;

symbolic procedure s!:comlet(x, env, context);
   s!:comval('progn . s!:expand_let(cadr x, cddr x), env, context);

!#if common!-lisp!-mode
put('let, 's!:compfn, function s!:comlet);
!#else
put('!~let, 's!:compfn, function s!:comlet);
!#endif

symbolic procedure s!:expand_let!*(vl, local_decs, b);
% This has loads of fun because although it basically wants to expand
%        (LET* ((v1 e1) (v2 e2)) b1 b1 b2)
% into   ((LAMBDA (v1)               |              ) e1)
%                                    v
%                      ((LAMBDA (v2) b1 b2 b3) e2)
% it also needs to migrate special declarations to the proper levels.
% I also want the degenerate case (LET* () (DECLARE ...) ...) to arrange to
% spot and process the DECLARE, so I expand it into a vacuuous LAMBDA.
  begin
    scalar r, var, val;
    r := ('declare . local_decs) . b;
    for each x in reverse vl do <<
       val := nil;
       if atom x then var := x
       else if atom cdr x then var := car x
       else << var := car x; val := cadr x >>;
       for each z in local_decs do
          if eqcar(z, 'special) then
             if memq(var, cdr z) then
                r := list('declare, list('special, var)) . r;
       r := list list('lambda . list var . r, val) >>;
    if eqcar(car r, 'declare) then r := list('lambda . nil . r)
    else r := 'progn . r;
    return r
  end;

symbolic procedure s!:comlet!*(x, env, context);
  begin
    scalar b;
    b := s!:find_local_decs(cddr x, nil);
    return s!:comval(s!:expand_let!*(cadr x, car b, cdr b),
                     env, context)
  end;

put('let!*, 's!:compfn, function s!:comlet!*);

symbolic procedure s!:restore_stack(e1, e2);
% This is used when a GO (or a RETURN-FROM) is being compiled to restore
% the stack to a proper level for the destination of the branch.
  begin
    scalar n;
    n := 0;
    while not (e1 = e2) do <<
       if null e1 then error(0, "bad block nesting with GO or RETURN-FROM");
       if numberp car e1 and car e1 > 2 then <<
          if not zerop n then s!:outlose n;
          n := car e1;
          s!:outopcode0('FREERSTR, '(FREERSTR));
          for i := 1:n do e1 := cdr e1;
          n := 0 >>
       else if car e1 = '(catch) then <<
          if not zerop n then s!:outlose n;
          s!:outopcode0('UNCATCH, '(UNCATCH));
          e1 := cdddr e1;
          n := 0 >>
       else if eqcar(car e1, 'unwind!-protect) then <<
          if not zerop n then s!:outlose n;
          s!:outopcode0('PROTECT, '(PROTECT));
          s!:comval('progn . cadar e1, e1, 2);
          s!:outopcode0('UNPROTECT, '(UNPROTECT));
          e1 := cdddr e1;
          n := 0 >>
!#if common!-lisp!-mode
       else if car e1 = '(pvbind) then <<
          if not zerop n then s!:outlose n;
          s!:outopcode0('PVRESTORE, '(PVRESTORE));
          e1 := cddr e1;
          n := 0 >>
!#endif
       else <<
          e1 := cdr e1;
          n := n + 1 >> >>;
     if not zerop n then s!:outlose n
  end;

symbolic procedure s!:comgo(x, env, context);
% Even in Common Lisp Mode I do not support (yet) GO statements that
% escape from one LAMBDA expression into an enclosing one.
  begin
    scalar pl, d;
!#if (not common!-lisp!-mode)
    if context < 4 then <<
        princ "go not in program context";
        terpri() >>;
!#endif
    pl := s!:current_proglabels;
    while pl and null d do <<
       d := atsoc(cadr x, car pl);
       if null d then pl := cdr pl >>;
    if null d then <<
       if posn() neq 0 then terpri();
       princ "+++++ label "; prin cadr x; princ " not set"; terpri();
       return >>;
    d := cadr d;
    s!:restore_stack(cdr env, cdr d);
    s!:outjump('JUMP, car d)
  end;

put('go, 's!:compfn, function s!:comgo);

symbolic procedure s!:comreturn!-from(x, env, context);
% Even in Common Lisp Mode I do not support (yet) RETURN statements that
% escape from one LAMBDA expression into an enclosing one.
  begin
    scalar tag;
!#if (not common!-lisp!-mode)
    if context < 4 then <<
       princ "+++++ return or return-from not in prog context";
       terpri() >>;
!#endif
    x := cdr x;
    tag := car x;
    if cdr x then x := cadr x else x := nil;
!#if common!-lisp!-mode
    s!:comval(x, env, 1);
!#else
    s!:comval(x, env, context-4);
!#endif
    x := atsoc(tag, s!:current_exitlab);
    if null x then error(0, list("invalid return-from", tag));
    x := cdr x;
    s!:restore_stack(cdr env, cdr x);
    s!:outjump('JUMP, car x)
  end;

put('return!-from, 's!:compfn, function s!:comreturn!-from);

symbolic procedure s!:comreturn(x, env, context);
   s!:comreturn!-from('return!-from . nil . cdr x, env, context);

put('return, 's!:compfn, function s!:comreturn);

% conditional code is generated via jumpif, which jumps to label lab
% if x evaluates to the value of neg

global '(s!:jumplts s!:jumplnils s!:jumpatoms s!:jumpnatoms);

s!:jumplts := s!:vecof '(JUMPL0T JUMPL1T JUMPL2T JUMPL3T JUMPL4T);
s!:jumplnils := s!:vecof '(JUMPL0NIL JUMPL1NIL JUMPL2NIL JUMPL3NIL JUMPL4NIL);
s!:jumpatoms := s!:vecof '(JUMPL0ATOM JUMPL1ATOM JUMPL2ATOM JUMPL3ATOM);
s!:jumpnatoms := s!:vecof '(JUMPL0NATOM JUMPL1NATOM JUMPL2NATOM JUMPL3NATOM);


symbolic procedure s!:jumpif(neg, x, env, lab);
% There are some special optimised cases for tests on simple atomic
% values - both local and free variables.
  begin
    scalar w, w1, j;
top:
    if null x then <<
       if not neg then s!:outjump('JUMP, lab);
       return nil >>
    else if x eq t or (eqcar(x, 'quote) and cadr x) or
       (atom x and not symbolp x) then <<
       if neg then s!:outjump('JUMP, lab);
       return nil >>
    else if (w := s!:islocal(x, env)) < 5 then
       return s!:outjump(getv(if neg then s!:jumplts else s!:jumplnils, w),
                         lab)
    else if w = 99999 and symbolp x then <<
       s!:should_be_fluid x;
       w := list(if neg then 'JUMPFREET else 'JUMPFREENIL, x, x);
       return s!:record_literal_for_jump(w, env, lab) >>;
    if not atom x and atom car x and (w := get(car x, 's!:testfn)) then
       return funcall(w, neg, x, env, lab);
    if not atom x then <<
       w := s!:improve x;
       if atom w or not eqcar(x, car w) then << x := w; go to top >>;
!#if common!-lisp!-mode
       if w1 := s!:local_macro car w then <<
          if atom cdr w1 then x := 'funcall . cdr w1 . cdr w
          else x := funcall('lambda . cdr w1, w);
          go to top >>;
!#endif
       if (w1 := get(car w, 's!:compilermacro)) and
          (w1 := funcall(w1, w, env, 1)) then <<
          x := w1; go to top >> >>;
% I only expand ordinary macros here if the expansion leads to something
% with a TESTFN or COMPILERMACRO property or to an atom.
remacro:
    if (not atom w) and (w1 := macro!-function car w) then <<
       w := funcall(w1, w);
       if atom w or
          eqcar(w, 'quote) or
          get(car w, 's!:testfn) or
          get(car w, 's!:compilermacro) then << x := w; go to top >>;
       go to remacro >>;
    s!:comval(x, env, 1);
    w := s!:current_block;
    while w and not atom car w do w := cdr w;
    j := '(JUMPNIL . JUMPT);
    if w then <<
       w1 := car w;
       w := cdr w;
       if w1 = 'STORELOC0 then <<
          s!:current_block := w;
          s!:current_size := s!:current_size - 1;
          j := '(JUMPST0NIL . JUMPST0T) >>
       else if w1 = 'STORELOC1 then <<
          s!:current_block := w;
          s!:current_size := s!:current_size - 1;
          j := '(JUMPST1NIL . JUMPST1T) >>
       else if w1 = 'STORELOC2 then <<
          s!:current_block := w;
          s!:current_size := s!:current_size - 1;
          j := '(JUMPST2NIL . JUMPST2T) >>
       else if eqcar(w, 'BUILTIN1) then <<
          s!:current_block := cdr w;
          s!:current_size := s!:current_size - 2;
          j := list('JUMPB1NIL, w1) . list('JUMPB1T, w1) >>
       else if eqcar(w, 'BUILTIN2) then <<
          s!:current_block := cdr w;
          s!:current_size := s!:current_size - 2;
          j := list('JUMPB2NIL, w1) . list('JUMPB2T, w1) >> >>;
    return s!:outjump(if neg then cdr j else car j, lab)
  end;

symbolic procedure s!:testnot(neg, x, env, lab);
   s!:jumpif(not neg, cadr x, env, lab);

put('null, 's!:testfn, function s!:testnot);
put('not,  's!:testfn, function s!:testnot);

symbolic procedure s!:testatom(neg, x, env, lab);
  begin
    scalar w;
    if (w := s!:islocal(cadr x, env)) < 4 then
       return s!:outjump(getv(if neg then s!:jumpatoms else s!:jumpnatoms, w),
                         lab);
    s!:comval(cadr x, env, 1);
    if neg then s!:outjump('JUMPATOM, lab)
    else s!:outjump('JUMPNATOM, lab)
  end;

put('atom, 's!:testfn, function s!:testatom);

symbolic procedure s!:testconsp(neg, x, env, lab);
  begin
    scalar w;
    if (w := s!:islocal(cadr x, env)) < 4 then
       return s!:outjump(getv(if neg then s!:jumpnatoms else s!:jumpatoms, w),
                         lab);
    s!:comval(cadr x, env, 1);
    if neg then s!:outjump('JUMPNATOM, lab)
    else s!:outjump('JUMPATOM, lab)
  end;

put('consp, 's!:testfn, function s!:testconsp);

symbolic procedure s!:comcond(x, env, context);
  begin
    scalar l1, l2, w;
    l1 := gensym();
    while (x := cdr x) do <<
      w := car x;
      if atom cdr w then <<
        s!:comval(car w, env, 1);
        s!:outjump('JUMPT, l1);
        l2 := nil >>
      else <<
         if car w = t then l2 := nil
         else <<
           l2 := gensym();
           s!:jumpif(nil, car w, env, l2) >>;
         w := cdr w;
         if null cdr w then w := car w
         else w := 'progn . w;
         s!:comval(w, env, context);
         if l2 then << s!:outjump('JUMP, l1); s!:set_label l2 >>
         else x := '(nil) >> >>;
    if l2 then s!:comval(nil, env, context);
    s!:set_label l1
  end;

put('cond, 's!:compfn, function s!:comcond);

symbolic procedure s!:comif(x, env, context);
  begin
    scalar l1, l2;
    l2 := gensym();
    s!:jumpif(nil, cadr x, env, l2);
    x := cddr x;
    s!:comval(car x, env, context);
    x := cdr x;
    if x or (context < 2 and (x := '(nil))) then <<
       l1 := gensym();
       s!:outjump('JUMP, l1);
       s!:set_label l2;
       s!:comval(car x, env, context);
       s!:set_label l1 >>
    else s!:set_label l2
  end;

put('if, 's!:compfn, function s!:comif);

symbolic procedure s!:comwhen(x, env, context);
  begin
    scalar l2;
    l2 := gensym();
    if context < 2 then <<
       s!:comval(cadr x, env, 1);
       s!:outjump('JUMPNIL, l2) >>
    else s!:jumpif(nil, cadr x, env, l2);
    s!:comval('progn . cddr x, env, context);
    s!:set_label l2
  end;

put('when, 's!:compfn, function s!:comwhen);

symbolic procedure s!:comunless(x, env, context);
   s!:comwhen(list!*('when, list('not, cadr x), cddr x), env, context);

put('unless, 's!:compfn, function s!:comunless);

% The S:ICASE function is not really intended for direct use. It is there
% to provide Lisp-code with the ability to generate the ICASE byte opcode.
% The usage is
%    (s!:icase <expression>
%        <default-value>
%        <case 0 value>
%        <case 1 value>
%        <case 2 value>
%        ...
%        <case n value>)
% and the value if selected on the basis of the expression, which will
% normally evaluate to an integer in the range 0 to n.

symbolic procedure s!:comicase(x, env, context);
  begin
    scalar l1, labs, labassoc, w;
    x := cdr x;
    for each v in cdr x do <<
        w := assoc!*!*(v, labassoc);
% If the same value occurs in several cases then I set just one label
% and re-use it.
        if w then l1 := cdr w . l1
        else <<
            l1 := gensym();
            labs := l1 . labs;
            labassoc := (v . l1) . labassoc >> >>;
    s!:comval(car x, env, 1);
    s!:outjump('ICASE, reversip labs);
    l1 := gensym();
    for each v in labassoc do <<
       s!:set_label cdr v;
       s!:comval(car v, env, context);
       s!:outjump('JUMP, l1) >>;
    s!:set_label l1
  end;

put('s!:icase, 's!:compfn, function s!:comicase);

put('JUMPLITEQ!*, 's!:opcode, get('JUMPLITEQ, 's!:opcode));
put('JUMPLITNE!*, 's!:opcode, get('JUMPLITNE, 's!:opcode));

%
% s!:jumpliteqn jumps to lab is the A register is EQL to val. In
% all sensible cases an EQ test can be used, but when that will not
% be possible (mainly floats or bignums) the EQL function itself is
% invoked. This preserves full generality!
%

symbolic procedure s!:jumpliteql(val, lab, env);
  begin
    scalar w;
    if idp val or
       eq!-safe val then <<
        w := list('JUMPLITEQ!*, val, val);
        s!:record_literal_for_jump(w, env, lab) >>
    else <<
        s!:outopcode0('PUSH, '(PUSH));
        s!:loadliteral(val, env);
        s!:outopcode1('BUILTIN2, get('eql, 's!:builtin2), 'eql);
        s!:outjump('JUMPT, lab);
        flag(list lab, 's!:jumpliteql);
        s!:outopcode0('POP, '(POP)) >>
  end;

symbolic procedure s!:casebranch(sw, env, dflt);
  begin
    scalar size, w, w1, r, g;
    size := 4+truncate(length sw,2);
% I probably do not need to go as far as making the size of my hash table
% prime, but the specific case of multiples of 13 is filtered for here
% since powers of 13 are used in the sxhash/eqlhash calculation.
    while remainder(size, 2)=0 or remainder(size, 3)=0 or
          remainder(size, 5)=0 or remainder(size, 13)=0 do size := size+1;
    for each p in sw do <<
        w := remainder(eqlhash car p, size);
        w1 := assoc!*!*(w, r);
        if w1 then rplacd(cdr w1, p . cddr w1)
        else r := list(w, gensym(), p) . r >>;
    s!:outopcode0('PUSH, '(PUSH));
    rplacd(env, 0 . cdr env);
    s!:outopcode1lit('CALL1, 'eqlhash, env);
    s!:loadliteral(size, env);
    g := gensym();
    s!:outopcode1('BUILTIN2, get('iremainder, 's!:builtin2), 'iremainder);
    s!:outjump('ICASE, g . for i := 0:size-1 collect <<
        w := assoc!*!*(i, r);
        if w then cadr w else g >>);
    for each p in r do <<
        s!:set_label cadr p;
        s!:outopcode0('POP, '(POP));
        for each q in cddr p do s!:jumpliteql(car q, cdr q, env);
        s!:outjump('JUMP, dflt) >>;
    s!:set_label g;
    s!:outopcode0('POP, '(POP));
    s!:outjump('JUMP, dflt);
    rplacd(env, cddr env)
  end;

symbolic procedure s!:comcase(x, env, context);
  begin
    scalar keyform, blocks, v, w, g, dflt, sw, keys, nonnum;
    x := cdr x;
    keyform := car x;
    for each y on cdr x do <<
        w := assoc!*!*(cdar y, blocks);
        if w then g := cdr w
        else <<
            g := gensym();
            blocks := (cdar y . g) . blocks >>;
        w := caar y;
        if null cdr y and (w = t or w = 'otherwise) then dflt := g
        else <<
            if atom w then w := list w;
            for each n in w do <<
                if idp n
!#if common!-lisp!-mode
                   or characterp n
!#endif
                   or numberp n then <<
                    if not fixp n then nonnum := t;
                    keys := n . keys;
                    sw := (n . g) . sw >>
% The test made is supposed (in Common Lisp) to be EQL. I take the
% severe view that I will not accept labels that are lists or vectors
% or strings or other things where EQL is a nasty sort of test. This is
% not in accordance with full Common Lisp, and if this really hurts me
% some time I can degenerate and turn out very clumsy sequences of test-
% and-branch code in marginal cases.
                else error(0, list("illegal case label", n)) >> >> >>;
    if null dflt then <<
        if (w := assoc!*!*(nil, blocks)) then dflt := cdr w
        else blocks := (nil . (dflt := gensym())) . blocks >>;
    if not nonnum then <<
        keys := sort(keys, function lessp);
        nonnum := car keys;
        g := lastcar keys;
        if g - nonnum < 2*length keys then <<
% If the keys are a fairly compact block of fixnums I can do an
% especially good job.
            if not (nonnum = 0) then <<
                keyform := list('xdifference, keyform, nonnum);
                sw := for each y in sw collect (car y - nonnum) . cdr y >>;
            s!:comval(keyform, env, 1);
            w := nil;
            for i := 0:g do
               if (v := assoc!*!*(i, sw)) then w := cdr v . w
               else w := dflt . w;
            w := dflt . reversip w;
            s!:outjump('ICASE, w);
            nonnum := nil >>
        else nonnum := t >>;
    if nonnum then <<
% If I have only a few cases I do repeated test/branch combinations,
% but if I have a LOT I will try hashing. The change-over point at 7
% is pretty much a GUESS for where it should reasonably go.
        s!:comval(keyform, env, 1);
        if length sw < 7 then <<
% The code here is DELICATE. This is because USUALLY the JUMPLITEQ
% code preserve the A register, but when expanded to a pair of
% instructions maybe it does not. To deal with this I use JUMPLITEQ!*
% which expands slightly differently...  Also I have to be prepared to
% cope with floats or bignums (and I do so in a very ugly way)
            for each y in sw do s!:jumpliteql(car y, cdr y, env);
            s!:outjump('JUMP, dflt) >>
        else s!:casebranch(sw, env, dflt) >>;
    g := gensym();
    for each v in blocks do <<
       s!:set_label cdr v;
       if flagp(cdr v, 's!:jumpliteql) then s!:outlose 1;
       s!:comval('progn . car v, env, context);
       s!:outjump('JUMP, g) >>;
    s!:set_label g
  end;

put('case, 's!:compfn, function s!:comcase);


fluid '(!*defn dfprint!* s!:dfprintsave s!:faslmod_name);

symbolic procedure s!:comeval!-when(x, env, context);
  begin
    scalar y;
    x := cdr x;
    y := car x;
princ "COMPILING eval-when: "; print y; print x;
    x := 'progn . cdr x;
    if memq('compile, y) then eval x;
    if memq('load, y) then <<
       if dfprint!* then apply1(dfprint!*, x) >>;
    if memq('eval, y) then s!:comval(x, env, context)
    else s!:comval(nil, env, context)
  end;

put('eval!-when, 's!:compfn, function s!:comeval!-when);

% (the <type> <value>) is treated here as just <value>, but in the
% longer term notice should be taken of the type information.

symbolic procedure s!:comthe(x, env, context);
   s!:comval(caddr x, env, context);

put('the, 's!:compfn, function s!:comthe);

symbolic procedure s!:comand(x, env, context);
% AND and OR are not transparent to program context, and
% are always assumed to be used for their value.
% Is it worth doing something special if all the values tested are
% known to be regular style predicates? (eg NULL, ATOM, EQ etc calls)
  begin
    scalar l;
    l := gensym();
    x := cdr x;
    s!:comval(car x, env, 1);
    while x := cdr x do <<
      s!:outjump('JUMPNIL, l);
      s!:comval(car x, env, 1) >>;
    s!:set_label l
  end;

put('and, 's!:compfn, function s!:comand);

symbolic procedure s!:comor(x, env, context);
  begin
    scalar l;
    l := gensym();
    x := cdr x;
    s!:comval(car x, env, 1);
    while x := cdr x do <<
      s!:outjump('JUMPT, l);
      s!:comval(car x, env, 1) >>;
    s!:set_label l
  end;

put('or, 's!:compfn, function s!:comor);

symbolic procedure s!:combool(neg, x, env, lab);
% Used for AND and OR when they occur in predicates rather
% than in places where their (full) value is required.
  begin
    scalar fn;
    fn := eqcar(x, 'or);
    if fn eq neg then
       while x := cdr x do
          s!:jumpif(fn, car x, env, lab)
    else <<
       neg := gensym();
       while x := cdr x do
          s!:jumpif(fn, car x, env, neg);
       s!:outjump('JUMP, lab);
       s!:set_label neg >>
  end;

put('and, 's!:testfn, function s!:combool);
put('or,  's!:testfn, function s!:combool);

symbolic procedure s!:testeq(neg, x, env, lab);
  begin
    scalar a, b;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
    if s!:eval_to_eq_unsafe a or s!:eval_to_eq_unsafe b then <<
       if posn() neq 0 then terpri();
       princ "++++ EQ on number upgraded to EQUAL in ";
       prin s!:current_function; princ " : ";
       prin a; princ " "; print b;
       return s!:testequal(neg, 'equal . cdr x, env, lab) >>;
    if !*carefuleq then <<
       s!:comval(x, env, 1);
       s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab);
       return >>;
% eq tests against nil can be optimised a bit
    if null a then s!:jumpif(not neg, b, env, lab)
    else if null b then s!:jumpif(not neg, a, env, lab)
    else if eqcar(a, 'quote) or (atom a and not symbolp a) then <<
       s!:comval(b, env, 1);
       if eqcar(a, 'quote) then a := cadr a;
       b := list(if neg then 'JUMPLITEQ else 'JUMPLITNE, a, a);
       s!:record_literal_for_jump(b, env, lab) >>
    else if eqcar(b, 'quote) or (atom b and not symbolp b) then <<
       s!:comval(a, env, 1);
       if eqcar(b, 'quote) then b := cadr b;
       a := list(if neg then 'JUMPLITEQ else 'JUMPLITNE, b, b);
       s!:record_literal_for_jump(a, env, lab) >>
    else <<
       s!:load2(a, b, env);
       if neg then s!:outjump('JUMPEQ, lab)
       else s!:outjump('JUMPNE, lab) >>;
  end;

symbolic procedure s!:testeq1(neg, x, env, lab);
  begin
    scalar a, b;
    if !*carefuleq then <<
       s!:comval(x, env, 1);
       s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab);
       return >>;
    a := s!:improve cadr x;
    b := s!:improve caddr x;
% eq tests against nil can be optimised a bit
    if null a then s!:jumpif(not neg, b, env, lab)
    else if null b then s!:jumpif(not neg, a, env, lab)
    else if eqcar(a, 'quote) or (atom a and not symbolp a) then <<
       s!:comval(b, env, 1);
       if eqcar(a, 'quote) then a := cadr a;
       b := list(if neg then 'JUMPLITEQ else 'JUMPLITNE, a, a);
       s!:record_literal_for_jump(b, env, lab) >>
    else if eqcar(b, 'quote) or (atom b and not symbolp b) then <<
       s!:comval(a, env, 1);
       if eqcar(b, 'quote) then b := cadr b;
       a := list(if neg then 'JUMPLITEQ else 'JUMPLITNE, b, b);
       s!:record_literal_for_jump(a, env, lab) >>
    else <<
       s!:load2(a, b, env);
       if neg then s!:outjump('JUMPEQ, lab)
       else s!:outjump('JUMPNE, lab) >>;
  end;

put('eq, 's!:testfn, function s!:testeq);

if eq!-safe 0 then put('iequal, 's!:testfn, function s!:testeq1)
else put('iequal, 's!:testfn, function s!:testequal);

symbolic procedure s!:testequal(neg, x, env, lab);
  begin
    scalar a, b;
    a := cadr x;
    b := caddr x;
% equal tests against nil can be optimised
    if null a then s!:jumpif(not neg, b, env, lab)
    else if null b then s!:jumpif(not neg, a, env, lab)
% comparisons involving a literal identifier or (in this
% Lisp implementation) a fixnum can be turned into uses of
% eq rather than equal, to good effect.
    else if (eqcar(a, 'quote) and (symbolp cadr a or eq!-safe cadr a)) or
            (eqcar(b, 'quote) and (symbolp cadr b or eq!-safe cadr b)) or
            (not idp a and eq!-safe a) or
            (not idp b and eq!-safe b) then
        s!:testeq1(neg, 'eq . cdr x, env, lab)
   else <<
      s!:load2(a, b, env);    % args commute here if that helps
      if neg then s!:outjump('JUMPEQUAL, lab)
      else s!:outjump('JUMPNEQUAL, lab) >>
  end;

put('equal, 's!:testfn, function s!:testequal);

symbolic procedure s!:testneq(neg, x, env, lab);
   s!:testequal(not neg, 'equal . cdr x, env, lab);

put('neq, 's!:testfn, function s!:testneq);

symbolic procedure s!:testeqcar(neg, x, env, lab);
  begin
    scalar a, b, sw, promote;
    a := cadr x;
    b := s!:improve caddr x;
    if s!:eval_to_eq_unsafe b then <<
       if posn() neq 0 then terpri();
       princ "++++ EQCAR on number upgraded to EQUALCAR in ";
       prin s!:current_function; princ " : "; print b;
       promote := t >>
    else if !*carefuleq then <<
       s!:comval(x, env, 1);
       s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab);
       return >>;
    if not promote and eqcar(b, 'quote) then <<
       s!:comval(a, env, 1);
       b := cadr b;
       a := list(if neg then 'JUMPEQCAR else 'JUMPNEQCAR, b, b);
       s!:record_literal_for_jump(a, env, lab) >>
    else <<
       sw := s!:load2(a, b, env);
       if sw then s!:outopcode0('SWOP, '(SWOP));
       if promote then
           s!:outopcode1('BUILTIN2, get('equalcar, 's!:builtin2), 'equalcar)
       else s!:outopcode0('EQCAR, '(EQCAR));
       s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab) >>
  end;

put('eqcar, 's!:testfn, function s!:testeqcar);

symbolic procedure s!:testflagp(neg, x, env, lab);
  begin
    scalar a, b, sw;
    a := cadr x;
    b := caddr x;
    if eqcar(b, 'quote) then <<
       s!:comval(a, env, 1);
       b := cadr b;
       sw := symbol!-make!-fastget(b, nil);
       if sw then <<
          s!:outopcode1('FASTGET, logor(sw, 128), b);
          s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab) >>
       else <<
          a := list(if neg then 'JUMPFLAGP else 'JUMPNFLAGP, b, b);
          s!:record_literal_for_jump(a, env, lab) >> >>
    else <<
       sw := s!:load2(a, b, env);
       if sw then s!:outopcode0('SWOP, '(SWOP));
       s!:outopcode0('FLAGP, '(FLAGP));
       s!:outjump(if neg then 'JUMPT else 'JUMPNIL, lab) >>
  end;

put('flagp, 's!:testfn, function s!:testflagp);

global '(s!:storelocs);
s!:storelocs := s!:vecof '(STORELOC0 STORELOC1 STORELOC2 STORELOC3
                           STORELOC4 STORELOC5 STORELOC6 STORELOC7);

symbolic procedure s!:comsetq(x, env, context);
  begin
    scalar n, w, var;
    x := cdr x;
    if null x then return;
    if not symbolp car x or null cdr x then
       return error(0, list("bad args for setq", x));
    s!:comval(cadr x, env, 1);
    var := car x;
    n := 0;
    w := cdr env;
% storing into a lexical variable involves stack access, otherwise
% I need to update the global value cell
    while w and not eqcar(w, var) do << n := add1 n; w := cdr w >>;
    if w then <<
       if not member!*!*('loc . w, s!:a_reg_values) then
           s!:a_reg_values := ('loc . w) . s!:a_reg_values;
       if n < 8 then s!:outopcode0(getv(s!:storelocs, n),
                                          list('storeloc, var))
       else if n > 4095 then error(0, "stack frame > 4095")
       else if n > 255 then
          s!:outopcode2('BIGSTACK, 64+truncate(n,256), logand(n, 255),
                        list('STORELOC, var))
       else s!:outopcode1('STORELOC, n, var) >>
    else if w := s!:find_lexical(var, s!:lexical_env, 0) then <<
        if not member!*!*('lex . w, s!:a_reg_values) then
            s!:a_reg_values := ('lex . w) . s!:a_reg_values;
        s!:outlexref('STORELEX, length cdr env, car w, cadr w, var) >>
    else <<
        if null var or var eq t then
           error(0, list("bad variable in setq", var))
        else s!:should_be_fluid var;
        w := 'free . var;
        if not member!*!*(w, s!:a_reg_values) then
           s!:a_reg_values := w . s!:a_reg_values;
        s!:outopcode1lit('STOREFREE, var, env) >>;
% For this very small extra I can support (setq a A b B c C ...)
    if cddr x then return s!:comsetq(cdr x, env, context)
  end;

put('setq, 's!:compfn, function s!:comsetq);
put('noisy!-setq, 's!:compfn, function s!:comsetq);

% cons-related functions seem quite important to Lisp, so I provide
% a bit of special support - cons has a variant xcons for use when its
% arguments are most conveniently evaluated in the 'other' order, and
% list gets specialised into ncons, list2 and list3.  Two functions
% acons and list2!* provide useful combinations of a pair of cons
% operations - use of them reduces overhead associated with the allocation
% of freestore.

symbolic procedure s!:comlist(x, env, context);
 begin
    scalar w;
    if null (x := cdr x) then return s!:comval(nil, env, context);
    s!:a_reg_values := nil;
    if null (w := cdr x) then
       s!:comval(list('ncons, car x), env, context)
    else if null (w := cdr w) then
       s!:comval(list('list2, car x, cadr x), env, context)
    else if null cdr w then
       s!:comval(list('list3, car x, cadr x, car w), env, context)
    else s!:comval(list('list2!*, car x, cadr x, 'list . w), env, context)
  end;

put('list, 's!:compfn, function s!:comlist);

symbolic procedure s!:comlist!*(x, env, context);
 begin
    scalar w;
    if null (x := cdr x) then return s!:comval(nil, env, context);
    s!:a_reg_values := nil;
    if null (w := cdr x) then
       s!:comval(car x, env, context)
    else if null (w := cdr w) then
       s!:comval(list('cons, car x, cadr x), env, context)
    else if null cdr w then
       s!:comval(list('list2!*, car x, cadr x, car w), env, context)
    else s!:comval(list('list2!*, car x, cadr x, 'list!* . w), env, context)
  end;

put('list!*, 's!:compfn, function s!:comlist!*);

symbolic procedure s!:comcons(x, env, context);
  begin
    scalar a, b;
    a := cadr x;
    b := caddr x;
    if b=nil or b='(quote nil) then
       s!:comval(list('ncons, a), env, context)
    else if eqcar(a, 'cons) then
       s!:comval(list('acons, cadr a, caddr a, b), env, context)
    else if eqcar(b, 'cons) then
       if null caddr b then s!:comval(list('list2, a, cadr b), env, context)
       else s!:comval(list('list2!*, a, cadr b, caddr b), env, context)
!#if (not common!-lisp!-mode)
% For Common Lisp it seems that I *must* evaluate args strictly left-to-right.
    else if not !*ord and s!:iseasy a and not s!:iseasy b then
       s!:comval(list('xcons, b, a), env, context)
!#endif
    else s!:comcall(x, env, context)
  end;

put('cons, 's!:compfn, function s!:comcons);

!#if common!-lisp!-mode

% I must not open-compile VECTOR in Standard Lisp mode because REDUCE
% has a function of that name that is nothing like the one I support here.
% But the version here can be useful so that things like
%    (vector e1 e2 ... en)
% for large n do not generate function calls with excessive numbers of args.

symbolic procedure s!:vector_compilermacro(x, env, context);
  begin
    scalar args, n, n1, r, w, v, i;
    v := gensym();
    i := gensym();
    args := cdr x;
    n := n1 := length args;
    while n > 12 do <<
       w := nil;
       for j := 1:12 do << w := car args . w; args := cdr args >>;
       r := list('setq, i, ('fill!-vector . v . i . reverse w)) . r;
       n := n - 12 >>;
    if n > 0 then r := ('fill!-vector . v . i . args) . r;
    r := 'let .
          list(list(v, list('mkvect, n1-1)),
               list(i, 0)) .
          reverse (v . r);
    return r
  end;

put('vector, 's!:compilermacro, function s!:vector_compilermacro);

symbolic procedure s!:commv!-call(x, env, context);
  begin
    scalar fn, args;
    fn := cadr x;
    args := for each v in cddr x collect list('mv!-list!*, v);
    args := expand(args, 'append);
    if not (fn = '(function list)) then
       args := list('apply, fn, args);
    s!:comval(args, env, context)
  end;

put('multiple!-value!-call, 's!:compfn, function s!:commv!-call);

symbolic procedure s!:commv!-prog1(x, env, context);
  begin
    x := cdr x;
    if null x then return s!:comval(nil, env, context)
    else if null cdr x then return s!:comval(car x, env, context);
    s!:comval(list('mv!-list!*, car x), env, context);
    s!:outopcode0('PUSH, '(PUSH));
    rplacd(env, 0 . cdr env);
    for each a in x do
        s!:comval(a, env, if context >= 4 then context else 2);
    s!:outopcode0('POP, '(POP));
    rplacd(env, cddr env);
    s!:loadliteral('values, env);
    s!:outopcode1('BUILTIN2, get('apply1, 's!:builtin2), 'apply1)
  end;

put('multiple!-value!-prog1, 's!:compfn, function s!:commv!-prog1);

!#endif

symbolic procedure s!:comapply(x, env, context);
  begin
    scalar a, b, n;
    a := cadr x;        % fn
    b := caddr x;       % args
% I collect the very special idiom
%     (apply xxx (list A B C ...))
% and map it on to
%     (funcall xxx A B C)
% but if the list is made up on any other way (eg using a mixture of
% calls to LIST and CONS) I just let it go through the usual slower route.
    if null cdddr x and eqcar(b, 'list) then <<
       if eqcar(a, 'quote) then return <<
          n := s!:current_function;
          begin 
              scalar s!:current_function;
% the re-binding of current-function is to avoid use of callself
% in some cases (e.g. the autoloader) when the function I am
% in has just been redefined.
              s!:current_function := compress
                 append(explode n, '!! . '!. . explodec
                    (s!:current_count := s!:current_count + 1));
              return s!:comval(cadr a . cdr b, env, context)
          end >>;
       n := length (b := cdr b);
       return s!:comval('funcall . a . b, env, context) >>
    else if null b and null cdddr x then
       return s!:comval(list('funcall, a), env, context)
    else return s!:comcall(x, env, context)
  end;

put('apply, 's!:compfn, function s!:comapply);

symbolic procedure s!:imp_funcall u;
  begin
    scalar n;
    u := cdr u;
    if eqcar(car u, 'function) then return s!:improve(cadar u . cdr u);
    n := length cdr u;
    u := if n = 0 then 'apply0 . u
      else if n = 1 then 'apply1 . u
      else if n = 2 then 'apply2 . u
      else if n = 3 then 'apply3 . u
%     else if n = 4 then 'apply4 . u
      else 'funcall!* . u;
!#if record!-use!-of!-funcall
% If this flag is set when the compiler is built then every "funcall" in the
% original source will get logged. If there are too many of them this
% can be painfully expensive.
    u := list('progn, 
           list('s!:record_funcall, mkquote s!:current_function, mkquote u),
           u);
!#endif
    return u
  end;

!#if record!-use!-of!-funcall

global '(all_funcalls);

symbolic procedure s!:record_funcall(fromfn, call);
  begin
    scalar w;
    if not memq(fromfn, all_funcalls) then
       all_funcalls := fromfn . all_funcalls;
    w := get(fromfn, 'all_funcalls);
    while not atom w and not atom car w and not (caar w = call) do w := cdr w;
    if null w then
       put(fromfn, 'all_funcalls, (call . 1) . get(fromfn, 'all_funcalls))
    else rplacd(car w, cdar w + 1)
  end;

symbolic procedure display!-funcalls();
  begin
    scalar w;
    w := linelength 500;
    terpri();
    for each fn in all_funcalls do <<
      for each x in get(fn, 'all_funcalls) do <<
        princ cdr x; ttab 10; prin fn; ttab 40; prin car x; terpri() >>;
      remprop(fn, 'all_funcalls) >>;
    all_funcalls := nil;
    terpri();
    linelength w;
  end;

!#endif

put('funcall, 's!:tidy_fn, 's!:imp_funcall);


%
% The next few cases are concerned with demoting functions that use
% equal tests into ones that use eq instead

symbolic procedure s!:eval_to_eq_safe x;
   null x or 
   x=t or 
   (not symbolp x and eq!-safe x) or
   (not atom x and flagp(car x, 'eq!-safe)) or
   (eqcar(x, 'quote) and (symbolp cadr x or eq!-safe cadr x));

symbolic procedure s!:eval_to_eq_unsafe x;
   (atom x and not symbolp x and not eq!-safe x) or
   (not atom x and flagp(car x, 'eq!-unsafe)) or
   (eqcar(x, 'quote) and (not atom cadr x or
      (not symbolp cadr x and not eq!-safe cadr x)));

symbolic procedure s!:list_all_eq_safe u;
  atom u or
  ((symbolp car u or eq!-safe car u) and s!:list_all_eq_safe cdr u);

symbolic procedure s!:eval_to_list_all_eq_safe x;
  null x or
  (eqcar(x, 'quote) and s!:list_all_eq_safe cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (s!:eval_to_eq_safe cadr x and
     s!:eval_to_list_all_eq_safe ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   s!:eval_to_eq_safe cadr x and
   s!:eval_to_list_all_eq_safe caddr x);

symbolic procedure s!:list_some_eq_unsafe u;
  not atom u and
  (s!:eval_to_eq_unsafe car u or s!:list_some_eq_unsafe cdr u);

symbolic procedure s!:eval_to_list_some_eq_unsafe x;
  if atom x then nil
  else if eqcar(x, 'quote) then s!:list_some_eq_unsafe cadr x
  else if eqcar(x, 'list) and cdr x then
     s!:eval_to_eq_unsafe cadr x or s!:eval_to_list_some_eq_unsafe ('list . cddr x)
  else if eqcar(x, 'cons) then
     s!:eval_to_eq_unsafe cadr x or s!:eval_to_list_some_eq_unsafe caddr x
  else nil;

symbolic procedure s!:eval_to_car_eq_safe x;
  (eqcar(x, 'cons) or eqcar(x, 'list)) and
  not null cdr x and
  s!:eval_to_eq_safe cadr x;

symbolic procedure s!:eval_to_car_eq_unsafe x;
  (eqcar(x, 'cons) or eqcar(x, 'list)) and
  not null cdr x and
  s!:eval_to_eq_unsafe cadr x;

symbolic procedure s!:alist_eq_safe u;
  atom u or
  (not atom car u and
   (symbolp caar u or eq!-safe caar u) and 
   s!:alist_eq_safe cdr u);

symbolic procedure s!:eval_to_alist_eq_safe x;
  null x or
  (eqcar(x, 'quote) and s!:alist_eq_safe cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (s!:eval_to_car_eq_safe cadr x and
     s!:eval_to_alist_eq_safe ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   s!:eval_to_car_eq_safe cadr x and
   s!:eval_to_alist_eq_safe caddr x);


symbolic procedure s!:alist_eq_unsafe u;
  not atom u and
  not atom car u and
  (not atom caar u or
   (not symbolp caar u and not eq!-safe caar u) or 
   s!:alist_eq_unsafe cdr u);

symbolic procedure s!:eval_to_alist_eq_unsafe x;
  if null x then nil
  else if eqcar(x, 'quote) then s!:alist_eq_unsafe cadr x
  else if eqcar(x, 'list) then
   (cdr x and
    (s!:eval_to_car_eq_unsafe cadr x or
     s!:eval_to_alist_eq_unsafe ('list . cddr x)))
  else if eqcar(x, 'cons) then
   s!:eval_to_car_eq_unsafe cadr x or
   s!:eval_to_alist_eq_safe caddr x
  else nil;

!#if (not common!-lisp!-mode)

flag('(eq eqcar null not greaterp lessp geq leq minusp
       atom numberp consp), 'eq!-safe);

if not eq!-safe 1 then
   flag('(length plus minus difference times quotient
          plus2 times2 expt fix float), 'eq!-unsafe);

symbolic procedure s!:comequal(x, env, context);
   if s!:eval_to_eq_safe cadr x or
      s!:eval_to_eq_safe caddr x then
      s!:comcall('eq . cdr x, env, context)
   else s!:comcall(x, env, context);

put('equal, 's!:compfn, function s!:comequal);

symbolic procedure s!:comeq(x, env, context);
   if s!:eval_to_eq_unsafe cadr x or
      s!:eval_to_eq_unsafe caddr x then <<
      if posn() neq 0 then terpri();
      princ "++++ EQ on number upgraded to EQUAL in ";
      prin s!:current_function; princ " : ";
      prin cadr x; princ " "; print caddr x;
      s!:comcall('equal . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('eq, 's!:compfn, function s!:comeq);

symbolic procedure s!:comeqcar(x, env, context);
   if s!:eval_to_eq_unsafe caddr x then <<
      if posn() neq 0 then terpri();
      princ "++++ EQCAR on number upgraded to EQUALCAR in ";
      prin s!:current_function; princ " : ";
      prin caddr x;
      s!:comcall('equalcar . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('eqcar, 's!:compfn, function s!:comeqcar);

symbolic procedure s!:comsublis(x, env, context);
   if s!:eval_to_alist_eq_safe cadr x then
      s!:comval('subla . cdr x, env, context)
   else s!:comcall(x, env, context);

put('sublis, 's!:compfn, function s!:comsublis);

symbolic procedure s!:comsubla(x, env, context);
   if s!:eval_to_alist_eq_unsafe cadr x then <<
       if posn() neq 0 then terpri();
       princ "++++ SUBLA on number upgraded to SUBLIS in ";
       prin s!:current_function; princ " : ";
       print cadr x;
       s!:comval('sublis . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('subla, 's!:compfn, function s!:comsubla);

symbolic procedure s!:comassoc(x, env, context);
   if (s!:eval_to_eq_safe cadr x or s!:eval_to_alist_eq_safe caddr x) and length x = 3 then
      s!:comval('atsoc . cdr x, env, context)
   else if length x = 3 then s!:comcall('assoc!*!* . cdr x, env, context)
   else s!:comcall(x, env, context);

put('assoc, 's!:compfn, function s!:comassoc);
put('assoc!*!*, 's!:compfn, function s!:comassoc);

symbolic procedure s!:comatsoc(x, env, context);
   if (s!:eval_to_eq_unsafe cadr x or
       s!:eval_to_alist_eq_unsafe caddr x) then <<
       if posn() neq 0 then terpri();
       princ "++++ ATSOC on number upgraded to ASSOC in ";
       prin s!:current_function; princ " : ";
       prin cadr x; princ " "; print caddr x;
       s!:comval('assoc . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('atsoc, 's!:compfn, function s!:comatsoc);

symbolic procedure s!:commember(x, env, context);
   if (s!:eval_to_eq_safe cadr x or s!:eval_to_list_all_eq_safe caddr x) and length x = 3 then
      s!:comval('memq . cdr x, env, context)
   else s!:comcall(x, env, context);

put('member, 's!:compfn, function s!:commember);
put('member!*!*, 's!:compfn, function s!:commember);

symbolic procedure s!:commemq(x, env, context);
   if (s!:eval_to_eq_unsafe cadr x or s!:eval_to_list_some_eq_unsafe caddr x) then <<
       if posn() neq 0 then terpri();
       princ "++++ MEMQ on number upgraded to MEMBER in ";
       prin s!:current_function; princ " : ";
       prin cadr x; princ " "; print caddr x;
       s!:comval('member . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('memq, 's!:compfn, function s!:commemq);

symbolic procedure s!:comdelete(x, env, context);
   if (s!:eval_to_eq_safe cadr x or s!:eval_to_list_all_eq_safe caddr x) and length x = 3 then
      s!:comval('deleq . cdr x, env, context)
   else s!:comcall(x, env, context);

put('delete, 's!:compfn, function s!:comdelete);

symbolic procedure s!:comdeleq(x, env, context);
   if (s!:eval_to_eq_unsafe cadr x or s!:eval_to_list_some_eq_unsafe caddr x) then <<
       if posn() neq 0 then terpri();
       princ "++++ DELEQ on number upgraded to DELETE in ";
       prin s!:current_function; princ " : ";
       prin cadr x; princ " "; print caddr x;
       s!:comval('delete . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('deleq, 's!:compfn, function s!:comdeleq);

!#endif

!#if (not common!-lisp!-mode)

% mapcar etc are compiled specially as a fudge to achieve an effect as
% if proper environment-capture was implemented for the functional
% argument (which I do not support at present). Not done (here) for
% Common Lisp since args to mapcar etc are in the other order.

symbolic procedure s!:commap(fnargs, env, context);
  begin
    scalar carp, fn, fn1, args, var, avar, moveon, l1, r, s, closed;
    fn := car fnargs;
% if the value of a mapping function is not needed I demote from mapcar to
% mapc or from maplist to map.
    if context > 1 then <<
       if fn = 'mapcar then fn := 'mapc
       else if fn = 'maplist then fn := 'map >>;
    if fn = 'mapc or fn = 'mapcar or fn = 'mapcan then carp := t;
    fnargs := cdr fnargs;
    if atom fnargs then error(0,"bad arguments to map function");
    fn1 := cadr fnargs;
    while eqcar(fn1, 'function) or
          (eqcar(fn1, 'quote) and eqcar(cadr fn1, 'lambda)) do <<
       fn1 := cadr fn1;
       closed := t >>;
% if closed is false I will insert FUNCALL since I am invoking a function
% stored in a variable - NB this means that the word FUNCTION becomes
% essential when using mapping operators - this is because I have built
% a 2-Lisp rather than a 1-Lisp.
    args := car fnargs;
    l1 := gensym();
    r := gensym();
    s := gensym();
    var := gensym();
    avar := var;
    if carp then avar := list('car, avar);
% Here if closed is true and fn1 is of the form (lambda (w) ... w ...) where
% the local variable occurs only once in the body, and w is not fluid or
% global (and there had better be no other bindings to wreck scope) then I
% might simplify by doing a textual substitution here rather than a real
% lambda binding. Maybe I should detect such cases in the code that
% compiles the application of lambda expressions?  For now do not bother!
    if closed then fn1 := list(fn1, avar)
    else fn1 := list('funcall, fn1, avar);
    moveon := list('setq, var, list('cdr, var));
    if fn = 'map or fn = 'mapc then fn := sublis(
       list('l1 . l1, 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon),
       '(prog (var)
             (setq var args)
       l1    (cond
                ((not var) (return nil)))
             fn
             moveon
             (go l1)))
    else if fn = 'maplist or fn = 'mapcar then fn := sublis(
       list('l1 . l1, 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon, 'r . r),
       '(prog (var r)
             (setq var args)
       l1    (cond
                ((not var) (return (reversip r))))
             (setq r (cons fn r))
             moveon
             (go l1)))
    else fn := sublis(
       list('l1 . l1, 'l2 . gensym(), 'var . var,
            'fn . fn1, 'args . args, 'moveon . moveon,
            'r . gensym(), 's . gensym()),
       '(prog (var r s)
             (setq var args)
             (setq r (setq s (list nil)))
       l1    (cond
                ((not var) (return (cdr r))))
             (rplacd s fn)
       l2    (cond
                ((not (atom (cdr s))) (setq s (cdr s)) (go l2)))
             moveon
             (go l1)));
    s!:comval(fn, env, context)
  end;

put('map,     's!:compfn, function s!:commap);
put('maplist, 's!:compfn, function s!:commap);
put('mapc,    's!:compfn, function s!:commap);
put('mapcar,  's!:compfn, function s!:commap);
put('mapcon,  's!:compfn, function s!:commap);
put('mapcan,  's!:compfn, function s!:commap);

!#endif

!#if common!-lisp!-mode

% The next few cases are concerned with demoting functions that use
% equal tests into ones that use eq instead

symbolic procedure s!:eval_to_eq_safe x;
   null x or x=t or (atom x and not idp x and eq!-safe x) or
   (eqcar(x, 'quote) and (symbolp cadr x or eq!-safe cadr x));

symbolic procedure s!:list_all_eq_safe u;
  atom u or
  ((symbolp car u or eq!-safe car u) and s!:list_all_eq_safe cdr u);

symbolic procedure s!:eval_to_list_some_eq_unsafe x;
  null x or
  (eqcar(x, 'quote) and s!:list_all_eq_safe cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (s!:eval_to_eq_safe cadr x and
     s!:eval_to_list_some_eq_unsafe ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   s!:eval_to_eq_safe cadr x and
   s!:eval_to_list_some_eq_unsafe caddr x);

symbolic procedure s!:eval_to_car_eq_safe x;
  (eqcar(x, 'cons) or eqcar(x, 'list)) and
  not null cdr x and
  s!:eval_to_eq_safe cadr x;

symbolic procedure s!:alist_eq_safe u;
  atom u or
  (not atom car u and
   (symbolp caar u or eq!-safe caar u) and 
   s!:alist_eq_safe cdr u);

symbolic procedure s!:eval_to_alist_eq_safe x;
  null x or
  (eqcar(x, 'quote) and s!:alist_eq_safe cadr x) or
  (eqcar(x, 'list) and
   (null cdr x or
    (s!:eval_to_car_eq_safe cadr x and
     s!:eval_to_alist_eq_safe ('list . cddr x)))) or
  (eqcar(x, 'cons) and
   s!:eval_to_car_eq_safe cadr x and
   s!:eval_to_alist_eq_safe caddr x);

symbolic procedure s!:comsublis(x, env, context);
   if s!:eval_to_alist_eq_safe cadr x then
      s!:comval('subla . cdr x, env, context)
   else s!:comcall(x, env, context);

put('sublis, 's!:compfn, function s!:comsublis);

symbolic procedure s!:comassoc(x, env, context);
   if length x = 3 then s!:comcall('atsoc . cdr x, env, context)
   else if length x = 5 and 
           cadddr x = '!:test and 
           (cadddr cdr x = '(function equal) or
            cadddr cdr x = '(quote equal) or
            cadddr cdr x = 'equal) then
      s!:comval(list('assoc!*!*, cadr x, caddr x), env, context)
   else s!:comcall(x, env, context);

put('assoc, 's!:compfn, function s!:comassoc);

symbolic procedure s!:comassoc!*!*(x, env, context);
   if s!:eval_to_eq_safe cadr x or s!:eval_to_alist_eq_safe caddr x then
      s!:comval('atsoc . cdr x, env, context)
   else s!:comcall(x, env, context);

put('assoc!*!*, 's!:compfn, function s!:comassoc!*!*);

symbolic procedure s!:commember(x, env, context);
   if length x = 3 then s!:comcall('memq . cdr x, env, context)
   else if length x = 5 and
           cadddr x = '!:test and
           (cadddr cdr x = '(function equal) or
            cadddr cdr x = '(quote equal) or
            cadddr cdr x = 'equal) then
      s!:comval(list('member!*!*, cadr x, caddr x), env, context)
   else if length x = 5 and cadddr x = !:test then begin
      scalar r, g0, g1, g2;
      g0 := gensym(); g1 := gensym(); g2 := gensym();
      r := list('prog, list(g0, g1),
             list('setq, g0, cadr x),
             list('setq, g1, caddr x),
             g2,
             list('cond,
                list(list('null, g1), list('return, nil)),
                list(list('funcall, cadddr cdr x, g0, list('car, g1)),
                     list('return, g1))),
             list('setq, g1, list('cdr, g1)),
             list('go, g2));
      return s!:comval(r, env, context) end
   else s!:comcall(x, env, context);

put('member, 's!:compfn, function s!:commember);

symbolic procedure s!:commember!*!*(x, env, context);
   if s!:eval_to_eq_safe cadr x or s!:eval_to_list_some_eq_unsafe caddr x then <<
       if posn() neq 0 then terpri();
       princ "++++ MEMQ on number upgraded to MEMBER in ";
       prin s!:current_function; princ " : ";
       prin cadr x; princ " "; print caddr x;
       s!:comval('memq . cdr x, env, context) >>
   else s!:comcall(x, env, context);

put('member!*!*, 's!:compfn, function s!:commember!*!*);

!#endif

symbolic procedure s!:nilargs use;
   if null use then t
   else if car use = 'nil or car use = '(quote nil) then s!:nilargs cdr use
   else nil;

symbolic procedure s!:subargs(args, use);
   if null use then t
   else if null args then s!:nilargs use
   else if not (car args = car use) then nil
   else s!:subargs(cdr args, cdr use);

fluid '(!*where_defined!*);

symbolic procedure clear_source_database();
  << !*where_defined!* := mkhash(10, 2, 1.5);
     nil >>;

symbolic procedure load_source_database filename;
  begin
    scalar a, b;
    clear_source_database();
!#if common!-lisp!-mode
    a := open(filename, !:direction, !:input, !:if!-does!-not!-exist, nil);
!#else
    a := open(filename, 'input);
!#endif
    if null a then return nil;
    a := rds a;
    while (b := read()) do
       puthash(car b, !*where_defined!*, cdr b);
    close rds a;
    return nil
  end;

symbolic procedure save_source_database filename;
  begin
    scalar a;
!#if common!-lisp!-mode
    a := open(filename, !:direction, !:output);
!#else
    a := open(filename, 'output);
!#endif
    if null a then return nil;
    a := wrs a;
    for each z in sort(hashcontents !*where_defined!*, function orderp) do <<
       prin z; terpri() >>;
    princ nil; terpri();
    wrs a;
    !*where_defined!* := nil;
    return nil
  end;

symbolic procedure display_source_database();
  begin
    scalar w;
    if null !*where_defined!* then return nil;
    w := hashcontents !*where_defined!*;
    w := sort(w, function orderp);
    terpri();
    for each x in w do <<
       princ car x;
       ttab 40;
       prin cdr x;
       terpri() >>
  end;

% Recursion to Iteration conversions...
%
% The idea of the code here is to map code such as
%
%   (de f (a) (cond
%       (P Q)
%       (t (cons R (f S)))))
% onto
%
%   (de f (a) (prog (v w)
%     lab
%       (cond
%         (P (setq w Q) (return (nreverse v w)))
%         (t (progn (setq v (cons R v))
%                   (setq a S)             % ***
%                   (go lab))))))
%
% [note I invent a 2-arg version of nreverse here... meaning maybe obvious
%  from usage!]
%
% If f has more than 1 arg I may need temporary variables to cope with
% the assignments corresponding to (***).



%
% To Do:
%  .  support for LET and a few more Common Lisp special forms
%     that do not really generate any special difficulties
%  .  treatment of PROG (and hence TAGBODY) and RETURN so that recursion
%     in more complicated functions can be handled gracefully.
%

% Despite the limitations of the code here it seems to work quite well for
% REDUCE and it catches most of the nasty cases. Specifically it left ONE
% function that caused big practical trouble... 
% How well it will cope with Common Lisp code is less clear. I really suspec
% that in its present form it can be confused eg by local special
% declarations or some other Common Lisp feature... some debugging and
% checking is needed.


fluid '(s!:r2i_simple_recurse s!:r2i_cons_recurse);

symbolic procedure s!:r2i(name, args, body);
  begin
    scalar lab, v, b1, s!:r2i_simple_recurse, s!:r2i_cons_recurse;
    lab := gensym();
    v := list gensym();
    b1 := s!:r2i1(name, args, body, lab, v);
    if s!:r2i_cons_recurse then <<
       b1 := list('prog, v, lab, b1);
%      terpri();
%      prettyprint list('de, name, args, b1);
       return b1 >>
    else if s!:r2i_simple_recurse then <<
       v := list gensym();
       b1 := s!:r2i2(name, args, body, lab, v);
       b1 := list('prog, cdr v, lab, b1);
%      terpri();
%      prettyprint list('de, name, args, b1);
       return b1 >>
    else return s!:r2i3(name, args, body, lab, v)
  end;

symbolic procedure s!:r2i1(name, args, body, lab, v);
  if null body or body = '(progn) then list('return, list('nreverse, car v))
  else if eqcar(body, name) and (length cdr body = length args) then <<
     s!:r2i_simple_recurse := t;
     'progn . append(s!:r2isteps(args, cdr body, v), list list('go, lab)) >>
  else if eqcar(body, 'cond) then
     'cond . s!:r2icond(name, args, cdr body, lab, v)
  else if eqcar(body, 'if) then
     'if . s!:r2iif(name, args, cdr body, lab, v)
  else if eqcar(body, 'when) then
     'when . s!:r2iwhen(name, args, cdr body, lab, v)
  else if eqcar(body, 'cons) then
     s!:r2icons(name, args, cadr body, caddr body, lab, v)
  else if eqcar(body, 'progn) or eqcar(body, 'prog2) then
     'progn . s!:r2iprogn(name, args, cdr body, lab, v)
  else if eqcar(body, 'and) then
     s!:r2i1(name, args, s!:r2iand cdr body, lab, v)
  else if eqcar(body, 'or) then
     s!:r2i1(name, args, s!:r2ior cdr body, lab, v)
% Consider support for LET, LET* here
% Think what I can do about PROG/BLOCK/TAGBODY
  else list('return, list('nreverse, car v, body));

symbolic procedure s!:r2iand l;
  if null l then t
  else if null cdr l then car l
  else list('cond, list(car l, s!:r2iand cdr l));

symbolic procedure s!:r2ior l;
  if null l then nil
  else 'cond . for each x in l collect list x;

symbolic procedure s!:r2icond(name, args, b, lab, v);
  if null b then list list(t, list('return, list('nreverse, car v)))
  else if null cdar b then << %  (COND (a) ...)
    if null cdr v then rplacd(v, list gensym());
    list(list('setq, cadr v, caar b),
         list('return, list('nreverse, car v, cadr v))) .
    s!:r2icond(name, args, cdr b, lab, v) >>
  else if eqcar(car b, t) then
    list (t . s!:r2iprogn(name, args, cdar b, lab, v))
  else (caar b . s!:r2iprogn(name, args, cdar b, lab, v)) .
    s!:r2icond(name, args, cdr b, lab, v);

symbolic procedure s!:r2iif(name, args, b, lab, v);
  if null cddr b then list(car b, s!:r2i1(name, args, cadr b, lab, v))
  else list(car b, s!:r2i1(name, args, cadr b, lab, v),
                   s!:r2i1(name, args, caddr b, lab, v));

symbolic procedure s!:r2iwhen(name, args, b, lab, v);
  car b . s!:r2iprogn(name, args, cdr b, lab, v);

symbolic procedure s!:r2iprogn(name, args, b, lab, v);
  if null cdr b then list s!:r2i1(name, args, car b, lab, v)
  else car b . s!:r2iprogn(name, args, cdr b, lab, v);

symbolic procedure s!:r2icons(name, args, a, d, lab, v);
  if eqcar(d, 'cons) then
     s!:r2icons2(name, args, a, cadr d, caddr d, lab, v)
  else if eqcar(d, name) and (length cdr d = length args) then <<
     s!:r2i_cons_recurse := t;
     'progn .
        list('setq, car v, list('cons, a, car v)) .
           append(s!:r2isteps(args, cdr d, v), list list('go, lab)) >>
  else list('return, list('nreverse, car v, list('cons, a, d)));

symbolic procedure s!:r2icons2(name, args, a, ad, dd, lab, v);
  if eqcar(dd, name) and (length cdr dd = length args) then <<
     s!:r2i_cons_recurse := t;
     'progn .
        list('setq, car v, list('cons, a, car v)) .
           list('setq, car v, list('cons, ad, car v)) .
              append(s!:r2isteps(args, cdr dd, v), list list('go, lab)) >>
  else list('return, list('nreverse, car v, list('cons, a, list('cons, ad, dd))));

symbolic procedure s!:r2isteps(vars, vals, v);
  if null vars then
     if null vals then nil
     else error(0, "too many args in recursive call to self")
  else if null vals then
     error(0, "not enough args in recursive call to self")
  else if car vars = car vals then s!:r2isteps(cdr vars, cdr vals, v)
  else if s!:r2i_safestep(car vars, cdr vars, cdr vals) then
     list('setq, car vars, car vals) .
         s!:r2isteps(cdr vars, cdr vals, v)
  else begin
    scalar w;
    if null cdr v then rplacd(v, list gensym());
    v := cdr v;
    w := s!:r2isteps(cdr vars, cdr vals, v);
    return list('setq, car v, car vals) .
       append(w, list list('setq, car vars, car v))
  end;

symbolic procedure s!:r2i_safestep(x, vars, vals);
% true if clobbering x will not hurt anything in vals that has to be computed
  if null vars and null vals then t
  else if s!:r2i_dependson(car vals, x) then nil
  else s!:r2i_safestep(x, cdr vars, cdr vals);

symbolic procedure s!:r2i_dependson(e, x);
  if e=x then t
  else if atom e or eqcar(e, 'quote) then nil
  else if not atom car e then t
  else if flagp(car e, 's!:r2i_safe) then s!:r2i_list_dependson(cdr e, x)
  else if fluidp x or globalp x then t
  else if flagp(car e, 's!:r2i_unsafe) or macro!-function car e then t
  else s!:r2i_list_dependson(cdr e, x);

% the things in the following list never refer to global (or fluid) variables
% so I ONLY need to check their args

flag('(car cdr caar cadr cdar cddr
       caaar caadr cadar caddr cdaar cdadr cddar cdddr
       cons ncons rcons acons list list2 list3 list!*
       add1 sub1 plus plus2 times times2 difference minus quotient
       append reverse nreverse null not assoc atsoc member memq
       subst sublis subla pair prog1 prog2 progn), 's!:r2i_safe);

% The things that follow may have odd-format argument lists and so can not
% be processed in the usual simple way. I lock out AND and OR here because
% of the order-of-evaluation issues associated with them.
% WARNING other funny-format things might cause wreckage...

flag('(cond if when case de defun dm defmacro
       prog let let!* flet and or), 's!:r2i_unsafe);

symbolic procedure s!:r2i_list_dependson(l, x);
  if null l then nil
  else if s!:r2i_dependson(car l, x) then t
  else s!:r2i_list_dependson(cdr l, x);


symbolic procedure s!:r2i2(name, args, body, lab, v);
  if null body or body = '(progn) then list('return, nil)
  else if eqcar(body, name) and (length cdr body = length args) then <<
     'progn . append(s!:r2isteps(args, cdr body, v), list list('go, lab)) >>
  else if eqcar(body, 'cond) then
     'cond . s!:r2i2cond(name, args, cdr body, lab, v)
  else if eqcar(body, 'if) then
     'if . s!:r2i2if(name, args, cdr body, lab, v)
  else if eqcar(body, 'when) then
     'when . s!:r2i2when(name, args, cdr body, lab, v)
  else if eqcar(body, 'progn) or eqcar(body, 'prog2) then
     'progn . s!:r2i2progn(name, args, cdr body, lab, v)
  else if eqcar(body, 'and) then
     s!:r2i2(name, args, s!:r2iand cdr body, lab, v)
  else if eqcar(body, 'or) then
     s!:r2i2(name, args, s!:r2ior cdr body, lab, v)
  else list('return, body);

symbolic procedure s!:r2i2cond(name, args, b, lab, v);
  if null b then list list(t, list('return, nil))
  else if null cdar b then << %  (COND (a) ...)
    if null cdr v then rplacd(v, list gensym());
    list(list('setq, cadr v, caar b),
         list('return, cadr v)) .
    s!:r2i2cond(name, args, cdr b, lab, v) >>
  else if eqcar(car b, t) then
    list (t . s!:r2i2progn(name, args, cdar b, lab, v))
  else (caar b . s!:r2i2progn(name, args, cdar b, lab, v)) .
    s!:r2i2cond(name, args, cdr b, lab, v);

symbolic procedure s!:r2i2if(name, args, b, lab, v);
  if null cddr b then list(car b, s!:r2i2(name, args, cadr b, lab, v))
  else list(car b, s!:r2i2(name, args, cadr b, lab, v),
                   s!:r2i2(name, args, caddr b, lab, v));

symbolic procedure s!:r2i2when(name, args, b, lab, v);
  car b . s!:r2i2progn(name, args, cdr b, lab, v);

symbolic procedure s!:r2i2progn(name, args, b, lab, v);
  if null cdr b then list s!:r2i2(name, args, car b, lab, v)
  else car b . s!:r2i2progn(name, args, cdr b, lab, v);

% This version looks for a VERY rigid template
%    name
%    args
%    (cond (P Q)
%          (t (g R (name ...))))
% or
%    (if P Q (g R (name ...)))

symbolic procedure s!:r2i3(name, args, body, lab, v);
  begin
    scalar v, v1, v2, lab1, lab2, lab3, w, P, Q, g, R;
    if s!:any_fluid args then return body;
    if eqcar(body, 'cond) then <<
       if not (w := cdr body) then return body;
       P := car w; w := cdr w;
       if null P then return body;
       Q := cdr P; P := car P;
       if null Q or cdr Q then return body;
       Q := car Q;
       if null w or cdr w then return body;
       w := car w;
       if not eqcar(w, t) then return body;
       w := cdr w;
       if not w or cdr w then return body;
       w := car w >>
    else if eqcar(body, 'if) then <<
       w := cdr body;
       P := car w; w := cdr w;      % predicate
       Q := car w; w := cdr w;      % base-case result
       if null w then return body;
       w := car w >>                % recursion-case result
    else return body;
% recursion case must be of form (g R w)
    if atom w or atom cdr w or atom cddr w or cdddr w then return body;
    g := car w;
    R := cadr w;
    w := caddr w;
    if not atom g then return body; % eg a lambda-expression
    if member(g, '(and or progn prog1 prog2 cond if when)) then return body;
    if not eqcar(w, name) then return body;
    w := cdr w; % new args for the call
    if not (length w = length args) then return body;
%   terpri();
%   printc "[[[[[[[";
%   prettyprint list('de, name, args, body);   % just print it for now
%   printc "=======";
    v1 := gensym();
    v2 := gensym();
    v := list v2;
    lab1 := gensym();
    lab2 := gensym();
    lab3 := gensym();
    w := s!:r2isteps(args, w, v);  % has side-effects - must be in separate stmt.
    w := list('prog, v1 . v,
      lab1,
      list('cond, list(P, list('go, lab2))),
      list('setq, v1, list('cons, R, v1)),
      'progn . w,
      list('go, lab1),
      lab2,
      list('setq, v2, Q),
      lab3,
      list('cond, list(list('null, v1), list('return, v2))),
      list('setq, v2, list(g, list('car, v1), v2)),
      list('setq, v1, list('cdr, v1)),
      list('go, lab3));
%   prettyprint list('de, name, args, w);   % just print it for now
%   printc "]]]]]]]";
    return w
  end;

symbolic procedure s!:any_fluid l;
  if null l then nil
  else if fluidp car l then t
  else s!:any_fluid cdr l;

% s!:compile1 directs the compilation of a single function, and bind all the
% major fluids used by the compilation process

symbolic procedure s!:compile1(name, args, body, s!:lexical_env);
  begin
    scalar w, aargs, oargs, oinit, restarg, svars, nargs, nopts, env, fluids,
           s!:current_function, s!:current_label, s!:current_block,
           s!:current_size, s!:current_procedure, s!:current_exitlab,
           s!:current_proglabels, s!:other_defs, local_decs, s!:has_closure,
           s!:local_macros, s!:recent_literals, s!:a_reg_values, w1, w2,
           s!:current_count, s!:env_alist, checksum;
% If there is a lexical environment present I will set the checksum to 0
% and thus prevent any native compilation (for now at least)
    if s!:lexical_env then checksum := 0
    else checksum := md60 (name . args . body);
    s!:current_function := name;
    s!:current_count := 0;
    if !*where_defined!* then <<
!#if common!-lisp!-mode
        w := symbol!-package name;
        if w then w := list(package!-name w, symbol!-name name);
!#else
        w := name;
!#endif
        puthash(w, !*where_defined!*, where!-was!-that()) >>;
    body := s!:find_local_decs(body, nil);
    local_decs := car body; body := cdr body;
    if atom body then body := nil
    else if null cdr body then body := car body
    else body := 'progn . body;
    nargs := nopts := 0;
    while args and
          not eqcar(args, '!&optional) and
          not eqcar(args, '!&rest) do <<
       if car args = '!&key or car args = '!&aux then error(0, "&key/&aux");
       aargs := car args . aargs;
       nargs := nargs + 1;
       args := cdr args >>;
    if eqcar(args, '!&optional) then <<
       args := cdr args;
       while args and not eqcar(args, '!&rest) do <<
          if car args = '!&key or car args = '!&aux then error(0, "&key/&aux");
          w := car args;
% Things that are written as (v) or (v nil) might as well have
% been treated as just v. I use a WHILE loop so that silly people
% who write (((((v))))) get their code reduced to just v.
          while not atom w and
             (atom cdr w or cdr w = '(nil)) do w := car w;
          args := cdr args;
          oargs := w . oargs;
          nopts := nopts + 1;
          if atom w then aargs := w . aargs
          else <<
             oinit := t;
             aargs := car w . aargs;
             if not atom cddr w then svars := caddr w . svars >> >> >>;
    if eqcar(args, '!&rest) then <<
       w := cadr args;
       aargs := w . aargs;
       restarg := w;
       args := cddr args;
       if args then error(0, "&rest arg not at end") >>;
% NB I have not allowed for &aux or &key - I take the view that
% they will be expanded out by a DEFUN macro.
    args := reverse aargs;
% The variable args is now a map of how my arguments will actually be
% presented to me on the stack.
    oargs := reverse oargs;   % Optional args, possibly with initforms
                              % oinit is true if there are initforms.
% Now I will TRY to be kind.  If any variable mentioned in the argument list
% is a GLOBAL I will convert it to FLUID, but tell the user that that
% has happened.
    for each v in append(svars, args) do <<
       if globalp v then <<
           if !*pwrds then <<
              if posn() neq 0 then terpri();
              princ "+++++ global ";
              prin v;
              princ " converted to fluid";
              terpri() >>;
           unglobal list v;
           fluid list v >> >>;
    if oinit then
       return s!:compile2(name, nargs, nopts,
                          args, oargs, restarg, body, local_decs,
                          checksum);
    w := nil;
    for each v in args do w := s!:instate_local_decs(v, local_decs, w);
% I will not even attempt recursion removal from functions that have
% &optional or &rest (or &keyword) arguments. But I will do the removal
% at a stage when local fluid declarations (if any) are in force.
    if !*r2i and null oargs and null restarg then
       body := s!:r2i(name, args, body);
    for each v on args do <<
       if fluidp car v then begin
          scalar g;
          g := gensym();
          fluids := (car v . g) . fluids;
          rplaca(v, g) end >>;
% In the case that the variables a and b are fluid, I map
%  (lambda (a b) X) onto
%  (lambda (g1 g2) (prog (a b) (setq a g1) (setq b g2) (return X)))
% and then let the compilation of the PROG deal with the fluid bindings.
% [I worry a bit about adding an extra PROG here, since it can mean that
% RETURN becomes valid when it used not to...].  Note that since the
% variable g1, g2 ... are all new gensyms none of them can be locally special
% so at least I do not have muddle because of that.
    if fluids then <<
       body := list list('return, body);
       for each v in fluids do
          body := list('setq, car v, cdr v) . body;
       body := 'prog .
         (for each v in fluids collect car v) . body >>;
% If I am compiling in-store I will common up literals only if they are EQL.
% However if s!:faslmod_name is set then I am compiling to a file, and in
% that case I will dare common things up if they are EQUAL.  The reasoning
% behind this is that going via a file necesarily loses EQ-ness on some
% things, so one can afford to do so while for in-store compilation it
% could make sense to preserve sharing (or not) between literal lists in
% the code being compiled.
%   env := mkhash(10, (if s!:faslmod_name then 2 else 1), 1.5) .
%          reverse args;
%
% On further thought maybe code that has been constructed so its behaviour
% depends on the level of sharing of literals is pretty bad and also in fact
% uncommon, and making in-core and FASL compilation behave the same way is
% a good idea, so I am altering this (May 2010) to use an EQUAL check always.
% This should also mean that string literals end up shared, and that the
% order items end up in a literal vector does not depend on memory addresses
% and hence becomes consistent from platform to platform and run to run.
%
    env := mkhash(10, 2, 1.5) .
           reverse args;
    puthash(name, car env, 10000000 . nil);
    w := s!:residual_local_decs(local_decs, w);
    s!:start_procedure(nargs, nopts, restarg);
% Now, so that I will be able to take special action on cases which would
% compile into nothing more than a tail-call operation, I do a bit of
% early expansion. If this does not reveal that I have a definition
% of the form (de f1 (a b c ...) (f2 a b c)) then I ignore what I have done
% to let comval handle it in the normal way...
    w1 := body;
more:
    if atom w1 then nil
    else if car w1 = 'block and length w1 = 3 then << 
       w1 := caddr w1; go to more >>
    else if car w1 = 'progn and length w1 = 2 then <<
       w1 := cadr w1; go to more >>
    else if atom (w2 := car w1) and (w2 := get(w2, 's!:newname)) then <<
       w1 := w2 . cdr w1; go to more >>
    else if atom (w2 := car w1) and (w2 := macro!-function w2) then <<
        w1 := funcall(w2, w1); go to more >>;
    if not ((w2 := s!:improve w1) = w1) then << w1 := w2; go to more >>;

    if not atom w1 and atom car w1 and not special!-form!-p car w1 and 
       s!:subargs(args, cdr w1) and
% Just for the moment I will only enable this special case code for
% instances where the new function has less than 3 args and the one it calls
% does not need nil-padding.
       nargs <= 3 and nopts = 0 and not restarg and
       length cdr w1 <= nargs then <<
        s!:cancel_local_decs w;
        if restarg then nopts := nopts + 512;
% The argument count info gets bits added in here to show how many args
% must be passed on. If this is larger than the number originally provided
% it means that nils should be padded onto the end.
        nopts := nopts + 1024*length w1;
        nargs := nargs + 256*nopts;
        if !*pwrds then <<
            if posn() neq 0 then terpri();
            princ "+++ "; prin name; princ " compiled as link to ";
            princ car w1; terpri() >>;
        return (name . nargs . nil . car w1) . s!:other_defs >>;
    s!:comval(body, env, 0);
    s!:cancel_local_decs w;
% This returns a list of values suitable for handing to symbol-set-definition
    if restarg then nopts := nopts + 512;
    nargs := nargs + 256*nopts;
    return (name . nargs . s!:endprocedure(name, env, checksum)) . s!:other_defs;
  end;





end;


symbolic procedure s!:compile2(name, nargs, nopts,
                               args, oargs, restarg, body, local_decs,
                               checksum);
% If I have any &optional args that have initforms then I will generate
% code in a very cautious, slow and pessimistic manner - because I need
% to be able to evaluate the initforms in an environment where the
% previously mentioned arguments have all been bound, but subsequent
% ones have not.
  begin
    scalar fluids, env, penv, g, v, init, atend, w;
% I start off with an environment that shows how deep my stack is, but
% which does not give any names to the locations on it.
    for each v in args do <<
       env := 0 . env;
       penv := env . penv >>;
%   env := mkhash(10, (if s!:faslmod_name then 2 else 1), 1.5) . env;
    env := mkhash(10, 2, 1.5) . env;
    puthash(name, car env, 10000000 . nil);
    penv := reversip penv;
% I make the list of optional args as long as the complete arg list - with
% zero entries for things that are not optional.
    if restarg then oargs := append(oargs, '(0));
    for i := 1:nargs do oargs := 0 . oargs;
    s!:start_procedure(nargs, nopts, restarg);
    while args do <<
       v := car args;
       init := car oargs;
       if init = 0 then <<
          w := s!:instate_local_decs(v, local_decs, w);
          if fluidp v then <<
              g := gensym();
              rplaca(car penv, g);
              s!:outopcode1lit('FREEBIND, s!:vecof list v, env);
              rplacd(env, 3 . 0 . 0 . cdr env);
              atend := 'FREERSTR . atend;
              s!:comval(list('setq, v, g), env, 2) >>
          else rplaca(car penv, v) >>
       else begin
          scalar ival, sp, l1, l2;
          if not atom init then <<
             init := cdr init;
             ival := car init;
             if not atom cdr init then sp := cadr init >>;
          l1 := gensym();
          g := gensym();
          rplaca(car penv, g);
          if null ival and null sp then
             s!:comval(list('setq, g, list('spid!-to!-nil, g)), env, 1)
          else <<
             s!:jumpif(nil, list('is!-spid, g), env, l1);
% Here is code for when an initform must be activated.
             s!:comval(list('setq, g, ival), env, 1);
             if sp then <<
                if fluidp sp then <<
                   s!:outopcode1lit('FREEBIND, s!:vecof list sp, env);
                   s!:outjump('JUMP, l2 := gensym());
                   s!:set_label l1;
                   s!:outopcode1lit('FREEBIND, s!:vecof list sp, env);
                   rplacd(env, 3 . 0 . 0 . cdr env);
                   s!:comval(list('setq, sp, t), env, 1);
                   s!:set_label l2;
                   atend := 'FREERSTR . atend >>
                else <<
                   s!:outopcode0('PUSHNIL, '(PUSHNIL));
                   s!:outjump('JUMP, l2 := gensym());
                   s!:set_label l1;
                   s!:loadliteral(t, env);
                   s!:outopcode0('PUSH, '(PUSH));
                   s!:set_label l2;
                   rplacd(env, sp . cdr env);
                   atend := 'LOSE . atend >> >>
             else s!:set_label l1 >>;
          w := s!:instate_local_decs(v, local_decs, w);
          if fluidp v then <<
             s!:outopcode1lit('FREEBIND, s!:vecof list v, env);
             rplacd(env, 3 . 0 . 0 . cdr env);
             s!:comval(list('setq, v, g), env, 1);
             atend := 'FREERSTR . atend >>
          else rplaca(car penv, v)
          end;
       args := cdr args;
       oargs := cdr oargs;
       penv := cdr penv >>;
    w := s!:residual_local_decs(local_decs, w);
    s!:comval(body, env, 0);
    while atend do <<
       s!:outopcode0(car atend, list car atend);
       atend := cdr atend >>;
    s!:cancel_local_decs w;
    nopts := nopts + 256;     % Always have complex &optional here
    if restarg then nopts := nopts + 512;
    nargs := nargs + 256*nopts;
    return (name . nargs . s!:endprocedure(name, env, checksum)) .
           s!:other_defs;
  end;


% compile-all may be invoked at any time to ensure that everything that can be
% has been compiled

!#if common!-lisp!-mode

symbolic procedure compile!-all;
% Bootstrapping issues mean that I can not easily use do-all-symbols() here
  for each p in list!-all!-packages() do begin
    scalar !*package!*;
    !*package!* := find!-package p;
    for each x in oblist() do
      begin scalar w;
        w := getd x;
        if (eqcar(w, 'expr) or eqcar(w, 'macro)) and
            eqcar(cdr w, 'lambda) then <<
           princ "Compile: "; prin x; terpri();
           errorset(list('compile, mkquote list x), t, t) >> end end;

!#else

symbolic procedure compile!-all;
   for each x in oblist() do begin
      scalar w;
      w := getd x;
      if (eqcar(w, 'expr) or eqcar(w, 'macro)) and
          eqcar(cdr w, 'lambda) then <<
         princ "Compile: "; prin x; terpri();
         errorset(list('compile, mkquote list x), t, t) >> end;

!#endif

% Support for a FASL mechanism, styled after that which I expect existing
% Standard Lisp applications to require.

% The 'eval and 'ignore flags are to help the RLISP interface to the
% fasl mechanism

flag('(rds deflist flag fluid global
       remprop remflag unfluid
       unglobal dm defmacro carcheck
       faslend c_end), 'eval);

flag('(rds), 'ignore);

fluid '(!*backtrace);

symbolic procedure s!:fasl_supervisor;
  begin
    scalar u, w, !*echo;
top:u := errorset('(read), t, !*backtrace);
    if atom u then return;      % failed, or maybe EOF
    u := car u;
    if u = !$eof!$ then return; % end of file
    if not atom u then u := macroexpand u; % In case it expands into (DE ...)
    if atom u then go to top
% the apply('faslend, nil) is here because faslend has a "stat" property
% and so it will mis-parse if I just write "faslend()".  Yuk.
    else if eqcar(u, 'faslend) then return apply('faslend, nil)
!#if common!-lisp!-mode
    else if eqcar(u, 'load) then << <<
       w := open(u := eval cadr u, !:direction, !:input,
                                   !:if!-does!-not!-exist, nil);
!#else
    else if eqcar(u, 'rdf) then <<
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          terpri();
          princ "Reading file "; prin u; terpri();
          w := rds w;
          s!:fasl_supervisor();
          princ "End of file "; prin u; terpri();
          close rds w >>
       else << princ "Failed to open file "; prin u; terpri() >> >>
!#if common!-lisp!-mode
       >> where !*package!* = !*package!*
!#endif
    else s!:fslout0 u;
    go to top
  end;

symbolic procedure s!:fslout0 u;
   s!:fslout1(u, nil);

symbolic procedure s!:fslout1(u, loadonly);
  begin
    scalar w;
    if not atom u then u := macroexpand u;
    if atom u then return nil
    else if eqcar(u, 'progn) then <<
       for each v in cdr u do s!:fslout1(v, loadonly);
       return >>
    else if eqcar(u, 'eval!-when) then return begin
      w := cadr u;
      u := 'progn . cddr u;
      if memq('compile, w) and not loadonly then eval u;
      if memq('load, w) then s!:fslout1(u, t);
      return nil end
% When called from REDUCE the treatment of things flagged as EVAL here
% will end up leading to them getting evaluated twice.  Often this will
% not matter - a case where I have had to be careful is in (faslend) which
% can thus be called twice: the second call must be ignored.
    else if flagp(car u, 'eval) or
% The special treatment here is so that (setq x (carcheck 0)) will get
% picked up as needing compile-time evaluation.
          (car u = 'setq and not atom caddr u and flagp(caaddr u, 'eval)) then
       if not loadonly then errorset(u, t, !*backtrace);
!#if common!-lisp!-mode
    if eqcar(u, 'load) then << begin
       w := open(u := eval cadr u, !:direction, !:input,
                                   !:if!-does!-not!-exist, nil);
!#else
    if eqcar(u, 'rdf) then begin
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          princ "Reading file "; prin u; terpri();
          w := rds w;
          s!:fasl_supervisor();
          princ "End of file "; prin u; terpri();
          close rds w
       >>
       else << princ "Failed to open file "; prin u; terpri() >> end
!#if common!-lisp!-mode
       >> where !*package!* = !*package!*
!#endif
    else if !*nocompile then <<  % Funny option not for general use!
       if not eqcar(u, 'faslend) and
          not eqcar(u, 'carcheck) then write!-module u >>
% If I have a regular function definition, ie NOT a macro, and if it
% does not appear to use any of the Lisp features that my native-mode
% compiler does not support then I will turn pass it through for
% further work.
    else if eqcar(u, 'de) or eqcar(u, 'defun) then <<
% For now I will just DISABLE native compilation for the win64 case where
% for a variety of reasons I have not got it sorted out.
        if !*native_code and
           (not memq('win64, lispsystem!*)) then <<
           if c!:valid_fndef(caddr u, cdddr u) then begin
               scalar pending_functions, u1;
               c!:ccmpout1a u;
               while pending_functions do <<
                  u1 := car pending_functions;
                  pending_functions := cdr pending_functions;
                  s!:fslout0 u1 >>
           end
           else <<
              princ "+++ ";
              prin cadr u;
              printc " can not be compiled into native code" >> >>;
        u := cdr u;
        if (w := get(car u, 'c!-version)) and
            w = md60 (car u . cadr u . s!:fully_macroexpand_list cddr u) then <<
            princ "+++ "; prin car u;
            printc " not compiled (C version available)";
            write!-module list('restore!-c!-code, mkquote car u) >>
        else if flagp(car u, 'lose) then <<
            princ "+++ "; prin car u;
            printc " not compiled (LOSE flag)" >>
        else <<
            if w := get(car u, 'c!-version) then <<
                princ "+++ "; prin car u;
                princ " reports C version with checksum ";
                print w;
                print "+++ differing from this version:";
                w := car u . cadr u . s!:fully_macroexpand_list cddr u;
                princ "::: "; prettyprint w;
                princ "+++ which has checksum "; print md60 w >>;
            for each p in s!:compile1(car u, cadr u, cddr u, nil) do 
                s!:fslout2(p, u) >> >>
    else if eqcar(u, 'dm) or eqcar(u, 'defmacro) then begin
        scalar g;
        g := hashtagged!-name(cadr u, cddr u);
        u := cdr u;
% At present (and maybe for ever?) macros can not be compiled into C.
%
%       if (w := get(car u, 'c!-version)) and
%           md60 u = w then <<
%           princ "+++ "; prin car u;
%           printc " not compiled (C version available flag)";
%           return nil >>
%       else 
        if flagp(car u, 'lose) then <<
            princ "+++ "; prin car u;
            printc " not compiled (LOSE flag)";
            return nil >>;
        w := cadr u;
        if w and null cdr w then w := car w . '!&optional . gensym() . nil;
        for each p in s!:compile1(g, w, cddr u, nil) do s!:fslout2(p, u);
        write!-module list('dm, car u, '(u !&optional e), list(g, 'u, 'e))
      end    
    else if eqcar(u, 'putd) then begin
% If people put (putd 'name 'expr '(lambda ...)) in their file I will
% expand it out as if it had been a (de name ...) [similarly for macros].
      scalar a1, a2, a3;
      a1 := cadr u; a2 := caddr u; a3 := cadddr u;
      if eqcar(a1, 'quote) and
         (a2 = '(quote expr) or a2 = '(quote macro)) and
         (eqcar(a3, 'quote) or eqcar(a3, 'function)) and
         eqcar(cadr a3, 'lambda) then <<
         a1 := cadr a1; a2 := cadr a2; a3 := cadr a3;
         u := (if a2 = 'expr then 'de else 'dm) . a1 . cdr a3;
% More complicated uses of PUTD may defeat the C-version hack...
         s!:fslout1(u, loadonly) >>
      else write!-module u end
    else if not eqcar(u, 'faslend) and
            not eqcar(u, 'carcheck) then write!-module u
  end;

symbolic procedure s!:fslout2(p, u);
  begin
    scalar name, nargs, code, env, w;
    name := car p;
    nargs := cadr p;
    code := caddr p;
    env := cdddr p;
    if !*savedef and name = car u then <<
% I associate the saved definition with the top-level function
% that is being defined, and ignore any embedded lambda expressions
        define!-in!-module(-1);    % savedef marker
        write!-module('lambda . cadr u . s!:fully_macroexpand_list cddr u) >>;
% If the FASL file format tail-call definitions are represented by giving the
% number of args in the thing to chain to as an integer where otherwise
% a vector of bytecodes would be provided.
    w := irightshift(nargs, 18);
    nargs := logand(nargs, 262143); % 0x3ffff
    if not (w = 0) then code := w - 1;
    define!-in!-module nargs;
    write!-module name;
    write!-module code;
    write!-module env
  end;

remprop('faslend, 'stat);

symbolic procedure faslend;
  begin
    scalar copysrc, copydest;
    if null s!:faslmod_name then return nil;
    princ "Completed FASL files for ";
    print car s!:faslmod_name;
    if !*native_code and
       (not memq('win64, lispsystem!*)) then begin
        scalar cmnd, w, w1, obj, deff;
        w := C!-end1 nil;
        close C_file;
        cmnd := append(explodec s!:native_file, '(!")); 
% I will need to review the tests on "win32" here if am ever to stand a chance
% of making a win64 system build native code in this way. At present the fact
% that for win64 I tend to cross-compile would make building the DLLs there
% especially tricky, so I am not going to worry too much about that just yet.
        if 'win32 memq lispsystem!* then obj := "dll"
        else obj := "so";
        obj := tmpnam obj;
% NB worry re win64. There are at least two things to fuss about for the
% win64 case
% (a) at present I can only cross-build for win64, and at present I have
%     not set up any scheme that lets me do this native compilation in a
%     cross-build style.
% (b) the ".def" file has to be different in the Microsoft C/win64 build, and
%     I have to pass an export library "reduce.lib" to the compilation rather
%     than the list of imports mentioned in the .def file here.
% (c) I have not quite sorted out and stabilised MSVC vs Mingw-w64...
        if 'win32 memq lispsystem!* then begin
           scalar nn;
           nn := car s!:faslmod_name;
% The name-conversion here had better match one done when I actually
% created the C code... The issue is that module names that have a "-"
% in them give trouble since "-" is not a good constituent for a
% C name. So I map it onto "_".
           nn := list!-to!-string
               (for each c in explodec nn collect
                   if c = '!- then '!_ else c);
           deff := tmpnam "def";
           w1 := open(deff, 'output);
           w1 := wrs w1;
           princ "LIBRARY "; princ car s!:faslmod_name; printc ".dll";
           printc "EXPORTS";
           printc " init";
           princ " "; princ nn; printc "_setup";
% If I build using msvc (eg the cross-build for Windows-64) I must NOT
% have IMPORTS definitions here but instead I must include reduce.lib as
% input to the compilation. But I will not support this on win64 until
% I sort out how to work around that. Or until I really agree that I am using
% MinGW-w64.
           printc "IMPORTS";
           print!-imports();
           close wrs w1;
           cmnd := append(explodec deff, '!  . cmnd)
        end;
        cmnd := append(explodec obj, '!  . cmnd);
        cmnd := append(explodec " -o ", cmnd);
        for each x in reverse cdr assoc('compiler!-command, lispsystem!*) do
           cmnd :=append(explodec x, '!  . cmnd);
        cmnd := compress ('!" . cmnd);
% As a debugging and confidence-building feature I will print the command
% that is to be obeyed before I obey it. One issue on Windows systems is that
% the C compiler is liable to be a "console application" and so if I launch
% it normally it will create itself a console. So I have a special call
% that executes commands "quietly" to avoid a messy black window popping
% up as I run the compiler.
        print cmnd;
        if not zerop silent!-system cmnd then <<
% I will always leave the C code around in the temp directory in this case,
% since debugging may be in order.
           princ "+++ C compilation for ";
           prin car s!:faslmod_name;
           printc " failed" >>
        else <<
           if !*strip_native then <<
              cmnd := compress ('!" . append(explodec "strip ",
                                          append(explodec obj, '(!"))));
              print cmnd;
              silent!-system cmnd >>;
% Once I have done the compilation I can delete the .c and .def files
% Now copy <obj> into the image file keyed by the linker attribute from
% lispsystem!*. Rather than do that right now I will do if after I have
% closed the main FAST output file that I am generating (so I only try
% to have one such active at once). If the module was called xxx and the
% machine architecture is yyy I will use the name xxx/yyy.
           copysrc := obj;
           copydest := list!-to!-string append(explodec car s!:faslmod_name,
                            '!. . explodec cdr assoc('linker, lispsystem!*));
           if not !*save_native then <<
              delete!-file s!:native_file;
              if 'win32 memq lispsystem!* then delete!-file deff >>;
% Write an entry at the end of the module to instate the compiled code
% that has just been generated (if possible).
           write!-module
              list('instate!-c!-code,
                   mkquote car s!:faslmod_name,
                   mkquote w) >>
    end;
    start!-module nil;
    if copysrc then <<  % Copy object code into place
        copy!-native(copysrc, copydest);
        if not !*save_native then delete!-file copysrc >>;
    dfprint!* := s!:dfprintsave;
    !*defn := nil;
    !*comp := cdr s!:faslmod_name;
    s!:faslmod_name := nil;
    return nil
  end;

put('faslend, 'stat, 'endstat);

symbolic procedure s!:file s;
% Keep just the filename part of a name. Eg /a/b/c.d -> c.d
  begin
    scalar r;
    s := reverse explodec s;
    while s and not (eqcar(s, '!/) or eqcar(s, '!\)) do <<
       r := car s . r;
       s := cdr s >>;
    return list!-to!-string r
  end;

symbolic procedure s!:trim!.c s;
% remove a suffix ".c" if present, eg xxx.c -> xxx
  begin
    scalar r;
    s := reverse explodec s;
    if eqcar(s, 'c) then <<
        s := cdr s;
        if eqcar(s, '!.) then s := cdr s >>;
    return list!-to!-string reverse s
  end;

symbolic procedure s!:dir s;
% Keep just the directory name of a file. Eg /a/b/c.d -> /a/b
  begin
    s := reverse explodec s;
    while s and not (eqcar(s, '!/) or eqcar(s, '!\)) do s := cdr s;
    if s then s := cdr s;
    if null s then return "."
    else return list!-to!-string reverse s
  end;

symbolic procedure faslout u;
  begin
    terpri();
    princ "FASLOUT ";
    prin u; princ ": IN files;  or type in expressions"; terpri();
    princ "When all done, execute FASLEND;"; terpri();
% I permit the argument to be either a name, or a list of one item
% that is a name.  The idea here is that when called from RLISP it is
% most convenient for the call to map onto (faslout '(xxx)), while direct
% use from Lisp favours (faslout 'xxx)
    if not atom u then u := car u;
    if not start!-module u then <<
       if posn() neq 0 then terpri();
       princ "+++ Failed to open FASL output file"; terpri();
       return nil >>;
    if !*native_code and
       (not memq('win64, lispsystem!*)) then <<
%       if not getd 'c!:ccompilestart then load!-module "ccomp";
        s!:native_file := tmpnam "c";
        c!:ccompilestart(s!:trim!.c s!:file s!:native_file, 
                         u,
                         s!:dir s!:native_file,
                         t) >>;
    s!:faslmod_name := u . !*comp;
    s!:dfprintsave := dfprint!*;
    dfprint!* := 's!:fslout0;
    !*defn := t;
    !*comp := nil;
    if getd 'begin then return nil;
    s!:fasl_supervisor();
  end;

put('faslout, 'stat, 'rlis);

symbolic procedure s!:c_supervisor;
  begin
    scalar u, w, !*echo;
top:u := errorset('(read), t, !*backtrace);
    if atom u then return;      % failed, or maybe EOF
    u := car u;
    if u = !$eof!$ then return; % end of file
    if not atom u then u := macroexpand u; % In case it expands into (DE ...)
    if atom u then go to top
% the apply('c_end, nil) is here because c_end has a "stat" property
% and so it will mis-parse if I just write "c_end()".  Yuk.
    else if eqcar(u, 'c_end) then return apply('c_end, nil)
!#if common!-lisp!-mode
    else if eqcar(u, 'load) then << <<
       w := open(u := eval cadr u, !:direction, !:input,
                                   !:if!-does!-not!-exist, nil);
!#else
    else if eqcar(u, 'rdf) then <<
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          terpri();
          princ "Reading file "; prin u; terpri();
          w := rds w;
          s!:c_supervisor();
          princ "End of file "; prin u; terpri();
          close rds w
       >>
       else << princ "Failed to open file "; prin u; terpri() >> >>
!#if common!-lisp!-mode
       >> where !*package!* = !*package!*
!#endif
    else s!:cout0 u;
    go to top
  end;

symbolic procedure s!:cout0 u;
   s!:cout1(u, nil);

symbolic procedure s!:cout1(u, loadonly);
  begin
    scalar s!:into_c;
    s!:into_c := t;
    if not atom u then u := macroexpand u;
    if atom u then return nil
    else if eqcar(u, 'progn) then <<
       for each v in cdr u do s!:cout1(v, loadonly);
       return >>
    else if eqcar(u, 'eval!-when) then return begin
      scalar w;
      w := cadr u;
      u := 'progn . cddr u;
      if memq('compile, w) and not loadonly then eval u;
      if memq('load, w) then s!:cout1(u, t);
      return nil end
% When called from REDUCE the treatment of things flagged as EVAL here
% will end up leading to them getting evaluated twice.  Often this will
% not matter - a case where I have had to be careful is in (c_end) which
% can thus be called twice: the second call must be ignored.
    else if flagp(car u, 'eval) or
% The special treatment here is so that (setq x (carcheck 0)) will get
% picked up as needing compile-time evaluation.
          (car u = 'setq and not atom caddr u and flagp(caaddr u, 'eval)) then
       if not loadonly then errorset(u, t, !*backtrace);
!#if common!-lisp!-mode
    if eqcar(u, 'load) then << begin
       scalar w;
       w := open(u := eval cadr u, !:direction, !:input,
                                   !:if!-does!-not!-exist, nil);
!#else
    if eqcar(u, 'rdf) then begin
       scalar w;
       w := open(u := eval cadr u, 'input);
!#endif
       if w then <<
          princ "Reading file "; prin u; terpri();
          w := rds w;
          s!:c_supervisor();
          princ "End of file "; prin u; terpri();
          close rds w
       >>
       else << princ "Failed to open file "; prin u; terpri() >> end
!#if common!-lisp!-mode
       >> where !*package!* = !*package!*
!#endif
    else if eqcar(u, 'de) or eqcar(u, 'defun) then begin
        scalar w;
        u := cdr u;
        w := s!:compile1(car u, cadr u, cddr u, nil);
        for each p in w do s!:cgen(car p, cadr p, caddr p, cdddr p)
      end
    else if eqcar(u, 'dm) or eqcar(u, 'defmacro) then begin
        scalar w, g;
        g := hashtagged!-name(cadr u, cddr u);
        u := cdr u;
        w := cadr u;  % List of bound vars. Either (u) or (u &optional e) (?)
        if w and null cdr w then w := car w . '!&optional . gensym() . nil;
        w := s!:compile1(g, w, cddr u, nil);
        for each p in w do s!:cgen(car p, cadr p, caddr p, cdddr p);
        s!:cinit list('dm, car u, '(u !&optional e), list(g, 'u, 'e))
      end    
    else if eqcar(u, 'putd) then begin
% If people put (putd 'name 'expr '(lambda ...)) in their file I will
% expand it out as if it had been a (de name ...) [similarly for macros].
% This is done at least once in REDUCE.
      scalar a1, a2, a3;
      a1 := cadr u; a2 := caddr u; a3 := cadddr u;
      if eqcar(a1, 'quote) and
         (a2 = '(quote expr) or a2 = '(quote macro)) and
         (eqcar(a3, 'quote) or eqcar(a3, 'function)) and
         eqcar(cadr a3, 'lambda) then <<
         a1 := cadr a1; a2 := cadr a2; a3 := cadr a3;
         u := (if a2 = 'expr then 'de else 'dm) . a1 . cdr a3;
         s!:cout1(u, loadonly) >>
      else s!:cinit u end
    else if not eqcar(u, 'c_end) and
            not eqcar(u, 'carcheck) then s!:cinit u
  end;

fluid '(s!:cmod_name);

symbolic procedure c_end;
  begin
    if null s!:cmod_name then return nil;
    s!:cend();
    dfprint!* := s!:dfprintsave;
    !*defn := nil;
    !*comp := cdr s!:cmod_name;
    s!:cmod_name := nil;
    return nil
  end;

put('c_end, 'stat, 'endstat);

symbolic procedure c_out u;
  begin
    terpri();
    princ "C_OUT ";
    prin u; princ ": IN files;  or type in expressions"; terpri();
    princ "When all done, execute C_END;"; terpri();
% I permit the argument to be either a name, or a list of one item
% that is a name.  The idea here is that when called from RLISP it is
% most convenient for the call to map onto (c_out '(xxx)), while direct
% use from Lisp favours (c_out 'xxx)
    if not atom u then u := car u;
    if null s!:cstart u then <<
       if posn() neq 0 then terpri();
       princ "+++ Failed to open C output file"; terpri();
       return nil >>;
    s!:cmod_name := u . !*comp;
    s!:dfprintsave := dfprint!*;
    dfprint!* := 's!:cout0;
    !*defn := t;
    !*comp := nil;
    if getd 'begin then return nil;
    s!:c_supervisor();
  end;

put('c_out, 'stat, 'rlis);

symbolic procedure s!:compile!-file!*(fromfile,
                                      !&optional, tofile, verbose, !*pwrds);
  begin
    scalar !*comp, w, save;
    if null tofile then tofile := fromfile;
    if verbose then <<
       if posn() neq 0 then terpri();
!#if common!-lisp!-mode
       princ ";; Compiling file ";
!#else
       princ "+++ Compiling file ";
!#endif
       prin fromfile;
       terpri();
       save := verbos nil;
       verbos ilogand(save, 4) >>;
    if not start!-module tofile then <<
       if posn() neq 0 then terpri();
       princ "+++ Failed to open FASL output file"; terpri();
       if save then verbos save;
       return nil >>;
!#if common!-lisp!-mode
       << w := open(fromfile, !:direction, !:input,
                              !:if!-does!-not!-exist, nil);
!#else
          w := open(fromfile, 'input);
!#endif
          if w then <<
             w := rds w;
             s!:fasl_supervisor();
             close rds w >>
          else << princ "Failed to open file "; prin fromfile; terpri() >>
!#if common!-lisp!-mode
       >> where !*package!* = !*package!*;
!#else
       ;
!#endif
    if save then verbos save;
    start!-module nil;
    if verbose then <<
       if posn() neq 0 then terpri();
!#if common!-lisp!-mode
       princ ";; Compilation complete";
!#else
       princ "+++ Compilation complete";
!#endif
       terpri() >>;
    return t
  end;

% I provide a version that will suffice for Standard Lisp and replace it
% later on in the Common Lisp case (where keyword args are needed)

symbolic procedure compile!-file!*(fromfile, !&optional, tofile);
   s!:compile!-file!*(fromfile, tofile, t, t);

symbolic procedure compd(name, type, defn);
  begin
    scalar g, !*comp;
    !*comp := t;
    if eqcar(defn, 'lambda) then << 
       g := dated!-name type;
       symbol!-set!-definition(g, defn);
       compile list g;
       defn := g >>;
     put(name, type, defn);
    return name
  end;

symbolic procedure s!:compile0 name;
  begin
    scalar w, args, defn;
    defn := getd name;
    if eqcar(defn, 'macro) and eqcar(cdr defn, 'lambda) then 
       begin
         scalar !*comp, lx, vx, bx;
% If I have a macro definition
%    (dm fff (v) (ggg ...))
% I will usually map it onto a pair of definitions
%    (dm fff (v) (fff!* v))
%    (de fff!* (v) (ggg ...))
% and then compile fff!* but not fff itself.  If this has been done already
% or the initial definition of fff was in the required form then I will
% not perform any transformation and I will not compile fff.
% I also need to detect the case
%    (dm fff (v &optional e) (fff!* v e))
         lx := cdr defn;    % (LAMBDA vx bx)
         if not ((length lx = 3 and
                  not atom (bx := caddr lx) and
                  cadr lx = cdr bx) or
                 (length lx = 3 and
                  not atom (bx := caddr lx) and
                  not atom cadr lx and eqcar(cdadr lx, '!&optional) and
                  not atom (bx := cdr bx) and caadr lx = car bx and
                  cddadr lx = cdr bx)) then <<
            w := hashtagged!-name(name, defn);
            symbol!-set!-definition(w, cdr defn);
            s!:compile0 w;
            if 1 = length cadr lx then
                symbol!-set!-env(name, list('(u !&optional env),
                                       list(w, 'u)))
            else symbol!-set!-env(name, list('(u !&optional env),
                                        list(w, 'u, 'env))) >>
       end
    else if not eqcar(defn, 'expr) or not eqcar(cdr defn, 'lambda) then <<
       if !*pwrds then <<
          if posn() neq 0 then terpri();
          princ "+++ "; prin name; princ " not compilable"; terpri() >> >>
    else <<
       args := cddr defn;
       defn := cdr args;
       args := car args;
       if stringp args then <<
          if !*pwrds then <<
             if posn() neq 0 then terpri();
             princ "+++ "; prin name; princ " was already compiled";
             terpri() >> >>
       else <<
          if !*savedef then
             put(name, '!*savedef,
                       'lambda . args . s!:fully_macroexpand_list defn);
          w := s!:compile1(name, args, defn, nil);
          for each p in w do
             symbol!-set!-definition(car p, cdr p) >> >>
  end;

symbolic procedure s!:fully_macroexpand_list l;
   if atom l then l
   else for each u in l collect s!:fully_macroexpand u;

symbolic procedure s!:fully_macroexpand x;
% This MUST match the logic in s!:comval, so that there are no oddities
% about which order expansions are done in. 
  begin
    scalar helper;
    if atom x or eqcar(x, 'quote) then return x
    else if eqcar(car x, 'lambda) then return
       ('lambda . cadar x . s!:fully_macroexpand_list cddar x) .
          s!:fully_macroexpand_list cdr x
!#if common!-lisp!-mode
    else if helper := s!:local_macro car x then <<
       if atom cdr helper then
          s!:fully_macroexpand('funcall . cdr helper . cdr x)
       else s!:fully_macroexpand funcall('lambda . cdr helper, x) >>
!#endif
% NB I do not expand compilermacros here. Actually at present "vector"
% seems to be the only function that has one.
    else if (helper := get(car x, 's!:newname)) then
        return s!:fully_macroexpand (helper . cdr x)
    else if helper := get(car x, 's!:expandfn) then
        return funcall(helper, x)
    else if helper := macro!-function car x then
       return s!:fully_macroexpand funcall(helper, x)
    else return car x . s!:fully_macroexpand_list cdr x
  end;

symbolic procedure s!:expandfunction u;
   u;

symbolic procedure s!:expandflet u;
% u is (flet ( (name1 (v1 v2..) body)
%              (name2 ( ...) body) )
%            body)
   car u . (for each b in cadr u collect s!:expandfletvars b) .
           s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandfletvars b;
% b is (name (.. lambda vars ...) body ...)
   car b . cadr b . s!:fully_macroexpand_list cddr b;

symbolic procedure s!:expandlabels u;
   s!:expandflet u;   

symbolic procedure s!:expandmacrolet u;
   s!:expandflet u;   

symbolic procedure s!:expandprog u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandtagbody u;
   s!:fully_macroexpand_list u;

symbolic procedure s!:expandprogv u;
   car u . cadr u . caddr u . s!:fully_macroexpand_list cadddr u;

symbolic procedure s!:expandblock u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expanddeclare u;
   u;

symbolic procedure s!:expandlet u;
   car u . (for each x in cadr u collect s!:fully_macroexpand_list x) .
      s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandlet!* u;
   s!:expandlet u;

symbolic procedure s!:expandgo u;
   u;

symbolic procedure s!:expandreturn!-from u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandcond u;
   car u . for each x in cdr u collect s!:fully_macroexpand_list x;

symbolic procedure s!:expandcase u;
   car u . s!:fully_macroexpand cadr u . for each x in cddr u collect 
           (car x . s!:fully_macroexpand_list cdr x);

symbolic procedure s!:expandeval!-when u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandthe u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;

symbolic procedure s!:expandmv!-call u;
   car u . cadr u . s!:fully_macroexpand_list cddr u;


put('function, 's!:expandfn, function s!:expandfunction);
put('flet, 's!:expandfn, function s!:expandflet);
put('labels, 's!:expandfn, function s!:expandlabels);
put('macrolet, 's!:expandfn, function s!:expandmacrolet);
put('prog, 's!:expandfn, function s!:expandprog);
put('tagbody, 's!:expandfn, function s!:expandtagbody);
put('progv, 's!:expandfn, function s!:expandprogv);
!#if common!-lisp!-mode
put('block, 's!:expandfn, function s!:expandblock);
!#else
put('!~block, 's!:expandfn, function s!:expandblock);
!#endif
put('declare, 's!:expandfn, function s!:expanddeclare);
!#if common!-lisp!-mode
put('let, 's!:expandfn, function s!:expandlet);
!#else
put('!~let, 's!:expandfn, function s!:expandlet);
!#endif
put('let!*, 's!:expandfn, function s!:expandlet!*);
put('go, 's!:expandfn, function s!:expandgo);
put('return!-from, 's!:expandfn, function s!:expandreturn!-from);
put('cond, 's!:expandfn, function s!:expandcond);
put('case, 's!:expandfn, function s!:expandcase);
put('eval!-when, 's!:expandfn, function s!:expandeval!-when);
put('the, 's!:expandfn, function s!:expandthe);
put('multiple!-value!-call, 's!:expandfn, function s!:expandmv!-call);


% As soon as compile() is defined, !*comp enables automatic compilation
% when functions are defined - so I must put the definition of compile
% right at the end of this file so that nothing untoward happens during
% initial building.

symbolic procedure compile l;
  begin
     if atom l and not null l then l := list l;
     for each name in l do
        errorset(list('s!:compile0, mkquote name), t, t);
     return l
  end;

% These days I am putting the compiler that generates C in with
% the bytecode compiler...

in "$cslbase/ccomp.red"$


end;

% End of compiler.red
