module bibasis_involutive;

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
% POSSIBILITY OF SUCH DAMAG


expr procedure Init();
begin integer i;
    FluidBibasisReversedVariables := reverse(FluidBibasisVariables);
    
    InitMonomials();
    
    i := 0;
    FluidBibasisSingleVariableMonomialsA := for each v in FluidBibasisVariables collect(v . getv(FluidBibasisSingleVariableMonomialsS, i := i+1));
    
    SetTReset();
    SetQReset();
    
    FluidBibasisReductionsMade := 0;
    FluidBibasisNormalForms := 0;
    FluidBibasisNonZeroNormalForms := 0;
end;


expr procedure SortedPolynomListInsert(polynomList, polynom);
begin scalar polynomListIterator;
    polynomListIterator := polynomList;
    while and(car(polynomListIterator),
              neq(PolynomCompare(car(polynomListIterator), polynom), -1)) do
    <<
        polynomListIterator := cdr(polynomListIterator);
    >>;
    bibasis_insert(polynomListIterator, polynom);
end;


expr procedure PolynomListFindDivisor(polynomList, polynom, toGroebner);
if or(null(PolynomGetLm(polynom)),
      null(car(polynomList))) then
    nil
else begin scalar tmpMonom, currentPolynom, break;
    tmpMonom := PolynomGetLm(polynom);
    currentPolynom := polynomList;
    
    while and(car(currentPolynom), not(break)) do
    <<
        if or(and(toGroebner, MonomIsDivisibleBy(tmpMonom, PolynomGetLm(car(currentPolynom)))),
              and(not(toGroebner), MonomIsPommaretDivisibleBy(tmpMonom, PolynomGetLm(car(currentPolynom))))
              ) then
        <<
            break := t;
        >>
        else
        <<
            currentPolynom := cdr(currentPolynom);
        >>;
    >>;
    return car(currentPolynom);
end;


%returns new polynom, the argument polynom itself will be destroyed
expr procedure PolynomListReduce(polynomList, polynom, toGroebner);
begin scalar result, divisor;
    result := (nil . nil);
    if null(PolynomGetLm(polynom)) then
    <<
        return result;
    >>;
    
    while PolynomGetLm(polynom) do
    <<
        divisor := PolynomListFindDivisor(polynomList, polynom, toGroebner);
        while divisor do
        <<
            PolynomReduceBy(polynom, divisor);
            divisor := PolynomListFindDivisor(polynomList, polynom, toGroebner);
        >>;
        if PolynomGetLm(polynom) then
        <<
            PolynomAdd(result, (PolynomGetLm(polynom) . (nil . nil)));
            bibasis_remove(polynom);
        >>;
    >>;
    
    return result;
end;


expr procedure PolynomListAutoReduce(polynomList, toGroebner);
begin scalar tmpPolynomList, tmpPolynom, tmpMonom, tmpPolynomIterator;
    tmpPolynomList := (nil . nil);
    
    while car(polynomList) do
    <<
        tmpPolynom := PolynomListReduce(tmpPolynomList, car(polynomList), toGroebner);
        bibasis_remove(polynomList);
        
        if PolynomGetLm(tmpPolynom) then
        <<
            tmpMonom := PolynomGetLm(tmpPolynom);
            tmpPolynomIterator := tmpPolynomList;
            while car(tmpPolynomIterator) do
            <<
                if MonomIsDivisibleBy(PolynomGetLm(car(tmpPolynomIterator)), tmpMonom) then
                <<
                    PushBack(polynomList, car(tmpPolynomIterator));
                    bibasis_remove(tmpPolynomIterator);
                >>
                else
                <<
                    tmpPolynomIterator := cdr(tmpPolynomIterator);
                >>;
            >>;
            PushBack(tmpPolynomList, tmpPolynom);
        >>;
    >>;

    tmpPolynomIterator := tmpPolynomList;
    while car(tmpPolynomIterator) do
    <<
        tmpPolynom := car(tmpPolynomIterator);
        bibasis_remove(tmpPolynomIterator);

        tmpPolynom := PolynomListReduce(tmpPolynomList, tmpPolynom, toGroebner);

        if tmpPolynom and PolynomGetLm(tmpPolynom) then
        <<
            bibasis_insert(tmpPolynomIterator, tmpPolynom);
            tmpPolynomIterator := cdr(tmpPolynomIterator);
        >>;
    >>;

    return tmpPolynomList;
