%********************************************************************
%                                                                   *
%  The program LIEPDE for computing point-, contact- and higher     *
%  order symmetries of individual ODEs/PDEs or systems of ODEs/PDEs *
%                                                                   *
%  Author: Thomas Wolf                                              *
%  Date:   20.July 1996                                             *
%                                                                   *
%  For details of how to use LIEPDE see the file LIEPDE.TXT or the  *
%  header of the procedure LIEPDE below.                            *
%                                                                   *
%********************************************************************

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

create!-package('(liepde), nil);

symbolic fluid '(print_ logoprint_ nfct_ fname_ adjust_fnc proc_list_
                 prelim_ individual_ prolong_order !*batch_mode)$
lisp << !*batch_mode:=t$ prelim_:=nil$
        individual_:=nil$ prolong_order:=0 >>$

symbolic procedure diffdeg(p,v)$
%   liefert Ordnung der Ableitung von p nach v$
%   p Liste Varible+Ordnung der Ableitung, v Variable (Atom)
if null p then 0                        %  alle Variable bearbeitet ?
else if car p=v then                    %  v naechste Variable ?
     if cdr p then
        if numberp(cadr p) then cadr p  %  folgt eine Zahl ?
                                else 1
        else 1
     else diffdeg(cdr p,v)$             %  weitersuchen

symbolic procedure ldiff1(l,v)$
%  liefert Liste der Ordnungen der Ableitungen nach den Variablen aus v
%  l Liste (Variable + Ordnung)$ v Liste der Variablen
if null v then nil                      %  alle Variable abgearbeitet ?
else cons(diffdeg(l,car v),ldiff1(l,cdr v))$
                                        %  Ordnung der Ableitung nach
                                        %  erster Variable anhaengen

%----------------------------

