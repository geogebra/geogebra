MODULE FRACDI;

% Author: James H. Davenport.

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


FLUID '(BASIC!-LISTOFALLSQRTS BASIC!-LISTOFNEWSQRTS EXPSUB INTVAR
    SQRT!-INTVAR);

GLOBAL '(COATES!-FDI);

EXPORTS FDI!-PRINT,FDI!-REVERTSQ,FDI!-UPGRADE,
   FRACTIONAL!-DEGREE!-AT!-INFINITY;

% internal!-fluid '(expsub);

SYMBOLIC PROCEDURE FDI!-PRINT();
<< PRINC "We substitute ";
   PRINC INTVAR;
   PRINC "**";
   PRINC COATES!-FDI;
   PRINC " for ";
   PRINC INTVAR;
   PRINTC " in order to avoid fractional degrees at infinity" >>;


SYMBOLIC PROCEDURE FDI!-REVERTSQ U;
IF COATES!-FDI IEQUAL 1
  THEN U
  ELSE (FDI!-REVERT NUMR U) ./ (FDI!-REVERT DENR U);


SYMBOLIC PROCEDURE FDI!-REVERT U;
IF NOT INVOLVESF(U,INTVAR)
  THEN U
  ELSE ADDF(FDI!-REVERT RED U,
        !*MULTF(FDI!-REVERTPOW LPOW U,
            FDI!-REVERT LC U));


SYMBOLIC PROCEDURE FDI!-REVERTPOW POW;
IF NOT DEPENDSP(CAR POW,INTVAR)
  THEN (POW .* 1) .+ NIL
  ELSE IF CAR POW EQ INTVAR
    THEN BEGIN
      SCALAR V;
      V:=DIVIDE(CDR POW,COATES!-FDI);
      IF CDR POW=0
        THEN RETURN (MKSP(INTVAR,CAR POW) .* 1) .+ NIL
    ELSE INTERR "Unable to revert fdi";
      END
    ELSE IF EQ(CAR POW,'SQRT)
      THEN SIMPSQRT2 FDI!-REVERT !*Q2F SIMP ARGOF CAR POW
      ELSE INTERR "Unrecognised term to revert";


SYMBOLIC PROCEDURE FDI!-UPGRADE PLACE;
BEGIN
  SCALAR ANS,U,EXPSUB,N;
  N:=COATES!-FDI;
  FOR EACH U IN PLACE DO
    IF EQCAR(U:=RSUBS U,'EXPT)
      THEN N:=N / CADDR U;
      % if already upgraded, we must take account of this.
  IF N = 1
    THEN RETURN PLACE;
  EXPSUB:=LIST(INTVAR,'EXPT,INTVAR,N);
  ANS:=NCONC(BASICPLACE PLACE,LIST EXPSUB);
  EXPSUB:=LIST EXPSUB; % this prevents later nconc from causing trouble.
  U:=EXTENPLACE PLACE;
  WHILE U DO BEGIN
    SCALAR V,W,RFU;
    V:=FDI!-UPGR2 LFIRSTSUBS U;
    IF V IEQUAL 1
      THEN RETURN (U:=CDR U);
    IF EQCAR(RFU:=RFIRSTSUBS U,'MINUS)
      THEN W:=ARGOF RFU
      ELSE IF EQCAR(RFU,'SQRT)
        THEN W:=RFU
    ELSE INTERR "Unknown place format";
    W:=FDI!-UPGR2 W;
    IF W IEQUAL 1
      THEN INTERR "Place collapses under rewriting";
    IF EQCAR(RFU,'MINUS)
      THEN ANS:=NCONC(ANS,LIST LIST(V,'MINUS,W))
      ELSE ANS:=NCONC(ANS,LIST(V.W));
    U:=CDR U;
    RETURN
    END;
  SQRTSAVE(BASIC!-LISTOFALLSQRTS,
       BASIC!-LISTOFNEWSQRTS,
           BASICPLACE ANS);
  RETURN ANS
  END;


SYMBOLIC PROCEDURE FDI!-UPGR2 U;
BEGIN
  SCALAR V,MV;
% V:=SUBSTITUTESQ(SIMP U,EXPSUB);
% The above line doesn't work due to int(sqrt(x-1)/sqrt(x+1),x);
% where the attempt to make sqrt(x^2-1) is frustrated by the presence of
% sqrt(x-1) and sqrt(x+1) which get SIMPed (even after we allow for the
% NEWPLACE call in COATES
  V:=XSUBSTITUTEP(U,EXPSUB);
  IF DENR V NEQ 1
    THEN GOTO ERROR;
  V:=NUMR V;
LOOP:
  IF ATOM V
    THEN RETURN V;
  IF RED V
    THEN GO TO ERROR;
  MV:=MVAR V;
  IF (NOT DEPENDSP(MV,INTVAR)) OR (MV EQ INTVAR)
    THEN <<
      V:=LC V;
      GOTO LOOP >>;
  IF EQCAR(MV,'SQRT) AND NOT SQRTSINSF(LC V,NIL,INTVAR)
      THEN RETURN MV;
ERROR:
  PRINTC "*** Format error ***";
  PRINC "unable to go x:=x**";
  PRINTC COATES!-FDI;
  SUPERPRINT U;
  interr "Failure to make integral at infinity"
  END;


SYMBOLIC PROCEDURE FRACTIONAL!-DEGREE!-AT!-INFINITY SQRTS;
IF SQRTS
  THEN LCMN(FDI2 CAR SQRTS,FRACTIONAL!-DEGREE!-AT!-INFINITY CDR SQRTS)
  ELSE 1;


SYMBOLIC PROCEDURE FDI2 U;
   % Returns the denominator of the degree of x at infinity
   % in the sqrt expression u.
BEGIN
  SCALAR N;
  U:=SUBSTITUTESQ(SIMP U,LIST LIST(INTVAR,'QUOTIENT,1,INTVAR));
  N:=0;
  WHILE INVOLVESQ(U,SQRT!-INTVAR) DO <<
    N:=IADD1 N;
    U:=SUBSTITUTESQ(U,LIST LIST(INTVAR,'EXPT,INTVAR,2)) >>;
  RETURN (2**N)
  END;


SYMBOLIC PROCEDURE LCMN(I,J);
  I*J/GCDN(I,J);

% unfluid '(expsub);

ENDMODULE;

END;
