module  codpri;  % Support for visualizing output.

% -------------------------------------------------------------------- ;
% Copyright : J.A. Van Hulzen, Twente University, Dept. of Computer    ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands. ;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, M.C. van Heerwaarden,   ;
%             J.B. van Veelen                                          ;
% -------------------------------------------------------------------- ;

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

% -------------------------------------------------------------------- ;
% The module CODPRI consists of three parts:                           ;
%  1 - Facilities to vizualize the data structures on user request,i.e.;
%      when ON PRIMAT or ON PRIALL is set(see CODCTL.RED).             ;
%  2 - Routines for constructing PREFIXLIST. The value of this variable;
%      is an association list,consisting of pairs (name.value),where   ;
%      name is the (sub)expression name and where value stands for the ;
%      prefixform of the corresponding (sub)expression. Its construc-  ;
%      tion is activated via the procedure MAKEPREFIXL used in CALC    ;
%      (see the module CODCTL).                                        ;
%  3 - Functions for improving the final layout of the output. These   ;
%      functions are applied on the final form of Codmat before the    ;
%      preparations for the printing process start.Calling the function;
%      ImproveFinalLayout suffices.                                    ;
% -------------------------------------------------------------------- ;

% -------------------------------------------------------------------- ;
% Global identifiers needed in this module are :                       ;
% -------------------------------------------------------------------- ;

fluid '(preprefixlist);

global '(codbexl!* rowmax rowmin lintlst kvarlst endmat rhsaliases
         avarlst min!-expr!-length!* !*vectorc)$

global '(codmat maxvar)$

% -------------------------------------------------------------------- ;
% LINTLST is a list of integers which are too long to be included in   ;
% the schemes directly.LINTLST is built up in the procedure PRINUMB and;
% used in the procedure PRISCHEME via the procedure PRILINT.           ;
% The globals ROWMAX,ROWMIN and ENDMAT are defined in CODCTL.RED. The  ;
% global KVARLST is introduced in CODMAT.RED.                          ;
% -------------------------------------------------------------------- ;

