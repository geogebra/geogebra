module bibasis_janet_tree;

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
% JanetTreeNode = (degree . triple) . (nextdeg . nextvar)
%   integer   degree
%   Triple    ph
%   JanetTreeNode nextdeg
%   JanetTreeNode nextvar
% ph != NIL && nextvar == NIL && nextdeg == NIL  
% ph == NIL && nextvar != NIL && (nextdeg == NIL || degree < nextdeg.degree)


smacro procedure CreateJanetTreeNode(degree, triple, nextDegree, nextVariable); (degree . triple) . (nextDegree . nextVariable);
smacro procedure JanetTreeNodeGetDegree(node); caar(node);
smacro procedure JanetTreeNodeGetTriple(node); cdar(node);
smacro procedure JanetTreeNodeGetNextDegree(node); cadr(node);
smacro procedure JanetTreeNodeGetNextVariable(node); cddr(node);

smacro procedure JanetTreeReset(); FluidBibasisJanetTreeRootNode := nil;


expr procedure JanetTreeNodeBuild(degree, variable, triple);
begin scalar tmpNode1, tmpNode2, monom;
    monom := TripleGetLm(triple);
    tmpNode1 := CreateJanetTreeNode(MonomGetVariableDegree(monom, variable), nil, nil, nil);
    tmpNode2 := tmpNode1;
    
    while igreaterp(degree, MonomGetVariableDegree(monom, variable)) do 
    <<
        degree := idifference(degree, MonomGetVariableDegree(monom, variable));
        variable := iadd1(variable);
        JanetTreeNodeGetNextVariable(tmpNode2) := CreateJanetTreeNode(MonomGetVariableDegree(monom, variable), nil, nil, nil);
        tmpNode2 := JanetTreeNodeGetNextVariable(tmpNode2);
    >>;
    JanetTreeNodeGetTriple(tmpNode2) := triple;
    return tmpNode1;
end;


expr procedure JanetTreeNodePrint(node);
begin
    if null(node) then
    <<
        prin2 "((nil . nil) . (nil . nil))"; terpri();
    >>
    else
    <<
        prin2 "(("; prin2 JanetTreeNodeGetDegree(node); prin2 ") . ("; 
        prin2 if JanetTreeNodeGetTriple(node) then TripleGetLm(JanetTreeNodeGetTriple(node)) else "nil"; 
        prin2 ")) . ((";
        
        if JanetTreeNodeGetNextDegree(node) then JanetTreeNodePrint(JanetTreeNodeGetNextDegree(node)) else prin2 "nil";
        prin2 ") . (";
        if JanetTreeNodeGetNextVariable(node) then JanetTreeNodePrint(JanetTreeNodeGetNextVariable(node)) else prin2 "nil";
        prin2 "))";
    >>;
end;


smacro procedure JanetTreePrint(); JanetTreeNodePrint(FluidBibasisJanetTreeRootNode);


expr procedure JanetTreeInsert(triple);
begin integer variable, degree; scalar nodeIterator, tmpNode, monom;
    monom := TripleGetLm(triple);
    degree := MonomGetDegree(monom);
    variable := 1;
    if null(FluidBibasisJanetTreeRootNode) then
    <<
        FluidBibasisJanetTreeRootNode := JanetTreeNodeBuild(degree, variable, triple);
    >>
    else
    <<
        nodeIterator := FluidBibasisJanetTreeRootNode;

        while degree > 0 do
        <<
            while and(nodeIterator,
                      JanetTreeNodeGetDegree(nodeIterator) < MonomGetVariableDegree(monom, variable), 
                      JanetTreeNodeGetNextDegree(nodeIterator)) do 
            <<
                nodeIterator := JanetTreeNodeGetNextDegree(nodeIterator);
            >>;
   
            if and(nodeIterator,
                   JanetTreeNodeGetDegree(nodeIterator) > MonomGetVariableDegree(monom, variable)) then
            <<
                tmpNode := JanetTreeNodeBuild(degree, variable, triple);
                degree := 0;
                
                JanetTreeNodeGetNextDegree(nodeIterator) := CreateJanetTreeNode(JanetTreeNodeGetDegree(nodeIterator),
                                                                                JanetTreeNodeGetTriple(nodeIterator),
                                                                                JanetTreeNodeGetNextDegree(nodeIterator),
                                                                                JanetTreeNodeGetNextVariable(nodeIterator));
                JanetTreeNodeGetNextVariable(nodeIterator) := JanetTreeNodeGetNextVariable(tmpNode);
                JanetTreeNodeGetDegree(nodeIterator) := JanetTreeNodeGetDegree(tmpNode);
                JanetTreeNodeGetTriple(nodeIterator) := JanetTreeNodeGetTriple(tmpNode);
            >>
            else if and(nodeIterator,
                        JanetTreeNodeGetDegree(nodeIterator) = MonomGetVariableDegree(monom, variable),
                        JanetTreeNodeGetNextVariable(nodeIterator)) then 
            <<
                degree := degree - MonomGetVariableDegree(monom, variable);
                variable := variable + 1;
                nodeIterator := JanetTreeNodeGetNextVariable(nodeIterator);
            >>
            else if not(null(nodeIterator)) then
            <<
                JanetTreeNodeGetNextDegree(nodeIterator) := JanetTreeNodeBuild(degree, variable, triple);
                degree := 0;
            >>;
        >>;
    >>;
