MODULE FINITISE;

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


FLUID '(!*tra INTVAR);

EXPORTS FINITISE;
IMPORTS NEWPLACE,GETSQRTSFROMPLACES,INTERR,COMPLETEPLACES2,SQRTSIGN;
IMPORTS MKILIST,EXTENPLACE;


SYMBOLIC PROCEDURE FINITISE(PLACES,MULTS);
BEGIN
  SCALAR PLACESMISC,MULTSMISC,M,N,SQRTS;
  SCALAR PLACES0,MULTS0,PLACESINF,MULTSINF;
  NEWPLACE LIST (INTVAR.INTVAR);
    % fix the disaster with 1/sqrt(x**2-1)
    % (but with no other 1/sqrt(x**2-k).
  SQRTS:=GETSQRTSFROMPLACES PLACES;
  PLACESMISC:=PLACES;
  MULTSMISC:=MULTS;
  N:=0;
  WHILE PLACESMISC DO <<
    IF EQCAR(RFIRSTSUBS CAR PLACESMISC,'QUOTIENT)
        AND (N > CAR MULTSMISC)
      THEN <<
        N:=CAR MULTSMISC;
        M:=MULTIPLICITY!-FACTOR CAR PLACESMISC >>;
    PLACESMISC:=CDR PLACESMISC;
    MULTSMISC:=CDR MULTSMISC >>;
  IF N = 0
    THEN INTERR "Why did we call finitise ??";
  % N must be corrected to allow for our representation of
  % multiplicities at places where X is not the local parameter.
  N:=DIVIDE(N,M);
  IF CDR N neq 0 and !*TRA
    THEN PRINTC
     "Cannot get the poles moved precisely because of ramification";
   IF (CDR N) < 0
     THEN N:=(-1) + CAR N
     ELSE N:=CAR N;
        % The above 3 lines (as a replacement for the line below)
        % inserted JHD 06 SEPT 80.
%  n:=car n;
% ***** not true jhd 06 sept 80 *****;
    % This works because, e.g., DIVIDE(-1,2) is -1 remainder 1.
    % Note that N is actually negative.
  % We now wish to divide by X**N, thus increasing
  % the degrees of all infinite places by N and
  % decreasing the degrees of all places lying over 0.
  WHILE PLACES DO <<
    IF ATOM RFIRSTSUBS CAR PLACES
      THEN <<
        PLACES0:=(CAR PLACES).PLACES0;
        MULTS0:=(CAR MULTS).MULTS0 >>
      ELSE IF CAR RFIRSTSUBS CAR PLACES EQ 'QUOTIENT
        THEN <<
          PLACESINF:=(CAR PLACES).PLACESINF;
          MULTSINF:=(CAR MULTS).MULTSINF >>
        ELSE <<
          PLACESMISC:=(CAR PLACES).PLACESMISC;
          MULTSMISC:=(CAR MULTS).MULTSMISC >>;
    PLACES:=CDR PLACES;
    MULTS:=CDR MULTS >>;
  IF PLACES0
    THEN <<
      PLACES0:=COMPLETEPLACES2(PLACES0,MULTS0,SQRTS);
      MULTS0:=CDR PLACES0;
      PLACES0:=CAR PLACES0;
      M:=MULTIPLICITY!-FACTOR CAR PLACES0;
      MULTS0:=FOR EACH U IN MULTS0 COLLECT U+N*M >>
    ELSE <<
      PLACES0:=FOR EACH U IN SQRTSIGN(SQRTS,INTVAR)
                 COLLECT (INTVAR.INTVAR).U;
      MULTS0:=MKILIST(PLACES0,N * (MULTIPLICITY!-FACTOR CAR PLACES0))>>;
  PLACESINF:=COMPLETEPLACES2(PLACESINF,
                             MULTSINF,
                             FOR EACH U IN EXTENPLACE CAR PLACESINF
                               COLLECT LSUBS U);
  MULTSINF:=CDR PLACESINF;
  PLACESINF:=CAR PLACESINF;
  WHILE PLACESINF DO <<
    M:=MULTIPLICITY!-FACTOR CAR PLACESINF;
    IF (CAR MULTSINF) NEQ N*M
      THEN <<
        PLACESMISC:=(CAR PLACESINF).PLACESMISC;
        MULTSMISC:=(CAR MULTSINF -N*M).MULTSMISC >>;
      % This test ensures that we do not add places
      % with a multiplicity of zero.
    PLACESINF:=CDR PLACESINF;
    MULTSINF:=CDR MULTSINF >>;
  RETURN LIST(NCONC(PLACES0,PLACESMISC),
              NCONC(MULTS0,MULTSMISC),
              -N)
  END;


SYMBOLIC PROCEDURE MULTIPLICITY!-FACTOR PLACE;
BEGIN
  SCALAR N;
  N:=1;
  FOR EACH U IN PLACE DO
    IF (LSUBS U EQ INTVAR) AND
        EQCAR(RSUBS U,'EXPT)
      THEN N:=N*(CADDR RSUBS U);
  RETURN N
  END;

ENDMODULE;

END;
