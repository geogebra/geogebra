module redlsp;    %%  GENTRAN LISP Code Generation Module  %%

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

% Entry Point:  LispCode


symbolic$

% GENTRAN Global Variables %
global '(!*lisparithexpops!* !*lisplogexpops!* !*redarithexpops!*
        !*redlogexpops!* !*redreswds!* !*redstmtgpops!* !*redstmtops!*)$
!*redarithexpops!*:= '(difference expt minus plus quotient recip times)$
!*redlogexpops!*   := '(and equal geq greaterp leq lessp neq not or)$
!*redreswds!*:= '(and rblock cond de difference end equal expt !~for for
                   geq getel go greaterp leq lessp list minus neq not or
                   plus plus2 prog progn procedure quotient read recip
                   repeat return setel setk setq stop times times2
                   while write)$ %REDUCE reserved words
!*redstmtgpops!*   := '(rblock progn)$
!*redstmtops!*     := '(cond end !~for for go repeat return setq stop
                        while write)$

% REDUCE Non-local Variable %

fluid '(!*period);

global '(deftype!*)$

global '(!*do!* !*for!*)$

% Irena variable referenced here.
global '(irena!-constants)$
irena!-constants := nil$

procedure lispcode forms;
for each f in forms collect
    if redexpp f then
        lispcodeexp(f, !*period)
    else if redstmtp f or redstmtgpp f then
        lispcodestmt f
    else if reddefp f then
        lispcodedef f
    else if pairp f then
        for each e in f collect lispcode e$

symbolic procedure check!-for!-irena!-constants form;
if listp form and memq(car form,!*redarithexpops!*) then
  for each u in cdr form do check!-for!-irena!-constants(u)
else if pairp form and car form memq '( !:cr!: !:crn!: !:gi!: )then
  repeat
  <<
    form := cdr form;
    check!-for!-irena!-constants(if atom form then form else car form);
  >>
  until atom form
