module rlfi;

%***********************************************************************
%*****                                                            ******
%*****     M O D U L E     R L F I      Ver. 1.2.1   23/05/1995   ******
%*****                                                            ******
%***********************************************************************
%*****   Program for LATEX syntax of REDUCE output formulas,      ******
%*****   to activate it, turn the LATEX switch ON.                ******
%*****   Program can be used only on systems supporting lower     ******
%*****   case characters through OFF RAISE.                       ******
%*****   Note that in REDUCE 3.6 one has to input REDUCE commands ******
%*****   in lower case!!!!!                                       ******
%***********************************************************************

% Author: Richard Liska
%         Faculty of Nuclear Sciences and Physical Engineering
%         Czech Technical University in Prague
%         Brehova 7, 115 19 Prague 1, Czech Republic
% E-mail: liska@siduri.fjfi.cvut.cz
% Program RLFI, Version REDUCE 3.6  23/05/1995

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


% History:
%     Ver. 1.01    17/11/1989   ******
%     Ver. 1.1     27/05/1991   ******
%     Ver. 1.1.1   27/08/1992   ****** added lists
%     Ver. 1.2     02/10/1992   ****** corrected lists, underscores _,
%           added all prefix operators from REDUCE kernel and SOLVE,
%           subscripts for ARBINT etc., VERBATIM OFF by default -
%           prints REDUCE Input: (to avoid empty verbatims), corrected
%           repeated printing of longer ids, added ROUNDED numbers
%     Ver. 1.2.1   23/05/1995   ****** minor change to 3.6

create!-package('(rlfi),'(contrib misc));

fluid'(posn!* orig!*);

% Global variables and their default values
global '(mstyle!* nochar!* nochar1!* linel!* laline!* ncharspr!*
         mstyles!*);
nochar!*:=nil;   % List of identifiers longer than one character
                 % used in previous commands
nochar1!*:=nil;  % List of identifiers longer than one character
                 % in actual command which are used for the first time
laline!*:=72;    % Linelength of output file
ncharspr!*:=0;   % Position on output line
linel!*:=linelength nil . laline!*; % actual length of line

mstyle!*:='!d!i!s!p!l!a!y!m!a!t!h;         % Default mathematical style
                                           % Possible math. styles
mstyles!*:= '(!m!a!t!h !d!i!s!p!l!a!y!m!a!t!h !e!q!u!a!t!i!o!n);

% Declaration of symbols and operators for LaTeX
flag('(!a!lp!h!a !b!e!t!a !g!a!m!m!a !d!e!l!t!a !e!p!s!i!l!o!n
       !v!a!r!e!p!s!i!l!o!n !z!e!t!a !e!t!a !t!h!e!t!a !v!a!r!t!h!e!t!a
       !i!o!t!a !k!a!p!p!a !l!a!m!b!d!a !m!u !n!u !x!i !p!i !v!a!r!p!i
       !r!h!o !v!a!r!r!h!o !s!i!g!m!a !v!a!r!s!i!g!m!a !t!a!u
       !u!p!s!i!l!o!n !p!h!i !v!a!r!p!h!i !c!h!i !p!s!i !o!m!e!g!a
       !G!a!m!m!a !D!e!l!t!a !T!h!e!t!a !L!a!m!b!d!a !X!i !P!i
       !S!i!g!m!a !U!p!s!i!l!o!n !P!h!i !P!s!i !O!m!e!g!a !i!n!f!t!y
       !h!b!a!r !n!a!b!l!a !p!e!r!p alpha beta gamma delta epsilon
       varepsilon zeta eta theta vartheta iota kappa lambda mu nu xi pi
       varpi rho varrho sigma varsigma tau upsilon phi varphi chi psi
       omega Gamma Delta Theta Lambda Xi Pi Sigma Upsilon Phi Psi Omega
       infty hbar nabla perp),'symbol);

flag('(!h!a!t !c!h!e!c!k !b!r!e!v!e !a!c!u!t!e !g!r!a!v!e !t!i!l!d!e
       !b!a!r !v!e!c !d!o!t !d!d!o!t hat check breve acute grave tilde
       bar vec dot ddot),'accdef);


