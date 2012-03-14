MODULE PLACES;

% Author: James H. Davenport.

FLUID '(BASIC!-LISTOFALLSQRTS
        BASIC!-LISTOFNEWSQRTS
        INTVAR
        LISTOFALLSQRTS
        LISTOFNEWSQRTS
        SQRT!-INTVAR
        SQRT!-PLACES!-ALIST
        SQRTS!-IN!-INTEGRAND);

EXPORTS GETSQRTSFROMPLACES,SQRTSINPLACES,GET!-CORRECT!-SQRTS,BASICPLACE,
        EXTENPLACE,EQUALPLACE,PRINTPLACE;



% Function to manipulate places
% a place is stored as a list of substitutions
% substitutions (x.f(x)) define the algrbraic number
% of which this place is an extension,
% while places (f(x).g(x)) define the extension.
%    currently g(x( is list ('minus,f(x))
%       or similar,e.g. (sqrt(sqrt x)).(sqrt(-sqrt x)).

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




% Given a list of places, produces a list of all
% the SQRTs in it that depend on INTVAR.
SYMBOLIC PROCEDURE GETSQRTSFROMPLACES PLACES;
  % The following loop finds all the SQRTs for a basis,
  % taking account of BASICPLACEs.
BEGIN
  SCALAR BASIS,V,B,C,VV;
  FOR EACH U IN PLACES DO <<
    V:=ANTISUBS(BASICPLACE U,INTVAR);
    VV:=SQRTSINSQ (SUBSTITUTESQ(!*KK2Q INTVAR,V),INTVAR);
      % We must go via SUBSTITUTESQ to get parallel
      % substitutions performed correctly.
    IF VV
      THEN VV:=SIMP ARGOF CAR VV;
    FOR EACH W IN EXTENPLACE U DO <<
      B:=SUBSTITUTESQ(SIMP LSUBS W,V);
      B:=DELETE(SQRT!-INTVAR,SQRTSINSQ(B,INTVAR));
      FOR EACH U IN B DO
        FOR EACH V IN DELETE(U,B) DO
          IF DEPENDSP(V,U)
            THEN B:=DELETE(U,B);
            % remove all the "inner" items, since they will
            % be accounted for anyway.
      IF LENGTH B IEQUAL 1
        THEN B:=CAR B
 ELSE B:=MVAR NUMR SIMPSQRTSQ MAPPLY(FUNCTION !*MULTSQ,
                                FOR EACH U IN B COLLECT SIMP ARGOF U);
      IF VV AND NOT (B MEMBER SQRTS!-IN!-INTEGRAND)
        THEN <<
          C:=NUMR MULTSQ(SIMP ARGOF B,VV);
          C:=CAR SQRTSINSF(SIMPSQRT2 C,NIL,INTVAR);
   IF C MEMBER SQRTS!-IN!-INTEGRAND
            THEN B:=C >>;
      IF NOT (B MEMBER BASIS)
        THEN BASIS:=B.BASIS >> >>;
  % The following loop deals with the annoying case of, say,
  % (X DIFFERENCE X 1) (X EXPT X 2) which should give rise to
  % SQRT(X-1).
  FOR EACH U IN PLACES DO BEGIN
    V:=CDR U;
    IF NULL V OR (CAR RFIRSTSUBS V NEQ 'EXPT)
      THEN RETURN;
    U:=SIMP!* SUBST(LIST('MINUS,INTVAR),INTVAR,RFIRSTSUBS U);
    WHILE V AND (CAR RFIRSTSUBS V EQ 'EXPT) DO <<
      U:=SIMPSQRTSQ U;
      V:=CDR V;
      BASIS:=UNION(BASIS,DELETE(SQRT!-INTVAR,SQRTSINSQ(U,INTVAR))) >>
    END;
  RETURN REMOVE!-EXTRA!-SQRTS BASIS
  END;



SYMBOLIC PROCEDURE SQRTSINPLACES U;
% Note the difference between this procedure and
% the previous one: this one does not take account
% of the BASICPLACE component (& is pretty useless).
IF NULL U
  THEN NIL
  ELSE SQRTSINTREE(FOR EACH V IN CAR U COLLECT LSUBS V,
                   INTVAR,
                   SQRTSINPLACES CDR U);



%symbolic procedure placesindiv places;
% Given a list of places (i.e. a divisor),
% produces a list of all the SQRTs on which the places
% explicitly depend.
%begin scalar v;
%  for each u in places do
%    for each uu in u do
%      if not (lsubs uu member v)
%        then v:=(lsubs uu) . v;
%  return v
%  end;



SYMBOLIC PROCEDURE GET!-CORRECT!-SQRTS U;
% u is a basicplace.
BEGIN
  SCALAR V;
  V:=ASSOC(U,SQRT!-PLACES!-ALIST);
  IF V
    THEN <<
      V:=CDR V;
      LISTOFALLSQRTS:=CDR V;
      LISTOFNEWSQRTS:=CAR V
      >>
    ELSE <<
      LISTOFNEWSQRTS:=BASIC!-LISTOFNEWSQRTS;
      LISTOFALLSQRTS:=BASIC!-LISTOFALLSQRTS
      >>;
  RETURN NIL
  END;



%symbolic procedure change!-place(old,new);
%% old and new are basicplaces;
%begin
%  scalar v;
%  v:=assoc(new,sqrt!-places!-alist);
%  if v
%    then sqrtsave(cddr v,cadr v,old)
%    else <<
%      listofnewsqrts:=basic!-listofnewsqrts;
%      listofallsqrts:=basic!-listofallsqrts
%      >>;
%  return nil
%  end;



SYMBOLIC PROCEDURE BASICPLACE(U);
% Returns the basic part of a place.
IF NULL U
  THEN NIL
  ELSE IF ATOM CAAR U
    THEN (CAR U).BASICPLACE CDR U
    ELSE NIL;



SYMBOLIC PROCEDURE EXTENPLACE(U);
% Returns the extension part of a place.
IF U AND ATOM CAAR U
  THEN EXTENPLACE CDR U
  ELSE U;



SYMBOLIC PROCEDURE EQUALPLACE(A,B);
% Sees if two extension places represent the same place or not.
IF NULL A
  THEN IF NULL B
    THEN T
    ELSE NIL
  ELSE IF NULL B
    THEN NIL
    ELSE IF MEMBER(CAR A,B)
      THEN EQUALPLACE(CDR A,DELETE(CAR A,B))
      ELSE NIL;



SYMBOLIC PROCEDURE REMOVE!-EXTRA!-SQRTS BASIS;
BEGIN
  SCALAR BASIS2,SAVE;
  SAVE:=BASIS2:=FOR EACH U IN BASIS COLLECT !*Q2F SIMP ARGOF U;
  FOR EACH U IN BASIS2 DO
    FOR EACH V IN DELETE(U,BASIS2) DO
      IF QUOTF(V,U)
        THEN BASIS2:=DELETE(V,BASIS2);
  IF BASIS2 EQ SAVE
    THEN RETURN BASIS
    ELSE RETURN FOR EACH U IN BASIS2 COLLECT LIST('SQRT,PREPF U)
  END;



SYMBOLIC PROCEDURE PRINTPLACE U;
BEGIN
  SCALAR A,N,V;
  A:=RFIRSTSUBS U;
  PRINC (V:=LFIRSTSUBS U);
  PRINC "=";
  IF ATOM A
    THEN PRINC "0"
    ELSE IF (CAR A EQ 'QUOTIENT) AND (CADR A=1)
      THEN PRINC "infinity"
      ELSE <<
 N:=NEGSQ ADDSQ(!*KK2Q V,NEGSQ SIMP!* A);
% NEGSQ added JHD 22.3.87 - the previous value was wrong.
% If the substitution is (X-v) then this takes -v to 0,
% so the place was at -v.
        IF (NUMBERP NUMR N) AND (NUMBERP DENR N)
          THEN <<
            PRINC NUMR N;
            IF NOT ONEP DENR N
              THEN <<
                PRINC " / ";
                PRINC DENR N >> >>
          ELSE <<
            IF DEGREEIN(NUMR N,INTVAR) > 1
             THEN PRINTC "Any root of:";
            PRINTSQ N;
            IF CDR U
              THEN PRINC "at the place " >> >>;
  U:=CDR U;
  IF NULL U
    THEN GOTO NL!-RETURN;
  N:=1;
  WHILE U AND (CAR RFIRSTSUBS U EQ 'EXPT) DO <<
    N:=N * CADDR RFIRSTSUBS U;
    U:=CDR U >>;
  IF N NEQ 1 THEN <<
    TERPRI!* NIL;
    prin2 " ";
    PRINC V;
    PRINC "=>";
    PRINC V;
    PRINC "**";
    PRINC N >>;
  WHILE U DO <<
    IF CAR RFIRSTSUBS U EQ 'MINUS
      THEN PRINC "-"
      ELSE PRINC "+";
    U:=CDR U >>;
NL!-RETURN:
  TERPRI();
  RETURN
  END;



SYMBOLIC PROCEDURE DEGREEIN(SF,VAR);
IF ATOM SF
  THEN 0
  ELSE IF MVAR SF EQ VAR
    THEN LDEG SF
    ELSE MAX(DEGREEIN(LC SF,VAR),DEGREEIN(RED SF,VAR));

ENDMODULE;

END;
