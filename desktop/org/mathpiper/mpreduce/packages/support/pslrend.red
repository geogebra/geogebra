module pslrend; % PSL REDUCE "back-end".

% Authors: Martin L. Griss and Anthony C. Hearn.

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


% Except where noted, this works with both PSL 3.2 and PSL 3.4.

create!-package('(pslrend psl fastmath fastmod),'(build));

fluid '(!*break
        !*echo
        !*eolinstringok
        !*fastcar
        !*fulltrace
        !*gc
        !*int
        !*lower
        !*mode
        !*pgwd
        !*plap
        !*pwrds
        !*rlisp88
        !*usermode
        !*verboseload
        currentreadmacroindicator!*
        currentscantable!*
%       current!-modulus
        errout!*
        lispscantable!*
        lispsystem!*
        promptstring!*
        rlispscantable!*);

global '(!$eol!$
         !*extraecho
         !*loadversion
         !*raise
         charassoc!*
         crbuflis!*
         crchar!*
         date!*
         esc!*
         ifl!*
         ipl!*
         largest!-small!-modulus
         ofl!*
         spare!*
         statcounter
         version!*
         symchar!*);

compiletime global '(cr!* ff!* tab!*);

switch break,gc,usermode,verboseload;

switch plap; % pgwd,pwrds;

flag('(define!-constant),'eval);

% Encode information about underlying system.

