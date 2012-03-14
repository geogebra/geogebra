module lspfor;    %%  GENTRAN LISP-to-FORTRAN Translation Module  %%

%%  Author:  Barbara L. Gates  %%
%%  December 1986              %%

% Updates:

% M. Warns 7 Oct 89 Patch in FORTEXP1 for negative constant exponents
%                   and integer arguments of functions like SQRT added.

% M.C. Dewar and J.H. Davenport 8 Jan 88 Double precision etc. added.

% Entry Point:  FortCode

symbolic$


% To allow Fortran-90 Extensions:
fluid '(!*f90)$
switch f90$

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


fluid '(!*gendecs)$
switch gendecs$
fluid '(!*getdecs)$

fluid '(!*makecalls)$
switch makecalls$
!*makecalls := t$

% User-Accessible Global Variables %
global '(gentranlang!* fortlinelen!* minfortlinelen!*
         fortcurrind!* !*fortcurrind!* tablen!*)$
share fortcurrind!*, fortlinelen!*, minfortlinelen!*, tablen!*$
fortcurrind!* := 0$
!*fortcurrind!* := 6$     %current level of indentation for FORTRAN code
fortlinelen!*    := 72$
minfortlinelen!* := 40$

% Double Precision Switch (defaults to OFF) - mcd 13/1/88 %
fluid '(!*double);
% !*double := t;
switch double;


% GENTRAN Global Variables %

global '(!*notfortranfuns!* !*endofloopstack!* !*subprogname!*)$
!*notfortranfuns!*:= '(acosh asinh atanh cot dilog ei erf sec)$
                        %mcd 10/11/87
!*endofloopstack!* := nil$
!*subprogname!*    := nil$  %name of subprogram being generated

global '(!*do!* deftype!*)$

% The following ought to be all the legal Fortran types mcd 19/11/87.
global '(!*legalforttypes!*);
!*legalforttypes!* := '(real integer complex real!*8 complex!*16 logical
                        implicit! integer implicit! real
                        implicit! complex implicit! real!*8
                        implicit! complex!*16 implicit! logical)$

global '(!*stdout!*)$
global '(!*posn!* !$!#);
%%                                       %%
%% LISP-to-FORTRAN Translation Functions %%
%%                                       %%

