COMMENT



                 REDUCE INTERACTIVE LESSON NUMBER 4

                         David R. Stoutemyer
                        University of Hawaii


COMMENT This is lesson 4 of 7 REDUCE lessons.  As before, please
refrain from using variables beginning with the letters F through H
during the lesson.

In  theory, assignments and LET  statements are sufficient to
accomplish anything that any other practical computing mechanism is
capable of doing. However, it is more convenient for some purposes
to use function procedures which can employ branched selection and
iteration as do most traditional programming languages. As a trivial
example, if we invariably wanted to replace cotangents with the
corresponding tangents, we could type;

ALGEBRAIC PROCEDURE COT(X); 1/TAN(X);

COMMENT As an example of the use of this function, we have;

COT(LOG(F));

PAUSE;
COMMENT Note:
   1.  The procedure definition automatically declares the procedure
       name as an operator.
   2.  A procedure can be executed any time after its definition,
       until it is cleared.
   3.  Any parameters are dummy variables that are distinct from
       any other variables with the same name outside the procedure
       definition, and the corresponding arguments can be
       arbitrary expressions.
   4.  The value returned by a procedure is the value of the
       expression following the procedure statement.

We can replace this definition with a different one;

ALGEBRAIC PROCEDURE COT(Y); COS(Y)/SIN(Y);

G1:= COT(LOG(F));

COMMENT In place of the word ALGEBRAIC, we can optionally use the
word INTEGER when a function always returns an integer value, or we
can optionally use the word REAL when a function always returns a
floating-point value.

Try writing a procedure definition for the sine in terms of the
cosine, then type G1;

PAUSE;

COMMENT Here is a more complicated function which introduces the
notion of a conditional expression;

ALGEBRAIC PROCEDURE SUMCHECK(AJ, J, M, N, S);
   COMMENT  J is an indeterminate and the other parameters are
      expressions.  This function returns the global variable named
      PROVED if the function can inductively verify that S equals the
      sum of AJ for J going from M through N, returning the global
      variable named UNPROVED otherwise.  For the best chance of
      proving a correct sum, the function should be executed under
      the influence of ON EXP, ON MCD, and any other user-supplied
      simplification rules relevant to the expression classes of AJ
      and S;
   IF SUB(J=M,AJ)-SUB(N=M,S) NEQ 0
       OR S+SUB(J=N+1,AJ)-SUB(N=N+1,S) NEQ 0 THEN UNPROVED
    ELSE PROVED;

ON EXP, MCD;

CLEAR X, J, N;

SUMCHECK(J, J, 1, N, N*(N+1)/2);

SUMCHECK(X**J, J, 0, N, (X**(N+1)-1)/(X-1));

COMMENT Within procedures of this sort a global variable is any
variable which is not one of the parameters, and a global variable
has the value, if any, which is current for that name at the point
from where the procedure is used.

;PAUSE; COMMENT
Conditional expressions have the form

   IF condition THEN expression1 ELSE expression2.

There are generally several equivalent ways of writing a conditional
expression. For example, the body of the above procedure could have
been written

   IF SUB(J=M,A)-SUB(N=M,S)=0 AND S+SUB(J=N+1,A)-SUB(N=N+1,S)=0
      THEN PROVED
    ELSE UNPROVED.

Note how we compare a difference with 0, rather than comparing
two nonzero expressions, for reasons explained in lesson 3.

As an exercise, write a procedure analogous to SUMCHECK for proving
closed-form product formulas, then test it on the valid formula that
COS(N*X) equals the product of COS(J*X)/COS(J*X-X) for J ranging from
1 through N.  You do not need to include prefatory comments
describing parameters and the returned value until you learn how to
use a text editor;

PAUSE;

COMMENT Most REDUCE statements are also expressions because they have
a value. The value is usually 0 if nothing else makes sense, but I
will mention the value only if it is useful.

The value of an assignment statement is the assigned value. Thus a
multiple assignment, performed right to left, can be achieved by a
sequence of the form

    "variable1 := variable2 := ... := variableN := expression",

moreover, assignments can be inserted within ordinary expressions
such as X*(Y:=5). Such assignments must usually be parenthesized
because of the low precedence of the assignment operator, and
excessive use of this construct tends to make programs confusing.

;PAUSE;COMMENT

