
linelength 72;

in "struct.red"$

fluid '(all_jumps);

%
% "unbyte" is the main body of the decoder
%

fluid '(!@a !@b !@w !@stack !@catch);

global '(opnames);

symbolic procedure unbyte name;
  begin
    scalar pc, code, len, env, byte, r, entry_stack,
           w, w1, w2, args, nargs, stack, deepest, locals,
           all_jumps, !@a, !@b, !@w, !@stack, !@catch;
    !@a := gensym(); !@b := gensym(); !@w := gensym(); !@stack := gensym();
    code := symbol!-env name;
    nargs := symbol!-argcount name;
    if atom code or not bpsp car code then return nil;
    env := cdr code;
    code := car code;
    len := bps!-upbv code;
% If the function has 4 or more arge then the first byte of the bytestream
% says just how many. If it has &optional and/or &rest support the first
% two bytes give information on the largest and smallest valid number of
% args.
    if fixp nargs then
    <<  entry_stack := nargs;
        if nargs < 4 then pc := 0 else pc := 1 >>
    else <<
       entry_stack := cadr nargs;
       if logand(caddr nargs, 2) neq 0 then entry_stack := entry_stack+1;
       pc := 2 >>;
% The first stage will be to unpick the byte-stream into at least some sort
% of more spread-out data structure, recognising the lengths of various
% instructions. The output I will collect will be a list where each item is
% of the form
%      (address nil s-expression-1 s-expression-1 ...)
% with stack operands shown as (stack nn) and label operands as numeric
% offsets. Subsequent passes will use the field that is initially set as
% nil to help me decide where labels should be set and I will need to
% convert data references from being relative to the top of the stack into
% being relative to a known stack-base.
    r := nil;
    all_jumps := list(nil, pc);  % Force label on entrypoint
    while pc <= len do <<
       byte := bps!-getv(code, pc);
       w := funcall(getv(opnames, byte), pc+1, code, env);
