module lsppasc;    %%  GENTRAN LISP-to-PASCAL Translation Module  %%

%%  Author:  John Fitch and James Davenport after Barbara L. Gates  %%
%%  November 1987                               %%

% Entry Point:  PASCCode

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


fluid '(!*gendecs)$
switch gendecs$

% User-Accessible Global Variables %
global '(pasclinelen!* minpasclinelen!* !*pasccurrind!* pasccurrind!*
         tablen!* pascfuncname!*)$
share pasclinelen!*, minpasclinelen!*,
      pasccurrind!*, tablen!*, pascfuncname!*$
pasccurrind!* := 0$
minpasclinelen!* := 40$
pasclinelen!*    := 70$
!*pasccurrind!* := 0$     %current level of indentation for PASCAL code

global '(!*do!* !*for!*)$
global '(!*posn!* !$!#)$

%%                                      %%
%% LISP-to-PASCAL Translation Functions %%
%%                                      %%

put('pascal,'formatter,'formatpasc);
put('pascal,'codegen,'pasccode);
put('pascal,'proctem,'procpasctem);
put('pascal,'gendecs,'pascdecs);
put('pascal,'assigner,'mkfpascassign);
put('pascal,'boolean!-type,'boolean);

symbolic procedure pasc!-symtabput(name,type,value);
% Like symtabput, but indirects through TYPE declarations.
% has to be recursive
begin
  scalar basetype, origtype, wastypedecl;
  basetype:=car value;
  if basetype = 'TYPE then <<
     wastypedecl:=t;
     value:=cdr value;
    basetype:=car value >>;
  origtype:=symtabget(name,basetype) or symtabget('!*main!*,basetype);
  if pairp origtype then origtype:=cdr origtype; % strip off name;
  if pairp origtype and car origtype = 'TYPE
     then value:= (cadr origtype). append(cdr value,cddr origtype);
  if wastypedecl
     then symtabput(name,type,'TYPE . value)
     else symtabput(name,type,value);
  end;

%% Control Function %%


procedure pasccode forms;
for each f in forms conc
    if atom f then
        pascexp f
    else if car f memq '(!:rd!: !:cr!: !:crn!: !:gi!:) then
        pascexp f
    else if lispstmtp f or lispstmtgpp f then
        if !*gendecs then
            begin
            scalar r;
            r := append(pascdecs symtabget('!*main!*, '!*decs!*),
                        pascstmt f);
            symtabrem('!*main!*, '!*decs!*);
            return r
            end
        else
            pascstmt f
    else if lispdefp f then
        pascproc f
    else
        pascexp f$


%% Procedure Translation %%


procedure pascproc deff;
begin
  scalar type, name, params, paramtypes, vartypes, body, r;
  name := cadr deff;
  if onep length (body := cdddr deff) and lispstmtgpp car body then
     <<  body := cdar body;
         if null car body then body := cdr body  >>;
  if (type := symtabget(name, name)) then
     <<  type := cadr type;  symtabrem(name, name)  >>;
  params := symtabget(name, '!*params!*) or caddr deff;
  symtabrem(name, '!*params!*);
  for each dec in symtabget(name, '!*decs!*) do
    if car dec memq params
       then paramtypes := append(paramtypes, list dec)
       else if cadr dec neq 'TYPE then
            vartypes := append(vartypes, list dec);
  r := mkfpascprocdec(type, name, params, paramtypes);
  if !*gendecs then
     << r:= append(r,list(mkpasctab(),'label,mkpascterpri()));
        indentpasclevel(+1);
        r:= append(r,list(mkpasctab(),'99999, '!;, mkpascterpri()));
        indentpasclevel(-1);
        r := append(r, pascdecs vartypes) >>;
  r:= append(r, mkfpascbegingp() );
  indentpasclevel(+1);
  r := append(r, for each s in body
                   conc pascstmt s);
  indentpasclevel(-1);
  r:=append(r,list(mkpasctab(), 99999, '!:, mkpascterpri()));
  r := append(r, mkfpascendgp());
  if !*gendecs then
     <<  symtabrem(name, nil);  symtabrem(name, '!*decs!*)  >>;
  return r
  end$


%% Generation of Declarations %%


procedure pascdecs decs;
begin scalar r;
  decs:=for each r in decs conc
    if cadr r eq 'type then nil else list r;
  if decs then <<
    indentpasclevel(+1);
    decs:=for each tl in formtypelists decs
                      conc mkfpascdec(car tl, cdr tl);
    indentpasclevel(-1);
    r:=append(list(mkpasctab(),'var, mkpascterpri()), decs) >>;
  return r
end$


%% Expression Translation %%


procedure pascexp exp;
pascexp1(exp, 0)$

procedure pascexp1(exp, wtin);
if atom exp then
    list pascname exp
else
    if onep length exp then
        pascname exp
    else if optype car exp then
        begin
        scalar wt, op, res;
        wt := pascprecedence car exp;
        op := pascop car exp;
        exp := cdr exp;
        if onep length exp then
            res := op . pascexp1(car exp, wt)
        else
        <<
            res := pascexp1(car exp, wt);
            if op eq '!+ then
                while exp := cdr exp do
                <<
                    if atom car exp or caar exp neq 'minus then
                        res := append(res, list op);
                    res := append(res, pascexp1(car exp, wt))
                >>
            else
                while exp := cdr exp do
                    res := append(append(res, list op),
                                  pascexp1(car exp, wt))
        >>;
        if wtin >= wt then res := insertparens res;
        return res
        end
    else if car exp eq 'literal then
        pascliteral exp
    else if car exp eq 'range then
        append(pascexp cadr exp, '!.!. . pascexp caddr exp)
    else if car exp eq '!:rd!: then
    begin scalar mt;
        integer dotpos,!:lower!-sci!:,!:upper!-sci!:; % this forces most
                                         % numbers to exponential format
        mt := rd!:explode exp;
        exp := car mt;
        mt  := cadr mt + caddr mt - 1;
        exp := append(list('literal,car exp, '!.),cdr exp);
        if null (mt = 0) then exp := append(exp, list('!e,mt));

        return pascliteral exp;
    end
    else if car exp memq '(!:cr!: !:crn!: !:gi!:) then
        gentranerr('e,exp,"Pascal doesn't support complex data",nil)
    else if arrayeltp exp then
        if cddr exp and ((caddr exp) equal '!.!.) then
           pascname car exp . pascinsertbrackets cdr exp
          else pascname car exp .
               pascinsertbrackets cdr foreach s in cdr exp conc
                                 '!, . pascexp1(s, 0)
    else
        begin
        scalar op, res;
        op := pascname car exp;
        exp := cdr exp;
        res := pascexp1(car exp, 0);
        while exp := cdr exp do
            res := append(append(res, list '!,), pascexp1(car exp, 0));
        return op . insertparens res
        end$


procedure pascop op;
get(op, '!*pascop!*) or op$

put('or,       '!*pascop!*, 'or  )$
put('and,      '!*pascop!*, 'and )$
put('not,      '!*pascop!*, 'not )$
put('equal,    '!*pascop!*, '!=  )$
put('neq,      '!*pascop!*, '!<!>)$
put('greaterp, '!*pascop!*, '!>  )$
put('geq,      '!*pascop!*, '!>!=)$
put('lessp,    '!*pascop!*, '!<  )$
put('leq,      '!*pascop!*, '!<!=)$
put('plus,     '!*pascop!*, '!+  )$
put('times,    '!*pascop!*, '!*  )$
put('quotient, '!*pascop!*, '!/  )$
put('minus,    '!*pascop!*, '!-  )$
put('expt,     '!*pascop!*, '!*!*)$

procedure pascname a;
if stringp a then
    stringtopascatom a    % convert a to atom containing ''s
else
    get(a, '!*pascname!*) or a$

procedure stringtopascatom a;
intern compress
    foreach c in append('!' . explode2 a, list '!')
        conc list('!!, c)$

put('true,   '!*pascname!*, 'true)$
put('false, '!*pascname!*, 'false)$

procedure pascprecedence op;
get(op, '!*pascprecedence!*) or 9$

put('or,       '!*pascprecedence!*, 1)$
put('and,      '!*pascprecedence!*, 2)$
put('equal,    '!*pascprecedence!*, 3)$
put('neq,      '!*pascprecedence!*, 3)$
put('greaterp, '!*pascprecedence!*, 4)$
put('geq,      '!*pascprecedence!*, 4)$
put('lessp,    '!*pascprecedence!*, 4)$
put('leq,      '!*pascprecedence!*, 4)$
put('plus,     '!*pascprecedence!*, 5)$
put('times,    '!*pascprecedence!*, 6)$
put('quotient, '!*pascprecedence!*, 6)$
put('expt,     '!*pascprecedence!*, 7)$
put('not,      '!*pascprecedence!*, 8)$
put('minus,    '!*pascprecedence!*, 8)$


%% Statement Translation %%


procedure pascstmt stmt;
if null stmt then
    nil
else if lisplabelp stmt then
    pasclabel stmt            % Are there labels?
else if car stmt eq 'literal then
    pascliteral stmt
else if lispassignp stmt then
    pascassign stmt
else if lispcondp stmt then
    pascif stmt
else if lispgop stmt then        % Is there a go?
    pascgoto stmt
else if lispreturnp stmt then
    pascreturn stmt
else if lispstopp stmt then
    pascstop stmt
else if lisprepeatp stmt then
    pascrepeat stmt
else if lispwhilep stmt then
    pascwhile stmt
else if lispforp stmt then
    pascfor stmt
else if lispstmtgpp stmt then
    pascstmtgp stmt
else if lispdefp stmt then
    pascproc stmt
else
    pascexpstmt stmt$

procedure pascassign stmt;
mkfpascassign(cadr stmt, caddr stmt)$

procedure pascstop stmt;
mkfpascstop()$

procedure pascexpstmt exp;
append(mkpasctab() . pascexp exp, list('!;, mkpascterpri()))$

procedure pascfor stmt;
begin
  scalar r, variable, loexp, stepexp, hiexp, stmtlst;
  variable := cadr stmt;
  stmt := cddr stmt;
  loexp := caar stmt;
  stepexp := cadar stmt;
  hiexp := caddar stmt;
  stmtlst := cddr stmt;
  r := mkfpascfor(variable, loexp, hiexp, stepexp);
  indentpasclevel(+1);
  %% ?? Should not the stmtlst have only one member??
  r := append(r, foreach st in stmtlst conc pascstmt st);
  indentpasclevel(-1);
  return r
  end$

procedure pascgoto stmt;
begin
  scalar stmtnum;
  if not ( stmtnum := get(cadr stmt, '!*stmtnum!*) ) then
      stmtnum := put(cadr stmt, '!*stmtnum!*, genstmtnum());
  return mkfpascgo stmtnum
  end$

procedure pascif stmt;
begin
  scalar r, st;
  r := mkfpascif caadr stmt;
  indentpasclevel(+1);
  st := seqtogp cdadr stmt;
  if eqcar(st, 'cond) and length st=2 then
      st := mkstmtgp(0, list st);
  r := append(r, pascstmt st);
  indentpasclevel(-1);
  stmt := cddr stmt;
  if stmt then
  <<
      r := append(r, mkfpascelse());
      indentpasclevel(+1);
      st := seqtogp cdar stmt;
      if eqcar(st, 'cond) and length st=2 then
          st := mkstmtgp(0, list st);
      r := append(r, pascstmt st);
      indentpasclevel(-1)
  >>;
  return r
  end$

procedure pasclabel label;
mkfpasclabel label$

procedure pascliteral stmt;
mkfpascliteral cdr stmt$

procedure pascrepeat stmt;
begin
  scalar r, stmtlst, logexp;
  stmt := reverse cdr stmt;
  logexp := car stmt;
  stmtlst := reverse cdr stmt;
  r := mkfpascrepeat();
  indentpasclevel(+1);
  r := append(r, foreach st in stmtlst conc pascstmt st);
  r:=removefinalsemicolon(r);    % Remove final semicolon
  indentpasclevel(-1);
  return append(r, mkfpascuntil logexp)
  end$

procedure pascreturn stmt;
if cdr stmt then
begin scalar r;
    r := mkfpascbegingp();
    indentpasclevel(+1);
    r := append(r, mkfpascassign(pascfuncname!*, cadr stmt));
    r := append(r, mkfpascreturn());
    r := removefinalsemicolon(r);    % Remove final semicolon
    indentpasclevel(-1);
    return append(r, mkfpascendgp())
end
else
    mkfpascreturn()$

procedure pascstmtgp stmtgp;
begin
  scalar r;
  if car stmtgp eq 'progn then
      stmtgp := cdr stmtgp
  else
    stmtgp :=cddr stmtgp;
  r := mkfpascbegingp();
  indentpasclevel(+1);
  r := append(r, for each stmt in stmtgp conc pascstmt stmt);
  r:=removefinalsemicolon(r);    % Remove final semicolon
  indentpasclevel(-1);
  return append(r, mkfpascendgp())
  end$

procedure pascwhile stmt;
begin
  scalar r, logexp, stmtlst;
  logexp := cadr stmt;
  stmtlst := cddr stmt;
  r := mkfpascwhile logexp;
  indentpasclevel(+1);
  r := append(r, foreach st in stmtlst conc pascstmt st);
  indentpasclevel(-1);
  return r
  end$

procedure removefinalsemicolon r;
begin scalar rr;
  r:=reversip r;
  if car r eq '!; then return reversip cdr r;
  if not ('!; memq r) then return reversip r;
  rr:=r;
  while not (cadr rr eq '!;) do << rr := cdr rr >>;
  rplacd(rr, cddr rr);
  return reversip r
end$

%%                                  %%
%% Pascal Code Formatting Functions %%
%%                                  %%


%% Statement Formatting %%


% A macro used to prevent things with *pascname*
% properties being evaluated in certain circumstances.  MCD 28.3.94
symbolic smacro procedure pascexp_name(u);
if atom u then
  list(u)
else
  rplaca(pascexp ('dummyArrayToken . cdr u), car u)$

procedure mkfpascassign(lhs, rhs);
begin
scalar st;
    st := append(pascexp_name lhs, '!:!= . pascexp rhs);
    return append(mkpasctab() . st, list('!;, mkpascterpri()))
end$

procedure mkfpascbegingp;
list(mkpasctab(), 'begin, mkpascterpri())$

symbolic procedure mkfpascdec (type, varlist);
 begin scalar simplet, arrayt;
    varlist := for each v in varlist do
       if atom v then simplet := v . simplet
       else
          arrayt :=
           (car v . cdr for each dim in cdr v conc
                     if eqcar(dim,'range)
                        then list ('!, , cadr dim, '!.!., caddr dim )
                        else list ('!, , 0, '!.!., dim ))
            . arrayt;

    return append(if simplet
                    then append(mkpasctab() .
              for each v in insertcommas simplet conc pascexp v,
                           (list('!:!  , type, '!;, mkpascterpri()))),
           for each v in arrayt conc
             append(mkpasctab() . car pascexp car v. '!:!  .
             'array . insertbrackets cdr v,
                  list('! of!  , type, '!;, mkpascterpri())))
end;


procedure mkfpascdo;
list(mkpasctab(), !*do!*, mkpascterpri())$

procedure mkfpascuntil exp;
append(append(list(mkpasctab(), 'until, '! ),
              pascexp exp),
       list('!;, mkpascterpri() ));

procedure mkfpascelse;
list(mkpasctab(), 'else, mkpascterpri())$

procedure mkfpascendgp;
list(mkpasctab(), 'end, '!;, mkpascterpri())$

procedure mkfpascstop;
list(mkpasctab(), 'svr, '!(, '!0, '!), '!;, mkpascterpri())$

procedure mkfpascfor(var1, lo, hi, stepexp);
<<
    stepexp := if stepexp = 1 then list('! , 'to, '! )  else
           if (stepexp = -1) or (stepexp = '(minus 1)) then
           list('! , 'downto, '! )  else list('error);
    hi:=append(pascexp hi,list('! , !*do!*, mkpascterpri()));
    hi:=append(pascexp lo, nconc(stepexp, hi));
    append(list(mkpasctab(), !*for!*, '! , var1, '!:!=), hi)
>>$

procedure mkfpascgo label;
list(mkpasctab(), 'goto, '! , label, '!;, mkpascterpri())$

procedure mkfpascif exp;
append(append(list(mkpasctab(), 'if, '! ), pascexp exp),
       list('!  , 'then, mkpascterpri()))$

procedure mkfpasclabel label;
list(label, '!:, mkpascterpri())$

procedure mkfpascliteral args;
for each a in args conc
    if a eq 'tab!* then
        list mkpasctab()
    else if a eq 'cr!* then
        list mkpascterpri()
    else if pairp a then
        pascexp a
    else
        list stripquotes a$

procedure mkfpascprocdec(type, name, params, paramtypes);
<<  pascfuncname!* := name;
    params := append('!( . cdr for each p in params
                              conc '!, . pascdum(p, paramtypes),
                     list '!));
    if type then
        append(mkpasctab() . 'function . '!  . pascexp name,
               append(params,list( '!:, type,  '!;, mkpascterpri())))
    else
        append(mkpasctab() . 'procedure . '!  . pascexp name,
               append(params, list('!;, mkpascterpri())))
>>$


symbolic procedure pascdum (p,types);
  begin scalar type;
    type := pascgettype(p,types);
    type := if atom type then list type
             else if null cdr type then type
             else append('array .
                          insertbrackets
                           cdr for each dim in cdr type conc
                               if eqcar(dim,'range)
                                 then list('!,,cadr dim,'!.!.,caddr dim)
                                else list ('!, , 0, '!.!., dim ),
                         list ('! of!  , car type));
    return p . '!: . type
end;


symbolic procedure pascgettype(p,types);
  if null types then 'default
  else if p memq car types then cdr car types
  else pascgettype(p,cdr types);


procedure mkfpascrepeat;
list(mkpasctab(), 'repeat, mkpascterpri())$

procedure mkfpascreturn;
   list(mkpasctab(), 'goto, '! , 99999, '!;,
        '!{return!}, mkpascterpri())$

procedure mkfpascwhile exp;
append(append(list(mkpasctab(), 'while, '! , '!(), pascexp exp),
       list('!), mkpascterpri()))$


%% Indentation Control %%


procedure mkpasctab;
list('pasctab, pasccurrind!*)$


procedure indentpasclevel n;
pasccurrind!* := pasccurrind!* + n * tablen!*$


procedure mkpascterpri;
list 'pascterpri$


%%                 %%
%% Misc. Functions %%
%%                 %%


procedure pascinsertbrackets exp;
'![ . append(exp, list '!] )$




%% PASCAL Code Formatting & Printing Functions %%


procedure formatpasc lst;
begin
  scalar linelen;
  linelen := linelength 300;
  !*posn!* := 0;
  for each elt in lst do
      if pairp elt then lispeval elt
      else
      <<
          if !*posn!* + length explode2 elt > pasclinelen!* then
              pasccontline();
          pprin2 elt
      >>;
  linelength linelen
  end$

procedure pasccontline;
<<
    pascterpri();
    pasctab !*pasccurrind!*;
    pprin2 " "
>>$

procedure pascterpri;
pterpri()$

procedure pasctab n;
<<
    !*pasccurrind!* := min0(n, pasclinelen!* - minpasclinelen!*);
    if (n := !*pasccurrind!* - !*posn!*) > 0 then pprin2 nspaces n
>>$



%% PASCAL     %%
%% John Fitch %%

global '(pascfuncname!*)$
share pascfuncname!*$

symbolic procedure procpasctem;
begin
  scalar c;
  c:=flushspaces readch();
  while not (c eq !$eof!$ or c eq '!.)
    do c:=flushspaces procpasctem1(c);
  end;

symbolic procedure procpasctem1 c;
begin
  scalar l,w, linelen;
  linelen := linelength 150;
  pprin2 c;
  while c neq !$eof!$  and w neq 'END do <<
    if c eq !$eol!$ then
    <<  pterpri(); c := readch()  >>
    else if c eq '!{ then << c := procpasccomm(); w:= nil >>
    else if c eq '!; then
        << c := procactive(); pprin2 c; w:=nil >>;
    if null w then <<
      if liter c then l:= list c;
      c := readch();
      while liter c or digit c or c eq '!_ do
         << pprin2 c; l:=c . l; c := readch() >>;
      w:=intern compress reverse l;
      l:=nil >>;
    if w eq 'VAR then c:=procpascvar c
    else if w eq 'CONST then c:=procpascconst c
    else if w eq 'TYPE then c:=procpasctype c
    else if w memq '(FUNCTION PROCEDURE OPERATOR)
         then c:=procfuncoperheading(w,c)
    else if w eq 'BEGIN then c:= NIL . procpasctem1 c
    else if w neq 'END then <<
       while c neq '!; do <<
         if c eq '!{ then c := procpasccomm()
           else << pprin2 c; c := readch() >> >>;
       pprin2 c;
       c:=nil . readch() >>;
    % recursive, since PASCAL is
    if w eq 'END then <<
       c:=flushspaces c;
       if not ( c memq '(!; !.)) then
          gentranerr('e,nil,"END not followed by ; or .",nil);
       pprin2 c; c:=readch() >>
       else <<
         w:=car c;
         c:=flushspaces cdr c; >>
    >>;
  linelength linelen;
  return c;
  end$

symbolic procedure procpasctype c;
% TYPE ...; ...; ... %
begin
  scalar w,l;
next:
  while not liter c do <<
        if c eq !$eol!$ then pterpri() else pprin2 c;
        c:=readch() >>;
  l:=nil;
  while liter c or digit c or c eq '!_ do
    << pprin2 c; l:=c . l; c := readch() >>;
  w:=intern compress reverse l;
  if w memq '(FUNCTION PROCEDURE OPERATOR CONST VAR)
     then return w . c;
  c:=flushspaces c;
  if c neq '!= then
     gentranerr('e,nil,"Malformed TYPE declaration", nil);
  l:=readpascaltype c;
  c:=car l;
  pasc!-symtabput(pascfuncname!*,w,'TYPE . cdr l);
  goto next;
  end;

symbolic procedure procpascvar c;
% VAR ...; ...; ... %
begin
  scalar name,l,namelist;
next:
  while not liter c do <<
        if c eq !$eol!$ then pterpri() else pprin2 c;
        c:=readch() >>;
  l:=nil;
  while liter c or digit c or c eq '!_ do
    << pprin2 c; l:=c . l; c := readch() >>;
  name:=intern compress reverse l;
  if name memq '(FUNCTION PROCEDURE OPERATOR CONST VAR BEGIN)
     then return name . c;
  c:=flushspaces c;
  namelist:=list name;
  while (c = '!, ) do <<
    pprin2 c;
    c:=flushspaces readch();
    l:=nil;
    while liter c or digit c or c eq '!_ do
      << pprin2 c; l:=c . l; c := readch() >>;
    name:=intern compress reverse l;
    namelist:= name . namelist;
    c:=flushspaces c >>;
  if c neq '!: then gentranerr('e,nil,"Malformed VAR declaration", nil);
  l:=readpascaltype c;
  c:=car l;
  for each name in namelist do
    pasc!-symtabput(pascfuncname!*,name, cdr l);
  goto next;
  end;

symbolic procedure procpasccomm;
% { ... } %
begin
scalar c;
pprin2 '!{;
c := readch();
while c neq '!} do
        <<
            if c eq !$eol!$
                then pterpri()
                else pprin2 c;
            c := readch()
        >>;
pprin2 c;
c := readch();
return c
end$

symbolic procedure procfuncoperheading(keyword,c);
% returns the word after the procedure, and the character delimiting it
begin
  scalar lst, name, i, ty, args, myargs;
  c:=flushspaces c;
  while not(seprp c or c eq '!( or c eq '!: ) do
    <<  name := aconc(name, c);  pprin2 c;  c := readch()  >>;
  name := intern compress name;
  put('!$0, '!*pascalname!*, name);
  symtabput(name,'!*type!*,keyword);
  pascfuncname!*:=name;
  c:=flushspaces c;
  if c eq '!( then <<
    i := 1;
    pprin2 c;
    c := readch();
    while c neq '!) do
    <<  c:=flushspacescommas c;
        name := list c;
        pprin2 c;
        while not (seprp (c := readch()) or
                   c memq list('!,, '!), '!:)) do
        <<  name := aconc(name, c);  pprin2 c  >>;
        put(intern compress append(explode2 '!$, explode2 i),
            '!*pascalname!*,
            name:=intern compress name);
        myargs := name . myargs;
        i := add1 i;
    if c eq '!: then <<
                ty:=readpascaltype(c);
                c:=car ty; ty:=cdr ty;
        foreach n in myargs do
             pasc!-symtabput(pascfuncname!*,n,ty);
                args:=append(myargs,args);
                myargs:=nil;
                if (c eq '!;) then << pprin2 c; c:=readch() >>
    >>;
        c:=flushspaces c
    >>;
    !$!# := sub1 i;
    >>
    else !$!# :=0;
  if c neq '!: then
     << pprin2 c;
        while not (((c := readch()) eq '!:) or (c eq !$eol!$)) do
              pprin2 c >>;
  if c eq '!: then
  <<
      ty := readpascaltype c;
      pasc!-symtabput(name,name,cdr ty);
          c:=car ty
  >>;
  if numberp i then
     while get(name := intern compress append(explode2 '!$, explode2 i),
            '!*pascalname!*) do
        << remprop(name, '!*pascalname!*); i:=sub1 i >>;
  lst:=nil;
  c:=flushspaces c;
  while liter c or digit c or c eq '!_ do
     << pprin2 c; lst:=c . lst; c := readch() >>;
  if lst then
     lst:=intern compress reverse lst;
  return lst . c
  end$

symbolic procedure readpascaltype(c);
begin
  scalar ty;
  pprin2 c;
  c := flushspaces readch();
  ty := list c;
  pprin2 c;
  while not (seprp (c := readch()) or c memq list('!;, '!), '![ )) do
    <<  ty := aconc(ty, c);  pprin2 c  >>;
  ty := intern compress ty;
  if ty eq 'array then return readpascalarraydeclaration(c)
     else return c . list ty;
  end;

symbolic procedure readpascalarraydeclaration (c);
begin
  scalar lo,hi,ty;
  ty:= nil;
  c:=flushspaces c;
  if not (c eq '![) then
     gentranerr(c,nil,"invalid pascal array declaration",nil);
  pprin2 c;
l:  c:=flushspaces readch();
  lo:= list c;
  pprin2 c;
  while not (seprp (c := readch()) or c eq '!.) do
    << lo:=aconc(lo,c); pprin2 c  >>;
  lo :=  compress lo;
  c:=flushspaces c;
  if not numberp lo then lo:=intern lo;
  pprin2 c;
  c:=readch();
  if not (c eq '!.) then
     gentranerr (c,nil,".. not found in array declaration",nil);
  pprin2 c;
  c:=flushspaces readch();
  hi:= list c;
  pprin2 c;
  while not (seprp (c := readch()) or c memq list('!,, '!])) do
    << hi:=aconc(hi,c); pprin2 c  >>;
  hi := compress hi;
  if not numberp hi then hi:=intern hi;
  ty:= hi . ty;
  pprin2 c;
  c:=flushspaces c;
  if c eq '!] then
    << ty:= reverse ty;
       c:=flushspaces readch();
       if not(c memq '( !o !O)) then gentranerr(c,nil,"not 'of'",nil);
       pprin2 c;
       c:=readch();
       if not(c memq '( !f !F)) then gentranerr(c,nil,"not 'of'",nil);
       pprin2 c;
       c:=readpascaltype(readch());
       return car c . append(cdr c,ty) >>;
  goto l;
  end;

procedure procpascheader c;
begin
  scalar name, i;
  while seprp c and c neq !$eol!$ do
    <<  pprin2 c;  c := readch()  >>;
  while not(seprp c or c memq list('!{, '!;, '!()) do
    <<  name := aconc(name, c);  pprin2 c;  c := readch()  >>;
  if c memq list(!$eol!$, '!{, '!;) then return c;
  while seprp c and c neq !$eol!$ do
    <<  pprin2 c;  c := readch()  >>;
  if c neq '!( then return c;
  name := intern compress name;
  if not !*gendecs then
      pasc!-symtabput(name, nil, nil);
  put('!$0, '!*cname!*, name);
  pprin2 c;
  i := 1;
  c := readch();
  while c neq '!) do
  <<  c:=flushspacescommas c;
      name := list c;
      pprin2 c;
      while not(seprp (c := readch()) or c memq list('!,, '!))) do
        <<  name := aconc(name, c);  pprin2 c  >>;
      put(intern compress append(explode2 '!$, explode2 i),
          '!*cname!*,
          intern compress name);
      i := add1 i;
      c:=flushspaces c;
  >>;
  !$!# := sub1 i;
  while get(name := intern compress append(explode2 '!$, explode2 i),
            '!*cname!*) do
      remprop(name, '!*cname!*);
  return procpascfunction c
  end$

procedure procpascfunction c;
begin
  scalar block!-count;
  while c neq '!{ do
      if c eq '!; then
          c := procactive()
      else if c eq !$eol!$ then
      <<  pterpri();  c := readch()  >>
      else
      <<  pprin2 c;  c := readch()  >>;
  pprin2 c;
  block!-count := 1;
  c := readch();
  while block!-count > 0 do
    if c eq 'begin then
        <<  block!-count := add1 block!-count;
            pprin2 c;  c := readch()  >>
    else if c eq 'end then
    <<  block!-count := sub1 block!-count;  pprin2 c;  c := readch()  >>
    else if c eq '!{ then
        c := procpasccomm()
    else if c eq '!; then
        c := procactive()
    else if c eq !$eol!$ then
    <<  pterpri();  c := readch()  >>
    else
    <<  pprin2 c;  c := readch()  >>;
  return c
  end$

% misc routines - JHD 15.12.87


endmodule;

end;
