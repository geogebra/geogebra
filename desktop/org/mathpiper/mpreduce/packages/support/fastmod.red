module fastmod; % fast computation with modular numbers.

% Author: Herbert Melenk <melenk@sc.zib-berlin.de>.

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


%
%    ( a * b ) mod current!-modulus
%
% in one call with double length intermediate result, avoiding
% conversion to bignums. Significant speedup for e.g. factorizer.

remflag('(modular!-times general!-modular!-times),'lose);

compiletime
<<
  if memq('nbig30a,options!*) or
     memq('nbig0,options!*) then load muls else load muls32;
  slow_wquotientdouble := memq('mips,lispsystem!*) or
                          memq('ultrasparc,lispsystem!*);
>>;

fluid '(!*second!-value!* current!-modulus);

remflag('(modular!-times general!-modular!-times),'lose);

% Routines from smallmod.red and genmod.red

compiletime if slow_wquotientdouble then
     flag('(modular!-times),'lose);

symbolic procedure modular!-times(a,b);
   begin scalar q;
    q:=wtimesdouble(a,b); % upper part in second value.
    wquotientdouble(!*second!-value!*,q,current!-modulus);
                          % remainder in second value.
    return !*second!-value!*;
   end;

compiletime if slow_wquotientdouble then
     remflag('(modular!-times),'lose)
       else
     flag('(modular!-times),'lose);

symbolic procedure modular!-times(a,b);
 % for systems where single divide is substantially faster than
 % double divide.
   begin scalar q;
    q:=wtimesdouble(a,b); % upper part in second value.
    if weq(!*second!-value!*,0) and wgreaterp(q,0) then
        return wremainder(q,current!-modulus);
    wquotientdouble(!*second!-value!*,q,current!-modulus);
                          % remainder in second value.
    return !*second!-value!*;
   end;

compiletime if not memq('ultrasparc,lispsystem!*) then
               flag('(modular!-times),'lose);

symbolic procedure modular!-times(a,b);
  begin scalar q;
     q:=times2(a,b);
     return remainder(q,current!-modulus);
  end;

compiletime remflag('(modular!-times),'lose);


symbolic procedure general!-modular!-times(a,b);
  % Use fast function if all operands are inums.
   if weq(0,iplus2(tag a,iplus2(tag b,tag current!-modulus)))
      then modular!-times(a,b) else general!-modular!-times!*(a,b);

symbolic procedure general!-modular!-times!*(a,b);
  begin scalar result;
     result:=remainder(a*b,current!-modulus);
     if result<0
       then result := result+current!-modulus;  %can this happen?
     return result
  end;

flag ('(modular!-times general!-modular!-times),'lose);

% Routines from factor/VECPOLY.red.
% Smallmod arithmetic never allocates heap space such
% that vector base addresses remain valid and subsequent
% vector access can be transformed into index incremental.

remflag('(times!-in!-vector remainder!-in!-vector),'lose);

SYMBOLIC PROCEDURE TIMES!-IN!-VECTOR(A,DA,B,DB,C);
% Put the product of A and B into C and return its degree.
% C must not overlap with either A or B;
  BEGIN
    SCALAR DC,IC,W,lc,lb;
    IF ilessp(DA,0) OR ilessp(DB,0) THEN RETURN MINUS!-ONE;
    DC:=iplus2(DA,DB);
    FOR I:=0:DC DO PUTV(C,I,0);
    FOR IA:=0:DA DO <<
      W:=GETV(A,IA);
      lb := loc igetv(b,0);
      lc := loc igetv(c,ia);
      FOR IB:=0:DB DO <<
        IC:=iplus2(IA,IB);

     %  PUTV(C,IC,MODULAR!-PLUS(GETV(C,IC),
     %    MODULAR!-TIMES(W,GETV(B,IB))))

        putmem(lc,MODULAR!-PLUS(getmem lc,
          MODULAR!-TIMES(W,getmem lb)));
        lb := iplus2(lb,addressingunitsperitem);
        lc := iplus2(lc,addressingunitsperitem);

      >> >>;
    RETURN DC
  END;

SYMBOLIC PROCEDURE REMAINDER!-IN!-VECTOR(A,DA,B,DB);
% Overwrite the vector A with the remainder when A is
% divided by B, and return the degree of the result;
  BEGIN
    SCALAR DELTA,DB!-1,RECIP!-LC!-B,W,la,lb;
    IF DB=0 THEN RETURN MINUS!-ONE
    ELSE IF DB=MINUS!-ONE THEN ERRORF "ATTEMPT TO DIVIDE BY ZERO";
    RECIP!-LC!-B:=MODULAR!-MINUS MODULAR!-RECIPROCAL GETV(B,DB);
    DB!-1:=isub1 DB; % Leading coeff of B treated specially, hence this;
    WHILE NOT ilessp(DELTA:=idifference(DA,DB),0) DO <<
      W:=MODULAR!-TIMES(RECIP!-LC!-B,GETV(A,DA));
      la := loc(igetv(a,delta)); lb:= loc(igetv(b,0));
      FOR I:=0:DB!-1 DO

       %PUTV(A,I#+DELTA,MODULAR!-PLUS(GETV(A,I#+DELTA),
       %  MODULAR!-TIMES(GETV(B,I),W)));

       <<putmem(la,MODULAR!-PLUS(getmem la,
          MODULAR!-TIMES(getmem lb,w)));
         la := iplus2(la,addressingunitsperitem);
         lb := iplus2(lb,addressingunitsperitem);
       >>;

      DA:=isub1 DA;
      WHILE NOT ilessp(DA,0) AND GETV(A,DA)=0 DO DA:=isub1 DA >>;
    RETURN DA
  END;

flag('(times!-in!-vector remainder!-in!-vector),'lose);

endmodule;

end;
