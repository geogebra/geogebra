MODULE ratint;   % Support for direct rational integration.

% Authors: Mary Ann Moore and Arthur C. Norman.

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


SYMBOLIC PROCEDURE RATIONALINTEGRATE(X,VAR);
    BEGIN     SCALAR N,D;
      N:=NUMR X; D:=DENR X;
      IF NOT(VAR MEMBER VARSINSF(D,NIL)) THEN
            RETURN !*MULTSQ(POLYNOMIALINTEGRATE(N,VAR),1 ./ D);
      REDERR "Rational integration not coded yet"
    END;

SYMBOLIC PROCEDURE POLYNOMIALINTEGRATE(X,V);
% Integrate standard form. result is standard quotient.
    IF NULL X THEN NIL ./ 1
    ELSE IF ATOM X THEN ((MKSP(V,1) .* 1) .+ NIL) ./ 1
    ELSE BEGIN    SCALAR R;
      R:=POLYNOMIALINTEGRATE(RED X,V); % deal with reductum
      IF V=MVAR X THEN BEGIN    SCALAR DEGREE,NEWLT;
         DEGREE:=1+TDEG LT X;
         NEWLT:=((MKSP(V,DEGREE) .* LC X) .+ NIL) ./ 1; % up exponent
         R:=ADDSQ(!*MULTSQ(NEWLT,1 ./ DEGREE),R)
         END
      ELSE BEGIN        SCALAR NEWTERM;
        NEWTERM:=(((LPOW X) .* 1) .+ NIL) ./ 1;
        NEWTERM:=!*MULTSQ(NEWTERM,POLYNOMIALINTEGRATE(LC X,V));
        R:=ADDSQ(R,NEWTERM)
        END;
      RETURN R
    END;

endmodule;

end;
