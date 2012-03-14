module coddec;  % Functions for generating declarations.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Author  :   M.C. van Heerwaarden, W.N. Borst.                       ;
% ------------------------------------------------------------------- ;
%                                                                     ;
% ------------------------------------------------------------------- ;
% The module CODDEC contains the functions, which have to be used to  ;
% generate declarations, associated with the optimized version of a   ;
% set of input expressions when the switch Optdecs is turned on.      ;
% It can also be used via GENTRAN, when the SCOPE-GENTRAN interface is;
% modified, by adding the command TYPEALL Prefixlist;                 ;
% GLOBALS   : -                                                       ;
% INDICATORS: CHKTYPE, ARGTYPE                                        ;
% ENTRIES   : dettype, typecheck, argnrcheck                          ;
% IMPORTED  : Subscriptedvarp, symtabput, sybtabget, symtabrem        ;
%             FROM $gentransrc/util.red                               ;
% CONVERSION: Conversion imposes a partial ordering on types. With    ;
%             respect to this ordering, we can speak of types being   ;
%             greater or less than others. Uncertainty in the type of ;
%             a certain variable or function is expressed by typing   ;
%             the variable in combination with type-bounds, i.e. a    ;
%             variable for which nothing is certain is typed as       ;
%             '(UNKNOWN ALL).                                         ;
% REMARK    : Double precision declarations are dealt with in the     ;
%             following way: any kind of a double precision           ;
%             declaration causes the gentran switch DOUBLE to be      ;
%             switched on. As a result, ALL declarations in the       ;
%             output will be of double precision.                     ;
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


symbolic$

global '(fortconv!* optlang!*)$
fluid '(!*double)$
switch double;

