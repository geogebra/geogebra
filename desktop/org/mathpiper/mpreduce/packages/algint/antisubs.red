MODULE ANTISUBS;

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


EXPORTS ANTISUBS;

IMPORTS INTERR,DEPENDSP,setdiff;


SYMBOLIC PROCEDURE ANTISUBS(PLACE,X);
% Produces the inverse substitution to a substitution list.
BEGIN
  SCALAR ANSWER,W;
  WHILE PLACE AND
        (X=CAAR PLACE) DO<<
    W:=CDAR PLACE;
    % w is the substitution rule.
    IF ATOM W
      THEN IF W NEQ X
        THEN INTERR "False atomic substitution"
        ELSE NIL
      ELSE ANSWER:=(X.ANTI2(W,X)).ANSWER;
    PLACE:=CDR PLACE>>;
  IF NULL ANSWER
    THEN ANSWER:=(X.X).ANSWER;
  RETURN ANSWER
  END;


SYMBOLIC PROCEDURE ANTI2(EEXPR,X);
%Produces the function inverse to the eexpr provided.
IF ATOM EEXPR
  THEN IF EEXPR EQ X
    THEN X
    ELSE INTERR "False atom"
  ELSE IF CAR EEXPR EQ 'PLUS
    THEN DEPLUS(CDR EEXPR,X)
    ELSE IF CAR EEXPR EQ 'MINUS
      THEN SUBST(LIST('MINUS,X),X,ANTI2(CADR EEXPR,X))
      ELSE IF CAR EEXPR EQ 'QUOTIENT
        THEN IF DEPENDSP(CADR EEXPR,X)
          THEN IF DEPENDSP(CADDR EEXPR,X)
            THEN INTERR "Complicated division"
            ELSE SUBST(LIST('TIMES,CADDR EEXPR,X),X,ANTI2(CADR EEXPR,X))
          ELSE IF DEPENDSP(CADDR EEXPR,X)
            THEN SUBST(LIST('QUOTIENT,CADR EEXPR,X),X,
                       ANTI2(CADDR EEXPR,X))
            ELSE INTERR "No division"
        ELSE IF CAR EEXPR EQ 'EXPT
          THEN IF CADDR EEXPR IEQUAL 2
            THEN SUBST(LIST('SQRT,X),X,ANTI2(CADR EEXPR,X))
            ELSE INTERR "Unknown root"
          ELSE IF CAR EEXPR EQ 'TIMES
            THEN DETIMES(CDR EEXPR,X)
            ELSE IF CAR EEXPR EQ 'DIFFERENCE
              THEN DEPLUS(LIST(CADR EEXPR,LIST('MINUS,CADDR EEXPR)),X)
              ELSE INTERR "Unrecognised form in antisubs";



SYMBOLIC PROCEDURE DETIMES(P!-LIST,VAR);
% Copes with lists 'times.
BEGIN
  SCALAR U,V;
  U:=DEPLIST(P!-LIST,VAR);
  V:=setdiff(P!-LIST,u);
  IF NULL V
    THEN V:=VAR
    ELSE IF NULL CDR V
      THEN V:=LIST('QUOTIENT,VAR,CAR V)
      ELSE V:=LIST('QUOTIENT,VAR,'TIMES.V);
  IF (NULL U) OR
     (CDR U)
    THEN INTERR "Weird multiplication";
  RETURN SUBST(V,VAR,ANTI2(CAR U,VAR))
  END;


SYMBOLIC PROCEDURE DEPLIST(P!-LIST,VAR);
% Returns a list of those elements of p!-list which depend on var.
IF NULL P!-LIST
  THEN NIL
  ELSE IF DEPENDSP(CAR P!-LIST,VAR)
    THEN (CAR P!-LIST).DEPLIST(CDR P!-LIST,VAR)
    ELSE DEPLIST(CDR P!-LIST,VAR);


SYMBOLIC PROCEDURE DEPLUS(P!-LIST,VAR);
% Copes with lists 'plus.
BEGIN
  SCALAR U,V;
  U:=DEPLIST(P!-LIST,VAR);
  V:=setdiff(P!-LIST,u);
  IF NULL V
    THEN V=VAR
    ELSE IF NULL CDR V
      THEN V:=LIST('PLUS,VAR,LIST('MINUS,CAR V))
      ELSE V:=LIST('PLUS,VAR,LIST('MINUS,'PLUS.V));
  IF (NULL U) OR
     (CDR U)
    THEN INTERR "Weird addition";
  RETURN SUBST(V,VAR,ANTI2(CAR U,VAR))
  END;

ENDMODULE;

END;

