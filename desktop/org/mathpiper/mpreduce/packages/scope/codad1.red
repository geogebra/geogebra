module codad1;  % Description of some procedures.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Authors :   J.A. van Hulzen, B.J.A. Hulshof, W.N. Borst.            ;
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

% ------------------------------------------------------------------- ;
% The module CODAD1 contains the description of the procedures        ;
% IMPROVELAYOUT (part 1), TCHSCHEME (part 2) and CODFAC (part 3),     ;
% which are used in the procedure OPTIMIZELOOP (see the module CODCTL);
% to complete the effect of an application of EXTBRSEA (see the module;
% CODOPT). Application of each of these routines is completed by re-  ;
% turning a Boolean value, which is used to decide if further optimi- ;
% zation is still profitable.                                         ;
% The Smacro's Find!+Var and Find!*Var form service facilities, needed;
% at different places in this module. These Smacro's define an applic-;
% ation of the procedure GetCind.                                     ;
% ------------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
% Global identifiers needed in this module are:                       ;
% ------------------------------------------------------------------- ;

global '(rowmin rowmax kvarlst codbexl!*);

% ------------------------------------------------------------------- ;
% The meaning of these globals is given in the module CODMAT.         ;
% ------------------------------------------------------------------- ;

symbolic procedure getcind(var,varlst,op,fa,iv);
% ------------------------------------------------------------------- ;
% The purpose of the procedure GetCind is to create a column in CODMAT;
% which will be associated with the variable Var if this variable does;
% not yet belong to the set Varlst,i.e.does not yet play a role in the;
% corresponding PLUS- or TIMES setting (known by the value of Op).Once;
% the column exists (either created or already available), its Zstrt  ;
% is modified by inserting the Z-element (Fa,IV) in it. Finally the   ;
% corresponding Z-element for the father-row, i.e. (Y,IV) is returned.;
% ------------------------------------------------------------------- ;
begin scalar y,z;
  if null(y:=get(var,varlst))
  then
  <<y:=rowmin:=rowmin-1;
    put(var,varlst,y);
    setrow(y,op,var,nil,nil)
  >>;
  setzstrt(y,inszzzn(z:=mkzel(fa,iv),zstrt y));
  return mkzel(y,val z)
end;

symbolic smacro procedure find!+var(var,fa,iv);
getcind(var,'varlst!+,'plus,fa,iv);

symbolic smacro procedure find!*var(var,fa,iv);
getcind(var,'varlst!*,'times,fa,iv);

% ------------------------------------------------------------------- ;
% PART 1 : LAYOUT IMPROVEMENT                                         ;
% ------------------------------------------------------------------- ;

