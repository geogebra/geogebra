COMMENT
 
                  REDUCE INTERACTIVE LESSON NUMBER 6
 
                         David R. Stoutemyer
                        University of Hawaii
 
 
COMMENT This is lesson 6 of 7 REDUCE lessons.  A prerequisite is to
read the pamphlet "An Introduction to LISP", by D. Lurie'.

To avoid confusion between RLISP and the SYMBOLIC-mode algebraic
algorithms, this lesson will treat only RLISP.  Lesson 7 deals with how
the REDUCE algebraic mode is implemented in RLISP and how the user can
interact directly with that implementation.  That is why I suggested
that you run this lesson in RLISP rather than full REDUCE.  If you
forgot or do not have a locally available separate RLISP, then please
switch now to symbolic mode by typing the statement SYMBOLIC;

SYMBOLIC;
PAUSE;

COMMENT Your most frequent mistakes are likely to be forgetting to quote
data examples, using commas as separators within lists, and not puttng
enough levels of parentheses in your data examples.

Now that you have learned from your reading about the built-in RLISP
functions CAR, CDR, CONS, ATOM, EQ, NULL, LIST, APPEND, REVERSE, DELETE,
MAPLIST, MAPCON, LAMBDA, FLAG, FLAGP, PUT, GET, DEFLIST, NUMBERP, ZEROP,
ONEP, AND, EVAL, PLUS, TIMES, CAAR, CADR, etc., here is an opportunity
to reinforce the learning by practice.:  Write expressions using CAR,
CDR, CDDR, etc., (which are defined only through 4 letters between C and
R), to individually extract each atom from F, where;

F := '((JOHN . DOE) (1147 HOTEL STREET) HONOLULU);
PAUSE;

COMMENT  My solutions are CAAR F, CDAR F, CAADR F, CADADR F,
CADDR CADR F, and CADDR F.

Although commonly the "." is only mentioned in conjunction with data, we
can also use it as an infix alias for CONS.  Do this to build from F and
from the data 'MISTER the s-expression consisting of F with MISTER
inserted before JOHN.DOE;

PAUSE;

