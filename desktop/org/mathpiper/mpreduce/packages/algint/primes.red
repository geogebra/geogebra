MODULE PRIMES;

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


% EXPORTS NEXTPRIME,JHD!-PRIMEP;
% JHD!-PRIMEP replaced by the primep from "module zfactor" 8.Sept.1988
EXPORTS NEXTPRIME;

SYMBOLIC PROCEDURE NEXTPRIME P;
% Returns the next prime number bigger than p.
    IF P=0 THEN 1
    ELSE IF P=1 THEN 2
    ELSE BEGIN
        IF EVENP P THEN P:=P+1 ELSE P:=P+2;
 TEST:  IF PRIMEP P THEN RETURN P;
        P:=P+2;
        GO TO TEST END;

% SYMBOLIC PROCEDURE JHD!-PRIMEP P;
%     IF P < 4 THEN T
%     ELSE IF EVENP P THEN NIL
%     ELSE BEGIN
%       SCALAR N;
%       N:=3; %trial factor.
%  TOP: IF N*N>P THEN RETURN T
%       ELSE IF REMAINDER(P,N)=0 THEN RETURN NIL;
%       N:=N+2;
%       GO TO TOP END;

ENDMODULE;

END;
