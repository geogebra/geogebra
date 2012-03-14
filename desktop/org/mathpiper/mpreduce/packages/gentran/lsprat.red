module lsprat;    %%  GENTRAN LISP-to-RATFOR Translation Module  %%

%%  Author:  Barbara L. Gates  %%
%%  December 1986              %%

% Updates:

% M.C. Dewar and J.H. Davenport 8 Jan 88 Double precision check added.

% Entry Point:  RatCode


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



symbolic$

fluid '(!*double !*gendecs !*getdecs);

switch gendecs$

fluid '(!*makecalls)$
switch makecalls$
!*makecalls := t$

% User-Accessible Global Variables %
global '(minratlinelen!* ratlinelen!* !*ratcurrind!*
         ratcurrind!* tablen!*)$
share ratcurrind!*, minratlinelen!*, ratlinelen!*, tablen!*$
ratcurrind!* := 0$
minratlinelen!*  := 40$
ratlinelen!*     := 80$
!*ratcurrind!*  := 0$     %current level of indentation for RATFOR code


global '(deftype!* !*do!* !*notfortranfuns!* !*legalforttypes!*)$

global '(!*stdout!*)$
global '(!*posn!* !$!#)$

%%                                      %%
%% LISP-to-RATFOR Translation Functions %%
%%                                      %%