deflist('((!b!o!l!d !{!\!b!f! )(!r!o!m!a!n !{!\r!m! )
          (bold !{!\!b!f! )(roman !{!\r!m! )),'fontdef);

deflist('((!( !\!l!e!f!t!() (!) !\!r!i!g!h!t!)) (!P!I !\!p!i! )
         (!p!i !\pi! ) (!E !e) (!I !i) (e !e) (i !i)),'name);

deflist('((times ! )(setq !=)(geq !\!g!e!q! )(leq !\!l!e!q! )),'lapr);

% LaTeX supported operators

deflist('(
          (acos !\!a!r!c!c!o!s) (coth !\!c!o!t!h)   (sec !\!s!e!c)
          (asin !\!a!r!c!s!i!n) (csc  !\!c!s!c)     (sin !\!s!i!n)
          (atan !\!a!r!c!t!a!n) (exp !\!e!x!p)      (sinh !\!s!i!n!h)
          (arg !\!a!r!g)        (ln !\!l!n)         (tan !\!t!a!n)
          (cos !\!c!o!s)        (log !\!l!o!g)      (tanh !\!t!a!n!h)
          (cosh !\!c!o!s!h)     (max !\!m!a!x)      (sum !\!s!u!m)
          (cot !\!c!o!t)        (min !\!m!i!n)      (product !\!p!r!o!d)
          ),'lapop);


% Other REDUCE operators

deflist('(
(abs        "{\rm abs}")           (deg2rad    "{\rm deg2rad}")
(acosd      "{\rm acosd}")         (dilog      "{\rm dilog}")
(acosh      "{\rm acosh}")         (dms2deg    "{\rm dms2deg}")
(acot       "{\rm acot}")          (dms2rad    "{\rm dms2rad}")
(acotd      "{\rm acotd}")         (erf        "{\rm erf}")
(acoth      "{\rm acoth}")         (expint     "{\rm expint}")
(acsc       "{\rm acsc}")          (factorial  "{\rm factorial}")
(acscd      "{\rm acscd}")         (fix        "{\rm fix}")
(acsch      "{\rm acsch}")         (floor      "{\rm floor}")
(arbcomplex "{\rm arbcomplex}")    (hypot      "{\rm hypot}")
(arbint     "{\rm arbint}")        (icbrt      "{\rm icbrt}")
(arbreal    "{\rm arbreal}")       (ilog2      "{\rm ilog2}")
(argd       "{\rm argd}")          (impart     "{\rm impart}")
(asec       "{\rm asec}")          (irootn     "{\rm irootn}")
(asecd      "{\rm asecd}")         (isqrt      "{\rm isqrt}")
(asech      "{\rm asech}")         (log10      "{\rm log10}")
(asind      "{\rm asind}")         (logb       "{\rm logb}")
(asinh      "{\rm asinh}")         (norm       "{\rm norm}")
(atan2      "{\rm atan2}")         (one_of     "{\rm one_of}")
(atan2d     "{\rm atan2d}")        (perm       "{\rm perm}")
(atand      "{\rm atand}")         (rad2deg    "{\rm rad2deg}")
(atanh      "{\rm atanh}")         (rad2dms    "{\rm rad2dms}")
(cbrt       "{\rm cbrt}")          (repart     "{\rm repart}")
(ceiling    "{\rm ceiling}")       (root_of    "{\rm root_of}")
(choose     "{\rm choose}")        (round      "{\rm round}")
(cosd       "{\rm cosd}")          (secd       "{\rm secd}")
(cosh       "{\rm cosh}")          (sech       "{\rm sech}")
(cotd       "{\rm cotd}")          (sgn        "{\rm sgn}")
(cscd       "{\rm cscd}")          (sind       "{\rm sind}")
(csch       "{\rm csch}")          (sol        "{\rm sol}")
(deg2dms    "{\rm deg2dms}")       (tand       "{\rm tand}")
          ),'lapop);

symbolic procedure get!*(u,v);
if numberp u then nil else get(u,v);

fluid '(!*latex !*lasimp !*verbatim !*!*a2sfn);
switch latex,lasimp,verbatim;
!*lasimp := t;
symbolic put('latex,'simpfg,'((t (latexon)) (nil(latexoff)) ));
symbolic put('verbatim,'simpfg,'((t (verbatimon)) (nil (verbatimoff))));

symbolic procedure latexon;
% Procedure called after ON LATEX
<<!*!*a2sfn:='texaeval;
  !*raise:=nil;
  prin2t "\documentstyle{article}";
  prin2t "\begin{document}";
  if !*verbatim then
      <<prin2t "\begin{verbatim}";
        prin2t "REDUCE Input:">>;
  put('tex,'rtypefn,'(lambda(x) 'tex)) >>;

symbolic procedure latexoff;
% Procedure called after OFF LATEX
<<!*!*a2sfn:='aeval;
  !*raise:=t;
  remprop('tex,'rtypefn);
  if !*verbatim then
      <<terpri();
        prin2t "\end{verbatim}" >>;
  prin2t "\end{document}";
  rmsubs() >>;

procedure verbatimon;
<<if !*latex and null !*verbatim then
      <<prin2t "\begin{verbatim}";
        prin2t "REDUCE Input:">>;
  !*echo:=t>>;

procedure verbatimoff;
<<if !*latex and !*verbatim then
      <<terpri();
        prin2t "\end{verbatim}">>;
  !*echo:=nil >>;

symbolic procedure texaeval u;
% Procedure replaces the AEVAL procedure in the LATEX mode
if !*lasimp then list('tex,aeval u)
  else list('tex,u);

% deklarace latex modu;
put('tex,'tag,'tex);
put('tex,'simpfn,'simpcar);
put('tex,'typeletfn,'texlet);
put('tex,'prifn,'latexprint);
put('tex,'setprifn,'setlaprin);
flag('(tex),'sprifn);

symbolic procedure texlet(u,v,tu,b,tv);
% Assignment procedure for LATEX mode
% !!! match can be evaluated like let!!!!;
if eqcar(v,'tex) then let2(u,cadr v,nil,b)
  else msgpri(" value for ",u," not assigned ",v,nil);

symbolic procedure latexprint u;
% Prints expression U in the LaTeX syntax
<<prinlabegin();
  latexprin u;
  prinlaend() >>;

symbolic procedure setlaprin(u,v);
% Prints assignment command in LaTeX syntax
<<prinlabegin();
  latexprin u;
  oprinla 'setq;
  latexprin v;
  prinlaend() >>;

symbolic procedure mathstyle u;
% Defines the output mathematical style
if car u memq mstyles!* then <<mstyle!*:=car u;nil>>
  else msgpri(" mathematical style ",car u," not supported ",nil,nil);

put('mathstyle,'stat,'rlis);

symbolic procedure prinlabegin;
% Initializes the output
<<if !*verbatim then
      <<terpri();
        prin2t "\end{verbatim}">>;
  linel!*:=linelength nil . laline!*;
  if ofl!* then linelength(laline!* + 2)
    else laline!*:=car linel!* - 2;
  prin2 "\begin{";
  prin2 mstyle!*;
  prin2t "}" >>;

symbolic procedure prinlaend;
% Ends the output of one expression
<<terpri();
  prin2 "\end{";
  prin2 mstyle!*;
  prin2t "}";
  if !*verbatim then
      <<prin2t "\begin{verbatim}";
        prin2t "REDUCE Input:">>;
  ncharspr!*:=0;
  if nochar1!*
    then msgpri(" Longer than one character identifiers used ",
                nil,nochar1!*,nil,nil);
  if ofl!* then linelength(car linel!*)
    else laline!*:=cdr linel!*;
  nochar!*:=append(nochar!*,nochar1!*);
  nochar1!*:=nil >>;

symbolic procedure latexprin u;
% Prints expression U in the LaTeX syntax
if eqcar(u,'tex) then maprintla(cadr u,0)
  else maprintla(u,0);

symbolic procedure texprla(u,p);
maprintla(car u,p);

put('tex,'laprifn,'texprla);

symbolic procedure maprintla(l,p);
% L is printed expression, P is the infix precedence of infix operator
% Procedure is similar to that one in the REDUCE source
begin
  scalar x;
  if null l then return nil
    else if numberp l then go to c
    else if atom l then return prinlatom l
    else if stringp l then return prin2la l
    else if not atom car l then return maprintla(car l,p)
    else if (x:=get(car l,'laprifn)) and
        ((not flagp(car l,'fulla)
          and not (apply(x,list(cdr l,p)) eq 'failed))
        or (flagp(car l,'fulla) and not(apply(x,list(l,p)) eq 'failed)))
           then return l
    else if (x:=get(car l,'indexed)) then return prinidop(car l,cdr l,x)
    else if x:=get(car l,'infix) then go to a
    else if car l eq '!:rd!: then return
      begin
        scalar !*nat,ll;
        % max. estimate
        ll:=if floatp cdr l then lengthc cdr l
             else lengthc cadr l + lengthc cddr l + 5;
        if ncharspr!* + ll > laline!* then
            <<terpri();
              ncharspr!*:=ll >>
          else ncharspr!*:=ncharspr!* + ll;
        posn!*:=orig!*;
        rd!:prin l
      end;
  oprinla(car l);
  prinpopargs(car l,cdr l,p);
  return l;
a:p:=x>p;
  if null p and car l eq 'equal then p:=t;
  if p then go to b;
  prinlatom '!(;
b:inprinla(car l,x,cdr l);
  if p then return l;
  prinlatom '!);
  return l;
c:if not(l<0) or p<get('minus,'infix) then return prin2la l;
  prin2la '!(;
  prin2la l;
  prin2la '!);
  return l
end;

symbolic procedure prinpopargs(op,args,p);
% Prints argument(s) of prefix operator, decides if arg(s) will be
% enclosed in parantheses
begin
  scalar x;
  x:=null args or cdr args or not atom car args;
% x:=x or null get(op,'lapop);
  if x then prinlatom '!( else prin2la "\,";
  if args then inprinla('!*comma!*,0,args);
  if x then prinlatom '!);
  if null x and p=get('times,'infix) then prin2la "\:";
  return args
end;

symbolic procedure prinlatom u;
% Prints atom or the symbol associated to the atom in given font
% and with given accent
begin
  scalar n,f,a;
  if f:=get(u,'font) then prin2la f;
  if a:=get(u,'accent) then prin2la a;
  if n:=get(u,'name) then prin2la n
    else prin2la testchar1 u;
  if a then prin2la "}";
  if f then prin2la "}";
  return u
end;

symbolic procedure defid u;
% Defines the statement DEFID for defining symbol, font and accent
% associated to given atom
begin
  scalar at,x,y;
  at:=car u;
  if not atom at or null car u then go to er;
a:u:=cdr u;
  x:=car u;
  if eqcar(x,'equal) then x:=cdr x
    else go to er;
  if car x eq 'name then
      if flagp(cadr x,'symbol)
        then put(at,'name,incompe3('!\,cadr x,'! ))
        else put(at,'name,testchar1 cadr x)
    else if car x eq 'font then
      if y:=get(cadr x,'fontdef) then put(at,'font,y)
        else go to er
    else if car x eq 'accent then
      if flagp(cadr x,'accdef)
        then put(at,'accent,incompe3('!\,cadr x,'!{))
       else go to er
    else go to er;
  if cdr u then go to a;
  return nil;
er:lprie(" Syntax error ")
end;

put('defid,'stat,'rlis);

symbolic procedure incompe3(a,b,c);
% Constructs new atom by concatenating A,B,C
intern compress append(explode a,append(explode b,explode c));

symbolic procedure testchar1 u;
% Checks if id U has only one character
if fixp u then u
  else if null cdr explode2 u then u
  else if member(u,nochar!*) then u
  else if member(u,nochar1!*) then u
  else <<nochar1!*:=u . nochar1!*; u>>;

procedure chundexp u;
% Replaces underscores _ in ids by \_
% except if u = !_
begin
  scalar x;
  u:=explode2 u;
  x:=u;
  if eqcar(u,'_) and cdr u then u:='!\ . u;
a:if null cdr x then goto r;
  if cadr x eq '_ then
      <<rplacd(x,'!\ . cdr x);
        x:=cdr x>>;
  x:=cdr x;
  goto a;
r:return u
end;

symbolic procedure inprinla(op,p,l);
% Prints infix operator OP with arguments in the list L
begin
  if get(op,'alt) then go to a;
  maprintla(car l,p);
a0:l:=cdr l;
a:if null l then return nil
    else if atom car l or not(op eq get!*(caar l,'alt)) then
      <<oprinla op;
        maprintla(negnumberchk car l,p)>>
    else maprintla(car l,p);
  go to a0;
end;

symbolic procedure oprinla op;
% Prints operator OP
begin
  scalar x;
  if x:=get(op,'lapr) then prin2la x
    else if x:=get(op,'prtch) then prin2la x
    else if x:=get(op,'lapop) then <<prin2la x; prin2la '! >>
    else prinlatom op
end;

% Definition of new operator of division --> horizontal division line
newtok '((!\) backslash);
deflist('((backslash recip)),'unary);
algebraic infix \;
precedence 'backslash,'quotient;
put('backslash,'simpfn,'simpiden);

symbolic procedure prin2la u;
% Prints atom or string U, checks the length of line
% CHUNDEXP makes the change _ -> \_
begin
  scalar l;
  u:=chundexp u;
  l:=length u;
  if ncharspr!* + l > laline!* then <<terpri(); ncharspr!*:=0 >>;
  for each a in u do prin2 a;
  ncharspr!*:=ncharspr!* + l
end;

symbolic procedure prinfrac(l,p);
% Prints the fraction with horizontal division line
<<prin2la "\frac{";
  maprintla(car l,0);
  prin2la "}{";
  maprintla(cadr l,0);
  prin2la "}" >>;
put('backslash,'laprifn,'prinfrac);

symbolic procedure defindex u;
% Defines the placing of indices of an operator
for each a in u do defindex1 a;
put('defindex,'stat,'rlis);

symbolic procedure defindex1 u;
begin
  scalar at,x;
  at:=car u;
  for each a in cdr u do if not(a memq '(arg up down leftup leftdown))
                           then x:=t;
  if not atom at or null cdr u then x:=t;
  return if x then msgpri(" Syntax error ",u,nil,nil,'hold)
           else put(at,'indexed,cdr u)
end;

symbolic procedure prinidop(op,args,mask);
% Prints operator with indices. MASK describe the place of indices
begin
  scalar arg,up,down,lup,ldown;
  if null args then return prinlatom op;
a:if car mask eq 'arg then arg:=car args . arg
    else if car mask eq 'up then up:=car args . up
    else if car mask eq 'down then down:=car args . down
    else if car mask eq 'leftup then lup:=car args . lup
    else if car mask eq 'leftdown then ldown:=car args . ldown;
  mask:=cdr mask;
  args:=cdr args;
  if mask and args then go to a;
  mask:='(arg);
  if args then go to a;
  arg:=reverse arg;
  up:=reverse up;
  down:=reverse down;
  lup:=reverse lup;
  ldown:=reverse ldown;
  if lup or ldown then prin2la "\:";
  if lup then
      <<prin2la '!^!{;
        prinindexs lup;
        prin2la "}" >>;
  if ldown then
      <<prin2la "_";
        prin2la "{";
        prinindexs ldown;
        prin2la "}" >>;
  oprinla op;
  if up then
      <<prin2la '!^!{;
        prinindexs up;
        prin2la "}" >>;
  if down then
      <<prin2la "_";
        prin2la "{";
        prinindexs down;
        prin2la "}" >>;
  if arg then
      <<prinlatom '!(;
        inprinla('!*comma!*,0,arg);
        prinlatom '!) >>;
  return op
end;

symbolic procedure prinindexs ndxs;
% Prints indexces NDXS, if all indices are atoms prints them withouth
% separating commas
begin
  scalar b;
  for each a in ndxs do if not atom a then b:=t;
  if not b then for each a in ndxs do prinlatom a
    else inprinla('!*comma!*,0,ndxs)
end;

symbolic procedure exptprla(args,p);
% Prints powers
begin
  scalar arg,exp,ilist;
  arg:=car args;
  exp:=cadr args;
  if not atom exp and car exp eq 'quotient and cadr exp = 1
     and atom caddr exp
    then if caddr exp = 2 then
                <<prin2la "\sqrt{";
                  maprintla(arg,0);
                  prin2la "}" >>
              else
                <<prin2la "\sqrt[";
                  prinlatom caddr exp;
                  prin2la "]{";
                  maprintla(arg,0);
                  prin2la "}" >>
    else if atom arg then
      <<prinlatom arg;
        prin2la '!^!{;
        maprintla(exp,0);
        prin2la "}" >>
    else if atom car arg and not (ilist:=get(car arg,'indexed)) and
                not get(car arg,'laprifn) and
                not get(car arg,'infix) and atom exp then
             <<oprinla car arg;
               prin2la '!^!{;
               prinlatom exp;
               prin2la "}";
               prinpopargs(car arg,cdr arg,p) >>
    else if atom car arg and (ilist:=get(car arg,'indexed)) and
                 not memq('up,ilist) then
              <<maprintla(arg,0);
                prin2la '!^!{;
                maprintla(exp,0);
                prin2la '!} >>
    else
      <<prinlatom '!(;
        maprintla(arg,0);
        prinlatom '!);
        prin2la '!^!{;
        maprintla(exp,0);
        prin2la "}" >>;
  return args
end;
put('expt,'laprifn,'exptprla);

procedure sqrtprla(arg,p);
% Prints square root
<<prin2la "\sqrt {";
  maprintla(car arg,0);
  prin2la "}" >>;
put('sqrt,'laprifn,'sqrtprla);

symbolic procedure intprla(args,p);
% Prints indefinite itegral
begin
  scalar arg,var;
  if null args or null cdr args or not atom cadr args
    then return 'failed;
  arg:=car args;
  var:=cadr args;
  prin2la "\int ";
  maprintla(arg,0);
  prin2la "\:d\,";
  prinlatom var;
  return args
end;
put('int,'laprifn,'intprla);

symbolic procedure dintprla(args,p);
% Prints definite integral
begin
  scalar down,up,arg,var;
  if null args or null cdr args or null cddr args or null cdddr args or
     not atom (var:=cadddr args) then return 'failed;
  down:=car args;
  up:=cadr args;
  arg:=caddr args;
  prin2la "\int";
  prin2la "_";
  prin2la "{";
  maprintla(down,0);
  prin2la "}^{";
  maprintla(up,0);
  prin2la "}";
  maprintla(arg,0);
  prin2la "\:d\,";
  prinlatom var;
  return args
end;
put('dint,'laprifn,'dintprla);

symbolic procedure sumprla(ex,p);
% Prints a sum
begin
  scalar op,down,up,arg;
  if not get(op:=car ex,'lapop) or null cdr ex or null cddr ex
     or null cdddr ex
    then return 'failed;
  down:=cadr ex;
  up:=caddr ex;
  arg:=cadddr ex;
  oprinla op;
  if down then
      <<prin2la "_";
        prin2la "{";
        maprintla(down,0);
        prin2la "}" >>;
  if up then
      <<prin2la '!^!{;
        maprintla(up,0);
        prin2la "}" >>;
  maprintla(arg,get('times,'infix) - 1);
  return ex
end;
put('sum,'laprifn,'sumprla);
put('product,'laprifn,'sumprla);
flag('(sum product),'fulla);

symbolic procedure sqprla(args,p);
% Prints standard quotient
maprintla(prepsq!* car args,p);
put('!*sq,'laprifn,'sqprla);

symbolic procedure dfprla(dfex,p);
% Prints derivaves
begin
  scalar op,ord,arg,x,argup;
  op:=get(car dfex,'lapop);
  arg:=cadr dfex;
  dfex:=cddr dfex;
  x:=dfex;
  ord:=0;
a:if null cdr x then
      <<x:=cdr x;
        ord:=ord+1 >>
    else if fixp cadr x then
      <<ord:=ord+cadr x;
        x:=cddr x >>
    else
      <<x:=cdr x;
        ord:=ord+1 >>;
  if x then go to a;
  if atom arg or (not get(car arg,'infix) and not get(car arg,'laprifn))
    then argup:=t;
  prin2la "\frac{";
  prin2la op;
  if ord=1 then prin2la "\,"
    else
      <<prin2la '!^!{;
        prin2la ord;
        prin2la "}" >>;
  if argup then maprintla(arg,0);
  prin2la "}{";
  x:=dfex;
b:if not atom car x and cdr x and fixp cadr x then prin2la "(";
  prin2la op;
  if null cdr x or not fixp cadr x then
      <<prin2la "\,";
        maprintla(car x,0);
        x:=cdr x >>
    else
      <<maprintla(car x,0);
        if not atom car x then prin2la ")";
        prin2la '!^!{;
        prin2la cadr x;
        prin2la "}";
        x:=cddr x >>;
  if x then go to b;
  prin2la "}";
  if null argup then maprintla(arg,get('quotient,'infix));
  return arg
end;

put('df,'laprifn,'dfprla);
put('pdf,'laprifn,'dfprla);
flag('(df pdf),'fulla);
put('df,'lapop,"{\rm d}");
put('pdf,'lapop,"\partial ");

procedure listprla(args,p);
% Prints list of expressions
if args then
<<prin2t "\left\{";
  maprintla(car args,0);
  args:=cdr args;
  if args then
      for each a in args do
        <<prin2la" , ";
          maprintla(a,0)>>;
  terpri();
  prin2 "\right\}">>
else prin2 "\{\}";

put('list,'laprifn,'listprla);

put('arbint,'indexed,'(down));
put('arbreal,'indexed,'(down));
put('arbcomplex,'indexed,'(down));

algebraic;

operator pdf,dint,product;

endmodule;


end;
