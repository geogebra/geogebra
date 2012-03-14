% Test of Rlisp88 version of Rlisp. Many of these functions are taken
% from the solved exercises in the book "RLISP '88: An Evolutionary
% Approach to Program Design and Reuse".

% Author: Jed B. Marti.

on rlisp88;

% Confidence test tries to do a little of everything. This doesn't really
% test itself so  you need to compare to the log file. Syntax errors on
% the other hand should be cause for alarm.

%                             ARRAYS
% 1. Single dimension array.
global '(v1);
v1 := mkarray 5;
for i:=0:5 do v1[i] := 3**i;
v1;


% 2. 2D array.
global '(v3x3);
v3x3 := mkarray(2, 2);
for row := 0:2 do
  for col := 0:2 do
     v3x3[row, col] := if row = col then 1.0 else 0.0;
v3x3;

% 3. Triangular array.
global '(tri);
tri := mkarray 3;
for row := 0:3 do tri[row] := mkarray row;
for row := 0:3 do
  for col := 0:row do
     tri[row,col] := row * col;
tri;

% 4. ARRAY test.
expr procedure rotate theta;
/* Generates rotation array for angle theta (in radians) */
array(array(cosd theta, - sind theta, 0.0),
      array(sind theta, cosd theta, 0.0),
      array(0.0, 0.0, 1.0));
rotate 45.0;

% 5. Random elements.
% Now create a vector with random elements.
M3 := ARRAY('A, 3 + 4, ARRAY("String", 'ID), '(a b));
M3[2, 1];
M4 := ARRAY(ARRAY('a, 'b), ARRAY('c, 'd));
M4[1];

% 6. Array addition.
expr procedure ArrayAdd(a, b);
if vectorp a then
    for i:=0:uc
        with c, uc
        initially c :=  mkarray(uc := upbv a)
        do c[i] := ArrayAdd(a[i], b[i])
        returns c
  else a + b;
ArrayAdd(array(array(array(1, 2), array(3, 4)),
               array(array(5, 6), array(7, 8))),
         array(array(array(1, 1), array(2, 2)),
               array(array(3, 3), array(4, 4))));

%                               RECORDS
% 1: Declaration.
RECORD MAPF  /* A MAPF record defines
               the contents of a MAPF file. */
  WITH 
  MAPF!:NAME := ""        /* Name of MAPF (a string) */,
  MAPF!:NUMBER := 0       /* MAPF number (integer) */,
  MAPF!:ROAD-COUNT := 0   /* Number of roads */,
  MAPF!:NODE-COUNT := 0   /* Number of nodes */,
  MAPF!:LLAT := 0.0       /* Lower left hand corner map latitude */,
  MAPF!:LLONG := 0.0      /* Lower left hand corner map longitude */,
  MAPF!:ULAT := 0.0       /* Upper right hand corner map latitude */,
  MAPF!:ULONG := 0.0      /* Upper right hand corner map longitude */;

% 2: Creation.
global '(r1 r2 r3);
r1 := mapf();
r2 := mapf(mapf!:name := "foobar", mapf!:road-count := 34);
r3 := list('a . r1, 'b . r2);

% 3: Accessing.
mapf!:number r1;
mapf!:road-count cdr assoc('b, r3);

% 4: Assignment.
mapf!:number r1 := 7622;
mapf!:road-count cdr assoc('b, r3) := 376;
mapf!:node-count(mapf!:name r2 := mapf()) := 34;
r2;


% 5. Options.
RECORD complex /* Stores complex reals */
  WITH
   R := 0.0  /* Real part */,
   I := 0.0  /* Imaginary part */
  HAS CONSTRUCTOR;
Make-Complex(I := 34.0, R := 12.0);

RECORD Rational /* Representation of rational numbers */
  WITH
    Num := 0  /* Numerator */,
    Den := 1  /* Denominator */
  HAS CONSTRUCTOR = rat;

expr procedure gcd(p, q);
if q > p then gcd(q, p)
else (if r = 0 then q else gcd(q, r)) where r = remainder(p,q);

expr procedure Rational(a, b);
/* Build a rational number in lowest terms */
  Rat(Num := a / g, Den := b / g) where g := gcd(a, b);
Rational(34, 12);


RECORD Timing /* Timing Record for RLISP test */
  WITH
    Machine := ""    /* Machine name */,
    Storage := 0     /* Main storage in bits */,
    TimeMS = 0       /* Test time in milliseconds */
  HAS NO CONSTRUCTOR;



% PREDICATE option.
RECORD History /* Record of an event */
  WITH
    EventTime := 0.0   /* Time of event (units) */,
    EventData := NIL   /* List with (type ...) */
  HAS PREDICATE = History!?;

History!? History(EventData := '(MOVE 34.5 52.5));


%                            FOR LOOP
% 1) Basic test.
EXPR PROCEDURE LPRINT lst;
/* LPRINT displays each element of its argument separated by blanks.
   After the last element has been displayed, the print line is
   terminated. */
FOR EACH element IN lst 
    DO << PRIN2 element; PRINC " " >>
    FINALLY TERPRI()
    RETURNS lst;
LPRINT '(Now is the time to use RLISP);

% 2) Basic iteration in both directions.
FOR i:=5 STEP -2 UNTIL 0 DO PRINT i;
FOR i:=1:3 DO PRINT i;

% 3) COLLECT option.
FOR EACH leftpart IN '(A B C)
    EACH rightpart IN '(1 2 "string")
    COLLECT leftpart . rightpart;

% 4) IN/ON iterators.
FOR EACH X IN '(a b c) DO PRINT x;
FOR EACH x ON '(a b c) DO PRINT x;


% 5) EVERY option.
FOR EACH x IN '(A B C) EVERY IDP x
    RETURNS "They are all id's";
FOR EACH x IN '(A B 12) EVERY IDP x
    RETURNS "They are all id's";

% 6) INITIALLY/FINALLY option.
EXPR PROCEDURE ListPrint x;
/* ListPrint(x) displays each element of x separated by blanks. The
   first element is prefixed with "*** ". The last element is suffixed
   with a period and a new line. */
FOR EACH element ON x 
    INITIALLY PRIN2 "*** "
    DO << PRIN2 CAR element;
          IF CDR element THEN PRIN2 " " >>
    FINALLY << PRIN2 "."; TERPRI() >>;
ListPrint '(The quick brown bert died);


% 7) MAXIMIZE/MINIMIZE options.
FOR EACH x IN '(A B 12 -34 2.3)
    WHEN NUMBERP x
    MAXIMIZE x;
FOR EACH x IN '(A B 12 -34 2.3)
    WHEN NUMBERP x
    MINIMIZE x;


% 8) RETURNS option.
EXPR PROCEDURE ListFiddle(f, x);
/* ListFiddle displays every element of its second argument and returns
   a list of those for which the first argument returns non-NIL. */
FOR EACH element IN x
   WITH clist
   DO << PRINT element;
         IF APPLY(f, LIST element) THEN clist := element . clist >>
   RETURNS REVERSIP clist;
ListFiddle(FUNCTION ATOM, '(a (BANG 12) "OOPS!"));


% 9) SOME option.
FOR EACH x IN '(a b 12) SOME NUMBERP x
    DO PRINT x;

% 10) UNTIL/WHILE options.
EXPR PROCEDURE CollectUpTo l;
/* CollectUpTo collect all the elements of the list l up to the
   first number. */
FOR EACH x IN l UNTIL NUMBERP x COLLECT x;
CollectUpTo '(a b c 1 2 3);

% 11) WHEN/UNLESS options.
FOR EACH x IN '(A 12 "A String" 32)
    WHEN NUMBERP x
    COLLECT x;


% ##### Basic Tests #####
% Tests some very basic things that seem to go wrong frequently.

% Numbers.
if +1 neq 1 then error(0, "+1 doesn't parse");
if -1 neq - 1 then error(0, "-1 doesn't parse");

expr procedure factorial n;
if n < 2 then 1 else n * factorial(n - 1);

if +2432902008176640000 neq factorial 20 then 
    error(0, "bignum + doesn't work");
if -2432902008176640000 neq - factorial 20 then
    error(0, "bignum - doesn't work");

% This actually blew up at one time.
if -3.14159 neq - 3.14159 then error(0, "negative floats don't work");
if +3.14159 neq 3.14159 then error(0, "positive floats don't work");


% ##### Safe Functions #####

% Description: A set of CAR/CDR alternatives that
%    return NIL when CAR/CDR of an atom is tried.

expr procedure SafeCar x;
/* Returns CAR of a list or NIL. */
if atom x then nil else car x;

expr procedure SafeCdr x;
/* Returns CDR of a list or NIL. */
if atom x then nil else cdr x;

expr procedure SafeFirst x; SafeCar x;
expr procedure SafeSecond x; SafeCar SafeCdr x;
expr procedure SafeThird x; SafeSecond SafeCdr x;


% ##### Test of Procedures #####

%------------------------- Exercise #1 -------------------------

expr procedure delassoc(x, a);
/* Delete the element from x from the alist a non-destructively. Returns
 the reconstructed list. */
if null a then nil
  else if atom a then a . delassoc(x, cdr a)
  else if caar a = x then cdr a
  else car a . delassoc(x, cdr a);


if delassoc('a, '((a b) (c d))) = '((c d)) 
   then "Test 1 delassoc OK"
   else error(0, "Test 1 delassoc failed");

if delassoc('b, '((a b) (b c) (c d))) = '((a b) (c d))
   then "Test 2 delassoc OK"
   else error(0, "Test 2 delassoc failed");

if delassoc('c, '((a b) (b c) (c d))) = '((a b) (b c))
   then "Test 3 delassoc OK"
   else error(0, "Test 3 delassoc failed");

if delassoc('d, '((a b) (b c) (c d))) = '((a b) (b c) (c d))
   then "Test 4 delassoc OK"
   else error(0, "Test 4 delassoc failed");


%------------------------- Exercise #2 -------------------------
expr procedure gcd(u, v);
if v = 0 then u else gcd(v, remainder(u, v));

if gcd(2, 4) = 2 then "Test 1 GCD OK" else error(0, "Test 1 GCD fails");
if gcd(13, 7) = 1
  then "Test 2 GCD OK" else error(0, "Test 2 GCD fails");
if gcd(15, 10) = 5
  then "Test 3 GCD OK" else error(0, "Test 3 GCD fails");
if gcd(-15, 10) = -5
  then "Test 4 GCD OK" else error(0, "Test 4 GCD fails");
if gcd(-15, 0) = -15
  then "Test 5 GCD OK" else error(0, "Test 5 GCD fails");


%-------------------- Exercise #3 --------------------
expr procedure properintersection(a, b);
/* Returns the proper intersection of proper sets a and b.
  The set representation is a list of elements with the
  EQUAL relation. */
if null a then nil
  else if car a member b then car a . properintersection(cdr a, b)
  else properintersection(cdr a, b);

% Test an EQ intersection.
properintersection('(a b), '(b c));
if properintersection('(a b), '(b c)) = '(b)
     then "Test 1 properintersection OK"
     else error(0, "Test 1 properintersection fails");

% Test an EQUAL intersection.
properintersection('((a) b (c)), '((a) b (c)));
if properintersection('((a) b (c)), '((a) b (c))) = '((a) b (c))
     then "Test 2 properintersection OK"
     else error(0, "Test 2 properintersection fails");

% Test an EQUAL intersection, out of order.
properintersection('((a) b (c)), '(b (c) (a)));
if properintersection('((a) b (c)), '(b (c) (a))) = '((a) b (c))
     then "Test 3 properintersection OK"
     else error(0, "Test 3 properintersection fails");

