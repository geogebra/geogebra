module codmat;  %  Support for matrix optimization.

% -------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer    ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands. ;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, M.C. van Heerwaarden,   ;
%             J.C.A. Smit, W.N. Borst.                                 ;
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


% -------------------------------------------------------------------- ;
% The module CODMAT consists of two parts:                             ;
%  1 - A collection of Extended Access Functions to the CODMAT-matrix  ;
%      and the associated hashvector CODHISTO.                         ;
%  2 - Routines for constructing the incidence matrix CODMAT via par-  ;
%      sing and storage of a set of input expressions.                 ;
%  3 - Routines for removing gcd's from quotients.                     ;
% -------------------------------------------------------------------- ;
%                                                                      ;
% -------------------------------------------------------------------- ;
% PART 1 : EXTENDED ACCESS FUNCTIONS                                   ;
% -------------------------------------------------------------------- ;
%                                                                      ;
% These functions allow to STORE,RETRIEVE or MODIFY information stored ;
% in CODMAT and CODHISTO, used for hashing.                            ;
% Remark:A detailed description of the vectors CODMAT and CODHISTO and ;
% their DIRECT ACCESS FUNCTIONS, heavily used here, is given in the    ;
% module COSYMP.                                                       ;
%                                                                      ;
% ------ A CLASSIFICATION OF THE EXTENDED ACCESS FUNCTIONS ------      ;
%                                                                      ;
% - STORAGE : SetRow,InsZZZ,InsZZZn,InsZZZr,PnthXZZ.                   ;
% - HISTOGRAM OPERATIONS : InsHisto,DelHisto,Downwght,Downwght1,Upwght,;
%   Upwght1,Initwght.                                                  ;
% - MODIFICATION : Rowdel,Rowins,RemZZZZ,Chdel,DelYZZ,Clearrow.        ;
% - PRINTING TESTRUNS : ChkCodMat.                                     ;
%                                                                      ;
% ------ TERMINOLOGY USED ------                                       ;
% ZZ stands for a Zstrt and Z for a single item in ZZ.  A Zstrt is a   ;
% list of pairs (row(column)index . coeff(exponent)information).Hence a;
% double linked list representation is used. Both X and Y denote indi- ;
% ces.The Cdr-part of a Z-element is in fact again a dotted pair (IVal.;
% BVal). The BValue however is only used in CODPRI.RED for printing    ;
% purposes,related to the finishing touch. Therefore we only take IVal ;
% as Cdr-part in the                                                   ;
% Example :                               +| a b c d                   ;
%  Let                                    -+---------                  ;
%  f =  a + 2*b + 3*c                     f| 1 2 3                     ;
%  g =2*a + 4*b + 5*d                     g| 2 4   5                   ;
%                                                                      ;
% Taking MaxVar=4 results in :                                         ;
%                                                                      ;
% CODMAT index=|I| |Zstrt ZZ            |                              ;
% -------------+-+-+--------------------+----------------------------- ;
% .......      | | |                    |Rows: Structure created by    ;
% .......      | | |                    |Fvar or FFvar using I=MaxVar+ ;
% .......      | | |                    |RowMax (See Row and FillRow,  ;
% Rowmax= 1    |5|g|((-4.5)(-2.4)(-1.2))|defined in module COSYMP      ;
% Rowmax= 0    |4|f|((-3.3)(-2.2)(-1.1))|and used in SETROW).          ;
% -------------+-+-+--------------------+----------------------------- ;
% Rowmin=-1    |3|a|((1.2)(0.1))        |Columns:Created by SSetVars(  ;
% Rowmin=-2    |2|b|((1.4)(0.2))        |part 2 of this module) : I=   ;
% Rowmin=-3    |1|c|((0.3))             |Maxvar+Rowmin. The Zstrts of  ;
% Rowmin=-4    |0|d|((1.5))             | the rows are also completed  ;
% .......      | | |                    | by SSetvars.                 ;
% -------------------------------------------------------------------- ;
%                                                                      ;
% Remarks :                                                            ;
% -1- The CODMAT index I used in the above example is thus the physical;
%     value of the subscript. This in contrast to the indices used when;
%     calling routines like SETROW, which operate on Rowmax or Rowmin  ;
%     values (details are given in CODCTL.RED and in the routine ROW in;
%     COSYMP.RED).                                                     ;
% -2- A similar picture is produced for f=a*b^2*c^3 and g=a^2*b^4*d^5. ;
%     When introducing monomials as terms or sum as factors also the   ;
%     Child-facilities have to be used like done for operators other   ;
%     than + or *.                                                     ;
% -------------------------------------------------------------------- ;

symbolic$

global '(codmat maxvar rowmin rowmax endmat codhisto headhisto
         !*vectorc !*inputc known rhsaliases);

fluid '(preprefixlist prefixlist);

switch vectorc$

!*vectorc := nil$

% ____________________________________________________________________ ;
% A description of these globals is given in the module CODCTL         ;
% -------------------------------------------------------------------- ;

