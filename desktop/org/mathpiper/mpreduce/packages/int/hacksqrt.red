MODULE HACKSQRT;  % Routines for manipulation of sqrt expressions.

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


FLUID '(NESTEDSQRTS THISPLACE);

EXPORTS SQRTSINTREE,SQRTSINSQ,SQRTSINSQL,SQRTSINSF,SQRTSIGN;
EXPORTS DEGREENEST,SORTSQRTS;

IMPORTS MKVECT,INTERR,GETV,DEPENDSP,UNION;

SYMBOLIC PROCEDURE SQRTSINTREE(U,VAR,SLIST);
% Adds to slist all the sqrts in the prefix-type tree u.
IF ATOM U
  THEN SLIST
  ELSE IF CAR U EQ '!*SQ
    THEN UNION(SLIST,SQRTSINSQ(CADR U,VAR))
    ELSE IF CAR U EQ 'SQRT
      THEN IF DEPENDSP(ARGOF U,VAR)
        THEN <<
          SLIST:=SQRTSINTREE(ARGOF U,VAR,SLIST);
          %  nested square roots
          IF MEMBER(U,SLIST)
            THEN SLIST
            ELSE U.SLIST >>
        ELSE SLIST
      ELSE SQRTSINTREE(CAR U,VAR,SQRTSINTREE(CDR U,VAR,SLIST));


SYMBOLIC PROCEDURE SQRTSINSQ(U,VAR);
   % Returns list of all sqrts in sq.
   SQRTSINSF(DENR U,SQRTSINSF(NUMR U,NIL,VAR),VAR);


SYMBOLIC PROCEDURE SQRTSINSQL(U,VAR);
% Returns list of all sqrts in sq list.
IF NULL U
  THEN NIL
  ELSE SQRTSINSF(DENR CAR U,
      SQRTSINSF(NUMR CAR U,SQRTSINSQL(CDR U,VAR),VAR),VAR);


SYMBOLIC PROCEDURE SQRTSINSF(U,SLIST,VAR);
% Adds to slist all the sqrts in sf.
IF DOMAINP U OR NULL U
  THEN SLIST
  ELSE <<
    IF  EQCAR(MVAR U,'SQRT) AND
        DEPENDSP(ARGOF MVAR U,VAR) AND
        NOT MEMBER(MVAR U,SLIST)
      THEN BEGIN
        SCALAR SLIST2;
        SLIST2:=SQRTSINTREE(ARGOF MVAR U,VAR,NIL);
        IF SLIST2
          THEN <<
            NESTEDSQRTS:=T;
            SLIST:=UNION(SLIST2,SLIST) >>;
        SLIST:=(MVAR U).SLIST
        END;
    SQRTSINSF(LC U,SQRTSINSF(RED U,SLIST,VAR),VAR) >>;


SYMBOLIC PROCEDURE EASYSQRTSIGN(SLIST,THINGS);
% This procedure builds a list of all substitutions for all possible
% combinations of square roots in list.
IF NULL SLIST
  THEN THINGS
  ELSE EASYSQRTSIGN(CDR SLIST,
                    NCONC(MAPCONS(THINGS,(CAR SLIST).(CAR SLIST)),
                          MAPCONS(THINGS,
                                  LIST(CAR SLIST,'MINUS,CAR SLIST))));

SYMBOLIC PROCEDURE HARDSQRTSIGN(SLIST,THINGS);
% This procedure fulfils the same role for nested sqrts
% ***assumption: the simpler sqrts come further up the list.
IF NULL SLIST
  THEN THINGS
  ELSE BEGIN
    SCALAR THISPLACE,ANSWERS,POS,NEG;
    THISPLACE:=CAR SLIST;
    ANSWERS:= for each u in THINGS collect SUBLIS(U,THISPLACE) . U;
    POS := for each u in ANSWERS collect (THISPLACE . CAR U) . CDR U;
    % pos is sqrt(f) -> sqrt(innersubst f)
    NEG := for each u in ANSWERS
       collect {THISPLACE,'MINUS,CAR U} . CDR U;
    % neg is sqrt(f) -> -sqrt(innersubst f)
    RETURN HARDSQRTSIGN(CDR SLIST,NCONC(POS,NEG))
    END;


SYMBOLIC PROCEDURE DEGREENEST(PF,VAR);
% Returns the maximum degree of nesting of var
% inside sqrts in the prefix form pf.
IF ATOM PF
  THEN 0
  ELSE IF CAR PF EQ 'SQRT
    THEN IF DEPENDSP(CADR PF,VAR)
      THEN IADD1 DEGREENEST(CADR PF,VAR)
      ELSE 0
    ELSE IF CAR PF EQ 'EXPT
      THEN IF DEPENDSP(CADR PF,VAR)
        THEN IF EQCAR(CADDR PF,'QUOTIENT)
          THEN IADD1 DEGREENEST(CADR PF,VAR)
          ELSE DEGREENEST(CADR PF,VAR)
        ELSE 0
      ELSE DEGREENESTL(CDR PF,VAR);

SYMBOLIC PROCEDURE DEGREENESTL(U,VAR);
%Returns max degreenest from list of pfs u.
IF NULL U
  THEN 0
  ELSE MAX(DEGREENEST(CAR U,VAR),
           DEGREENESTL(CDR U,VAR));


SYMBOLIC PROCEDURE SORTSQRTS(U,VAR);
% Sorts list of sqrts into order required by hardsqrtsign
% (and many other parts of the package).
BEGIN
  SCALAR I,V;
  V:=MKVECT(10); %should be good enough!
  WHILE U DO <<
    I:=DEGREENEST(CAR U,VAR);
    IF I IEQUAL 0
      THEN INTERR "Non-dependent sqrt found";
    IF I > 10
      THEN INTERR
         "Degree of nesting exceeds 10 (recompile with 10 increased)";
    PUTV(V,I,(CAR U).GETV(V,I));
    U:=CDR U >>;
  U:=GETV(V,10);
  FOR I :=9 STEP -1 UNTIL 1 DO
    U:=NCONC(GETV(V,I),U);
  RETURN U
  END;


SYMBOLIC PROCEDURE SQRTSIGN(SQRTS,X);
   IF NESTEDSQRTS THEN HARDSQRTSIGN(SORTSQRTS(SQRTS,X),LIST NIL)
    ELSE EASYSQRTSIGN(SQRTS,LIST NIL);

ENDMODULE;

END;
