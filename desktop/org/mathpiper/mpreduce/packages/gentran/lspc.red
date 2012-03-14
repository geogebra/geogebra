module lspc;    %%  GENTRAN LISP-to-C Translation Module  %%

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

% Entry Point:  CCode


symbolic$

fluid '(!*double !*gendecs)$

switch gendecs$

% User-Accessible Global Variables %
global '(clinelen!* minclinelen!* !*ccurrind!* ccurrind!* tablen!*)$
share clinelen!*, minclinelen!*, ccurrind!*, tablen!*$
ccurrind!* := 0$
clinelen!*       := 80$
minclinelen!*    := 40$
!*ccurrind!*    := 0$     %current level of indentation for C code

global '(deftype!* !*c!-functions!*)$
global '(!*posn!* !$!#);

!*c!-functions!* := '(sin cos tan asin acos atan atan2 sinh cosh tanh
                     asinh acosh atanh sincos sinpi cospi tanpi asinpi
                     acospi atanpi exp expm1 exp2 exp10 log log1p log2
                     log10 pow compound annuity abs fabs fmod sqrt
                     cbrt)$

flag( '(abs),'!*int!-args!*)$ % Intrinsic function with integer arg.
%%                                 %%
%% LISP-to-C Translation Functions %%
%%                                 %%


