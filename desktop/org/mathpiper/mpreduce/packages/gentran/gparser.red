module gparser;    %%  GENTRAN Parser Module  %%

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

% Entry Point:  GentranParse


symbolic$

% GENTRAN Global Variable %
global '(!*reservedops!*)$
!*reservedops!* := '(and rblock cond difference equal expt for geq go
                     greaterp leq lessp mat minus neq not or plus
                     procedure progn quotient read recip repeat return
                     setq times while write)$ %reserved operators


symbolic procedure gentranparse forms;
begin scalar found_error;
  for each f in forms do
    if not(gpstmtp f or gpexpp f or gpdefnp f) then
    <<
        gentranerr('e, f, "CANNOT BE TRANSLATED", nil);
        % If we are processing a template (for example) then this will
        % not result in a hard error, so make Gentran aware that
        % something went wrong:
        found_error := 't;
    >>;
  return not found_error;
end$

procedure gpexpp exp;
%  exp  ::=  id | number | (PLUS exp exp') | (MINUS exp) |             %
%         (DIFFERENCE exp exp) | (TIMES exp exp exp') |                %
%         (RECIP exp) |(QUOTIENT exp exp) | (EXPT exp exp) | (id arg') %
if atom exp then
    idp exp or numberp exp
else if car exp memq '(!:rd!: !:cr!:  !:crn!: !:gi!:) then
    t
else
    if car exp eq 'plus then
        length exp >= 2 and gpexpp cadr exp and gpexp1p cddr exp
    else if car exp memq '(minus recip) then
        length exp=2 and gpexpp cadr exp
    else if car exp memq '(difference quotient expt) then
        length exp=3 and gpexpp cadr exp and gpexpp caddr exp
    else if car exp eq 'times then
        length exp >= 3 and gpexpp cadr exp and gpexpp caddr exp and
         gpexp1p cdddr exp
    else if car exp eq '!:rd!: then t
    else if car exp memq '(!:cr!: !:crn!: !:gi!:) then t
    else if unresidp car exp then
        gparg1p cdr exp$

procedure gpexp1p exp;
%  exp'  ::=  exp exp' | eps  %
null exp or (gpexpp car exp and gpexp1p cdr exp)$

procedure gplogexpp exp;
%  logexp  ::=  id | (EQUAL exp exp) | (NEQ exp exp) |                 %
%            (GREATERP exp exp) |(GEQ exp exp) | (LESSP exp exp) |     %
%            (LEQ exp exp) | (NOT logexp) | (AND logexp logexp logexp')%
%            | (OR logexp logexp logexp') | (id arg')                  %
if atom exp then
    idp exp
else
    if car exp memq '(equal neq greaterp geq lessp leq) then
        length exp=3 and gpexpp cadr exp and gpexpp caddr exp
    else if car exp eq 'not then
        length exp=2 and gplogexpp cadr exp
    else if car exp memq '(and or) then
        length exp >= 3 and gplogexpp cadr exp and gplogexpp caddr exp
        and gplogexp1p cdddr exp
    else if unresidp car exp then
        gparg1p cdr exp$

procedure gplogexp1p exp;
%  logexp'  ::=  logexp logexp' | eps  %
null exp or (gplogexpp car exp and gplogexp1p cdr exp)$

procedure gpargp exp;
%  arg  ::=  string | exp | logexp  %
stringp exp or gpexpp exp or gplogexpp exp$

procedure gparg1p exp;
%  arg'  ::=  arg arg' | eps  %
null exp or (gpargp car exp and gparg1p cdr exp)$

procedure gpvarp exp;
%  var  ::=  id | (id exp exp')  %
if atom exp then
    idp exp
else
    if unresidp car exp then
        length exp >= 2 and gpexpp cadr exp and gpexp1p cddr exp$

procedure gplistp exp;
%  list  ::=  (exp exp')  %
if pairp exp then
    length exp >= 1 and gpexpp car exp and gpexp1p cdr exp$

procedure gplist1p exp;
%  list'  ::=  list list' | eps  %
null exp or (gplistp car exp and gplist1p cdr exp)$

procedure gpid1p exp;
%  id'  ::=  id id' | eps  %
null exp or (idp car exp and gpid1p cdr exp)$

procedure gpstmtp exp;
%  stmt  ::=  id | (SETQ setq') | (COND cond') | (WHILE logexp stmt) | %
%             (REPEAT stmt logexp) | (FOR var (exp exp exp) DO stmt) | %
%             (GO id) | (RETURN arg) | (WRITE arg arg') |              %
%             (PROGN stmt stmt') | (BLOCK (id') stmt') | (id arg')     %
if atom exp then
    idp exp
else if car exp memq '(!:rd!: !:cr!: !:crn!: !:gi!:) then
    nil
else
    if car exp eq 'setq then
        gpsetq1p cdr exp
    else if car exp eq 'cond then
        gpcond1p cdr exp
    else if car exp eq 'while then
        length exp=3 and gplogexpp cadr exp and gpstmtp caddr exp
    else if car exp eq 'repeat then
        length exp=3 and gpstmtp cadr exp and gplogexpp caddr exp
    else if car exp eq 'for then
        length exp=5 and gpvarp cadr exp and pairp caddr exp and
         (length caddr exp=3 and gpexpp car caddr exp and
          gpexpp cadr caddr exp and gpexpp caddr caddr exp) and
           cadddr exp eq 'do and gpstmtp car cddddr exp
    else if car exp eq 'go then
        length exp=2 and idp cadr exp
    else if car exp eq 'return then
        length exp=2 and gpargp cadr exp
    else if car exp eq 'write then
        length exp >= 2 and gpargp cadr exp and gparg1p cddr exp
    else if car exp eq 'progn then
        length exp >= 2 and gpstmtp cadr exp and gpstmt1p cddr exp
    else if car exp eq 'rblock then
        length exp >= 2 and gpid1p cadr exp and gpstmt1p cddr exp
    else if unresidp car exp then
        gparg1p cdr exp$

procedure gpsetq1p exp;
%  setq'  ::=  id setq'' | (id exp exp') setq'''  %
if exp and length exp=2 then
    if atom car exp then
        idp car exp and gpsetq2p cdr exp
    else
        (length car exp >= 2 and idp car car exp
             and unresidp car car exp and gpexpp cadr car exp
             and gpexp1p cddr car exp) and gpsetq3p cdr exp$

procedure gpsetq2p exp;
%  setq''  ::=  (MAT list list') | setq'''  %
if exp then
    if eqcar(car exp, 'mat) then
        onep length exp and (gplistp cadar exp and gplist1p cddar exp)
    else
        gpsetq3p exp$

procedure gpsetq3p exp;
% setq''' ::= (FOR var (exp exp exp) forop exp) | (READ) | exp | logexp
if exp and onep length exp then
    gpexpp car exp or
     gplogexpp car exp or
        (if caar exp eq 'for then
            length car exp=5 and gpvarp cadar exp and
             (pairp caddar exp and length caddar exp=3 and
              gpexpp car caddar exp and gpexpp cadr caddar exp and
               gpexpp caddr caddar exp) and gpforopp car cdddar exp and
                gpexpp cadr cdddar exp
        else if caar exp eq 'read then
            onep length car exp)$

procedure gpforopp exp;
%  forop  ::=  SUM | PRODUCT  %
exp memq '(sum product)$

procedure gpcond1p exp;
%  cond'  ::=  (logexp stmt) cond' | eps  %
null exp or
    (pairp car exp and length car exp=2 and gplogexpp caar exp and
     gpstmtp cadar exp and gpcond1p cdr exp)$

procedure gpstmt1p exp;
%  stmt'  ::=  stmt stmt' | eps  %
   null exp or (gpstmtp car exp and gpstmt1p cdr exp)$

procedure gpdefnp exp;
%  defn  ::=  (PROCEDURE id NIL EXPR (id') stmt)  %
eqcar(exp, 'procedure) and length exp=6 and
 idp cadr exp and null caddr exp and atom cadddr exp and
 gpid1p car cddddr exp and gpstmtp cadr cddddr exp
        and not idp cadr cddddr exp$

%%            %%
%% Predicates %%
%%            %%


procedure unresidp id;
not (id memq !*reservedops!*)$


endmodule;

end;
