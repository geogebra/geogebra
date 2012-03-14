module bibasis_qset;

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
% Q = triple_1 . ... . triple_n . (nil . nil) and triple_i <= triple_(i+1)


expr procedure SortedTripleListInsert(tripleList, triple);
begin scalar tmpMonom, tripleListIterator;
    tmpMonom := TripleGetLm(triple);
    tripleListIterator := tripleList;
    while and(car(tripleListIterator),
              neq(MonomCompare(TripleGetLm(car(tripleListIterator)), tmpMonom), 1)) do
    <<
        tripleListIterator := cdr(tripleListIterator);
    >>;
    bibasis_insert(tripleListIterator, triple);
end;


smacro procedure SetQReset(); FluidBibasisSetQ := (nil . nil);
smacro procedure SetQIsEmpty(); null(car(FluidBibasisSetQ));
smacro procedure SetQInsert(triple); SortedTripleListInsert(FluidBibasisSetQ, triple);


expr procedure SetQInsertList(tripleList);
begin scalar iteratorQ, iteratorList; integer monomCompare;
    iteratorQ := FluidBibasisSetQ;
    iteratorList := tripleList;
    while and(car(iteratorQ), car(iteratorList)) do 
    <<
        monomCompare := MonomCompare(TripleGetLm(car(iteratorQ)), TripleGetLm(car(iteratorList)));
        if or(monomCompare = -1,
              monomCompare = 0) then
        <<
            iteratorQ := cdr(iteratorQ);
        >>
        else
        <<
            bibasis_insert(iteratorQ, car(iteratorList));
            iteratorQ := cdr(iteratorQ);
            iteratorList := cdr(iteratorList);
        >>;
    >>;
    if car(iteratorList) then
    <<
        bibasis_remove(rplacd(iteratorQ, iteratorList));
    >>;
end;


expr procedure SetQGet();
begin scalar triple;
    triple := car(FluidBibasisSetQ);
    bibasis_remove(FluidBibasisSetQ);
    return triple;
end;


expr procedure SetQDeleteDescendants(ancestorID);
begin scalar currentTriple;
    currentTriple := FluidBibasisSetQ;
    while car(currentTriple) do
    <<
        if TripleGetAncestorID(car(currentTriple)) = ancestorID then
        <<
            bibasis_remove(currentTriple);
        >>
        else
        <<
            currentTriple := cdr(currentTriple);
        >>;
    >>;
end;


expr procedure SetQPrint();
begin scalar currentTriple;
    prin2 "SetQ( ";
    currentTriple := FluidBibasisSetQ;
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