compiletime define!-constant(system_list!*, 'psl . system_list!*);

lispsystem!* := system_list!*;

compiletime (lispsystem!* := system_list!*); % for this module

!*fastcar := t;   % Since REDUCE doesn't use car and cdr on atoms.

% Control of character case.

% The PSL switch "raise" means really "fold input uniformly to the
% standard character case defined by the system", that is upper case
% for PSL 3.4 and lower case for PSL 4.2.

fluid '(!*!*low!-case); % t if psl=4.2 and running in lower case.

symbolic procedure input_case m;
  if !*!*low!-case then input!-case m else
  !*raise:= m = 'lower;

symbolic procedure output_case m;
  if !*!*low!-case then output!-case m else
  !*lower := m neq 'raise;

flag('(input_case output_case), 'opfn);

input_case 'lower; % default: fold to system case.
output_case nil;   % default: avoid upper case output.

% Constants used in scanner.

define!-constant(cr!*,intern int2id 13);   % carriage return (^M).

define!-constant(ff!*,intern int2id 12);   % form feed (^L).

define!-constant(tab!*,intern int2id 9);   % tab key (^I)

deflist('((!$eol!$ t) (!$eof!$ t)),'constant!?);


% One inessential reference to REVERSIP in this module (left unchanged).

% This file defines the system dependent code necessary to run REDUCE
% under PSL.

Comment The following functions, which are referenced in the basic
REDUCE source (RLISP, ALG1, ALG2, MATR and PHYS) should be defined to
complete the definition of REDUCE:

        BYE
        EVLOAD
        ERROR1
        MKFIL
        ORDERP
        QUIT
        SEPRP
        SETPCHAR.

Prototypical descriptions of these functions are as follows;

remprop('bye,'stat);

fluid '(bye!-actions!*);

% A package may put a call to some termination code on top of this list.

bye!-actions!* := '((close!-output!-files) (exitlisp));

symbolic procedure bye;
   eval('progn . bye!-actions!*);

deflist('((bye endstat)),'stat);

symbolic procedure seprp u;
   % Returns true if U is a blank, end-of-line, tab, carriage return or
   % form feed.  This definition replaces the one in the BOOT file.
   u eq '!  or u eq tab!* or u eq !$eol!$ or u eq ff!* or u eq cr!*;

symbolic procedure error1;
   %This is the simplest error return, without a message printed. It can
   %be defined as ERROR(99,NIL) if necessary;
   throw('!$error!$,99);

symbolic procedure mkfil u;
   % Converts file descriptor U into valid system filename.
   if stringp u then u
    else if not idp u then typerr(u,"file name")
    else string!-downcase u;

% The following three functions are only used as part of mkfil.

symbolic procedure string!-downcase u;
   begin scalar z;
      if not stringp u then u := '!" . append(explode2 u,'(!"))
       else u := explode u;
      for each x in u do z := red!-char!-downcase x . z;
      return compress reverse z
   end;

remflag('(red!-char!-downcase),'lose);

symbolic procedure red!-char!-downcase u;
   (if x then cdr x else u) where x = atsoc(u,charassoc!*);

flag('(red!-char!-downcase),'lose);

charassoc!* :=
         '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e) (!F . !f)
           (!G . !g) (!H . !h) (!I . !i) (!J . !j) (!K . !k) (!L . !l)
           (!M . !m) (!N . !n) (!O . !o) (!P . !p) (!Q . !q) (!R . !r)
           (!S . !s) (!T . !t) (!U . !u) (!V . !v) (!W . !w) (!X . !x)
           (!Y . !y) (!Z . !z));

% symbolic procedure orderp(u,v);
%    % U, V are non-numeric atoms (but can be vectors).
%    % Returns true if U has same or higher order than id V by some
%    % consistent convention (eg unique position in memory).
%    wleq(inf u,inf v);       % PSL 3.4 form.
% %  id2int u <= id2int v;    % PSL 3.2 form.

symbolic procedure orderp(u,v);
   % This PSL-specific definition of ORDERP is designed to work in
   % lexicographical order.  It also checks to make sure arguments are
   % truly id's, which should be true with current REDUCE.
   begin scalar i,j,k,l,m;  % All sints.
      if idp u then u := strinf symnam idinf u
       else return typerr(u,"identifier");
      if idp v then v := strinf symnam idinf v
       else return typerr(v,"identifier");
      i := 0;
      j := strlen u;
      k := strlen v;
      % In the following, we assume size of u and v are inums.
   a: if null((l := strbyt(u,i)) eq (m := strbyt(v,i)))
        then return ilessp(l,m)
       else if i eq j then return null igreaterp(j,k)
       else if i eq k then return nil;
      i := iplus2(i,1);
      go to a;
   end;

procedure setpchar c;
   % Set prompt, return old one.
   begin scalar oldprompt;
    oldprompt := promptstring!*;
    promptstring!* := if stringp c then c
                      else if idp c then copystring id2string c
                      else bldmsg("%W", c);
    return oldprompt
   end;


Comment The following functions are only referenced if various flags are
set, or the functions are actually defined. They are defined in another
module, which is not needed to build the basic system. The name of the
flag follows the function name, enclosed in parentheses:

        CEDIT (?)
        COMPD (COMP)
        EDIT1   This function provides a link to an editor. However, a
                definition is not necessary, since REDUCE checks to see
                if it has a function value.
        EZGCDF (EZGCD)
        PRETTYPRINT (DEFN --- also called by DFPRINT)
                This function is used in particular for output of RLISP
                expressions in LISP syntax. If that feature is needed,
                and the prettyprint module is not available, then it
                should be defined as PRINT
        RPRINT (PRET)
        TIME (TIME) returns elapsed time from some arbitrary initial
                    point in milliseconds;


Comment The FACTOR module also requires a definition for GCTIME. Since
this is currently undefined in PSL, we provide the following definition;

symbolic procedure gctime; gctime!*;


Comment The following operator is used to save a REDUCE session as a
file for later use;

symbolic procedure savesession u;
   savesystem("Saved session",u,nil);

flag('(savesession),'opfn);

flag('(savesession),'noval);


Comment make "system" available as an operator;

flag('(system),'opfn);

flag('(system),'noval);


Comment to make "faslend" an endstat;

put('faslend,'stat,'endstat);


Comment to make "bye" and "quit" equivalent, as stated by the REDUCE
manual;

put('quit,'newnam,'bye);


Comment There are a number of system constants required for each
implementation. In systems that don't support inums, the equivalent
single precision integers should be used;

% LARGEST!-SMALL!-MODULUS is the largest power of two that can
% fit in the fast arithmetic (inum) range of the implementation.
% This is constant for the life of the system and could be
% compiled in-line if the compiler permits it.

largest!-small!-modulus := 2**23;

% If the (small) modular arithmetic is always limited to LARGEST-SMALL-
% MODULUS, it all fits in the inum range of the machine, with the
% exception of modular-times, that needs to use generic arithmetic for
% the multiplication.  However, on some machines (e.g., the VAX), it is
% possible to 'borrow' the extra precision needed, so that the following
% definition works.  This will not work of course for non-inums.

% remflag('(modular!-times),'lose);

% smacro procedure modular!-times(u,v);
%    iremainder(itimes2(u,v),current!-modulus);

% flag('(modular!-times),'lose);


% The following two definitions are commented out as they lead to
% unchecked vector ranges;

% symbolic smacro procedure getv(a,b); igetv(a,b);

% symbolic smacro procedure putv(a,b,c); iputv(a,b,c);

% flag('(intersection),'lose);


Comment PSL Specific patches;

Comment We need to define a function BEGIN, which acts as the top-level
call to REDUCE, and sets the appropriate variables;

% global '(startuproutine!* toploopread!* toploopeval!* toploopprint!*
%          toploopname!*);

remflag('(begin),'go);

symbolic procedure begin;
   begin
        !*echo := not !*int;
        !*extraecho := t;
        ifl!* := ipl!* := ofl!* := nil;
        if null date!* then go to a;
        if !*loadversion then errorset!*('(load entry),nil);
%       if version!* neq "REDUCE Development Version"
%         then errorset!*('(load patches),nil);
        !*gc := nil;
        !*usermode := nil;
        linelength 80;
        prin2 version!*;
        prin2 ", ";
        prin2 date!*;
%       if patch!-date!*
%         then progn(prin2 ", patched to ",prin2 patch!-date!*);
        prin2t " ...";
        !*mode := if getd 'addsq then 'algebraic else 'symbolic;
        if !*mode eq 'algebraic then !*break := nil;
           %since most REDUCE users won't use LISP
        date!* := nil;
a:      % crchar!* := '! ;
        if errorp errorset!*('(begin1),nil) then go to a;
           %until PSL fixed
%       if not yesp "do you really want to leave REDUCE?"
%          then go to a;
        prin2t "Entering LISP ... "
 end;

flag('(begin),'go);


Comment Initial setups for REDUCE;

% spare!* := 11;   % We need this for bootstrapping.

spare!* := 0;   % We need this for bootstrapping.

symchar!* := t;  % Changed prompt when in symbolic mode.

symbolic procedure initreduce; initrlisp();   % For compatibility.

symbolic procedure initrlisp;
  % Initial declarations for REDUCE
  <<statcounter := 0;
%   spare!* := 11;
    spare!* := 0;
    !*int := t;
    !*eolinstringok := t;  % We don't want the "string continued" msg.
    crbuflis!* := nil;     % We don't want to leave old input around.
    remd 'main;
    copyd('main,'rlispmain)>>;

symbolic procedure rlispmain;
  begin scalar l;
    rlispscantable!* := mkvect 128;
    l := '(17 11 11 11 11 11 11 11 11 17 17 11 17 17 11 11 11 11 11 11
           11 11 11 11 11 11 11 11 11 11 11 11 17 14 15 11 11 12 11 11
           11 11 13 11 11 11 20 11 00 01 02 03 04 05 06 07 08 09 13 11
           13 11 13 11 11 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10
           10 10 10 10 10 10 10 10 10 10 10 11 16 11 11 10 11 10 10 10
           10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10 10
           10 10 10 11 11 11 11 11 rlispdipthong);
    for i:=0:128 do <<putv(rlispscantable!*,i,car l); l := cdr l>>;
    currentreadmacroindicator!* := 'rlispreadmacro;
    currentscantable!* := rlispscantable!*;
    errout!* := 1;  % Errors to standard output, not special stream;
    lispeval '(begin);
    currentscantable!* := lispscantable!*; % But Slisp should use same
                                           % syntax as RLISP?
    standardlisp()
  end;

copyd('rdf,'dskin);       % CSL has a nicer name for this.

flag('(dskin rdf savesystem reclaim),'opfn);

flag('(dskin rdf savesystem),'noval);

% The following two statements are commented out to encourage algebraic
% mode users to use load_package and thus pick up any patches installed.

% flag('(load reload),'noform);

% deflist('((load rlis) (reload rlis)),'stat);

flag('(tr trst untr untrst),'noform);

deflist('((tr rlis) (trst rlis) (untr rlis) (untrst rlis)),'stat);

% Allow for direct calls to some UNIX and PSL functions.

flag('(pwd cd setenv getenv set!-heap!-size set!-bndstk!-size
       set_heap_size set_bndstk_size),'opfn);

if getd 'set!-heap!-size then copyd('set_heap_size,'set!-heap!-size);
if getd 'set!-bndstk!-size
  then copyd('set_bndstk_size,'set!-bndstk!-size);


% The following is PSL 3.4 specific.

switch fulltrace;   % Prevents node renaming in trace output.

!*fulltrace := t;   % Since we usually want it this way.

Comment The global variable ESC* is used by the interactive string
editor (defined in CEDIT) as a terminator for input strings.  In PSL
we use the escape character;

esc!* := intern int2id 27;


% The following are compiler switches.

fluid '(!*pgwd !*plap !*pwrds !*pcmac);

flag('(pgwd plap pwrds pcmac),'switch);


Comment The following declarations are needed to build various modules;

flag('(fl2int),'lose);                  % Used in MATH.

flag('(nth pnth spaces subla),'lose);   % Used in ALG1.

flag('(explode2 explode21),'lose);      % Used in RPRINT.

flag('(flag1 remflag1),'lose);          % Used in RCREF.

flag('(vector2list),'lose);             % Used in HILBERTS.

flag('(lconc tconc adjoin list2set deliqp1 deliqp),'lose);
                                        % Used in ASSIST.

deflist('((imports rlis)),'stat);   % Needed for ~imports to work.

% if null getd 'concat2 then <<copyd('concat2,'concat); remd 'concat>>;
   % In case this file loaded more than once.

symbolic procedure concat2(u,v); concat(u,v);

% Used by patching mechanism.

symbolic procedure dated!-gensym u; gensym();

load get!-options;
load strings;
load str!-search;
load numeric!-ops;
load chars;

symbolic procedure commandline_setq();

% executes a setq(var,value); from  commandline arg
% reduce -d var=value

begin scalar extraargs,extracommand;

getunixargs();
extraargs := get!-command!-args()$

extraargs := member('"-d",extraargs);
if extraargs
   then
   << extracommand := cadr extraargs;
      extracommand := split!-str(extracommand,"=");
      eval list('setq,intern car extracommand, cadr extracommand);
>>$
end$

% got used to Perl/php split WN

symbolic procedure split!-str (string,separator);
  reverse split!-str!-1(string,separator,NIL);

symbolic procedure split!-str!-1 (string,separator,r);
  begin scalar n;
  n:= string!-search!-equal (separator,string);
  return
    if n then
     split!-str!-1 (subseq(string ,n+1,string!-length(string)),separator,
                     subseq(string,0,n) .  r)
    else
      string . r ;
  end;

endmodule;

end;
