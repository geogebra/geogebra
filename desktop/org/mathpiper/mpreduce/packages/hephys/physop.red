module physop;

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                  %
%                      P H Y S O P                                 %
%                                                                  %
%           A Package for Operator Calculus                        %
%                      in Physics                                  %
%                                                                  %
%  Author:            Mathias Warns                                %
%                   Physics Institute                              %
%                   University of Bonn                             %
%                     Nussallee 12                                 %
%                 D-5300 BONN 1 (F.R.G.)                           %
%                <UNP008@DBNRHRZ1.bitnet>                          %
%                                                                  %
%  Version:           1.5  06 Jan 1992                             %
%                                                                  %
% Designed for: REDUCE version 3.4                                 %
% Tested on   : - Intel 386/486  AT compatible computers           %
%                  PSL implementation of REDUCE 3.4                %
%               - IBM 3084/9000-620 MVS/XA                         %
%                  PSL implementation of REDUCE 3.4                %
%                                                                  %
% CAUTION: (i)  The NONCOM2 package is needed to run this package  %
%          (ii) This package cannot be used simultaneously with    %
%               packages modifying the standard GETRTYPE procedure %
%                                                                  %
%             Copyright (c) Mathias Warns 1990 - 1992              %
%                                                                  %
% This file has been re-released under the BSD license by          %
% A C Hearn under powers granted to him by the original author     %
% when this package was contributed for use in a commercial        %
% edition of Reduce.                                               %
%                                                                  %
%                                                                  %
%      *** Revision history since issue of Version 0.99 ***        %
%                                                                  %
% - sloppy use of CAR on atoms corrected in various procedures     %
% - MUL and TSTACK added in PHYSOPTIMES                            %
% - Bug in CLEARPHYSOP corrected                                   %
% - ordering procedures recoded for greater  efficiency            %
% - handling of PROG expressions included via                      %
%     procedure PHYSOPPROG                                         %
% - procedures PHYSOPTIMES and MULTOPOP!* modified                 %
% - extended error handling inclued  via REDERR2                   %
% - PHYSOPTYPELET  recoded                                         %
% - PHYSOPCONTRACT modified for new pattern natcher                %
% - EQ changed to = in MULTF and MULTFNC                           %
% - PHYSOPCOMMUTE/PHYSOPANTICOMMUTE  and COMM2 corrected           %
% - Handling of SUB and output printing adapted to 3.4             %
%                                                                  %
% 1.1 130791 mw                                                    %
% - Modifications for greater efficiency in procedures ORDOP,      %
%   ISANINDEX and ISAVARINDEX                                      %
% - PHYSOP2SQ slightly modified for greater efficiency             %
% - Procedure  COLLECTPHYSTYPE added                               %
% - handling of inverse and adjoint operators modified             %
%   procedures INV and ADJ2  modified                              %
%   procedures INVP and ADJP recoded                               %
% - procedures GETPHYSTYPE!*SQ and GETPHYSTYPESF added for greater %
%   efficiency in type checking of !*SQ expressions                %
% - procedure GETPHYTYPE modified accordingly                      %
% - SIMP!* changed to SUBS2 in procedure PHYSOPSUBS                %
% - Bug in EXPTEXPAND and PHYSOPEXPT corrected                     %
% - PHYSOPORDCHK and PHYSOPSIMP slightly enhanced                  %
% - PHYSOPTYPELET enhanced  (COND treatment)                       %
% - phystypefn for PLUS and DIFFERENCE changed to GETPHYSTYPEALL   %
% - GETPHYSTYPEALL  added                                          %
% - GETPHYSTYPETIMES modified                                      %
% 1.2 190891 mw                                                    %
% - implementation of property PHYSOPNAME for PHYSOPs              %
% - procedures SCALOP,VECOP,TENSOP,STATE,INV,ADJ2,INVADJ modified  %
% - procedure ORDOP recoded, NCMPCHK and PHYSOPMEMBER modified     %
% - procedure PHYSOPSM!* enhanced                                  %
% 1.3  test implementation of a new ordering scheme 260891 mw      %
% - Procedure OPNUM!* and RESET!_OPNUMS added                      %
% - procedure ORDOP recoded                                        %
% - procedure SCALOP,VECOP,TENSOP,STATE,OPORDER modified           %
% - procedure !*XADD added                                         %
% - procedure PHYSOPSIMP corrected                                 %
% 1.4 181291 mw                                                    %
% - bug in procedures SCALOPP, PHYSOPSIMP and TENSOP corrected     %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

create!-package('(physop),'(contrib physics));

%-------------------------------------------------------%
% This part has to be modified by the user if required  %
%-------------------------------------------------------%
% input the NONCOM2 package here  for a compiled version
% input noncom2;

load!-package 'noncom2;

% Modify the infix character for the OPAPPLY function if needed

newtok'((|) opapply);
flag('(opapply), 'spaced);

%-------------------------------------------------------%
% E N D of user modifiable part                         %
%-------------------------------------------------------%


%****************  the following is needed for REDUCE 3.4 *************
fluid '(!*nosq);  % controls proper use of !*q2a
!*nosq := t;
% **************  end of 3.4 modifications **************************

fluid '(frlis!* obrkp!*);

newtok '((d o t) dot);
flag ('(dot), 'spaced);

% define some global variables from REDUCE needed in the package
fluid '(alglist!* subfg!* wtl!*);
Global '(tstack!* mul!*);
% ---define global variables needed for the package---
FLuid '(oporder!* defoporder!* physopindices!* physopvarind!*);
Fluid '(physoplist!*);
Global '(specoplist!*);
% define global flags
fluid '(!*anticom !*anticommchk !*contract !*contract2 !*hardstop);
fluid '(!*indflg indcnt!* !*ncmp ncmp!*);
indcnt!* := 0;
% additional flag needed for contraction
 !*contract2 := nil;
% flag indicating that one elementary comm or opapply has not
% been found --> print warning message
 !*hardstop := nil;

% this are algebraic mode switches
switch contract;
switch anticom;

% reserved operators and variables;
% idx is the basic identifier for system created indices
Global '(idx);


% ----- link new data type PHYSOP in REDUCE ------

