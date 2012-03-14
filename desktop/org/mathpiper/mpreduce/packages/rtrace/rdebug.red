module rdebug; % REDUCE print extension for PSL's debug commands.

% Author: Herbert Melenk

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


%         June 1994

if not ('psl member lispsystem!*)
      then rederr "incompatible LISP support";

load debug;

errorset('(psl!-import '(break !*break breaker breakfunction !$break!$
   evtrwhen msgchnl!* !*redefmsg trprinter*
   trstinsidefn !-trstprog !-mktrst1 !-trget
   unbreak vartype)),nil,nil);

if null getd '!-trget then
  symbolic procedure !-trget(id,ind);
    (if w then cdr w) where w=atsoc(ind,get(id,'trace));

switch break;

remprop('statcounter,'vartype);
remprop('inputbuflis!*,'vartype);
remprop('resultbuflis!*,'vartype);

fluid '(!*nat out!* statcounter inputbuflis!* resultbuflis!*);
global '(trprinter!* msgchnl!* lispsystem!*);


if null getd 'assgnpri then
 symbolic procedure assgnpri(val,vars,mode);
  <<for each x in vars do <<writepri(mkquote x,'nil);writepri(":=",nil)>>;
    writepri(mkquote val,mode)>>;

%------------------------------------------------------------------
% Print algebraic expressions by REDUCE printer.

fluid '(trlimit);
share trlimit;
trlimit := 5;

symbolic procedure rdbprint u;
 % Try to print an expression u as algebraic expression rather than
 % LISP internal style.
  <<algpri1(u,0); assgnpri("",nil,'last)>>
      where out!* = (msgchnl!* or out!*), !*nat=nil;

symbolic procedure rdbprin2 u;
  algpri1(u,0) where out!* = (msgchnl!* or out!*), !*nat=nil;

symbolic procedure algpri1(u,n);
  begin scalar r;
   n:=n+1;
   if (r:=algpriform u) then return algpri2 r;
   algpri2 "[";
   while u do
     if atom u then <<algpri2 "."; algpri2 u; u:=nil>>
     else <<algpri1(car u,n);
            u:=cdr u; n:=n+1;
            if pairp u then algpri2 ",";
            if n>trlimit then <<algpri2 " ...";u:=nil>>
          >>;
   algpri2 "]";
  end;

symbolic procedure algpriform u;
  % is expression printable in algebraic mode?
   if atom u then u else
  if get(car u,'prifn) or get(car u,'pprifn) then u else
  if eqcar(u,'!*sq) then prepsq cadr u else
  if is!-algebraic!? u then u else
  if get(car u,'prepfn) then prepf u else
  if is!-sform!? u then prepf u else
  if is!-sfquot!? u then prepsq u;

symbolic procedure is!-algebraic!? u;
  atom u or get(car u,'dname)
         or (get(car u,'simpfn) or get(car u,'psopfn))
            and algebraic!-args cdr u;

symbolic procedure algebraic!-args u;
  null u or is!-algebraic!? car u and algebraic!-args cdr u;

symbolic procedure is!-sform!? u;
  if atom u then t else
  if get(car u,'dname) then t else
     pairp car u and pairp caar u and
     (is!-algebraic!? mvar u or is!-sform!? mvar u)
        and fixp ldeg u and ldeg u>0
        and is!-sform!? lc u and is!-sform!? red u;

symbolic procedure is!-sfquot!? u;
   pairp u and is!-sform!? numr u and is!-sform!? denr u;

symbolic procedure algpri2 u;
    assgnpri(u,nil,nil)
     where out!* = (msgchnl!* or out!*), !*nat=nil;

trprinter!* := 'rdbprint;

%------------------------------------------------------------------
% TRST extended to algebraic assignments (SETK function).

symbolic procedure !-trstsetk u;
 {'prog,'(!*nat),{'assgnpri, u,{'list,cadr u},''last}};

put('setk,'trstinsidefn,'!-trstsetk);

% prevent wrapper to go into assgnpri.

put('assgnpri,'trstinsidefn,'(lambda(u) u));

symbolic procedure !-trstprog!* u;
  % trst wrapper for prog: print labels additionally
  begin scalar c,r;
   c:=car u;
   r:= c . cadr u . for each s in cddr u join
      if pairp s then {s} else
      if c neq 'lambda and idp s and not gensymp s then
        {s,{'prin2,mkquote s},'(prin2t ":")}
      else {s};
   return !-trstprog r;
  end;

put('prog,'trstinsidefn,'!-trstprog!*);

symbolic procedure gensymp u;
   idp u and car(u:=explode2 u)= '!G and cdr u and gensymp1 cdr u;

symbolic procedure gensymp1 u;
   null u or digit car u and gensymp1 cdr u;

%------------------------------------------------------------------
% TROUT

symbolic operator trout;

%------------------------------------------------------------------
% TRWHEN

remd 'trwhen;

symbolic procedure trwhen u;
 <<evtrwhen{car u,nil}; % Install target function.
  evtrwhen{car u,formbool(cadr u,
             for each x in !-trget(car u,'argnames)
           collect x . !*mode ,'algebraic)}>>;

put('trwhen,'stat,'rlis);

%------------------------------------------------------------------
% BR



put('br,'stat,'rlis);
put('unbr,'stat,'rlis);
flag('(br unbr),'noform);

fluid '(breaklevel!*);

breaklevel!*:=0;

symbolic procedure break();
 (begin scalar pp,q,statcounter,inputbuflis!*,resultbuflis!*,!*redefmsg;
    breaklevel!*:=breaklevel!* + 1;
    statcounter := 0;
    pp:=getd 'printprompt;
    q:=get('!_,'psopfn);
    put('!_,'psopfn,'break_);
    remflag('(printprompt),'lose);
    putd('printprompt,'expr, cdr getd 'break_prompt);
    catch('!$break!$, eval '(begin));
    putd('printprompt,'expr,cdr pp);
    remprop('!_,'psopfn);
    if q then put('!_,'psopfn,q);
  end) where breaklevel!*=breaklevel!*;


symbolic procedure break_ u;
  <<if not atom car u then u:=car u;
   if get(car u,'breakfunction) then apply(get(car u,'breakfunction),cdr u)
     else prin2t "### unknown break function">>;

symbolic procedure break_prompt();
  <<prin2 "break["; prin2 breaklevel!*; prin2"]";>>;

symbolic procedure local_var u;
  begin scalar r;
    r:=errorset(u,nil,nil);
    return if errorp r then <<prin2l{"### variable",u," not bound"}; terpri();>>
           else car r;
  end;

put('l,'breakfunction,'local_var);

%------------------------------------------------------------------
% BRWHEN

remd 'brwhen;

symbolic procedure brwhen u;
 <<!-trinstall(car u,nil);
  evtrwhen{car u,formbool(cadr u,
             for each x in !-trget(car u,'argnames)
           collect x . !*mode ,'algebraic)}>>;

put('brwhen,'stat,'rlis);

%------------------------------------------------------------------
% RULE Trace

symbolic procedure rule!-trprint!* u;
   begin scalar r;
    rdbprin2 "Rule ";
    rdbprin2 car u; %name
    if cadr u then<<rdbprin2 "."; rdbprin2 cadr u>>;
    rdbprin2 ": ";
    rdbprin2 caddr u;
    rdbprin2 " => ";
    rdbprint (r:=cadddr u);
    return reval r;
   end;

put('rule!-trprint,'psopfn,'rule!-trprint!*);

fluid '(trace!-rules!*);

symbolic procedure trrl w;
  for each u in w do
   begin scalar name,rs,rsx,n;
     if idp u then
       <<name:=u;
         rs:=reval u;
         if rs=u then rs:=showrules u;
         if atom rs or car rs neq 'list or null cdr rs
              then rederr {"could not find rules for",u};
       >>
     else
       <<name:=gensym();
         prin2 "*** using name ";
         prin2 name;
         prin2t " for rule set">>;
     if eqcar(rs,'list) then <<rs:=cdr rs;n:=1>> else <<rs:={rs};n:=nil>>;
     rsx := trrules1(name,n,rs);
     trace!-rules!* := {name,rs,rsx} . trace!-rules!*;
     algebraic clearrules ('list.rs);
     algebraic let ('list.rsx);
     return name;
   end;

put('trrl,'stat,'rlis);

symbolic procedure trrules1(name,n,rs);
  begin scalar rl,nrl,rh,lh;
    rl:=car rs; rs:=cdr rs;
    if atom rl or not memq(car rl,'(replaceby equal))
       then typerr(rl,'rule);
    lh:=cadr rl; rh:=caddr rl;
    if constant_exprp lh then go to a;
    rh := if eqcar(rh,'when) then
      {'when,{'rule!-trprint,name,n,lh,cadr rh},caddr rh}
      else {'rule!-trprint,name,n,lh,rh};
a:  nrl := {car rl,lh,rh};
    return if null rs then {nrl} else
     nrl . trrules1(name,n+1,rs);
  end;

symbolic procedure untrrl u;
   begin scalar w,v;
     for each r in u do
     <<if not idp r then typerr(r,"rule set name");
       w:=assoc(r,trace!-rules!*);
       if w then
       << v:='list.caddr w;
          algebraic clearrules v;
          v:='list.cadr w;
          algebraic let v;
          trace!-rules!*:=delete(w,trace!-rules!*);
       >> ;
     >>;
   end;

put('untrrl,'stat,'rlis);

% Make 'rule!-trprint invisible when printed.

put('rule!-trprint,'prifn,
          function(lambda(u); maprin car cddddr u));

put('rule!-trprint,'fancy!-prifn,
          function(lambda(u);fancy!-maprin car cddddr u));

endmodule;

end;