REDUCE treats as a single expression any sequence of statements
preceded by the pair of adjacent characters << and followed by the
pair >>.  The value of such a group expression is the value of the
last statement in the group.

Group expressions facilitate the implementation of tasks that are
most easily stated as a sequence of operations.  However, such
sequences often  utilize temporary  variables to  count,  hold
intermediate results, etc., and it is hazardous to use global
variables for that purpose. If a top-level REDUCE statement or
another function directly or indirectly uses that variable name, then
its value or its virgin indeterminate status there might be damaged
by our use as a temporary variable. In large programs or programs
which rely on the  work of others, such interference has a
nonnegligible probability, even if all programmers agree to the
convention that all such temporary variables should begin with the
function name as a prefix and all programmers attempt to comply with
the convention. For this reason, REDUCE provides another
expression-valued sequence called a BEGIN-block, which permits the
declaration of local variables that are distinct from any other
variables outside the block having the same name. Another advantage
of using local variables for temporary variables is that the perhaps
large amount of storage occupied by their values can be reclaimed
after leaving their block.

;PAUSE;COMMENT
A BEGIN-block consists of the word BEGIN, followed by optional
declarations, followed by a sequence of statements, followed by the
word END.  Within BEGIN-blocks, it is often convenient to return
control and a value from someplace other than the end of the block.
Control and a value may be returned via a
RETURN-statement of the form

         RETURN expression
or
          RETURN,

0 being returned in the latter case.  A BEGIN-block does not return
the value of the last statement.  If a value is to be returned RETURN
must be used.  These features and others are illustrated by the
following function;

PAUSE;

ALGEBRAIC PROCEDURE LIMIT(EX, INDET, PNT);
   BEGIN COMMENT This function uses up through 4 iterations of
      L'Hospital's rule to attempt determination of the limit of
      expression EX as indeterminate INDET approaches expression
      PNT.  This function is intended for the case where
      SUB(INDET=PNT, EX) yields 0/0, provoking a zero-divide
      message.  This function returns the global variable named
      UNDEFINED when the limit is 0 dividing an expression which did
      not simplify to 0, and this function returns the global
      variable named UNKNOWN when it cannot determine the limit.
      Otherwise this function returns an expression which is the
      limit. For best results, this function should be executed
      under the influence of ON EXP, ON MCD, and any user-supplied
      simplification rules appropriate to the expression classes of
      EX and PNT;
   INTEGER ITERATION;
   SCALAR N, D, NLIM, DLIM;
   ITERATION := 0;
   N := NUM(EX);
   D := DEN(EX);
   NLIM := SUB(INDET=PNT, N);
   DLIM := SUB(INDET=PNT, D);
   WHILE NLIM=0 AND DLIM=0 AND ITERATION<5 DO <<
      N := DF(N, INDET);
      D := DF(D, INDET);
      NLIM := SUB(INDET=PNT, N);
      DLIM := SUB(INDET=PNT, D);
      ITERATION := ITERATION + 1 >>;
   RETURN (IF NLIM=0 THEN
              IF DLIM=0 THEN UNKNOWN
              ELSE 0
           ELSE IF DLIM=0 THEN UNDEFINED
           ELSE NLIM/DLIM)
   END;

% Examples follow..
PAUSE;

G1 := (E**X-1)/X;

% Evaluation at 1, causes Zero denominator error at top level, continue
% anyway.
SUB(X=0, G1);

LIMIT(G1, X, 0);

G1:= ((1-X)/LOG(X))**2;

% Evaluation at 1, causes Zero denominator error at top level, continue
% anyway.
SUB(X=1, G1);

LIMIT(G1, X, 1);

