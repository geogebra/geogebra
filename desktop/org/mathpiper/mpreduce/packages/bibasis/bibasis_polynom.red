module bibasis_polynom;

% Authors:  Yuri A. Blinkov
%           Saratov State University
%           Saratov, Russia
%           e-mail: BlinkovUA@info.sgu.ru
%
%           Mikhail V. Zinin
%           Joint Instutite for Nuclear Research
%           Dubna, Russia
%           e-mail: mzinin@gmail.com
%
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


%----------------------------------------------------------------------------
% poly k_1 + ... k_n = k_1 . ... . k_n . (nil . nil)


smacro procedure PolynomGetLm(p); car(p);


expr procedure PolynomClone(polynom);
begin scalar tmpPolynom, polynomIterator;
    polynomIterator := polynom;
    while polynomIterator do
    <<
        tmpPolynom := MonomClone(car(polynomIterator)) . tmpPolynom;
        polynomIterator := cdr(polynomIterator);
    >>;
    return reverse(tmpPolynom);
end;


% writes result in polynom
expr procedure PolynomMultiplyByVariable(polynom, variable);
begin scalar tmpPolynomNoVariable, polynomIterator;
    polynomIterator := polynom;
    while car(polynomIterator) do
    <<
        if eqn(MonomGetVariableDegree(car(polynomIterator), variable), 0) then
        <<
            tmpPolynomNoVariable := car(polynomIterator) . tmpPolynomNoVariable;
            bibasis_remove(polynomIterator);
        >>
        else
        <<
            polynomIterator := cdr(polynomIterator);
        >>;
    >>;
    tmpPolynomNoVariable := nil . tmpPolynomNoVariable;
    tmpPolynomNoVariable := reverse(tmpPolynomNoVariable);
    
    polynomIterator := tmpPolynomNoVariable;
    while car(polynomIterator) do
    <<
        MonomMultiplyByVariable(car(polynomIterator), variable);
        polynomIterator := cdr(polynomIterator);
    >>;
    
    PolynomAdd(polynom, tmpPolynomNoVariable);
end;


% returns new polynom
expr procedure PolynomMultiplyByMonom(polynom, monom);
if null(polynom) then
    nil
else begin scalar tmpPolynom, exponent;
    tmpPolynom := PolynomClone(polynom);
    exponent := MonomGetExponent(monom);
    while car(exponent) do
    <<
        PolynomMultiplyByVariable(tmpPolynom, car(exponent));
        exponent := cdr(exponent);
    >>;
    return tmpPolynom;
end;


% writes result in polynom1
expr procedure PolynomAdd(polynom1, polynom2);
begin scalar tmpPolynom1, tmpPolynom2; integer monomCompare;
    tmpPolynom1 := polynom1;
    tmpPolynom2 := polynom2;
    while and(PolynomGetLm(tmpPolynom1), PolynomGetLm(tmpPolynom2)) do 
    <<
        monomCompare := MonomCompare(PolynomGetLm(tmpPolynom1), PolynomGetLm(tmpPolynom2));
        if monomCompare = 1 then
        <<
            tmpPolynom1 := cdr(tmpPolynom1);
        >>
        else if monomCompare = -1 then 
        <<
            bibasis_insert(tmpPolynom1, car(tmpPolynom2));
            tmpPolynom1 := cdr(tmpPolynom1);
            tmpPolynom2 := cdr(tmpPolynom2);
        >>
        else
        <<
            bibasis_remove(tmpPolynom1);
            tmpPolynom2 := cdr(tmpPolynom2);
        >>;
    >>;
    if car(tmpPolynom2) then
    <<
        bibasis_remove(rplacd(tmpPolynom1, tmpPolynom2));
    >>;
    return polynom1;
end;