put('ratfor,'formatter,'formatrat);
put('ratfor,'codegen,'ratcode);
put('ratfor,'proctem,'procrattem);
put('ratfor,'gendecs,'ratdecs);
put('ratfor,'assigner,'mkfratassign);
put('ratfor,'boolean!-type,'logical);

%% Control Function %%


procedure ratcode forms;
for each f in forms conc
    if atom f then
        ratexp f
    else if car f memq '(!:rd!: !:cr!: !:crn!: !:gi!:) then
        ratexp f
    else if lispstmtp f or lispstmtgpp f then
        if !*gendecs then
            begin
            scalar r;
            r := append(ratdecs symtabget('!*main!*, '!*decs!*),
                        ratstmt f);
            symtabrem('!*main!*, '!*decs!*);
            return r
            end
        else
            ratstmt f
    else if lispdefp f then
        ratsubprog f
    else
        ratexp f$


%% Subprogram Translation %%


symbolic procedure ratsubprog deff;
begin
scalar type, stype, name, params, body, lastst, r;
name := cadr deff;
if onep length(body := cdddr deff) and lispstmtgpp car body then
<<  body := cdar body;  if null car body then body := cdr body  >>;
if lispreturnp (lastst := car reverse body) then
    body := append(body, list '(end))
else if not lispendp lastst then
    body := append(body, list('(return), '(end)));
type := cadr symtabget(name, name);
stype := symtabget(name, '!*type!*) or
         (    if type or functionformp(body, name)
                 then 'function
                 else 'subroutine    );
symtabrem(name, '!*type!*);
params := symtabget(name, '!*params!*) or caddr deff;
symtabrem(name, '!*params!*);
if !*getdecs and null type and stype eq 'function
   then type := deftype!*;
if type then
<<  symtabrem(name, name);
    % Generate the correct double precision type name - mcd 28/1/88 %
    if !*double then
        if type memq '(real real!*8) then
                type := 'double! precision
        else if type eq 'complex then
                type := 'complex!*16;
>>;
r := mkfratsubprogdec(type, stype, name, params);
if !*gendecs then
    r := append(r, ratdecs symtabget(name, '!*decs!*));
r := append(r, for each s in body
                   conc ratstmt s);
if !*gendecs then
<<  symtabrem(name, nil);  symtabrem(name, '!*decs!*)  >>;
return r
end$


%% Generation of Declarations %%


procedure ratdecs decs;
for each tl in formtypelists decs
    conc mkfratdec(car tl, cdr tl)$


%% Expression Translation %%


procedure ratexp exp;
ratexp1(exp, 0)$

procedure ratexp1(exp, wtin);
if atom exp then
    list fortranname exp
else
    if onep length exp then
        fortranname exp
    else if optype car exp then
        begin
        scalar wt, op, res;
        wt := ratforprecedence car exp;
        op := ratforop car exp;
        exp := cdr exp;
        if onep length exp then
            res := op . ratexp1(car exp, wt)
        else
        <<
            res := ratexp1(car exp, wt);
            if op eq '!+ then
                while exp := cdr exp do
                <<
                    if atom car exp or caar exp neq 'minus then
                        res := append(res, list op);
                    res := append(res, ratexp1(car exp, wt))
                >>
            else
                while exp := cdr exp do
                    res := append(append(res, list op),
                                  ratexp1(car exp, wt))
        >>;
        if wtin >= wt then res := insertparens res;
        return res
        end
    else if car exp eq 'literal then
        ratliteral exp
    else if car exp eq 'range
        then append(fortexp cadr exp,'!: . fortexp caddr exp)
    else if car exp eq '!:rd!: then
    begin scalar mt;
        integer dotpos,!:lower!-sci!:,!:upper!-sci!:; % this forces most
                                         % numbers to exponential format
        mt := rd!:explode exp;
        exp := car mt;
        mt  := cadr mt + caddr mt - 1;
        exp := append(list('literal,car exp, '!.),cdr exp);
        if null (mt = 0) then
            exp := append(exp, list(if !*double then '!d else '!e,mt))
        else if !*double then
            exp := append(exp,'(!e 0));
        return ratliteral exp;
    end
    else if car exp memq '(!:cr!: !:crn!: !:gi!:) then
    begin scalar re,im;

        re := explode if smallfloatp cadr exp then cadr exp
                                              else caadr exp;
        re := if memq ('!e, re) then
            subst('d,'!e,re)
        else if memq ('!e, re) then
            subst('d,'!e,re)
        else if !*double then
            append(re,'(d 0))
        else
            append(re,'(e 0));

        im := explode if smallfloatp cddr exp then cddr exp
                                              else caddr exp;
        im := if memq ('!e, im) then
            subst('d,'!e,im)
        else if memq ('!e, im) then
            subst('d,'!e,im)
        else if !*double then
            append(im,'(d 0))
        else
            append(im,'(e 0));

        return ('!().append(re,('!,).append(im,'(!))));
    end
    else
        begin
        scalar op, res;
        op := fortranname car exp;
        exp := cdr exp;
        res := ratexp1(car exp, 0);
        while exp := cdr exp do
            res := append(append(res, list '!,), ratexp1(car exp, 0));
        return op . insertparens res
        end$


procedure ratforop op;
get(op, '!*ratforop!*) or op$

put('or,       '!*ratforop!*, '|   )$
put('and,      '!*ratforop!*, '&   )$
put('not,      '!*ratforop!*, '!!  )$
put('equal,    '!*ratforop!*, '!=!=)$
put('neq,      '!*ratforop!*, '!!!=)$
put('greaterp, '!*ratforop!*, '>   )$
put('geq,      '!*ratforop!*, '!>!=)$
put('lessp,    '!*ratforop!*, '<   )$
put('leq,      '!*ratforop!*, '!<!=)$
put('plus,     '!*ratforop!*, '!+  )$
put('times,    '!*ratforop!*, '*   )$
put('quotient, '!*ratforop!*, '/   )$
put('minus,    '!*ratforop!*, '!-  )$
put('expt,     '!*ratforop!*, '!*!*)$

procedure ratforprecedence op;
get(op, '!*ratforprecedence!*) or 9$

put('or,       '!*ratforprecedence!*, 1)$
put('and,      '!*ratforprecedence!*, 2)$
put('not,      '!*ratforprecedence!*, 3)$
put('equal,    '!*ratforprecedence!*, 4)$
put('neq,      '!*ratforprecedence!*, 4)$
put('greaterp, '!*ratforprecedence!*, 4)$
put('geq,      '!*ratforprecedence!*, 4)$
put('lessp,    '!*ratforprecedence!*, 4)$
put('leq,      '!*ratforprecedence!*, 4)$
put('plus,     '!*ratforprecedence!*, 5)$
put('times,    '!*ratforprecedence!*, 6)$
put('quotient, '!*ratforprecedence!*, 6)$
put('minus,    '!*ratforprecedence!*, 7)$
put('expt,     '!*ratforprecedence!*, 8)$


%% Statement Translation %%


procedure ratstmt stmt;
if null stmt then
    nil
else if lisplabelp stmt then
    ratstmtnum stmt
else if car stmt eq 'literal then
    ratliteral stmt
else if lispreadp stmt then
    ratread stmt
else if lispassignp stmt then
    ratassign stmt
else if lispprintp stmt then
    ratwrite stmt
else if lispcondp stmt then
    ratif stmt
else if lispbreakp stmt then
    ratbreak stmt
else if lispgop stmt then
    ratgoto stmt
else if lispreturnp stmt then
    ratreturn stmt
else if lispstopp stmt then
    ratstop stmt
else if lispendp stmt then
    ratend stmt
else if lisprepeatp stmt then
    ratrepeat stmt
else if lispwhilep stmt then
    ratwhile stmt
else if lispforp stmt then
    ratforfor stmt
else if lispstmtgpp stmt then
    ratstmtgp stmt
else if lispdefp stmt then
    ratsubprog stmt
else if lispcallp stmt then
    ratcall stmt$


procedure ratassign stmt;
mkfratassign(cadr stmt, caddr stmt)$

procedure ratbreak stmt;
mkfratbreak()$

procedure ratcall stmt;
mkfratcall(car stmt, cdr stmt)$

procedure ratforfor stmt;
begin
scalar r, var, loexp, stepexp, hiexp, stmtlst;
var := cadr stmt;
stmt := cddr stmt;
loexp := caar stmt;
stepexp := cadar stmt;
hiexp := caddar stmt;
stmtlst := cddr stmt;
r := mkfratdo(var, loexp, hiexp, stepexp);
indentratlevel(+1);
r := append(r, foreach st in stmtlst conc ratstmt st);
indentratlevel(-1);
return r
end$

procedure ratend stmt;
mkfratend()$

procedure ratgoto stmt;
begin
scalar stmtnum;
stmtnum := get(cadr stmt, '!*stmtnum!*) or
          put(cadr stmt, '!*stmtnum!*, genstmtnum());
return mkfratgo stmtnum
end$

procedure ratif stmt;
begin
scalar r, st;
r := mkfratif caadr stmt;
indentratlevel(+1);
st := seqtogp cdadr stmt;
if eqcar(st, 'cond) and length st=2 then
    st := mkstmtgp(0, list st);
r := append(r, ratstmt st);
indentratlevel(-1);
stmt := cdr stmt;
while (stmt := cdr stmt) and caar stmt neq t do
<<
    r := append(r, mkfratelseif caar stmt);
    indentratlevel(+1);
    st := seqtogp cdar stmt;
    if eqcar(st, 'cond) and length st=2 then
        st := mkstmtgp(0, list st);
    r := append(r, ratstmt st);
    indentratlevel(-1)
>>;
if stmt then
<<
    r := append(r, mkfratelse());
    indentratlevel(+1);
    st := seqtogp cdar stmt;
    if eqcar(st, 'cond) and length st=2 then
        st := mkstmtgp(0, list st);
    r := append(r, ratstmt st);
    indentratlevel(-1)
>>;
return r
end$

procedure ratliteral stmt;
mkfratliteral cdr stmt$

procedure ratread stmt;
mkfratread cadr stmt$

procedure ratrepeat stmt;
begin
scalar r, stmtlst, logexp;
stmt := reverse cdr stmt;
logexp := car stmt;
stmtlst := reverse cdr stmt;
r := mkfratrepeat();
indentratlevel(+1);
r := append(r, foreach st in stmtlst conc ratstmt st);
indentratlevel(-1);
return append(r, mkfratuntil logexp)
end$

procedure ratreturn stmt;
if cdr stmt then
    mkfratreturn cadr stmt
else
    mkfratreturn nil$

procedure ratstmtgp stmtgp;
begin
scalar r;
if car stmtgp eq 'progn then
    stmtgp := cdr stmtgp
else
    stmtgp := cddr stmtgp;
r := mkfratbegingp();
indentratlevel(+1);
r := append(r, for each stmt in stmtgp conc ratstmt stmt);
indentratlevel(-1);
return append(r, mkfratendgp())
end$

procedure ratstmtnum label;
begin
scalar stmtnum;
stmtnum := get(label, '!*stmtnum!*) or
          put(label, '!*stmtnum!*, genstmtnum());
return mkfratcontinue stmtnum
end$

procedure ratstop stmt;
mkfratstop()$

procedure ratwhile stmt;
begin
scalar r, logexp, stmtlst;
logexp := cadr stmt;
stmtlst := cddr stmt;
r := mkfratwhile logexp;
indentratlevel(+1);
r := append(r, foreach st in stmtlst conc ratstmt st);
indentratlevel(-1);
return r
end$

procedure ratwrite stmt;
mkfratwrite cdr stmt$


%%                                  %%
%% RATFOR Code Formatting Functions %%
%%                                  %%


%% Statement Formatting %%


% A macro used to prevent things with *fortranname* or *doublename*
% properties being evaluated in certain circumstances.  MCD 28.3.94
symbolic smacro procedure ratexp_name(u);
   if atom u then list(u)
    else rplaca(ratexp ('dummyArrayToken . cdr u), car u)$

procedure mkfratassign(lhs, rhs);
append(append(mkrattab() . ratexp_name lhs, '!= . ratexp rhs),
       list mkratterpri())$

procedure mkfratbegingp;
list(mkrattab(), '!{, mkratterpri())$

procedure mkfratbreak;
list(mkrattab(), 'break, mkratterpri())$

procedure mkfratcall(fname, params);
% Installed the switch makecalls 18/11/88 mcd.
<<
    if params then
        params := append(append(list '!(,
                                for each p in insertcommas params
                                              conc ratexp p),
                         list '!));
    % If we want to generate bits of statements, then what might
    % appear a subroutine call may in fact be a function reference.
    if !*makecalls then
            append(append(list(mkrattab(), 'call, '! ), ratexp fname),
           append(params, list mkratterpri()))
    else
        append(ratexp fname,params)
>>$

procedure mkfratcontinue stmtnum;
list(stmtnum, '! , mkrattab(), 'continue, mkratterpri())$


symbolic procedure mkfratdec(type, varlist); %Ammended mcd 3/12/87
<<
    if type equal 'scalar then type := deftype!*;
    if type and null (type memq !*legalforttypes!*) then
        gentranerr('e,type,"Illegal Ratfor type. ",nil);
    type := type or 'dimension;

    % Generate the correct double precision type name - mcd 14/1/88 %
    if !*double then
        if type memq '(real real!*8) then
                type := 'double! precision
        else if type memq '(implicit! real implicit! real!*8) then
                type := 'implicit! double! precision
        else if type eq 'complex then
                type := 'complex!*16
        else if type eq 'implicit! complex then
                type := 'implicit! complex!*16;

    varlist := for each v in insertcommas varlist
                   conc ratexp_name v;
    if implicitp type then
        append(list(mkrattab(), type, '! , '!(),
               append(varlist, list('!), mkratterpri())))
    else
        append(list(mkrattab(), type, '! ),
               append(varlist, list mkratterpri()))
>>$

procedure mkfratdo(var, lo, hi, incr);
<<
    if onep incr then
        incr := nil
    else if incr then
        incr := '!, . ratexp incr;
    append(append(append(list(mkrattab(), !*do!*, '! ), ratexp var),
                  append('!= . ratexp lo, '!, . ratexp hi)),
           append(incr, list mkratterpri()))
>>$

procedure mkfratelse;
list(mkrattab(), 'else, mkratterpri())$

procedure mkfratelseif exp;
append(append(list(mkrattab(), 'else, '! , 'if, '! , '!(), ratexp exp),
       list('!), mkratterpri()))$

procedure mkfratend;
list(mkrattab(), 'end, mkratterpri())$

procedure mkfratendgp;
list(mkrattab(), '!}, mkratterpri())$

procedure mkfratgo stmtnum;
list(mkrattab(), 'goto, '! , stmtnum, mkratterpri())$

procedure mkfratif exp;
append(append(list(mkrattab(), 'if, '! , '!(), ratexp exp),
       list('!), mkratterpri()))$

procedure mkfratliteral args;
for each a in args conc
    if a eq 'tab!* then
        list mkrattab()
    else if a eq 'cr!* then
        list mkratterpri()
    else if pairp a then
        ratexp a
    else
        list stripquotes a$

procedure mkfratread var;
append(list(mkrattab(), 'read, '!(!*!,!*!), '! ),
       append(ratexp var, list mkratterpri()))$

procedure mkfratrepeat;
list(mkrattab(), 'repeat, mkratterpri())$

procedure mkfratreturn exp;
if exp then
    append(append(list(mkrattab(), 'return, '!(), ratexp exp),
           list('!), mkratterpri()))
else
    list(mkrattab(), 'return, mkratterpri())$

procedure mkfratstop;
list(mkrattab(), 'stop, mkratterpri())$

procedure mkfratsubprogdec(type, stype, name, params);
<<
    if params then
        params := append('!( . for each p in insertcommas params
                                  conc ratexp p,
                         list '!));
    if type then
        type := list(mkrattab(), type, '! , stype, '! )
    else
        type := list(mkrattab(), stype, '! );
    append(append(type, ratexp name),
           append(params,list mkratterpri()))
>>$

procedure mkfratuntil logexp;
append(list(mkrattab(), 'until, '! , '!(),
       append(ratexp logexp, list('!), mkratterpri())))$

procedure mkfratwhile exp;
append(append(list(mkrattab(), 'while, '! , '!(), ratexp exp),
       list('!), mkratterpri()))$

procedure mkfratwrite arglist;
append(append(list(mkrattab(), 'write, '!(!*!,!*!), '! ),
              for each arg in insertcommas arglist conc ratexp arg),
       list mkratterpri())$


%% Indentation Control %%


procedure mkrattab;
list('rattab, ratcurrind!*)$


procedure indentratlevel n;
ratcurrind!* := ratcurrind!* + n * tablen!*$


procedure mkratterpri;
list 'ratterpri$

%% RATFOR Code Formatting & Printing Functions %%


procedure formatrat lst;
begin
scalar linelen,str;
linelen := linelength 300;
!*posn!* := 0;
for each elt in lst do
    if pairp elt then lispeval elt
    else
    <<  str:=explode2 elt;
        if floatp elt then
           if !*double then
              if memq('!e,str)
                 then str:=subst('D,'!e,str)
                 else if memq('E,str)   % Some LISPs use E not e
                      then str:=subst('D,'E,str)
                      else str:=append(str,'(D !0))
           else str:=subst('E,'!e,str);
                % get the casing conventions correct
        if !*posn!* + length str > ratlinelen!* then
            ratcontline();
        for each u in str do pprin2 u
    >>;
linelength linelen
end$

procedure ratcontline;
<<
    ratterpri();
    rattab !*ratcurrind!*;
    pprin2 " "
>>$

procedure ratterpri;
pterpri()$

procedure rattab n;
<<
    !*ratcurrind!* := min0(n, ratlinelen!* - minratlinelen!*);
    if (n := !*ratcurrind!* - !*posn!*) > 0 then pprin2 nspaces n
>>$

%% RATFOR template processing %%


procedure procrattem;
begin
scalar c, linelen;
linelen := linelength 150;
c := readch();
while c neq !$eof!$ do
    if c memq '(!F !f !S !s) then
    <<
        pprin2 c;
        c := procsubprogheading c
    >>
    else if c eq '!# then
        c := procratcomm()
    else if c eq '!; then
        c := procactive()
    else if c eq !$eol!$ then
    <<
        pterpri();
        c := readch()
    >>
    else
    <<
        pprin2 c;
        c := readch()
    >>;
linelength linelen
end$

procedure procratcomm;
% # ... <cr> %
begin
scalar c;
pprin2 '!#;
while (c := readch()) neq !$eol!$ do
    pprin2 c;
pterpri();
return readch()
end$


endmodule;

end;
