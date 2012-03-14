MATRIX M(3,3)$
M(1,1) := 18*COS(Q3)*COS(Q2)*M30*P**2 - 9*SIN(Q3)**2*P**2*M30
	  - SIN(Q3)**2*J30Y + SIN(Q3)**2*J30Z + P**2*M10
	  + 18*P**2*M30 + J10Y + J30Y;
M(2,1) :=
M(1,2) := 9*COS(Q3)*COS(Q2)*M30*P**2 - SIN(Q3)**2*J30Y +
	  SIN(Q3)**2*J30Z - 9*SIN(Q3)**2*M30*P**2 + J30Y +
	  9*M30*P**2;
M(3,1) :=
M(1,3) := -9*SIN(Q3)*SIN(Q2)*M30*P**2;
M(2,2) := -SIN(Q3)**2*J30Y + SIN(Q3)**2*J30Z - 9*SIN(Q3)**2
	   *M30*P**2 + J30Y + 9*M30*P**2;
M(3,2) :=
M(2,3) := 0;
M(3,3) := 9*M30*P**2 + J30X;

GENTRANLANG!* := 'FORTRAN$
FORTLINELEN!* := 72$

GENTRAN LITERAL "C", CR!*,
		"C", TAB!*, "*** COMPUTE VALUES FOR MATRIX M ***", CR!*,
		"C", CR!*$

FOR j:=1:3 DO
    FOR k:=j:3 DO
	 GENTRAN M(j,k) ::=: M(j,k)$

GENTRAN LITERAL "C", CR!*,
		"C", TAB!*, "*** COMPUTE VALUES FOR INVERSE MATRIX ***",
		     CR!*,
		"C", CR!*$

SHARE var$
FOR j:=1:3 DO
    FOR k:=j:3 DO
	IF M(j,k) NEQ 0 THEN
	<<
	    var := TEMPVAR NIL;
	    MARKVAR var;
	    M(j,k) := var;
	    M(k,j) := var;
	    GENTRAN
		EVAL(var) := M(EVAL(j),EVAL(k))
        >>$

COMMENT -- Contents of Matrix M: --$
M := M;

MATRIX MXINV(3,3)$
MXINV := M**(-1)$

FOR j:=1:3 DO
    FOR k:=j:3 DO
	GENTRAN MXINV(j,k) ::=: MXINV(j,k)$

GENTRAN
   for j:=1:3 do
       for k:=j+1:3 do
       <<
	   m(k,j) := m(j,k);
	   mxinv(k,j) := mxinv(j,k)
        >>$

END$