COMMENT  Note:
   1.  The idea behind L'Hospital's rule is that as long as the
       numerator and denominator are both zero at the limit point, we
       can replace them by their derivatives without altering the
       limit of the quotient.
   2.  Assignments within groups and BEGIN-blocks do not
       automatically cause output.
   3.  Local variables are declared INTEGER, REAL, or SCALAR, the
       latter corresponding to the same most general class denoted by
       ALGEBRAIC in a procedure statement.  All local variables are
       initialized to zero, so they cannot serve as indeterminates.
       Moreover, if we attempted to overcome this by clearing them,
       we would clear all variables with their names.
   4.  We do not declare the attributes of parameters.
   5.  The NUM and DEN functions respectively extract the numerator
       and denominator of their arguments.  (With OFF MCD, the
       denominator of  1+1/X would be 1.)
   6.  The WHILE-loop has the general form

          WHILE condition DO statement.

       REDUCE also has a "GO TO" statement, and using commas rather
       than semicolons to prevent termination of this comment, the
       above general form of a WHILE-loop is equivalent to

          BEGIN  GO TO TEST,
       LOOP: statement,
       TEST: IF condition THEN GO TO LOOP,
          RETURN 0
          END  .

       A GOTO statement is permitted only within a block, and the
       GOTO statement cannot refer to a label outside the same block
       or to a label inside a block that the GOTO statement is not
       also within.  Actually, 99.99% of REDUCE BEGIN-blocks are less
       confusing if written entirely without GOTOs, and I mention
       them primarily to explain WHILE-loops in terms of a more
       primitive notion.

;PAUSE;COMMENT
   7.  The LIMIT function provides a good illustration of nested
       conditional expressions.  Proceeding sequentially through such
       nests, each ELSE clause is matched with the nearest preceding
       unmatched THEN clause in the group or block.  In order to help
       reveal their structure, I have consistently indented nested
       conditional statements, continuations of multi-line statements
       and loop-bodies according to one of the many staunchly
       defended indentation styles. However, older versions of REDUCE
       may ruin my elegant style.  If you have such a version, I
       encourage you to indent nonetheless, in anticipation of a
       replacement for your obsolete version.  (If you have an
       instructor, I also urge you to humor him by adopting his style
       for the duration of the course.)
   8.  PL/I programmers take note:  "IF ... THEN ... ELSE ..." is
       regarded as one expression, and semicolons are used to
       separate rather than terminate statements.  Moreover, BEGIN
       and END are brackets rather than statements, so a semicolon is
       never needed immediately after BEGIN, and a semicolon is
       necessary immediately preceding END only if the END is
       intended as a labeled destination for a GOTO. Within
       conditional expressions, an inappropriate semicolon after an
       END, a >>, or an ELSE-clause is likely to be one of your most
       prevalent mistakes.;
PAUSE;

COMMENT The next exercise is based on the above LIMIT function:

For the sum of positive expressions AJ for J ranging from some finite
initial value to infinity, the infinite series converges if the limit
of the ratio SUB(J=J+1,AJ)/AJ is less than 1 as J approaches
infinity.  The series diverges if this limit exceeds 1, and the test
is inconclusive if the limit is 1.  To convert the problem to the
form required by the above LIMIT program, we can replace J by the
indeterminate 1/!*FOO in the ratio, then take the limit as !*FOO
approaches zero. (Since an indeterminate is necessary here, I picked
the weird name !*FOO to make the chance of conflict negligible)

After writing such a function to perform the ratio test, test it on
the examples AJ=J/2**J, AJ=1/J**2, AJ=2**J/J**10, and AJ=1/J.  (The
first two converge and the second two diverge);

PAUSE;

COMMENT  Groups or blocks can be used wherever any arbitrary
expression is allowed, including the right-hand side of a LET rule.

The need for loops with an integer index variable running from a
given initial value through a given final value by a given increment
is so prevalent that REDUCE offers a convenient special way of
accomplishing it via a FOR-loop, which has the general form

   FOR index := initial STEP increment UNTIL final DO statement .

Except for the use of commas as statement separators, this construct
is equivalent to

   BEGIN INTEGER index,
   index := initial,
   IF increment>0 THEN WHILE index <= final DO <<
      statement,
      index := index + increment >>
   ELSE WHILE index >= final DO <<
      statement,
      index := index + increment >>,
   RETURN 0
   END .

;PAUSE;COMMENT
Note:
   1.  The index variable is automatically declared local to the FOR-
       loop.
   2.  "initial", "increment", and "final" must have integer values.
   3.  FORTRAN programmers take note:  the body of the loop is not
       automatically executed at least once.
   4.  An acceptable abbreviation for "STEP 1 UNTIL" is ":".
   5.  Since the WHILE-loop and the FOR-loop have implied BEGIN-
       blocks, a RETURN statement within their bodies cannot transfer
       control further than the point following the loops.

Another frequent need is to produce output from within a group or
block, because such output is not automatically produced. This can
be done using the WRITE-statement, which has the form

