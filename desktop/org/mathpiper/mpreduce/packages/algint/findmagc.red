MODULE FINDMAGC;

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


FLUID '(!*tra MAGICLIST);

SYMBOLIC PROCEDURE FINDMAGIC L;
BEGIN
  SCALAR P,N,PVEC,M,INTVEC,MCOUNT,TEMP;
  % L is a list of things which must be made non-zero by means of
%   a suitable choice of values for the variables in MAGICLIST;
  L:=FOR EACH U IN L COLLECT
     << MAPC(MAGICLIST,FUNCTION (LAMBDA V;
                                 IF INVOLVESF(DENR U,V)
                                   THEN INTERR "Hard findmagic"));
        NUMR U >>;
  IF !*TRA THEN <<
    PRINTC "We must make the following non-zero:";
    MAPC(L,FUNCTION PRINTSF);
    PRINC "by suitable choice of ";
    PRINTC MAGICLIST >>;
  % Strategy is random choice in a space which has only finitely
%   many singular points;
  P:=0;
  N:=ISUB1 LENGTH MAGICLIST;
  PVEC:=MKVECT N;
  PUTV(PVEC,0,2);
  FOR I:=1:N DO
    PUTV(PVEC,I,NEXTPRIME GETV(PVEC,ISUB1 I));
  % Tactics are based on Godel (is this a mistake ??) and let P run
%   through numbers and take the prime factorization of them;
  INTVEC:=MKVECT N;
LOOP:
  P:=IADD1 P;
  IF !*TRA THEN <<
    PRINC "We try the number ";
    PRINTC P >>;
  M:=P;
  FOR I:=0:N DO <<
    MCOUNT:=0;
    WHILE CDR(TEMP:=DIVIDE(M,GETV(PVEC,I)))=0 DO <<
      MCOUNT:=IADD1 MCOUNT;
      M:=CAR TEMP >>;
    PUTV(INTVEC,I,MCOUNT) >>;
  IF M NEQ 1
    THEN GO TO LOOP;
  IF !*TRA THEN <<
    PRINTC "which corresponds to ";
    SUPERPRINT INTVEC >>;
  M:=NIL;
  TEMP:=MAGICLIST;
  FOR I:=0:N DO <<
    M:=((CAR TEMP).GETV(INTVEC,I)).M;
    TEMP:=CDR TEMP >>;
  % M is the list of substitutions corresponding to this value of P;
  TEMP:=L;
LOOP2:
  IF NULL NUMR algint!-SUBF(CAR TEMP,M)
    THEN GO TO LOOP;
  TEMP:=CDR TEMP;
  IF TEMP
    THEN GO TO LOOP2;
  IF !*TRA THEN <<
    PRINTC "which corresponds to the values:";
    SUPERPRINT M >>;
  RETURN M
  END;

ENDMODULE;

END;