end;


expr procedure JanetTreeFind(monom);
if null(FluidBibasisJanetTreeRootNode) then
    nil
else begin scalar result, nodeIterator; integer degree, variable;
    nodeIterator := FluidBibasisJanetTreeRootNode;
    degree := MonomGetDegree(monom);
    variable := 1;
    while igreaterp(degree, 0) do 
    <<
        while and(JanetTreeNodeGetDegree(nodeIterator) < MonomGetVariableDegree(monom, variable), 
                 JanetTreeNodeGetNextDegree(nodeIterator)) do 
        <<
            nodeIterator := JanetTreeNodeGetNextDegree(nodeIterator);
        >>;

        if neq(JanetTreeNodeGetDegree(nodeIterator), MonomGetVariableDegree(monom, variable)) then
        <<
            degree := 0;
        >>
        else if JanetTreeNodeGetNextVariable(nodeIterator) then 
        <<
            degree := idifference(degree, MonomGetVariableDegree(monom, variable));
            variable := iadd1(variable);
            nodeIterator := JanetTreeNodeGetNextVariable(nodeIterator);
        >>
        else 
        <<
            degree := 0;
            result := JanetTreeNodeGetTriple(nodeIterator);
        >>;
    >>;
    return result;
end;


expr procedure JanetTreeDelete(monom);
if not(null(FluidBibasisJanetTreeRootNode)) then
begin scalar nodeIterator, nodeIteratorParent, lastBifurcation, lastBifurcationParent, break, varDirection; integer variable, degree;
    nodeIterator := FluidBibasisJanetTreeRootNode;
    nodeIteratorParent := nil;
    lastBifurcation := FluidBibasisJanetTreeRootNode;
    lastBifurcationParent := nil;
    variable := 1;
    
    while not(break) do
    <<
        degree := MonomGetVariableDegree(monom, variable);

        while and(nodeIterator,
                  ilessp(JanetTreeNodeGetDegree(nodeIterator), degree)) do
        <<
            if eq(lastBifurcation, nodeIterator) then
            <<
                varDirection := nil;
            >>;
            
            nodeIteratorParent := nodeIterator;
            nodeIterator := JanetTreeNodeGetNextDegree(nodeIterator);
            
            if and(nodeIterator,
                   JanetTreeNodeGetNextDegree(nodeIterator),
                   JanetTreeNodeGetNextVariable(nodeIterator)) then
            <<
                lastBifurcation := nodeIterator;
                lastBifurcationParent := nodeIteratorParent;
            >>;
        >>;

        if and(nodeIterator,
               JanetTreeNodeGetNextVariable(nodeIterator)) then
        <<
            variable := iadd1(variable);
            if eq(lastBifurcation, nodeIterator) then
            <<
                varDirection := t;
            >>;
            
            nodeIteratorParent := nodeIterator;
            nodeIterator := JanetTreeNodeGetNextVariable(nodeIterator);
            
            if and(nodeIterator,
                   JanetTreeNodeGetNextDegree(nodeIterator),
                   JanetTreeNodeGetNextVariable(nodeIterator)) then
            <<
                lastBifurcation := nodeIterator;
                lastBifurcationParent := nodeIteratorParent;
            >>;
        >>
        else
        <<
            break := t;
        >>;
    >>;
    
    if varDirection then
    <<
        if eq(lastBifurcation, FluidBibasisJanetTreeRootNode) then
        <<
            % if last bifurcation is root node, just replace root node with its next degree subtree
            FluidBibasisJanetTreeRootNode := JanetTreeNodeGetNextDegree(FluidBibasisJanetTreeRootNode);
        >>
        else
        <<
            % connect bifurcation parent with bifurcation's next degree subtree
            % as far as we are in binary ring, max degree is 1 and bifurcation is next degree subtree of it's parent
            JanetTreeNodeGetNextVariable(lastBifurcationParent) := JanetTreeNodeGetNextDegree(lastBifurcation);
        >>;
    >>
    else
    <<
        JanetTreeNodeGetNextDegree(lastBifurcation) := nil;
    >>;
    
    % there were no bifurcations, i.e. lastBifurcation = FluidBibasisJanetTreeRootNode
    if not(or(JanetTreeNodeGetNextDegree(lastBifurcation), 
              JanetTreeNodeGetNextVariable(lastBifurcation))) then
    <<
        FluidBibasisJanetTreeRootNode := nil;
    >>;
end;


endmodule;
end;
