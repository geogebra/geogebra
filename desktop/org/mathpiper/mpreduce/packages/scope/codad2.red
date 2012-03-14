module codad2;   % Facilities applied after optimization.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Authors : J.A. van Hulzen, B.J.A. Hulshof, W.N. Borst.              ;
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
% The module CODAD2 contains a number of facilities, to be applied    ;
% when the optimization process itself is finished and before  produ- ;
% cing output. This finishing touch, obtained by applying the function;
% PrepFinalplst (see the module CODCTL), covers the following one-row ;
% and/or one-column operations:                                       ;
%                                                                     ;
% PART 1 : Sum restructuring : s = (t1 + ... + tn) ^ exponent is re-  ;
%          placed by name := t1 + ... + tn; s:= name ^ exponent.      ;
%          Remark : This form allows application of an addition chain ;
%          algorithm on the exponent, as part of the print process,   ;
%          and as defined in the module CODPRI.                       ;
%                                                                     ;
% PART 2 : REMoval of REPeatedly occurring MULTiples of VARiables in  ;
%          linear (sub)expressions, which could not be replaced by a  ;
%          Breuer-search, since it requires one-column operations in  ;
%          the PLUS-part of CodMat. If such a multiple occurs atleast ;
%          twice, it is replaced by a new name. The TIMES-part of     ;
%          CodMat is consulted  if such a multiple is found to allow  ;
%          the replacement of such multiples in monomials as well. So ;
%          x = 3.a + b, y = 3.a + c, z = 3.a.b + c                    ;
%          is replaced by                                             ;
%          s = 3.a                                                    ;
%          x = s + b, y = s + c, z = s.b + c.                         ;
%                                                                     ;
% PART 3 : An UPDATE of MONOMIALS is performed. Constant multilpes of ;
%          identifiers are selected using the TIMES-part of CodMat.   ;
%          Since the PLUS-part is already checked with REMREPMULTVARS ;
%          the search is limited to the TIMES-part. Replacement by a  ;
%          new name is only effectuated if such a multiple literally  ;
%          occurs twice. So                                           ;
%          x = 3.a.b + 6.b.c, y = 3.a.c + 6.a.b                       ;
%          is replaced by                                             ;
%          s1 = 3.a, s2 = 6.b                                         ;
%          x = s1.b + s2.c, y = s1.c + s2.a.                          ;
%                                                                     ;
% PART 4 : An all level factoring out of gcd's of constant coeff.'s in;
%          (composite) sums, using the function CODGCD. For example   ;
%           sum = 9.a - 18.b + 6.sin(x) + 5.c -5.d                    ;
%          can be rewritten into                                      ;
%           sum = 3.(3.a - 6.b + 2.sin(x)) + 5.(c - d).               ;
%          But the arithmetic complexity of both representations of   ;
%          sum is equal. We therefore produce                         ;
%           sum = 9.a - 18.b + 6.sin(x) + 5.(c - d).                  ;
%          Regrouping of (composite) products demands for an identical;
%          algorithm. For instance                                    ;
%                   9 18   6                                          ;
%           prod = a b  sin (x)                                       ;
%          can be rewritten into                                      ;
%                               3                                     ;
%                    3 6   2                                          ;
%           prod = {a b sin (x)}                                      ;
%          thus reducing the required number of multiplications.      ;
%                                                                     ;
% PART 5 : A quotient-cse search. For example                         ;
%          kvarlst = ( (g1 quotient g2 g3)                            ;
%                      (g4 quotient g5 dm) )                          ;
%          matrix :  g2 = nr * a                                      ;
%                    g3 = dm * b                                      ;
%                    g5 = nr * c                                      ;
%          will be rewritten as                                       ;
%          kvarlst = ( (g7 quotient nr dm)                            ;
%                      (g1 quotient g2 b)                             ;
%                      (g4 g5) )                                      ;
%          matrix :  g2 = g7 * a                                      ;
%                    g5 = g7 * c                                      ;
% ------------------------------------------------------------------- ;

% ------------------------------------------------------------------- ;
% Global identifiers needed in this module are :                      ;
% ------------------------------------------------------------------- ;

global '(rowmin rowmax);

% ------------------------------------------------------------------- ;
% The meaning of these globals is given in the module CODMAT.         ;
% ------------------------------------------------------------------- ;

symbolic smacro procedure find!+var(var,fa,iv);
getcind(var,'varlst!+,'plus,fa,iv);

symbolic smacro procedure find!*var(var,fa,iv);
getcind(var,'varlst!*,'times,fa,iv);

symbolic procedure getcind(var,varlst,op,fa,iv);
% ------------------------------------------------------------------- ;
% REMARK : GETCIND is also defined in the module CODAD1. This copy    ;
%          allows seperate compilation.                               ;
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


% ------------------------------------------------------------------- ;
% PART 1 : SUM RESTRUCTURING                                          ;
% ------------------------------------------------------------------- ;