% physop is the new datatype containing all subtypes
put('physop,'name,'physop);     %datatype name
put('physop,'evfn,'!*physopsm!*);  % basic simplification routine
put('physop,'typeletfn,'physoptypelet); % routine for type assignements

% Note: we need to make gamma a regular id.

remprop('gamma,'simpfn);  remflag('(gamma),'full);

% ----RLISP procedures which have been modified -----
% procedure for extended error handling
symbolic procedure rederr2(u,v);
begin
msgpri("Error in procedure ",u, ": ", nil,nil);
rederr v
end ;

% procedures multf and multfnc have to be redefined to avoid
% contraction of terms after exptexpand

% The following is an updated version by E.S. and is similiar to what we have
% implemented in the sstools package.

symbolic procedure multfnc(u,v);
   % Returns canonical product of U and V, with both main vars non-
   % commutative.
   begin scalar x,y;
      x := multf(lc u,!*t2f lt v);
      if null x then nil
       else if not domainp x and mvar x eq mvar u and 
%               ((not noncomp2 mvar x) or !*contract2)
                ((not physopp mvar x) or !*contract2)
        then x := addf(if null (y := mkspm(mvar u,ldeg u+ldeg x))
                         then nil
                        else if y = 1 then lc x
                        else !*t2f(y .* lc x),
                       multf(!*p2f lpow u,red x))
       else if noncomp2 mvar u 
               then if null noncommuting(mvar u,mvar x) and null ordop(mvar u,mvar x)
                       then if null(y := multf(!*p2f lpow u,lc x))
                              then x := multf(!*p2f lpow u,red x)
                             else x := addf(!*t2f(lpow x .* y),
                                            multf(!*p2f lpow u,red x))
                     else x := !*t2f(lpow u .* x)
       else x := multf(!*p2f lpow u,x) where !*!*processed=t;
      return addf(x,addf(multf(red u,v),multf(!*t2f lt u,red v)))
   end;

%% This function could be further cleaned up. E.S.

symbolic procedure multf(u,v); % changed
   %U and V are standard forms.
   %Value is standard form for U*V;
   begin scalar x,y;
    a:  if null u or null v then return nil
         else if u=1 then return v     % ONEP
         else if v=1 then return u     % ONEP
         else if domainp u then return multd(u,v)
         else if domainp v then return multd(v,u)
         else if not(!*exp or ncmp!* or wtl!* or x)
          then <<u := mkprod u; v := mkprod v; x := t; go to a>>;
        x := mvar u;
        y := mvar v;
%       if (ncmp := noncomp y) and noncomp x then return multfnc(u,v)
        if noncomp2f v and (noncomp2 x or null !*!*processed) then return multfnc(u,v)
%       if noncommuting(x,y) and null !*!*processed then return multfnc(u,v)
%    we have to put this clause here to prevent evaluation in case
%    of equal main vars
        else if %noncommutingf(y, lc u) or  %noncommutingf shouldn't be necessary.
                (ordop(x,y) and (x neq y))
          then << x := multf(lc u,v);
                 y := multf(red u,v);
                 return if null x then y else lpow u .* x .+ y>>
         else if x = y and (not physopp x or !*contract2)
         % two forms have the same mvars
         % switch contract added here to inhibit power contraction
         % if not wanted (used by PHYSOP)
          then << x := mkspm(x,ldeg u+ldeg v);
          y := addf(multf(red u,v),multf(!*t2f lt u,red v));
                 return if null x or null(u := multf(lc u,lc v))
                    then <<!*asymp!* := t; y>>
                   else if x=1 then addf(u,y)
                   else if null !*mcd then addf(!*t2f(x .* u),y)
                   else x .* u .+ y>>;
        x := multf(u,lc v);
        y := multf(u,red v);
       return if null x then y else lpow v .* x .+ y
   end;

%symbolic procedure noncomp2f u;
% if domainp u then nil
%  else noncomp2 mvar u or noncomp2f lc u or noncomp2f red u;

symbolic procedure noncomp2f u;
% I found an example where this was absolutely a hot-spot: so I have
% transcribed from noncom2 to put that in-line here and I have explicitly
% turned the recursion into an interation in the CDR direction...
  begin
    scalar v;
top:if domainp u then return nil
    else if atom (v := mvar u) then <<
      if flagp(v, 'noncom) then return t >>
    else if flagp(car v, 'noncom) then return t
    else if noncomp2f lc u then return t;
    u := red u;
    go to top
  end; 

symbolic procedure opmtch!* u;
% same as opmtch but turns subfg!* on
begin scalar x,flg;
flg:= subfg!*; subfg!* := t;x:= opmtch u; subfg!* := flg;
return x
end;

symbolic procedure reval3 u;
% this is just a redefinition of reval2(u,nil)
% which call simp instead of simp!*
% it saves at lot of writing in some procedures
mk!*sq x where x := simp u;

%  ---- procedure related to ordering of physops in epxr -------

symbolic procedure oporder u;
% define a new oporder list
begin
if not listp u then rederr2('oporder, "invalid argument to oporder");
if (u = '(nil)) then oporder!* := defoporder!*  % default list
else for each x in reverse u do <<
         if not physopp x then rederr2('oporder,
         list(x," is not a PHYSOP"));
         oporder!* := nconc(list(x),physopdelete(x,oporder!*)) >>; %1.01
% write "oporder!* set to: ",oporder!*;terpri();
         reset!_opnums(); %1.03
rmsubs()
end;
rlistat '(oporder);

symbolic procedure physopdelete(u,v);
% u is a physop, v is a list of physops
% deletes u from v
  if atom u then delete(u,v)
  else delete(u,delete(car u,delete(removeindices(u,collectindices u),v)));

symbolic procedure opnum!* u; % new 1.03
  begin
    scalar op,arglist, opnums;
    if not idp u then u := removeindices(u,collectindices u);
    if idp u then op := u
    else << op := car u; arglist := cdr u;>>;
    opnums := get(op, 'opnum);
    if u:= assoc(arglist, opnums) then return cdr u
    else return cdr assoc(nil, opnums)
end;

symbolic procedure reset!_opnums();
begin scalar x,lst,n,op,arglist;
lst := oporder!*;
n := 1;
a: if null lst then return;
   x := car lst;   lst := cdr lst;
   if idp x then <<op := x; arglist := nil>>
   else <<op := car x; arglist := cdr x>>;
  put(op,'opnum,!*xadd((arglist . n),get(op,'opnum)));
  n:= n+1;
  go to a
end;

symbolic procedure !*xadd(u,v); % new 1.03
% u is assignement , v is a table
% returns updated table
begin scalar x;
x := v;
while x and not (car u = caar x) do x := cdr x;
if x then v := delete(car x,v);
v := u . v;
return v
end;


symbolic procedure ordop(u,v);
% recoded ordering procedure of operators
% checks new list oporder!*  for physops or calls ordop2
% default is to put anything ahead of physops
% we use !*physopp instead of physopp in order to use
% ordop even if we hide the physop rtype
begin scalar x,y,z,nx,ny;
% this are the trivial cases
if not (!*physopp u and !*physopp v) then return
       if !*physopp u then nil
       else if !*physopp v then t
            else ordop2(u,v);
% now come the cases with 2 physops
% following section modified 1.02
if idp u then x:= get(u,'physopname)
else
<<
   x:=get(car u,'physopname);
   x:= x . cdr u;
   u := car u;
>>;
if  member(u,specoplist!*) then return t;
if idp v then y:= get(v,'physopname)
else
<<
   y:= get(car v, 'physopname);
   y := y . cdr v;
   v := car v;
>>;
if member(v,specoplist!*) then return t;
% end of modifications 1.02
% from here it is 1.03
nx := opnum!* x;
ny := opnum!* y;
return
if nx < ny then t
else if nx > ny then nil
     else if idp x then t
          else if idp y then nil
               else ordop(cdr x, cdr y);
end;

symbolic procedure ordop2(u,v);
% this is nothing but the standard ordop procedure
   begin scalar x;
        x := kord!*;
    a:  if null x then return ordp(u,v)
         else if u eq car x then return t
         else if v eq car x then return;
        x := cdr x;
        go to a
   end;

% obsolete in 1.03
%symbolic procedure physopmember(u,v); % 1.02 order modified
% u is a physop, v is a list
% return part of v starting with u
%member(u,v) or ((not atom u) and (member(car u,v)
% or member(removeindices(u,collectindices u),v)));


symbolic procedure physopordchk(u,v);  % new version 080591
% u and v are physopexpr
% builds up a list of physops of u and v
% checks if there is a pair of wrong ordered noncommuting operators
% in these lists
  begin
    scalar x,y,z,oplist,lst;
    x := deletemult!* !*collectphysops u;  %1.01
    y := deletemult!* !*collectphysops v; % 1.01
    return
      if null x then t
      else if null y then nil
      else if member('unit,x) or member('unit,y) then nil %further eval needed
      else physopordchk!*(x,y);
  end;

symbolic procedure ncmpchk(x,y); % order changed 1.02
% x and y are physops
% checks for correct ordering in noncommuting case
  (not noncommuting(x,y)) or ordop(x,y);

symbolic procedure  physopordchk!*(u,v);
% u and v are lists of physops
% checks if there is a pair of wrong ordered noncommuting operators
% in this list
begin scalar x,y,lst;
x:= car u;  u := cdr U;
if null u then
    if null cdr v then
        return (ncmpchk(x,car v) and not (invp x = car v))
    else
         <<
            lst := for each y in v collect ncmpchk(x,y);
            if member(nil,lst) then return nil
            else return t
         >>
else return (physopordchk!*(list(x),v) and physopordchk!*(u,v));
end;

% ---general testing functions for PHYSOP expressions----

symbolic procedure physopp u;
if atom u then (idp u and (get(u,'rtype) eq 'physop))
else (idp car u and (get(car u,'rtype) eq 'physop));

% slightly more general

symbolic procedure !*physopp u;
% used to determine physops when physop rtype is hidden
  if atom u then (idp u and get(u,'phystype))
  else (idp car u and get(car u,'phystype));


symbolic procedure physopp!* u;
  physopp u or
  (not atom u and
    (flagp(car u,'physopfn) or
    (flagp(car u,'physoparith) and hasonephysop cdr u) or
    (flagp(car u,'physopmapping) and hasonephysop cdr u)));

symbolic procedure !*physopp!* u;
  physopp!* u or getphystype u;

symbolic  procedure hasonephysop u;
if null u then nil
else (physopp!* car u) or hasonephysop cdr u;

symbolic  procedure areallphysops u;
  if null u then nil
  else if null cdr u then !*physopp!* car u
  else (!*physopp!* car u) and areallphysops cdr u;

% *****defining functions for different data subtypes******

% scalar operator
symbolic procedure scalop u;
begin scalar y;
for each x in u do
if not idp x then
  msgpri("cannot declare",x,"a scalar operator",nil,nil)
else if physopp x then
  msgpri(x,"already declared as",get(x,'phystype),nil,nil)
else <<y :=gettype x;
       if y memq '(matrix operator array procedure) then
          msgpri(x,"already defined as",y,nil,nil)
       else <<
              put(x,'rtype,'physop);
              put(x,'phystype,'scalar);
              put(x,'psimpfn,'physopsimp);
              put(x,'physopname,x); % 1.02
              defoporder!* := nconc(defoporder!*,list(x));
              oporder!* := nconc(oporder!*,list(x));
              physoplist!* := nconc(physoplist!*,list(x));
              invphysop x; adj2 x; invadj x; %1.01
              reset!_opnums();   %1.03
            >>;
     >>;
return nil
end;

symbolic procedure scalopp u;
(idp u  and get(u,'phystype) = 'scalar)  or (not atom u and (
(get(car u,'phystype) = 'scalar) or
((get(car u,'phystype) = 'vector) and isanindex cadr u)
or ((get(car u,'phystype) = 'tensor)  and
     (length(cdr u)  >= get(car u,'tensdimen)) and
 areallindices(for k:=1 :get(car u,'tensdimen) collect nth(cdr u,k)))));

symbolic procedure vecop u;
begin scalar y;
for each x in u do
if not idp x then
   msgpri("cannot declare",x,"a vector operator",nil,nil)
else if physopp x then
   msgpri(x,"already declared as",get(x,'phystype),nil,nil)
else <<y :=gettype x;
       if y memq '(matrix operator array procedure) then
          msgpri(x,"already defined as",y,nil,nil)
       else <<put(x,'rtype,'physop);
              put(x,'phystype,'vector);
              put(x,'psimpfn,'physopsimp);
              put(x,'physopname,x); % 1.02
              defoporder!* := nconc(defoporder!*,list(x));
              oporder!* := nconc(oporder!*,list(x));
              physoplist!* := nconc(physoplist!*,list(x));
              invphysop x; adj2 x; invadj x; %1.01
              reset!_opnums();
            >>;
     >>;
return nil
end;

symbolic  procedure vecopp u;
(idp u and (get(u,'phystype) = 'vector)) or (not atom u and
((get(car u,'phystype) ='vector) and not isanindex cadr u));

symbolic procedure tensop u;
begin scalar y,n;
% write "car u=",car u;terpri();
for each x in u do
<<
  if idp x  or not numberp cadr x then
    msgpri("Tensor operator",x,"declared without dimension",nil,nil)
  else
  <<
     n:= cadr x; x:= car x;
     if not idp x then
          msgpri("cannot declare",x,"a tensor operator",nil,nil)
     else if physopp x then
          msgpri(x,"already declared as",get(x,'phystype),nil,nil)
     else
     <<
        y :=gettype x;
        if y memq '(matrix operator array procedure) then
            msgpri(x,"already defined as",y,nil,nil)
        else
        <<
            put(x,'rtype,'physop);
            put(x,'phystype,'tensor);
            put(x,'psimpfn,'physopsimp);
            put(x,'physopname,x); % 1.02
            put(x,'tensdimen,n);
            defoporder!* := nconc(defoporder!*,list(x));
            oporder!* := nconc(oporder!*,list(x));
            physoplist!* := nconc(physoplist!*,list(x));
            invphysop x; adj2 x; invadj x; %1.01
            reset!_opnums();
        >>
     >>
  >>
>>;
return nil
end;

symbolic  procedure tensopp u;
(idp u and (get(u,'phystype) = 'tensor)) or (not atom u and
((get(car u,'phystype) ='tensor) and not isanindex cadr u));

symbolic procedure state u;
begin scalar y;
for each x in u do
if not idp x then msgpri("cannot declare",x,"a state",nil,nil)
else if physopp x then
        msgpri(x,"already declared as",get(x,'phystype),nil,nil)
else <<y :=gettype x;
       if y memq '(matrix operator array procedure) then
          msgpri(x,"already defined as",y,nil,nil)
       else <<put(x,'rtype,'physop);
              put(x,'phystype,'state);
              put(x,'psimpfn,'physopsimp);
              put(x,'physopname,x); % 1.02
              defoporder!* := nconc(defoporder!*,list(x));
              oporder!* := nconc(oporder!*,list(x));
              physoplist!* := nconc(physoplist!*,list(x));
              adj2 x;
              reset_opnums(); % 1.03
            >>
     >>;
return nil
end;

symbolic  procedure statep u;
(idp u and get(u,'phystype) = 'state) or (not atom u and
   (idp car u and get(car u,'phystype) = 'state));

symbolic  procedure statep!* u;
% slightly more general since state may be hidden in another operator
(getphystype u = 'state);


% some procedures for vecop and tensop indices

symbolic procedure physindex u;
begin scalar y;
for each x in u do <<
if not idp x then msgpri("cannot declare",x,"an index",nil,nil)
else if physopp x then
        msgpri(x,"already declared as",get(x,'phystype),nil,nil)
else <<y :=gettype x;
       if y memq '(matrix operator array procedure) then
          msgpri(y,"already defined as",y,nil,nil)
       else putanewindex x >>
     >>;
return nil
end;

symbolic procedure physindexp u;
% boolean function to test if an id is a physindex
% in algebraic mode
if idp u and  isanindex u then t
else if idlistp u and areallindices u then t
else nil;

flag ('(physindexp),'opfn);
flag ('(physindexp),'boolean);

deflist('((scalop rlis) (vecop rlis) (tensop  rlis)
          (state rlis) (physindex rlis)),'stat);


symbolic procedure isanindex u;  %recoded 1.01
idp u and (memq(u,physopindices!*) or member(u,physopvarind!*)
           or (memq(u,frlis!*) and member(revassoc(u,frasc!*),
               physopindices!*)));

symbolic  procedure isavarindex u; % recoded 1.01
member(u,physopvarind!*);


symbolic  procedure areallindices u;
isanindex car u and (null cdr u or areallindices cdr u);


symbolic procedure putanewindex u;
% makes a new index available to the system
begin scalar indx;
indx := u;
if isanindex indx then nil
else if (not atom indx)  or getrtype indx then
             rederr2('putanewindex,list(indx,"is not an  index"))
     else physopindices!* := nconc(physopindices!*,list(indx));
return nil
end;

symbolic procedure putanewindex!* u;
% used by ISANINDEX to recognize unresolved IDXn indices
begin scalar x;
if not idp u then return;
x:= explode u;
if length(x) < 4 then return;
x := for j:= 1 : 3 collect nth(x,j);
if not(x='(I D X ) or x='(!i !d !x)) then return; % check both cases.
physopindices!* := nconc(physopindices!*,list(u));
return t
end;

symbolic procedure makeanewindex();
% generates a new index
begin scalar x,n;
n:=0;
a: n:=n+1;
  x:= mkid('idx,n);
  if isanindex x then go to a
  else putanewindex x;
return x
end;

symbolic procedure makeanewvarindex();
% generates a new variable index
% for patterm matching
% physopvarind!* keeps var indices to avoid inflation
begin scalar x,y,n;
n:=0;
   y:= makeanewindex();
   x := intern compress append(explode '!=,explode y);
   nconc(frlis!*,list(x));
   physopvarind!*:= nconc(physopvarind!*,list(x));
   frasc!* := nconc(frasc!*,list((y . x)));
return x
end;

symbolic procedure getvarindex n;
begin scalar ilen;
if not numberp n then rederr2 ('getvarindex,
"invalid argument to getvarindex");
ilen := length(physopvarind!*);
return
if n > ilen then makeanewvarindex()
else nth(physopvarind!*,n);
end;

symbolic procedure transformvarindex u;
% u is a free index
% looks for the corresponding index on the frasc
% or creates a new one
begin scalar x;
x := explode u;
if length(x) < 3 or not(nth(x,2) eq '=) then return u;
x := intern compress pnth(x,3);
putanewindex x;
if not atsoc(x,frasc!*) then
   frasc!* := nconc(frasc!*,list((x . u)));
return x
end;

symbolic procedure insertindices(u,x);
% u is a vecopp or tensopp
% x is an index or a list of indices
if (idp x and not isanindex x) or (idlistp x and not areallindices x)
   then rederr2('insertindices, "invalid indices to insertindex")
else if vecopp u then if idp u then list(u,x)
                 else car u . ( x . cdr u)
     else if tensopp u then if idp u then u . x
                            else car u . nconc(x,cdr u)
% do not insert any index in states or scalops
          else u;

symbolic procedure insertfreeindices(u,flg);
% procedure to transform vecop and tensop into scalops
% by inserting free indices taken from the varindlist
% flg is set to t if variable indices are requested
begin scalar n,x;
if vecopp u then <<x:= if flg then
                           transformvarindex getvarindex(indcnt!* + 1)
                       else getvarindex(indcnt!* + 1);
                    return insertindices(u,x)>>
else if tensopp u then <<n:= get(u,'tensdimen);
                         x:= for k:=1 :n collect if flg then
                              transformvarindex getvarindex(indcnt!* +k)
                              else getvarindex(indcnt!* +k);
                         return insertindices(u,x) >>
     else rederr2('insertfreeindices,
      "invalid argument to insertfreeindices");
end;

%- symbolic procedure collectindices u;
%- % makes a list of all indices in a.e. u
%- begin scalar v,x;
%- if atom u then
%-   if isanindex u then return list(u)
%-   else return nil;
%- a: v := car u;
%-    u := cdr u;
%-    x :=nconc(x,collectindices v);
%-    if null u then return x;
%-    go to a
%- end;

% This new version is intended to return exactly the same result as the
% original version as per above - however I think it is shorter and hence
% nicer. It does not use NCONC which I like because nconc is a destructive
% function, and if the result list ends up long it can avoid a potential
% quadratic cost as nconc searches to find the tail of the list that it
% needs to clobber. If it did not matter what order the indices were listed
% in one could avoid the little wrapper that calls REVERSIP.
% ACN August 2011.

symbolic procedure collectindices_reversed(u, r);
  begin
    if atom u then <<
      if isanindex u then return u . r
      else return r >>;
    while u do <<
       r := collectindices_reversed(car u, r);
       u := cdr u >>;
    return r
  end;

symbolic procedure collectindices u;
  reversip collectindices_reversed(u, nil);


symbolic procedure removeindices(u,x);
% u is physop (scalop) containing  physindices
% x is an index or a list of indices
  begin
    scalar op;
% Because I see this function rather heavily used I list the test up
% so that I do not do the function call to trwrite unless it is going to
% be useful.
    if flagp('removeindices, 'tracing) then
      trwrite('removeindices,"u= ",u," x= ",x);
    if null x or idp u or not !*physopp u then return u;
    if (idp x and not isanindex x) or
       (idlistp x and not areallindices x) then
      rederr2('removeindices, "invalid arguments to removeindices");
    op := car u;
    u := cdr u;
    if null u then return op
    else if idp x then u := delete(x,u)
    else for each y in x do u:= delete(y,u);
    return if null u then op else op . u
end;

symbolic procedure deadindices u;
% checks an a.e. u to see if there are dead indices
% i.e. indices appearing twice or more
%returns the list of dead indices in u
  begin
    scalar x,res;
    if null u or atom u then return nil;
    x := collectindices u;
    for each y in x do
      if memq(y,memq(y,x)) then res :=y . res;
    return reversip res
  end;

%- symbolic procedure collectphysops u;
%- % makes a list of all physops in a.e. u
%-   begin
%-     scalar v,x;
%-     if atom u then
%-       if physopp u then return list(u)
%-       else return nil
%-     else if physopp u then return list(removeindices(u,collectindices u));
%- a:  v := car u;
%-     u := cdr u;
%-     x :=nconc(x,collectphysops v);
%-     if null u then return x;
%-     go to a
%- end;
%-

symbolic procedure collectphysops_reversed(u, r);
% makes a list of all physops in a.e. u
  begin
    if atom u then <<
      if physopp u then return u . r
      else return r >>
    else if physopp u then
      return removeindices(u, collectindices u) . r;
    while not atom u do <<
      r := collectphysops_reversed(car u, r);
      u := cdr u >>;  
    return r;
  end;

symbolic procedure collectphysops u;
  reversip collectphysops_reversed(u, nil);

%- symbolic procedure !*collectphysops u;
%- % makes a list of all physops in a.e. u
%- % with ALL indices
%- begin scalar v,x;
%- if physopp u then return list(u);
%- if atom u then return nil;
%- a: v := car u;
%-    u := cdr u;
%-    x :=nconc(x,!*collectphysops v);
%-    if null u then return x;
%-    go to a
%- end;

symbolic procedure !*collectphysops_reversed(u, r);
% makes a list of all physops in a.e. u
% with ALL indices
  begin
    if physopp u then return u . r;
    while not atom u do <<
      r := !*collectphysops_reversed(car u, r);
      u := cdr u >>;
    return r;
  end;

symbolic procedure !*collectphysops u;
  reversip !*collectphysops_reversed(u, nil);


symbolic procedure collectphysops!* u;
  for each y in collectphysops u collect (if idp y then y else car y);

symbolic procedure collectphystype u; % new 1.01
% makes a list of all physops in u
% with ALL indices
  if physopp u then list(getphystype u)
  else if atom u then nil
  else deletemult!* (for each v in u collect getphystype v);

% ----  PHYSOP procedures for type check and assignement ----

% modify the REDUCE GETRTYPE routine to get control over PHYSOP
% expressions

symbolic procedure getrtype u; %modified
   % Returns overall algebraic type of u (or NIL if expression is a
   % scalar). Analysis is incomplete for efficiency reasons.
   % Type conflicts will later be resolved when expression is evaluated.
   begin scalar x,y;
    return
    if atom u
      then if not idp u then nil
            else if flagp(u,'share) then getrtype eval u
            else if x := get(u,'rtype)
                    then if y := get(x,'rtypefn) then apply1(y,nil)
                          else x
                  else nil
     else if not idp car u then nil
     else if physopp!* u then 'physop  % added
     else if (x := get(car u,'rtype)) and (x := get(x,'rtypefn))
      then apply1(x, cdr u)
     else if x := get(car u,'rtypefn) then apply1(x, cdr u)
     else nil
   end;

symbolic procedure getrtypecadr u;
not atom u and getrtype cadr u;

symbolic procedure getnewtype u;
not atom u and get(car u,'newtype);

symbolic procedure getphystype u;
% to get the type of a PHYSOP object
begin scalar x;
return
if physopp u then
   if scalopp u then  'scalar
   else if vecopp u then 'vector
        else if tensopp u then 'tensor
             else if statep u then  'state
                  else nil
else if atom u then nil
% following line suppressed 1.01
%    else if car u = '!*sq then return getphystype physopaeval u
          else if (x:=get(car u,'phystype)) then x
               else if (x:=get(car u,'phystypefn)) then
                        apply1(x,cdr u)
                             % from here it is 1.01
                    else if null (x := collectphystype u)         % 1.01
                             then nil
                         else if null cdr x then car x
                              else if member('state,x) then 'state
                                   else rederr2('getphystype,list(
                                          "PHYSOP type conflict in",u));
end;

symbolic procedure getphystypecar u;
 not atom u and getphystype car u;

symbolic procedure getphystypeor u;
not atom u and (getphystype car u or getphystypeor cdr u);

symbolic procedure getphystypeall args;  % new 1.01
begin scalar x;
 if null  (x := collectphystype deleteall(0,args)) then
        return nil
 else if cdr x then
          rederr2('getphystypeall,
              list("PHYSOP type mismatch in",args))
      else return car x
end;

% ***** dirty trick *****
% we introduce a rtypefn for !*sq expressions to get
% proper type checking in assignements
symbolic procedure physop!*sq U;
% u is a !*sq expressions
% checks if u contains physops
begin scalar x;
x:= !*collectphysops !*q2a car u;
return
if null x then nil
else 'physop
end;

deflist('((!*sq physop!*sq)), 'rtypefn);

% 1.01 we add also a phystypefn for !*sq

symbolic procedure getphystype!*sq u;  % new 1.01
getphystypesf caar u;

deflist('((!*sq getphystype!*sq)), 'phystypefn);

symbolic procedure getphystypesf u; % new 1.01
% u is a s.f.
% returns the phystype of u
if null u or domain!*p u then nil
else getphystype mvar u or getphystypesf lc u;

%-----end of 1.01 modifications -----------------

% we have also to modify the simp!*sq routine since
% there is no type checking included
symbolic procedure physopsimp!*sq u;
  if cadr u then car u
  else if physop!*sq u then physop2sq physopsm!* !*q2a car u
  else resimp car u;

put('!*sq,'simpfn,'physopsimp!*sq);
% ***** end of dirty trick ******

% ----PHYSOP evaluation and simplification procedures----

symbolic procedure !*physopsm!* (u,v);
% u is the PHYSOP expression to simplify
  begin
    scalar x,contractflg;
% if contract is set to on we keep its value at the top level
% (first call to physopsm) and set it to nil;
    contractflg:=!*contract;!*contract := nil;
    !*hardstop := nil;
    if physopp u then
      if (x:= get(u,'rvalue)) then u := physopaeval x
      else if idp u then return u
      else if x:=get(car u,'psimpfn) then u:= apply1(x,u)
      else return physopsimp u;
    u:= physopsm!* u;
    if !*hardstop then <<
      write "        *************** WARNING: ***************";terpri();
      write "Evaluation incomplete due to missing elementary relations";
      terpri();
      return u >>;
% the next step is to do substitutions if there are someones on
% the matching lists
    if !*match or powlis1!* then <<
      u := physopsubs u;
% now eval u with the substitutions
      u := physopsim!* u; >>;
if not contractflg then return u
    else <<
      !*contract:=contractflg;
      return physopcontract u >>
  end;

symbolic procedure physopsim!* u;
   if eqcar(u,'!:dn!:) then prepsq simp u
    else if !*physopp!* u then physopsm!* u else u;

symbolic  procedure physop2sq u; %modified 1.01
% u is a physop expr
% returns standard quotient of evaluated u
  begin scalar x;
    return
      if physopp u then
        if (x:= get(u,'rvalue)) then physop2sq x
        else if idp u then !*k2q u
        else if (x:= get(car u,'psimpfn)) then
          if physopp (x:=apply1(x,u)) then !*k2q x
          else cadr physopsm!* x
        else if get(car u,'opmtch) and
                (x:= opmtch!* u) then physop2sq x
        else !*k2q u
      else if atom u then simp u % added 1.01
      else if car u eq '!*sq then cadr u
      else if null getphystype u then simp u % moved from top 1.01
      else physop2sq physopsm!* u
  end;

symbolic procedure physopsm!* u;
% basic simplification routine
 begin scalar oper,args,y,v,physopflg;
 % the following is 1.02
 if (null u or numberp u) then v := u
 else if physopp u then v:= if (y:= get(u,'rvalue)) then physopaeval y
                       else if idp u then u
                       else if (y:=get(car u,'psimpfn)) then
                               apply1(y,u)
                       else if get(car u,'opmtch) and
                               (y:=opmtch!* u) then y
                       else u
 else if atom u then v := aeval u
 else <<
   oper := car u;
   args := cdr u;
   if y:= get(oper,'physopfunction) then
   % this is a function which may also have normal scalar arguments
   % eg TIMES so we must check if args contain PHYSOP objects
   % or if it is an already evaluated expression of physops
        if flagp(oper,'physoparith) then
            if hasonephysop args then v:= apply(y,list args)
            else  v := reval3 (oper . args)
        else if flagp(oper,'physopfn) then
                if areallphysops args then v:= apply(y,list args)
                else
                rederr2('physopsm!*,
                list("invalid call of ",oper," with args: ",args))
        else rederr2('physopsm!*,list(oper,
            " has been flagged Physopfunction"," but is not defined"))
% this is for fns having a physop argument and no evaluation procedure
   else if flagp(oper,'physopmapping) and !*physopp!* args then
                v := mk!*sq !*k2q (oper . args)
%   special hack for handling of PROG constructs
   else if oper = 'PROG then v := physopprog args
        else  v := aeval  u
      >>;
  return v
end;

symbolic procedure physopsubs u;
% general substitution routine for physop expressions
% corresponds to subs2
% u is a !*sq
% result is u in a.e. form with all substitutions of
% !*MATCH and POWLIS1!* applied
% we use a quite dirty trick here which allows to use
% the pattern matcher of standard REDUCE by hiding the
% PHYSOP rtype temporarily
begin scalar ulist,kord,alglist!*;
% step 1: convert u back to an a.e.
% u := physopaeval u; % 1.01 this line replaced
  u := physop2sq u;
% step 2: transform all physops on physoplist in normal ops
  for each x in physoplist!* do << remprop(x,'rtype);
                                  put(x,'simpfn,'simpiden)>>;
  % since we need it here as a prefix op
  remflag('(dot),'physopfn);
  put('dot,'simpfn,'simpiden);
% step 3: call simp!* on u
% u := simp!* u; % 1.01 this line replaced
 u := subs2 u;
% step 4: transform u back in an a.e.
  u := !*q2a u;
% step 5: transform ops in physoplist back to physops
  for each x in physoplist!* do <<remprop(x,'simpfn);
                                   put(x,'rtype,'physop)>>;
  remprop('dot,'simpfn);
  flag('(dot),'physopfn);
% final step return u
return u
end;

symbolic procedure physopaeval u;
% transformation of physop expression in a.e.
begin scalar x;
return
if physopp u then
   if (x:=get(u,'rvalue)) then
       if car x eq '!*sq then !*q2a cadr x
       else x
   else if atom u then u
   else if (x:= get(car u,'psimpfn)) then apply1(x,u)
   else if get(car u,'opmtch) and (x:= opmtch!* u)  then x
       else  u
else if (not atom u) and car u eq '!*sq then !*q2a cadr u
     else u
end;

symbolic procedure physopcontract u;
% procedure to contract over dead indices
begin scalar x,x1,w,y,z,ulist,veclist,tenslist,oldmatch,oldpowers,
             alglist!*,ncmplist;
u := physopaeval u;
if physopp u then return mk!*sq physop2sq u
else if not getphystype u then return aeval u;
% now came the tricky cases
!*contract2 := t;
% step1 : collect all physops in u
 ulist := collectphysops u;
 veclist := for each x in ulist collect if vecopp x then  x else nil;
 tenslist := for each x in ulist collect if tensopp x then x else nil;
 veclist:= deletemult!* deleteall(nil,veclist);
 tenslist:=deletemult!* deleteall(nil,tenslist);
% step2: we now modify powlis1!* and !*match
  oldmatch := !*match; !*match := nil;
  oldpowers := powlis1!*; powlis1!* := nil;
% step3: transform all physops on physoplist in normal ops
for each x in physoplist!* do
<<
   remprop(x,'rtype);
   put(x,'simpfn,'simpiden);
   if noncomp!* x then ncmplist := x . ncmplist;
>>;
% we have to declare the ops in the specoplist as noncom to avoid
% spurious simplifications  during contraction
remflag('(dot opapply),'physopfn); % needed here as a normal op
flag(specoplist!*,'noncom);
!*ncmp := t;
for each x in specoplist!* do
<<
   put(x,'simpfn,'simpiden);
   put(x,'noncommutes,ncmplist)
>>;
% step4: put new matching for each vecop on the list
  y := getvarindex(1);
  frlis!* := nconc(frlis!*,list('!=nv));
  frasc!* := nconc(frasc!*,list('nv . '!=nv));
  for each x in veclist do <<
     let2(list('expt,insertindices(x,transformvarindex y),'nv),
           list('expt,x,'nv),nil,t);
      x1:=delete(x,veclist);
     for each w in x1 do
       << z := list(list((insertindices(x,y) . 1),
            (insertindices(w,y) . 1)),(nil . t),
            list('dot,x,w),nil);
       !*match :=append(list(z),!*match) >>
  >>;
% step4: put new matching for each tensop on the list
  frlis!* := nconc(frlis!*,list('!=nt));
  frasc!* := nconc(frasc!*,list('nt . '!=nt));
  for each x in tenslist do
     let2(list('expt,insertfreeindices(x,t),'nt),
           list('expt,x,'nt),nil,t);
% step 6: call simp on u
u := simp!* u;
% step 7: restore previous settings
powlis1!* := oldpowers;!*match := oldmatch;
for each x in physoplist!* do
<<
   remprop(x,'simpfn);
   put(x,'rtype,'physop)
>>;
flag('(dot opapply),'physopfn);
remflag(specoplist!*,'noncom);
for each x in specoplist!* do
<<
   remprop(x,'noncommutes);
   remprop(x,'simpfn)
>>;
!*contract2 := nil;
return mk!*sq u
end;

symbolic procedure physopsimp u;  % 1 line deleted 1.03
% procedure to simplify the arguments of a physop
% inspired from SIMPIDEN
begin scalar opname,w,x,y,flg;
if idp u then return u;
opname := car u;
x := for each j in cdr u collect
         if idp j and (isanindex j or isavarindex j) then j %added 1.01
         else physopsm!* j;
u := opname . for each j in x collect
                if eqcar(j,'!*sq) then prepsqxx cadr J
                else j;
if x := opmtch!* u then return x;
% special hack introduced here to check for
% symmetric and antisymmetric tensor operators
if scalopp u and tensopp opname then
 << y := get(opname,'tensdimen);
% x is the list of physopsindices
    x:= for k:=1 :y collect nth(cdr u,k);
% y contains the remaining indices
    if length(cdr u) > y then y := pnth(cdr u,y+1)
    else y := nil;
    if flagp(opname,'symmetric) then u:= opname . ordn x
    else if flagp(opname,'antisymmetric) then
           << if repeats x then return 0
              else if not permp(w := ordn x, x) then flg := t;
              x := w;
              u := opname . x >>
         else u := opname . x;
    if y then  u:= append(u,y);
    return if flg then list('minus,u)  else u
 >>
% special hack to introduce unrecognized IDXn indices
 else if vecopp u then << if listp u then putanewindex!* cadr u;
                          return u >>
      else if tensopp u then << if listp u then
                                     for j:= 1 : length(cdr u) do
                                    putanewindex!* nth(cdr u,j);
                                return u >>
           else return u
end;
% ---- different procedures for arithmetic in phsyop expressions ----

flag('(quotient times expt difference minus plus opapply),'physoparith);
flag('(adj recip dot commute anticommute),'physopfn);
flag ('(sub),'physoparith);
flag('(sin cos tan asin acos atan sqrt int df log exp sinh cosh tanh),
      'physopmapping);
% the following is needed for correct type checking 101290 mw

symbolic procedure checkphysopmap u;
% checks an expression u for unresolved physopmapping  operators
begin scalar x;
a: if null u or domain!*p u or atom u or null cdr u then return nil;
   x:= car u; u:= cdr u;
   if listp x and flagp(car x,'physopmapping) and hasonephysop cdr x
         then return t;
   go to a;
end;

symbolic procedure physopfn(oper,proc);
begin
put(oper,'physopfunction,proc);
end;

physopfn('difference,'physopdiff);

symbolic procedure physopdiff args;
 begin scalar lht,rht,lhtype,rhtype;
 lht := physopsim!* car args;
 for each v in cdr args do  <<
   rht := physopsim!* v;
   lhtype := getphystype lht;
   rhtype :=  getphystype rht;
   if (rhtype and lhtype) and not(lhtype eq rhtype) then
        rederr2('physopdiff,"type mismatch in diff");
   lht :=
       mk!*sq addsq(physop2sq lht,negsq(physop2sq rht))
 >>;
 return lht
end;

put('difference,'phystypefn,'getphystypeall);  % changed 1.01

physopfn('minus,'physopminus);

symbolic procedure physopminus arg;
begin scalar rht,rhtype;
 rht := physopsim!* car arg;
 rht :=
     mk!*sq negsq(physop2sq rht);
return rht
end;

put('minus,'phystypefn,'getphystypecar);

physopfn('plus,'physopplus);

symbolic procedure physopplus args;
 begin scalar lht,rht,lhtype,rhtype;
 lht := physopsim!* car args;
 for each v in cdr args do  <<
   rht := physopsim!* v;
   lhtype := getphystype lht;
   rhtype := getphystype rht;
   if (rhtype and lhtype) and  not (lhtype eq rhtype) then
        rederr2 ('physopplus,"type mismatch in plus ");
   lht :=
      mk!*sq addsq(physop2sq lht,physop2sq rht)
 >>;
 return lht
end;

put('plus,'phystypefn,'getphystypeall);  % changed 1.01

physopfn('times,'physoptimes);

symbolic procedure physoptimes args;
 begin scalar lht, rht,lhtype,rhtype,x,mul;
 if (tstack!* = 0) and mul!* then << mul:= mul!*; mul!* := nil; >>;
 tstack!* := tstack!* + 1;
 lht := physopsim!* car args;
 for each v in cdr args do  <<
   rht :=physopsim!* v;
   lhtype := getphystype  lht;
   rhtype := getphystype  rht;
if not lhtype then
   if not rhtype then lht := mk!*sq multsq(physop2sq lht,physop2sq rht)
    else if zerop lht then lht := mk!*sq (nil . 1)
         else if onep lht then lht:= mk!*sq physop2sq rht
              else lht:= mk!*sq  multsq(physop2sq lht,physop2sq rht)
else if not rhtype then lht:=
        if zerop rht then mk!*sq (nil . 1)
        else if onep rht then mk!*sq physop2sq lht
             else mk!*sq multsq(physop2sq rht,physop2sq lht)
else if physopordchk(physopaeval lht,physopaeval rht)
             and (lhtype = rhtype) and (lhtype = 'scalar)
        then lht := mk!*sq multsq(physop2sq lht,physop2sq rht)
     else  lht:= multopop!*(lht,rht)
 >>;
b: if null mul!* or tstack!* > 1 then go to c;
   lht := apply1(car mul!*,lht);
   mul!* := cdr mul!*;
   go to b;
c: tstack!* := tstack!* - 1;
   if tstack!* = 0  then mul!* := mul;
   return lht
end;

put('times,'phystypefn,'getphystypetimes);

symbolic procedure getphystypetimes args;  % modified 1.01
begin scalar x;
 if null  (x := deleteall(nil,collectphystype args)) then
        return nil
 else if null cdr x then   return car x
      else rederr2('getphystypetimes,
              list("PHYSOP type mismatch in",args))
end;

symbolic procedure multopop!*(u,v);
% u and v are physop exprs in a.e. form
% value is the product of u and v + commutators if needed
begin scalar x,y,u1,v1,stac!*,res;
% if there is no need for additional computations of commutators
% return the product as a standard quotient
u1:= physopaeval u;
v1:= physopaeval v;
if physopp u1 and physopp v1 then res := multopop(u1,v1)
else if physopp v1 then
         if car u1 memq '(plus difference minus) then <<
              x:= for each y in cdr u1 collect physoptimes list(y,v);
              res:= reval3 (car u1 . x) >>
         else if car u1 eq 'times then <<
                  stac!*:= reverse cdr u1; % begin with the last el
                  y:= v;
                  while stac!* do <<
                  x := car stac!*;
                  y := physoptimes list(x,y);
                  stac!* := cdr stac!*;
                                  >>;
                  res:= y >>
                  else if car u1 eq 'quotient then res:= mk!*sq
            quotsq(physop2sq physoptimes list(cadr u1,v),
                   physop2sq caddr u1)
                       else res:= physoptimes list(u1,v1)
     else if car v1 memq '(plus difference minus) then <<
             x:= for each y in cdr v1 collect physoptimes list(u,y);
             res:= reval3 (car v1 . x) >>
             else if car v1 eq 'times then <<
                  stac!*:= cdr v1;
                  y:= u;
                  while stac!* do <<
                  x := car stac!*;
                  y := physoptimes list(y,x);
                  stac!* := cdr stac!*;
 %                write "y= ",y," stac= ",stac!*;terpri();
                                  >>;
                  res:= y >>
                  else if car v1 eq 'quotient then res:= mk!*sq
                          quotsq(physop2sq physoptimes list(u,cadr v1),
                             physop2sq caddr v1)
                       else res:= physoptimes list(u1,v1);
return res
end;

symbolic procedure multopop(u,v);
% u and v are physops  (kernels)
% value is the product of physops + commutators if necessary
begin scalar res,x,ltype,rtype;
ltype := getphystype u;
rtype := getphystype v;
if ltype neq rtype then
       rederr2('multopop,"type conflict in TIMES")
 else if (invp u = v) then res := mk!*sq !*k2q 'unit
 else if u = 'unit then res := mk!*sq !*k2q  v
 else if v = 'unit then res := mk!*sq !*k2q u
 else if ordop(u,v) then
              res :=  mk!*sq !*f2q multfnc(!*k2f u,!*k2f v)
 else if noncommuting(u,v) then <<x:= comm2(u,v);
                 res:= if !*anticommchk then physopplus
                          list(list('minus,list('times,v,u)),x)
                       else  physopplus
                           list(list('times,v,u),x) >>
 else  res := mk!*sq !*f2q multfnc(!*k2f v,!*k2f u);
return res
end;

physopfn('expt,'physopexpt);

symbolic procedure physopexpt args;
 begin scalar n1,n2,lht,rht,lhtype,rhtype,x,y,z;
% we have to add a special bootstrap to avoid too much simplification
% in case of dot products raise to a power
 lht := physopsm!* car args;
 rht := physopsm!* cadr args;
 lhtype := physopp  lht ;
 rhtype := physopp  rht;
 if rhtype then
     rederr2('physopexpt,"operators in the exponent cannot be handled");
 if not getphystype lht then lht := reval3 list('expt,lht,rht);
 if not lhtype then
    if numberp rht then <<
            n1 := car divide(rht,2);
            n2 := cdr divide(rht,2);
            lhtype := getphystype lht;
       if (lhtype and zerop rht) then lht := mk!*sq !*k2q 'unit %1.01
       else if lhtype = 'vector then <<
            x:= for k:= 1 : n1 collect physopdot list(lht,lht);
            if onep n1 then x := 1 . x;
            lht:= if zerop n2 then physoptimes x
                  else physoptimes append(x,list(lht));>>
       else if lhtype  = 'tensor then <<
            x:= for k:= 1 : n1 collect physoptens list(lht,lht);
            if onep n1 then x := 1 . x;
            lht:= if zerop n2 then physoptimes x
                  else physoptimes append(x,list(lht));>>
       else if lhtype = 'state then
         rederr2('physopexpt,
         "expressions involving states cannot be exponentiated")
       else << lht := physopaeval lht;
               x := deletemult!* collectindices lht;
               z := lht;
               for k :=2 :rht do <<
                for each x1 in x do
                    if isavarindex x1 then
                       lht:= subst(makeanewvarindex(),x1,lht)
                    else lht:=subst(makeanewindex(),x1,lht);
                y := append(y,list(lht));
                lht := z; >>;
               lht := physoptimes (z . y); >>;
       >>
    else lht := mk!*sq simpx1(physopaeval lht,physopaeval rht,1)
else if lht = 'unit then lht := mk!*sq !*k2q 'unit
     else if numberp rht  then  lht := exptexpand(lht,rht)
          else  lht := mk!*sq !*P2q (lht . physopaeval rht); %0.99c
return lht
end;

put('expt,'phystypefn,'getphystypeexpt);

symbolic  procedure getphystypeexpt args;  % recoeded 0.99c
begin scalar x;
x := getphystypecar args;
return
    if null x then nil
    else if numberp cadr args and evenp cadr args then 'scalar
    else x;
end;

symbolic procedure exptexpand(u,n);
begin scalar bool,x,y,v,n1,n2,res,flg;
if not numberp n then
rederr2('exptexpand,list("invalid argument ",n," to EXPT"));
if zerop n then return mk!*sq !*k2q 'unit; %1.01
bool := if n < 0 then t else nil;
n := if bool then abs(n) else n;
n1 := car divide(n,2);
n2 := cdr divide(n,2);
if zerop n1 then return mk!*sq !*k2q
            if bool then invp u else u;
res := (1 . 1);
for k := 1 : n1 do <<
  if scalopp u then
     if bool then x := multf(!*k2f invp u, !*k2f invp u) . 1
     else x := multf(!*k2f u, !*k2f u) . 1
%    if bool then  x:= list(list((invp u . 1),((invp u . 1) . 1))) . 1
%    else x:= list(list((u . 1),((u . 1) . 1))) . 1
  else if vecopp u then
          if bool then x:= quotsq((1 . 1),physop2sq physopdot list(u,u))
          else  x:= physop2sq physopdot list(u,u)
  else if tensopp u then <<
          if bool then x:= quotsq((1 . 1),
                           physop2sq physoptens list(u,u))
          else  x:= physop2sq physoptens list(u,u) >>
       else  rederr2('exptexpand, "cannot raise a state to a power");
res := multsq(res,x)
>>;
b:
if zerop n2 then return mk!*sq res;
u:= if bool then invp u else u;
return mk!*sq multsq(res,!*k2q u)
end;


physopfn('quotient,'physopquotient);

symbolic  procedure physopquotient args;
 begin scalar lht, rht,y,lhtype,rhtype;
 lht := physopsim!*  car args;
 rht := physopsim!* cadr args;
 lhtype := getphystype car args;
 rhtype := getphystype cadr args;
 if rhtype memq '(vector state tensor) then
      rederr2('physopquotient, "invalid quotient")
 else if not rhtype then return
       mk!*sq quotsq(physop2sq lht,physop2sq rht);
   lhtype := physopp  lht;
   rht := physopaeval rht;
   rhtype := physopp  rht;
if rhtype then
   if not lhtype then  lht:= mk!*sq multsq(physop2sq lht,!*k2q invp rht)
   else lht:= physoptimes list(lht,invp rht)
else if car rht eq 'times and null deadindices rht then
           << rht := reverse cdr rht;
              rht := for each  x in rht collect
                     physopquotient list(1,x);
              lht := physoptimes append(list(lht),rht) >>
     else lht:= mk!*sq quotsq(physop2sq lht,physop2sq rht);
return lht
end;

put('quotient,'phystypefn,'getphystypeor);

physopfn('recip,'physoprecip);

symbolic procedure physoprecip args;
physopquotient list(1,args);

put('recip,'phystypefn,'getphystypecar);

symbolic procedure invphysop u;
% inverse of physops
begin scalar x,y;
if not physopp u then rederr2('invphysop,"invalid argument to INVERSE");
if u = 'unit then return u;
 y:= if idp u then u else car u;
x := reversip explode y;
x := intern compress nconc(reversip x,list('!!,'!-,'!1));
put(y,'inverse,x); % 1.01
put(x,'inverse,y); % 1.01
put(x,'physopname,y); % 1.02
if not physopp x then << put(x,'rtype,'physop);
                         put(x,'phystype,get(y,'phystype));
                         put(x,'psimpfn,'physopsimp);
                         put(x,'tensdimen,get(y,'tensdimen));
                         physoplist!* := nconc(physoplist!*,list(x));
                      >>;
if idp u then return x
else return nconc(list(x),cdr u)
end;

symbolic procedure invp u; % recoded 1.01
% special cases
if u = 'unit then u
else if atom u then get(u,'inverse)
else if member(car u,'(comm anticomm)) then
     list('quotient,1,u)
else get(car u,'inverse) . cdr u;

physopfn('sub,'physopsub); %subcommand;


% ********* redefinition of SUB handling is necessary in 3.4 **********
remprop('sub,'physopfunction);
put('sub,'physopfunction,'subeval);
put('physop,'subfn,'physopsub);

symbolic procedure physopsub(u,v); %redefined
% u is a list of substitutions  as an a--list
% v is a simplified physop  in prefix form
begin  scalar res;
if null u or null v then return v;
v := physopaeval v;
for each x in u do  v := subst(cdr x,car x,v);
return physopsm!* V
end;
% *********** end of 3.4 modifications ******************

symbolic procedure physopprog u;
% procedure to handle prog expressions (i.e. loops) containing physops
begin scalar x;
% we use basically the same trick as in physopsubs
% step 1: transform all physops on physoplist in normal ops
  for each x in physoplist!* do <<remprop(x,'rtype);
                                  put(x,'simpfn,'simpiden)>>;
% step 2: call normal prog on u
   u := aeval ('prog . u);
% step 3: transform u back in an a.e.
  u := physopaeval u;
% step 4: transform ops in physoplist back to physops
  for each x in physoplist!* do <<remprop(x,'simpfn);
                                  put(x,'rtype,'physop)>>;
% final step return u
  return physopsm!* u
end;

% ******   procedures for physopfns  ***********

physopfn('dot,'physopdot);
infix dot;
precedence dot,*;
symbolic procedure  physopdot args;
begin scalar lht,rht,lhtype,rhtype,x,n,res;
lht :=  physopaeval physopsim!*  car args;
rht := physopaeval physopsim!* cadr args;
lhtype := getphystype lht;
rhtype := getphystype rht;
if not( (lhtype and rhtype) and (lhtype eq 'vector) ) then
   rederr2 ('physopdot,"invalid arguments to dotproduct");
lhtype := physopp lht;
rhtype := physopp rht;
if rhtype then
  if lhtype then << if !*indflg then<<
                           lht := insertfreeindices(lht,nil);
                           rht := insertfreeindices(rht,nil);
                           indcnt!* := indcnt!* + 1; >>
                    else <<x :=  makeanewindex();
                           lht := insertindices(lht,x);
                           rht := insertindices(rht,x);>>;
                    res := physoptimes list(lht,rht)>>
     else <<
  if car lht eq 'minus then
        res := mk!*sq negsq(physop2sq physopdot list(cadr lht,rht))
    else if car lht eq 'difference then res := mk!*sq addsq(
        physop2sq physopdot list(cadr lht,rht),negsq(physop2sq
                           physopdot list(caddr lht,rht)))
     else if car lht eq 'plus then <<
       x := for each y in cdr lht collect physopdot list(y,rht);
       res := reval3 append(list('plus),x)  >>
     else if car lht  eq 'quotient then <<
             if not vecopp cadr lht  then
               rederr2('physopdot,"argument to DOT")
              else res := mk!*sq quotsq(physop2sq
                physopdot list(cadr lht,rht),physop2sq caddr lht) >>
     else if car lht  eq 'times then <<
        for each y in cdr lht do
           if getphystype y  eq 'vector then x:=y;
        lht :=delete(x,cdr lht);
        res := physoptimes
        nconc(lht,list(physopdot list(x,rht))) >>
     else rederr2('physopdot, "invalid arguments to DOT") >>;
if not rhtype then <<
  if car rht eq 'minus then
        res := mk!*sq negsq(physop2sq physopdot list(lht,cadr rht))
    else if car rht eq 'difference then res := mk!*sq addsq(
        physop2sq physopdot list(lht,cadr rht),negsq(physop2sq
                           physopdot list(lht, caddr rht)))
     else if car rht eq 'plus then <<
       x := for each y in cdr rht collect physopdot list(lht,y);
       res := reval3 append(list('plus),x)  >>
     else if car rht  eq 'quotient then <<
             if not vecopp cadr rht  then
      rederr2 ('physopdot,"invalid argument to DOT")
              else res := mk!*sq quotsq(physop2sq physopdot
                    list(lht,cadr rht),physop2sq caddr rht)  >>
     else if car rht  eq 'times then <<
        for each y in cdr rht do if getphystype y  eq 'vector then x:=y;
        rht :=delete(x,cdr rht);
        res := physoptimes
               nconc(rht,list(physopdot list(lht,x))) >>
     else rederr2 ('physopdot,"invalid arguments to DOT") >>;
return res
end;

put('dot,'phystype,'scalar);

symbolic procedure  physoptens args;
% procedure for products of tensor expressions
begin scalar lht,rht,lhtype,rhtype,x,n,res;
lht :=  physopaeval physopsim!*  car args;
rht := physopaeval physopsim!* cadr args;
lhtype := getphystype lht;
rhtype := getphystype rht;
if not( (lhtype and rhtype) and (lhtype eq 'tensor) ) then
   rederr2 ('physoptens,"invalid arguments to tensproduct");
lhtype := physopp lht;
rhtype := physopp rht;
if rhtype then
  if lhtype then << n:= get(lht,'tensdimen);
                    if (n neq get(rht,'tensdimen)) then
                    rederr2('physoptens,
               "tensors must have the same dimension to be multiplied");
                    if !*indflg then<<
                           lht := insertfreeindices(lht,nil);
                           rht := insertfreeindices(rht,nil);
                           indcnt!* := indcnt!* + n;  >>
                    else <<x :=  for k:= 1 : n collect makeanewindex();
                           lht := insertindices(lht,x);
                           rht := insertindices(rht,x);>>;
                    res := physoptimes list(lht,rht)>>
     else <<
  if car lht eq 'minus then
        res := mk!*sq negsq(physop2sq physoptens list(cadr lht,rht))
    else if car lht eq 'difference then res := mk!*sq addsq(
        physop2sq physoptens list(cadr lht,rht),negsq(physop2sq
                           physoptens list(caddr lht,rht)))
     else if car lht eq 'plus then <<
       x := for each y in cdr lht collect physoptens list(y,rht);
       res := reval3 append(list('plus),x)  >>
     else if car lht  eq 'quotient then <<
             if not tensopp cadr lht  then
               rederr2 ('physoptens,"invalid argument to TENS")
              else res := mk!*sq quotsq(physop2sq
                physoptens list(cadr lht,rht),physop2sq caddr lht) >>
     else if car lht  eq 'times then <<
        for each y in cdr lht do
           if getphystype y  eq 'tensor then x:=y;
        lht :=delete(x,cdr lht);
        res := physoptimes
        nconc(lht,list(physoptens list(x,rht))) >>
     else rederr2('physoptens, "invalid arguments to TENS") >>;
if not rhtype then <<
  if car rht eq 'minus then
        res := mk!*sq negsq(physop2sq physoptens list(lht,cadr rht))
    else if car rht eq 'difference then res := mk!*sq addsq(
        physop2sq physoptens list(lht,cadr rht),negsq(physop2sq
                           physoptens list(lht, caddr rht)))
     else if car rht eq 'plus then <<
       x := for each y in cdr rht collect physoptens list(lht,y);
       res := reval3 append(list('plus),x)  >>
     else if car rht  eq 'quotient then <<
             if not tensopp cadr rht  then
      rederr2 ('physoptens,"invalid argument to TENS")
              else res := mk!*sq quotsq(physop2sq physoptens
                    list(lht,cadr rht),physop2sq caddr rht)  >>
     else if car rht  eq 'times then <<
        for each y in cdr rht do if getphystype y  eq 'tensor then x:=y;
        rht :=delete(x,cdr rht);
        res := physoptimes
               nconc(rht,list(physoptens list(lht,x))) >>
     else rederr2('physoptens, "invalid arguments to TENS") >>;
return res
end;

put('tens,'phystype,'scalar);


% -------- procedures for commutator handling -------------

symbolic procedure comm2(u,v);
% general procedure for getting commutators
begin scalar x,utype,vtype,y,z,z1,res;
if not (physopp u and physopp v) then rederr2('comm2,
                       "invalid arguments to COMM");
utype := getphystype u;
vtype := getphystype v;
if not (utype eq 'scalar) and (vtype eq 'scalar) then
rederr2('comm2, "comm2 can only handle scalar operators");
!*anticommchk:= nil;
if not noncommuting(u,v) then return
  if !*anticom then mk!*sq !*f2q multf(!*n2f 2,multfnc(!*k2f v,!*k2f u))
  else mk!*sq (nil . 1);
x := list(u,v);
z := opmtch!* ('comm . x);
if  null z then z:= if (y:= opmtch!* ('comm . reverse x)) then
                        physopsim!* list('minus,y)
                    else nil;
if z and null !*anticom then res:=  physopsim!* z
else << z1 := opmtch!* ('anticomm . x);
        if null z1 then
        z1 :=  if (y:=opmtch!* ('anticomm . reverse x)) then y
               else nil;
        if z1 then << !*anticommchk := T;
                      res:=  physopsim!* z1>>
     >>;
if null res then
   << !*hardstop:= T;
      if null !*anticom then res := mk!*sq !*k2q ('comm . x)
      else << !*anticommchk := T;
              res := mk!*sq !*k2q ('anticomm . x) >>
>>;
return res
end;

physopfn('commute,'physopcommute);

symbolic procedure physopcommute args;
begin scalar lht,rht,lhtype,rhtype,x,n,res,flg;
lht :=  physopaeval  physopsim!*  car args;
rht := physopaeval  physopsim!* cadr args;
lhtype := getphystype lht;
rhtype := getphystype rht;
if not (lhtype and rhtype) then return mk!*sq !*d2q 0
else if not(rhtype = lhtype) then
  rederr2('physopcommute,
   "physops of different types cannot be commuted")
else if not(lhtype eq 'scalar) then
 rederr2 ('physopcommute,
 "commutators only implemented for scalar physop expressions");
% flg := !*anticom; !*anticom := nil;
lhtype := physopp lht;
rhtype := physopp rht;
% write "lht= ",lht," rht= ",rht;terpri();
if rhtype then
   if lhtype then << res := comm2(lht,rht);
                     if !*anticommchk then
                         res := physopdiff list(res,
                                physoptimes list(2,rht,lht)); >>
   else res := mk!*sq negsq(physop2sq physopcommute list(rht,lht))
else <<
if car rht eq 'minus then res:= mk!*sq negsq(physop2sq
    physopcommute list(lht, cadr rht));
if car rht eq 'difference then  res := mk!*sq addsq(
     physop2sq physopcommute list(lht,cadr rht),negsq(physop2sq
            physopcommute list(lht,caddr rht)));
if car rht  eq 'plus then <<
                  x:= for each y in cdr rht collect
                      physopcommute list(lht,y);
                  res:= reval3 append(list('plus),x) >>;
if car rht memq '(expt dot commute) then
               res := physopcommute list(lht,physopsim!* rht);
if car rht eq 'quotient then
   if physopp caddr rht then
       res:= physopcommute list(lht,physopsim!* rht)
    else
   res := mk!*sq quotsq(physop2sq physopcommute list(lht,cadr rht),
          physop2sq caddr rht);
if car rht eq 'times then <<
   n := length cdr rht;
   if (n = 2) then res := reval3 list('plus, physopsim!*
        list('times,cadr rht,physopcommute list(lht, caddr rht)),
  physopsim!* list('times,physopcommute list(lht, cadr rht),caddr rht))
   else res := reval3 list('plus, physopsim!*
        list('times,cadr rht,physopcommute list(lht,
           append('(times),cddr rht))), physopsim!* append(
          list('times,physopcommute list(lht, cadr rht)), cddr rht)) >>
     >>;
% !*anticom := flg;
return res
end;

put('commute,'phystype,'scalar);

physopfn('anticommute,'physopanticommute);

symbolic procedure physopanticommute args;
begin scalar lht,rht,lhtype,rhtype,x,n,res,flg;
lht :=  physopaeval  physopsim!*  car args;
rht := physopaeval  physopsim!* cadr args;
lhtype := getphystype lht;
rhtype := getphystype rht;
if not (lhtype and rhtype) then
     return mk!*sq aeval list('plus,list('times,lht,rht),
                               list('times,rht,lht))
else if not(rhtype = lhtype) then
  rederr2('physopanticommute,
   "physops of different types cannot be commuted")
else if not(lhtype eq 'scalar) then
 rederr2 ('physopanticommute,
 "commutators only implemented for scalar physop expressions");
% flg := !*anticom;!*anticom :=t;
lhtype := physopp lht;
rhtype := physopp rht;
% write "lht= ",lht," rht= ",rht;terpri();
if rhtype then
   if lhtype then
   <<
      x := comm2(lht,rht);
      if null !*anticommchk then
         If !*hardstop then res := mk!*sq !*k2q list('anticomm,lht,rht)
         else res := reval3 list('plus,x,physoptimes list(2,rht,lht))
      else res := x;
   >>
   else res := physopsim!* physopanticommute list(rht,lht)
else <<
if car rht eq 'minus then res:= mk!*sq negsq(physop2sq
    physopanticommute list(lht, cadr rht));
if car rht eq 'difference then mk!*sq addsq(physop2sq
     physopanticommute list(lht,cadr rht),negsq(physop2sq
     physopanticommute list(lht,caddr rht)));
if car rht  eq 'plus then <<
                  x:= for each y in cdr rht collect
                      physopanticommute list(lht,y);
                  res:= reval3 append(list('plus),x) >>
else res := physopplus list(physoptimes list(lht,rht),
                             physoptimes list(rht,lht));
     >>;
% !*anticom := flg;
return res
end;

put('anticommute,'phystype,'scalar);

symbolic procedure commsimp u;
% procedure to simplify the arguments of COMM or ANTICOMM
% if they are not simple physops
begin scalar opname,x,y,flg,res;
opname := car u;
x := physopsim!* cadr u;
y := physopsim!* caddr u;
% write "op= ",opname," x= ",x," y= ",y;terpri();
flg := !*anticom;
if opname = 'anticomm then !*anticom := t;
res := if physopp x and physopp y then physopaeval comm2(x,y)
       else if opname eq 'comm then list('commute,physopaeval x,
                                            physopaeval y)
            else list('anticommute,physopaeval x,physopaeval y);
!*anticom := flg;
return res
end;

% -------------- application of ops on states ----------------


physopfn('opapply,'physopapply);

infix opapply;
precedence opapply,-;

symbolic procedure physopapply args; % changed 0.99b
begin scalar lhtype,rhtype,wave,op,wavefct,res,x,y,flg;
lhtype := statep!* car args;
rhtype := statep!* cadr args;
if rhtype  and lhtype  then
   return statemult(car args,cadr args)
else if rhtype then
         <<wave :=  physopaeval physopsim!* cadr args;
           op := physopaeval physopsim!* car args >>
else if lhtype then
        <<wave := physopaeval physopadj list(car args);
          op := physopaeval physopadj cdr args >>
% a previous application of physopapply may have annihilated the
% state
else  if zerop car args or zerop cadr args then return mk!*sq (nil . 1)
else rederr2('opapply, "invalid arguments to opapply");
if null getphystype op then
     res:= mk!*sq multsq(physop2sq op,physop2sq wave)
else if not physopp op then
        if car op eq 'minus then
          res := mk!*sq negsq(physop2sq physopapply list(cadr op,wave))
        else if car op memq '(plus difference) then <<
               for each y in cdr op do <<
                  res:= nconc(res,list(physopapply list(y,wave)));
                  if !*hardstop then flg:= t;
                  !*hardstop := nil;>>;
               if flg then !*hardstop := t;
               res := reval3 ((car op) . res)  >>
        else if car op memq '(dot commute anticommute expt) then
                   res := physopapply list(physopsim!* op,wave)
        else if car op eq 'quotient then
                if physopp caddr op then
                    res := physopapply list(physopsim!* op,wave)
                else res := mk!*sq quotsq(physop2sq
                   physopapply list(cadr op,wave),physop2sq caddr op)
        else if car op eq 'times then
               <<op := reverse cdr op;
                 while op do 
                       %and not !*hardstop do %%% E.S.
                    << x := car op; op := cdr op;
                       wave := physopapply list(x,wave) >>;
                       if !*hardstop then
                          if null op then res := wave
                          else << x:= physopaeval wave;
                                  op := 'times . reverse op;
                                  while x do
                                    << y := car x; x := cdr x;
                                       if listp y and
                                       (y := assoc('opapply,y)) then
                                         << wavefct := list('opapply,
                                             nconc(op,list(cadr y)),
                                               caddr y);
                                        wave := subst(wavefct,y,wave);
                                         >>;
                                    >>;
                                  res := wave;
                               >>
                       else res := wave;
                >>
             else rederr2('opapply, "invalid operator to opapply")
% special hack here for unit operator  0.99c
else if op = 'unit then res := mk!*sq physop2sq wave
else if physopp wave or eqcar(wave,'opapply) or %%% E.S.
        (flagp(car wave,'physopmapping) and statep!* cdr wave) then
             <<x := opmtch!* list('opapply,op,wave);
               if null x then x := physopadj list(
                        opmtch!* list('opapply,adjp wave,adjp op));
               if null x then <<!*hardstop := t;
                                res := mk!*sq !*k2q
                                       list('opapply,op,wave); >>
               else  res := mk!*sq physop2sq x;
             >>
     else << x := wave; wave := nil;
             while x do <<
                 wavefct := car x; x := cdr x;
                 if statep!*  wavefct then wave := nconc(wave,
                     list(physopaeval physopapply list(op,wavefct)))
                 else wave := nconc(wave,list(wavefct));
                 if !*hardstop then flg := t;
                  !*hardstop := nil >>;
             if flg then !*hardstop := t;
             res := mk!*sq physop2sq wave;
          >>;
return res
end;

put('opapply,'phystypefn,'getphystypestate);

symbolic procedure getphystypestate  args;
if statep!* car args and statep!* cadr args then nil
else 'state;

symbolic procedure statemult(u,v); % recoded 0.99c
% u and v are states
% returns product of these
begin scalar x,y,res,flg;
if not (statep!* u or statep!* v) then
      rederr2 ('statemult,"invalid args to statemult");
if eqcar(v,'opapply) then
             return expectval(u,cadr v,caddr v);
if eqcar(u,'opapply) then
        return expectval(cadr u,caddr u,v);
u := physopaeval physopsim!* u;
v := physopaeval physopsim!* v;
if physopp u then
   if physopp v then
   <<
      x := opmtch!* list('opapply,u,v);
      if x then  res := physop2sq aeval x
      else
      <<
         x:= opmtch!* list('opapply,v,u);
         if null x then
         <<
             !*hardstop := t;
             res:= !*k2q list('opapply,u,v)
         >>
         else  res := physop2sq aeval compconj x
      >>;
     >>
   else
   <<
       x := deletemult!* !*collectphysops v;
       for each y in x do
       <<
          v := subst(physopaeval statemult(u,y),y,v);
          if !*hardstop then flg := t;
          !*hardstop := nil;
       >>;
       if flg then !*hardstop := t;
       res := physop2sq v;
   >>
else
<<
    x := deletemult!* !*collectphysops u;
    for each y in x do
    <<
        u := subst(physopaeval statemult(y,v),y,u);
        if !*hardstop then flg := t;
        !*hardstop := nil;
    >>;
    if flg then !*hardstop := t;
    res := physop2sq u;
>>;
return mk!*sq res
end;

symbolic procedure expectval(u,op,v);
% u and v are states
% calculates the expectation value < u ! op ! v >
% tries to apply op first on v, then on u
% PHYSOPAPPLY is used rather than STATEMULT to multiply
% resulting states together because of more general definition
begin scalar x,y,z,flg,res;
op := physopaeval physopsim!* op;
if null getphystype op then
   return mk!*sq multsq(physop2sq op,physop2sq physopapply list(u,v));
if physopp op then
   <<x := physopapply list(op,v);
     if !*hardstop then
       << !*hardstop := nil;
          x:= physopapply list(u,op);
          res := if !*hardstop then mk!*sq
                    !*k2q list('opapply,list('opapply,u,op),v)
                 else physopapply list(x,v) >>
     else res:= physopapply list(u,x)
   >>
else if car op eq 'minus then
      res := mk!*sq negsq(physop2sq expectval(u,cadr op,v))
else if car op eq 'quotient then
      if physopp caddr op then res := expectval(u,physopsm!* op,v)
      else res := mk!*sq quotsq(physop2sq expectval(u,cadr op,v),
      physop2sq caddr op)
else if car op memq '(dot commute anticommute expt) then
        res := expectval (u,physopsm!* op,v)
else if car op memq '(plus difference) then
   << for each y in cdr op do
          << x:=nconc(x,list(expectval(u,y,v)));
             if !*hardstop then flg:= !*hardstop ;
             !*hardstop := nil >>;
      if flg then !*hardstop := t;
      res :=  reval3 ((car op) . x);
   >>
else if car op eq 'times then
   << x := physopapply list(op,v);
      if not !*hardstop then return physopapply list(u,x);
      x := cdr op;
      while (x and !*hardstop and not flg) do
      << y:=car x; x := cdr x;
         if not getphystype y then << v:= physopapply list(y,v);
                                       y := v;>>
         else << !*hardstop := nil;
                 z:= physopapply list(u,y);
                 if !*hardstop then
                    << flg := T; x := y . x;
                       y := if null cdr x then list('opapply,car x,
                               physopaeval v)
                            else list('opapply,('times . x),
                               physopaeval v); >>
                 else << u:= z;
                         y:= if null x then v
                             else if null cdr x then
                         physopapply list(car x, physopaeval v)
                                  else physopapply
                           list(('times . x),physopaeval v) >>
              >>
      >>;
     res := if !*hardstop then mk!*sq !*k2q list('opapply,
                 physopaeval u,physopaeval y)
            else physopapply list(u,y);
   >>
else rederr2('expectval, "invalid args to expectval");
return res
end;


symbolic procedure compconj u;
% dirty and trivial implementation of
% complex conjugation of everything (hopefully);
% not yet tested for arrays
begin scalar x;
   if null u or numberp u then return u
   else if idp u and (x:=get(u,'rvalue)) then <<
   x:=subst(list('minus,'I),'I,x);
   put(u,'rvalue,x); return u >>
   else return subst(list('minus,'I),'I,u)
end;

 % --------------  adjoint of operators ---------------------
physopfn('adj, 'physopadj);

symbolic procedure  physopadj arg;
begin scalar rht,rhtype,x,n,res;
rht :=  physopaeval physopsim!* car arg;
rhtype := physopp rht;
if rhtype then return mk!*sq !*k2q physopsm!* adjp rht
else <<
  if not getphystype rht then res := aeval compconj rht
  else if car rht eq 'minus then
        res := mk!*sq negsq(physop2sq physopadj list(cadr rht))
  else if car rht eq 'difference then res := mk!*sq addsq(
        physop2sq physopadj list(cadr rht),negsq(physop2sq
                           physopadj list(caddr rht)))
  else if car rht eq 'plus then <<
     x := for each y in cdr rht collect physopadj list(y);
     res := reval3 ('plus . x)  >>
  else if car rht  eq 'quotient then <<
       if not getphystype cadr rht then
          rederr2('physopadj, "invalid argument to ADJ")
       else res := mk!*sq quotsq(physop2sq
            physopadj list(cadr rht),physop2sq caddr rht)  >>
  else if car rht  eq 'times  then <<
      x:= for each y in cdr rht collect physopadj list(y);
      res := physoptimes reverse x >>
  else if flagp(car rht,'physopmapping) then
       res := mk!*sq !*k2q list(car rht, physopaeval physopadj cdr rht)
  else  res :=physopadj list(physopsim!* rht) >>;
return res
end;

Put('adj,'phystypefn,'getphystypecar);

symbolic procedure adj2 u;
begin scalar x,y;
if not physopp u then rederr2('adj2, "invalid argument to adj2");
if u = 'unit then return u;
y:= if idp u then u else car u;
x := reverse explode y;
x := intern compress nconc(reverse x,list('!!,'!+));
put(y,'adjoint,x); %1.01
put(x,'adjoint,y); %1.01
put(x,'physopname,x); % 1.02
if not physopp x then << put(x,'rtype,'physop);
                         put(x,'phystype,get(y,'phystype));
                         put(x,'psimpfn,'physopsimp);
                         put(x,'tensdimen,get(y,'tensdimen));
              defoporder!* := nconc(defoporder!*,list(x));
              oporder!* := nconc(oporder!*,list(x));
              physoplist!* := nconc(physoplist!*,list(x));
                       >>;
if idp u then return x
else return x . cdr u
end;

symbolic procedure invadj u;  %new 1.01
% create the inverse adjoint op
begin scalar x,y;
if not physopp u then rederr2('invadj, "invalid argument to invadj");
if u = 'unit then return u;
y:= if idp u then u else car u;
x := reverse explode y;
x := intern compress nconc(reverse x,list('!!,'!+,'!!,'!-,'!1));
put(x,'adjoint,get(y,'inverse));
put(x,'inverse,get(y,'adjoint));
put(get(y,'inverse),'adjoint,x);
put(get(y,'adjoint),'inverse,x);
put(x,'physopname,get(y,'adjoint)); % 1.02
if not physopp x then << put(x,'rtype,'physop);
                         put(x,'phystype,get(y,'phystype));
                         put(x,'psimpfn,'physopsimp);
                         put(x,'tensdimen,get(y,'tensdimen));
              physoplist!* := nconc(physoplist!*,list(x));
                       >>;
if idp u then return x
else return x . cdr u
end;

symbolic procedure adjp u;  %recoded 1.01
% special cases
if u = 'unit then  u
else if atom u then get(u,'adjoint)
else if (car u = 'comm) then
       list('comm,adjp caddr u,adjp cadr u)
else if (car u = 'anticomm) then
     list('anticomm,adjp cadr u,adjp caddr u)
else get(car u,'adjoint) . cdr u;

% --- end of arithmetic routines ---------------------

% ---- procedure for handling let assignements ------

symbolic procedure physoptypelet(u,v,ltype,b,rtype);
% modified version of original typelet
% General function for setting up rules for PHYSOP expressions.
% LTYPE is the type of the left hand side U, RTYPE, that of RHS V.
% B is a flag that is true if this is an update, nil for a removal.
% updated 101290 mw
%do not check physop type in prog exprs on the rhs
begin scalar x,y,n,u1,v1,z,contract;
 if not physopp u and getphystype u then goto c; % physop expr
 u1 := if atom u then u else car u;
 if ltype then
          if rtype = ltype then go to a
          ELSE IF NULL B OR ZEROP V OR (LISTP V AND ((CAR V = 'PROG)
                                           OR (CAR V = 'COND))) %1.01
                  or ((not atom u) and (car u = 'opapply)) then
                      return physopset(u,v,b)
               else rederr2('physoptypelet,
                     list("physop type mismatch in assignement ",
                               u," := ",v))
 else if null (x:= getphystype v) then return physopset(u,v,b)
 else << if x = 'scalar then scalop u1;
         if x = 'vector then vecop u1;
         if x = 'state then state u1;
         if x = 'tensor then tensop list(u1,get(v,'tensdimen));
         ltype := rtype >>;
A: if b and (not atom u or flagp(u,'used!*)) then rmsubs();
% perform the assignement
   physopset(u,v,b);
% phystype checking added 1.01
   if b and (getphystype u neq getphystype v) then
               rederr2('physoptypelet,
                     list("physop type mismatch in assignement ",
                               u," <=> ",v));
% special hack for commutators here
      if (not atom u) and (car u = 'comm) then
       physopset(list('comm,adjp caddr u,adjp cadr u),list('adj,v),b);
      if (not atom u) and (car u = 'anticomm) then
       physopset(list(car u,adjp cadr u,adjp caddr u),list('adj,v),b);
   if null (x := getphystype u)  or (x = 'state) or (x = 'scalar)
           then return;
% we have here to add additional scalar let rules for vector
% and tensor operators with arbitrary indices
   u1:=u;v1:=v;
   if (x eq 'vector)  or (x eq 'tensor) then
           << x := collectphysops u;
              for each z in x do
                u1:= subst(insertfreeindices(z,nil),z,u1);
              x := collectphysops v;
              for each z in x do
                  v1:= subst(insertfreeindices(z,nil),z,v1) >>;
   physoptypelet(u1,v1,ltype,b,rtype);
   return;
C:
% this is for more complicated let rules involving more than
% one term on the lhs
% special hack here to handle let rules involving elementary
% OPAPPLY relations
   if car u = 'opapply then  return physopset(u,v,b);
% step 1: do all physop simplifications on lhs
%  we set indflg!* for dot product simplifications on the lhs
   !*indflg:= T; indcnt!* := 0;
   contract := !*contract2; !*contract2 := T;
   u := physopsm!* u;
   !*indflg := nil; indcnt!* := 0; !*contract2 := contract;
% check correct phystype
   x := getphystype u;
   y := getphystype v;
   if b and ((not (y or zerop v)) or (y and (x neq y))) then
              rederr2 ('physoptypelet,"phystype mismatch in LET");
% step 2 : transform back in ae
   u := physopaeval u;
%  write "u= ",u; terpri();
% ab hier neu
% step3 : do some modifications in case of a sum or difference on the lh
   if car u = 'PLUS then
   <<
      u1 := cddr u;
      u := cadr u;
      v := list('plus,v);
      for each x in u1 do
      <<
         x := list('minus,x);
         v := append(v,list(x));
      >>;
   >>;
   if car u = 'DIFFERENCE then
   <<
       u1:= cddr u;
       u:= cadr u;
       v := append(list('plus,v),list(u1));
   >>;
   if car U = 'MINUS then
   <<
       u := cadr u;
       v := list('minus,v);
   >>;
% step 4: add the rule to the corresponding list
% expression may still contain quotients and expt
   if car u ='EXPT then
   <<
       u := cadr u . caddr u;
       powlis1!* :=  xadd!*(u .
                            list(nil . (if mcond!* then mcond!* else t),
                                  v,nil), powlis1!*,b)
   >>
   else if car u = 'quotient then
   <<
       v:= list('times,v,caddr u);
       physoptypelet(cadr u,v,ltype,b,rtype);
   >>
   else   % car u = times
   <<
       u1 := nil;
       for each x in cdr u do
       <<
           if car x= 'expt then
              u1 := append(u1,list(cadr x . caddr x))
           else if car x = 'quotient then
           <<
              v:= list('times,v,caddr x);
              u1 := append(u1,
                           list(if cadr x = 'expt then
                                   (caddr x . cadddr x)
                                else (cadr x . 1)));
           >>
           else u1 := append(u1,list(x . 1));
       >>;
       !*match := xadd!*(u1 . list(nil .
                                   (if mcond!* then mcond!* else t),
                                 v,nil), !*match,b);
   >>;
   return;
   end;

symbolic procedure physopset(u,v,b);
% assignement procedure for physops
% special hack for assignement of unresolved physop expressions
begin
   if not atom u  then put(car u,'opmtch,xadd!*(cdr u .
                        list(nil . (if mcond!* then mcond!* else t),
                         v,nil), get(car u,'opmtch),b))
   else if b then
           if physopp u then put(u,'rvalue,physopsim!* v)
           else put(u,'avalue,list('scalar,
                    list('!*sq,cadr physopsim!* v,not !*hardstop)))
        else if not member(u,specoplist!*) then
                << remprop(u,'rvalue);
                   remprop(u,'opmtch);
                  >>;
   !*hardstop := nil;
end;

  symbolic procedure clearphysop u;
% to remove physop type from an id
  begin scalar y;
 for each x in u do <<
   if not (physopp x and idp x) then rederr2('clearphysop,
             list("invalid argument ",x," to CLEARPHYSOP"));
   y := invp x;
   remprop(y,'rtype);
   remprop(y,'tensdimen);
   remprop(y,'phystype);
   remprop(y,'psimpfn);
   remprop(y,'inverse); %1.01
   remprop(y,'adjoint);  %1.01
   remprop(y,'rvalue);  % 1.01
   oporder!* := delete(y,oporder!*);
   defoporder!* := delete(y,defoporder!*);
   physoplist!* := delete(y,physoplist!*);
   y:= adjp x;
   remprop(y,'rtype);
   remprop(y,'tensdimen);
   remprop(y,'phystype);
   remprop(y,'psimpfn);
   remprop(y,'inverse); %1.01
   remprop(y,'adjoint);  %1.01
   remprop(y,'rvalue);  % 1.01
   oporder!* := delete(y,oporder!*);
   defoporder!* := delete(y,defoporder!*);
   physoplist!* := delete(y,physoplist!*);
  remprop(x,'rtype);
  remprop(x,'tensdimen);
  remprop(x,'phystype);
  remprop(x,'psimpfn);
   remprop(x,'inverse); %1.01
   remprop(x,'adjoint);  %1.01
   remprop(x,'rvalue);  % 1.01
  oporder!* := delete(x,oporder!*);
  defoporder!* := delete(x,defoporder!*);
  physoplist!* := delete(x,physoplist!*);
   >>;
return nil
end;

 Rlistat '(clearphysop);

%------ procedures for printing out physops correctly ---------

% we modify the standard MAPRINT routine to get control
% over the printing of PHYSOPs


%**** This section had to be modified for 3.4  **********************
symbolic procedure physoppri u; % modified 3.4
begin scalar x,y,z,x1;
x :=  if idp u then u else car u;
y := if idp u then nil else cdr u;
trwrite(physoppri,"x= ",x," y= ",y,"nat= ",!*nat," contract= ",
        !*contract);
if !*nat and not !*contract then go to a;
% transform the physop name in a string in order not to loose the
% special characters
x:= compress append('!" . explode x,list('!"));
 prin2!* x;
 if y then << prin2!* "(";
              obrkp!* := nil;
              inprint('!*comma!*,0,y);
              obrkp!* := t;
              prin2!* ")"  >>;
 return u;
a:
x := reverse explode x;
if length(x) > 2 then
   if cadr x = '!- then <<z :=  compress list('!-,'!1);
                          x :=  compress  reverse pnth(x,4); >>
   else if car x = '!+ then << z:='!+;
                               x:= compress reverse pnth(x,3); >>
        else x := compress reverse x
else x := compress reverse x;
x:= compress append('!" . explode x,list('!"));
x1 := if y then  x . y else x;
trwrite(physoppri,"x= ",x," z= ",z," x1= ",x1);
% if z then exptpri(get('expt,'infix),list(x1,z))
% the following is 3.4
if z then  exptpri(list('expt,x1,z),get('expt,'infix))
else << prin2!* x;
        if y then << prin2!* "(";
                     obrkp!* := nil;
                     inprint('!*comma!*,0,y);
                     obrkp!* := t;
                     prin2!* ")"  >>
     >>;
return u
end;



symbolic procedure maprint(l,p!*!*); %3.4 version
   begin scalar p,x,y;
        p := p!*!*;     % p!*!* needed for (expt a (quotient ...)) case.
        if null l then return nil
         else if physopp l then return apply1('physoppri,l)
         else if atom l
          then <<if not numberp l or (not(l<0) or p<=get('minus,'infix))
                   then prin2!* l
                  else <<prin2!* "("; prin2!* l; prin2!* ")">>;
                 return l >>
         else if stringp l then return prin2!* l
         else if not atom car l then maprint(car l,p)
         else if ((x := get(car l,'pprifn)) and
                   not(apply2(x,l,p) eq 'failed)) or
                 ((x := get(car l,'prifn)) and
                   not(apply1(x,l) eq 'failed))
          then return l
         else if x := get(car l,'infix) then <<
           p := not(x>p);
           if p then <<
             y := orig!*;
             prin2!* "(";
             orig!* := if posn!*<18 then posn!* else orig!*+3 >>;
% (expt a b) was dealt with using a pprifn sometime earlier than this
           inprint(car l,x,cdr l);
           if p then <<
              prin2!* ")";
              orig!* := y >>;
           return l >>
         else prin2!* car l;
        prin2!* "(";
        obrkp!* := nil;
        y := orig!*;
        orig!* := if posn!*<18 then posn!* else orig!*+3;
        if cdr l then inprint('!*comma!*,0,cdr l);
        obrkp!* := t;
        orig!* := y;
        prin2!* ")";
        return l
    end;

%  ******* end of 3.4 modifications  ********************
% ------- end of module printout  -------------------------

% ------------- some default declarations -------------------
% this list contains operators which when appearing in expressions
% have unknown properties  (unresolved expressions)
specoplist!* := list('dot,'comm,'anticomm,'opapply);
% unit,comm and anticomm operators
put('comm,'rtype,'physop);
put('comm,'phystype,'scalar);
put('comm,'psimpfn,'commsimp);
put('anticomm,'rtype,'physop);
put('anticomm,'phystype,'scalar);
put('anticomm,'psimpfn,'commsimp);
physoplist!* := list('comm,'anticomm);
scalop 'unit;
flag ('(unit comm anticomm opapply),'reserved);

endmodule;

end;