% -------------------------------------------------------------------- ;
% PART 1 : PROCEDURES FOR VIZUALIZING THE DATA STRUCTURES              ;
% -------------------------------------------------------------------- ;
% These print facilities are mainly designed as debugging tool.They are;
% usable via an ON PRIMAT or an ON PRIALL setting.The governing routine;
% is PRIMAT,called in the procedure CALC to vizualize the result of    ;
% parsing a set of input expressions and to show the results of optimi-;
% zing this set.                                                       ;
% In PRIMAT the linelength is temporarily reset to 120,thus limiting   ;
% the size of the matrix schemes produced by PRISCHEME('PLUS) and      ;
% PRISCHEME('TIMES) in PRIMAT.                                         ;
% In PRISCHEME(Operator) a message is generated when the linelength is ;
% not sufficient telling that printing is impossible.In all other cases;
% the procedure PRISCHEME produces a compact version of reality.It uses;
% the routines PRI(nt)NUMB(er),PRI(nt)ROW,PRI(nt)VAR(iable) and PRI(nt);
% L(ong)INT(eger). The procedures TESTPROW and MEMPQ are used for test-;
% ing details in PRISCHEME and PRIROW,resp. To simplify explaining the ;
% code we give a simple example :                                      ;
%                                                                      ;
% Assume we have :                                                     ;
%                                       8  2                   8       ;
% U := ((A + 2*B)*SIN(A + 2*B)*A*B + 2*A *B  + 2*A + 4*B - 677)  + 1234;
%                                                                      ;
% Then PRIMAT produces via PRISCHEME :                                 ;
%                                                                      ;
% Sumscheme :                                                          ;
%                                                                      ;
%    |  3  4  5| EC|Far                                                ;
% ---------------------                                                ;
%   0|        X|  1| U                                                 ;
%   2|  2  4  X|  8| 1                                                 ;
%   4|  1  2   |  1! 3                                                 ;
%   5|  1  2   |  1| S0                                                ;
% ---------------------                                                ;
% The following integers ought to replace the X-entries of the matrix  ;
% in a left-to-right-and-top-down order : 1234  -677                   ;
% 3 : A                                                                ;
% 4 : B                                                                ;
% 5 : +ONE                                                             ;
%                                                                      ;
% Productscheme :                                                      ;
%                                                                      ;
%    |  0  1  2| EC|Far                                                ;
% ---------------------                                                ;
%   1|         |  1| 0                                                 ;
%   3|  1  1  1|  1| 2                                                 ;
%   6|     8  2|  2| 2                                                 ;
% ---------------------                                                ;
% 0 : S1=SIN(S0)                                                       ;
% 1 : A                                                                ;
% 2 : B                                                                ;
%                                                                      ;
% If Far has a name (U,S0) as value its row defines the prim.part of   ;
% the expression assigned to this name.Its composite parts can be found;
% in those rows of the other scheme,which have the index of the present;
% row in their Far-field( i.e. their father). The EC-field shows the   ;
% E(xponent of a sum) or the C(oefficient of a product).               ;
% The column numbers in the schemes correspondent with the CODMAT co-  ;
% lumn indices. These numbers are used to give a (vertical) list of    ;
% pairs (number : varname),where varname is either a variable name,the ;
% special symbol !+ONE( for the constants in a sum) or an assignment   ;
% like S1=SIN(S0),indicating that function applications are replaced by;
% system selected names.                                               ;
% When exponents or coefficients are too long to be printed,i.e. when  ;
% entry>999 or when entry<-99 an X is printed instead. A sequence of   ;
% integers corresponding with these X's in the scheme is given directly;
% below it in a left-to-right-and-top-down order. Hence :              ;
%                                                                      ;
% U     := 1234 + prod1(= product defined in row 1)                    ;
% prod1 := 1 * sum2(= sum defined in row 2)                            ;
% sum2  := (2*A + 4*B -677 + prod3 + prod6)^8                          ;
% prod3 := S1 * A * B * sum4                                           ;
% sum4  := A + 2*B                                                     ;
% S1    := SIN(S0)                                                     ;
% S0    := A + 2*B                                                     ;
% prod6 := 2 * A^8 * B^2                                               ;
% -------------------------------------------------------------------- ;

symbolic smacro procedure testprow(y,opv);
% -------------------------------------------------------------------- ;
% arg : Column index Y. Operator value Opv.                            ;
% res : T if the column Y is part of the Opv-scheme,NIL otherwise.     ;
% -------------------------------------------------------------------- ;
free(y) and opval(y) eq opv;


symbolic procedure primat;
% -------------------------------------------------------------------- ;
% res : A reflection is produced of the state of the matrix CODMAT     ;
% -------------------------------------------------------------------- ;
   begin scalar l;
     l:=linelength 120;
     terpri();
     prin2 "Sumscheme :";
     prischeme('plus);
     terpri();
     terpri();
     terpri();
     prin2 "Productscheme :";
     prischeme('times);
     linelength(l);
   end;

% -------------------------------------------------------------------- ;
% The procedure Primat1 can be used for testing new features.          ;
% -------------------------------------------------------------------- ;
global '(freevec freetest)$
freetest:=nil;

symbolic procedure primat1;
begin scalar freevec1,rmin,rmax;
 rmin:=rowmin; rmax:=rowmax;
 if null freetest or freetest<maxvar
  then <<freetest:=maxvar;
         freevec1:=mkvect(2*maxvar);
         freevec:=freevec1
       >>;
 for j:=rmin:rmax do <<putv(freevec,j+maxvar,free(j));setfree(j)>>;
 primat();
 for j:=rmin:rmax do
  << if not getv(freevec,j+maxvar) then setoccup(j);
     terpri();
     if j<0 then write "col(",j,")=",getv(codmat,maxvar+j)
     else write "row(",j,")=",getv(codmat,maxvar+j)
  >>;
 terpri()
end;

symbolic procedure prischeme(opv);
% -------------------------------------------------------------------- ;
% arg : The value of Opv is either 'TIMES or 'PLUS.                    ;
% eff : The Opv-scheme is printed                                      ;
% -------------------------------------------------------------------- ;
begin scalar n,yl;
  n:=0;
  lintlst:=nil;
  terpri();
  terpri();
  prin2 "   |";
  for y:=rowmin:(-1) do
  if testprow(y,opv)
  then <<prinumb(y+abs(rowmin)); yl:=y.yl; n:=n+1>>;
  prin2 "| EC|Far";
  terpri();
  n:=3*n+12;
  if n>120 then <<prin2 "Scheme to large to be printed"; return>>;
  for j:=1:n do prin2 "-";
  yl:=reverse(yl);
  for x:=0:rowmax do
  if testprow(x,opv)
  then prirow(x,opv,yl);
  terpri();
  for j:=1:n do prin2 "-";
  prilint();
  terpri();
  for y:=rowmin:(-1) do
  if testprow(y,opv)
  then
  <<prin2(yl:=y+abs(rowmin));
    if yl < 10 then prin2 "  : " else prin2 " : ";
    privar(farvar y);
    if n:=assoc(farvar y,kvarlst)
    then <<prin2 "="; privar(cdr n)>>;
    terpri()
  >>;
end;

symbolic procedure prirow(x,opv,yl);
% -------------------------------------------------------------------- ;
% arg : Index X of a row of the Opv-scheme. Y1 is the list of column   ;
%       indices which occur in the Opv-scheme.                         ;
% eff : Row X of the Opv-scheme is printed in the above discussed way. ;
% -------------------------------------------------------------------- ;
   begin
     terpri();
     prinumb(x);
     prin2 "|";
     foreach z in zstrt(x) do
     if testprow(yind z,opv)
     then
     <<yl:=memqp(yind z,yl);
       prinumb(ival z)>>;
     for j:=1:length(yl) do prin2 "   ";
     prin2 "|";
     prinumb(expcof x);
     prin2 "| ";
     privar(farvar x);
   end;

symbolic procedure memqp(y,yl);
% -------------------------------------------------------------------- ;
% arg : Y is the index of the column of which the exponent/coefficient ;
%       of the corresponding variable has to be printed. Y1 is the list;
%       of indices of columns which can also contribute to the row     ;
%       which is now in the process of being printed.                  ;
% eff : If Y=Car(Y1) the calling routine,PRIROW,can continue its prin- ;
%       ting activities directly with the exp./coeff. in question. If  ;
%       not we have to print blanks to indicate that the column and row;
%       have nothing in common. We continue with the Cdr of the list Y1;
% -------------------------------------------------------------------- ;
if y=car(yl)
then cdr(yl)
else
<<prin2 "   ";
  memqp(y,cdr yl)>>;

symbolic procedure prinumb(n);
% -------------------------------------------------------------------- ;
% arg : A number N.                                                    ;
% eff : N is printed using atmost three positions if possible.In case  ;
%       the size of the number is to large or the number is a float,   ;
%       we print "  X" and add N to then list LINTLST of long numbers, ;
%       which are printed once the scheme is completed.                ;
% -------------------------------------------------------------------- ;
<<if pairp(n) and memq(car n, domainlist!*)
  then <<lintlst:=n.lintlst; n:="  X">>
  else
  if minusp(n)
  then
    (if n>-10
     then prin2 " "
     else
      if n<=-100
      then <<lintlst:=n.lintlst; n:="  X">>)
  else
   (if n<10
    then prin2 "  "
    else
      if n<100
      then prin2 " "
      else
        if n>=1000
        then <<lintlst:=n.lintlst; n:="  X">>);
  prin2 n;
>>;

symbolic procedure prilint;
% -------------------------------------------------------------------- ;
% eff : The list of "long" numbers LINTLST,produced in the procedure   ;
%       PRINUMB,is printed.                                            ;
% -------------------------------------------------------------------- ;
if lintlst
then
<<terpri();
  prin2
  "The following numbers ought to replace the X-entries of the matrix";
  terpri();
  prin2 "in a left-to-right-and top-down order : ";
  foreach n in reverse(lintlst) do <<dm!-print n; prin2 "  ">>;
>>;

symbolic procedure privar(var);
% -------------------------------------------------------------------- ;
% arg : The template VAR  for a variable,a list defining a kernel in   ;
%       prefix notation,i.e.(a b c) in stead of a(b,c) or a constant.  ;
% eff : VAR is printed.                                                ;
% -------------------------------------------------------------------- ;
if atom(var)
then prin2 var
else
<<prin2(car var);
  prin2 "(";
  var:=cdr var;
  while var do
  <<dm!-print(car var);
   if var:=cdr(var) then prin2 ",">>;
  prin2 ")";
>>;


% -------------------------------------------------------------------- ;
% PART 2 : PRODUCTION OF PREFIXLIST - THE FINAL RESULT                 ;
% -------------------------------------------------------------------- ;
% Given :                                                              ;
%                                       8  2                   8       ;
% U := ((A + 2*B)*SIN(A + 2*B)*A*B + 2*A *B  + 2*A + 4*B - 677)  + 1234;
%                                                                      ;
% The optimizer produces the sequence of assignment statements :       ;
%                                                                      ;
%    S0 := A + 2*B                                                     ;
%    S1 := SIN(S0)                                                     ;
%    S3 := A*B                                                         ;
%    S9 := A*A                                                         ;
%    S8 := A*S9                                                        ;
%    S7 := S8*S8                                                       ;
%    S5 := 2*S0 - 677 + S3*(S0*S1 + 2*S3*S7)                           ;
%    S9 := S5*S5                                                       ;
%    S8 := S9*S9                                                       ;
%    S6 := S8*S8                                                       ;
%    U := 1234 + S6                                                    ;
%                                                                      ;
% The above given REDUCE infix notation can be replaced by FORTRAN or a;
% prefix form. This depends on the current flag settings. But for prin-;
% ting we always use the value of PREFIXLIST,which is in this particu- ;
% lar case :                                                           ;
%                                                                      ;
%   ((S0 PLUS A (TIMES 2 B))                                           ;
%    (S1 SIN S0)                                                       ;
%    (S3 TIMES A B)                                                    ;
%    (S9 TIMES A A)                                                    ;
%    (S8 TIMES A S9)                                                   ;
%    (S7 TIMES S8 S8)                                                  ;
%    (S5                                                               ;
%       PLUS                                                           ;
%       (TIMES 2 S0)                                                   ;
%       (MINUS 677)                                                    ;
%       (TIMES S3 (PLUS (TIMES S0 S1) (TIMES 2 S3 S7))))               ;
%    (S9 TIMES S5 S5)                                                  ;
%    (S8 TIMES S9 S9)                                                  ;
%    (S6 TIMES S8 S8)                                                  ;
%    (U PLUS 1234 (TIMES S6)))                                         ;
%                                                                      ;
% PREFIXLIST is iteratively constructed by the procedure MAKEPREFIXL   ;
% (see CODCTL.RED),by successively using the items of the (global) list;
% CodBexl!* via a ForEach-statement. Such an item is either an index of;
% a row,where the description of the corresponding assignment statement;
% starts(in the above example U) or of a system generated cse-name.    ;
% These alternatives demand for either a call of PRFEXP(rowindex) or of;
% PRFKVAR(cse-name).The routines PR(epare pre)F(ix form of an )EXP(res-;
% sion) and PR(epare pre)F(ix form of an element of)KVAR(lst) call each;
% other and the procedures CONSTR(uct an)EXP(ression),PR(epare the list;
% of operands in pre)F(ix form of the pri)M(.part of an)EX(pression),  ;
% (prepare the list of operands in prefix form of the)COMP(osite part  ;
% of an)EX(pression) and PR(epare in pre)F(ix form a redefinition of a);
% POW(er into a)L(ist of multiplications(i.d. an addition chain mecha- ;
% nism)). The last routine uses the additional procedures PREPPOWLS    ;
% and INSEXPLST. For further comment : see below.                      ;
% -------------------------------------------------------------------- ;

global '(!*prefix !*again)$
fluid '(prefixlist);
prefixlist:=nil;

% -------------------------------------------------------------------- ;
% These globals are already introduced in CODCTL.RED.                  ;
% -------------------------------------------------------------------- ;

symbolic procedure prfexp(x,prefixlist);
% -------------------------------------------------------------------- ;
% arg : X is the CODMAT-index of the row where the description of a top;
%       level sum or product starts.                                   ;
% eff : The prefix definition of this expression ,a dotted pair (name. ;
%       value) is added to PREFIXLIST,in combination with all its cse's;
%       which have to precede the expression when printing the result. ;
%       Since "consing" is used for the construction of PREFIXLIST it  ;
%       ought to be reversed before it can be used for the actual prin-;
%       ting.The cse-ordering is defined by the value of the ORDR-field;
%       of row X of CODMAT,a list built up during input parsing (see   ;
%       CODMAT.RED) and optimization(see CODOPT.RED) using the procedu-;
%       re SETPREV(see CODMAT.RED,part 2).                             ;
% -------------------------------------------------------------------- ;
begin scalar xx,nex;
 if free(x)
 then % Start with cse's.;
 <<foreach y in reverse(ordr x) do
   if constp(y)
   then prefixlist:=prfexp(y,prefixlist)
   else
   <<prefixlist:=prfkvar(y,prefixlist);
     if get(y,'nvarlst)
     then <<prefixlist:=prfpowl(y,prefixlist); remprop(y,'nvarlst)>>
   >>;
    % ---------------------------------------------------------------- ;
    % Continue with expression itself if it has not yet been printed as;
    % part of an addition chain ('Bexl:=T,see PREPPOWLS).              ;
    % ---------------------------------------------------------------- ;
   if not( get(farvar x,'bexl) = x)
    then if nex:=get(farvar x,'nex)
          then << foreach arg in cdr nex do
                   if xx := get(arg, 'rowindex)
                     then prefixlist:=prfexp(xx,prefixlist)
                     else prefixlist:=prfkvar(arg,prefixlist);
                  remprop(car nex, 'kvarlst);
                  % remprop(farvar x,'nex); Needed in cleanupprefixl to
                  %                         handle arrays
                  prefixlist:=(nex.constrexp(x)).prefixlist;
                  symtabrem(nil, farvar x)
               >>
          else prefixlist:=(farvar(x).constrexp(x)).prefixlist
     else remprop(farvar x,'bexl);
   setoccup(x)
 >>;
 return prefixlist
end;


symbolic procedure constrexp(x);
% -------------------------------------------------------------------- ;
% arg : X is the CODMAT-index of the row where the description starts  ;
%       of the expression to be added to PREFIXLIST.                   ;
% res : Construction of the expression in prefix form. The result is   ;
%       used in PRFEXP.                                                ;
% -------------------------------------------------------------------- ;
begin scalar s,ec,opv,ch,ls;
  if (opv:=opval x) eq 'times
  then
  <<s:=append(prfmex(zstrt x,'times),compex chrow x);
    if null(s) then s:=list 0;
    ec:=expcof(x);ls:=length(s);
    if !:onep(ec)
     then if ls>1 then s:='times.s else s:=car(s)
     else
       if !:onep(dm!-minus ec)
        then s:=(if ls>1 then list('minus,'times.s)
                                              else list('minus,car s))
        else
         if !:minusp(ec)
          then s:=list('minus,'times.((dm!-minus ec).s))
          else s:='times.(ec.s)
  >>
  else
    if opv eq 'plus
    then
    <<s:=append(prfmex(zstrt x,'plus),compex chrow x);
      if null(s) then s:=list 0;
      if length(s)>1 then s:='plus.shiftminus(s) else s:=car(s);
      if (ec:=expcof(x))>1 then s:=list('expt,s,ec)
    >>
    else
    <<ch:=chrow(x);
      foreach z in zstrt(x) do
      if null(z)
      then <<s:=constrexp(car ch).s; ch:=cdr(ch)>>
      else s:=z.s;
      s:=car(opv).reverse(s);
      foreach op in cdr(opv) do
      s:=list(op,s);
      if (ec:=expcof x)>1
      then s:=list('expt,s,ec)
    >>;
  return s
end;

symbolic procedure shiftminus(s);
begin scalar ts,head;
 ts:=s; head:=nil;
 while ts and (pairp(car ts) and caar(ts) eq 'minus) do
  << head:=car(ts).head; ts:=cdr ts>>;
 return if ts then append(ts,reverse head) else s
end;

symbolic procedure prfmex(zz,op);
% -------------------------------------------------------------------- ;
% arg : ZZ is a Zstrt and Op an element of {'PLUS,'TIMES}.             ;
% res : List of operands in prefix form,i.e. a list of multiples or a  ;
%       list of powers of variables.                                   ;
% -------------------------------------------------------------------- ;
foreach z in zz collect
begin scalar var,nex;
  var:=farvar(yind z);
  if nex:=get(var,'nex) then << var:=nex; symtabrem(nil,var)>>;
  if var eq '!+one
  then % A constant.;
    if !:minusp(ival(z))
    then return list('minus,dm!-minus(ival(z)))
    else return ival(z);
  if not(!:onep dm!-abs(ival z))
  then
    if op eq 'plus
    then var:=list('times,dm!-abs ival z,var)
    else
      if bval(z)
      then var:=bval(z)
      else var:=list('expt,var,ival z);
  if !:minusp(ival z)
  then var:=list('minus,var);
  return var;
end;

symbolic procedure compex(chr);
% -------------------------------------------------------------------- ;
% arg : Chr is a list of indices of rows where the description starts  ;
%       of (sub)expressions,being composite terms or factors.          ;
% res : A list of these (sub)expressions in prefix form.               ;
% -------------------------------------------------------------------- ;
foreach ch in chr collect
constrexp(ch);

symbolic procedure prfkvar(kv,prefixlist);
% -------------------------------------------------------------------- ;
% arg : Kv is the Car-part of an element (Var.F) of the Kvarlst,where F;
%       is a list of the form (function-name (list of arguments)),if   ;
%       not already added to PREFIXLIST                                ;
% eff : The occurence of Kv in Kvarlst is tested. If Kv is still there ;
%       the corresponding dotted pair is used for extending PREFIXLIST ;
%       before it is removed from Kvarlst.                             ;
% -------------------------------------------------------------------- ;
begin scalar kvl,x,kvl1,nex;
  while kvarlst and not (kv=caar(kvarlst)) do
  <<kvl:=car(kvarlst).kvl;
    kvarlst:=cdr(kvarlst)
  >>;
  if null(kvarlst)
  then
  <<% KVar already printed or redefined as a lhs.;
    kvarlst:=kvl;
    if nex:=get(kv,'nex)
     then prefixlist:=(kv.nex).nexcheck(kv,nex,prefixlist)
  >>
  else
  <<kvl1:=car(kvarlst);
    kvarlst:=append(kvl,cdr kvarlst);
     % Restore Kvarlst before next recursive check;
    foreach var in cddr(kvl1) do
    % ---------------------------------------------------------------- ;
    % Add argument description,if composite,to Prefixlist before func. ;
    % application itself.                                              ;
    % ---------------------------------------------------------------- ;
    if x:=get(var,'rowindex)
       then prefixlist:=prfexp(x,prefixlist)
       else prefixlist:=prfkvar(var,prefixlist);
    if nex:=get(kv,'nex)
     then << prefixlist:=nexcheck(kv,nex,prefixlist);
             kv := nex
         >>;
    prefixlist:=(kv.cdr(kvl1)).prefixlist;
    flag (list (kv),'done)
  >>;
  return prefixlist
end;

symbolic procedure nexcheck(kv,nex,prefixlist);
begin scalar x;
if not (flagp (kv, 'done) or (!*vectorc and subscriptedvarp (car nex)))
  then for each arg in cdr nex do
          if x:=get(arg,'rowindex)
             then prefixlist:=prfexp(x,prefixlist)
             else prefixlist:=prfkvar(arg,prefixlist);
  symtabrem(nil,kv);
  %--------------------------------------------------------------------;
  % Otherwise, this further non-used temporary variable will also be   ;
  % declared.                                                          ;
  %--------------------------------------------------------------------;
  remprop(kv,'nex);
  return prefixlist
end;

symbolic procedure evalpartprefixlist(prefixlist);
% ------------------------------------------------------------------- ;
% Evaluate partially the elements of Prefixlist leading to a list of  ;
% (sub)expressions, which have either PLUS or MINUS as their leading  ;
% operator.                                                           ;
% ------------------------------------------------------------------- ;
begin scalar newprefixlist,pair,temp;
 while not null prefixlist do
  <<if pair:=evalpart1 car prefixlist
     then newprefixlist:=pair.newprefixlist;
    prefixlist:=cdr prefixlist
  >>;
 foreach item in get('evalpart1,'setklist) do
  << remprop(item,'avalue);
     if (temp:=get(item,'taval))
      then <<setk(item,mk!*sq simp!* temp); remprop(item,'taval) >>
  >>;
 remprop('evalpart1,'setklist);
 return reverse(newprefixlist)
end;

symbolic procedure evalpart1 pair;
begin scalar carpair,exp,res,x;
 exp:=!*exp; !*exp:=t;
 carpair:= car pair;
 x:=reval cdr pair;
 if not (atom(x) or (car x memq '(plus difference))) and
    flagp(carpair,'newsym)
  then << if (get(carpair,'avalue)) and not(get(carpair,'taval))
           then  put(carpair,'taval,prepsq cadadr get(carpair,'avalue));
          setk(carpair,aeval(x))
       >>
  else res:=(carpair).x;
 if null res
  then put('evalpart1,'setklist,(carpair).get('evalpart1,'setklist));
 !*exp:=exp;
 return res
end;

symbolic procedure removearraysubstitutes(prefixlist);
% ------------------------------------------------------------------- ;
% When arrayelements form rhs's in pairs of prefixlist, used to       ;
% produce output, the cse-names, used to denote them in the rest of   ;
% prefixlist, are replaced by these arrayelements if the arrayname    ;
% occurs in the GENTRAN symboltable, used for making declarations.    ;
% ------------------------------------------------------------------- ;
begin scalar newprefixlist,pair;
 while not null prefixlist do
  << pair:= car prefixlist; prefixlist:=cdr prefixlist;
     if flagp(car pair,'newsym)
         and
        (pairp(cdr pair) and subscriptedvarp(cadr pair))
      then
       prefixlist:=(foreach item in prefixlist collect
                                     subst(cdr pair,car pair,item))
    %subst(cdr pair,car pair,car item).subst(cdr pair,car pair,cdr item)
      else
       newprefixlist:=pair.newprefixlist;
  >>;
 return reverse newprefixlist
end;


% -------------------------------------------------------------------- ;
% COMPUTATION RULES FOR POWERS : AN ADDITION CHAIN MECHANISM           ;
%                                                                      ;
% The above given Optimizer output contains the following subsequences ;
%  ................                                                    ;
%  S9 := A * A          A ^ 2   ( 2 = 1 + 1 )                          ;
%  S8 := A * S9         A ^ 3   ( 3 = 2 + 1 )                          ;
%  S7 := S8 * S8        A ^ 6   ( 6 = 3 + 3 )                          ;
%  ................                                                    ;
%  S9 := S5 * S5       S5 ^ 2   ( 2 = 1 + 1 )                          ;
%  S8 := S9 * S9       S5 ^ 4   ( 4 = 2 + 2 )    S9 is re used         ;
%  S6 := S8 * S8       S5 ^ 8   ( 8 = 4 + 4 )    S8 is re used         ;
%                                                                      ;
% Printing a view on CODMAT (after the above given output is produced) ;
% using the procedure PRIMAT (see part 1 of this module) shows:        ;
%                                                                      ;
%  Sumscheme :                                                         ;
%                                                                      ;
%     |  7 11 12 13| EC|Far                                            ;
%  ------------------------                                            ;
%    0|          X |  1| U                                             ;
%    5|     1  2   |  1| S0                                            ;
%   10|            |  1| 9                                             ;
%   12|  2       X |  1| S5                                            ;
%  ------------------------                                            ;
%  The following integers ought tp replace the X-entries of the matrix ;
%  in a left-to-right-and-top-down order : 1234  -677                  ;
%  7  : S0                                                             ;
%  11 : A                                                              ;
%  12 : B                                                              ;
%  13 : +ONE                                                           ;
%                                                                      ;
%  Productscheme :                                                     ;
%                                                                      ;
%     |  1  3  4  8  9 10| EC|Far                                      ;
%  ------------------------------                                      ;
%    1|  8               |  1| 0                                       ;
%    3|     1     1      |  1| 10                                      ;
%    6|        1     6   |  2| 10                                      ;
%    8|              1  1|  1| S3                                      ;
%    9|        1         |  1| 12                                      ;
%  ------------------------------                                      ;
%  1  : S5                                                             ;
%  3  : S0                                                             ;
%  4  : S3                                                             ;
%  8  : S1=SIN(S0)                                                     ;
%  9  : A                                                              ;
%  10 : B                                                              ;
%                                                                      ;
% S5 ^ 8 and A ^ 6 are still there,in contrast to S6,S7,S8 and S9, be- ;
% cause the latter group is produced in a different way. S6 and S7 are ;
% generated via PREPPOWLS,called in PREPFINALPLST(see CODCTL.RED),acti-;
% vated in MAKEPREFIXL, assuming OFF AGAIN holds.                      ;
% In PREPPOWLS the Nvarlst's ((8.S6)(1.S5)) and ((6.S7)(1.A)) are made ;
% and via their property lists associated with S5 and A,respectively.  ;
% These lists are used in PRFPOWL to produce the above given chains.   ;
% The addition chain-like algorithm used is reflected by the structure ;
% of PRFPOWL : Given a list of at least two exponents(integers),being  ;
% the Car's of the elements of Nvarlst,produce an intuitively minimal  ;
% number of additions by halving even numbers and by making odd numbers;
% even by substracting 1. Hence (63 1) leads to :                      ;
% 63=62+1,62=31+31,31=30+1,30=15+15,15=14+1,14=7+7,7=6+1,6=3+3,3=2+1,  ;
% 2=1+1. Since the Nvarlst might be longer,for instance (63 28 15 1),  ;
% PRFPOWL allows a more general approach,which for example leads to :  ;
% 63=62+1,62=31+31,31=28+3,28=15+13,15=13+2,13=12+1,12=6+6,6=3+3,3=2+1,;
% 2=1+1.                                                               ;
% -------------------------------------------------------------------- ;

symbolic procedure preppowls;
% -------------------------------------------------------------------- ;
% eff : This procedure is called before the actual printing starts,i.e.;
%       before PREFIXLIST is made. This allows to refer to results     ;
%       produced by this routine in PRFEXP at two different places. The;
%       value of the indicators 'Nvarlst(i.e. exists such a list?) and ;
%       'Bexl(=T if the corresponding (sub)expression name is part of a;
%       chain) are used in PRFEXP.                                     ;
%       The Zstrt's of all relevant 'TIMES-columns are analysed. If non;
%       one elements occur they are stored in a so called Nvarlst,asso-;
%       ciated with these relevant columns as value of the indicator   ;
%       'Nvarlst,which is put on the property list of the variable gi- ;
%       ving the column its identity via its FarVar-value. Nvarlst is a;
%       list of pairs (exponent=IVal(Zstrt-element) . associated name).;
%       This name can be newly generated(such as S6 and S7 in the above;
%       example) or already exist if,for instance, FarVar^exponent is  ;
%       itself an expression.This is marked with the indicator 'Bexl=T.;
%       The incorporation of this expression in PREFIXLIST is now done ;
%       via the production of the addition chain,implying that it is no;
%       longer necessary to treat it seperately.                       ;
% -------------------------------------------------------------------- ;
begin scalar var,nvar,nvarlst,rindx;
  for y:=rowmin:(-1) do
  if not numberp(var:=farvar y) and opval(y) eq 'times
  then
  <<foreach z in zstrt(y) do
    if ival(z)=1
    then setbval(z,var)
    else
    <<rindx:=xind(z);
      setprev(rindx,var);
      if not nvarlst then nvarlst:=list(1 . var);
      if numberp(nvar:=farvar rindx) or pairp(nvar) or
         not (null(cdr zstrt rindx) and null(chrow rindx)
         and expcof(rindx)=1)
       then nvar:=fnewsym()
       else put(nvar,'bexl,rindx);
      setbval(z,nvar);
      setzstrt(rindx,inszzzr(mkzel(y,ival(z).nvar),
                             delyzz(y,zstrt rindx)));
      nvarlst:=insexplst(ival(z).nvar,nvarlst);
    >>;
    if nvarlst then <<put(var,'nvarlst,nvarlst);
                      nvarlst:=nil>>
  >>;
terpri()
end$

symbolic procedure prfpowl(y,prefixlist);
% -------------------------------------------------------------------- ;
% arg : Y is a variable with an NVarlst in its property list.          ;
% res : The NVarlst is used to produce an addition chain in the above  ;
%       suggested way.Its is produced in the form of a list Powlst of  ;
%       dotted pairs which can be included in PREFIXLIST directly. So  ;
%       the pairs have a name as Car-part and a product of 2 variables ;
%       as Cdr-part.                                                   ;
% -------------------------------------------------------------------- ;
begin scalar nvarlst,explst,first,cfirst,csecond,diff,var,
             powlst,var1,var2;
  nvarlst:=explst:=get(y,'nvarlst);
  repeat
  <<first:=car(explst);
    cfirst:=car(first);
    csecond:=caar(explst:=cdr explst);
    diff:=cfirst-csecond;
    if diff>csecond
    then
    <<if remainder(cfirst,2)=1
      then
      <<cfirst:=cfirst-1;
        var:=fnewsym();
        powlst:=(cdr(first).list('times,y,var)).powlst;
        first:=(cfirst.var);
        nvarlst:=first.nvarlst
      >>;
      diff:=csecond:=cfirst/2;
    >>;
    if null(assoc(diff,nvarlst))
    then
    <<var:=fnewsym();
      nvarlst:=(diff.var).nvarlst
    >>;
    var1:=cdr(assoc(diff,nvarlst));
    var2:=cdr(assoc(csecond,nvarlst));
    powlst:=(cdr(first).list('times,var1,var2)).powlst;
    explst:=insexplst((diff.var1),explst);
  >>
  until diff=csecond and csecond=1;
  prefixlist:=append(reverse(powlst),prefixlist);
  return prefixlist
end;

symbolic procedure insexplst(el,explst);
% -------------------------------------------------------------------- ;
% arg : EL is a dotted pair (integer . name). Explst is a list of such ;
%       dotted pairs . The car-parts of the list elements define a de- ;
%       cending order for the elements of Explst.                      ;
% res : EL is inserted in Explst,but only if the Car-part was not yet  ;
%       available.                                                     ;
% -------------------------------------------------------------------- ;
if null(explst) or car(el)>caar(explst)
then el.explst
else
  if car(el)=caar(explst)
  then explst
  else car(explst).insexplst(el,cdr explst);


% -------------------------------------------------------------------- ;
% PART 3 : IMPROVEMENT OF THE FINAL FORM OF PREFIXLIST                 ;
% -------------------------------------------------------------------- ;
% The function CleanupPrefixlist is used in MakePrefixlist, defined in ;
% CODCTL.RED, for back substitution of identifiers, which occur only   ;
% once in  the set of right hand sides, defining the optimized version ;
% of the input.                                                        ;
% -------------------------------------------------------------------- ;

global '(codbexl!*);

symbolic procedure aliasbacksubst(pfl);
%--------------------------------------------------------------------
% pfl : list of (lhsides . rhsides) in reverse order.
% ret : new pfl with no more superfluous aliases in correct order.
%--------------------------------------------------------------------
begin
  scalar backsubstlist,original,lhs,npfl;
  backsubstlist := rhsaliases;
  foreach stat in reverse pfl do
  <<if (original:=get((lhs:=car stat),'alias))
     then % lhs is an alias.
          % Should it be backsubstituted ?
          if (atom original)
                       % lhs was a dependence-alias.
                       % Backsubstitute !
             or
             eq(lhs,cadr assoc(original,get(car original,'finalalias)))
                       % Output-occurence of original.
                       % Backsubstitute !
             or
             vectorvarp(car original)
                       % User wants backsubstitution.
                       % Backsubstitute !
             then backsubstlist:= (lhs . original) . backsubstlist
             else original := nil;
     npfl:=((if original then original else lhs)
           .
           recaliasbacksubst(cdr stat,backsubstlist)) . npfl;
   >>;
   return reverse npfl;
   end;

symbolic procedure recaliasbacksubst(ex, al);
%---------------------------------------------------------------
% Commit the actual backsubstitution.
%---------------------------------------------------------------
if atom ex or constp(ex)
   then if assoc(ex,al)
           then cdr assoc(ex,al)
           else ex
   else foreach el in ex collect recaliasbacksubst(el,al);

symbolic procedure reinsertratexps (ppl,pfl);
% ----------------------------------------------------------------
% All rational exponents, collected in the preprefixlist, are
% reinserted in the prefixlist, in a position defining them just
% before their use.
% ----------------------------------------------------------------
begin
  scalar keys,npfl;
  keys:= foreach re in ppl collect car re;
  for each stat in pfl do
           << foreach k in keys do
                      if not freeof(cdr stat, k)
                         then << npfl:=assoc(k,ppl) . npfl;
                                 keys:=delete(k,keys)
                              >>;
              npfl:=stat . npfl
            >>;
  return reverse npfl
  end;

symbolic procedure cleanupvars (p,pfl);
% ----------------------------------------------------------------
% Remove all generated flags and properties w.r.t. aliases.
% ----------------------------------------------------------------
begin scalar csenow,lp,dp,pn,sv;
  csenow:=fnewsym();
  lp:=letterpart csenow;
  dp:=digitpart csenow;
  pn:=for idx:=0:dp collect mkid(lp,idx);
  if !*again and not !*vectorc
     then <<foreach f in pfl do
             if atom(car f) and flagp(car f,'inalias)
                then<< remflag(list car f,'inalias);
                       remflag(list car f, 'newsym) >>
          >>
     else if not !*again
             then <<foreach v in pn do
                       if (sv:=get(v,'alias))
                           then <<if pairp(sv) then sv:=car sv;
                                  remprop(sv,'finalalias);
                                  foreach a in get(sv,'aliaslist) do
                                   << remprop(a,'alias);
                                      foreach a2 in get(a,'aliaslist)
                                        do remprop(a2,'alias);
                                      remprop(a,'finalalias);
                                   >>;
                                 remprop(sv,'aliaslist);
                                >>;
                     % remove all garbage from variables.
                     pn := append(pn,p);
%                           foreach el in p collect
%                              if atom el then el else car el);
                     remflag(pn,'subscripted);
                   % remflag(pn,'vectorvar); % This is user-controlled
                                             % JB 16/3/94
                     remflag(pn,'inalias);
                     remflag(pn,'aliasnewsym);
                  >>;
  end;

symbolic procedure listeq(a,b);
if atom a
   then eq(a,b)
   else if atom b
           then nil
           else listeq(car a, car b)
                and listeq(cdr a, cdr b);

symbolic smacro procedure protected(a,pn);
member((if atom a then a else car a), pn);

symbolic smacro procedure protect(n,pn);
if member((if atom n then n else car n),pn)
   then pn
   else (if atom n then n else car n). pn;

symbolic procedure cleanupprefixlist(prefixlist);
% -------------------------------------------------------------------- ;
% This procedure is used for making the final version of the prefix-   ;
% list. The prefixlist is made shorter by substituting some assign-    ;
% ments occuring in the prefixlist in expressions in the other assign- ;
% ments in the list.                                                   ;
% The following cases are considered:                                  ;
%                                                                      ;
% 1)    :                                                              ;
%    a := (-)constant     a is not protected i.e. not an output var.   ;
%    . := ...a...      T  a -> (-)constant (old -> new) is substituted ;
%       :              v                in this part of the prefixlist ;
%                                                                      ;
% 2)    :                                                              ;
%    a := expression      a not protected, this assignment is removed  ;
%    . := ...a...      T  this is the only place where a is used       ;
%       :              v  a -> expression substituted in this part     ;
%                                                                      ;
% 3)    :                                                              ;
%    b := ...                                                          ;
%    . := ...b...                                                      ;
%    a := (-)b            a not protected, this assignment is removed  ;
%    . := ...b...      T  a -> (-)b substituted in this part of the    ;
%    . := ...a...      |            prefixlist                         ;
%       :              v                                               ;
%                                                                      ;
% 4)    :                                                              ;
%    b := ...          T  b not protected, changed to a := ...         ;
%    . := ...b...      |                                               ;
%    a := b            |  a protected, this assignment is removed      ;
%    . := ...b...      |  b -> a substituted in this part of the       ;
%    . := ...a...      |         prefixlist                            ;
%       :              v                                               ;
%                                                                      ;
% 5)    :                                                              ;
%    b := ...          T  b not protected, changed to a:= ...          ;
%    . := ...b...      |                                               ;
%    a := -b           |  a protected, this assignment is removed      ;
%    . := ...b...      |  b -> a and a -> -a substituted in this part  ;
%    . := ...a...      |                     of the prefixlist         ;
%       :              v                                               ;
%                                                                      ;
% Substitution-rules are collected in a list called SUBSTLST.          ;
% All assignments in the prefixlist are treated one by one.            ;
% First, all substitutions are made in the assignment. Second, the     ;
% resulting assignment is checked if it leads to a new substitution as ;
% described in 1) - 3). If so, the new substitution is added to the    ;
% substitutionlist.                                                    ;
% Note that substitutions of kind 4) and 5) require substitutions in   ;
% assignments prior to the one that is treated (a := (-)b). Therefore  ;
% these substitutions are collected before the actual cleaning-up.     ;
% These backward-substitutions may not contain subscripted variables.  ;
% This constraint is made because of the following reasons:            ;
%  - Substitution of b -> a[i] can introduce an assignment at a point  ;
%    where i is not yet calculated.                                    ;
%  - As substitutions are not applied to the substitutes, b -> a[expr] ;
%    can become invalid by a substitution of/in expr.                  ;
%  - The second reason also applies to b[i] -> a.                      ;
%  - b -> a[i] introduces more accesses of a[i] which are slower than  ;
%    accesses of b.                                                    ;
%  - b[i] -> a cannot occur because subscripted variables are output-  ;
%    variables and therefore protected.                                ;
% When, during the cleanup, a substitution is formed concerning a      ;
% variable already involved in a backward-substitution this backward-  ;
% substitution is overrided (i.e. removed) and the new substitution is ;
% added to the substitutionlist.                                       ;
% Variables:                                                           ;
%    protectednames  : output variables                                ;
%    defvarlst       : list of variables defined in the prefixlist     ;
%    rhsocc          : ((var . #) ...) where # = number of times that  ;
%                      var occurs in the rhs or in a subscript of a    ;
%                      lhs in an assignment in the prefixlist          ;
%    substlst        : ((old . new) ...), substitutionlist             ;
%    dellst          : list of indices of assignments in the prefixlist;
%                      which must be removed because of a backward-    ;
%                      substitution                                    ;
% -------------------------------------------------------------------- ;
begin scalar lpl,protectednames,j,item,substlst,dellst,se,ose,
             r,defvarlst,rhsocc,occ,var,sgn,lhs,rhs;
  % -------------------------------------------------------------------
  %  Add rational exponentexpressions to prefixlist.
  % -------------------------------------------------------------------
  if preprefixlist
     then prefixlist:=reinsertratexps(preprefixlist, prefixlist);
  % -------------------------------------------------------------------
  %  Ensure backsubstitution of `aliased' output-variables.
  % -------------------------------------------------------------------
  prefixlist := aliasbacksubst(reverse prefixlist);
  lpl:=length(prefixlist);
  lhs:=mkvect(lpl); rhs:=mkvect(lpl);
  % -------------------------------------------------------------------
  %  Determine protected names.
  % -------------------------------------------------------------------
  foreach indx in codbexl!* do
    if numberp(indx) then
       <<if var:=get(farvar indx,'nex)
          then protectednames:= protect(var,protectednames)
          else if not flagp(farvar indx,'aliasnewsym)
               then protectednames:=protect(farvar(indx),protectednames)
                else if (var:=get(farvar(indx),'alias))
                      then protectednames := protect(var,protectednames)
        >>
       else if idp(indx) then
               if not flagp(indx,'aliasnewsym)
                  then protectednames:=protect(indx, protectednames)
                  else if (var:=get(indx,'alias))
                          then protectednames
                                  := protect(var, protectednames);
  % -------------------------------------------------------------------
  % Preliminaries.
  % -------------------------------------------------------------------
  j:=0;
  foreach item in prefixlist do
     <<  % Build lhs and rhs vectors
       putv(lhs,j:=j+1,car item);
       putv(rhs,j,cdr item);
         % Remove now redundant information.
       se := nil;
       if pairp(cdr item) and get (se := cadr item, 'kvarlst)
          then remprop (se ,'kvarlst);
       if flagp (se,'done)
          then remflag (list(se),'done);
         % Build defvarlst
       defvarlst:=(car(item) . j) . defvarlst;
         % Build variable occurences lists
       if pairp car(item)
          then rhsocc:=insoccs(car(item),rhsocc);
       rhsocc:=insoccs(cdr(item),rhsocc);
         % Determine backward substitutions
       sgn:=nil;
       if eqcar(cdr item,'minus) then
       << sgn:=t;
          item:=car(item).caddr(item)>>;
       if idp(cdr item) and
          (protected(car item, protectednames) and
              not protected(cdr item,protectednames)) and
          not(get(car item,'finalalias) and pairp(car item)) and
          (r:= assoc(cdr item, defvarlst)) and
          not(assoc(cdr item,substlst)) and
          movable(item,defvarlst)
          then << dellst:=car(item).dellst;
                  substlst:=substel(cdr(item).car(item),sgn).substlst;
                  if sgn and r then
                     <<% We 've found : S0 := blah
                       %                A  := - S0
                       % This becomes : A  := - blah,
                       % and further occurences of S0 will be replaced
                       % by: - A
                       % The actual substitution takes now place.
                       % We also create a nonsense-statement at here,
                       % to be deleted later on.
                       putv(rhs,cdr r,('minus . list getv(rhs,cdr r)));
                       putv(lhs,cdr r,getv(lhs,j));
                       putv(rhs,j,(getv(lhs,j)))>>
               >>;
     >>;
  % -------------------------------------------------------------------
  % Do the cleaning up!
  % -------------------------------------------------------------------
  for j:=1:lpl do
     <<if member(getv(lhs,j),dellst)
          and (not protected(getv(lhs,j),protectednames)
          or eq(getv(lhs,j),getv(rhs,j)))
          then % line j is deleted by a backward substitution
               <<putv(lhs,j,nil);
                 putv(rhs,j,nil)
               >>
          else % Do the substitutions
               <<if pairp(getv(lhs,j))
                    then putv(lhs,j,replacein(getv(lhs,j),substlst));
                 putv(rhs,j,replacein(getv(rhs,j),substlst));
                 % Determine a substitution
                 item:=getv(lhs,j).getv(rhs,j);
                 sgn:=nil;
                 if eqcar(cdr item,'minus)
                    then <<sgn:=t;
                           item:=car(item).caddr(item)>>;
                 se:=nil;
                 if listeq(car item,cdr item)
                    then % We created nonsense like ( a . a )
                         <<putv(lhs,j,nil);
                           putv(rhs,j,nil)>>
                    else
                    <<if constp(cdr item) and
                         not protected(car item,protectednames)
                         then % a := (-)constant
                           se:=substel(item,sgn)
                         else if (if atom(cdr item)
                                   then idp(cdr item)
                                   else subscriptedvarp(cadr item))
                              and not protected(car item,protectednames)
                              then % a := (-)b, a not protected
                                se:=substel(item,sgn)
                              else
                               if not protected(car item,protectednames)
                                  and (occ:=assoc(car item,rhsocc))
                                  and cdr(occ)=1
                                  then % a:=(-)b,a not protected
                                       % and used once
                                       se:=substel(item,sgn);
                     >>;
                 % Add the substitution
                 if se
                 then <<if ose:=assoc(car se,substlst) then
                           % remove backward-substitution
                           << substlst:=delete(ose,substlst);
                              substlst:=
                                delete(substel(cdr(ose).cdr(ose), t),
                                               substlst);
                              dellst:=delete(cdr ose,dellst)
                           >>;
                        substlst:=se.substlst;
                        putv(lhs,j,nil); % delete current assignment
                        putv(rhs,j,nil)
                      >>
                 else if (se:=assoc(car item,substlst)) and
                         not(protected(car item, protectednames) and
                             eq(j,cdr assoc(car item,defvarlst)))
                         then % backward-substitution of lhs
                              putv(lhs,j,cdr se)
                         else if se
                                 then % This is an output occurrence
                                      substlst:=delete(se,substlst);
               >>
     >>;
  % -------------------------------------------------------------------
  % Determine new prefixlist
  % -------------------------------------------------------------------
  prefixlist:=nil;
  for j:=1:lpl do
     if getv(lhs,j)
        then prefixlist:=(getv(lhs,j).getv(rhs,j)).prefixlist;
  % -------------------------------------------------------------------
  % Check on minimumlength requirements.
  % -------------------------------------------------------------------
  if min!-expr!-length!*
     then prefixlist:=
                     make_min_length(reverse prefixlist,protectednames)
     else prefixlist:=reverse prefixlist;
  % -------------------------------------------------------------------
  % Undo temporary value-backup and remove rubbish.
  % -------------------------------------------------------------------
  apply1('arestore,avarlst);   % For bootstrapping.
  cleanupvars(protectednames,prefixlist);
  % -------------------------------------------------------------------
  % Finish.
  % -------------------------------------------------------------------
  return prefixlist
  end$

symbolic procedure movable(v,defl);
%---------------------------------------------------------------------;
% We have to avoid that a subscripted variable is moved outside the
% scope of a cse-definition it depends upon. We can check this by
% comparing the new position and the position of the cse in the defl.
% ------------------------------------------------------------------- ;
  if pairp car(v)
     then
       not(nil member foreach idx in cdar v collect
                (numberp(idx) or
                if assoc(idx, defl)
                   then (cdr assoc(idx,defl) < cdr assoc(cdr v,defl))
                   else t))
     else t$

symbolic procedure insoccs(x,occlst);
begin
   if idp(x) or subscriptedvarp(x) or
      ((pairp x) and (subscriptedvarp car x))
      then occlst:=insertocc(occlst,x);
   if not(idp x) and not(constp x) then
      foreach arg in cdr x do
         occlst:=insoccs(arg,occlst);
   return occlst
end;


symbolic procedure insertocc(occlst,var);
begin scalar oldel;
   if oldel:=assoc(var,occlst) then
      occlst:=subst((var.(cdr(oldel)+1)),oldel,occlst)
   else
      occlst:=(var.1).occlst;
   return occlst
end;

symbolic procedure substel(oldnew,sign);
   car(oldnew).(if sign then list('minus,cdr oldnew) else cdr oldnew);

symbolic procedure replacein(expr1,sl);
% -------------------------------------------------------------------- ;
% All substitutions in sl are applied to expr1.                        ;
% In the resulting expression,                                         ;
%    (times 1 rest)        is replaced by (times rest)                 ;
%    (plus 0 rest)                     by (plus rest)                  ;
%    (minus (minus expr))              by expr                         ;
%    (minus 0)                         by 0                            ;
%    (times 1)                         by 1                            ;
%    (plus 0)                          by 0                            ;
%    (expt expr 0)                     by 1                            ;
%    (expt expr 1)                     by expr                         ;
%    (quotient expr 1)                 by expr                         ;
% -------------------------------------------------------------------- ;
begin scalar nexpr,iszero;
   return
      if idp(expr1) or subscriptedvarp(expr1) then
         if (nexpr:=assoc(expr1,sl)) then cdr(nexpr) else expr1
      else if constp expr1 then
         expr1
      else
      << nexpr:=foreach el in cdr(expr1) collect replacein(el,sl);
         expr1:=append(list(car expr1),nexpr);
         if eqcar(expr1,'minus) and eqcar(cadr expr1,'minus) then
            expr1:=cadadr expr1;
         if eqcar(expr1,'plus) then
         << nexpr:='(plus);
            foreach el in cdr(expr1) do
               if not(constp(el) and !:zerop(el)) then
                  nexpr:=append(nexpr,
                       (if eqcar(el,'plus) then cdr(el) else list(el)));
            expr1:=nexpr
         >>
         else if eqcar(expr1,'times) then
         << iszero:=nil;
            nexpr:='(times);
            foreach el in cdr(expr1) do
            << if not(constp(el) and !:onep(el)) then
                  nexpr:=append(nexpr,
                      (if eqcar(el,'times) then cdr(el) else list(el)));
               if constp(el) and !:zerop(el) then iszero:=t
            >>;
            expr1:=if iszero then 0 else nexpr
         >>
         else if eqcar(expr1,'quotient) and constp(caddr expr1) and
                    !:onep(caddr expr1) then
            expr1:=cadr(expr1)
         else if eqcar(expr1,'quotient) then
            expr1:=qqstr!?(expr1)
         else if eqcar(expr1,'minus) and constp(cadr expr1) and
                    !:zerop(cadr expr1) then
            expr1:=0
         else if eqcar(expr1,'expt) and constp(caddr expr1) then
            if !:zerop(caddr expr1) then
               expr1:=1
            else if !:onep(caddr expr1) then
               expr1:=cadr expr1;
         if pairp(expr1) and memq(car expr1,'(times plus)) then
            if length(expr1)=2 then
               expr1:=cadr expr1
            else
               if length(expr1)=1 then
                  expr1:=if expr1='plus then 0 else 1;
         expr1
      >>
end$

symbolic procedure qqstr!?(expr1);
 begin scalar nr,dm,nr2,dm2;
  nr:=cadr expr1; dm:=caddr expr1;
  if eqcar(nr,'quotient)
   then << dm2:=caddr nr; nr:=cadr nr>>
  else if eqcar(nr,'times)
   then nr:=foreach fct in nr collect
             if eqcar(fct,'quotient)
              then << dm2:=caddr fct; cadr fct>>
              else fct;
  if eqcar(dm,'quotient)
   then <<nr2:=caddr dm; dm:=cadr dm>>
  else if eqcar(dm,'times)
   then dm:=foreach fct in dm collect
             if eqcar(fct,'quotient)
              then << nr2:=caddr fct; cadr fct>>
              else fct;
  if dm2 then dm:=append(list('times,dm2),
                         if eqcar(dm,'times) then cdr dm else list dm);
  if nr2 then nr:=append(list('times,nr2),
                         if eqcar(nr,'times) then cdr nr else list nr);
  return(list('quotient,nr,dm))
end;

endmodule;
end;