else if form and atom form then
  if memq(form,irena!-constants) then set(get(form,'!*found!-flag),t)$

symbolic procedure lispcodeexp(form, fp);
% (RECIP exp) ==> (QUOTIENT 1.0 exp)                   %
% (DIFFERENCE exp1 exp2) ==> (PLUS exp1 (MINUS exp2))  %
% integer ==> floating point  iff  PERIOD flag is ON & %
%                                  not exponent &      %
%                                  not subscript &     %
%                                  not loop index      %
% The above is a little simplistic. We have problems
% With expressions like x**(1/2)
% Now believed fixed. JHD 14.5.88
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% mcd 16-11-88.  Added code to spot certain variables which irena
% needs to generate values for.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
begin
return if numberp form then
    if fp then
        float form
    else
        form
% Substitute (EXP 1) for e - mcd 29/4/88 %
else if form eq 'e then
    lispcodeexp(list('exp,1.0),fp)
else if atom form or car form memq '( !:rd!: !:cr!: !:crn!: !:gi!: )then
<<
    if irena!-constants and form and not stringp form then
        check!-for!-irena!-constants form;
    form
>>
else if car form eq 'expt then
    % Changes (EXPT E X) to (EXP X). mcd 29/4/88 %
    if cadr form eq 'e then
      lispcodeexp(list('exp,caddr form),fp)
    else if caddr form = '(quotient 1 2) then
      lispcodeexp(list('sqrt,cadr form),fp)
    else if eqcar(caddr form,'!:rd!:) then begin scalar r;
         r := realrat caddr form;
         return if r = '(1 . 2)
                  then {'sqrt,lispcodeexp(cadr form, fp)}
                 else {'expt,lispcodeexp(cadr form, fp),
                       lispcodeexp({'quotient,car r,cdr r},nil)}
         end
    else
      list('expt,lispcodeexp(cadr form,fp),lispcodeexp(caddr form,nil))
else if car form eq 'quotient then % re-instate periods if necessary
                                   %e.g. in expressions like **(1/3)
        list('quotient, lispcodeexp(cadr form, t),
                        lispcodeexp(caddr form, t))
else if car form eq 'recip then
    if !*period then % test this not FP, for same reason as above
        list('quotient, 1.0, lispcodeexp(cadr form, fp))
    else
        list('quotient, 1, lispcodeexp(cadr form, fp))
else if car form eq 'difference then
    list('plus, lispcodeexp(cadr form, fp),
                list('minus, lispcodeexp(caddr form, fp)))
else if not(car form memq !*lisparithexpops!*) and
        not(car form memq !*lisplogexpops!*) then
    for each elt in form collect lispcodeexp(elt, nil)
else
    for each elt in form collect lispcodeexp(elt, fp)$
end$


procedure lispcodestmt form;
if atom form then
    form
else if redassignp form then
    lispcodeassign form
else if redreadp form then
    lispcoderead form
else if redprintp form then
    lispcodeprint form
else if redwhilep form then
    lispcodewhile form
else if redrepeatp form then
    lispcoderepeat form
else if redforp form then
    lispcodefor form
else if redcondp form then
    lispcodecond form
else if redreturnp form then
    lispcodereturn form
else if redstmtgpp form then
    lispcodestmtgp form
else if reddefp form then
    lispcodedef form
else if car form eq 'literal then
    for each elt in form collect lispcodeexp(elt, nil)
else
    for each elt in form collect lispcodeexp(elt, !*period)$


symbolic procedure lispcodeassign form;
% Modified mcd 27/11/87 to prevent coercing things already declared as
% integers to reals when the PERIOD flag is on.
%
% (SETQ var (MAT lst lst')) --> (PROGN (SETQ (var 1 1) exp11)          %
%                                      (SETQ (var 1 2) exp12)          %
%                                       .                              %
%                                       .                              %
%                                      (SETQ (var m n) expmn))         %
if eqcar( caddr form, 'mat) then
    begin
    scalar name, r, c, relts, result,ftype;
    name := cadr form;
    form := caddr form;
    r := c := 1;
    ftype := symtabget(nil,name);
    if null ftype then ftype := !*period else
    << ftype := cadr ftype;
       ftype := if ftype equal 'integer or
            (ftype equal 'scalar and deftype!* equal 'integer) then nil
                                                         else !*period;
    >>;
    while form := cdr form do
    <<
        relts := car form;
        repeat
        <<
            result := mkassign(list(name, r, c),
                               lispcodeexp(car relts, ftype))
                                  . result;
            c := add1 c
        >>
        until null(relts := cdr relts);
        r := add1 r;
        c := 1
    >>;
    return mkstmtgp(nil, reverse result)
    end
else begin
        scalar ftype,name;
        name := cadr form;
        if pairp name then name := car name;
        ftype := symtabget(nil,name);
        if null ftype then ftype := !*period else
        << ftype := cadr ftype;
           ftype := if ftype equal 'integer or
             (ftype equal 'scalar and deftype!* equal 'integer) then nil
                                                          else !*period;
        >>;
        if cadr form eq 'e then % To prevent an 'e on the lhs
                                % being changed to exp(1) by lispcodeexp
                                % mcd 29/4/88
                return mkassign('e,lispcodeexp(caddr form, ftype))
        else
             return mkassign(lispcodeexp(cadr form, ftype),
                                    lispcodeexp(caddr form, ftype))
end$

procedure lispcoderead form;
% (SETQ var (READ)) --> (READ var) %
list('read, lispcodeexp(cadr form, nil))$

procedure lispcodeprint form;
'write . for each elt in cdr form collect lispcodeexp(elt, !*period)$

procedure lispcodewhile form;
'while . lispcodeexp(cadr form, !*period) .
         foreach st in cddr form collect lispcodestmt st$

procedure lispcoderepeat form;
begin
scalar body, logexp;
body := reverse cdr form;
logexp := car body;
body := reverse cdr body;
return 'repeat . append(foreach st in body collect lispcodestmt st,
                        list lispcodeexp(logexp, !*period))
end$

procedure lispcodefor form;
% (SETQ var1 (FOR var (exp1 exp2 exp3) SUM exp))
%   --> (PROGN (SETQ var1 0/0.0)
%             (FOR var (exp1 exp2 exp3) DO (SETQ var1 (PLUS var1 exp))))
% (SETQ var1 (FOR var (exp1 exp2 exp3) PRODUCT exp))
%   --> (PROGN (SETQ var1 1/1.0)
%            (FOR var (exp1 exp2 exp3) DO (SETQ var1 (TIMES var1 exp))))
if car form eq 'for then
    begin
    scalar explst, stmtlst;
    explst := list(cadr form, caddr form);
    stmtlst := cddddr form;
    return append(!*for!* .
                    foreach exp in explst collect lispcodeexp(exp, nil),
                  !*do!* .
                    foreach st in stmtlst collect lispcodestmt st)
    end
else
    begin
    scalar var1, var, explst, op, exp;
    var1 := cadr form;
    form := caddr form;
    var := cadr form;
    explst := caddr form;
    if cadddr form eq 'sum then
        op := 'plus
    else
        op := 'times;
    exp := car cddddr form;
    form := list('prog, nil,
                 lispcode list('setq,var1,if op eq 'plus then 0 else 1),
                 lispcode list(!*for!*, var, explst, !*do!*,
                      list('setq, var1, list(op, var1, exp))));
    return lispcodestmt form
    end$

procedure lispcodecond form;
begin
scalar result, pr;
while form := cdr form do
<<
    pr := car form;
    pr := lispcodeexp(car pr, !*period)
            . for each stmt in cdr pr collect lispcodestmt stmt;
    result := pr . result
>>;
return mkcond reverse result
end$

procedure lispcodereturn form;
% (RETURN NIL) --> (RETURN) %
if form member '((return) (return nil)) then
    list 'return
else
    mkreturn lispcodeexp(cadr form, !*period)$

procedure lispcodestmtgp form;
% (RBLOCK () stmt1 stmt2 .. stmtm)      %
%   --> (PROG () stmt1 stmt2 .. stmtm) %
if car form memq '(prog rblock) then
    mkstmtgp(cadr form,
             for each stmt in cddr form collect lispcodestmt stmt)
else
    mkstmtgp(0, for each stmt in cdr form collect lispcodestmt stmt)$

procedure lispcodedef form;
% (PROCEDURE id NIL EXPR (p1 p2 .. pn) stmt') %
%   --> (DEFUN id (p1 p2 .. pn) stmt')        %
if car form eq 'procedure then
    mkdef(cadr form, car cddddr form, for each stmt in cdr cddddr form
                                              collect lispcodestmt stmt)
else
    mkdef(cadr form, caddr form, for each stmt in cdddr form
                                          collect lispcodestmt stmt)$


%% REDUCE Form Predicates %%


procedure redassignp form;
eqcar(form, 'setq) and redassign1p caddr form$

procedure redassign1p form;
if atom form then
    t
else if car form eq 'setq then
    redassign1p caddr form
else if car form memq '(read for) then
    nil
else
    t$

procedure redcondp form;
eqcar(form, 'cond)$

procedure reddefp form;
eqcar(form, 'procedure)$

procedure redexpp form;
atom form or
car form memq !*redarithexpops!* or
car form memq !*redlogexpops!* or
not(car form memq !*redreswds!*)$

procedure redforp form;
if pairp form then
    if car form eq 'for then
        t
    else if car form eq 'setq then
        redfor1p caddr form$

procedure redfor1p form;
if atom form then
    nil
else if car form eq 'setq then
    redfor1p caddr form
else if car form eq 'for then
    t$

procedure redprintp form;
eqcar(form, 'write)$

procedure redreadp form;
eqcar(form, 'setq) and redread1p caddr form$

procedure redread1p form;
if atom form then
    nil
else if car form eq 'setq then
    redread1p caddr form
else if car form eq 'read then
    t$

procedure redrepeatp form;
eqcar(form, 'repeat)$

procedure redreturnp form;
eqcar(form, 'return)$

procedure redstmtp form;
atom form or
car form memq !*redstmtops!* or
atom car form and not(car form memq !*redreswds!*)$

procedure redstmtgpp form;
pairp form and car form memq !*redstmtgpops!*$

procedure redwhilep form;
eqcar(form, 'while)$

endmodule;

end;
