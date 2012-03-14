module control;

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


fluid '(!*revpri);

global '(!:flaglis !:proplis indices!*);

switch distribute;

% functions which offer a BETTER CONTROL on various objects.

% 1. BOOLEAN functions.

symbolic procedure nordp(u,v);
% TRUE if a>b, FALSE if a=<b.
  not ordp(u,v);

symbolic procedure depvarp(u,v)$
% V is an idf. or a kernel$
   if depends(u,v) then t else nil$

symbolic procedure alatomp(u)$
% U is any expression . Test if U is an idf. whose only value is its
% printname or another atom$
 fixp u or idp u$

symbolic procedure alkernp u$
% U is any expression . Test if U is a kernel.
 not stringp u  and kernp(simp!* u);

symbolic procedure precp(u,v)$
% Tests if the operator U has precedence over the operator V.
 begin integer nn$scalar uu,vv,aa$
    uu:=u$ vv:=v$aa:=preclis!*$
    if or(not(uu member aa),not(vv member aa)) then return nil$
    nn:=lpos(u,aa)$;
    nn:=nn-lpos(v,aa)$
    if nn geq 0 then return t else return nil
end;

flag('(null idp flagp nordp alatomp alkernp precp
                   depvarp stringp ),'boolean);

% THE declaration below is useful for "teaching" purpose.

flag('(alatomp precp depvarp alkernp depatom ) ,'opfn);


% 2. MISCELLANEOUS functions.

symbolic procedure korderlist;
% gives a list of the user defined internal order of the
% indeterminates. Just issue KORDERLIST; to get it.
kord!*;

flag('(korderlist), 'opfn);