% If the previous instruction had been a branch (marked here as an IF
% statement) then I would have indicated a jump to an explicit label as
% the ELSE part and I want to set the label concerned on whatever follows.
% The stacked-up IF is stored as
%   (address label (IF cond dest (GO ggg)))
% where ggg is what I want.
       if r then w1 := caddr car r
       else w1 := nil;
       if eqcar(w1, 'if) then
          r := (pc . cadr cadddr w1 . cdr w) . r
       else r := (pc . nil  . cdr w) . r;
       pc := pc + car w >>;
% All jumps in the code will have been represented as
%   (if xxx (go xx) (go yy))
% but in the first pass I can not have these resolved as symbolic labels.
% To begin with xx will be a numeric address, and the items (go xx) will be
% cahined through their CAR fields (so the 'go is not present yet). The
% (go yy) will have a symbolic label for yy and this must be set on the
% instruction immediately after then goto.
    while all_jumps do <<
       w := assoc(cadr all_jumps, r);  % The branch destination
       if null w then error(1, "Branch destination not found");
       if null cadr w then rplaca(cdr w, gensym());
       rplaca(cdr all_jumps, cadr w);
       w := car all_jumps;
       rplaca(all_jumps, 'go);
       all_jumps := w >>;
% Now jumps are under control I will consolidate the entire decoded mess into
% a collection of basic blocks, keyed by labels. At this stage it is
% possible for a block not to have any explicit branch at its end. I want to
% change that so that every block does end in an explicit jump or exit. The
% cases I will recognise are:
%   (if ...)
%   (go ..)
%   (return ..)
%   (throw) and maybe some others that I am not worrying about yet
    w := nil;
    while r do <<
       w1 := cddar r;
       w2 := w1;
       while cdr w2 do w2 := cdr w2;
       w2 := car w2; % Final instruction in this block
% Append GO to drop through, if necessary
       if w and not (
           eqcar(w2, 'if) or
           eqcar(w2, 'go) or
           eqcar(w2, 'return) or
           eqcar(w2, 'throw)) then <<
          w1 := append(w1, list list('go, caar w)) >>;
       while null cadar r do <<
          r := cdr r;
          w1 := append(cddar r, w1) >>;
       w := (cadar r . nil . w1) . w;
       r := cdr r >>;
% The next thing I have to do is to link FREERSTR opcodes up with the
% FREEBIND opcodes that they belong to. I NEED to do this early on
% because a FREEBIND and its FREERSTR move the stack up or down by
% an amount dependent on the number of variables being bound. For FREEBIND
% this is instantly visible, but for FREERSTR the information is only
% available by determining which FREEBIND it matches. But finding this
% out should be OK since every FREERSTR should correspond to exactly one
% FREEBIND. Because there should be no ambiguity at all about matching
% binds with restores I can have a fairly simple version of data flow
% analysis to make the link-up.
    rplaca(cdar w, list nil);  % No free bindings at entry-point
    r := list caar w;          % pending blocks
    while r do begin
       scalar n;
       w1 := assoc(car r, w);
       r := cdr r;
       n := caadr w1;
       for each z in cddr w1 do <<
          if eqcar(z, 'freebind) then n := cadr z . n
          else if eqcar(z, 'freerstr) then <<
             rplaca(cdr z, car n);
             n := cdr n >>
          else if eqcar(z, 'if) then <<
             r := set_bind(assoc(cadr caddr z, w), r, n);
             r := set_bind(assoc(cadr cadddr z, w), r, n) >>
          else if eqcar(z, 'go) then
             r := set_bind(assoc(cadr z, w), r, n) >>
       end;
% Blocks are now in order with the starting basic block at the top of
% the list (w). Each block is (label flag contents..) where the flag is nil
% at present. I will traverse the collection of blocks replacing the nils
% with the stack depth in force at the start of each block. This gives
% me a chance to detect inconsistencies in this area, but is also
% a vital prelude to replacing stack references with names.
    for each z in w do rplaca(cdr z, nil);
    rplaca(cdar w, entry_stack); % stack depth for entry block
    deepest := entry_stack;
    r := list caar w;      % list of "pending" blocks
    while r do begin
       scalar n;
       w1 := assoc(car r, w);
       if null w1 then <<
          prin car r; princ " not found in "; print w;
          error(1, r) >>;
       r := cdr r;
       n := cadr w1;
       if n > deepest then deepest := n;
       for each z in cddr w1 do <<
          if z = 'push then n := n + 1
          else if z = 'lose then n := n - 1
          else if eqcar(z, 'freebind) then n := n + 2 + length cadr z
          else if z = 'pvbind then n := n + 2
          else if eqcar(z, 'freerstr) then n := n - 2 - length cadr z
          else if z = 'pvrestore then n := n - 2
          else if z = 'uncatch or z = 'unprotect then n := n - 3
          else if eqcar(z, 'if) then <<
             if eqcar(cadr z, !@catch) then <<
                n := n+3;
                rplaca(z, 'ifcatch) >>;
             r := set_stack(assoc(cadr caddr z, w), r, n);
             r := set_stack(assoc(cadr cadddr z, w), r, n) >>
          else if eqcar(z, 'go) then
             r := set_stack(assoc(cadr z, w), r, n);
          if n < entry_stack then error(1, "Too many POPs in the codestream")
          else if n > deepest then deepest := n >>
      end;
% Now I want three separate things. One is the list of formal arguments
% to be put in a procedure header. This must contain annotations such as
% &optional and &rest where relevant. The other is a map of the stack.
% this will include all arguments, but without &optional etc. The final thing
% will be a list of local variables required for this procedure. This
% will include all the stack items not present as arguments together with
% the workspace items !@a, !@b and !@w.
    args := stack := locals := nil;
    if fixp nargs then <<
       for i := 1:nargs do stack := gensym() . stack;
       args := reverse stack >>
    else <<
       for i := 1:car nargs do stack := gensym() . stack;
       args := stack;
       if not (cadr nargs = car nargs) then <<
          args := '!&optional . args;
          for i := car nargs+1:cadr nargs do <<
             w1 := gensym();
             stack := w1 . stack;
             if logand(caddr nargs, 1) = 0 then args := w1 . args
             else args := list(w1, ''!*spid!*) . args >>;
          if logand(caddr nargs, 2) neq 0 then <<
             w1 := gensym();
             stack := w1 . stack;
             args := w1 . '!&rest . args >> >>;
       args := reverse args >>;
    locals := list(!@a, !@b, !@w);
    for i := 1+length stack:deepest do locals := gensym() . locals;
% Now if I find a reference to a location (!@stack n) at a stage when
% the logical stack depth is m I can map it onto a reference to a simple
% variable - either a local or one of the arguments. The code in
% stackref knows how to do this.
    for each b in w do begin
       scalar m, z1;
       m := cadr b;
       if not fixp m then error(1, "Unreferenced code block");
       for each z in cddr b do <<
          if z = 'push then m := m + 1
          else if z = 'lose then m := m - 1
          else if eqcar(z, 'freebind) then m := m + 2 + length cadr z
          else if z = 'pvbind then m := m + 2
          else if eqcar(z, 'freerstr) then m := m - 2 - length cadr z
          else if z = 'pvrestore then m := m - 2
          else if z = 'uncatch or z = 'unprotect then m := m - 3
          else <<
             z1 := stackref(z, m, stack, locals, entry_stack);
             rplaca(z, car z1); rplacd(z, cdr z1) >> >>;
      end;
% Now is the time to deal with constructs that include matching
% pairs of byte-opcodes that must be brought together in the reconstructed
% Lisp code. The cases that arise are
%      FREEBIND(data); ... FREERSTR
%          which must map onto
%      (prog (vars) ...)
%          and note that there could be several places where the FREERSTR
%          is present - these can correspond to places where the original
%          code contained a RETURN or a GO that exited from the scope
%          of the fluid binding. Since at the level I am working here
%          values are passed in the !@a variable I do not need to distinguish
%          these cases too specially and reconstruct clever arguments for
%          a RETURN. If there is just one exit point from the reconstructed
%          block I may as well use RETURN but it is not vital.
%
%      CATCH(label); ....UNCATCH; label: ...
%          the label mentioned in the CATCH ought always to be the one
%          just after an UNCATCH.  There can be other UNCATCH statements
%          on branches through the code that represent lexical exits from the
%          protected region (eg GO or RETURN). Distinguishing between
%          exits of this sort that represent GO and those that are RETURN
%          seems un-obvious but is a similar issue to the case with FREEBIND
%          and so perhaps does not matter too much.
%      (catch !@a  ... (go label)) label:
%
%      PVBIND; ... PVRESTORE
%          this is for
%      (progv !@a !@b ...)
%          teh compiler arranges for PVRESTOREs to be placed on every exit
%          from the funny region, and so arguments similar to those for
%          FREEBIND and CATCH apply about multiple exits.
%
%      (setq @a (load-spid)) CATCH(label); ... PROTECT; label: ... UNPROTECT
%          the CATCH used here is passed the result from the builtin function
%          (load-spid), which obtains a value that would not be valid as a
%          proper catch tag. The purpose of the PROTECT and UNPROTECT is
%          to delimit the cleanup forms and so indicate that a proper
%          value from the main protected form should survive across
%          that region. 
%          Any lexical (eg GO or RETURN) exit from the protected region
%          will have the sequence PROTECT cleanup-forms UNPROTECT inserted
%          along the path. Lexical exits from the region between PROTECT
%          and UNPROTECT are possible and will just LOSE three items from
%          the stack on the way, thereby discarding the way in which
%          the execution of UNPROTECT would have re-instated the exit
%          values and condition from the protected region.
%
    w := fix_free_bindings w;  % Ignore catch, unwind-protect, progv for now.
    w := optimise_blocks(w, stack, locals);
    r := 'prog . locals . flowgraph_to_lisp w;
    terpri(); princ "=> "; prettyprint r;
    w := errorset(list('structchk, mkquote r), t, t);
    if not atom w then r := car w;
    r := list('de, name, args, r);
    terpri(); princ "Finally: ";
    prettyprint r;
    return nil
  end;

symbolic procedure flowgraph_to_lisp w;
  begin
    scalar r;
    for each i in w do <<
       r := car i . r;
       for each j in cddr i do <<
          if eqcar(j, 'prog) then
              r := ('prog . cadr j . flowgraph_to_lisp cddr j) . r
% I convert from IF into COND because that will interact better with the
% re-structuring code that is used later on.
          else if eqcar(j, 'if) then
              r := list('cond, list(cadr j, caddr j),
                               list('t, cadddr j)) . r
          else if eqcar(j, 'freerstr) or
                  eqcar(j, 'progexits) then nil
          else if not member(j, '(push lose)) then r := j . r >> >>;
    return reversip r
  end;

symbolic procedure set_stack(block, r, n);
  if null cadr block then <<
     rplaca(cdr block, n);
     car block . r >>
  else if not (cadr block = n) then <<
     printc "++++ Stack confusion";
     prin n; princ " vs. "; print block;
     r >>
  else r;

symbolic procedure set_bind(block, r, n);
  if null cadr block then <<
     rplaca(cdr block, list n);
     car block . r >>
  else if not (caadr block = n) then <<
     printc "++++ Binding confusion";
     prin n; princ " vs. "; print block;
     r >>
  else r;

symbolic procedure stackref(u, m, stack, locals, entry_stack);
  if atom u or eqcar(u, 'quote) then u
  else if eqcar(u, !@stack) then begin
    scalar n, x;
    n := cadr u;
    x := n - m + entry_stack;
    if x >= 0 then <<
       if x >= entry_stack then error(1, "Reference outside stack-frame");
       for i := 1:x do stack := cdr stack;
       return car stack >>
    else <<
       for i := 1:-(x+1) do locals := cdr locals;
       return car locals >> end
  else for each x in u collect
           stackref(x, m, stack, locals, entry_stack);

opnames := mkvect 255$

% The table that follows lists the various opcodes that are used here.
% Each of these must be decoded, and the irregularity of the "machine"
% involved will leave this process rather untidy. For instance opcodes
% with similar actions are grouped together here but addressing modes are
% not at all consistently supported. This irregularity is not an accident:
% it is a consequence of attempting to keep code sequences as short as
% convenient.

%--  LOADLOC        general opcode to load from the stack
%--  LOADLOC0     LOADLOC1     LOADLOC2     LOADLOC3    specific offsets
%--  LOADLOC4     LOADLOC5     LOADLOC6     LOADLOC7
%--  LOADLOC8     LOADLOC9     LOADLOC10    LOADLOC11
%--  combinations to load two values (especially common cases)
%--  LOC0LOC1     LOC1LOC2     LOC2LOC3
%--  LOC1LOC0     LOC2LOC1     LOC3LOC2
%--  
%--  VNIL           load the value NIL
%--  
%--  LOADLIT        load a literal from the literal vector
%--               LOADLIT1     LOADLIT2     LOADLIT3    specific offsets
%--  LOADLIT4     LOADLIT5     LOADLIT6     LOADLIT7
%--  
%--  LOADFREE       load value of a free (FLUID/SPECIAL) variable
%--               LOADFREE1    LOADFREE2    LOADFREE3   specific offsets
%--  LOADFREE4
%--  
%--  STORELOC       Store onto stack
%--  STORELOC0    STORELOC1    STORELOC2    STORELOC3   specific offsets
%--  STORELOC4    STORELOC5    STORELOC6    STORELOC7
%--  
%--  STOREFREE      Set value of FLUID/SPECIAL variable
%--               STOREFREE1   STOREFREE2   STOREFREE3
%--  
%--  LOADLEX        access to non-local lexical variables (for Common Lisp)
%--  STORELEX
%--  CLOSURE
%--  
%--  Code to access local variables and also take CAR or CDR
%--  CARLOC0      CARLOC1      CARLOC2      CARLOC3
%--  CARLOC4      CARLOC5      CARLOC6      CARLOC7
%--  CARLOC8      CARLOC9      CARLOC10     CARLOC11
%--  CDRLOC0      CDRLOC1      CDRLOC2      CDRLOC3
%--  CDRLOC4      CDRLOC5
%--  CAARLOC0     CAARLOC1     CAARLOC2     CAARLOC3
%--  
%--  Function call support
%--  CALL0        CALL1        CALL2        CALL2R       CALL3        CALLN
%--  CALL0_0      CALL0_1      CALL0_2      CALL0_3
%--  CALL1_0      CALL1_1      CALL1_2      CALL1_3      CALL1_4      CALL1_5
%--  CALL2_0      CALL2_1      CALL2_2      CALL2_3      CALL2_4
%--  BUILTIN0     BUILTIN1     BUILTIN2     BUILTIN2R    BUILTIN3
%--  APPLY1       APPLY2       APPLY3       APPLY4   
%--  JCALL        JCALLN
%--  
%--  Branches. The main collection come in variants with long or short
%--  offsets and with the branch to go fowards or backwards.
%--  JUMP         JUMP_B       JUMP_L       JUMP_BL
%--  JUMPNIL      JUMPNIL_B    JUMPNIL_L    JUMPNIL_BL
%--  JUMPT        JUMPT_B      JUMPT_L      JUMPT_BL
%--  JUMPATOM     JUMPATOM_B   JUMPATOM_L   JUMPATOM_BL
%--  JUMPNATOM    JUMPNATOM_B  JUMPNATOM_L  JUMPNATOM_BL
%--  JUMPEQ       JUMPEQ_B     JUMPEQ_L     JUMPEQ_BL
%--  JUMPNE       JUMPNE_B     JUMPNE_L     JUMPNE_BL
%--  JUMPEQUAL    JUMPEQUAL_B  JUMPEQUAL_L  JUMPEQUAL_BL
%--  JUMPNEQUAL   JUMPNEQUAL_B JUMPNEQUAL_L JUMPNEQUAL_BL
%--  
%--  The following jumps go forwards only, and by only short offsets.  They
%--  are provided to support a collection of common special cases
%--  (a) test local variables for NIl or TRUE
%--  JUMPL0NIL    JUMPL0T                JUMPL1NIL    JUMPL1T
%--  JUMPL2NIL    JUMPL2T                JUMPL3NIL    JUMPL3T
%--  JUMPL4NIL    JUMPL4T
%--  (b) store in a local variable and test for NIL or TRUE
%--  JUMPST0NIL   JUMPST0T               JUMPST1NIL   JUMPST1T
%--  JUMPST2NIL   JUMPST2T
%--  (c) test if local variable is atomic or not
%--  JUMPL0ATOM   JUMPL0NATOM            JUMPL1ATOM   JUMPL1NATOM
%--  JUMPL2ATOM   JUMPL2NATOM            JUMPL3ATOM   JUMPL3NATOM
%--  (d) test free variable for NIL or TRUE
%--  JUMPFREE1NIL JUMPFREE1T             JUMPFREE2NIL JUMPFREE2T
%--  JUMPFREE3NIL JUMPFREE3T             JUMPFREE4NIL JUMPFREE4T
%--  JUMPFREENIL  JUMPFREET
%--  (e) test for equality (EQ) against literal value
%--  JUMPLIT1EQ   JUMPLIT1NE             JUMPLIT2EQ   JUMPLIT2NE
%--  JUMPLIT3EQ   JUMPLIT3NE             JUMPLIT4EQ   JUMPLIT4NE
%--  JUMPLITEQ    JUMPLITNE
%--  (f) call built-in one-arg function and use that as a predicate
%--  JUMPB1NIL    JUMPB1T                JUMPB2NIL    JUMPB2T
%--  (g) flagp with a literal tag
%--  JUMPFLAGP    JUMPNFLAGP
%--  (h) EQCAR test against literal
%--  JUMPEQCAR    JUMPNEQCAR
%--  
%--  CATCH needs something that behaves a bit like a (general) jump.
%--  CATCH        CATCH_B      CATCH_L      CATCH_BL
%--  After a CATCH the stack (etc) needs restoring
%--  UNCATCH      THROW        PROTECT      UNPROTECT
%--  
%--  PVBIND       PVRESTORE      PROGV support
%--  FREEBIND     FREERSTR       Bind/restore FLUID/SPECIAL variables
%--  
%--  Exiting from a procedure, optionally popping the stack a bit
%--  EXIT         NILEXIT      LOC0EXIT     LOC1EXIT     LOC2EXIT
%--  
%--  General stack management
%--  PUSH         PUSHNIL      PUSHNIL2     PUSHNIL3     PUSHNILS
%--  POP          LOSE         LOSE2        LOSE3        LOSES
%--  
%--  Exchange A and B registers
%--  SWOP
%--  
%--  Various especially havily used Lisp functions
%--  EQ           EQCAR        EQUAL        NUMBERP
%--  CAR          CDR          CAAR         CADR         CDAR         CDDR
%--  CONS         NCONS        XCONS        ACONS        LENGTH
%--  LIST2        LIST2STAR    LIST3
%--  PLUS2        ADD1         DIFFERENCE   SUB1         TIMES2
%--  GREATERP     LESSP
%--  FLAGP        GET          LITGET
%--  GETV         QGETV        QGETVN
%--  
%--  Support for over-large stack-frames (LOADLOC/STORELOC + lexical access)
%--  BIGSTACK
%--  Support for CALLs where the literal vector has become huge
%--  BIGCALL
%--  
%--  An integer-based SWITCH or CASE statement has special support
%--  ICASE
%--  
%--  Speed-up support for compiled GET and FLAGP when tag is important
%--  FASTGET
%--  
%--  Opcodes that have not yet been allocated.
%--  SPARE1
%--  SPARE2
%--  

in "../cslbase/opcodes.red";

begin
  scalar w;
  w := s!:opcodelist;
  for i := 0:255 do <<
     putv(opnames, i, compress('h . '!! . '!: . explode car w));
     w := cdr w >>
end;

global '(builtin0 builtin1 builtin2 builtin3);

builtin0 := mkvect 255$
builtin1 := mkvect 255$
builtin2 := mkvect 255$
builtin3 := mkvect 255$

for each x in oblist() do
  begin scalar w;
    if (w := get(x, 's!:builtin0)) then putv(builtin0, w, x)
    else if (w := get(x, 's!:builtin1)) then putv(builtin1, w, x)
    else if (w := get(x, 's!:builtin2)) then putv(builtin2, w, x)
    else if (w := get(x, 's!:builtin3)) then putv(builtin3, w, x)
  end;

% Now I have one procedure per opcode, so I can call the helper code to
% do the decoding. The result that must be handed back will be
% (n-bytes lisp1 lisp2 ...) where n-bytes is the number of
% bytes that composes this instruction. One could readily argue that the
% large number of somewhat repetitive procedures here represents bad
% software design and that some table-driven approach would be much better.
% My defence is that the bytecode model is inherently irregular and so the
% flexibility of using code is useful.

off echo;

symbolic procedure byte1;
   bps!-getv(code, pc);

symbolic procedure byte2;
   bps!-getv(code, pc+1);

symbolic procedure twobytes;
   256*byte1() + byte2();

symbolic procedure makeif(why, loc);
  list('if, why, loc, list('go, gensym()));

symbolic procedure jumpto x;
  all_jumps := list(all_jumps, x);

symbolic procedure jumpop why;
  list(2, makeif(why, jumpto(pc + byte1() + 1)));

symbolic procedure jumpopb why;
  list(2, makeif(why, jumpto(pc - byte1() + 1)));

symbolic procedure jumpopl why;
  list(3, makeif(why, jumpto(pc + twobytes() + 1)));

symbolic procedure jumpopbl why;
  list(3, makeif(why, jumpto(pc - twobytes() + 1)));

<<

symbolic procedure h!:LOADLOC(pc, code, env);
  list(2, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, byte1())));

symbolic procedure h!:LOADLOC0(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 0)));

symbolic procedure h!:LOADLOC1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 1)));

symbolic procedure h!:LOADLOC2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 2)));

symbolic procedure h!:LOADLOC3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 3)));

symbolic procedure h!:LOADLOC4(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 4)));

symbolic procedure h!:LOADLOC5(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 5)));

symbolic procedure h!:LOADLOC6(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 6)));

symbolic procedure h!:LOADLOC7(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 7)));

symbolic procedure h!:LOADLOC8(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 8)));

symbolic procedure h!:LOADLOC9(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 9)));

symbolic procedure h!:LOADLOC10(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 10)));

symbolic procedure h!:LOADLOC11(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(!@stack, 11)));

symbolic procedure h!:LOC0LOC1(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 0)), list('setq, !@a, list(!@stack, 1)));

symbolic procedure h!:LOC1LOC2(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 1)), list('setq, !@a, list(!@stack, 2)));

symbolic procedure h!:LOC2LOC3(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 2)), list('setq, !@a, list(!@stack, 3)));

symbolic procedure h!:LOC1LOC0(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 1)), list('setq, !@a, list(!@stack, 1)));

symbolic procedure h!:LOC2LOC1(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 2)), list('setq, !@a, list(!@stack, 1)));

symbolic procedure h!:LOC3LOC2(pc, code, env);
  list(1, list('setq, !@b, list(!@stack, 3)), list('setq, !@a, list(!@stack, 2)));

symbolic procedure h!:VNIL(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, nil));

symbolic procedure freeref(env, n);
   if n < 0 or n > upbv env then error(1, "free variable (etc) reference failure")
   else getv(env, n);

symbolic procedure litref(env, n);
   if n < 0 or n > upbv env then error(1, "literal reference failure")
   else mkquote getv(env, n);

symbolic procedure h!:LOADLIT(pc, code, env);
  list(2, list('setq, !@b, !@a), list('setq, !@a, litref(env, byte1())));

symbolic procedure h!:LOADLIT1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 1)));

symbolic procedure h!:LOADLIT2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 2)));

symbolic procedure h!:LOADLIT3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 3)));

symbolic procedure h!:LOADLIT4(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 4)));

symbolic procedure h!:LOADLIT5(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 5)));