WRITE expression1, expression2, ..., expressionN.

Beginning a new line with expression1, the expressions are printed
immediately adjacent to each other, split over line boundaries if
necessary. The value of the WRITE-statement is the value of its last
expression, and any of the expressions can be a character-string
of the form "character1 character2 ... characterM" .

Inserting the word "WRITE" on a separate line before an assignment
is convenient for debugging, because the word is then easily deleted
afterward. These features and others are illustrated by the following
equation solver;

PAUSE;
OPERATOR SOLVEFOR, SOLN;

FOR ALL X, LHS, RHS LET SOLVEFOR(X, LHS, RHS) = SOLVEFOR(X, LHS-RHS);

COMMENT LHS and RHS are expressions such that P=NUM(LHS-RHS) is a
polynomial of degree at most 2 in the indeterminate or functional
form X.  Otherwise an error message is printed.  As a convenience,
RHS can be omitted if it is 0.  If P is quadratic in X, the two
values of X which satisfy P=0 are stored as the values of the
functional forms SOLN(1) and SOLN(2).  If P is a first-degree
polynomial in X, SOLN(1) is set to the one solution.  If P simplifies
to 0, SOLN(1) is set to the identifier ARBITRARY.  If P is an
expression which does not simplify to zero but does not contain X,
SOLN(1) is set to the identifier NONE.  In all other cases, SOLN(1)
is set to the identifier UNKNOWN.  The function then returns the
number of SOLN forms which were set.  This function prints a well
deserved warning message if the denominator of LHS-RHS contains X. If
LHS-RHS is not polynomial in X, it is wise to execute this function
under the influence of ON GCD;

PAUSE;
FOR ALL X, LHSMRHS LET SOLVEFOR(X, LHSMRHS) =
   BEGIN INTEGER HIPOW;  SCALAR TEMP, CFLIST, CF0, CF1, CF2;
   IF LHSMRHS = 0 THEN <<
      SOLN(1) := ARBITRARY;
      RETURN 1 >>;
   CFLIST :=  COEFF(LHSMRHS, X);
   HIPOW := HIPOW!*;
   IF HIPOW = 0 THEN <<
      SOLN(1) := NONE;
      RETURN 1 >>;
   IF HIPOW > 2 THEN <<
      SOLN(1) := UNKNOWN;
      RETURN 1 >>;
   IF HIPOW = 1 THEN <<
      SOLN(1) := FIRST(CFLIST)/SECOND(CFLIST);
      IF DF(SUB(X=!*FOO, SOLN(1)), !*FOO) NEQ 0 THEN
         SOLN(1) := UNKNOWN;
      RETURN 1 >>;
   CF0 := FIRST(CFLIST)/THIRD(CFLIST);
   CF1 := -SECOND(CFLIST)/THIRD(CFLIST)/2;
   IF DF(SUB(X=!*FOO, CF0), !*FOO) NEQ 0
         OR DF(SUB(X=!*FOO, CF1), !*FOO) NEQ 0  THEN <<
      SOLN(1) := UNKNOWN;
      RETURN 1 >>;
   TEMP := (CF1**2 - CF0)**(1/2);
   SOLN(1) := CF1 + TEMP;
   SOLN(2) := CF1 - TEMP;
   RETURN 2
   END;

COMMENT And some examples;
PAUSE;

FOR K:=1:SOLVEFOR(X, A*X**2, -B*X-C) DO WRITE SOLN(K) := SOLN(K);

FOR K:=1:SOLVEFOR(LOG(X), 5*LOG(X)-7) DO WRITE SOLN(K) := SOLN(K);

FOR K:=1:SOLVEFOR(X, X, X) DO WRITE SOLN(K) := SOLN(K);

FOR K:= 1:SOLVEFOR(X, 5) DO WRITE SOLN(K) := SOLN(K);

FOR K:=1:SOLVEFOR(X, X**3+X+1) DO WRITE SOLN(K) := SOLN(K);

FOR K:=1:SOLVEFOR(X, X*E**X, 1) DO WRITE SOLN(K) := SOLN(K);

G1 := X/(E**X-1);

%Results in 'invalid as POLYNOMIAL' error, continue anyway;
FOR K:=1:SOLVEFOR(X, G1) DO WRITE SOLN(K) := SOLN(K);

SUB(X=SOLN(1), G1);

