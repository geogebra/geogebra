MODULE ALGNUMS;

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


EXPORTS DENR!-ALGNO;


SYMBOLIC PROCEDURE DENR!-ALGNO U;
% Returns the true denominator of the algebraic number u.
BEGIN
  SCALAR SQLIST,N,M,U!*!*J,D;
  U!*!*J:=1 ./ 1;
  SQLIST:=SQRTSINSQ(U,NIL);
  SQLIST:=MULTBYALLCOMBINATIONS(LIST(1 ./ 1),
                               FOR EACH U IN SQLIST
                                 COLLECT !*KK2Q U);
  N:=0;
  SQLIST:=FOR EACH U IN SQLIST COLLECT
    (NUMR U) . (N:=IADD1 N);
    % format is of an associtaion list.
  N:=LENGTH SQLIST;
  M:=MKVECT N;
  FOR I:=0:N DO
    PUTV(M,I,MKVECT2(N,NIL ./ 1));
  PUTV(GETV(M,0),CDR ASSOC(1,SQLIST),1 ./ 1);
  % initial matrix is now set up.
  FOR J:=1:N DO BEGIN
    SCALAR V,W;
    U!*!*J:=!*MULTSQ(U!*!*J,U);
    DUMP!-SQRTS!-COEFFS(U!*!*J,SQLIST,GETV(M,J));
    V:=FIRSTLINEARRELATION(M,N);
    IF NULL V
      THEN RETURN;
    IF LAST!-NON!-ZERO V > J
      THEN RETURN;
    IF (W:=GETV(V,J)) NEQ (1 ./ 1)
      THEN <<
        W:=!*INVSQ W;
        FOR I:=0:J DO
          PUTV(V,I,!*MULTSQ(W,GETV(V,I))) >>;
    M:=V;
    N:=J;
    RETURN
    END;
  % Now m is a monic polynomial, minimal for u, of degree n.
  D:=1;
  FOR I:=0:ISUB1 N DO BEGIN
    SCALAR V,PRIME;
    V:=DENR GETV(M,I);
    PRIME:=2;
LOOP:
    IF V = 1
      THEN RETURN;
    IF CDR DIVIDE(V,PRIME) neq 0 THEN PRIME:=NEXTPRIME(PRIME)
      ELSE <<
        D:=D*PRIME;
        FOR I:=0:N DO
          PUTV(V,I,MULTSQ(GETV(V,I),1 ./ (PRIME ** (N-I)) )) >>;
    GO TO LOOP;
    END;
  RETURN D;
  END;


SYMBOLIC PROCEDURE DUMP!-SQRTS!-COEFFS(U,SQLIST,VEC);
BEGIN
  SCALAR W;
  DUMP!-SQRTS!-COEFFS2(NUMR U,SQLIST,VEC,1);
  U:=1 ./ DENR U;
  IF DENR U NEQ 1
    THEN FOR I:=0:UPBV VEC DO
      IF NUMR(W:=GETV(VEC,I))
        THEN PUTV(VEC,I,!*MULTSQ(U,W));
  END;


SYMBOLIC PROCEDURE DUMP!-SQRTS!-COEFFS2(U,SQLIST,VEC,SQRTSSOFAR);
IF NULL U
  THEN NIL
  ELSE IF NUMBERP U
    THEN PUTV(VEC,CDR ASSOC(SQRTSSOFAR,SQLIST),U)
    ELSE <<
      DUMP!-SQRTS!-COEFFS2(RED U,SQLIST,VEC,SQRTSSOFAR);
      DUMP!-SQRTS!-COEFFS2(LC U,SQLIST,VEC,!*MULTF(SQRTSSOFAR,
                                                   !*K2F MVAR U)) >>;


SYMBOLIC PROCEDURE LAST!-NON!-ZERO VEC;
BEGIN
  SCALAR N;
  FOR I:=0:UPBV VEC DO
    IF NUMR GETV(VEC,I)
      THEN N:=I;
  RETURN N
  END;

ENDMODULE;

END;