symbolic procedure h!:LOADLIT6(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 6)));

symbolic procedure h!:LOADLIT7(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, litref(env, 7)));

symbolic procedure h!:LOADFREE(pc, code, env);
  list(2, list('setq, !@b, !@a), list('setq, !@a, freeref(env, byte1())));

symbolic procedure h!:LOADFREE1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, freeref(env, 1)));

symbolic procedure h!:LOADFREE2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, freeref(env, 2)));

symbolic procedure h!:LOADFREE3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, freeref(env, 3)));

symbolic procedure h!:LOADFREE4(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, freeref(env, 4)));

symbolic procedure h!:STORELOC(pc, code, env);
  list(2, list('setq, list(!@stack, byte1()), !@a));

symbolic procedure h!:STORELOC0(pc, code, env);
  list(1, list('setq, list(!@stack, 0), !@a));

symbolic procedure h!:STORELOC1(pc, code, env);
  list(1, list('setq, list(!@stack, 1), !@a));

symbolic procedure h!:STORELOC2(pc, code, env);
  list(1, list('setq, list(!@stack, 2), !@a));

symbolic procedure h!:STORELOC3(pc, code, env);
  list(1, list('setq, list(!@stack, 3), !@a));

symbolic procedure h!:STORELOC4(pc, code, env);
  list(1, list('setq, list(!@stack, 4), !@a));

symbolic procedure h!:STORELOC5(pc, code, env);
  list(1, list('setq, list(!@stack, 5), !@a));

symbolic procedure h!:STORELOC6(pc, code, env);
  list(1, list('setq, list(!@stack, 6), !@a));

symbolic procedure h!:STORELOC7(pc, code, env);
  list(1, list('setq, list(!@stack, 7), !@a));

symbolic procedure h!:STOREFREE(pc, code, env);
  list(2, list('setq, freeref(env, byte1()), !@a));

symbolic procedure h!:STOREFREE1(pc, code, env);
  list(1, list('setq, freeref(env, 1), !@a));

symbolic procedure h!:STOREFREE2(pc, code, env);
  list(1, list('setq, freeref(env, 2), !@a));

symbolic procedure h!:STOREFREE3(pc, code, env);
  list(1, list('setq, freeref(env, 3), !@a));

symbolic procedure h!:LOADLEX(pc, code, env);
  begin
    error(1, "loadlex");        % Not yet implemented here
    return list(3, 'loadlex)
  end;

symbolic procedure h!:STORELEX(pc, code, env);
  begin
    error(1, "storelex");       % Not yet implemented here
    return list(3, 'storelex)
  end;

symbolic procedure h!:CLOSURE(pc, code, env);
  begin
    error(1, "closure");       % Not yet implemented here
    return list(2, 'closure)
  end;

symbolic procedure h!:CARLOC0(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 0))));

symbolic procedure h!:CARLOC1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 1))));

symbolic procedure h!:CARLOC2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 2))));

symbolic procedure h!:CARLOC3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 3))));

symbolic procedure h!:CARLOC4(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 4))));

symbolic procedure h!:CARLOC5(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 5))));

symbolic procedure h!:CARLOC6(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 6))));

symbolic procedure h!:CARLOC7(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 7))));

symbolic procedure h!:CARLOC8(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 8))));