put('korderlist,'stat,'endstat);

symbolic procedure remsym u;
% ALLOWS TO ELIMINATE THE DECLARED SYMMETRIES.
for each j in u do
  if flagp(j,'symmetric) then remflag(list j,'symmetric) else
  if flagp(j,'antisymmetric) then remflag(list j,'antisymmetric);

put('remsym,'stat,'rlis);

symbolic procedure listofvars u $
if null u  or numberp u  then nil else
if atom u then list u else
varsinargs if eqcar(u,'list) then cdr reval u  else cdr u$

symbolic procedure varsinargs(u)$
if null u then nil else
append(listofvars car u,varsinargs cdr u)$

symbolic procedure rfuncvar(u)$
% U is an arbitrary expression
% Gives a list which contains all the variables whom U depends
% in an ARBITRARY order$
<<if atom (u:=reval car u) then
if not flagp(u,'reserved) then
        if depatom u neq u  then depatom u else nil
else nil else
 begin scalar wi,aa$
 aa:=listofvars(u)$
 if null cdr aa then return
      if flagp(car aa,'reserved) or flagp(car aa,'constant)
      then nil else car aa
 else aa:=list2set aa $ wi:=aa$
  while wi do if flagp(car wi ,'reserved) then
    <<aa:=delete(car wi ,aa)$ wi:=cdr wi >> else wi:=cdr wi $
  return aa:='list . aa end >>;

put('funcvar,'psopfn ,'rfuncvar);

flag('(e i),'reserved);

symbolic procedure implicit u;
if atom u then u else
 begin scalar prf;
 prf:=car u;
 if get(prf,'simpfn) neq 'simpiden  then
                  rederr list(u,"must be an OPERATOR");
 remprop(car u,'simpfn);
 depl!*:=union(list (car u . reverse
           for each y in cdr u collect implicit y),depl!*);
return prf end;

symbolic procedure depatom a$
%Gives a list of variables declared in DEPEND commands whom A depends
%A must be an atom$
    if not atom a then rederr("ARGUMENT MUST BE AN ATOM") else
         if null assoc(a,depl!*) then a  else
                            'list . reverse cdr assoc(a,depl!*);
flag('(depatom),'opfn);

symbolic procedure explicit u$
% U is an atom. It gives a function named A which depends on the
% variables detected by DEPATOM and this to all levels$
begin scalar aa$
    aa:=depatom u $
    if aa = u then  return u$
    put(u,'simpfn,'simpiden)$
    return u . (for each x in cdr aa collect explicit x) end$

flag('(implicit explicit),'opfn);

symbolic procedure simplify u;
% Enforces simplifications if necessary.
% u is any expression.
 mk!*sq resimp simp!* reval u;

flag('(simplify),'opfn);

% This function is for dummy.red:

rlistat('(remnoncom));

symbolic procedure remnoncom u;
<<for each x in u do
remflag(list x,'noncom);t>>;

% To have a better control on the HEPHYS package.

symbolic procedure remvector u;
for each x in u do <<remprop(x,'rtype); remflag(list x,'used!*); 0>>;

symbolic procedure remindex u;
begin;
  for each x in u do <<remprop(x,'rtype); indices!*:=delete(x,indices!*);
                    remflag(list x, 'used!*)>>;
 return t
end;

rlistat('(remvector remindex));

symbolic procedure mkgam(u,v);
% u is supposed to be an idp. v equals either t or another idp.
 if v neq t then
 <<remflag(list u,'noncom); remprop(u,'simpfn);
     if v eq 'op then  put(u,'simpfn,'simpiden); remflag(list u, 'used!*)>>
 else
 <<clear u; clearop u; put(u,'simpfn,'simpgamma); flag(list u,'noncom); t>>;

symbolic operator getmas, mkgam;


% 3. Control of SWITCHES.

symbolic procedure switches;
%This procedure allows to  see the values of the switches chosen.
<<terpri();
prin2 "      **** exp:=";prin2 !*exp;prin2 " .................... ";
prin2 "allfac:= ";prin2 !*allfac;prin2 " ****";terpri(); terpri();
prin2 "      **** ezgcd:=";prin2 !*ezgcd;prin2 " ................. ";
prin2 "gcd:= ";prin2 !*gcd;prin2 " ****";terpri();terpri();
prin2 "      **** mcd:=";prin2 !*mcd;prin2 " ....................... ";
prin2 "lcm:= ";prin2 !*lcm;prin2 " ****";terpri();terpri();
prin2 "      **** div:=";prin2 !*div;prin2 " ................... ";
prin2 "rat:= ";prin2 !*rat;prin2 " ****";terpri();terpri();
prin2 "      **** intstr:=";prin2 !*intstr;prin2 " ........... ";
prin2 "rational:= ";prin2 !*rational;prin2 " ****";terpri();terpri();
prin2 "      **** precise:=";prin2 !*precise;prin2 " ............. ";
prin2 "reduced:= ";prin2 !*reduced;prin2 " ****";terpri();terpri();
prin2 "      **** complex:=";prin2 !*complex;prin2 " ....... ";
prin2 "rationalize:= ";prin2 !*rationalize;
prin2 " ****";terpri();terpri();
prin2 "      **** factor:= "; prin2 !*factor;prin2 " ....... ";
prin2 "combineexpt:= ";prin2 !*combineexpt;
                                  prin2 " ****";terpri();terpri();
prin2 "      **** revpri:= "; prin2 !*revpri;prin2 " ........ ";
prin2 "distribute:= "; prin2 !*distribute;prin2 " ****";>>;

symbolic procedure switchorg$
%It puts all switches relevant to current algebra calculations to
% their initial values.
<< !*exp:=t;
   !*allfac:=t;
   !*gcd:=nil;
   !*mcd:=t;
   !*div:=nil;
   !*rat:=nil;
   !*distribute:=nil;
   !*intstr:=nil;
   !*rational:=nil;
   !*ezgcd:=nil;
   !*ratarg:=nil;
   !*precise:=t;
   !*complex:=nil;
   !*heugcd:=nil;
   !*lcm:=t;
   !*factor:=nil;
   !*ifactor:=nil;
   !*rationalize:=nil;
   !*reduced:=nil;
   !*savestructr:=nil;
   !*combineexpt:=nil;
   !*revpri:=nil>>;

flag('(switchorg ),'opfn)$

deflist('((switches endstat) (switchorg endstat) ),
           'stat)$

% 4. Control of USER DEFINED objects.

% The procedures below allow to extract from the history of the
% INTERACTIVE run in the ALGEBRAIC mode the data previously
% defined by the user.
% It DOES NOT give insights on operations done
% in the SYMBOLIC mode.

symbolic procedure remvar!:(u,v)$
% This procedure traces and clear both assigned or saved scalars and
% lists.
 begin scalar buf,comm,lv;
     buf:=inputbuflis!*;
     for each x in buf do if not atom (comm:=caddr x)
                                 and car comm = 'setk then
  begin scalar obj;
  l1: if null cddr comm  or car comm eq 'prog then return lv;
         obj:=cadadr comm;
       if  gettype obj  eq v then
     lv:=adjoin(obj,lv);
         comm:=caddr comm;
         go to l1  end;
 if null u then
    <<for each x in lv do clear x; return t>> else return lv
     end;

flag('(displaylst displayscal),'noform);

symbolic  procedure displayscal;
% Allows to see all scalar variables which have been assigned
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file;
 union(remvar!:(t,'scalar),remsvar!:(t,'scalar));

symbolic  procedure displaylst$
% Allows to see all list variables which have been assigned
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file;
  union(remvar!:(t,'list),remsvar!:(t,'list)) ;

symbolic procedure clearscal$
% Allows to clear all scalar variables introduced
% DIRECTLY ON THE CONSOLE;
<<remvar!:(nil,'scalar);remsvar!:(nil,'scalar)>>$

symbolic procedure clearlst$
% Allows to clear all list variables introduced
% DIRECTLY ON THE CONSOLE;
<<remvar!:(nil,'list);remsvar!:(nil,'list)>>;

symbolic procedure remsvar!:(u,v)$
 begin scalar buf,comm,lsv,obj;
     buf:= inputbuflis!*;
     for each x in buf do
      if not atom (comm:=caddr x) and car comm eq 'saveas then
         if  v eq t then
             if gettype (obj:=cadr cadadr comm)
                  member list('scalar,'list,'matrix,'hvector,'tvector)
                  then lsv:=adjoin(obj,lsv)
             else nil
         else if v eq gettype (obj:=cadr cadadr comm)
                  then lsv:=adjoin(obj,lsv);
%     lsv:= !:mkset lsv$
    if null u then
    <<for each x in lsv do clear x$ return t>> else return lsv
     end;

flag('(displaysvar),'noform);

symbolic  procedure displaysvar;
% Allows to see all variables created by SAVEAS.
remsvar!:(t,t) ;

symbolic  procedure clearsvar;
% Allows to clear  all variables created.
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file.
remsvar!:(nil,t);

symbolic procedure rema!:(u);
% This function works to trace or to clear arrays.
 begin scalar buf,comm,la$
     buf:=inputbuflis!*$
     for each x in buf do if not atom (comm:=caddr x) and
                car comm eq 'arrayfn then
     begin scalar arl,obj;
         arl:=cdaddr comm;
     l1: if null arl then return la else
           if gettype (obj:=cadadr car arl ) eq 'array then
             la:=adjoin(obj,la);
         arl:=cdr arl$
         go to l1  end$
  if null u then
   <<for each x in la do clear x$ return t>> else return la
 end;

flag('(displayar),'noform);

symbolic  procedure displayar;
% Allows to see all array variables created.
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file.
 rema!:(t)$

symbolic procedure clearar;
% Allows to clear array variables introduced
% DIRECTLY ON THE CONSOLE;
rema!:(nil)$

symbolic procedure remm!:(u)$
% This function works to trace or to clear matrices. Be CAREFUL to use
% the declaration MATRIX on input (not m:=mat(...) directly).
% declaration MATRIX ..
%x ==> (97 SYMBOLIC (MATRIX (LIST (LIST (QUOTE MM) 1 1))))
% Declaration MM:=MAT((...))
% x==>(104 ALGEBRAIC
%       (SETK (QUOTE M2) (AEVAL (LIST (QUOTE MAT) (LIST 1) (LIST 1)))))
 begin scalar buf,comm,lm;
     buf:= inputbuflis!*;
  for each x in buf do if  not atom (comm:=caddr x) and
                           car comm eq 'matrix then
      begin scalar lob,obj;
      lob:=cdadr comm;
   l1: if null lob then return lm else
       if gettype(obj:=if length car lob = 2 then cadr car lob else
                    cadadr car lob) then
          lm:=adjoin(obj,lm);
      lob:=cdr lob;
      go to l1  end$
  lm :=union(lm,remvar!:(t,'matrix));
 if null u then
 <<for each x in lm do clear x$ return t>> else return lm
end;

flag('(displaymat),'noform);

symbolic procedure displaymat$
% Allows to see all variables of matrix type
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file;
union( remm!:(t),remsvar!:(t,'matrix));

symbolic procedure clearmat$
% Allows to clear all user variables introduced
% DIRECTLY ON THE CONSOLE;
<<remm!:(nil);remsvar!:(nil,'matrix)>>;

symbolic procedure remv!:(u)$
% This function works to trace or to clear vectors.
 begin scalar buf,av$
     buf:= inputbuflis!*$
  for each x in buf do if not atom (x:=caddr x) and
              car x member list('vector,'tvector,'index)
         then
     begin scalar uu,xx$
         uu:=cdadr x$
     l1: if null uu then return av else
           if gettype(xx:=cadar uu) or get(xx,'fdegree) then
             av:=adjoin(xx,av);
         uu:=cdr uu$
         go to l1  end$
  if null u then
   <<for each x in av do clear x$ return t>> else return av
 end$

flag('(displayvec),'noform);

symbolic  procedure displayvec$
% Allows to see all variables which have been assigned
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file;
union(remv!:(t),union(remsvar!:(t,'hvector),remsvar!:(t,'tvector)) );

symbolic procedure clearvec$
% Allows to clear all user variables introduced
% DIRECTLY ON THE CONSOLE;
<<remv!:(nil);remsvar!:(nil,'hvector);remsvar!:(nil,'tvector)>>;

symbolic procedure remf!:(u)$
% This function works to trace or to clear forms.
 begin scalar buf,av$
     buf:= inputbuflis!*$
     for each x in buf do if not atom (x:=caddr x) and
                              car x eq 'pform then
     begin scalar uu,xx$
         uu:=cdadr x$
     l1: if null uu then return av else
           if get(xx:=cadadr cdar uu ,'fdegree) or
             (not atom xx and get(xx:=cadr xx,'ifdegree))
    then
             av:=adjoin(xx,av);
         uu:=cdr uu$
         go to l1  end$
  if null u then
   <<for each x in av do clear x$ return t>> else return av
 end$

flag('(displayform),'noform);

symbolic  procedure displayform$
% Allows to see all variables which have been assigned
% independently DIRECTLY ON THE CONSOLE. It does not work
% for assignments introduced THROUGH an input file;
union(remf!:(t),remvar!:(t,'pform));
symbolic procedure clearform$
% Allows to clear all user variables introduced
% DIRECTLY ON THE CONSOLE;
<<remf!:(nil);remvar!:(nil,'pform)>>;

symbolic procedure clear!_all;
<<remvar!: (nil,'scalar); remvar!:(nil,'list); remvar!:(nil,'pform);
  remsvar!:(nil,t);rema!: nil;remv!: nil;remm!: nil; remf!: nil ;t>>;

symbolic procedure show u;
begin u:=car u;
           if u eq 'scalars then
              return write "scalars are: ", displayscal()
           else
           if u eq 'lists  then
              return write "lists are: ", displaylst()
           else
           if u eq 'arrays then
               return write "arrays are: ", displayar()
           else
           if u eq 'matrices then
                       return write "matrices are: ",displaymat()
           else
           if u member list('vectors,'tvectors,'indices)  then
                       return  write "vectors are: ", displayvec()
           else
           if u eq 'forms then
                        return write "forms are: ", displayform()
           else
           if u eq 'all then for each i in
            list('scalars,'arrays,'lists,'matrices,'vectors,'forms) do
                        <<show list i;lisp terpri()>>;
end;

put('show,'stat,'rlis);

symbolic procedure suppress u;
begin u:=car u;
            if u member list('vectors,'tvectors,'indices) then
                       return clearvec() else
            if u eq 'variables then return clearvar() else
            if u eq 'scalars then return clearscal() else
            if u eq 'lists then return clearlst() else
            if u eq 'saveids  then return clearsvar() else
            if u eq 'matrices then return clearmat() else
            if u eq 'arrays then return clearar() else
            if u eq 'forms then return clearform() else
            if u eq 'all then return clear!_all() end;

put('suppress,'stat,'rlis);


% 5. Complementary means to CLEAR operators and functions.

symbolic procedure clearop u;
<<clear u; remopr u; remprop(u , 'kvalue);remprop(u,'klist)$
  for each x in !:flaglis do
            if u eq car x then putflag(u,cadr x,0) else nil;
  for each x in !:proplis do
            if u eq car x then putprop(u,cadr x,caddr x,0)
                              else nil;
     remflag(list u,'used!*); t>>;

flag('(clearop),'opfn);

symbolic procedure clearfunctions u$
% U is any number of idfs. This function erases properties of  non
% protected functions described by the idfs.
% It is very convenient but is dangerous if applied to the
% basic functions of the system since most of them  are NOT protected.
% It clears all properties introduced by PUTFLAG, PUTPROP and DEPEND.
begin scalar uu,vv$
l1: uu:=car u$
    vv:=cdr rdisplayflag (list  uu )$
    if flagp(uu,'lose) then go to l2 else
    << terpri();spaces(5)$
       write "*** ",uu," is unprotected : Cleared ***"$
       followline(0)>>$
  for each x in !:proplis do
            if u eq car x then putprop(u,cadr x,caddr x,0)
                              else nil;
    remprop('uu,'!*lambdalink);
  if get(uu,'simpfn) then <<clearop uu; remprop(uu,'!:ft!:);
      remprop(uu,'!:gf!:)>> ;
    remprop(uu,'psopfn);
    remprop(uu,'expr);
  if get(uu,'subr) then remd uu$
    remprop(uu,'stat);
    remprop(uu,'dfn);
    remprop(uu,'rtypefn);
    remprop(uu,'number!-of!-args);
    remflag(list uu,'opfn)$
    remflag(list uu,'full)$
    remflag(list uu,'odd)$
    remflag(list uu,'even)$
    remflag(list uu,'boolean)$
    remflag(list uu,'used!*)$
    for each x in vv do putflag( uu,x,0)$
    depl!*:=delete(assoc(uu,depl!*),depl!*);
    remflag(list uu,'impfun)$ % to be effective in EXCALC;
    u:= cdr u$ go to l3$
l2: << spaces(5)$
       write "*** ",uu," is a protected function: NOT cleared ***"$
       terpri(); u:=cdr u>>$
l3: if null u then <<terpri();
              return "Clearing is complete">> else

    go to l1 end$

rlistat '(clearfunctions);


endmodule;

end;
