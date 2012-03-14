
%            CONLAW file with subroutines for CONLAW1/2/3/4

%                   by Thomas Wolf, September 1997

%----------------------------------------------------------------------


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

symbolic fluid '(reducefunctions_ print_)$

algebraic procedure print_claw(eqlist,qlist,plist,xlist)$
begin scalar n$
 n:=length eqlist$
 while qlist neq {} do <<
  if length qlist < n then write "+"$
  write"( ",first qlist," ) * ( ",first eqlist," )"$
  qlist:=rest qlist; eqlist:=rest eqlist
 >>$
 write" = "$
 n:=length xlist$
 while plist neq {} do <<
  if length plist < n then write "+"$
  write"df( ",first plist,", ",first xlist," )"$
  plist:=rest plist;
  xlist:=rest xlist
 >>
end$

symbolic operator lhsli$
symbolic procedure lhsli(eqlist)$
% lhslist1 will be a list of all those lhs's which are a derivative or
%          a power of a derivative which is used to fix dependencies
%          of q_i or p_j
% lhslist2 will be a list of all lhs's of all equations in their
%          order with those lhs's set to 0 which can not be used
%          for substitutions
begin scalar lhslist1,lhslist2,h1,flg1,flg2$
  for each h1 in cdr eqlist do <<
    flg1:=nil$    % no assignment to lhslist1 done yet
    if (pairp h1) and (car h1 = 'EQUAL) then <<
      h1:=reval cadr h1;
      if (pairp h1) and
         (car h1='EXPT) and
         (numberp caddr h1) then <<flg2:=nil;h1:=cadr h1>>
                            else   flg2:=t;
      if (not numberp h1) and
         ((atom h1) or ((car h1='DF) and (atom cadr h1) )) then
      <<lhslist1:=cons(h1,lhslist1)$
        if flg2 then <<lhslist2:=cons(h1,lhslist2)$
                       flg1:=t>>
      >>
    >>;
    if null flg1 then lhslist2:=cons(0,lhslist2);
  >>$
  return list('LIST,cons('LIST,lhslist1),cons('LIST,lhslist2))
end$

symbolic operator chksub$
symbolic procedure chksub(eqlist,ulist)$
% eqlist is a list of equations   df(f,x,2,y) = ...
% this procedure tests whether
% - for any equation a derivative on the rhs is equal or a derivative of
%   the lhs?
% - any lhs is equal or the derivative of any other lhs
begin scalar h1,h2,derili,complaint$
 derili:=for each e1 in cdr eqlist collect
 cons( all_deriv_search(cadr  e1,cdr ulist),      % lhs
       all_deriv_search(caddr e1,cdr ulist) );    % rhs
 %--- Is for any equation a derivative on the rhs equal or a derivative of
 %--- the lhs?
 for each e1 in derili do
 if car e1 then <<
  h1:=caaar e1;                  % e.g. h1 = (f x 2 y)
  for each h2 in cdr e1 do
  if (car h1 = caar h2) and null which_deriv(cdar h2,cdr h1) then <<
   complaint:=t;
   write "The left hand side ",
         if length h1 = 1 then car h1
                          else cons('DF,h1)$terpri()$
   write " is not a leading derivative in its equation!"$ terpri()
  >>
 >>$
 %--- Is any lhs equal or the derivative of any other lhs?
 if derili then
 while cdr derili do <<
  if caar derili then <<
   h1:=caaaar derili$
   for each h2 in cdr derili do
   if (car h2)           and
      (car h1=caaaar h2) and
      ((null which_deriv(cdr h1,cdaaar h2)) or
       (null which_deriv(cdaaar h2,cdr h1))    ) then <<
    complaint:=t;
    write"--> One left hand side (lhs) contains a derivative which"$
    terpri()$
    write"is equal or a derivative of a derivative on another lhs!"$
    terpri()$
   >>$
  >>$
  derili:=cdr derili
 >>;
 if complaint then terpri()$
end$

%==== Procedures as in LIEPDE:

symbolic procedure comparedif1(u1l,u2l)$
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

%-------------

symbolic procedure comparedif2(u1,u1list,du2)$
% checks whether du2 is a derivative of u1 differentiated
% wrt. u1list
begin
 scalar u2l;
 u2l:=combidif(du2)$ % u2l=(u2, 1, 1, ..)
 if car u2l neq u1 then return nil else
 return comparedif1(u1list, cdr u2l)
end$ % of comparedif2

%-------------

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

%-------------

symbolic operator listdifdif2$
symbolic procedure listdifdif2(lhslist,deplist)$
% lists all elements of deplist which are *not* derivatives
% of any element of lhslist
begin
  scalar h;
  deplist:=cdr reval deplist;
  lhslist:=cdr reval lhslist;
  for each h in lhslist do
  deplist:=listdifdif1(h,deplist);
  return cons('LIST,deplist)
end$ % of listdifdif2

%-------------

symbolic operator totdeg$
symbolic procedure totdeg(p,f)$
%   Ordnung (total) der hoechsten Ableitung von f im Ausdruck p
eval(cons('PLUS,ldiff1(car ldifftot(reval p,reval f),fctargs reval f)))$

%-------------

% symbolic operator totordpot$
% symbolic procedure totordpot(p,f)$
% %   Ordnung (total) der hoechsten Ableitung von f im Ausdruck p
% %   und hoechste Potenz der hoechsten Ableitung
% %   currently not used
% begin scalar a;
%  a:=ldifftot(reval p,reval f);
%  return
%  cons(eval(cons('PLUS,ldiff1(car a,fctargs reval f))), cdr a)
% end$

%-------------

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

%-------------

symbolic procedure ldiff1(l,v)$
%  liefert Liste der Ordnungen der Ableitungen nach den Variablen aus v
%  l Liste (Variable + Ordnung)$ v Liste der Variablen
if null v then nil                      %  alle Variable abgearbeitet ?
else cons(diffdeg(l,car v),ldiff1(l,cdr v))$
                                        %  Ordnung der Ableitung nach
                                        %  erster Variable anhaengen

%-------------

symbolic procedure ldifftot(p,f)$
%  leading derivative total degree ordering
%  liefert Liste der Variablen + Ordnungen mit Potenz
%  p Ausdruck in LISP - Notation, f Funktion
ldifftot1(p,f,fctargs f)$

%-------------

symbolic procedure ldifftot1(p,f,vl)$
%  liefert Liste der Variablen + Ordnungen mit Potenz
%  p Ausdruck in LISP - Notation, f Funktion, lv Variablenliste
begin scalar a$
  a:=cons(nil,0)$
  if not atom p then
%    if member(car p,list('EXPT,'PLUS,'MINUS,'TIMES,
%                        'QUOTIENT,'DF,'EQUAL)) then
    if member(car p,REDUCEFUNCTIONS_) then
                                        %  erlaubte Funktionen
    <<if (car p='PLUS) or (car p='TIMES) or
         (car p='QUOTIENT) or (car p='EQUAL) then
      <<p:=cdr p$
        while p do
        <<a:=diffreltot(ldifftot1(car p,f,vl),a,vl)$
          p:=cdr p
        >>
      >>                      else
      if car p='MINUS then a:=ldifftot1(cadr p,f,vl) else
      if car p='EXPT then               %  Exponent
%      if numberp caddr p then
%      <<a:=ldifftot1(cadr p,f,vl)$
%        a:=cons(car a,times(caddr p,cdr a))
%      >>                 else a:=cons(nil,0)
      <<a:=ldifftot1(cadr p,f,vl)$
        if (numberp caddr p) and
           (numberp cdr a) then a:=cons(car a,times(caddr p,cdr a))
                           else a:=cons(car a,10000)
      >>
                                        %  Potenz aus Basis wird mit
                                        %  Potenz multipliziert
                     else
      if car p='DF then                 %  Ableitung
      if cadr p=f then a:=cons(cddr p,1)
                                        %  f wird differenziert?
                  else a:=cons(nil,0)
                   else                 %  any other non-linear function
      <<p:=cdr p$
        while p do
        <<a:=diffreltot(ldifftot1(car p,f,vl),a,vl)$
          p:=cdr p
        >>;
        a:=cons(car a,10000)
      >>
    >> else                             %  sonst Konstante bzgl. f

    if p=f then a:=cons(nil,1)         %  Funktion selbst
           else a:=cons(nil,0)          %  alle uebrigen Fkt. werden
  else if p=f then a:=cons(nil,1)$        %  wie Konstante behandelt
  return a
end$

%-------------

symbolic procedure diffreltot(p,q,v)$
%   liefert komplizierteren Differentialausdruck$
if diffreltotp(p,q,v) then q
                   else p$

%-------------

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

%-------------

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

%-------------

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

%-------------

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

%-------------

symbolic operator dif$
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

%-------------

algebraic procedure depnd(y,xlist)$
for each xx in xlist do
for each x  in xx    do depend y,x$

%==== Other procedures:

symbolic operator totdif$
symbolic procedure totdif(s,x,n,dylist)$
% total derivative of s(x,dylist) w.r.t. x which is the n'th variable
begin
  scalar tdf,el1,el2;
  tdf:=simpdf {s,x};
  <<dylist:=cdr dylist;
    while dylist do
    <<el1:=cdar dylist;dylist:=cdr dylist;
      while el1 do
      <<el2:=car el1;el1:=cdr el1;
        tdf:=addsq(tdf ,multsq( simp!* dif(el2,n), simpdf {s,el2}))
      >>
    >>
  >>;
  return prepsq tdf
end$

%-------------

algebraic procedure simppl(pllist,ulist,tt,xx)$
begin
 scalar pl,hh,td,xd,lulist,ltt,lxx,ltd,dv,newtd,e1,deno,ok,
        newpllist,contrace;
% contrace:=t;
 lisp <<
  lulist:=cdr reval algebraic ulist;
  lxx:=reval algebraic xx;
  ltt:=reval algebraic tt;
 >>;
 newpllist:={};
 for each pl in pllist do <<
  td:=first  pl;
  xd:=second pl;
  repeat <<
   lisp <<
    ltd:=reval algebraic td;
    if contrace then <<write"ltd1=",ltd;terpri()>>$
    dv:=nil;
    newtd:=nil;
    deno:=nil;
    if (pairp ltd) and (car ltd='QUOTIENT)   and
      my_freeof(caddr ltd,ltt) and
      my_freeof(caddr ltd,lxx)
    then <<deno:=caddr ltd;ltd:=cadr ltd>>;
    ok:=t;

    if (pairp ltd) and (car ltd = 'PLUS) then ltd:= cdr ltd else
    if (pairp ltd) and (car ltd neq 'TIMES) then ok:=nil
                                            else ltd:=list ltd;
    if contrace then <<write"ltd2=",ltd;terpri()>>$
    if ok then <<
     for each e1 in ltd do <<
      hh:=intpde(e1, lulist, list(lxx,ltt), lxx, t);
      if null hh then hh:=list(nil,e1);
      dv   :=cons(car hh,dv);
      newtd:=cons(cadr hh,newtd);
     >>;
     dv   :=reval cons('PLUS,dv);
     newtd:=reval cons('PLUS,newtd);
     if deno then <<newtd:=list('QUOTIENT,newtd,deno);
                    dv   :=list('QUOTIENT,dv   ,deno) >>;
     if contrace then <<write"newtd=",newtd;terpri();
                        write"dv=",dv      ;terpri() >>$

     td:=newtd;
     if contrace then <<write"td=",td;terpri()>>$
     if (dv neq 0) and (dv neq nil) then <<
      xd:=reval(list('PLUS,xd,list('DF,dv,tt)));
      if contrace then <<write"xd=",xd;terpri()>>$
      %algebraic mode:
      %hh:=lisp gensym()$
      %sbb:=absorbconst({td*hh,xd*hh},{hh})$
      %if (sbb neq nil) and (sbb neq 0) then
      %<<td:=sub(sbb,td*hh)/hh;  xd:=sub(sbb,xd*hh)/hh>>;
      % cllist would have to be scaled as well
     >>
    >>
   >>
  >>
  until lisp(dv)=0;
  newpllist:=cons({td,xd}, newpllist);
 >>;
 return reverse newpllist
end$ % simppl

%-------------

symbolic operator fdepterms$
symbolic procedure fdepterms(td,f)$
% fdepterms regards td as a fraction where f occurs only in the
% numerator. It determines all terms of the numerator in
% which f occurs divided through the denominator.
begin
  scalar nu,de,e1,sm;
  td:=reval td;
  if pairp td then
  if car td='QUOTIENT then <<nu:=cadr td;de:=caddr td>>;
  if null nu then nu:=td;
  if not pairp nu then if freeof(nu,f) then sm:=0
                                       else sm:=nu
                  else <<
    if car nu = 'PLUS then nu:=cdr nu
                      else nu:=list nu;
    for each e1 in nu do
    if not freeof(e1,f) then sm:=cons(e1,sm);
    if null sm then sm:=0 else
    if length sm = 1 then sm:=car sm
                     else sm:=cons('PLUS,sm)
  >>;
  if de then sm:=list('QUOTIENT,sm,de);
  return sm
end$ % of fdepterms

%-------------

symbolic procedure subtract_diff(d1,d2)$
% assumes d1,d2 to be equally long lists of numbers (at least one)
% that are orders of derivatives (which may be 0),
% These lists ca be produced using the procedure maxderivs(),
% returns nil if any number in d2 is bigger than the corresponding
% number in d1, returns list of differences otherwise
begin scalar d;
 return
 if car d2 > car d1 then nil else
 if null cdr d1 then {car d1 - car d2} else
 if d:=subtract_diff(cdr d1,cdr d2) then cons(car d1 - car d2,d)
                                    else nil
end$

%-------------

symbolic procedure transfer_fctrs(h,flist)$
begin scalar fctrs;
%algebraic write"begin: caar h=",lisp caar h," cdar h =",lisp cdar h;
 if (pairp cdar h) and (cadar h='MINUS) then
 rplaca(h,cons(reval {'MINUS,caar h},cadr cdar h));

 if (pairp cdar h) and (cadar h='TIMES) then
 for each fc in cddar h do
 if freeoflist(fc,flist) then fctrs:=cons(fc,fctrs);
 if fctrs then <<
  if cdr fctrs then fctrs:=cons('TIMES,fctrs)
               else fctrs:=car fctrs;
  rplaca(h,cons(reval {'TIMES   ,caar h,fctrs},
                reval {'QUOTIENT,cdar h,fctrs} ))
 >>
%;algebraic write"end:   caar h=",lisp caar h," cdar h =",lisp cdar h;
end$

%-------------

symbolic operator partintdf$
symbolic procedure partintdf(eqlist,qlist,plist,xlist,flist,jlist,sb)$
% eqlist ... list of equations
% qlist  ... list of characteristic functions
% plist  ... list of components of conserved current
% xlist  ... list of independent variables
% flist  ... list of the arbitrary function occuring in this conservation law
% jlist  ... list of all jet-variables
% eqlist and qlist are in order.
% plist and xlist are in order.
% The aim is to remove all derivatives of f in the conservation law
% At first terms with derivatives of f in qlist are partially integrated.
% Then terms with derivatives of f in plist are partially integrated.
begin scalar f,n,d,deltali,subli,lhs,rhs,cof,x,y,cpy,newpl,lowd,su,vle,
             idty,idtysep,sbrev,dno,lsb,h0,h1,h2,h3,h4,h5,h6,h7,ldh1,ldh2,
             reductions_to_do,ld1,ld2,h0_changed;

 % 0. check that plist is homogeneous in flist
 algebraic <<
  cpy:=plist$
  for each f in flist do cpy:=sub(f=0,cpy)$
  while (cpy neq {}) and (first cpy = 0) do cpy:=rest cpy$
 >>$
 if cpy neq {'LIST} then return nil$

 eqlist:=cdr eqlist$
 qlist :=cdr  qlist$
 plist :=cdr  plist$
 xlist :=cdr  xlist$
 flist :=cdr  flist$
 jlist :=cdr  jlist$

 % 0. check that flist functions do only depend on xlist variables
 d:=t;
 for each f in flist do
 if not_included(fctargs f,xlist) then d:=nil$
 if null d then return nil$

 terpri()$
 write"An attempt to factor out linear differential operators:"$terpri()$
 n:=0;
 while eqlist do <<
  n:=add1 n;
  su:=print_;print_:=nil;
  d:=newfct('eq_,xlist,n);
  print_:=su;
  deltali:=cons(d,deltali);
  algebraic write d,":=",lisp car eqlist$
  subli:=cons({'EQUAL,d,car eqlist},subli)$
  lhs:=cons({'TIMES,car qlist,d % car eqlist
     },lhs);
  eqlist:=cdr eqlist;
  qlist:=cdr qlist
 >>;
 lhs:=reval cons('PLUS,lhs)$
 subli:=cons('LIST,subli)$
 for each f in flist do <<
  f:=reval f$

  % removing f-derivatives from the lhs
  repeat <<
   d:=car ldiffp(lhs,f)$ %  liefert Liste der Variablen + Ordnungen mit Potenz
   if d then <<
    % correcting plist
    cpy:=d;
    while cpy and ((numberp car cpy) or freeof(xlist,car cpy)) do cpy:=cdr cpy;
    if null cpy then d:=nil
                else <<
     cof:=coeffn(lhs,cons('DF,cons(f,d)),1);
     lhs:=reval {'DIFFERENCE,lhs,cons('DF,cons({'TIMES,cof,f},d))}$
     x:=car cpy;
     lowd:=lower_deg(d,x)$ % the derivative d reduced by one
     su:=if lowd then cons('DF,cons({'TIMES,cof,f},lowd))
                 else               {'TIMES,cof,f}$

     cpy:=xlist;
     newpl:=nil;
     while cpy and (x neq car cpy) do <<
      newpl:=cons(car plist,newpl);
      plist:=cdr plist;
      cpy:=cdr cpy
     >>;
     plist:=cons({'DIFFERENCE,car plist,su},cdr plist);
     while newpl do <<
      plist:=cons(car newpl,plist)$
      newpl:=cdr newpl
     >>
    >>
   >>
  >> until null d;     % until no derivative of f occurs
  plist:=cdr algebraic(sub(subli,lisp cons('LIST,plist)))$

  % Now we add trivial conservation laws in order to get rid of
  % derivatives of f in the conserved current
  repeat <<
   newpl:=nil;
   cpy:=xlist;
   while plist and null(d:=car ldiffp(car plist,f)) do <<
    newpl:=cons(car plist,newpl);
    plist:=cdr plist;
    cpy:=cdr cpy
   >>;
   if d and (car d neq car cpy) then <<   % otherwise infinte loop
    cof:=coeffn(car plist,cons('DF,cons(f,d)),1);
    x:=car d;
    lowd:=lower_deg(d,x)$ % the derivative d reduced by one
    su:=if lowd then {'TIMES,cof,cons('DF,cons(f,lowd))}
                else {'TIMES,cof,              f       }$

    plist:=cons(reval reval {'DIFFERENCE,car plist,{'DF,su,x}},cdr plist);
    while newpl do <<
     plist:=cons(car newpl,plist)$
     newpl:=cdr newpl
    >>$

    % adding the correction to the other component of plist
    y:=car cpy;
    cpy:=xlist;
    while x neq car cpy do <<
     newpl:=cons(car plist,newpl);
     plist:=cdr plist;
     cpy:=cdr cpy
    >>$
    plist:=cons(reval reval {'PLUS,car plist,{'DF,su,y}},cdr plist);
    while newpl do <<
     plist:=cons(car newpl,plist)$
     newpl:=cdr newpl
    >>
   >> else <<d:=nil;plist:=append(reverse newpl,plist)>>
  >> until null d;
 >>;

 vle:=length xlist;

 newpl:=algebraic absorbconst(lisp cons('LIST,append(qlist,plist)),
                                   cons('LIST,flist))$
 if newpl then newpl:=cdadr newpl;

 % Now factorizing out a linear differential operator
 % 2. extend dependencies of functions from flist and add extra conditions
 for each f in flist do <<
  depl!*:=delete(assoc(f,depl!*),depl!*);
  depl!*:=cons(cons(f,xlist),depl!*);
 >>$
 % 3. compute coefficients of the conditions in the identity
 idty:=algebraic(sub(subli,lhs))$
 for n:=1:vle do
 if not zerop nth(plist,n) then
 idty:={'DIFFERENCE,idty,{'DF,nth(plist,n),nth(xlist,n)}}$
 % 4. separate idty into conditions with multiplicities
 sbrev:=cons('LIST,for each d in cdr sb collect {'EQUAL,caddr d,cadr d})$
 idty:=reval reval idty$
 dno:=algebraic den idty;
 if dno neq 1 then idty:=algebraic num idty$

 idty:=algebraic(sub(sbrev,idty))$
 su:=print_;print_:=nil;
 idtysep:=separ(reval idty,flist,jlist,nil)$
 print_:=su;
 idtysep:=for each d in idtysep collect
 cons(algebraic(sub(sb,lisp car d)),cdr d);

 % 5. integrations of cdr of the elements of idty have to be done:
 %    - sufficiently often so that there are not more conditions
 %      than functions in flist
 %    - as few as possible to have factored out afterall an as
 %      high as possible operator

 reductions_to_do:=length idtysep - length flist;
 if reductions_to_do>0 then <<
  h0:=idtysep;
  while h0 do <<
   rplaca(h0,cons(reval caar h0, reval cdar h0));
   transfer_fctrs(h0,flist); h0:=cdr h0
  >>$

%write"Separation gives:"$terpri()$
%for each d in idtysep do
%algebraic write "0 = (",lisp car d,") * (",lisp cdr d,")"$

  h0:=idtysep;
  repeat <<  % check whether cdar h0 is a derivative of another condition
   h0_changed:=nil;
   h1:=cdar h0;
%algebraic write"caar h0=",lisp caar h0," cdar h0 =",lisp cdar h0;
   % find a function appearing in h1 and its leading derivative
   cpy:=flist;
   while cpy and freeof(h1,car cpy) do cpy:=cdr cpy;
   % if null cpy then error!

   ld1:=car ldiffp(h1,car cpy)$
   ldh1:=maxderivs(nil,ld1,xlist)$
   ld1:=if null ld1 then car cpy
                    else cons('DF,cons(car cpy,ld1))$

   h2:=idtysep;
   while h2 do
   % is h1 a derivative of car h2 or car h2 a derivative of h1?
   if (h2 eq h0) or freeof(cdar h2,car cpy) then h2:=cdr h2
                                            else <<

%algebraic write"caar h2=",lisp caar h2," cdar h2 =",lisp cdar h2;
    ld2:=car ldiffp(cdar h2,car cpy)$
    ldh2:=maxderivs(nil,ld2,xlist)$
    ld2:=if null ld2 then car cpy
                     else cons('DF,cons(car cpy,ld2))$

    % is h1 a derivative of car h2?
    h3:=subtract_diff(ldh1,ldh2);
    if null h3 then h2:=cdr h2
               else <<
     % the leading derivative in h1 is a derivative of
     % the leading derivative in cdar h2
     h4:=cdar h2;
%write"h4=",h4;terpri()$
     if pairp h4 and (car h4 = 'PLUS) then <<
      for n:=1:vle do if not zerop nth(h3,n) then
      h4:={'DF,h4,nth(xlist,n),nth(h3,n)};
      if null freeoflist(h5:=algebraic(h1/h4),flist) then h2:=cdr h2
                                                     else <<
       % h1 = h5 * derivative of (cdar h2)
       h6:={'TIMES,caar h0,reval h5};
       for n:=1:vle do <<
        h7:=nth(h3,n);
        if not zerop h7 then
        h6:={'TIMES,{'EXPT,-1,h7},{'DF,h6,nth(xlist,n),h7}};
       >>;
       rplaca(h2,cons(reval {'PLUS,caar h2,h6},cdar h2));
       rplaca(h0,cons(0,0));
%algebraic write"Change(1):"$
%algebraic write"caar h2=",lisp caar h2," cdar h2 =",lisp cdar h2;
%algebraic write"caar h0=",lisp caar h0," cdar h0 =",lisp cdar h0;
       reductions_to_do:=sub1 reductions_to_do;
       h2:=nil
      >>
     >>                               else <<
      % Update of car h2
      h6:=algebraic(lisp(caar h0)*coeffn(h1,ld1,1));
      for n:=1:vle do <<
       h7:=nth(h3,n);
       if not zerop h7 then
       h6:={'TIMES,{'EXPT,-1,h7},{'DF,h6,nth(xlist,n),h7}};
      >>;
      rplaca(h2,cons(reval {'PLUS,caar h2,h6},cdar h2));
%;algebraic write"Change(2):"$
%algebraic write"caar h2=",lisp caar h2," cdar h2 =",lisp cdar h2;
      % Update of car h0
      h1:=reval {'DIFFERENCE,h1,{'TIMES,coeffn(h1,ld1,1),ld1}}$

      if zerop h1 then <<rplaca(h0,cons(0,0));h2:=nil;
                         reductions_to_do:=sub1 reductions_to_do;>>
                  else <<rplaca(h0,cons(caar h0,h1));
                         transfer_fctrs(h0,flist);
                         h1:=cdar h0;
                         cpy:=flist;
                         while cpy and freeof(h1,car cpy) do cpy:=cdr cpy;
                         ld1:=car ldiffp(h1,car cpy)$
                         ldh1:=maxderivs(nil,ld1,xlist)$
                         ld1:=if null ld1 then car cpy
                                          else cons('DF,cons(car cpy,ld1))$
                         h2:=cdr h2;h0_changed:=t>>
%;algebraic write"caar h0=",lisp caar h0," cdar h0 =",lisp cdar h0;
     >>

    >>
   >>;
   if (null h0_changed) or (zerop caar h0) then h0:=cdr h0
  >> until (reductions_to_do=0) or (null h0);

%write"After correction the separation gives:"$terpri()$
%for each d in idtysep do
%if not zerop car d then
%algebraic write "0 = (",lisp car d,") * (",lisp cdr d,")"$

 >>$

 % Now the number of f in flist should be equal the number of conditions
 % or as low as possible
 n:=0;
 rhs:=nil;
 for each d in idtysep do
 if not zerop car d then << % for each condition
  n:=add1 n;
  su:=print_;print_:=nil;
  x:=newfct('l_,xlist,n);
  print_:=su;
  su:=if dno=1 then car d
               else reval {'QUOTIENT,car d,dno}$
  algebraic write x,":=",su$
  lsb:=cons({'EQUAL,x,su},lsb);
  % 5. for each condition integrate all terms
  y:=cdr d;
  cpy:=flist;
  while y and not zerop y do <<
   repeat <<
    d:=ldiffp(y,car cpy)$
    if zerop cdr d then
    if null cpy then <<write"The backintegration is faulty."$terpri()>>
                else cpy:=cdr cpy
   >> until not zerop cdr d;
   if car d = nil then <<
    cof:=coeffn(y,car cpy,1);
    rhs:={'PLUS,{'TIMES,x,cof,car cpy},rhs};
    y:=reval reval {'DIFFERENCE,y,{'TIMES,cof,car cpy}}
   >>             else <<
    cof:=coeffn(y,cons('DF,cons(car cpy,car d)),1);
    rhs:=reval {'PLUS,rhs,{'TIMES,cons('DF,cons({'TIMES,x,cof},car d)),
                                  car cpy,{'EXPT,{'MINUS,1},absdeg(car d)}}};
    y:=reval reval {'DIFFERENCE,y,{'TIMES,cof,cons('DF,cons(car cpy,car d))}}
   >>
  >>
 >>$
 lsb:=cons('LIST,lsb)$
 flist:=cons('LIST,flist)$
 algebraic <<
  d:=gcd(den lhs,den rhs);
  lhs:=lhs*d; rhs:=rhs*d;

  %--- Correctness test
  d:=sub(subli,lhs)-sub(lsb,rhs);
  if d neq 0 then write "Not identically zero : ",d$

  for each f in flist do algebraic <<
   x:=coeffn(num lhs,f,1);  y:=coeffn(num rhs,f,1);
   d:=gcd(x,y);
   algebraic write x/d/den lhs," = ",y/d/den rhs$
  >>
 >>$

end$

%-------------

end$