symbolic procedure h!:CARLOC9(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 9))));

symbolic procedure h!:CARLOC10(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 10))));

symbolic procedure h!:CARLOC11(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 11))));

symbolic procedure h!:CDRLOC0(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 0))));

symbolic procedure h!:CDRLOC1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 1))));

symbolic procedure h!:CDRLOC2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 2))));

symbolic procedure h!:CDRLOC3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 3))));

symbolic procedure h!:CDRLOC4(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 4))));

symbolic procedure h!:CDRLOC5(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('cdr, list(!@stack, 5))));

symbolic procedure h!:CAARLOC0(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('caar, list(!@stack, 0))));

symbolic procedure h!:CAARLOC1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('caar, list(!@stack, 1))));

symbolic procedure h!:CAARLOC2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('caar, list(!@stack, 2))));

symbolic procedure h!:CAARLOC3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list('car, list(!@stack, 3))));

symbolic procedure h!:CALL0(pc, code, env);
  list(2, list('setq, !@b, !@a), list('setq, !@a, list(freeref(env, byte1()))));

symbolic procedure h!:CALL1(pc, code, env);
  list(2, list('setq, !@a, list(freeref(env, byte1()), !@a)));

symbolic procedure h!:CALL2(pc, code, env);
  list(2, list('setq, !@a, list(freeref(env, byte1()), !@b, !@a)));

symbolic procedure h!:CALL2R(pc, code, env);
  list(2, list('setq, !@a, list(freeref(env, byte1()), !@a, !@b)));

symbolic procedure h!:CALL3(pc, code, env);
  list(2, list('setq, !@a, expand_call(3, freeref(env, byte1()))), 'lose);

symbolic procedure h!:CALLN(pc, code, env);
  begin
    scalar n, w;
    n := byte1();
    for i := 1:n-2 do w := 'lose . w;
    return list!*(3, 
      list('setq, !@a, expand_call(n, freeref(env, byte2()))), w)
  end;

symbolic procedure h!:CALL0_0(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(freeref(env, 0))));

symbolic procedure h!:CALL0_1(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(freeref(env, 1))));

symbolic procedure h!:CALL0_2(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(freeref(env, 2))));

symbolic procedure h!:CALL0_3(pc, code, env);
  list(1, list('setq, !@b, !@a), list('setq, !@a, list(freeref(env, 3))));

symbolic procedure h!:CALL1_0(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 0), !@a)));

symbolic procedure h!:CALL1_1(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 1), !@a)));

symbolic procedure h!:CALL1_2(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 2), !@a)));

symbolic procedure h!:CALL1_3(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 3), !@a)));

symbolic procedure h!:CALL1_4(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 4), !@a)));

