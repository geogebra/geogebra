MODULE MODIFY;

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


FLUID '(!*TRA INTVAR);

EXPORTS MODIFY!-SQRTS,COMBINE!-SQRTS;

SYMBOLIC PROCEDURE MODIFY!-SQRTS(BASIS,SQRTL);
BEGIN
  SCALAR SQRTL!-IN!-SF,N,U,V,F;
  N:=UPBV BASIS;
  SQRTL!-IN!-SF:=FOR EACH U IN SQRTL COLLECT
                    !*Q2F SIMP ARGOF U;
  FOR I:=0:N DO BEGIN
    U:=GETV(BASIS,I);
    V:=SQRTSINSQ(U,INTVAR);
    % We have two tasks to perform,
    % the replacing of SQRT(A)*SQRT(B) by SQRT(A*B)
    % where relevant and the replacing of SQRT(A)
    % by SQRT(A*B) or 1 (depending on whether it occurs in
    % the numerator or the denominator).
    V:=setdiff(v,SQRTL);
    IF NULL V
      THEN GO TO NOCHANGE;
    U:=SQRT2TOP U;
    U:=MULTSQ(MODIFY2(NUMR U,V,SQRTL!-IN!-SF) ./ 1,
              1 ./ MODIFY2(DENR U,V,SQRTL!-IN!-SF));
    V:=SQRTSINSQ(U,INTVAR);
    V:=setdiff(v,SQRTL);
    IF V THEN <<
      IF !*TRA THEN <<
        PRINTC "Discarding element";
        PRINTSQ U >>;
      PUTV(BASIS,I,1 ./ 1) >>
      ELSE PUTV(BASIS,I,REMOVECMSQ U);
    F:=T;
  NOCHANGE:
    END;
  BASIS:=MKUNIQUEVECT BASIS;
  IF F AND !*TRA THEN <<
    PRINTC "Basis replaced by";
    MAPVEC(BASIS,FUNCTION PRINTSQ) >>;
  RETURN BASIS
  END;


SYMBOLIC PROCEDURE COMBINE!-SQRTS(BASIS,SQRTL);
BEGIN
  SCALAR SQRTL!-IN!-SF,N,U,V,F;
  N:=UPBV BASIS;
  SQRTL!-IN!-SF:=FOR EACH U IN SQRTL COLLECT
                    !*Q2F SIMP ARGOF U;
  FOR I:=0:N DO BEGIN
    U:=GETV(BASIS,I);
    V:=SQRTSINSQ(U,INTVAR);
    % We have one task to perform,
    % the replacing of SQRT(A)*SQRT(B) by SQRT(A*B)
    % where relevant.
    V:=setdiff(v,SQRTL);
    IF NULL V
      THEN GO TO NOCHANGE;
    U:=MULTSQ(MODIFY2(NUMR U,V,SQRTL!-IN!-SF) ./ 1,
              1 ./ MODIFY2(DENR U,V,SQRTL!-IN!-SF));
    PUTV(BASIS,I,U);
    F:=T;
  NOCHANGE:
    END;
  IF F AND !*TRA THEN <<
    PRINTC "Basis replaced by";
    MAPVEC(BASIS,FUNCTION PRINTSQ) >>;
  RETURN BASIS
  END;


