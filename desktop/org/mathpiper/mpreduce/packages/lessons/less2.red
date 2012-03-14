COMMENT

                 REDUCE INTERACTIVE LESSON NUMBER 2

                         David R. Stoutemyer
                        University of Hawaii


COMMENT This is lesson 2 of 7 REDUCE lessons.  Please refrain from
using variables beginning with the letters F through H during the
lesson.

By now you have probably had the experience of generating an
expression, and then having to repeat the calculation because you
forgot to assign it to a variable or because you did not expect to
want to use it later.  REDUCE maintains a history of all inputs and
computation during an interactive session. (Note, this is only for
interactive sessions.) To use an input expression in a new
computation, you can say

        INPUT(n)

where n is the appropriate command number.  The evaluated computations
can be accessed through

        WS(n)    or simply WS

if you wish to refer to the last computation.  WS stands for Work Space.
As with all REDUCE expressions, these can also be used to create new
expressions:

        (INPUT(n)/WS(n2))**2

Special characters can be used to make unique REDUCE variable names
that reduce the chance of accidental interference with any other
variables.  In general, whenever you want to include an otherwise
forbidden character such as * in a name, merely precede it by an
exclamation point, which is called the escape character.  However,
pick a character other than "*", which is used for many internal
REDUCE names.  Otherwise, if most of us use "*" the purpose will be
defeated;

G+!%H;
WS;
PAUSE;

COMMENT You can also name the expression in the workspace by using
the command SAVEAS, for example:;

SAVEAS GPLUSH;
GPLUSH;
PAUSE;

COMMENT You may have noticed that REDUCE imposes its own order on the
indeterminates and functional forms that appear in results, and that
this ordering can strongly affect the intelligibility of the results.
For example:;

G1:= 2*H*G + E + F1 + F + F**2 + F2 + 5 + LOG(F1) + SIN(F1);

COMMENT The ORDER declaration permits us to order indeterminates and
functional forms as we choose. For example, to order F2 before F1,
and to order F1 before all remaining variables:;

ORDER F2, F1;
G1;
PAUSE;

COMMENT Now suppose we partially change our mind and decide to
order LOG(F1) ahead of F1;

ORDER LOG(F1), F1;
G1;

COMMENT Note that any other indeterminates or functional forms under
the influence of a previous ORDER declaration, such as F2, rank
before those mentioned in the later declaration.  Try to determine
the default ordering algorithm used in your REDUCE implementation, and
try  to achieve some delicate  rearrangements using the ORDER
declaration.;

PAUSE;

COMMENT You may have also noticed that REDUCE factors out any
number, indeterminate, functional form, or the largest integer power
thereof which exactly divides every term of a result or every term of
a parenthesized subexpression of a result. For example:;

ON EXP, MCD;
G1:= F**2*(G**2 + 2*G) + F*(G**2+H)/(2*F1);

COMMENT This process usually leads to more compact expressions and
reveals important structural information. However, the process can
yield results which are difficult to interpret if the resulting
parentheses are nested more than about two levels, and it is often
desirable to see a fully expanded result to facilitate direct
comparison of all terms. To suppress this monomial factoring, we can
turn off an output control switch named ALLFAC;

OFF ALLFAC;
G1;
PAUSE;

COMMENT The ALLFAC monomial-factorization process is strongly
dependent upon the ordering.  We can achieve a more selective monomial
factorization by using the FACTOR declaration, which declares a
variable to have FACTOR status.  If any indeterminates or functional
forms occurring in an expression are in FACTOR status when the
expression is printed, terms having the same powers of the
indeterminates or functional forms are collected together, and the
power is factored out.  Terms containing two or more indeterminates or
functional forms under FACTOR status are not included in this monomial
factorization process.  For example:;

OFF ALLFAC; FACTOR F; G1;
FACTOR G; G1; PAUSE;

COMMENT We can use the REMFAC command to remove items from factor
status;

REMFAC F;
G1;

COMMENT ALLFAC can still have an effect on the coefficients of the
monomials that have been factored out under the influence of FACTOR:;

ON ALLFAC;
G1;
PAUSE;