symbolic procedure h!:CALL1_5(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 5), !@a)));

symbolic procedure h!:CALL2_0(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 0), !@b, !@a)));

symbolic procedure h!:CALL2_1(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 1), !@b, !@a)));

symbolic procedure h!:CALL2_2(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 2), !@b, !@a)));

symbolic procedure h!:CALL2_3(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 3), !@b, !@a)));

symbolic procedure h!:CALL2_4(pc, code, env);
  list(1, list('setq, !@a, list(freeref(env, 4), !@b, !@a)));

symbolic procedure h!:BUILTIN0(pc, code, env);
  begin
    scalar w;
    w := getv(builtin0, byte1());
    if null w then error(1, "Invalid builtin-function specifier");
    return list(2, list('setq, !@a, list w))
  end;

symbolic procedure h!:BUILTIN1(pc, code, env);
  begin
    scalar w;
    w := getv(builtin1, byte1());
    if null w then error(1, "Invalid builtin-function specifier");
    return list(2, list('setq, !@a, list(w, !@a)))
  end;

symbolic procedure h!:BUILTIN2(pc, code, env);
  begin
    scalar w;
    w := getv(builtin2, byte1());
    if null w then error(1, "Invalid builtin-function specifier");
    return list(2, list('setq, !@a, list(w, !@b, !@a)))
  end;

symbolic procedure h!:BUILTIN2R(pc, code, env);
  begin
    scalar w;
    w := getv(builtin2, byte1());
    if null w then error(1, "Invalid builtin-function specifier");
    return list(2, list('setq, !@a, list(w, !@a, !@b)))
  end;

symbolic procedure h!:BUILTIN3(pc, code, env);
  begin
    scalar w;
    w := getv(builtin3, byte1());
    if null w then error(1, "Invalid builtin-function specifier");
    return list(2, list('setq, !@a, expand_call(3, w)), 'lose)
  end;

symbolic procedure h!:APPLY1(pc, code, env);
  list(1, list('setq, !@a, list('apply, !@b, !@a)));

symbolic procedure h!:APPLY2(pc, code, env);
  list(1, list('setq, !@a, list('apply, list(!@stack, 0), !@b, !@a)), 'lose);  

symbolic procedure h!:APPLY3(pc, code, env);
  list(1, list('setq, !@a, list('apply, list(!@stack, 0), list(!@stack, 1), !@b, !@a)), 'lose, 'lose);  

symbolic procedure h!:APPLY4(pc, code, env);
  list(1, list('setq, !@a, list('apply, list(!@stack, 0), list(!@stack, 1), list(!@stack, 2), !@b, !@a)),
      'lose, 'lose, 'lose);  

symbolic procedure h!:JCALL(pc, code, env);
  begin
    scalar nargs, dest;
    nargs := byte1();
    dest := freeref(env, logand(nargs, 31));
    nargs := irightshift(nargs, 5);
    return list(2, expand_jcall(nargs, dest))
  end;

symbolic procedure h!:JCALLN(pc, code, env);
  list(3, expand_jcall(byte2(), freeref(env, byte1())));

symbolic procedure expand_jcall(nargs, dest);
  list('return, expand_call(nargs, dest));

symbolic procedure expand_call(nargs, dest);
  if nargs = 0 then list dest
  else if nargs = 1 then list(dest, !@a)
  else if nargs = 2 then list(dest, !@b, !@a)
  else begin scalar w;
    w := list(!@b, !@a);
    for i := 1:nargs-2 do w := list(!@stack, i) . w;
    return dest . w end;

symbolic procedure h!:JUMP(pc, code, env);
  list(2, jumpto(pc + byte1() + 1));

symbolic procedure h!:JUMP_B(pc, code, env);
  list(2, jumpto(pc - byte1() + 1));

symbolic procedure h!:JUMP_L(pc, code, env);
  list(3, jumpto(pc + twobytes() + 1));

symbolic procedure h!:JUMP_BL(pc, code, env);
  list(3, jumpto(pc - twobytes() + 1));

symbolic procedure h!:JUMPNIL(pc, code, env);
  jumpop list('null, !@a);

symbolic procedure h!:JUMPNIL_B(pc, code, env);
  jumpopb list('null, !@a);

symbolic procedure h!:JUMPNIL_L(pc, code, env);
  jumpopl list('null, !@a);

symbolic procedure h!:JUMPNIL_BL(pc, code, env);
  jumpopbl list('null, !@a);

symbolic procedure h!:JUMPT(pc, code, env);
  jumpop !@a;

symbolic procedure h!:JUMPT_B(pc, code, env);
  jumpopb !@a;

symbolic procedure h!:JUMPT_L(pc, code, env);
  jumpopl !@a;

symbolic procedure h!:JUMPT_BL(pc, code, env);
  jumpopbl !@a;

symbolic procedure h!:JUMPATOM(pc, code, env);
  jumpop list('atom, !@a);

symbolic procedure h!:JUMPATOM_B(pc, code, env);
  jumpopb list('atom, !@a);

symbolic procedure h!:JUMPATOM_L(pc, code, env);
  jumpopl list('atom, !@a);

symbolic procedure h!:JUMPATOM_BL(pc, code, env);
  jumpopbl list('atom, !@a);

symbolic procedure h!:JUMPNATOM(pc, code, env);
  jumpop list('not, list('atom, !@a));

symbolic procedure h!:JUMPNATOM_B(pc, code, env);
  jumpopb list('not, list('atom, !@a));

symbolic procedure h!:JUMPNATOM_L(pc, code, env);
  jumpopl list('not, list('atom, !@a));

symbolic procedure h!:JUMPNATOM_BL(pc, code, env);
  jumpopbl list('not, list('atom, !@a));

symbolic procedure h!:JUMPEQ(pc, code, env);
  jumpop list('eq, !@b, !@a);

symbolic procedure h!:JUMPEQ_B(pc, code, env);
  jumpopb list('eq, !@b, !@a);

symbolic procedure h!:JUMPEQ_L(pc, code, env);
  jumpopl list('eq, !@b, !@a);

symbolic procedure h!:JUMPEQ_BL(pc, code, env);
  jumpopbl list('eq, !@b, !@a);

symbolic procedure h!:JUMPNE(pc, code, env);
  jumpop list('not, list('eq, !@b, !@a));

symbolic procedure h!:JUMPNE_B(pc, code, env);
  jumpopb list('not, list('eq, !@b, !@a));

symbolic procedure h!:JUMPNE_L(pc, code, env);
  jumpopl list('not, list('eq, !@b, !@a));

symbolic procedure h!:JUMPNE_BL(pc, code, env);
  jumpopbl list('not, list('eq, !@b, !@a));

symbolic procedure h!:JUMPEQUAL(pc, code, env);
  jumpop list('equal, !@b, !@a);

symbolic procedure h!:JUMPEQUAL_B(pc, code, env);
  jumpopb list('equal, !@b, !@a);

symbolic procedure h!:JUMPEQUAL_L(pc, code, env);
  jumpopl list('equal, !@b, !@a);

symbolic procedure h!:JUMPEQUAL_BL(pc, code, env);
  jumpopbl list('equal, !@b, !@a);

symbolic procedure h!:JUMPNEQUAL(pc, code, env);
  jumpop list('not, list('equal, !@b, !@a));

symbolic procedure h!:JUMPNEQUAL_B(pc, code, env);
  jumpopb list('not, list('equal, !@b, !@a));

symbolic procedure h!:JUMPNEQUAL_L(pc, code, env);
  jumpopl list('not, list('equal, !@b, !@a));

symbolic procedure h!:JUMPNEQUAL_BL(pc, code, env);
  jumpopbl list('not, list('equal, !@b, !@a));

symbolic procedure h!:JUMPL0NIL(pc, code, env);
  jumpop list('null, list(!@stack, 0));

symbolic procedure h!:JUMPL0T(pc, code, env);
  jumpop list(!@stack, 0);

symbolic procedure h!:JUMPL1NIL(pc, code, env);
  jumpop list('null, list(!@stack, 1));

symbolic procedure h!:JUMPL1T(pc, code, env);
  jumpop list(!@stack, 1);

symbolic procedure h!:JUMPL2NIL(pc, code, env);
  jumpop list('null, list(!@stack, 2));

symbolic procedure h!:JUMPL2T(pc, code, env);
  jumpop list(!@stack, 2);

symbolic procedure h!:JUMPL3NIL(pc, code, env);
  jumpop list('null, list(!@stack, 3));

symbolic procedure h!:JUMPL3T(pc, code, env);
  jumpop list(!@stack, 3);

symbolic procedure h!:JUMPL4NIL(pc, code, env);
  jumpop list('null, list(!@stack, 4));

symbolic procedure h!:JUMPL4T(pc, code, env);
  jumpop list(!@stack, 4);

symbolic procedure h!:JUMPST0NIL(pc, code, env);
  jumpop list('null, list('setq, list(!@stack, 0), !@a));

symbolic procedure h!:JUMPST0T(pc, code, env);
  jumpop list('setq, list(!@stack, 0), !@a);

symbolic procedure h!:JUMPST1NIL(pc, code, env);
  jumpop list('null, list('setq, list(!@stack, 1), !@a));

symbolic procedure h!:JUMPST1T(pc, code, env);
  jumpop list('setq, list(!@stack, 1), !@a);

symbolic procedure h!:JUMPST2NIL(pc, code, env);
  jumpop list('null, list('setq, list(!@stack, 2), !@a));

symbolic procedure h!:JUMPST2T(pc, code, env);
  jumpop list('setq, list(!@stack, 2), !@a);

symbolic procedure h!:JUMPL0ATOM(pc, code, env);
  jumpop list('atom, list(!@stack, 0));

symbolic procedure h!:JUMPL0NATOM(pc, code, env);
  jumpop list('not, list('atom, list(!@stack, 0)));

symbolic procedure h!:JUMPL1ATOM(pc, code, env);
  jumpop list('atom, list(!@stack, 1));

symbolic procedure h!:JUMPL1NATOM(pc, code, env);
  jumpop list('not, list('atom, list(!@stack, 1)));

symbolic procedure h!:JUMPL2ATOM(pc, code, env);
  jumpop list('atom, list(!@stack, 2));

symbolic procedure h!:JUMPL2NATOM(pc, code, env);
  jumpop list('not, list('atom, list(!@stack, 2)));

symbolic procedure h!:JUMPL3ATOM(pc, code, env);
  jumpop list('atom, list(!@stack, 3));

symbolic procedure h!:JUMPL3NATOM(pc, code, env);
  jumpop list('not, list('atom, list(!@stack, 3)));

symbolic procedure h!:JUMPFREE1NIL(pc, code, env);
  jumpop list('null, freeref(env, 1));

symbolic procedure h!:JUMPFREE1T(pc, code, env);
  jumpop freeref(env, 1);

symbolic procedure h!:JUMPFREE2NIL(pc, code, env);
  jumpop list('null, freeref(env, 2));

symbolic procedure h!:JUMPFREE2T(pc, code, env);
  jumpop freeref(env, 2);

symbolic procedure h!:JUMPFREE3NIL(pc, code, env);
  jumpop list('null, freeref(env, 3));

symbolic procedure h!:JUMPFREE3T(pc, code, env);
  jumpop freeref(env, 3);

symbolic procedure h!:JUMPFREE4NIL(pc, code, env);
  jumpop list('null, freeref(env, 4));

symbolic procedure h!:JUMPFREE4T(pc, code, env);
  jumpop freeref(env, 4);

symbolic procedure h!:JUMPFREENIL(pc, code, env);
  list(3, makeif(list('null, freeref(env, byte1())),
                 jumpto(pc + byte2() + 2)));

symbolic procedure h!:JUMPFREET(pc, code, env);
  list(3, makeif(freeref(env, byte1()), jumpto(pc + byte2() + 2)));

symbolic procedure h!:JUMPLIT1EQ(pc, code, env);
  jumpop list('eq, !@a, litref(env, 1));

symbolic procedure h!:JUMPLIT1NE(pc, code, env);
  jumpop list('not, list('eq, !@a, litref(env, 1)));

symbolic procedure h!:JUMPLIT2EQ(pc, code, env);
  jumpop list('eq, !@a, litref(env, 2));

symbolic procedure h!:JUMPLIT2NE(pc, code, env);
  jumpop list('not, list('eq, !@a, litref(env, 1)));

symbolic procedure h!:JUMPLIT3EQ(pc, code, env);
  jumpop list('eq, !@a, litref(env, 3));

symbolic procedure h!:JUMPLIT3NE(pc, code, env);
  jumpop list('not, list('eq, !@a, litref(env, 1)));

symbolic procedure h!:JUMPLIT4EQ(pc, code, env);
  jumpop list('eq, !@a, litref(env, 4));

symbolic procedure h!:JUMPLIT4NE(pc, code, env);
  jumpop list('not, list('eq, !@a, litref(env, 1)));

symbolic procedure h!:JUMPLITEQ(pc, code, env);
  list(3, makeif(list('eq, !@a, litref(env, byte1())),
                 jumpto(pc + byte2() + 2)));

symbolic procedure h!:JUMPLITNE(pc, code, env);
  list(3, makeif(list('not, list('eq, !@a, litref(env, byte1()))),
                 jumpto(pc + byte2() + 2)));

symbolic procedure h!:JUMPB1NIL(pc, code, env);
  begin
    scalar w;
    w := elt(builtin1, byte1());
    if null w then error(1, "Bad in JUMPB1NIL");
    return list(3, makeif(list('null, list(w, !@a)),
                 jumpto(pc + byte2() + 2)));
  end;

symbolic procedure h!:JUMPB1T(pc, code, env);
  begin
    scalar w;
    w := elt(builtin1, byte1());
    if null w then error(1, "Bad in JUMPB1T");
    return list(3, makeif(list(w, !@a),
                 jumpto(pc + byte2() + 2)));
  end;

symbolic procedure h!:JUMPB2NIL(pc, code, env);
  begin
    scalar w;
    w := elt(builtin2, byte1());
    if null w then error(1, "Bad in JUMPB2NIL");
    return list(3, makeif(list('null, list(w, !@b, !@a)),
                 jumpto(pc + byte2() + 2)));
  end;

symbolic procedure h!:JUMPB2T(pc, code, env);
  begin
    scalar w;
    w := elt(builtin2, byte1());
    if null w then error(1, "Bad in JUMPB2T");
    return list(3, makeif(list(w, !@b, !@a),
                 jumpto(pc + byte2() + 2)));
  end;

symbolic procedure h!:JUMPFLAGP(pc, code, env);
  jumpop list('flagp, !@b, !@a);

symbolic procedure h!:JUMPNFLAGP(pc, code, env);
  jumpop list('not, list('flagp, !@b, !@a));

symbolic procedure h!:JUMPEQCAR(pc, code, env);
  list(3, makeif(list('eqcar, !@a, litref(env, byte1())),
                 jumpto(pc + byte2() + 2)));

symbolic procedure h!:JUMPNEQCAR(pc, code, env);
  list(3, makeif(list('not, list('eqcar, !@a, litref(env, byte1()))),
                 jumpto(pc + byte2() + 2)));

symbolic procedure h!:CATCH(pc, code, env);
  jumpop list(!@catch, !@a);

symbolic procedure h!:CATCH_B(pc, code, env);
  jumpopb list(!@catch, !@a);

symbolic procedure h!:CATCH_L(pc, code, env);
  jumpopl list(!@catch, !@a);

symbolic procedure h!:CATCH_BL(pc, code, env);
  jumpopbl list(!@catch, !@a);

symbolic procedure h!:UNCATCH(pc, code, env);
  list(1, 'uncatch, jumpto(pc));

symbolic procedure h!:THROW(pc, code, env);
  '(1 throw);

% There is a jolly feature here. I force in a JUMP just after any
% FREEBIND/FREERSTR since that will make later processing easier for me.
% Ditto CATCH etc.

symbolic procedure h!:PROTECT(pc, code, env);
  list(1 ,'protect, jumpto(pc));

symbolic procedure h!:UNPROTECT(pc, code, env);
  list(1, 'unprotect, jumpto(pc));

symbolic procedure h!:PVBIND(pc, code, env);
  list(1, 'pvbind, jumpto(pc));

symbolic procedure h!:PVRESTORE(pc, code, env);
  list(1, 'pvrestore, jumpto(pc));

symbolic procedure vector_to_list v;
  if not vectorp v then error(1, "Error in binding fluid variables")
  else begin
    scalar r;
    for i := 0:upbv v do r := getv(v, i) . r;
    return reversip r
  end;    

symbolic procedure h!:FREEBIND(pc, code, env);
  list(2, list('freebind, vector_to_list freeref(env, byte1())), jumpto(pc+1));

symbolic procedure h!:FREERSTR(pc, code, env);
  list(1, '(freerstr !*), jumpto(pc));

symbolic procedure h!:EXIT(pc, code, env);
  list(1, list('return, !@a));

symbolic procedure h!:NILEXIT(pc, code, env);
  list(1, list('return, nil));

symbolic procedure h!:LOC0EXIT(pc, code, env);
  list(1, list('return, list(!@stack, 0)));

symbolic procedure h!:LOC1EXIT(pc, code, env);
  list(1, list('return, list(!@stack, 1)));

symbolic procedure h!:LOC2EXIT(pc, code, env);
  list(1, list('return, list(!@stack, 2)));

symbolic procedure h!:PUSH(pc, code, env);
  list(1, 'push, list('setq, list(!@stack, 0), !@a));

symbolic procedure h!:PUSHNIL(pc, code, env);
  list(1, 'push, list('setq, list(!@stack, 0), nil));

symbolic procedure h!:PUSHNIL2(pc, code, env);
  list(1, 'push, list('setq, list(!@stack, 0), nil),
          'push, list('setq, list(!@stack, 0), nil));

symbolic procedure h!:PUSHNIL3(pc, code, env);
  list(1, 'push, list('setq, list(!@stack, 0), nil),
          'push, list('setq, list(!@stack, 0), nil),
          'push, list('setq, list(!@stack, 0), nil));

symbolic procedure h!:PUSHNILS(pc, code, env);
  begin
    scalar n, w;
    n := byte1();
    for i := 1:n do w := 'push . list('setq, list(!@stack, 0), nil) . w;
    return 2 . w
  end;

symbolic procedure h!:POP(pc, code, env);
  list(1, list('setq, list('!@stack, 0)), 'lose);

symbolic procedure h!:LOSE(pc, code, env);
  '(1 lose);

symbolic procedure h!:LOSE2(pc, code, env);
  '(1 lose lose);

symbolic procedure h!:LOSE3(pc, code, env);
  '(1 lose lose lose);

symbolic procedure h!:LOSES(pc, code, env);
  begin
    scalar n, w;
    n := byte1();
    for i := 1:n do w := 'lose . w;
    return 2 . w
  end;

symbolic procedure h!:SWOP(pc, code, env);
  list(1, list('setq, !@w, !@a),
          list('setq, !@a, !@b),
          list('setq, !@b, !@w));

symbolic procedure h!:EQ(pc, code, env);
  list(1, list('setq, !@a, list('eq, !@b, !@a)));

symbolic procedure h!:EQCAR(pc, code, env);
  list(1, list('setq, !@a, list('eqcar, !@b, !@a)));

symbolic procedure h!:EQUAL(pc, code, env);
  list(1, list('setq, !@a, list('equal, !@b, !@a)));

symbolic procedure h!:NUMBERP(pc, code, env);
  list(1, list('setq, !@a, list('numberp, !@a)));

symbolic procedure h!:CAR(pc, code, env);
  list(1, list('setq, !@a, list('car, !@a)));

symbolic procedure h!:CDR(pc, code, env);
  list(1, list('setq, !@a, list('cdr, !@a)));

symbolic procedure h!:CAAR(pc, code, env);
  list(1, list('setq, !@a, list('caar, !@a)));

symbolic procedure h!:CADR(pc, code, env);
  list(1, list('setq, !@a, list('cadr, !@a)));

symbolic procedure h!:CDAR(pc, code, env);
  list(1, list('setq, !@a, list('cdar, !@a)));

symbolic procedure h!:CDDR(pc, code, env);
  list(1, list('setq, !@a, list('cddr, !@a)));

symbolic procedure h!:CONS(pc, code, env);
  list(1, list('setq, !@a, list('cons, !@b, !@a)));

symbolic procedure h!:NCONS(pc, code, env);
  list(1, list('setq, !@a, list('ncons, !@a)));

symbolic procedure h!:XCONS(pc, code, env);
  list(1, list('setq, !@a, list('cons, !@a, !@b)));

symbolic procedure h!:ACONS(pc, code, env);
  list(1, list('setq, !@a, list('acons, !@b, !@a, list(!@stack, 0))), 'lose);

symbolic procedure h!:LENGTH(pc, code, env);
  list(1, list('setq, !@a, list('length, !@a)));

symbolic procedure h!:LIST2(pc, code, env);
  list(1, list('setq, !@a, list('list, !@b, !@a)));

symbolic procedure h!:LIST2STAR(pc, code, env);
  list(1, list('setq, !@a, list('list!*, !@b, !@a, list(!@stack, 0))), 'lose);

symbolic procedure h!:LIST3(pc, code, env);
  list(1, list('setq, !@a, list('list, !@b, !@a, list(!@stack, 0))), 'lose);

symbolic procedure h!:PLUS2(pc, code, env);
  list(1, list('setq, !@a, list('plus, !@b, !@a)));

symbolic procedure h!:ADD1(pc, code, env);
  list(1, list('setq, !@a, list('add1, !@a)));

symbolic procedure h!:DIFFERENCE(pc, code, env);
  list(1, list('setq, !@a, list('difference, !@b, !@a)));

symbolic procedure h!:SUB1(pc, code, env);
  list(1, list('setq, !@a, list('sub1, !@a)));

symbolic procedure h!:TIMES2(pc, code, env);
  list(1, list('setq, !@a, list('times, !@b, !@a)));

symbolic procedure h!:GREATERP(pc, code, env);
  list(1, list('setq, !@a, list('greaterp, !@b, !@a)));

symbolic procedure h!:LESSP(pc, code, env);
  list(1, list('setq, !@a, list('lessp, !@b, !@a)));

symbolic procedure h!:FLAGP(pc, code, env);
  list(1, list('setq, !@a, list('flagp, !@b, !@a)));

symbolic procedure h!:GET(pc, code, env);
  list(1, list('setq, !@a, list('get, !@b, !@a)));

symbolic procedure h!:LITGET(pc, code, env);
  list(2, list('setq, !@a, list('get, !@a, litref(env, byte1()))));

symbolic procedure h!:GETV(pc, code, env);
  list(1, list('setq, !@a, list('getv, !@b, !@a)));

symbolic procedure h!:QGETV(pc, code, env);
  list(1, list('setq, !@a, list('qgetv, !@b, !@a)));

symbolic procedure h!:QGETVN(pc, code, env);
  list(2, list('setq, !@a, list('qgetv, !@a, byte1())));

symbolic procedure h!:BIGSTACK(pc, code, env);
  begin
    error(1, "bigstack");        % Not yet implemented here
    return list(3, 'bigstack)
  end;

symbolic procedure h!:BIGCALL(pc, code, env);
  begin
    error(1, "bigcall");         % Not yet implemented here
    return list(3, 'bigcall)
  end;

symbolic procedure h!:ICASE(pc, code, env);
  begin
    error(1, "ICASE opcode found"); % Not yet implemented here
% This is followed by a whole bunch of addresses for destinations
    return list(4 + 2*byte1(), 'icase)
  end;

symbolic procedure h!:FASTGET(pc, code, env);
  begin
    error(1, "fastget");       % Not yet implemented here
    return list(2, 'fastget)
  end;

symbolic procedure h!:SPARE1(pc, code, env);
  error(1, "Invalid (spare) opcode found in byte-stream");

symbolic procedure h!:SPARE2(pc, code, env);
  error(1, "Invalid (spare) opcode found in byte-stream");

"All helper functions present" >>;

%
% fix_free_bindings searches for a (FREEBIND) and clips out everything
% up as far as all matching FREERSTRs
%

symbolic procedure find_freebind x;
   if null x then nil
   else if eqcar(car x, 'freebind) then x
   else find_freebind cdr x;

symbolic procedure find_freerstr x;
   if null x then nil
   else if eqcar(car x, 'freerstr) then x
   else find_freerstr cdr x;

symbolic procedure mark_restores(w, lab);
  begin
    scalar b;
    b := assoc(lab, w);
    if null b then error(1, "block not found");
    if cadr b then return nil;  % processed earlier...
    rplaca(cdr b, t);           % Mark this one as already noticed
    if find_freerstr cddr b then return nil
    else if find_freebind cddr b then return t;
    while not atom cdr b do b := cdr b;
    b := car b;
    if eqcar(b, 'go) then return mark_restores(w, cadr b)
    else if eqcar(b, 'if) then <<
       if mark_restores(w, cadr caddr b) then return t
       else return mark_restores(w, cadr cadddr b) >>
    else if eqcar(b, 'progexits) then return mark_several_restores(w, cdr b)
    else return nil
  end;

symbolic procedure mark_several_restores(w, l);
  if null l then nil
  else if mark_restores(w, car l) then t
  else mark_several_restores(w, cdr l);

symbolic procedure lift_free_binding(w, fb);
% Now all the marked basic blocks form part of a nested chunk, so I
% pull that out and re-insert it headed by the word "prog".
  begin
    scalar r1, r2, w1;
    while w do <<
       w1 := cdr w;
       if cadar w then << rplaca(cdar w, nil); rplacd(w, r1); r1 := w >>
       else << rplacd(w, r2); r2 := w >>;
       w := w1 >>;
    r1 := reversip r1;
    rplaca(fb, 'prog . cadar fb . r1);
    rplacd(fb, list ('progexits . free_exits r1));
    return reversip r2
  end;

symbolic procedure free_exits b;
  begin
    scalar r, r1;
    for each i in b do <<
       while not atom cdr i do i := cdr i;
       i := car i;
       if eqcar(i, 'go) then r := union(cdr i, r)
       else if eqcar(i, 'if) then
          r := union(cdr caddr i, union(cdr cadddr i, r))
       else if eqcar(i, 'progexits) then r := union(cdr i, r) >>;
    for each i in r do
       if null assoc(i, b) then r1 := i . r1;
    return r1
  end;

symbolic procedure fix_free_bindings w;
  begin
    scalar changed, aborted, p, fb;
    changed := t;
    while changed do <<
       changed := nil;
       for each z in w do rplaca(cdr z, nil);
       if aborted then p := cdr p
       else p := w;
       aborted := nil;
       while p and not (fb := find_freebind cddar p) do p := cdr p;
       if p then <<
          changed := t;
% fb = ((freebind (x y z)) (go lab))
          if mark_restores(w, cadr cadr fb) then aborted := t
          else w := lift_free_binding(w, fb) >> >>;
    return w
  end;

%
% The code above here is concerned with generating VALID Lisp code out of
% a byte-stream. It can be used as nothing more than a byte-code verifier
% if that is what you want. There is one call-out left in it, to a
% function called "optimise-blocks", and this is expected to turn the initial
% bunch of machine-code-like basic blocks into ones whose contents
% look a lot more like reasonable Lisp.
%


symbolic procedure optimise_blocks(w, args, locals);
  begin
    scalar vars, changed, avail;
    vars := append(args, locals);
    for each z in w do rplaca(cdr z, 'unknown);
    rplaca(cdar w, nil);
    changed := t;
    while changed do <<
       changed := nil;
       for each z in w do <<
          avail := cadr z;
          % prin car z; printc ":";
          for each q in cddr z do <<
             % princ "OPT: "; print q;
             nil >>
          >>
       >>;
    return w
  end;

!*echo := !*plap := t;

symbolic procedure simple x;
   if atom x then x
   else if null cdr x then car x
   else simple cdr x;

fluid '(x y);

symbolic procedure mylast x;
   if atom x then x
   else if null cdr x then car x
   else mylast cdr x;

symbolic procedure test a;
  begin scalar x;
  x := a+a+a;
  x := begin scalar y;
          y := x*x;
          print list(x, y);
          return y end;
  return x/a
  end;

unfluid '(x y);

!*plap := nil;

unbyte 'simple;
unbyte 'mylast;
unbyte 'test;

end;
