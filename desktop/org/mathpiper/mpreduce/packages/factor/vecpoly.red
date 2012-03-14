MODULE VECPOLY;

% Authors: A. C. Norman and P. M. A. Moore, 1979;

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


FLUID '(CURRENT!-MODULUS SAFE!-FLAG);


%**********************************************************************;
% Routines for working with modular univariate polynomials
% stored as vectors. Used to avoid unwarranted storage management
% in the mod-p factorization process;


SAFE!-FLAG:=CARCHECK 0;


SYMBOLIC PROCEDURE COPY!-VECTOR(A,DA,B);
% Copy A into B;
 << FOR I:=0:DA DO
      PUTV(B,I,GETV(A,I));
    DA >>;

SYMBOLIC PROCEDURE TIMES!-IN!-VECTOR(A,DA,B,DB,C);
% Put the product of A and B into C and return its degree.
% C must not overlap with either A or B;
  BEGIN
    SCALAR DC,IC,W;
    IF DA#<0 OR DB#<0 THEN RETURN MINUS!-ONE;
    DC:=DA#+DB;
    FOR I:=0:DC DO PUTV(C,I,0);
    FOR IA:=0:DA DO <<
      W:=GETV(A,IA);
      FOR IB:=0:DB DO <<
        IC:=IA#+IB;
        PUTV(C,IC,MODULAR!-PLUS(GETV(C,IC),
          MODULAR!-TIMES(W,GETV(B,IB)))) >> >>;
    RETURN DC
  END;


SYMBOLIC PROCEDURE QUOTFAIL!-IN!-VECTOR(A,DA,B,DB);
% Overwrite A with (A/B) and return degree of result.
% The quotient must be exact;
    IF DA#<0 THEN DA
    ELSE IF DB#<0 THEN ERRORF "Attempt to divide by zero"
    ELSE IF DA#<DB THEN ERRORF "Bad degrees in QUOTFAIL-IN-VECTOR"
    ELSE BEGIN
      SCALAR DC;
      DC:=DA#-DB; % Degree of result;
      FOR I:=DC STEP -1 UNTIL 0 DO BEGIN
        SCALAR Q;
        Q:=MODULAR!-QUOTIENT(GETV(A,DB#+I),GETV(B,DB));
        FOR J:=0:DB#-1 DO
          PUTV(A,I#+J,MODULAR!-DIFFERENCE(GETV(A,I#+J),
            MODULAR!-TIMES(Q,GETV(B,J))));
        PUTV(A,DB#+I,Q)
      END;
      FOR I:=0:DB#-1 DO IF GETV(A,I) NEQ 0 THEN
        ERRORF "Quotient not exact in QUOTFAIL!-IN!-VECTOR";
      FOR I:=0:DC DO
        PUTV(A,I,GETV(A,DB#+I));
      RETURN DC
    END;


SYMBOLIC PROCEDURE REMAINDER!-IN!-VECTOR(A,DA,B,DB);
% Overwrite the vector A with the remainder when A is
% divided by B, and return the degree of the result;
  BEGIN
    SCALAR DELTA,DB!-1,RECIP!-LC!-B,W;
    IF DB=0 THEN RETURN MINUS!-ONE
    ELSE IF DB=MINUS!-ONE THEN ERRORF "ATTEMPT TO DIVIDE BY ZERO";
    RECIP!-LC!-B:=MODULAR!-MINUS MODULAR!-RECIPROCAL GETV(B,DB);
    DB!-1:=DB#-1; % Leading coeff of B treated specially, hence this;
    WHILE NOT((DELTA:=DA#-DB) #< 0) DO <<
      W:=MODULAR!-TIMES(RECIP!-LC!-B,GETV(A,DA));
      FOR I:=0:DB!-1 DO
        PUTV(A,I#+DELTA,MODULAR!-PLUS(GETV(A,I#+DELTA),
          MODULAR!-TIMES(GETV(B,I),W)));
      DA:=DA#-1;
      WHILE NOT(DA#<0) AND GETV(A,DA)=0 DO DA:=DA#-1 >>;
    RETURN DA
  END;

SYMBOLIC PROCEDURE EVALUATE!-IN!-VECTOR(A,DA,N);
% Evaluate A at N;
  BEGIN
    SCALAR R;
    R:=GETV(A,DA);
    FOR I:=DA#-1 STEP -1 UNTIL 0 DO
      R:=MODULAR!-PLUS(GETV(A,I),
        MODULAR!-TIMES(R,N));
    RETURN R
  END;

SYMBOLIC PROCEDURE GCD!-IN!-VECTOR(A,DA,B,DB);
% Overwrite A with the gcd of A and B. On input A and B are
% vectors of coefficients, representing polynomials
% of degrees DA and DB. Return DG, the degree of the gcd;
  BEGIN
    SCALAR W;
    IF DA=0 OR DB=0 THEN << PUTV(A,0,1); RETURN 0 >>
    ELSE IF DA#<0 OR DB#<0 THEN ERRORF "GCD WITH ZERO NOT ALLOWED";
TOP:
% Reduce the degree of A;
    DA:=REMAINDER!-IN!-VECTOR(A,DA,B,DB);
    IF DA=0 THEN << PUTV(A,0,1); RETURN 0 >>
    ELSE IF DA=MINUS!-ONE THEN <<
      W:=MODULAR!-RECIPROCAL GETV(B,DB);
      FOR I:=0:DB DO PUTV(A,I,MODULAR!-TIMES(GETV(B,I),W));
      RETURN DB >>;
% Now reduce degree of B;
    DB:=REMAINDER!-IN!-VECTOR(B,DB,A,DA);
    IF DB=0 THEN << PUTV(A,0,1); RETURN 0 >>
    ELSE IF DB=MINUS!-ONE THEN <<
      W:=MODULAR!-RECIPROCAL GETV(A,DA);
      IF NOT (W=1) THEN
        FOR I:=0:DA DO PUTV(A,I,MODULAR!-TIMES(GETV(A,I),W));
      RETURN DA >>;
    GO TO TOP
  END;



CARCHECK SAFE!-FLAG;


ENDMODULE;


END;
