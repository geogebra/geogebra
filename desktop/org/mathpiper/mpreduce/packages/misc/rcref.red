module rcref; % Cross reference program.

% Author: Martin L. Griss, with modifications by Anthony C. Hearn and
%            Winfried Neun.

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


% Requires REDIO and SORT support.

create!-package('(rcref redio),'(util));

fluid '(!*backtrace !*cref !*defn !*mode !*nocrefpri calls!* curfun!*
        dfprint!* globs!* locls!* toplv!*);

global '(undefg!* gseen!* btime!* expand!* haveargs!* notuse!* nolist!*
        dclglb!* entpts!* undefns!* seen!* tseen!* xseen!* op!*!* cloc!*
        pfiles!* curlin!* pretitl!* !*creftime !*saveprops maxarg!*
        !*crefsummary !*comp !*raise !*rlisp !*globals !*algebraics);

switch cref;

!*algebraics:='t; % Default is normal parse of algebraic.
!*globals:='t;  % Do analyze globals.
% !*rlisp:=nil;   % REDUCE as default.
maxarg!*:=15;   % Maximum args in Standard Lisp.


deflist('((anlfn procstat) (crflapo procstat)),'stat);

flag('(anlfn crflapo),'compile);

flag('(bldmsg printf),'naryargs);  % Added by Eberhard Schruefer.

comment  EXPAND flag on these forces expansion of MACROS;

expand!* := '(for foreach repeat while);

nolist!* := nconc(for each j in slfns!* collect car j,nolist!*);

nolist!* := append('(and cond endmodule lambda list max min module or
                     plus prog prog2 progn putc switch times),
                   nolist!*);