COMMENT It is often desirable to distribute denominators over all
factored subexpressions generated under the influence of a FACTOR
declaration, such as when we wish to view a result as a polynomial or
as a power series in the factored indeterminates or functional forms,
with  coefficients which are rational  functions of any other
indeterminates or functional forms.  (A mnemonic aid is: think RAT
for RATional-function coefficients.) For example:;

ON RAT;
G1;
PAUSE;

COMMENT RAT has no effect on expressions which have no
indeterminates or functional forms under the influence of FACTOR.
The related but different DIV switch permits us to distribute numerical
and monomial factors of the denominator over every term of the
numerator, expressing these distributed portions as rational-number
coefficients and negative power factors respectively. (A mnemonic
aid: DIV DIVides by monomials.) The overall effect can also depend
strongly on whether the RAT switch is on or off.  Series and
polynomials are often most attractive with RAT and DIV both on;

ON DIV, RAT;
G1;
OFF RAT;
G1;
PAUSE;

REMFAC G;
G1;
PAUSE;

COMMENT With a very complicated result, detailed study of the result
is often facilitated by having each new term begin on a new line,
which can be accomplished using the LIST switch:;

ON LIST;
G1;
PAUSE;

COMMENT  In various combinations, ORDER, FACTOR, the computational
switches EXP, MCD, GCD, and ROUNDED, together with the output control
switches ALLFAC, RAT, DIV, and LIST provide a variety of output
alternatives. With experience, it is usually possible to use these
tools to produce a result in the desired form, or at least in a form
which is far more acceptable than the one produced by the default
settings.  I encourage you to experiment with various combinations
while this information is fresh in your mind;

PAUSE;
OFF LIST, RAT, DIV, GCD, ROUNDED;
ON ALLFAC, MCD, EXP;

COMMENT You may have wondered whether or not an assignment to a
variable, say F1, automatically updates the value of a bound
variable, say G1, which was previously assigned an expression
containing F1. The answer is:

   1.  If F1 was a bound variable in the expression when it was set
       to G1, then subsequent changes to the value of F1 have no
       effect on G1 because all traces of F1 in G1 disappeared after
       F1 contributed its value to the formation of G1.
   2.  If F1 was an indeterminate in an expression previously
       assigned to G1, then for each subsequent use of G1, F1
       contributes its current value at the time of that use.

These phenomena are illustrated by the following sequence:;

PAUSE;
F2 := F;
G1 := F1 + F2;
F2 := G;
G1;
F1 := G;
F1 := H;
G1;
F1 := G;
G1;

COMMENT  Experience indicates that it is well worth studying this
sequence and experimenting with others until these phenomena are
thoroughly understood. You might, for example, mimic the above
example, but with another level of evaluation included by inserting a
statement analogous to "Q9:=G1" after "F2:=G", and inserting an
expression analogous to "Q9" at the end, to compare with G1. ;

PAUSE;
COMMENT Note also, that if an indeterminate is used directly, or
indirectly through another expression, in evaluating itself, this will
lead to an infinite recursion.  For example, the following expression
results in infinite recursion at the first evaluation of H1.  On some
machines (Vax/Unix, IBM) this will cause REDUCE to terminate abnormally.

        H1 := H1 + 1

You may experiment with this problem, later at your own risk.

It is often desirable to make an assignment to an indeterminate in a
previously established expression have a permanent effect, as if the
assignment were done before forming the expression.  This can be done by
using the substitute function, SUB.

G1 := F1 + F2;

H1 := SUB(F1=H, G1);
F1 := G;
H1;

COMMENT Note the use of "=" rather than ":=" in SUB. This function
is also valuable for achieving the effect of a local assignment
within a subexpression, without binding the involved indeterminate or
functional form in the rest of the expression or wherever else it
occurs. More generally the SUB function can have any number of
equations of  the form  "indeterminate or  functional form  =
expression", separated by commas, before the expression which is its
last argument. Try devising a set of examples which reveals whether
such multiple substitutions are done left to right, right to left, in
parallel, or unpredictably.

This is the end of lesson 2. To execute lesson 3, start a fresh
REDUCE job.

;END;
