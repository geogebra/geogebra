module codctl;  % Facilities for controlling the overall optimization.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, M.C. van Heerwaarden,  ;
%             J.B. van Veelen, B.L. Gates.                            ;
% ------------------------------------------------------------------- ;
% The file CODCTL.RED contains the functions defining the interface   ;
% between SCOPE and REDUCE.                                           ;
% Besides, CODCTL consists of facilities for controlling the          ;
% overall optimization process( making use of a number of global      ;
% variables and switches) and for the creation of an initial operating;
% environment for the optimization process.                           ;
% ------------------------------------------------------------------- ;

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


% ------------------------------------------------------------------- ;
% The optimization process is initialized by applying the function    ;
% INIT, designed to create the initial state of the data structures,  ;
% used to store the input, which will be subjected to a heuristic     ;
% search for common sub-expressions (cse's). INIT serves also to      ;
% restore initial settings when an unexpected termination occurs.     ;
% ARESULTS can be used to obtain the output in an algebraic list, once;
% the optimization itself is finished and only when relevant, i.e. if ;
% !*SIDREL=T, !*AGAIN or Optlang!* is NIL.                            ;
% During input translation the incidence matrix(CODMAT) is partly     ;
% made, by creating its row structure via FFVAR!!, given in the module;
% CODMAT.  Once input is processed the optimization activities are    ;
% activated by applying the function CALC.The kernel of the body of   ;
% this function is the procedure OPTIMIZELOOP. However, first the     ;
% function SSETVSARS (see CODMAT module) is applied to complete the   ;
% matrix CODMAT (column creation). The optimize-loop is a repeated    ;
% search for cse's, using facilities, defined in the modules CODOPT   ;
% and CODAD1.  During these searches different cse-names for identical;
% cse's might be created,for instance due to EXPAND- and SHRINK-      ;
% activities (see CODOPT), an inefficiency repaired via IMPROVELAYOUT ;
% (see the module CODAD1). When !*AGAIN is T  output is created       ;
% without performing the finishing touch (see CODAD2). Output is      ;
% created through the functions MAKEPREFIXL and PRIRESULT. Finally the;
% REDUCE environment, which existed before the optimization activities;
% is restored as last activity of CALC.                               ;
% ------------------------------------------------------------------- ;

symbolic$

global '(codmat endmat !*acinfo prevlst !*sidrel maxvar malst
        rowmax rowmin !*priall !*primat codbexl!* !*prefix !*again
        ops kvarlst cname!* cindex!* optlang!* gentranlang!*
        varlst!* varlst!+ !*outstk!* !*optdecs !*inputc !*vectorc
        !*intern min!-expr!-length!*)$

fluid '(!*gentranopt !*double !*period !*noequiv );

switch acinfo,sidrel,priall,primat,prefix,optdecs,again,inputc,vectorc,
       intern$

% ------------------------------------------------------------------- ;
% Initial settings for the globals.                                   ;
% ------------------------------------------------------------------- ;
codmat:=!*priall:=!*primat:=!*sidrel:=!*optdecs:=optlang!*:=nil;
!*again:=!*prefix:=!*acinfo:=!*inputc:=!*intern:=!*vectorc:=nil;
min!-expr!-length!*:=nil;
rowmin:=0; rowmax:=-1;