end;


%returns new polynom, the argument polynom itself will be destroyed
expr procedure NormalForm(polynom);
begin scalar involutiveDivisor, normalForm;
    normalForm := (nil . nil);
    if null(PolynomGetLm(polynom)) then
    <<
        return normalForm;
    >>;
    FluidBibasisNormalForms := iadd1(FluidBibasisNormalForms);

    while PolynomGetLm(polynom) do
    <<
        involutiveDivisor := JanetTreeFind(PolynomGetLm(polynom));
        while involutiveDivisor do
        <<
            PolynomHeadReduceBy(polynom, TripleGetPolynom(involutiveDivisor));
            if PolynomGetLm(polynom) then
            <<
                involutiveDivisor := JanetTreeFind(PolynomGetLm(polynom));
            >>
            else
            <<
                involutiveDivisor := nil;
            >>;
        >>;
        if PolynomGetLm(polynom) then
        <<
            PolynomAdd(normalForm, (PolynomGetLm(polynom) . (nil . nil)));
            bibasis_remove(polynom);
        >>;
    >>;

    return normalForm;
end;


expr procedure ConstructInvolutiveBasis(polynomList, toGroebner);
begin scalar tmpTriple, tmpMonom, normalForm, normalFormLm, setTIterator, newTripleList;
    polynomList := PolynomListAutoReduce(polynomList, t);
    while car(polynomList) do
    <<
        SetQInsert(CreateTriple(car(polynomList)));
        polynomList := cdr(polynomList);
    >>;

    while not(SetQIsEmpty()) do
    <<
        tmpTriple := SetQGet();
        tmpMonom := TripleGetLm(tmpTriple);
        normalForm := NormalForm(TripleGetPolynom(tmpTriple));
        normalFormLm := PolynomGetLm(normalForm);

        if normalFormLm then
        <<
            FluidBibasisNonZeroNormalForms := iadd1(FluidBibasisNonZeroNormalForms);
            
            newTripleList := (nil . nil);
            setTIterator := FluidBibasisSetT;
            while car(setTIterator) do
            <<
                if MonomIsDivisibleBy(TripleGetLm(car(setTIterator)), normalFormLm) then
                <<
                    SetQDeleteDescendants(TripleGetID(car(setTIterator)));
                    SortedTripleListInsert(newTripleList, car(setTIterator));
                    JanetTreeDelete(TripleGetLm(car(setTIterator)));
                    bibasis_remove(setTIterator);
                >>
                else
                <<
                    setTIterator := cdr(setTIterator);
                >>;
            >>;

            if eq(tmpMonom, normalFormLm) then
            <<
                SetTInsert(CreateTripleWithAncestor(normalForm, TripleGetAncestorID(tmpTriple)));
                TripleSetProlongSet(car(FluidBibasisSetT), TripleGetProlongSet(tmpTriple));
            >>
            else
            <<
                SetTInsert(CreateTriple(normalForm));
            >>;
            SetTCollectNonMultiProlongations(newTripleList);
            
            if eqn(MonomGetDegree(normalFormLm), 0) then
            <<
                SetQReset();
            >>
            else
            <<
                SetQInsertList(newTripleList);
            >>;
        >>;
    >>;

    polynomList := (nil . nil);
    setTIterator := FluidBibasisSetT;
    while car(setTIterator) do
    <<
        SortedPolynomListInsert(polynomList, TripleGetPolynom(car(setTIterator)));
        setTIterator := cdr(setTIterator);
    >>;
    polynomList := PolynomListAutoReduce(polynomList, toGroebner);
    return polynomList;
end;


endmodule;
end;
