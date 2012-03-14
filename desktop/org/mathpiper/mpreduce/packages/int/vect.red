MODULE VECT;  % Vector support routines.

% Authors: Mary Ann Moore and Arthur C. Norman.
% Modified by: James H. Davenport.

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


EXPORTS MKUNIQUEVECT,MKVEC,MKVECF2Q,MKIDENM,COPYVEC,VECSORT,SWAP,
        NON!-NULL!-VEC,MKVECT2;

SYMBOLIC PROCEDURE MKUNIQUEVECT V;
BEGIN SCALAR U,N;
  N:=UPBV V;
  FOR I:=0:N DO BEGIN
    SCALAR UU;
    UU:=GETV(V,I);
    IF NOT (UU MEMBER U)
      THEN U:=UU.U
    END;
  RETURN MKVEC U
  END;

SYMBOLIC PROCEDURE MKVEC(L);
BEGIN SCALAR V,I;
  V:=MKVECT(ISUB1 LENGTH L);
  I:=0;
  WHILE L DO <<PUTV(V,I,CAR L); I:=IADD1 I; L:=CDR L>>;
  RETURN V
  END;

SYMBOLIC PROCEDURE MKVECF2Q(L);
BEGIN
  SCALAR V,I,LL;
  V:=MKVECT(ISUB1 LENGTH L);
  I:=0;
  WHILE L DO <<
    LL:=CAR L;
    IF LL = 0 THEN LL:=NIL;
    PUTV(V,I,!*F2Q LL);
    I:=IADD1 I;
    L:=CDR L >>;
  RETURN V
  END;

SYMBOLIC PROCEDURE MKIDENM N;
BEGIN
  SCALAR ANS,U;
  SCALAR C0,C1;
  C0:=NIL ./ 1;
  C1:= 1 ./ 1;
  % constants.
  ANS:=MKVECT(N);
  FOR I:=0 STEP 1 UNTIL N DO <<
    U:=MKVECT N;
    FOR J:=0 STEP 1 UNTIL N DO
      IF I IEQUAL J
        THEN PUTV(U,J,C1)
        ELSE PUTV(U,J,C0);
    PUTV(ANS,I,U) >>;
  RETURN ANS
  END;

SYMBOLIC PROCEDURE COPYVEC(V,N);
   BEGIN SCALAR NEW;
    NEW:=MKVECT(N);
    FOR I:=0:N DO PUTV(NEW,I,GETV(V,I));
    RETURN NEW
   END;

SYMBOLIC PROCEDURE VECSORT(U,L);
% Sorts vector v of numbers into decreasing order.
% Performs same interchanges of all vectors in the list l.
BEGIN
  SCALAR J,K,N,V,W;
  N:=UPBV U;% elements 0...n exist.
  % algorithm used is a bubble sort.
  FOR I:=1:N DO BEGIN
    V:=GETV(U,I);
    K:=I;
  LOOP:
    J:=K;
    K:=ISUB1 K;
    W:=GETV(U,K);
    IF V<=W
      THEN GOTO ORDERED;
    PUTV(U,K,V);
    PUTV(U,J,W);
    MAPC(L,FUNCTION (LAMBDA U;SWAP(U,J,K)));
    IF K>0
      THEN GOTO LOOP;
  ORDERED:
    END;
  RETURN NIL
  END;

SYMBOLIC PROCEDURE SWAP(U,J,K);
IF NULL U
  THEN NIL
  ELSE BEGIN
    SCALAR V;
    %swaps elements i,j of vector u.
    V:=GETV(U,J);
    PUTV(U,J,GETV(U,K));
    PUTV(U,K,V)
    END;

SYMBOLIC PROCEDURE NON!-NULL!-VEC V;
BEGIN
  SCALAR CNT;
  CNT := 0;
  FOR I:=0:UPBV V DO
    IF GETV(V,I)
      THEN CNT:=IADD1 CNT;
  RETURN CNT
  END;

SYMBOLIC PROCEDURE MKVECT2(N,INITIAL);
BEGIN
  SCALAR U;
  U:=MKVECT N;
  FOR I:=0:N DO
    PUTV(U,I,INITIAL);
  RETURN U
  END;

ENDMODULE;

END;
