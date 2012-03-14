module gstructr;  % Generalized structure routines.

% ------------------------------------------------------------------- ;
% Copyright : J.A. van Hulzen, Twente University, Dept. of Computer   ;
%             Science, P.O.Box 217, 7500 AE Enschede, the Netherlands.;
% Author  :   M.C. van Heerwaarden, J.A. van Hulzen                   ;
% ------------------------------------------------------------------- ;

symbolic$

% ------------------------------------------------------------------- ;
% This module contains an extended version of the structr facility of ;
% REDUCE.                                                             ;
%                                                                     ;
% Author of structr-routines: Anthony C. Hearn.                       ;
%                                                                     ;
% Copyright (c) 1987 The RAND Corporation. All rights reserved.       ;
%                                                                     ;
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
% This is a generalization of the STRUCTR-command. Instead of one     ;
% expression, GSTRUCTR takes as input a list of assignment statements.;
% SYNTAX:                                                             ;
%        <gstructr-command> ::= GSTRUCTR <ass-list> NAME <id>         ;
%        <ass-list>         ::= {<assignments> | <matrix>}            ;
%        <id>               ::= <name for CSE>                        ;
% As a result, all assignments are printed with substitutions for the ;
% CSE's. Then WHERE is printed, followed by the list of CSE's. These  ;
% CSE's are printed in reversed order. Matrices are treated as if     ;
% assignments were made for all matrix elements.                      ;
% When the switch FORT is ON, the statements will be in  FORTRAN execu;
% table order. Be sure PERIOD is OFF when using a matrix,since FORTRAN;
% expects integer subscripts, and REDUCE generates a floating point   ;
% representation for these subscripts when PERIOD is ON.              ;
% The switch ALGPRI can be turned OFF when the list of assignments is ;
% needed in prefix-form.                                              ;
% ------------------------------------------------------------------- ;

fluid '(countr svar !*varlis);

global '(!*algpri );
%global '(!*fort );
%global '(!*nat );
%global '(!*savestructr);

global'(varnam!*);

switch savestructr, algpri;

% loadtime(on algpri);

% ***** two essential uses of RPLACD occur in this module.