put('fortran,'formatter,'formatfort);
put('fortran,'codegen,'fortcode);
put('fortran,'proctem,'procforttem);
put('fortran,'gendecs,'fortdecs);
put('fortran,'assigner,'mkffortassign);
put('fortran,'boolean!-type,'logical);

%% Control Function %%


symbolic procedure fortcode forms;
for each f in forms conc
    if atom f then
        fortexp f
    else if car f memq '(!:rd!: !:cr!: !:crn!: !:gi!:) then
        fortexp f
    else if lispstmtp f or lispstmtgpp f then
        if !*gendecs then
            begin
            scalar r;
            r := append(fortdecs symtabget('!*main!*, '!*decs!*),
                        fortstmt f);
            symtabrem('!*main!*, '!*decs!*);
            return r
            end
        else
            fortstmt f
    else if lispdefp f then
        fortsubprog f
    else
        fortexp f$


%% Subprogram Translation %%


symbolic procedure fortsubprog deff;
begin
scalar type, stype, name, params, body, lastst, r;
name := !*subprogname!* := cadr deff;
if onep length (body := cdddr deff) and lispstmtgpp car body then
<<  body := cdar body;  if null car body then body := cdr body  >>;
if lispreturnp (lastst := car reverse body) then
    body := append(body, list '(end))
else if not lispendp lastst then
    body := append(body, list('(return), '(end)));
type :=  symtabget(name, name);
if type then type := cadr type;
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
r := mkffortsubprogdec(type, stype, name, params);
if !*gendecs then
    r := append(r, fortdecs symtabget(name, '!*decs!*));
r := append(r, for each s in body
                   conc fortstmt s);
if !*gendecs then
<<  symtabrem(name, nil);  symtabrem(name, '!*decs!*)  >>;
return r
end$


%% Generation of Declarations %%


symbolic procedure fortdecs decs;
for each tl in formtypelists decs
    conc mkffortdec(car tl, cdr tl)$


%% Expression Translation %%


procedure fortexp exp;
fortexp1(exp, 0)$

symbolic procedure fortexp1(exp, wtin);
if atom exp then
    list fortranname exp
else
    if listp exp and onep length exp then
        fortranname exp
    else if optype car exp then
        begin
        scalar wt, op, res;
        wt := fortranprecedence car exp;
        op := fortranop car exp;
        exp := cdr exp;
        if onep length exp then
            res := op . fortexp1(car exp, wt)
        else
        <<
            res := fortexp1(car exp, wt);
            if op eq '!+ then
                while exp := cdr exp do
                <<
                    if atom car exp or caar exp neq 'minus then
                        res := append(res, list op);
                    res := append(res, fortexp1(car exp, wt))
                >>
            else if op eq '!*!*  then
                while exp := cdr exp do
                  begin
                   if numberp car exp and lessp(car exp, 0) then
                    res := append(append(res, list op),
                   insertparens fortexp1(car exp, wt))
                   else
                    res := append(append(res, list op),
                                  fortexp1(car exp, wt))
                  end
            else
                while exp := cdr exp do
                    res := append(append(res, list op),
                                  fortexp1(car exp, wt))
        >>;
        if wtin >= wt then res := insertparens res;
        return res
        end
    else if car exp eq 'literal then
        fortliteral exp
    else if car exp eq 'range
        then append(fortexp cadr exp,'!: . fortexp caddr exp)
    else if car exp eq '!:rd!: then
        if smallfloatp cdr exp then
            list cdr exp
        else
        begin scalar mt; % Print bigfloats more naturally. MCD 26/2/90
            integer dotpos,!:lower!-sci!:,!:upper!-sci!:;
                    % This forces most numbers to exponential format.
            mt := rd!:explode exp;
            exp := car mt;
            mt  := cadr mt + caddr mt - 1;
            exp := append(list('literal,car exp, '!.),cdr exp);
            if null (mt = 0) then
                exp := append(exp,
                              list(if !*double then '!D else '!E,mt))
            else if !*double then
                exp := append(exp,'(!D 0));

            return fortliteral exp;
        end
    else if car exp eq '!:crn!: then
        fortexp1(!*crn2cr exp,wtin)
    else if car exp eq '!:gi!: then
        fortexp1(!*gi2cr exp,wtin)
    else if car exp eq '!:cr!: then
        if !*double and !*f90 then
            ('CMPLX!().append(fortexp1(cons('!:rd!:,cadr exp),wtin),
              ('!,).append(fortexp1(cons('!:rd!:,cddr exp),wtin),
              list( '!, , 'KIND!(!1!.!0!D!0!) , '!) ))
            )
        else
            ('CMPLX!().append(fortexp1(cons('!:rd!:,cadr exp),wtin),
              ('!,).append(fortexp1(cons('!:rd!:,cddr exp),wtin),
                           list '!)))
               % We must make this list up at run time, since there's
               % a CONC loop that relies on being able to RPLAC into it.
               % Yuck. JHD/MCD 19.6.89
    else
        begin scalar op, res, intrinsic;
        intrinsic := get(car exp, '!*fortranname!*);
        op := fortranname car exp;
        exp := cdr exp;
        % Make the arguments of intrinsic functions real if we aren't
        % sure.  Note that we can't simply evaluate the argument and
        % test that, unless it is a constant.  MCD 7/11/89.
        res := cdr foreach u in exp conc
            '!, . if not intrinsic then
              fortexp1(u,0)
            else if fixp u then
              list float u
            else if isfloat u or memq(op,'(real dble)) then
              fortexp1(u,0)
            else
              (fortranname 'real . insertparens fortexp1(u,0));
        return op . insertparens res
        end;


symbolic procedure isfloat u;
   % Returns T if u is a float or a list whose car is an intrinsic
   % function name.  MCD 7/11/89.
   floatp(u) or (idp u and declared!-as!-float(u) ) or
   pairp(u) and (car u eq '!:rd!: or
                 get(car u,'!*fortranname!*) or
                 declared!-as!-float(car u) );


procedure fortranop op;
get(op, '!*fortranop!*) or op$

put('or,       '!*fortranop!*, '!.or!. )$
put('and,      '!*fortranop!*, '!.and!.)$
put('not,      '!*fortranop!*, '!.not!.)$
put('equal,    '!*fortranop!*, '!.eq!. )$
put('neq,      '!*fortranop!*, '!.ne!. )$
put('greaterp, '!*fortranop!*, '!.gt!. )$
put('geq,      '!*fortranop!*, '!.ge!. )$
put('lessp,    '!*fortranop!*, '!.lt!. )$
put('leq,      '!*fortranop!*, '!.le!. )$
put('plus,     '!*fortranop!*, '!+     )$
put('times,    '!*fortranop!*, '!*     )$
put('quotient, '!*fortranop!*, '/      )$
put('minus,    '!*fortranop!*, '!-     )$
put('expt,     '!*fortranop!*, '!*!*   )$

% This procedure (and FORTRANNAME, RATFORNAME properties, and
% the DOUBLE flag) are shared between FORTRAN and RATFOR
procedure fortranname a; % Amended mcd 10/11/87
if stringp a then
            stringtoatom a    %  convert a to atom containing "'s
else
<<      if a memq !*notfortranfuns!* then
        <<      wrs cdr !*stdout!*;
                prin2 "*** WARNING: ";
                prin1 a;
                prin2t " is not an intrinsic Fortran function";
        >>$

        if !*double then
                get(a, '!*doublename!*) or a
        else
                    get(a, '!*fortranname!*) or a
>>$

put('true,   '!*fortranname!*, '!.true!. )$
put('false, '!*fortranname!*, '!.false!.)$

%% mcd 10/11/87
%% Reduce functions' equivalent Fortran 77 real function names

put('abs,'!*fortranname!*, 'abs)$
put('sqrt,'!*fortranname!*, 'sqrt)$
put('exp,'!*fortranname!*, 'exp)$
put('log,'!*fortranname!*, 'alog)$
put('ln,'!*fortranname!*, 'alog)$
put('sin,'!*fortranname!*, 'sin)$
put('cos,'!*fortranname!*, 'cos)$
put('tan,'!*fortranname!*, 'tan)$
put('acos,'!*fortranname!*, 'acos)$
put('asin,'!*fortranname!*, 'asin)$
put('atan,'!*fortranname!*, 'atan)$
put('sinh,'!*fortranname!*, 'sinh)$
put('cosh,'!*fortranname!*, 'cosh)$
put('tanh,'!*fortranname!*, 'tanh)$
put('real,'!*fortranname!*, 'real)$
put('max,'!*fortranname!*, 'amax1)$
put('min,'!*fortranname!*, 'amin1)$

%% Reduce function's equivalent Fortran 77 double-precision names

put('abs,'!*doublename!*, 'dabs)$
put('sqrt,'!*doublename!*, 'dsqrt)$
put('exp,'!*doublename!*, 'dexp)$
put('log,'!*doublename!*, 'dlog)$
put('ln,'!*doublename!*, 'dlog)$
put('sin,'!*doublename!*, 'dsin)$
put('cos,'!*doublename!*, 'dcos)$
put('tan,'!*doublename!*, 'dtan)$
put('acos,'!*doublename!*, 'dacos)$
put('asin,'!*doublename!*, 'dasin)$
put('atan,'!*doublename!*, 'datan)$
put('sinh,'!*doublename!*, 'dsinh)$
put('cosh,'!*doublename!*, 'dcosh)$
put('tanh,'!*doublename!*, 'dtanh)$
put('true,    '!*doublename!*, '!.true!. )$
put('false,  '!*doublename!*, '!.false!.)$
put('real,'!*doublename!*, 'dble)$
put('max,' !*doublename!*, 'dmax1)$
put('min, '!*doublename!*, 'dmin1)$

%% end of mcd


procedure fortranprecedence op;
get(op, '!*fortranprecedence!*) or 9$

put('or,       '!*fortranprecedence!*, 1)$
put('and,      '!*fortranprecedence!*, 2)$
put('not,      '!*fortranprecedence!*, 3)$
put('equal,    '!*fortranprecedence!*, 4)$
put('neq,      '!*fortranprecedence!*, 4)$
put('greaterp, '!*fortranprecedence!*, 4)$
put('geq,      '!*fortranprecedence!*, 4)$
put('lessp,    '!*fortranprecedence!*, 4)$
put('leq,      '!*fortranprecedence!*, 4)$
put('plus,     '!*fortranprecedence!*, 5)$
put('times,    '!*fortranprecedence!*, 6)$
put('quotient, '!*fortranprecedence!*, 6)$
put('minus,    '!*fortranprecedence!*, 7)$
put('expt,     '!*fortranprecedence!*, 8)$


%% Statement Translation %%


procedure fortstmt stmt;
if null stmt then
    nil
else if lisplabelp stmt then
    fortstmtnum stmt
else if car stmt eq 'literal then
    fortliteral stmt
else if lispreadp stmt then
    fortread stmt
else if lispassignp stmt then
    fortassign stmt
else if lispprintp stmt then
    fortwrite stmt
else if lispcondp stmt then
    fortif stmt
else if lispbreakp stmt then
    fortbreak stmt
else if lispgop stmt then
    fortgoto stmt
else if lispreturnp stmt then
    fortreturn stmt
else if lispstopp stmt then
    fortstop stmt
else if lispendp stmt then
    fortend stmt
else if lispwhilep stmt then
    fortwhile stmt
else if lisprepeatp stmt then
    fortrepeat stmt
else if lispforp stmt then
    fortfor stmt
else if lispstmtgpp stmt then
    fortstmtgp stmt
else if lispdefp stmt then
    fortsubprog stmt
else if lispcallp stmt then
    fortcall stmt$


procedure fortassign stmt;
mkffortassign(cadr stmt, caddr stmt)$

procedure fortbreak stmt;
if null !*endofloopstack!* then
    gentranerr('e, nil, "BREAK NOT INSIDE LOOP - CANNOT BE TRANSLATED",
               nil)
else if atom car !*endofloopstack!* then
    begin
    scalar n1;
    n1 := genstmtnum();
    rplaca(!*endofloopstack!*, list(car !*endofloopstack!*, n1));
    return mkffortgo n1
    end
else
    mkffortgo cadar !*endofloopstack!*$

procedure fortcall stmt;
mkffortcall(car stmt, cdr stmt)$

procedure fortfor stmt;
begin
scalar n1, result, var, loexp, stepexp, hiexp, stmtlst;
var := cadr stmt;
stmt := cddr stmt;
loexp := caar stmt;
stepexp := cadar stmt;
hiexp := caddar stmt;
stmtlst := cddr stmt;
n1 := genstmtnum();
!*endofloopstack!* := n1 . !*endofloopstack!*;
result := mkffortdo(n1, var, loexp, hiexp, stepexp);
indentfortlevel(+1);
result := append(result, for each st in stmtlst conc fortstmt st);
indentfortlevel(-1);
result := append(result, mkffortcontinue n1);
if pairp car !*endofloopstack!* then
    result := append(result, mkffortcontinue cadar !*endofloopstack!*);
!*endofloopstack!* := cdr !*endofloopstack!*;
return result
end$

procedure fortend stmt;
mkffortend()$

procedure fortgoto stmt;
begin
scalar stmtnum;
if not ( stmtnum := get(cadr stmt, '!*stmtnum!*) ) then
    stmtnum := put(cadr stmt, '!*stmtnum!*, genstmtnum());
return mkffortgo stmtnum
end$

symbolic procedure fortif stmt;
begin scalar r, st;
    r := mkffortif caadr stmt;
    indentfortlevel(+1);
    st := seqtogp cdadr stmt;
    if eqcar(st, 'cond) and length st=2 then
        st := mkstmtgp(0, list st);
    r := append(r, fortstmt st);
    indentfortlevel(-1);
    stmt := cdr stmt;
    while (stmt := cdr stmt) and caar stmt neq t do
    <<
        r := append(r, mkffortelseif caar stmt);
        indentfortlevel(+1);
        st := seqtogp cdar stmt;
        if eqcar(st, 'cond) and length st=2 then
            st := mkstmtgp(0, list st);
        r := append(r, fortstmt st);
        indentfortlevel(-1)
    >>;
    if stmt then
    <<
        r := append(r, mkffortelse());
        indentfortlevel(+1);
        st := seqtogp cdar stmt;
        if eqcar(st, 'cond) and length st=2 then
            st := mkstmtgp(0, list st);
        r := append(r, fortstmt st);
        indentfortlevel(-1)
    >>;
    return append(r,mkffortendif());
end$


symbolic procedure mkffortif exp;
append(append(list(mkforttab(), 'if, '! , '!(), fortexp exp),
              list('!),'! , 'then , mkfortterpri()))$

symbolic procedure mkffortelseif exp;
append(append(list(mkforttab(), 'else, '! , 'if, '! , '!(),
              fortexp exp),
       list('!), 'then,  mkcterpri()))$

symbolic procedure mkffortelse();
list(mkforttab(), 'else, mkfortterpri())$

symbolic procedure mkffortendif();
list(mkforttab(), 'endif, mkfortterpri())$

procedure fortliteral stmt;
mkffortliteral cdr stmt$

procedure fortread stmt;
mkffortread cadr stmt$

procedure fortrepeat stmt;
begin
scalar n, result, stmtlst, logexp;
stmtlst := reverse cdr stmt;
logexp := car stmtlst;
stmtlst := reverse cdr stmtlst;
n := genstmtnum();
!*endofloopstack!* := 'dummy . !*endofloopstack!*;
result := mkffortcontinue n;
indentfortlevel(+1);
result := append(result, for each st in stmtlst conc fortstmt st);
indentfortlevel(-1);
result := append(result, mkffortifgo(list('not, logexp), n));
if pairp car !*endofloopstack!* then
    result := append(result, mkffortcontinue cadar !*endofloopstack!*);
!*endofloopstack!* := cdr !*endofloopstack!*;
return result
end$

procedure fortreturn stmt;
if onep length stmt then
    mkffortreturn()
else if !*subprogname!* then
    append(mkffortassign(!*subprogname!*, cadr stmt), mkffortreturn())
else
    gentranerr('e, nil,
               "RETURN NOT INSIDE FUNCTION - CANNOT BE TRANSLATED",
               nil)$

procedure fortstmtgp stmtgp;
<<
    if car stmtgp eq 'progn then
        stmtgp := cdr stmtgp
    else
        stmtgp := cddr stmtgp;
    for each stmt in stmtgp conc fortstmt stmt
>>$

procedure fortstmtnum label;
begin
scalar stmtnum;
if not ( stmtnum := get(label, '!*stmtnum!*) ) then
    stmtnum := put(label, '!*stmtnum!*, genstmtnum());
return mkffortcontinue stmtnum
end$

procedure fortstop stmt;
mkffortstop()$

procedure fortwhile stmt;
begin
scalar n1, n2, result, logexp, stmtlst;
logexp := cadr stmt;
stmtlst := cddr stmt;
n1 := genstmtnum();
n2 := genstmtnum();
!*endofloopstack!* := n2 . !*endofloopstack!*;
result := append(list(n1, '! ), mkffortifgo(list('not, logexp), n2));
indentfortlevel(+1);
result := append(result, for each st in stmtlst conc fortstmt st);
result := append(result, mkffortgo n1);
indentfortlevel(-1);
result := append(result, mkffortcontinue n2);
if pairp car !*endofloopstack!* then
    result := append(result, mkffortcontinue cadar !*endofloopstack!*);
!*endofloopstack!* := cdr !*endofloopstack!*;
return result
end$

procedure fortwrite stmt;
mkffortwrite cdr stmt$


%%                                   %%
%% FORTRAN Code Formatting Functions %%
%%                                   %%


%% Statement Formatting %%


% A macro used to prevent things with *fortranname* or *doublename*
% properties being evaluated in certain circumstances.  MCD 28.3.94
symbolic smacro procedure fortexp_name(u);
   if atom u then list(u)
    else rplaca(fortexp ('dummyArrayToken . cdr u), car u)$

symbolic procedure mkffortassign(lhs, rhs);
append(append(mkforttab() . fortexp_name lhs, '!= . fortexp rhs),
       list mkfortterpri())$

symbolic procedure mkffortcall(fname, params);
% Installed the switch makecalls 18/11/88 mcd.
<<
    if params then
        params := append(append(list '!(,
                                for each p in insertcommas params
                                              conc fortexp p),
                         list '!));
    % If we want to generate bits of statements, then what might
    % appear a subroutine call may in fact be a function reference.
    if !*makecalls then
            append(append(list(mkforttab(), 'call, '! ), fortexp fname),
           append(params, list mkfortterpri()))
    else
        append(fortexp fname,params)
>>$

procedure mkffortcontinue stmtnum;
list(stmtnum, '! , mkforttab(), 'continue, mkfortterpri())$

symbolic procedure mkffortdec(type, varlist); %Ammended mcd 13/11/87
<<
    if type equal 'scalar then type := deftype!*;
    if type and null (type memq !*legalforttypes!*) then
        gentranerr('e,type,"Illegal Fortran type. ",nil);
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
                   conc fortexp_name v;
    if implicitp type then
        append(list(mkforttab(), type, '! , '!(),
               append(varlist, list('!), mkfortterpri())))
    else
        append(list(mkforttab(), type, '! ),
               append(varlist,list mkfortterpri()))
>>$

procedure mkffortdo(stmtnum, var, lo, hi, incr);
<<
    if onep incr then
        incr := nil
    else if incr then
        incr := '!, . fortexp incr;
    append(append(append(list(mkforttab(), !*do!*, '! , stmtnum, '! ),
                         fortexp var),
                  append('!= . fortexp lo, '!, . fortexp hi)),
           append(incr, list mkfortterpri()))
>>$

procedure mkffortend;
list(mkforttab(), 'end, mkfortterpri())$

procedure mkffortgo stmtnum;
list(mkforttab(), 'goto, '! , stmtnum, mkfortterpri())$

procedure mkffortifgo(exp, stmtnum);
append(append(list(mkforttab(), 'if, '! , '!(), fortexp exp),
       list('!), '! , 'goto, '! , stmtnum, mkfortterpri()))$

symbolic procedure mkffortliteral args;
   begin scalar !*lower;
      return for each a in args conc
         if a eq 'tab!* then list mkforttab()
          else if a eq 'cr!* then list mkfortterpri()
          else if pairp a then fortexp a
          else list stripquotes a
   end$

procedure mkffortread var;
append(list(mkforttab(), 'read, '!(!*!,!*!), '! ),
       append(fortexp var, list mkfortterpri()))$

procedure mkffortreturn;
list(mkforttab(), 'return, mkfortterpri())$

procedure mkffortstop;
list(mkforttab(), 'stop, mkfortterpri())$

procedure mkffortsubprogdec(type, stype, name, params);
<<
    if params then
        params := append('!( . for each p in insertcommas params
                                   conc fortexp p,
                          list '!));
    if type then
        type := list(mkforttab(), type, '! , stype, '! )
    else
        type := list(mkforttab(), stype, '! );
    append(append(type, fortexp name),
           append(params, list mkfortterpri()))
>>$

procedure mkffortwrite arglist;
append(append(list(mkforttab(), 'write, '!(!*!,!*!), '! ),
              for each arg in insertcommas arglist conc fortexp arg),
       list mkfortterpri())$


%% Indentation Control %%


procedure mkforttab;
list('forttab, fortcurrind!* + 6)$


procedure indentfortlevel n;
fortcurrind!* := fortcurrind!* + n * tablen!*$


procedure mkfortterpri;
list 'fortterpri$

%% FORTRAN Code Formatting & Printing Functions %%

fluid '(maxint);

maxint := 2**31-1;

symbolic procedure formatfort lst;
begin scalar linelen,str,!*lower;
linelen := linelength 300;
!*posn!* := 0;
for each elt in lst do
    if pairp elt then lispeval elt
    else
    <<
        if fixp elt and (elt>maxint or elt<-maxint) then
           elt := cdr i2rd!* elt;
        str:=explode2 elt;
        if floatp elt then
           if !*double then
              if memq('!e,str)
                 then str:=subst('!D,'!e,str)
                 else if memq('!E,str) % some LISPs use E not e
                      then str:=subst('!D,'!E,str)
                      else str:=append(str,'(D !0))
           else if memq('!e,str) then
                   str:=subst('!E,'!e,str);
           % get the casing conventions correct
        if !*posn!* + length str > fortlinelen!* then
            fortcontline();
        for each u in str do pprin2 u
    >>;
linelength linelen
end$

procedure fortcontline;
<<
    fortterpri();
    pprin2 "     .";
    forttab !*fortcurrind!*;
    pprin2 " "
>>$

procedure fortterpri;
pterpri()$

procedure forttab n;
<<
    !*fortcurrind!* := max(min0(n, fortlinelen!* - minfortlinelen!*),6);
    if (n := !*fortcurrind!* - !*posn!*) > 0 then pprin2 nspaces n
>>$



%% FORTRAN Template routines%%


symbolic procedure procforttem;
   begin scalar c, linelen, !*lower;
      linelen := linelength 150;
      c := procfortcomm();
      while c neq !$eof!$ do
         if c memq '(!F !f !S !s)
           then <<pprin2 c; c := procsubprogheading c>>
          else if c eq !$eol!$
           then <<pterpri(); c := procfortcomm()>>
         else if c eq '!; then c := procactive()
         else <<pprin2 c; c := readch()>>;
      linelength linelen
   end$

procedure procfortcomm;
% <col 1>C ... <cr> %
% <col 1>c ... <cr> %
begin
scalar c;
while (c := readch()) memq '(!C !c) do
<<
    pprin2 c;
    repeat
        if (c := readch()) neq !$eol!$ then
           pprin2 c
    until c eq !$eol!$;
    pterpri()
>>;
return c
end$



%% This function is shared between FORTRAN and RATFOR %%

procedure procsubprogheading c;
% Altered to allow an active statement to be included in a subprogram
% heading.  This is more flexible than forbidding it as in the previous
% version, although it does mean that where such a statement occurs the
% value of !$!# may be incorrect.   MCD 21/11/90
begin
scalar lst, name, i, propname;
lst := if c memq '(!F !f)
          then '((!U !u) (!N !n) (!C !c) (!T !t) (!I !i) (!O !o)
                 (!N !n))
          else '((!U !u) (!B !b) (!R !r) (!O !o) (!U !u)
                 (!T !t) (!I !i) (!N !n) (!E !e));
while lst and (c := readch()) memq car lst do
<<  pprin2 c;  lst := cdr lst  >>;
if lst then return c;
c:=flushspaces readch();
while not(seprp c or c eq '!() do
<<  name := aconc(name, c);  pprin2 c;  c := readch()  >>;
name := intern compress name;
if not !*gendecs then
    symtabput(name, nil, nil);
propname := if gentranlang!* eq 'fortran
               then '!*fortranname!*
               else '!*ratforname!*;
put('!$0, propname, name);
c:=flushspaces c;
if c neq '!( then return c;
i := 1;
pprin2 c;
c := readch();
while c neq '!) and c neq '!; do
<<
    while c neq '!; and (seprp c or c eq '!,) do
    <<
        if c eq !$eol!$
            then pterpri()
            else pprin2 c;
        c := readch()
    >>;
    if c neq '!; then
    <<
        name := list c;
        pprin2 c;
        while not (seprp (c := readch())
              or c memq list('!,,'!;, '!))) do
        <<  name := aconc(name, c);  pprin2 c  >>;
        put(intern compress append(explode2 '!$, explode2 i),
            propname,
            intern compress name);
        i := add1 i;
        c:=flushspaces c;
    >>;
>>;
!$!# := sub1 i;
while get(name := intern compress append(explode2 '!$, explode2 i),
          propname) do
    remprop(name, propname);
return c
end$

endmodule;


end;
