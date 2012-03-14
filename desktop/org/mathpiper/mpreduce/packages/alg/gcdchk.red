MODULE GCDCHK;   % Check for a unit gcd using modular arithmetic.

% Author: Arthur C. Norman and Mary Ann Moore.

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


% Modifications by: Anthony C. Hearn.

FLUID '(!*BACKTRACE LIST!-OF!-LARGE!-PRIMES MODULAR!-VALUES);

% LIST!-OF!-LARGE!-PRIMES is a list of the largest pair of adjacent
% primes that can fit in the inum range of the implementation.
% This should be set here in an implementation dependent manner.
% For the time begin, a maximum inum value of 2^23 is assumed.

LIST!-OF!-LARGE!-PRIMES := '(8388449 8388451);

SYMBOLIC PROCEDURE MONIC!-MOD!-P A;
   IF NULL A THEN NIL
    ELSE IF DOMAINP A THEN 1
    ELSE IF LC A = 1 THEN A
    ELSE MULTIPLY!-BY!-CONSTANT!-MOD!-P(A,MODULAR!-RECIPROCAL LC A);

SMACRO PROCEDURE BEFORE!-IN!-ORDER(A,B);
%Predicate taking the value true if the polynomial
%a has a leading term which comes strictly before that
%of b in canonical order;
   NULL DOMAINP A AND (DOMAINP B OR LDEG A>LDEG B);

SYMBOLIC PROCEDURE UNI!-PLUS!-MOD!-P(A,B);
% Form the sum of the two univariate polynomials a and b
% working over the ground domain defined by the routines
% modular!-plus, modular!-times etc. The inputs to this
% routine are assumed to have coefficients already
% in the required domain;
  IF NULL A THEN B
   ELSE IF NULL B THEN A
   ELSE IF BEFORE!-IN!-ORDER(A,B)
    THEN (LT A) .+ UNI!-PLUS!-MOD!-P(RED A,B)
   ELSE IF BEFORE!-IN!-ORDER(B,A)
    THEN (LT B) .+ UNI!-PLUS!-MOD!-P(A,RED B)
   ELSE IF DOMAINP A
    THEN <<A:=MODULAR!-PLUS(A,B); IF A=0 THEN NIL ELSE A>>
   ELSE BEGIN SCALAR W;
      W:=UNI!-PLUS!-MOD!-P(RED A,RED B);
      B:=MODULAR!-PLUS(LC A,LC B);
      IF B=0 THEN RETURN W;
      RETURN (LPOW A .* B) .+ W
   END;

%symbolic procedure uni!-times!-mod!-p(a,b);
%   if (null a) or (null b) then nil
%   else if domainp a then multiply!-by!-constant!-mod!-p(b,a)
%   else if domainp b then multiply!-by!-constant!-mod!-p(a,b)
%   else uni!-plus!-mod!-p(
%         uni!-plus!-mod!-p(uni!-times!-mod!-p(red a,red b),
%                                 uni!-times!-term!-mod!-p(lt a,b)),
%                    uni!-times!-term!-mod!-p(lt b,red a));

SYMBOLIC PROCEDURE UNI!-TIMES!-TERM!-MOD!-P(TERM,B);
%Multiply the given polynomial by the given term;
    IF NULL B THEN NIL
    ELSE IF DOMAINP B THEN <<
       B:=MODULAR!-TIMES(TC TERM,B);
       IF B=0 THEN NIL
       ELSE (TPOW TERM .* B) .+ NIL >>
    ELSE BEGIN SCALAR W;
        W:=MODULAR!-TIMES(TC TERM,LC B);
        IF W=0 THEN RETURN UNI!-TIMES!-TERM!-MOD!-P(TERM,RED B);
        W:= (TVAR TERM TO (TDEG TERM+LDEG B)) .* W;
        RETURN W .+ UNI!-TIMES!-TERM!-MOD!-P(TERM,RED B)
    END;

SYMBOLIC PROCEDURE UNI!-REMAINDER!-MOD!-P(A,B);
% Remainder when a is divided by b;
    IF NULL B THEN REDERR "B=0 IN REMAINDER-MOD-P"
    ELSE IF DOMAINP B THEN NIL
    ELSE XUNI!-REMAINDER!-MOD!-P(A,B);

SYMBOLIC PROCEDURE XUNI!-REMAINDER!-MOD!-P(A,B);
% Remainder when the univariate modular polynomial a is
% divided by b, given that b is non degenerate;
   IF DOMAINP A OR LDEG A < LDEG B THEN A
   ELSE BEGIN
    SCALAR Q,W;
    Q:=MODULAR!-QUOTIENT(MODULAR!-MINUS LC A,LC B);
% compute -lc of quotient;
    W:= LDEG A - LDEG B; %ldeg of quotient;
    IF W=0 THEN A:=UNI!-PLUS!-MOD!-P(RED A,
      MULTIPLY!-BY!-CONSTANT!-MOD!-P(RED B,Q))
    ELSE
      A:=UNI!-PLUS!-MOD!-P(RED A,UNI!-TIMES!-TERM!-MOD!-P(
            (MVAR B TO W) .* Q,RED B));
