module bibasis_interface;

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


smacro procedure mk_sq(a); list('!*SQ, a, t);


expr procedure bibasis(initialSet, variablesList, monomialOrder, toGroebner);
begin scalar result, polynomList;
    if and(initialSet, car(initialSet) neq 'list) then 
    << 
        mathprint(initialSet);
        rederr "is not a polynomial list";
    >>;
    if null(cdr(initialSet)) then 
    <<
        mathprint(initialSet);
        rederr "polynomial list is empty";
    >>;
    if and(variablesList, car(variablesList) neq 'list) then 
    << 
        mathprint(variablesList);
        rederr "is not a variable list";
    >>;
    if null(cdr(variablesList)) then 
    <<
        mathprint(variablesList);
        rederr "variable list is empty";
    >>;
    if and(monomialOrder neq 'Lex,
           monomialOrder neq 'DegLex,
           monomialOrder neq 'DegRevLex) then
    <<
        mathprint(monomialOrder);
        rederr " is unsupported monomial ordering";
    >>;
  
    FluidBibasisVariables := cdr(variablesList);
    FluidBibasisMonomialOrder := monomialOrder;
    Init();
    
    polynomList := (nil . nil);
    if initialSet then
    <<
        for each polynom in cdr(initialSet) do
        <<
            SortedPolynomListInsert(polynomList, PolynomRead(numr(simp(reval(polynom)))));
        >>;
    >>;
    
    FluidBibasisRunningTime := time();
    FluidBibasisGCTime := gctime();

    polynomList := ConstructInvolutiveBasis(polynomList, toGroebner);

    FluidBibasisGCTime := gctime() - FluidBibasisGCTime;
    FluidBibasisRunningTime := time() - FluidBibasisRunningTime - FluidBibasisGCTime;
    
    while car(polynomList) do
    <<
        result := mk_sq(!*f2q PolynomWrite(car(polynomList))) . result;
        polynomList := cdr(polynomList);
    >>;
  
    return 'list . reverse(result);
end;


expr procedure bibasis_print_statistics();
if car(FluidBibasisSetQ) = nil then
begin
    terpri();
    write "        Variables order = ", car(FluidBibasisVariables);
    for each x in cdr(FluidBibasisVariables) do 
    <<
        write " > ", x;
    >>;
    terpri();
    
    write "Normal forms calculated = ", FluidBibasisNormalForms; terpri();
    write "  Non-zero normal forms = ", FluidBibasisNonZeroNormalForms; terpri();
    write "        Reductions made = ", FluidBibasisReductionsMade; terpri();
    write "Time: ", FluidBibasisRunningTime, " ms"; terpri();
    write "GC time: ", FluidBibasisGCTime, " ms"; terpri();
end;


lisp operator bibasis, bibasis_print_statistics;

endmodule;
end;