SYMBOLIC PROCEDURE MODIFY2(SF,SQRTSIN,REALSQRTS);
IF ATOM SF
  THEN SF
  ELSE IF ATOM MVAR SF
    THEN SF
    ELSE IF EQCAR(MVAR SF,'SQRT) AND DEPENDSP(MVAR SF,INTVAR)
      THEN BEGIN
        SCALAR U,V,W,LCSF,SQRTSIN2,W2,LCSF2,TEMP;
        U:=!*Q2F SIMP ARGOF MVAR SF;
        V:=REALSQRTS;
        WHILE V AND NULL (W:=MODIFY!-QUOTF(CAR V,U))
          DO V:=CDR V;
        IF NULL V
          THEN <<
            IF !*TRA THEN <<
              PRINTC "Unable to modify (postponed)";
              PRINTSF !*KK2F MVAR SF >>;
            RETURN SF >>;
        V:=CAR V;
        % We must modify SQRT(U) into SQRT(V) if possible.
        LCSF:=LC SF;
        SQRTSIN2:=DELETE(MVAR SF,SQRTSIN);
        WHILE SQRTSIN2 AND (W NEQ 1) DO <<
          TEMP:=!*Q2F SIMP ARGOF CAR SQRTSIN2;
          IF (W2:=MODIFY!-QUOTF(W,TEMP)) AND
             (LCSF2:=MODIFY!-QUOTF(LCSF,!*KK2F CAR SQRTSIN2))
            THEN <<
              W:=W2;
              LCSF:=LCSF2 >>;
          SQRTSIN2:=CDR SQRTSIN2 >>;
        IF W = 1
          THEN RETURN ADDF(MULTF(LCSF,FORMSQRT V),
                           MODIFY2(RED SF,SQRTSIN,REALSQRTS));
                           % It is important to use FORMSQRT here since
                           % SIMPSQRT will recreate the factorisation
                           % we are trying to destroy.
          % Satisfactorily explained away.
        RETURN ADDF(MULTF(!*P2F LPOW SF,
                          MODIFY2(LC SF,SQRTSIN,REALSQRTS)),
                    MODIFY2(RED SF,SQRTSIN,REALSQRTS))
        END
      ELSE ADDF(MULTF(!*P2F LPOW SF,
                      MODIFY2(LC SF,SQRTSIN,REALSQRTS)),
                MODIFY2(RED SF,SQRTSIN,REALSQRTS));



%symbolic procedure modifydown(sf,sqrtl);
%if atom sf
%  then sf
%  else if atom mvar sf
%    then sf
%    else if eqcar(mvar sf,'sqrt) and
%            dependsp(mvar sf,intvar) and
%           not member(!*q2f simp argof mvar sf,sqrtl)
%      then addf(modifydown(lc sf,sqrtl),
%                modifydown(red sf,sqrtl))
%      else addf(multf(!*p2f lpow sf,
%                      modifydown(lc sf,sqrtl)),
%                modifydown(red sf,sqrtl));


% symbolic procedure modifyup(sf,sqrtl);
% if atom sf
%   then sf
%   else if atom mvar sf
%     then sf
%     else if eqcar(mvar sf,'sqrt) and
%             dependsp(mvar sf,intvar)
%       then begin
%         scalar u,v;
%         u:=!*q2f simp argof mvar sf;
%         if u member sqrtl
%         then return addf(multf(!*p2f lpow sf,
%                                 modifyup(lc sf,sqrtl)),
%                           modifyup(red sf,sqrtl));
%        v:=sqrtl;
%        while v and not modify!-quotf(car v,u)
%          do v:=cdr v;
%        if null v
%          then interr "No sqrt to upgrade to";
%       return addf(multf(!*kk2f simpsqrt2 car v,
%                          modifyup(lc sf,sqrtl)),
%                    modifyup(red sf,sqrtl))
%        end
%      else addf(multf(!*p2f lpow sf,
%                      modifyup(lc sf,sqrtl)),
%                modifyup(red sf,sqrtl));


SYMBOLIC PROCEDURE MODIFY!-QUOTF(U,V);
% Replacement for quotf, in that it gets sqrts right.
IF ATOM V OR ATOM MVAR V
  THEN QUOTF(U,V)
  ELSE IF U=V THEN 1
  ELSE BEGIN
    SCALAR SQ;
    SQ:=SQRT2TOP(U ./ V);
    IF INVOLVESF(DENR SQ,INTVAR)
      THEN RETURN NIL;
    IF NOT ONEP DENR SQ
      THEN IF NOT NUMBERP DENR SQ
        THEN INTERR "Gauss' lemma violated in modify"
        ELSE IF !*TRA
          THEN <<
            PRINTC "*** Denominator ignored in modify";
            PRINTC DENR SQ >>;
    RETURN NUMR SQ
    END;

ENDMODULE;

END;