% ------------------------------------------------------------------- ;
% Description of global variables and switches.                       ;
% ------------------------------------------------------------------- ;
% MATRIX ACCESS:                                                      ;
%                                                                     ;
% CODMAT : is a vector used to store the +,* matrices,merged in CODMAT;
% MAXVAR : The size of this merged matrix is 2*MAXVAR.                ;
% ROWMAX : Largest actual row index.                                  ;
% ROWMIN : Smallest actual column index.                              ;
% ENDMAT : Value of MAXVAR when cse-search starts.                    ;
%                                                                     ;
% Remark - The storage strategy can be vizualized as follows:         ;
%                                                                     ;
%  MAXVAR + MAXVAR                                                    ;
%  -------|------------------------------------------------|          ;
%         | Storage left for cse's                         |          ;
%  -------|------------------------------------------------|          ;
%  MAXVAR + ROWMAX (ENDMAT when input processing completed)|          ;
%  -------|------------------------------------------------|          ;
%         | Matrix Rows:Input decomposition                |          ;
%  -------|------------------------------------------------|          ;
%  MAXVAR + 0                                              |          ;
%  -------|------------------------------------------------|          ;
%         | Matrix columns:Variable occurrence information |          ;
%  -------|------------------------------------------------|          ;
%  MAXVAR - ROWMIN                                         |          ;
%  -------|------------------------------------------------|          ;
%         | Storage left for cse-occurrence information    |          ;
%  -------|------------------------------------------------|          ;
%  MAXVAR - MAXVAR                                         |          ;
%                                                                     ;
%                                                                     ;
%  CSE-NAME SELECTION                                                 ;
%                                                                     ;
%  Cname!* : Created in INAME and exploded representation of letter-  ;
%            part of current cse-name.                                ;
%  Cindex!*: Current cse-number. If cindex!*:=Nil then GENSYM() is use;
%  Bnlst   : List of initial cse-names. When !*AGAIN=T used to save   ;
%            these names via CSES:=('PLUS.Bnlst).If necessary extended;
%            with last GENSYM-generation(see MAKEPREFIXLIST). This    ;
%            assignment statement preceeds other output and is used in;
%            FFVAR!! (see module CODMAT) to flag all old cse-names    ;
%            with NEWSYM when continuing with next set of input files.;
%                                                                     ;
%  The cse-name generation process is organized by the procedures     ;
%  INAME,NEWSYM1 and FNEWSYM. The procedure DIGITPART is needed in    ;
%  FFVAR!! (via RestoreCseInfo)  to restore the cse-name flags NEWSYM.;
%  This information is saved by SaveCseInfo (see MAKEPREFIXLST).      ;
%                                                                     ;
%  SWITCHES : THE ON-EFFECT IS DESCRIBED                              ;
%                                                                     ;
%    ACinfo   : (Evaluated) input and Operation counts displayed with-;
%               out disturbing Outfile declarations.                  ;
%    Primat   : Initial and final state of matrix CODMAT is printed.  ;
%    Priall   : Turns !*ACinfo,!*Primat on.                           ;
%    Prefix   : Output in pretty printed prefixform.                  ;
%    Again    : Optimization of partioned input will be continued a   ;
%               next time. Cse's added to prefixlist and finishing    ;
%               touch delayed.                                        ;
%    SidRel   : The Optimizer output, collected in Prefixlist, is re- ;
%               written, using the procedure EvalPart, defined in this;
%               module, resulting in a list of (common sub)expressions;
%               with PLUS or DIFFERENCE as their leading operator,    ;
%               when ever possible.                                   ;
%    Optdecs :  The output is preceded by a list of declarations.     ;
%                                                                     ;
%  REMAINING GLOBALS                                                  ;
%                                                                     ;
%  Prefixlist : Association list defining output. Built in CODPRI-part;
%               2 and used either via ASSGNPRI (ON FORT or ON/OFF NAT);
%               or via PRETTYPRINT (ON PREFIX).                       ;
% Pre-                                                                ;
% Prefixlist :  Rational exponentiations require special provisions   ;
%               during parsing, such as the production of this list of;
%               special assignments, made as side-effect of the appli-;
%               cation of the function PrepMultMat in SSetVars (see   ;
%               the module CODMAT). This list is put in front of the  ;
%               list Prefixlist.                                      ;
%  Prevlst    : Used in FFVAR!! to store information about expression ;
%               hierarchy when translating input.                     ;
%               Later used (initialized in SSETVARS) to obtain correct;
%               (sub)expression ordering.                             ;
%  Kvarlst    : Used for storing information about kernels.           ;
%  Optlang!*  : Its value ('FORTRAN, 'C, for instance) denotes the    ;
%               target language selection for the output production.  ;
%  CodBexl!*  : List consisting of expression recognizers. It guaran- ;
%               tees a correct output sequence. Its initial structure ;
%               is built in FFVAR!! and modified in IMPROVELAYOUT,for ;
%               instance, when superfluous intermediate cse-names are ;
%               removed.                                              ;
% ------------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
% Some GENTRAN modules  are required to obtain a correct interface.   ;
% The file names are installation dependent.                          ;
% ------------------------------------------------------------------- ;

%IN "$gentranutil/sun-gentran-load"$
 load!-package 'gentran$  % Moet worden gentran90 !!

% Load and initialize rounded-package
if not !*rounded then << on 'rounded; off 'rounded >>;


% ------------------------------------------------------------------- ;
% PART 1 : Interface between Scope and Reduce.                        ;
% ------------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
%   ALGEBRAIC MODE COMMAND PARSER                                     ;
% ------------------------------------------------------------------- ;

put('optimize, 'stat, 'optimizestat);


global '(optlang!* avarlst known rhsaliases);
fluid '(!*fort preprefixlist prefixlist);

symbolic expr procedure optimizestat;
    % --------------------------------------------------------------- ;
    %  OPTIMIZE command parser.                                       ;
    % --------------------------------------------------------------- ;
    begin scalar forms, vname, infiles, outfile, x, decs, kwds, delims;
    symtabrem('!*main!*,'!*decs!*);
    kwds := '(iname in out declare);
    delims := append(kwds, '(!*semicol!* !*rsqb!* end));
    flag(kwds, 'delim);
    while not memq(cursym!*, delims) do
        if (x := xreadforms()) then
            forms := append(forms, x);
    while memq(cursym!*, kwds) do
        if eq(cursym!*, 'iname) then
            vname := xread t
        else if eq(cursym!*, 'in) then
            if atom (x := xread nil) then
                infiles := list x
            else if eqcar(x, '!*comma!*) then
                infiles := cdr x
            else
                infiles := x
        else if eq(cursym!*, 'out) then
            outfile:=xread t
        else if eq(cursym!*, 'declare) then
            decs := append(decs, cdr declarestat());
    remflag(kwds, 'delim);
    return list('symoptimize, mkquote forms,
                              mkquote infiles,
                              mkquote outfile,
                              mkquote vname,
                              mkquote decs)
    end;

% ------------------------------------------------------------------- ;
%   ALGEBRAIC MODE OPERATOR ALGOPT                                    ;
% ------------------------------------------------------------------- ;

symbolic procedure algopteval u;
% ------------------------------------------------------------------- ;
% Algebraic mode interface in the form of a function-application. The ;
% procedure algresults1 is used for result production.                ;
% u = list of the form : (forms, filesnames, csename). The arguments  ;
% are optional.                                                       ;
% forms is a list of eq's, defining pairs (lhs-name,rhs-value),       ;
% filenames is a list of filenames, containing symtactically correct  ;
% input and the csename, optional too, is the initial cse-name part,  ;
% a scalar.                                                           ;
% --------------------------------------------------------------------;
begin
  scalar su,res,intern!*; integer nargs;
  intern!*:=!*intern; !*intern:='t;
  nargs := length u;
  u:=foreach el in u collect
   if listp(el) and eqcar(el,'list) and allstring(cadr el)
    then cdr(el) else el;
  if listp(car u) and not(allstring car u) and not(eqcar(car u,'list))
   then u:=list('list,car u).cdr u;
  res :=
   if nargs = 1
    then if su:=allstring(car u)
          then symoptimize(nil,su,nil,nil,nil)
          else symoptimize(car u,nil,nil,nil,nil)
    else if nargs = 2
     then if su:=allstring(cadr u)
           then symoptimize(car u,su,nil,nil,nil)
           else if (su:=allstring(car u)) and atom cadr u
            then symoptimize(nil,su,nil,cadr u,nil)
            else if atom cadr u
             then symoptimize(car u,nil,nil,cadr u,nil)
             else '!*!*error!*!*
    else if nargs = 3 and (su:=allstring cadr u)
     then symoptimize(car u,su, nil, caddr u,nil)
     else '!*!*error!*!*;
   !*intern:=intern!*;
   if eq(res,'!*!*error!*!*)
    then rederr("SYNTAX ERROR IN ARGUMENTS ALGOPT")
    else return algresults1(foreach el in res
                                   collect cons(cadr el,caddr el))
   end;

put ('algopt,'psopfn,'algopteval);

symbolic procedure allstring s;
% ------------------------------------------------------------------- ;
% Consists s of one are more filenames?                               ;
% ------------------------------------------------------------------- ;
if atom s
 then if stringp s then list(s)
                   else nil
else if not(nil member foreach el in s collect stringp el) then s
                                                           else nil;


% ------------------------------------------------------------------- ;
%   SYMBOLIC MODE PROCEDURE                                           ;
% ------------------------------------------------------------------- ;

global '(!*algpri !*optdecs)$
switch algpri,optdecs$
!*optdecs:=nil$

symbolic expr procedure symoptimize(forms,infiles,outfile,vname,decs);
    % --------------------------------------------------------------- ;
    %  Symbolic mode function.                                        ;
    % --------------------------------------------------------------- ;
    begin scalar algpri,echo,fn,forms1,optdecs, comdecs;
    echo:=!*echo;
    eval list('off, mkquote list 'echo);
    if infiles then
        forms := append(forms, files2forms infiles);
    algpri := !*algpri;
    !*echo:=echo;
    if decs
       then << optdecs:=!*optdecs; !*optdecs:=t;
               % JB 31/3/94 Fixed to deal with complex input:
              if (comdecs:=assoc('complex, decs)) or
                 (comdecs:=assoc('complex!*16, decs))
                 then <<if not freeof(comdecs,'i)
                           then forms:= '(setq i (sqrt (minus 1)))
                                   . forms;

                      >>
             >>;
    eval list('off, mkquote list 'algpri);
    if vname then iname vname;
    forms := analyse_forms(forms);
    !*algpri := algpri;
    preproc1 ('declare . decs);
%   prefixlist:=segmentation_if_needed(forms,outfile,vname);
    prefixlist:=
           eval formoptimize(list('optimizeforms,forms,outfile,vname),
                             !*vars!*,
                             !*mode);
    if decs then !*optdecs:=optdecs;

    if !*intern
     then return (foreach el in prefixlist
                   collect list('setq,car el,cdr el))
  end$

symbolic expr procedure symoptimize(forms,infiles,outfile,vname,decs);
    % --------------------------------------------------------------- ;
    %  Symbolic mode function.                                        ;
    % --------------------------------------------------------------- ;
    begin scalar algpri,echo,fn,forms1,optdecs,comdecs;

    echo:=!*echo;
    eval list('off, mkquote list 'echo);
    if infiles then
        forms := append(forms, files2forms infiles);
    algpri := !*algpri;
    !*echo:=echo;
    if decs
       then <<optdecs:=!*optdecs; !*optdecs:=t; >>;
    eval list('off, mkquote list 'algpri);
    if vname then iname vname;
    forms := analyse_forms(forms);
    !*algpri := algpri;
    preproc1 ('declare . decs);
    prefixlist:=
           eval formoptimize(list('optimizeforms,forms,outfile,vname),
                             !*vars!*,
                             !*mode);
    if decs then !*optdecs:=optdecs;
            %else !*gendecs:=optdecs;

    if !*intern
     then return (foreach el in prefixlist
                   collect list('setq,car el,cdr el))
  end$


symbolic procedure analyse_forms(forms);
% --------------------------------------------------------------------;
% forms is recursively analysed and replaced by a flattened list of   ;
% items, which are either of the form ('setq lhs rhs) or have the     ;
% structure ('equal lhs rhs).
% Here lhs can be a scalar, a matrix or an array identifier.          ;
% The rhs is a REDUCE expression in prefix form. During the analysis  ;
% scalar, matrix or array identifier elements of the list forms are   ;
% replaced by the prefix equivalent of their algebraic value, which is;
% assumed to be a list of equations of the form                       ;
%  {lhs1=rhs1,...,lhsn=rhsn}.                                         ;
% Similarly elements of forms, being function-applications (either    ;
% symbolic operators or psopfn facilities), evaluable to lists of the ;
% above mentioned structure, are replaced by their evaluations.       ;
% ------------------------------------------------------------------- ;
begin scalar fn,res,forms1;
if atom(forms) then forms:=list(forms)
else if (listp(forms) and get(car forms,'avalue)
        and car(get(car forms,'avalue)) member '(array matrix))
 then forms:=list(forms)
else if listp forms and eqcar(forms,'list) then forms:=cdr forms;
res:=
 foreach f in forms conc
  if atom(f) and car(get(f,'avalue))='list then cdr reval f
  else if listp(f) and get(car f,'avalue) and
          car(get(car f,'avalue)) member '(array matrix)
   then cdr reval f
  else if listp(f) and eqcar(f,'list) then list f
  else if listp(f) and eqcar(f,'equal) and eqcar(caddr f,'!*sq)
      then list list('equal,cadr f,sq2pre caddr f)
  else if listp(f) and
          not member(car f,'(equal lsetq lrsetq rsetq setq))
   then <<forms1:=
            apply(if fn:=get(car f,'psopfn) then fn else car f,
                  if get(car f,'psopfn)
                    then list(foreach x in cdr f collect x)
                    else foreach x in cdr f collect x);
          if pairp(forms1) and eqcar(forms1,'list)
           then cdr forms1 else forms1
         >>
  else list f;
return foreach f in res conc
 if listp(f) and eqcar(f,'list) then analyse_forms(cdr f) else list f
end;

symbolic expr procedure xreadforms;
    begin scalar x;
    x := xread t;
    if listp x and eqcar(x, 'list) then
        return flattenlist x
    else if x then
        return list x
    else
        return x
    end;

symbolic expr procedure flattenlist x;
    if atom(x) or constp(x) then
      x
    else
    << if eqcar(x, 'list) then
         foreach y in cdr x collect flattenlist y
       else
         x
    >>;

symbolic expr procedure files2forms flist;
    begin scalar ch, holdch, x, forms;
    holdch := rds nil;
    forms := nil;
    foreach f in flist do
    <<
        ch := open(mkfil f, 'input);
        rds ch;
        while (x := xreadforms()) do
            forms := append(forms, x);
        rds holdch;
        close ch
    >>;
    return forms
    end;


symbolic expr procedure formoptimize(u, vars, mode);
    car u . foreach arg in cdr u
                collect formoptimize1(arg, vars, mode);

symbolic procedure chopchop rep;
% rep : m . e;
% no trailing zeros in m; e < 0.
% rep is the cdr-part of a (!:rd!: !:cr!: !:crn!: !:dn!:)-notation.
if length(explode abs car rep)> !!rdprec
   then begin
          scalar sgn,restlist,lastchop,exppart;
          restlist:=reverse explode abs(car rep);
          sgn:=(car rep < 0);
          exppart:= cdr rep;
          while length(restlist) > !!rdprec
                do << lastchop:=car restlist;
                      restlist:=cdr restlist;
                      exppart:=exppart+1 >>;
          restlist:= compress reverse restlist;
          if compress list lastchop >= 5
             then restlist:=restlist + 1;
          return (if sgn then -1*restlist else restlist) . exppart;
          end
   else rep;

symbolic expr procedure formoptimize1(u, vars, mode);
 if constp u
    then mkquote u % JB 30/3/94.
                   % Constants are not neccesarily atoms.
    else
 if atom u
    then mkquote u
    else if member(car u,'(!:rd!: !:cr!: !:crn!: !:dn!:))
            then % JB 31/3/94 This seems to work. Honestly
                 % stolen from formgentran.
                 mkquote <<%precmsg length explode abs car(u := cdr u);
                           u:=chopchop cdr u;
                           decimal2internal(car u,cdr u)>>
    else if eq(car u,'!:int!:)
            then mkquote cadr u
    else if eqcar(u, 'eval) then
            list('sq2pre, list('aeval, form1(cadr u, vars, mode)))
    else if car u memq '(lsetq rsetq lrsetq) then
        begin scalar op, lhs, rhs;
        op := car u;
        lhs := cadr u;
        rhs := caddr u;
        if op memq '(lsetq lrsetq) and listp lhs then
            lhs := car lhs . foreach s in cdr lhs
                                collect list('eval, s);
        if op memq '(rsetq lrsetq) then
            rhs := list('eval, rhs);
        return formoptimize1(list('setq, lhs, rhs), vars, mode)
        end
    else
        ('list . foreach elt in u
                        collect formoptimize1(elt, vars, mode));

symbolic expr procedure sq2pre f;
    if atom f then
        f
    else if listp f and eqcar(f, '!*sq) then
        prepsq cadr f
    else
        prepsq f;

% ------------------------------------------------------------------- ;
%   CALL CODE OPTIMIZER                                               ;
% ------------------------------------------------------------------- ;

symbolic procedure optimizeforms(forms,outfile,vname);
begin
  scalar noequiv,double,period,ch,fort,holdch,optlang,primat,
         acinfo,inputc;
  period:=!*period; !*period:=nil;   % No periods in subscripts please.
  noequiv:=!*noequiv; !*noequiv:=t;  % No equivalence check, see coddom
  double:=!*double;
  put('!:rd!:,'zerop,'rd!:zerop!:);  % New zerop which respects
                                     % precision-setting, onep is o.k.
  if vname and not(getd('newsym)) then iname vname;
  if !*fort then << fort:=t;!*fort:=nil;
                    optlang:=optlang!*; optlang!*:='fortran>>;
  if outfile then
  << if not(optlang!*)
      then << holdch:=wrs nil;               % get old output channel
              if ch:=assoc(intern outfile,!*outstk!*)
               then ch:=cdr ch
               else ch:=open(mkfil outfile,'output);
              wrs ch                         % set output channel to ch
           >>
      else eval list('gentranoutpush,list('quote,list(outfile)))
  >>;
  if !*priall     % Save previous flag configuration.
   then << primat:=!*primat; acinfo:=!*acinfo; inputc:=!*inputc;
           !*primat:=!*acinfo:=!*inputc:=t >>;

  prefixlist:=calc forms;

  if !*priall then               % Restore original flag configuration.
  << !*primat:=primat; !*acinfo:=acinfo; !*inputc:=inputc >>;
  if outfile then
  << if not(optlang!*)
      then
       << if (not(!*nat) or !*again) then write ";end;";
          % Restore output channel
          if assoc(intern outfile,!*outstk!*)
            then <<terpri(); wrs holdch>> else <<wrs holdch; close ch>>
       >>
      else eval '(gentranpop '(nil))
  >>;
  if fort then << !*fort:=t; optlang!*:=optlang>>;
  put('!:rd!:,'zerop,'rd!:zerop);
  !*double:=double; !*noequiv:=noequiv; !*period := period;
  return prefixlist;
end;

symbolic procedure opt forms;
    % --------------------------------------------------------------- ;
    %  Replace each sequence of one or more assignment(s) by its      ;
    %  optimized equivalent sequence.                                 ;
    % --------------------------------------------------------------- ;
    begin scalar seq, res, fort, optlang;
        fort:=!*fort;
        !*fort:=nil;
        optlang:=optlang!*;
        optlang!*:=gentranlang!*;
        if atom forms then
            res := forms
        else if eqcar(forms, 'setq) then
        <<
            res := foreach pr in optimizeforms(list forms, nil, nil)
                      collect list('setq, car pr, cdr pr);
            if onep length res
                then res := car res
                else res := mkstmtgp(0, res)
         >>
        else if atom car forms then
            res := (car forms . opt cdr forms)
        else
        <<
            seq := nil;
            while forms and listp car forms and eqcar(car forms, 'setq)
               do <<seq := (car forms . seq); forms := cdr forms>>;
            if seq then
            <<seq := foreach pr in optimizeforms(reverse seq, nil, nil)
                        collect list('setq, car pr, cdr pr);
              if length seq > 1 then
                  seq := list mkstmtgp(0, seq);
              res := append(seq, opt forms)
            >>
            else
                res := (opt car forms . opt cdr forms);
        >>;
        optlang!*:=optlang;
        !*fort:=fort;
        return res;
    end;


% ------------------------------------------------------------------- ;
% PART 2 : Control of overall optimization process.                   ;
% ------------------------------------------------------------------- ;

symbolic procedure init n;
% ------------------------------------------------------------------- ;
% arg: Size of the matrix N.                                          ;
% eff: Initial state (re)created by (re)initializing the matrix CODMAT;
%      and some related identifiers.                                  ;
% ------------------------------------------------------------------- ;
begin scalar var;
  for y:=rowmin:rowmax do
  if row(y) and not numberp(var:=farvar y)
  then
  <<remprop(var,'npcdvar); remprop(var,'nvarlst);
    remprop(var,'varlst!+); remprop(var,'varlst!*);
    remprop(var,'rowindex);
    remprop(var,'nex);
    remprop(var,'inlhs);
    remprop(var,'rowocc);
    remprop(var,'kvarlst);
    remprop(var,'alias);remprop(var,'finalalias);
    remprop(var,'aliaslist);remprop(var,'inalias);
  >>;
  if maxvar=n
    then for x:=0:2*n do putv(codmat,x,nil)
    else codmat:=mkvect(2*n);
  if kvarlst then foreach item in kvarlst do
  << remprop(cadr item,'kvarlst);
     remprop(cadr item,'nex)
  >>;
  foreach item in '(plus minus difference times expt sqrt) do
                                               remprop(item,'kvarlst);
  %-------------------------------------------------------------------
  % If not all algresults were reversed by the user, by means of
  % `restorall', or `arestore', they become irreversible commited
  % after the following resetting of `avarlst'.
  %-------------------------------------------------------------------
  %bnlst:=
   varlst!*:=varlst!+:=prevlst:=kvarlst:=codbexl!*:=avarlst:=nil;
  malst:=preprefixlist:=nil; prefixlist:=nil;
  rowmax:=-1; maxvar:=n;
  rowmin:=0;
  ops:=list(0,0,0,0)
end;


symbolic procedure calc forms;
% ------------------------------------------------------------------- ;
% CALC produces,via OPTIMIZELOOP, the association list PREFIXLIST.    ;
% This list is used for output production by apllying PRIRESULT.      ;
% ------------------------------------------------------------------- ;
begin scalar fil;
  init 200;
  prefixlist:=rhsaliases:=nil;
  forms := preremdep forms;
  foreach item in forms do
          prefixlist:=ffvar!!(cadr item, caddr item, prefixlist);
  preprefixlist:=ssetvars(preprefixlist); % Complete parsing.
  fil:=wrs(nil);              % Save name output file,which has to be ;
                              % used for storing the final results    ;
  if !*primat then primat();
  if !*acinfo then countnop(reverse prefixlist,'input);
  optimizeloop();
  terpri();
  wrs(fil);
  prefixlist:=makeprefixl(preprefixlist,nil);
  if !*gentranopt
     then typeall(prefixlist)
     else if not !*intern
             then priresult(prefixlist);
  fil:=wrs(nil);
  if getd('newsym) then remd('newsym); %bnlst:=nil;
  if !*acinfo then << countnop(reverse prefixlist,'output); terpri()>>;
  if !*primat
  then << for x:=rowmin:rowmax do if farvar(x)=-1 or farvar(x)=-2
                                   then setoccup(x) else setfree(x);
           primat();
       >>;
  wrs(fil);
  return prefixlist
end$


% ------------------------------------------------------------------- ;
% Reduce interface for CALC, allowing the command CALC instead of     ;
% CALC().                                                             ;
% ------------------------------------------------------------------- ;

% put('calc,'stat,'endstat);


symbolic procedure pprintf(ex,nex);
% --------------------------------------------------------------------;
% arg : The name Nex of an expression Ex.                             ;
% eff : Nex:=Ex is printed using assgnpri on the output medium without;
%       disturbing the current file management and output flagsettings;
% --------------------------------------------------------------------;
begin scalar s,fil,nat;
  terpri();
  fil:=wrs(nil);
  if not(!*nat) then << nat:=!*nat; s:=!*nat:=t>>;
  assgnpri(ex,list nex,'last);
  wrs(fil);
  if s then !*nat:=nat
end;

symbolic procedure optimizeloop;
% ------------------------------------------------------------------- ;
% Iterative cse-search.                                               ;
% ------------------------------------------------------------------- ;
begin scalar b1,b2,b3,b4;
  repeat
  << extbrsea();
    % --------------------------------------------------------------- ;
    % Extended Breuer search (see module CODOPT):                     ;
    % Common linear expressions or power products are heuristically   ;
    % searched for using methods which are partly based on Breuer's   ;
    % grow factor algorithm.                                          ;
    % --------------------------------------------------------------- ;
    b1:=improvelayout();
    % --------------------------------------------------------------- ;
    % Due to search strategy, employed in EXTBRSEA, identical cse's   ;
    % can have different names. IMPROVELAYOUT (see module CODAD1 is   ;
    % used to detect such situations and to remove double names.      ;
    % --------------------------------------------------------------- ;
    b2:=tchscheme();
    % --------------------------------------------------------------- ;
    % Migration of information, i.e. the newly generated cse-names for;
    % linear expressions occuring as factor in a product are transfer-;
    % red from the + to the * scheme. Similar operations are performed;
    % for power products acting as terms. File CODAD1.RED contains    ;
    % TCHSCHEME.                                                      ;
    % --------------------------------------------------------------- ;
    b3:=codfac();
    % --------------------------------------------------------------- ;
    % Application of the distributive law,i.e. a*b + a*c is changed in;
    % a*(b + c) and expression storage in CODMAT is modified according;
    % ly. File CODAD1.RED contains CODFAC.                            ;
    % --------------------------------------------------------------- ;
    b4:=searchcsequotients();
  >>
  until not(b1 or b2 or b3 or b4);
end;

symbolic procedure countnop(prefixlst,io);
% ------------------------------------------------------------------- ;
% The number of +/-, unary -, *, integer ^, / and function applica-   ;
% tions is counted in prefixlist, consisting of pairs (lhs.rhs). Array;
% references are seen as function applications if the array name is   ;
% not contained in the symbol table. The content of the symbol table  ;
% is prescribed through the declare-option of the optimize-command,   ;
% i.e. when io='input, and posibly modified after optimization, i.e.  ;
% when io='output.                                                    ;
% ------------------------------------------------------------------- ;
begin scalar totcts;
 totcts:='(0 0 0 0 0 0);
 foreach item in prefixlst do
 << if pairp(car item) then totcts:=counts(car item,totcts,nil);
    totcts:=counts(cdr item,totcts,nil)
 >>;
 terpri();
 if io eq 'input
  then write "Number of operations in the input is: "
  else write "Number of operations after optimization is:";
 terpri(); terpri();
 write "Number of (+/-) operations      : ",car totcts; terpri();
 write "Number of unary - operations    : ",cadr totcts; terpri();
 write "Number of * operations          : ",caddr totcts; terpri();
 write "Number of integer ^ operations  : ",cadddr totcts; terpri();
 write "Number of / operations          : ",car cddddr totcts;terpri();
 write "Number of function applications : ",car reverse totcts;terpri()
end;

symbolic procedure counts(expression,locs,root);
% ------------------------------------------------------------------- ;
% The actual counts are recursively done with the function counts by  ;
% modifying the value of the 6 elements of locs.  The elements of locs;
% define the present number of the 6 possible categories of operators,;
% which we distinguish.                                               ;
% ------------------------------------------------------------------- ;
begin scalar n!+,n!-,n!*,n!^,n!/,n!f,tlocs,loper,operands;
 if idp(expression) or constp(expression)
  then tlocs:=locs
  else
   << n!+:=car locs; n!-:=cadr locs; n!*:=caddr locs; n!^:=cadddr locs;
      n!/:=car cddddr locs; n!f:= car reverse locs;
      loper:=car expression; operands:=cdr expression;
      if loper memq '(plus difference)
       then n!+:=(length(operands)-1)+n!+
       else
        if loper eq 'minus
         then (if root neq 'plus then n!-:=1+n!-)
         else
          if loper eq 'times
           then n!*:=(length(operands)-1)+n!*
           else
            if loper eq 'expt
             then (if integerp(cadr operands)
                   then n!^:=1+n!^ else n!f:=1+n!f)
             else
              if loper eq 'quotient
               then n!/:=1+n!/
               else
                if not(subscriptedvarp(loper))
                 then n!f:=1+n!f;
      tlocs:=list(n!+,n!-,n!*,n!^,n!/,n!f);
      foreach op in operands do tlocs:=counts(op,tlocs,loper)
   >>;
 return(tlocs)
end;

symbolic procedure complex!-i!-init!-statement st;
%
% See if we need to initialize i.
%
begin scalar tl, res;
  tl:=formtypelists symtabget('!*main!*,'!*decs!*);
  foreach el in tl do
       <<if member(car el,
                   '(complex implicit! complex implicit! complex!*16))
            and member('i, el)
            then res :=
               if !*double
                 then if st then "i=(0.0D0, 1.0D0)"
                        else '((literal tab!* "I=(0.0D0, 1.0D0)" cr!*))
                else if st then "i=(0.0, 1.0)"
                else '((literal tab!* "I=(0.0, 1.0)" cr!*))
        >>;
   return res;
   end;

symbolic procedure priresult(prefixlist);
% ------------------------------------------------------------------- ;
% Besides flag settings and the like the essential action is printing.;
% ------------------------------------------------------------------- ;
begin scalar pfl,nat,istat;
  if !*optdecs then typeall prefixlist;

  if optlang!*
  then << if null(assoc('e,prefixlist)) then symtabrem(nil,'e);
          pfl := foreach pr in prefixlist collect
                    list('setq, car pr,lispcodeexp(cdr pr,!*period));
          if (istat:=complex!-i!-init!-statement(nil))
             then pfl := append(istat, pfl);
          pfl := list mkstmtgp(0, pfl);
          apply1(get(optlang!*, 'formatter),
                 apply1(get(optlang!*, 'codegen), pfl));
       >>
  else if !*prefix
       then << write "Prefixlist:=";
               terpri();
               prettyprint(prefixlist)
            >>
       else << if !*optdecs then printdecs();
               if (istat:=complex!-i!-init!-statement('t))
                  then <<write caddar istat;terpri()>>;
               if not !*again
               then
                  foreach item in prefixlist do
                     assgnpri(cdr item,list car item,'last)
               else
               << nat:=!*nat; !*nat:=nil;
                  assgnpri(append(list('list),
                           for each item in prefixlist
                            collect list('setq,car item,cdr item)),
                         nil,'last);
                  !*nat:=nat;
                  terpri();%write ";end;";  % done by nat being off.
                                            % JB 15/3/94
               >>
            >>
end;

symbolic procedure printdecs;
% ------------------------------------------------------------------- ;
% A list of declarations is printed.                                  ;
% ------------------------------------------------------------------- ;
begin
   scalar typ;
   terpri!* t;
   for each typelist in formtypelists symtabget('!*main!*, '!*decs!*)
   do << if !*double then
         << typ:=assoc(car typelist,
                   '((real . double! precision) (complex . complex!*16)
                    (implicit! real . implicit! double! precision)
                    (implicit! complex . implicit! complex!*16)));
            typ:=if null typ then car typelist else cdr typ
         >>
         else
            typ:=car typelist;
         prin2!* typ;
         prin2!* " ";
         inprint('!*comma!*, 0, cdr typelist);
         terpri!* t
      >>
end;

global '(!*ftch);
switch ftch;
!*ftch:='t;

symbolic procedure makeprefixl(pp,prefixlist);
% ------------------------------------------------------------------- ;
% If the finishing touch is appropriate, i.e. if OFF AGAIN holds      ;
% PREPFINALPLST is called before producing PREFIXLIST using a FOREACH ;
% statement. If the optimization attempts have to be continued during ;
% another session(i.e. ON AGAIN) SAVECSEINFO is called to guarantee   ;
% all relevant cse-information to be saved.                           ;
% ------------------------------------------------------------------- ;
begin scalar b,kvl,nex,xx;
  if not(!*again)
     then prepfinalplst();
  for x:=0:rowmax do setfree(x);

  kvl:=kvarlst;

  foreach bex in reverse(codbexl!*) do
  <<if numberp(bex)                           % --------------------- ;
    then prefixlist:=prfexp(bex,prefixlist)   % Leading operator is   ;
                                              %  ^,*,+ or - .         ;
    else prefixlist:=prfkvar(bex,prefixlist); % Another leading       ;
                                              %  operator.            ;
  >>;                                         % --------------------- ;
  % ----------------------------------------------------------------- ;
  % Possibly, information about primitive factors of the form         ;
  % ('EXPT <identifier> <rational exponent>) as given in the list     ;
  % PrePrefixlist is put in front of Prefixlist.                      ;
  % ----------------------------------------------------------------- ;
  kvarlst:=kvl;
  prefixlist:=reverse prefixlist;
  if !*optdecs or !*gentranopt then
     prefixlist:=removearraysubstitutes(prefixlist);
  prefixlist:=cleanupprefixlist(prefixlist);
  if !*sidrel then prefixlist:=evalpartprefixlist(prefixlist);
  if !*again then prefixlist:=savecseinfo(prefixlist);
  return prefixlist
end$

global '(!*min!-expr!-length!*)$
!*min!-expr!-length!*:=nil$

symbolic procedure prepfinalplst;
% ------------------------------------------------------------------- ;
% The refinements defined by this procedure - the socalled finishing  ;
% touch - are only applied directly before producing the final version;
% of the output, i.e. the optimized version of the input.             ;
% These refinements are:                                              ;
% - POWEROFSUMS (see module CODAD2): Replace (a+b+...)^intpower by    ;
%                                   cse1=(a+b+...),cse1^intpower.     ;
% - CODGCD     (see module CODAD2): Replace 4.a+2.b+2.c+4.d by        ;
%                                   2.(2.(a+d)+b+c),where a,b,c,d can ;
%                                   be composite as well.             ;
% - REMREPMULTVARS (see   CODAD2) : Replace 3.a+b,3.a+c by            ;
%                                   cse3=3.a,cse3+b,cse3+c.           ;
% - UPDATEMONOMIALS (see  CODAD2) : Replace 3.a.b, 3.a.c., 6.a.d,     ;
%                                   6.a.f by                          ;
%                                   cse4=3.a, cse4.b, cse4.c, cse5=6.a;
%                                   cse5.d, cse5.f.                   ;
% ------------------------------------------------------------------- ;
begin scalar n;
  if (!*vectorc or !*sidrel or not !*ftch
               or not null(min!-expr!-length!*)) % HvH 8/11/94
     then  codgcd()
     else << repeat
             << n:=rowmax;
                powerofsums();
                remrepmultvars();
                updatemonomials();
                codgcd();
                if not(n=rowmax) then optimizeloop()
             >> until n=rowmax;
             preppowls()
          >>;
  if not !*ftch and optlang!*='c then preppowls()
  % ----------------------------------------------------------------- ;
  % PREPPOWLS (see module CODPRI, part 2) serves to create addition   ;
  % chains for integer powers, such as cse1^intpower (due to          ;
  % POWEROFSUMS) and cse4=a^3 (produced by UPDATEMONOMIALS).          ;
  % ----------------------------------------------------------------- ;
end;

symbolic procedure savecseinfo(prefixlist);
% ------------------------------------------------------------------- ;
% If ON AGAIN then cse-information have to be saved. This is done by  ;
% extending PREFIXLIST resulting in:                                  ;
% ((CSES.cses) (GSYM.gsym) PREFIXLIST) or                             ;
% ((CSES.cses) (BINF.binf) PREFIXLIST).                               ;
% Here                                                                ;
% CSES=first cse nsme[+...+ last cse name],                           ;
% GSYM=GENSYM(), if GENSYM has been used for cse-name generation,     ;
%      because we do not want to generate identical cse-names during a;
%      next run when using GENSYM.                                    ;
%      If GENSYM is not used then we create                           ;
% BINF=first initial cse-name[+...+ last initial cse-name],thus saving;
%      the Bnlst.                                                     ;
% ------------------------------------------------------------------- ;
begin scalar cses,gsym,binf;
 foreach item in prefixlist do
  if pairp(item) and flagp( car(item),'newsym)
    then cses:=car(item).cses;
  if pairp(cses) then if cdr(cses) then cses:='plus.cses
                                   else cses:=car cses;
  prefixlist:=('cses.cses).prefixlist;
  return if cses
            then ('gsym . fnewsym()) . prefixlist
            else ('gsym . gensym()) . prefixlist
end;

symbolic operator iname;

symbolic procedure iname(nm);
% ------------------------------------------------------------------- ;
% Construction of initial cse-name, extension of Bnlst and creation of;
% NEWSYM procedure via MOVD and using NEWSYM1.                        ;
% If, for instance, the initial name is aa55 then NEWSYM1 generates   ;
% aa55, aa56 , aa57, etc.                                             ;
% ------------------------------------------------------------------- ;
  begin scalar digitl,dlst,nb,dg,initname;
      digitl:='((!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4) (!5 . 5)
                (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9) (!0 . 0));
      cname!*:=nil;
      dlst:=reverse explode nm;
      repeat
      <<if (dg:=(assoc(car dlst,digitl))) and numberp (dg:=cdr dg)
         then << dlst:=cdr dlst;
                 nb:= dg.nb >>
         else << cname!*:=reverse dlst;
                 cindex!*:=0;
                 dg:=length(nb);
                 for i:=1:dg do
                  <<cindex!*:=10*cindex!*+car(nb);
                    nb:=cdr(nb)>> >>
      >>
      until cname!* or null(dlst);
      if not getd('newsym) then movd('newsym,'newsym1);
      % ------------------------------------------------------------- ;
      % Bnlst is empty if INAME is used for the first time, i.e. if   ;
      % NEWSYM has to be identified with NEWSYM1.                     ;
      % ------------------------------------------------------------- ;
      initname:=newsym();
      cindex!*:=cindex!*-1;
%      bnlst:=initname.bnlst
end;

symbolic procedure movd(tod,fromd);
% ------------------------------------------------------------------- ;
% Transfer of a procedure description from Fromd to Tod.              ;
% ------------------------------------------------------------------- ;
begin scalar s;
  s:=getd(fromd);
  putd(tod,car s,cdr s);
end;

symbolic procedure newsym1();
% ------------------------------------------------------------------- ;
% Global variables:                                                   ;
% cname!* is exploded letterpart of current cse-name.                 ;
% cindex!* is current cse-index.                                      ;
% ------------------------------------------------------------------- ;
  begin scalar x;
        x:=explode cindex!*;
        cindex!*:=cindex!*+1;
        return compress append(cname!*,x)
  end;

symbolic procedure fnewsym;
begin scalar x;
  if getd('newsym)
   then x:=newsym()
   else << x:=gensym();
           x:=compress(append(explode(letterpart(x)),
                              explode(digitpart(x))))
        >>;
   x:=intern(x); % May be necessary for some REDUCE systems;
  flag(list x,'newsym);
  return x;
end;

symbolic procedure letterpart(name);
% ------------------------------------------------------------------- ;
% Eff: Letterpart of Name returned,i.e. aa of aa55.                   ;
% ------------------------------------------------------------------- ;
begin scalar digitl,letters,el;
    digitl:='((!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4) (!5 . 5)
                (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9) (!0 . 0));
    letters:=reverse explode name;
    while (el := assoc(car letters,digitl)) and numberp cdr el do
      << letters:=cdr letters >>;
    return intern compress reverse letters;
end;

symbolic procedure digitpart(name);
% ------------------------------------------------------------------- ;
% Eff: Digitpart of Name returned,i.e. 55 of aa55.                    ;
% ------------------------------------------------------------------- ;
begin scalar digitl,nb,dg,dlst;
   digitl:='((!1 . 1) (!2 . 2) (!3 . 3) (!4 . 4) (!5 . 5)
                (!6 . 6) (!7 . 7) (!8 . 8) (!9 . 9) (!0 . 0));
   dlst:= reverse explode name;
   nb:=nil;
   while (dg:=assoc(car dlst,digitl)) and numberp(dg := cdr dg) do
     << dlst:=cdr dlst; nb:=dg.nb >>;
   dg:=0;
   foreach digit in nb do dg:=10*dg+digit;
   return dg;
 end;

endmodule;

end;