% Test an empty intersection.
properintersection('((a) b (c)), '(a (b) c));
if properintersection('((a) b (c)), '(a (b) c)) = nil
     then "Test 4 properintersection OK"
     else error(0, "Test 4 properintersection fails");



%-------------------- Exercise #4 -------------------------

expr procedure TreeVisit(a, tree, c);
/* Preorder visit of tree to find a. Returns path from root. c
  contains path to root of tree so far. */
if null tree then nil
  else if a = car tree then append(c, {a})
  else TreeVisit(a, cadr tree, append(c, {car tree})) or
       TreeVisit(a, caddr tree, append(c, {car tree}));

TreeVisit('c, '(a (b (d nil nil) (c nil nil)) (e nil nil)), nil);
if TreeVisit('c, '(a (b (d nil nil) (c nil nil)) (e nil nil)), nil)
             = '(a b c)
     then "Test 1 TreeVisit OK"
     else error(0, "Test 1 TreeVisit fails");

TreeVisit('h, '(a (b (d nil nil) (c nil nil))
                  (e (f nil nil) (g (h nil nil) nil)) ), nil);
if TreeVisit('h, '(a (b (d nil nil) (c nil nil))
                  (e (f nil nil) (g (h nil nil) nil))),nil) = '(a e g h)
     then "Test 2 TreeVisit OK"
     else error(0, "Test 2 TreeVisit fails");

if TreeVisit('i, '(a (b (d nil nil) (c nil nil))
                  (e (f nil nil) (g (h nil nil) nil)) ), nil) = nil
     then "Test 3 TreeVisit OK"
     else error(0, "Test 3 TreeVisit fails");

if TreeVisit('a, '(a (b (d nil nil) (c nil nil))
                  (e (f nil nil) (g (h nil nil) nil)) ), nil) = '(a)
     then "Test 4 TreeVisit OK"
     else error(0, "Test 4 TreeVisit fails");

if TreeVisit('e, '(a (b (d nil nil) (c nil nil))
                  (e (f nil nil) (g (h nil nil) nil)) ), nil) = '(a e)
     then "Test 5 TreeVisit OK"
     else error(0, "Test 5 TreeVisit fails");


%-------------------- Exercise #5 ------------------------- 

expr procedure lookfor(str, l);
/* Search for the list str (using =) in the top level
   of list l. Returns str and remaining part of l if
   found. */
if null l then nil
  else if lookfor1(str, l) then l
  else lookfor(str, cdr l);

expr procedure lookfor1(str, l);
if null str then t
  else if null l then nil
  else if car str = car l then lookfor1(cdr str, cdr l);


if lookfor('(n o w),'(h e l l o a n d n o w i t i s)) = '(n o w i t i s)
  then "Test 1 lookfor OK"
  else error(0, "Test 1 lookfor fails");

if lookfor('(now is), '(now we have nothing is)) = NIL
  then "Test 2 lookfor OK"
  else error(0, "Test 2 lookfor fails");

if lookfor('(now is), '(well hello!, now)) = NIL
  then "Test 3 lookfor OK"
  else error(0, "Test 3 lookfor fails");


%-------------------- Exercise #6 ------------------------- 

expr procedure add(a, b, carry, modulus);
/* Add two numbers stored as lists with digits of
  modulus. Carry passes the carry around. Tries to
  suppress leading 0's but fails with negatives. */
if null a then
  if null b then if zerop carry then nil
                   else {carry}
  else remainder(carry + car b, modulus) .
        add(nil, cdr b, (carry + car b) / modulus, modulus)
else if null b then add(b, a, carry, modulus)
else remainder(car a + car b + carry, modulus) .
      add(cdr a, cdr b, (car a + car b + carry) / modulus,
          modulus);


if add('(9 9), '(9 9), 0, 10) = '(8 9 1)
  then "Test 1 add OK"
  else error(0, "Test 1 add fails");

if add('(-9 -9), '(9 9), 0, 10) = '(0 0)
  then "Test 2 add OK"
  else error(0, "Test 2 add fails");

if add('(9 9 9), '(9 9 9 9), 0, 10) = '(8 9 9 0 1)
  then "Test 3 add OK"
  else error(0, "Test 3 add fails");

if add('(99 99 99), '(99 99 99 99), 0, 100) = '(98 99 99 0 1)
  then "Test 4 add OK"
  else error(0, "Test 4 add fails");

if add('(13 12), '(15 1), 0, 16) = '(12 14)
  then "Test 5 add OK"
  else error(0, "Test 5 add fails");


%-------------------- Exercise #7 ------------------------- 

expr procedure clength(l, tmp);
/* Compute the length of the (possibly circular) list l.
  tmp is used to pass values looked at down the list. */
if null l or l memq tmp then 0
  else 1 + clength(cdr l, l . tmp);

