module intrfc;    %%  GENTRAN Parsing Routines & Control Functions  %%

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

% Entry Points:
% DeclareStat, GENDECS, GenInStat (GentranIn), GenOutStat
% (GentranOutPush), GenPopStat (GentranPop), GenPushStat, GenShutStat
% (GentranShut), GenStat (Gentran), (GENTRANPAIRS),
% LiteralStat, SYM!-GENTRAN, SYM!-GENTRANIN, SYM!-GENTRANOUT,
% SYM!-GENTRANSHUT,
% SYM!-GENTRANPUSH, SYM!-GENTRANPOP

fluid '(!*getdecs);

% GENTRAN Commands %
put('gentran,     'stat, 'genstat    )$
put('gentranin,   'stat, 'geninstat  )$
put('gentranout,  'stat, 'genoutstat )$
put('gentranshut, 'stat, 'genshutstat)$
put('gentranpush, 'stat, 'genpushstat)$
put('gentranpop,  'stat, 'genpopstat )$

% Form Analysis Function %
put('gentran,        'formfn, 'formgentran)$
put('gentranin,      'formfn, 'formgentran)$
put('gentranoutpush, 'formfn, 'formgentran)$
put('gentranshut,    'formfn, 'formgentran)$
put('gentranpop,     'formfn, 'formgentran)$

% GENTRAN Functions %
put('declare, 'stat, 'declarestat)$
put('literal, 'stat, 'literalstat)$

% GENTRAN Operators %
newtok '((!: !: !=)    lsetq )$  infix ::= $
newtok '((!: != !:)    rsetq )$  infix :=: $
newtok '((!: !: != !:) lrsetq)$  infix ::=:$

% User-Accessible Primitive Function %
operator gendecs$

% GENTRAN Mode Switches %
fluid '(!*gendecs)$
!*gendecs := t$
put('gendecs, 'simpfg, '((nil) (t (gendecs nil))))$
switch gendecs$

%See procedure gendecs:
fluid '(!*keepdecs)$
!*keepdecs := nil$
switch keepdecs$

% GENTRAN Flags %

fluid '(!*gentranopt !*gentranseg !*period);

!*gentranseg := t$

switch gentranseg$

% User-Accessible Global Variable %
global '(gentranlang!*)$
share gentranlang!*$
gentranlang!* := 'fortran$

% GENTRAN Global Variable %
global '(!*term!* !*stdin!* !*stdout!* !*instk!* !*currin!* !*outstk!*
         !*currout!* !*outchanl!*)$
!*term!*     := (t . nil)$             %terminal filepair
!*stdin!*    := !*term!*$              %standard input filepair
!*stdout!*   := !*term!*$              %standard output filepair
!*instk!*    := list !*stdin!*$        %template file stack
!*currin!*   := car !*instk!*$         %current input filepair
!*outstk!*   := list !*stdout!*$       %output file stack
!*currout!*  := car !*outstk!*$        %current output filepair
!*outchanl!* := list cdr !*currout!*$  %current output channel list

global '(!*do!* !*for!*)$
off quotenewnam$
!*do!* := 'do$
!*for!* := 'for$
on quotenewnam$

global '(!*lispstmtops!*);

!*lispstmtops!* := !*for!* . !*lispstmtops!*; % added by R. Liska to
                                              % handle long FOR loops.

% REDUCE Variables %
global '(cursym!* !*vars!*)$
fluid '(!*mode)$


%%                    %%
%%  PARSING ROUTINES  %%
%%                    %%


%%  GENTRAN Command Parsers  %%


procedure genstat;
%                     %
% GENTRAN             %
%     stmt            %
% [OUT f1,f2,...,fn]; %
%                     %
begin
scalar stmt;
flag('(out), 'delim);
stmt := xread t;
remflag('(out), 'delim);
if cursym!* eq 'out then
    return list('gentran, stmt, readfargs())
else if endofstmtp() then
    return list('gentran, stmt, nil)
else
    gentranerr('e, nil, "INVALID SYNTAX", nil)
end$


procedure geninstat;
%                     %
% GENTRANIN           %
%     f1,f2,...,fm    %
% [OUT f1,f2,...,fn]; %
%                     %
begin
scalar f1, f2;
flag('(out), 'delim);
f1 := xread nil;
if atom f1 then f1 := list f1 else f1 := cdr f1;
remflag('(out), 'delim);
if cursym!* eq 'out then
    f2 := readfargs();
return list('gentranin, f1, f2)
end$


procedure genoutstat;
%                          %
% GENTRANOUT f1,f2,...,fn; %
%                          %
list('gentranoutpush, readfargs())$


procedure genshutstat;
%                           %
% GENTRANSHUT f1,f2,...,fn; %
%                           %
list('gentranshut, readfargs())$


procedure genpushstat;
%                           %
% GENTRANPUSH f1,f2,...,fn; %
%                           %
list('gentranoutpush, readfargs())$


procedure genpopstat;
%                          %
% GENTRANPOP f1,f2,...,fn; %
%                          %
list('gentranpop, readfargs())$


%%  GENTRAN Function Parsers  %%

newtok '((!: !:) range);
    % Used for declarations with lower and upper bounds;

procedure declarestat;
%                              %
% DECLARE v1,v2,...,vn : type; %
%                              %
% DECLARE                      %
% <<                           %
%     v1,v2,...,vn1 : type1;   %
%     v1,v2,...,vn2 : type2;   %
%     .                        %
%     .                        %
%     v1,v2,...,vnn : typen    %
% >>;                          %
%                              %
begin
scalar res, varlst, type;
scan();
put('range,'infix,4);
put('range,'op,'((4 4)));
if cursym!* eq '!*lsqbkt!* then
<<
    scan();
    while cursym!* neq '!*rsqbkt!* do
    <<
        varlst := list xread1 'for;
        while cursym!* neq '!*colon!* do
            varlst := append(varlst, list xread 'for);
        type := declarestat1();
        res := append(res, list(type . varlst));
        if cursym!* eq '!*semicol!* then scan()
    >>;
    scan()
>>
else
<<
    varlst := list xread1 'for;
    while cursym!* neq '!*colon!* do
        varlst := append(varlst, list xread 'for);
    type := declarestat1();
    res := list (type . varlst);
>>;
if not endofstmtp() then
    gentranerr('e, nil, "INVALID SYNTAX", nil);
remprop('range,'infix);
remprop('range,'op);
return ('declare . res)
end$

procedure declarestat1;
begin
scalar res;
scan();
if endofstmtp() then
    return nil;
if cursym!* eq 'implicit then
<<
    scan();
    res := intern compress append(explode 'implicit! , explode cursym!*)
>>
else
    res := cursym!*;
scan();
if cursym!* eq 'times then
<<
    scan();
    if numberp cursym!* then
    <<
        res := intern compress append(append(explode res, explode '!*),
                                      explode cursym!*);
        scan()
    >>
    else
        gentranerr('e, nil, "INVALID SYNTAX", nil)
>>;
return res
end$


procedure literalstat;
%                             %
% LITERAL arg1,arg2,...,argn; %
%                             %
begin
scalar res;
repeat
    res := append(res, list xread t)
until endofstmtp();
if atom res then
    return list('literal, res)
else if car res eq '!*comma!* then
    return rplaca(res, 'literal)
else
    return('literal . res)
end$


%%                           %%
%%  Symbolic Mode Functions  %%
%%                           %%


procedure sym!-gentran form;
lispeval formgentran(list('gentran, form, nil), !*vars!*, !*mode)$

procedure sym!-gentranin flist;
if flist then
  lispeval formgentran(list('gentranin,
                        (if atom flist then list flist else flist),
                        nil),
                   !*vars!*, !*mode)$

procedure sym!-gentranout flist;
lispeval formgentran(list('gentranoutpush,
                      if atom flist then list flist else flist),
                 !*vars!*, !*mode)$

procedure sym!-gentranshut flist;
lispeval formgentran(list('gentranshut,
                      if atom flist then list flist else flist),
                 !*vars!*, !*mode)$

procedure sym!-gentranpush flist;
lispeval formgentran(list('gentranoutpush,
                      if atom flist then list flist else flist),
                 !*vars!*, !*mode)$

procedure sym!-gentranpop flist;
lispeval formgentran(list('gentranpop,
                      if atom flist then list flist else flist),
                 !*vars!*, !*mode)$


%%                           %%
%%  Form Analysis Functions  %%
%%                           %%


procedure formgentran(u, vars, mode);
(car u) . foreach arg in cdr u collect formgentran1(arg, vars, mode)$

symbolic procedure formgentran1(u, vars, mode);
if pairp u and car u eq '!:dn!: then
    mkquote <<precmsg length explode abs car(u := cdr u);
              decimal2internal(car u,cdr u)>>
else if pairp u and car u eq '!:rd!: then mkquote u
else if pairp u and not listp u then
  if !*getdecs
     then formgentran1(list ('declare,list(cdr u,car u)),vars,mode)
          % Amended mcd 13/11/87 to allow local definitions.
     else gentranerr('e,u,
                     "Scalar definitions cannot be translated",nil)
else if atom u then
    mkquote u
else if car u eq 'eval then
    if mode eq 'algebraic then
        list('aeval, form1(cadr u, vars, mode))
    else
        form1(cadr u, vars, mode)
else if car u memq '(lsetq rsetq lrsetq) then
    % (LSETQ (var s1 s2 ... sn) exp)                                 %
    %   -> (SETQ (var (EVAL s1) (EVAL s2) ... (EVAL sn)) exp)        %
    % (RSETQ var exp)                                                %
    %   -> (SETQ var (EVAL exp))                                     %
    % (LRSETQ (var s1 s2 ... sn) exp)                                %
    %   -> (SETQ (var (EVAL s1) (EVAL s2) ... (EVAL sn)) (EVAL exp)) %
    begin
    scalar op, lhs, rhs;
    op := car u;
    lhs := cadr u;
    rhs := caddr u;
    if op memq '(lsetq lrsetq) and listp lhs then
        lhs := car lhs . foreach s in cdr lhs collect list('eval, s);
    if op memq '(rsetq lrsetq) then
        rhs := list('eval, rhs);
    return formgentran1(list('setq, lhs, rhs), vars, mode)
    end
else
    'list . foreach elt in u
                collect formgentran1(elt, vars, mode)$


%%                     %%
%%  Control Functions  %%
%%                     %%


%%  Command Control Functions  %%


symbolic procedure gentran(forms, flist);
begin scalar !:print!-prec!: ; % Gentran ignores print_precision
if flist then
    lispeval list('gentranoutpush, list('quote, flist));
forms := preproc list forms;
if gentranparse(forms) then
<<
  forms := lispcode forms;
  if smemq('differentiate,forms) then
    <<load!-package 'adiff; forms := adiff!-eval forms>>;
  if !*gentranopt then forms := opt forms;
  if !*gentranseg then forms := seg forms;
  apply1(get(gentranlang!*,'formatter) or get('fortran,'formatter),
         apply1(get(gentranlang!*,'codegen) or get('fortran,'codegen),
                forms))
>>;
if flist then
<<
    flist := car !*currout!* or ('list . cdr !*currout!*);
    lispeval '(gentranpop '(nil));
    return flist
>>
else
    return car !*currout!* or ('list . cdr !*currout!*)
end$


procedure gentranin(inlist, outlist);
begin
scalar ich;
foreach f in inlist do
    if pairp f then
        gentranerr('e, f, "Wrong Type of Arg", nil)
    else if not !*filep!* f and f neq car !*stdin!* then
        gentranerr('e, f, "Nonexistent Input File", nil);
if outlist then
    lispeval list('gentranoutpush, mkquote outlist);
ich := rds nil;
foreach f in inlist do
<<
    if f = car !*stdin!* then
        pushinputstack !*stdin!*
    else if retrieveinputfilepair f then
        gentranerr('e, f, "Template File Already Open for Input", nil)
    else
        pushinputstack makeinputfilepair f;
    rds cdr !*currin!*;
    lispapply(get(gentranlang!*,'proctem) or get('fortran,'proctem),
              nil);
%    if gentranlang!* eq 'ratfor then
%        procrattem()
%    else if gentranlang!* eq 'c then
%        procctem()
%    else
%        procforttem();
    rds ich;
    popinputstack()
>>;
if outlist then
<<
    outlist := car !*currout!* or ('list . cdr !*currout!*);
    lispeval '(gentranpop '(nil));
    return outlist
>>
else
    return car !*currout!* or ('list . cdr !*currout!*)
end$


procedure gentranoutpush flist;
<<
    if onep length (flist := fargstonames(flist, t)) then
        flist := car flist;
    pushoutputstack (retrieveoutputfilepair flist
                        or makeoutputfilepair flist);
    car !*currout!* or ('list . cdr !*currout!*)
>>$


procedure gentranshut flist;
%  close, delete, [output to T]  %
begin
scalar trm;
flist := fargstonames(flist, nil);
trm := if onep length flist then (car flist = car !*currout!*)
       else if car !*currout!*
        then (if car !*currout!* member flist then t)
       else lispeval('and . foreach f in cdr !*currout!*
                                collect (if f member flist then t));
deletefromoutputstack flist;
if trm and !*currout!* neq !*stdout!* then
    pushoutputstack !*stdout!*;
return car !*currout!* or ('list . cdr !*currout!*)
end$


procedure gentranpop flist;
<<
    if 'all!* member flist then
        while !*outstk!* neq list !*stdout!* do
            lispeval '(gentranpop '(nil))
    else
    <<
        flist := fargstonames(flist,nil);
        if onep length flist then
            flist := car flist;
        popoutputstack flist
    >>;
    car !*currout!* or ('list . cdr !*currout!*)
>>$


%%  Mode Switch Control Function  %%


procedure gendecs name;
% Hacked 15/11/88 to make it actually tidy up symbol table properly.
% KEEPDECS also added.  mcd.
%%%%%%%%%%%%%%%%%%%%%%%%
%                      %
% ON/OFF GENDECS;      %
%                      %
% GENDECS subprogname; %
%                      %
%%%%%%%%%%%%%%%%%%%%%%%%
<<
    if name equal 0 then name := nil;
    apply1(get(gentranlang!*,'formatter) or get('fortran,'formatter),
       apply1(get(gentranlang!*,'gendecs) or get('fortran,'gendecs),
              symtabget(name, '!*decs!*)));
%    if gentranlang!* eq 'ratfor then
%        formatrat ratdecs symtabget(name, '!*decs!*)
%    else if gentranlang!* eq 'c then
%        formatc cdecs symtabget(name, '!*decs!*)
%    else
%        formatfort fortdecs symtabget(name, '!*decs!*);
    % Sometimes it would be handy to know just what we've generated.
    % If the switch KEEPDECS is on (usually off) this is done.
    if null !*keepdecs then
    <<
        symtabrem(name, '!*decs!*);
        symtabrem(name, '!*type!*);
    >>;
    symtabrem(name, nil);
>>$


%%  Misc. Control Functions  %%


procedure gentranpairs prs;
%                              %
% GENTRANPAIRS dottedpairlist; %
%                              %
begin
  scalar formatfn,assignfn;
  formatfn:=get(gentranlang!*,'formatter) or get('fortran,'formatter);
  assignfn:=get(gentranlang!*,'assigner) or get('fortran,'assigner);
  return
    for each pr in prs do
        apply1(formatfn,apply2(assignfn,lispcodeexp(car pr, !*period),
                                        lispcodeexp(cdr pr, !*period)))
  end;

%procedure gentranpairs prs;
%%                              %
%% GENTRANPAIRS dottedpairlist; %
%%                              %
%if gentranlang!* eq 'ratfor then
%    for each pr in prs do
%        formatrat mkfratassign(lispcodeexp(car pr, !*period),
%                               lispcodeexp(cdr pr, !*period))
%else if gentranlang!* eq 'c then
%    for each pr in prs do
%        formatc mkfcassign(lispcodeexp(car pr, !*period),
%                           lispcodeexp(cdr pr, !*period))
%else
%    for each pr in prs do
%        formatfort mkffortassign(lispcodeexp(car pr, !*period),
%                                 lispcodeexp(cdr pr, !*period))$


%%                                                  %%
%% Input & Output File Stack Manipulation Functions %%
%%                                                  %%


%%  Input Stack Manipulation Functions  %%


procedure makeinputfilepair fname;
(fname . open(mkfil fname, 'input))$

procedure retrieveinputfilepair fname;
retrievefilepair(fname, !*instk!*)$

procedure pushinputstack pr;
<<
    !*instk!* := pr . !*instk!*;
    !*currin!* := car !*instk!*;
    !*instk!*
>>$

procedure popinputstack;
begin scalar x;
        x := !*currin!*;
        if cdr !*currin!* then close cdr !*currin!*;
        !*instk!* := cdr !*instk!* or list !*stdin!*;
        !*currin!* := car !*instk!*;
        return x
end$

%%  Output File Stack Manipulation Functions  %%


procedure makeoutputfilepair f;
if atom f then
    (f . open(mkfil f, 'output))
else
    aconc((nil . f) .
          foreach fn in f
                  conc if not retrieveoutputfilepair fn
                          then list makeoutputfilepair fn,
          (nil . nil))$

procedure retrieveoutputfilepair f;
if atom f
   then retrievefilepair(f, !*outstk!*)
   else retrievepfilepair(f, !*outstk!*)$

procedure pushoutputstack pr;
<<
    !*outstk!* := if atom cdr pr
                     then (pr . !*outstk!*)
                     else append(pr, !*outstk!*);
    !*currout!* := car !*outstk!*;
    !*outchanl!* := if car !*currout!*
                       then list cdr !*currout!*
                       else foreach f in cdr !*currout!*
                                   collect cdr retrieveoutputfilepair f;
    !*outstk!*
>>$

procedure popoutputstack f;
%  [close], remove top-most exact occurrence, reset vars  %
begin
scalar pr, s;
if atom f then
<<
    pr := retrieveoutputfilepair f;
    while !*outstk!* and car !*outstk!* neq pr do
        if caar !*outstk!* then
        <<s := aconc(s, car !*outstk!*);  !*outstk!* := cdr !*outstk!*>>
        else
        <<
            while car !*outstk!* neq (nil . nil) do
            <<  s := aconc(s, car !*outstk!*);
                !*outstk!* := cdr !*outstk!* >>;
                s := aconc(s, car !*outstk!*);
                !*outstk!* := cdr !*outstk!*
        >>;
    if !*outstk!* then s := append(s, cdr !*outstk!*);
    !*outstk!* := s;
    if not retrieveoutputfilepair f then close cdr pr
>>
else
<<
    pr := foreach fn in f collect retrieveoutputfilepair fn;
    while !*outstk!* and not filelistequivp(cdar !*outstk!*, f) do
        if caar !*outstk!* then
        <<  s := aconc(s, car !*outstk!*);
            !*outstk!* := cdr !*outstk!*  >>
        else
        <<
            while car !*outstk!* neq (nil . nil) do
            <<  s := aconc(s, car !*outstk!*);
                !*outstk!* := cdr !*outstk!* >>;
                s := aconc(s, car !*outstk!*);
                !*outstk!* := cdr !*outstk!*
        >>;
    if !*outstk!* then
    <<
        while car !*outstk!* neq (nil . nil) do
           !*outstk!* := cdr !*outstk!*;
        s := append(s, cdr !*outstk!*)
    >>;
    !*outstk!* := s;
    foreach fn in f do pr := delete(retrieveoutputfilepair fn, pr);
    foreach p in pr do close cdr p
>>;
!*outstk!* := !*outstk!* or list !*stdout!*;
!*currout!* := car !*outstk!*;
!*outchanl!* := if car !*currout!*
                   then list cdr !*currout!*
                   else foreach fn in cdr !*currout!*
                                collect cdr retrieveoutputfilepair fn;
return f
end$

procedure deletefromoutputstack f;
begin
scalar s, pr;
    if atom f then
    <<
        pr := retrieveoutputfilepair f;
        while retrieveoutputfilepair f do
            !*outstk!* := delete(pr, !*outstk!*);
        close cdr pr;
        foreach pr in !*outstk!* do
            if listp cdr pr and pairp cdr pr and f member cdr pr then
                rplacd(pr, delete(f, cdr pr)) % Fixed 26-2-88 mcd
    >>
    else
    <<
        foreach fn in f do
            deletefromoutputstack fn;
        foreach fn in f do
            foreach pr in !*outstk!* do
                if pairp cdr pr and fn member cdr pr then
                    rplacd(pr, delete(fn, cdr pr))
    >>;
    while !*outstk!* do
        if caar !*outstk!* and caar !*outstk!* neq 't then
        <<
            s := aconc(s, car !*outstk!*);
            !*outstk!* := cdr !*outstk!*
        >>
        else if cdar !*outstk!* and cdar !*outstk!* neq '(t) then
        <<
            while car !*outstk!* neq (nil . nil) do
            <<
                s := aconc(s, car !*outstk!*);
                !*outstk!* := cdr !*outstk!*
            >>;
            s := aconc(s, car !*outstk!*);
            !*outstk!* := cdr !*outstk!*
        >>
        else
            if cdr !*outstk!* then !*outstk!* := cddr !*outstk!*
               else !*outstk!*:=nil;
    !*outstk!* := s or list !*stdout!*;
    !*currout!* := car !*outstk!*;
    !*outchanl!* := if car !*currout!*
                       then list cdr !*currout!*
                       else foreach fn in cdr !*currout!*
                                 collect cdr retrieveoutputfilepair fn;
return f
end$


procedure retrievefilepair(fname, stk);
if null stk then
    nil
else if caar stk and mkfil fname = mkfil caar stk then
    car stk
else
    retrievefilepair(fname, cdr stk)$

procedure retrievepfilepair(f, stk);
if null stk then
    nil
else if null caar stk and filelistequivp(f, cdar stk) then
    list(car stk, (nil . nil))
else
    retrievepfilepair(f, cdr stk)$

procedure filelistequivp(f1, f2);
if pairp f1 and pairp f2 then
<<
    f1 := foreach f in f1 collect mkfil f;
    f2 := foreach f in f2 collect mkfil f;
    while (car f1 member f2) do
    <<
        f2 := delete(car f1, f2);
        f1 := cdr f1
    >>;
    null f1 and null f2
>>$


%%

procedure !*filep!* f;
   not errorp errorset(list('close,
                            list('open,list('mkfil,mkquote f),''input)),
                       nil,nil)$

%%                                     %%
%% Scanning & Arg-Conversion Functions %%
%%                                     %%


procedure endofstmtp;
if cursym!* member '(!*semicol!* !*rsqbkt!* end) then t$

procedure fargstonames(fargs, openp);
begin
scalar names;
fargs :=
    for each a in fargs conc
        if a memq '(nil 0) then
            if car !*currout!* then
                list car !*currout!*
            else
                cdr !*currout!*
        else if a eq 't then
            list car !*stdout!*
        else if a eq 'all!* then
            for each fp in !*outstk!* conc
               (if car fp and not(fp equal !*stdout!*) then list car fp)
        else if atom a then
            if openp then
            <<
                if null getd 'bpsmove and
                   % That essentially disables the test on IBM SLISP
                   % where it causes chaos with the PDS management.
                   !*filep!* a and null assoc(a, !*outstk!*) then
                    gentranerr('w, a, "OUTPUT FILE ALREADY EXISTS",
                               "CONTINUE?");
                list a
            >>
            else
                if retrieveoutputfilepair a then
                    list a
                else
                    gentranerr('w, a, "File not Open for Output", nil)
        else
            gentranerr('e, a, "WRONG TYPE OF ARG", nil);
repeat
    if not (car fargs member names) then
        names := append(names, list car fargs)
until null (fargs := cdr fargs);
return names
end$

procedure readfargs;
begin
scalar f;
while not endofstmtp() do
    f := append(f, list xread t);
return f or list nil
end$


endmodule;


end;