symbolic procedure powerofsums;
% ------------------------------------------------------------------- ;
% The CODMAT PLUS-rows are investigated, who have an ExpCof-value > 1.;
% Such rows define a sum raised to the exponent ExpCof(rowindex).     ;
% ------------------------------------------------------------------- ;
begin scalar var,z,rmax;
  rmax:=rowmax;
  for x:=0:rmax do
  if opval(x) eq 'plus and expcof(x)>1 and not(farvar(x)=-1)
  then
   <<var:=fnewsym();
     setrow(rowmax:=rowmax+1,'plus,var,list chrow x,zstrt x);
     % -------------------------------------------------------------- ;
     % A new name Var is introduced and 2 new CODMAT-rows to store the;
     % information about the new expression,in connection with the al-;
     % raedy available information. Furthermore some bookkeeping is   ;
     % required.                                                      ;
     % The new row above contains all the information about the sum,  ;
     % except its exponent.Below the second row is used to store Var ^;
     % ExpCof in the form of a Z-element in a TIMES-row.              ;
     % This row becomes the only child of the old sum-defining row.   ;
     % -------------------------------------------------------------- ;
     put(var,'rowindex,rowmax);
     foreach z in zstrt(x) do
      setzstrt(yind z,mkzel(rowmax,val z).delyzz(x,zstrt yind z));
     foreach ch in chrow(x) do setfarvar(ch,rowmax);
     setprev(x,rowmax); % Preserve ordening;
     setrow(rowmax:=rowmax+1,'times,x,list nil,
                            list(z:=mkzel(rowmin:=rowmin-1,expcof x)));
     % -------------------------------------------------------------- ;
     % The new row for the power of the sum is based on indirection to;
     % guarantee a correct functioning of the function Tchscheme.     ;
     % -------------------------------------------------------------- ;
     setrow(rowmin,'times,var,nil,list mkzel(rowmax,val z));
     % -------------------------------------------------------------- ;
     % A new column is generated, associated with the new name genera-;
     % ted for the sum.                                               ;
     % -------------------------------------------------------------- ;
     setchrow(x,list rowmax);
     put(var,'varlst!*,rowmin);
     setzstrt(x,nil);
     setexpcof(x,1)
   >>;
end;


% ------------------------------------------------------------------- ;
% PART 2 : REMoval of REPeatedly Occurring Constant MULTiples of PLUS ;
%          VARiableS.                                                 ;
% ------------------------------------------------------------------- ;

symbolic procedure remrepmultvars;
% ------------------------------------------------------------------- ;
% All PLUS-columns of CODMAT are investigated. Let Var be the variable;
% associated with thw column Y. A list P(lus)col(umn)inf(ormation) is ;
% made out of the Zstreet of column Y. Pcolinf consists of pairs of   ;
% the form  constant(k). list of pairs (rowindex.sign(constant(k))),  ;
% such that 0<constant(i)<constant(j) if i<j and also such that coef- ;
% ficient of Var in Zstreet(rowindex) is sign(k)*constant(k).         ;
% Then for each element of this list Pcolinf a corresponding list with;
% T(imes)col(umn)inf(ormation) is made. This is a list consisting of  ;
% pairs of the form (rowindex . Z-element with the same index as value;
% of its index-part and taken from the Zstreet of the column with the ;
% index Prod(uct)col(umn)i(ndex), whose Expcof-value is a multiple of ;
% the car of the element of Pcolinf, which is under consideration).   ;
% So assuming some multiples 3*A occur in some sums, which are easily ;
% retrievable using the corresponding element of Pcolinf, we also re- ;
% place parts of monomials of the same form. Hence 6*A^2*B is replaced;
% by 2*A*B*(cse-name for 3*A).This does not increase the multiplicati-;
% ve complexity. It can even decrease if some monomials of the form   ;
% 3*A*(something else) occur in the set of expressions currently being;
% investigated.                                                       ;
% ------------------------------------------------------------------- ;
begin
  scalar
     rmin,var,prodcoli,pcolinf,mmult,srows,tcolinf,rindx,nvar,z,zz,zz1;
  rmin:=rowmin;
  for y:=rmin:(-1) do
  % ----------------------------------------------------------------- ;
  % Analysis of Zstreets of the PLUS-columns, which are associated    ;
  % with variables Var.                                               ;
  % ----------------------------------------------------------------- ;
  if (not numberp(var:=farvar y)) and (var neq '!+one) and
                                                   (opval(y) eq 'plus)
  then
  <<prodcoli:=get(var,'varlst!*);
    pcolinf:=nil;
    foreach z in zstrt(y) do
    if not(!:onep dm!-abs(ival z))
    then pcolinf:=inspcvv(xind(z).(if !:minusp(ival(z)) then -1 else 1),
                          dm!-abs(ival z),pcolinf);
    % --------------------------------------------------------------- ;
    % The function InsPCvv, defined in the module CODOPT, is used to  ;
    % produce the list Pcolinf. The NIL-initialisation is necessary   ;
    % since a fresh start is required for each column under investiga-;
    % tion. The different elements of Pcolinf are used for a closer   ;
    % look.                                                           ;
    % --------------------------------------------------------------- ;
    foreach cseinfo in pcolinf do
    <<mmult:=car(cseinfo);
      srows:=cdr(cseinfo);
      tcolinf:=nil;
      if prodcoli
      then
        foreach z in zstrt(prodcoli) do
        <<rindx:=xind(z);
          if dm!-eq(dm!-abs expcof rindx,mmult)
          then tcolinf:=(rindx.z).tcolinf
        >>;
      % ------------------------------------------------------------- ;
      % The list Tcolinf is now ready.If the number of elem.s of Srows;
      % and Tcolinf together is atleast 2 the multiplicative complexi-;
      % ty is not increasing if say 3*A is replaced by cse-name.      ;
      % ------------------------------------------------------------- ;
      if length(srows)+length(tcolinf)>1
      then
       << % --------------------------------------------------------- ;
          % A new expression is made and all required bookkeeping ac- ;
          % tions are performed. So all occurrences of say 3*A are re-;
          % moved from the Zstreet of the corresponding PLUS-column, a;
          % new column to store the placeholder for this 3*A is crea- ;
          % ted and all required modifications in the affected Zstrts ;
          % are carries out.                                          ;
          % --------------------------------------------------------- ;
          z:=mkzel(y,mmult);
          nvar:=fnewsym();
          rowmax:=rowmax+1;
          setrow(rowmax,'plus,nvar,list nil,list z);
          put(nvar,'rowindex,rowmax);
          rowmin:=rowmin-1;
          zz:=nil;
          foreach rowinf in srows do
            <<rindx:=car(rowinf);
              zz:=mkzel(rindx,cdr rowinf).zz;
              setzstrt(rindx,mkzel(rowmin,val car zz).
                                              delyzz(y,zstrt rindx));
              setprev(rindx,rowmax)
            >>;
          setzstrt(y,mkzel(rowmax,val z).remzzzz(zz,zstrt y));
          setrow(rowmin,'plus,nvar,nil,zz);
          put(nvar,'varlst!+,rowmin);
          if tcolinf
           then
            << % --------------------------------------------------- ;
               % Since Tcolinf is not empty some monomials have to be;
               % modified as well.                                   ;
               % --------------------------------------------------- ;
               rowmin:=rowmin-1;
               zz1:=zz:=nil;
               foreach rowinf in tcolinf do
                 <<rindx:=car(rowinf);
                   z:=cdr(rowinf);
                   zz:=mkzel(rindx,1).zz;
                   if ival(z)>1
                    then setival(z,ival(z)-1)
                    else
                     <<zz1:=car(zz).zz1;
                       setzstrt(rindx,delyzz(prodcoli,zstrt rindx))
                     >>;
                   setzstrt(rindx,mkzel(rowmin,val car zz).
                                                       zstrt(rindx));
                   setprev(rindx,rowmax);
                   setexpcof(rindx,dm!-quotient(expcof(rindx),mmult))
                 >>;
               setzstrt(prodcoli,remzzzz(zz1,zstrt prodcoli));
               setrow(rowmin,'times,nvar,nil,zz);
               put(nvar,'varlst!*,rowmin)
            >>
         >>
      >>
  >>
end;


% ------------------------------------------------------------------- ;
% PART 3 : An UPDATE of MONOMIALS via a TIMES-columns search.         ;
% ------------------------------------------------------------------- ;

symbolic procedure updatemonomials;
% ------------------------------------------------------------------- ;
% For each column, which is associated with an identifier, a Gclst is ;
% produced. The syntax of such a list is given in PART 4. Each element;
% of such a list, is itself a list, consisting of a constant and      ;
% structural information about the occurrences of this constant. These;
% sublists are used to deside if constant multiples can be replaced by;
% new names. The decision are made by applying the function REMGCMON. ;
% ------------------------------------------------------------------- ;
for y:=rowmin:(-1) do
if not numberp(farvar y) and opval(y) eq 'times
 then foreach gcel in mkgclstmon(y) do remgcmon(gcel,y);

symbolic procedure mkgclstmon(y);
% ------------------------------------------------------------------- ;
% All monomial coefficients of the TIMES-rows sharing an element with ;
% the current TIMES-column are grouped in a Gclst if their absolute   ;
% value is atleast 2.                                                 ;
% ------------------------------------------------------------------- ;
begin scalar gclst,cof,indxsgn;
  foreach z in zstrt(y) do
   if not !:onep dm!-abs(cof:=expcof xind z)
    then
     << indxsgn:=cons(xind(z), if !:minusp cof then -1 else 1);
        gclst:=insgclst(cof,indxsgn,gclst,1)
     >>;
  return gclst
end;

symbolic procedure remgcmon(gcel,y);
% ------------------------------------------------------------------- ;
% RemGcMon is recursively applied on Gcel. Its purpose is finding re- ;
% peatedly occurring multiples of idntifiers in monomials. However 6.a;
% is not considered when 3.a proves to be a cse, simply because it    ;
% does not reduce the multiplicative complexity of the set of expres- ;
% sions being optimized.                                              ;
% The srategy employed is very similar to the techniques used in PART ;
% 4.                                                                  ;
% ------------------------------------------------------------------- ;
begin scalar x,nvar,gc,zel,zzy,zzgc,ivalz;
 if length(cadr gcel)>1
  then
   << gc:=car gcel;
      rowmin:=rowmin-1; rowmax:=rowmax+1;
      nvar:=fnewsym();
      zel:=mkzel(y,1);
      setrow(rowmax,'times,nvar,list(nil,gc),list(zel));
      put(nvar,'rowindex,rowmax);
      zzy:=mkzel(rowmax,val(zel)).zstrt(y);
      zzgc:=nil;
      foreach z in cadr(gcel) do
       << x:=car(z);
          setexpcof(x,1);
          setprev(x,rowmax);
          zel:=car(pnthxzz(x,zzy));
          if ival(zel)>1
           then
            << zzy:=inszzz(mkzel(x,ivalz:=dm!-difference(ival(zel),1)),
                                                         delyzz(x,zzy));
               setzstrt(x,inszzzr(mkzel(y,ivalz),delyzz(y,zstrt x)))
            >>
           else
            << zzy:=delyzz(x,zzy);
               setzstrt(x,delyzz(y,zstrt x))
            >>;
          zzgc:=inszzz(zel:=mkzel(x,1),zzgc);
          setzstrt(x,mkzel(rowmin,val zel).zstrt(x))
       >>;
     setzstrt(y,zzy);
     setrow(rowmin,'times,nvar,nil,zzgc);
     put(nvar,'varlst!*,rowmin)
   >>;
  if cddr(gcel) then foreach item in cddr(gcel) do remgcmon(item,y)
end;

% ------------------------------------------------------------------- ;
% PART 4 : Gcd-based expression rewriting                             ;
% ------------------------------------------------------------------- ;
% We employ a two stage strategy. We start producing a Gclst, consis- ;
% ting of row-information. If relevant, Gclst is used to rewrite the  ;
% expression (part), defined by the current row of CodMat. The Gclst- ;
% syntax is :                                                         ;
%                                                                     ;
% Gclst ::= (Gcdlst  Gcdlst  ... Gcdlst ) , n >= 1 .                  ;
%                  1       2           n                              ;
% Gcdlst ::= (G Glocations glst  ... glst ) , m >= 0 .                ;
%                              1         m                            ;
% G ::= positive integer                                              ;
% Glocations ::= (location  ... location ) , k >= 0 .                 ;
%                         1             k                             ;
% location ::= (index . coeffsign)                                    ;
% coeffsign ::= +1 | -1                                               ;
% index ::= columnindex | rowindex                                    ;
% columnindex ::= negative integer (relative value, see CodMat def.)  ;
% rowindex ::= non-negative integer (relative value, see Codmat def.) ;
% glst ::= (g Glocations)                                             ;
% g ::= positive integer                                              ;
%                                                                     ;
% Semantics : We assume G = gcd(g1,...,gm) > 1. When other domains are;
% introduced, the presumed domain is not longer Z, implying that Gcd2,;
% * and / have to be made generic, when producing Gclst and rewriting ;
% the expression using the function RemGc.                            ;
% When m = 0, i.e. no glst's occur, the absolute value of all coeffi- ;
% cients is equal to G.                                               ;
% Glocations can be an empty list,as shown in the following example : ;
%                                                                     ;
% ((3 NIL (9 ((a.1))) (18 ((b.-1))) (6 ((sin(x).1))))                 ;
%  (5 ((c.1) (d.-1))))                                                ;
%                                                                     ;
% is the Gclst, associated with                                       ;
% sum = 9.a - 18.b + 6.sin(x) + 5.c - 5.d,                            ;
% when replacing the negative, relative column-indices by a,b,c and d,;
% and the positive relative child row-index by sin(x).                ;
% This list is used for the remodelling. The Glocations list is NIL,  ;
% because sum has no coefficients equal to either 3 or -3.            ;
% ------------------------------------------------------------------- ;

symbolic procedure codgcd();
begin scalar presentrowmax;
% ------------------------------------------------------------------- ;
% For all relevant rows of CodMat we compute the Gclst, by applying   ;
% the function MkGclst. Then each item in this list, a Gcdlst, is used;
% for a reconstruction of the expression( part) defined by row X.     ;
% ------------------------------------------------------------------- ;
presentrowmax:=rowmax;
for x:=0:presentrowmax do
  if not(farvar(x)=-1)then foreach gcel in mkgclst(x) do remgc(gcel,x)
end;

symbolic procedure mkgclst(x);
% ------------------------------------------------------------------- ;
% The Gclst of row X is produced and returned.                        ;
% ------------------------------------------------------------------- ;
begin scalar gclst,iv,opv;
  foreach z in zstrt(x) do
   if not !:onep(dm!-abs(iv:=ival z))
    then
     % -------------------------------------------------------------- ;
     % The location (Yind(Z).coeffsign) is added to the glst with g = ;
     % abs(IV).                                                       ;
     % -------------------------------------------------------------- ;
     if !:minusp(iv)
      then gclst:=insgclst(dm!-minus(iv),yind(z).(-1),gclst,1)
      else gclst:=insgclst(iv,yind(z) . 1,gclst,1);
  opv:=opval(x);
  foreach ch in chrow(x) do
   if not(opval(ch)=opv) and not(!:onep dm!-abs(iv:=expcof ch))
    % --------------------------------------------------------------- ;
    % Only non *(+)-children of *(+)-parents are considered.          ;
    % --------------------------------------------------------------- ;
    then
      % ------------------------------------------------------------- ;
      % The location (CH(=rowindex of child).coeffsign) is added to   ;
      % the glst with g = abs(IV).                                    ;
      % ------------------------------------------------------------- ;
      if !:minusp(iv)
       then gclst:=insgclst(dm!-minus iv,ch.(-1),gclst,1)
       else gclst:=insgclst(iv,ch . 1,gclst,1);
  return gclst;
end;

symbolic procedure insgclst(iv,y,gclst,gc0);
% ------------------------------------------------------------------- ;
% The most recent version of Gclst is returned after being updated by ;
% adding the location Y to the glst with g = abs(IV) in Gclst, assu-  ;
% ming that G = Gc0.                                                  ;
% ------------------------------------------------------------------- ;
begin scalar gc,cgcl;
  return
    if null(gclst)
     then
      % ------------------------------------------------------------- ;
      % Start making such a list : If Y = (-1 . 1) and IV = 4 then we ;
      % get ((4 ((-1 . 1)))).                                         ;
      % ------------------------------------------------------------- ;
      list(iv.(list(y).nil))
     else
      % ------------------------------------------------------------- ;
      % Extend the Gclst.                                             ;
      % ------------------------------------------------------------- ;
      if dm!-eq(caar(gclst),iv)
       % ------------------------------------------------------------ ;
       % Add floats only to Gcdlst's of type (G Glocations).          ;
       % Then IV = G (of Gcdlst ) and Y is added to Glocations  as new;
       %                       1                              1       ;
       % location (since Cadar(Gclst) = Glocations of Gcdlst , Cddar  ;
       %                                                    1         ;
       % (Gclst) = (glst  ... glst ) and Cdr(Gclst) = (Gcdlst  ...    ;
       %                1         m                           2       ;
       % Gcdlst )).                                                   ;
       %       n                                                      ;
       % If now IV = 4 and Y =(-2 . 1) then Gclst = ((4 ((-1 . 1))))  ;
       % is extended to ((4 ((-2 . 1) (-1 . 1)))).                    ;
       % ------------------------------------------------------------ ;
       then (iv.((y.cadar(gclst)).cddar(gclst))).(cdr gclst)
       else
        if floatprop(iv) or floatprop(caar gclst) or
             (gc:=gcd2(iv,caar gclst)) <= gc0
         then
         % ---------------------------------------------------------- ;
         % IV and G  are relative prime. The elements Gcdlst , i > 1, ;
         %                                                  i         ;
         % are further investigated, if existing.                     ;
         % So if IV = 5 and Y = (-2 . 1) then Gclst = ((4 (-1 . 1)))) ;
         % is extended to ((4 ((-1 . 1))) (5 ((-2 . 1))))).           ;
         % ---------------------------------------------------------- ;
          car(gclst).insgclst(iv,y,cdr gclst,gc0)
         else
        % ----------------------------------------------------------- ;
        % Gc = gcd(IV,G ) > Gc0 (=1, initially).                      ;
        %              1                                              ;
        % ----------------------------------------------------------- ;
          if gc=caar(gclst)
           % -------------------------------------------------------- ;
           % IV > Gc = G , implying that the (IV,Y)-info has to be    ;
           %            1                                             ;
           % stored in one of the Gcdlst  lists, i > 1.               ;
           %                            i                             ;
           % So if IV=8 and Y=(-2 . 1) then Gclst = ((4 ((-1 . 1))))  ;
           % is extended to ((4 ((-1 . 1)) (8 ((-2 . 1)))).           ;
           % -------------------------------------------------------- ;
           then (append
                  (list(gc,cadar gclst),insdiff(iv,y,cddar gclst))).
                                                            (cdr gclst)
           else
            if gc=iv
            % ------------------------------------------------------- ;
            % Gc = IV < G  demands for remodelling of Gcdlst , such   ;
            %            1                                  1         ;
            % that now Gcdlst  = (Gc Etc).So if IV = 2 and Y =(-2 . 1);
            %                1                                        ;
            % then Gclst = ((4 ((-1 . 1)))) is extended to the list   ;
            % ((2 ((-2 . 1)) (4 ((-1 . 1))))).                        ;
            % ------------------------------------------------------- ;
             then << if null(cadar gclst)
                      then list(append(list(gc,list(y)),cddar gclst))
                      else if cddar(gclst) and caddar(gclst)
            % ------------------------------------------------------- ;
            %                  ^ Neccesary test for R35.              ;
            % Can't take car of cddar if cddar is NIL (a.o.t. R34)    ;
            %----------------------------------------------JB 1994----;
                        then (append(list(gc,list(y),list(caar gclst,
                                cadar gclst)),cddar gclst)).(cdr gclst)
                        else (gc.(list(y).list(car gclst))).(cdr gclst)
                  >>
             else
             % ------------------------------------------------------ ;
             % Gc < IV and Gc < G , i.e. Glocations := NIL. So if IV =;
             %                   1                 1                  ;
             % 6 and Y = (-2 . 1) then Gclst = ((4 (-1 . 1)))0 is ex- ;
             % tended to ((2 NIL (6 ((-2 . 1))) (4 ((-1 . 1))))).     ;
             % ------------------------------------------------------ ;
               (gc.(nil.append(list(iv.(list(y).nil)),
                    if cddar gclst
                     then append(list(list(caar gclst,cadar gclst)),
                                                           cddar gclst)
                     else list(list(caar gclst,cadar gclst)))))
                     .(cdr gclst)
end;

symbolic procedure insdiff(iv,y,glsts);
% ------------------------------------------------------------------- ;
% glstst is a list of glst 's, i >= 0. If IV = g , k<= i, then Y is   ;
%                         i                     k                     ;
% inserted in glocations  and else list(IV.(list(Y).NIL)) is added to ;
%                       k                                             ;
% glsts.                                                              ;
% ------------------------------------------------------------------- ;
begin scalar b,rlst;
 while glsts and (not b) do
  << if caar(glsts)=iv
      then <<rlst:=list(iv,append(list(y),cadar glsts)).rlst;
             b:=t >>
      else rlst:=car(glsts).rlst;
     glsts:=cdr(glsts)
  >>;
 return if b
         then append(reverse(rlst),glsts)
         else append(list(iv.(list(y).nil)),reverse(rlst))
end;


symbolic procedure remgc(gcel,x);
% ------------------------------------------------------------------- ;
% RemGc allows a recursive investigation of Gcel, a Gcdlst being an   ;
% element of the Gclst of row X. Therefore it returns a list of loca- ;
% tions, which can be empty as well. These locations are remodelled   ;
% into Zstrt-elements, subject to some profitability criteria, which  ;
% will be explained in the body of this function.                     ;
% Once the list of remodelled locations is ready, it is used to re-   ;
% arrange the corresponding CodMat-elements into the desired form.    ;
% ------------------------------------------------------------------- ;
begin scalar zzch,zzchl,zzr,chr,zz,ch,nsum,nprod,ns,np,opv,gc,cof,
                                                   cofloc,iv,var1,var2;
  % ----------------------------------------------------------------- ;
  % Gcel is a Gcdlst, i.e. it has the structure (G Glocations glst's).;
  % So Cddr(Gcel) = (glsts's) =(glst  ... glst ), m>= 0. A glst itself;
  %                                 1         m                       ;
  % has the structure (g Glocations), i.e. Cddr(glst) = NIL.          ;
  % Hence Gcel is either a Gcdlst or a glst. For both alternatives    ;
  % holds : Car(Gcel) = a positive integer (G or g) and Cadr(Gcel) =  ;
  % a Glocations-list, i.e. each element of Cadr(Gcel) ia a pair      ;
  % (index.coeffsign), where Car(Gcel) is the absolute value of the   ;
  % coefficient (exponent) to be associated with row X and a column-  ;
  % index or the row-index of a child, respectively.                  ;
  % If Gcel defines the structure of a monomial the description is im-;
  % proved if atleast 2 exponents are G or if the exponents have a gcd;
  %               6 6      6 9                         2 3 3          ;
  % > 1. So both a b  and a b  are restructured into (a b )  and      ;
  %     6                                                             ;
  % (ab) , respectively.                                              ;
  % If Gcel defines the structure of a sum coefficients are factored  ;
  % out (recursively), i.e. 6.a + 9.b remains unchanged and 6.a + 6.b ;
  % is restructured into 6.(a + b). The Gcel is (3 NIL (6 ((a.1)))    ;
  % (9 ((b.1)))) and (6 ((a.1) (b.1))), respectively.                 ;
  % Restructuring requires a new TIMES(PLUS)-row to store the EXPCOF  ;
  % value GC (6) and a new PLUS(TIMES)-row to store its base ab or    ;
  % factor a + b, respectively.                                       ;
  % ----------------------------------------------------------------- ;
  if ((opv:=opval(x)) eq 'times and
     (length(cadr gcel)>1 or cddr(gcel))) or
     ((opv eq 'plus) and (length(cadr gcel)>1))
  then
   <<if opv eq 'times
      then
       << nsum:=rowmax:=rowmax+1;
          var1:=fnewsym();
          put(var1,'rowindex,nsum);
          setprev(x,nsum);
          setrow(rowmin:=rowmin-1,'times,var1,nil,
                                      list(iv:=mkzel(x,gc:=car gcel)));
          setzstrt(x,inszzzr(mkzel(rowmin,val iv),zstrt x));
          put(var1,'varlst!*,rowmin);
          setrow(nsum,'times,var1,list nil,nil)
       >>
     else
       << nprod:=rowmax+1; nsum:=rowmax:=rowmax+2;
          setchrow(x,nprod.chrow(x));
          setrow(nprod,if opv eq 'plus then 'times else 'plus,x,
                                    list(list(nsum),gc:=car gcel),nil);
          setrow(nsum,opv,nprod,list nil,nil)
       >>;
     zzch:=updaterowinf(x,nsum,1,cadr gcel,zzr,chr);
     foreach y in cddr gcel do
      <<cof:=dm!-quotient(car(y),gc); cofloc:=cadr y;
        if cdr cofloc
         then
          << if opv eq 'plus
              then
               << np:=rowmax+1; ns:=rowmax:=rowmax+2;
                  setrow(np,if opv eq 'plus then 'times else 'plus,
                                          nsum,list(list(ns),cof),nil);
                  setrow(ns,opv,np,list nil,nil);
                  setchrow(nsum,np.chrow(nsum))
               >>
              else
               << ns:=rowmax:=rowmax+1;
                  var2:=fnewsym();
                  put(var2,'rowindex,ns);
                  setprev(get(var1,'rowindex),ns);
                  setrow(rowmin:=rowmin-1,'times,var2,nil,
                                             list(iv:=mkzel(nsum,cof)));
                  setzstrt(nsum,inszzzr(mkzel(rowmin,val iv),
                           zstrt nsum));
                  put(var2,'varlst!*,rowmin);
                  setrow(ns,'times,var2,list nil,nil)
               >>;
            zz:=ch:=nil;
            zzchl:=updaterowinf(x,ns,1,cofloc,zz,ch);
            setzstrt(ns,car zzchl);
            setchrow(ns,cdr zzchl)
          >>
         else
          zzch:=updaterowinf(x,nsum,cof,cofloc,car zzch,cdr zzch)
      >>;
     foreach zel in car(zzch) do setzstrt(nsum,inszzzr(zel,zstrt nsum));
     setchrow(nsum,if chrow(nsum) then append(chrow(nsum),cdr zzch)
                                  else cdr zzch)
  >>
else
 foreach item in cddr gcel do remgc(item,x)
end;


symbolic procedure updaterowinf(x,nrow,cof,infolst,zz,ch);
% ------------------------------------------------------------------- ;
% UpdateRowInf is used in the function RemGc to construct the Zstrt   ;
% ZZ and the list of children CH of row Nrow and using the Infol(i)st.;
% Infolst is a glst.                                                  ;
% ------------------------------------------------------------------- ;
begin scalar indx,iv,mz,dyz;
 foreach item in infolst do
  << indx:=car(item);
     if indx < 0
      then
       << zz:=inszzzr(iv:=mkzel(indx,dm!-times(cof,cdr(item))),zz);
          setzstrt(indx,inszzz(mkzel(nrow,val(iv)),
                                               delyzz(x,zstrt indx)));
          setzstrt(x,delyzz(indx,zstrt x))
       >>
      else
       << ch:=indx.ch;
          chdel(x,indx);
          setfarvar(indx,nrow);
          setexpcof(indx,dm!-times(cof,cdr(item)))
       >>
  >>;
 return zz.ch
 end;

% ------------------------------------------------------------------- ;
% PART 5 : QUOTIENT-CSE SEARCH                                        ;
% ------------------------------------------------------------------- ;

global '(kvarlst qlhs qrhs qlkvl);

symbolic procedure tchscheme2;
% ---
% Moves every plus-row having just one z-element to the times-scheme.
% Also copies every single child(i.e. it's the only child of its father)
% of a plus-row to its father-row.
% ---
begin
   for x:=0:rowmax do
   << removechild x;
      to!*scheme x
   >>;
end;

symbolic procedure to!*scheme x;
% ---
% Moves plus-row x, which has just one z-element, to the times-scheme.
% ---
begin scalar z,yi,exp;
   if not(numberp farvar(x)) and opval(x) eq 'plus and
         length(zstrt x)=1 and null(chrow x) then
   << z:=car zstrt(x);
      yi:=yind z;
      exp:=expcof x;
      setexpcof(x,dm!-expt(ival z,exp));
      z:=find!*var(farvar yi,x,exp.bval(z));
      setzstrt(yi,delyzz(x,zstrt yi));
      setzstrt(x,list z);
      setopval(x,'times);
   >>
end;

symbolic procedure removechild x;
% ---
% Copies the only child of plus-row x to row x.
% ---
begin scalar ch,exp,iv;
   if not(numberp farvar(x)) and opval(x) eq 'plus and
         null(zstrt x) and length(chrow x)=1 then
   << ch:=car chrow x;
      exp:=expcof x;
      foreach z in zstrt ch do
      << setzstrt(yind z,delyzz(ch,zstrt yind z));
         iv:=dm!-times(ival(z),exp);
         setzstrt(yind z,inszzz(mkzel(x,iv),zstrt yind z));
         setzstrt(x,inszzzr(mkzel(yind z,iv),zstrt x))
      >>;
      foreach chld in chrow(ch) do setfarvar(chld,x);
      setopval(x,'times);
      setexpcof(x,dm!-times(expcof ch,exp));
      setchrow(x,chrow ch);
      clearrow ch;
   >>
end;

symbolic procedure searchcsequotients;
begin
  scalar res,continuesearch;
  tchscheme2();
  res := continuesearch := searchcsequotients2();
  while continuesearch do
        continuesearch := searchcsequotients2();
  return res;
  end;

symbolic procedure searchcsequotients2;
% -------------------------------------------------------------------- ;
% Quotient-structured cse's can exist in the prefixlist, defining the
% result of an extended Breuer-search, since this search is performed
% on a set of polynomial-like (sub)-expressions, which may contain
% numerators and denominators as seperate expressions.
% So we know after optimization that neither the subset of numerators
% nor the subset of denominators have a cse in common.
% This implies that possibly occurring cse's always have the form
% (quotient numer denom), where both numer and denom are either numbers
% or identifiers.
% An example:
% The set {x:=(ab)/(cd),y:=(ae)/(cf),z:=(bg)/(dh)} contains the cse's
% s1:=a/c and s2:=b/d,
% which can lead to the new set
%  {s1:=a/c,s2:=b/d, x:=s1.s2, y:=(s1.e)/f,z:=(s2.g)/h},
% thus saving 3 *'s but adding 1 /.
% This function serves to produce such revisions when ever possible,
% and assuming that one / is equivalent to at most two *'s.
% -------------------------------------------------------------------- ;
begin
  scalar j,quotients,dmlst,dm,numerinfol,nrlst,selecteddms,
         selectednrs,quotlst,b,quots,profit,qcse,cselst,var,s;
  qlkvl:=length(kvarlst);
  qlhs:=mkvect(qlkvl); qrhs:=mkvect(qlkvl);
  j:=0;
  quotients:=nil;
  foreach item in kvarlst do
     << putv(qlhs,j:=j+1,car item);
        putv(qrhs,j,cdr item);
        if relquottest(getv(qrhs,j))
           then quotients:=cons(j,quotients);
     >>;
% ---
% quotients contains indices of relevant quotients in lhs-rhs (kvarlst)
% ---
   if quotients then
   <<
      foreach indx in quotients do
         dmlst:=insertin(dmlst,caddr getv(qrhs,indx),indx);
      dmlst:=addmatnords(dmlst);
% ---
% dmlst = ( (item.(indices to quotients containing item in denominator))
%           ... )
% ---
      selecteddms:=selectmostfreqnord(dmlst);
         if selecteddms and length(cdr selecteddms)>1
            then % at least 2 ../dm's.
              << % selecteddms = item which appears the most in
                 % denominators.
                 dm:=car selecteddms; numerinfol:=cdr selecteddms;
                 nrlst:=nil;
                 foreach indx in numerinfol do
                    nrlst:=insertin(nrlst,cadr getv(qrhs,indx),indx);
                 nrlst:=addmatnords(nrlst);
                 % ---
                 % nrlst = ((item.(indices of quotients containing item
                 %             in numerator and the selected denominator
                 %             in the denominator) ... )
                 % ---
                 if (selectednrs:=selectmostfreqnord(nrlst))
                    then if length(cdr selectednrs)>1
                            then % cse is car(selectednrs)/dm.
                       quotlst:=((car(selectednrs).dm).cdr(selectednrs))
                                           . quotlst
              >>;

      %  dmlst:=delete(selecteddms,dmlst);
      % ---
      % quotlst = (((numerator . denominator) .
      %            st of indices to quotients containing quotient)) ...)
      % i.e. list of quotients containing the cse-quotient
      % ---

      if quotlst then
      << quots:=mkvect(qlkvl);
         foreach item in quotlst do
         << profit:=qprofit(item);
         % ----------------------------------------------------------- ;
         % qprofit delivers the pair *-savings./-savings. The assoc.   ;
         % quotient, defined as pair numerator.denominator and stored  ;
         % as car of the item, will be considered as cse if profit=t.  ;
         % ----------------------------------------------------------- ;
          if ((cdr profit) geq 0) or ((car(profit)+2*cdr(profit)) geq 0)
            then   % cse-quotient is profitable
            << b:=t;
               qcse:=list('quotient,caar item,cdar item);
               if (var:=assoc(qcse,s:=get(car qcse,'kvarlst))) then
                  qcse:=cdr(var).qcse
               else
               << var:=fnewsym();
                  put(car qcse,'kvarlst,(qcse.var).s);
                  qcse:=var.qcse;
                  cselst:=qcse.cselst
               >>;
               foreach indx in cdr(item) do
                  if car(qcse) neq getv(qlhs,indx)
                     then substqcse(qcse,indx)
            >>
         >>;
         kvarlst:=nil;
         for j:=1:qlkvl do
            if getv(qlhs,j)
               then % remove cleared quotients
               kvarlst:=append(kvarlst,list(getv(qlhs,j).getv(qrhs,j)));
         % add new quotients
         kvarlst:=append(kvarlst,cselst);
      >>
   >>;
   qlkvl:=qlhs:=qrhs:=nil;
   return(b)
end$


symbolic procedure relquottest(item);
% -------------------------------------------------------------------- ;
% returns t if item is a quotient with a numerator (cadr item) and a
% denominator (caddr item), which are a product, a constant or an .    ;
% identifier i.e. , which have a relv(evant) str(ucture).              ;
% -------------------------------------------------------------------- ;
   eqcar(item,'quotient) and relvstr(cadr item) and relvstr(caddr item);

symbolic procedure relvstr(item);
% -------------------------------------------------------------------- ;
% Only those numerators or denominators are relevant which can possibly;
% contribute to cse-quotients, i.e. constants, identifiers or products ;
% -------------------------------------------------------------------- ;
begin scalar rowindx;
   return
      constp(item) or idp(item) %or
%         ((rowindx:=get(item,'rowindex)) and opval(rowindx) eq 'times)
end;

symbolic procedure addmatnords(nordlst);
% ---
% The numerators and denominators are concidered at two levels:
% 1) nords in the kvarlst and
% 2) nords in rows which are used in the kvarlst. Nordlst contains
% relevant nords from level 1.
% A row from level 1 is opened, i.e. replaced by relevant nords from
% level 2 (its z-elements) when:
%     o The row occurs only once in the union of both levels.
%     o The row is only used for this nord and is used nowhere else in
%       codmat or kvarlst.
% Otherwise the nord is unchanged.
% ---
begin scalar matnords,templst,rowindx;
   % First: find all the nords at level 2 (matnords)
   foreach nord in nordlst do
      foreach indx in cdr nord do
         if (rowindx:=get(car nord,'rowindex)) and
            opval(rowindx) eq 'times then
         << foreach z in zstrt rowindx do
               matnords:=insertin(matnords,farvar yind z,indx);
            if abs(expcof rowindx) neq 1 then
               matnords:=insertin(matnords,expcof rowindx,indx)
         >>;
   % Second: open the appropriate 1st level rows
   foreach nord in nordlst do
   << if length(cdr nord)>1 then
         foreach indx in cdr nord do
            templst:=insertin(templst,car nord,indx)
      else
         if assoc(car nord,matnords) then
            templst:=insertin(templst,car nord,cadr nord)
         else
            if (rowindx:=get(car nord,'rowindex)) and
               opval(rowindx) eq 'times and nofnordocc(car nord)=1 then
            << foreach z in zstrt rowindx do
                  templst:=insertin(templst,farvar yind z,cadr nord);
               templst:=insertin(templst,expcof rowindx,cadr nord)
            >>
   >>;
   return templst
end;

symbolic procedure nofnordocc(nord);
% ---
% Finds out howmany times nord occurs in the kvarlst and the schemes.
% ---
begin scalar nofocc;
   nofocc:=nofmatnords nord;
   for i:=1:qlkvl do
      nofocc:=nofocc+numberofocc(nord,getv(qrhs,i));
   return nofocc
end;

symbolic procedure numberofocc(var,expression);
% -------------------------------------------------------------------- ;
% The number of occurrences of Var in Expression is computed and       ;
% returned.                                                            ;
% -------------------------------------------------------------------- ;
if constp(expression) or idp(expression)
 then
  if var=expression then 1 else 0
 else
 (if cdr expression
   then numberofocc(var,cdr expression)
   else 0)
 +
 (if var=car expression
   then 1
   else
    if not atom car expression
     then numberofocc(var,car expression)
     else 0);

symbolic procedure nofmatnords nord;
begin scalar nofocc,colindx;
   nofocc:=0;
   if (colindx:=get(nord,'varlst!*)) then
      nofocc:=length zstrt colindx;
   if (colindx:=get(nord,'varlst!+)) then
      nofocc:=nofocc+length zstrt colindx;
   return nofocc
end;

symbolic procedure insertin(nordlst,item,indx);
% -------------------------------------------------------------------- ;
% Once it is known that item is a constant or an identifier it can be  ;
% stored in the nordlst list.If item is a negative number the -indx is ;
% attached to the cdr of nordlst and -item is used as recognizer.      ;
% -------------------------------------------------------------------- ;
begin scalar pr;
 return(if !:onep(dm!-abs item) then nordlst
        else
           if (pr:=assoc(item,nordlst))
           then foreach el in nordlst collect
            if car(el)=item then item.append(cdr pr,list(indx)) else el
           else append(list(item.list(indx)),nordlst))
end;

symbolic procedure selectmostfreqnord(nordlst);
% -------------------------------------------------------------------- ;
% The nordlst consists of pairs, formed by a constant or identifier as ;
% car and a list of indices of rhs's, denoting the quotients containing;
% this car.                                                            ;
% The pair with the longest indxlst is selected and returned.          ;
% -------------------------------------------------------------------- ;
begin scalar templst,temp,selectedpr,lmax;
 if nordlst
  then
   << selectedpr:=car nordlst;
      lmax:=length(cdr selectedpr);
      templst:=cdr nordlst;
      foreach pr in templst do
       << if lmax < (temp:=length(cdar templst))
           then << lmax:=temp; selectedpr:=car templst >>;
          templst:=cdr templst
       >>
   >>;
 return(selectedpr)
end;

symbolic procedure qprofit(item);
% -------------------------------------------------------------------- ;
% indxlist consists of signed indices of the vectors lhs and rhs. The  ;
% structure of the rhs's, being quotients is used to determine the     ;
% number of multiplications and divisions saved by considering the     ;
% corresponding quotient as a cse.                                     ;
% The rules we apply are straightforward. Assume the cse-candidate     ;
% is defined by s:=nr/dm. Then we can distinguish between the 4 fol-   ;
% lowing situations:                                                   ;
% -1- quotient=s,       i.e. 1 /-operation can be saved.               ;
% -2- quotient=s/a,     i.e. 1 *-operation can be saved.               ;
% -3- quotient=s*a,     i.e. 1 /-operation can be saved.               ;
% -4- quotient=(s*a)/b, i.e. 1 *-operation can de saved, but no        ;
%                              /-operation is saved.                   ;
% We simply test if dm is a constant or an identifier (1 /-saving) or a;
% product (1 *-saving).                                                ;
% But if nr is a product we still need the /-operation                 ;
% s will function as cse if nbof!/>=0 or when nbof!*+2*nbof!/>=0,      ;
% assuming that a division is atmost as costly as 2 multiplications.   ;
% We neglect for the moment the extra assignments, i.e. stores.        ;
% -------------------------------------------------------------------- ;
begin scalar nbof!*,nbof!/,tempquot,h,f,tf,il;
   il:=cdr(item);
   while il do
   << h:= car(il); il:=cdr(il); f:=h.f;
      foreach indx in il do << if indx neq h then tf:=indx.tf >>;
      if not null(tf)
       then << il:=reverse tf, tf:=nil >>
       else il:=nil
    >>;
   if length(il:=reverse f)=1
    then
     << nbof!*:=0; nbof!/:=-1 >>
    else
     << nbof!*:=0; nbof!/:=-1;
                   % nbof!* is atmost 0. nbof!/ may be negative.
        foreach sgnindx in il do
         << tempquot:=getv(qrhs,sgnindx);
                                 % The rhs-struct. is '(quotient nr dm).
            if cdar(item)=caddr(tempquot) then nbof!/:=1+nbof!/
                                          else nbof!*:=1+nbof!*;
         >>
      >>;
   return(cons(nbof!*,nbof!/))
end;

symbolic procedure substqcse(csepair,indx);
% -------------------------------------------------------------------- ;
% csepair is a pair consisting of a system generated cse name and the  ;
% struct. of a quotient-cse. If sgnindx<0 the cse parent has a minus as;
% leading operator. If minsgn the cse has also a minus as leading ope- ;
% rator. Based on this information the rhs(abs(sgnindx)) is rewritten, ;
% i.e. the cse-value is removed and replaced by the cse-name.          ;
% -------------------------------------------------------------------- ;
begin scalar var,val,dm,nr,pnr,pdm,ninrow,dinrow,expo;
   var:=car(csepair);
   val:=cdr(csepair);
   nr:=cadr val;
   dm:=caddr val;
   pnr:=cadr(getv(qrhs,indx));
   pdm:=caddr(getv(qrhs,indx));
   ninrow:=if (nr neq pnr) then get(pnr,'rowindex) else nil;
   dinrow:=if (dm neq pdm) then get(pdm,'rowindex) else nil;
   expo:=min(nordexpo(nr,pnr),nordexpo(dm,pdm));
   pnr:=remnord(nr,expo,pnr,indx);
   pnr:=insnord(var,expo,pnr,indx);
   pdm:=remnord(dm,expo,pdm,indx);
   pnr:=checknord(pnr,ninrow,indx);
   pdm:=checknord(pdm,dinrow,indx);
   % If we want to remove qlhs[indx] this should not be a protected
   % variable of some sort...
   if !:onep(pdm) and unprotected(getv(qlhs,indx))
    then << remquotient(pnr,indx); putv(qlhs,indx,nil) >>
    else putv(qrhs,indx,if !:onep(pdm)
                          then pnr else list('quotient,pnr,pdm))
end;

symbolic procedure unprotected var;
% States wether var is free to be removed or not.
flagp(var,'newsym) and not get(var,'alias);

symbolic procedure nordexpo(x,y);
% ---
% Calculates the power of x in product y.
% Assumption : y contains x.
% ---
   if constp x then
      1
   else if idp x then
      if x=y then
         1
      else
         begin scalar res;
           if (res:=assoc(get(x,'varlst!*),zstrt get(y,'rowindex)))
              then res := ival res
              else res := 0;
           return res
           end;

symbolic procedure remnord(item,expo,dest,indx);
% ---
% Divides item^expo out of dest. Dest is a constant, a variable or a
% variable determining a row in CODMAT.
% Item is a constant or a variable.
% Assumption : dest contains item^n, n >= expo.
% ---
begin scalar rowindx,colindx,z;
   return
      if constp dest then
         dm!-quotient(dest,dm!-expt(item,expo))
      else
         if item=dest then
         << remquotordr(indx,item);
            if (rowindx:=get(item,'rowindex)) then
               remquotordr(indx,rowindx);
            1
         >>
         else
         << rowindx:=get(dest,'rowindex);
            if constp(item) then
            << if opval(rowindx)='times then
                setexpcof(rowindx,dm!-quotient(expcof rowindx,
                                              dm!-expt(item,expo)))
               else <<setzstrt(rowindx,foreach z in zstrt(rowindx)
                        collect mkzel(xind z,
                         dm!-quotient(ival z,dm!-expt(item,expo))
                                               . bval(z)));
                      foreach z in zstrt(rowindx) do
                       setzstrt(yind z,inszzz(mkzel(rowindx,val z),
                                              zstrt(yind z)))
                    >>;
               dest
            >>
            else
            << colindx:=get(item,'varlst!*);
               z:=assoc(colindx,zstrt rowindx);
               setzstrt(colindx,delyzz(rowindx,zstrt colindx));
               setzstrt(rowindx,delete(z,zstrt rowindx));
               if ival(z)=expo then
               << remprev(rowindx,item);
                  if get(item,'rowindex) then
                     remprev(rowindx,get(item,'rowindex))
               >>
               else
               << setzstrt(colindx,
                           inszzz(mkzel(rowindx,(ival(z)-expo).bval(z)),
                                          zstrt colindx));
                  setzstrt(rowindx,
                          inszzzr(mkzel(colindx,(ival(z)-expo).bval(z)),
                                           zstrt rowindx))
               >>;
               dest
            >>
         >>
end;

symbolic procedure insnord(item,expo,dest,indx);
% ---
% Multiplies item^expo into dest. Dest is a constant, a variable or a
% variable determining a row in CODMAT.
% Item is a constant or a variable.
% ---
begin scalar rowindx;
   return
      if constp dest then
         if constp item then
            dm!-times(dest,dm!-expt(item,expo))
         else
         << %if (rowindx:=get(item,'rowindex)) then
            %   insquotordr(indx,rowindx)
            %else
            %   insquotordr(indx,item);
            item  % dest = 1
         >>
      else
      << rowindx:=get(dest,'rowindex);
         if constp item then
         <<setexpcof(rowindx,
                     dm!-times(expcof rowindx,dm!-expt(item,expo)));
           dest
         >>
         else
         << setzstrt(rowindx,inszzzr(mkzel(car find!*var(item,
                                                         rowindx,expo),
                                           expo),zstrt rowindx));
            if get(item,'rowindex) then
               setprev(rowindx,get(item,'rowindex))
            else
               setprev(rowindx,item);
            dest
         >>
      >>
end;

symbolic procedure insquotordr(indx,ord);
% ---
% This procedure inserts ord in all order-lists of rows containing the
% quotient indiced by indx.
% ---
begin scalar col;
   if (col:=get(getv(qlhs,indx),'varlst!+)) then
      foreach z in zstrt(col) do
         setprev(xind z,ord);
   if (col:=get(getv(qlhs,indx),'varlst!*)) then
      foreach z in zstrt(col) do
         setprev(xind z,ord)
end;

symbolic procedure remquotordr(indx,ord);
% ---
% This procedure removes ord from all order-lists of rows containing
% the quotient indiced by indx.
% ---
begin scalar col;
   if (col:=get(getv(qlhs,indx),'varlst!+)) then
      foreach z in zstrt(col) do
         remprev(xind z,ord);
   if (col:=get(getv(qlhs,indx),'varlst!*)) then
      foreach z in zstrt(col) do
         remprev(xind z,ord)
end;

symbolic procedure remprev(x,y);
% ---
% See setprev.
% ---
   if numberp(farvar x) then
      remprev(farvar x,y)
   else
      setordr(x,remordr(y,ordr x));

symbolic procedure checknord(nord,inrow,indx);
begin
   if inrow then
   << if null(zstrt inrow) and null(chrow inrow) then
      << nord:=expcof inrow;
         remquotordr(indx,inrow);
         remquotordr(indx,farvar inrow);
         clearrow(inrow)
      >>
      else insquotordr(indx,get(nord,'rowindex))
      % In inrow obviously something usefull is defined, so
      % this cse should be defined for its use.
      % This means update ordr-fields.  JB. 7-5-93.

      %else
      % if (zz:=zstrt(inrow)) and null(cdr zz) and
      %    null(chrow inrow) and
      %    !:onep(expcof inrow) and !:onep(ival car zz) then ...
      %  handled by IMPROVELAYOUT
   >>;
   return nord
end;

symbolic procedure remquotient(pnr,indx);
% pnr is a variable (row)
begin scalar var,col,rowindx;
   var:=getv(qlhs,indx);
   if (col:=get(var,'varlst!+)) then
      foreach z in zstrt col do
         remprev(xind z,var);
   if (col:=get(var,'varlst!*)) then
      foreach z in zstrt col do
         remprev(xind z,var);
   tshrinkcol(getv(qlhs,indx),pnr,'varlst!+);
   tshrinkcol(getv(qlhs,indx),pnr,'varlst!*);
   for i:=1:(qlkvl) do
      putv(qrhs,i,subst(pnr,getv(qlhs,indx),getv(qrhs,i)));
   if (rowindx:=get(pnr,'rowindex)) then
      pnr:=rowindx;
   if (col:=get(pnr,'varlst!+)) then
      foreach z in zstrt col do
         setprev(xind z,pnr);
   if (col:=get(pnr,'varlst!*)) then
      foreach z in zstrt col do
         setprev(xind z,pnr)
end;

endmodule;

end;
