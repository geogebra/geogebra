module bibasis_tset;

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

%----------------------------------------------------------------------------
% T = triple_1 . ... . triple_n . (nil . nil) and triple_i >= triple_(i+1)


expr procedure SetTReset();
begin
    JanetTreeReset();
    FluidBibasisSetT := (nil . nil);
end;


expr procedure SetTInsert(triple);
begin
    JanetTreeInsert(triple);
    FluidBibasisSetT := (triple . FluidBibasisSetT);
end;


expr procedure SetTCollectNonMultiProlongations(tripleList);
if car(FluidBibasisSetT) then
begin scalar lastTriple, tmpPolynom, tmpTriple; integer lastNonMultiVar;
    lastTriple := car(FluidBibasisSetT);
    lastNonMultiVar := isub1(MonomGetFirstMultiVar(TripleGetLm(lastTriple)));
    for i:=1:lastNonMultiVar do
    <<
        if not(TripleIsProlongedBy(lastTriple, i)) then
        <<
            tmpPolynom := PolynomMultiplyByMonom(TripleGetPolynom(lastTriple), GetVariable(i));
            TripleSetProlongedBy(lastTriple, i);
            if PolynomGetLm(tmpPolynom) then
            <<
                tmpTriple := CreateTripleWithAncestor(tmpPolynom, TripleGetAncestorID(lastTriple));
                TripleSetProlongSet(tmpTriple, TripleGetProlongSet(lastTriple));
                SortedTripleListInsert(tripleList, tmpTriple);
            >>;
        >>;
    >>;
end;


expr procedure SetTPrint();
begin scalar currentTriple;
    prin2 "SetT( ";
    currentTriple := FluidBibasisSetT;
    while car(currentTriple) do
    <<
        prin2 car(currentTriple);
        prin2 ", ";
        currentTriple := cdr(currentTriple);
    >>;
    prin2 " )"; 
    terpri();
end;


endmodule;
end;