symbolic procedure setrow(n,op,fa,s,zz);
% -------------------------------------------------------------------- ;
% arg : N : Row(column)index of the row(column) of which the value has ;
%           to be (re)set. Physically we need MaxVar + N(see ROW in    ;
%           COSYMP.RED).                                               ;
%       Op: Operator value to be stored in Opval,i.e. 'PLUS,'TIMES or  ;
%           some other operator.                                       ;
%       Fa: For a row the name (toplevel) or index (subexpression) of  ;
%           the father.For a column the template of the column variable;
%       S : Compiled code demands atmost 5 parameters,atleast for some ;
%           REDUCE implementations. Therefore S stands for a list of   ;
%           Chrow information,if necessary extended with the monomial  ;
%           coefficient(Opval='TIMES) or the exponent of a linear ex-  ;
%           pression(Opval='PLUS),to be stored in the CofExp-field.    ;
%       ZZ: The Z-street.                                              ;
% eff : Row(column) N is created and set. If necessary,i.e. if N>MaxVar;
%       then CODMAT is doubled in size.                                ;
% -------------------------------------------------------------------- ;
begin scalar codmat1;
  if abs(n)>maxvar
  then % Double the size of CODMAT.
    <<codmat1:=mkvect(4*maxvar);
      for x:=max(rowmin,-maxvar):min(rowmax,maxvar) do
      putv(codmat1,x+2*maxvar,row x);
      codmat:=codmat1;
      maxvar:=2*maxvar;
    >>;
 % --------------------------------------------------------------------;
 % Now the values are set,using LenCol=4 and LenRow=8,i.e. the fields  ;
 % Chrow,CofExp,HiR and Ordr are not in use for columns because:       ;
 % - Chrow and CofExp are irrelevant for storing information about     ;
 %   variable occurrences.                                             ;
 % - Hashing(HiR) and CSE-insertion(Ordr) are based on row-information ;
 %   only.                                                             ;
 % --------------------------------------------------------------------;
  if n<0
  then fillrow(n,mkvect lencol)
  else
  <<fillrow(n,mkvect lenrow);
    setchrow(n,car s);
    if cdr s
    then setexpcof(n,cadr s)
    else setexpcof(n,1)>>;
  setfree(n);
  setopval(n,op);
  setfarvar(n,fa);
  setzstrt(n,zz)
end;

symbolic procedure inszzz(z,zz);
% -------------------------------------------------------------------- ;
% arg : Z : A matrix element.                                          ;
%       ZZ: A set of matrix elements with indices in descending order. ;
% eff : A set of matrix elements including Z and ZZ,again in ascending ;
%       order,such that in case Z's index already exists the Ival-     ;
%       parts of both elements are added together.                     ;
% -------------------------------------------------------------------- ;
if null zz or xind(car zz)<xind(z)
then z.zz
else
  if xind(car zz)=xind(z)
  then <<setival(car zz,dm!-plus(ival(car zz),ival(z)));
         if zeropp(ival car zz)
            then cdr(zz)
            else zz>>
  else car(zz).inszzz(z,cdr zz);

symbolic procedure inszzzn(z,zz);
% -------------------------------------------------------------------- ;
% eff : Similar to InsZZZ.However,Z is only inserted if its index is   ;
%       not occuring as car-part of one of the elements of ZZ.         ;
% -------------------------------------------------------------------- ;
if null(zz) or xind(car zz)<xind(z)
then z.zz
else
  if xind(car zz)=xind(z)
  then zz
  else car(zz).inszzzn(z,cdr zz);

symbolic procedure inszzzr(z,zz);
% -------------------------------------------------------------------- ;
% eff :  Similar to InsZZZ,but the indices of ZZ are now given in as-  ;
%        cending order.                                                ;
% -------------------------------------------------------------------- ;
if null(zz) or xind(car zz)>xind(z)
then z.zz
else
  if xind(car zz)=xind(z)
  then <<setival(car zz,dm!-plus(ival(car zz),ival(z)));
         % We have to test whether the result of dm!-plus was zero.
         % Storing a zero leads to errors.  Hvh 06-04-95.
         if zeropp(ival car zz)
            then cdr(zz)
            else zz>>
  else car(zz).inszzzr(z,cdr zz);

symbolic procedure pnthxzz(x,zz);
% -------------------------------------------------------------------- ;
% arg : X is a row(column)index and ZZ a Z-street.                     ;
% res : A sublist of ZZ such that Caar ZZ = X.                         ;
% -------------------------------------------------------------------- ;
if null(zz) or xind(car zz)=x
then zz
else pnthxzz(x,cdr zz);

symbolic procedure inshisto(x);
% -------------------------------------------------------------------- ;
% arg : Rowindex X.                                                    ;
% eff : X is inserted in the Histogram-hierarchy.                      ;
%                                                                      ;
% The insertion can be vizualized in the following way :               ;
%                                                                      ;
%    CODHISTO                 CODMAT                                   ;
%                                                                      ;
% index  value      Row Hwght      HiR                                 ;
%  200   +---+      index     (PHiR . NHiR)                            ;
%        |   |      .   .   .                                          ;
%        :   :      :   :   :                                          ;
%        |   |      :   :   :                                          ;
%        +---+      |   |   |                                          ;
%   i    | k | <--> +---+---+---------------+                          ;
%        +---+      | k | i |   Nil .  m    |                          ;
%        |   |      +---+---+---------------+                          ;
%        :   :      |   |   |               |                          ;
%        |   |      :   :   :               :                          ;
%        +---+      |   |   |               |                          ;
%   0    |   |      +---+---+---------------+                          ;
%        +---+      | m | i |     k .  p    |                          ;
%                   +---+---+---------------+                          ;
%                   |   |   |               |                          ;
%                   :   :   :               :                          ;
%                   |   |   |               |                          ;
%                   +---+---+---------------+                          ;
%                   | p | i |     m . Nil   |                          ;
%                   +---+---+---------------+                          ;
%                   :   :   :               :                          ;
%                                                                      ;
% -------------------------------------------------------------------- ;
if free(x) and x>=0
then
begin scalar y,hv;
  if y:=histo(hv:=min(hwght x,histolen))
  then setphir(y,x)
  else
    if hv>headhisto
    then headhisto:=hv;
  sethir(x,nil.y);
  sethisto(hv,x)
end;

symbolic procedure delhisto(x);
% -------------------------------------------------------------------- ;
% arg : Rowindex X.                                                    ;
% eff : Removes X from the histogram-hierarchy.                        ;
% -------------------------------------------------------------------- ;
if free(x) and x>=0
then
begin scalar y,z,hv;
  y:=phir x;
  z:=nhir x;
  hv:=min(hwght(x),histolen);
  if y then setnhir(y,z) else sethisto(hv,z);
  if z then setphir(z,y);
end;

symbolic procedure rowdel x;
% -------------------------------------------------------------------- ;
% arg : Row(column)index X.                                            ;
% eff : Row X is deleted from CODMAT. SetOccup ensures that row X is   ;
%       disregarded until further notice. Although the Zstrt remains,  ;
%       the weights of the corresponding columns are reset like the    ;
%       Histogram info.                                                ;
% -------------------------------------------------------------------- ;
<<delhisto(x);
  setoccup(x);
  foreach z in zstrt(x) do
  downwght(yind z,ival z)>>;

symbolic procedure rowins x;
% -------------------------------------------------------------------- ;
% arg : Row(column)index X.                                            ;
% eff : Reverse of the Rowdel operations.                              ;
% -------------------------------------------------------------------- ;
<<setfree(x);
  inshisto(x);
  foreach z in zstrt(x) do
  upwght(yind z,ival z)>>;

symbolic procedure downwght(x,iv);
% -------------------------------------------------------------------- ;
% arg : Row(column)index X. Value IV.                                  ;
% eff : The weight of row X is adapted because an element with value IV;
%       has been deleted.                                              ;
% -------------------------------------------------------------------- ;
<<delhisto(x);
  downwght1(x,iv);
  inshisto(x)>>;

symbolic procedure downwght1(x,iv);
% -------------------------------------------------------------------- ;
%  eff : Weight values reset in accordance with defining rules given in;
%        COSYMP.RED and further argumented in CODOPT.RED.              ;
% -------------------------------------------------------------------- ;
if not(!:onep dm!-abs(iv))
then setwght(x,((awght(x)-1).(mwght(x)-1)).(hwght(x)-4))
else setwght(x,((awght(x)-1).mwght(x)).(hwght(x)-1));

symbolic procedure upwght(x,iv);
% -------------------------------------------------------------------- ;
% arg : Row(column)index X. value IV.                                  ;
% eff : The weight of row X is adapted because an element with value IV;
%       is brought into the matrix.                                    ;
% -------------------------------------------------------------------- ;
<<delhisto(x);
  upwght1(x,iv);
  inshisto(x)>>;

symbolic procedure upwght1(x,iv);
% -------------------------------------------------------------------- ;
%  eff : Functioning similar to Downwght1.                             ;
% -------------------------------------------------------------------- ;
if not(!:onep dm!-abs(iv))
then setwght(x,((awght(x)+1).(mwght(x)+1)).min(hwght(x)+4,histolen))
else setwght(x,((awght(x)+1).mwght(x)).min(hwght(x)+1,histolen));

symbolic procedure initwght(x);
% -------------------------------------------------------------------- ;
% arg : Row(column)index X.                                            ;
% eff : The weight of row(column) X is initialized.                    ;
% -------------------------------------------------------------------- ;
begin scalar an,mn;
  an:=mn:=0;
  foreach z in zstrt(x) do
  if free(xind z)
  then
  << if not(!:onep dm!-abs(ival z)) then mn:=mn+1;
     an:=an+1>>;
  setwght(x,(an.mn).(an+3*mn));
end;

symbolic procedure remzzzz(zz1,zz2);
% -------------------------------------------------------------------- ;
% arg : Zstrt ZZ1 and ZZ2, where ZZ1 is a part of ZZ2.                 ;
% res : All elements of ZZ2, without the elements of ZZ2.              ;
% -------------------------------------------------------------------- ;
if null(zz1)
then zz2
else
  if yind(car zz1)=yind(car zz2)
  then remzzzz(cdr zz1,cdr zz2)
  else car(zz2).remzzzz(zz1,cdr zz2);

symbolic procedure chdel(fa,x);
% -------------------------------------------------------------------- ;
% arg : Father Fa of child X.                                          ;
% eff : Child X is removed from the Chrow of Fa.                       ;
% -------------------------------------------------------------------- ;
setchrow(fa,delete(x,chrow fa));

symbolic procedure delyzz(y,zz);
% -------------------------------------------------------------------- ;
% arg : Column(row)index Y. Zstrt ZZ.                                  ;
% res : Zstrt without the element corresponding with Y.                ;
% -------------------------------------------------------------------- ;
if y=yind(car zz)
then cdr(zz)
else car(zz).delyzz(y,cdr zz);

symbolic procedure clearrow(x);
% -------------------------------------------------------------------- ;
% arg : Rowindex X.                                                    ;
% eff : Row X is cleared. This can be recognized since the father is   ;
%       set to -1.                                                     ;
% -------------------------------------------------------------------- ;
<<setzstrt(x,nil);
  if x>=0
  then
  <<setchrow(x,nil);
    if not numberp(farvar x)
    then remprop(farvar x,'rowindex)
  >>;
  setwght(x,nil);
  setfarvar(x,-1)
>>;

% -------------------------------------------------------------------- ;
% PART 2 : PROCEDURES FOR THE CONSTRUCTION OF THE MATRIX CODMAT,i.e.   ;
%          FOR INPUT PARSING                                           ;
% -------------------------------------------------------------------- ;
%                                                                      ;
% ------ GENERAL STRATEGY ------                                       ;
% REDUCE assignment statements of the form "Nex:=Expression" are trans-;
% formed into pairs (Nex,Ex(= prefixform of the Expression)), using    ;
% GENTRAN-facilities.The assignment operator := defines a literal trans;
% lation of  both Nex and Ex. Replacing this operator by :=: results in;
% translation of the simplified form of Ex. When taking ::=: or ::= the;
% Nex is evaluated before translation, i.e. the subscripts occurring in;
% Nex are evaluated before the translation is performed.               ;
% Once input reading is completed(i.e. when calling CALC) the data-    ;
% structures can and have to be completed (column info and the like)   ;
% using SSETVARS (called in OPTIMIZE (see CODCTL.RED)) before the CSE- ;
% search actually starts.                                              ;
%                                                                      ;
% ------ PRESUMED EXPRESSION STRUCTURE ------                          ;
% Each expression is considered to be an (exponentiated) sum,a product ;
% or something else and to consist of an (eventually empty) primitive  ;
% part and an (also eventually empty) composite part. The primitive    ;
% part of a sum is a linear combination of atoms(variables) and its    ;
% composite part consists of terms which are products or functions. The;
% primitive part of a product is a monomial in atoms and its composite ;
% part is formed by factors which are again expressions(Think of OFF   ;
% EXP).Primitive parts are stored in Zstrts as lists of pairs (RCindex.;
% COFEXP). Composite parts are stored in and via Chrows.               ;
% The RCindex denotes a Row(Column)index in CODMAT if the Zstrt defines;
% a column(row). Rows describe primitive parts. Due to the assumption  ;
% that the commutative law holds column information is not completely  ;
% available as long as input processing is not finished.               ;
% Conclusion : Zstrts cannot be completed (by SSETVARS in CALC or in   ;
% HUGE (see CODCTL.RED)) before input processing is completed,i.e.tools;
% to temporarily store Zstrt info are required. They consist of certain;
% lists,which are built up during parsing, being :                     ;
% The identifiers Varlst!+, Varlst!*  and Kvarlst play a double role.  ;
% They are used as indicators in certain propertylists and also as glo-;
% bal variables carrying information during parsing and optimization.  ;
% To distinguish between these two roles we quote the indicator name   ;
% in the comment given below.                                          ;
% -- Varlst!+  : A list of atoms occuring in primitive sum parts of the;
%                input expressions,i.e. variables used to construct the;
%                sum part of CODMAT.                                   ;
% -- 'Varlst!+ : The value of this indicator,associated with each atom ;
%                of Varlst!+, is a list of dotted pairs (X,IV),where X ;
%                is a rowindex and IV a coefficient,i.e.IV*atom occurs ;
%                as term of a primitive part of some input expression  ;
%                defined by row X.                                     ;
% -- Varlst!*  : Similar to Varlst!+ when replacing the word sum by mo-;
%                nomial and the word coefficient by exponent.          ;
% -- 'Varlst!* : The value of this indicator,occuring on the property  ;
%                list of each element of Varlst!*, is  a list of dotted;
%                pairs of the form (X.IV),where X is a rowindex and IV ;
%                an exponent,i.e. atom^IV occurs as factor in a mono-  ;
%                mial,being a primitive (sub)product,defined through   ;
%                row X.                                                ;
% Remark : Observe that it is possible that an atom possesses both     ;
% 'Varlst!+ and 'Varlst!*,i.e. plays a role in the + - and in the * -  ;
% part of CODMAT.                                                      ;
% -- Kvarlst   : A list of dotted pairs (var.F),where var is an identi-;
%                fier (system selected via FNEWSYM,if necessary) and   ;
%                where F is a list of the form (Functionname . (First  ;
%                argument ... Last argument)). The arguments are either;
%                atoms or composite,and in the latter case replaced by ;
%                a system selected identifier. This identifier is asso-;
%                ciated with the CODMAT-row which is used to define the;
%                composite argument.                                   ;
%                Remark : Kvarlst is also used in CODPRI.RED to guaran-;
%                tee the F's to be printed in due time,i.e.directly    ;
%                after all its composite arguments.                    ;
% -- 'Kvarlst  : This indicator is associated with each operator name  ;
%                during input processing. Its value consists of a list ;
%                of pairs os the form (F.var). To avoid needless name- ;
%                selections this list if values is consulted whenever  ;
%                necessary to see of an expression of the form F is    ;
%                already associated with a system selected identifier. ;
%                As soon as input processing is completed the 'Kvarlst ;
%                values are removed.                                   ;
% -- Prevlst   : This list is also constructed during input processing.;
%                It is a list of dotted pairs (Father.Child),where     ;
%                Child is like Father a rowindex or a system selected  ;
%                identifier name. Prevlst is employed,using SETPREV,to ;
%                store in the ORDR-field of CODMAT-rows relevant info  ;
%                about the structure of the input expressions. During  ;
%                the iterative CSE-search the ORDR-info is updated when;
%                ever necessary.                                       ;
% -- CodBexpl!*: A list consisting of CODMAT-row indices associated    ;
%                with input expression toplevel(i.e. the FarVar-field  ;
%                contains the expression name).                        ;
%                This list is used on output to obtain a correct input ;
%                reflection (see procedures MAKEPREFIXL and PRIRESULT  ;
%                in CODCTL.RED).                                       ;
%                                                                      ;
% ------ PARSING PATHS and PROCEDURE CLASSIFICATION ------             ;
% A prefix-form parsing is performed via FFVAR!!,FFVAR!* and FFVAR!+.  ;
% During parsing,entered via FFVAR!!, the procedure FVAROP is used to  ;
% analyse and transform functions( Operators in the REDUCE terminology);
% and thus also to construct Kvarlst and Prevlst. FVAROP is indirectly ;
% activated through the routines PVARLST!* and PVARLST!+, which assist ;
% in preparing (')Varlst!* and (')Varlst!+,respectively.               ;
% FCOFTRM ,assisting in detecting prim.parts, is used in FFVAR!!2.     ;
% PPRINTF is used (in FFVAR!!) to obtain an input echo on the terminal ;
% (when ON ACINFO, the default setting, holds).                        ;
% RESTORECSEINFO serves to restore the CSE-info when combining the re- ;
% sult of a previous session with the present one( see also CODCTL.RED);
% SSETVARS,and thus SSETVARS1, serves to complete CODMAT once input    ;
% processing is finished. PREPMULTMAT is used to preprocess *-columns  ;
% if one of the exponents, occuring in it, is rational, i.e. when the  ;
% with this column corresponding indentifier has the flag Ratexp.      ;
% SETPREV is used for maintaining consistency in input expression orde-;
% ring and thus for consequent information retrieval at a later stage, ;
% such as during printing.                                             ;
% -------------------------------------------------------------------- ;
global '(varlst!+ varlst!* kvarlst prevlst codbexl!* )$
fluid '(preprefixlist prefixlist);

varlst!+:=varlst!*:=kvarlst:=nil;

% -------------------------------------------------------------------- ;
% ------ THE PREFIX FORM PARSING  ------                               ;
% FFvar!! is the main procedure activating parsing. Besides some house-;
% keeping,information is send to either FFvar!* (either a product (but ;
% not a prim. term) or a 'EXPT-application) or FFvar!+(a  sum or a     ;
% function application).                                               ;
% The parsing is based on the following Prefix-Form syntax:            ;
% -------------------------------------------------------------------- ;
% This syntax needs some revision!!!                                   ;
% -------------------------------------------------------------------- ;
% <expression>           ::= <sumform>|<productform>                   ;
% <sumform>              ::= <sum>|('EXPT <sum> <exponent>)            ;
% <productform>          ::= <product>|                                ;
%                            ('TIMES <constant> <factor>)|             ;
%                            ('TIMES <constant> <list of factors>)|    ;
%                            ('MINUS <productform>)                    ;
% <sum>                  ::= <term>|('PLUS.<list of terms>)            ;
% <list of terms>        ::= (<term> <term>)|(<term> <list of terms>)  ;
% <term>                 ::= <primitive term>|<productform>|<sumform>  ;
% <primitive term>       ::= <constant>|<variable>|                    ;
%                            ('TIMES <constant> <variable>)|           ;
%                            <function application>                    ;
% <product>              ::= <factor>|('TIMES.<list of factors>)       ;
% <list of factors>      ::= (<factor> <factor>)|(<factor> <list of    ;
%                                                             factors>);
% <factor>               ::= <primitive factor>|<sumform>|<productform>;
% <primitive factor>     ::= <variable>|('EXPT <variable> <exponent>)| ;
%                            <function application>                    ;
% <function application> ::= <function symbol>.<list of expressions>   ;
% <function symbol>      ::= identifier, where identifier is not       ;
%                            in {'PLUS,'TIMES,'EXPT,'MINUS,'DIFFERENCE,;
%                                'SQRT,dmode!*}.                       ;
%                            Obvious elements are sin,cos,tan,etc.     ;
%                            The function applications are further     ;
%                            analyzed in FvarOp.                       ;
% <list of expressions>  ::= (<expression>)|<expression>.<list of      ;
%                                                          expressions>;
% <variable>             ::= element of the set of variable names,     ;
%                            either delivered as input or produced by  ;
%                            the Optimizer when the need to introduce  :
%                            cse-names exists. This is done with the   ;
%                            procedure FNewSym(see CODCTL.RED) which is;
%                            initiated either using the result of the  ;
%                            procedure INAME(see CODCTL.RED) or simply ;
%                            by using GENSYM().                        ;
% <constant>             ::= element of the set of integers            ;
%                            representable by REDUCE | domain element  ;
% <exponent>             ::= element of the set of integer an rational ;
%                            numbers representable by REDUCE.          ;
% -------------------------------------------------------------------- ;

symbolic procedure ffvar!!(nex,ex,prefixlist);
% -------------------------------------------------------------------- ;
% arg : An expression Ex in Prefix-Form, and its associated name NEx.  ;
% eff : The expression Ex is added to the incidence matrix CODMAT.     ;
%       Parsing is based on the above given syntax.                    ;
% -------------------------------------------------------------------- ;
begin scalar n, nnex, argtype, var, s;
 prefixlist:=cons(nex,ex).prefixlist;
% if nex memq '(cses gsym)     % deleted : binf no more used. JB 13/4/94
% then restorecseinfo(nex,ex)
 n:=rowmax:=rowmax+1;
 codbexl!*:=n.codbexl!*;
 if flagp(nex,'newsym)
    then put(nex,'rowindex,n);
 put(nex,'rowocc, list n);
 ffvar!!2(n,nex,remdiff ex);
 return prefixlist
end;


symbolic procedure restorecseinfo(nex,ex);
% -------------------------------------------------------------------- ;
% arg : Nex is an element of the set {CSES,GSYM,BINF} and Ex a corres- ;
%       pondig information carrier.                                    ;
% eff : RestoreCseInfo is called in FFvar!! when during input parsing  ;
%       name Nex belongs to the above given set. In this case the input;
%       is coming from a file which is prepared during a previous run. ;
%       It contains all output from this previous run, preceded by     ;
%       system prepared cse-info stored as value of the 4 system       ;
%       variables CSES,GSYM and BINF (see the function SaveCseInfo  in ;
%       CODCTL.RED for further information).                           ;
% -------------------------------------------------------------------- ;
begin scalar inb,nb,s;
if nex eq 'cses
 then (if atom(ex) then flag(list ex,'newsym)
                   else foreach el in cdr(ex) do flag(list el,'newsym))
 % Ammendments to increase robustness:
 % More strict control over what cse-name is going to be used,
 % starting from which index.
 % This prevents scope from generating a cse twice, thus overwriting
 % earlier occurrences and introducing strange erronous output.
 %                                                           JB 13/4/94
 else if eq(letterpart(ex),'g)
         then if eq((s:=letterpart fnewsym()),'g)
                 then iname s
                 else<< nb:=digitpart(ex);
                       inb:=digitpart(fnewsym());
                       for j:=inb:nb do gensym() >>
         else if eq(letterpart(ex), letterpart(s:= fnewsym())) and
                 digitpart(ex) > digitpart(s)
                 then iname ex
                 else iname s
end;

symbolic procedure remdiff f;
% -------------------------------------------------------------------- ;
% Replace all occurrences of (DIFFERENCE A B) in F for arbitrary A and ;
% B by (PLUS A (MINUS B)).                                             ;
% -------------------------------------------------------------------- ;
if idp(f) or constp(f) then f
 else
 << if car(f) eq 'difference
   then f:=list('plus,remdiff cadr f,list('minus,remdiff caddr f))
   else car(f) . (foreach op in cdr(f) collect remdiff(op))
 >>;

symbolic procedure ffvar!!2(n, nex, ex);
% -------------------------------------------------------------------- ;
% Serviceroutine used in FFvar!!.                                      ;
% -------------------------------------------------------------------- ;
  if eqcar(ex, 'times) and not fcoftrm ex
   then setrow(n, 'times, nex, ffvar!*(cdr ex, n), nil)
   else
    if eqcar(ex, 'expt) and (integerp(caddr ex) or rationalexponent(ex))
     then setrow(n, 'times, nex, ffvar!*(list ex, n), nil)
     else setrow(n, 'plus, nex, ffvar!+(list ex, n), nil);

symbolic procedure fcoftrm f;
% -------------------------------------------------------------------- ;
% arg : A prefix form F.                                               ;
% res : T if F is a (simple) term with an integer coefficient, NIL     ;
%       otherwise.                                                     ;
% -------------------------------------------------------------------- ;
(null(cdddr f) and cddr f) and
(constp(cadr f) and not (pairp(caddr f) and
      caaddr(f) memq '(expt times plus difference minus)));

symbolic procedure rationalexponent(f);
% -------------------------------------------------------------------- ;
% arg : F is an atom or a  prefixform.                                 ;
% res : T if F is an 'EXPT with a rational exponent.                   ;
% -------------------------------------------------------------------- ;
   rationalp caddr f;
%(pairp caddr f) and (caaddr f eq 'quotient) and (integerp(cadr caddr f)
%                                          and integerp(caddr caddr f));

symbolic procedure rationalp f;
   eqcar(f,'quotient) and integerp(cadr f) and integerp(caddr f);

symbolic procedure ffvar!+(f,ri);
% -------------------------------------------------------------------- ;
% arg : F is a list of terms,i.e. th sum SF='PLUS.F is parsed. Info    ;
%       storage starts in row RI resulting in                          ;
% res : a list (CH) formed by all the indices of rows where the descrip;
%       tion of children(composite terms) starts. As a by product(via  ;
% eff : PVARLST!+) the required Zstrt info is made.                    ;
% N.B.: Possible forms for the terms of SF( the elements of F) are:    ;
%       -a sum     - which is recursively managed after minus-symbol   ;
%                    distribution.                                     ;
%       -a product - of the form constant*atom : which is as term of a ;
%                    prim. sum treated by PVARLST!+.                   ;
%                    of another form : which is managed via FFVAR!*.   ;
%       -a constant                                                    ;
%            power - of a product of atoms : is transformed into a prim;
%                    product and then treated as such.                 ;
%                    of something else : is always parsed via FFVAR!*. ;
%       -a function- application is managed via PVARLST!+,i.e. via     ;
%                    FVAROP with additional Varlst!+ storage of system ;
%                    selected subexpression names.                     ;
% -------------------------------------------------------------------- ;
begin scalar ch,n,s,b,s1,nn;
  foreach trm in f do
  <<b:=s:=nil;
    while pairp(trm) and (s:=car trm) eq 'minus do
    <<trm:=cadr trm;
      b:=not b>>;
    if s eq 'difference
     then
     <<trm:=list('plus,cadr trm,list('minus,caddr trm));
       s:='plus>>;
    if s eq 'plus
     then
     <<s1:=ffvar!+(if b
                   then foreach el in cdr(trm) collect list('minus,el)
                   else cdr trm,ri);
       ch:=append(ch,car s1)>>
     else
      if s eq 'times
     then
      <<% ------------------------------------------------------------ ;
        % Trm is a <productform>, which might have the form            ;
        % ('TIMES <constant> <function application>). Here the         ;
        % <function application> can be ('SQRT <expression>) , i.e. has;
        %  to be changed into :                                        ;
        % ('TIMES <constant> ('EXPT <expression> ('QUOTIENT 1 2)))     ;
        % ------------------------------------------------------------ ;
        if pairp caddr trm and caaddr trm eq 'sqrt and null cdddr trm
        then
          trm := list('times,cadr trm,list('expt,cadr caddr trm,
                                                  list('quotient,1,2)));
        if fcoftrm trm
          % ---------------------------------------------------------- ;
          % Trm is ('TIMES <constant> <variable>)                      ;
          % ---------------------------------------------------------- ;
         then pvarlst!+(caddr trm,ri,if b then dm!-minus(cadr trm)
                                          else cadr trm)
         else
          % ---------------------------------------------------------- ;
          % Trm is a <productform>                                     ;
          % ---------------------------------------------------------- ;
         <<n:=rowmax:=rowmax+1;
           s1:=ffvar!*(cdr trm,n);
           if b
            then setrow(n,'times,ri,list(car s1,dm!-minus cadr s1),nil)
            else setrow(n,'times,ri,s1,nil);
           ch:=n.ch>>
        >>
        else
        <<if s eq 'sqrt
          then
          % ---------------------------------------------------------- ;
          % Trm is a <primitive term> which is a <function application>;
          % which is ('SQRT <expression>) which is of course           ;
          % ('EXPT <expression> <exponent>)                            ;
          % ---------------------------------------------------------- ;
          <<trm := cons('expt,cons(cadr trm,list list('quotient,1,2)));
            s := 'expt
          >>;
          if s eq 'expt and eqcar(caddr trm,'minus) and
             (integerp(cadr caddr trm) or rationalp(cadr caddr trm))
            then
          << trm:=list('quotient,1,list('expt,cadr trm,cadr caddr trm));
             s:='quotient
          >>;
          if s eq 'expt and
                          (integerp(caddr trm) or rationalexponent(trm))
          then
          <<n:=rowmax:=rowmax+1;
            s1:=ffvar!*(list trm,n);
            if b
             then setrow(n,'times,ri,list(car s1,-1),nil)
             else setrow(n,'times,ri,s1,nil);
            ch:=n.ch
          >>
          else pvarlst!+(trm,ri,if b then -1 else 1)
        >>;
  >>;
  return list(ch)
end;

symbolic procedure pvarlst!+(var,x,iv);
% -------------------------------------------------------------------- ;
% arg : Var is one of the first 2 alternatives for a kernel,i.e. a vari;
%       able or an operator with a simplified list of arguments (like  ;
%       sin(x)) with a coefficient IV,belonging to a Zstrt which will  ;
%       be stored in row X.                                            ;
% eff : If the variable happens to be a constant a special internal var;
%       !+ONE is introduced to assist in defining the constant contribu;
%       tions to primitive sumparts in accordance with the chosen data-;
%       structures.                                                    ;
%       When Var is an operator(etc.) Fvarop is used for a further ana-;
%       lysis and a system selected name for var is returned. Then this;
%       name,!+ONE or the variable name Var are used to eventually     ;
%       extend Varlst!+ with a new name.The pair (rowindex.coeff.value);
%       is stored on the property list of this var as pair of the list ;
%       'Varlst!+,which is used in SSETVARS1 to built the Zstrts associ;
%       ated with this variable.                                       ;
% -------------------------------------------------------------------- ;
begin scalar l,s,nvar;
  if constp var then <<iv:=dm!-times(iv,var); var:='!+one>>;
  if not (idp(var) or constp(var)) then var:=fvarop(var,x);
  if null(s:=get(var,'varlst!+)) then varlst!+:=var.varlst!+;
  put(var,'varlst!+,(x.iv).s)
end;

symbolic procedure ffvar!*(f,ri);
% -------------------------------------------------------------------- ;
% arg : F is a list of factors,i.e. the product PF='TIMES.F is parsed. ;
%       Info storage starts in row RI,resulting in                     ;
% res : a list (CH COF),where CH is a list of all the indices of rows  ;
%       where the description of children of PF(composite factors)     ;
% eff : starts. As a by product(via the procedure PVARLST!*) Zstrt info;
%       is made.                                                       ;
% N.B.: Possible forms for the factors of PF( the elements of F) are:  ;
%       -a constant- contributing as factor to COF.                    ;
%       -a variable- contributing as factor to a prim.product,stored in;
%                    a Zstrt(via SSETVARS) after initial management via;
%                    PVARLST!* and storage in Varlst!* and 'Varlst!*'s.;
%       -a product - Recursively managed via FFVAR!*,implying that CH:=;
%                    Append(CH,latest version created via FFVAR!* and  ;
%                    denoted by Car S).                                ;
%       -a sum     - (or difference or negation) contributing as comp. ;
%                    factor and demanding a subexpression row  N to    ;
%                    start its description. Storage management is done ;
%                    via FFVAR!+,implying that CH:=N.CH.               ;
%       -a power   - of the form sum^integer : and managed like a sum. ;
%                    of the form atom^integer: and managed like single ;
%                    atom as part of a prim. product.                  ;
%       -a function- application,which is managed via PVARLST!*,i.e.via;
%                    FVAROP with additional Varlst!* storage of system ;
%                    selected subexpression names.                     ;
% -------------------------------------------------------------------- ;
begin scalar cof,ch,n,s,b,rownr,pr,nr,dm;
  cof:=1;
  foreach fac in f do
   if constp fac
    then cof:=dm!-times(fac,cof)
    else
     if atom fac
      then pvarlst!*(fac,ri,1)
      else
       if (s:=car fac) eq 'times
        then
         <<s:=ffvar!*(cdr fac,ri);
           ch:=append(ch,car s);
           cof:=dm!-times(cof,cadr(s))
         >>
        else
         if s memq '(plus difference minus)
          then
           << if s eq 'minus and constp(cadr fac) and null cddr fac
               then cof:=dm!-minus dm!-times(cof,cadr(fac))
               else <<n:=rowmax:=rowmax+1;
                      if (not b) then <<b:=t; rownr:=n>>;
                      setrow(n,'plus,ri,ffvar!+(list fac,n),nil);
                      ch:=n.ch
                    >>
           >>
          else
          <<if s eq 'sqrt
            then
            % -------------------------------------------------------- ;
            % The primitive factor is a <function application>. In this;
            % case a ('SQRT <expression>) which is of course           ;
            % ('EXPT <expression> ('QUOTIENT 1 2)).                    ;
            % -------------------------------------------------------- ;
            <<fac:=cons('expt,cons(cadr fac,list list('quotient,1,2)));
              s:='expt
            >>;
            if s eq 'expt and eqcar(caddr fac,'minus) and
               (integerp(cadr caddr fac) or rationalp(cadr caddr fac))
              then
               <<fac:=list('quotient,1,
                                   list('expt,cadr fac,cadr caddr fac));
                 s:='quotient
               >>;
            if s eq 'expt and
               (integerp(caddr fac) or (nr:=rationalexponent(fac)))
            then % --------------------------------------------------- ;
                 % Fac = (EXPT <expression or variable>                ;
                 %                       <integer or rational number>) ;
                 % --------------------------------------------------- ;
             (if pairp(cadr fac) and caadr(fac) eq 'sqrt
               then
                << if nr then <<nr:=cadr caddr fac;
                                dm:=2*(caddr caddr fac)>>
                         else <<nr:=1; dm:=2>>;
                   pvarlst!*(cadr cadr fac,ri,cons(nr,dm))
                >>
               else
                 pvarlst!*(cadr fac,ri,
                           if integerp(caddr fac)
                            then caddr fac
                            else (cadr caddr fac . caddr caddr fac)))
            else pvarlst!*(fac,ri,1)
          >>;
  if b and not(!:onep dm!-abs(cof))
   then
    % ---------------------------------------------------------------- ;
    % The product Cof*....*(c1*a+....+cn*z) is replaced by             ;
    % the product ....*({Cof*c1}*a+...+{Cof*cn}*z), assuming Cof, c1,..;
    % ..,cn are numerical constants.                                   ;
    % ---------------------------------------------------------------- ;
    << foreach el in chrow(rownr) do
           setexpcof(el,dm!-times(cof,expcof(el)));
       foreach var in varlst!+ do
                         if (pr:=assoc(rownr,get(var,'varlst!+)))
                          then rplacd(pr,dm!-times(cdr(pr),cof));
       cof:=1;
    >>;
  return list(ch,cof)
end;

symbolic procedure pvarlst!*(var,x,iv);
% -------------------------------------------------------------------- ;
%  eff : Similar to Pvarlst!+.                                         ;
%      : The flag Ratexp is associated with Var if one of its exponents;
%        is rational. This flag is used in the function PrepMultMat.   ;
% -------------------------------------------------------------------- ;
begin scalar l,s,bvar,bval;
 if constp(var)
  then
   << var:=fvarop(if iv='(1 . 2)
                   then list('sqrt,var)
                   else list('expt,var,
                              if pairp iv
                               then list('quotient,car iv,cdr iv)
                               else iv),x);
      iv:=1
   >>;
  if not(atom(var) or constp(var))
   then << s:=get('!*bases!*,'kvarlst);
           if s then bvar:=assoc(bval:=reval var,s);
           if bvar then var:=cdr bvar
                   else <<  var:=fvarop(var,x);
                            put('!*bases!*,'kvarlst,(bval.var).s)
                        >>
        >>;
 if null(s:=get(var,'varlst!*)) then varlst!*:=var.varlst!*;
 if pairp(iv) and not(constp iv) then flag(list(var),'ratexp);
 put(var,'varlst!*,(x.iv).s)
end;

symbolic procedure fvarop(f,x);
% ------------------------------------------------------------------- ;
% arg : F is a prefixform, being <operator>.<list of arguments>. X is ;
%       the index of the CODMAT row where the description of F has to ;
%       start.                                                        ;
% ------------------------------------------------------------------- ;
begin scalar svp,varf,valf,n,fargl,s,b;
 if eqcar(f,'sqrt) and not(constp(cadr f))
  then f:=list('expt,cadr f,list('quotient,1,2));
 b:=(not (car f memq '(plus minus times expt)))
      or
    (car(f) eq 'expt
       and
    (not (numberp(caddr f) or rationalexponent(f))
       or
    ((cadr(f) eq 'e) or constp(cadr(f)))));
 svp:=subscriptedvarp car f;
 s:=get(car f, 'kvarlst);
 %------------------------------------------------------------
 % b tells us whether f is a regular function (NIL) or
 % not (T). So b=T for everything but ye ordinary expressions.
 % We've got to check whether we deal with an array variable
 % and if so, whether there is a valid cse-name for this
 % variable.
 % We also want to recognize a valid index-expression, for
 % wich `not b' holds.
 %------------------------------------------------------------
 varf := if svp then assoc(ireval(f),s)
                else assoc(f,s);
 if (varf and svp) or
    (b and varf and allconst(cdr f, cdr varf))
    %---------------------------------------------------------
    % This condition states that in order to allow the current
    % and a previous expression to be regarded as equal, the
    % expression should denote a subscripted variable, or a
    % use of an function with constant parameters, i.e.
    % numerical parameters.
    %---------------------------------------------------------
  then varf:=cdr varf
  else
  << varf:=fnewsym();
     put(car f,'kvarlst,((if svp then ireval f else f).varf).s);
     if not b
      then
       << put(varf,'rowindex,n:=rowmax:=rowmax+1);
          if not(eqcar(f,'expt) and
                 rationalexponent(f) or flagp(cadr f,'ratexp))
           then prevlst:=(x.n).prevlst;
          ffvar!!2(n,varf,f)
       >>
      else
        << if not (!*vectorc and svp)
            then << foreach arg in cdr(f) do
                    if not(constp(arg) or atom(arg))
                       then fargl:=fvarop(if svp then reval arg
                                                 else arg,x).fargl
                       else fargl:=arg.fargl;
                    f:=car(f).reverse(fargl);
                 >>;
           kvarlst:=(varf.f).kvarlst
        >>
  >>;
 prevlst:=(x.varf).prevlst;
 return varf
end;

symbolic procedure allconst (l,f);
not (nil member foreach el in l collect jbconstp (el,f));

symbolic procedure jbconstp (item,ref);
if constp item
   then % some numerical value
        T
   else if atom item
           then % some id
                if get(item,'rowocc)
                   then % item parsed as lefthandside.
                        if (car(get(item,'rowocc))< findvardef(ref))
                            then % This use and the previous are in the
                                 % scope of one definition of item.
                                 T
                            else % This use and the previous are in
                                 % scopes of diferent definitions of
                                 % item.
                                 NIL

                   else % some input id used twice ore more on rhs.
                        T
           else not(NIL member foreach el in cdr item
                                  collect jbconstp(el,ref));

symbolic procedure findvardef v;
begin
  scalar r,vp,vt;
  r:=get(v,'rowocc);
  vt:=get(v,'varlst!*);
  vp:=get(v,'varlst!+);
  if r
    then r:= car r
    else if vt
           then if vp
                  then
                  if ((vt := caar reverse vt) > (vp := caar reverse vp))
                         then r:= vt
                         else r:= vp
                  else r:= caar reverse vt
           else r:= caar reverse vp;
   return r;
   end;


symbolic procedure ssetvars(preprefixlist);
% -------------------------------------------------------------------- ;
% eff : The information stored on the property lists of the elements of;
%       the lists Varlst!+ and Varlst!* is stored in the matrix CODMAT,;
%       i.e.the Z-streets are produced via the SSetvars1 calls.        ;
%       Before doing so PrepMultMat is used to modify, if necessary,the;
%       Varlst!* information by incorporating information about ratio- ;
%       nal exponents.                                                 ;
%       Furthermore the elements of Prevlst are used to store the hier-;
%       archy information in the ORDR-fields in the matrix CODMAT. In  ;
%       addition some bookkeeping activities are performed: Needless   ;
%       information is removed from property lists and not longer need-;
%       ed lists are cleared. EndMat is also initialized.              ;
% -------------------------------------------------------------------- ;
<<
  preprefixlist:=prepmultmat(preprefixlist);
  %--------------------------------------------------------------------
  % From now on preprefixlist has the following structure :
  %
  %  ((var1 aliases )(var2 aliases )...)
  %
  %--------------------------------------------------------------------
  ssetvars1('varlst!+,'plus);
  ssetvars1('varlst!*,'times);
  varlst!+:=varlst!*:=nil;
  foreach el in reverse(prevlst) do setprev(car el,cdr el);
  foreach el in kvarlst do remprop(cadr el,'kvarlst);
  foreach el in '(plus minus difference times sqrt expt) do
                                                   remprop(el,'kvarlst);
  remprop('!*bases!*,'kvarlst);
  endmat:=rowmax;
  preprefixlist
>>;

symbolic procedure revise2 (f,d);
begin
  scalar res;
  if atom f
    then if constp f
          then return f
          else if get(f,'aliaslist)
                then return get(f,'finalalias)
                else << if not(member(f,known))
                         then known:=f . known;
                        return f;
                     >>
    else if not constp f
          then % car f is operator or indexed var
               if subscriptedvarp car f
                then % We have to search d to rewrite f.
                     % Then we check `known' for an alias.
                     if get(car f,'aliaslist)
                      then <<f:= car f . foreach el in cdr ireval f
                                            collect revise2 (el,d);
                             if (res:=assoc(f,get(car f,'finalalias)))
                              then return cadr res
                              else if !*vectorc
                                    then % rhs-alias introduction.
                                    <<rhsaliases :=
                                                (introduce!-alias f . f)
                                                    .  rhsaliases;
                                      return caar rhsaliases>>
                                    else return f >>
                       else if !*vectorc
                             then % rhs-alias introduction.
                             <<rhsaliases := (introduce!-alias f . f) .
                                             rhsaliases;
                               return caar rhsaliases>>
                             else return f
                else if res:=assoc(f,d)
                      then return cadr res
                      else return car f . foreach el in cdr f
                                             collect revise2 (el,d)
          else return f;
end;

symbolic procedure revise (f,d);
car f . (cadr f . foreach l in cddr f collect revise2 (l,d));

symbolic procedure preremdep forms;
%----------------------------------------------------------------------
% We remove dependencies and indexed variables in forms by introducing
% aliases.
%                      ABOUT ALIASES.
%
% In search for common subexpressions, scope does not, ironically,
% bother for rules of scope. This means that :
%
% a:=x+y
% ..
% a:=cos(x)
% z:=x+y
%
% is going to be optimized into:
%
% a:=x+y,
% ..
% a:=cos(x),
% z:=a.
%
% We solve this anomaly by replacing every occurrence of `a', starting
% from the second definition, by a generated name; so
%
% a := ...
%   := ... a ...
% a := ... a ...
% a := ...
%   := ... a ...
%
% becomes :
%
% a := ...
%   := ... a ...
% a1:= ... a ...
% a2:= ...
%   := ... a2 ...
%
% This prevents scope from finding c.s.e.'s where there aren't any. At
% the end of the optimization process, these aliases are backsubstitu-
% ted, with their original values, (provided these are atomic!)
% Secondly the aliasmechanism is usefull in the storage process:
% When dealing with nonatomic, i.e. subscripted variables, problems
% arise in storing these variables in codmat, and putting all kind of
% info as properties on them.  A variable is subscripted when declared
% as such by the option `declare' or `vectorcode', or when encountered
% as lhs of an assignment.
% We alias subscripted variables by an atomic, generated variable:
%
% a(i) := ...
% ...  := ... a(i) ...
%
% becomes:
%
% g1   := ...
% ...  := ... g1 ...
%
% When the indexexpressions are not atomic, i.e. they could be or con-
% tain c.s.e.'s, we put an assignment right in front of their first
% use (when the switch VECTORC is off!!!):
%
% a(x+y):= ...
% ...   := ... a(x+y) ...
%
% becomes:
%
% g0    := x+y
% g1    := ...
% ...   := ... g1 ...
%
% We only backsubstitute the output-definition of a sub'ted variable,
% i.e. the last definition, thus saving some memorymanagementoverhead.
% Atomic originals are all backsubstituted, for economy in allocation
% of variables.
%
%                 TECHNICAL DETAILS ALIASMECHANISM
%
% Aliasing is performed by a double linking mechanism:
% The original has properties `aliaslist'(a list of all aliases for
% this variable) and `finalalias' (the alias to be used in the current
% or final scope).
%
%    Original ------[finalalias]--------> Aliasxx
%       |     <-----[alias     ]---------/   ^
%       |                                    |
%    [aliaslist]                             |
%       |                                    |
%       *------------------------------------/
%       |
%       *-------------------------------> Aliasyy
%       |                                    .
%       .                                    .
%       |                                    .
%       *-------------------------------> Aliaszz
%
% All aliases of the original are linked to the original by their
% property `alias' with value Original. (This is left out of above pic.
% for Aliasyy .. Aliaszz.)
% Finally, all generated assignments, stemming from indexexpressions,
% have the property `inalias', which links them to the variable they
% arose from. This property can be updated during optimization, or even
% be copied onto other variables, due to finding of c.s.e.'s.
%
%    Generated Assignment:
%            Aliasxx := indexexpression.
%                  |
%                  [  inalias ]
%                             |
%                             V
%    Original: <----[alias]---Aliasyy
%    A(.., Aliasxx, ..)
%
% All variables generated in the aliasing process obtain a flag
% `aliasnewsym'.
% All aliasinfo is removed after the optimization process.
%----------------------------------------------------------------------
begin
  scalar defs,var,alias,res,currall;
  known:=nil;
  foreach f in forms do
    <<if !*inputc then pprintf(caddr f,cadr f);
      if !*complex then f := remcomplex f;
      if not(cadr f member '(cses gsym))
       then
         if car f member '(equal setq)
          then << f:=revise(f,defs);
                  if atom(var:=cadr f)
                   then <<if member(var,known)
                           then % This is a redefinition.
                                % Introduce an alias
                                << alias:=introduce!-alias var;
                                   rplaca(cdr f,alias);
                                   %remflag(list alias,'newsym);
                                >>
                           else  known:= var . known;
                           res:=f . res;
                         >>
                   else if !*vectorc or flagp(car var, 'vectorvar)
                         then % Switch vectorc is set,or this is just
                              % `vectorcode-marked' variable.
                              % No further analization of var needed.
                              % For output purposes we apply remdiff to
                              % the subscripts.
                              % Then just introduce aliases.
                           <<flag(list car var,'subscripted);
                             var :=(car var). foreach idx in cdr var
                                                 collect remdiff idx;
                             alias:=introduce!-alias var;
                             rplaca(cdr f,alias);
                             res:= f . res;
                           >>
                         else % Introduce cse's for the non-atomic
                              % index-expressions,
                              % prepend this to current assignment and
                              % introduce its alias.
                           <<flag(list car var, 'subscripted);
                             var:= car var .
                             foreach ie in cdr var collect
                             if not atom ie
                              then<<if assoc((ie:=ireval ie),defs)
                                     then alias:= cadr assoc(ie,defs)
                                     else
                                       <<alias:=fnewsym();
                                         res:= list('setq,alias,ie)
                                               . res;
                                         defs:=list(ie,alias) . defs;
                                         currall:= alias . currall;
                                         flag(list alias,'aliasnewsym);
                                         %remflag(list alias,'newsym);
                                       >>;
                                     alias
                                   >>
                               else ie;
                              alias:=introduce!-alias ireval var;
                              foreach a in currall
                                 do put(a,'inalias,
                                        alias . get(a,'inalias));
                              rplaca(cdr f,alias);
                              res:= f . res;
                           >>
               >>
          else res:= f . res
       else restorecseinfo(cadr forms, caddr forms)
     >>;
  restoreall;
  return reverse res;
  end;

symbolic procedure introduce!-alias var;
% Introduce an alias for var;
begin
  scalar alias,v2;
  alias:=fnewsym();
  remflag(list alias,'newsym);
  flag(list alias, 'aliasnewsym);
  v2:= if atom var then var else car var;
  put(v2,'aliaslist,
      alias . get(v2,'aliaslist));
  if atom var
     then put(var,'finalalias,alias)
     else %-----------------------------------------------------------
          % An subscripted var can have a finalalias for several
          % entries.
          %-----------------------------------------------------------
         put(v2,'finalalias,
                 list(var,alias)
                 . delete(assoc(var, get(v2,'finalalias)),
                                     get(v2,'finalalias)));
  put(alias,'alias,var);
  known:=alias . known;
  return alias;
  end;

symbolic procedure ssetvars1(varlst,opv);
% -------------------------------------------------------------------- ;
% eff : Zstrt's are completed via a double loop and association of     ;
%       column indices(if necessary for both the + and  the * part of  ;
%       CODMAT) with the var's via storage on the var property lists.  ;
% -------------------------------------------------------------------- ;
begin scalar z,zz,zzel;
  %foreach var in lispeval(varlst) do
  foreach var in eval(varlst) do
  <<zz:=nil;
    rowmin:=rowmin-1;
    foreach el in get(var,varlst) do
    <<z:=mkzel(rowmin,cdr el);
      if null(zzel:=zstrt car el) or not(xind(car zzel)=rowmin)
       % To deal with X*X OR X+X;
      then setzstrt(car el,z.zzel);
      zz:=inszzz(mkzel(car el,val z),zz)
    >>;
    put(var,varlst,rowmin); % Save column index for later use;
    setrow(rowmin,opv,var,nil,zz)
  >>;
end;

symbolic procedure prepmultmat(preprefixlist);
% -------------------------------------------------------------------- ;
% eff : The information concerning rational exponents and stored in the;
%       Varlst!* lists is used to produce exact integer exponents,to be;
%       stored in the Z-streets of the matrix Codmat:                  ;
%       For all elements in Varlst!* the Least Common Multiplier (LCM) ;
%       of their exponent-denominators is computed.                    ;
%       If LCM > 1 the element has a rational exponent. The exponent of;
%       each element is re-calculated  to obtain LCM * the orig. exp.  ;
%       Modulo LCM arithmetic is used to spread information over 2     ;
%       varlst!*'s, one for the original var(iable) and another for the;
%       fraction-part left.                                            ;
%       Renaming is adequately introduced when necessary.              ;
% -------------------------------------------------------------------- ;
begin scalar tlcm,var,varexp,kvl,kfound,pvl,pfound,tel,ratval,ratlst,
                                                      newvarlst,hvarlst;
 hvarlst:= nil;
 while not null (varlst!*) do
 <<var := car varlst!*; varlst!* := cdr varlst!*;
   if flagp(var,'ratexp)
    then
     <<tlcm:=1;
       remflag(list var,'ratexp);
       foreach elem in get(var,'varlst!*) do
        if pairp cdr elem then tlcm := lcm2(tlcm,cddr elem);
       varexp:=fnewsym();
       tel:=(varexp.(if tlcm = 2
                 then list('sqrt,var)
                 else list('expt,var,
                         if onep cdr(tel:=simpquot list(1,tlcm)) then
                            car tel
                         else
                            list('quotient,car tel,cdr tel))));
       if assoc(var,kvarlst)
        then
         <<kvl:=kfound:=nil;
           while kvarlst and not(kfound) do
            if caar(kvarlst) eq var
             then
              << kvl:=tel.kvl; kfound:=t;
                 pvl:=pfound:=nil; prevlst:=reverse(prevlst);
                 while prevlst and not(pfound) do
                  if cdar(prevlst) eq var
                   then << pvl:=cons(caar prevlst,varexp).pvl;
                           pfound:=t
                        >>
                   else << pvl:=car(prevlst).pvl;
                           prevlst:=cdr(prevlst)
                        >>;
                 if pvl then
                  if prevlst then prevlst:=append(reverse prevlst,pvl)
                             else prevlst:=pvl
              >>
             else
              << kvl:=car(kvarlst).kvl; kvarlst:=cdr kvarlst>>;
           if kvl then
             if kvarlst then kvarlst:=append(reverse kvl,kvarlst)
                      else kvarlst:=reverse kvl
         >>
        else preprefixlist:=tel.preprefixlist;
       ratlst:=newvarlst:=nil;
       foreach elem in get(var,'varlst!*) do
        if pairp cdr elem
         then
          << ratval:=divide((tlcm * cadr elem)/(cddr elem),tlcm);
             ratlst:=cons(car elem,cdr ratval).ratlst;
             if car(ratval)>0
              then newvarlst:=cons(car elem,car ratval).newvarlst
          >>
         else newvarlst:=elem.newvarlst;
       if ratlst
        then << put(varexp,'varlst!*,reverse ratlst);
                hvarlst:=varexp.hvarlst
             >>;
       if newvarlst
        then << put(var,'varlst!*,reverse newvarlst);
                hvarlst:=var.hvarlst
             >>
        else remprop(var,'varlst!*)
     >>
    else hvarlst:=var.hvarlst
 >>;
 varlst!* := hvarlst;
 return preprefixlist
end;

symbolic procedure lcm2(a,b);
% ---
% Switch rounded off before calling lcm.
% lcm doesn't seem to work in rounded mode
% for lcm
% ---
begin scalar g, res;
  g := gcd2(a,b);
  res := a*b;
  return res/g;
  end;

% -------------------------------------------------------------------- ;
% ORDERING OF (SUB)EXPRESSIONS :                                       ;
% -------------------------------------------------------------------- ;
% It is based op the presumption that the ordering of the input expres-;
% sions has to remain unchanged when attempting to optimize their des- ;
% cription. This ordering is stored in the list CodBexl!* via FFVAR    ;
% and used in the procedure MAKEPREFIXL( via PRIRESULT and also given  ;
% in CODCTL.RED) for managing output. Hence any subexpression found by ;
% whatever means has to be inserted in the latest version of the       ;
% description of the set ahead of the first expression in which it     ;
% occurs and assuming its occurences are replaced by a system selected ;
% name which is also used as subexpression recognizer(i.e., as assigned;
% var). We distinguish between different types of subexpressions:      ;
% Some are directly recognizable : sin(x),a(1,1) and the like. Others  ;
% need optimizer searches to be found: sin(a+2*b),f(a,c,d+g(a)),etc.   ;
% Via FVAROP an expression like sin(x) is replaced by a system selected;
% name(g001,for instance),the pair (g001.sin(x)) is added to the       ;
% Kvarlst, the pair (sin(x).g001) is added to the 'Kvarlst of sin,thus ;
% allowing a test to be able to uniquely use the name g001 for sin(x). ;
% Finally the pair (rowindex of father of this occurence of sin(x) .   ;
% g001) is added to Prevlst. However if the argument of a sin applica- ;
% tion is not directly recognizable(a*b+a*c or a*(b+c),etc) the argu-  ;
% ment is replaced by a system selected name(g002,for instance),which  ;
% then needs incorporation in the administration. This is also done in ;
% FVAROP: The index of the CODMAT-row used to start the description of ;
% this argument is stored on the property list of g002 as value of the ;
% indicator Rowindex and the Prevlist is now extended with the pair    ;
% (father indx. g002 indx).When storing nested expressions in CODMAT   ;
% the father-child relations based on interchanges of + and * symbols  ;
% are treated in a similar way.So the Prevlst consists of two types of ;
% pairs: (row number.row number) and (row number.subexpression name).  ;
% The CODMAT-row, where the description of this subexpression starts   ;
% can be found on the property list of the subexpression name as value ;
% of the indicator Rowindex. All function applications are stored uni- ;
% quely in Kvarlst. This list is consulted in CODPRI.RED when construc-;
% ting PREFIXLIST,which represents the result as a list of dotted pairs;
% of the form ((sub)expr.name . (sub)expr.value) as to guarantee a cor-;
% rect insertion of the function appl.,i.e. directly ahead of the first;
% (sub)expr. it is part of.After inserting the pair (subexpression name;
% . function application) the corresponding description is removed from;
% the Kvarlst,thus avoiding a multiple insertion. This demands for a   ;
% tool to know when to consult the Kvarlst.This is provided by the ORDR;
% field of the CODMAT-rows.It contains a list of row indices and func- ;
% tion application recognizers, which is recursively built up when     ;
% searching for subexpressions,after its initialization in SSETVARS,   ;
% using the subexpression recognizers introduced during parsing.       ;
% -------------------------------------------------------------------- ;

symbolic procedure setprev(x,y);
% -------------------------------------------------------------------- ;
% arg : Both X and Y are rowindices.                                   ;
% eff : Y is the index of a row where the description of a subexpr.    ;
%       starts. If X is the index of the row where the description of a;
%       toplevel expression starts( an input expression recognizable by;
%       the father-field Farvar) Y is put on top of the list of indices;
%       of subexpressions which have to be printed ahead of this top-  ;
%       level expression.Otherwise we continue searching for this top- ;
%       level father via a recursive call of SetPrev.                  ;
% -------------------------------------------------------------------- ;
if numberp(farvar x)
then setprev(farvar x,y)
else setordr(x,y.ordr(x));

endmodule;

end;

