module bibasis_monom;

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


smacro procedure bibasis_insert(y, x); rplaca(rplacd(y, car(y) . cdr(y)), x);
smacro procedure bibasis_remove(y); rplacd(rplaca(y, cadr(y)), cddr(y));

expr procedure PushBack(list, value);
begin scalar listIterator;
    listIterator := list;
    while car(listIterator) do
    <<
        listIterator := cdr(listIterator);
    >>;
    bibasis_insert(listIterator, value);
end;


%----------------------------------------------------------------------------
% monom x_1^k_1*...x_n^k_n = mkvect(1) 
% 0 - k_1+...+k_n
% 1 - k_1 . ... . k_n . (nil . nil)


expr procedure InitMonomials();
begin
    FluidBibasisNumberOfVariables := length(FluidBibasisVariables);
    FluidBibasisSingleVariableMonomialsS := mkvect(FluidBibasisNumberOfVariables);
    for i:=1:FluidBibasisNumberOfVariables do
    <<
        putv(FluidBibasisSingleVariableMonomialsS, i, CreateSingleVariableMonom(i));
    >>;
    FluidBibasisTripleID := 0; %move somewhere else
end;


smacro procedure GetVariable(variable); getv(FluidBibasisSingleVariableMonomialsS, variable);
smacro procedure MonomGetDegree(monom); getv(monom, 0);
smacro procedure MonomSetDegree(monom, degree); putv(monom, 0, degree);
smacro procedure MonomGetExponent(monom); getv(monom, 1);
smacro procedure MonomSetExponent(monom, exponent); putv(monom, 1, exponent);


expr procedure MonomGetVariableDegree(monom, variable); 
begin scalar exponent;
    exponent := MonomGetExponent(monom);
    while and(car(exponent),
              car(exponent) > variable) do
    <<
        exponent := cdr(exponent);
    >>;
    if car(exponent) and car(exponent) = variable then
    <<
        return 1;
    >>
    else
    <<
       return 0; 
    >>;
end;


expr procedure CreateMonomUnit();
begin scalar tmpMonom;
    tmpMonom := mkvect(1);
    MonomSetDegree(tmpMonom, 0);
    MonomSetExponent(tmpMonom, (nil . nil));
    return tmpMonom;
end;


expr procedure CreateSingleVariableMonom(variable);
begin scalar tmpMonom;
    tmpMonom := mkvect(1);
    MonomSetDegree(tmpMonom, 1);
    MonomSetExponent(tmpMonom, (variable . nil . nil));
    return tmpMonom;;
end;


expr procedure MonomClone(monom);
if null(monom) then
    nil
else begin scalar tmpMonom, exponent, tmpExponent;
    tmpMonom := mkvect(1);
    MonomSetDegree(tmpMonom, MonomGetDegree(monom));
    exponent := MonomGetExponent(monom);
    while exponent do
    <<
        tmpExponent := car(exponent) . tmpExponent;
        exponent := cdr(exponent);
    >>;
    MonomSetExponent(tmpMonom, reverse(tmpExponent));
    return tmpMonom;
end;


expr procedure MonomMultiplyByVariable(monom, variable);
begin scalar exponent;
    exponent := MonomGetExponent(monom);
    while and(car(exponent),
              car(exponent) > variable) do
    <<
        exponent := cdr(exponent);
    >>;
    if not(car(exponent) and car(exponent) = variable) then
    <<
        bibasis_insert(exponent, variable);
        MonomSetDegree(monom, MonomGetDegree(monom) + 1);
    >>;
end;


expr procedure MonomCompareLex(monom1, monom2);
begin scalar exponent1, exponent2; integer i;
    exponent1 := cdr(reverse(nil . MonomGetExponent(monom1)));
    exponent2 := cdr(reverse(nil . MonomGetExponent(monom2)));
    i := 0;   
    while car(exponent1) and car(exponent2) do
    <<
        if car(exponent1) < car(exponent2) then
        <<
            i := 1;
            exponent1 := (nil . nil);
        >>
        else if car(exponent1) > car(exponent2) then
        <<
            i := -1;
            exponent1 := (nil . nil);
        >>
        else
        <<
            exponent1 := cdr(exponent1);
            exponent2 := cdr(exponent2)
        >>;
    >>;
    
    if i = 0 and car(exponent1) then
    <<
        i := 1;
    >>
    else if i = 0 and car(exponent2) then
    <<
        i := -1;
    >>;
    
    return i;
end;


expr procedure MonomCompareDegLex(monom1, monom2);
begin scalar exponent1, exponent2; integer i;
    i := 0;
    if igreaterp(MonomGetDegree(monom1), MonomGetDegree(monom2)) then
    <<
        i := 1
    >>
    else if ilessp(MonomGetDegree(monom1), MonomGetDegree(monom2)) then
    <<
        i := -1
    >>
    else 
    <<
        exponent1 := cdr(reverse(nil . MonomGetExponent(monom1)));
        exponent2 := cdr(reverse(nil . MonomGetExponent(monom2)));
        while car(exponent1) and car(exponent2) do
        <<
            if car(exponent1) < car(exponent2) then
            <<
                i := 1;
                exponent1 := (nil . nil);
            >>
            else if car(exponent1) > car(exponent2) then
            <<
                i := -1;
                exponent1 := (nil . nil);
            >>
            else
            <<
                exponent1 := cdr(exponent1);
                exponent2 := cdr(exponent2)
            >>;
        >>;
    >>;
    return i;
