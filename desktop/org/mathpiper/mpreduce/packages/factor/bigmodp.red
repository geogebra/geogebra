MODULE BIGMODP; % Modular polynomial arithmetic where the modulus may
                % be a bignum.

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


FLUID '(CURRENT!-MODULUS MODULUS!/2);

symbolic SMACRO PROCEDURE COMES!-BEFORE(P1,P2);
% Similar to the REDUCE function ORDPP, but does not cater for
% non-commutative terms and assumes that exponents are small integers.
    (CAR P1=CAR P2 AND IGREATERP(CDR P1,CDR P2)) OR
       (NOT(CAR P1=CAR P2) AND ORDOP(CAR P1,CAR P2));

SYMBOLIC PROCEDURE GENERAL!-PLUS!-MOD!-P(A,B);
% form the sum of the two polynomials a and b
% working over the ground domain defined by the routines
% general!-modular!-plus, general!-modular!-times etc. the inputs to
% this routine are assumed to have coefficients already
% in the required domain;
   IF NULL A THEN B
   ELSE IF NULL B THEN A
   ELSE IF domainp A THEN
      IF domainp B THEN !*n2f GENERAL!-MODULAR!-PLUS(A,B)
      ELSE (LT B) .+ GENERAL!-PLUS!-MOD!-P(A,RED B)
   ELSE IF domainp B THEN (LT A) .+ GENERAL!-PLUS!-MOD!-P(RED A,B)
   ELSE IF LPOW A = LPOW B THEN
      ADJOIN!-TERM(LPOW A,
         GENERAL!-PLUS!-MOD!-P(LC A,LC B),
         GENERAL!-PLUS!-MOD!-P(RED A,RED B))
   ELSE IF COMES!-BEFORE(LPOW A,LPOW B) THEN
         (LT A) .+ GENERAL!-PLUS!-MOD!-P(RED A,B)
   ELSE (LT B) .+ GENERAL!-PLUS!-MOD!-P(A,RED B);

SYMBOLIC PROCEDURE GENERAL!-TIMES!-MOD!-P(A,B);
   IF (NULL A) OR (NULL B) THEN NIL
   ELSE IF domainp A THEN GEN!-MULT!-BY!-CONST!-MOD!-P(B,A)
   ELSE IF domainp B THEN GEN!-MULT!-BY!-CONST!-MOD!-P(A,B)
   ELSE IF MVAR A=MVAR B THEN GENERAL!-PLUS!-MOD!-P(
     GENERAL!-PLUS!-MOD!-P(GENERAL!-TIMES!-TERM!-MOD!-P(LT A,B),
                  GENERAL!-TIMES!-TERM!-MOD!-P(LT B,RED A)),
     GENERAL!-TIMES!-MOD!-P(RED A,RED B))
   ELSE IF ORDOP(MVAR A,MVAR B) THEN
     ADJOIN!-TERM(LPOW A,GENERAL!-TIMES!-MOD!-P(LC A,B),
       GENERAL!-TIMES!-MOD!-P(RED A,B))
   ELSE ADJOIN!-TERM(LPOW B,
        GENERAL!-TIMES!-MOD!-P(A,LC B),GENERAL!-TIMES!-MOD!-P(A,RED B));

SYMBOLIC PROCEDURE GENERAL!-TIMES!-TERM!-MOD!-P(TERM,B);
%multiply the given polynomial by the given term;
    IF NULL B THEN NIL
    ELSE IF domainp B THEN
        ADJOIN!-TERM(TPOW TERM,
            GEN!-MULT!-BY!-CONST!-MOD!-P(TC TERM,B),NIL)
    ELSE IF TVAR TERM=MVAR B THEN
         ADJOIN!-TERM(MKSP(TVAR TERM,IPLUS2(TDEG TERM,LDEG B)),
                      GENERAL!-TIMES!-MOD!-P(TC TERM,LC B),
                      GENERAL!-TIMES!-TERM!-MOD!-P(TERM,RED B))
    ELSE IF ORDOP(TVAR TERM,MVAR B) THEN
      ADJOIN!-TERM(TPOW TERM,GENERAL!-TIMES!-MOD!-P(TC TERM,B),NIL)
    ELSE ADJOIN!-TERM(LPOW B,
      GENERAL!-TIMES!-TERM!-MOD!-P(TERM,LC B),
      GENERAL!-TIMES!-TERM!-MOD!-P(TERM,RED B));

SYMBOLIC PROCEDURE GEN!-MULT!-BY!-CONST!-MOD!-P(A,N);
% multiply the polynomial a by the constant n;
   IF NULL A THEN NIL
   ELSE IF N=1 THEN A
   ELSE IF domainp A THEN !*n2f GENERAL!-MODULAR!-TIMES(A,N)
   ELSE ADJOIN!-TERM(LPOW A,GEN!-MULT!-BY!-CONST!-MOD!-P(LC A,N),
     GEN!-MULT!-BY!-CONST!-MOD!-P(RED A,N));

SYMBOLIC PROCEDURE GENERAL!-DIFFERENCE!-MOD!-P(A,B);
   GENERAL!-PLUS!-MOD!-P(A,GENERAL!-MINUS!-MOD!-P B);

SYMBOLIC PROCEDURE GENERAL!-MINUS!-MOD!-P A;
   IF NULL A THEN NIL
   ELSE IF domainp A THEN GENERAL!-MODULAR!-MINUS A
   ELSE (LPOW A .* GENERAL!-MINUS!-MOD!-P LC A) .+
        GENERAL!-MINUS!-MOD!-P RED A;

SYMBOLIC PROCEDURE GENERAL!-REDUCE!-MOD!-P A;
%converts a multivariate poly from normal into modular polynomial;
    IF NULL A THEN NIL
    ELSE IF domainp A THEN !*n2f GENERAL!-MODULAR!-NUMBER A
    ELSE ADJOIN!-TERM(LPOW A,
                      GENERAL!-REDUCE!-MOD!-P LC A,
                      GENERAL!-REDUCE!-MOD!-P RED A);

SYMBOLIC PROCEDURE GENERAL!-MAKE!-MODULAR!-SYMMETRIC A;
% input is a multivariate MODULAR poly A with nos in the range 0->(p-1).
% This folds it onto the symmetric range (-p/2)->(p/2);
    IF NULL A THEN NIL
    ELSE IF DOMAINP A THEN
      IF A>MODULUS!/2 THEN !*n2f(A - CURRENT!-MODULUS)
      ELSE A
    ELSE ADJOIN!-TERM(LPOW A,
                      GENERAL!-MAKE!-MODULAR!-SYMMETRIC LC A,
                      GENERAL!-MAKE!-MODULAR!-SYMMETRIC RED A);

ENDMODULE;

END;