flag ('(plus times and or lambda progn max min cond prog case list),
       'naryargs);

dclglb!*:='(!*comp emsg!* !*raise);

if not getd 'begin then
  flag('(rds deflist flag fluid global remprop remflag unfluid
           setq crefoff),'eval);

symbolic procedure crefon;
  begin btime!*:=time();
        dfprint!* := 'refprint;
        !*defn := t;
        if not !*algebraics then put('algebraic,'newnam,'symbolic);
        flag(nolist!*,'nolist);
        flag(expand!*,'expand);
        flag(dclglb!*,'dclglb);
%  Global lists.
        entpts!*:=nil;  % Entry points to package.
        undefns!*:=nil; % Functions undefined in package.
        seen!*:=nil;    % List of all encountered functions.
        tseen!*:=nil;   % List of all encountered types not flagged
                        % FUNCTION.
        gseen!*:=nil;   % All encountered globals.
        pfiles!*:=nil;  % Processed files.
        undefg!*:=nil;  % Undeclared globals encountered.
        curlin!*:=nil;  % Position in file(s) of current command.
        pretitl!*:=nil; % T if error or questionables found.
% Usages in specific function under analysis.
        globs!*:=nil;   % Globals refered to in this.
        calls!*:=nil;   % Functions called by this.
        locls!*:=nil;   % Defined local variables in this.
        toplv!*:=t;     % NIL if inside function body.
        curfun!*:=nil;  % Current function beeing analyzed.
        op!*!*:=nil;    % Current op. in LAP code.
        if not !*nocrefpri
          then setpage("  Errors or questionables",nil);
        if not getd 'begin then crefonlsp()   % In Lisp.
   end;

symbolic procedure undefdchk fn;
 if not flagp(fn,'defd) then undefns!* := fn . undefns!*;

symbolic procedure princng u;
   princn getes u;

symbolic procedure crefoff;
   % Main call, sets up, alphabetizes and prints.
   begin scalar tim,x;
        crefoff1();
        tim:=time()-btime!*;
        setpage("  Summary",nil);
        newpage();
        pfiles!*:=punused("Crossreference listing for files:",
                          for each z in pfiles!* collect cdr z);
        entpts!*:=punused("Entry Points:",entpts!*);
        undefns!*:=punused("Undefined Functions:",undefns!*);
        undefg!*:=punused("Undeclared Global Variables:",undefg!*);
        gseen!*:=punused("Global variables:",gseen!*);
        seen!*:=punused("Functions:",seen!*);
        for each z in tseen!* do
          <<rplacd(z,punused(list(car z," procedures:"),cdr z));
            x:='!( . nconc(explode car z,list '!));
            for each fn in cdr z do
             <<fn:=getes fn; rplacd(fn,append(x,cdr fn));
               rplaca(fn,length cdr fn)>> >>;
        if !*crefsummary then goto xy;
        if !*globals and gseen!* then
              <<setpage("  Global Variable Usage",1);
                newpage();
                for each z in gseen!* do cref6 z>>;
        if seen!* then cref52("  Function Usage",seen!*);
        for each z in tseen!* do
           cref52(list("  ",car z," procedures"),cdr z);
        setpage("  Toplevel calls:",nil);
        x:=t;
        for each z in pfiles!* do
         if get(z,'calls) or get(z,'globs) then
           <<if x then <<newpage(); x:=nil>>;
             newline 0; newline 0; princng z;
             spaces!-to 15; underline2 (linelength(nil)-10);
             cref51(z,'calls,"Calls:");
             if !*globals then cref51(z,'globs,"Globals:")>>;
  xy:   if !*saveprops then goto xx;
        rempropss(seen!*,'(gall calls globs calledby alsois sameas));
        remflagss(seen!*,'(seen cinthis defd));
        rempropss(gseen!*,'(usedby usedunby boundby setby));
        remflagss(gseen!*,'(dclglb gseen glb2rf glb2bd glb2st));
        for each z in tseen!* do remprop(car z,'funs);
%       for each z in haveargs!* do remprop(z,'number!-of!-args);
        haveargs!* := gseen!* := seen!* := tseen!* := nil;
  xx:   newline 2;
        if not !*creftime then return;
        btime!*:=time()-btime!*;
        setpage(" Timing Information",nil);
        newpage(); newline 0;
        prtatm " Total Time="; prtnum btime!*;
        prtatm " (ms)";
        newline 0;
        prtatm " Analysis Time="; prtnum tim;
        newline 0;
        prtatm " Sorting Time="; prtnum (btime!*-tim);
        newline 0; newline 0
  end;

symbolic procedure crefoff1;
   begin scalar x;
        dfprint!* := nil;
        !*defn := nil;
        if not !*algebraics
          then remprop('algebraic,'newnam);     % Back to normal.
         for each fn in seen!* do
         <<if null get(fn,'calledby) then entpts!*:=fn . entpts!*;
           undefdchk fn>>;
        tseen!*:=for each z in idsort tseen!* collect
         <<remprop(z,'tseen);
           for each fn in (x:=get(z,'funs)) do
            <<undefdchk fn; remprop(fn,'rccnam)>>;
           z.x>>;
        for each z in gseen!* do
         if get(z,'usedunby) then undefg!*:=z . undefg!*;
   end;

symbolic procedure punused(x,y);
 if y then
  <<newline 2; prtlst x; newline 0;
    lprint(y := idsort y,8); newline 0; y>>;

symbolic procedure cref52(x,y);
 <<setpage(x,1); newpage(); for each z in y do cref5 z>>;

symbolic procedure cref5 fn;
   % Print single entry.
   begin scalar x,y;
        newline 0; newline 0;
        prin1 fn; spaces!-to 15;
        y:=get(fn,'gall);
        if y then <<prin1 cdr y; x:=car y>>
         else prin2 "Undefined";
        spaces!-to 25;
        if flagp(fn,'naryargs) then prin2 "  Nary Args  "
         else if (y:=get(fn,'number!-of!-args)) then
          <<prin2 "  "; prin2 y; prin2 " Args  ">>;
        underline2 (linelength(nil)-10);
        if x then
          <<newline 15; prtatm "Line:"; spaces!-to 27;
            prtnum cddr x; prtatm '!/; prtnum cadr x;
            prtatm " in "; prtatm car x>>;
        cref51(fn,'calledby,"Called by:");
        cref51(fn,'calls,"Calls:");
        cref51(fn,'alsois,"Is also:");
        cref51(fn,'sameas,"Same as:");
        if !*globals then cref51(fn,'globs,"Globals:")
   end;

symbolic procedure cref51(x,y,z);
 if (x:=get(x,y)) then <<newline 15; prtatm z; lprint(idsort x,27)>>;

symbolic procedure cref6 glb;
   % Print single global usage entry.
      <<newline 0; prin1 glb; spaces!-to 15;
        notuse!*:=t;
        cref61(glb,'usedby,"Global in:");
        cref61(glb,'usedunby,"Undeclared:");
        cref61(glb,'boundby,"Bound in:");
        cref61(glb,'setby,"Set by:");
        if notuse!* then prtatm "*** Not Used ***">>;

symbolic procedure cref61(x,y,z);
   if (x:=get(x,y)) then
     <<if not notuse!* then newline 15 else notuse!*:=nil;
       prtatm z; lprint(idsort x,27)>>;

% Analyze bodies of LISP functions for functions called, and globals
% used, undefined.

smacro procedure flag1(u,v); flag(list u,v);

smacro procedure remflag1(u,v); remflag(list u,v);

smacro procedure isglob u;
 flagp(u,'dclglb);

smacro procedure chkseen s;
   % Has this name been encountered already?
        if not flagp(s,'seen) then
          <<flag1(s,'seen); seen!*:=s . seen!*>>;

smacro procedure globref u;
  if not flagp(u,'glb2rf)
   then <<flag1(u,'glb2rf); globs!*:=u . globs!*>>;

smacro procedure anatom u;
   % Global seen before local..ie detect extended from this.
   if !*globals and u and not(u eq 't)
      and idp u and not assoc(u,locls!*)
     then globref u;

smacro procedure chkgseen g;
 if not flagp(g,'gseen) then <<gseen!*:=g . gseen!*;
                            flag1(g,'gseen)>>;

symbolic procedure do!-global l;
  % Catch global defns.
  % Distinguish FLUID from GLOBAL later.
   if pairp(l:=qcrf car l) and !*globals and toplv!* then
     <<for each v in l do chkgseen v; flag(l,'dclglb)>>;

put('global,'anlfn,'do!-global);

put('fluid,'anlfn,'do!-global);

symbolic anlfn procedure unfluid l;
   if pairp(l:=qcrf car l) and !*globals and toplv!* then
     <<for each v in l do chkgseen v; remflag(l,'dclglb)>>;

symbolic procedure add2locs ll;
  begin scalar oldloc;
   if !*globals then for each gg in ll do
      <<oldloc:=assoc(gg,locls!*);
        if (oldloc and isglob gg) then <<
%       if not null oldloc then <<
           qerline 0;
           prin2 "*** Variable ";
           prin1 gg;
           prin2 " nested declaration in ";
           princng curfun!*;
           newline 0;
           rplacd(oldloc,nil.oldloc)>>
         else locls!*:=(gg . list nil) . locls!*;
        if isglob(gg) or flagp(gg,'glb2rf) then globind gg;
        if flagp(gg,'seen) then
          <<qerline 0;
            prin2 "*** Function ";
            princng gg;
            prin2 " used as variable in ";
            princng curfun!*;
            newline 0>> >>
  end;

symbolic procedure qerline u;
   if !*nocrefpri then nil
    else if pretitl!* then newline u
    else <<pretitl!*:=t; newpage()>>;

symbolic procedure globind gg;
  <<flag1(gg,'glb2bd); globref gg>>;

symbolic procedure remlocs lln;
   begin scalar oldloc;
    if !*globals then for each ll in lln do
      <<oldloc:=assoc(ll,locls!*);
        if null oldloc then
          if getd 'begin then rederr list(" Lvar confused",ll)
           else error(0,list(" Lvar confused",ll));
        if cddr oldloc then rplacd(oldloc,cddr oldloc)
         else locls!*:=efface1(oldloc,locls!*)>>
   end;

symbolic procedure efface1(u,v);
 if null v then nil
  else if u eq car v then cdr v
  else rplacd(v,efface1(u,cdr v));

symbolic procedure add2calls fn;
   % Update local CALLS!*.
   not flagp(fn,'cinthis) and
      <<if flagp(fn,'nolist) then xseen!* := fn . xseen!*
         else calls!* := fn . calls!*;
        flag1(fn,'cinthis)>>;

symbolic procedure anform u;
   if atom u then anatom u else anform1 u;

symbolic procedure anforml l;
   begin
        while not atom l do <<anform car l; l:=cdr l>>;
        if l then anatom l
   end;

symbolic procedure anform1 u;
   begin scalar fn,x;
        fn:=car u; u:=cdr u;
        if not atom fn then return <<anform1 fn; anforml u>>;
        if not idp fn then return nil
         else if isglob fn then <<globref fn; return anforml u>>
         else if assoc(fn,locls!*) then return anforml u;
        add2calls fn;
        checkargcount(fn,length u);
        if flagp(fn,'noanl) then nil
         else if x:=get(fn,'anlfn) then apply1(x,u)
         else anforml u
   end;

symbolic anlfn procedure lambda u;
 <<add2locs car u; anforml cdr u; remlocs car u>>;

symbolic procedure anlsetq u;
 <<anforml u;
   if !*globals and flagp(u:=car u,'glb2rf) then flag1(u,'glb2st)>>;

put('setq,'anlfn,'anlsetq);

symbolic anlfn procedure cond u;
 for each x in u do anforml x;

symbolic anlfn procedure prog u;
 <<add2locs car u;
   for each x in cdr u do
    if not atom x then anform1 x;
   remlocs car u>>;

symbolic anlfn procedure function u;
 if pairp(u:=car u) then anform1 u
  else if isglob u then globref u
  else if null assoc(u,locls!*) then add2calls u;

flag('(quote go),'noanl);

symbolic anlfn procedure errorset u;
 begin scalar fn,x;
  anforml cdr u;
  if eqcar(u:=car u,'quote) then return ersanform cadr u
   else if not((eqcar(u,'cons) or (x:=eqcar(u,'list)))
               and quotp(fn:=cadr u))
    then return anform u;
  anforml cddr u;
  if pairp(fn:=cadr fn) then anform1 fn
   else if flagp(fn,'glb2rf) then nil
   else if isglob fn then globref fn
   else <<add2calls fn; if x then checkargcount(fn,length cddr u)>>
 end;

symbolic procedure ersanform u;
 begin scalar locls!*;
  return anform u
 end;

symbolic procedure anlmap u;
 <<anforml u;
   if quotp(u:=cadr u) and idp(u:=cadr u)
      and not isglob u and not assoc(u,locls!*)
     then checkargcount(u,1)>>;

for each x in '(map mapc maplist mapcar mapcon mapcan) do
 put(x,'anlfn,'anlmap);

symbolic anlfn procedure lispapply u;
 begin scalar fn;
  anforml cdr u;
  if quotp(fn:=cadr u) and idp(fn:=cadr fn) and eqcar(u:=caddr u,'list)
    then checkargcount(fn,length cdr u)
 end;

symbolic anlfn procedure apply u;
 begin scalar fn;
  anforml cdr u;
  if quotp(fn:=cadr u) and idp(fn:=cadr fn) and eqcar(u:=caddr u,'list)
    then checkargcount(fn,length cdr u)
 end;

symbolic procedure quotp u; eqcar(u,'quote) or eqcar(u,'function);

put('cref ,'simpfg ,'((t (crefon)) (nil (crefoff))));

symbolic procedure outref(s,varlis,body,type);
 begin scalar curfun!*,calls!*,globs!*,locls!*,toplv!*,a;
  a:=if varlis memq '(anp!!atom anp!!idb anp!!eq anp!!unknown)
       then nil
      else length varlis;
  s := outrdefun(s,type,if a then a else get(body,'number!-of!-args));
  if a then <<add2locs varlis; anform(body); remlocs varlis>>
   else if null body or not idp body then nil
   else if varlis eq 'anp!!eq
    then <<put(s,'sameas,list body); traput(body,'alsois,s)>>
   else add2calls body;
  outrefend s
 end;

symbolic procedure traput(u,v,w);
 begin scalar a;
  if a:=get(u,v) then
    (if not(toplv!* or w memq a) then rplacd(a,w . cdr a))
   else put(u,v,list w)
 end;

smacro procedure toput(u,v,w);
 if w then put(u,v,if toplv!* then union(w,get(u,v)) else w);

symbolic procedure outrefend s;
  <<toput(s,'calls,calls!*);
    for each x in calls!* do
     <<remflag1(x,'cinthis);
        if not(x eq s) then <<chkseen x; traput(x,'calledby,s)>> >>;
    toput(s,'globs,globs!*);
    for each x in globs!* do
        <<traput(x,if isglob x then 'usedby
                    else <<chkgseen x; 'usedunby>>,s);
          remflag1(x,'glb2rf);
          if flagp(x,'glb2bd)
            then <<remflag1(x,'glb2bd); traput(x,'boundby,s)>>;
          if flagp(x,'glb2st)
            then <<remflag1(x,'glb2st); traput(x,'setby,s)>> >> >>;

symbolic procedure recref(s,type);
          <<qerline 2;
            prtatm "*** Redefinition to ";
            prin1 type;
            prtatm " procedure, of:";
            cref5 s;
            rempropss(list s,'(calls globs sameas));
            newline 2>>;

symbolic procedure outrdefun(s,type,v);
  begin
    s:=qtypnm(s,type);
    if flagp(s,'defd) then recref(s,type)
     else flag1(s,'defd);
    if flagp(type,'function) and (isglob s or assoc(s,locls!*)) then
      <<qerline 0;
        prin2 "**** Variable ";
        princng s;
        prin2 " defined as function";
        newline 0>>;
    if v and not flagp(type,'naryarg) then defineargs(s,v)
     else if flagp(type,'naryarg) and not flagp(s,'naryargs)
      then flag1(s,'naryargs);
    put(s,'gall,curlin!* . type);
    globs!*:=nil;
    calls!*:=nil;
    return curfun!*:=s
  end;

flag('(macro fexpr),'naryarg);

symbolic procedure qtypnm(s,type);
 if flagp(type,'function) then <<chkseen s; s>>
  else begin scalar x,y,z;
        if (y:=get(type,'tseen)) and (x:=atsoc(s,cdr y))
          then return cdr x;
        if null y then
          <<y:=list ('!( . nconc(explode type,list '!)));
            put(type,'tseen,y); tseen!* := type . tseen!*>>;
        x := compress (z := explode s);
        rplacd(y,(s . x) . cdr y);
        y := append(car y,z);
        put(x,'rccnam,length y . y);
        traput(type,'funs,x);
        return x
       end;

symbolic procedure defineargs(name,n);
  begin scalar calledwith,x;
    calledwith:=get(name,'number!-of!-args);
    if null calledwith then return hasarg(name,n);
    if n=calledwith then return nil;
    if x := get(name,'calledby) then instdof(name,n,calledwith,x);
    hasarg(name,n)
  end;

symbolic procedure instdof(name,n,m,fnlst);
  <<qerline 0;
    prin2 "***** ";
    prin1 name;
    prin2 " called with ";
    prin2 m;
    prin2 " instead of ";
    prin2 n;
    prin2 " arguments in:";
    lprint(idsort fnlst,posn()+1);
    newline 0>>;

symbolic procedure hasarg(name,n);
  <<haveargs!*:=name . haveargs!*;
    if n>maxarg!* then
           <<qerline 0;
             prin2 "**** "; prin1 name;
             prin2 " has "; prin2 n;
             prin2 " arguments";
             newline 0 >>;
    if name neq 'bldmsg then put(name,'number!-of!-args,n)>>;

symbolic procedure checkargcount(name,n);
  begin scalar correctn;
    if flagp(name,'naryargs) then return nil;
    correctn:=get(name,'number!-of!-args);
    if null correctn then return hasarg(name,n);
    if not(correctn=n) then instdof(name,correctn,n,list curfun!*)
  end;

symbolic procedure refprint u;
 begin scalar x,y;
% x:=if cloc!* then filemk car cloc!* else "*ttyinput*";
  x:=if cloc!* then car cloc!* else "*TTYINPUT*";
  if (curfun!*:=assoc(x,pfiles!*)) then
    <<x:=car curfun!*; curfun!*:=cdr curfun!*>>
   else <<pfiles!*:=(x.(curfun!*:=gensym())).pfiles!*;
          y:=reversip cdr reversip cdr explode x;
          put(curfun!*,'rccnam,length y . y)>>;
  curlin!*:=if cloc!* and cdr cloc!* then x . cdr cloc!* else nil;
  calls!*:=globs!*:=locls!*:=nil;
  anform u;
  outrefend curfun!*
 end;

symbolic procedure filemk u;
   % Convert a file specification from lisp format to a string.
   % This is essentially the inverse of MKFILE.
 begin scalar dev,name,flg,flg2;
  if null u then return nil
   else if atom u then name := explode2 u
   else for each x in u do
    if x eq 'dir!: then flg := t
     else if atom x then
      if flg then dev := '!< . nconc(explode2 x,list '!>)
       else if x eq 'dsk!: then dev:=nil
       else if !%devp x then dev := explode2 x
       else name := explode2 x
     else if atom cdr x then
      name := nconc(explode2 car x,'!. . explode2 cdr x)
     else <<flg2 := t;
            dev := '![ . nconc(explode2 car x,
                               '!, . nconc(explode2 cadr x,list '!]))>>;
  u := if flg2 then nconc(name,dev)
        else nconc(dev,name);
  return compress('!" . nconc(u,'(!")))
 end;

flag('(smacro nmacro),'cref);

symbolic anlfn procedure put u;
 if toplv!* and qcputx cadr u then anputx u
  else anforml u;

put('putc,'anlfn,get('put,'anlfn));

symbolic procedure qcputx u;
 eqcar(u,'quote) and (flagp(cadr u,'cref) or flagp(cadr u,'compile));

symbolic procedure anputx u;
 begin scalar nam,typ,body;
  nam:=qcrf car u;
  typ:=qcrf cadr u;
  u:=caddr u;
  if atom u then <<body:=qcrf u; u:='anp!!atom>>
   else if car u memq '(quote function) then
    if eqcar(u:=cadr u,'lambda) then <<body:=caddr u; u:=cadr u>>
     else if idp u then <<body:=u; u:='anp!!idb>>
     else return nil
   else if car u eq 'cdr and eqcar(cadr u,'getd) then
    <<body:=qcrf cadadr u; u:='anp!!eq>>
   else if car u eq 'get and qcputx caddr u then
    <<body:=qtypnm(qcrf cadr u,cadr caddr u); u:='anp!!eq>>
   else if car u eq 'mkcode then
    <<anform cadr u; u:=qcrf caddr u; body:=nil>>
   else <<body:=qcrf u; u:='anp!!unknown>>;
  outref(nam,u,body,typ)
 end;

symbolic anlfn procedure putd u;
 if toplv!* then anputx u else anforml u;

symbolic anlfn procedure de u;
 outdefr(u,'expr);

symbolic anlfn procedure df u;
 outdefr(u,'fexpr);

symbolic anlfn procedure dm u;
 outdefr(u,'macro);

symbolic anlfn procedure dn u;   % PSL function.
 outdefr(u,'macro);

symbolic anlfn procedure ds u;   % PSL function.
 outdefr(u,'smacro);

symbolic procedure outdefr(u,type);
 outref(car u,cadr u,caddr u,type);

symbolic procedure qcrf u;
 if null u or u eq t then u
  else if eqcar(u,'quote) then cadr u
  else <<anform u; compress explode '!?value!?!?>>;

flag('(expr fexpr macro smacro nmacro),'function);

endmodule;

end;
