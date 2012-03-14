MODULE JHDDIFF;

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


% Differentiation routines for algebraic expressions;
SYMBOLIC PROCEDURE !*DIFFSQ(U,V);
   %U is a standard quotient, V a kernel.
   %Value is the standard quotient derivative of U wrt V.
   %Algorithm: df(x/y,z)= (x'-(x/y)*y')/y;
   !*MULTSQ(!*ADDSQ(!*DIFFF(NUMR U,V),
                    NEGSQ !*MULTSQ(U,!*DIFFF(DENR U,V))),
          1 ./ DENR U);

SYMBOLIC PROCEDURE !*DIFFF(U,V);
   %U is a standard form, V a kernel.
   %Value is the standard quotient derivative of U wrt V;
   IF DOMAINP U THEN NIL ./ 1
    ELSE !*ADDSQ(!*ADDSQ(MULTPQ(LPOW U,!*DIFFF(LC U,V)),
                        !*MULTSQ(LC U ./ 1,!*DIFFP(LPOW U,V))),
               !*DIFFF(RED U,V));

SYMBOLIC PROCEDURE !*DIFFP(U,V);
%  Special treatment of SQRT's (JHD is not sure why,
%  but it seems to be necessary);
IF ATOM (CAR U) THEN DIFFP(U,V)
  ELSE IF NOT(CAAR U EQ 'SQRT) THEN DIFFP(U,V)
    ELSE BEGIN
           SCALAR W,DW;
           W:=SIMP ARGOF CAR U;
           DW:= !*DIFFSQ(W,V);
           IF NULL NUMR DW THEN RETURN DW;
           RETURN !*MULTSQ(!*MULTSQ(DW,INVSQ W),
                           !*MULTF(CDR U,MKSP(CAR U,1) .* 1 .+ NIL)./ 2)
           END;

ENDMODULE;

END;
