module utils;   %%  GENTRAN Utility Functions  %%

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

% Entry Points:  ALL FUNCTIONS


symbolic$


% User-Accessible Primitive Function %
operator genstmtnum$

% User-Accessible Global Variables %
global '(genstmtincr!* genstmtnum!* tablen!*)$
share genstmtincr!*, genstmtnum!*, tablen!*$
genstmtincr!* := 1$
genstmtnum!*  := 25000$
tablen!*      := 4$

% GENTRAN Global Variables %
global '(!*lisparithexpops!* !*lispdefops!* !*lisplogexpops!*
         !*lispstmtgpops!* !*lispstmtops!* !*symboltable!*)$
!*lisparithexpops!* := '(expt minus plus quotient times)$
    %LISP arithmetic expression operators
!*lispdefops!*      := '(defun)$  %LISP function definition operator
!*lisplogexpops!*   := '(and equal geq greaterp leq lessp neq not or)$
    %LISP logical & relational exp operators
!*lispstmtgpops!*   := '(prog progn)$  %LISP statement group operators
!*lispstmtops!*     := '(break cond end for go read repeat
                         return setq stop while write)$
%LISP statement operators
!*symboltable!*     := '(!*main!*)$  %symbol table

global '(!*for!*)$


%%                                      %%
%% Statement Number Generation Function %%
%%                                      %%


procedure genstmtnum;
genstmtnum!* := genstmtnum!* + genstmtincr!*$


%%                                                        %%
%% Symbol Table Insertion, Retrieval & Deletion Functions %%
%%                                                        %%


