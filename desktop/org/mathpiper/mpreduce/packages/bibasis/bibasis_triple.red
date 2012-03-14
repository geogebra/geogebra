module bibasis_triple;

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
% triple := mkvect(3);
% 0 - ID
% 1 - polynom
% 2 - ancestor ID
% 3 - prolong set


smacro procedure CreateProlongSet(); (nil . nil);


expr procedure CreateTriple(polynom);
begin scalar tmpTriple;
    tmpTriple := mkvect(3);
    putv(tmpTriple, 0, FluidBibasisTripleID);
    putv(tmpTriple, 1, polynom);
    putv(tmpTriple, 2, FluidBibasisTripleID);
    putv(tmpTriple, 3, CreateProlongSet());
    FluidBibasisTripleID := iadd1(FluidBibasisTripleID);
    return tmpTriple;
end;


expr procedure CreateTripleWithAncestor(polynom, ancestorID);
begin scalar tmpTriple;
    tmpTriple := mkvect(3);
    putv(tmpTriple, 0, FluidBibasisTripleID);
    putv(tmpTriple, 1, polynom);
    putv(tmpTriple, 2, ancestorID);
    putv(tmpTriple, 3, CreateProlongSet());
    FluidBibasisTripleID := iadd1(FluidBibasisTripleID);
    return tmpTriple;
end;


smacro procedure TripleGetID(triple); getv(triple, 0);
smacro procedure TripleGetPolynom(triple); getv(triple, 1);
smacro procedure TripleGetAncestorID(triple); getv(triple, 2);
smacro procedure TripleGetLm(triple); PolynomGetLm(getv(triple, 1));
smacro procedure TripleGetProlongSet(triple); getv(triple, 3);


expr procedure TripleIsProlongedBy(triple, variable);
begin scalar set;
    set := TripleGetProlongSet(triple);
    while and(car(set),
              car(set) > variable) do
    <<
        set := cdr(set);
    >>;
    if car(set) and car(set) = variable then
    <<
        return t;
    >>
    else
    <<
       return nil; 
    >>;
end;


expr procedure TripleSetProlongedBy(triple, variable);
begin scalar set;
    set := TripleGetProlongSet(triple);
    while and(car(set),
              car(set) > variable) do
    <<
        set := cdr(set);
    >>;
    if not(car(set) and car(set) = variable) then
    <<
        bibasis_insert(set, variable);
    >>;
end;


expr procedure TripleSetProlongSet(triple, prolongSet);
begin scalar set;
    while prolongSet do
    <<
        set := car(prolongSet) . set;
        prolongSet := cdr(prolongSet);
    >>;
    putv(triple, 3, reverse(set));
end;


endmodule;
end;