symbolic procedure typeall forms;
   begin
      scalar b, declst,nforms;
      on!-double(forms);
      declst := symtabget(nil, '!*decs!*);
      if optlang!* = 'fortran2
      then <<while declst and not b
             do << b := cadar declst = 'complex or
                        cadar declst = 'implicit! complex;
                   declst := cdr declst >>;
             if b
             then fortconv!* := '(unknown
                                    (integer real complex all)
                                    (bool all)
                                    (char string all)
                                  )
             else fortconv!* := '(unknown
                                    (integer real all)
                                    (bool all)
                                    (char string all)
                                 )
           >>;
      foreach ass in forms do
          <<ass := car ass .
                     if complexp(ass)
                        then cireval(cdr ass)
                        else cdr ass;
            asstype(car ass, cdr ass);
            nforms:=(car ass . cdr ass) . nforms;
          >>;
      apply1('arestore,avarlst);   % For bootstrapping.
      nforms := reverse nforms;
      finish!-typing nforms;
      fix!-implicit();
      return nforms;
   end;


symbolic procedure on!-double(forms);
% ------------------------------------------------------------------- ;
% eff : Changes the Gentran symbol table and the DOUBLE switch to     ;
%       use the Gentran double precision facility.                    ;
%       Any double precision declaration in the symbol table causes   ;
%       the DOUBLE switch to be switched on. Then, these double       ;
%       precision declarations are replaced by their single precision ;
%       types. Walks also through the FORMS-list an switches DOUBLE   ;
%       switch on when FORMS contains a bigfloat or gaussian bigfloat ;
%       number.                                                       ;
% ------------------------------------------------------------------- ;
begin
  scalar newtype;
  foreach dec in symtabget(nil, '!*Decs!*) do
    if newtype := assoc(cadr(dec),
                         '((real!*8 . real)
                           (complex!*16 . complex)
                           (implicit! real!*8 . implicit! real)
                           (implicit! complex!*16 . implicit! complex))
                       ) then
    << symtabput(nil, car(dec), list(cdr newtype));
       !*double := t
    >>;
  on!-double1(forms)
end;

symbolic procedure on!-double1(forms);
if pairp(forms)
then if doublep(car forms)
     then !*double := 't
     else << on!-double1(car forms);
             on!-double1(cdr forms)
          >>;

symbolic procedure fix!-implicit;
% ------------------------------------------------------------------- ;
% eff : Checks every declaration in the symbol table if that          ;
%       declaration matches an implicit declaration in the table.     ;
%       If so, the types are checked and the explicit declaration is  ;
%       removed out of the table                                      ;
% ------------------------------------------------------------------- ;
begin
  scalar decl, type;
  foreach decl in symtabget(nil, '!*decs!*)
  do if (not isimplicit(cadr decl)) and (type := implicitdec(car decl))
     then << if greatertype(type, cdr(decl))
             then typerror(8, cdr(decl) . type);
             symtabrem(nil, car(decl))
          >>
end;



% ------------------------------------------------------------------- ;
% MODULE       Operations on the Symbol table                         ;
% OPERATIONS : getdec, implicitdec, isimplicit, implicittype          ;
% ------------------------------------------------------------------- ;

symbolic procedure getdec(vname);
% ------------------------------------------------------------------- ;
% args: vname = name of the variable which declaration is requested   ;
% ret : the type of the variable as it is explicitly or implicitly    ;
%       stored in the symbol table                                    ;
% ------------------------------------------------------------------- ;
begin
  scalar decl;
  decl := symtabget(nil, vname);
  if not decl
  then decl := implicitdec(vname);
  return decl
end;

symbolic procedure implicitdec(vname);
% ------------------------------------------------------------------- ;
% args: vname = name of the implicit declared variable which          ;
%       declaration is requested                                      ;
% ret : the type of the variable as it is stored in the symbol table  ;
% ------------------------------------------------------------------- ;
begin
  scalar decl, decs;
  decl := nil;
  decs := symtabget(nil, '!*decs!*);
  while not decl and decs do
  << if isimplicit(cadar(decs)) and firstmatch(vname,caar(decs))
     then decl := list(vname, implicittype(cadar(decs)));
     decs := cdr(decs)
  >>;
  return decl
end;

symbolic procedure firstmatch(vname, implicit);
% -------------------------------------------------------------------- ;
% args: vname = variable name                                          ;
%       implicit = range of an implicit declaration (for instance x!-z);
% ret : 'T iff the variable name matches the range, nil otherwise      ;
% -------------------------------------------------------------------- ;
begin
  scalar first;
  first := id2int(car(explode(vname)));
  return first >= id2int(car(explode(implicit))) and
         first <= id2int(cadddr(explode(implicit)))
end;

symbolic procedure isimplicit(type);
% ------------------------------------------------------------------- ;
% args: type = type of a variable                                     ;
% ret : 'T iff the type is an implicit type, nil otherwise            ;
% ------------------------------------------------------------------- ;
begin
  scalar implicit, result, etype;
  implicit := explode('IMPLICIT! );
  etype := explode(type);
  result := 't;
  while result and implicit do
  <<  result := car(etype) = car(implicit);
      implicit := cdr(implicit);
      etype := cdr(etype)
  >>;
  return result
end;

symbolic procedure implicittype(implicit);
% ------------------------------------------------------------------- ;
% args: implicit = an implicit type                                   ;
% ret : the type of the implicit type                                 ;
% ------------------------------------------------------------------- ;
  intern compress pnth(explode implicit,11);


symbolic procedure asstype(lhs, rhs);
% ------------------------------------------------------------------- ;
% Performs typechecking on the assignment statement lhs-rhs, leading  ;
% to a lhs-type, which fits in the ordering imposed by the rhs.       ;
% ------------------------------------------------------------------- ;
begin
   scalar lhstype;
   lhstype :=
        typecheck(dettype(lhs, 'unknown), dettype(rhs, 'unknown), rhs);
   if atom lhs
   then symtabput(nil, lhs, list lhstype)
   else if subscriptedvarp car lhs
   then symtabput(nil, car lhs, list lhstype)
   else symtabput(nil, car lhs, append(list if atom lhstype
                                            then list lhstype
                                            else lhstype,
                                       for each ndx in cdr lhs
                                       collect 'n
              )               )
end;


symbolic procedure dettype(xpr, minimumtype);
% ------------------------------------------------------------------- ;
% args: xpr = some expression                                         ;
%       minimumtype = minimum type xpr should have. This is set when  ;
%                     operators are encountered.                      ;
% eff : Determine type of xpr and typecheck arguments of operators in ;
%       xpr.                                                          ;
% ret : Type of xpr. If no type is known, '(UNKNOWN ALL) is returned. ;
% ------------------------------------------------------------------- ;
%
% Fixed to handle a NIL returned from OPCHECK mcd 22/7/89
%
begin
   scalar type, dtype, optype, mtype, mntype, mxtype;
   return
          if atom(xpr)
          then if numberp xpr
               then if floatp(xpr)
                    then 'real
                    else 'integer
               else if (type := getdec(xpr))
                   and (type := cadr type)
               then if greatertype(minimumtype, mintype type)
                    then if greatertype(minimumtype, maxtype type)
                         then typerror(1, xpr)
                         else
                          << symtabput(nil, xpr, list(type:=returntype
                                       list(minimumtype,maxtype type)));
                             type
                          >>
                    else type
               else << symtabput(nil, xpr, list list(minimumtype,'all));
                       list(minimumtype, 'all)
                    >>
          else if memq(car xpr, domainlist!*)
          then if memq(car xpr, '(!:rd!: !:rn!:))
               then 'real
               else if memq(car xpr, '(!:gi!: !:cr!: !:crn!:))
                    then 'complex
                    else typerror(5, car xpr)
          else if subscriptedvarp2 car xpr
          then << for each ndx in cdr xpr
                  do typecheck('integer, dettype(ndx, 'integer), ndx);
                     % argument minimumtype independent of parameter
                     % minimumtype
                  cadr getdec(car xpr)
               >>
          else if smember('argtype,
                          car( (optype := opcheck xpr) or '(nil)))
          then << mtype:=mntype:=mxtype:=
                                        car eval get(car xpr,'argtype);
                  % mxtype now contains the first type of the class in
                  % which the arguments must be
                  for each arg in cdr xpr
                  do << dtype := dettype(arg, mtype);
                        if greatertype(type := maxtype dtype, mxtype)
                        then mxtype := type;
                        if greatertype(type := mintype dtype, mntype)
                        then mntype := type
                     >>;
                  if atom cdr optype
                  then << if cdr optype = 'argtype
                          then returntype list(mntype, mxtype)
                          else cdr optype
                       >>
                  else if greatertype(mxtype, cadr optype)
                       then <<  if greatertype(mntype, cadr optype)
                                then list(mntype, mxtype)
                                else list(cadr optype, mxtype)
                            >>
                       else cadr optype
               >>
          else if optype
          then << type := car optype;
                  if atom type then type := list type;
                  foreach arg in cdr xpr
                                       % Number of args already checked
                  do << mtype := firstinclass car type;
                        typecheck(car type, dettype(arg, mtype), arg);
                        type := cdr type
                     >>;
                  cdr optype
               >>
          else << for each arg in cdr xpr
                  do dettype(arg, 'unknown);
                  list(minimumtype, 'all)
               >>
end;


symbolic procedure typecheck(lhstype, rhstype, rhs);
% ------------------------------------------------------------------- ;
% args: lhstype = type as known so far for lhs of ass. stat.          ;
%       rhstype = type as known so far for rhs of ass. stat.          ;
%       rhs     = rhs of ass. stat                                    ;
% eff : The rules used for typechecking are :                         ;
%                                                                     ;
%                    Condition:        Check:            Result:      ;
%                                                                     ;
% lhs        |---|   mintype(lhs) >    OK                mintype(lhs) ;
% rhs  |---|         maxtype(rhs)                                     ;
%                                                                     ;
% lhs  |---|         maxtype(lhs) <    ERROR                          ;
% rhs        |---|   mintype(rhs)                                     ;
%                                                                     ;
% lhs      ...-|     maxtype(lhs) <    OK when adjust-   intersection ;
% rhs      ...---|   maxtype(rhs)      ments possible    of lhs & rhs ;
%                                                                     ;
%                    all other cases   OK                intersection ;
%                                                        of lhs & rhs ;
%                                                                     ;
% ret: The - possibly adjusted type of lhs.                           ;
% ------------------------------------------------------------------- ;
begin
  scalar type;
  if greatertype(mintype lhstype, maxtype rhstype)
  then mintype lhstype
  else << type := typeintersec(lhstype, rhstype);
          if greatertype(maxtype rhstype, maxtype type)
          then if not(putmaxtype(rhs, maxtype type))
               then typerror(2, lhstype . rhstype)
       >>;
  return type
end;

symbolic procedure typeintersec(type1, type2);
% ------------------------------------------------------------------- ;
% ret : the intersection of the two types.                            ;
%       generates an error when the intersection is empty or when the ;
%       types are in different typeclasses.                           ;
% ------------------------------------------------------------------- ;
begin
  scalar mint, maxt;
  mint := if greatertype(mintype type1, mintype type2)
          then mintype type1
          else mintype type2;
  maxt := if greatertype(maxtype type1, maxtype type2)
          then maxtype type2
          else maxtype type1;
  if greatertype(mint, maxt)
  then typerror(2, type1 . type2);
  return returntype list(mint, maxt)
end;


symbolic procedure mintype type;
% ------------------------------------------------------------------- ;
% A type may be a pair (l u) wher l is the minimum type for a variable;
% and u is the maximum type. This procedure returns the minimum type. ;
% ------------------------------------------------------------------- ;
   if atom type
   then type
   else car type;

symbolic procedure maxtype type;
% ------------------------------------------------------------------- ;
% A type may be a pair (l u) wher l is the minimum type for a variable;
% and  u is the maximum type. This procedure returns the maximum type.;
% ------------------------------------------------------------------- ;
   if atom type then type
    else if pairp cdr type then cadr type else car type;

symbolic procedure returntype type;
% ------------------------------------------------------------------- ;
% ret: returns mintype if mintype and maxtype are equal and type      ;
% otherwise.                                                          ;
% ------------------------------------------------------------------- ;
   if mintype type = maxtype type
   then mintype type
   else if greatertype(mintype type, maxtype type)
        then typerror(7, nil)
        else type;


symbolic procedure putmaxtype(xpr, type);
% ------------------------------------------------------------------- ;
% args: xpr = some expression                                         ;
%       type = maximum type for variables and for the result type of  ;
%              operators.                                             ;
% eff : To generate a correctly typed program,the maximum type for xpr;
%       should be Type. If the result type of the main operator of Xpr;
%       is not dependent of its arguments, it is sufficient to check  ;
%       this result type. Otherwise, putmaxtype must be applied to all;
%       arguments.                                                    ;
%       When xpr is a variable and its maximum type is greater than   ;
%       Type the maximum type is tried to be smallened to Type.If this;
%       is not possible, an error occurs.                             ;
% ret:  T if xpr is of correct type, i.e. smaller than Type           ;
%       NIL if it is not possible to smallen the type of xpr when     ;
%       necessary.                                                    ;
% note: Perhaps this procedure does not choose consequently between   ;
%       returning an error and returning NIL.                         ;
% ------------------------------------------------------------------- ;
%
% Fixed to handle a NIL returned from OPCHECK mcd 22/7/89
%
begin
   scalar restype, b;
   return if null xpr
          then t
          else if atom xpr
          then if numberp xpr
               then geqtype(type, dettype(xpr, 'integer))
               else if restype := cadr getdec(xpr)
               then if atom restype
                    then geqtype(type, restype)
                    else if geqtype(type, mintype restype)
                    then << if type = mintype restype
                            then symtabput(nil, xpr, list type)
                            else symtabput(nil, xpr,
                                    list list(mintype restype, type));
                            t
                         >>
                    else nil
               else typerror(3, xpr)
          else if subscriptedvarp car xpr
          then geqtype(type, cadr getdec(car xpr))
               % No uncertainty allowed in type of matrix
          else if (restype := cdr (opcheck(xpr) or '(nil))) = 'argtype
                  or listp(restype)
          then << b := t;
                  for each arg in cdr xpr
                  do b := b and putmaxtype(arg, type);
                  b
               >>
          else if restype
          then geqtype(type, restype)
          else geqtype(type, 'unknown)
end;


% ------------------------------------------------------------------- ;
% MODULE    : CONVERSION fortconv!*, cconv!*, ratconv!*, pasconv!*,   ;
%                        f90conv!*                                    ;
% STRUCTURE : conv!* ::= (UNKNOWN (class-list)-list)                  ;
%             class-list ::= ordered list of types: a type can be     ;
%             converted to the types which occur in the rest of the   ;
%             list.                                                   ;
% OPERATIONS: greatertype, geqtype, lesstype, getnum                  ;
% GLOBALS   : fortconv!*, cconv!*, ratconv!*, pasconv!*,f90conv!*     ;
% INDICATORS: conversion                                              ;
% ------------------------------------------------------------------- ;

global '(fortconv!* cconv!* ratconv!* pasconv!* f90conv!* optlang!*);

put('fortran, 'conversion, 'fortconv!*);
put('f90, 'conversion, 'f90conv!*);
put('c, 'conversion, 'cconv!*);
put('ratfor, 'conversion, 'ratconv!*);
put('pascal, 'conversion, 'pasconv!*);

fortconv!* := '(unknown
                   (integer real complex all)
                   (bool all)
                   (char string all)
               );

f90conv!* := '(unknown
                   (integer real complex all)
                   (bool all)
                   (char string all)
               );

cconv!* := ratconv!* := pasconv!* :=
              '(unknown
                   (integer real all)
                   (bool all)
                   (char string all)
               );

symbolic procedure getnum;
% ------------------------------------------------------------------- ;
% Returns class of numeric types.                                     ;
% ------------------------------------------------------------------- ;
begin
  scalar conv, found;
  conv := eval get(if optlang!* then optlang!* else 'fortran,
                   'conversion);
  while not found and (conv := cdr conv)
  do if caar conv = 'integer
     then found := t;
  return car conv
end;

symbolic procedure greatertype(t1, t2);
% ------------------------------------------------------------------- ;
% args: t1 = t2 = type                                                ;
% ret : T       if t1 >  t2                                           ;
%                      t                                              ;
%                                                                     ;
%       NIL     if t1 <=  t2                                          ;
%                       t                                             ;
% note: >  means greater in the sense of the ordering which is        ;
%        t                                                            ;
%       described above for various languages.                        ;
% ------------------------------------------------------------------- ;
begin
  scalar conv, class, found, found1, found2, f;
  conv := eval get(if optlang!* then optlang!*
                                else 'fortran, 'conversion);
  if car conv = t2
  then f := t
  else if car conv = t1
  then f := nil
  else << while (conv := cdr conv) and not found
          do << class := car conv;
                while class and not found2
                do << if car class = t1
                      then found1 := t;
                      if car class = t2
                      then found2 := t
                      else class := cdr class
                   >>;
                if found2
                then << class := cdr class;
                        while class and not f
                        do if car class = t1
                           then found1 := f := t
                           else class := cdr class;
                     >>;
                if (found1 and not found2) or (not found1 and found2)
                then typerror(4, t1 . t2)
                else if found1 and found2 then found := t
       >>    >>;
  return f
end;

symbolic procedure geqtype(t1, t2);
% ------------------------------------------------------------------- ;
% args: t1 = t2 = type                                                ;
% ret : T       if t1 >=  t2                                          ;
%                       t                                             ;
%                                                                     ;
%       NIL     if t1 <  t2                                           ;
%                      t                                              ;
% Note: See greatertype.                                              ;
% ------------------------------------------------------------------- ;
begin
  scalar conv, class, found, found1, found2, f;
  conv := eval get(if optlang!* then optlang!*
                                else 'fortran, 'conversion);
  if car conv = t2
  then f := t
  else if car conv = t1
  then nil
  else << while (conv := cdr conv) and not found
          do << class := car conv;
                while class and not found2
                do << if car class = t1
                      then found1 := t;
                      if car class = t2
                      then found2 := t
                      else class := cdr class
                   >>;
                if found2
                then while class and not f
                     do if car class = t1
                        then found1 := f := t
                        else class := cdr class;
                if (found1 and not found2) or (not found1 and found2)
                then typerror(4, t1 . t2)
                else if found1 and found2 then found := t
       >>    >>;
  return f
end;

symbolic procedure lesstype(t1, t2);
   greatertype(t2, t1);

symbolic procedure firstinclass type;
% ------------------------------------------------------------------- ;
% Return : First (smallest) type of the class of types in which Type  ;
% belongs.                                                            ;
% ------------------------------------------------------------------- ;
begin
   scalar conv, found, class, mclass;
   conv := eval get(if optlang!* then optlang!*
                                 else 'fortran, 'conversion);
   return if (type = 'all) or (type = 'unknown)
          then 'unknown
          else << while (conv := cdr conv) and not found
                  do << mclass := car(class := car conv);
                        while class and not found
                        do << if car class = type
                              then found := t;
                              class := cdr class
                     >>    >>;
                  if found
                  then mclass
                  else typerror(5, type)
               >>
end;

symbolic procedure lastinclass type;
% ------------------------------------------------------------------- ;
% Returns : Last (greatest) type of the class of types in which Type  ;
% belongs.                                                            ;
% ------------------------------------------------------------------- ;
begin
   scalar conv, found, class;
   conv := eval get(if optlang!* then optlang!*
                                 else 'fortran, 'conversion);
   if type neq 'all
   then while (conv := cdr conv) and not found
        do << class := car conv;
              while class and not found
              do if car class = type
                 then << found := t;
                         repeat type := car class
                         until (class := cdr class) = '(all)
                      >>
                 else class := cdr class
           >>;
   return type
end;

% ------------------------------------------------------------------- ;
% MODULE    : FUNCTION TYPING                                         ;
% STRUCTURE :                                                         ;
% OPERATIONS: resulttype                                              ;
% GLOBALS   :                                                         ;
% INDICATORS: type: (argumenttype . resulttype)                       ;
%             argumenttype:                                           ;
%              Atom ==> 1 argument                                    ;
%              List with 1 type ==> number of arguments must be >= 2  ;
%              List with > 1 type ==> number of types = number        ;
%                                                         of arguments;
%             resulttype has the following meaning                    ;
%                                                                     ;
%   resulttype     meaning                                            ;
%                                                                     ;
%    'argtype   the type of the result is determined by the arguments ;
%    'type      the type of the result is always the given type       ;
%    '(type)    the type of the result is determined in the following ;
%               way:                                                  ;
%                                                                     ;
%             maximium of the    minimum of the                       ;
%             mintypes of the    maxtypes of the                      ;
%             arguments          arguments                            ;
%                                                                     ;
%       type       |------------------|                               ;
%                  |------------------|         = type of the result  ;
%                                                                     ;
%                  |-----type---------|                               ;
%                        type---------|         = type of the result  ;
%                                                                     ;
%                  |------------------|    type                       ;
%                                          type = type of the result  ;
%                                                                     ;
%             argtype:                                                ;
%              The type of a function or argument can be one of a     ;
%              class of types. Evaluation of the value of this        ;
%              indicator returns the whole class.                     ;
%                                                                     ;
% ------------------------------------------------------------------- ;



for each op in '(times plus difference)
do << put(op, 'chktype, '((argtype) . argtype));
      put(op, 'argtype, '(getnum))
   >>;
put('quotient, 'chktype, '((argtype argtype) . (real)));
put('quotient, 'argtype, '(getnum));
put('expt, 'chktype, '((argtype argtype) . argtype));
put('expt, 'argtype, '(getnum));
put('minus, 'chktype, '(argtype . argtype));
put('minus, 'argtype, '(getnum));
for each op in '(or and)
do put(op, 'chktype, '((bool) . bool));
put('not, 'chktype, '(bool . bool));
for each op in '(eq leq geq greaterp lessp neq)
do << put(op, 'chktype, '((argtype argtype) . bool));
      put(op, 'argtype, '(getnum))
   >>;
for each op in '(sin cos tan asin acos atan sinh cosh tanh asinh
                                            acosh atanh cot log sqrt)
do put(op, 'chktype, '(real . real));

symbolic procedure opcheck op;
% ------------------------------------------------------------------- ;
% args: op = operator                                                 ;
% eff : performs a check on the number of arguments                   ;
% ret : Complete type of operator, i.e.                               ;
%       (type-of-arguments-list .  resulttype)                        ;
% note: Decisions about what to do when Op's type is ARGTYPE are left ;
%       to the calling procedures.                                    ;
% ------------------------------------------------------------------- ;
begin
   scalar optype;
   return if not(optype := get(car op, 'chktype))
          then 'nil
          else if atom car optype
          then if length cdr op = 1
               then optype
               else typerror(6, car op)
          else if cdar optype
          then if length cdr op = length car optype
               then optype
               else typerror(6, car op)
          else if length cdr op >= 2
               then optype
               else typerror(6, car op)
end;

% ------------------------------------------------------------------- ;
% MODULE finish type analysis & checking.                             ;
%        Each variable will be bound to a single type.                ;
% ------------------------------------------------------------------- ;

symbolic procedure finish!-typing prflst;
% ------------------------------------------------------------------- ;
% args: prflst = the prefixlist from the optimizer.                   ;
% eff : After some simple checks, each variable in the assignment has ;
%       a definite type. This type can be found in the symbol table.  ;
% ret : -                                                             ;
% ------------------------------------------------------------------- ;
begin
   scalar ltype, rtype;
   for each item in prflst
   do if (ltype := det!&bind(car item, 'all))
      then << if ltype = 'all
              then if (rtype := det!&bind(cdr item, ltype)) = 'all
                   then
                     write list("Unknown type for operator", cdr item)
                   else ltype := lastinclass rtype
              else rtype := det!&bind(cdr item, ltype);
              if greatertype(rtype, ltype)
              then typerror(2, item)
              else if atom car item
              then symtabput(nil, car item, list ltype)
              else symtabput(nil, caar item, list ltype)
           >>
      else % When a lhs variable is not declared, it is a variable
           % generated by the optimizer which still needs typing.
           symtabput(nil, car item, list det!&bind(cdr item, 'all))
end;


symbolic procedure det!&bind(xpr, maximumtype);
% ------------------------------------------------------------------- ;
% args: xpr = expression for which a definite type must be determined ;
%       maximumtype = the maximum type which Xpr may obtain; only used;
%                     in cases where the variable's type is           ;
%                     (UNKNOWN ALL).                                  ;
%                     Typechecking is done in finish!-typing.         ;
% eff : if xpr is a variable,its definite type is stored on the symbol;
%       table.                                                        ;
% ret : the type of Xpr after binding all variables to a certain type.;
% ------------------------------------------------------------------- ;
%
% Fixed to handle a NIL returned from OPCHECK mcd 22/7/89
%
begin
   scalar type, mtype, optype;
   return if idp(xpr) or constp(xpr)
          then if constp(xpr)
               then dettype(xpr, 'integer)
               else det!&bindmax(xpr, maximumtype)
          else if subscriptedvarp car xpr
          then << for each ndx in cdr xpr
                  do det!&bind(ndx, 'integer);
                  det!&bindmax(car xpr, maximumtype)
               >>
          else if smember('argtype,
                          car((optype := opcheck xpr) or '(nil)))
          then << mtype := 'unknown;
                  for each arg in cdr xpr do
                  if greatertype(type:=
                                      det!&bind(arg,maximumtype),mtype)
                     then mtype := type;
                  % Fixed to handle complex division. ;
                  if atom cdr optype
                  then << if cdr optype = 'argtype
                          then mtype
                          else cdr optype
                       >>
                  else if greatertype(mtype, cadr optype)
                       then mtype
                       else cadr optype
               >>
          else if optype
          then << type := car optype;
                  if atom type then type := list type;
                  for each arg in cdr xpr
                  do << det!&bind(arg, car type);
                        type := cdr type
                     >>;
                  cdr optype
               >>
          else << for each arg in cdr xpr
                  do det!&bind(arg, 'all);
                  maximumtype
               >>
end;


symbolic procedure det!&bindmax(xpr, maximumtype);
begin
   scalar type;
   if pairp(type := cadr getdec(xpr))
   then if maxtype type = 'all
        then if mintype type = 'unknown
             then << type := maximumtype;
                     symtabput(nil, xpr, list maximumtype)
                  >>
             else << type := lastinclass mintype type;
                     if greatertype(type, maximumtype)
                        then type:=maximumtype;
                     symtabput(nil, xpr, list type)
                  >>
        else symtabput(nil, xpr, list(type := maxtype type));
    return type
end;


symbolic procedure typerror(errornr, xpr);
% ------------------------------------------------------------------- ;
% eff : Besides the error message, the declarations known so far are  ;
%       printed.                                                      ;
% ------------------------------------------------------------------- ;
   if errornr = 6
   then rederr list("Wrong number of arguments for", xpr)
   else << terpri!* t;
           write("***** Type error:");
           terpri!* t;
           printdecs();
           if errornr = 1
           then rederr list("Wrong type for variable", xpr)
           else if errornr = 2
           then <<assgnpri(cdr xpr, list car xpr, t);
                  rederr list("Wrong typing")>>
           else if errornr = 3
           then rederr list(xpr, "not checked on type")
           else if errornr = 4
           then rederr
             list(car xpr, "and", cdr xpr, "in different type classes")
           else if errornr = 5
           then rederr list(xpr, "is an unknown type")
           else if errornr = 7
           then rederr list("Wrong reasoning")
           else if errornr = 8
           then rederr list(car xpr, "cannot be redeclared to",cdr xpr)
           else rederr list("Unknown type error")
        >>;

symbolic expr procedure subscriptedvarp v;
    % --------------------------------------------------------------- ;
    %  Returns t if and only if v has been declared to be a           ;
    %  subscripted variable name, or assumed to be so by the parser.  ;
    % --------------------------------------------------------------- ;
    length symtabget(nil, v) > 2
    or flagp(v,'subscripted);

symbolic expr procedure subscriptedvarp2 v;
    % --------------------------------------------------------------- ;
    %  Returns t if and only if v has been declared to be a           ;
    %  subscripted variable name.                                     ;
    % --------------------------------------------------------------- ;
    length symtabget(nil, v) > 2;

global '(!*symboltable!*);

symbolic expr procedure dumpsymtab;
    begin scalar res;
    res :=
        foreach pn in !*symboltable!* conc
                list(
                     list('symtabput,mkquote pn, mkquote '!*type!*,
                                     mkquote symtabget(pn, '!*type!*)),
                     list('symtabput,mkquote pn, mkquote '!*params!*,
                                     mkquote symtabget(pn,'!*params!*)),
                     list('symtabput,mkquote pn, mkquote '!*decs!*,
                                     mkquote symtabget(pn, '!*decs!*))
                     );
    res := 'progn . list('setq,'!*symboltable!*,mkquote !*symboltable!*)
                   . res;
    return res
    end;

%--- Coddec patch. John Boers wil i.p.v. ranges ook atomen impliciet
%---               declareren. dus: s,t i.p.v. s-t.

symbolic procedure firstmatch(vname, implicit);
% -------------------------------------------------------------------- ;
% args: vname = variable name                                          ;
%       implicit = range of an implicit declaration (for instance x!-z);
% ret : 'T iff the variable name matches the range, nil otherwise      ;
% -------------------------------------------------------------------- ;
begin
  scalar first;
  first := id2int(car(explode(vname)));
  if freeof(explode implicit,'!-)
     then return first=id2int(car(explode(implicit)))
     else
  return first >= id2int(car(explode(implicit))) and
         first <= id2int(cadddr(explode(implicit)))
end;
endmodule;
end;