% writes result in polynom1
expr procedure PolynomReduceBy(polynom1, polynom2);
begin scalar break, tmpMonom, tmpPolynom;
    while (not break) do
    <<
        tmpPolynom := polynom1;
        while and(PolynomGetLm(tmpPolynom),
                  not MonomIsDivisibleBy(PolynomGetLm(tmpPolynom), PolynomGetLm(polynom2))) do
        <<
            tmpPolynom := cdr(tmpPolynom);
        >>;
        
        if not PolynomGetLm(tmpPolynom) then
        <<
            break := t;
        >>
        else
        <<
            tmpMonom := MonomDivide(PolynomGetLm(tmpPolynom), PolynomGetLm(polynom2));
            PolynomAdd(polynom1, PolynomMultiplyByMonom(polynom2, tmpMonom));
        >>;
    >>;
    FluidBibasisReductionsMade := iadd1(FluidBibasisReductionsMade);
end;


% writes result in polynom1
expr procedure PolynomHeadReduceBy(polynom1, polynom2);
begin scalar break, tmpMonom;
    while and(PolynomGetLm(polynom1), not break) do
    <<
        if MonomIsDivisibleBy(PolynomGetLm(polynom1), PolynomGetLm(polynom2)) then
        <<
            tmpMonom := MonomDivide(PolynomGetLm(polynom1), PolynomGetLm(polynom2));
            PolynomAdd(polynom1, PolynomMultiplyByMonom(polynom2, tmpMonom));
        >>
        else
        <<
            break := t;
        >>;
    >>;
    FluidBibasisReductionsMade := iadd1(FluidBibasisReductionsMade);
end;


expr procedure PolynomCompare(polynom1, polynom2);
begin scalar tmpPolyIterator1, tmpPolyIterator2; integer monomCompare;
    tmpPolyIterator1 := polynom1;
    tmpPolyIterator2 := polynom2;
    
    while car(tmpPolyIterator1) and car(tmpPolyIterator2) do
    <<
        monomCompare := MonomCompare(car(tmpPolyIterator1), car(tmpPolyIterator2));
        if monomCompare = 1 then
        <<
            tmpPolyIterator2 := (nil . nil);
        >>
        else if monomCompare = -1 then
        <<
            tmpPolyIterator1 := (nil . nil);
        >>
        else
        <<
            tmpPolyIterator1 := cdr(tmpPolyIterator1);
            tmpPolyIterator2 := cdr(tmpPolyIterator2);
        >>;
    >>;
    
    if car(tmpPolyIterator1) then
    <<
        return 1;
    >>
    else if car(tmpPolyIterator2) then
    <<
        return -1;
    >>
    else
    <<
        return 0;
    >>;
end;


expr procedure PolynomRead(polynom);
if null(polynom) then 
<<
    (nil . nil)
>>
else if domainp(polynom) then 
<<
    if eqn(remainder(polynom, 2), 1) then
    <<
        (CreateMonomUnit() . (nil . nil))
    >>
    else
    <<
        (nil . nil)
    >>
>>
else if member(mvar(polynom), FluidBibasisVariables) then
<<
    PolynomAdd(PolynomMultiplyByMonom(PolynomRead(lc(polynom)), 
                                      cdr(assoc(mvar(polynom), FluidBibasisSingleVariableMonomialsA))),
               PolynomRead(red(polynom)))
>>
else
<<
    PolynomAdd(PolynomMultiplyByMonom(PolynomRead(lc(polynom)), CreateMonomUnit()), PolynomRead(red(polynom)));
>>;


expr procedure PolynomWrite(polynom);
if null(PolynomGetLm(polynom)) then
<<
    nil
>>
else if MonomGetDegree(PolynomGetLm(polynom)) = 0 then
<<
    1
>>
else
<<
    (MonomWrite(PolynomGetLm(polynom)) . PolynomWrite(cdr(polynom)))
>>;


expr procedure PolynomPrint(polynom);
begin scalar currentMonom;
    currentMonom := polynom;
    if null(car(currentMonom)) then
    <<
        prin2 "0";
    >>
    else
    <<
        MonomPrint(car(currentMonom));
        currentMonom := cdr(currentMonom);
        while car(currentMonom) do
        <<
            prin2 " + ";
            MonomPrint(car(currentMonom));
            currentMonom := cdr(currentMonom);
        >>;
    >>;
end;


endmodule;
end;