LIMIT(G1, X, SOLN(1));

PAUSE;

COMMENT Here we have used LET rules to permit the user the
convenience of omitting default arguments. (Function definitions have
to have a fixed number of parameters.)

Array elements are designated by the same syntax as matrix elements
and as functional forms having integer arguments. Here are some
desiderata that may help you decide which of these alternatives is
most appropriate for a particular application:
   1.  The lower bound of each array subscript is 0, vs. 1 for
       matrices vs unrestricted for functional forms.
   2.  The upper bound of each array subscript must have a specific
       integer value at the time the array is declared, as must the
       upper bounds of matrix subscripts when a matrix is first
       referred to, on the left side of a matrix assignment.  In
       contrast, functional forms never require a commitment to a
       specific upper bound.
   3.  An array can have any fixed number of subscripts, a matrix
       must have exactly 2, and a functional form can have a varying
       arbitrary number.
   4.  Matrix operations, such as transpose and inverse, are built-in
       only for matrices.
   5.  For most implementations, access to array elements requires
       time approximately proportional to the number of subscripts,
       whereas access to matrix elements takes time approximately
       proportional to the sum of the two subscript values, whereas
       access to functional forms takes average time approximately
       proportional to the number of bound functional forms having
       that name.
   6.  Only functional forms permit the effect of a subscripted
       indeterminate such as having an answer be "A(M,N) + B(3,4)".
   7.  Only functional forms can be used alone in the LHS of LET
       substitutions.
;PAUSE; COMMENT
   8.  All arrays, matrices, and operators are global regardless
       of where they are declared, so declaring them within a BEGIN
       block does not afford the protection and automatic storage
       recovery of local variables.  Moreover, clearing them within a
       BEGIN-block will clear them globally, and functions
       cannot return an array or a matrix value.  Furthermore, REDUCE
       parameters are referenced by value, which means that an
       assignment to a parameter has no effect on the corresponding
       argument.  Thus, matrix or array results cannot be transmitted
       back to an argument either.
   9.  It is often advantageous to use two or more of these
       alternatives to represent a set of quantities at different
       times in the same program. For example, to get the general
       form of the inverse of a 3-by-3 matrix, we could write

          MATRIX AA,
          OPERATOR A,
          AA := MAT((0,0,0),(0,0,0),(0,0,0)),
          FOR J:=1:3 DO
             FOR K:=1:3 DO AA(J,K) := A(J,K),
          AA**-1 .

       As another example, we might use an array to receive some
       polynomial coefficients, then transfer the values to a matrix
       for inversion.

;PAUSE;COMMENT
The COEFF function is the remaining new feature in our SOLVEFOR
example.  The first argument is a polynomial expression in the
indeterminate or functional form which is the second argument.  The
polynomial coefficients of the integer powers of the indeterminate are
returned as a LIST, with the independent coefficient first.  The
highest and lowest non-zero powers are placed in the variables HIPOW!*
and LOWPOW!* respectively.

A LIST is a kind of data structure, just as matrices and arrays are.
It is represented as comma separated list of elements enclosed in
braces.  The elements can be accessed with the functions FIRST,
SECOND, THIRD, PART(i) which returns the i-th element, and REST, which
returns a list of all but the first element.  For example;

CLEAR X;

COEFF(X**5+2, X);

LOWPOW!*;

HIPOW!*;

PAUSE;

COMMENT COEFF does not check to make sure that the coefficients do not
contain its second argument within a functional form, so that is the
reason we differentiated.  The reason we first substituted the
indeterminate !*FOO for the second argument is that differentiation
does not work with respect to a functional form.

The last exercise is to rewrite the last rule so that we can solve
equations which simplify to the form

   a*x**(m+2*l) + b*x**(m+l) + c*x**m = 0,   where m>=0 and l>=1.

The solutions are

   0,  with multiplicity m,
   x1*E**(2*j*I*pi/l),
   x2*E**(2*j*I*pi/l),   with j = 0, 1, ..., l-1,

where x1 and x2 are the solutions to the quadratic equation

   a*x**2 + b*x + c = 0 .

As a convenience to the user, you might also wish to have a global
switch named SOLVEPRINT, such that when it is nonzero, the solutions
are automatically printed.

This is the end of lesson 4. When you are ready to run lesson 5,
start a new REDUCE job.

;END;