if clength('(a b c), nil) = 3
  then "Test 1 clength OK"
  else error(0, "Test 1 clength fails");


<< xxx := '(a b c); cdr lastpair xxx := xxx; nil >>;

if clength(xxx, nil) = 3
  then "Test 2 clength OK"
  else error(0, "Test 1 clength fails");

if clength(append('(a b c), xxx), nil) = 6
  then "Test 3 clength OK"
  else error(0, "Test 1 clength fails");

%------------------------- Exercise #8 -------------------------

expr procedure fringe x;
/* FRINGE(X) -- returns the fringe of X (the atoms at the
  end of the tree structure of X). */
if atom x then {x}
  else if cdr x then append(fringe car x, fringe cdr x)
  else fringe car x;

if fringe nil = '(NIL)
  then "Test 1 fringe OK"
  else error(0, "Test 1 fringe fails");

if fringe '(a b . c) = '(a b c)
  then "Test 2 fringe OK"
  else error(0, "Test 2 fringe fails");

if fringe '((((a) . b) (c . d)) . e) = '(a b c d e)
  then "Test 3 fringe OK"
  else error(0, "Test 3 fringe fails");


%------------------------- Exercise #9 -------------------------
expr procedure delall(x, l);
/* DELALL(X, L) -- Delete all X's from the list L using EQUAL
  test. The list is reconstructed. */
if null l then nil
  else if x = car l then delall(x, cdr l)
  else car l . delall(x, cdr l);

if delall('X, nil) = NIL
  then "Test 1 delall OK"
  else error(0, "Test 1 delall fails");

if delall('X, '(X)) = NIL
  then "Test 2 delall OK"
  else error(0, "Test 2 delall fails");

if delall('X, '(A)) = '(A)
  then "Test 3 delall OK"
  else error(0, "Test 3 delall fails");

if delall('(X B), '(A (B) (X B))) = '(A (B))
  then "Test 4 delall OK"
  else error(0, "Test 4 delall fails");

if delall('(X B), '((X B) (X B))) = NIL
  then "Test 5 delall OK"
  else error(0, "Test 5 delall fails");

if delall('(X B), '((X B) X B (X B))) = '(X B)
  then "Test 6 delall OK"
  else error(0, "Test 6 delall fails");


% ------------------------- Exercise #10 -------------------------
expr procedure startswith(prefix, word);
/* STARTSWITH(PREFIX, WORD) -- Returns T if the list of
  characters WORD begins with the list of characters PREFIX. */
if null prefix then T
  else if word then
     if car prefix eq car word then
         startswith(cdr prefix, cdr word);

if startswith('(P R E), '(P R E S I D E N T)) = T
  then "Test 1 startswith OK!"
  else error(0, "Test 1 startswith fails");

if startswith('(P R E), '(P O S T F I X)) = NIL
  then "Test 2 startswith OK!"
  else error(0, "Test 2 startswith fails");

if startswith('(P R E), '(P R E)) = T
  then "Test 3 startswith OK!"
  else error(0, "Test 3 startswith fails");

if startswith('(P R E), '(P R)) = NIL
  then "Test 4 startswith OK!"
  else error(0, "Test 4 startswith fails");

if startswith('(P R E), NIL) = NIL
  then "Test 5 startswith OK!"
  else error(0, "Test 5 startswith fails");

if startswith('(P R E), '(P P R E)) = NIL
  then "Test 6 startswith OK!"
  else error(0, "Test 6 startswith fails");


% ##### Test of Definitions #####

%------------------------- Exercise #1 -------------------------
expr procedure goodlist l;
/* GOODLIST(L) - returns T if L is a proper list. */
if null l then T
  else if pairp l then goodlist cdr l;

if goodlist '(a b c) = T
  then "Test 1 goodlist OK"
  else error(0, "Test 1 goodlist fails");

if goodlist nil = T
  then "Test 2 goodlist OK"
  else error(0, "Test 2 goodlist fails");

if goodlist '(a . b) = NIL
  then "Test 3 goodlist OK"
  else error(0, "Test 3 goodlist fails");


%------------------------- Exercise #2 -------------------------
expr procedure fmember(a, b, fn);
/* FMEMBER(A, B, FN) - Returns rest of B is A is a member
  of B using the FN of two arguments as an equality check. */
if null b then nil
  else if apply(fn, {a, car b}) then b
  else fmember(a, cdr b, fn);

if fmember('a, '(b c a d), function EQ) = '(a d)
  then "Test 1 fmember is OK"
  else error(0, "Test 1 fmember fails");

if fmember('(a), '((b c) (a) d), function EQ) = NIL
  then "Test 2 fmember is OK"
  else error(0, "Test 2 fmember fails");

if fmember('(a), '((b c) (a) d), function EQUAL) = '((a) d)
  then "Test 3 fmember is OK"
  else error(0, "Test 3 fmember fails");

if fmember(34, '(1 2 56 12), function LESSP) = '(56 12)
  then "Test 4 fmember is OK"
  else error(0, "Test 4 fmember fails");

%------------------------- Exercise #3-4 -------------------------
expr procedure findem(l, fn);
/* FINDEM(L, FN) - returns a list of elements in L that satisfy
  the single argument function FN. */
if null l then nil
  else if apply(fn, {car l}) then car l . findem(cdr l, fn)
  else findem(cdr l, fn);

if findem('(a 1 23 b "foo"), function idp) = '(a b)
  then "Test 1 findem OK!"
  else error(0, "Test 1 findem fails");

if findem('(1 3 a (44) 12 9),
          function (lambda x; numberp x and x < 10)) = '(1 3 9)
  then "Test 2 findem OK!"
  else error(0, "Test 2 findem fails");



%------------------------- Exercise #5 -------------------------
expr procedure insert(a, l, f);
/* Insert the value a into list l based on the partial ordering function
  f(x,y). Non-destructive insertion. */
if null l then {a}
  else if apply(f, {car l, a}) then a . l
  else car l . insert(a, cdr l, f);


% Basic ascending order sort.
insert(6, '(1 5 10), function geq);
if insert(6, '(1 5 10), function geq) = '(1 5 6 10)
     then "Test 1 insert (>=) OK"
     else error(0, "Test 1 insert (>=) fails");

% Try inserting element at end of list.
insert(11, '(1 5 10), function geq);
if insert(11, '(1 5 10), function geq) = '(1 5 10 11)
     then "Test 2 insert (>=) OK"
     else error(0, "Test 2 insert (>=) fails");

% Tru inserting something at the list beginning.
insert(-1, '(1 5 10), function geq);
if insert(-1, '(1 5 10), function geq) = '(-1 1 5 10)
     then "Test 3 insert (>=) OK"
     else error(0, "Test 3 insert (>=) fails");


% Insert into an empty list.
insert('34, nil, function leq);
if insert(34, nil, function leq) = '(34)
     then "Test 4 insert (<=) OK"
     else error(0, "Test 4 insert (<=) fails");

% Use a funny insertion function for (order . any);
expr procedure cargeq(a, b); car a >= car b;
insert('(34 . any), '((5 . now) (20 . and) (30 . then) (40 . but)),
       function cargeq);
if insert('(34 . any), '((5 . now) (20 . and) (30 . then) (40 . but)),
       function cargeq) = '((5 . now) (20 . and) (30 . then) (34 . any)
                            (40 . but))
     then "Test 5 insert (>=) OK"
     else error(0, "Test 5 insert (>=) fails");


% ###### FOR Loop Exercises #####

%------------------------- Exercise #1 -------------------------
expr procedure floatlist l;
/* FLOATLIST(L) returns a list of all floating point
   numbers in list L. */
for each x in l
    when floatp x
    collect x;

if floatlist '(3 3.4 a nil) = '(3.4)
  then "Test 1 floatlist OK"
  else error(0, "Test 1 floatlist fails");

if floatlist '(3.4 1.222 1.0e22) = '(3.4 1.222 1.0e22)
  then "Test 2 floatlist OK"
  else error(0, "Test 2 floatlist fails");

if floatlist '(a b c) = NIL
  then "Test 3 floatlist OK"
  else error(0, "Test 3 floatlist fails");


%------------------------- Exercise #2 -------------------------
expr procedure revpairnum l;
/* REVPAIRNUM(L) returns elements of L in a pair with
  the CAR a number starting at length of L and working
  backwards.*/
for i:=length l step -1 until 0
    each x in l
    collect i . x;

if revpairnum '(a b c) = '((3 . a) (2 . b) (1 . c))
  then "Test 1 revpairnum OK"
  else error(0, "Test 1 revpairnum fails");

if revpairnum  nil = nil
  then "Test 2 revpairnum OK"
  else error(0, "Test 2 revpairnum fails");

if revpairnum '(a) = '((1 . a))
  then "Test 3 revpairnum OK"
  else error(0, "Test 3 revpairnum fails");

%------------------------- Exercise #3 -------------------------
expr procedure lflatten l;
/* LFLATTEN(L) destructively flattens the list L
  to all levels. */
if listp l then for each x in l conc lflatten x
  else {l};

if lflatten '(a (b) c (e (e))) = '(a b c e e)
  then "Test 1 lflatten OK"
  else error(0, "Test 1 lflatten fails");

if lflatten '(a b c) = '(a b c)
  then "Test 2 lflatten OK"
  else error(0, "Test 2 lflatten fails");

if lflatten nil = nil
  then "Test 3 lflatten OK"
  else error(0, "Test 3 lflatten fails");

if lflatten '(a (b (c (d)))) = '(a b c d)
  then "Test 4 lflatten OK"
  else error(0, "Test 4 lflatten fails");

%------------------------- Exercise #4 -------------------------
expr procedure realstuff l;
/* REALSTUFF(L) returns the number of non-nil items in l. */
for each x in l count x;

if realstuff '(a b nil c) = 3
  then "Test 1 realstuff OK"
  else error(0, "Test 1 realstuff fails");

if realstuff '(nil nil nil) = 0
  then "Test 2 realstuff OK"
  else error(0, "Test 2 realstuff fails");

if realstuff '(a b c d) = 4
  then "Test 3 realstuff OK"
  else error(0, "Test 3 realstuff fails");

%------------------------- Exercise #5 -------------------------
expr procedure psentence s;
/* PSENTENCE(S) prints the list of "words" S with
  separating blanks and a period at the end. */
for each w on s
    do << prin2 car w;
          if cdr w then prin2 " " else prin2t "." >>;

psentence '(The man in the field is happy);

%------------------------- Exercise #6 -------------------------
expr procedure bsort v;
/* BSORT(V) sorts the vector V into ascending order using
  bubble sort. */
for i:=0:sub1 upbv v
    returns v
    do for j:=add1 i:upbv v
           when i neq j and v[i] > v[j]
           with tmp
           do << tmp := v[j]; v[j] := v[i]; v[i] := tmp >>;

xxx := [4,3,2,1, 5];
if bsort xxx = [1,2,3,4,5]
  then "Test 1 bsort OK"
  else error(0, "Test 1 bsort fails");

xxx := [1];
if bsort xxx = [1]
  then "Test 2 bsort OK"
  else error(0, "Test 2 bsort fails");


%------------------------- Exercise #7 -------------------------
expr procedure bsortt v;
/* BSORTT(V) sorts the vector V into ascending order using
  bubble sort. It verifies that all elements are numbers. */
<< for i:=0:upbv v 
       when not numberp v[i]
       do error(0, {v[i], "is not a number for BSORTT"});
   for i:=0:sub1 upbv v
       returns v
       do for j:=add1 i:upbv v
              when i neq j and v[i] > v[j]
              with tmp
              do << tmp := v[j]; v[j] := v[i]; v[i] := tmp >> >>;

xxx := [1,2,'a];
if atom errorset(quote bsortt xxx, nil, nil)
  then "Test 1 bsortt OK"
  else error(0, "Test 1 bsortt fails");

xxx := [1, 4, 3, 1];
if car errorset(quote bsortt xxx, nil, nil) = [1,1,3,4]
  then "Test 2 bsortt OK"
  else error(0, "Test 2 bsortt fails");


% ------------------------- Exercise #8 -------------------------
expr procedure average l;
/* AVERAGE(L) compute the average of the numbers
  in list L. Returns 0 if there are none. */
for each x in l
    with sm, cnt
    initially sm := cnt := 0
    when numberp x
    do << sm := sm + x; cnt := cnt + 1 >>
    returns if cnt > 0 then sm / cnt else 0;

if average '(a 12 34) = 23 then
  "Test 1 average OK"
  else error(0, "Test 1 average fails");

if average '(a b c) = 0 then
  "Test 2 average OK"
  else error(0, "Test 2 average fails");

if average '(a b c 5 6) = 5 then
  "Test 3 average OK"
  else error(0, "Test 3 average fails");

if average '(a b c 5 6.0) = 5.5 then
  "Test 4 average OK"
  else error(0, "Test 4 average fails");

%------------------------- Exercise #9 -------------------------
expr procedure boundingbox L;
/* BOUNDINGBOX(L) returns a list of
  (min X, max X, min Y, max Y)
 for the list L of dotted-pairs (x . y). */
{ for each x in L minimize car x,
  for each x in L maximize car x,
  for each y in L minimize cdr y,
  for each y in L maximize cdr y};


if boundingbox '((0 . 1) (4 . 5)) = '(0 4 1 5)
  then "Test 1 boundingbox OK"
  else error(0, "Test 1 boundingbox fails");

if boundingbox nil = '(0 0 0 0)
  then "Test 2 boundingbox OK"
  else error(0, "Test 2 boundingbox fails");

if boundingbox '((-5 . 3.4) (3.3 . 2.3) (1.2 . 33)
                 (-5 . -8) (22.11 . 3.14) (2 . 3)) = '(-5 22.11 -8 33)
  then "Test 3 boundingbox OK"
  else error(0, "Test 3 boundingbox fails");


%------------------------- Exercise #10 -------------------------

expr procedure maxlists(a, b);
/* MAXLISTS(A, B) -- Build a list such that for each pair
  of elements in lists A and B the new list has the largest
  element. */
for each ae in a
    each be in b
    collect max(ae, be);

if maxlists('(3 1.2), '(44.22 0.9 1.3)) = '(44.22 1.2)
  then "Test 1 maxlists OK"
  else error(0, "Test 1 maxlists fails");

if maxlists(nil, '(44.22 0.9 1.3)) = nil
  then "Test 2 maxlists OK"
  else error(0, "Test 2 maxlists fails");

if maxlists('(44.22 0.9 1.3), nil) = nil
  then "Test 3 maxlists OK"
  else error(0, "Test 3 maxlists fails");

if maxlists('(1.0 1.2 3.4), '(1 1)) = '(1.0 1.2)
  then "Test 4 maxlists OK"
  else error(0, "Test 4 maxlists fails");

%------------------------- Exercise #11 -------------------------
expr procedure numberedlist l;
/* NUMBEREDLIST(L) -- returns an a-list with the CAR being
  elements of L and CDR, the position in the list of the
  element starting with 0. */
for i:=0:length l
    each e in l
    collect e . i;

if numberedlist nil = nil
  then "Test 1 numberedlist is OK"
  else error(0, "Test 1 numberedlist fails");

if numberedlist '(a) = '((a . 0))
  then "Test 2 numberedlist is OK"
  else error(0, "Test 2 numberedlist fails");

if numberedlist '(a b c) = '((a . 0) (b . 1) (c . 2))
  then "Test 2 numberedlist is OK"
  else error(0, "Test 2 numberedlist fails");



%------------------------- Exercise #12 -------------------------
expr procedure reduce x;
/* REDUCE(X) -- X is a list of things some of which are
  encapsulated as (!! . y) and returns x. Destructively
  replace these elements with just y. */
for each v on x
    when eqcar(car v, '!!)
    do car v := cdar v
    returns x;

global '(x11);
x11 := '((!! . a) (b c) (d (!! . 34)));

if reduce x11 = '(a (b c) (d (!! . 34)))
  then "Test 1 reduce OK"
  else error(0, "Test 1 reduce fails");

if x11 = '(a (b c) (d (!! . 34)))
  then "Test 2 reduce OK"
  else error(0, "Test 2 reduce fails");


% ##### Further Procedure Tests #####

%------------------------- Exercise #1 -------------------------
expr procedure removeflags x;
/* REMOVEFLAGS(X) -- Scan list x replacing each top level
  occurrence of (!! . x) with x (whatever x is) and return
  the list. Replacement is destructive. */
while x and eqcar(car x, '!!)
      with v
      initially v := x
      do << print x; car x := cdar x;  print x; x := cdr x >>
      returns v;

xxx := '((!!. a) (!! . b) c (!! . d));
if removeflags xxx = '(a b c (!! . d))
 then "Test 1 removeflags OK"
 else error(0, "Test 1 removeflags fails");

if xxx = '(a b c (!! . d))
 then "Test 2 removeflags OK"
 else error(0, "Test 2 removeflags fails");


%------------------------- Exercise #2 -------------------------

expr procedure read2char c;
/* READ2CHAR(C) -- Read characters to C and return the
 list including C. Terminates at end of file. */
repeat l := (ch := readch()) . l
       with ch, l
       until ch eq c or ch eq !$EOF!$
       returns reversip l;

if read2char '!* = {!$EOL!$, 'a, 'b, 'c, '!*}
  then "Test 1 read2char OK"
  else error(0, "Test 1 read2char fails");
abc*

%------------------------- Exercise #3 -------------------------

expr procedure skipblanks l;
/* SKIPBLANKS(L) - Returns L with leading blanks
  removed. */
while l and eqcar(l, '! )
      do l := cdr l
      returns l;

if skipblanks '(!  !  !  a b) neq '(a b)
  then error(0, "Skipblanks fails test #1");

if skipblanks nil
  then error(0, "Skipblanks fails test #2");

if skipblanks '(!  !  !  )
  then error(0, "Skipblanks fails test #3");

if skipblanks '(!  !  a b !  ) neq '(a b ! )
  then error(0, "Skipblanks fails test #4");

%------------------------- Exercise #4 -------------------------

expr procedure ntoken l;
/* NTOKEN(L) - Scan over blanks in l. Then collect
  and return all characters up to the next blank
  returning a dotted-pair of (token . rest of L) or
  NIL if none is found. */
while l and eqcar(l, '! ) do l := cdr l
 returns
  if l then
     while l and not eqcar(l, '! )
           with tok
           do << tok := car l . tok;
                 l := cdr l >>
           returns (reversip tok . l);


if ntoken '(!  !  a b !  ) neq '((a b) . (!  ))
  then error(0, "ntoken fails test #1");

if ntoken nil then error(0, "ntoken fails test #2");

if ntoken '(!  !  !  ) then  error(0, "ntoken fails test #3");

if ntoken '(!  !  a b) neq '((a b) . nil) 
  then error(0, "ntoken fails test #4");


% ##### Block Statement Exercises #####

%------------------------- Exercise #1 -------------------------
expr procedure r2nums;
/* R2NUMS() -- Read 2 numbers and return as a list. */
begin scalar n1;
  n1 := read();
  return {n1, read()}
end;

if r2nums() = '(2 3)
  then "Test 1 r2nums OK"
  else error(0, "Test 1 r2nums failed");
2 3

%------------------------- Exercise #2 -------------------------
expr procedure readcoordinate;
/* READCOORDINATE() -- Read a coordinate and return
 it in radians. If prefixed with @, convert from
 degrees. If a list convert from degrees minutes
 seconds. */
begin scalar x;
  return
    (if (x := read()) eq '!@ then read() / 57.2957795130823208767981
      else if pairp x then
        (car x + cadr x / 60.0 + caddr x / 3600.0)
            / 57.2957795130823208767981
      else x)
end;

fluid '(val);
val := readcoordinate();
@ 57.29577
if val < 1.000001 AND val > 0.999999
  then "Test 1 readcoordinate OK"
  else error(0, "Test 1 readcoordinate failed");

% This fails with poor arithmetic.
val := readcoordinate();
(57 17 44.772)
if val < 1.000001 AND val > 0.999999
  then "Test 2 readcoordinate OK"
  else error(0, "Test 2 readcoordinate failed");
unfluid '(val);


if readcoordinate() = 1.0
  then "Test 3 readcoordinate OK"
  else error(0, "Test 3 readcoordinate failed");
1.0


%------------------------- Exercise #3 -------------------------
expr procedure delallnils l;
/* DELALLNILS(L) - destructively remove all NIL's from
  list L. The resulting value is always EQ to L. */
begin scalar p, prev;
   p := l;
loop: if null p then return l;
    if null car p then 
      if null cdr p then
         if null prev then return nil
         else << cdr prev := nil;
                 return l >>
      else << car p := cadr p;
              cdr p := cddr p;
              go to loop >>;
    prev := p;
    p := cdr p;
    go to loop
end;

fluid '(xxx yyy);    % New - added to aid CSL.
xxx := '(a b c nil d);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 1 dellallnils OK"
  else error(0, "Test 1 delallnils Fails!");

xxx := '(a nil b nil c nil d);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 2 dellallnils OK"
  else error(0, "Test 2 delallnils Fails!");

xxx := '(a nil b nil c nil d nil);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 3 dellallnils OK"
  else error(0, "Test 3 delallnils Fails!");

xxx := '(a nil nil nil nil b c d);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 4 dellallnils OK"
  else error(0, "Test 4 delallnils Fails!");

xxx := '(nil a b c d);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 5 dellallnils OK"
  else error(0, "Test 5 delallnils Fails!");

xxx := '(nil nil nil a b c d);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 6 dellallnils OK"
  else error(0, "Test 6 delallnils Fails!");

xxx := '(a b c d nil nil nil);
yyy := delallnils xxx;
if yyy = '(a b c d) and yyy eq xxx
  then "Test 7 dellallnils OK"
  else error(0, "Test 7 delallnils Fails!");


%------------------------- Exercise 4 -------------------------

expr procedure dprin1 x;
/* DPRIN1(X) - Print X in dotted-pair notation (to
  all levels). Returns X as its value. */
if vectorp x then 
  << prin2 "[";
     for i:=0:upbv x
         do << dprin1 x[i];
               if i < upbv x then prin2 " " >>;
     prin2 "]";
     x >>
  else if atom x then prin1 x
  else << prin2 "(";
          dprin1 car x;
          prin2 " . ";
          dprin1 cdr x;
          prin2 ")";
          x >>;

% The test is hard to make because we're doing output.
% Verify the results by hand and make sure it returns the
% argument.
dprin1 nil;
dprin1 '(a . b);
dprin1 '(a 1 "foo");
dprin1 '(((a)));
<< x := mkvect 2; x[0] := 'a; x[1] := '(b c); x[2] := 34; >>;
dprin1 {'(b c), x, 34};


% ##### Property List Exercises #####

%---------------------------- Exercise #1 ------------------------------

global '(stack!*);
expr procedure pexecute l;
/* PEXECUTE(L) - L is a stack language. Constants are
  placed on the global stack!*, id's mean a function
  call to a function under the STACKFN property of the
  function name. Other values are placed on the stack
  without evaluation. */
if null l then nil
else if constantp car l then
    << stack!* := car l . stack!*;
       pexecute cdr l >>
  else if idp car l then
    if get(car l, 'STACKFN) then
       << apply(get(car l, 'STACKFN), nil);
          pexecute cdr l >>
    else error(0, {car l, "undefined function"})
  else << stack!* := car l . stack!*;
          pexecute cdr l >>;

 expr procedure pdiff;
/* PADD1() - Subtract the 2nd stack elt from the
  first and replace top two entries with result. */
stack!* := (cadr stack!* - car stack!*) . cddr stack!*;
put('!-, 'STACKFN, 'pdiff);

expr procedure pplus2;
/* PPLUS2() - Pop and add the top two numbers
  on the stack and push the result. */
stack!* := (car stack!* + cadr stack!*) . cddr stack!*;
put('!+, 'STACKFN, 'pplus2);

expr procedure pprint;
/* PPRINT() - Print the top stack element. */
print car stack!*;
put('PRINT, 'STACKFN, 'pprint);


pexecute '(3 4 !+);
if stack!* neq '(7) then error(0, "PEXECUTE test #1 fails");
stack!* := nil;

pexecute '(5 3 !- 2 4 !+ !+);
if stack!* neq '(8) then error(0, "PEXECUTE test #2 fails");


%---------------------------- Exercise #2 ------------------------------

expr procedure pexecute l;
/* PEXECUTE(L) - L is a stack language. Constants are
  placed on the global stack!*, id's mean a function
  call to a function under the STACKFN property of the
  function name. Other values are placed on the stack
  without evaluation. */
if null l then nil
else if constantp car l then
    << stack!* := car l . stack!*;
       pexecute cdr l >>
  else if idp car l then
    if eqcar(l, 'QUOTE) then
       << stack!* := cadr l . stack!*;
          pexecute cddr l >>
    else if flagp(car l, 'STACKVAR) then
       << stack!* := get(car l, 'STACKVAL) . stack!*;
          pexecute cdr l >>
    else if get(car l, 'STACKFN) then
       << apply(get(car l, 'STACKFN), nil);
          pexecute cdr l >>
    else error(0, {car l, "undefined function"})
  else << stack!* := car l . stack!*;
          pexecute cdr l >>;


expr procedure pset;
/* PSET() - Put the second value on the stack under
  the STACKVAL attribute of the first. Flag the id as
  a STACKVAR for later use. Pop the top stack
  element. */
<< put(car stack!*, 'STACKVAL, cadr stack!*);
   flag({car stack!*}, 'STACKVAR);
   stack!* := cdr stack!* >>;
put('SET, 'STACKFN, 'pset);

stack!* := nil;
pexecute '(4.5 quote x set 4 !+ x !+ PRINT);
if stack!* neq '(13.0) then error(0, "Test 3 PEXECUTE fails");


% ##### Records Exercises #####

%------------------------- Exercise #1 -------------------------

record qtree /* QTREE is a quad tree node element. */
 with
   node := NIL        /* Node name */,
   q1 := NIL          /* Child #1 */,
   q2 := NIL          /* Child #2 */,
   q3 := NIL          /* Child #3 */,
   q4 := NIL          /* Child #4 */;


expr procedure qvisit q;
/* QVISIT(Q) -- Q is a QTREE data structure or NIL as are
  each of its children. Return a preorder visit of each
  node. */
if null q then nil
  else append({node q},
              append(qvisit q1 q,
               append(qvisit q2 q,
                append(qvisit q3 q, qvisit q4 q))));


/* A simple quad tree. */
global '(qdemo);
qdemo := qtree(node := 'A,
            q1 := qtree(node := 'B),
            q2 := qtree(node := 'C),
            q3 := qtree(node := 'D,
                        q1 := qtree(node := 'E)),
            q4 := qtree(node := 'F));

if qvisit qdemo = '(A B C D E F)
  then "Test 1 qvisit OK!"
  else error(0, "Test 1 qvisit Fails!");

/* The quadtree in the book. */
global '(qdemo2);
qdemo2 := qtree(node := 'A,
    q1 := qtree(node := 'B),
    q2 := qtree(node := 'C),
    q3 := qtree(node := 'D,
         q1 := qtree(node := 'E,
            q2 := qtree(node := 'F)),
         q2 := qtree(node := 'G),
         q3 := qtree(node := 'H),
         q4 := qtree(node := 'I)));

if qvisit qdemo2 = '(A B C D E F G H I)
  then "Test 2 qvisit OK!"
  else error(0, "Test 2 qvisit Fails!");

if qvisit nil = NIL
  then "Test 3 qvisit OK!"
  else error(0, "Test 3 qvisit Fails!");


%------------------------- Exercise #2 -------------------------

expr procedure qsearch(q, val, fn);
/* QSEARCH(Q, VAL, FN) -- Returns the node path from the
  root of the quadtree Q to VAL using FN as an equality
  function whose first argument is from the tree and
  second VAL. */
if null q then nil
else if apply(fn, {val, node q}) then {node q}
else begin scalar v;
  if v := qsearch(q1 q, val, fn) then return node q . v;
  if v := qsearch(q2 q, val, fn) then return node q . v;
  if v := qsearch(q3 q, val, fn) then return node q . v;
  if v := qsearch(q4 q, val, fn) then return node q . v
end;

if qsearch(qdemo, 'E, function EQ) = '(A D E)
  then "Test 1 qsearch OK!"
  else error(0, "Test 1 qsearch fails");

if qsearch(qdemo, 'XXX, function EQ) = nil
  then "Test 2 qsearch OK!"
  else error(0, "Test 2 qsearch fails");

if qsearch(qdemo2, 'F, function EQ) = '(A D E F)
  then "Test 3 qsearch OK!"
  else error(0, "Test 3 qsearch fails");


%------------------------- Exercise #3 -------------------------

record commchain
/* A COMMCHAIN is an n-ary tree with superior and
 subordinate links. */
with 
  name := NIL          /* Name of this node. */,
  superior := NIL      /* Pointer to superior node. */,
  subordinates := NIL  /* List of subordinates. */;


expr procedure backchain(l, sup);
/* BACKCHAIN(L, SUP) -- Fill in the SUPERIOR fields of
  each record in the n-ary tree (links in the SUBORDINATES
  field) to the lowest level. SUP is the current 
  superior. */
if null l then nil
 else << superior l := sup;
         for each sb in subordinates l
             do backchain(sb, l) >>;

/* Demo the back chain. */
global '(cch);
cch :=
  commchain(
    name := 'TOP,
    subordinates :=
      {commchain(name := 'LEV1-A),
       commchain(
          name := 'LEV1-B,
          subordinates := 
            {commchain(name := 'LEV2-A),
             commchain(name := 'LEV2-B)}),
       commchain(name := 'LEV1-C)});

% Wrap this up to avoid printing problems. 
<< backchain(cch, 'COMMANDER); NIL >>;


if superior cch EQ 'COMMANDER
  then "Test 1 backchain OK!"
  else error(0, "Test 1 backchain Fails!");

if name superior car subordinates cch EQ 'TOP
  then "Test 2 backchain OK!"
  else error(0, "Test 2 backchain Fails!");

if name superior car subordinates cadr subordinates cch
      eq 'LEV1-B
  then "Test 3 backchain OK!"
  else error(0, "Test 3 backchain Fails!");


% ##### Local Variable Exercises #####

%------------------------- Exercise #1 -------------------------

expr procedure lookup(v, a);
/* LOOKUP(V, A) -> Look for V in A and signal an error if not present.*/
(if rv then cdr rv else error(0, {v, "not in association list"}))
  where rv := assoc(v, a);


if lookup('a, '((a . b) (c . d))) = 'b
  then "Test 1 lookup success"
  else error(0, "Test 1 lookup fails");

if errorset(quote lookup('f, '((a . b) (c . d))), nil, nil) = 0
  then "Test 2 lookup success"
  else error(0, "Test 2 lookup fails");

%------------------------- Exercise #2 -------------------------

expr procedure quadratic(a, b, c);
/* QUADRATIC(A, B, C) -- Returns both solutions of the
  quadratic equation A*X^2 + B*X + C */
{(-B + U) / V, (-B - U) / V}
  where U := SQRT(B^2 - 4*A*C),
        V := 2.0 * A;

if quadratic(1.0, 2.0, 1.0) = '(-1.0 -1.0)
  then "Test 1 quadratic OK!"
  else error(0, "Test 1 quadratic Fails!");

if quadratic(1.0, 0.0, -1.0) = '(1.0 -1.0)
  then "Test 2 quadratic OK!"
  else error(0, "Test 2 quadratic Fails!");


%------------------------- Exercise #3 -------------------------
expr procedure lineintersection(x1, y1,
                                x2, y2,
                                x3, y3,
                                x4, y4);
/* LINEINTERSECTION(X1,Y1,X2,Y2,X3,Y3,X4,Y4) -
  Computes the intersection of line X1,Y1 ->
  X2,Y2 with X3,Y3 -> X4,Y4 if any. Returns NIL
  if no such intersection. */
(if zerop denom or zerop d1 or zerop d2 then nil
    else
     ((if p1 < 0 or p1 > d1 or p2 < 0 or p2 > d2
       then nil
       else (x1 + (x2 - x1) * p1 / d1) .
            (y1 + (y2 - y1) * p1 / d1))
          where p1 := num1 / denom,
                p2 := num2 / denom)
    where 
      num1 := d1*(x1*y3 - x1*y4 - x3*y1 + x3*y4
              + x4*y1 - x4*y3),
      num2 := d2*(- x1*y2 + x1*y3 + x2*y1 - x2*y3
              - x3*y1 + x3*y2))
where d1 :=sqrt((x2 - x1)^2 + (y2 - y1)^2),
      d2 := sqrt((x4 - x3)^2 + (y4 - y3)^2),
      denom := x1*y3 - x1*y4 - x2*y3 + x2*y4
               - x3*y1 + x3*y2 + x4*y1 - x4*y2;

if lineintersection(1, 1, 3, 3, 1, 2, 5, 2) = '(2.0 . 2.0)
  then "Test 1 LINEINTERSECTION success!"
  else error(0, "Test 1 LINEINTERSECTION fails intersect test");

% intersection at start and end points.
if lineintersection(1, 1, 2, 2, 1, 1, 1, 0) = '(1.0 . 1.0)
  then "Test 2 LINEINTERSECTION success!"
  else error(0, "Test 2LINEINTERSECTION fails intersect at start test");
if lineintersection(1, 1, 2, 2, 0, 1, 2, 2) = '(2.0 . 2.0)
  then "Test 3 LINEINTERSECTION success!"
  else error(0,
            "Test 3 LINEINTERSECTION fails intersect at endpoint test");
if lineintersection(1, 1, 2, 2, 2, 2, 3, 4) = '(2.0 . 2.0)
  then "Test 4 LINEINTERSECTION success!"
  else error(0,
      "Test 4 LINEINTERSECTION fails intersect end - begin point test");

% Now try no intersection test.
if null lineintersection(1, 1, 2, 3, 2, 4, 4, 5)
  then "Test 5 LINEINTERSECTION success!"
  else error(0,
            "Test 5 LINEINTERSECTION fails quadrant 1 no intersection");
if null lineintersection(1, 1, 2, 2, 1.75, 1.5, 5, 1.75)
  then "Test 6 LINEINTERSECTION success!"
  else error(0,
            "Test 6 LINEINTERSECTION fails quadrant 2 no intersection");



%------------------------- Exercise #4 -------------------------

expr procedure stdev x;
/* STDEV(X) - compute the standard deviation of the
  numbers in list X. */
if null x then 0
else (sqrt((for each v in x sum (v - avg)^2) / n)
         where avg := (for each v in x sum v) / n)
           where n := length x;

if stdev '(3.0 3.0 3.0) neq 0.0 then
  error(0, "Test 1 STDEV fails");


% ##### Array Exercises #####

%------------------------- Exercise #1 -------------------------
expr procedure vaverage v;
/* VAVERAGE(V) -- compute the average of all numeric
  elements of the vector v. */
(if cnt > 0 then
    ((for i:=0:upbv v when numberp v[i] sum v[i]) / float cnt)
  else 0.0)
 where cnt := for i:=0:upbv v count numberp v[i];

if vaverage array(1,2,3) = 2.0
  then "Test 1 vaverage is OK"
  else error(0, "Test 1 vaverage fails");

if vaverage array(3, 'a, 3, 6.0, 'f) = 4.0
  then "Test 2 vaverage is OK"
  else error(0, "Test 2 vaverage fails");

if vaverage array('a, 'b) = 0.0
  then "Test 3 vaverage is OK"
  else error(0, "Test 3 vaverage fails");

%------------------------- Exercise #2 -------------------------

expr procedure MAPPEND(a, b);
/* MAPPEND(A, B) -- Appends array B to array A and
  returns a new array with both. */
begin scalar c, ua;
  c := mkvect((ua := 1 + upbv a) + upbv b);
  for i:=0:upbv a do c[i] := a[i];
  for i:=0:upbv b do c[i + ua] := b[i];
  return c
end;

global '(a1 a2);
a1 := array(1, 2, 3);
a2 := array(3, 4, 5, 6);

if mappend(a1, a2) = array(1,2,3,3,4,5,6)
  then "Test 1 MAPPEND is OK"
  else error(0, "Test 1 MAPPEND fails");

if mappend(mkvect 0, mkvect 0) = mkvect 1
  then "Test 2 MAPPEND is OK"
  else error(0, "Test 2 MAPPEND fails");


%------------------------- Exercise #3 -------------------------

expr procedure indx(a, v);
/* INDX(A, V) -- returns index of A in V using EQ test,
  otherwise NIL. */
for i:=0:upbv v
    until a eq v[i]
    returns if i <= upbv v then i


if indx('a, array(1, 2, 'a, 34)) = 2
  then "Test 1 indx OK"
  else error(0, "Test 1 indx fails");

if null indx('a, array(1, 2, 3, 4))
  then "Test 2 indx OK"
  else error(0, "Test 2 indx fails");


%------------------------- Exercise #4 -------------------------
expr procedure mpy4x4(a, b);
/* MPY4X4(A, B) -- Create a new 4x4 matrix and return with
  the product of A and B in it. */
for row:=0:3
    with c, s
    initially c := mkarray(3,3)
    do << for col := 0:3 do
              do c[row,col] :=
                   for p := 0:3 sum a[row,p] * b[p,col] >>
    returns c;


expr procedure translate4x4(x, y, z);
/* TRANSLATE4X4(X, Y, Z) -- Generate and return a
 4x4 matrix to translate X, Y, Z. */
array(array(1.0, 0.0, 0.0, 0.0),
      array(0.0, 1.0, 0.0, 0.0),
      array(0.0, 0.0, 1.0, 0.0),
      array(x,   y,   z,   1.0));

expr procedure rotatex4x4 th;
/* ROTATEX4X4(TH) -- Generate a 4x4 rotation matrix about
  the X axis, TH radians. */
array(array(1.0,     0.0,    0.0,     0.0),
      array(0.0,     cos th, -sin th, 0.0),
      array(0.0,     sin th, cos th,  0.0),
      array(0.0,     0.0,    0.0,     1.0));



expr procedure mappoint(x, y, z, m);
/* MAPPOINT(X, Y, Z, M) -- Returns the transformed point
  X, Y, Z by the 4x4 matrix M. */
{x*m[0,0] + y*m[1,0] + z*m[2,0] + m[3,0],
 x*m[0,1] + y*m[1,1] + z*m[2,1] + m[3,1],
 x*m[0,2] + y*m[1,2] + z*m[2,2] + m[3,2]};


/* tmat is test matrix to rotate about x. In our tests we
  have to construct the resulting numbers on the fly
  because when input, they aren't the same for EQUAL. */
global '(tmat);
tmat := rotatex4x4(45.0 / 57.29577);

if mappoint(0.0, 0.0, 0.0, tmat) = '(0.0 0.0 0.0)
  then "Test 1 4x4 OK"
  else error(0, "Test 1 4x4 failed");
if mappoint(1.0, 0.0, 0.0, tmat) = '(1.0 0.0 0.0)
  then "Test 2 4x4 OK"
  else error(0, "Test 2 4x4 failed");
if mappoint(0.0, 1.0, 0.0, tmat) =
     {0.0, cos(45.0 / 57.29577), - sin(45.0 / 57.29577)}
  then "Test 3 4x4 OK"
  else error(0, "Test 3 4x4 failed");
if mappoint(1.0, 1.0, 0.0, tmat) =
     {1.0, cos(45.0 / 57.29577), - sin(45.0 / 57.29577)}
  then "Test 4 4x4 OK"
  else error(0, "Test 4 4x4 failed");
if mappoint(0.0, 0.0, 1.0, tmat) = 
     {0.0, sin(45.0 / 57.29577), cos(45.0 / 57.29577)}
  then "Test 5 4x4 OK"
  else error(0, "Test 5 4x4 failed");
if mappoint(1.0, 0.0, 1.0, tmat) = 
     {1.0, sin(45.0 / 57.29577), cos(45.0 / 57.29577)}
  then "Test 6 4x4 OK"
  else error(0, "Test 6 4x4 failed");
if mappoint(0.0, 1.0, 1.0, tmat) =
     {0.0, cos(45.0 / 57.29577) + sin(45.0 / 57.29577),
      cos(45.0 / 57.29577) - sin(45.0 / 57.29577)}
  then "Test 7 4x4 OK"
  else error(0, "Test 7 4x4 failed");
if mappoint(1.0, 1.0, 1.0, tmat) = 
     {1.0, cos(45.0 / 57.29577) + sin(45.0 / 57.29577),
      cos(45.0 / 57.29577) - sin(45.0 / 57.29577)}
  then "Test 8 4x4 OK"
  else error(0, "Test 8 4x4 failed");


/* Now try the multiplication routine. */
tmat := mpy4x4(rotatex4x4(45.0 / 57.29577),
               translate4x4(1.0, 2.0, 3.0));
if mappoint(0.0, 0.0, 0.0, tmat) = '(1.0 2.0 3.0)
  then "Test 9 4x4 OK"
  else error(0, "Test 9 4x4 failed");
if mappoint(0.0, 0.0, 1.0, tmat) =
     {1.0, 2.0 + sin(45.0 / 57.29577),
      3.0 + cos(45.0 / 57.29577)}
  then "Test 10 4x4 OK"
  else error(0, "Test 10 4x4 failed");
  

%------------------------- Exercise 4 -------------------------

expr procedure ltident n;
/* LTIDENT(N) -- Create and return a lower triangular,
   square, identity matrix with N+1 rows. */
for i:=0:n
    with a
    initially a := mkvect n
    do << a[i] := mkvect i;
          for j:=0:i - 1 do a[i,j] := 0.0;
          a[i,i] := 1.0 >>
    returns a;

expr procedure ltmpy(a, b);
/* LTMPY(A, B) -- Compute the product of two square,
  lower triangular matrices of the same size and return.
  Note that the product is also lower triangular. */
(for i:=0:rows
     with c
     initially c := mkvect rows
     do << c[i] := mkvect i;
           for j:=0:i do
               c[i,j] := for k:=j:i sum a[i,k] * b[k,j] >>
     returns c)
  where rows := upbv a;

if ltident 2 = array(array(1.0),
                     array(0.0, 1.0),
                     array(0.0, 0.0, 1.0))
  then "Test 1 ltident OK"
  else "Test 1 ltident fails";

if ltident 0 = array(array(1.0))
  then "Test 2 ltident OK"
  else "Test 2 ltident fails";

if ltmpy(ltident 2, ltident 2) = ltident 2
  then "Test 3 ltident OK"
  else "Test 3 ltident fails";

if ltmpy(array(array(1.0),
               array(1.0, 2.0),
               array(1.0, 2.0, 3.0)),
         array(array(1.0),
               array(1.0, 2.0),
               array(1.0, 2.0, 3.0))) = 
   array(array(1.0),
         array(3.0, 4.0),
         array(6.0, 10.0, 9.0))
  then "Test 4 ltmpy OK"
  else error(0, "Test 4 ltmpy fails");

if ltmpy(array(array(1.2),
               array(3.4, 5.0),
               array(1.0,-2.3,-1.3)), ltident 2)
     = array(array(1.2),
             array(3.4, 5.0),
             array(1.0, -2.3, -1.3))
  then "Test 5 ltmpy OK"
  else error(0, "Test 5 ltmpy fails");

                

%------------------------- Exercise #5 -------------------------

expr procedure coerce(a, b, pth, cmat);
/* COERCE(A,B,PTH,CMAT) -- return a list of functions 
  to coerce type A (an index into CMAT) into type B. PTH
  is NIL   to start and CMAT the coercion table arranged
  with "from" type as rows, "to" type as columns. */
if cmat[a,b] then cmat[a,b] . pth
else
  for j:=0:upbv cmat[a]
      with cp
      until j neq a and cmat[a,j] and
            not (cmat[a,j] memq pth) and
            not(cmat[j,a] memq pth) and
            (cp := coerce(j, b, cmat[a,j] . pth, cmat))
      returns cp;

/* Create the coercion array. Here int=0, string=1,
  float=2, complex=3, and gaussian=4 */
global '(cpath);
cpath :=
 array(array('ident,   'int2str, 'float,   nil,      nil),
       array('str2int, 'ident,   'str2flt, nil,      nil),
       array('fix,     'flt2str, 'ident,   'flt2cplx,nil),
       array(nil,      nil,      nil,      'ident, 'cfix),
       array(nil,      nil,      nil,    'cfloat, 'ident));


% Coerce int to complex.
if coerce(0, 3, nil, cpath) = '(FLT2CPLX STR2FLT INT2STR)
  then "Test 1 coerce OK"
  else error(0, "Test 1 coerce fails");

% Coerce Complex into int.
if coerce(3, 0, nil, cpath) = NIL
  then "Test 2 coerce OK"
  else error(0, "Test 2 coerce fails");

% Coerce int into gaussian.
if coerce(0, 4, nil, cpath) = 
       '(CFIX FLT2CPLX STR2FLT INT2STR)
  then "Test 3 coerce OK"
  else error(0, "Test 3 coerce fails");

               
    
%------------------------- Exercise #6 -------------------------

expr procedure cellvon(a, b, fn);
/* CELLVON(A, B, FN) -- Compute the next generation of the
  cellular matrix A and place it into B. Use the VonNeumann
  neighborhood and the function FN to compute the next
  generation. The space edges are wrapped into a torus*/
for r:=0:rows
    with rows, cols
    initially << rows := upbv a; cols := upbv a[1] >>
    do for c:=0:cols
        do b[r,c] := apply(fn,
             {a[r,c],
              a[torus(r + 1, rows), torus(c - 1, cols)],
              a[torus(r + 1, rows), c],
              a[torus(r + 1, rows), torus(c + 1, cols)],
              a[r, torus(c + 1, cols)],
              a[torus(r - 1, rows), torus(c + 1, cols)],
              a[torus(r - 1, rows), c],
              a[torus(r - 1, rows), torus(c - 1, cols)],
              a[r, torus(c - 1, cols)]});

expr procedure torus(i, v);
/* TORUS(I, V) -- A positive modulus: if I is less than
  0, wrap to V, or if it exceeds V, wrap to I. */
if i < 0 then v 
  else if i > v then 0
  else i;

expr procedure life(c, n1, n2, n3, n4, n5, n6, n7, n8);
/* LIFE(C, N1 ... N8) -- Game of life rules. Here C is
  the cell being examined and N1-N8 are the VonNeumann
  neighbor states. */
(if c = 1 then if cnt = 2 or cnt = 3 then 1 else 0
  else if cnt = 3 then 1 else 0)
 where cnt = n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8;

/* LIFESTATES contains a vector of states and what
  character to print. */
global '(LIFESTATES);
LIFESTATES := array(" ", "*");

expr procedure pcell(gen, a, pr);
/* PCELL(GEN, A) -- Display the state of the GEN generation
  of the cellular matrix A. Display a * for state=1, and
  a blank for state 0. */
for r:=0:rows
    with rows, cols
    initially << rows := upbv a; cols := upbv a[1];
                 terpri(); prin2 "Generation: "; print gen >>
    do << terpri();
          for c:=0:cols do prin2 pr[a[r,c]] >>;



expr procedure rungame(a, n, fn, pr);
/* RUNGAME(A, N, FN, PR) -- Run through N generations
  starting with the cellular matrix A and using the
  function FNto compute the new generation. Use the array
  PR to display the state. */
for i:=1:n
    with tmp, b
    initially b := mkarray(upbv a, upbv a[1])
    do << pcell(i, a, pr);
          cellvon(a, b, function life);
          tmp := a;  a := b;  b := tmp >>;
 

/* SEED is the seed array with 1's for on state, 0 for
  off. */
global '(seed);
seed := array(
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 1, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 1, 0, 0, 0),
  array(0, 0, 0, 0, 1, 1, 1, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
  array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

rungame(seed, 10, function life, LIFESTATES);


%------------------------- Exercise #7 -------------------------

expr procedure compact heep;
/* compact(HEEP) -- HEEP is an array of blocks of
  sequentially allocated items. The first entry in each 
  block is INUSE, the second the total number of entries
  + 2 (for the header). The remainder are random values.
  Free blocks are the same but instead have the header
  FREE. Returns a compacted structure with a single FREE
  entry at the end with entries changed to *. Returns the
  number of free entries. */
begin scalar dest, src, last, u;
  last := dest := src := 0;
loop: if src > upbv heep then
        if src = dest then return 0
        else << heep[dest] := 'FREE;
                heep[dest+1] := src - dest;
                for i:=dest+2:upbv heep do heep[i] := '!*;
                return heep[dest+1] >>;
  if heep[src] eq 'FREE then
       src := heep[src+1] + src
  else << u := heep[src+1] + src - 1;
          for i:=src:u do << heep[dest] := heep[i];
                             dest := dest + 1 >>;
          src := u + 1 >>;
  go to loop
end;


/* A simple array to test. */
global '(H);
H := array('INUSE, 3, 0,
           'FREE, 4, '!*, '!*,
           'INUSE, 4, 0, 1,
           'FREE, 3, '!*,
           'FREE, 5, '!*, '!*, '!*,
           'INUSE, 5, 0, 1, 2,
           'INUSE, 5, 3, 4, 5);

if compact H = 12
  then "Test 1 compact OK!"
  else error(0, "Test 1 compact fails!");
if H = array('INUSE, 3, 0, 'INUSE, 4, 0, 1, 'INUSE,
             5, 0, 1, 2, 'INUSE, 5, 3, 4, 5,
             'FREE, 12, '!*, '!*, '!*, '!*, '!*, '!*,
             '!*, '!*, '!*, '!*)
  then "Test 2 compact OK!"
  else error(0, "Test 2 compact fails!");

/* Test a completely full one. */
H := array('INUSE, 3, 0, 'INUSE, 5, 1, 2, 3);
if compact H = 0
  then "Test 3 compact OK!"
  else error(0, "Test 3 compact fails!");
if H = array('INUSE, 3, 0, 'INUSE, 5, 1, 2, 3)
  then "Test 4 compact OK!"
  else error(0, "Test 4 compact fails!");


/* Test a completely empty one. */
H := array('FREE, 3, '!*, 'FREE, 5, '!*, '!*, '!*);
if compact H = 8 
  then "Test 5 compact OK!"
  else error(0, "Test 5 compact fails!");
if H = array('FREE, 8, '!*, '!*, '!*, '!*, '!*, '!*)
  then "Test 6 compact OK!"
  else error(0, "Test 6 compact fails!");


%------------------------- Exercise #8 -------------------------

expr procedure HISTOGRAM(v, n);
/* HISTOGRAM(V,N) -- V is an arbitrarily size vector of
  numbers. Compute its an N element histogram over its
  range and return it. */
begin scalar minv, maxv, h, range;
  minv := maxv := v[0];
  for i:=1:upbv v
      do << if v[i] < minv then minv := v[i];
            if v[i] > maxv then maxv := v[i] >>;
  range := maxv - minv;
  h := mkvect(n - 1);
  for i:=0:n - 1 do h[i] := 0;
  for i:=0:upbv v
      with hn
      do << hn := fix(n * (v[i] - minv) / range);
            if hn = n then hn := hn - 1;
            h[hn] := h[hn] + 1 >>;
  return h
end;

global '(v1);
<< v1 := mkvect 100;
   for i:=0:100 do v1[i] := float i >>;

if HISTOGRAM(v1, 1) = array(101)
  then "Test 1 HISTOGRAM OK!"
  else error(0, "Test 1 HISTOGRAM Fails!");

if HISTOGRAM(v1, 2) = array(50, 51)
  then "Test 2 HISTOGRAM OK!"
  else error(0, "Test 2 HISTOGRAM Fails!");

if HISTOGRAM(v1, 7) = array(15, 14, 14, 15, 14, 14, 15)
  then "Test 3 HISTOGRAM OK!"
  else error(0, "Test 3 HISTOGRAM Fails!");


%------------------------- Exercise #9 -------------------------

expr procedure rarray n;
/* RARRAY(N) - generate an NxN matrix with uniform
  distribution random numbers in the range 0.0 -> 1.0. */
for x:=0:n
    with a
    initially a := mkarray(n,n)
    returns a
    do for y:=0:n do a[x,y] := random(1000) / 1000.0;

if upbv rarray 4 =  4
  then "Test 1 rarray OK"
  else error(0, "Test 1 rarray fails");


expr procedure addcircle(a, r, xc, yc, v);
/* ADDCIRCLE(A, R, XC, YC, V) -- Add V to each cell within
  distance R from center point XC, YC and return a new
  matrix with these values. Values always remain in the
  range 0.0 -> 1.0. */
begin scalar uax, uay, b;
  b := mkarray(uax := upbv a, uay := upbv a[0]);
  for x:=0:uax do
    for y:=0:uay do
       b[x,y] := if sqrt((x - xc)^2 + (y - yc)^2) <= r
            then min(1.0, v + a[x,y]) else a[x,y];
  return b
end;

global '(xxx);
xxx := array(array(0, 0, 0, 0, 0),
             array(0, 0, 0, 0, 0),
             array(0, 0, 0, 0, 0),
             array(0, 0, 0, 0, 0),
             array(0, 0, 0, 0, 0));

% This will fail if sqrt isn't very accurate. 
if addcircle(xxx, 2.0, 2, 2, 0.75) = 
  array(array(0,    0,    0.75, 0,    0),
        array(0,    0.75, 0.75, 0.75, 0),
        array(0.75, 0.75, 0.75, 0.75, 0.75),
        array(0,    0.75, 0.75, 0.75, 0),
        array(0,    0,    0.75, 0,    0))
  then "Test 1 addcircle OK!"
  else error(0, "Test 1 addcircle fails!");

if addcircle(xxx, 10.0, 2, 2, 0.75) = 
  array(array(0.75, 0.75, 0.75, 0.75, 0.75),
        array(0.75, 0.75, 0.75, 0.75, 0.75),
        array(0.75, 0.75, 0.75, 0.75, 0.75),
        array(0.75, 0.75, 0.75, 0.75, 0.75),
        array(0.75, 0.75, 0.75, 0.75, 0.75))
  then "Test 2 addcircle OK!"
  else error(0, "Test 2 addcircle fails!");


%------------------------- Exercise #10 -------------------------

expr procedure areaaverage(a, n);
/* AREAAVERAGE(A, N) -- Compute the average of the NxN
  neighborhood of each cell in the matrix A and return a
  new matrix with these values. */
begin scalar uax, uay, sm, cnt, b, n2;
  n2 := n / 2;
  b := mkarray(uax := upbv a, uay := upbv a[1]);
  for x := 0:uax do
   for y := 0:uay do
     << sm := 0.0;
        cnt := 0;
        for xp := max(0, x - n2):min(uax, x + n2) do
          for yp := max(0, y - n2):min(uay, y + n2) do
            << sm := sm + a[xp,yp];
               cnt := cnt + 1 >>;
        b[x,y] := sm / cnt >>;
  return b
end;

global '(ninth);
xxx[2,2] := 1.0;
ninth := 1.0 / 9.0;

if areaaverage(xxx, 3) =
  array(array(0.0, 0.0,   0.0,   0.0,   0.0),
        array(0.0, ninth, ninth, ninth, 0.0),
        array(0.0, ninth, ninth, ninth, 0.0),
        array(0.0, ninth, ninth, ninth, 0.0),
        array(0.0, 0.0,   0.0,   0.0,   0.0))
  then "Test 1 areaaverage OK!"
  else error(0, "Test 1 areaaverage Fails!");


%------------------------- Exercise #11 -------------------------

expr procedure laplace a;
/* LAPLACE(A) -- Compute the Laplacian on A but assuming
  0.0 at the borders. Returns a new array the same size
  as A. */
begin scalar uax, uay, b, sm;
  b := mkarray(uax := upbv a, uay := upbv a[0]);
  for x := 0:uax do
   for y := 0:uay do
     << sm := 0.0;
        for xp := max(0, x - 1):min(uax, x + 1)
          when xp neq x do
          for yp := max(0, y - 1):min(uay, y + 1)
            when yp neq y
            do sm := sm + a[xp,yp];
        b[x,y] := max(0.0, min(5.0 * a[x,y] - sm, 1.0)) >>;
  return b
end;


xxx := array(array(0,0,0,0,0),
             array(0,1,1,1,0),
             array(0,1,1,1,0),
             array(0,1,1,1,0),
             array(0,0,0,0,0));
if laplace xxx = array(array(0.0, 0.0, 0.0, 0.0, 0.0),
                       array(0.0, 1.0, 1.0, 1.0, 0.0),
                       array(0.0, 1.0, 1.0, 1.0, 0.0),
                       array(0.0, 1.0, 1.0, 1.0, 0.0),
                       array(0.0, 0.0, 0.0, 0.0, 0.0))
  then "Test 1 laplace OK!"
  else error(0, "Test 1 laplace fails!");


%------------------------- Exercise #12 -------------------------

expr procedure threshold(a, vl, vh);
/* THRESHOLD(A, VL, VH) -- Returns a new matrix of the same
  size as A with each cell set to 1.0 that is 
  VL <= A(i,j) <= VH. Others are set to 0.0. */
for x := 0:uax
    with uax, uay, b
    initially b := mkarray(uax := upbv a,
                           uay := upbv a[0])
    returns b
    do for y := 0:uay
         do b[x,y] := 
               if a[x,y] >= vl and a[x,y] <= vh then 1.0
                                                else 0.0;

xxx := mkarray(4,4);
for i:=0:4 do for j:=0:4 do xxx[i,j] := i * j;

if threshold(xxx, 8, 10) = array(
  array(0.0, 0.0, 0.0, 0.0, 0.0),
  array(0.0, 0.0, 0.0, 0.0, 0.0),
  array(0.0, 0.0, 0.0, 0.0, 1.0),
  array(0.0, 0.0, 0.0, 1.0, 0.0),
  array(0.0, 0.0, 1.0, 0.0, 0.0))
  then "Test 1 threshold OK!"
  else error(0, "Test 1 threshold Fails!");



expr procedure dump(a, f);
/* DUMP(A,F) -- Dump an array A into a PicTex format
  file for document processing. */
begin scalar fh;
  fh := wrs open(f, 'output);
  for x:=0:upbv a do
    for y:=0:upbv a[0] do
      printf("\setshadegrid span <%wpt>%n\vshade %d %d %d %d %d %d /%n",
             max(0.5, 5.5 - a[x,y]*5.0),
             x, y, y+1, x+1, y, y+1);
  close wrs fh;
end;


% ##### Macro Exercises #####

%------------------------- Exercise -----------------------
macro procedure appendl x;
/* APPENDL( ...) - append all the lists together. */
 expand(cdr x, 'append);

if appendl('(a b), '(c d), '(e f)) = '(a b c d e f)
  then "Test 1 appendl OK!"
  else error(0, "Test 1 appendl fails!");
if appendl '(a b c) = '(a b c)
  then "Test 2 appendl OK!"
  else error(0, "Test 2 appendl fails!");
if appendl nil = nil
  then "Test 3 appendl OK!"
  else error(0, "Test 3 appendl fails!");

%------------------------- Exercise ------------------------
macro procedure nconcl x;
/* NCONCL(...) - destructive concatenation of all the
  lists. */
  expand(cdr x, 'nconc);

global '(b1 b2 b3);
b1 := '(a b);
b2 := '(c d);
b3 := '(e f);
if nconcl(b1, b2, b3) = '(a b c d e f)
  then "Test 1 nconcl OK!"
  else error(0, "Test 1 nconcl fails!");
if b1 = '(a b c d e f)
  then "Test 2 nconcl OK!"
  else error(0, "Test 2 nconcl fails!");
if b2 = '(c d e f)
  then "Test 3 nconcl OK!"
  else error(0, "Test 3 nconcl fails!");
if b3 = '(e f)
  then "Test 4 nconcl OK!"
  else error(0, "Test 4 nconcl fails!");


%------------------------- Exercise ------------------------
smacro procedure d(x1, y1, x2, y2);
/* D(X1, Y1, X2, Y2) - Euclidean distance between points
  (X1,Y1) -> (X2,Y2) */
sqrt((x1 - x2)^2 + (y1 - y2)^2);

% This fails with poor sqrt.
if d(0, 0, 3, 4) = 5.0
  then "Test 1 d OK!"
  else error(0, "Test 1 d Fails!");
if d(0, 0, 1, 1) = sqrt 2
  then "Test 2 d OK!"
  else error(0, "Test 2 d Fails!");

%------------------------- Exercise -------------------------
macro procedure pop x;
/* POP(X) - Assuming X is an identifier, pop the stack
  and return the popped value. */
(`(prog (!$V!$)
     (setq !$V!$ (car #v))
     (setq #v (cdr #v))
     (return !$V!$))) where v := cadr x;

xxx := '(A B);
if pop xxx eq 'A
  then "Test 1 POP ok!"
  else error(0, "Test 1 POP fails!");
if xxx = '(B) 
  then "Test 1 POP ok!"
  else error(0, "Test 1 POP fails!");
if pop xxx eq 'B
  then "Test 2 POP ok!"
  else error(0, "Test 2 POP fails!");
if xxx eq NIL
  then "Test 2 POP ok!"
  else error(0, "Test 2 POP fails!");

%------------------------- Exercise -------------------------

macro procedure push x;
/* PUSH(ST, V) - push V onto ST (an identifier) and 
  return V. */
`(progn (setq #st (cons #v #st))
        #v)
  where st := cadr x,
        v := caddr x;

if push(xxx, 'A) = 'A
  then "Test 1 push OK!"
  else error(0, "Test 1 push fails");
if xxx = '(A)
  then "Test 1 push OK!"
  else error(0, "Test 1 push fails");
if push(xxx, 'B) = 'B
  then "Test 2 push OK!"
  else error(0, "Test 2 push fails");
if xxx = '(B A)
  then "Test 2 push OK!"
  else error(0, "Test 2 push fails");
  
%------------------------- Exercise -------------------------

macro procedure format x;
/* FORMAT("str", ...) - A formatted print utility. It
  looks for %x things in str, printing everything else.
  A property of printf!-format will cause a call on
  the named function with the corresponding argument.
  This should return a print form to use. A property
  printf!-expand calls a function without an argument.
  Common controls are:
    %n new line
    %p prin2 call.
    %w prin1 call.
*/
begin scalar str, localstr, m;
  str := explode2 cadr x;
  x := cddr x;
loop: if null str then
    << if localstr then
          m := {'prin2, makestring reversip localstr} . m;
       return 'progn . reverse m  >>;
  if eqcar(str, '!%) then
     if cdr str then
       if fn := get(cadr str, 'printf!-format) then
         << if localstr then 
              << m := {'prin2, makestring reversip localstr} . m;
                 localstr := nil >>;
            m := apply(fn, {car x}) . m;
            x := cdr x;
            str := cddr str; 
            go to loop >>
       else if fn := get(cadr str, 'printf!-expand) then
         << if localstr then
              << m := {'prin2, makestring reverse localstr} . m;
                 localstr := nil >>;
            m := apply(fn, nil) . m;
            str := cddr str; 
            go to loop >>;
  localstr := car str . localstr;
  str := cdr str;
  go to loop
end;

expr procedure makestring l;
/* MAKESTRING(L) - convert the list of character L into
  a string. */
 compress('!" . append(l, '(!")));

expr procedure printf!-terpri;
/* PRINTF!-TERPRI() - Generates a TERPRI call for %n */
 '(terpri);
put('!n, 'printf!-expand, 'printf!-terpri);
put('!N, 'printf!-expand, 'printf!-terpri);

expr procedure printf!-prin1 x;
/* PRINTF!-PRIN1(X) - Generates a PRIN1 call for %w */
 {'prin1, x};
put('!w, 'printf!-format, 'printf!-prin1);
put('!W, 'printf!-format, 'printf!-prin1);

expr procedure printf!-prin2 x; 
/* PRINTF!-PRIN2(X) - Generates a PRIN2 call for %p */
 {'prin2, x};
put('!p, 'printf!-format, 'printf!-prin2);
put('!P, 'printf!-format, 'printf!-prin2);

%------------------------- Exercise -------------------------
macro procedure rmsg x;
/* RMSG("str", ...) - A formatted string utility. It
  looks for %x things in str, copying everything else.
  A property of rmsg!-format will cause a call on
  the named function with the corresponding argument.
  This should return a explode form to use. A property
  rmsg!-expand calls a function without an argument.
  Common controls are:
    %n new line
    %p explode2 call.
    %w explode call.
*/
begin scalar str, localstr, m;
  str := explode2 cadr x;
  x := cddr x;
loop: if null str then
    << if localstr then
          m := mkquote reversip localstr . m;
       return `(makestring (nconcl #@(reversip m)))  >>;
  if eqcar(str, '!%) then
     if cdr str then
       if fn := get(cadr str, 'rmsg!-format) then
         << if localstr then 
              << m := mkquote reversip localstr . m;
                 localstr := nil >>;
            m := apply(fn, {car x}) . m;
            x := cdr x;
            str := cddr str; 
            go to loop >>
       else if fn := get(cadr str, 'rmsg!-expand) then
         << if localstr then
              << m := mkquote reversip localstr . m;
                 localstr := nil >>;
            m := apply(fn, nil) . m;
            str := cddr str; 
            go to loop >>;
  localstr := car str . localstr;
  str := cdr str;
  go to loop
end;

expr procedure makestring l;
/* MAKESTRING(L) - convert the list of character L into
  a string. */
 compress('!" . append(l, '(!")));

expr procedure rmsg!-terpri;
/* RMSG!-TERPRI() - Generates an EOL. */
mkquote {!$eol!$};
put('!n, 'rmsg!-expand, 'rmsg!-terpri);
put('!N, 'rmsg!-expand, 'rmsg!-terpri);

expr procedure rmsg!-prin1 x;
/* RMSG!-PRIN1(X) - Generates an EXPLODE call */
 `(fixstr (explode #x));
put('!w, 'rmsg!-format, 'rmsg!-prin1);
put('!W, 'rmsg!-format, 'rmsg!-prin1);

expr procedure rmsg!-prin2 x; 
/* RMSG!-PRIN2(X) - Generates an EXPLODE2 call for x. */
 `(explode2 #x);
put('!p, 'rmsg!-format, 'rmsg!-prin2);
put('!P, 'rmsg!-format, 'rmsg!-prin2);

expr procedure fixstr x;
/* FIXSTR(X) - Double up "'s in x. */
if null x then nil
  else if eqcar(x, '!") then '!" . '!" . fixstr cdr x
  else car x . fixstr cdr x;


if rmsg "abc" = "abc"
  then "Test 1 rmsg OK!"
  else error(0, "Test 1 rmsg fails!");

if rmsg("Test %w test", 12) = "Test 12 test"
  then "Test 2 rmsg OK!"
  else error(0, "Test 2 rmsg fails!");

if rmsg("Test %w string", "foo") = "Test ""foo"" string"
  then "Test 3 rmsg OK!"
  else error(0, "Test 3 rmsg fails!");

if rmsg("Test %w now %p", "foo", "foo") = "Test ""foo"" now foo"
  then "Test 4 rmsg OK!"
  else error(0, "Test 4 rmsg fails!");

%------------------------- Exercise -------------------------
define CFLAG = T;

macro procedure ifcflag x;
/* IFCLFAG(X) - generate the code for X if CFLAG is non-NIL,
  otherwise generate NIL (this can't be used everywhere). */
if CFLAG then cadr x else nil;

ifCFLAG expr procedure pslfoo x; car x;
if getd 'pslfoo
  then "Test 1 ifCFLAG OK!"
  else error(0, "Test 1 ifCFLAG fails!");


% ##### Interactive Exercises #####

%------------------------- Exercise #2 -------------------------

/* Lists functions that have been embedded with count code. */
global '(EMBEDDED!*);
EMBEDDED!* := NIL;

expr procedure embed f;
/* EMBED(F) - wrap function F with counter code. Error if F is
   not interpreted. Put the information under property COUNT and
   add to the global list EMBEDDED!*. */
begin scalar def, args, nfn;
  if not(def := getd f) then  error(0, {f, "is undefined"});
  if codep cdr def then error(0, {f, "is not interpreted"});
  put(f, 'COUNT, 0);
  if f memq EMBEDDED!* then return NIL;
  EMBEDDED!* := f . EMBEDDED!*;
  putd(nfn := intern gensym(), car def, cdr def);
  putd(f, car def,
       {'lambda, caddr def,
        {'progn,
           {'put, mkquote f, mkquote 'COUNT,
                  {'add1, {'get, mkquote f, mkquote 'COUNT}}},
           nfn . caddr def}});
  return f
end;


expr procedure stats;
/* STATS() - list all the embedded functions and their 
   counts. */
for each f in EMBEDDED!*
    do << prin1 f; prin2 "  "; print get(f, 'COUNT) >>;


expr procedure pcnt x;
/* PCNT(X) - returns the number of dotted-pairs in X (vectors
  can hide dotted-pairs). */
if atom x then 0
  else 1 + pcnt car x + pcnt cdr x;

if embed 'pcnt eq 'pcnt 
  then "Test 1 embed OK!"
  else error(0, "Test 1 embed Fails!");
if get('pcnt, 'count) = 0
  then "Test 2 embed OK!"
  else error(0, "Test 2 embed Fails!");
if pcnt '(a . (b . c)) = 2
  then "Test 3 embed OK!"
  else error(0, "Test 3 embed Fails!");
if get('pcnt, 'COUNT) = 5
  then "Test 4 embed OK!"
  else error(0, "Test 4 embed Fails!");
if EMBEDDED!* = '(PCNT)
  then "Test 5 embed OK!"
  else error(0, "Test 5 embed Fails!");

% Just a visual check.
stats();


%   ##### Test the inspector module #####
%
% We set LINELENGTH to various values to check how good we do on output.
% Don't let the default screw up the test:
LINELENGTH 80;

% Describe some of the basic data types.
% Dotted-pairs.
describe '(a . b);

% Vectors;
global '(xvar);
xvar := mkvect 3;
describe xvar;

% Records.
record insprec /* A record for testing. */
  with
    field1 := 'a;
xvar := insprec();
describe xvar;
describe 'insprec;

% A code pointer (usually).
describe cdr getd 'car;

% Numbers.
describe 1;
describe 3.14159;

% Strings
describe "This is a string";

% identifiers of various sourts.
describe 'car;
describe 'a!-plain!-jane!-identifier;  
describe nil;   % This message is sort of funny in odd ways.


% Now let's get serious. Here's a global with no active comment. The
% remprop is something you shouldn't know about but allows us to run
% the test file multiple times and get the same results.
remprop('TheCow, 'NEWNAM);
DEFINE TheCow = "How now brown cow";
describe 'TheCow;

off saveactives;
/* I never saw a purple cow, I never hope to see one now. */
global '(PurpleCow);
describe 'PurpleCow;

on saveactives;
/* But I'd rather see one than be one! */
global '(Pcow);
describe 'Pcow;

% Now we march on to procedures.
% Here's one with no comment and we don't save it.
off saveactives;
remd 'comtest1;
expr procedure comtest1 x;
print x;
describe 'comtest1;

% Here's one with no comment and we do save it.
on saveactives;
remd 'comtest2;
expr procedure comtest2(x, y);
print x;
describe 'comtest2;

% Here's one with a comment but we don't save it.
off saveactives;
remd 'comtest3;
expr procedure comtest3(x, y, z);
/* You should never see this comment. */
print x;
describe 'comtest3;

% Here's one with a comment and we should see it.
on saveactives;
remd 'comtest4;
expr procedure comtest4(x, y, z, xx);
/* COMTEST4(X, Y, Z, XX) - A well commented routine. This routine
  does almost nothing, but a good article thereof. */
print x;
describe 'comtest4;

% Now try MACROS.

remd 'comtest5;
macro procedure comtest5 x;
/* COMTEST5(X) - A macro that doesn't really do much of anything. */
{'car, cadr x};
describe 'comtest5;


smacro procedure comtest6 x;
/* COMTEST6(X) - a SMACRO with an active comment. This smacro expands
  to take CAR of its argument. */
car x;
describe 'comtest6;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Module testing.

/* This is a test module which occurs at the top level just to make
  sure that the module type works. */
module testmodule;
endmodule;
describe 'testmodule;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Format testing. Put a big comment out there and look at it with
% various line lengths.

/* ********************
This is a test comment. We'll try do different things with it in
different contexts. Does it work?

  expr procedure fact n;
    if n < 2 then 1 else n * fact(n - 1);

Well hoop de doo! Is there anything else funny?

 +------------+----------+
 |  Column 1  |  Col. 2  |
 +------------+----------+
 | Aardvarks  |      345 |
 +------------+----------+
 | Zarfs      |        3 |
 +------------+----------+
/// */
global '(testvariable);

describe 'testvariable;
LINELENGTH 60;
describe 'testvariable;
LINELENGTH 50;
describe 'testvariable;
LINELENGTH 40;
describe 'testvariable;
LINELENGTH 30;
describe 'testvariable;
LINELENGTH 20;
describe 'testvariable;
LINELENGTH 10;
describe 'testvariable;


% ##### Records Package #####

global '(rec1 rec2);

% Simple test.
record rtest1;
rec1 := rtest1();

if rec1 neq array 'rtest1 then
  error(0, "Test 1 RECORD fails creation test!");
if null rtest1p rec1 then
  error(0, "Test 1 RECORD fails predicate test!");


% A record with two fields.
record rtest2 with field1 := 0, field2 := 1;

% Test default creation.
rec2 := rtest2();
if rec2 neq array('rtest2, 0, 1) then
  error(0, "Test 2 RECORD fails to create a record");
if null rtest2p rec2 then
  error(0, "Test 2 RECORD fails predicate test");
if rtest2p rec1 then
  error(0, "Test 2 RECORD fails to test record differences");



% Build a record with a predicate. Remove any old occurrence.
remd 'rtest3!?;
record rtest3 with field1 := 0, field2 := 1 has predicate = rtest3!?;

if not getd 'rtest3!? then 
  error(0, "Test 3 RECORD fails - no predicate built");
if rtest3!? rec2 then
  error(0, "Test 3 RECORD fails - predicate returns T on non RTEST3 record");
for each x in {'identifier, 12, 12.3, "a string", cdr getd 'car,
               '(a list), array("an", "array")}
  when rtest3!? x
  do error(0, {"Test 3 RECORD fails - predicate returns T on", x});

rec2 := rtest3();
if not rtest3!? rec2 then
  error(0, "Test 3 RECORD fails - predicate returns NIL on record");


% Check that the no-predicate option works.
remd 'rtest4p;      % Just to make sure.
record rtest4 with a := 34, b := 56 has no predicate;
if getd 'rtest4p then
  error(0, "Test 4 RECORD fails - NO PREDICATE option generates a predicate");


% Verify that the CONSTRUCTOR option works.
remd 'rtest5;
remd 'make-rtest5;
record rtest5 with r5a := 0, r5b := 1 has constructor;

if getd 'rtest5 then
  error(0, "Test 5 RECORD fails - CONSTRUCTOR generates simple constructor");
if not getd 'make-rtest5 then
  error(0, "Test 5 RECORD fails - CONSTRUCTOR doesn't generate constructor");
if not rtest5p make-rtest5() then
  error(0, "Test 5 RECORD fails - CONSTRUCTOR doesn't generate record");

% Verify that the named constructor works.
remd 'rtest6; remd 'please-make-rtest6;
record rtest6 with r6a := 0 has constructor = please!-make!-arecord;

if getd 'rtest6 then
  error(0, "Test 6 RECORD fails - CONSTRUCTOR generates simple constructor");
if getd 'make-rtest6 then
  error(0, "Test 6 RECORD fails - CONSTRUCTOR generates make- constructor");
if not getd 'please-make-arecord then
  error(0, "Test 6 RECORD fails - CONSTRUCTOR doesn't generate constructor");
if not rtest6p please-make-arecord() then
  error(0, "Test 6 RECORD fails - CONSTRUCTOR doesn't generate record");


end;