algebraic procedure equ_to_expr(a)$
% converts an equation into an expression
begin scalar lde;
 return
 if a=nil then a else
 <<lisp(lde:=reval algebraic a);
  if lisp(atom lde) then a else num
  if lisp(car lde = 'EQUAL) then lhs a - rhs a
              else a
 >>
end$ % of equ_to_expr


%********************************************************************
module pdesymmetry$
%********************************************************************
%  Routines for finding Symmetries of single or systems of ODEs/PDEs
%  Author: Thomas Wolf
%  July 1996


symbolic operator totdeg$
symbolic procedure totdeg(p,f)$
%   Ordnung (total) der hoechsten Ableitung von f im Ausdruck p
eval(cons('PLUS,ldiff1(car ldifftot(reval p,reval f),fctargs reval f)))$

symbolic procedure diffreltot(p,q,v)$
%   liefert komplizierteren Differentialausdruck$
if diffreltotp(p,q,v) then q
                   else p$

symbolic procedure diffreltotp(p,q,v)$
%   liefert t, falls p einfacherer Differentialausdruck, sonst nil
%   p, q Paare (liste.power), v Liste der Variablen
%   liste Liste aus Var. und Ordn. der Ableit. in Diff.ausdr.,
%   power Potenz des Differentialausdrucks
begin scalar n,m$
m:=eval(cons('PLUS,ldiff1(car p,v)))$
n:=eval(cons('PLUS,ldiff1(car q,v)))$
return
 if m<n then t
 else if n<m then nil
      else diffrelp(p,q,v)$
end$

symbolic procedure ldifftot(p,f)$
%  leading derivative total degree ordering
%  liefert Liste der Variablen + Ordnungen mit Potenz
%  p Ausdruck in LISP - Notation, f Funktion
ldifftot1(p,f,fctargs f)$

symbolic procedure ldifftot1(p,f,vl)$
%  liefert Liste der Variablen + Ordnungen mit Potenz
%  p Ausdruck in LISP - Notation, f Funktion, lv Variablenliste
begin scalar a$
a:=cons(nil,0)$
if not atom p then
if member(car p,list('EXPT,'PLUS,'MINUS,'TIMES,
          'QUOTIENT,'DF,'EQUAL)) then
                                        %  erlaubte Funktionen
        <<if (car p='PLUS) or (car p='TIMES) or (car p='QUOTIENT)
             or (car p='EQUAL) then
                <<p:=cdr p$
                while p do
                        <<a:=diffreltot(ldifftot1(car p,f,vl),a,vl)$
                        p:=cdr p>> >>
        else if car p='MINUS then
                a:=ldifftot1(cadr p,f,vl)
        else if car p='EXPT then        %  Exponent
                        if numberp caddr p then
                        <<a:=ldifftot1(cadr p,f,vl)$
                        a:=cons(car a,times(caddr p,cdr a))>>
                        else a:=cons(nil,0)
                                        %  Poetenz aus Basis wird mit
                                        %  Potenz multipliziert
        else if car p='DF then          %  Ableitung
                if cadr p=f then a:=cons(cddr p,1)
                                        %  f wird differenziert?
                else a:=cons(nil,0)>>   %  sonst Konstante bzgl. f
else if p=f then a:=cons(nil,1)
                                        %  Funktion selbst
        else a:=cons(nil,0)             %  alle uebrigen Fkt. werden
else if p=f then a:=cons(nil,1)$        %  wie Konstante behandelt
return a
end$

%---------------------

% Bei jedem totdf2-Aufruf pruefen, ob evtl. kuerzere dylist reicht
% evtl die combidiff-Kette und combi nicht mit in dylist, sond. erst in
% prolong jedesmal frisch generieren.

%symbolic operator desort$
algebraic procedure nextdy(revx,xlist,dy)$
% generates all first order derivatives of lhs dy
% revx = reverse xlist; xlist is the list of variables;
%                       dy the old derivative
begin
  scalar x,n,ldy,rdy,ldyx,sublist;
  x:=first revx; revx:=rest revx;
  sublist:={};
  ldy:=lhs dy;
  rdy:=rhs dy;

  while lisp(not member(prepsq simp!* algebraic x,
             prepsq simp!* algebraic ldy))
        and (revx neq {}) do
  <<x:=first revx; revx:=rest revx>>;

  n:=length xlist;
  if revx neq {} then                % dy is not the function itself
  while first xlist neq x do xlist:=rest xlist;
  xlist:=reverse xlist;

  % New higher derivatives
  while xlist neq {} do
  <<x:=first xlist;
    ldyx:=df(ldy,x);
    sublist:=cons((lisp reval algebraic ldyx)=
                  mkid(mkid(rdy,!`),n), sublist);
    n:=n-1;
    xlist:=rest xlist
  >>;
  return sublist
end$

%---------------------

algebraic procedure subdif1(xlist,ylist,ordr)$
% A list of lists of derivatives of one order for all functions
begin
 scalar allsub,revx,i,el,oldsub,newsub;
 revx:=reverse xlist;
 allsub:={};
 oldsub:= for each y in ylist collect y=y;
 for i:=1:ordr do      %  i is the order of next substitutions
 <<oldsub:=for each el in oldsub join nextdy(revx,xlist,el);
   allsub:=cons(oldsub,allsub)
 >>;
 return allsub
end$

%---------------------

symbolic procedure combidif(s)$
% extracts the list of derivatives from s: % u`1`1`2 --> (u, 1, 1, 2)
begin scalar temp,ans,no,n1;
  s:=reval s; % to guarantee s is in true prefix form
  temp:=reverse explode s;

  while not null temp do
  <<n1:=<<no:=nil;
          while (not null temp) and (not eqcar(temp,'!`)) do
          <<no:=car temp . no;temp:=cdr temp>>;
          compress no
        >>;
    if (not fixp n1) then n1:=intern n1;
    ans:=n1 . ans;
    if eqcar(temp,'!`) then <<temp:=cdr temp; temp:=cdr temp>>;
  >>;
  return ans
end$

%---------------------

symbolic procedure comparedif1(u1l,u2l)$
% u1l, u2l are lists of indicees of differentiation variables
% checks whether u2l has more or at least equally many 1's, 2's, ...
% contained as u1l.
% returns a list of 1's, 2's, ... which are in excess in u2l
% compared with u1l. The returned value is 0 if both are identical
begin
 scalar ul;
 if u2l=nil then if u1l neq nil then return nil
                                else return 0
            else if u1l=nil then return u2l
                            else % both are non-nil
 if car u1l < car u2l then return nil else
 if car u1l = car u2l then return comparedif1(cdr u1l,cdr u2l) else <<
  ul:=comparedif1(u1l,cdr u2l);
  return if not   ul then nil          else
         if zerop ul then list car u2l else
                          cons(car u2l,ul)
 >>
end$ % of comparedif1

%---------------------

symbolic procedure comparedif2(u1,u1list,du2)$
% checks whether du2 is a derivative of u1 differentiated
% wrt. u1list
begin
 scalar u2l;
 u2l:=combidif(du2)$ % u2l=(u2, 1, 1, ..)
 if car u2l neq u1 then return nil else
 return comparedif1(u1list, cdr u2l)
end$ % of comparedif2

%---------------------

symbolic procedure comparedif3(du1,u2,u2list)$
% checks whether u2 differentiated wrt. u2list
% is a derivative of du1
begin
 scalar u1l;
 u1l:=combidif(du1)$ % u1l=(u1, 1, 1, ..)
 if car u1l neq u2 then return nil else
 return comparedif1(cdr u1l, u2list)
end$ % of comparedif3

%---------------------

symbolic procedure listdifdif1(du1,deplist)$
% lists all elements of deplist which are *not* derivatives
% of du1
begin
 scalar u1,u1list,res,h$
 h:=combidif(du1);
 u1:=car h;
 u1list:=cdr h;
 for each h in deplist do
 if not comparedif2(u1,u1list,h) then res:=cons(h,res);
 return res
end$ % of listdifdif1

%---------------------

symbolic operator  dif$
symbolic procedure dif(s,n)$
% e.g.:   dif(fnc!`1!`3!`3!`4, 3) --> fnc!`1!`3!`3!`3!`4
begin scalar temp,ans,no,n1,n2,done;
  s:=reval s; % to guarantee s is in true prefix form
  temp:=reverse explode s;
  n2:=reval n;
  n2:=explode n2;

  while (not null temp) and (not done) do
  <<n1:=<<no:=nil;
          while (not null temp) and (not eqcar(temp,'!`)) do
          <<no:=car temp . no;temp:=cdr temp>>;
          compress no
        >>;
    if (not fixp n1) or ((fixp n1) and (n1 leq n)) then
    <<ans:=nconc(n2,ans); ans:='!` . ans; ans:='!! . ans; done:=t>>;
    ans:=nconc(no,ans);
    if eqcar(temp,'!`) then <<ans:='!` . ans; ans:='!! . ans;
                              temp:=cdr temp; temp:=cdr temp>>;
  >>;
  return intern compress nconc(reverse temp,ans);
end$

%---------------------

%symbolic procedure orderofderiv(du)$
%if atom du then (length combidif(du))-1
%           else nil$

%---------------------

symbolic procedure mergedepli(li1,li2)$
% li1,li2 are lists of lists
% mergedepli merges the sublists to make one list of lists
begin scalar newdep;
  while li1 and li2 do <<
    newdep:=union(car li1, car li2) . newdep;
    li1:=cdr li1; li2:=cdr li2
  >>;
  return if li1 then reversip2(newdep,li1) else
         if li2 then reversip2(newdep,li2) else reversip newdep
end$

%---------------------

symbolic procedure adddepli(ex,revdylist)$
begin scalar a,b,c,d;
 for each a in revdylist do <<
  c:=nil;
  for each b in a do
  if not my_freeof(ex,b) then c:=b . c;
  if c or d then d:=c . d;
 >>;
 return list(ex,d)
end$

%---------------------

symbolic procedure add_xi_eta_depli(xilist,etalist,revdylist)$
begin
  scalar e1,g,h$
  for e1:=1:(length xilist) do <<
    g:=nth(xilist,e1);
    h:=pnth(g,4);
    rplaca(h,cadr adddepli(car g,revdylist))
  >>;
  for e1:=1:(length etalist) do <<
    g:=nth(etalist,e1);
    h:=pnth(g,3);
    rplaca(h,cadr adddepli(car g,revdylist))
  >>
end$

%---------------------

symbolic procedure subtest(uik,sb,xlist,ordok,subordinc)$
begin
  scalar el5,el6,el7,el8,el9,el10,sbc$
  el5:=combidif(uik);
  el6:=car el5; el5:=cdr el5;  % el6..function name, el5..var.list
  el7:=nil; el8:=100; el9:=nil;
  sbc:=sb;
  while sbc and
        ((caaar sbc neq el6) or
         (0 neq <<el7:=comparedif1(cdaar sbc,el5);
                  if el7 and (not zerop el7) and
                     (length(el7)<el8) then
                  <<el8 :=length el7;
                    el9 :=el7;
                    el10:=car sbc>>    else
                  el7
                >>)
        ) do sbc:=cdr sbc;
  return
  if sbc then (cadar sbc . caddar sbc)         % simple substitution
         else
  if el9 then <<    % uik is total deriv of car el10 wrt el9
    uik:=cadr el10 . caddr el10;
    % car uik becomes the expr. to replace the former uik
    while el9 do <<
      uik:=totdf3(car uik, cdr uik, nth(xlist, car el9),
                  car el9, sb, xlist, ordok, subordinc);
      el9:=cdr el9
    >>;
    uik
  >>     else       % no substitution
  nil
end$

%---------------------

symbolic procedure totdf3(s,depli,x,n,sb,xlist,ordok,subordinc)$
% s is the expression to be differentiated wrt. x which is the nth
%   variable in xlist
% depli is a list of lists, each being the list of jet-variables
%   of order 0,1,2,..., such that s=s(xlist,depli), but
%   as little as possible jet-variables in depli
% xlist, depli are lisp lists, i.e. without 'LIST
% - totdf3 calculates total derivative of s(xlist,depli) w.r.t. x which
%   is the n'th variable, it returns (df(s,x), newdepli)
% - totdf3 drops jet-variables on which s does not depend
% - totdf3 automatically does substitutions using the list sb which
%   is updated if prolongations of substitutions are calculated,
%   i.e. sb is destructively changed!!
% - structure of sb: lisp list of lisp lists:
%   ((to_be_replaced_jet_var_name, to_be_replaced_jet_var_deriv_1,..),
%    subst_expr_in_jet_space_coord, list_of_jet_vars_in_subst_expr)
% - subordinc is a number by how much the order may increase due to
%   substitutions sb.
% - ordok is the lowest order which must be accurate. If ordok>0 and
%   s is of lower order than ordok then from depli only derivatives
%   of order ordok-1-subordinc to ordok-1 are used.
begin
  scalar tdf,el1,el2,el3,el4,el5,newdepli,
         newdy,dy,ddy,s;
  newdepli:=nil;                 % the new dependence list
  newdy:=nil;                    % the new dep.list due to chain rule
  ddy:=nil;                      % ddy .. derivatives of jet-variables
                                 % resulting from diff. of lower order

  %--- Should only terms in the result be acurate that include
  %--- derivatives of order>=ordok?
  if ordok>0 then <<
    tdf:=simp!* 0;
    depli:=copy depli;
    el2:=length depli;
    if el2<(ordok-subordinc) then depli:=nil
                             else
    for el1:=1:(ordok-1-subordinc) do <<
      dy:=pnth(depli,el1);
      rplaca(dy,nil);
    >>
  >>                                          else tdf:=simpdf {s,x};
  %--- The differentiations wrt. u-derivatives
  for each el1 in depli do       % for each order do
  <<dy:=union(ddy,el1); ddy:=nil;% dy .. occuring jet-var. of this order
    while el1 do
    <<el2:=car el1; el1:=cdr el1;% el2 is one jet-variable of this order
      el3:=simpdf {s,el2};
      if zerop el3 then dy:=delete(el2,dy)
                   else <<
        el4:=dif(el2,n);         % el4=df(el2,x)
        %----- Is el4 to be substituted by sb?
        if el5:=subtest(el4,sb,xlist,ordok,subordinc) then <<
          el4:=car el5;
          newdepli:=mergedepli(newdepli,cdr el5)
        >>                      else ddy:=el4 . ddy;
        tdf:=addsq(tdf, multsq(simp!* el4, el3))
      >>
    >>;
    newdy:=dy . newdy
  >>;
  if ddy then newdy:=ddy . newdy;
  newdepli:=mergedepli(reversip newdy,newdepli);
% possibly drop at the end
  return (prepsq tdf . newdepli)
end$ % of totdf3

%---------------------

symbolic procedure joinsublists(a)$
% It is assumed, a is either nil or a list of lists or nils which
% have to be joined
if null a then nil
          else append(car a,joinsublists(cdr a))$

%---------------------

symbolic procedure depnd(y,xlist)$
for each xx in xlist do
for each x  in xx    do depend y,x$

%---------------------

algebraic procedure transeq(eqn,xlist,ylist,sb)$
<<for each el1 in sb do eqn:=sub(el1,eqn);
  for each el1 in ylist do
  for each el2 in xlist do nodepend el1,el2;
  eqn>>$

%---------------------

symbolic operator  drop$
symbolic procedure drop(a,vl)$
% liefert summe aller terme aus a, die von elementen von vl abhaengen
begin scalar b$
  if not((pairp a) and (car a='PLUS)) then b:=a
                                      else
  <<vl:=cdr vl;             % because vl is an algebraic list
    for each c in cdr a do
    if not freeoflist(c,vl) then b:=cons(c,b)$
    if b then b:=cons('PLUS,reverse b)>>$
  return b$
end$

%---------------------

symbolic procedure etamn(u,indxlist,xilist,etalist,
                         ordok,truesub,subordinc,xlist)$
% determines etamn recursively
% At the end, ulist= list of df(u,i,cdr indxlist) for all i
begin
  scalar etam,x,h1,h2,h3,h4,ulist,el,r,cplist,depli;

  if (null indxlist) or ((length indxlist)=1) then
  <<cplist:=etalist;
    while u neq cadar cplist do cplist:=cdr cplist;
    etam:=(caar cplist . caddar cplist) . nil;
  >>                                           else
  etam:=etamn(u,cdr indxlist,xilist,etalist,ordok,truesub,
              subordinc,xlist)$

  return

  if null indxlist then etam
                   else <<
    ulist:=nil;
    x:=cdr nth(xilist,car indxlist);    % e.g.  x := (v3,3,dylist)
    r:=if zerop caar etam then simp!* <<depli:=nil;0>>
                          else simp!* <<
      h2:=totdf3(caar etam,cdar etam,car x,cadr x,truesub,xlist,
                 ordok,subordinc)$
      depli:=cdr h2;
      car h2
    >>;
    etam:=cdr etam;      % = reverse ulist
    cplist:=xilist;
    h3:=nil;
    while cplist do
    <<el:=car cplist;  % e.g.  el=xi_z
      cplist:=cdr cplist;
      if (length indxlist)=1 then h1:=dif(u,caddr el)
                             else <<
        h1:=dif(car etam,cadr indxlist);  % e.g.  h1:=u!`i!`n
        etam:=cdr etam;
      >>;

      ulist:=h1 . ulist;
      if not zerop car el then <<

        %--- substitution of h1?
        if h4:=subtest(h1,truesub,xlist,ordok,subordinc) then
        h1:=car h4;

        r:=subtrsq(r,
                   multsq(simp!* h1,
                          simp!* <<h2:=totdf3(car el,cadddr el,car x,
                                       cadr x,truesub,xlist,0,0)$
                                   if zerop car h2 then 0
                                                   else
                                   <<if h4 then
                                     depli:=mergedepli(depli,cdr h4)
                                           else h3:=h1 . h3;
                                     depli:=mergedepli(depli,cdr h2);
                                     car h2
                                   >>
                                 >>
                         )
                  );
      >>
    >>;
    if h3 then <<
      h3:=list h3;
      for h2:=1:(length indxlist) do h3:=nil . h3;
      depli:=mergedepli(depli,h3);
    >>;
    % (if not full then drop(r,'LIST . car revdylist) else r) .
    % (reverse ulist)
    (prepsq r . depli) . (reverse ulist)
  >>
end$ % of etamn

%---------------------

symbolic procedure prolong(uik,xilist,etalist,ordok,truesub,subordinc,
                           xlist)$
begin
  scalar h;
  h:=combidif(uik);
  h:=car etamn(car h,cdr h,xilist,etalist,ordok,truesub,
               subordinc,xlist)$
  return (simp!* car h) . cdr h
end$ % of prolong

%---------------------

symbolic procedure callcrack(!*time,cpu,gc,lietrace_,symcon,
                             flist,vl,xilist,etalist,inequ)$
begin
  scalar g,h; % ,batch_mode_old;
  if !*time then <<terpri()$
    write "time to formulate conditions: ", time() - cpu,
          " ms    GC time : ", gctime() - gc," ms"$
  >>;
  if lietrace_ then algebraic <<
    write"Symmetry conditions before CRACK: ";
    write lisp ('LIST . symcon);
  >>;
  % batch_mode_old:=!*batch_mode$
  % !*batch_mode:=nil$
  h:=crack('LIST . symcon,'LIST . inequ,'LIST . flist,'LIST . vl);
  % !*batch_mode:=batch_mode_old$
  if h neq list('LIST) then
  <<h:=cadr h;
    symcon:=cdadr h;

    for each g in cdaddr h do <<

      xilist :=subst(caddr g, cadr g,  xilist);
      etalist:=subst(caddr g, cadr g, etalist);
      inequ  :=subst(caddr g, cadr g,  inequ);
%--> Erkennung von 'e, 'x siehe:

%      h:=intern car explode cadr e1;
%write"h=",h;terpri()$
%      if (h='x) or (h='X) then
%      xilist :=subst(caddr e1, cadr e1,  xilist) else
%      if (h='e) or (h='E) or (h="e") or (h="E") then
%      etalist:=subst(caddr e1, cadr e1, etalist) else
%      rederr("One ansatz does not specify XI_ nor ETA_.")
    >>;

    if lietrace_ then <<
      write"symcon nachher: ",symcon;
      write"xilist=", xilist;
      write"etalist=", etalist;
    >>;
    flist:=cdr reval cadddr h;
    if print_ then
    <<terpri()$
      write"Remaining free functions after the last CRACK-run:";
      terpri()$
      fctprint flist;terpri()$terpri()>>;
  >>;
  return list(symcon,xilist,etalist,flist,inequ)
end$ % of callcrack

%---------------------

symbolic operator  liepde$
symbolic procedure liepde(problem,symtype,flist,inequ)$

 comment

 problem:  {{eq1,eq2, ...},   % equations
            { y1, y2, ...},   % functions
            { x1, x2, ...} }  % variables

           % Equations `eqi' can be given as single differential
           % expressions which have to vanish or they can be given
           % in the form df(..,..[,..]) = ..   .

           % If the equations are given as single differential
           % expressions then the program will try to bring it into
           % the `solved form' ..=.. automatically.

           % The solved forms (either from input or generated within
           % LIEPDE) will be used for substitutions, to find
           % all symmetries satisfied by solutions of the equations.
           % Sufficient conditions for this procedure to be correct,
           % i.e. to get *all* symmetries of the specified type on the
           % solution space are:

           % - There are equally many equations and functions.
           % - Each function is used once for a substitution and
           %   each equation is used once for a substitution.
           % - All functions differentiated on the left hand sides
           %   (lhs) depend on all variables.
           % - In no equation df(..,..[,..]) = .. does the right hand
           %   side contain the derivative on the lhs nor any
           %   derivative of it.
           % - No equation does contain a lhs or the derivative
           %   of a lhs of another equation.

           % These conditions are checked in LIEPDE and execution
           % is stoped if they are not satisfied, i.e. if the input
           % was not correct, or if the program was not able to
           % write the input expressions properly the solved
           % form ..=..  . One then should find for each function
           % one derivative which occurs linearly in one equation.
           % The chosen derivatives should be as high as possible,
           % at least there must no derivative of them occur in any
           % equation. An easy way to get the equations in the
           % desired form is to use
           % FIRST SOLVE({eq1,eq2,...},{list of derivatives})

           % NOTE that to improve efficiency it is advisable not to
           % express lower order derivatives on the left hand side
           % through higher order derivatives on the right hand side.
           % SEE also the implications on completeness for the
           % determination of generalized symmetries with
           % characteristic functions of a given order, as described
           % below and the two examples with the Burgers equation.

 symtype:  {"point"}          % for point   symmetries
           {"contact"}        % for contact symmetries, is only
                              % applicable if only one function,
                              % only one equation of order>1
           {"general",order}  % for generalized symmetries of
                              % order `order' which is an integer>0
                              % NOTE: Characteristic functions of
                              % generalized symmetries (i.e. the
                              % eta_.. if xi_..=0) are equivalent
                              % if they are equal on the solution
                              % manifold. Therefore all dependencies
                              % of characteristic functions on
                              % the substituted derivatives and their
                              % derivatives are dropped. This has the
                              % consequence that if, e.g. for the heat
                              % equation df(u,t)=df(u,x,2), df(u,t) is
                              % substituted by df(u,x,2) then
                              % {"general",2) would not include
                              % characteristic functions depending
                              % on df(u,t,x), or df(u,x,3). THEREFORE:
                              % If you want to find all symmetries up
                              % to a given order then
                              % - either avoid substituting lower
                              %   order derivatives by expressions
                              %   involving higher derivatives, or,
                              % - go up in the order specified in
                              %   symtype.
                              %
                              % Example:
                              %
                              % depend u,t,x
                              % liepde({{df(u,t)=df(u,x,2)+df(u,x)**2},
                              %         {u},{t,x}},
                              %        {"general",3},{})
                              %
                              % will give 10 symmetries + one infinite
                              % family of symmetries whereas
                              %
                              % liepde({{df(u,x,2)=df(u,t)-df(u,x)**2},
                              %         {u},{t,x}},
                              %        {"general",3},{})
                              %
                              % will give 28 symmetries + one infinite
                              % family of symmetries.

           {xi!_x1 =...,
               ...
            eta!_y3=... }     % - An ansatz must specify all xi!_.. for
                              %   all indep. variables and all eta!_..
                              %   for all dep. variables in terms of
                              %   differential expressions which may
                              %   involve unknown functions/constants.
                              %   The dependencies of the unknown
                              %   functions have to declared earlier
                              %   using the DEPEND command.
                              % - If the ansatz should consist of the
                              %   characteristic functions then set
                              %   all xi!_..=0 and assign the charac-
                              %   teristic functions to the eta!_.. .

 flist:    {- all parameters and functions in the equations which are to
              be determined, such that there exist symmetries,
            - if an ansatz has been made in symtype then flist contains
              all unknown functions and constants in xi!_.. and eta!_..}

 inequ:    {all non-vanishing expressions which represent
            inequalities for the functions in flist}

 Further comments:

 The syntax of input is the usual REDUCE syntax. For example, the
 derivative of y3 wrt x1 once and x2 twice would be df(y3,x1,x2,2).
 --> One exception: If in the equations or in the ansatz the dependence
 of a free function F on a derivative, like df(y3,x1,x2,2) shall be
 declared then the declaration has to have the form:
 DEPEND F, Y3!`1!`2!`2
 - the ! has to preceede each special character, like `,
 - `i stands for the derivative with respect to the i'th variable in
   the list of variables (the third list in the problem above)

 If the flag individual_ is t then conditions are investigated for
 each equation of a system of equations at first individually before
 conditions resulting from other equations are added.

 If the flag prelim_ is t then preliminary conditions for equations
 of higher than 1st order are formulated and investigated before the
 full condition is formulated and investigated by CRACK.

 If the REDUCE switch TIME is set on with ON TIME then times for the
 individual steps in the calculation are shown.

 Further switches and parameters which can be changed to affect the
 output and performance of CRACK in solving the symmetry conditions
 are listed in the file CRINIT.RED.

;

begin
  scalar cpu, gc, lietrace_, oldadj, eqlist, ylist, xlist, pointp,
         contactp, generalp, ansatzp, symord, e1, e2, ordr, sb,
         dylist, revdylist, xi, eta, eqordr, eqordrcop, no, eqcopy1,
         truesub, deplist, xilist, etalist, dycopy, freelist, eqlen,
         dylen, truesubno, minordr, n1, n2, n3, n4, n, h, jetord,
         allsub, subdy, lhslist, symcon, subordinc, eqn, depli,
         vl, occli, revdycopy, subordinclist, xicop, etacop, flcop,
         etapqlist, etapqcop, etapq, batch_mode_old;

  backup_reduce_flags()$
  cpu:=time()$ gc:=gctime()$
%  lietrace_:=t;
  oldadj:=adjust_fnc;
  adjust_fnc:=nil;

  %--------- extracting input data
  eqlist:= cdr   maklist cadr   problem;
  ylist := reval maklist caddr  problem;
  xlist := reval maklist cadddr problem;

  if inequ then inequ:=cdr inequ;

  eqlen:=length eqlist;

  %  if 1+eqlen neq length(ylist) then rederr(
  %  "Number of equations does not match number of unknown functions.");

  for each e1 in cdr ylist do
  for each e2 in cdr xlist do
  if my_freeof(e1,e2) then rederr(
  "Not all functions do depend on all variables.");

  if atom cadr symtype then                % default case
  if cadr symtype = "point"   then <<pointp  :=t;symord:=0>> else
  if cadr symtype = "contact" then <<contactp:=t;symord:=1;
    if eqlen>1 then rederr(
    "Contact symmetries only in case of one equation for one function.")
  >> else
  if cadr symtype = "general" then <<generalp:=t;symord:=caddr symtype;
    if (not fixp symord) or (symord<1) then rederr(
    "The order of the generalized symmetry must be an integer > 0.")
  >>                          else rederr("Inconclusive symmetry type.")
                       else <<
    ansatzp:=t;    % an ansatz has been made
    if length(ylist)+length(xlist) neq length(symtype)+1 then
    rederr("Number of assignments in the ansatz is wrong.");

    symord:=0;
    for each e1 in cdr symtype do
    for each e2 in ylist do
    <<n:=totdeg(e1,e2);
      if n>symord then symord:=n>>;
    if  symtype = 0                        then pointp  :=t else
    if (symtype = 1) and (length(ylist)=2) then contactp:=t else
                                                generalp:=t
  >>$

  if flist then flist:=cdr flist;
  problem:=0;

  %---- Are substitutions already given in the input?
  eqcopy1:=eqlist;
  while eqcopy1 and (pairp car eqcopy1) and (caar eqcopy1='EQUAL) and
        (pairp cadar eqcopy1) and (caadar eqcopy1='DF) do
  eqcopy1:=cdr eqcopy1;
  if null eqcopy1 then truesub:=eqlist;
  eqcopy1:=nil;

  %--------- initial printout
  if print_ and logoprint_ then <<terpri()$
    write "-----------------------------------------------",
    "---------------------------"$ terpri()$terpri()$
    write"This is LIEPDE - a program for calculating infinitesimal",
         " symmetries"; terpri()$
    write "of single differential equations or systems of de's";
  >>;
  terpri();terpri();
  if length xlist=2 then write"The ODE"
                    else write"The PDE";
  if length ylist>2 then write"-system";
  write " under investigation is :";terpri();
%  for each e1 in eqlist do algebraic write"0 = ",lisp e1;
  for each e1 in eqlist do algebraic write lisp e1;
  terpri()$write "for the function(s) : ";terpri()$
  terpri()$fctprint cdr reval ylist;
  terpri()$terpri();

  eqlist:=for each e1 in eqlist collect algebraic equ_to_expr(lisp e1);
  if eqlen > 1 then eqlist:=desort eqlist;

  if !*time then <<terpri()$
    terpri()$terpri()$
    write"=============== Initializations" ;
  >>;
  %--------- initializations
  ordr:=0;
  for each e1 in eqlist do <<
    h:=0;
    for each e2 in cdr ylist do
    <<n:=totdeg(e1,e2);
      if n>h then h:=n>>;
    eqordr:=h . eqordr;
    if h>ordr then ordr:=h
  >>;
  eqordr:=reversip eqordr;

  if ordr>symord then jetord:=ordr
                 else jetord:=symord$
  sb:=subdif1(xlist,ylist,jetord)$

  eqlist:=cons('LIST,eqlist);
  if ansatzp then eqlist:=list('LIST,symtype,eqlist);
  if truesub then eqlist:=list('LIST,cons('LIST,truesub),eqlist);
  if inequ   then eqlist:=list('LIST,cons('LIST,inequ),eqlist);

  on evallhseqp;
  eqlist:=transeq(eqlist,xlist,ylist,sb);
  off evallhseqp;

  if inequ   then <<inequ  :=cdadr eqlist;eqlist:=caddr eqlist>>;
  if truesub then <<truesub:=cdadr eqlist;eqlist:=caddr eqlist>>;
  if ansatzp then <<symtype:=cdadr eqlist;eqlist:=cdaddr eqlist>>
             else eqlist:=cdr eqlist;

  ylist:=cdr ylist;
  xlist:=cdr xlist;

  if lietrace_ and ansatzp then write"ansatz=",symtype;

  dylist:=ylist . reverse for each e1 in cdr sb collect
                          for each e2 in cdr e1 collect caddr e2;
  revdylist:=reverse dylist;  % dylist with decreasing order

  vl:=xlist;
  for each e1 in dylist do vl:=append(e1,vl);
  vl:='LIST . vl;

  if not ansatzp then
  deplist:=for n:=0:symord collect nth(dylist,n+1);
    % list of variables the xi_, eta_ depend on

  xi :=reval algebraic xi!_;
  eta:=reval algebraic eta!_;
  n:=0;
  xilist :=for each e1 in xlist collect
  <<n:=n+1;
    if pointp or ansatzp then <<
      h:=mkid(xi,e1);
      if not ansatzp then <<
        depnd(h,xlist . deplist);
        flist:=h . flist;
        depli:=deplist;
      >>             else depli:=nil
    >>                   else <<h:=0;depli:=nil>>;
    {h,e1,n,depli}
  >>;
  depli:=if (not ansatzp) and (not generalp) then deplist
                                             else nil;
  n:=0;
  etalist:=for each e1 in ylist collect
  <<n:=n+1;
    h:=mkid(eta,e1);
    if not ansatzp then <<
      if not generalp then depnd(h,xlist . deplist);
      % the generalp-case is done below when substitutions are known
      flist:=h . flist;
    >>;
    {h,e1,depli}
  >>;

  if ansatzp then <<
    for each e1 in symtype do <<

      xilist :=subst(caddr e1, cadr e1,  xilist);
      etalist:=subst(caddr e1, cadr e1, etalist);
      %--> Erkennung von 'e, 'x siehe:
      %      h:=intern car explode cadr e1;
      %write"h=",h;terpri()$
      %      if (h='x) or (h='X) then
      %      xilist :=subst(caddr e1, cadr e1,  xilist) else
      %      if (h='e) or (h='E) or (h="e") or (h="E") then
      %      etalist:=subst(caddr e1, cadr e1, etalist) else
      %      rederr("One ansatz does not specify XI_ nor ETA_.")
    >>;
    add_xi_eta_depli(xilist,etalist,revdylist)$
  >>;

  if lietrace_ then write"xilist=",xilist,"  etalist=",etalist;
  %---- Determining a substitution list for highest derivatives
  %---- from eqlist. Substitutions may not be optimal if starting
  %---- system is not in standard form

  comment: Counting in how many equations each highest
  derivative occurs. Those which do not occur allow Stephani-Trick,
  those which do occur once and there linearly are substituted by that
  equation.

  Because one derivative shall be assigned it must be one of
  the highest derivatives from each equation used, or one such that
  no other derivative in the equation is a derivative of it.

  Each equation must be used only once.

  Each derivative must be substituted by only one equation.

  At first determining the number of occurences of each highest
  derivative.

  The possiblity of substitutions is checked in each total derivative.

  $

  if truesub then <<       %--- determination of freelist %and occurlist
    dycopy:=car revdylist; %--- the highest derivatives
    while dycopy do
    <<e1:=car dycopy; dycopy:=cdr dycopy;
      eqcopy1:=eqlist;
      while eqcopy1 and my_freeof(car eqcopy1,e1) do
      eqcopy1:=cdr eqcopy1;

      if null eqcopy1 then freelist :=e1 . freelist
                      %else occurlist:=e1 . occurlist;
    >>
  >>         else <<

    no:=0;               % counter of the following repeat-loop
                         % freelist (and occurlist) are determined
                         % only in the first run
    eqordrcop:=copy eqordr;
                         % for bookkeeping which equation have been used

    repeat <<
      no:=no+1;        %--- incrementing the loop counter

      %--- truesubno is the number of substitutions so far found.
      %--- It is necessary at the end to check whether new substitutions
      %--- have been found.
      if null truesub then truesubno:=0
                      else truesubno:=length truesub;
      %--- substitutions of equations of minimal order are searched first
      minordr:=1000;   %--- minimal order of the so far unused equations
      for each e1 in eqordrcop do
      if (e1 neq 0) and (e1<minordr) then minordr:=e1;

      dycopy:=copy nth(dylist,minordr+1); %-- all deriv. of order minordr
      dylen:=length dycopy;

      allsub:=nil;

      for n1:=1:dylen do    %--- checking all deriv. of order minordr
      <<e1:=nth(dycopy,n1); %--- e1 is the current candidate
        %--- here test, whether e1 is not a derivative of a lhs of one
        %--- of the substitutions car e2 found so far
        h:=combidif(e1); n:=car h; h:=cdr h;
        e2:=truesub;
        while e2 and (null comparedif3(cadar e2,n,h)) do e2:=cdr e2;
        if null e2 then <<

          n2:=0; %-- number of equations in which the derivative e1 occurs
          subdy:=nil;
          for n3:=1:eqlen do
          if not my_freeof(nth(eqlist,n3),e1) then
          % here should also be tested whether derivatives of e1 occur
          % and not just my_freeof
          %-->
          <<n2:=n2+1;
            if nth(eqordrcop,n3)=minordr then
            %--- equation is not used yet and of the right order
            <<e2:=cdr algebraic coeff(lisp nth(eqlist,n3),lisp e1);
              if hipow!*=1 then
              subdy:=list(n1,n3,list('EQUAL,e1,list('MINUS,
                          list('QUOTIENT, car e2, cadr e2)))) . subdy
            >>
          >>;
          if n2=0 then if no=1 then freelist:=e1 . freelist else
                  else
          <<%if no=1 then occurlist:=e1 . occurlist;
            if subdy then if n2=1 then
            <<h:=car subdy;
              truesub:=(caddr h) . truesub;
              n:=pnth(dycopy   ,car  h);rplaca(n,0);
              n:=pnth(eqordrcop,cadr h);rplaca(n,0);
            >>                    else
            allsub:=nconc(allsub,subdy);
          >>
        >>
      >>;

      %---- Taking the remaining known substitutions of highest deriv.
      h:=subdy:=0;
      for each h in allsub do
      if (nth(dycopy   , car h) neq 0) and
         (nth(eqordrcop,cadr h) neq 0) then
      <<truesub:=(caddr h) . truesub;
        n:=pnth(dycopy   ,car  h);rplaca(n,0);
        n:=pnth(eqordrcop,cadr h);rplaca(n,0);
      >>;
    >> until (truesub and (length(truesub)=eqlen)) % complete
             or (truesubno=length(truesub))$       % no progress
    allsub:=eqordrcop:=dycopy:=nil;

    if (null truesub) or
       (eqlen neq length(truesub)) then rederr(
  "Unable to find all substitutions. Input equations as df(..,..)=..!");
  >>;

  lhslist:=for each e1 in truesub collect cadr e1;

  %-- Bringing truesub into a specific form: lisp list of lisp lists:
  %   ((to_be_replaced_jet_var_name, to_be_replaced_jet_var_deriv_1,..),
  %    subst_expr_in_jet_space_coord, list_of_jet_vars_in_subst_expr)
  truesub:=for each e1 in truesub collect
  cons(combidif cadr e1, adddepli(caddr e1,revdylist))$

  %--- Checking that no rhs of a substitution contains any lhs or
  %--- derivative of a lhs
  h:=t;  %--- h=nil if lhs's are derivatives of each other
  no:=t; %--- no=nil if one lhs can be substituted in a rhs
  for each e1 in truesub do
  if h and no then <<
    n1:=caar e1; n2:=cdar e1; dylen:=length n2;
    for each e2 in truesub do <<
      %--- comparison of two lhs's
      if not(e1 eq e2) and (n1=caar e2) and
      comparedif1(n2,cdar e2) then h:=nil;   %--- truesub is not ok
      %--- can the lhs of e1 be substituted on the rhs?
      dycopy:=caddr e2;
      for n:=1:dylen do if dycopy then dycopy:=cdr dycopy;
      for each e3 in dycopy do
      for each e4 in e3 do
      if comparedif2(n1,n2,e4) then no:=nil;
    >>
  >>;
  if null h  then rederr(
  "One substitution can be made in the lhs of another substitution!");
  if null no then rederr(
  "One substitution can be made in the rhs of another substitution!");

  %????????????????????????????????????????????
  %  %--- Checking that a derivative of each dependent variable is
  %  %--- substituted once. This is a sufficient condition for having
  %  %--- a de-system that is a differential Groebner basis
  %  h:=nil;
  %  for each e1 in lhslist do h:=adjoin(car combidif e1,h);
  %  if length(h) neq length(lhslist) then rederr(
  %  "For at least one function there is more that one substituion!")$

  %--- Determine of how much the order may increase by a substitution
  subordinc:=0;
  subordinclist:=for each h in truesub collect <<
    n:=(length caddr h) - (length car h);
    if n>subordinc then subordinc:=n;
    n
  >>;
  if lietrace_ then <<terpri()$write"truesub=",truesub;
                      terpri()$write"freelist=",freelist;
                      %terpri()$write"occurlist=",occurlist
                    >>;

  %--- To avoid non-uniqueness in the case of the investigation of
  %--- generalized symmetries let the characteristics eta_.. (xi_..=0)
  %--- not depend on substituted derivatives
  if generalp and (null ansatzp) then <<
    deplist:=ylist .
             for each dycopy in cdr deplist collect <<
               for each h in lhslist do
               %---- delete h and derivatives of h
               dycopy:=listdifdif1(h,dycopy);
               dycopy
             >>;
    for e1:=1:(length etalist) do <<
      h:=nth(etalist,e1);
      depnd(car h,xlist . deplist);
      h:=pnth(h,3);
      rplaca(h,deplist)
    >>
  >>;
  % reduced set of solution techniques for preliminary conditions
  proc_list_:=delete('multintfac,proc_list_)$
  if !*time then <<terpri()$
    write "time for initializations: ", time() - cpu,
          " ms    GC time : ", gctime() - gc," ms"$
    cpu:=time()$ gc:=gctime()$
  >>;
  %------ Determining first short determining equations and solving them
  symcon:=nil;
  n1:=0;
  if prelim_ then
  for each eqn in eqlist do
  <<n1:=n1+1;
    if !*time then <<terpri()$
      terpri()$terpri()$
      write"=============== Preconditions for the ",n1,". equation" ;
    >>;
    revdycopy:=revdylist;
    for e1:=(nth(eqordr,n1) + 1):ordr do revdycopy:=cdr revdycopy;
    n2:=cadr adddepli(eqn,revdycopy); % jet-variables in eqn
    vl:=n2;
    occli:=lastcar n2;
    freelist:=setdiff(car revdycopy,occli);
    if pointp and (subordinc=0) then
    eqn:=drop(eqn,occli) % dropp all terms without a highest deriv.
                                else occli:=joinsublists n2$
    % freelist must not contain substituted variables
    freelist:=setdiff(freelist,lhslist);
    % It must be possible to separate wrt freelist variables
    for each n4 in freelist do
    if not freeof(depl!*,n4) then freelist:=delete(n4,freelist);
    If freelist then <<
      n:=nth(eqordr,n1);   % order of this equation
      h:=simp!* 0;
      for each e1 in xilist do
      if (cadddr e1) and ((length cadddr e1) > n) then
      % xi (=car e1) is of order n
      h:=addsq(h,
               if car e1 = 0 then simp!* 0
                             else <<n3:=mergedepli(n3,cadddr e1);
                                    multsq(simp!* car e1,
                                           simpdf {eqn,cadr e1})
                                  >>
              );
      for each e2 in occli do
      h:=addsq(h,
               multsq(<<n4:=prolong(e2,xilist,etalist,nth(eqordr,n1),
                                    truesub,subordinc,xlist);
                        vl:=mergedepli(vl,cdr n4);
                        car n4
                      >>,
                      simpdf {eqn,e2}
                     )
              );
      for each e2 in freelist do
      <<
      e1:=algebraic num lisp coeffn(prepsq h,e2,1);
        if not zerop e1 then symcon:=e1 . symcon>>;

      vl:=joinsublists(xlist . vl)$
      for n2:=1:eqlen do
      <<n4:=nth(lhslist,n2);
        if not my_freeof(eqn,n4) then
        symcon:=subst(cadr nth(truesub,n2), n4, symcon);
        vl:=delete(n4,vl)
      >>;
      if symcon and (individual_ or (n1=eqlen)) then <<
        inequ:=callcrack(!*time,cpu,gc,lietrace_,symcon,
                         flist,vl,xilist,etalist,inequ);
        symcon :=car   inequ; xilist:=cadr   inequ;
        etalist:=caddr inequ; flist :=cadddr inequ;
        inequ  :=cadddr cdr inequ;
        cpu:=time()$ gc:=gctime()$
      >>
    >>
  >>;
  %------------ Determining the full symmetry conditions
  n1:=0;
  vl:=nil;
  for each eqn in eqlist do
  <<n1:=n1+1;
    if !*time then <<terpri()$
      terpri()$terpri()$
      write"=============== Full conditions for the ",n1,". equation" ;
    >>;
    n2:=cadr adddepli(eqn,revdylist);
    n3:=n2;  % n3 are the variables in the new condition
    symcon:=(reval algebraic num lisp prepsq addsq(
    <<h:=simp!* 0;
      for each e1 in xilist do
      h:=addsq(h,
               if car e1 = 0 then simp!* 0
                             else <<n3:=mergedepli(n3,cadddr e1);
                                    multsq(simp!* car e1,
                                           simpdf {eqn,cadr e1})
                                  >>
              );
      h
    >>,
    <<h:=simp!* 0;
      for each e1 in n2 do
      for each e2 in e1 do
      h:=addsq(h,
               multsq(<<n4:=prolong(e2,xilist,etalist,0,truesub,
                                    0,xlist );
                        n3:=mergedepli(n3,cdr n4);
                        car n4
                      >>,
                      simpdf {eqn,e2}
                     )
              );
      h
    >>                                       )) . symcon;

    n3:=joinsublists(xlist . n3)$
    for n2:=1:eqlen do
    <<n4:=nth(lhslist,n2);
      if not my_freeof(eqn,n4) then
      symcon:=subst(cadr nth(truesub,n2), n4, symcon);
      n3:=delete(n4,n3)
    >>;
    vl:=union(vl,n3);

    if individual_ or (n1=eqlen) then <<
      inequ:=callcrack(!*time,cpu,gc,lietrace_,symcon,
                       flist,vl,xilist,etalist,inequ);
      symcon :=car   inequ; xilist:=cadr   inequ;
      etalist:=caddr inequ; flist :=cadddr inequ;
      inequ  :=cadddr cdr inequ;
      cpu:=time()$ gc:=gctime()$
    >>
  >>;
  eqn:=sb:=e1:=e2:=n:=h:=dylist:=deplist:=symord:=nil;%occurlist:=nil;
  lisp(adjust_fnc:=oldadj);
  %------- Calculation finished, simplification of the result
  h:=append(for each el in  xilist collect car el,
            for each el in etalist collect car el );
  if symcon then for each el in symcon do h:=cons(el,h);
  h:=cons('LIST,h);

  %------- droping redundant constants or functions
  batch_mode_old:=!*batch_mode$
  !*batch_mode:=t$
  sb:=reval dropredundant(h,'LIST . flist,'LIST . vl,list('LIST));
  !*batch_mode:=batch_mode_old$
  if sb then <<
   flist:=cdr cadddr sb;
   h:=caddr sb;
   sb:=cadr sb;
   e1:=nil
  >>;

  %------- absorbing numerical constants into free constants
  h:=reval absorbconst(h,'LIST . flist);
  if h then if sb then sb:=append(sb,cdr h)
                  else sb:='LIST . cdr h;

  %------- doing the substitutions
  if sb then <<
    if print_ then <<terpri()$
      write"Free constants and/or functions have been rescaled. ">>$
    for each e1 in cdr sb do <<
      xilist :=subst(caddr e1, reval cadr e1,  xilist);
      etalist:=subst(caddr e1, reval cadr e1, etalist);
      symcon :=cdr reval cons('LIST,subst(caddr e1, reval cadr e1,  symcon));
    >>;
  >>;

  %------- Computing the prolongation of the symmetry vector
  if fixp(prolong_order) and (prolong_order>0) then <<
    for each e1 in ylist do depnd(e1,list(xlist));
    on evallhseqp;
    sb:=subdif1(cons('LIST,xlist),cons('LIST,ylist),prolong_order)$
    for each e1 in cdr sb do
    for each e2 in cdr e1 do <<
      h:=combidif(caddr e2);

      n1:=mkid(eta,car h);
      for each n2 in cdr h do
      n1:=mkid(n1,nth(xlist,n2));
      h:=car etamn(car h,cdr h,xilist,etalist,0,truesub,0,xlist)$
      n3:=(length cdr h) - 1;
      if n3>jetord then jetord:=n3$
      etapqlist:=cons(list('EQUAL,n1,car h),etapqlist);
    >>
  >>$
  revdylist:=nil;

  %------- output
  if length flist>1 then n:=t
                    else n:=nil;
  terpri()$terpri()$
  if null flist then write"No such symmetry does exist. "
                else write"The symmetr",
                          if n then "ies are:" else "y is:";
  terpri()$

  xilist:=for each el in  xilist collect
  <<e1:=mkid( xi,second el);
    if freeof(flist,e1) then
    for each e2 in fctargs(e1) do nodepend(e1,e2);
    e1:=list('EQUAL,e1,reval car el);
    e1>>;

  etalist:=for each el in etalist collect
  <<e1:=mkid(eta,second el);
    if freeof(flist,e1) then
    for each e2 in fctargs(e1) do nodepend(e1,e2);
    e1:=list('EQUAL,e1,reval car el);
    e1>>;
  %--- Backsubstitution of e.g. u`1`1 --> df(u,x,2)
  for each e1 in ylist do depnd(e1,list(xlist));
  on evallhseqp;
  sb:=subdif1(cons('LIST,xlist),cons('LIST,ylist),jetord)$
  algebraic(
  sb:=for each e1 in sb join
      for each e2 in e1 collect(rhs e2 = lhs e2));
  off evallhseqp;
  xilist   :=cdr algebraic(sub(sb,cons('LIST,   xilist)));
  etalist  :=cdr algebraic(sub(sb,cons('LIST,  etalist)));
  etapqlist:=cdr algebraic(sub(sb,cons('LIST,etapqlist)));
  xicop   :=   xilist$
  etacop  :=  etalist$
  etapqcop:=etapqlist$

  sb:=nil$
  flcop:=flist;
  n1:=0;
  for each e1 in flcop do
  <<% if null n2 then n2:=freeof(xicop,e1) and freeof(etacop,e1)$
    if freeof(symcon,e1) then <<
      n1:=n1+1;
      xi:=xicop;eta:=etacop;etapq:=etapqcop;
      for each e2 in flcop do
      if e2 neq e1 then
       <<xi:=subst(0,e2,xi);eta:=subst(0,e2,eta);etapq:=subst(0,e2,etapq)>>
                   else
      if null cdr fargs e1 then
       <<xi:=subst(1,e2,xi);eta:=subst(1,e2,eta);etapq:=subst(1,e2,etapq)>>;
      terpri()$write"-------- ",n1,". Symmetry:";terpri()$
      for each e2 in xi    do algebraic write reval e2;
      for each e2 in eta   do algebraic write reval e2;
      for each e2 in etapq do algebraic write reval e2;
      if cdr fargs e1 then <<terpri()$write"with ";fctprint list(e1);terpri()>>$
      xicop   :=subst(0,e1,xicop   );
      etacop  :=subst(0,e1,etacop  );
      etapqcop:=subst(0,e1,etapqcop);
      flcop:=delete(e1,flcop);
      %depl!*:=delete(assoc(e1,depl!*),depl!*)$
    >>;
  >>;
  if flcop neq flist then <<
    terpri()$write"-------- ";terpri()$
  >>;

  if flcop then <<
    if length flist>1 then n:=t
                      else n:=nil;
    terpri()$
    if flcop=flist then write"S"
                   else write"Further s";
    write"ymmetr",if n then "ies:" else "y:"$
    terpri()$
    for each e1 in xicop    do algebraic write reval e1;
    for each e1 in etacop   do algebraic write reval e1;
    for each e1 in etapqcop do algebraic write reval e1;
  >>;

  terpri()$
  if flcop then
  <<write"with ";fctprint cdr reval ('LIST . flcop)>>;

  if null symcon then if flcop then
  <<write" which ",if n then "are" else "is"," free. ";
    terpri()>>                 else
                 else
  <<h:=print_;print_:=50$
    if print_ then
    <<terpri()$
      write"which still ",if n then "have" else "has"," to satisfy: ";
      terpri()$
      deprint symcon;
    >>        else
    <<terpri()$
      write"which ",if n then "have" else "has",
           " to satisfy conditions. To see them set ";
      terpri()$
      write
      "lisp(print_= max. number of terms of an equation to print);";
      terpri()$
    >>;
    print_:=h
  >>;
  recover_reduce_flags()$

  return list('LIST,'LIST . symcon,'LIST . append(xilist,etalist),
              'LIST . flist);
end$ % of liepde

endmodule$

end$