symbolic procedure improvelayout;
% ------------------------------------------------------------------- ;
% During optimization, and thus during common subexpression generation;
% it might happen that a (sub)expression is reduced to a single varia-;
% ble, leading to output containing the assignment statements :       ;
%  b:=b-thing;                                                        ;
%  ......                                                             ;
%  a:=b;                                                              ;
% This redundancy can be removed by replacing all occurrences of b by ;
% a, by replacing b:=b-thing by a:=b=thing and by removing a:=b. Here ;
% we assume a,b to be only cse-names.                                 ;
% ------------------------------------------------------------------- ;
begin scalar var,b;
  for x:=0:rowmax do
  if not (numberp(var:=farvar x)
          or
          pairp(var)
          or
          (member(x,codbexl!*)
           and
           (get(var,'nex) or
            not(flagp(var, 'newsym)) or
            get(var,'alias)
            % or not(get(var,'alias)) % JB 10/3/94
            % finds no cse in p.e. cos(e^s6),sin(e^s6)
           )))
     and testononeel(var,x) then b:=t;
  % ----------------------------------------------------------------- ;
  % If B=T redundancy was removed from CODMAT, but not necessarily    ;
  % from Kvarlst, the list of pairs of kernels and names associated   ;
  % with them. ImproveKvarlst is applied to achieve this.             ;
  % ----------------------------------------------------------------- ;
  if b then improvekvarlst();
  return b
end;

symbolic procedure testononeel(var,x);
% ------------------------------------------------------------------- ;
% Row X,having Var as its assigned variable, and defining some expres-;
% sion, through its Zstrt, Chrow and ExpCof, is analysed.             ;
% If this row defines a redundant assignment statement the above indi-;
% cated actions are performed.                                        ;
% ------------------------------------------------------------------- ;
begin
  scalar scol,srow,el,signiv,signec,zz,ordrx,negcof,trow,
                                           oldvar,b,el1,scof,bop!+,lhs;
  if (zz:=zstrt x) and null(cdr zz) and null(chrow x) and
      !:onep(dm!-abs(signiv:=ival(el:=car zz))) and
      !:onep(signec:=expcof(x))
  %   !:onep(dm!-abs(signec:=expcof(x)))
  %   This could mean a:=b^(-1), which is rather tricky to update
  %   when b is used in other plusrows.  JB. 7-5-93.
  then
   << % ------------------------------------------------------------- ;
      % Row(X) defines a Zstreet, consisting of one Z-element. The    ;
      % variable-name, associated with this element is stored in the  ;
      % FarVar-field of the column, whose index is in the Yind-part of;
      % this Z-element,i.e. Oldvar:=FarVar(SCol),the b mentioned above;
      % The IVal-value of this element, an exponent or a coefficient, ;
      % is 1 or -1 and the ExpCof-value, a coefficient or an exponent,;
      % is also 1 or -1. Realistic possibilities are of course only   ;
      % 1*Oldvar^1 or -1*Oldvar^1 (i.e. 1*b^1 or -1*b^1).             ;
      % ------------------------------------------------------------- ;
      scol:=yind el;
      oldvar:=farvar(scol);
      if srow:=get(oldvar,'rowindex)
       then b:=t
       else
        if assoc(oldvar,kvarlst) and
           !:onep(signiv) and !:onep(signec) and
           not member(oldvar,codbexl!*)
         then b:=t;
      % ------------------------------------------------------------- ;
      % So B=T if either Oldvar has its own defining row, whose index ;
      % is stored as value of the indicator Rowindex, i.e. if Oldvar  ;
      % defines a cse, or if Oldvar is the name of a kernel, stored in;
      % Kvarlst, as cdr-part of the pair having Oldvar as its car-part;
      % ------------------------------------------------------------- ;
       if b
        then
         << % ------------------------------------------------------- ;
            % We start replacing all occurrences of Oldvar by Var, in ;
            % both the PLUS- and the TIMES-part of CODMAT, by applying;
            % the function TShrinkCol. In addition all eventually exis;
            % ting occurences of Oldvar in Kvarlst have to replaced as;
            % well by Var(,the a mentioned above).                    ;
            % ------------------------------------------------------- ;
            setzstrt(scol,delyzz(x,zstrt scol));
            tshrinkcol(oldvar,var,'varlst!+);
            tshrinkcol(oldvar,var,'varlst!*);
            if ((opval(x) eq 'plus) and !:onep(dm!-minus signiv))
                or
               ((opval(x) eq 'times) and !:onep(dm!-minus signec))
             then << var:=list('minus,var);
                     kvarlst:=subst(var,oldvar,kvarlst);
                     preprefixlist:=subst(var,oldvar,preprefixlist);
                     var:=cadr var;
                     negcof:=-1
                  >>
             else << kvarlst:=subst(var,oldvar,kvarlst);
                     preprefixlist:=subst(var,oldvar,preprefixlist);
                     negcof:=1
                  >>;
            if (lhs:=get(oldvar,'inlhs))
               then
               << put(lhs,'nex,subst(var,oldvar,get(lhs,'nex)));
                  remprop(oldvar,'inlhs)>>;
            if (lhs:=get(oldvar,'inalias))
               then
               << updatealiases(oldvar,var);
                  %put(lhs,'alias,subst(var,oldvar,get(lhs,'alias)));
                  remprop(oldvar,'inalias)>>;
            if srow
            then
             << % --------------------------------------------------- ;
                % Oldvar is the name of a cse, defined through the row;
                % index Srow. So this cse-definition has to be assign-;
                % ed to Var as new value and the Srow itself has to be;
                % made redundant. The Ordr-field of Var has to be chan;
                % ged to be able to remain guaranteeing a correct out-;
                % put sequence.                                       ;
                % --------------------------------------------------- ;
                ordrx:=ordr(x);
                bop!+:=opval(srow) eq 'plus;
                if bop!+ then scof:=expcof srow
                         else scof:=dm!-times(negcof,expcof(srow));
                setrow(x,opval srow,var,list(chrow srow,scof),
                                                           zstrt srow);
                setordr(x,append(ordr srow,remordr(srow,ordrx)));
                if !:onep(dm!-minus signiv)
                 then
                  <<foreach z in zstrt(scol) do
                       setival(z,dm!-minus ival(z));
                    foreach ch in chrow(x) do
                       setexpcof(ch,dm!-minus expcof(ch));
                    if trow:=get(var,'varlst!*) then
                    foreach el in zstrt(trow) do
                       setexpcof(xind el, dm!-minus expcof(xind el));
                  >>;
                foreach ch in chrow(srow) do setfarvar(ch,x);
                clearrow(srow);
                setordr(srow,nil);
                codbexl!*:=subst(x,srow,codbexl!*);
                foreach z in zstrt(x) do
                 <<if bop!+ then setival(z,dm!-times(signiv,ival(z)));
                   setzstrt(yind z,inszzz(mkzel(x,val z),
                                            delyzz(srow,zstrt yind z)))
                 >>;
                for sindex:=0:rowmax
                 do setordr(sindex,subst(x,srow,ordr sindex));
                testononeel(var,x)
             >>
            else
             << % --------------------------------------------------- ;
                % Oldvar is the system-generated name of a kernel.    ;
                % The internal administration is modified, as to pro- ;
                % vide Var with its new role.                         ;
                % As a side-effect the index X of the kernel defining ;
                % row is replaced in CodBexl!* by the name Var, if oc-;
                % curring of course, i.e. if this function definition ;
                % was given at toplevel on input.                     ;
                % This information is used in ImproveKvarlst.         ;
                % --------------------------------------------------- ;
                codbexl!*:=subst(var,x,codbexl!*);
                ordrx:=remordr(oldvar,ordr x);
                clearrow(x);
                setordr(x,nil);
                for sindex:=0:rowmax do
                 setordr(sindex,
                              updordr(ordr sindex,var,oldvar,ordrx,x));
                improvekvarlst()
             >>;
         >>
   >>;
  return b;
end$

symbolic procedure remordr(x,olst);
% ------------------------------------------------------------------- ;
% Olst is the value of the Ordr-field of a row of CODMAT. Olst defines;
% in which order the cse's, occurring in the (sub)expression, whose   ;
% description starts in this row, have to be printed ahead of this    ;
% (sub)expression. It is a list of kernelnames and/or indices of rows ;
% where cse-descriptions start.                                       ;
% RemOrdr returns Olst after removal of X, if occcurring.             ;
% ------------------------------------------------------------------- ;
if null(olst)
then olst
else
  if car(olst)=x
  then remordr(x,cdr olst)
  else car(olst).remordr(x,cdr olst);

symbolic procedure updordr(olst,var,oldvar,ordrx,x);
% ------------------------------------------------------------------- ;
% Olst is described in RemOrdr. OrdrX is the Olst of row X after remo-;
% val Oldvar from it. Row X defines Var:=Oldvar. Oldvar, a kernelname,;
% is replaced by Var in Olst. If X is occurring in Olst OrdrX have to ;
% be inserted in Olst. The thus modified version of Olst is returned. ;
% ------------------------------------------------------------------- ;
if null(olst)
then olst
else
  if car(olst) eq oldvar
  then var.updordr(cdr olst,var,oldvar,ordrx,x)
  else
    if car(olst)=x
    then append(var.ordrx,updordr(cdr olst,var,oldvar,ordrx,x))
    else car(olst).updordr(cdr olst,var,oldvar,ordrx,x);

symbolic procedure improvekvarlst;
% ------------------------------------------------------------------- ;
% Kvarlst, a list of pairs (name . function definition) is improved,if;
% necessary. This is only required if in the list CodBexl!* occuring  ;
% names are not yet used in Kvarlst. Hence adequate rewriting of      ;
% b:=sin(x)                                                           ;
% ........                                                            ;
% a:=b                                                                ;
% into                                                                ;
% a:=sin(x) is needed,i.e. replacement of (b . sin(x)) by (a . sin(x));
% in Kvarlst.                                                         ;
% ------------------------------------------------------------------- ;
begin scalar invkvl,newkvl,x,y,kv,lkvl,cd,cd1;
  newkvl:=kvarlst;
  repeat
  <<lkvl:=kvarlst:=newkvl;
    invkvl:=newkvl:=nil;
    while lkvl do
    <<kv:=car(lkvl);
      lkvl:=cdr(lkvl);
      cd1:=member(car kv,codbexl!*);
      x:=assoc(cdr kv,invkvl);
      if x
      then cd:=(cd1 and member(cdr x,codbexl!*));
      if x and not cd
      then
      <<kv:=car(kv);
        x:=cdr(x);
        if cd1
        then <<y:=x;
               x:=kv;
               kv:=y>>;
        tshrinkcol(kv,x,'varlst!+);
        tshrinkcol(kv,x,'varlst!*);
        for rindx:=0:rowmax do
        setordr(rindx,subst(x,kv,ordr rindx));
        newkvl:=subst(x,kv,newkvl);
        invkvl:=subst(x,kv,invkvl);
        lkvl:=subst(x,kv,lkvl)
      >>
      else
      <<invkvl:=(cdr(kv).car(kv)).invkvl;
        newkvl:=kv.newkvl
      >>
    >>
  >>
  until length(kvarlst)=length(newkvl);
end;

symbolic procedure tshrinkcol(oldvar,var,varlst);
% ------------------------------------------------------------------- ;
% All occurrences of Oldvar have to be replaced by Var. This is done  ;
% by replacing the PLUS and TIMES column-indices of Oldvar by the cor-;
% responding indices of Var. Y1 and Y2 get the value of the Oldvar-   ;
% index and the Var-index, respectively. As a side-effect, all additi-;
% onal information, stored in the property-list of Oldvar is removed. ;
% ------------------------------------------------------------------- ;
begin scalar y1,y2;
  if get(oldvar,'inalias)
     then updatealiases(oldvar, var);
  if y1:=get(oldvar,varlst)
  then
  <<if y2:=get(var,varlst)
    then
    <<foreach z in zstrt(y1) do
      <<setzstrt(y2,inszzzn(z,zstrt y2));
        setzstrt(xind z,inszzzr(mkzel(y2,val z),
                 delyzz(y1,zstrt xind z)))
      >>;
      clearrow(y1)
    >>
    else
    <<setfarvar(y1,var);
      put(var,varlst,y1)
    >>;
    remprop(oldvar,varlst)
  >>;
  remprop(oldvar,'npcdvar);
  remprop(oldvar,'nvarlst);
end;

symbolic procedure updatealiases(old, new);
% ----------------------------------------------------------------- ;
% Variable old is going to be replaced  by new.
% We hav eto ensure that the alias-linking remains
% consistent. This means that the following has to
% be updated:
% Occurrence-info of index-alias:
%           new.inalias <- old.inalias
% The aliased vars have to be informed that the alias
% is performed by a new variable:
%           alias <- new|old
%           original.finalalias <- new|old
%     where A|B means : replace B by A.
% ----------------------------------------------------------------- ;
begin scalar original;
  put(new,'inalias,get(old,'inalias));
  flag(list new,'aliasnewsym);
  foreach el in get(old,'inalias) do
     <<put(el,'alias,subst(new,old,(original:=get(el,'alias))));
       if atom original
         then put(original,'finalalias,
                    subst(new, old, get(original,'finalalias)))
         else put(car original,'finalalias,
                    subst(new,old,get(car original,'finalalias)))
     >>;
end$

% ------------------------------------------------------------------- ;
% PART 2 : INFORMATION MIGRATION                                      ;
% ------------------------------------------------------------------- ;
symbolic procedure tchscheme;
% ------------------------------------------------------------------- ;
% A product(sum) -reduced to a single element- can eventually be remo-;
% ved from the TIMES(PLUS)-part of CODMAT. If certain conditions are  ;
% fulfilled (defined by the function TransferRow) it is transferred to;
% the Zstreet of its father PLUS(TIMES)-row and its index is removed  ;
% from the ChRow of its father.                                       ;
% T is returned if atleast one such a migration event takes place.    ;
% NIL is returned otherwise.                                          ;
% ------------------------------------------------------------------- ;
begin scalar zz,b;
  for x:=0:rowmax do
  if not(farvar(x)=-1)
     and (zz:=zstrt x) and null(cdr zz) and transferrow(x,ival car zz)
   then <<chscheme(x,car zz); b:=t>>;
  return b;
end;

symbolic procedure chscheme(x,z);
% ------------------------------------------------------------------- ;
% The Z-element Z, the only element the Zstreet of row(X) has, has to ;
% be transferred from the PLUS(TIMES)-part to the TIMES(PLUS)-part of ;
% CODMAT.                                                             ;
% ------------------------------------------------------------------- ;
begin scalar fa,opv,cof,exp;
    setzstrt(yind z,delyzz(x,zstrt yind z));
    setzstrt(x,nil);
    if opval(x) eq 'plus
    then <<exp:=1; cof:=ival z>>
    else <<exp:=ival z; cof:=1>>;
 l1: fa:=farvar(x);
     opv:=opval(x);
     if opv eq 'plus
     then
     <<cof:=dm!-expt(cof,expcof(x));
       exp:=dm!-times(expcof(x),exp);
       chdel(fa,x);
       clearrow(x);
       if null(zstrt fa) and transferrow(fa,exp)
       then <<x:=fa; goto l1>>
     >>
     else
     << if opv eq 'times
        then
        <<cof:=dm!-times(cof,expcof(x));
          chdel(fa,x);
          clearrow(x);
          if null(zstrt fa) and transferrow(fa,cof)
          then <<x:=fa; goto l1>>
        >>
     >>;
     updfa(fa,exp,cof,z)
end;

symbolic procedure updfa(fa,exp,cof,z);
% ------------------------------------------------------------------- ;
%  FA is the index of the father-row of the Z-element Z,which has to  ;
% be incorporated in the Zstreet of this row. Its exponent is Exp and ;
% its coefficient is Cof, both computed in its calling function       ;
% ChScheme.                                                           ;
% ------------------------------------------------------------------- ;
if opval(fa) eq 'plus
then setzstrt(fa,inszzzr(find!+var(farvar yind z,fa,cof),zstrt fa))
else
<<setzstrt(fa,inszzzr(find!*var(farvar yind z,fa,exp),zstrt fa));
  setexpcof(fa,dm!-times(cof,expcof(fa)))
>>;

symbolic procedure transferrow(x,iv);
% ------------------------------------------------------------------- ;
% IV is the Ivalue of the Z-element, oreming the Zstreet of row X.    ;
% This element can possibly be transferred.                           ;
% T is returned if this element can be transferred. NIL is returned   ;
% otherwise.                                                          ;
% ------------------------------------------------------------------- ;
if opval(x) eq 'plus
 then transferrow1(x) and opval(farvar x) eq 'times
 else transferrow1(x) and transferrow2(x,iv);

symbolic procedure transferrow1(x);
% ------------------------------------------------------------------- ;
% T is returned if row(X) defines a primitive expression (no children);
% which is part of a larger expression, i.e. row(X) defines a child-  ;
% expression.                                                         ;
% ------------------------------------------------------------------- ;
null(chrow x) and numberp(farvar x);

symbolic procedure transferrow2(x,iv);
% ------------------------------------------------------------------- ;
% Row(X) defines a product of the form ExpCof(X)*(a variable) ^ IV,   ;
% which is part of a sum.                                             ;
% X is temporarily removed from the list of its fathers children when ;
% computing B, the return-value.                                      ;
% B=T if the father-row defines a sum and if either the exponent IV=1 ;
% or if the father-Zstreet is empty (no primitive terms) and the fa-  ;
% ther itself can be transferred, i.e. if ExpCof(X)*(a variable) ^ (IV;
% *ExpCof(Fa)) can be incorporated in the Zstreet of the grandfather- ;
% row (,which again defines a product).                               ;
% ------------------------------------------------------------------- ;
begin scalar fa,b;
  fa:=farvar(x);
  chdel(fa,x);
  b:=opval(fa) eq 'plus and (iv=1 or (null(zstrt fa) and
                            transferrow(fa,iv*expcof(fa))));
  setchrow(fa,x.chrow(fa));
  return b;
end;

% ------------------------------------------------------------------- ;
% PART 3 : APPLICATION OF THE DISTRIBUTIVE LAW.                       ;
% ------------------------------------------------------------------- ;
% An expression of the form a*b + a*c + d is distributed over 3 rows  ;
% of CODMAT : One to store the sum structure, i.e. to store the pp of ;
% the sum, being d, in a Zstrt and 2 others to store the composite    ;
% terms a*b and a*c as monomials. The indices of the latter rows are  ;
% also stored in the list Chrow, associated with the sum-row.         ;
% In addition 4 columns are introduced. One to store the 2 occurrences;
% of a and 3 others to store the information about b,c and d. The a,b ;
% and c column belong to the set of TIMES-columns, i.e. a,b and c are ;
% elements of the list Varlst!* (see the module CODMAT). Similarly the;
% d belongs to Varlst!+. If this sum is remodelled to obtain a*(b + c);
% + d changes have to be made in the CODMAT-structure:                ;
% Now 2 sum-rows are needed and only 1 product-row. Hence the Chrow-  ;
% information of the original sum-row has to be changed and the 2 pro-;
% duct-rows have to be removed and replaced by one new row, defining  ;
% the Zstrt for a and the Chrow to find the description of b + c back.;
% In addition the column-information for all 4 columns has to be reset;
% This is a simple example. In general more complicated situations can;
% be expected. An expression like a*b + a*sin(c) + d requires 4 rows, ;
% for instance . A CODFAC-application always follows a ExtBrsea-execu-;
% tion. This implies that potential common factors, defined through *-;
% col's always have an exponent-value = 1. A common factor like a^3 is;
% always replaced by a cse (via an appl. of Expand- and Shrinkprod),  ;
% before the procedure CODFAC is applied. Hence atmost 1 exponent in a;
% column is not equal 1.                                              ;
% ------------------------------------------------------------------- ;

symbolic procedure codfac;
% ------------------------------------------------------------------- ;
% An application of the procedure CodFac results in an exhaustive all-;
% level application of the distributive law on the present structure  ;
% of the set of input-expressions, as reflected by the present version;
% of CODMAT.                                                          ;
% If any application of the distributive law proves to be possible the;
% value T is returned.This is an indication for the calling routine   ;
% OptimizeLoop that an additional application of ExtBrsea might be    ;
% profitable.                                                         ;
% If such an application is not possible the value Nil is returned.   ;
% ------------------------------------------------------------------- ;
begin scalar b,lxx;
  for y:=rowmin:(-1) do
   % ---------------------------------------------------------------- ;
   % The Zstrts of all *-columns, which are usable (because their Far-;
   % Var-field contains a Var-name), are examined by applying the pro-;
   % cedure SameFar. If this application leads to a non empty list LXX;
   % with information, needed to be able to apply the distributive law;
   % the local variable B is set T, possibly the value to be returned.;
   % B gets the initial value Nil, by declaration.                    ;
   % ---------------------------------------------------------------- ;
   if not (farvar(y)=-1 or farvar(y)=-2) and
                                opval(y) eq 'times and (lxx:=samefar y)
    then
     <<b:=t;
       foreach el in lxx do commonfac(y,el)
     >>;
  return b
end;

symbolic procedure samefar(y);
% ------------------------------------------------------------------- ;
% Y is the index of a TIMES-column. The procedure SameFar is designed ;
% to allow to find and return a list Flst consisting of pairs, formed ;
% by a father-index and a sub-Zstrt of the Zstrt(Y), consisting of Z's;
% such that Farvar(Xind Z) = Car Flst, i.e. the Xind(Z)-rows define   ;
% (composite) productterms of the same sum, which contain the variable;
% corresponding with column Y as factor in their primitive part.      ;
% ------------------------------------------------------------------- ;
begin scalar flst,s,far;
  foreach z in zstrt(y) do
   if numberp(far:=farvar xind z) and opval(far) eq 'plus
    then
     if s:=assoc(far,flst)
      then rplacd(s,inszzz(z,cdr(s)))
      else flst:=(far.inszzz(z,s)).flst;
  return
    foreach el in flst conc
    if cddr(el)
    then list(el)
    else nil
end;

symbolic procedure commonfac(y,xx);
% ------------------------------------------------------------------- ;
% Y is the index of a TIMES-column and XX an element of LXX, made with;
% SameFar(Y), i.e. a pair consisting of the index Far of a father-sum ;
% row and a sub-Zstrt,consisting of Z-elements, defining factors in   ;
% productterms of this father-sum.                                    ;
% These factors are defined by Z-elements (Y.exponent). Atmost one of ;
% these exponents is greater than 1.                                  ;
% The purpose of CommonFac is to factor out this element,i.e. to remo-;
% ve a Z-element (Y.1) from the Zstrts of the children and also its   ;
% corresponding occurrences from ZZ3 = Zstrt(Y), to combine the remai-;
% ning sum-information in a new PLUS-row, with index Nsum, and to cre-;
% ate a TIMES-row, with index Nprod, defining the product of the sum, ;
% given by the row Nsum, and the variable corresponding with column Y.;
% ZZ2 and CH2 are used to (re)structure information, by allowing to   ;
% combine the remaining portions of the child-rows.The father (with   ;
% index Far) is defined by a Zstrt (its primitive part) and by CH1 =  ;
% Chrow (its composite part). ZZ4 and CH4 are used to identify the    ;
% Zstrts of the children after removal of a (Y.1)-element and the     ;
% Chrow's,respectively.If exponent>1 in (Y.exponent) the Zstrt has to ;
% be modified to obtain ZZ4, instead of a simple removal of (Y.1) from;
% from Zstrt X.                                                       ;
% Alternatives for the structure of the such a child-row are :        ;
% -1- A combination of a non-empty Zstrt and a non-empty list Chrow   ;
%     of children.                                                    ;
% -2- An empty Zstrt, but a non-empty Chrow.                          ;
% -3- A non-empty Zstrt, but an empty Chrow.                          ;
% Special attention is required when in case -3- the Zstrt consists of;
% only 1 Z-element besides the element shared with column Y.          ;
% In case -2- similar care have to be taken when Chrow consists of 1  ;
% row index only.                                                     ;
% Remark : Since the overall intention is optimization, i.e. reduction;
% of the arithmetic complexity of a set of expressions, viewed as ru- ;
% les to perform arithmetic operations, expression parts like a*b + a ;
% are not changed into a*(b + 1). Hence a forth alternative, being an ;
% empty Zstrt and an empty Chrow is irrelevant.                       ;
% ------------------------------------------------------------------- ;
begin scalar far,ch1,ch2,ch4,chindex,zel,zeli,zz2,zz3,zz4,
                                         nsum,nprod,opv,y1,cof,x,ivalx;
  far:=car(xx);
  ch1:=chrow(far);
  zz3:=zstrt(y);
  nprod:=rowmax+1;
  nsum:=rowmax:=rowmax+2;
  % ----------------------------------------------------------------- ;
  % After some initial settings all children,accessible via the Z-el.s;
  % collected in Cdr(XX) are examined using a FOREACH_loop.           ;
  % ----------------------------------------------------------------- ;
  foreach item in cdr(xx) do
  <<x:=xind item;
    if (ivalx:=ival item)=1
     then zz4:=delyzz(y,zstrt x)
     else zz4:=inszzzr(zeli:=mkzel(y,ivalx-1),delyzz(y,zstrt x));
    ch4:=chrow(x);
    cof:=expcof(x);
    % --------------------------------------------------------------- ;
    % (Y.1) is removed from the child's Zstrt, defining a monomial,   ;
    % without the coefficient, stored in Cof.                         ;
    % --------------------------------------------------------------- ;
    if null(zz4) and (null(cdr ch4) and car(ch4))
    then
    <<% ------------------------------------------------------------- ;
      % This is the special case of possibility -2-. ZZ4 is empty and ;
      % CH4 contains only 1 index.                                    ;
      % ------------------------------------------------------------- ;
      if (opv:=opval(ch4:=car ch4)) eq 'plus and expcof(ch4)=1
      then
      <<% ----------------------------------------------------------- ;
        % The child with row-index CH4 has the form (..+..+..)^1 = ..+;
        %  ..+.. . Its definition has to be moved to the row Nsum.    ;
        % The different terms can be either primitive or composite and;
        % have all to be multiplied by Cof. Both Zstrt(CH4) - the pri-;
        % mitives - and Chrow(CH4) - the composites - have to be exa- ;
        % mined.                                                      ;
        % ----------------------------------------------------------- ;
        foreach z in zstrt(ch4) do
        <<% --------------------------------------------------------- ;
          % A new Zstrt ZZ2 is made with the primitive elements of the;
          % the different Zstrt(CH4)'s. InsZZZr guarantees summation  ;
          % of the Ival's if the Xind's are equal (see module CODMAT).;
          % ZZ2 is build using the FOREACH X loop. The Zstrt's of the ;
          % columns, which share an element with ZZ2,are also updated:;
          % The CH4-indexed elements are removed and the Nsum-indexed ;
          % elements are inserted.                                    ;
          % --------------------------------------------------------- ;
          zel:=mkzel(xind z,dm!-times(ival(z),cof));
          zz2:=inszzzr(zel,zz2);
          setzstrt(yind z,inszzz(mkzel(nsum,ival zel),
                                  delyzz(ch4,zstrt yind z)))
        >>;
        foreach ch in chrow(ch4) do
        <<% --------------------------------------------------------- ;
          % The row CH defines a child directly if Cof = 1. In all    ;
          % other cases a multiplication with Cof has to be performed.;
          % Either by changing the ExpCof field if the child is a pro-;
          % duct or by introducing a new TIMES-row.                   ;
          % --------------------------------------------------------- ;
          chindex:=ch;
          if not(!:onep cof)
           then
            if opval(ch) eq 'times
             then
              << setexpcof(ch,dm!-times(cof,expcof(ch)));
                 setfarvar(ch,nsum)
              >>
             else
              << chindex:=rowmax:=rowmax+1;
                 setrow(chindex,'times,nsum,(ch).cof,nil)
              >>
           else  setfarvar(ch,nsum);
          ch2:=chindex.ch2
        >>;
        % ----------------------------------------------------------- ;
        % The row CH4 is not longer needed in CODMAT, because its     ;
        % content is distributed over other rows.                     ;
        % ----------------------------------------------------------- ;
        clearrow(ch4);
      >>
      else
      <<% ----------------------------------------------------------- ;
        % This is still the special case -2-. (CH4) contains 1 child  ;
        % index. The leading operator of this child is not PLUS. So   ;
        % CH4 is simply added to the list of children indices CH2 and ;
        % the father index of row CH4 is changed into Nsum.           ;
        % ----------------------------------------------------------- ;
        setfarvar(ch4,nsum);
        ch2:=ch4.ch2
      >>;
      % ------------------------------------------------------------- ;
      % The row X is not longer needed in CODMAT, because its content ;
      % is distributed over other rows.                               ;
      % ------------------------------------------------------------- ;
      clearrow(x)
    >>
    else
     if null(ch4) and (null(cdr zz4) and car(zz4))
      then
      <<% ----------------------------------------------------------- ;
        % This is the special case of possibility -3-: A Zstrt ZZ4    ;
        % consisting of only one Z-element.                           ;
        % This Z-element defines just a variable if IVal(Car ZZ4) =1. ;
        % It is a power of a variable in case IVal-value > 1 holds.   ;
        % In the latter situation Nsum ought to become the new father ;
        % index of the row with index Xind Car ZZ4.In the former case ;
        % the single variable is added to the Zstrt ZZ2, before row X ;
        % can be cleared.                                             ;
        % ----------------------------------------------------------- ;
        if not(!:onep ival(car(zz4)))
         then
          << setfarvar(x,nsum);
             setzstrt(x,zz4);
             ch2:=x.ch2
          >>
         else
          << zz2:=inszzzr(find!+var(farvar(y1:=yind car zz4),nsum,
                                                            cof),zz2);
             setzstrt(y1,delyzz(x,zstrt y1));
             clearrow(x)
          >>
      >>
      else
      <<% ----------------------------------------------------------- ;
        % Now the general form of one of the 3 alternatives holds.    ;
        % Row index X is added to the list of children indices CH2    ;
        % and the new father index for row X becomes Nsum. The Zstrt  ;
        % of X is also reset. It becomes ZZ4, i.e. the previous Zstrt ;
        % after removal of (Y.1).                                     ;
        % ----------------------------------------------------------- ;
        ch2:=x.ch2;
        setfarvar(x,nsum);
        setzstrt(x,zz4)
      >>;
    % --------------------------------------------------------------- ;
    % The previous "life" of X is skipped by removing its impact from ;
    % the "history book" CODMAT.                                      ;
    % --------------------------------------------------------------- ;
    ch1:=delete(x,ch1);
    zz3:=delyzz(x,zz3);
    if ivalx>2 then zz3:=inszzz(mkzel(x,val(zeli)),zz3)
  >>;
  % ----------------------------------------------------------------- ;
  % Some final bookkeeping is needed :                                ;
  % -1- (Y.1) was deleted from the ZZ4's. Its new role, factor in the ;
  %     product,defined via the row Nprod, has still to be establish- ;
  %     ed by inserting this information in Y's Zstrt.                ;
  % ----------------------------------------------------------------- ;
   setzstrt(y,(zel:=mkzel(nprod,1)).zz3);
  % ----------------------------------------------------------------- ;
  % -2- The list of indices of children of the row with index Far     ;
  %     ought to be extended with Nprod.                              ;
  % ----------------------------------------------------------------- ;
  setchrow(far,nprod.ch1);
  % ----------------------------------------------------------------- ;
  % -3- Finally the new rows Nprod and Nsum have to be filled. How-   ;
  %     ever the :=: assignment-option might cause - otherwise non-   ;
  %     existing - problems, because simplification is skipped before ;
  %     parsing input and storing the relevant information in CODMAT. ;
  % An input expression of the form x*(a + t) + x*(a - t) can thus be ;
  % transformed - by an application of CODFAC - into the form         ;
  % x*(2*a + 0). Its Zstrt can contain an element (index  . 0), like  ;
  % the Zstrt associated with t. The latter is due to the coefficient ;
  % addition, implied by insert-operations, like InsZZZ or InsZZZr.   ;
  % Hence a test is made to discover if a Z-element Zel exists, such  ;
  % that IVal(Zel)=0. If so, its occurrence is removed from both ZZ2  ;
  % and the Zstrt of the t-column.                                    ;
  % If now Null(CH2) and Null(Cdr ZZ2) holds the PLUS-row Nsum is     ;
  % superfluous. Only 2*a*x has to be stored in Nprod. The row Nsum   ;
  % is removed when it is easily detectable, because this index is    ;
  % not used anymore and anywhere, when the above limitations are     ;
  % valid.                                                            ;
  % ----------------------------------------------------------------- ;
  foreach z in zz2 do if zeropp(ival(z))
     then << zz2:=delyzz(y1:=xind z,zz2);
             setzstrt(y1,delyzz(nsum,zstrt y1))
          >>;
  % ----------------------------------------------------------------- ;
  % Expressions like x(a-w)+x(a+w) lead to printable, but not yet to  ;
  % completely satisfactory prefixlist-representations. This problem  ;
  % is solved in the module CODPRI in the function  ConstrExp.        ;
  % ----------------------------------------------------------------- ;
  setrow(nprod,'times,far,list list nsum,list mkzel(y,val zel));
  setrow(nsum,'plus,nprod,list ch2,zz2)
 end;

endmodule;

end;