put('c,'formatter,'formatc);
put('c,'codegen,'ccode);
put('c,'proctem,'procctem);
put('c,'gendecs,'cdecs);
put('c,'assigner,'mkfcassign);
put('c,'boolean!-type,'!i!n!t);

%% Control Function %%


symbolic procedure ccode forms;
for each f in forms conc
    if atom f then
        cexp f
    else if car f memq '(!:rd!: !:cr!: !:crn!: !:gi!:) then
        cexp f
    else if lispstmtp f or lispstmtgpp f then
        if !*gendecs then
            begin
            scalar r;
            r := append(cdecs symtabget('!*main!*, '!*decs!*),
                        cstmt f);
            symtabrem('!*main!*, '!*decs!*);
            return r
            end
        else
            cstmt f
    else if lispdefp f then
        cproc f
    else
        cexp f$


%% Procedure Translation %%


symbolic procedure cproc deff; % Type details amended mcd 3/3/88
begin
scalar type, name, params, paramtypes, vartypes, body, r;
name := cadr deff;
if onep length (body := cdddr deff) and lispstmtgpp car body then
<<  body := cdar body;  if null car body then body := cdr body  >>;
if (type := symtabget(name, name)) then
<<      type := cadr type;

        % Convert reduce types to c types

        if type equal 'real then
                type := '!f!l!o!a!t
        else if type equal 'integer then
                type := '!i!n!t;

        if !*double then
                if type equal '!f!l!o!a!t then
                        type := '!d!o!u!b!l!e
                else if type equal '!i!n!t then
                        type := '!l!o!n!g;

        symtabrem(name, name)
>>;
params := symtabget(name, '!*params!*) or caddr deff;
symtabrem(name, '!*params!*);
for each dec in symtabget(name, '!*decs!*) do
    if car dec memq params
       then paramtypes := append(paramtypes, list dec)
       else vartypes := append(vartypes, list dec);
r := append( append( mkfcprocdec(type, name, params),
                     cdecs paramtypes ),
             mkfcbegingp() );
indentclevel(+1);
if !*gendecs then
    r := append(r, cdecs vartypes);
r := append(r, for each s in body
                   conc cstmt s);
indentclevel(-1);
r := append(r, mkfcendgp());
if !*gendecs then
<<  symtabrem(name, nil);  symtabrem(name, '!*decs!*)  >>;
return r
end$


%% Generation of Declarations %%


symbolic procedure cdecs decs;
for each tl in formtypelists decs
    conc mkfcdec(car tl, cdr tl)$


%% Expression Translation %%


symbolic procedure cexp exp;
cexp1(exp, 0)$

symbolic procedure cexp1(exp, wtin);
if atom exp then
    list cname exp
else
    if onep length exp then
        append(cname exp, insertparens(()))
    else if car exp eq 'expt then
      if caddr exp = 2 then
        cexp1 (list('times, cadr exp, cadr exp), wtin)
      else if caddr exp = 3 then
        cexp1 (list('times, cadr exp, cadr exp, cadr exp), wtin)
      else if caddr exp = 4  then
        cexp1(list('times,cadr exp,cadr exp,cadr exp,cadr exp),wtin)
      else if caddr exp = '(quotient 1 2) then
        cexp1 (list('sqrt, cadr exp), wtin)
      else
        cexp1 ('pow . cdr exp,wtin)
    else if optype car exp then
        begin
        scalar wt, op, res;
        wt := cprecedence car exp;
        op := cop car exp;
        exp := cdr exp;
        if onep length exp then
            res := op . cexp1(car exp, wt)
        else
        <<
            res := cexp1(car exp, wt);
            if op eq '!+ then
                while exp := cdr exp do
                <<
                    if atom car exp or caar exp neq 'minus then
                        res := append(res, list op);
                    res := append(res, cexp1(car exp, wt))
                >>
            else
                while exp := cdr exp do
                    res := append(append(res, list op),
                                  cexp1(car exp, wt))
        >>;
        if wtin >= wt then res := insertparens res;
        return res
        end
    else if car exp eq 'literal then
        cliteral exp
    else if car exp eq 'range then
        if cadr exp = 0 then cexp caddr exp
           else gentranerr('e,exp,
                   "C does not support non-zero lower bounds",nil)
    else if car exp eq '!:rd!: then
        if smallfloatp cdr exp then
            list cdr exp
        else
        begin scalar mt; % Print bigfloats more naturally.
            integer dotpos,!:lower!-sci!:,!:upper!-sci!:;
                    % This forces most numbers to exponential format.
            mt := rd!:explode exp;
            exp := car mt;
            mt  := cadr mt + caddr mt - 1;

            exp := append(list('literal,car exp, '!.),cdr exp);
            if null (mt = 0) then
                exp := append(exp,
                              list('!e,mt));
            return cliteral exp;
        end
else if car exp memq '(!:cr!: !:crn!: !:gi!:) then
        gentranerr('e,exp,"C doesn't support complex data type",nil)
    else if arrayeltp exp then
        cname car exp . foreach s in cdr exp conc
                                insertbrackets cexp1(s, 0)
    else if memq(car exp,!*c!-functions!*) then
        begin scalar op,res,dblp;
        dblp := not get(car exp,'!*int!-args!*);
        op := cname car exp;
        res := '!( . list op ;
        while exp := cdr exp do
        <<
           op := cexp1(car exp, 0);
           if dblp and not
                (is!-c!-float(op) or is!-c!-float(car exp)) then
             op := if fixp car op then
                 (float car op) . (cdr op)
             else
                 append(list('!(,'!d!o!u!b!l!e,'!),'!(),
                        append(op,list '!)));
           res := if cdr exp then
             append('!, . reversip op,res)
           else
             append(reversip op,res);
        >>;
        return reversip ( '!) . res )
        end
    else if cfunctcallp exp then
        begin
        scalar op, res;
        op := cname car exp;
        exp := cdr exp;
        res := '!( . cexp1(car exp, 0);
        while exp := cdr exp do
            res := append(res, '!, . cexp1(car exp, 0));
        return op . append(res, list('!)) )
        end
    else
        begin
        scalar op, res;
        op := cname car exp;
        exp := cdr exp;
        res := append( '![ . cexp1(car exp, 0),list('!]) );
        % Changed to generate proper C arrays - mcd 25/9/89
        while exp := cdr exp do
            res := append(res, append('![ . cexp1(car exp, 0)
                                      ,list('!]) ) );
        return op . res
        end$

symbolic procedure string2id str;
intern compress reversip cdr reversip cdr explode str$

symbolic procedure is!-c!-float u;
   % Returns T if u is a float or a list whose car is an intrinsic
   % function name with a floating point result.
   floatp(u) or (idp u and declared!-as!-float(u) ) or
   pairp(u) and (car u eq '!:rd!: or
      stringp car u and memq(string2id car u,!*c!-functions!*) and
          not flagp(string2id car u, '!*int!-args!*) or
      declared!-as!-float(car u) )$


symbolic procedure cfunctcallp exp;
symtabget(car exp,'!*type!*)$


symbolic procedure cop op;
get(op, '!*cop!*) or op$

put('or,       '!*cop!*, '!|!|)$
put('and,      '!*cop!*, '!&!&)$
put('not,      '!*cop!*, '!!  )$
put('equal,    '!*cop!*, '!=!=)$
put('neq,      '!*cop!*, '!!!=)$
put('greaterp, '!*cop!*, '>   )$
put('geq,      '!*cop!*, '!>!=)$
put('lessp,    '!*cop!*, '<   )$
put('leq,      '!*cop!*, '!<!=)$
put('plus,     '!*cop!*, '!+  )$
put('times,    '!*cop!*, '*   )$
put('quotient, '!*cop!*, '/   )$
put('minus,    '!*cop!*, '!-  )$

symbolic procedure cname a;
if stringp a then
    stringtoatom a    % convert a to atom containing "'s
else if memq(a,!*c!-functions!*) then
    string!-downcase a
else
    get(a, '!*cname!*) or a$

symbolic procedure cprecedence op;
get(op, '!*cprecedence!*) or 8$

put('or,       '!*cprecedence!*, 1)$
put('and,      '!*cprecedence!*, 2)$
put('equal,    '!*cprecedence!*, 3)$
put('neq,      '!*cprecedence!*, 3)$
put('greaterp, '!*cprecedence!*, 4)$
put('geq,      '!*cprecedence!*, 4)$
put('lessp,    '!*cprecedence!*, 4)$
put('leq,      '!*cprecedence!*, 4)$
put('plus,     '!*cprecedence!*, 5)$
put('times,    '!*cprecedence!*, 6)$
put('quotient, '!*cprecedence!*, 6)$
put('not,      '!*cprecedence!*, 7)$
put('minus,    '!*cprecedence!*, 7)$


%% Statement Translation %%


symbolic procedure cstmt stmt;
if null stmt then
    nil
else if lisplabelp stmt then
    clabel stmt
else if car stmt eq 'literal then
    cliteral stmt
else if lispassignp stmt then
    cassign stmt
else if lispcondp stmt then
    cif stmt
else if lispbreakp stmt then
    cbreak stmt
else if lispgop stmt then
    cgoto stmt
else if lispreturnp stmt then
    creturn stmt
else if lispstopp stmt then
    cexit stmt
else if lisprepeatp stmt then
    crepeat stmt
else if lispwhilep stmt then
    cwhile stmt
else if lispforp stmt then
    cfor stmt
else if lispstmtgpp stmt then
    cstmtgp stmt
else if lispdefp stmt then
    cproc stmt
else
    cexpstmt stmt$

symbolic procedure cassign stmt;
mkfcassign(cadr stmt, caddr stmt)$

symbolic procedure cbreak stmt;
mkfcbreak()$

symbolic procedure cexit stmt;
mkfcexit()$

symbolic procedure cexpstmt exp;
append(mkctab() . cexp exp, list('!;, mkcterpri()))$

symbolic procedure cfor stmt;
begin
scalar r, var, loexp, stepexp, hiexp, stmtlst;
var := cadr stmt;
stmt := cddr stmt;
loexp := caar stmt;
stepexp := cadar stmt;
hiexp := caddar stmt;
stmtlst := cddr stmt;
r := mkfcfor(var, loexp,
             list(if (numberp stepexp and stepexp < 0) or
                     eqcar(stepexp,'minus) then 'geq else 'leq,
                  var, hiexp),
             var,
             list('plus, var, stepexp));
indentclevel(+1);
r := append(r, foreach st in stmtlst conc cstmt st);
indentclevel(-1);
return r
end$

symbolic procedure cgoto stmt;
mkfcgo cadr stmt$

symbolic procedure cif stmt;
begin
scalar r, st;
r := mkfcif caadr stmt;
indentclevel(+1);
st := seqtogp cdadr stmt;
if eqcar(st, 'cond) and length st=2 then
    st := mkstmtgp(0, list st);
r := append(r, cstmt st);
indentclevel(-1);
stmt := cdr stmt;
while (stmt := cdr stmt) and caar stmt neq t do
<<
    r := append(r, mkfcelseif caar stmt);
    indentclevel(+1);
    st := seqtogp cdar stmt;
    if eqcar(st, 'cond) and length st=2 then
        st := mkstmtgp(0, list st);
    r := append(r, cstmt st);
    indentclevel(-1)
>>;
if stmt then
<<
    r := append(r, mkfcelse());
    indentclevel(+1);
    st := seqtogp cdar stmt;
    if eqcar(st, 'cond) and length st=2 then
        st := mkstmtgp(0, list st);
    r := append(r, cstmt st);
    indentclevel(-1)
>>;
return r
end$

symbolic procedure clabel label;
mkfclabel label$

symbolic procedure cliteral stmt;
mkfcliteral cdr stmt$

symbolic procedure crepeat stmt;
begin
scalar r, stmtlst, logexp;
stmt := reverse cdr stmt;
logexp := car stmt;
stmtlst := reverse cdr stmt;
r := mkfcdo();
indentclevel(+1);
r := append(r, foreach st in stmtlst conc cstmt st);
indentclevel(-1);
return append(r, mkfcdowhile list('not, logexp))
end$

symbolic procedure creturn stmt;
if cdr stmt then
    mkfcreturn cadr stmt
else
    mkfcreturn nil$

symbolic procedure cstmtgp stmtgp;
begin
scalar r;
if car stmtgp eq 'progn then
    stmtgp := cdr stmtgp
else
    stmtgp :=cddr stmtgp;
r := mkfcbegingp();
indentclevel(+1);
r := append(r, for each stmt in stmtgp conc cstmt stmt);
indentclevel(-1);
return append(r, mkfcendgp())
end$

symbolic procedure cwhile stmt;
begin
scalar r, logexp, stmtlst;
logexp := cadr stmt;
stmtlst := cddr stmt;
r := mkfcwhile logexp;
indentclevel(+1);
r := append(r, foreach st in stmtlst conc cstmt st);
indentclevel(-1);
return r
end$


%%                             %%
%% C Code Formatting Functions %%
%%                             %%


%% Statement Formatting %%


% A macro used to prevent things with *cname*
% properties being evaluated in certain circumstances.  MCD 28.3.94
symbolic smacro procedure cexp_name(u);
   if atom u then list(u)
    else rplaca(cexp ('dummyArrayToken . cdr u), car u)$

symbolic procedure mkfcassign(lhs, rhs);
begin
scalar st;
if length rhs = 3 and lhs member rhs then
    begin
    scalar op, exp1, exp2;
    op := car rhs;
    exp1 := cadr rhs;
    exp2 := caddr rhs;
    if op = 'plus then
        if onep exp1 or onep exp2 then
            st := ('!+!+ . cexp_name lhs)
        else if exp1 member '(-1 (minus 1))
           or exp2 member '(-1 (minus 1)) then
            st := ('!-!- . cexp_name lhs)
        else if eqcar(exp1, 'minus) then
            st := append(cexp_name lhs, '!-!= . cexp cadr exp1)
        else if eqcar(exp2, 'minus) then
            st := append(cexp_name lhs, '!-!= . cexp cadr exp2)
        else if exp1 = lhs then
            st := append(cexp_name lhs, '!+!= . cexp exp2)
        else
            st := append(cexp_name lhs, '!+!= . cexp exp1)
    else if op = 'difference and onep exp2 then
        st := ('!-!- . cexp_name lhs)
    else if op = 'difference and exp1 = lhs then
        st := append(cexp_name lhs, '!-!= . cexp exp2)
    else if op = 'times and exp1 = lhs then
        st := append(cexp_name lhs, '!*!= . cexp exp2)
    else if op = 'times then
        st := append(cexp_name lhs, '!*!= . cexp exp1)
    else if op = 'quotient and exp1 = lhs then
        st := append(cexp_name lhs, '!/!= . cexp exp2)
    else
        st := append(cexp_name lhs, '!= . cexp rhs)
    end
else
    st := append(cexp_name lhs, '!= . cexp rhs);
return append(mkctab() . st, list('!;, mkcterpri()))
end$

symbolic procedure mkfcbegingp;
list(mkctab(), '!{, mkcterpri())$

symbolic procedure mkfcbreak;
list(mkctab(), '!b!r!e!a!k, '!;, mkcterpri())$

symbolic procedure mkfcdec(type, varlist); %Amended mcd 13/11/87,3/3/88
<<
    if type equal 'scalar then
        type := deftype!*;

    % Convert Reduce types to C types.
    if type equal 'real then
        type := '!f!l!o!a!t
    else if type equal 'integer then
        type := '!i!n!t;

    % Deal with precision.
    if !*double then
        if type equal '!f!l!o!a!t then
                type := '!d!o!u!b!l!e
        else if type equal '!i!n!t then
                type := '!l!o!n!g;

    varlist := for each v in varlist collect
           if atom v then
               v
           else
               car v . for each dim in cdr v collect
                       if dim eq 'times then '!  %
                       else if numberp dim then add1 dim
                       else if eqcar (dim, 'range) and cadr dim = 0
                         then add1 caddr dim
                       else gentranerr('e,dim,"Not C dimension",nil);
    append(mkctab() . type . '!  . for each v in insertcommas varlist
                                       conc cexp_name v,
           list('!;, mkcterpri()))
>>$

symbolic procedure mkfcdo;
list(mkctab(), '!d!o, mkcterpri())$

symbolic procedure mkfcdowhile exp;
append(append(list(mkctab(), '!w!h!i!l!e, '! , '!(), cexp exp),
       list('!), '!;, mkcterpri()))$

symbolic procedure mkfcelse;
list(mkctab(), '!e!l!s!e, mkcterpri())$

symbolic procedure mkfcelseif exp;
append(append(list(mkctab(), '!e!l!s!e, '! , '!i!f, '! , '!(),
              cexp exp),
       list('!), mkcterpri()))$

symbolic procedure mkfcendgp;
list(mkctab(), '!}, mkcterpri())$

symbolic procedure mkfcexit;
list(mkctab(), '!e!x!i!t, '!(, 0, '!), '!;, mkcterpri())$

symbolic procedure mkfcfor(var1, lo, cond, var2, nextexp);
<<
    if var1 then
        var1 := append(cexp var1, '!= . cexp lo);
    if cond then
        cond := cexp cond;
    if var2 then
    <<
        var2 := cdr mkfcassign(var2, nextexp);
        var2 := reverse cddr reverse var2
    >>;
    append(append(append(list(mkctab(), '!f!o!r! , '! , '!(), var1),
                         '!; . cond),
           append('!; . var2, list('!), mkcterpri())))
>>$

symbolic procedure mkfcgo label;
list(mkctab(), '!g!o!t!o, '! , label, '!;, mkcterpri())$

symbolic procedure mkfcif exp;
append(append(list(mkctab(), '!i!f, '! , '!(), cexp exp),
       list('!), mkcterpri()))$

symbolic procedure mkfclabel label;
list(label, '!:, mkcterpri())$

symbolic procedure mkfcliteral args;
for each a in args conc
    if a eq 'tab!* then
        list mkctab()
    else if a eq 'cr!* then
        list mkcterpri()
    else if pairp a then
        cexp a
    else
        list stripquotes a$

symbolic procedure mkfcprocdec(type, name, params);
<<
    params := append('!( . for each p in insertcommas params
                              conc cexp p,
                     list '!));
    if type then
        append(mkctab() . type . '!  . cexp name,
               append(params,list mkcterpri()))
    else
        append(mkctab() . cexp name, append(params, list mkcterpri()))
>>$

symbolic procedure mkfcreturn exp;
if exp then
    append(append(list(mkctab(), '!r!e!t!u!r!n, '!(), cexp exp),
           list('!), '!;, mkcterpri()))
else
    list(mkctab(), '!r!e!t!u!r!n, '!;, mkcterpri())$

symbolic procedure mkfcwhile exp;
append(append(list(mkctab(), '!w!h!i!l!e, '! , '!(), cexp exp),
       list('!), mkcterpri()))$


%% Indentation Control %%


symbolic procedure mkctab;
list('ctab, ccurrind!*)$


symbolic procedure indentclevel n;
ccurrind!* := ccurrind!* + n * tablen!*$


symbolic procedure mkcterpri;
list 'cterpri$


%%                 %%
%% Misc. Functions %%
%%                 %%


symbolic procedure insertbrackets exp;
'![ . append(exp, list '!])$


%% C Code Formatting & Printing Functions %%


symbolic procedure formatc lst;
begin
scalar linelen;
linelen := linelength 300;
!*posn!* := 0;
for each elt in lst do
    if pairp elt then lispeval elt
    else
    <<
        if !*posn!* + length explode2 elt > clinelen!* then
            ccontline();
        pprin2 elt
    >>;
linelength linelen
end$

symbolic procedure ccontline;
<<
    cterpri();
    ctab !*ccurrind!*;
    pprin2 " "
>>$

symbolic procedure cterpri;
pterpri()$

symbolic procedure ctab n;
<<
    !*ccurrind!* := min0(n, clinelen!* - minclinelen!*);
    if (n := !*ccurrind!* - !*posn!*) > 0 then pprin2 nspaces n
>>$

%% C template processing %%


symbolic procedure procctem;
begin
scalar c, linelen;
linelen := linelength 150;
c := readch();
if c eq '!# then c := procc!#line c;
while c neq !$eof!$ do
    if c eq !$eol!$ then
        c := procc!#line c
    else if c eq '!/ then
        c := procccomm()
    else if c eq '!; then
        c := procactive()
    else
        c := proccheader(c);
linelength linelen
end$

symbolic procedure procc!#line c;
%  # ... <cr>  %
begin
if c eq !$eol!$ then
<<  pterpri();  c := readch()  >>;
if c eq '!# then
    repeat
    <<  pprin2 c;  c := readch()  >>
    until c eq !$eol!$;
return c
end$

symbolic procedure procccomm;
% /* ... */ %
begin
scalar c;
pprin2 '!/;
c := readch();
if c eq '!* then
<<
    pprin2 c;
    c := readch();
    repeat
    <<
        while c neq '!* do
        <<
            if c eq !$eol!$
                then pterpri()
                else pprin2 c;
            c := readch()
        >>;
        pprin2 c;
        c := readch()
    >>
    until c eq '!/;
    pprin2 c;
    c := readch()
>>;
return c
end$

symbolic procedure proccheader c;
begin
scalar name, i;
while seprp c and c neq !$eol!$ do
<<  pprin2 c;  c := readch()  >>;
while not(seprp c or c memq list('!/, '!;, '!()) do
<<  name := aconc(name, c);  pprin2 c;  c := readch()  >>;
if c memq list(!$eol!$, '!/, '!;) then return c;
while seprp c and c neq !$eol!$ do
<<  pprin2 c;  c := readch()  >>;
if c neq '!( then return c;
name := intern compress name;
if not !*gendecs then
    symtabput(name, nil, nil);
put('!$0, '!*cname!*, name);
pprin2 c;
i := 1;
c := readch();
while c neq '!) do
<<
    while seprp c or c eq '!, do
    <<
        if c eq !$eol!$
            then pterpri()
            else pprin2 c;
        c := readch()
    >>;
    name := list c;
    pprin2 c;
    while not(seprp (c := readch()) or c memq list('!,, '!))) do
    <<  name := aconc(name, c);  pprin2 c  >>;
    put(intern compress append(explode2 '!$, explode2 i),
        '!*cname!*,
        intern compress name);
    i := add1 i;
    c:=flushspaces c
>>;
!$!# := sub1 i;
while get(name := intern compress append(explode2 '!$, explode2 i),
          '!*cname!*) do
    remprop(name, '!*cname!*);
return proccfunction c
end$

symbolic procedure proccfunction c;
begin
scalar !{!}count;
while c neq '!{ do
    if c eq '!/ then
        c := procccomm()
    else if c eq '!; then
        c := procactive()
    else if c eq !$eol!$ then
    <<  pterpri();  c := readch()  >>
    else
    <<  pprin2 c;  c := readch()  >>;
pprin2 c;
!{!}count := 1;
c := readch();
while !{!}count > 0 do
    if c eq '!{ then
    <<  !{!}count := add1 !{!}count;  pprin2 c;  c := readch()  >>
    else if c eq '!} then
    <<  !{!}count := sub1 !{!}count;  pprin2 c;  c := readch()  >>
    else if c eq '!/ then
        c := procccomm()
    else if c eq '!; then
        c := procactive()
    else if c eq !$eol!$ then
    <<  pterpri();  c := readch()  >>
    else
    <<  pprin2 c;  c := readch()  >>;
return c
end$

endmodule;

end;