put('gstructr, 'stat, 'gstructrstat);

symbolic procedure gstructrstat;
begin
    scalar x,y;
    flag('(name), 'delim);
    if eqcar(x := xread t, 'progn)
    then x := cdr x
    else x := list x;
    if cursym!* = 'name
    then y := xread t;
    remflag('(name), 'delim);
    return list('gstructr, x, y)
end;

put('gstructr, 'formfn, 'formgstructr);

symbolic procedure formgstructr(u, vars, mode);
list('gstructr, mkquote cadr u, mkquote caddr u);

symbolic procedure gstructr(assset, name);
begin
  !*varlis := nil;
  countr := 0;
  for each ass in assset
  do if not pairp ass
     then if get(ass, 'rtype) = 'matrix
            then prepstructr(cadr get(ass,'avalue),name,ass)
           else rederr {ass, "is not a matrix"}
     else prepstructr(caddr ass, name, cadr ass);
  if !*algpri
  then print!*varlis()
  else return remredundancy(for each x in reversip!* !*varlis
              collect list('setq, cadr x, cddr x))
end;

symbolic procedure prepstructr(u, name, fvar);
   begin scalar i, j;
      %!*VARLIS is a list of elements of form:
      %(<unreplaced expression> . <newvar> . <replaced exp>);
      if name
      then svar := name
      else svar := varnam!*;
      u := aeval u;
      if flagpcar(u, 'struct)
      then << i := 0;
              u:= car u .
                   (for each row in cdr u collect
                    << i := i + 1;
                       j := 0;
                       for each column in row collect
                       << j := j + 1;
                          !*varlis := (nil .
                                       list(fvar,i,j) .
                                       prepsq prepstruct!*sq column) .
                                       !*varlis
                    >> >>
                   )
           >>
      else if getrtype u
           then typerr(u,"STRUCTR argument")
           else !*varlis:=(nil.fvar.prepsq prepstruct!*sq u).!*varlis
   end;

symbolic procedure print!*varlis;
begin
  if !*fort
  then !*varlis := reversip!* !*varlis;
  if not !*fort
  then << for each x in reverse !*varlis
          do if null car x
             then << assgnpri(cddr x,list cadr x,t);
                     if not flagpcar(cddr x,'struct) then terpri();
                     if null !*nat then terpri()
                  >>;
          if countr=0 then return nil;
          prin2t "   where"
       >>;
  for each x in !*varlis
  do if !*fort or car x
     then <<terpri!* t;
            if null !*fort then prin2!* "      ";
            assgnpri(cddr x,list cadr x,t)
          >>;
  if !*savestructr
    then <<if arrayp svar
           then <<put(svar,'array,
                      %  mkarray(list(countr+1),'algebraic));
                        mkarray1(list(countr+1),'algebraic));
                  put(svar,'dimension,list(countr+1))>>;
           for each x in !*varlis do
              if car x then setk2(cadr x,mk!*sq !*k2q car x)>>
end;

symbolic procedure prepstruct!*sq u;
if eqcar(u,'!*sq)
   then prepstructf numr cadr u ./ prepstructf denr cadr u
   else u;

symbolic procedure prepstructf u;
if null u
then nil
else if domainp u
     then u
     else begin
            scalar x,y;
            x := mvar u;
            if sfp x
            then if y := assoc(x,!*varlis)
                 then x:=cadr y
                 else x:=prepstructk(prepsq!*(prepstructf x ./ 1),
                                     prepstructvar(),x)
            else if not atom x and not atomlis cdr x
                 then if y := assoc(x,!*varlis)
                      then x := cadr y
                      else x := prepstructk(x,prepstructvar(),x);
            return x .** ldeg u .* prepstructf lc u .+ prepstructf red u
          end;

symbolic procedure prepstructk(u,id,v);
begin
  scalar x;
  if x := prepsubchk1(u,!*varlis,id)
     then rplacd(x,(v . id . u) . cdr x)
     else if x := prepsubchk2(u,!*varlis)
             then !*varlis := (v . id . x) . !*varlis
             else !*varlis := (v . id . u) . !*varlis;
  return id
end;

symbolic procedure prepsubchk1(u,v,id);
   begin scalar w;
      while v do
       <<smember(u,cddar v)
            and <<w := v; rplacd(cdar v,subst(id,u,cddar v))>>;
         v := cdr v>>;
      return w
   end;

symbolic procedure prepsubchk2(u,v);
   begin scalar bool;
      for each x in v do
       smember(cddr x,u)
          and <<bool := t; u := subst(cadr x,cddr x,u)>>;
      if bool then return u else return nil
   end;

symbolic procedure prepstructvar;
   begin
      countr := countr + 1;
      return if arrayp svar then list(svar,countr)
       else compress append(explode svar,explode countr)
   end;

symbolic procedure remredundancy setqlist;
% -------------------------------------------------------------------- ;
% This function is used for backsubstitution of values of identifiers  ;
% in rhs's if the corresponding identifier occurs only once in the set ;
% of rhs's. SetqList is thus made shorter if possible.                 ;
% An element of Setqlist has the form (SETQ assname value), where      ;
% assname can be redundant if                                          ;
% Atom(assname) and Letterpart(assname) = svar                         ;
% -------------------------------------------------------------------- ;
begin scalar lsl,lhs,rhs,relevant,j,var,freq,k,firstocc,templist;
 lsl:=length(setqlist);
 lhs:=mkvect(lsl); rhs:=mkvect(lsl); relevant:=mkvect(lsl);
 j:=0; var:=explode(svar);
 foreach item in setqlist do
  <<putv(lhs,j:=j+1,cadr item); putv(rhs,j,caddr item);
    if atom(cadr item ) and letterparts(cadr item) = var
     then putv(relevant,j,t)
  >>;
 for j:=1:lsl do
  if getv(relevant,j)
   then
    << var:=getv(lhs,j); freq:=0; k:=j; firstocc:=0;
       while freq=0 and k<lsl do
       << if (freq:=numberofoccs(var,getv(rhs,k:=k+1)))=1 and firstocc=0
           then <<firstocc:=k; freq:=0>>;
          if firstocc>0 and freq>0 then firstocc:=0
       >>;
       if firstocc=0
        then templist:=list('setq,getv(lhs,j),getv(rhs,j)) . templist
        else  putv(rhs,firstocc,
                            subst(getv(rhs,j),var,getv(rhs,firstocc)))
    >>
    else templist:=list('setq,getv(lhs,j),getv(rhs,j)) . templist;
 return reverse(templist);
end;

symbolic procedure letterparts(name);
   % ----------------------------------------------------------------- ;
   % Eff: The exploded form of the Letterpart of Name returned, i.e.   ;
   % (!a !a) if Name=aa55.                                             ;
   % ----------------------------------------------------------------- ;
   begin scalar letters;
       letters:=reversip explode name;
       while digit car letters do letters:=cdr letters;
       return reversip letters
   end;

symbolic procedure numberofoccs(var,expression);
% -------------------------------------------------------------------- ;
% The number of occurrences of Var in Expression is computed and       ;
% returned.                                                            ;
% -------------------------------------------------------------------- ;
if atom(expression)
 then
  if var=expression then 1 else 0
 else
 (if cdr expression
   then numberofoccs(var,cdr expression)
   else 0)
 +
 (if var=car expression
   then 1
   else
    if not atom car expression
     then numberofoccs(var,car expression)
     else 0);



%-----------------------------------------------------------------------
% Algebraic mode psop-function definition.
%-----------------------------------------------------------------------

symbolic procedure algstructreval u;
% -------------------------------------------------------------------- ;
% Variant of gstructr-command. Accepts list of equations and optionally
% an initial part of a subpart recognizer name.
% -------------------------------------------------------------------- ;
begin scalar algpri,name,period,res; integer nargs;
 nargs:=length u;
 name:= (if nargs=1 and getd('newsym) then fnewsym()
         else if nargs=2 then cadr u
         else '!*!*error!*!*);
 if eq(name,'!*!*error!*!*)
  then rederr("WRONG NUMBER OF ARGUMENTS ALGSTRUCTR")
  else << algpri:=!*algpri; period:=!*period; !*algpri:=!*period:=nil;
          res:=apply('gstructr,list(cdar u,name));
          !*period:=period;
          if (!*algpri:=algpri)
           then return
             algresults1(foreach el in res
                            collect cons(cadr el,caddr el))
           else return res
       >>
end;

put('algstructr,'psopfn,'algstructreval)$

endmodule;
end;