% the above lines of code use red a and red b because
% by construction the leading terms of the required
% answers will cancel out;
     RETURN XUNI!-REMAINDER!-MOD!-P(A,B)
   END;

SYMBOLIC PROCEDURE MULTIPLY!-BY!-CONSTANT!-MOD!-P(A,N);
% Multiply the polynomial a by the constant n
% assumes that a is univariate, and that n is coprime with
% the current modulus so that modular!-times(xxx,n) neq 0
% for all xxx;
   IF NULL A THEN NIL
   ELSE IF N=1 THEN A
   ELSE IF DOMAINP A THEN MODULAR!-TIMES(A,N)
   ELSE (LPOW A .* MODULAR!-TIMES(LC A,N)) .+
     MULTIPLY!-BY!-CONSTANT!-MOD!-P(RED A,N);

SYMBOLIC PROCEDURE UNI!-GCD!-MOD!-P(A,B);
%Return the monic gcd of the two modular univariate
%polynomials a and b;
    IF NULL A THEN MONIC!-MOD!-P B
    ELSE IF NULL B THEN MONIC!-MOD!-P A
    ELSE IF DOMAINP A THEN 1
    ELSE IF DOMAINP B THEN 1
    ELSE IF LDEG A > LDEG B THEN
      ORDERED!-UNI!-GCD!-MOD!-P(A,B)
    ELSE ORDERED!-UNI!-GCD!-MOD!-P(B,A);

SYMBOLIC PROCEDURE ORDERED!-UNI!-GCD!-MOD!-P(A,B);
% As above, but degr a > degr b;
   IF NULL B THEN MONIC!-MOD!-P A
   ELSE ORDERED!-UNI!-GCD!-MOD!-P(B,UNI!-REMAINDER!-MOD!-P(A,B));

SYMBOLIC MACRO PROCEDURE MYERR U;
   LIST('ERRORSET,
        'LIST .
           MKQUOTE CAADR U .
              FOR EACH J IN CDADR U COLLECT LIST('MKQUOTE,J),
        T,'!*BACKTRACE);

SYMBOLIC PROCEDURE MODULAR!-MULTICHECK(U,V,VAR);
   IF ERRORP (U := MYERR MODULAR!-MULTICHECK1(U,V,VAR)) THEN NIL
    ELSE CAR U;

SYMBOLIC PROCEDURE MODULAR!-MULTICHECK1(U,V,VAR);
% TRUE if a modular check tells me that U and V are coprime;
  BEGIN
    SCALAR OLDP,P,MODULAR!-VALUES,UMODP,VMODP;
    P:=LIST!-OF!-LARGE!-PRIMES;
    OLDP:=SETMOD NIL;
TRY!-NEXT!-PRIME:
    MODULAR!-VALUES:=NIL;
    IF NULL P THEN GOTO UNCERTAIN;
    SETMOD CAR P;
    P:=CDR P;
    IF NULL MODULAR!-IMAGE(LC U,VAR) OR NULL MODULAR!-IMAGE(LC V,VAR)
      THEN GO TO TRY!-NEXT!-PRIME;
    UMODP:=MODULAR!-IMAGE(U,VAR);
    VMODP:=MODULAR!-IMAGE(V,VAR);
    P := DOMAINP UNI!-GCD!-MOD!-P(UMODP,VMODP);
UNCERTAIN:
    SETMOD OLDP;
    RETURN P
  END;

SYMBOLIC PROCEDURE MODULAR!-IMAGE(P,VAR);
    IF DOMAINP P
      THEN IF NULL P THEN NIL
            ELSE IF NOT ATOM P THEN ERROR1()
            ELSE <<P := MODULAR!-NUMBER P; IF P=0 THEN NIL ELSE P>>
    ELSE BEGIN
      SCALAR V,X,W;
      V:=MVAR P;
      IF V=VAR THEN <<
          X:=MODULAR!-IMAGE(LC P,VAR);
          IF NULL X THEN RETURN MODULAR!-IMAGE(RED P,VAR)
          ELSE RETURN (LPOW P .* X) .+ MODULAR!-IMAGE(RED P,VAR) >>;
      X:=ATSOC(V,MODULAR!-VALUES);
      IF NULL X THEN <<
          X:=MODULAR!-NUMBER RANDOM CAR LIST!-OF!-LARGE!-PRIMES;
          MODULAR!-VALUES:=(V . X) . MODULAR!-VALUES >>
      ELSE X:=CDR X;
      X:=MODULAR!-EXPT(X,LDEG P);
      W:=MODULAR!-IMAGE(RED P,VAR);
      V:=MODULAR!-IMAGE(LC P,VAR);
      IF NULL V THEN X:=NIL
      ELSE X:=MODULAR!-TIMES(V,X);
      IF W THEN X:=MODULAR!-PLUS(X,W);
      RETURN IF X=0 THEN NIL ELSE X
    END;

ENDMODULE;

END;
