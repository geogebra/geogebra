module pre;    %%  GENTRAN Preprocessing Module  %%

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

% Entry Point:  Preproc


symbolic$


procedure preproc exp;
begin
scalar r;
r := preproc1 exp;
if r then
    return car r
else
    return r
end$

% This switch causes gentran to attempt to automatically generate type
% declarations, without use of the 'declare' statement.  mcd 12/11/87.
fluid '(!*getdecs)$
!*getdecs := nil$
switch getdecs$

% This global variable is the default type given when 'getdecs' is on:
global '(deftype!*)$
share deftype!*$
deftype!* := 'real$

% Bfloat defined in arith.red.

% symbolic procedure bfloat x; if floatp x then fl2bf x else
%    normbf(if atom x then read!:num x else x);

symbolic procedure preproc1 exp;
   % Amended mcd 12/11/87,13/11/87,14/10/91.
if atom exp then
    list exp
else if car exp = '!:rd!: then
    list if smallfloatp cdr exp then bfloat cdr exp else exp
else if car exp = '!:dn!: then
    preproc1 decimal2internal(cadr exp,cddr exp)
else if car exp eq '!*sq then
    % (!*SQ dpexp) --> (PREPSQ dpexp) %
    preproc1 prepsq cadr exp
else if car exp eq 'procedure then
<<
    % Store subprogram name & parameters in symbol table %
    symtabput(cadr exp, '!*params!*, car cddddr exp);

    % Store subprogram type and parameters types in symbol table
    % if !*getdecs switch is on.  Use default type unless
    % procedure is declared as either:
    % INTEGER PROCEDURE ...    or    REAL PROCEDURE ...
    if !*getdecs then
            if caddr exp memq '(real integer) then
        <<
                symtabput(cadr exp,cadr exp,list caddr exp);
                for each v in car cddddr exp do
                        symtabput(cadr exp,v,list caddr exp);
                list nconc(list ('procedure,cadr exp,'nil),
                           for each e in cdddr exp conc preproc1 e)
        >>
        else
        <<
                for each v in car cddddr exp do
                        symtabput(cadr exp,v,list deftype!*);
                    list for each e in exp
                             conc preproc1 e
        >>
    else
                 list for each e in exp
                         conc preproc1 e


>>
else if car exp eq 'declare then
<<
    % Store type declarations in symbol table %
    exp := car preproc1 cdr exp;
    exp := preprocdec exp;
    for each dec in exp do
        for each var in cdr dec do
            if car dec memq '(subroutine function) then
                symtabput(var, '!*type!*, car dec)
            else
                symtabput(nil,
                          if atom var then var else car var,
                          if atom var then list car dec
                                      else (car dec . cdr var));
    nil
>>
else if car exp eq 'setq and pairp caddr exp and
  memq(caaddr exp,'(cond progn) ) then
    migrate!-setqs exp
else if memq(car exp, '(plus times difference quotient minus) ) then
begin scalar simp_exp;
  return if pairp numr (simp_exp:=simp!* exp)
            and memq(car numr simp_exp,'(!:cr!: !:crn!: !:gi!:)) then
    if onep denr simp_exp then
      list numr simp_exp
    else
      list list('quotient,numr simp_exp,
                car preproc1 prepsq !*f2q denr simp_exp)
  else
    list for each e in exp conc preproc1 e;
end
else
<<
% The next statement stores the index of a for loop in the symbol
% table, assigning them the type integer,
% if the switch 'getdecs' is on.
        if !*getdecs and (car exp memq '(!~FOR for)) then
                        symtabput(nil,cadr exp, '(integer));
            list for each e in exp
             conc preproc1 e
>>$


symbolic procedure preprocdec arg;
% (TIMES type int) --> type!*int     %
% (IMPLICIT type) --> IMPLICIT! type %
% (DIFFERENCE v1 v2) --> v1!-v2      %
if atom arg then
    arg
else if car arg eq 'times then
  if equal(length arg,3) and fixp(caddr arg) then
    intern
         compress
            append( append( explode cadr arg, explode '!* ),
                    explode caddr arg )
  else
  begin scalar result;
    for i:=1:length(arg) do
        result := append(result,
          if equal(nth(arg,i),'times)
             then '(!*)
             else explode nth(arg,i));
        return intern compress result;
  end
else if car arg eq 'implicit then
    intern
        compress
            append( explode 'implicit! , explode preprocdec cadr arg )
else if car arg eq 'difference then
    intern
        compress
            append( append( explode cadr arg, explode '!- ),
                    explode caddr arg )
else
    for each a in arg collect
        preprocdec a$


symbolic procedure migrate!-setqs exp;
% Move setq's within a progn or cond so that we can translate things
% like gentran x := if ... then ...
list migrate!-setqs1(cadr exp,caddr exp)$

symbolic procedure migrate!-setqs1(var,exp);
if atom exp then
    preproc list('setq,var,exp)
else if eqcar(exp,'cond) then
    ('cond . for each u in cdr exp collect
        list (preproc car u,migrate!-setqs1(var,cadr u)) )
else if eqcar(exp,'progn) then
    reverse rplaca(exp := reverse exp,migrate!-setqs1(var,car exp))
else
    preproc list('setq,var,exp)$


endmodule;

end;
