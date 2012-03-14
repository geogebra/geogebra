module segmnt;    %%  Segmentation Module  %%

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


%%  Author:  Barbara L. Gates  %%
%%  December 1986              %%

% Entry points:  Seg, MARKEDVARP, MARKVAR, TEMPVAR, UNMARKVAR


symbolic$

% User-Accessible Global Variables %
global '(gentranlang!* maxexpprintlen!* tempvarname!* tempvarnum!*
         tempvartype!*)$
share gentranlang!*, maxexpprintlen!*, tempvarname!*, tempvarnum!*,
      tempvartype!*$
maxexpprintlen!* := 800$
tempvarname!*    := 't$
tempvarnum!*     := 0$
tempvartype!*    := nil$

% User-Accessible Primitive Functions %
operator markedvarp, markvar, tempvar, unmarkvar$

global '(!*do!* !*for!*)$


%%                       %%
%% Segmentation Routines %%
%%                       %%


procedure seg forms;
% exp --+--> exp                                          %
%       +--> (assign    assign    ... assign      exp   ) %
%                   (1)       (2)           (n-1)    (n)  %
% stmt --+--> stmt                                        %
%        +--> stmtgp                                      %
% stmtgp --> stmtgp                                       %
% def --> def                                             %
for each f in forms collect
    if lispexpp f then
        if toolongexpp f then
            segexp(f, 'unknown)
        else
            f
    else if lispstmtp f then
        segstmt f
    else if lispstmtgpp f then
        if toolongstmtgpp f then
            seggroup f
        else
            f
    else if lispdefp f then
        if toolongdefp f then
            segdef f
        else
            f
    else
        f$


procedure segexp(exp, type);
% exp --> (assign    assign    ... assign      exp   ) %
%                (1)       (2)           (n-1)    (n)  %
reverse segexp1(exp, type)$

procedure segexp1(exp, type);
% exp --> (exp    assign      assign      ... assign   ) %
%             (n)       (n-1)       (n-2)           (1)  %
begin
scalar res;
res := segexp2(exp, type);
unmarkvar res;
if car res = cadadr res then
<<
    res := cdr res;
    rplaca(res, caddar res)
>>;
return res
end$

procedure segexp2(exp, type);
% exp --> (exp    assign      assign      ... assign   ) %
%             (n)       (n-1)       (n-2)           (1)  %
begin
scalar expn, assigns, newassigns, unops, op, termlist, var, tmp;
expn := exp;
while length expn=2 do
<<  unops := car expn . unops;  expn := cadr expn  >>;
op := car expn;
for each term in cdr expn do
<<
    if toolongexpp term then
    <<
        tmp := segexp2(term, type);
        term := car tmp;
        newassigns := cdr tmp
    >>
    else
        newassigns := '();
    if toolongexpp (op . term . termlist) and
       termlist and
       (length termlist > 1 or pairp car termlist) then
        <<
            unmarkvar termlist;
            var := var or tempvar type;
            markvar var;
            assigns := mkassign(var, if onep length termlist
                                        then car termlist
                                        else op . termlist) . assigns;
            termlist := list(var, term)
        >>
        else
            termlist := append(termlist, list term);
        assigns := append(newassigns, assigns)
>>;
expn := if onep length termlist
           then car termlist
           else op . termlist;
while unops do
<<  expn := list(car unops, expn);  unops := cdr unops  >>;
if expn = exp then
<<
    unmarkvar expn;
    var := var or tempvar type;
    markvar var;
    assigns := list mkassign(var, expn);
    expn := var
>>;
return expn . assigns
end$


procedure segstmt stmt;
% assign --+--> assign %
%          +--> stmtgp %
% cond --+--> cond     %
%        +--> stmtgp   %
% while --+--> while   %
%         +--> stmtgp  %
% repeat --> repeat    %
% for --+--> for       %
%       +--> stmtgp    %
% return --+--> return %
%          +--> stmtgp %
if lispassignp stmt then
    if toolongassignp stmt then
        segassign stmt
    else
        stmt
else if lispcondp stmt then
    if toolongcondp stmt then
        segcond stmt
    else
        stmt
else if lispwhilep stmt then
    if toolongwhilep stmt then
        segwhile stmt
    else
        stmt
else if lisprepeatp stmt then
    if toolongrepeatp stmt then
        segrepeat stmt
    else
        stmt
else if lispforp stmt then
    if toolongforp stmt then
        segfor stmt
    else
        stmt
else if lispreturnp stmt then
    if toolongreturnp stmt then
        segreturn stmt
    else
        stmt
else
    stmt$


procedure segassign stmt;
% assign --> stmtgp %
begin
scalar var, exp, type;
var := cadr stmt;
type := getvartype var;
exp := caddr stmt;
stmt := segexp1(exp, type);
rplaca(stmt, mkassign(var, car stmt));
return mkstmtgp(nil, reverse stmt)
end$

procedure segcond condd;
% cond --+--> cond   %
%        +--> stmtgp %
begin
scalar tassigns, res, markedvars, type;
%if gentranlang!* eq 'c
%    then type := 'int
%    else type := 'logical;
type:=get(gentranlang!*,'boolean!-type) or get('fortran,'boolean!-type);
while condd := cdr condd do
    begin
    scalar exp, stmt;
    if toolongexpp(exp := caar condd) then
    <<
        exp := segexp1(exp, type);
        tassigns := append(cdr exp, tassigns);
        exp := car exp;
        markvar exp;
        markedvars := exp . markedvars
    >>;
    stmt := for each st in cdar condd conc seg list st;
    res := (exp . stmt) . res
    end;
unmarkvar markedvars;
return
    if tassigns then
        mkstmtgp(nil, reverse(mkcond reverse res . tassigns))
    else
        mkcond reverse res
end$

procedure segwhile stmt;
% while --+--> while  %
%         +--> stmtgp %
begin
scalar logexp, stmtlst, tassigns, type, res;
logexp := cadr stmt;
stmtlst := cddr stmt;
if toolongexpp logexp then
<<
    type:=get(gentranlang!*,'boolean!-type)
          or get('fortran,'boolean!-type);
%    if gentranlang!* eq 'c
%       then type := 'int
%       else type := 'logical;
    tassigns := segexp1(logexp, type);
    logexp := car tassigns;
    tassigns := cdr tassigns
>>;
stmtlst := foreach st in stmtlst
                   conc seg list st;
res := 'while . logexp . stmtlst;
if tassigns then
<<
    res := append(res, reverse tassigns);
    res := 'progn . append(reverse tassigns, list res)
>>;
return res
end$

procedure segrepeat stmt;
% repeat --> repeat %
begin
scalar stmtlst, logexp, type;
stmt := reverse cdr stmt;
logexp := car stmt;
stmtlst := reverse cdr stmt;
stmtlst := foreach st in stmtlst conc seg list st;
if toolongexpp logexp then
<<
    type:=get(gentranlang!*,'boolean!-type)
          or get('fortran,'boolean!-type);
%    if gentranlang!* eq 'c
%       then type := 'int
%       else type := 'logical;
    logexp := segexp1(logexp, type);
    stmtlst := append(stmtlst, reverse cdr logexp);
    logexp := car logexp
>>;
return 'repeat . append(stmtlst, list logexp)
end$

procedure segfor stmt;
% for --+--> for    %
%       +--> stmtgp %
begin
scalar var, loexp, stepexp, hiexp, stmtlst, tassigns1, tassigns2, type,
       markedvars, res;
var := cadr stmt;
type := getvartype var;
stmt := cddr stmt;
loexp := caar stmt;
stepexp := cadar stmt;
hiexp := caddar stmt;
stmtlst := cddr stmt;
if toolongexpp loexp then
<<
    loexp := segexp1(loexp, type);
    tassigns1 := reverse cdr loexp;
    loexp := car loexp;
    markvar loexp;
    markedvars := loexp . markedvars
>>;
if toolongexpp stepexp then
<<
    stepexp := segexp1(stepexp, type);
    tassigns2 := reverse cdr stepexp;
    stepexp := car stepexp;
    markvar stepexp;
    markedvars := stepexp . markedvars
>>;
if toolongexpp hiexp then
<<
    hiexp := segexp1(hiexp, type);
    tassigns1 := append(tassigns1, reverse cdr hiexp);
    tassigns2 := append(tassigns2, reverse cdr hiexp);
    hiexp := car hiexp
>>;
unmarkvar markedvars;
stmtlst := foreach st in stmtlst conc seg list st;
stmtlst := append(stmtlst, tassigns2);
res := !*for!* . var . list(loexp, stepexp, hiexp) . !*do!* . stmtlst;
if tassigns1 then
    return mkstmtgp(nil, append(tassigns1, list res))
else
    return res
end$

procedure segreturn ret;
% return --> stmtgp %
<<
    ret := segexp1(cadr ret, 'unknown);
    rplaca(ret, mkreturn car ret);
    mkstmtgp(nil, reverse ret)
>>$

procedure seggroup stmtgp;
% stmtgp --> stmtgp %
begin
scalar locvars, res;
if car stmtgp eq 'prog then
<<
    locvars := cadr stmtgp;
    stmtgp := cdr stmtgp
>>
else
    locvars := 0;
while stmtgp := cdr stmtgp do
    res := append(seg list car stmtgp, res);
return mkstmtgp(locvars, reverse res)
end$

procedure segdef deff;
% def --> def %
mkdef(cadr deff, caddr deff,
      for each stmt in cdddr deff conc seg list stmt)$


%%                                        %%
%% Long Statement & Expression Predicates %%
%%                                        %%


procedure toolongexpp exp;
numprintlen exp > maxexpprintlen!*$

procedure toolongstmtp stmt;
if atom stmt then nil else
if lispstmtp stmt then
    if lispcondp stmt then
        toolongcondp stmt
    else if lispassignp stmt then
        toolongassignp stmt
    else if lispreturnp stmt then
        toolongreturnp stmt
    else if lispwhilep stmt then
        toolongwhilep stmt
    else if lisprepeatp stmt then
        toolongrepeatp stmt
    else if lispforp stmt then
        toolongforp stmt
    else lispeval('or . for each exp in stmt collect toolongexpp exp)
else
    toolongstmtgpp stmt$

procedure toolongassignp assign;
toolongexpp caddr assign$

procedure toolongcondp condd;
begin
scalar toolong;
while condd := cdr condd do
    if toolongexpp caar condd or toolongstmtp cadar condd then
        toolong := t;
return toolong
end$

procedure toolongwhilep stmt;
   toolongexpp cadr stmt or
      lispeval('or . foreach st in cddr stmt collect toolongstmtp st)$

procedure toolongrepeatp stmt;
<<
    stmt := reverse cdr stmt;
    toolongexpp car stmt or
    lispeval('or . foreach st in cdr stmt collect toolongstmtp st)
>>$

procedure toolongforp stmt;
lispeval('or . foreach exp in caddr stmt collect
                   toolongexpp exp          ) or
lispeval('or . foreach st in cddddr stmt collect
                   toolongstmtp st          )$

procedure toolongreturnp ret;
cdr ret and toolongexpp cadr ret$

procedure toolongstmtgpp stmtgp;
lispeval('or . for each stmt in cdr stmtgp collect
               toolongstmtp stmt              )$

procedure toolongdefp deff;
if lispstmtgpp cadddr deff then
    toolongstmtgpp cadddr deff
else
    lispeval('or .
             for each stmt in cdddr deff collect toolongstmtp stmt)$


%%                       %%
%% Print Length Function %%
%%                       %%

symbolic procedure numprintlen exp;
if atom exp then
    length explode exp
else if onep length exp then
    numprintlen car exp
else if car exp = '!:rd!: then
%     2+length explode cadr exp + length explode cddr exp
%else if car exp memq '( !:cr!: !:crn!: !:gi!: ) then
%     8+length explode cadr exp + length explode cddr exp
<<
     exp := rd!:explode exp;
     2+length car exp + length explode cadr exp
>>
else if car exp memq '( !:cr!: !:crn!: !:gi!: ) then
<<
    exp := cons (rd!:explode('!:rd!: . cadr exp),
                 rd!:explode('!:rd!: . cddr exp));
    12 + length caar exp + length explode cdar exp
       + length cadr exp + length explode cddr exp
>>
else
    length exp + lispeval('plus . for each elt in cdr exp collect
                                numprintlen elt              )$


%%                                                              %%
%% Temporary Variable Generation, Marking & Unmarking Functions %%
%%                                                              %%


procedure tempvar type;
%                                                             %
%  IF type Member '(NIL 0) THEN type <- TEMPVARTYPE!*         %
%                                                             %
%  IF type Neq 'NIL And type Neq 'UNKNOWN THEN                %
%    var <- 1st unmarked tvar of VType type or of VType NIL   %
%           which isn't in the symbol table                   %
%    put type on var's VType property list                    %
%    put declaration in symbol table                          %
%  ELSE IF type = NIL THEN                                    %
%    var <- 1st unmarked tvar of type NIL which isn't in the  %
%           symbol table                                      %
%  ELSE type = 'UNKNOWN                                       %
%    var <- 1st unmarked tvar of type NIL which isn't in the  %
%           symbol table                                      %
%    put 'UNKNOWN on var's VType property list                %
%    print warning - "undeclared"                             %
%                                                             %
%  RETURN var                                                 %
%                                                             %
begin
scalar tvar, xname, num;
if type memq '(nil 0) then type := tempvartype!*;
xname := explode tempvarname!*;
num := tempvarnum!*;
if type memq '(nil unknown) then
    repeat
    <<
        tvar := intern compress append(xname, explode num);
        num := add1 num
    >>
    until not markedvarp tvar and not get(tvar, '!*vtype!*) and
          not getvartype tvar
else
    repeat
    <<
        tvar := intern compress append(xname, explode num);
        num := add1 num
    >>
    until not markedvarp tvar and
          (get(tvar, '!*vtype!*) eq type or
           not get(tvar, '!*vtype!*) and not getvartype tvar);
put(tvar, '!*vtype!*, type);
if type eq 'unknown then
    gentranerr('w, tvar, "UNDECLARED VARIABLE", nil)
else if type then
    symtabput(nil, tvar, list type);
return tvar
end$


symbolic procedure isnumber u;
numberp(u) or (pairp(u) and memq(car u,domainlist!*) )$

symbolic procedure markvar var;
if isnumber var then
    var
else if atom var then
<<  flag(list var, '!*marked!*);  var  >>
else
<<  for each v in var do markvar v;  var  >>$

symbolic procedure markedvarp var;
flagp(var, '!*marked!*)$

symbolic procedure unmarkvar var;
if isnumber var then
    var
else if atom var then
    remflag(list var, '!*marked!*)
else
    foreach elt in var do
        unmarkvar elt$


endmodule;

end;