COMMENT  My solution is ('MISTER . CAR F) . CDR F .

Enough of these inane exercises -- let's get on to something useful!
Let's develop a collection of functions for operating on finite sets.
We will let the elements be arbitrary s-expressions, and we will
represent a set as a list of its elements in arbitrary order, without
duplicates.

Here is a function which determines whether its first argument is a
member of the set which is its second element;

SYMBOLIC PROCEDURE MEMBERP(ELEM, SET1);
   COMMENT  Returns T if s-expression ELEM is a top-level element
      of list SET1, returning NIL otherwise;
   IF NULL SET1 THEN NIL
      ELSE IF ELEM = CAR SET1 THEN T
   ELSE MEMBERP(ELEM, CDR SET1);
MEMBERP('BLUE, '(RED BLUE GREEN));

COMMENT This function illustrates several convenient techniques for
writing functions which process lists:

   1.  To avoid the errors of taking the CAR or the CDR of an atom, and
   to build self confidence while it is not immediately apparent how to
   completely solve the problem, treat the trivial cases first.  For an
   s-expression or list argument, the most trivial cases are generally
   when one or more of the arguments are NIL, and a slightly less
   trivial case is when one or more is an atom. (Note that we will get
   an error message if we use MEMBERP with a second argument which is
   not a list.  We could check for this, but in the interest of brevity,
   I will not strive to make our set-package give set-oriented error
   messages.)

   2.  Use CAR to extract the first element and use CDR to refer to the
   remainder of the list.

   3.  Use recursion to treat more complicated cases by extracting the
   first element and using the same functions on smaller arguments.;

PAUSE;
COMMENT To make MEMBERP into an infix operator we make the declaration;

INFIX MEMBERP;
'(JOHN.DOE) MEMBERP '((FIG.NEWTON) FONZO (SANTA CLAUS));

COMMENT Infix operators associate left, meaning expressions of the form

   (operator1 operator operand2 operator ... operandN)

are interpreted as

   ((...(operand1 operator operand2) operator ... operandN).

Operators may also be flagged RIGHT  by

   FLAG ('(op1 op2 ...), 'RIGHT) .

to give the interpretation

   (operand1 operator (operand2 operator (... operandN))...).

Of the built-in operators, only ".", "*=", "+", and "*" associate right.

If we had made the infix declaration before the function definition, the
latter could have begun with the more natural statement

   SYMBOLIC PROCEDURE ELEM MEMBERP SET  .

Infix functions can also be referred to by functional notation if one
desires.  Actually, an analogous infix operator named MEMBER is already
built-into RLISP, so we will use MEMBER rather than MEMBERP from here
on;

MEMBER(1147, CADR F);

COMMENT Inspired by the simple yet elegant definition of MEMBERP, write
a function named SETP which uses MEMBER to check for a duplicate element
in its list argument, thus determining whether or not the argument of
SETP is a set;

PAUSE;

COMMENT  My solution is;

SYMBOLIC PROCEDURE SETP CANDIDATE;
   COMMENT Returns T if list CANDIDATE is a set, returning NIL
      otherwise;
   IF NULL CANDIDATE THEN T
   ELSE IF CAR CANDIDATE MEMBER CDR CANDIDATE THEN NIL
   ELSE SETP CDR CANDIDATE;
SETP '(KERMIT, (COOKIE MONSTER));
SETP '(DOG CAT DOG);

COMMENT If you used a BEGIN-block, local variables, loops, etc., then
your solution is surely more awkward than mine.  For the duration of the
lesson, try to do everything without groups, BEGIN-blocks, local
variables, assignments, and loops.  Everything can be done using
function composition, conditional expressions, and recursion.  It will
be a mind-expanding experience -- more so than transcendental
meditation, psilopsybin, and EST.  Afterward, you can revert to your old
ways if you disagree.

Thus endeth the sermon.

Incidentally, to make the above definition of SETP work for non-list
arguments all we have to do is insert "ELSE IF ATOM CANDIDATE THEN NIL"
below "IF NULL CANDIDATE THEN T".

Now try to write an infix procedure named SUBSETOF, such that SET1
SUBSETOF SET2 returns NIL if SET1 contains an element that SET2 does
not, returning T otherwise.  You are always encouraged, by the way, to
use any functions that are already builtin, or that we have previously
defined, or that you define later as auxiliary functions;

PAUSE;
COMMENT  My solution is;

INFIX SUBSETOF;
SYMBOLIC PROCEDURE SET1 SUBSETOF SET2;
   IF NULL SET1 THEN T
   ELSE IF CAR SET1 MEMBER SET2 THEN CDR SET1 SUBSETOF SET2
   ELSE NIL;
'(ROOF DOOR) SUBSETOF '(WINDOW DOOR FLOOR ROOF);
'(APPLE BANANA) SUBSETOF '((APPLE COBBLER) (BANANA CREME PIE));

COMMENT  Two sets are equal when they have identical elements, not
necessarily in the same order.  Write an infix procedure named
EQSETP which returns T if its two operands are equal sets, returning
NIL otherwise;

PAUSE;

COMMENT  The following solution introduces the PRECEDENCE declaration;

INFIX EQSETP;
PRECEDENCE EQSETP, =;
PRECEDENCE SUBSETOF, EQSETP;
SYMBOLIC PROCEDURE SET1 EQSETP SET2;
   SET1 SUBSETOF SET2  AND  SET2 SUBSETOF SET1;
'(BALLET TAP) EQSETP '(TAP BALLET);
'(PINE FIR ASPEN) EQSETP '(PINE FIR PALM);

COMMENT The precedence declarations make SUBSETOF have a higher
precedence than EQSETP and make the latter have higher precedence than
"=", which is higher than "AND",.  Consequently, these declarations
enabled me to omit parentheses around "SET1 SUBSUBSETOF SET2" and around
"SET2 SUBSETOF SET1".  All prefix operators are higher than any infix
operator, and to inspect the ordering among the latter, we merely
inspect the value of the global variable named;

PRECLIS!*;

COMMENT Now see if you can write a REDUCE infix function named
PROPERSUBSETOF, which determines if its left operand is a proper subset
of its right operand, meaning it is a subset which is not equal to the
right operand;

PAUSE;

COMMENT  All of the above exercises have been predicates.  In contrast,
the next exercise is to write a function called MAKESET, which returns
a list which is a copy of its argument, omitting duplicates;

PAUSE;

COMMENT  How about;

SYMBOLIC PROCEDURE MAKESET LIS;
   IF NULL LIS THEN NIL
   ELSE IF CAR LIS MEMBER CDR LIS THEN MAKESET CDR LIS
   ELSE CAR LIS . MAKESET CDR LIS;

COMMENT As you may have guessed, the next exercise is to implement an
operator named INTERSECT, which returns the intersection of its set
operands;

PAUSE;

COMMENT  Here is my solution;

INFIX INTERSECT;
PRECEDENCE INTERSECT, SUBSETOF;
SYMBOLIC PROCEDURE SET1 INTERSECT SET2;
   IF NULL SET1 THEN NIL
   ELSE IF CAR SET1 MEMBER SET2
      THEN CAR SET1 . CDR SET1 INTERSECT SET2
   ELSE CDR SET1 INTERSECT SET2;

COMMENT  Symbolic-mode REDUCE has a built-in function named SETDIFF,
which returns the set of elements which are in its first argument but
not the second.  See if you can write an infix definition of a similar
function named DIFFSET;

PAUSE;

COMMENT  Presenting --;

INFIX DIFFSET;
PRECEDENCE DIFFSET, INTERSECT;
SYMBOLIC PROCEDURE LEFT DIFFSET RIGHT;
   IF NULL LEFT THEN NIL
   ELSE IF CAR LEFT MEMBER RIGHT THEN CDR LEFT DIFFSET RIGHT
   ELSE CAR LEFT . (CDR LEFT DIFFSET RIGHT);
'(SEAGULL WREN CONDOR) DIFFSET '(WREN LARK);

COMMENT The symmetric difference of two sets is the set of all elements
which are in only one of the two sets.  Implement a corresponding infix
function named SYMDIFF.  Look for the easy way!  There is almost always
one for examinations and instructional exercises; PAUSE;

COMMENT  Presenting --;
INFIX SYMDIFF;
PRECEDENCE SYMDIFF, INTERSECT;
SYMBOLIC PROCEDURE SET1 SYMDIFF SET2;
   APPEND(SET1 DIFFSET SET2, SET2 DIFFSET SET1);
'(SEAGULL WREN CONDOR) SYMDIFF '(WREN LARK);

COMMENT We can use APPEND because the two set differences are disjoint.

The above set of exercises (exercises of set?) have all returned set
results.  The cardinality, size, or length of a set is the number of
elements in the set.  More generally, it is useful to have a function
which returns the length of its list argument, and such a function is
built-into RLISP.  See if you can write a similar function named SIZEE;

PAUSE;
COMMENT  Presenting --;
SYMBOLIC PROCEDURE SIZEE LIS;
   IF NULL LIS THEN 0
   ELSE 1 + SIZEE CDR LIS;
SIZEE '(HOW MARVELOUSLY CONCISE);
SIZEE '();

COMMENT Literal atoms, meaning atoms which are not numbers, are stored
uniquely in LISP and in RLISP, so comparison for equality of literal
atoms can be implemented by comparing their addresses, which is
significantly more efficient than a character-by-character comparison of
their names.  The comparison operator "EQ" compares addresses, so it is
the most efficient choice when comparing only literal atoms.  The
assignments

   N2 := N1 := 987654321,
   S2 := S1 := '(FROG (SALAMANDER.NEWT)),

make N2 have the same address as N1 and make S2 have the same address as
S1, but if N1 and N2 were constructed independently, they would not
generally have the same address, and similarly for S1 vs. S2.  The
comparison operator "=", which is an alias for "EQUAL", does a general
test for identical s-expressions, which need not be merely two pointers
to the same address.  Since "=" is built-in, compiled, and crucial, I
will define my own differently-named version denoted ".=" as follows:;

PAUSE;
NEWTOK '((!. !=) MYEQUAL);
INFIX EQATOM, MYEQUAL;
PRECEDENCE MYEQUAL, EQUAL;
PRECEDENCE EQATOM,EQ;
SYMBOLIC PROCEDURE S1 MYEQUAL S2;
   IF ATOM S1 THEN
      IF ATOM S2 THEN S1 EQATOM S2
      ELSE NIL
   ELSE IF ATOM S2 THEN NIL
   ELSE CAR S1 MYEQUAL CAR S2 AND CDR S1 MYEQUAL CDR S2;
SYMBOLIC PROCEDURE A1 EQATOM A2;
   IF NUMBERP A1 THEN
      IF NUMBERP A2 THEN ZEROP(A1-A2)
      ELSE NIL
   ELSE IF NUMBERP A2 THEN NIL
   ELSE A1 EQ A2;

COMMENT Here I introduced a help function named EQATOM, because I was
beginning to become confused by detail when I got to the line which uses
EQATOM.  Consequently, I procrastinated on attending to some fine detail
by relegating it to a help function which I was confident could be
successfully written later.  After completing MYEQUAL, I was confident
that it would work provided EQATOM worked, so I could then turn my
attention entirely to EQATOM, freed of further distraction by concern
about the more ambitious overall goal.  It turns out that EQATOM is a
rather handy utility function anyway, and practice helps develop good
judgement about where best to so subdivide tasks.  This psychological
divide-and-conquer programming technique is important in most other
programming languages too.

".=" is different from our previous examples in that ".=" recurses down
the CAR as well as down the CDR of an s-expression;

PAUSE;
COMMENT
If a list has n elements, our function named MEMBERP or the equivalent
built-in function named MEMBER requires on the order of n "=" tests.
Consequently, the above definitions of SETP and MAKESET, which require
on the order of n membership tests, will require on the order of n**2
"=" tests.  Similarly, if the two operands have m and n elements, the
above definitions of SUBSETOF, EQSETP, INTERSECT, DIFFSET, and
SYMDIFF require on the order of m*n "=" tests.  We could decrease the
growth rates to order of n and order of m+n respectively by sorting the
elements before giving lists to these functions.  The best algorithms
sort a list of n elements in the order of n*log(n) element comparisons,
and this need be done only once per input set.  To do so we need a
function which returns T if the first argument is "=" to the second
argument or should be placed to the left of the second argument.  Such a
function, named ORDP, is already built-into symbolic-mode REDUCE, based
on the following rules:

   1.  Any number orders left of NIL.
   2.  Larger numbers order left of smaller numbers.
   4.  Literal atoms order left of numbers.
   3.  Literal atoms order among themselves by address, as determined
       by the built-in RLISP function named ORDERP.
   5.  Non-atoms order left of atoms.
   6.  Non-atoms order among themselves according to ORDP of their
       CARs, with ties broken according to ORDP of their CDRs.

Try writing an analogous function named MYORD, and, if you are in
REDUCE rather than RLISP, test its behavior in comparison to ORDP;

PAUSE;

COMMENT  Whether or not we use sorted sets, we can reduce the
proportionality constant associated with the growth rate by replacing
"=" by "EQ" if the set elements are restricted to literal atoms.
However, with such elements we can use property-lists to achieve the
growth rates of the sorted algorithms without any need to sort the
sets.  On any LISP system that is efficient enough to support REDUCE
with acceptable performance, the time required to access a property of
an atom is modest and very insensitive to the number of distinct
atoms in the program and data.  Consequently, the basic technique
for any of our set operations is:
   1.  Scan the list argument or one of the two list arguments,
       flagging each element as "SEEN".
   2.  During the first scan, or during a second scan of the same
       list, or during a scan of the second list, check each element
       to see whether or not it has already been flagged, and act
       accordingly.
   3.  Make a final pass through all elements which were flagged to
       remove the flag "SEEN". (Otherwise, we may invalidate later set
       operations which utilize any of the same atoms.)

We could use indicators rather than flags, but the latter are slightly
more efficient when an indicator would have only one value (such as
having "SEEN" as the value of an indicator named "SEENORNOT").

As an example, here is INTERSECT defined using this technique;

SYMBOLIC PROCEDURE INTERSECT(S1, S2);
   BEGIN SCALAR ANS, SET2;
   FLAG(S1, 'SEEN);
   SET2 := S2;
   WHILE SET2 DO <<
      IF FLAGP(CAR SET2, 'SEEN) THEN ANS := CAR SET2 . ANS;
      SET2 := CDR SET2 >>;
   REMFLAG(S1, 'SEEN);
   RETURN ANS
   END;

COMMENT  Perhaps you noticed that, having used a BEGIN-block, group,
loop, and assignments, I have not practiced what I preached about
using only function composition, conditional expressions, and
recursion during this lesson.  Well, now that you have had some
exposure to both extremes, I think you should always fairly
consider both together with appropriate compromises, in each case
choosing whatever is most clear, concise, and natural.  For set
operations based on the property-list approach, I find the style
exemplified immediately above most natural.

As your last exercise for this lesson, develop a file containing a
package for set operations based upon either property-lists or sorting.

This is the end of lesson 6.  When you are ready to run the final lesson
7, load a fresh copy of REDUCE.

;END;
