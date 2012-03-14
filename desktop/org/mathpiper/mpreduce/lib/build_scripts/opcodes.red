%
% This code may be used and modified, and redistributed in binary
% or source form, subject to the "CCL Public License", which should
% accompany it. This license is a variant on the BSD license, and thus
% permits use of code derived from this in either open and commercial
% projects: but it does require that updates to this code be made
% available back to the originators of the package. Note that as with
% any BSD-style licenses the terms here are not compatible with the GNU
% public license, and so GPL code should not be combined with the material
% here in any way.
%



s!:opcodelist := '(
LOADLOC      % general opcode to load from the stack
LOADLOC0     LOADLOC1     LOADLOC2     LOADLOC3  % specific offsets
LOADLOC4     LOADLOC5     LOADLOC6     LOADLOC7
LOADLOC8     LOADLOC9     LOADLOC10    LOADLOC11
% combinations to load two values (especially common cases)
LOC0LOC1     LOC1LOC2     LOC2LOC3
LOC1LOC0     LOC2LOC1     LOC3LOC2

VNIL         % load the value NIL

LOADLIT      % load a literal from the literal vector
             LOADLIT1     LOADLIT2     LOADLIT3  % specific offsets
LOADLIT4     LOADLIT5     LOADLIT6     LOADLIT7

LOADFREE     % load value of a free (FLUID/SPECIAL) variable
             LOADFREE1    LOADFREE2    LOADFREE3 % specific offsets
LOADFREE4

STORELOC     % Store onto stack
STORELOC0    STORELOC1    STORELOC2    STORELOC3 % specific offsets
STORELOC4    STORELOC5    STORELOC6    STORELOC7

STOREFREE    % Set value of FLUID/SPECIAL variable
             STOREFREE1   STOREFREE2   STOREFREE3

LOADLEX      % access to non-local lexical variables (for Common Lisp)
STORELEX
CLOSURE

% Code to access local variables and also take CAR or CDR
CARLOC0      CARLOC1      CARLOC2      CARLOC3
CARLOC4      CARLOC5      CARLOC6      CARLOC7
CARLOC8      CARLOC9      CARLOC10     CARLOC11
CDRLOC0      CDRLOC1      CDRLOC2      CDRLOC3
CDRLOC4      CDRLOC5
CAARLOC0     CAARLOC1     CAARLOC2     CAARLOC3

% Function call support
CALL0        CALL1        CALL2        CALL2R       CALL3        CALLN
CALL0_0      CALL0_1      CALL0_2      CALL0_3
CALL1_0      CALL1_1      CALL1_2      CALL1_3      CALL1_4      CALL1_5
CALL2_0      CALL2_1      CALL2_2      CALL2_3      CALL2_4
BUILTIN0     BUILTIN1     BUILTIN2     BUILTIN2R    BUILTIN3
APPLY1       APPLY2       APPLY3       APPLY4   
JCALL        JCALLN

% Branches. The main collection come in variants with long or short
% offsets and with the branch to go fowards or backwards.
JUMP         JUMP_B       JUMP_L       JUMP_BL
JUMPNIL      JUMPNIL_B    JUMPNIL_L    JUMPNIL_BL
JUMPT        JUMPT_B      JUMPT_L      JUMPT_BL
JUMPATOM     JUMPATOM_B   JUMPATOM_L   JUMPATOM_BL
JUMPNATOM    JUMPNATOM_B  JUMPNATOM_L  JUMPNATOM_BL
JUMPEQ       JUMPEQ_B     JUMPEQ_L     JUMPEQ_BL
JUMPNE       JUMPNE_B     JUMPNE_L     JUMPNE_BL
JUMPEQUAL    JUMPEQUAL_B  JUMPEQUAL_L  JUMPEQUAL_BL
JUMPNEQUAL   JUMPNEQUAL_B JUMPNEQUAL_L JUMPNEQUAL_BL

% The following jumps go forwards only, and by only short offsets.  They
% are provided to support a collection of common special cases
% (a) test local variables for NIl or TRUE
JUMPL0NIL    JUMPL0T                JUMPL1NIL    JUMPL1T
JUMPL2NIL    JUMPL2T                JUMPL3NIL    JUMPL3T
JUMPL4NIL    JUMPL4T
% (b) store in a local variable and test for NIL or TRUE
JUMPST0NIL   JUMPST0T               JUMPST1NIL   JUMPST1T
JUMPST2NIL   JUMPST2T
% (c) test if local variable is atomic or not
JUMPL0ATOM   JUMPL0NATOM            JUMPL1ATOM   JUMPL1NATOM
JUMPL2ATOM   JUMPL2NATOM            JUMPL3ATOM   JUMPL3NATOM
% (d) test free variable for NIL or TRUE
JUMPFREE1NIL JUMPFREE1T             JUMPFREE2NIL JUMPFREE2T
JUMPFREE3NIL JUMPFREE3T             JUMPFREE4NIL JUMPFREE4T
JUMPFREENIL  JUMPFREET
% (e) test for equality (EQ) against literal value
JUMPLIT1EQ   JUMPLIT1NE             JUMPLIT2EQ   JUMPLIT2NE
JUMPLIT3EQ   JUMPLIT3NE             JUMPLIT4EQ   JUMPLIT4NE
JUMPLITEQ    JUMPLITNE
% (f) call built-in one-arg function and use that as a predicate
JUMPB1NIL    JUMPB1T                JUMPB2NIL    JUMPB2T
% (g) flagp with a literal tag
JUMPFLAGP    JUMPNFLAGP
% (h) EQCAR test against literal
JUMPEQCAR    JUMPNEQCAR

% CATCH needs something that behaves a bit like a (general) jump.
CATCH        CATCH_B      CATCH_L      CATCH_BL
% After a CATCH the stack (etc) needs restoring
UNCATCH      THROW        PROTECT      UNPROTECT

PVBIND       PVRESTORE    % PROGV support
FREEBIND     FREERSTR     % Bind/restore FLUID/SPECIAL variables

% Exiting from a procedure, optionally popping the stack a bit
EXIT         NILEXIT      LOC0EXIT     LOC1EXIT     LOC2EXIT

% General stack management
PUSH         PUSHNIL      PUSHNIL2     PUSHNIL3     PUSHNILS
POP          LOSE         LOSE2        LOSE3        LOSES

% Exchange A and B registers
SWOP

% Various especially havily used Lisp functions
EQ           EQCAR        EQUAL        NUMBERP
CAR          CDR          CAAR         CADR         CDAR         CDDR
CONS         NCONS        XCONS        ACONS        LENGTH
LIST2        LIST2STAR    LIST3
PLUS2        ADD1         DIFFERENCE   SUB1         TIMES2
GREATERP     LESSP
FLAGP        GET          LITGET
GETV         QGETV        QGETVN

% Support for over-large stack-frames (LOADLOC/STORELOC + lexical access)
BIGSTACK
% Support for CALLs where the literal vector has become huge
BIGCALL

% An integer-based SWITCH or CASE statement has special support
ICASE

% Speed-up support for compiled GET and FLAGP when tag is important
FASTGET

% Opcodes that have not yet been allocated.
SPARE1
SPARE2
)$

end;