procedure symtabput(name, type, value);
%                                                                      %
% CALL                                               INSERTS           %
% SymTabPut(subprogname, NIL,         NIL         )  subprogram name   %
% SymTabPut(subprogname, '!*Type!*,   subprogtype )  subprogram type   %
% SymTabPut(subprogname, '!*Params!*, paramlist   )  parameter list    %
% SymTabPut(subprogname, vname,  '(type d1 d2 ...))  type & dimensions %
%                                                    for variable,     %
%                                                    variable range,   %
%   if subprogname=NIL                               parameter, or     %
%      then subprogname <-- Car symboltable          function name     %
%                                                                      %
<<
    name := name or car !*symboltable!*;
    !*symboltable!* := name . delete(name, !*symboltable!*);
    if type memq '(!*type!* !*params!*) then
        put(name, type, value)
    else if type then
        begin
        scalar v, vtype, vdims, dec, decs;
        v := type;
        vtype := car value;
        vdims := cdr value;
        decs := get(name, '!*decs!*);
        dec := assoc(v, decs);
        decs := delete(dec, decs);
        vtype := vtype or (if length dec > 1 then cadr dec);
        vdims := vdims or (if length dec > 2 then cddr dec);
        dec := v . vtype . vdims;
        put(name, '!*decs!*, append(decs, list dec))
        end
>>$

procedure symtabget(name, type);
%                                                                      %
% CALL                                 RETRIEVES                       %
% SymTabGet(NIL,         NIL        )  all subprogram names            %
% SymTabGet(subprogname, '!*Type!*  )  subprogram type                 %
% SymTabGet(subprogname, '!*Params!*)  parameter list                  %
% SymTabGet(subprogname, vname      )  type & dimensions for variable, %
%                                      variable range, parameter, or   %
%                                      function name                   %
% SymTabGet(subprogname, '!*Decs!*  )  all types & dimensions          %
%                                                                      %
%   if subprogname=NIL & 2nd arg is non-NIL                            %
%      then subprogname <-- Car symboltable                            %
%                                                                      %
<<
    if type then name := name or car !*symboltable!*;
    if null name then
        !*symboltable!*
    else if type memq '(!*type!* !*params!* !*decs!*) then
        get(name, type)
    else
        assoc(type, get(name, '!*decs!*))
>>$


symbolic procedure declared!-as!-float u;
begin scalar decs;
    return (decs := symtabget(nil,u)) and
            memq(cadr decs,
                 '(real real!*8 real!*16
                   double! precision double float) )$
end$

procedure symtabrem(name, type);
%                                                                      %
% CALL                                 DELETES                         %
% SymTabRem(subprogname, NIL        )  subprogram name                 %
% SymTabRem(subprogname, '!*Type!*  )  subprogram type                 %
% SymTabRem(subprogname, '!*Params!*)  parameter list                  %
% SymTabRem(subprogname, vname      )  type & dimensions for variable, %
%                                      variable range, parameter, or   %
%                                      function name                   %
% SymTabRem(subprogname, '!*Decs!*  )  all types & dimensions          %
%                                                                      %
%   if subprogname=NIL                                                 %
%      then subprogname <-- Car symboltable                            %
%                                                                      %
<<
    name := name or car !*symboltable!*;
    if null type then
        !*symboltable!* := delete(name, !*symboltable!*) or '(!*main!*)
    else if type memq '(!*type!* !*params!* !*decs!*) then
        remprop(name, type)
    else
        begin
        scalar v, dec, decs;
        v := type;
        decs := get(name, '!*decs!*);
        dec := assoc(v, decs);
        decs := delete(dec, decs);
        put(name, '!*decs!*, decs)
        end
>>$

procedure getvartype var;
begin
scalar type;
if pairp var then
    var := car var;
type := symtabget(nil, var);
if type and length type >= 2 then
    type := cadr type
else
    type := nil;
return type
end$

procedure arrayeltp exp;
length symtabget(nil, car exp) > 2 or equal(car exp,'dummyArrayToken)$


%%                                 %%
%% Functions for Making LISP Forms %%
%%                                 %%


procedure mkassign(var, exp);
list('setq, var, exp)$

procedure mkcond pairs;
'cond . pairs$

procedure mkdef(name, params, body);
append(list('defun, name, params), body)$

procedure mkreturn exp;
list('return, exp)$

procedure mkstmtgp(vars, stmts);
if numberp vars then
    'progn . stmts
else
    'prog . vars . stmts$


%% LISP Form Predicates %%


procedure lispassignp stmt;
eqcar(stmt,'setq)$

procedure lispbreakp form;
eqcar(form, 'break)$

procedure lispcallp form;
pairp form$

procedure lispcondp stmt;
eqcar(stmt, 'cond)$

procedure lispdefp form;
pairp form and car form memq !*lispdefops!*$

procedure lispexpp form;
atom form or
car form memq !*lisparithexpops!* or
car form memq !*lisplogexpops!* or
not (car form memq !*lispstmtops!*) and
not (car form memq !*lispstmtgpops!*) and
not (car form memq !*lispdefops!*)$

procedure lispendp form;
   eqcar( form, 'end)$

procedure lispforp form;
   eqcar( form, !*for!*)$

procedure lispgop form;
   eqcar( form, 'go)$

procedure lisplabelp form;
   atom form$

procedure lispprintp form;
   eqcar( form, 'write)$

procedure lispreadp form;
   eqcar( form, 'read)$

procedure lisprepeatp form;
   eqcar(form, 'repeat)$

procedure lispreturnp stmt;
   eqcar( stmt, 'return)$

procedure lispstmtp form;
atom form or
car form memq !*lispstmtops!* or
( atom car form and
  not (car form memq !*lisparithexpops!* or
       car form memq !*lisplogexpops!* or
       car form memq !*lispstmtgpops!* or
       car form memq !*lispdefops!*) )$

procedure lispstmtgpp form;
pairp form and car form memq !*lispstmtgpops!*$

procedure lispstopp form;
   eqcar(form, 'stop)$

procedure lispwhilep form;
   eqcar(form, 'while)$


%%                                               %%
%% Type Predicates & Type List Forming Functions %%
%%                                               %%


procedure formtypelists varlists;
% ( (var TYPE d1 d2...)       ( (TYPE (var d1 d2...) ...)   %
%   :                     ==>   :                           %
%   (var TYPE d1 d2...) )       (TYPE (var d1 d2...) ...) ) %
begin
scalar type, typelists, tl;
for each vl in varlists do
<<
    type := cadr vl;
    if onep length(vl := delete(type, vl)) then
        vl := car vl;
    if (tl := assoc(type, typelists)) then
        typelists := delete(tl, typelists)
    else
        tl := list type;
    typelists := append(typelists, list append(tl, list vl))
>>;
return typelists
end$


procedure functionformp(stmt, name);
% Does stmt contain an assignment which assigns a value to name? %
% Does it contain a RETURN exp; stmt?                            %
% (i.e., (SETQ name exp) -or- (RETURN exp)                       %
if null stmt or atom stmt then
    nil
else if car stmt eq 'setq and cadr stmt eq name then
    t
else if car stmt eq 'return and cdr stmt then
    t
else
    lispeval('or . for each st in stmt collect functionformp(st, name))$

procedure implicitp type;
begin
scalar xtype, ximp, r;
xtype := explode2 type;
ximp := explode2 'implicit;
r := t;
repeat
    r := r and (car xtype eq car ximp)
until null(xtype := cdr xtype) or null(ximp := cdr ximp);
return r
end$


%%                 %%
%% Misc. Functions %%
%%                 %%


procedure insertcommas lst;
begin
scalar result;
if null lst then
    return nil;
result := list car lst;
while lst := cdr lst do
    result := car lst . '!, . result;
return reverse result
end$

procedure insertparens exp;
'!( . append(exp, list '!))$

procedure optype op;
get(op, '!*optype!*)$

put('minus,    '!*optype!*, 'unary )$
put('not,      '!*optype!*, 'unary )$
put('quotient, '!*optype!*, 'binary)$
put('expt,     '!*optype!*, 'binary)$
put('equal,    '!*optype!*, 'binary)$
put('neq,      '!*optype!*, 'binary)$
put('greaterp, '!*optype!*, 'binary)$
put('geq,      '!*optype!*, 'binary)$
put('lessp,    '!*optype!*, 'binary)$
put('leq,      '!*optype!*, 'binary)$
put('plus,     '!*optype!*, 'nary  )$
put('times,    '!*optype!*, 'nary  )$
put('and,      '!*optype!*, 'nary  )$
put('or,       '!*optype!*, 'nary  )$

procedure seqtogp lst;
if null lst or atom lst or lispstmtp lst or lispstmtgpp lst then
    lst
else if onep length lst and pairp car lst then
    seqtogp car lst
else
    mkstmtgp(nil, for each st in lst collect seqtogp st)$

procedure stringtoatom a;
intern compress
    foreach c in append('!" . explode2 a, list '!")
        conc list('!!, c)$

procedure stripquotes a;
if atom a then
    intern compress
        for each c in explode2 a conc list('!!, c)
else if car a eq 'quote then
    stripquotes cadr a
else
    a$

symbolic procedure flushspaces c;
<< while seprp c do
   <<
     if c eq !$eol!$
         then pterpri()
         else pprin2 c;
     c := readch()
   >>;
   c
   >>;

symbolic procedure flushspacescommas c;
<< while seprp c or c eq '!, do
   <<
     if c eq !$eol!$
         then pterpri()
         else pprin2 c;
     c := readch()
   >>;
   c
   >>;

endmodule;

end;