end;


expr procedure MonomCompareDegRevLex(monom1, monom2);
begin scalar exponent1, exponent2; integer i;
    if MonomGetDegree(monom1) > MonomGetDegree(monom2) then
    <<
        i := 1;
    >>
    else if MonomGetDegree(monom1) < MonomGetDegree(monom2) then
    <<
        i := -1;
    >>
    else 
    <<
        exponent1 := MonomGetExponent(monom1);
        exponent2 := MonomGetExponent(monom2);
        while car(exponent1) do
        <<
            if car(exponent1) < car(exponent2) then
            <<
                i := 1;
                exponent1 := (nil . nil);
            >>
            else if car(exponent1) > car(exponent2) then
            <<
                i := -1;
                exponent1 := (nil . nil);
            >>
            else
            <<
                exponent1 := cdr(exponent1);
                exponent2 := cdr(exponent2);
            >>;
        >>;
        if null(car(exponent2)) then
        <<
            i := 0;
        >>;
    >>;
    return i;
end;


expr procedure MonomCompare(monom1, monom2);
begin
    if (eq(FluidBibasisMonomialOrder, 'Lex)) then
    <<
        return MonomCompareLex(monom1, monom2);
    >>
    else if (eq(FluidBibasisMonomialOrder, 'DegLex)) then
    <<
        return MonomCompareDegLex(monom1, monom2);
    >>
    else
    <<
        return MonomCompareDegRevLex(monom1, monom2);
    >>    
end;


expr procedure MonomIsDivisibleBy(monom1, monom2);
begin scalar exponent1, exponent2;
    exponent1 := MonomGetExponent(monom1);
    exponent2 := MonomGetExponent(monom2);
    while and(car(exponent1),
              car(exponent2)) do
    <<
        if car(exponent1) = car(exponent2) then
        <<
            exponent1 := cdr(exponent1);
            exponent2 := cdr(exponent2);
        >>
        else if car(exponent1) > car(exponent2) then
        <<
            exponent1 := cdr(exponent1);
        >>
        else
        <<
            exponent1 := (nil . nil);
        >>;
    >>;
    return null(car(exponent2));
end;


expr procedure MonomIsPommaretDivisibleBy(monom1, monom2);
begin scalar exponent1, exponent2, break;
    exponent1 := MonomGetExponent(monom1);
    exponent2 := MonomGetExponent(monom2);
    while and(car(exponent1),
              car(exponent1) > car(exponent2)) do
    <<
        exponent1 := cdr(exponent1);
    >>;
    
    while and(not(break),
              car(exponent1),
              car(exponent2)) do
    <<
        if neq(car(exponent1), car(exponent2)) then
        <<
            break := t;
        >>
        else
        <<
            exponent1 := cdr(exponent1);
            exponent2 := cdr(exponent2);
        >>;
    >>;
    return null(car(exponent1)) and null(car(exponent2));
end;


expr procedure MonomDivide(monom1, monom2);
begin scalar tmpMonom, exponent1, exponent2, tmpExponent;
    tmpMonom := mkvect(1);
    MonomSetDegree(tmpMonom, MonomGetDegree(monom1) - MonomGetDegree(monom2));
    exponent1 := MonomGetExponent(monom1);
    exponent2 := MonomGetExponent(monom2);
    while car(exponent1) do
    <<
        if car(exponent1) = car(exponent2) then
        <<
            exponent1 := cdr(exponent1);
            exponent2 := cdr(exponent2);
        >>
        else
        <<
            tmpExponent := car(exponent1) . tmpExponent;
            exponent1 := cdr(exponent1);
        >>;
    >>;
    tmpExponent := nil . tmpExponent;
    MonomSetExponent(tmpMonom, reverse(tmpExponent));
    return tmpMonom;
end;


expr procedure MonomGetFirstMultiVar(monom);
begin
    return if car(getv(monom, 1)) then car(getv(monom, 1)) else 1;
end;


expr procedure MonomWrite(monom);
begin scalar result, variables, exponent; integer previousVariable;
    previousVariable := FluidBibasisNumberOfVariables;
    variables := FluidBibasisReversedVariables;
    exponent := MonomGetExponent(monom);
    while car(exponent) do
    <<
        for i:=1:(previousVariable - car(exponent)) do
        <<
            variables := cdr(variables);
        >>;
        previousVariable := car(exponent);

        if result then
        <<
            result := (car(variables) . 1) . result . nil
        >>
        else
        <<
            result := (car(variables) . 1) . 1;
        >>;
        exponent := cdr(exponent);
    >>;
    return result;
end;


expr procedure MonomPrint(monom);
begin scalar variables, exponent; integer previousVariable;
    if MonomGetDegree(monom) = 0 then
    <<
        prin2 "1";
    >>
    else
    <<
        previousVariable := 1;
        variables := FluidBibasisVariables;
        exponent := cdr(reverse(nil . MonomGetExponent(monom)));
        for i:=1:(car(exponent) - previousVariable) do
        <<
            variables := cdr(variables);
        >>;
        previousVariable := car(exponent);
        prin2 car(variables);
        exponent := cdr(exponent);

        while car(exponent) do
        <<
            for i:=1:(car(exponent) - previousVariable) do
            <<
                variables := cdr(variables);
            >>;
            previousVariable := car(exponent);
            prin2 "*"; prin2 car(variables);
            exponent := cdr(exponent);
        >>;
    >>;
end;



endmodule;
end;
